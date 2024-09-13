package za.co.liberty.web.pages.search;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.wicket.Page;
import org.apache.wicket.PageReference;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow.MaskType;

import za.co.liberty.dto.gui.context.ResultContextItemDTO;
import za.co.liberty.web.data.enums.ContextType;
import za.co.liberty.web.pages.search.models.ContextSearchPageOptions;

/**
 * Object contains convenience methods for creating a pop-up window
 * for searching the context. 
 * 
 * @author JZB0608 - 29 Jul 2008
 *
 */
public abstract class ContextSearchPopUp implements Serializable {
	
	protected static Logger logger = Logger.getLogger(ContextSearchPopUp.class);
			
	/* Constants */
	private static final long serialVersionUID = 1;
	
	/* Attributes */
	private boolean isSaveCookie;
	private ContextSearchPage searchPage = null;
	private ContextSearchPageOptions searchOptions;
	private PageReference pageReference;
	private ArrayList<ResultContextItemDTO> resultList = new ArrayList<ResultContextItemDTO>();
	
	/**
	 * Default constructor.  Cookies are not saved 
	 */
	public ContextSearchPopUp() {
		this(false);
	}
	
	/**
	 * If true a cookie will be saved holding re-size info for window.
	 * 
	 * @param saveCookie
	 */
	public ContextSearchPopUp(boolean saveCookie) {
		this.isSaveCookie = saveCookie;
	}
	
	/**
	 * Type of context search required.
	 * 
	 * @return
	 */
	public abstract ContextType getContextType();
	
	/**
	 * Called after a selection was made and the window closed
	 * 
	 * @param target
	 * @param selectedItemList
	 */
	public abstract void doProcessSelectedItems(AjaxRequestTarget target,
			ArrayList<ResultContextItemDTO> selectedItemList);
	
	/**
	 * The title of the pop-up window.  A default is provided.
	 * 
	 * @return
	 */
	public String getWindowTitle() {
		return "Search";
	}
	
	/**
	 * Name of the cookie.  A default is provided.  Note that all windows
	 * that have the same cookie name will have the same size information
	 * stored.
	 * 
	 * @return
	 */
	public String getCookieName() {
		return "SEARCH_CONTEXT_WINDOW";
	}
	
	/** 
	 * Get the options
	 * 
	 * @return
	 */
	public ContextSearchPageOptions getSearchPageOptions() {
		if (searchOptions==null) {
			searchOptions = new ContextSearchPageOptions(getWindowTitle());
			searchOptions.setSelectedItemList(resultList);
		}
		return searchOptions;
	}
	
	/**
	 * Create the search page. Override this if you 
	 * want to pass different options or create a different
	 * search page.
	 * 
	 * @param window
	 * @return
	 */
	public ContextSearchPage createSearchPage(ModalWindow window) {
		return new ContextSearchPage(window, getContextType(), 
				getSearchPageOptions());
	}
	
	/**
	 * Create the modal window
	 * 
	 * @param id
	 * @return
	 */
	public ModalWindow createModalWindow(String id) {
		final ModalWindow window = new ModalWindow(id);
		
		window.setTitle(getWindowTitle());
		
		if (isSaveCookie) {
			window.setCookieName(getCookieName());
		}
		
		
		
		// Create the page
		window.setPageCreator(new ModalWindow.PageCreator() {
			private static final long serialVersionUID = 1L;
			public Page createPage() {
				 searchPage = createSearchPage(window);
				 pageReference = searchPage.getPageReference();
				 List list = searchPage.getSelectedItemList();
				 if (logger.isDebugEnabled())
					 logger.debug("#JB1 - createPage + " + searchPage + " " +System.identityHashCode(searchPage)
						 + "    list=" + list + " " +System.identityHashCode(list )
						 + "    optionList= "  + searchOptions.getSelectedItemList() + " " +System.identityHashCode(searchOptions.getSelectedItemList())
						 + "    popUp=" + ContextSearchPopUp.this);
				 
				 return searchPage;
			}
			
			
		});
		
		// Close window call back
		window.setWindowClosedCallback(new ModalWindow.WindowClosedCallback() {
			private static final long serialVersionUID = 1L;
			
			public void onClose(AjaxRequestTarget target) {
				
				List list = null;
				if (logger.isDebugEnabled())
					 logger.debug("#JB1 - onClose + " + searchPage + " " +System.identityHashCode(searchPage)
						+ "  size=" + searchPage.getSelectedItemList().size()
						+ "  searchPage.list=" + searchPage.getSelectedItemList()
						+ "  original.list=" + list+ " " +System.identityHashCode(list )
						+ "    optionList= "  + searchOptions.getSelectedItemList()+ " " +System.identityHashCode(searchOptions.getSelectedItemList())
						+ "    popUp=" + ContextSearchPopUp.this
						+ " pagereference=" + ((ContextSearchPage)pageReference.getPage()).selectedItemList);
				
				afterOnClose(target);
				doProcessSelectedItems(target,((ContextSearchPage)pageReference.getPage()).selectedItemList);
			}
			
		});

		// Initialise window settings
		window.setMinimalHeight(420);
		window.setInitialHeight(520);
		window.setMinimalWidth(750);
		window.setInitialWidth(750);
		window.setMaskType(MaskType.SEMI_TRANSPARENT);
		window.setCssClassName(ModalWindow.CSS_CLASS_GRAY);
		window.setVersioned(false);
		
		return window;
	}
	
	/**
	 * Called after the selection has been processed.
	 * 
	 * @param target
	 */
	public void afterOnClose(AjaxRequestTarget target) {
		
	}

}