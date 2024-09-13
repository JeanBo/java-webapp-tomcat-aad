package za.co.liberty.web.system;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.apache.wicket.Session;
import org.apache.wicket.authroles.authentication.AuthenticatedWebApplication;
import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.apache.wicket.feedback.FeedbackMessage;
import org.apache.wicket.request.Request;

import za.co.liberty.business.admin.IServerManagement;
import za.co.liberty.business.security.ISecurityManagement;
import za.co.liberty.dto.gui.context.IContextSearchType;
import za.co.liberty.dto.userprofiles.ContextDTO;
import za.co.liberty.dto.userprofiles.ISessionUserProfile;
import za.co.liberty.dto.userprofiles.MenuItemDTO;
import za.co.liberty.dto.userprofiles.SessionUserIdDTO;
import za.co.liberty.exceptions.UnResolvableException;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.exceptions.security.InvalidUserIdException;
import za.co.liberty.exceptions.security.MenuItemsNotLinkedException;
import za.co.liberty.exceptions.security.UserIdNotInSessionException;
import za.co.liberty.helpers.config.HelperConfigParameterTypes;
import za.co.liberty.helpers.config.HelpersParameterFactory;
import za.co.liberty.helpers.lookup.ServiceLocator;
import za.co.liberty.interfaces.system.SystemModeType;
import za.co.liberty.web.data.pages.IModalMaintenancePageModel;
import za.co.liberty.web.pages.MenuContainer.MenuItem;

/**
 * Project specific session class.  All session relevant data is kept here.
 * 
 */
public class SRSAuthWebSession extends AuthenticatedWebSession {
	/* Constants */
	private static final long serialVersionUID = 1L;
	
	
	/* Attributes */
	private transient Logger logger = Logger.getLogger(this.getClass());
	private Roles roles;
	private List<MenuItemDTO> menuItemList;
	private Map<String, MenuItemDTO> menuItemMap;
	private String applicationVersionNumber;
	private String serverHostName;
	private ContextDTO contextDTO;
	//Keep the user context incase we need to reset it if current context is invalid
	private ContextDTO currentUserContextDTO;
	private boolean hasContextBeenSet;
	
	/* Session attributes */
	private transient IContextSearchType defaultContextSearchType;
	
	private boolean isUserAlreadyLoggedOn = false;
    private boolean isAuthenticated = false;
	private String userId;
	private String password;
	private ISessionUserProfile sessionUser = null;
	private transient IServerManagement serverManagementBean = null;
	
	/* System created menus for the user owning this session */
	private List<MenuItem> usersActualMenus = null;
	/* List of menuItem configurations */
	private HashMap<String, List<MenuItem>> menuItemConfigs = new HashMap<String, List<MenuItem>>();	

	private boolean usersActualMenusSet = false;
	private boolean menuItemConfigsMapSet = false;
	
	private boolean menuItemsDisabledForUser;
	private boolean isConcurrentSession = false;
	private boolean isUserHasPartyProfile = true;
	
	private List<FeedbackMessage> wizardMessages = new ArrayList<FeedbackMessage>();
	
	private static final String MODAL_ATTRIBUTE_PREFIX = "MOD@L$A1_";
	private Map<String, Long> modalAttributeMap = new HashMap<String, Long>();
	
	/**
	 * Default constructor
	 * 
	 * @param application
	 * @param request
	 */
	public SRSAuthWebSession(AuthenticatedWebApplication application,
			Request request) {
		super(request);
	}		
	
	/**
	 * Get a reference to the getLogger()
	 * 
	 * @return
	 */
	protected Logger getLogger() {
		if (logger==null) {
			logger=  Logger.getLogger(this.getClass()); 
		}
		return logger;
	}
	

	/**
	 * @deprecated Not called as we don't use form based authentication
	 */
	@Override
	public boolean authenticate(String userid, String password) {		
//		this.userId = userid;
//		this.password = password;
//		try {
//			//System.out.println("### authenticate");
//			ISecurityManagement userProfile = getSecurityManagement();
//			
////			sessionUser = userProfile.authenticate(userid, password);
//			
//			isAuthenticated = sessionUser!=null;
//			//System.out.println("### authenticate - isAuth = " + isAuthenticated);
//			if (isAuthenticated) {
////				first time on new gui so init the panels
//				//initializeUserMenuItems();
//				//System.out.println("### authenticate - sessionUser = " + sessionUser);
//				getLogger().info("User authenticated :" + userid + ":" + sessionUser);
//			}
//			return isAuthenticated;
//			
//		} catch (SRSBusinessException e) {
//			getLogger().info("Invalid log-in credentials for userId="+userid+" ,class" + e.getClass() + "," + e.getExceptionType());
//			return false;
//
//		}
			return false;
	}
	
