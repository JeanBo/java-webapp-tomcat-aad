package za.co.liberty.business.guicontrollers.request;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import za.co.liberty.business.guicontrollers.ContextManagement;
import za.co.liberty.common.domain.ApplicationContext;
import za.co.liberty.dto.agreement.properties.CommissionKindsDTO;
import za.co.liberty.dto.agreement.request.RequestEnquiryAUMRowDTO;
import za.co.liberty.dto.agreement.request.RequestEnquiryAmountRowDTO;
import za.co.liberty.dto.agreement.request.RequestEnquiryDPERowDTO;
import za.co.liberty.dto.agreement.request.RequestEnquiryDPESearchDTO;
import za.co.liberty.dto.agreement.request.RequestEnquiryResultDTO;
import za.co.liberty.dto.agreement.request.RequestEnquiryRowDTO;
import za.co.liberty.dto.agreement.request.RequestEnquirySearchDTO;
import za.co.liberty.dto.contracting.ResultAgreementDTO;
import za.co.liberty.dto.contracting.ResultPartyDTO;
import za.co.liberty.dto.gui.context.AgreementSearchType;
import za.co.liberty.dto.gui.context.PolicyTransactionTypeEnum;
import za.co.liberty.dto.gui.request.BulkAuthDeclineResultDTO;
import za.co.liberty.dto.gui.request.FundCodeDTO;
import za.co.liberty.dto.gui.request.ProductCodeDTO;
import za.co.liberty.dto.gui.request.RequestEnquiryDTO;
import za.co.liberty.dto.gui.request.RequestEnquiryPageModelDTO;
import za.co.liberty.dto.gui.request.RequestEnquiryPeriodDTO;
import za.co.liberty.dto.gui.request.RequestUserDTO;
import za.co.liberty.dto.gui.templates.DescriptionDTO;
import za.co.liberty.dto.gui.userprofiles.RequestCategoryBulkDTO;
import za.co.liberty.dto.gui.userprofiles.RequestCategoryDividerDTO;
import za.co.liberty.dto.transaction.DistributePolicyEarningDTO;
import za.co.liberty.dto.transaction.RecordPolicyInfoDTO;
import za.co.liberty.dto.userprofiles.ISessionUserProfile;
import za.co.liberty.dto.userprofiles.RequestCategoryDTO;
import za.co.liberty.dto.userprofiles.TeamDTO;
import za.co.liberty.dto.userprofiles.TeamPartiesDTO;
import za.co.liberty.exceptions.data.DataNotFoundException;
import za.co.liberty.exceptions.data.QueryTimeoutException;
import za.co.liberty.exceptions.data.ValidationException;
import za.co.liberty.exceptions.error.request.RequestConfigurationException;
import za.co.liberty.exceptions.error.request.RequestException;
import za.co.liberty.exceptions.fatal.InconsistentConfigurationException;
import za.co.liberty.helpers.util.DateUtil;
import za.co.liberty.interfaces.agreements.AgreementKindType;
import za.co.liberty.interfaces.agreements.requests.PropertyKindType;
import za.co.liberty.interfaces.agreements.requests.RequestActionType;
import za.co.liberty.interfaces.agreements.requests.RequestDateType;
import za.co.liberty.interfaces.agreements.requests.RequestEnquiryFetchType;
import za.co.liberty.interfaces.agreements.requests.RequestKindType;
import za.co.liberty.interfaces.agreements.requests.RequestStatusType;
import za.co.liberty.interfaces.gui.IDivider;
import za.co.liberty.interfaces.gui.request.RequestEnquiryBulkAuthoriseType;
import za.co.liberty.interfaces.persistence.agreement.request.IRequestEnquiryRow;
import za.co.liberty.interfaces.rating.ci.CIDateType;
import za.co.liberty.interfaces.rating.description.DescriptionKindType;
import za.co.liberty.persistence.agreement.entity.RequestCategoryEntity;
import za.co.liberty.persistence.agreement.entity.RequestCategoryKindsEntity;
import za.co.liberty.persistence.party.entity.fastlane.PartyProfileNameFLO;
import za.co.liberty.persistence.rating.entity.Description;
import za.co.liberty.persistence.rating.entity.FundCodes;
import za.co.liberty.persistence.rating.entity.fastlane.CalculationDateFLO;
import za.co.liberty.persistence.rating.flo.ProductCodeFLO;

public class RequestEnquiryGuiController implements IRequestEnquiryGuiController {

	private static Logger logger = Logger.getLogger(RequestEnquiryGuiController.class);
	
	private static final long ENQUIRY_PERIOD_CACHE_MILLIS = 30 * 60 * 1000; 
	private static final String MONTH_DESC_CURRENT = "Current Prod Month";
	private static final String MONTH_DESC_PREVIOUS = "Previous Prod Month";
	private static final String MONTH_DESC_PREVIOUS_N = "@1 Prod Month";
	private static Long enquiryPeriodLastInitialised = null;
	
	private static List<RequestEnquiryPeriodDTO> enquiryPeriodList;
	
	private static List<RequestKindType> requestKindList;
	private static List<AgreementKindType> agreementKindList;
	private static List<RequestStatusType> requestStatusTypeList;
	private static List<RequestDateType> requestDateTypeList;
	
