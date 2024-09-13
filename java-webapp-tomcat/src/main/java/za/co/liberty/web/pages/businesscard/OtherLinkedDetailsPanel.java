package za.co.liberty.web.pages.businesscard;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import javax.naming.NamingException;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow.MaskType;
import org.apache.wicket.markup.html.WebMarkupContainer;
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

import za.co.liberty.business.guicontrollers.IAgreementPrivilegesController;
import za.co.liberty.business.guicontrollers.businesscard.IBusinessCardGuiController;
import za.co.liberty.dto.agreementprivileges.AgreementPrivilegesDataDTO;
import za.co.liberty.dto.agreementprivileges.ExplicitAgreementType;
import za.co.liberty.dto.contracting.ResultAgreementDTO;
import za.co.liberty.dto.contracting.ResultPartyDTO;
import za.co.liberty.dto.gui.context.ResultContextItemDTO;
import za.co.liberty.dto.rating.DescriptionDTO;
import za.co.liberty.dto.userprofiles.ContextDTO;
import za.co.liberty.dto.userprofiles.ExplicitAgreementDTO;
import za.co.liberty.dto.userprofiles.ISessionUserProfile;
import za.co.liberty.exceptions.data.DataNotFoundException;
import za.co.liberty.exceptions.data.InconsistentDataException;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.helpers.lookup.ServiceLocator;
import za.co.liberty.interfaces.party.PartyRoleType;
import za.co.liberty.web.data.enums.ContextType;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.pages.businesscard.model.BusinessCardPageModel;
import za.co.liberty.web.pages.businesscard.model.MaintainBusinessCardPanelModel;
import za.co.liberty.web.pages.interfaces.ISecurityPanel;
import za.co.liberty.web.pages.panels.BasePanel;
import za.co.liberty.web.pages.panels.HelperPanel;
import za.co.liberty.web.pages.party.PartyRolesHistoryPage;
import za.co.liberty.web.pages.search.ContextSearchPopUp;
import za.co.liberty.web.system.EJBReferences;
import za.co.liberty.web.system.SRSAuthWebSession;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGrid;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGrid.GridSizeUnit;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGridColumn;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataProviderAdapter;
import za.co.liberty.web.wicket.markup.html.grid.SRSGridRowSelectionCheckBox;
import za.co.liberty.web.wicket.markup.repeater.data.ListDataProvider;

import com.inmethod.grid.IGridColumn;
import com.inmethod.grid.SizeUnit;

