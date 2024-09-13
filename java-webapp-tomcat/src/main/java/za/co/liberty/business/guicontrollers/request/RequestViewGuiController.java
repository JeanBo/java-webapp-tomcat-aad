package za.co.liberty.business.guicontrollers.request;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.rmi.RemoteException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.CreateException;
import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.log4j.Logger;

import za.co.liberty.agreement.client.vo.AgreementRoleVO;
import za.co.liberty.agreement.client.vo.PropertyVO;
import za.co.liberty.agreement.client.vo.RequestVO;
import za.co.liberty.agreement.common.exceptions.KindNotFoundException;
import za.co.liberty.business.agreement.IAgreementManagement;
import za.co.liberty.business.guicontrollers.ContextManagement;
import za.co.liberty.business.party.IPartyManagement;
import za.co.liberty.business.request.IGuiRequestManagement;
import za.co.liberty.business.request.IRequestEnquiryManagement;
import za.co.liberty.business.request.IRequestManagement;
import za.co.liberty.business.request.handlers.IGuiRequestHandler;
import za.co.liberty.common.domain.ApplicationContext;
import za.co.liberty.common.domain.CurrencyAmount;
import za.co.liberty.common.domain.TypeVO;
import za.co.liberty.common.exceptions.ServiceNotFoundException;
//import za.co.liberty.common.type.TypeManagerInterfaceBPO;
//import za.co.liberty.common.type.TypeManagerInterfaceBPOHome;
import za.co.liberty.dto.agreement.AgreementRoleDTO;
import za.co.liberty.dto.agreement.request.RequestEnquiryDPERowDTO;
import za.co.liberty.dto.agreement.request.RequestEnquiryResultDTO;
import za.co.liberty.dto.agreement.request.RequestEnquiryRowDTO;
import za.co.liberty.dto.agreement.request.RequestEnquirySearchDTO;
import za.co.liberty.dto.agreement.request.RequestResultDTO;
import za.co.liberty.dto.contracting.ResultAgreementDTO;
import za.co.liberty.dto.contracting.ResultPartyDTO;
import za.co.liberty.dto.gui.request.ViewRequestContextModelDTO;
import za.co.liberty.dto.gui.request.ViewRequestModelDTO;
import za.co.liberty.dto.gui.request.alternative.AlternativePAYERequestDTO;
import za.co.liberty.dto.gui.request.alternative.AlternativeVATRequestDTO;
import za.co.liberty.dto.gui.request.alternative.AlternativeVATRequestFLO;
import za.co.liberty.dto.gui.request.alternative.IAlternativeRequestDTO;
import za.co.liberty.dto.transaction.ExternalPaymentRequestDTO;
import za.co.liberty.dto.transaction.IPolicyTransactionDTO;
import za.co.liberty.dto.transaction.RecordPolicyInfoDTO;
import za.co.liberty.dto.userprofiles.ISessionUserProfile;
import za.co.liberty.exceptions.data.DataNotFoundException;
import za.co.liberty.exceptions.data.QueryTimeoutException;
import za.co.liberty.exceptions.data.ValidationException;
import za.co.liberty.exceptions.error.request.RequestConfigurationException;
import za.co.liberty.exceptions.error.request.RequestException;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.exceptions.fatal.InconsistentConfigurationException;
import za.co.liberty.exceptions.system.EjbFinderSystemException;
import za.co.liberty.helpers.util.DateUtil;
import za.co.liberty.interfaces.account.VATRateType;
import za.co.liberty.interfaces.agreements.RoleKindType;
import za.co.liberty.interfaces.agreements.requests.PropertyKindType;
import za.co.liberty.interfaces.agreements.requests.RequestKindType;
import za.co.liberty.interfaces.agreements.requests.RequestStatusType;
import za.co.liberty.interfaces.gui.GuiRequestKindType;
import za.co.liberty.interfaces.persistence.agreement.request.IRequestEnquiryRow;
import za.co.liberty.persistence.account.IAccountEntryEntityManager;
import za.co.liberty.persistence.srs.entity.GuiRequestEntity;
import za.co.liberty.persistence.srs.entity.GuiRequestImageTypeEntity;
import za.co.liberty.persistence.srs.entity.GuiRequestRequestEntity;

