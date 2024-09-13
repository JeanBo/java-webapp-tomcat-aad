package za.co.liberty.business.guicontrollers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;

import za.co.liberty.dto.contracting.ContractingSearchOptionsDTO;
import za.co.liberty.dto.contracting.ResultAgreementDTO;
import za.co.liberty.dto.contracting.ResultPartyDTO;
import za.co.liberty.dto.gui.context.AgreementSearchType;
import za.co.liberty.dto.gui.context.ContextSearchModelDTO;
import za.co.liberty.dto.gui.context.ContextSearchOptionsDTO;
import za.co.liberty.dto.gui.context.IContextSearchType;
import za.co.liberty.dto.gui.context.IPartySearchType;
import za.co.liberty.dto.gui.context.IndividualSearchType;
import za.co.liberty.dto.gui.context.OrganisationPracticeAgreementSearchType;
import za.co.liberty.dto.gui.context.OrganisationPracticeSearchType;
import za.co.liberty.dto.gui.context.OrganisationSearchType;
import za.co.liberty.dto.gui.context.ResultContextItemDTO;
import za.co.liberty.dto.gui.context.ResultContextSearchDTO;
import za.co.liberty.dto.userprofiles.ContextAgreementDTO;
import za.co.liberty.dto.userprofiles.ContextDTO;
import za.co.liberty.dto.userprofiles.ContextPartyDTO;
import za.co.liberty.dto.userprofiles.ISessionUserProfile;
import za.co.liberty.exceptions.data.DataNotFoundException;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.interfaces.agreements.AgreementStatusType;
import za.co.liberty.interfaces.agreements.ProductKindType;
import za.co.liberty.srs.type.SRSType;

public class ContextManagement implements IContextManagement {

	Logger logger = Logger.getLogger(this.getClass());
	
	/**
	 * Get the context for the context item
	 * 
	 * @param resultContextItemDTO
	 * @return
	 * @throws CommunicationException 
	 */
	public ContextDTO getContext(ResultContextItemDTO resultContextItemDTO) throws CommunicationException{
		return getContext(resultContextItemDTO.getPartyDTO(),
				resultContextItemDTO.getAgreementDTO());
	}

	/**
	 * Get the context for party only.
	 * 
	 * @param resultPartyDTO
	 * @return
	 * @throws CommunicationException 
	 */
	public ContextDTO getContext(ResultPartyDTO resultPartyDTO) throws CommunicationException{
		return getContext(resultPartyDTO, null);
	}

	/**
	 * Get the context for agreement only.
	 * 
	 * @param resultAgreementDTO
	 * @return
	 * @throws CommunicationException 
	 */
	public ContextDTO getContext(ResultAgreementDTO resultAgreementDTO) throws CommunicationException{
		return getContext(null, resultAgreementDTO);
	}

	/**
	 * Get the context for both party and agreement.
	 * 
	 * @param resultPartyDTO
	 * @param resultAgreementDTO
	 * @return
	 * @throws CommunicationException 
	 */
	public ContextDTO getContext(ResultPartyDTO resultPartyDTO,
			ResultAgreementDTO resultAgreementDTO) throws CommunicationException {

		ContextDTO contextDTO = new ContextDTO();
		ContextAgreementDTO contextAgreementDTO = new ContextAgreementDTO();
		ContextPartyDTO contextPartyDTO = new ContextPartyDTO();

		if (resultAgreementDTO != null) {
			populateContext(contextAgreementDTO, resultAgreementDTO);
		}
		if (resultPartyDTO != null) {
			populateContext(contextPartyDTO, resultPartyDTO);
			
//			/* Retrieve other agreements that are available */
//			try {
////				contextDTO.setAllAgreementsList(agreementManagementBean.findAgreementWithParty(
////						resultPartyDTO.getPartyOid()));
//			} catch (DataNotFoundException e) {
//				contextDTO.setAllAgreementsList(null);
//				// No problem, ignore
//			} catch (CommunicationException e) {
//				throw e;
//			}
			// No agreements are listed for now
			contextDTO.setAllAgreementsList(new ArrayList());
		}

		contextDTO.setAgreementContextDTO(contextAgreementDTO);
		contextDTO.setPartyContextDTO(contextPartyDTO);

		return contextDTO;
	}

