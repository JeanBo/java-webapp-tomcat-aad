package za.co.liberty.web.data.pages;

/**
 * Has methods to get the current Selected tab
 * One should carry a variable and store the selected tab in it
 * The getCurrentTab should return this variable
 * 
 * @author DZS2610
 *
 */
public interface ITabbedPageModel<T> extends IMaintenancePageModel<T>{

	/**
	 * Return -1 when the default tab  is to be used
	 * @return
	 */
	public int getCurrentTab();	
	
	public void setCurrentTab(int currentTab);
	
	/**
	 * Returns the selected tabs class
	 * @return
	 */
	public Class getCurrentTabClass();
	
	public void setCurrentTabClass(Class currentTabClass);
}
