	package za.co.liberty.web.pages.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.naming.NamingException;

import org.apache.wicket.markup.html.panel.FeedbackPanel;

import za.co.liberty.business.guicontrollers.core.ICoreTransferGuiController;
import za.co.liberty.dto.agreement.core.CoreConsultantDto;
import za.co.liberty.dto.agreement.core.CoreTransferDto;
import za.co.liberty.dto.agreement.request.RequestEnquiryResultDTO;
import za.co.liberty.dto.agreement.request.RequestEnquiryRowDTO;
import za.co.liberty.exceptions.data.QueryTimeoutException;
import za.co.liberty.exceptions.error.request.RequestException;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.helpers.lookup.ServiceLocator;
import za.co.liberty.interfaces.agreements.AgreementKindType;
import za.co.liberty.interfaces.agreements.AgreementStatusType;
import za.co.liberty.interfaces.agreements.requests.PropertyKindType;
import za.co.liberty.interfaces.agreements.requests.RequestKindType;
import za.co.liberty.interfaces.persistence.agreement.request.IRequestEnquiryRow;
import za.co.liberty.web.pages.core.model.CoreTransferPageModel;

public class CoreHelper implements Serializable{

	private static final long serialVersionUID = 1L;
	
	public static int pageSize=50;
	
	private Set<String> branchCodeExclusionSet;
	public static final Set<AgreementKindType> AGREEMENT_KIND_ORGANISATION_SET;
	private HashMap<String,String> agreementCodeMap=null;
	private String paysToAgreement;

	
	static {
		Set<AgreementKindType> tmpSet = new HashSet<AgreementKindType>();
		tmpSet.add(AgreementKindType.FRANCHISE);
		tmpSet.add(AgreementKindType.AGENT);
		tmpSet.add(AgreementKindType.BROKER);
		AGREEMENT_KIND_ORGANISATION_SET = Collections.unmodifiableSet(tmpSet);
	}
	
