package za.co.liberty.web.pages.transactions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow.MaskType;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.ValidationError;

import com.inmethod.grid.IGridColumn;
import com.inmethod.grid.SizeUnit;
import com.inmethod.grid.column.PropertyColumn;

import za.co.liberty.business.guicontrollers.transactions.TransactionRejectsGuiController;
import za.co.liberty.dto.gui.context.AgreementSearchType;
import za.co.liberty.dto.gui.context.PolicyTransactionTypeEnum;
import za.co.liberty.dto.gui.context.RejectsErrorFlagType;
import za.co.liberty.dto.pretransactionreject.PreTransactionRejectSearchDTO;
import za.co.liberty.dto.pretransactionreject.RejectElementDTO;
import za.co.liberty.exceptions.data.ValidationException;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.helpers.lookup.ServiceLocator;
import za.co.liberty.helpers.util.DateUtil;
import za.co.liberty.interfaces.agreements.requests.RequestKindType;
import za.co.liberty.srs.integration.util.ErrorCode;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.pages.panels.BasePanel;
import za.co.liberty.web.pages.panels.HelperPanel;
import za.co.liberty.web.pages.request.model.ResultTableColumnLayoutEnum;
import za.co.liberty.web.pages.transactions.model.TransactionRejectsModel;
import za.co.liberty.web.wicket.markup.html.form.SRSAbstractChoiceRenderer;
import za.co.liberty.web.wicket.markup.html.form.SRSDateField;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGrid;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGridColumn;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataProviderAdapter;
import za.co.liberty.web.wicket.markup.repeater.data.SortableListDataProvider;

/**
 * Main panel for rejects enabling reject searches and routes to the relevant transaction page for 
 * edits.
 * 
 */
public class TransactionRejectsPanel extends BasePanel {

	private static final long serialVersionUID = 6399715166895884087L;
	private static Logger logger = Logger.getLogger(TransactionRejectsPanel.class);

	private transient TransactionRejectsGuiController transactionRejectsGuiController;
	
	private Form<?> searchForm;
	private SRSDateField fromDateField;
	private SRSDateField toDateField;

	private SRSDataGrid searchResultPanelField;
	private ModalWindow xmlMessageWindow;
	private ModalWindow editMessageWindow;
	private List<IGridColumn> searchResultColumns;

	private final String SEARCH_RESULT_PANEL_ID = "searchResultPanel";

	protected static final List<String> transactionSearchTypeList;
	protected static final List<RejectsErrorFlagType> flagTypeList;
	protected static final List<AgreementSearchType> agreementNumberTypeList;
	
	static {
		// Initialise some additional types based on enums
		flagTypeList = new ArrayList<RejectsErrorFlagType>();
		for (RejectsErrorFlagType errorFlagType : RejectsErrorFlagType.values()) {
			flagTypeList.add(errorFlagType);
		}

		transactionSearchTypeList = new ArrayList<String>();
		for (PolicyTransactionTypeEnum transactionType : PolicyTransactionTypeEnum.values()) {
			transactionSearchTypeList.add(transactionType.getLabel());
		}
		
		
		// Initialise all static variables
		agreementNumberTypeList = new ArrayList<AgreementSearchType>();
		for (AgreementSearchType t : AgreementSearchType.values()) {
			agreementNumberTypeList.add(t);
		}
	}

	private final TransactionRejectsModel pageModel;

	/**
	 * Default constructor
	 * 
	 * @param id
	 * @param model
	 */
	public TransactionRejectsPanel(String id, IModel<?> model) {
		super(id, EditStateType.VIEW, null);
		pageModel = (TransactionRejectsModel) model.getObject();
		add(searchForm = createSearchForm("rejectsSearchForm"));
		add(searchResultPanelField = createSearchResultPanel(SEARCH_RESULT_PANEL_ID));
		add(xmlMessageWindow = createXMLMessageModalWindow("xmlMessageWindow", null, null));
		add(editMessageWindow = createEditXMLMessageModalWindow("editMessageWindow", null,null));
		searchResultPanelField.setOutputMarkupId(true);
	}

