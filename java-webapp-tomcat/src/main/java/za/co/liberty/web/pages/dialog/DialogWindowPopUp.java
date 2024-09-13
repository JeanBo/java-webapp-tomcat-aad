package za.co.liberty.web.pages.dialog;

import java.awt.Dimension;
import java.io.Serializable;

import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow.MaskType;

import za.co.liberty.dto.agreement.maintainagreement.WorkflowDTO;
import za.co.liberty.web.data.enums.ContextType;

/**
 * Generic dialog panel that has a context panel and button panel at the bottom.  
 * 
 * @author JZB0608 - Oct 2014
 *
 */
public abstract class DialogWindowPopUp implements Serializable {
	
	/* Constants */
	private static final long serialVersionUID = 1;
	
	/* Attributes */
	private boolean isSaveCookie;
	private DialogWindowPage searchPage = null;
	
	
	/**
	 * Default constructor.  Cookies are not saved 
	 */
	public DialogWindowPopUp() {
		this(false);
	}
	
	/**
	 * If true a cookie will be saved holding re-size info for window.
	 * 
	 * @param saveCookie
	 */
	public DialogWindowPopUp(boolean saveCookie) {
		this.isSaveCookie = saveCookie;
	}
	
	/**
	 * Type of context search required.
	 * 
	 * @return
	 */
	public ContextType getContextType() {
		return ContextType.NONE;
	}
	
	/**
	 * Called after a selection was made and the window closed
	 * 
	 * @param target
	 * @param selectedItemList
	 */
	public abstract void processAnswer(AjaxRequestTarget target,Boolean answer);
	
	/**
	 * The title of the pop-up window.  A default is provided.
	 * 
	 * @return
	 */
	public String getWindowTitle() {
		return "Dialog";
	}
	
	/**
	 * Name of the cookie.  A default is provided.  Note that all windows
	 * that have the same cookie name will have the same size information
	 * stored.
	 * 
	 * @return
	 */
	public String getCookieName() {
		return "DIALOG_CONTEXT_WINDOW" + this.hashCode();
	}
	
//	/** 
//	 * Get the options
//	 * 
//	 * @return
//	 */
//	public ContextSearchPageOptions getSearchPageOptions() {
//		return new ContextSearchPageOptions(getWindowTitle());
//	}
	
	/**
	 * Create the search page. Override this if you 
	 * want to pass different options or create a different
	 * search page.
	 * 
	 * @param window
	 * @return
	 */
	public DialogWindowPage createSearchPage(ModalWindow window) {
		return new DialogWindowPage(window, new WorkflowDTO()) {

			@Override
			public void processClose(AjaxRequestTarget target,
					boolean finishedSuccessfully) {		
				DialogWindowPopUp.this.processAnswer(target, finishedSuccessfully);
			}
			
			@Override
			public String getPageName() {
				return getWindowTitle();
			}

			@Override
			public String getDailogMessage() {
				return getDialogMessage();
			}
			
			@Override
			public boolean isShowFeedBackPanel() {
				return false;
			}
		};
		// remember to change this to getOptions
	}
	
	public abstract String getDialogMessage();
	
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
				return searchPage = createSearchPage(window);
			}
		});
		
		// Close window call back
		window.setWindowClosedCallback(new ModalWindow.WindowClosedCallback() {
			private static final long serialVersionUID = 1L;
			
			public void onClose(AjaxRequestTarget target) {
				System.out.println("WindowsClosedCallBack");
				processAnswer(target,searchPage.getAnswer());
			}
			
		});

		// Initialise window settings
		Dimension initD = getInitialWindowDimension();
		Dimension minD = getMinimumWindowDimension();
		window.setMinimalHeight( minD.height);
		window.setInitialHeight( initD.height);
		window.setMinimalWidth( minD.width);
		window.setInitialWidth( initD.width);
		window.setMaskType(MaskType.SEMI_TRANSPARENT);
		window.setCssClassName(ModalWindow.CSS_CLASS_GRAY);
		return window;
	}

	public Dimension getMinimumWindowDimension() {
		return new Dimension(200, 100 );
	}
	
	public Dimension getInitialWindowDimension() {
		return new Dimension(300, 180 );
	}

}