package za.co.liberty.web.pages.maintainagreement.payroll;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.model.IModel;
import org.apache.wicket.validation.IValidator;

import za.co.liberty.dto.agreement.payroll.FixedDeductionDTO;
import za.co.liberty.dto.agreement.payroll.FixedPayrollDTO;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.wicket.markup.html.form.SRSTextField;
import za.co.liberty.web.wicket.markup.html.grid.SRSTextFieldColumn;

public class PayrollTextColumn<T extends FixedPayrollDTO> extends
		SRSTextFieldColumn<T> {

	public PayrollTextColumn(String columnId, 
			IModel headerModel, String objectProperty, 
			EditStateType state) {
		this(columnId,headerModel,objectProperty,state,null);
	}
	
	public PayrollTextColumn(String columnId, 
			IModel headerModel, String objectProperty, 
			EditStateType state,IValidator validator) {
		super(columnId, headerModel, objectProperty, state, validator);
	}

	private static final long serialVersionUID = 1L;

	@Override
	public boolean isCellEditable(EditStateType state, T data) {
		return PayrollColumnHelper.isCellEditable(state, data);
	}

	@Override
	protected void updateTextFieldComponent(SRSTextField component) {
		super.updateTextFieldComponent(component);
		component.add(new AjaxFormComponentUpdatingBehavior("change") {
			@Override
			protected void onUpdate(AjaxRequestTarget arg0) {
			}
		});
	}

}
