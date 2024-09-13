package za.co.liberty.business.request;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.Session;
import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.naming.NamingException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

import org.apache.commons.lang.NotImplementedException;
import org.apache.log4j.Logger;

import za.co.liberty.agreement.client.vo.AgreementVO;
import za.co.liberty.business.agreement.IAgreementManagement;
import za.co.liberty.business.agreement.ISpecManagment;
import za.co.liberty.business.agreement.IValidAgreementValuesFactory;
import za.co.liberty.business.converter.IAgreementConverter;
import za.co.liberty.business.dpe.helper.DPEBenefitGroupHelper;
import za.co.liberty.business.guicontrollers.IContextManagement;
import za.co.liberty.business.party.IPartyManagement;
import za.co.liberty.business.security.ISecurityManagement;
import za.co.liberty.common.domain.CurrencyAmount;
import za.co.liberty.common.enums.CurrencyEnum;
import za.co.liberty.csv.CSVRowMappingDTO;
import za.co.liberty.dto.agreement.AgreementRoleDTO;
import za.co.liberty.dto.agreement.PaymentSchedulerDTO;
import za.co.liberty.dto.agreement.properties.PaysToDTO;
import za.co.liberty.dto.agreement.request.RequestEnquiryAUMRowDTO;
import za.co.liberty.dto.agreement.request.RequestEnquiryDPERowDTO;
import za.co.liberty.dto.agreement.request.RequestEnquiryRowDTO;
import za.co.liberty.dto.agreement.request.RequestEnquirySearchDTO;
import za.co.liberty.dto.contracting.ResultAgreementDTO;
import za.co.liberty.dto.gui.templates.DescriptionDTO;
import za.co.liberty.dto.request.RequestTransactionBulkLoadResponseDTO;
import za.co.liberty.dto.spec.TypeDTO;
import za.co.liberty.dto.transaction.DistributePolicyEarningDTO;
import za.co.liberty.dto.transaction.ExternalPaymentRequestDTO;
import za.co.liberty.dto.transaction.IPolicyTransactionDTO;
import za.co.liberty.dto.transaction.RecordPolicyInfoDTO;
import za.co.liberty.dto.transaction.RequestTransactionDTO;
import za.co.liberty.dto.userprofiles.ISessionUserProfile;
import za.co.liberty.exceptions.data.ConformanceTypeException;
import za.co.liberty.exceptions.data.DataNotFoundException;
import za.co.liberty.exceptions.data.QueryTimeoutException;
import za.co.liberty.exceptions.data.ValidationException;
import za.co.liberty.exceptions.error.request.RequestException;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.helpers.communication.MailHelper;
import za.co.liberty.helpers.config.HelperConfigParameterTypes;
import za.co.liberty.helpers.config.HelpersParameterFactory;
import za.co.liberty.helpers.util.DateUtil;
import za.co.liberty.interfaces.agreements.AgreementStatusType;
import za.co.liberty.interfaces.agreements.PolicyInfoKindType;
import za.co.liberty.interfaces.agreements.ProductKindType;
import za.co.liberty.interfaces.agreements.RoleKindType;
import za.co.liberty.interfaces.agreements.requests.EarningAndDeductionParentType;
import za.co.liberty.interfaces.agreements.requests.RequestKindType;
import za.co.liberty.interfaces.agreements.requests.RequestStatusType;
import za.co.liberty.interfaces.persistence.agreement.request.IRequestEnquiryAUMRowDTO;
import za.co.liberty.interfaces.persistence.agreement.request.IRequestEnquiryRow;
import za.co.liberty.interfaces.rating.description.DescriptionKindType;
import za.co.liberty.interfaces.reporting.ProductReferenceType;
import za.co.liberty.persistence.agreement.entity.DateRange;
import za.co.liberty.persistence.rating.FundCodeEntityManager;
import za.co.liberty.persistence.rating.IDescriptionEntityManager;
import za.co.liberty.persistence.rating.IRatingEntityManager;
import za.co.liberty.persistence.rating.ProductCodesEntityManager;
import za.co.liberty.persistence.rating.entity.Description;
import za.co.liberty.persistence.rating.flo.FundCodeFLO;
import za.co.liberty.persistence.rating.flo.ProductCodeFLO;
import za.co.liberty.persistence.srs.IBulkLoadAgreementEntityManager;
import za.co.liberty.persistence.srs.entity.BulkLoadBatchEntity;
import za.co.liberty.persistence.srs.entity.BulkLoadBatchType;
import za.co.liberty.srs.type.SRSType;
import za.co.liberty.srs.util.BigDecimalFactory;
import za.co.liberty.srs.util.agreement.CurrencyAmountFactory;
import za.co.liberty.srs.util.agreement.TaxBasisConstants;
import za.co.liberty.xml.transactions.externalpayments.ExternalPaymentMessageJXBO;
import za.co.liberty.xml.transactions.externalpayments.ProcessExternalPaymentJXBO;

/**
 * Management bean used to load batches of transactions. A file is passed 
 *  
 * @author JZB0608
 *
 */
@Stateless(name="RequestTransactionManagement") 
public class otherRequestTransactionManagement implements IRequestTransactionManagement {

//	@EJB
//	ISpecManagment specManagement;
//	
//	@EJB
//	IAgreementConverter agreementConverter;
//	
//	@EJB
//	IValidAgreementValuesFactory validAgreementValuesFactory;
//	
//	@EJB 
//	ISecurityManagement securityManagement;
//	
//	@EJB
//	IGuiRequestManagement guiRequestManagement;
//	
//
//	@EJB
//	IPartyManagement partyManagement;
//	
//	@EJB
//	IAgreementManagement agreementManagement;
//
//	
//	@EJB 
//	IBulkLoadAgreementEntityManager bulkLoadAgreementEntityManager;
//	
//	@EJB
//	protected IRequestEnquiryManagement requestEnquiryManagement;
//	
//	@EJB 
//	IContextManagement contextManagement;
//	
//	@EJB
//	private IRatingEntityManager ratingEntityManager;
//	
//	@EJB
//	private IDescriptionEntityManager descriptionEntityManager;
//	
//	@Resource 
//	private SessionContext sessionContext;
//	
//	@Resource(name="IAAQCF")
//	private QueueConnectionFactory connectionFactoryInject;
//	
//	@Resource(name="ProcessExternalPaymentsInQ")
//	private static Queue processExternalPaymentsInQ;
//	
//	@EJB
//	FundCodeEntityManager fundCodeEntityManager;
//	
//	@EJB
//	ProductCodesEntityManager productCodesEntityManager;
	
	
	private Logger logger = Logger.getLogger(this.getClass());
	
