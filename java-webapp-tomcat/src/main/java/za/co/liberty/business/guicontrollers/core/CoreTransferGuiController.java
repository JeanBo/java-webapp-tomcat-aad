package za.co.liberty.business.guicontrollers.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.naming.NamingException;

import org.apache.log4j.Logger;

import za.co.liberty.agreement.common.exceptions.LogicExecutionException;
import za.co.liberty.agreement.common.exceptions.LogicExecutionValidationException;
import za.co.liberty.business.agreement.IAgreementManagement;
import za.co.liberty.business.party.IPartyManagement;
import za.co.liberty.business.request.IGuiRequestManagement;
import za.co.liberty.business.request.IRequestEnquiryManagement;
import za.co.liberty.common.domain.ApplicationContext;
import za.co.liberty.dto.agreement.AgreementDTO;
import za.co.liberty.dto.agreement.AgreementRoleDTO;
import za.co.liberty.dto.agreement.core.CoreConsultantDto;
import za.co.liberty.dto.agreement.core.CoreTransferDto;
import za.co.liberty.dto.agreement.request.RaiseGuiRequestResultDTO;
import za.co.liberty.dto.agreement.request.RequestEnquiryResultDTO;
import za.co.liberty.dto.agreement.request.RequestEnquirySearchDTO;
import za.co.liberty.dto.contracting.ResultAgreementDTO;
import za.co.liberty.dto.contracting.ResultPartyDTO;
import za.co.liberty.dto.party.HierarchyNodeDTO;
import za.co.liberty.dto.party.PartyDTO;
import za.co.liberty.dto.persistence.party.flow.PartyNameAgreementFLO;
import za.co.liberty.dto.userprofiles.ExplicitAgreementDTO;
import za.co.liberty.dto.userprofiles.ISessionUserProfile;
import za.co.liberty.exceptions.data.DataNotFoundException;
import za.co.liberty.exceptions.data.QueryTimeoutException;
import za.co.liberty.exceptions.data.ValidationException;
import za.co.liberty.exceptions.error.request.RequestConfigurationException;
import za.co.liberty.exceptions.error.request.RequestException;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.helpers.lookup.ServiceLocator;
import za.co.liberty.helpers.util.DateUtil;
import za.co.liberty.interfaces.agreements.AgreementKindType;
import za.co.liberty.interfaces.agreements.AgreementStatusType;
import za.co.liberty.interfaces.agreements.IPropertyFLO;
import za.co.liberty.interfaces.agreements.RoleKindType;
import za.co.liberty.interfaces.agreements.requests.PropertyKindType;
import za.co.liberty.interfaces.agreements.requests.RequestDateType;
import za.co.liberty.interfaces.agreements.requests.RequestKindType;
import za.co.liberty.interfaces.agreements.requests.RequestStatusType;
import za.co.liberty.interfaces.core.CoreTransferRequestType;
import za.co.liberty.interfaces.gui.GuiRequestKindType;
import za.co.liberty.interfaces.party.PartyType;
import za.co.liberty.interfaces.persistence.party.flow.IPartyNameAgreementFLO;
import za.co.liberty.persistence.agreement.IAgreementEntityManager;
import za.co.liberty.persistence.party.IPartyEntityManager;
import za.co.liberty.persistence.rating.IRatingEntityManager;
import za.co.liberty.persistence.rating.entity.fastlane.CoreExceptionCodesFLO;

@Stateless
public class CoreTransferGuiController implements ICoreTransferGuiController, Serializable{
	
	private static final long serialVersionUID = 1L;
	
//	@EJB
//	IAgreementManagement agreementManagement;
//	
//	@EJB
//	IPartyManagement partyManagementBean;
//	
//	@EJB
//	IPartyEntityManager partyEntityManager;
//	
//	@EJB 
//	IGuiRequestManagement guiRequestManager;
//	
//	@EJB
//	IAgreementEntityManager agreementEntityManager;
//	
//	@EJB
//	IRatingEntityManager ratingEntityManager;
	
