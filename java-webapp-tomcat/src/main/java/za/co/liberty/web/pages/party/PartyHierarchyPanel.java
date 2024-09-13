package za.co.liberty.web.pages.party;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import javax.naming.NamingException;

import org.apache.commons.lang.SerializationUtils;
import org.apache.log4j.Logger;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.AttributeModifier;//org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow.MaskType;
import org.apache.wicket.markup.ComponentTag;
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

import za.co.liberty.agreement.common.DateUtilities;
import za.co.liberty.agreement.common.exceptions.LogicExecutionException;
import za.co.liberty.business.common.IBusinessUtilitiesBean;
import za.co.liberty.business.guicontrollers.partymaintenance.IPartyMaintenanceController;
import za.co.liberty.dto.common.MonthEndDates;
import za.co.liberty.dto.common.SearchResultBaseDataDTO;
import za.co.liberty.dto.contracting.ResultPartyDTO;
import za.co.liberty.dto.gui.context.ResultContextItemDTO;
import za.co.liberty.dto.party.PartyDTO;
import za.co.liberty.dto.party.PartyRoleDTO;
import za.co.liberty.dto.rating.DescriptionDTO;
import za.co.liberty.dto.userprofiles.ContextDTO;
import za.co.liberty.dto.userprofiles.ContextPartyDTO;
import za.co.liberty.dto.userprofiles.ISessionUserProfile;
import za.co.liberty.exceptions.data.DataNotFoundException;
import za.co.liberty.exceptions.data.ValidationException;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.helpers.lookup.ServiceLocator;
import za.co.liberty.interfaces.agreements.requests.RequestKindType;
import za.co.liberty.interfaces.party.PartyRoleType;
import za.co.liberty.interfaces.rating.description.DescriptionKindType;
import za.co.liberty.srs.type.SRSType;
import za.co.liberty.web.data.enums.ContextType;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.data.enums.PanelToRequestMapping;
import za.co.liberty.web.pages.interfaces.ISecurityPanel;
import za.co.liberty.web.pages.panels.BasePanel;
import za.co.liberty.web.pages.panels.HelperPanel;
import za.co.liberty.web.pages.party.model.MaintainPartyHierarchyPanelModel;
import za.co.liberty.web.pages.party.model.MaintainPartyPageModel;
import za.co.liberty.web.pages.search.ContextSearchPopUp;
import za.co.liberty.web.system.SRSAuthWebSession;
import za.co.liberty.web.wicket.markup.html.form.SRSDateField;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGrid;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGrid.GridSizeUnit;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGridColumn;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataProviderAdapter;
import za.co.liberty.web.wicket.markup.html.grid.SRSGridRowSelectionCheckBox;
import za.co.liberty.web.wicket.markup.repeater.data.ListDataProvider;

import com.inmethod.grid.IGridColumn;
import com.inmethod.grid.SizeUnit;

/**
 * The Panel containing all the party hirarchy detail
 * @author DZS2610
 *
 */
public class PartyHierarchyPanel extends BasePanel implements ISecurityPanel {
	private static final long serialVersionUID = 1L;
	
	private static final transient Logger logger = Logger.getLogger(PartyHierarchyPanel.class);
	
	private MaintainPartyHierarchyPanelModel panelModel; 
	
	private MaintainPartyPageModel pageModel;
	
	private ModalWindow searchWindow;
	
	private ModalWindow roleSearchWindow;
	
	private ModalWindow historyWindow;
	
	private FeedbackPanel feedBackPanel;
	
	private HelperPanel instance ;

	private SRSDataGrid rolesGrid;
	private ModalWindow  rolesHistoryWindow;
	
	private boolean initialised = false;
	
	private transient IPartyMaintenanceController partyMaintenanceController;
	
	private SRSDataGrid partyToPartyRolesGrid;
	
	private String searchContext="";
	
	private PartyRoleDTO currentworkingGridRole;
	
	private List<DescriptionDTO> distributionRolesSubTypes = new ArrayList<DescriptionDTO>();
	
	private boolean maintainLinkedAssistantsRequestExists;
	
	private boolean maintainPartnershipsRequestExists;
	
	private boolean isTypeSelected;
	
	private boolean isSubTypeSelected;
	
	private Button searchButton;
	
	private Button addButton;
	
	private ContextDTO dto;
	
	private List<PartyRoleType> roleTypesUsed = new ArrayList<PartyRoleType>(roleTypes);
	
	private List<PartyRoleType> distributionRolesUsed = new ArrayList<PartyRoleType>(distributionRoleTypes);
	
	private static List<PartyRoleType> distributionRoleTypes = new ArrayList<PartyRoleType>(4);
	
	private static List<PartyRoleType> roleTypes = new ArrayList<PartyRoleType>(4);
	static{
		//default roles used on this panel
		roleTypes.add(PartyRoleType.PARTNERTO);
		roleTypes.add(PartyRoleType.PARTNEROF);
		roleTypes.add(PartyRoleType.ISASSISTEDBY);
		roleTypes.add(PartyRoleType.ISPERSONALASISSTANTTO);
		roleTypes.add(PartyRoleType.HASDIRECTOR);		
		roleTypes.add(PartyRoleType.ISDIRECTOROF);
		distributionRoleTypes.add(PartyRoleType.ISDISTRIBUTIONGROUPADMINSTRATOR);
		distributionRoleTypes.add(PartyRoleType.ISDISTRIBUTIONGROUPMANAGER);
	}
	
