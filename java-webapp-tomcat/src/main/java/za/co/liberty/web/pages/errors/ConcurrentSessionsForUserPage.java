/**
 * 
 */
package za.co.liberty.web.pages.errors;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.http.WebRequest;

import za.co.liberty.helpers.util.DateUtil;
import za.co.liberty.web.system.SRSAuthWebSession;

/**
 * This page is routed to when dual sessions are encountered.
 * 
 * <b>There is a link to this page from the old GUI which must be updated
 * if the name or package is changed (SRSAuthenticationFilter)</b>
 * 
 * @author jzb0608
 *
 */
public class ConcurrentSessionsForUserPage extends WebPage {
	private static final long serialVersionUID = 2L;
	
	/**
	 * Default constructor 
	 * 
	 */
	public ConcurrentSessionsForUserPage() {
		String userId = SRSAuthWebSession.get().getCurrentUserid();
		if (userId==null || userId.trim().length()==0) {
			/*
			 *  Might have been routed from BusWeb, get the user from the
			 *  request.  We don't want to create a new Session for this
			 */	
			RequestCycle cycle =RequestCycle.get();
			if (cycle!=null && cycle.getRequest()!=null) {
				WebRequest request =  (WebRequest) RequestCycle.get().getRequest();
				if (request != null && request.getContainerRequest() !=null
						&& ((HttpServletRequest)request.getContainerRequest()).getUserPrincipal()!=null) {
					HttpServletRequest httpRequest = (HttpServletRequest) request.getContainerRequest();
					userId = httpRequest.getUserPrincipal().getName();
					if (userId!=null) {
						userId = userId.toUpperCase().trim();
					}
				}
			}
		}
		add(new Label("userId", new Model(
				userId)));
		add(new Label("timestamp", new Model(
				DateUtil.getInstance().getNewDateTimeFormat().format(new Date(System.currentTimeMillis())))));
	}
	

	
}