	private static List<CoreTransferRequestType> coreTransferRequestTypeList;

	static final Logger logger = Logger.getLogger(CoreTransferGuiController.class);
	
	static Set<String> excludedBranchCodeSet = null;
	
	public ResultAgreementDTO findAgreementWithConsultantCode(
			long thirteenDigitConsultantCode)
			throws DataNotFoundException, CommunicationException {
//		return agreementManagement.findAgreementWithConsultantCode(thirteenDigitConsultantCode);
		return null;
	}
	
	
	public ResultPartyDTO findPartyIntermediaryWithAgreementNr(
			long agreementNr)
			throws DataNotFoundException, CommunicationException {
//		return partyManagementBean.findPartyIntermediaryWithAgreementNr(agreementNr);
		return null;
	}
	
	public AgreementDTO getAgreementDTOForObjectOID(long id) throws CommunicationException, DataNotFoundException {
//		return agreementManagement.getAgreementWithObjectOID(id);
		return null;
	}
	
	/**
	 * Return a set of branch codes that should be rejected
	 */
	public Set<String> getExcludedBranchCodes() {
		if (excludedBranchCodeSet==null) {
			initialiseCoreExclusions();
		}
		return excludedBranchCodeSet;
	}
	
	private void initialiseCoreExclusions() {
//		List<CoreExceptionCodesFLO> list = ratingEntityManager.getAllCoreExceptionCodes();
//		Set<String> branchSet = new HashSet<String>();
//		
//		for (CoreExceptionCodesFLO f : list) {
//			logger.info("Branch code exclusions :" + f.getCode() + " - " + f.getCodeType());
//			if (f.getCodeType().equalsIgnoreCase("BRANCH_CODE")) {
//				branchSet.add(f.getCode());
//			}
//		}
//		excludedBranchCodeSet = Collections.unmodifiableSet(branchSet);
	}
	
	/**
	 * Return a set of agreement codes for ABSA and FNB
	 */
	public String paysToAgreementCodeSet(CoreTransferDto dto) {
		Collection<AgreementRoleDTO> agreementRole=null;
		
			
//			try{
//				agreementRole=agreementManagement.getAgreementRoleDTOForAgreement(dto.getToAgreementCode(), RoleKindType.PAYSTO);
//			}catch(DataNotFoundException dnfe){
//				return "";
//			}
		
		if(agreementRole==null){
			return "";
		}else{
			for (AgreementRoleDTO roleDTO : agreementRole) {
				return roleDTO.getRolePlayerID().toString();
			}
		return "";
		}
	}
	
