/**
 * 
 */
package za.co.liberty.web.wicket.markup.html.form;

import java.util.Date;
import java.util.Map;

import org.apache.wicket.datetime.DateConverter;
import org.apache.wicket.datetime.PatternDateConverter;
import org.apache.wicket.extensions.markup.html.form.DateTextField;
import org.apache.wicket.extensions.yui.calendar.DatePicker;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.model.IModel;

import za.co.liberty.web.wicket.convert.converters.SRSDateConverter;

/**
 * This field should only be used for datepicker
 * 
 * @author DZS2610
 *
 */
public class SRSDateField  extends DateTextField {	

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Default date format converter, based on default pattern
	 * 
	 * @return
	 */
	public static DateConverter newDefaultDateConverter () {
		return new PatternDateConverter("dd/MM/yyyy",false);
	}
	
	/**
	 * Default date format retrieved from {@linkplain SRSDateConverter#DATE_FORMAT_PATTERN}
	 * 
	 * @return
	 */
	public String getDefaultDatePattern () {
		return SRSDateConverter.DATE_FORMAT_PATTERN;
	}
	
	
	/**
	 * @param id
	 * @param model
	 */
	public SRSDateField(String id, IModel<Date> model) {
		super(id, model, "dd/MM/yyyy");
	}
	
	/**
	 * Creates a new DatePicker behaviour and adds it automatically.
	 * 
	 */
	public void addNewDatePicker() {
		this.add(newDatePicker());
	}
	
	
	/**
	 * Return a default date picker for this panel with settings defined.
	 * 
	 * @return
	 */
	public DatePicker newDatePicker() {
		DatePicker datePicker = new DatePicker() {
			@Override
			protected boolean enableMonthYearSelection() {
				return true;
			}

			@Override
			protected String getAdditionalJavaScript() {
				return "${calendar}.cfg.setProperty(\"navigator\",true,false); ${calendar}.render();";
			}
		};

		datePicker.setShowOnFieldClick(true);
		datePicker.setAutoHide(true);
		return datePicker;
	}

}
