/**
 * 
 */
package za.co.liberty.web.pages.transactions.model;

import java.io.Serializable;
import java.util.List;

import za.co.liberty.dto.gui.context.PolicyTransactionTypeEnum;
import za.co.liberty.dto.gui.request.FundCodeDTO;
import za.co.liberty.dto.gui.request.ProductCodeDTO;
import za.co.liberty.dto.gui.templates.DescriptionDTO;
import za.co.liberty.dto.transaction.IPolicyTransactionDTO;
import za.co.liberty.dto.transaction.IPolicyTransactionModel;
import za.co.liberty.dto.transaction.PolicyInfoCommissionCalculationDTO;
import za.co.liberty.interfaces.agreements.FrequencyType;

/**
 * @author zzt2108
 *
 */
public class PolicyTransactionModel implements Serializable, IPolicyTransactionModel {

	private static final long serialVersionUID = 5506237907805524162L;
	
	private IPolicyTransactionDTO selectedObject;
	
	private PolicyTransactionTypeEnum transactionType;
	private List<FundCodeDTO> allFundCodes;
	private FundCodeDTO fundCode;
	
	private List<ProductCodeDTO> allProductCodes;
	private List<ProductCodeDTO> currentProductCodes;
	
	private List<DescriptionDTO> commissionKinds;
	private DescriptionDTO contributionIncIndicator;
	private List<DescriptionDTO> allContributionIncIndicators;
	private FrequencyType frequencyType;
	private List<DescriptionDTO> allProductReferences;
	private DescriptionDTO productReference;
	private List<DescriptionDTO> benefitTypes;
	private List<String> businessUnitList;
	private List<String> glCompanyList;
	private List<Integer> numberOfMonthsList;
	private List<Integer> commissionFrequencyList;
	private String transactionTypeLabel;
	private Long topAgreementId;
	private List<DescriptionDTO> allFrequencyTypes;
	private List<DescriptionDTO> allFrequencyTypesForPolicyInfo;
	private List<DescriptionDTO> allInfoKindTypes;
	private List<DescriptionDTO> benefitGroups;
	
	private PolicyInfoCommissionCalculationDTO selectedPolicyInfoCalculation;
	
	private List<Integer> pcrCodes;
	private List<FundCodeDTO> currentFundCodes;
	
	private String currentProductName;
	
	private String currentFundCategory;
	
	private List<FundCodeDTO> allDfmModelCodes;

	
	public List<DescriptionDTO> getAllInfoKindTypes() {
		return allInfoKindTypes;
	}

	public void setAllInfoKindTypes(List<DescriptionDTO> infoKindTypes) {
		this.allInfoKindTypes = infoKindTypes;
	}

	public IPolicyTransactionDTO getSelectedObject() {
		return selectedObject;
	}

	public void setSelectedObject(IPolicyTransactionDTO selectedObject) {
		this.selectedObject = selectedObject;
	}

	/* (non-Javadoc)
	 * @see za.co.liberty.web.pages.transactions.model.IPolicyTransactionModel#getCommissionFrequencyList()
	 */
	public List<Integer> getCommissionFrequencyList() {
		return commissionFrequencyList;
	}

	/* (non-Javadoc)
	 * @see za.co.liberty.web.pages.transactions.model.IPolicyTransactionModel#setCommissionFrequencyList(java.util.List)
	 */
	public void setCommissionFrequencyList(List<Integer> commissionFrequencyList) {
		this.commissionFrequencyList = commissionFrequencyList;
	}

	/* (non-Javadoc)
	 * @see za.co.liberty.web.pages.transactions.model.IPolicyTransactionModel#getNumberOfMonthsList()
	 */
	public List<Integer> getNumberOfMonthsList() {
		return numberOfMonthsList;
	}

	/* (non-Javadoc)
	 * @see za.co.liberty.web.pages.transactions.model.IPolicyTransactionModel#setNumberOfMonthsList(java.util.List)
	 */
	public void setNumberOfMonthsList(List<Integer> numberOfMonthsList) {
		this.numberOfMonthsList = numberOfMonthsList;
	}


	/* (non-Javadoc)
	 * @see za.co.liberty.web.pages.transactions.model.IPolicyTransactionModel#getAllProductCodes()
	 */
	public List<ProductCodeDTO> getAllProductCodes() {
		return allProductCodes;
	}

	/* (non-Javadoc)
	 * @see za.co.liberty.web.pages.transactions.model.IPolicyTransactionModel#setAllProductCodes(java.util.List)
	 */
	public void setAllProductCodes(List<ProductCodeDTO> allProductCodes) {
		this.allProductCodes = allProductCodes;
	}

