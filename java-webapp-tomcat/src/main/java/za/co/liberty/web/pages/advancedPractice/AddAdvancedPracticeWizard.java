package za.co.liberty.web.pages.advancedPractice;

import java.util.ArrayList;
import java.util.Collection;

import javax.naming.NamingException;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.wizard.WizardStep;
import org.apache.wicket.model.Model;

import za.co.liberty.business.guicontrollers.IContextManagement;
import za.co.liberty.business.guicontrollers.advancedPractice.IAdvancedPracticeGUIController;
import za.co.liberty.business.party.IPartyManagement;
import za.co.liberty.dto.advancedPractice.AdvancedPracticeDTO;
import za.co.liberty.dto.advancedPractice.AdvancedPracticeManagerDTO;
import za.co.liberty.dto.advancedPractice.AdvancedPracticeMemberDTO;
import za.co.liberty.dto.contracting.ResultPartyDTO;
import za.co.liberty.dto.userprofiles.ContextDTO;
import za.co.liberty.dto.userprofiles.ISessionUserProfile;
import za.co.liberty.exceptions.data.DataNotFoundException;
import za.co.liberty.exceptions.data.ValidationException;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.helpers.lookup.ServiceLocator;
import za.co.liberty.helpers.persistence.TemporalityHelper;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.pages.MaintenanceBasePage;
import za.co.liberty.web.pages.advancedPractice.model.MaintainAdvancedPracticePageModel;
import za.co.liberty.web.pages.contactdetail.ContactDetailsPanel;
import za.co.liberty.web.pages.wizard.SRSPopupWizard;
import za.co.liberty.web.pages.wizard.object.SRSWizardPageDetail;
import za.co.liberty.web.system.SRSAuthWebSession;
import za.co.liberty.web.wicket.modal.SRSModalWindow;

public class AddAdvancedPracticeWizard extends SRSPopupWizard<MaintainAdvancedPracticePageModel> {	
		private static final long serialVersionUID = 1L;
		private MaintainAdvancedPracticePageModel pageModel;	
		private AdvancedPracticePanel practicePanel;
		private ContactDetailsPanel contactDetailsPanel;
		private transient IAdvancedPracticeGUIController controller;
		private transient IPartyManagement partyManagement;
		
		public AddAdvancedPracticeWizard(String id,MaintainAdvancedPracticePageModel pageModel,SRSModalWindow parentWindow) {
			super(id,parentWindow,pageModel);	
			this.setOutputMarkupId(true);		
//			this.parentPage = parentPage;	
			this.controller = getAdvancedPracticeGUIController();
		}

		/**
		 * Step 1, advanced practice details
		 * @author MXM1904
		 *
		 */
		private final class Step1 extends WizardStep
		{			
			private static final long serialVersionUID = 1L;

			public Step1()
			{
				setTitleModel(new Model("Step 1"));
				practicePanel = new AdvancedPracticePanel("practicePanel",
						pageModel.getPanelModel(), 
						EditStateType.ADD, 
						null);			
		
				
				add(practicePanel);					
			}		
		}
		


		public boolean onFinish(AjaxRequestTarget target){		
			//this gets called after onFinish(AjaxRequestTarget target)
			//validate all fields		
			ISessionUserProfile userProfile = SRSAuthWebSession.get().getSessionUser();
			//I want to close the popup so I will do all work in the other onFinish
			
			try {		
				//Raise request  
				// Jean - Request requires authorisation and will not be able to be set in the context.
				//pageModel.getAdvancedPracticeDTO().setContactPreferences(new ContactPreferenceWrapperDTO(contactDetailsPanel.getCurrentContactPreferenceDetails()));
				getAdvancedPracticeGUIController().raiseAdvancedPracticeRequest(userProfile, pageModel.getPanelModel().getAdvancedPracticeDTO(),
						pageModel.getAdvancedPracticeDTOBeforeImage());
				
				
				
				//add newly stored managers and members into practice list
//				if(pageModel.getAdvancedPracticeDTO() != null){
//					try {
//						pageModel.getAdvancedPracticeDTO().setAdvancedPracticeManagerDTOlist((new ArrayList<AdvancedPracticeManagerDTO>()));
//					} catch (Exception e) {				
//						//do nothing, old list is still ok to use -- should never cause an issue
//					}
//				}
				
				/*
				 * Uncomment this if the nr of authorisers change
				 */
//				setAdvancedPracticeInContext();
					
				return true;		
			} catch (ValidationException e) {
				for(String error : e.getErrorMessages()){
					error(error);
				}
				target.add(getFeedback());
				return false;
			}			
		}

