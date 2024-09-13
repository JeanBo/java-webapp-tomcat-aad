package za.co.liberty.business.guicontrollers.hierarchy;

import java.util.Collection;
import java.util.List;

import javax.ejb.Local;

import za.co.liberty.agreement.common.exceptions.LogicExecutionException;
import za.co.liberty.common.domain.ObjectReference;
import za.co.liberty.dto.agreement.AgreementRoleDTO;
import za.co.liberty.dto.agreement.SimpleAgreementDetailDTO;
import za.co.liberty.dto.agreement.maintainagreement.BulkAgreementTransferDTO;
import za.co.liberty.dto.agreement.request.RequestEnquiryResultDTO;
import za.co.liberty.dto.common.IDValueDTO;
import za.co.liberty.dto.contracting.ResultPartyDTO;
import za.co.liberty.dto.databaseenum.BranchCategoryEnumDTO;
import za.co.liberty.dto.databaseenum.CostCenterDBEnumDTO;
import za.co.liberty.dto.databaseenum.OrganisationExternalTypeEnumDTO;
import za.co.liberty.dto.hierarchy.MiHierarchyNodeAddressClassificationDTO;
import za.co.liberty.dto.party.HierarchyEmployeeLinkDTO;
import za.co.liberty.dto.party.HierarchyNodeDTO;
import za.co.liberty.dto.party.HierarchyNodeLinkDTO;
import za.co.liberty.dto.userprofiles.ISessionUserProfile;
import za.co.liberty.exceptions.SystemException;
import za.co.liberty.exceptions.data.DataNotFoundException;
import za.co.liberty.exceptions.data.QueryTimeoutException;
import za.co.liberty.exceptions.data.ValidationException;
import za.co.liberty.exceptions.error.request.RequestException;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.interfaces.agreements.requests.RequestKindType;
import za.co.liberty.interfaces.party.IPartyNameAndIdFLO;
import za.co.liberty.interfaces.persistence.agreement.request.IRequestEnquiryRow;
import za.co.liberty.party.exceptions.InvalidPartyRegException;
import za.co.liberty.persistence.party.entity.fastlane.PartyProfileFLO;
import za.co.liberty.persistence.rating.entity.MiHierarchyNodeAddressClassificationEntity;
import za.co.liberty.persistence.rating.entity.MiHierarchyNodeCharacteristicsEntity;

@Local
public interface IHierarchyGUIController {

	/**
	 * Find nodes of a type where their parent is linked to the channel
	 * 
	 * @param type
	 * @param channel
	 * @param includeFutureEndedNodes
	 *            If true will return all nodes even if they are ending in the
	 *            future
	 * @return
	 * @throws DataNotFoundException
	 */
	public Collection<ResultPartyDTO> getHeirarchyNodesByTypeAndChannel(
			long type, long channel, boolean includeFutureEndedNodes)
			throws DataNotFoundException;

	/**
	 * Return a HierarchyNodeDTO using the given partyid
	 * 
	 * @param partyID
	 * @return
	 * @throws DataNotFoundException
	 */
	public HierarchyNodeDTO getHierarchyNodeDTO(long partyID)
			throws DataNotFoundException;

	/**
	 * Gets all the hierarchy type ie Division, Branch etc
	 * 
	 * @return
	 * @throws DataNotFoundException
	 */
	public List<IDValueDTO> getHierarchyTypeList() throws DataNotFoundException;

	/**
	 * Returns a list of ResultPartyDTO representing the selectable channels
	 * 
	 * @return
	 * @throws DataNotFoundException
	 */
	public List<IPartyNameAndIdFLO> getHierarchyChannelList()
			throws DataNotFoundException;

	/**
	 * Returns a list of employee role types, this wil lbe used for selection on
	 * the gui
	 * 
	 * @return
	 */
	public List<IDValueDTO> getEmployeeTypeList();

	/**
	 * Search for users starting with the given uacfid
	 * 
	 * @return List a list of Fast Lane User Profiles
	 */
	public List<PartyProfileFLO> findUserWithUACFID(String uacfid);

