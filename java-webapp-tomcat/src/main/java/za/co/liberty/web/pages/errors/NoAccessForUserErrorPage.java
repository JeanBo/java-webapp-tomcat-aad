/**
 * 
 */
package za.co.liberty.web.pages.errors;

/**
 * This page is routed to when the user does not have any access configured.
 * 
 * @author jzb0608
 *
 */
public class NoAccessForUserErrorPage extends ErrorBasePage {
	
	private static final long serialVersionUID = 2L;

	@Override
	public void init() {
		what2Container.setVisible(false);
	}

	@Override
	public String getMainErrorText() {
		return "User does not have access to any SRS functions - " + getUserId();
	}
	
	@Override
	protected String getWhyLabelText() {
		return "The user has not been assigned access to any SRS access roles.";
	}

	@Override
	protected String getWhatLabelText() {
		return "Follow the approriate process to get access to the SRS application. \n"
				+ "Or wait for up to 2 hours if access was granted recently";
	}

	@Override
	protected String getWhat2LabelText() {
		return "";
	}

	
			
}
