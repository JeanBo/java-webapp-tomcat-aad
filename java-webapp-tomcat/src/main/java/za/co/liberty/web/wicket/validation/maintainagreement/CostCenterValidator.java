package za.co.liberty.web.wicket.validation.maintainagreement;

import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.ValidationError;

import za.co.liberty.business.guicontrollers.agreement.IAgreementGUIController;
import za.co.liberty.dto.agreement.properties.CostCenterDTO;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.helpers.lookup.ServiceLocator;

public class CostCenterValidator implements IValidator {
	
	private static final long serialVersionUID = 3901862672438224156L;
	
	private transient IAgreementGUIController guiController;
	
	private transient Logger logger;
	
	public CostCenterValidator() {
	}
	
	public IAgreementGUIController getGuiController() {
		if (guiController==null) {
			try {
				guiController = ServiceLocator.lookupService(IAgreementGUIController.class);
			} catch (NamingException e) {
				getLogger().fatal("Could not lookup IAgreementGUIController",e);
				throw new CommunicationException("Could not lookup IAgreementGUIController",e);
			}
		}
		return guiController;
	}
	
	public Logger getLogger() {
		if (logger == null) {
			 logger = Logger.getLogger(CostCenterValidator.class);
		}
		return logger;
	}

	@Override
	public void validate(IValidatable validatable) {
		if (validatable==null || validatable.getValue()==null) {
			return;
		}
		String value = validatable.getValue().toString();
		if (value.length()==0) {
			return;
		}
		int maxLength = 10;
		if (value.length()>maxLength) {
//			Map<String,String> map = new HashMap<String,String>();
//			map.put("validator.type", "CostCenter");
			validatable.error(new ValidationError()
					.addKey("validator.invalid")
					.setVariable("validator.type", "CostCenter"));
			return;
		}
		CostCenterDTO costCenter = getGuiController().getCostCenterForValue(value);
		if (costCenter==null) {
			validatable.error(new ValidationError()
					.addKey("validator.invalid")
					.setVariable("validator.type", "CostCenter"));
			return;
		}
	}

}
