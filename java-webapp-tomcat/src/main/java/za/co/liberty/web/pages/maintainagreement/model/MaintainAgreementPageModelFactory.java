package za.co.liberty.web.pages.maintainagreement.model;

import za.co.liberty.business.guicontrollers.agreement.IAgreementGUIController;
import za.co.liberty.dto.agreement.AgreementDTO;
import za.co.liberty.dto.party.PartyDTO;
import za.co.liberty.dto.userprofiles.ContextPartyDTO;
import za.co.liberty.exceptions.SystemException;
import za.co.liberty.exceptions.data.DataNotFoundException;
import za.co.liberty.exceptions.data.ValidationException;
import za.co.liberty.interfaces.agreements.AgreementKindType;
import za.co.liberty.web.pages.maintainagreement.model.MaintainAgreementPageModel.PartyTypeSelection;

public class MaintainAgreementPageModelFactory {
	
	/**
	 * Create a new page model specifically for the maintain agreement process
	 * @param agreementDTO
	 * @param validAgreementValuesDTO
	 * @return
	 */
	public static MaintainAgreementPageModel createPageModelForMaintenance(
			IAgreementGUIController guiController,
			AgreementDTO agreementDTO) {
		if (guiController==null || agreementDTO==null) {
			throw new IllegalArgumentException("guiController and agreementDTO must not be null");
		}
		MaintainAgreementPageModel ret = new MaintainAgreementPageModel(
				agreementDTO,
				guiController.getValidAgreementValues(agreementDTO));
		if (agreementDTO==null || agreementDTO.getId()==0) {
			ret.setSelectedItem(null);
		} else {
			ret.setSelectedItem(ret.getMaintainAgreementDTO());
		}
		return ret;
	}
	
	public static MaintainAgreementPageModel createPageModelForTerminate(
			IAgreementGUIController guiController) {
		/**
		 * init agreement model
		 */
		MaintainAgreementPageModel ret = new MaintainAgreementPageModel(
				null,
				null);
		return ret;
	}
	
	/**
	 * Create a new page model specifically for the create new agreement process
	 * @param guiController
	 * @param partyContext 
	 * @return
	 */
	public static MaintainAgreementPageModel createPageModelForCreate(
			IAgreementGUIController guiController, 
			AgreementKindType agreementKind,
			ContextPartyDTO partyContext) {
		/**
		 * init agreement model
		 */
		AgreementDTO agreementDTO = guiController.createNewAgreementDTO(agreementKind);
		MaintainAgreementPageModel ret = new MaintainAgreementPageModel(
				agreementDTO,
				guiController.getValidAgreementValues(agreementDTO));
		ret.setAgreementKindChangeEnabled(true);
		ret.setPartyTypeSelection(PartyTypeSelection.CURRENT_PARTY);
		updateModelWithExistingParty(guiController, partyContext, ret);
		return ret;
	}
	
	/**
	 * Update an existing model with a new agreementDTO and valid agreement values
	 * @param guiController
	 * @param agreementKind
	 * @param pageModel
	 */
	public static void updatePageModelWithNewAgreementKind(
			IAgreementGUIController guiController, 
			AgreementKindType agreementKind,
			MaintainAgreementPageModel pageModel) {
		AgreementDTO agreementDTO = guiController.createNewAgreementDTO(agreementKind);
		pageModel.updateModel(agreementDTO, 
				guiController.getValidAgreementValues(agreementDTO));
	}

	/**
	 * Update an existing model with an existing party
	 * @param guiController
	 * @param partyContext
	 * @param pageModel
	 */
	public static void updateModelWithExistingParty(
			IAgreementGUIController guiController, 
			ContextPartyDTO partyContext, 
			MaintainAgreementPageModel pageModel) {
		/**
		 * init the existing party data
		 */
		if (partyContext!=null) {
			try {
				PartyDTO party = guiController.getPartyDTO(partyContext.getPartyOid());
				// SSM2707 Added for Hierarchy FR3.5 Primary Agreement SWETA MENON
				party.setPrimaryAgreementMap(guiController
						.getPrimaryAgreementMap(partyContext.getPartyOid()));
				pageModel.setExistingPartyDetails(party);
				pageModel.getMaintainAgreementDTO().getAgreementDTO().setPartyOid(party.getId());
			} catch (DataNotFoundException e) {
				SystemException sys = new SystemException();
				sys.initCause(e);
				throw sys;
			} catch (ValidationException e) {
				SystemException sys = new SystemException();
				sys.initCause(e);
				throw sys;
			}
		}
	}

}
