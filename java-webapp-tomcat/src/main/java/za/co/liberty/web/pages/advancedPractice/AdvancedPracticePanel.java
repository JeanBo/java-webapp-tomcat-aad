package za.co.liberty.web.pages.advancedPractice;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.AttributeModifier;//import org.apache.wicket.behavior.SimpleAttributeModifier;
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
import org.apache.wicket.markup.html.form.validation.AbstractFormValidator;
import org.apache.wicket.markup.html.form.validation.IFormValidator;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import za.co.liberty.agreement.common.exceptions.LogicExecutionException;
import za.co.liberty.business.guicontrollers.advancedPractice.IAdvancedPracticeGUIController;
import za.co.liberty.common.domain.Percentage;
import za.co.liberty.dto.advancedPractice.AdvancedPracticeDTO;
import za.co.liberty.dto.advancedPractice.AdvancedPracticeDTOGrid;
import za.co.liberty.dto.advancedPractice.AdvancedPracticeManagerDTO;
import za.co.liberty.dto.advancedPractice.AdvancedPracticeMemberDTO;
import za.co.liberty.dto.agreement.AgreementRoleDTO;
import za.co.liberty.dto.contracting.ResultPartyDTO;
import za.co.liberty.dto.gui.context.ResultContextItemDTO;
import za.co.liberty.dto.persistence.party.flow.PartyRoleRolePlayerFLO;
import za.co.liberty.dto.userprofiles.ISessionUserProfile;
import za.co.liberty.exceptions.data.DataNotFoundException;
import za.co.liberty.exceptions.data.ValidationException;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.helpers.lookup.ServiceLocator;
import za.co.liberty.interfaces.advancedPractice.GridName;
import za.co.liberty.interfaces.agreements.RoleKindType;
import za.co.liberty.interfaces.agreements.requests.RequestKindType;
import za.co.liberty.interfaces.party.PartyRoleType;
import za.co.liberty.persistence.agreement.IAgreementEntityManager;
import za.co.liberty.persistence.party.IPartyEntityManager;
import za.co.liberty.srs.type.SRSType;
import za.co.liberty.web.data.enums.ComponentType;
import za.co.liberty.web.data.enums.ContextType;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.pages.advancedPractice.AdvRoleHistoryPage.HistoryType;
import za.co.liberty.web.pages.advancedPractice.model.AdvancedPracticePanelModel;
import za.co.liberty.web.pages.interfaces.ISecurityPanel;
import za.co.liberty.web.pages.panels.BasePanel;
import za.co.liberty.web.pages.panels.GUIFieldPanel;
import za.co.liberty.web.pages.panels.HelperPanel;
import za.co.liberty.web.pages.search.ContextSearchPopUp;
import za.co.liberty.web.system.SRSAuthWebSession;
import za.co.liberty.web.wicket.markup.html.form.SRSDateField;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGrid;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGridColumn;
import za.co.liberty.web.wicket.markup.html.grid.SRSGridRowSelectionCheckBox;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGrid.GridSizeUnit;
import za.co.liberty.web.wicket.markup.repeater.data.SortableListDataProvider;

import com.inmethod.grid.DataProviderAdapter;
import com.inmethod.grid.IGridColumn;
import com.inmethod.grid.SizeUnit;

/**
 * Advanced Practice details panel, capturing all Advanced Practicee details
 * 
 * @author MXM1904
 * 
 */

public class AdvancedPracticePanel extends BasePanel  implements ISecurityPanel{

	private static final long serialVersionUID = 1L;

	private  transient  IAgreementEntityManager agreementEntityManager = getAdvancedPracticeGUIController().getIAgreementEntityManager();	

	private  transient IPartyEntityManager partyEntityManager = getAdvancedPracticeGUIController().getIPartyEntityManager();
	
	private final SimpleDateFormat dteFormatter = new SimpleDateFormat("dd/MM/yyyy");	

	private transient IAdvancedPracticeGUIController advancedPracticeGUIController;
	
	public static final Logger logger = Logger.getLogger(AdvancedPracticePanel.class);
	
    private AdvancedPracticePanelModel panelModel;
	
	private boolean initialised;
	
	EditStateType editState;

	
	private PracticeForm practiceForm;
	
	private ModalWindow mangersHistoryWindow;
	
	private ModalWindow membersHistoryWindow;	
	
	private ModalWindow searchManagerWindow;
	
	private ModalWindow searchManagerMemberWindow;
	
	private WebMarkupContainer statusName;
	
	private ModalWindow searchMemberWindow;
	
	private HistoryType currentHistoryType = HistoryType.MANAGER;

	private SRSDataGrid managersGrid;
	
	private SRSDataGrid membersGrid;
	
	private Button addManagerButton;
	
	private Button managerHistoryButton;
	
	private Button membersHistoryButton;
	
	private Button addMemberButton;

	private HelperPanel typePanel;
	