	private static List<RequestCategoryDTO> bulkRequestCategoryList;
	
	
	// TODO jean - this needs to move to the enum and the values must be retrieved from there
	private static Set<RequestKindType> propertyOnlyrequestKindSet	= Collections.unmodifiableSet( new HashSet<RequestKindType> (Arrays.asList(new RequestKindType[] {
			// Calculations
			RequestKindType.CalculateManagerPersistency, RequestKindType.CalculateIntermediaryPersistency, 
			RequestKindType.CalculatePersistencyBasedProductionBonus, RequestKindType.CalculateVariablePercentageAllowance, RequestKindType.CalculateSecretarialAllowance,
			RequestKindType.CalculateOfficeAllowance, RequestKindType.CalculateBrokerBranchFranchiseOverrider, 
			RequestKindType.CalculateProductionOverrider, RequestKindType.CalculateFranchiseCompetitionPool,
			RequestKindType.CalculateProductionOverriderBasedBonus,RequestKindType.CalculateFranchiseManagementFee, 
			RequestKindType.CalculateRecruitmentOverrider, RequestKindType.CalculateActivePolicyPersistency, 
			RequestKindType.CalculateAnnualProductionBonus, RequestKindType.CalculateQuarterlyProductionBonus,
			RequestKindType.CalculateQualityBonus, RequestKindType.CalculateTitularLevel, RequestKindType.CalculateTaxableBridgingFinanceFringeBenefit,
			RequestKindType.CalculateBridgingFinance, RequestKindType.CalculateLapseSuspense, RequestKindType.CalculateProvidentFundDeduction, 
			RequestKindType.CalculateConditionalActivityFee, RequestKindType.CalculateBBFPersistency, 
			RequestKindType.CalculateActivePolicyPersistency, RequestKindType.CalculateAnnualProductionBonus,
			RequestKindType.CalculateQuarterlyProductionBonus, RequestKindType.CalculateQualityBonus, 
			RequestKindType.CalculateTitularLevel, RequestKindType.CalculateTaxableBridgingFinanceFringeBenefit, 
			RequestKindType.CalculateBridgingFinance, RequestKindType.CalculateLapseSuspense, RequestKindType.CalculateProvidentFundDeduction, 
			RequestKindType.CalculateConditionalActivityFee, RequestKindType.CalculateServicingPersistency,
			RequestKindType.CalculateConsultantFinanceTaxableFringeBenefit, RequestKindType.CalculateConsultantFinance, 
			RequestKindType.CalculateUpfrontFranchiseManagementFee, RequestKindType.CalculateFranchiseExcelsior2000ManagementFee,
			RequestKindType.CalculateMedicalAidTaxableTaxableFringeBenefit, RequestKindType.CalculateAUMManagerFee,
			// Other
			RequestKindType.SubInvestmentValue, RequestKindType.AnnuityValue, RequestKindType.SubIllustrativeSurrenderValue,
			RequestKindType.SubIllustrativePaidUpValue,	RequestKindType.SubIllustrativeMaturityValue, 
			RequestKindType.SubIllustrativeDeathValue,RequestKindType.SubIllustrativeEarlyRetirementValue,
			RequestKindType.IllustrativeEarlyRetirementValue, RequestKindType.NewBusiness,RequestKindType.IllustrativeSurrenderValue,
			RequestKindType.IllustrativePaidUpValue, RequestKindType.IllustrativeMaturityValue, RequestKindType.IllustrativeDeathValue,
			RequestKindType.PolicyEarning, RequestKindType.FutureEarnings,RequestKindType.AccountForDistributionSurplusOrShortfall,
			RequestKindType.DistributePolicyEarning, RequestKindType.RecordCalculateIncentive,
//		    
			RequestKindType.TransferMoneyProvisionElement, RequestKindType.ProcessOnePercentBond, RequestKindType.ProcessIncentiveTopUp,
			RequestKindType.ProcessVariableEarningsOrDeductions, RequestKindType.RegisterFixedEarningsOrDeductions,
			RequestKindType.PAYEPaidToDate, RequestKindType.AggregateTaxableEarnings, RequestKindType.PAYE,
			RequestKindType.ProcessPaymentReply, RequestKindType.VAT,RequestKindType.ReleasePayment,
			RequestKindType.Net, RequestKindType.Settle, RequestKindType.ProcessAdvance,
			RequestKindType.TransferLoanBalance, RequestKindType.SettleLoan, RequestKindType.ChangeLoanInterestRate,
			RequestKindType.AdhocLoanRepayment, RequestKindType.InterestOnlyLoanRepayment, RequestKindType.TaxableFringeBenefit,
			RequestKindType.LoanRepayment,RequestKindType.AddLoanForIntermediary, RequestKindType.ManualSettle,
			RequestKindType.SetEarlyDebits, RequestKindType.RequestAdhocIncentiveCalculation, RequestKindType.DeclineReleasePayment,
			RequestKindType.ValidateMonthlyProductionStandard, RequestKindType.ManageRates, RequestKindType.PayBridgingFinance,
			RequestKindType.RecoverBridgingFinance,	RequestKindType.TerminateBridgingFinance,RequestKindType.MaintainBridgingFinance,
			RequestKindType.LapseSuspenseTransferIn, RequestKindType.LapseSuspenseTransferOut, RequestKindType.TerminateLapseSuspense,  
			RequestKindType.InterAccountTransfer, RequestKindType.ProcessFranchiseInterAccountTransfer, 
			RequestKindType.AdjustLapseSuspenseAccountBalance, RequestKindType.AdjustBridgingFinanceAccountBalance,
			RequestKindType.BulkProductionCreditAdjustment, RequestKindType.PAYE2, RequestKindType.SettlePAYE,
			RequestKindType.SettleConsultantFinance, RequestKindType.PayConsultantFinanceAdvance, RequestKindType.RecoverConsultantFinanceAdvance,
			RequestKindType.TerminateConsultantFinance, RequestKindType.SetManpower, RequestKindType.CorrectPoolBalance,
			RequestKindType.CalculatePoolInterest,RequestKindType.CalculateFutureEarningsInterest,
			RequestKindType.AdvancedPracticeAccountTransfer,RequestKindType.ProcessAdvancedPracticeAccountBalanceTransfer,
			RequestKindType.CalculateFranchiseTerminatedBalanceTransfer,RequestKindType.ProcessFranchiseTerminatedBalanceTransfer,
			RequestKindType.CalculatePracticeFee, 	
			RequestKindType.CalculateCommissionForPolicyInfoPremium,
			// SSM2707 Agency Pool - RDR Bridge Account
			RequestKindType.CalculateAgencyPoolInterest,
			RequestKindType.SetEffectiveIntoPoolRate,
			RequestKindType.CalculateAgencyPoolTransferIn, 
			RequestKindType.CalculateAgencyPoolTransferOut,
			RequestKindType.TransferAgencyPoolBalance
			
			
		})));
	
	/**
	 * Return list of request kinds that only properties may be viewed.
	 * 
	 * @return
	 */
	public Set<RequestKindType> getAllPropertyOnlyRequestKindSet() {
		return propertyOnlyrequestKindSet;
	}
	
	/**
	 * Sort request kinds alphabetically
	 */
	private Comparator<RequestKindType> requestKindTypeComparator = new Comparator<RequestKindType> () {
		public int compare(RequestKindType t1, RequestKindType t2) {
			return t1.getDescription().compareTo(t2.getDescription());
		}
	};
			
//	TODO look into caching this for a day only.
//	private static List<RequestEnquiryPeriodDTO> periodList; 
	
	/**
	 * Return a list of all request categories.
	 * 
	 * @return
	 */
	public List<RequestCategoryDTO> getAllRequestCategoryList() {
		 List<RequestCategoryDTO> list =  new ArrayList<RequestCategoryDTO>();
		 RequestCategoryDTO d = new RequestCategoryDTO();
		 d.setId(11200L);
		 d.setName("Jeans settlements");
		 d.setSelectedRequestKindsList(new ArrayList<RequestKindType>());
		 d.getSelectedRequestKindsList().add(RequestKindType.ManualSettle);
		 d.getSelectedRequestKindsList().add(RequestKindType.Settle);
		list.addAll(getAllBulkAuthCategories());
		return list;
	}
	
