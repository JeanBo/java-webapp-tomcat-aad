package za.co.liberty.web.pages;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.NamingException;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.authorization.UnauthorizedInstantiationException;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.http.WebResponse;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import za.co.liberty.business.guicontrollers.IBasePageGuiController;
import za.co.liberty.business.guicontrollers.IContextManagement;
import za.co.liberty.business.security.ISecurityManagement;
import za.co.liberty.dto.gui.context.AgreementSearchType;
import za.co.liberty.dto.gui.context.IndividualSearchType;
import za.co.liberty.dto.gui.context.ResultContextItemDTO;
import za.co.liberty.dto.gui.context.ResultContextSearchDTO;
import za.co.liberty.dto.userprofiles.ContextAgreementDTO;
import za.co.liberty.dto.userprofiles.ContextDTO;
import za.co.liberty.dto.userprofiles.ContextPartyDTO;
import za.co.liberty.dto.userprofiles.ISessionUserProfile;
import za.co.liberty.dto.userprofiles.MenuItemDTO;
import za.co.liberty.exceptions.data.QueryTimeoutException;
import za.co.liberty.exceptions.error.request.RequestException;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.helpers.lookup.ServiceLocator;
import za.co.liberty.helpers.xls.IExcelRows;
import za.co.liberty.helpers.xls.SimpleExcelProvider;
import za.co.liberty.interfaces.agreements.requests.RequestKindType;
import za.co.liberty.interfaces.agreements.requests.RequestStatusType;
import za.co.liberty.interfaces.persistence.agreement.request.IRequestEnquiryRow;
import za.co.liberty.interfaces.security.SecurityPageActionType;
import za.co.liberty.interfaces.system.SystemModeType;
import za.co.liberty.web.data.enums.ContextType;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.data.enums.PanelToRequestMapping;
import za.co.liberty.web.pages.interfaces.IChangeableStatefullComponent;
import za.co.liberty.web.system.EJBReferences;
import za.co.liberty.web.system.SRSAuthWebSession;

/** 
 * <p>This page should be used as the base class for all web pages in this
 * project.  It implements Page Security, Dynamic Context functionality,
 * Menu Structures, EditState, Excel streaming and more.</p>
 * 
 * <p><strong>Context functionality</strong> is provided by this page, just 
 * override {@link #getContextTypeRequired()} to indicate the type of context 
 * that is required.  The default behaviour is to not show a context at all.
 * <br/><br/>
 * This page retrieves the Context from the session and stores a cloned 
 * copy of it locally when the page is shown for the first time. This is to 
 * ensure that the back button and that multiple pages that are open on one 
 * session behave as expected.</p>
 * 
 * <p><strong>Security functionality</strong> is also provided by this page
 * and indicates what access the logged in user has on the page.  Refer to 
 * {@link #hasAddAccess()}, {@link #hasDeleteAccess()} and 
 * {@link #hasModifyAccess()}.</p>
 * 
 * @author DHR1910 / JZB0608
 */
public abstract class BasePage extends WebPage implements IChangeableStatefullComponent {

	/* Constants */
	private static final long serialVersionUID = -924281747339707143L;
	
	public static final String CONTEXT_PANEL_NAME = "contextPanel";
	public static final String VERSION_DESCRIPTION = "Version:";
	public static final String USER_DESCRIPTION = "User:";
	public static final String SERVER_DESCRIPTION = "Server:";
	
	/* components */
	private Panel menuContainerComponent;
	
	/* attributes/fields */
	protected transient Logger logger;
	private EditStateType editState;
	private boolean isInitialised = false;
	protected List<MenuItemDTO> menuList = null;
	protected Map<String,MenuItemDTO> menuMap = null;
	private FeedbackPanel feedbackPanel;
	private String applicationVersion;
	private String serverName;
	private String userId;
	private String sessionId;
	protected ContextDTO pageContextDTO;
	private boolean removeMenuItems;
	protected Panel contextPanel;
	
	static {
		Logger.getInstance(BasePage.class).setLevel(Level.DEBUG);
	}
	//beans
	private transient IBasePageGuiController basePageGuiController;
	