	/**
	 * Create the form
	 * 
	 * @param id
	 * @return
	 */
	private Form<?> createSearchForm(String id) {
		searchForm = new Form<Object>(id);
		searchForm.add(fromDateField = createFromDateField("fromDate"));
		searchForm.add(toDateField = createToDateField("toDate"));
		searchForm.add(createTransactionTypeField("transactionType"));
		searchForm.add(createFlagFieldType("flagType"));
		searchForm.add(createContractNumberField("contractNumber"));
		searchForm.add(createComponentIdField("componentId"));
		
		searchForm.add(createAgreementType("agreementType"));
		searchForm.add(createAgreementNr("agreementNr"));
		
		searchForm.add(createSearchButton("search"));
		return searchForm;
	}

	/**
	 * create the result table
	 * 
	 * @param id
	 * @return
	 */
	protected SRSDataGrid createSearchResultPanel(String id) {

		/* Create the search result table */
		searchResultColumns = createSearchResultColumns();

		SRSDataGrid grid = new SRSDataGrid(id, new SRSDataProviderAdapter(new SortableListDataProvider<RejectElementDTO>(
				pageModel.getSearchResults())), searchResultColumns, EditStateType.VIEW);
		grid.setAutoResize(false);
		grid.setRowsPerPage(12);
		grid.setContentHeight(270, SizeUnit.PX);
		grid.setGridWidth(99, SRSDataGrid.GridSizeUnit.PERCENTAGE);

		return grid;
	}

	/**
	 * Columns for the table
	 * 
	 * @return
	 */
	protected List<IGridColumn> createSearchResultColumns() {
		List<IGridColumn> columns = new ArrayList<IGridColumn>();
		
		columns.add(new PropertyColumn(new Model<String>("Request"), 
				 "requestKindType", "requestKind").setInitialSize(150));
		
		columns.addAll(ResultTableColumnLayoutEnum.TRANSACTION_REJECTS.getColumnList());

		// Add Stacktrace column
		columns.add(new SRSDataGridColumn<RejectElementDTO>("exceptionChain", new Model<String>("Exception Chain"), "exceptionChain", EditStateType.MODIFY) {

			private static final long serialVersionUID = 1L;

			@Override
			public Panel newCellPanel(WebMarkupContainer parent, String componentId, final IModel rowModel, String objectProperty, EditStateType state, final RejectElementDTO data) {
				if (data.getXmlMessage() != "") {
					Button searchButton = new Button("value", new Model<String>("View Exception"));
					searchButton.add(new AjaxFormComponentUpdatingBehavior("click") {
						private static final long serialVersionUID = 1L;

						@Override
						protected void onUpdate(AjaxRequestTarget target) { 
							ModalWindow window = createXMLMessageModalWindow(
									"xmlMessageWindow", 
									((RejectElementDTO) rowModel.getObject()).getExceptionChain(), 
									"Exception Chain");
							xmlMessageWindow.replaceWith(window);
							xmlMessageWindow = window;
							xmlMessageWindow.show(target);
							target.add(xmlMessageWindow);
						}

					});
					return HelperPanel.getInstance(componentId, searchButton);
				} else {
					return new EmptyPanel(componentId);
				}
			}

		}.setInitialSize(100));

		// Add the XML Message Column
		columns.add(new SRSDataGridColumn<RejectElementDTO>("xmlMessage", new Model<String>("XML Message"), "xmlMessage", EditStateType.MODIFY) {

			private static final long serialVersionUID = 1L;

			@Override
			public Panel newCellPanel(WebMarkupContainer parent, String componentId, final IModel rowModel, String objectProperty, EditStateType state, final RejectElementDTO data) {
				if (data.getXmlMessage() != null && data.getXmlMessage().length()>0) {
					Button searchButton = new Button("value", new Model<String>("View XML"));
					searchButton.add(new AjaxFormComponentUpdatingBehavior("click") {
						private static final long serialVersionUID = 1L;

						@Override
						protected void onUpdate(AjaxRequestTarget target) {
							
							String xml = getGuiController().prettyPrintXML(
									((RejectElementDTO) rowModel.getObject()).getXmlMessage());
							
							ModalWindow window = createXMLMessageModalWindow("xmlMessageWindow", 
									xml, 
									"XML Message");
							// xml
							xmlMessageWindow.replaceWith(window);
							xmlMessageWindow = window;
							xmlMessageWindow.show(target);
							target.add(xmlMessageWindow);
							// currentworkingGridRole = data;
							// searchWindow.show(target);
							System.out.println("XML Message: \n"
									+ ((RejectElementDTO) rowModel.getObject()).getXmlMessage());
						}

					});
					return HelperPanel.getInstance(componentId, searchButton);
				} else {
					return new EmptyPanel(componentId);
				}
			}

		}.setInitialSize(80));

		// Add the edit button
		columns.add(new SRSDataGridColumn<RejectElementDTO>("editButton", new Model<String>(""), "xmlMessage", EditStateType.MODIFY) {

			private static final long serialVersionUID = 1L;

	        //RKS2008 Modified to do a pop up 
			@Override
			public Panel newCellPanel(WebMarkupContainer parent, String componentId, final IModel rowModel, String objectProperty, EditStateType state, final RejectElementDTO data) {
				if (data.getXmlMessage() != null && data.getXmlMessage().length() > 0 
						&& (data.getErrorCode() !=null 
						&& data.getErrorCode().getErrorCode() != ErrorCode.REJECT_CANCELLED.getValue())) {
					
					Button editButton = new Button("value", new Model<String>("Edit"));
					String errorFlag = ((RejectElementDTO) rowModel.getObject()).getErrorFlag();
					editButton.setEnabled((errorFlag!=null && errorFlag.trim().equalsIgnoreCase("B") ));
					editButton.add(new AjaxFormComponentUpdatingBehavior("click") {
						private static final long serialVersionUID = 1L;

						@Override
						protected void onUpdate(AjaxRequestTarget target) {
//							String xmlMessage = ((RejectElementDTO) rowModel.getObject()).getXmlMessage();
							
							/*
							 * pop-up a window allowing editing of the message
							 */
							ModalWindow window = createEditXMLMessageModalWindow("editMessageWindow", 
									((RejectElementDTO) rowModel.getObject()), 
									"Rejected Transaction");
							editMessageWindow.replaceWith(window);
							editMessageWindow = window;
							editMessageWindow.show(target);
							target.add(editMessageWindow);
						
						}
					});
					return HelperPanel.getInstance(componentId, editButton);
				} else {
					return new EmptyPanel(componentId);
				}
			}

		}.setInitialSize(50));

		return columns;
	}
	