	/* (non-Javadoc)
	 * @see za.co.liberty.web.pages.transactions.model.IPolicyTransactionModel#getTransactionType()
	 */
	public PolicyTransactionTypeEnum getTransactionType() {
		return transactionType;
	}

	/* (non-Javadoc)
	 * @see za.co.liberty.web.pages.transactions.model.IPolicyTransactionModel#setTransactionType(za.co.liberty.dto.gui.context.PolicyTransactionTypeEnum)
	 */
	public void setTransactionType(PolicyTransactionTypeEnum transactionType) {
		this.transactionType = transactionType;
	}

	/* (non-Javadoc)
	 * @see za.co.liberty.web.pages.transactions.model.IPolicyTransactionModel#getAllFundCodes()
	 */
	public List<FundCodeDTO> getAllFundCodes() {
		return allFundCodes;
	}

	/* (non-Javadoc)
	 * @see za.co.liberty.web.pages.transactions.model.IPolicyTransactionModel#setAllFundCodes(java.util.List)
	 */
	public void setAllFundCodes(List<FundCodeDTO> allFundCodes) {
		this.allFundCodes = allFundCodes;
	}

	/* (non-Javadoc)
	 * @see za.co.liberty.web.pages.transactions.model.IPolicyTransactionModel#getFundCode()
	 */
	public FundCodeDTO getFundCode() {
		return fundCode;
	}

	/* (non-Javadoc)
	 * @see za.co.liberty.web.pages.transactions.model.IPolicyTransactionModel#setFundCode(za.co.liberty.dto.gui.request.FundCodeDTO)
	 */
	public void setFundCode(FundCodeDTO fundCode) {
		this.fundCode = fundCode;
	}


	/* (non-Javadoc)
	 * @see za.co.liberty.web.pages.transactions.model.IPolicyTransactionModel#getCommissionKinds()
	 */
	public List<DescriptionDTO> getCommissionKinds() {
		return commissionKinds;
	}

	/* (non-Javadoc)
	 * @see za.co.liberty.web.pages.transactions.model.IPolicyTransactionModel#setCommissionKinds(java.util.List)
	 */
	public void setCommissionKinds(List<DescriptionDTO> commissionKinds) {
		this.commissionKinds = commissionKinds;
	}

	/* (non-Javadoc)
	 * @see za.co.liberty.web.pages.transactions.model.IPolicyTransactionModel#getAllContributionIncIndicators()
	 */
	public List<DescriptionDTO> getAllContributionIncIndicators() {
		return allContributionIncIndicators;
	}

	/* (non-Javadoc)
	 * @see za.co.liberty.web.pages.transactions.model.IPolicyTransactionModel#setAllContributionIncIndicators(java.util.List)
	 */
	public void setAllContributionIncIndicators(List<DescriptionDTO> allContributionIncIndicators) {
		this.allContributionIncIndicators = allContributionIncIndicators;
	}

	/* (non-Javadoc)
	 * @see za.co.liberty.web.pages.transactions.model.IPolicyTransactionModel#getContributionIncIndicator()
	 */
	public DescriptionDTO getContributionIncIndicator() {
		return contributionIncIndicator;
	}

	/* (non-Javadoc)
	 * @see za.co.liberty.web.pages.transactions.model.IPolicyTransactionModel#setContributionIncIndicator(za.co.liberty.dto.gui.templates.DescriptionDTO)
	 */
	public void setContributionIncIndicator(DescriptionDTO contributionIncIndicator) {
		this.contributionIncIndicator = contributionIncIndicator;
	}

	/* (non-Javadoc)
	 * @see za.co.liberty.web.pages.transactions.model.IPolicyTransactionModel#getFrequencyType()
	 */
	public FrequencyType getFrequencyType() {
		return frequencyType;
	}

	/* (non-Javadoc)
	 * @see za.co.liberty.web.pages.transactions.model.IPolicyTransactionModel#setFrequencyType(za.co.liberty.interfaces.agreements.FrequencyType)
	 */
	public void setFrequencyType(FrequencyType frequencyType) {
		this.frequencyType = frequencyType;
	}

	/* (non-Javadoc)
	 * @see za.co.liberty.web.pages.transactions.model.IPolicyTransactionModel#getAllProductReferences()
	 */
	public List<DescriptionDTO> getAllProductReferences() {
		return allProductReferences;
	}

	/* (non-Javadoc)
	 * @see za.co.liberty.web.pages.transactions.model.IPolicyTransactionModel#setAllProductReferences(java.util.List)
	 */
	public void setAllProductReferences(List<DescriptionDTO> allProductReferences) {
		this.allProductReferences = allProductReferences;
	}

