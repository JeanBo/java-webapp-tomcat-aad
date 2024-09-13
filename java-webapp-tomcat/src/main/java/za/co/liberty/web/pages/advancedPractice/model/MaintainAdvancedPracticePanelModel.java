package za.co.liberty.web.pages.advancedPractice.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import za.co.liberty.dto.advancedPractice.AdvancedPracticeDTO;
import za.co.liberty.dto.advancedPractice.AdvancedPracticeManagerDTO;
import za.co.liberty.dto.advancedPractice.AdvancedPracticeMemberDTO;
import za.co.liberty.dto.agreement.AgreementRoleDTO;
import za.co.liberty.srs.type.SRSType;

public class MaintainAdvancedPracticePanelModel implements Serializable , Cloneable {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/* (non-Javadoc)
	 * @see za.co.liberty.web.pages.advancedPractice.model.IMaintainAdvancedPracticePageModel#clone()
	 */
	public Object clone() {		
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {			
			e.printStackTrace();
		}
		return null;
	}
	
	private AdvancedPracticeDTO  advancedPracticeDTO;
	
	private String uacfID;

	
	/* Keeps a before image of the DTO */
	private AdvancedPracticeManagerDTO advancedPracticeManagerDTOBeforeImage;
	
	private AdvancedPracticeMemberDTO advancedPracticeMemberDTOBeforeImage;
	
	private List<AdvancedPracticeDTO> advancedPracticeDTOList ;
	
	private List<AdvancedPracticeMemberDTO> advancedPracticeMemberDTOList;
	
	private List<AdvancedPracticeManagerDTO> advancedPracticeManagerDTOlist; 
	
	private List<AgreementRoleDTO> agreementRoleDTOList;
	
	private AdvancedPracticeDTO partyDTOBeforeImage;
	
	private int currentTab = -1;
	
	public AdvancedPracticeDTO getAdvancedPracticeDTO() {
		// TODO Auto-generated method stub
		return null;
	}


	public String getCostCenterSelection() {
		// TODO Auto-generated method stub
		return null;
	}

	public int getCurrentTab() {
		// TODO Auto-generated method stub
		return 0;
	}

	public Class getCurrentTabClass() {
		// TODO Auto-generated method stub
		return null;
	}

	public AdvancedPracticeDTO getPartyDTOBeforeImage() {
		AdvancedPracticeDTO org = new AdvancedPracticeDTO();
		org.setBusinessName("Test Business");
		org.setEffectiveFrom(new Date());
		org.setRegDateFrom(new Date());
		org.setRegistrationNumber("2009/111111/21");
		org.setTypeOID(SRSType.ORGANISATION);
		return org;
	}

	public AdvancedPracticeDTO getSelectedItem() {
		AdvancedPracticeDTO org = new AdvancedPracticeDTO();
		org.setBusinessName("Test Business");
		org.setEffectiveFrom(new Date());
		org.setRegDateFrom(new Date());
		org.setRegistrationNumber("2009/111111/21");
		org.setTypeOID(SRSType.ORGANISATION);
		return org;
	}

	public List<AdvancedPracticeDTO> getSelectionList() {
		AdvancedPracticeDTO org = new AdvancedPracticeDTO();
		org.setBusinessName("Test Business");
		org.setEffectiveFrom(new Date());
		org.setRegDateFrom(new Date());
		org.setRegistrationNumber("2009/111111/21");
		org.setTypeOID(SRSType.ORGANISATION);
		List<AdvancedPracticeDTO> list = new ArrayList <AdvancedPracticeDTO>();
		list.add(org);
		return list;
	}

	public String getUacfID() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setAdvancedPracticeDTO(AdvancedPracticeDTO advancedPracticeDTO) {
		// TODO Auto-generated method stub
		
	}

	public void setAdvancedPracticeDTOList(List<AdvancedPracticeDTO> advancedPracticeDTOList) {
		// TODO Auto-generated method stub
		
	}

	public void setAdvancedPracticeManagerDTOBeforeImage(AdvancedPracticeManagerDTO advancedPracticeManagerDTOBeforeImage) {
		// TODO Auto-generated method stub
		
	}

	public void setAdvancedPracticeManagerDTOList(List<AdvancedPracticeManagerDTO> advancedPracticeManagerDTOlist) {
		// TODO Auto-generated method stub
		
	}

	public void setAdvancedPracticeManagerDTOlist(List<AdvancedPracticeManagerDTO> advancedPracticeManagerDTOlist) {
		// TODO Auto-generated method stub
		
	}

	public void setAdvancedPracticeMemberDTOBeforeImage(AdvancedPracticeMemberDTO advancedPracticeMemberDTOBeforeImage) {
		// TODO Auto-generated method stub
		
	}

	public void setAdvancedPracticeMemberDTOList(List<AdvancedPracticeMemberDTO> advancedPracticeMemberDTOList) {
		// TODO Auto-generated method stub
		
	}

	public void setCostCenterSelection(String costCenterSelection) {
		// TODO Auto-generated method stub
		
	}

	public void setCurrentTab(int currentTab) {
		// TODO Auto-generated method stub
		
	}

	public void setCurrentTabClass(Class currentTabClass) {
		// TODO Auto-generated method stub
		
	}

	public void setPartyDTOBeforeImage(AdvancedPracticeDTO partyDTOBeforeImage) {
		// TODO Auto-generated method stub
		
	}

	public void setSelectedItem(AdvancedPracticeManagerDTO selected) {
		// TODO Auto-generated method stub
		
	}

	public void setSelectedItem(AdvancedPracticeDTO selected) {
		// TODO Auto-generated method stub
		
	}

	public void setSelectionList(List<AdvancedPracticeDTO> selectionList) {
		// TODO Auto-generated method stub
		
	}

	public void setUacfID(String uacfID) {
		// TODO Auto-generated method stub
		
	}

	public List<AgreementRoleDTO> getAgreementRoleDTOList() {
		return agreementRoleDTOList;
	}

	public void setAgreementRoleDTOList(List<AgreementRoleDTO> agreementRoleDTOList) {
		this.agreementRoleDTOList = agreementRoleDTOList;
	}

}
