package za.co.liberty.web.pages.maintainagreement.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import za.co.liberty.dto.agreement.AgreementDTO;
import za.co.liberty.dto.party.aqcdetail.AQCDTO;
import za.co.liberty.dto.party.aqcdetail.AQCDetailsWithTypeDTO;
import za.co.liberty.dto.party.aqcdetail.AQCValueDTO;
import za.co.liberty.dto.party.aqcdetail.AdvisorQualityCodeDTO;
import za.co.liberty.dto.party.aqcdetail.EffectiveAQCDTO;
import za.co.liberty.dto.persistence.party.flow.PartyAQCHistoryFLO;
import za.co.liberty.interfaces.rating.difffactor.AQCType;
import za.co.liberty.web.data.enums.fields.AgreementGUIField;
import za.co.liberty.web.wicket.view.ContextDrivenViewTemplate;

public class SubAdvisorQualityCodePanelModel implements Serializable{
	
	
	private static final long serialVersionUID = 7011750339953022052L;
	
	//Calculated AQC
	private AQCDTO calcAQCDTO;
	
	//Manual AQC
	private List<AQCDTO> manualAQCDTOs;
	
	//Effective AQC
	private String effAqcValue;
	
	//Panel Title
	private String title;
		
	//Valid values for Manual AQC Values DTO
	private List<AQCValueDTO> manualAQCValidValues;
	private List<AQCValueDTO> manualAQCValidValuesForAll;
	
	//View template	 
	private ContextDrivenViewTemplate<AgreementGUIField, AgreementDTO> viewTemplate;
	
	//Calculated AQC History	 
	private List<PartyAQCHistoryFLO> calculatedAQCHistory;
	
	//Manual AQC History	 
	private List<PartyAQCHistoryFLO> manualAQCHistory;	
	
	/**
	 * Flags to see if the History has been fetched then no need to reload again
	 */
	private boolean calcAQCHistoryFetched;
	
	private boolean manualAQCHistoryFetched;
	
	//Required for History
	private AdvisorQualityCodePanelModel parentPanelModel;
	private AQCType aqcType ; 
	
	
	
	public SubAdvisorQualityCodePanelModel(AdvisorQualityCodePanelModel panelModel, AQCType aqcType) {
		
		AdvisorQualityCodeDTO  advisorQualityCodeDTO = panelModel.getAdvisorQualityCodeDTO();
		
		List<AQCDetailsWithTypeDTO> aqcDetailsWithTypeDTOs = advisorQualityCodeDTO != null?advisorQualityCodeDTO.getAqcDetailsWithTypeDTO():null;
		List<EffectiveAQCDTO> effectiveAQCValues = advisorQualityCodeDTO != null?advisorQualityCodeDTO.getEffectiveAQCValues():null;
		
		if(aqcDetailsWithTypeDTOs != null){
			
			for(AQCDetailsWithTypeDTO detailsWithTypeDTO:aqcDetailsWithTypeDTOs){
				if(aqcType == detailsWithTypeDTO.getAqcType()){
					setCalcAQCDTO(detailsWithTypeDTO.getCalculatedAQCDTO());
					setManualAQCDTOs(detailsWithTypeDTO.getManualAQCDTOs());
					break;
				}				
			}
		}
		
		if(effectiveAQCValues != null){
			
			for(EffectiveAQCDTO effectiveAQCDTO:effectiveAQCValues){
				if(aqcType == effectiveAQCDTO.getAqcType()){
						setEffAqcValue(effectiveAQCDTO.getValue());
						break;
				}				
			}
		}
		
		this.viewTemplate = panelModel.getViewTemplate();		
		
		if(aqcType == AQCType.RISK || aqcType == AQCType.ELM_RISK){
			this.manualAQCValidValues = panelModel.getValidManualAQCValues() != null?panelModel.getValidManualAQCValues().getValidManualRiskAQCValues(): new ArrayList<AQCValueDTO>();
			this.manualAQCValidValuesForAll = panelModel.getValidManualAQCValues() != null?panelModel.getValidManualAQCValues().getValidManualRiskAQCValuesForAll(): new ArrayList<AQCValueDTO>();
		}else if(aqcType == AQCType.INVESTMENT || aqcType == AQCType.ELM_INVESTMENT){
			this.manualAQCValidValues = panelModel.getValidManualAQCValues() != null?panelModel.getValidManualAQCValues().getValidManualInvAQCValues(): new ArrayList<AQCValueDTO>();
			this.manualAQCValidValuesForAll = panelModel.getValidManualAQCValues() != null?panelModel.getValidManualAQCValues().getValidManualInvAQCValuesForAll(): new ArrayList<AQCValueDTO>();
		}
		setTitle(aqcType.getDesc());
		
		setCalculatedAQCHistory(new ArrayList<PartyAQCHistoryFLO>());
		setManualAQCHistory(new ArrayList<PartyAQCHistoryFLO>());	
		setParentPanelModel(panelModel);
		setAqcType(aqcType);
	}


