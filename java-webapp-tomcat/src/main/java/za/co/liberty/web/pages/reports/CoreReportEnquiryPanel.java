package za.co.liberty.web.pages.reports;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AbstractAutoCompleteRenderer;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteSettings;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.IAutoCompleteRenderer;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.extensions.yui.calendar.DatePicker;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.link.PopupSettings;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.Response;
import org.apache.wicket.util.convert.IConverter;

import com.inmethod.grid.IGridColumn;
import com.inmethod.grid.SizeUnit;
import com.inmethod.grid.column.AbstractColumn;

import za.co.liberty.business.guicontrollers.core.ICoreReportGuiController;
import za.co.liberty.dto.agreement.core.CoreTransferDto;
import za.co.liberty.dto.gui.context.ResultContextItemDTO;
import za.co.liberty.dto.gui.report.ReportEnquiryDTO;
import za.co.liberty.dto.gui.request.RequestEnquiryPageModelDTO;
import za.co.liberty.dto.gui.request.RequestEnquiryPeriodDTO;
import za.co.liberty.dto.gui.request.RequestUserDTO;
import za.co.liberty.dto.report.BookControlDto;
import za.co.liberty.dto.report.ReportEnquiryResponseDto;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.helpers.lookup.ServiceLocator;
import za.co.liberty.helpers.util.CompareUtil;
import za.co.liberty.helpers.util.DateUtil;
import za.co.liberty.interfaces.agreements.requests.RequestKindType;
import za.co.liberty.web.data.enums.ContextType;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.pages.report.model.CoreReportEnquiryPageModel;
import za.co.liberty.web.pages.request.model.ResultTableColumnLayoutEnum;
import za.co.liberty.web.pages.search.ContextSearchPopUp;
import za.co.liberty.web.wicket.ajax.attributes.SRSAjaxCallListener;
import za.co.liberty.web.wicket.markup.html.form.SRSDateField;
import za.co.liberty.web.wicket.markup.html.form.SRSTextField;
import za.co.liberty.web.wicket.markup.html.grid.GridToCSVHelper;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGrid;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGrid.GridSizeUnit;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGridColumn;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataProviderAdapter;
import za.co.liberty.web.wicket.markup.repeater.data.SortableListDataProvider;

public class CoreReportEnquiryPanel extends Panel{

	private static final long serialVersionUID = -177436596237031482L;

	private static final Logger logger = Logger.getLogger(CoreReportEnquiryPanel.class);
	
	public static final String VIEW_WINDOW_PAGE_MAP = "REQUEST_VIEW_WINDOW_PAGE_MAP";
	public static final String VIEW_WINDOW_COOKIE_NAME = "SRS_REQUEST_VIEW_WINDOW_COOKIE";
	
	protected static final String COMP_SEARCH_FORM = "searchForm";
	protected static final String COMP_SELECT_FORM = "selectForm";
	protected static final String COMP_SEARCH_FILTER_PANEL = "searchFilterPanel";
	
	protected FeedbackPanel feedbackPanel;
	@SuppressWarnings("unchecked")
	protected Form searchForm;
	@SuppressWarnings("unchecked")
	protected Form exportForm;
	
	@SuppressWarnings("unchecked")
	protected DropDownChoice requestKindTypeField;
	@SuppressWarnings("unchecked")
	protected DropDownChoice requestEnquiryPeriodField;

	protected DatePicker startDatePicker;
	protected DatePicker endDatePicker;
	protected SRSDateField startDateField;
	protected SRSDateField endDateField;
	
	protected CoreReportEnquiryPageModel pageModel = null;
	protected Date startDate;
	protected Date endDate;
	protected ReportEnquiryDTO dataModel = null;
	
	protected Button searchButton;
	protected Button exportButton;
	protected SRSDataGrid searchResultPanelField;
	protected List<IGridColumn> searchResultColumns;
	
	protected ModalWindow modalViewWindow;
	private ModalWindow searchWindow;
	private List<RequestUserDTO> userChoiceList;
	private String contextSearchType=null;
	
