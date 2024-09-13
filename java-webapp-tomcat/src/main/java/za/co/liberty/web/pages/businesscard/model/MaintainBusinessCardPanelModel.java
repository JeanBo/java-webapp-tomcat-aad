package za.co.liberty.web.pages.businesscard.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import za.co.liberty.dto.agreementprivileges.AgreementPrivilegesDataDTO;
import za.co.liberty.dto.businesscard.BusinessCardDetailsDTO;
import za.co.liberty.dto.businesscard.IFindAnFAModel;
import za.co.liberty.dto.contracting.ResultAgreementDTO;
import za.co.liberty.dto.databaseenum.LanguagePreferenceDBEnumDTO;
import za.co.liberty.dto.databaseenum.rating.PostalProvinceDBEnumDTO;
import za.co.liberty.dto.databaseenum.rating.PostalRegionDBEnumDTO;
import za.co.liberty.persistence.rating.entity.fastlane.PostalAreaFLO;
import za.co.liberty.persistence.rating.entity.fastlane.PostalPostalCodeFLO;

/**
 * Page model for the business card details screen
 * @author DZS2610
 *
 */
public class MaintainBusinessCardPanelModel implements Serializable, IFindAnFAModel{
	private static final long serialVersionUID = 1L;

	private BusinessCardDetailsDTO businessCardDetails;
	
	private BusinessCardDetailsDTO beforeImage;
	
	private List<AgreementPrivilegesDataDTO> agreementPrivilegesDataDTOs = new ArrayList<AgreementPrivilegesDataDTO>();
	
	private List<AgreementPrivilegesDataDTO> deletePrivilegesDataDTOs = new ArrayList<AgreementPrivilegesDataDTO>();
	
	private AgreementPrivilegesDataDTO agreementPrivilegesDataDTO;
	
	private List<ResultAgreementDTO> allAgreementsList = new ArrayList<ResultAgreementDTO>();
	
	private List<AgreementPrivilegesDataDTO> accessGrantedOwnAgreementList;
	
	private List<AgreementPrivilegesDataDTO> ownAgreementList;

	private boolean showFindAnFAPanel;
	private boolean findAnFAOptinEnabled;
	private String findAnFAWarningMessage;
	
	private List<LanguagePreferenceDBEnumDTO> allSpokenLanguages;
	private List<PostalProvinceDBEnumDTO> allProvinces;
	private List<PostalRegionDBEnumDTO> allRegions;
	private List<PostalAreaFLO> allPostalAreas = new ArrayList<PostalAreaFLO>();  // Always set to clear list
	private List<PostalPostalCodeFLO> allPostalPostalCodes = new ArrayList<PostalPostalCodeFLO>(); // Always set to clear list
	
	private PostalProvinceDBEnumDTO selectedProvince;
	private PostalRegionDBEnumDTO selectedRegion;
	private PostalAreaFLO selectedArea;
	
	
	public BusinessCardDetailsDTO getBeforeImage() {
		return beforeImage;
	}

	public void setBeforeImage(BusinessCardDetailsDTO beforeImage) {
		this.beforeImage = beforeImage;
	}

	public BusinessCardDetailsDTO getBusinessCardDetails() {
		return businessCardDetails;
	}

	public void setBusinessCardDetails(BusinessCardDetailsDTO businessCardDetails) {
		this.businessCardDetails = businessCardDetails;
	}
	
	public List<AgreementPrivilegesDataDTO> getAgreementPrivilegesDataDTOs() {
		return agreementPrivilegesDataDTOs;
	}

	public void setAgreementPrivilegesDataDTOs(
			List<AgreementPrivilegesDataDTO> agreementPrivilegesDataDTOs) {
		this.agreementPrivilegesDataDTOs = agreementPrivilegesDataDTOs;
	}

	public AgreementPrivilegesDataDTO getAgreementPrivilegesDataDTO() {
		return agreementPrivilegesDataDTO;
	}

	public void setAgreementPrivilegesDataDTO(
			AgreementPrivilegesDataDTO agreementPrivilegesDataDTO) {
		this.agreementPrivilegesDataDTO = agreementPrivilegesDataDTO;
	}

	public List<AgreementPrivilegesDataDTO> getDeletePrivilegesDataDTOs() {
		return deletePrivilegesDataDTOs;
	}

	public void setDeletePrivilegesDataDTOs(
			List<AgreementPrivilegesDataDTO> deletePrivilegesDataDTOs) {
		this.deletePrivilegesDataDTOs = deletePrivilegesDataDTOs;
	}


	public List<ResultAgreementDTO> getAllAgreementsList() {
		return allAgreementsList;
	}

