package za.co.liberty.web.pages.maintainagreement;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.extensions.wizard.WizardStep;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.Model;

import za.co.liberty.business.guicontrollers.agreement.IAgreementGUIController;
import za.co.liberty.dto.agreement.AgreementDTO;
import za.co.liberty.dto.agreement.AssociatedAgreementDetailsDTO;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.helpers.lookup.ServiceLocator;
import za.co.liberty.interfaces.agreements.UserActionType;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.pages.maintainagreement.model.MaintainAgreementPageModel;
import za.co.liberty.web.pages.wizard.SRSPopupWizard;
import za.co.liberty.web.pages.wizard.object.SRSWizardPageDetail;
import za.co.liberty.web.wicket.modal.SRSModalWindow;

@Deprecated
//Wizard not being used anymore-14/07/2010-Pritam
//For add/edit associated agreements ,AddAssociatedAgreementWizardPage does all processing and display logic
public class AddAssociatedAgreementWizard extends SRSPopupWizard<MaintainAgreementPageModel> {
	
	private MaintainAgreementPageModel pageModel;
	private transient IAgreementGUIController guiController;
	private transient Logger logger = Logger.getLogger(AddAssociatedAgreementWizard.class);
	private ModalWindow parentWindow;
	private AddAssociatedAgreementsPanel addAssociatedAgreementDetailsPanel;
	private List<AssociatedAgreementDetailsDTO> editList;
	private Form form;
	
	

	public AddAssociatedAgreementWizard(String id, SRSModalWindow parentWindow, MaintainAgreementPageModel pageModel,List<AssociatedAgreementDetailsDTO> editList, Form form) { 
		super(id,parentWindow);
		this.parentWindow=parentWindow;
		this.pageModel = pageModel;
		this.editList = editList;
		this.form = form;
		addAssociatedAgreementDetailsPanel.setPageModel(pageModel);
		addAssociatedAgreementDetailsPanel.setEditListAssAgmt(editList);
	}
	
	@Override
	protected Collection<SRSWizardPageDetail> getWizardSteps(MaintainAgreementPageModel pageModel) {
		List<SRSWizardPageDetail> ret = new ArrayList<SRSWizardPageDetail>();
		ret.add(new SRSWizardPageDetail(new AssociatedAgreementDetailsStep()));
		
		return ret;
	}
	
	private final class AssociatedAgreementDetailsStep extends WizardStep {
		
		public AssociatedAgreementDetailsStep() {
			setTitleModel(new Model("Associated Agreement Details"));
			addAssociatedAgreementDetailsPanel = getAddAssociatedAgreementDetailsPanel(AddAssociatedAgreementWizard.this.pageModel);
			add(addAssociatedAgreementDetailsPanel);
		}
	}


	private AddAssociatedAgreementsPanel getAddAssociatedAgreementDetailsPanel(MaintainAgreementPageModel pageModel) {
		return new AddAssociatedAgreementsPanel(
				"addAssociatedAgreementDetailsPanel",
				pageModel, getFeedback(),
				EditStateType.ADD,editList
				);
	}

	@Override
	protected MaintainAgreementPageModel initializePageModel(MaintainAgreementPageModel model) {
		AgreementDTO agreementDTO = new AgreementDTO();
		pageModel = new MaintainAgreementPageModel(
				agreementDTO,
				getGuiController().getValidAgreementValues(agreementDTO));
		return pageModel;
	}
	
	@Override
	public boolean onFinish(AjaxRequestTarget target) {
		
		AssociatedAgreementDetailsDTO  agreementDetailsDTO = addAssociatedAgreementDetailsPanel.getAssociatedAgmtModel();
		processAssociatedAgreementListOfModel(agreementDetailsDTO);
		return true;
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
				logger.fatal("Could not lookup AgreementGUIController",e);
				throw new CommunicationException("Could not lookup AgreementGUIController",e);
			}
		}
		return guiController;
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

	@Override
	public boolean onCancel(AjaxRequestTarget target) {
		// TODO Auto-generated method stub
		return false;
	}
	
}