	private RequestUserDTO tempUserDTO = new RequestUserDTO(-1,null,null);

//  INC000006693842- CoreReportFix
//	private transient ICoreReportGuiController controller;
	@SuppressWarnings("unused")
	private AutoCompleteTextField userTypeField;
	private Component fromConsultantField;
	private SRSTextField toConsultantField;	
	private SRSTextField contractNumberField;	
	
//  INC000006693842- CoreReportFix
	private transient ICoreReportGuiController guiController;
	/**
	 * Default constructor 
	 * 
	 * @param arg0
	 * @param model
	 */
	@SuppressWarnings("unchecked")
	public CoreReportEnquiryPanel(String arg0, IModel model, FeedbackPanel feedbackPanel,ICoreReportGuiController controller) {
		super(arg0, model);
		pageModel = (CoreReportEnquiryPageModel) model.getObject();
		dataModel = pageModel.getDataModel(this.getClass());
		this.feedbackPanel = feedbackPanel;
//  INC000006693842- CoreReportFix
//		this.controller=controller;
		add(searchForm=createSearchForm(CoreReportEnquiryPanel.COMP_SEARCH_FORM));
		add(searchResultPanelField=createSearchResultPanel("searchResultPanel",0));
		add(searchWindow = createSearchWindow("searchContextWindow"));
	}	
	
	/**
	 * Create the search form which holds all the search filter fields.
	 * 
	 * @param id
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Form createSearchForm(String id) {
		Form form = new Form(id) {		
			private static final long serialVersionUID = -6308633210871154462L;
			@Override
			protected void onSubmit() {
				super.onSubmit();	
			}
		};
		form.add(userTypeField=createUserTypeField("userType"));
		form.add(fromConsultantField=createFromConsultantField("fromConsultant"));
		form.add(createFromContextSearchButton("contextFromSearchButton"));
		form.add(createToContextSearchButton("contextToSearchButton"));
		form.add(toConsultantField=createToConsultantField("toConsultant"));
		form.add(contractNumberField=createContractNumberField("contractNumber"));
		form.add(requestKindTypeField=createRequestKindTypeField("requestKindType"));
		form.add(requestEnquiryPeriodField=createPeriodField("period"));
		form.add(startDateField=createStartDateField("startDate"));
		form.add(endDateField=createEndDateField("endDate"));
		form.add(searchButton=createSearchButton("searchButton", form));
		form.add(exportForm=createExportForm("exportForm"));
		form.setOutputMarkupId(true);
		return form;
	}


	/**
	 * Create the export to CSV form
	 * 
	 * @param string
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private Form createExportForm(String id) {
		Form form = new Form(id);
		form.add(exportButton=createExportButton("exportButton", form));
		return form;
	}
	
	// ==============================================================================================
	// Create the search result table / panel
	// ==============================================================================================
	/**
	 * Create the search result panel (Empty when no search has been done)
	 * 
	 * @param id
	 * @return
	 */
	protected SRSDataGrid createSearchResultPanel(String id,int requestKind) {
	
		/* Create the search result table */
		searchResultColumns = createSearchResultColumns(requestKind);

		SRSDataGrid grid = new SRSDataGrid(id, new SRSDataProviderAdapter(
				new SortableListDataProvider<ReportEnquiryResponseDto>(pageModel.getReportEnquiryResponseList())), 
				searchResultColumns, null);
		grid.setAutoResize(false);
		grid.setRowsPerPage(50);
		grid.setContentHeight(350, SizeUnit.PX);
		grid.setAllowSelectMultiple(false);
		grid.setCleanSelectionOnPageChange(true);
		grid.setGridWidth(99, GridSizeUnit.PERCENTAGE);
		
/*		grid.setAutoResize(false);
		grid.setRowsPerPage(12);
		grid.setContentHeight(270, SizeUnit.PX);
		grid.setAllowSelectMultiple(dataModel.getBulkAuthoriseType()!=null);
		grid.setCleanSelectionOnPageChange(dataModel.getBulkAuthoriseType()==null);*/
		
		return grid;
	}
	