	/**
	 * Default constructor
	 * @param id
	 * @param model
	 * @param editState
	 * @param feedBackPanel
	 * @param parentPage
	 */
	public PartyHierarchyPanel(String id, MaintainPartyPageModel model , 
			EditStateType editState, FeedbackPanel feedBackPanel, Page parentPage) {
		this(id, model , editState, feedBackPanel, parentPage, null);
	}
	
	
	public PartyHierarchyPanel(String id, MaintainPartyPageModel model , 
			EditStateType editState, FeedbackPanel feedBackPanel, Page parentPage, 
			List<PartyRoleType> roleTypesToUse) {
		super(id, editState,parentPage);
		this.pageModel = model;		
		this.feedBackPanel = feedBackPanel;		
		if(roleTypesToUse != null){
			roleTypesUsed = roleTypesToUse;			
		}
		
	}	
		
	
	/**
	 * Load the components on the page on first render, 
	 * so that the components are only generated when the page is displayed 
	 */
	@Override
	protected void onBeforeRender() {
		if(!initialised) {			
			initialised=true;	
			
//			initialize the panel model with the agreement role data
			initPanelModelModel();	
			List<RequestKindType> unAuthRequests = getOutStandingRequestKinds();			
			//check for existing requests FIRST as other panels use variables set here
			
			//after the outstanding requests, we check if user can actually raise requests on left of request kinds
			ISessionUserProfile user = getLoggedInUser();
			RequestKindType[] requestsForPanel = PanelToRequestMapping.getRequestKindsForPanel(PartyHierarchyPanel.class);
			Set<RequestKindType> unAvailableRequest = new HashSet<RequestKindType>(requestsForPanel.length);			
			for(RequestKindType kind : requestsForPanel){
				if(!user.isAllowRaise(kind)){
					unAvailableRequest.add(kind);
				}
			}	
			unAvailableRequest.addAll(unAuthRequests);			
			for (RequestKindType kind : unAvailableRequest) {
				if(kind == RequestKindType.MaintainLinkedAssistants){
					maintainLinkedAssistantsRequestExists = true;
					roleTypesUsed.remove(PartyRoleType.ISASSISTEDBY);	
					roleTypesUsed.remove(PartyRoleType.ISPERSONALASISSTANTTO);
				}	
				if(kind == RequestKindType.MaintainPartnerships){
					maintainPartnershipsRequestExists = true;
					roleTypesUsed.remove(PartyRoleType.PARTNERTO);	
					roleTypesUsed.remove(PartyRoleType.PARTNEROF);
				}
			}			
			add(new HierarchyForm("hierarchyForm"));
			add(searchWindow = createSearchWindow("searchPartyWindow"));				
			add(historyWindow = createHistoryWindow("historyWindow"));
			add(rolesHistoryWindow = createRolesHistoryWindow("rolesHistoryWindow"));
			add(roleSearchWindow = createRolesSearchWindow("searchRoleWindow"));
		}
		if(feedBackPanel == null){			
			feedBackPanel = this.getFeedBackPanel();		
		}
		super.onBeforeRender();
	}
	
	
	private ModalWindow createRolesSearchWindow(String id) {

		ContextSearchPopUp popUp = new ContextSearchPopUp() {

			private static final long serialVersionUID = 1L;

			@Override
			public ContextType getContextType() {
				return ContextType.PARTY_ORGANISATION_ONLY;				
			}
			
			@Override
			public void doProcessSelectedItems(AjaxRequestTarget target,
					ArrayList<ResultContextItemDTO> selectedItemList) {
				dto = SRSAuthWebSession.get().getContextDTO();
				if(selectedItemList.size() == 1){
					ResultContextItemDTO item = selectedItemList.get(0);
					ISessionUserProfile userProfile = SRSAuthWebSession.get().getSessionUser();
					if (selectedItemList.size() == 1) {
							PartyHierarchyPanel.this.doProcessSearchResult(target, item, dto, userProfile);
						
					}
					
				}
			}
		};		
		ModalWindow win = popUp.createModalWindow(id);
		win.setCookieName("partyRoleSearchPageMap");//win.setPageMapName("partyRoleSearchPageMap");
		return win;	
	}


	protected void doProcessSearchResult(AjaxRequestTarget target,
			ResultContextItemDTO item, ContextDTO dto,
			ISessionUserProfile userProfile) {
		boolean isHierarchyNode=isHierarchyNode(item.getPartyDTO());
		if(!isHierarchyNode)
		{
			error(item.getPartyDTO().getName()+" is not a hierachy node. Please ensure that your search for a hierachy node");
			target.add(getFeedBackPanel());
			target.add(this);
			return;
		}
		List<PartyRoleDTO> partyRoles = panelModel.getPartyRoles();
		for (PartyRoleDTO roleDTO:partyRoles)
		{
			if ((roleDTO.getRolePlayerReference().getOid()==item.getPartyDTO().getPartyOid())&&
					(roleDTO.getSubTypeId()==currentworkingGridRole.getSubtype())&&
					(roleDTO.getType().equals(currentworkingGridRole.getType())))
			{
				if ((roleDTO.getEffectiveTo()==currentworkingGridRole.getEffectiveTo())||
						((roleDTO.getEffectiveTo().compareTo(DateUtilities.today())!=0)&&(roleDTO.getEffectiveTo().compareTo(DateUtilities.today())<0))){
				error("Role has already been added for :"+item.getPartyDTO().getName()+" .Please end or remove the previous role before attempting to set new organisation");
				target.add(getFeedBackPanel());
				target.add(this);
				return;
			}
			}
			if (roleDTO.getOid()==0||roleDTO.equals(currentworkingGridRole))
			{
				SearchResultBaseDataDTO rolePlayerRef = new SearchResultBaseDataDTO();
				ContextPartyDTO partyContextDTO = dto.getPartyContextDTO();
				ResultPartyDTO partyDTO = item.getPartyDTO();
				rolePlayerRef.setOid(partyDTO.getPartyOid());
				rolePlayerRef.setTypeOid(partyDTO.getTypeOid());
				rolePlayerRef.setComponentOid(partyDTO.getComponentOid());
				roleDTO.setMainRolePlayerID(partyContextDTO.getPartyOid());
				roleDTO.setRolePlayerReference(rolePlayerRef);
				roleDTO.setCreationTime(new Date());
				roleDTO.setCreatedByPartyID(userProfile.getPartyOid());
				roleDTO.setSubtype(currentworkingGridRole.getSubtype());
				roleDTO.setSubTypeId(currentworkingGridRole.getSubtype());
				roleDTO.setType(currentworkingGridRole.getType());

			}
		}
		currentworkingGridRole.setRolePlayerReference(item.getPartyDTO());
		isTypeSelected = false;
		isSubTypeSelected = false;
		target.add(rolesGrid);
	}


	private boolean isHierarchyNode(ResultPartyDTO partyDTO) {
		List<Long> hierarchyNodes = new ArrayList<Long>();
		hierarchyNodes.add(SRSType.DIVISION);
		hierarchyNodes.add(SRSType.REGION);
		hierarchyNodes.add(SRSType.BRANCH);
		hierarchyNodes.add(SRSType.SUBREGION);
		hierarchyNodes.add(SRSType.UNIT);
		return (hierarchyNodes.contains(partyDTO.getTypeOid()));

	}

	private boolean isSearchEnabled()
	{
		if (isSubTypeSelected&&isTypeSelected)
		{
			return true;
		}
		return false;
	}

	/**
	 * Create a search popup
	 * @param id
	 * @return
	 */
	private ModalWindow createSearchWindow(String id) {

		ContextSearchPopUp popUp = new ContextSearchPopUp() {

			@Override
			public ContextType getContextType() {
				return ContextType.PARTY;				
			}

			@Override
			public void doProcessSelectedItems(AjaxRequestTarget target,
					ArrayList<ResultContextItemDTO> selectedItemList) {
				//fill in the party into the role
				if(selectedItemList.size() == 1){
					ResultContextItemDTO item = selectedItemList.get(0);
					currentworkingGridRole.setRolePlayerReference(item.getPartyDTO());
					if (searchContext.equalsIgnoreCase("hierachy"))
					{
					target.add(partyToPartyRolesGrid);
					}else{
						target.add(rolesGrid);
					}
				}
			}
		};		
		ModalWindow win = popUp.createModalWindow(id);
		win.setCookieName("partyRoleSearchPageMap");//win.setPageMapName("partyRoleSearchPageMap");
		return win;	
	}
	