	/**
	 * Return a set of agreement codes for ABSA and FNB
	 */
	public Map<String,String> findPriBiblifeAgreementCode() {
		Map<String,String> agreementMap = null;
//		if (agreementMap==null) {
//			List<CoreExceptionCodesFLO> list = ratingEntityManager.getAllCoreExceptionCodes();
//			agreementMap=new HashMap<String,String>();
//			
//			for (CoreExceptionCodesFLO f : list) {
//				logger.info("ABSA and FNB :" + f.getCode() + " - " + f.getCodeType());
//				if (f.getCodeType().equalsIgnoreCase("ABSA") || f.getCodeType().equalsIgnoreCase("FNB")) {
//					agreementMap.put(f.getCodeType(),f.getCode());
//				}
//			}
//		}
		return agreementMap;
	}

	
	/**
	 * Returns a list of all available Request Statusses
	 * 
	 * @return
	 */
	public List<CoreTransferRequestType> getAllRequestStatusTypeList() {
		if (coreTransferRequestTypeList==null) {
			List<CoreTransferRequestType> list = new ArrayList<CoreTransferRequestType>();
			for (CoreTransferRequestType e : CoreTransferRequestType.values()) {
				list.add(e);
			}
			Collections.sort(list, new Comparator<CoreTransferRequestType> () {
				public int compare(CoreTransferRequestType t1, CoreTransferRequestType t2) {
					return t1.getName().compareTo(t2.getName());
				}
			});
			coreTransferRequestTypeList = Collections.unmodifiableList(list);
		}
		return coreTransferRequestTypeList;
	}
	
	
	public Map<String,String> populatePartyNames(List<ResultAgreementDTO> resultList) {

		Set<Long> fullPartyOidSet = new HashSet<Long>((int)(resultList.size()*1.1));
		Set<Long> fullAgreementNrSet = new HashSet<Long>((int)(resultList.size()*1.1));
		
		Map<Long, Long> tempMap = new HashMap<Long, Long>(fullAgreementNrSet.size());
		
		for (ResultAgreementDTO row :resultList) {
				fullPartyOidSet.add(row.getOid());
				fullAgreementNrSet.add(row.getAgreementNumber());
				tempMap.put(row.getOid(), row.getConsultantCode());
		}
	
//		Collection<IPartyNameAgreementFLO> floList = partyEntityManager.findPartyNamesForAgreementsAndPartyOids(
//				fullAgreementNrSet, fullPartyOidSet, PartyNameAgreementFLO.class);
		Map<String, String> nameMap = new HashMap<String, String>(fullAgreementNrSet.size());
//
//		
//		for (IPartyNameAgreementFLO flo : floList) {
//			if (flo.getAgreementNr()!=null) {
//				nameMap.put(String.valueOf(tempMap.get(flo.getAgreementNr())), flo.getName());				
//			} 
//		}
		
		return nameMap;
		
	}
	
	
	
	/**
	 * Raise a new maintain agreement GUI request.
	 * 
	 * @param sessionUserProfile web session user profile
	 * @param partyOid id of the party that the agreement is for
	 * @param coreTransferDto the current agreement DTO
	 * @param previousMaintainAgreementDTO the previous agreement DTO
	 * @return 
	 * @throws ValidationException 
	 */
	public RaiseGuiRequestResultDTO raiseCoreTransferRequest(
			ISessionUserProfile sessionUserProfile,
			Long partyOid,
			CoreTransferDto coreTransferDto,Set<RequestKindType> allowableRequestKinds)
			//,RequestKindType[] requests) 
		throws ValidationException, DataNotFoundException, RequestException {


		
		try {
			ApplicationContext applicationContext = new ApplicationContext();
			applicationContext.setUserid(""+sessionUserProfile.getPartyOid());
			sessionUserProfile.getExplicitAgreements();
			Map <Long,ExplicitAgreementDTO> explicitAgreementDTOMap=sessionUserProfile.getExplicitAgreements();
			//explicitAgreementDTOMap.ge
		for (Long type : explicitAgreementDTOMap.keySet()) {
		
			
		}
			
			ExplicitAgreementDTO explicitAgreementDTO=new ExplicitAgreementDTO();
			explicitAgreementDTO.getExplicitAgreementID();

//			/**
//			 * Raise GUI request
//			 */
//			RaiseGuiRequestResultDTO request = guiRequestManager.raiseGuiRequest(applicationContext, 
//					sessionUserProfile, 
//					coreTransferDto.getFromAgreementCode(), 
//					sessionUserProfile.getPartyOid(), 
//					GuiRequestKindType.CoreTransfer, 
//					allowableRequestKinds.toArray(new RequestKindType[0]), 
//					coreTransferDto, null);
//			return request;
			return null;
		} 
//		catch (RequestConfigurationException e) {
//			throw new CommunicationException(e.getMessage(),e);
//		} catch (RequestException e) {
//			if(e.getCause() instanceof LogicExecutionValidationException){
//				throw new ValidationException(((LogicExecutionException)e.getCause()).getMessage());
//			}else{
//				throw e;
//			}
//		}		
		finally {
			
		}
	}

