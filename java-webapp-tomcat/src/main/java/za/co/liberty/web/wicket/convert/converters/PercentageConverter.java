package za.co.liberty.web.wicket.convert.converters;

import java.io.Serializable;
import java.util.Locale;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.apache.wicket.util.convert.ConversionException;
import org.apache.wicket.util.convert.IConverter;

import za.co.liberty.common.domain.Percentage;
import za.co.liberty.helpers.util.SRSUtility;

/**
 * <p>
 * A converter for Percentage values. Used in Associated Agreement Details GUI, etc.
 * </p>
 *  
 * @author PKS2802 - 02 Oct 2009
 * 
 */

public class PercentageConverter implements IConverter<Percentage>, Serializable {	
	
	private static final long serialVersionUID = 1L;

	private transient Logger logger = Logger.getLogger(this.getClass());
	
	/* Constants */
	/**
	 * A pattern to determine if a value is a valid decimal nr. Valid values are
	 * "100", "100.01", "100.1" etc.
	 */
	public static final Pattern PERCENT_PATTERN = Pattern
			.compile("^[0-9]+(\\.[0-9]*)?%?$");
	
	/**
	 * Defaults to a decimal size of 2
	 *
	 */
	public PercentageConverter() {
		
	}
	
	protected Class getTargetType() {
		return Percentage.class;

	}

	public Percentage convertToObject(String value, Locale locale) {
		// Clean up value
		value = value.replaceAll("\\p{Blank}|,", "");
		
		// Is it a decimal nr?
		if (PERCENT_PATTERN.matcher(value).matches() == false) {
			throw new ConversionException("")
				.setResourceKey("validator.invalid")
				.setSourceValue(value)
				.setVariable("validator.type", "percentage");
		}
		
		// Convert value
		if(value.indexOf("%") != -1)
			value = value.substring(0, value.indexOf("%"));

		return SRSUtility.convertToPercentageDiv100(value);
	}

	
	public String convertToString(Percentage value, Locale locale) {
		if (value == null) {
			return null;	
		}
		if(value instanceof Percentage)
		{
			return ((Percentage)value).toString(2,Percentage.ROUND_HALF_UP);
		}
		
		return "0.00%";
	}	
	
}