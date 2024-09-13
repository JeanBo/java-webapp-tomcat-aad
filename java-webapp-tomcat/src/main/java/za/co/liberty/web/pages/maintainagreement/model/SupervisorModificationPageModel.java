package za.co.liberty.web.pages.maintainagreement.model;

import java.util.ArrayList;
import java.util.List;

import za.co.liberty.dto.agreement.AgreementRoleDTO;
import za.co.liberty.dto.party.fais.FAISLicenseCategoryDTO;
import za.co.liberty.web.data.pages.IModalMaintenancePageModel;

/**
 * This page model is used to pass values to and from the Supervisor Page.   It is a bit 
 * different from other pages as it uses a list of values.
 * 
 * In this case a full list of agreement role values are provided but only those of a specific type is 
 * modified.
 * 
 * @author JZB0608
 *
 */
public class SupervisorModificationPageModel implements IModalMaintenancePageModel<List<AgreementRoleDTO>> {
	
	private static final long serialVersionUID = 1L;
	private List<AgreementRoleDTO> selectedItem;
	private boolean success = false;
	private long typeOID;
	
	@Override
	public void setSelectedItem(List<AgreementRoleDTO> selected) {
		selectedItem = selected;
	}

	@Override
	public List<AgreementRoleDTO> getSelectedItem() {
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
	
	
	
	public long getTypeOID() {
		return typeOID;
	}

	public void setTypeOID(long typeOID) {
		this.typeOID = typeOID;
	}

	/**
	 * Return a list of all the roles not of the type sent in
	 * @return
	 */
	public List<AgreementRoleDTO> splitOutRolesNotOfType(List<AgreementRoleDTO> currentAndFutureSupervisionRoles, long typeOID){
		List<AgreementRoleDTO> splitRoles = new ArrayList<AgreementRoleDTO>();
		for (AgreementRoleDTO roleDTO : currentAndFutureSupervisionRoles) {
			if(roleDTO.getType().longValue() != typeOID)
			{
				splitRoles.add(roleDTO);
			}
		}
		return splitRoles;
	}
}
