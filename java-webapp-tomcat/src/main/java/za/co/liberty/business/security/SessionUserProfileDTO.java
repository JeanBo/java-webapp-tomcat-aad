package za.co.liberty.business.security;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import za.co.liberty.dto.userprofiles.AllowableRequestActionDTO;
import za.co.liberty.dto.userprofiles.ExplicitAgreementDTO;
import za.co.liberty.dto.userprofiles.ISessionUserProfile;
import za.co.liberty.dto.userprofiles.MenuItemDTO;
import za.co.liberty.dto.userprofiles.RuleDTO;
import za.co.liberty.dto.userprofiles.SessionUserHierarchyNodeDTO;
import za.co.liberty.dto.userprofiles.SessionUserHierarchyRoleDTO;
import za.co.liberty.dto.userprofiles.SessionUserIdDTO;
import za.co.liberty.helpers.validators.RuleValidator;
import za.co.liberty.interfaces.agreements.requests.RequestKindType;
import za.co.liberty.interfaces.rules.RuleType;

/**
 * Represents the sessions logged in user.  These values should only
 * be loaded/set by Security Management business logic.  The lists
 * and maps that are returned are immutable for a reason.
 * 
 * NOTE - The fields are not defined as private so that the management bean
 * can set the values internally.
 * 
 * @author Jean Bodemer (JZB0608) - 12 Nov 2008
 * @author Pritam (pks2802) - 04-10-2013 changed for View Infoslip, added new method getServicedOrManagedAgreementList
 *
 */
class SessionUserProfileDTO implements Serializable, ISessionUserProfile {

	private static final long serialVersionUID = -49670839887847116L;
	
	String uacfId;
	long partyOid;
	long profileOid;
	
	Map<Long, ExplicitAgreementDTO> explicitAgreementMap;
	Map<RuleType,RuleDTO> ruleListMap = new HashMap<RuleType, RuleDTO>();
	List<MenuItemDTO> menuItemList = new ArrayList<MenuItemDTO>();
	Map<RequestKindType, AllowableRequestActionDTO> requestActionMap = 
		new HashMap<RequestKindType, AllowableRequestActionDTO>();
	List<Long> reportsToAgreementList;
	List<Long> ownAgreementList;
	List<Long> servicesAgreementList;
	//Added for View Infoslip-Pritam
	List<Long> managesAgreementList;
	// SSM2707 Market Integration Sweta Menon Start
	List<Long> panelAgreementList;
	Collection<SessionUserHierarchyNodeDTO> allHierarchicalNodeAccessList;
	// SSM2707 Market Integration Sweta Menon End
	RuleValidator ruleValidator;		
	
	Map<Long, ExplicitAgreementDTO> immutableExplicitAgreements;
	List<MenuItemDTO> immutableMenuItemList;

	Collection<SessionUserHierarchyNodeDTO> hierarchicalNodeAccessList;
	Set<Long> hierarchicalNodePartyOidAccessSet;
	
	List<SessionUserHierarchyRoleDTO> hierarchyNodeRoleList;
	
	
	private Map<String, SessionUserIdDTO> sessionMap = new HashMap<String, SessionUserIdDTO>();
	private Stack<String> sessionStack = new Stack<String>();
	
	public boolean isValidSessionId(String sessionId) {		
		return false;
	}
	
	public long getPartyOid() {
		return partyOid;
	}		
	public long getProfileOid() {
		return profileOid;
	}
	public String getUacfId() {
		return uacfId;
	}	
	public Map<Long, ExplicitAgreementDTO> getExplicitAgreements() {
		if (immutableExplicitAgreements==null) {
			immutableExplicitAgreements = Collections
				.unmodifiableMap(explicitAgreementMap);
		}
		return immutableExplicitAgreements;
	}
	public List<MenuItemDTO> getMenuItemList() {
		if (immutableMenuItemList==null) {
			immutableMenuItemList = Collections
				.unmodifiableList(menuItemList);
		}
		return immutableMenuItemList;
	}
	
	public boolean isUserProductionSupport() {
		return (ruleListMap.containsKey(RuleType.PRODUCTION_SUPPORT_USER));
	}
	
