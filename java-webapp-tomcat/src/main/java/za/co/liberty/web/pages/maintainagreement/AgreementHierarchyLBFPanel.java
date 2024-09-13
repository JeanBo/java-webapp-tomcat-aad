package za.co.liberty.web.pages.maintainagreement;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AbstractAjaxTimerBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow.MaskType;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.validation.IFormValidator;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.util.time.Duration;

import za.co.liberty.business.guicontrollers.agreement.IAgreementGUIController;
import za.co.liberty.dto.agreement.AgreementDTO;
import za.co.liberty.dto.agreement.AgreementHomeRoleDTO;
import za.co.liberty.dto.agreement.AgreementRoleDTO;
import za.co.liberty.dto.agreement.maintainagreement.AgreementRoleGridDTO;
import za.co.liberty.dto.agreement.maintainagreement.MaintainAgreementDTO;
import za.co.liberty.dto.agreement.maintainagreement.ValidAgreementValuesDTO;
import za.co.liberty.dto.common.IDValueDTO;
import za.co.liberty.dto.contracting.ResultPartyDTO;
import za.co.liberty.dto.userprofiles.ISessionUserProfile;
import za.co.liberty.exceptions.data.ValidationException;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.helpers.lookup.ServiceLocator;
import za.co.liberty.interfaces.agreements.RoleKindType;
import za.co.liberty.interfaces.agreements.requests.RequestKindType;
import za.co.liberty.srs.type.SRSType;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.data.enums.PanelToRequestMapping;
import za.co.liberty.web.pages.maintainagreement.InverseAgreementRolesPage.InverseAgreementRolesPageType;
import za.co.liberty.web.pages.maintainagreement.RoleHistoryPage.HistoryPageType;
import za.co.liberty.web.pages.maintainagreement.model.MaintainAgreementPageModel;
import za.co.liberty.web.system.SRSAuthWebSession;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGrid;

/**
 * The Panel containing all the agreement hierarchy detail
 * @author MZL2610
 *
 */
public class AgreementHierarchyLBFPanel extends AbstractHomeRoleFactory {
	private static final long serialVersionUID = 1L;
	
	private static final transient Logger logger = Logger.getLogger(AgreementHierarchyLBFPanel.class);
	
	private MaintainAgreementPageModel pageModel; 	
	
	private ModalWindow searchWindow;
	
	/**
	 * The active home role
	 */
//	private AgreementHomeRoleDTO currentHomeRole;
	
	private ResultPartyDTO currentHomeParent;
	
	private ResultPartyDTO currentHomeManager;
	
	private ResultPartyDTO currentHomeParentManager;
	
	/**
	 * The role that is being changed
	 */
	private AgreementRoleDTO currentSearchHomeRole;
	
	private AgreementRoleGridDTO currentSearchAgmtRole;
	
	private SRSDataGrid homeGrid;
	
	private SRSDataGrid agreementRoleGrid;
	
	private List<AgreementRoleGridDTO> agreementRoles;
		
	
	private FeedbackPanel feedBackPanel;	
		
	private ModalWindow historyWindow;
	
	
	//private HomeRoleFactory homeRoleFactory ;
	
	//will be true if there is an existing home request
	private boolean existingHomeRequest;
	
//	will be true if there is an existing servicing request
	private boolean existingServicingRequest;

//	will be true if there is an existing hierarchy request
	private boolean existingHierarchyRequest;	
	
	private HistoryPageType historyType;
	
	private InverseAgreementRolesPageType inverseAgreementRolesPageType;
	
	private boolean initialised;	
	
	private ModalWindow mw;
		
	private transient IAgreementGUIController guiController;
	
	private HashMap<AgreementRoleGridDTO, AgreementGridData> gridData = new HashMap<AgreementRoleGridDTO, AgreementGridData>();
	
	private HashMap<AgreementHomeRoleDTO, HashMap<String,Component>> homeGridComponentMap = new HashMap<AgreementHomeRoleDTO, HashMap<String,Component>>();
	
	private static ArrayList<IDValueDTO> homeTypes = new ArrayList<IDValueDTO>();	
	static{
		//Homes can only be unit and branch for now
		homeTypes.add(new IDValueDTO("Branch",SRSType.BRANCH));
		homeTypes.add(new IDValueDTO("Unit",SRSType.UNIT));		
	}
	 private Page parentPAge;

	
	
