package za.co.liberty.web.pages.hierarchy;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
import za.co.liberty.business.guicontrollers.hierarchy.IHierarchyGUIController;
import za.co.liberty.dto.agreement.maintainagreement.BulkAgreementTransferDTO;
import za.co.liberty.dto.common.IDValueDTO;
import za.co.liberty.dto.contracting.ResultPartyDTO;
import za.co.liberty.dto.databaseenum.PartyStatusDBEnumDTO.PartyStatus;
import za.co.liberty.dto.party.HierarchyNodeDTO;
import za.co.liberty.dto.party.RolePlayerDTO;
import za.co.liberty.dto.party.maintainparty.MaintainPartyDTO;
import za.co.liberty.dto.userprofiles.ContextDTO;
import za.co.liberty.dto.userprofiles.ContextPartyDTO;
import za.co.liberty.dto.userprofiles.ISessionUserProfile;
import za.co.liberty.exceptions.data.DataNotFoundException;
import za.co.liberty.exceptions.data.ValidationException;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.exceptions.security.TabAccessException;
import za.co.liberty.helpers.lookup.ServiceLocator;
import za.co.liberty.interfaces.party.IPartyNameAndIdFLO;
import za.co.liberty.srs.type.SRSType;
import za.co.liberty.web.data.enums.ContextType;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.data.enums.PanelToRequestMapping;
import za.co.liberty.web.data.pages.ITabbedPageModel;
import za.co.liberty.web.pages.Logon;
import za.co.liberty.web.pages.MaintenanceBasePage;
import za.co.liberty.web.pages.hierarchy.model.MaintainHierarchyPageModel;
import za.co.liberty.web.pages.interfaces.IPageDataLoaded;
import za.co.liberty.web.pages.maintainagreement.MaintainAgreementPage;
import za.co.liberty.web.pages.maintainagreement.model.MaintainAgreementPageModel;
import za.co.liberty.web.pages.panels.BaseModificationButtonsPanel;
import za.co.liberty.web.system.SRSAuthWebSession;
import za.co.liberty.web.wicket.modal.SRSModalWindow;

/**
 * Hierarchy maintenance page
 * 
 * @author DZS2610
 *
 */
public class MaintainHierarchyPage extends MaintenanceBasePage<Integer> implements IPageDataLoaded{


	private static final int NDP_BRANCH_TYPE = 16;

	private static final int LBF_BRANCH_TYPE = 2;

	private static final long serialVersionUID = 1L;

	private MaintainHierarchyPageModel pageModel;

	private String pageName = "Maintain Hierarchy Node";
	
	private BaseModificationButtonsPanel buttonsPanel;
	
	private SRSModalWindow window;
	
	private SRSModalWindow lbfNDPwindow;
	
	private transient IHierarchyGUIController hierarchyGUIController;
	
//	by default it is set to true, uses should set to false when intializing their pages and set back to true once all data is loaded
	private boolean pageDataLoaded = true;

	/**
	 * 
	 */
	public MaintainHierarchyPage() {
		this(null);
		//setEditState(EditStateType.MODIFY, null);
	}

