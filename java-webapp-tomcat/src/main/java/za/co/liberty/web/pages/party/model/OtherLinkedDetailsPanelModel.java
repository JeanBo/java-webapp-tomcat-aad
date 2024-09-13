package za.co.liberty.web.pages.party.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import za.co.liberty.dto.userprofiles.ExplicitAgreementDTO;
import za.co.liberty.web.pages.agreementprivilege.model.AgreementLinkingPageModel;

public class OtherLinkedDetailsPanelModel extends AgreementLinkingPageModel implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private List<ExplicitAgreementDTO> explicitAgreementDTOs;
	private ExplicitAgreementDTO agreementDTO;
	
	//Kept here as the behaviour logic could not check this due to inverse roles being used
	private List<ExplicitAgreementDTO> explicitAgreementDTORemovals = new ArrayList<ExplicitAgreementDTO>();

	public List<ExplicitAgreementDTO> getExplicitAgreementDTOs() {
		return explicitAgreementDTOs;
	}

	public void setExplicitAgreementDTOs(
			List<ExplicitAgreementDTO> explicitAgreementDTOs) {
		this.explicitAgreementDTOs = explicitAgreementDTOs;
	}

	public List<ExplicitAgreementDTO> getExplicitAgreementDTORemovals() {
		return explicitAgreementDTORemovals;
	}

	public void setExplicitAgreementDTORemovals(
			List<ExplicitAgreementDTO> explicitAgreementDTORemovals) {
		this.explicitAgreementDTORemovals = explicitAgreementDTORemovals;
	}

	public void setExplicitAgreementDTO(ExplicitAgreementDTO agreementDTO) {
		this.agreementDTO=agreementDTO;
		
	}

	public ExplicitAgreementDTO getAgreementDTO() {
		return agreementDTO;
	}

	public void setAgreementDTO(ExplicitAgreementDTO agreementDTO) {
		this.agreementDTO = agreementDTO;
	}
	
	

}
