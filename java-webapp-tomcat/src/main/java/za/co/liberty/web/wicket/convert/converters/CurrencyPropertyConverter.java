package za.co.liberty.web.wicket.convert.converters;

import java.math.BigDecimal;
import java.util.Locale;

public class CurrencyPropertyConverter extends CurrencyConverter {

	private static final long serialVersionUID = 1L;

	@Override
	public BigDecimal convertToObject(String value, Locale locale) {
		if (value==null || value.length()==0) {
			return null;
		}
		return super.convertToObject(value, locale);
	}
	
	

}
