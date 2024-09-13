package za.co.liberty.web.wicket.validation.maintainagreement;

import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.ValidationError;

import za.co.liberty.interfaces.agreements.AgreementCodeType;

/**
 * This validator extends the generic agreement codes validator, 
 * This validates the Organisation Mid six
 * 
 * Validation occurs as per the superclass, with additional validation when
 * handling a caught ObjectNotFoundChainedException
 * 
 * @author sbs0510
 * 
 */
public class OrgMidSixValidator extends AgreementCodeValidator {

	private static final long serialVersionUID = 6714454047745084414L;
	
	private static final String REGEX_MIDSIX = "[\\d+]{6}"; 

	public OrgMidSixValidator(Long existingAgreementId) {
		super(AgreementCodeType.THIRTEEN_DIGIT_CONSULTANT, existingAgreementId);
	}
	
	
	/**
	 * Add the additional check for pattern matching to ensure that
	 * the Org mid six is 6 digits
	 */
	@Override
	public void validate(IValidatable validatable) {
		/**
		 * Validate format of 6 digit code
		 */
		String code = (String)validatable.getValue();
		if (!code.matches(REGEX_MIDSIX)) {
			validatable.error(new ValidationError().addKey("OrgMidSixValidator.format"));
			return;
		}
		/**
		 * Run validation from superclass
		 */
		super.validate(validatable);
	}
	
}