	@Override
	public ResultContextSearchDTO searchForContext(ISessionUserProfile sessionUser, IContextSearchType searchType,
			Object value, ContextSearchOptionsDTO options) throws CommunicationException {

		
		/* Validate arguments */
		if (value == null) {
			throw new NullPointerException("Context search value may not be null");
		}
		if (value.getClass() != searchType.getValueClassType()) {
			// The value should be a subclass of the specified value class.
			boolean isValid = false;
			for (Class clazz : value.getClass().getClasses()) {
				if (clazz == searchType.getValueClassType()) {
					isValid= true;
					break;
				}
			}
			if (!isValid) {
				throw new IllegalArgumentException(
					"The search value is not a subclass of \""
							+ searchType.getValueClassType().toString() + "\"");
			}
		}

		/* Init */
		ContextSearchModelDTO beanSearchModel = new ContextSearchModelDTO();
		beanSearchModel.setContextSearchOptions((options == null) ? new ContextSearchOptionsDTO()
				: options);
		beanSearchModel.setSearchType(searchType);
		beanSearchModel.setSearchValue(value);
		
		List<ResultContextItemDTO> itemList = new ArrayList<ResultContextItemDTO>();

//		/* Do the search */
		if (searchType instanceof AgreementSearchType) {
			/* An agreement search */
			itemList = searchForContextAgreement(
					sessionUser,
					(AgreementSearchType) searchType, value);
			beanSearchModel = null;
//		} else	if (searchType instanceof OrganisationPracticeAgreementSearchType) {
//			/* An agreement search */
//			itemList = searchForContextAgreement(
//					sessionUser,
//					(OrganisationPracticeAgreementSearchType) searchType, value);
//			beanSearchModel = null;
		} else {
			/* Configure options */
			ContractingSearchOptionsDTO contractOptions = 
				new ContractingSearchOptionsDTO();
			contractOptions.setRetrieveStartingFrom(1);
			// Retrieve one extra to ensure there are more results.
			contractOptions.setRetrieveNumberOfRecords(
					beanSearchModel.getContextSearchOptions().getRetrieveNumberOfRecords()+1);
			
			/* do a party search */
			itemList = searchForContextParty(sessionUser,
					(IPartySearchType) searchType,
					value, contractOptions, beanSearchModel);
		}

		/* Assemble the return objects */
		ResultContextSearchDTO result = new ResultContextSearchDTO();
		result.setResultList(itemList);
		if ((searchType instanceof AgreementSearchType) == false) {
//			result.setHasMoreData(beanSearchModel.isMoreRecordsAvailable()); //MXM1904 i commented out this section of the code due to the error it was giving after agmt number search..
		}
		
		result.setSearchModel(beanSearchModel);
		return result;
	}

	@Override
	public ResultContextSearchDTO next(ISessionUserProfile sessionUser, ContextSearchModelDTO beanSearchModel)
			throws CommunicationException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Long> getServicedAndManagedByAdvisorList() throws CommunicationException {
		return new ArrayList();
	}

	@Override
	public ResultAgreementDTO findAgreementWithSRSAgreementNr(long srsAgreementNr)
			throws CommunicationException, DataNotFoundException {
		
		return newExampleResultAgreementDTO();
	}

	@Override
	public List<ResultPartyDTO> findPartyWithUacfID(String uacfID)
			throws CommunicationException, DataNotFoundException {
		// TODO Auto-generated method stub
		return new ArrayList<ResultPartyDTO>();
	}