	private static String HOME_EXTERNAL_REF_GRID_OBJECT_PROPERTY_NAME = "rolePlayerReference.externalReference";
	
	public AgreementHierarchyLBFPanel(String id, MaintainAgreementPageModel model , 
			EditStateType editState, FeedbackPanel feedBackPanel, Page parentPAge, ModalWindow mw) {
		super(id, editState,parentPAge);
		this.pageModel = model;
		this.feedBackPanel = feedBackPanel;
		this.mw = mw;
		 
	}	
	
	/**
	 * Set the pageModel for this panel
	 * @param model
	 */
	public void setPageModel(MaintainAgreementPageModel model){
		pageModel = model;
	}
	
	/**
	 * Load the components on the page on first render, 
	 * so that the components are only generated when the page is displayed 
	 */
	@Override
	protected void onBeforeRender() {
		if(!initialised) {			
			initialised=true;				
			
//			initialize the page model with the agreement role data
			initPageModel();	
			List<RequestKindType> unAuthRequests = getOutStandingRequestKinds();			
			//check for existing requests FIRST as other panels use variables set here
			
			//after the outstanding requests, we check if user can actually raise requests on left of request kinds
			ISessionUserProfile user = getLoggedInUser();
			
			RequestKindType[] requestsForPanel = PanelToRequestMapping.getRequestKindsForPanel(AgreementHierarchyPanel.class);
			Set<RequestKindType> unAvailableRequest = new HashSet<RequestKindType>(requestsForPanel.length);			
			for(RequestKindType kind : requestsForPanel){
				if(!user.isAllowRaise(kind)){
					unAvailableRequest.add(kind);
				}
			}	
			unAvailableRequest.addAll(unAuthRequests);			
			for (RequestKindType kind : unAvailableRequest) {
				if(kind == RequestKindType.MaintainAgreementHome 
						|| kind == RequestKindType.BranchTransfer){
					existingHomeRequest = true;
				}						
				
				if(kind == RequestKindType.MaintainAgreementServicingRelationships){
					existingServicingRequest = true;
					//remove the isservicedby role from being selectable
					pageModel.getSelectableRoleKinds().remove(RoleKindType.ISSERVICEDBY);
				}
				if(kind == RequestKindType.MaintainAgreementHierarchy){
					existingHierarchyRequest = true;
					pageModel.getSelectableRoleKinds().retainAll(Arrays.asList(new RoleKindType[]{RoleKindType.ISSERVICEDBY, RoleKindType.KEYINDIVIDUAL}));
				}
			}		
			
			
			
			
			add(getFeedBackPanel());
			setHomeRoleFactoryVariables("homeRoleFactory", getEditState(), pageModel, feedBackPanel,  existingHomeRequest, homeTypes, currentHomeRole, homeGridComponentMap);
			add(new HierarchyForm("hierarchyForm"));
			add(searchWindow = createSearchWindow("searchPartyWindow"));				
			add(historyWindow = createHistoryWindow("historyWindow"));					
			
		}
		if(feedBackPanel == null){			
			feedBackPanel = this.getFeedBackPanel();		
		}
		super.onBeforeRender();
	}
	
	/**
	 * Create the history popup
	 * @param id
	 * @return
	 */
	private ModalWindow createHistoryWindow(String id) {
		final ModalWindow window = new ModalWindow(id);
		window.setTitle("History");		
		// Create the page
		window.setPageCreator(new ModalWindow.PageCreator() {
			private static final long serialVersionUID = 1L;
			public Page createPage() {	
				return new RoleHistoryPage(window,pageModel.getMaintainAgreementDTO().getAgreementDTO(),historyType, pageModel.getServicingTypes());
			}			
		});			
		// Initialise window settings
		window.setMinimalHeight(300);
		window.setInitialHeight(300);
		window.setMinimalWidth(600);
		window.setInitialWidth(600);
		window.setMaskType(MaskType.SEMI_TRANSPARENT);
		window.setCssClassName(ModalWindow.CSS_CLASS_GRAY);	
		window.setOutputMarkupId(true);
		window.setOutputMarkupPlaceholderTag(true);
//		window.setPageMapName("RoleHistoryPageMap");
		return window;
	}	

