package za.co.liberty.web.pages.hierarchy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.naming.NamingException;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.wizard.WizardStep;
import org.apache.wicket.model.Model;

import za.co.liberty.business.guicontrollers.IContextManagement;
import za.co.liberty.business.guicontrollers.hierarchy.IHierarchyGUIController;
import za.co.liberty.dto.contracting.ResultPartyDTO;
import za.co.liberty.dto.party.HierarchyNodeDTO;
import za.co.liberty.dto.party.contactdetail.ContactPreferenceWrapperDTO;
import za.co.liberty.dto.userprofiles.ContextDTO;
import za.co.liberty.dto.userprofiles.ISessionUserProfile;
import za.co.liberty.exceptions.data.DataNotFoundException;
import za.co.liberty.exceptions.data.ValidationException;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.helpers.lookup.ServiceLocator;
import za.co.liberty.helpers.persistence.TemporalityHelper;
import za.co.liberty.srs.type.SRSType;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.pages.MaintenanceBasePage;
import za.co.liberty.web.pages.contactdetail.ContactDetailsPanel;
import za.co.liberty.web.pages.hierarchy.model.MaintainHierarchyPageModel;
import za.co.liberty.web.pages.wizard.SRSPopupWizard;
import za.co.liberty.web.pages.wizard.object.SRSWizardPageDetail;
import za.co.liberty.web.system.SRSAuthWebSession;
import za.co.liberty.web.wicket.modal.SRSModalWindow;
/**
 * Wizard to add a new party to the system
 * @author DZS2610
 *
 */
public class AddHierarchyWizard extends SRSPopupWizard<MaintainHierarchyPageModel> {	
	private static final long serialVersionUID = 1L;
	private MaintainHierarchyPageModel pageModel;	
	private HierarchyNodePanel nodePanel;
	private ContactDetailsPanel contactDetailsPanel;
	private MIReportingDetailsPanel miReportingDetailsPanel;
	MaintenanceBasePage parentPage;	
	
	public AddHierarchyWizard(String id,MaintainHierarchyPageModel pageModel,SRSModalWindow parentWindow, MaintenanceBasePage parentPage) {
		super(id,parentWindow,pageModel);	
		this.setOutputMarkupId(true);		
		this.parentPage = parentPage;			
	}

	/**
	 * Step 1, store hierarchy details
	 * @author DZS2610
	 *
	 */
	private final class Step1 extends WizardStep
	{			
		private static final long serialVersionUID = 1L;

		public Step1()
		{
			setTitleModel(new Model("Step 1"));
			nodePanel = new HierarchyNodePanel("hierarchyNodePanel",pageModel, EditStateType.ADD, getFeedback(),null);			
			add(nodePanel);					
		}		
	}
	
	/**
	 * Step 1, store hierarchy details
	 * @author DZS2610
	 *
	 */
	private final class Step2 extends WizardStep
	{			
		private static final long serialVersionUID = 1L;

		public Step2()
		{
			setTitleModel(new Model("Step 2"));
			contactDetailsPanel = new ContactDetailsPanel("contactDetailsPanel",((pageModel != null && pageModel.getHierarchyNodeDTO() != null && pageModel.getHierarchyNodeDTO().getContactPreferences() != null) ? pageModel.getHierarchyNodeDTO().getContactPreferences().getContactPreferences() : null), 
					pageModel.getHierarchyNodeDTO().getCommunicationPreferences(), EditStateType.ADD, getFeedback(),false,null, false);
			add(contactDetailsPanel);					
		}		
		
	}	
	
	/**
	 * Step 3, store hierarchy mi details
	 * @author MZL2611
	 *
	 */
	private final class Step3 extends WizardStep
	{			
		private static final long serialVersionUID = 1L;

		public Step3()
		{
			setTitleModel(new Model("Step 3"));
			miReportingDetailsPanel = new MIReportingDetailsPanel("miReportingDetailsPanel",((pageModel != null && pageModel.getHierarchyNodeDTO() != null && pageModel != null) ? pageModel : null), EditStateType.ADD, getFeedback(),null);
			add(miReportingDetailsPanel);					
		}		
		
	}	

