package za.co.liberty.web.pages.party;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.apache.wicket.PageReference;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.wizard.WizardStep;
import org.apache.wicket.model.Model;

import za.co.liberty.business.guicontrollers.IContextManagement;
import za.co.liberty.business.guicontrollers.partymaintenance.IPartyMaintenanceController;
import za.co.liberty.dto.contracting.ResultPartyDTO;
import za.co.liberty.dto.party.EmployeeDTO;
import za.co.liberty.dto.party.PartyDTO;
import za.co.liberty.dto.party.PersonDTO;
import za.co.liberty.dto.party.contactdetail.ContactPreferenceDTO;
import za.co.liberty.dto.party.contactdetail.ContactPreferenceWrapperDTO;
import za.co.liberty.dto.userprofiles.ContextDTO;
import za.co.liberty.dto.userprofiles.ISessionUserProfile;
import za.co.liberty.exceptions.data.DataNotFoundException;
import za.co.liberty.exceptions.data.ValidationException;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.helpers.lookup.ServiceLocator;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.pages.contactdetail.ContactDetailsPanel;
import za.co.liberty.web.pages.party.model.MaintainPartyPageModel;
import za.co.liberty.web.pages.wizard.SRSPopupWizard;
import za.co.liberty.web.pages.wizard.object.SRSWizardPageDetail;
import za.co.liberty.web.system.SRSAuthWebSession;
import za.co.liberty.web.wicket.modal.SRSModalWindow;
/**
 * Wizard to add a new party to the system
 * @author DZS2610
 *
 */
public class AddPartyWizard extends SRSPopupWizard<MaintainPartyPageModel> {	
	private static final long serialVersionUID = 1L;
	private MaintainPartyPageModel pageModel;
	private SRSModalWindow parentWindow;	
	transient Logger transLogger;
	private PageReference pageReference;
	
	/**
	 * Initialize all variables
	 *
	 */
	private void init(){
			
	}
	
	/**
	 * Default constructor
	 * @param id
	 * @param pageModel
	 * @param parentWindow
	 */
	public AddPartyWizard(String id,SRSModalWindow parentWindow, PageReference pageReference) {
		super(id,parentWindow, pageReference);		
		this.parentWindow = parentWindow;	
		this.pageReference = pageReference;
		init();
	}
	
	
	/**
	 * Step 2, store personal details of employee
	 * @author DZS2610
	 *
	 */
	private final class Step2 extends WizardStep
	{			
		private static final long serialVersionUID = 1L;

		public Step2()
		{						
			if(pageModel == null || pageModel.getPartyDTO() == null || pageModel.getPartyDTO() instanceof PersonDTO){
				setTitleModel(new Model("Person Details"));
				add(new PersonDetailsPanel("partyPanel",pageModel, EditStateType.ADD,null));		
			}else{
				setTitleModel(new Model("Organisation Details"));
				add(new OrganisationDetailsPanel("partyPanel",pageModel, EditStateType.ADD,null));	
			}
		}
		
		

		@Override
		protected void onBeforeRender() {			
			if(pageModel == null || pageModel.getPartyDTO() == null || pageModel.getPartyDTO() instanceof PersonDTO){
				if(!(this.get("partyPanel") instanceof PersonDetailsPanel)){
					remove("partyPanel");	
					setTitleModel(new Model("Person Details"));
					add(new PersonDetailsPanel("partyPanel",pageModel, EditStateType.ADD,null));	
				}
			}else{
				if(!(this.get("partyPanel") instanceof OrganisationDetailsPanel)){
					remove("partyPanel");
					setTitleModel(new Model("Organisation Details"));
					add(new OrganisationDetailsPanel("partyPanel",pageModel, EditStateType.ADD,null));	
				}
			}
			super.onBeforeRender();
		}		
	}	
	

	@Override
	protected Collection<SRSWizardPageDetail> getWizardSteps(MaintainPartyPageModel pageModel) {		
		ArrayList<SRSWizardPageDetail> steps = new ArrayList<SRSWizardPageDetail>();
		steps.add(new SRSWizardPageDetail("Party Type Selection",new PartyTypePanel(SRSPopupWizard.SRS_WIZARD_STEP_ID,pageModel, EditStateType.ADD)));
		steps.add(new SRSWizardPageDetail(new Step2()));			
		steps.add(new SRSWizardPageDetail("Contact Details", new ContactDetailsPanel(SRSPopupWizard.SRS_WIZARD_STEP_ID,
				((pageModel != null && pageModel.getPartyDTO() != null && pageModel.getPartyDTO().getContactPreferences() != null) ? pageModel.getPartyDTO().getContactPreferences().getContactPreferences() : null),
				pageModel.getPartyDTO().getCommunicationPreferences(),
				EditStateType.ADD,this.getFeedback(),true,false,null, false)));
		return steps;
	}	
	
	@Override
	public boolean onFinish(AjaxRequestTarget target){		
		
		if (getLogger().isDebugEnabled())
			getLogger().debug("AddPartyWizard.onFinish(target)");
		
		//this gets called after onFinish(AjaxRequestTarget target)
		//validate all fields	
		//if(pageModel.getPartyDTO() != null && pageModel.getPartyDTO() instanceof PersonDTO){
		//	PartyUtil.isPartyAlreadyExist(new ApplicationContext(),((PersonDTO)pageModel.getPartyDTO()).getIdentificationNumber(),((PersonDTO)pageModel.getPartyDTO()).getIdentificationNumberType(),pageModel.getPartyDTO().getOid());
		//}
		//get actual the contact details panel
		
		
		pageModel.getPartyDTO().setContactPreferences(new ContactPreferenceWrapperDTO(getStepPanelOfType(ContactDetailsPanel.class,SRSPopupWizard.SRS_WIZARD_STEP_ID).getCurrentContactPreferenceDetails()));
		IPartyMaintenanceController controller = MaintainPartyPageModel.getPartyMaintenanceController();	
		try {
			ISessionUserProfile userProfile = SRSAuthWebSession.get().getSessionUser();
			long partyOID =  controller.storeParty(this.pageModel.getPartyDTO(),null,userProfile,null,null,false,null);
			this.pageModel.getPartyDTO().setOid(partyOID);
//			System.out.println("Pagemodel - " + this.pageModel);
//			System.out.println("Party Stored - " + partyOID);
			 SRSAuthWebSession.get().setAttribute("AddPartyWizardModel", this.pageModel);
		} catch (ValidationException e) {
			for(String error : e.getErrorMessages()){
				error(error);
			}
			target.add(getFeedback());//target.addComponent(getFeedback());
			return false;
		}
//		
		
		return true;		
	}


	@Override
	protected MaintainPartyPageModel initializePageModel(MaintainPartyPageModel model) {
		if(pageModel == null){
			pageModel = new MaintainPartyPageModel();			
		}
		//we need a new party object as it is an add new wizard
		//we default to an employee
		EmployeeDTO emp = new EmployeeDTO();	
		emp.setEffectiveFrom(new Date());
		ContactPreferenceWrapperDTO wrapper = new ContactPreferenceWrapperDTO();
		wrapper.setContactPreferences(new ArrayList<ContactPreferenceDTO>());
		emp.setContactPreferences(wrapper);	
		pageModel.setPartyDTO(emp);
		return pageModel;
	}

	@Override
	public boolean onCancel(AjaxRequestTarget target) {
		return false;
	}

	protected Logger getLogger() {
		if (transLogger==null) {
			transLogger = Logger.getLogger(this.getClass());
		}
		return transLogger;
	}
	
	
	
}