	public List<AQCValueDTO> getManualAQCValidValuesForAll() {
		return manualAQCValidValuesForAll;
	}


	public void setManualAQCValidValuesForAll(
			List<AQCValueDTO> manualAQCValidValuesForAll) {
		this.manualAQCValidValuesForAll = manualAQCValidValuesForAll;
	}


	public AQCDTO getCalcAQCDTO() {
		return calcAQCDTO;
	}


	public void setCalcAQCDTO(AQCDTO calcAQCDTO) {
		this.calcAQCDTO = calcAQCDTO;
	}


	public String getEffAqcValue() {
		return effAqcValue;
	}


	public void setEffAqcValue(String effAqcValue) {
		this.effAqcValue = effAqcValue;
	}


	public List<AQCDTO> getManualAQCDTOs() {
		return manualAQCDTOs;
	}


	public void setManualAQCDTOs(List<AQCDTO> manualAQCDTOs) {
		if(manualAQCDTOs != null)
		this.manualAQCDTOs = manualAQCDTOs;
		else
			this.manualAQCDTOs = new ArrayList<AQCDTO>();
	}


	public List<AQCValueDTO> getManualAQCValidValues() {
		return manualAQCValidValues;
	}


	public void setManualAQCValidValues(List<AQCValueDTO> manualAQCValidValues) {
		this.manualAQCValidValues = manualAQCValidValues;
	}


	public ContextDrivenViewTemplate<AgreementGUIField, AgreementDTO> getViewTemplate() {
		return viewTemplate;
	}


	public void setViewTemplate(
			ContextDrivenViewTemplate<AgreementGUIField, AgreementDTO> viewTemplate) {
		this.viewTemplate = viewTemplate;
	}


	public String getTitle() {
		return title;
	}


	public void setTitle(String title) {
		this.title = title;
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


	public boolean isCalcAQCHistoryFetched() {
		return calcAQCHistoryFetched;
	}


	public void setCalcAQCHistoryFetched(boolean calcAQCHistoryFetched) {
		this.calcAQCHistoryFetched = calcAQCHistoryFetched;
	}


	public boolean isManualAQCHistoryFetched() {
		return manualAQCHistoryFetched;
	}


	public void setManualAQCHistoryFetched(boolean manualAQCHistoryFetched) {
		this.manualAQCHistoryFetched = manualAQCHistoryFetched;
	}

	public AdvisorQualityCodePanelModel getParentPanelModel() {
		return parentPanelModel;
	}


	public void setParentPanelModel(AdvisorQualityCodePanelModel parentPanelModel) {
		this.parentPanelModel = parentPanelModel;
	}


	public AQCType getAqcType() {
		return aqcType;
	}


	public void setAqcType(AQCType aqcType) {
		this.aqcType = aqcType;
	}	
}

	