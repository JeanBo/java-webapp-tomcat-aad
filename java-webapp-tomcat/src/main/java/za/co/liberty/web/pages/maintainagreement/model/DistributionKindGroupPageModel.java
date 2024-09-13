package za.co.liberty.web.pages.maintainagreement.model;

import java.io.Serializable;
import java.util.List;

import za.co.liberty.dto.gui.templates.DistributionKindGroupRatesDTO;

public class DistributionKindGroupPageModel implements Serializable{
	
	private DistributionKindGroupRatesDTO distributionKindGroupDTO;
	
	private List<DistributionKindGroupRatesDTO> distributionKindGroupDTOs;

	public DistributionKindGroupRatesDTO getDistributionKindGroupDTO() {
		return distributionKindGroupDTO;
	}

	public void setDistributionKindGroupDTO(
			DistributionKindGroupRatesDTO distributionKindGroupDTO) {
		this.distributionKindGroupDTO = distributionKindGroupDTO;
	}

	public List<DistributionKindGroupRatesDTO> getDistributionKindGroupDTOs() {
		return distributionKindGroupDTOs;
	}

	public void setDistributionKindGroupDTOs(
			List<DistributionKindGroupRatesDTO> distributionKindGroupDTOs) {
		this.distributionKindGroupDTOs = distributionKindGroupDTOs;
	}
	
	

}
