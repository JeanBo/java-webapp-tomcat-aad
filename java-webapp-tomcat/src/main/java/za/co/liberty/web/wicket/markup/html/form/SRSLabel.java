package za.co.liberty.web.wicket.markup.html.form;

import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;

import za.co.liberty.web.data.enums.ColumnStyleType;

/**
 * Extends a basic label by adding SRS specific style information. Style
 * information will only be changed if isView or isNumber is true otherwise this
 * is a normal label.
 * 
 * @author jzb0608 - 24 Apr 2008
 * 
 */
public class SRSLabel extends Label implements Stylable<SRSLabel> {

	private static final long serialVersionUID = 172017402623052988L;

	private ColumnStyleType columnStyle;

	boolean isView;

	boolean isNumber;

	/**
	 * Default constructor isView=false, isNumber=false
	 * 
	 * @param id
	 */
	public SRSLabel(final String id) {
		super(id);
	}

	/**
	 * Calls {@link #SRSLabel(String, IModel, boolean, boolean)} with
	 * isView=false and isNumber=false
	 * 
	 * @param id
	 * @param model
	 */
	public SRSLabel(final String id, final IModel model) {
		this(id, model, false, false);
	}

	/**
	 * Calls {@link #SRSLabel(String, IModel, boolean, boolean)} with
	 * isNumber=false
	 * 
	 * @param id
	 * @param model
	 * @param isView
	 */
	public SRSLabel(final String id, final IModel model, final boolean isView) {
		this(id, model, isView, false);
	}

	/**
	 * 
	 * @param id
	 * @param model
	 * @param isView
	 * @param isNumber
	 */
	public SRSLabel(final String id, final IModel model, final boolean isView,
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
	public SRSLabel setView(boolean isView) {
		this.isView = isView;
		return this;
	}

	/**
	 * Indicate if this is a number style class
	 * 
	 * @param isView
	 * @return
	 */
	public SRSLabel setNumber(boolean isNumber) {
		this.isNumber = isNumber;
		return this;
	}

	/**
	 * Set the style that should be applied to the column, set to null if no
	 * style should be applied
	 * 
	 * @return
	 */
	public SRSLabel setStyleColumn(ColumnStyleType styleType) {
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
