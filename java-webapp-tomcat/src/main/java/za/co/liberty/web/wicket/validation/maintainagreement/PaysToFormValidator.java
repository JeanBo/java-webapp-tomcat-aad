package za.co.liberty.web.wicket.validation.maintainagreement;

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.validation.AbstractFormValidator;
import org.apache.wicket.markup.html.form.validation.IFormValidator;

import za.co.liberty.dto.agreement.AgreementDTO;
import za.co.liberty.dto.agreement.properties.PaysToDTO;
import za.co.liberty.exceptions.data.ValidationException;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.pages.maintainagreement.model.MaintainAgreementPageModel;

public class PaysToFormValidator implements IFormValidator {

	private static final long serialVersionUID = 6888657645247696068L;

	private FormComponent paysToFormComponent;
	
	private PaysToValidator paysToValidator;

	private EditStateType editState;

	private AgreementDTO paysToContainer;
	
	public PaysToFormValidator(
			EditStateType editState,
			Long agreementId,
			AgreementDTO paysToContainer,
			FormComponent paysToFormComponent) {
		super();
		this.editState=editState;
		this.paysToFormComponent=paysToFormComponent;
		this.paysToContainer=paysToContainer;
		paysToValidator = new PaysToValidator(agreementId);
	}

	public FormComponent[] getDependentFormComponents() {
		return null;
	}

	public void validate(Form arg0) {
		if (editState==EditStateType.VIEW) {
			return;
		}
		/**
		 * Validate Pays To On Form Submit, rather than in the ajax behaviour
		 */
		try {
			paysToValidator.validatePaysToDTO(paysToContainer.getPaymentDetails());
		} catch (ValidationException e) {
			if (paysToFormComponent!=null) {
				arg0.error(e.getErrorMessages().get(0));
			}
		}
	}

}
