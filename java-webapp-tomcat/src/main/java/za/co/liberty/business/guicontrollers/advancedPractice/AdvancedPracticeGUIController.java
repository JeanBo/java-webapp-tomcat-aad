package za.co.liberty.business.guicontrollers.advancedPractice;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.EJBContext;
import javax.ejb.Stateless;
import javax.naming.NamingException;

import org.apache.log4j.Logger;

import za.co.liberty.agreement.common.exceptions.LogicExecutionException;
import za.co.liberty.agreement.common.exceptions.LogicExecutionValidationException;
import za.co.liberty.business.agreement.IAgreementManagement;
import za.co.liberty.business.agreement.validator.IAgreementValidator;
import za.co.liberty.business.party.IPartyManagement;
import za.co.liberty.business.request.IGuiRequestManagement;
import za.co.liberty.common.domain.ApplicationContext;
import za.co.liberty.common.exceptions.businessexceptions.InvalidConformanceTypeException;
import za.co.liberty.dto.advancedPractice.AdvancedPracticeDTO;
import za.co.liberty.dto.advancedPractice.AdvancedPracticeDTOGrid;
import za.co.liberty.dto.advancedPractice.AdvancedPracticeManagerDTO;
import za.co.liberty.dto.advancedPractice.AdvancedPracticeMemberDTO;
import za.co.liberty.dto.agreement.AgreementRoleDTO;
import za.co.liberty.dto.agreement.maintainagreement.AdvancedPracticeRequestConfigurationDTO;
import za.co.liberty.dto.agreement.maintainagreement.ValidAgreementValuesDTO;
import za.co.liberty.dto.agreement.request.RaiseGuiRequestResultDTO;
import za.co.liberty.dto.contracting.ContractingSearchOptionsDTO;
import za.co.liberty.dto.contracting.ResultAgreementDTO;
import za.co.liberty.dto.contracting.ResultPartyDTO;
import za.co.liberty.dto.databaseenum.PartyStatusDBEnumDTO;
import za.co.liberty.dto.persistence.party.flow.PartyRoleRolePlayerFLO;
import za.co.liberty.dto.userprofiles.ISessionUserProfile;
import za.co.liberty.exceptions.SystemException;
import za.co.liberty.exceptions.data.DataNotFoundException;
import za.co.liberty.exceptions.data.ValidationException;
import za.co.liberty.exceptions.error.request.RequestConfigurationException;
import za.co.liberty.exceptions.error.request.RequestException;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.helpers.lookup.ServiceLocator;
import za.co.liberty.helpers.util.ComparatorUtil;
import za.co.liberty.helpers.util.ComparatorUtil.ObjectComparisonDifferences;
import za.co.liberty.interfaces.agreements.RoleKindType;
import za.co.liberty.interfaces.agreements.requests.RequestKindType;
import za.co.liberty.interfaces.gui.GuiRequestKindType;
import za.co.liberty.persistence.agreement.IAgreementEntityManager;
import za.co.liberty.persistence.party.IPartyEntityManager;
import za.co.liberty.srs.type.SRSType;
import za.co.liberty.srs.util.BigDecimalUtil;



@Stateless(name = "AdvancedPracticeGUIController") 
public class AdvancedPracticeGUIController implements IAdvancedPracticeGUIController, Serializable{



	private static final long serialVersionUID = 1L;
	public static final Logger logger = Logger.getLogger(AdvancedPracticeGUIController.class);

	@EJB
	IPartyManagement partyManagement;
	
	@EJB
	IAgreementManagement agreementManagement;
	
	@EJB
	IAgreementEntityManager agreementEntityManager;
	
	@EJB
	IPartyEntityManager partyEntityManager;
	
	
	@EJB 
	IGuiRequestManagement guiRequestManager;
	
	@EJB
	IAgreementValidator agreementValidator;
	
	@Resource
	EJBContext ejbContext;
	
	public List<ResultPartyDTO> findAdvancedPraticeWithExternalReference(String externalRef) throws DataNotFoundException {
		// TODO Auto-generated method stub
		List<ResultPartyDTO> list = new ArrayList<ResultPartyDTO>(); 
		ResultPartyDTO partyDTO = new ResultPartyDTO();
		boolean full = false;
		while(full==false){
			partyDTO.setFirstName("Test");
			partyDTO.setExternalReference("500");
			list.add(partyDTO);
			if(list.size() > 4){
				full = true;
			}
		}
		return list;
	}

