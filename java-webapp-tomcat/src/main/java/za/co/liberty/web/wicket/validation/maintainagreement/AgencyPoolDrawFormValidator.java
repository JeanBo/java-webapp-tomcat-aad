package za.co.liberty.web.wicket.validation.maintainagreement;

import java.math.BigDecimal;
import java.math.RoundingMode;

import javax.naming.NamingException;

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.validation.IFormValidator;

import za.co.liberty.business.security.ISecurityManagement;
import za.co.liberty.common.domain.CurrencyAmount;
import za.co.liberty.common.domain.Percentage;
import za.co.liberty.common.enums.PoolDrawOptionsEnum;
import za.co.liberty.dto.agreement.AgencyPoolAccountDetailDTO;
import za.co.liberty.dto.userprofiles.ISessionUserProfile;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.helpers.lookup.ServiceLocator;
import za.co.liberty.web.system.SRSAuthWebSession;

/**
 * This class represents a form validator for Agency Pool Draw
 * 
 * 
 * 
 * @author SSM2707
 * 
 */
public class AgencyPoolDrawFormValidator implements IFormValidator {

	private static final long serialVersionUID = 5184933489869162251L;

	private AgencyPoolAccountDetailDTO poolDTO;
	
	private transient ISecurityManagement securityManagement;

	public AgencyPoolDrawFormValidator(AgencyPoolAccountDetailDTO poolDTO) {
		super();
		this.poolDTO = poolDTO;
	}

	public FormComponent[] getDependentFormComponents() {
		return null;
	}

	public void validate(Form form) {
		String poolDrawOption;
		Percentage poolDrawRateSelected;
		CurrencyAmount requestedPaymentAmount;
		// Get the Pool Draw selection, if any
		poolDrawOption = poolDTO.getPoolDrawOption();
		poolDrawRateSelected = poolDTO.getPoolDrawRateSelected();
		requestedPaymentAmount = poolDTO.getRequestedPaymentAmount();

		if (poolDrawOption == null) {
			if (poolDrawRateSelected != null || requestedPaymentAmount != null) {
				form.error("Please select a Pool Draw Option.");
			}
		} else if (poolDrawOption.equals(PoolDrawOptionsEnum.PERCENTAGE_OF_BALANCE
				.getValue())) {
			if (poolDrawRateSelected == null) {
				form.error("Please select the desired Pool Draw Rate.");
			}
		} else if (poolDrawOption.equals(PoolDrawOptionsEnum.SPECIFIC_RAND_AMOUNT
				.getValue())) {
			if (requestedPaymentAmount == null) {
				form.error("Please select the desired Payment Amount.");
			} else if (requestedPaymentAmount.isValueZero()) {
				form.error("Please select a non zero Payment Amount.");
			}  else {

				CurrencyAmount poolBal = poolDTO.getCurrentPoolBalance();
				Percentage percAmt = new Percentage(requestedPaymentAmount
						.getValue().divide(poolBal.getValue(), 2,
								RoundingMode.HALF_UP));

				/*
				 * Validation: The Requested Payment Amount must be less than
				 * or equal to 50% of the current pool balance
				 */
				// Get the draw limit based on the pool account balance
				BigDecimal drawLimit = percentageValue(poolDTO
						.getCurrentPoolBalance().getValue(), new BigDecimal(
						50.00));

				ISessionUserProfile userProfile = SRSAuthWebSession.get().getSessionUser();
				
				
				/*
				 * check if the draw amount is less than or equal to the draw
				 * limit.
				 */
				if(getSecurityManagement().canUserTransferMaxPoolBalance(userProfile)){
					//All is good
				}else if (drawLimit.compareTo(requestedPaymentAmount.getValue()) < 0) {
					form.error("The Requested Payment Amount must be less than or equal to 50% of the current pool balance - the value captured exceeds this limit.");
				}
			}
		}

	}
	
	public BigDecimal percentageValue(BigDecimal base, BigDecimal pct){
	    return base.multiply(pct).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);
	}

	public ISecurityManagement getSecurityManagement() {
		
		if(securityManagement == null){
			try {
				securityManagement = ServiceLocator.lookupService(ISecurityManagement.class);
			} catch (NamingException e) {
				throw new CommunicationException(e);
			}
		}
		
		return securityManagement;
	}


}
