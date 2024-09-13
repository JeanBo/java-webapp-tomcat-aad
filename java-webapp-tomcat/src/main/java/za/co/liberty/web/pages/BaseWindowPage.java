package za.co.liberty.web.pages;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.Model;

import za.co.liberty.business.guicontrollers.IBasePageGuiController;
import za.co.liberty.dto.userprofiles.SessionUserIdDTO;
import za.co.liberty.exceptions.data.QueryTimeoutException;
import za.co.liberty.exceptions.error.request.RequestException;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.helpers.lookup.ServiceLocator;
import za.co.liberty.interfaces.agreements.requests.RequestKindType;
import za.co.liberty.interfaces.agreements.requests.RequestStatusType;
import za.co.liberty.interfaces.persistence.agreement.request.IRequestEnquiryRow;
import za.co.liberty.web.data.enums.PanelToRequestMapping;
import za.co.liberty.web.system.ConcurrentSessionForUserRuntimeException;
import za.co.liberty.web.system.SRSAuthWebSession;

/**
 * The base page for all pages that are shown in windows/frames/pop-ups.  
 * 
 * @author JZB0608 - 22 May 2008
 * 
 */
public abstract class BaseWindowPage extends WebPage {

	/* Constants */
	private static final long serialVersionUID = 4008008744919434971L;

	/* Attributes */
	private FeedbackPanel feedbackPanel;	
	
	private Long agreementContextId;
	private Long partyContextId;
	private boolean isInitialised = false;
	
	private static Logger logger = Logger.getLogger(BaseWindowPage.class);
	
//	beans
	private transient IBasePageGuiController basePageGuiController;
		
	/**
	 * Default constructor 
	 */
	public BaseWindowPage() {
		System.out.println("BaseWindowPage.Constructor 1");
		/* Page name */
		add(new Label("pageName",new Model() {
			private static final long serialVersionUID = 1L;
			@Override
			public String getObject() {
				return BaseWindowPage.this.getPageName();
			}
		}));
		
		/* Feed back panel */
		/* Configure Message panel */
		if (isShowFeedBackPanel()) {
		  feedbackPanel = new FeedbackPanel("messages");
		  feedbackPanel.setOutputMarkupId(true);
		  add(feedbackPanel);
		} else {
		  add(new EmptyPanel("messages"));
		}
		
		initialise();
	}

	/**
	 * Initialises this object.  
	 * 
	 */
	@SuppressWarnings("unchecked")
	protected void initialise() {
		if (isInitialised) {
			return;
		}
		
		SRSAuthWebSession webSession = SRSAuthWebSession.get();
		
		/* Do authentication */		
		if (isCheckAuthentication()) { 
			
			if (webSession.isAuthenticated()) {
				// Check if this is a concurrent session
				webSession.isConcurrentSession();
			}
		}
				
		isInitialised = true;
	}
	
	
	/**
	 * Return the name of the current web page.
	 * 
	 * @return
	 */
	public abstract String getPageName();

	/**
	 * Indicates whether a feed back panel is shown
	 * on this page.  When false it should be catered
	 * for in the subclass and you should then override 
	 * {@link #getFeedBackPanel()).
	 * 
	 * @return
	 */
	public boolean isShowFeedBackPanel() {
		return true;
	}
	
	/**
	 * The current instance of the feedback panel
	 * @return
	 */
	public FeedbackPanel getFeedBackPanel() {
		return feedbackPanel;
	}

	public Long getAgreementContextId() {
		return agreementContextId;
	}

	public Long getPartyContextId() {
		return partyContextId;
	}

	public void setAgreementContextId(Long agreementContextId) {
		this.agreementContextId = agreementContextId;
	}

	public void setPartyContextId(Long partyContextId) {
		this.partyContextId = partyContextId;
	}
	
	/**
	 * Returns the RequestKindTypes that have unauthorised requests for the panel using the context data
	 * @param panelClass
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<RequestKindType> getOutStandingRequestTypesForPanel(Class panelClass){
		
		List<RequestKindType> ret = new ArrayList<RequestKindType>();
		RequestKindType[] kinds = PanelToRequestMapping.getRequestKindsForPanel(panelClass);
		if(kinds == null){
			return Collections.EMPTY_LIST;
		}
		
		if(agreementContextId == null && partyContextId == null){
			return Collections.EMPTY_LIST;
		}
		boolean lookForAgreementRequests = agreementContextId != null?true:false;
		
		try{
			List<IRequestEnquiryRow> requests = Collections.EMPTY_LIST;
			if(lookForAgreementRequests){
				requests = getBasePageGuiController().findRequestsForAgreement(agreementContextId,RequestStatusType.REQUIRES_AUTHORISATION, kinds);
								
			}else{
				if(partyContextId != null){					
					requests = getBasePageGuiController().findRequestsForParty(partyContextId, RequestStatusType.REQUIRES_AUTHORISATION, kinds);
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
	 * Authentication is checked when this returns true. Defaults to true.
	 * 
	 * @return
	 */
	protected boolean isCheckAuthentication() {
		return true;
	}
}
