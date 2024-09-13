package za.co.liberty.web.system;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.validator.routines.DomainValidator;
import org.apache.commons.validator.routines.DomainValidator.ArrayType;
import org.apache.log4j.Logger;
import org.apache.wicket.ConverterLocator;
import org.apache.wicket.IConverterLocator;
import org.apache.wicket.RuntimeConfigurationType;
import org.apache.wicket.Session;
import org.apache.wicket.authroles.authentication.AuthenticatedWebApplication;
import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.apache.wicket.authroles.authorization.strategies.role.metadata.MetaDataRoleAuthorizationStrategy;
import org.apache.wicket.core.util.file.WebApplicationPath;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.Response;
import org.apache.wicket.request.cycle.PageRequestHandlerTracker;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.http.WebRequest;
import org.apache.wicket.settings.ResourceSettings;
import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.wicketstuff.select2.ChoiceProvider;
import org.wicketstuff.select2.JsonResourceReference;

import za.co.liberty.business.userprofiles.IProfileManagement;
import za.co.liberty.common.domain.CurrencyAmount;
import za.co.liberty.common.domain.Percentage;
import za.co.liberty.dto.userprofiles.MenuItemDTO;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.helpers.config.HelperConfigParameterTypes;
import za.co.liberty.helpers.config.HelpersParameterFactory;
import za.co.liberty.helpers.lookup.ServiceLocator;
import za.co.liberty.web.pages.BasePage;
import za.co.liberty.web.pages.Home;
import za.co.liberty.web.pages.Logon;
import za.co.liberty.web.pages.MenuContainer;
import za.co.liberty.web.pages.businesscard.BusinessCardDetailsPage;
import za.co.liberty.web.pages.errors.ConcurrentSessionsForUserPage;
import za.co.liberty.web.pages.errors.ConcurrentSessionsForUserWarningPage;
import za.co.liberty.web.pages.fitprop.FitAndProperExternalPage;
import za.co.liberty.web.pages.test.Country;
import za.co.liberty.web.pages.test.HomePage;
import za.co.liberty.web.wicket.convert.converters.CurrencyAmountConverter;
import za.co.liberty.web.wicket.convert.converters.CurrencyConverter;
import za.co.liberty.web.wicket.convert.converters.PercentageConverter;
import za.co.liberty.web.wicket.convert.converters.SRSDateConverter;

/**
 * Application initialisation for SRS
 * 
 */
public class SRSApplication extends AuthenticatedWebApplication {
	static {System.out.println("### SRSAppWeb - Application Start ###");}
	
	private static final Logger logger = Logger.getLogger(SRSApplication.class);
	
	/* Keeps a hashmap of page panels, this is to make menu items load faster */	
	//private Map<String,List<PagePanelInfoObject>> PANELS_MAP;
	
	/* List of all Panels in the system */
	private HashMap<String,MenuItemDTO> ALL_PANEL_ITEMS = new HashMap<String, MenuItemDTO>();
	
	//private boolean leaveOutUserInSessionCheckForPageInstantiation;
	
	/* Set to true if the pages must be reinitiialized */
	public boolean INTIALIZE_PANEL_ITEMS = true;
	
	private Document menuItemStructureDocument;

	
	private URL imageURL = null;
	
	private String [] allowedDomains;
	
	public static String COUNTRIES_MOUNT_PATH = "countries/";
	
	//private HashMap<String, List<PagePanelInfoObject>> pagePanelsMap = new HashMap<String, List<PagePanelInfoObject>>();

	@Override
	public Class<? extends BasePage> getHomePage() {
		// WebPage subclasses would also suffice
		return Home.class;
	}

