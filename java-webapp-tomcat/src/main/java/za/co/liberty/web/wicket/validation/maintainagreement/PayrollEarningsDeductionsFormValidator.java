package za.co.liberty.web.wicket.validation.maintainagreement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.validation.AbstractFormValidator;
import org.apache.wicket.markup.html.form.validation.IFormValidator;

import za.co.liberty.dto.agreement.AgreementDTO;
import za.co.liberty.dto.agreement.payroll.FixedPayrollDTO;
import za.co.liberty.dto.spec.ActualLifeCycleStatusDTO;
import za.co.liberty.interfaces.agreements.AgreementStatusType;
import za.co.liberty.srs.util.DateUtil;
import za.co.liberty.web.data.enums.EditStateType;

/**
 * This class represents a form validator that will validate both fixed earnings and deductions 
 * panels to ensure values are valid for a process 
 * 
 * @author kxd1203
 *
 */
public class PayrollEarningsDeductionsFormValidator implements IFormValidator {

	private EditStateType editState;
	
	private AgreementDTO agreementDTO;
	
	public PayrollEarningsDeductionsFormValidator(EditStateType editState, AgreementDTO agreementDTO) {
		super();
		this.editState = editState;
		this.agreementDTO = agreementDTO;
	}

	public FormComponent[] getDependentFormComponents() {
		return null;
	}

	public void validate(Form form) {
		if (editState==EditStateType.VIEW) {
			return;
		}
		if (agreementDTO==null ||
				agreementDTO.getCurrentStatus()==null ||
				agreementDTO.getFixedDeductions()==null || 
				agreementDTO.getFixedEarnings()==null) {
			form.error("Invalid Validation Configuration");
		}
		if (editState.equals(EditStateType.TERMINATE)) {
			/**
			 * On terminate ensure all earnings/deductions are ended with system date
			 * 
			 * This validation ensures that the automated process populates all the 
			 * end dates correctly
			 */
			List<FixedPayrollDTO> fixedPayrollObjects = new ArrayList<FixedPayrollDTO>();
			fixedPayrollObjects.addAll(agreementDTO.getFixedDeductions());
			fixedPayrollObjects.addAll(agreementDTO.getFixedEarnings());
			Date currentDate = DateUtil.minimizeTime(new Date());
			for (FixedPayrollDTO fixedPayroll : fixedPayrollObjects) {
				if (!fixedPayroll.isModifyEndDateEnabled()) {
					/**
					 * Only check the end date for entries
					 * where modify is possible
					 */
					continue;
				}
				if (fixedPayroll.getEnd()!=null &&
					!fixedPayroll.getEnd().equals(currentDate)) {
						form.error("All fixed earnings/deductions must have an end date equal to current date");
						return;
				}
			}
		}
	}

	private boolean isTerminated(ActualLifeCycleStatusDTO currentStatus) {
		AgreementStatusType requestStatusTypeForSpecId = 
			AgreementStatusType.getRequestStatusTypeForSpecId(currentStatus.getSpecId());
		if (Arrays.asList(
				AgreementStatusType.getTerminationStatus()).contains(
						requestStatusTypeForSpecId)) {
			return true;
		}
		return false;
	}

	public AgreementDTO getAgreementDTO() {
		return agreementDTO;
	}

	public void setAgreementDTO(AgreementDTO agreementDTO) {
		this.agreementDTO = agreementDTO;
	}

	public EditStateType getEditState() {
		return editState;
	}

	public void setEditState(EditStateType editState) {
		this.editState = editState;
	}

}