	/**
	 * Quickly process the file to validate it's contents.  This is done without retrieving DB
	 * data.
	 */
	public void validateFileContents(List<String> lines) throws ValidationException {

		String [] str = null;
		String toConsultant=null;
		String fromConsultant=null;
		int totalCount=0;
		int toConsultantCodeCount=0;
		int fromConsultantCodeCount=0;
			
		Map<String,Boolean> toConsultantCodeMap=new HashMap<String,Boolean>();
		Map<String,Boolean> fromConsultantCodeMap=new HashMap<String,Boolean>();
		for (String line : lines ) {
			totalCount++;
			
			str = splitLine(line, 5);
			
			if(str[0]!=null ){
				fromConsultant=str[0].trim();
			}else{
				fromConsultant="";
			}
			if(fromConsultantCodeMap.get(fromConsultant)==null && ! fromConsultant.equals("")){
				fromConsultantCodeCount++;
				fromConsultantCodeMap.put(fromConsultant, true);
			}
			
			if(str[1]!=null ){
					toConsultant=str[1].trim();
			}else{
					toConsultant="";
			}
			if(toConsultantCodeMap.get(toConsultant)==null && ! toConsultant.equals("")){
				toConsultantCodeCount++;
				toConsultantCodeMap.put(toConsultant, true);
			}
		} 
		
//		}catch (Exception e) {
//			e.printStackTrace();
//			return "Unable to process, Please check the file";
//		}
		if(totalCount==0)
			throw new ValidationException("Please add record/records to transfer");
		if(totalCount>200)
			throw new ValidationException("System allows 200 recods max. File has " 
					+ totalCount+ " records to process. Please split the file");
		if(toConsultantCodeCount>10)
			throw new ValidationException("System allows 10 To Consultant max. File has "
					+toConsultantCodeCount+ " To Consultants. Please split the file");
		if(fromConsultantCodeCount>1)
			throw new ValidationException("System allows 1 From Consultant max. File has "
					+fromConsultantCodeCount+ " From Consultants. Please split the file");
		
	}
	