/**
 * The implementation for viewing requests in the Gui.
 * 
 * @author JZB0608 - 10 Feb 2010
 *
 */
@Stateless
public class RequestViewGuiController implements IRequestViewGuiController {
		
	
	@SuppressWarnings("unused")
	private static Logger logger = Logger.getLogger(RequestViewGuiController.class);
	
	static {
//		logger.setLevel(Level.DEBUG);
	}
	
	/**
	 * Initialise the request context from the given request detail
	 * 
	 * @param rowDTO
	 * @return
	 */
	private ViewRequestContextModelDTO initialiseRequestContext(RequestEnquiryRowDTO requestDto, 
			Long agreementNr, Long partyOid) {
		ResultAgreementDTO agreementDto = null;
		ResultPartyDTO partyDto = null;
		try {
			// Initialise with Agreement
			if (agreementNr!=null && agreementNr!=0) {
				ContextManagement m = new ContextManagement();
				agreementDto = m.findAgreementWithSRSAgreementNr(agreementNr);
				partyDto = m.newExampleResultPartyDTO();
//					.findPartyIntermediaryWithAgreementNr(agreementNr);
			} 
			
			// Initialise with party
			if (partyDto == null && partyOid!=null) {
				logger.error("Oops, we didn't implement this");
//				partyDto = partyManagementBean.findPartyWithObjectOid(partyOid);
			}
	
			return new ViewRequestContextModelDTO(requestDto, agreementDto, partyDto);
		} catch (DataNotFoundException e) {
			throw new InconsistentConfigurationException("Unable to retrieve expected data for request oid "
					+ requestDto.getRequestId()+" and agreement nr "
					+agreementNr+" and party id "+partyOid);
		}
	}
	
	
	/**
	 * Create a model to test ExternalPaymentRequestDTO request 
	 * 
	 * @param rowDto
	 * @return
	 */
	public ViewRequestModelDTO initViewModel_externalPayment(RequestEnquiryRowDTO rowDto) {
		logger.info("Initialise ViewModel - external payment");
		ViewRequestContextModelDTO contextDto = initialiseRequestContext(rowDto, rowDto.getAgreementNr(), null);	
		contextDto.getAgreementDto().setAgreementNumber(rowDto.getAgreementNr());
		contextDto.getPartyDto().setName(rowDto.getPartyName());
		contextDto.getPartyDto().setPartyOid(rowDto.getPartyOid());
		ViewRequestModelDTO pageModel = new ViewRequestModelDTO(rowDto.getRequestKindType());
		pageModel.setNewGuiRequest(true);
		pageModel.setAlternativeRequest(false);
		pageModel.setViewRequestContextDto(contextDto);
		
		ExternalPaymentRequestDTO dto = new ExternalPaymentRequestDTO();
		dto.setOid(rowDto.getRequestId());
		dto.setAgreementNumber(rowDto.getAgreementNr());
		dto.setDescription("Description");
		dto.setDirectiveNumber("321123333");
		dto.setFullAmount(new CurrencyAmount(new BigDecimal("1000.00")));
		dto.setTaxAmount(new CurrencyAmount(new BigDecimal("300.00")));
		dto.setIt88Amount(new CurrencyAmount(new BigDecimal("0.00")));
		dto.setExecutedDate(new Date(DateUtil.getInstance().getTodayDatePart().getTime()));
		pageModel.setCurrentImage(dto);
		pageModel.setRequestEnquiryRowList(new ArrayList<RequestEnquiryRowDTO>());
		pageModel.getRequestEnquiryRowList().add(rowDto);
		return pageModel;
	}
	
	
	/**
	 * Create a model to test ExternalPaymentRequestDTO request 
	 * 
	 * @param rowDto
	 * @return
	 */
	public ViewRequestModelDTO initViewModel_properties(RequestEnquiryRowDTO rowDto) {
		logger.info("Initialise ViewModel - properties");
		ViewRequestContextModelDTO contextDto = initialiseRequestContext(rowDto, rowDto.getAgreementNr(), null);	
		contextDto.getAgreementDto().setAgreementNumber(rowDto.getAgreementNr());
		contextDto.getPartyDto().setName(rowDto.getPartyName());
		contextDto.getPartyDto().setPartyOid(rowDto.getPartyOid());
		ViewRequestModelDTO pageModel = new ViewRequestModelDTO(rowDto.getRequestKindType());
		pageModel.setNewGuiRequest(false);
		pageModel.setAlternativeRequest(false);
		pageModel.setViewRequestContextDto(contextDto);
		
		ExternalPaymentRequestDTO dto = new ExternalPaymentRequestDTO();
		dto.setOid(rowDto.getRequestId());
		dto.setAgreementNumber(rowDto.getAgreementNr());
		dto.setDescription("Description");
		dto.setDirectiveNumber("321123333");
		dto.setFullAmount(new CurrencyAmount(new BigDecimal("1000.00")));
		dto.setTaxAmount(new CurrencyAmount(new BigDecimal("300.00")));
		dto.setIt88Amount(new CurrencyAmount(new BigDecimal("0.00")));
		dto.setExecutedDate(new Date(DateUtil.getInstance().getTodayDatePart().getTime()));
		pageModel.setCurrentImage(dto);
		pageModel.setRequestEnquiryRowList(new ArrayList<RequestEnquiryRowDTO>());
		pageModel.getRequestEnquiryRowList().add(rowDto);
		return pageModel;
	}
	
	
	 /**
	  * Initialise the request View model.
	  * 
	  * @param rowDto
	  * @return
	  */
	@SuppressWarnings("unchecked")
	public ViewRequestModelDTO initialiseRequestViewModel(RequestEnquiryRowDTO rowDto) {
		logger.info("Start view model  id=" + rowDto.getRequestId() + "  ,kind= " + rowDto.getRequestKind() );
		
		if (rowDto.getRequestId() == 200L) {
			return initViewModel_externalPayment(rowDto);
		} else if (rowDto.getRequestId() == 400L) {
			return initViewModel_properties(rowDto);
		}
		
		// Is this a new or old request?
		RequestKindType requestKind = rowDto.getRequestKindType();
		GuiRequestKindType guiRequestKind= null;
		GuiRequestEntity entity = null;
		Long partyOid = null;
		Long agreementNr = null;
		Object currentImageObj = null;
		Object beforeImageObj = null;
		Long guiRequestId = null;
		
		/*
		 * Set various flags indicating which code path to follow.  Gui Requests
		 */
		boolean isRequestPropsMapped = false; // requestManagementBean.isRequestPropertiesMappedToAnObject(requestKind);
		boolean isGuiRequest = false; //requestManagementBean.isRequestConverted(requestKind, rowDto.getRequestDate());
		boolean isAlternativeRequest = true; //= requestManagementBean.isRequestHasAlternativeGUI(requestKind);
		
		if (!isGuiRequest && !isRequestPropsMapped && !isAlternativeRequest) {
			// It's an old one
			if (logger.isDebugEnabled())
				logger.debug("requestKind \""+requestKind+"\" has not been converted to new behaviour");
			ViewRequestContextModelDTO contextDto = initialiseRequestContext(rowDto, rowDto.getAgreementNr(), null);			
			ViewRequestModelDTO pageModel = new ViewRequestModelDTO(requestKind);
			pageModel.setViewRequestContextDto(contextDto);
			return pageModel;
		}	
		
//		/* Request is part of a GuiRequest or it can be converted from properties */
//		try {
//			List<RequestKindType> requestTypeList = new ArrayList<RequestKindType>();
//			List<Long> requestIdList = new ArrayList<Long>();
//			
//			/* Initialise the id's and types	 */
//			if (!isGuiRequest && (isRequestPropsMapped || isAlternativeRequest)) {
//				requestTypeList.add(rowDto.getRequestKindType());
//				requestIdList.add(rowDto.getRequestId());
//				
//				// Get the agreeement nr
//				// Get the party id
//				agreementNr = rowDto.getAgreementNr();
//				partyOid = rowDto.getPartyOid();
//				
//			} else {
//				entity = guiRequestManagementBean.findGuiRequestEntityWithRequestId(rowDto.getRequestId());
//				if (logger.isDebugEnabled())
//					logger.debug("Found GuiRequestEntity " + entity.getId());
//				
//				/* Get attached request kinds */
//			
//				for (GuiRequestRequestEntity requestE : entity.getRequestList()) {
//					if (requestE.getRequestOid()==0) {
//						// Reject invalid requests (due to GuiRequestEntityManager bug)
//						continue;
//					}
//					requestTypeList.add(RequestKindType.getRequestKindTypeForKind((int)requestE.getRequestKind()));
//					requestIdList.add(requestE.getRequestOid());
//				}
//				
//				guiRequestKind = GuiRequestKindType
//						.getGuiRequestKindTypeForKind((int)entity.getGuiRequestKind());
//					
//				agreementNr = entity.getAgreementNr();
//				partyOid = entity.getPartyOid();
//				guiRequestId = entity.getId();
//			}
//			
//			/* Get all the attached request row data */
//			List<RequestEnquiryRowDTO> requestEnquiryList = null;
//			
//			RequestEnquirySearchDTO searchDto = new RequestEnquirySearchDTO();
//			searchDto.setRequestIdList(requestIdList);
//			try {
//				requestEnquiryList = (List)requestEnquiryManagementBean.findRequests(
//						searchDto, 1, requestIdList.size()+1, RequestEnquiryRowDTO.class).getResultList();
//			} catch (RequestException e) {
//				// This can only be due to a programming error
//				throw new RuntimeException("Unable to retrieve data while initialising request view.",e);
//			} catch (QueryTimeoutException e) {
//				// This should also not happen
//				throw new RuntimeException("Unable to retrieve data in the alloted time, " +
//						"while initialising request view.",e);
//			}
//				
//			
//			/* Now retrieve the before and after images */
//			if (!isGuiRequest) {
//				if (isAlternativeRequest) {
//					currentImageObj = null;
//					// DO nothing, will initialise on page
//				} else if (isRequestPropsMapped) {
//					/*
//					 * THis is an old request VO request that must be converted to an object.
//					 *   - For now the DPE and AUM kinds are hard coded (should work but can't do all testing now).  
//					 *   - All other requests will use the default behaviour
//					 *   
//					 *   TODO jean - to remove this as yet? This should automatically work.
//					 */
////					if(requestKind.equals(RequestKindType.DistributePolicyEarning)) {
////						currentImageObj = new DistributePolicyEarningDTO();
////					} else if(requestKind.equals(RequestKindType.RecordPolicyInfo)) {
////						currentImageObj = new RecordPolicyInfoDTO();
////					}
//					try {
//						// Initialise using request management bean
//						if (currentImageObj==null) {
//							currentImageObj = requestManagementBean.newRequestObjectInstance(requestKind);
//						}
//						// Copy the request properties to the object
//						requestManagementBean.copyRequestPropertiesToObject(requestManagementBean.findRequestVO(
//								rowDto.getRequestId()), currentImageObj);
//						// Code for Zweli specifically (jzb0608)
//						if (currentImageObj instanceof IPolicyTransactionDTO) {
//							((IPolicyTransactionDTO)currentImageObj).setEffectiveDate(new Date(rowDto.getRequestedDate().getTime()));
//						}
//					} catch (RequestException e) {
//						throw new InconsistentConfigurationException(
//								"Unable to retrieve request data, please contact system staff",e);
//					}
//				} // end if isAlternativeRequest
//				
//			} else {
//				/* Get current image obj */
//				if (logger.isDebugEnabled())
//					logger.debug("Retrieve current image " + entity.getId());
//				try {
//					currentImageObj = guiRequestManagementBean.retrieveDTOFromGuiRequest(entity, 
//							GuiRequestImageTypeEntity.CurrentImage);
//				} catch (RequestConfigurationException e) {
//					throw new InconsistentConfigurationException(
//							"Unable to retrieve request data, please contact system staff",e);
//				} catch (DataNotFoundException e) {
//					throw new InconsistentConfigurationException(
//							"Unable to retrieve request data, please contact system staff",e);
//				}
//				
//				/* Get before image obj (if available) */
//				if (logger.isDebugEnabled())
//					logger.debug("Retrieve after image " + entity.getId());
//				try {
//					beforeImageObj = guiRequestManagementBean.retrieveDTOFromGuiRequest(entity, 
//							GuiRequestImageTypeEntity.BeforeImage);
//				} catch (RequestConfigurationException e) {
//					throw new InconsistentConfigurationException(
//							"Unable to retrieve request data, please contact system staff",e);
//				} catch (DataNotFoundException e) {
//					// this is only required for maintenance requests, ignore
//				}
//				if (logger.isDebugEnabled())
//					logger.debug("Done retrieving " + entity.getId());
//				
//			}
			
			// build response
			ViewRequestModelDTO pageModel = new ViewRequestModelDTO(
					guiRequestKind,
					requestKind,
					currentImageObj,
					beforeImageObj);
			ViewRequestContextModelDTO contextDto = initialiseRequestContext(rowDto, agreementNr, partyOid);
			contextDto.setGuiRequestKind(guiRequestKind);
			contextDto.setGuiRequestId(guiRequestId);
			pageModel.setViewRequestContextDto(contextDto);
			
//			pageModel.setRequestKindList(requestTypeList);
			
			pageModel.setGuiRequestId(guiRequestId);
			pageModel.setNewGuiRequest(isGuiRequest || isRequestPropsMapped);
			pageModel.setAlternativeRequest(isAlternativeRequest);
			
//			if (requestEnquiryList.size()>1) {
//				// Order the list using the handler order
//				List<RequestEnquiryRowDTO> newList = new ArrayList<RequestEnquiryRowDTO>();
//				
//				IGuiRequestHandler handler = guiRequestManagementBean.getGuiRequestHandler(guiRequestKind);
//				for (RequestKindType type : handler.getOrderedListOfAllowableRequestKinds()) {
//					for (int i = 0; i < requestEnquiryList.size(); ++i) {
//						if (requestEnquiryList.get(i).getRequestKindType()==type) {
//							newList.add(requestEnquiryList.get(i));
//							requestEnquiryList.remove(i--);
//						}
//					}
//				}
//				
//				requestEnquiryList = newList;
//			}
//			pageModel.setRequestEnquiryRowList(requestEnquiryList);
			logger.info("End view model");
			return pageModel;
//		} catch (DataNotFoundException e) {
//			logger.error("Unable to retrieve GuiRequest for new RNA request");
//			throw new InconsistentConfigurationException("Unable to retrieve GuiRequest for new RNA request. " +
//					"RequestOid = "+rowDto.getRequestorId(),e);
//		} catch (RequestConfigurationException e) {
//			// If this happens, it's the end of the world
//			throw new InconsistentConfigurationException(e);
//		}
		
	}
	
