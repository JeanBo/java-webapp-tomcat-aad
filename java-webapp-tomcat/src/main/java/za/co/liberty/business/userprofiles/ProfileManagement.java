/**
 * 
 */
package za.co.liberty.business.userprofiles;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.sql.DataSource;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import za.co.liberty.agreement.domain.spec.helper.SpecManagerLocalHome;
import za.co.liberty.business.converter.party.profile.MenuItemConverter;
import za.co.liberty.business.converter.party.profile.RequestActionLinkConverter;
import za.co.liberty.business.converter.party.profile.RoleConverter;
import za.co.liberty.business.converter.party.profile.RoleLinkConverter;
import za.co.liberty.business.converter.party.profile.RuleConverter;
import za.co.liberty.business.converter.party.profile.RuleLinkConverter;
import za.co.liberty.business.converter.party.profile.UserProfileConverter;
import za.co.liberty.business.party.IPartyManagement;
import za.co.liberty.business.security.ISecurityManagement;
import za.co.liberty.dto.contracting.ResultPartyDTO;
import za.co.liberty.dto.userprofiles.AllowableRequestActionDTO;
import za.co.liberty.dto.userprofiles.ExplicitAgreementDTO;
import za.co.liberty.dto.userprofiles.MenuItemDTO;
import za.co.liberty.dto.userprofiles.PartyProfileDTO;
import za.co.liberty.dto.userprofiles.ProfileRoleDTO;
import za.co.liberty.dto.userprofiles.RuleDTO;
import za.co.liberty.dto.userprofiles.RunnableRuleDTO;
import za.co.liberty.entity.facades.IUserProfileEntityFacade;
import za.co.liberty.exceptions.data.DataNotFoundException;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.exceptions.security.InvalidUserIdException;
import za.co.liberty.helpers.util.CompareUtil;
import za.co.liberty.helpers.util.DateUtil;
import za.co.liberty.helpers.util.CompareUtil.MatchListResultObject;
import za.co.liberty.helpers.util.CompareUtil.MatchedObject;
import za.co.liberty.interfaces.common.IDifferentObjectComparatorWithNew;
import za.co.liberty.interfaces.persistence.IPersistenceFetchType;
import za.co.liberty.interfaces.persistence.party.PartyProfileFetchType;
import za.co.liberty.interfaces.persistence.party.RoleFetchType;
import za.co.liberty.interfaces.rules.RuleDataType;
import za.co.liberty.interfaces.rules.RuleType;
import za.co.liberty.party.vo.PartyVO;
import za.co.liberty.party.vo.PersonVO;
import za.co.liberty.persistence.party.IMenuItemEntityManager;
import za.co.liberty.persistence.party.IPartyProfileEntityManager;
import za.co.liberty.persistence.party.IRoleEntityManager;
import za.co.liberty.persistence.party.IRuleEntityManager;
import za.co.liberty.persistence.party.entity.AbstractRequestActionsEntity;
import za.co.liberty.persistence.party.entity.PartyProfileRequestActionsEntity;
import za.co.liberty.persistence.party.entity.RoleRequestActionsEntity;
import za.co.liberty.persistence.party.entity.fastlane.PartyProfileFLO;
import za.co.liberty.persistence.party.entity.fastlane.PartyProfileNameFLO;
import za.co.liberty.persistence.party.entity.userprofile.AbstractMenuLinkEntity;
import za.co.liberty.persistence.party.entity.userprofile.AbstractRuleLinkEntity;
import za.co.liberty.persistence.party.entity.userprofile.MenuItemEntity;
import za.co.liberty.persistence.party.entity.userprofile.PartyProfileEntity;
import za.co.liberty.persistence.party.entity.userprofile.PartyProfileMenuItemsEntity;
import za.co.liberty.persistence.party.entity.userprofile.PartyProfileRolesEntity;
import za.co.liberty.persistence.party.entity.userprofile.PartyProfileRulesEntity;
import za.co.liberty.persistence.party.entity.userprofile.RoleEntity;
import za.co.liberty.persistence.party.entity.userprofile.RoleMenuItemsEntity;
import za.co.liberty.persistence.party.entity.userprofile.RoleRulesEntity;
import za.co.liberty.persistence.party.entity.userprofile.RuleEntity;
import za.co.liberty.xml.queries.QueryHelper;

/**
 * Manage all business rules for user profiles
 * 
 * @author DZS2610
 * @author jzb0608 - 10/2009 - Removed all code relating to the dreaded
 * 		userProfileEntityFacade class, we now use entityManagers (yay) so
 * 		all conversions to entities and much of the other code were re-written 
 * 		in this class and
 * 
 */
@Stateless(name="ProfileManagement")
public class ProfileManagement implements IProfileManagement {

	private static final Logger logger = Logger.getLogger(ProfileManagement.class);
	
	@EJB
	IUserProfileEntityFacade entityFacade;

	@EJB
	IPartyProfileEntityManager partyProfileEntityManager;

	@EJB
	IRuleEntityManager ruleEntityManager;
	
	@EJB
	IRoleEntityManager roleEntityManager;
	
	@EJB
	IMenuItemEntityManager menuItemEntityManager;
	
	@EJB
	ISecurityManagement securityManagement;

	@EJB
	IAgreementPrivilegeManagement agreementPrivilegeManagement;

	@EJB
	SpecManagerLocalHome specManagerHome;

	@EJB 
	IPartyManagement partyManagementBean;
	
	@Resource(name = "PartyDatasource")
	DataSource partyDatasource;
	
	private static RoleLinkConverter roleLinkConverter = new RoleLinkConverter();
	private static MenuItemConverter menuItemConverter = new MenuItemConverter();
	private static RequestActionLinkConverter requestActionConverter = new RequestActionLinkConverter();
	private static RuleLinkConverter ruleLinkConverter = new RuleLinkConverter();
	
