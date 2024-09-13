package za.co.liberty.web.wicket.validation.maintainagreement;

import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.validation.IFormValidator;

import za.co.liberty.business.guicontrollers.agreement.IAgreementGUIController;
import za.co.liberty.dto.party.PartyDTO;
import za.co.liberty.exceptions.data.DataNotFoundException;
import za.co.liberty.exceptions.data.ValidationException;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.helpers.lookup.ServiceLocator;
import za.co.liberty.web.data.enums.EditStateType;

/**
 * This class represents validation that can be added to a party panel to check if any agreements
 * associated to a specified party have an active status reason of Fraud, to ensure compliance
 * @author kxd1203
 *
 */
public class StatusReasonDebarmentFormValidator implements IFormValidator {

	private static final long serialVersionUID = -5602519849402377031L;
	
	private EditStateType editState;
	private PartyDTO party;
	private transient IAgreementGUIController agreementGUIController;
	
	public StatusReasonDebarmentFormValidator(
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
		if(!(party.getOid() <= 0)) 	{
				try {
					
					 getGUIController().validateAllAgreementForDebarment( party.getOid());
				} catch (CommunicationException e) {
					form.error(e.getMessage());
				} 
				catch (ValidationException e) {
					Logger.getLogger(this.getClass()).warn("Debarment validation failed",e);
					for (String msg : e.getErrorMessages()) {
						form.error(msg);
					}
				}
				catch (DataNotFoundException e) {
					form.error(e.getMessage());
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
