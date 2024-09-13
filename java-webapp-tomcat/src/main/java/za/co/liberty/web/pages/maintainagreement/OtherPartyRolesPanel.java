/**
 * 
 */
package za.co.liberty.web.pages.maintainagreement;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import javax.naming.NamingException;

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
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import com.inmethod.grid.DataProviderAdapter;
import com.inmethod.grid.IGridColumn;
import com.inmethod.grid.SizeUnit;

import za.co.liberty.business.guicontrollers.agreement.IAgreementGUIController;
import za.co.liberty.business.party.IPartyManagement;
import za.co.liberty.business.party.validator.IPartyValidator;
import za.co.liberty.common.domain.TypeVO;
import za.co.liberty.dto.agreement.AgreementDTO;
import za.co.liberty.dto.agreement.AgreementRoleDTO;
import za.co.liberty.dto.agreement.maintainagreement.AgreementRoleGridDTO;
import za.co.liberty.dto.agreement.maintainagreement.MaintainAgreementDTO;
import za.co.liberty.dto.contracting.ResultAgreementDTO;
import za.co.liberty.dto.contracting.ResultPartyDTO;
import za.co.liberty.dto.gui.context.ResultContextItemDTO;
import za.co.liberty.dto.userprofiles.ContextDTO;
import za.co.liberty.dto.userprofiles.ISessionUserProfile;
import za.co.liberty.exceptions.data.DataNotFoundException;
import za.co.liberty.exceptions.data.ValidationException;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.helpers.lookup.ServiceLocator;
import za.co.liberty.helpers.util.DateUtil;
import za.co.liberty.interfaces.agreements.RoleKindType;
import za.co.liberty.interfaces.agreements.requests.RequestKindType;
import za.co.liberty.persistence.party.entity.fastlane.PartyProfileFLO;
import za.co.liberty.srs.type.SRSType;
import za.co.liberty.web.data.enums.ContextType;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.data.enums.PanelToRequestMapping;
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
 * @author AAA1210
 *
 */
public class OtherPartyRolesPanel  extends BasePanel {
	
	private static final long serialVersionUID = 1L;
	
	
	private List<AgreementRoleGridDTO> agreementOtherPartyRoles;
	private MaintainAgreementPageModel pageModel; 	
	private FeedbackPanel feedBackPanel;
	private ModalWindow searchWindow;
	private AgreementRoleGridDTO currentSearchOtherPartyRole;
	private Page parentPage;
	private EditStateType editState;
	private long agreementNo;
	
	private boolean existingSupervisionRequest;	
	
	private boolean existingHierarchyRequest;
	
	private SRSDataGrid agreementOtherPartyRolesGrid;
	
	private transient IAgreementGUIController guiController;
	
	private transient IPartyManagement partyManagement;
	
	private HistoryPageType historyType;
	
	private ModalWindow historyWindow;
	
	boolean showRoleColumns;
	
	private HashMap<AgreementRoleGridDTO, AgreementGridData> gridData = new HashMap<AgreementRoleGridDTO, AgreementGridData>();

	private long typeOID;

	protected List<AgreementRoleDTO> currentAndFutureOtherPartyRoles;
	
	private boolean initialised;
	
	private ModalWindow businessOwnerValidationPopUp ;
	
	private ResultPartyDTO resultPartyDto;
	
	private IPartyValidator partyValidator;
	
	private String validationErrorMessage;

	private transient Logger logger = Logger.getLogger(this.getClass());
	
	/**
	 * Constructor for Agreement Hierarchy Panel
	 * @param id
	 * @param editState
	 * @param parentPage
	 * @param model
	 * @param showRoleColumns
	 */
	public OtherPartyRolesPanel(String id, EditStateType editState, Page parentPage,MaintainAgreementPageModel model, boolean showRoleColumns ) {
		super(id ,editState,parentPage  );
		this.editState = editState;
		this.parentPage = parentPage;
		// TODO Auto-generated constructor stub
		this.pageModel = model;
		this.feedBackPanel = feedBackPanel;
		this.showRoleColumns= showRoleColumns;
		initGridRoles();		
//		this.add(searchWindow = createSearchWindow("searchPartyWindow"));
//		this.add(new OtherPartyRolesForm("otherPartyRolesForm"));
	}
	/**
	 * Constructor for Fais Licence Panel.
	 * @param id
	 * @param editState
	 * @param currentAndFutureOtherPartyRoles
	 * @param typeOID
	 * @param showRoleColumns
	 * @param agreementNo
	 */
	public OtherPartyRolesPanel(String id, EditStateType editState,List<AgreementRoleDTO> currentAndFutureOtherPartyRoles, 
			long typeOID,boolean showRoleColumns , long agreementNo) {
		super(id ,editState );
		this.editState = editState;
		this.typeOID = typeOID;
		this.feedBackPanel = feedBackPanel;
		this.currentAndFutureOtherPartyRoles = currentAndFutureOtherPartyRoles;
		this.showRoleColumns = showRoleColumns;
		initGridRolesForFais();
		this.agreementNo = agreementNo;
//		this.add(searchWindow = createSearchWindow("searchPartyWindow"));
//		this.add(new OtherPartyRolesForm("otherPartyRolesForm"));
	}	
	
