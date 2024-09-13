package za.co.liberty.business.guicontrollers;

import java.util.List;

import javax.ejb.Local;

import za.co.liberty.exceptions.data.QueryTimeoutException;
import za.co.liberty.exceptions.error.request.RequestException;
import za.co.liberty.interfaces.agreements.requests.RequestKindType;
import za.co.liberty.interfaces.agreements.requests.RequestStatusType;
import za.co.liberty.interfaces.persistence.agreement.request.IRequestEnquiryRow;

/**
 * The interface for the Gui Controller for the BasePage
 * 
 * @author JZB0608 - 04 Mar 2010
 *
 */
@Local
public interface IBasePageGuiController {
	
	/**
	 * Get the requests for a party
	 * @param partyOID
	 * @param requestKindTypes
	 * @return
	 * @throws RequestException
	 * @throws QueryTimeoutException
	 */
	public List<IRequestEnquiryRow> findRequestsForParty(long partyOID, RequestStatusType status, 
			RequestKindType ... requestKindTypes)
		throws RequestException, QueryTimeoutException;
	
	/**
	 * Get the requests for an agreement
	 * @param agreementIDs
	 * @param status
	 * @param requestKindTypes
	 * @return
	 * @throws RequestException
	 * @throws QueryTimeoutException
	 */
	public List<IRequestEnquiryRow> findRequestsForAgreement(long agreementID,RequestStatusType status, 
			RequestKindType ... requestKindTypes) 
		throws RequestException, QueryTimeoutException;
	
	/**
	 * Get the requests for a list of agreements
	 * 
	 * @param agreementIds
	 * @param status
	 * @param requestKindTypes
	 * @return
	 * @throws RequestException
	 * @throws QueryTimeoutException
	 */
	public List<IRequestEnquiryRow> findRequestsForAgreement(List<Long> agreementIDs, 
			RequestStatusType status, 
			RequestKindType ... requestKindTypes) 
		throws RequestException, QueryTimeoutException;

	public List<IRequestEnquiryRow> findRequestsForTemplate(RequestStatusType requires_authorisation, RequestKindType ... requestKindTypes) 	throws RequestException, QueryTimeoutException;
	
}
