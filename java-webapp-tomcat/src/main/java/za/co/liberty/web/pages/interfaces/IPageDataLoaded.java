package za.co.liberty.web.pages.interfaces;

/**
 * Will have contain values that can be adjusted based on the Page data loads<br/>
 * if isPageDataLoaded returns tru then all data for a page, after a lazy load, has been loaded
 * @author DZS2610
 *
 */
public interface IPageDataLoaded {

	public boolean isPageDataLoaded();
	
	public void setPageDataLoaded(boolean pageDataLoaded);
}