	/**
	 * Do a party type search. Requires that one of the search methods are
	 * called first.
	 * 
	 * @param searchType
	 * @param value
	 * @param searchOptionsDTO
	 * @param searchModel
	 * @return
	 * @throws CommunicationException
	 */
	private List<ResultContextItemDTO> searchForContextParty(
			ISessionUserProfile sessionUser,
			IPartySearchType searchType, Object value, 
			ContractingSearchOptionsDTO searchOptionsDTO,
			ContextSearchModelDTO searchModel)
			throws CommunicationException {
		long partyTime = System.currentTimeMillis();
		
		logger.info("ContextManagement.searchForContextParty " + searchType + "  value=" + value);
		/* Do the search */
		List<ResultPartyDTO> list = null;
		if (searchType == IndividualSearchType.PERSON_DETAIL) {
//				list = partyManagementBean
//						.findPartyWithPartyName((SearchIndividualNameDTO) value,searchOptionsDTO);
		} else if (searchType == IndividualSearchType.UACF_ID) {
			
			list = new ArrayList<ResultPartyDTO>();
			list.add(newExampleResultPartyDTO());
		} else if (searchType == IndividualSearchType.ID_NUMBER) {
//				List<ResultPartyDTO> dtos = partyManagementBean
//						.findPartyWithIdentityNumber((String) value);
//				list = new ArrayList<ResultPartyDTO>();
//				list.addAll(dtos);
		} else if (searchType == IndividualSearchType.PASSPORT_NUMBER) {
//				List<ResultPartyDTO> dtos  = partyManagementBean
//						.findPartyWithPassportNumber((String) value);
//				list = new ArrayList<ResultPartyDTO>();
//				list.addAll(dtos);
		} else if (searchType == OrganisationSearchType.ORGANISATION_NAME) {
//				list = partyManagementBean
//						.findPartyWithOrganisationName((String) value,searchOptionsDTO);
		} else if (searchType == OrganisationSearchType.REGISTRATION_NR) {
//				list = partyManagementBean
//						.findPartyWithOrganisationRegistrationNumber((String) value,
//								searchOptionsDTO);
		} else if (searchType == OrganisationSearchType.VAT_REGISTRATION_NR) {
//				list = partyManagementBean
//						.findPartyWithOrganisationVatRegistrationNumber((String) value,
//								searchOptionsDTO);
		} else if (searchType == OrganisationPracticeSearchType.PRACTICE_NAME) {
//				searchOptionsDTO.setRetrieveNumberOfRecords(60);
//				searchOptionsDTO.setRegistrationType(SRSType.ADVANCEDPRACTICE);
//				list = partyManagementBean
//			    .findPartyWithOrganisationName((String) value,
//						searchOptionsDTO);
		} else if (searchType == OrganisationPracticeSearchType.PARTY_ID) {
//				List<ResultPartyDTO> partyList = new ArrayList<ResultPartyDTO>();
//				partyList.add(partyManagementBean
//						.findAdvancedPracticeWithObjectOid((Long) value));
//				list = partyList;
		}	else if (searchType == OrganisationSearchType.HIERARCHY_NODE_DETAIL) {
//				HierarchySearchDetailDTO searchDetail = (HierarchySearchDetailDTO) value;
//				if(searchDetail.getName() != null && !searchDetail.getName().equals("")){
//					if(searchDetail.getChannel() != null 
//							&& searchDetail.getChannel().getPartyOid() != 0 && searchDetail.getType() != null && searchDetail.getType().getOid() != 0){
//						list = partyManagementBean.findHierarchyNodesByNameTypeAndChannel(searchDetail.getName() ,searchDetail.getType().getOid(), searchDetail.getChannel().getPartyOid());
//					}else if(searchDetail.getChannel() != null 
//							&& searchDetail.getChannel().getPartyOid() != 0){
//						list = partyManagementBean.findHierarchyNodesByNameAndChannel(searchDetail.getName(), searchDetail.getChannel().getPartyOid());
//						
//					}else if(searchDetail.getType() != null && searchDetail.getType().getOid() != 0){
//						list = partyManagementBean.findPartyWithOrganisationNameAndOfType(searchDetail.getName(), searchDetail.getType().getOid(), searchOptionsDTO);
//					}
//				}				
		} else if (searchType == OrganisationSearchType.HIERARCHY_NODE_EXTERNAL_REFERENCE) {
//				list = partyManagementBean.findHierarchyNodeWithExternalReference((String) value, searchOptionsDTO);			
		}
		
		partyTime = System.currentTimeMillis() - partyTime;
		long agreementTime = System.currentTimeMillis();
		searchModel.setLastRetrievedPartyRecordNumber(
				searchModel.getLastRetrievedPartyRecordNumber()+list.size());
		
		List<ResultPartyDTO> unProcessedList = new ArrayList<ResultPartyDTO>();
		List<ResultContextItemDTO> itemList = processPartyResult(sessionUser,list,
				searchOptionsDTO.getRetrieveNumberOfRecords()-1, 
				unProcessedList,
				searchModel.getContextSearchOptions().isRetrievePartyOnly());
		searchModel.setUnProcessedList(unProcessedList);
		searchModel.setMoreRecordsAvailable(unProcessedList.size()>0);
		
		if (logger.isDebugEnabled()) {
			logger.debug("Party Context Search complete  partyRecords="+list.size()
					+ " ,agreementRecords (approx)="+itemList.size()
					+ " ,unprocessed party="+unProcessedList.size()
					+ " ,partyTime="+partyTime+" ,agreementTime="
					+ (System.currentTimeMillis()-agreementTime));
		}
		
		
		// The seach type is OrganisationPracticeSearchType the manager
		// details need to be added
		if (searchType == OrganisationPracticeSearchType.PRACTICE_NAME
				|| searchType == OrganisationPracticeSearchType.PARTY_ID) {
//				setLinkedDataOnSearchResulat(itemList);
		}

		
		
		return itemList; 

	}
	
	
	
