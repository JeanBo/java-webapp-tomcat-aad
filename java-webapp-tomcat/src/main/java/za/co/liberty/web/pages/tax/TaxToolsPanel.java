package za.co.liberty.web.pages.tax;

import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.validation.IFormValidator;
import org.apache.wicket.model.PropertyModel;

import za.co.liberty.business.guicontrollers.taxgui.ITaxGuiController;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.helpers.lookup.ServiceLocator;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.pages.BasePage;
import za.co.liberty.web.pages.dialog.DialogWindowPopUp;
import za.co.liberty.web.pages.panels.BasePanel;
import za.co.liberty.web.pages.tax.model.TaxToolsModel;

public class TaxToolsPanel extends BasePanel {
	
	private static final long serialVersionUID = 5184011117772142255L;
	

	private Button submit; 
	private Form form;
	transient protected ITaxGuiController guiController;
		
	private Label queueManager;
	private Label queueName;
	private Label queueCount;

	private TaxToolsModel pageModel;
	private BasePage parentPage;
	private ModalWindow modalBackupWindow;
	private ModalWindow modalClearWindow;
		
	private static final Logger logger = Logger.getLogger(TaxToolsPanel.class);
	
	public TaxToolsPanel(String id, EditStateType editState, BasePage parentPage, TaxToolsModel pageModel){
		super(id, editState);
		this.parentPage = parentPage;
		this.pageModel = pageModel;
//		pageModel.setQueueCount(3200);
//		pageModel.setQueueManager("LMD1USRS.tst");
//		pageModel.setQueueName("LQ.PENDING.test");
		init();
	}
	
//	private boolean createDirectory(){
//		boolean success = (new File(masterFullDirectoryLogPath)).mkdir();
//		logger.info("Create directory path:" + masterFullDirectoryLogPath + " success:" + success);
//		
//		if (success) {
//			logger.info("Current log:" + mapXMLLookup("LogPath").getValue() + ": change to:" + masterFullDirectoryLogPath);
//			mapXMLLookup("LogPath").setValue(masterFullDirectoryLogPath);
//		}	
//
//		return success;
//	}
	
	
	
	private void init(){
		
		add(form = new TaxForm("xmlForm"));		
	}
		
	
	public class TaxForm extends Form {

		private static final long serialVersionUID = 1L;				
		
		public TaxForm(String id){
			super(id);
			
			add(queueManager = createQueueManager("queueManager"));
			add(queueName = createQueueName("queueName"));
			add(queueCount = createQueueCount("queueCount"));

			add(modalBackupWindow=createBackupModalWindow("backupModalWindow"));
			add(modalClearWindow=createClearWindow("clearModalWindow"));
			
			add(createBackupQueueButton("backupButton"));
			add(createClearQueueButton("clearButton"));
			
			
			
//			add(logPath = createLogPath("logpath"));
//			add(templatePath = createTemplatePath("templatePath"));
//			add(pdb = createPdb("pdb"));
//			add(agmnumbers = createAgmNumbers("agmnumbers"));
				
			
		}
		

		@Override
		public void add(IFormValidator validator) {
			// TODO Auto-generated method stub
			super.add(validator);
		}

		@Override
		protected void onSubmit() {
			
		}
	}		
	
	private ModalWindow createClearWindow(String id) {
		DialogWindowPopUp popUp = new DialogWindowPopUp() {
			
			@Override
			public void processAnswer(AjaxRequestTarget target, Boolean answer) {
				// TODO Auto-generated method stub
				System.out.println("Processed the answer " + answer);
				if (answer == Boolean.TRUE) {
					// Clear
					getTaxGuiController().clearCatchQueue();
					setResponsePage(parentPage.getClass());
				}
			}

			@Override
			public String getDialogMessage() {
				return "Are you sure you want to clear the queue (" + pageModel.getQueueCount() 
						+ " messages)?";
			}
			
		};
		ModalWindow w = popUp.createModalWindow(id);
//		w.setPageMapName("clearMQPageMap");
		return w;
		
	}

	
	private ModalWindow createBackupModalWindow(String id) {
		DialogWindowPopUp popUp = new DialogWindowPopUp() {
			
			@Override
			public void processAnswer(AjaxRequestTarget target, Boolean answer) {
				// TODO Auto-generated method stub
				System.out.println("Processed the answer on clear " + answer);
				
			}
			
			@Override
			public String getDialogMessage() {
				return "Do you want to backup the messages to the file \"" + "\"?";
			}
		};
		ModalWindow w = popUp.createModalWindow(id);
//		w.setPageMapName("backupMQPageMap");
		return w;
		
	}
	
