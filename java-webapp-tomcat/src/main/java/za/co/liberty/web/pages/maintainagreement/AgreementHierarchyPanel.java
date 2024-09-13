package za.co.liberty.web.pages.maintainagreement;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.Vector;

import javax.naming.NamingException;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.SerializationUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow.MaskType;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.validation.IFormValidator;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.cycle.RequestCycle;

import com.inmethod.grid.DataProviderAdapter;
import com.inmethod.grid.IGridColumn;
import com.inmethod.grid.SizeUnit;

import za.co.liberty.agreement.common.exceptions.ProductNotFoundException;
import za.co.liberty.business.guicontrollers.agreement.IAgreementGUIController;
import za.co.liberty.common.domain.TypeVO;
import za.co.liberty.dto.agreement.AgreementDTO;
import za.co.liberty.dto.agreement.AgreementHomeRoleDTO;
import za.co.liberty.dto.agreement.AgreementRoleDTO;
import za.co.liberty.dto.agreement.ConsCodeGenerationDTO;
import za.co.liberty.dto.agreement.maintainagreement.AgreementRoleGridDTO;
import za.co.liberty.dto.agreement.maintainagreement.MaintainAgreementDTO;
import za.co.liberty.dto.agreement.maintainagreement.ValidAgreementValuesDTO;
import za.co.liberty.dto.common.IDValueDTO;
import za.co.liberty.dto.contracting.ResultAgreementDTO;
import za.co.liberty.dto.contracting.ResultPartyDTO;
import za.co.liberty.dto.gui.context.ResultContextItemDTO;
import za.co.liberty.dto.userprofiles.ISessionUserProfile;
import za.co.liberty.exceptions.data.DataNotFoundException;
import za.co.liberty.exceptions.data.ValidationException;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.helpers.config.HelperConfigParameterTypes;
import za.co.liberty.helpers.config.HelpersParameterFactory;
import za.co.liberty.helpers.lookup.ServiceLocator;
import za.co.liberty.helpers.persistence.TemporalityHelper;
import za.co.liberty.helpers.util.ComparatorUtil;
import za.co.liberty.helpers.util.DateUtil;
import za.co.liberty.interfaces.advancedPractice.GridName;
import za.co.liberty.interfaces.agreements.AgreementKindType;
import za.co.liberty.interfaces.agreements.ProjectBaseType;
import za.co.liberty.interfaces.agreements.RoleKindType;
import za.co.liberty.interfaces.agreements.requests.RequestKindType;
import za.co.liberty.interfaces.party.PartyType;
import za.co.liberty.srs.type.SRSType;
import za.co.liberty.web.data.enums.ContextType;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.data.enums.PanelToRequestMapping;
import za.co.liberty.web.pages.advancedPractice.AdvRoleHistoryPage;
import za.co.liberty.web.pages.advancedPractice.AdvRoleHistoryPage.HistoryType;
import za.co.liberty.web.pages.interfaces.IHasAccessPanel;
import za.co.liberty.web.pages.interfaces.ISecurityPanel;
import za.co.liberty.web.pages.maintainagreement.InverseAgreementRolesPage.InverseAgreementRolesPageType;
import za.co.liberty.web.pages.maintainagreement.RoleHistoryPage.HistoryPageType;
import za.co.liberty.web.pages.maintainagreement.model.MaintainAgreementPageModel;
import za.co.liberty.web.pages.panels.BasePanel;
import za.co.liberty.web.pages.panels.HelperPanel;
import za.co.liberty.web.pages.search.ContextSearchPopUp;
import za.co.liberty.web.system.SRSAuthWebSession;
import za.co.liberty.web.wicket.markup.html.form.SRSDateField;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGrid;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGrid.GridSizeUnit;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGridColumn;
import za.co.liberty.web.wicket.markup.html.grid.SRSGridRowSelectionCheckBox;
import za.co.liberty.web.wicket.markup.repeater.data.ListDataProvider;

/**
 * The Panel containing all the agreement hirarchy detail
 * @author DZS2610
 *
 */
public class AgreementHierarchyPanel extends BasePanel implements ISecurityPanel,IHasAccessPanel {
	private static final long serialVersionUID = 1L;
	
	private static final Logger logger = Logger.getLogger(AgreementHierarchyPanel.class);
	
	private MaintainAgreementPageModel pageModel; 	
	
	private ModalWindow searchWindow;
	
	private HistoryType currentHistoryType = HistoryType.MANAGER;
	
	/**
	 * The active home role
	 */
	private AgreementHomeRoleDTO currentHomeRole;
	
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
	private Label homeTypeLabel;	
	private Label homeManagerLabel;
	private Label homeParentTypeLabel;	
	private Label homeParentManagerLabel;	
	private ModalWindow historyWindow;
	private ModalWindow rolesPopupWindow;
	private ModalWindow advancedPracticeRolesWindow;
	
	//will be true if there is an existing home request
	private boolean existingHomeRequest;
	
//	will be true if there is an existing servicing request
	private boolean existingServicingRequest;

//	will be true if there is an existing hierarchy request
	private boolean existingHierarchyRequest;	
	
	private HistoryPageType historyType;
	
	private InverseAgreementRolesPageType inverseAgreementRolesPageType;
	
	private boolean initialised;	
		
	private transient IAgreementGUIController guiController;
	
	private HashMap<AgreementRoleGridDTO, AgreementGridData> gridData = new HashMap<AgreementRoleGridDTO, AgreementGridData>();
	
	private HashMap<AgreementHomeRoleDTO, HashMap<String,Component>> homeGridComponentMap = new HashMap<AgreementHomeRoleDTO, HashMap<String,Component>>();
	
	private static ArrayList<IDValueDTO> homeTypes = new ArrayList<IDValueDTO>();	
	static{
		//Homes can only be unit and branch for now
		homeTypes.add(new IDValueDTO("Branch",SRSType.BRANCH));
		homeTypes.add(new IDValueDTO("Unit",SRSType.UNIT));		
	}
	
	private static String HOME_EXTERNAL_REF_GRID_OBJECT_PROPERTY_NAME = "rolePlayerReference.externalReference";
	
	private boolean filterRoleKinds; 	
	
	public AgreementHierarchyPanel(String id, MaintainAgreementPageModel model , 
			EditStateType editState, FeedbackPanel feedBackPanel, Page parentPAge) {
		super(id, editState,parentPAge);
		this.pageModel = model;
		this.feedBackPanel = feedBackPanel;
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
//			if(pageModel.getMaintainAgreementDTO().getAgreementDTO().getCurrentAndFutureAgreementRoles() == null){
//				pageModel.getMaintainAgreementDTO().getAgreementDTO().setCurrentAndFutureAgreementRoles(new ArrayList<AgreementRoleDTO>());
//			}
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
			/*SSM2707 Sweta Menon Market Integration Begin*/
			/*
			 * The market Roles are not maintainable from the Agreement
			 * Hierarchy screen. These roles can only be maintained via the BC
			 * Linking screen. Therefore we take them out of the Selectable Role
			 * Kind list.
			 */
			pageModel.getSelectableRoleKinds().remove(RoleKindType.HASSALESPANEL);
			pageModel.getSelectableRoleKinds().remove(RoleKindType.HASSUPPORTPANEL);
			pageModel.getSelectableRoleKinds().remove(RoleKindType.HASSERVICINGPANEL);
			/*SSM2707 Sweta Menon Market Integration End*/
			add(new HierarchyForm("hierarchyForm"));
			add(searchWindow = createSearchWindow("searchPartyWindow"));				
			add(historyWindow = createHistoryWindow("historyWindow"));
			add(rolesPopupWindow = createRolesWindow("rolesPopupWindow"));
			add(advancedPracticeRolesWindow = getAdvancedPracticeRoleWindow("advancedPracticeRolesWindow" ,HistoryType.MANAGERROLE));
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
//		page.setPageMapName("InverseAgreementRolesPageMap");
	return page;
}
	
