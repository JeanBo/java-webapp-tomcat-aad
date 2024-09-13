package za.co.liberty.web.pages.hierarchy;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import javax.naming.NamingException;

import org.apache.commons.lang.SerializationUtils;
import org.apache.log4j.Logger;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AbstractAjaxTimerBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.validation.IFormValidator;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.util.time.Duration;

import com.inmethod.grid.IGridColumn;
import com.inmethod.grid.SizeUnit;

import za.co.liberty.business.guicontrollers.hierarchy.HierarchyGUIController;
import za.co.liberty.business.guicontrollers.hierarchy.IHierarchyGUIController;
import za.co.liberty.common.domain.ObjectReference;
import za.co.liberty.common.enums.ComponentEnum;
import za.co.liberty.dto.agreement.AgreementDTO;
import za.co.liberty.dto.agreement.AgreementHomeRoleDTO;
import za.co.liberty.dto.agreement.AgreementRoleDTO;
import za.co.liberty.dto.agreement.SimpleAgreementDetailDTO;
import za.co.liberty.dto.agreement.maintainagreement.BulkAgreementTransferDTO;
import za.co.liberty.dto.agreement.maintainagreement.MaintainAgreementDTO;
import za.co.liberty.dto.agreement.maintainagreement.MaintainAgreementRequestConfigurationDTO;
import za.co.liberty.dto.agreement.request.RequestEnquiryResultDTO;
import za.co.liberty.dto.contracting.ResultPartyDTO;
import za.co.liberty.dto.gui.context.ResultContextItemDTO;
import za.co.liberty.dto.party.HierarchyNodeDTO;
import za.co.liberty.exceptions.data.DataNotFoundException;
import za.co.liberty.exceptions.data.QueryTimeoutException;
import za.co.liberty.exceptions.data.ValidationException;
import za.co.liberty.exceptions.error.request.RequestException;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.helpers.lookup.ServiceLocator;
import za.co.liberty.interfaces.agreements.requests.PropertyKindType;
import za.co.liberty.interfaces.agreements.requests.RequestKindType;
import za.co.liberty.interfaces.persistence.agreement.request.IRequestEnquiryRow;
import za.co.liberty.persistence.agreement.IAgreementEntityManager;
import za.co.liberty.persistence.rating.IRatingEntityManager;
import za.co.liberty.srs.type.SRSType;
import za.co.liberty.web.data.enums.ContextType;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.pages.hierarchy.model.BulkTransferGridModel;
import za.co.liberty.web.pages.hierarchy.model.BulkTransferPanelModel;
import za.co.liberty.web.pages.hierarchy.model.MaintainHierarchyPageModel;
import za.co.liberty.web.pages.interfaces.IPageDataLoaded;
import za.co.liberty.web.pages.interfaces.ISecurityPanel;
import za.co.liberty.web.pages.panels.BasePanel;
import za.co.liberty.web.pages.panels.HelperPanel;
import za.co.liberty.web.pages.search.ContextSearchPopUp;
import za.co.liberty.web.system.SRSAuthWebSession;
import za.co.liberty.web.wicket.markup.html.grid.GridToCSVHelper;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGrid;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGrid.GridSizeUnit;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGridColumn;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataProviderAdapter;
import za.co.liberty.web.wicket.markup.html.grid.SRSGridRowSelectionCheckBox;
import za.co.liberty.web.wicket.markup.repeater.data.SortableListDataProvider;

/**
 * Bulk Transfer Panel
 * 
 * @author DZS2610
 * 
 */
public class BulkTransferPanel extends BasePanel implements ISecurityPanel{	
	
	private transient IHierarchyGUIController hierarchyGUIController;
	
	private MaintainHierarchyPageModel pageModel;	
	
	private FeedbackPanel feedBackPanel;
	
	private ModalWindow searchWindow;
	
	private SRSDataGrid agreementsGrid;	
	
	private Form transferForm;

	private List<FormComponent> validationComponents = new ArrayList<FormComponent>();
	
	private final SimpleDateFormat dteFormatter = new SimpleDateFormat("dd/MM/yyyy");
	
	private Label nodeToLabel;
	
	private TextField codeField;
	
	private BulkTransferPanelModel panelModel;	
	
	private WebMarkupContainer gridLoadingImage;
	
	private Button exportToExcelButton;
	
	private static final Logger logger = Logger.getLogger(BulkTransferPanel.class); 

	private static final long serialVersionUID = 1L;
	
	private static final String PENDING_REQUESTS_STYLE = "color:#993300;";
	
	private boolean initialized = false;
	
	//keeps track of the currently selected external reference
	private String currentNodeExternalRef;
	
    //if lockALL set to true then no agreements can be transfered
	private boolean lockALL = false;
	
	private boolean gridUpdated = false;
	
	private AbstractAjaxTimerBehavior gridUpdateTimer;
	
