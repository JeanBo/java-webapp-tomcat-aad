package za.co.liberty.web.wicket.markup.html.form;

import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;

import za.co.liberty.web.data.enums.ColumnStyleType;

/**
 * A text field for the SRS application. Allows for the application of styles
 * 
 * @author jzb0608 - 25 Apr 2008
 * 
 */
public class SRSTextField extends TextField implements Stylable<SRSTextField> {

	private static final long serialVersionUID = -5680774706773681796L;

	private ColumnStyleType columnStyle;

	boolean isView;

	boolean isNumber;

	/**
	 * Default constructor isView=false, isNumber=false
	 * 
	 * @param id
	 */
	public SRSTextField(final String id) {
		super(id);
	}

	/**
	 * Calls {@link #SRSTextField(String, IModel, boolean, boolean)} with
	 * isView=false and isNumber=false
	 * 
	 * @param id
	 * @param model
	 */
	public SRSTextField(final String id, final IModel model) {
		this(id, model, false, false);
	}

	/**
	 * Calls {@link #SRSTextField(String, IModel, boolean, boolean)} with
	 * isNumber=false
	 * 
	 * @param id
	 * @param model
	 * @param isView
	 */
	public SRSTextField(final String id, final IModel model,
			final boolean isView) {
		this(id, model, isView, false);
	}

	/**
	 * 
	 * @param id
	 * @param model
	 * @param isView
	 * @param isNumber
	 */
	public SRSTextField(final String id, final IModel model,
			final boolean isView, final boolean isNumber) {
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
	public SRSTextField setView(boolean isView) {
		this.isView = isView;
		return this;
	}

	/**
	 * Indicate if this is a number style class
	 * 
	 * @param isView
	 * @return
	 */
	public SRSTextField setNumber(boolean isNumber) {
		this.isNumber = isNumber;
		return this;
	}

	/**
	 * Set the style that should be applied to the column, set to null if no
	 * style should be applied
	 * 
	 * @return
	 */
	public SRSTextField setStyleColumn(ColumnStyleType styleType) {
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