	private HelperPanel status_Panel;

	private FeedbackPanel feedBackPanel;
	
	private AdvancedPracticeManagerDTO practiceManagerDTO;
	
	private AdvancedPracticeMemberDTO practiceMemberDTO;
	
	private AdvancedPracticeManagerDTO beforeImageManagerDTO;
	
	private AdvancedPracticeManagerDTO currentImageManagerDTO;

	private AdvancedPracticeDTOGrid currentworkingGridManager;
	
	private AdvancedPracticeDTOGrid currentworkingGridMember;
	
	private AdvancedPracticeDTOGrid currentworkingGrid;

	private ModalWindow memberPopup;	

	EditStateType[] editableEditstates = new EditStateType[]{EditStateType.MODIFY,EditStateType.ADD};
	
	private List<FormComponent> validationComponents = new ArrayList<FormComponent>();
	
	boolean enableComponent = true;

	/**
	 * booleans set to true if there are existing requests that still need to be
	 * authorised, screen must then be disabled
	 */
	private boolean hasOutstandingRequest;
	
	
	private transient IAdvancedPracticeGUIController advGUIController = getAdvancedPracticeGUIController();

	/**
	 * @param arg0
	 */
		
	public AdvancedPracticePanel(String id,
			 final AdvancedPracticePanelModel panelModel,
			EditStateType editState, 
			Page parentPage) {
		super(id, editState,parentPage);
		List<RequestKindType> unAuthRequests = getOutStandingRequestKinds();
		hasOutstandingRequest = (unAuthRequests.size()>0);
		if (logger.isDebugEnabled())
			logger.debug("There are outstanding requests :" + unAuthRequests);
		panelModel.setExistingMaintenanceRequest(hasOutstandingRequest);

		this.panelModel = panelModel;
		
	}
	
	/**
	 * Load the components on the page on first render, 
	 * so that the components are only generated when the page is displayed 
	 */
	@Override
	protected void onBeforeRender() {
		super.onBeforeRender();
		getEditState();
		if (!initialised) {
			if (logger.isDebugEnabled()) {
				logger.debug("Adding components to the page on first render");
				
			}
			initGridRoles(panelModel);
			
			add(getPracticeForm());
			add(mangersHistoryWindow=getMangerMemberHistoryWindow("mangersHistoryWindow",HistoryType.MANAGER));	
			add(membersHistoryWindow=getMangerMemberHistoryWindow("membersHistoryWindow",HistoryType.MEMBER));	
			add(searchManagerWindow=getSearchManagerMemberWindow("searchManagerWindow",GridName.MANAGER));
			add(searchMemberWindow=getSearchManagerMemberWindow("searchMemberWindow",GridName.MEMBER));
			initialised=true;

		}
		
	}
	
	/**
	 * Get the main page form
	 * @return
	 */
	private PracticeForm getPracticeForm() {
		if (practiceForm==null) {
			practiceForm = new PracticeForm("practiceForm");
		}
		return practiceForm;
	}
	

	@Override
	protected boolean isView(EditStateType[] editableStates) {
		// will disable any modification if there are any requests pending auth
		if (hasOutstandingRequest) {
			return true;
		}
		return super.isView(editableStates);
	}

	@Override
	public EditStateType getEditState() {
		//will disable any modification if there are any requests pending auth
		if(hasOutstandingRequest){
			return EditStateType.VIEW;
		}
		return super.getEditState();
	}
   
	/**
	 * Form used for the panel so we can add validations and on submit method
	 * calls
	 * 
	 * @author MXM1904
	 * 
	 */
	public class PracticeForm extends Form {
		private static final long serialVersionUID = 1L;

