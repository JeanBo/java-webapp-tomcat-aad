package za.co.liberty.web.pages.maintainagreement.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import za.co.liberty.dto.agreement.properties.DistributionDetailDTO;
import za.co.liberty.dto.agreement.properties.DistributionTemplateHistoryDTO;
import za.co.liberty.dto.gui.templates.DistributionKindGroupRatesDTO;
import za.co.liberty.interfaces.rating.FranchiseTemplateKindEnum;

/**
 * This class represents the model to be used for the distribution details page
 * @author kxd1203
 *
 */
public class DistributionDetailPageModel implements Serializable {
	
	private List<DistributionDetailDTO> distributionDetails;
	
	private List<DistributionTemplateHistoryDTO> distribDetailsHistoryList;
	
	private List<DistributionKindGroupRatesDTO> distributionKindGroupRatesDTOs;
	
	private DistributionKindGroupRatesDTO distributionKindGroupRatesDTO;
	
	private FranchiseTemplateKindEnum franchiseTemplateKindEnum;
	

	public DistributionDetailPageModel() {
		setDistributionDetails(new ArrayList<DistributionDetailDTO>());
		setDistribDetailsHistoryList(new ArrayList<DistributionTemplateHistoryDTO> ());
		setDistributionKindGroupRatesDTOs(new ArrayList<DistributionKindGroupRatesDTO>());
	}

	/**
	 * Get the distribution details list. The list cannot be set, only modified, 
	 * so that once the page model is set on the target page the model will 
	 * remain in sync with the user interface components.
	 * 
	 * To change the contents, clear the list and then add new components.
	 * 
	 * @return the list of distribution template details
	 */
	public List<DistributionDetailDTO> getDistributionDetails() {
		return distributionDetails;
	}

	private void setDistributionDetails(
			List<DistributionDetailDTO> distributionDetails) {
		this.distributionDetails = distributionDetails;
	}

	public List<DistributionTemplateHistoryDTO> getDistribDetailsHistoryList() {
		return distribDetailsHistoryList;
	}

	private void setDistribDetailsHistoryList(
			List<DistributionTemplateHistoryDTO> distribDetailsHistoryList) {
		this.distribDetailsHistoryList = distribDetailsHistoryList;
	}

	public List<DistributionKindGroupRatesDTO> getDistributionKindGroupRatesDTOs() {
		return distributionKindGroupRatesDTOs;
	}

	public void setDistributionKindGroupRatesDTOs(
			List<DistributionKindGroupRatesDTO> distributionKindGroupRatesDTOs) {
		this.distributionKindGroupRatesDTOs = distributionKindGroupRatesDTOs;
	}

	public DistributionKindGroupRatesDTO getDistributionKindGroupRatesDTO() {
		return distributionKindGroupRatesDTO;
	}

	public void setDistributionKindGroupRatesDTO(
			DistributionKindGroupRatesDTO distributionKindGroupRatesDTO) {
		this.distributionKindGroupRatesDTO = distributionKindGroupRatesDTO;
	}

	public FranchiseTemplateKindEnum getFranchiseTemplateKindEnum() {
		return franchiseTemplateKindEnum;
	}

	public void setFranchiseTemplateKindEnum(
			FranchiseTemplateKindEnum franchiseTemplateKindEnum) {
		this.franchiseTemplateKindEnum = franchiseTemplateKindEnum;
	}
	
	

}
