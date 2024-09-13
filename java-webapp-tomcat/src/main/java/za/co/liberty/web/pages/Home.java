package za.co.liberty.web.pages;

import org.apache.log4j.Logger;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxClientInfoBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.protocol.http.ClientProperties;
import org.apache.wicket.protocol.http.request.WebClientInfo;
import org.apache.wicket.request.cycle.RequestCycle;

import za.co.liberty.web.pages.panels.HelperPanel;

/**
 * Represents the default home screen.  
 * 
 * In future we will possibly show news here i.e. new functionality, changes etc.
 * 
 */
public class Home extends BasePage {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(Home.class);

	private String warningMessage;
	
	public Home() {
		this("");
	}
	
	public Home(String warningMessage) {
		this.warningMessage = warningMessage;
		add(createContentPanel("contentPanel"));
		
		
		getApplication().getRequestCycleSettings().setGatherExtendedBrowserInfo(true);
		WebClientInfo w = (WebClientInfo) getSession().getClientInfo();
		getApplication().getRequestCycleSettings().setGatherExtendedBrowserInfo(false);
		
		if (logger.isDebugEnabled())
			logger.debug("On client info called\n" + w.getProperties().toString());
		if (w.getProperties().isBrowserInternetExplorer()) {
			warn("You are using Internet Explorer or Edge with IE compatability mode which is no longer supported on SRS.   "
					+ "You may experience performance and/or other issues on certain pages.");
			warn("For best experience please disable compatability mode in Edge or change to another browser if you are using IE.");
			
		} 
		
//		/*
//		 * This behaviour does an AJAX call after a short period to return client data such as browser info
//		 */
//		add(new AjaxClientInfoBehavior() {
//
////			@Override
////			public void renderHead(Component component, IHeaderResponse response)	{
////				super.renderHead(component, response);
////
////				String script = "Wicket.BrowserInfo.collectExtraInfo = function(info) { info.extendedProperty = 'This property was read extra.'; };";
////
////				response.render(JavaScriptHeaderItem.forScript(script, "extended-client-info"));
////			}
//
////			@Override
////			protected WebClientInfo newWebClientInfo(RequestCycle requestCycle)	{
////				return new WebClientInfo(requestCycle, new ClientProperties());
////			}
//
//			@Override
//			protected void onClientInfo(AjaxRequestTarget target, WebClientInfo webClientInfo)	{
//				logger.info("On client info called\n" + webClientInfo.getProperties().toString());
//				if (webClientInfo.getProperties().isBrowserInternetExplorer()) {
//					warn("You are using Internet Explorer or Edge with IE compatability mode which is no longer supported on SRS.   "
//							+ "You may experience performance and/or other issues on certain pages.");
//					warn("For best experience please disable compatability mode in Edge or change to another browser if you are using IE.");
//					
//				} 
//				target.add(getFeedbackPanel());
//			}
//		});
		
	}
	
	private Component createContentPanel(String id) {
		if (warningMessage!=null) {
			return HelperPanel.getInstance(id, 
					new Label("value", warningMessage).add(new AttributeModifier(
							"class", "feedbackPanelERROR")));
		}
		return new EmptyPanel(id);
	}
	
	@Override
	public String getPageName() {
		return "Home";
	}
	
	@Override
	protected Panel getContextPanel() {				
		/* Does not require a panel */
		return new EmptyPanel(CONTEXT_PANEL_NAME);
	}

}
