package za.co.liberty.web.pages.maintainagreement.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import za.co.liberty.dto.agreement.AgreementDTO;
import za.co.liberty.dto.agreement.maintainagreement.MaintainAQCDetailsDTO;
import za.co.liberty.dto.party.aqcdetail.AdvisorQualityCodeDTO;
import za.co.liberty.dto.party.aqcdetail.ValidManualAQCValuesDTO;
import za.co.liberty.dto.persistence.party.flow.PartyAQCHistoryFLO;
import za.co.liberty.web.data.enums.fields.AgreementGUIField;
import za.co.liberty.web.pages.maintainagreement.template.AgreementTemplate;
import za.co.liberty.web.wicket.view.ContextDrivenViewTemplate;

/**
 * This class represents the model that will be used for the AQC Details panel.
 * 
 * @author PKS2802
 *
 */
public class AdvisorQualityCodePanelModel implements Serializable {
	
	private static final long serialVersionUID = 2183077774696471219L;

	/**
	 * Advisor Quality Code DTO
	 */
	private AdvisorQualityCodeDTO advisorQualityCodeDTO;
	
	/**
	 * Valid values for Manual RISK AQC Values 
	 */
	private ValidManualAQCValuesDTO validManualAQCValues;

	
	/**
	 * View template
	 */
	private ContextDrivenViewTemplate<AgreementGUIField, AgreementDTO> viewTemplate;
	
	//Party Oid
	private Long partyOid ; 
	
	//Calculated AQC History	 
	private List<PartyAQCHistoryFLO> calculatedAQCHistory = new ArrayList<PartyAQCHistoryFLO>();
	
	//Manual AQC History	 
	private List<PartyAQCHistoryFLO> manualAQCHistory = new ArrayList<PartyAQCHistoryFLO>();	
	
	private String title;
	
	/**
	 * Construct a new AQC panel model with the required parameters
	 * @param AdvisorQualityCodeDTO
	 * @param ValidManualAQCValuesDTO
	 * @param viewTemplate 
	 */
	public AdvisorQualityCodePanelModel(AdvisorQualityCodeDTO advisorQualityCodeDTO, ValidManualAQCValuesDTO validManualAQCValues, 
			ContextDrivenViewTemplate<AgreementGUIField, AgreementDTO> viewTemplate) {
		super();
		this.advisorQualityCodeDTO = advisorQualityCodeDTO;
		this.validManualAQCValues = validManualAQCValues;
		this.viewTemplate = viewTemplate;
	}
	
	
	public AdvisorQualityCodePanelModel(MaintainAQCDetailsDTO maintainAQCDetailsDTO) {
		this(maintainAQCDetailsDTO!=null
				?maintainAQCDetailsDTO.getRequestDTO():null,
			new ValidManualAQCValuesDTO(),
			maintainAQCDetailsDTO!=null
				?new AgreementTemplate(maintainAQCDetailsDTO.getAgreementContext())
				:null);
	}

	public AdvisorQualityCodePanelModel(MaintainAgreementPageModel pageModel) {
		this.advisorQualityCodeDTO =
			pageModel!=null && pageModel.getMaintainAgreementDTO()!=null &&
			pageModel.getMaintainAgreementDTO().getAgreementDTO()!=null
			?pageModel.getMaintainAgreementDTO().getAgreementDTO().getAdvisorQualityCodeDTO()
					:null;
		this.validManualAQCValues = 
			pageModel!=null && pageModel.getValidAgreementValues()!=null
			?new ValidManualAQCValuesDTO(pageModel.getValidAgreementValues().getValidManualRisks(),
					pageModel.getValidAgreementValues().getValidManualRisksForAll(),
					pageModel.getValidAgreementValues().getValidManualInvests(),
					pageModel.getValidAgreementValues().getValidManualInvestsForAll(),
					pageModel.getValidAgreementValues().getValidManualShortTerm(),
					pageModel.getValidAgreementValues().getValidManualShortTermForAll())
			:null;
		this.viewTemplate = 
			pageModel!=null
			?pageModel.getViewTemplate()
					:null;
			
		if(pageModel!=null && pageModel.getMaintainAgreementDTO()!=null &&
				pageModel.getMaintainAgreementDTO().getAgreementDTO()!=null && 	
			pageModel.getMaintainAgreementDTO().getAgreementDTO().getPartyOid() != null){
				setPartyOid(pageModel.getMaintainAgreementDTO().getAgreementDTO().getPartyOid());
		}
		else 
			setPartyOid(0l);		
		
		
	}


	public AdvisorQualityCodeDTO getAdvisorQualityCodeDTO() {
		return advisorQualityCodeDTO;
	}


	public void setAdvisorQualityCodeDTO(AdvisorQualityCodeDTO advisorQualityCodeDTO) {
		this.advisorQualityCodeDTO = advisorQualityCodeDTO;
	}


	public ValidManualAQCValuesDTO getValidManualAQCValues() {
		return validManualAQCValues;
	}


	public void setValidManualAQCValues(ValidManualAQCValuesDTO validManualAQCValues) {
		this.validManualAQCValues = validManualAQCValues;
	}


	public ContextDrivenViewTemplate<AgreementGUIField, AgreementDTO> getViewTemplate() {
		return viewTemplate;
	}


	public void setViewTemplate(
			ContextDrivenViewTemplate<AgreementGUIField, AgreementDTO> viewTemplate) {
		this.viewTemplate = viewTemplate;
	}


	public Long getPartyOid() {
		return partyOid;
	}


	public void setPartyOid(Long partyOid) {
		this.partyOid = partyOid;
	}

	public List<PartyAQCHistoryFLO> getCalculatedAQCHistory() {
		return calculatedAQCHistory;
	}


	public void setCalculatedAQCHistory(List<PartyAQCHistoryFLO> calculatedAQCHistory) {
		this.calculatedAQCHistory = calculatedAQCHistory;		
	}


	public List<PartyAQCHistoryFLO> getManualAQCHistory() {
		return manualAQCHistory;
	}


	public void setManualAQCHistory(List<PartyAQCHistoryFLO> manualAQCHistory) {
		this.manualAQCHistory = manualAQCHistory;		
	}


	public String getTitle() {
		return title;
	}


	public void setTitle(String title) {
		this.title = title;
	}
	
	
}