	/**
	 * Default fetch groups to retrieve when updating a complete user entity
	 */
	public static final IPersistenceFetchType[] DEFAULT_PROFILE_FETCH_TYPES_FOR_UPDATE =
		 new IPersistenceFetchType[] {
				PartyProfileFetchType.MENU_ITEMS,
				PartyProfileFetchType.ROLES,
				PartyProfileFetchType.RULES,
				PartyProfileFetchType.REQUEST_ACTIONS,
				PartyProfileFetchType.EXPLICIT_AGREEMENTS};
	/**
	 * Default fetch groups to retrieve when updating a complete role entity
	 */
	public static final RoleFetchType[] DEFAULT_ROLE_FETCH_TYPES_FOR_UPDATE =
		new RoleFetchType[] {
			RoleFetchType.MENU_ITEMS,
			RoleFetchType.REQUEST_ACTIONS,
			RoleFetchType.RULES};
	
	/**
	 * Comparator for rule links and rule DTO's
	 */
	private static final IDifferentObjectComparatorWithNew<AbstractRuleLinkEntity, RunnableRuleDTO> ruleComparator 
			= new IDifferentObjectComparatorWithNew<AbstractRuleLinkEntity, RunnableRuleDTO>() {
		
		public boolean isEqual(AbstractRuleLinkEntity currentObj,
				RunnableRuleDTO updatedObj) {
			if (currentObj == null || updatedObj == null) {
				return false;
			}
			return currentObj.getRuleEntity().getOid().equals(
					updatedObj.getRuleID());
		}
		public boolean isNew(RunnableRuleDTO object) {
			return object.getDbKey() == null;
		}
	};
	
	/**
	 * Comparator for menu item links and menu item DTO's
	 */
	private static final IDifferentObjectComparatorWithNew<AbstractMenuLinkEntity, MenuItemDTO> menuItemComparator 
			= new IDifferentObjectComparatorWithNew<AbstractMenuLinkEntity, MenuItemDTO>() {
		
		public boolean isEqual(AbstractMenuLinkEntity currentObj,
				MenuItemDTO updatedObj) {
			if (currentObj == null || updatedObj == null) {
				return false;
			}
			return currentObj.getMenuItemEntity().getOid().equals(
					updatedObj.getMenuItemID());
		}
		public boolean isNew(MenuItemDTO object) {
			return object.getDbKey() == null;
		}
	};

	/**
	 * Comparator for Request Action links and Request Action DTO's
	 */
	private static final IDifferentObjectComparatorWithNew<AbstractRequestActionsEntity, AllowableRequestActionDTO> requestActionsComparator
		= new IDifferentObjectComparatorWithNew<AbstractRequestActionsEntity, AllowableRequestActionDTO>() {
		
		public boolean isEqual(AbstractRequestActionsEntity currentObj, AllowableRequestActionDTO updatedObj) {
			if (currentObj == null || updatedObj == null) {
				return false;
			}
			return currentObj.getRequestKind() == updatedObj.getRequestKind();
		}
		public boolean isNew(AllowableRequestActionDTO object) {
			return object.getOid() == null;
		}

	};
	
	/**
	 * Comparator for role links and menu item DTO's
	 */
	private static final IDifferentObjectComparatorWithNew<PartyProfileRolesEntity, ProfileRoleDTO> roleComparator 
			= new IDifferentObjectComparatorWithNew<PartyProfileRolesEntity, ProfileRoleDTO>() {
		
		public boolean isEqual(PartyProfileRolesEntity currentObj,
				ProfileRoleDTO updatedObj) {
			if (currentObj == null || updatedObj == null) {
				return false;
			}
			return currentObj.getRoleEntity().getOid().equals(
					updatedObj.getProfileRoleID());
		}
		public boolean isNew(ProfileRoleDTO object) {
			return object.getRoleLinkOid() == null;
		}
	};
	
	public ProfileManagement() {
//		logger.setLevel(Level.DEBUG);
	}
	
	// ==============================================================================================
	// UserProfileDTO
	// ==============================================================================================	
	/**
	 * Search for users starting with the given text
	 * 
	 * @return List a list of User Profiles
	 */
	public List<PartyProfileFLO> findUserFastLaneWithUacfStartingWith(String startsWith) {
		startsWith = startsWith.toUpperCase()+ "%";
		
		return partyProfileEntityManager.findFastlanePartyProfileWithUacfIDLike(startsWith);
	}
	
	/**
	 * Search for users starting with the given text.  Object that is returned also 
	 * includes user firstName and lastName.
	 * 
	 * @return list of PartyProfileNameFLO objects
	 */
	public List<PartyProfileNameFLO> findUserAndNameFastLaneWithUacfStartingWith(String startsWith) {
		startsWith = startsWith.toUpperCase()+ "%";
		return partyProfileEntityManager.findFastlanePartyProfileNameWithUacfIDLike(startsWith);
	}
	
	/**
	 * Search for a user with a given partyOid, object that is returned also 
	 * includes user firstName and lastName.
	 * 
	 * @return a PartyProfileNameFLO objects
	 */
	public PartyProfileNameFLO findUserAndNameFastLaneWithPartyOid(long partyOid) throws DataNotFoundException {

		ResultPartyDTO partyDto = partyManagementBean.findPartyWithObjectOid(
				partyOid);
		PartyProfileNameFLO flo = new PartyProfileNameFLO();
		flo.setFirstName(partyDto.getFirstName());
		flo.setLastName(partyDto.getLastName());
		flo.setUacfId(partyDto.getUacfID());
		flo.setPartyOid(partyOid);
		flo.setOid(partyDto.getOid());
		return flo;
	}
	
	/**
	 * Find a user with its primary key and retrieves the relevant linked
	 * links like (Roles, MenuItems, Rules, RequestActions). 
	 * 
	 * @param primaryKey
	 * @return
	 * @throws DataNotFoundException 
	 */
	public PartyProfileDTO findUserForUpdate(long primaryKey) throws DataNotFoundException {
		
		PartyProfileEntity partyProfileEntity = 
			partyProfileEntityManager.findPartyProfile(primaryKey, 
					DEFAULT_PROFILE_FETCH_TYPES_FOR_UPDATE);
		return convertUserProfileDtoForUpdate(partyProfileEntity);
	}
	