	/**
	 * Return an instance to the GuiController bean for this page.
	 * 
	 * @return
	 */
	protected TransactionRejectsGuiController getGuiController() {
		if (transactionRejectsGuiController==null) {
			try {
				transactionRejectsGuiController = ServiceLocator.lookupService(TransactionRejectsGuiController.class);
			} catch (NamingException e) {
				throw new CommunicationException();
			}
		}
		return transactionRejectsGuiController;
	}

	/**
	 * Pop up window for XML message 
	 * 
	 * @param id
	 * @param displayText
	 * @param title
	 * @return
	 */
	private ModalWindow createXMLMessageModalWindow(String id, final String displayText, final String title) {
		final ModalWindow window = new ModalWindow(id);

		window.setPageCreator(new ModalWindow.PageCreator() {

			private static final long serialVersionUID = 6234331649251955077L;

			public Page createPage() {
				return new TransactionRejectXMLMessagePage(window, displayText, title);
			}
		});
		window.setTitle(title);
		window.setMinimalHeight(400);
		window.setInitialHeight(400);
		window.setMinimalWidth(700);
		window.setInitialWidth(700);
		window.setMaskType(MaskType.SEMI_TRANSPARENT);
		window.setCssClassName(ModalWindow.CSS_CLASS_GRAY);
		window.setOutputMarkupId(true);
		window.setOutputMarkupPlaceholderTag(true);
		window.setCookieName("TransactionRejectXMLMessagePage");
		return window;
	}

	
	/**
	 * Pop up window for Editing a reject.
	 * 
	 * @param id
	 * @param rejectElementDTO
	 * @param title
	 * @return
	 */
	private ModalWindow createEditXMLMessageModalWindow(String id, final RejectElementDTO rejectElementDTO, final String title) {
		final ModalWindow window = new ModalWindow(id);

		window.setPageCreator(new ModalWindow.PageCreator() {

			private static final long serialVersionUID = 6234331649251955077L;

			public Page createPage() {
				return new TransactionRejectEditMessagePage(window, rejectElementDTO, title); 
			}
			
			
		});
		window.setCloseButtonCallback(new ModalWindow.CloseButtonCallback() {
			
			@Override
			public boolean onCloseButtonClicked(AjaxRequestTarget target) {
				logger.info("Closed edit reject, search again to refresh.");
				// We need to refresh the search
				doSearch(target);
				return true;
			}
		});
		window.setTitle(title);
		window.setMinimalHeight(400);
		window.setInitialHeight(500);
		window.setMinimalWidth(900);
		window.setInitialWidth(900);
		window.setMaskType(MaskType.SEMI_TRANSPARENT);
		window.setCssClassName(ModalWindow.CSS_CLASS_GRAY);
		window.setOutputMarkupId(true);
		window.setOutputMarkupPlaceholderTag(true);
		window.setCookieName("TransactionRejectXMLMessagePage");
		return window;
	}
	
