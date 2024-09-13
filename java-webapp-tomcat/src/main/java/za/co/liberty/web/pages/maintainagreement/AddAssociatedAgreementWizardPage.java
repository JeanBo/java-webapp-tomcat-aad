package za.co.liberty.web.pages.maintainagreement;

import java.util.ArrayList;
import java.util.List;

import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.form.AjaxFormSubmitBehavior;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;

import za.co.liberty.business.guicontrollers.agreement.IAgreementGUIController;
import za.co.liberty.dto.agreement.AgreementDTO;
import za.co.liberty.dto.agreement.AssociatedAgreementDetailsDTO;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.helpers.lookup.ServiceLocator;
import za.co.liberty.interfaces.agreements.UserActionType;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.pages.BaseWindowPage;
import za.co.liberty.web.pages.maintainagreement.model.MaintainAgreementPageModel;
import za.co.liberty.web.wicket.modal.SRSModalWindow;

public class AddAssociatedAgreementWizardPage extends BaseWindowPage {
	
	private SRSModalWindow parentWindow;
	private MaintainAgreementPageModel pageModel;
	private List<AssociatedAgreementDetailsDTO> editList;
//	private Form form;
	public enum ButtonSelected{OK,CANCEL};
	private ButtonSelected currentAction;
	private AddAssociatedAgreementsPanel addAssociatedAgreementDetailsPanel;
	private transient IAgreementGUIController guiController;
	private transient Logger logger; 

	private static final long serialVersionUID = 5966777694740706438L;
	
	public AddAssociatedAgreementWizardPage(SRSModalWindow parentWindow, MaintainAgreementPageModel pageModel,
			List<AssociatedAgreementDetailsDTO> editList) {
		super();
		this.parentWindow = parentWindow;
		this.pageModel = pageModel;
		this.editList = editList;
//		this.form = form;
		initComponents();
	}

	private void initComponents() {
		//add(getAddAssociatedAgreementWizard());
		add(new AddEditAssociatedAgreementsForm("addEditAssociatedAgmtForm",parentWindow,pageModel,editList));
//		parentWindow.setWindowClosedCallback(new ModalWindow.WindowClosedCallback() {
//				private static final long serialVersionUID = 1L;			
//				public void onClose(AjaxRequestTarget target) {
//					processClose(target);
//				}			
//			});	
	}

	/*private Wizard getAddAssociatedAgreementWizard() {
		Wizard agreementWizard = new AddAssociatedAgreementWizard("addAssociatedAgreementWizard",parentWindow,model,editList, form);
		return agreementWizard;
	}*/
	
	/**
	 * Form for the adding/editing of associated agreements
	 * 
	 * @author DZS2610
	 * 
	 */
	private class AddEditAssociatedAgreementsForm extends Form {
		private static final long serialVersionUID = 1L;
		
		public AddEditAssociatedAgreementsForm(String id,SRSModalWindow parentWindow, MaintainAgreementPageModel pageModel,
				List<AssociatedAgreementDetailsDTO> editList) {
			super(id);
			add(addOkButton("okButton"));
			add(addCancelButton("cancelButton"));
			add(addAssociatedAgreementDetailsPanel = new AddAssociatedAgreementsPanel(
					"addAssociatedAgreementDetailsPanel",
					initializePageModel(), getFeedBackPanel(),
					EditStateType.ADD,editList
					));
			addAssociatedAgreementDetailsPanel.setPageModel(pageModel);
			parentWindow.setSessionModelForPage(pageModel);
			addAssociatedAgreementDetailsPanel.setEditListAssAgmt(editList);
		}
		
		private MaintainAgreementPageModel initializePageModel() {
			AgreementDTO agreementDTO = new AgreementDTO();
			MaintainAgreementPageModel model = new MaintainAgreementPageModel(
					agreementDTO,
					getGuiController().getValidAgreementValues(agreementDTO));
			
			return model;
			
		}
		/**
		 * Ok button
		 * 
		 * @return
		 */
		@SuppressWarnings("unused")
		private Button addOkButton(String id) {
			Button button = new Button(id);
			button.add(new AjaxFormSubmitBehavior("click") {
				private static final long serialVersionUID = 1L;

				@Override
				protected void onError(AjaxRequestTarget target) {
					// error occured, add to feedback and display
					target.add(getFeedBackPanel());

				}

				@Override
				protected void onSubmit(AjaxRequestTarget target) {
					do_Ok(target);
				}
			});
			return button;
		}
		
