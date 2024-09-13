package za.co.liberty.web.wicket.validation;

import java.util.Date;

import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.ValidationError;
import org.apache.wicket.validation.validator.DateValidator;

public class ExactDateValidator extends DateValidator {

	private Date required;

	public ExactDateValidator(Date required) {
		this.required = required;
	}
	
	@Override
	public void validate(IValidatable validatable) {
		if (validatable!=null && 
				validatable.getValue() instanceof Date) {
			Date value = (Date)validatable.getValue();
			if (!value.equals(required)) {
				validatable.error(new ValidationError()
						.addKey("ExactDateValidator.exact")
						.setVariable("required", required));
			}
		}
	}

}
