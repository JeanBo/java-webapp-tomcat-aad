package za.co.liberty.web.wicket.validation.maintainagreement;

import java.util.Date;

import org.apache.wicket.Component;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.ValidationError;

import za.co.liberty.dto.spec.ActualLifeCycleStatusDTO;

/**
 * This class represents the validator for agreement end date.
 * 
 * Rules:
 * 1 - Agreement end date cannot be before agreement start date
 * 2 - Agreement end date must be populated when status is set to terminated
 * 
 * @author kxd1203
 *
 */
public class AgreementEndDateValidator implements IValidator {

	private Component startDateComponent;
	private Component currentStatusComponent;


	public AgreementEndDateValidator(Component startDateComponent,Component currentStatusComponent) {
		this.startDateComponent=startDateComponent;
		this.currentStatusComponent=currentStatusComponent;
	}
	
	
	@Override
	public void validate(IValidatable validatable) {
		Date endDate = 
			(Date)validatable.getValue();
		Date startDate = 
			(Date)startDateComponent.getDefaultModelObject();
		ActualLifeCycleStatusDTO currentStatus = 
			(ActualLifeCycleStatusDTO)currentStatusComponent.getDefaultModelObject();
		if (startDate!=null) {
			if (endDate==null || endDate.before(startDate)) {
				validatable.error(new ValidationError().addKey("AgreementEndDateValidator.beforeStart"));
				return;
			}
		}
	}

}
