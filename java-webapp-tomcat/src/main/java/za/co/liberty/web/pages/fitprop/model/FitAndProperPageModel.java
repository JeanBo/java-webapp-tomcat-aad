package za.co.liberty.web.pages.fitprop.model;

import java.io.Serializable;
import java.util.Date;


/**
 * Page model for the Fit and Proper screen
 * @author DZS2610
 *
 */
public class FitAndProperPageModel implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private long agreementNumber;
	
	private int agreementKind;
	
	private Date agreementStartdate;
	
	private long partyoid;
	
	private boolean userManagesAgreement;
	

	public boolean isUserManagesAgreement() {
		return userManagesAgreement;
	}

	public void setUserManagesAgreement(boolean userManagesAgreement) {
		this.userManagesAgreement = userManagesAgreement;
	}

	public long getAgreementNumber() {
		return agreementNumber;
	}

	public void setAgreementNumber(long agreementNumber) {
		this.agreementNumber = agreementNumber;
	}

	public long getPartyoid() {
		return partyoid;
	}

	public void setPartyoid(long partyoid) {
		this.partyoid = partyoid;
	}

	public int getAgreementKind() {
		return agreementKind;
	}

	public void setAgreementKind(int agreementKind) {
		this.agreementKind = agreementKind;
	}

	public Date getAgreementStartdate() {
		return agreementStartdate;
	}

	public void setAgreementStartdate(Date agreementStartdate) {
		this.agreementStartdate = agreementStartdate;
	}
	
}
