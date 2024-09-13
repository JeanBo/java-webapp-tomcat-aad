package za.co.liberty.web.pages.account;

import java.io.Serializable;
import java.util.List;

import za.co.liberty.dto.account.AccountEntryTypeDTO;

public class AccountEntrySelectionAllModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<AccountEntryTypeDTO> allAccountEntryTypeList;
	private List<AccountEntryTypeDTO> selectedAccountEntryTypeList;

	public List<AccountEntryTypeDTO> getAllAccountEntryTypeList() {
		return allAccountEntryTypeList;
	}

	public void setAllAccountEntryTypeList(
			List<AccountEntryTypeDTO> allAccountEntryTypeList) {
		this.allAccountEntryTypeList = allAccountEntryTypeList;
	}

	public List<AccountEntryTypeDTO> getSelectedAccountEntryTypeList() {
		return selectedAccountEntryTypeList;
	}

	public void setSelectedAccountEntryTypeList(
			List<AccountEntryTypeDTO> selectedAccountEntryTypeList) {
		this.selectedAccountEntryTypeList = selectedAccountEntryTypeList;
	}
	
	
}
