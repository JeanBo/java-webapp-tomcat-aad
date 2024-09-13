package za.co.liberty.business.guicontrollers;

import java.util.Collections;
import java.util.List;

import za.co.liberty.exceptions.data.QueryTimeoutException;
import za.co.liberty.exceptions.error.request.RequestException;
import za.co.liberty.interfaces.agreements.requests.RequestKindType;
import za.co.liberty.interfaces.agreements.requests.RequestStatusType;
import za.co.liberty.interfaces.persistence.agreement.request.IRequestEnquiryRow;

public class BasePageGuiController implements IBasePageGuiController {

	@Override
	public List<IRequestEnquiryRow> findRequestsForParty(long partyOID, RequestStatusType status,
			RequestKindType... requestKindTypes) throws RequestException, QueryTimeoutException {
		
		return Collections.EMPTY_LIST;
	}

	@Override
	public List<IRequestEnquiryRow> findRequestsForAgreement(long agreementID, RequestStatusType status,
			RequestKindType... requestKindTypes) throws RequestException, QueryTimeoutException {
		return Collections.EMPTY_LIST;
	}

	@Override
	public List<IRequestEnquiryRow> findRequestsForAgreement(List<Long> agreementIDs, RequestStatusType status,
			RequestKindType... requestKindTypes) throws RequestException, QueryTimeoutException {
		return Collections.EMPTY_LIST;
	}

	@Override
	public List<IRequestEnquiryRow> findRequestsForTemplate(RequestStatusType requires_authorisation,
			RequestKindType... requestKindTypes) throws RequestException, QueryTimeoutException {
		return Collections.EMPTY_LIST;
	}

}