	public Collection<AgreementRoleDTO> findAllAgreementHomeRoles(List<Long> agreementNumbers) throws DataNotFoundException {
		List<AgreementRoleDTO> list = new ArrayList<AgreementRoleDTO>(); 
		AgreementRoleDTO partyDTO = new AgreementRoleDTO();
		boolean full = false;
		while(full==false){
			partyDTO.setAgreementNumber(612L);
			partyDTO.setAgreementRoleKind(149L);
			list.add(partyDTO);
			if(list.size() > 4){
				full = true;
			}
		}
		return list;
	}

	public ResultPartyDTO findPartyWithObjectOid(long objectOid) throws DataNotFoundException {
		return partyManagement.findPartyWithObjectOid(objectOid);
	}
	
	


	public List<ResultPartyDTO> findPartyWithOrganisationName(String name) 	throws DataNotFoundException {
		ContractingSearchOptionsDTO searchOptions = new ContractingSearchOptionsDTO();
		searchOptions.setRetrieveNumberOfRecords(10);		
		return partyManagement.findPartyWithOrganisationName(name, searchOptions);
	}

	public List<ResultPartyDTO> findPartyWithOrganisationNameOfType(String name, long type)throws DataNotFoundException {
		ContractingSearchOptionsDTO searchOptions = new ContractingSearchOptionsDTO();
		searchOptions.setRetrieveNumberOfRecords(10);		
		return partyManagement.findPartyWithOrganisationNameAndOfType(name,type, searchOptions);
	}

	/**
	 * 
	 * This method will return AdvancedPracticeDTO practiceId(party) of practice
	 * will be passed in as the search parameter. If no party(Practice) is found
	 * then the default list of members and managers list are set and returned.
	 * else Practice details are set up in the AdvancedPracticeDTO. With the
	 * partyId , a list of all the party roles are retrived. By looping throught
	 * the partyRoles for the respective practiceId I get the agreement numbers
	 * linked to the partyRole and use it to search for the agreement roles
	 * (either manager or member roles) each time I populate the manager and
	 * member roleDTO and and passing them in to their specific list and return
	 * on completion.
	 * 
	 * @author MXM1904
	 * 
	 */
	public AdvancedPracticeDTO getAdvancedPracticeDTO(long practiceId)
			throws DataNotFoundException {
		AdvancedPracticeDTO practice = null;
		Object object =  partyManagement
		.getPartyDTOWithObjectOid(practiceId);
		if( object != null && object instanceof AdvancedPracticeDTO){
			practice = (AdvancedPracticeDTO) object;
		}		 

		return practice;
	}

		/**
		 * Set up agreement role data for one grid row
		 *
		 */
	public void setUpGridRoleData(AdvancedPracticeDTOGrid gridRow){
			if(gridRow.getAgreementParty() == null || gridRow.getAgreementParty().getOid() <= 0){
				try {
					if(gridRow.getRole().getAgreementNumber() != null){
						gridRow.setAgreementParty(partyManagement.findPartyIntermediaryWithAgreementNr(gridRow.getRole().getAgreementNumber()));	
						}else{
							logger.warn("This request can only be declined");
							throw  new DataNotFoundException(" Agreement numebr is null.  Note you can only decline this request!");
						
						}
				} catch (DataNotFoundException e1) {
					//display error
					ResultPartyDTO resultPartyDTO = new ResultPartyDTO();
					resultPartyDTO.setName("Unknown");
					gridRow.setAgreementParty(resultPartyDTO);
					//logger.error("Agreement " + gridRow.getRole().getRolePlayerReference().getOid() + " party data could not be found");
				}
			}

		}
	
  /**
   * Returns the list of manager(s) for the popUp 
   * @param partyID
   * @return List of AdvancedPracticeDTOGrid
   */
	public List<AdvancedPracticeDTOGrid> getManagerPopUpDetails(long partyID) {

		/**
		 * Run through the agreement roles and set up the grid data
		 * 
		 */
		AdvancedPracticeDTO practiceDTO;
		List<AdvancedPracticeDTOGrid> managerGridRoles = null;
		try {
			practiceDTO = ( !BigDecimalUtil.isEqual(new BigDecimal(partyID), BigDecimal.ZERO)   ? getAdvancedPracticeDTO(partyID) : null );
			if (practiceDTO != null) {

				List<AdvancedPracticeManagerDTO> managerRoles = practiceDTO
						.getAdvancedPracticeManagerDTOlist();

				managerGridRoles = new ArrayList<AdvancedPracticeDTOGrid>();

				for (AdvancedPracticeManagerDTO managerRole : managerRoles) {
					AdvancedPracticeDTOGrid gridRole = new AdvancedPracticeDTOGrid();
					gridRole.setRole(managerRole);

					setUpGridRoleData(gridRole);

					managerGridRoles.add(gridRole);
				}

			}
		} catch (DataNotFoundException e1) {
			logger.error("An error occured while setting up grid data", e1);
		}
		return managerGridRoles;
	}
	

