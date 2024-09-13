package za.co.liberty.web.pages.maintainagreement.payroll;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import za.co.liberty.common.domain.Percentage;
import za.co.liberty.dto.agreement.AgreementDTO;
import za.co.liberty.dto.agreement.payroll.FixedEarningDTO;
import za.co.liberty.dto.agreement.payroll.FixedPayrollDTO;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.data.enums.fields.AgreementGUIField;
import za.co.liberty.web.pages.maintainagreement.model.FixedEarningsPanelModel;
import za.co.liberty.web.pages.panels.GUIFieldPanel;
import za.co.liberty.web.pages.panels.HelperPanel;
import za.co.liberty.web.pages.panels.ViewTemplateBasePanel;
import za.co.liberty.web.wicket.convert.converters.PercentageConverter;
import za.co.liberty.web.wicket.markup.html.form.SRSAbstractChoiceRenderer;
import za.co.liberty.web.wicket.markup.html.form.SRSDropDownChoice;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGrid;
import za.co.liberty.web.wicket.markup.html.grid.SRSGridRowSelectionCheckBox;
import za.co.liberty.web.wicket.markup.html.grid.SRSTextFieldColumn;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGrid.GridSizeUnit;
import za.co.liberty.web.wicket.markup.repeater.data.ListDataProvider;
import za.co.liberty.web.wicket.validation.BigDecimalValidator;
import za.co.liberty.web.wicket.view.ContextDrivenViewTemplate;

import com.inmethod.grid.DataProviderAdapter;
import com.inmethod.grid.IGridColumn;
import com.inmethod.grid.SizeUnit;

public class FixedEarningsPanel extends ViewTemplateBasePanel<AgreementGUIField, AgreementDTO> {

	private SRSDataGrid deductionsComponent;
	private FixedEarningsPanelModel panelModel;
	private WebMarkupContainer buttonPanel;
	private Button addButton;
	private Button removeButton;
	private RepeatingView topupLeftPanel;
	private RepeatingView topupRightPanel;
	private GUIFieldPanel bondPercentagePanel;
	private PercentageConverter converter;
	private WebMarkupContainer conglomerateWarningPanel;
	private boolean initialised;
	
	public FixedEarningsPanel(String id, EditStateType editState,FixedEarningsPanelModel panelModel) {
		super(id, editState);
		this.panelModel = panelModel;
	}
	
	@Override
	protected boolean isProcessOutstandingRequestsAllowed() {
		return false;
	}

	@Override
	protected void onBeforeRender() {
		super.onBeforeRender();
		if (!initialised) {
			converter = new PercentageConverter();
			if (panelModel!=null && panelModel.getFixedEarnings()==null) {
				panelModel.setFixedEarnings(Collections.EMPTY_LIST);
			}
			if (panelModel!=null && getEditState().equals(EditStateType.TERMINATE)) {
				panelModel.updateModelForTerminate();
			}
			this.add(new EarningsForm("fixedEarningsForm"));
			initialised = true;
		}
	}

	private class EarningsForm extends Form {

		public EarningsForm(String id) {
			super(id);
			initComponents();
			setVisiblity();
		}

		private void setVisiblity() {
			boolean enableButtons =  
				(getEditState().equals(EditStateType.MODIFY) && 
					getViewTemplate().isModifiable(
							AgreementGUIField.FIXED_EARNING_BUTTONS, 
							getViewTemplateContext())) ||
				(getEditState().equals(EditStateType.ADD) && 
						getViewTemplate().isAddable(
								AgreementGUIField.FIXED_EARNING_BUTTONS, 
								getViewTemplateContext()));
			getAddButton().setEnabled(enableButtons);
			getRemoveButton().setEnabled(enableButtons);			
		}

		private void initComponents() {
			add(getConglomerateWarningPanel());
			add(getEarningsComponent());
			add(getButtonPanel());
//			add(getTopupLeftPanel());
//			add(getTopupRightPanel());
		}

	}
	
	private SRSDataGrid getEarningsComponent() {
		if (deductionsComponent == null) {
			DataProviderAdapter dataProviderAdapter = 
				getDataProviderAdapter(panelModel.getFixedEarnings());
			deductionsComponent = new SRSDataGrid("fixedEarningsComponent",
					dataProviderAdapter,
					getStatusColumns(),null);
			deductionsComponent.setClickRowToSelect(false);
			deductionsComponent.setCleanSelectionOnPageChange(false);
			deductionsComponent.setAllowSelectMultiple(false);
			deductionsComponent.setGridWidth(99, GridSizeUnit.PERCENTAGE);
			deductionsComponent.setOutputMarkupId(true);
			deductionsComponent.setOutputMarkupPlaceholderTag(true);
			deductionsComponent.setContentHeight(100, SizeUnit.PX);
			deductionsComponent.setNonSelectableRowObjects(getNonSelectableRowObjects());
		}
		return deductionsComponent;
	}
	
	public Component getTopupRightPanel() {
		if (topupRightPanel == null) {
			topupRightPanel = new RepeatingView("topupRightPanel");
		}
		return topupRightPanel;
	}

	public RepeatingView getTopupLeftPanel() {
		if (topupLeftPanel == null) {
			topupLeftPanel = new RepeatingView("topupLeftPanel");
			topupLeftPanel.add(getBondPercentagePanel());
		}
		return topupLeftPanel;
	}

