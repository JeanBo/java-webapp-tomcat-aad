package za.co.liberty.web.pages.maintainagreement.model;

import java.io.Serializable;

import za.co.liberty.dto.agreement.maintainagreement.WorkflowDTO;
import za.co.liberty.web.data.pages.IModalMaintenancePageModel;

/**
 * Data Model used to pass and retrieve data for the Workflow dialog Modification Popup 
 * 
 * @author JZB0608
 *
 */
public class WorkflowDialogPopupModel implements 
		IModalMaintenancePageModel<WorkflowDTO>, Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private WorkflowDTO selectedItem;
	private boolean success = false;
	
	@Override
	public void setSelectedItem(WorkflowDTO selected) {
		selectedItem = selected;
	}

	@Override
	public WorkflowDTO getSelectedItem() {
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
