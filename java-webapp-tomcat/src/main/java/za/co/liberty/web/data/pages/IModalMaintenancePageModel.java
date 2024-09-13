package za.co.liberty.web.data.pages;

import java.io.Serializable;

/**
 * Used in conjunction with SRSModalWindow and wizards.
 * 
 * @author JZB0608
 *
 * @param <T>
 */
public interface IModalMaintenancePageModel <T> extends Serializable {
	/**
	 * Set the currently selected item
	 * 
	 * @param selected
	 */
	public void setSelectedItem(T selected);
	
	/**
	 * Get the currently selected item
	 * @return
	 */
	public T getSelectedItem();
	
	/**
	 * Is the call a success
	 * 
	 * @return
	 */
	public boolean isModalWizardSucces();
	

	/**
	 * Set if the call was a success 
	 * 
	 * @param success
	 */
	public void setModalWizardSuccess(boolean success);
	
	/**
	 * Message to show after the modal window closes, null if nothing should be shown.
	 * 
	 * @return
	 */
	public String getModalWizardMessage();


}
