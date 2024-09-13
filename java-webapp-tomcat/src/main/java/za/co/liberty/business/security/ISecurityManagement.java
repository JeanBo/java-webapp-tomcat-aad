package za.co.liberty.business.security;

import java.util.List;
import java.util.Map;

import javax.ejb.Local;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import za.co.liberty.dto.security.SecurityPageActionDTO;
import za.co.liberty.dto.userprofiles.ContextDTO;
import za.co.liberty.dto.userprofiles.ISessionUser;
import za.co.liberty.dto.userprofiles.ISessionUserProfile;
import za.co.liberty.dto.userprofiles.SessionUserIdDTO;
import za.co.liberty.exceptions.ErrorExceptionType;
import za.co.liberty.exceptions.SRSBusinessException;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.exceptions.security.InvalidUserIdException;
import za.co.liberty.exceptions.security.UserIdNotInSessionException;
import za.co.liberty.interfaces.rules.RuleType;
import za.co.liberty.interfaces.security.SecurityPageActionType;
import za.co.liberty.persistence.party.entity.fastlane.PartyProfileFLO;
import za.co.liberty.persistence.party.entity.userprofile.RoleEntity;

/**
 * Manages user profile objects
 * 
 */
@Local
public interface ISecurityManagement  {
	
	/**
	 * User logged on
	 * 
	 * @param user
	 */
	public void logUserLogonAction(ISessionUser user);
	
	/**
	 * User has concurrent sessions
	 * 
	 * @param sessionUser
	 */
	public void logUserConcurrentSession(ISessionUser sessionUser);
	
	/**
	 * User logged on
	 * 
	 * @param user
	 */
	public void logUserPageAction(Class pageClass, ISessionUserProfile user, SecurityPageActionType userActionType, ContextDTO contextDto);
	
	/**
	 * User logged on
	 * 
	 * @param user
	 */
	public void logUserPageAction(Class pageClass, ISessionUserProfile user, SecurityPageActionType userActionType, ContextDTO contextDto, 
			SecurityPageActionDTO pageActionDto);
	
	/**
	 * This method should only be used for testing
	 * 
	 * @param uacfId
	 * @return
	 * @throws InvalidUserIdException 
	 */
	public ISessionUserProfile getUserForTesting(String uacfId) throws InvalidUserIdException;
	
	/**
	 * Automatically logs a user in if their user id is already in the
	 * session.  Can throw the following BusinessExceptions types
	 * {@linkplain ErrorExceptionType#USER_ID_NOT_IN_SESSION}, 
	 * {@linkplain ErrorExceptionType#INVALID_USER_ID}
	 * 
	 * @param sessionId session id from httpsession
	 * 
	 * @return The session user object
	 * @throws SRSBusinessException
	 */
	public ISessionUserProfile getUserInSession(String sessionId) 
		throws InvalidUserIdException, UserIdNotInSessionException;
	
	/**
	 * Returns a SessionUserIdDTO object that is unique per uacf id.  This object
	 * holds the most recent session id
	 * 
	 * @param uacfId
	 * @param sessionId
	 * @return
	 */
	public SessionUserIdDTO getUserSessionIdForUacf(String uacfId, String sessionId);
	
	
	public boolean isUserUnauthenticated() throws UserIdNotInSessionException;
	
	
	/**
	 * Determines if this is a valid user on the system.
	 *  
	 * @param userid
	 * @return
	 */
	public boolean isUserValid(String userid);
	   
	
	/**
	 * Validate a rule on a DataObject using annotations
	 * 
	 * @param sessionUser The currently logged in user
	 * @param dataObject Data object with Rule annotations
	 * @param ruleTypes  List of rules to apply
	 * @return
	 */	
	public boolean validateRuleOnDataObject(ISessionUserProfile sessionUser, Object dataObject, 
			RuleType ... ruleTypes);
	
	/**
	 * Validate a rule on a value
	 * 
	 * @param sessionUser The currently logged in user
	 * @param dataObject Data object with Rule annotations
	 * @param ruleTypes  List of rules to apply
	 * @return
	 */
	public boolean validateRuleOnValue(ISessionUserProfile sessionUser, Object value, 
			RuleType ... ruleTypes);

	public void syncPartyProfilesAndRolesToTAM();
	
	
	/**
	 * Retrieve the application version information (specified in 
	 * EAR MANIFEST)
	 * 
	 * @return
	 */
	public String getApplicationVersion();
	
