// Source File Name:   SRSFlatCalendar.java

package za.co.liberty.web.pages.flatcalendar;

import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import za.co.liberty.web.data.enums.ColumnStyleType;
import za.co.liberty.web.wicket.markup.html.form.SRSStyleHelper;

// Referenced classes of package za.co.liberty.web.flatcalendar:
//            FlatCalendarSettings

public class SRSFlatCalendar extends Panel
{
	
	private static final long serialVersionUID = 1L;

	private ColumnStyleType columnStyle;

	boolean isView;


	boolean isNumber;	

	/**
	 * Default constructor isView=false, isNumber=false
	 * 
	 * @param id
	 */
	public SRSFlatCalendar(final String id) {
		super(id);		
	}

	/**
	 * Calls {@link #SRSFlatCalendar(String, IModel, boolean, boolean)} with
	 * isView=false and isNumber=false
	 * 
	 * @param id
	 * @param model
	 */
	public SRSFlatCalendar(final String id, final IModel model) {
		this(id, model, false, false);
	}

	/**
	 * Calls {@link #SRSFlatCalendar(String, IModel, boolean, boolean)} with
	 * isNumber=false
	 * 
	 * @param id
	 * @param model
	 * @param isView
	 */
	public SRSFlatCalendar(final String id, final IModel model, final boolean isView) {
		this(id, model, isView, false);
	}

	/**
	 * 
	 * @param id
	 * @param model
	 * @param isView
	 * @param isNumber
	 */
	public SRSFlatCalendar(final String id, final IModel model, final boolean isView,
			final boolean isNumber) {
		super(id, model);
		this.isNumber = isNumber;
		this.isView = isView;		
	}

	/**
	 * Indicate if this is a view style class
	 * 
	 * @param isView
	 * @return
	 */
	public SRSFlatCalendar setView(boolean isView) {
		this.isView = isView;
		return this;
	}

	/**
	 * Indicate if this is a number style class
	 * 
	 * @param isView
	 * @return
	 */
	public SRSFlatCalendar setNumber(boolean isNumber) {
		this.isNumber = isNumber;
		return this;
	}

	/**
	 * Set the style that should be applied to the column, set to null if no
	 * style should be applied
	 * 
	 * @return
	 */
	public SRSFlatCalendar setStyleColumn(ColumnStyleType styleType) {
		this.columnStyle = styleType;
		return this;
	}

	/**
	 * Decorate the class html tag with the relevant style information
	 */
	@Override
	protected void onComponentTag(ComponentTag tag) {
		super.onComponentTag(tag);
		SRSStyleHelper.getInstance().decorateComponentTag(tag, columnStyle,
				isView, isNumber);
	}

}