	/**
	 * Load the components on the page on first render, 
	 * so that the components are only generated when the page is displayed 
	 */
	@Override
	protected void onBeforeRender() {
		if(!initialised) {			
			initialised=true;				
	
			List<RequestKindType> unAuthRequests = getOutStandingRequestKinds();			
			//check for existing requests FIRST as other panels use variables set here
			
			//after the outstanding requests, we check if user can actually raise requests on left of request kinds
			ISessionUserProfile user = SRSAuthWebSession.get().getSessionUser();
			
			RequestKindType[] requestsForPanel = PanelToRequestMapping.getRequestKindsForPanel(AgreementHierarchyPanel.class);
			Set<RequestKindType> unAvailableRequest = new HashSet<RequestKindType>(requestsForPanel.length);			
			for(RequestKindType kind : requestsForPanel){
				if(!user.isAllowRaise(kind)){
					unAvailableRequest.add(kind);
				}
			}	
			unAvailableRequest.addAll(unAuthRequests);			
			for (RequestKindType kind : unAvailableRequest) {
				if(kind == RequestKindType.MaintainAgreementSupervisors){
					existingSupervisionRequest = true;
				}	
				if(kind == RequestKindType.MaintainAgreementHierarchy){
					existingHierarchyRequest = true;
					
				}
			}			
			this.add(searchWindow = createSearchWindow("searchPartyWindow"));
			this.add(new OtherPartyRolesForm("otherPartyRolesForm"));
		}
		if(feedBackPanel == null){			
			feedBackPanel = this.getFeedBackPanel();		
		}
		super.onBeforeRender();
	}

	/**
	 * 
	 * @author MZL2611
	 *
	 */
	public class OtherPartyRolesForm extends Form {
		private static final long serialVersionUID = 5808296649559984427L;

		public OtherPartyRolesForm(String id) {
			super(id);
			add(agreementOtherPartyRolesGrid = createOtherPartyRolesGrid("otherPartyRolesGrid", pageModel));
			add(createRemoveOtherPartyRoleButton("removeOtherPartyRoleButton"));
			add(createAddOtherPartyRoleButton("addOtherPartyRoleButton"));	
			add(createOtherPartyRolesHistoryButton("otherPartyRoleHistoryButton"));
			add(historyWindow = createHistoryWindow("historyWindow"));
			add (businessOwnerValidationPopUp = createValidationErrorPopUP("businessOwnerValidation"));
		}
	}
	
	
	/**
	 * This method initialises the Supervisor Grid Roles for the FAIS Screen 
	 *
	 */
	private void initGridRolesForFais() {
		try {
			if (pageModel == null){
				pageModel = new MaintainAgreementPageModel();
			}
			if(pageModel.getMaintainAgreementDTO() == null){
				pageModel.setMaintainAgreementDTO(new MaintainAgreementDTO());
			}
			if(pageModel.getMaintainAgreementDTO().getAgreementDTO() == null){
				pageModel.getMaintainAgreementDTO().setAgreementDTO(new AgreementDTO());
			}
			if(pageModel.getMaintainAgreementDTO().getAgreementDTO().getCurrentAndFutureSupervisorRoles() == null){
				pageModel.getMaintainAgreementDTO().getAgreementDTO().setCurrentAndFutureSupervisorRoles(currentAndFutureOtherPartyRoles);
			}
			List<TypeVO> supervisorTypeList = getAgreementGUIController().getFaisLicenseSubTypeList();
			TypeVO supervisorType = getAgreementGUIController().getSupervisorType(typeOID, supervisorTypeList);
			List<TypeVO> retainList =  new ArrayList<TypeVO>();
			retainList.add(supervisorType);			
			supervisorTypeList.retainAll(retainList);
			pageModel.setSupervisorTypes(supervisorTypeList);
		} catch (DataNotFoundException e) {
			logger.warn(e);
			pageModel.setSupervisorTypes(new ArrayList<TypeVO>());
		}
		
		List<RoleKindType> list = new ArrayList<RoleKindType>();
		list.add(RoleKindType.SUPERVISEDBY);
		
		pageModel.setSelectableSupervisionRoleKinds(list);
		List<AgreementRoleGridDTO> otherPartygridRoles = new ArrayList<AgreementRoleGridDTO>();
		pageModel.setOtherPartyGridRoles(otherPartygridRoles);
		if(this.currentAndFutureOtherPartyRoles != null)
		{
			for(AgreementRoleDTO role : this.currentAndFutureOtherPartyRoles)
			{
				if(role.getType() == this.typeOID )
				{
					if(role.getKind() == RoleKindType.PAYSTO.getKind()){
						//pays to is used in another screen, also used here for defualt on belongs to so we keep this role separate
						//do nothing as the pays to is set up for that tab, if set up on the tab then add it here to display
					}else{
						AgreementRoleGridDTO otherPartyGridRole = new AgreementRoleGridDTO();
						otherPartyGridRole.setRole(role);
						//set the other grid display data
						getAgreementGUIController().setUpOtherPartyGridRoleData(otherPartyGridRole);
						otherPartygridRoles.add(otherPartyGridRole);
						role.setRolePlayerReference(otherPartyGridRole.getAgreementParty());
					}
				}
			}
		}
		
		//first see, maybe the grid roles data has already been set up on the pagemodel
		if(pageModel.getOtherPartyGridRoles() == null || pageModel.getOtherPartyGridRoles().size() == 0){
			pageModel.setOtherPartyGridRoles(otherPartygridRoles);
		}
	}
	