	/**
	 * Find a party with object oid
	 * 
	 * @param objectOid
	 * @return
	 * @throws DataNotFoundException
	 */
	public ResultPartyDTO findPartyWithObjectOid(long objectOid)
			throws DataNotFoundException;

	/**
	 * Return a Collection of ResultPartyDTOs that of of the type sent in
	 * 
	 * @param type
	 * @return
	 * @throws CommunicationException
	 * @throws DataNotFoundException
	 */
	public List<ResultPartyDTO> findPartiesByType(long type)
			throws DataNotFoundException;

	/**
	 * Store the hierarchy node , do validation and raise the request
	 * 
	 * @param hierarchyNodeDTO
	 * @param sessionUserProfile
	 *            The current session user
	 * @param hierarchyNodeDTOBeforeImage
	 * @throws ValidationException
	 */
	public void storeNode(HierarchyNodeDTO hierarchyNodeDTO,
			ISessionUserProfile sessionUserProfile, 
			HierarchyNodeDTO hierarchyNodeDTOBeforeImage,RequestKindType[] requestKinds) throws ValidationException;

	/**
	 * Will return a list of allowable parent types that a child node may link
	 * to
	 * 
	 * @return
	 */
	public List<Long> getAllowableHierarchyParentTypesForChildTypes(
			long childType);

	/**
	 * Return a list of cost centers form the database
	 * 
	 * @return
	 */
	public List<CostCenterDBEnumDTO> getCostCenters();

	/**
	 * Find a party with given name, limit response to 5
	 * 
	 * @param objectOid
	 * @return
	 * @throws DataNotFoundException
	 */
	public List<ResultPartyDTO> findPartyWithOrganisationName(String name)
			throws DataNotFoundException;

	/**
	 * Find a party with given name, limit response to 5
	 * 
	 * @param objectOid
	 * @return
	 * @throws DataNotFoundException
	 */
	public List<ResultPartyDTO> findPartyWithOrganisationNameOfType(
			String name, long type) throws DataNotFoundException;

	/**
	 * Validate the hierarchy node excluding the contact details
	 * 
	 * @param hierarchyNodeDTO
	 * @throws ValidationException
	 */
	public void validateNodeWithoutContactDetail(
			HierarchyNodeDTO hierarchyNodeDTO) throws ValidationException;

	/**
	 * Return the organisations parent node history dated node
	 * 
	 * @return
	 */
	public List<HierarchyNodeLinkDTO> getNodeParentHistory(long partyID)
			throws SystemException, LogicExecutionException,
			InvalidPartyRegException;

	/**
	 * Return the organisations employee history dated node
	 * 
	 * @return
	 */
	public List<HierarchyEmployeeLinkDTO> getNodeEmployeeHistory(long partyID)
			throws SystemException, LogicExecutionException,
			InvalidPartyRegException;
	
	/**
	 * Reactivate the hierarchy node , will set the end date to null and the status to active
	 * 
	 * @param hierarchyNodeDTO
	 * @param sessionUserProfile
	 *            The current session user
	 * @param hierarchyNodeDTOBeforeImage
	 * @throws ValidationException
	 */
	public void reactivateNode(HierarchyNodeDTO hierarchyNodeDTO,
			ISessionUserProfile sessionUserProfile, 
			HierarchyNodeDTO hierarchyNodeDTOBeforeImage) throws ValidationException;
	
	/**
	 * Terminate the hierarchy node , do validation and raise the request<br/>
	 * Will follow a life cycle, first Pending Termination then Terminated
	 * 
	 * @param hierarchyNodeDTO
	 * @param sessionUserProfile
	 *            The current session user
	 * @param hierarchyNodeDTOBeforeImage
	 * @throws ValidationException
	 */
	public void terminateNode(HierarchyNodeDTO hierarchyNodeDTO,
			ISessionUserProfile sessionUserProfile, 
			HierarchyNodeDTO hierarchyNodeDTOBeforeImage) throws ValidationException;
	