	/**
	 * Add additional bulk auth categories.
	 * 
	 * @return
	 */
	private List<RequestCategoryDTO> getAllBulkAuthCategories() {
		if (bulkRequestCategoryList==null) {
			List<RequestCategoryDTO> list = new ArrayList<RequestCategoryDTO>();
			RequestCategoryDTO d = new RequestCategoryDTO();
			d.setSelectedRequestKindsList(Arrays.asList(RequestKindType.values()));
			d.setName("All");
			d.setId(10000L);
			list.add(d);
			RequestCategoryDividerDTO divider = new RequestCategoryDividerDTO();
			divider.setName("-------- Bulk --------");
			list.add(divider);
			
			for (RequestEnquiryBulkAuthoriseType t : RequestEnquiryBulkAuthoriseType.values()) {
				RequestCategoryBulkDTO bulk = new RequestCategoryBulkDTO();
				bulk.setName(t.getDescription());
				bulk.setSelectedRequestKindsList(new ArrayList<RequestKindType>(t.getRequestKindTypeList()));
				Collections.sort(bulk.getSelectedRequestKindsList(), requestKindTypeComparator);
				list.add(bulk);
			}
			bulkRequestCategoryList = Collections.unmodifiableList(list);
		}
		return bulkRequestCategoryList;
	}

	/**
	 * Return a list of all teams.
	 * 
	 * @return
	 */
	public List<TeamDTO> getAllTeamList() {
		List<TeamDTO> l = new ArrayList<TeamDTO>();
		l.add(findUserTeamWithPartyOid(123L));
		return l;
	}
	
	/**
	 * Return a list of all request kinds.
	 * 
	 * @return
	 */
	public List<RequestKindType> getAllRequestKindTypeList() {
		if (requestKindList==null) {
			List<RequestKindType> list = new ArrayList<RequestKindType>();
			for (RequestKindType t : RequestKindType.values()) {
				list.add(t);
			}
			Collections.sort(list, requestKindTypeComparator);
			requestKindList = Collections.unmodifiableList(list);
		}
		return requestKindList;
	}
	
	/**
	 * Retreives all the valid request kind types for a request category.  Returns
	 * all request kinds if no category is defined.
	 * 
	 * @param dto
	 * @return
	 */
	public List<RequestKindType> getAllRequestKindTypeListForCategory(RequestCategoryDTO dto) {
		if (dto == null || dto instanceof IDivider) {
			return getAllRequestKindTypeList();
		}
		
		if (dto instanceof RequestCategoryBulkDTO) {
			return new ArrayList<RequestKindType>(dto.getSelectedRequestKindsList());
		}
		
//		//TODO jzb608 - will change to use businessManagement bean once Jac fixes it.
//		try {	
//			RequestCategoryEntity entity =  requestEnumEntityManager.findRequestCategory(dto.getId());
//			List<RequestKindType> list = new ArrayList<RequestKindType>();
//			for (RequestCategoryKindsEntity e : entity.getRequestCategoryKindsList()) {
//				list.add(e.getRequestKind());
//			}
//			Collections.sort(list, requestKindTypeComparator);
//			return list;
//		} catch (DataNotFoundException e) {
//			logger.warn("Unable to retrieve request kinds for category "+dto.getId());
//			// This method should only be called from the GUI and should always contain a valid id.
		
		if (dto.getSelectedRequestKindsList()!=null && dto.getSelectedRequestKindsList().size() > 0) {
			return dto.getSelectedRequestKindsList();
		}
			return new ArrayList<RequestKindType>();
//		}
	}
	
	/**
	 * Return a list of all agreement types.
	 * 
	 * @return
	 */
	public List<AgreementKindType> getAllAgreementKindTypeList() {
		if (agreementKindList==null) {
			List<AgreementKindType> list = new ArrayList<AgreementKindType>();
			for (AgreementKindType t : AgreementKindType.values()) {
				list.add(t);
			}
			agreementKindList = Collections.unmodifiableList(list);
		}
		return agreementKindList;
	}

	/**
	 * Return a list of request date types.
	 * 
	 * @return
	 */
	public List<RequestDateType> getAllRequestDateTypeList() {
		if (requestDateTypeList==null) {
			List<RequestDateType> list = new ArrayList<RequestDateType>();
			for (RequestDateType t : RequestDateType.values()) {
				list.add(t);
			}
			requestDateTypeList = Collections.unmodifiableList(list);
		}
		return requestDateTypeList;
	}
	
	
	/**
	 * Returns a list of all available Request Statusses
	 * 
	 * @return
	 */
	public List<RequestStatusType> getAllRequestStatusTypeList() {
		if (requestStatusTypeList==null) {
			List<RequestStatusType> list = new ArrayList<RequestStatusType>();
			for (RequestStatusType e : RequestStatusType.values()) {
				list.add(e);
			}
			Collections.sort(list, new Comparator<RequestStatusType> () {
				public int compare(RequestStatusType t1, RequestStatusType t2) {
					return t1.getDescription().compareTo(t2.getDescription());
				}
			});
			requestStatusTypeList = Collections.unmodifiableList(list);
		}
		return requestStatusTypeList;
	}
	
	/**
	 * Returns a list with pre-defined date ranges.
	 * 
	 * @return
	 */
	public List<RequestEnquiryPeriodDTO> getPeriodList() {
		// Cache the variables, might have issue at midnight when month-end dates change
		if (enquiryPeriodList == null || enquiryPeriodLastInitialised == null || 
				(System.currentTimeMillis()-enquiryPeriodLastInitialised) > ENQUIRY_PERIOD_CACHE_MILLIS) {
			enquiryPeriodList = initialisePeriodList();
			enquiryPeriodLastInitialised = System.currentTimeMillis();
		}
		return enquiryPeriodList;
	}
	