	/**
	 * Validate the segment grid
	 * 
	 * @param pageModel
	 * @param feedbackPanel
	 * @return
	 */
	protected boolean validateSegmentGrid(CoreTransferPageModel pageModel,
			FeedbackPanel feedbackPanel) {
		int segmentRowCount = 0;
		boolean segmentTransferFlag = true;
		Long fromConsultantCode = null;
		Long fromAgreementCode = null;
		Long tempCode;
		String toConsultantStatus = null;
		String toConsultantStatusReason = null;

		RequestEnquiryResultDTO requestEnquiryResultDTO=null;
		List<CoreTransferDto> coreTransferDtoList = pageModel
				.getCoreTransferDto();
		feedbackPanel.setEnabled(true);
		
		if(pageModel.getTransferType()==null || pageModel.getTransferType().equals("")||pageModel.getTransferType().equals(" ")){
			feedbackPanel.error("Please select Request Kind");
			segmentTransferFlag = false;
		}
		
		//This loop finds first non empty from consultant code. All other from consultant code will be compared with this code for duplication 
		for (CoreTransferDto dto : coreTransferDtoList) {
			fromConsultantCode = dto.getFromConsultantCode();
			fromAgreementCode=dto.getFromAgreementCode();
			if (fromConsultantCode != null && fromAgreementCode !=null
//					&& isConsultantCode(fromConsultantCode)
					) {
				try {
					requestEnquiryResultDTO	=getGUIController().getOutstandingRequests(fromAgreementCode,
							RequestKindType.ProcessSegmentedContractTransfer);
				} catch (QueryTimeoutException e) {
					feedbackPanel.error("A background query timed out, please try again");
					return false;
				} catch (RequestException e) {
					feedbackPanel.error("A background query had an error - " + e.getMessage());
					return false;
				}
				break;
			}else if(coreTransferDtoList.size()==1 && fromConsultantCode == null && dto.getToConsultantCode() ==null){
				feedbackPanel.error("Please add record/records to transfer");
				return false;
			}/*else{
				fromConsultantCode=null;
				fromAgreementCode=null;
			}*/
		}
		
		if(fromConsultantCode!=null && fromAgreementCode==null){
			feedbackPanel.error("From consultant is invalid");
			return false;
		}else if(fromConsultantCode==null ){
			feedbackPanel.error("From consultant is missing ");
			return false;
		}

		segmentRowCount = 0;
		Set set = new HashSet();
		boolean code11Error = false;
		CoreConsultantDto fromCoreConsultantDto = pageModel.getConsultantMap().get(fromConsultantCode);
		if (fromCoreConsultantDto.isOrganisation() && AGREEMENT_KIND_ORGANISATION_SET.contains(fromCoreConsultantDto.getAgreementKind())) {
			code11Error = true;	
		}
		
		/*
		 *	Iterating each and every row in grid for validations 
		 */
		for (CoreTransferDto dto : coreTransferDtoList) {
//			checkConsultantCodeLength(dto);
			tempCode = dto.getFromConsultantCode();
			segmentRowCount++;// incrementing row count
			dto.setRowStatus(true);
			
			dto.setErrorMsg(null);
			
			//Validation for empty "From consultant code"
			if (tempCode == null || tempCode.equals(0L)) {
				segmentTransferFlag = false;
				dto.setRowStatus(false);		
				dto.setErrorMsg("1");
			
				//Validation for numeric and length of "From consultant code"
			} else if (tempCode!=null && !tempCode.equals(fromConsultantCode)) {
				segmentTransferFlag = false;
				dto.setRowStatus(false);
				dto.setErrorMsg(appendErrorCode(dto.getErrorMsg(), "2"));
			}
			
			tempCode = dto.getToConsultantCode();
			
			//Validation for empty "To consultant code"
			if (tempCode == null ||  tempCode==0L) {
				segmentTransferFlag = false;
				dto.setRowStatus(false);
				dto.setErrorMsg(appendErrorCode(dto.getErrorMsg(), "3"));
				
				//Validation for numeric and length of "To consultant code"				
			} else if (tempCode==null || tempCode>9999999999999L ||pageModel.getConsultantMap().get(dto.getToConsultantCode())==null) {
				segmentTransferFlag = false;
				dto.setRowStatus(false);
				dto.setErrorMsg(appendErrorCode(dto.getErrorMsg(), "4"));
			} 
			//Validation for From and To consultant code wiil not be same	
			else if (fromConsultantCode != null
					&& fromConsultantCode.equals(tempCode)) {
				segmentTransferFlag = false;
				dto.setRowStatus(false);
				dto.setErrorMsg(appendErrorCode(dto.getErrorMsg(), "5"));
			}
			
			// Validate to consultant status
			CoreConsultantDto toCoreConsultantDto = pageModel.getConsultantMap().get(dto.getToConsultantCode());
			if (toCoreConsultantDto!=null &&! isConsultantActiveStatus(toCoreConsultantDto)) {
				dto.setErrorMsg(appendErrorCode(dto.getErrorMsg(), "6"));
				dto.setRowStatus(false);
				segmentTransferFlag = false;
			}
					
			//Validation for empty To "Contract code"
			if (dto.getContractNumber() == null
					|| dto.getContractNumber().equals("")) {
				segmentTransferFlag = false;
				dto.setRowStatus(false);
				dto.setErrorMsg(appendErrorCode(dto.getErrorMsg(), "7"));
				//Validation for duplication of "Contract number"				
			} else if (set.contains(dto.getContractNumber())) {
				segmentTransferFlag = false;
				dto.setRowStatus(false);
				dto.setErrorMsg(appendErrorCode(dto.getErrorMsg(), "8"));
			}else if (!isConsultantCode(dto.getContractNumber())) {			
				dto.setErrorMsg(appendErrorCode(dto.getErrorMsg(), "9"));
				
				dto.setRowStatus(false);
			}
			
			// Error 11 - From consultant may not be an organisation (for specified channel)
			if(code11Error){
				dto.setErrorMsg(appendErrorCode(dto.getErrorMsg(), "11"));
				dto.setRowStatus(false);
				segmentTransferFlag = false;
			}
			
			// Error 12 - To consultant may not be an organisation (for specified channel)
			if (toCoreConsultantDto != null && toCoreConsultantDto.isOrganisation() 
					&& AGREEMENT_KIND_ORGANISATION_SET.contains(fromCoreConsultantDto.getAgreementKind())) {
				dto.setErrorMsg(appendErrorCode(dto.getErrorMsg(), "12"));
				dto.setRowStatus(false);
				segmentTransferFlag = false;
			}
			
			if (toCoreConsultantDto != null && toCoreConsultantDto.getBranchCode()!=null 
					&& isBranchCodeExcluded(toCoreConsultantDto.getBranchCode())) {
				dto.setErrorMsg(appendErrorCode(dto.getErrorMsg(), "13"));
				dto.setRowStatus(false);
				segmentTransferFlag = false;
			}
			set.add(dto.getContractNumber());
			Boolean tempBool = dto.getAdvisoryFeeIndicator();

		}
		
		// Error 10   
		if(!validateSegmentOutStandingRequest(requestEnquiryResultDTO,coreTransferDtoList,feedbackPanel)){
			segmentTransferFlag = false;
		}
		
		// Check bookLevelTransfer
		if (fromAgreementCode !=null) {
			try {
				requestEnquiryResultDTO	=getGUIController().getOutstandingRequests(fromAgreementCode,
					RequestKindType.ProcessBookLevelTransfer);
			} catch (QueryTimeoutException e) {
				feedbackPanel.error("A background query timed out, please try again");
				return false;
			} catch (RequestException e) {
				feedbackPanel.error("A background query had an error - " + e.getMessage());
				return false;
			}
		}
		if(requestEnquiryResultDTO.getResultList()!=null && requestEnquiryResultDTO.getResultList().size()!=0){
			feedbackPanel.error("There is an outstanding request[Process Book Transfer] needing authorisation");
			segmentTransferFlag = false;
		}
		
		feedbackPanel.anyErrorMessage();
		feedbackPanel.getFeedbackMessages();
		
		return segmentTransferFlag;
	}