	/**
	 * Create the search result grid column configuration
	 * 
	 * @return
	 */
	protected List<IGridColumn> createSearchResultColumns(int requestKind) { 
		List<IGridColumn> columns = new ArrayList<IGridColumn>();
		
		if(requestKind==RequestKindType.ProcessBookLevelTransfer.getRequestKind()){
			PopupSettings popupSettings = new PopupSettings(
					PopupSettings.RESIZABLE);
			popupSettings.setWindowName("ManagerPopupPage").setWidth(350)
					.setHeight(160).setTop(450).setLeft(650);

			columns.add(new PopupColumn("requestId", new Model(
					"Request Id"), "requestId",
					popupSettings, "requestId"));
		}else{
			columns.add(new SRSDataGridColumn<CoreTransferDto>("requestId",
					new Model("Request Id"), "requestId", EditStateType.VIEW) {
				private static final long serialVersionUID = 1L;
			}.setInitialSize(100));
		}
		columns.addAll(ResultTableColumnLayoutEnum.REPORT.getColumnList());
	
		return columns;
	}
	
	
	// ==============================================================================================
	// Generate button fields
	// ==============================================================================================
	
	/**
	 * Create the search button
	 * 
	 * @param id
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected Button createSearchButton(String id, Form form) {
		
		Button but = new AjaxButton(id, form) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form form) {
				doSearchButtonValidation();
				if(feedbackPanel.hasErrorMessage())
					target.add(feedbackPanel);
				else{
					doSearchButtonSubmit(target);
					super.onSubmit();
				}
			}

			@Override
			protected void onComponentTag(ComponentTag tag) {
				super.onComponentTag(tag);
			}
			
//			@Override
//			protected IAjaxCallDecorator getAjaxCallDecorator() {
//				return new AjaxCallDecorator() {
//					private static final long serialVersionUID = 1L;
//
//					public CharSequence decorateScript(CharSequence script) {
//						return "overlay(true);" + script;
//					}
//				};
//			}
			
			@Override
			protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
				super.updateAjaxAttributes(attributes);
			        
			        // SRS Convenience method for overLay hiding/showing
			        attributes.getAjaxCallListeners().add(new SRSAjaxCallListener());
			}
		};

		but.setOutputMarkupId(true);
		
		return but;
	}
	
	/**
	 * Event called when search button is clicked.  Call search functionality and update
	 * table with result.
	 * @param target 
	 */
	@SuppressWarnings("unchecked")
	protected void doSearchButtonSubmit(AjaxRequestTarget target) {
	
		try {
			RequestEnquiryPageModelDTO resultDto = null;
			List<ReportEnquiryResponseDto> reportEnquiryResponseList=null;
			try {
			//  INC000006693842- CoreReportFix
				reportEnquiryResponseList=getGuiController().findReport(dataModel);
			} catch (Exception e) {
				Logger.getLogger(this.getClass()).error("Error when searching", e);
				this.error(e.getMessage());
				
				
				return;
			} 
			
			//pageModel.getReportEnquiryResponseList().clear();
			pageModel.setReportEnquiryResponseList(reportEnquiryResponseList);
	
			// Refresh the Grid
			SRSDataGrid tmpGrid = createSearchResultPanel("searchResultPanel",dataModel.getRequestKind().getRequestKind());
			searchResultPanelField.replaceWith(tmpGrid);
			searchResultPanelField = tmpGrid;
			tmpGrid.size();
			target.add(searchResultPanelField);
		} finally {
			addComponentsForSearch(target);
		}
	}
	
	/**
	 * Components to refresh after a search
	 * 
	 * @param target
	 */
	protected void addComponentsForSearch(AjaxRequestTarget target) {
		target.add(feedbackPanel);
		target.add(searchResultPanelField);
		target.add(searchButton);
		target.add(exportButton);
		target.add(searchForm);
	}
	
	/**
	 * Override this method to implement validation on the search button.
	 * 
	 * @param val
	 */
	@SuppressWarnings("unchecked")
	protected void doSearchButtonValidation() {
		// Request kind validation
		if (dataModel.getRequestKind()==null) {
			feedbackPanel.error("Request Kind should be selected.");
		}
		// Some date validation
		DateUtil dateUtil = DateUtil.getInstance();
		if (dateUtil.compareDatePart(dataModel.getStartDate(), dataModel.getEndDate())>0) {
			feedbackPanel.error("Please enter valid date Range");
		}
		
	
		
	}
	
