package za.co.liberty.web.pages.party;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.naming.NamingException;

import org.apache.commons.lang.SerializationUtils;
import org.apache.wicket.Page;
import org.apache.wicket.PageReference;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow.MaskType;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.Panel;

import za.co.liberty.business.guicontrollers.IContextManagement;
import za.co.liberty.business.guicontrollers.partymaintenance.IPartyMaintenanceController;
import za.co.liberty.business.party.IPartyManagement;
import za.co.liberty.dto.agreement.maintainagreement.ProvidentFundBeneficiariesDTO;
import za.co.liberty.dto.contracting.ResultPartyDTO;
import za.co.liberty.dto.party.EmployeeDTO;
import za.co.liberty.dto.party.HierarchyNodeDTO;
import za.co.liberty.dto.party.OrganisationDTO;
import za.co.liberty.dto.party.PartyDTO;
import za.co.liberty.dto.party.PartyRoleDTO;
import za.co.liberty.dto.party.PersonDTO;
import za.co.liberty.dto.party.maintainparty.MaintainPartyDTO;
import za.co.liberty.dto.userprofiles.ContextAgreementDTO;
import za.co.liberty.dto.userprofiles.ContextDTO;
import za.co.liberty.dto.userprofiles.ContextPartyDTO;
import za.co.liberty.dto.userprofiles.ISessionUserProfile;
import za.co.liberty.exceptions.broadcast.PartyDetailsBroadcastException;
import za.co.liberty.exceptions.data.DataNotFoundException;
import za.co.liberty.exceptions.data.ValidationException;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.exceptions.security.TabAccessException;
import za.co.liberty.helpers.lookup.ServiceLocator;
import za.co.liberty.srs.type.SRSType;
import za.co.liberty.web.data.enums.ContextType;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.data.enums.PanelToRequestMapping;
import za.co.liberty.web.data.pages.IMaintenancePageModel;
import za.co.liberty.web.data.pages.IModalMaintenancePageModel;
import za.co.liberty.web.pages.Logon;
import za.co.liberty.web.pages.MaintenanceBasePage;
import za.co.liberty.web.pages.panels.BaseModificationButtonsPanel;
import za.co.liberty.web.pages.party.model.MaintainPartyPageModel;
import za.co.liberty.web.pages.party.model.MedicalAidDetailsPanelModel;
import za.co.liberty.web.pages.party.model.ProvidentFundBeneficiariesModel;
import za.co.liberty.web.pages.taxdetails.TaxDetailsPanel;
import za.co.liberty.web.system.SRSAuthWebSession;
import za.co.liberty.web.wicket.modal.SRSModalWindow;

/**
 * This class represents the Maintain Party PAGE.
 * 
 * @author pks2802
 * 
 */
public class MaintainPartyPage extends MaintenanceBasePage<Integer>{

	private static final long serialVersionUID = 1L;

	private MaintainPartyPageModel pageModel;

	private String pageName = "Maintain Party";
	
	private SRSModalWindow window;
	
	private PageReference pageReference;
	
	private transient IPartyManagement partyManagement;

	/**
	 * 
	 */
	public MaintainPartyPage() {
		this(null,-1);
		//setEditState(EditStateType.MODIFY, null);
	}
	
	/**
	 * @param obj
	 */
	public MaintainPartyPage(Object obj) {		
		this(obj,null);
	}