	/**
	 * Create the search button 
	 * 
	 * @param id
	 * @return
	 */
	private Button createSearchButton(String id) {
		final Button button = new AjaxButton(id, searchForm) {

			private static final long serialVersionUID = -4882643017383628144L;

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {

				doSearch(target);

			}
		};

		button.add(new IValidator<String>() {
			private static final long serialVersionUID = 1L;

			@Override
			public void validate(IValidatable<String> val) {
				try {
					doValidation(val);
				} catch (za.co.liberty.exceptions.data.ValidationException e) {
					for (String msg : e.getErrorMessages()) {
						val.error(new ValidationError().setMessage(msg));
					}
				}

				AjaxRequestTarget target = RequestCycle.get().find(AjaxRequestTarget.class);
				if (target!=null) {
					target.add(getFeedBackPanel());
					target.add(toDateField);
					target.add(fromDateField);
				} else {
					logger.warn("No target for searchButton");
				}
			}
		});
		button.setOutputMarkupId(true);

		return button;
	}

	private void doSearch(AjaxRequestTarget target) {
		PreTransactionRejectSearchDTO rejectSearchDTO = new PreTransactionRejectSearchDTO();
		rejectSearchDTO.setComponentId(pageModel.getComponentId());
		rejectSearchDTO.setContractSchemeNumber(pageModel.getContractNumber());
		rejectSearchDTO.setFromDate(pageModel.getFromDate());
		rejectSearchDTO.setToDate(pageModel.getToDate());
		rejectSearchDTO.setRequestKind((short) pageModel.getTransactionSearchType().getRequestKind().getRequestKind());
		rejectSearchDTO.setAgreementNumberText(pageModel.getAgreementNumber());
		rejectSearchDTO.setAgreementSearchType(pageModel.getAgreementSearchType());
		
		if (pageModel.getFlagType() != null) {
			rejectSearchDTO.setTechnicalFlag(new Short(pageModel.getFlagType().getId()));
		}

		Logger.getLogger(this.getClass()).info("Rejects Search Parameters: From Date = "
				+ pageModel.getFromDate() + ", To Date = "
				+ pageModel.getToDate() + ", Contract Nr = "
				+ pageModel.getContractNumber() + ", RequestKind = "
				+ pageModel.getTransactionSearchType().getDescription()
				+ ", Error Type = "
				+ ((pageModel.getFlagType() != null) ? pageModel.getFlagType().getDescription() : null));

		List<RejectElementDTO> rejectElements;
		try {
			rejectElements = getGuiController().searchRejectedElement(rejectSearchDTO);
		} catch (ValidationException e) {
			for (String err : e.getErrorMessages()) {
				getFeedBackPanel().error(err);
			}	
			
			target.add(getFeedBackPanel());
			return;
		}

		Logger.getLogger(this.getClass()).info("  Reject search result :  recordCount returned =  "
					+ rejectElements.size());
		
		populateAdditionalFields(rejectElements);

		pageModel.getSearchResults().clear();
		pageModel.getSearchResults().addAll((rejectElements));

		// Refresh the Grid
		SRSDataGrid tmpGrid = createSearchResultPanel(SEARCH_RESULT_PANEL_ID);
		searchResultPanelField.replaceWith(tmpGrid);
		searchResultPanelField = tmpGrid;
		searchResultPanelField.setVisible(true);
		
		if (target!=null) {
			target.add(searchResultPanelField);
			target.add(toDateField);
			target.add(fromDateField);
		}
	}
	/**
	 * Validation 
	 * @param val
	 * @throws za.co.liberty.exceptions.data.ValidationException
	 */
	protected void doValidation(IValidatable<String> val) throws za.co.liberty.exceptions.data.ValidationException {
		List<String> errors = new ArrayList<String>();

		if (pageModel.getFromDate() == null) {
			errors.add("From Date is required");
		}
		
		if (pageModel.getToDate() == null) {
			errors.add("To Date is required");
		}

		if (pageModel.getTransactionSearchType() == null) {
			errors.add("Request Kind is required");
		}

		if (pageModel.getFlagType() == null
				|| (RejectsErrorFlagType.ALL.equals(pageModel.getFlagType()) || RejectsErrorFlagType.TECHNICAL.equals(pageModel.getFlagType()))) {
			if (pageModel.getContractNumber() == null) {
				errors.add("Contract Nr is required when Technical errors are included");
			}
		}
		
		if (pageModel.getAgreementSearchType()!=null && (pageModel.getAgreementNumber() == null 
				|| pageModel.getAgreementNumber().trim().length()==0)) {
			errors.add("Agreement number is required when specifying the 'agreement number type' field");
		}

		if (!errors.isEmpty())
			throw new za.co.liberty.exceptions.data.ValidationException(errors);

	}