	/**
	 * Initialise the period list and return a reference.
	 * 
	 * @return
	 */
	private List<RequestEnquiryPeriodDTO> initialisePeriodList() {
		Date startDate = DateUtil.getInstance().addMonths(new Date(), -4);
		Date endDate = new Date();
		SimpleDateFormat dateFormat = DateUtil.getInstance().getNewDateFormat_DisplayShort();
		SimpleDateFormat monthDateFormat = new SimpleDateFormat("MMM"); 
		
		List<RequestEnquiryPeriodDTO> list = new ArrayList<RequestEnquiryPeriodDTO>();
		
		/* Get the SRS month-end dates */
//		List<CalculationDateFLO> srsList = calculationDateEntityManager
//			.findFastLaneCalculationDatesForPeriod(CIDateType.SRS_MONTHLY, startDate, endDate);
		
		List<RequestEnquiryPeriodDTO> srsList = new ArrayList<RequestEnquiryPeriodDTO>();
		
		try {
			RequestEnquiryPeriodDTO f = new RequestEnquiryPeriodDTO();
			f.setStartDate(DateUtil.getInstance().getDateFromString("2022-04-04"));
			f.setEndDate(DateUtil.getInstance().getDateFromString("2022-05-08"));
			f.setDescription(MONTH_DESC_CURRENT);
			srsList.add(f);
			
			f = new RequestEnquiryPeriodDTO();
			f.setStartDate(DateUtil.getInstance().getDateFromString("2022-03-07"));
			f.setEndDate(DateUtil.getInstance().getDateFromString("2022-04-03"));
			f.setDescription(MONTH_DESC_PREVIOUS);
			srsList.add(f);
			
			f = new RequestEnquiryPeriodDTO();
			f.setStartDate(DateUtil.getInstance().getDateFromString("2022-02-07"));
			f.setEndDate(DateUtil.getInstance().getDateFromString("2022-03-06"));
			f.setDescription(MONTH_DESC_PREVIOUS_N.replace("@1", "March"));
			srsList.add(f);
			
		} catch (ParseException e) {
			// Ignore this, we only testing
			e.printStackTrace();
		}
		

		return srsList;
	}
	
	/**
	 * Return a list of request users with uacf id's starting with the 
	 * passed parameter.
	 * 
	 * @param uacf
	 * @return
	 */
	public List<RequestUserDTO> findUsersWithUacfStartingWith(String uacf) {
		List<RequestUserDTO> fullList = new ArrayList<RequestUserDTO>();
		
		fullList.add(new RequestUserDTO(123L, "SRS1802", "Jean"));
		fullList.add(new RequestUserDTO(124L, "SRS1510", "Don't pick it"));
		
		List<RequestUserDTO> list = new ArrayList<RequestUserDTO>();
		for (RequestUserDTO flo : fullList ) {
			if (flo.getUacfId().startsWith(uacf)) {
				list.add(flo);
			}
		}
		
		return list;
	}

	/**
	 * Return a users with given partyOid 
	 * 
	 * @param uacf
	 * @return
	 */
	public RequestUserDTO findUserWithPartyOid(long partyOid) {
		RequestUserDTO d = new RequestUserDTO(partyOid, "SRS1802", "Jean B");
		return d;
//		PartyProfileNameFLO flo;
//		try {
//			flo = profileManagementBean.findUserAndNameFastLaneWithPartyOid(partyOid);
//			String name = flo.getLastName();
//			name += (flo.getFirstName()!=null)? ", "+flo.getFirstName() : "";
//			return new RequestUserDTO(flo.getPartyOid(), flo.getUacfId(), name);
//		} catch (DataNotFoundException e) {
//			// This should not happen as we are only retrieving the party details for the
//			// user that is logged in.  So the data should exist.
//			logger.error("Unable to find user with partyOid="+partyOid);
//			return null;
//		}
	}
	
	/**
	 * Validate the date range together with the search criteria.  For now we will limit normal
	 * requests to 3 months max, many requests to 1 month and DPE searches with agreement nr
	 * to 1 month and DPE searches without an agreement nr to 1 day. 
	 * 
	 * TODO jzb0608 - Add some dynamic logic here later.  We can rate a request and use it 
	 * to calculate the expected nr of results for a day.
	 * 
	 * @param searchValues
	 * @return
	 * @throws ValidationException 
	 */
	public boolean validateSearchDateRange(RequestEnquiryDTO searchValues) throws ValidationException {
		List<String> messageList = new ArrayList<String>();
		
		// Calculate the date range first.
		DateUtil dateUtil = DateUtil.getInstance();
		boolean isSearchExecuted = (searchValues.getRequestStatus()==RequestStatusType.EXECUTED || searchValues.getRequestStatus()==null);
		
		List<RequestKindType> requestKindList = null;
		if (searchValues.getRequestKind()!=null) {
			logger.info("Adding request kind to kind list "+searchValues.getRequestKind());
			requestKindList = new ArrayList<RequestKindType>();
			requestKindList.add(searchValues.getRequestKind());
		} else if (searchValues.getRequestCategory()!=null) {
			requestKindList = getAllRequestKindTypeListForCategory(searchValues.getRequestCategory());
			logger.info("Adding request categories to kind list.  Size = "+requestKindList.size());
		} else {
			throw new ValidationException("At least one request kind is required");
		}
		boolean isContainsDPE =  PolicyTransactionTypeEnum.DPE.equals(searchValues.getTransactionSearchType());//requestKindList.contains(RequestKindType.DistributePolicyEarning);
		boolean isContainsCalc = false;
		
//		boolean isContainsCalc = new ArrayList<RequestKindType>(requestKindList).
		
		boolean isSearchWithAgreement = searchValues.getAgreementNumberType() != null 
			|| (searchValues.getAgreementNumberList()!=null && searchValues.getAgreementNumberList().size() > 0)
			|| searchValues.getRequestContextType()!= null; 
		
		// Override end date to allow for longer months if end date is in future
		Date endDate = searchValues.getEndDate();
		endDate = (endDate.getTime() > System.currentTimeMillis() 
				&& searchValues.getStartDate().getTime() < System.currentTimeMillis()) 
					? dateUtil.getDatePart(new Date()) : endDate;
		
		long timeDiff = endDate.getTime()-searchValues.getStartDate().getTime();
		long nrOfDays = dateUtil.getDays(timeDiff);
		long nrOfMonths = dateUtil.getDifferenceInMonths(searchValues.getStartDate(), 
				endDate);
		if (searchValues.getRequestEnquiryPeriod()!=null) {
			// we are using a pre-determined period, overide month calculation
			nrOfMonths = 0;
		}
		
		if (!isSearchExecuted) {
			// No limit defined so fall thru
		} else if (isSearchWithAgreement) {
			if (isContainsDPE && nrOfMonths >= 12) {
				messageList.add("Searches for Executed DPE's for an Agreement is limited to 1 year.");
			} else {
				// No problem, all other searches with agreement nr's have no limit
			}
		} else if (isContainsDPE && isSearchExecuted) {
			if (nrOfDays > 0) {
				messageList.add("Searches for DPE's that are Executed are limited to 1 day.");
			} else {
				// No problem
			}
		} else if (isSearchExecuted && requestKindList.size()>1 && nrOfMonths >= 1) {
			messageList.add("Searches that include Executed status and more than one request kind are limited to 1 month");
		} else if (isSearchExecuted && requestKindList.size()==1 && nrOfMonths >= 3) {
			messageList.add("Searches that include Executed status for one request kind are limited to 3 months");
			
		} else if (isSearchExecuted && isContainsCalc && nrOfMonths >= 1) {
			messageList.add("Searches that include Executed status and calculation request kinds are limited to 1 month");
		}

		
		if (messageList.size()!=0) {
			throw new ValidationException(messageList);
		}
		
		return true;
	}
	
