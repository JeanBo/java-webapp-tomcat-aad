package za.co.liberty.web.pages;

import org.apache.log4j.Logger;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.Panel;

import za.co.liberty.web.system.SRSAuthWebSession;

/**
 * Logon page
 *
 */
public class Logon extends BasePage {
	
	private static final long serialVersionUID = 1L;
	
	private static Logger logger = Logger.getLogger(Logon.class);
	
	public Logon() {
		logger.debug("Logon page");
		if(SRSAuthWebSession.get().isUserAlreadyLoggedOn()) {
			logger.debug("  --Logon page - alreadyLoggedOn");
			setResponsePage(Home.class);	
//			setRedirect(true);
		}
//		if (SRSAuthWebSession.get().isUserUnauthenticated()) {
//			setResponsePage(SecureHome.class);
//			setRedirect(true);
//		}
		
		logger.debug("  --Return empty panel");
		add(new EmptyPanel("logonPanel"));

//		add(new LogonPanel("logonPanel"));
	}	

	@Override
	protected boolean isCheckAuthentication() {
		/* Disable authentication as we are logging on */
		return false;
	}
	
	@Override
	public String getPageName() {
		return "Logon";
	}
	
	@Override
	protected Panel getContextPanel() {
		/* Does not require a panel */
		return new EmptyPanel(CONTEXT_PANEL_NAME);
	}	

}
