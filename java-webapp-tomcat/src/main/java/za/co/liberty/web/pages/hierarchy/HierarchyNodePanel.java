package za.co.liberty.web.pages.hierarchy;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AbstractAutoCompleteRenderer;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
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
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.Response;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.util.string.Strings;

import com.inmethod.grid.DataProviderAdapter;
import com.inmethod.grid.IGridColumn;
import com.inmethod.grid.SizeUnit;
import com.inmethod.grid.column.AbstractColumn;

import za.co.liberty.business.guicontrollers.hierarchy.IHierarchyGUIController;
import za.co.liberty.business.party.IPartyManagement;
import za.co.liberty.dto.common.IDValueDTO;
import za.co.liberty.dto.contracting.ResultPartyDTO;
import za.co.liberty.dto.databaseenum.CostCenterDBEnumDTO;
import za.co.liberty.dto.databaseenum.DatabaseEnumDTO;
import za.co.liberty.dto.gui.context.ResultContextItemDTO;
import za.co.liberty.dto.party.HierarchyEmployeeLinkDTO;
import za.co.liberty.dto.party.HierarchyLBFNDPLinkDTO;
import za.co.liberty.dto.party.HierarchyNodeDTO;
import za.co.liberty.dto.party.HierarchyNodeLinkDTO;
import za.co.liberty.dto.userprofiles.ContextDTO;
import za.co.liberty.dto.userprofiles.ContextPartyDTO;
import za.co.liberty.exceptions.data.DataNotFoundException;
import za.co.liberty.exceptions.data.ValidationException;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.helpers.lookup.ServiceLocator;
import za.co.liberty.helpers.persistence.TemporalityHelper;
import za.co.liberty.interfaces.agreements.requests.RequestKindType;
import za.co.liberty.interfaces.party.IPartyNameAndIdFLO;
import za.co.liberty.persistence.party.entity.fastlane.PartyProfileFLO;
import za.co.liberty.srs.type.SRSType;
import za.co.liberty.web.data.enums.ComponentType;
import za.co.liberty.web.data.enums.ContextType;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.pages.hierarchy.model.MaintainHierarchyPageModel;
import za.co.liberty.web.pages.interfaces.ISecurityPanel;
import za.co.liberty.web.pages.panels.BasePanel;
import za.co.liberty.web.pages.panels.HelperPanel;
import za.co.liberty.web.pages.search.ContextSearchPopUp;
import za.co.liberty.web.system.SRSAuthWebSession;
import za.co.liberty.web.wicket.markup.html.form.SRSDateField;
import za.co.liberty.web.wicket.markup.html.form.SRSDropDownChoice;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGrid;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGrid.GridSizeUnit;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGridColumn;
import za.co.liberty.web.wicket.markup.html.grid.SRSGridRowSelectionCheckBox;
import za.co.liberty.web.wicket.markup.repeater.data.ListDataProvider;

/**
 * Hierarchy node details panel, capturing all hierarchy node details
 * 
 * @author DZS2610
 * 
 */
public class HierarchyNodePanel extends BasePanel implements ISecurityPanel{	
	
	private static final Logger logger = Logger.getLogger(HierarchyNodePanel.class);

	private transient IPartyManagement partyManagement;
	
	private MaintainHierarchyPageModel pageModel;	
	
	private FeedbackPanel feedBackPanel;
	
	private ModalWindow searchWindow;
	
	private SRSDataGrid employeeGrid;

	private SRSDataGrid nodeGrid;		
	
	private SRSDataGrid lbfndpGrid;

	private HelperPanel typePanel;
	
	private HelperPanel externalType;

	private HelperPanel channelPanel;
	
	private HelperPanel costCenterPanel;
	
	private HelperPanel branchCodePanel;
	
	private Component costCenterNameComponent;
	
	private Component externalTypeLabelComponent;
	
	private WebMarkupContainer channelName;
	
	private Label branchCodeNameComponent;
	
	private List<HierarchyNodeLinkDTO> oldParents;

	private ArrayList<IDValueDTO> parentSelectionList = new ArrayList<IDValueDTO>(2);
	
	private HierarchyEmployeeLinkDTO currentSearchEmployee;
	
	private Button addParentButton;
	
	private Button removeParentButton;
	
	private long currentType = 0;
	
	private long currentChannel;
	
	private Form nodeForm;
	
	private ModalWindow historyWindow;
	
	private RoleHistoryPage.HistoryType currentHistoryType = RoleHistoryPage.HistoryType.PARENT;
	
	private HierarchyNodeLinkDTO currentParent = null;
	
	private ModalWindow rolesPopup;	
	
//	TODO add all form components to this list to validate before controller validates
	private List<FormComponent> validationComponents = new ArrayList<FormComponent>();
	
	private final SimpleDateFormat dteFormatter = new SimpleDateFormat("dd/MM/yyyy");	

	private static final long serialVersionUID = 1L;
	
	/**
	 * booleans set to true if there are existing requests that still need to be authorised, screen must then be disabled
	 */
	private boolean existingMaintenanceRequest;
	
	private boolean existingTerminationRequest;
	
	private boolean existingReactivationRequest;
	
	private Popup_Type popupType;
	
	private enum Popup_Type{ROLES,CHILDREN};
	
	private HashMap<HierarchyNodeLinkDTO,ParentDropdownObject> parentSelections = new HashMap<HierarchyNodeLinkDTO, ParentDropdownObject>();
	
	private HashMap<HierarchyEmployeeLinkDTO, AgreementGridData> gridData = new HashMap<HierarchyEmployeeLinkDTO, AgreementGridData>();
	
	
	private transient IHierarchyGUIController hierarchyGUIController;

	private List<IDValueDTO> typeListForCategories;
	
	private HelperPanel typeDropDown;

	/**
	 * @param arg0
	 */
	public HierarchyNodePanel(String id, final MaintainHierarchyPageModel pageModel,
			EditStateType editState, FeedbackPanel feedBackPanel, Page parentPage) {
		super(id,editState,parentPage);		
		this.pageModel = pageModel;		
		this.feedBackPanel = feedBackPanel;
		List<RequestKindType> unAuthRequests = getOutStandingRequestKinds();
		//check outstanding requests and disable fields		
		for (RequestKindType kind : unAuthRequests) {
			if(kind == RequestKindType.MaintainHierarchyNodeDetails){
				existingMaintenanceRequest = true;
			}else if(kind == RequestKindType.TerminateHierarchyNode){
				existingTerminationRequest = true;
			}else if(kind == RequestKindType.ReactivateHierarchyNode){
				existingReactivationRequest = true;
			}
		}		
		
//		set the cost center in the page model
		if(pageModel != null && pageModel.getHierarchyNodeDTO() != null && pageModel.getHierarchyNodeDTO().getCostCenter() != null){
			pageModel.setCostCenterSelection(pageModel.getHierarchyNodeDTO().getCostCenter().getName());
		}else{
			pageModel.setCostCenterSelection(null);
		}		
		
		try {
			typeListForCategories = getHierarchyGUIController().getTypeListForCategories(SRSType.FAISLICENSESUBCATERORIES);
			pageModel.setOrganisationTypeList(getHierarchyGUIController().getOrganisationTypes());
		} catch (DataNotFoundException e) {
			this.error(e);
		}
		
		add(nodeForm = createNodeForm("nodeForm"));
		add(searchWindow = createSearchWindow("searchPartyWindow"));	
		add(historyWindow = createHistoryWindow("historyWindow"));
		add(rolesPopup = createRolesWindow("roleModelWindow"));
		currentParent = getCurrentParentNode();
	}
	
	@Override
	public EditStateType getEditState() {
		//will disable any modification if there are any requests pending auth
		if(existingMaintenanceRequest || existingTerminationRequest || existingReactivationRequest){
			return EditStateType.VIEW;
		}
		return super.getEditState();
	}

	@Override
	protected boolean isView(EditStateType[] editableStates) {	
		//will disable any modification if there are any requests pending auth
		if(existingMaintenanceRequest || existingTerminationRequest || existingReactivationRequest){
			return true;
		}
		return super.isView(editableStates);
	}	
	
	/**
	 * Form used for the panel so we can add validations and on submit method calls
	 * @author DZS2610
	 *
	 */
	public class NodeForm extends Form {
		private static final long serialVersionUID = 1L;
		
