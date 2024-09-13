package za.co.liberty.web.pages.admin;

import java.util.List;

import za.co.liberty.dto.userprofiles.PartyProfileDTO;
import za.co.liberty.dto.userprofiles.ProfileRoleDTO;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.pages.admin.models.UserAdminModel;

/**
 * <p>
 * Manages the roles linked to a user.
 * </p>
 * 
 * @author jzb0608 - 16 May 2008
 * 
 */
public class UserAdminRoleLinkPanel extends RoleLinkingPanel<UserAdminModel> {

	private static final long serialVersionUID = 2985122363706095392L;

	/**
	 * Default constructor
	 * 
	 */
	public UserAdminRoleLinkPanel(String id, UserAdminModel pageModel,
			EditStateType editState) {
		super(id, pageModel, editState);
	}

	/**
	 * Return the list of currently selected items
	 * @return
	 */
	protected List<ProfileRoleDTO> getCurrentlyLinkedItemList() {
		return ((PartyProfileDTO) bean).getRoleItemList();
	}
	
	/**
	 * Get list of all available items
	 * 
	 * @return
	 */
	protected List<ProfileRoleDTO> getCompleteAvailableItemList() {
		return  pageModel.getAllAvailableRoles();
	}
}
