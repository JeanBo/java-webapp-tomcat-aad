package za.co.liberty.web.wicket.convert.converters;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Locale;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.apache.wicket.util.convert.ConversionException;
import org.apache.wicket.util.convert.converter.AbstractDecimalConverter;

/**
 * <p>
 * A converter for currencies.
 * </p>
 * 
 * TODO Allow converter to convert to more types.
 * 
 * @author JZB0608 - 03 Apr 2008
 * 
 */
public class CurrencyConverter extends AbstractDecimalConverter<BigDecimal> {

	private static final long serialVersionUID = -2222271935848647780L;

	/* Constants */
	/**
	 * Format for currencies
	 */ 

	/* Local variables */
	private transient Logger logger = Logger.getLogger(this.getClass());

	/**
	 * A pattern to determine if a value is a valid decimal nr. Valid values are
	 * "-100", "+100.01", "100.1" etc.
	 */
	public static final Pattern DECIMAL_PATTERN = Pattern
			.compile("^(\\+|-)?[0-9]+(\\.[0-9]*)?$");

	@Override
	protected Class getTargetType() {
		return BigDecimal.class;

	}

	public BigDecimal convertToObject(String value, Locale locale) {
		if (value==null || value.length()==0) {
			return null;
		}
		value = value.replaceAll("R|\\p{Blank}|,", "");
		if (DECIMAL_PATTERN.matcher(value).matches() == false) {
//			throw this.newConversionException("Unable to convert value", value,
//					locale).setResourceKey("validator.invalid");
			throw new ConversionException("")
				.setResourceKey("validator.invalid")
				.setSourceValue(value)
				.setVariable("validator.type", "currency");
		}
		return new BigDecimal(value);
	}
	
	transient static final ThreadLocal<DecimalFormat> FORMAT = new ThreadLocal<DecimalFormat>() {
		@Override
	    protected DecimalFormat initialValue() {
			return new DecimalFormat(
					"R ###,###,###,##0.00");
	    }
	  };
	//to support 3 decimal format
	  transient static final ThreadLocal<DecimalFormat> THREEDECIMAL_FORMAT = new ThreadLocal<DecimalFormat>() {
			@Override
		    protected DecimalFormat initialValue() {
				return new DecimalFormat(
						"R ###,###,###,##0.000");
		    }
		  };

	@Override
	public String convertToString(BigDecimal value, Locale locale) {
		
		if (value == null) {
			return null;
		}
		//Fix for PF override rate as it will be having 3 decimal positions
		if(value.stripTrailingZeros().scale() >= 3) {
			return THREEDECIMAL_FORMAT.get().format(value);
		}
		
		return FORMAT.get().format(value);
	}

}
