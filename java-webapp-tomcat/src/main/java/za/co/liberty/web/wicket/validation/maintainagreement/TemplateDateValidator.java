package za.co.liberty.web.wicket.validation.maintainagreement;

import java.util.Date;

import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.ValidationError;

import za.co.liberty.srs.util.DateUtil;


/**
 * Template effective date must not be backdated, and must not 
 * be future dated by more than 1 month
 * 
 * @author kxd1203
 *
 */
public class TemplateDateValidator implements IValidator {
	
	private Date minimum;
	private Date maximum;

	public TemplateDateValidator() {
		minimum = DateUtil.minimizeTime(new Date());
		maximum = DateValidationUtil.addToDate(
				minimum, DateValidationUtil.DatePart.MONTH, 1);
	}

	@Override
	public void validate(IValidatable validatable) {
		Date date = (Date) validatable.getValue();
		if (date.before(minimum)) {
			validatable.error(new ValidationError().addKey("TemplateValidator.minimum"));
		}
		if (date.after(maximum)) {
			validatable.error(new ValidationError().addKey("TemplateValidator.maximum"));
		}
	}

}