	@Override
	protected void init() {
		super.init();
		System.out.println("### INIT ###");
		
		
//		mount
		// mount a countries resource
		mountResource(COUNTRIES_MOUNT_PATH, new JsonResourceReference<Country>("countries")
		{
			private static final long serialVersionUID = 1L;

			@Override
			protected ChoiceProvider<Country> getChoiceProvider() {
				return new HomePage.CountriesProvider();
			}
		});

		/* Configure resource locations etc. */		
		ResourceSettings resourceSettings = getResourceSettings();
		resourceSettings.getResourceFinders().add(new WebApplicationPath(getServletContext(), "/pages"));
		resourceSettings.getResourceFinders().add(new WebApplicationPath(getServletContext(), "/properties"));
		
		resourceSettings.setResourceStreamLocator(new PathLocator());
		Logger.getLogger(this.getClass()).info("Initialise PathLocator");

		mountPage("/businesscard/BusinessCardDetailsPage", BusinessCardDetailsPage.class);
		
		mountPage("/FitAndProper/FitAndProperPage", FitAndProperExternalPage.class);
		
		mountPage("/block/ConcurrentSessionsForUser", ConcurrentSessionsForUserPage.class);
		
		mountPage("/block/ConcurrentSessionsForUserWarning", ConcurrentSessionsForUserWarningPage.class);
		
		getRequestCycleListeners().add(new PageRequestHandlerTracker());
		getRequestCycleListeners().add(new SRSRequestCycleProcessor());
		
		/* Configure roles/authorisations etc. */
		// RoleAuthorizationStrategy authorizationStrategy = new
		// RoleAuthorizationStrategy(new RoleCheckingStrategy());
		MetaDataRoleAuthorizationStrategy authorizationStrategy = new MetaDataRoleAuthorizationStrategy(
				new RoleCheckingStrategy());
		getSecuritySettings().setAuthorizationStrategy(authorizationStrategy);
		
			
		//TODO jzb0608 error handling, does it influence?????
//		getApplicationSettings().setInternalErrorPage(SystemError.class);
//		getApplicationSettings().setAccessDeniedPage(SystemError.class);
		
		//ZZT2108: Register additional domain names for email validation
		allowedDomains = getAllowedDomainNames();
		if (allowedDomains != null && allowedDomains.length > 0) {
			DomainValidator.updateTLDOverride(ArrayType.GENERIC_PLUS, allowedDomains);
		}
		
		if (isDevelopmentMode()) {
//			resourceSettings.setResourcePollFrequency(Duration.))
//			resourceSettings.setResourcePollFrequency(Duration.ONE_WEEK);
			getDebugSettings().setAjaxDebugModeEnabled(true);
//			logger.getLogger("org.apache.wicket.util").setLevel(Level.DEBUG);
		}	
		
		logger.info("#JB - Cleanup filter for application " + getApplicationSettings().getFeedbackMessageCleanupFilter().getClass());
		 
	}
	
	
	
//	@Override
//	public void sessionDestroyed(String sessionId) {
//		logger.info("#JB - SESSION DESTROYED " + sessionId);
//		super.sessionDestroyed(sessionId);
//	}

//	@Override
//	public RuntimeConfigurationType getConfigurationType() {
//		//#WICKETFIX #WICKETTEST Change this to check for prod and switch on/off.
//		return RuntimeConfigurationType.DEVELOPMENT;
//	}

	/**
	 * Reads a comma separated list of domain names configured in LDAP
	 * @return {@link String[]} : array of domain names
	 */
	private String[] getAllowedDomainNames() {

		if (allowedDomains == null) {
			String domains = HelpersParameterFactory.getInstance().getParameter(HelperConfigParameterTypes.ALLOWED_EMAIL_DOMAINS, String.class);

			if (domains != null && !domains.trim().equals("")) {
				
				allowedDomains = domains.split(",");

			}
		}
		return allowedDomains;
	}

	@Override
	public Session newSession(Request request, Response response) {
		SRSAuthWebSession session = new SRSAuthWebSession(this, request);
		return session;
	}

	@Override
	protected Class<? extends WebPage> getSignInPageClass() {

		return Logon.class;
	}

	@Override
	protected Class<? extends AuthenticatedWebSession> getWebSessionClass() {
		return SRSAuthWebSession.class;
	}

//	@Override
//	protected IRequestCycleProcessor newRequestCycleProcessor() {
//		return new SRSRequestCycleProcessor();
//	}

	/**
	 * Return the host name of the server
	 * 
	 * @return
	 */
	public String getServerHostName() {
		WebRequest webRequest = (WebRequest) RequestCycle.get().getRequest();
		HttpServletRequest httpServletRequest = ((HttpServletRequest) webRequest.getContainerRequest());
		return httpServletRequest.getServerName();
	}

	/**
	 * Returns a url for the images folder
	 * @return
	 * @throws MalformedURLException
	 */
	public URL getImageURL() throws MalformedURLException {
		//Loading the URL for images	
		if(imageURL == null){			
			imageURL = getServletContext().getResource("/images/");			
		}
		return imageURL;			
	}

	/**
	 * Get the base url for the application i.e. http://server/SRSAppWeb
	 * 
	 * @return
	 */
	public String getServerBaseUrl() {
		WebRequest webRequest = (WebRequest) RequestCycle.get().getRequest();
		HttpServletRequest httpServletRequest = ((HttpServletRequest) webRequest.getContainerRequest());

		StringBuilder sb = new StringBuilder();
		sb.append(httpServletRequest.getScheme());
		sb.append("://");
		sb.append(httpServletRequest.getServerName());
		if (httpServletRequest.getServerPort() != 80) {
			sb.append(":");
			sb.append(httpServletRequest.getServerPort());
		}
		sb.append("/");
		sb.append(httpServletRequest.getContextPath());

		return sb.toString();
	}