	private static CurrencyAmount ZERO_CURRENCY = new CurrencyAmount(BigDecimal.ZERO, CurrencyEnum.ZAR);
	private static List<TypeDTO> validExternalPaymentTransactionTypeList;
	private static Date taxYearStart;
	
	private static Set<RequestKindType> validRequestKindSet = new HashSet<RequestKindType>(
			Arrays.asList(new RequestKindType[] {RequestKindType.ProcessExternalPayments}));
	private static Set<String> externalPaymentStatusSet = new HashSet<String>();
	private static Map<RequestKindType, BulkLoadBatchType> requestKindAndBatchTypeMap 
			= new HashMap<RequestKindType, BulkLoadBatchType>();
	
	private static Set<Integer>  riskPremiumProductTypeSet;
	
	/**
	 * Initialise some static values
	 */
	static {
		// Status init
		externalPaymentStatusSet.add(AgreementStatusType.ACTIVE.getDescription().toUpperCase());
		externalPaymentStatusSet.add(AgreementStatusType.RETIRED.getDescription().toUpperCase());
		
		// Map init
		requestKindAndBatchTypeMap.put(RequestKindType.ProcessExternalPayments, BulkLoadBatchType.EXTERNAL_PAYMENTS);
		
		Set<Integer> tmpSet = new HashSet<Integer>();
		for (ProductReferenceType t : ProductReferenceType.getRiskPremiumProductTypes()) {
			tmpSet.add(t.getProductRef());
		}
		riskPremiumProductTypeSet = tmpSet;
	}

	/**
	 * For the specified file name load the records mentioned.  We specifically allow for the start and end records to 
	 * be sent and will only load batch information, and do processing, only for those numbers inclusive.
	 * 
	 * @throws DataNotFoundException  Thrown if the file doesn't exist or no records were processed  
	 * 
	 */
	public RequestTransactionBulkLoadResponseDTO loadRequestTransactionsFromFile(RequestKindType requestKindType,
			String fileName, int startRecord,
			int endRecord) throws DataNotFoundException, ValidationException{
		return this.loadRequestTransactionsFromFile(requestKindType,
				fileName, startRecord, endRecord, false);
	}
	
	/**
	 * For the specified file name load the records mentioned.  We specifically allow for the start and end records to 
	 * be sent and will only load batch information, and do processing, only for those numbers inclusive.
	 * <br/>
	 * All changes happen in one transaction and will back out if there are any failures.
	 * 
	 * @throws DataNotFoundException  Thrown if the file doesn't exist or no records were processed  
	 * 
	 */
	public RequestTransactionBulkLoadResponseDTO loadRequestTransactionsFromFile(RequestKindType requestKindType,
			String fileName, int startRecord,
			int endRecord, boolean isValidate) throws DataNotFoundException, ValidationException{
		
		logger.info("Processing request transactions"
				+"\n  File = " + fileName
				+"\n  Parameters  start=" + startRecord 
					+", end = " + endRecord
					+", validate = " + isValidate);
	
		// Remove large chunk of code and throw exception rather
		
		throw new IllegalStateException("Not implemented");
	}


	
	/**
	 * Create an Excel report for the given report and email it 
	 * 
	 * @param requestTransactionBulkLoadResponseDTO
	 */
	public void createAndSendReport(RequestTransactionBulkLoadResponseDTO responseDTO) throws CommunicationException {
		File tempFile;
		try {
			tempFile = File.createTempFile("RequestTransactionErrors", ".xls");
			
			createReport(responseDTO, tempFile);
			
			String mailMessage = "Please find attached the request transaction errors report";
			
			mailMessage+= "\n\nRead=" + responseDTO.getRecordsRead()
				+ "\nValidated=" + responseDTO.getRecordsValidated()
				+ "\nProcessed=" + responseDTO.getRecordsProcessed()
				+ "\nSuccess=" + responseDTO.getRecordsSuccessFul();

			String subject = "Request Error Report";
			sendEmail(subject, mailMessage, tempFile);	
			
		} catch (Exception e) {
			throw new CommunicationException("An internal issue occurred when creating/sending report", e);
		}
		
		
	}

	/**
	 * Create an Excel report for the given response
	 * 
	 * @param requestTransactionBulkLoadResponseDTO
	 * @param file
	 */
	public void createReport(RequestTransactionBulkLoadResponseDTO requestTransactionBulkLoadResponseDTO, File file) {
		// Requires additional Jar files
		throw new IllegalStateException("Not implemented");
	}

	
	/**
	 * This method emails the generated report
	 * @param subject
	 * @param message
	 * @param fileName
	 * @throws MessagingException 
	 * @throws NamingException 
	 * @throws AddressException 
	 */
	private void sendEmail(String subject, String message,File attachFile) throws AddressException, NamingException, MessagingException {
		
		
		String defaultEmailTo = HelpersParameterFactory.getInstance().getParameter(
				HelperConfigParameterTypes.DEV_NOTIFICATION_EMAIL_TO, String.class);
		String mailTo = defaultEmailTo;
//		mailTo = "jean.bodemer@liberty.co.za,antonie.meyer@liberty.co.za";
		String mailFrom =  defaultEmailTo;

		new MailHelper().sendMail(mailTo, mailFrom, subject, message, attachFile);
		
	}
	

	/**
	 * Internal method to retrieve rows from the CSV file specified but limited to the given rows.
	 * 
	 * 
	 * @param fileName
	 * @param startRecord  Should be > than zero
	 * @param endRecord  Should be > startRecord
	 * @return
	 * @throws FileNotFoundException
	 */
	public <T extends RequestTransactionDTO> List<T> retrieveRows(String fileName, int startRecord, int endRecord,
			Class<T> resultClass) throws FileNotFoundException {
		
		logger.info("Starting file processing :" + fileName + " ,start=" + startRecord + "  ,end=" + endRecord);
		// Removed due to jar dependencies or db requirements
		throw new IllegalStateException("Not implemented");
	}  
	
	/**
	 * Return the CSV configuration mapping for the given file template.
	 * @return
	 */
	private List<CSVRowMappingDTO> getCSVConfiguration(RequestKindType requestKind) {
        	
		// Removed due to jar dependencies or db requirements
				throw new IllegalStateException("Not implemented");
	}


