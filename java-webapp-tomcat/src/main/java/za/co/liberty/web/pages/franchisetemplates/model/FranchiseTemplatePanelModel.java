package za.co.liberty.web.pages.franchisetemplates.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import za.co.liberty.dto.gui.templates.FranchiseTemplateDTO;
import za.co.liberty.interfaces.agreements.requests.RequestKindType;
import za.co.liberty.interfaces.rating.FranchiseTemplateKindEnum;
import za.co.liberty.persistence.rating.entity.Description;
import za.co.liberty.persistence.rating.entity.DistributionKindGroupDefaultValuesEntity;

public class FranchiseTemplatePanelModel implements Serializable{
	
	private FranchiseTemplateDTO franchiseTemplateDTO;
	
	private FranchiseTemplateKindEnum distributionKindGroupEnum;

	private List<DistributionKindGroupDefaultValuesEntity> distributionKindGroupDefaultValues;
	
	private List<Description> templates;
	
	private Description template;

	
	private List<RequestKindType> unAuthRequests; 


	public FranchiseTemplatePanelModel(MaintainFranchiseTemplatePageModel model) {
		this.franchiseTemplateDTO = 
			model!=null && model.getMaintainFranchiseTemplateDTO() != null && model.getMaintainFranchiseTemplateDTO().getFranchiseTemplateDTO()!=null?model.getMaintainFranchiseTemplateDTO().getFranchiseTemplateDTO():null;
		this.unAuthRequests = new ArrayList<RequestKindType>();
		
	}

	public FranchiseTemplateKindEnum getDistributionKindGroupEnum() {
		return distributionKindGroupEnum;
	}

	public void setDistributionKindGroupEnum(
			FranchiseTemplateKindEnum distributionKindGroupEnum) {
		this.distributionKindGroupEnum = distributionKindGroupEnum;
	}

	

	public FranchiseTemplateDTO getFranchiseTemplateDTO() {
		return franchiseTemplateDTO;
	}

	public void setFranchiseTemplateDTO(FranchiseTemplateDTO franchiseTemplateDTO) {
		this.franchiseTemplateDTO = franchiseTemplateDTO;
	}

	public List<DistributionKindGroupDefaultValuesEntity> getDistributionKindGroupDefaultValues() {
		return distributionKindGroupDefaultValues;
	}

	public void setDistributionKindGroupDefaultValues(
			List<DistributionKindGroupDefaultValuesEntity> distributionKindGroupDefaultValues) {
		this.distributionKindGroupDefaultValues = distributionKindGroupDefaultValues;
	}

	public List<Description> getTemplates() {
		return templates;
	}

	public void setTemplates(List<Description> templates) {
		this.templates = templates;
	}

	public Description getTemplate() {
		return template;
	}

	public void setTemplate(Description template) {
		this.template = template;
	}
	
	
	public List<RequestKindType> getUnAuthRequests() {
		return unAuthRequests;
	}

	public void setUnAuthRequests(List<RequestKindType> unAuthRequests) {
		this.unAuthRequests = unAuthRequests;
	}


}