	/**
	 * Search request for the passed parameters.
	 * 
	 * @param searchValues
	 * @return
	 * 
	 */
	public RequestEnquiryPageModelDTO findRequests(RequestEnquiryDTO searchValues, boolean isDPERequest) 
			throws QueryTimeoutException, RequestException {

		return findRequests(searchValues, 1, isDPERequest);
	}
	
	/**
	 * Search request for the passed parameters.
	 * 
	 * @param searchValues
	 * @return
	 * 
	 */ 
	public RequestEnquiryPageModelDTO findRequestsNext(RequestEnquiryDTO searchValues, boolean isDPERequest) 
			throws QueryTimeoutException, RequestException {
		
		return findRequests(searchValues, searchValues.getLastRecordRetrieved()+1, isDPERequest);
	}
	
	/**
	 * Search request for the passed parameters.
	 * 
	 * @param searchValues
	 * @return
	 * 
	 */
	protected RequestEnquiryPageModelDTO findRequests(RequestEnquiryDTO searchValues, int startRecord, boolean isDPERequest  ) 
			throws QueryTimeoutException, RequestException {

		logger.info("Find request - Start");
		long start = System.currentTimeMillis();

		List<Long> agreementNrList = searchValues.getAgreementNumberList();

		// Determine agreementNr
		if (agreementNrList == null && searchValues.getAgreementNumber()!=null && searchValues.getAgreementNumberType()!=null) {
			// Search the agreement codes for the agreement number
			Long agreementNr = findAgreementNumber(searchValues.getAgreementNumberType(), searchValues.getAgreementNumber());
			agreementNrList = new ArrayList<Long>();
			agreementNrList.add(agreementNr);
		}

		// Determine the request kinds (at least one of the following is
		// required)
		List<RequestKindType> requestKindList = null;
		if (searchValues.getRequestKind() != null) {
			logger.info("Adding request kind to kind list " + searchValues.getRequestKind());
			requestKindList = new ArrayList<RequestKindType>();
			requestKindList.add(searchValues.getRequestKind());
		} else if (searchValues.getRequestCategory() != null) {
			requestKindList = getAllRequestKindTypeListForCategory(searchValues.getRequestCategory());
			logger.info("Adding request categories to kind list.  Size = " + requestKindList.size());
		} else {
			throw new RequestException("Request Cagegory or Request Kind is required");
		}

		/* Build search value dto */
		RequestEnquirySearchDTO searchDto = null;
		
		if (RequestKindType.DistributePolicyEarning.equals(searchValues.getRequestKind())) {
			searchDto = new RequestEnquiryDPESearchDTO();
			searchDto.setAgreementIdList(agreementNrList);
			searchDto.setRequestKindList(requestKindList);
			searchDto.setDateType(searchValues.getRequestDateType());
			searchDto.setStartDate(searchValues.getStartDate());
			searchDto.setEndDate(searchValues.getEndDate());
			searchDto.setRequestStatus(searchValues.getRequestStatus());

			if (searchValues.getCommissionKind() != null && searchValues.getCommissionKind().getId() != 0) {
				((RequestEnquiryDPESearchDTO) searchDto).setComponentKindList(Arrays.asList(searchValues.getCommissionKind().getId()));
			}
		}else {
			searchDto = new RequestEnquirySearchDTO(agreementNrList, requestKindList, searchValues.getRequestDateType(), searchValues.getStartDate(), searchValues.getEndDate(), searchValues.getRequestStatus());
		}

		searchDto.setWorkFlowNr(searchValues.getWorkflowNumber());

		logger.info("User=" + searchValues.getUser() + ",  Team=" + searchValues.getTeam());

		// Configure requestor and authoriser lists
		if (searchValues.getUser() != null) {
			long rolePlayer = searchValues.getUser().getPartyOid();
			List<Long> roleList = new ArrayList<Long>();
			roleList.add(rolePlayer);
			logger.info("Setting user " + rolePlayer);
			searchDto.setRolePlayerIdList(roleList);
		}
		if (searchValues.getTeam() != null) {
			// Add all team members
			List<Long> roleList = new ArrayList<Long>();
			for (TeamPartiesDTO dto : searchValues.getTeam().getSelectedTeamPartiesList()) {
				roleList.add(dto.getPartyOID());
			}
			searchDto.setRolePlayerIdList(roleList);
		}
		if (searchValues.getAgreementKind() != null) {
			searchDto.setAgreementKind(searchValues.getAgreementKind().getKind());
		}

		if (searchValues.getContributionIncreaseIndicator() != null) {
			searchDto.setContributionIndicator(searchValues.getContributionIncreaseIndicator().getReference());
		}

		if (searchValues.getProductReference() != null && searchValues.getProductReference().getReference() > 0) {
			searchDto.setProductReference(searchValues.getProductReference().getReference());
		}
		if(searchValues.getFundCode() != null){
			searchDto.setFundCode(searchValues.getFundCode().getId());
		}
		if(searchValues.getProductCode() != null){
			searchDto.setProductCode(searchValues.getProductCode().getId());
		}
		if(searchValues.getInfoKindSearchType() != null){
			searchDto.setInfoKind(searchValues.getInfoKindSearchType().toString());
		}
		if(searchValues.getPolicyReference() != null){
			searchDto.setPolicyReference(searchValues.getPolicyReference());
		}
		
		if(searchValues.getCommissionKind() != null && searchValues.getCommissionKind().getId() != 0){
			searchDto.setCommissionKind(searchValues.getCommissionKind().getId());
		}
		
		if (searchValues.getStartDate() != null || searchValues.getEndDate() != null) {
			if (searchValues.getRequestStatus() == null) {
				searchDto.setDateType(RequestDateType.REQUEST);
			} else if (RequestDateType.EXECUTED.equals(searchValues.getRequestStatus()))
				searchDto.setDateType(RequestDateType.EXECUTED);
			else if (RequestDateType.REQUESTED.equals(searchValues.getRequestStatus()))
				searchDto.setDateType(RequestDateType.REQUESTED);
			else if (RequestDateType.REQUEST.equals(searchValues.getRequestStatus()))
				searchDto.setDateType(RequestDateType.REQUEST);
			else 
				searchDto.setDateType(RequestDateType.REQUESTED);

		}

		// Is this perhaps a Bulk Auth search?
		Set<RequestEnquiryBulkAuthoriseType> bulkAuthTypeSet = RequestEnquiryBulkAuthoriseType.getBulkAuthoriseTypesForKinds(requestKindList.toArray(new RequestKindType[] {}));
		RequestEnquiryBulkAuthoriseType bulkAuthType = (bulkAuthTypeSet != null && bulkAuthTypeSet.size() == 1) ? bulkAuthTypeSet.iterator().next() : null;

		List<IRequestEnquiryRow> listResult = new ArrayList<IRequestEnquiryRow>();
		RequestEnquiryAmountRowDTO d = new RequestEnquiryAmountRowDTO();
		d.setRequestKindType(RequestKindType.MaintainPartyDetails);
		d.setAgreementKindType(AgreementKindType.AGENT);
		d.setStatusType(RequestStatusType.EXECUTED);
		d.setAgreementNr(145890L);
		d.setPartyName("Piet Pompies");
		d.setPartyOid(1001L);
		d.setRequestor("Jean1");
		d.setAuthoriser1("Jean1");
		d.setRequestDate(DateUtil.getInstance().getTodayDatePart());
		d.setRequestedDate(d.getRequestDate());
		d.setExecutedDate(d.getRequestDate());
		d.setAdditionalPropertyMap(new HashMap<PropertyKindType, Object>());
		d.setAmount(new BigDecimal(100));
		d.setSortAmount(100L);
		d.setRequestId(100L);
		listResult.add(d);
		
		
		d = new RequestEnquiryAmountRowDTO();
		d.setRequestKindType(RequestKindType.ProcessExternalPayments);
		d.setAgreementKindType(AgreementKindType.AGENT);
		d.setStatusType(RequestStatusType.EXECUTED);
		d.setAgreementNr(145890L);
		d.setPartyOid(1002L);
		d.setPartyName("Piet Pompies2");
		d.setRequestor("Jean2");
		d.setAuthoriser1("Jean2");
		d.setRequestDate(DateUtil.getInstance().getTodayDatePart());
		d.setRequestedDate(d.getRequestDate());
		d.setExecutedDate(d.getRequestDate());
		d.setAdditionalPropertyMap(new HashMap<PropertyKindType, Object>());
		d.setAmount(new BigDecimal(100));
		d.setSortAmount(100L);
		d.setRequestId(200L);
		listResult.add(d);
		
		d = new RequestEnquiryAmountRowDTO();
		d.setRequestKindType(RequestKindType.RecordPolicyInfo);
		d.setAgreementKindType(AgreementKindType.AGENT);
		d.setStatusType(RequestStatusType.EXECUTED);
		d.setAgreementNr(145890L);
		d.setPartyOid(1003L);
		d.setPartyName("Piet Pompies3");
		d.setRequestor("Jean3");
		d.setAuthoriser1("Jean3");
		d.setRequestDate(DateUtil.getInstance().getTodayDatePart());
		d.setRequestedDate(d.getRequestDate());
		d.setExecutedDate(d.getRequestDate());
		d.setAdditionalPropertyMap(new HashMap<PropertyKindType, Object>());
		d.setAmount(new BigDecimal(101));
		d.setSortAmount(101L);
		d.setRequestId(300L);
		listResult.add(d);
		
		d = new RequestEnquiryAmountRowDTO();
		d.setRequestKindType(RequestKindType.CalculateAgencyPoolInterest);
		d.setAgreementKindType(AgreementKindType.AGENT);
		d.setStatusType(RequestStatusType.EXECUTED);
		d.setAgreementNr(145890L);
		d.setPartyOid(1003L);
		d.setPartyName("View Properties");
		d.setRequestor("Props");
		d.setAuthoriser1("Props");
		d.setRequestDate(DateUtil.getInstance().getTodayDatePart());
		d.setRequestedDate(d.getRequestDate());
		d.setExecutedDate(d.getRequestDate());
		d.setAdditionalPropertyMap(new HashMap<PropertyKindType, Object>());
		d.setAmount(new BigDecimal(101));
		d.setSortAmount(101L);
		d.setRequestId(400L);
		listResult.add(d);
		
		// Do the search
		RequestEnquiryResultDTO resultDto = new RequestEnquiryResultDTO(false, listResult, 1, 1);
		
		
		logger.info("Find request - end.  Found " + resultDto.getResultList().size() + " and took " + (System.currentTimeMillis() - start) + " milli seconds");
		return new RequestEnquiryPageModelDTO(resultDto, bulkAuthType);
	}
	
	
	/**
	 * Find the agreement number for the passed parameters.  Throws an exception if not found or
	 * if the format is incorrect.
	 * 
	 * @param agreementNumberType
	 * @param agreementNumber
	 * @return
	 * @throws RequestException
	 */
	private Long findAgreementNumber(AgreementSearchType agreementNumberType, String agreementNumber) 
			throws RequestException {
		return 14590L;
	}