	/**
	 * True if the user already logged on
	 * 
	 * @return
	 */
	public boolean isAuthenticated() {
		return isAuthenticated;
	}

	
	@Override
	public void signOut() {		
		this.invalidate();			
	}

	/**
	 * Get an instance of the session.
	 * 
	 * @return
	 */
	public static SRSAuthWebSession get() {		
		return (SRSAuthWebSession) Session.get();
	}

	@Override
	public void invalidate() {
		getLogger().info("Invalidate Session for " + Session.get().getId()
				+ " - " + Session.get());
		super.invalidate();
	}

	/**
	 * Verify that the user is already logged on.
	 * 
	 * @return
	 */
	public boolean isUserAlreadyLoggedOn() {		
		try {
			if (getLogger().isDebugEnabled())
				getLogger().debug("isUserAlreadyLoggedOn = " + isUserAlreadyLoggedOn
						+ "  - sessionId="+getId());
						
			if (isUserAlreadyLoggedOn) {
				return true;
			}
			
//			// No profile so user is unable to do anything in the application
//			if (!isUserHasPartyProfile) {
//				throw new InvalidUserConfigurationRuntimeException();
//			}
	
			/*
			 *  Force bind temporary sessions to ensure the session id
			 *  is bound to the Wicket Session.
			 */
			boolean isNewSession = false;
			if (this.isTemporary()) {
				if (getLogger().isDebugEnabled()) 
					logger.debug("Session isTemporary, bind it");
				/*
				 * Note that the some of the session ID management linked
				 * to a user happens on session binding. Refer to the session
				 * store defined in Application 
				 */
				this.bind();
				if (getLogger().isDebugEnabled()) 
					logger.debug("Session isTemporary, bound complete - sessionId="
							+ getId());
				isNewSession = true;
			}

			ISecurityManagement userProfileManagement = getSecurityManagement();
			sessionUser = userProfileManagement.getUserInSession(getId());
						
			/*
			 *  Add the user to the session map if it doesn't exist.  And update
			 *  the most recent session id / expire old one.
			 */			 
			SessionUserIdDTO sessionIdDto =  userProfileManagement.getUserSessionIdForUacf(
					sessionUser.getUacfId(), getId());
			
			
			
			userProfileManagement.logUserLogonAction(sessionUser);
			
			if (sessionUser.hasNoAccess()) {
				throw new UserNoAccessRuntimeException();
			}
			isUserAlreadyLoggedOn = (sessionUser!=null);
			isAuthenticated = isUserAlreadyLoggedOn;
			
			return isUserAlreadyLoggedOn;
		} catch (InvalidUserIdException e) {
			throw new InvalidUserConfigurationRuntimeException();
		} catch (UserIdNotInSessionException e) {
			return false;
		}
	}
	
