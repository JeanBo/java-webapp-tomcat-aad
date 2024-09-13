package za.co.liberty.web.wicket.validation;

import java.math.BigDecimal;

import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.ValidationError;
import org.apache.wicket.validation.validator.RangeValidator;

/**
 * This class represents a validator of BigDecimal components
 * 
 * @author kxd1203
 *
 */
public class BigDecimalValidator extends RangeValidator<BigDecimal>  {

	private static final long serialVersionUID = -4528065301435356920L;

	private static final String RANGE_INVALID = "BigDecimalValidator.range";

	private static final String MINIMUM_VALUE = "BigDecimalValidator.minimum";
	
	private static final String MAXIMUM_VALUE = "BigDecimalValidator.maximum";
	
	private BigDecimal minimum;
	
	private BigDecimal maximum;
	
	/**
	 * Create a new BigDecimalValidator that validates a value is within a specified range
	 * 
	 * @param minimum
	 * @param maximum
	 * @return
	 */
	public static BigDecimalValidator range(BigDecimal minimum, BigDecimal maximum) {
		return new BigDecimalValidator(minimum,maximum);
	}
	
	/**
	 * Create a new BigDecimalValidator that validates a value is not less 
	 * than a specified minimum value
	 */
	public static BigDecimalValidator minimum(BigDecimal minimum) {
		return new BigDecimalValidator(minimum,null);
	}
	
	/**
	 * Create a new BigDecimalValidator that validates a value is not greater 
	 * than a specified maximum value
	 * 
	 * @param maximum
	 * @return
	 */
	public static BigDecimalValidator maximum(BigDecimal maximum) {
		return new BigDecimalValidator(null,maximum);
	}
	
	/**
	 * Create a new BigDecimalValidator - This method is private so that
	 * only the static methods within this class may create a new instance
	 * configured correctly for a specified purpose
	 * 
	 * @param minimum
	 * @param maximum
	 */
	private BigDecimalValidator(BigDecimal minimum, BigDecimal maximum) {
		super();
		this.minimum = minimum;
		this.maximum = maximum;
	}

	@Override
	public void validate(IValidatable<BigDecimal> validatable) {

		super.validate(validatable);
	
//	protected void onValidate(IValidatable validatable) {
		if (validatable.getValue() instanceof BigDecimal) {
			BigDecimal value = (BigDecimal) validatable.getValue();
			if (minimum!=null && maximum!=null) {
				//VALIDATE RANGE
				if (value.compareTo(minimum)<0 || 
						value.compareTo(maximum)>0) {
					validatable.error(newError().addKey(RANGE_INVALID));

				}
			} else if (minimum!=null) {
				//VALIDATE MINIMUM
				if (value.compareTo(minimum)<0) {
					validatable.error(newError().addKey(MINIMUM_VALUE));
				}
			} else if (maximum!=null) {
				//VALIDATE MAXIMUM
				if (value.compareTo(maximum)>0) {
					validatable.error(newError().addKey(MAXIMUM_VALUE));
				}
			}
		}
	}

	

	protected ValidationError newError() {
		ValidationError e = new ValidationError();
		e.setVariable("minimum",minimum);
		e.setVariable("maximum", maximum);
		return e;
	}

	public BigDecimal getMaximum() {
		return maximum;
	}

	public BigDecimal getMinimum() {
		return minimum;
	}
	
	

}