	/**
	 * Check if this is an excluded branch code.
	 * 
	 * @param consDto
	 * @return
	 */
	public boolean isBranchCodeExcluded(CoreConsultantDto consDto) {
		return (consDto==null || isBranchCodeExcluded(consDto.getBranchCode()));
	}
	
	/**
	 * Returns true if the branch code is in the exclusion list
	 * @param branchCode
	 * @return
	 */
	public boolean isBranchCodeExcluded(String branchCode) {
		if (branchCodeExclusionSet==null) {
			branchCodeExclusionSet = getGUIController().getExcludedBranchCodes();
		}
		return branchCodeExclusionSet.contains(branchCode);
	}

	/**
	 * Append an error message
	 * @param msg
	 * @param error
	 * @return
	 */
	protected String appendErrorCode(String msg, String error) {
		if (msg==null||msg.length()==0) {
			return error;
		}
		return msg+", "+error;
	}

	protected boolean isConsultantCode(String code) {
		/*if (code.length() != 13)
			return false;*/
		try {
			Long.parseLong(code);
		} catch (NumberFormatException ex) {
			return false;
		}
		return true;
	}

	/**
	 * Validate the contracting grid
	 * 
	 * @param pageModel
	 * @param feedbackPanel
	 * @return
	 */
	protected boolean validateContractGrid(CoreTransferPageModel pageModel,
			FeedbackPanel feedbackPanel) {
		int contractRowCount = 0;
		boolean contractTransferFlag = true;
		Long fromConsultantCode = null;
		Long fromAgreementCode = null;
		Long tempCode;
		String toConsultantStatus = null;
		String toConsultantStatusReason = null;

		RequestEnquiryResultDTO requestEnquiryResultDTO=null;		
		List<CoreTransferDto> coreTransferDtoList = pageModel
				.getCoreTransferDto();
		contractRowCount = 0;
		Set<String> contractSet = new HashSet<String>();
		Set<Long> booklevelRequestSet = new HashSet<Long>();
		Map<Long, RequestEnquiryResultDTO> segmentEnquiryMap = new HashMap<Long, RequestEnquiryResultDTO>(); 
		
		//Iterarting each and every row in grid for validations		
		for (CoreTransferDto dto : coreTransferDtoList) {
			contractRowCount++;

			tempCode = dto.getFromConsultantCode();
			dto.setErrorMsg(null);
			dto.setRowStatus(true);
			
//			Validation for empty "From Consultant code"
			if (tempCode == null || tempCode==0L) {
				contractTransferFlag = false;
				dto.setErrorMsg("1");
				dto.setRowStatus(false);
				//continue;
			} 
//			Validation for empty "To Consultant code"			
			tempCode = dto.getToConsultantCode();
			if (tempCode == null || tempCode==0L) {
				contractTransferFlag = false;
				dto.setErrorMsg(appendErrorCode(dto.getErrorMsg(), "2"));
				dto.setRowStatus(false);
			}  else if (dto.getFromConsultantCode()!=null && dto.getFromConsultantCode().equals(tempCode)) {
				contractTransferFlag = false;
				dto.setErrorMsg(appendErrorCode(dto.getErrorMsg(), "3"));
				dto.setRowStatus(false);
			}
			
			// Validate to consultant status
			CoreConsultantDto toCoreConsultantDto = pageModel.getConsultantMap().get(dto.getToConsultantCode());
			if (toCoreConsultantDto != null&& !isConsultantActiveStatus(toCoreConsultantDto)) {
				dto.setErrorMsg(appendErrorCode(dto.getErrorMsg(), "4"));
				dto.setRowStatus(false);
				contractTransferFlag = false;
			}

			
			// Validation for empty "Contract number"
			if (dto.getContractNumber() == null
					|| dto.getContractNumber().equals("")) {
				contractTransferFlag = false;
				dto.setErrorMsg(appendErrorCode(dto.getErrorMsg(), "5"));
				dto.setRowStatus(false);
				
//				Validation for duplicate "Contract number"				
			} else if (contractSet.contains(dto.getContractNumber())) {
				contractTransferFlag = false;
				dto.setErrorMsg(appendErrorCode(dto.getErrorMsg(), "6"));
				dto.setRowStatus(false);
			}
			else if (!isConsultantCode(dto.getContractNumber())) {
				contractTransferFlag = false;
				dto.setErrorMsg(appendErrorCode(dto.getErrorMsg(), "7"));
				dto.setRowStatus(false);
			}
			
			if (toCoreConsultantDto != null && toCoreConsultantDto.getBranchCode()!=null 
					&& isBranchCodeExcluded(toCoreConsultantDto.getBranchCode())) {
				dto.setErrorMsg(appendErrorCode(dto.getErrorMsg(), "10"));
				dto.setRowStatus(false);
				contractTransferFlag = false;
			}
			contractSet.add(dto.getContractNumber());
			
			if (dto!=null && dto.getFromAgreementCode() !=null) {

				// Check if a book level transfer is on this code
				if (!booklevelRequestSet.contains(dto.getFromAgreementCode())) {
					try {
						requestEnquiryResultDTO	=getGUIController().getOutstandingRequests(dto.getFromAgreementCode(),
							RequestKindType.ProcessBookLevelTransfer);
					} catch (QueryTimeoutException e) {
						feedbackPanel.error("A background query timed out, please try again");
						return false;
					} catch (RequestException e) {
						feedbackPanel.error("A background query had an error - " + e.getMessage());
						return false;
					}
					if (requestEnquiryResultDTO!=null && requestEnquiryResultDTO.getResultList()!=null && requestEnquiryResultDTO.getResultList().size()>0) {
						booklevelRequestSet.add(dto.getFromAgreementCode());
					}
				}
				
				if(booklevelRequestSet.contains(dto.getFromAgreementCode())) {
					dto.setRowStatus(false);			
					contractTransferFlag = false;
					dto.setErrorMsg(appendErrorCode(dto.getErrorMsg(), "8"));
				}
				
				// Validate Other transfers
				requestEnquiryResultDTO = segmentEnquiryMap.get(dto.getFromAgreementCode());
				
				if (requestEnquiryResultDTO==null) {
					try {
						requestEnquiryResultDTO=getGUIController().getOutstandingRequests(dto.getFromAgreementCode()
								,RequestKindType.ProcessSegmentedContractTransfer);
					} catch (QueryTimeoutException e) {
						feedbackPanel.error("A background query timed out, please try again");
						return false;
					} catch (RequestException e) {
						feedbackPanel.error("A background query had an error - " + e.getMessage());
						return false;
					}
					segmentEnquiryMap.put(dto.getFromAgreementCode(),requestEnquiryResultDTO);
				}
				
				/*validateContractOutStandingRequest(requestEnquiryResultDTO,dto);*/
				if(requestEnquiryResultDTO!=null && !validateContractOutStandingRequest(requestEnquiryResultDTO,dto)){	
					dto.setErrorMsg(appendErrorCode(dto.getErrorMsg(), "9"));
					dto.setRowStatus(false);
					contractTransferFlag = false;
				}
			}
			tempCode=dto.getFromConsultantCode();
			if(! (tempCode == null)){
			tempCode=dto.getFromAgreementCode();
			//Validation for empty "From consultant code not found"
			if (tempCode == null || tempCode.equals(0L)) {
				contractTransferFlag = false;
				dto.setRowStatus(false);		
				dto.setErrorMsg(appendErrorCode(dto.getErrorMsg(), "11"));
			
				//Validation for numeric and length of "From consultant code"
			}
			}
			
			if(dto.getPriBibLife()!=null && !isConsultantCode(dto.getPriBibLife())){
				contractTransferFlag = false;
				dto.setErrorMsg(appendErrorCode(dto.getErrorMsg(), "15"));
				dto.setRowStatus(false);
			}else{
				if(dto.getToAgreementCode()!=null){
				
					findPaysToAgreementCode(dto);
					findPriBiblifeAgreementCode();
					
					if (!paysToAgreement.equals("") && paysToAgreement.equals(agreementCodeMap.get("ABSA")) &&
							(dto.getPriBibLife()==null ||(dto.getPriBibLife()!=null && dto.getPriBibLife().toString().length()!=6))){
						dto.setErrorMsg(appendErrorCode(dto.getErrorMsg(), "12"));
						dto.setRowStatus(false);
						contractTransferFlag = false;
					}
					
					if (!paysToAgreement.equals("") && paysToAgreement.equals(agreementCodeMap.get("FNB")) &&
							(dto.getPriBibLife()==null ||(dto.getPriBibLife()!=null && dto.getPriBibLife().toString().length()!=14))) {
						dto.setErrorMsg(appendErrorCode(dto.getErrorMsg(), "13"));
						dto.setRowStatus(false);
						contractTransferFlag = false;
					}
					if ((!paysToAgreement.equals("443") && !paysToAgreement.equals("2211"))  && (dto.getPriBibLife()!=null && dto.getPriBibLife().length()>0) ) {
						dto.setErrorMsg(appendErrorCode(dto.getErrorMsg(), "14"));
						dto.setRowStatus(false);
						contractTransferFlag = false;
					}
				}

			}
		}	
		return contractTransferFlag;
	}
	