	/**
	 * Will check if a user exists on tam with the given uacfid
	 * <br/>Throws a InvalidUserIdException if the user does not exist on TAM
	 * 
	 * @param uacfID
	 * @return
	 * @throws InvalidUserIdException
	 */
	public String userExistsOnTam(String uacfID) throws InvalidUserIdException , CommunicationException;
	
	/**
	 * Will check if a user exists on tam with the given uacfid
	 * <br/>Throws a InvalidUserIdException if the user does not exist on TAM
	 * 
	 * @param uacfID
	 * @return
	 * @throws InvalidUserIdException
	 */
	public List<String> getTamRolesForUacfId(String uacfID) throws InvalidUserIdException , CommunicationException;

//	/**
//	 * Will update a user profile's roles from TAM and update the user profile if it changed
//	 * 
//	 * @param dto
//	 */		
//	public void updateUserRolesFromTam(PartyProfileDTO partyProfile, boolean removeUserAssignedRoles, boolean userProfileChanged);
	
	/**
	 * Will return true if the current user can modify the party with oid sent in otherwise false
	 * @param partyoid
	 * @param currentUser
	 * @return
	 */
	public boolean canUserModifyPartyDetails(Long partyoid, ISessionUserProfile currentUser);
	
	/**
	 * Will return true if the current user can view the party with oid sent in otherwise false
	 * @param partyoid
	 * @param currentUser
	 * @return
	 */
	public boolean canUserViewPartyDetails(Long partyoid, ISessionUserProfile currentUser);
	
	/**
	 * Will return true if the current user can modify the agreement with oid sent in otherwise false.
	 * 
	 * @param agreementNumber
	 * @param hasHomePartyOid
	 * @param currentUser
	 * @return
	 */
	public boolean canUserModifyAgreementDetails(Long agreementNumber, 
			Long hasHomePartyOid,
			ISessionUserProfile currentUser);
	
	/**
	 * Will return true if the current user can view the agreement with oid sent in otherwise false.
	 * 
	 * @param agreementNumber
	 * @param hasHomePartyOid
	 * @param currentUser
	 * @return
	 */
	public boolean canUserViewAgreementDetails(Long agreementNumber, Long hasHomePartyOid, 
			ISessionUserProfile currentUser);
	
	/**
	 * Will return true if the user is allowed to view the passed hierarchy node with party
	 * oid.
	 * @param hasHomePartyOid
	 * @param currentUser
	 * @return
	 */
	public boolean canUserViewHierarchyNode(Long hasHomePartyOid, ISessionUserProfile currentUser);
	
	/**
	 * Create a new System Tam Role
	 * 
	 * @param roleName
	 */
	@TransactionAttribute (TransactionAttributeType.REQUIRES_NEW)
	public void createAndCommitSystemRole(String roleName);
	
	/**
	 * Calls the method {@linkplain #syncPartyProfileToTam(PartyProfileFLO, Map)}.  
	 * 
	 * @param partyProfileFLO
	 * @return  true if changed
	 */
	public boolean syncPartyProfileToTam(PartyProfileFLO partyProfileFLO);
	
	/**
	 * Sync the passed partyProfile with the roles on Tam
	 * 
	 * @param partyProfileFLO
	 * @param systemRoleMap
	 * @return true if there was a change
	 */
	public boolean syncPartyProfileToTam(PartyProfileFLO partyProfileFLO, Map<String, RoleEntity> systemRoleMap);
	
	/**
	 * Wraps the call to {@linkplain #syncPartyProfileToTam(PartyProfileFLO, Map)} in a new
	 * transaction that forces a commit.
	 * 
	 * @param partyProfileFLO
	 * @param systemRoleMap
	 * @return
	 */
	@TransactionAttribute (TransactionAttributeType.REQUIRES_NEW)
	public boolean syncPartyProfileToTamAndCommit(PartyProfileFLO partyProfileFLO, 
			Map<String, RoleEntity> systemRoleMap);
	
	
	/**
	 * Get a link to the system profile which have all capabilities
	 * 
	 * @param sessionId
	 * @return
	 */
	public ISessionUserProfile getSystemProfile();
	
	/*
	 * 
	 * 
	 */
	public boolean canUserTransferMaxPoolBalance(ISessionUserProfile currentUser);

	
	

}
