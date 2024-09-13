package za.co.liberty.web.wicket.validation.maintainagreement;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.ValidationError;

import za.co.liberty.dto.party.EmployeeDTO;
import za.co.liberty.dto.party.PartyDTO;

/**
 * This class represents a validator that will validate agreement start
 * date to ensure that if an agreement is being added against an employee
 * that has a job title start date captured that the agreement start date
 * falls on or before the job title start date.
 *  
 * @author kxd1203
 *
 */
public class AgreementStartDateJobTitleValidator implements IValidator {

	private static final long serialVersionUID = 8223517094744128782L;
	
	private PartyDTO partyDTO;

	public AgreementStartDateJobTitleValidator(PartyDTO partyDTO) {
		this.partyDTO=partyDTO;
	}
	
	public void setPartyDTO(PartyDTO partyDTO) {
		this.partyDTO = partyDTO;
	}

	@Override
	public void validate(IValidatable validatable) {
		
		if (partyDTO==null || !(partyDTO instanceof EmployeeDTO)) {
			/**
			 * Ignore null instances of party and instances which are 
			 * not employees 
			 */
			return;
		}
		EmployeeDTO employeeDTO = (EmployeeDTO)partyDTO;
		Date agreementStart = (Date)validatable.getValue();
		if (agreementStart==null) {
			/**
			 * Agreement start date is required
			 */
			validatable.error(new ValidationError().addKey("Required"));
			return;
		}
		if (employeeDTO.getJobStartDate()==null) {
			/**
			 * Validation only occurs when a job title start date is captured
			 */
			return;
		}
		if (employeeDTO.getJobStartDate().before(agreementStart)) {
			/**
			 * Agreement Start must fall on or before the job title start date
			 */
			validatable.error(new ValidationError().addKey(
					"AgreementStartDateJobTitleValidator.afterJobTitleStart").setVariable("jobTitleStartDate", getJobTitleStart()));
			return;
		}
	}
	
	/**
	 * Return the jobtitle start
	 * @return
	 */
	protected String getJobTitleStart() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		String jobTitleStart = "";
		if (partyDTO!=null && 
				partyDTO instanceof EmployeeDTO && 
				((EmployeeDTO)partyDTO).getJobStartDate()!=null) {
			/**
			 * add formatted job title start date
			 */
			jobTitleStart = dateFormat.format(
					((EmployeeDTO)partyDTO).getJobStartDate());
		} 
//		ret.put("jobTitleStartDate", jobTitleStart);
		return jobTitleStart;
	}
	
	

}