	private void findPriBiblifeAgreementCode() {
			if (agreementCodeMap==null) {
				agreementCodeMap =(HashMap) getGUIController().findPriBiblifeAgreementCode();
			}
	}
		

	private void findPaysToAgreementCode(CoreTransferDto dto) {
			if (paysToAgreement==null) {
				paysToAgreement = getGUIController().paysToAgreementCodeSet(dto);
				dto.setPaysToAgreement(paysToAgreement);
			}
			
	}

		
	/**
	 * True if consultant is active
	 * 
	 * @param coreDto
	 * @return
	 */
	public boolean isConsultantActiveStatus(CoreConsultantDto coreDto) {
	
		return !(coreDto != null && 
				(coreDto.getAgreementStatus() == AgreementStatusType.IN_PROGRESS ||
				coreDto.getAgreementStatus() == AgreementStatusType.BROKER_IN_PROGRESS ||
				coreDto.getAgreementStatus() == AgreementStatusType.DECLINED ||
				coreDto.getAgreementStatus() == AgreementStatusType.DEATH ||
				coreDto.getAgreementStatus() == AgreementStatusType.TERMINATED ||
				coreDto.getAgreementStatus() == AgreementStatusType.BROKER_TERMINATED ||
				coreDto.getAgreementStatusReason().toLowerCase().contains("inactive")
				/*coreDto.getAgreementStatus() == AgreementStatusType.RETIRED ||
				coreDto.getAgreementStatus() == AgreementStatusType.BROKER_RETIRED*/));
			
	}
	