	//change this value if you need more agreements coming back when the screen loads
	private Integer FIRST_RETREIVAL_COUNT = 50;
	
	private IPageDataLoaded pageDataLoaded;
	
	private transient IAgreementEntityManager agmtEntityManager;
	private transient IRatingEntityManager ratingEntityManager;

	/**
	 * @param arg0
	 */
	public BulkTransferPanel(String id, final MaintainHierarchyPageModel pageModel,
			EditStateType editState, FeedbackPanel feedBackPanel, Page parentPage) {
		super(id,editState,parentPage);		
		this.pageModel = pageModel;	
		this.feedBackPanel = feedBackPanel;	
		pageDataLoaded = (parentPage instanceof IPageDataLoaded) ? (IPageDataLoaded)parentPage : null;
		if(pageDataLoaded != null){
			pageDataLoaded.setPageDataLoaded(false);
		}
	}
	
	
	
	@Override
	protected void onBeforeRender() {		
		super.onBeforeRender();
		if(feedBackPanel == null){
			feedBackPanel = getFeedBackPanel();
		}
		if(!initialized){
			if(getEditState() == EditStateType.MODIFY){
				FIRST_RETREIVAL_COUNT = null;
			}
			//create the panel model
			initialized = true;
			if(pageModel.getBulkTransferPanelModel() == null){
				panelModel = createPanelModel(pageModel);
				pageModel.setBulkTransferPanelModel(panelModel);
			}else{
				panelModel = pageModel.getBulkTransferPanelModel();
			}			
			add(transferForm = createTransferForm("transferForm"));
			add(searchWindow = createSearchWindow("searchPartyWindow"));
		}
	}



