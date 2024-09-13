package za.co.liberty.web.pages.maintainagreement.model;

import java.io.Serializable;

import za.co.liberty.dto.agreement.AgencyPoolAccountDetailDTO;
import za.co.liberty.dto.agreement.AgreementContextDTO;
import za.co.liberty.dto.agreement.AgreementDTO;
import za.co.liberty.dto.agreement.maintainagreement.MaintainAgencyPoolDetailDTO;
import za.co.liberty.dto.agreement.maintainagreement.MaintainAgreementDTO;
import za.co.liberty.web.data.enums.fields.AgreementGUIField;
import za.co.liberty.web.pages.maintainagreement.template.AgreementTemplate;
import za.co.liberty.web.wicket.view.ContextDrivenViewTemplate;

/**
 * 
 * @author zzt2108
 * 
 */
public class AgencyPoolAccountDetailsPanelModel implements Serializable {

	private static final long serialVersionUID = 1L;

	private long agreementId;
	private AgencyPoolAccountDetailDTO agencyPoolAccountDetailDTO;

	/**
	 * View template
	 */
	private ContextDrivenViewTemplate<AgreementGUIField, AgreementDTO> viewTemplate;

	public AgencyPoolAccountDetailsPanelModel(
			long agreementId,
			AgencyPoolAccountDetailDTO agencyPoolAccountDetailDTO,
			ContextDrivenViewTemplate<AgreementGUIField, AgreementDTO> viewTemplate) {
		super();
		this.agreementId = agreementId;
		this.agencyPoolAccountDetailDTO = agencyPoolAccountDetailDTO;
		this.viewTemplate = viewTemplate;
	}

	public AgencyPoolAccountDetailsPanelModel(
			MaintainAgreementPageModel pageModel) {
		this.viewTemplate = pageModel != null ? pageModel.getViewTemplate()
				: null;
		this.agencyPoolAccountDetailDTO = pageModel != null
				&& pageModel.getMaintainAgreementDTO() != null
				&& pageModel.getMaintainAgreementDTO().getAgreementDTO() != null
				&& pageModel.getMaintainAgreementDTO().getAgreementDTO()
						.getAgencyPoolAccountDetailDTO() != null ? pageModel
				.getMaintainAgreementDTO().getAgreementDTO()
				.getAgencyPoolAccountDetailDTO() : null;
		
		this.agreementId = pageModel != null
				&& pageModel.getMaintainAgreementDTO() != null
				&& pageModel.getMaintainAgreementDTO().getAgreementDTO() != null ? pageModel
				.getMaintainAgreementDTO().getAgreementDTO().getId()
				: 0;

	}
	
	public AgencyPoolAccountDetailsPanelModel(
			MaintainAgreementDTO maintainAgreementDTO) {
		super();

		this.viewTemplate = new AgreementTemplate(new AgreementContextDTO(
				maintainAgreementDTO != null ? maintainAgreementDTO
						.getAgreementDTO() : null));

		this.agreementId = maintainAgreementDTO != null
				&& maintainAgreementDTO.getAgreementDTO() != null ? maintainAgreementDTO
				.getAgreementDTO().getId() : 0;
		this.agencyPoolAccountDetailDTO = maintainAgreementDTO != null
				&& maintainAgreementDTO.getAgreementDTO() != null
				&& maintainAgreementDTO.getAgreementDTO()
						.getAgencyPoolAccountDetailDTO() != null ? maintainAgreementDTO
				.getAgreementDTO().getAgencyPoolAccountDetailDTO() : null;
	}

	public AgencyPoolAccountDetailsPanelModel() {
		super();
	}

	public AgencyPoolAccountDetailsPanelModel(AgencyPoolAccountDetailsPanelModel panelModel) {
		this.viewTemplate = panelModel.getViewTemplate();
	}

	public AgencyPoolAccountDetailsPanelModel(
			MaintainAgencyPoolDetailDTO maintainAgencyPoolDetail, long agreementID) {
		this(agreementID,maintainAgencyPoolDetail != null ? maintainAgencyPoolDetail.getRequestDTO() : null, 
				maintainAgencyPoolDetail != null ? new AgreementTemplate(
						maintainAgencyPoolDetail.getAgreementContext()) : null);
	}

	public long getAgreementId() {
		return agreementId;
	}

	public void setAgreementId(long agreementId) {
		this.agreementId = agreementId;
	}

	public AgencyPoolAccountDetailDTO getAgencyPoolAccountDetailDTO() {
		if(agencyPoolAccountDetailDTO == null)
			agencyPoolAccountDetailDTO = new AgencyPoolAccountDetailDTO();
		return agencyPoolAccountDetailDTO;
	}

	public void setAgencyPoolAccountDetailDTO(AgencyPoolAccountDetailDTO agencyPoolAccountDetailDTO) {
		this.agencyPoolAccountDetailDTO = agencyPoolAccountDetailDTO;
	}

	public ContextDrivenViewTemplate<AgreementGUIField, AgreementDTO> getViewTemplate() {
		return viewTemplate;
	}

	public void setViewTemplate(ContextDrivenViewTemplate<AgreementGUIField, AgreementDTO> viewTemplate) {
		this.viewTemplate = viewTemplate;
	}
}
