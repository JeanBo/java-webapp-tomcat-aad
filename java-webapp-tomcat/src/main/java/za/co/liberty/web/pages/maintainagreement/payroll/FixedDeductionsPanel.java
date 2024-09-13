package za.co.liberty.web.pages.maintainagreement.payroll;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.Model;

import com.inmethod.grid.DataProviderAdapter;
import com.inmethod.grid.IGridColumn;
import com.inmethod.grid.SizeUnit;

import za.co.liberty.dto.agreement.AgreementDTO;
import za.co.liberty.dto.agreement.payroll.FixedDeductionDTO;
import za.co.liberty.dto.agreement.payroll.FixedPayrollDTO;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.data.enums.fields.AgreementGUIField;
import za.co.liberty.web.pages.maintainagreement.model.FixedDeductionsPanelModel;
import za.co.liberty.web.pages.panels.ViewTemplateBasePanel;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGrid;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGrid.GridSizeUnit;
import za.co.liberty.web.wicket.markup.html.grid.SRSGridRowSelectionCheckBox;
import za.co.liberty.web.wicket.markup.html.grid.SRSTextFieldColumn;
import za.co.liberty.web.wicket.markup.repeater.data.ListDataProvider;
import za.co.liberty.web.wicket.validation.BigDecimalValidator;
import za.co.liberty.web.wicket.view.ContextDrivenViewTemplate;

public class FixedDeductionsPanel extends ViewTemplateBasePanel<AgreementGUIField, AgreementDTO> {

	private SRSDataGrid deductionsComponent;
	private FixedDeductionsPanelModel panelModel;
	private WebMarkupContainer buttonPanel;
	private Button addButton;
	private Button removeButton;
	private WebMarkupContainer conglomerateWarningPanel;
	private boolean initialised;
	
	public FixedDeductionsPanel(String id, EditStateType editState,FixedDeductionsPanelModel panelModel) {
		super(id, editState);
		this.panelModel = panelModel;
	}
	
	@Override
	protected boolean isProcessOutstandingRequestsAllowed() {
		return false;
	}

	@Override
	protected void onBeforeRender() {
		// TODO Auto-generated method stub
		super.onBeforeRender();
		if (!initialised) {
			if (panelModel!=null && panelModel.getFixedDeductions()==null) {
				panelModel.setFixedDeductions(Collections.EMPTY_LIST);
			}
			if (panelModel!=null && getEditState().equals(EditStateType.TERMINATE)) {
				panelModel.updateModelForTerminate();
			}
			this.add(new DeductionsForm("fixedDeductionsForm"));
			initialised=true;
		}
	}



	private class DeductionsForm extends Form {

		public DeductionsForm(String id) {
			super(id);
			initComponents();
			setVisiblity();
		}

		private void setVisiblity() {
			boolean enableButtons =  
					(getEditState().equals(EditStateType.MODIFY) && 
						getViewTemplate().isModifiable(
								AgreementGUIField.FIXED_DEDUCTION_BUTTONS, 
								getViewTemplateContext())) ||
					(getEditState().equals(EditStateType.ADD) && 
							getViewTemplate().isAddable(
									AgreementGUIField.FIXED_DEDUCTION_BUTTONS, 
									getViewTemplateContext()));
			getAddButton().setEnabled(enableButtons);
			getRemoveButton().setEnabled(enableButtons);
		}

		private void initComponents() {
			add(getConglomerateWarningPanel());
			add(getDeductionsComponent());
			add(getButtonPanel());
		}

	}
	
	private SRSDataGrid getDeductionsComponent() {
		if (deductionsComponent == null) {
			DataProviderAdapter dataProviderAdapter = 
				getDataProviderAdapter(panelModel.getFixedDeductions());
			deductionsComponent = new SRSDataGrid("fixedDeductionsComponent",
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
	
	public WebMarkupContainer getConglomerateWarningPanel() {
		if (conglomerateWarningPanel == null) {
			conglomerateWarningPanel = new WebMarkupContainer("conglomerateWarningPanel");
			conglomerateWarningPanel.setVisible(
					panelModel.getViewTemplate().isViewable(
							AgreementGUIField.FIXED_DEDUCTION_CONGLOMERATE_WARNING, 
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
						getDeductionsComponent().getSelectedItemObjects();
					panelModel.getFixedDeductions().removeAll(selectedItems);
					target.add(getDeductionsComponent());
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
					FixedDeductionDTO fixedDeductionDTO = 
						new FixedDeductionDTO();
					fixedDeductionDTO.setGuiSequenceNo(
							panelModel.getNextGUISequence());
					panelModel.getFixedDeductions().add(fixedDeductionDTO);
					target.add(getDeductionsComponent());
				}
			});
		}
		return addButton;
	}

	private List<FixedDeductionDTO> getNonSelectableRowObjects() {
		List<FixedDeductionDTO> ret = new ArrayList<FixedDeductionDTO>();
		for (FixedDeductionDTO modelObject : panelModel.getFixedDeductions()) {
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
				AgreementGUIField.FIXED_DEDUCTION_GRID, 
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
				panelModel.getValidPayrollValues().getValidDeductionTypes(),
				state)
			.setMinSize(150)
			.setInitialSize(150)
			.setSizeUnit(SizeUnit.PX));
		ret.add(new PayrollFrequencyColumn("Frequency",
				new Model("Frequency"),
				state));
		/*ret.add(new PayrollTextColumn<FixedDeductionDTO>(
				"MedicalAidNo",
				new Model("Medical Aid No"),
				"medicalAidNumber",
				state));
		SRSTextFieldColumn<FixedDeductionDTO> addChargeColumn = 
			new PayrollTextColumn<FixedDeductionDTO>(
				"AdditionalCharge",
				new Model("Additional Charge"),
				"additionalCharge",
				state,
				BigDecimalValidator.minimum(BigDecimal.ZERO));
		ret.add(addChargeColumn);*/
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

	private DataProviderAdapter getDataProviderAdapter(List<FixedDeductionDTO> fixedDeductions) {
		ListDataProvider<FixedDeductionDTO> listDataProvider = 
			new ListDataProvider<FixedDeductionDTO>(fixedDeductions);
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