	/**
	 * 
	 * @param requestEnquiryResultDTO
	 * @param coreTransferDtoList
	 * @param feedbackPanel
	 * @return
	 */
	private boolean validateSegmentOutStandingRequest(RequestEnquiryResultDTO requestEnquiryResultDTO,List<CoreTransferDto> coreTransferDtoList,
		FeedbackPanel feedbackPanel){
		boolean result=true;
		Map contractNumberMap=getContractNumberMap(requestEnquiryResultDTO);
		StringBuffer outstandingRequest = null;
		int rowCount=0;
		for (CoreTransferDto dto : coreTransferDtoList) {
			rowCount++;
			if(contractNumberMap.get(dto.getContractNumber())!=null){
				dto.setRowStatus(false);
				if (outstandingRequest == null) {
					outstandingRequest = new StringBuffer();
				} else {
					outstandingRequest.append(", ");
				}
				outstandingRequest.append(rowCount);
				if(dto.getErrorMsg()==null || dto.getErrorMsg().equals("")){
					dto.setErrorMsg("10");
				}else{
					dto.setErrorMsg(dto.getErrorMsg()+", 10");
				}
			}
		}
		if (outstandingRequest != null) {
			/*feedbackPanel.error("Outstanding Segment request at Rows : "
					+ outstandingRequest);*/
			result=false;
		}
		return result;
}
	