	/**
	 * This method reads uploaded file and load data into page model   
	 * */	
	public void convertFileLinesToDTOList(List<String> lines, 
			List<CoreTransferDto> resultList,
			Map<Long, CoreConsultantDto> conscodeCoreMap)  throws ValidationException {
		
		ResultAgreementDTO fromAgreementDTO = null;
		ResultAgreementDTO toAgreementDTO = null;
		ResultPartyDTO fromPartyDTO = null;
		ResultPartyDTO toPartyDTO = null;
		CoreTransferDto coreTransferDto = null;
		
		resultList.removeAll(resultList);
		
//		List<ResultAgreementDTO> resultAgreementList=new ArrayList<ResultAgreementDTO>();
		boolean flag = true;
		AgreementDTO agreementDTO = null;
//		Map<String, String> oidMap = new HashMap<String, String>();
//		Map<Long, ResultAgreementDTO> conscodeResultMap = new HashMap<Long, ResultAgreementDTO>((int) (lines.size()*1.1));
		Set<Long> consultantCodeSet = new HashSet<Long>();
		
		//pageModel.getCoreTransferDto().removeAll(pageModel.getCoreTransferDto());
		
		long startTime = System.currentTimeMillis();
		logger.info("Starting processing of file with "+lines.size()+" lines");

		String [] str = null;
		Map<String,CoreTransferDto> coreMap=new HashMap<String,CoreTransferDto>();
		int count=1;
		for (String line : lines) {
			str = splitLine(line, 4);
			coreTransferDto = new CoreTransferDto();
			coreTransferDto.setRowCount(count++);
			
			String lineFromCode = str[0];
			String lineToCode = str[1];
			String lineContractNr = str[2];
			String lineAdvisoryFee = str[3];
			
			Long fromCode = null;
			Long toCode = null;
			String contractNumber = "";
			String advisoryfee = "";
			
			if (lineFromCode!=null){
				try {
					fromCode = Long.parseLong(str[0].trim());	
				} catch (NumberFormatException n) {
					// Ignore
				}
			}
					
			/* This block search first valid from consultant code */
//				if (flag && !(currCode == null) {
//					fromCode = currCode;
//					try {
//						fromAgreementDTO = findAgreementWithConsultantCode(fromCode);// this service require to find agreement number					
//						resultAgreementList.add(fromAgreementDTO);
//						oidMap.put(fromAgreementDTO.getConsultantCodeFormatted(),String.valueOf( fromAgreementDTO.getOid()));
//						// this service require to find consultant name
//						flag = false;
//					} catch (DataNotFoundException dnfe) {
//						flag = true;
//						coreTransferDto.setFromConsultantCode(fromCode);
//						coreTransferDto.setFromConsultantName("");
//						coreTransferDto.setFromConsultantStatus("");
//						coreTransferDto.setFromAgreementCode(null);
//					}
//				}
//				if (!flag) {
//					if (fromAgreementDTO == null) {// validation for "From
//						// consultant code" not found
//						coreTransferDto.setFromConsultantCode(null);
//						coreTransferDto.setFromConsultantName("");
//						coreTransferDto.setFromConsultantStatus("");
//						coreTransferDto.setFromAgreementCode(null);
//					} else if (!fromCode.equals(currCode)) { // check
//						// whether "From consultant code" in current row is similar to first
//						// valid from consultant code 
//						coreTransferDto.setFromConsultantCode(currCode);
//						coreTransferDto.setFromConsultantName("");
//						coreTransferDto.setFromConsultantStatus("");
//						coreTransferDto.setFromAgreementCode("");
//					} else {
//						coreTransferDto.setFromConsultantCode(fromCode);
//						/*coreTransferDto.setFromConsultantName(fromPartyDTO
//								.getName());*/
//						coreTransferDto
//								.setFromConsultantStatus(fromAgreementDTO
//										.getAgreementStatus());
//						coreTransferDto.setFromAgreementCode(String.valueOf(fromAgreementDTO.getOid()));
//					}
//				}
			
			if (lineToCode!=null){
				try {
					toCode = Long.parseLong(lineToCode);
				} catch (NumberFormatException n) {
					// Ignore
				}
			}

				
//				if (!(toCode == null || toCode.equals("") || toCode
//						.equals(" "))) {
//					try {
//						CoreTransferDto tempDto=coreMap.get(toCode);
//						if(tempDto==null){
//						
//							toAgreementDTO = findAgreementWithConsultantCode(Long.parseLong(toCode));
//							resultAgreementList.add(toAgreementDTO);
//							oidMap.put(toAgreementDTO.getConsultantCodeFormatted(),String.valueOf( toAgreementDTO.getOid()));
//							
//							toPartyDTO = findPartyIntermediaryWithAgreementNr(toAgreementDTO
//											.getAgreementNumber());
//							agreementDTO = getAgreementDTOForObjectOID(toAgreementDTO
//											.getAgreementNumber());
//							
//							coreTransferDto.setToConsultantCode(toCode);
//
//							coreTransferDto.setToConsultantStatusReason(agreementDTO.getStatusReason());
//							coreTransferDto
//									.setToConsultantStatus(toAgreementDTO
//											.getAgreementStatus());
//							coreTransferDto.setToConsultantImplicitStatus(getToConsultantStatus(toPartyDTO.getJobTitle(),toAgreementDTO.getAgreementStatus(),agreementDTO
//									.getStatusReason(),String.valueOf(toAgreementDTO.getConsultantCode())));
//							
//							coreTransferDto.setToAgreementCode(String.valueOf(toAgreementDTO.getOid()));
//							
//							coreMap.put(toCode, coreTransferDto);
//						}else{
//							coreTransferDto.setToConsultantCode(toCode);
//							coreTransferDto.setToConsultantStatusReason(tempDto.getToConsultantStatusReason());
//							coreTransferDto.setToConsultantStatus(tempDto.getToConsultantStatus());
//							coreTransferDto.setToConsultantImplicitStatus(tempDto.getToConsultantImplicitStatus());
//							coreTransferDto.setToAgreementCode(tempDto.getToAgreementCode());
//						}
//					} catch (DataNotFoundException dnfe) {
//						coreTransferDto.setToConsultantCode(toCode);
//						coreTransferDto.setToConsultantName("");
//						coreTransferDto.setToConsultantStatus("");
//						coreTransferDto.setToConsultantStatusReason("");
//						coreTransferDto.setToAgreementCode(0L);
//					}
//				}else{
//					if(str[1]!=null) {
//						toCode = str[1].trim();
//					}else{
//						toCode="";
//					}
//					coreTransferDto.setToConsultantCode(toCode);
//				}
				
			if (lineContractNr!=null ) {
				contractNumber = lineContractNr;
			}
			
			if (lineAdvisoryFee!=null) {
				advisoryfee = lineAdvisoryFee.trim();
			}
			coreTransferDto.setFromConsultantCode(fromCode);
			coreTransferDto.setToConsultantCode(toCode);
			coreTransferDto.setContractNumber(contractNumber);
		
			if (advisoryfee.equalsIgnoreCase("n"))
				coreTransferDto.setAdvisoryFeeIndicator(false);
			else if (advisoryfee.equalsIgnoreCase("y"))
				coreTransferDto.setAdvisoryFeeIndicator(true);
			else
				coreTransferDto.setAdvisoryFeeIndicator(false);
			
			consultantCodeSet.add(toCode);
			consultantCodeSet.add(fromCode);
			resultList.add(coreTransferDto);
			//pageModel.getCoreTransferDto().add(coreTransferDto);
		}
		
		logger.info("  - Processed lines in file " + (System.currentTimeMillis()-startTime) + " millis");
		startTime = System.currentTimeMillis();
		
		/*
		 * Now Retrieve the Agreement Nr's for each consultant code
		 */
//		Map<Long, CoreConsultantDto> conscodeCoreMap = new HashMap<Long, CoreConsultantDto>();
		for (Long code : consultantCodeSet) {
			if (code == null) {
				continue;
			}
			// Get SRS Agreement nr
			ResultAgreementDTO agreementDto = null;
			try {
				agreementDto = findAgreementWithConsultantCode(code);
			} catch (DataNotFoundException e) {
				// Continue, null behaviour is sufficient
			}
			if (agreementDto==null) {
				continue;
			}
			
			// Get the Party data
			ResultPartyDTO partyDto = null;
			try {
				partyDto = findPartyIntermediaryWithAgreementNr(agreementDto.getOid());
			} catch (DataNotFoundException e) {
				// This isn't possible, if it does happen we have big problems
				throw new CommunicationException(e);
			}
			CoreConsultantDto coreConsDto = getCoreConsultantDto(agreementDto, partyDto);
			conscodeCoreMap.put(code, coreConsDto);
		}
		
		logger.info("  - Completed finding consultants, found " + conscodeCoreMap.size() 
				+ ", took " + (System.currentTimeMillis()-startTime) + " millis");
		startTime = System.currentTimeMillis();
		
		/*
		 * Now fill the data on all the from consultants
		 */
		for (CoreTransferDto dto :resultList) {
			CoreConsultantDto coreDto = conscodeCoreMap.get(dto.getFromConsultantCode());
			if (coreDto!=null) {
				dto.setFromAgreementCode(coreDto.getAgreementNumber());
				dto.setFromConsultantName(coreDto.getPartyName());
				dto.setFromConsultantStatus(coreDto.getAgreementStatus().getDescription());
			}
			
			coreDto = conscodeCoreMap.get(dto.getToConsultantCode());
			if (coreDto!=null) {
				dto.setToAgreementCode(coreDto.getAgreementNumber());
				dto.setToConsultantName(coreDto.getPartyName());
				dto.setToConsultantStatus(coreDto.getAgreementStatus().getDescription());
				dto.setToConsultantStatusReason(coreDto.getAgreementStatusReason());
				if (dto.getToConsultantStatusReason()!=null && dto.getToConsultantStatusReason().length()>0) {
					dto.setToConsultantStatus(dto.getToConsultantStatus() + " - " + dto.getToConsultantStatusReason());
				}
				dto.setToConsultantImplicitStatus(getToConsultantStatus(coreDto));
			}
		}
		
		logger.info("  - Completed filling data on lines took " + (System.currentTimeMillis()-startTime) + " millis");

	}
	