	/**
	 * Initialize the panel model used for this panel
	 *
	 */
	private void initPanelModelModel() {
		if(pageModel.getMaintainPartyHierarchyPanelModel() != null){
			panelModel = pageModel.getMaintainPartyHierarchyPanelModel();
			return;
		}
		panelModel = new MaintainPartyHierarchyPanelModel();		
		//get the party to party roles from the DB			
		ArrayList<PartyRoleDTO> partyToPartyRoles = new ArrayList<PartyRoleDTO>();
		ArrayList<PartyRoleDTO> partyRoles = new ArrayList<PartyRoleDTO>();
		ArrayList<PartyRoleType> roleTypes = new ArrayList<PartyRoleType>();
		partyRoles = getDistributionGroupRoles(roleTypes);
		if(pageModel != null && pageModel.getPartyDTO() != null && pageModel.getPartyDTO().getOid() > 0){
			partyToPartyRoles = new ArrayList<PartyRoleDTO>(getPartyMaintenanceController().getPartyRolesForPartyOIDForTheHierarchyPanel(pageModel.getPartyDTO().getOid(),roleTypesUsed));
			
		}
		distributionRolesSubTypes.addAll(getPartyMaintenanceController().findValuesByDescriptionKind(DescriptionKindType.CHANNEL_GROUP_KIND.getKind()));
		distributionRolesSubTypes.addAll(getPartyMaintenanceController().findValuesByDescriptionKind(DescriptionKindType.SUPER_DIVISION_KIND.getKind()));
		panelModel.setDistributionGroupSubType(distributionRolesSubTypes);
		panelModel.setPartyToPartyRoles(partyToPartyRoles);	
		panelModel.setPartyRoles(partyRoles);
		panelModel.setPartyRolesBeforeImage((List<PartyRoleDTO>) SerializationUtils.clone(partyRoles));
		panelModel.setPartyToPartyRolesBeforeImage((List<PartyRoleDTO>) SerializationUtils.clone(partyToPartyRoles));
		pageModel.setMaintainPartyHierarchyPanelModel(panelModel);
	}


