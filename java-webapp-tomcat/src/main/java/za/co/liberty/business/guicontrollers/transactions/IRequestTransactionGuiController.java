package za.co.liberty.business.guicontrollers.transactions;

import java.io.Serializable;
import java.util.List;

import javax.ejb.Local;

import za.co.liberty.agreement.client.vo.RequestVO;
import za.co.liberty.dto.contracting.ResultAgreementDTO;
import za.co.liberty.dto.contracting.ResultPartyDTO;
import za.co.liberty.dto.pretransactionreject.RejectElementDTO;
import za.co.liberty.dto.spec.TypeDTO;
import za.co.liberty.dto.transaction.IPolicyTransactionModel;
import za.co.liberty.dto.transaction.RequestTransactionDTO;
import za.co.liberty.dto.userprofiles.ContextAgreementDTO;
import za.co.liberty.dto.userprofiles.ISessionUserProfile;
import za.co.liberty.exceptions.data.DataNotFoundException;
import za.co.liberty.exceptions.data.QueryTimeoutException;
import za.co.liberty.exceptions.data.ValidationException;
import za.co.liberty.exceptions.error.request.RequestException;
import za.co.liberty.interfaces.agreements.requests.EarningAndDeductionParentType;
import za.co.liberty.interfaces.agreements.requests.RequestKindType;

/**
 * Generic GUI controller for Request based transactions such as 
 * Settlements, VED's etc.
 * 
 * @author jzb0608 - Jun 1, 2017
 *
 */
@Local
public interface IRequestTransactionGuiController extends Serializable {

	/**
	 * Initialise the policy transaction model.
	 * 
	 * @param model
	 */
	public void initialisePageModel(IPolicyTransactionModel model);
	
	/**
	 * Return earning types for VED panel.   No type will list all types, with options 
	 * for the other subtypes
	 * 
	 * @param parentTYpe
	 * @return
	 */
	public List<TypeDTO> getVEDEarningTypesForType(EarningAndDeductionParentType parentType);
	
	/**
	 * Return a list of valid external payment types
	 * 
	 * @return
	 */
	public List<TypeDTO> getValidExternalPaymentTransactionTypes();
	

	/**
	 * Raise the request for the request object and request kind passed. 
	 * 
	 * @param userProfile
	 * @param requestKind
	 * @param object
	 * @return 
	 * @throws RequestException 
	 */
	public RequestVO raiseRequest(ISessionUserProfile userProfile, long agreementNr, 
			RequestKindType requestKind, RequestTransactionDTO object) throws RequestException;
	

	/**
	 * Do validation for the agreement selected and the request kind that needs to be raised.
	 * 
	 * 
	 * 
	 * @param contextAgreementDTO
	 * @param requestKind
	 * @throws ValidationException
	 * @throws DataNotFoundException 
	 */
	public void doAgreementValidation(ContextAgreementDTO contextAgreementDTO, 
			RequestKindType requestKind) throws ValidationException, DataNotFoundException;
	

	
	/**
	 * Validate policy transaction fields
	 * 
	 * @param iSessionUserProfile 
	 * @param agreementNr
	 * @param requestKind
	 * @param selectedItem
	 * @throws ValidationException
	 */
	public void doTransactionValidation(ISessionUserProfile iSessionUserProfile, long agreementNr, 
			RequestKindType requestKind,
			RequestTransactionDTO selectedItem)  throws ValidationException;
	

	/**
	 * Initialise a new DTO for the given request kind type
	 * 
	 * @param requestKind
	 * @return
	 */
	public RequestTransactionDTO initialiseDTO(RequestKindType requestKind);


	/**
	 * Find table data for the selected request kind / agreement
	 * 
	 * @param requestKind
	 * @param agreementNr
	 * @return
	 * @throws RequestException
	 * @throws QueryTimeoutException
	 */
	public List<Object> findTableData(
			RequestKindType requestKind, long agreementNr) throws RequestException, QueryTimeoutException;

	/**
	 * Convert a RejectElementDTO in the appropriate transaction DTO
	 *  
	 * @param rejectElementDTO
	 * @return
	 * @throws ValidationException 
	 */
	public RequestTransactionDTO convertRejectToTransaction(RejectElementDTO rejectElementDTO) throws ValidationException;

	
	/**
	 * Change the status of the passed Reject OID to canceled
	 * @param rejectOid
	 * @param value
	 * @throws ValidationException
	 */
	public void cancelReject(long rejectOid) throws ValidationException;

	/**
	 * Retrieve the agreement linked to the reject object.  Return null if no agreement is linked
	 * 
	 * @param rejectElementDTO
	 * @return
	 */
	public ResultAgreementDTO getLinkedAgreement(RejectElementDTO rejectElementDTO);

	/**
	 * Retrieve the party linked to the agreement found in the earlier step.  Return null
	 * if not found. 
	 * 
	 * @param resultAgreementDTO
	 * @return
	 */
	public ResultPartyDTO getLinkedParty(ResultAgreementDTO resultAgreementDTO);
	
}