	/**
	 * Convert the String agreement nr value to a Long
	 * 
	 * @param value
	 * @return
	 * @throws RequestException
	 */
	private Long convertAgreementToLong(String value) throws RequestException {
		try {
			return Long.parseLong(value);
		} catch (NumberFormatException e) {
			throw new RequestException("Agreement number has incorrect format");
		}
	}
	
	/**
	 * Find the TeamDTO for a party oid.  Returns null if nothing found.
	 * 
	 * @param partyOid
	 */
	public TeamDTO findUserTeamWithPartyOid(long partyOid) {
		TeamDTO d = new TeamDTO();
		d.setOid(partyOid);
		d.setTeamName("Jean's team");
		TeamPartiesDTO dp = new TeamPartiesDTO();
		dp.setId(123L);
		dp.setParty(new ContextManagement().newExampleResultPartyDTO());
		d.getSelectedTeamPartiesList().add(dp);
		return d;
	}
	
	/**
	 * Bulk authorise the passed requests.  Some additional validation is done here.
	 * 
	 * @param sessionUser
	 * @param selectedRequestList
	 * @throws ValidationException 
	 */
	public BulkAuthDeclineResultDTO bulkAuthoriseRequests(ISessionUserProfile sessionUser, 
			List<RequestEnquiryRowDTO> selectedRequestList)  {

		List<RequestEnquiryRowDTO> successIdList = new ArrayList<RequestEnquiryRowDTO>();
		Map<RequestEnquiryRowDTO, String> failureIdMessageMap = new HashMap<RequestEnquiryRowDTO, String>();
		
		// Validate if the specified request kinds may be authorised
		Set<RequestKindType> requestTypeSet = new HashSet<RequestKindType>();
		for (RequestEnquiryRowDTO dto : selectedRequestList) {
			RequestKindType requestType = dto.getRequestKindType();
			if (requestTypeSet.contains(requestType)) {
				continue;
			}
			requestTypeSet.add(requestType);
			// Ensure that we have access
			if (!sessionUser.isAllowAuthorise(requestType)) {
				failureIdMessageMap.put(dto, RequestActionType.AUTHORISE
						.getRequestSecurityErrorMessage(requestType));
				return new BulkAuthDeclineResultDTO(failureIdMessageMap.size()==0, successIdList, failureIdMessageMap);
			}
			
		}
		
//		// Authorise each request
//		for (RequestEnquiryRowDTO dto : selectedRequestList) {		
//			try {
//				if (dto.getRequestKindType().isNewGuirequest()) {
//					guiRequestManagementBean.authoriseRequestForGuiRequestNewTransaction(new ApplicationContext(), 
//							sessionUser, dto.getRequestId());
//				} else {
//					requestManagementBean.authoriseRequestNewTransaction(new ApplicationContext(),
//							dto.getRequestKindType(), dto.getRequestId(), sessionUser);
//				}
//				successIdList.add(dto);
//			} catch (RequestConfigurationException e) {
//				failureIdMessageMap.put(dto, e.getMessage());
//			} catch (RequestException e) {
//				failureIdMessageMap.put(dto, e.getMessage());
//			} catch (DataNotFoundException e) {
//				failureIdMessageMap.put(dto, e.getMessage());
//			}
//		}
		
		// Updated the statusses 
		if (successIdList.size() > 0) {
			// Updated the status - bulk auth is in new transactions so the weird status update
			// 	  issue should not occur here.
			try {
				refresh(successIdList);
			} catch (RequestException e) {
				// Ignore this
			} catch (QueryTimeoutException e) {
				// Ignore this
			}
		}
		
		return new BulkAuthDeclineResultDTO(failureIdMessageMap.size()==0, successIdList, failureIdMessageMap);
	}
	
	
	/**
	 * Bulk decline the passed requests.  Some additional validation is done here.
	 * 
	 * @param sessionUser
	 * @param selectedRequestList
	 * @throws ValidationException 
	 */
	public BulkAuthDeclineResultDTO bulkDeclineRequests(ISessionUserProfile sessionUser, 
			List<RequestEnquiryRowDTO> selectedRequestList) {

		List<RequestEnquiryRowDTO> successIdList = new ArrayList<RequestEnquiryRowDTO>();
		Map<RequestEnquiryRowDTO, String> failureIdMessageMap = new HashMap<RequestEnquiryRowDTO, String>();
		
		// Validate if the specified request kinds may be authorised
		Set<RequestKindType> requestTypeSet = new HashSet<RequestKindType>();
		for (RequestEnquiryRowDTO dto : selectedRequestList) {
			RequestKindType requestType = dto.getRequestKindType();
			if (requestTypeSet.contains(requestType)) {
				continue;
			}
			requestTypeSet.add(requestType);
			// Ensure that we have access
			if (!sessionUser.isAllowDecline(requestType)) {
				failureIdMessageMap.put(dto, RequestActionType.DECLINE
						.getRequestSecurityErrorMessage(requestType));
				return new BulkAuthDeclineResultDTO(failureIdMessageMap.size()==0, successIdList, failureIdMessageMap);
			}
			
		}
		
//		// Authorise each request
//		for (RequestEnquiryRowDTO dto : selectedRequestList) {		
//			try {
//				
//				if (dto.getRequestKindType().isNewGuirequest()) {
//					guiRequestManagementBean.declineRequestForGuiRequestNewTransaction(new ApplicationContext(), 
//							sessionUser, dto.getRequestId());
//				} else {
//					requestManagementBean.declineRequestNewTransaction(new ApplicationContext(),
//							dto.getRequestKindType(), dto.getRequestId(), sessionUser);
//				}
//				successIdList.add(dto);
//			} catch (RequestConfigurationException e) {
//				failureIdMessageMap.put(dto, e.getMessage());
//			} catch (RequestException e) {
//				failureIdMessageMap.put(dto, e.getMessage());
//			} catch (DataNotFoundException e) {
//				failureIdMessageMap.put(dto, e.getMessage());
//			}
//		}
		
		// Updated the statusses 
		if (successIdList.size() > 0) {
			// Updated the status - bulk auth is in new transactions so the weird status update
			// 	  issue should not occur here.
			try {
				refresh(successIdList);
			} catch (RequestException e) {
				// Ignore this
			} catch (QueryTimeoutException e) {
				// Ignore this
			}
		}
		
		return new BulkAuthDeclineResultDTO(failureIdMessageMap.size()==0, successIdList, failureIdMessageMap);
	}
	