	/* (non-Javadoc)
	 * @see za.co.liberty.web.pages.transactions.model.IPolicyTransactionModel#getProductReference()
	 */
	public DescriptionDTO getProductReference() {
		return productReference;
	}

	/* (non-Javadoc)
	 * @see za.co.liberty.web.pages.transactions.model.IPolicyTransactionModel#setProductReference(za.co.liberty.dto.gui.templates.DescriptionDTO)
	 */
	public void setProductReference(DescriptionDTO productReference) {
		this.productReference = productReference;
	}

	/* (non-Javadoc)
	 * @see za.co.liberty.web.pages.transactions.model.IPolicyTransactionModel#getDpeAmount()
	 */
	/*public BigDecimal getDpeAmount() {
		return dpeAmount;
	}

	 (non-Javadoc)
	 * @see za.co.liberty.web.pages.transactions.model.IPolicyTransactionModel#setDpeAmount(java.math.BigDecimal)
	 
	public void setDpeAmount(BigDecimal dpeAmount) {
		this.dpeAmount = dpeAmount;
	}

	 (non-Javadoc)
	 * @see za.co.liberty.web.pages.transactions.model.IPolicyTransactionModel#getCommissionFrequency()
	 
	public Integer getCommissionFrequency() {
		return commissionFrequency;
	}

	 (non-Javadoc)
	 * @see za.co.liberty.web.pages.transactions.model.IPolicyTransactionModel#setCommissionFrequency(java.lang.Integer)
	 
	public void setCommissionFrequency(Integer commissionFrequency) {
		this.commissionFrequency = commissionFrequency;
	}

	 (non-Javadoc)
	 * @see za.co.liberty.web.pages.transactions.model.IPolicyTransactionModel#getGrowthPension()
	 
	public Boolean getGrowthPension() {
		return growthPension;
	}

	 (non-Javadoc)
	 * @see za.co.liberty.web.pages.transactions.model.IPolicyTransactionModel#setGrowthPension(java.lang.Boolean)
	 
	public void setGrowthPension(Boolean growthPension) {
		this.growthPension = growthPension;
	}

	 (non-Javadoc)
	 * @see za.co.liberty.web.pages.transactions.model.IPolicyTransactionModel#getCommissionBalance()
	 
	public Boolean getCommissionBalance() {
		return commissionBalance;
	}

	 (non-Javadoc)
	 * @see za.co.liberty.web.pages.transactions.model.IPolicyTransactionModel#setCommissionBalance(java.lang.Boolean)
	 
	public void setCommissionBalance(Boolean commissionBalance) {
		this.commissionBalance = commissionBalance;
	}

	 (non-Javadoc)
	 * @see za.co.liberty.web.pages.transactions.model.IPolicyTransactionModel#getMovementEffectiveDate()
	 
	public Date getMovementEffectiveDate() {
		return movementEffectiveDate;
	}

	 (non-Javadoc)
	 * @see za.co.liberty.web.pages.transactions.model.IPolicyTransactionModel#setMovementEffectiveDate(java.util.Date)
	 
	public void setMovementEffectiveDate(Date movementEffectiveDate) {
		this.movementEffectiveDate = movementEffectiveDate;
	}*/

	/* (non-Javadoc)
	 * @see za.co.liberty.web.pages.transactions.model.IPolicyTransactionModel#getBenefitTypes()
	 */
	public List<DescriptionDTO> getBenefitTypes() {
		return benefitTypes;
	}

	/* (non-Javadoc)
	 * @see za.co.liberty.web.pages.transactions.model.IPolicyTransactionModel#setBenefitTypes(java.util.List)
	 */
	public void setBenefitTypes(List<DescriptionDTO> benefitTypes) {
		this.benefitTypes = benefitTypes;
	}

//	/* (non-Javadoc)
//	 * @see za.co.liberty.web.pages.transactions.model.IPolicyTransactionModel#getBenefitType()
//	 */
//	public DescriptionDTO getBenefitType() {
//		return benefitType;
//	}
//
//	/* (non-Javadoc)
//	 * @see za.co.liberty.web.pages.transactions.model.IPolicyTransactionModel#setBenefitType(za.co.liberty.dto.gui.templates.DescriptionDTO)
//	 */
//	public void setBenefitType(DescriptionDTO benefitType) {
//		this.benefitType = benefitType;
//	}