	/**
	 * Create a panel model forthe panel
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private BulkTransferPanelModel createPanelModel(MaintainHierarchyPageModel pageModel){		
		BulkTransferPanelModel model = new BulkTransferPanelModel();
		pageModel.setBulkTransferPanelModel(model);
		if(pageModel == null || pageModel.getHierarchyNodeDTO() == null || pageModel.getHierarchyNodeDTO().getId() <= 0){
			return model;
		}
		model.setBranchFrom(pageModel.getHierarchyNodeDTO());
		//now get all agreements and set
		model.setGridData(new ArrayList<BulkTransferGridModel>());
		
		// we look for outstanding requests per agreement so we can mark them uneditable
		
		//will lock all agreements if there is an outstanding maintain hierarchy node request
		//or terminate/reactivate request	
		try {
			List<IRequestEnquiryRow> requests = getHierarchyGUIController().findOutstandingNodeRequest(pageModel.getHierarchyNodeDTO().getOid(), new RequestKindType[]{RequestKindType.MaintainHierarchyNodeDetails,RequestKindType.TerminateHierarchyNode,RequestKindType.ReactivateHierarchyNode});
			if(requests.size() != 0){
				RequestKindType[] outstandingRequests = new RequestKindType[requests.size()];				
				int i = 0;
				for(IRequestEnquiryRow request : requests){					
					outstandingRequests[i] = RequestKindType.getRequestKindTypeForKind(request.getRequestKind());
					i++;
				}
				error("There are currently outstanding requests["+outstandingRequests+"] that still need to be approved, locking all agreements from changes");
				lockALL = true;
			}
		} catch (RequestException e1) {
			error("There was a problem getting the outstanding hierarchy change requests, locking all agreements from changes");
			logger.error("An error occured when getting the outstanding hierarchy change requests",e1);
			lockALL = true;
		} catch (QueryTimeoutException e1) {
			error("There was a problem getting the outstanding hierarchy change requests, locking all agreements from changes");
			logger.error("An error occured when getting the outstanding hierarchy change requests",e1);
			lockALL = true;
		}
		//update the model grid data for first FIRST_RETREIVAL_COUNT only, once panel is loaded we will load the rest with ajax
		updatePanelModel(model,null,FIRST_RETREIVAL_COUNT);						
		return model;
	}
	
	private void updatePanelModel(BulkTransferPanelModel model, Integer fromRecord, Integer toRecord){
		if(fromRecord == null && toRecord == null){
			//clear the model grid data so we can put all the data in again
			model.getGridData().clear();
		}		
		long time = System.currentTimeMillis();
//		limit search to 50, will fetch all the rest afterRender
		Collection<SimpleAgreementDetailDTO> agData = getHierarchyGUIController().findAllAgreementsLinkedToHierarchyNode(pageModel.getHierarchyNodeDTO().getOid(),fromRecord,toRecord);
		List<Long> agreementNumbers = new ArrayList<Long>(agData.size());
		HashMap<Long, SimpleAgreementDetailDTO> floMap = new HashMap<Long, SimpleAgreementDetailDTO>(agData.size());
		for(SimpleAgreementDetailDTO dto : agData){			
			agreementNumbers.add(dto.getAgreementNumber());
			floMap.put(dto.getAgreementNumber(), dto);
		}	
		
		Collection<AgreementRoleDTO> roleData = Collections.EMPTY_LIST;
		try {
			roleData = getHierarchyGUIController().findAllAgreementHomeRoles(agreementNumbers);
		} catch (DataNotFoundException e) {
			//should never happen
			error("Could not find the role data for the branch");
		}
		
		HashMap<Long, List<RequestKindType>> lockedAgreements = new HashMap<Long, List<RequestKindType>>(agData.size());
		try {
			RequestEnquiryResultDTO results = getHierarchyGUIController().findAllOutstandingHomeRequestsForAgreements(agreementNumbers);
			List<IRequestEnquiryRow> rows = results.getResultList();
			for(IRequestEnquiryRow row : rows){
				List<RequestKindType> types = lockedAgreements.get(row.getAgreementNr());
				if(types == null){
					types = new ArrayList<RequestKindType>();
				}
				types.add(RequestKindType.getRequestKindTypeForKind(row.getRequestKind()));
				lockedAgreements.put(row.getAgreementNr(),types);				
			}
		} catch (RequestException e) {
			error("There was a problem getting the outstanding requests for the agreement list, locking all agreements from changes");
			logger.error("An error occured when getting the requests for the agreements",e);
			lockALL = true;
		} catch (QueryTimeoutException e) {		
			error("There was a problem getting the outstanding requests for the agreement list, locking all agreements from changes");
			logger.error("An error occured when getting the requests for the agreements",e);
			lockALL = true;
		}
		
		//fill in the consultant code
		for(AgreementRoleDTO role : roleData){
			if(role instanceof AgreementHomeRoleDTO){
				BulkTransferGridModel gridModel = new BulkTransferGridModel();
				//set the outsanding request kinds
				if(lockALL){
					//will display an outstanding request for each agreement, locks them from being changed
					gridModel.addOutstandingRequestkind(RequestKindType.BranchTransfer);
				}else if(lockedAgreements.get(role.getAgreementNumber()) != null){
					for(RequestKindType type : lockedAgreements.get(role.getAgreementNumber())){
						gridModel.addOutstandingRequestkind(type);
					}
				}
				
				AgreementHomeRoleDTO homeRole = (AgreementHomeRoleDTO)role;
				homeRole.setConsultantCode(SimpleAgreementDetailDTO.getConsultantCodeFromLong(floMap.get(role.getAgreementNumber()).getConsCode()));
				//we also need to reset this role as it will be changed
				//we change the start date of this to today
				
				
				//roleplayer will be a party in context
				ResultPartyDTO party = new ResultPartyDTO();
				party.setName(pageModel.getHierarchyNodeDTO().getBusinessName());
				party.setOid(pageModel.getHierarchyNodeDTO().getOid());
				party.setTypeOid(pageModel.getHierarchyNodeDTO().getTypeOID());
				party.setExternalReference(pageModel.getHierarchyNodeDTO().getBranchCode());				
				party.setComponentOid(ComponentEnum._PARTY);
				party.setEffectiveFrom(pageModel.getHierarchyNodeDTO().getEffectiveFrom());
				party.setEffectiveTo(pageModel.getHierarchyNodeDTO().getEffectiveTo());	
				party.setJobTitle("RESERVED VALUE");
				homeRole.setRolePlayerReference(party);
				
				//set the data for the grid
				gridModel.setHomeRole(homeRole);
				gridModel.setAgreementDetails(floMap.get(homeRole.getAgreementNumber()));
//				set up the history
				AgreementHomeRoleDTO beforeImage = (AgreementHomeRoleDTO) SerializationUtils.clone(homeRole);
				gridModel.setOrigImage(beforeImage);
				model.getGridData().add(gridModel);			
			}			
		}		
		logger.info("time to get all agreement data = " + (System.currentTimeMillis() - time));
	}
	
	
	
	/**
	 * Form used for the panel so we can add validations and on submit method calls
	 * @author DZS2610
	 *
	 */
	public class TransferForm extends Form {
		private static final long serialVersionUID = 1L;
		public TransferForm(String id) {
			super(id);			
			add(new Label("nodeFrom",panelModel.getBranchFrom().getBusinessName()));
			add(new Label("nodeFromCode",panelModel.getBranchFrom().getBranchCode()));
			nodeToLabel = new Label("nodeTo",new PropertyModel(panelModel,"branchTo.name"));
			nodeToLabel.setOutputMarkupId(true);
			nodeToLabel.setOutputMarkupPlaceholderTag(true);
			add(nodeToLabel);
			add(codeField = createNodeToCodeField("nodeToCode"));
			add(createSearchButton("searchButton"));
			add(agreementsGrid = createAgreementGrid("agreementsGrid"));
			add(gridLoadingImage = createGridLoadingComponent("gridLoading"));
			add(exportToExcelButton = createExportToExcelButton("exportToExcelButton"));
			add(new IFormValidator() {
				private static final long serialVersionUID = 1L;
				public FormComponent[] getDependentFormComponents() {				
					return null;
				}
				public void validate(final Form form) {				
					if (getEditState().isViewOnly() && getEditState() != EditStateType.TERMINATE) {
						return;
					}
					boolean validate = true;
					for(FormComponent comp : validationComponents){
						if(!comp.isValid()){
							validate = false;
						}
					}
					if(validate){
						//update the selection list in the panel model for processing
						//this is due to validation being called before submit on the form
						long loggedInParty = (SRSAuthWebSession.get().getSessionUser() != null) ? SRSAuthWebSession.get().getSessionUser().getPartyOid() : 0;
						if(agreementsGrid != null){
							List<BulkTransferGridModel> selections = (List)agreementsGrid.getSelectedItemObjects();
							List<BulkAgreementTransferDTO> transfers = new ArrayList<BulkAgreementTransferDTO>(selections.size());
							for(BulkTransferGridModel data : selections){
								AgreementHomeRoleDTO transfer = data.getHomeRole();
								AgreementHomeRoleDTO orig = data.getOrigImage();
								AgreementHomeRoleDTO beforeRoleToAjust = (AgreementHomeRoleDTO) SerializationUtils.clone(orig);
														
								//should it be today?
								Date now = new Date();
								transfer.setEffectiveFrom(now);
								//set new role id to zero so it stores new record
								transfer.setRoleID(0L);
								beforeRoleToAjust.setEffectiveTo(now);				
								//first obj
								MaintainAgreementDTO mdto = new MaintainAgreementDTO();
								AgreementDTO agmt = new AgreementDTO();
								agmt.setCurrentAndFutureAgreementRoles(new ArrayList<AgreementRoleDTO>(0));
								ArrayList<AgreementHomeRoleDTO> agmtRoles = new ArrayList<AgreementHomeRoleDTO>();
								agmtRoles.add(beforeRoleToAjust);
								agmtRoles.add(transfer);				
								agmt.setCurrentAndFutureHomeRoles(agmtRoles);
								agmt.setId(transfer.getAgreementNumber());
								agmt.setKind(data.getAgreementDetails().getKind().intValue());
								agmt.setStartDate(data.getAgreementDetails().getStartDate());
								agmt.setPartyOid(data.getAgreementDetails().getPartyOID());
								mdto.setAgreementDTO(agmt);							
								mdto.setLoggedInPartyId(loggedInParty);
								
								
								//origional data
								MaintainAgreementDTO mdto2 = new MaintainAgreementDTO();
								AgreementDTO agmt2 = new AgreementDTO();
								agmt2.setCurrentAndFutureAgreementRoles(new ArrayList<AgreementRoleDTO>(0));
								ArrayList<AgreementHomeRoleDTO> agmtRoles2 = new ArrayList<AgreementHomeRoleDTO>();
								agmtRoles2.add(orig);				
								agmt2.setCurrentAndFutureHomeRoles(agmtRoles2);
								agmt2.setId(orig.getAgreementNumber());
								agmt2.setPartyOid(data.getAgreementDetails().getPartyOID());
								agmt2.setKind(data.getAgreementDetails().getKind().intValue());
								agmt2.setStartDate(data.getAgreementDetails().getStartDate());
								mdto2.setAgreementDTO(agmt2);	
								mdto2.setLoggedInPartyId(loggedInParty);
								MaintainAgreementRequestConfigurationDTO obj1 = new MaintainAgreementRequestConfigurationDTO(mdto); 
								MaintainAgreementRequestConfigurationDTO obj2 = new MaintainAgreementRequestConfigurationDTO(mdto2);
								BulkAgreementTransferDTO transferDTO = new BulkAgreementTransferDTO(obj1,obj2);
								transfers.add(transferDTO);
							}					
							panelModel.setSelections(transfers);
						}						
						//validate all the 13 digit codes of the selections
						List<BulkAgreementTransferDTO> selections = panelModel.getSelections();												
						try{
							getHierarchyGUIController().validateBranchTransferRequirements(selections);
						}catch(ValidationException ex){
							for(String error : ex.getErrorMessages()){
								TransferForm.this.error(error);								
							}
						}
					}
				}
			});
		
		}		
	}
	