		private ModalWindow createRolesWindow(String id) {		
			final ModalWindow page = new ModalWindow(id);
			page.setTitle("Agreement�s Roleplayer Roles");	
			// Create the page
			page.setPageCreator(new ModalWindow.PageCreator() {
			private static final long serialVersionUID = 1L;
			public Page createPage(){	
				return new InverseAgreementRolesPage(page ,pageModel.getMaintainAgreementDTO().getAgreementDTO(),inverseAgreementRolesPageType,pageModel.getServicingTypes());
	    	}			
			});			
			// Initialise window settings
				page.setMinimalHeight(300);
				page.setInitialHeight(300);
				page.setMinimalWidth(600);
				page.setInitialWidth(800);
				page.setMaskType(MaskType.SEMI_TRANSPARENT);
				page.setCssClassName(ModalWindow.CSS_CLASS_GRAY);	
				page.setOutputMarkupId(true);
				page.setOutputMarkupPlaceholderTag(true);
//				page.setPageMapName("InverseAgreementRolesPageMap");
			return page;
		}
	
	/*
	 * Add in all extra detail to the pagemodel that this panel requires
	 */
	private void initPageModel(){
		if(pageModel == null){
			error("Page Model should never be null, Please call support if you continue seeing this error");
			pageModel = new MaintainAgreementPageModel(new AgreementDTO(),new ValidAgreementValuesDTO()); 
		}				
		if(pageModel.getMaintainAgreementDTO() == null){
			error("An agreement needs to be selected to adjust the hierarchy");
			pageModel.setMaintainAgreementDTO(new MaintainAgreementDTO());
		}
		if(pageModel.getMaintainAgreementDTO().getAgreementDTO() == null){
			error("An agreement needs to be selected to adjust the hierarchy");
			pageModel.getMaintainAgreementDTO().setAgreementDTO(new AgreementDTO());
		}		
		if(pageModel.getMaintainAgreementDTO().getAgreementDTO().getCurrentAndFutureHomeRoles() == null){
			pageModel.getMaintainAgreementDTO().getAgreementDTO().setCurrentAndFutureHomeRoles(new ArrayList<AgreementHomeRoleDTO>());
		}
		if(pageModel.getMaintainAgreementDTO().getAgreementDTO().getCurrentAndFutureAgreementRoles() == null){
			pageModel.getMaintainAgreementDTO().getAgreementDTO().setCurrentAndFutureAgreementRoles(new ArrayList<AgreementRoleDTO>());
		}
//		set the current home role, so all componenents can use it
		setCurrentHomeRole(pageModel.getMaintainAgreementDTO().getAgreementDTO(), null);		
		//set up the servicing types
		IAgreementGUIController agMan = this.getAgreementGUIController();
		
		//set the grid role data
		initGridRoles();	
	}
	