	public boolean isUserAllowedViewAccessInBatchOnlyMode() {
		return (ruleListMap.containsKey(RuleType.VIEW_ACCESS_FOR_BATCH_ONLY_MODE));
	}
	public boolean isUserAllowedViewHieararchyAgreements() {
		return (ruleListMap.containsKey(RuleType.VIEW_HIERARCHY_TREE_AGREEMENT_LIST));
	}
	public boolean isUserLimitedToDebtFields() {
		return (ruleListMap.containsKey(RuleType.LIMIT_AGREEMENT_MAINT_TO_DEBT_FIELDS));
	}
	
//	TODO jzb0608 - should I really allow this
//	public boolean isHierarchyNodeInNodeAccessList(long partyOid) {
//		return hierarchicalNodePartyOidAccessSet.contains(partyOid);
//	}
			
	public boolean isAllowRaise(RequestKindType requestKindType) {
		return true;
//		return (requestActionMap.containsKey(requestKindType)) ? 
//				requestActionMap.get(requestKindType).isAllowRaise() : false;
	}
	public boolean isAllowAuthorise(RequestKindType requestKindType) {
		return true;
//		return (requestActionMap.containsKey(requestKindType)) ? 
//				requestActionMap.get(requestKindType).isAllowAuthorise() : false;
	}
	public boolean isAllowDecline(RequestKindType requestKindType) {
		return true;
//		return (requestActionMap.containsKey(requestKindType)) ? 
//				requestActionMap.get(requestKindType).isAllowDecline() : false;
	}
	public boolean isAllowView(RequestKindType requestKindType) {
		return true;
//		return (requestActionMap.containsKey(requestKindType)) ? 
//				requestActionMap.get(requestKindType).isAllowView() : false;
	}
	
	/**
	 * True if the user manages or administers a hierarchy node and 
	 * hierarchical access applies
	 * 
	 * @return
	 */
	public boolean hasHierarchicalAccess() {
		return hierarchyNodeRoleList.size()!=0;
	}
	
	/**
	 * Get the list of hierarchical nodes that this node/person has access to.
	 * 
	 * @return
	 */
	public Collection<SessionUserHierarchyNodeDTO> getHierarchicalNodeAccessList() {
		return hierarchicalNodeAccessList;
	}
	
	/*Market Integration SSM2707 Sweta Menon Begin*/
	/**
	 * This method checks for existence of Rule VIEW_ALL_HIERARCHY_NODES. If
	 * yes, all Branch information existing in the database is loaded for open
	 * access.
	 * 
	 * @return
	 */
	public Collection<SessionUserHierarchyNodeDTO> getAllHierarchicalNodeAccessList() {
		return allHierarchicalNodeAccessList;
	}


	public boolean isViewAllAgreementsRule() {
		return false;
	}
	/*Market Integration SSM2707 Sweta Menon End*/
	
	/**
	 * Get the list of hierarchical roles this user is the context in i.e.
	 * who this users manages or runs.
	 * 
	 * @return
	 */
	public List<SessionUserHierarchyRoleDTO> getHierarchyNodeRoleList() {
		return hierarchyNodeRoleList;
	}
	
	/**
	 * Get the List of ServicedByORManagedBy users
	 * @return List of Long
	 */

	public List<Long> getServicedOrManagedAgreementList() {
		/*SSM2707 Market Integration Sweta Menon*/
		if (this.servicesAgreementList == null
				&& this.managesAgreementList == null
				&& this.panelAgreementList == null)
			return Collections.EMPTY_LIST;
		
		Set<Long> retSet = new HashSet<Long>();
		List<Long> retList = new ArrayList<Long>();
		if(this.servicesAgreementList != null){
			for(Long l:this.servicesAgreementList){
				retList.add(l);
			}
		}
		if(this.managesAgreementList != null){
			for(Long l:this.managesAgreementList){
				retList.add(l);
			}
		}
		
		/*SSM2707 Market Integration Sweta Menon Begin*/
		/* Add the agreements belonging to the panel to the list of agreements. */
		if(this.panelAgreementList != null){
			for(Long l:this.panelAgreementList){
				retList.add(l);
			}
		}
		/*SSM2707 Market Integration Sweta Menon End*/
		
		Iterator<Long> it = retSet.iterator();
		while(it.hasNext()){
			retList.add(it.next());
		}
		
		return retList;
		
	}
	
	/**
	 * True if there is no menu items for this user.
	 * 
	 * @return
	 */
	public boolean hasNoAccess() {
//		if (menuItemList.size()==0) {
//			return true;
//		}
		return false;
	}
	
}