		public NodeForm(String id) {
			super(id);
			this.add(createNameField("businessName"));
			this.add(typePanel = createTypeField());	
			this.add(channelName = createChannelNameField());	
			this.add(channelPanel = createChannelField());
			this.add(branchCodePanel = createBranchCodeField());	
			this.add(branchCodeNameComponent = createBranchNameField());
			this.add(costCenterNameComponent = createCostCenterNameField());
			this.add(costCenterPanel = createCostCenterField("costCenter"));	
			this.add(externalTypeLabelComponent = createExternalTypeLabelField());
			this.add(externalType = createExternalType());	
			this.add(createStartDateField());
			this.add(createEndDateField());	
			this.add(createStatusField());
			this.add(createStatusChangeDateField());
			this.add(createTerminationDateField());
			WebMarkupContainer agreements = createRolesView("rolesPopupView");
			agreements.add(createViewRolesButton("rolesPopupButton"));
			this.add(agreements);
			WebMarkupContainer nodes = createRolesView("nodesPopupView");
			nodes.add(createViewNodesButton("nodesPopupButton"));
			this.add(nodes);
			this.add(nodeGrid = createParentNodeGrid("nodeList", pageModel.getHierarchyNodeDTO()));
			this.add(lbfndpGrid = createLBFNDPGrid("lbfndpList", pageModel.getHierarchyNodeDTO()));
			this.add(employeeGrid = createEmployeeGrid("employeeList", pageModel.getHierarchyNodeDTO()));
			this.add(addParentButton = createAddFutureParentButton("AddFutureParentButton"));
			this.add(removeParentButton = createRemoveFutureParentButton("RemoveFutureParentButton"));
			this.add(createRemoveEmployeeButton("RemoveEmployeeButton"));
			this.add(createAddEmployeeButton("AddEmployeeButton"));
			this.add(createEmployeeHistoryButton("EmployeeHistoryButton"));
			this.add(createParentHistoryButton("ParentHistoryButton"));
			add(new IFormValidator() {

				private static final long serialVersionUID = 1L;

				@SuppressWarnings("unchecked")
				public FormComponent[] getDependentFormComponents() {				
					return null;
				}
				
				public void validate(final Form form) {				
					if (getEditState().isViewOnly()) {
						return;
					}
					boolean validate = true;
					for(FormComponent comp : validationComponents){
						if(!comp.isValid()){
							validate = false;
						} 
					}
					if(validate){
						IHierarchyGUIController controller = pageModel.getHierarchyGUIController();	
						try{	
							//To get this to work I had to add ajax update for every single component
							//validate party without contact details
							controller.validateNodeWithoutContactDetail(pageModel.getHierarchyNodeDTO());					
						}catch(ValidationException ex){							
							for(String error : ex.getErrorMessages()){
								NodeForm.this.error(error);								
							}
						}
					}					
				}
				
			});
		}		
	}	

	/**
	 * Will only display the view roles link if a party is selected
	 * @param id
	 * @return
	 */
	private WebMarkupContainer createRolesView(String id){
		WebMarkupContainer comp = new WebMarkupContainer(id);
		comp.setOutputMarkupPlaceholderTag(true);
		comp.setOutputMarkupId(true);
		if(pageModel == null || pageModel.getHierarchyNodeDTO() == null 
				|| !(pageModel.getHierarchyNodeDTO() instanceof HierarchyNodeDTO) 
				|| pageModel.getHierarchyNodeDTO().getId() == 0
				|| getEditState() == EditStateType.AUTHORISE){
			comp.setVisible(false);
		}else{
			comp.setVisible(true);
		}
		return comp;
	}	
	
	/**
	 * create the button to popup the view roles popup
	 * @return
	 */
	private Link createViewRolesButton(String buttonID){		
		Link but = new AjaxFallbackLink(buttonID){			
			private static final long serialVersionUID = 1L;
			@Override
			public void onClick(AjaxRequestTarget target) {
				popupType = Popup_Type.ROLES;
				rolesPopup.show(target);					
			}						
		};
		return but;
	}
	
	/**
	 * create the button to popup the view child nodes popup
	 * @return
	 */
	private Link createViewNodesButton(String buttonID){		
		Link but = new AjaxFallbackLink(buttonID){			
			private static final long serialVersionUID = 1L;
			@Override
			public void onClick(AjaxRequestTarget target) {
				popupType = Popup_Type.CHILDREN;
				rolesPopup.show(target);					
			}						
		};
		return but;
	}
	
	/**
	 * Create the end date field
	 * @return
	 */
	private HelperPanel createTerminationDateField(){				
		final HelperPanel endDate = createPageField(pageModel.getHierarchyNodeDTO(), "Termination Date", "terminationDate", ComponentType.DATE_SELECTION_TEXTFIELD, true,true,new EditStateType[]{EditStateType.TERMINATE});
		if(endDate.getEnclosedObject() instanceof SRSDateField){
			SRSDateField field = (SRSDateField)endDate.getEnclosedObject();
			field.add(field.newDatePicker());
		}
		return endDate;
	}
	
	/**
	 * Create the status field
	 * @return
	 */
	private Label createStatusField(){
		Label status = new Label("status",(pageModel.getHierarchyNodeDTO().getStatus() != null) ? pageModel.getHierarchyNodeDTO().getStatus().getName() : "Unknown");
		return status;
	}
	
	/**
	 * Create the status change date field
	 * @return
	 */
	private Label createStatusChangeDateField(){
		//status change date will be the creation time of the last store - One can not change the hierarchy details if in a pending termination or terminated status
		Label status = new Label("statusChangeDate",(pageModel.getHierarchyNodeDTO().getCreationTime() != null) ? dteFormatter.format(pageModel.getHierarchyNodeDTO().getCreationTime()) : "");
		return status;
	}
	
	/**
	 * Create the end date field
	 * @return
	 */
	private Label createEndDateField(){
		Label status = new Label("effectiveTo",(pageModel.getHierarchyNodeDTO().getEffectiveTo() != null) ? dteFormatter.format(pageModel.getHierarchyNodeDTO().getEffectiveTo()) : "");
		return status;
	}
	
