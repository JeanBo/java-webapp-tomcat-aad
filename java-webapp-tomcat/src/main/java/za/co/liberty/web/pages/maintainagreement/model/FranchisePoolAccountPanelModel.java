package za.co.liberty.web.pages.maintainagreement.model;

import java.io.Serializable;

import za.co.liberty.dto.agreement.AgreementDTO;
import za.co.liberty.dto.agreement.maintainagreement.MaintainPoolAccountDTO;
import za.co.liberty.dto.agreement.properties.FranchisePoolAccountDTO;
import za.co.liberty.web.data.enums.fields.AgreementGUIField;
import za.co.liberty.web.pages.maintainagreement.template.AgreementTemplate;
import za.co.liberty.web.wicket.view.ContextDrivenViewTemplate;

public class FranchisePoolAccountPanelModel implements Serializable{

	private static final long serialVersionUID = 2318394738767104805L;

	/**
	 * Pool Account DTO
	 */
	private FranchisePoolAccountDTO franchisePoolAccount;
	
	/**
	 * View Template
	 */
	private ContextDrivenViewTemplate<AgreementGUIField,AgreementDTO> viewTemplate;

	public FranchisePoolAccountPanelModel() {
		super();
	}

	public FranchisePoolAccountPanelModel(FranchisePoolAccountDTO franchisePoolAccount, ContextDrivenViewTemplate<AgreementGUIField, AgreementDTO> viewTemplate) {
		super();
		this.franchisePoolAccount = franchisePoolAccount!=null?franchisePoolAccount:new FranchisePoolAccountDTO();
		this.viewTemplate = viewTemplate;
	}
	
	public FranchisePoolAccountPanelModel(MaintainAgreementPageModel pageModel) {
		this(pageModel!=null && pageModel.getMaintainAgreementDTO()!=null &&
			 pageModel.getMaintainAgreementDTO().getAgreementDTO()!=null
		     	?pageModel.getMaintainAgreementDTO().getAgreementDTO().getFranchisePoolAccount():null,
		     pageModel!=null?pageModel.getViewTemplate():null);
	}
	
	public FranchisePoolAccountPanelModel(MaintainPoolAccountDTO maintainPoolAccountDTO) {
		this(maintainPoolAccountDTO!=null
				?maintainPoolAccountDTO.getRequestDTO():null,
			new AgreementTemplate(maintainPoolAccountDTO!=null
				?maintainPoolAccountDTO.getAgreementContext():null));
	}

	public FranchisePoolAccountDTO getFranchisePoolAccount() {
		return franchisePoolAccount;
	}

	public void setFranchisePoolAccount(FranchisePoolAccountDTO franchisePoolAccount) {
		this.franchisePoolAccount = franchisePoolAccount;
	}

	public ContextDrivenViewTemplate<AgreementGUIField, AgreementDTO> getViewTemplate() {
		return viewTemplate;
	}

	public void setViewTemplate(
			ContextDrivenViewTemplate<AgreementGUIField, AgreementDTO> viewTemplate) {
		this.viewTemplate = viewTemplate;
	}
	
}
