package za.co.liberty.web.pages.maintainagreement.model;

import java.io.Serializable;

import za.co.liberty.dto.agreement.AgreementDTO;

import za.co.liberty.dto.agreement.maintainagreement.MaintainFAISLicenseDTO;
import za.co.liberty.dto.agreement.maintainagreement.MaintainFAISLicenseDTOOLD;
import za.co.liberty.dto.agreement.maintainagreement.ValidFAISLicenseValuesDTO;

import za.co.liberty.dto.agreement.properties.FAISLicenseDTOOLD;

import za.co.liberty.web.data.enums.fields.AgreementGUIField;
import za.co.liberty.web.pages.maintainagreement.template.AgreementTemplate;
import za.co.liberty.web.wicket.view.ContextDrivenViewTemplate;

/**
 * This class represents the model that will be used for the FAIS license panel.
 * 
 * @author kxd1203
 *
 */
public class FAISLicensePanelModelOLD implements Serializable {

	private static final long serialVersionUID = -1653141120807831898L;

	/**
	 * FAIS License details
	 */
	private FAISLicenseDTOOLD faisLicenseDTO;

	/**
	 * Valid values for FAIS license details
	 */

	private ValidFAISLicenseValuesDTO validFaisLicenseValues;

	/**
	 * View template
	 */
	private ContextDrivenViewTemplate<AgreementGUIField, AgreementDTO> viewTemplate;

	/**
	 * Construct a new FAIS license panel model with the required parameters
	 * @param faisLicenseDTO FAIS license details
	 * @param validFaisLicenseValues valid FAIS license details values
	 * @param viewTemplate 
	 */
	public FAISLicensePanelModelOLD(
			FAISLicenseDTOOLD faisLicenseDTO,
			ValidFAISLicenseValuesDTO validFaisLicenseValues,
			ContextDrivenViewTemplate<AgreementGUIField, AgreementDTO> viewTemplate) {

		super();
		this.faisLicenseDTO = faisLicenseDTO;
		this.validFaisLicenseValues = validFaisLicenseValues;
		this.viewTemplate = viewTemplate;
	}

	public FAISLicensePanelModelOLD(
			MaintainFAISLicenseDTOOLD maintainFAISLicenseDTO) {
		this(maintainFAISLicenseDTO != null ? maintainFAISLicenseDTO
				.getRequestDTO() : null, new ValidFAISLicenseValuesDTO(),
				maintainFAISLicenseDTO != null ? new AgreementTemplate(
						maintainFAISLicenseDTO.getAgreementContext()) : null);

	}

//	public FAISLicensePanelModelOLD(MaintainAgreementPageModel pageModel) {
//		this.faisLicenseDTO = pageModel != null
//				&& pageModel.getMaintainAgreementDTO() != null
//				&& pageModel.getMaintainAgreementDTO().getAgreementDTO() != null ? pageModel
//				.getMaintainAgreementDTO().getAgreementDTO().getFaisLicense()
//				: null;
//		this.validFaisLicenseValues = pageModel != null
//				&& pageModel.getValidAgreementValues() != null ? pageModel
//				.getValidAgreementValues().getValidFAISLicenseValues() : null;
//		this.viewTemplate = pageModel != null ? pageModel.getViewTemplate()
//				: null;
//
//	}

	public FAISLicenseDTOOLD getFaisLicenseDTO() {
		return faisLicenseDTO;
	}

	public void setFaisLicenseDTO(FAISLicenseDTOOLD faisLicenseDTO) {
		this.faisLicenseDTO = faisLicenseDTO;
	}

	public ValidFAISLicenseValuesDTO getValidFaisLicenseValues() {
		return validFaisLicenseValues;
	}

	public void setValidFaisLicenseValues(
			ValidFAISLicenseValuesDTO validFaisLicenseValues) {
		this.validFaisLicenseValues = validFaisLicenseValues;
	}

	public ContextDrivenViewTemplate<AgreementGUIField, AgreementDTO> getViewTemplate() {
		return viewTemplate;
	}

	public void setViewTemplate(
			ContextDrivenViewTemplate<AgreementGUIField, AgreementDTO> viewTemplate) {
		this.viewTemplate = viewTemplate;
	}

}
