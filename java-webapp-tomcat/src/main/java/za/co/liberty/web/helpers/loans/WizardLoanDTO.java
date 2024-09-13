package za.co.liberty.web.helpers.loans;

import java.io.Serializable;

public class WizardLoanDTO implements Serializable
{
	private String questionsGroup;
	private String useQuotes;
	private String dontUseQuotes;
	
	private String quotesGroup;
	
	private long agreementNo = 612;
	private String quoteId;
	private String loanAmount;
	private String interestRate;
	private String term;
	private String paymentAmount;
	
	public String getLoanAmount() {
		return loanAmount;
	}
	public void setLoanAmount(String loanAmount) {
		this.loanAmount = loanAmount;
	}
	public String getInterestRate() {
		return interestRate;
	}
	public void setInterestRate(String interestRate) {
		this.interestRate = interestRate;
	}
	public String getTerm() {
		return term;
	}
	public void setTerm(String term) {
		this.term = term;
	}
	public String getPaymentAmount() {
		return paymentAmount;
	}
	public void setPaymentAmount(String paymentAmount) {
		this.paymentAmount = paymentAmount;
	}
	
	public String getQuoteId() {
		return quoteId;
	}
	public void setQuoteId(String quoteId) {
		this.quoteId = quoteId;
	}
	public long getAgreementNo() {
		return agreementNo;
	}
	public void setAgreementNo(long agreementNo) {
		this.agreementNo = agreementNo;
	}
	public String getQuestionsGroup() {
		return questionsGroup;
	}
	public void setQuestionsGroup(String questionsGroup) {
		this.questionsGroup = questionsGroup;
	}
	public String getUseQuotes() {
		return useQuotes;
	}
	public void setUseQuotes(String useQuotes) {
		this.useQuotes = useQuotes;
	}
	public String getDontUseQuotes() {
		return dontUseQuotes;
	}
	public void setDontUseQuotes(String dontUseQuotes) {
		this.dontUseQuotes = dontUseQuotes;
	}
}