	/**
	 * Update the passed list of DTO's with the latest data from the database.  Ensure 
	 * you do this in a seperate transaction after authorisations & declines have been
	 * commited.
	 * 
	 * @param selectedRequestList
	 * @throws QueryTimeoutException 
	 * @throws RequestException 
	 */
	@SuppressWarnings("unchecked")
	public void refresh(List<RequestEnquiryRowDTO> selectedRequestList) throws RequestException, QueryTimeoutException {
		
		Map<Long, RequestEnquiryRowDTO> map = new HashMap<Long, RequestEnquiryRowDTO>();
		List<Long> requestIds = new ArrayList<Long>();
			
		// Do some pre-processing
		for (RequestEnquiryRowDTO dto : selectedRequestList) {
			map.put(dto.getRequestId(), dto);
			requestIds.add(dto.getRequestId());
		}
		
		// Search for these objects
		RequestEnquirySearchDTO searchDto = new RequestEnquirySearchDTO();
		searchDto.setRequestIdList(requestIds);

		List<? extends IRequestEnquiryRow> resultList;
//		resultList = requestEnquiryManagement.findRequests(searchDto).getResultList();
//	
//		for (RequestEnquiryRowDTO row : ((List<RequestEnquiryRowDTO>)resultList)) {
//			// Update the current requests
//			RequestEnquiryRowDTO dto = map.get(row.getRequestId());
//			if (dto==null) {
//				logger.warn("Unable to find request id " + row.getRequestId() + " to update result");
//				continue;
//			}
//			dto.setAuthoriser1(row.getAuthoriser1());
//			dto.setAuthoriser2(row.getAuthoriser2());
//			dto.setAuthoriserDate1(row.getAuthoriserDate1());
//			dto.setAuthoriserDate2(row.getAuthoriserDate2());
//			dto.setAuthoriserId1(row.getAuthoriserId1());
//			dto.setAuthoriserId2(row.getAuthoriserId2());
//			dto.setStatusType(row.getStatusType());
//			
//		}

	}
	