	/**
	 * Retrieve the list of outstanding requests
	 * 
	 * @param fromAgreementCode
	 * @param requestKindType
	 * @return
	 * @throws QueryTimeoutException 
	 * @throws RequestException 
	 */
	public RequestEnquiryResultDTO getOutstandingRequests(Long fromAgreementCode,
			RequestKindType  requestKindType ) throws QueryTimeoutException, RequestException {
		
		RequestEnquiryResultDTO requestEnquiryList=null;
		try{
			IRequestEnquiryManagement requestManager = ServiceLocator.lookupService(IRequestEnquiryManagement.class);		
			List<Long> agmtIds= new ArrayList<Long>(1);
			agmtIds.add(fromAgreementCode);		
			
			Date startDate = DateUtil.getInstance().addMonths(new Date(), -3);
			
			List<RequestKindType> requestKinds = new ArrayList<RequestKindType>(1);
			requestKinds.add(requestKindType);
			
			RequestEnquirySearchDTO searchDto = new RequestEnquirySearchDTO(agmtIds, 
					requestKinds,RequestDateType.REQUESTED,startDate,new Date(), RequestStatusType.REQUIRES_AUTHORISATION); 	
						
			List<PropertyKindType> propertiesToInclude = new ArrayList<PropertyKindType>();
			propertiesToInclude.add(PropertyKindType.ToConsultantCode);
			propertiesToInclude.add(PropertyKindType.FromConsultantCode);
			propertiesToInclude.add(PropertyKindType.ContractNumber);
			
			requestEnquiryList = requestManager.findRequests( 
					searchDto,
					propertiesToInclude);
		
		} catch (NamingException e) {
			throw new CommunicationException(e);
		} 
		return requestEnquiryList;
	}
	
