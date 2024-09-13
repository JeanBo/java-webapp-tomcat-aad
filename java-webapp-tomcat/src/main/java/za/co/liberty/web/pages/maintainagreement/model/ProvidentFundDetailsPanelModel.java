package za.co.liberty.web.pages.maintainagreement.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import za.co.liberty.dto.agreement.AgreementContextDTO;
import za.co.liberty.dto.agreement.AgreementDTO;
import za.co.liberty.dto.agreement.ProvidentFundDetailDTO;
import za.co.liberty.dto.agreement.maintainagreement.MaintainAgreementDTO;
import za.co.liberty.dto.agreement.ProvidentFundOverrideRatesDTO;
import za.co.liberty.dto.agreement.maintainagreement.ProvidentFundBeneficiariesDTO;
import za.co.liberty.dto.agreement.maintainagreement.ProvidentFundBeneficiaryDetailsDTO;
import za.co.liberty.dto.agreement.maintainagreement.ProvidentFundRequestDetailDTO;
import za.co.liberty.dto.agreement.maintainagreement.ValidAgreementValuesDTO;
import za.co.liberty.dto.agreement.properties.PropertyDTO;
import za.co.liberty.web.data.enums.fields.AgreementGUIField;
import za.co.liberty.web.pages.maintainagreement.template.AgreementTemplate;
import za.co.liberty.web.wicket.view.ContextDrivenViewTemplate;

public class ProvidentFundDetailsPanelModel implements Serializable {
	
	private static final long serialVersionUID = 1L;

	/**
	 * Agreement Context
	 */
	private long agreementId;
	
	private String currentProvidentFundNumber;
	
	/**
	 * Agreement Provident Fund Detail
	 */
	private ProvidentFundDetailDTO providentFundDetailDTO;
	
	private List<PropertyDTO> propertyHistory;
	
	/**
	 * Valid Agreement Values
	 */
	private ValidAgreementValuesDTO validAgreementValues;
	
	private ProvidentFundRequestDetailDTO latestRequestDetail;
	
	private ProvidentFundBeneficiariesDTO providentFundBeneficiariesDTO;

	private ProvidentFundOverrideRatesDTO providentFundOverrideRatesDTO;

	private List<BigDecimal> providentFundOverrideRatesList;

	/**
	 * View template
	 */
	private ContextDrivenViewTemplate<AgreementGUIField, AgreementDTO> viewTemplate;

	public ProvidentFundDetailsPanelModel() {
		super();
	}
	
	public ProvidentFundDetailsPanelModel(long agreementId,String currentProvidentFundNumber, ProvidentFundDetailDTO providentFundDetailDTO, ContextDrivenViewTemplate<AgreementGUIField, AgreementDTO> viewTemplate, ProvidentFundBeneficiariesDTO providentFundBeneficiariesDTO) {
		super();
		this.agreementId = agreementId;
		this.currentProvidentFundNumber = currentProvidentFundNumber;
		this.providentFundDetailDTO = providentFundDetailDTO;
		this.providentFundBeneficiariesDTO = providentFundBeneficiariesDTO;
		this.viewTemplate = viewTemplate;
	}

	public ProvidentFundDetailsPanelModel(MaintainAgreementDTO maintainAgreementDTO) {
		super();
		this.agreementId= maintainAgreementDTO!=null &&
			maintainAgreementDTO.getAgreementDTO()!=null
			?maintainAgreementDTO.getAgreementDTO().getId()
			:0;
		this.currentProvidentFundNumber= maintainAgreementDTO!=null &&
			maintainAgreementDTO.getAgreementDTO()!=null 
			&& maintainAgreementDTO.getAgreementDTO().getAssociatedCodes() != null 
			?maintainAgreementDTO.getAgreementDTO().getAssociatedCodes().getProvidentFundNumber()
			:null;
		this.providentFundDetailDTO = maintainAgreementDTO!=null &&
			maintainAgreementDTO.getAgreementDTO()!=null
			?maintainAgreementDTO.getAgreementDTO().getProvidentFundDetail()
					:null;
		this.providentFundBeneficiariesDTO = maintainAgreementDTO!=null &&
				maintainAgreementDTO.getAgreementDTO()!=null
						?maintainAgreementDTO.getAgreementDTO().getProvidentFundBeneficiariesDTO()
								:null;
		this.viewTemplate = new AgreementTemplate(new AgreementContextDTO(
				maintainAgreementDTO!=null?maintainAgreementDTO.getAgreementDTO():null));
		this.propertyHistory = maintainAgreementDTO!=null &&
		maintainAgreementDTO.getAgreementDTO()!=null  
		?maintainAgreementDTO.getAgreementDTO().getProvidentFundHistoricalValues() : null;
	}
	