	/**
	 * Functionality for Agreement type searches.  Requires that one of the
	 * search methods are called first.
	 * 
	 * @param type
	 * @param value
	 * @return
	 * @throws CommunicationException 
	 */
//	private List<ResultContextItemDTO> searchForContextAgreement(
//			ISessionUserProfile sessionUser,
//			AgreementSearchType type, Object value)
//			throws CommunicationException {
	
	private List<ResultContextItemDTO> searchForContextAgreement(
			ISessionUserProfile sessionUser,
			Object type, Object value)
			throws CommunicationException {
		
		long startTime = System.currentTimeMillis();
		List<ResultAgreementDTO> agreementList = null;
		ResultAgreementDTO agreementResult = null;
//		try {
			
			if (type instanceof AgreementSearchType) {
				agreementResult = newExampleResultAgreementDTO();
				

			} 
//			else 	if (type instanceof OrganisationPracticeAgreementSearchType) {
//				
//				if (type == OrganisationPracticeAgreementSearchType.PRACTICE_MANAGER_MEMBER_AGREEMENT_NO) { 
//					
//					agreementResult = agreementManagementBean
//					.findAgreementWithSRSAgreementNr((Long) value);
//					
//
//					
//					
//				}  else if (type == OrganisationPracticeAgreementSearchType.PRACTICE_MANAGER_MEMBER_CONS_CODE) {
//					agreementResult = agreementManagementBean
//					.findAgreementWithConsultantCode((Long) value);
//				}
//			}
			

			/* Assemble result */
			List<ResultContextItemDTO> itemList = new ArrayList<ResultContextItemDTO>();
			ResultContextItemDTO item = new ResultContextItemDTO();
			
			//check if agreement can be selected
			boolean selectable = (agreementResult.getAgreementNumber() == null) ? true : false;
			if(agreementResult.getAgreementNumber() != null){
				//check rules
				selectable = true;
//						
//							securityManagementBean.canUserViewAgreementDetails(agreementResult.getAgreementNumber(),
//						agreementResult.getHasHomePartyOid(), sessionUser);
			}			
			item.setMayBeSelected(selectable);
			item.setAgreementDTO(agreementResult);
			itemList.add(item);
			
			/* Retrieve party */
//			try {
				
//				if (type instanceof OrganisationPracticeAgreementSearchType) {
//					 List<Long> partyRoleTypeList = new ArrayList<Long>();
//					 partyRoleTypeList.add(SRSType.HASADVANCEDPRACTICEMANAGER);
//					 partyRoleTypeList.add(SRSType.HASADVANCEDPRACTICEMEMBER);
//					 
//					 //Get Party Details 
//						try {
//							ResultPartyDTO partyDTO = partyManagementBean
//							.findAdvancedPracticeFoaAGivenAgreementNr(agreementResult.getAgreementNumber(),partyRoleTypeList );
//							if(partyDTO.getOid() == 0){
//								throw new DataNotFoundException("No records found for the areement " );
//							}
//							item.setPartyDTO(partyDTO);
//							
//						} catch (NoResultException e) {
//							throw new DataNotFoundException("No records found for the areement" );
//						}
//
//				}else{
					item.setPartyDTO(newExampleResultPartyDTO());
//				}
				
				
//			} catch (DataNotFoundException e) {
//				throw new DataNotFoundException("Unable to retrieve party for the specified agreement",e);
//			}
//			if (logger.isDebugEnabled()) {
				logger.info("Agreement Context Search complete  records="+itemList.size() +
						" totalTime="+(System.currentTimeMillis()-startTime));
//			}
			
			
			// The seach type is OrganisationPracticeSearchType
			// the manager details need to be added
			if (type instanceof OrganisationPracticeAgreementSearchType) {
//				setLinkedDataOnSearchResulat(itemList);
			}
			
			
			
			return itemList;
//		} catch (DataNotFoundException e) {
//			return new ArrayList<ResultContextItemDTO>();
//		}
	}


