package za.co.liberty.business.userprofiles;

import java.util.List;

import javax.ejb.Local;

import za.co.liberty.dto.userprofiles.MenuItemDTO;
import za.co.liberty.dto.userprofiles.PartyProfileDTO;
import za.co.liberty.dto.userprofiles.ProfileRoleDTO;
import za.co.liberty.dto.userprofiles.RuleDTO;
import za.co.liberty.exceptions.data.DataNotFoundException;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.exceptions.security.InvalidUserIdException;
import za.co.liberty.interfaces.rules.RuleDataType;
import za.co.liberty.party.vo.PartyVO;
import za.co.liberty.persistence.party.entity.fastlane.PartyProfileFLO;
import za.co.liberty.persistence.party.entity.fastlane.PartyProfileNameFLO;

/**
 * Interface for ProfileManagement Bean
 * Used for the local stub of EJB
 * @author DZS2610
 *
 */
@Local
public interface IProfileManagement {

	/**
	 * Search for users starting with the given text
	 * 
	 * @return List a list of User Profiles
	 */
	public List<PartyProfileFLO> findUserFastLaneWithUacfStartingWith(String startsWith);
	
	/**
	 * Search for users starting with the given text.  Object that is returned also 
	 * includes user firstName and lastName.
	 * 
	 * @return list of PartyProfileNameFLO objects
	 */
	public List<PartyProfileNameFLO> findUserAndNameFastLaneWithUacfStartingWith(String startsWith);
	
	/**
	 * Search for a user with a given partyOid, object that is returned also 
	 * includes user firstName and lastName.
	 * 
	 * @return a PartyProfileNameFLO objects
	 */
	public PartyProfileNameFLO findUserAndNameFastLaneWithPartyOid(long partyOid) throws DataNotFoundException;
	
	/**
	 * Find a user with its primary key
	 * 
	 * @param primaryKey
	 * @return
	 * @throws DataNotFoundException 
	 */
	public PartyProfileDTO findUserForUpdate(long primaryKey) throws DataNotFoundException;
	
	/**
	 * Find a user with its primary key
	 * 
	 * @param primaryKey
	 * @return
	 */
	public PartyProfileDTO findUserForUpdateByPartyOID(long partyOID) 
		throws DataNotFoundException;	
	
	/**
	 * Persist the changes made to a user
	 * 
	 * @param dto
	 * @param updatedBy partyOid
	 * 
	 * @return Updated Role
	 */
	public PartyProfileDTO updateUser(PartyProfileDTO dto, String updatedBy);
	
	

	/**
	 * Retrieve a list of all menu items that are available
	 * for selection
	 *  
	 * @return
	 */
	public List<MenuItemDTO> findAllAvailableMenuItems();

	/**
	 * Retrieve a list of all rules that are available
	 * for selection.
	 *  
	 * @return
	 */
	public List<RuleDTO> findAllAvailableRules() ;
	
	/**
	 * Retrieve a list of all roles that are available
	 * for selection
	 *  
	 * @return
	 */
	public List<ProfileRoleDTO> findAllAvailableRoles();
		
	/**
	 * Retrieve all the rules
	 * 
	 * @return List of all menu items
	 */
	public List<RuleDTO> findAllRules();
	

	/**
	 * Find a rule with its primary key
	 * 
	 * @param primaryKey
	 * @return
	 * @throws DataNotFoundException 
	 */
	public RuleDTO findRule(long primaryKey) throws DataNotFoundException;

	/**
	 * Create a menu item
	 * 
	 * @param dto New rule to store
	 * @param createdBy partyOid
	 * @return The newly created rule
	 */
	public RuleDTO createRule(RuleDTO dto, String createdBy);

	/**
	 * Persist the changes made to a rule
	 * 
	 * @param dto
	 * @return Updated Rule
	 */
	public RuleDTO updateRule(RuleDTO dto);
	
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
	 * Create a menu item and persist.
	 * 
	 * @param dto
	 *            New menu item to store
	 * @param createdBy partyOid
	 * @return The newly created Menu item
	 */
	public MenuItemDTO createMenuItem(MenuItemDTO dto, String createdBy);

	/**
	 * Persist the changes made to menu item
	 * 
	 * @param dto
	 * @return Updated Menu Item
	 */
	public MenuItemDTO updateMenuItem(MenuItemDTO dto);
	
	/**
	 * Retrieve all the roles
	 * 
	 * @return List of all menu items
	 */
	public List<ProfileRoleDTO> findAllRoles();
	
	/**
	 * Find a role with its primary key. More expensive than a find
	 * as the mappings are also retrieved.
	 * 
	 * @param primaryKey
	 * @return
	 * @throws DataNotFoundException 
	 */
	public ProfileRoleDTO findRole(long primaryKey) throws DataNotFoundException;
	
	/**
	 * Create a menu item
	 * 
	 * @param dto New role to store
	 * @param createdBy partyOid 
	 * @return The newly created rule
	 */
	public ProfileRoleDTO createRole(ProfileRoleDTO dto, String createdBy);
	
	/**
	 * Persist the changes made to a role. 
	 * 
	 * @param dto
	 * @param udpatedBy partyOid
	 * 
	 * @return Updated Role
	 */
	public ProfileRoleDTO updateRole(ProfileRoleDTO dto, String updatedBy);
	
	/**
	 * Add MenuItemDTO to all userProfileDTO ProfileRoles
	 * @param userProfileDTO
	 */
	public void populateMenuItemsforRole(PartyProfileDTO userProfileDTO);
	
	/**
	 * Adds RunnableRuleDTO to all userProfileDTO ProfileRoles
	 * @param userProfileDTO
	 */
	public void populateRuleItemsforRole(PartyProfileDTO userProfileDTO);
	
	/**
	 * Gets the ProfileRoleDTOs for a given partyid
	 * 
	 * @param partyOid
	 * @return
	 */
	public List<ProfileRoleDTO> getProfileRoleDTOsForParty(long partyOid)
			throws DataNotFoundException;

	/**
	 * Retrieve all the rule data type objects
	 * 
	 * @return
	 */
	public List<RuleDataType> findAllRuleDataTypes();
	
	/**
	 * Update the party Profile so as to reflect the new uacfid added This is
	 * only a temp method for the old SRS system <br/>Will also get the own
	 * agreements for the party and add them to the new SRS tables
	 * 
	 * @param partyVo
	 * @param oldUacfID
	 * @param createdBy
	 * @throws InvalidUserIdException
	 * @throws CommunicationException
	 * @throws DataNotFoundException
	 */
	public void updatePartyProfile(PartyVO partyVo, String oldUacfID, String createdBy)
			throws InvalidUserIdException, CommunicationException,
			DataNotFoundException;
	
	/**
	 * checks if the given role is linked to any roles or profiles
	 * 
	 * @param ruleID
	 * @return
	 */
	public boolean ruleIsLinkedToRolesOrProfiles(long ruleID);	
}
