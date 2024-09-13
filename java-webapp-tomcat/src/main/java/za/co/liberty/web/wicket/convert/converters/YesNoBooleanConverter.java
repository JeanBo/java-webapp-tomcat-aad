package za.co.liberty.web.wicket.convert.converters;

import java.util.Locale;

import org.apache.wicket.util.convert.IConverter;

/**
 * This class allows conversion between String and Boolean objects, 
 * possibly allowing nulls and representing null values with a supplied alternative.
 * 
 * To use an alternate label for nulls, the constructor that takes a single String parameter can be
 * used to instantiate the class, or both the setAllowNull and setNullLabel methods can be used to 
 * enable nulls and specify the label used to represent null values 
 * 
 * @author kxd1203
 *
 */
public class YesNoBooleanConverter implements IConverter {
	
	private static final long serialVersionUID = 1L;
	
	private boolean allowNull = false;
	private String nullLabel;
	
	/**
	 * Default constructor - create a converter that does not allow nulls
	 */
	public YesNoBooleanConverter() {
	}
	
	/**
	 * Constructor that can be used to allow nulls and represent null values with a specified value and vice versa
	 * @param nullLabel The String value to represent null values with
	 */
	public YesNoBooleanConverter(String nullLabel) {
		setAllowNull(nullLabel!=null);
		setNullLabel(nullLabel);
	}
	
	private String getLabelForBooleanValue(Boolean boolVal,Locale locale) {
		if (boolVal) {
			return "Yes";
		} else {
			return "No";
		}
	}	

	public Object convertToObject(String object, Locale locale) {
		if (object != null && object.equalsIgnoreCase(getLabelForBooleanValue(true, locale))) {
			return new Boolean(true);
		} else if (object != null && object.equalsIgnoreCase(getLabelForBooleanValue(false, locale))) {
			return new Boolean(false);
		} else if (object != null && isAllowNull() && getNullLabel()!=null
				&&object.equals(getNullLabel())) {
			return null;
		} else {
			return new Boolean(false);
		}
	}

	public String convertToString(Object object, Locale locale) {
		System.out.println("Convert To String: "+object);
		if (object!=null && object instanceof Boolean) {
			return getLabelForBooleanValue((Boolean)object, locale);
		} else if (object==null && isAllowNull() && getNullLabel()!=null) {
			return getNullLabel();
		} else if (object!=null && !(object instanceof Boolean) && isAllowNull() && getNullLabel()!=null) {
			return getNullLabel();
		} else {
			return getLabelForBooleanValue(false, locale);
		}
	}

	public boolean isAllowNull() {
		return allowNull;
	}

	public void setAllowNull(boolean allowNull) {
		this.allowNull = allowNull;
	}

	public String getNullLabel() {
		return nullLabel;
	}

	public void setNullLabel(String nullLabel) {
		this.nullLabel = nullLabel;
	}
	
	

}
