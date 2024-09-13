package za.co.liberty.business.guicontrollers.advancedPractice;

import java.util.Collection;
import java.util.List;

import javax.ejb.Local;

import za.co.liberty.agreement.common.exceptions.LogicExecutionException;
import za.co.liberty.dto.advancedPractice.AdvancedPracticeDTO;
import za.co.liberty.dto.advancedPractice.AdvancedPracticeDTOGrid;
import za.co.liberty.dto.advancedPractice.AdvancedPracticeManagerDTO;
import za.co.liberty.dto.advancedPractice.AdvancedPracticeMemberDTO;
import za.co.liberty.dto.agreement.AgreementRoleDTO;
import za.co.liberty.dto.agreement.maintainagreement.ValidAgreementValuesDTO;
import za.co.liberty.dto.contracting.ResultAgreementDTO;
import za.co.liberty.dto.contracting.ResultPartyDTO;
import za.co.liberty.dto.databaseenum.PartyStatusDBEnumDTO;
import za.co.liberty.dto.persistence.party.flow.PartyRoleRolePlayerFLO;
import za.co.liberty.dto.userprofiles.ISessionUserProfile;
import za.co.liberty.exceptions.data.DataNotFoundException;
import za.co.liberty.exceptions.data.ValidationException;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.interfaces.agreements.requests.RequestKindType;
import za.co.liberty.persistence.agreement.IAgreementEntityManager;
import za.co.liberty.persistence.party.IPartyEntityManager;

@Local
public interface IAdvancedPracticeGUIController {

	/**
	 * Return a AdvancedPracticeDTO using the given partyid
	 * 
	 * @param partyID
	 * @return
	 * @throws DataNotFoundException
	 */
	public AdvancedPracticeDTO getAdvancedPracticeDTO(long practiceId)
			throws DataNotFoundException;


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
	 * Find a hierarchy node using the external reference
	 * @param externalRef
	 * @return
	 * @throws DataNotFoundException
	 */
	public List<ResultPartyDTO> findAdvancedPraticeWithExternalReference(String externalRef) throws DataNotFoundException;			


	
	/**
	 * finds all the agreement home roles for the given agreement numbers
	 * @param agreementNumbers
	 * @throws DataNotFoundException 
	 */
	public Collection<AgreementRoleDTO> findAllAgreementHomeRoles(List<Long> agreementNumbers) throws DataNotFoundException;

	/**
	 * Set up agreement role data for one grid row
	 *
	 */
	public void setUpManagerGridRoleData(AdvancedPracticeManagerDTO gridRow);

	
	
	public AdvancedPracticeDTO loadDeferredDataForRequest(AdvancedPracticeDTO advancedPracticeDTO,AdvancedPracticeDTO beforeImage, ValidAgreementValuesDTO validAgreementValues, RequestKindType... requestKindTypes) throws CommunicationException, DataNotFoundException;
	/**
	 * Will raise agreement request <br/>
	 * 
	 * @param transfers
	 * @param origRolesMap
	 * @throws ValidationException 
	 */
	public void raiseAdvancedPracticeRequest(ISessionUserProfile sessionUserProfile ,AdvancedPracticeDTO practiceDTO ,AdvancedPracticeDTO practiceDTOBeforeImage)throws ValidationException;
	


	public ResultAgreementDTO findAgreementWithParty(long partyOid)throws CommunicationException, DataNotFoundException;
	
	/**
	 * Set up agreement role data for one grid row
	 *
	 */
	public void setUpGridRoleData(AdvancedPracticeDTOGrid gridRow) throws LogicExecutionException;
	
	/**
	 * Validate all current agreement roles
	 * @param roles All the current agreement role, do not send in old roles as they will also be validated
	 * @throws ValidationException 
	 */
	public void validateAgreementRoles(AdvancedPracticeDTO advancedPracticeDTO,Collection<AgreementRoleDTO> currentAndFutureRoles, Collection<AgreementRoleDTO> pastRoles)throws ValidationException;
	
    /**
     * Returns the history party roles
     * @param partyOid
     * @param partyRoleType
     * @return History PartyRoles
     */	
	public List<PartyRoleRolePlayerFLO> getNonActivePartyRolesForPractice(long partyOid , long partyRoleType);
	
    /**
     * Returns the history manager roles
     * @param agreements list
     * @return History ManagerRoles
     */	
	public List<AdvancedPracticeManagerDTO> getNonActiveManagerRoles(List<Long> agreements)  throws DataNotFoundException;
	
	
	 /**
     * Returns the history member roles
     * @param agreements list
     * @return History MemberRoles
     */	
	public List<AdvancedPracticeMemberDTO> getNonActiveMemberRoles(List<Long> agreements)  throws DataNotFoundException;
	
	  /**
	   * Returns the list of manager(s) for the popUp 
	   * @param partyID
	   * @return
	   */
	public List<AdvancedPracticeDTOGrid> getManagerPopUpDetails(long partyID);
	
	/**
	 * Get the roles an agreement plays. 
	 * This is for the agreement hierarchy.
	 * 
	 * @param agreementNum
	 * @return
	 * @throws DataNotFoundException
	 */
	public List<? extends AdvancedPracticeMemberDTO>  getUpPracticeRoles(long agreementNum) throws DataNotFoundException ;
	
	/**
	 * Get AgreementEntityManager 
	 * @return 
	 */
	public IAgreementEntityManager getIAgreementEntityManager() ;
	
	
	/**
	 * Get the PartyEntityManager interface
	 * @return
	 */
	public IPartyEntityManager getIPartyEntityManager() ;



}