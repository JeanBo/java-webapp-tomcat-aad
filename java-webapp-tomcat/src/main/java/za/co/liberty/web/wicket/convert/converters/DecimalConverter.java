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
 * A converter for decimals. By default it converts to BigDecimals but 
 * this can easily be overridden.
 * </p>
 * 
 * TODO Allow converter to convert to more types.
 * 
 * @author JZB0608 - 03 Apr 2008
 * 
 */
public class DecimalConverter extends AbstractDecimalConverter<BigDecimal> {

	private static final long serialVersionUID = -2222271935848647780L;

	/* Constants */
	/**
	 * A pattern to determine if a value is a valid decimal nr. Valid values are
	 * "-100", "+100.01", "100.1" etc.
	 */
	public static final Pattern DECIMAL_PATTERN = Pattern
			.compile("^(\\+|-)?[0-9]+(\\.[0-9]*)?$");
		
	/* Local variables */
	private transient Logger logger = Logger.getLogger(this.getClass());
//	private DecimalFormat format;
	private String formatString;
	
	final transient ThreadLocal<DecimalFormat> FORMAT = new ThreadLocal<DecimalFormat>() {
		@Override
	    protected DecimalFormat initialValue() {
			return new DecimalFormat(formatString);
	    }
	};
	
	/**
	 * Defaults to a decimal size of 2
	 *
	 */
	public DecimalConverter() {
		this(2);
	}
	
	
	public DecimalConverter(int decimalSize) {
		if (decimalSize<0) {
			throw new IllegalArgumentException("Decimal size may not be negative");
		}
		String formatStr = "###,###,###,##0";
		if (decimalSize>0) {
			formatStr+=".";
			for (int i = decimalSize; i > 0;--i) {
				formatStr+="0";
			}
		}
		formatString = formatStr;
	}
	
	@Override
	protected Class<BigDecimal> getTargetType() {
		return BigDecimal.class;

	}

	@Override
	public BigDecimal convertToObject(String value, Locale locale) {
		// Clean up value
		value = value.replaceAll("\\p{Blank}|,", "");
		
		// Is it a decimal nr?
		if (DECIMAL_PATTERN.matcher(value).matches() == false) {
			throw new ConversionException("")
				.setResourceKey("validator.invalid")
				.setSourceValue(value)
				.setVariable("validator.type", "currency");
		}
		
		// Convert value
		return new BigDecimal(value);
	}
	  
	@Override
	public String convertToString(BigDecimal value, Locale locale) {
		if (value == null) {
			return null;
		}
		return FORMAT.get().format(value);
	}

}