	/**
	 * Validate outstanding request
	 * @param requestEnquiryResultDTO
	 * @param dto
	 * @return
	 */
	private boolean validateContractOutStandingRequest(RequestEnquiryResultDTO requestEnquiryResultDTO,CoreTransferDto dto){
		boolean result=true;
		Map contractNumberMap=getContractNumberMap(requestEnquiryResultDTO);
		if(dto.getContractNumber()!=null && contractNumberMap.get(dto.getContractNumber())!=null){
			dto.setRowStatus(false);
			result=false;
		}
		return result;
	}
	

	public Map getContractNumberMap(
			RequestEnquiryResultDTO requestEnquiryResultDTO) {
		Map<String, Boolean> contarctNumbetMap = new HashMap<String, Boolean>();
		RequestEnquiryRowDTO requestEnquiryRowDTO = null;
		List<IRequestEnquiryRow> requestEnquiryRow = (ArrayList<IRequestEnquiryRow>) requestEnquiryResultDTO
				.getResultList();
		for (IRequestEnquiryRow row : requestEnquiryRow) {
			requestEnquiryRowDTO = (RequestEnquiryRowDTO) row;
			contarctNumbetMap.put(
					(String)requestEnquiryRowDTO.getAdditionalProperty(PropertyKindType.ContractNumber.getPropertyKind()),
					true);
		}
		return contarctNumbetMap;
	}
	