		/**
		 * Cancel button
		 * 
		 * @return
		 */
		@SuppressWarnings("unused")
		private Button addCancelButton(String id) {			
			Button button = new Button(id);
			button.add(new AjaxFormComponentUpdatingBehavior("click") {
				private static final long serialVersionUID = 1L;

				@Override
				protected void onUpdate(AjaxRequestTarget target) {					
					currentAction = ButtonSelected.CANCEL;
					parentWindow.setSessionModelForPage(pageModel);
					pageModel.setModalWizardSuccess(false);
					parentWindow.close(target);
				}				
				
			});
			return button;
		}
		
		

		/**
		 * Process the ok click
		 * 
		 */
		private void do_Ok(AjaxRequestTarget target) {
				AssociatedAgreementDetailsDTO  agreementDetailsDTO = addAssociatedAgreementDetailsPanel.getAssociatedAgmtModel();
				processAssociatedAgreementListOfModel(agreementDetailsDTO);
				currentAction = ButtonSelected.OK;
				parentWindow.setSessionModelForPage(pageModel);
				pageModel.setModalWizardSuccess(true);
				 //Close window call back
				parentWindow.close(target);	
		}
		
		
	}
	
	public void processClose(AjaxRequestTarget target) {

	}	
	
	/**
	 * Load the AgreementGUIController dynamically if it is null as this is a transient variable.
	 * @return {@link IAgreementGUIController}
	 */
	private IAgreementGUIController getGuiController() {
		if (guiController==null) {
			try {
				guiController = ServiceLocator.lookupService(IAgreementGUIController.class);
			} catch (NamingException e) {
				getLogger().fatal("Could not lookup AgreementGUIController",e);
				throw new CommunicationException("Could not lookup AgreementGUIController",e);
			}
		}
		return guiController;
	}
	

	@Override
	public String getPageName() {
		if(this.editList == null)
			return "Add Associated Agreements";
		else
			return "Edit Associated Agreements";
	}
	
	@Override
	public boolean isShowFeedBackPanel() {
		return true;
	}
	
	private void processAssociatedAgreementListOfModel(AssociatedAgreementDetailsDTO addEditDTO)
	{
		List<AssociatedAgreementDetailsDTO> list = pageModel.getMaintainAgreementDTO().getAgreementDTO().getAssociatedAgreementDetailsList();
		if(addEditDTO == null)
			return;
		
		if(list == null){
			list = new ArrayList<AssociatedAgreementDetailsDTO>();
		}
		//For Add
		if(editList == null){
			addEditDTO.setUserActionType(UserActionType.ADD);
			list.add(addEditDTO);
			pageModel.getMaintainAgreementDTO().getAgreementDTO().setAssociatedAgreementDetailsList(list);
		} else//For Edit
		{
			for(AssociatedAgreementDetailsDTO detailsDTO:list){
				if(addEditDTO.getAssociatedAgreement().equals(detailsDTO.getAssociatedAgreement()) &&
						addEditDTO.getCommissionKind().equals(detailsDTO.getCommissionKind()) &&
						addEditDTO.getAssociatedPercentage().equals(detailsDTO.getAssociatedPercentage()) &&
						addEditDTO.getStartDate().equals(detailsDTO.getStartDate()))
				{
					detailsDTO.setEndDate(addEditDTO.getEndDate());
					detailsDTO.setUserActionType(UserActionType.UPDATE);
				}			
				
			}			
			pageModel.getMaintainAgreementDTO().getAgreementDTO().setAssociatedAgreementDetailsList(list);
		}		
	}
	
	private Logger getLogger(){
		if(logger == null){
		logger = Logger.getLogger(AddAssociatedAgreementWizardPage.class);
		}
		return logger;
	}

}