	/**
	 * Check if this is a concurrent session for the user.  Ensure the 
	 * session is bound (i.e. not temporary) before calling this function.  As
	 * long 
	 * 
	 * @return
	 */
	public boolean isConcurrentSession() throws ConcurrentSessionForUserRuntimeException {
		
		Boolean isBlock = HelpersParameterFactory.getInstance().getParameter(
	    		  HelperConfigParameterTypes.WEBAPP_CONCURRENT_SESSIONS_BLOCKED, Boolean.class);
		isBlock = (isBlock != null && isBlock);
	    Boolean isWarn = HelpersParameterFactory.getInstance().getParameter(
	    		  HelperConfigParameterTypes.WEBAPP_CONCURRENT_SESSIONS_WARNING, Boolean.class);
	    isWarn = (isWarn != null && isWarn);
	    
	    /*
	     * Keep blocking or warning if this is a concurrent session
	     */
		if (isConcurrentSession) {
			getLogger().warn("Existing concurrent session for user " 
					+ getCurrentUserid());
		    
		    // Always throw but set it to warn if we are not blocking.
		    if (isBlock) {
		    	throw new ConcurrentSessionForUserRuntimeException();
			} 
//		    else if (isWarn) {
//				this.warn("Multiple concurrent user sessions were detected for user.");
//			}
		    return isConcurrentSession;
		}
		
		/*
		 * Ensure this is not an old session that has expired.  If it is
		 * throw an exception which will result in the appropriate routing. 
		 */
		SessionUserIdDTO sessionIdDto = getSecurityManagement()
				.getUserSessionIdForUacf(getCurrentUserid(), getId());

		if (sessionIdDto == null) {
			this.error("Unable to retrieve the session ID, should not happen"); 
			// rather fail than take the risk
			
		} else {
			if (sessionIdDto.isExpiredSessionId(getId()) ) {
				isConcurrentSession=true;
				getSecurityManagement().logUserConcurrentSession(this.getSessionUser());
				getLogger().error("Direct to expired session page, session expired. " 
						+ getCurrentUserid());
				if (isBlock) {
					throw new ConcurrentSessionForUserRuntimeException();
				} else if (isWarn) {
					throw new ConcurrentSessionForUserRuntimeException(true);
//					this.error("Multiple concurrent user sessions were detected for user and has been logged.");
				}
			} else if (!sessionIdDto.isCurrentSessionId(getId())) {
				isConcurrentSession=true;
				getSecurityManagement().logUserConcurrentSession(this.getSessionUser());
				getLogger().error("Direct to expired session page, not current session. " 
						+ getCurrentUserid());
				if (isBlock) {
					throw new ConcurrentSessionForUserRuntimeException();
				} else if (isWarn) {
					throw new ConcurrentSessionForUserRuntimeException(true);
//					this.error("Multiple concurrent user sessions were detected for user and has been logged.");
				}
			} 
		}
		
		return isConcurrentSession;
		
	}

	/**
	 * Return a new reference to Security Management bean
	 * 
	 * @return
	 */
	protected ISecurityManagement getSecurityManagement() {
		
		try {
			return ServiceLocator.lookupService(ISecurityManagement.class);
		} catch (NamingException e) {
			((SRSApplication) getApplication()).getLogger().error(e.getMessage());
			throw new CommunicationException(e);
		}
	}
	
	/**
	 * Returns an instance of the Server Management bean.
	 * 
	 * @return
	 */
	protected IServerManagement getServerManagement() {
		if (serverManagementBean==null) {
			try {
				serverManagementBean = ServiceLocator.lookupService(IServerManagement.class);
			} catch (NamingException e) {
				((SRSApplication) getApplication()).getLogger().error(e.getMessage());
				throw new CommunicationException(e);
			}
		}
		return serverManagementBean;
	}
	
	public boolean isUserInRole(String roleName) {
		if(roleName.equalsIgnoreCase("EveryBody"))
			return true;
		if(roles.contains(roleName))
			return true;
		return false;
	}

	@Override
	public Roles getRoles() {
		return null;
	}
	
	/**
	 * Returns a list of available menu items for the logged in user 
	 * 
	 * @return
	 * @throws MenuProfileNotSetupException
	 * @throws UnResolvableException
	 * @throws MenuItemsNotLinkedException 
	 */
	public List<MenuItemDTO> getMenuItems() {
		if(menuItemList == null && sessionUser != null) {
			menuItemList = sessionUser.getMenuItemList();
		}
		return menuItemList;
	}
	
	private Map<String, MenuItemDTO> getMenuItemMap() {
	
		if (menuItemMap ==null) {
			Map<String, MenuItemDTO> tmp = new HashMap<String, MenuItemDTO>();
			for (MenuItemDTO menuItemDTO : getMenuItems()) {
				tmp.put(menuItemDTO.getImplClazz(), menuItemDTO);
			}
			menuItemMap = tmp;
		}
		return menuItemMap;
		
	}
	
