package za.co.liberty.business.guicontrollers.userprofiles;

import java.util.List;

import javax.ejb.Local;

import za.co.liberty.dto.userprofiles.ISessionUserProfile;
import za.co.liberty.dto.userprofiles.MenuItemDTO;
import za.co.liberty.exceptions.data.DataNotFoundException;

/**
 * Interface for Menu Management bean
 * 
 * @author jzb0608 - 23 Apr 2008
 *
 */
@Local
public interface IMenuManagement {

	/**
	 * Retrieve all the menu items
	 * 
	 * @return List of all menu items
	 */
	public List<MenuItemDTO> findAllMenuItems();
	
	/**
	 * Find a menu item with its primary key
	 * 
	 * @param primaryKey
	 * @return
	 * @throws DataNotFoundException 
	 */
	public MenuItemDTO findMenuItem(long primaryKey) throws DataNotFoundException;
	
	/**
	 * Create a menu item
	 * 
	 * @param dto New menu item to store
	 * @param user
	 * @return The newly created Menu item
	 */
	public MenuItemDTO createMenuItem(MenuItemDTO dto,ISessionUserProfile user);
	
	/**
	 * Persist the changes made to menu item
	 * @param dto
	 * @return Updated Menu Item
	 */
	public MenuItemDTO updateMenuItem(MenuItemDTO dto);
}
