package za.co.liberty.web.pages.core;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.naming.NamingException;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import com.inmethod.grid.IGridColumn;
import com.inmethod.grid.SizeUnit;

import za.co.liberty.business.guicontrollers.core.ICoreTransferGuiController;
import za.co.liberty.dto.agreement.AgreementDTO;
import za.co.liberty.dto.agreement.AgreementHomeRoleDTO;
import za.co.liberty.dto.agreement.core.AdvisoryFeeDTO;
import za.co.liberty.dto.agreement.core.CoreConsultantDto;
import za.co.liberty.dto.agreement.core.CoreTransferDto;
import za.co.liberty.dto.contracting.ResultAgreementDTO;
import za.co.liberty.dto.contracting.ResultPartyDTO;
import za.co.liberty.dto.gui.context.ResultContextItemDTO;
import za.co.liberty.exceptions.data.DataNotFoundException;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.helpers.lookup.ServiceLocator;
import za.co.liberty.interfaces.core.CoreTransferRequestType;
import za.co.liberty.web.data.enums.ContextType;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.pages.core.model.CoreTransferPageModel;
import za.co.liberty.web.pages.panels.BasePanel;
import za.co.liberty.web.pages.panels.HelperPanel;
import za.co.liberty.web.pages.search.ContextSearchPopUp;
import za.co.liberty.web.wicket.markup.html.grid.COREDataGrid;
import za.co.liberty.web.wicket.markup.html.grid.COREDataGrid.GridSizeUnit;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGridColumn;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataProviderAdapter;
import za.co.liberty.web.wicket.markup.html.grid.SRSGridRowSelectionCheckBox;
import za.co.liberty.web.wicket.markup.repeater.data.SortableListDataProvider;

/**
 * Base panel for Core Transfers 
 *
 */
public abstract class AbstractTransferPanel extends BasePanel {

	private static final long serialVersionUID = 1L;

	protected Form searchForm;

	protected FeedbackPanel feedbackPanel;

	protected CoreTransferPageModel pageModel;

	protected static final String COMP_SEARCH_FORM = "searchForm";

	protected transient ICoreTransferGuiController guiController;

	protected CoreHelper coreHelper;

	protected ModalWindow searchWindow;

	protected ResultAgreementDTO resultAgreementDTO;

	protected ResultPartyDTO partyDto;

	protected CoreTransferDto fromTransferDto;

	protected CoreTransferDto toTransferDto;

	protected CoreTransferDto coreTransferDto;

	protected COREDataGrid transferGrid;

	protected List<CoreTransferDto> coreTransferDtoList;
	protected List<CoreTransferDto> segTransferDtoList;
	
	protected ModalWindow uploadWindow;

	protected String gridType;

	protected COREDataGrid grid;

	private AgreementDTO agreementDTO = null;

	private static final String INCORRECT_ROWS_STYLE = "color:#FF0000;";

	private static final String DEFAULT_ROWS_STYLE = "color:#000000;";
	
	private static final String WARNING_ROWS_STYLE = "color:#CD950C;";

	protected AdstractTransferPage adstractTransferPage;

	protected boolean formInitialised = false;
	
	protected ResultAgreementDTO agreementDto = null;
	
	

	protected Form form = null;
	EditStateType editState;

	private static ArrayList<AdvisoryFeeDTO> homeTypes = new ArrayList<AdvisoryFeeDTO>();
	static {
		homeTypes.add(new AdvisoryFeeDTO("Yes", true));
		homeTypes.add(new AdvisoryFeeDTO("No", false));
	}

	/**
	 * Default contructor
	 * 
	 * @param tab_panel_id
	 * @param model
	 * @param editState
	 * @param feedBackPanel
	 * @param guiController
	 * @param page
	 */
	public AbstractTransferPanel(String tab_panel_id,
			CoreTransferPageModel model, EditStateType editState,
			FeedbackPanel feedBackPanel,
			AdstractTransferPage page) {
		super(tab_panel_id, editState);
		pageModel = model;
		this.feedbackPanel = feedBackPanel;
		this.adstractTransferPage = page;
		this.editState=editState;
		resultAgreementDTO = new ResultAgreementDTO();

		// blank row added in pageModel to display initial blank row
		getGUIController();
		if(!editState.isViewOnly()){
			coreTransferDtoList = pageModel.getCoreTransferDto();
			coreTransferDtoList = new ArrayList<CoreTransferDto>();
			coreTransferDto = new CoreTransferDto();
			coreTransferDtoList.add(0, coreTransferDto);
			pageModel.setCoreTransferDto(coreTransferDtoList);	
			segTransferDtoList = new ArrayList<CoreTransferDto>();
			pageModel.setSegTransferDto(segTransferDtoList);
		}else{
			coreTransferDto=model.getCoreTransferDto().get(0);
			if(coreTransferDto.getTransferTypeInd()==null){
				pageModel.setRequestCategory(CoreTransferRequestType.Service_Transfer);
			}
			else if(coreTransferDto.getTransferTypeInd().equalsIgnoreCase("S")){
				pageModel.setRequestCategory(CoreTransferRequestType.Service_Transfer);
			}else if(coreTransferDto.getTransferTypeInd().equalsIgnoreCase("C")){
				pageModel.setRequestCategory(CoreTransferRequestType.Service_Commision_Transfer);
			}
		}
	}