	/**
	 * Default constructor 
	 * 
	 * @param parameters
	 */
	public BasePage(final PageParameters parameters) {
		this();
	}	
	
	/**
	 * Default constructor
	 *
	 */
	public BasePage() {
		this(false);
	}

	/**
	 * 
	 * @param removeMenuItems if true then all menu items will be removed
	 */
	public BasePage(boolean removeMenuItems) {
		this.removeMenuItems = removeMenuItems;
		editState = EditStateType.VIEW;
		initialise();
	}

	/**
	 * Initialises this object.  
	 * 
	 */
	@SuppressWarnings("unchecked")
	protected void initialise() {
		/* Ensure initialised is only called once */
		if (isInitialised) {
			throw new IllegalStateException("Webpage may be initialised once only!");
		}
		
		/* Configure Message panel */
		if (isShowFeedBackPanel()) {
		  feedbackPanel = new FeedbackPanel("messages");
		  feedbackPanel.setOutputMarkupId(true);
		  add(feedbackPanel);
		} else {
		  add(new EmptyPanel("messages"));
		}
		
		/* Do authentication */		
		if (isCheckAuthentication() && (((!SRSAuthWebSession.get().isAuthenticated()) ? SRSAuthWebSession.get().isUserAlreadyLoggedOn() : SRSAuthWebSession.get().isAuthenticated()))) {// || !((SRSApplication)this.getApplication()).isLeaveOutUserInSessionCheckForPageInstantiation()
			if (SRSAuthWebSession.get().isAuthenticated()) {
				// Application version
				applicationVersion = SRSAuthWebSession.get().getApplicationVersionNumber();
				userId = SRSAuthWebSession.get().getCurrentUserid();
				
				// Highly unlikely as it is bound in WebSession
				if (SRSAuthWebSession.get().isTemporary()) {
					if (getLogger().isDebugEnabled()) 
						logger.debug("Session isTemporary, bind it");
					SRSAuthWebSession.get().bind();
				}
				sessionId = SRSAuthWebSession.get().getId();			
							
				/*
				 * Ensure this is not an old session that has expired.  If it is
				 * throw an exception which will result in the appropriate routing. 
				 */
				SRSAuthWebSession.get().isConcurrentSession();	
				
				// Linked menu items
				if(menuList == null) {
					menuList =  SRSAuthWebSession.get().getMenuItems();

					menuMap = new HashMap<String, MenuItemDTO>();
					for (MenuItemDTO menuItemDTO : menuList) {
						menuMap.put(menuItemDTO.getImplClazz(), menuItemDTO);
					}
				}
			} else {
				throw new UnauthorizedInstantiationException(this.getClass()); 
			}
		} else {
			menuList = new ArrayList<MenuItemDTO>();
		}
		if(!removeMenuItems && !SRSAuthWebSession.get().isMenuItemsDisabledForUser()){
			add(menuContainerComponent = new MenuContainer("menucontainer",this, menuList, this.getClass()));
		}else{
			add(menuContainerComponent = new EmptyPanel("menucontainer"));
			menuContainerComponent.setOutputMarkupId(true);
			menuContainerComponent.setOutputMarkupPlaceholderTag(true);
		}
		
		// Host name
		serverName = SRSAuthWebSession.get().getServerHostName();
		
		/* Page fields */
		add(new Label("pageName",new Model() {
			private static final long serialVersionUID = 1L;
			@Override
			public String getObject() {
				return BasePage.this.getPageName();
			}
		}));
		add(new Label("applicationVersion",new Model(applicationVersion)));
		add(new Label("applicationVersionLabel",new Model(
				(applicationVersion== null || applicationVersion.length() == 0)
					? null : VERSION_DESCRIPTION)));
		add(new Label("serverName",new Model(serverName)));
		add(new Label("serverNameLabel",new Model(
				(serverName== null || serverName.length() == 0)
					? null : SERVER_DESCRIPTION)));
		add(new Label("userId",new Model(userId)));
		add(new Label("userIdLabel",new Model(
				(userId== null || userId.length() == 0)
					? null : USER_DESCRIPTION)));
		SystemModeType modeType = SRSAuthWebSession.get().getSystemModeType();
		add(new Label("systemMode", new Model(
				(modeType!=SystemModeType.ONLINE_MODE) ? " - " + modeType.getDescription() : "")));
		
		/* Context */
		if (getContextTypeRequired() != ContextType.NONE && pageContextDTO==null) {
			// Attempt to retrieve from session and make a copy
			ContextDTO contextDTO = SRSAuthWebSession.get().getContextDTO();
			if (SRSAuthWebSession.get().hasContextBeenSet() == false 
					&& contextDTO == null) {
				setContextToCurrentUser();
				contextDTO = SRSAuthWebSession.get().getContextDTO();
			}
			
			if (contextDTO != null) {
				// Populate the page context with a copy of the session context
				try {
					pageContextDTO = contextDTO.clone();
				} catch (CloneNotSupportedException e) {
					getLogger().error("Unable to clone page context",e);
				}
			} 
			
		}		
		add(contextPanel = getContextPanel());	
		//initializeAllPagesPanelsMap();
		
		getSecurityManagement().logUserPageAction(this.getClass(), 
				SRSAuthWebSession.get().getSessionUser(), 
				SecurityPageActionType.VIEW, pageContextDTO);
		
		/* Check whether the system is in Batch Only Mode */
		if(SRSAuthWebSession.get().isSystemInBatchOnlyMode() && SRSAuthWebSession.get().isAuthenticated() 
				&& !SRSAuthWebSession.get().isUserAllowedViewAccessInBatchOnlyMode()) {
			getLogger().debug("About to direct to BATCH MODE Screen.");
			setResponsePage(BatchModePage.class);
		}

		
		/* Add some session stuff here */
//		if (this.getRequest().get)
			
//		WebRequest request =  (WebRequest) RequestCycle.get().getRequest();
//		if (this.getRequest()!=null) {
//			HttpServletRequest httpRequest = ((WebRequest) getRequest()).getHttpServletRequest();
//			httpSession = httpRequest.getSession(false);
//			if (getLogger().isDebugEnabled()) 
//				logger.debug("Session already exist = " + (httpSession!=null));
//			if (httpSession == null) {
//				logger.info("Force new session");
//				httpSession = httpRequest.getSession();
//			}
//			getLogger().info("Session id is " + httpSession.getId());
//			getLogger().info("End Wicket Session id is " + getId());
//		}
//		ISecurityManagement userProfile = getSecurityMangement();
//
//		sessionUser = userProfile.getUserInSession((httpSession==null)?null:httpSession.getId());
		getLogger().info("Base Page Init done");
	}

