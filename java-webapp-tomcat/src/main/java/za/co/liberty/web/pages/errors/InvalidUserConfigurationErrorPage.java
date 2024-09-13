/**
 * 
 */
package za.co.liberty.web.pages.errors;

/**
 * This page is routed to when the user is invalid i.e. not configured on the
 * SRS database
 * 
 * @author jzb0608
 *
 */
public class InvalidUserConfigurationErrorPage extends ErrorBasePage {
	
	private static final long serialVersionUID = 2L;

	@Override
	public void init() {
		what2Container.setVisible(false);
	}

	@Override
	public String getMainErrorText() {
		return "Incomplete user configuration detected for user - " + getUserId();
	}
	
	@Override
	protected String getWhyLabelText() {
		return "The user id used is not linked to any personality records in the database.";
	}

	@Override
	protected String getWhatLabelText() {
		return "Follow the approriate process to get access to the SRS application.";
	}

	@Override
	protected String getWhat2LabelText() {
		return "";
	}

	
			
}
