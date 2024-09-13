package za.co.liberty.web.wicket.validation.maintainagreement;

import java.util.List;

import javax.naming.NamingException;

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.validation.AbstractFormValidator;
import org.apache.wicket.markup.html.form.validation.IFormValidator;

import za.co.liberty.business.guicontrollers.agreement.IAgreementGUIController;
import za.co.liberty.dto.party.OrganisationDTO;
import za.co.liberty.dto.party.PartyDTO;
import za.co.liberty.dto.party.PersonDTO;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.helpers.lookup.ServiceLocator;
import za.co.liberty.web.data.enums.EditStateType;

/**
 * This class represents validation that can be added to a party panel to check if any agreements
 * associated to a specified party have an active status reason of Fraud, to ensure compliance
 * @author kxd1203
 *
 */
public class StatusReasonFraudFormValidator implements IFormValidator {

	private static final long serialVersionUID = -5602519849402377031L;
	
	private EditStateType editState;
	private PartyDTO party;
	private transient IAgreementGUIController agreementGUIController;
	
	public StatusReasonFraudFormValidator(
			EditStateType editState,
			PartyDTO party) {
		super();
		this.editState=editState;
		this.party=party;
	}
	
	public void setParty(PartyDTO party) {
		this.party = party;
	}
	
	public FormComponent[] getDependentFormComponents() {
		return null;
	}

	public void validate(Form form) {
		if (editState==EditStateType.VIEW) {
			return;
		}
		/**
		 * Check for status reason fraud
		 */
		if (party!=null) {
			String idNumber = null;
			if (party instanceof PersonDTO) {
				idNumber = ((PersonDTO)party).getIdentificationNumber();
			} else if (party instanceof OrganisationDTO) {
				idNumber = ((OrganisationDTO)party).getRegistrationNumber();
			}
			long typeOid = party.getTypeOID();
			List<Long> agreementsWithStatusReasonFraud = getGUIController()
				.getSRSAgreementNumbersWithStatusReasonFraudForIdentificationNumber(
					typeOid, idNumber);
			if (agreementsWithStatusReasonFraud!=null
					&& agreementsWithStatusReasonFraud.size()>0) {
				form.error("An agreement can not be added for this party, due to a " +
						"related agreement having status reason fraud");
				return;
			}
		}
	}

	private IAgreementGUIController getGUIController() {
		if (agreementGUIController == null) {
			try {
				agreementGUIController = 
					ServiceLocator.lookupService(
							IAgreementGUIController.class);
			} catch (NamingException e) {
				throw new CommunicationException(
						"Could not load agreement GUI controller",e);
			}
		}
		return agreementGUIController;
	}

}