	public ProvidentFundDetailsPanelModel(MaintainAgreementPageModel pageModel) {
		this.agreementId = 
			pageModel!=null && pageModel.getMaintainAgreementDTO()!=null &&
			pageModel.getMaintainAgreementDTO().getAgreementDTO()!=null 
			?pageModel.getMaintainAgreementDTO().getAgreementDTO().getId()
			:0;
	    this.currentProvidentFundNumber= pageModel!=null && pageModel.getMaintainAgreementDTO()!=null &&
			pageModel.getMaintainAgreementDTO().getAgreementDTO()!=null 
			&& pageModel.getMaintainAgreementDTO().getAgreementDTO().getAssociatedCodes() != null		
			?pageModel.getMaintainAgreementDTO().getAgreementDTO().getAssociatedCodes().getProvidentFundNumber()
			:null;
		this.providentFundDetailDTO =
			pageModel!=null && pageModel.getMaintainAgreementDTO()!=null &&
			pageModel.getMaintainAgreementDTO().getAgreementDTO()!=null
			?pageModel.getMaintainAgreementDTO().getAgreementDTO().getProvidentFundDetail()
					:null;
			this.providentFundBeneficiariesDTO = pageModel!=null && pageModel.getMaintainAgreementDTO()!=null &&
					pageModel.getMaintainAgreementDTO().getAgreementDTO()!=null
					?pageModel.getMaintainAgreementDTO().getAgreementDTO().getProvidentFundBeneficiariesDTO()
							:null;
		this.viewTemplate = 
			pageModel!=null?pageModel.getViewTemplate():null;
			
		this.propertyHistory = pageModel!=null && pageModel.getMaintainAgreementDTO()!=null &&
		pageModel.getMaintainAgreementDTO().getAgreementDTO()!=null  
			?pageModel.getMaintainAgreementDTO().getAgreementDTO().getProvidentFundHistoricalValues() : null;
	}
	

	public ValidAgreementValuesDTO getValidAgreementValues() {
		return validAgreementValues;
	}

	public void setValidAgreementValues(ValidAgreementValuesDTO validAgreementValues) {
		this.validAgreementValues = validAgreementValues;
	}

	public ContextDrivenViewTemplate<AgreementGUIField, AgreementDTO> getViewTemplate() {
		return viewTemplate;
	}

	public void setViewTemplate(
			ContextDrivenViewTemplate<AgreementGUIField, AgreementDTO> viewTemplate) {
		this.viewTemplate = viewTemplate;
	}

	public long getAgreementId() {
		return agreementId;
	}

	public void setAgreementId(long agreementId) {
		this.agreementId = agreementId;
	}

	//Load the screen even when there is no agreement on the context 
	public ProvidentFundDetailDTO getProvidentFundDetailDTO() {
		return providentFundDetailDTO != null ?providentFundDetailDTO: new ProvidentFundDetailDTO();
	}

	public void setProvidentFundDetailDTO(
			ProvidentFundDetailDTO providentFundDetailDTO) {
		this.providentFundDetailDTO = providentFundDetailDTO;
	}
	
	public ProvidentFundOverrideRatesDTO getProvidentFundOverrideRatesDTO() {
		return providentFundOverrideRatesDTO;
	}
	public void setProvidentFundOverrideRatesDTO(ProvidentFundOverrideRatesDTO providentFundOverrideRatesDTO) {
		this.providentFundOverrideRatesDTO = providentFundOverrideRatesDTO;
	}


	public String getCurrentProvidentFundNumber() {
		return currentProvidentFundNumber;
	}

	public void setCurrentProvidentFundNumber(String currentProvidentFundNumber) {
		this.currentProvidentFundNumber = currentProvidentFundNumber;
	}

	public List<PropertyDTO> getPropertyHistory() {
		return propertyHistory;
	}