	/**
	 * Authorise the list of request.  The passed request list is updated after authorise.
	 * 
	 * @param sessionUserProfile
	 * @param guiRequestOid,
	 * @param name
	 * 
	 * @throws ValidationException
	 */
	public void authoriseRequests(ISessionUserProfile sessionUserProfile, Long guiRequestOid, List<RequestEnquiryRowDTO> requestList) throws ValidationException {
		System.out.println("AUTHORISE request " + guiRequestOid);
//		List<Long> requestIds = new ArrayList<Long>();
//		for (RequestEnquiryRowDTO dto : requestList) {
//			requestIds.add(dto.getRequestId());
//		}
//		try {
//			logger.info("Start authorise request oid = " + requestIds);
//				
//			// Authorise the requests 
//			List<RequestResultDTO> resultList = null;
//			if (guiRequestOid != null) {
//				resultList = guiRequestManagementBean.authoriseRequestsForGuiRequest(
//						new ApplicationContext(), 
//						sessionUserProfile, guiRequestOid, requestIds);
//			} else {
//				// This is for non-GUI requests
//				if (requestList.size()!=1) {
//					throw new ValidationException("");
//				}
//				RequestEnquiryRowDTO rowDto = requestList.get(0);
//				RequestVO requestVO = requestManagementBean.authoriseRequest(
//						new ApplicationContext(), rowDto.getRequestKindType(), 
//						rowDto.getRequestId(), sessionUserProfile);
//				RequestStatusType updatedStatus = RequestStatusType.getRequestStatusTypeForPsdDescription(
//						requestVO.getCurrentStatus());
//				rowDto.setStatusType(updatedStatus);
//			}
//			
//			// Refresh these requests
//			refreshRequests(requestList, resultList);
//		} catch (RequestConfigurationException e) {
//			throw new InconsistentConfigurationException(e);
//		} catch (RequestException e) {
//			String message = e.getMessage();
//			if (e.getLinkedRequestId()!=null) {
//				// Add some additional info about linked request
//				message = "Request id \""+e.getLinkedRequestId()+"\" : " + message;
//			}
//			throw new ValidationException(message);
//		} catch (DataNotFoundException e) {
//			throw new InconsistentConfigurationException(e);
//		} catch (QueryTimeoutException e) {
//			logger.error("Timeout when refreshing request data",e);
//			// This should never happen, but let's give a warning just in case
//			throw new ValidationException("Unable to retrieve request data in the required time, " +
//					"please try again.  If the problem persists, please contact support.");
//		}
		
	}