	public  List<AgreementRoleGridDTO> updateAgreementRoleGridDTOList(){
		return pageModel.getOtherPartyGridRoles();
	}
	
	private void initGridRoles(){
		try {
			List<TypeVO> supervisorTypeList = getAgreementGUIController().getFaisLicenseSubTypeList();
			supervisorTypeList.addAll(pageModel.getBusinessStakeHolderTypes());
			pageModel.setSupervisorTypes(supervisorTypeList);
		} catch (DataNotFoundException e) {
			logger.warn(e);
			pageModel.setSupervisorTypes(new ArrayList<TypeVO>());
		}
		ContextDTO contextDTO = SRSAuthWebSession.get().getContextDTO();
		List<RoleKindType> list = new ArrayList<RoleKindType>();
		list.add(RoleKindType.SUPERVISEDBY);
		if (contextDTO!=null && contextDTO.getPartyContextDTO().getTypeOid()==SRSType.ORGANISATION)
		{
		list.add(RoleKindType.HASBUSINESSSTAKEHOLDER);
		}
		pageModel.setSelectableSupervisionRoleKinds(list);

		//first see, maybe the grid roles data has already been set up on the pagemodel
		if(pageModel.getOtherPartyGridRoles() == null || pageModel.getOtherPartyGridRoles().size() == 0){			
			List<AgreementRoleGridDTO> otherPartygridRoles = new ArrayList<AgreementRoleGridDTO>();
			pageModel.setOtherPartyGridRoles(otherPartygridRoles);
			if(pageModel.getMaintainAgreementDTO() != null && pageModel.getMaintainAgreementDTO().getAgreementDTO() != null && pageModel.getMaintainAgreementDTO().getAgreementDTO().getCurrentAndFutureSupervisorRoles() != null){
				IAgreementGUIController agreementGUIController = getAgreementGUIController();
				List<AgreementRoleDTO> roles = pageModel.getMaintainAgreementDTO().getAgreementDTO().getCurrentAndFutureSupervisorRoles();
				for(AgreementRoleDTO role : roles){					
						AgreementRoleGridDTO otherPartyGridRole = new AgreementRoleGridDTO();
						otherPartyGridRole.setRole(role);
						otherPartygridRoles.add(otherPartyGridRole);
						if(!(role.getRolePlayerReference() instanceof ResultPartyDTO)){
							//set the other grid display data
							agreementGUIController.setUpOtherPartyGridRoleData(otherPartyGridRole);		
							role.setRolePlayerReference(otherPartyGridRole.getAgreementParty());
						}else{
							otherPartyGridRole.setAgreementParty((ResultPartyDTO)role.getRolePlayerReference());
						}
				}				
			}	
		}
	}

	
	/**
	 * Create a grid for the agreement roles
	 * 
	 * @return
	 */
	private SRSDataGrid createOtherPartyRolesGrid(String id, MaintainAgreementPageModel model ) {
		agreementOtherPartyRoles = model.getOtherPartyGridRoles();
		if (agreementOtherPartyRoles == null) {
			agreementOtherPartyRoles = new ArrayList<AgreementRoleGridDTO>();
		}
		List<AgreementRoleGridDTO> nonSelectable = new ArrayList<AgreementRoleGridDTO>();
		//non selectable will be determined via outstatnding requests
		if( existingSupervisionRequest||existingHierarchyRequest){
			for(AgreementRoleGridDTO role : agreementOtherPartyRoles){
				if((existingSupervisionRequest ||existingHierarchyRequest)&& (role.getRole().getKind() == RoleKindType.SUPERVISEDBY.getKind()
						||role.getRole().getKind() == RoleKindType.HASBUSINESSSTAKEHOLDER.getKind())){
					nonSelectable.add(role);			
				}				
			}
		}		
		
		SRSDataGrid grid = new SRSDataGrid(id, new DataProviderAdapter(
				new ListDataProvider<AgreementRoleGridDTO>(agreementOtherPartyRoles)),
				getOtherPartyRolesColumns(), getEditState(),nonSelectable);		
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
	private List<IGridColumn> getOtherPartyRolesColumns() {
		Vector<IGridColumn> cols = new Vector<IGridColumn>();		
		if (!getEditState().isViewOnly()) {
			SRSGridRowSelectionCheckBox col = new SRSGridRowSelectionCheckBox(
					"checkBox");
			cols.add(col.setInitialSize(30));
		}
		//add in the name column
		cols.add(new SRSDataGridColumn<AgreementRoleGridDTO>("agreementParty.name",
				new Model("Name"), "agreementParty.name", "agreementParty.name", getEditState()).setInitialSize(140).setWrapText(true));
		
//		add search button, don't display this column on view
		if(getEditState() == null ||  !getEditState().isViewOnly() ){
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
										currentSearchOtherPartyRole = data;
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
		//add in the UACF ID
		cols.add(new SRSDataGridColumn<AgreementRoleGridDTO>("agreementParty.uacfID",
				new Model("UACF ID"), "agreementParty.uacfID", "agreementParty.uacfID", getEditState()){
			@Override
			public Panel newCellPanel(WebMarkupContainer parent,
					String componentId, IModel rowModel,
					String objectProperty, EditStateType state,
					final AgreementRoleGridDTO data) {				
				
				if (getEditState().isViewOnly() || getEditState() ==  EditStateType.TERMINATE || data.getRole().getRoleID() != 0) {
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
						currentSearchOtherPartyRole = data;
						String input = (String) uacfid.getModelObject();
						if(input != null && input.length() > 6){
							List<PartyProfileFLO> users = getAgreementGUIController().findUserWithUACFID(input);
							for(PartyProfileFLO user : users){
								if(user.getUacfId().equalsIgnoreCase(input)){								
									updateOtherPartyRowWithSelection(users.get(0), data, target);
								}
							}
						}
					}
				});				
				//validationComponents.add(uacfid);				
				return HelperPanel.getInstance(componentId, uacfid);
			}

		}.setInitialSize(120));
		

		cols.add(new SRSDataGridColumn<AgreementRoleGridDTO>("role.kind",
				new Model("Role Kind"), "role.kind", "role.kind",
				getEditState()) {

			@Override
			public Panel newCellPanel(WebMarkupContainer parent,
					String componentId, IModel rowModel,
					String objectProperty, EditStateType state,
					final AgreementRoleGridDTO data) {	
				
				if ((existingSupervisionRequest||existingHierarchyRequest) 
						|| getEditState().isViewOnly() 
						|| (data.getRole().getRoleID() != 0)) {
					//create label with type and display	
					RoleKindType role = RoleKindType.getRoleKindTypeForKind(data.getRole().getKind().intValue());
					return HelperPanel.getInstance(componentId, new Label("value",(role != null) ? role.getDescription() : ""));
				}				
				DropDownChoice dropdown = new DropDownChoice("value",new PropertyModel(data,objectProperty){
					@Override
						public Object getObject() {
							//return one of the values in the static list						
							Long id = (Long) super.getObject();
							if(id == null){
								return null;							
							}
							for(RoleKindType type : pageModel.getSelectableSupervisionRoleKinds()){
								if(type.getKind() == id){
									return type;
								}
							}
							return null;
						}
						@Override
						public void setObject(Object arg0) {						
							super.setObject(((RoleKindType)arg0).getKind());
							if (((RoleKindType)arg0).getKind()==RoleKindType.SUPERVISEDBY.getKind())
							{
								data.getRole().setKind(new Long(RoleKindType.SUPERVISEDBY.getKind()));
								data.getRole().setAgreementRoleKind(new Long(RoleKindType.SUPERVISEDBY.getKind()));
							}
							if (((RoleKindType)arg0).getKind()==RoleKindType.HASBUSINESSSTAKEHOLDER.getKind())
							{
								data.getRole().setKind(new Long(RoleKindType.HASBUSINESSSTAKEHOLDER.getKind()));
								data.getRole().setAgreementRoleKind(new Long(RoleKindType.HASBUSINESSSTAKEHOLDER.getKind()));
							}
						}
				},pageModel.getSelectableSupervisionRoleKinds(),new ChoiceRenderer("description", "kind"));
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
						if(data.getRole().getKind() == RoleKindType.SUPERVISEDBY.getKind() && pageModel.getSupervisorTypes().size() == 1){
							//selecting first one if list is only 1 size big
							data.getRole().setType(pageModel.getSupervisorTypes().get(0).getOid());							
						}else{
							data.getRole().setType(SRSType.MANAGESAGREEMENTROLE);
						}
						if(data.getRole().getKind() == RoleKindType.HASBUSINESSSTAKEHOLDER.getKind() && pageModel.getBusinessStakeHolderTypes().size() == 1){
							//selecting first one if list is only 1 size big
							data.getRole().setType(pageModel.getBusinessStakeHolderTypes().get(0).getOid());							
						}else{
							data.getRole().setType(SRSType.PARTYAGREEMENTROLE);
						}
						AgreementGridData gridDataObj = gridData.get(data);
						if(gridDataObj == null){
							gridDataObj = new AgreementGridData();
							gridData.put(data, gridDataObj);
						}
						
						Panel panel = (Panel)gridDataObj.getComponent("role.type");						
						if(panel != null)	{				
							HelperPanel panel2 = getSecondaryChoiceGridOtherPartyRolesSelectionDropdown(panel.getId(), "role.type", data);							
							panel.replaceWith(panel2);	
							gridDataObj.addComponent("role.type", panel2);
							target.add(panel2);
						}
						
						//update the grid as the next column needs this value
						target.add(agreementOtherPartyRolesGrid);
					}					
				});
				//create dropdown of selectable types				
				HelperPanel dropdownPanel = HelperPanel.getInstance(componentId, dropdown);	
				dropdownPanel.setVisible(true);
				
				return dropdownPanel;
			}

		}.setInitialSize(120));
		
		//adding the relationship type column
		cols.add(new SRSDataGridColumn<AgreementRoleGridDTO>("role.type",
				new Model("Sub Type"), "role.type", "role.type",
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
				HelperPanel panel = getSecondaryChoiceGridOtherPartyRolesSelectionDropdown(componentId, 
						objectProperty, data);				
				gridDataObj.addComponent(objectProperty, panel);
				panel.setVisible(true);
				return panel;				
			}

		}.setInitialSize(120));
		
		//the effective dates of the role
		cols.add(new SRSDataGridColumn<AgreementRoleGridDTO>("role.effectiveFrom",
				new Model("Start Date"), "role.effectiveFrom", "role.effectiveFrom",
				getEditState()) {

			@Override
			public Panel newCellPanel(WebMarkupContainer parent,
					String componentId, IModel rowModel,
					String objectProperty, EditStateType state,
					AgreementRoleGridDTO data) {	
				if ((existingSupervisionRequest ) || getEditState().isViewOnly() 
						|| (data.getRole().getRoleID() != 0 && DateUtil.getInstance().compareDatePart(data.getRole().getEffectiveFrom(),new Date()) <= 0)
						|| (existingSupervisionRequest && data.getRole().getRoleID() != 0 && data.getRole().getKind() != RoleKindType.ISSERVICEDBY.getKind())
						|| (existingSupervisionRequest && data.getRole().getRoleID() != 0 && data.getRole().getKind() == RoleKindType.ISSERVICEDBY.getKind())) {
					return super.newCellPanel(parent, componentId,
							rowModel, objectProperty, state, data);
				}
				SRSDateField startDate = new SRSDateField("value",
						new PropertyModel(data, objectProperty));
//				TextField startDate = new TextField("value",
//						new PropertyModel(data, objectProperty));
//				if (!getEditState().isViewOnly()) {
//					startDate.addNewDatePicker();
//				}
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
				if ((existingSupervisionRequest) || getEditState().isViewOnly()
						|| (existingSupervisionRequest && data.getRole().getRoleID() != 0 && data.getRole().getKind() != RoleKindType.ISSERVICEDBY.getKind())
						|| (existingSupervisionRequest && data.getRole().getRoleID() != 0 && data.getRole().getKind() == RoleKindType.ISSERVICEDBY.getKind())) {
					return super.newCellPanel(parent, componentId,
							rowModel, objectProperty, state, data);
				}
				SRSDateField endDate = new SRSDateField("value",
						new PropertyModel(data, objectProperty));
				endDate.addNewDatePicker();
				endDate.add(new AttributeModifier("size", "12"));
				endDate.add(new AttributeModifier("maxlength", "10"));
				endDate.setLabel(new Model("Parent End Date"));
				endDate.add(new AjaxFormComponentUpdatingBehavior("change") {
					@Override
					protected void onUpdate(AjaxRequestTarget target) {
						//do nothing just want the object value updated
					}
				});
				return HelperPanel.getInstance(componentId, endDate, true);
			}

		}.setInitialSize(130));

		return cols;
	}
	
	/**
	 * Create the button to remove a home
	 * 
	 * @return
	 */
	private Button createRemoveOtherPartyRoleButton(String id) {
		final Button button = new Button(id);
		button.add(new AjaxFormComponentUpdatingBehavior("click") {
			private static final long serialVersionUID = 0L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				List<Object> selections = agreementOtherPartyRolesGrid.getSelectedItemObjects();				
				for (Object selection : selections) {					
					AgreementRoleGridDTO gridRole = (AgreementRoleGridDTO) selection;
					pageModel.removeOtherPartyGridRole(gridRole);						
				}					
				target.add(agreementOtherPartyRolesGrid);
			}
		});		
		if(getEditState() == EditStateType.AUTHORISE){			
			button.setVisible(false);
		}
		button.setOutputMarkupId(true);
		button.setOutputMarkupPlaceholderTag(true);
		setUpbutton(button,!(existingSupervisionRequest||existingHierarchyRequest));
		return button;
	}
	
	/**
	 * Create the button to add a home
	 * 
	 * @return
	 */
	private Button createAddOtherPartyRoleButton(String id) {
		final Button button = new Button(id);
		button.add(new AjaxFormComponentUpdatingBehavior("click") {
			private static final long serialVersionUID = 0L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {				
				AgreementRoleDTO dto = new AgreementRoleDTO();

				if(pageModel.getMaintainAgreementDTO().getAgreementDTO().getCurrentAndFutureSupervisorRoles() != null)
				{
					if(pageModel.getMaintainAgreementDTO().getAgreementDTO().getCurrentAndFutureSupervisorRoles().size() == 0){
						dto.setEffectiveFrom(pageModel.getMaintainAgreementDTO().getAgreementDTO().getStartDate());
					}else if(currentSearchOtherPartyRole != null){
						//set current other party end date
						currentSearchOtherPartyRole.getRole().setEffectiveTo(new Date());
					}
				}
/*				dto.setKind(new Long(RoleKindType.SUPERVISEDBY.getKind()));
				dto.setAgreementRoleKind(new Long(RoleKindType.SUPERVISEDBY.getKind()));*/
				//dto.setType(SRSType.PARTYAGREEMENTROLE);				
				dto.setRolePlayerReference(new ResultPartyDTO());
				if(pageModel.getMaintainAgreementDTO().getAgreementDTO().getId() != 0)
				{
					dto.setAgreementNumber(pageModel.getMaintainAgreementDTO().getAgreementDTO().getId());
				}
				else
				{
						 dto.setAgreementNumber(agreementNo);
						 dto.setType(typeOID);
				}

				AgreementRoleGridDTO gridRole = new AgreementRoleGridDTO();
				gridRole.setRole(dto);
				pageModel.addOtherPartyGridRole(gridRole);				
				target.add(agreementOtherPartyRolesGrid);
				
			}
		});		
		if(getEditState() == EditStateType.AUTHORISE){			
			button.setVisible(false);
		}
		button.setOutputMarkupId(true);
		button.setOutputMarkupPlaceholderTag(true);
		setUpbutton(button, !(existingSupervisionRequest||existingHierarchyRequest));
		return button;
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
	private HelperPanel getSecondaryChoiceGridOtherPartyRolesSelectionDropdown(String componentId,
			String objectProperty, final AgreementRoleGridDTO data){
		HelperPanel emptyPanel = null;
		final Long kind = data.getRole().getKind();
		boolean isRequiredRoleKind = false;
		List<Long> kinds = new ArrayList<Long>();
			kinds.add(Long.valueOf(RoleKindType.SUPERVISEDBY.getKind()));
			kinds.add(Long.valueOf(RoleKindType.HASBUSINESSSTAKEHOLDER.getKind()));
			if (kinds.contains(kind))
			{
				isRequiredRoleKind= true;
			}
		if ((existingSupervisionRequest||existingHierarchyRequest) || getEditState().isViewOnly() || (data.getRole().getRoleID() != 0) || !isRequiredRoleKind) {
			//create label with type and display	
			TypeVO type = new TypeVO();
			if (data.getRole().getKind()==RoleKindType.SUPERVISEDBY.getKind()){
			type =pageModel.getSupervisorType(data.getRole().getType());
			}
			else if (data.getRole().getKind()==RoleKindType.HASBUSINESSSTAKEHOLDER.getKind())
			{
				type =pageModel.getBusinessType(data.getRole().getType());
			}
			HelperPanel panel = HelperPanel.getInstance(componentId, new Label("value",(type != null) ? type.getName(): ""));			
			panel.setOutputMarkupId(true);
			panel.setOutputMarkupPlaceholderTag(true);
			return panel;
		}
		if (data.getRole().getKind()==RoleKindType.SUPERVISEDBY.getKind()){
		DropDownChoice<?> dropdown = new DropDownChoice("value",new PropertyModel(data,objectProperty){
			@Override
				public Object getObject() {
					//return one of the values in the static list						
					Long id = (Long) super.getObject();
					if(id == null){
						return null;							
					}
					for(TypeVO type : pageModel.getSupervisorTypes()){
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
		},pageModel.getSupervisorTypes(),new ChoiceRenderer("name", "oid"));
		
		dropdown.add(new AjaxFormComponentUpdatingBehavior("change"){
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				//update the value					
			}					
		});		
		dropdown.add(new AttributeModifier("style","width: 120px;"));
		dropdown.setRequired(true);
		//create dropdown of selectable types	
		dropdown.setLabel(new Model("Relationship Type"));
		HelperPanel dropdownPanel = HelperPanel.getInstance(componentId, dropdown);	
		dropdownPanel.setOutputMarkupId(true);
		dropdownPanel.setOutputMarkupPlaceholderTag(true);
		return dropdownPanel;
	}else if (data.getRole().getKind()==RoleKindType.HASBUSINESSSTAKEHOLDER.getKind())
	{
		DropDownChoice dropdown = new DropDownChoice("value",new PropertyModel(data,objectProperty){
			@Override
				public Object getObject() {
					//return one of the values in the static list						
					Long id = (Long) super.getObject();
					if(id == null){
						return null;							
					}
					for(TypeVO type : pageModel.getBusinessStakeHolderTypes()){
						if(type.getOid() == id){
							return type;
						}
					}
					return null;
				}
				@Override
				public void setObject(Object arg0) {						
					super.setObject(((TypeVO)arg0).getOid());
					data.getRole().setType(((TypeVO)arg0).getOid());
				}
		},pageModel.getBusinessStakeHolderTypes(),new ChoiceRenderer("description", "oid"));
		
		dropdown.add(new AjaxFormComponentUpdatingBehavior("change"){
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				currentSearchOtherPartyRole = data;
				if (currentSearchOtherPartyRole.getAgreementParty().getOid()!=0&&currentSearchOtherPartyRole.getRole().getType().equals(SRSType.BUSINESSOWNER))
				{
					ContextDTO contextPartyDto = SRSAuthWebSession.get().getContextDTO();
					try {
						resultPartyDto = currentSearchOtherPartyRole.getAgreementParty();
						validateBusinessParty(currentSearchOtherPartyRole.getAgreementParty(), contextPartyDto, target);
					} catch (ValidationException e) {
						validationErrorMessage = e.getErrorMessages().get(0);
						businessOwnerValidationPopUp.show(target);
					}
				}
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
	}else{
		if(typeOID > 0)
		{
			TypeVO type =pageModel.getSupervisorType(typeOID);
			HelperPanel panel = HelperPanel.getInstance(componentId, new Label("value",(type != null) ? type.getName(): ""));			
			panel.setOutputMarkupId(true);
			panel.setOutputMarkupPlaceholderTag(true);
			return panel;

		}
		else{
			TypeVO type =pageModel.getServicingType(data.getRole().getType());
			emptyPanel = HelperPanel.getInstance(componentId, new Label("value",(type != null) ? type.getDescription(): ""));			
			emptyPanel.setOutputMarkupId(true);
			emptyPanel.setOutputMarkupPlaceholderTag(true);
		}
	}
		return emptyPanel;
	}
	private ModalWindow createSearchWindow(String id) {
		ContextSearchPopUp popUp = new ContextSearchPopUp() {

		@Override
		public ContextType getContextType() {
			
			//if(currentSearchSupervisonAgmtRole != null){
				return ContextType.PARTY_PERSON_ONLY;
			//}
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
				resultPartyDto = resultPartyDTO;
				if(currentSearchOtherPartyRole != null){
					
					if(resultPartyDTO == null || resultPartyDTO.getOid() < 1){
						OtherPartyRolesPanel.this.error("Please only select Persons for party roles");		
						if(feedBackPanel != null){
							target.add(feedBackPanel);
							break;
						}
					}if (currentSearchOtherPartyRole.getRole().getType().equals(SRSType.BUSINESSOWNER))
					{
						ContextDTO contextPartyDto = SRSAuthWebSession.get().getContextDTO();
						try{
							validateBusinessParty(resultPartyDTO, contextPartyDto, target);
							currentSearchOtherPartyRole.getRole().setRolePlayerReference(resultPartyDTO);		
							currentSearchOtherPartyRole.setAgreementParty(resultPartyDTO);
							agreementGUIController.setUpAgreementGridRoleData(currentSearchOtherPartyRole);
							if (target != null) {
								target.add(agreementOtherPartyRolesGrid);
							}
						}catch (ValidationException e) {
							validationErrorMessage = e.getErrorMessages().get(0);
							businessOwnerValidationPopUp.show(target);
						}
					}
					else{
						currentSearchOtherPartyRole.getRole().setRolePlayerReference(resultPartyDTO);	
						currentSearchOtherPartyRole.setAgreementParty(resultPartyDTO);
						
						agreementGUIController.setUpOtherPartyGridRoleData(currentSearchOtherPartyRole);						
						
						if (target != null) {
							
							target.add(agreementOtherPartyRolesGrid);
							if(feedBackPanel != null){
								target.add(feedBackPanel);
							}
						}
					}
					break;
				}		
			}
			
			target.add(agreementOtherPartyRolesGrid);	
		}											
	};	
		
	ModalWindow win = popUp.createModalWindow(id);
//	win.setPageMapName("homeSearchPageMap");
	return win;	
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


	public EditStateType getEditState() {
		return editState;
	}


	public void setEditState(EditStateType editState) {
		this.editState = editState;
	}
	
	/**
	 * Create the button to show the other agreement roles history for this node
	 * 
	 * @return
	 */
	private Button createOtherPartyRolesHistoryButton(String id) {
		final Button button = new Button(id);
		button.add(new AjaxFormComponentUpdatingBehavior("click") {
			private static final long serialVersionUID = 0L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				historyType = HistoryPageType.SUPERVISOR;
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

	public List<AgreementRoleGridDTO> getAgreementOtherPartyRoles() {
		return agreementOtherPartyRoles;
	}

	public void setAgreementOtherPartyRoles(List<AgreementRoleGridDTO> agreementOtherPartyRoles) {
		this.agreementOtherPartyRoles = agreementOtherPartyRoles;
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
				return new RoleHistoryPage(window,pageModel.getMaintainAgreementDTO().getAgreementDTO(),historyType, pageModel.getSupervisorTypes());
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
	

	

	/**
	 * Update the employee row with the selected user
	 * @param selection
	 * @param row
	 */
	private void updateOtherPartyRowWithSelection(PartyProfileFLO selection, AgreementRoleGridDTO row, AjaxRequestTarget target){
		IAgreementGUIController agreementGUIController = getAgreementGUIController();
		if(selection != null && row != null){
			try {
				ResultPartyDTO party = getPartyManagement().findPartyWithObjectOid(selection.getPartyOid());
				resultPartyDto = party;
				if(currentSearchOtherPartyRole != null){
					
					if(party == null || party.getOid() < 1){
						OtherPartyRolesPanel.this.error("Please only select Persons for party roles");		
						if(feedBackPanel != null){
							target.add(feedBackPanel);
							
						}
					}if (currentSearchOtherPartyRole.getRole().getType().equals(SRSType.BUSINESSOWNER))
					{
						ContextDTO contextPartyDto = SRSAuthWebSession.get().getContextDTO();
						try{
							validateBusinessParty(party, contextPartyDto, target);
							currentSearchOtherPartyRole.getRole().setRolePlayerReference(party);		
							currentSearchOtherPartyRole.setAgreementParty(party);
							agreementGUIController.setUpAgreementGridRoleData(currentSearchOtherPartyRole);
							if (target != null) {
								target.add(agreementOtherPartyRolesGrid);
							}
						}catch (ValidationException e) {
							validationErrorMessage = e.getErrorMessages().get(0);
							businessOwnerValidationPopUp.show(target);
						}
					}
					else{
						currentSearchOtherPartyRole.getRole().setRolePlayerReference(party);	
						currentSearchOtherPartyRole.setAgreementParty(party);
						
						getAgreementGUIController().setUpOtherPartyGridRoleData(currentSearchOtherPartyRole);						

						if (target != null) {
							target.add(agreementOtherPartyRolesGrid);
						}
					}
					
				}		
				//party.setBranchName(party.getUacfID());				
				//row.setAgreementParty(party);				
			} catch (DataNotFoundException e) {
				error("Could not find party with id " + selection.getPartyOid());
				target.add(feedBackPanel);	
			}
			target.add(agreementOtherPartyRolesGrid);	
		}
	}

	/**
	 * Get the agreement manager
	 * @return
	 */
	private IPartyManagement getPartyManagement() {
		if(partyManagement == null){
			try{
				partyManagement = ServiceLocator.lookupService(IPartyManagement.class);
			
				
			} catch (NamingException e) {
					throw new CommunicationException(e);
			}	
		}
		return partyManagement;
	}
	
	protected void validateBusinessParty(ResultPartyDTO partyDTO, ContextDTO contextPartyDto,AjaxRequestTarget target) throws ValidationException {
		getPartyValidator().validateBusinessOwner(partyDTO,contextPartyDto);

}
	
	private IPartyValidator getPartyValidator() {
		if(partyValidator == null){
			try {
				partyValidator = ServiceLocator.lookupService(IPartyValidator.class);
			} catch (NamingException e) {
				throw new CommunicationException(e);
			}
		}
		return partyValidator;
	}
	private ModalWindow createValidationErrorPopUP(String id) {
		final IAgreementGUIController agreementGUIController = getAgreementGUIController();
		final ModalWindow window = new ModalWindow(id);
		window.setTitle("Validation Error");
		window.setPageCreator(new ModalWindow.PageCreator() {

			private static final long serialVersionUID = 1L;

			public Page createPage() {
				return new BusinessOwnerValidationErrorPage(window,validationErrorMessage);
			}
		});
		window.setWindowClosedCallback(new ModalWindow.WindowClosedCallback() {
			private static final long serialVersionUID = 1L;
			public void onClose(AjaxRequestTarget target) {
				currentSearchOtherPartyRole.getRole().setRolePlayerReference(resultPartyDto);		
				currentSearchOtherPartyRole.setAgreementParty(resultPartyDto);
				agreementGUIController.setUpAgreementGridRoleData(currentSearchOtherPartyRole);
				if (target != null) {
					target.add(agreementOtherPartyRolesGrid);
				}
			}
		});
		window.setMinimalHeight(150);
		window.setInitialHeight(150);
		window.setMinimalWidth(600);
		window.setInitialWidth(600);
		window.setMaskType(MaskType.SEMI_TRANSPARENT);
		window.setCssClassName(ModalWindow.CSS_CLASS_GRAY);
		return window;
	}
}
