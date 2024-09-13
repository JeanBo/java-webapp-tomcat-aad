package za.co.liberty.web.pages.maintainagreement.model;

import java.io.Serializable;
import java.util.List;

import za.co.liberty.dto.gui.templates.DistributionKindGroupRatesDTO;

public class DistributionKindGroupDetailsPanelModel implements Serializable {
	
	private List<DistributionKindGroupRatesDTO >  distributionKindGroupDTOs;

	public List<DistributionKindGroupRatesDTO> getDistributionKindGroupDTOs() {
		return distributionKindGroupDTOs;
	}

	public void setDistributionKindGroupDTOs(
			List<DistributionKindGroupRatesDTO> distributionKindGroupDTOs) {
		this.distributionKindGroupDTOs = distributionKindGroupDTOs;
	}
}