	/**
	 * Run through the agreement roles and set up the grid data
	 *
	 */
	private void initGridRoles(){
		//first see, maybe the grid roles data has already been set up on the pagemodel
		if(pageModel.getGridRoles() == null || pageModel.getGridRoles().size() == 0){			
			List<AgreementRoleGridDTO> gridRoles = new ArrayList<AgreementRoleGridDTO>();
			pageModel.setGridRoles(gridRoles);
			if(pageModel.getMaintainAgreementDTO() != null && pageModel.getMaintainAgreementDTO().getAgreementDTO() != null && pageModel.getMaintainAgreementDTO().getAgreementDTO().getCurrentAndFutureAgreementRoles() != null){
				IAgreementGUIController agreementGUIController = getAgreementGUIController();
				List<AgreementRoleDTO> roles = pageModel.getMaintainAgreementDTO().getAgreementDTO().getCurrentAndFutureAgreementRoles();
				for(AgreementRoleDTO role : roles){
					if(role.getKind() == RoleKindType.PAYSTO.getKind() ||
							role.getKind() == RoleKindType.ISSOLEPROPRIETOR.getKind()){
						//pays to is used in another screen, also used here for defualt on belongs to so we keep this role separate
						//same with Sole proprietor
						//do nothing as the pays to is set up for that tab, if set up on the tab then add it here to display
					}else{
						AgreementRoleGridDTO gridRole = new AgreementRoleGridDTO();
						gridRole.setRole(role);
						//set the other grid display data
						agreementGUIController.setUpAgreementGridRoleData(gridRole);
						gridRoles.add(gridRole);
					}
				}
			}	
		}
		
	}
	
	
	/**
	 * Form used for the panel so we can add validations and on submit method calls
	 * @author DZS2610
	 *
	 */
	public class HierarchyForm extends Form {
		private static final long serialVersionUID = 1L;
		public HierarchyForm(String id) {
			super(id);
			String home = "";
			String parent = "";
			if(currentHomeRole != null && currentHomeRole.getRolePlayerReference() != null 
					&& currentHomeRole.getRolePlayerReference() instanceof ResultPartyDTO 
					&& currentHomeRole.getRolePlayerReference().getOid() > 0){
				ResultPartyDTO homeParty = (ResultPartyDTO)currentHomeRole.getRolePlayerReference();
				home = homeParty.getName() + " (" + homeParty.getHierarchyOrganisationTypeName()+ " " +homeParty.getExternalReference()+")";	
			}	
			if(currentHomeParent != null && currentHomeParent.getOid() > 0){
				parent = currentHomeParent.getName() + " (" + currentHomeParent.getHierarchyOrganisationTypeName()+ " " +currentHomeParent.getExternalReference()+")";	
			}
			//add in home grid and buttons
			add(homeGrid = createHomeGrid("homeList",pageModel.getMaintainAgreementDTO().getAgreementDTO()));			
			add(createRemoveHomeButton("removeFutureHomeButton"));
			add(createAddHomeButton("addFutureHomeButton"));	
			
			add( createHomeHistoryButton("homeHistoryButton"));
			Button addLBFHomeRole = createSaveHomeRole();	
			add(new AbstractAjaxTimerBehavior(Duration.seconds(1)) {			
				@Override
				protected void onTimer(AjaxRequestTarget target) {
					//trigger an AJAX request every three seconds
					if(existingHomeRequest){
						AgreementHierarchyLBFPanel.this.info("There is an existing Home Request that needs authorization");
					}
					target.add(AgreementHierarchyLBFPanel.this.getFeedBackPanel());
					target.add(AgreementHierarchyLBFPanel.this.homeGrid);
					this.stop(target);
//					stop();
				}
			});
			   
			add(addLBFHomeRole);
			
			//add pays to role
			
			String paysTo = "";
			if(pageModel.getMaintainAgreementDTO().getAgreementDTO().getPaymentDetails() != null &&
					pageModel.getMaintainAgreementDTO().getAgreementDTO().getPaymentDetails().getOrgAgreementNumber() != null){
				String paysToName = "";
				try{
					paysToName =  "(" + getAgreementGUIController().findPartyNameForAgreement(pageModel.getMaintainAgreementDTO().getAgreementDTO().getPaymentDetails().getOrgAgreementNumber()).getName() + ")";
				}catch(Exception e){
					//no worries, no name will be displayed
				}
				paysTo = "This agreement pays to agreement number " + pageModel.getMaintainAgreementDTO().getAgreementDTO().getPaymentDetails().getOrgAgreementNumber() + paysToName;
			}
			//add(new Label("paysToRole", paysTo));
			add(new IFormValidator() {
				private static final long serialVersionUID = 1L;

				public void validate(Form arg0) {
					if (getEditState().isViewOnly()) {
						return;
					}
					//validate all the roles 
					
					try {
						validateHomeRole();
						//getAgreementGUIController().validateAgreementRoles(pageModel.getMaintainAgreementDTO().getAgreementDTO().getId(), pageModel.getMaintainAgreementDTO().getAgreementDTO().getKind(),pageModel.getMaintainAgreementDTO().getAgreementDTO().getStartDate(),allCurrentRoles,allPastRoles);
					} catch (ValidationException e) {
						for(String error : e.getErrorMessages()){
							HierarchyForm.this.error(error);	
						}
					}
				}

				public FormComponent[] getDependentFormComponents() {
					return null;
				}
			});
		}			
	}

