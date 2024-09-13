/**
 * 
 */
package za.co.liberty.web.pages.errors;

import java.util.Date;

import za.co.liberty.helpers.util.DateUtil;

/**
 * This page is routed to when the user has a concurrent session but 
 * only warnings are configured and not blocks.
 * 
 * <b>There is a link to this page from the old GUI which must be updated
 * if the name or package is changed (SRSAuthenticationFilter)</b>
 * 
 * @author jzb0608
 *
 */
public class ConcurrentSessionsForUserWarningPage extends ErrorBasePage {
	
	private static final long serialVersionUID = 2L;

	@Override
	public void init() {
		what2Container.setVisible(true);
	}

	/**
	 * Get the first part of the additional error message, usually time.
	 * GetMainErrorText() is appended to this 
	 *
	 * 
	 * @return
	 */
	@Override
	public String getAdditionalErrorMessageTimePart() {
		return 	DateUtil.getInstance().getNewDateTimeFormat().format(
				new Date(System.currentTimeMillis()));
	}

	/**
	 * Get the last part of the additional error message
	 * 
	 * @return
	 */
	@Override
	public String getAdditionalErrorMessage() {
		return "";
	}
	
	@Override
	public String getMainErrorText() {
		return "Warning - Multiple concurrent user sessions were detected for user - " + getUserId();
	}
	
	@Override
	protected String getWhyLabelText() {
		return "The same user is using SRS from multiple machines or multiple " + 
				"browser sessions at the same time.";
	}

	@Override
	protected String getWhatLabelText() {
		return "You may continue with your work by either clicking the back button or by opening the SRS URL again.";
	}

	
	@Override
	public String getWhat2QuestionLabelText() {
		return "What will happen in future";
	}

	@Override
	protected String getWhat2LabelText() {
		return "In future your session will be blocked and you will be forced to re-open your browser to access SRS";
	}

	
			
}
