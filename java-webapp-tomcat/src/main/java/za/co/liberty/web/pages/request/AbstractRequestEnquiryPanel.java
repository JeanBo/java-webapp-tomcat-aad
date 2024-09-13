package za.co.liberty.web.pages.request;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.apache.wicket.Page;
import org.apache.wicket.PageReference;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.ajax.markup.html.form.AjaxFallbackButton;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow.MaskType;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.ValidationError;

import com.inmethod.grid.IGridColumn;
import com.inmethod.grid.SizeUnit;

import za.co.liberty.agreement.common.enums.ProductKindEnumeration;
import za.co.liberty.business.guicontrollers.request.IRequestEnquiryGuiController;
import za.co.liberty.dto.agreement.request.RequestEnquiryRowDTO;
import za.co.liberty.dto.contracting.ContractEnquiryDTO;
import za.co.liberty.dto.gui.context.PolicyTransactionTypeEnum;
import za.co.liberty.dto.gui.request.BulkAuthDeclineResultDTO;
import za.co.liberty.dto.gui.request.RequestEnquiryDTO;
import za.co.liberty.dto.gui.request.RequestEnquiryPageModelDTO;
import za.co.liberty.dto.gui.request.RequestEnquiryPeriodDTO;
import za.co.liberty.dto.gui.request.RequestUserDTO;
import za.co.liberty.dto.gui.userprofiles.RequestCategoryDividerDTO;
import za.co.liberty.dto.transaction.IPolicyTransactionDTO;
import za.co.liberty.dto.userprofiles.RequestCategoryDTO;
import za.co.liberty.exceptions.data.QueryTimeoutException;
import za.co.liberty.exceptions.data.ValidationException;
import za.co.liberty.exceptions.error.request.RequestException;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.exceptions.fatal.InconsistentConfigurationException;
import za.co.liberty.helpers.lookup.ServiceLocator;
import za.co.liberty.helpers.util.CompareUtil;
import za.co.liberty.helpers.util.DateUtil;
import za.co.liberty.interfaces.agreements.AgreementKindType;
import za.co.liberty.interfaces.agreements.requests.RequestActionType;
import za.co.liberty.interfaces.agreements.requests.RequestDateType;
import za.co.liberty.interfaces.agreements.requests.RequestKindType;
import za.co.liberty.interfaces.agreements.requests.RequestStatusType;
import za.co.liberty.interfaces.persistence.agreement.request.IRequestEnquiryRow;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.pages.request.model.RequestEnquiryModel;
import za.co.liberty.web.pages.request.model.ResultTableColumnLayoutEnum;
//import za.co.liberty.web.pages.transactions.PolicyTransactionsPage;
import za.co.liberty.web.system.SRSAuthWebSession;
import za.co.liberty.web.wicket.ajax.attributes.SRSAjaxCallListener;
import za.co.liberty.web.wicket.markup.html.form.SRSAbstractChoiceRenderer;
import za.co.liberty.web.wicket.markup.html.form.SRSDateField;
import za.co.liberty.web.wicket.markup.html.grid.GridToCSVHelper;
import za.co.liberty.web.wicket.markup.html.grid.SRSCheckBoxColumn;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGrid;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataProviderAdapter;
import za.co.liberty.web.wicket.markup.repeater.data.ListDataProvider;
import za.co.liberty.web.wicket.markup.repeater.data.SortableListDataProvider;

/**
 * <p>This is the base Request Enquiry Panel class which contains all the common controls
 * and basic functionality for the panels that are used in the Request Enquiry page tabs. This
 * panel adds the controls that are common to all the panels to the search form.</p>
 * 
 * <p>Usage: Extend this panel and override the createForm method and add any additional
 * components.  Then make sure you provide the HTML for placing the components in the 
 * search section, taking care to ensure that all of the controls common to all panels
 * are placed.</p>
 *
 * @author JZB0608 - 11 Dec 2009
 *
 */
public abstract class AbstractRequestEnquiryPanel extends Panel {

	private static final long serialVersionUID = -177436596237031482L;
	
	protected transient Logger logger = null;
	public static final String VIEW_WINDOW_PAGE_MAP = "REQUEST_VIEW_WINDOW_PAGE_MAP";
	public static final String VIEW_WINDOW_COOKIE_NAME = "SRS_REQUEST_VIEW_WINDOW_COOKIE";
	
	protected static final String COMP_SEARCH_FORM = "searchForm";
	protected static final String COMP_SELECT_FORM = "selectForm";
	protected static final String COMP_SEARCH_FILTER_PANEL = "searchFilterPanel";
	
	private transient IRequestEnquiryGuiController guiController;
	
	protected FeedbackPanel feedbackPanel;
	@SuppressWarnings("unchecked")
	protected Form searchForm;
	@SuppressWarnings("unchecked")
	protected Form selectForm;
	@SuppressWarnings("unchecked")
	protected Form exportForm;
	
	protected Panel searchFilterPanel;
	protected Button viewButton;
	@SuppressWarnings("unchecked")
	protected DropDownChoice contextTypeField;
	@SuppressWarnings("unchecked")
	protected DropDownChoice requestCategoryTypeField;
	@SuppressWarnings("unchecked")
	protected DropDownChoice requestKindTypeField;
	@SuppressWarnings("unchecked")
	protected DropDownChoice requestStatusTypeField;
	@SuppressWarnings("unchecked")
	protected DropDownChoice agreementKindTypeField;
	@SuppressWarnings("unchecked")
	protected DropDownChoice requestDateTypeField;
	@SuppressWarnings("unchecked")
	protected DropDownChoice requestEnquiryPeriodField;
	
	protected Panel startDatePicker;
	protected Panel endDatePicker;
	
	protected SRSDateField startDateField;
	protected SRSDateField endDateField;
	
	protected RequestEnquiryModel pageModel = null;
	protected Date startDate;
	protected Date endDate;
	protected RequestEnquiryDTO dataModel = null;
	
	protected Button searchButton;
	protected Button nextButton;
	protected WebMarkupContainer exportButton;
	
	protected Button selectButton;
	protected Button editButton;
	protected Button bulkAuthoriseButton;
	protected Button bulkDeclineButton;
	
	protected SRSDataGrid searchResultPanelField;
	protected List<IGridColumn> searchResultColumns;
	
	protected Panel totalsPanelField;	
	protected List<IGridColumn> totalsColumns;
	
	protected ModalWindow modalViewWindow;
	protected PageReference viewWindowPage;
		