	@Override
	protected void onBeforeRender() {
		super.onBeforeRender();
		add(searchForm = createSearchForm("searchForm"));
	}

	@SuppressWarnings("unchecked")
	public Form createSearchForm(String id) {
		if (formInitialised)
			return form;
		form = new Form(id) {
			private static final long serialVersionUID = -6308633210871154462L;

			@Override
			protected void onSubmit() {
				CoreButtonsPanel<CoreTransferDto> buttonPanel = (CoreButtonsPanel<CoreTransferDto>) adstractTransferPage
						.getButtonPanel();
				buttonPanel.setTransferGrid(transferGrid);

				refreshPageModel();
				super.onSubmit();
				
			}

			@Override
			protected void onError() {
			}
		};
		form.add(createRemoveButton("removeButton"));
		form.add(createAddButton("addButton"));
		return form;
	}

	/**
	 * Create the add button
	 * 
	 * @param id
	 * @return
	 */
	protected Button createAddButton(String id) {
		final Button button = new Button(id);
		button.add(new AjaxFormComponentUpdatingBehavior("click") {
			private static final long serialVersionUID = 0L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				//check for maximum allowed rows
				if (gridType.equals("contract")
						&& coreTransferDtoList.size() >= 10) {
					feedbackPanel
							.error("This exceeded the maximum allowable limit of 10 rows");
					target.add(feedbackPanel);
					return;
				}else if (!gridType.equals("contract")
						&& coreTransferDtoList.size() >= CoreHelper.pageSize) {
					feedbackPanel
							.error("This exceeded the maximum allowable limit of "+ CoreHelper.pageSize +" rows");
					target.add(feedbackPanel);
					return;
				}
				coreTransferDto = new CoreTransferDto();
				coreTransferDtoList.add(coreTransferDto);
				target.add(transferGrid);
				target.add(feedbackPanel);
			}
		});
		return button;
	}

	/**
	 * Create the remove button
	 * @param id
	 * @return
	 */
	protected Button createRemoveButton(String id) {
		final Button button = new Button(id);
		button.add(new AjaxFormComponentUpdatingBehavior("click") {
			private static final long serialVersionUID = 0L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				List<Object> selections = transferGrid.getSelectedItemObjects();
				boolean found = false;
				int selectedRowCount = selections.size();
				
				List<CoreTransferDto> segList=pageModel.getSegTransferDto();
				if(segList!=null && segList.size() > 0 && coreTransferDtoList.size() - selectedRowCount <= 0){
				
				List<CoreTransferDto> coreTransferList1=pageModel.getCoreTransferDto();
				coreTransferList1.removeAll(coreTransferList1);
				int count=1;
				for (CoreTransferDto dto : segList) {
					pageModel.getCoreTransferDto().add(dto);
					if(count++ >=CoreHelper.pageSize)
						break;
				}
				segList.removeAll(coreTransferList1);
				
				getCoreHelper().validateSegmentGrid(pageModel, feedbackPanel);
				target.add(transferGrid);
				target.add(feedbackPanel);
				}else{
				
				
				if (coreTransferDtoList.size() - selectedRowCount <= 0) {
					feedbackPanel
							.error("Restricted to at least one row at any point of time");
					target.add(transferGrid);
					target.add(feedbackPanel);
					transferGrid.resetSelectedItems();
					return;
				}
				for (Object selection : selections) {
					pageModel.getCoreTransferDto().remove(selection);
					// pageModel.getAgreementDTO().getCurrentAndFutureHomeRoles().remove(selection);
				}
				}
				transferGrid.resetSelectedItems();
				target.add(transferGrid);
				target.add(feedbackPanel);
			}
		});
		return button;
	}

	/**
	 * Crete the search window
	 * 
	 * @param id
	 * @return
	 */
	protected ModalWindow createSearchWindow(String id) {
		ContextSearchPopUp popUp = new ContextSearchPopUp() {
			@Override
			public ContextType getContextType() {
				return ContextType.AGREEMENT_ONLY;
			}

			@Override
			public void doProcessSelectedItems(AjaxRequestTarget target,
					ArrayList<ResultContextItemDTO> selectedItemList) {
				if (selectedItemList.size() == 0) {
					// Nothing was selected
					return;
				}
							
				// adding party to agreement
				resultAgreementDTO = (selectedItemList.get(0))
						.getAgreementDTO();
				partyDto = (selectedItemList.get(0)).getPartyDTO();
				
				// Check valid
				if (resultAgreementDTO == null || resultAgreementDTO.getAgreementNumber() == null 
						|| resultAgreementDTO.getAgreementNumber().equals(0L)) {
					warn("Standalone parties can not be selected");
					target.add(feedbackPanel);
					return;
				}
				
				
				// Retrieve the cons code dto
				CoreConsultantDto consultantDto = pageModel.getConsultantMap().get(resultAgreementDTO.getConsultantCode());
				if (consultantDto == null) {
					consultantDto = getGUIController().getCoreConsultantDto(resultAgreementDTO, partyDto);
					pageModel.getConsultantMap().put(consultantDto.getConsultantCode(), consultantDto);
				}
				
				
				if (fromTransferDto != null) {//check for wheather "From Search" button is pressed				
					getCoreHelper().setFromConsultant(consultantDto, fromTransferDto);
				} else if (toTransferDto != null) {//check for wheather "To Search" button is pressed
					getCoreHelper().setToConsultant(consultantDto, toTransferDto);
				}
				
				refreshPageModel();
				target.add(transferGrid);
			}
		};
		ModalWindow win = popUp.createModalWindow(id);
//		win.setPageMapName("homeSearchPageMap");
		return win;
	}
	
	protected COREDataGrid createTransferGrid(String id, String gridType) {
		List<CoreTransferDto> coreTransferList = pageModel.getCoreTransferDto();
		this.gridType = gridType;
		if (coreTransferList == null) {
			coreTransferList = new ArrayList<CoreTransferDto>();
		}
		List<AgreementHomeRoleDTO> noneSelectable = null;
		grid = new COREDataGrid(id, new SRSDataProviderAdapter(
				new SortableListDataProvider<CoreTransferDto>(coreTransferList)),
				getColumns(gridType), EditStateType.ADD, noneSelectable);
		
		grid.setCleanSelectionOnPageChange(false);
		grid.setClickRowToSelect(false);
		grid.setAllowSelectMultiple(true);
		grid.setGridWidth(99, GridSizeUnit.PERCENTAGE);
	
		
		//grid.setAllowSelectMultiple(false);
		
		grid.setOutputMarkupId(true);
		if (gridType.equals("segmented")) {
			grid.setRowsPerPage(CoreHelper.pageSize);
			grid.setContentHeight(400, SizeUnit.PX);
		} else {
			grid.setContentHeight(235, SizeUnit.PX);
			grid.setRowsPerPage(10);
		}
		return grid;
	}

	private List<IGridColumn> getColumns(String gridType) {
		if (gridType.equals("segmented")) {
			return getSegmentedColumns();
		} else {
			return getContractColumns();
		}
	}

	private List<IGridColumn> getSegmentedColumns() {
		Vector<IGridColumn> cols = new Vector<IGridColumn>(12);

		// add in the selection column
		SRSGridRowSelectionCheckBox col = new SRSGridRowSelectionCheckBox(
				"checkBox");
	
		cols.add(col.setInitialSize(30));
		
		cols.add(new SRSDataGridColumn<CoreTransferDto>("rowCount",
				new Model("Row Count"),"rowCount",
				"rowCount", getEditState()) {
			private static final long serialVersionUID = 1L;

			@Override
			public Panel newCellPanel(WebMarkupContainer parent,
					String componentId, IModel rowModel, String objectProperty,
					EditStateType state, CoreTransferDto data) {
				if (data.isRowStatus()) {
					setDefaultLabelStyle(DEFAULT_ROWS_STYLE);
				} else {
					setDefaultLabelStyle(INCORRECT_ROWS_STYLE);
				}
				return super.newCellPanel(parent, componentId, rowModel,
						objectProperty, state, data);
			}
		}.setInitialSize(64));

		// add in the From Consultant name column(Display only col)
		cols.add(new SRSDataGridColumn<CoreTransferDto>("fromConsultantName",
				new Model("From Consultant Name"), "fromConsultantName",
				"fromConsultantName", getEditState()) {
			private static final long serialVersionUID = 1L;

			@Override
			public Panel newCellPanel(WebMarkupContainer parent,
					String componentId, IModel rowModel, String objectProperty,
					EditStateType state, CoreTransferDto data) {
				if (data.isRowStatus()) {
					setDefaultLabelStyle(DEFAULT_ROWS_STYLE);
				} else {
					setDefaultLabelStyle(INCORRECT_ROWS_STYLE);
				}
				return super.newCellPanel(parent, componentId, rowModel,
						objectProperty, state, data);
			}
			
		}.setInitialSize(220));

		// add in the From search Button
		cols.add(new SRSDataGridColumn<CoreTransferDto>("searchFromParty",
				new Model("Search"), "searchFromParty", getEditState()) {
			private static final long serialVersionUID = 1L;

			@Override
			public Panel newCellPanel(WebMarkupContainer parent,
					String componentId, IModel rowModel, String objectProperty,
					EditStateType state, final CoreTransferDto data) {

				Button searchButton = new Button("value", new Model("Search"));
				if(editState.isViewOnly()){
					searchButton.setEnabled(false);
				}
				searchButton.add(new AjaxFormComponentUpdatingBehavior(
						"click") {
					private static final long serialVersionUID = 1L;

					@Override
					protected void onUpdate(AjaxRequestTarget target) {
						fromTransferDto = data;
						toTransferDto = null;
						searchWindow.show(target);
					}
				});
				return HelperPanel.getInstance(componentId, searchButton);
			}
		}.setInitialSize(60));

		// add in the From Consultant code Column
		cols.add(createColumnFromConsultantCode().setInitialSize(130));

		// add in the From Consultant status Column
		cols.add(new SRSDataGridColumn<CoreTransferDto>("fromConsultantStatus",
				new Model("From Consultant Status"), "fromConsultantStatus",
				"fromConsultantStatus", getEditState()) {
			private static final long serialVersionUID = 1L;

			@Override
			public Panel newCellPanel(WebMarkupContainer parent,
					String componentId, IModel rowModel, String objectProperty,
					EditStateType state, CoreTransferDto data) {
				if (data.isRowStatus()) {
					setDefaultLabelStyle(DEFAULT_ROWS_STYLE);
				} else {
					setDefaultLabelStyle(INCORRECT_ROWS_STYLE);
				}
			
				return super.newCellPanel(parent, componentId, rowModel,
						objectProperty, state, data);
			}
		}.setInitialSize(138));

		// add in the To Consultant Name Column
		cols.add(new SRSDataGridColumn<CoreTransferDto>("toConsultantName",
				new Model("To Consultant Name"), "toConsultantName",
				"toConsultantName", getEditState()) {
			private static final long serialVersionUID = 1L;

			@Override
			public Panel newCellPanel(WebMarkupContainer parent,
					String componentId, IModel rowModel, String objectProperty,
					EditStateType state, CoreTransferDto data) {
				if (data.isRowStatus()) {
					setDefaultLabelStyle(DEFAULT_ROWS_STYLE);
				} else {
					setDefaultLabelStyle(INCORRECT_ROWS_STYLE);
				}
				return super.newCellPanel(parent, componentId, rowModel,
						objectProperty, state, data);
			}
		}.setInitialSize(220));

		// add in search button
		cols.add(new SRSDataGridColumn<CoreTransferDto>("searchParty",
				new Model("Search"), "searchParty", getEditState()) {
			private static final long serialVersionUID = 1L;

			@Override
			public Panel newCellPanel(WebMarkupContainer parent,
					String componentId, IModel rowModel, String objectProperty,
					EditStateType state, final CoreTransferDto data) {
				Button searchButton = new Button("value", new Model("Search"));
				if(editState.isViewOnly()){
					searchButton.setEnabled(false);
				}
				searchButton.add(new AjaxFormComponentUpdatingBehavior(
						"click") {
					private static final long serialVersionUID = 1L;

					@Override
					protected void onUpdate(AjaxRequestTarget target) {
						fromTransferDto = null;
						toTransferDto = data;
						searchWindow.show(target);
					}
				});
				return HelperPanel.getInstance(componentId, searchButton);
			}
		}.setInitialSize(60));
		
		// add in the To Consultant Code Column
		cols.add(createColumnToConsultantCode().setInitialSize(120));

		// add in the To Consultant Status Column
		cols.add(createColumnToConsultantStatus().setInitialSize(125));

		// add in the Contract Number Column
		cols.add(createColumnContractNumber());
		
		cols.add(new SRSDataGridColumn<CoreTransferDto>("advisoryFeeIndicator",
				new Model("Advisory Fee"), "advisoryFeeIndicator",
				"advisoryFeeIndicator", getEditState()) {
			private static final long serialVersionUID = 1L;

			@Override
			public Panel newCellPanel(WebMarkupContainer parent,
					String componentId, IModel rowModel, String objectProperty,
					EditStateType state, final CoreTransferDto data) {
				HelperPanel dropdown = createDropdownField("Node Type",
						componentId, new PropertyModel(data, objectProperty) {
							@Override
							public Object getObject() {
								Object obj = super.getObject();
								boolean id = false;
								if (obj != null)
									id = Boolean.parseBoolean(obj.toString());
								
								for (AdvisoryFeeDTO type : homeTypes) {
									if (type.getOid() == id) {
										return type;
									}
								}
								return null;
							}

							@Override
							public void setObject(Object arg0) {
								if(arg0!=null )
									super.setObject(((AdvisoryFeeDTO) arg0).getOid());
								else 
									super.setObject(null);
							}
						}, homeTypes, new ChoiceRenderer("name", "oid"),
						null, false, false, new EditStateType[] {
								EditStateType.ADD, EditStateType.VIEW,
								EditStateType.MODIFY });

				if(editState.isViewOnly()){
					
				}else{
				DropDownChoice dropdownComp = (DropDownChoice) dropdown
						.getEnclosedObject();
				dropdownComp.add(new AjaxFormComponentUpdatingBehavior(
						"change") {
					private static final long serialVersionUID = 1L;

					@Override
					protected void onUpdate(AjaxRequestTarget target) {
						//System.out.println(data);
					}
				});
				dropdownComp.setNullValid(false);
				dropdownComp.setOutputMarkupId(true);
				}
				return dropdown;
			}
				
		}.setInitialSize(75));
		
		
		cols.add(new SRSDataGridColumn<CoreTransferDto>("errorMsg",
				new Model("Error Code"), "errorMsg",
				"errorMsg", getEditState()) {
			private static final long serialVersionUID = 1L;

			@Override
			public Panel newCellPanel(WebMarkupContainer parent,
					String componentId, IModel rowModel, String objectProperty,
					EditStateType state, CoreTransferDto data) {
				if (data.isRowStatus()) {
					setDefaultLabelStyle(DEFAULT_ROWS_STYLE);
				} else {
					setDefaultLabelStyle(INCORRECT_ROWS_STYLE);
				}
				return super.newCellPanel(parent, componentId, rowModel,
						objectProperty, state, data);
			}
			
		}.setInitialSize(135));
		
		return cols;
	}

	private List<IGridColumn> getContractColumns() {
		Vector<IGridColumn> cols = new Vector<IGridColumn>(11);

		// add in the selection column
		SRSGridRowSelectionCheckBox col = new SRSGridRowSelectionCheckBox(
				"checkBox");
		cols.add(col.setInitialSize(30));

		// add in the From Consultant name column(Display only col)
		cols.add(new SRSDataGridColumn<CoreTransferDto>("fromConsultantName",
				new Model("From Consultant Name"), 
				"fromConsultantName", getEditState()) {
			private static final long serialVersionUID = 1L;

			@Override
			public Panel newCellPanel(WebMarkupContainer parent,
					String componentId, IModel rowModel, String objectProperty,
					EditStateType state, CoreTransferDto data) {
				if (data.isRowStatus()) {
					setDefaultLabelStyle(DEFAULT_ROWS_STYLE);
				} else {
					setDefaultLabelStyle(INCORRECT_ROWS_STYLE);
				}
				return super.newCellPanel(parent, componentId, rowModel,
						objectProperty, state, data);
			}
		}.setInitialSize(245));

		// add in the From search Button
		cols.add(new SRSDataGridColumn<CoreTransferDto>("searchFromParty",
				new Model("Search"), "searchFromParty", getEditState()) {
			private static final long serialVersionUID = 1L;

			@Override
			public Panel newCellPanel(WebMarkupContainer parent,
					String componentId, IModel rowModel, String objectProperty,
					EditStateType state, final CoreTransferDto data) {
				Button searchButton = new Button("value", new Model("Search"));
				searchButton.add(new AjaxFormComponentUpdatingBehavior(
						"click") {
					private static final long serialVersionUID = 1L;

					@Override
					protected void onUpdate(AjaxRequestTarget target) {
						fromTransferDto = data;
						toTransferDto = null;
						searchWindow.show(target);
					}
				});
				return HelperPanel.getInstance(componentId, searchButton);
			}

		}.setInitialSize(67));

		// add in the From Consultant code Column
		cols.add(createContractColumnToConsultantCode());
		

		// add in the From Consultant status Column
		cols.add(new SRSDataGridColumn<CoreTransferDto>("fromConsultantStatus",
				new Model("From Consultant Status"), 
				"fromConsultantStatus", getEditState()) {
			private static final long serialVersionUID = 1L;

			@Override
			public Panel newCellPanel(WebMarkupContainer parent,
					String componentId, IModel rowModel, String objectProperty,
					EditStateType state, CoreTransferDto data) {
				if (data.isRowStatus()) {
					setDefaultLabelStyle(DEFAULT_ROWS_STYLE);
				} else {
					setDefaultLabelStyle(INCORRECT_ROWS_STYLE);
				}
				if(data.getFromConsultantStatus()!=null && data.getFromConsultantStatus().contains("old")){
					setDefaultLabelStyle(WARNING_ROWS_STYLE);
				}
				return super.newCellPanel(parent, componentId, rowModel,
						objectProperty, state, data);
			}
		}.setInitialSize(140));

		// add in the To Consultant Name Column
		cols.add(new SRSDataGridColumn<CoreTransferDto>("toConsultantName",
				new Model("To Consultant Name"),
				"toConsultantName", getEditState()) {
			private static final long serialVersionUID = 1L;

			@Override
			public Panel newCellPanel(WebMarkupContainer parent,
					String componentId, IModel rowModel, String objectProperty,
					EditStateType state, CoreTransferDto data) {
				if (data.isRowStatus()) {
					setDefaultLabelStyle(DEFAULT_ROWS_STYLE);
				} else {
					setDefaultLabelStyle(INCORRECT_ROWS_STYLE);
				}
				return super.newCellPanel(parent, componentId, rowModel,
						objectProperty, state, data);
			}
		}.setInitialSize(245));

		// add in search button
		cols.add(new SRSDataGridColumn<CoreTransferDto>("searchParty",
				new Model("Search"), "searchParty", getEditState()) {
			private static final long serialVersionUID = 1L;

			@Override
			public Panel newCellPanel(WebMarkupContainer parent,
					String componentId, IModel rowModel, String objectProperty,
					EditStateType state, final CoreTransferDto data) {
				Button searchButton = new Button("value", new Model("Search"));
				searchButton.add(new AjaxFormComponentUpdatingBehavior(
						"click") {
					private static final long serialVersionUID = 1L;

					@Override
					protected void onUpdate(AjaxRequestTarget target) {
						fromTransferDto = null;
						toTransferDto = data;
						searchWindow.show(target);
					}
				});
				return HelperPanel.getInstance(componentId, searchButton);
			}
		}.setInitialSize(67));

		// add in the To Consultant Code Column
		cols.add(createColumnToConsultantCode().setInitialSize(135));

		// add in the To Consultant Status Column
		cols.add(createColumnToConsultantStatus().setInitialSize(130));

		// add in the Contract Number Column
		cols.add(createColumnContractNumber());
		
		cols.add(createColumnPriBibLifeNumber());
		
		cols.add(new SRSDataGridColumn<CoreTransferDto>("errorMsg",
				new Model("Error Code"), "errorMsg",
				"errorMsg", getEditState()) {
			private static final long serialVersionUID = 1L;

			@Override
			public Panel newCellPanel(WebMarkupContainer parent,
					String componentId, IModel rowModel, String objectProperty,
					EditStateType state, CoreTransferDto data) {
			
					setDefaultLabelStyle(INCORRECT_ROWS_STYLE);
				return super.newCellPanel(parent, componentId, rowModel,
						objectProperty, state, data);
			}
			
		}.setInitialSize(170));
		
		for (IGridColumn c1 : cols) {
			//System.out.println("c1 "+c1.getId());
		}
		return cols;
	}

	protected CoreHelper getCoreHelper() {
		if (coreHelper == null) {
			try {
				coreHelper = new CoreHelper();
			} catch (Exception e) {
			}
		}
		return coreHelper;
	}

	

	
	/**
	 * Only new objects in SRSGrid will be rendered through "target.addComponent". 
	 * The Method below replacing all the objects with new ones,
	 * while keeping the same data.*/  
	protected void refreshPageModel() {
		List<CoreTransferDto> coreDtoList = new ArrayList<CoreTransferDto>();
		CoreTransferDto tempDto = null;
		List<CoreTransferDto> coreDtoList1 = pageModel.getCoreTransferDto();
		for (CoreTransferDto coreDto : coreDtoList1) {
			tempDto = new CoreTransferDto(coreDto);
			coreDtoList.add(tempDto);
		}
		pageModel.getCoreTransferDto()
				.removeAll(pageModel.getCoreTransferDto());
		for (CoreTransferDto coreDto : coreDtoList) {
			tempDto = new CoreTransferDto(coreDto);
			pageModel.getCoreTransferDto().add(tempDto);
		}
	}

	/**
	 * Create the contract number column
	 * 
	 * @return
	 */
	private IGridColumn createColumnContractNumber() {
		IGridColumn c = new SRSDataGridColumn<CoreTransferDto>("contractNumber",
				new Model("Contract Number"), "contractNumber",
				"contractNumber", getEditState()) {
			@Override
			public Panel newCellPanel(WebMarkupContainer parent,
					String componentId, IModel rowModel, String objectProperty,
					EditStateType state, final CoreTransferDto data) {
				TextField contractNumber = new TextField("value",
						new PropertyModel(data, objectProperty));
				contractNumber.add(new AttributeModifier("size", "19"));
				contractNumber.add(new AttributeModifier("maxlength",
						"15"));
				if(editState.isViewOnly()){
					contractNumber.setEnabled(false);
				}
				contractNumber.setLabel(new Model("Parent End Date"));
				contractNumber.setOutputMarkupId(true);
				contractNumber.add(new AjaxFormComponentUpdatingBehavior(
						"change") {
					@Override
					protected void onUpdate(AjaxRequestTarget target) {
					}
				});
				HelperPanel panel = HelperPanel.getInstance(componentId,
						contractNumber, false);
				panel.setOutputMarkupId(true);
				// addToHomeRowComponentsList(data,panel,"effectiveTo");
				return panel;
			}
		}.setInitialSize(115);
		return c;
	}
	
	/**
	 * Create the contract number column
	 * 
	 * @return
	 */
	private IGridColumn createColumnPriBibLifeNumber() {
		IGridColumn c = new SRSDataGridColumn<CoreTransferDto>("priBibLife",
				new Model("Personal Reference"), "priBibLife",
				"priBibLife", getEditState()) {
			@Override
			public Panel newCellPanel(WebMarkupContainer parent,
					String componentId, IModel rowModel, String objectProperty,
					EditStateType state, final CoreTransferDto data) {
				TextField priBiblifeNumber = new TextField("value",
						new PropertyModel(data, objectProperty));
				priBiblifeNumber.add(new AttributeModifier("size", "19"));
				priBiblifeNumber.add(new AttributeModifier("maxlength",
						"14"));
				if(editState.isViewOnly()){
					priBiblifeNumber.setEnabled(false);
				}
				priBiblifeNumber.setLabel(new Model("Personal Reference"));
				priBiblifeNumber.setOutputMarkupId(true);
				priBiblifeNumber.add(new AjaxFormComponentUpdatingBehavior(
						"change") {
					@Override
					protected void onUpdate(AjaxRequestTarget target) {
					}
				});
				HelperPanel panel = HelperPanel.getInstance(componentId,
						priBiblifeNumber, false);
				panel.setOutputMarkupId(true);
				// addToHomeRowComponentsList(data,panel,"effectiveTo");
				return panel;
			}
		}.setInitialSize(115);
		return c;
	}
	
	/**
	 * Create the ToConsultantStatus column
	 * 
	 * @return
	 */
	private SRSDataGridColumn<CoreTransferDto> createColumnToConsultantStatus() {
		return new SRSDataGridColumn<CoreTransferDto>("toConsultantStatus",
				new Model("To Consultant Status"), "toConsultantStatus",
				"toConsultantStatus", getEditState()) {
			private static final long serialVersionUID = 1L;

			@Override
			public Panel newCellPanel(WebMarkupContainer parent,
					String componentId, IModel rowModel, String objectProperty,
					EditStateType state, CoreTransferDto data) {
				if (data.isRowStatus()) {
					setDefaultLabelStyle(DEFAULT_ROWS_STYLE);
				} else {
					setDefaultLabelStyle(INCORRECT_ROWS_STYLE);
				}
				return super.newCellPanel(parent, componentId, rowModel,
						objectProperty, state, data);
			}
		};
	}
	
	

	

	/**
	 * Create the ToConsultantCode column
	 * 
	 * @return
	 */
	private SRSDataGridColumn<CoreTransferDto> createColumnToConsultantCode() {
		return new SRSDataGridColumn<CoreTransferDto>("toConsultantCode",
				new Model("To Consultant Code"), "toConsultantCode",
				"toConsultantCode", getEditState()) {
			
			private static final long serialVersionUID = 1L;
			private  NumberFormat consultantFormatter = new DecimalFormat("0000000000000");
			
			@Override
			public Panel newCellPanel(WebMarkupContainer parent,
					String componentId, IModel rowModel, String objectProperty,
					EditStateType state, CoreTransferDto data) {
				if (data.isRowStatus()) {
					setDefaultLabelStyle(DEFAULT_ROWS_STYLE);
				} else {
					setDefaultLabelStyle(INCORRECT_ROWS_STYLE);
				}
				return super.newCellPanel(parent, componentId, rowModel,
						objectProperty, state, data);
			}
			
			/**
			 * Get the string value from the object
			 * 
			 * @param value
			 * @return
			 */
			public String newCellPanelValue(Object value) {
				if (!(value instanceof Long) || value == null) {
					return "";
				}
				return consultantFormatter.format((Long)value);
			}
		};
	}
	

	/**
	 * Create theToConsultantCode column
	 * 
	 * @return
	 */
	private SRSDataGridColumn<CoreTransferDto> createColumnFromConsultantCode() {
	
		return new SRSDataGridColumn<CoreTransferDto>("fromConsultantCode",
				new Model("From Consultant Code"), "fromConsultantCode",
				"fromConsultantCode", getEditState()) {
			
			private static final long serialVersionUID = 1L;
			private  NumberFormat consultantFormatter = new DecimalFormat("0000000000000");
			
			@Override
			public Panel newCellPanel(WebMarkupContainer parent,
					String componentId, IModel rowModel, String objectProperty,
					EditStateType state, CoreTransferDto data) {
				if (data.isRowStatus()) {
					setDefaultLabelStyle(DEFAULT_ROWS_STYLE);
				} else {
					setDefaultLabelStyle(INCORRECT_ROWS_STYLE);
				}
				return super.newCellPanel(parent, componentId, rowModel,
						objectProperty, state, data);
			}
			
			/**
			 * Get the string value from the object
			 * 
			 * @param value
			 * @return
			 */
			public String newCellPanelValue(Object value) {
				if (!(value instanceof Long) || value == null) {
					return "";
				}
				return consultantFormatter.format((Long)value);
			}
		};
	}
	
	
	private IGridColumn createContractColumnToConsultantCode() {

		IGridColumn c = new SRSDataGridColumn<CoreTransferDto>("fromConsultantCode",
				new Model("From Consultant Code"), "fromConsultantCode",
				"fromConsultantCode", getEditState()) {
			@Override
			public Panel newCellPanel(WebMarkupContainer parent,
					String componentId, IModel rowModel, String objectProperty,
					EditStateType state, final CoreTransferDto data) {
				TextField fromConsultant = new TextField("value",
						new PropertyModel(data, objectProperty));
				fromConsultant.add(new AttributeModifier("size", "16"));
				fromConsultant.add(new AttributeModifier("maxlength",
						"13"));
				if(editState.isViewOnly()){
					fromConsultant.setEnabled(false);
				}
				fromConsultant.setLabel(new Model("Parent End Date"));
				fromConsultant.setOutputMarkupId(true);
				fromConsultant.add(new AjaxFormComponentUpdatingBehavior(
						"change") {
					@Override
					protected void onUpdate(AjaxRequestTarget target) {
						try {
							fromTransferDto=data;
							if(fromTransferDto.getFromConsultantCode()!=null){
							agreementDto=guiController.findAgreementWithConsultantCode(fromTransferDto.getFromConsultantCode());
							partyDto = guiController.findPartyIntermediaryWithAgreementNr(agreementDto.getOid());
							fromTransferDto.setFromConsultantName(partyDto.getName());
							fromTransferDto.setFromConsultantStatus(agreementDto.getAgreementStatus());
							fromTransferDto.setFromAgreementCode(agreementDto.getAgreementNumber());
							if(!agreementDto.getConsultantCode().equals(fromTransferDto.getFromConsultantCode())){
								fromTransferDto.setFromConsultantStatus(fromTransferDto.getFromConsultantStatus()+ "(old code)");
								
							}
							refreshPageModel();
							target.add(transferGrid);
							}
						} catch (CommunicationException e) {
							getCoreHelper().clearFromConsultant(fromTransferDto);
							refreshPageModel();
							target.add(transferGrid);
						} catch (DataNotFoundException e) {
							getCoreHelper().clearFromConsultant(fromTransferDto);
							refreshPageModel();
							target.add(transferGrid);
						}
					}
				});
				HelperPanel panel = HelperPanel.getInstance(componentId,
						fromConsultant, false);
				panel.setOutputMarkupId(true);
				// addToHomeRowComponentsList(data,panel,"effectiveTo");
				return panel;
			}
		}.setInitialSize(160);
		return c;
	}
	
	
	/**
	 * Get the GUI Controller
	 * 
	 * @return
	 */
	protected ICoreTransferGuiController getGUIController() {
		if (guiController == null) {
			try {
				guiController = ServiceLocator
						.lookupService(ICoreTransferGuiController.class);
			} catch (NamingException e) {
				throw new CommunicationException(
						"Naming exception looking up CoreTransferGUIController",
						e);
			}
		}
		return guiController;
	}
}