	/**
	 * Create the history popup
	 * @param id
	 * @return
	 */
	private ModalWindow getAdvancedPracticeRoleWindow(String id, final HistoryType type) {
		final ModalWindow page = new ModalWindow(id);
		page.setTitle("Advanced Practice Roles");		
		// Create the page
		page.setPageCreator(new ModalWindow.PageCreator() {
			private static final long serialVersionUID = 10L;
			public Page createPage() {	
				return new AdvRoleHistoryPage(page,pageModel,type); 
				
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
//		page.setPageMapName("advancedPracticeRolePageMap");
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
		 try {
			 pageModel.setServicingTypes(agMan.getServicingTypeList());
		} catch (DataNotFoundException e) {
			 logger.warn(e);
			 pageModel.setServicingTypes(new ArrayList<TypeVO>());
		}
		 try{
			 pageModel.setBusinessStakeHolderTypes(agMan.getBusinessStakeHolderTypeList());
		 }catch(DataNotFoundException e)
		 {
			 pageModel.setBusinessStakeHolderTypes(new ArrayList<TypeVO>());
		 }
		 try {
			 Collection<RoleKindType> rolesTypes = agMan.getAllowableRoleTypesForAgreement(pageModel.getMaintainAgreementDTO().getAgreementDTO().getSpecificationId(), ProjectBaseType.AGREEMENT);
			 //remove pays to roles as this is maintained elsewhere
			 rolesTypes.remove(RoleKindType.PAYSTO);
			 rolesTypes.remove(RoleKindType.HASBUSINESSSTAKEHOLDER);
			 //remove issoleproprietor roles as this is maintained elsewhere
			 rolesTypes.remove(RoleKindType.ISSOLEPROPRIETOR);
			 pageModel.setSelectableRoleKinds(new ArrayList<RoleKindType>(rolesTypes));
			 //TODO temp for REPORTS TO ROLE, remove when changed
			 pageModel.getSelectableRoleKinds().add(RoleKindType.REPORTSTO);
			 
		} catch (ProductNotFoundException e) {
			//no problem, list will be empty
			 logger.warn(e);
			 pageModel.setSelectableRoleKinds(new ArrayList<RoleKindType>());
		} 
		 
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
							role.getKind() == RoleKindType.ISSOLEPROPRIETOR.getKind()
							/* Market Integration SSM2707 Sweta Menon Begin */
							|| role.getKind() == RoleKindType.HASSERVICINGPANEL
									.getKind()
							|| role.getKind() == RoleKindType.HASSALESPANEL
									.getKind()
							|| role.getKind() == RoleKindType.HASSUPPORTPANEL
									.getKind()) {
						/* Market Integration SSM2707 Sweta Menon End */
						/*
						 * HASSERVICINGPANEL, HASSALESPANEL and HASSUPPORTPANEL
						 * roles are maintained on the BC linking screen and via
						 * [Create Agreement,Branch Transfer and Terminate
						 * Agreements]. This role will not be displayed on this
						 * screen.
						 */
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
	 * Will look at the current home end date and ajust the start date of the future home
	 * @param target
	 */
	private void adjustFutureHomeStartDate(AjaxRequestTarget target){
			if(currentHomeRole != null && currentHomeRole.getEffectiveTo() != null){
				for(AgreementHomeRoleDTO role : pageModel.getMaintainAgreementDTO().getAgreementDTO().getCurrentAndFutureHomeRoles()){
					if(role != currentHomeRole){
						role.setEffectiveFrom(currentHomeRole.getEffectiveTo());
						Component comp = getComponentsForHomeGrid(role).get("effectiveFrom");
						if(comp != null & comp instanceof HelperPanel && ((HelperPanel)comp).getEnclosedObject() instanceof TextField){
							TextField txt = (TextField)((HelperPanel)comp).getEnclosedObject();					
							txt.setModelObject(role.getEffectiveFrom());
							txt.modelChanged();
							if(target != null){								
								target.add(comp);
								
							}
						}
						break;
					}
				}
			}	
	}
	
	
	/**	
	 * Will look at the current home end date and ajust the start date of the future home
	 * @param target
	 */
	private void adjustCurrentHomeEndDate(AjaxRequestTarget target, AgreementHomeRoleDTO role){
			if(role != null && role.getEffectiveFrom() != null){
				currentHomeRole.setEffectiveTo(role.getEffectiveTo());
				if(target != null){							
					//rather update indiv comp
					//update the date comp only		
					Component comp = getComponentsForHomeGrid(currentHomeRole).get("effectiveTo");
					if(comp != null & comp instanceof HelperPanel && ((HelperPanel)comp).getEnclosedObject() instanceof TextField){
						TextField txt = (TextField)((HelperPanel)comp).getEnclosedObject();
						txt.setModelObject(currentHomeRole.getEffectiveTo());
						txt.modelChanged();
						if(target != null){
							target.add(comp);
						}
					}		
				}
			}	
	}

	
	/**
	 * Go through the home roles and set the current home role<br/>
	 * This will also set all the page labels to refresh if ajax target is not null
	 * @param dto
	 */
	private void setCurrentHomeRole(AgreementDTO dto, AjaxRequestTarget target){
		//set all objects to blank to display blank		
		if(currentHomeRole != null && currentHomeRole.getRoleID() > 0){
//			skip if currentHomeRole has already been selected and the current home id is not 0
			return;
		}		
		 Date now = TemporalityHelper.getInstance().getNewNOWDateWithNoTime();
		 currentHomeRole = new AgreementHomeRoleDTO();
		 currentHomeRole.setRolePlayerReference(new ResultPartyDTO());			
		 currentHomeParent = new ResultPartyDTO();			
		 currentHomeManager = new ResultPartyDTO();			
		 currentHomeParentManager = new ResultPartyDTO();
		if(dto.getCurrentAndFutureHomeRoles() != null && dto.getCurrentAndFutureHomeRoles().size() > 0){
			for(AgreementHomeRoleDTO role : dto.getCurrentAndFutureHomeRoles()){
				Date effectiveFrom = DateUtil.getInstance().getDatePart(role.getEffectiveFrom());
				if(effectiveFrom.compareTo(now) <= 0 && (role.getEffectiveTo() == null || role.getEffectiveTo().after(now))){
					//found
					currentHomeRole = role;
					//now we need to set the parent details
					//get the parent
					ResultPartyDTO node = (ResultPartyDTO) role.getRolePlayerReference();
					if(node != null && node.getOid() > 0){
						try {						
							currentHomeParent = getAgreementGUIController().findParentOfHierarchyNode(node.getOid(),node.getTypeOid());					
						} catch (DataNotFoundException e) {
							//do nothing, will just not be shown
						}
						try {						
							currentHomeManager = getAgreementGUIController().findHierarchyNodeManager(node.getOid(),node.getTypeOid());
						} catch (DataNotFoundException e) {
							//do nothing, will just not be shown
						}
						if(currentHomeParent != null && currentHomeParent.getOid() > 0){
							try {
								currentHomeParentManager = getAgreementGUIController().findHierarchyNodeManager(currentHomeParent.getOid(),currentHomeParent.getTypeOid());
							} catch (DataNotFoundException e) {
								//do nothing, will just not be shown
							}
						}
					}
				}
			}
		}
		if(target != null){			
			String home = "";
			String parent = "";
			String consCode = null;
			if(currentHomeRole != null && currentHomeRole.getRolePlayerReference() != null && currentHomeRole.getRolePlayerReference() instanceof ResultPartyDTO
					&& currentHomeRole.getRolePlayerReference().getOid() > 0){
				ResultPartyDTO homeParty = (ResultPartyDTO)currentHomeRole.getRolePlayerReference();
				home = homeParty.getName() + " (" + homeParty.getHierarchyOrganisationTypeName()+ " " +homeParty.getExternalReference()+")";
				
				//Added SBS0510
				if(isAutoGenEnabledAndADDAction()) {
					consCode = autoGenConsultantCode(); 
				}					
			}	
			if(currentHomeParent != null && currentHomeParent.getOid() > 0){
				parent = currentHomeParent.getName() + " (" + currentHomeParent.getHierarchyOrganisationTypeName()+ " " +currentHomeParent.getExternalReference()+")";	
			}
			
			Label homeTypeLabel1 = (Label) new Label("currentHome",home).setOutputMarkupId(true).setOutputMarkupPlaceholderTag(true);
			Label homeManagerLabel1 = (Label)new Label("currentHomeManager",new PropertyModel(currentHomeManager,"name")).setOutputMarkupId(true).setOutputMarkupPlaceholderTag(true);
			Label homeParentTypeLabel1 = (Label)new Label("currentHomeParent",parent).setOutputMarkupId(true).setOutputMarkupPlaceholderTag(true);			
			Label homeParentManagerLabel1 = (Label)new Label("currentHomeParentManager",new PropertyModel(currentHomeParentManager,"name")).setOutputMarkupId(true).setOutputMarkupPlaceholderTag(true);
			homeTypeLabel.replaceWith(homeTypeLabel1);
			homeTypeLabel = homeTypeLabel1;			
			homeManagerLabel.replaceWith(homeManagerLabel1);
			homeManagerLabel = homeManagerLabel1;
			homeParentTypeLabel.replaceWith(homeParentTypeLabel1);
			homeParentTypeLabel = homeParentTypeLabel1;			
			homeParentManagerLabel.replaceWith(homeParentManagerLabel1);
			homeParentManagerLabel = homeParentManagerLabel1;			
			target.add(homeTypeLabel);
			target.add(homeManagerLabel);
			target.add(homeParentTypeLabel);
			target.add(homeParentManagerLabel);
			
			if(!StringUtils.isBlank(consCode)) {				
				target.add(homeGrid);
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
			//add in the home display fields	
			
//			add(consultantCodePanel = getConsultantCodePanel());
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
			add(homeTypeLabel = (Label) new Label("currentHome",home).setOutputMarkupId(true).setOutputMarkupPlaceholderTag(true));
			add(homeManagerLabel = (Label)new Label("currentHomeManager",new PropertyModel(currentHomeManager,"name")).setOutputMarkupId(true).setOutputMarkupPlaceholderTag(true));
			
			add(homeParentTypeLabel = (Label)new Label("currentHomeParent",parent).setOutputMarkupId(true).setOutputMarkupPlaceholderTag(true));
			add(homeParentManagerLabel = (Label)new Label("currentHomeParentManager",new PropertyModel(currentHomeParentManager,"name")).setOutputMarkupId(true).setOutputMarkupPlaceholderTag(true));
			//add in home grid and buttons
			add(homeGrid = createHomeGrid("homeList",pageModel.getMaintainAgreementDTO().getAgreementDTO()));			
			add(createRemoveHomeButton("removeFutureHomeButton"));
			add(createAddHomeButton("addFutureHomeButton"));	
			
			//add in the other agreement role grid
			add(agreementRoleGrid = createAgreementRoleGrid("agreementRoleList",pageModel));			
			add(createRemoveAgreementRoleButton("removeAgreementRoleButton"));
			add(createAddAgreementRoleButton("addAgreementRoleButton"));	

//			Add in the supervision roles grid
			add(new OtherPartyRolesPanel("otherPartyRoles",getEditState(),getParentPage(),pageModel,true));
			///add(agreementSupervisionRoleGrid = createAgreementSupervisionRoleGrid("agreementSupervisionRoleList",pageModel));

			
			add(createHomeHistoryButton("homeHistoryButton"));
			add(createOtherRolesHistoryButton("agreementRoleHistoryButton"));
			add(createOtherAgmtRolesHistoryButton("otherAgreementRolesHistoryButton"));
			add(createAdvancedPracticeRolesButton("advncedPrcaticeRoles",GridName.MANAGERROLE));
			
			
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
			add(new Label("paysToRole", paysTo));
			add(new IFormValidator() {
				private static final long serialVersionUID = 1L;

				public void validate(Form arg0) {
					logger.info("AgreementHierarchyPanel.validate");
					if (getEditState().isViewOnly()) {
						return;
					}
					
//					if (getEditState().isAdd()) {
//						validateFormComponents(validationComponents, feedbackPanel)
//					}
					
					//validate all the roles 
					ArrayList<AgreementRoleDTO> allCurrentRoles = new ArrayList<AgreementRoleDTO>(pageModel.getMaintainAgreementDTO().getAgreementDTO().getCurrentAndFutureHomeRoles());
					allCurrentRoles.addAll(pageModel.getMaintainAgreementDTO().getAgreementDTO().getCurrentAndFutureAgreementRoles());
					if(pageModel.getMaintainAgreementDTO().getAgreementDTO().getCurrentAndFutureSupervisorRoles() != null){
						allCurrentRoles.addAll(pageModel.getMaintainAgreementDTO().getAgreementDTO().getCurrentAndFutureSupervisorRoles());
					}
					ArrayList<AgreementRoleDTO> allPastRoles = new ArrayList<AgreementRoleDTO>();
					if(pageModel.getMaintainAgreementDTO().getAgreementDTO().getPastHomeRoles() != null){
						allPastRoles.addAll(pageModel.getMaintainAgreementDTO().getAgreementDTO().getPastHomeRoles());
					}					
					if(pageModel.getMaintainAgreementDTO().getAgreementDTO().getPastAgreementRoles() != null){
						allPastRoles.addAll(pageModel.getMaintainAgreementDTO().getAgreementDTO().getPastAgreementRoles());
					}
					if(pageModel.getMaintainAgreementDTO().getAgreementDTO().getPastSupervisorRoles() != null){
						allPastRoles.addAll(pageModel.getMaintainAgreementDTO().getAgreementDTO().getPastSupervisorRoles());
					}
					
					try {

						getAgreementGUIController().validateSalesCategory(pageModel.getMaintainAgreementDTO().getAgreementDTO().getSalesCategory());
						getAgreementGUIController().validateAgreementRoles(pageModel.getMaintainAgreementDTO().getAgreementDTO().getId(), pageModel.getMaintainAgreementDTO().getAgreementDTO().getKind(),pageModel.getMaintainAgreementDTO().getAgreementDTO().getStartDate(),allCurrentRoles,allPastRoles);
					    
						if(isAutoGenEnabledAndADDAction()) { 
							
							if (pageModel.getRemoveHomeRoleKindType() != null) {
								HierarchyForm.this.error("Remove the home role and add it again");
							} else {
								// If not Auto Gen the don't validate
								try {
									getAgreementGUIController().validateForMiddleSix(pageModel.getMaintainAgreementDTO().getAgreementDTO(), allCurrentRoles);		
								} catch (ValidationException e) {
									pageModel.setRemoveHomeRoleKindType( 
									  getAgreementGUIController().getApplicableRoleKindTypeForAgmtKind(
											  pageModel.getMaintainAgreementDTO().getAgreementDTO().getKind()));
									logger.info("After exception - RemoveRoleKind = " + pageModel.getRemoveHomeRoleKindType() + "   agreementDto kind " 
											+ pageModel.getMaintainAgreementDTO().getAgreementDTO().getKind()
											+ "  this " + this);
									throw e;
								}	
							}
						}
						validateForRoleOnMaintain(allCurrentRoles);
						
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
	
	/**
	 * Create the button to show the other agreement roles history for this node
	 * 
	 * @return
	 */
	private Button createOtherRolesHistoryButton(String id) {
		final Button button = new Button(id);
		button.add(new AjaxFormComponentUpdatingBehavior("click") {
			private static final long serialVersionUID = 0L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				historyType = HistoryPageType.OTHER;
				historyWindow.show(target);
				
			}
		});
		if (!getEditState().isViewOnly() 
				|| getEditState() == EditStateType.AUTHORISE) {
			button.setEnabled(false);
			button.setVisible(false);
		}		
		button.setOutputMarkupId(true);
		button.setOutputMarkupPlaceholderTag(true);
		return button;
	}
	/**
	 * Create the button to show the other agreement roles history for this node
	 * 
	 * @return
	 */
	private Button createOtherAgmtRolesHistoryButton(String id) {
		final Button button = new Button(id);
		button.add(new AjaxFormComponentUpdatingBehavior("click") {
			private static final long serialVersionUID = 0L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				inverseAgreementRolesPageType = InverseAgreementRolesPageType.HOME;
				rolesPopupWindow.show(target);
				
			}
		});
		if (!getEditState().isViewOnly() || getEditState() == EditStateType.AUTHORISE) {
			button.setEnabled(false);
			button.setVisible(false);
		}
		button.setOutputMarkupId(true);
		button.setOutputMarkupPlaceholderTag(true);
		return button;
	}
	
	
	/**
	 * Create the button to show the manager history for this pratice
	 * 
	 * @return
	 */
	private Button createAdvancedPracticeRolesButton(String id, final GridName roleKind) {
		final Button button = new Button(id);
		
		button.add(new AjaxFormComponentUpdatingBehavior("click") {
			private static final long serialVersionUID = 0L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				if(roleKind != null && roleKind.equals(GridName.MANAGERROLE)){
					currentHistoryType = HistoryType.MANAGERROLE;
					advancedPracticeRolesWindow.show(target);
				}else{
					currentHistoryType = HistoryType.MEMBERROLE;
					advancedPracticeRolesWindow.show(target);
				}
			}
		});
		if(getEditState() == EditStateType.AUTHORISE){
			button.setVisible(false);
		}else if (!getEditState().isViewOnly() && getEditState() != EditStateType.TERMINATE) {
			button.setEnabled(false);
			button.setVisible(false);
		}
		button.setOutputMarkupId(true);
		button.setOutputMarkupPlaceholderTag(true);
		return button;
	}
	
	/**
	 * Create the button to show the home history for this node
	 * 
	 * @return
	 */
	private Button createHomeHistoryButton(String id) {
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
	
	/**
	 * Create the button to add a home
	 * 
	 * @return
	 */
	private Button createAddAgreementRoleButton(String id) {
		final Button button = new Button(id);
		button.add(new AjaxFormComponentUpdatingBehavior("click") {
			private static final long serialVersionUID = 0L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {	
				
				if(!validateOnAddNewRole(target)) {
					
					AgreementRoleDTO dto = new AgreementRoleDTO();
					dto.setType(SRSType.MANAGESAGREEMENTROLE);
					dto.setRolePlayerReference(new ResultPartyDTO());
					AgreementRoleGridDTO gridRole = new AgreementRoleGridDTO();
					gridRole.setRole(dto);
					pageModel.addGridRole(gridRole);
					target.add(agreementRoleGrid);	
				}
			}
		});		
		if(getEditState() == EditStateType.AUTHORISE){			
			button.setVisible(false);
		}
		button.setOutputMarkupId(true);
		button.setOutputMarkupPlaceholderTag(true);
		setUpbutton(button, !(existingServicingRequest && existingHierarchyRequest));
		return button;
	}
	
	/**
	 * Create the button to remove a home
	 * 
	 * @return
	 */
	private Button createRemoveAgreementRoleButton(String id) {
		final Button button = new Button(id);
		button.add(new AjaxFormComponentUpdatingBehavior("click") {
			private static final long serialVersionUID = 0L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				refreshFeedbackPanel(target);
				List<Object> selections = agreementRoleGrid.getSelectedItemObjects();
				RoleKindType roleKindType = getAgreementGUIController()
						.getApplicableRoleKindTypeForAgmtKind(pageModel.getMaintainAgreementDTO()
								.getAgreementDTO().getKind());
				for (Object selection : selections) {					
					AgreementRoleGridDTO gridRole = (AgreementRoleGridDTO) selection;
					if(roleKindType.getKind() == gridRole.getRole().getKind()) {
						//SBS0510 - Azure Bug 182926 fix
						if(validateForHasHomeAlreadySelected()) {
							warn("Warning: \'HAS HOME\' and Midsix are linked to each other so '"+roleKindType.getDescription()+"' can not be removed !");

							refreshFeedbackPanel(target);				
							return;
						}					
						
					}
					
					pageModel.removeGridRole(gridRole);	
				}					
				target.add(agreementRoleGrid);
			}
		});		
		if(getEditState() == EditStateType.AUTHORISE){			
			button.setVisible(false);
		}
		button.setOutputMarkupId(true);
		button.setOutputMarkupPlaceholderTag(true);
		setUpbutton(button,!(existingServicingRequest && existingHierarchyRequest));
		return button;
	}
	
	/**
	 * Create the button to add a home
	 * 
	 * @return
	 */
	private Button createAddHomeButton(String id) {
		final Button button = new Button(id);
		button.add(new AjaxFormComponentUpdatingBehavior("click") {
			private static final long serialVersionUID = 0L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
								
				//Added SBS0510 - Check if Edit state is ADD and is New Organisation
				//Azure Bug fix - 186278-Validation only for ADD action
				
				if(isAutoGenEnabledAndADDAction()) {
					
					if (!addHomeForAutoGen(target)) {
						// Only return false
						return;
					}
							
				}			
					
				
				if (pageModel.getMaintainAgreementDTO().getAgreementDTO().getCurrentAndFutureHomeRoles().size() < 2) {					
					AgreementHomeRoleDTO dto = new AgreementHomeRoleDTO();
					if(pageModel.getMaintainAgreementDTO().getAgreementDTO().getCurrentAndFutureHomeRoles().size() == 0){
						dto.setEffectiveFrom(pageModel.getMaintainAgreementDTO().getAgreementDTO().getStartDate());
					}else if(currentHomeRole != null){
						//set current home end date
						currentHomeRole.setEffectiveTo(new Date());
					}
					if (isAutoGenEnabledAndADDAction()) {// Azure Bug fix - 189902
						dto.setFirstThree(pageModel.getMaintainAgreementDTO().getAgreementDTO().getFirstThree());
						dto.setMiddleSix(pageModel.getMaintainAgreementDTO().getAgreementDTO().getMiddleSix());
						dto.setLastFour(pageModel.getMaintainAgreementDTO().getAgreementDTO().getLastFour());
						dto.setConsultantCode(pageModel.getMaintainAgreementDTO().getAgreementDTO().getConsultantCode());
					}
					dto.setKind(new Long(RoleKindType.HASHOME.getKind()));
					dto.setAgreementRoleKind(new Long(RoleKindType.HASHOME.getKind()));
					dto.setType(SRSType.PARTYAGREEMENTROLE);
					dto.setRolePlayerReference(new ResultPartyDTO());
					dto.setAgreementNumber(pageModel.getMaintainAgreementDTO().getAgreementDTO().getId());
					pageModel.getMaintainAgreementDTO().getAgreementDTO().getCurrentAndFutureHomeRoles().add(dto);	
					adjustFutureHomeStartDate(target);
					target.add(homeGrid);
				} else {
					error("Only one future home is allowed");
					refreshFeedbackPanel(target);	
				}
			}
		});		
		if(getEditState() == EditStateType.AUTHORISE){			
			button.setVisible(false);
		}
		button.setOutputMarkupId(true);
		button.setOutputMarkupPlaceholderTag(true);
		setUpbutton(button,!existingHomeRequest);
		return button;
	}
	
	/**
	 * When auto gen is valid call this method on add of home.  Ensure this is only called for autogen
	 */
	protected boolean addHomeForAutoGen(AjaxRequestTarget target) {
		
		if(!isAutoGenEnabledAndADDAction()) {
			// Double checking this
			return false;
		}
		
		// Initialise
		boolean isOrganisation = isPartyOrganisation();				
		RoleKindType roleKindType = getAgreementGUIController().getApplicableRoleKindTypeForAgmtKind(
				pageModel.getMaintainAgreementDTO().getAgreementDTO().getKind());
		
		// Retrieve the role kind for this agreement kind  (broker = belongsTo, franchise = isManagedBy etc.)		
		List<AgreementRoleDTO> agreementRolesDTO = getRolePlayerForRole(roleKindType);						
		
					
		/*
		 * Do organisation specific
		 */
		if(isOrganisation) {	
			// jzb0608 - Unsure if this should be set here
			pageModel.getMaintainAgreementDTO().getAgreementDTO().setMiddleSixReserved(true);

			//Validate if only 1 Has Home role exists for Organisation
			if(pageModel.getMaintainAgreementDTO().getAgreementDTO().getCurrentAndFutureHomeRoles().size() == 1) {
				warn("Warning: Please note there can only be One 'HAS HOME' Role");
				refreshFeedbackPanel(target);						
				return false;				
			}
			return true;
		}
			
		/*
		 * Do individual specific.   Validation first
		 */
		//Check if Role ( Belongs_to / Reports To/Managed By) is present for Party
		if(CollectionUtils.isEmpty(agreementRolesDTO)) {
			warn("Warning: Please load a '"+roleKindType.getDescription()+"' Role first");
			refreshFeedbackPanel(target);					
			return false;
		}
		
		//Check if Role ( Belongs_to / Reports To/Managed By) is present Only ONCE for Party. No duplicates
		//Prevent midsix and lastfour generation

		if(agreementRolesDTO.size() > 1) {
			warn("Warning: Please note there can only be One '"+roleKindType.getDescription()+"' Role");
			refreshFeedbackPanel(target);						
			return false;
		}	
		
		//Validate if only 1 Has Home role exists for Person. Prevent midsix and lastfour generation
		if(pageModel.getMaintainAgreementDTO().getAgreementDTO().getCurrentAndFutureHomeRoles().size() == 1) {
			warn("Warning: Please note there can only be One 'HAS HOME' Role");
			refreshFeedbackPanel(target);						
			return false;				
		}				
		
		
		try {						
			//Get Belongs_to / Reports To/Managed By agreement
			AgreementRoleDTO agreementRoleDTO = agreementRolesDTO.get(0);
			
			
			// Get the mid six from the anchor
			ConsCodeGenerationDTO midSixDTO = null;
			try {
				midSixDTO = getAgreementGUIController().getMidSixFromAnchor(
						pageModel.getMaintainAgreementDTO().getAgreementDTO(), agreementRoleDTO);
			} catch (ValidationException e) {
				error(e.getFirstErrorMessage());
				refreshFeedbackPanel(target);	
				return false;
			}
			
			if(Objects.nonNull(midSixDTO)) {
				pageModel.getMaintainAgreementDTO().getAgreementDTO().setMiddleSix(midSixDTO.getCode());
			}
			
			AgreementDTO rolePlayerAgmt  = new AgreementDTO();						
			rolePlayerAgmt.setId(agreementRoleDTO.getRolePlayerReference().getOid());
			rolePlayerAgmt.setConsultantCode(((ResultAgreementDTO)agreementRoleDTO.getRolePlayerReference()).getConsultantCodeFormatted());
			rolePlayerAgmt.setKind(pageModel.getMaintainAgreementDTO().getAgreementDTO().getKind());
			ConsCodeGenerationDTO lastFourDTO =  getAgreementGUIController().getLastFour(
					rolePlayerAgmt, isOrganisation ? PartyType.ORGANISATION : PartyType.PERSON, midSixDTO);

			if(Objects.nonNull(lastFourDTO)) {
				pageModel.getMaintainAgreementDTO().getAgreementDTO().setLastFour(lastFourDTO.getCode());
				pageModel.getMaintainAgreementDTO().getAgreementDTO().setLastFourReserved(rolePlayerAgmt.isLastFourReserved());
			}							

		}
		catch (CommunicationException | DataNotFoundException e) {
			error(e.getMessage());
			
			refreshFeedbackPanel(target);					
			return false;
		}						

		return true;
	}

	/**
	 * Create the button to remove a home
	 * 
	 * @return
	 */
	private Button createRemoveHomeButton(String id) {
		final Button button = new Button(id);
		button.add(new AjaxFormComponentUpdatingBehavior("click") {
			private static final long serialVersionUID = 0L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				List<Object> selections = homeGrid.getSelectedItemObjects();	
				
				if (logger.isDebugEnabled())
					logger.debug("removeHome - isAutoGenEnabledAndADDAction =" + isAutoGenEnabledAndADDAction()
							+ "  removeRoleKindType=" + pageModel.getRemoveHomeRoleKindType()
							+ "  isOrganisation= " + isPartyOrganisation()
							+ " this=" + this);
				if (isAutoGenEnabledAndADDAction() && pageModel.getRemoveHomeRoleKindType() != null) {
					logger.info("Removing home role");
					pageModel.getMaintainAgreementDTO().getAgreementDTO().getCurrentAndFutureHomeRoles().clear();
					refreshFeedbackPanel(target);	
					currentHomeRole = null;
					target.add(homeGrid);
					pageModel.setRemoveHomeRoleKindType(null);
					return;
				}
				

				
				boolean found = false;
				if(currentHomeRole != null){
					for(AgreementRoleDTO home : pageModel.getMaintainAgreementDTO().getAgreementDTO().getCurrentAndFutureHomeRoles()){
						if(currentHomeRole == home){
							found = true;
						}
					}
				} else if (pageModel.getMaintainAgreementDTO().getAgreementDTO().getCurrentAndFutureHomeRoles().size()==0) {
					// Its null and no selection so it wont be found
					return;
				}
				if(currentHomeRole == null || !found){
					currentHomeRole = pageModel.getMaintainAgreementDTO().getAgreementDTO().getCurrentAndFutureHomeRoles().get(0);
				}				
				for (Object selection : selections) {
					// check that we are not removing the main home link,
					// users must change the main home but can not remove
					// it
					if (selection != currentHomeRole) {
						pageModel.getMaintainAgreementDTO().getAgreementDTO().getCurrentAndFutureHomeRoles().remove(selection);
					}
				}
				if (pageModel.getMaintainAgreementDTO().getAgreementDTO().getCurrentAndFutureHomeRoles().size() == 1 && selections.size() != 0) {
					// we erase the end date
					currentHomeRole.setEffectiveTo(null);
				}
				refreshFeedbackPanel(target);	
				adjustCurrentHomeEndDate(target, currentHomeRole);
				target.add(homeGrid);
			}
		});		
		if(getEditState() == EditStateType.AUTHORISE){			
			button.setVisible(false);
		}
		button.setOutputMarkupId(true);
		button.setOutputMarkupPlaceholderTag(true);
		setUpbutton(button, !existingHomeRequest);
		return button;
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
	 * Create a grid for the agreement roles
	 * 
	 * @return
	 */
	private SRSDataGrid createAgreementRoleGrid(String id, MaintainAgreementPageModel model) {
		agreementRoles = model.getGridRoles();
		if (agreementRoles == null) {
			agreementRoles = new ArrayList<AgreementRoleGridDTO>();
		}
		List<AgreementRoleGridDTO> nonSelectable = new ArrayList<AgreementRoleGridDTO>();
		//non selectable will be determined via outstatnding requests
		if(existingHierarchyRequest || existingServicingRequest){
			for(AgreementRoleGridDTO role : agreementRoles){
				if(existingHierarchyRequest && role.getRole().getKind() != RoleKindType.ISSERVICEDBY.getKind()
						&&role.getRole().getKind() != RoleKindType.HASBUSINESSSTAKEHOLDER.getKind()){
					nonSelectable.add(role);					
				}else if(existingServicingRequest && role.getRole().getKind() == RoleKindType.ISSERVICEDBY.getKind()){
					nonSelectable.add(role);			
				}
				
			}
		}		
		
		//quick doing a check to see if the actual roleplayer is still active, the role is active but the roleplayer might have been terminated
//		for(AgreementRoleGridDTO role : agreementRoles){
//			SearchResultBaseDataDTO rolePlayer = (ResultPartyDTO) role.getRole().getRolePlayerReference();
//			if(rolePlayer == null || rolePlayer.getEffectiveTo() != null && !rolePlayer.getEffectiveTo().after(TemporalityHelper.getInstance().getNewNOWDateWithNoTime())){
//				//rolePlayer has been terminated
//				warn("One of the rolePlayers has been ended, please remove the link to it and add another, rolePlayer type " + ((rolePlayer != null)? rolePlayer.getTypeOid() : "") + " roleplayer id = " + ((rolePlayer != null)? rolePlayer.getOid() : ""));
//			}
//		}		
		
		//removed below as all roles can be removed, ended
//		build up list of roles not selectable		
//		if(agreementRoles.size() > 0){				
//			for(AgreementRoleGridDTO role : agreementRoles){				
//				if(role.getRole().getRoleID() > 0){
//					//not selectable for removal
//					nonSelectable.add(role);
//				}							
//			}						
//		}		
		/*Market Integration SSM2707 Sweta Menon Begin*/
//		for (AgreementRoleGridDTO role : agreementRoles) {
//			if (role.getRole().getKind() == RoleKindType.HASSERVICINGPANEL
//					.getKind()
//					|| role.getRole().getKind() == RoleKindType.HASSALESPANEL
//							.getKind()
//					|| role.getRole().getKind() == RoleKindType.HASSERVICINGPANEL
//							.getKind()) {
//				role.getAgreementParty().setName(
//						role.getRole().getRolePlayerReference().getName());
//				nonSelectable.add(role);
//			}
//		}
		/*Market Integration SSM2707 Sweta Menon End*/
		SRSDataGrid grid = new SRSDataGrid(id, new DataProviderAdapter(
				new ListDataProvider<AgreementRoleGridDTO>(agreementRoles)),
				getAgreementRoleColumns(), getEditState(),nonSelectable);		
		grid.setCleanSelectionOnPageChange(false);
		grid.setClickRowToSelect(false);
		grid.setAllowSelectMultiple(true);
		//grid.setGridWidth(650, GridSizeUnit.PIXELS);
		grid.setGridWidth(99, GridSizeUnit.PERCENTAGE);
		grid.setRowsPerPage(5);
		grid.setContentHeight(140, SizeUnit.PX);
		return grid;
	}
	
	/**
	 * Get the list of node columns for the role grid
	 * @return
	 */
	private List<IGridColumn> getAgreementRoleColumns() {
		Vector<IGridColumn> cols = new Vector<IGridColumn>();		
		if (!getEditState().isViewOnly()) {
			SRSGridRowSelectionCheckBox col = new SRSGridRowSelectionCheckBox(
					"checkBox");
			cols.add(col.setInitialSize(30));
		}
		//add in the agreement number
		cols.add(new SRSDataGridColumn<AgreementRoleGridDTO>("role.rolePlayerReference.oid",
				new Model("SRS ID"), "role.rolePlayerReference.oid", "role.rolePlayerReference.oid", getEditState()).setInitialSize(90));
		//add in the name column
		cols.add(new SRSDataGridColumn<AgreementRoleGridDTO>("agreementParty.name",
				new Model("Name"), "agreementParty.name", "agreementParty.name", getEditState()).setInitialSize(140).setWrapText(true));
		
//		add search button, don't display this column on view
		if(getEditState() == null ||  !getEditState().isViewOnly()){
			cols.add(new SRSDataGridColumn<AgreementRoleGridDTO>("searchParty",
					new Model("Search"), "searchParty", getEditState()){	
				
						private static final long serialVersionUID = 1L;							

						@Override
						public Panel newCellPanel(WebMarkupContainer parent, String componentId, IModel rowModel, String objectProperty, EditStateType state, final AgreementRoleGridDTO data) {
							if(data.getRole().getRoleID() == 0){
								Button searchButton = new Button("value", new Model("Search"));	
								searchButton.add(new AjaxFormComponentUpdatingBehavior("click"){									
									private static final long serialVersionUID = 1L;
									@Override
									protected void onUpdate(AjaxRequestTarget target) {	
										refreshFeedbackPanel(target);
										currentSearchHomeRole = null;
										currentSearchAgmtRole = data;										
										searchWindow.show(target);										
									}									
								});
								return HelperPanel.getInstance(componentId,searchButton);	
							}else{
								return new EmptyPanel(componentId);
							}
						}				
				
			}.setInitialSize(64));
		}	
		
		//adding the agreement branch name
		cols.add(new SRSDataGridColumn<AgreementRoleGridDTO>("role.rolePlayerReference.branchName",
				new Model("Branch Name"), "role.rolePlayerReference.branchName", "role.rolePlayerReference.branchName", getEditState()).setInitialSize(140).setWrapText(true));
	  
		//TODO put back if users want to see the actual home of the agreement
//		adding the agreement unit name
//		cols.add(new SRSDataGridColumn<AgreementRoleGridDTO>("agreementHome.name",
//				new Model("Home Name"), "agreementHome.name", "agreementHome.name", getEditState()).setInitialSize(170));
		
		//add in the role kind selection
		cols.add(new SRSDataGridColumn<AgreementRoleGridDTO>("role.kind",
				new Model("Role Kind"), "role.kind", "role.kind",
				getEditState()) {

			@Override
			public Panel newCellPanel(WebMarkupContainer parent,
					String componentId, IModel rowModel,
					String objectProperty, EditStateType state,
					final AgreementRoleGridDTO data) {	
				
				if ((existingHierarchyRequest && existingServicingRequest) 
						|| getEditState().isViewOnly() 
						|| (data.getRole().getRoleID() != 0)) {
					//create label with type and display	
					RoleKindType role = RoleKindType.getRoleKindTypeForKind(data.getRole().getKind().intValue());
					return HelperPanel.getInstance(componentId, new Label("value",(role != null) ? role.getDescription() : ""));
				}	
				
				List<RoleKindType> roleKindChoices  = getSelectableRoleKindChoices();
				
				DropDownChoice dropdown = new DropDownChoice("value",new PropertyModel(data,objectProperty){
					@Override
						public Object getObject() {
							//return one of the values in the static list						
							Long id = (Long) super.getObject();
							if(id == null){
								return null;							
							}
							for(RoleKindType type : pageModel.getSelectableRoleKinds()){
								if(type.getKind() == id){
									return type;
								}
							}
							return null;
						}
						@Override
						public void setObject(Object arg0) {						
							super.setObject(((RoleKindType)arg0).getKind());
						}
				},roleKindChoices,new ChoiceRenderer("description", "kind"));
				dropdown.add(new AjaxFormComponentUpdatingBehavior("change"){
					@Override
					protected void onUpdate(AjaxRequestTarget target) {
						//check if this is a belongs to selection, if so default to the pays to agreement
						if(data.getRole().getKind() == RoleKindType.BELONGSTO.getKind() 
								&& (data.getRole().getRolePlayerReference() == null || data.getRole().getRolePlayerReference().getOid() <= 0)
								&& pageModel.getMaintainAgreementDTO().getAgreementDTO().getPaymentDetails().getOrgAgreementNumber() != null){
							//we add the pays to role as the default
							try {
								ResultAgreementDTO agmt = getAgreementGUIController().findAgreementWithSRSAgreementNr(pageModel.getMaintainAgreementDTO().getAgreementDTO().getPaymentDetails().getOrgAgreementNumber());
								data.getRole().setRolePlayerReference(agmt);
								IAgreementGUIController agreementGUIController = getAgreementGUIController();
								agreementGUIController.setUpAgreementGridRoleData(data);
							} catch (Exception e) {								
								//do nothing, user will have to find the agreement himself
							}							
						}
						if(data.getRole().getKind() == RoleKindType.ISSERVICEDBY.getKind() && pageModel.getServicingTypes().size() == 1){
							//selecting first one if list is only 1 size big
							data.getRole().setType(pageModel.getServicingTypes().get(0).getOid());							
						}else{
							data.getRole().setType(SRSType.MANAGESAGREEMENTROLE);
						}
						AgreementGridData gridDataObj = gridData.get(data);
						if(gridDataObj == null){
							gridDataObj = new AgreementGridData();
							gridData.put(data, gridDataObj);
						}
						
						Panel panel = (Panel)gridDataObj.getComponent("role.type");						
						if(panel != null)	{				
							HelperPanel panel2 = getSecondaryChoiceGridSelectionDropdown(panel.getId(), "role.type", data);							
							panel.replaceWith(panel2);	
							gridDataObj.addComponent("role.type", panel2);
							target.add(panel2);
						}
						
						//update the grid as the next column needs this value
						target.add(agreementRoleGrid);
					}					
				});
				//create dropdown of selectable types				
				HelperPanel dropdownPanel = HelperPanel.getInstance(componentId, dropdown);				
				return dropdownPanel;
			}			

		}.setInitialSize(170));
		
		//adding the relationship type column
		cols.add(new SRSDataGridColumn<AgreementRoleGridDTO>("role.type",
				new Model("Relationship Type"), "role.type", "role.type",
				getEditState()) {

			@Override
			public Panel newCellPanel(WebMarkupContainer parent,
					String componentId, IModel rowModel,
					String objectProperty, EditStateType state,
					AgreementRoleGridDTO data) {					
				AgreementGridData gridDataObj = gridData.get(data);
				if(gridDataObj == null){
					gridDataObj = new AgreementGridData();
					gridData.put(data, gridDataObj);
				}				
				HelperPanel panel = getSecondaryChoiceGridSelectionDropdown(componentId, 
						objectProperty, data);				
				gridDataObj.addComponent(objectProperty, panel);
				return panel;				
			}

		}.setInitialSize(145));
		
		//the effective dates of the role
		cols.add(new SRSDataGridColumn<AgreementRoleGridDTO>("role.effectiveFrom",
				new Model("Start Date"), "role.effectiveFrom", "role.effectiveFrom",
				getEditState()) {

			@Override
			public Panel newCellPanel(WebMarkupContainer parent,
					String componentId, IModel rowModel,
					String objectProperty, EditStateType state,
					AgreementRoleGridDTO data) {	
				if ((existingHierarchyRequest && existingServicingRequest) || getEditState().isViewOnly() 
						|| (data.getRole().getRoleID() != 0 && DateUtil.getInstance().compareDatePart(data.getRole().getEffectiveFrom(),new Date()) <= 0)
						|| (existingHierarchyRequest && data.getRole().getRoleID() != 0 && data.getRole().getKind() != RoleKindType.ISSERVICEDBY.getKind())
						|| (existingServicingRequest && data.getRole().getRoleID() != 0 && data.getRole().getKind() == RoleKindType.ISSERVICEDBY.getKind())) {
					return super.newCellPanel(parent, componentId,
							rowModel, objectProperty, state, data);
				}
				TextField startDate = new TextField("value",
						new PropertyModel(data, objectProperty));
				startDate.add(new AttributeModifier("size", "12"));
				startDate.add(new AttributeModifier("maxlength", "10"));
				startDate.add(new AttributeModifier("readonly","true"));
				startDate.setRequired(true);
				startDate.setLabel(new Model("Parent Start Date"));
				startDate.add(new AjaxFormComponentUpdatingBehavior("change") {
					@Override
					protected void onUpdate(AjaxRequestTarget target) {
						//do nothing just want the object value updated
					}
				});									
				return HelperPanel
						.getInstance(componentId, startDate, true);
			}

		}.setInitialSize(100));

		cols.add(new SRSDataGridColumn<AgreementRoleGridDTO>("role.effectiveTo",
				new Model("End Date"), "role.effectiveTo", "role.effectiveTo", getEditState()) {

			@Override
			public Panel newCellPanel(WebMarkupContainer parent,
					String componentId, IModel rowModel,
					String objectProperty, EditStateType state,
					AgreementRoleGridDTO data) {
				if ((existingHierarchyRequest && existingServicingRequest) || getEditState().isViewOnly()
						|| (existingHierarchyRequest && data.getRole().getRoleID() != 0 && data.getRole().getKind() != RoleKindType.ISSERVICEDBY.getKind())
						|| (existingServicingRequest && data.getRole().getRoleID() != 0 && data.getRole().getKind() == RoleKindType.ISSERVICEDBY.getKind())) {
					return super.newCellPanel(parent, componentId,
							rowModel, objectProperty, state, data);
				}
				SRSDateField endDate = new SRSDateField("value",
						new PropertyModel(data, objectProperty));
				endDate.add(new AttributeModifier("size", "12"));
				endDate.add(new AttributeModifier("maxlength", "10"));
				endDate.setLabel(new Model("Parent End Date"));
				endDate.add(new AjaxFormComponentUpdatingBehavior("change") {
					@Override
					protected void onUpdate(AjaxRequestTarget target) {
						//do nothing just want the object value updated
					}
				});	
				endDate.add(endDate.newDatePicker());
				return HelperPanel.getInstance(componentId, endDate, true);
			}

		}.setInitialSize(100));

		return cols;
	}
	
	
	/**
	 * Create the secondary dropdown on th agreement role grid
	 * @param parent
	 * @param componentId
	 * @param rowModel
	 * @param objectProperty
	 * @param state
	 * @param data
	 * @return
	 */
	private HelperPanel getSecondaryChoiceGridSelectionDropdown(String componentId,
			String objectProperty, AgreementRoleGridDTO data){
		if (existingServicingRequest || getEditState().isViewOnly() || (data.getRole().getRoleID() != 0) || data.getRole().getKind() != RoleKindType.ISSERVICEDBY.getKind()) {
			//create label with type and display	
			TypeVO type =pageModel.getServicingType(data.getRole().getType());
			HelperPanel panel = HelperPanel.getInstance(componentId, new Label("value",(type != null) ? type.getDescription(): ""));			
			panel.setOutputMarkupId(true);
			panel.setOutputMarkupPlaceholderTag(true);
			return panel;
		}
		
		DropDownChoice dropdown = new DropDownChoice("value",new PropertyModel(data,objectProperty){
			@Override
				public Object getObject() {
					//return one of the values in the static list						
					Long id = (Long) super.getObject();
					if(id == null){
						return null;							
					}
					for(TypeVO type : pageModel.getServicingTypes()){
						if(type.getOid() == id){
							return type;
						}
					}
					return null;
				}
				@Override
				public void setObject(Object arg0) {						
					super.setObject(((TypeVO)arg0).getOid());
				}
		},pageModel.getServicingTypes(),new ChoiceRenderer("description", "oid"));
		
		dropdown.add(new AjaxFormComponentUpdatingBehavior("change"){
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				//update the value					
			}					
		});		
		dropdown.add(new AttributeModifier("style","width: 143px;"));
		dropdown.setRequired(true);
		//create dropdown of selectable types	
		dropdown.setLabel(new Model("Relationship Type"));
		HelperPanel dropdownPanel = HelperPanel.getInstance(componentId, dropdown);	
		dropdownPanel.setOutputMarkupId(true);
		dropdownPanel.setOutputMarkupPlaceholderTag(true);
		return dropdownPanel;
	}
	
	/**
	 * Create a grid for the home roles
	 * 
	 * @return
	 */
	private SRSDataGrid createHomeGrid(String id, AgreementDTO dto) {
		List<AgreementHomeRoleDTO> homeRoles = dto.getCurrentAndFutureHomeRoles();
		if (homeRoles == null) {
			homeRoles = new ArrayList<AgreementHomeRoleDTO>();
		}
		//quick doing a check to see if the actual party is still active, the role is active but the party might have been terminated
		for(AgreementHomeRoleDTO home : homeRoles){
			ResultPartyDTO homeDTO = (ResultPartyDTO) home.getRolePlayerReference();
			if(homeDTO.getEffectiveTo() != null && !homeDTO.getEffectiveTo().after(TemporalityHelper.getInstance().getNewNOWDateWithNoTime())){
				//node has been terminated
				warn("Parent node " + homeDTO.getName() + " has been removed, please remove the link to it and add another parent");
			}
		}
		
		List<AgreementHomeRoleDTO> noneSelectable = null;
		if(homeRoles.size() > 0){			
			Date now = TemporalityHelper.getInstance().getNewNOWDateWithNoTime();
			for(AgreementHomeRoleDTO home : homeRoles){
				if(home.getEffectiveFrom() != null && 
						(!dto.getStartDate().after(now) && !home.getEffectiveFrom().after(now) && 
						(home.getEffectiveTo() == null || !home.getEffectiveTo().before(now))) || 
						!dto.getStartDate().before(now) && !home.getEffectiveFrom().after(dto.getStartDate())){
					//got current parent
					currentHomeRole = home;
					break;
				}
			}
			if(currentHomeRole == null){
				currentHomeRole = homeRoles.get(0);
			}
			noneSelectable = new ArrayList<AgreementHomeRoleDTO>(1);				
			noneSelectable.add(currentHomeRole);				
		}			
		SRSDataGrid grid = new SRSDataGrid(id, new DataProviderAdapter(
				new ListDataProvider<AgreementHomeRoleDTO>(homeRoles)),
				getHomeColumns(), ((existingHomeRequest) ? EditStateType.VIEW : getEditState()),noneSelectable);
		
		grid.setCleanSelectionOnPageChange(false);
		grid.setClickRowToSelect(false);
		grid.setAllowSelectMultiple(true);
		//grid.setGridWidth(650, GridSizeUnit.PIXELS);
		grid.setGridWidth(99, GridSizeUnit.PERCENTAGE);
		grid.setRowsPerPage(2);
		grid.setContentHeight(70, SizeUnit.PX);
		return grid;
	}
	
	/**
	 * Get the list of node columns for the grid
	 * @return
	 */
	private List<IGridColumn> getHomeColumns() {
		Vector<IGridColumn> cols = new Vector<IGridColumn>(7);
		if (!getEditState().isViewOnly() && !existingHomeRequest) {
			SRSGridRowSelectionCheckBox col = new SRSGridRowSelectionCheckBox(
					"checkBox");
			cols.add(col.setInitialSize(30));
		}
		/* 
		 * add in the type selection column 
		 */
		cols.add(new SRSDataGridColumn<AgreementHomeRoleDTO>("rolePlayerReference.typeOid",
				new Model("Type"), "rolePlayerReference.typeOid", "rolePlayerReference.typeOid", getEditState()) {		
			private static final long serialVersionUID = 1L;
			@Override
			public Panel newCellPanel(WebMarkupContainer parent,
					String componentId, IModel rowModel,
					String objectProperty, EditStateType state,
					final AgreementHomeRoleDTO data) {					
				if (existingHomeRequest || getEditState().isViewOnly() || data.getRoleID() != 0) {
					return super.newCellPanel(parent, componentId,
							rowModel, "rolePlayerReference.hierarchyOrganisationTypeName", state, data);
				}					
				HelperPanel dropdown = createDropdownField("Node Type",componentId,new PropertyModel(data,objectProperty){
					@Override
					public Object getObject() {
						//return one of the values in the static list						
						Long id = (Long) super.getObject();
						if(id == null){
							return null;							
						}
						for(IDValueDTO type : homeTypes){
							if(type.getOid() == id){
								return type;
							}
						}
						return null;
					}
					@Override
					public void setObject(Object arg0) {						
						super.setObject(((IDValueDTO)arg0).getOid());
					}					
				},homeTypes, new ChoiceRenderer("name","oid"), 
						 "Select", true,false,new EditStateType[]{EditStateType.ADD,EditStateType.MODIFY});
				DropDownChoice dropdownComp = (DropDownChoice) dropdown
						.getEnclosedObject();
				dropdownComp.add(new AjaxFormComponentUpdatingBehavior(
						"change") {					
					private static final long serialVersionUID = 1L;

					@Override
					protected void onUpdate(AjaxRequestTarget target) {
						//refresh table						
						//remove all detail in row	
						long type = data.getRolePlayerReference().getTypeOid();
						ResultPartyDTO party = new ResultPartyDTO();
						party.setTypeOid(type);
						data.setRolePlayerReference(party);						
						target.add(homeGrid);
						updateExternalRefCompSize(data,target);
					}
				});
				dropdownComp.setNullValid(false);	
				dropdownComp.setOutputMarkupId(true);
				addToHomeRowComponentsList(data,dropdownComp,"rolePlayerReference.hierarchyOrganisationTypeName");
				return dropdown;
			}
		}.setInitialSize(80));
		
	
		/*
		 * add in the name column(Display only col)
		 */
		cols.add(new SRSDataGridColumn<AgreementHomeRoleDTO>("rolePlayerReference.name",
				new Model("Name"), "rolePlayerReference.name", "rolePlayerReference.name", getEditState()).setInitialSize(230));
		
		/*
		 * adding the external reference column
		 */
		cols.add(new SRSDataGridColumn<AgreementHomeRoleDTO>(HOME_EXTERNAL_REF_GRID_OBJECT_PROPERTY_NAME,
				new Model("Code"), HOME_EXTERNAL_REF_GRID_OBJECT_PROPERTY_NAME, HOME_EXTERNAL_REF_GRID_OBJECT_PROPERTY_NAME, getEditState()) {

			@Override
			public Panel newCellPanel(WebMarkupContainer parent,
					String componentId, IModel rowModel,
					String objectProperty, EditStateType state,
					final AgreementHomeRoleDTO data) {				
				
				if (existingHomeRequest || getEditState().isViewOnly() || data.getRoleID() != 0) {
					return super.newCellPanel(parent, componentId,
							rowModel, objectProperty, state, data);
				}		

				final TextField externalRef = new TextField("value", new PropertyModel(data, objectProperty));
				externalRef.setOutputMarkupId(true);
				externalRef.setLabel(new Model("External Reference"));
				externalRef.setRequired(true);				
				//validationComponents.add(uacfid);		
				addToHomeRowComponentsList(data,externalRef,HOME_EXTERNAL_REF_GRID_OBJECT_PROPERTY_NAME);
				updateExternalRefCompSize(data,null);
				return HelperPanel.getInstance(componentId, externalRef);
			}
		}.setInitialSize(50));
		
		/*
		 * add search button, don't display this column on view
		 */
		if((getEditState() == null || !getEditState().isViewOnly()) && !existingHomeRequest){
			cols.add(new SRSDataGridColumn<AgreementHomeRoleDTO>("searchParty",
					new Model("Search"), "searchParty", getEditState()){	
				
						private static final long serialVersionUID = 1L;							

						@Override
						public Panel newCellPanel(WebMarkupContainer parent, String componentId, IModel rowModel, String objectProperty, EditStateType state, final AgreementHomeRoleDTO data) {
							if(data.getRoleID() == 0){
								Button searchButton = new Button("value", new Model("Search"));	
								searchButton.add(new AjaxFormComponentUpdatingBehavior("click"){									
									private static final long serialVersionUID = 1L;
									@Override
									protected void onUpdate(AjaxRequestTarget target) {
										currentSearchHomeRole = data;
										currentSearchAgmtRole = null;										
										searchWindow.show(target);										
									}									
								});
								return HelperPanel.getInstance(componentId,searchButton);	
							}else{
								return new EmptyPanel(componentId);
							}
						}				
				
			}.setInitialSize(67));
		}	

		//Add the middle six and last four columns when Broker and editState ADD
				
		if (isAutoGenEnabledAndADDAction()) {
			cols.add(new SRSDataGridColumn<AgreementHomeRoleDTO>("middleSix",
				new Model("Middle Six"), "middleSix", "middleSix",
				getEditState()) {
				private static final long serialVersionUID = 1L;

				@Override
				public Panel newCellPanel(WebMarkupContainer parent,
						String componentId, IModel rowModel,
						String objectProperty, EditStateType state,
						final AgreementHomeRoleDTO data) {	
					
					if (existingHomeRequest || getEditState().isViewOnly() || (data.getRoleID() != 0 && !currentHomeRole.equals(data)) 
							|| (data.getRoleID() != 0 && currentHomeRole.equals(data) && pageModel.getMaintainAgreementDTO().getAgreementDTO().getCurrentAndFutureHomeRoles().size() > 1)) {
						return super.newCellPanel(parent, componentId,
								rowModel, objectProperty, state, data);
					}
					TextField midSix = new TextField("value",
							new PropertyModel(data, objectProperty));				
					midSix.add(new AttributeModifier("size", "9"));
					midSix.add(new AttributeModifier("maxlength", "6"));				
					midSix.setRequired(true);
					midSix.setLabel(new Model("Middle Six"));
					midSix.setEnabled(false);
					midSix.setOutputMarkupId(true);
					midSix.setOutputMarkupPlaceholderTag(true);							
					addToHomeRowComponentsList(data,midSix,objectProperty);
					return HelperPanel.getInstance(componentId, midSix);
				}
			}.setInitialSize(115));
			

			cols.add(new SRSDataGridColumn<AgreementHomeRoleDTO>("lastFour",
				new Model("Last Four"), "lastFour", "lastFour",
				getEditState()) {
				private static final long serialVersionUID = 1L;

				@Override
				public Panel newCellPanel(WebMarkupContainer parent,
						String componentId, IModel rowModel,
						String objectProperty, EditStateType state,
						final AgreementHomeRoleDTO data) {	
					
					if (existingHomeRequest || getEditState().isViewOnly() || (data.getRoleID() != 0 && !currentHomeRole.equals(data)) 
							|| (data.getRoleID() != 0 && currentHomeRole.equals(data) && pageModel.getMaintainAgreementDTO().getAgreementDTO().getCurrentAndFutureHomeRoles().size() > 1)) {
						return super.newCellPanel(parent, componentId,
								rowModel, objectProperty, state, data);
					}
					TextField lastF4 = new TextField("value",
							new PropertyModel(data, objectProperty));				
					lastF4.add(new AttributeModifier("size", "9"));
					lastF4.add(new AttributeModifier("maxlength", "4"));				
					lastF4.setRequired(true);
					lastF4.setLabel(new Model("Last Four"));
					lastF4.setEnabled(false);
					lastF4.setOutputMarkupId(true);
					lastF4.setOutputMarkupPlaceholderTag(true);								
					addToHomeRowComponentsList(data,lastF4,objectProperty);
					return HelperPanel.getInstance(componentId, lastF4);
				}
			}.setInitialSize(115));
		
		}
		
		cols.add(new SRSDataGridColumn<AgreementHomeRoleDTO>("consultantCode",
				new Model("Consultant Code"), "consultantCode", "consultantCode",
				getEditState()) {
			private static final long serialVersionUID = 1L;

			@Override
			public Panel newCellPanel(WebMarkupContainer parent,
					String componentId, IModel rowModel,
					String objectProperty, EditStateType state,
					final AgreementHomeRoleDTO data) {	
				
				if (existingHomeRequest || getEditState().isViewOnly() || (data.getRoleID() != 0 && !currentHomeRole.equals(data)) 
						|| (data.getRoleID() != 0 && currentHomeRole.equals(data) && pageModel.getMaintainAgreementDTO().getAgreementDTO().getCurrentAndFutureHomeRoles().size() > 1)) {
					return super.newCellPanel(parent, componentId,
							rowModel, objectProperty, state, data);
				}
				TextField consCode = new TextField("value",
						new PropertyModel(data, objectProperty));				
				consCode.add(new AttributeModifier("size", "16"));
				consCode.add(new AttributeModifier("maxlength", "13"));				
				consCode.setRequired(true);
				consCode.setEnabled(!isAutoGenEnabledAndADDAction());//Non Editable if auto generated and ADD Action Type-SBS0510
				consCode.setLabel(new Model("Thirteen Digit Consultant Code"));
				consCode.setOutputMarkupId(true);
				consCode.setOutputMarkupPlaceholderTag(true);
				//make sure that the code is updated when typing as row gets refreshed
				consCode.add(new AjaxFormComponentUpdatingBehavior("keyup"){					
					private static final long serialVersionUID = 1L;
					@Override
					protected void onUpdate(AjaxRequestTarget target) {
						//check that the first three digits of the code match the branch code
						check13DigitCodeAgainstBranch(data,target);			
					}					
				});				
				//will validate thirteen digit code once submitted as it takes a bit of time
				//consCode.add(new ConsultantCodeValidator(pageModel.getPreviousMaintainAgreementDTO().getAgreementDTO().getId()));
				addToHomeRowComponentsList(data,consCode,objectProperty);
				return HelperPanel.getInstance(componentId, consCode);
			}
		}.setInitialSize(115));
		
		
		//the effective dates of the role
		cols.add(new SRSDataGridColumn<AgreementHomeRoleDTO>("effectiveFrom",
				new Model("Start Date"), "effectiveFrom", "effectiveFrom",
				getEditState()) {
			private static final long serialVersionUID = 1L;

			@Override
			public Panel newCellPanel(WebMarkupContainer parent,
					String componentId, IModel rowModel,
					String objectProperty, EditStateType state,
					final AgreementHomeRoleDTO data) {	
				if (existingHomeRequest || getEditState().isViewOnly() || (data.getRoleID() != 0 && currentHomeRole.equals(data))) {
					return super.newCellPanel(parent, componentId,
							rowModel, objectProperty, state, data);
				}
				SRSDateField startDate = new SRSDateField("value",
						new PropertyModel(data, objectProperty));
				startDate.add(new AttributeModifier("size", "12"));
				startDate.add(new AttributeModifier("maxlength", "10"));
				startDate.add(new AttributeModifier("readonly","true"));
				startDate.setOutputMarkupId(true);
				startDate.setRequired(true);
				startDate.setLabel(new Model("Parent Start Date"));
				startDate.add(new AjaxFormComponentUpdatingBehavior("change") {
					@Override
					protected void onUpdate(AjaxRequestTarget target) {
						setCurrentHomeRole(pageModel.getMaintainAgreementDTO().getAgreementDTO(),target);
						if(data != currentHomeRole){
							adjustCurrentHomeEndDate(target, data);
						}
					}
				});	
				startDate.add(startDate.newDatePicker());
				HelperPanel panel = HelperPanel.getInstance(componentId, startDate, true);
				panel.setOutputMarkupId(true);
				addToHomeRowComponentsList(data,panel,"effectiveFrom");
				return panel;
			}

		}.setInitialSize(115));

		cols.add(new SRSDataGridColumn<AgreementHomeRoleDTO>("effectiveTo",
				new Model("End Date"), "effectiveTo", "effectiveTo", getEditState()) {

			@Override
			public Panel newCellPanel(WebMarkupContainer parent,
					String componentId, IModel rowModel,
					String objectProperty, EditStateType state,
					final AgreementHomeRoleDTO data) {
				if (existingHomeRequest || getEditState().isViewOnly()) {
					return super.newCellPanel(parent, componentId,
							rowModel, objectProperty, state, data);
				}
				SRSDateField endDate = new SRSDateField("value",
						new PropertyModel(data, objectProperty));
				endDate.add(new AttributeModifier("size", "12"));
				endDate.add(new AttributeModifier("maxlength", "10"));
				endDate.setLabel(new Model("Parent End Date"));
				endDate.setOutputMarkupId(true);
				endDate.add(new AjaxFormComponentUpdatingBehavior("change") {
					@Override
					protected void onUpdate(AjaxRequestTarget target) {
						//if(data.getEffectiveTo() != null && data.getEffectiveTo().compareTo(new Date()) < current ){
							setCurrentHomeRole(pageModel.getMaintainAgreementDTO().getAgreementDTO(),target);
						//}
						if(data == currentHomeRole){
							adjustFutureHomeStartDate(target);
						}
					}
				});
				endDate.add(endDate.newDatePicker());
				HelperPanel panel = HelperPanel.getInstance(componentId, endDate, true);
				panel.setOutputMarkupId(true);
				addToHomeRowComponentsList(data,panel,"effectiveTo");
				
				return panel;
			}

		}.setInitialSize(115));
		return cols;
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
	 * Add a componenet to the row
	 * @param data
	 * @param comp
	 */
	private void addToHomeRowComponentsList(AgreementHomeRoleDTO data,Component comp, String compID){
		if(comp != null){
			getComponentsForHomeGrid(data).put(compID, comp);			
		}
	}
	
	/**
	 * Update the external refs size
	 * @param data
	 */
	private void updateExternalRefCompSize(final AgreementHomeRoleDTO data, AjaxRequestTarget target){
		Component externalRefComp = getComponentsForHomeGrid(data).get(HOME_EXTERNAL_REF_GRID_OBJECT_PROPERTY_NAME);
		if(externalRefComp != null && externalRefComp instanceof TextField){
			final TextField externalRef = (TextField) externalRefComp;
			String ref = ((ResultPartyDTO)data.getRolePlayerReference()).getExternalReference();
			externalRef.setModelObject(ref);
			externalRef.modelChanged();
			int size = 3;
			if(data.getRolePlayerReference().getTypeOid() == SRSType.BRANCH){
				 size = 3;
			}else if(data.getRolePlayerReference().getTypeOid() == SRSType.UNIT){
				 size = 5;
			}
			final int maxSize = size;
			externalRef.add(new AttributeModifier("size", "" + size));
			externalRef.add(new AttributeModifier("maxlength", "" + size));			
			/* Add behavior to update selected item */
			externalRef.add(new AjaxFormComponentUpdatingBehavior("keyup") {					
				private static final long serialVersionUID = 1L;
				@Override
				protected void onUpdate(AjaxRequestTarget target) {
					String input = (String) externalRef.getModelObject();
					if(input != null && input.length() == maxSize){
						updateHomeSelection(input,data,target);				
					}
				}
			});	
			externalRef.add(new AjaxFormComponentUpdatingBehavior("blur") {					
				private static final long serialVersionUID = 1L;
				@Override
				protected void onUpdate(AjaxRequestTarget target){
					//make sure code input is valid
					String input = (String) externalRef.getModelObject();
					updateHomeSelection(input,data,target);	
				}						
			});
			if(target != null){
				target.add(externalRef);
			}
		}
	}
	
	
	/**
	 * Based on the keys input, this method will update the home node in the home grid
	 * @param target
	 */
	private void updateHomeSelection(String input, AgreementHomeRoleDTO data ,AjaxRequestTarget target){
		AgreementHomeRoleDTO before = (AgreementHomeRoleDTO) SerializationUtils.clone(data);
		List<ResultPartyDTO> nodes = new ArrayList<ResultPartyDTO>(0);
		try {
			nodes = getAgreementGUIController().findHierarchyNodeWithExternalReference(input, null);
		} catch (DataNotFoundException e) {
			//display that code could not be found
			ResultPartyDTO party = (ResultPartyDTO) data.getRolePlayerReference();
			party.setName("Code Not Found");
			party.setOid(0);			
		}
		for(ResultPartyDTO node : nodes){
			if(node.getExternalReference().equalsIgnoreCase(input)){								
				data.setRolePlayerReference(node);
				fillIn13Digitcode(data);
				check13DigitCodeAgainstBranch(data,target);
				setCurrentHomeRole(pageModel.getMaintainAgreementDTO().getAgreementDTO(), target);
				
			}
		}	
		//update only if grid object changed
		if(target != null && ComparatorUtil.compareObjects(before, data).size() != 0){
			target.add(homeGrid);
		}
		
	}
	
	/**
	 * Checks that the first 3 characters match the branch code selected, this is just to popup a warning message
	 * @param role
	 * @param target
	 */
	private void check13DigitCodeAgainstBranch(AgreementHomeRoleDTO data,AjaxRequestTarget target){
		if(data.getConsultantCode() != null && data.getConsultantCode().length() >= 3 
				&& data.getRolePlayerReference() != null && data.getRolePlayerReference().getOid() > 0){
			//first we check if the selected roleplayer is a branch			
			ResultPartyDTO branch = null;			
			if(((ResultPartyDTO)data.getRolePlayerReference()).getTypeOid() == SRSType.BRANCH){
				branch = ((ResultPartyDTO)data.getRolePlayerReference());
			}else if(((ResultPartyDTO)data.getRolePlayerReference()).getTypeOid() == SRSType.UNIT){
				 try {
					branch = getAgreementGUIController().findParentOfHierarchyNode(data.getRolePlayerReference().getOid(),data.getRolePlayerReference().getTypeOid());
				} catch (DataNotFoundException e) {
					//no problem, warning will not happen
				}
			}			
			if(branch != null && branch.getExternalReference() != null && branch.getExternalReference().length() == 3){
				//currentBranch = partyManager.findParentOfHierarchyNode(party.getOid(),party.getTypeOid());
				String branchCode = data.getConsultantCode().substring(0,3);				
				if(!branchCode.equals(branch.getExternalReference())){
					if(((ResultPartyDTO)data.getRolePlayerReference()).getTypeOid() == SRSType.UNIT){
						warn("Warning: Unit selected belongs to Branch ["+branch.getName()+"] with code [" +branch.getExternalReference() +"], the first 3 characters entered ["+branchCode+"] does not match this code");
					}else{					
						warn("Warning: The first 3 characters entered ["+branchCode+"] does not match the branch's code[" +branch.getExternalReference() + "]");
					}
//					update save to include a confirm box before user can continue
				}
				//always refresh the feedback panel
				if (target != null && feedBackPanel != null) {					
					target.add(feedBackPanel);
				}
			}
		}
	}
	
	private ModalWindow createSearchWindow(String id) {

		ContextSearchPopUp popUp = new ContextSearchPopUp() {

			@Override
			public ContextType getContextType() {
				if(currentSearchHomeRole != null){
					return ContextType.PARTY_ORGANISATION_ONLY;
				}else {
					return ContextType.AGREEMENT_ONLY;
				}
			}

			@Override
			public void doProcessSelectedItems(AjaxRequestTarget target,
					ArrayList<ResultContextItemDTO> selectedItemList) {
				IAgreementGUIController agreementGUIController = getAgreementGUIController();
				if (selectedItemList.size() == 0) {
					// Nothing was selected
					return;
				}					
				//adding party to agreement
				for (ResultContextItemDTO contextItemDTO : selectedItemList) {	
					ResultPartyDTO resultPartyDTO = contextItemDTO.getPartyDTO();
					if(currentSearchHomeRole != null){											
						if(resultPartyDTO.getTypeOid() == SRSType.PERSON){
							error("Please only select Organisations as Homes");		
							if(feedBackPanel != null){
								target.add(feedBackPanel);
								break;
							}
						}else if(!(resultPartyDTO.getTypeOid() == SRSType.BRANCH 
								|| resultPartyDTO.getTypeOid() == SRSType.UNIT)){
							//remove this if agreements can be on other nodes
							error("Please only select Branchs/Units as Homes");		
							if(feedBackPanel != null){
								target.add(feedBackPanel);
								break;
							}
						}
						
						
						currentSearchHomeRole.setRolePlayerReference(resultPartyDTO);
						setCurrentHomeRole(pageModel.getMaintainAgreementDTO().getAgreementDTO(), target);
						fillIn13Digitcode((AgreementHomeRoleDTO)currentSearchHomeRole);
						check13DigitCodeAgainstBranch((AgreementHomeRoleDTO)currentSearchHomeRole,target);
						if (target != null) {
							target.add(homeGrid);
							if(currentSearchHomeRole instanceof AgreementHomeRoleDTO){
								updateExternalRefCompSize((AgreementHomeRoleDTO)currentSearchHomeRole,target);
								//make sure the dropdown of type knows its model changed
								Component comp = getComponentsForHomeGrid((AgreementHomeRoleDTO)currentSearchHomeRole).get("rolePlayerReference.hierarchyOrganisationTypeName");
								if(comp != null){
									comp.modelChanged();
									target.add(comp);
								}
							}							
						}
						break;
					}else if(currentSearchAgmtRole != null){
						ResultAgreementDTO resultAgmtDTO = contextItemDTO.getAgreementDTO();					
						if(resultAgmtDTO == null || resultAgmtDTO.getOid() < 1){
							AgreementHierarchyPanel.this.error("Please only select Agreements for agreement roles");		
							if(feedBackPanel != null){
								target.add(feedBackPanel);
								break;
							}
						}else{
							currentSearchAgmtRole.getRole().setRolePlayerReference(resultAgmtDTO);		
							currentSearchAgmtRole.setAgreementParty(resultPartyDTO);
							agreementGUIController.setUpAgreementGridRoleData(currentSearchAgmtRole);
							if (target != null) {
								target.add(agreementRoleGrid);
							}
						}
						break;
					}					
				}
				
			}
		};		
		ModalWindow win = popUp.createModalWindow(id);
//		win.setPageMapName("homeSearchPageMap");
		return win;	
	}
	
	/**
	 * Will check if a home role should have the same 13 digit code
	 * @param homeRole
	 */
	private void fillIn13Digitcode(AgreementHomeRoleDTO homeRole){
		if(currentHomeRole != null && homeRole != null){
			ResultPartyDTO party = (ResultPartyDTO) currentHomeRole.getRolePlayerReference();
			ResultPartyDTO newParty = (ResultPartyDTO) homeRole.getRolePlayerReference();
			if(party != null && newParty != null){
				//check if the branch/unit is the same branch as before
				try {
					//first we check if the two parties are branches
					if(party.getTypeOid() == SRSType.BRANCH && newParty.getTypeOid() == SRSType.BRANCH 
							&& party.getOid() == newParty.getOid()){
						//same branch was selected, duplicated 13 digit code
						//No change
						if (logger.isDebugEnabled())
							logger.debug("Same Branch - consCode=:" + currentHomeRole.getConsultantCode() + ":");
						homeRole.setConsultantCode(currentHomeRole.getConsultantCode());					
					}else if(party.getTypeOid() == SRSType.BRANCH && newParty.getTypeOid() == SRSType.BRANCH 
							&& party.getOid() != newParty.getOid()){
						//different branches, leave blank	
						
						//SBS0510- Auto-populate Cons code on Branch Transfer
						
						String currentConsCode = currentHomeRole.getConsultantCode();
						
						String newBranch = newParty.getUacfID();
						String newConsCode = newBranch.concat(currentConsCode.substring(3));
						homeRole.setConsultantCode(newConsCode);
						if (logger.isDebugEnabled())
							logger.debug("Update branch - currentConsCode:" + currentConsCode +":   newBranch :" + newBranch + ":     newConsCode=" + newConsCode);
					}else{	
						//now check if movement in same branch					
						//first get branch of current
						ResultPartyDTO currentBranch = null;
						if(party.getTypeOid() == SRSType.BRANCH){
							//party is branch
							currentBranch = party;
						}else if(party.getTypeOid() == SRSType.UNIT){
							//get the branch of this unit
							currentBranch = getAgreementGUIController().findParentOfHierarchyNode(party.getOid(),party.getTypeOid());
						}else{
							logger.warn("current home is not a branch or unit, check code in this panel at method fillIn13Digitcode");
						}
						//now get branch of new
						ResultPartyDTO newBranch = null;
						if(newParty.getTypeOid() == SRSType.BRANCH){
							//party is branch
							newBranch = newParty;
						}else if(newParty.getTypeOid() == SRSType.UNIT){
							//get the branch of this unit
							newBranch = getAgreementGUIController().findParentOfHierarchyNode(newParty.getOid(),newParty.getTypeOid());
						}else{
							logger.warn("new home is not a branch or unit, check code in this panel at method fillIn13Digitcode");
						}
						
						/*
						 * UPDATE
						 */
						if(currentBranch != null && newBranch != null && currentBranch.getOid() == newBranch.getOid()){
							
							if (logger.isDebugEnabled())
								logger.debug("Update other - currentConsCode:" + homeRole.getConsultantCode() + ":     newConsCode=" + currentHomeRole.getConsultantCode());
							//same branch so 13 digit code canstay the same
							homeRole.setConsultantCode(currentHomeRole.getConsultantCode());
						}						
					}
				} catch (DataNotFoundException e) {
					//leave blank, not a big problem
					e.printStackTrace();
				}
			}
		}
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
		return AgreementHierarchyPanel.class;
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
	 * Override the modify access behaviour.  Disable maintain button
	 * if there is any pending request for approval.
	 * RXS1408
	 */
	public boolean hasModifyAccess(boolean originalAccess) {
		if(getOutStandingRequestKinds().size() > 0){
			return false;
		}
		return originalAccess;
	}
	
	
	/*************************************SBS0510-AUTOGEN*********************************************************/
	private boolean isPartyOrganisation() {
		if (pageModel.getIsOrganisation()!=null) {
			return pageModel.getIsOrganisation();
		}
		boolean val;
		try {
			val = getAgreementGUIController().isPartyOrganisation(pageModel.getMaintainAgreementDTO());
		} catch (DataNotFoundException e) {
			error("Unable to identify if party is an Organisation");
			if (RequestCycle.get().find(AjaxRequestTarget.class) != null) {
				refreshFeedbackPanel(RequestCycle.get().find(AjaxRequestTarget.class));
			}
			return false;
		}
		pageModel.setIsOrganisation(val);
		return val;
	}
	

	
	/**
	 * AutoGen cons code from currentHomeRole
	 * @return String
	 */
	private String autoGenConsultantCode() {		

		String midSix = pageModel.getMaintainAgreementDTO().getAgreementDTO().getMiddleSix();
		String lastFour = pageModel.getMaintainAgreementDTO().getAgreementDTO().getLastFour();
		String firstThree = ((ResultPartyDTO)currentHomeRole.getRolePlayerReference()).getExternalReference();
		String consCode = null;

		if(!StringUtils.isBlank(firstThree) && !StringUtils.isBlank(midSix) && !StringUtils.isBlank(lastFour)) {
			
			// TODO this decision must be made by calling guiController
			boolean specialFirstThree = (isPartyOrganisation() && (
					pageModel.getMaintainAgreementDTO().getAgreementKindType() == AgreementKindType.BROKER
					|| pageModel.getMaintainAgreementDTO().getAgreementKindType() == AgreementKindType.STOP_ORDER_BROKER));
			consCode = ((specialFirstThree)? "000" : firstThree )+midSix+lastFour;
			pageModel.getMaintainAgreementDTO().getAgreementDTO().setConsultantCode(consCode);

			currentHomeRole.setFirstThree(firstThree);
			currentHomeRole.setMiddleSix(midSix);
			currentHomeRole.setLastFour(lastFour);
			currentHomeRole.setConsultantCode(consCode);			
			
			// TODO at later stage change this to DEBUG
			logger.info("AutoGenConsCode :" + consCode + ":"
					+ "  specialFirstThree=" + specialFirstThree
					+ "  isPartyOrganisation=" + isPartyOrganisation()
					+ "  getAgreementKindType=" + pageModel.getMaintainAgreementDTO().getAgreementKindType()
					+ "  check=" + (pageModel.getMaintainAgreementDTO().getAgreementKindType() == AgreementKindType.BROKER
						|| pageModel.getMaintainAgreementDTO().getAgreementKindType() == AgreementKindType.STOP_ORDER_BROKER));

		} else {
			pageModel.getMaintainAgreementDTO().getAgreementDTO().setConsultantCode(null);
		}


		return consCode;
	}
	
	/**
	 * Method that checks if the Role Kind exists for the Party. e.g. BELONGS_TO for Brokers , REPORTSTO for Agents etc.
	 * And returns all such Roles
	 * @param kind
	 * @return boolean
	 */
	
	private List<AgreementRoleDTO> getRolePlayerForRole(RoleKindType roleKindType) {
		
		List<AgreementRoleDTO> result = new ArrayList();

		List<AgreementRoleDTO> agmtRoles = pageModel.getMaintainAgreementDTO().getAgreementDTO()
				.getCurrentAndFutureAgreementRoles();

		if (!CollectionUtils.isEmpty(agmtRoles)) {
			for (AgreementRoleDTO roleDto : agmtRoles) {
				if (roleDto.getKind() == roleKindType.getKind()) {
					if(roleDto.getRolePlayerReference().getOid() > 0) {
						result.add(roleDto);
					};
				}

			}

		}

		return result;

	}
	
	/**
	 * Validate If Has Home is already selected the applicable role cannot be removed.
	 * @return boolean
	 */
	
	private boolean validateForHasHomeAlreadySelected() {
		
		//Validate Only for Those Agreement Kinds for which AutoGen is enabled - e.g - Broker, Stop Order Broker, BC, BBF as of now
		// This must be in ADD edit state
		if(!isAutoGenEnabledAndADDAction())
			return false;
		
		//Validate Only for New Person
		if(isPartyOrganisation())
			return false;	
		
		
		//Check if Role ( Belongs to / Reports To / Managed By) is present for the agreement kind
		RoleKindType roleKindType = getAgreementGUIController().getApplicableRoleKindTypeForAgmtKind(pageModel.getMaintainAgreementDTO().getAgreementDTO().getKind());
		List<AgreementRoleDTO> agreementRolesDTO = getRolePlayerForRole(roleKindType);
		
		if(CollectionUtils.isEmpty(agreementRolesDTO))
			return false;
		
		//Now we check if Has Home role exists and midsix has been assigned from (Belongs_to / Reports To / Managed By)
		List<AgreementHomeRoleDTO> agmtHomeRoles = pageModel.getMaintainAgreementDTO().getAgreementDTO()
				.getCurrentAndFutureHomeRoles();			
		
		String midSix = pageModel.getMaintainAgreementDTO().getAgreementDTO().getMiddleSix();
				
		if(!StringUtils.isBlank(midSix) && !CollectionUtils.isEmpty(agmtHomeRoles)){
			return true;
		}	
		
		return false;
	}
	
	/**
	 * Method to refresh FeedBack Panel
	 * 
	 * @param target
	 */
	private void refreshFeedbackPanel(AjaxRequestTarget target) {
		if (AgreementHierarchyPanel.this.feedBackPanel != null) {
			target.add(AgreementHierarchyPanel.this.feedBackPanel);
		}
	}

		
	/**
	 * Validate if check for Auto Gen is Enabled and Action Type is ADD
	 * 
	 * @return boolean
	 */
	private boolean isAutoGenEnabledAndADDAction() {
		Boolean isEnabled = HelpersParameterFactory.getInstance().getParameter(HelperConfigParameterTypes.AUTOGEN_BROKER_ENABLED, Boolean.class);
		
		if(isEnabled == null || isEnabled.booleanValue() == false) {
			return false;			
		}
		
		AgreementDTO dto = pageModel.getMaintainAgreementDTO().getAgreementDTO();
		return AgreementKindType.isAutoGenConsCodeEnabled(dto.getKind()) && getEditState() == EditStateType.ADD;
	}
	
	/**
	 * Validate if check for Auto Gen is Enabled
	 * 
	 * @return boolean
	 */	
	private boolean isAutoGenEnabled() {
		AgreementDTO dto = pageModel.getMaintainAgreementDTO().getAgreementDTO();
		return AgreementKindType.isAutoGenConsCodeEnabled(dto.getKind());
	}
	
	/**
	 * Reset the Agreement Role Kind Dropdown box
	 * @param selectableRoleKinds
	 * @return List<RoleKindType>
	 */
	
	private List<RoleKindType> getSelectableRoleKindChoices() {
		
		List<RoleKindType> originalRoleKindList = pageModel.getSelectableRoleKinds();
		
		List<AgreementRoleDTO> agmtRoles = pageModel.getMaintainAgreementDTO().getAgreementDTO()
				.getCurrentAndFutureAgreementRoles();
		
		if(!isAutoGenEnabled() || CollectionUtils.isEmpty(agmtRoles) || !filterRoleKinds)
			return originalRoleKindList;		
		
		List<RoleKindType> roleKindList = new ArrayList();
		
		roleKindList.addAll(originalRoleKindList);			
		
		for(AgreementRoleDTO agmtRole:agmtRoles) {
			roleKindList.removeIf(e->e.getKind() == agmtRole.getKind().intValue());
		}
				
		return roleKindList;
	}
	
	/**
	 * Validation to prevent add new role Only for New Person on ADD action
	 * @param target
	 * @return boolean
	 */
	
	private boolean validateOnAddNewRole(AjaxRequestTarget target) {
		
		if(!isAutoGenEnabled())
			return false;			
		
		List<AgreementRoleDTO> agmtRoles = pageModel.getMaintainAgreementDTO().getAgreementDTO()
				.getCurrentAndFutureAgreementRoles();
		
		if(CollectionUtils.isEmpty(agmtRoles))
			return false;	
		
		filterRoleKinds = Boolean.TRUE;
		
		
		for(AgreementRoleDTO agmtRole:agmtRoles) {
			
			if(agmtRole.getKind().intValue() == 0) {
				
				warn("Warning: Please select a Role Kind before adding a new role !");
				refreshFeedbackPanel(target);
				return true;
			}			
			
			
			if(agmtRole.getKind().intValue() != 0 && agmtRole.getRolePlayerReference().getOid() == 0) {
				
				warn("Warning: Please select a roleplayer for '"
				+RoleKindType.getRoleKindTypeForKind(agmtRole.getKind().intValue()).getDescription()+"' role first before adding a new role !");
				refreshFeedbackPanel(target);
				return true;
			}
			
		}
		
		return false;
		
	}
	
	
	
	/**
	 * Azure Defect fix - 210933
	 * Method to validate the Applicable Role like Belongs To for Brokers /Reports to for AGentcannot be removed on Maintain
	 * @param allCurrentRoles
	 * @throws ValidationException
	 */
	private void validateForRoleOnMaintain(ArrayList<AgreementRoleDTO> allCurrentRoles) throws ValidationException {
		if (!isAutoGenEnabled() || getEditState() != EditStateType.MODIFY) // If not Auto Gen the don't validate
			return;

		if (CollectionUtils.isEmpty(allCurrentRoles))
			return;

		ArrayList<String> errors = new ArrayList<String>();

		RoleKindType roleKindType = getAgreementGUIController().getApplicableRoleKindTypeForAgmtKind(
				pageModel.getMaintainAgreementDTO().getAgreementDTO().getKind());

		boolean isOrganisation = isPartyOrganisation();	
		logger.info("Jean - partyType=" + isOrganisation);
		
		if (isOrganisation) {
			// We don't validate this for organisations
			// We might want to move this to guiController when it gets more complex
			return;
		}
		
		boolean roleExists = false;

		for (AgreementRoleDTO agmRole : allCurrentRoles) {
			if (roleKindType.getKind() == agmRole.getKind()) {
				roleExists = true;
				break;
			}
		}

		if (!roleExists) {
			errors.add("The '" + roleKindType.getDescription() + "' role cannot be removed !");
			throw new ValidationException(errors);
		}
	}
	/*************************************END - SBS0510-AUTOGEN*********************************************************/


}