	/**
	 * Find a user with its partyOID
	 * 
	 * @param partyOID
	 * @return
	 */
	public PartyProfileDTO findUserForUpdateByPartyOID(long partyOid) 
		throws DataNotFoundException {
		PartyProfileEntity e = partyProfileEntityManager.findPartyProfileWithPartyOid(
				partyOid, DEFAULT_PROFILE_FETCH_TYPES_FOR_UPDATE);
		return convertUserProfileDtoForUpdate(e);
	}	

	/**
	 * Find a user with its partyOID
	 * 
	 * @param partyOID
	 * @return
	 */
	public PartyProfileDTO findUserForUpdateByUacfID(String uacfId) 
		throws DataNotFoundException {
		PartyProfileEntity e = partyProfileEntityManager.findPartyProfileWithUacfID(uacfId,
				DEFAULT_PROFILE_FETCH_TYPES_FOR_UPDATE);
		return convertUserProfileDtoForUpdate(e);
	}	
	
	/**
	 * Convert a PartyProfile entity to a DTO
	 * 
	 * @param partyProfileEntity
	 * @return
	 * @throws CommunicationException
	 * @throws DataNotFoundException
	 */
	private PartyProfileDTO convertUserProfileDtoForUpdate(PartyProfileEntity partyProfileEntity) throws CommunicationException, DataNotFoundException {
		UserProfileConverter converter = new UserProfileConverter();
		
		// Convert values
		PartyProfileDTO profileDto = converter.toDTO(partyProfileEntity); 
		
		profileDto.setMenuItemList(menuItemConverter.convertMenuItemListToDto(
				partyProfileEntity.getMenuitemList()));
		profileDto.setRoleItemList(roleLinkConverter.convertRoleListToDto(
				partyProfileEntity.getRoleList()));
		profileDto.setRunnableRuleList(ruleLinkConverter.convertRuleListToDto(
				partyProfileEntity.getRuleList()));
		profileDto.setAllowableRequestActionList(requestActionConverter.convertRequestActionListToDTO(
				partyProfileEntity.getRequestActionList()));
		
		// Retrieve uacfID
		ResultPartyDTO partyDto = partyManagementBean.findPartyWithObjectOid(
				partyProfileEntity.getPartyOid());
		profileDto.setSecurityID(partyDto.getUacfID());
		
		return profileDto;
	}
	
	/**
	 * <p>Persist the changes made to a user</p>
	 * 
	 * <p><b>Note:</b>Explicit agreements do not get updated here, please refer to 
	 * {@linkplain AgreementPrivilegeManagement}</p>
	 * 
	 * @param dto
	 * @param updatedBy	PartyOid 
	 * @return Updated Role
	 */
	public PartyProfileDTO updateUser(PartyProfileDTO dto, String updatedBy) {
		
		// Retrieve the old entity, ensure it's detached or the 
		// updates will persist immediately.
		PartyProfileEntity entity;
		try {
			entity = partyProfileEntityManager.findPartyProfile(dto.getProfileOID(), 
					DEFAULT_PROFILE_FETCH_TYPES_FOR_UPDATE);
		} catch (DataNotFoundException e) {
			// Attempt to update a record that does not exist.
			// TODO jzb0608 - throw a proper business exception????
			throw new RuntimeException(e);
		}
		entity = partyProfileEntityManager.detachPartyProfile(entity);
		
		/* Menu items */
		if (dto.getMenuItemList()!=null) {
			entity.setMenuitemList(convertMenuItemList(dto.getMenuItemList(), entity.getMenuitemList(), 
					PartyProfileMenuItemsEntity.class));
		} else {
			// Null lists are not updated.
			entity.setMenuitemList(null);
		}
		
		/* Role items */
		if (dto.getRoleItemList()!=null) {
			//entity.setRoleList(convertRoleList(dto.getRunnableRuleList(), entity.getRoleList(), RoleRulesEntity.class));
			entity.setRoleList(convertRoleList(dto.getRoleItemList(), entity.getRoleList()));

		} else {
			// Null lists are not updated.
			entity.setRoleList(null);
		}

		// Explicit agreements do not get updated here, please refer to AgreementPrivilegeManagement
		entity.setExplicitAgreementList(null);
		
		/* Request Actions */
		if (dto.getAllowableRequestActionList()!=null) {
			entity.setRequestActionList(convertRequestActionList(
					dto.getAllowableRequestActionList(), 
					entity.getRequestActionList(),
					PartyProfileRequestActionsEntity.class));
		} else {
			entity.setRequestActionList(null);
		}
		
		/* Rule list */
		if (dto.getRunnableRuleList()!=null) {
			entity.setRuleList(convertRuleList(dto.getRunnableRuleList(), entity.getRuleList()));
		} else {
			entity.setRuleList(null);
		}
		
		// Update
		partyProfileEntityManager.updatePartyProfile(entity, updatedBy);
		
		return dto;
	}

	
	/**
	 * Convert the runnableRuleDTO list into an entity list that can
	 * be used for update.
	 * 
	 * @param updatedList
	 * @param currentList
	 * @return
	 */
	private List<PartyProfileRulesEntity> convertRuleList(List<RunnableRuleDTO> updatedList, 
			List<PartyProfileRulesEntity> currentList) {
		
		/* Match the lists */
		MatchListResultObject<AbstractRuleLinkEntity, RunnableRuleDTO> resultObj = CompareUtil
				.getInstance().matchDiffLists(
						currentList, 
						updatedList,
						ruleComparator, 
						AbstractRuleLinkEntity.class,
						RunnableRuleDTO.class);
		
		// Add added items
		if (logger.isDebugEnabled())
			logger.debug("\n\nAdded items");
		for (RunnableRuleDTO o : resultObj.getAddedCollection()) {
			PartyProfileRulesEntity link = new PartyProfileRulesEntity();
			ruleLinkConverter.mergeToEntity(o, link);
			try {
				link.setRuleEntity(ruleEntityManager.findRule(o.getRuleID()));
			} catch (DataNotFoundException e) {
				throw new RuntimeException(
						"Expected rule was not found for id = "+o.getRuleID(),e);
			}
			currentList.add(link);
			if (logger.isDebugEnabled())
				logger.debug("  -- added = " + o);
		}

		// Ignore removed items
		if (logger.isDebugEnabled())
			logger.debug("\n\nRemoved items");
		for (AbstractRuleLinkEntity o : resultObj.getRemoveCollection()) {
			if (logger.isDebugEnabled())
				logger.debug("  -- removed = "+ o);
			currentList.remove(o);
		}

		// Update matched items
		if (logger.isDebugEnabled())
			logger.debug("\n\nMatched items");
		for (MatchedObject<AbstractRuleLinkEntity, RunnableRuleDTO> o : resultObj
				.getMatchedObjectCollection()) {
			PartyProfileRulesEntity link = (PartyProfileRulesEntity) o.getCurrentObject();
			ruleLinkConverter.mergeToEntity((RunnableRuleDTO) o.getUpdatedObject(), link);
			if (logger.isDebugEnabled())
				logger.debug("  -- matched = "+ o.getUpdatedObject());
		}
		return currentList;
		
	}

