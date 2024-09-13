package za.co.liberty.web.pages.maintainagreement.model;

import java.io.Serializable;
import java.util.Date;

import za.co.liberty.dto.agreement.AgreementDTO;
import za.co.liberty.dto.agreement.maintainagreement.ActivateAgreementDTO;
import za.co.liberty.dto.agreement.maintainagreement.MaintainAgreementDTO;
import za.co.liberty.dto.agreement.maintainagreement.ValidAgreementValuesDTO;
import za.co.liberty.dto.agreement.properties.DistributionTemplateDTO;
import za.co.liberty.dto.agreement.properties.PaysToDTO;
import za.co.liberty.web.data.enums.fields.AgreementGUIField;
import za.co.liberty.web.pages.maintainagreement.template.AgreementTemplate;
import za.co.liberty.web.wicket.view.ContextDrivenViewTemplate;

public class AgreementDetailsPanelModel implements Serializable {
	
	private static final long serialVersionUID = 2767040932840172669L;

	/**
	 * AgreementDTO
	 */
	private AgreementDTO agreement;
	
	/**
	 * Valid Agreement Values
	 */
	private ValidAgreementValuesDTO validAgreementValues;
	
	/**
	 * View template
	 */
	private ContextDrivenViewTemplate<AgreementGUIField, AgreementDTO> viewTemplate;

	public AgreementDetailsPanelModel() {
		super();
	}

	public AgreementDetailsPanelModel(AgreementDTO agreement, ValidAgreementValuesDTO validAgreementValues, ContextDrivenViewTemplate<AgreementGUIField, AgreementDTO> viewTemplate) {
		super();
		this.agreement = agreement;
		this.validAgreementValues = validAgreementValues;
		this.viewTemplate = viewTemplate;
	}
	
	public AgreementDetailsPanelModel(MaintainAgreementPageModel pageModel) {
		super();
		this.agreement = pageModel!=null && pageModel.getMaintainAgreementDTO()!=null
			?pageModel.getMaintainAgreementDTO().getAgreementDTO():null;
		this.validAgreementValues = pageModel!=null
			?pageModel.getValidAgreementValues():null;
		this.viewTemplate = pageModel!=null
			?pageModel.getViewTemplate():null;
	}
	
	public AgreementDetailsPanelModel(MaintainAgreementDTO maintainAgreementDTO) {
		super();
		this.agreement = maintainAgreementDTO!=null
			?maintainAgreementDTO.getAgreementDTO():null;
		this.validAgreementValues = new ValidAgreementValuesDTO();
		this.viewTemplate = new AgreementTemplate(
				maintainAgreementDTO!=null?maintainAgreementDTO.getAgreementDTO():null);
	}
	
	public AgreementDetailsPanelModel(ActivateAgreementDTO activateAgreementDTO) {
		super();
		this.agreement = activateAgreementDTO!=null
			?activateAgreementDTO.getAgreementDTO():null;
		this.validAgreementValues = new ValidAgreementValuesDTO();
		this.viewTemplate = new AgreementTemplate(
				activateAgreementDTO!=null?activateAgreementDTO.getAgreementDTO():null);
	}

	/**
	 * This method will do any updates required on the model due to 
	 * a possible change in the agreement start date
	 * 
	 * Agreement Start date is tied to Pays To Date and Distribution Template Date
	 * 
	 * The actual values and all valid values will have to be updated accordingly
	 */
	public void updateModelForCreateAgreementFromAgreementStartDate() {
		if (agreement!=null) {
			AgreementDTO agreementDTORef = agreement;
			ValidAgreementValuesDTO validValuesRef = getValidAgreementValues();
			Date agreementStart = agreement.getStartDate();
			if (agreementStart!=null) {
				if (agreementDTORef.getPaymentDetails()!=null) {
					agreementDTORef.getPaymentDetails().setEffectiveFrom(agreementStart);
				}
				if (agreementDTORef.getDistributionDetails()!=null) {
					agreementDTORef.getDistributionDetails().setEffectiveFrom(agreementStart);
				}
				if (validValuesRef!=null) {
					if (validValuesRef.getValidPaysToChoices()!=null) {
						for (PaysToDTO paysTo : 
							validValuesRef.getValidPaysToChoices()) {
							paysTo.setEffectiveFrom(agreementStart);
						}
					}
					if (validValuesRef.getValidDistributionTemplates()!=null) {
						for (DistributionTemplateDTO template : 
							validValuesRef.getValidDistributionTemplates()) {
							template.setEffectiveFrom(agreementStart);
						}
					}
				}
			}
		}
	}

	public AgreementDTO getAgreement() {
		return agreement;
	}

	public void setAgreement(AgreementDTO agreement) {
		this.agreement = agreement;
	}

	public ValidAgreementValuesDTO getValidAgreementValues() {
		return validAgreementValues;
	}

	public void setValidAgreementValues(ValidAgreementValuesDTO validAgreementValues) {
		this.validAgreementValues = validAgreementValues;
	}

	public ContextDrivenViewTemplate<AgreementGUIField, AgreementDTO> getViewTemplate() {
		return viewTemplate;
	}

	public void setViewTemplate(
			ContextDrivenViewTemplate<AgreementGUIField, AgreementDTO> viewTemplate) {
		this.viewTemplate = viewTemplate;
	}

}