	/**
	 * Returns a list of available menu items for the logged in user 
	 * 
	 * @return
	 * @throws MenuProfileNotSetupException
	 * @throws UnResolvableException
	 * @throws MenuItemsNotLinkedException 
	 */
	public boolean hasMenuInList(Class clazz) {
		return getMenuItemMap().containsKey(clazz.getName());
	}
	
	
	/**
	 * True if the calling object has modify access
	 * 
	 * @param callingObject
	 * @return
	 */
	public boolean hasModifyAccess(Object callingObject ) {
		return hasModifyAccess(callingObject.getClass());
	}
	
	/**
	 * True if the calling object has modify access
	 * 
	 * @param callingObject
	 * @return
	 */
	public boolean hasModifyAccess(Class clazz ) {
		if (isSystemInReadOnlyMode() || isSystemInBatchOnlyMode()) {
			return false;
		}
		if(getMenuItemMap().containsKey(clazz.getName())) {
			return getMenuItemMap().get(clazz.getName()).isModifyAccess();
		}
		return false;
	}
	
	/**
	 * True if the calling object has delete access
	 * 
	 * @param callingObject
	 * @return
	 */
	public boolean hasDeleteAccess(Object callingObject ) {
		return hasDeleteAccess(callingObject.getClass());
	}
		
	/**
	 * True if the calling object has delete access
	 * 
	 * @param callingObject
	 * @return
	 */
	public boolean hasDeleteAccess(Class clazz) {
		if (isSystemInReadOnlyMode() || isSystemInBatchOnlyMode()) {
			return false;
		}
		if(getMenuItemMap().containsKey(clazz.getName())) {
			return getMenuItemMap().get(clazz.getName()).isDeleteAccess();
		}
		return false;
	}
	
	/**
	 * True if the calling object has add access
	 * 
	 * @param callingObject
	 * @return
	 */
	public boolean hasAddAccess(Object callingObject ) {
		return hasAddAccess(callingObject.getClass());
	}
	
	/**
	 * True if the calling object has add access
	 * 
	 * @param callingObject
	 * @return
	 */
	public boolean hasAddAccess(Class clazz ) {
		return hasAddAccess(clazz, false);
	}
	
	/**
	 * True if the calling object has add access
	 * 
	 * @param callingObject
	 * @return
	 */
	public boolean hasAddAccess(Class clazz, boolean ignoreSystemMode ) {
		if (!ignoreSystemMode && (isSystemInReadOnlyMode() || isSystemInBatchOnlyMode())) {
			return false;
		}
		if(getMenuItemMap().containsKey(clazz.getName())) {
			return getMenuItemMap().get(clazz.getName()).isAddAccess();
		}
		return false;
	}
	
	
	
	/**
	 * Retrieve the network host name of the server
	 * 
	 * @return
	 */
	public String getServerHostName() {
		if (serverHostName == null) {
			try {
				serverHostName = InetAddress.getLocalHost().getHostName();
			} catch (UnknownHostException e) {
				getLogger().warn("Unable to retrieve server host name",e);
			}
			serverHostName = (serverHostName ==null) ? "" : serverHostName;
		}
		return serverHostName;
	}
	
	/**
	 * Returns information about the current application
	 * version number.
	 * 
	 * @return
	 */
	public String getApplicationVersionNumber() {
		if (applicationVersionNumber == null) {
			/* Retrieve version nr */
			try {
				applicationVersionNumber = 
					getSecurityManagement().getApplicationVersion();
			} catch (Exception e) {
				getLogger().warn("Unable to retrieve application version number",e);
				applicationVersionNumber="";
			}
		}
		return applicationVersionNumber;
	}
	
	/**
	 * Return the currently logged in user id
	 * 
	 * @return
	 */
	public String getCurrentUserid() {
		try {
			return (sessionUser != null)? sessionUser.getUacfId() : "";
		} catch (Exception e) {
			getLogger().warn("Unable to retrieve currently logged in user",e);
			return null;
		}
	}
	
	/**
	 * Retrieve the currently selected Context object
	 * 
	 * @param contextDTO
	 */
	public void setContextDTO(ContextDTO contextDTO) {
		if(this.contextDTO == null && contextDTO != null){
			//user is always set as the first context
			currentUserContextDTO = contextDTO;
		}
		this.contextDTO = contextDTO;
		this.hasContextBeenSet = true;
	}
	
	/**
	 * Set the currently selected context object
	 * 
	 * @return
	 */
	public ContextDTO getContextDTO() {
		return this.contextDTO;
	}
	
