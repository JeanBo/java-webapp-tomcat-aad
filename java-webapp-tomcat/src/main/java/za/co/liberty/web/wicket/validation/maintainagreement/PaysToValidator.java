package za.co.liberty.web.wicket.validation.maintainagreement;

import javax.naming.NamingException;

import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.ValidationError;

import za.co.liberty.business.guicontrollers.agreement.IAgreementGUIController;
import za.co.liberty.dto.agreement.properties.PaysToDTO;
import za.co.liberty.exceptions.data.ValidationException;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.helpers.lookup.ServiceLocator;

/**
 * This class represents the validation on the input for the Pays To details on
 * the maintain agreement page.
 * 
 * @author kxd1203
 * 
 */
public class PaysToValidator implements IValidator {

	private Long existingAgreementId;

	transient IAgreementGUIController agreementGUIController;

	/**
	 * Create a new validator against the specified context
	 * 
	 * @param context
	 *            The {@link PaysToDTO} representing the payment details
	 * @param existingAgreementId
	 */
	public PaysToValidator(Long existingAgreementId) {
		this.existingAgreementId = existingAgreementId;
	}

	synchronized IAgreementGUIController getAgreementGUIController() {
		if (agreementGUIController == null) {
			try {
				agreementGUIController = ServiceLocator
						.lookupService(IAgreementGUIController.class);
			} catch (NamingException e) {
				throw new CommunicationException(
						"Could not lookup AgreementGUIController", e);
			}
		}
		return agreementGUIController;
	}

	/**
	 * Validates the Pays To input against the following criteria:
	 * 
	 * 1 - The PaysToType selected is not null 2 - If the PaysToType is
	 * ORGANISATION, then the context MUST have a non-null and non-zero
	 * agreement number.
	 * 
	 */
	@Override
	public void validate(IValidatable validatable) {
		if (validatable == null || validatable.getValue() == null) {
			return;
		}
		if (!(validatable.getValue() instanceof PaysToDTO)) {
			return;
		}
		PaysToDTO paysTo = (PaysToDTO) validatable.getValue();
		try {
			validatePaysToDTO(paysTo);
		} catch (ValidationException ex) {
			validatable.error(new ValidationError().setMessage(ex.getErrorMessages().get(0)));
		}
	}

	/**
	 * Convenience method used to validate the PaysToDTO object representing the
	 * pays to options selected. This can be used externally within the same
	 * package by other validators requiring validation on this field
	 * 
	 * @param paysTo
	 * @throws ValidationException
	 */
	public void validatePaysToDTO(PaysToDTO paysTo) throws ValidationException {
		getAgreementGUIController().validatePaysToDTO(existingAgreementId, paysTo);
	}

}