	/**
	 * Decline the list of requests.  The passed list data will be updated after decline.
	 * 
	 * @param sessionUserProfile
	 * @param guiRequestOid
	 * @param requestList
	 * @throws ValidationException
	 */
	public void declineRequests(ISessionUserProfile sessionUserProfile, 
			Long guiRequestOid, List<RequestEnquiryRowDTO> requestList) throws ValidationException {
		
		logger.info("Decline requests " + guiRequestOid);
	}
	
	/**
	 * Update the list of requests with latest information.  Use requestRowList to update
	 * request statusses as this only gets commited at the end of the transaction.
	 * 
	 * @param requestList
	 * @param requestRowList
	 * @throws QueryTimeoutException 
	 * @throws RequestException 
	 */
	@SuppressWarnings("unchecked")
	private void refreshRequests(List<RequestEnquiryRowDTO> requestList, 
			List<RequestResultDTO> requestRowList) throws RequestException, QueryTimeoutException {
		
		logger.info("Refresh requests");
		
	}
	
	/**
	 * Get the data for an alternative request
	 * @param requestId
	 * @return
	 */
	public IAlternativeRequestDTO getAlternativeRequestDTO(Long requestId) throws ValidationException  {
		
//		try {
//			RequestVO requestVO = requestManagementBean.findRequestVO(requestId);
//			
//			if (RequestKindType.getRequestKindTypeForKind(requestVO.getKind())==RequestKindType.VAT) {
//				return getAlternativeVATRequestDTO(requestVO);
//			} else if (RequestKindType.getRequestKindTypeForKind(requestVO.getKind())==RequestKindType.PAYE) {
//				return getAlternativePAYERequestDTO(requestVO);
//			}
//			
//		} catch (RequestException e) {
//			logger.error("Unable to retrieve alternative DTO for request " + requestId,e);
//			throw new ValidationException("Unable to retrieve required request");
//		}
		
		return null;
	}
	