	/**
	 * Get the consultant code DTO.
	 * @throws DataNotFoundException 
	 * @throws CommunicationException 
	 */
	@SuppressWarnings("unchecked")
	public CoreConsultantDto getCoreConsultantDto(long agreementNr) throws CommunicationException, DataNotFoundException {
		// Get SRS Agreement nr
//		ResultAgreementDTO agreementDto = null;
//		agreementDto = agreementManagement.findAgreementWithSRSAgreementNr(agreementNr);
//	
//		// Get the Party data
//		ResultPartyDTO partyDto = null;
//		partyDto = findPartyIntermediaryWithAgreementNr(agreementDto.getOid());
//
//		return getCoreConsultantDto(agreementDto, partyDto);
		return null;
	}
	/**
	 * Get the consultant code DTO.
	 */
	@SuppressWarnings("unchecked")
	public CoreConsultantDto getCoreConsultantDto(
			ResultAgreementDTO agreementDto, ResultPartyDTO partyDto) {

		/*
		 * Set the Agreement data
		 */
		CoreConsultantDto dto = new CoreConsultantDto();
		if (agreementDto!=null) {
			dto.setAgreementKind(AgreementKindType.getAgreementKindType(
					agreementDto.getAgreementDivision().getKind()));
			dto.setAgreementNumber(agreementDto.getOid());
			dto.setConsultantCode(agreementDto.getConsultantCode());
			dto.setAgreementStatus(agreementDto.getAgreementStatusType());
			
			// Set the status reason if Retired
//			if (dto.getAgreementStatus().equals(AgreementStatusType.RETIRED)) {
//				IPropertyFLO<String> obj = (IPropertyFLO<String>) agreementManagement.getAgreementPropertyFLOOfKind(agreementDto.getOid(), PropertyKindType.StatusReason);
//				dto.setAgreementStatusReason((obj!=null)?obj.getValue():"");
//			} else {
//				dto.setAgreementStatusReason("");
//			}		
//			
//			IPropertyFLO<Long> obj = (IPropertyFLO<Long>) agreementManagement.getAgreementPropertyFLOOfKind(agreementDto.getOid(), PropertyKindType.CompassCode);
//			dto.setCompassCode((obj!=null)?obj.getValue():null);
//			
//			// Branch code
//			if (agreementDto.getHasHomePartyOid()!=null) {
//				try {
//					PartyDTO homeDto = partyManagementBean.getPartyDTOWithObjectOid(agreementDto.getHasHomePartyOid());		
//					if(homeDto instanceof HierarchyNodeDTO){
//						dto.setBranchCode(((HierarchyNodeDTO)homeDto).getBranchCode());
//					}
//				} catch (DataNotFoundException e) {
//					// Unable to find the branch code
//					logger.warn("Unable to retrieve the branch code for branch " + agreementDto.getHasHomePartyOid()
//							+ " and agreement " + dto.getAgreementNumber());
//				}
//			}
		}
				
		/*
		 * Set the party 
		 */
		if (partyDto!=null) {
			dto.setPartyName(partyDto.getName());
			dto.setOrganisation(PartyType.ORGANISATION.getType()==partyDto.getTypeOid());
		}
		return dto;
	}
	
	
	/**
	 * Get the consultant status
	 * 
	 * @param coreDto
	 * @return
	 */
	public String getToConsultantStatus(CoreConsultantDto coreDto){
//		String status="O";
//		String branch = "001";
//		AgreementStatusType statusType = coreDto.getAgreementStatus();
//		String statusReason = (coreDto.getAgreementStatusReason()!=null) ? coreDto.getAgreementStatusReason() : "";
//		
//		if (!(statusType == AgreementStatusType.ACTIVE || statusType == AgreementStatusType.CLOSED_TO_BN 
//				|| statusType == AgreementStatusType.BROKER_ACTIVE || statusType == AgreementStatusType.BROKER_CLOSED_TO_BN
//				|| (statusType == AgreementStatusType.BROKER_RETIRED && statusReason.equalsIgnoreCase("Retired active"))
//				|| (statusType == AgreementStatusType.RETIRED && statusReason.equalsIgnoreCase("Retired active")))) {
//		
////			if((brnach.equals("000")||brnach.equals("020")||brnach.equals("029")||brnach.equals("200")||brnach.equals("399")||brnach.equals("798")||brnach.equals("799"))){
//			
//			if (coreDto.getAgreementKind()==AgreementKindType.BROKER) {
//				//TODO some branch code crap
//				status="C";
//			} else {
//				status="C";
//			}
//			
//		}
//		
//		return status;
		// jzb0608 - Discussed with Denise.  This isn't really used so we are just forcing a C for now.
		return "C";
	}
	
	protected boolean isConsultantCode(String code) {
		try {
			Long.parseLong(code);
		} catch (NumberFormatException ex) {
			return false;
		}
		return true;
	}
	
	/**
	 * Split a line that is delimited with ",".  This ensures that subsequent delimiters
	 * are treated as empty strings.
	 * 
	 * @param line
	 * @param size
	 * @return
	 */
	public String[] splitLine(String line, int size) {
		StringTokenizer tok = new StringTokenizer(line, ",", true);
		String[] array = new String[size];
		
		boolean delimiter = true;
		int i = 0;
		
		// Every alternate will be a delimiter, this is to cater for blanks
		while (tok.hasMoreElements() && i<(size)) {
			String s = tok.nextToken();
			if (s!=null && s.equals(",")) {
				if (delimiter) {
					array[i++]="";
				}
				delimiter = true;
				continue;
			}
			delimiter = false;
			array[i++]=s;
		}
		if (delimiter && i<(size)) {
			array[i++]="";
		}
		
		return array;
	}
	
}