	public Object getSystemResource(String resourceName) {

		return null;
	}

	public Object getDataSource(String datasourceName) {

		return null;
	}

	public Object getEJBResource(String datasourceName) {

		return null;
	}

	public Object getJMSResource(String connectionfactoryName) {

		return null;
	}

	public Logger getLogger() {
		return logger;
	}
	
	/**
	 * <p>Will return the current path</p>
	 * <p>i.e if you ar in http://localhost/SRSAppWeb/app/Logon
	 * You will be returned back /app/Logon</p>
	 * @return The current path
	 */
	public static String getCurrentPath(){
		WebRequest webRequest = (WebRequest) RequestCycle.get().getRequest();
		HttpServletRequest httpServletRequest = ((HttpServletRequest) webRequest.getContainerRequest());
		return httpServletRequest.getPathInfo();
	}
	
	/**
	 * <p>Will return back the image folder based on your current path</p>
	 <p>i.e if you ar in http://localhost/SRSAppWeb/app/Logon
	 * You will be returned back ../../images as images are located in the root/images folder</p>
	 * @return
	 */
	public static String getPathToImageFolder(){
		String images = "images";
		String currentPath = getCurrentPath();
		String[]split = currentPath.split("/");	
		for(String folder : split){
			String trimmedFolder = folder.trim();
			if(!trimmedFolder.equals("")){
				//we have a legitimate folder, now we go back one
				images = "../" + images;
			}
		}
		return images;
	}

	/**
	 * The default converters are loaded here
	 */
	@Override
	protected IConverterLocator newConverterLocator() {
		ConverterLocator converterLocator = new ConverterLocator();
		converterLocator.set(Date.class, new SRSDateConverter());
		converterLocator.set(java.sql.Date.class, new SRSDateConverter());
		converterLocator.set(BigDecimal.class, new CurrencyConverter());
		converterLocator.set(Percentage.class, new PercentageConverter());
		converterLocator.set(CurrencyAmount.class, new CurrencyAmountConverter());
		
		return converterLocator;
	}
	
	/**
	 * Returns true if the application is running in Development mode
	 * 
	 * @return
	 */
	public boolean isDevelopmentMode() {
		return (RuntimeConfigurationType.DEVELOPMENT == this.getConfigurationType());	
	}
	
	/**
	 * Initialize all the panels
	 *
	 */
	private void initializeAllPagesPanelsMap(){		
		IProfileManagement facade;
		try {
		//	facade = (IProfileManagement) new InitialContext().lookup("ejblocal:" + IProfileManagement.class.getName());
// #WICKETTEST #WICKETFIX		
//MSK#Change
						facade = (IProfileManagement)ServiceLocator.lookupService(IProfileManagement.class);
			List<MenuItemDTO> menuItems = facade.findAllMenuItems();
			ALL_PANEL_ITEMS = new HashMap<String, MenuItemDTO>(menuItems.size());
			for(MenuItemDTO item : menuItems){
				if(item.isPanel()){
					ALL_PANEL_ITEMS.put(item.getImplClazz(), item);
				}
			}
		} catch (NamingException e) {
			throw new CommunicationException("Could not initializeAllPagesPanelsMap",e);
		}
		INTIALIZE_PANEL_ITEMS = false;
	}
	
	/**
	 * Returns the origional panel configuration
	 * @param panelClass
	 * @return
	 */
	public MenuItemDTO getPanelConfig(Class panelClass){
		if(panelClass != null){	
			if(ALL_PANEL_ITEMS == null || INTIALIZE_PANEL_ITEMS){
				initializeAllPagesPanelsMap();
			}
			return ALL_PANEL_ITEMS.get(panelClass.getName());
		}
		return null;
	}

	
	/**
	 * Load the menu item structure document
	 *
	 */
	public synchronized Document getMenuItemDocument(){
		if(menuItemStructureDocument == null){
			SAXBuilder builder = new SAXBuilder();
			try {
				menuItemStructureDocument = builder.build(MenuContainer.class.getResourceAsStream("menu_config.xml"));				
			} catch (JDOMException | IOException e) {
				e.printStackTrace();
				menuItemStructureDocument = null;
			}			
		}
		return menuItemStructureDocument;
	}
	
	/**
	 * Returns the relative path to the Request Action page in SRSBusWeb
	 * 
	 * @return
	 */
	public static String getBusWebRequestActionPath() {
		return "/SRSBusWeb/secure/onlineenquiries/RequestStatus.do";
	}
	
	/**
	 * Returns the relative path to the SRSBusWeb project.
	 * 
	 * @return
	 */
	public static String getBusWebPath() {
		return "/SRSBusWeb";
	}
}
