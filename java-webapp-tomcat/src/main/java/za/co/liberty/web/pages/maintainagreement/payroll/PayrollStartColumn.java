package za.co.liberty.web.pages.maintainagreement.payroll;

import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.model.IModel;

import za.co.liberty.dto.agreement.payroll.FixedPayrollDTO;
import za.co.liberty.dto.spec.TypeDTO;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.wicket.markup.html.form.SRSDateField;
import za.co.liberty.web.wicket.markup.html.form.SRSTextField;
import za.co.liberty.web.wicket.markup.html.grid.SimpleDateColumn;

public class PayrollStartColumn extends SimpleDateColumn<FixedPayrollDTO> {

	private static final long serialVersionUID = 8236313331116797084L;

	private static final String VIEW_PROPERTY = "start";
	

	public PayrollStartColumn(String columnId, IModel headerModel, 
			EditStateType state) {
		super(SimpleDateColumn.FORMAT_DATE_ONLY,columnId, 
				headerModel, VIEW_PROPERTY, state);
		setRequired(true);
	}

	@Override
	public boolean isCellEditable(EditStateType state, FixedPayrollDTO data) {
		return PayrollColumnHelper.isCellEditable(state, data);
	}
	
	@Override
	protected void updateComponent(SRSDateField component) {
		super.updateComponent(component);
		component.add(new AjaxFormComponentUpdatingBehavior("change") {
			@Override
			protected void onUpdate(AjaxRequestTarget arg0) {
				System.out.println("Start Change");
			}
		});
	}

}
