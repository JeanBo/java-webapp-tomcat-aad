package za.co.liberty.web.data.pages;

import java.util.List;

import za.co.liberty.dto.userprofiles.ProfileRoleDTO;

/** 
 * Used to implement page data models for 
 * Maintenance pages.
 * 
 * @author JZB0608 - 05 May 2008
 *
 * @param <T> Dto type
 */
public interface IMaintenancePageModel <T> {
	/**
	 * Set the currently selected item
	 * 
	 * @param selected
	 */
	public void setSelectedItem(T selected);
	
	/**
	 * Get the currently selected item
	 * @return
	 */
	public T getSelectedItem();

	/**
	 * Retrieve the list of available items to select
	 * 
	 * @return
	 */
	public List<T> getSelectionList();

	/**
	 * Set the list of available items to select
	 * 
	 * @param selectionList
	 */
	public void setSelectionList(List<T> selectionList);
}
