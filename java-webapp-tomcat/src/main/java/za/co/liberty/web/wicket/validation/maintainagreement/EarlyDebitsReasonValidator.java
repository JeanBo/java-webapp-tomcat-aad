package za.co.liberty.web.wicket.validation.maintainagreement;

import java.util.Locale;

import org.apache.wicket.Component;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.ValidationError;

import za.co.liberty.dto.agreement.properties.EarlyDebitsReasonDTO;
import za.co.liberty.web.wicket.convert.converters.YesNoBooleanConverter;

/**
 * This class represents a validator for the Early debits reason GUI component.
 *  
 * @author kxd1203
 *
 */
public class EarlyDebitsReasonValidator implements IValidator {

	private Component earlyDebitsIndicatorComponent;

	public EarlyDebitsReasonValidator(Component earlyDebitsIndicatorComponent) {
		super();
		this.earlyDebitsIndicatorComponent = earlyDebitsIndicatorComponent;
	}

	/**
	 * Validate early debits reason only if the early debits indicator is set.
	 * 
	 * The reason must correspond to the value selected in the indicator.
	 */
	@Override
	public void validate(IValidatable validatable) {
		String earlyDebitsReason = 
			validatable.getValue()!=null?((EarlyDebitsReasonDTO) validatable
				.getValue()).getValue():null;
		Boolean earlyDebitsIndicator = getEarlyDebitsIndicator();
		if (earlyDebitsIndicator != null && earlyDebitsIndicator) {
			if (earlyDebitsReason != null
					&& earlyDebitsReason.indexOf("Not") != -1) {
				validatable.error(new ValidationError().addKey("EarlyDebitsReasonValidator.invalid"));
			}
		} else if (earlyDebitsIndicator != null && !earlyDebitsIndicator) {
			if (earlyDebitsReason != null && earlyDebitsReason.length() > 0
					&& earlyDebitsReason.indexOf("Not") == -1) {
				validatable.error(new ValidationError().addKey("EarlyDebitsReasonValidator.invalid"));
			}
		}
	}

	/**
	 * Get the value from either TextField or Label using the 
	 * YesNoBooleanConverter instantiated with the early debits
	 * not applicable null label, to ensuer proper conversion
	 * to Boolean from the component value.
	 * @return
	 */
	private Boolean getEarlyDebitsIndicator() {
		String nullLabel = "Early Debits Not Applicable";
		Object modelObject = getModelObjectFromComponent();
		if (modelObject instanceof Boolean) {
			return (Boolean) modelObject;
		} else if (modelObject instanceof String) {
			YesNoBooleanConverter converter = new YesNoBooleanConverter(nullLabel);
			return (Boolean) converter.convertToObject(
					(String) modelObject, 
					Locale.getDefault());
		}
		return null;
	}

	Object getModelObjectFromComponent() {
		Object modelObject = earlyDebitsIndicatorComponent.getDefaultModelObject();
		return modelObject;
	}

}