	/**
	 * Default constructor 
	 * 
	 * @param arg0
	 * @param model
	 */
	@SuppressWarnings("unchecked")
	public AbstractRequestEnquiryPanel(String arg0, IModel model, FeedbackPanel feedbackPanel) {
		super(arg0, model);
		pageModel = (RequestEnquiryModel) model.getObject();
		dataModel = pageModel.getDataModel(this.getClass());
		this.feedbackPanel = feedbackPanel;
		add(searchForm = createSearchForm(AbstractRequestEnquiryPanel.COMP_SEARCH_FORM));
		add(totalsPanelField = new EmptyPanel("searchTotalPanel")); //createTotalsPanel("searchTotalPanel"));
		add(searchResultPanelField = createSearchResultPanel("searchResultPanel"));
		add(selectForm = createSelectForm(AbstractRequestEnquiryPanel.COMP_SELECT_FORM));
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
		
//		if(this instanceof RequestPolicyTransactionPanel)
//			dataModel.setRequestKind(RequestKindType.DistributePolicyEarning);
		if (isIncludeRequestKindFields()) {
			form.add(requestCategoryTypeField=createRequestCategoryTypeField("requestCategoryType"));
			form.add(requestKindTypeField=createRequestKindTypeField("requestKindType"));
		}
		
		
		form.add(requestStatusTypeField=createRequestStatusTypeField("requestStatusType"));
		form.add(requestEnquiryPeriodField=createPeriodField("period"));
		form.add(requestDateTypeField=createRequestDateTypeField("requestDateType"));
		form.add(startDateField=createStartDateField("startDate"));
		form.add(endDateField=createEndDateField("endDate"));
		form.add(startDatePicker=createStartDatePicker("startDatePicker", startDateField));
		form.add(endDatePicker=createEndDatePicker("endDatePicker", endDateField));
		form.add(searchButton=createSearchButton("searchButton", form));
		form.add(nextButton=createNextButton("nextButton", form));
		form.add(exportForm=createExportForm("exportForm"));
		form.setOutputMarkupId(true);
		return form;
	}

	/**
	 * Override to exlude certain request kind related components from being added
	 * @return
	 */
	protected boolean isIncludeRequestKindFields() {
		return true;
	}

	/**
	 * Create the select form which holds all the select actions
	 * 
	 * @param id
	 * @return
	 */
	public Form<?> createSelectForm(String id) {
		Form<?> form = new Form<Object>(id);
		form.add(selectButton=createSelectButton("selectButton", form));
		form.add(editButton = createEditButton("editButton", form));
		form.add(bulkAuthoriseButton=createBulkAuthOrDeclineButton("bulkAuthoriseButton", form, RequestActionType.AUTHORISE));
		form.add(bulkDeclineButton=createBulkAuthOrDeclineButton("bulkDeclineButton", form, RequestActionType.DECLINE));
		form.add(modalViewWindow=createModalViewWindow("modalViewWindow"));
		return form;
	}

	protected Button createEditButton(String id, Form form) {
		Button but = new AjaxButton(id, form) {
			private static final long serialVersionUID = 1L;

			
			@Override
			protected void onSubmit(AjaxRequestTarget target, Form form) {
				int selectedCount = searchResultPanelField.getSelectedItems().size();
				if (selectedCount==0) {
					// Give an error???
					error(this.getString(RequestEnquiryValidationType.VIEW_SELECTION_REQUIRED.getMessageKey()));
					
					if (target!=null)
						target.add(feedbackPanel);
					return;
				}
				if (selectedCount>1) {
					// Give an error???
					error(this.getString(RequestEnquiryValidationType.VIEW_SELECTION_MULTIPLE.getMessageKey()));
					if (target!=null)
						target.add(feedbackPanel);
					return;
				}
				
				RequestKindType requestType = ((RequestEnquiryRowDTO)searchResultPanelField.getSelectedItemObjects().get(0)).getRequestKindType();
				
				
				// Ensure that we have access
				if (!SRSAuthWebSession.get().getSessionUser().isAllowView(requestType)) {
					error(RequestActionType.VIEW.getRequestSecurityErrorMessage(requestType));
					if (target!=null)
						target.add(feedbackPanel);
					return;
				}
				if (RequestKindType.RecordPolicyInfo.equals(requestType) || RequestKindType.DistributePolicyEarning.equals(requestType)) {

					RequestEnquiryRowDTO rowDto = (RequestEnquiryRowDTO) searchResultPanelField.getSelectedItemObjects().get(0);
					if (rowDto != null) {
						Object currentImage = getGuiController().initialiseRequestViewModel(rowDto, requestType);
						((IPolicyTransactionDTO) currentImage).setOid(rowDto.getRequestId());
						((IPolicyTransactionDTO) currentImage).setEffectiveDate(new java.sql.Date(rowDto.getRequestedDate().getTime()));
						((IPolicyTransactionDTO) currentImage).setRequestDate(new java.sql.Date(rowDto.getRequestDate().getTime()));
						if (rowDto.getExecutedDate() != null) {
							((IPolicyTransactionDTO) currentImage).setExecutedDate(new java.sql.Date(rowDto.getExecutedDate().getTime()));
						}
						
						// #TODO @TODO ADd this back JEAN
						// #WICKETFIX - Fix this dependency
//						setResponsePage(new PolicyTransactionsPage(currentImage, EditStateType.MODIFY, false, rowDto.getAgreementNr()));
					}
				}
				else{
					modalViewWindow.show(target);
				}
				
			}

			@Override
			protected void onComponentTag(ComponentTag tag) {
				super.onComponentTag(tag);
				decorateComponentStyleToHide(dataModel.getSearchResultList() == null
						|| dataModel.getSearchResultList().size() == 0, tag);
			}
			
			
		};
		but.setOutputMarkupId(true);
		return but;
	}

