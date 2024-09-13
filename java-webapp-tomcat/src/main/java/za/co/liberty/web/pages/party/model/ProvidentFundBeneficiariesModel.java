package za.co.liberty.web.pages.party.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import za.co.liberty.dto.agreement.maintainagreement.ProvidentFundBeneficiariesDTO;
import za.co.liberty.dto.agreement.maintainagreement.ProvidentFundBeneficiaryDetailsDTO;

public class ProvidentFundBeneficiariesModel implements Serializable {

	private static final long serialVersionUID = 1055290042788102175L;
	
	private ProvidentFundBeneficiariesDTO providentFundBeneficiariesDTO;
	
	private ProvidentFundBeneficiariesDTO providentFundBeneficiariesDTOBeforeImage;
	
	public List<ProvidentFundBeneficiaryDetailsDTO> getProvidentFundBeneficiariesList() {
		if (getProvidentFundBeneficiariesDTO() != null && getProvidentFundBeneficiariesDTO().getProvidentFundBeneficiaryDetailsList() != null) {
			return getProvidentFundBeneficiariesDTO().getProvidentFundBeneficiaryDetailsList();
		}
		return null;
	}

	public void setProvidentFundBeneficiariesList(List<ProvidentFundBeneficiaryDetailsDTO> providentFundBeneficiariesList) {
		this.getProvidentFundBeneficiariesDTO().setProvidentFundBeneficiaryDetailsList(providentFundBeneficiariesList);
	}
	
	//Get before image
	public List<ProvidentFundBeneficiaryDetailsDTO> getProvidentFundBeneficiariesListBeforeImage() {
		if (getProvidentFundBeneficiariesDTOBeforeImage() != null && getProvidentFundBeneficiariesDTOBeforeImage().getProvidentFundBeneficiaryDetailsList() != null) {
			return getProvidentFundBeneficiariesDTOBeforeImage().getProvidentFundBeneficiaryDetailsList();
		}
		return null;
	}
	
	//Set before image
	public void setProvidentFundBeneficiariesListBeforeImage(List<ProvidentFundBeneficiaryDetailsDTO> providentFundBeneficiariesList) {
		this.getProvidentFundBeneficiariesDTOBeforeImage().setProvidentFundBeneficiaryDetailsList(providentFundBeneficiariesList);
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

	public ProvidentFundBeneficiariesDTO getProvidentFundBeneficiariesDTOBeforeImage() {
		return providentFundBeneficiariesDTOBeforeImage;
	}

	public void setProvidentFundBeneficiariesDTOBeforeImage(ProvidentFundBeneficiariesDTO providentFundBeneficiariesDTOBeforeImage) {
		this.providentFundBeneficiariesDTOBeforeImage = providentFundBeneficiariesDTOBeforeImage;
	}
	
	
}