	/**
	 * Create the export button
	 * 
	 * @param id
	 * @param form
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected Button createExportButton(String id, Form form) {
		Button but = new Button(id) {
			private static final long serialVersionUID = 1L;
			
			@Override
			protected void onComponentTag(ComponentTag tag) {
				super.onComponentTag(tag);
			}
			
			@Override
			public void onSubmit() {
				super.onSubmit();
				try {
					new GridToCSVHelper().createCSVFromDataGrid(searchResultPanelField,
							"Core_Report_Enquiry.csv");
				} catch (Exception e) {	
					Logger.getLogger(this.getClass()).error(
							"An error occured when trying to generate the excel document",e);
					this.error("Error occurred during export:" + e.getCause());
				}				
			}
		};
		but.setOutputMarkupId(true);
		return but;
	}
	

	/**
	 * Create the request kind type field
	 * 
	 * @param string
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private DropDownChoice createRequestKindTypeField(String id) {
		IModel model = new IModel() {
			private static final long serialVersionUID = 1L;
			
			public Object getObject() {
				return dataModel.getRequestKind();
			}
			public void setObject(Object arg0) {
				dataModel.setRequestKind((RequestKindType) arg0);
			}
			public void detach() {	
			}
		};
		DropDownChoice field = new DropDownChoice(id, model, pageModel.getAllRequestKindTypeList());
		field.setOutputMarkupId(true);
		field.setNullValid(true);
		field.add(new AjaxFormComponentUpdatingBehavior("change") {
			private static final long serialVersionUID = 1L;
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
			}		
		});
		return field;
	}


	/**
	 * Create the period field
	 * 
	 * @param string
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private DropDownChoice createPeriodField(String id) {
		IModel model = new IModel() {
			private static final long serialVersionUID = 1L;
			
			public Object getObject() {
				return dataModel.getRequestEnquiryPeriod();
			}
			public void setObject(Object arg0) {
				dataModel.setRequestEnquiryPeriod((RequestEnquiryPeriodDTO) arg0);
			}
			public void detach() {	
			}
		};
		DropDownChoice field = new DropDownChoice(id, model, pageModel.getAllPeriodList());
		field.add(new AjaxFormComponentUpdatingBehavior("change") {
			private static final long serialVersionUID = 1L;
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				
				RequestEnquiryPeriodDTO dto = dataModel.getRequestEnquiryPeriod();
				if (dto!=null) {
					target.add(startDateField);
					target.add(endDateField);
				}
			}		
		});
		field.setOutputMarkupId(true);
		return field;
	}
	
	
	/**
	 * Create Start Date field
	 * 
	 * @param id
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private SRSDateField createStartDateField(String id) {
		SRSDateField text = new SRSDateField(id,  new IModel() {
			private static final long serialVersionUID = -1060562129103084694L;

			public Object getObject() {
				return dataModel.getStartDate();
			}
			public void setObject(Object arg0) {
				dataModel.setStartDate((Date) arg0);			
			}
			public void detach() {			
			}
		});
		text.add(createDateFieldUpdateBehavior("change"));
		text.add(text.newDatePicker());
		text.setOutputMarkupId(true);
		return text;
	}
	
	
	/**
	 * Create End Date field
	 * 
	 * @param id
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private SRSDateField createEndDateField(String id) {
		
		
		SRSDateField text = new SRSDateField(id,  new IModel() {
			private static final long serialVersionUID = -1060562129103084694L;
			public Object getObject() {
				return dataModel.getEndDate();
			}
			public void setObject(Object arg0) {
				dataModel.setEndDate((Date) arg0);			
			}
			public void detach() {			
			}
		});
		text.add(createDateFieldUpdateBehavior("change"));
		text.add(text.newDatePicker());
		text.setOutputMarkupId(true);
		return text;
	}


	/**
	 * Create the common dateFieldUpdate behaviour for the start and end date.
	 *  
	 * @param event
	 * @return
	 */
	public AjaxFormComponentUpdatingBehavior createDateFieldUpdateBehavior (final String event) {
		return new AjaxFormComponentUpdatingBehavior(event) {
			private static final long serialVersionUID = 1L;
			@Override
			protected void onUpdate(AjaxRequestTarget target) {

				
				// Update the period if the date has changed
				RequestEnquiryPeriodDTO dto = dataModel.getRequestEnquiryPeriod();
								
				if (dto==null) {
					return;
				}
				CompareUtil compareUtil = CompareUtil.getInstance();
				if (!compareUtil.isEqual(dto.getEndDate(), dataModel.getEndDate()) ||
						!compareUtil.isEqual(dto.getStartDate(), dataModel.getStartDate())) {
					dataModel.setRequestEnquiryPeriod(null);
					target.add(requestEnquiryPeriodField);	
				}
			}
		};
	}