	/**
	 * Return the relevant context panel.  Will only return a 
	 * ContextPanel if {@link #isShowContextPanel()} returns true.
	 * 
	 * @return
	 */
	protected Panel getContextPanel() {
		if (isShowContextPanel()) {
			return new DynamicContextPanel(CONTEXT_PANEL_NAME,this);
		} else {
			return new EmptyPanel(CONTEXT_PANEL_NAME);
		}
	}
	
	/**
	 * Indicates if the page requires a Context Panel. By default this 
	 * page will only return true if the ContextType required is not
	 * NONE.  Refer to {@link #getContextTypeRequired()).
	 *  
	 * @return 
	 */
	protected boolean isShowContextPanel() {
		return ContextType.NONE != getContextTypeRequired();
	}
	
	/**
	 * Returns {@link ContextType#NONE} by default.  Override this to 
	 * indicate what Context Type you need. 
	 * 
	 * @return
	 */
	public ContextType getContextTypeRequired() {
		return ContextType.NONE;
	}
	
	/**
	 * Authentication is checked when this returns true. Defaults to true.
	 * 
	 * @return
	 */
	protected boolean isCheckAuthentication() {
		return true;
	}
	
	/**
	 * Feedback panel can be disabled by overriding this method
	 * and returning false.
	 * 
	 * @return
	 */
	protected boolean isShowFeedBackPanel() {
		return true;
	}
	
	/**
	 * Return the name of the current web page.
	 * 
	 * @return
	 */
	public abstract String getPageName();
	