	private ArrayList<PartyRoleDTO> getDistributionGroupRoles(
			ArrayList<PartyRoleType> roleTypes) {
		ArrayList<PartyRoleDTO> partyRoles = new ArrayList<PartyRoleDTO>();
		dto = SRSAuthWebSession.get().getContextDTO();
		ResultPartyDTO resultPartyDTO = new ResultPartyDTO();
		resultPartyDTO.setTypeOid(dto.getPartyContextDTO().getTypeOid());
		if (isHierarchyNode(resultPartyDTO))
		{
			PartyRoleType partyRoleType = PartyRoleType.ISDISTRIBUTIONGROUPADMINSTRATOR;
			partyRoleType.setInverseRole(true);
			roleTypes.add(partyRoleType);
			PartyRoleType partyRoleType2 = PartyRoleType.ISDISTRIBUTIONGROUPMANAGER;
			partyRoleType2.setInverseRole(true);
			roleTypes.add(partyRoleType2);
			PartyDTO partyDTO = pageModel.getPartyDTO();
			if (dto.getPartyContextDTO()!=null)
			{
			partyRoles = new ArrayList<PartyRoleDTO>(getPartyMaintenanceController().getPartyRolesForPartyOIDForTheHierarchyPanel(dto.getPartyContextDTO().getPartyOid(), roleTypes));
		}
		}else{
			PartyRoleType partyRoleType = PartyRoleType.ISDISTRIBUTIONGROUPADMINSTRATOR;
			partyRoleType.setInverseRole(false);
			roleTypes.add(partyRoleType);
			PartyRoleType partyRoleType2 = PartyRoleType.ISDISTRIBUTIONGROUPMANAGER;
			partyRoleType2.setInverseRole(false);
			roleTypes.add(partyRoleType2);
			PartyDTO partyDTO = pageModel.getPartyDTO();
			if (partyDTO!=null)
			{
			partyRoles = new ArrayList<PartyRoleDTO>(getPartyMaintenanceController().getPartyRolesForPartyOIDForTheHierarchyPanel(partyDTO.getOid(), roleTypes));
		}
		}
		return partyRoles;
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
				return new PartyRolesHistoryPage(window,pageModel.getPartyDTO().getOid(),roleTypesUsed);
			}			
		});			
		// Initialise window settings
		window.setMinimalHeight(500);
		window.setInitialHeight(500);
		window.setMinimalWidth(700);
		window.setInitialWidth(700);
		window.setMaskType(MaskType.SEMI_TRANSPARENT);
		window.setCssClassName(ModalWindow.CSS_CLASS_GRAY);	
		window.setOutputMarkupId(true);
		window.setOutputMarkupPlaceholderTag(true);
		window.setCookieName("RoleHistoryPageMap2");//window.setPageMapName("RoleHistoryPageMap2");
		return window;
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
			add(partyToPartyRolesGrid = createPartyToPartyRolesGrid("partyRolesGrid",panelModel));			
			add(createRemovePartyRoleButton("removePartyRoleButton"));
			add(createOtherRemovePartyRoleButton("removeOtherPartyRoleButton"));
			add(createAddPartyRoleButton("addPartyRoleButton"));	
			add(createPartyRoleHistoryButton("historyButton"));
			add(rolesGrid = createRolesgrid("rolesGrid",pageModel));
			add(createAddRolesButton("addRolesButton"));
			add(createRolesHistoryButton("rolesHistoryButton"));
	
			add(new IFormValidator() {
				private static final long serialVersionUID = 1L;

				public void validate(Form arg0) {
					if (getEditState().isViewOnly()) {
						return;
					}
					//validate all the roles 					
					try {
						//we first make sure that if we have IsAssistedBy roles that the user has selected a target agreement number
						//we do this as the request allows no agmt to be selected but this is to overcome the inverse roles problem
						ContextDTO context = SRSAuthWebSession.get().getContextDTO();
						for(PartyRoleDTO partyRole : panelModel.getPartyToPartyRoles()){
							if(partyRole.getPartyRoleType() == PartyRoleType.ISASSISTEDBY
									&& (context.getAgreementContextDTO() == null
									|| context.getAgreementContextDTO().getAgreementNumber() == null
									|| (context.getAgreementContextDTO().getAgreementNumber() <= 0))){
								throw new ValidationException("Please select an agreement in the context that will be used for this request, The " 
										+ PartyRoleType.ISASSISTEDBY.getDescription() + " role needs an intermediary and agreement set in the context panel above");
							}
						//MXM1904 Added for Partnership Project
							if(partyRole.getPartyRoleType() == PartyRoleType.PARTNERTO
									&& (context.getAgreementContextDTO() == null
									|| context.getAgreementContextDTO().getAgreementNumber() == null
									|| (context.getAgreementContextDTO().getAgreementNumber() <= 0))){
								throw new ValidationException("Please select an agreement in the context that will be used for this request, The " 
										+ PartyRoleType.PARTNERTO.getDescription() + " role needs an intermediary and agreement set in the context panel above");
						
							}
						}			
						List<PartyRoleDTO> roles = new ArrayList<PartyRoleDTO>(panelModel.getPartyToPartyRoles());
						roles.addAll(panelModel.getPartyToPartyRoleRemovals());
						getPartyMaintenanceController().validatePartyRoles(roles);
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
	private Button createPartyRoleHistoryButton(String id) {
		final Button button = new Button(id);
		button.add(new AjaxFormComponentUpdatingBehavior("click") {
			private static final long serialVersionUID = 0L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
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
	 * Create the button to remove a party role
	 * 
	 * @return
	 */
	private Button createRemovePartyRoleButton(String id) {
		final Button button = new Button(id);
		button.add(new AjaxFormComponentUpdatingBehavior("click") {
			private static final long serialVersionUID = 0L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				List<Object> selections = partyToPartyRolesGrid.getSelectedItemObjects();				
				for (Object selection : selections) {					
					PartyRoleDTO gridRole = (PartyRoleDTO) selection;
					panelModel.getPartyToPartyRoles().remove(gridRole);	
					//add the role to the removals list and end date it
					if(gridRole.getOid() > 0){
						//end it when it was created so it never existed and add it to removals list
						gridRole.setEffectiveTo(gridRole.getEffectiveFrom());
						panelModel.getPartyToPartyRoleRemovals().add(gridRole);
					}
				}					
				target.add(partyToPartyRolesGrid);
			}
		});		
		if(getEditState() == EditStateType.AUTHORISE){			
			button.setVisible(false);
		}
		button.setOutputMarkupId(true);
		button.setOutputMarkupPlaceholderTag(true);
		setUpbutton(button,true);//!(existingServicingRequest && existingHierarchyRequest));
		return button;
	}
	
	private Button createOtherRemovePartyRoleButton(String id) {
		final Button button = new Button(id);
		button.add(new AjaxFormComponentUpdatingBehavior("click") {
			private static final long serialVersionUID = 0L;
		
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				List<Object> selections = rolesGrid.getSelectedItemObjects();				
				for (Object selection : selections) {					
					PartyRoleDTO gridRole = (PartyRoleDTO) selection;
					panelModel.getPartyRoles().remove(gridRole);	
					//add the role to the removals list and end date it
					if(gridRole.getOid() > 0){
						//end it when it was created so it never existed and add it to removals list
						gridRole.setEffectiveTo(gridRole.getEffectiveFrom());
						panelModel.getPartyRoleRemovals().add(gridRole);
					}
				}					
				target.add(rolesGrid);
			}
		});	
		 dto = SRSAuthWebSession.get().getContextDTO();
		if((dto.getPartyContextDTO().getTypeOid()!=SRSType.PERSON)
				||(getEditState() == EditStateType.AUTHORISE)){			
			button.setVisible(false);
		}
		button.setOutputMarkupId(true);
		button.setOutputMarkupPlaceholderTag(true);
		setUpbutton(button,true);//!(existingServicingRequest && existingHierarchyRequest));
		return button;
	}
	
	/**
	 * Create the button to add a party role
	 * 
	 * @return
	 */
	private Button createAddPartyRoleButton(String id) {
		final Button button = new Button(id);
		button.add(new AjaxFormComponentUpdatingBehavior("click") {
			private static final long serialVersionUID = 0L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {				
				PartyRoleDTO rolePartyDTO = new PartyRoleDTO();				
				rolePartyDTO.setRolePlayerReference(new ResultPartyDTO());	
				rolePartyDTO.setEffectiveFrom(new Date());	
				//AJM0308 remove hasDirector and add isDirectorOf option when person
				//  add hasDirector and remove isDirectorOf option when not person
				if(roleTypesUsed.contains(PartyRoleType.ISDIRECTOROF)) 
				{			
					roleTypesUsed.remove(PartyRoleType.ISDIRECTOROF);		
				}
				
				if((dto.getPartyContextDTO().getTypeOid()==SRSType.PERSON) && 
						(roleTypesUsed.contains(PartyRoleType.HASDIRECTOR))) 
				{			
					roleTypesUsed.remove(PartyRoleType.HASDIRECTOR);		
				}
//				
//				else
//				{			
//					roleTypesUsed.add(PartyRoleType.HASDIRECTOR);		
//					roleTypesUsed.remove(PartyRoleType.ISDIRECTOROF);
//				}	
				
//				if((dto.getPartyContextDTO().getTypeOid()==SRSType.PERSON)||(roleTypesUsed.contains(PartyRoleType.HASDIRECTOR))) 
//				{			
//					roleTypesUsed.remove(PartyRoleType.HASDIRECTOR);		
//					roleTypesUsed.add(PartyRoleType.ISDIRECTOROF);
//				}
//				
//				else
//				{			
//					roleTypesUsed.add(PartyRoleType.HASDIRECTOR);		
//					roleTypesUsed.remove(PartyRoleType.ISDIRECTOROF);
//				}	
				
				
				if(pageModel.getPartyDTO() != null){
					rolePartyDTO.setMainRolePlayerID(pageModel.getPartyDTO().getOid());
				}
				//if the list is only one size big then we auto select the role
				if(roleTypesUsed.size() == 1){
					rolePartyDTO.setPartyRoleType(roleTypesUsed.get(0));
				}
				panelModel.getPartyToPartyRoles().add(rolePartyDTO);
				target.add(partyToPartyRolesGrid);				
			}
		});		
		if(getEditState() == EditStateType.AUTHORISE){			
			button.setVisible(false);
		}
		button.setOutputMarkupId(true);
		button.setOutputMarkupPlaceholderTag(true);
		setUpbutton(button, true);//!(existingServicingRequest && existingHierarchyRequest));
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
	 * Creates the grid for the party to party roles 
	 * @param panelModel
	 * @return
	 */
	private SRSDataGrid createPartyToPartyRolesGrid(String id,MaintainPartyHierarchyPanelModel panelModel){
		List<PartyRoleDTO> partyTopartyRoles = panelModel.getPartyToPartyRoles();
		if(partyTopartyRoles == null){
			partyTopartyRoles = new ArrayList<PartyRoleDTO>(0);
		}		
		List<PartyRoleDTO> nonSelectable = new ArrayList<PartyRoleDTO>();
		//non selectable will be determined via outstanding requests
		if(maintainLinkedAssistantsRequestExists){
			for(PartyRoleDTO role : partyTopartyRoles){
				if(maintainLinkedAssistantsRequestExists && role.getType() == PartyRoleType.ISASSISTEDBY.getType()){
					nonSelectable.add(role);					
				}				
			}
		}		
		
		if(maintainPartnershipsRequestExists){
			for(PartyRoleDTO role : partyTopartyRoles){
				if(maintainPartnershipsRequestExists && role.getType() == PartyRoleType.PARTNERTO.getType()){
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
		
		SRSDataGrid grid = new SRSDataGrid(id, new SRSDataProviderAdapter(
				new ListDataProvider<PartyRoleDTO>(partyTopartyRoles)),
				getPartyToPartyRoleColumns(), getEditState(),nonSelectable);		
		grid.setCleanSelectionOnPageChange(false);
		grid.setClickRowToSelect(false);
		grid.setAllowSelectMultiple(true);
		//grid.setGridWidth(650, GridSizeUnit.PIXELS);
		grid.setGridWidth(99, GridSizeUnit.PERCENTAGE);
		grid.setRowsPerPage(5);
		grid.setContentHeight(140, SizeUnit.PX);
		return grid;
	}
	
	
	private List<IGridColumn> getPartyToPartyRoleColumns() {		
		Vector<IGridColumn> cols = new Vector<IGridColumn>();		
		if (!getEditState().isViewOnly()) {
			SRSGridRowSelectionCheckBox col = new SRSGridRowSelectionCheckBox(
					"checkBox");
			cols.add(col.setInitialSize(30));
		}
		
		//dont display the role type column if there is only one role being used		
		if(roleTypesUsed.size() != 1){		
			//add in the role type selection
			cols.add(new SRSDataGridColumn<PartyRoleDTO>("type",
					new Model<String>("type"), "type", "type",
					getEditState()) {
				private static final long serialVersionUID = 1L;
	
				@Override
				public Panel newCellPanel(WebMarkupContainer parent,
						String componentId, IModel rowModel,
						String objectProperty, EditStateType state,
						final PartyRoleDTO data) {	
					if (maintainLinkedAssistantsRequestExists || 
							getEditState().isViewOnly() 
							|| (data.getOid() != 0)) {
						//create label with type and display	
						PartyRoleType role = PartyRoleType.getPartyRoleType(data.getType(), data.isInverseRole());
						return HelperPanel.getInstance(componentId, new Label("value",(role != null) ? role.getDescription() : ""));
					}
		logger.info("maintainPartnershipsRequestExists " + maintainPartnershipsRequestExists );
		logger.info("getEditState().isViewOnly()"+ getEditState().isViewOnly());
		logger.info(" data.getOid()" + data.getOid());			
					
					
					if (maintainPartnershipsRequestExists || 
							getEditState().isViewOnly() 
							|| (data.getOid() != 0)) {
						//create label with type and display	
						PartyRoleType role = PartyRoleType.getPartyRoleType(data.getType(), data.isInverseRole());
						return HelperPanel.getInstance(componentId, new Label("value",(role != null) ? role.getDescription() : ""));
					}
					DropDownChoice<Object> dropdown = new DropDownChoice<Object>("value",new PropertyModel(data,objectProperty){
						@Override
							public Object getObject() {
								//return one of the values in the static list						
								Long id = (Long) super.getObject();
								if(id == null){
									return null;							
								}
								for(PartyRoleType type : roleTypesUsed){
									if(type.getType() == id && (data.isInverseRole() == type.isInverseRole())){
										return type;
									}
								}
								return null;
							}
							@Override
							public void setObject(Object arg0) {		
	//							we have to set two values here
								super.setObject(((PartyRoleType)arg0).getType());
								data.setInverseRole(((PartyRoleType)arg0).isInverseRole());
							}
					},roleTypesUsed,new ChoiceRenderer("description"));	
					//update on modify
					dropdown.add(new AjaxFormComponentUpdatingBehavior("change"){
						@Override
						protected void onUpdate(AjaxRequestTarget arg0) {
							//do nothing, just want the model to update with ajax		
						}					
					});
					dropdown.setLabel(new Model<String>("Role Type"));
					dropdown.setRequired(true);
					
					//create dropdown of selectable types				
					HelperPanel dropdownPanel = HelperPanel.getInstance(componentId, dropdown);				
					return dropdownPanel;
				}
	
			}.setInitialSize(160));
		}
		
		//add in the name column
		cols.add(new SRSDataGridColumn<PartyRoleDTO>("rolePlayerReference.name",
				new Model<String>("Party Name"), "rolePlayerReference.name", "rolePlayerReference.name", getEditState()).setInitialSize(140).setWrapText(true));
//		add in the uacfid column
		cols.add(new SRSDataGridColumn<PartyRoleDTO>("rolePlayerReference.uacfID",
				new Model<String>("UACFID"), "rolePlayerReference.uacfID", "rolePlayerReference.uacfID", getEditState()).setInitialSize(80));
		
		
//		add search button, don't display this column on view
		if(getEditState() == null ||  !getEditState().isViewOnly()){
			cols.add(new SRSDataGridColumn<PartyRoleDTO>("searchParty",
					new Model<String>("Search"), "searchParty", getEditState()){	
				
						private static final long serialVersionUID = 1L;							

						@Override
						public Panel newCellPanel(WebMarkupContainer parent, String componentId, IModel rowModel, String objectProperty, EditStateType state, final PartyRoleDTO data) {
							if(data.getId() == 0){
								Button searchButton = new Button("value", new Model<String>("Search"));	
								searchButton.add(new AjaxFormComponentUpdatingBehavior("click"){									
									private static final long serialVersionUID = 1L;
									@Override
									protected void onUpdate(AjaxRequestTarget target) {	
										searchContext="hierachy";
										currentworkingGridRole = data;
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
		
//		the effective dates of the role
		cols.add(new SRSDataGridColumn<PartyRoleDTO>("effectiveFrom",
				new Model<String>("Start Date"), "effectiveFrom", "effectiveFrom",
				getEditState()) {
			private static final long serialVersionUID = 1L;

			@Override
			public Panel newCellPanel(WebMarkupContainer parent,
					String componentId, IModel rowModel,
					String objectProperty, EditStateType state,
					PartyRoleDTO data) {	
				if (maintainLinkedAssistantsRequestExists || 
						getEditState().isViewOnly() 
						//|| (data.getOid() != 0 && DateUtil.getInstance().compareDatePart(data.getEffectiveFrom(),new Date()) <= 0)
						|| data.getOid() != 0){
					return super.newCellPanel(parent, componentId,
							rowModel, objectProperty, state, data);
				}
				if (maintainPartnershipsRequestExists || 
						getEditState().isViewOnly() 
						//|| (data.getOid() != 0 && DateUtil.getInstance().compareDatePart(data.getEffectiveFrom(),new Date()) <= 0)
						|| data.getOid() != 0){
					return super.newCellPanel(parent, componentId,
							rowModel, objectProperty, state, data);
				}
				SRSDateField startDate = new SRSDateField("value",
						new PropertyModel(data, objectProperty));
				startDate.add(new AttributeModifier("size", "12"));
				startDate.add(new AttributeModifier("maxlength", "10"));
				startDate.add(new AttributeModifier("readonly","true"));
				startDate.setRequired(true);
				startDate.setLabel(new Model<String>("Start Date"));
				startDate.add(new AjaxFormComponentUpdatingBehavior("change") {
					@Override
					protected void onUpdate(AjaxRequestTarget target) {
						//do nothing just want the object value updated
					}
				});		
				startDate.add(startDate.newDatePicker());
				return HelperPanel.getInstance(componentId, startDate, true);
				
			}

		}.setInitialSize(110));

		cols.add(new SRSDataGridColumn<PartyRoleDTO>("effectiveTo",
				new Model<String>("End Date"), "effectiveTo", "effectiveTo", getEditState()) {

			@Override
			public Panel newCellPanel(WebMarkupContainer parent,
					String componentId, IModel rowModel,
					String objectProperty, EditStateType state,
					PartyRoleDTO data) {
				if (maintainLinkedAssistantsRequestExists ||
						getEditState().isViewOnly()){
					return super.newCellPanel(parent, componentId,
							rowModel, objectProperty, state, data);
				}
				if (maintainPartnershipsRequestExists ||
						getEditState().isViewOnly()){
					return super.newCellPanel(parent, componentId,
							rowModel, objectProperty, state, data);
				}
				SRSDateField endDate =  new SRSDateField("value",
						new PropertyModel(data, objectProperty));
				endDate.add(new AttributeModifier("size", "12"));
				endDate.add(new AttributeModifier("maxlength", "10"));
				endDate.setLabel(new Model<String>("End Date"));
				endDate.add(new AjaxFormComponentUpdatingBehavior("change") {
					@Override
					protected void onUpdate(AjaxRequestTarget target) {
						//do nothing just want the object value updated
					}
				});	
				endDate.add(endDate.newDatePicker());
				return HelperPanel.getInstance(componentId, endDate, true);
				
			}

		}.setInitialSize(110));
		
		return cols;
	}


	/**
	 * get the PartyMaintenanceController bean
	 * @return
	 */
	private IPartyMaintenanceController getPartyMaintenanceController(){
		if(partyMaintenanceController == null){
			try {
				partyMaintenanceController = ServiceLocator.lookupService(IPartyMaintenanceController.class);
			} catch (NamingException e) {
				throw new CommunicationException(e);
			}
		}
		return partyMaintenanceController;
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
	
	private ModalWindow createRolesHistoryWindow(String id) {
		final ModalWindow window = new ModalWindow(id);
		window.setTitle("History");		
		// Create the page
		window.setPageCreator(new ModalWindow.PageCreator() {
			private static final long serialVersionUID = 1L;
			public Page createPage() {		
				dto = SRSAuthWebSession.get().getContextDTO();
				ContextPartyDTO selectedParty = null;
				if (dto != null) {
					selectedParty = dto.getPartyContextDTO();
				}
				return new ShowAllRolesPage(historyWindow, selectedParty, pageModel);

			}			
		});			
		// Initialise window settings
		window.setMinimalHeight(500);
		window.setInitialHeight(500);
		window.setMinimalWidth(700);
		window.setInitialWidth(700);
		window.setMaskType(MaskType.SEMI_TRANSPARENT);
		window.setCssClassName(ModalWindow.CSS_CLASS_GRAY);	
		window.setOutputMarkupId(true);
		window.setOutputMarkupPlaceholderTag(true);
		window.setCookieName("RoleHistory");//window.setPageMapName("RoleHistory");
		return window;
	}
	private Button createRolesHistoryButton(String id) {
		final Button button = new Button(id);
		button.add(new AjaxFormComponentUpdatingBehavior("click") {
			private static final long serialVersionUID = 0L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				rolesHistoryWindow.show(target);
			}
		});
		if ((dto.getPartyContextDTO().getTypeOid()!=SRSType.PERSON)||!getEditState().isViewOnly() 
				|| getEditState() == EditStateType.AUTHORISE) {
			button.setEnabled(false);
			button.setVisible(false);
		}		
		button.setOutputMarkupId(true);
		button.setOutputMarkupPlaceholderTag(true);		
		return button;
	}
	private Button createAddRolesButton(String id) {
		addButton = new Button(id);
		addButton.add(new AjaxFormComponentUpdatingBehavior("click") {
			private static final long serialVersionUID = 0L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {				
				PartyRoleDTO partyRoleDTO = new PartyRoleDTO();
				MonthEndDates libertyMonthEndDates = getLibertyMonthEndDates(); 
				partyRoleDTO.setEffectiveTo(DateUtilities.getDateFromString("9999-12-31"));
				if (libertyMonthEndDates!=null){
				partyRoleDTO.setEffectiveFrom(libertyMonthEndDates.getMonthStart());
				}
				//if the list is only one size big then we auto select the role
				if(roleTypesUsed.size() == 1){
					partyRoleDTO.setPartyRoleType(roleTypesUsed.get(0));
				}
				for (PartyRoleDTO dto:panelModel.getPartyRoles())
				{
					dto.setEffectiveTo(new Date());
				}
				panelModel.getPartyRoles().add(partyRoleDTO);
				target.add(rolesGrid);
				addButton.setEnabled(false);
				target.add(addButton);
			}

		});		
		if((dto.getPartyContextDTO().getTypeOid()!=SRSType.PERSON)
				||(getEditState() == EditStateType.AUTHORISE)){			
			addButton.setVisible(false);
		}
		addButton.setOutputMarkupId(true);
		addButton.setOutputMarkupPlaceholderTag(true);
		setUpbutton(addButton, true);//!(existingServicingRequest && existingHierarchyRequest));
		return addButton;
	}
	private SRSDataGrid createRolesgrid(String id,
			MaintainPartyPageModel pageModel) {
		List<PartyRoleDTO> partyRoles = panelModel.getPartyRoles();
		List<PartyRoleDTO> nonSelectable = new ArrayList<PartyRoleDTO>();
		SRSDataGrid grid = new SRSDataGrid(id, new SRSDataProviderAdapter(
				new ListDataProvider<PartyRoleDTO>(partyRoles)),
				getPartyRoleColumns(), getEditState(),nonSelectable);		
		grid.setCleanSelectionOnPageChange(false);
		grid.setClickRowToSelect(false);
		grid.setAllowSelectMultiple(true);
		grid.setGridWidth(99, GridSizeUnit.PERCENTAGE);
		grid.setRowsPerPage(5);
		grid.setContentHeight(140, SizeUnit.PX);
		return grid;
	}

	private List<IGridColumn> getPartyRoleColumns() {
		Vector<IGridColumn> cols = new Vector<IGridColumn>();		
		if (!getEditState().isViewOnly()) {
			SRSGridRowSelectionCheckBox col = new SRSGridRowSelectionCheckBox(
					"checkBox");
			cols.add(col.setInitialSize(30));
		}
				//dont display the role type column if there is only one role being used		
		final List<DescriptionDTO> distributionGroupSubType = panelModel.getDistributionGroupSubType();
			if(distributionGroupSubType.size() != 1){		
				//add in the role type selection
				cols.add(new SRSDataGridColumn<PartyRoleDTO>("type",
						new Model<String>("Type"), "type", "type",
						getEditState()) {
					private static final long serialVersionUID = 1L;
		
					@Override
					public Panel newCellPanel(WebMarkupContainer parent,
							String componentId, IModel rowModel,
							String objectProperty, EditStateType state,
							final PartyRoleDTO data) {	
						if (maintainLinkedAssistantsRequestExists || 
								getEditState().isViewOnly() 
								|| (data.getOid() != 0)) {
							//create label with type and display	
							PartyRoleType role = PartyRoleType.getPartyRoleType(data.getType(), data.isInverseRole());
							return HelperPanel.getInstance(componentId, new Label("value",(role != null) ? role.getDescription() : ""));
						}
			logger.info("maintainPartnershipsRequestExists " + maintainPartnershipsRequestExists );
			logger.info("getEditState().isViewOnly()"+ getEditState().isViewOnly());
			logger.info(" data.getOid()" + data.getOid());			
						
						
						if ((dto.getPartyContextDTO().getTypeOid()!=SRSType.PERSON) || 
								getEditState().isViewOnly() 
								|| (data.getOid() != 0)) {
							//create label with type and display
							PartyRoleType role = null;
							for (PartyRoleType partyRoleType:distributionRolesUsed)
							{
								if (data.getType()==partyRoleType.getType())
								{
									role=partyRoleType;
									break;
								}
							}
							
							return HelperPanel.getInstance(componentId, new Label("value",(role != null) ? role.getDescription() : ""));
						}
						DropDownChoice<Object> dropdown = new DropDownChoice<Object>("value",new PropertyModel(data,objectProperty){
							@Override
								public Object getObject() {
									//return one of the values in the static list						
									Long id = (Long) super.getObject();
									if(id == null){
										return null;							
									}
									for(PartyRoleType type : distributionRolesUsed){
										if(type.getType() == id ){
											return type;
										}
									}
									return null;
								}
								@Override
								public void setObject(Object arg0) {		
		//							we have to set two values here
									super.setObject(((PartyRoleType)arg0).getType());
									data.setType(((PartyRoleType)arg0).getType());
								}
						},distributionRolesUsed,new ChoiceRenderer("description"));	
						//update on modify
						dropdown.add(new AjaxFormComponentUpdatingBehavior("change"){
							@Override
							protected void onUpdate(AjaxRequestTarget arg0) {
								if (data.getType()!=0&&data.getType()!=null){
								isTypeSelected=true;
								}else{
									isSubTypeSelected =false;
								}
								if (isSearchEnabled())
								{
									searchButton.setEnabled(true);
									arg0.add(searchButton);
									arg0.add(addButton);
								}else{
									searchButton.setEnabled(false);
									arg0.add(searchButton);
									arg0.add(addButton);
								}
							}					
						});
						dropdown.setLabel(new Model<String>("Role Type"));
						dropdown.setRequired(true);
						
						//create dropdown of selectable types				
						HelperPanel dropdownPanel = HelperPanel.getInstance(componentId, dropdown);				
						return dropdownPanel;
					}
		
				}.setInitialSize(200));
				
				// Sub-Type
				
				cols.add(new SRSDataGridColumn<PartyRoleDTO>("subtype",
						new Model<String>("Sub type"), "subtype", "subtype",
						getEditState()) {
					private static final long serialVersionUID = 1L;
		
					@Override
					public Panel newCellPanel(WebMarkupContainer parent,
							String componentId, IModel rowModel,
							String objectProperty, EditStateType state,
							final PartyRoleDTO data) {	
						if ((dto.getPartyContextDTO().getTypeOid()!=SRSType.PERSON) || 
								getEditState().isViewOnly() 
								|| (data.getOid() != 0)) {
							DescriptionDTO descriptionDTO = null;
							for (DescriptionDTO dto:distributionGroupSubType)
							{
								if (data.getSubTypeId()==dto.getUniqId())
								{
									descriptionDTO = dto;
								}
							}
							//create label with type and display	
					return HelperPanel.getInstance(componentId, new Label("value",(descriptionDTO != null) ? descriptionDTO.getDescription() : ""));
						}
			logger.info("maintainPartnershipsRequestExists " + maintainPartnershipsRequestExists );
			logger.info("getEditState().isViewOnly()"+ getEditState().isViewOnly());
			logger.info(" data.getOid()" + data.getOid());			
						
						
						if ((dto.getPartyContextDTO().getTypeOid()!=SRSType.PERSON) || 
								getEditState().isViewOnly() 
								|| (data.getOid() != 0)) {
							//create label with type and display
							DescriptionDTO description = null;
							for (DescriptionDTO descriptionDTO:distributionGroupSubType)
							{
								if (descriptionDTO.getUniqId()==data.getSubTypeId())
								{
									description = descriptionDTO;
								}
							}
						return HelperPanel.getInstance(componentId, new Label("value",(description != null) ? description.getDescription() : ""));
						}
						DropDownChoice<Object> dropdown = new DropDownChoice<Object>("value",new PropertyModel(data,objectProperty){
							@Override
								public Object getObject() {
									//return one of the values in the static list	
								if (data.getSubtype()!=0)
								{
									super.setObject(data.getSubtype());
								}
								Object object = super.getObject();
								if (object==null){
									return null;
								}
									int id = (Integer) super.getObject();
									if(id == 0){
										return 0;							
									}
									for(DescriptionDTO dto : distributionGroupSubType){
										if(dto.getUniqId() == id ){
											return dto;
										}
									}
									return null;
								}
								@Override
								public void setObject(Object arg0) {		
		//							we have to set two values here
									super.setObject(((DescriptionDTO)arg0).getUniqId());
									data.setSubtype(((DescriptionDTO)arg0).getUniqId());
								
								}
						},distributionGroupSubType,new ChoiceRenderer("description"));	
						//update on modify
						dropdown.add(new AjaxFormComponentUpdatingBehavior("change"){
							@Override
							protected void onUpdate(AjaxRequestTarget arg0) {
								if (data.getSubtype()!=0){
								isSubTypeSelected = true;
								}else{
									isSubTypeSelected = false;
								}
								if (isSearchEnabled())
								{
									searchButton.setEnabled(true);
									arg0.add(searchButton);
									arg0.add(addButton);
								}else{
									searchButton.setEnabled(false);
									arg0.add(searchButton);
									arg0.add(addButton);
								}
							}					
						});
						dropdown.setLabel(new Model<String>("Sub Type"));
						dropdown.setRequired(true);
						
						//create dropdown of selectable types				
						HelperPanel dropdownPanel = HelperPanel.getInstance(componentId, dropdown);				
						return dropdownPanel;
					}
		
				}.setInitialSize(160));
			}
			//add in the name column
			cols.add(new SRSDataGridColumn<PartyRoleDTO>("rolePlayerReference.name",
					new Model<String>("Party Name"), "rolePlayerReference.name", "rolePlayerReference.name", getEditState()).setInitialSize(140).setWrapText(true));
			
			
//			add search button, don't display this column on view
			if((dto.getPartyContextDTO().getTypeOid()==SRSType.PERSON)&&(getEditState() == null ||  !getEditState().isViewOnly())){
				cols.add(new SRSDataGridColumn<PartyRoleDTO>("searchParty",
						new Model<String>("Search"), "searchParty", getEditState()){	
					
							private static final long serialVersionUID = 1L;							

							@Override
							public Panel newCellPanel(WebMarkupContainer parent, String componentId, IModel rowModel, String objectProperty, EditStateType state, final PartyRoleDTO data) {
								if(data.getId() == 0){
									searchButton = new Button("value", new Model<String>("Search"));
									searchButton.setOutputMarkupPlaceholderTag(true);
									searchButton.setOutputMarkupId(true);
									searchButton.setEnabled(false);
									searchButton.add(new AjaxFormComponentUpdatingBehavior("click"){									
										private static final long serialVersionUID = 1L;
										@Override
										protected void onUpdate(AjaxRequestTarget target) {	
											currentworkingGridRole = data;
											roleSearchWindow.show(target);										
										}									
									});
									instance = HelperPanel.getInstance(componentId,searchButton);
									instance.setOutputMarkupId(true);
									instance.setOutputMarkupPlaceholderTag(true);
									return instance;	
								}else{
									return new EmptyPanel(componentId);
								}
							}				
					
				}.setInitialSize(64));
			}	
//				the effective dates of the role
				cols.add(new SRSDataGridColumn<PartyRoleDTO>("effectiveFrom",
						new Model<String>("Start Date"), "effectiveFrom", "effectiveFrom",
						getEditState()) {
					private static final long serialVersionUID = 1L;

					@Override
					public Panel newCellPanel(WebMarkupContainer parent,
							String componentId, IModel rowModel,
							String objectProperty, EditStateType state,
							final PartyRoleDTO data) {	
						if ((dto.getPartyContextDTO().getTypeOid()!=SRSType.PERSON)|| 
								getEditState().isViewOnly() 
								//|| (data.getOid() != 0 && DateUtil.getInstance().compareDatePart(data.getEffectiveFrom(),new Date()) <= 0)
								|| data.getOid() != 0){
							return super.newCellPanel(parent, componentId,
									rowModel, objectProperty, state, data);
						}
						if ((dto.getPartyContextDTO().getTypeOid()!=SRSType.PERSON) || 
								getEditState().isViewOnly() 
								//|| (data.getOid() != 0 && DateUtil.getInstance().compareDatePart(data.getEffectiveFrom(),new Date()) <= 0)
								|| data.getOid() != 0){
							return super.newCellPanel(parent, componentId,
									rowModel, objectProperty, state, data);
						}
						IModel<Object> model = new IModel<Object>() {
							private static final long serialVersionUID = 1L;

							public Object getObject() {
								return (data.getEffectiveFrom());
							}

							public void setObject(Object arg0) {
								data.setEffectiveFrom((Date) arg0);
							}

							public void detach() {
							}
						};
						List<Date> dates = new ArrayList<Date>();
						dates.add(getLibertyMonthEndDates().getMonthStart());
						dates.add(getLibertyMonthEndDates().getMonthEnd());
						DropDownChoice<Object> startDates = new DropDownChoice<Object>("value",model, dates)
								{

									private static final long serialVersionUID = 1L;
									@Override
									protected void onComponentTag(final ComponentTag tag) {
										super.onComponentTag(tag);
																			}
							
								};
									startDates.setOutputMarkupId(true);	
									startDates.add(new AjaxFormComponentUpdatingBehavior("change"){

										private static final long serialVersionUID = 1L;

										@Override
										protected void onUpdate(
												AjaxRequestTarget arg0) {
											// TODO Auto-generated method stub
											
										}
										
									});
						return HelperPanel
								.getInstance(componentId, startDates);
					}

				}.setInitialSize(110));

				cols.add(new SRSDataGridColumn<PartyRoleDTO>("effectiveTo",
						new Model<String>("End Date"), "effectiveTo", "effectiveTo", getEditState()) {

							private static final long serialVersionUID = 1L;

					@Override
					public Panel newCellPanel(WebMarkupContainer parent,
							String componentId, IModel rowModel,
							String objectProperty, EditStateType state,
							PartyRoleDTO data) {
						if ((dto.getPartyContextDTO().getTypeOid()!=SRSType.PERSON)||
								getEditState().isViewOnly()){
							return super.newCellPanel(parent, componentId,
									rowModel, objectProperty, state, data);
						}
						if ((dto.getPartyContextDTO().getTypeOid()!=SRSType.PERSON)||
								getEditState().isViewOnly()){
							return super.newCellPanel(parent, componentId,
									rowModel, objectProperty, state, data);
						}
						return super.newCellPanel(parent, componentId,
								rowModel, objectProperty, state, data);
					}

				}.setInitialSize(110));
		return cols;
	}

	private MonthEndDates getLibertyMonthEndDates() {
		MonthEndDates libertyMonthEndDates = null;
		try {
			 libertyMonthEndDates = getBusinessUtil().getLibertyMonthEndDates(new Date());
		} catch (DataNotFoundException e) {
			PartyHierarchyPanel.this
			.error("The liberty month end dates could not be found on the database, Please contact support");
		} catch (LogicExecutionException e) {
			PartyHierarchyPanel.this
			.error("The liberty month end dates could not be found on the database, Please contact support");
		}
		return libertyMonthEndDates;
	}
	
	private IBusinessUtilitiesBean getBusinessUtil() throws LogicExecutionException {
		IBusinessUtilitiesBean ratingUtil = null;
		try {
			ratingUtil = ServiceLocator.lookupService(IBusinessUtilitiesBean.class);					
		} catch (NamingException e) {
			throw new LogicExecutionException("Could not get the BusinessUtilitiesBean MSG: " + e.getMessage(),0,0,e);
		}
		
		return ratingUtil;
	}
	
	public Class<PartyHierarchyPanel> getPanelClass() {		
		return PartyHierarchyPanel.class;
	}	
	
	
}