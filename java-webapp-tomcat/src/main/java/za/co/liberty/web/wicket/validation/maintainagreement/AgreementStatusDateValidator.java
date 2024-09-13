package za.co.liberty.web.wicket.validation.maintainagreement;

import java.util.Date;

import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.ValidationError;

import za.co.liberty.dto.agreement.AgreementDTO;

public class AgreementStatusDateValidator implements IValidator {

	private AgreementDTO agreementDTO;

	public AgreementStatusDateValidator(AgreementDTO agreementDTO) {
		this.agreementDTO=agreementDTO;
	}
	
//	@Override
//	public boolean validateOnNullValue() {
//		return false;
//	}

	@Override
	public void validate(IValidatable validatable) {
		Date start = agreementDTO.getStartDate();
		Date status = (Date) validatable.getValue();
		if (status==null) {
//			error(validatable,"Required");
			validatable.error(new ValidationError().addKey("Required"));
		} else if (start==null) {
			validatable.error(new ValidationError().addKey("AgreementStatusDateValidator.startNotSet"));
		} else if (start.after(status)) {
			validatable.error(new ValidationError().addKey("AgreementStatusDateValidator.beforeStart"));
		}
	}

}