public class OtherLinkedDetailsPanel extends BasePanel implements
		ISecurityPanel {
	private static final long serialVersionUID = 1L;
	private BusinessCardPageModel pageModel;
	private MaintainBusinessCardPanelModel panelModel;
	private EditStateType editState;
	private FeedbackPanel feedBackPanel;
	private boolean initialised = false;
	private ModalWindow searchWindow;
	private ModalWindow historyWindow;
	private AgreementPrivilegesDataDTO currentworkingGridRole;
	private SRSDataGrid partyToPartyRolesGrid;
	private boolean maintainPartnershipsRequestExists;
	private boolean maintainLinkedAssistantsRequestExists;
	private List<DescriptionDTO> explicitPartyRelationsType;
	private Button addButton;
	private Long agreementNumber;
	private ExplicitAgreementDTO dataModel = new ExplicitAgreementDTO();
	private List<ResultAgreementDTO> allAgreementsList = new ArrayList<ResultAgreementDTO>();
	private List<String> agreementNumbers = new ArrayList<String>();
	private IBusinessCardGuiController businessCardGuiController;
	private Button searchButton = new Button("value", new Model<String>("Search"));	
	
	public OtherLinkedDetailsPanel(String id, BusinessCardPageModel businssCardpageModel,
			EditStateType editState, FeedbackPanel feedBackPanel,
			Page parentPage, List<DescriptionDTO> explicitPartyRelationsType, Long agreementNumber) {
		super(id, editState, parentPage);
		this.pageModel = businssCardpageModel;
		this.editState = editState;
		this.feedBackPanel = feedBackPanel;
		this.explicitPartyRelationsType=explicitPartyRelationsType;
		this.agreementNumber = agreementNumber;
	}


	@Override
	protected void onBeforeRender() {
		if (!initialised) {
			initialised = true;
			initPanelModelModel();
			add(new HierarchyForm("hierarchyForm"));
			add(searchWindow = createSearchWindow("searchPartyWindow"));
			add(historyWindow = createHistoryWindow("historyWindow"));
		}
		if (feedBackPanel == null) {
			feedBackPanel = this.getFeedBackPanel();
		}
		super.onBeforeRender();
	}

	private void initPanelModelModel() {
		searchButton.setEnabled(false);
		ContextDTO dto = SRSAuthWebSession.get().getContextDTO();
		allAgreementsList.addAll(dto.getAllAgreementsList());
		agreementNumbers.add("All");
		for (ResultAgreementDTO agreementDTO:dto.getAllAgreementsList())
		{
			agreementNumbers.add(String.valueOf(agreementDTO.getOid()));
		}
		if (pageModel.getMaintainBusinessCardPanelModel() != null) {
			panelModel = pageModel.getMaintainBusinessCardPanelModel();
			panelModel.setAllAgreementsList(allAgreementsList);
			panelModel.setOwnAgreementList(getOwnAgreements(dto.getPartyContextDTO().getPartyOid()));
//			for (ResultAgreementDTO agreementDTO:panelModel.getAllAgreementsList())
//			{
//				accessGrantedOwnAgreementList.addAll(getAgreementsTheUserAllowedAccess(panelModel.getOwnAgreementList(), agreementDTO.getOid()));
//			}
			panelModel.setAcessGrantedOwnAgreementList(panelModel.getBusinessCardDetails().getAgreementPriviledges());
			return;
		}
		panelModel = new MaintainBusinessCardPanelModel();
		// get the party to party roles from the DB
		ArrayList<AgreementPrivilegesDataDTO> explicitAgreementDTOs = new ArrayList<AgreementPrivilegesDataDTO>();
		AgreementPrivilegesDataDTO agreementDTO = new AgreementPrivilegesDataDTO();
		panelModel.setAgreementPrivilegesDataDTOs(explicitAgreementDTOs);
		panelModel.setAgreementPrivilegesDataDTO(agreementDTO);
		pageModel.setMaintainBusinessCardPanelModel(panelModel);
	}


	/**
	 * 
	 */
	private ModalWindow createHistoryWindow(String id) {
		final ModalWindow window = new ModalWindow(id);
		window.setTitle("History");
		// Create the page
		window.setPageCreator(new ModalWindow.PageCreator() {
			private static final long serialVersionUID = 1L;

			public Page createPage() {
				return new PartyRolesHistoryPage(window, pageModel
						.getPartyOID(), new ArrayList<PartyRoleType>());
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
//		window.setPageMapName("RoleHistoryPageMap2");
		return window;
	}

	private ModalWindow createSearchWindow(String id) {

		ContextSearchPopUp popUp = new ContextSearchPopUp() {
			@Override
			public ContextType getContextType() {
				return ContextType.PARTY;
			}
			@Override
			public void doProcessSelectedItems(AjaxRequestTarget target,
					ArrayList<ResultContextItemDTO> selectedItemList) {
				// fill in the party into the role
				ContextDTO dto = SRSAuthWebSession.get().getContextDTO();
				boolean alreadyLinked = false;
				ISessionUserProfile userProfile = SRSAuthWebSession.get().getSessionUser();
				if (selectedItemList.size() == 1) {
					OtherLinkedDetailsPanel.this.doProcessSearchResult(target, selectedItemList,dto,userProfile,alreadyLinked);
		
				}
				
			}
		};
		ModalWindow win = popUp.createModalWindow(id);
//		win.setPageMapName("partyRoleSearchPageMap");
		return win;
	}


	public class HierarchyForm extends Form {
		private static final long serialVersionUID = 1L;

		public HierarchyForm(String id) {
			super(id);
			add(partyToPartyRolesGrid = createPartyToPartyRolesGrid(
					"partyRolesGrid", panelModel));
			add(createRemovePartyRoleButton("removePartyRoleButton"));
			add(createAddPartyRoleButton("addPartyRoleButton"));
			add(createPartyRoleHistoryButton("historyButton"));
			add(new IFormValidator() {
				private static final long serialVersionUID = 1L;

				public void validate(Form arg0) {
					if (getEditState().isViewOnly()) {
						return;
					}
				}

				public FormComponent[] getDependentFormComponents() {
					return null;
				}
			});
		}
	}

	private List<IGridColumn> getPartyToPartyRoleColumns() {
		Vector<IGridColumn> cols = new Vector<IGridColumn>();
		if (!editState.isViewOnly()) {
			SRSGridRowSelectionCheckBox col = new SRSGridRowSelectionCheckBox(
					"checkBox");
			cols.add(col.setInitialSize(30));
		}
		cols.add(new SRSDataGridColumn<AgreementPrivilegesDataDTO>("agmntNo",
				new Model<String>("Agmnt No"), "agmntNo", "agmntNo",
				getEditState()) {
			private static final long serialVersionUID = 1L;

			@Override
			public Panel newCellPanel(WebMarkupContainer parent,
					String componentId, IModel rowModel,
					String objectProperty, EditStateType state,
					final AgreementPrivilegesDataDTO data) {	
		
				DropDownChoice<?> dropdown = new DropDownChoice<Object>("value", new IModel<Object>() {

					private static final long serialVersionUID = 1L;

					public void detach() {
						// TODO Auto-generated method stub
						
					}

					public Object getObject() {
						if (data.getAgmntNo()!=null && data.getAgmntNo().equalsIgnoreCase("all"))
						{
							return data.getAgmntNo();
						}
						long agreementOID = data.getAgreementOID();
						return Long.toString(agreementOID);
					}

					public void setObject(Object arg0) {
						data.setAgmntNo((String) arg0);
						String agmntNo = (String) arg0;
						if (agmntNo !=null &&agmntNo.equalsIgnoreCase("all"))
						{
							data.setAllAgreementsList(allAgreementsList);
						}else{
							data.setAgreementOID(Long.parseLong((String)arg0));
							data.setAllAgreementsList(new ArrayList<ResultAgreementDTO>());
						}
					}
				
				},agreementNumbers);
				//update on modify
				dropdown.add(new AjaxFormComponentUpdatingBehavior("change"){
					@Override
					protected void onUpdate(AjaxRequestTarget  arg0) {
						if (data.getAgmntNo()!=null)
						{
						searchButton.setEnabled(true);
						arg0.add(searchButton);
						}else{
							searchButton.setEnabled(false);
							arg0.add(searchButton);
						}
						arg0.add(partyToPartyRolesGrid);
					}					
				});
				dropdown.setLabel(new Model<String>("Agmt No"));
				dropdown.setRequired(false);
				
				//create dropdown of selectable types				
				HelperPanel dropdownPanel = HelperPanel.getInstance(componentId, dropdown);				
				return dropdownPanel;
			}

		}.setInitialSize(160));
		// add in the role type selection
		//dont display the role type column if there is only one role being used		
		if(explicitPartyRelationsType!=null && explicitPartyRelationsType.size() != 1){		
			//add in the role type selection
			cols.add(new SRSDataGridColumn<AgreementPrivilegesDataDTO>("partyType",
					new Model<String>("Relationship"), "partyType", "partyType",
					getEditState()) {
				private static final long serialVersionUID = 1L;
	
				@Override
				public Panel newCellPanel(WebMarkupContainer parent,
						String componentId, IModel rowModel,
						String objectProperty, EditStateType state,
						final AgreementPrivilegesDataDTO data) {	
			
					final DropDownChoice dropdown = new DropDownChoice("value",new PropertyModel(data,objectProperty){
						/**
						 * 
						 */
						private static final long serialVersionUID = 1L;
						@Override
							public Object getObject() {
								//return one of the values in the static list		
							if (data.getDescriptionDTO()!=null&&data.getDescriptionDTO().getUniqId()!=0)
							{
								super.setObject(data.getDescriptionDTO().getUniqId());
							}
							Object object = super.getObject();
							if (object==null){
								return null;
							}
								Long id = Long.valueOf((String)object);
								if(id == null){
									return null;							
								}
								for(DescriptionDTO type : explicitPartyRelationsType){
									if(type.getUniqId() == id ){
										data.setDescriptionDTO(type);
										data.setRelationshipId(type.getUniqId());
										return type;
									}
								}
								return null;
							}
							@Override
							public void setObject(Object arg0) {		
	//							we have to set two values here
								super.setObject(((DescriptionDTO)arg0).getUniqId());
								data.setDescriptionDTO((DescriptionDTO)arg0);
							}
					},explicitPartyRelationsType,new ChoiceRenderer("description"));
					//update on modify
					dropdown.add(new AjaxFormComponentUpdatingBehavior("change"){
						@Override
						protected void onUpdate(AjaxRequestTarget  arg0) {
							arg0.add(partyToPartyRolesGrid);
						}					
					});
					dropdown.setLabel(new Model<String>("Relationship Type"));
					dropdown.setRequired(false);
					
					//create dropdown of selectable types				
					HelperPanel dropdownPanel = HelperPanel.getInstance(componentId, dropdown);				
					return dropdownPanel;
				}
	
			}.setInitialSize(160));
		}
		
//		add in the uacfid column
		cols.add(new SRSDataGridColumn<AgreementPrivilegesDataDTO>("ucafID",
				new Model<String>("UACFID"), "ucafID", "ucafID", editState).setInitialSize(80));
//		add in the name column
		cols.add(new SRSDataGridColumn<AgreementPrivilegesDataDTO>("name",
				new Model<String>("Party Name"), "name", "name", editState).setInitialSize(140).setWrapText(true));

		
//		add search button, don't display this column on view
		if(getEditState() == null ||  !getEditState().isViewOnly()){
			cols.add(new SRSDataGridColumn<AgreementPrivilegesDataDTO>("searchParty",
					new Model<String>("Search"), "searchParty", getEditState()){	
				
						private static final long serialVersionUID = 1L;							

						@Override
						public Panel newCellPanel(WebMarkupContainer parent, String componentId, IModel rowModel, String objectProperty, EditStateType state, final AgreementPrivilegesDataDTO data) {
							if(data.getPartyOid() == 0){
								searchButton.setOutputMarkupPlaceholderTag(true);
								searchButton.setOutputMarkupId(true);
								searchButton.add(new AjaxFormComponentUpdatingBehavior("click"){									
									private static final long serialVersionUID = 1L;
									@Override
									protected void onUpdate(AjaxRequestTarget target) {	
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
		cols.add(new SRSDataGridColumn<AgreementPrivilegesDataDTO>("effectiveFrom",
				new Model<String>("Start Date"), "effectiveFrom", "effectiveFrom",
				getEditState()) {
			private static final long serialVersionUID = 1L;

			@Override
			public Panel newCellPanel(WebMarkupContainer parent,
					String componentId, IModel rowModel,
					String objectProperty, EditStateType state,
					AgreementPrivilegesDataDTO data) {	
				if (maintainLinkedAssistantsRequestExists || 
						editState.isViewOnly() 
						//|| (data.getOid() != 0 && DateUtil.getInstance().compareDatePart(data.getEffectiveFrom(),new Date()) <= 0)
						|| data.getPartyOid() != 0){
					return super.newCellPanel(parent, componentId,
							rowModel, objectProperty, state, data);
				}
				if (maintainPartnershipsRequestExists || 
						editState.isViewOnly() 
						//|| (data.getOid() != 0 && DateUtil.getInstance().compareDatePart(data.getEffectiveFrom(),new Date()) <= 0)
						|| data.getPartyOid() != 0){
					return super.newCellPanel(parent, componentId,
							rowModel, objectProperty, state, data);
				}
				TextField startDate = new TextField("value",
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
				return HelperPanel
						.getInstance(componentId, startDate, true);
			}

		}.setInitialSize(110));

		cols.add(new SRSDataGridColumn<AgreementPrivilegesDataDTO>("effectiveTo",
				new Model<String>("End Date"), "effectiveTo", "effectiveTo", getEditState()) {

			@Override
			public Panel newCellPanel(WebMarkupContainer parent,
					String componentId, IModel rowModel,
					String objectProperty, EditStateType state,
					AgreementPrivilegesDataDTO data) {
				if (maintainLinkedAssistantsRequestExists ||
						editState.isViewOnly()){
					return super.newCellPanel(parent, componentId,
							rowModel, objectProperty, state, data);
				}
				if (maintainPartnershipsRequestExists ||
						editState.isViewOnly()){
					return super.newCellPanel(parent, componentId,
							rowModel, objectProperty, state, data);
				}
				TextField endDate = new TextField("value",
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
				return HelperPanel.getInstance(componentId, endDate, true);
			}

		}.setInitialSize(110));
		
		
		return cols;

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
				List<Object> selections = partyToPartyRolesGrid
						.getSelectedItemObjects();
				for (Object selection : selections) {
					AgreementPrivilegesDataDTO gridRole = (AgreementPrivilegesDataDTO) selection;
					panelModel.getBusinessCardDetails().getAgreementPriviledges().remove(gridRole);
					// add the role to the removals list and end date it
					if (gridRole.getAgreementOID() > 0) {
						// end it when it was created so it never existed and
						// add it to removals list
						gridRole.setEffectiveTo(gridRole.getEffectiveFrom());
						panelModel.getDeletePrivilegesDataDTOs().add(gridRole);
					}
				}
				target.add(partyToPartyRolesGrid);
			}
		});
		if (getEditState() == EditStateType.AUTHORISE) {
			button.setVisible(false);
		}
		button.setOutputMarkupId(true);
		button.setOutputMarkupPlaceholderTag(true);
		setUpbutton(button, true);// !(existingServicingRequest &&
									// existingHierarchyRequest));
		return button;
	}

	/**
	 * Set up the button to be enabled or disabled
	 * 
	 * @param button
	 */
	private void setUpbutton(Button button, boolean enabled) {
		if (!getEditState().isViewOnly() && enabled) {
			button.setEnabled(true);
		} else {
			button.setEnabled(false);
		}
	}

	/**
	 * Create the button to add a party role
	 * 
	 * @return
	 */
	private Button createAddPartyRoleButton(String id) {
		addButton = new Button(id);
		addButton.add(new AjaxFormComponentUpdatingBehavior("click") {
			private static final long serialVersionUID = 0L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				AgreementPrivilegesDataDTO dto = new AgreementPrivilegesDataDTO();
				dto.setEffectiveFrom(new Date());
//				dto.setAgreementOID(agreementNumber);
				dto.setExplicitPartyRelationsType(explicitPartyRelationsType);
				if (pageModel.getPartyOID() != null) {
					dto.setMainRolePlayerID(pageModel.getPartyOID());
				}
				panelModel.getBusinessCardDetails().getAgreementPriviledges().add(dto);
				target.add(partyToPartyRolesGrid);
				setUpbutton(addButton, false);
			}
		});
		if (getEditState() == EditStateType.AUTHORISE) {
			addButton.setVisible(false);
		}
		addButton.setOutputMarkupId(true);
		addButton.setOutputMarkupPlaceholderTag(true);
		setUpbutton(addButton, true);// !(existingServicingRequest &&
									// existingHierarchyRequest));
		return addButton;
	}
	private SRSDataGrid createPartyToPartyRolesGrid(String id,MaintainBusinessCardPanelModel businessCardPanelModel){
		List<AgreementPrivilegesDataDTO> explictRoles = businessCardPanelModel.getBusinessCardDetails().getAgreementPriviledges();
		SRSDataGrid grid = new SRSDataGrid(id, new SRSDataProviderAdapter(
				new ListDataProvider<AgreementPrivilegesDataDTO>(explictRoles)),
				getPartyToPartyRoleColumns(), getEditState(),new ArrayList<DescriptionDTO>());		
		grid.setCleanSelectionOnPageChange(false);
		grid.setClickRowToSelect(false);
		grid.setAllowSelectMultiple(true);
		//grid.setGridWidth(650, GridSizeUnit.PIXELS);
		grid.setGridWidth(110, GridSizeUnit.PERCENTAGE);
		grid.setRowsPerPage(5);
		grid.setContentHeight(140, SizeUnit.PX);
		return grid;
	}
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
	 * Return a list of agreement(People) that have been linked to the given
	 * agreement number (agreementNumberAccessGiven)
	 * 
	 * @param partyOid
	 * @param agreementNumberAccessGiven
	 * @return
	 */
	private List<AgreementPrivilegesDataDTO> getAgreementsTheUserAllowedAccess(
			List<AgreementPrivilegesDataDTO> ownAgreements,
			long agreementNumberAccessGiven) {
		
		AgreementPrivilegesDataDTO ownAgreement = null;
		for (AgreementPrivilegesDataDTO d:ownAgreements) {
			if (agreementNumberAccessGiven == d.getAgreementOID()) {
				ownAgreement = d;
				break;
			}
		}
		return getAgreementsTheUserAllowedAccess(ownAgreement);

	}
	/**
	 * Get all the agreements linked to the parties(partyOid) agreements
	 * 
	 * @param partyOid
	 * @return
	 */
	private List<AgreementPrivilegesDataDTO> getAgreementsTheUserAllowedAccess(
			AgreementPrivilegesDataDTO ownAgreements) {
		List<AgreementPrivilegesDataDTO> results = null;

		try {
			IAgreementPrivilegesController agreementPrivilegesController = getAgreementPrivilegesController();
			results = agreementPrivilegesController.getAgreementsThatTheUserAllowedAccess(ownAgreements);
		} catch (DataNotFoundException e) {
			results = new ArrayList<AgreementPrivilegesDataDTO>();
		}

		return results;
	}

	private List<AgreementPrivilegesDataDTO> getOwnAgreements(long partyOid) {
		List<AgreementPrivilegesDataDTO> results = new ArrayList<AgreementPrivilegesDataDTO>();

		try {
			IAgreementPrivilegesController agreementPrivilegesController = getAgreementPrivilegesController();
			results = agreementPrivilegesController
					.getUserOwnAgreements(partyOid);
			// add in the type of agreement
			for (AgreementPrivilegesDataDTO dto : results) {
				dto.setExplicitAgreementType(ExplicitAgreementType.OWN_AGREEMENT);
			}
		} catch (DataNotFoundException e) {
			// do nothing, empty list returned to user
		}catch (InconsistentDataException e) {
			error("Could not retreive you own agreements as inconsistent data was found");
		}
		return results;
	}

	protected void doProcessSearchResult(AjaxRequestTarget target,
			ArrayList<ResultContextItemDTO> selectedItemList, ContextDTO dto, ISessionUserProfile userProfile, boolean alreadyLinked) {
		ResultContextItemDTO item = selectedItemList.get(0);
		if (dto.getPartyContextDTO().getPartyOid() == item.getPartyDTO().getPartyOid())
		{
			error("User not allowed to grant explict access to themself");
			target.add(getFeedBackPanel());
			target.add(this);
			return;
		}
		Boolean hasAgreement = hasAgreement(item.getPartyDTO());
		if (!hasAgreement)
		{
			error("There must be at least one adviser agreement for the context party set as the context of the relationship being created");
			target.add(getFeedBackPanel());
			target.add(this);
			return;
		}
		List<AgreementPrivilegesDataDTO> agreementPriviledges = panelModel.getBusinessCardDetails().getAgreementPriviledges();
		for (AgreementPrivilegesDataDTO agreementPrivilegesDataDTO:agreementPriviledges)
		{
		if ((agreementPrivilegesDataDTO.getPartyOid()==0)||
					(agreementPrivilegesDataDTO.equals(currentworkingGridRole)))
			{
				if (currentworkingGridRole.getAgmntNo().equalsIgnoreCase("all")){
					for (ResultAgreementDTO agreementNo:currentworkingGridRole.getAllAgreementsList())
					{
						 alreadyLinked = isAlreadyLinked(item.getPartyDTO().getPartyOid(), agreementNo.getOid());
						if (alreadyLinked)
						{
							error("Agreement number: "+agreementNo.getOid()+" has already been linked to :"+item.getPartyDTO().getName());
							target.add(getFeedBackPanel());
							target.add(this);
							return;
						}
					}
				
				}else{
					alreadyLinked = isAlreadyLinked(item.getPartyDTO().getPartyOid(), Long.valueOf(currentworkingGridRole.getAgmntNo()));
					if (alreadyLinked)
					{
						error("Agreement number: "+currentworkingGridRole.getAgmntNo()+" has already been linked to :"+item.getPartyDTO().getName());
						target.add(getFeedBackPanel());
						target.add(this);
						return;
					}
				}
				agreementPrivilegesDataDTO.setName(item.getPartyDTO().getName());
				agreementPrivilegesDataDTO.setAgreementOID(dto.getAgreementContextDTO().getAgreementNumber());
				agreementPrivilegesDataDTO.setAgmntNo(currentworkingGridRole.getAgmntNo());
				agreementPrivilegesDataDTO.setAllAgreementsList(currentworkingGridRole.getAllAgreementsList());
				agreementPrivilegesDataDTO.setRelationshipId(currentworkingGridRole.getDescriptionDTO().getUniqId());
				agreementPrivilegesDataDTO.setUcafID(item.getPartyDTO().getUacfID());
				agreementPrivilegesDataDTO.setPartyOid(item.getPartyDTO().getPartyOid());
				agreementPrivilegesDataDTO.setCreationTime(new Date());
				agreementPrivilegesDataDTO.setEffectiveFrom(item.getPartyDTO().getEffectiveFrom());
				agreementPrivilegesDataDTO.setEffectiveTo(item.getPartyDTO().getEffectiveTo());
				agreementPrivilegesDataDTO.setExplicitAgreementType(ExplicitAgreementType.EXPLICT_AGREEMENT);
				agreementPrivilegesDataDTO.setCreatedByPartyID(userProfile.getPartyOid());
				break;
			}
			
		}
		currentworkingGridRole.setViewerName(item.getPartyDTO().getName());
		currentworkingGridRole.setDescriptionDTO(dataModel.getDescriptionDTO());
		currentworkingGridRole.setPartyOid(item.getPartyDTO().getPartyOid());
		currentworkingGridRole.setOid(item.getPartyDTO().getOid());
		currentworkingGridRole.setUcafID(item.getPartyDTO().getUacfID());
		currentworkingGridRole.setType(item.getPartyDTO().getTypeOid());
		currentworkingGridRole.setRolePlayerReference(item.getPartyDTO());
		setUpbutton(addButton, true);
		target.add(partyToPartyRolesGrid);
		
	}
	private Boolean hasAgreement(ResultPartyDTO partyDTO) {
		ResultAgreementDTO agmt = new ResultAgreementDTO();
		try {
			agmt = getBusinessCardGuiController().findBestAgreementForParty(partyDTO.getOid());
		} catch (CommunicationException e) {
		} catch (DataNotFoundException e) {
			//do nothing
		}
		long agmtNumber = agmt.getAgreementNumber();
		if (agmtNumber==0)
		{
			return false;
		}
		return true;
		
	}
	private boolean isAlreadyLinked(long partyOid, long agreementNo) {
		boolean result = false;			
		if(panelModel.getAcessGrantedOwnAgreementList() != null){
		for (AgreementPrivilegesDataDTO agreementPrivilegesDataDTO : panelModel.getAcessGrantedOwnAgreementList()) {
			if ((agreementPrivilegesDataDTO.getPartyOid() == partyOid)
					&& (agreementPrivilegesDataDTO.getAgreementOID() == agreementNo)) {
				result = true;
			}
		}
		}
		return result;
	}
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
	private IAgreementPrivilegesController getAgreementPrivilegesController() {
		try {
			return ServiceLocator.lookupService(
					IAgreementPrivilegesController.class);
		} catch (NamingException e) {
			throw new CommunicationException(e);
		}

	}
	public Class<OtherLinkedDetailsPanel> getPanelClass() {
		return OtherLinkedDetailsPanel.class;
	}

}
