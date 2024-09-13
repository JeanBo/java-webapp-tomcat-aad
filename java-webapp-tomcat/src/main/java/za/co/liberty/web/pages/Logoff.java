package za.co.liberty.web.pages;

import javax.servlet.http.HttpServletRequest;

import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.http.WebRequest;
import org.apache.wicket.request.http.handler.RedirectRequestHandler;

import za.co.liberty.web.system.SRSAuthWebSession;

/**
 * Logon page
 *
 */
public class Logoff extends BasePage {
	
	private static final long serialVersionUID = 1L;

	public Logoff() {
		SRSAuthWebSession.get().signOut();
		WebRequest webRequest = (WebRequest) RequestCycle.get().getRequest();
		HttpServletRequest httpServletRequest = ((HttpServletRequest) webRequest.getContainerRequest());
		getRequestCycle().scheduleRequestHandlerAfterCurrent(new RedirectRequestHandler(
				"http://" + httpServletRequest.getServerName() + ":" + httpServletRequest.getServerPort() + "/SRSAppWeb"));
	}

	@Override
	public String getPageName() {
		
		return "Logoff";
	}
	
}
