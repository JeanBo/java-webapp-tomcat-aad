package za.co.liberty.security;

import java.io.Serializable;
import java.util.List;

import za.co.liberty.dto.userprofiles.ISessionUser;

/**
 * The UserSecurityProfile interface defines the security information required by the 
 * request authorisation rules engine for validate of user privileges
 *
 * @since 1.0
 * @version 1.0
 */
public interface UserSecurityProfile extends Serializable, ISessionUser {
	
	/**
	 * Constant defining the session attribute name used to identify the user's
	 * security profile within the user's HTTP session  
	 */
	public static String USER_PROFILE = "userProfile";

	/**
	 * Retrieve the profile owner's UACIF id (user login name)
	 * 
	 * @return The profile's UACIF id
	 * 
	 * @since 1.0
	 * @version 1.0
	 */
	public abstract String getUacfId();
	/**
	 * Set the UACIF id (user login name) that should be associated with the profile
	 * 
	 * @param UACIF that should be associated with the profile
	 * @throws IllegalArgumentException if the UACIF id is null
	 * 
	 * @since 1.0
	 * @version 1.0 
	 */
	public abstract void setUacfId(String uacfId);
	
	/**
	 * Retrieve the list of agreement no's of the profile for each party role 
	 * 
	 * @return List of Long, the oid's for the agreement
	 * 
	 * @since 1.1
	 * @version 1.1 
	 */
	public abstract List getOwnAgreements();
	
	/**
	 * Set the list of agreement no's of the profile for each party role
	 * 
	 * @param ownAgreements
	 * 
	 * @since 1.0
	 * @version 1.0 
	 */
	public abstract void setOwnAgreements(List ownAgreements);
	
	/**
	 * Add a agreement number to the list of own agreements
	 * associated with the profile owner
	 * 
	 * @param agreementOid
	 * 
	 * @since 1.1
	 * @version 1.1 
	 */
	public abstract void addOwnAgreement(long agreementOid);

	/**
	 * Verifies whether a agreement number is contained within the 
	 * profile's own agreements list
	 * 
	 * @param agreementOid that should be verified for existance within 
	 * the own agreements list
	 * @return true if the agreementOid is contained within the 
	 * own agreements list
	 * 
	 * @since 1.1
	 * @version 1.1 
	 */
	public abstract boolean containOwnAgreement(long agreementOid);

	/**
	 * Retrieve the list of agreement no's that the user manages
	 * Reverse navigation of ReprtsTo role type
	 * 
	 * @return List of Long, the oid's for the agreement
	 * 
	 * @since 1.1
	 * @version 1.1 
	 */
	public abstract List getAgreements();
	
	/**
	 * Set the list of agreement no's that the user manages
	 * 
	 * @param ownAgreements
	 * 
	 * @since 1.1
	 * @version 1.1 
	 */
	public abstract void setAgreements(List agreements);
	
	/**
	 * Add a agreement number to the list of agreement no's 
	 * that the user manages.
	 * 
	 * @param agreementOid
	 * 
	 * @since 1.1
	 * @version 1.1 
	 */
	public abstract void addAgreement(long agreementOid);

	/**
	 * Verifies whether a agreement number is contained within the 
	 * profile's managed by agreement list
	 * 
	 * @param agreementOid that should be verified for existance within 
	 * the agreement list
	 * @return true if the agreementOid is contained within the 
	 * agreement list
	 * 
	 * @since 1.1
	 * @version 1.1 
	 */
	public abstract boolean containAgreement(long agreementOid);

	/**
	 * Retrieve the party oid of the profile owner's party object
	 * 
	 * @return the profile owner's party oid
	 * 
	 * @since 1.0
	 * @version 1.0 
	 */
	public abstract long getPartyOid();
	
	/**
	 * Set the party oid that should be associated with the profile 
	 * 
	 * @param partyOid that should be associated with the profile
	 * 
	 * @since 1.0
	 * @version 1.0 
	 */
	public abstract void setPartyOid(long partyOid);

	/**
	 * Retrieve the string represenatation of a specific limit
	 * 
	 * @param processName The name of the process the limit should be associate with
	 * @param actionName The name of the process-action the limit should be associate 
	 * with
	 * @param limitPropertyName The property kind of the request property the 
	 * limit should be associate with
	 * 
	 * @return a string representation of the limit value, null if the specified limit
	 * could not be found. 
	 * 
	 * @since 1.0
	 * @version 1.0 
	 */
	public abstract String getLimitValue(String processName, String actionName, String limitPropertyName);
	
	/**
	 * Add a process-activity limit to the list of limits associated with the 
	 * profile owner
	 * 
	 * @param processName The name of the process the limit should be associate with
	 * @param actionName The name of the process-action the limit should be associate 
	 * with
	 * @param limitPropertyName The property kind of the request property the 
	 * limit should be associate with
	 * @param limitValue the value oft the limit  
	 * 
	 * @since 1.0
	 * @version 1.0 
	 */
	public abstract void addLimit(String processName, String actionName, String limitPropertyName, String limitValue);
	/**
	 * Add a process-activity limit to the list of limits associated with the 
	 * profile owner
	 * 
	 * @param userLimit A limit object that should be added to the limits list
	 * 
	 * @since 1.0
	 * @version 1.0 
	 */
	public abstract void addLimit(UserLimit userLimit);
	
	/**
	 * Retrieve the list of process-activity limits associated with the profile owner
	 * 
	 * @return list of process-activity limits 
	 * 
	 * @since 1.0
	 * @version 1.0 
	 */
	public abstract List getLimits();
	
	/**
	 * Set the list of process-activity limits that should be associated with the 
	 * profile 
	 * 
	 * @param limits that should be associated with the profile
	 * 
	 * @since 1.0
	 * @version 1.0 
	 */
	public abstract void setLimits(List limits);
	
	/**
	 * Set the allowViewAccessForBatchMode flag.
	 *  
	 * @param allow
	 */
	public abstract void setAllowViewAccessForBatchMode(boolean allow);
	
	/**
	 * Retrieve the allowViewAccessForBatchMode flag
	 * 
	 * @return
	 */
	public abstract boolean isAllowViewAccessForBatchMode();
	
	/**
	 * When true indicates that all relevant rules from new SRS GUI has been set
	 * 
	 * @param flag
	 */
	public abstract void setNewSRSRulesFlag(boolean flag);
	
	/**
	 * When true indicates that all relevant rules from new SRS GUI has been set
	 * 
	 * @return
	 */
	public abstract boolean hasNewSRSRulesBeenSet();
}