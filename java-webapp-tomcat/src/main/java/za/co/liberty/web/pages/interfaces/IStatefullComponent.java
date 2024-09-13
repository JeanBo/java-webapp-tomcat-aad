package za.co.liberty.web.pages.interfaces;

import za.co.liberty.web.data.enums.EditStateType;

/**
 * Indicates that a component has state 
 * 
 * @author JZB0608 - 13 May 2008
 *
 */
public interface IStatefullComponent {

	/**
	 * Get the edit state of the component 
	 * 
	 * @return
	 */
	public EditStateType getEditState();
	
}
