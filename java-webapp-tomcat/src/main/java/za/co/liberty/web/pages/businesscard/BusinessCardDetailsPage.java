package za.co.liberty.web.pages.businesscard;

import java.util.List;

import javax.naming.NamingException;

import org.apache.commons.lang.SerializationUtils;
import org.apache.log4j.Logger;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.extensions.markup.html.tabs.TabbedPanel;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import za.co.liberty.business.guicontrollers.IContextManagement;
import za.co.liberty.business.guicontrollers.businesscard.IBusinessCardGuiController;
import za.co.liberty.business.guicontrollers.partymaintenance.IPartyMaintenanceController;
import za.co.liberty.dto.businesscard.BusinessCardDetailsDTO;
import za.co.liberty.dto.contracting.ResultAgreementDTO;
import za.co.liberty.dto.contracting.ResultPartyDTO;
import za.co.liberty.dto.party.PersonDTO;
import za.co.liberty.dto.userprofiles.ContextAgreementDTO;
import za.co.liberty.dto.userprofiles.ContextDTO;
import za.co.liberty.dto.userprofiles.ContextPartyDTO;
import za.co.liberty.dto.userprofiles.ISessionUserProfile;
import za.co.liberty.exceptions.data.DataNotFoundException;
import za.co.liberty.exceptions.data.ValidationException;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.exceptions.security.TabAccessException;
import za.co.liberty.helpers.lookup.ServiceLocator;
import za.co.liberty.srs.type.SRSType;
import za.co.liberty.web.data.enums.ContextType;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.pages.DynamicContextPanel;
import za.co.liberty.web.pages.MaintenanceBasePage;
import za.co.liberty.web.pages.businesscard.model.BusinessCardPageModel;
import za.co.liberty.web.pages.businesscard.model.MaintainBusinessCardPanelModel;
import za.co.liberty.web.pages.interfaces.IMaintenanceParent;
import za.co.liberty.web.pages.panels.BaseModificationButtonsPanel;
import za.co.liberty.web.pages.panels.MaintenanceTabbedPanel;
import za.co.liberty.web.pages.party.model.MaintainPartyPageModel;
import za.co.liberty.web.system.SRSAuthWebSession;
import za.co.liberty.web.wicket.markup.html.tabs.StatefullCachingTab;

/**
 * A new page for the business card details
 * @author DZS2610
 *
 */
public class BusinessCardDetailsPage extends MaintenanceBasePage<PageParameters>{

	private static final long serialVersionUID = 1L;

	private BusinessCardPageModel pageModel;

	private String pageName = "Business Card";	
	
	private transient IBusinessCardGuiController businessCardGuiController;
	
	//private PageParameters passedInParams;
	