	/**
	 * Get the start of the tax year
	 * 
	 * @return
	 */
	public Date getTaxYearStartDate() {
		if (taxYearStart ==null) {
//			DateRange range = ratingEntityManager.getTaxYearDates(new Date(), TaxBasisConstants.TAX_YEAR_LIBERTY);
//			taxYearStart = DateUtil.getInstance().getDatePart(	range.getStartDate());
			try {
				taxYearStart = DateUtil.getInstance().getDateFromString("2023-03-05");
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return taxYearStart;
			
	}
	
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
	public void doAgreementValidation(long agreementNr,
			RequestKindType requestKind) throws ValidationException, DataNotFoundException {
		
		// Used to switch between validation methods.
		boolean useHeavy = false;  
		
		long startTime = System.currentTimeMillis();
		Map<String, Long> stepMap = new LinkedHashMap<String, Long>();
		stepMap.put("Start", startTime);
		
		
		List<String> errors = new ArrayList<String>();
		
//		/*
//		 * Validate the agreement and return if issues are encountered
//		 */
//		try {
//			
//			// Agreement VO is a heavy (slow) validation method.  Look into lightweight calls
//			AgreementVO agreementVO = null;
//			if (useHeavy && requestKind == RequestKindType.ProcessExternalPayments) {
//				agreementManagement.getAgreementVOWithObjectOID(agreementNr);
//			}
//			
//			ResultAgreementDTO lightAgreementDTO = agreementManagement.findAgreementWithSRSAgreementNr(
//					agreementNr);
//			
//			
//			stepMap.put("Get Agreement", System.currentTimeMillis());
//
//			/*
//			 * External payments must pay to own
//			 * 			
//			 */
//			boolean isPaysTo = false;
//			if (requestKind == RequestKindType.ProcessExternalPayments) {
//
//				if (logger.isDebugEnabled())
//					logger.debug("Checking paysTo");
//				if (useHeavy) {
//					PaysToDTO paysTo = agreementConverter.getPaymentDetails(agreementVO);
//					if (paysTo == null || paysTo.getPayto()!=PaysToDTO.PayToType.OWN_ACCOUNT) {
//						errors.add("Agreement must have a current account and not pay to another agreement.");
//					} else {
//						isPaysTo = true;
//					}
//				} else {
//					// TODO light weight method, test this
//					Collection<AgreementRoleDTO> roleList = agreementManagement.getAgreementRoleDTOForAgreement(agreementNr, RoleKindType.PAYSTO);
//					if (roleList.size()>0) {
//						errors.add("Agreement must have a current account and not pay to another agreement.");
//					}
//				}
//				stepMap.put("Get Pays To", System.currentTimeMillis());
//			}	
//			
//			
//			/*
//			 * External payments must not be do not pay
//			 */
//			if (requestKind == RequestKindType.ProcessExternalPayments) {
//				try {
//					if (logger.isDebugEnabled())
//						logger.debug("Do not pay");
//					if (useHeavy) {
//						PaymentSchedulerDTO schedDTO = agreementConverter.getPaymentSchedulerDTOFromAgreementVO(agreementVO);
//						if (schedDTO != null && schedDTO.isDoNotPay()) {
//							errors.add("Agreement payment scheduler may not be 'Do Not Pay'");
//						}
//					} else {
//						// TODO complete this
//						if (!isPaysTo) {
//							// Pays to would fail for a different reason
//							
//							if (agreementConverter.isPaySchedDoNotPayLight(agreementNr)) {
//								errors.add("Agreement payment scheduler may not be 'Do Not Pay'");
//							}
//						}
//						logger.warn("Not complete");
//					}
//				} catch (ConformanceTypeException e1) {
//					errors.add("An internal error occurred while validating - " + e1.getMessage());
//					logger.error("Payment scheduler validation error for agreement " 
//							+ agreementNr,e1);
//				}
//				stepMap.put("Do not Pay", System.currentTimeMillis());
//			}
//			
//			/*
//			 * External payment must have correct status
//			 */
//			if (requestKind == RequestKindType.ProcessExternalPayments) {
//				if (logger.isDebugEnabled())
//					logger.debug("Checking status");
//				String status = (lightAgreementDTO.getAgreementStatus()!=null)?lightAgreementDTO.getAgreementStatus().trim():"null";
//				if (!externalPaymentStatusSet.contains(status.toUpperCase())) {
//					errors.add("External payments may not be raised for this agreement status '" + status + "'");
//				}
//				stepMap.put("Agreement Status", System.currentTimeMillis());
//			}
//			
//			/*
//			 * External payments must have settlement in year
//			 */
//			if (requestKind == RequestKindType.ProcessExternalPayments) {
//				if (logger.isDebugEnabled())
//					logger.debug("Checking settlement");
//				
//				
//				RequestEnquirySearchDTO searchDto = new RequestEnquirySearchDTO(agreementNr, 
//						Arrays.asList(new RequestKindType[] {RequestKindType.Settle, 
//								RequestKindType.ManualSettle}), RequestStatusType.EXECUTED);
//				searchDto.setStartDate(new Date(getTaxYearStartDate().getTime()));
//				searchDto.setEndDate(new Date(System.currentTimeMillis()));
////				try {
////					if (requestEnquiryManagement.findRequests(searchDto,1,1,RequestEnquiryRowDTO.class).getResultList().size()==0) {
////						errors.add("Agreement should have been settled at least once in the tax year.");
////					}
////				} catch (RequestException e) {
////					errors.add("An internal error occurred while validating - " + e.getMessage());
////					logger.error("Search error for agreement " + agreementNr,e);
////				} catch (QueryTimeoutException e) {
////					logger.error("Search error for agreement " + agreementNr,e);
////				}
////				stepMap.put("Has settlement (taxyear)", System.currentTimeMillis());
//			}
//	 
//			
//		} catch (DataNotFoundException e) {
//			throw e;  // Throw a checked exception instead, used by bulk load
////			throw new CommunicationException("Error retrieving agreement", e);
//		} finally {
//			
//			stepMap.put("End", System.currentTimeMillis());
//			StringBuilder build = new StringBuilder();
//			if (logger.isDebugEnabled()) {
//				long t = startTime;
//				for (String s : stepMap.keySet()) {
//					build.append(String.format("%-50s", s));
//					build.append(": ");
//					build.append(stepMap.get(s)-t);
//					build.append("\n");
//					t=stepMap.get(s);
//				}
//				logger.debug("Time to Validate agreement = "
//						+ (System.currentTimeMillis() - startTime) + " milliseconds."
//						+ "\n" + build.toString());	
//			}
//		}
//		
//		/*
//		 * Throw an exception
//		 */
//		if (!errors.isEmpty()) {
//			throw new ValidationException(errors);
//		}
		
	}


	/**
	 * Validate policy transaction fields
	 * 
	 * @param agreementNr
	 * @param requestKind
	 * @param selectedItem
	 * @throws ValidationException
	 */
	public void doTransactionValidation(long agreementNr, 
			RequestKindType requestKind,
			RequestTransactionDTO dto)  throws ValidationException {
		
		List<String> errors = new ArrayList<String>();
		
		if (dto instanceof ExternalPaymentRequestDTO) {
			// Validate External payments
			errors.addAll(doValidateExternalPayment(agreementNr, (ExternalPaymentRequestDTO)dto));
		} else if (dto instanceof RecordPolicyInfoDTO || dto instanceof DistributePolicyEarningDTO) {
			// Validate Record Policy Info 
			errors.addAll(doValidatePolicyTransaction(agreementNr, dto));
		} else {
			errors.add("No Validation configured for the request kind ("+requestKind +") and selected object type ("
					+ dto.getClass().getSimpleName() + 	").");
		}

		if (!errors.isEmpty()) {
			throw new ValidationException(errors);
		}
		
	}
	
	



	/**
	 * 
	 * @param processexternalpayments
	 * @param row
	 * @return
	 */
	public String getXML(RequestKindType requestKindType,
			RequestTransactionDTO row) {
		
		if (requestKindType == RequestKindType.ProcessExternalPayments) {
			return getXMLForExternalPayment((ExternalPaymentRequestDTO)row);
		}
		throw new IllegalArgumentException("The request kind type of " + requestKindType
				+ " is not supported");
	}
	
	
	

	/*********************************************************************************************
	 * Do Class Type specific code here
	 * 
	 * 
	 ***********************************************************************************************
	 */
	
	public String getXMLForExternalPayment(ExternalPaymentRequestDTO row) {

		
		/*
		 * Convert to jaxb format
		 */
		ExternalPaymentMessageJXBO message = new ExternalPaymentMessageJXBO();
		ProcessExternalPaymentJXBO transaction = new ProcessExternalPaymentJXBO();
		message.setProcessExternalPayment(transaction);
		transaction.setDescription(row.getDescription());
		transaction.setDirectiveNumber(row.getDirectiveNumber());  // Required field
		transaction.setFullAmount(row.getFullAmount().getValue());
		transaction.setTaxAmount(row.getTaxAmount().getValue());
		transaction.setIT88Amount(row.getIt88Amount().getValue());
		transaction.setSrsId(row.getAgreementNumber());
		transaction.setEarningType(row.getEarningType());
		
		GregorianCalendar c = new GregorianCalendar();
//		c.setTime(row.getRequestedDate());
		c.setTime(new Date());
		
		try {
			transaction.setRequestedDate(DatatypeFactory.newInstance().newXMLGregorianCalendar(c));
			transaction.setEffectiveDate(DatatypeFactory.newInstance().newXMLGregorianCalendar(c));
		} catch (DatatypeConfigurationException e) {
			throw new RuntimeException("Issue processing dates",e);
		}
		
		/*
		 * Convert objects to XML string
		 */
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(ExternalPaymentMessageJXBO.class);
			Marshaller marshaller = jaxbContext.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			StringWriter writer = new StringWriter();
			marshaller.marshal(message, writer);
			return writer.toString();
		} catch (JAXBException e) {
			throw new RuntimeException("Error marshalling - " + e.getMessage(), e);
		}	
		
	}

	
	/**
	 * Validate external payment
	 * 
	 * @param dto
	 */
	public List<String> doValidateExternalPayment(long agreementNr, ExternalPaymentRequestDTO dto) {
		List<String> errors = new ArrayList<String>();
		
		/*
		 * Validate individual fields
		 */
		if (dto.getEarningType()==null) {
			errors.add("Transaction earning type is required");
		}
		
		if (dto.getFullAmount().equals(ZERO_CURRENCY)) {
			errors.add("Full amount may not be zero");
		}
		if (dto.getTaxAmount().equals(ZERO_CURRENCY)) {
			errors.add("Tax amount can not be zero");
		}
		if (dto.getIt88Amount().getValue().abs().compareTo(dto.getFullAmount().getValue().abs())>=0) {
			errors.add("IT88 amount may not be bigger than full amount.");
		}
		if (dto.getTaxAmount().getValue().abs().compareTo(dto.getFullAmount().getValue().abs())>=0) {
			errors.add("Tax amount can not be equal or bigger than the full amount");
		}
		if (getNumberOfDecimalPlaces(dto.getTaxAmount().getValue())>2
				|| getNumberOfDecimalPlaces(dto.getIt88Amount().getValue())>2
				|| getNumberOfDecimalPlaces(dto.getFullAmount().getValue())>2) {
			errors.add("Only 2 decimal spaces are allowed");
		}
		if (dto.getDirectiveNumber()==null || dto.getDirectiveNumber().trim().length()<4) {
			errors.add("Directive number is required and should be at least 4 characters long.");
		}
		return errors;
	}
	
	/**
	 * Validate policy info transactions.  This is for both DPE and Policy Info requests
	 * 
	 * @param agreementNr
	 * @param dto
	 * @return
	 */
	public List<String> doValidatePolicyTransaction(long agreementNr,
			RequestTransactionDTO dto) {

		List<String> errors = new ArrayList<String>();
		IPolicyTransactionDTO selectedObject = (IPolicyTransactionDTO) dto;
		
		String policyRefNr = (selectedObject instanceof DistributePolicyEarningDTO) ? 
				((DistributePolicyEarningDTO)selectedObject).getPolicyReference() 
					: ((RecordPolicyInfoDTO)selectedObject).getPolicyNr();
		if (policyRefNr == null || policyRefNr.trim().length() == 0) {
			errors.add("Policy Reference is Required");
		}
		
		if (selectedObject.getPolicyStartDate() == null) {
			errors.add("Policy Start Date is Required");
		}
//		TODO to sort out
		if (selectedObject.getEffectiveDate() == null) {
			errors.add("Effective Date is Required");
		}
		
		// Owner name
		String ownerName = null;
		if (selectedObject instanceof DistributePolicyEarningDTO) {
			ownerName = ((DistributePolicyEarningDTO)selectedObject).getDPELifeAssuredName();
		} else {
			ownerName = ((RecordPolicyInfoDTO)selectedObject).getLifeAssured();
		}
		if (ownerName == null || ownerName.trim().length() == 0) {
			errors.add("Owner Name is Required");
		}
		
		
		/**
		 * Policy Info specific
		 */
		if (selectedObject instanceof RecordPolicyInfoDTO) {
			/*
			 * AUM specific
			 */
			RecordPolicyInfoDTO aumDto = (RecordPolicyInfoDTO) selectedObject;
			if (aumDto.getProductCode() == null) {
				errors.add("Product Reference is Required");
			}
			
			if (aumDto.getInfoKindType()==null) {
				errors.add("Info Kind is required");
			} else {
				if (aumDto.getInfoKindType()==SRSType.POLICYINFORMATION_ASSETSUNDERMANAGEMENT) {
				
					if (aumDto.getFundCode() == null) {
						errors.add("Fund Code is Required");
					}
		
					if (aumDto.getFundUnitCount() == null || aumDto.getFundUnitCount().doubleValue() <= 0) {
						errors.add("Fund Unit Count is Rrequired");
					}
		
					if (aumDto.getFundUnitPrice() == null || aumDto.getFundUnitPrice().getValue() == null) {
						errors.add("Fund Unit Price is Rrequired");
					}
		
					if (aumDto.getPricingDate() == null) {
						errors.add("Fund Pricing Date is Required");
					}
		
					if (aumDto.getFundAssetValue() == null || aumDto.getFundAssetValue().getValue() == null) {
						errors.add("Fund Asset Value is Required");
					}
	
				} else {
					// Premium types
					if (aumDto.getAmount()==null || aumDto.getAmount().getValue().compareTo(BigDecimal.ZERO)==0) {
						errors.add("A non-zero amount is required");
					}
					if (aumDto.getPremiumFrequency()== null)
						errors.add("Premium Frequency is required");
					
					if (aumDto.getEffectiveDate()== null)
						errors.add("Effective date is required");
					
					// One more check, ensure that the component exists
					
					if (aumDto.getInfoKindType()==PolicyInfoKindType.PolicyInfoRiskPremium.getType()) {
						// Risk Premiums have additional fields
						
						if (logger.isDebugEnabled())
							logger.debug(
								"#JB - " + (aumDto.getIsLapse() != null && aumDto.getIsLapse() == 1 
								&& aumDto.getAmount() != null && aumDto.getAmount().isGreaterThan(ZERO_CURRENCY))
								+ "  ,isLapse="+aumDto.getIsLapse()
								+ "  ,amount="+aumDto.getAmount() 
								+ "  ,zero="+ZERO_CURRENCY + " - " + ZERO_CURRENCY.getCurrencyCode()
								+ "  ,premFreq="+aumDto.getPremiumFrequency()
								);
						
						
						if (aumDto.getIsLapse() != null && aumDto.getIsLapse() == 1 
								&& aumDto.getAmount() != null && aumDto.getAmount().getValue() != null 
								&& aumDto.getAmount().getValue().compareTo(ZERO_CURRENCY.getValue())>0) {
							errors.add("Lapse may only be set for negative amounts");
						}
						if (aumDto.getActivePolicyMonths() == null || aumDto.getActivePolicyMonths() == 0) {
							errors.add("Active policy months is required");
						}
						if (aumDto.getPremiumsReceivedCount()== null || aumDto.getActivePolicyMonths() == 0) {
							errors.add("Premiums received count is required");
						}					
						if (aumDto.getTerm()== null || aumDto.getTerm() <= 0 || aumDto.getTerm() > 26) {
							errors.add("Term is required and may not be more than 26");
						}
						
						if (aumDto.getPremiumFrequency() != null && aumDto.getPremiumFrequency() != 1 ) {

							// Validate that risk premiums have a limited premium frequency options linked
							//   to the selected product codes.
							//  The product actually links to PI products and not SRS products. TODO 
//							if (//riskPremiumProductTypeSet.contains(aumDto.getProductCode())	&& 
//									aumDto.getPremiumFrequency() != 1 ) {
								//TODO jzb0608 - must change this to an enumeration lookup.  However the fields
								//   are usually from a rating table, but we still need a fixed value in the code base.
								errors.add("The selected risk premium products require a Monthly premium frequency");

							
						}
					}
					
					//Added SBS0510 - For Guardbank GUI Validation
					if (aumDto.getInfoKindType() == PolicyInfoKindType.PolicyInfoGuardbankPremium.getType()) {
						// Guardbank Premiums have additional fields

						if (logger.isDebugEnabled())
							logger.debug(
									"  FundCode="+aumDto.getFundCode()
									+ "  PcrCode="+aumDto.getPcrCode()
									+ "  ,Transaction Code="+aumDto.getTransCode() 
									+ "  ,Commision Amount="+aumDto.getCommissionAmount()								
									);
						
							fieldRequiredIfNull(aumDto.getFundCode(), "Fund Code", errors);						
							fieldRequiredIfNull(aumDto.getPricingDate(), "Fund Pricing Date", errors);
							fieldRequiredIfNull(aumDto.getPcrCode(), "PCR Code", errors);
							fieldRequiredIfNull(aumDto.getTransCode(), "Transaction Code", errors);
							fieldRequiredIfNull(aumDto.getCommissionAmount(), "Commision Amount", errors);			

						
						if (aumDto.getPremiumFrequency() != null && aumDto.getPremiumFrequency() != 5 ) {
							errors.add("The selected Guardbank premium products require a Single premium frequency");

						}
					}
					
					//Added SBS0510 - For INN8 PCR GUI Validation
					if (aumDto.getInfoKindType() == PolicyInfoKindType.PolicyInformationINN8PCRPremium.getType()) {

						if (logger.isDebugEnabled())
							logger.debug(
									"  FundCode="+aumDto.getFundCode()
									+ "  ,Transaction Code="+aumDto.getTransCode() 
									+ "  ,Conversion Rate="+aumDto.getConversionRate()								
									);
						
						fieldRequiredIfNull(aumDto.getFundCode(), "Fund Code", errors);	
						fieldRequiredIfNull(aumDto.getTransCode(), "Transaction Type", errors);
						fieldRequiredIfNull(aumDto.getConversionRate(), "Conversion Rate", errors);					
					
						if (aumDto.getPremiumFrequency() != null && aumDto.getPremiumFrequency() != 5 ) {
							errors.add("The selected INN8 premium products require a Single premium frequency");


						}
					}
					
					//Added SBS0510 - For INN8 Commission GUI Validation
					if (aumDto.getInfoKindType() == PolicyInfoKindType.PolicyInformationINN8CommissionPremium.getType()) {

						if (logger.isDebugEnabled())
							logger.debug(
									"  CommTypeCode="+aumDto.getCommTypeCode()									
									+ "  ,Conversion Rate="+aumDto.getConversionRate()								
									);	
						
						fieldRequiredIfNull(aumDto.getCommTypeCode(), "Commission Type Code", errors);	
						fieldRequiredIfNull(aumDto.getConversionRate(), "Conversion Rate", errors);	
						
						if (aumDto.getPremiumFrequency() != null && aumDto.getPremiumFrequency() != 5 ) {
							errors.add("The selected INN8 premium products require a Single premium frequency");


						}
					}
					
					//VALIDATION FOR GIPP PRODUCTS- SBS0510
					//GIPP PCR
					if (aumDto.getInfoKindType() == PolicyInfoKindType.PolicyInformationGIPPCRPremium.getType()) {

						if (logger.isDebugEnabled())
							logger.debug(
									"  FundCode="+aumDto.getFundCode()
									+ "  ,Transaction Code="+aumDto.getTransCode() 
									+ "  ,Conversion Rate="+aumDto.getConversionRate()	
									+ "  ,Contribution Increase Indicator="+aumDto.getContributionIncreaseIndicator()	
									+ "  ,Investment Source="+aumDto.getInvestmentSource()	
									+ "  ,Premium Frequency="+aumDto.getPremiumFrequency()
									);
						
						fieldRequiredIfNull(aumDto.getFundCode(), "Fund Code", errors);	
						fieldRequiredIfNull(aumDto.getTransCode(), "Transaction Code", errors);
						fieldRequiredIfNull(aumDto.getContributionIncreaseIndicator(), "Contribution Increase Indicator", errors);
						fieldRequiredIfNull(aumDto.getInvestmentSource(), "Investment Source", errors);

					
						if (aumDto.getPremiumFrequency() != null && aumDto.getPremiumFrequency() != 5  && aumDto.getPremiumFrequency() != 1) {// for GIPPCR Premium Frequency should be 1-Monthly Or 5-Single
							errors.add("The selected GIP PCR premium products require a Single Or Monthly premium frequency");
						}
					}
					
					//GIPP COMMISSION
					if (aumDto.getInfoKindType() == PolicyInfoKindType.PolicyInformationGIPCommissionPremium.getType()) {

						if (logger.isDebugEnabled())
							logger.debug(
									"  CommTypeCode="+aumDto.getCommTypeCode()									
									+ "  ,Conversion Rate="+aumDto.getConversionRate()								
									+ "  ,Contribution Increase Indicator="+aumDto.getContributionIncreaseIndicator()	
									+ "  ,Investment Source="+aumDto.getInvestmentSource()	
									+ "  ,Premium Frequency="+aumDto.getPremiumFrequency()
									);
						
						fieldRequiredIfNull(aumDto.getCommTypeCode(), "Commission Type Code", errors);	
						fieldRequiredIfNull(aumDto.getContributionIncreaseIndicator(), "Contribution Increase Indicator", errors);
						fieldRequiredIfNull(aumDto.getInvestmentSource(), "Investment Source", errors);
						
						if (aumDto.getPremiumFrequency() != null && aumDto.getPremiumFrequency() != 5  && aumDto.getPremiumFrequency() != 1) {// for GIPPCOMMISSION Premium Frequency should be 1-Monthly Or 5-Single
							errors.add("The selected GIP Commissions premium products require a Single Or Monthly premium frequency");
						}
					}					
					
					//--END- GIPP-SBS0510
			
				}
			}
			
		} else if (selectedObject instanceof DistributePolicyEarningDTO) {
			/*
			 * DPE specific
			 */
			DistributePolicyEarningDTO dpeDto = (DistributePolicyEarningDTO) selectedObject;
			if (dpeDto.getCommissionKind() == null || dpeDto.getCommissionKind()==0)
				errors.add("Commission Kind is required");

			if (dpeDto.getContributionIncreaseIndicator() == null)
				errors.add("Movement Type is required");

			if (dpeDto.getPremiumFrequency()== null)
				errors.add("Premium Frequency is required");

			int productReference = 0;
			
			if (dpeDto.getProductReference() == null || dpeDto.getProductReference()==0)
				errors.add("Product Name is required");
			else
				productReference = dpeDto.getProductReference();
			
			if(dpeDto.getMovementCode() == null)
				errors.add("Movement Code is required");
			else if(dpeDto.getMovementCode().length() > 4)
				errors.add("The maximum lenght of the Movement Code value is 4");

			if (dpeDto.getAmount() == null)
				errors.add("DPE Amount is required");
			
			if (productReference != 0 && productReference == ProductReferenceType.LIFESTYLE_PROTECTOR.getProductRef()) {
				if (dpeDto.getBenefitType() == null || dpeDto.getBenefitType().intValue() == 0)
					errors.add("Benefit Type is required for the " + ProductReferenceType.LIFESTYLE_PROTECTOR.getDescription() + " product" );
			// TODO Jean - you'll have to add a lookup here
			} else if (dpeDto.getBenefitType() != null && dpeDto.getBenefitType().intValue() != 0) {
				errors.add("Benefit Type is only required for product Liberty Lifestyle Protector");
			}
			
			//SBS0510-Check if Benefit Group is applicable for the Product, then validate
			if (productReference != 0){
				DescriptionDTO prodDTO = getDescription(DescriptionKindType.PRODUCT_REFERENCE_KIND.getKind(), dpeDto.getProductReference());				
				String prodName = prodDTO != null ? prodDTO.getDescription():"";
						
				if(DPEBenefitGroupHelper.getInstance().isBenefitGroupApplicableForProduct(productReference)){
					if (dpeDto.getBenefitGroup() == null || dpeDto.getBenefitGroup().intValue() == 0)
							errors.add("Benefit Group is required for the selected product - "+prodName);
					
				} else {
					if (dpeDto.getBenefitGroup() != null && dpeDto.getBenefitGroup().intValue() != 0) {
							errors.add("Benefit Group is not applicable for the selected product - "+prodName);
						}
					
				}	
			}
			
			if(dpeDto.getCommissionTerm() != null && (dpeDto.getCommissionTerm().intValue() < 1 || dpeDto.getCommissionTerm().intValue() > 25)){
				errors.add("Invalid Commission Term value - minimum allowed value is 1 and the maximum is 25 for this field");
			}
			
			if(dpeDto.getMaxCommissionTerm() != null && (dpeDto.getMaxCommissionTerm().intValue() < 1 || dpeDto.getMaxCommissionTerm().intValue() > 25)){
				errors.add("Invalid Max Commission Term value - minimum allowed value is 1 and the maximum is 25 for this field");
			}
			
			//Validation for Commission Term,Maximum Commission Term and Upfront Commission percentage
			//All 3 fields are mandatory only if Product reference is of Kind Excelsior 2000.
			if(isEP2000Product(productReference)){
//			TODO fix the product lookup	
				if(dpeDto.getCommissionTerm() == null || dpeDto.getCommissionTerm().intValue() < 1){
					errors.add("Commission Term is required for product " + getDescription(DescriptionKindType.PRODUCT_REFERENCE_KIND.getKind(), dpeDto.getProductReference()).getDescription());
				}
				
				if(dpeDto.getMaxCommissionTerm() == null || dpeDto.getMaxCommissionTerm().intValue() < 1){
					errors.add("Max Commission Term is required for product " + getDescription(DescriptionKindType.PRODUCT_REFERENCE_KIND.getKind(), dpeDto.getProductReference()).getDescription());
				}
				
				if (dpeDto.getUpfrontCommPercentage() == null || dpeDto.getUpfrontCommPercentage().getValue().intValue() < 1) {
					errors.add("Upfront Commission % is required for product " + getDescription(DescriptionKindType.PRODUCT_REFERENCE_KIND.getKind(), dpeDto.getProductReference()).getDescription());
				}
			}
			
			if(dpeDto.getCommissionKind()!= null && ProductKindType.POLICYCOUNT.getKind() == dpeDto.getCommissionKind()){
				if(dpeDto.getNumberOfMonths() == null){
					errors.add("Number of months is required when selecting the " + ProductKindType.getProductKindType(dpeDto.getCommissionKind()).getDescription() + " Commission");
				}
			}
		}

		return errors;
	}
	
	/**
	 * TODO  jzb0608 - copied from somewhere else, but this should be a config
	 * 
	 * @param productReference
	 * @return
	 */
	private boolean isEP2000Product(int productReference){
		int [] ep2000Products = {177, 178, 179};
		for (int reference : ep2000Products) {
			if(reference == productReference)
				return true;
		}
		
		return false;
	}
	
	/*
	 * (non-Javadoc)
	 * @see za.co.liberty.business.guicontrollers.transactions.PolicyTransactionGuiController#getDescription(int, int)
	 */
	public DescriptionDTO getDescription(int descriptionKind, int refetence) {
		DescriptionDTO descriptionDTO = null;

		Description description = null;
//		description = descriptionEntityManager.findDescrtionByDescKindAndMaxRef(descriptionKind, refetence);

		if (description != null) {
			descriptionDTO = new DescriptionDTO();
			descriptionDTO.setDescription(description.getDescription());
			descriptionDTO.setUniqId(description.getUniqId());
			descriptionDTO.setReference(description.getReference());

		}
		return descriptionDTO;
	}
	/**
	 * Get the number of decimal places
	 * 
	 * @param bigDecimal
	 * @return
	 */
	private int getNumberOfDecimalPlaces(BigDecimal bigDecimal) {
	    String string = bigDecimal.stripTrailingZeros().toPlainString();
	    int index = string.indexOf(".");
	    return index < 0 ? 0 : string.length() - index - 1;
	}
	
	
	/**
	 * Broadcast the complete factor table and return the message id of the message that was
	 * sent.
	 */
	public String putBroadcastRequestTransaction(String xml) {
		return xml;
	}
	
	
	/**
	 * This method converts a request enquiry row DTO (as retrieved via request enquiry) to the appropriate 
	 * Request Transaction Type.  This is only valid for {@linkplain RequestTransactionDTO} and its subtypes.
	 * 
	 * @param objectDTO
	 * @param object
	 * 
	 * TODO jzb0608 - convert the additional properties to be on each object
	 * @return 
	 */
	public RequestTransactionDTO convertRequestEnquiryToDTO(IRequestEnquiryRow objectDTO, RequestTransactionDTO object) {
		
		if ((object instanceof DistributePolicyEarningDTO)&&(objectDTO instanceof IRequestEnquiryRow)) 	{
			
			RequestEnquiryDPERowDTO requestEnquiryDPERowDTO = (RequestEnquiryDPERowDTO)objectDTO;
			DistributePolicyEarningDTO distributePolicyEarningDTO = (DistributePolicyEarningDTO)object;
			distributePolicyEarningDTO.setProductReference(requestEnquiryDPERowDTO.getProductRef());
			distributePolicyEarningDTO.setPolicyReference(requestEnquiryDPERowDTO.getPolicyNr());
			distributePolicyEarningDTO.setPremiumFrequency(requestEnquiryDPERowDTO.getPremiumFrequencyId());
			distributePolicyEarningDTO.setContributionIncreaseIndicator(requestEnquiryDPERowDTO.getContributionIncreaseIndicator());
			distributePolicyEarningDTO.setAmount((CurrencyAmount) (requestEnquiryDPERowDTO.getAmount()==null?0: convertNumberToCurrency(requestEnquiryDPERowDTO.getAmount())));
			distributePolicyEarningDTO.setMovementCode(requestEnquiryDPERowDTO.getMovementCode());
			distributePolicyEarningDTO.setContributionIncreaseIndicator(requestEnquiryDPERowDTO.getContributionIncreaseIndicator());
			distributePolicyEarningDTO.setDPELifeAssuredName(requestEnquiryDPERowDTO.getLifeAssured());
			distributePolicyEarningDTO.setCommissionKind(requestEnquiryDPERowDTO.getCommissionKindId());
			if(requestEnquiryDPERowDTO.getMovementEffectiveDate() != null)
				distributePolicyEarningDTO.setMovementEffectiveDate(new java.sql.Date(requestEnquiryDPERowDTO.getMovementEffectiveDate().getTime()));
			if(requestEnquiryDPERowDTO.getPolicyStartDate() != null)
				distributePolicyEarningDTO.setPolicyStartDate(new java.sql.Date(requestEnquiryDPERowDTO.getPolicyStartDate().getTime()));
			
			distributePolicyEarningDTO.setGlCompanyCode(requestEnquiryDPERowDTO.getGlCompany());
			distributePolicyEarningDTO.setGrowthPensionIndicator(requestEnquiryDPERowDTO.getGrowthPensionIndicator());
			distributePolicyEarningDTO.setContributionIncreaseIndicator(requestEnquiryDPERowDTO.getContributionIncreaseIndicator());
			distributePolicyEarningDTO.setNumberOfMonths(requestEnquiryDPERowDTO.getNumberOfMounths());
			distributePolicyEarningDTO.setInForceIndicator(requestEnquiryDPERowDTO.getPolicyInForceIndicator());
			distributePolicyEarningDTO.setSourceSystemReference(requestEnquiryDPERowDTO.getSourceSystemReference());
			distributePolicyEarningDTO.setBusinessUnit(requestEnquiryDPERowDTO.getBusinessUnit());
			/*distributePolicyEarningDTO.setBenefitType(requestEnquiryDPERowDTO.getBenefitType());*/
			distributePolicyEarningDTO.setUpfrontCommPercentage(requestEnquiryDPERowDTO.getUpfrontCommissionPercentage());
			/*distributePolicyEarningDTO.setCommissionTerm(requestEnquiryDPERowDTO.getCommissionTerm());
			distributePolicyEarningDTO.setMaxCommissionTerm(requestEnquiryDPERowDTO.getMaximumCommissionTerm());*/
			
			return distributePolicyEarningDTO;
		}
		if ((object instanceof RecordPolicyInfoDTO)&&(objectDTO instanceof IRequestEnquiryAUMRowDTO))
		{
			RequestEnquiryAUMRowDTO aumRowDTO = (RequestEnquiryAUMRowDTO) objectDTO;
			RecordPolicyInfoDTO policyInfoDTO = (RecordPolicyInfoDTO) object;
			
			
			policyInfoDTO.setInfoKindType(aumRowDTO.getInfoKindType());
			policyInfoDTO.setPremiumFrequency(aumRowDTO.getPremiumFrequency());
			policyInfoDTO.setAmount(aumRowDTO.getAmount()==null?null:convertNumberToCurrency(aumRowDTO.getAmount()));
			policyInfoDTO.setFundAssetValue(aumRowDTO.getFundAssetValue()==null?null:convertNumberToCurrency(aumRowDTO.getFundAssetValue()));
			policyInfoDTO.setFundUnitCount((aumRowDTO.getFundUnitCount()!=null)?new BigDecimal(aumRowDTO.getFundUnitCount()):null);
			policyInfoDTO.setFundUnitPrice(aumRowDTO.getFundUnitPrice()==null?null:CurrencyAmountFactory.create(BigDecimalFactory.getInstance().adjustAmount(aumRowDTO.getFundUnitPrice())));
			policyInfoDTO.setInfoKind(aumRowDTO.getInfoKind());
			policyInfoDTO.setLifeAssured(aumRowDTO.getLifeAssured());
			policyInfoDTO.setPolicyNr(aumRowDTO.getPolicyNr());
			policyInfoDTO.setPolicyStartDate(aumRowDTO.getPolicyStartDate()==null?null:new java.sql.Date(aumRowDTO.getPolicyStartDate().getTime()));
			policyInfoDTO.setPricingDate(aumRowDTO.getPricingDate()==null?null:new java.sql.Date(aumRowDTO.getPricingDate().getTime()));

//			TODO comment out
//			if (aumRowDTO.getProductCodeId() == null && aumRowDTO.getProductCode()!=null) {
//				// TODO jzb0608 - this is inefficient to say the least, change it
//				ProductCodeFLO productCodeFLO = productCodesEntityManager.getProductCodeByProductCode(aumRowDTO.getProductCode());
//				if(productCodeFLO != null)
//					policyInfoDTO.setProductCode(productCodeFLO.getReference());
//			} else if (aumRowDTO.getProductCodeId()!=null) {
//				policyInfoDTO.setProductCode(aumRowDTO.getProductCodeId().intValue());
//			}
//			
//			if (aumRowDTO.getFundCode()!=null) {
//				// TODO jzb0608 - this is inefficient to say the least, change it
//				FundCodeFLO fundCodeFLO = fundCodeEntityManager.getFundCodeByCode(aumRowDTO.getFundCode());
//				if(fundCodeFLO != null) {				
//					policyInfoDTO.setFundCode(fundCodeFLO.getReference());
//					if(fundCodeFLO.getProductCode() > 0)
//						policyInfoDTO.setProductCode(fundCodeFLO.getProductCode());//Added for GBNK- SBS0510
//				}
//			}
			
			
			return policyInfoDTO;
		}
		return object;
	}
	
	
	/**
	 * Helper class for numbers
	 * 
	 * @param amount
	 * @return
	 */
	private CurrencyAmount convertNumberToCurrency(Number amount) {
		 BigDecimal payment = new BigDecimal(amount.longValue()).movePointLeft(2);
		 CurrencyAmount currencyAmount = new CurrencyAmount(payment, CurrencyEnum.ZAR);
		return currencyAmount;
	}
	
	/**
	 * Utility method to add Error for Required Field if NULL - SBS0510
	 * @param obj
	 * @param fieldName
	 * @param errors
	 */
	private void fieldRequiredIfNull(Object obj, String fieldName,List<String> errors) {
		
		if(Objects.isNull(obj)) {
			errors.add(fieldName.concat(" is Required"));
		}		
		
	}

	
	/**
	 * Return earning types EarningAndDeductionParentTypes.  Additional methods will be written for other
	 * types to ensure we limit this function to specific types.
	 * 
	 * @param parentType  		ParentType to retrieve for
	 * @param filterListForGUI	When true - Filter list of subtypes to only show valid types that may be used in GUI
	 * @return
	 */
	public List<TypeDTO> getTypesForParentType(EarningAndDeductionParentType parentType, boolean filterListForGUI) {
		throw new NotImplementedException("Not implemented in test project, check method");
	}
	
	/**
	 * Validate rule limits linked to a request but only if there are no other errors (to protect against data issues).
	 * Throws a ValidationException when there are validation issues.
	 * 
	 * @param sessionUser
	 * @param agreementNr
	 * @param requestKind
	 * @param dto
	 * @throws ValidationException  Throws this exception when there are validation issues
	 */
	public void doTransactionRuleLimitValidation(ISessionUserProfile sessionUserProfile, long agreementNr, RequestKindType requestKind,
			RequestTransactionDTO dto) throws ValidationException {
		throw new NotImplementedException("Not implemented in test project, check method");
	}
}