	/**
	 * True if this page has modify access
	 * @return
	 */
	public boolean hasModifyAccess() {
		return hasModifyAccess(this);
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
		if (SRSAuthWebSession.get().isSystemInReadOnlyMode()|| SRSAuthWebSession.get().isSystemInBatchOnlyMode()) {
			return false;
		}
		if(menuMap != null && menuMap.containsKey(clazz.getName())) {
			MenuItemDTO menuItemDTO = menuMap.get(clazz.getName());
			return menuItemDTO.isModifyAccess();
		}
		return false;
	}
	
	/**
	 * True if this page has delete access
	 * @return
	 */
	public boolean hasDeleteAccess() {
		return hasDeleteAccess(this);
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
		if (SRSAuthWebSession.get().isSystemInReadOnlyMode()|| SRSAuthWebSession.get().isSystemInBatchOnlyMode()) {
			return false;
		}
		if(menuMap != null && menuMap.containsKey(clazz.getName())) {
			MenuItemDTO menuItemDTO = menuMap.get(clazz.getName());
			return menuItemDTO.isDeleteAccess();
		}
		return false;
	}
	
	/**
	 * Is user allowed to raise 
	 * @param requestKind
	 * @return
	 */
	public boolean isAllowRaise(RequestKindType requestKind) {
		ISessionUserProfile loggedInUser = SRSAuthWebSession.get()
				.getSessionUser();
		return loggedInUser.isAllowRaise(requestKind);
	}
		
	/**
	 * True if this page has add access
	 * @return
	 */
	public boolean hasAddAccess() {		
		return hasAddAccess(this);
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
			
		
		if (SRSAuthWebSession.get().isSystemInReadOnlyMode() || SRSAuthWebSession.get().isSystemInBatchOnlyMode()) {
			return false;
		}
		if(menuMap != null && menuMap.containsKey(clazz.getName())) {
			MenuItemDTO menuItemDTO = menuMap.get(clazz.getName());
			return menuItemDTO.isAddAccess();
		}
		return false;
	}
	
	public void streamToExcel(IExcelRows rowList) {
		
		SimpleExcelProvider excelProvider = new SimpleExcelProvider(rowList);
		
		// WICKETTEST WICKETFIX
		// Unsure if this will work, test it.
		WebResponse response = (WebResponse)getResponse();
		HttpServletResponse servletResponse = (HttpServletResponse) response.getContainerResponse();
		servletResponse.setContentType("application/vnd.ms-excel");
		servletResponse.setHeader("Content-Disposition","attachment;filename=SRSDownload.xls");
		try {
			OutputStream outputStream = servletResponse.getOutputStream();
			excelProvider.getExcelOpject(outputStream);
			outputStream.close();
		} catch (IOException e) {
			getLogger().error("Unable to stream file to Excel",e);
		}
	}
	
