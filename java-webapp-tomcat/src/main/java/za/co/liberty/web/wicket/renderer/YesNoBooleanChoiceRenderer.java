package za.co.liberty.web.wicket.renderer;

import java.util.Locale;

import org.apache.wicket.util.convert.IConverter;

import za.co.liberty.web.wicket.convert.converters.YesNoBooleanConverter;
import za.co.liberty.web.wicket.markup.html.form.SRSAbstractChoiceRenderer;

/**
 * This class represents a renderer ro render a boolean choice as either a yes or a no, and if a null label is supplied
 * then the supplied label will be used in the case of rendering a null value, otherwise no will be used.
 * @author kxd1203
 *
 */
public class YesNoBooleanChoiceRenderer extends SRSAbstractChoiceRenderer {

	IConverter converter;
	
	/**
	 * Default constructor - create a converter that does not allow nulls
	 */
	public YesNoBooleanChoiceRenderer() {
		converter = new YesNoBooleanConverter();
	}
	
	/**
	 * Constructor that can be used to allow nulls and represent null values with a specified value
	 * @param nullLabel The String value to represent null values with
	 */
	public YesNoBooleanChoiceRenderer(String nullLabel) {
		converter = new YesNoBooleanConverter(nullLabel); 
	}	
	
	public Object getDisplayValue(Object object) {
		return converter.convertToString(object, Locale.ENGLISH);
	}

	public String getIdValue(Object object, int index) {
		return object.toString();
	}

}
