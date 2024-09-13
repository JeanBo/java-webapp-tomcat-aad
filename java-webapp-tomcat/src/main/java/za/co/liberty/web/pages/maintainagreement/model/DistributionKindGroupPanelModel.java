package za.co.liberty.web.pages.maintainagreement.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import za.co.liberty.dto.agreement.AgreementContextDTO;
import za.co.liberty.dto.agreement.AgreementDTO;
import za.co.liberty.dto.gui.templates.DistributionKindGroupRatesDTO;
import za.co.liberty.web.data.enums.fields.AgreementGUIField;
import za.co.liberty.web.wicket.view.ContextDrivenViewTemplate;

public class DistributionKindGroupPanelModel implements Serializable{
	
	
	private AgreementContextDTO agreementContext;
	
	private DistributionKindGroupRatesDTO distributionKindGroupDTO;
	
	private AgreementDTO distributionKindGroupContainer;
	
	private List <DistributionKindGroupRatesDTO> distributionKindGroupDTOs;
	
	private ContextDrivenViewTemplate<AgreementGUIField, AgreementDTO> viewTemplate;
	
	
	public DistributionKindGroupPanelModel(MaintainAgreementPageModel pageModel) {
		this.agreementContext = new AgreementContextDTO(
				pageModel!=null && pageModel.getMaintainAgreementDTO()!=null
				?pageModel.getMaintainAgreementDTO().getAgreementDTO()
				:null);
		this.distributionKindGroupContainer =
			pageModel!=null && pageModel.getMaintainAgreementDTO()!=null
			?pageModel.getMaintainAgreementDTO().getAgreementDTO()
					:null;
//		this.validDistributionTemplates = 
//			pageModel!=null && pageModel.getValidAgreementValues()!=null
//			?pageModel.getValidAgreementValues().getValidDistributionTemplates()
//					:null;
		this.viewTemplate = 
			pageModel!=null
			?pageModel.getViewTemplate()
				:null;
			
		this.distributionKindGroupDTOs = this.distributionKindGroupDTOs == null ? new ArrayList<DistributionKindGroupRatesDTO>():this.distributionKindGroupDTOs;
			
//		if (pageModel!=null) {
//			this.viewTemplate = pageModel.getViewTemplate();
			
//			this.distributionKindGroupDTOs = this.distributionKindGroupDTOs == null ? new ArrayList<DistributionKindGroupDTO>():this.distributionKindGroupDTOs);
//			if (pageModel.getMaintainAgreementDTO()!=null) {
//				this.agreementDTO = pageModel.getMaintainAgreementDTO()
//					.getAgreementDTO();
//			}
//		}
	
	}
	

	public List<DistributionKindGroupRatesDTO> getDistributionKindGroupDTOs() {
		return distributionKindGroupDTOs;
	}

	public void setDistributionKindGroupDTOs(
			List<DistributionKindGroupRatesDTO> distributionKindGroupDTOs) {
		this.distributionKindGroupDTOs = distributionKindGroupDTOs;
	}

	public DistributionKindGroupRatesDTO getDistributionKindGroupDTO() {
		return distributionKindGroupDTO;
	}

	public void setDistributionKindGroupDTO(
			DistributionKindGroupRatesDTO distributionKindGroupDTO) {
		this.distributionKindGroupDTO = distributionKindGroupDTO;
	}

	

	public AgreementDTO getDistributionKindGroupContainer() {
		return distributionKindGroupContainer;
	}


	public void setDistributionKindGroupContainer(
			AgreementDTO distributionKindGroupContainer) {
		this.distributionKindGroupContainer = distributionKindGroupContainer;
	}


	public ContextDrivenViewTemplate<AgreementGUIField, AgreementDTO> getViewTemplate() {
		return viewTemplate;
	}

	public void setViewTemplate(
			ContextDrivenViewTemplate<AgreementGUIField, AgreementDTO> viewTemplate) {
		this.viewTemplate = viewTemplate;
	}


	public AgreementContextDTO getAgreementContext() {
		return agreementContext;
	}


	public void setAgreementContext(AgreementContextDTO agreementContext) {
		this.agreementContext = agreementContext;
	}
	
	

}
