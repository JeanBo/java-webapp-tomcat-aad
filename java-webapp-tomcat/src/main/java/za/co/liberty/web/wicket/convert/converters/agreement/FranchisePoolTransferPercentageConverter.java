package za.co.liberty.web.wicket.convert.converters.agreement;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.apache.wicket.util.convert.ConversionException;

import za.co.liberty.common.domain.Percentage;
import za.co.liberty.web.wicket.convert.converters.PercentageConverter;

/**
 * <p>
 * A converter for Percentage values. Used in Associated Agreement Details GUI, etc.
 * </p>
 *  
 * @author PKS2802 - 02 Oct 2009
 * 
 */

public class FranchisePoolTransferPercentageConverter extends PercentageConverter {	
	
	private static final long serialVersionUID = 1L;

	private transient Logger logger;
	
	public static final Pattern PERCENT_PATTERN = Pattern
	.compile("^([0-9]+(\\.[0-9]*)?)%?$");
	
	private final DecimalFormat percentFormat;
	
	/**
	 * Constructs a default converter
	 */
	public FranchisePoolTransferPercentageConverter() {
		/**
		 * format must always have at least 1 leading 0,
		 * and exactly 2 decimal places
		 */
		 percentFormat = new DecimalFormat("####0.00");
	}
	
	private Logger getLogger() {
		if (logger == null) {
			 logger = Logger.getLogger(this.getClass());
		}
		return logger;
	}

	@Override
	public Percentage convertToObject(String value, Locale locale) {
		if (value==null) {
			return null;
		}
		/**
		 * Remove whitespace and commas from the string value 
		 */
		value = value.replaceAll("\\p{Blank}|,", "");
		/**
		 * Create a regex matcher that will match the value as
		 * well as getting the decimal value from the string value 
		 * using a capturing group
		 */
		Matcher matcher = PERCENT_PATTERN.matcher(value);
		/**
		 * Check to see if the string value conforms to a percent value
		 */
		if (!matcher.matches()) {
			throw new ConversionException("")
				.setResourceKey("validator.invalid")
				.setSourceValue(value)
				.setVariable("validator.type", "percentage");
		}
		/**
		 * Get the decimal value from the matcher using a 
		 * capturing group
		 */
		String decimalValue = matcher.group(1);
		BigDecimal decimal = new BigDecimal(decimalValue);
		return new Percentage(decimal, Percentage.ROUND_HALF_UP);
	}

	/**
	 * Convert a percentage object back to a formatted string value
	 */
	@Override
	public String convertToString(Percentage value, Locale locale) {
		if (value == null) {
			return null;
		}
		double ret = 0.00f;
		if(value instanceof Percentage)
		{
			ret = ((Percentage)value).getValue().doubleValue();
		}
		
		return formatStringValue(ret);
	}

	/**
	 * Return the percentage formatted string value for the double value
	 * @param doubleValue
	 * @return
	 */
	String formatStringValue(double doubleValue) {
		return percentFormat.format(doubleValue)+"%";
	}	
	
}