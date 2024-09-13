package za.co.liberty.web.wicket.validation.maintainagreement;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.validation.IFormValidator;

import za.co.liberty.dto.agreement.AgencyPoolAccountDetailDTO;

/**
 * This class represents a form validator for Agency Pool Draw
 * 
 * 
 * 
 * @author SSM2707
 * 
 */
public class StopAgencyPoolTransferFormValidator implements IFormValidator {

	private static final long serialVersionUID = 5184933489869162251L;

	private AgencyPoolAccountDetailDTO poolDTO;
	private Boolean oldStopInd;

	public StopAgencyPoolTransferFormValidator(AgencyPoolAccountDetailDTO poolDTO, Boolean oldStopInd) {
		super();
		this.poolDTO = poolDTO;
		this.oldStopInd = oldStopInd;
	}

	public FormComponent[] getDependentFormComponents() {
		return null;
	}

	public void validate(Form form) {
		Boolean currStopInd = poolDTO.getStopIntoPoolTransfers();
		String comment;
		// Get the Pool Draw selection, if any
		if (oldStopInd == null && currStopInd != null) {
			comment = poolDTO.getStopIntoPoolTransferComment();
			if (comment == null || comment.trim().length() == 0) {
				form.error("Please enter Comment for Stop Into Pool Transfers.");
			}
		} else if (oldStopInd != null && currStopInd != null
				&& (!oldStopInd.equals(currStopInd))) {
			comment = poolDTO.getStopIntoPoolTransferComment();
			if (comment == null || comment.trim().length() == 0) {
				form.error("Please enter Comment for Stop Into Pool Transfers.");
			}
		}

	}
	
	public BigDecimal percentageValue(BigDecimal base, BigDecimal pct){
	    return base.multiply(pct).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);
	}

}
