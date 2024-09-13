/**
 * 
 */
package za.co.liberty.web.pages.errors;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.http.WebRequest;

import za.co.liberty.helpers.util.DateUtil;
import za.co.liberty.web.system.SRSAuthWebSession;

/**
 * This page is the base page most errors are routed to.
 * 
 * The page Shows a red block at the top with two messages
 *  <br/> Line 1 - Bold text indicating error
 *  <br/> Line 2 - Normal text, showing timestamp and what action was taken with error
 *  
 * In addition there are additional panels of why, what, what2 (additional what message).  
 * The panels may be hidden and messages can be customised
 * 
 * @author jzb0608
 *
 */
public abstract class ErrorBasePage extends WebPage {
	
	private static final long serialVersionUID = 2L;
	
	private String userId;
	
	protected WebMarkupContainer whyContainer;
	protected WebMarkupContainer whatContainer;
	protected WebMarkupContainer what2Container;
	
	/**
	 * Default constructor 
	 * 
	 */
	public ErrorBasePage() {
		userId = SRSAuthWebSession.get().getCurrentUserid();
		if (userId==null || userId.trim().length()==0) {
			/*
			 *  Might have been routed from BusWeb, get the user from the
			 *  request.  We don't want to create a new Session for this
			 */
			RequestCycle cycle =RequestCycle.get();
			if (cycle!=null && cycle.getRequest()!=null) {
				WebRequest request =  (WebRequest) RequestCycle.get().getRequest();
				
				WebRequest webRequest = (WebRequest) RequestCycle.get().getRequest();
				HttpServletRequest httpServletRequest = ((HttpServletRequest) webRequest.getContainerRequest());
				
				if (request != null && ((HttpServletRequest) webRequest.getContainerRequest()) !=null
						&& ((HttpServletRequest) webRequest.getContainerRequest()).getUserPrincipal()!=null) {
					HttpServletRequest httpRequest = ((HttpServletRequest) webRequest.getContainerRequest());
					userId = httpRequest.getUserPrincipal().getName();
					if (userId!=null) {
						userId = userId.toUpperCase().trim();
					}
				}
			}
		}
		
		add(new Label("mainError", new Model(
				getMainErrorText())));
		add(new Label("additionalError", new Model(
				getAdditionalErrorMessageTimePart() +
				getAdditionalErrorMessage())));
		
		add(whyContainer = createWhyContainer("whyContainer"));
		add(whatContainer = createWhatContainer("whatContainer"));		
		add(what2Container = createWhat2Container("what2Container"));
		
		// Called after the constructor
		init();
	}


	/**
	 * Called after the constructor is complete
	 */
	public abstract void init();
	
	/**
	 * Main error text
	 * 
	 * @return
	 */
	public abstract String getMainErrorText();

	/**
	 * Get the first part of the additional error message, usually time.
	 * GetMainErrorText() is appended to this 
	 *
	 * 
	 * @return
	 */
	public String getAdditionalErrorMessageTimePart() {
		return 	DateUtil.getInstance().getNewDateTimeFormat().format(
				new Date(System.currentTimeMillis()))
				+ " - ";
	}

	/**
	 * Get the last part of the additional error message
	 * 
	 * @return
	 */
	public String getAdditionalErrorMessage() {
		return "Access to the application has been blocked";
	}
	
	
	
	protected abstract String getWhyLabelText();
	
	protected abstract String getWhatLabelText();
	
	protected abstract String getWhat2LabelText();
	
	/**
	 * Create the WHY container
	 * 
	 * @param id
	 * @return
	 */
	protected WebMarkupContainer createWhyContainer(String id) {
		WebMarkupContainer tmp = new WebMarkupContainer(id);
		Label tmpLable = new Label("whyLabel",	new Model(
				getWhyLabelText()));
		tmp.add(tmpLable);
		return tmp;
	}

	/**
	 * Create the WHAT container
	 * 
	 * @param id
	 * @return
	 */
	protected WebMarkupContainer createWhatContainer(String id) {
		WebMarkupContainer tmp = new WebMarkupContainer(id);
		Label tmpLable = new Label("whatLabel",	new Model(
				getWhatLabelText()));
		tmp.add(tmpLable);
		return tmp;
	}
	
	/**
	 * Create the WHAT2 container
	 * 
	 * @param id
	 * @return
	 */
	protected WebMarkupContainer createWhat2Container(String id) {
		WebMarkupContainer tmp = new WebMarkupContainer(id);
		Label tmpLable = new Label("what2Label",	new Model(
				getWhat2LabelText()));
		tmp.add(tmpLable);
		tmp.add(new Label("what2QuestionLabel", new Model(getWhat2QuestionLabelText())));
		return tmp;
	}


	/**
	 * Question label for what2 section
	 * 
	 * @return
	 */
	public String getWhat2QuestionLabelText() {
		return "What should I do if this keeps happening";
	}


	/**
	 * Get the user id.  System might use user as specified on request if there
	 * is no session.
	 * 
	 * @return
	 */
	public String getUserId() {
		return userId;
	}
	
}