	/**
	 * Create a grid for the agreements linked
	 * 
	 * @return
	 */
	private SRSDataGrid createAgreementGrid(String id) {
		final List<BulkTransferGridModel> agreements = ((panelModel.getGridData() == null) ? new ArrayList<BulkTransferGridModel>() : panelModel.getGridData());
		List<BulkTransferGridModel> nonSelectable = new ArrayList<BulkTransferGridModel>();
		/*Market Integration 31/05/2016 SSM2707 Sweta Menon*/
		/*Get all Sales Categories that can own a Panel*/
		List<String> bcSalesCat = getRatingEntityManager().getBCSalesCatAllowed();
		
		//we check for oustatning requests per agreement,if there are thy can not be updated
		for(BulkTransferGridModel model : agreements){
			if(model.getOutstandingRequestkinds().size() != 0
					|| model.getOrigImage().getEffectiveTo() != null){
				nonSelectable.add(model);
			}
			
			/*Market Integration 31/05/2016 SSM2707 Sweta Menon Begin*/
			/*
			 * If the agreement belongs to a Sales Category that can own a
			 * panel, limit the Bulk transfer.
			 */
			// Obtain and set the Sales category for the agreement.
			String salesCategory = (String) getAgreementEntityManager()
					.getAgreementPropertyOfKind(PropertyKindType.SalesCategory,
							model.getAgreementDetails().getAgreementNumber());
			if (salesCategory != null && bcSalesCat.contains(salesCategory)) {
				if (!nonSelectable.contains(model)) {
					nonSelectable.add(model);
				}
			}
			/*Market Integration 31/05/2016 SSM2707 Sweta Menon End*/
		}		
		SRSDataGrid grid = new SRSDataGrid(id, new SRSDataProviderAdapter(
				new SortableListDataProvider<BulkTransferGridModel>(agreements))
				, getAgreementColumns(), getEditState(),nonSelectable){
			private static final long serialVersionUID = 1L;

			@Override
			public void selectItem(IModel itemModel, boolean selected) {				
				//allowing max 50 requests at a time for timeouts
				BulkTransferPanel.this.getSession().getFeedbackMessages().clear();				
				super.selectItem(itemModel, selected);			
				int selectionCount = getSelectionCount();
				if(getSelectionCount() > 50){					
					error("You have selected " + selectionCount + " agreements to update. A max of "+HierarchyGUIController.MAX_SIMULTANEOUS_BRANCH_TRANSFER_REQUESTS+" is allowed, please unselect " + (selectionCount - HierarchyGUIController.MAX_SIMULTANEOUS_BRANCH_TRANSFER_REQUESTS) + " to proceed");
				}
				AjaxRequestTarget target = RequestCycle.get().find(AjaxRequestTarget.class);
				if (target!=null)
					target.add(feedBackPanel);
			}			
		};
		grid.setPreLight(false);
		grid.setCleanSelectionOnPageChange(false);
		grid.setClickRowToSelect(false);
		grid.setAllowSelectMultiple(true);
		grid.setGridWidth(99, GridSizeUnit.PERCENTAGE);
		//grid.setGridWidth(650, GridSizeUnit.PIXELS);
		grid.setRowsPerPage(15);
		grid.setContentHeight(400, SizeUnit.PX);					
		gridUpdateTimer = new AbstractAjaxTimerBehavior(Duration.milliseconds(100)){		
				private static final long serialVersionUID = 1L;	
				@Override
				protected void onTimer(AjaxRequestTarget target) {
					if(!gridUpdated){
						gridUpdated =true;	
						if(FIRST_RETREIVAL_COUNT != null && agreements.size() == FIRST_RETREIVAL_COUNT){
							//get all the agreements from the db and refresh the grid
							updatePanelModel(panelModel,null,null);
							target.add(agreementsGrid);
						}
						gridUpdateTimer.stop(target);
						gridLoadingImage.setVisible(false);
						target.add(gridLoadingImage);	
						exportToExcelButton.setEnabled(true);
						target.add(exportToExcelButton);
						if(pageDataLoaded != null){
							pageDataLoaded.setPageDataLoaded(true);
						}
					}				
				}			
			};		
		grid.add(gridUpdateTimer);		
		return grid;
	}
	
