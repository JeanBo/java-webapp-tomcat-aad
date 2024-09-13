package za.co.liberty.business.party.validator;

import java.util.Date;
import java.util.List;

import za.co.liberty.dto.contracting.ResultPartyDTO;
import za.co.liberty.dto.databaseenum.CostCenterDBEnumDTO;
import za.co.liberty.dto.party.EmployeeDTO;
import za.co.liberty.dto.party.HierarchyNodeDTO;
import za.co.liberty.dto.party.PartyDTO;
import za.co.liberty.dto.party.PartyRoleDTO;
import za.co.liberty.dto.party.PersonDTO;
import za.co.liberty.dto.party.contactdetail.AddressDTO;
import za.co.liberty.dto.party.contactdetail.ContactPreferenceDTO;
import za.co.liberty.dto.party.medicalaid.MedicalAidDetailDTO;
import za.co.liberty.dto.party.taxdetails.BBBEEDTO;
import za.co.liberty.dto.party.taxdetails.DirectivesDTO;
import za.co.liberty.dto.party.taxdetails.LabourBrokersDTO;
import za.co.liberty.dto.party.taxdetails.TaxDetailsDTO;
import za.co.liberty.dto.party.taxdetails.TrustCompDTO;
import za.co.liberty.dto.party.taxdetails.VatDTO;
import za.co.liberty.dto.userprofiles.ContextDTO;
import za.co.liberty.exceptions.data.ValidationException;
import za.co.liberty.interfaces.agreements.AgreementKindType;

public class PartyValidator implements IPartyValidator {

	@Override
	public void validate(PartyDTO arg0, PartyDTO arg1, boolean arg2, boolean arg3, AgreementKindType arg4)
			throws ValidationException {
		// TODO Auto-generated method stub

	}

	@Override
	public void validateAddress(AddressDTO arg0) throws ValidationException {
		// TODO Auto-generated method stub

	}

	@Override
	public void validateBBBEEDetail(List<BBBEEDTO> arg0) throws ValidationException {
		// TODO Auto-generated method stub

	}

	@Override
	public void validateBusinessOwner(ResultPartyDTO arg0, ContextDTO arg1) throws ValidationException {
		// TODO Auto-generated method stub

	}

	@Override
	public void validateContactPreferenceDetailsAndAllContactDetails(Long arg0, List<ContactPreferenceDTO> arg1,
			boolean arg2) throws ValidationException {
		// TODO Auto-generated method stub

	}

	@Override
	public void validateContactPreferenceDetailsAndAllContactDetails(Long arg0, List<ContactPreferenceDTO> arg1,
			boolean arg2, boolean arg3) throws ValidationException {
		// TODO Auto-generated method stub

	}

	@Override
	public void validateCostCentreFormat(CostCenterDBEnumDTO arg0) throws ValidationException {
		// TODO Auto-generated method stub

	}

	@Override
	public void validateDirectivesDetail(long arg0, Date arg1, List<DirectivesDTO> arg2) throws ValidationException {
		// TODO Auto-generated method stub

	}

	@Override
	public void validateExternalReference(HierarchyNodeDTO arg0) throws ValidationException {
		// TODO Auto-generated method stub

	}

	@Override
	public void validateIDNumber(PersonDTO arg0) throws ValidationException {
		// TODO Auto-generated method stub

	}

	@Override
	public void validateLabourBrokerDetail(List<LabourBrokersDTO> arg0) throws ValidationException {
		// TODO Auto-generated method stub

	}

	@Override
	public void validateMedicalAidDetail(MedicalAidDetailDTO arg0) throws ValidationException {
		// TODO Auto-generated method stub

	}

	@Override
	public void validatePartyAgeBetween16And150(Date arg0, boolean arg1) throws ValidationException {
		// TODO Auto-generated method stub

	}

	@Override
	public void validatePartyRoles(List<PartyRoleDTO> arg0) throws ValidationException {
		// TODO Auto-generated method stub

	}

	@Override
	public void validateReactivateRequirements(PartyDTO arg0) throws ValidationException {
		// TODO Auto-generated method stub

	}

	@Override
	public void validateStanlibUACFID(String arg0, long arg1) throws ValidationException {
		// TODO Auto-generated method stub

	}

	@Override
	public void validateTaxDetail(TaxDetailsDTO arg0, Long arg1) throws ValidationException {
		// TODO Auto-generated method stub

	}

	@Override
	public void validateTerminationRequirements(PartyDTO arg0) throws ValidationException {
		// TODO Auto-generated method stub

	}

	@Override
	public void validateTrustCompDetail(List<TrustCompDTO> arg0) throws ValidationException {
		// TODO Auto-generated method stub

	}

	@Override
	public void validateUACFIDUniqueAndExistsOnTam(long arg0, String arg1) throws ValidationException {
		// TODO Auto-generated method stub

	}

	@Override
	public void validateVatDetail(List<VatDTO> arg0) throws ValidationException {
		// TODO Auto-generated method stub

	}

	@Override
	public void validateWealthConnectionRole(EmployeeDTO arg0, PartyRoleDTO arg1, boolean arg2)
			throws ValidationException {
		// TODO Auto-generated method stub

	}

	@Override
	public void validateUPN(PersonDTO arg0) throws ValidationException {
		// TODO Auto-generated method stub
		
	}

}
