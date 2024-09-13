package za.co.liberty.web.pages;

import javax.servlet.http.HttpServletRequest;

import org.apache.wicket.markup.html.pages.RedirectPage;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.http.WebRequest;

/**
 * Logon page
 *
 */
public class OldSRS extends BasePage {
	
	private static final long serialVersionUID = 1L;

	public OldSRS() {
		
		WebRequest webRequest = (WebRequest) RequestCycle.get().getRequest();
		HttpServletRequest httpServletRequest = ((HttpServletRequest) webRequest.getContainerRequest());
	
		String url = "http://" + httpServletRequest.getServerName() + ":" + httpServletRequest.getServerPort() + "/SRSBusWeb";
		
		logger.info("Redirect to URL :" + url);
		
		setResponsePage(new RedirectPage(url));
		
//		setRedirect(true);
//		getRequestCycle().setRequestTarget(new RedirectRequestTarget("http://" + httpServletRequest.getServerName() + ":" + httpServletRequest.getServerPort() + "/SRSBusWeb"));
//	
		
	}

	@Override
	public String getPageName() {
		
		return "OldSRS";
	}
	
}