	public void setAllAgreementsList(List<ResultAgreementDTO> allAgreementsList) {
		this.allAgreementsList = allAgreementsList;
	}
	public List<AgreementPrivilegesDataDTO> getAcessGrantedOwnAgreementList() {
		return accessGrantedOwnAgreementList;
	}

	public void setAcessGrantedOwnAgreementList(
			List<AgreementPrivilegesDataDTO> acessGrantedOwnAgreementList) {
		accessGrantedOwnAgreementList = acessGrantedOwnAgreementList;
	}
	public void setOwnAgreementList(
			List<AgreementPrivilegesDataDTO> ownAgreementList) {
		this.ownAgreementList = ownAgreementList;
	}


	public List<AgreementPrivilegesDataDTO> getOwnAgreementList() {
		return ownAgreementList;
	}

	/* (non-Javadoc)
	 * @see za.co.liberty.web.pages.businesscard.model.IFindAnFAModel#isShowFindAnFAPanel()
	 */
	public boolean isShowFindAnFAPanel() {
		return showFindAnFAPanel;
	}

	/* (non-Javadoc)
	 * @see za.co.liberty.web.pages.businesscard.model.IFindAnFAModel#setShowFindAnFAPanel(boolean)
	 */
	public void setShowFindAnFAPanel(boolean showFindAnFAPanel) {
		this.showFindAnFAPanel = showFindAnFAPanel;
	}

	/* (non-Javadoc)
	 * @see za.co.liberty.web.pages.businesscard.model.IFindAnFAModel#isFindAnFAOptinEnabled()
	 */
	public boolean isFindAnFAOptinEnabled() {
		return findAnFAOptinEnabled;
	}

	/* (non-Javadoc)
	 * @see za.co.liberty.web.pages.businesscard.model.IFindAnFAModel#setFindAnFAOptinEnabled(boolean)
	 */
	public void setFindAnFAOptinEnabled(boolean findAnFAOptinEnabled) {
		this.findAnFAOptinEnabled = findAnFAOptinEnabled;
	}

	/* (non-Javadoc)
	 * @see za.co.liberty.web.pages.businesscard.model.IFindAnFAModel#getFindAnFAWarningMessage()
	 */
	public String getFindAnFAWarningMessage() {
		return findAnFAWarningMessage;
	}

	/* (non-Javadoc)
	 * @see za.co.liberty.web.pages.businesscard.model.IFindAnFAModel#setFindAnFAWarningMessage(java.lang.String)
	 */
	public void setFindAnFAWarningMessage(String findAnFAWarningMessage) {
		this.findAnFAWarningMessage = findAnFAWarningMessage;
	}

	public List<LanguagePreferenceDBEnumDTO> getAllSpokenLanguages() {
		return allSpokenLanguages;
	}

	public void setAllSpokenLanguages(
			List<LanguagePreferenceDBEnumDTO> allSpokenLanguages) {
		this.allSpokenLanguages = allSpokenLanguages;
	}

	public PostalProvinceDBEnumDTO getSelectedProvince() {
		return selectedProvince;
	}

	public void setSelectedProvince(PostalProvinceDBEnumDTO selectedProvince) {
		this.selectedProvince = selectedProvince;
	}

	public List<PostalProvinceDBEnumDTO> getAllProvinces() {
		return allProvinces;
	}

	public void setAllProvinces(List<PostalProvinceDBEnumDTO> allProvinces) {
		this.allProvinces = allProvinces;
	}

	public List<PostalRegionDBEnumDTO> getAllRegions() {
		return allRegions;
	}

	public void setAllRegions(List<PostalRegionDBEnumDTO> allRegions) {
		this.allRegions = allRegions;
	}

	public PostalRegionDBEnumDTO getSelectedRegion() {
		return selectedRegion;
	}

	public void setSelectedRegion(PostalRegionDBEnumDTO selectedRegion) {
		this.selectedRegion = selectedRegion;
	}

	public List<PostalAreaFLO> getAllPostalAreas() {
		return allPostalAreas;
	}

	public void setAllPostalAreas(List<PostalAreaFLO> allPostalAreas) {
		this.allPostalAreas = allPostalAreas;
	}

	public PostalAreaFLO getSelectedArea() {
		return selectedArea;
	}

	public void setSelectedArea(PostalAreaFLO selectedArea) {
		this.selectedArea = selectedArea;
	}

	public List<PostalPostalCodeFLO> getAllPostalPostalCodes() {
		return allPostalPostalCodes;
	}

	public void setAllPostalPostalCodes(
			List<PostalPostalCodeFLO> allPostalPostalCodes) {
		this.allPostalPostalCodes = allPostalPostalCodes;
	}	


}
