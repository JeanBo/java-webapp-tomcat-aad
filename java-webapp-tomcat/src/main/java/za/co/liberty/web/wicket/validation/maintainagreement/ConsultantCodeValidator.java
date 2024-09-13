package za.co.liberty.web.wicket.validation.maintainagreement;

import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.ValidationError;

import za.co.liberty.agreement.common.AgreementObjectReference;
import za.co.liberty.exceptions.data.DataNotFoundException;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.interfaces.agreements.AgreementCodeType;

/**
 * This validator extends the generic agreement codes validator, but adds
 * additional checks when an ObjectNotFoundChainedException is caught, to ensure
 * that the thirteen digit consultant code also does not exist as an active
 * property against a different agreement
 * 
 * Validation occurs as per the superclass, with additional validation when
 * handling a caught ObjectNotFoundChainedException
 * 
 * @author kxd1203
 * 
 */
public class ConsultantCodeValidator extends AgreementCodeValidator {

	private static final long serialVersionUID = 6714454047745084414L;
	
	private static final String REGEX_CONSULTANT_CODE = "[\\d+]{13}"; 

	public ConsultantCodeValidator(Long existingAgreementId) {
		super(AgreementCodeType.THIRTEEN_DIGIT_CONSULTANT, existingAgreementId);
	}
	
	
	/**
	 * Add the additional check for pattern matching to ensure that
	 * the consultant code is 13 digits
	 */
	@Override
	public void validate(IValidatable validatable) {
		/**
		 * Validate format of 13 digit code
		 */
		String code = (String)validatable.getValue();
		if (!code.matches(REGEX_CONSULTANT_CODE)) {
			validatable.error(new ValidationError().addKey("ConsultantCodeValidator.format"));
			return;
		}
		/**
		 * Run validation from superclass
		 */
		super.validate(validatable);
	}



	/**
	 * ObjectNotFoundChainedException caught, so the code does not exist on
	 * AgreementCodes table.
	 * 
	 * Do additional checks and validation to ensure that the code does not
	 * exist on any agreements as an active property
	 * 
	 */
	@Override
	protected void handleObjectNotFoundException(IValidatable validatable) {
		debug("Validate 13 Digit Code - Object Not Found Exception - Check in agreement....",null);
		AgreementObjectReference agreementObjectReference = null;
		try {
			agreementObjectReference = 
				getAgreementCodesValidation()
				.findAgreementObjectReferenceForConsultantCodeProperty(
						Long.parseLong((String)validatable.getValue()));
			if (agreementObjectReference==null) {
				throw new DataNotFoundException("Agreement not found");
			}
		} catch (CommunicationException e) {
			debug("DataAccessException caught when validating " +
					"the 13 digit code, registering an error",e);
			validatable.error(new ValidationError().addKey("validator.duplicate"));
			return;
		} catch (DataNotFoundException e) {
			debug("DataNotFoundException caught when validating " +
					"the 13 digit code, returning",e);
			return;
		}
		if (agreementObjectReference == null) {
			debug("Validate 13 Digit Code - Object Not Found " +
					"in agreement.",null);
			return;
		}
		if ((getExistingAgreementId() != null)
				&& (getExistingAgreementId().longValue() == agreementObjectReference
						.getObjectOid())) {
			// Found 13 digit code, but on existing agreement
			debug("Found 13 Digit Code on current agreement "
							+ getExistingAgreementId()
							+ " : "
							+ agreementObjectReference.getObjectOid(),null);
			return;
		}
		debug("Returning False",null);
		validatable.error(new ValidationError().addKey("validator.duplicate"));
	}

}
