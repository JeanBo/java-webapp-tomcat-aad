package za.co.liberty.web.wicket.markup.html.form;

import za.co.liberty.web.data.enums.ColumnStyleType;

/**
 * Specifies components that can apply style info
 * 
 * @author jzb0608 - 25 Apr 2008
 *
 */
public interface Stylable <T> {

	/**
	 * Indicates the style of the column. Should have no effect 
	 * when null
	 * 
	 * @param styleType
	 * @return
	 */
	public T setStyleColumn(ColumnStyleType styleType);
	
	/**
	 * Indicates that the component may only be viewed.
	 * 
	 * @param isView
	 * @return
	 */
	public T setView(boolean isView);
	
	/**
	 * Indicates that the component applies to a number
	 * 
	 * @param isNumber
	 * @return
	 */
	public T setNumber(boolean isNumber);
}