	private Button createSaveHomeRole() {
		Button addLBFHomeRole = new Button ("addLBFHomeRole");
		 addLBFHomeRole.add(new AjaxFormComponentUpdatingBehavior("click") {
				protected void onUpdate(AjaxRequestTarget target) {
					boolean isValid = true;
					MaintainAgreementPage maintainAgmtPage = (MaintainAgreementPage) AgreementHierarchyLBFPanel.this.getParentPage();
					if(AgreementHierarchyLBFPanel.this.getEditState() == EditStateType.MODIFY){
						try {
							AgreementHierarchyLBFPanel.this.validateHomeRole();
						} catch (ValidationException e1) {
							
							for(String error : e1.getErrorMessages()){
								AgreementHierarchyLBFPanel.this.error(error);	
								
								
							}
					
							isValid = false;
							//target.add(AgreementHierarchyLBFPanel.this.feedBackPanel);
							target.add(AgreementHierarchyLBFPanel.this);
							target.add(AgreementHierarchyLBFPanel.this.getFeedBackPanel());
							//target.add(AgreementHierarchyLBFPanel.this.homeGrid);

						
						}
						//MaintainAgreementPage maintainAgmtPage = (MaintainAgreementPage) SIMSLBFAddPage.this.parentPage;
						if(isValid){
							AgreementHierarchyLBFPanel.this.pageModel.setCurrentTabClass(AgreementHierarchyLBFPanel.class);
							//get if existing requests exist;
							List<RequestKindType> outStandingRequestTypesForPanel = ((MaintainAgreementPage) AgreementHierarchyLBFPanel.this.getParentPage()).getOutStandingRequestTypesForPanel(AgreementHierarchyLBFPanel.class);
							if(outStandingRequestTypesForPanel.size() > 0 && outStandingRequestTypesForPanel.contains(RequestKindType.BranchTransfer)){
								//do not raise request already exists 
							} else {
								if(AgreementHierarchyLBFPanel.this.pageModel.getMaintainAgreementDTO().getAgreementDTO().getCurrentAndFutureHomeRoles().size() > 1){
									maintainAgmtPage.genericRaiseRequest(target);
								}

							}
									
							AgreementHierarchyLBFPanel.this.pageModel.setCurrentTabClass(AgreementDetailsPanel.class);
							try {
								Thread.sleep(2000);
							} catch (InterruptedException e) {
								logger.error("Error occured during Thread sleep",e);
							}
						}
					}
					if(AgreementHierarchyLBFPanel.this.getEditState() == EditStateType.ADD){
//						try {
//							AgreementHierarchyLBFPanel.this.validateHomeRole();
//						} catch (ValidationException e1) {
//							
//							for(String error : e1.getErrorMessages()){
//								AgreementHierarchyLBFPanel.this.error(error);	
//								target.add(AgreementHierarchyLBFPanel.this.feedBackPanel);	
//								
//							}
//					
//							isValid = false;
//						
//						}
						//do not raise an a request
						try {
							getGUIController().validateLBFHomeRole(AgreementHierarchyLBFPanel.this.pageModel.getMaintainAgreementDTO().getAgreementDTO());
						} catch (ValidationException e) {
							//MaintainAgreementPage maintainAgmtPage = (MaintainAgreementPage) SIMSLBFAddPage.this.parentPage;
							for(String error : e.getErrorMessages()){									
								maintainAgmtPage.error(error);									
								target.add(maintainAgmtPage.getFeedbackPanel());
							}
						}
					}
					if(isValid){
						mw.close(target);
					}
					else{
						target.add(AgreementHierarchyLBFPanel.this);
						target.add(AgreementHierarchyLBFPanel.this.getFeedBackPanel());
						//target.add(AgreementHierarchyLBFPanel.this.feedBackPanel);						
					}
				}
//				@Override
//				protected IAjaxCallDecorator getAjaxCallDecorator() {
//					return new BlockUIDecorator();
//				}
			});
		 if(getEditState() == EditStateType.AUTHORISE){			
			 addLBFHomeRole.setVisible(false);
			}
		 addLBFHomeRole.setOutputMarkupId(true);
		 addLBFHomeRole.setOutputMarkupPlaceholderTag(true);
			setUpbutton(addLBFHomeRole,!existingHomeRequest);
		
		return addLBFHomeRole;
	}
	/**
	 * 
	 * @throws ValidationException
	 */
	public void validateHomeRole() throws ValidationException{
		ArrayList<AgreementRoleDTO> allCurrentRoles = new ArrayList<AgreementRoleDTO>(pageModel.getMaintainAgreementDTO().getAgreementDTO().getCurrentAndFutureHomeRoles());
		allCurrentRoles.addAll(pageModel.getMaintainAgreementDTO().getAgreementDTO().getCurrentAndFutureAgreementRoles());
		if(pageModel.getMaintainAgreementDTO().getAgreementDTO().getCurrentAndFutureSupervisorRoles() != null){
			allCurrentRoles.addAll(pageModel.getMaintainAgreementDTO().getAgreementDTO().getCurrentAndFutureSupervisorRoles());
		}
		ArrayList<AgreementRoleDTO> allPastRoles = new ArrayList<AgreementRoleDTO>();
		if(pageModel.getMaintainAgreementDTO().getAgreementDTO().getPastHomeRoles() != null){
			allPastRoles.addAll(pageModel.getMaintainAgreementDTO().getAgreementDTO().getPastHomeRoles());
		}					
		
		try {
			getAgreementGUIController().validateAgreementRoles(pageModel.getMaintainAgreementDTO().getAgreementDTO().getId(), pageModel.getMaintainAgreementDTO().getAgreementDTO().getKind(),pageModel.getMaintainAgreementDTO().getAgreementDTO().getStartDate(),allCurrentRoles,allPastRoles);
		} catch (ValidationException e) {
			throw e;
		}
	}
	/**
	 * Set up the button to be enabled or disabled
	 * @param button
	 */
	private void setUpbutton(Button button, boolean enabled){
		if(!getEditState().isViewOnly() && enabled){
			button.setEnabled(true);
		}else{
			button.setEnabled(false);
		}
	}
	
