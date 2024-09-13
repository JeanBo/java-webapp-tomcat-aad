package za.co.liberty.business.security;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import za.co.liberty.dto.userprofiles.ExplicitAgreementDTO;
import za.co.liberty.dto.userprofiles.ISystemUserProfile;
import za.co.liberty.dto.userprofiles.MenuItemDTO;
import za.co.liberty.dto.userprofiles.SessionUserHierarchyNodeDTO;
import za.co.liberty.dto.userprofiles.SessionUserHierarchyRoleDTO;
import za.co.liberty.interfaces.agreements.requests.RequestKindType;
import za.co.liberty.security.UserSecurityProfileImpl;

/**
 * Profile for the system as a user who raises requests and executes actions
 * @author DZS2610
 *
 */
@SuppressWarnings("unchecked")
class SystemUserProfileDTO extends SessionUserProfileDTO implements ISystemUserProfile{
	private static final long serialVersionUID = 1L;
	
	public Map<Long, ExplicitAgreementDTO> getExplicitAgreements() {				
		return Collections.EMPTY_MAP;
	}

	public Collection<SessionUserHierarchyNodeDTO> getHierarchicalNodeAccessList() {				
		return Collections.EMPTY_LIST;
	}

	public List<SessionUserHierarchyRoleDTO> getHierarchyNodeRoleList() {				
		return Collections.EMPTY_LIST;
	}

	public List<MenuItemDTO> getMenuItemList() {				
		return Collections.EMPTY_LIST;
	}

	public long getPartyOid() {				
		return 236;
	}

	public String getUacfId() {				
		return "SRSSYS";
	}

	public boolean hasHierarchicalAccess() {				
		return true;
	}

	public boolean isAllowAuthorise(RequestKindType requestKindType) {
		return true;
	}

	public boolean isAllowDecline(RequestKindType requestKindType) {
		return true;
	}

	public boolean isAllowRaise(RequestKindType requestKindType) {
		return true;
	}

	public boolean isAllowView(RequestKindType requestKindType) {
		return true;
	}

	public boolean isUserAllowedViewAccessInBatchOnlyMode() {
		return true;
	}

	public boolean isUserAllowedViewHieararchyAgreements() {
		return true;
	}

	public Object getUserSecurityProfile() {		
		UserSecurityProfileImpl systemProfile = new UserSecurityProfileImpl();
		systemProfile.setAgreements(Collections.EMPTY_LIST);
		systemProfile.setAllowViewAccessForBatchMode(true);
		systemProfile.setLimits(Collections.EMPTY_LIST);
		systemProfile.setNewSRSRulesFlag(true);		
		systemProfile.setOwnAgreements(Collections.EMPTY_LIST);
		systemProfile.setPartyOid(getPartyOid());
		systemProfile.setUacfId(getUacfId());		
		return systemProfile;
	}	
}