	/**
	 * Will raise the bulk transfer request by raise all the branchTransfer requests
	 * @param transfers
	 * @param origRolesMap
	 * @throws ValidationException 
	 */
	public void raiseBulkAgreementTransferRequest(ISessionUserProfile sessionUserProfile, List<BulkAgreementTransferDTO> transfers) throws ValidationException;

	/**
	 * Find a hierarchy node using the external reference
	 * @param externalRef
	 * @return
	 * @throws DataNotFoundException
	 */
	public List<ResultPartyDTO> findHierarchyNodeWithExternalReference(String externalRef) throws DataNotFoundException;			

	/**
	 * Get all the agreements linked to a hierarchy node, includes most of the agreement details<br/>
	 * Will fetch only between the numbers specified, 0 to 50 will return the first 50 etc
	 * @param oid
	 * @return
	 */
	public Collection<SimpleAgreementDetailDTO> findAllAgreementsLinkedToHierarchyNode(long oid, Integer fromRecord, Integer toRecord);
	
	/**
	 * finds all the agreement home roles for the given agreement numbers
	 * @param agreementNumbers
	 * @throws DataNotFoundException 
	 */
	public Collection<AgreementRoleDTO> findAllAgreementHomeRoles(List<Long> agreementNumbers) throws DataNotFoundException;

	/**
	 * Find all outstanding home requests for agreements passed in
	 * @param agreementNumbers
	 * @return
	 * @throws RequestException
	 * @throws QueryTimeoutException
	 */
	public RequestEnquiryResultDTO findAllOutstandingHomeRequestsForAgreements(List<Long> agreementNumbers) throws RequestException, QueryTimeoutException;
	
	
	/**
	 * Finds the parent of the given hierarchy node
	 * @param oid
	 * @param type
	 * @return 
	 * @throws DataNotFoundException 
	 */
	public ResultPartyDTO findParentOfHierarchyNode(long oid, long type) throws DataNotFoundException;
	
	/**
	 * Validate the transfer requirements
	 * @param selections
	 * @throws ValidationException 
	 */
	public void validateBranchTransferRequirements(List<BulkAgreementTransferDTO> selections) throws ValidationException;

	/**
	 * Find all outstanding node requests
	 * @param agreementNumbers
	 * @return
	 * @throws RequestException
	 * @throws QueryTimeoutException
	 */
	public List<IRequestEnquiryRow> findOutstandingNodeRequest(long nodeOID, RequestKindType[] requestKinds) throws RequestException, QueryTimeoutException;
	
	
	/**
	 * Validate the chosen home to
	 * @param chosenHome
	 * @throws ValidationException if this node or the parents of the node are not of an active status
	 */
	public void validateHomeToChosen(ObjectReference oldHome,ResultPartyDTO newHome) throws ValidationException;
	
	/**
	 * 
	 * @param type
	 * @return
	 * @throws DataNotFoundException
	 */
	public List<IDValueDTO> getTypeListForCategories(long type) throws DataNotFoundException;
	
	/**
	 * Return a list of organisation types for the SOLOH Project from the database
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<OrganisationExternalTypeEnumDTO> getOrganisationTypes();
	
	/**
	 * Find the LBF NDP roles to check for main branch
	 * @param roletype
	 * @param branchtype
	 * @param branchOid
	 * @return
	 */
	public Long findLBFNDPRelations(long roletype, long branchtype, long branchOid);
	
	/**
	 * Return a list of branch categories for the SIMS Hierarchy Project from the database
	 * @return
	 */
	public List<BranchCategoryEnumDTO> getBranchCategories();
	
	public List<MiHierarchyNodeCharacteristicsEntity> findNodeCharacteristicsByBranchType (int branchType);
	
	public List<MiHierarchyNodeAddressClassificationEntity> findByMIHierNodeAddressClassBySuburb (String suburb);
		
	public MiHierarchyNodeAddressClassificationDTO buildMiHierarchyNodeAddressClassificationDTO(
			List<MiHierarchyNodeAddressClassificationEntity> classificationsEntity) ;
	
}
