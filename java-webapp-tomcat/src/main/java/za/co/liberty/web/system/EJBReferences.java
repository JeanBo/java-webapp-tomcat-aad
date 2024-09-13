package za.co.liberty.web.system;

/**
 * <p>Constant EJB references that are required by this project.</p>
 * 
 * <p>Please add new references according to the accepted standard which is 
 * the name of the ejb bean in capitals with an underscore between the words.
 * Also do not add an I in front of the name.</p>
 *
 */
public interface EJBReferences {
	/* 
	 * Contracting 
	 */
	public static final String ICONTRACTNOENQUIRY   = "ejblocal:za.co.liberty.business.guicontrollers.contracting.IContractNoEnquiryManagement";
	
	
	/*
	 * User profile
	 */
	public static final String CONTEXT_MANAGEMENT = "ejblocal:za.co.liberty.business.guicontrollers.IContextManagement";
	public static final String MENU_MANAGEMENT = "ejblocal:za.co.liberty.business.guicontrollers.userprofiles.IMenuManagement";
	public static final String RULE_MANAGEMENT = "ejblocal:za.co.liberty.business.guicontrollers.userprofiles.IRuleManagement";
	public static final String PROFILE_ROLE_MANAGEMENT = "ejblocal:za.co.liberty.business.guicontrollers.userprofiles.IProfileRoleManagement";
	public static final String USER_ADMIN_MANAGEMENT = "ejblocal:za.co.liberty.business.guicontrollers.userprofiles.IUserAdminManagement";
	public static final String USER_PROFILE_FACADE = "ejblocal:za.co.liberty.entity.facades.IUserProfileEntityFacade";
	
	/*
	 * Security
	 */
	public static final String SECURITY_MANAGEMENT = "ejblocal:za.co.liberty.business.security.ISecurityManagement";
	public static final String TIMED_ROLE_UPDATE =  "ejblocal:za.co.liberty.business.security.ITimedRoleUpdate";
	public static final String SERVER_MANAGEMENT = "ejblocal:za.co.liberty.business.admin.IServerManagement";
	
	/*
	 * Agreement Privileges
	 */
	public static final String AGREEMENT_PRIVILEGES_CONTROLLER   = "ejblocal:za.co.liberty.business.guicontrollers.IAgreementPrivilegesController";
	
	/**
	 * Party Maintenance controller
	 */
	public static final String PARTY_MAINTENANCE_CONTROLLER   = "ejblocal:za.co.liberty.business.guicontrollers.partymaintenance.IPartyMaintenanceController";
	
	/**
	 * Hierarchy Maintenance Controller
	 */
	public static final String HIERARCHY_MAINTENANCE_CONTROLLER   = "ejblocal:za.co.liberty.business.guicontrollers.hierarchy.IHierarchyGUIController";
	
	/**
	 * BAU Reports Management 
	 */
	public static final String BAU_REPORTS_MANAGEMENT   = "ejblocal:za.co.liberty.business.baureports.IBAUReportsManagement";
		
}
