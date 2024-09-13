package za.co.liberty.web.pages.advancedPractice.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import za.co.liberty.dto.advancedPractice.AdvancedPracticeDTO;
import za.co.liberty.dto.advancedPractice.AdvancedPracticeDTOGrid;
import za.co.liberty.dto.advancedPractice.AdvancedPracticeManagerDTO;
import za.co.liberty.dto.advancedPractice.AdvancedPracticeMemberDTO;
import za.co.liberty.interfaces.agreements.RoleKindType;
import za.co.liberty.interfaces.agreements.requests.RequestKindType;
import za.co.liberty.web.pages.advancedPractice.AdvancedPracticeStatusType;

public class AdvancedPracticePanelModel implements  Serializable , Cloneable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private AdvancedPracticeStatusType AdvancedPracticeStatusType =  null;
	
	private AdvancedPracticeDTO practiceDTO;
	
	private AdvancedPracticeDTO pastRoles;
	
	private Long praticeOid ;
	
	private boolean existingMaintenanceRequest = false;
	
	//the selectable roles for the specific agreement in the context
	private List<RequestKindType> selectableRoleKinds = new ArrayList<RequestKindType>();
	
	
	private List<AdvancedPracticeDTOGrid> managersGrids = new ArrayList<AdvancedPracticeDTOGrid>();
    private List<AdvancedPracticeDTOGrid> membersGrids  = new ArrayList<AdvancedPracticeDTOGrid>();
    
    
	private List<AdvancedPracticeDTOGrid> managersHistoryGrids = new ArrayList<AdvancedPracticeDTOGrid>();
    private List<AdvancedPracticeDTOGrid> membersHistoryGrids  = new ArrayList<AdvancedPracticeDTOGrid>();
	
	private Long practiceId ;

	public AdvancedPracticePanelModel() {
		
	}
		
	public AdvancedPracticePanelModel(IMaintainAdvancedPracticePageModel pageModel) {
		
			super();
			this.praticeOid = pageModel!=null && pageModel.getAdvancedPracticeDTO()!=null
				?pageModel.getAdvancedPracticeDTO().getOid():null;
			this.practiceDTO = pageModel!=null && pageModel.getAdvancedPracticeDTO()!=null
				?pageModel.getAdvancedPracticeDTO():null;
			this.pastRoles = pageModel!=null && pageModel.getAdvancedPracticeDTOBeforeImage()!=null
				?pageModel.getAdvancedPracticeDTOBeforeImage():null;				

		
	}


	public static long getSerialVersionUID() {
		return serialVersionUID;
	}


	public AdvancedPracticeDTO getAdvancedPracticeDTO() {
		return practiceDTO;
	}


	public void setAdvancedPracticeDTO(AdvancedPracticeDTO practiceDTO) {
		this.practiceDTO = practiceDTO;
	}

 	
	public AdvancedPracticeDTO getPastRoles() {
		return pastRoles;
	}

	public void setPastRoles(AdvancedPracticeDTO pastRoles) {
		this.pastRoles = pastRoles;
	}

	public Long getPracticeId() {
		return practiceId;
	}


	public void setPracticeId(Long practiceId) {
		this.practiceId = practiceId;
	}
	
	public List<AdvancedPracticeDTOGrid> getManagersGrids() {
		return managersGrids;
	}	
	
	
	public List<AdvancedPracticeDTOGrid> getManagersHistoryGrids() {
		return managersHistoryGrids;
	}

	public void setManagersHistoryGrids(
			List<AdvancedPracticeDTOGrid> managersHistoryGrids) {
		this.managersHistoryGrids = managersHistoryGrids;
	}

	public List<AdvancedPracticeDTOGrid> getMembersHistoryGrids() {
		return membersHistoryGrids;
	}

	public void setMembersHistoryGrids(
			List<AdvancedPracticeDTOGrid> membersHistoryGrids) {
		this.membersHistoryGrids = membersHistoryGrids;
	}

	/**
	 * Warning, do not call this method in the gui when working with the roles, rather call the addGridRole</br>
	 * One call should be done to set the grid roles
	 * @param roles
	 */
	public void setManagersGrids(List<AdvancedPracticeDTOGrid> roles) {
		managersGrids = roles;
	}
	
	
	
	public boolean getIsExistingMaintenanceRequest() {
		return existingMaintenanceRequest;
	}

	public void setExistingMaintenanceRequest(boolean existingMaintenanceRequest) {
		this.existingMaintenanceRequest = existingMaintenanceRequest;
	}

	public List<AdvancedPracticeDTOGrid> getMembersGrid() {
		return membersGrids;
	}

	public void setMembersGrid(List<AdvancedPracticeDTOGrid> membersGrid) {
		this.membersGrids = membersGrid;
	}

	public List<RequestKindType> getSelectableRoleKinds() {
		return selectableRoleKinds;
	}

	public void setSelectableRoleKinds(List<RequestKindType> selectableRoleKinds) {
		this.selectableRoleKinds = selectableRoleKinds;
	}

	/**
	 * Call this to add role
	 * @param role
	 */
	public void addGridRole(AdvancedPracticeDTOGrid role){
		if(managersGrids == null){
			managersGrids = new ArrayList<AdvancedPracticeDTOGrid>();			
		}else if(membersGrids == null){
			membersGrids = new ArrayList<AdvancedPracticeDTOGrid>();			
		}			
			
		if(role.getRole().getKind().intValue() == RoleKindType.ISADVANCEDPRACTICEMANAGEROF.getKind()){
			managersGrids.add(role);
		}else if(role.getRole().getKind().intValue() == RoleKindType.ISADVANCEDPRACTICEMEMBEROF.getKind()){
			membersGrids.add(role);
		}

		if(role.getRole()!= null){
			if(role.getRole().getKind().intValue() == RoleKindType.ISADVANCEDPRACTICEMANAGEROF.getKind()){
				practiceDTO.getAdvancedPracticeManagerDTOlist().add((AdvancedPracticeManagerDTO) role.getRole());
			}else if(role.getRole().getKind().intValue() == RoleKindType.ISADVANCEDPRACTICEMEMBEROF.getKind()){
				practiceDTO.getAdvancedPracticeMemberDTOList().add((AdvancedPracticeMemberDTO)role.getRole());
			}	
		}		
		
		

	}
	
	/**
	 * Call this to add role
	 * @param role
	 */
	public void removeGridRole(AdvancedPracticeDTOGrid role){
		if(managersGrids == null){
			return;			
		}else if(membersGrids == null){
			return;				
		}
	
		if(role.getRole().getKind().intValue() == RoleKindType.ISADVANCEDPRACTICEMANAGEROF.getKind()){
			managersGrids.remove(role);	
		}else if(role.getRole().getKind().intValue() == RoleKindType.ISADVANCEDPRACTICEMEMBEROF.getKind()){
			membersGrids.remove(role);	
		}	
		
		
		//also remove from the dto
		if(role.getRole()!= null){
			if(role.getRole().getKind().intValue() == RoleKindType.ISADVANCEDPRACTICEMANAGEROF.getKind()){
				practiceDTO.getAdvancedPracticeManagerDTOlist().remove(role.getRole());
			}else if(role.getRole().getKind().intValue() == RoleKindType.ISADVANCEDPRACTICEMEMBEROF.getKind()){
				practiceDTO.getAdvancedPracticeMemberDTOList().remove(role.getRole());
			}	
		}

		
	}

	public AdvancedPracticeStatusType getAdvancedPracticeStatusType() {
		return AdvancedPracticeStatusType ;
	}

	

	
}
