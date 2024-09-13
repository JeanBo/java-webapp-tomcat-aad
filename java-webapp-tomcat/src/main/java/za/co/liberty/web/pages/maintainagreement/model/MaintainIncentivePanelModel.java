package za.co.liberty.web.pages.maintainagreement.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import za.co.liberty.dto.agreement.IncentiveDetailDTO;
import za.co.liberty.srs.type.SRSType;

/**
 * Panel Model for the agreement incentive panel
 */
public class MaintainIncentivePanelModel implements Serializable {

	private static final long serialVersionUID = 1643792587546952282L;
	
	private long agreementnumber;
	
	private Date agreementStartDate;
	
	private long agreementKind;
	
	private String agreementStatusString;	
	
	private List<IncentiveDetailDTO> currentAndfutureIncentiveDetails;
	
	private List<IncentiveDetailDTO> availableIncentivesDetails;
	
	private List<IncentiveDetailDTO> availableIntermediaryLevelIncentives = new ArrayList<IncentiveDetailDTO>();
	
	private List<IncentiveDetailDTO> availableManagerLevelIncentives = new ArrayList<IncentiveDetailDTO>();
	
	private List<IncentiveDetailDTO> currentAndFutureIntermediaryLevelIncentives = new ArrayList<IncentiveDetailDTO>();
	
	private List<IncentiveDetailDTO> currentAndFutureManagerLevelIncentives = new ArrayList<IncentiveDetailDTO>();
	
	/**
	 * Values for the dropdown
	 */
	private List<String> allowedManPowerValues;
	
	private List<Long> allowedGEPamounts; 
	
	public List<Long> getAllowedGEPamounts() {
		return allowedGEPamounts;
	}

	public void setAllowedGEPamounts(List<Long> allowedGEPamounts) {
		this.allowedGEPamounts = allowedGEPamounts;
	}

	public List<String> getAllowedManPowerValues() {
		return allowedManPowerValues;
	}

	public void setAllowedManPowerValues(List<String> allowedManPowerValues) {
		this.allowedManPowerValues = allowedManPowerValues;
	}

	public List<IncentiveDetailDTO> getAvailableIncentivesDetails() {
		return availableIncentivesDetails;
	}

	public void setAvailableIncentivesDetails(
			List<IncentiveDetailDTO> availableIncentivesDetails) {
		this.availableIncentivesDetails = availableIncentivesDetails;
	}

	public long getAgreementnumber() {
		return agreementnumber;
	}

	public void setAgreementnumber(long agreementnumber) {
		this.agreementnumber = agreementnumber;
	}

	public List<IncentiveDetailDTO> getCurrentAndfutureIncentiveDetails() {
		return currentAndfutureIncentiveDetails;
	}

	public void setCurrentAndfutureIncentiveDetails(
			List<IncentiveDetailDTO> currentAndfutureIncentiveDetails) {
		this.currentAndfutureIncentiveDetails = currentAndfutureIncentiveDetails;
	}
	
	/**
	 * returns a list of current and future dated manager level incetives out of the given incetive list
	 * @return
	 */
	public List<IncentiveDetailDTO> getCurrentAndFutureManagerLevelIncentives(){
		currentAndFutureManagerLevelIncentives.clear();
		if(currentAndfutureIncentiveDetails == null || currentAndfutureIncentiveDetails.size() == 0){
			return currentAndFutureManagerLevelIncentives;
		}
		for(IncentiveDetailDTO incentive : currentAndfutureIncentiveDetails){
				if(incentive.getIncentiveLevelType() == SRSType.MANAGER_LEVEL){
					currentAndFutureManagerLevelIncentives.add(incentive);
				}
		}
		return currentAndFutureManagerLevelIncentives;		
	}
	
	/**
	 * returns a list of current and future dated manager level incetives  out of the given incetive list
	 * @return
	 */
	public List<IncentiveDetailDTO> getCurrentAndFutureIntermediaryLevelIncentives(){		
		currentAndFutureIntermediaryLevelIncentives.clear();
		if(currentAndfutureIncentiveDetails == null || currentAndfutureIncentiveDetails.size() == 0){
			return currentAndFutureIntermediaryLevelIncentives;
		}
		for(IncentiveDetailDTO incentive : currentAndfutureIncentiveDetails){
				if(incentive.getIncentiveLevelType() == SRSType.INTERMEDIARY_LEVEL){
					currentAndFutureIntermediaryLevelIncentives.add(incentive);
				}
		}
		return currentAndFutureIntermediaryLevelIncentives;		
	}
	
	/**
	 * returns a list available manager level incentives out of the given incentive list
	 * @return
	 */
	public List<IncentiveDetailDTO> getAvailableManagerLevelIncentives(){
		availableManagerLevelIncentives.clear();
		if(availableIncentivesDetails == null || availableIncentivesDetails.size() == 0){
			return availableManagerLevelIncentives;
		}
		for(IncentiveDetailDTO incentive : availableIncentivesDetails){
				if(incentive.getIncentiveLevelType() == SRSType.MANAGER_LEVEL){
					availableManagerLevelIncentives.add(incentive);
				}
		}
		return availableManagerLevelIncentives;		
	}
	
	/**
	 * returns a list of available intermediary level incentives  out of the given incentive list
	 * @return
	 */
	public List<IncentiveDetailDTO> getAvailableIntermediaryLevelIncentives(){		
		availableIntermediaryLevelIncentives.clear();
		if(availableIncentivesDetails == null || availableIncentivesDetails.size() == 0){
			return availableIntermediaryLevelIncentives;
		}
		for(IncentiveDetailDTO incentive : availableIncentivesDetails){
				if(incentive.getIncentiveLevelType() == SRSType.INTERMEDIARY_LEVEL){
					availableIntermediaryLevelIncentives.add(incentive);
				}
		}
		return availableIntermediaryLevelIncentives;		
	}
	
	/**
	 * Add an icentive to the current list
	 *
	 */
	public void addIncentive(IncentiveDetailDTO incentiveDetailDTO){
		getCurrentAndfutureIncentiveDetails().add(incentiveDetailDTO);
		getAvailableIncentivesDetails().remove(incentiveDetailDTO);
		//refresh the other lists		
		getCurrentAndFutureIntermediaryLevelIncentives();
		getCurrentAndFutureManagerLevelIncentives();
		getAvailableIntermediaryLevelIncentives();
		getAvailableManagerLevelIncentives();
	}	

	public Date getAgreementStartDate() {
		return agreementStartDate;
	}

	public void setAgreementStartDate(Date agreementStartDate) {
		this.agreementStartDate = agreementStartDate;
	}

	public String getAgreementStatusString() {
		return agreementStatusString;
	}

	public void setAgreementStatusString(String agreementStatusString) {
		this.agreementStatusString = agreementStatusString;
	}

	public long getAgreementKind() {
		return agreementKind;
	}

	public void setAgreementKind(long agreementKind) {
		this.agreementKind = agreementKind;
	}	
}
