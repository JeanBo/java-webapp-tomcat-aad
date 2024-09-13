package za.co.liberty.web.pages.maintainagreement.model;

import java.io.Serializable;

import za.co.liberty.dto.agreement.AgreementCodesDTO;
import za.co.liberty.dto.agreement.AgreementContextDTO;
import za.co.liberty.dto.agreement.AgreementDTO;
import za.co.liberty.dto.agreement.maintainagreement.MaintainAgreementDTO;
import za.co.liberty.web.data.enums.fields.AgreementGUIField;
import za.co.liberty.web.pages.maintainagreement.template.AgreementTemplate;
import za.co.liberty.web.wicket.view.ContextDrivenViewTemplate;

public class AgreementCodePanelModel implements Serializable {
	
	/**
	 * Agreement Context
	 */
	private long agreementId;
	
	long agreementPartyID;
	
	/**
	 * Agreement Codes
	 */
	private AgreementCodesDTO agreementCodes;
	
	/**
	 * View template
	 */
	private ContextDrivenViewTemplate<AgreementGUIField, AgreementDTO> viewTemplate;

	public AgreementCodePanelModel() {
		super();
	}
	
	public AgreementCodePanelModel(long agreementId, long agreementPartyID, AgreementCodesDTO agreementCodes, ContextDrivenViewTemplate<AgreementGUIField, AgreementDTO> viewTemplate) {
		super();
		this.agreementId = agreementId;
		this.agreementPartyID = agreementPartyID;
		this.agreementCodes = agreementCodes;
		this.viewTemplate = viewTemplate;
	}

	public AgreementCodePanelModel(MaintainAgreementDTO maintainAgreementDTO) {
		super();
		this.agreementId= maintainAgreementDTO!=null &&
			maintainAgreementDTO.getAgreementDTO()!=null
			?maintainAgreementDTO.getAgreementDTO().getId()
			:0;
		this.agreementPartyID= maintainAgreementDTO!=null &&
			maintainAgreementDTO.getAgreementDTO()!=null 
			&& maintainAgreementDTO.getAgreementDTO().getPartyOid() != null
			?maintainAgreementDTO.getAgreementDTO().getPartyOid()
			:0;
		this.agreementCodes = maintainAgreementDTO!=null &&
			maintainAgreementDTO.getAgreementDTO()!=null
			?maintainAgreementDTO.getAgreementDTO().getAssociatedCodes()
					:null;
		this.viewTemplate = new AgreementTemplate(new AgreementContextDTO(
				maintainAgreementDTO!=null?maintainAgreementDTO.getAgreementDTO():null));
		
		
	}
	
	public AgreementCodePanelModel(MaintainAgreementPageModel pageModel) {
		this.agreementId = 
			pageModel!=null && pageModel.getMaintainAgreementDTO()!=null &&
			pageModel.getMaintainAgreementDTO().getAgreementDTO()!=null 
			?pageModel.getMaintainAgreementDTO().getAgreementDTO().getId()
			:0;
		this.agreementPartyID = 
				pageModel!=null && pageModel.getMaintainAgreementDTO()!=null &&
				pageModel.getMaintainAgreementDTO().getAgreementDTO()!=null 
				&& pageModel.getMaintainAgreementDTO().getAgreementDTO().getPartyOid() != null
				?pageModel.getMaintainAgreementDTO().getAgreementDTO().getPartyOid()
				:0;
		this.agreementCodes =
			pageModel!=null && pageModel.getMaintainAgreementDTO()!=null &&
			pageModel.getMaintainAgreementDTO().getAgreementDTO()!=null
			?pageModel.getMaintainAgreementDTO().getAgreementDTO().getAssociatedCodes()
					:null;
		this.viewTemplate = 
			pageModel!=null?pageModel.getViewTemplate():null;
	}

	public AgreementCodesDTO getAgreementCodes() {
		return agreementCodes;
	}

	public void setAgreementCodes(AgreementCodesDTO agreementCodes) {
		this.agreementCodes = agreementCodes;
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

	public long getAgreementPartyID() {
		return agreementPartyID;
	}

	public void setAgreementPartyID(long agreementPartyID) {
		this.agreementPartyID = agreementPartyID;
	}
	
}
