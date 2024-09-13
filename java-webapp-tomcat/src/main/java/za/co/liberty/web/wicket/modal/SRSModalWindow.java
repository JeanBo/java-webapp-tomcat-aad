package za.co.liberty.web.wicket.modal;

import java.io.Serializable;
import java.util.Date;
import java.util.Random;

import org.apache.log4j.Logger;
import org.apache.wicket.Page;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.model.IModel;

import za.co.liberty.dto.party.EmployeeDTO;
import za.co.liberty.web.data.pages.IMaintenancePageModel;
import za.co.liberty.web.data.pages.IModalMaintenancePageModel;
import za.co.liberty.web.pages.party.AddPartyWizardPage;
import za.co.liberty.web.system.SRSAuthWebSession;

/**
 * <p>SRS Modal Window is a tool to assist with modal windows and issues relating to the data being sent 
 * and retrieved from the modal window.  As sessions are used that would mean that only one wizard of a 
 * kind can be created at a time.  </p>
 * 
 * <p><b>IMPORTANT USAGE NOTES:</b> This function uses the <b>session</b> to transfer data and special care must be 
 * taken to ensure that we cleanup and manage space in the session.  As Wicket stores it on the file system
 * and it can cause size issues as we are currently experiencing (2022 Wicket 1.4).
 * 
 * <p><b>Usage :</b> The {@linkplain this#setSessionModelForPage(IMaintenancePageModel)} and 
 *  {@linkplain this#getSessionModelForPage()} methods should be called at the appropriate 
 *  place to ensure the PageModel can be sent and retrieved back.  The ModalWindow is 
 *  always passed as a reference and can be used within the Modal page / Wizards.  Suggest it is set 
 *  on createPage if values need to be passed, do not use the constructor of the page you are opening
 *  as you can not get the same object reference back.  Or if no data is set it can be stored when the 
 *  pageModel is initialised.</p>
 *  
 *  <p><b>Close :</b>  The pageModel should implement IModalMaintenancePageModel as this provides
 *  a success indicator to determine if the process was cancelled </p>
 * 
 * @author JZB0608
 *
 * @param <M>
 */
public abstract class SRSModalWindow<M extends IModalMaintenancePageModel, ADD extends Serializable> extends ModalWindow {

	private static final long serialVersionUID = -8586098609201467439L;
	public static final String MODAL_IDENTIFIER = "MODAL.VAL-";
	public static final String ADDITIONAL_IDENTIFIER = ".additional.";
	protected transient Logger transLogger;
	protected static final String ERROR_PARENTPAGE = "The getParentPageID() method must overridden and provide a "
			+ "value when isAllowMultiplePageModels() is true";
	
	
	
	public SRSModalWindow(String id, IModel<?> model) {
		super(id, model);
	}

	public SRSModalWindow(String id) {
		super(id);
		clearModalPageModelInSession();
	}

	/**
	 * Set any additional session model data for the page.  
	 * @param object
	 */
	public void setAdditionalSessionModelForPage(ADD object) {
		SRSAuthWebSession.get().storeAdditionalModalPageModel(getInternalModalSessionIdentifier()+ADDITIONAL_IDENTIFIER, object);
	}
	
	/**
	 * Get any additional session model data for the page.
	 * 
	 * @return
	 */
	public ADD getAdditionalSessionModelForPage() {
		return (ADD) SRSAuthWebSession.get().getAdditionalModalPageModel(getInternalModalSessionIdentifier()+ADDITIONAL_IDENTIFIER);
	}
	
	/**
	 * Set the page model in the session
	 * @param model
	 */
	public void setSessionModelForPage(M model) {
		SRSAuthWebSession.get().storeModalPageModel(getInternalModalSessionIdentifier(), model);
	}
	
	/**
	 * Get the page model in the session
	 */
	public M getSessionModelForPage() {
		return (M) SRSAuthWebSession.get().getModalPageModel(getInternalModalSessionIdentifier());
	}
	
	/**
	 * Do not override this method
	 * 
	 * @return
	 */
	public final String getInternalModalSessionIdentifier() {
		
		String id = MODAL_IDENTIFIER + getModalSessionIdentifier();
		if (isAllowMultiplePageModels()) {
			String pId = getParentPageID();
			if (pId == null || pId.trim().length()==0) {
				getLogger().error(ERROR_PARENTPAGE);
				throw new IllegalAccessError(ERROR_PARENTPAGE);
			}
			id+="_"+pId;
		}
		
		if (getLogger().isDebugEnabled())
			getLogger().debug("Session variable for modal window :" + id);
		return id;
	}
	
	/**
	 * Provide a concrete implementation for this to identify the wizard, should be unique.
	 * 
	 * @return
	 */
	public abstract String getModalSessionIdentifier();
	
	/**
	 * Clear out the session to save file system and memory space.  This includes additional model data
	 * required.
	 */
	public void clearModalPageModelInSession() {
		SRSAuthWebSession.get().clearModalPageModel(getInternalModalSessionIdentifier());
		SRSAuthWebSession.get().clearModalPageModel(getInternalModalSessionIdentifier()+ADDITIONAL_IDENTIFIER);
	}
	
	/**
	 * Must this support multiple usages at the same time for one session.   If this is true then
	 * you must override the getPage
	 * 
	 * @return
	 */
	public boolean isAllowMultiplePageModels() {
		return false;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getParentPageID() {
		return "";
	}
	
	/**
	 * Should the session models expire after some time.
	 * 
	 * @return
	 */
	public boolean isExpireSessionModels() {
		return false;
	}
	
	/**
	 * Return a random number used when pageId is not available.
	 * 
	 * @return
	 */
	public int getRandomId() {
		return new Random(1000).nextInt();
	}
	
	/**
	 * Return the transient logger instance
	 * 
	 * @return
	 */
	protected Logger getLogger() {
		if (transLogger == null) {
			transLogger = Logger.getLogger(this.getClass());
		}
		return transLogger;
	}

}
