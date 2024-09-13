package za.co.liberty.web.pages.maintainagreement.model;

import java.io.Serializable;

import za.co.liberty.dto.agreement.IncentiveDetailDTO;
import za.co.liberty.web.data.pages.IModalMaintenancePageModel;

/**
 * Data Model used to pass and retrieve data for the Incentive Modification Popup 
 * 
 * @author JZB0608
 *
 */
public class IncentiveModificationPopupModel implements 
		IModalMaintenancePageModel<IncentiveDetailDTO>, Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private IncentiveDetailDTO selectedItem;
	private boolean success = false;
	
	@Override
	public void setSelectedItem(IncentiveDetailDTO selected) {
		selectedItem = selected;
	}

	@Override
	public IncentiveDetailDTO getSelectedItem() {
		return selectedItem;
	}

	@Override
	public boolean isModalWizardSucces() {
		return success;
	}

	@Override
	public void setModalWizardSuccess(boolean success) {
		this.success = success;
	}

	@Override
	public String getModalWizardMessage() {
		return null;
	}
	
	
};
