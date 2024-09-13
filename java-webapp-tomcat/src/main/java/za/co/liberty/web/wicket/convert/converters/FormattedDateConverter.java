package za.co.liberty.web.wicket.convert.converters;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.apache.wicket.util.convert.ConversionException;
import org.apache.wicket.util.convert.converter.AbstractConverter;
import org.apache.wicket.util.string.Strings;

/**
 * Format dates for the SRS application.  Allows for the input of dates
 * in various formats i.e. "dd-MM-yyyy", "dd/MM/yyyy" etc. but always
 * displays in "dd/MM/yyyy".
 * 
 * <p>Note that this is not thread safe, don't set as default converter.</p>
 * 
 * @author JZB0608 - 23 May 2008
 *
 */
public class FormattedDateConverter extends AbstractConverter {

	private static final long serialVersionUID = -2222271935848647781L;

	/* Constants */
	/**
	 * Format for dates
	 */
	public SimpleDateFormat dateFormat;

	/* Local variables */
	private transient Logger logger = Logger.getLogger(this.getClass());
	
	public FormattedDateConverter(SimpleDateFormat dateFormat) {
		super();
		this.dateFormat = dateFormat;
	}

	@Override
	protected Class getTargetType() {
		return Date.class;

	}

	/**
	 * Create a date from the specified string value
	 */
	public Object convertToObject(String value, Locale locale) {
		if (Strings.isEmpty(value)) {
			return null;
		}
		
		/* Tidy input date */
		value = value.replaceAll("-|\\p{Blank}|,|\\\\", "/");
		if (value.length()==8 && value.indexOf("/")==-1) {
			// Fix non slash
			value = value.substring(0,2) + "/" + value.substring(2,4)+ "/" 
				+ value.substring(4,8);
		}
		
		/* Attempt a match */
		try {
			Date date = dateFormat.parse(value);
			return date;
		} catch (ParseException e) {

		}
		
		throw new ConversionException("")
			.setResourceKey("validator.invalid")
			.setSourceValue(value)
			.setVariable("validator.type", "date");

	}

	@Override
	public String convertToString(Object value, Locale locale) {
		if (value == null) {
			return null;
		}
		return dateFormat.format(value);
	}

	public SimpleDateFormat getDateFormat() {
		return dateFormat;
	}

	public void setDateFormat(SimpleDateFormat dateFormat) {
		this.dateFormat = dateFormat;
	}
	
	

}