	/**
	 * Validate all current agreement roles
	 * @param roles All the current agreement role, do not send in old roles as they will also be validated
	 * @throws ValidationException 
	 */
	public void validateAgreementRoles(AdvancedPracticeDTO advancedPracticeDTO,Collection<AgreementRoleDTO> currentAndFutureRoles, Collection<AgreementRoleDTO> pastRoles) throws ValidationException{
		agreementValidator.validateAdvancedPracticeAgreementRoles(advancedPracticeDTO,currentAndFutureRoles, pastRoles);//validateAgreementRoles(0L, 0L, null, currentAndFutureRoles, pastRoles);
	}
	
	
	
	
	/**
	 * Set up agreement role data for one grid row
	 *
	 */
	public void setUpManagerGridRoleData(AdvancedPracticeManagerDTO gridRow){
		if(gridRow.getAgreementNumber() == null || gridRow.getAgreementNumber() <= 0){
			try {
				if(gridRow.getRolePlayerReference() instanceof ResultPartyDTO
						&& gridRow.getRolePlayerReference().getTypeOid() == SRSType.HASADVANCEDPRACTICEMANAGER){
					//this means the agreement reports to a party that is not an Manager					
					gridRow.setAgreementNumber(gridRow.getAgreementNumber());
					//we now create a blank agreement role reference					
				}else{				
					gridRow.setAgreementNumber(partyManagement.findPartyIntermediaryWithAgreementNr(gridRow.getRolePlayerReference().getOid()).getOid());
				}
			} catch (DataNotFoundException e1) {
				//display error
				ResultPartyDTO party = new ResultPartyDTO();
				party.setName("Unknown");
				gridRow.setAgreementNumber(party.getOid());
				//logger.error("Agreement " + gridRow.getRolePlayerReference().getOid() + " party data could not be found");
			}
		}

	}

	public AdvancedPracticeDTO loadDeferredDataForRequest(AdvancedPracticeDTO advancedPracticeDTO,AdvancedPracticeDTO beforeImage,
			ValidAgreementValuesDTO validAgreementValues, RequestKindType... requestKindTypes) 
			throws CommunicationException, DataNotFoundException {

		return advancedPracticeDTO;
	}
	
	/**

	
	/**
	 * Compare the before and after MaintainAgreementDTO image to determine
	 * if MaintainIntermediaryAgreement request should be raised
	 * 
	 * @param maintainAgreementDTO
	 * @param previousMaintainAgreementDTO
	 * @return
	 */
	private boolean isMaintainAdvancedPracticeRequestAllowed(AdvancedPracticeDTO advancedPracticeDTO, AdvancedPracticeDTO previousAdvancedPracticeDTO) {
		if (advancedPracticeDTO==null ) {
			return false;
		}
		if (previousAdvancedPracticeDTO==null ) {
			return true;
		}
		
		try {
			AdvancedPracticeDTO currentClone = advancedPracticeDTO.getAdvancedPracticeDTOForRequestComparison(
						RequestKindType.MaintainAdvancedPractice);
			AdvancedPracticeDTO previousClone = (AdvancedPracticeDTO) 
					previousAdvancedPracticeDTO.getAdvancedPracticeDTOForRequestComparison(
							RequestKindType.MaintainAdvancedPractice);
			return !currentClone.equals(previousClone);
		} catch (InvalidConformanceTypeException e) {
			SystemException sys = new SystemException("Cannot do AdvancedPracticeDTO comparison when checking for " +
					"MaintainAdvancedPractice ",0,0);
			sys.initCause(e);
			throw sys;
		}
	}

	
	@SuppressWarnings("unused")
	public ResultAgreementDTO findAgreementWithParty(long partyOid)throws CommunicationException, DataNotFoundException{
				
		List<ResultAgreementDTO> agmts = agreementManagement.findAgreementWithParty(partyOid);
		if(agmts != null){
			return agmts.get(0);
		}else {
			return null;
		}
		
		
	}
	