	/**
	 * Update some values on the reject elements
	 * 
	 * @param rejectElements
	 */
	private void populateAdditionalFields(List<RejectElementDTO> rejectElements) {

		if (rejectElements != null && !rejectElements.isEmpty()) {
			SimpleDateFormat simpleDateFormat =DateUtil.getInstance().getNewDateTimeFormat();
			
			for (RejectElementDTO rejectElementDTO : rejectElements) {
				// Set the Request Kind
				rejectElementDTO.setRequestKindType(RequestKindType.getRequestKindTypeForKind(rejectElementDTO.getRequestKind()).getDescription());

				// Set Error Flag
				if (rejectElementDTO.getErrorCode() != null)
					switch (rejectElementDTO.getErrorCode().getTechnicalFlag()) {
					case 0:
						rejectElementDTO.setErrorFlag("B");
						break;

					case 1:
						rejectElementDTO.setErrorFlag("T");
						break;

					case 2:
						rejectElementDTO.setErrorFlag("C");
						break;

					default:
						rejectElementDTO.setErrorFlag("");
						break;
					}

				// Set Date Format
				
				simpleDateFormat.format(rejectElementDTO.getRejectTimestamp().getTime());
				rejectElementDTO.setFormattedTime(simpleDateFormat.format(rejectElementDTO.getRejectTimestamp().getTime()));

			}
		}

	}

	private DropDownChoice<?> createComponentIdField(String id) {
		IModel<Object> fieldModel = new IModel<Object>() {
			private static final long serialVersionUID = 1L;

			public Object getObject() {
				return pageModel.getComponentId();
			}

			public void setObject(Object arg0) {
				pageModel.setComponentId((String) arg0);
			}

			public void detach() {
			}
		};

		DropDownChoice<?> field = new DropDownChoice<Object>(id, fieldModel, pageModel.getComponentIds(), new SRSAbstractChoiceRenderer<Object>() {

			private static final long serialVersionUID = 1L;

			public Object getDisplayValue(Object value) {
				return (value == null) ? null : (String) value;
			}

			public String getIdValue(Object value, int arg1) {
				return (value == null) ? null : arg1 + "";
			}
		});
		field.setOutputMarkupId(true);

		return field;
	}