		public PracticeForm(String id) {
			super(id);	
			RepeatingView namePanel 	= new RepeatingView("namePanel");
			RepeatingView datePanel 	= new RepeatingView("datePanel");
			RepeatingView statusPanel	= new RepeatingView("statusPanel");
			
			//check for outstanding requests requiring authorisation

			boolean canRaiseMaintain = true;
			ISessionUserProfile loggedInUser = SRSAuthWebSession.get().getSessionUser();			
			canRaiseMaintain = loggedInUser.isAllowRaise(RequestKindType.MaintainAdvancedPractice);
			
			this.add(namePanel);
			this.add(datePanel); 
			this.add(statusPanel); 
			

			this.add(namePanel.add(createPracticeNameField()));
			this.add(datePanel.add(createStartDateField()));
			
			this.add(statusPanel.add(createStatusField()));

			
			this.add(managersGrid = createManagerMemberGrid("managerGrid", panelModel,GridName.MANAGER));
			this.add(membersGrid = createManagerMemberGrid("membersGrid", panelModel,GridName.MEMBER));
			this.add(createRemoveManagerMemberButton("removeManagerButton",GridName.MANAGER));
			this.add(createRemoveManagerMemberButton("removeMemberButton",GridName.MEMBER));
			
			this.add(managerHistoryButton = createMangerMemberHistoryButton("managerHistoryButton",GridName.MANAGER));
			this.add(membersHistoryButton = createMangerMemberHistoryButton("membersHistoryButton",GridName.MEMBER));
			
			this.add(addManagerButton = createAddManagerMemberButton("addManagerButton",managersGrid,GridName.MANAGER));
			this.add(addMemberButton = createAddManagerMemberButton("addMemberButton",membersGrid,GridName.MEMBER));
	
			this.add(new AbstractFormValidator() {
				
				@Override
				public void validate(Form<?> arg0) {
					if (getEditState().isViewOnly()) {
						if (logger.isDebugEnabled())
							logger.debug("Validating advanced practice form - view only so ignore validation.");
						return;
					}
					
					if (logger.isDebugEnabled())
						logger.debug("Validating advanced practice form");
					
//					try {
//						throw new RuntimeException();
//					} catch (RuntimeException e) {
//						logger.debug("Validate", e);
//					}
					
					boolean validate = true;
					for (FormComponent comp : validationComponents) {
						if (!comp.isValid()) {
							if (logger.isDebugEnabled())
								logger.debug("Validation errors occurred on this form - " + comp);
							validate = false;
						}
					}

//					if (!validate) {
//						PracticeForm.this.error("Validation errors occurred on this form");
//					}
//					
					if (validate) {												
						try {
							List<AgreementRoleDTO> currentAndFutureRoles =  new ArrayList<AgreementRoleDTO>();						
					
							List<AgreementRoleDTO> pastRoles = new ArrayList<AgreementRoleDTO>();

							setcurrentAndFutureRoles( panelModel,  currentAndFutureRoles, pastRoles);
							
							//outstandingRequest(hasOutstandingRequest);   
							
							getAdvancedPracticeGUIController().validateAgreementRoles(panelModel.getAdvancedPracticeDTO(),currentAndFutureRoles, pastRoles);
						
						} catch (ValidationException e) {
							for(String error : e.getErrorMessages()){
								PracticeForm.this.error(error);	
							}
						}
					}
				}
				
				@Override
				public FormComponent<?>[] getDependentFormComponents() {
//					Jean is busy here.
//					We need to confirm that the validation is actually called
					return validationComponents.toArray(new FormComponent[] {});
					
				}
			});
			
			
//			add(new IFormValidator() {
//
//				private static final long serialVersionUID = 1L;
//
//				
//
//				public void validate(final Form form) {
//				
//	
//				}
//
//			});
		}
		
		/**
		 * Returns true if edit state is in one of the view states
		 * 
		 * @return
		 */
		private boolean isViewOnly() {
			return 
				getEditState().isViewOnly();
		}	
		
		/**
		 * Returns true if edit state is in one of the view states
		 * 
		 * @return
		 */
		private boolean isEditState() {
			return 
			!getEditState().isViewOnly();
		}	
	}

	
	/**
	 * Create the Practice Name field
	 * @return
	 */
	private GUIFieldPanel createPracticeNameField(){

	GUIFieldPanel practiceNamePanel = createGUIFieldPanel("Practice Name","Practice Name","Practice Name",
			createPageField(panelModel.getAdvancedPracticeDTO(),"Practice Name","panel","businessName",ComponentType.TEXTFIELD, true,true,new EditStateType[] {EditStateType.ADD}),1);
	if(((HelperPanel)practiceNamePanel.getComponent()).getEnclosedObject() instanceof FormComponent){
		validationComponents.add((FormComponent)((HelperPanel)practiceNamePanel.getComponent()).getEnclosedObject());
	}
	return (practiceNamePanel);
	}