	/**
	 * Create the button to show the employee history for this node
	 * 
	 * @return
	 */
	private Button createEmployeeHistoryButton(String id) {
		final Button button = new Button(id);
		button.add(new AjaxFormComponentUpdatingBehavior("click") {
			private static final long serialVersionUID = 0L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				currentHistoryType = RoleHistoryPage.HistoryType.EMPLOYEE;
				historyWindow.show(target);
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
	 * Create the button to show the parent history for this node
	 * 
	 * @return
	 */
	private Button createParentHistoryButton(String id) {
		final Button button = new Button(id);
		button.add(new AjaxFormComponentUpdatingBehavior("click") {
			private static final long serialVersionUID = 0L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				currentHistoryType = RoleHistoryPage.HistoryType.PARENT;
				historyWindow.show(target);
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
				return new RoleHistoryPage(window,pageModel,currentHistoryType);
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
//		window.setPageMapName("EditAddressPageMap");
		return window;
	}
	
	/**
	 * create a new node form with validations attached
	 * @param id
	 * @return
	 */
	private Form createNodeForm(String id) {
		Form form = new NodeForm(id);		
		return form;
		
	}
	
	/**
	 * Create the start date field
	 * @return
	 */
	private HelperPanel createStartDateField(){
		HelperPanel startDate = createPageField(pageModel.getHierarchyNodeDTO(), "Start Date","effectiveFrom", ComponentType.DATE_SELECTION_TEXTFIELD, true,true,new EditStateType[]{EditStateType.ADD});
		if(startDate.getEnclosedObject() instanceof TextField){
			TextField field = (TextField)startDate.getEnclosedObject();
			field.add(new AttributeModifier("readonly","true"));
			validationComponents.add(field);
		}
		if(startDate.getEnclosedObject() instanceof SRSDateField){
			SRSDateField field = (SRSDateField)startDate.getEnclosedObject();
			field.add(field.newDatePicker());
		}
		
		return startDate;
	}

	/**
	 * get a type string for a given id using the list in the pagemodel
	 * @return
	 */
	private IDValueDTO getTypeForId(long type){
		for(IDValueDTO typeName : pageModel.getHierarchyTypeList()){
			if(type == typeName.getOid()){
				return typeName;
			}
		}
		return null;
	}
	
	private HelperPanel createNameField(String id){
//		add in popup op current branch names so user does not enter same/similar name twice			
		AutoCompleteTextField nameField = new AutoCompleteTextField(
				"value", new PropertyModel(pageModel.getHierarchyNodeDTO(),
				"businessName"),
				new AbstractAutoCompleteRenderer() {
					private static final long serialVersionUID = 1L;

					@Override
					protected String getTextValue(Object object) {							
						return ((ResultPartyDTO) object).getName();
					}

					@Override
					protected void renderChoice(Object object,
							Response response, String arg2) {
						ResultPartyDTO listVal = (ResultPartyDTO) object;							
						response.write(listVal.getName() + "("+getTypeForId(listVal.getTypeOid()).getName()+")");
					}
				}) {
			private static final long serialVersionUID = 1L;
			
			@Override
			protected Iterator getChoices(String input) {
				if (Strings.isEmpty(input) && input.length() < 4) {
					return Collections.EMPTY_LIST.iterator();
				}	
				//get search					
				List<ResultPartyDTO> subselection = new ArrayList<ResultPartyDTO>();
				List<ResultPartyDTO> parties = null;
				try {
					if(pageModel.getHierarchyNodeDTO() != null && pageModel.getHierarchyNodeDTO().getType() != null){
						parties = pageModel.getHierarchyGUIController().findPartyWithOrganisationNameOfType(input + "*",pageModel.getHierarchyNodeDTO().getType().getOid());
					}else{
						parties = pageModel.getHierarchyGUIController().findPartyWithOrganisationName(input + "*");
					}
					for(ResultPartyDTO party : parties){
						if(getTypeForId(party.getTypeOid()) != null){
							subselection.add(party);
						}
					}
				} catch (DataNotFoundException e) {
					return Collections.EMPTY_LIST.iterator();
				}
				return subselection.iterator();
			}

		};
		nameField.add(new AttributeModifier("size","30"));
		nameField.setRequired(true);
		nameField.setOutputMarkupId(true);
		nameField.add(new AjaxFormComponentUpdatingBehavior("change"){
			@Override
			protected void onUpdate(AjaxRequestTarget arg0) {
				//Do nothing, just want the value updated				
			}			
		});
		nameField.add(new AjaxFormComponentUpdatingBehavior("keyup"){
			@Override
			protected void onUpdate(AjaxRequestTarget arg0) {
				//Do nothing, just want the value updated				
			}			
		});
		validationComponents.add(nameField);
		HelperPanel namePanel =createPageField(id,"Name", nameField, new EditStateType[]{EditStateType.ADD,EditStateType.MODIFY});
		return namePanel;
	}
	
	private ModalWindow createSearchWindow(String id) {

		ContextSearchPopUp popUp = new ContextSearchPopUp() {

			@Override
			public ContextType getContextType() {
				return ContextType.PARTY_PERSON_ONLY; //MZP0801 Party Person Only
			}

			@Override
			public void doProcessSelectedItems(AjaxRequestTarget target,
					ArrayList<ResultContextItemDTO> selectedItemList) {
				if (selectedItemList.size() == 0) {
					// Nothing was selected
					return;
				}					
				//adding party to agreement
				for (ResultContextItemDTO contextItemDTO : selectedItemList) {
					ResultPartyDTO resultPartyDTO = contextItemDTO
							.getPartyDTO();
					if(resultPartyDTO.getTypeOid() != SRSType.PERSON){
						HierarchyNodePanel.this.error("Employee's must be people");		
						if(feedBackPanel != null){
							target.add(feedBackPanel);
							break;
						}
					}else if(currentSearchEmployee != null){
						currentSearchEmployee.setSelectedParty(resultPartyDTO);
						if (target != null) {
							target.add(employeeGrid);
						}
						break;
					}					
				}
				
			}
		};		
		ModalWindow win = popUp.createModalWindow(id);
//		win.setPageMapName("employeeSearchPageMap");
		return win;	
	}

	/**
	 * Create the branch code field
	 * @return
	 */
	private HelperPanel createBranchCodeField() {
		HelperPanel ret = createPageField(pageModel.getHierarchyNodeDTO(),
				"Branch Code/External Reference", "branchCode", ComponentType.TEXTFIELD,
				true,true,new EditStateType[]{});//removed as refs will now be auto generated --> EditStateType.ADD,EditStateType.MODIFY
		Component comp = ret.get("value");
		if (comp != null && comp instanceof TextField) {				
			//comp.add(new AttributeModifier("maxlength", "3"));
			validationComponents.add((TextField)comp);
			comp.add(new AttributeModifier("size", "5")).add(new AttributeModifier("class", ""));
		}			
		ret.setOutputMarkupId(true);
		return ret;
	}
	
	private WebMarkupContainer createChannelNameField(){
		WebMarkupContainer channelName = new WebMarkupContainer("channelName");						
		channelName.setOutputMarkupId(true);	
		channelName.setOutputMarkupPlaceholderTag(true);	
		changeChannelNameField(channelName);		
		return channelName;
	}
	
	private void changeChannelNameField(WebMarkupContainer channelName){
		if(pageModel != null && pageModel.getHierarchyNodeDTO() != null && pageModel.getHierarchyNodeDTO().getType() != null && pageModel.getHierarchyNodeDTO().getType().getOid() != SRSType.DIVISION){
			channelName.setVisible(true);
		}else{
			channelName.setVisible(false);
		}
	}
	
	private Label createBranchNameField(){
		Label branchCodeName = new Label("branchCodeName");						
		branchCodeName.setOutputMarkupId(true);		
		changeBranchNameField(branchCodeName);		
		return branchCodeName;
	}		

	private HelperPanel createTypeField() {
		SRSDropDownChoice dropDownChoice = new SRSDropDownChoice("value",
				new PropertyModel(pageModel.getHierarchyNodeDTO(), "type"),
				pageModel.getHierarchyTypeList(), new ChoiceRenderer(
						"name", "oid"), "Select");
		dropDownChoice
				.add(new AjaxFormComponentUpdatingBehavior("change") {

					@Override
					protected void onUpdate(AjaxRequestTarget target) {
						typeSelected(target);				
					}
				});
		dropDownChoice.setLabel(new Model("Type"));
		dropDownChoice.setRequired(true);
		typePanel = createPageField("type","Type", dropDownChoice,new EditStateType[]{EditStateType.ADD});
		typePanel.setOutputMarkupId(true);
		dropDownChoice.setOutputMarkupId(true);
		validationComponents.add(dropDownChoice);
		return typePanel;
	}
	

	private HelperPanel createExternalType() {
		SRSDropDownChoice dropDownChoice = new SRSDropDownChoice("value",
				new PropertyModel(pageModel.getHierarchyNodeDTO(), "externalType"),
				pageModel.getOrganisationTypeList(), new ChoiceRenderer(
						"name", "key"), "Select");
		dropDownChoice
				.add(new AjaxFormComponentUpdatingBehavior("change") {

					@Override
					protected void onUpdate(AjaxRequestTarget target) {
						typeSelected(target);				
					}
				});
		dropDownChoice.setLabel(new Model("ExternalType"));
		dropDownChoice.setRequired(true);
		externalType = createPageField("externalType","ExternalType", dropDownChoice,new EditStateType[]{EditStateType.ADD, EditStateType.MODIFY});
		externalType.setOutputMarkupId(true);
		dropDownChoice.setOutputMarkupId(true);
		changeExternalTypeField(externalType);
		validationComponents.add(dropDownChoice);
		
		return externalType;
	}
	
	/**
	 * Called whenever a new type is chosen
	 *
	 */
	private void typeSelected(AjaxRequestTarget target){
//		 want to check that the filter details are
		// corrrect so we can update the parent type
		// selection
		if (pageModel.getHierarchyNodeDTO() != null && pageModel.getHierarchyNodeDTO().getType() != null) {
			//add and remove mandetory parent node, divisions don't have a parent
			if(pageModel.getHierarchyNodeDTO().getType().getOid() == SRSType.DIVISION){
//				remove parent node
				oldParents = null;
				if(pageModel.getHierarchyNodeDTO().getParents().size() != 0){
					oldParents = new ArrayList<HierarchyNodeLinkDTO>(pageModel.getHierarchyNodeDTO().getParents());
					pageModel.getHierarchyNodeDTO().getParents().clear();
				}
			}else{	
				//add parent node
				if(pageModel.getHierarchyNodeDTO().getParents().size() == 0){
					if(oldParents == null || oldParents.size() == 0){
						HierarchyNodeLinkDTO newlink = new HierarchyNodeLinkDTO();
						newlink.setEffectiveFrom(TemporalityHelper.getInstance().getNewNOWDateWithNoTime());
						pageModel.getHierarchyNodeDTO().getParents().add(newlink);
					}else{
						pageModel.getHierarchyNodeDTO().getParents().addAll(oldParents);
						oldParents = null;
					}
				}
			}	
			if(target != null){				
				target.add(nodeGrid);	
			}
		}
		changeCostCenterNameField((WebMarkupContainer) costCenterNameComponent);
		changeExternalTypeLabelField((WebMarkupContainer) externalTypeLabelComponent);
		changeExternalTypeField(externalType);
		changeCostCenterField(costCenterPanel);
		changeBranchNameField(branchCodeNameComponent);		
		changeChannelField(channelPanel);		
		setUpParentbutton(addParentButton);
		setUpParentbutton(removeParentButton);
		changeChannelNameField(channelName);
		if(target != null){
			target.add(costCenterNameComponent);
			target.add(externalTypeLabelComponent);
			target.add(externalType);
			target.add(branchCodeNameComponent);
			target.add(costCenterPanel);
			target.add(channelPanel);
			target.add(addParentButton);
			target.add(removeParentButton);
			target.add(channelName);		
		}
		updateParentTypeList(target);	
	}

	@SuppressWarnings("unchecked")
	private HelperPanel createCostCenterField(String id) {
		final List<CostCenterDBEnumDTO> dtos = pageModel.getCostCenters();			
		final AutoCompleteTextField costCenterField = new AutoCompleteTextField(
				"value", new PropertyModel(pageModel,
				"costCenterSelection"),
				new AbstractAutoCompleteRenderer() {
					private static final long serialVersionUID = 1L;

					@Override
					protected String getTextValue(Object object) {
						return ((CostCenterDBEnumDTO) object).getName();
					}

					@Override
					protected void renderChoice(Object object,
							Response response, String arg2) {
						response.write(((CostCenterDBEnumDTO) object).getName());
					}

				}) {

			private static final long serialVersionUID = 1L;
			
			@Override
			protected Iterator getChoices(String input) {
				if (Strings.isEmpty(input)) {
					return Collections.EMPTY_LIST.iterator();
				}	
				List<DatabaseEnumDTO> subList  = new ArrayList<DatabaseEnumDTO>(dtos.size());
				for (DatabaseEnumDTO dto : dtos) {	
					if(dto.getName().toUpperCase().contains(input.toUpperCase())){
						subList.add(dto);
					}
				}					
				return subList.iterator();
			}

		};	
		costCenterField.add(new AjaxFormComponentUpdatingBehavior("change"){

			@Override
			protected void onUpdate(AjaxRequestTarget arg0) {
				//put in for value to update 				
			}				
		});
		costCenterField.setLabel(new Model("Cost Center"));	
		costCenterField.add(new AttributeModifier("size","30"));
		HelperPanel costCenterPanel = createPageField(id,"Cost Center", costCenterField, new EditStateType[]{EditStateType.ADD,EditStateType.MODIFY});
		costCenterPanel.setOutputMarkupId(true);
		costCenterPanel.setOutputMarkupPlaceholderTag(true);
		changeCostCenterField(costCenterPanel);	
		validationComponents.add(costCenterField);
		return costCenterPanel;
	}		
	
	private Component createCostCenterNameField(){
		//keep name
		WebMarkupContainer costCenterNameComponent = new WebMarkupContainer("costCenterName");
		
		costCenterNameComponent.setOutputMarkupId(true);
		costCenterNameComponent.setOutputMarkupPlaceholderTag(true);
		changeCostCenterNameField(costCenterNameComponent);
		return costCenterNameComponent;
	}
	
	private Component createExternalTypeLabelField(){
		//keep name
		WebMarkupContainer externalTypeLabelComponent = new WebMarkupContainer("externalTypeLabel");
		
		externalTypeLabelComponent.setOutputMarkupId(true);
		externalTypeLabelComponent.setOutputMarkupPlaceholderTag(true);
		changeExternalTypeLabelField(externalTypeLabelComponent);
		return externalTypeLabelComponent;
	}
	
	private void changeCostCenterNameField(WebMarkupContainer costCenterNameComponent){
		if(pageModel != null && pageModel.getHierarchyNodeDTO() != null && pageModel.getHierarchyNodeDTO().getType() != null && pageModel.getHierarchyNodeDTO().getType().getOid() == SRSType.BRANCH){
			costCenterNameComponent.setVisible(true);
		}else{
			costCenterNameComponent.setVisible(false);
		}
	}
	
	private void changeExternalTypeLabelField(WebMarkupContainer externalTypeLabelComponent){
		if(pageModel != null && pageModel.getHierarchyNodeDTO() != null && pageModel.getHierarchyNodeDTO().getType() != null && pageModel.getHierarchyNodeDTO().getType().getOid() == SRSType.BRANCH){
			externalTypeLabelComponent.setVisible(true);
		}else{
			externalTypeLabelComponent.setVisible(false);
		}
	}
	
	private void changeBranchNameField(Label branchCodeName){
		if(pageModel != null && pageModel.getHierarchyNodeDTO() != null && pageModel.getHierarchyNodeDTO().getType() != null && pageModel.getHierarchyNodeDTO().getType().getOid() == SRSType.BRANCH){
			branchCodeName.setDefaultModel(new Model("Branch Code:*"));
		}else{
			branchCodeName.setDefaultModel(new Model("External Ref:*"));
		}
	}
	
	private void changeCostCenterField(HelperPanel costCenterPanel){			
		Component comp = costCenterPanel.getEnclosedObject();
		TextField costCenterField = null;
		if(comp instanceof TextField){
			costCenterField = (TextField) comp;
		}
		if(pageModel != null && pageModel.getHierarchyNodeDTO() != null && pageModel.getHierarchyNodeDTO().getType() != null && pageModel.getHierarchyNodeDTO().getType().getOid() == SRSType.BRANCH){
			costCenterPanel.setVisible(true);	
			if(costCenterField != null){
				costCenterField.setRequired(true);
			}
		}else{
			costCenterPanel.setVisible(false);
			if(costCenterField != null){
				costCenterField.setRequired(false);
			}
		}
	}
	
	private void changeExternalTypeField(HelperPanel externalTypePanel){			
		Component comp = externalTypePanel.getEnclosedObject();
		TextField externalTypeField = null;
		if(comp instanceof TextField){
			externalTypeField = (TextField) comp;
		}
		//if(pageModel != null && pageModel.getHierarchyNodeDTO() != null && pageModel.getHierarchyNodeDTO().getType() != null && pageModel.getHierarchyNodeDTO().getType().getOid() == SRSType.BRANCH){
		if(pageModel != null && pageModel.getHierarchyNodeDTO() != null && pageModel.getHierarchyNodeDTO().getType() != null && pageModel.getHierarchyNodeDTO().getType().getOid() == SRSType.BRANCH){
			comp.setVisible(true);	
//			if(externalTypeField != null){
//				externalTypeField.setRequired(true);
//				externalTypeField.setVisible(true);
//			}
		}else{
			comp.setVisible(false);
//			if(externalTypeField != null){
//				externalTypeField.setVisible(false);
//				externalTypeField.setRequired(false);
//			}
		}
	}
	
	private void setUpParentbutton(Button button){
		if(getEditState() == EditStateType.AUTHORISE){
			button.setVisible(false);
		}else if(!getEditState().isViewOnly() && getEditState() != EditStateType.TERMINATE 
				&& pageModel != null && pageModel.getHierarchyNodeDTO() != null 
				&& pageModel.getHierarchyNodeDTO().getType() != null 
				&& pageModel.getHierarchyNodeDTO().getType().getOid() != SRSType.DIVISION){
			button.setEnabled(true);
		}else{
			button.setEnabled(false);
		}
	}
	
	/**
	 * create the channel helper panel
	 * @param channelPannel
	 */
	private void changeChannelField(HelperPanel channelPannel){
		Component comp = channelPannel.getEnclosedObject();
		DropDownChoice channelField = null;
		if(comp instanceof DropDownChoice){
			channelField = (DropDownChoice) comp;
		}
		if(pageModel != null && pageModel.getHierarchyNodeDTO() != null && pageModel.getHierarchyNodeDTO().getType() != null && pageModel.getHierarchyNodeDTO().getType().getOid() != SRSType.DIVISION){
			channelPannel.setVisible(true);	
			if(channelField != null){
				channelField.setRequired(true);
			}
		}else{
			channelPannel.setVisible(false);
			if(channelField != null){
				channelField.setRequired(false);
			}
		}			
	}
	
	private HelperPanel createChannelField(){
//		if(getEditState().isViewOnly() || getEditState() == EditStateType.TERMINATE){
//		Label lab = new Label("value",);
//	}
		
		Model<IPartyNameAndIdFLO> dropdownModel = new Model<IPartyNameAndIdFLO>(){
			private static final long serialVersionUID = 1L;

			@Override
			public IPartyNameAndIdFLO getObject() {
				if(pageModel.getHierarchyNodeDTO() == null || pageModel.getHierarchyNodeDTO().getChannel() == null){
					return null;
				}
				if(pageModel.getHierarchyChannelList() == null){
					logger.warn("pageModel.getHierarchyChannelList is null");
					return null;
				}
				for(IPartyNameAndIdFLO flo : pageModel.getHierarchyChannelList()){
					if(flo.getOid() == pageModel.getHierarchyNodeDTO().getChannel().getOid()){
						return flo;
					}
				}
				return null;
			}

			@Override
			public void setObject(IPartyNameAndIdFLO flo) {
				if(flo == null){
					super.setObject(null);
					pageModel.getHierarchyNodeDTO().setChannel(null);
					return;
				}
				//look up ResultPartyDTO
				try {
					pageModel.getHierarchyNodeDTO().setChannel(getPartyManagement().findPartyWithObjectOid(flo.getOid()));					
				} catch (DataNotFoundException e) {	
					//should never happen and serious if it does so throw comm exception
					throw new CommunicationException(e);
				}					
			}			
		};
		
		SRSDropDownChoice<IPartyNameAndIdFLO> dropDownChoice = new SRSDropDownChoice<IPartyNameAndIdFLO>("value",
				dropdownModel, pageModel.getHierarchyChannelList(),
				new ChoiceRenderer<IPartyNameAndIdFLO>("name", "oid"), "Select");
				dropDownChoice.add(new AjaxFormComponentUpdatingBehavior("change") {					
					private static final long serialVersionUID = 1L;

					@Override
					protected void onUpdate(AjaxRequestTarget target) {
						//updateParentTypeList(target);
						typeSelected(target);
					}
				});
		dropDownChoice.setLabel(new Model<String>("Channel"));	
		HelperPanel channelPanel = createPageField("channel","Channel", dropDownChoice, new EditStateType[]{EditStateType.ADD,EditStateType.MODIFY});
		channelPanel.setOutputMarkupId(true);
		dropDownChoice.setOutputMarkupId(true);
		channelPanel.setOutputMarkupPlaceholderTag(true);
		changeChannelField(channelPanel);	
		dropDownChoice.setRequired(true);
		return channelPanel;
	}
	

	/**
	 * Update the parent type list based on what was selected for the actual
	 * node being added
	 * 
	 * @param targeto
	 */
	private void updateParentTypeList(AjaxRequestTarget target) {		
		ResultPartyDTO channel = pageModel.getHierarchyNodeDTO()
				.getChannel();
		IDValueDTO type = pageModel.getHierarchyNodeDTO().getType();	
		if(channel == null || type == null){
			parentSelectionList.clear();
		}		
		if (channel != null && type != null && (currentType != type.getOid() || currentChannel != channel.getTypeOid())) {
			parentSelectionList.clear();
			currentType = type.getOid();
			currentChannel = channel.getTypeOid();
			List<Long> parentTypes = pageModel.getHierarchyGUIController().getAllowableHierarchyParentTypesForChildTypes(type.getOid());
			IDValueDTO defaultVal = null;
			for(Long parentType : parentTypes){
				parentSelectionList.add(pageModel.getTypeFromList(parentType));
			}
			if(parentSelectionList.size() > 0){
				defaultVal = parentSelectionList.get(0);
			}				
			// we make a default choice for the user			
			if (defaultVal != null) {
				List<HierarchyNodeLinkDTO> parents = pageModel
						.getHierarchyNodeDTO().getParents();
				for (HierarchyNodeLinkDTO parent : parents) {
					if(parent.getOid() == 0 && (parent.getType() == null || parent.getType().getOid() != defaultVal.getOid())){
						parent.setType(defaultVal);	
//						if(target != null){			
//							typeSelected(target);							
//						}
					}
				}
			}	
		}	
		if(target != null){	
			//based on the type selection, update the parent selection list			
			for(HierarchyNodeLinkDTO parent : pageModel.getHierarchyNodeDTO().getParents()){
				updateData(parent);				
			}	
			target.add(nodeGrid);	
		}
	}		

	/**
	 * Create the button to add a future Parent Node
	 * 
	 * @return
	 */
	private Button createAddFutureParentButton(String id) {
		final Button button = new Button(id);
		button.add(new AjaxFormComponentUpdatingBehavior("click") {
			private static final long serialVersionUID = 0L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {					
				if (pageModel.getHierarchyNodeDTO().getParents().size() != 2) {					
					HierarchyNodeLinkDTO dto = new HierarchyNodeLinkDTO();
					dto.setEffectiveFrom(new Date());
					List<Long> parentTypes = pageModel.getHierarchyGUIController().getAllowableHierarchyParentTypesForChildTypes(pageModel.getHierarchyNodeDTO().getType().getOid());
					if(parentTypes != null && parentTypes.size() != 0){
						dto.setType(pageModel.getTypeFromList(parentTypes.get(0)));						
					}					
					pageModel.getHierarchyNodeDTO().getParents().add(dto);					
					target.add(nodeGrid);
				} else {
					error("Only one future parent is allowed");
					if (HierarchyNodePanel.this.feedBackPanel != null) {
						target.add(HierarchyNodePanel.this.feedBackPanel);
					}
				}
			}
		});		
		button.setOutputMarkupId(true);
		button.setOutputMarkupPlaceholderTag(true);
		setUpParentbutton(button);
		return button;
	}		

	/**
	 * Create the button to remove the current Parent Node
	 * 
	 * @return
	 */
	private Button createRemoveFutureParentButton(String id) {
		final Button button = new Button(id);
		button.add(new AjaxFormComponentUpdatingBehavior("click") {
			private static final long serialVersionUID = 0L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				List<Object> selections = nodeGrid.getSelectedItemObjects();				
				//currentParent = getCurrentParentNode();				
				//HierarchyNodeLinkDTO main = pageModel.getHierarchyNodeDTO()
				//		.getParents().get(0);
				for (Object selection : selections) {
					// check that we are not removing the main parent link,
					// users must change the main parent but can not remove
					// it
					if (selection != currentParent) {
						pageModel.getHierarchyNodeDTO().getParents()
								.remove(selection);
					}
				}
				if (pageModel.getHierarchyNodeDTO().getParents().size() == 1
						&& selections.size() != 0) {
					// we erase the end date
					try {
						currentParent.setEffectiveTo(null);
					}catch (NullPointerException npe){
						logger.error("in npe");
					}
				}
				if (HierarchyNodePanel.this.feedBackPanel != null) {
					target.add(HierarchyNodePanel.this.feedBackPanel);
				}
				target.add(nodeGrid);
			}
		});
		button.setOutputMarkupId(true);
		button.setOutputMarkupPlaceholderTag(true);
		setUpParentbutton(button);
		return button;
	}

	/**
	 * Create the button to remove the current Parent Node
	 * 
	 * @return
	 */
	private Button createRemoveEmployeeButton(String id) {
		final Button button = new Button(id);
		button.add(new AjaxFormComponentUpdatingBehavior("click") {
			private static final long serialVersionUID = 0L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				List<Object> links = employeeGrid.getSelectedItemObjects();
				for (Object link : links) {
					pageModel.getHierarchyNodeDTO().getLinkedEmployees()
							.remove(link);
				}
				target.add(employeeGrid);
			}
		});
		if(getEditState() == EditStateType.AUTHORISE){
			button.setVisible(false);
		}else if (getEditState().isViewOnly() || getEditState() ==  EditStateType.TERMINATE) {
			button.setEnabled(false);			
		}
		button.setOutputMarkupId(true);
		return button;
	}

	/**
	 * Create the button to remove the current Parent Node
	 * 
	 * @return
	 */
	private Button createAddEmployeeButton(String id) {
		final Button button = new Button(id);
		button.add(new AjaxFormComponentUpdatingBehavior("click") {
			private static final long serialVersionUID = 0L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				HierarchyEmployeeLinkDTO dto = new HierarchyEmployeeLinkDTO();
				dto.setEffectiveFrom(new Date());
				pageModel.getHierarchyNodeDTO().getLinkedEmployees().add(
						dto);
				target.add(employeeGrid);
			}
		});
		if(getEditState() == EditStateType.AUTHORISE){
			button.setVisible(false);
		}else if(getEditState().isViewOnly() || getEditState() ==  EditStateType.TERMINATE) {
			button.setEnabled(false);
			//button.setVisible(false);
		}
		button.setOutputMarkupId(true);
		return button;
	}

	/**
	 * Create a grid for the parent nodes
	 * 
	 * @return
	 */
	private SRSDataGrid createParentNodeGrid(String id, HierarchyNodeDTO dto) {
		List<HierarchyNodeLinkDTO> nodes = dto.getParents();
		if (nodes == null) {
			nodes = new ArrayList<HierarchyNodeLinkDTO>();
		}
		//quick doing a check to see if the actual node is still active, the role is active but the node might have been terminated
		for(HierarchyNodeLinkDTO parent : nodes){
			if(parent.getPartyLink().getEffectiveTo() != null && !parent.getPartyLink().getEffectiveTo().after(TemporalityHelper.getInstance().getNewNOWDateWithNoTime())){
				//node has been terminated
				warn("Parent node " + parent.getPartyLink().getName() + " has been removed, please remove the link to it and add another parent");
			}
		}
		List<HierarchyNodeLinkDTO> noneSelectable = TemporalityHelper.getInstance().getCurrentObjects(new Date(), nodes);
		SRSDataGrid grid = new SRSDataGrid(id, new DataProviderAdapter(
				new ListDataProvider<HierarchyNodeLinkDTO>(nodes)),
				getNodeColumns(), getEditState(),noneSelectable);
		
		grid.setCleanSelectionOnPageChange(true);
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
	private List<IGridColumn> getNodeColumns() {
		Vector<IGridColumn> cols = new Vector<IGridColumn>(6);
		if (!getEditState().isViewOnly() && getEditState() !=  EditStateType.TERMINATE) {
			SRSGridRowSelectionCheckBox col = new SRSGridRowSelectionCheckBox(
					"checkBox");
			cols.add(col.setInitialSize(30));
		}
		cols.add(new SRSDataGridColumn<HierarchyNodeLinkDTO>("type",
				new Model("Parent Type"), "type", "type", getEditState()) {		
			private static final long serialVersionUID = 1L;
			@Override
			public Panel newCellPanel(WebMarkupContainer parent,
					String componentId, IModel rowModel,
					String objectProperty, EditStateType state,
					final HierarchyNodeLinkDTO data) {					
				if (getEditState().isViewOnly() || getEditState() ==  EditStateType.TERMINATE || data.getOid() != 0) {
					return super.newCellPanel(parent, componentId,
							rowModel, objectProperty, state, data);
				}
				updateParentTypeList(null);
				HelperPanel dropdown = createDropdownField(data, "Parent Type",
						objectProperty, parentSelectionList, new ChoiceRenderer("name",
								"oid"), null, true,new EditStateType[]{EditStateType.ADD,EditStateType.MODIFY});
				DropDownChoice dropdownComp = (DropDownChoice) dropdown
						.getEnclosedObject();
				dropdownComp.add(new AjaxFormComponentUpdatingBehavior(
						"change") {					
					private static final long serialVersionUID = 1L;

					@Override
					protected void onUpdate(AjaxRequestTarget target) {
						//refresh table
						typeSelected(target);
						//target.add(nodeGrid);
					}
				});
				dropdownComp.setNullValid(false);
				validationComponents.add(dropdownComp);
				return dropdown;
			}
		}.setInitialSize(140));

		AbstractColumn parentNameCol = new SRSDataGridColumn<HierarchyNodeLinkDTO>("partyLink",
				new Model("Parent Name"), "partyLink", "partyLink", getEditState()) {
			@Override
			public Panel newCellPanel(WebMarkupContainer parent,
					String componentId, IModel rowModel,
					String objectProperty, EditStateType state,
					final HierarchyNodeLinkDTO data) {					
				if (getEditState().isViewOnly() || getEditState() ==  EditStateType.TERMINATE  || data.getOid() != 0) {				
					return super.newCellPanel(parent, componentId,
							rowModel, objectProperty, state, data);
				}	
				updateData(data);				
				SRSDropDownChoice dropDownChoice = new SRSDropDownChoice("value",
						new PropertyModel(data,objectProperty){							
							private static final long serialVersionUID = 1L;							

							@Override
							public void setObject(Object obj) {
//								use the Resultto create a flo
								IPartyNameAndIdFLO flo = (IPartyNameAndIdFLO) obj;
								if(flo == null){
									super.setObject(null);
									return;
								}
								//look up ResultPartyDTO
								try {
									super.setObject(getPartyManagement().findPartyWithObjectOid(flo.getOid()));
								} catch (DataNotFoundException e) {	
									//should never happen and serious if it does so throw comm exception
									//throw new CommunicationException(e);
									error("Party " + flo.getName() + " can not be used as the party seems to have been ended but the role is still active");
									
									AjaxRequestTarget target = RequestCycle.get().find(AjaxRequestTarget.class);
									super.setObject(null);
									
									if (target!=null) {
										target.add(getFeedBackPanel());
										target.add(nodeGrid);
									}
									
								}								
							}					
					}, parentSelections.get(data).getParentNodesSelection(),
						new ChoiceRenderer("name", "oid"), "Select");
				dropDownChoice.add(new AjaxFormComponentUpdatingBehavior("change") {
					@Override
					protected void onUpdate(AjaxRequestTarget target) {
						//update the model with a ResultPartyDTO
						
					}
				});
				HelperPanel dropdownPanel = HelperPanel.getInstance(componentId, dropDownChoice);
				
//				HelperPanel dropdownPanel = createDropdownField(data, "Parent Name", objectProperty,
//						nodes ,
//						new ChoiceRenderer("name", "partyOid"), "Select",
//						true,new EditStateType[]{EditStateType.ADD,EditStateType.MODIFY});
//					dropdownPanel.getEnclosedObject().add(new AjaxFormComponentUpdatingBehavior("change") {
//					@Override
//					protected void onUpdate(AjaxRequestTarget target) {
//						//update the model with a ResultPartyDTO
//						
//					}
//				});
				if(dropdownPanel.getEnclosedObject() instanceof DropDownChoice){
					validationComponents.add((DropDownChoice)dropdownPanel.getEnclosedObject());
				}
				return dropdownPanel;
			}
		};
		if(getEditState() == EditStateType.ADD){
			parentNameCol.setInitialSize(230);
		}else{
			parentNameCol.setInitialSize(330);
		}
		cols.add(parentNameCol);
		cols.add(new SRSDataGridColumn<HierarchyNodeLinkDTO>("effectiveFrom",
				new Model("Start Date"), "effectiveFrom", "effectiveFrom",
				getEditState()) {

			@Override
			public Panel newCellPanel(WebMarkupContainer parent,
					String componentId, IModel rowModel,
					String objectProperty, EditStateType state,
					HierarchyNodeLinkDTO data) {				
				//HierarchyNodeLinkDTO currentParent = getCurrentParentNode();				
				if (getEditState().isViewOnly() || getEditState() ==  EditStateType.TERMINATE || (data.getOid() != 0 && currentParent.equals(data))) {
					return super.newCellPanel(parent, componentId,
							rowModel, objectProperty, state, data);
				}
				SRSDateField startDate = new SRSDateField("value",
						new PropertyModel(data, objectProperty));
				startDate.add(startDate.newDatePicker());
				startDate.add(new AttributeModifier("size", "11"));
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
				validationComponents.add(startDate);				
				return HelperPanel
						.getInstance(componentId, startDate, true);
			}

		}.setInitialSize(115));

		cols.add(new SRSDataGridColumn<HierarchyNodeLinkDTO>("effectiveTo",
				new Model("End Date"), "effectiveTo", "effectiveTo", getEditState()) {

			@Override
			public Panel newCellPanel(WebMarkupContainer parent,
					String componentId, IModel rowModel,
					String objectProperty, EditStateType state,
					HierarchyNodeLinkDTO data) {
				if (getEditState().isViewOnly() || getEditState() ==  EditStateType.TERMINATE) {
					return super.newCellPanel(parent, componentId,
							rowModel, objectProperty, state, data);
				}
				SRSDateField endDate = new SRSDateField("value",
						new PropertyModel(data, objectProperty));
				endDate.add(endDate.newDatePicker());
				endDate.add(new AttributeModifier("size", "11"));
				endDate.add(new AttributeModifier("maxlength", "10"));
				endDate.setLabel(new Model("Parent End Date"));
				endDate.add(new AjaxFormComponentUpdatingBehavior("change") {
					@Override
					protected void onUpdate(AjaxRequestTarget target) {
						//do nothing just want the object value updated
					}
				});
				//validationComponents.add(endDate);	
				return HelperPanel.getInstance(componentId, endDate, true);
			}

		}.setInitialSize(115));
		return cols;
	}

	/**
	 * Create a grid for the nodes
	 * 
	 * @return
	 */
	private SRSDataGrid createEmployeeGrid(String id, HierarchyNodeDTO dto) {
		List<HierarchyEmployeeLinkDTO> nodes = dto.getLinkedEmployees();
		if (nodes == null) {
			nodes = new ArrayList<HierarchyEmployeeLinkDTO>();
		}
		
		SRSDataGrid grid = new SRSDataGrid(id, new DataProviderAdapter(
				new ListDataProvider<HierarchyEmployeeLinkDTO>(nodes))
				, getEmployeeColumns(), null);
		grid.setCleanSelectionOnPageChange(false);
	
		grid.setClickRowToSelect(false);
		grid.setAllowSelectMultiple(true);
		grid.setGridWidth(99, GridSizeUnit.PERCENTAGE);
		//grid.setGridWidth(650, GridSizeUnit.PIXELS);
		grid.setRowsPerPage(10);
		grid.setContentHeight(70, SizeUnit.PX);
		return grid;
	}

	private List<IGridColumn> getEmployeeColumns() {
		Vector<IGridColumn> cols = new Vector<IGridColumn>(7);
		if (!getEditState().isViewOnly() && getEditState() != EditStateType.TERMINATE) {
			SRSGridRowSelectionCheckBox col = new SRSGridRowSelectionCheckBox(
					"checkBox");
			cols.add(col.setInitialSize(30));
		}
		
		// Hierarchy Type
		cols.add(new SRSDataGridColumn<HierarchyEmployeeLinkDTO>(
				"hierarchyType", new Model("Type"), "hierarchyType",
				"hierarchyType", getEditState()) {

			@Override
			public Panel newCellPanel(WebMarkupContainer parent,
					String componentId, IModel rowModel,
					String objectProperty, EditStateType state,
					final HierarchyEmployeeLinkDTO data) {
				if (getEditState().isViewOnly() || getEditState() ==  EditStateType.TERMINATE || data.getOid() != 0) {
					return super.newCellPanel(parent, componentId,
							rowModel, objectProperty, state, data);
				}


				HelperPanel dropdownPanel = createDropdownField(data, "Employee Type", objectProperty, pageModel
								.getEmployeeTypeList(), new ChoiceRenderer(
								"name", "oid"), "Select", true,new EditStateType[]{EditStateType.ADD,EditStateType.MODIFY});
				if (logger.isDebugEnabled())
					logger.debug("Column col.employeeType  " + data);
				
				dropdownPanel.getEnclosedObject().add(new AjaxFormComponentUpdatingBehavior("change") {
					@Override
					protected void onUpdate(AjaxRequestTarget target) {
						//do nothing just want the object value updated
						AgreementGridData gridDataObj = gridData.get(data);
						if (logger.isDebugEnabled())
							logger.debug("Update col.hierarchyType " + data
									+ "  gridDataObj =" + gridDataObj);
						if(gridDataObj == null){
							gridDataObj = new AgreementGridData();
							gridData.put(data, gridDataObj);
						}
						
						Panel panel = (Panel)gridDataObj.getComponent("subType");						
						if(panel != null)	{	
							if (logger.isDebugEnabled())
								logger.debug(" -- Update col.hierarchyType - replace panel ");
							HelperPanel panel2 = getSecondaryChoiceGridSupervisorSelectionDropdown(panel.getId(), "subType", data);							
							panel.replaceWith(panel2);	
							gridDataObj.addComponent("subType", panel2);
							
							if (logger.isDebugEnabled())
								logger.debug(" -- Update col.hierarchyType - fail here?? ");
			
//							target.add(panel2);  // #JEAN CHANGED HERE
							target.add(employeeGrid);
							//target.add(typeDropDown);
						} else {
							if (logger.isDebugEnabled())
								logger.debug(" -- Update col.hierarchyType, subtype not found  " + data); 
						}
						
						//update the grid as the next column needs this value
						//target.add(employeeGrid);
					}

					
				});
				dropdownPanel.setOutputMarkupId(true);
				if(dropdownPanel.getEnclosedObject() instanceof DropDownChoice){
					validationComponents.add((DropDownChoice)dropdownPanel.getEnclosedObject());
				}
//				HelperPanel dropdownPanel = HelperPanel.getInstance(componentId, dropdown);
				typeDropDown = dropdownPanel;
				typeDropDown.setOutputMarkupId(true);
				return dropdownPanel;
			}

		}.setInitialSize(130));
		
		SRSDataGridColumn nameCol = new SRSDataGridColumn<HierarchyEmployeeLinkDTO>("selectedParty.name",
				new Model("Name"), "selectedParty.name", "selectedParty.name", getEditState());
		if(getEditState() == EditStateType.ADD){
			nameCol.setInitialSize(150);
		}else{
			nameCol.setInitialSize(250);
		}
		cols.add(nameCol);
		cols.add(new SRSDataGridColumn<HierarchyEmployeeLinkDTO>("selectedParty.uacfID",
				new Model("UACFID"), "selectedParty.uacfID", "selectedParty.uacfID", getEditState()) {

			@Override
			public Panel newCellPanel(WebMarkupContainer parent,
					String componentId, IModel rowModel,
					String objectProperty, EditStateType state,
					final HierarchyEmployeeLinkDTO data) {				
				
				if (getEditState().isViewOnly() || getEditState() ==  EditStateType.TERMINATE || data.getOid() != 0) {
					return super.newCellPanel(parent, componentId,
							rowModel, objectProperty, state, data);
				}		

				final TextField uacfid = new TextField("value", new PropertyModel(data, objectProperty));				
				uacfid.add(new AttributeModifier("size", "7"));
				uacfid.add(new AttributeModifier("maxlength", "7"));
				uacfid.setOutputMarkupId(true);
				uacfid.setLabel(new Model("Employee UACFID"));
				uacfid.setRequired(true);
				/* Add behavior to update selected item */
				uacfid.add(new AjaxFormComponentUpdatingBehavior("keyup") {
					@Override
					protected void onUpdate(AjaxRequestTarget target) {
						String input = (String) uacfid.getModelObject();
						if(input != null && input.length() > 6){
							List<PartyProfileFLO> users = pageModel.getHierarchyGUIController().findUserWithUACFID(input);
							for(PartyProfileFLO user : users){
								if(user.getUacfId().equalsIgnoreCase(input)){								
									updateEmployeeRowWithSelection(users.get(0), data, target);
								}
							}
						}
					}
				});				
				//validationComponents.add(uacfid);				
				return HelperPanel.getInstance(componentId, uacfid);
			}
		}.setInitialSize(70));
		
//		add search button, don't display this column on view
		if(getEditState() == null || !(getEditState().isViewOnly() || getEditState() == EditStateType.TERMINATE)){
			cols.add(new SRSDataGridColumn<HierarchyEmployeeLinkDTO>("searchParty",
					new Model("Search"), "searchParty", getEditState()){	
				
						private static final long serialVersionUID = 1L;							

						@Override
						public Panel newCellPanel(WebMarkupContainer parent, String componentId, IModel rowModel, String objectProperty, EditStateType state, final HierarchyEmployeeLinkDTO data) {
							if(data.getOid() == 0){
								Button searchButton = new Button("value", new Model("Search"));	
								searchButton.add(new AjaxFormComponentUpdatingBehavior("click"){									
									private static final long serialVersionUID = 1L;
									@Override
									protected void onUpdate(AjaxRequestTarget target) {
										currentSearchEmployee = data;
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
		
		cols.add(new SRSDataGridColumn<HierarchyEmployeeLinkDTO>(
				"subType", new Model("Sub Type"),
				"subType", "subType", getEditState()) {				
					private static final long serialVersionUID = 1L;
					
					@Override
					public Panel newCellPanel(WebMarkupContainer parent,
							String componentId, IModel rowModel,
							String objectProperty, EditStateType state,
							final HierarchyEmployeeLinkDTO data) {
						AgreementGridData gridDataObj = gridData.get(data);
						

						if(gridDataObj == null){
							gridDataObj = new AgreementGridData();
							gridData.put(data, gridDataObj);
						}		
		
						HelperPanel panel = getSecondaryChoiceGridSupervisorSelectionDropdown(componentId, 
								objectProperty, data);				
						gridDataObj.addComponent(objectProperty, panel);
						// #JEAN
						if (logger.isDebugEnabled())
							logger.debug("Create  col.subtype for data " + data
									+ "   \n"+ panel.getEnclosedObject().getClass()
									+ "    ---  " + panel);
						return panel;			
					}
		}.setInitialSize(110));
		
		cols.add(new SRSDataGridColumn<HierarchyEmployeeLinkDTO>(
				"effectiveFrom", new Model("Start Date"),
				"effectiveFrom", "effectiveFrom", getEditState()) {				
					private static final long serialVersionUID = 1L;

			@Override
			public Panel newCellPanel(WebMarkupContainer parent,
					String componentId, IModel rowModel,
					String objectProperty, EditStateType state,
					HierarchyEmployeeLinkDTO data) {
				if (getEditState().isViewOnly() || getEditState() ==  EditStateType.TERMINATE) {
					return super.newCellPanel(parent, componentId,
							rowModel, objectProperty, state, data);
				}
				SRSDateField startDate = new SRSDateField("value",
						new PropertyModel(data, objectProperty));		
				startDate.add(startDate.newDatePicker());
				startDate.add(new AttributeModifier("size", "11"));
				startDate.add(new AttributeModifier("maxlength", "10"));
				startDate.setLabel(new Model("Employee Start Date"));
				startDate.add(new AjaxFormComponentUpdatingBehavior("change") {
					@Override
					protected void onUpdate(AjaxRequestTarget target) {
						//do nothing just want the object value updated
					}
				});	
				startDate.add(new AjaxFormComponentUpdatingBehavior("keyup") {
					@Override
					protected void onUpdate(AjaxRequestTarget target) {
						//do nothing just want the object value updated
					}
				});				
				validationComponents.add(startDate);
				startDate.add(new AttributeModifier("readonly","true"));
				startDate.setRequired(true);
				return HelperPanel.getInstance(componentId, startDate, true);
			}
		}.setInitialSize(110));
		cols.add(new SRSDataGridColumn<HierarchyEmployeeLinkDTO>(
				"effectiveTo", new Model("End Date"),
				"effectiveTo", "effectiveTo", getEditState()) {
			@Override
			public Panel newCellPanel(WebMarkupContainer parent,
					String componentId, IModel rowModel,
					String objectProperty, EditStateType state,
					HierarchyEmployeeLinkDTO data) {
				if (getEditState().isViewOnly() || getEditState() ==  EditStateType.TERMINATE) {
					return super.newCellPanel(parent, componentId,
							rowModel, objectProperty, state, data);
				}
				SRSDateField endDate = new SRSDateField("value",
						new PropertyModel(data, objectProperty));
				endDate.add(new AttributeModifier("size", "11"));
				endDate.add(new AttributeModifier("maxlength", "10"));
				endDate.setLabel(new Model("Employee End Date"));
				endDate.add(new AjaxFormComponentUpdatingBehavior("change") {
					@Override
					protected void onUpdate(AjaxRequestTarget target) {
						//do nothing just want the object value updated
					}
				});
				endDate.add(new AjaxFormComponentUpdatingBehavior("keyup") {
					@Override
					protected void onUpdate(AjaxRequestTarget target) {
						//do nothing just want the object value updated
					}
				});
				endDate.add(endDate.newDatePicker());
				return HelperPanel.getInstance(componentId, endDate, true);
			}
		}.setInitialSize(115));
		return cols;
	}
	
	/**
	 * Create an LBF NDP grid for the nodes
	 * 
	 * @return
	 */
	private SRSDataGrid createLBFNDPGrid(String id, HierarchyNodeDTO dto) {
		List<HierarchyLBFNDPLinkDTO> nodes = dto.getLinkedLBFNDP();
		if (nodes == null) {
			nodes = new ArrayList<HierarchyLBFNDPLinkDTO>();
		}
		
		SRSDataGrid grid = new SRSDataGrid(id, new DataProviderAdapter(
				new ListDataProvider<HierarchyLBFNDPLinkDTO>(nodes))
				, getLBFNDPColumns(), null);
		grid.setCleanSelectionOnPageChange(false);
	
		grid.setClickRowToSelect(false);
		grid.setAllowSelectMultiple(true);
		grid.setGridWidth(99, GridSizeUnit.PERCENTAGE);
		//grid.setGridWidth(650, GridSizeUnit.PIXELS);
		grid.setRowsPerPage(10);
		grid.setContentHeight(70, SizeUnit.PX);
		return grid;
	}
	
	private List<IGridColumn> getLBFNDPColumns() {
		Vector<IGridColumn> cols = new Vector<IGridColumn>(7);
	
		cols.add(new SRSDataGridColumn<HierarchyLBFNDPLinkDTO>(
				"hierarchyType", new Model("Type"), "hierarchyType",
				"hierarchyType", getEditState()) {



			@Override
			public Panel newCellPanel(WebMarkupContainer parent,
					String componentId, IModel rowModel,
					String objectProperty, EditStateType state,
					final HierarchyLBFNDPLinkDTO data) {
				Panel tempPanel = super.newCellPanel(parent, componentId,
						rowModel, objectProperty, state, data);
				if (getEditState().isViewOnly() || getEditState() ==  EditStateType.TERMINATE || data.getOid() != 0) {
					 
					return tempPanel;
				}
				return tempPanel;

			}

		}.setInitialSize(100));
		
		SRSDataGridColumn nameCol = new SRSDataGridColumn<HierarchyLBFNDPLinkDTO>("selectedParty.businessName",
				new Model("Name"), "selectedParty.businessName", "selectedParty.businessName", getEditState());
		if(getEditState() == EditStateType.ADD){
			nameCol.setInitialSize(150);
		}else{
			nameCol.setInitialSize(250);
		}
		cols.add(nameCol);
		
		
						
		
		cols.add(new SRSDataGridColumn<HierarchyLBFNDPLinkDTO>(
				"effectiveFrom", new Model("Start Date"),
				"effectiveFrom", "effectiveFrom", getEditState()) {				
					private static final long serialVersionUID = 1L;

			@Override
			public Panel newCellPanel(WebMarkupContainer parent,
					String componentId, IModel rowModel,
					String objectProperty, EditStateType state,
					HierarchyLBFNDPLinkDTO data) {

				return super.newCellPanel(parent, componentId,
						rowModel, objectProperty, state, data);
			}
		}.setInitialSize(110));
		cols.add(new SRSDataGridColumn<HierarchyLBFNDPLinkDTO>(
				"effectiveTo", new Model("End Date"),
				"effectiveTo", "effectiveTo", getEditState()) {
			@Override
			public Panel newCellPanel(WebMarkupContainer parent,
					String componentId, IModel rowModel,
					String objectProperty, EditStateType state,
					HierarchyLBFNDPLinkDTO data) {
				return super.newCellPanel(parent, componentId,
						rowModel, objectProperty, state, data);

			}
		}.setInitialSize(115));
		return cols;
	}
	
	/**
	 * Update the employee row with the selected user
	 * @param selection
	 * @param row
	 */
	private void updateEmployeeRowWithSelection(PartyProfileFLO selection, HierarchyEmployeeLinkDTO row, AjaxRequestTarget target){
		if(selection != null && row != null){
			try {
				ResultPartyDTO party = pageModel.getHierarchyGUIController().findPartyWithObjectOid(selection.getPartyOid());
				//party.setBranchName(party.getUacfID());				
				row.setSelectedParty(party);				
			} catch (DataNotFoundException e) {
				error("Could not find party with id " + selection.getPartyOid());
				target.add(feedBackPanel);	
			}
			target.add(employeeGrid);	
		}
	}
	
	/**
	 * Returns the current parent node for the page model node, if one cant be found then the first parent is selected
	 * @return
	 */
	private HierarchyNodeLinkDTO getCurrentParentNode(){
		if(currentParent != null){
			for(HierarchyNodeLinkDTO parent : pageModel.getHierarchyNodeDTO().getParents()){
				if(currentParent == parent){
					return currentParent;
				}
			}
		}		
		List<HierarchyNodeLinkDTO> currentParents = TemporalityHelper.getInstance().getCurrentObjects(new Date(),pageModel.getHierarchyNodeDTO().getParents());	
		if(currentParents.size() > 0){
			currentParent = currentParents.get(0);
		}
		if(currentParent == null && pageModel.getHierarchyNodeDTO().getParents().size() > 0){
			currentParent = pageModel.getHierarchyNodeDTO().getParents().get(0);
		}	
		return currentParent;
	}
	
	/**
	 * Create the roles window
	 * 
	 * @param id
	 * @return
	 */
	private ModalWindow createRolesWindow(String id) {		
		final ModalWindow window = new ModalWindow(id);
		window.setTitle("Node Info");		
		
		window.setPageCreator(new ModalWindow.PageCreator() {
			private static final long serialVersionUID = 1L;
			public Page createPage() {					
				ContextDTO dto = SRSAuthWebSession.get().getContextDTO();
				ContextPartyDTO selectedParty = null;
				if(dto != null){
					selectedParty = dto.getPartyContextDTO();						
				}		
				if(popupType == Popup_Type.CHILDREN){
					return new HierarchyChildrenPage(window,selectedParty);	
				}else{
					return new HierarchyLinkedAgreementsPopupPage(pageModel.getHierarchyNodeDTO());		
				}
			}
		});		

		// Initialise window settings		
		window.setMinimalHeight(420);
		window.setInitialHeight(420);
		window.setMinimalWidth(750);
		window.setInitialWidth(750);
		window.setMaskType(MaskType.SEMI_TRANSPARENT);
		window.setCssClassName(ModalWindow.CSS_CLASS_GRAY);		
		return window;
	}

	public Class getPanelClass() {
		return HierarchyNodePanel.class;
	}

	/**
	 * Set the feedback panel to use for errors
	 * @param feedBackPanel
	 */
	public void setFeedBackPanel(FeedbackPanel feedBackPanel) {
		this.feedBackPanel = feedBackPanel;
	}
	
	
	/**
	 * create a IPartyNameAndIdFLO from a ResultPartyDTO
	 * @param party
	 * @return
	 */
	private IPartyNameAndIdFLO createNewPartyNameAndIdFLO(final ResultPartyDTO party){
		return new IPartyNameAndIdFLO(){
			public String getName() {										
				return party.getName();
			}
			public long getOid() {
				return party.getOid();
			}
			public String getExternalReference() {
				return null;
			}
			public long getType() {
				return party.getTypeOid();
			}
			public String getJobTitle() {
				return party.getJobTitle();
			}		
			
		};
	}
	
	
	/**
	 * Get the HierarchyGUIController bean 
	 * @return
	 */
	private IHierarchyGUIController getHierarchyGUIController(){
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
	 * Update the data selection lists
	 * @param data
	 */
	private void updateData(HierarchyNodeLinkDTO data){		
		if(parentSelections.get(data) == null){
			parentSelections.put(data, new ParentDropdownObject(data));
		}				
		long type = (data.getType() != null) ? data.getType().getOid() : 0;
		long channel = (pageModel != null && pageModel.getHierarchyNodeDTO() != null && pageModel.getHierarchyNodeDTO().getChannel() != null) ? pageModel.getHierarchyNodeDTO().getChannel().getPartyOid() : 0;
		ArrayList<IPartyNameAndIdFLO> nodes;
		if(type != SRSType.DIVISION){				
			nodes = new ArrayList<IPartyNameAndIdFLO>(getPartyManagement().findPartyNamesWithIDByTypeAndChannel(type,channel, false));
		}else{
			nodes = new ArrayList<IPartyNameAndIdFLO>(1);					
			nodes.add(createNewPartyNameAndIdFLO(pageModel.getHierarchyNodeDTO().getChannel()));
		}	
		parentSelections.get(data).setParentNodesSelection(nodes);		
	}

	
	/**
	 * Class for parent selections
	 * @author DZS2610
	 *
	 */
	private class ParentDropdownObject implements Serializable{		
		private static final long serialVersionUID = 1L;
		private HierarchyNodeLinkDTO parent;
		private List<IPartyNameAndIdFLO> parentNodesSelection = new ArrayList<IPartyNameAndIdFLO>();
		
		public ParentDropdownObject(HierarchyNodeLinkDTO parent) {
			super();
			this.parent = parent;			
		}

		public HierarchyNodeLinkDTO getParent() {
			return parent;
		}

		public List<IPartyNameAndIdFLO> getParentNodesSelection() {
			return parentNodesSelection;
		}

		public void setParentNodesSelection(
				List<IPartyNameAndIdFLO> parentNodesSelection) {
			this.parentNodesSelection.clear();
			if(parentNodesSelection != null){
				this.parentNodesSelection.addAll(parentNodesSelection);
			}			
		}
		
	}	
	private List<IDValueDTO> getSubtypes() {
				
		return typeListForCategories;
	}
	
	/**
	 * get the Party Management Bean
	 * @return
	 */
	private IPartyManagement getPartyManagement(){
		if(partyManagement == null){
			try {
				partyManagement = ServiceLocator.lookupService(IPartyManagement.class);
			} catch (NamingException e) {
				throw new CommunicationException(e);
			}
		}
		return partyManagement;
	}
	
	/**
	 * Class to hold extra data about the grid, needed as the models changed in wicket 1.4
	 * @author DZS2610
	 *
	 */
	private class AgreementGridData implements Serializable{	
		private static final long serialVersionUID = 1L;
		private HashMap<String, Component> componentMap = new HashMap<String, Component>();
		private HierarchyEmployeeLinkDTO gridData;
		
		public HierarchyEmployeeLinkDTO getGridData() {
			return gridData;
		}
		public void setGridData(HierarchyEmployeeLinkDTO gridData) {
			this.gridData = gridData;
		}
		
		public void addComponent(String key, Component comp){
			componentMap.put(key, comp);
		}
		
		public Component getComponent(String key){
			if (logger.isDebugEnabled())
				logger.debug("Retrieve component for key " + key);
			return componentMap.get(key);
		}
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
	private HelperPanel getSecondaryChoiceGridSupervisorSelectionDropdown(String componentId,
			String objectProperty, HierarchyEmployeeLinkDTO data){
		
		
			if(!getEditState().isViewOnly() && getEditState() != EditStateType.TERMINATE && data.getOid() != 0)				
			{
				return createSubTypeLabel(componentId, data);
			}
			if (existingMaintenanceRequest || getEditState().isViewOnly() ){				
					return createSubTypeLabel(componentId, data);
			}
			if(data.getHierarchyType() != null){
				if( (data.getHierarchyType().getOid() != 0) && data.getHierarchyType().getOid() != SRSType.ISSUPERVISEDBY ) {
						//create label with type and display
					data.setSubType(null);
					return createSubTypeLabel(componentId, data);
				}
			}
			
			if(data.getHierarchyType() == null)
			{
				data.setSubType(null);
				return createSubTypeLabel(componentId, data);
			}
		
		DropDownChoice dropdown = new DropDownChoice("value",new PropertyModel(data,objectProperty),getSubtypes(),new ChoiceRenderer("name", "oid"));
		
		dropdown.add(new AjaxFormComponentUpdatingBehavior("change"){
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				//update the value					
			}					
		});		
		dropdown.add(new AttributeModifier("style","width: 143px;"));
		dropdown.setRequired(true);
		//create dropdown of selectable types	
		dropdown.setLabel(new Model("Sub Type"));
		HelperPanel dropdownPanel = HelperPanel.getInstance(componentId, dropdown);	
		dropdownPanel.setOutputMarkupId(true);
		dropdownPanel.setOutputMarkupPlaceholderTag(true);
		return dropdownPanel;
	}

	private HelperPanel createSubTypeLabel(String componentId, HierarchyEmployeeLinkDTO data) {
		IDValueDTO type = null;
		if(data.getSubType() != null)
		{
			type = getSupervisorType(data.getSubType().getOid());
		}
		//IDValueDTO type = getSupervisorType(data.getSubType().getOid());
		HelperPanel panel = HelperPanel.getInstance(componentId, new Label("value",(type != null) ? type.getName(): ""));			
		panel.setOutputMarkupId(true);
		panel.setOutputMarkupPlaceholderTag(true);
		return panel;
	}

	/**
	 * get the servicing type for this typeid
	 * @param typeID
	 * @return
	 */
	public IDValueDTO getSupervisorType(long typeID){
		for(IDValueDTO type : getSubtypes()){
			if(type.getOid() == typeID){
				return type;
			}
		}
		return null;
	}
}