	/**
	 * Get the data for an alternative VAT request DTO.
	 * 
	 * @param requestId
	 * @return
	 */
	protected AlternativeVATRequestDTO getAlternativeVATRequestDTO(RequestVO requestVO) throws ValidationException  {
		return null;
	}
	
	
	/**
	 * Get the data for an alternative VAT request DTO.
	 * 
	 * @param requestId
	 * @return
	 */
	protected AlternativePAYERequestDTO getAlternativePAYERequestDTO(RequestVO requestVO) throws ValidationException  {
		
		try {
			
			// Find current account
			Long agreementId = requestVO.getTopLevelAgreementId();

			AlternativePAYERequestDTO dto = new AlternativePAYERequestDTO();
			
			/**
			 * Loop through the properties and set relevant properties
			 */
			for (PropertyVO propVO : requestVO.getProperties()) {
				PropertyKindType propType = PropertyKindType.getPropertyKindTypeForKind(propVO.getKind());
				List<CurrencyAmount> currencyList;
				CurrencyAmount total;
				
				if (logger.isDebugEnabled())
						logger.debug("Setting property for paye: " + propVO.getKind() + "   ,value: " + propVO.getValue());
				switch (propType) {
					case DIRECTIVE_LIMIT_PAYE_AMOUNT : 
						dto.setDirectiveLimitPayeAmount((CurrencyAmount) propVO.getValue());
						break;
					case DIRECTIVE_EFFECTIVE_STARTDATE : 
						dto.setDirectiveEffectiveStartDate( (java.util.Date) propVO.getValue());
						break;
					case PAYE_LIMIT_REFUND : 
						dto.setIsPayeLimitRefund((Boolean)propVO.getValue());
						break;
					case CALC_TAXBASIS : 
						dto.setTaxBasis((Integer)propVO.getValue());
						break;
					case PAYETaxPeriodToDate : 
						dto.setPayeTaxPeriodToDatePaid((CurrencyAmount) propVO.getValue());
						break;
					case PAYETaxPeriodToDatePerCompany : 
						currencyList = (List<CurrencyAmount>) propVO.getValue();
						total = new CurrencyAmount(BigDecimal.ZERO);
						for (CurrencyAmount amt :currencyList ) {
							if (amt!=null) {
								total.add(amt, CurrencyAmount.DEFAULT_ROUNDING_METHOD);
							}
						}
						dto.setTotalPayeTaxPeriodToDateDue(total);
						break;
					case PAYEAmountPerCompany : 
						currencyList = (List<CurrencyAmount>) propVO.getValue();
						total = new CurrencyAmount(BigDecimal.ZERO);
						for (CurrencyAmount amt :currencyList ) {
							if (amt!=null) {
								total.add(amt, CurrencyAmount.DEFAULT_ROUNDING_METHOD);
							}
						}
						dto.setTotalPayeAmountPerCompany(total);
						break;
					case DIRECTIVE_LIMIT_PAYE_REQUESTOID :
						dto.setDirectiveLimitPayeRequestOid((Long)propVO.getValue());
						break;
					case DIRECTIVE_LIMIT_ANNUAL_PAYMENT_AMOUNT:
						dto.setDirectiveLimitAnnualTEAmount((CurrencyAmount) propVO.getValue());
						break;
					case DIRECTIVE_LIMIT_MONTHLY_PAYMENT_AMOUNT:
						dto.setDirectiveLimitMonthlyTEAmount((CurrencyAmount) propVO.getValue());
						break;
					case DIRECTIVE_LIMIT_ATE_REQUESTOID:
						dto.setDirectiveLimitATERequestOid((Long) propVO.getValue());
						break;
					default:
						if (logger.isDebugEnabled())
								logger.debug("   -- No value set for kind: " + propVO.getKind() + "   ,value: " + propVO.getValue());
						break;
				}
				
			}
			

			// end
			
//			dto.setVatPayments(list);
//			dto.setVatPaymentTotals(new ArrayList<AlternativeVATRequestDTO.VATPayment>(paymentMap.values()));
			return dto;
			
		} catch (Exception e) { 	//(KindNotFoundException e) {
			logger.error("Issue while retrieving PAYE request for " + requestVO.getObjectReference(), e);
			throw new ValidationException("Unable to retrieve payments for PAYE request", e);
		}
		
//		return null;
	}
}