	private Label createQueueManager(String id){
		Label temp = new Label(id, new PropertyModel(pageModel,"queueManager"));
		temp.setOutputMarkupId(true);
		return temp;
	}
	
	private Label createQueueName(String id){
		Label temp = new Label(id, new PropertyModel(pageModel,"queueName"));
		temp.setOutputMarkupId(true);
		return temp;
	}	

	private Label createQueueCount(String id){
		Label temp = new Label(id, new PropertyModel(pageModel,"queueCount"));
		temp.setOutputMarkupId(true);
		return temp;
	}
	
	private Button createBackupQueueButton(String id) {
		Button but = new AjaxButton(id, form) {
			private static final long serialVersionUID = 1L;
			@Override
			protected void onSubmit(AjaxRequestTarget target, Form form) {
				System.out.println("AjaxButton.onsubmit - Start");
				
				modalBackupWindow.show(target);
				
				
				System.out.println("AjaxButton.onsubmit - End");
			}
		};;
		

		but.setOutputMarkupId(true);
//		but.add(new AjaxEventBehavior("onsubmit") {
//			
//			@Override
//			protected void onEvent(AjaxRequestTarget arg0) {
//				System.out.println("button.onclick - Start");
//				System.out.println("Queue depth:=" + getTaxGuiController().getCatchQueueCount());
//				System.out.println("button.onclick - End");				
//			}
//		});
		
		return but;
		
	}
	
	private Button createClearQueueButton(String id) {
		Button but = new AjaxButton(id, form) {
			private static final long serialVersionUID = 1L;
			@Override
			protected void onSubmit(AjaxRequestTarget target, Form form) {
				System.out.println("AjaxButton.onsubmit - Start");
				
				modalClearWindow.show(target);
			
				System.out.println("AjaxButton.onsubmit - End");
			}
		};;
		

		but.setOutputMarkupId(true);
//		but.add(new AjaxEventBehavior("onsubmit") {
//			
//			@Override
//			protected void onEvent(AjaxRequestTarget arg0) {
//				System.out.println("button.onclick - Start");
//				System.out.println("Queue depth:=" + getTaxGuiController().getCatchQueueCount());
//				System.out.println("button.onclick - End");				
//			}
//		});
		
		return but;
		
	}
	
//	private Button createSubmitButton(String id){
//		Button temp = new Button(id){
//		private static final long serialVersionUID = 1L;
//
//		@Override
//		public void onSubmit() {
//			super.onSubmit();
//			
//
//			createDirectory();
//			logger.info("Inter Tax Value:" + pageModel2.getSelectedActionIndicator().getCode());
//			logger.info("Get Agm Kind:" + pageModel2.getSelectedAgreementKind().getKind());
//			
//			mapXMLLookup("ActionIndicator").setValue(pageModel2.getSelectedActionIndicator().getCode());
//			mapXMLLookup("AgreementKinds").setValue(pageModel2.getSelectedAgreementKind().getKind()+"");
//			
//			// Always gets updated, regardless of setting
//			Application modifiedApplication = getTaxGuiController().saveXML(application);
//			TaxLogDTO taxSessionLogDTO = new TaxLogDTO();
//			taxSessionLogDTO.setScheduled(pageModel2.isScheduleJob());
//			taxSessionLogDTO.setProcess(application.getName());
//			taxSessionLogDTO.setUacfid(SRSAuthWebSession.get().getSessionUser().getPartyOid()+"");
//			taxSessionLogDTO.setFileName(pageModel2.getXmlFileSelectedLocal());
//			
//			getTaxGuiController().execute(modifiedApplication, taxSessionLogDTO);
//			setResponsePage(new TaxGuiPage(modifiedApplication.getName()
//				+ " has been " + (taxSessionLogDTO.isScheduled() ? "scheduled" : "kicked off")
//				+ ". For more info please check Public Folder. "
//				+ " Emails to be sent to "	+ getTaxGuiController().getTcsMailToAddress()));
//			setRedirect(true);
//			
//		}};
//
//		return temp;
//	}
		
	protected ITaxGuiController getTaxGuiController() {
		if (guiController == null) {
			try {
				guiController = ServiceLocator.lookupService(ITaxGuiController.class);
			} catch (NamingException namingErr) {
				logger.error(""
						+ " Lookup of ITaxGuiController failed: "
						+ namingErr.getMessage());
				CommunicationException comm = new CommunicationException("Lookup of ITaxGuiController failed!");
				throw new CommunicationException(comm);
			} 
		}
		return guiController;
	}
	

	
}