	/**
	 * @param obj
	 */
	public MaintainPartyPage(Object obj, Integer currentTab) {
		super(obj,currentTab);
		this.add(window = createModalWindow("addNewWizzardWindow"));
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
				return "MaintainPartyPage.Add";
			}
			
		};
		window.setTitle("Add New Party");	
		// Create the page
		
		
		window.setPageCreator(new ModalWindow.PageCreator() {
			private static final long serialVersionUID = 1L;
			public Page createPage() {	
				EmployeeDTO newEmployee = new EmployeeDTO();
				newEmployee.setEffectiveFrom(new Date());
				AddPartyWizardPage p = new AddPartyWizardPage(window,newEmployee);
				pageReference = p.getPageReference();
				return p;
			}
		});		

		window.setWindowClosedCallback(new ModalWindow.WindowClosedCallback() {
			private static final long serialVersionUID = 1L;			
			public void onClose(AjaxRequestTarget target) {
				if (getLogger().isDebugEnabled())
					getLogger().debug("MaintainPartyPage.setWindowClosedCallback.onClose   ");
				
				Page parentPage = pageReference.getPage();
				System.out.println("Parent page fromTarget=" + parentPage
						+"\nFromPageReference=" + pageReference.getPage());

				MaintainPartyPageModel model = (MaintainPartyPageModel) window.getSessionModelForPage();
				
				testStuff(model);
				
				/**
				 * Now we do the stuff that would usually be done here.
				 */				
				if (model.isModalWizardSucces()) {
					if (getLogger().isDebugEnabled())
						getLogger().debug("  Reloading Page!!!   ");
					
					onAfterAddParty(target, model.getPartyDTO());
				} else {
					feedbackPanel.info("Cancelled Add Party");
					target.add(feedbackPanel);
				}
				window.clearModalPageModelInSession();
			}			
		});
		
		
		// Initialise window settings
		window.setMinimalHeight(600);
		window.setInitialHeight(600);
		window.setMinimalWidth(750);
		window.setInitialWidth(800);
		window.setMaskType(MaskType.SEMI_TRANSPARENT);
		window.setCssClassName(ModalWindow.CSS_CLASS_GRAY);			
		return window;
	}
	
	/**
	 * On After Add Party is called after the Add party wizard closes
	 * 
	 * @param target
	 * @param partyDTO
	 */
	public void onAfterAddParty(AjaxRequestTarget target, PartyDTO partyDTO) {
		
		try{
			if (getLogger().isDebugEnabled())
				getLogger().debug("AddPartyWizard.processClose  setContextParty=" +partyDTO.getOid());
			IContextManagement contextBean = ServiceLocator.lookupService(IContextManagement.class);

			boolean found = false;
			ResultPartyDTO updatedParty = MaintainPartyPageModel.getPartyManagement().findPartyWithObjectOid(partyDTO.getOid());
			if(updatedParty != null){
				ContextDTO dto = contextBean.getContext(updatedParty);			
				SRSAuthWebSession.get().setContextDTO(dto);
				found = true;
			}						
			if(!found){
				error("Party stored but could not be put into the context, this could be due to future dating");
				target.add(feedbackPanel);
			}				

		}
		catch (DataNotFoundException e) {
			getLogger().error("Stored party not found ", e);
			getSession().error("Party stored but could not be put into the context");
		} catch (NamingException e) {
			throw new CommunicationException(e); 
		}

		getSession().info("Party Stored successfully");
		setResponsePage(MaintainPartyPage.class);
	}
	
	/**
	 * Print out detail of the party model, for initial testing and will be removed.
	 * 
	 * @param obj
	 */
	public void testStuff(IModalMaintenancePageModel obj) {
		if (getLogger().isDebugEnabled())
			getLogger().debug("MaintainPartyPage.testStuff  " 
					+ obj);
		MaintainPartyPageModel mod = (MaintainPartyPageModel) obj;
		Object d = mod.getPartyDTO();
		
		if (d instanceof OrganisationDTO) {
			System.out.println("Organisation d=" + ((OrganisationDTO)d).getBusinessName()
					+ "  " + ((OrganisationDTO)d).getName());
		} else if (d instanceof PersonDTO) {
			System.out.println("Person d=" + ((PersonDTO)d).getName());
		}

	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see za.co.liberty.web.pages.MaintenanceBasePage#createContainerPanel()
	 */
	@Override
	public Panel createContainerPanel() {
		Panel panel;
//		if (pageModel.getPartyOid() == 0) {
//			panel = new EmptyPanel(CONTAINER_PANEL_NAME);
//		} else {
		try {
			//TODO disable panels based on editstate
			int[] disabledPanels = null;
			if(this.getEditState() != EditStateType.VIEW){
				disabledPanels = new int[]{};
			}
			panel = new MaintainPartyPanel(CONTAINER_PANEL_NAME, pageModel,
					getEditState(), disabledPanels,this.getFeedbackPanel(),this);
		} catch (TabAccessException e) {			
			//display message that all tabs have been disabled
			error(e.getUserMessage());
			panel = new EmptyPanel(CONTAINER_PANEL_NAME);
		}				
//		}
		panel.setOutputMarkupId(true);
		return panel;
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
		return new BaseModificationButtonsPanel<MaintainPartyDTO>(
				SELECTION_PANEL_NAME, pageModel, this,containerForm ,
				MaintainPartyDTO.class,this.getFeedbackPanel(), true,true,true,false,true,true) {

			private static final long serialVersionUID = 1L;

			@Override
			public void resetSelection() {
				
			}	
			
			
			@SuppressWarnings("unchecked")
			@Override
			public void doModify_onSubmit(AjaxRequestTarget target, Form form) {
				//first check that an agreement is in context if the agreement list is not empty
				if(pageContextDTO.getAllAgreementsList() != null && pageContextDTO.getAllAgreementsList().size() > 0
						&& (pageContextDTO.getAgreementContextDTO() == null || pageContextDTO.getAgreementContextDTO().getAgreementNumber() == null)){
					//new rule is that a user must select a context agreement for use with the party request and broadcast
					error("Please select an agreement into the context for use as the request agreement, this will also be the agreement that will be broadcast");					
					if(target != null && getFeedbackPanel() != null){
						target.add(getFeedbackPanel());//target.addComponent(getFeedbackPanel());
					}
					return;
				}
				super.doModify_onSubmit(target, form);
			}

			@SuppressWarnings("unchecked")
			@Override
			public void doTerminate_onSubmit(AjaxRequestTarget target, Form form) {
				//delete the party selected
				//all we do to delete is set the end date of the dto to todays date
				PartyDTO dto = MaintainPartyPage.this.pageModel.getPartyDTO();
				dto.setEffectiveTo(new Date());
				MaintainPartyPage.this.doSave_onSubmit();
			}		


			@SuppressWarnings("unchecked")
			@Override
			public void doBroadcast_onSubmit(AjaxRequestTarget target, Form form) {				
				super.doBroadcast_onSubmit(target, form);
				ContextPartyDTO party = pageContextDTO.getPartyContextDTO();
				ContextAgreementDTO agmt = pageContextDTO.getAgreementContextDTO();
				IPartyMaintenanceController controller = MaintainPartyPageModel.getPartyMaintenanceController();
				try {
					controller.broadcastPartySynchronous(party.getPartyOid(), (agmt != null) ? agmt.getAgreementNumber() : null, SRSAuthWebSession.get().getSessionUser().getPartyOid());
					info("Party broadcast successfully");
				} catch (ValidationException e) {
					for(String error : e.getErrorMessages()){
						error(error);
					}
				}catch (PartyDetailsBroadcastException e) {
					logger.error("An error occured while broadcasting the party msg: " + e.getMessage(),e);
					error("An error occured while broadcasting the party msg: " + e.getMessage());
				}
				if(target != null){
					target.add(getFeedbackPanel());//target.addComponent(getFeedbackPanel());
				}
			}
			
			@Override
			protected boolean isBroadcastButtonEnabled() {
				boolean ret = super.isBroadcastButtonEnabled();
				@SuppressWarnings("unused")
				MaintainPartyPageModel thisPageModel = 
					MaintainPartyPage.this.pageModel;
				if (ret &&
						MaintainPartyPage.this.pageModel!=null &&
						MaintainPartyPage.this.pageModel.getPartyDTO() !=null) {
					/**
					 * Broadcast only allowed if agreement in context and add access is allowed
					 */
					ret &= MaintainPartyPage.this.pageModel.getPartyDTO().getOid()>0 && MaintainPartyPage.this.hasAddAccess();
					
					return ret;
				}
				return false;
			}

			@Override
			protected String getTerminateButtonText() {
				return "Delete";
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
				if(parent.hasAddAccess()){
					//popup wizzard
					window.show(target);					
					//pageModel.setSelectedItem(getNewDtoInstance());
					//parent.setEditState(EditStateType.ADD, target);
					//parent.swapContainerPanel(target);
					//parent.swapNavigationPanel(target);
				}else{
					//display error
				}
			}
		};
		
		//return new EmptyPanel(SELECTION_PANEL_NAME);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see za.co.liberty.web.pages.MaintenanceBasePage#initialisePageModel(java.lang.Object)
	 */
	@Override
	public Object initialisePageModel(Object obj, Integer currentTab) {	
		MaintainPartyPageModel model = new MaintainPartyPageModel();
		if (obj instanceof PartyDTO && !(obj instanceof HierarchyNodeDTO)) {
			// jzb0608 (GuiRequestTest)
			return initialisePageModelForGuiRequest((PartyDTO)obj,model);
		}
		//ContextAgreementDTO agreement = dto.getAgreementContextDTO();
		if(pageContextDTO != null){
			ContextPartyDTO partydto = pageContextDTO.getPartyContextDTO();		
			if (partydto == null || partydto.getPartyOid() == 0 || partydto.getTypeOid()== SRSType.ADVANCEDPRACTICE) {
				error("There is no party selected in the context");
			}else if(partydto.getTypeOid() == SRSType.DIVISION || partydto.getTypeOid() == SRSType.REGION ||
					partydto.getTypeOid() == SRSType.SUBREGION || partydto.getTypeOid() == SRSType.BRANCH || partydto.getTypeOid() == SRSType.UNIT){
					error("Please go to the hierachy option to maintain hierarchy nodes");
			}else {
				if (obj != null) {
					model = (MaintainPartyPageModel) obj;
				} else {
					IPartyMaintenanceController controller = MaintainPartyPageModel.getPartyMaintenanceController();
					long partyOID = pageContextDTO.getPartyContextDTO().getPartyOid();
					model.setSelectedAgreementNr(((pageContextDTO.getAgreementContextDTO()!=null) ? pageContextDTO.getAgreementContextDTO().getAgreementNumber() : null));
					try {						
						PartyDTO party = controller.getPartyDTO(partyOID);
						model.setPartyBeforeImage((PartyDTO) SerializationUtils.clone(party));
						model.setPartyDTO(party);						
						model.setPaysToAgreementList(controller.getPaysToAgreementRolesForPartyOid(partyOID));
					} catch (DataNotFoundException e) {
						error("Could not retreive party details using key " + partyOID + ", Please contact support if you beleive you should not be seeing this message.");
						model.setPartyDTO(new PartyDTO());
					}
					model.setUacfID(pageContextDTO.getPartyContextDTO().getUacfID());	
					if(currentTab != null){
						model.setCurrentTab(currentTab);
					}
				}
			}
			if(model.getMedicalAidDetailsPanelModel() == null){
				MedicalAidDetailsPanelModel panelModel = new MedicalAidDetailsPanelModel();
				panelModel.setCurrentPartyID((partydto != null) ? partydto.getPartyOid() : 0);			
				panelModel.setCurrentPartyType((partydto != null) ? partydto.getTypeOid() : 0);
				model.setMedicalAidDetailsPanelModel(panelModel);
			}
			if(model.getProvidentFundBeneficiariesModel() == null){
				ProvidentFundBeneficiariesModel beneficiariesModel = new ProvidentFundBeneficiariesModel();
				beneficiariesModel.setProvidentFundBeneficiariesDTO(new ProvidentFundBeneficiariesDTO());
				model.setProvidentFundBeneficiariesModel(beneficiariesModel);
			}
		}
		
		
		pageModel = model;		
		return pageModel;
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
	private Object initialisePageModelForGuiRequest(PartyDTO party, 
			MaintainPartyPageModel model) {
		model.setPartyDTO(party);
		model.setSelectedItem(party);		
		model.setUacfID(pageContextDTO.getPartyContextDTO().getUacfID());	
		pageModel = model;
		return pageModel;
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
		return ContextType.PARTY;
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
			//For TAX Details Request
			if(TaxDetailsPanel.class == pageModel.getCurrentTabClass()){
				save_TaxDetails(userProfile,agmtNoContext);
			}else if(pageModel.getCurrentTabClass() == MedicalAidDetailsPanel.class){
				//store medical aid details
				save_MedicalAidDetails(userProfile,agmtNoContext);
			}else if(pageModel.getCurrentTabClass() == PartyHierarchyPanel.class){
				//raise the party hierarchy request
				ArrayList<PartyRoleDTO> partyRolesToStore = new ArrayList<PartyRoleDTO>(this.pageModel.getMaintainPartyHierarchyPanelModel().getPartyToPartyRoles());
				partyRolesToStore.addAll(this.pageModel.getMaintainPartyHierarchyPanelModel().getPartyToPartyRoleRemovals());
				ArrayList<PartyRoleDTO> partyRoles = new ArrayList<PartyRoleDTO>(this.pageModel.getMaintainPartyHierarchyPanelModel().getPartyRoles());
				partyRoles.addAll(this.pageModel.getMaintainPartyHierarchyPanelModel().getPartyRoleRemovals());
				controller.raisePartyhierarchyAndRoleRequest(
						partyRolesToStore,
						this.pageModel.getMaintainPartyHierarchyPanelModel().getPartyToPartyRolesBeforeImage(),
						userProfile,agmtNoContext,this.pageModel.getPartyDTO().getOid(),
						PanelToRequestMapping.getMappingForPageAndPanel(MaintainPartyPage.class,pageModel.getCurrentTabClass()),partyRoles,this.pageModel.getMaintainPartyHierarchyPanelModel().getPartyRolesBeforeImage());	
			
			}else if(ProvidentFundBeneficiariesPanel.class == pageModel.getCurrentTabClass()){
				saveProvidentFundBeneficiariesDetails(userProfile, agmtNoContext);
				System.out.println("Provident Fund Save : " + pageModel.getProvidentFundBeneficiariesModel().getProvidentFundBeneficiariesList().size());
			}else {					
				// jzb0608 - removed the isPartyAnIntermediary check, this is now done in the validator.
				controller.storeParty(this.pageModel.getPartyDTO(),agmtNoContext,userProfile,
					this.pageModel.getPartyBeforeImage(),
					PanelToRequestMapping.getMappingForPageAndPanel(MaintainPartyPage.class,pageModel.getCurrentTabClass()),
					false,null);			
//			Use below if users want to see stored node on main screen		
//			reload context as details have changed			
			try{
				ContextDTO newContextDTO = SRSAuthWebSession.get().getContextDTO();					
				//IContextManagement contextBean = (IContextManagement) SRSAuthWebSession.get().getEJBReference(EJBReferences.CONTEXT_MANAGEMENT);
				IContextManagement contextBean=ServiceLocator.lookupService(IContextManagement.class);
				//First we check if the node has ended, if it has we load user into context
				boolean ended = true;
				boolean found = false;				
				if(pageModel.getPartyDTO().getEffectiveTo() == null || !pageModel.getPartyDTO().getEffectiveTo().before(new Date())){
					//node has not ended
					ended = false;
					found = false;
					ResultPartyDTO updatedParty = MaintainPartyPageModel.getPartyManagement().findPartyWithObjectOid(pageModel.getPartyDTO().getOid());
					if(updatedParty != null){
						newContextDTO = contextBean.getContext(updatedParty);
						if(aggContext != null){
							newContextDTO.setAgreementContextDTO(aggContext);
						}						
						found = true;
					}
				}			
				//if node has not been found or has ended then we put user in context
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
					getSession().error("Party stored but could not be put into the context so you were put in the context");
				}
				if(ended){
					getSession().info("User set back in context as node is not active anymore");
				}
				SRSAuthWebSession.get().setContextDTO(newContextDTO);				
//			}catch (CloneNotSupportedException e) {
//				e.printStackTrace();
//				getSession().error("Party stored but could not be put into the context");
			}catch (DataNotFoundException e) {
				e.printStackTrace();
				getSession().error("Party stored but could not be put into the context");
			} catch (NamingException e) { //MSK#Cahnge
				throw new CommunicationException(e);
			}	
		}
			
		} catch (ValidationException e) {
			for(String error : e.getErrorMessages()){
				error(error);
			}
			return;
		}	
		
		invalidatePage();
		List<String> bankingVerificationRuleWarnings = pageModel.getCurrentTabClass() == BankingDetailsPanel.class
				? bankingVerificationRuleWarnings = this.pageModel.getPartyDTO().getBankingDetailsDTO()
						.getBankingVerificationResponseDTO().getBankingVerificationRuleWarnings()
				: new ArrayList<>();
		
		if (bankingVerificationRuleWarnings.size() > 0) {
			bankingVerificationRuleWarnings.forEach(m -> getSession().warn(m));
			getSession().info("Record was saved succesfully with warning(s)");
		} else {
			getSession().info("Record was saved succesfully");
		}

		setResponsePage(new MaintainPartyPage(null, pageModel.getCurrentTab()));
	}		
	
	private void save_TaxDetails(ISessionUserProfile userProfile,Long agmtNoContext) throws ValidationException{
		
		IPartyMaintenanceController controller = MaintainPartyPageModel.getPartyMaintenanceController();	
		
		if(this.pageModel.getTaxDetailsPanelModel() == null)
			throw new ValidationException("Tax Panel Model is Null");
		pageModel.getTaxDetailsPanelModel().getTaxDetailsDTO().setPartyOID(pageModel.getPartyDTO().getOid());
		controller.storeTaxDetails(this.pageModel.getTaxDetailsPanelModel().getTaxDetailsDTO(),agmtNoContext,userProfile,
				this.pageModel.getTaxDetailsPanelModel().getTaxDetailsBeforeImage(),
				PanelToRequestMapping.getMappingForPageAndPanel(MaintainPartyPage.class,pageModel.getCurrentTabClass()),
				getPartyManagement().isPartyAnIntermediary(this.pageModel.getPartyDTO().getOid()));		
	}
	
	/**
	 * Save and raise a request to store the medical aid details
	 * @param userProfile
	 * @param agmtNoContext
	 * @throws ValidationException
	 */
	private void save_MedicalAidDetails(ISessionUserProfile userProfile,Long agmtNoContext) throws ValidationException{
		
		IPartyMaintenanceController controller = MaintainPartyPageModel.getPartyMaintenanceController();	
		
		if(this.pageModel.getMedicalAidDetailsPanelModel() == null)
			throw new ValidationException("Medical Aid Panel Model is Null, Please call support if this error persists");
		pageModel.getMedicalAidDetailsPanelModel().getMedicalAidDetail().setCreatedByPartyID(userProfile.getPartyOid());
		
		controller.storeMedicalAidDetails(pageModel.getMedicalAidDetailsPanelModel().getMedicalAidDetail(),agmtNoContext,userProfile,
				pageModel.getMedicalAidDetailsPanelModel().getMedicalAidDetailBeforeImage(),
				PanelToRequestMapping.getMappingForPageAndPanel(MaintainPartyPage.class,pageModel.getCurrentTabClass()),
				getPartyManagement().isPartyAnIntermediary(this.pageModel.getPartyDTO().getOid()));		
	}
	
	/**
	 * Save and raise a request to store provident fund beneficiaries details
	 * @param userProfile
	 * @param agmtNoContext
	 * @throws ValidationException
	 */
	private void saveProvidentFundBeneficiariesDetails(ISessionUserProfile userProfile,Long agmtNoContext) throws ValidationException{
		
		IPartyMaintenanceController controller = MaintainPartyPageModel.getPartyMaintenanceController();	
		
		if(this.pageModel.getProvidentFundBeneficiariesModel() == null)
			throw new ValidationException("Provident Fund Beneficiaries Panel Model is Null, Please call support if this error persists");
		
		controller.storeProvidentFundBeneficiaries(pageModel.getProvidentFundBeneficiariesModel().getProvidentFundBeneficiariesList(),
				pageModel.getProvidentFundBeneficiariesModel().getProvidentFundBeneficiariesListBeforeImage(),
				agmtNoContext, userProfile, 
				PanelToRequestMapping.getMappingForPageAndPanel(MaintainPartyPage.class,pageModel.getCurrentTabClass()), 
				getPartyManagement().isPartyAnIntermediary(this.pageModel.getPartyDTO().getOid()));	
	}
	
	protected IPartyManagement getPartyManagement(){
		if(partyManagement == null){
			try {
				partyManagement = ServiceLocator.lookupService(IPartyManagement.class);
			} catch (NamingException e) {
				throw new CommunicationException(e);
			}
		}
		return partyManagement;
	}	
	
}

