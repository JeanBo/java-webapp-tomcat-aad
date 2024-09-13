package za.co.liberty.web.pages;

import za.co.liberty.web.models.ErrorModel;
import za.co.liberty.web.models.ErrorModel.ErrorType;
import za.co.liberty.web.system.SRSAuthWebSession;

/**
 * The error page for the system.  Still shows the menu items on the left.
 * 
 * @author JZB0608 - 02 Sep 2008
 *
 */
public class ErrorPage extends BasePage {

	private static final long serialVersionUID = -4189123657138892557L;

	protected ErrorModel errorModel;
	
	// Only used when testing the default screen
	static private ErrorModel getTestModel() {
		ErrorModel m = new ErrorModel();
		m.setApplicationVersion("Version");
		m.setCause(new Throwable());
		m.setErrorMessage("Oh no, stuff happened");
		m.setIncidentReference("Bla die bla bla");
		m.setServerName("Jean's machine");
		m.setTechnicalErrorMessage("Very technical error");
		m.setUserName("SRS1802");
		m.setErrorType(ErrorType.ERROR);
		return m;
	}
	
//	public ErrorPage() {
//		this(getTestModel());
//	}
	/**
	 * Default constructor 
	 * 
	 * @param errorModel
	 */
	public ErrorPage(ErrorModel errorModel) {
		this.errorModel = errorModel;
		
		try {
			add(new ErrorPanel("errorPanel", errorModel));
		} catch (RuntimeException e) {
			logger.error("#JBJBJB - Error page error",e);
			throw e;
		}
	}
	
	@Override
	public String getPageName() {
		return "Error Page";
	}

	@Override
	protected boolean isCheckAuthentication() {
		try {
			return SRSAuthWebSession.get().isAuthenticated();
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	protected void initialise() {
		try {
			super.initialise();
		} catch (Exception e) {
			// Do nothing
		}
	}

	@Override
	protected boolean isShowContextPanel() {
		return false;
	}

	@Override
	protected boolean isShowFeedBackPanel() {
		return false;
	}

	
}
