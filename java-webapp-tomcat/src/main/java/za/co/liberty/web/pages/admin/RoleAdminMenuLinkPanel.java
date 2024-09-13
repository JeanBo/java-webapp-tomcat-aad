package za.co.liberty.web.pages.admin;

import java.util.List;

import za.co.liberty.dto.userprofiles.MenuItemDTO;
import za.co.liberty.dto.userprofiles.ProfileRoleDTO;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.pages.admin.models.RolesModel;

/**
 * <p>
 * Manages the menu items linked to a role.
 * </p>
 * 
 * @author jzb0608 - 07 May 2008
 * 
 */
public class RoleAdminMenuLinkPanel extends MenuLinkingPanel<RolesModel> {

	private static final long serialVersionUID = -3746490082834722838L;

	/**
	 * Default constructor
	 * 
	 */
	public RoleAdminMenuLinkPanel(String id, RolesModel pageModel,
			EditStateType editState) {
		super(id, pageModel, editState);
	}
	
	/**
	 * Return the list of currently selected items
	 * @return
	 */
	protected List<MenuItemDTO> getCurrentlyLinkedItemList() {
		return ((ProfileRoleDTO) bean).getDefaultMenuItemList();
	}
	
	/**
	 * Get list of all available items
	 * 
	 * @return
	 */
	protected List<MenuItemDTO> getCompleteAvailableItemList() {
		return pageModel.getAllAvailableMenuItems();
	}	
}