	public Map getFromConsultantMap(
			RequestEnquiryResultDTO requestEnquiryResultDTO) {
	
		Map<Long, Boolean> contarctNumbetMap = new HashMap<Long, Boolean>();
		RequestEnquiryRowDTO requestEnquiryRowDTO = null;
		List<IRequestEnquiryRow> requestEnquiryRow = (ArrayList<IRequestEnquiryRow>) requestEnquiryResultDTO
				.getResultList();
		for (IRequestEnquiryRow row : requestEnquiryRow) {
			requestEnquiryRowDTO = (RequestEnquiryRowDTO) row;
			contarctNumbetMap.put(
					(Long)requestEnquiryRowDTO.getAdditionalProperty(PropertyKindType.FromConsultantCode.getPropertyKind()),
					true);
		}
		return contarctNumbetMap;
	}
	
	/**
	 * Get a reference to the GUI Controller (not cached)
	 * 
	 * @return
	 */
	protected ICoreTransferGuiController getGUIController() {
		try {
			return ServiceLocator
					.lookupService(ICoreTransferGuiController.class);
		} catch (NamingException e) {
			throw new CommunicationException(
					"Naming exception looking up CoreTransferGUIController",e);
		}
	}

	/**
	 * Set the From consultant with the coreconsultant info
	 * 
	 * @param consultantDto
	 * @param fromTransferDto
	 */
	public void setFromConsultant(CoreConsultantDto consultantDto, CoreTransferDto fromTransferDto) {
		fromTransferDto.setFromConsultantName(consultantDto.getPartyName());
		fromTransferDto.setFromConsultantCode(consultantDto.getConsultantCode());
		fromTransferDto.setFromConsultantStatus(consultantDto.getAgreementStatus().getDescription());
		fromTransferDto.setFromAgreementCode(consultantDto.getAgreementNumber());
	}
	
	/**
	 * Set the From consultant with the coreconsultant info
	 * 
	 * @param consultantDto
	 * @param fromTransferDto
	 */
	public void clearFromConsultant(CoreTransferDto fromTransferDto) {
		fromTransferDto.setRowStatus(false);
		if(fromTransferDto.getErrorMsg()==null){
			fromTransferDto.setErrorMsg("11");
		}else if(!fromTransferDto.getErrorMsg().contains("11"))
			fromTransferDto.setErrorMsg(appendErrorCode(fromTransferDto.getErrorMsg(), "11"));
		fromTransferDto.setFromConsultantName("");
		fromTransferDto.setFromConsultantStatus("");
	}
	
	
	/**
	 * Set the To consultant with the coreconsultant info
	 * 
	 * @param consultantDto
	 * @param fromTransferDto
	 */
	public void setToConsultant(CoreConsultantDto consultantDto, CoreTransferDto transferDto) {
		transferDto.setToConsultantName(consultantDto.getPartyName());
		transferDto.setToConsultantCode(consultantDto.getConsultantCode());	
		transferDto.setToConsultantImplicitStatus(getGUIController().getToConsultantStatus(consultantDto));
		transferDto.setToConsultantStatus(consultantDto.getAgreementStatus().getDescription());
		transferDto.setToConsultantStatusReason(consultantDto.getAgreementStatusReason());
		transferDto.setToAgreementCode(consultantDto.getAgreementNumber());
	}
	
	
}