	private GUIFieldPanel getBondPercentagePanel() {
		if (bondPercentagePanel == null) {
			PropertyModel propertyModel = new PropertyModel(panelModel,
				"onePercentBond.percentage");
			IChoiceRenderer percentageRenderer = new SRSAbstractChoiceRenderer<Object>() {

				public Object getDisplayValue(Object object) {
					return converter.convertToString((Percentage) object, Locale.getDefault());
				}

				public String getIdValue(Object object, int index) {
					return ""+index;
				}
				
			};
			SRSDropDownChoice dropDownChoice = new SRSDropDownChoice("value",
					propertyModel, panelModel
					.getValidPayrollValues()
					.getValidBondPercentages(),
					percentageRenderer, "Select");
			HelperPanel bondPercentage = createPageField("panel", "Bond Percentage", 
					null, dropDownChoice, 
					new EditStateType[] { EditStateType.MODIFY,EditStateType.TERMINATE});
			bondPercentagePanel = createGUIFieldPanel(
				"Bond Percentage", 
				"Bond Percentage", 
				"BondPercentage", 
				bondPercentage);
		}
		return bondPercentagePanel;
	}
	
	public WebMarkupContainer getConglomerateWarningPanel() {
		if (conglomerateWarningPanel == null) {
			conglomerateWarningPanel = new WebMarkupContainer("conglomerateWarningPanel");
			conglomerateWarningPanel.setVisible(
					panelModel.getViewTemplate().isViewable(
							AgreementGUIField.FIXED_EARNING_CONGLOMERATE_WARNING, 
							getEditState(), 
							panelModel.getViewTemplateContext()));
		}
		return conglomerateWarningPanel;
	}

	public WebMarkupContainer getButtonPanel() {
		if (buttonPanel == null) {
			buttonPanel = new WebMarkupContainer("buttonPanel");
			buttonPanel.add(getAddButton());
			buttonPanel.add(getRemoveButton());
			buttonPanel.setOutputMarkupId(true);
			buttonPanel.setOutputMarkupPlaceholderTag(true);
		}
		return buttonPanel;
	}

	private Button getRemoveButton() {
		if (removeButton == null) {
			removeButton = new Button("removeButton");
			removeButton.add(new AjaxFormComponentUpdatingBehavior("click") {
				@Override
				protected void onUpdate(AjaxRequestTarget target) {
					List<Object> selectedItems = 
						getEarningsComponent().getSelectedItemObjects();
					panelModel.getFixedEarnings().removeAll(selectedItems);
					target.add(getEarningsComponent());
				}
			});
		}
		return removeButton;
	}

	private Button getAddButton() {
		if (addButton == null) {
			addButton = new Button("addButton");
			addButton.add(new AjaxFormComponentUpdatingBehavior("click") {
				@Override
				protected void onUpdate(AjaxRequestTarget target) {
					FixedEarningDTO fixedEarningDTO = 
						new FixedEarningDTO();
					fixedEarningDTO.setGuiSequenceNo(
							panelModel.getNextGUISequence());
					panelModel.getFixedEarnings().add(fixedEarningDTO);
					target.add(getEarningsComponent());
				}
			});
		}
		return addButton;
	}

	private List<FixedEarningDTO> getNonSelectableRowObjects() {
		List<FixedEarningDTO> ret = new ArrayList<FixedEarningDTO>();
		for (FixedEarningDTO modelObject : panelModel.getFixedEarnings()) {
			if (modelObject.getOid()!=null) {
				ret.add(modelObject);
			}
		}
		return ret;
	}

	private List<IGridColumn> getStatusColumns() {
		List<IGridColumn> ret = new ArrayList<IGridColumn>();
		EditStateType state = getEditState();
		EditStateType[] states = getViewTemplate().getEditStates(
				AgreementGUIField.FIXED_EARNING_GRID, 
				getViewTemplateContext());
		if (!Arrays.asList(states).contains(state)) {
			state = EditStateType.VIEW;
		}
		if (state.equals(EditStateType.MODIFY)) {
			SRSGridRowSelectionCheckBox selection = new SRSGridRowSelectionCheckBox("Select");
			ret.add(selection);
		}
		ret.add(new PayrollTypeColumn("Type",
				new Model("Type"),
				panelModel.getValidPayrollValues().getValidEarningTypes(),
				state)
			.setMinSize(150)
			.setInitialSize(150)
			.setSizeUnit(SizeUnit.PX));
		ret.add(new PayrollFrequencyColumn("Frequency",
				new Model("Frequency"),
				state));
		PayrollStartColumn payrollStartColumn = 
			new PayrollStartColumn("StartDate",
				new Model("Start Date"),
				state);
		ret.add(payrollStartColumn);
		PayrollEndColumn payrollEndColumn = 
			new PayrollEndColumn("EndDate",
				new Model("End Date"),
				state);
		ret.add(payrollEndColumn);
		SRSTextFieldColumn<FixedPayrollDTO> amountColumn = 
			new PayrollTextColumn<FixedPayrollDTO>(
				"Amount",
				new Model("Amount"),
				"amount",
				state,
				BigDecimalValidator.minimum(BigDecimal.ZERO));
		amountColumn.setRequired(true);
		ret.add(amountColumn);
		return ret;
	}

	private DataProviderAdapter getDataProviderAdapter(List<FixedEarningDTO> fixedEarnings) {
		ListDataProvider<FixedEarningDTO> listDataProvider = 
			new ListDataProvider<FixedEarningDTO>(fixedEarnings);
		return new DataProviderAdapter(listDataProvider);
	}

	@Override
	protected ContextDrivenViewTemplate<AgreementGUIField, AgreementDTO> getViewTemplate() {
		return panelModel.getViewTemplate();
	}

	@Override
	protected AgreementDTO getViewTemplateContext() {
		return panelModel.getViewTemplateContext();
	}

}