	/**
	 * Convert the Request Action DTO list to an entity list that 
	 * can be used for an update.
	 *  
	 * @param requestActionList
	 * @param requestActionList2
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private <T extends AbstractRequestActionsEntity> List<T> 
		convertRequestActionList(
			List<AllowableRequestActionDTO> updatedList, 
			List<T> currentList, 
			Class<T> type) {
		
		/* Match the lists */
		MatchListResultObject<AbstractRequestActionsEntity, AllowableRequestActionDTO> resultObj = 
			CompareUtil.getInstance().matchDiffLists(currentList, updatedList,
						requestActionsComparator, AbstractRequestActionsEntity.class,
						AllowableRequestActionDTO.class);
		
		// Add added items
		if (logger.isDebugEnabled())
			logger.debug("\n\nAdded items");
		for (AllowableRequestActionDTO o : resultObj.getAddedCollection()) {
			T link;
			try {
				link = type.newInstance();
			} catch (IllegalAccessException e) {
				throw new RuntimeException("Unable to instantiate type "+type.getName(),e);
			} catch (InstantiationException e) {
				throw new RuntimeException("Unable to instantiate type "+type.getName(),e);
			}
			requestActionConverter.mergeToEntity(o, link);
			currentList.add(link);
			if (logger.isDebugEnabled())
				logger.debug("  -- added = " + o);
		}

		// Ignore removed items
		if (logger.isDebugEnabled())
			logger.debug("\n\nRemoved items");
		for (AbstractRequestActionsEntity o : resultObj.getRemoveCollection()) {
			if (logger.isDebugEnabled())
				logger.debug("  -- removed = "+o);
			currentList.remove(o);
		}

