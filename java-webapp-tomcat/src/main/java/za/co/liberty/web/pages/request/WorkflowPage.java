package za.co.liberty.web.pages.request;

import org.apache.log4j.Logger;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxCallListener;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.Model;

import za.co.liberty.dto.agreement.maintainagreement.WorkflowDTO;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.pages.BaseWindowPage;
import za.co.liberty.web.pages.maintainagreement.WorkflowPanel;
import za.co.liberty.web.pages.maintainagreement.model.WorkflowDialogPopupModel;
import za.co.liberty.web.wicket.modal.SRSModalWindow;

/**
 * Allow for the entering of workflow code and comment.  To retrieve the answer you must call the
 * getModel method and deal with pageReferences etc.  Only retrieve the model if isSuccess = true
 * 
 * @author JZB0608
 *
 */
public class WorkflowPage extends BaseWindowPage {

	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(WorkflowPage.class);
	
	private AjaxButton cancelButton;
	private SRSModalWindow window;
	private WorkflowDTO workflowDTO;
	private WorkflowPanel workflowPanel;
	private Button saveButton;
	private WorkflowDialogPopupModel pageModel;
	
	private boolean success = false;
	
	/**
	 * The passed object can only initialise the object, a new one is returned
	 * @param window
	 * @param model
	 */
	public WorkflowPage(SRSModalWindow window, WorkflowDTO dto) {
		super();
		
		this.window = window;
		this.workflowDTO = dto;
		pageModel = new WorkflowDialogPopupModel();
		pageModel.setSelectedItem(dto);
		window.setSessionModelForPage(pageModel);
		add(new WorkflowForm("workflowForm", dto));
	}
	


	/**
	 * The wrapping form
	 * 
	 * @author JZB0608
	 *
	 */
	private class WorkflowForm extends Form {

		WorkflowDTO model;
		
		public WorkflowForm(String id, WorkflowDTO dto ) {
			super(id);
			this.model = dto;
			initComponents();
		}

		private void initComponents() {
			
			add(getWorkflowPanel());
			add(getCancelButton());
			add(getSaveButton());
		}
		
	}

	@Override
	public String getPageName() {
		return "Workflow Details";
	}
	
	/**
	 * This method is called when the window is closed, so that post
	 * close processing can be done. The finished successfully flag
	 * indicates if the save was completed successfully before the 
	 * window closed.
	 * 
	 * @param target
	 * @param finishedSuccesfully
	 */
	public  void processClose(AjaxRequestTarget target, boolean finishedSuccessfully) {
		logger.debug("ProcessClose called " + finishedSuccessfully);
	}
	
	private Button getSaveButton() {
		if (saveButton==null) {
			saveButton = new AjaxButton("saveButton") {
				
				@Override
				protected void onSubmit(AjaxRequestTarget target, Form form) {
					if (logger.isDebugEnabled())
						logger.debug("Save.onsubmit finished=true"
							+ "  form=" + form
							+ "   workflow=" + workflowDTO.getWorkflowComment()
							+ "   page=" + target.getPageClass()
							+ "   page renderCnt=" + target.getPage().getRenderCount()
							+ "   page =" + target.getPage());

					success = true;
					processClose(target, true);
					pageModel.setModalWizardSuccess(true);
					window.setSessionModelForPage(pageModel);
					window.close(target);

				}

				@Override
				protected void onError(AjaxRequestTarget target, Form form) {
					logger.debug("Save.onError finished=false");
					target.add(getFeedBackPanel());	
//					target.add(this);	
					
//					processClose(target, false);
				}
				
				@Override
				protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
					super.updateAjaxAttributes(attributes);
					attributes.getAjaxCallListeners().add(new AjaxCallListener() {

						@Override
						public CharSequence getInitHandler(Component component) {
							CharSequence s =   super.getInitHandler(component);
							return "overlay(true);"+ ((s==null)?"":s);
						}
									
					});
					
				}

				
				
			};
			
			
//			// Add an update behaviour which is slightly delayed and will 
//			saveButton.add(new AjaxFormComponentUpdatingBehavior("click"){
//						   private static final long serialVersionUID = 1L;
//							@Override
//							protected void onUpdate(AjaxRequestTarget target) {		
//								pageModel.setModalWizardSuccess(true);
//								window.setSessionModelForPage(pageModel);
//								window.close(target);
//							}	
//							
//						});	
			saveButton.setOutputMarkupId(true);
		}
		return saveButton;
	}
	
	private AjaxButton getCancelButton() {
		if (cancelButton==null) {
			cancelButton = new AjaxButton("cancelButton") {
				@Override
				protected void onSubmit(AjaxRequestTarget target, Form form) {
					logger.debug("Cancel.onsubmit finished=false");
					pageModel.setModalWizardSuccess(false);
					window.setSessionModelForPage(pageModel);
					window.close(target);
				}
				
			};
			cancelButton.setOutputMarkupId(true);
			cancelButton.setDefaultFormProcessing(false);
		}
		return cancelButton;
	}

	private WorkflowPanel getWorkflowPanel() {
		if (workflowPanel==null) {
			workflowPanel = new WorkflowPanel(
					"workflowPanel",
					EditStateType.MODIFY,
					workflowDTO);
		}
		return workflowPanel;
	}

	@Override
	public boolean isShowFeedBackPanel() {
		return true;
	}
	
	public boolean isSuccess() {
		return success;
	}
}
