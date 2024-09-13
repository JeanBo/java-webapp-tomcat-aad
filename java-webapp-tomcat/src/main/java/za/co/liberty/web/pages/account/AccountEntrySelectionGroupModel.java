package za.co.liberty.web.pages.account;

import java.io.Serializable;
import java.util.List;

import za.co.liberty.dto.account.AccountEntryTypeDTO;

public class AccountEntrySelectionGroupModel implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<AccountEntryTypeDTO> allGroupedEntryTypeList;
	private List<AccountEntryTypeDTO> selectedGroupedEntryTypeList;
	
	private String teamName;
	
	

	public String getTeamName() {
		if (teamName == null || teamName == ""){
			teamName = "test";
		}
		return teamName;
	}

	public void setTeamName(String teamName) {
		this.teamName = teamName;
	}

	public List<AccountEntryTypeDTO> getAllGroupedEntryTypeList() {
		return allGroupedEntryTypeList;
	}

	public void setAllGroupedEntryTypeList(
			List<AccountEntryTypeDTO> allGroupedEntryTypeList) {
		this.allGroupedEntryTypeList = allGroupedEntryTypeList;
	}

	public List<AccountEntryTypeDTO> getSelectedGroupedEntryTypeList() {
		return selectedGroupedEntryTypeList;
	}

	public void setSelectedGroupedEntryTypeList(
			List<AccountEntryTypeDTO> selectedGroupedEntryTypeList) {
		this.selectedGroupedEntryTypeList = selectedGroupedEntryTypeList;
	}
	
	
	
	
}
