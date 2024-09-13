package za.co.liberty.web.wicket.validation.maintainagreement;

import java.util.List;

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.validation.IFormValidator;

import za.co.liberty.dto.agreement.AgreementDTO;
import za.co.liberty.dto.agreement.AgreementRoleDTO;
import za.co.liberty.dto.contracting.ResultAgreementDTO;
import za.co.liberty.interfaces.agreements.RoleKindType;

/**
 * This class represents a form validator for Bank Consultant Code
 * 
 * This is a generic validator that must be added to all panels 
 * that affect the requirement of Bank Consultant Code which 
 * is that if an agreement pays to a bank or belongs to a bank
 * then it must have Bank Consultant Code populated.
 * 
 * @author kxd1203
 *
 */
public class BankConsultantCodeFormValidator implements IFormValidator {

	private static final long serialVersionUID = 5184933489869162251L;

	private AgreementDTO agreementDTO;
	
	private List<Long> bankCodes;
	
	public BankConsultantCodeFormValidator(AgreementDTO agreementDTO,List<Long> bankCodes) {
		super();
		this.agreementDTO = agreementDTO;
		this.bankCodes = bankCodes;
	}

	public FormComponent[] getDependentFormComponents() {
		return null;
	}

	public void validate(Form form) {
		if (isPaysToBank() || isBelongsToBank()) {
			if (agreementDTO==null || agreementDTO.getAssociatedCodes()==null ||
					agreementDTO.getAssociatedCodes().getBankConsultantCode()==null) {
				form.error("Bank Consultant Code is required when an agreement pays to/belongs to a bank. " +
						"Please capture this before proceeding");
			}
		}
	}
	
	private boolean isBankCode(Long code) {
		if (code==null) {
			return false;
		}
		for (Long bankCode : bankCodes) {
			if (bankCode.equals(code)) {
				return true;
			}
		}
		return false;
	}
	
	private boolean isPaysToBank() {
		return agreementDTO!=null && agreementDTO.getPaymentDetails()!=null
			&&isBankCode(agreementDTO.getPaymentDetails().getOrgAgreementNumber());
	}
	
	private boolean isBelongsToBank() {
		if (agreementDTO!=null && 
				agreementDTO.getCurrentAndFutureAgreementRoles()!=null) {
			for (AgreementRoleDTO agreementRole : 
				agreementDTO.getCurrentAndFutureAgreementRoles()) {
				if (agreementRole.getKind()==RoleKindType.BELONGSTO.getKind()) {
					/**
					 * Belongs to role
					 */
					if (agreementRole.getRolePlayerReference() instanceof ResultAgreementDTO) {
						return isBankCode(
								((ResultAgreementDTO)agreementRole.getRolePlayerReference())
								.getAgreementNumber());
					}
				}
			}
		}
		return false;
	}

	public AgreementDTO getAgreementDTO() {
		return agreementDTO;
	}

	public void setAgreementDTO(AgreementDTO agreementDTO) {
		this.agreementDTO = agreementDTO;
	}

	public List<Long> getBankCodes() {
		return bankCodes;
	}

	public void setBankCodes(List<Long> bankCodes) {
		this.bankCodes = bankCodes;
	}
	
}
