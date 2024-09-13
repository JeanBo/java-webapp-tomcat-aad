package za.co.liberty.web.pages.advancedPractice;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.naming.NamingException;

import org.apache.commons.lang.SerializationUtils;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow.MaskType;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.Panel;

import za.co.liberty.business.guicontrollers.IContextManagement;
import za.co.liberty.business.guicontrollers.advancedPractice.IAdvancedPracticeGUIController;
import za.co.liberty.business.request.handlers.guirequest.CreateAgreementGuiRequestHandler;
import za.co.liberty.dto.advancedPractice.AdvancedPracticeDTO;
import za.co.liberty.dto.advancedPractice.AdvancedPracticeDTOGrid;
import za.co.liberty.dto.advancedPractice.AdvancedPracticeManagerDTO;
import za.co.liberty.dto.advancedPractice.AdvancedPracticeMemberDTO;
import za.co.liberty.dto.agreement.AgreementDTO;
import za.co.liberty.dto.contracting.ResultPartyDTO;
import za.co.liberty.dto.databaseenum.PartyStatusDBEnumDTO;
import za.co.liberty.dto.databaseenum.PartyStatusDBEnumDTO.PartyStatus;
import za.co.liberty.dto.userprofiles.ContextDTO;
import za.co.liberty.dto.userprofiles.ContextPartyDTO;
import za.co.liberty.dto.userprofiles.ISessionUserProfile;
import za.co.liberty.exceptions.data.DataNotFoundException;
import za.co.liberty.exceptions.data.ValidationException;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.exceptions.security.TabAccessException;
import za.co.liberty.helpers.lookup.ServiceLocator;
import za.co.liberty.interfaces.agreements.requests.RequestKindType;
import za.co.liberty.interfaces.party.PartyType;
import za.co.liberty.srs.type.SRSType;
import za.co.liberty.web.data.enums.ContextType;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.pages.Logon;
import za.co.liberty.web.pages.MaintenanceBasePage;
import za.co.liberty.web.pages.advancedPractice.model.AdvancedPracticePanelModel;
import za.co.liberty.web.pages.advancedPractice.model.MaintainAdvancedPracticePageModel;
import za.co.liberty.web.pages.interfaces.IMaintenanceParent;
import za.co.liberty.web.pages.maintainagreement.MaintainAgreementPage;
import za.co.liberty.web.pages.maintainagreement.model.MaintainAgreementPageModel;
import za.co.liberty.web.pages.panels.BaseModificationButtonsPanel;
import za.co.liberty.web.system.EJBReferences;
import za.co.liberty.web.system.SRSAuthWebSession;
import za.co.liberty.web.wicket.modal.SRSModalWindow;

public class MaintainAdvancedPracticePage extends MaintenanceBasePage<Integer>{
	private static final long serialVersionUID = 1L;

	private MaintainAdvancedPracticePageModel pageModel;
	
	private AgreementDTO agreementDTO ;

	private String pageName = "Maintain Advanced Practice Page";
	
	private SRSModalWindow addNewWizzardWindow;

	
	private transient IAdvancedPracticeGUIController guiController;

	private ContextPartyDTO partyContext;
	
	private boolean modelIntialised ;
	
	protected IMaintenanceParent parent;
	
	/**
	 * 
	 */
	public MaintainAdvancedPracticePage() {
		this(null,null);
	}
	
	public MaintainAdvancedPracticePage(Object obj) {
		this(obj,null);
	}	
		

