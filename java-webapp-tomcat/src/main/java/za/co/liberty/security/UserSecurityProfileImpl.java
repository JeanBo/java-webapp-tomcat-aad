package za.co.liberty.security;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Concrete implementation of the UserSecurityProfile interface. 
 * 
 * @see UserSecurityProfile
 *
 * @since 1.0
 * @version 1.0
 */
public class UserSecurityProfileImpl implements UserSecurityProfile, Serializable {

	/**
	 * The UACF id of the user, that is the user's login name
	 */
	private String uacfId = null;  
	/**
	 * The list identifying the user's intermediary agreements
	 */
	private List ownAgreements = new ArrayList();  
	/**
	 * The oid of the party object associated with the user
	 */
	private long partyOid;  
	/**
	 * List of UserLimit instances that defines process-action limits associated
	 * with the user
	 */
	private List limits = new ArrayList();
	
	/**
	 * The list of agreements that the user is allowed to manage
	 */
	private List agreements = new ArrayList(); 

	private boolean allowViewAccessForBatchMode;
	
	private boolean hasNewSRSRulesBeenSet;
	
	/**
	 * Default constructor
	 * 
	 * @since 1.0
	 * @version 1.0
	 */
	public UserSecurityProfileImpl() {
		super();
	}

	/* (non-Javadoc)
	 * @see za.co.liberty.security.UserSecurityProfile#getPartyOid()
	 */
	public long getPartyOid() {
		return partyOid;
	}

	/* (non-Javadoc)
	 * @see za.co.liberty.security.UserSecurityProfile#setPartyOid(long)
	 */
	public void setPartyOid(long partyOid) {
		this.partyOid = partyOid;
	}

	/* (non-Javadoc)
	 * @see za.co.liberty.security.UserSecurityProfile#getLimits()
	 */
	public List getLimits() {
		return limits;
	}

	/* (non-Javadoc)
	 * @see za.co.liberty.security.UserSecurityProfile#setLimits(java.util.List)
	 */
	public void setLimits(List limits) {
		this.limits = limits;
	}

	/* (non-Javadoc)
	 * @see za.co.liberty.security.UserSecurityProfile#addLimit(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	public void addLimit(String processName, String actionName, String limitPropertyName, String limitValue) {
		UserLimit userLimit = new UserLimitImp(processName, actionName, limitPropertyName, limitValue);
		addLimit(userLimit);		
	}

	/* (non-Javadoc)
	 * @see za.co.liberty.security.UserSecurityProfile#addLimit(za.co.liberty.security.UserLimit)
	 */
	public void addLimit(UserLimit userLimit) {
		limits.add(userLimit);
	}

	/* (non-Javadoc)
	 * @see za.co.liberty.security.UserSecurityProfile#getLimitValue(java.lang.String, java.lang.String, java.lang.String)
	 */
	public String getLimitValue(String processName, String actionName, String limitPropertyName) {
		UserLimit userLimit = null;
		boolean found = false;
		
		Iterator it = limits.iterator();
		while((it.hasNext()) && (!found)) {
			userLimit = (UserLimit)it.next();
			if ((processName.equals(userLimit.getProcessName())) &&
				(actionName.equals(userLimit.getActionName())) &&
				(limitPropertyName.equals(userLimit.getLimitPropertyName()))) {	
				found = true;
			}
		}
		
		if (!found) {
			userLimit = null;
		}
		
		return userLimit.getLimitValue();
	}

	/* (non-Javadoc)
	 * @see za.co.liberty.security.UserSecurityProfile#getUacfId()
	 */
	public String getUacfId() {
		return uacfId;
	}

	/* (non-Javadoc)
	 * @see za.co.liberty.security.UserSecurityProfile#setUacfId(java.lang.String)
	 */
	public void setUacfId(String uacfId) {
		if (uacfId == null) {
			throw new IllegalArgumentException("Invalid null UACFID id value");
		}
		this.uacfId = uacfId;	
	}

	/* (non-Javadoc)
	 * @see za.co.liberty.security.UserSecurityProfile#getOwnAgreements()
	 */
	public List getOwnAgreements() {
		return ownAgreements;
	}

	/* (non-Javadoc)
	 * @see za.co.liberty.security.UserSecurityProfile#setOwnAgreements(java.util.List)
	 */
	public void setOwnAgreements(List ownAgreements) {
		if (ownAgreements == null) {
			throw new IllegalArgumentException("Invalid null agreement no's");
		}
		this.ownAgreements = ownAgreements;	
	}

	/* (non-Javadoc)
	 * @see za.co.liberty.security.UserSecurityProfile#addOwnAgreement(long)
	 */
	public void addOwnAgreement(long agreementOid) {
		ownAgreements.add(new Long(agreementOid));		
	}

	/* (non-Javadoc)
	 * @see za.co.liberty.security.UserSecurityProfile#containOwnAgreement(long)
	 */
	public boolean containOwnAgreement(long agreementOid) {
		return ownAgreements.contains(new Long(agreementOid));
	}

	/* (non-Javadoc)
	 * @see za.co.liberty.security.UserSecurityProfile#getAgreements()
	 */
	public List getAgreements() {
		return agreements;
	}

	/* (non-Javadoc)
	 * @see za.co.liberty.security.UserSecurityProfile#setAgreements(java.util.List)
	 */
	public void setAgreements(List agreements) {
		if (agreements == null) {
			throw new IllegalArgumentException("Invalid null agreement no's");
		}
		this.agreements = agreements;
	}

	/* (non-Javadoc)
	 * @see za.co.liberty.security.UserSecurityProfile#addAgreement(long)
	 */
	public void addAgreement(long agreementOid) {
		agreements.add(new Long(agreementOid));		
	}

	/* (non-Javadoc)
	 * @see za.co.liberty.security.UserSecurityProfile#containAgreement(long)
	 */
	public boolean containAgreement(long agreementOid) {
		return agreements.contains(new Long(agreementOid));
	}
	
	/**
	 * Set the allowViewAccessForBatchMode flag.
	 *  
	 * @param allow
	 */
	public void setAllowViewAccessForBatchMode(boolean allow) {
		allowViewAccessForBatchMode = allow;
	}
	
	/**
	 * Retrieve the allowViewAccessForBatchMode flag
	 * 
	 * @return
	 */
	public boolean isAllowViewAccessForBatchMode() {
		return allowViewAccessForBatchMode;
	}
	
	/**
	 * When true indicates that all relevant rules from new SRS GUI has been set
	 * 
	 * @param flag
	 */
	public void setNewSRSRulesFlag(boolean flag) {
		hasNewSRSRulesBeenSet=flag;
	}
	
	/**
	 * When true indicates that all relevant rules from new SRS GUI has been set
	 * 
	 * @return
	 */
	public boolean hasNewSRSRulesBeenSet() {
		return hasNewSRSRulesBeenSet;
	}
}