	public SRSTextField  createContractNumberField(String id){		
		IModel model = new IModel() {
			private static final long serialVersionUID = 1L;
			
			public Object getObject() {
				return dataModel.getContactNumber();
			}
			public void setObject(Object arg0) {
				dataModel.setContactNumber(String.valueOf(arg0));
			}
			public void detach() {	
			}
		};
		
		// Create the field
		final SRSTextField field = new SRSTextField(id, model) {
			private static final long serialVersionUID = 1L;
		};
		return field;
}
	
	public SRSTextField  createToConsultantField(String id){		
		IModel model = new IModel() {
		private static final long serialVersionUID = 1L;
		
		public Object getObject() {
			return dataModel.getToConsultatCode();
		}
		public void setObject(Object arg0) {
			dataModel.setToConsultatCode(String.valueOf(arg0));
		}
		public void detach() {	
		}
	};
	
	// Create the field
	final SRSTextField field = new SRSTextField(id, model) {
		private static final long serialVersionUID = 1L;
	};
	field.setOutputMarkupId(true);
	field.setEnabled(false);
	return field;
}
	
	public SRSTextField createFromConsultantField(String id){
		IModel model = new IModel() {
			private static final long serialVersionUID = 1L;
			
			public Object getObject() {
				return dataModel.getFromConsultatCode();
			}
			public void setObject(Object arg0) {
				dataModel.setFromConsultatCode(String.valueOf(arg0));
			}
			public void detach() {	
			}
		};
		
		// Create the field
		final SRSTextField field = new SRSTextField(id, model) {
			private static final long serialVersionUID = 1L;
		};
		field.setOutputMarkupId(true);
		field.setEnabled(false);
		return field;
	}
	
	
	public AutoCompleteTextField createUserTypeField(String id) {
		if (dataModel.getUser()!=null) {
			// Initialise choices
			userChoiceList = new ArrayList<RequestUserDTO>();
			userChoiceList.add(dataModel.getUser());
		}
		
		Model<RequestUserDTO> model = new Model<RequestUserDTO>(){
			private static final long serialVersionUID = 1L;

			@Override
			public RequestUserDTO getObject() {				
				return dataModel.getUser();
			}

			@Override
			public void setObject(RequestUserDTO object) {
				dataModel.setUser(object);				
			}
			
		};
		
		// Renderer
		IAutoCompleteRenderer<RequestUserDTO> renderer = new AbstractAutoCompleteRenderer<RequestUserDTO>() {

			private static final long serialVersionUID = 1L;

			@Override
			protected String getTextValue(RequestUserDTO object) {
				if (object == null) {
					return "";
				}
				return ((RequestUserDTO)object).toString();
			}

			@Override
			protected void renderChoice(RequestUserDTO object, Response response,
					String criteria) {
				response.write(getTextValue(object));
			}


		};

		// Create the field
		final AutoCompleteTextField<RequestUserDTO> field = new AutoCompleteTextField<RequestUserDTO>(id, model, RequestUserDTO.class, renderer, new AutoCompleteSettings().setPreselect(true)) {
			private static final long serialVersionUID = 1L;
			@SuppressWarnings("unchecked")
			@Override
			protected Iterator<RequestUserDTO> getChoices(String input) {
				if (input.length()<=2) {
					return Collections.EMPTY_LIST.iterator();
				}
			//  INC000006693842- CoreReportFix
				userChoiceList = getGuiController().findUsersWithUacfStartingWith(input);
				return userChoiceList.iterator();
			}
			
			@Override
			public IConverter getConverter(Class  type) {
				if(type == RequestUserDTO.class){
					return new IConverter(){						
						private static final long serialVersionUID = 1L;
						public Object convertToObject(String value, Locale locale) {
							String strVal = value;
							strVal = strVal.trim();
							if (strVal.length()>=3) {
								// At least the uacfID should be entered.
								String uacfId = (strVal.length()==7) ? strVal : strVal.substring(0, 
										(strVal.length()>7) ? 7 : strVal.length());
							//  INC000006693842- CoreReportFix
								List<RequestUserDTO> list =  getGuiController().findUsersWithUacfStartingWith(uacfId);
								if (list.size() > 0) {
									for(RequestUserDTO user : list){
										if(user.getUacfId().equalsIgnoreCase(uacfId)){
											dataModel.setUser(user);
											userChoiceList.add(dataModel.getUser());
											return dataModel.getUser();
										}
									}
								} else {
//									System.out.println("  -- SEARCH returned x items = "+ list.size());
								}
							}
							tempUserDTO.setName(value);
							dataModel.setUser(null);
							return tempUserDTO;
						}
						public String convertToString(Object value, Locale locale) {							
							if(value != null && value instanceof RequestUserDTO){
								return ((RequestUserDTO)value).toString();
							}else{
								return null;
							}
						}						
					};
				}else{
					return super.getConverter(type);
				}
			}		
		};
		
		// Refresh this component when updated to force IModel logic to be applied.
		AjaxFormComponentUpdatingBehavior behaviour = new AjaxFormComponentUpdatingBehavior("change") {
			private static final long serialVersionUID = 1L;
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				target.add(field);
			}
			@Override
			protected void onError(AjaxRequestTarget target, RuntimeException e) {
				super.onError(target, e);				
				if(feedbackPanel != null){
					target.add(feedbackPanel);
				}
			}
		};
		field.add(behaviour);
		field.setOutputMarkupId(true);
		field.add(new AttributeModifier("autocomplete", "off"));
		field.setLabel(new Model<String>("User"));
		return field;
	}
	
	
	protected Button createFromContextSearchButton(String id) {
		Button but = new AjaxButton(id) {
			private static final long serialVersionUID = 1L;

			@SuppressWarnings("unchecked")
			@Override
			protected void onSubmit(AjaxRequestTarget target, Form form) {
				contextSearchType="From";
				searchWindow.show(target);
			}
		};
		return but;
	}
	protected Button createToContextSearchButton(String id) {
		Button but = new AjaxButton(id) {
			private static final long serialVersionUID = 1L;

			@SuppressWarnings("unchecked")
			@Override
			protected void onSubmit(AjaxRequestTarget target, Form form) {
				contextSearchType="To";
				searchWindow.show(target);
			}
		};
		return but;
	}
	private ModalWindow createSearchWindow(String id) {
		ContextSearchPopUp popUp = new ContextSearchPopUp() {
			@Override
			public ContextType getContextType() {
				return ContextType.AGREEMENT_ONLY;
			}

			@Override
			public void doProcessSelectedItems(AjaxRequestTarget target,
					ArrayList<ResultContextItemDTO> selectedItemList) {
				if (selectedItemList.size() == 0) {
					return;
				}
				if(contextSearchType.equalsIgnoreCase("From")){
				//  INC000006693842- CoreReportFix For fetching the Fromconsultantcode which preceeding with 0.
					String cons=String.valueOf((selectedItemList.get(0)).getAgreementDTO().getConsultantCode());
					if(cons.length()==13)
					{
					dataModel.setFromConsultatCode(String.valueOf((selectedItemList.get(0)).getAgreementDTO().getConsultantCode()));
					}
					else
					{   int count= 13-cons.length();
					    String s1="";
					    for( int i=0;i<count;i++)
					    {
					    	s1+="0";
					    }
						dataModel.setFromConsultatCode(s1+String.valueOf((selectedItemList.get(0)).getAgreementDTO().getConsultantCode()));
					}
					target.add(fromConsultantField);
					
				}
				else{
				//  INC000006693842- CoreReportFix For fetching the Toconsultantcode which preceeding with 0.
					String Tocons=String.valueOf((selectedItemList.get(0)).getAgreementDTO().getConsultantCode());
					if(Tocons.length()==13)
					{
						dataModel.setToConsultatCode(String.valueOf((selectedItemList.get(0)).getAgreementDTO().getConsultantCode()));
					}
					
					else
					{
						int count=13-Tocons.length();
						String s2="";
						 for( int i=0;i<count;i++)
						    {
						    	s2+="0";
						    }
						 dataModel.setToConsultatCode(s2+String.valueOf((selectedItemList.get(0)).getAgreementDTO().getConsultantCode()));
					}
					
					target.add(toConsultantField);
				}
			}
		};
		ModalWindow win = popUp.createModalWindow(id);
//		win.setPageMapName("homeSearchPageMap");
		return win;
	}