	public boolean onFinish(AjaxRequestTarget target){		
		//this gets called after onFinish(AjaxRequestTarget target)
		//validate all fields		
		ISessionUserProfile userProfile = SRSAuthWebSession.get().getSessionUser();
		//I want to close the popup so I will do all work in the other onFinish
		IHierarchyGUIController controller = this.getHierarchyGUIController();	
		try {		
			pageModel.getHierarchyNodeDTO().setContactPreferences(new ContactPreferenceWrapperDTO(contactDetailsPanel.getCurrentContactPreferenceDetails()));
			controller.storeNode(this.pageModel.getHierarchyNodeDTO(),userProfile,
					null,null);	
			
			//add newly store node into division list if it is a division
			if(pageModel.getHierarchyNodeDTO().getType().getOid() == SRSType.DIVISION){
				try {
					pageModel.setHierarchyChannelList(pageModel.getHierarchyGUIController().getHierarchyChannelList());
				} catch (DataNotFoundException e) {				
					getLogger().error("An internal error occurred while raising the request - " + e.getMessage(),e);
					error("An internal error occurred while raising the request - " + e.getMessage());
					target.add(getFeedback());
					return false;

				}
			}

	
		} catch (ValidationException e) {
			for(String error : e.getErrorMessages()){
				if (error == null || error.trim().length()==0) {
					error("Internal error occurred while raising the request, check logs '" + error +"'");
				} else {
					error(error);
				}
			}
			target.add(getFeedback());
			return false;
		}			
		
		
		
		try{
			IContextManagement contextBean;
			try {
				contextBean = ServiceLocator.lookupService(IContextManagement.class);
			} catch (NamingException e) {
				throw new CommunicationException(e);
			}		
			ContextDTO newContextDTO = SRSAuthWebSession.get().getContextDTO().clone();				
			List<ResultPartyDTO> parties = pageModel.getHierarchyGUIController().findPartyWithOrganisationNameOfType(pageModel.getHierarchyNodeDTO().getBusinessName(),pageModel.getHierarchyNodeDTO().getType().getOid());
			for(ResultPartyDTO party : parties){
				boolean found = false;
				if(party.getName().equalsIgnoreCase(pageModel.getHierarchyNodeDTO().getBusinessName())){
					ContextDTO dto = contextBean.getContext(party);
					newContextDTO.setPartyContextDTO(dto.getPartyContextDTO());
					newContextDTO.setAgreementContextDTO(dto.getAgreementContextDTO());
					SRSAuthWebSession.get().setContextDTO(newContextDTO);
					found = true;
					break;
				}
				if(!found){
					getSession().error("Node stored but could not be put into the context, this could be due to future dating");
				}
			}
			return true;
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			getSession().error("Node stored but could not be put into the context");
		} catch (DataNotFoundException e) {
			e.printStackTrace();
			getSession().error("Node stored but could not be put into the context");
		}
		
		return false;
	}

	
	/**
	 * get an instance of IHierarchyGUIController
	 * @return
	 */
	private IHierarchyGUIController getHierarchyGUIController() {
		IHierarchyGUIController hierarchyMaintenanceController;
		try {
			hierarchyMaintenanceController = ServiceLocator.lookupService(IHierarchyGUIController.class);
		} catch (NamingException e) {
			throw new CommunicationException(e);
		}
		return hierarchyMaintenanceController;
	}
	
	/**
	 * Returns the node that is used for the panel details
	 * @return
	 */
	public HierarchyNodeDTO getHierarchyNodeDTO(){
		if(pageModel != null){
			return pageModel.getHierarchyNodeDTO();
		}else{
			return null;
		}
	}

	@Override
	protected Collection<SRSWizardPageDetail> getWizardSteps(MaintainHierarchyPageModel pageModel) {
		Collection<SRSWizardPageDetail> steps = new ArrayList<SRSWizardPageDetail>(2);
		steps.add(new SRSWizardPageDetail(new Step1()));
		steps.add(new SRSWizardPageDetail(new Step2()));	
		steps.add(new SRSWizardPageDetail(new Step3()));	
		return steps;
	}

	@Override
	protected MaintainHierarchyPageModel initializePageModel(MaintainHierarchyPageModel model) {		
		this.pageModel = model;
		HierarchyNodeDTO dto = new HierarchyNodeDTO();			
		dto.setEffectiveFrom(TemporalityHelper.getInstance().getNewNOWDateWithNoTime());		
		if(pageModel == null){			
			pageModel = new MaintainHierarchyPageModel();
		}
		pageModel.setHierarchyNodeDTO(dto);
		return pageModel;
	}



	@Override
	public boolean onCancel(AjaxRequestTarget target) {
		// TODO Auto-generated method stub
		return false;
	}	
}