	/**
	 * Determine the requests that should be raised from changes that 
	 * have been made by comparing the before and after images.
	 * 
	 * This will delegate comparison for each request to sub methods.
	 * 
	 * @param maintainAgreementDTO
	 * @param previousMaintainAgreementDTO
	 * @return
	 */
	private Set<RequestKindType> getAllowableRequestsForAdvancePractice(AdvancedPracticeDTO advancePracticeDTO, AdvancedPracticeDTO previousAdvancePracticeDTO) {
		Set<RequestKindType> ret = new HashSet<RequestKindType>();
		if (advancePracticeDTO==null) {
			return ret;
		}
		
		/**
		 * Determine based on the changes in the DTOs which requests should be raised, if any
		 */
		if (previousAdvancePracticeDTO!=null) {
			/**
			 * Check MaintainIntermediaryAgreement
			 */
			if (isMaintainAdvancedPracticeRequestAllowed(
					advancePracticeDTO,
					previousAdvancePracticeDTO)) {
				ret.add(RequestKindType.MaintainIntermediaryAgreement);
			}
			
		}
		return ret;
	}
	/**
	 * Will raise agreement request
	 * 
	 * @param ISessionUserProfile
	 * @param MaintainAdvancedPracticePageModel
	 * @throws ValidationException 
	 */
	public void raiseAdvancedPracticeRequest(ISessionUserProfile sessionUserProfile, AdvancedPracticeDTO practiceDTO, AdvancedPracticeDTO practiceBeforeDTO) throws ValidationException {

		
		ArrayList<String> errors = new ArrayList<String>();
			
		if(isRaiseAdvancedPracticeRequestAllowed( practiceDTO,  practiceBeforeDTO)){
			errors.add("You can not save the Practice if no changes were made");
		}
		if(!errors.isEmpty()){
	     throw new ValidationException(errors);
		}
		
			try{

				AdvancedPracticeRequestConfigurationDTO currrentAdvancedPracticeDTO = new AdvancedPracticeRequestConfigurationDTO();
				currrentAdvancedPracticeDTO.setAdvancedPracticeDTO(practiceDTO);

			//	Raise request for all the roles  to be created.
					
							RaiseGuiRequestResultDTO request = guiRequestManager.raiseGuiRequest(new ApplicationContext(), 
									sessionUserProfile, 
									null, 
									practiceDTO.getOid(),
									GuiRequestKindType.MaintainAdvancedPractice, 
									new RequestKindType[] {RequestKindType.MaintainAdvancedPractice}, 
									practiceDTO,practiceBeforeDTO);
						

			} catch (RequestConfigurationException e) {
				ejbContext.setRollbackOnly();
				throw new CommunicationException(e.getMessage(),e);
			} catch (RequestException e) {
				ejbContext.setRollbackOnly();
				if(e.getCause() instanceof LogicExecutionValidationException){
					
					if(errors.isEmpty()){
					//	if(!errors.isEmpty()){
						throw new ValidationException(e.getMessage());
					}else{
						throw new ValidationException(((LogicExecutionException)e.getCause()).getMessage());
					}
					
					
					
				}else{
					throw new CommunicationException(e.getMessage(),e);
				}
			}
				
	}
	
	
	/**
	 * Will return true if the MaintainAdvancedPrcatice Request is allowe to be raised<br/>
	 * Will compare the list of objects and see if there are any changes
	 * @param AdvancedPracticeDTO
	 * @param previousAdvancedPracticeDTO
	 * @return
	 */
	private boolean isRaiseAdvancedPracticeRequestAllowed(
			AdvancedPracticeDTO maintainAgreementDTO,
			AdvancedPracticeDTO previousAdvancedPracticeDTO) {
		boolean requestAllowed = true;

		List<ObjectComparisonDifferences> diffs = ComparatorUtil
				.compareObjects(maintainAgreementDTO,
						previousAdvancedPracticeDTO);

		if (diffs.size() == 0) {
			
		} else if (diffs.size() > 0) {
			// if adding a new home role then a cons code is required
			outer:
			for (ObjectComparisonDifferences diff : diffs) {
				if (diff.getFieldName().equalsIgnoreCase("compIDSet")) {
					continue outer;
				} else if (!diff.getFieldName().equalsIgnoreCase("compIDSet")){
					requestAllowed = false;
				}
			}
		}

		return requestAllowed;
	}
	
	
	
	
    /**
     * Returns the history party roles
     * @param partyOid
     * @param partyRoleType
     * @return History PartyRoles
     */	
	public List<PartyRoleRolePlayerFLO> getNonActivePartyRolesForPractice(long partyOid, long partyRoleType) {
    	return partyEntityManager.findActivePartyRolesWherePartyIsRolePlayerWithRolePlayerType(partyOid,partyRoleType,false);
   	}

	
    /**
     * Returns the history manager roles
     * @param agreements list
     * @return History ManagerRoles
     */	
	public List<AdvancedPracticeManagerDTO> getNonActiveManagerRoles(List<Long> agreements) throws DataNotFoundException {
		Collection<AgreementRoleDTO> managerAgmtRoles;
		List<AdvancedPracticeManagerDTO> managerList =null;
		
			managerAgmtRoles = agreementManagement.findAgreementRolesForAgreementsOfType(agreements, RoleKindType.ISADVANCEDPRACTICEMANAGEROF,true,false);
			managerList = new ArrayList<AdvancedPracticeManagerDTO>();
		
		for(AgreementRoleDTO roleDTO : managerAgmtRoles){
			managerList.add((AdvancedPracticeManagerDTO)roleDTO);
		}
		
		
		return managerList;
	}