	private static final Logger logger = Logger.getLogger(BusinessCardDetailsPage.class);
		
	
	private BusinessCardDetailsPage(BusinessCardPageModel pageModel, PageParameters parameters) {
		super(true,pageModel,parameters);
	}
	

	
	/**
	 * Constructor called via blueprintonline
	 * @param parameters
	 */
	public BusinessCardDetailsPage(PageParameters parameters) {	
		super(true,null,parameters);		
		//while a user is in a session that came from another system to business card, the user should not be able to continue to use SRS menus
		SRSAuthWebSession.get().setMenuItemsDisabledForUser(true);				
		String consultantCode = parameters.get("consultantCode").toString();
		String signedOnUACFID = parameters.get("signedOnUACFID").toString();	
		//disable the menu bar
		logger.info("Business card called via external system by user " + signedOnUACFID + " for consultant code " + consultantCode);
		//put the agreement into context		
		ResultAgreementDTO agmt;
		try {
			agmt = getBusinessCardGuiController().findAgreementWithConsCode(consultantCode);		
//			check if user is actually allowed to view this business card
			if(this.getSecurityManagement().canUserViewAgreementDetails(agmt.getAgreementNumber(), agmt.getHasHomePartyOid(), SRSAuthWebSession.get().getSessionUser())){			
				ResultPartyDTO party = getBusinessCardGuiController().findPartyWithAgreementNumber(agmt.getOid());
				ContextDTO newContextDTO = SRSAuthWebSession.get().getContextDTO();			
				IContextManagement contextBean;
				try {
					contextBean = ServiceLocator.lookupService(IContextManagement.class);
				} catch (NamingException e) {
					throw new CommunicationException(e);
				}
				newContextDTO = contextBean.getContext(party,agmt);				
				SRSAuthWebSession.get().setContextDTO(newContextDTO);
				pageContextDTO = newContextDTO;
				//now that we have set the context, refresh the page
				initialisePageModel(null, parameters);
				
				getSession().getFeedbackMessages().clear();
				setResponsePage(new BusinessCardDetailsPage(pageModel,parameters));
			}else{
				error("You may not view consultant " + agmt.getConsultantCodeFormatted() + "'s businesscard due to rule restrictions");
			}
				
		} catch (NumberFormatException e) {
			error("Could not convert consultant code " + consultantCode + " into a number");			
		} catch (DataNotFoundException e) {
			error("Could not find any agreement with consultant code " + consultantCode);			
		}
//		FeedbackMessage message = this.getFeedbackMessages()
//		if(message != null && message.getMessage() != null && !message.getMessage().toString().equals("")){
		
		// Unsure of what they are doing here, assume its checking for a message.
		if (!this.getFeedbackMessages().isEmpty()) {
			//error reported, clear the parameters
			parameters.clearIndexed();
			parameters.clearNamed();
			ContextAgreementDTO agmtContext = pageContextDTO.getAgreementContextDTO();			
			parameters.add("consultantCode",agmtContext.getConsultantCode());
			parameters.add("signedOnUACFID",signedOnUACFID);
		}
	}	
	

	/**
	 * gets the data needed for this panel
	 *
	 */
	protected void initPanelModel(){
		if(pageModel.getMaintainBusinessCardPanelModel() != null
				&& pageModel.getMaintainBusinessCardPanelModel().getBusinessCardDetails() != null){
			
			if (getEditState()==EditStateType.AUTHORISE && 
					pageModel.getMaintainBusinessCardPanelModel().getAllSpokenLanguages()==null) {
				// Initialise spoken languages for auth view
				pageModel.getMaintainBusinessCardPanelModel().setAllSpokenLanguages(
						getBusinessCardGuiController().getAllSpokenLanguagesList());
			}
			//model already exists			
			return;
		}		
		MaintainBusinessCardPanelModel panelModel = new MaintainBusinessCardPanelModel();			
		if(pageModel.getPartyOID() != null){
//			get the partyDTO and set into the model
			try {
				Long agreementNumber = pageModel.getAgreementNumber();
				if (agreementNumber==null)
				{
					agreementNumber = new Long(0);
				}
				BusinessCardDetailsDTO bcDetails = getBusinessCardGuiController().getBusinessCardDetailsDTO(pageModel.getPartyOID(),agreementNumber);
				//before we clone, we insert data from the external system if it exists
				if(pageModel.getPassedInParams() != null){
					PageParameters parameters = pageModel.getPassedInParams();											
					bcDetails.setConsultantUACFID(parameters.get("consultantUACFID").toString());
					bcDetails.setPresentedByInitials(parameters.get("currentPBInitials").toString());
					bcDetails.setPresentedByName(parameters.get("currentPBName").toString());
					bcDetails.setPresentedBySurname(parameters.get("currentPBSurname").toString());
					bcDetails.setPromotionalDetails(parameters.get("promoDetails").toString());
					bcDetails.setReferenceNo(parameters.get("referenceNo").toString());
					bcDetails.setWTDID(parameters.get("currentWTDID").toString());
				}				
				BusinessCardDetailsDTO beforeImage = (BusinessCardDetailsDTO)SerializationUtils.clone(bcDetails);
				panelModel.setBusinessCardDetails(bcDetails);
				panelModel.setBeforeImage(beforeImage);
				
				/*
				 * Only show Find an FA for individuals intermediaries
				 */
				if (pageModel.getAgreementNumber()!=null  
						&& bcDetails.getParty() instanceof PersonDTO) {
					getBusinessCardGuiController().initialiseFindAnFAModel(panelModel, pageModel.getPartyOID(), 
							pageModel.getAgreementNumber(), bcDetails);
				}
			} catch (DataNotFoundException e) {
				//party could not be found
				error("Party details could not be found");
			}			
		}else{
			panelModel.setBusinessCardDetails(new BusinessCardDetailsDTO());
		}		
		pageModel.setMaintainBusinessCardPanelModel(panelModel);
	}
	