	/**
	 * 
	 * @return
	 */
	public boolean hasContextBeenSet() {
		return hasContextBeenSet;
	}
	
	/**
	 * Get the default context search type for the session.
	 * 
	 * @return
	 */
	public IContextSearchType getDefaultContextSearchType() {
		return defaultContextSearchType;
	}
	
	/**
	 * Set the default context search type for the session.
	 * 
	 * @param searchType
	 */
	public void setDefaultContextSearchType(IContextSearchType searchType) {
		this.defaultContextSearchType = searchType;
	}
	
	/**
	 * Get the logged in users security data
	 * @return
	 */
	public ISessionUserProfile getSessionUser(){
		return sessionUser;
	}	
	
	/* User menu Item data */
	/**
	 * Gets the users system configured menu item details
	 */
	public List<MenuItem> getUsersActualMenus() {
		return usersActualMenus;
	}
	
	public void setUsersActualMenus(List<MenuItem> usersActualMenus) {		
		if(usersActualMenusSet){
			throw new IllegalStateException("Menu items should only be set once per user session");
		}
		usersActualMenusSet = true;		
		this.usersActualMenus = usersActualMenus;
	}
	
	/**
	 * Gets the users system configured menu item details, the key for the map is the class of the page
	 */
	public HashMap<String, List<MenuItem>> getMenuItemConfigs() {
		return menuItemConfigs;
	}

	public void setMenuItemConfigs(HashMap<String, List<MenuItem>> menuItemConfigs) {
		if(menuItemConfigsMapSet){
			throw new IllegalStateException("Menu items should only be set once per user session");
		}
		menuItemConfigsMapSet = true;
		this.menuItemConfigs = menuItemConfigs;
	}
	/* END User menu Item data */

	public ContextDTO getCurrentUserContextDTO() {
		return currentUserContextDTO;
	}
	
	/**
	 * Will return the panel configuration in the DB, Not user specific
	 * @return
	 */
	public MenuItemDTO getPanelConfigurationInDB(Class panelClass){		
		if(panelClass != null){
			SRSApplication app = (SRSApplication)this.getApplication();
			return app.getPanelConfig(panelClass);
		}
		return null;
	}
	
	/**
	 * True if the system is currently in read-only mode.
	 * 
	 * @return
	 */
	public boolean isSystemInReadOnlyMode() {
		return getServerManagement().isOnlineSystemInReadOnlyMode();
//		return false;
	}
	
	/**
	 * True if the system is currently in Batch only mode.
	 * 
	 * @return
	 */
	public boolean isSystemInBatchOnlyMode() {
		return getServerManagement().isOnlineSystemInBatchOnlyMode();
	}
	
	/**
	 * Returns the current system mode of the system
	 * @return
	 */
	public SystemModeType getSystemModeType() {
		return getServerManagement().getCurrentSystemMode();
	}
	
	public boolean isUserAllowedViewAccessInBatchOnlyMode() {
		return (sessionUser!=null && sessionUser.isUserAllowedViewAccessInBatchOnlyMode());
	}
	
	/**
	 * True if the system is currently in Online mode.
	 * 
	 * @return
	 */
	public boolean isSystemInOnlineMode() {
		return getServerManagement().isOnlineSystemInOnlineMode();
	}

	/**
	 * Returns true if the current user is unauthenticated.
	 * 
	 * @return
	 */
	public boolean isUserUnauthenticated() {
		try {
			return getSecurityManagement().isUserUnauthenticated();
		} catch (UserIdNotInSessionException e) {
			return true;
		}
	}	
	
	public boolean isMenuItemsDisabledForUser() {
		return menuItemsDisabledForUser;
	}

	/**
	 * Set if the user should have menu items displayed on the screen
	 * @param menuItemsDisabledForUser
	 */
	public void setMenuItemsDisabledForUser(boolean menuItemsDisabledForUser) {
		this.menuItemsDisabledForUser = menuItemsDisabledForUser;
	}

	
	
	/**
	 * Store a session variable, only for modal pages
	 * 
	 * @param internalModalSessionIdentifier
	 * @param model
	 */
	public void storeModalPageModel(String internalModalSessionIdentifier, IModalMaintenancePageModel model) {
		checkModalMap();
		modalAttributeMap.put(MODAL_ATTRIBUTE_PREFIX+  internalModalSessionIdentifier, System.currentTimeMillis());
		setAttribute(MODAL_ATTRIBUTE_PREFIX+  internalModalSessionIdentifier, (Serializable) model);
	}
	
