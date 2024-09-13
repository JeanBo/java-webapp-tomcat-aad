/**
 * 
 */
package za.co.liberty.web.pages.transactions.model;

import java.io.Serializable;

import za.co.liberty.dto.transaction.VEDTransactionDTO;

/**
 * Data model for 
 * @author JZB0608
 *
 */
public class VEDTransactionModel implements Serializable {

	private static final long serialVersionUID = 5506237907805524162L;
	
	private VEDTransactionDTO selectedObject;

	public VEDTransactionDTO getSelectedObject() {
		return selectedObject;
	}

	public void setSelectedObject(VEDTransactionDTO selectedObject) {
		this.selectedObject = selectedObject;
	}

}