	/**
	 * Create the export to CSV form
	 * 
	 * @param string
	 * @return
	 */
	@SuppressWarnings("unchecked")
	/* SSM2707 Market Integration 28/09/2015 SWETA MENON Begin */
	/*
	 * Access modifier changed to Protected to allow
	 * ServicingPanelsAuthorisationSearchPanel to access the method.
	 */
	protected Form createExportForm(String id) {
		Form form = new Form(id) {

			@Override
			protected void onSubmit() {
				if (getLogger().isDebugEnabled())
					logger.debug("Export - submit start");
				super.onSubmit();
				if (getLogger().isDebugEnabled())
					logger.debug("Export - submit end");
			}
			
		};
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
	protected SRSDataGrid createSearchResultPanel(String id) {
	
		/* Create the search result table */
		searchResultColumns = createSearchResultColumns();

		SRSDataGrid grid = new SRSDataGrid(id, new SRSDataProviderAdapter(
				new SortableListDataProvider<RequestEnquiryRowDTO>(dataModel.getSearchResultList())), 
				searchResultColumns, EditStateType.VIEW);
		grid.setAutoResize(false);
		grid.setRowsPerPage(12);
		grid.setContentHeight(270, SizeUnit.PX);
		grid.setAllowSelectMultiple(dataModel.getBulkAuthoriseType()!=null);
		grid.setCleanSelectionOnPageChange(dataModel.getBulkAuthoriseType()==null);
		grid.setGridWidth(99, SRSDataGrid.GridSizeUnit.PERCENTAGE);
		return grid;
	}
	
	/**
	 * Create the search result grid column configuration
	 * 
	 * @return
	 */
	protected List<IGridColumn> createSearchResultColumns() { 
		List<IGridColumn> columns = new ArrayList<IGridColumn>();
//		columns.add(new SRSGridRowSelectionCheckBox("check"));
		columns.add(new SRSCheckBoxColumn("check"));
		
		if (dataModel.getBulkAuthoriseType() == null && RequestKindType.DistributePolicyEarning.equals(dataModel.getRequestKind())) {
			columns.addAll(ResultTableColumnLayoutEnum.DPE_SEARCH.getColumnList());
		} else if (dataModel.getBulkAuthoriseType() == null && RequestKindType.RecordPolicyInfo.equals(dataModel.getRequestKind())) {
			columns.addAll(ResultTableColumnLayoutEnum.AUM_SEARCH.getColumnList());
		} else if (dataModel.getBulkAuthoriseType() == null) {
			columns.addAll(ResultTableColumnLayoutEnum.DEFAULT.getColumnList());
		} else {
			ResultTableColumnLayoutEnum e = ResultTableColumnLayoutEnum.getLayoutForBulkAuthoriseType(dataModel.getBulkAuthoriseType());
			if (e == null) {
				throw new InconsistentConfigurationException("No layout is defined for the bulk auth type \"" + dataModel.getBulkAuthoriseType() + "\"");
			}
			columns.addAll(e.getColumnList());
		}
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

		Button but = new AjaxFallbackButton(id, form) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form form) {
				doSearchButtonSubmit(target);
				super.onSubmit(target, form);
			}

			@Override
			protected void onComponentTag(ComponentTag tag) {
				super.onComponentTag(tag);
			}
			
			@Override
			protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
				super.updateAjaxAttributes(attributes);
				attributes.getAjaxCallListeners().add(new SRSAjaxCallListener());
			}
		};

		but.setOutputMarkupId(true);
	
		but.add(new IValidator() {
			private static final long serialVersionUID = 1L;

			@Override
			public void validate(IValidatable val) {
				doSearchButtonValidation(val);
				
				if (val.isValid()) {
					// Check max date ranges for search criteria
					try {
						getGuiController().validateSearchDateRange(dataModel);
						return;
					} catch (ValidationException e) {
						// Add all the error messages
						for (String msg : e.getErrorMessages()) {
							val.error(new ValidationError().setMessage(msg));
						}
					}
					// #WICKETTEST - Check for nullpointers.
					AjaxRequestTarget target = RequestCycle.get().find(AjaxRequestTarget.class);
					if (target!=null) {
						target.add(feedbackPanel);					
					}
				} else {
					if (getLogger().isDebugEnabled())
						logger.debug("After validate date range - ERR isValid = false =" + val.isValid());
				}
				

			}

	
		});
		
		return but;
	}
	
	/**
	 * Create the next button
	 * 
	 * @param id
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected Button createNextButton(String id, Form form) {
		
		Button but = new AjaxFallbackButton(id, form) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form form) {
				doNextButtonSubmit(target);
				super.onSubmit(target, form);	
			}

			@Override
			protected void onComponentTag(ComponentTag tag) {
				super.onComponentTag(tag);
				decorateComponentStyleToHide(!dataModel.isShowNextButton(), tag);
			}
			
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

		this.getFeedbackMessages().clear();
		
		boolean dpeRequest = false;
		try {
			RequestEnquiryPageModelDTO resultDto = null;;
			try {
				//TODO : Consider AUM transaction search
				if (this instanceof RequestPolicyTransactionPanel) {
	
					dataModel.setRequestKind(dataModel.getTransactionSearchType().getRequestKind());
					dpeRequest = (dataModel.getRequestKind() == RequestKindType.DistributePolicyEarning);
						
					dataModel.setUser(null);
					if(dataModel.getUacfId() != null && !dataModel.getUacfId().trim().equals("")){
						List<RequestUserDTO> users = guiController.findUsersWithUacfStartingWith(dataModel.getUacfId());
						if(users != null && !users.isEmpty()){
							dataModel.setUser(users.get(0));
						}else{
							error("No user found with UACFID: " + dataModel.getUacfId());
							if (target!=null)
								target.add(feedbackPanel);
							return;
						}
					}
				}

				resultDto = getGuiController().findRequests(dataModel, dpeRequest);
			} catch (QueryTimeoutException e) {
				this.error(e.getMessage());
				return;
			} catch (RequestException e) {
				this.error(e.getMessage());
				return;
			}
			
			//TODO  why am I not updating this in the GuiController, it already has the datamodel?
			dataModel.getSearchResultList().clear();
			dataModel.getSearchResultList().addAll((List)resultDto.getEnquiryResultDto().getResultList());
			/*Market Integration SSM2707 Sweta Menon*/
			doResultPostProcessing();
			/*Does changing the dataModel affect the other properties*/
			dataModel.setHasMoreData(resultDto.getEnquiryResultDto().isHasMoreResults());
			dataModel.setShowNextButton(resultDto.getEnquiryResultDto().isHasMoreResults());
			dataModel.setLastRecordRetrieved(resultDto.getEnquiryResultDto().getToRecord());
			dataModel.setBulkAuthoriseType(resultDto.getBulkAuthoriseType());
	
			// Refresh the Grid
			SRSDataGrid tmpGrid = createSearchResultPanel("searchResultPanel");
			searchResultPanelField.replaceWith(tmpGrid);
			searchResultPanelField = tmpGrid;
			searchResultPanelField.setVisible(true);
			if (target!=null)
				target.add(searchResultPanelField);
			
			if(this instanceof RequestPolicyTransactionPanel && dataModel.getTransactionSearchType() != null && dataModel.getTransactionSearchType().equals(PolicyTransactionTypeEnum.DPE)){
				populateSearchTotals(resultDto);
				SRSDataGrid totalsDataGrid = createTotalsPanel("searchTotalPanel");
				totalsPanelField.replaceWith(totalsDataGrid);
				totalsPanelField = totalsDataGrid;
				if (target!=null)
					target.add(totalsPanelField);
			}
			
			if (dataModel.isHasMoreData()) {
				this.info("Your results were limited.  Press next to retrieve more records.");
			}
		} finally {
			addComponentsForSearch(target);
		}
	}
	
	/*Market Integration SSM2707 Sweta Menon 19/05/2016*/
	/**
	 * Method created to perform post processing activities(possibly filtering
	 * the search results further) on the requests that are obtained as the
	 * search result
	 */
	public void doResultPostProcessing() {
	}
	
	/**
	 * Sets the totals for each of the each DPE attribute  
	 * @param resultDto
	 */
	private void populateSearchTotals(RequestEnquiryPageModelDTO resultDto) {

		List<IRequestEnquiryRow> resultList = resultDto.getEnquiryResultDto().getResultList();
		if(resultList != null && !resultList.isEmpty()){
			
			Map<Integer, BigDecimal> totalsMap = new HashMap<Integer, BigDecimal>();
			
			for (IRequestEnquiryRow enquiryRow : resultList) {
				if(enquiryRow != null)
				{
					if(enquiryRow.getDpeAllocatedAmount() == null || enquiryRow.getDpeAllocatedAmount().equals(BigDecimal.ZERO))
						continue;
					
					BigDecimal amount = totalsMap.get(enquiryRow.getCommissionKindId());
					if(amount == null){
						amount = enquiryRow.getDpeAllocatedAmount();
					}else{
						amount = amount.add(enquiryRow.getDpeAllocatedAmount());
					}
					
					totalsMap.put(enquiryRow.getCommissionKindId(), amount);
				}
			}
			
			//Populate the searchTotals list with the objects in the map
			ContractEnquiryDTO contractEnquiryDTO = new ContractEnquiryDTO();
			for (int key : totalsMap.keySet()) {
				BigDecimal amount = totalsMap.get(key);
				
				if(key == ProductKindEnumeration.FIRSTYEARCOMMISSION.getValue()
					|| key == ProductKindEnumeration.RENEWALCOMMISSION.getValue()
					|| key == ProductKindEnumeration.PREISSUECOMMISSION.getValue()
					|| key == ProductKindEnumeration.TRAILERCOMMISSION.getValue()
					|| key == ProductKindEnumeration.ACCELERATEDCOMMISSION.getValue()
					|| key == ProductKindEnumeration.INTRODUCTORYCOMMISSION.getValue()
					|| key == ProductKindEnumeration.STANDARDCORPORATEFIRSTYEARCOMMISSION.getValue()
					|| key == ProductKindEnumeration.STANDARDCORPORATESUBSEQUENTCOMMISSION.getValue()
					|| key == ProductKindEnumeration.INITIALADVISORYFEEEXTENSIONSPRODUCT.getValue()
					|| key == ProductKindEnumeration.ONGOINGADVISORYFEEEXTENSIONSPRODUCT.getValue()
					|| key == ProductKindEnumeration.ASANDWHENCOMMISSIONEXTENSIONSPRODUCT.getValue()
					|| key == ProductKindEnumeration.ASANDWHENCOMMISSIONPRODUCT.getValue() 
					|| key == ProductKindEnumeration.ONGOINGCOMMISSIONPRODUCT.getValue()
					|| key == ProductKindEnumeration.STANDARDCORPORATEMONTHLYINTROCOMMISSIONPRODUCT.getValue()
					|| key == ProductKindEnumeration.STANDARDCORPORATEMONTHLYRENEWALCOMMISSIONPRODUCT.getValue()
					|| key == ProductKindEnumeration.INITIALCOMMISSIONPRODUCT.getValue()){

					if(amount != null && contractEnquiryDTO.getCommission() != null){
						BigDecimal tempAmount = amount.add(contractEnquiryDTO.getCommission());
						contractEnquiryDTO.setCommission(tempAmount);
					}
					
				}else if (key == ProductKindEnumeration.ANNUALISEDPREMIUMINCOME.getValue()) {
					if (amount != null && contractEnquiryDTO.getApi() != null) {
						BigDecimal tempValue = amount.add(contractEnquiryDTO.getApi());
						contractEnquiryDTO.setApi(tempValue);
					}
				}else if(key == ProductKindEnumeration.POLICYCOUNT.getValue()){
					if (amount != null && contractEnquiryDTO.getPolicyCount() != null) {
						BigDecimal tempValue = amount.add(new BigDecimal(contractEnquiryDTO.getPolicyCount()));
						contractEnquiryDTO.setPolicyCount(tempValue.setScale(2, RoundingMode.HALF_UP).toString());
					}
				}else if (key == ProductKindEnumeration.PRODUCTIONCREDITS.getValue()
						|| key == ProductKindEnumeration.BONUSPRODUCTIONCREDITS.getValue()
						|| key == ProductKindEnumeration.INITIALADVISORYFEEEXTENSIONSPRODUCTCREDITSPRODUCT.getValue()
						|| key == ProductKindEnumeration.ONGOINGADVISORYFEEEXTENSIONSPRODUCTIONCREDITSPRODUCT.getValue()) {
					if (amount != null && contractEnquiryDTO.getProductionCredits() != null) {
						BigDecimal tempValue = amount.add(contractEnquiryDTO.getProductionCredits());
						contractEnquiryDTO.setProductionCredits(tempValue);
					}
				}else if (key == ProductKindEnumeration.INDIVIDUALNEWBUSINESSBLUEPRINTALLOWANCE.getValue()
						|| key == ProductKindEnumeration.INDIVIDUALSERVICINGBLUEPRINTALLOWANCE.getValue()
						|| key == ProductKindEnumeration.CORPORATENEWBUSINESSBLUEPRINTALLOWANCE.getValue()
						|| key == ProductKindEnumeration.CORPORATESERVICINGBLUEPRINTALLOWANCE.getValue()) {
					if (amount != null && contractEnquiryDTO.getBluePrintAll() != null) {
						BigDecimal tempValue = amount.add(contractEnquiryDTO.getBluePrintAll());
						contractEnquiryDTO.setBluePrintAll(tempValue);
					}
				}else if (key == ProductKindEnumeration.CONTRIBUTIONCONSULTINGFEEPRODUCT .getValue()
						|| key == ProductKindEnumeration.ASSETBASEDCONSULTINGFEEPRODUCT.getValue()
						|| key == ProductKindEnumeration.ADHOCINTERMEDIARYFEEPRODUCT.getValue()
						|| key == ProductKindEnumeration.INTERMEDIARYADMINISTRATIONFEEPRODUCT.getValue()
						|| key == ProductKindEnumeration.REFERRALFEEPRODUCT.getValue()
						|| key == ProductKindEnumeration.INITIALADVISORYFEEPRODUCT.getValue()
						|| key == ProductKindEnumeration.ONGOINGADVISORYFEEPRODUCT.getValue()
						//ZZT2108 Binder Fee Changes
						|| key == ProductKindEnumeration.BINDERFEEPRODUCT.getValue()) {
					if (amount != null && contractEnquiryDTO.getFees() != null) {
						BigDecimal tempValue = amount.add(contractEnquiryDTO.getFees());
						contractEnquiryDTO.setFees(tempValue);
					}
				}
			}
			
			if(dataModel.getDPETotals() == null)
				dataModel.setDPETotals(new ArrayList<ContractEnquiryDTO>());
			
			dataModel.getDPETotals().clear();
			dataModel.getDPETotals().add(contractEnquiryDTO);
		}
		
		
		
	}


	/**
	 * Event called when next button is clicked.  Call search functionality and update
	 * table with result.
	 * 
	 * @param target 
	 */
	@SuppressWarnings("unchecked")
	protected void doNextButtonSubmit(AjaxRequestTarget target) {
		boolean dpeRequest = false;
		try {
			RequestEnquiryPageModelDTO resultDto = null;;
			try {
				
				if (this instanceof RequestPolicyTransactionPanel) {
					dataModel.setRequestKind(dataModel.getTransactionSearchType().getRequestKind());
					dpeRequest = (dataModel.getRequestKind() == RequestKindType.DistributePolicyEarning);
				}			
				resultDto = getGuiController().findRequestsNext(dataModel, dpeRequest);
			} catch (QueryTimeoutException e) {
				this.error(e.getMessage());
				return;
			} catch (RequestException e) {
				this.error(e.getMessage());
				return;
			}
			
			//TODO  why am I not updating this in the GuiController, it already has the datamodel?
			dataModel.getSearchResultList().addAll((List)resultDto.getEnquiryResultDto().getResultList());
			dataModel.setHasMoreData(resultDto.getEnquiryResultDto().isHasMoreResults());
			dataModel.setLastRecordRetrieved(resultDto.getEnquiryResultDto().getToRecord());
			dataModel.setShowNextButton(resultDto.getEnquiryResultDto().isHasMoreResults());
			
			// Refresh the Grid
			SRSDataGrid tmpGrid = createSearchResultPanel("searchResultPanel");
			searchResultPanelField.replaceWith(tmpGrid);
			searchResultPanelField = tmpGrid;
			if (target!=null)
				target.add(searchResultPanelField);
			
			if (dataModel.isHasMoreData()) {
				this.info("You results were limited.  Press next to retrieve more records.");
			}
			
			if (dataModel.getEndDate()!=null && DateUtil.getInstance().compareDatePart(
					dataModel.getEndDate(), new Date()) <= 0) {
				this.warn("Some requests may be missing or duplicated as the date range includes todays date.");
			}
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
		
		
		if(RequestKindType.DistributePolicyEarning.equals(this.dataModel.getRequestKind()) || RequestKindType.RecordPolicyInfo.equals(
				this.dataModel.getRequestKind())) {
//			target.add(editButton);
			editButton.setEnabled(false);
		} else {
			editButton.setVisible(false);
		}
//		if(RequestKindType.DistributePolicyEarning.equals(this.dataModel.getRequestKind()) || RequestKindType.RecordPolicyInfo.equals(
//				this.dataModel.getRequestKind())) {
//			
//		}
		
		if (target!=null) {
			target.add(editButton);
			
			
			target.add(feedbackPanel);
			target.add(searchResultPanelField);
			target.add(searchButton);
			target.add(bulkAuthoriseButton);
			target.add(bulkDeclineButton);
			target.add(editButton);
//			remove(editButton);
			target.add(selectButton);
			target.add(nextButton);
			target.add(exportButton);
//		target.add(searchForm);
		} else {
			if (getLogger().isDebugEnabled())
				logger.debug("#JB Ajaxtarget is null - addComponentsForSearch");
		}
		
	}
	
	/**
	 * Override this method to implement validation on the search button.
	 * 
	 * @param val
	 */
	@SuppressWarnings("unchecked")
	protected void doSearchButtonValidation(IValidatable val) {
		
		boolean changed = false;
		
		// Request kind validation
		if (dataModel.getRequestCategory()==null && dataModel.getRequestKind()==null && !(this instanceof RequestPolicyTransactionPanel)) {
			val.error(new ValidationError().addKey(RequestEnquiryValidationType.REQUIRE_REQUEST_KIND_OR_CATEGORY.getMessageKey()));
			changed = true;
		} else if (dataModel.getRequestCategory()!=null 
				&& dataModel.getRequestCategory() instanceof RequestCategoryDividerDTO
				&& dataModel.getRequestKind()==null) {
	
			val.error(new ValidationError().addKey(
					RequestEnquiryValidationType.REQUIRE_REQUEST_KIND_OR_CATEGORY_DIVIDER.getMessageKey()));
			changed = true;
		}
			
		// Some date validation
		DateUtil dateUtil = DateUtil.getInstance();
		if (dataModel.getStartDate() != null && dataModel.getEndDate() != null) {
			if (dateUtil.compareDatePart(dataModel.getStartDate(), dataModel.getEndDate()) > 0) {
				val.error(new ValidationError().addKey(RequestEnquiryValidationType.DATE_START_BEFORE_END.getMessageKey()));
				changed = true;
			}
		}
		
		if (changed) {
			AjaxRequestTarget target = RequestCycle.get().find(AjaxRequestTarget.class);
			if (target != null) {
				target.add(feedbackPanel);
			}
		}
		
	}
	
	/**
	 * Create the select button
	 * 
	 * @param id
	 * @param form
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected Button createSelectButton(String id, Form form) {
		Button but = new AjaxButton(id, form) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form form) {
				int selectedCount = searchResultPanelField.getSelectedItems().size();
				if (selectedCount==0) {
					// Give an error???
					error(this.getString(RequestEnquiryValidationType.VIEW_SELECTION_REQUIRED.getMessageKey()));
					
					if (target!=null)
						target.add(feedbackPanel);
					return;
				}
				if (selectedCount>1) {
					// Give an error???
					error(this.getString(RequestEnquiryValidationType.VIEW_SELECTION_MULTIPLE.getMessageKey()));
					if (target!=null)
						target.add(feedbackPanel);
					return;
				}
				
				RequestKindType requestType = ((RequestEnquiryRowDTO)searchResultPanelField.getSelectedItemObjects().get(0)).getRequestKindType();
				
				// Ensure that we support these request kinds.
//				if (pageModel.getAllPropertyOnlyRequestKindSet().contains(requestType)) {					
//					Map<String, String> map = new HashMap<String, String>();
//					map.put("requestKind", requestType.toString());
//					error(this.getString(RequestEnquiryValidationType.VIEW_PROPERTY_ONLY_REQUEST.getMessageKey(),
//							new Model((Serializable) map)));					
//					target.add(feedbackPanel);
//					return;
//				}
				
				// Ensure that we have access
				if (!SRSAuthWebSession.get().getSessionUser().isAllowView(requestType)) {
					error(RequestActionType.VIEW.getRequestSecurityErrorMessage(requestType));
					if (target!=null)
						target.add(feedbackPanel);
					return;
				}
				/*if(RequestKindType.RecordPolicyInfo.equals(requestType)){
					setResponsePage(new PolicyTransactionsPage(((RequestEnquiryRowDTO)searchResultPanelField.getSelectedItemObjects().get(0)),EditStateType.VIEW, false));
				}
				else{*/
					modalViewWindow.show(target);
				/*}*/
				
			}

			@Override
			protected void onComponentTag(ComponentTag tag) {
				super.onComponentTag(tag);
				decorateComponentStyleToHide(dataModel.getSearchResultList() == null
						|| dataModel.getSearchResultList().size() == 0, tag);
			}
			
			
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
	 * Create the export button
	 * 
	 * @param id
	 * @param form
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected WebMarkupContainer createExportButton(String id, Form form) {
		Link link = new Link(id) {

			@Override
			protected void onComponentTag(ComponentTag tag) {
				super.onComponentTag(tag);
				decorateComponentStyleToHide(dataModel.getSearchResultList() == null
						|| dataModel.getSearchResultList().size() == 0, tag);
			}
			
			@Override
			public void onClick() {

				try {
					new GridToCSVHelper().createCSVFromDataGrid(searchResultPanelField,
							"Request_Enquiry.csv");
				} catch (Exception e) {	
					Logger.getLogger(this.getClass()).error(
							"An error occured when trying to generate the excel document",e);
					this.error("Error occurred during export:" + e.getCause());
				}	

			}
		};
		link.setOutputMarkupId(true);
		return link;
//		Button but = new AjaxFallbackButton(id, form) {
//			private static final long serialVersionUID = 1L;
//			
//			@Override
//			protected void onComponentTag(ComponentTag tag) {
//				super.onComponentTag(tag);
//				decorateComponentStyleToHide(dataModel.getSearchResultList() == null
//						|| dataModel.getSearchResultList().size() == 0, tag);
//			}
//			
//			@Override
//			protected void onSubmit(AjaxRequestTarget target, Form form) {
//				System.out.println("Export - submit begin");
//				super.onSubmit(target, form);
//				try {
//					new GridToCSVHelper().createCSVFromDataGrid(searchResultPanelField,
//							"Request_Enquiry.csv");
//				} catch (Exception e) {	
//					Logger.getLogger(this.getClass()).error(
//							"An error occured when trying to generate the excel document",e);
//					this.error("Error occurred during export:" + e.getCause());
//				}	
//				System.out.println("Export - submit end");
//			}
//		};
//		but.setOutputMarkupId(true);
//		return but;
	}
	
	/**
	 * Create the bulk authorise button
	 * 
	 * @param id
	 * @param form
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected Button createBulkAuthOrDeclineButton(String id, Form form, final RequestActionType actionType) {
		
		final String action = actionType.name().toLowerCase(); 
			
		Button but = new AjaxFallbackButton(id, form) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form form) {
				/*
				 * Pre-processing validation
				 */
				int selectedCount = searchResultPanelField.getSelectedItems().size();
				
				// Did we select the minimum?
				if (selectedCount==0) {
					Map<String, String> map = new HashMap<String, String>();
					map.put("action", action);
				
					// Give an error???
					error(this.getString(
							RequestEnquiryValidationType.BULK_SELECTION_REQUIRED.getMessageKey(),
							new Model((Serializable) map)));				
					if (target!=null)
						target.add(feedbackPanel);
					return;
				}
				
				// Are we within the limits
				if (selectedCount > ((actionType==RequestActionType.AUTHORISE) 
						? dataModel.getBulkAuthoriseType().getMaxAuthCount() 
								:  dataModel.getBulkAuthoriseType().getMaxDeclineCount())) {
					
					Map<String, String> map = new HashMap<String, String>();
					map.put("action", action);
					map.put("type", dataModel.getBulkAuthoriseType().getDescription());
					map.put("max", ""+dataModel.getBulkAuthoriseType().getMaxAuthCount());
					
					// Give an error???
					error(this.getString(RequestEnquiryValidationType.BULK_SELECTION_MAX.getMessageKey(),
							new Model((Serializable) map)));
					if (target!=null)
						target.add(feedbackPanel);
					return;
				}
				
				List<RequestEnquiryRowDTO> selectedRequestList = (List)searchResultPanelField.getSelectedItemObjects();	
				// Is status correct
				if (!isCorrectStatus(selectedRequestList, RequestStatusType.REQUIRES_AUTHORISATION)) {
					Map<String, String> map = new HashMap<String, String>();
					map.put("action", action);
					map.put("type", "authorise");
					map.put("status", RequestStatusType.REQUIRES_AUTHORISATION.toString());
					
					// Give an error???
					error(this.getString(
							RequestEnquiryValidationType.BULK_STATUS_INVALID.getMessageKey(),
							new Model((Serializable) map)));
					if (target!=null)
						target.add(feedbackPanel);
					return;
				}
				
				/*
				 * Do the authorisation or decline
				 */
				BulkAuthDeclineResultDTO resultDto = null;
				if (actionType==RequestActionType.AUTHORISE) {
					resultDto = getGuiController().bulkAuthoriseRequests(
							SRSAuthWebSession.get().getSessionUser(),
							selectedRequestList);
				} else {
					resultDto = getGuiController().bulkDeclineRequests(
							SRSAuthWebSession.get().getSessionUser(),
							selectedRequestList);
				}
				
				if (!resultDto.isSuccess()) {
					// Add all the error messages
					for (RequestEnquiryRowDTO dto : resultDto.getFailureIdMessageMap().keySet()) {
						String message = resultDto.getFailureIdMessageMap().get(dto);
						
						Map<String, String> map = new HashMap<String, String>();
						map.put("requestId", ""+dto.getRequestId());
						map.put("message", message);
						error(this.getString(RequestEnquiryValidationType.BULK_ERROR.getMessageKey(),	
								new Model((Serializable) map)));
					}
					if (target!=null)
						target.add(feedbackPanel);
				}
				
				
				// Show final messages
				Map<String, String> map = new HashMap<String, String>();
				map.put("action", action);
				
				if (resultDto.getSuccessIdList().size()==selectedRequestList.size()) {
					info(this.getString(RequestEnquiryValidationType.BULK_SUCCESS.getMessageKey(),
						new Model((Serializable) map)));
				} else {
					StringBuilder builder = new StringBuilder();
					for (RequestEnquiryRowDTO id : resultDto.getSuccessIdList()) {
						if (builder.length()>0) {
							builder.append(", ");
						}
						builder.append(id.getRequestId());
					}
					map.put("requestIds", builder.toString());
					info(this.getString(RequestEnquiryValidationType.BULK_SUCCESS_PARTIAL.getMessageKey(),
							new Model((Serializable) map)));
				}
				
				/*
				 * Unselect successful ones 
				 */
				// Remove succesfull ones from list and reset selection
				selectedRequestList.removeAll(resultDto.getSuccessIdList());
				List<IModel> originalSelectedList = new ArrayList<IModel>((Collection<IModel>)searchResultPanelField.getSelectedItems());
				searchResultPanelField.resetSelectedItems();
				
				// Re-select failures by using original model object
				for (IModel model : originalSelectedList) {
					for (int i = 0; i < selectedRequestList.size(); ++i) {
						RequestEnquiryRowDTO dto = selectedRequestList.get(0);
						if (dto==model.getObject()) {
							searchResultPanelField.selectItem(model,true);
							selectedRequestList.remove(i--);
							break;
						}
					}
				}
								
				if (target!=null) {
					target.add(feedbackPanel);
					target.add(searchResultPanelField);
				}
			}

			@Override
			protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
				super.updateAjaxAttributes(attributes);
			        
			        // SRS Convenience method for overLay hiding/showing
			        attributes.getAjaxCallListeners().add(new SRSAjaxCallListener());
			}

			@Override
			protected void onComponentTag(ComponentTag tag) {
				super.onComponentTag(tag);
				decorateComponentStyleToHide(dataModel.getSearchResultList() == null
						|| dataModel.getSearchResultList().size() == 0
						|| dataModel.getBulkAuthoriseType()==null, tag);
			}
			
			
		};
		but.setOutputMarkupId(true);
		return but;
	}
	
	/**
	 * Ensure the 
	 * 
	 * @param selectedRequestList
	 * @param status
	 * @return
	 */
	private boolean isCorrectStatus(List<RequestEnquiryRowDTO> selectedRequestList, RequestStatusType status) {
		for (RequestEnquiryRowDTO dto : selectedRequestList ) {
			if (dto.getStatusType()!=status) {
				return false;
			}
		}
		return true;
	}
	
	// ==============================================================================================
	// Generate fields
	// ==============================================================================================

	/**
	 * Create the request category type field
	 * 
	 * @param string
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private DropDownChoice createRequestCategoryTypeField(String id) {

		IModel model = new IModel() {
			private static final long serialVersionUID = 1L;
			
			public Object getObject() {
				return dataModel.getRequestCategory();
			}
			public void setObject(Object arg0) {
				dataModel.setRequestCategory((RequestCategoryDTO) arg0);
			}
			public void detach() {	
			}
		};
		
		DropDownChoice field = new DropDownChoice(id, model, pageModel.getAllRequestCategoryList(), 
				new SRSAbstractChoiceRenderer() {
					private static final long serialVersionUID = -4367276358153378234L;
					
					public Object getDisplayValue(Object value) {
						return (value==null)?null:((RequestCategoryDTO)value).getName();
					}
					public String getIdValue(Object arg0, int arg1) {
						return arg1+"";
					}
		});
		
		// Update the request kinds field when changing this one
		AjaxFormComponentUpdatingBehavior behaviour = new AjaxFormComponentUpdatingBehavior("change") {
			private static final long serialVersionUID = 1L;
			
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				updateShowNextButton(target);
				
				// The request category has changed, update the list of request kinds.
				RequestCategoryDTO dto = dataModel.getRequestCategory();
				pageModel.getAllRequestKindTypeList().clear();
				pageModel.getAllRequestKindTypeList().addAll(getGuiController().getAllRequestKindTypeListForCategory(dto));
				target.add(requestKindTypeField);
			}

		};
		field.add(behaviour);
		field.setNullValid(true);
		return field;
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
				updateShowNextButton(target);
			}		
		});
		return field;
	}

	/**
	 * Create the request status type field.
	 * 
	 * @param string
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private DropDownChoice createRequestStatusTypeField(String id) {
		
		IModel model = new IModel() {
			private static final long serialVersionUID = 1L;
			
			public Object getObject() {
				return dataModel.getRequestStatus();
			}
			public void setObject(Object arg0) {
				dataModel.setRequestStatus((RequestStatusType) arg0);
			}
			public void detach() {	
			}
		};
		
		DropDownChoice field = new DropDownChoice(id, model, pageModel.getAllRequestStatusTypeList());
		field.setNullValid(true);
		field.add(new AjaxFormComponentUpdatingBehavior("change") {
			private static final long serialVersionUID = 1L;
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				updateShowNextButton(target);
			}		
		});
		return field;
	}

	/**
	 * Create the agreement kind type field
	 * 
	 * @param string
	 * @return
	 */
	@SuppressWarnings("unchecked" )
	protected DropDownChoice createAgreementKindTypeField(String id) {
		IModel model = new IModel() {
			private static final long serialVersionUID = 1L;
			
			public Object getObject() {
				return dataModel.getAgreementKind();
			}
			public void setObject(Object arg0) {
				dataModel.setAgreementKind((AgreementKindType) arg0);
			}
			public void detach() {	
			}
		};
		DropDownChoice field = new DropDownChoice(id, model, pageModel.getAllAgreementKindTypeList());
		field.setNullValid(true);
		field.add(new AjaxFormComponentUpdatingBehavior("change") {
			private static final long serialVersionUID = 1L;
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				updateShowNextButton(target);
			}		
		});
		return field;
	}
	
	/**
	 * Create the request date type field
	 * 
	 * @param string
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private DropDownChoice createRequestDateTypeField(String id) {
		IModel model = new IModel() {
			private static final long serialVersionUID = 1L;
			
			public Object getObject() {
				return dataModel.getRequestDateType();
			}
			public void setObject(Object arg0) {
				dataModel.setRequestDateType((RequestDateType) arg0);
			}
			public void detach() {	
			}
		};
		DropDownChoice field = new DropDownChoice(id, model, pageModel.getAllRequestDateType());
		field.add(new AjaxFormComponentUpdatingBehavior("change") {
			private static final long serialVersionUID = 1L;
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				updateShowNextButton(target);
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
				updateShowNextButton(target);
				
				RequestEnquiryPeriodDTO dto = dataModel.getRequestEnquiryPeriod();
				if (dto!=null) {
					target.add(startDateField);
					target.add(endDateField);
					// TODO #WICKETFIX - Add this back later  
//					target.add(startDatePicker);
//					target.add(endDatePicker);
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
	/* SSM2707 Market Integration 28/09/2015 SWETA MENON Begin */
	/*
	 * Access modifier changed to Protected to allow
	 * ServicingPanelsAuthorisationSearchPanel to access the method.
	 */
	protected SRSDateField createStartDateField(String id) {
		SRSDateField text = new SRSDateField(id,  new IModel<Date>() {
			private static final long serialVersionUID = -1060562129103084694L;

			public Date getObject() {
				return dataModel.getStartDate();
			}
			public void setObject(Date arg0) {
				dataModel.setStartDate((Date) arg0);			
			}
			public void detach() {			
			}
		});
		text.add(createDateFieldUpdateBehavior("change"));
		text.setOutputMarkupId(true);
		
		
		text.add(text.newDatePicker());
		
		
		return text;
	}
	
	
	/**
	 * Create start date picker, includes the startDate field.
	 * 
	 * @param string
	 * @return
	 */
	/* SSM2707 Market Integration 28/09/2015 SWETA MENON Begin */
	/*
	 * Access modifier changed to Protected to allow
	 * ServicingPanelsAuthorisationSearchPanel to access the method.
	 * 
	 * #WICKETFIX TODO Jean to add back 
	 */
	
	protected Panel createStartDatePicker(String id, SRSDateField dateField) {	
		return new EmptyPanel(id);
//		DatePicker picker = new DatePicker(id, dateField);
//				
//				new PopupDatePicker(id, dateField);
//		picker.setOutputMarkupId(true);
//		return picker;
	}
	
	/**
	 * Create End Date field
	 * 
	 * @param id
	 * @return
	 */
	/* SSM2707 Market Integration 28/09/2015 SWETA MENON Begin */
	/*
	 * Access modifier changed to Protected to allow
	 * ServicingPanelsAuthorisationSearchPanel to access the method.
	 */
	@SuppressWarnings("unchecked")
	protected SRSDateField createEndDateField(String id) {
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
		text.setOutputMarkupId(true);
		text.add(text.newDatePicker());
		return text;
	}
	/** 
	 * Create the end date picker, includes the endDateField.
	 * 
	 * @param string
	 * @return
	 */
	/* SSM2707 Market Integration 28/09/2015 SWETA MENON Begin */
	/*
	 * Access modifier changed to Protected to allow
	 * ServicingPanelsAuthorisationSearchPanel to access the method.
	 * 
	 * #WICKETFIX TODO Jean to add back 
	 * In Wicket 1.7 we don't have PopupDatePicker anymore, we now only add a behaviour.  So default this to return 
	 * an empty panel
	 * 
	 */
	protected Panel createEndDatePicker(String id, SRSDateField dateField) {
		return new EmptyPanel(id);
//		DatePicker picker = new PopupDatePicker(id, dateField);
//		picker.setOutputMarkupId(true);
//		return picker;
	}
	
	/**
	 * Create the modal view window
	 * 
	 * @param id
	 * @return
	 */
	public ModalWindow createModalViewWindow(String id) {
		final ModalWindow window = new ModalWindow(id);
		
		window.setTitle("View Request");
		
		window.setCookieName(VIEW_WINDOW_COOKIE_NAME);

		
		// Create the page
		window.setPageCreator(new ModalWindow.PageCreator() {
			private static final long serialVersionUID = 1L;
			public Page createPage() {
				Page page = createViewWindowPage(window, (RequestEnquiryRowDTO)
						((IModel)searchResultPanelField.getSelectedItems().iterator().next()).getObject());
				viewWindowPage  = page.getPageReference();
				return page;
			}
		});
		
		// Close window call back
		window.setWindowClosedCallback(new ModalWindow.WindowClosedCallback() {
			private static final long serialVersionUID = 1L;
			
			public void onClose(AjaxRequestTarget target) {
				refreshCurrentlySelectedObject(target);
			}
			
		});

		// Initialise window settings
		window.setMinimalHeight(420);
		window.setInitialHeight(520);
		window.setMinimalWidth(850);
		window.setInitialWidth(850);
		window.setMaskType(MaskType.SEMI_TRANSPARENT);
		window.setCssClassName(ModalWindow.CSS_CLASS_GRAY);
		// #WICKETTEST - Check this
		window.setCookieName(VIEW_WINDOW_PAGE_MAP);
//		window.setPageMapName(VIEW_WINDOW_PAGE_MAP);
		
		return window;
	}
	
	/**
	 * Create an the ViewRequest window page.
	 * 
	 * @param window
	 * @return
	 */
	protected Page createViewWindowPage(ModalWindow window, RequestEnquiryRowDTO dto) {
		if (getLogger().isDebugEnabled())
				getLogger().debug("Create viewWindowPage - window=" + window 
						+ "   ,dto=" + dto + "  -" + ((dto!=null)?dto.getPartyName() : "null"));
		return new ViewRequestWindowPage(window, dto);

		
	}

	// ==============================================================================================
	// Common behaviour logic
	// ==============================================================================================
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
				updateShowNextButton(target);
				
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
	
	/**
	 * Decorate the style tag to hide the component
	 * 
	 * @param isHidden Hide component if true. 
	 * @param tag
	 */
	private void decorateComponentStyleToHide(boolean isHidden, ComponentTag tag) {
		if (!isHidden) {
			return;
		}
		String val = (String) tag.getAttributes().get("style");
		val = (val ==null) ? "" : val;
		val += " ;visibility:hidden;";
		tag.put("style", val);
	}
	
	/**
	 * Return an instance to the GuiController bean for this page.
	 * 
	 * @return
	 */
	protected IRequestEnquiryGuiController getGuiController() {
		
		if (guiController==null) {
			try {
				guiController = ServiceLocator.lookupService(IRequestEnquiryGuiController.class);
			} catch (NamingException e) {
				throw new CommunicationException(e);
			}
		}
		return guiController;
	}
	
	/**
	 * Disable the next button as a componenet has  been updated.
	 * 
	 * @param target
	 */
	protected void updateShowNextButton(AjaxRequestTarget target) {
		if (dataModel.isShowNextButton()) {
			dataModel.setShowNextButton(false);
			if (target!=null)
				target.add(nextButton);
		}
	}
	
	/**
	 * Refresh the currently selected row object
	 * 
	 * @param target
	 */
	@SuppressWarnings("unchecked")
	private void refreshCurrentlySelectedObject(AjaxRequestTarget target) {
		List<RequestEnquiryRowDTO> selectedRequestList = (List)searchResultPanelField.getSelectedItemObjects();
		try {
			getGuiController().refresh(selectedRequestList);
			// Refresh table to ensure status updates reflect.
			if (target!=null)
				target.add(searchResultPanelField);
		} catch (QueryTimeoutException e) {
			this.error(e.getMessage());
			return;
		} catch (RequestException e) {
			this.error(e.getMessage());
			return;
		}
	}

	/**
	 * Creates the data grid containing the totals for DPE
	 * @param id the name of the grid
	 * @return {@link SRSDataGrid}
	 */
	protected SRSDataGrid createTotalsPanel(String id) {
		totalsColumns = new ArrayList<IGridColumn>();
		totalsColumns.addAll(ResultTableColumnLayoutEnum.DPE_SEARCH_TOTAL.getColumnList());
		
		SRSDataGrid totalsGrid = new SRSDataGrid(id, new SRSDataProviderAdapter( new ListDataProvider(dataModel.getDPETotals())),  totalsColumns, null);
		
		totalsGrid.setAutoResize(false);
		totalsGrid.setRowsPerPage(2);
		totalsGrid.setContentHeight(15, SizeUnit.PX);
		totalsGrid.setAllowSelectMultiple(dataModel.getBulkAuthoriseType()!=null);
		totalsGrid.setCleanSelectionOnPageChange(dataModel.getBulkAuthoriseType()==null);
		totalsGrid.setOutputMarkupId(true);
//		if(!(this instanceof RequestPolicyTransactionPanel))
//			totalsGrid.setVisible(false);
//		
		return totalsGrid;
	}
	
	/**
	 * Return a logger instance
	 * 
	 * @return
	 */
	public Logger getLogger() {
		if (logger == null) {
			logger = Logger.getLogger(this.getClass());
		}
		return logger;
	}
}
