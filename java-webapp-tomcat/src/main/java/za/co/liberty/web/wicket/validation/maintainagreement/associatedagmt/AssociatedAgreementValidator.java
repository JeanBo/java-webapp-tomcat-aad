package za.co.liberty.web.wicket.validation.maintainagreement.associatedagmt;

import java.util.HashMap;
import java.util.Map;

import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.ValidationError;

import za.co.liberty.business.guicontrollers.agreement.IAgreementGUIController;
import za.co.liberty.dto.agreement.AgreementDTO;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.helpers.lookup.ServiceLocator;
/**
 * This class represents the Associated Agreements Validator.
 * All validations for Associated Agreements on adding of Associated Agreements is listed here
 * @author pks2802
 * @date 05/10/2009
 *
 */
public class AssociatedAgreementValidator implements IValidator {

	private static final long serialVersionUID = 3901862672438224156L;

	private transient IAgreementGUIController guiController;

	private AgreementDTO  agreementDTO;
	
	private transient Logger logger = Logger.getLogger(AssociatedAgreementValidator.class);

	public AssociatedAgreementValidator() {
	}

	public AssociatedAgreementValidator(AgreementDTO dto) {
		this.agreementDTO = dto;
	}

	public IAgreementGUIController getGuiController() {
		if (guiController==null) {
			try {
				guiController = ServiceLocator.lookupService(IAgreementGUIController.class);
			} catch (NamingException e) {
				logger.fatal("Could not lookup IAgreementGUIController",e);
				throw new CommunicationException("Could not lookup IAgreementGUIController",e);
			}
		}
		return guiController;
	}

	@Override
	public void validate(IValidatable validatable) {
				
		long contextAgmt = 0l;
		Map<String,String> map = new HashMap<String,String>();
		
		if (validatable==null || validatable.getValue()==null) {
			return;
		}
		long agreementValue = (Long)validatable.getValue();
		
		if(agreementDTO != null )
		{
			contextAgmt = agreementDTO.getId();
		}

		if(contextAgmt == agreementValue){
			validatable.error(new ValidationError().addKey("associatedagmt.validator.sameagmt"));
			return;
		}

		if(!getGuiController().doesAgreementExist(agreementValue)){
			validatable.error(new ValidationError().addKey("associatedagmt.validator.notexist")
					.setVariable("input0", String.valueOf(agreementValue)));

			return;
		}				
	}	
}