	/**
	 * Create the start date field
	 * @return
	 */
	private GUIFieldPanel createStartDateField(){
		GUIFieldPanel startDatePanel = createGUIFieldPanel("Start Date","Start Date","Start Date",
				createPageField(panelModel.getAdvancedPracticeDTO(),"Start Date","panel","effectiveFrom",
						ComponentType.DATE_SELECTION_TEXTFIELD, true,true,new EditStateType[] {EditStateType.ADD})
				,2);
		if(((HelperPanel)startDatePanel.getComponent()).getEnclosedObject() instanceof FormComponent){
			validationComponents.add((FormComponent)((HelperPanel)startDatePanel.getComponent()).getEnclosedObject());
		}
		if(((HelperPanel)startDatePanel.getComponent()).getEnclosedObject() instanceof SRSDateField){
			SRSDateField f = (SRSDateField) ((HelperPanel)startDatePanel.getComponent()).getEnclosedObject();
			f.add(f.newDatePicker());
		}
		return (startDatePanel);
	}
	/**
	 * Create the end date field
	 * @return
	 */
	private GUIFieldPanel createStatusField(){
		
		GUIFieldPanel statusPanel = createGUIFieldPanel("Status ","Status ","Status",
				createDropdownField(panelModel.getAdvancedPracticeDTO(),"Status","panel","status",
						AdvancedPracticeStatusType.getPartyStatusDBEnums(),new ChoiceRenderer("name","key"),"Select one",true,true,editableEditstates),4);
		if(((HelperPanel)statusPanel.getComponent()).getEnclosedObject() instanceof FormComponent){
			FormComponent statusComp = (FormComponent)((HelperPanel)statusPanel.getComponent()).getEnclosedObject();	
			if(statusComp instanceof DropDownChoice){
				statusComp.add(new AttributeModifier("style","width: 200px;"));//statusComp.add(new SimpleAttributeModifier("style","width: 200px;"));
			}
			validationComponents.add(statusComp);
		}
				
	return (statusPanel);
	}
	
	
	/**
	 * Create a grid for the manager nodes
	 * 
	 * @return 
	 */
	private SRSDataGrid createManagerMemberGrid(String id,AdvancedPracticePanelModel panelModel , final GridName gridType) {
		
		//Managers Grids Data
		logger.info("About to print gridRoles manager");
		List<AdvancedPracticeDTOGrid> managersMembersDTO = panelModel.getManagersGrids();
		if(gridType != null && gridType.equals(GridName.MANAGER)){
			managersMembersDTO = panelModel.getManagersGrids();
		}else{
			 managersMembersDTO = panelModel.getMembersGrid();
		}

		SRSDataGrid grid = new SRSDataGrid(id, new DataProviderAdapter(new SortableListDataProvider<AdvancedPracticeDTOGrid>(managersMembersDTO)), getManagerMemberColumns(gridType),null);            
        grid.setCleanSelectionOnPageChange(false);
        grid.setClickRowToSelect(false);        
        //grid.setContentHeight(100, SizeUnit.PX);
        grid.setAllowSelectMultiple(true);
        grid.setGridWidth(100, GridSizeUnit.PERCENTAGE);
        grid.setRowsPerPage(10);
        grid.setContentHeight(150, SizeUnit.PX);
        if(gridType != null && gridType.equals(GridName.MANAGER)){
        	grid.setContentHeight(100, SizeUnit.PX);
        }
        
        return grid;
	}

	
	/**
	 * Create the add manager button
	 * @return
	 */  
	private Button createAddManagerMemberButton(String id, final SRSDataGrid grid, final GridName butKind) {
		final Button button = new Button(id);
		if(this.getEditState().isViewOnly()){
			button.setVisible(false); 
		}
		button.add(new AjaxFormComponentUpdatingBehavior("click") {
			private static final long serialVersionUID = 0L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {				
				
				
				if ( panelModel != null ) {		
					AdvancedPracticeMemberDTO member = new AdvancedPracticeMemberDTO();
					ResultPartyDTO rolePlayerReference = new ResultPartyDTO();
					AdvancedPracticeDTOGrid practiceDTOGrid = new  AdvancedPracticeDTOGrid();
					Date date = new Date();	
					member.setRoleID(0L);
					member.setAgreementRoleKind((long)RoleKindType.ISADVANCEDPRACTICEMEMBEROF.getKind());
					member.setKind((long)RoleKindType.ISADVANCEDPRACTICEMEMBEROF.getKind());
					if(butKind!= null && butKind == GridName.MANAGER){						
						member = new AdvancedPracticeManagerDTO();
						member.setAgreementRoleKind((long)RoleKindType.ISADVANCEDPRACTICEMANAGEROF.getKind());
						member.setKind((long)RoleKindType.ISADVANCEDPRACTICEMANAGEROF.getKind());
						Percentage percentage = new Percentage();
						percentage.setValue(BigDecimal.ZERO);
						((AdvancedPracticeManagerDTO)member).setPracticeShare(percentage);		
					}
					member.setType(SRSType.PARTYAGREEMENTROLE);
					member.setEffectiveFrom(date);
					member.setRolePlayerReference(rolePlayerReference);
					member.setAgreementNumber(0L);
					practiceDTOGrid.setRole(member);	
					practiceDTOGrid.setAgreementParty(new ResultPartyDTO());
					panelModel.addGridRole(practiceDTOGrid);						
					target.add(grid);//target.addComponent(grid);
				} else {
					error("Not more than four managers can be added");
					if (AdvancedPracticePanel.this.feedBackPanel != null) {
						target.add(AdvancedPracticePanel.this.feedBackPanel);//target.addComponent(AdvancedPracticePanel.this.feedBackPanel);
					}
				}
			}
		});		
	if(getEditState() == EditStateType.AUTHORISE){
		button.setVisible(false);
	}else if(getEditState().isViewOnly() || getEditState() ==  EditStateType.TERMINATE) {
		button.setEnabled(false);
	}
	button.setOutputMarkupId(true);
	return button;
	}