	/**
	 * Add agreement information to the party result and ensures that next
	 * functionality is correctly implemented by cutting the result off
	 * at the maximum required (more might be returned if the last party have
	 * more than one agreement).
	 * 
	 * @param partyResultList
	 * @param recordLimit
	 * @param unprocessedList records that are above the recordLimit and not processed
	 * 		will be added to this list.
	 * @param isRetrievePartyOnly
	 * @return
	 * @throws CommunicationException
	 */
	private List<ResultContextItemDTO> processPartyResult(
			ISessionUserProfile sessionUser,
			List<ResultPartyDTO> partyResultList,
			long recordLimit,
			List<ResultPartyDTO> unProcessedList,
			boolean isRetrievePartyOnly)
			throws CommunicationException {

		if (logger.isDebugEnabled()) {
			logger.debug("Context Search processPartyResult    recordLimit="+recordLimit
				+ "  ,partyList.size="+partyResultList.size());
		}
		
		/* Initialise */
		List<ResultContextItemDTO> itemList = new ArrayList<ResultContextItemDTO>(
				partyResultList.size() + 5);
		List<ResultPartyDTO> processedList = new ArrayList<ResultPartyDTO>(
				partyResultList.size() + 5);

		long count = 0;
		
		/* Additional processing for each party retrieved */
		for (ResultPartyDTO dto : partyResultList) {
			if (itemList.size() >= recordLimit) {
				/* Move unprocessed results to session model (for next functionality) */
				unProcessedList.addAll(partyResultList);
				unProcessedList.removeAll(processedList);
				if (logger.isDebugEnabled()) {
					logger.debug("--Context Search processPartyResult - There are more " +
							"records to process, at least "	+ unProcessedList.size());
				}
				/* End */				
				break;
			}
			
			if (logger.isDebugEnabled()) {
				logger.debug("--Context Search processPartyResult - Processing Party   partyOid="
						+dto.getPartyOid() + " ,name="+dto.getName());
			}
			
			/* Convert party to context item */
			ResultContextItemDTO item = new ResultContextItemDTO();
			item.setPartyDTO(dto);
			itemList.add(item);
			processedList.add(dto);
			
//			check party view rules
			boolean selectable  = true;
//					securityManagementBean.canUserViewPartyDetails(dto.getPartyOid(), sessionUser);
			
			/* Add agreement info */
			if (isRetrievePartyOnly) { 		
				item.setMayBeSelected(selectable);				
			} 
//			else {
//				/* Add agreements */
//				List<ResultAgreementDTO> agreementList;
//				try {
//					agreementList = agreementManagementBean
//							.findAgreementWithParty(dto.getPartyOid());
//
//					boolean isProcessedFirst = false;
//					for (ResultAgreementDTO agreementDTO : agreementList) {
//						if (logger.isDebugEnabled()) {
//							logger.debug("----Context Search processPartyResult  add agreement nr =" 
//								+ agreementDTO.getAgreementNumber());
//						}
//						if (item.getAgreementDTO() == null) {
//							// First agreement for party
//							item.setAgreementDTO(agreementDTO);
//						} else {
//							// Next agreement for party, add new result
//							item = new ResultContextItemDTO();
//							item.setPartyDTO(dto);
//							item.setAgreementDTO(agreementDTO);
//							itemList.add(item);
//						}
//						
//						//check for agreement view rules						
//						if(selectable){
//						/* Apply rules */							
//							selectable = (agreementDTO.getAgreementNumber() == null) ? true : false;
//							if(agreementDTO.getAgreementNumber() != null){
//								//check rules
//								selectable = securityManagementBean.canUserViewAgreementDetails(
//										agreementDTO.getAgreementNumber(), agreementDTO.getHasHomePartyOid(), sessionUser);
//							}			
//							item.setMayBeSelected(selectable);
//						}						
//					}
//				
//				} catch (DataNotFoundException e) {
//					// There are not agreements so rule (above) does not apply
//					item.setMayBeSelected(true);
//					
//					// No agreement for party, ignore
//					logger.debug("No agreement found for partyOid="
//							+ dto.getPartyOid());
//				}
//
//			}

			if (item.getAgreementDTO() == null) {
				// Ensure Agreement is set regardless
				item.setAgreementDTO(new ResultAgreementDTO());
			}
			
		}
		return itemList;
	}
	