	/**
	 * @param obj
	 */
	public MaintainHierarchyPage(Object obj) {
		super(obj);
		this.add(window = createModalWindow("addNewWizzardWindow"));
		this.add(lbfNDPwindow = createLBFNDPModalWindow("addNewLBFNDPWizzardWindow"));
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
				return "MAINTAIN.HIERARCHY.ADD-";
			}
		};
		window.setTitle("Add New Hierarchy Node");				
		// Create the page
		window.setPageCreator(new ModalWindow.PageCreator() {
			private static final long serialVersionUID = 1L;
			public Page createPage() {			
				return new AddHierarchyNodeWizardPage(window,pageModel, 
						MaintainHierarchyPage.this);
			}			
		});		
		
		window.setWindowClosedCallback(new ModalWindow.WindowClosedCallback() {
			private static final long serialVersionUID = 1L;			
			public void onClose(AjaxRequestTarget target) {
				if (getLogger().isDebugEnabled())
					getLogger().debug("MaintainHierarchy.add.setWindowClosedCallback.onClose   ");
				
				MaintainHierarchyPageModel model = (MaintainHierarchyPageModel) window.getSessionModelForPage();
				
				
				/**
				 * Now we do the stuff that would usually be done here.
				 */				
				if (model.isModalWizardSucces()) {
					if (getLogger().isDebugEnabled())
						getLogger().debug("  Reloading Page!!!   ");
					
					getSession().info("Hierarchy Node added and set into context");
					setResponsePage(MaintainHierarchyPage.class);
					target.add(feedbackPanel);
//					onAfterAddParty(target, model.getPartyDTO());
				} else {
					feedbackPanel.info("Cancelled Add Hierarchy");
					target.add(feedbackPanel);
				}
				
				window.clearModalPageModelInSession();
			}			
		});
		
		// Initialise window settings
		window.setCookieName("MAINTAIN.HIERARCHY.ADD");
		window.setMinimalHeight(500);
		window.setInitialHeight(500);
		window.setMinimalWidth(500);
		window.setInitialWidth(500);
		window.setMaskType(MaskType.SEMI_TRANSPARENT);
		window.setCssClassName(ModalWindow.CSS_CLASS_GRAY);	
		window.setOutputMarkupId(true);
		window.setOutputMarkupPlaceholderTag(true);
		return window;
	}
	
	
	/**
	 * Create the LBF NDP window
	 * 
	 * @param id
	 * @return
	 */
	private SRSModalWindow createLBFNDPModalWindow(String id) {		
		final SRSModalWindow window = new SRSModalWindow(id) {
			
			@Override
			public String getModalSessionIdentifier() {
				return "MAINTAIN.HIERARCHY.LBF.ADD-";
			}
		};
		window.setTitle("Add New LBF NDP Hierarchy Node");				
		// Create the page
		window.setPageCreator(new ModalWindow.PageCreator() {
			private static final long serialVersionUID = 1L;
			public Page createPage() {					
				return new AddLBFNDPHierarchyNodeWizardPage(window,pageModel, MaintainHierarchyPage.this);

			}			
		});		
		
		window.setWindowClosedCallback(new ModalWindow.WindowClosedCallback() {
			private static final long serialVersionUID = 1L;			
			public void onClose(AjaxRequestTarget target) {
				if (getLogger().isDebugEnabled())
					getLogger().debug("MaintainHierarchy.add.setWindowClosedCallback.onClose   ");
				
				MaintainHierarchyPageModel model = (MaintainHierarchyPageModel) window.getSessionModelForPage();
				
				
				/**
				 * Now we do the stuff that would usually be done here.
				 */				
				if (model.isModalWizardSucces()) {
					if (getLogger().isDebugEnabled())
						getLogger().debug("  Reloading Page!!!   ");
					
					getSession().info("Hierarchy LBF Node added and set into context");
					setResponsePage(MaintainHierarchyPage.class);
					target.add(feedbackPanel);
//					onAfterAddParty(target, model.getPartyDTO());
				} else {
					feedbackPanel.info("Cancelled Add LBF Hierarchy");
					target.add(feedbackPanel);
				}
				
				window.clearModalPageModelInSession();
			}			
		});
		
		// Initialise window settings
		window.setMinimalHeight(400);
		window.setInitialHeight(500);
		window.setMinimalWidth(200);
		window.setInitialWidth(300);

		window.setMaskType(MaskType.SEMI_TRANSPARENT);
		window.setCssClassName(ModalWindow.CSS_CLASS_GRAY);	
		window.setOutputMarkupId(true);
		window.setOutputMarkupPlaceholderTag(true);
		return window;
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see za.co.liberty.web.pages.MaintenanceBasePage#createContainerPanel()
	 */
	@Override
	public Panel createContainerPanel() {
		Panel panel;
		try {
			int[] disabledPanels = null;
			if(this.getEditState() != EditStateType.VIEW){
				disabledPanels = new int[]{};
			}
			panel = new MaintainHierarchyNodePanel(CONTAINER_PANEL_NAME, pageModel,
					getEditState(), disabledPanels, this.getFeedbackPanel(), containerForm,this);
		} catch (TabAccessException e) {			
			//display message that all tabs have been disabled
			error(e.getUserMessage());
			panel = new EmptyPanel(CONTAINER_PANEL_NAME);
		}				
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
		
		boolean isLBFMB = false;
		getLogger().debug("@@@@%%%%%% hierarchy oid " + this.pageModel.getHierarchyNodeDTO().getOid());
		if(this.pageModel.getHierarchyNodeDTO().getOid() == 0){
			isLBFMB = false;
		}
		
		Long countlbfrelations = null;
		
		if(this.pageModel.getHierarchyNodeDTO().getOid() != 0){
			hierarchyGUIController = pageModel.getHierarchyGUIController();
		
			countlbfrelations = hierarchyGUIController.findLBFNDPRelations(SRSType.ADDITIONALBRANCHOF,pageModel.getHierarchyNodeDTO().getExternalType().getKeyInt(), this.pageModel.getHierarchyNodeDTO().getOid());
		}
		//check to see if the lbf is a main branch
		if(countlbfrelations != null && countlbfrelations == 0L && (this.pageModel.getHierarchyNodeDTO().getExternalType().getKeyInt() == LBF_BRANCH_TYPE || this.pageModel.getHierarchyNodeDTO().getExternalType().getKeyInt() == NDP_BRANCH_TYPE )){
			isLBFMB = true;
		}
		
		if(isLBFMB){
			createLBFButtonPanel();	
		}
		else {
			createButtonPanel();	
		}
			
		return buttonsPanel;
	}

	private void createLBFButtonPanel() {
		buttonsPanel =  new BaseModificationButtonsPanel<MaintainPartyDTO>(
				SELECTION_PANEL_NAME, pageModel, this, containerForm,
				MaintainPartyDTO.class,this.getFeedbackPanel(), true,true,true,false,true,true,false,true) {

			private static final long serialVersionUID = 1L;

			@Override
			public void resetSelection() {
				
			}	

			@Override
			public void doModify_onSubmit(AjaxRequestTarget target, Form form) {
				if(!MaintainHierarchyPage.this.isPageDataLoaded()){
					error("The Page data is still busy loading, please wait until finished before clicking the modify button");
					target.add(getFeedbackPanel());					
				}else{
					super.doModify_onSubmit(target, form);
				}
			}

			/**
			 * Called when Add new is submitted. Notify parent and 
			 * swap panels.  Ensure that selected item is set before calling.
			 * 
			 * @param target
			 * @param form
			 */
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

			/**
			 * Called when Add new is submitted. Notify parent and 
			 * swap panels.  Ensure that selected item is set before calling.
			 * 
			 * @param target
			 * @param form
			 */
			@Override
			public void doAddNewLBFNDP_onSubmit(AjaxRequestTarget target, Form form) {							
				//add new is different here as we will display a popup wizzard and users will not input anything into the current panel
				//first we check access to this function, user might have buttons enabled, code mistake, but should not be allowed to add
				if(parent.hasAddAccess()){
					//popup wizzard
					lbfNDPwindow.show(target);					
					//pageModel.setSelectedItem(getNewDtoInstance());
					//parent.setEditState(EditStateType.ADD, target);
					//parent.swapContainerPanel(target);
					//parent.swapNavigationPanel(target);
				}else{
					//display error
				}
			}	
			@Override
			protected boolean isReactivateButtonenabled() {
				//first call super, if disabled then return --> this is incase user does not have rights to perform the function
				boolean enabled =  super.isReactivateButtonenabled();
				if(!enabled){
					return enabled;
				}
				//enable only if edit state is view and the status is terminated or pending termination
				if(getEditState() == EditStateType.VIEW &&
						pageModel.getSelectedItem() != null 
						&& ((RolePlayerDTO)pageModel.getSelectedItem()).getStatus() != null
						&& (((RolePlayerDTO)pageModel.getSelectedItem()).getStatus().getKeyInt()  == PartyStatus.TERMINATED.getId()
						|| ((RolePlayerDTO)pageModel.getSelectedItem()).getStatus().getKeyInt() == PartyStatus.PENDING_TERMINATION.getId())){
					return true;
				}
				return false;
			}

			@Override
			protected boolean isTerminateButtonenabled() {
				//first call super, if disabled then return --> this is incase user does not have rights to perform the function
				boolean enabled =  super.isReactivateButtonenabled();
				if(!enabled){
					return enabled;
				}
				//enable only if edit state is view and the status is pending termination or active
				if(getEditState() == EditStateType.VIEW &&
						pageModel.getSelectedItem() != null 
						&& ((RolePlayerDTO)pageModel.getSelectedItem()).getStatus() != null
						&& (((RolePlayerDTO)pageModel.getSelectedItem()).getStatus().getKeyInt()  == PartyStatus.ACTIVE.getId()
						|| ((RolePlayerDTO)pageModel.getSelectedItem()).getStatus().getKeyInt() == PartyStatus.PENDING_TERMINATION.getId())){
					return true;
				}
				return false;
			}

			@Override
			public void doReactivate_onSubmit(AjaxRequestTarget target, Form form) {				
				super.doReactivate_onSubmit(target, form);
				//reactivate is a process, we will kick it off here
				save(true);				
			}

			@Override
			public void doTerminate_onSubmit(AjaxRequestTarget target, Form form) {				
				//for now make sure Hierarchy Panel is selected
				if(((ITabbedPageModel)pageModel).getCurrentTabClass() == HierarchyNodePanel.class){
					super.doTerminate_onSubmit(target, form);
					MaintainHierarchyPage.this.setEditState(EditStateType.TERMINATE, target);
					MaintainHierarchyPage.this.swapContainerPanel(target);
					MaintainHierarchyPage.this.swapNavigationPanel(target);	
				}else{					
					error("Please make sure the Hierarchy Details Tab is selected when terminating a Hierachy Node");
				}
				//user will click save to commit
			}			
		};
	}

	private void createButtonPanel() {
		buttonsPanel =  new BaseModificationButtonsPanel<MaintainPartyDTO>(
				SELECTION_PANEL_NAME, pageModel, this, containerForm,
				MaintainPartyDTO.class,this.getFeedbackPanel(), true,true,true,false,true,true,false) {

			private static final long serialVersionUID = 1L;

			@Override
			public void resetSelection() {
				
			}	

			@Override
			public void doModify_onSubmit(AjaxRequestTarget target, Form form) {
				if(!MaintainHierarchyPage.this.isPageDataLoaded()){
					error("The Page data is still busy loading, please wait until finished before clicking the modify button");
					target.add(getFeedbackPanel());					
				}else{
					super.doModify_onSubmit(target, form);
				}
			}

			/**
			 * Called when Add new is submitted. Notify parent and 
			 * swap panels.  Ensure that selected item is set before calling.
			 * 
			 * @param target
			 * @param form
			 */
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


			@Override
			protected boolean isReactivateButtonenabled() {
				//first call super, if disabled then return --> this is incase user does not have rights to perform the function
				boolean enabled =  super.isReactivateButtonenabled();
				if(!enabled){
					return enabled;
				}
				//enable only if edit state is view and the status is terminated or pending termination
				if(getEditState() == EditStateType.VIEW &&
						pageModel.getSelectedItem() != null 
						&& ((RolePlayerDTO)pageModel.getSelectedItem()).getStatus() != null
						&& (((RolePlayerDTO)pageModel.getSelectedItem()).getStatus().getKeyInt()  == PartyStatus.TERMINATED.getId()
						|| ((RolePlayerDTO)pageModel.getSelectedItem()).getStatus().getKeyInt() == PartyStatus.PENDING_TERMINATION.getId())){
					return true;
				}
				return false;
			}

			@Override
			protected boolean isTerminateButtonenabled() {
				//first call super, if disabled then return --> this is incase user does not have rights to perform the function
				boolean enabled =  super.isReactivateButtonenabled();
				if(!enabled){
					return enabled;
				}
				//enable only if edit state is view and the status is pending termination or active
				if(getEditState() == EditStateType.VIEW &&
						pageModel.getSelectedItem() != null 
						&& ((RolePlayerDTO)pageModel.getSelectedItem()).getStatus() != null
						&& (((RolePlayerDTO)pageModel.getSelectedItem()).getStatus().getKeyInt()  == PartyStatus.ACTIVE.getId()
						|| ((RolePlayerDTO)pageModel.getSelectedItem()).getStatus().getKeyInt() == PartyStatus.PENDING_TERMINATION.getId())){
					return true;
				}
				return false;
			}

			@Override
			public void doReactivate_onSubmit(AjaxRequestTarget target, Form form) {				
				super.doReactivate_onSubmit(target, form);
				//reactivate is a process, we will kick it off here
				save(true);				
			}

			@Override
			public void doTerminate_onSubmit(AjaxRequestTarget target, Form form) {				
				//for now make sure Hierarchy Panel is selected
				if(((ITabbedPageModel)pageModel).getCurrentTabClass() == HierarchyNodePanel.class){
					super.doTerminate_onSubmit(target, form);
					MaintainHierarchyPage.this.setEditState(EditStateType.TERMINATE, target);
					MaintainHierarchyPage.this.swapContainerPanel(target);
					MaintainHierarchyPage.this.swapNavigationPanel(target);	
				}else{					
					error("Please make sure the Hierarchy Details Tab is selected when terminating a Hierachy Node");
				}
				//user will click save to commit
			}			
		};
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see za.co.liberty.web.pages.MaintenanceBasePage#initialisePageModel(java.lang.Object)
	 */
	@Override
	public Object initialisePageModel(Object obj, Integer currentTab) {
		MaintainHierarchyPageModel model = new MaintainHierarchyPageModel();		
		//ContextAgreementDTO agreement = dto.getAgreementContextDTO();
		ContextPartyDTO partydto = null;
		if(pageContextDTO != null){
			partydto = pageContextDTO.getPartyContextDTO();	
		}
		IHierarchyGUIController controller = model.getHierarchyGUIController();
		if (obj instanceof HierarchyNodeDTO) {
			// jzb0608 (GuiRequestTest)
			return initialisePageModelForGuiRequest((HierarchyNodeDTO)obj,model, controller);
		}
		
		if ((partydto == null || partydto.getPartyOid() == 0) || partydto.getTypeOid()== SRSType.ADVANCEDPRACTICE) {
			model.setHierarchyNodeDTO(new HierarchyNodeDTO());
			model.setSelectedItem(null);
			error("There is no node selected in the context");
		} else {
			if (obj != null && model.getHierarchyNodeDTO() != null) {
				model = (MaintainHierarchyPageModel) obj;				
				if(model.getHierarchyNodeDTO().getType() !=  null && model.getHierarchyNodeDTO().getType().getOid() == SRSType.DIVISION){
					//refresh division list
					try {
						model.setHierarchyChannelList(controller.getHierarchyChannelList());
					} catch (DataNotFoundException e) {					
						error("Hierarchy channels could not be found msg:" + e.getMessage());
						model.setHierarchyChannelList(new ArrayList<IPartyNameAndIdFLO>(0));
					}
				}else if(model.getHierarchyNodeDTO().getType() !=  null && model.getHierarchyNodeDTO().getType().getOid() == SRSType.BRANCH){
					//refresh cost centre list
					model.setCostCenters(controller.getCostCenters());
					model.setOrganisationTypeList(controller.getOrganisationTypes());
				}
			} else {
				long partyOID = pageContextDTO.getPartyContextDTO().getPartyOid();				
				try {					
					HierarchyNodeDTO party = controller.getHierarchyNodeDTO(partyOID);					
					model.setHierarchyNodeDTO(party);
					model.setPartyDTOBeforeImage(
							(HierarchyNodeDTO) SerializationUtils.clone(party));
					model.setLbfNDPPartyDTO(
							(HierarchyNodeDTO) SerializationUtils.clone(party));
					
					model.setSelectedItem(party);					
				} catch (DataNotFoundException e) {
					error("Party in context is not a hierarchy node, please search for a hierarchy node in the context panel above");
					model.setHierarchyNodeDTO(new HierarchyNodeDTO());
					model.setSelectedItem(null);					
				}					
				try {
					model.setHierarchyTypeList(controller.getHierarchyTypeList());
				} catch (DataNotFoundException e) {					
					error("Hierarchy types could not be found msg:" + e.getMessage());
					model.setHierarchyTypeList(new ArrayList<IDValueDTO>(0));
				}
				try {
					model.setHierarchyChannelList(controller.getHierarchyChannelList());
				} catch (DataNotFoundException e) {					
					error("Hierarchy channels could not be found msg:" + e.getMessage());
					model.setHierarchyChannelList(new ArrayList<IPartyNameAndIdFLO>(0));
				}				
				model.setEmployeeTypeList(controller.getEmployeeTypeList());
				model.setUacfID(pageContextDTO.getPartyContextDTO().getUacfID());	
				model.setCostCenters(controller.getCostCenters());
			}
		}
		pageModel = model;		
		return pageModel;
	}

	/**
	 * Enables the testing of GuiRequests by accepting the HierarchyNodeDTO that is
	 * sent via the constructor.
	 * 
	 * Added by jzb0608 (Jean).
	 * 
	 * @param party
	 * @param model
	 * @param controller
	 * @return
	 */
	private Object initialisePageModelForGuiRequest(HierarchyNodeDTO party, 
			MaintainHierarchyPageModel model,IHierarchyGUIController controller) {

		model.setHierarchyNodeDTO(party);
		model.setSelectedItem(party);					
		try {
			model.setHierarchyTypeList(controller.getHierarchyTypeList());
		} catch (DataNotFoundException e) {					
			error("Hierarchy types could not be found msg:" + e.getMessage());
			model.setHierarchyTypeList(new ArrayList<IDValueDTO>(0));
		}
		try {
			model.setHierarchyChannelList(controller.getHierarchyChannelList());
		} catch (DataNotFoundException e) {					
			error("Hierarchy channels could not be found msg:" + e.getMessage());
			model.setHierarchyChannelList(new ArrayList<IPartyNameAndIdFLO>(0));
		}				
		model.setEmployeeTypeList(controller.getEmployeeTypeList());
		model.setUacfID(pageContextDTO.getPartyContextDTO().getUacfID());	
		model.setCostCenters(controller.getCostCenters());
		model.setOrganisationTypeList(controller.getOrganisationTypes());
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
		return ContextType.PARTY_ORGANISATION_ONLY;  //MZP0801 Party Organisation Only
	}

	@Override
	public void doSave_onSubmit() {	
		save(false);
	}
	
	private void save(boolean reactivate){
		ISessionUserProfile userProfile = SRSAuthWebSession.get().getSessionUser();		
		IHierarchyGUIController controller = getHierarchyGUIController();	
		try {
			if(pageModel.getCurrentTabClass() == BulkTransferPanel.class 
					&& !reactivate && getEditState() != EditStateType.TERMINATE){
				//completely separate function, will raise a bulk transfer
				List<BulkAgreementTransferDTO> selections = pageModel.getBulkTransferPanelModel().getSelections();
				controller.raiseBulkAgreementTransferRequest(userProfile, selections);
			}else{			
				if(reactivate){
					controller.reactivateNode(this.pageModel.getHierarchyNodeDTO(),userProfile,
							this.pageModel.getPartyDTOBeforeImage());
				}			
				else if(getEditState() == EditStateType.TERMINATE){
					controller.terminateNode(this.pageModel.getHierarchyNodeDTO(),userProfile,
							this.pageModel.getPartyDTOBeforeImage());	
				}else{
					controller.storeNode(this.pageModel.getHierarchyNodeDTO(),userProfile,
							this.pageModel.getPartyDTOBeforeImage(),PanelToRequestMapping.getMappingForPageAndPanel(MaintainHierarchyPage.class,pageModel.getCurrentTabClass()));	
				}
				//reload context as details have changed			
				try{
					ContextDTO newContextDTO = SRSAuthWebSession.get().getContextDTO().clone();					
					IContextManagement contextBean;
					try {
						contextBean = ServiceLocator.lookupService(IContextManagement.class);
					} catch (NamingException e) {
						throw new CommunicationException(e);
					}			
					//First we check if the node has ended, if it has we load user into context
					boolean ended = true;
					boolean found = false;				
					if(pageModel.getHierarchyNodeDTO().getEffectiveTo() == null || !pageModel.getHierarchyNodeDTO().getEffectiveTo().before(new Date())){
						//node has not ended
						ended = false;
						List<ResultPartyDTO> parties = getHierarchyGUIController().findPartyWithOrganisationNameOfType(pageModel.getHierarchyNodeDTO().getBusinessName(),pageModel.getHierarchyNodeDTO().getType().getOid());
						for(ResultPartyDTO party : parties){				
							if(party.getName().equalsIgnoreCase(pageModel.getHierarchyNodeDTO().getBusinessName())){
								ContextDTO dto = contextBean.getContext(party);
								newContextDTO.setPartyContextDTO(dto.getPartyContextDTO());
								newContextDTO.setAgreementContextDTO(dto.getAgreementContextDTO());							
								found = true;
								break;
							}						
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
						getSession().error("Node stored but could not be put into the context, this could be due to future dating");
					}
					if(ended){
						getSession().info("User set back in context as node is not active anymore");
					}
					SRSAuthWebSession.get().setContextDTO(newContextDTO);				
				} catch (CloneNotSupportedException e) {
					e.printStackTrace();
					getSession().error("Node stored but could not be put into the context");
				} catch (DataNotFoundException e) {
					e.printStackTrace();
					getSession().error("Node stored but could not be put into the context");
				}
			}
			invalidatePage();		
			getSession().info("Record was saved successfully");			
			setResponsePage(new MaintainHierarchyPage(pageModel));
		} catch (ValidationException e) {
			for(String error : e.getErrorMessages()){
				error(error);
			}			
		}	
	}
	
	/**
	 * get an instance of IHierarchyGUIController
	 * @return
	 */
	private IHierarchyGUIController getHierarchyGUIController() {
		if(hierarchyGUIController == null){
			try {
				hierarchyGUIController = ServiceLocator.lookupService(IHierarchyGUIController.class);
			} catch (NamingException e) {
				throw new CommunicationException(e);
			}
		}
		return hierarchyGUIController;		
	}
	
	/**
	 * Will always return true unless the coder has set it to false
	 */
	public boolean isPageDataLoaded() {
		return pageDataLoaded;
	}

	public void setPageDataLoaded(boolean pageDataLoaded) {
		this.pageDataLoaded = pageDataLoaded;
	}

}