	/**
	 * Create the remove manager button
	 * @return
	 */  
	private Button createRemoveManagerMemberButton(String id, final GridName butKind){
		Button button = new Button(id);
		if(this.getEditState().isViewOnly()){
			button.setVisible(false); 
		}
		button.add(new AjaxFormComponentUpdatingBehavior("click"){
			private static final long serialVersionUID = 1L;
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				
				if(butKind != null && butKind.equals(GridName.MANAGER)){
					List<Object> managers = managersGrid.getSelectedItemObjects();
					for (Object manager : managers) {
						//getAdvancedPracticeManagerDTOlist
						
						panelModel.removeGridRole((AdvancedPracticeDTOGrid)manager);
						
					}
					target.add(managersGrid);//target.addComponent(managersGrid);	
				} else if(butKind != null && butKind.equals(GridName.MEMBER)){
					List<Object> members = membersGrid.getSelectedItemObjects();
					for (Object member : members) {
						//getAdvancedPracticeManagerDTOlist
						
						panelModel.removeGridRole((AdvancedPracticeDTOGrid)member);
						
					}
					target.add(membersGrid);//target.addComponent(membersGrid);
				}
	
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
	 * Create the button to show the manager history for this pratice
	 * 
	 * @return
	 */
	private Button createMangerMemberHistoryButton(String id, final GridName historyKind) {
		final Button button = new Button(id);
		
		button.add(new AjaxFormComponentUpdatingBehavior("click") {
			private static final long serialVersionUID = 0L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				if(historyKind != null && historyKind.equals(GridName.MANAGER)){
					currentHistoryType = HistoryType.MANAGER;
					mangersHistoryWindow.show(target);
				}else{
					currentHistoryType = HistoryType.MEMBER;
					membersHistoryWindow.show(target);
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
	 * Create the history popup
	 * @param id
	 * @return
	 */
	private ModalWindow getMangerMemberHistoryWindow(String id, final HistoryType type) {
		final ModalWindow window = new ModalWindow(id);
		window.setTitle("Roles History");		
		// Create the page
		window.setPageCreator(new ModalWindow.PageCreator() {
			private static final long serialVersionUID = 10L;
			public Page createPage() {	
				return new AdvRoleHistoryPage(window,panelModel,type); 
				
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
		window.setCookieName("partyRoleSearchPageMap");//window.setPageMapName("partyRoleSearchPageMap");
		return window;
	}
	
	HelperPanel ispracticeShare;
	/**
	 * Get the list of node columns for the grid
	 * 
	 * @return
	 */ 
	private List<IGridColumn> getManagerMemberColumns(final GridName columType) {
		Vector<IGridColumn> cols = new Vector<IGridColumn>();
		if (!getEditState().isViewOnly() && getEditState() != EditStateType.TERMINATE) {
			SRSGridRowSelectionCheckBox col = new SRSGridRowSelectionCheckBox("checkBox");
			cols.add(col.setInitialSize(30));
		}

//		add in the name column(Display only col)

		cols.add(new SRSDataGridColumn<AdvancedPracticeDTOGrid>("agreementParty.name",
				new Model("Name"), "agreementParty.name", "agreementParty.name", getEditState() ).setInitialSize(230));
		cols.add(new SRSDataGridColumn<AdvancedPracticeDTOGrid>("role.agreementNumber",
				new Model("Agreement Number"), "role.agreementNumber", "role.agreementNumber", getEditState()).setInitialSize(111));	

		
//		add search button, don't display this column on view
		if( !getEditState().isViewOnly() ){ 
			cols.add(new SRSDataGridColumn<AdvancedPracticeDTOGrid>("managerAgm",
					new Model("Search"), "agreementParty", getEditState()){	
				
						private static final long serialVersionUID = 1L;							

						@Override
						public Panel newCellPanel(WebMarkupContainer parent, String componentId, IModel rowModel, String objectProperty, EditStateType state, final AdvancedPracticeDTOGrid data) {
							if(data != null && data.getRole().getRoleID() == 0){
								Button searchButton = new Button("value", new Model("Search"));	
								searchButton.add(new AjaxFormComponentUpdatingBehavior("click"){									
									private static final long serialVersionUID = 1L;
									@Override
									protected void onUpdate(AjaxRequestTarget target) {
										currentworkingGrid=data;										
										
										if(columType != null && columType.equals(GridName.MANAGER)){
											searchManagerWindow.show(target);
										}else{
											searchMemberWindow.show(target);
										}
									}									
								});
								return HelperPanel.getInstance(componentId,searchButton);	
							}else{
								
								return new EmptyPanel(componentId);
							}
						}				
				
			}.setInitialSize(67));
		}	
		
		if(columType != null && columType.equals(GridName.MANAGER)){
			
		cols.add(new SRSDataGridColumn<AdvancedPracticeDTOGrid>(
				"practiceSharePercentage", new Model("Practice Share"),
				"practiceSharePercentage", "practiceSharePercentage",getEditState()) 

				{
			private static final long serialVersionUID = 1L;

			@Override
			public Panel newCellPanel(WebMarkupContainer parent,
					String componentId, IModel rowModel, String objectProperty,
					EditStateType state, AdvancedPracticeDTOGrid data) {


				if (getEditState().isViewOnly() || getEditState() ==  EditStateType.TERMINATE) {
					Label lbl = new Label("value",new PropertyModel(data,objectProperty)) {

					};
					return HelperPanel.getInstance(componentId, lbl);
					
				}
				

				TextField  practiceShare = new TextField("value",
						new PropertyModel(data, objectProperty));
				System.out.println(((AdvancedPracticeManagerDTO)data.getRole()).getPracticeShare());

				practiceShare.setEnabled(true);
				practiceShare.add(new AttributeModifier("maxlength", "10"));//practiceShare.add(new SimpleAttributeModifier("maxlength", "10"));
				practiceShare.add(new AttributeModifier("style","width: 50px;"));//practiceShare.add(new SimpleAttributeModifier("style","width: 50px;"));
				practiceShare.add(new AjaxFormComponentUpdatingBehavior("change") {
					@Override
					protected void onUpdate(AjaxRequestTarget target) {
						//do nothing just want the object value updated
					}
				});
				practiceShare.add(new AjaxFormComponentUpdatingBehavior("keyup") {
					@Override
					protected void onUpdate(AjaxRequestTarget target) {
						//do nothing just want the object value updated
					}
				});
				//HelperPanel helperPanel = HelperPanel.getInstance(componentId, lbl);
				return HelperPanel.getInstance(componentId, practiceShare);
				

			}
		}.setInitialSize(60));
			
		
	}
		
		cols.add(new SRSDataGridColumn<AdvancedPracticeDTOGrid>(
				"role.effectiveFrom", new Model("Start Date"),
				"role.effectiveFrom", "role.effectiveFrom", getEditState()) {
			@Override
			public Panel newCellPanel(WebMarkupContainer parent,
					String componentId, IModel rowModel,
					String objectProperty, EditStateType state,
					AdvancedPracticeDTOGrid data) {
				if ((getEditState().isViewOnly() || getEditState() ==  EditStateType.TERMINATE || getEditState() ==  EditStateType.MODIFY ) && (!(data.getRole().getAgreementNumber()==0) || getEditState() ==  EditStateType.AUTHORISE)) {
					return super.newCellPanel(parent, componentId,
							rowModel, objectProperty, state, data);
				}
				TextField startDate = new TextField("value",
						new PropertyModel(data, objectProperty));
				startDate.add(new AttributeModifier("size", "11"));//startDate.add(new SimpleAttributeModifier("size", "11"));
				startDate.add(new AttributeModifier("maxlength", "10"));//startDate.add(new SimpleAttributeModifier("maxlength", "10"));
				startDate.setLabel(new Model("Manager Start Date"));
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
				return HelperPanel.getInstance(componentId, startDate, true);
			}
		}.setInitialSize(95));
			
		cols.add(new SRSDataGridColumn<AdvancedPracticeDTOGrid>(
				"role.effectiveTo", new Model("End Date"),
				"role.effectiveTo", "role.effectiveTo", getEditState()) {  
			@Override
			public Panel newCellPanel(WebMarkupContainer parent,
					String componentId, IModel rowModel,
					String objectProperty, EditStateType state,
					AdvancedPracticeDTOGrid data) {
//				if (getEditState().isViewOnly() || getEditState() ==  EditStateType.TERMINATE) {
//					return super.newCellPanel(parent, componentId,
//							rowModel, objectProperty, state, data);
//				}
				if ((getEditState().isViewOnly() || getEditState() ==  EditStateType.TERMINATE ||(data.getRole().getAgreementNumber()==0)) )  {
					return super.newCellPanel(parent, componentId,
							rowModel, objectProperty, state, data);
				}
				
				
				TextField endDate = new TextField("value",
						new PropertyModel(data, objectProperty));
				endDate.add(new AttributeModifier("size", "11"));//endDate.add(new SimpleAttributeModifier("size", "11"));
				endDate.add(new AttributeModifier("maxlength", "10"));//endDate.add(new SimpleAttributeModifier("maxlength", "10"));
				endDate.setLabel(new Model("Manager End Date"));
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
				return HelperPanel.getInstance(componentId, endDate, true);
			}
		}.setInitialSize(95));
		
		
		

		return cols;		
}



	public Class getPanelClass() {
		return AdvancedPracticePanel.class;
	}

	/**
	 * Set the feedback panel to use for errors
	 * @param feedBackPanel
	 */
	public void setFeedBackPanel(FeedbackPanel feedBackPanel) {
		this.feedBackPanel = feedBackPanel;
	}

	/**
	 * Get the AdvancedPracticeGUIController bean
	 * 
	 * @return
	 */
	private IAdvancedPracticeGUIController getAdvancedPracticeGUIController() {
		if (advancedPracticeGUIController == null) {
			try {
				advancedPracticeGUIController = ServiceLocator
						.lookupService(IAdvancedPracticeGUIController.class);
			} catch (NamingException e) {
				throw new CommunicationException(e);
			}
		}
		return advancedPracticeGUIController;
	}

	


	/**
	 * Process the address popup window once closed
	 *
	 */
	private void processManagerPopupClosed(AjaxRequestTarget target){

		target.add(managersGrid);//target.addComponent(managersGrid);
}	
	
	
	
	
	/**
	 * Create a search popup
	 * @param id
	 * @return
	 */
	private ModalWindow getSearchManagerMemberWindow(String id, final GridName gridType) {

		ContextSearchPopUp popUp = new ContextSearchPopUp() {

			private static final long serialVersionUID = 1L;

			@Override
			public ContextType getContextType() {
				return ContextType.AGREEMENT_ONLY;	
			}

			@Override
			public void doProcessSelectedItems(AjaxRequestTarget target,
					ArrayList<ResultContextItemDTO> selectedItemList) {
				
				if(selectedItemList.size() > 0){
					ResultContextItemDTO item = selectedItemList.get(0);
					
					if(currentworkingGrid != null){
						AdvancedPracticeMemberDTO role = (AdvancedPracticeMemberDTO) currentworkingGrid.getRole();
			
						if(gridType != null && gridType.equals(GridName.MANAGER)){
							role = null; 
							role = (AdvancedPracticeManagerDTO) currentworkingGrid.getRole();							
							role.setKind(new Long(RoleKindType.ISADVANCEDPRACTICEMANAGEROF.getKind()));
							role.setAgreementRoleKind(new Long(RoleKindType.ISADVANCEDPRACTICEMANAGEROF.getKind()));
							target.add(managersGrid);//target.addComponent(managersGrid);
						}else if(gridType != null && gridType.equals(GridName.MEMBER)){
							role.setKind(new Long(RoleKindType.ISADVANCEDPRACTICEMEMBEROF.getKind()));
							role.setAgreementRoleKind(new Long(RoleKindType.ISADVANCEDPRACTICEMEMBEROF.getKind()));
						}
						role.setAgreementNumber(item.getAgreementDTO().getAgreementNumber());
						role.setRolePlayerReference(item.getPartyDTO());
						role.setType(SRSType.PARTYAGREEMENTROLE);	
	
						currentworkingGrid.setAgreementParty(item != null ? item.getPartyDTO() : new ResultPartyDTO());
	
					}
					target.add(membersGrid);//target.addComponent(membersGrid);
		
				}
			}
		};		
		ModalWindow win = popUp.createModalWindow(id);
		win.setCookieName("partyRoleSearchPageMap");//win.setPageMapName("partyRoleSearchPageMap");
		return win;	
	} 
	

	/**
	 * Run through the agreement roles and set up the grid data
	 *
	 */
	private void initGridRoles(AdvancedPracticePanelModel panelModel){
		//first see, maybe the grid roles data has already been set up on the panelModel
		if(panelModel != null && (panelModel.getManagersGrids() == null || panelModel.getManagersGrids().size() == 0)){			

			if(panelModel.getAdvancedPracticeDTO() != null ){
				
				                                                
				List<AdvancedPracticeManagerDTO> managerRoles = panelModel.getAdvancedPracticeDTO().getAdvancedPracticeManagerDTOlist();
				List<AdvancedPracticeMemberDTO>  membersRoles = panelModel.getAdvancedPracticeDTO().getAdvancedPracticeMemberDTOList();

					List<AdvancedPracticeDTOGrid> managerGridRoles = new ArrayList<AdvancedPracticeDTOGrid>();
					List<AdvancedPracticeDTOGrid> memberGridRoles = new ArrayList<AdvancedPracticeDTOGrid>();

					for(AdvancedPracticeManagerDTO managerRole : managerRoles){
						AdvancedPracticeDTOGrid gridRole = new AdvancedPracticeDTOGrid();
						gridRole.setRole(managerRole);
						try {
							advGUIController.setUpGridRoleData(gridRole);
						} catch (LogicExecutionException e) {
							logger.error("An error occured while setting up grid data",e);
						}
						managerGridRoles.add(gridRole);
					}
					for(AdvancedPracticeMemberDTO memberRole : membersRoles){
						AdvancedPracticeDTOGrid gridRole = new AdvancedPracticeDTOGrid();
						gridRole.setRole(memberRole);
						try {
							advGUIController.setUpGridRoleData(gridRole);
						} catch (LogicExecutionException e) {
							logger.error("An error occured while setting up grid data",e);
						}
						memberGridRoles.add(gridRole);
					}
					
					panelModel.setManagersGrids(managerGridRoles);
					panelModel.setMembersGrid(memberGridRoles);

					/**
					 * Setup history grid roles
					 */
					try {
						setUpManagermemberHistory(panelModel);
					} catch (DataNotFoundException e) {
						logger.error("An error occured while setting up history grid data",e);
					}

				}
			}	
		}
	
	
	/**
	 * Set history for the manager and member grids
	 * @param panelModel
	 * @throws DataNotFoundException 
	 */
	private void setUpManagermemberHistory(AdvancedPracticePanelModel panelModel) throws DataNotFoundException{
		
		AdvancedPracticeDTO practiceDTO = panelModel.getAdvancedPracticeDTO();
		
		if(practiceDTO != null && practiceDTO.getOid() > 1){
			List<PartyRoleRolePlayerFLO> managerHisRoles = advancedPracticeGUIController.getNonActivePartyRolesForPractice(practiceDTO.getOid(), PartyRoleType.HASADVANCEDPRACTICEMANAGER.getType());
			List<PartyRoleRolePlayerFLO> membersHisRoles = advancedPracticeGUIController.getNonActivePartyRolesForPractice(practiceDTO.getOid(), PartyRoleType.HASADVANCEDPRACTICEMEMBER.getType());

			List<AdvancedPracticeDTOGrid> managerHistoryGridRoles = new ArrayList<AdvancedPracticeDTOGrid>();
			List<AdvancedPracticeDTOGrid> memberHistoryGridRoles = new ArrayList<AdvancedPracticeDTOGrid>();
			
			List<Long> agmts = new ArrayList<Long>();
			for(PartyRoleRolePlayerFLO agmtNo : managerHisRoles){						
				agmts.add(agmtNo.getContextId());
			}
			
			List<AdvancedPracticeManagerDTO> managerAgmtRolesHistory = advancedPracticeGUIController.getNonActiveManagerRoles(agmts);
			
			agmts.clear();
			for(PartyRoleRolePlayerFLO agmtNo : membersHisRoles){						
				agmts.add(agmtNo.getContextId());
			}
			List<AdvancedPracticeMemberDTO> memberAgmtRolesHistory = advancedPracticeGUIController.getNonActiveMemberRoles(agmts);
			
			for(AdvancedPracticeManagerDTO managerRole : managerAgmtRolesHistory){
				AdvancedPracticeDTOGrid gridRole = new AdvancedPracticeDTOGrid();
				gridRole.setRole(managerRole);
				try {
					advGUIController.setUpGridRoleData(gridRole);
				} catch (LogicExecutionException e) {
					logger.error("An error occured while setting up history grid data",e);
				}
				managerHistoryGridRoles.add(gridRole);
			}
			for(AdvancedPracticeMemberDTO memberRole : memberAgmtRolesHistory){
				AdvancedPracticeDTOGrid gridRole = new AdvancedPracticeDTOGrid();
				gridRole.setRole(memberRole);
				try {
					advGUIController.setUpGridRoleData(gridRole);
				} catch (LogicExecutionException e) {
					logger.error("An error occured while setting up history grid data",e);
				}
				memberHistoryGridRoles.add(gridRole);
			}

			panelModel.setManagersHistoryGrids(managerHistoryGridRoles);
			panelModel.setMembersHistoryGrids(memberHistoryGridRoles);
		}
	}

	
	/**
	 * Set the current anad the past role list 
	 * @param panelModel
	 * @param currentAndFutureRoles
	 * @param pastRoles
	 */
	
	private void setcurrentAndFutureRoles(AdvancedPracticePanelModel panelModel, List<AgreementRoleDTO> currentAndFutureRoles, List<AgreementRoleDTO> pastRoles){
									
		if(panelModel != null && panelModel.getManagersGrids() != null ){
			
			for(AdvancedPracticeDTOGrid gridRole : panelModel.getManagersGrids()){
				currentAndFutureRoles.add(gridRole.getRole());									
			}
		} 
		if(panelModel != null && panelModel.getMembersGrid() != null ){
			
			for(AdvancedPracticeDTOGrid gridRole : panelModel.getMembersGrid()){
				currentAndFutureRoles.add(gridRole.getRole());									
			}
		}
		
		
		
		
		
		if(panelModel != null && panelModel.getPastRoles() != null ){
			
			for(AdvancedPracticeManagerDTO gridRole :  (panelModel.getPastRoles() !=  null ?panelModel.getPastRoles().getAdvancedPracticeManagerDTOlist(): panelModel.getPastRoles().getAdvancedPracticeManagerDTOlist())){
				pastRoles.add(gridRole);									
			}
			
			for(AdvancedPracticeMemberDTO gridRole : (panelModel.getPastRoles() != null ?panelModel.getPastRoles().getAdvancedPracticeMemberDTOList() : null)){
				pastRoles.add(gridRole);									
			}
		}else {
			pastRoles = new ArrayList<AgreementRoleDTO>(0);
		}
	}
	
	
}