//  INC000006693842- CoreReportFix
    protected ICoreReportGuiController getGuiController() {
        if (guiController==null) {
        
                try {
                        guiController = ServiceLocator.lookupService(ICoreReportGuiController.class);
                } catch (NamingException e) {
                        throw new CommunicationException(e);
                }
        }
        return guiController;
}
}




class PopupColumn extends AbstractColumn {

	private PopupSettings popupSettings;
	private  String value;
//  INC000006693842- CoreReportFix
	private transient ICoreReportGuiController guiController;
	public PopupColumn(String columnID,IModel displayModel, String sortProperty, PopupSettings popupSettings,String value) {
		super(columnID,displayModel, sortProperty);
		this.popupSettings = popupSettings;
		this.value = value;
		
	}

	private static final long serialVersionUID = 1L;

	

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Component newCell(WebMarkupContainer parent, String componentId, IModel rowModel) {
		return new LinkedPanel(componentId, rowModel);
	}

	
	/**
	 * Panel with Row that has an anchor on click of which a popup has to be opened.
	 * 
	 * 
	 */
	private class LinkedPanel extends Panel {
	//  INC000006693842- CoreReportFix		
//		private transient ICoreReportGuiController guiController;
		private static final long serialVersionUID = 1L;
		Link link = null;

