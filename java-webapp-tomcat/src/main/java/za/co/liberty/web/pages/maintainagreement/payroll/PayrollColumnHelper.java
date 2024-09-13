package za.co.liberty.web.pages.maintainagreement.payroll;

import za.co.liberty.dto.agreement.payroll.FixedPayrollDTO;
import za.co.liberty.web.data.enums.EditStateType;

public class PayrollColumnHelper {
	
	public static boolean isCellEditable(EditStateType state, FixedPayrollDTO data) {
		return state.equals(EditStateType.MODIFY) && data.getOid()==null;
	}

	public static boolean isEndDateCellEditable(EditStateType state, FixedPayrollDTO data) {
		return (state.equals(EditStateType.MODIFY) || 
				state.equals(EditStateType.TERMINATE)) &&
				data.isModifyEndDateEnabled();
	}
	
}