	/**
	 * Returns the RequestKindTypes that have unauthorised requests for the panel using the context data
	 * @param panelClass
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<RequestKindType> getOutStandingRequestTypesForPanel(Class panelClass){
		List<RequestKindType> ret = new ArrayList<RequestKindType>();
		ContextDTO context = getPageContextDTO();
		if(context == null){
			return Collections.EMPTY_LIST;
		}
		RequestKindType[] kinds = PanelToRequestMapping.getRequestKindsForPanel(panelClass);
		if(kinds == null){
			return Collections.EMPTY_LIST;
		}		
		
		boolean lookForTemplateRequests = false;
		List<RequestKindType> templateRequestKindTypes = RequestKindType.getTemplateRequestKindType();
		for(RequestKindType kind : kinds){
			for (RequestKindType templateType : templateRequestKindTypes) {
				if(kind == templateType){
					lookForTemplateRequests = true;
					break;
				}
			}
			if(lookForTemplateRequests){
				break;
			}
		}
		boolean lookForAgreementRequests = false;
		for(RequestKindType kind : kinds){
			if(kind.isRequiresTargetActual()){
				lookForAgreementRequests = true;
				break;
			}
		}
		try{
			List<IRequestEnquiryRow> requests = Collections.EMPTY_LIST;
			if(lookForAgreementRequests){
				if(context.getAgreementContextDTO() != null && context.getAgreementContextDTO().getAgreementNumber() != null){					
					requests = getBasePageGuiController().findRequestsForAgreement(
							context.getAgreementContextDTO().getAgreementNumber(), RequestStatusType.REQUIRES_AUTHORISATION, kinds);
				}				
			}else if (lookForTemplateRequests){
				requests = getBasePageGuiController().findRequestsForTemplate(
						 RequestStatusType.REQUIRES_AUTHORISATION, kinds);
			}else {			
				if(context.getPartyContextDTO() != null){					
					requests = getBasePageGuiController().findRequestsForParty(
							context.getPartyContextDTO().getPartyOid(), RequestStatusType.REQUIRES_AUTHORISATION, kinds);
				}
			}
			for(IRequestEnquiryRow row : requests){
				ret.add(RequestKindType.getRequestKindTypeForKind(row.getRequestKind()));
			}
		}catch(QueryTimeoutException e){
			//we display error and return all requests, this will mean the screen should be blocked from editing
			error("Could not determine if all requests have been executed for the given panel, the query timed out. Please try again or call support");
			return Arrays.asList(kinds);
		} catch (RequestException e) {
			error(e.getMessage());
			return Arrays.asList(kinds);
		}
		return ret;
	}
	
	/*SSM2707 SWETA MENON Market Integration 08/12/2015 Begin*/
	public List<RequestKindType> getOutStandingRequestTypesForPanel(
			Class panelClass, long panelOID) {
		List<RequestKindType> ret = new ArrayList<RequestKindType>();

		RequestKindType[] kinds = PanelToRequestMapping
				.getRequestKindsForPanel(panelClass);
		if (kinds == null) {
			return Collections.EMPTY_LIST;
		}

		try {
			List<IRequestEnquiryRow> requests = Collections.EMPTY_LIST;
			requests = getBasePageGuiController().findRequestsForParty(
					panelOID, RequestStatusType.REQUIRES_AUTHORISATION, kinds);

			for (IRequestEnquiryRow row : requests) {
				ret.add(RequestKindType.getRequestKindTypeForKind(row
						.getRequestKind()));
			}
		} catch (QueryTimeoutException e) {
			// we display error and return all requests, this will mean the
			// screen should be blocked from editing
			error("Could not determine if all requests have been executed for the given panel, the query timed out. Please try again or call support");
			return Arrays.asList(kinds);
		} catch (RequestException e) {
			error(e.getMessage());
			return Arrays.asList(kinds);
		}
		return ret;
	}
	/*SSM2707 SWETA MENON Market Integration 08/12/2015 End*/
	
	/**
	 * <p>Convenience method used to set the currently logged in user  
	 * in the <b>session</b> context.  The context will be set to null if 
	 * nothing is found.</p>
	 * 
	 * <p>The first active agreement that is found for this user
	 * will be set to the active agreement in the context.</p>
	 * 
	 */
	public void setContextToCurrentUser() {
		ISessionUserProfile profile = SRSAuthWebSession.get().getSessionUser();
			if(profile != null){
			/* Initialise beans */
			SRSAuthWebSession session = SRSAuthWebSession.get();
			IContextManagement contextBean = null;
//					(IContextManagement) session
//				.getEJBReference(EJBReferences.CONTEXT_MANAGEMENT);
			
			try {
				contextBean = ServiceLocator.lookupService(IContextManagement.class);
			} catch (NamingException e) {
				throw new CommunicationException(e);
			}
			
			/* Do a search on the current user */
			String userId = session.getCurrentUserid();
	
			if (getLogger().isDebugEnabled()) getLogger().debug("Setting context to current user \"" 
					+ userId + "\"");					
			
			ResultContextSearchDTO searchDTO = 
				contextBean.searchForContext(
						profile,
							AgreementSearchType.SRS_AGREEMENT, 145890L, null);
			
			ResultContextItemDTO selectedDTO = null;
			if (searchDTO.getResultList().size() == 0) {
				// Ensures that the context is not retrieved again for 
				// this session
				logger.warn("Unable to set context to current user");
				session.setContextDTO(null);
				return;
			} 
			
			/* Find the first active agreement */
			for (ResultContextItemDTO itemDTO : searchDTO.getResultList()) {		
				if (itemDTO.getAgreementDTO()!=null
						&& itemDTO.getAgreementDTO().isActive()) {
					selectedDTO = itemDTO;
					break;
				}
			}
			
			/* Ensure one is selected */
			if (searchDTO.getResultList().size() == 1 || selectedDTO == null) {
				selectedDTO = searchDTO.getResultList().get(0); 
			}
			
			ContextDTO contextDTO = contextBean.getContext(selectedDTO.getPartyDTO(), 
					selectedDTO.getAgreementDTO());
			session.setContextDTO(contextDTO);
		}
	}
	