	@Override
	protected void onBeforeRender() {
		logger.info("onRender");
		//disable the context panel before rendering if page params were sent in		
		if(pageModel.getPassedInParams() != null && contextPanel != null && contextPanel instanceof DynamicContextPanel){
			((DynamicContextPanel)contextPanel).disableContext(null);
		}
		super.onBeforeRender();
	}

	
	/**
	 * 
	 */
	public BusinessCardDetailsPage() {
		super(null);		
	}

	/**
	 * @param obj
	 */
	public BusinessCardDetailsPage(Object obj) {
		super(obj);
	}

	
	/*
	 * (non-Javadoc)
	 * 
	 * @see za.co.liberty.web.pages.MaintenanceBasePage#createContainerPanel()
	 */
	@Override
	public Panel createContainerPanel() {

		try {
			Panel panel = new MaintenanceTabbedPanel (CONTAINER_PANEL_NAME, (BusinessCardPageModel)pageModel, 
						getEditState(), new int[] {}, (IMaintenanceParent)this) {

				private static final long serialVersionUID = 1L;			
					
				/* (non-Javadoc)
				 * @see za.co.liberty.web.pages.panels.MaintenanceTabbedPanel#initialiseTabs(java.util.List)
				 */
				@Override
				public void initialiseTabs(List<AbstractTab> tabList) {
					tabList.add(new StatefullCachingTab(new Model<String>("Business Card Details"),	this) {
							private static final long serialVersionUID = 1L;
							
							@Override
							public Panel createPanel(String id) {				
								
								Panel panel = new BusinessCardDetailsPanel(
										TabbedPanel.TAB_PANEL_ID, (BusinessCardPageModel)pageModel,
										getEditState(),getFeedbackPanel(), BusinessCardDetailsPage.this);
								panel.setOutputMarkupId(true);
								return panel;
							}		
						});
					
					/*
					 * Find an FA panel only show for tied or certain brokers.
					 */
					final BusinessCardPageModel model = (BusinessCardPageModel)pageModel;
					System.out.println("model="+model);
					if (model!=null) {
						System.out.println("businesscard model="+model.getMaintainBusinessCardPanelModel());
					}
//					if (model!=null && model.getMaintainBusinessCardPanelModel()!=null
//							&& model.getMaintainBusinessCardPanelModel().isShowFindAnFAPanel()) {
					
					
					
					/*
					 * Only show Find an FA for individuals intermediaries
					 */
					if (model.getAgreementNumber()!=null  && model.getMaintainBusinessCardPanelModel().getBusinessCardDetails().getParty() instanceof PersonDTO) {
												
							tabList.add(new StatefullCachingTab(new Model<String>("Find an FA"),	this) {
							private static final long serialVersionUID = 1L;
							
							@Override
							public Panel createPanel(String id) {				
								
								Panel panel = new BusinessCardFindAnFADetailsPanel(
										TabbedPanel.TAB_PANEL_ID, (BusinessCardPageModel)pageModel,
										getEditState(),getFeedbackPanel(), BusinessCardDetailsPage.this);
								panel.setOutputMarkupId(true);
								return panel;
							}		
						});
							
					}	
					/*if (model.getAgreementNumber()!=null  && model.getMaintainBusinessCardPanelModel().getBusinessCardDetails().getParty() instanceof PersonDTO) {
						
						tabList.add(new StatefullCachingTab(new Model<String>("Other Linked Parties"),	this) {
						private static final long serialVersionUID = 1L;
						
						@Override
						public Panel createPanel(String id) {				
							Panel panel = new BusinessCardOtherLinkedDetailsPanel(TabbedPanel.TAB_PANEL_ID, (BusinessCardPageModel)pageModel, getEditState(),getFeedbackPanel(), BusinessCardDetailsPage.this);
							panel.setOutputMarkupId(true);
							return panel;
						}		
					});*/
						
				//}
				}
				
				@Override
				public boolean isLockTabsForModify() {
					// Don't lock the tabs for modifying.
					return false;
				}
				
			};
			panel.setOutputMarkupId(true);
			return panel;
		} catch (TabAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new EmptyPanel(CONTAINER_PANEL_NAME);
		}		
		
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see za.co.liberty.web.pages.MaintenanceBasePage#createNavigationalButtons()
	 */
	@Override
	public Button[] createNavigationalButtons() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see za.co.liberty.web.pages.MaintenanceBasePage#createSelectionPanel()
	 */
	@Override
	public Panel createSelectionPanel() {
		//return null;
		return new BaseModificationButtonsPanel(
				SELECTION_PANEL_NAME, pageModel, this,containerForm ,
				BusinessCardDetailsDTO.class,this.getFeedbackPanel(), false,true,true,false,false,false){
				private static final long serialVersionUID = 1L;
				@Override
				public void resetSelection() {
					
				}
				
				@Override
				public void doModify_onSubmit(AjaxRequestTarget target, Form form) {
					//first check that an agreement is in context if the agreement list is not empty
					if(pageContextDTO.getAllAgreementsList() != null && pageContextDTO.getAllAgreementsList().size() > 0
							&& (pageContextDTO.getAgreementContextDTO() == null || pageContextDTO.getAgreementContextDTO().getAgreementNumber() == null)){
						//new rule is that a user must select a context agreement for use with the party request and broadcast
						error("Please select an agreement into the context for use as the request agreement, this will also be the agreement that will be broadcast");					
						if(target != null && getFeedbackPanel() != null){
							target.add(getFeedbackPanel());
						}
						return;
					}
					super.doModify_onSubmit(target, form);
				}
				
				@Override
				protected void do_cancel() {
					parent.invalidatePage();
					if(BusinessCardDetailsPage.this.pageModel.getPassedInParams() != null){
						setResponsePage(new BusinessCardDetailsPage(BusinessCardDetailsPage.this.pageModel.getPassedInParams()));
					}else{
						setResponsePage(BusinessCardDetailsPage.class);	
					}
				}					
			};
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see za.co.liberty.web.pages.MaintenanceBasePage#initialisePageModel(java.lang.Object)
	 */
	@Override
	public Object initialisePageModel(Object obj, PageParameters pageParams) {	
		ContextPartyDTO partydto = pageContextDTO.getPartyContextDTO();	
		ContextAgreementDTO agmtContext = pageContextDTO.getAgreementContextDTO();
		List<ResultAgreementDTO> agmts = pageContextDTO.getAllAgreementsList();		
		boolean blankDisplay = false;//removes all traces of the context from the model
		if(!((agmtContext != null && agmtContext.getAgreementNumber() != null) || (agmts != null && agmts.size() != 0))){
			error("Only intermediaries can use this screen, for standalone parties go to the party screen");	
			blankDisplay = true;
		}
		BusinessCardPageModel model = new BusinessCardPageModel();
		model.setPassedInParams(pageParams);
		if(pageContextDTO != null){				
			if (partydto == null || partydto.getPartyOid() == 0 || partydto.getTypeOid()== SRSType.ADVANCEDPRACTICE) {
				error("There is no party selected in the context");
			}else if(partydto.getTypeOid() == SRSType.DIVISION || partydto.getTypeOid() == SRSType.REGION ||
					partydto.getTypeOid() == SRSType.SUBREGION || partydto.getTypeOid() == SRSType.BRANCH || partydto.getTypeOid() == SRSType.UNIT){
					error("Please go to the hierachy option to maintain hierarchy nodes, or search for another person ");
			}else{
				if (obj != null) {
					model = (BusinessCardPageModel) obj;
				} else if(!blankDisplay){
					model.setPartyOID(partydto.getPartyOid());
					model.setAgreementNumber(
							(agmtContext != null) ? agmtContext.getAgreementNumber() : null);					
				}
			}			
		}
		pageModel = model;
		initPanelModel();
		return pageModel;
	}

	
	/**
	 * Update the passed in params from the data in the DTO
	 * @param data
	 */
	private void updateParamsFromDTO(BusinessCardDetailsDTO data){	
		if(pageModel.getPassedInParams() == null){
			return;
		}
		PageParameters passedInParams = pageModel.getPassedInParams();
		passedInParams.add("consultantUACFID",data.getConsultantUACFID());
		passedInParams.add("currentWTDID",data.getWTDID());
		passedInParams.add("currentPBName",data.getPresentedByName());
		passedInParams.add("currentPBInitials",data.getPresentedByInitials());
		passedInParams.add("currentPBSurname",data.getPresentedBySurname());
		passedInParams.add("promoDetails",data.getPromotionalDetails());
		passedInParams.add("referenceNo",data.getReferenceNo());		
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see za.co.liberty.web.pages.BasePage#getPageName()
	 */
	@Override
	public String getPageName() {
		return pageName;
	}


	@Override
	public ContextType getContextTypeRequired() {
		return ContextType.AGREEMENT;
	}
	
	

	@Override
	public void doSave_onSubmit() {
		IPartyMaintenanceController controller = MaintainPartyPageModel.getPartyMaintenanceController();	
		try {
			ISessionUserProfile userProfile = SRSAuthWebSession.get().getSessionUser();
			Long agmtNoContext = null;			
			ContextAgreementDTO aggContext = pageContextDTO.getAgreementContextDTO();
			if(aggContext != null){
				agmtNoContext = aggContext.getAgreementNumber();
			}
		
			// Validate first (not on form submit as multi tab validation is problematic)
			getBusinessCardGuiController().validateBusinessCardDetails(
					pageModel.getMaintainBusinessCardPanelModel().getBusinessCardDetails());
			
			
			// Validate Business Card - Communication Preference Panel 
			if (pageModel.getMaintainBusinessCardPanelModel().getBusinessCardDetails() != null && pageModel.getMaintainBusinessCardPanelModel().getBusinessCardDetails().getParty() != null) {
				getBusinessCardGuiController().validateCommunicationPreferences(pageModel.getMaintainBusinessCardPanelModel().getBusinessCardDetails().getParty().getCommunicationPreferences());
			}
			
						
						
			//raise the business card requests
			getBusinessCardGuiController().raiseBusinessCardRequest(
					pageModel.getMaintainBusinessCardPanelModel().getBusinessCardDetails(),
					pageModel.getMaintainBusinessCardPanelModel().getBeforeImage(),
					userProfile,agmtNoContext,this.pageModel.getPartyOID());				
			updateParamsFromDTO(pageModel.getMaintainBusinessCardPanelModel().getBusinessCardDetails());
		} catch (ValidationException e) {
			for(String error : e.getErrorMessages()){
				error(error);
			}
			return;
		}		
		invalidatePage();		
		getSession().info("Record was saved successfully");
		if(pageModel.getPassedInParams() != null){
			setResponsePage(new BusinessCardDetailsPage(pageModel.getPassedInParams()));
		}else{
			setResponsePage(BusinessCardDetailsPage.class);
		}
	}
	
	/**
	 * Get the BusinessCardGuiController bean
	 * @return
	 */
	private IBusinessCardGuiController getBusinessCardGuiController(){
		if(businessCardGuiController == null){
			try {
				businessCardGuiController = ServiceLocator.lookupService(IBusinessCardGuiController.class);
			} catch (NamingException e) {
				throw new CommunicationException(e);
			}
		}
		return businessCardGuiController;
	}
}

