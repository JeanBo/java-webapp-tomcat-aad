package za.co.liberty.business.guicontrollers.contactdetail;

import java.util.Collections;
import java.util.List;

import za.co.liberty.dto.party.PartyDTO;
import za.co.liberty.dto.party.contactdetail.AddressDTO;
import za.co.liberty.dto.party.contactdetail.search.AddressSearchType;
import za.co.liberty.dto.party.contactdetail.search.SuburbSearchDTO;
import za.co.liberty.exceptions.data.ValidationException;
import za.co.liberty.interfaces.rating.SuburbSearchType;

public class ContactDetailsGUIController implements IContactDetailsGUIController {

	@Override
	public List<String> findCity(String arg0) {
		return Collections.EMPTY_LIST;
	}

	@Override
	public List<SuburbSearchDTO> findSuburb(SuburbSearchType arg0, String arg1) {
		return Collections.EMPTY_LIST;
	}

	@Override
	public List<SuburbSearchDTO> findSuburb(SuburbSearchType arg0, String arg1, AddressSearchType arg2) {
		return Collections.EMPTY_LIST;
	}

	@Override
	public void validateAddressDetail(AddressDTO arg0) throws ValidationException {
		// TODO Auto-generated method stub

	}

	@Override
	public void validateContactDetail(PartyDTO arg0, boolean arg1) throws ValidationException {
		// TODO Auto-generated method stub

	}

	@Override
	public void validateContactDetail(PartyDTO arg0, boolean arg1, boolean arg2) throws ValidationException {
		// TODO Auto-generated method stub

	}

}