	private WebMarkupContainer createGridLoadingComponent(String id){
		WebMarkupContainer comp = new WebMarkupContainer(id);	
		comp.setOutputMarkupId(true);
		comp.setOutputMarkupPlaceholderTag(true);
		return comp;
	}

	/**
	 * Create the columns used in the grid
	 * @return
	 */
	private List<IGridColumn> getAgreementColumns() {
		Vector<IGridColumn> cols = new Vector<IGridColumn>(7);
		if (!getEditState().isViewOnly() && getEditState() != EditStateType.TERMINATE) {
			SRSGridRowSelectionCheckBox col = new SRSGridRowSelectionCheckBox(
					"checkBox");			
			cols.add(col.setInitialSize(30));
		}
		cols.add(new SRSDataGridColumn<BulkTransferGridModel>(
				"agreementDetails.agreementNumber", new Model("Agreement Number"), "agreementDetails.agreementNumber",
				"agreementDetails.agreementNumber", getEditState()){
				private static final long serialVersionUID = 1L;
				@Override
				public Panel newCellPanel(WebMarkupContainer parent, String componentId, IModel rowModel, String objectProperty, EditStateType state, BulkTransferGridModel data) {
					if(data.getOutstandingRequestkinds().size() != 0){
						setDefaultLabelStyle(PENDING_REQUESTS_STYLE);
					}else{
						setDefaultLabelStyle("");
					}					
					return super.newCellPanel(parent, componentId, rowModel, objectProperty, state, data);
				}													
		}.setInitialSize(115));
		cols.add(new SRSDataGridColumn<BulkTransferGridModel>(
				"agreementDetails.ConsCodeString", new Model("Consultant Code"), "agreementDetails.ConsCodeString",
				"agreementDetails.ConsCodeString", getEditState()){
					private static final long serialVersionUID = 1L;
					@Override
					public Panel newCellPanel(WebMarkupContainer parent, String componentId, IModel rowModel, String objectProperty, EditStateType state, BulkTransferGridModel data) {
						if(data.getOutstandingRequestkinds().size() != 0){
							setDefaultLabelStyle(PENDING_REQUESTS_STYLE);
						}else{
							setDefaultLabelStyle("");
						}					
						return super.newCellPanel(parent, componentId, rowModel, objectProperty, state, data);
					}							
			}.setInitialSize(100));
		cols.add(new SRSDataGridColumn<BulkTransferGridModel>(
				"agreementDetails.name", new Model("Party Name"), "agreementDetails.name",
				"agreementDetails.name", getEditState()){
					private static final long serialVersionUID = 1L;
					@Override
					public Panel newCellPanel(WebMarkupContainer parent, String componentId, IModel rowModel, String objectProperty, EditStateType state, BulkTransferGridModel data) {
						if(data.getOutstandingRequestkinds().size() != 0){
							setDefaultLabelStyle(PENDING_REQUESTS_STYLE);
						}else{
							setDefaultLabelStyle("");
						}					
						return super.newCellPanel(parent, componentId, rowModel, objectProperty, state, data);
					}					
			}.setWrapText(true).setInitialSize(170));
		cols.add(new SRSDataGridColumn<BulkTransferGridModel>(
				"agreementDetails.status", new Model("Agreement Status"), "agreementDetails.status",
				"agreementDetails.status", getEditState()){
					private static final long serialVersionUID = 1L;
					@Override
					public Panel newCellPanel(WebMarkupContainer parent, String componentId, IModel rowModel, String objectProperty, EditStateType state, BulkTransferGridModel data) {
						if(data.getOutstandingRequestkinds().size() != 0){
							setDefaultLabelStyle(PENDING_REQUESTS_STYLE);
						}else{
							setDefaultLabelStyle("");
						}					
						return super.newCellPanel(parent, componentId, rowModel, objectProperty, state, data);
					}							
			}.setInitialSize(110));
		
		cols.add(new SRSDataGridColumn<BulkTransferGridModel>(
				"homeRole.consultantCode", new Model("New Consultant Code"), "homeRole.consultantCode",
				"homeRole.consultantCode", getEditState()){
			private static final long serialVersionUID = 1L;

			@Override
			public Panel newCellPanel(WebMarkupContainer parent, String componentId, IModel rowModel, String objectProperty, EditStateType state, final BulkTransferGridModel data) {
				if(getEditState().isViewOnly() || data.getOutstandingRequestkinds().size() != 0 || data.getOrigImage().getEffectiveTo() != null){
					if(data.getOutstandingRequestkinds().size() != 0){
						setDefaultLabelStyle(PENDING_REQUESTS_STYLE);
					}else{
						setDefaultLabelStyle("");
					}	
					return super.newCellPanel(parent, componentId, rowModel, objectProperty, state,
							data);
				}else{
					TextField field = new TextField("value",new PropertyModel(data,objectProperty));
					field.add(new AttributeModifier("size","17"));
					field.add(new AttributeModifier("maxlength","13"));
					field.add(new AjaxFormComponentUpdatingBehavior("keyup"){						
							private static final long serialVersionUID = 1L;
							@Override
							protected void onUpdate(AjaxRequestTarget target) {
								check13DigitCodeAgainstBranch(data.getAgreementDetails().getAgreementNumber() , data.getHomeRole(),target);							
							}						
						}
					);
				return HelperPanel.getInstance(componentId, field);
				}
			}			
		}.setInitialSize(130));	
		
		return cols;
	}
	
