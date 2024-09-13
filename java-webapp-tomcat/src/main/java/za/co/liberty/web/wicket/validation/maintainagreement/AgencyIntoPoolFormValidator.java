package za.co.liberty.web.wicket.validation.maintainagreement;

import java.util.Date;

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.validation.IFormValidator;

import za.co.liberty.common.domain.Percentage;
import za.co.liberty.dto.agreement.AgencyPoolAccountDetailDTO;

/**
 * This class represents a form validator for Agency Pool Draw
 * 
 * 
 * 
 * @author SSM2707
 * 
 */
public class AgencyIntoPoolFormValidator implements IFormValidator {

	private static final long serialVersionUID = 5184933489869162251L;

	private AgencyPoolAccountDetailDTO poolDTO;

	public AgencyIntoPoolFormValidator(AgencyPoolAccountDetailDTO poolDTO) {
		super();
		this.poolDTO = poolDTO;
	}

	public FormComponent[] getDependentFormComponents() {
		return null;
	}

	public void validate(Form form) {
		Percentage overrideRate = poolDTO.getFutureOverrideIntoAgencyPoolRate();
		Date endDate = poolDTO.getFutureOverrideIntoAgencyPoolEndDate();
		Date startDate = poolDTO.getFutureOverrideIntoAgencyPoolStartDate();
		
		// Get the Pool Draw selection, if any
		if (overrideRate != null && endDate == null) {
			form.error("Please select the Future Override End Date.");
			
		} else if (overrideRate == null && endDate != null) {
			form.error("Please select the Future Into Pool Rate Override.");
		}
		
		if (startDate == null) {
			form.error("Please ensure that the Future Override Start Date has a valid value.");
		}

	}

}
