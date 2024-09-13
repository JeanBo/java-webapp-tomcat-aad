package za.co.liberty.web.pages.reports;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.naming.NamingException;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;

import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow.MaskType;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBoxMultipleChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.convert.IConverter;

import com.inmethod.grid.DataProviderAdapter;
import com.inmethod.grid.IGridColumn;
import com.inmethod.grid.SizeUnit;

import za.co.liberty.business.guicontrollers.reports.IReportGUIController;
import za.co.liberty.dto.reports.ReportDatesDTO;
import za.co.liberty.dto.reports.ReportMaintenanceDTO;
import za.co.liberty.dto.reports.ReportMaintenanceDTO.ReportType;
import za.co.liberty.exceptions.SystemException;
import za.co.liberty.exceptions.reporting.DuplicateReportException;
import za.co.liberty.helpers.lookup.ServiceLocator;
import za.co.liberty.interfaces.reporting.StatementKindType;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.pages.interfaces.IMaintenanceParent;
import za.co.liberty.web.pages.panels.BasePanel;
import za.co.liberty.web.pages.panels.HelperPanel;
import za.co.liberty.web.pages.reports.model.MaintainReportsPageModel;
import za.co.liberty.web.system.SRSAuthWebSession;
import za.co.liberty.web.wicket.convert.converters.FormattedDateConverter;
import za.co.liberty.web.wicket.markup.html.form.SRSAbstractChoiceRenderer;
import za.co.liberty.web.wicket.markup.html.form.SRSDropDownChoice;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGrid;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGrid.GridSizeUnit;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGridColumn;
import za.co.liberty.web.wicket.markup.html.grid.SRSGridRowSelectionCheckBox;
import za.co.liberty.web.wicket.markup.repeater.data.ListDataProvider;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class ReportGenerationPanel extends BasePanel {

	private static final long serialVersionUID = 1L;
	
	private MaintainReportsPageModel model;
	private FeedbackPanel feedBackPanel;
	private ReportForm reportForm;
	private RepeatingView leftPanel;
	private SRSDropDownChoice reportCategory;
	private SRSDataGrid reportingPeriods;
	private CheckBoxMultipleChoice statementGroup;
	private AjaxButton generateReportsButton;
	private boolean disabled;
	
	private transient IReportGUIController guiController;
	private Label statementLabel;
	private SRSDropDownChoice reportingMonths;
	private Label reportingMonthsLabel;
	private ModalWindow notificationWindow;
	private Component timeRestrictionLabel;
	private boolean timeRestrictionInEffect;
	private WebMarkupContainer timeRestrictionContainer;

	@SuppressWarnings("deprecation")
	public ReportGenerationPanel(String tab_panel_id, MaintainReportsPageModel model, IMaintenanceParent page, EditStateType editState, FeedbackPanel feedBackPanel) {
		super(tab_panel_id,editState);
		initDisabled(model,page);
		this.model = model;
		this.feedBackPanel = feedBackPanel;
		add(getNotificationWindow());
		add(getReportForm());
	}

	private void initDisabled(MaintainReportsPageModel model, IMaintenanceParent page) {
		timeRestrictionInEffect = getGuiController().isTimeRestrictionInEffect(
				new Date(),model.getSelectedItem(), SRSAuthWebSession.get().getSessionUser());
		disabled = model==null || model.getSelectedItem()==null;
		if (disabled) {
			model.setSelectedItem(new ReportMaintenanceDTO());
		}
		disabled|=timeRestrictionInEffect;
		/**
		 * If edit state is not add then disable
		 */
		disabled|=!page.hasAddAccess();
	}
	
	public AjaxButton getGenerateReportButton() {
		if (generateReportsButton==null) {
			generateReportsButton = new AjaxButton("generateReport") {
				private static final long serialVersionUID = 1L;
			@Override
			protected void onSubmit(AjaxRequestTarget target, Form form) {
				if (model.getSelectedItem().getSelectedStatements()==null ||
						model.getSelectedItem().getSelectedStatements().size()==0) {
					ReportGenerationPanel.this.error("At least one statement kind must be selected");
				} else if (getReportingPeriods().getSelectedItemObjects()==null ||
						getReportingPeriods().getSelectedItemObjects().size()==0) {
					ReportGenerationPanel.this.error("At least one reporting period must be selected");
				} else {
					List<Object> selectedReportDates = 
						getReportingPeriods().getSelectedItemObjects();
					List<ReportDatesDTO> reportDates = 
						new ArrayList<ReportDatesDTO>();
					for (Object obj : selectedReportDates) {
						if (obj instanceof ReportDatesDTO) {
							reportDates.add((ReportDatesDTO)obj);
						}
					}
					try {
						getGuiController().sendReportMessages(model.getSelectedItem(),reportDates);
						info(reportDates.size()+" Report message(s) scheduled for processing");
						getNotificationWindow().show(target);
					} catch (DuplicateReportException e) {
						error(e.getMessage());
					}
					
				}
				target.add(feedBackPanel);
				}
			};
			generateReportsButton.setOutputMarkupId(true);
		}
		return generateReportsButton;
	}
	
	public CheckBoxMultipleChoice getStatementGroup() {
		if (statementGroup==null) {
			if (model.getSelectedItem().getStatementKinds()==null) {
				model.getSelectedItem().setStatementKinds(Collections.EMPTY_LIST);
			}
			if (model.getSelectedItem().getSelectedStatements()==null) {
				model.getSelectedItem().setSelectedStatements(new ArrayList<StatementKindType>());
			}
			statementGroup = new CheckBoxMultipleChoice("group",
					new PropertyModel(model.getSelectedItem(),"selectedStatements"),
					new PropertyModel(model.getSelectedItem(),"statementKinds"),
					new SRSAbstractChoiceRenderer<Object>() {
						private static final long serialVersionUID = 1L;
						public Object getDisplayValue(Object arg0) {
							return ((StatementKindType)arg0).getDescription();
						}
						public String getIdValue(Object arg0, int arg1) {
							return ""+arg1;
						}
				
			});
			statementGroup.setOutputMarkupId(true);
		}
		return statementGroup;
	}
	
	private SRSDataGrid getReportingPeriods() {
		if (reportingPeriods==null) {
			reportingPeriods = createNewReportingPeriods(null);
		}
		return reportingPeriods;
	}

	private SRSDataGrid createNewReportingPeriods(List<ReportDatesDTO> reportingDates) {
		if (model.getSelectedItem().getReportDates()==null) {
			model.getSelectedItem().setReportDates(Collections.EMPTY_LIST);
		}
		DataProviderAdapter dataProviderAdapter = 
			getDataProviderAdapter(//reportingDates!=null?reportingDates:
				model.getSelectedItem().getReportDates());
		SRSDataGrid ret = new SRSDataGrid("reportingPeriods",
				dataProviderAdapter,
				getReportingColumns(),null);
		ret.setCleanSelectionOnPageChange(false);
		ret.setGridWidth(99, GridSizeUnit.PERCENTAGE);
		ret.setEnabled(!disabled);
		ret.setOutputMarkupId(true);
		ret.setOutputMarkupPlaceholderTag(true);
		ret.setContentHeight(100, SizeUnit.PX);
		return ret;
	}

	private DataProviderAdapter getDataProviderAdapter(List<ReportDatesDTO> reportDates) {
		ListDataProvider<ReportDatesDTO> listDataProvider = new ListDataProvider<ReportDatesDTO>(reportDates);
		return new DataProviderAdapter(listDataProvider);
	}
	
	private List<IGridColumn> getReportingColumns() {
		final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		final SimpleDateFormat statementDate = new SimpleDateFormat("dd/MM/yyyy");
		List<IGridColumn> ret = new ArrayList<IGridColumn>();
		SRSGridRowSelectionCheckBox col = new SRSGridRowSelectionCheckBox("checkBox");
		ret.add(col.setInitialSize(30));
		IGridColumn statementDateColumn = new SRSDataGridColumn<ReportDatesDTO>(
				"statementDate",new Model("Statement Date"),"endDate",getEditState()) {
			private static final long serialVersionUID = 1L;
			@Override
			public Panel newCellPanel(WebMarkupContainer parent, String componentId, IModel rowModel, String objectProperty, EditStateType state, ReportDatesDTO data) {
				/**
				 * Custom converter for date columns 
				 */
				Label lbl = new Label("value",new PropertyModel(data,objectProperty)) {
					private static final long serialVersionUID = 1L;
					@Override
					public IConverter getConverter(Class targetClass) {
						return new FormattedDateConverter(statementDate);
					}
				};
				lbl.add(new AttributeModifier("style","font-weight: bold;"));
				return HelperPanel.getInstance(componentId, lbl);
			}
		};
		ret.add(statementDateColumn);
		IGridColumn startColumn = new SRSDataGridColumn<ReportDatesDTO>(
				"startDate",new Model("Start"),"startDate",getEditState()) {
			private static final long serialVersionUID = 1L;
			@Override
			public Panel newCellPanel(WebMarkupContainer parent, String componentId, IModel rowModel, String objectProperty, EditStateType state, ReportDatesDTO data) {
				/**
				 * Custom converter for date columns 
				 */
				Label lbl = new Label("value",new PropertyModel(data,objectProperty)) {
					private static final long serialVersionUID = 1L;
					@Override
					public IConverter getConverter(Class targetClass) {
						return new FormattedDateConverter(sdf);
					}
				};
				return HelperPanel.getInstance(componentId, lbl);
			}
		};
		ret.add(startColumn);
		IGridColumn endColumn = new SRSDataGridColumn<ReportDatesDTO>(
				"endDate",new Model("End"),"endDate",getEditState()) {
			private static final long serialVersionUID = 1L;
			@Override
			public Panel newCellPanel(WebMarkupContainer parent, String componentId, IModel rowModel, String objectProperty, EditStateType state, ReportDatesDTO data) {
				/**
				 * Custom converter for date columns 
				 */
				Label lbl = new Label("value",new PropertyModel(data,objectProperty)) {
					private static final long serialVersionUID = 1L;
					@Override
					public IConverter getConverter(Class targetClass) {
						return new FormattedDateConverter(sdf);
					}
				};
				return HelperPanel.getInstance(componentId, lbl);
			}
		};
		ret.add(endColumn);
		return ret;
	}

	private SRSDropDownChoice getReportCategory() {
		if (reportCategory==null) {
			List<ReportType> choices = Arrays.asList(ReportMaintenanceDTO.ReportType.values());
			reportCategory = new SRSDropDownChoice("reportCategory",
					new PropertyModel(model,"selectedItem.reportType"), 
					choices, new SRSAbstractChoiceRenderer<Object>() {
					
						private static final long serialVersionUID = 1L;
						public Object getDisplayValue(Object value) {
							return ((ReportType)value).getDescription();
						}

						public String getIdValue(Object value, int index) {
							return ""+index;
						}
			}," *** Select *** "
			);
			reportCategory.setEnabled(!disabled);
			reportCategory.setOutputMarkupId(true);
			reportCategory.add(new AjaxFormComponentUpdatingBehavior("change") {
				private static final long serialVersionUID = 1L;
				@Override
				protected void onUpdate(AjaxRequestTarget target) {
					updateVisibility(target);
					updateReportingPeriods(target);
					/**
					 * Change report types
					 */
					List<StatementKindType> statementKinds = 
						getGuiController().getStatementKindsForReportType(
								model.getSelectedItem().getReportType());
					model.getSelectedItem().setStatementKinds(statementKinds);
					target.add(getStatementGroup());
				}
			});
		}
		return reportCategory;
		
	}
	
	@SuppressWarnings("unused")
	private RepeatingView getLeftPanel() {
		if (leftPanel==null) {
			leftPanel = new RepeatingView("leftPanel");
		}
		return leftPanel;
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
			add(getTimeRestrictionContainer());
			add(getReportCategory());
			add(getReportingMonthsLabel());
			add(getReportingMonths());
			add(getReportingPeriods());
			add(getStatementLabel());
			add(getStatementGroup());
			add(getGenerateReportButton());
			updateVisibility(null);
		}
		
		@Override
		protected void onSubmit() {
			super.onSubmit();
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

	public WebMarkupContainer getTimeRestrictionContainer() {
		if (timeRestrictionContainer == null) {
			timeRestrictionContainer = new WebMarkupContainer("timeRestrictionContainer");
			timeRestrictionContainer.setOutputMarkupId(true);
			timeRestrictionContainer.setOutputMarkupPlaceholderTag(true);
			timeRestrictionContainer.add(getTimeRestrictionComponent());
		}
		return timeRestrictionContainer;
	}

	public Component getTimeRestrictionComponent() {
		if (timeRestrictionLabel == null) {
			timeRestrictionLabel = new Label("lblTimeRestriction",
					new Model("Report Generation for Organisations is only available after 17:30 daily"));
		}
		return timeRestrictionLabel;
	}

	public ModalWindow getNotificationWindow() {
		if (notificationWindow==null) {
			notificationWindow = new ModalWindow("notificationWindow");
			notificationWindow.setTitle("Report Generation In Progress");
			notificationWindow.setPageCreator(new ModalWindow.PageCreator() {
				private static final long serialVersionUID = 1L;
				public Page createPage() {
					return new ReportGenerationNotificationPage(notificationWindow);
				}
			});
			notificationWindow.setMinimalHeight(180);
			notificationWindow.setInitialHeight(180);
			notificationWindow.setMaskType(MaskType.SEMI_TRANSPARENT);
			notificationWindow.setCssClassName(ModalWindow.CSS_CLASS_GRAY);
		}
		return notificationWindow;
	}

	public Label getReportingMonthsLabel() {
		if (reportingMonthsLabel==null) {
			reportingMonthsLabel = new Label("lblReportingMonth",
					new Model("Month"));
			reportingMonthsLabel.setOutputMarkupId(true);
			reportingMonthsLabel.setOutputMarkupPlaceholderTag(true);
		}
		return reportingMonthsLabel;
	}

	public SRSDropDownChoice getReportingMonths() {
		if (reportingMonths==null) {
			if (model.getSelectedItem().getReportingMonths()==null) {
				model.getSelectedItem().setReportingMonths(Collections.EMPTY_LIST);
			}
			reportingMonths = new SRSDropDownChoice("reportingMonth",
					new PropertyModel(model,"selectedItem.reportingMonth"), 
					model.getSelectedItem().getReportingMonths(), new SRSAbstractChoiceRenderer<Object>() {
					
						private static final long serialVersionUID = 1L;
						
						public Object getDisplayValue(Object value) {
							return value.toString();
						}

						public String getIdValue(Object value, int index) {
							return ""+index;
						}
			}," *** Select *** "
			);
			reportingMonths.setEnabled(!disabled);
			reportingMonths.setOutputMarkupId(true);
			reportingMonths.setOutputMarkupPlaceholderTag(true);
			reportingMonths.add(new AjaxFormComponentUpdatingBehavior("change") {
				private static final long serialVersionUID = 1L;
				@Override
				protected void onUpdate(AjaxRequestTarget target) {
					updateReportingPeriods(target);
				}
			});
		}
		return reportingMonths;
	}

	public Label getStatementLabel() {
		if (statementLabel==null) {
			statementLabel = new Label("lblStatement",new Model("Statements To Produce"));
		}
		return statementLabel;
	}

	public void updateVisibility(AjaxRequestTarget target) {
		getStatementLabel().setVisible(!disabled && model.getSelectedItem().getReportType()!=null);
		getStatementGroup().setVisible(!disabled && model.getSelectedItem().getReportType()!=null);
		getGenerateReportButton().setVisible(!disabled && model.getSelectedItem().getReportType()!=null);
		getTimeRestrictionContainer().setVisible(timeRestrictionInEffect);
		getReportCategory().setEnabled(!disabled && model.getSelectedItem().getValidReportTypes()!=null);
		getReportingMonths().setEnabled(!disabled);
		boolean isReportingMonthsVisible = model.getSelectedItem()!=null &&
						model.getSelectedItem().getReportType()!=null &&
						model.getSelectedItem().getReportType().equals(ReportType.DAILY);
		getReportingMonths().setVisible(isReportingMonthsVisible);
		getReportingMonthsLabel().setVisible(isReportingMonthsVisible);
		getReportingPeriods().setVisible(!disabled);
		if (target!=null) {
			target.add(getReportingMonths());
			target.add(getReportingMonthsLabel());
		}
	}

	private void updateReportingPeriods(AjaxRequestTarget target, List<ReportDatesDTO> dates) {
		/**
		 * Replace the original table. Ensures that when current page is > 1, and new data
		 * has less records than current fromIndex, there will be no exception.
		 */
		getReportingPeriods().resetSelectedItems();
		model.getSelectedItem().getReportDates().clear();
		model.getSelectedItem().getReportDates().addAll(dates);
		target.add(getReportingPeriods());
	}

	private void updateReportingPeriods(AjaxRequestTarget target) {
		List<ReportDatesDTO> dates = Collections.EMPTY_LIST;
		dates = getGuiController().getReportDates(model.getSelectedItem());
		/**
		 * Update reporting periods
		 */
		updateReportingPeriods(target, dates);
	}

}