		// Update matched items
		if (logger.isDebugEnabled())
			logger.debug("\n\nMatched items");
		for (MatchedObject<AbstractRequestActionsEntity, AllowableRequestActionDTO> o : resultObj
				.getMatchedObjectCollection()) {
			T link = (T) o.getCurrentObject();
			requestActionConverter.mergeToEntity((AllowableRequestActionDTO) o.getUpdatedObject(), link);
			if (logger.isDebugEnabled())
				logger.debug("  -- matched = " +  o.getUpdatedObject());
		}
		return currentList;
	}

	/**
	 * Convert the Role DTO list to an entity list that
	 * can be used for update.
	 * 
	 * @param updatedList
	 * @param currentList
	 * @return
	 */
	private List<PartyProfileRolesEntity> convertRoleList(
			List<ProfileRoleDTO> updatedList,
			List<PartyProfileRolesEntity> currentList) {

		/* Match the lists */
		MatchListResultObject<PartyProfileRolesEntity, ProfileRoleDTO> resultObj = CompareUtil
				.getInstance().matchDiffListsWithNewCheck(currentList, updatedList,
						roleComparator, PartyProfileRolesEntity.class,
						ProfileRoleDTO.class);
	
		// Add added items
		if (logger.isDebugEnabled())
			logger.debug("\n\nAdding items");
		for (ProfileRoleDTO o : resultObj.getAddedCollection()) {
			PartyProfileRolesEntity link = new PartyProfileRolesEntity();
		
			try {
				link.setRoleEntity(roleEntityManager.findRole(o.getProfileRoleID()));
			} catch (DataNotFoundException e) {
				throw new RuntimeException(
						"Expected role was not found for id = "+o.getProfileRoleID(),	e);
			}
			currentList.add(link);
			if (logger.isDebugEnabled())
				logger.debug("  -- added role = " + o.getRoleName());
		}

		// Ignore removed items
		if (logger.isDebugEnabled())
			logger.debug("\n\nRemoved items");
		for (PartyProfileRolesEntity o : resultObj.getRemoveCollection()) {
			if (logger.isDebugEnabled())
				logger.debug("  -- removed = "
					+ o.getRoleEntity().getName());
			currentList.remove(o);
		}

		// Update matched items
		if (logger.isDebugEnabled())
			logger.debug("\n\nMatched items");
		for (MatchedObject<PartyProfileRolesEntity, ProfileRoleDTO> o : resultObj
				.getMatchedObjectCollection()) {
			PartyProfileRolesEntity link = (PartyProfileRolesEntity) o.getCurrentObject();
			if (logger.isDebugEnabled())
				logger.debug("  -- matched role = "
					+ link.getRoleEntity().getName());
		}
		return currentList;
		
	}

	
	/**
	 * Convert the menuItem DTO list into an entity list that can
	 * be used for update.
	 * 
	 * @param <C> Child type
	 * @param updatedList
	 * @param currentList
	 * @param childType<C>
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private <C extends AbstractMenuLinkEntity> List<C> convertMenuItemList(
			List<MenuItemDTO> updatedList,
			List<C> currentList,
			Class<C> childType) {
		
		/* Match the lists */
		MatchListResultObject<AbstractMenuLinkEntity, MenuItemDTO> resultObj = CompareUtil
				.getInstance().matchDiffListsWithNewCheck(currentList, updatedList,
						menuItemComparator, AbstractMenuLinkEntity.class,
						MenuItemDTO.class);
		
		// Add added items
		if (logger.isDebugEnabled())
			logger.debug("\n\nAdded items");
		for (MenuItemDTO o : resultObj.getAddedCollection()) {
			C link;
			try {
				link = childType.newInstance();
			} catch (IllegalAccessException e) {
				throw new RuntimeException("Unable to instantiate type "+childType.getName(),e);
			} catch (InstantiationException e) {
				throw new RuntimeException("Unable to instantiate type "+childType.getName(),e);
			}
			mergeToMenuLink(o, link);
			try {
				link.setMenuItemEntity(menuItemEntityManager.findMenuItem(o.getMenuItemID()));
			} catch (DataNotFoundException e) {
				throw new RuntimeException(
						"Expected menu item was not found for id = "+o.getMenuItemID(),	e);
			}
			currentList.add(link);
			if (logger.isDebugEnabled())
				logger.debug("  -- added = " + o.getMenuItemDescription());
		}

		// Ignore removed items
		if (logger.isDebugEnabled())
			logger.debug("\n\nRemoved items");
		for (AbstractMenuLinkEntity o : resultObj.getRemoveCollection()) {
			if (logger.isDebugEnabled())
				logger.debug("  -- removed = "
					+ o.getMenuItemEntity().getDescription());
			currentList.remove(o);
		}

		// Update matched items
		if (logger.isDebugEnabled())
			logger.debug("\n\nMatched items");
		for (MatchedObject<AbstractMenuLinkEntity, MenuItemDTO> o : resultObj
				.getMatchedObjectCollection()) {
			C link = (C) o.getCurrentObject();
			mergeToMenuLink(((MenuItemDTO) o.getUpdatedObject()), link);
			if (logger.isDebugEnabled())
				logger.debug("  -- matched = "
					+ ((MenuItemDTO) o.getUpdatedObject())
							.getMenuItemDescription());
		}
		
		return currentList;
	}
	
	/**
	 * Convert the role rules DTO list into an entity list that can
	 * be used for update.
	 * @param <C>
	 * @param updatedList
	 * @param currentList
	 * @param childType
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private <C extends AbstractRuleLinkEntity> List<C> convertRoleRulesList(
			List<RunnableRuleDTO> updatedList,
			List<C> currentList,
			Class<C> childType) {
		
		/* Match the lists */
		MatchListResultObject<AbstractRuleLinkEntity, RunnableRuleDTO> resultObj = CompareUtil
				.getInstance().matchDiffListsWithNewCheck(currentList, updatedList,
						//ruleItemComparator, AbstractRuleLinkEntity.class,
						ruleComparator, AbstractRuleLinkEntity.class,
						RunnableRuleDTO.class);
		
		// Add added items
		if (logger.isDebugEnabled())
			logger.debug("\n\nAdded items");
		for (RunnableRuleDTO o : resultObj.getAddedCollection()) {
			C link;
			try {
				link = childType.newInstance();
			} catch (IllegalAccessException e) {
				throw new RuntimeException("Unable to instantiate type "+childType.getName(),e);
			} catch (InstantiationException e) {
				throw new RuntimeException("Unable to instantiate type "+childType.getName(),e);
			}
			ruleLinkConverter.mergeToEntity(o, link);
			
			try {
				link.setRuleEntity(ruleEntityManager.findRule(o.getRuleID()));            //menuItemEntityManager.findMenuItem(o.getMenuItemID()));
			} catch (DataNotFoundException e) {
				throw new RuntimeException(
						"Expected menu item was not found for id = "+o.getRuleID(),	e);
			}
			currentList.add(link);
			if (logger.isDebugEnabled())
				logger.debug("  -- added = " + o.getRuleName());
		}

		// Ignore removed items
		if (logger.isDebugEnabled())
			logger.debug("\n\nRemoved items");
		for (AbstractRuleLinkEntity o : resultObj.getRemoveCollection()) {
			if (logger.isDebugEnabled())
				logger.debug("  -- removed = "
					+ o.getRuleEntity().getDescription());
			currentList.remove(o);
		}

		// Update matched items
		if (logger.isDebugEnabled())
			logger.debug("\n\nMatched items");
		for (MatchedObject<AbstractRuleLinkEntity, RunnableRuleDTO> o : resultObj
				.getMatchedObjectCollection()) {
			C link = (C) o.getCurrentObject();
			ruleLinkConverter.mergeToEntity((RunnableRuleDTO) o.getUpdatedObject(), link);
			if (logger.isDebugEnabled())
				logger.debug("  -- matched = "
					+ ((RuleDTO) o.getUpdatedObject())
							.getRuleDescription());
		}
		
		return currentList;
	}
	
	
	/**
	 * Copy values from a MenuItemDTO into the existing RolesMenuitems entity
	 * 
	 * @param dto
	 * @param roleMenuItem
	 */
	private void mergeToMenuLink(MenuItemDTO dto,
			AbstractMenuLinkEntity menuItem) {
		menuItem.setAddAccess(dto.isAddAccess());
		menuItem.setModifyAccess(dto.isModifyAccess());
		menuItem.setDelAccess(dto.isDeleteAccess());
	}
		
	// ==============================================================================================
	// Menu Items
	// ==============================================================================================
	/**
	 * Retrieve a list of all menu items that are available for selection
	 * 
	 * @return
	 */
	public List<MenuItemDTO> findAllAvailableMenuItems() {
		return findAllMenuItems();
	}
	/**
	 * Retrieve all the menu items
	 * 
	 * @return List of all menu items
	 */
	public List<MenuItemDTO> findAllMenuItems() {
		//List<MenuItemEntity> list = menuItemEntityManager.findAllMenuItems();
		List<MenuItemDTO> returnList = new ArrayList<MenuItemDTO>();

		//for (MenuItemEntity e : list) {
		//	returnList.add(menuItemConverter.toDTO(e));
		//}
		
		//MSK:Change commented abvoe lines
		System.out.println("MSK#Change--------ProfileManagement:findAllMenuItems()--------");
		MenuItemDTO dto=new MenuItemDTO();
		dto.setEnabled(true);
		dto.setImplClazz("za.co.liberty.web.pages.party.BasePartyDetailsPanel");
		dto.setMenuItemDescription("Person/Organisation Details");
		dto.setMenuItemLongDescription("Person/Organisation Details");
		dto.setMenuItemID(62l);
		dto.setMenuItemName("MAINTAIN_PARTY_PERSON_OR_ORGANISATION_DETAILS_PANEL");
		
		dto.setAddAccess(false);
		dto.setDeleteAccess(false);
		dto.setModifyAccess(true);
		dto.setPanel(true);
		dto.setTimeCreated(new Timestamp(System.currentTimeMillis()));
		dto.setTimeModified(new Timestamp(System.currentTimeMillis()));
		
		returnList.add(dto);
		
		
		
		
		return returnList;
	}

	/**
	 * Find a menu item with its primary key
	 * 
	 * @param primaryKey
	 * @return
	 * @throws DataNotFoundException 
	 */
	public MenuItemDTO findMenuItem(long primaryKey) throws DataNotFoundException {
		return
		menuItemConverter.toDTO(
					menuItemEntityManager.findMenuItem(primaryKey));
	}

	/**
	 * Create a menu item and persist.
	 * 
	 * @param dto
	 *            New menu item to store
	 * @param createdBy
	 * 			PartyOid
	 * @return The newly created Menu item
	 */
	public MenuItemDTO createMenuItem(MenuItemDTO dto, String createdBy) {
		MenuItemEntity entity = menuItemConverter.toEntity(dto);
		menuItemEntityManager.addMenuItem(entity, createdBy);
		return menuItemConverter.toDTO(entity);
	}

	/**
	 * Persist the changes made to menu item
	 * 
	 * @param dto
	 * @return Updated Menu Item
	 */
	public MenuItemDTO updateMenuItem(MenuItemDTO dto) {
		MenuItemEntity entity = menuItemConverter.toEntity(dto);
		menuItemEntityManager.updateMenuItem(entity);
		return menuItemConverter.toDTO(entity);
	}
	
	/**
	 * Add MenuItemDTO to all userProfileDTO ProfileRoles
	 * 
	 * TODO jzb0608 Look into optimising this
	 * 
	 * @param userProfileDTO
	 */
	public void populateMenuItemsforRole(PartyProfileDTO userProfileDTO) {
		List<ProfileRoleDTO> profileRoles = userProfileDTO
				.getRoleItemList();
		if (profileRoles != null) {
			for (ProfileRoleDTO role : profileRoles) {
				ProfileRoleDTO newRoleDTO;
				try {
					newRoleDTO = this.findRole(role
							.getProfileRoleID());
				} catch (DataNotFoundException e) {
					logger.warn("Unable to retrieve role with oid "
							+role.getProfileRoleID());
					continue;
				}
				role.setDefaultMenuItemList(newRoleDTO
								.getDefaultMenuItemList());
				if (role.getDefaultMenuItemList() != null) {
					for (MenuItemDTO menu : role.getDefaultMenuItemList()) {
						menu.setProfileRoleDTO(role);
						menu.setUserProfileDTO(userProfileDTO);
					}
				}
			}
		}
		// also set the userProfileDTO in all the rules
		if (userProfileDTO.getRunnableRuleList() != null) {
			for (MenuItemDTO menu : userProfileDTO.getMenuItemList()) {
				menu.setUserProfileDTO(userProfileDTO);
			}
		}
	}

	
	// ==============================================================================================
	// Rules
	// ==============================================================================================
	/**
	 * Retrieve a list of all rules that are available for selection
	 * 
	 * @return
	 */
	public List<RuleDTO> findAllAvailableRules() {
		List<RuleDTO> ruleList = findAllRules();
		//Remove rules that have not been configured yet.
		for (int i = 0; i < ruleList.size();++i) {
			RuleDTO dto = ruleList.get(i);
			if (RuleType.getRuleTypeWithDatabaseName(dto.getRuleName()) == null) {
				logger.warn("Database rule \""+dto.getRuleName()
						+"\" does not have a corresponding RuleType enum");
				ruleList.remove(i);
				--i;
			}
		}
		return ruleList;
	}

	/**
	 * Retrieve all the rules
	 * 
	 * @return List of all menu items
	 */
	public List<RuleDTO> findAllRules() {
		List<RuleEntity> list = ruleEntityManager.findAllRules();
		List<RuleDTO> returnList = new ArrayList<RuleDTO>();

		RuleConverter converter = new RuleConverter();
		for (RuleEntity e : list) {
			returnList.add(converter.toDTO(e));
		}
		
		return returnList;
	}

	/**
	 * Retrieve all the rule data type objects
	 * 
	 * @return
	 */
	public List<RuleDataType> findAllRuleDataTypes() {
		List<RuleDataType> list = new ArrayList<RuleDataType>();
		for (RuleDataType t : RuleDataType.values()) {
			list.add(t);
		}
		return list;
	}

	/**
	 * Find a rule with its primary key
	 * 
	 * @param primaryKey
	 * @return
	 * @throws DataNotFoundException 
	 */
	public RuleDTO findRule(long primaryKey) throws DataNotFoundException {
		return new RuleConverter().toDTO(
				ruleEntityManager.findRule(primaryKey));
	}

	/**
	 * Create a menu item
	 * 
	 * @param dto
	 *            New rule to store
	 * @param createdBy
	 * 			PartyOid            
	 * @return The newly created rule
	 */
	public RuleDTO createRule(RuleDTO dto, String createdBy) {
		RuleConverter converter = new RuleConverter();
		RuleEntity entity = converter.toEntity(dto);
		ruleEntityManager.addRule(entity, createdBy);
		return converter.toDTO(entity);
	}

	/**
	 * Persist the changes made to a rule
	 * 
	 * @param dto
	 * @return Updated Rule
	 */
	public RuleDTO updateRule(RuleDTO dto) {
		RuleConverter converter = new RuleConverter();
		RuleEntity entity = converter.toEntity(dto);
		ruleEntityManager.updateRule(entity);
		return converter.toDTO(entity);
	}
	
	/**
	 * Add Rule items that are held on the role for the given
	 * user
	 * 
	 * @param userProfileDTO
	 */
	public void populateRuleItemsforRole(PartyProfileDTO userProfileDTO) {
		List<ProfileRoleDTO> profileRoles = userProfileDTO.getRoleItemList();
		if (profileRoles != null) {
			for (ProfileRoleDTO role : profileRoles) {
				ProfileRoleDTO newRoleDTO=null;
				try {
					newRoleDTO = this.findRole(role
							.getProfileRoleID());
				} catch (DataNotFoundException e) {
					logger.warn("Unable to retrieve role with oid "
							+role.getProfileRoleID());
					continue;
				}
				role.setRunnableRuleList(newRoleDTO.getRunnableRuleList());
				if (role.getRunnableRuleList() != null) {
					for (RunnableRuleDTO rule : role.getRunnableRuleList()) {
						rule.setProfileRoleDTO(role);
						rule.setUserProfileDTO(userProfileDTO);
					}
				}
			}
		}
		// also set the userProfileDTO in all the rules
		if (userProfileDTO.getRunnableRuleList() != null) {
			for (RunnableRuleDTO rule : userProfileDTO.getRunnableRuleList()) {
				rule.setUserProfileDTO(userProfileDTO);
			}
		}
	}

	// ==============================================================================================
	// Roles
	// ==============================================================================================
	/**
	 * Retrieve a list of all roles that are available for selection
	 * 
	 * @return
	 */
	public List<ProfileRoleDTO> findAllAvailableRoles() {
		return findAllRoles();
	}

	/**
	 * Retrieve all the roles
	 * 
	 * @return List of all roles
	 */
	public List<ProfileRoleDTO> findAllRoles() {
		RoleConverter converter = new RoleConverter();
		List<RoleEntity> roleList = roleEntityManager.findAllRoles();
		List<ProfileRoleDTO> resultList = new ArrayList<ProfileRoleDTO>();
		
		for (RoleEntity e : roleList) {
			resultList.add(converter.toDTO(e));
		}

		return resultList;
	}

	/**
	 * Find a role with its primary key. More expensive than a find as the
	 * mappings are also retrieved.
	 * 
	 * @param primaryKey
	 * @return
	 * @throws DataNotFoundException 
	 */
	public ProfileRoleDTO findRole(long primaryKey) throws DataNotFoundException {
		
		RoleEntity roleEntity = 
			roleEntityManager.findRole(primaryKey, DEFAULT_ROLE_FETCH_TYPES_FOR_UPDATE);
		
		RoleConverter converter = new RoleConverter();
		
		// Convert values
		ProfileRoleDTO roleDto = converter.toDTO(roleEntity); 
		roleDto.setDefaultMenuItemList((ArrayList<MenuItemDTO>)
				new MenuItemConverter().convertMenuItemListToDto(roleEntity.getMenuitemList()));
		roleDto.setRunnableRuleList(ruleLinkConverter.convertRuleListToDto(
				roleEntity.getRuleList()));
		roleDto.setAllowableRequestActionList(new RequestActionLinkConverter()
				.convertRequestActionListToDTO(roleEntity.getRequestActionList()));
		
		return roleDto;
	}

	/**
	 * Create a menu item
	 * 
	 * @param dto
	 *            New role to store
	 * @param updatedBy
	 * 			PartyOid            
	 * @return The newly created rule
	 */
	public ProfileRoleDTO createRole(ProfileRoleDTO dto, String createdBy) {
		//TODO jzb0608 Complete this code
		throw new UnsupportedOperationException("Still busy");
//		return entityFacade.createRole(dto);
	}

	/**
	 * Persist the changes made to a role.
	 * 
	 * @param dto
	 * @param updatedBy
	 * 			PartyOid 
	 * @return Updated Role
	 */
	public ProfileRoleDTO updateRole(ProfileRoleDTO dto, String updatedBy) {
		// Retrieve the old entity, ensure it's detached or the 
		// updates will persist immediately.
		RoleEntity entity;
		try {
			entity = roleEntityManager.findRole(dto.getProfileRoleID(), DEFAULT_ROLE_FETCH_TYPES_FOR_UPDATE);
		} catch (DataNotFoundException e) {
			// Attempt to update a record that does not exist.
			// TODO jzb0608 - throw a proper business exception????
			throw new RuntimeException(e);
		}
		
		entity = roleEntityManager.detach(entity);
	
		/* Menu items */
		if (dto.getDefaultMenuItemList()!=null) {
			entity.setMenuitemList(convertMenuItemList(dto.getDefaultMenuItemList(), 
					entity.getMenuitemList(), RoleMenuItemsEntity.class));
		} else {
			// Null lists are not updated.
			entity.setMenuitemList(null);
		}
		
		/* Request Actions */
		if (dto.getAllowableRequestActionList()!=null) {
			entity.setRequestActionList(convertRequestActionList(
					dto.getAllowableRequestActionList(), 
					entity.getRequestActionList(),
					RoleRequestActionsEntity.class));
		} else {
			entity.setRequestActionList(null);
		}
		
		/* Role rules */
		 if (dto.getRunnableRuleList()!=null) {
			entity.setRuleList(convertRoleRulesList(
					dto.getRunnableRuleList(), 
					entity.getRuleList(),
					RoleRulesEntity.class));
		} else {
			entity.setRuleList(null);
		}
		
		
		roleEntityManager.updateRole(entity, updatedBy);
		return null;
	}
	
	/**
	 * Gets the ProfileRoleDTOs for a given partyid
	 * 
	 * @param partyOid
	 * @return
	 */
	public List<ProfileRoleDTO> getProfileRoleDTOsForParty(long partyOid)
			throws DataNotFoundException {
		PartyProfileEntity entity = partyProfileEntityManager.findPartyProfileWithPartyOid(
				partyOid, PartyProfileFetchType.ROLES);		
		return roleLinkConverter.convertRoleListToDto(entity.getRoleList());
	}

	/**
	 * checks if the given role is linked to any roles or profiles
	 * 
	 * @param ruleID
	 * @return
	 */
	public boolean ruleIsLinkedToRolesOrProfiles(long ruleID) {
		//TODO jzb0608 Perhaps this should move to the persistence layer.
		String query = QueryHelper.getBusinessLayerQuery("Party",
				"CHECK_NUMBER_LINKS_FOR_RULE").getQuery();
		Connection connection;
		PreparedStatement preparedStatement = null;
		ResultSet rs = null;
		try {
			connection = partyDatasource.getConnection();
			if (connection == null) {
				throw new CommunicationException("Connection not found");
			}
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setLong(1, ruleID);
			preparedStatement.setLong(2, ruleID);
			rs = preparedStatement.executeQuery();
			if (rs.next()) {
				int num = rs.getInt("num");
				if (num > 0) {
					return true;
				} else {
					return false;
				}
			}
		} catch (SQLException e1) {
			throw new CommunicationException(e1);
		} finally {
			try {
				if (preparedStatement != null) {
					preparedStatement.close();
				}
				if (rs != null) {
					rs.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		throw new CommunicationException("Problem accessing the DB");
	}

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
			DataNotFoundException {

		if (logger.isDebugEnabled())
			logger.debug("updatePartyProfile  partyOid="+partyVo.getObjectOid()
					+ " ,oldUacfID="+oldUacfID);
		
		boolean isUacfChanged = false;
		boolean isUacfRemoved = false;
		boolean update = true;

		if (!(partyVo instanceof PersonVO)) {
			// We only care about real people
			return;
		}

		// Some uacfId processing
		PersonVO personVO = (PersonVO) partyVo;
		String newUacfID = personVO.getExternalReference();
		newUacfID = (newUacfID != null) ? newUacfID.toUpperCase() : newUacfID;

		boolean isNewUacfDefined = (newUacfID != null && !newUacfID.equals(""));
		boolean isOldUacfDefined = (oldUacfID != null && !oldUacfID.equals(""));
		if ((isOldUacfDefined && !isNewUacfDefined)
				|| (isNewUacfDefined && !isOldUacfDefined)
				|| (isNewUacfDefined && isOldUacfDefined && !newUacfID
						.equals(oldUacfID))) {
			// uacfid changed
			isUacfChanged = true;
		}
		if (partyVo.getEffectiveTo() != null
				&& DateUtil.getInstance().compareDatePart(partyVo.getEffectiveTo(),
						DateUtil.getInstance().getTodayDatePart()) <= 0
				|| (isUacfChanged && (newUacfID == null || newUacfID.equals("")))) {
			// party or uacf has been removed
			isUacfRemoved = true;
		}
		if (!isUacfChanged && !isUacfRemoved) {
			// Stop processing, no change.
			if (logger.isDebugEnabled())
				logger.debug("No change to uacfID, stop profile update");
			return;
		}

		// Confirm this is a valid uacfId
		if (isNewUacfDefined) {
			if (logger.isDebugEnabled())
				logger.debug("New uacfId defined \""+newUacfID+"\" so check TAM");
			// InvalidUserIdException is thrown if not found
			securityManagement.userExistsOnTam(newUacfID);
		}
		
		PartyProfileDTO profileEntity = new PartyProfileDTO();
		try {
			profileEntity = findUserForUpdateByPartyOID(partyVo.getObjectOid());
		} catch (DataNotFoundException e) {		
			if (isUacfRemoved) {
				// No profile was found, so no need to remove.
				if (logger.isDebugEnabled())
					logger.debug("No profile found for partyOid " + partyVo.getObjectOid()
							+ " so no need to remove profile items");
				return;
			}
			
			if (logger.isDebugEnabled())
				logger.debug("No profile found for partyOid " + partyVo.getObjectOid()
						+ ", create a new profile.");
			
			// therefore we store a new one
			PartyProfileEntity entity = new PartyProfileEntity();
			entity.setPartyOid(partyVo.getObjectOid());
			entity.setRoleList(new ArrayList<PartyProfileRolesEntity>());
			entity.setCreatedBy(createdBy);
			entity.setEnabled(true);
			
			partyProfileEntityManager.persistPartyProfile(entity, createdBy);
			PartyProfileFLO flo = new PartyProfileFLO();
			flo.setOid(entity.getOid());
			flo.setPartyOid(entity.getPartyOid());
			flo.setUacfId(newUacfID);
			securityManagement.syncPartyProfileToTam(flo);
			return;
		}
		
		if (isUacfRemoved) {
			if (logger.isDebugEnabled())
				logger.debug("Remove profile items for partyOid " + partyVo.getObjectOid());
			// Reset all linked stuff
			profileEntity.getMenuItemList().clear();
			profileEntity.getAllowableRequestActionList().clear();
			profileEntity.getRoleItemList().clear();
			profileEntity.getRunnableRuleList().clear();

			updateUser(profileEntity, createdBy);
		} else if (update) {
			if (logger.isDebugEnabled())
				logger.debug("Update profile items for partyOid " + partyVo.getObjectOid());
			profileEntity.setSecurityID(newUacfID);
			PartyProfileFLO flo = new PartyProfileFLO();
			flo.setOid(profileEntity.getProfileOID());
			flo.setPartyOid(profileEntity.getPartyOID());
			flo.setUacfId(newUacfID);
			securityManagement.syncPartyProfileToTam(flo);
		}

		agreementPrivilegeManagement.updateExplicitAgreements(new ArrayList<ExplicitAgreementDTO>(), 
				profileEntity.getPartyOID(), createdBy);
	}
}
