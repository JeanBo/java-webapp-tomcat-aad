package za.co.liberty.web.wicket.convert.converters;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Locale;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.apache.wicket.util.convert.ConversionException;
import org.apache.wicket.util.convert.IConverter;

import za.co.liberty.common.domain.CurrencyAmount;
import za.co.liberty.common.enums.CurrencyEnum;

/**
 * <p>
 * A converter for currency amount
 * </p>
 * 
 * 
 * @author JZB0608 - 27 July 2016
 * 
 */
public class CurrencyAmountConverter implements IConverter<CurrencyAmount> {

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

	transient static final ThreadLocal<DecimalFormat> FORMAT = new ThreadLocal<DecimalFormat>() {
		@Override
	    protected DecimalFormat initialValue() {
			return new DecimalFormat(
					"R ###,###,###,##0.00");
	    }
	  };
	  
	  
	protected Class getTargetType() {
		return CurrencyAmount.class;

	}

	public CurrencyAmount convertToObject(String value, Locale locale) {
//		super.convertToObject(value, locale);
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
				.setVariable("validator.type", "currency amount");
		}
//		return CurrencyAmountUtil.create(new BigDecimal(value));
		return new CurrencyAmount(new BigDecimal(value), CurrencyEnum.ZAR);
	}
	
	
	

	@Override
	public String convertToString(CurrencyAmount value, Locale locale) {
		if (value == null) {
			return null;
		}
		return FORMAT.get().format(((CurrencyAmount)value).getValue());
	}

}