	/**
	 * Returns a list of valid commission kinds
	 */
	public List<CommissionKindsDTO> getAllCommissionKinds() {
	 
		List<CommissionKindsDTO> list = new ArrayList<CommissionKindsDTO>();
		CommissionKindsDTO d = new CommissionKindsDTO(4, "First Year Commission");
		list.add(d);
		d = new CommissionKindsDTO(4049, "As And When Commission");
		list.add(d);
		return list;
//		throw new IllegalArgumentException();
//		List<CommissionKindsDTO> commissionKinds = agreementValuesFactory.getValidCommKindList();
//		
//		return commissionKinds;
		
	}
	
	/**
	 * Returns a list of product references
	 */
	public List<DescriptionDTO> getAllProductReferences() {
		DescriptionKindType t = DescriptionKindType.PRODUCT_KIND;
		List<DescriptionDTO> list = new ArrayList<DescriptionDTO>();
		DescriptionDTO d = new DescriptionDTO();
		d.setDescription("First Year Commission");
		d.setDescriptionKind(t.getKind());
		d.setName(t.getName());
		d.setReference(37);
		d.setUniqId(1008);
		
		list.add(d);
		
		return list;
//		throw new IllegalArgumentException();
//		List<DescriptionDTO> productReferences = new ArrayList<DescriptionDTO>();
//		DescriptionDTO descriptionDTO = null;
//		
//		List<Description> descriptions = descriptionEntityManager.findValuesByDescriptionKind(DescriptionKindType.PRODUCT_REFERENCE_KIND.getKind());
//		if(descriptions != null && !descriptions.isEmpty()){
//			for (Description description : descriptions) {
//				descriptionDTO = new DescriptionDTO();
//				descriptionDTO.setDescription(description.getDescription());
//				descriptionDTO.setUniqId(description.getUniqId());
//				descriptionDTO.setReference(description.getReference());
//				productReferences.add(descriptionDTO);
//			}
//		}
//		
//		return productReferences;
		
	}

	public List<DescriptionDTO> getAllContributionIncIndicatorList() {
		DescriptionKindType t = DescriptionKindType.CONTRIBUTION_INCREASE_INDICATOR;
		List<DescriptionDTO> list = new ArrayList<DescriptionDTO>();
		DescriptionDTO d = new DescriptionDTO();
		d.setDescription("Basic");
		d.setDescriptionKind(t.getKind());
		d.setName(t.getName());
		d.setReference(0);
		d.setUniqId(145);
		list.add(d);
		
		d = new DescriptionDTO();
		d.setDescription("AdHoc");
		d.setDescriptionKind(t.getKind());
		d.setName(t.getName());
		d.setReference(1);
		d.setUniqId(146);
		list.add(d);
		
		d = new DescriptionDTO();
		d.setDescription("ACI");
		d.setDescriptionKind(t.getKind());
		d.setName(t.getName());
		d.setReference(2);
		d.setUniqId(147);
		list.add(d);
		
		return list;
		
//		throw new IllegalArgumentException();
//		List<DescriptionDTO> contributionIncrIndicators = new ArrayList<DescriptionDTO>();
//		DescriptionDTO descriptionDTO = null;
//		
//		List<Description> descriptions = descriptionEntityManager.findValuesByDescriptionKind(DescriptionKindType.CONTRIBUTION_INCREASE_INDICATOR.getKind());
//		if(descriptions != null && !descriptions.isEmpty()){
//			for (Description description : descriptions) {
//				descriptionDTO = new DescriptionDTO();
//				descriptionDTO.setDescription(description.getDescription());
//				descriptionDTO.setUniqId(description.getUniqId());
//				descriptionDTO.setReference(description.getReference());
//				contributionIncrIndicators.add(descriptionDTO);
//			}
//		}
//		
//		return contributionIncrIndicators;
		
	}
	
	public List<ProductCodeDTO> getAllProductCodes(){
		List<ProductCodeDTO> productCodeDTOs = new ArrayList<ProductCodeDTO>();
		
		ProductCodeDTO productCodeDTO = null;
		
		List<ProductCodeFLO> productCodeList = new ArrayList<ProductCodeFLO>();

		productCodeDTO = new ProductCodeDTO();
		productCodeDTO.setBatch("PROD1");
		productCodeDTO.setId(321L);
		productCodeDTO.setProductCode("321");
		productCodeDTO.setProductDescription("321 Product");
		productCodeDTO.setReference(Long.valueOf(321));
		productCodeDTOs.add(productCodeDTO);
	
		
		return productCodeDTOs;
	}

	public List<FundCodeDTO> getAllFundCodes() {
		
		List<FundCodeDTO> fundCodes = new ArrayList<FundCodeDTO>();
		
		FundCodeDTO fundCodeDTO = null;
		
		List<FundCodes> fundCodesList = new ArrayList<FundCodes>();

		fundCodeDTO = new FundCodeDTO();
		fundCodeDTO.setBatch("BATCH1");
		fundCodeDTO.setFundCode("FUND1");
		fundCodeDTO.setFundDescription("FUND1 DESC");
		fundCodeDTO.setId(1122L);
		fundCodeDTO.setReference(123L);
		fundCodes.add(fundCodeDTO);

		
		return fundCodes;
	}
	
	public Object initialiseRequestViewModel(IRequestEnquiryRow rowDto, RequestKindType requestKind) {
		Object policyTransationDto = null;

		if (requestKind.equals(RequestKindType.DistributePolicyEarning))
			policyTransationDto = new DistributePolicyEarningDTO();
		else if(requestKind.equals(RequestKindType.RecordPolicyInfo))
			policyTransationDto = new RecordPolicyInfoDTO();

//		try {
////			entity = guiRequestManagementBean.findGuiRequestEntityWithRequestId(rowDto.getRequestId());
//			requestManagementBean.copyRequestPropertiesToObject(requestManagementBean.findRequestVO(rowDto.getRequestId()), policyTransationDto);
//
////			policyTransationDto = guiRequestManagementBean.retrieveDTOFromGuiRequest(entity, GuiRequestImageTypeEntity.CurrentImage);
//		} catch (RequestException e) {
//			throw new InconsistentConfigurationException("Unable to retrieve request data, please contact system staff", e);
//		} catch (RequestConfigurationException e) {
//			throw new InconsistentConfigurationException("Unable to retrieve request data, please contact system staff", e);
//		}

		return policyTransationDto;
	}


}
