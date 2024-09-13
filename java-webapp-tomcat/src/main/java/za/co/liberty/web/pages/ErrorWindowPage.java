package za.co.liberty.web.pages;

import za.co.liberty.web.models.ErrorModel;

/**
 * The error page for the system.  Does not show the menu items on the left.
 * 
 * @author JZB0608 - 05 Sep 2008
 *
 */
public class ErrorWindowPage extends BaseWindowPage {

	private static final long serialVersionUID = -4189123657138892555L;

	protected ErrorModel errorModel;
	
	/**
	 * Default constructor 
	 * 
	 * @param errorModel
	 */
	public ErrorWindowPage(ErrorModel errorModel) {
		this.errorModel = errorModel;
		add(new ErrorPanel("errorPanel", errorModel));
	}
	
	@Override
	public String getPageName() {
		return "Error Page";
	}

//	@Override
//	protected boolean isShowFeedBackPanel() {
//		return false;
//	}

}