	/* (non-Javadoc)
	 * @see za.co.liberty.web.pages.transactions.model.IPolicyTransactionModel#getBusinessUnitList()
	 */
	public List<String> getBusinessUnitList() {
		return businessUnitList;
	}

	/* (non-Javadoc)
	 * @see za.co.liberty.web.pages.transactions.model.IPolicyTransactionModel#setBusinessUnitList(java.util.List)
	 */
	public void setBusinessUnitList(List<String> businessUnitList) {
		this.businessUnitList = businessUnitList;
	}

	/* (non-Javadoc)
	 * @see za.co.liberty.web.pages.transactions.model.IPolicyTransactionModel#getGlCompanyList()
	 */
	public List<String> getGlCompanyList() {
		return glCompanyList;
	}

	/* (non-Javadoc)
	 * @see za.co.liberty.web.pages.transactions.model.IPolicyTransactionModel#setGlCompanyList(java.util.List)
	 */
	public void setGlCompanyList(List<String> glCompanyList) {
		this.glCompanyList = glCompanyList;
	}


	/* (non-Javadoc)
	 * @see za.co.liberty.web.pages.transactions.model.IPolicyTransactionModel#setTransactionTypeLabel(java.lang.String)
	 */
	public void setTransactionTypeLabel(String transactionTypeLabel) {
		this.transactionTypeLabel = transactionTypeLabel;
	}

	public String getTransactionTypeLabel() {
		return transactionTypeLabel;
	}

	public Long getTopAgreementId() {
		return topAgreementId;
	}

	public void setTopAgreementId(Long topAgreementId) {
		this.topAgreementId = topAgreementId;
	}

	public List<DescriptionDTO> getAllFrequencyTypes() {
		return allFrequencyTypes;
	}

	public void setAllFrequencyTypes(List<DescriptionDTO> allFrequencyTypes) {
		this.allFrequencyTypes = allFrequencyTypes;
	}

	public PolicyInfoCommissionCalculationDTO getSelectedPolicyInfoCalculation() {
		return selectedPolicyInfoCalculation;
	}

	public void setSelectedPolicyInfoCalculation(
			PolicyInfoCommissionCalculationDTO selectedPolicyInfoCalculation) {
		this.selectedPolicyInfoCalculation = selectedPolicyInfoCalculation;
	}

	public List<DescriptionDTO> getAllFrequencyTypesForPolicyInfo() {
		return allFrequencyTypesForPolicyInfo;
	}

	public void setAllFrequencyTypesForPolicyInfo(
			List<DescriptionDTO> allFrequencyTypesForPolicyInfo) {
		this.allFrequencyTypesForPolicyInfo = allFrequencyTypesForPolicyInfo;
	}

	public List<ProductCodeDTO> getCurrentProductCodes() {
		return currentProductCodes;
	}

	public void setCurrentProductCodes(List<ProductCodeDTO> currentProductCodes) {
		this.currentProductCodes = currentProductCodes;
	}

	public List<DescriptionDTO> getBenefitGroups() {
		return benefitGroups;
	}

	public void setBenefitGroups(List<DescriptionDTO> benefitGroups) {
		this.benefitGroups = benefitGroups;
	}

	
	public List<Integer> getPcrCodes() {
		return pcrCodes;
	}

	public void setPcrCodes(List<Integer> pcrCodes) {
		this.pcrCodes = pcrCodes;
	}

	public List<FundCodeDTO> getCurrentFundCodes() {
		return currentFundCodes;
	}

	public void setCurrentFundCodes(List<FundCodeDTO> currentFundCodes) {
		this.currentFundCodes = currentFundCodes;
	}

	public String getCurrentProductName() {
		return currentProductName != null ?currentProductName:"";
	}

	public void setCurrentProductName(String currentProductName) {
		this.currentProductName = currentProductName;
	}

	public String getCurrentFundCategory() {
		return currentFundCategory != null ? currentFundCategory: "N/A";
	}

	public void setCurrentFundCategory(String currentFundCategory) {
		this.currentFundCategory = currentFundCategory;
	}
	public List<FundCodeDTO> getAllDfmModelCodes() {
		return allDfmModelCodes;
	}

	public void setAllDfmModelCodes(List<FundCodeDTO> allDfmModelCodes) {
		this.allDfmModelCodes = allDfmModelCodes;
	}

	public List<DescriptionDTO> getAllGipTransactionCodes() {
		return null;
	}


	public void setAllGipTransactionCodes(List<DescriptionDTO> list) {

	}

	public List<FundCodeDTO> getCurrentDfmModelCodes() {
		return null;
	}

	public void setCurrentDfmModelCodes(List<FundCodeDTO> list) {

	}


}