	/**
	 * Copy properties that are already available in the search result into the
	 * context
	 * 
	 * @param contextPartyDTO
	 * @param partyDTO
	 */
	private void populateContext(ContextPartyDTO contextPartyDTO,
			ResultPartyDTO partyDTO) {

		contextPartyDTO.setPartyOid(partyDTO.getPartyOid());
		contextPartyDTO.setTypeOid(partyDTO.getTypeOid());
		contextPartyDTO.setComponentOid(partyDTO.getComponentOid());
		if (partyDTO.getTypeOid() == SRSType.PERSON) {
			contextPartyDTO.setIntermediaryType("Person");
		} else if (partyDTO.getTypeOid() == SRSType.ORGANISATION) {
			contextPartyDTO.setIntermediaryType("Organisation");
		}else if (partyDTO.getTypeOid() == SRSType.ADVANCEDPRACTICE) {
			contextPartyDTO.setIntermediaryType("AdvancedPractice");
		}
		contextPartyDTO.setUacfID(partyDTO.getUacfID());		
		contextPartyDTO.setDateOfBirth(partyDTO.getDateOfBirth());
		contextPartyDTO.setIdNumber(partyDTO.getIdNumber());
		contextPartyDTO.setName(partyDTO.getName());

	}

	/**
	 * Copy properties that are already available in the search result into the
	 * context
	 * 
	 * @param contextAgreementDTO
	 * @param agreementDTO
	 */
	private void populateContext(ContextAgreementDTO contextAgreementDTO,
			ResultAgreementDTO agreementDTO) {
		boolean unitSet = false;
		boolean divisionSet = false;
		boolean branchSet = false;
		contextAgreementDTO.setAgreementDivision(agreementDTO.getAgreementDivision());				
		contextAgreementDTO.setAgreementNumber((agreementDTO.getAgreementNumber() == 0) ? null : agreementDTO.getAgreementNumber());
		contextAgreementDTO.setConsultantCode(agreementDTO.getConsultantCodeFormatted());		

		contextAgreementDTO.setBrokerageName(agreementDTO.getBrokerageName());
		contextAgreementDTO.setAgreementStatus(agreementDTO
				.getAgreementStatus());
		contextAgreementDTO.setAgreementStatusReason(agreementDTO.getStatusReason());
		contextAgreementDTO.setAgreementStartDate(agreementDTO.getStartDate());
		contextAgreementDTO.setAgreementEndDate(agreementDTO.getEndDate());
		contextAgreementDTO.setHasHomePartyOid(agreementDTO.getHasHomePartyOid());
		contextAgreementDTO.setProperAgreementNumber(agreementDTO.getProperAgreementNumber());
		
		// SSM2707 Market Integration 21/09/2015 SWETA MENON Begin
		if (contextAgreementDTO.getAgreementNumber() != null) {
//			String salesCategory = (String) agreementEntityManager
//					.getAgreementPropertyOfKind(PropertyKindType.SalesCategory,
//							contextAgreementDTO.getAgreementNumber());
//			if (salesCategory != null) {
				contextAgreementDTO.setSalesCategory("Agent");
//			}
		}
//		// SSM2707 Market Integration 21/09/2015 SWETA MENON End
//		if(contextAgreementDTO != null && contextAgreementDTO.getAgreementNumber() != null &&contextAgreementDTO.getAgreementNumber() != 0){
//			try {
//				ResultPartyDTO branch = agreementManagementBean.getBranchForAgreement(contextAgreementDTO.getAgreementNumber());
//				contextAgreementDTO.setBranch(branch);
//				branchSet = true;
//				//now we get the division
//				ResultPartyDTO division = partyManagementBean.getDivisionForHierarchyNode(branch.getOid(), branch.getTypeOid());			
//				contextAgreementDTO.setDivision(division);
//				divisionSet = true;
//			} catch (Exception e) {				
//				logger.warn("Could not get the branch for the agreement("+contextAgreementDTO.getAgreementNumber()+") in context");
//			}
//			try {
//				//setting the unit if it is linked to one
//				Collection<AgreementRoleDTO> hasHomeRole = agreementManagementBean.getAgreementRoleDTOForAgreement(contextAgreementDTO.getAgreementNumber(), RoleKindType.HASHOME);
//				if(hasHomeRole.size() > 0){		
//					AgreementRoleDTO role = hasHomeRole.iterator().next();
//					if(role.getRolePlayerReference() != null && role.getRolePlayerReference().getTypeOid() == SRSType.UNIT){
//						ResultPartyDTO unit = partyManagementBean.findPartyWithObjectOid(role.getRolePlayerReference().getOid());
//						contextAgreementDTO.setUnit(unit);
//						unitSet = true;
//					}				
//				}				
//			} catch (Exception e) {				
//				logger.warn("Could not get the unit for the agreement("+contextAgreementDTO.getAgreementNumber()+") in context");
//			}	
//			//get the kroll check property
//			Integer property = (Integer)agreementEntityManager.getAgreementPropertyOfKind(PropertyKindType.KROLLCheckDone, contextAgreementDTO.getAgreementNumber());
//			if(property != null){
//				contextAgreementDTO.setKrollCompleted((property == 1) ? true : false);
//			}else{
//				contextAgreementDTO.setKrollCompleted(false);
//			}						
//			
//		}
		if(!unitSet){
			contextAgreementDTO.setUnit(new ResultPartyDTO());
		}
		if(!divisionSet){
			contextAgreementDTO.setDivision(new ResultPartyDTO());
		}
		if(!branchSet){
			contextAgreementDTO.setBranch(new ResultPartyDTO());
		}
		
		contextAgreementDTO.setServicedAdvisorsList(getServicedAndManagedByAdvisorList());
	}
	
