package za.co.liberty.web.pages.interfaces;

/**
 * This interface should be implemented by Sub-Panels that want to override security
 * provided by the menu configuration.  For example, user has access to page and panel but
 * certain data conditions prevent the user from editing so some buttons need to be disabled.
 * 
 * @author JZB0608 - 29 Mar 2011
 *
 */
public interface IHasAccessPanel {

	/**
	 * True if this page has modify access
	 * @param originalAccess access as defined by menu config
	 * @return
	 */
	public boolean hasModifyAccess(boolean originalAccess);
	
}
