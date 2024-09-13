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

import org.apache.log4j.Logger;
import org.apache.wicket.ConverterLocator;
import org.apache.wicket.IConverterLocator;
import org.apache.wicket.Page;
import org.apache.wicket.RuntimeConfigurationType;
import org.apache.wicket.core.util.file.WebApplicationPath;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.http.WebRequest;
import org.apache.wicket.settings.ResourceSettings;
import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import za.co.liberty.business.userprofiles.IProfileManagement;
import za.co.liberty.common.domain.CurrencyAmount;
import za.co.liberty.common.domain.Percentage;
import za.co.liberty.dto.userprofiles.MenuItemDTO;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.web.pages.MenuContainer;
import za.co.liberty.web.pages.examples.helloworld.HelloWorld;
import za.co.liberty.web.wicket.convert.converters.CurrencyAmountConverter;
import za.co.liberty.web.wicket.convert.converters.CurrencyConverter;
import za.co.liberty.web.wicket.convert.converters.PercentageConverter;
import za.co.liberty.web.wicket.convert.converters.SRSDateConverter;

public class SRSApplicationNonAuth extends WebApplication
//extends AuthenticatedWebApplication 
{
	private static Logger logger = Logger.getLogger(SRSApplicationNonAuth.class);
	
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
	

	@Override
    public void init() {
        super.init();
        
        /*
         * Request Cycle and Error management
         * TODO configure error handling, conversion required.
         */
        getRequestCycleListeners().add(new SRSRequestCycleProcessor());
        
//        getRequestCycleListeners().add(new IRequestCycleListener() {
//			
//			@Override
//			public void onUrlMapped(RequestCycle arg0, IRequestHandler arg1, Url arg2) {
//				// TODO Auto-generated method stub
//				
//			}
//			
//			@Override
//			public void onRequestHandlerScheduled(RequestCycle arg0, IRequestHandler arg1) {
//				// TODO Auto-generated method stub
//				
//			}
//			
//			@Override
//			public void onRequestHandlerResolved(RequestCycle arg0, IRequestHandler arg1) {
//				// TODO Auto-generated method stub
//				
//			}
//			
//			@Override
//			public void onRequestHandlerExecuted(RequestCycle arg0, IRequestHandler arg1) {
//				// TODO Auto-generated method stub
//				
//			}
//			
//			@Override
//			public void onExceptionRequestHandlerResolved(RequestCycle arg0, IRequestHandler arg1, Exception arg2) {
//				// TODO Auto-generated method stub
//				
//			}
//			
//			@Override
//			public IRequestHandler onException(RequestCycle arg0, Exception arg1) {
//				getLogger().info("onException = " + arg1.getMessage());
//				return null;
//			}
//			
//			@Override
//			public void onEndRequest(RequestCycle arg0) {
//				// TODO Auto-generated method stub
//				
//			}
//			
//			@Override
//			public void onDetach(RequestCycle arg0) {
//				// TODO Auto-generated method stub
//				
//			}
//			
//			@Override
//			public void onBeginRequest(RequestCycle arg0) {
//				getLogger().info("onBeginRequest = ");
//				
//			}
//		});


        ResourceSettings resourceSettings = getResourceSettings();
		resourceSettings.getResourceFinders().add(new WebApplicationPath(getServletContext(), "/pages"));
		resourceSettings.getResourceFinders().add(new WebApplicationPath(getServletContext(), "/properties"));
//		
//		
		resourceSettings.setResourceStreamLocator(new PathLocator());
		Logger.getLogger(this.getClass()).info("Initialise PathLocator");
		
    }

//	@Override
//	protected Class<? extends WebPage> getSignInPageClass() {
//		// TODO Will Configure eventually
//		return null;
//	}

//	@Override
//	protected Class<? extends AbstractAuthenticatedWebSession> getWebSessionClass() {
//		// TODO Will configure at later stage, first test without auth
//		return null;
//	}

	@Override
	public Class<? extends Page> getHomePage() {
		return HelloWorld.class;
	}

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
			facade = (IProfileManagement) new InitialContext().lookup("ejblocal:" + IProfileManagement.class.getName());
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
