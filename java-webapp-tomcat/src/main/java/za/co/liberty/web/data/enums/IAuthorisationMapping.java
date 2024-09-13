package za.co.liberty.web.data.enums;

import za.co.liberty.web.pages.request.BaseRequestViewAndAuthorisePanel;

/**
 * Interface exposing a method to retrieve an authorisation panel class.
 * 
 * @author JZB0608
 *
 */
public interface IAuthorisationMapping {

	public Class<? extends BaseRequestViewAndAuthorisePanel> getAuthorisationPanelClass();
	
}
