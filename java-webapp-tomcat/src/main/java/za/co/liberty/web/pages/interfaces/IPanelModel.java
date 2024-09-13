package za.co.liberty.web.pages.interfaces;

import java.io.Serializable;

/**
 * Panel Model containg all the detail required for the panel to function
 * @author DZS2610
 *
 */
public interface IPanelModel<T> extends Serializable {

	/**
	 * Returns the data collected by the screen
	 * @return
	 */
	public T getPanelData();
}
