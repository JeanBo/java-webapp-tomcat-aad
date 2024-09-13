 package za.co.liberty.web.wicket.validation.maintainagreement.associatedagmt;

import java.util.HashMap;
import java.util.Map;

import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.ValidationError;

import za.co.liberty.business.guicontrollers.agreement.IAgreementGUIController;
import za.co.liberty.common.domain.Percentage;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.helpers.lookup.ServiceLocator;
/**
 * This class represents the Associated Percentage Validator.
 * All validations for Associated Percentage on adding of Associated Agreements is listed here
 * @author pks2802
 * @date 05/10/2009
 *
 */
public class AssociatedPercentageValidator implements IValidator {
	
	private static final long serialVersionUID = 3901862672438224156L;
	
	private transient IAgreementGUIController guiController;
	
	private transient Logger logger = Logger.getLogger(AssociatedPercentageValidator.class);
	
	
		
	public AssociatedPercentageValidator() {
				
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
		Map<String,String> map = new HashMap<String,String>();
		
		if (validatable==null || validatable.getValue()==null) {
			return;
		}
		
		Object obj = validatable.getValue();
		if(!(obj instanceof Percentage))
			return;
		
		Percentage assPercent = (Percentage)obj;
				
		if(assPercent.isGreaterThan(Percentage.ONE_HUNDRED_PERCENT)){
			validatable.error(new ValidationError().addKey("associatedagmt.validator.percentmorethan100"));
			return;
		}
		
		if(assPercent.isLessThanOrEqualTo(Percentage.ZERO_PERCENT)){
			validatable.error(new ValidationError().addKey("associatedagmt.validator.percentlessthan0"));
			return;
		}		
	}
}
