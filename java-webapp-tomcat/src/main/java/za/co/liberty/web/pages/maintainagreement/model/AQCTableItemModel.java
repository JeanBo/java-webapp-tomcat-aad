package za.co.liberty.web.pages.maintainagreement.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import za.co.liberty.common.domain.Percentage;
import za.co.liberty.dto.party.aqcdetail.AQCDTO;
import za.co.liberty.dto.party.aqcdetail.AQCValueDTO;
import za.co.liberty.dto.persistence.party.flow.PartyAQCHistoryFLO;
import za.co.liberty.interfaces.rating.difffactor.AQCType;

/**
 * 
 * @author jzb0608
 *
 */
public class AQCTableItemModel implements Serializable{
	
	
	private static final long serialVersionUID = 7011750339953022052L;
	
	//Calculated AQC
	private AQCDTO calculcatedCodeDTO;
	
	//Manual AQC
	private AQCDTO manualCodeDTO;
	
	private AQCDTO originalManualCodeDTO;
	
	//Effective AQC
	private String effectiveValue;
			
	//Valid values for Manual AQC Values DTO
	private List<AQCValueDTO> manualAQCValidValues;
	private List<AQCValueDTO> manualAQCValidValuesForAll;
	
	//Calculated AQC History	 
	private List<PartyAQCHistoryFLO> calculatedAQCHistory = new ArrayList<PartyAQCHistoryFLO>();
	
	//Manual AQC History	 
	private List<PartyAQCHistoryFLO> manualAQCHistory = new ArrayList<PartyAQCHistoryFLO>();	
	
	/**
	 * Flags to see if the History has been fetched then no need to reload again
	 */
	private boolean calcAQCHistoryFetched;
	
	private boolean manualAQCHistoryFetched;
	
	//Required for History
	private AQCType aqcType ; 
	
	private Percentage maxUpfrontCommPercent;
	
	private Percentage originalMaxUpfrontCommPercent;
	
	private Date maxUpfrontCommPercentEndDate;
	
	private Date originalMaxUpfrontCommPercentEndDate;
	
	public AQCTableItemModel() {

	}

	public List<AQCValueDTO> getManualAQCValidValuesForAll() {
		return manualAQCValidValuesForAll;
	}

	public void setManualAQCValidValuesForAll(
			List<AQCValueDTO> manualAQCValidValuesForAll) {
		this.manualAQCValidValuesForAll = manualAQCValidValuesForAll;
	}





	public AQCDTO getCalculcatedCodeDTO() {
		return calculcatedCodeDTO;
	}

	public void setCalculcatedCodeDTO(AQCDTO calculcatedCodeDTO) {
		this.calculcatedCodeDTO = calculcatedCodeDTO;
	}

	public AQCDTO getManualCodeDTO() {
		return manualCodeDTO;
	}

	public void setManualCodeDTO(AQCDTO manualCodeDTO) {
		this.manualCodeDTO = manualCodeDTO;
	}

	public String getEffectiveValue() {
		return effectiveValue;
	}

	public void setEffectiveValue(String effectiveValue) {
		this.effectiveValue = effectiveValue;
	}

	public List<AQCValueDTO> getManualAQCValidValues() {
		return manualAQCValidValues;
	}


	public void setManualAQCValidValues(List<AQCValueDTO> manualAQCValidValues) {
		this.manualAQCValidValues = manualAQCValidValues;
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

	public AQCType getAqcType() {
		return aqcType;
	}


	public void setAqcType(AQCType aqcType) {
		this.aqcType = aqcType;
	}

	public AQCDTO getOriginalManualCodeDTO() {
		return originalManualCodeDTO;
	}

	public void setOriginalManualCodeDTO(AQCDTO originalManualCodeDTO) {
		this.originalManualCodeDTO = originalManualCodeDTO;
	}

	public Percentage getMaxUpfrontCommPercent() {
		return maxUpfrontCommPercent;
	}

	public void setMaxUpfrontCommPercent(Percentage maxUpfrontCommPercent) {
		this.maxUpfrontCommPercent = maxUpfrontCommPercent;
	}

	public Date getMaxUpfrontCommPercentEndDate() {
		return maxUpfrontCommPercentEndDate;
	}

	public void setMaxUpfrontCommPercentEndDate(Date maxUpfrontCommPercentEndDate) {
		this.maxUpfrontCommPercentEndDate = maxUpfrontCommPercentEndDate;
	}

	public Percentage getOriginalMaxUpfrontCommPercent() {
		return originalMaxUpfrontCommPercent;
	}

	public void setOriginalMaxUpfrontCommPercent(Percentage originalMaxUpfrontCommPercent) {
		this.originalMaxUpfrontCommPercent = originalMaxUpfrontCommPercent;
	}

	public Date getOriginalMaxUpfrontCommPercentEndDate() {
		return originalMaxUpfrontCommPercentEndDate;
	}

	public void setOriginalMaxUpfrontCommPercentEndDate(Date originalMaxUpfrontCommPercentEndDate) {
		this.originalMaxUpfrontCommPercentEndDate = originalMaxUpfrontCommPercentEndDate;
	}

}

	