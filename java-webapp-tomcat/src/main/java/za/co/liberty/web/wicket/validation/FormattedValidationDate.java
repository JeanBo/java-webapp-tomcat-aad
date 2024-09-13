package za.co.liberty.web.wicket.validation;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * This class represents a date that is used as a paramter in the standard DateValidator
 * to provide a representation of the date in a custom format
 * 
 * 
 * @author kxd1203
 *
 */
public class FormattedValidationDate extends Date {
	
	private static final long serialVersionUID = -2174406455080716927L;
	
	private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy"); 

	@Override
	public String toString() {
		return sdf.format(this);
	}

	@Override
	public String toGMTString() {
		return toString();
	}

	@Override
	public String toLocaleString() {
		return toString();
	}

}