	/**
	 * Sets the context for the page
	 *  
	 * @param contextDTO
	 */
	public void setPageContextDTO(ContextDTO contextDTO) {
		this.pageContextDTO = contextDTO;
		
	}
	
	/**
	 * Get the current page context.
	 * 
	 * @return
	 */
	public ContextDTO getPageContextDTO() {
		return pageContextDTO;
	}
	
	/**
	 * Update the edit state
	 * 
	 * @param newState New state of page
	 * @param target Ajax target
	 */
	public void setEditState(EditStateType newState, AjaxRequestTarget target) {
		this.editState = newState;
		if (target!=null) {
			target.add(menuContainerComponent);
		}
	}
	
	/**
	 * Returns the edit state of this page
	 * 
	 */
	public EditStateType getEditState() {
		return editState;
	}
	
	/**
	 * Return this page's feedback panel
	 * @return
	 */
	public FeedbackPanel getFeedbackPanel() {
		return feedbackPanel;
	}	
	
	/**
	 * Check that the user can actually perform the modify using the rules
	 * @return
	 */
	public boolean checkModificationRules(){
		boolean allowModification = true;		
		ContextDTO context = getPageContextDTO();
		ISessionUserProfile user = SRSAuthWebSession.get().getSessionUser();
		//check what is being modified
		if(getContextTypeRequired() == ContextType.PARTY 
				|| getContextTypeRequired() == ContextType.PARTY_ONLY
				|| getContextTypeRequired() == ContextType.PARTY_ORGANISATION_ONLY
				|| getContextTypeRequired() == ContextType.PARTY_PERSON_ONLY
				|| getContextTypeRequired() == ContextType.PARTY_ORGANISATION_PRACTICE){
			//modifying party details
			ContextPartyDTO party =  context.getPartyContextDTO();
			if(party != null){
				allowModification = getSecurityManagement().canUserModifyPartyDetails(party.getPartyOid(), user);
			}
		}else if(getContextTypeRequired() == ContextType.AGREEMENT 
				|| getContextTypeRequired() == ContextType.AGREEMENT_ONLY){
			//modifying agreement details
			ContextAgreementDTO agmt =  context.getAgreementContextDTO();
			if(agmt != null){
				allowModification = getSecurityManagement().canUserModifyAgreementDetails(agmt.getAgreementNumber(),agmt.getHasHomePartyOid(), user);
			}
		} else if (getContextTypeRequired() == ContextType.FRANCHISE_TEMPLATE){
			allowModification = true;
		}	
		else{
			logger.info("Not applying rules on modification click as the context type could not be determined");
		}			
		return allowModification;
	}
	
	/**
	 * get the SecurityManager bean from the container
	 * @return
	 */
	public ISecurityManagement getSecurityManagement(){
		try {
			return ServiceLocator.lookupService(ISecurityManagement.class);
		} catch (NamingException e) {
			throw new CommunicationException(e);
		}			
	}
	
	/**
	 * get the GuiRequestManagement bean from the container
	 * @return
	 */
	public IBasePageGuiController getBasePageGuiController(){
		if(basePageGuiController == null){
			try {
				basePageGuiController = ServiceLocator.lookupService(IBasePageGuiController.class);
			} catch (NamingException e) {
					throw new CommunicationException(e);
			}		
		}
		return basePageGuiController;
	}
	
	/**
	 * Return an instance of the logger.
	 * 
	 * @return
	 */
	protected Logger getLogger() {
		if (logger==null) {
			logger = Logger.getLogger(this.getClass());
		}
		return logger;
	}
}