	/**
	 * Checks that the first 3 characters match the branch code selected, this is just to popup a warning message
	 * @param role
	 * @param target
	 */
	private void check13DigitCodeAgainstBranch(long agreementNumber, AgreementHomeRoleDTO data,AjaxRequestTarget target){
		if(data.getConsultantCode() != null && data.getConsultantCode().length() >= 3 
				&& data.getRolePlayerReference() != null && data.getRolePlayerReference().getOid() > 0){
			//first we check if the selected roleplayer is a branch			
			ResultPartyDTO branch = getBranch((ResultPartyDTO)data.getRolePlayerReference());						
			if(branch != null && branch.getExternalReference() != null && branch.getExternalReference().length() == 3){
				//currentBranch = partyManager.findParentOfHierarchyNode(party.getOid(),party.getTypeOid());
				String branchCode = data.getConsultantCode().substring(0,3);				
				if(!branchCode.equals(branch.getExternalReference())){
					if(((ResultPartyDTO)data.getRolePlayerReference()).getTypeOid() == SRSType.UNIT){
						warn("Warning: Agreement "+agreementNumber+" --> Unit selected belongs to Branch ["+branch.getName()+"] with code [" +branch.getExternalReference() +"], the first 3 characters entered ["+branchCode+"] does not match this code");
					}else{					
						warn("Warning: Agreement "+agreementNumber+" --> The first 3 characters entered ["+branchCode+"] does not match the branch's code[" +branch.getExternalReference() + "]");
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
	
	/**
	 * Will get the branch of the node
	 * @param node
	 * @return
	 */
	private ResultPartyDTO getBranch(ResultPartyDTO node){
		ResultPartyDTO branch = null;
		if(((ResultPartyDTO)node).getTypeOid() == SRSType.BRANCH){
			branch = node;
		}else if(node.getTypeOid() == SRSType.UNIT){
			 try {
				branch = getHierarchyGUIController().findParentOfHierarchyNode(node.getOid(),node.getTypeOid());
			} catch (DataNotFoundException e) {
				//no problem, branch will be null
			}
		}
		return branch;
	}
	
	/**
	 * creates the search button
	 * @param id
	 * @return
	 */
	private Button createSearchButton(String id){
		Button ret = new Button(id);
		ret.add(new AjaxFormComponentUpdatingBehavior("click"){			
			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				//do search
				searchWindow.show(target);
			}			
		});	
		if(getEditState().isViewOnly()){
			ret.setVisible(false);
		}
		return ret;
	}
	
	/**
	 * creates the search button
	 * @param id
	 * @return
	 */
	private Button createExportToExcelButton(String id){
		Button ret = new Button(id){
			private static final long serialVersionUID = 1L;
			@Override
			protected void onComponentTag(ComponentTag tag) {
				super.onComponentTag(tag);
				tag.getAttributes().put("type", "submit");
			}			
			@Override
			public void onSubmit() {				
				super.onSubmit();
				try {
					new GridToCSVHelper().createCSVFromDataGrid(agreementsGrid,"agreement_for_node_"+panelModel.getBranchFrom().getBranchCode()+".csv");
				} catch (Exception e) {	
					logger.error("An error occured when trying to generate the csv file",e);
					e.printStackTrace();
					error("An error occured when trying to generate the csv file");
				}
			}			
		};	
		ret.setOutputMarkupId(true);
		ret.setOutputMarkupPlaceholderTag(true);
		//will enable when agreements have all loaded
		ret.setEnabled(false);
		ret.add(new AjaxFormComponentUpdatingBehavior("click"){			
			private static final long serialVersionUID = 1L;
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				//update feedback so busy indicater can disappear and might display error generated above
				target.add(feedBackPanel);
			}			
		});
		if(!getEditState().isViewOnly()){
			ret.setVisible(false);
		}
		return ret;
	}
	
	/**
	 *  creates the node to text field
	 * @param id
	 * @return
	 */
	private TextField createNodeToCodeField(String id){
		final TextField nodeToCodeField = new TextField(id,new PropertyModel(panelModel, "branchTo.externalReference"));
		nodeToCodeField.setLabel(new Model("Node To Code"));
		nodeToCodeField.add(new AjaxFormComponentUpdatingBehavior("keyup") {					
			private static final long serialVersionUID = 1L;
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				String input = (String) nodeToCodeField.getModelObject();
				if(input != null && input.length() >= 3){
					//after 3 chars typed we will search
					//then we update the model and refresh only the label
					updateBranchToSelection(target,input);
				}
			}
		});	
		nodeToCodeField.add(new AjaxFormComponentUpdatingBehavior("blur") {					
			private static final long serialVersionUID = 1L;
			@Override
			protected void onUpdate(AjaxRequestTarget target){
				//make sure code input is valid
				String input = (String) nodeToCodeField.getModelObject();
				updateBranchToSelection(target,input);
			}						
		});
		if(getEditState().isViewOnly()){			
			nodeToCodeField.setVisible(false);			
		}
		nodeToCodeField.setRequired(true);
		validationComponents.add(nodeToCodeField);
		return nodeToCodeField;
	}
	
	
	/**
	 * update the branch to selection using the external ref as a lookup
	 * @param target
	 * @param selected
	 */
	private void updateBranchToSelection(AjaxRequestTarget target,String externalRef){
		if(externalRef.equals(currentNodeExternalRef)){
			//only update on change
			return;
		}	
		currentNodeExternalRef = externalRef;
		ResultPartyDTO selected = null;
		try{
			if(externalRef == null){
				throw new DataNotFoundException();
			}			
			List<ResultPartyDTO> nodes = getHierarchyGUIController().findHierarchyNodeWithExternalReference(externalRef);
			if(nodes == null){
				throw new DataNotFoundException();	
			}
			//update the model with the data						
			if(nodes.size() > 1){
				logger.error("More than one hierarchy node found with external reference " + externalRef);
			}
			selected = nodes.get(0);			
		} catch (DataNotFoundException e) {
			//display that code could not be found
			selected = createCodeNotFoundParty();					
		}		
		updateBranchToSelection(target,selected);
	}
	
	/**
	 * Create an empty ResultPartyDTO with Name set to "Code Not Found"
	 * @return
	 */
	private ResultPartyDTO createCodeNotFoundParty(){
		ResultPartyDTO party = panelModel.getBranchTo();
		party.setName("Code Not Found");
		party.setOid(0);
		return party;
	}
	
	/**
	 * update the branch to selection
	 * @param target
	 * @param selected
	 */
	private void updateBranchToSelection(AjaxRequestTarget target,ResultPartyDTO selected){
		if(selected.getOid() > 0){
			//check that the chosen node is active
			try {
				//validate home is active
				HierarchyNodeDTO branchFrom = panelModel.getBranchFrom();				
				getHierarchyGUIController().validateHomeToChosen(new ObjectReference(ComponentEnum._PARTY,branchFrom.getType().getOid(),branchFrom.getOid()),selected);
				//validate home is in same channel								
			} catch (ValidationException e) {
				//display error and change selection to Code Not found
				selected = createCodeNotFoundParty();
				for(String error : e.getErrorMessages()){
					error(error);
				}				
			}		
		}		
		panelModel.setBranchTo(selected);
		//update all the roles on the page
		boolean refreshGrid = false;
		for(BulkTransferGridModel model : panelModel.getGridData()){
			ResultPartyDTO branch = getBranch(panelModel.getBranchTo());
			if(branch != null && branch.getExternalReference().length() == 3 && model.getHomeRole().getConsultantCode() != null){
				refreshGrid = true;
				String endOldCode = model.getHomeRole().getConsultantCode().substring(3, model.getHomeRole().getConsultantCode().length());
				model.getHomeRole().setConsultantCode(branch.getExternalReference() + endOldCode);
			}
			model.getHomeRole().setRolePlayerReference(panelModel.getBranchTo());
		}			
		if(target != null){
			target.add(nodeToLabel);	
			if(refreshGrid){
				target.add(agreementsGrid);
			}
			if(feedBackPanel != null){
				target.add(feedBackPanel);
			}
		}		
	}	
	
	
	/**
	 * create a new node form with validations attached
	 * @param id
	 * @return
	 */
	private Form createTransferForm(String id) {
		Form form = new TransferForm(id);		
		return form;		
	}

	
	private ModalWindow createSearchWindow(String id) {

		ContextSearchPopUp popUp = new ContextSearchPopUp() {
			private static final long serialVersionUID = 1L;

			@Override
			public ContextType getContextType() {
				return ContextType.PARTY_ORGANISATION_ONLY;
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
					if(!(resultPartyDTO.getTypeOid() == SRSType.BRANCH
							|| resultPartyDTO.getTypeOid() == SRSType.UNIT)){
						error("Only Branches/Units must be selected");		
						if(feedBackPanel != null){
							target.add(feedBackPanel);
							break;
						}
					}					
					updateBranchToSelection(target, resultPartyDTO);
					if(target != null && codeField != null){
						target.add(codeField);
					}
				}				
			}
		};		
		ModalWindow win = popUp.createModalWindow(id);
//		win.setPageMapName("bulkTransferSearchPageMap");
		return win;	
	}


	public Class getPanelClass() {
		return BulkTransferPanel.class;
	}

	/**
	 * Set the feedback panel to use for errors
	 * @param feedBackPanel
	 */
	public void setFeedBackPanel(FeedbackPanel feedBackPanel) {
		this.feedBackPanel = feedBackPanel;
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

	/* Market Integration 31/05/2016 SSM2707 Sweta Menon Begin*/
	private IAgreementEntityManager getAgreementEntityManager() {
		if (agmtEntityManager == null) {
			try {
				agmtEntityManager = ServiceLocator
						.lookupService(IAgreementEntityManager.class);
			} catch (NamingException e) {
				throw new CommunicationException(e);
			}
		}
		return agmtEntityManager;
	}
	
	private IRatingEntityManager getRatingEntityManager() {
		if (ratingEntityManager == null) {
			try {
				ratingEntityManager = ServiceLocator
						.lookupService(IRatingEntityManager.class);
			} catch (NamingException e) {
				throw new CommunicationException(e);
			}
		}
		return ratingEntityManager;
	}
	/* Market Integration 31/05/2016 SSM2707 Sweta Menon End*/
}
