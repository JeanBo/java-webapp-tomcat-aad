package za.co.liberty.web.wicket.validation.maintainagreement;

import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.ValidationError;

import za.co.liberty.agreement.common.AgreementObjectReference;
import za.co.liberty.business.agreement.validator.IAgreementCodesValidation;
import za.co.liberty.business.agreement.validator.IAgreementValidator;
import za.co.liberty.exceptions.application.ObjectNotFoundChainedException;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.helpers.lookup.ServiceLocator;
import za.co.liberty.interfaces.agreements.AgreementCodeType;

/**
 * This class represents a generic validator for all agreement codes. Validation
 * is performed by a business method within {@link AgreementCodeValidation}
 * which looks for duplicate codes of a specified kind in the agreement codes
 * table.
 * 
 * This class uses {@link AgreementCodeValidation} from SRSBusEJB directly, as
 * there is no mechanism to call SRSBusEJB from SRSBusinessLayer, and this class
 * needs to make use of existing functionality within SRSBusEJB.
 * 
 * @author kxd1203
 * 
 */
public class AgreementCodeValidator implements IValidator  {

	private static final long serialVersionUID = 8761329903883390188L;

	private transient IAgreementCodesValidation validation;

	private Long existingAgreementId;

	private AgreementCodeType agreementCodeType;
	
	private transient IAgreementValidator agreementValidator;
	
	private transient Logger logger = Logger.getLogger(AgreementCodeValidator.class);

	/**
	 * Constructor with the specified agreement code type to validate, and the
	 * existing agreement id (if one exists)
	 * 
	 * @param agreementCodesType
	 *            the type of code to validate
	 * @param existingAgreementId
	 *            the existing agreement oid if any
	 */
	public AgreementCodeValidator(AgreementCodeType agreementCodesType,
			Long existingAgreementId) {
		super();
		this.existingAgreementId = existingAgreementId;
		this.agreementCodeType = agreementCodesType;
	}

	IAgreementCodesValidation getAgreementCodesValidation() {
		if (validation == null) {
			try {
				validation = ServiceLocator.lookupService(IAgreementCodesValidation.class);
			} catch (NamingException e) {
				throw new CommunicationException("Could not lookup Agreement Codes Validation",e);
			}
		}
		return validation;
	}
	
	/**
	 * Gets the AgreementValidator EJB
	 * @return
	 */
	IAgreementValidator getAgreementValidator() {
		if (agreementValidator == null) {
			try {
				agreementValidator = ServiceLocator.lookupService(IAgreementValidator.class);
			} catch (NamingException e) {
				throw new CommunicationException("Could not lookup Agreement Validator",e);
			}
		}
		return agreementValidator;
	}

	@Override
	public void validate(IValidatable validatable) {
		/**
		 * Validation is only called when a value is entered into the component,
		 * so this should always give the expected result
		 */
		String code = "" + validatable.getValue();
		if (code.equals("0")) {
			/**
			 * Ignore 0 codes - default value for agreement codes that have been nulled
			 */
			return;
		}
		try {
			/**
			 * Lookup existing code in AgreementCodes table
			 */
			AgreementObjectReference agreementObjectReference = getAgreementCodesValidation()
					.isAgreementCodeExists(agreementCodeType, code);
			if (agreementObjectReference==null) {
				throw new ObjectNotFoundChainedException("No Agreement Object Reference returned",0,0);
			}
			/**
			 * Validate
			 */
			if (existingAgreementId!=null && existingAgreementId.longValue() == agreementObjectReference
					.getObjectOid()) {
				/**
				 * Code found on current agreement number - this is valid
				 */
				return;
			}
			/**
			 * Code found on different agreement number - this is invalid report
			 * the error to the UI - uses an existing key in the resource bundle
			 * to report the duplicate
			 */
			validatable.error(new ValidationError().addKey("validator.duplicate"));
		} catch (ObjectNotFoundChainedException allowable) {
			/**
			 * No agreement found for code - this is valid
			 */
			handleObjectNotFoundException(validatable);
		}
	}

	/**
	 * When no object is found for the generic converter there is no additional
	 * validation action to perform. If additional actions are to be performed
	 * by subclasses when the ObjectNotFoundChainedException is thrown then
	 * this method must be overriden by sublasses
	 * @param validatable 
	 */
	protected void handleObjectNotFoundException(IValidatable validatable) {
		
	}
	
	/**
	 * Log debug output
	 * @return
	 */
	protected void debug(String message,Throwable exception) {
		if (getLogger().isDebugEnabled()) {
			getLogger().debug(message,exception);
		}
	}

	protected Logger getLogger() {
		if (logger==null) {
			logger = Logger.getLogger(AgreementCodeValidator.class);
		}
		return logger;
	}
	
	protected Long getExistingAgreementId() {
		return existingAgreementId;
	}

}