	// ===================================================================================================================================
	// 
	// 
	public ResultAgreementDTO newExampleResultAgreementDTO() {
		ResultAgreementDTO dto = new ResultAgreementDTO();
		dto.setActive(true);
		dto.setAgreementDivision(ProductKindType.AGENTINTERMEDIARYAGREEMENT);
		dto.setAgreementNumber(123l);
		dto.setAgreementStatusType(AgreementStatusType.ACTIVE);
		dto.setAgreementStatus("Active");
		dto.setConsultantCode(1234567890123L);
		dto.setConsultantCodeFormatted("1234567890123");
		dto.setSalesCategory("Agent");
		dto.setAgreementDivision(ProductKindType.AGENTINTERMEDIARYAGREEMENT);
		dto.setBranchName("Agency Branch");
		return dto;
	}
	
	
	public ResultPartyDTO newExampleResultPartyDTO() {
		ResultPartyDTO dto = new ResultPartyDTO();
		dto.setTypeOid(SRSType.PERSON);
		dto.setFirstName("Jean");
		dto.setLastName("Bodemer");
		dto.setName("Jean Bodemer");
		dto.setIdNumber("7401012222083");
		dto.setJobTitle("Mechanic");
		dto.setOid(1);
		dto.setPartyOid(123);
		dto.setUacfID("SRS1802");
		// TODO Auto-generated method stub
		return dto;
	}
}
