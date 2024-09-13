package za.co.liberty.web.pages.admin.models;

import java.io.Serializable;
import java.util.List;

import za.co.liberty.dto.userprofiles.RequestCategoryDTO;
import za.co.liberty.dto.userprofiles.TeamDTO;
import za.co.liberty.dto.userprofiles.TeamPartiesDTO;
import za.co.liberty.web.data.pages.IMaintenancePageModel;

public class TeamModel implements Serializable, IMaintenancePageModel<TeamDTO>  {
	
	private static final long serialVersionUID = 1L;
	private TeamDTO selectedItem;
	private List<TeamDTO> selectionList;
	
	private String uacfid;
	private String autoUacfid;
	private List<TeamPartiesDTO> originalTeamPartiesDTO;
	private List<TeamPartiesDTO> availableTeamPartiesDTO;
	
	private List<RequestCategoryDTO> allRequestCategories = null;
	
	private Integer selectedRequestCategory = null;
	private RequestCategoryDTO selectedRequestCategoryDTO;	
	
	
	
	public String getAutoUacfid() {
		return autoUacfid;
	}

	public void setAutoUacfid(String autoUacfid) {
		this.autoUacfid = autoUacfid;
	}

	public String getUacfidAsPartyOID() {
		return uacfid;
	}

	public void setUacfidAsPartyOID(String uacfid) {
		this.uacfid = uacfid;
	}

	public Integer getSelectedRequestCategory() {
		return selectedRequestCategory;
	}

	public void setSelectedRequestCategory(Integer selectedRequestCategory) {
		this.selectedRequestCategory = selectedRequestCategory;
	}

	public RequestCategoryDTO getSelectedRequestCategoryDTO() {
		return selectedRequestCategoryDTO;
	}

	public void setSelectedRequestCategoryDTO(
			RequestCategoryDTO selectedRequestCategoryDTO) {
		this.selectedRequestCategoryDTO = selectedRequestCategoryDTO;
	}

	public List<RequestCategoryDTO> getAllRequestCategories() {
		return allRequestCategories;
	}

	public void setAllRequestCategories(
			List<RequestCategoryDTO> allRequestCategories) {
		this.allRequestCategories = allRequestCategories;
	}

	public List<TeamPartiesDTO> getAvailableTeamPartiesDTO() {
		return availableTeamPartiesDTO;
	}

	public void setAvailableTeamPartiesDTO(
			List<TeamPartiesDTO> availableTeamPartiesDTO) {
		this.availableTeamPartiesDTO = availableTeamPartiesDTO;
	}

	public List<TeamPartiesDTO> getOriginalTeamPartiesDTO() {
		return originalTeamPartiesDTO;
	}

	public void setOriginalTeamPartiesDTO(
			List<TeamPartiesDTO> originalTeamPartiesDTO) {
		this.originalTeamPartiesDTO = originalTeamPartiesDTO;
	}

	public TeamDTO getSelectedItem() {
		return selectedItem;
	}

	public void setSelectedItem(TeamDTO selectedItem) {
		this.selectedItem = selectedItem;
	}

	public List<TeamDTO> getSelectionList() {
		return selectionList;
	}

	public void setSelectionList(List<TeamDTO> selectionList) {
		this.selectionList = selectionList;
	}

}