	/**
	 * Get modal page models from the session, for SRSModalWindow only
	 * 
	 * @param internalModalSessionIdentifier
	 * @return
	 */
	public IModalMaintenancePageModel getModalPageModel(String internalModalSessionIdentifier) {
		return (IModalMaintenancePageModel) getAttribute(MODAL_ATTRIBUTE_PREFIX + internalModalSessionIdentifier);
	}
	
	/**
	 * Store a session variable, only for modal pages
	 * 
	 * @param internalModalSessionIdentifier
	 * @param model
	 */
	public void storeAdditionalModalPageModel(String internalModalSessionIdentifier, Serializable model) {
		checkModalMap();
		modalAttributeMap.put(MODAL_ATTRIBUTE_PREFIX+  internalModalSessionIdentifier, System.currentTimeMillis());
		setAttribute(MODAL_ATTRIBUTE_PREFIX+  internalModalSessionIdentifier, (Serializable) model);
	}
	
	/**
	 * Get modal page models from the session, for SRSModalWindow only
	 * 
	 * @param internalModalSessionIdentifier
	 * @return
	 */
	public Serializable getAdditionalModalPageModel(String internalModalSessionIdentifier) {
		return (Serializable) getAttribute(MODAL_ATTRIBUTE_PREFIX + internalModalSessionIdentifier);
	}
	
	/**
	 * Clear page model from the session
	 * 
	 * @param internalModalSessionIdentifier
	 */
	public void clearModalPageModel(String internalModalSessionIdentifier) {
		modalAttributeMap.remove(MODAL_ATTRIBUTE_PREFIX+  internalModalSessionIdentifier);
//		, System.currentTimeMillis());
		removeAttribute(MODAL_ATTRIBUTE_PREFIX + internalModalSessionIdentifier);
	}
	
	/**
	 * Clear all modal attributes in session
	 */
	public void clearModalPageModelAll() {
		for (String s :getAttributeNames()) {
			if (s.startsWith(MODAL_ATTRIBUTE_PREFIX)) {
				getLogger().debug("Clearing modal session attribute :" + s +":");
				removeAttribute(s);
			}
		}
	}
	
	/**
	 * Check the modal map and print out some values if required.  Can be used to clean the session from time to time, to ensure
	 * size is maintained on file system.  (Wicket serializes to the file system)
	 */
	public void checkModalMap() {
		 
		StringBuilder builder = new StringBuilder(500);
		
		long t = System.currentTimeMillis();
		
		for (String k : modalAttributeMap.keySet()) {
			builder.append("\nKey :");
			builder.append(k);
			Long l = modalAttributeMap.get(k);
			builder.append("   age (ms):" +  ((l==null)? "null" : (t-l)));
			Object o = getAttribute("k");
//			builder.append("   est size :" + ObjectSizeCalculator.getObjectSize(o));
		}
		
		if (logger.isDebugEnabled())
			logger.debug("CheckModalMap = Modal entries below" + builder.toString());
		
	}
	
	
	
//	/**
//	 * Clear wizard messages, called at start of wizard.
//	 * 
//	 * TODO we may want to add messages per path
//	 * @deprecated  Used for testing only, will remove
//	 */
//	public void clearWizardMessages() {
//		
//		synchronized (wizardMessages) {
//			wizardMessages.clear();
//			
//		}
//		logger.info("Wizard.clear = " + wizardMessages.size());
//		
//	}
//	
//	/**
//	 * Add an error message from Feedback messages when the path includes
//	 * the wizard components.
//	 * 
//	 * @deprecated  Used for testing only, will remove
//	 * @param message
//	 */
//	public void addWizardMessage(FeedbackMessage message) {
//		synchronized (wizardMessages) {
//			wizardMessages.add(message);
//		}
//		
//	}
//	
//	/**
//	 * @deprecated  Used for testing only, will remove
//	 * 
//	 * @return
//	 */
//	public List<FeedbackMessage> getWizardMessages() {
//		return wizardMessages;
//	}
	
}