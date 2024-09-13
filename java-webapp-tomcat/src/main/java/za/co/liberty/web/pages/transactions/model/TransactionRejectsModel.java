package za.co.liberty.web.pages.transactions.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import za.co.liberty.dto.gui.context.AgreementSearchType;
import za.co.liberty.dto.gui.context.PolicyTransactionTypeEnum;
import za.co.liberty.dto.gui.context.RejectsErrorFlagType;
import za.co.liberty.dto.pretransactionreject.RejectElementDTO;

/**
 * Pagemodel used for reject transaction page.
 * 
 * @author JZB0608
 *
 */
public class TransactionRejectsModel implements Serializable {

	private static final long serialVersionUID = 4137223342247742915L;
	
	private Date fromDate;
	private Date toDate;
	private PolicyTransactionTypeEnum transactionSearchType;
	private RejectsErrorFlagType flagType;
	private String contractNumber;
	private String componentId;
	private List<String> componentIds; //List of Strings
	private List<RejectElementDTO> searchResults;
	
	private String agreementNumber;
	private AgreementSearchType agreementSearchType;

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	public PolicyTransactionTypeEnum getTransactionSearchType() {
		return transactionSearchType;
	}

	public void setTransactionSearchType(PolicyTransactionTypeEnum transactionSearchType) {
		this.transactionSearchType = transactionSearchType;
	}

	public RejectsErrorFlagType getFlagType() {
		return flagType;
	}

	public void setFlagType(RejectsErrorFlagType flagType) {
		this.flagType = flagType;
	}

	public String getContractNumber() {
		return contractNumber;
	}

	public void setContractNumber(String contractNumber) {
		this.contractNumber = contractNumber;
	}

	public List<String> getComponentIds() {
		return componentIds;
	}

	public void setComponentIds(List<String> componentIds) {
		this.componentIds = componentIds;
	}

	public String getComponentId() {
		return componentId;
	}

	public void setComponentId(String componentId) {
		this.componentId = componentId;
	}

	public List<RejectElementDTO> getSearchResults() {
		return searchResults;
	}

	public void setSearchResults(List<RejectElementDTO> searchResults) {
		this.searchResults = searchResults;
	}

	public String getAgreementNumber() {
		return agreementNumber;
	}

	public void setAgreementNumber(String agreementNumber) {
		this.agreementNumber = agreementNumber;
	}

	public AgreementSearchType getAgreementSearchType() {
		return agreementSearchType;
	}

	public void setAgreementSearchType(AgreementSearchType agreementSearchType) {
		this.agreementSearchType = agreementSearchType;
	}

}