	private TextField<?> createContractNumberField(String id) {
		TextField<?> text = new TextField<Object>(id, new IModel<Object>() {
			private static final long serialVersionUID = -1060562129103084694L;

			public Object getObject() {
				return pageModel.getContractNumber();
			}

			public void setObject(Object arg0) {
				pageModel.setContractNumber((String) arg0);
			}

			public void detach() {
			}
		});
		text.add(new AjaxFormComponentUpdatingBehavior("change") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
			}
		});
		text.setOutputMarkupId(true);
		return text;
	}

	private DropDownChoice<?> createTransactionTypeField(String id) {
		IModel<Object> fieldModel = new IModel<Object>() {
			private static final long serialVersionUID = 1L;

			public Object getObject() {
				return pageModel.getTransactionSearchType();
			}

			public void setObject(Object arg0) {
				pageModel.setTransactionSearchType(PolicyTransactionTypeEnum.getPolicyTransactionTypeByLabel((String) arg0));
			}

			public void detach() {
			}
		};

		DropDownChoice<?> field = new DropDownChoice<Object>(id, fieldModel, transactionSearchTypeList, new SRSAbstractChoiceRenderer<Object>() {

			private static final long serialVersionUID = -4104476095538680424L;

			public Object getDisplayValue(Object value) {
				return (value == null) ? null : value;
			}

			public String getIdValue(Object value, int arg1) {

				return (value == null) ? null : value + "";
			}
		});
		field.add(new AjaxFormComponentUpdatingBehavior("change") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				// updateShowNextButton(target);
			}
		});
		
		field.setOutputMarkupId(true);

		return field;
	}

	private DropDownChoice<?> createFlagFieldType(String id) {
		IModel<Object> fieldModel = new IModel<Object>() {
			private static final long serialVersionUID = 1L;

			public Object getObject() {
				return pageModel.getFlagType();
			}

			public void setObject(Object arg0) {
				pageModel.setFlagType((RejectsErrorFlagType) arg0);
			}

			public void detach() {
			}
		};

		DropDownChoice<?> field = new DropDownChoice<Object>(id, fieldModel, flagTypeList, new SRSAbstractChoiceRenderer<Object>() {

			private static final long serialVersionUID = 147879826508304070L;

			public Object getDisplayValue(Object value) {
				return (value == null) ? null : ((RejectsErrorFlagType) value).getDescription();
			}

			public String getIdValue(Object value, int arg1) {
				return (value == null) ? null : ((RejectsErrorFlagType) value).getId()
						+ "";
			}
		});
		field.add(new AjaxFormComponentUpdatingBehavior("change") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				// updateShowNextButton(target);
			}
		});
		field.setOutputMarkupId(true);

		return field;
	}

	/**
	 * Create the to date field
	 * @param id
	 * @return
	 */
	private SRSDateField createToDateField(String id) {
		final SRSDateField text = new SRSDateField(id, new IModel<Date>() {
			private static final long serialVersionUID = -1060562129103084694L;

			public Date getObject() {
				return pageModel.getToDate();
			}

			public void setObject(Date arg0) {
				pageModel.setToDate((Date) arg0);
			}

			public void detach() {
			}
		});
		text.add(new AjaxFormComponentUpdatingBehavior("change") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
//				target.addComponent(text);;
			}
		});
		text.add(text.newDatePicker());
		text.setOutputMarkupId(true);
		text.setLabel(new Model<String>("to date"));
		return text;
	}

	/**
	 * Create the from date field 
	 * @param id
	 * @return
	 */
	private SRSDateField createFromDateField(String id) {
		final SRSDateField text = new SRSDateField(id, new IModel<Date>() {
			private static final long serialVersionUID = -1060562129103084694L;

			public Date getObject() {
				return pageModel.getFromDate();
			}

			public void setObject(Date arg0) {
				pageModel.setFromDate((Date) arg0);
			}

			public void detach() {
			}
		});

		text.add(new AjaxFormComponentUpdatingBehavior("change") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
//				target.addComponent(text);;
			}
		});
		text.add(text.newDatePicker());
		text.setLabel(new Model<String>("from date"));
		text.setOutputMarkupId(true);
		return text;
	}
	
	/**
	 * Agreement number type
	 * @param id
	 * @return
	 */
	private DropDownChoice<?> createAgreementType(String id) {
		IModel<AgreementSearchType> fieldModel = new IModel<AgreementSearchType>() {
			private static final long serialVersionUID = 1L;
			public AgreementSearchType getObject() {
				return pageModel.getAgreementSearchType();
			}
			public void setObject(AgreementSearchType arg0) {
				pageModel.setAgreementSearchType( arg0);
			}
			public void detach() {
			}
		};


		DropDownChoice field = new DropDownChoice(id, fieldModel, agreementNumberTypeList);
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
	 * Create the agreement number field.  Valid values for this
	 * field is determined by the agreementNumberType.
	 * 
	 * @param id
	 * @return
	 */
	private TextField createAgreementNr(String id) {
		IModel model = new IModel() {
			private static final long serialVersionUID = 1L;
			public Object getObject() {
				return pageModel.getAgreementNumber();
			}
			public void setObject(Object arg0) {
				pageModel.setAgreementNumber((String)arg0);
			}
			public void detach() {
			}
		};
		TextField field = new TextField(id, model, String.class);
		field.add(new AjaxFormComponentUpdatingBehavior("change") {
			private static final long serialVersionUID = 1L;
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
			}		
		});
		return field;
	}

}
