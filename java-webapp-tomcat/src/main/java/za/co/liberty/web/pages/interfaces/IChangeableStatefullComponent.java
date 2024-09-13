package za.co.liberty.web.pages.interfaces;

import org.apache.wicket.ajax.AjaxRequestTarget;

import za.co.liberty.web.data.enums.EditStateType;

/**
 * Indicates that a component has state and that it can 
 * be changed
 * 
 * @author JZB0608 - 30 Apr 2008
 *
 */
public interface IChangeableStatefullComponent extends IStatefullComponent{

	/**
	 * Change the edit state
	 * 
	 * @param newState
	 * @param target
	 */
	public void setEditState(EditStateType newState, AjaxRequestTarget target);
	
}
