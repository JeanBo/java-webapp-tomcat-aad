package za.co.liberty.web.wicket.convert.converters.agreement;

import java.util.Locale;

import org.apache.wicket.util.convert.IConverter;

public class ThirteenDigitConsCodeConverter implements IConverter {

	private static final long serialVersionUID = -2662871231059813828L;
	
	private static final String REGEX_DIGITS_ONLY = "[\\d]+?";

	/**
	 * Convert a String consultant code value to a Long value 
	 */
	public Object convertToObject(String display, Locale locale) {
		Long ret = null;
		/**
		 * Use a regex matcher to see if only digits are 
		 * present in the display value before trying conversion -
		 * faster than catching an exception and handling. 
		 */
		if (display!=null && display.matches(REGEX_DIGITS_ONLY)) {
			ret = Long.parseLong(display);
		}
		return ret;
	}

	/**
	 * Convert a Long consultant code value to a String display value
	 */
	public String convertToString(Object value, Locale locale) {
		if (value instanceof Long) {
			StringBuffer ret = new StringBuffer(""+value);
			int diff = 13-ret.length();
			for (int i=0;i<diff;i++) {
				ret.insert(0, "0");
			}
			return ret.toString();
		}
		return null;
	}

}