		/**
		 * Set the newly added practice in the context.
		 * 
		 * @return
		 */
		private boolean setAdvancedPracticeInContext() {
			//Use below if users want to see stored practice on main screen		
			try{
				IContextManagement contextBean;
				try {
					contextBean = ServiceLocator.lookupService(IContextManagement.class);
				} catch (NamingException e) {
					throw new CommunicationException(e);
				}
				
				ContextDTO newContextDTO = SRSAuthWebSession.get().getContextDTO().clone();				
				//List<ResultPartyDTO> parties = controller.findPartyWithOrganisationNameOfType(pageModel.getAdvancedPracticeDTO().getBusinessName(),pageModel.getAdvancedPracticeDTO().getTypeOID());
				partyManagement = getIPartyManagement();
				if(partyManagement == null){
					return false;
				}	
				StringBuffer errorString = new StringBuffer("");
				getLogger().info("Searching for advanced practice  with partyOid " + pageModel.getAdvancedPracticeDTO().getOid());
				ResultPartyDTO party = getAdvancedPracticeGUIController().findPartyWithObjectOid(pageModel.getAdvancedPracticeDTO().getOid());

				if(party != null){
					boolean found = false;
					if(party.getName().equalsIgnoreCase(pageModel.getAdvancedPracticeDTO().getBusinessName())){
						ContextDTO dto = contextBean.getContext(party);
						newContextDTO.setPartyContextDTO(dto.getPartyContextDTO());
						newContextDTO.setAgreementContextDTO(dto.getAgreementContextDTO());
						SRSAuthWebSession.get().setContextDTO(newContextDTO);
						found = true;
					}
					if(!found){
						errorString = errorString.length() > 0 ? errorString.delete(0,errorString.length()+1) : errorString;
						errorString.append("Practice stored but could not be put into the context, this could be due to future dating");						
					} else {
						return true;
					}
					
				} else {
					getLogger().error("Advanced practice NOT FOUND - with partyOid " + pageModel.getAdvancedPracticeDTO().getOid());
				}
				getSession().warn(errorString.toString());
				
			} catch (CloneNotSupportedException e) {
				//getSession().warn("Practice stored but could not be put into the context");
				getLogger().info("Clone error is encontered : " + e.getMessage());
			} catch (DataNotFoundException e) {
				getSession().warn("Practice stored but could not be put into the context");
			}
			
			return false;
			
		}

		/**
		 * Returns the node that is used for the panel details
		 * @return
		 */
		public AdvancedPracticeDTO getAdvancedPracticeDTO(){
			if(pageModel != null){
				return pageModel.getAdvancedPracticeDTO();
			}else{
				return null;
			}
		}

		@Override
		protected Collection<SRSWizardPageDetail> getWizardSteps(MaintainAdvancedPracticePageModel pageModel) {
			Collection<SRSWizardPageDetail> steps = new ArrayList<SRSWizardPageDetail>(1);
			steps.add(new SRSWizardPageDetail(new Step1()));
			return steps;
		}

		@Override
		protected MaintainAdvancedPracticePageModel initializePageModel(MaintainAdvancedPracticePageModel model) {		
			this.pageModel = model;
			AdvancedPracticeDTO advancedPracticeDTO = new AdvancedPracticeDTO();			
			advancedPracticeDTO.setEffectiveFrom(TemporalityHelper.getInstance().getNewNOWDateWithNoTime());		
			if(pageModel == null){			
				pageModel = new MaintainAdvancedPracticePageModel();
			}
			advancedPracticeDTO.setAdvancedPracticeManagerDTOlist(new ArrayList<AdvancedPracticeManagerDTO>());
			advancedPracticeDTO.setAdvancedPracticeMemberDTOList(new ArrayList<AdvancedPracticeMemberDTO>());
			pageModel.setAdvancedPracticeDTO(advancedPracticeDTO);
			return pageModel;
		}



		/**
		 * Gets the IAgreementPrivilegesController interface for calls to the
		 * AdvancedPracticeGUIController session bean
		 * 
		 * @return
		 */
		private IAdvancedPracticeGUIController getAdvancedPracticeGUIController(){
			if(controller == null){
				try {
					controller = ServiceLocator.lookupService(IAdvancedPracticeGUIController.class);
				} catch (NamingException e) {
					throw new CommunicationException(e);
				}
			}
			return controller;
			
		}
		
		/**
		 * Gets the IAgreementPrivilegesController interface for calls to the
		 * AdvancedPracticeGUIController session bean
		 * 
		 * @return
		 */
		private IPartyManagement getIPartyManagement(){
			if(partyManagement == null){
				try {
					partyManagement = ServiceLocator.lookupService(IPartyManagement.class);
				} catch (NamingException e) {
					throw new CommunicationException(e);
				}
			}
			return partyManagement;
			
		}


		@Override
		public boolean onCancel(AjaxRequestTarget target) {
			// TODO Auto-generated method stub
			return false;
		}
		
	}