		@SuppressWarnings("unchecked")
		private LinkedPanel(String componentId, final IModel model) {
			super(componentId, model);
			
			link = new Link(componentId,model)	{
						
					@Override
					public void onComponentTagBody(org.apache.wicket.markup.MarkupStream markupStream, ComponentTag openTag) {
						Object obj = model.getObject();
						if (obj instanceof ReportEnquiryResponseDto) {
							ReportEnquiryResponseDto reportEnquiryResponseDto=(ReportEnquiryResponseDto)obj;
								replaceComponentTagBody(markupStream, openTag, String.valueOf(reportEnquiryResponseDto.getRequestId() ));
								
							}
					};

					@Override
					public void onClick() {
						BookControlDto bookControlDto=getGuiController().findBookControlRecord((ReportEnquiryResponseDto)model.getObject());
						if(bookControlDto==null || bookControlDto.getRequestId()==null){
							bookControlDto.setRequestId(((ReportEnquiryResponseDto)model.getObject()).getRequestId());
							bookControlDto.setReceivedRecord(0L);
							bookControlDto.setSystemName("Not Available");
							bookControlDto.setControlRecord(0L);
						}
							
						setResponsePage(new BookControlPopUpPage(bookControlDto));	
					}
					
									              	
	            }.setPopupSettings(popupSettings);
	            
	            link.setOutputMarkupId(true);
	            add(link);
		}
	}
//  INC000006693842- CoreReportFix
		private ICoreReportGuiController getGuiController() {
			if (guiController==null) {
			
				try {
					guiController = ServiceLocator.lookupService(ICoreReportGuiController.class);
				} catch (NamingException e) {
					throw new CommunicationException(e);
				}
			}
			return guiController;
		}
			
}