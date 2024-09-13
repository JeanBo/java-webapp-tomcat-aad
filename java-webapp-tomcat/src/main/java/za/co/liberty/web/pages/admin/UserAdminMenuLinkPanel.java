package za.co.liberty.web.pages.admin;

import java.util.ArrayList;
import java.util.List;

import javax.naming.NamingException;

import org.apache.log4j.Logger;

import za.co.liberty.business.guicontrollers.userprofiles.IUserAdminManagement;
import za.co.liberty.dto.userprofiles.MenuItemDTO;
import za.co.liberty.dto.userprofiles.PartyProfileDTO;
import za.co.liberty.dto.userprofiles.ProfileRoleDTO;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.helpers.lookup.ServiceLocator;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.pages.admin.models.UserAdminModel;

/**
 * <p>
 * Manages the menu items linked to a user.
 * </p>
 * 
 * @author jzb0608 - 14 May 2008
 * 
 */
public class UserAdminMenuLinkPanel extends MenuLinkingPanel<UserAdminModel> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2985122363706095792L;

	/**
	 * Default constructor
	 * 
	 */
	public UserAdminMenuLinkPanel(String id, UserAdminModel pageModel,
			EditStateType editState) {
		super(id, pageModel, editState);
	}

	/**
	 * Return the list of currently selected items
	 * @return
	 */
	protected List<MenuItemDTO> getCurrentlyLinkedItemList() {
		return ((PartyProfileDTO) bean).getMenuItemList();
	}
	
	/**
	 * Get list of all available items
	 * 
	 * @return
	 */
	protected List<MenuItemDTO> getCompleteAvailableItemList() {
		return  pageModel.getAllAvailableMenuItems();
	}

	@Override
	protected List<MenuItemDTO> getNotSelectableAdditionalLinkedItemList() {
		List<MenuItemDTO> menuItems = new ArrayList<MenuItemDTO>();
		PartyProfileDTO profile = (PartyProfileDTO) bean;
		try {//MSK#Change :commented below method temporarily and adding Servicelocator
			//UserAdminModel.getSessionBean().populateMenuItemsforRole(profile);
			ServiceLocator.lookupService(IUserAdminManagement.class).populateMenuItemsforRole(profile);
		} catch (CommunicationException e) {
			// TODO handle this error, Error Handling still to be cleared up
			Logger.getLogger(this.getClass()).error(
					"Unable to initialise session bean for page", e);
			this.error(e.getMessage());
			//no items will then be added to the list, not serious
			return null;
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			throw new CommunicationException(e);
		} 		
		if(profile.getRoleItemList() != null){
			for(ProfileRoleDTO role : profile.getRoleItemList()){
				menuItems.addAll(role.getDefaultMenuItemList());
			}
		}		
		return menuItems;				
	}	
}
