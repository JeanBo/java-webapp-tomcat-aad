package za.co.liberty.web.pages.core;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormSubmitBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.util.lang.Bytes;

import za.co.liberty.business.guicontrollers.core.ICoreTransferGuiController;
import za.co.liberty.dto.agreement.core.CoreTransferDto;
import za.co.liberty.exceptions.data.ValidationException;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.helpers.lookup.ServiceLocator;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.data.pages.IModalMaintenancePageModel;
import za.co.liberty.web.pages.core.model.CoreTransferPageModel;
import za.co.liberty.web.pages.core.model.CoreUploadModel;
import za.co.liberty.web.pages.panels.BasePanel;
import za.co.liberty.web.wicket.modal.SRSModalWindow;

public class SegmentUploadPanel extends BasePanel {

	private static final Logger logger = Logger.getLogger(SegmentUploadPanel.class);
	
	private FileUploadField fileUpload;
	private Button uploadButton;
	//private Button finishButton;
	private Form searchForm;

	private boolean initialised = false;

	CoreTransferPageModel pageModel2;

	private transient ICoreTransferGuiController guiController;
	private IModalMaintenancePageModel<CoreUploadModel> wizardModel;

	SRSModalWindow window;

	SegmentUploadPage page;
	
	public SegmentUploadPanel(String id, EditStateType editState,
			CoreTransferPageModel pageModel,
			SRSModalWindow window, 
			IModalMaintenancePageModel<CoreUploadModel> wizardModel) {
		super(id, editState);
		initialised = false;
		this.pageModel2 = pageModel;
		this.wizardModel = wizardModel;
		this.window = window;
		System.out.println("Inside SegmentUploadPanel constructor method()");
	}

	@Override
	protected void onBeforeRender() {
		System.out.println("Inside onBeforeRender menthod ");
		super.onBeforeRender();
		if (!initialised) {
			add(searchForm = createUploadForm("uploadForm"));
			initialised = true;
		}
	}

	@SuppressWarnings("unchecked")
	public Form createUploadForm(String id) {
		Form form = new Form(id) {
			private static final long serialVersionUID = -6308633210871154462L;

			@Override
			protected void onSubmit() {
				logger.info("Start lines upload");
				final FileUpload uploadedFile = fileUpload.getFileUpload();
				
				if (uploadedFile != null) {
					//Validation for file extension
					if (!uploadedFile.getClientFileName().toLowerCase().contains(".csv")) {
						error("File has an incorrect extension");
						return;
					}	
				
					try {
						BufferedReader reader = new BufferedReader(
								new InputStreamReader(uploadedFile.getInputStream()));
						List<String> lines = new ArrayList<String>(100);
						String line = null;
						while ((line=reader.readLine())!=null) {
							lines.add(line);
						}

						try {
							getGuiController().validateFileContents(lines);
						} catch (ValidationException e) {
							for (String s : e.getErrorMessages()) {
								error(s);
							}
							return;
						}
						if (pageModel2.getSegTransferDto()==null) {
							pageModel2.setSegTransferDto(new ArrayList<CoreTransferDto>());
						}
						
						try {
							getGuiController().convertFileLinesToDTOList(lines, pageModel2.getSegTransferDto(), pageModel2.getConsultantMap());
						} catch (ValidationException e) {
							for (String s : e.getErrorMessages()) {
								error(s);
							}
							return;
						}
						CoreUploadModel coreupload = new CoreUploadModel();
						coreupload.setCoreTransferDto(pageModel2.getSegTransferDto());
						coreupload.setConsultantMap(pageModel2.getConsultantMap());
						
						info("Succesfully processed " + lines.size() + " from input file, please close window to continue.");
						
						logger.info("Processed " + lines.size() + " from input file");
						wizardModel.setModalWizardSuccess(true);
						wizardModel.setSelectedItem(coreupload);
						logger.info("Processed " + wizardModel.getSelectedItem());
						window.setSessionModelForPage(wizardModel);
						
						fileUpload.setEnabled(false);
						uploadButton.setEnabled(false);
						
						//finishButton.setVisible(true);

					} catch (Exception e) {
						throw new IllegalStateException("Error");
					}
				}else{
					error("Please add record/records to transfer");
					return;
				}
			}
		};
		form.setMultiPart(true);
		form.setMaxSize(Bytes.megabytes(5));
		form.add(fileUpload = new FileUploadField("fileUpload"));

//		Button button = new AjaxButton("uploadButton") {
//			@Override
//			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
//				window.close(target);
//			}
//
//			@Override
//			protected void onError(AjaxRequestTarget target, Form<?> arg1) {
//				super.onError(target, arg1);
//				target.add(getFeedBackPanel());
//			}
//		};
		
		uploadButton = new Button("uploadButton");
		uploadButton.add(new AjaxFormSubmitBehavior("click") {
			private static final long serialVersionUID = 1L;
			@Override
			protected void onSubmit(AjaxRequestTarget target) {
				logger.info("button.onsubmit");
//				pageModel.setModalWizardSuccess(true);
				window.setSessionModelForPage(wizardModel);
				target.add(getFeedBackPanel());
			}

			@Override
			protected void onError(AjaxRequestTarget target) {
				super.onError(target);
				logger.info("button.onError");
				wizardModel.setModalWizardSuccess(false);
				window.setSessionModelForPage(wizardModel);
				target.add(getFeedBackPanel());
			}
		});
		form.add(uploadButton);
		
		
		//finishButton = new Button("finishButton");
		/*finishButton.add(new AjaxFormSubmitBehavior("click") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onError(AjaxRequestTarget target) {
//				pageModel.setModalWizardSuccess(false);
//				window.setSessionModelForPage(pageModel);
//				// error occured, add to feedback and display
//				target.add(getFeedBackPanel());

			}

			@Override
			protected void onSubmit(AjaxRequestTarget target) {
				wizardModel.setModalWizardSuccess(true);
				window.setSessionModelForPage(wizardModel);
				window.close(target);
			}
		});
		finishButton.setVisible(false);
		form.add(finishButton);*/
		
		return form;
	}

	/**
	 * Retrieve the relevant GUI Controller
	 * 
	 * @return
	 */
	public ICoreTransferGuiController getGuiController() {
		if (guiController==null) {
			try {
				guiController = ServiceLocator.lookupService(ICoreTransferGuiController.class);
			} catch (NamingException e) {
				throw new CommunicationException(e);
			}
		}
		return guiController;
	}
	
}
