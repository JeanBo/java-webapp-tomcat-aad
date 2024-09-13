package za.co.liberty.web.pages.maintainagreement.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.SerializationUtils;

import za.co.liberty.dto.agreement.AgreementDTO;
import za.co.liberty.dto.agreement.maintainagreement.MaintainPaysToDTO;
import za.co.liberty.dto.agreement.properties.PaysToDTO;
import za.co.liberty.helpers.util.DateUtil;
import za.co.liberty.web.data.enums.fields.AgreementGUIField;
import za.co.liberty.web.pages.maintainagreement.template.AgreementTemplate;
import za.co.liberty.web.wicket.view.ContextDrivenViewTemplate;

public class PaysToPanelModel implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Long agreementID;

	/**
	 * PaysToDTO
	 */
	private AgreementDTO paysToContainer;
	
	/**
	 * Previous PaysToDTO
	 */
	private PaysToDTO previousPaysToDTO;
	
	/**
	 * Valid PaysTo Values
	 */
	private List<PaysToDTO> validPaysToValues;
	
	/**
	 * View template
	 */
	private ContextDrivenViewTemplate<AgreementGUIField, AgreementDTO> viewTemplate;

	public PaysToPanelModel() {
		super();
	}

	public PaysToPanelModel(Long agreementID, AgreementDTO paysToContainer, PaysToDTO previousPaysToDTO, List<PaysToDTO> validPaysToValues, ContextDrivenViewTemplate<AgreementGUIField, AgreementDTO> viewTemplate) {
		super();
		this.agreementID = agreementID;
		this.paysToContainer = paysToContainer;
		this.previousPaysToDTO = previousPaysToDTO;
		this.validPaysToValues = validPaysToValues;
		this.viewTemplate = viewTemplate;
	}
	
	public PaysToPanelModel(MaintainAgreementPageModel pageModel) {
		if (pageModel!=null) {
			this.viewTemplate = pageModel.getViewTemplate();
			if (pageModel.getValidAgreementValues()!=null) {
				this.validPaysToValues = 
					pageModel.getValidAgreementValues().getValidPaysToChoices();
			}
			if (pageModel.getPreviousMaintainAgreementDTO()!=null &&
				pageModel.getPreviousMaintainAgreementDTO().getAgreementDTO()!=null) {
				this.agreementID = pageModel.getPreviousMaintainAgreementDTO()
					.getAgreementDTO().getId();
				this.previousPaysToDTO = pageModel.getPreviousMaintainAgreementDTO()
					.getAgreementDTO().getPaymentDetails();
			}
			if (pageModel.getMaintainAgreementDTO()!=null) {
				this.paysToContainer = pageModel.getMaintainAgreementDTO()
					.getAgreementDTO();
			}
		}
	}
	
	public PaysToPanelModel(MaintainPaysToDTO maintainPaysToDTO) {
		this.viewTemplate = 
			maintainPaysToDTO!=null?new AgreementTemplate(maintainPaysToDTO.getAgreementContext()):null;
		this.paysToContainer = new AgreementDTO();
		this.validPaysToValues = new ArrayList<PaysToDTO>();
		if (maintainPaysToDTO!=null) {
			this.validPaysToValues.add(maintainPaysToDTO.getRequestDTO());
		}
		this.paysToContainer.setPaymentDetails(
				maintainPaysToDTO!=null?maintainPaysToDTO.getRequestDTO():null);
	}

	/**
	 * Update model pays to effective date, based on previous and current selection
	 */
	public void updateModelForMaintainPaysTo() {
		if (paysToContainer!=null && paysToContainer.getPaymentDetails()!=null && previousPaysToDTO!=null) {
			/**
			 * Compare the current payment details to the old using cloned objects
			 * with nulls set to effective from so that the classes equals to 
			 * operation can be used to compare without taking effective from into account
			 */
			PaysToDTO oldDetails = (PaysToDTO) SerializationUtils.clone(
					previousPaysToDTO);
			oldDetails.setEffectiveFrom(null);
			PaysToDTO currentDetails = (PaysToDTO) SerializationUtils.clone(
					paysToContainer.getPaymentDetails());
			currentDetails.setEffectiveFrom(null);
			//PERFORM STANDARD EQUALS TO AS IMPLEMENTED IN THE PaysToDTO class, without effectiveFrom
			if (!oldDetails.equals(currentDetails)) {
				/**
				 * Fields don't match, a change has occurred on paysToType or 
				 * agreement number
				 */
				paysToContainer.getPaymentDetails().setEffectiveFrom(getCurrentDate());
			} else {
				paysToContainer.getPaymentDetails().setEffectiveFrom(
						previousPaysToDTO.getEffectiveFrom());
			}
		}
	}
	
	Date getCurrentDate() {
		return DateUtil.getInstance().getTodayDatePart();
	}

	public Long getAgreementID() {
		return agreementID;
	}

	public void setAgreementID(Long agreementID) {
		this.agreementID = agreementID;
	}

	public AgreementDTO getPaysToContainer() {
		return paysToContainer;
	}

	public void setPaysToContainer(AgreementDTO paysToContainer) {
		this.paysToContainer = paysToContainer;
	}

	public PaysToDTO getPreviousPaysToDTO() {
		return previousPaysToDTO;
	}

	public void setPreviousPaysToDTO(PaysToDTO previousPaysToDTO) {
		this.previousPaysToDTO = previousPaysToDTO;
	}

	public List<PaysToDTO> getValidPaysToValues() {
		return validPaysToValues;
	}

	public void setValidPaysToValues(List<PaysToDTO> validPaysToValues) {
		this.validPaysToValues = validPaysToValues;
	}

	public ContextDrivenViewTemplate<AgreementGUIField, AgreementDTO> getViewTemplate() {
		return viewTemplate;
	}

	public void setViewTemplate(
			ContextDrivenViewTemplate<AgreementGUIField, AgreementDTO> viewTemplate) {
		this.viewTemplate = viewTemplate;
	}
	
}
