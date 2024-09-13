package za.co.liberty.web.pages.reports;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import javax.naming.NamingException;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.util.convert.IConverter;

import com.inmethod.grid.DataProviderAdapter;
import com.inmethod.grid.IGridColumn;
import com.inmethod.grid.SizeUnit;

import za.co.liberty.business.guicontrollers.reports.IReportGUIController;
import za.co.liberty.dto.reports.ReportMaintenanceDTO;
import za.co.liberty.exceptions.SystemException;
import za.co.liberty.helpers.lookup.ServiceLocator;
import za.co.liberty.interfaces.reporting.StatementKindType;
import za.co.liberty.persistence.srs.entity.ReportingStatusEntity;
import za.co.liberty.web.data.enums.ComponentType;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.pages.interfaces.ISecurityPanel;
import za.co.liberty.web.pages.interfaces.ISecurityPanelConfiguration;
import za.co.liberty.web.pages.panels.BasePanel;
import za.co.liberty.web.pages.panels.HelperPanel;
import za.co.liberty.web.pages.reports.model.MaintainReportsPageModel;
import za.co.liberty.web.wicket.convert.converters.FormattedDateConverter;
import za.co.liberty.web.wicket.markup.html.form.SRSDateField;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGrid;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGrid.GridSizeUnit;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGridColumn;
import za.co.liberty.web.wicket.markup.html.grid.SRSGridRowSelectionCheckBox;
import za.co.liberty.web.wicket.markup.repeater.data.ListDataProvider;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class ReportTrackingPanel extends BasePanel implements ISecurityPanel, ISecurityPanelConfiguration{

	private static final long serialVersionUID = 1L;
	
	private MaintainReportsPageModel model;
	private FeedbackPanel feedBackPanel;
	private ReportForm reportForm;
	private SRSDataGrid reportingStatus;
	private Button refreshButton;
	private boolean disabled;
	
	private transient IReportGUIController guiController;
	private SimpleDateFormat simpleFormat = new SimpleDateFormat("dd/MM/yyyy");
	private final SimpleDateFormat fullFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
	private WebMarkupContainer statusPanel;
	private Label startDateLabel;
	private Label endDateLabel;
	private Label lastRunDateLabel;
	protected FormattedDateConverter fullFormatDateConverter = new FormattedDateConverter(fullFormat);
	private Label statementTypeLabel;
	private CheckBox statementProducedCheckBox;
	private Label statementReasonLabel;
	private CheckBox documentContentIdLabel;
	private CheckBox bankTapeProducedCheckBox;
	private CheckBox stopStatementDistributionCheckBox;
	
	private final IConverter statementKindConverter = new IConverter() {
		private static final long serialVersionUID = 1L;
		
		public Object convertToObject(String display, Locale locale) {
			if (display==null) {
				return null;
			}
			return StatementKindType.valueOf(display).getKind();
		}
		public String convertToString(Object value, Locale locale) {
			if (value==null) {
				return "";
			}
			int type = ((Integer)value);
			StatementKindType ret = StatementKindType.getStatementKindTypeForKind(type);
			return ret.getDescription();
		}
	};
	
//	private final IConverter documentIdConverter = new IConverter() {
//		public Object convertToObject(String arg0, Locale arg1) {
//			return arg0.equalsIgnoreCase("no")?0:Long.parseLong(arg0);
//		}
//		public String convertToString(Object arg0, Locale arg1) {
//			long type = arg0==null?0:((Long)arg0);
//			return type>0?"Yes":"No";
//		}
//	};
	private CheckBox documentPrintedCheckbox;
	private CheckBox documentEmailedCheckbox;
	private Label documentReasonLabel;
	private MultiLineLabel errorMessageTextArea;
	private WebMarkupContainer searchContainer;

	private TextField maxResultsTextField;
	private Button searchButton;
	private HelperPanel fromDateField;
	private HelperPanel toDateField;
	//private HelperPanel stopStatementDistributionField;
	private Label electronicDocumentIdLabel;
	private Label documentStatusIdLabel;
	private Label documentStatusLabel;
	private WebMarkupContainer electronicDocumentContainer;
	private Label emailAddressLabel;


	@SuppressWarnings("deprecation")
	public ReportTrackingPanel(String tab_panel_id, MaintainReportsPageModel model, EditStateType editState, FeedbackPanel feedBackPanel) {
		super(tab_panel_id,editState);
		disabled = model==null || model.getSelectedItem()==null;
		this.model = model;
		if (disabled) {
			model.setSelectedItem(new ReportMaintenanceDTO());
		}
		this.feedBackPanel = feedBackPanel;
		add(getReportForm());
	}
	
	public Button getRefreshButton() {
		if (refreshButton==null) {
			refreshButton = new Button("refresh") {
				private static final long serialVersionUID = 1L;

				@Override
				public void onSubmit() {
					super.onSubmit();
				}
			};
			refreshButton.setOutputMarkupId(true);
		}
		return refreshButton;
	}
		
	private SRSDataGrid getReportingStatus() {
		if (reportingStatus==null) {
			if (model.getReportingStatus()==null) {
				if (model.getSelectedItem()==null ||
						model.getSelectedItem().getAgreementId()==null) {
					model.setReportingStatus(Collections.EMPTY_LIST);
				} else {
					model.setReportingStatus(
						getGuiController().getReportingStatusByAgreementNumber(
								model.getSelectedItem().getAgreementId(),
								model.getSearchCriteria()));
				}
			}
			reportingStatus = createNewReportingStatus();
		}
		return reportingStatus;
	}

	private SRSDataGrid createNewReportingStatus() {
		DataProviderAdapter dataProviderAdapter = 
			getDataProviderAdapter(model.getReportingStatus());
		SRSDataGrid ret = new SRSDataGrid("reportingStatus",
				dataProviderAdapter,
				getStatusColumns(),null) {

			private static final long serialVersionUID = 1L;

			@Override
			public void onItemSelectionChanged(IModel arg0, boolean arg1) {
				super.onItemSelectionChanged(arg0, arg1);
				
				model.setCurrentStatus(getReportingStatus().getSelectedItemObjects().size()==0?
						null:(ReportingStatusEntity) 
						getReportingStatus().getSelectedItemObjects().get(0));
				if (model.getCurrentStatus()!=null && 
						model.getCurrentStatus().getElectronicDocumentId()>0) {
					model.setElectronicDocument(getGuiController().findElectronicDocumentForOid(
							model.getCurrentStatus().getElectronicDocumentId()));
				} else {
					model.setElectronicDocument(null);
				}
				updateVisibility();
				
				AjaxRequestTarget target = RequestCycle.get().find(AjaxRequestTarget.class);
				if (target!=null) {
					target.add(getStatusPanel());
				}
			}
		};
		ret.setClickRowToSelect(true);
		ret.setCleanSelectionOnPageChange(false);
		ret.setAllowSelectMultiple(false);
		ret.setGridWidth(99, GridSizeUnit.PERCENTAGE);
		ret.setEnabled(!disabled);
		ret.setOutputMarkupId(true);
		ret.setOutputMarkupPlaceholderTag(true);
		ret.setContentHeight(250, SizeUnit.PX);
		return ret;
	}

	private DataProviderAdapter getDataProviderAdapter(List<ReportingStatusEntity> statusEntries) {
		ListDataProvider<ReportingStatusEntity> listDataProvider = 
			new ListDataProvider<ReportingStatusEntity>(statusEntries);
		return new DataProviderAdapter(listDataProvider);
	}
	
	private List<IGridColumn> getStatusColumns() {
		final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		List<IGridColumn> ret = new ArrayList<IGridColumn>();
		SRSGridRowSelectionCheckBox col = new SRSGridRowSelectionCheckBox("checkBox");
		ret.add(col.setInitialSize(30));
		IGridColumn startColumn = new SimpleDateColumn(sdf,
				"systemDate",new Model("System Date"),"systemDate",getEditState());
		ret.add(startColumn);
		SimpleDateColumn statementDateColumn = new SimpleDateColumn(simpleFormat,
				"statementDate",new Model("Statement Date"),"endDate",getEditState());
		statementDateColumn.setBold(true);
		ret.add(statementDateColumn);
		IGridColumn statementKindColumn = new StatementKindColumn(
				"statementKind",new Model("Statement Kind"),"statementKind",getEditState());
		ret.add(statementKindColumn);
		IGridColumn statementProducedColumn = new YesNoBooleanColumn(
				"statementProduced",new Model("Statement Produced"),
				"statementProduced",getEditState());
		ret.add(statementProducedColumn);
		IGridColumn documentIdColumn = new DocumentIdColumn(
				"documentContentId",new Model("Document Produced"),
				"hasDocument",getEditState());
		ret.add(documentIdColumn);
		return ret;
	}

	

	private Component getReportForm() {
		if (reportForm==null) {
			reportForm = new ReportForm("reportForm");
		}
		return reportForm;
	}
	
	private class ReportForm extends Form {

		private static final long serialVersionUID = 1L;

		public ReportForm(String id) {
			super(id);
			add(getSearchContainer());
			add(getReportingStatus());
			add(getStatusPanel());
			updateVisibility();
		}
		
	}
	
	private IReportGUIController getGuiController() {
		if (guiController==null) {
			try {
				guiController = ServiceLocator.lookupService(IReportGUIController.class);
			} catch (NamingException e) {
				SystemException exception = new SystemException("Could not load Report GUI Controller", 0, 0);
				exception.initCause(e);
				throw exception;
			}
		}
		return guiController;
	}

	public WebMarkupContainer getSearchContainer() {
		if (searchContainer==null) {
			searchContainer = new WebMarkupContainer("searchContainer");
			searchContainer.setOutputMarkupId(true);
			searchContainer.add(getFromDateComponent());
			searchContainer.add(getToDateComponent());
			searchContainer.add(getMaxResultsComponent());
			searchContainer.add(getSearchButton());
		}
		return searchContainer;
	}

	private HelperPanel getToDateComponent() {
		if (toDateField==null) {
			toDateField = createPageField(model, 
					"searchCriteria.start", "searchCriteria.end", 
					ComponentType.DATE_SELECTION_TEXTFIELD, 
					false, true, 
					new EditStateType[] {EditStateType.VIEW}); 
			if (toDateField.getEnclosedObject() instanceof SRSDateField) {
				((SRSDateField) toDateField.getEnclosedObject()).addNewDatePicker();
			}
		}
		return toDateField;
	}

//	private HelperPanel getToStopStatementDistribution() {
//		if (stopStatementDistributionField==null) {
//			stopStatementDistributionField = createPageField(model, 
//					"searchCriteria.start", "searchCriteria.end", 
//					ComponentType.DATE_SELECTION_TEXTFIELD, 
//					false, true, 
//					new EditStateType[] {EditStateType.VIEW}); 
//		}
//		return stopStatementDistributionField;
//	}	
	
	private HelperPanel getFromDateComponent() {
		if (fromDateField==null) {
			fromDateField = createPageField(model, 
					"searchCriteria.start", "searchCriteria.start", 
					ComponentType.DATE_SELECTION_TEXTFIELD, 
					false, true, 
					new EditStateType[] {EditStateType.VIEW}); 
			if (fromDateField.getEnclosedObject() instanceof SRSDateField) {
				((SRSDateField) fromDateField.getEnclosedObject()).addNewDatePicker();
			}
		}
		return fromDateField;
	}

	private Button getSearchButton() {
		if (searchButton==null) {
			searchButton = new Button("searchButton");
			searchButton.add(new AjaxFormComponentUpdatingBehavior("click") {
				private static final long serialVersionUID = 1L;
				@Override
				protected void onUpdate(AjaxRequestTarget target) {
					if ((model.getSearchCriteria().getStart()==null && 
							model.getSearchCriteria().getEnd()!=null) ||
						(model.getSearchCriteria().getStart()!=null &&
								model.getSearchCriteria().getEnd()==null)) {
						ReportTrackingPanel.this.error("Either no dates, or both dates must be specified");
					}
					if (model.getSearchCriteria().getMaxResults()<=0) {
						ReportTrackingPanel.this.error("Maximum results must be a positive integer");
					}
					if (!hasErrorMessage()) {
						model.getReportingStatus().clear();
						model.getReportingStatus().addAll(
								getGuiController().getReportingStatusByAgreementNumber(
										model.getSelectedItem().getAgreementId(), 
										model.getSearchCriteria()));
					}
					target.add(getReportingStatus());
					target.add(getStatusPanel());
					target.add(getFeedBackPanel());
				}
				
			});
			searchButton.setOutputMarkupId(true);
			searchButton.setOutputMarkupPlaceholderTag(true);
		}
		return searchButton;
	}

	
	private TextField getMaxResultsComponent() {
		if (maxResultsTextField==null) {
			maxResultsTextField = new TextField("maxResults",
					new PropertyModel(model,"searchCriteria.maxResults"),
					Integer.class);
			maxResultsTextField.add(new AjaxFormComponentUpdatingBehavior("change") {
			
				private static final long serialVersionUID = 1L;

				@Override
				protected void onUpdate(AjaxRequestTarget arg0) {
				}
			});
		}
		return maxResultsTextField;
	}

	public WebMarkupContainer getStatusPanel() {
		if (statusPanel==null) {
			statusPanel = new WebMarkupContainer("statusPanel");
			statusPanel.setOutputMarkupId(true);
			statusPanel.setOutputMarkupPlaceholderTag(true);
			/**
			 * Reporting Status
			 */
			statusPanel.add(getStatementTypeLabel());
			statusPanel.add(getStartDateLabel());
			statusPanel.add(getEndDateLabel());
			statusPanel.add(getLastRunDateLabel());
			statusPanel.add(getBankTapeProducedComponent());
			statusPanel.add(getStatementProducedComponent());
			statusPanel.add(getStatementReasonComponent());
			statusPanel.add(getDocumentContentIdComponent());
			statusPanel.add(getDocumentPrintedComponent());
			statusPanel.add(getDocumentEmailedComponent());
			statusPanel.add(getEmailAddressComponent());
			statusPanel.add(getStopStatementDistributionComponent());
			
			statusPanel.add(getDocumentReasonComponent());
			statusPanel.add(getErrorMessageComponent());
			/**
			 * Electronic Document
			 */
			statusPanel.add(getElectronicDocumentContainer());
		}
		return statusPanel;
	}
	
	private Label getEmailAddressComponent() {
		if (emailAddressLabel == null) {
			emailAddressLabel = new Label("emailAddress",
					new PropertyModel(model,"currentStatus.emailAddress"));
		}
		return emailAddressLabel;
	}

	private WebMarkupContainer getElectronicDocumentContainer() {
		if (electronicDocumentContainer == null) {
			electronicDocumentContainer = new WebMarkupContainer("electronicDocumentContainer");
			electronicDocumentContainer.setOutputMarkupId(true);
			electronicDocumentContainer.setOutputMarkupPlaceholderTag(true);
			/**
			 * Components
			 */
			electronicDocumentContainer.add(getElectronicDocumentIdComponent());
			electronicDocumentContainer.add(getDocumentStatusIdComponent());
			electronicDocumentContainer.add(getDocumentStatusComponent());
		}
		return electronicDocumentContainer;
	}

	private Label getDocumentStatusComponent() {
		if (documentStatusLabel == null) {
			documentStatusLabel = new Label("documentStatus",
					new PropertyModel(model,"electronicDocument.fileLocation"));
		}
		return documentStatusLabel;
	}

	private Label getDocumentStatusIdComponent() {
		if (documentStatusIdLabel == null) {
			documentStatusIdLabel = new Label("documentStatusId",
					new PropertyModel(model,"electronicDocument.status"));	
		}
		return documentStatusIdLabel;
	}

	private Label getElectronicDocumentIdComponent() {
		if (electronicDocumentIdLabel == null) {
			electronicDocumentIdLabel = new Label("electronicDocumentId",
					new PropertyModel(model,"electronicDocument.oid"));
			
		}
		return electronicDocumentIdLabel;
	}

	private MultiLineLabel getErrorMessageComponent() {
		if (errorMessageTextArea==null) {
			errorMessageTextArea = new MultiLineLabel("errorMessage",
					new PropertyModel(model,"currentStatus.errorMessage"));
		}
		return errorMessageTextArea;
	}

	private Label getDocumentReasonComponent() {
		if (documentReasonLabel==null) {
			documentReasonLabel = new Label("documentReason",
					new PropertyModel(model,"currentStatus.documentReason"));
		}
		return documentReasonLabel;
	}

	private CheckBox getDocumentEmailedComponent() {
		if (documentEmailedCheckbox==null) {
			documentEmailedCheckbox = new CheckBox("documentEmailed",
					new PropertyModel(model,"currentStatus.emailed"));
			documentEmailedCheckbox.setEnabled(false);
		}
		return documentEmailedCheckbox;
	}
	
	private CheckBox getDocumentPrintedComponent() {
		if (documentPrintedCheckbox==null) {
			documentPrintedCheckbox = new CheckBox("documentPrinted",
					new PropertyModel(model,"currentStatus.printed"));
			documentPrintedCheckbox.setEnabled(false);
		}
		return documentPrintedCheckbox;
	}

	private CheckBox getDocumentContentIdComponent() {
		if (documentContentIdLabel==null) {
			documentContentIdLabel = new CheckBox("documentContentId",
					new PropertyModel(model, "currentStatus.hasDocument"));			
			documentContentIdLabel.setEnabled(false);
		}
		return documentContentIdLabel;
	}

	private Label getStatementReasonComponent() {
		if (statementReasonLabel==null) {
			statementReasonLabel = new Label("statementReason",
					new PropertyModel(model,"currentStatus.statementReason"));
		}
		return statementReasonLabel;
	}

	private CheckBox getBankTapeProducedComponent() {
		if (bankTapeProducedCheckBox==null) {
			bankTapeProducedCheckBox = new CheckBox("bankTapeProduced",
					new PropertyModel(model,"currentStatus.bankTape"));
			bankTapeProducedCheckBox.setEnabled(false);
		}
		return bankTapeProducedCheckBox;
	}
	
	private CheckBox getStopStatementDistributionComponent() {
		if (stopStatementDistributionCheckBox==null) {
			stopStatementDistributionCheckBox = new CheckBox("stopStatementDistribution",
					new PropertyModel(model,"currentStatus.stopStatementDistribution"));
			stopStatementDistributionCheckBox.setEnabled(false);
		}
		return stopStatementDistributionCheckBox;
	}
	
	private CheckBox getStatementProducedComponent() {
		if (statementProducedCheckBox==null) {
			statementProducedCheckBox = new CheckBox("statementProduced",
					new PropertyModel(model,"currentStatus.statementProduced"));
			statementProducedCheckBox.setEnabled(false);
		}
		return statementProducedCheckBox;
	}

	private Label getStatementTypeLabel() {
		if (statementTypeLabel==null) {
			statementTypeLabel = new Label("statementKind",
					new PropertyModel(model,"currentStatus.statementKind")) {
				private static final long serialVersionUID = 1L;
				@Override
				public IConverter getConverter(Class arg0) {
					return statementKindConverter;
				}
			};
			
		}
		return statementTypeLabel;
	}

	private Label getLastRunDateLabel() {
		if (lastRunDateLabel==null) {
			lastRunDateLabel = new Label("lastRunDate",
					new PropertyModel(model, "currentStatus.lastRunDate")) {
				private static final long serialVersionUID = 1L;
				@Override
				public IConverter getConverter(Class arg0) {
					return fullFormatDateConverter;
				}
			};
		}
		return lastRunDateLabel;
	}

	private Label getEndDateLabel() {
		if (endDateLabel==null) {
			endDateLabel = new Label("endDate",
					new PropertyModel(model,"currentStatus.endDate")) {
				private static final long serialVersionUID = 1L;
				@Override
				public IConverter getConverter(Class arg0) {
					return fullFormatDateConverter;
				}
			};
		}
		return endDateLabel;
	}

	private Label getStartDateLabel() {
		if (startDateLabel==null) {
			startDateLabel = new Label("startDate",
					new PropertyModel(model,"currentStatus.startDate")) {
				private static final long serialVersionUID = 1L;
				@Override
				public IConverter getConverter(Class arg0) {
					return fullFormatDateConverter;
				}
			};
		}
		return startDateLabel;
	}

	public void updateVisibility() {
		getSearchContainer().setVisible(!disabled);
		getStatusPanel().setVisible(model.getCurrentStatus()!=null);
		getElectronicDocumentContainer().setVisible(model.getElectronicDocument()!=null);
	}
	
	private class SimpleDateColumn extends SRSDataGridColumn<ReportingStatusEntity> {
		private static final long serialVersionUID = 1L;
		
		private SimpleDateFormat dateFormat;
		private boolean bold;

		public SimpleDateColumn(SimpleDateFormat dateFormat,String columnId, IModel headerModel, String objectProperty, EditStateType state) {
			super(columnId, headerModel, objectProperty, state);
			this.dateFormat=dateFormat;
		}

		@Override
		public Panel newCellPanel(WebMarkupContainer parent, String componentId, 
				IModel rowModel, String objectProperty, EditStateType state, ReportingStatusEntity data) {
			/**
			 * Custom converter for date columns 
			 */
			Label lbl = new Label("value",new PropertyModel(data,objectProperty)) {
				private static final long serialVersionUID = 1L;
				@Override
				public IConverter getConverter(Class targetClass) {
					return new FormattedDateConverter(dateFormat);
				}
			};
			if (isBold()) {
				lbl.add(new AttributeModifier("style","font-weight: bold;"));
			}
			return HelperPanel.getInstance(componentId, lbl);
		}

		public boolean isBold() {
			return bold;
		}

		public void setBold(boolean bold) {
			this.bold = bold;
		}
		
	}
	
	private class StatementKindColumn extends SRSDataGridColumn<ReportingStatusEntity> {
		private static final long serialVersionUID = 1L;
		
		public StatementKindColumn(String columnId, IModel headerModel, String objectProperty, EditStateType state) {
			super(columnId, headerModel, objectProperty, state);
		}

		@Override
		public Panel newCellPanel(WebMarkupContainer parent, String componentId, 
				IModel rowModel, String objectProperty, EditStateType state, ReportingStatusEntity data) {
			/**
			 * Custom converter for date columns 
			 */
			Label lbl = new Label("value",new PropertyModel(data,objectProperty)) {
				private static final long serialVersionUID = 1L;
				@Override
				public IConverter getConverter(Class targetClass) {
					return statementKindConverter;
				}
			};
			return HelperPanel.getInstance(componentId, lbl);
		}
	}
	
	private class YesNoBooleanColumn extends SRSDataGridColumn<ReportingStatusEntity> {
		private static final long serialVersionUID = 1L;
		public YesNoBooleanColumn(String columnId, IModel headerModel, String objectProperty, EditStateType state) {
			super(columnId, headerModel, objectProperty, state);
		}

		@Override
		public Panel newCellPanel(WebMarkupContainer parent, String componentId, 
				IModel rowModel, String objectProperty, EditStateType state, ReportingStatusEntity data) {
			/**
			 * Custom converter for date columns 
			 */
			Label lbl = new Label("value",new PropertyModel(data,objectProperty)) {
				private static final long serialVersionUID = 1L;
				@Override
				public IConverter getConverter(Class targetClass) {
					return new IConverter() {
						private static final long serialVersionUID = 1L;
						public Object convertToObject(String arg0, Locale arg1) {
							return arg0.equalsIgnoreCase("yes");
						}
						public String convertToString(Object arg0, Locale arg1) {
							boolean type = ((Boolean)arg0);
							return type?"Yes":"No";
						}
					};
				}
			};
			return HelperPanel.getInstance(componentId, lbl);
		}
	}
	
	private class DocumentIdColumn extends SRSDataGridColumn<ReportingStatusEntity> {
		private static final long serialVersionUID = 1L;
		public DocumentIdColumn(String columnId, IModel headerModel, String objectProperty, EditStateType state) {
			super(columnId, headerModel, objectProperty, state);
		}

		@Override
		public Panel newCellPanel(WebMarkupContainer parent, String componentId, 
				IModel rowModel, String objectProperty, EditStateType state, ReportingStatusEntity data) {
			/**
			 * Custom converter for date columns 
			 */
			Label lbl = new Label("value",new PropertyModel(data,objectProperty));
			return HelperPanel.getInstance(componentId, lbl);
		}
	}

	public FeedbackPanel getFeedBackPanel() {
		return feedBackPanel;
	}

	public void setFeedBackPanel(FeedbackPanel feedBackPanel) {
		this.feedBackPanel = feedBackPanel;
	}

	public Class getPanelClass() {
		return ReportTrackingPanel.class;
	}

	public boolean isRemoveNoAccessPanel() {
		return true;
	}

}