	public void setPropertyHistory(List<PropertyDTO> propertyHistory) {
		this.propertyHistory = propertyHistory;
	}

	public ProvidentFundRequestDetailDTO getLatestRequestDetail() {
		if(latestRequestDetail == null){
			latestRequestDetail = new ProvidentFundRequestDetailDTO();
		}
		return latestRequestDetail;
	}

	public void setLatestRequestDetail(
			ProvidentFundRequestDetailDTO latestRequestDetail) {
		this.latestRequestDetail = latestRequestDetail;
	}

	public List<ProvidentFundBeneficiaryDetailsDTO> getProvidentFundBeneficiariesList() {
		if (getProvidentFundBeneficiariesDTO() != null && getProvidentFundBeneficiariesDTO().getProvidentFundBeneficiaryDetailsList() != null) {
			return getProvidentFundBeneficiariesDTO().getProvidentFundBeneficiaryDetailsList();
		}
		return null;
	}

	public void setProvidentFundBeneficiariesList(List<ProvidentFundBeneficiaryDetailsDTO> providentFundBeneficiariesList) {
		if(this.getProvidentFundBeneficiariesDTO() == null)
			setProvidentFundBeneficiariesDTO(new ProvidentFundBeneficiariesDTO());
		this.getProvidentFundBeneficiariesDTO().setProvidentFundBeneficiaryDetailsList(providentFundBeneficiariesList);
	}
	
	/**
	 * Add a new Beneficiary detail
	 * @param newBeneficiary
	 */
	public void addBeneficiaryDetail(ProvidentFundBeneficiaryDetailsDTO newBeneficiary){
		//add to the top of the list so it displays first on the grid
		addBeneficiaryDetail(newBeneficiary,0);
	}
	
	/**
	 * Add a new Beneficiary detail
	 * @param newBeneficiary
	 * @param index
	 */
	public void addBeneficiaryDetail(ProvidentFundBeneficiaryDetailsDTO newBeneficiary, int index) {
		boolean found = false;
		if (newBeneficiary != null && getProvidentFundBeneficiariesDTO() != null) {
			if (getProvidentFundBeneficiariesDTO().getProvidentFundBeneficiaryDetailsList() == null)
				getProvidentFundBeneficiariesDTO().setProvidentFundBeneficiaryDetailsList(new ArrayList<ProvidentFundBeneficiaryDetailsDTO>());

			for (ProvidentFundBeneficiaryDetailsDTO beneficiaryDetailsDTOl : getProvidentFundBeneficiariesDTO().getProvidentFundBeneficiaryDetailsList()) {
				if (newBeneficiary == beneficiaryDetailsDTOl) {
					found = true;
					break;
				}
			}
			// only add if not in list already
			if (!found) {
				getProvidentFundBeneficiariesDTO().getProvidentFundBeneficiaryDetailsList().add(index, newBeneficiary);
			}
		}
	}
	
	/**
	 * Remove the a beneficiary from the list
	 * @param beneficiaryDetailsDTO
	 */
	public void removeBenficiary(ProvidentFundBeneficiaryDetailsDTO beneficiaryDetailsDTO) {
		if (beneficiaryDetailsDTO != null && getProvidentFundBeneficiariesDTO().getProvidentFundBeneficiaryDetailsList() != null && !getProvidentFundBeneficiariesDTO().getProvidentFundBeneficiaryDetailsList().isEmpty()) {
			getProvidentFundBeneficiariesDTO().getProvidentFundBeneficiaryDetailsList().remove(beneficiaryDetailsDTO);
		}
	}

	public ProvidentFundBeneficiariesDTO getProvidentFundBeneficiariesDTO() {
		return providentFundBeneficiariesDTO;
	}

	public void setProvidentFundBeneficiariesDTO(ProvidentFundBeneficiariesDTO providentFundBeneficiariesDTO) {
		this.providentFundBeneficiariesDTO = providentFundBeneficiariesDTO;
	}
	
	/**
	 * Get provident fund override list to pass on the panel
	 * 
	 */
	public List<BigDecimal> getProvidentFundOverrideRatesList() {
		return providentFundOverrideRatesList;
	}
	public void setProvidentFundOverrideRatesList(List<BigDecimal> providentFundOverrideRatesList) {
		this.providentFundOverrideRatesList = providentFundOverrideRatesList;
	}

}