    /**
     * Returns the history member roles
     * @param agreements list
     * @return History MemberRoles
     */	
	public List<AdvancedPracticeMemberDTO> getNonActiveMemberRoles(List<Long> agreements) throws DataNotFoundException {
		Collection<AgreementRoleDTO> membersAgmtRoles;
		List<AdvancedPracticeMemberDTO> membersList =null;
		
		membersAgmtRoles = agreementManagement.findAgreementRolesForAgreementsOfType(agreements, RoleKindType.ISADVANCEDPRACTICEMEMBEROF,true,false);
		membersList = new ArrayList<AdvancedPracticeMemberDTO>();
		
		for(AgreementRoleDTO roleDTO : membersAgmtRoles){
			membersList.add((AdvancedPracticeMemberDTO)roleDTO);
		}
		
		
		return membersList;
	}

	 
	/**
	 * @Override	 * 
	 */
	public List<? extends AdvancedPracticeMemberDTO> getUpPracticeRoles(long agreementNum) throws DataNotFoundException {

		List<? extends AdvancedPracticeMemberDTO> roleList = new ArrayList<AdvancedPracticeMemberDTO>();
		List<Long> agmtNumList = new ArrayList<Long>();
		agmtNumList.add(agreementNum);

		if (!agmtNumList.isEmpty()) {

			Collection<AgreementRoleDTO> managerAgmtRoles;
			List<AdvancedPracticeManagerDTO> managerList = null;

			managerAgmtRoles = agreementManagement
					.findAgreementRolesForAgreementsOfType(agmtNumList,
							RoleKindType.ISADVANCEDPRACTICEMANAGEROF, true,
							true);
			managerList = new ArrayList<AdvancedPracticeManagerDTO>();

			for (AgreementRoleDTO roleDTO : managerAgmtRoles) {
				managerList.add((AdvancedPracticeManagerDTO) roleDTO);
			}
			if(!managerList.isEmpty()){
				return managerList;
			}

			Collection<AgreementRoleDTO> membersAgmtRoles;
			List<AdvancedPracticeMemberDTO> membersList = null;

			membersAgmtRoles = agreementManagement
					.findAgreementRolesForAgreementsOfType(agmtNumList,
							RoleKindType.ISADVANCEDPRACTICEMEMBEROF, true,
							true);
			membersList = new ArrayList<AdvancedPracticeMemberDTO>();

			for (AgreementRoleDTO roleDTO : membersAgmtRoles) {
				membersList.add((AdvancedPracticeMemberDTO) roleDTO);
			}
			if(!membersList.isEmpty()){
				return membersList;
			}

		}
		return null;
	}

	/**
	 * Get AgreementEntityManager 
	 * @return 
	 */
	public IAgreementEntityManager getIAgreementEntityManager() {
		if (agreementEntityManager == null) {
			try {
				agreementEntityManager = ServiceLocator
						.lookupService(IAgreementEntityManager.class);
			} catch (NamingException e) {
				throw new CommunicationException(e);
			}
		}
		return agreementEntityManager;
	}

	public IPartyEntityManager getIPartyEntityManager() {

		if (partyEntityManager == null) {
			try {
				partyEntityManager = ServiceLocator
						.lookupService(IPartyEntityManager.class);
			} catch (NamingException e) {
				throw new CommunicationException(e);
			}
		}
		return partyEntityManager;
	}


	}