	/**
	 * Gest the row componenets for the grid row data object
	 * @param data
	 * @return
	 */
	private HashMap<String,Component> getComponentsForHomeGrid(AgreementHomeRoleDTO data){
		HashMap<String,Component> ret = homeGridComponentMap.get(data);
		if(ret == null){
			ret = new HashMap<String, Component>();
			homeGridComponentMap.put(data, ret);
		}
		return ret;
	}
	
	/**
	 * Get the agreement manager
	 * @return
	 */
	private IAgreementGUIController getAgreementGUIController() {
		if(guiController == null){
			try{
				guiController = ServiceLocator.lookupService(IAgreementGUIController.class);
			} catch (NamingException e) {
					throw new CommunicationException(e);
			}	
		}
		return guiController;
	}
	
	/**
	 * Get the logged in user
	 * @return
	 */
	private ISessionUserProfile getLoggedInUser(){
		return SRSAuthWebSession.get().getSessionUser();
	}
	
	/**
	 * Set the feedback panel to use for errors
	 * @param feedBackPanel
	 */
	public void setFeedBackPanel(FeedbackPanel feedBackPanel) {
		this.feedBackPanel = feedBackPanel;
	}

	public Class getPanelClass() {		
		return AgreementHierarchyLBFPanel.class;
	}	
	
	/**
	 * Class to hold extra data about the grid, needed as the models changed in wicket 1.4
	 * @author DZS2610
	 *
	 */
	private class AgreementGridData implements Serializable{	
		private static final long serialVersionUID = 1L;
		private HashMap<String, Component> componentMap = new HashMap<String, Component>();
		private AgreementRoleGridDTO gridData;
		
		public AgreementRoleGridDTO getGridData() {
			return gridData;
		}
		public void setGridData(AgreementRoleGridDTO gridData) {
			this.gridData = gridData;
		}
		
		public void addComponent(String key, Component comp){
			componentMap.put(key, comp);
		}
		
		public Component getComponent(String key){
			return componentMap.get(key);
		}
	}
	
	/**
	 * Create the button to show the home history for this node
	 * 
	 * @return
	 */
	protected Button createHomeHistoryButton(String id) {
		final Button button = new Button(id);
		button.add(new AjaxFormComponentUpdatingBehavior("click") {
			private static final long serialVersionUID = 0L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				historyType = HistoryPageType.HOME;
				historyWindow.show(target);
			}
		});
		if (!getEditState().isViewOnly()|| getEditState() == EditStateType.AUTHORISE) {
			button.setEnabled(false);
			button.setVisible(false);
		}
		button.setOutputMarkupId(true);
		button.setOutputMarkupPlaceholderTag(true);
		return button;
	}
	
	
	private IAgreementGUIController getGUIController() {
		if (guiController==null) {
			/**
			 * Load agreement controller
			 */
			try {
				guiController = ServiceLocator.lookupService(IAgreementGUIController.class);
			} catch (NamingException e) {
				logger.log(Priority.ERROR,"Naming exception looking up Agreement GUI Controller",e);
				throw new CommunicationException("Naming exception looking up Agreement GUI Controller",e);
			}
		}
		return guiController;
	}

	

}
