package za.co.liberty.web.pages.maintainagreement.payroll;

import java.util.Date;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.model.IModel;
import org.apache.wicket.validation.validator.DateValidator;

import za.co.liberty.dto.agreement.payroll.FixedPayrollDTO;
import za.co.liberty.srs.util.DateUtil;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.wicket.markup.html.form.SRSDateField;
import za.co.liberty.web.wicket.markup.html.grid.SimpleDateColumn;
import za.co.liberty.web.wicket.validation.ExactDateValidator;
import za.co.liberty.web.wicket.validation.FormattedValidationDate;

public class PayrollEndColumn extends SimpleDateColumn<FixedPayrollDTO> {

	private static final long serialVersionUID = 1114174380783970281L;

	private static final String VIEW_PROPERTY = "end";

	private EditStateType editState;

	public PayrollEndColumn(String columnId, IModel headerModel, 
			EditStateType state) {
		super(SimpleDateColumn.FORMAT_DATE_ONLY,columnId, 
				headerModel, VIEW_PROPERTY, state);
		this.editState = state;
		if (state!=null) {
			setRequired(state.equals(EditStateType.TERMINATE));
		}
	}

	@Override
	public boolean isCellEditable(EditStateType state, FixedPayrollDTO data) {
		return PayrollColumnHelper.isEndDateCellEditable(state, data);
	}
	
	@Override
	protected void updateComponent(SRSDateField component) {
		super.updateComponent(component);
		FormattedValidationDate currentDate = 
			new FormattedValidationDate();
		currentDate.setTime(
				DateUtil.minimizeTime(new Date()).getTime());
		if (!editState.equals(EditStateType.TERMINATE)) {
			/**
			 * if NOT in terminate state, then ensure that the end date is
			 * greater than or equal to current date
			 */ 
			component.add(DateValidator.minimum(currentDate));
		} else {
			/**
			 * If in terminate state, then ensure that the end date
			 * is exactly the current date
			 */
			component.add(new ExactDateValidator(currentDate));
		}
		component.add(new AjaxFormComponentUpdatingBehavior("change") {
			@Override
			protected void onUpdate(AjaxRequestTarget arg0) {
			}
		});
		
	}

}