	/**
	 * @param obj
	 */
	public MaintainAdvancedPracticePage(Object obj, Integer currentTab) {
		super(obj,currentTab);		
		
		Form f = new Form("wizardForm") {

			@Override
			protected void onSubmit() {
				if (logger.isDebugEnabled())
					logger.debug("wizardForm.submit.1");
				super.onSubmit();
				if (logger.isDebugEnabled())
					logger.debug("wizardForm.submit.2");
			}
			
		};
		f.add(addNewWizzardWindow = createModalWindow("addNewWizzardWindow"));
		this.add(f);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see za.co.liberty.web.pages.MaintenanceBasePage#createContainerPanel()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Panel createContainerPanel() {
		Panel panel = null;
		try {		
			panel = new MaintainAdvancedPracticePanel(CONTAINER_PANEL_NAME, pageModel,
					getEditState(), null, this.getFeedbackPanel(),
					this);
			
//			panel = new AdvancedPracticePanel(CONTAINER_PANEL_NAME,pageModel,
//					  this.getEditState(),this);
			
		} catch (TabAccessException e) {			
			//display message that all tabs have been disabled
			error(e.getUserMessage());
			panel = new EmptyPanel(CONTAINER_PANEL_NAME);
		}		
		panel.setOutputMarkupId(true);
		return panel;
	}
   /**
	 * Create the modal window
	 * 
	 * @param id
	 * @return
	 */
	private SRSModalWindow createModalWindow(String id) {		
		final SRSModalWindow window = new SRSModalWindow(id) {

			@Override
			public String getModalSessionIdentifier() {
				return "ADVANCED.PRACTICE.ADD-";
			}
			
		};
		window.setTitle("Add New Advanced Practice");				
		// Create the page
		window.setPageCreator(new ModalWindow.PageCreator() {
			private static final long serialVersionUID = 1L;
			public Page createPage() {	
				MaintainAdvancedPracticePageModel model = new MaintainAdvancedPracticePageModel();
				if(modelIntialised){
					//model = new MaintainAdvancedPracticePageModel();
					initialisePageModelForGuiRequest(null,model );
				}
				return new AddAdvancedPracticeWizardPage(window,model);
			}			
		});		
		
		window.setWindowClosedCallback(new ModalWindow.WindowClosedCallback() {
			private static final long serialVersionUID = 1L;			
			public void onClose(AjaxRequestTarget target) {
				if (getLogger().isDebugEnabled())
					getLogger().debug("MaintainAdvancedPracticePage.add.setWindowClosedCallback.onClose   ");
				
				MaintainAdvancedPracticePageModel model = (MaintainAdvancedPracticePageModel) window.getSessionModelForPage();
				
				/**
				 * Now we do the stuff that would usually be done here.
				 */				
				if (model.isModalWizardSucces()) {
					if (getLogger().isDebugEnabled())
						getLogger().debug("  Reloading Page!!!   ");
					
					getSession().info("Add Advanced Practice Request was raised and needs to be authorised before it can be viewed.");
					target.add(feedbackPanel);
				} else {
					feedbackPanel.info("Cancelled"
							+ " Add Advanced Practice");
					target.add(feedbackPanel);
				}
				
				window.clearModalPageModelInSession();
			}			
		});
		
		

		// Initialise window settings
		window.setCookieName("AdvancedPractice.add.wizard1122");
		window.setMinimalHeight(600);
		window.setInitialHeight(600);
		window.setMinimalWidth(750);
		window.setInitialWidth(750);
		window.setMaskType(MaskType.SEMI_TRANSPARENT);
		window.setCssClassName(ModalWindow.CSS_CLASS_GRAY);	
		window.setOutputMarkupId(true);
		window.setOutputMarkupPlaceholderTag(true);
		return window;
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
		return new BaseModificationButtonsPanel<AdvancedPracticeDTO>(
				SELECTION_PANEL_NAME, pageModel, this, containerForm,
				AdvancedPracticeDTO.class,this.getFeedbackPanel(), true,true,true,false,false,false) {

			private static final long serialVersionUID = 1L;

			@Override
			public void resetSelection() {
				
			}			

			@SuppressWarnings("unchecked")
			@Override
			public void doModify_onSubmit(AjaxRequestTarget target, Form form) {
				MaintainAdvancedPracticePageModel thisPageModel = 
					MaintainAdvancedPracticePage.this.pageModel;
				thisPageModel.getAdvancedPracticeDTO().getStatus();
				PartyStatusDBEnumDTO parcticeStatus = thisPageModel != null ? (thisPageModel.getAdvancedPracticeDTO() != null ? thisPageModel.getAdvancedPracticeDTO().getStatus(): null): null;
				if(parcticeStatus != null && parcticeStatus.getName().equals(PartyStatus.TERMINATED.getName())){
					
					MaintainAdvancedPracticePage.this.error("This practice is currently in terminated mode you may not modify it");
					if(getFeedbackPanel() != null){
						target.add(getFeedbackPanel());
					}
					return;
				}

				
				/**
				 * Check the security to ensure you can raise all the 
				 * requests first otherwise 
				 * fail with error
				 */
				ISessionUserProfile sessionUser = SRSAuthWebSession.get().getSessionUser();
				List <RequestKindType> requestsForAdd = new ArrayList<RequestKindType>();
				requestsForAdd.add(RequestKindType.MaintainAdvancedPractice);
				for (RequestKindType requestKind : requestsForAdd) {
					if (!sessionUser.isAllowRaise(requestKind)) {
						MaintainAdvancedPracticePage.this.error("You do not have the required access to add an Advanced Practice as " +
								"you are not allowed to raise the request [" + requestKind.getDescription() +"]");
						target.add(getFeedbackPanel());
						return;
					}
				}
				
				
				
				super.doModify_onSubmit(target, form);
			}
		
		
		
			/**
			 * Called when Add new is submitted. Notify parent and 
			 * swap panels.  Ensure that selected item is set before calling.
			 * 
			 * @param target
			 * @param form
			 */
			@SuppressWarnings("unchecked")
			public void doAddNew_onSubmit(AjaxRequestTarget target, Form form) {							
				//add new is different here as we will display a popup wizzard and users will not input anything into the current panel
				//first we check access to this function, user might have buttons enabled, code mistake, but should not be allowed to add
			
				
				ISessionUserProfile sessionUser = SRSAuthWebSession.get().getSessionUser();
				List <RequestKindType> requestsForAdd = new ArrayList<RequestKindType>();
				requestsForAdd.add(RequestKindType.MaintainAdvancedPractice);
				for (RequestKindType requestKind : requestsForAdd) {
					if (!sessionUser.isAllowRaise(requestKind)) {
						MaintainAdvancedPracticePage.this.error("You do not have the required access to add an Advanced Practice.");
						target.add(getFeedbackPanel());
						return;
					}else{
						addNewWizzardWindow.show(target);	
					}
				}
//				if(parent.hasAddAccess()){
//					//popup wizzard
//					addNewWizzardWindow.show(target);	
//				}else{
//					//display error
//					MaintainAdvancedPracticePage.this.error("You do not have the required access to add an Advanced Practice.");
//					target.addComponent(getFeedbackPanel());
//					return;
//				}
			}};
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


	/**
	 * Gets the IAgreementPrivilegesController interface for calls to the
	 * AdvancedPracticeGUIController session bean
	 * 
	 * @return
	 */
	protected IAdvancedPracticeGUIController getAdvancedPracticeGUIController(){
		if(guiController == null){
			try {
				guiController = ServiceLocator.lookupService(IAdvancedPracticeGUIController.class);
			} catch (NamingException e) {
				throw new CommunicationException(e);
			}
		}
		return guiController;
	}

	@Override
	public ContextType getContextTypeRequired() {
		return ContextType.PARTY_ORGANISATION_PRACTICE;
	}
	
	
	

     
	@Override
	public void doSave_onSubmit() {
		save(true);
        
	}
	

	private void save(boolean reactivate){
		ISessionUserProfile userProfile = SRSAuthWebSession.get().getSessionUser();		
		IAdvancedPracticeGUIController controller = getAdvancedPracticeGUIController();	
		
		try {
			Long partyOidContext = null;			
			
			
				partyOidContext = partyContext.getPartyOid();
				if (partyContext.getTypeOid() != PartyType.ADVANCEDPRACTICE.getType()){
					
				}
				AdvancedPracticePanelModel panelModel =  pageModel.getPanelModel();
			

				 if((pageModel.getCurrentTabClass() == AdvancedPracticePanel.class) && panelModel != null && !panelModel.getIsExistingMaintenanceRequest()){
						if(getEditState() == EditStateType.MODIFY){
						 	
						 	try{
						 		controller.raiseAdvancedPracticeRequest(userProfile, pageModel.getPanelModel().getAdvancedPracticeDTO(),pageModel.getAdvancedPracticeDTOBeforeImage());
						 		AdvancedPracticePanelModel advancedPracticePanelModel = null;
						 		
						 		advancedPracticePanelModel = pageModel.getPanelModel();
						 		AdvancedPracticeDTO practiceDTO = pageModel.getAdvancedPracticeDTOBeforeImage();
						 		advancedPracticePanelModel.setAdvancedPracticeDTO(practiceDTO);	
						 		pageModel.setPracticeDTO(practiceDTO);
						 		pageModel.setPanelModel(advancedPracticePanelModel);
						 	}catch(ValidationException e){
								for(String error : e.getErrorMessages()){
									error(error);
								}
								super.add(getFeedbackPanel());
								return;
							}
						}
				 }else{
					 
						//reload context as details have changed			
						try{
							ContextDTO newContextDTO = SRSAuthWebSession.get().getContextDTO().clone();					
							IContextManagement contextBean = ServiceLocator.lookupService(IContextManagement.class);
							
							//First we check if the practice has ended, if it has we load user into context
							boolean ended = true;
							boolean found = false;				
							if(pageModel.getAdvancedPracticeDTO().getEffectiveTo() == null || !pageModel.getAdvancedPracticeDTO().getEffectiveTo().before(new Date())){
								//Practice has not ended
								ended = false;
								List<ResultPartyDTO> parties = getAdvancedPracticeGUIController().findPartyWithOrganisationName(pageModel.getAdvancedPracticeDTO().getName());
								for(ResultPartyDTO party : parties){				
									if(party.getName().equalsIgnoreCase(pageModel.getAdvancedPracticeDTO().getName())){
										ContextDTO dto = contextBean.getContext(party);
										newContextDTO.setPartyContextDTO(dto.getPartyContextDTO());
										newContextDTO.setAgreementContextDTO(dto.getAgreementContextDTO());							
										found = true;
										break;
									}						
								}
							}
							//if practice has not been found or has ended then we put user in context
							if(ended || !found){
								long partyoid = SRSAuthWebSession.get().getSessionUser().getPartyOid();
								if(partyoid < 1){
									setResponsePage(new Logon());
									return;
								}
								else{
									//set back to user context
									newContextDTO = SRSAuthWebSession.get().getCurrentUserContextDTO();
								}
							}
							if(!ended && !found){
								getSession().warn("Practice stored but could not be put into the context");
							}
							if(ended){
								getSession().info("User set back in context as node is not active anymore");
							}
							SRSAuthWebSession.get().setContextDTO(newContextDTO);				
						} catch (CloneNotSupportedException e) {
							e.printStackTrace();
							getSession().warn("Practice stored but could not be put into the context");
						} catch (DataNotFoundException e) {
							e.printStackTrace();
							getSession().warn("Practice stored but could not be put into the context");
						}
					 
				 }


			
			invalidatePage();		
			if(getEditState() == EditStateType.MODIFY){
				getSession().info("Record was saved succesfully");	
			}			
			else if(getEditState() == EditStateType.TERMINATE){
				getSession().info("Record was saved succesfully");	
			}
			
			setResponsePage(new MaintainAdvancedPracticePage(pageModel));
		} catch (Exception e) {
			error(e);
		}
//		catch (ValidationException e) {
//			for(String error : e.getErrorMessages()){
//				error(error);
//			}			
//		}	
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see za.co.liberty.web.pages.MaintenanceBasePage#initialisePageModel(java.lang.Object)
	 */
	@Override
	public Object initialisePageModel(Object obj, Integer currentTab) {	
		MaintainAdvancedPracticePageModel model = new MaintainAdvancedPracticePageModel();
		
		AdvancedPracticeDTO advancedPracticeDTO = getBlankAdvancedPracticeDTO();

		model.setAdvancedPracticeDTO(advancedPracticeDTO);
				

		if(pageContextDTO != null){
			 partyContext = pageContextDTO.getPartyContextDTO();		
			if (partyContext == null || partyContext.getPartyOid() == 0 || partyContext.getTypeOid()!= SRSType.ADVANCEDPRACTICE) {
				error("There is no practice selected in the context");
			}else if (partyContext.getTypeOid() != PartyType.ADVANCEDPRACTICE.getType()){
				initialisePageModelForGuiRequest(obj,model);
				error("Please select a practice in the context");
				
			}else if (partyContext.getTypeOid() == PartyType.ADVANCEDPRACTICE.getType()){ 
				if (obj != null) {
					model = (MaintainAdvancedPracticePageModel) obj;
				} else {

					IAdvancedPracticeGUIController controller = getAdvancedPracticeGUIController();
					long practiceID = partyContext.getPartyOid();
					
					try {
						
						AdvancedPracticeDTO practiceDTO = null;
						Object object = controller.getAdvancedPracticeDTO(partyContext.getPartyOid());
						
						if( object != null && object instanceof AdvancedPracticeDTO){
							practiceDTO = (AdvancedPracticeDTO)object;
						}
						
						initialisePageModelForGuiRequest(practiceDTO, model);
						
						
					} catch (Exception e) {
						error("Could not retreive party details using key " + practiceID + ", Please contact support if you beleive you should not be seeing this message.");
						model.setAdvancedPracticeDTO(advancedPracticeDTO);
					}
					model.setUacfID(pageContextDTO.getPartyContextDTO().getUacfID());	
					if(currentTab != null){
						model.setCurrentTab(currentTab);
					}
				}
			}
		}
		pageModel = model;	
		modelIntialised = true;
		return pageModel;
	}
	

	/**
	 * Create  blank AdvancedPracticeDTO
	 * @return AdvancedPracticeDTO
	 */
	private AdvancedPracticeDTO getBlankAdvancedPracticeDTO(){
		AdvancedPracticeDTO advancedPracticeDTO = new AdvancedPracticeDTO ();
		advancedPracticeDTO.setAdvancedPracticeManagerDTOlist(new ArrayList<AdvancedPracticeManagerDTO>());
		advancedPracticeDTO.setAdvancedPracticeMemberDTOList(new ArrayList<AdvancedPracticeMemberDTO>());
		
		return advancedPracticeDTO;
	}
	
	
	
	
	
	
	
	@Override
	public void swapSelectionPanel(AjaxRequestTarget target) {
		
		/**
		 * Call super method to swap panel
		 */
		super.swapSelectionPanel(target);
		
		
	}


	
	/**
	 * Enables the testing of GuiRequests by accepting the PARTYDTO that is
	 * sent via the constructor.
	 * 
	 * @param party
	 * @param model
	 * @param controller
	 * @return
	 */
	@SuppressWarnings("unused")
	private Object initialisePageModelForGuiRequest( Object obj, 
			MaintainAdvancedPracticePageModel model) {

		AdvancedPracticeDTO practiceDTO = obj != null ? (AdvancedPracticeDTO)obj : getBlankAdvancedPracticeDTO();
	
		
		if(model.getAdvancedPracticeDTOBeforeImage() != null){
			practiceDTO = model.getAdvancedPracticeDTOBeforeImage();
		}
		model.setAdvancedPracticeDTO(practiceDTO);		
		
		model.setAdvancedPracticeDTO(practiceDTO);
		AdvancedPracticePanelModel panelModel = null;
		
		if(model.getPanelModel() == null){
			panelModel  = new AdvancedPracticePanelModel(model);
			panelModel.setManagersGrids(new ArrayList<AdvancedPracticeDTOGrid>());
			panelModel.setMembersGrid(new ArrayList<AdvancedPracticeDTOGrid>());
		}else{
			panelModel = model.getPanelModel();
		}
		

		model.setSelectedItem(practiceDTO);
		panelModel.setAdvancedPracticeDTO(practiceDTO);
		panelModel.setPracticeId(partyContext.getPartyOid());
		panelModel.setExistingMaintenanceRequest(false);
		
		model.setAdvancedPracticeDTOBeforeImage((AdvancedPracticeDTO) SerializationUtils.clone(practiceDTO));	
		
		model.setPanelModel(panelModel);
		
		
		return model;
	}

	@Override
	public boolean hasAddAccess(Object callingObject) {
		
		return super.hasAddAccess(callingObject);
	}

	@Override
	public boolean hasModifyAccess(Object callingObject) {
		// TODO Auto-generated method stub
		return super.hasModifyAccess(callingObject);
	}

}
