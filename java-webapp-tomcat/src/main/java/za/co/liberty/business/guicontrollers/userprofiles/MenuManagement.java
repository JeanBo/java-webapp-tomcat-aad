package za.co.liberty.business.guicontrollers.userprofiles;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import za.co.liberty.business.userprofiles.IProfileManagement;
import za.co.liberty.dto.rating.SegmentNameDTO;
import za.co.liberty.dto.userprofiles.ISessionUserProfile;
import za.co.liberty.dto.userprofiles.MenuItemDTO;
import za.co.liberty.exceptions.data.DataNotFoundException;

/**
 * Manage menu items
 * 
 * @author jzb0608 - 23 Apr 2008
 * Modified by Dean Scott(30 July 2008) to use the ProfileManagement bean
 * 
 */
@Stateless
public class MenuManagement implements IMenuManagement {
	
	//@EJB
	//protected IProfileManagement managementBean;	
	

	/**
	 * Retrieve all the menu items
	 * 
	 * @return List of all menu items
	 */
	public List<MenuItemDTO> findAllMenuItems() {
		List<MenuItemDTO> list = new ArrayList<MenuItemDTO>();//managementBean.findAllMenuItems();
		/*
		 * if (list!=null) { // Sort by long description Collections.sort(list, new
		 * Comparator<MenuItemDTO>() {
		 * 
		 * public int compare(MenuItemDTO o1, MenuItemDTO o2) { return
		 * o1.getMenuItemLongDescription().compareTo(o2.getMenuItemLongDescription()); }
		 * 
		 * }); }
		 */		
		MenuItemDTO menu=new MenuItemDTO();
		menu.setMenuItemDescription("Advanced Practice Panel");
		menu.setMenuItemID((long) 421);
		menu.setMenuItemLongDescription("Advanced Practice Panel");
		menu.setImplClazz("za.co.liberty.web.pages.advancedPractice.AdvancedPracticePanel");
		menu.setAddAccess(true);
		menu.setPanel(true);
		menu.setModifyAccess(false);
		menu.setMenuItemName("Advanced Practice Panel");
		menu.setDeleteAccess(false);
		list.add(menu);
		menu=new MenuItemDTO();
		menu.setMenuItemDescription("Segment Name Admin");
		menu.setMenuItemID((long) 261);
		menu.setMenuItemLongDescription("Segment Name Admin");
		menu.setImplClazz("za.co.liberty.web.pages.admin.SegmentNameAdminPage");
		menu.setAddAccess(true);
		menu.setPanel(false);
		menu.setModifyAccess(false);
		menu.setMenuItemName("Segment Name Admin");
		menu.setDeleteAccess(false);
		list.add(menu);
		return list;
	}

	/**
	 * Find a menu item with its primary key
	 * 
	 * @param primaryKey
	 * @return
	 * @throws DataNotFoundException 
	 */
	public MenuItemDTO findMenuItem(long primaryKey) throws DataNotFoundException {
		System.out.println("-------------------------------findMenuItem"+primaryKey);
		for (MenuItemDTO d : findAllMenuItems()) {
			if (d.getMenuItemID() == primaryKey) {
				return d;
			}
		}
		
		MenuItemDTO name = new MenuItemDTO();
		name.setMenuItemName("dummy");
		name.setMenuItemID(primaryKey);
		name.setMenuItemLongDescription("dummy Advanced Practice Panel");
		name.setImplClazz("za.co.liberty.web.pages.advancedPractice.AdvancedPracticePanel");
		name.setAddAccess(true);
		name.setPanel(true);
		name.setModifyAccess(false);
		name.setMenuItemName("dummy Advanced Practice Panel");
		name.setDeleteAccess(false);
		//return name;
		
		//return managementBean.findMenuItem(primaryKey);
		return name;//managementBean.findMenuItem(primaryKey);
	}

	/**
	 * Create a menu item and persist.
	 * 
	 * @param dto
	 *            New menu item to store
	 * @param user
	 * @return The newly created Menu item
	 */
	public MenuItemDTO createMenuItem(MenuItemDTO dto, ISessionUserProfile user) {
		System.out.println("------------------------createMenuItem");
		//return managementBean.createMenuItem(dto, user.getPartyOid()+"");
		dto.setMenuItemID((long)( Math.random()*(201)+100));
		return dto; 
	}

	/**
	 * Persist the changes made to menu item
	 * 
	 * @param dto
	 * @return Updated Menu Item
	 */
	public MenuItemDTO updateMenuItem(MenuItemDTO dto) {
		System.out.println("-----------------------------updateMenuItem"+dto.getMenuItemID());
		//return managementBean.updateMenuItem(dto);
		return dto;
	}

}
