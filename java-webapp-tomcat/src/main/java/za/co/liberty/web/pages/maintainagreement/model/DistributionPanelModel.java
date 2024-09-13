package za.co.liberty.web.pages.maintainagreement.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import za.co.liberty.dto.agreement.AgreementContextDTO;
import za.co.liberty.dto.agreement.AgreementDTO;
import za.co.liberty.dto.agreement.maintainagreement.MaintainDistributionTemplateDTO;
import za.co.liberty.dto.agreement.properties.DistributionTemplateDTO;
import za.co.liberty.web.data.enums.fields.AgreementGUIField;
import za.co.liberty.web.pages.maintainagreement.template.AgreementTemplate;
import za.co.liberty.web.wicket.view.ContextDrivenViewTemplate;

public class DistributionPanelModel implements Serializable {
	
	private static final long serialVersionUID = 5976883968316032223L;
	
	/**
	 * Agreement Context
	 */
	private AgreementContextDTO agreementContext;

	/**
	 * Distribution template
	 */
	private AgreementDTO distributionTemplateContainer;
	
	/**
	 * Valid values
	 */
	private List<DistributionTemplateDTO> validDistributionTemplates;
	
	/**
	 * View template
	 */
	private ContextDrivenViewTemplate<AgreementGUIField, AgreementDTO> viewTemplate;

	public DistributionPanelModel() {
		super();
	}

	public DistributionPanelModel(AgreementContextDTO agreementContext, AgreementDTO distributionTemplateContainer, List<DistributionTemplateDTO> validDistributionTemplates, ContextDrivenViewTemplate<AgreementGUIField, AgreementDTO> viewTemplate) {
		super();
		this.agreementContext = agreementContext;
		this.distributionTemplateContainer = distributionTemplateContainer;
		this.validDistributionTemplates = validDistributionTemplates;
		this.viewTemplate = viewTemplate;
	}
	
	public DistributionPanelModel(MaintainDistributionTemplateDTO maintainDistributionTemplateDTO, int kind) {
		this.viewTemplate = 
			maintainDistributionTemplateDTO!=null?new AgreementTemplate(
					maintainDistributionTemplateDTO.getAgreementContext()):null;
		this.distributionTemplateContainer = new AgreementDTO();
		this.validDistributionTemplates = new ArrayList<DistributionTemplateDTO>();
		if (maintainDistributionTemplateDTO!=null) {
			this.validDistributionTemplates.add(maintainDistributionTemplateDTO.getRequestDTO());
		}
		this.distributionTemplateContainer.setDistributionDetails(
				maintainDistributionTemplateDTO!=null
				?maintainDistributionTemplateDTO.getRequestDTO()
				:null);
	}
	
	public DistributionPanelModel(MaintainAgreementPageModel pageModel) {
		this.agreementContext = new AgreementContextDTO(
				pageModel!=null && pageModel.getMaintainAgreementDTO()!=null
				?pageModel.getMaintainAgreementDTO().getAgreementDTO()
				:null);
		this.distributionTemplateContainer =
			pageModel!=null && pageModel.getMaintainAgreementDTO()!=null
			?pageModel.getMaintainAgreementDTO().getAgreementDTO()
					:null;
		this.validDistributionTemplates = 
			pageModel!=null && pageModel.getValidAgreementValues()!=null
			?pageModel.getValidAgreementValues().getValidDistributionTemplates()
					:null;
		this.viewTemplate = 
			pageModel!=null
			?pageModel.getViewTemplate()
				:null;
	}

	public AgreementContextDTO getAgreementContext() {
		return agreementContext;
	}

	public void setAgreementContext(AgreementContextDTO agreementContext) {
		this.agreementContext = agreementContext;
	}

	public AgreementDTO getDistributionTemplateContainer() {
		return distributionTemplateContainer;
	}

	public void setDistributionTemplateContainer(
			AgreementDTO distributionTemplateContainer) {
		this.distributionTemplateContainer = distributionTemplateContainer;
	}

	public List<DistributionTemplateDTO> getValidDistributionTemplates() {
		return validDistributionTemplates;
	}

	public void setValidDistributionTemplates(
			List<DistributionTemplateDTO> validDistributionTemplates) {
		this.validDistributionTemplates = validDistributionTemplates;
	}

	public ContextDrivenViewTemplate<AgreementGUIField, AgreementDTO> getViewTemplate() {
		return viewTemplate;
	}

	public void setViewTemplate(
			ContextDrivenViewTemplate<AgreementGUIField, AgreementDTO> viewTemplate) {
		this.viewTemplate = viewTemplate;
	}
	
}
