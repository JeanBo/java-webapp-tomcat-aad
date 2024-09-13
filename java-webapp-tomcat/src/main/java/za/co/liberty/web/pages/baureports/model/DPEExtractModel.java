package za.co.liberty.web.pages.baureports.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class DPEExtractModel implements Serializable {
	
	public String qualityCentreId;
	public List<Long> commKinds;
	public List<Long> agreementKinds;
	public List<Long> productReferences;
	public List<Long> agreementNumbers;
	public List<Integer> movementCodes;
	public List<Integer> benefitTypes;
	public List<Integer> premiumFrequencies;
	public List<Integer> contrIncrIndicators;
	public Date requestedDateFrom;
	public Date requestedDateTo;
	public String emailId;
	
	/**
	 * @return the agreementKinds
	 */
	public List<Long> getAgreementKinds() {
		return agreementKinds;
	}
	/**
	 * @param agreementKinds the agreementKinds to set
	 */
	public void setAgreementKinds(List<Long> agreementKinds) {
		this.agreementKinds = agreementKinds;
	}
	/**
	 * @return the agreementNumbers
	 */
	public List<Long> getAgreementNumbers() {
		return agreementNumbers;
	}
	/**
	 * @param agreementNumbers the agreementNumbers to set
	 */
	public void setAgreementNumbers(List<Long> agreementNumbers) {
		this.agreementNumbers = agreementNumbers;
	}
	/**
	 * @return the benefitTypes
	 */
	public List<Integer> getBenefitTypes() {
		return benefitTypes;
	}
	/**
	 * @param benefitTypes the benefitTypes to set
	 */
	public void setBenefitTypes(List<Integer> benefitTypes) {
		this.benefitTypes = benefitTypes;
	}
	/**
	 * @return the commKinds
	 */
	public List<Long> getCommKinds() {
		return commKinds;
	}
	/**
	 * @param commKinds the commKinds to set
	 */
	public void setCommKinds(List<Long> commKinds) {
		this.commKinds = commKinds;
	}
	/**
	 * @return the contrIncrIndicators
	 */
	public List<Integer> getContrIncrIndicators() {
		return contrIncrIndicators;
	}
	/**
	 * @param contrIncrIndicators the contrIncrIndicators to set
	 */
	public void setContrIncrIndicators(List<Integer> contrIncrIndicators) {
		this.contrIncrIndicators = contrIncrIndicators;
	}
	/**
	 * @return the emailId
	 */
	public String getEmailId() {
		return emailId;
	}
	/**
	 * @param emailId the emailId to set
	 */
	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}
	/**
	 * @return the movementCodes
	 */
	public List<Integer> getMovementCodes() {
		return movementCodes;
	}
	/**
	 * @param movementCodes the movementCodes to set
	 */
	public void setMovementCodes(List<Integer> movementCodes) {
		this.movementCodes = movementCodes;
	}
	/**
	 * @return the premiumFrequencies
	 */
	public List<Integer> getPremiumFrequencies() {
		return premiumFrequencies;
	}
	/**
	 * @param premiumFrequencies the premiumFrequencies to set
	 */
	public void setPremiumFrequencies(List<Integer> premiumFrequencies) {
		this.premiumFrequencies = premiumFrequencies;
	}
	/**
	 * @return the productReferences
	 */
	public List<Long> getProductReferences() {
		return productReferences;
	}
	/**
	 * @param productReferences the productReferences to set
	 */
	public void setProductReferences(List<Long> productReferences) {
		this.productReferences = productReferences;
	}
	/**
	 * @return the qualityCentreId
	 */
	public String getQualityCentreId() {
		return qualityCentreId;
	}
	/**
	 * @param qualityCentreId the qualityCentreId to set
	 */
	public void setQualityCentreId(String qualityCentreId) {
		this.qualityCentreId = qualityCentreId;
	}
	/**
	 * @return the requestedDateFrom
	 */
	public Date getRequestedDateFrom() {
		return requestedDateFrom;
	}
	/**
	 * @param requestedDateFrom the requestedDateFrom to set
	 */
	public void setRequestedDateFrom(Date requestedDateFrom) {
		this.requestedDateFrom = requestedDateFrom;
	}
	/**
	 * @return the requestedDateTo
	 */
	public Date getRequestedDateTo() {
		return requestedDateTo;
	}
	/**
	 * @param requestedDateTo the requestedDateTo to set
	 */
	public void setRequestedDateTo(Date requestedDateTo) {
		this.requestedDateTo = requestedDateTo;
	}

}
