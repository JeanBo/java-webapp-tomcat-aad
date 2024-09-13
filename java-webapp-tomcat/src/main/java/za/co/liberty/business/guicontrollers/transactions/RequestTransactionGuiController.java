package za.co.liberty.business.guicontrollers.transactions;

import java.math.BigDecimal;
import java.text.ParseException;
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

import javax.ejb.Stateless;

import org.apache.log4j.Logger;

import za.co.liberty.agreement.client.vo.RequestVO;
import za.co.liberty.agreement.config.AgreementContext;
import za.co.liberty.agreement.config.AgreementHomeMetaDataFactory;
import za.co.liberty.business.request.RequestTransactionManagement;
import za.co.liberty.business.request.util.IIaaXmlUtilProxy;
import za.co.liberty.business.request.util.IaaXmlUtilProxy;
import za.co.liberty.common.domain.CurrencyAmount;
import za.co.liberty.common.domain.Percentage;
import za.co.liberty.common.enums.CurrencyEnum;
import za.co.liberty.dto.agreement.request.RequestEnquiryRowDTO;
import za.co.liberty.dto.contracting.ResultAgreementDTO;
import za.co.liberty.dto.contracting.ResultPartyDTO;
import za.co.liberty.dto.pretransactionreject.RejectElementDTO;
import za.co.liberty.dto.spec.TypeDTO;
import za.co.liberty.dto.transaction.DistributePolicyEarningDTO;
import za.co.liberty.dto.transaction.ExternalPaymentRequestDTO;
import za.co.liberty.dto.transaction.IPolicyTransactionModel;
import za.co.liberty.dto.transaction.ProcessAdvanceRequestDTO;
import za.co.liberty.dto.transaction.RecordPolicyInfoDTO;
import za.co.liberty.dto.transaction.RequestTransactionDTO;
import za.co.liberty.dto.transaction.SettleRequestDTO;
import za.co.liberty.dto.transaction.VEDTransactionDTO;
import za.co.liberty.dto.userprofiles.ContextAgreementDTO;
import za.co.liberty.dto.userprofiles.ISessionUserProfile;
import za.co.liberty.exceptions.data.DataNotFoundException;
import za.co.liberty.exceptions.data.QueryTimeoutException;
import za.co.liberty.exceptions.data.ValidationException;
import za.co.liberty.exceptions.error.request.RequestException;
import za.co.liberty.helpers.util.DateUtil;
import za.co.liberty.interfaces.agreements.AgreementStatusType;
import za.co.liberty.interfaces.agreements.PolicyInfoKindType;
import za.co.liberty.interfaces.agreements.requests.EarningAndDeductionParentType;
import za.co.liberty.interfaces.agreements.requests.PropertyKindType;
import za.co.liberty.interfaces.agreements.requests.RequestKindType;
import za.co.liberty.srs.util.rating.CachingRatingBPOFactory;
import za.co.liberty.srs.util.rating.distributepolicyearnings.DistributionRatingUtil;
import za.co.liberty.srs.vo.earningdeduction.EarningDeductionVO;
import za.co.liberty.srs.vo.policyinfo.PolicyInformationTransactionVO;
import za.co.liberty.srs.vo.pretransaction.PreTransactionVO;

/**
 * GUI Controller for all request level transactions
 * 
 * @author jzb0608 - July, 2017
 *
 */
@Stateless
public class RequestTransactionGuiController implements IRequestTransactionGuiController {

	
	private static final long serialVersionUID = 1L;
	
	static Logger logger = Logger.getLogger(RequestTransactionGuiController.class);
	
	
	
	private static CurrencyAmount ZERO_CURRENCY = new CurrencyAmount(BigDecimal.ZERO, CurrencyEnum.ZAR);
	private static List<TypeDTO> validExternalPaymentTransactionTypeList;
	private static Date taxYearStart;
	
	private static Integer ZERO = 0;
	private static Integer ONE = 1;
	
	private static Set<String> externalPaymentStatusSet = new HashSet<String>();
	static {
		externalPaymentStatusSet.add(AgreementStatusType.ACTIVE.getDescription().toUpperCase());
		externalPaymentStatusSet.add(AgreementStatusType.RETIRED.getDescription().toUpperCase());
	}
	
	private List<TypeDTO> fullEarningTypeList = new ArrayList<TypeDTO>();

	private Map<EarningAndDeductionParentType, List<TypeDTO>> earningMaps;

	/**
	 * Add any types here that must be excluded when returning types to the front-end
	 */
	private static final List<Long> VED_TYPE_EXCLUSION_LIST = Arrays.asList(new Long[] {
			11044l, 11046l, 11063l, 11064l, 460l});
	
	// 405
	private static String[][] VED_EARNINGS = new String[][] {
		{"11000","VariableEarningSecretarialAllowanceAdjustment","Secretarial Allowance Adjustment"},
		{"11001","VariableEarningPersistencyBasedProductionBonusAdjustment","Persistency Based Production Bonus Adjustment"},
		{"11002","VariableEarningAllowanceACIAdjustment","ACI Allowance Adjustment"},
		{"11003","VariableEarningAllowanceCorporateRenewalAdjustment","Corporate Renewal Allowance Adjustment"},
		{"11004","VariableEarningOnePercentBondAdjustment","1% Bond Adjustment"},
		{"11005","VariableEarningAgencyManagersProductionOverriderAdjustment","Agency Managers Production Overrider Adjustment"},
		{"11006","VariableEarningAgencyLeavePay","Agency Leave Pay"},
		{"11007","VariableEarningBBFOverriderAdjustment","BBF Overrider Adjustment"},
		{"11008","VariableEarningBrokerLeavePay","Broker Leave Pay"},
		{"11009","VariableEarningHouseAllowance","House Allowance"},
		{"11010","VariableEarningRecruitmentOverriderAdjustment","Recruitment Overrider Adjustment"},
		{"11011","VariableEarningYearEndBonus","Year End Bonus"},
		{"11012","VariableEarningAgencyAnnualBonus","Agency Annual Bonus"},
		{"11013","VariableEarningSinglePremium","Single Premium"},
		{"11014","VariableEarningOverriderAchievement","Overrider Achievement"},
		{"11015","VariableEarningGroupProductionBonus","Group Production Bonus"},
		{"11016","VariableEarningBrokerAnnualBonus","Broker Annual Bonus"},
		{"11017","VariableEarningOfficeAllowanceAdjustment","Office Allowance Adjustment"},
		{"11018","VariableEarningStarBonus","Star Bonus"},
		{"11019","VariableEarningBrokerProductionBonus","Broker Production Bonus"},
		{"11020","VariableEarningAgencyManagersOverriderBasedProductionBonusAdjustment","Agency Manager's Overrider Based Production Bonus Adjustment"},
		{"11021","VariableEarningOverriderBMC","Overrider Broker Managers"},
		{"11022","VariableEarningOverriderBCC","Overrider Broker Consultants"},
		{"11023","VariableEarningProductionBonus","Production Bonus"},
		{"11024","VariableEarningTopUpCumulative","TopUp - Cumulative"},
		{"11025","VariableEarningTopUpMonthly","TopUp - Monthly"},
		{"11026","VariableEarningBrokerDevelopmentBonus","Broker Development Bonus"},
		{"11027","VariableEarningBrokerConsultantProductionBonus","Broker Consultant Production Bonus"},
		{"11028","VariableEarningBrokerConsultantDirectBonus","Broker Consultant Direct Bonus"},
		{"11029","VariableEarningTransferAllowance","Transfer Allowance"},
		{"11030","VariableEarningFranchiseManagementFeeAdjustment","Franchise Management Fee Adjustment"},
		{"11031","VariableEarningFranchiseCompetitionPoolAdjustment","Franchise Competition Pool Adjustment"},
		{"11032","VariableEarningAddBMProductionBonus","Broker Manager Production Bonus"},
		{"11033","VariableEarningBCOverrider","Broker Consultant Overrider"},
		{"11034","VariableEarningLUTOptions","LUT Options"},
		{"11035","VariableEarningLongServiceAwardNonTaxable","Long Service Award - NonTaxable"},
		{"11036","VariableEarningRetentionAwardTaxable","Retention Award - Taxable"},
		{"11037","VariableEarningChairmansPrize","Chairman's Prize"},
		{"11038","VariableEarningBrokerBasicSalaryAdjustment","Broker Basic Salary Adjustment"},
		{"11039","VariableEarningBrokerAgencySalaryAdjustment","Agency Basic Salary Adjustment"},
		{"11040","VariableEarningCarAllowanceAdjustment","Car Allowance Adjustment"},
		{"11041","VariableEarningInterest","Interest"},
		{"11042","VariableEarningMiscellaneousNonTaxable","Non Taxable Earning"},
		{"11043","VariableEarningMiscellaneousTaxable","Taxable Earning"},
		{"11044","VariableEarningInRespectOfPriorTaxYear","Prior Tax Year Earnings"},
		{"11045","VariableEarningMiscellaneousTaxableSubjectToVAT","Taxable Earning Subject to VAT"},
		{"11047","VariableEarningL@WPetrolAllowance","Petrol Allowance"},
		{"11048","VariableEarningL@WPromotionaItemAllowance","Promotional Item Allowance"},
		{"11049","VariableEarningL@WQualityBonus","Quality Bonus Adjustment"},
		{"11050","VariableEarningL@WQuarterlyProductionBonus","Quarterly Production Bonus Adjustment"},
		{"11051","VariableEarningL@WAnnualProductionBonus","Annual Production Bonus Adjustment"},
		{"11052","VariableEarningL@WPetrolAllowanceAdjustment","Petrol Allowance Adjustment"},
		{"11053","VariableEarningL@WPromotionaItemAllowanceAdjustment","Promotional Item Allowance Adjustment"},
		{"11054","VariableEarningStopOrderBrokerAdminFee","Administration Fee"},
		{"11055","VariableEarningStopOrderBrokerAdminFeeAccuracyBonus","Administration Fee Accuracy Bonus"},
		{"11056","VariableEarningStopOrderBrokerAdminFeeConservationBonus ","Administration Fee Conservation Bonus"},
		{"11057","VariableEarningL@WBridgingFinanceAdjustment","Bridging Finance Adjustment"},
		{"11058","VariableEarningL@WLapseSuspenseAdjustment","Lapse Suspense Adjustment"},
		{"11059","VariableEarningL@WMiscellaneousTaxableEarning","Miscellaneous Taxable Earning"},
		{"11060","VariableEarningAcademyOfExcellenceTop-up","Academy of Excellence Top-up"},
		{"11061","VariableEarningAgencyTop-up","Agency Top-up"},
		{"11062","VariableEarningConditionalActivityFeeAdjustment","Conditional Activity Fee Adjustment"},
		{"11065","VariableEarningBBFEarning","BBF Earning"},
		{"11066","VariableEarningInterAgreementTransfer","Inter Agreement Transfer"},
		{"11067","VariableEarningVestingCommission","Vesting Commission"},
		{"11068","VariableEarningCommutation","Commutation"},
		{"11069","VariableEarningMarketingConcession","Marketing Concession"},
		{"11070","VariableEarningOwnYourBookAdvisorBonus","Own Your Book Advisor Bonus"},
		{"11071","VariableEarningOwnYourBookManagerBonus","Own Your Book Manager Bonus"},
		{"11072","VariableEarningProfitShareBonus","Profit Share Bonus"},
		{"11073","VariableEarningPracticeBuildingSubsidyAdjusment","Practice Building Subsidy Adjustment"},
		{"11074","VariableEarningCaseCountBonusAdjustment","Case Count Bonus Adjustment"},
		{"11075","VariableEarningPracticeMentorOverrider","Practice Mentor Bonus"},
		{"11076","VariableEarningReferralFee","General Agency Earnings"},
		{"11077","VariableEarningInRespectOfPriorTaxYearLibertyActive","Liberty type for liberty active prior tax"},
		{"11078","VariableEarningInRespectOfPriorTaxYearLibertyCal","Liberty type for liberty active prior tax cal"},
		{"11079","VariableEarningEntrepreneurManagementFeeAdjustment","Variable Earning Entrepreneur Management Fee Adjustment"},
		{"11080","VariableEarningPracticeFeeAdjustment","Variable Earning Practice Fee Adjustment"},
		{"11081","VariableEarningLegacyManagementFeeAdjustment","Variable Earning Legacy Managment Fee Adjustment"},
		{"11082","VariableEarningLegacyManagementFeeRecoveriesAdjustment","Variable Earning Legacy Managment Fee Recoveries Adjustment"},
		{"11083","VariableEarningLegacyE2000ManagementFeeRecoveriesAdjustment","Variable Earning Legacy E2000 Managment Fee Recoveries Adjustment"},
		{"11084","VariableEarningInRespectOfPriorTaxYearLibertyGrowth","Prior Tax Year Earnings Liberty Growth"},
		{"11086","VariableEarningLibertyLifePAYECreditAdjusment","Variable Earning Liberty Life PAYE Credit Adjusment"},
		{"11087","VariableEarningAdministrationAllowanceAdjustment","Variable Earning Administration Allowance Adjustment"},
		{"11089","VariableEarningLibertyLifePAYEAddCreditAdjustment","Variable Earning Liberty Life PAYE Add Credit Adjustment"},
		{"13556","VariableEarningL@WEstablishmentAllowanceAdjustment","VariableEarningL@WEstablishmentAllowanceAdjustment"},
		{"11091","VariableEarningReliefFund","Variable Earning Relief Fund COVID19"},
		{"11092","VariableEarningReliefFundTaxable","Variable Earning Relief Fund COVID19 Taxable"},
		{"11093","VariableEarningAgencyMedicalAid","Variable Earning Agency Medical Aid"},
		{"11094","VariableEarningFranchiseMedicalAid","Variable Earning Franchise Medical Aid"},
		{"11095","VariableEarningAgencyProvidentFund","Variable Earning Agency Provident Fund"},
		{"11096","VariableEarningAgencyRiskBenefits","Variable Earning Agency Risk Benefits"},
		{"11097","VariableEarningGapCover","Variable Earning Gap Cover"},
		{"11098","VariableEarningLAWProvident","Variable Earning LAW Provident"},
		{"11099","VariableEarningFMITIPP","Variable Earning FMITIPP"},
		{"11100","VariableEarningEmployeeProfessionalIndemnity","Variable Earning Employee Professional Indemnity"},
		{"11101","VariableEarningDebtManagement","Variable Earning Debt Management"},
		{"11102","VariableEarningFranchiseProvidentFund","Variable Earning Franchise ProvidentFund"},
		{"11103","VariableEarningLAWGRPLifeFuneralFund","Variable Earning LAW GRP Life FuneralFund"},
		{"11104","VariableEarningFranchiseRiskPremium","Variable Earning Franchise Risk Premium"},
		{"11105","VariableEarningSubsidyfornewindustryadviser","VariableEarningSubsidyfornewindustryadviser"}


	};
	
	
	// 406
	private static String[][] VED_FRINGEBENEFITS = new String[][] {
		{"10000","FringeBenefitGroupLife","Group Life"},
		{"10001","FringeBenefitIncomeProtectionPlan","Income Protection Plan"},
		{"10002","FringeBenefitLongServiceAward","Long Service Award"},
		{"10003","FringeBenefitBond","Bond Fringe Benefit"},
		{"10004","FringeBenefitLowOrZeroInterestLoan","Low or Zero Interest Loan"},
		{"10005","FringeBenefitPrize","Prize"},
		{"10006","FringeBenefitMedicalAid","Medical Aid"},
		{"10007","CheapOrFreeServiceInRespectOfPriorTaxYear","Prior Tax Year Fringe Benefit - Cheap or Free Service"},
		{"10008","LowOrZeroInterestLoanInRespectOfPriorTaxYear","Prior Tax Year Fringe Benefit - Low or Zero Interest Loan"},
		{"10009","EmployerMedicalAidContributionInRespectOfPriorTaxYear","Prior Tax Year Fringe Benefit - Medical Aid"},
		{"10010","FringeBenefitL@WGroupLife","Group Life"},
		{"10011","FringeBenefitFuneralCover","Funeral Cover"},
		{"10012","FringeBenefitL@wProvidentFund","Provident Fund"},
		{"10013","FringeBenefitL@WMedicalAid","Medical Aid Fringe Benefit"},
		{"10014","FringeBenefitL@WBridgingFinance","Bridging Finance Fringe Benefit"},
		{"10015","FringeBenefitMedicalAidContra","Medical Aid Contra"},
		{"10016","FringeBenefitIncomeProtectionPlanContra","Income Protection Plan Contra"},
		{"10017","FringeBenefitApprovedGroupLifeAmount","Fringe Benefit Approved Group Life Amount"},
		{"10018","FringeBenefitUnapprovedGroupLifeAmount","Fringe Benefit Unapproved Group Life Amount "},
		{"10019","FringeBenefitIncomeProtectionPlanPlus","Fringe Benefit Income Protection Plan Plus (IPP Plus)"},
		{"10020","FringeBenefitEmployerProvidentFundContribution","Fringe Benefit Employer Provident Fund Contribution"},
		{"10021","FringeBenefitEmployerProvidentFundContributionContra","Fringe Benefit Employer Provident Fund Contribution Contra"},
		{"13555","FringeBenefitL@W EstablishmentAllowance","FringeBenefitL@W EstablishmentAllowance"}};
	
		// 406
	private static String[][] VED_DEDUCTIONS = new String[][] {
		{"12000","VariableDeductionTelephone","Telephone"},
		{"12001","VariableDeductionPrinting","Printing"},
		{"12002","VariableDeductionStationery","Stationery"},
		{"12003","VariableDeductionAstute","Astute"},
		{"12004","VariableDeductionExtCourse","External Course"},
		{"12005","VariableDeductionBankCharges","Bank Charges"},
		{"12006","VariableDeductionLIBSports","LIB Sports"},
		{"12007","VariableDeductionMiscellaneous","Deduction"},
		{"12008","VariableDeductionDining","Dining"},
		{"12009","VariableDeductionSalaryExpense","Salary Expense"},
		{"12010","VariableDeductionLoanRepayment","Loan Repayment"},
		{"12011","VariableDeductionLegalFees","Legal Fees"},
		{"12012","VariableDeductionCarFine","Car Fine"},
		{"12013","VariableDeductionLegalTransferAgency","Legal Transfer - Agency"},
		{"12014","VariableDeductionLegalTransferBroker","Legal Transfer - Broker"},
		{"12015","VariableDeductionLegalTransferFranchise","Legal Transfer - Franchise"},
		{"12016","VariableDeductionPremiums","Premiums"},
		{"12017","VariableDeductionSalaryClearance","Salary Clearance"},
		{"12018","VariableDeductionAccessCard","Access Card"},
		{"12019","VariableDeductionCRDFACLTY","CARD FACULTY"},
		{"12020","VariableDeductionDialup","Dialup"},
		{"12021","VariableDeductionComputerCharges","Computer Charges"},
		{"12022","VariableDeductionSpouseGrp","Spouse GRP"},
		{"12023","VariableDeductionBondRepayment","Bond Repayment"},
		{"12024","VariableDeductionCapitalDisability","Capital Disability"},
		{"12025","VariableDeductionLabelRequests","Label Requests"},
		{"12026","VariableDeductionBloodFees","Blood Fees"},
		{"12027","VariableDeductionLaptopRental","Laptop Rental"},
		{"12028","VariableDeductionComputerMaintenance","Computer Maintenance"},
		{"12029","VariableDeductionForexPD","Forex Paid"},
		{"12030","VariableDeductionPAYEGARSH","PAYE Garnishment Order"},
		{"12031","VariableDeductionLGTFRXPD","LGT FRX PD"},
		{"12032","VariableDeductionPoolInt","Pool Interest"},
		{"12033","VariableDeductionTransferToSAPPayroll","Transfer to SAP Payroll"},
		{"12034","VariableDeductionRealPark","Real Parking"},
		{"12035","VariableDeductionSecretarialSpace","Secretarial Space"},
		{"12036","VariableDeductionRealRent","Real Rent"},
		{"12037","VariableDeductionFranchiseDeduction","Franchise Deduction"},
		{"12038","VariableDeductionSecretarialSalary","Secretarial Salary"},
		{"12039","VariableDeductionFranchiseBenefit","Franchise Benefit"},
		{"12040","VariableDeductionAgencyMedicalAid","Agency Medical Aid"},
		{"12041","VariableDeductionNonLibCareMedicalAid","Non LibCare Medical Aid"},
		{"12042","VariableDeductionBrokerMedicalAid","Broker Medical Aid"},
		{"12043","VariableDeductionAllGuard","AllGuard"},
		{"12044","VariableDeductionNotionalPark","Notional Parking"},
		{"12045","VariableDeductionDreadDisease","Dread Disease"},
		{"12046","VariableDeductionIndemnityCover","Indemnity Cover"},
		{"12047","VariableDeductionCRALoanRepaymentAdjustment","CRA Loan Repayment Adjustment"},
		{"12048","VariableDeductionInterest","Interest"},
		{"12049","VariableDeductionWriteOffAgency","Write Off - Agency"},
		{"12050","VariableDeductionWriteOffOther","Write Off Other"},
		{"12051","VariableDeductionPayment Received","Payment Received"},
		{"12052","VariableDeductionWriteOffFranchise","Write Off - Franchise"},
		{"12053","VariableDeductionWriteOffBroker","Write Off - Broker"},
		{"12054","VariableDeductionL@WGroup Life","Group Life Adjustment"},
		{"12055","VariableDeductionFuneralCover","Funeral Cover Adjustment"},
		{"12056","VariableDeductionL@WGarnisheeOrder","Garnishee Order"},
		{"12057","VariableDeductionL@WMedicalAid","Medical Aid"},
		{"12058","VariableDeductionL@WBondPayment","Bond Payment"},
		{"12059","VariableDeductionL@WProvidentFund","Provident Fund"},
		{"12060","VariableDeductionProfessionalFees","Professional Fees"},
		{"12061","VariableDeductionEnterpreneurMedicalAid","Enterpreneur Medical Aid"},
		{"12062","VariableDeductionProvidentFund","Provident Fund"},
		{"12064","VariableDeductionReliefFund","Relief Fund COVID19"},
		{"12063","VariableDeductionProvidentFundMember","Provident Fund Member Deduction"}};
	
	static {
//		logger.setLevel(Level.DEBUG);
//		logger.getLogger(RequestEntityManager.class).setLevel(Level.DEBUG);
	}
	
	/**
	 * Initialise the policy transaction model.
	 * 
	 * @param model
	 */
	public void initialisePageModel(IPolicyTransactionModel model) {
	}


	/**
	 * Return earning types for VED panel.   No type will list all types, with options 
	 * for the other subtypes
	 * 
	 * @param parentTYpe
	 * @return
	 */
	public List<TypeDTO> getVEDEarningTypesForType(EarningAndDeductionParentType parentType) {
		// In the real code section 
		//  1 - We call a bean to retrieve subtypes for each of the 3 types.
		//  2 - We apply a filter to remove some options as per current logic
		//  3 - New - Either we limit some types based on agreement kind or we enhance validation.
		
		// This is a non-persistent version for testing only
		if (earningMaps == null ) {
			logger.info("Initialising VED Earning types");
			// Initialise the maps first
			List<TypeDTO> earningList = Collections.unmodifiableList(convertToArray(VED_EARNINGS, 405));
			List<TypeDTO> fringeList = Collections.unmodifiableList(convertToArray(VED_FRINGEBENEFITS, 406));
			List<TypeDTO> deductionList = Collections.unmodifiableList(convertToArray(VED_DEDUCTIONS, 407));
			
			earningMaps = new HashMap<EarningAndDeductionParentType, List<TypeDTO>>();
			 
			earningMaps.put(EarningAndDeductionParentType.EARNING,  earningList);
			earningMaps.put(EarningAndDeductionParentType.FRINGE_BENEFIT,  fringeList);
			earningMaps.put(EarningAndDeductionParentType.DEDUCTION,  deductionList);
			
		}
		
		return earningMaps.get(parentType);
		
	}
	
	/**
	 * Convert full list of earnings
	 * @param earnings
	 * @return
	 */
	private ArrayList<TypeDTO> convertToArray (String[][] earnings, long parentType) {
		
		ArrayList<TypeDTO> list = new ArrayList<>();
		
		for (String[] val : earnings) {
			TypeDTO d = new TypeDTO();
			d.setOid(Long.parseLong(val[0]) );
			d.setName(val[1]);
			d.setDescription(val[2]);
			d.setParentType(parentType);
			System.out.println("Description: = " + d.getDescription() + "  - oid=" + d.getOid());
			list.add(d);
		}
		
		list.sort(new Comparator<TypeDTO>() {

			@Override
			public int compare(TypeDTO o1, TypeDTO o2) {
				return o1.getDescription().compareTo(o2.getDescription());
			}
			
		});
		
		return list;
	}
	
	/**
	 * Return a list of valid external payment types
	 * 
	 * @return
	 */
	public List<TypeDTO> getValidExternalPaymentTransactionTypes() {
		if (validExternalPaymentTransactionTypeList == null) {
//			TODO uncomment once types are loaded
//			validExternalPaymentTransactionTypeList = Collections.unmodifiableList(
//					validAgreementValuesFactory.getTypeSubTree(SRSType.EXTERNAL_PAYMENT_GROUP));
			validExternalPaymentTransactionTypeList = new ArrayList<TypeDTO>();
			TypeDTO d = new TypeDTO();
			d.setOid(11090);
			d.setName("VariableEarningExternalPayments");
			d.setDescription("Variable Earning External Payments");
			validExternalPaymentTransactionTypeList.add(d);
		}
		return validExternalPaymentTransactionTypeList;
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
	public void doAgreementValidation(ContextAgreementDTO contextAgreementDTO, 
			RequestKindType requestKind) throws ValidationException, DataNotFoundException {
		long start = System.currentTimeMillis();
		try {
//			requestTransactionManagement.doAgreementValidation(contextAgreementDTO.getAgreementNumber(), requestKind);
		} finally {
			logger.info("Validating agreement for request '"+ requestKind.getDescription() 
					+ "' took " + (System.currentTimeMillis()-start) + " millis, for agreement " 
					+contextAgreementDTO.getAgreementNumber());
		}
	}
	
	/**
	 * Validate policy transaction fields
	 * 
	 * @param agreementNr
	 * @param requestKind
	 * @param selectedItem
	 * @throws ValidationException
	 */
	public void doTransactionValidation(ISessionUserProfile sessionUser, long agreementNr, 
			RequestKindType requestKind,
			RequestTransactionDTO dto)  throws ValidationException {
		
		long start = System.currentTimeMillis();
		try {		
			
			// Jean - Adding VED screen and only doing relevant validation for it here, rest bypassed
			if (dto instanceof VEDTransactionDTO) {
				List<String> errors = new RequestTransactionManagement().doValidateVariableEarningDeduction(agreementNr, (VEDTransactionDTO) dto);
				if (!errors.isEmpty()) {
					throw new ValidationException(errors);
				}
			} else if (dto instanceof SettleRequestDTO) {
				List<String> errors = new RequestTransactionManagement().doValidateManualSettle(agreementNr, (SettleRequestDTO) dto);
				if (!errors.isEmpty()) {
					throw new ValidationException(errors);
				}
			} else if (dto instanceof ProcessAdvanceRequestDTO) {
				
				
//				List<String> errors = doValidateProcessAdvance(agreementNr, (ProcessAdvanceRequestDTO) dto);
				List<String> errors = new RequestTransactionManagement().doValidateProcessAdvance(agreementNr, null);
				
				if (!errors.isEmpty()) {
					throw new ValidationException(errors);
				}
			}
			
			
		} finally {
			logger.info("Validating transaction for request '"+ requestKind.getDescription() 
					+ "' took " + (System.currentTimeMillis()-start) + " millis, for agreement " +agreementNr);
		}
	}
	
	/**
	 * Validate process advance
	 * 
	 * @param dto
	 */
	public List<String> doValidateProcessAdvance(long agreementNr, ProcessAdvanceRequestDTO dto) {
		List<String> errors = new ArrayList<String>();
		
		/*
		 * Validate individual fields
		 */
		if (dto.getDescription()==null || dto.getDescription().trim().length() <= 3) {
			errors.add("Comment is required");
		}
		
		if (dto.getAmount()==null || dto.getAmount().isEqualTo(ZERO_CURRENCY)) {
			errors.add("Amount may not be zero");
		}
		
		return errors; 
	}
	
	/**
	 * Validate manual settle
	 * 
	 * @param dto
	 */
	public List<String> doValidateManualSettle(long agreementNr, SettleRequestDTO dto) {
		List<String> errors = new ArrayList<String>();
		
		/*
		 * Validate individual fields
		 */
		if (dto.getDescription()==null || dto.getDescription().trim().length() <= 3) {
			errors.add("Comment is required");
		}
		
		return errors; 
	}
	
	/**
	 * Validate external payment
	 * 
	 * @param dto
	 */
	public List<String> doValidateVariableEarningDeduction(long agreementNr, VEDTransactionDTO dto) {
		List<String> errors = new ArrayList<String>();
		
		/*
		 * Validate individual fields
		 */
		if (dto.getSuperType()==null) {
			errors.add("Transaction type is required");
		}
		
		if (dto.getEarningType()==null) {
			errors.add("Earning type is required");
		}
		
		if (dto.getAmount() == null || dto.getAmount().equals(ZERO_CURRENCY)) {
			errors.add("Amount may not be zero");
		} 
		if (dto.getAmount()!=null) {
			// Ensure that we only have 2 decimals
			// Not required as CurrencyAmount enforces 2 decimals
		}
		
		if (dto.getRequestedDate() == null || dto.getStartDate() == null) {
			errors.add("Requested Date is required");
		} else {
			// Not in the future or how far allowed?
			
			//  How far in the past?
		}
		
		if (dto.getDescription()==null || dto.getDescription().trim().length()<4) {
			errors.add("Description is required and should be at least 4 characters long.");
		}
		return errors;
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
	 * Initialise a new DTO for the given request kind type
	 * 
	 * @param requestKind
	 * @return
	 */
	public RequestTransactionDTO initialiseDTO(RequestKindType requestKind) {
//		try {
			RequestTransactionDTO dto = null;
			
			if (requestKind == RequestKindType.RecordPolicyInfo) {
				dto = new RecordPolicyInfoDTO();
			} else if (requestKind == RequestKindType.DistributePolicyEarning) {
				dto = new DistributePolicyEarningDTO();
			} else if (requestKind == RequestKindType.ProcessExternalPayments) {
				dto = new ExternalPaymentRequestDTO();
			} else if (requestKind == RequestKindType.ProcessVariableEarningsOrDeductions) {
				dto = new VEDTransactionDTO();
			} else if (requestKind == RequestKindType.ProcessAdvance) {
				dto = new ProcessAdvanceRequestDTO();
			} else if (requestKind == RequestKindType.ManualSettle) {
				dto = new SettleRequestDTO();
			} else {
				throw new RuntimeException("NOT SUPPORTED for testing - " + requestKind);
			}
					
			
			if (dto instanceof ExternalPaymentRequestDTO) {
				dto.setRequestedDate(new java.sql.Date(System.currentTimeMillis()));
				ExternalPaymentRequestDTO eDto = (ExternalPaymentRequestDTO) dto;
				eDto.setFullAmount(new CurrencyAmount(BigDecimal.ZERO, CurrencyEnum.ZAR));
				eDto.setTaxAmount(new CurrencyAmount(BigDecimal.ZERO, CurrencyEnum.ZAR));
				eDto.setIt88Amount(new CurrencyAmount(BigDecimal.ZERO, CurrencyEnum.ZAR));
			}
			
			
			return dto;
//		} catch (RequestConfigurationException e) {
//			throw new CommunicationException("A configuration issue occurred", e);
//		}
	}
	

	/**
	 * Raise a request for the given object
	 */
	public RequestVO raiseRequest(ISessionUserProfile userProfile,
			long agreementNr, RequestKindType requestKind,
			RequestTransactionDTO requestObject) throws RequestException {

		return null;
//		try {		
//			logger.info("Raising Request Transaction   requestKind=" + requestKind
//					+ "  ,agreementNr=" + agreementNr);
//			RequestWrapperDTO wrapper = new RequestWrapperDTO();
//			wrapper.setDtoObject(requestObject);
//			wrapper.setTargetActualId(agreementNr);
//			wrapper.setTargetAgreementNr(agreementNr);
//			
//			/*
//			 * Pre raise request behaviour, for now specific to DPE/PI
//			 */
//			if (requestKind == RequestKindType.RecordPolicyInfo) {
//				RecordPolicyInfoDTO policyInfoDTO = (RecordPolicyInfoDTO) requestObject;
//				// update some of the fields
//				if (policyInfoDTO.getMovementEffectiveDate() == null)
//					policyInfoDTO.setMovementEffectiveDate(policyInfoDTO.getEffectiveDate());
//				
//				if (policyInfoDTO.getInfoKindType()== PolicyInfoKindType.AssetsUnderManagement.getType()) {
//					policyInfoDTO.setAmount(policyInfoDTO.getFundAssetValue());
//					policyInfoDTO.setPremiumFrequency(1);
//					if (policyInfoDTO.getFundAssetValue() == null 
//							&& policyInfoDTO.getFundUnitCount() != null
//							&& policyInfoDTO.getFundUnitPrice() != null){
//						BigDecimal fundAssetValue = policyInfoDTO.getFundUnitCount().multiply(
//								policyInfoDTO.getFundUnitPrice().getValue());
//						policyInfoDTO.setFundAssetValue(new CurrencyAmount(fundAssetValue, CurrencyEnum.ZAR));
//					} 
//				} else if (policyInfoDTO.getInfoKindType()== PolicyInfoKindType.PolicyInfoGuardbankPremium.getType() ||
//						policyInfoDTO.getInfoKindType()== PolicyInfoKindType.PolicyInformationINN8PCRPremium.getType() ||
//						policyInfoDTO.getInfoKindType()== PolicyInfoKindType.PolicyInformationINN8CommissionPremium.getType()) {					
//					//Persist values passed on Screen as it is - SBS0510 	
//						policyInfoDTO.setInfoKind("PREMIUMS");	
//						policyInfoDTO.setFundAssetValue(policyInfoDTO.getAmount());
//						
//				} else {
//					// Premium related types
//					if (policyInfoDTO.getInfoKindType()== PolicyInfoKindType.PolicyInfoRiskPremium.getType()) {
//						// Default risk premium values not set
//						if (policyInfoDTO.getIsLapse()==null) {
//							policyInfoDTO.setIsLapse(0);
//						}
//					} else {
//						// Only Risk premium requires these values
//						policyInfoDTO.setIsLapse(null);
//						policyInfoDTO.setPremiumsReceivedCount(null);
//						policyInfoDTO.setTerm(null);
//						policyInfoDTO.setActivePolicyMonths(null);
//					}
//					// Only used for AUM so reset
//					policyInfoDTO.setFundCode(null);
//					policyInfoDTO.setFundUnitCount(null);
//					//TODO The limit check uses this value.
//					policyInfoDTO.setFundAssetValue(policyInfoDTO.getAmount());
//					policyInfoDTO.setFundUnitPrice(null);
//					policyInfoDTO.setPricingDate(null);
//				}
//				
//			} else if (requestKind == RequestKindType.DistributePolicyEarning) {
//				// Some additional configs for DPE's
//				DistributePolicyEarningDTO dpeDTO = (DistributePolicyEarningDTO) requestObject;
//				dpeDTO.setBusinessUnit(dpeDTO.getBusinessUnit() == null ? "LLA" : dpeDTO.getBusinessUnit());
//				dpeDTO.setGlCompanyCode(dpeDTO.getGlCompanyCode() == null ? "CALL" : dpeDTO.getGlCompanyCode());
//				dpeDTO.setInForceIndicator(dpeDTO.isInForceIndicator());
//
//				dpeDTO.setCommissionFrequency(dpeDTO.getCommissionFrequency() == null ? 0 : dpeDTO.getCommissionFrequency());
//				dpeDTO.setEffectiveDate(new java.sql.Date(dpeDTO.getEffectiveDate().getTime()));
//
//				if (dpeDTO.getMovementEffectiveDate() == null)
//					dpeDTO.setMovementEffectiveDate(dpeDTO.getEffectiveDate());
//
//				if (dpeDTO.getPremiumFrequency() == null) {
//					dpeDTO.setPremiumFrequency(0);
//				}
//
//				dpeDTO.setSubStatus(0); // Default value
//				
//				OnlyActualVO onlyActualVo;
//				try {
//					onlyActualVo = requestManagement.getOnlyActualForAgreementAndCommKind(agreementNr, dpeDTO.getCommissionKind());
//					wrapper.setTargetActualId(onlyActualVo.getObjectReference().getObjectOid());
//				} catch (DataNotFoundException e) {
//					// This should NEVER happen
//					throw new RequestException("Could not find agreement data for " + agreementNr);
//				} catch (ValidationException e) {
//					throw new RequestException("The commission kind is not valid for this agreement");
//				}
//				
//				//SBS0510 - Check the Distribution rules for Product Kind and Commission kind and validate for Manual DPE(defect raised by Mpho-01/06/20)
//				ApplicationContext appContext = new ApplicationContext();
//				try {
//					
//					List<Long> agmtList = new ArrayList<Long>();
//					Long agmtKind = 0l;
//					agmtList.add(agreementNr);
//					
//					List<IAgreementDetailFLO> agmtDetailList = agreementEntityManager.findAgreementsSimpleDetail(agmtList);
//					
//					if(agmtDetailList != null){
//						agmtKind = agmtDetailList.get(0).getKind();
//					}						
//					
//					
//					int intermediaryProductKind = agmtKind.intValue();
//					int commKind = dpeDTO.getCommissionKind() != null?dpeDTO.getCommissionKind():0;
//					short contribIncreaseIndicator =  dpeDTO.getContributionIncreaseIndicator() != null ?dpeDTO.getContributionIncreaseIndicator().shortValue():ZERO.shortValue();
//					short commBalanceIndicator = dpeDTO.getCommissionBalanceIndicator()!= null && dpeDTO.getCommissionBalanceIndicator().equals(Boolean.TRUE)?ONE.shortValue():ZERO.shortValue();
//					short growthPensionIndicator = dpeDTO.getGrowthPensionIndicator()!= null && dpeDTO.getGrowthPensionIndicator().equals(Boolean.TRUE)?ONE.shortValue():ZERO.shortValue();
//					short premFrequency = dpeDTO.getPremiumFrequency()!= null ? dpeDTO.getPremiumFrequency().shortValue():ZERO.shortValue();
//					int productRef = dpeDTO.getProductReference() != null ? dpeDTO.getProductReference():0;
//					int commFrequency = dpeDTO.getCommissionFrequency() != null ? dpeDTO.getCommissionFrequency():0;
//					
//					
//					Integer distributionKind = getDistributionRatingUtil().getDistributionKind(appContext, intermediaryProductKind, 
//							commKind,contribIncreaseIndicator, commBalanceIndicator, 
//							growthPensionIndicator, premFrequency, productRef, commFrequency);
//				}catch(DistributionKindNotFoundException de){
//					throw new RequestException("The commission kind is not valid for the product selected !");
//				} finally {
//					appContext = null;
//				}				
//			}
//			
//			RequestVO requestVO = requestManagement.raiseRequest(new ApplicationContext(), requestKind,
//					userProfile, wrapper);
//
//			/*
//			 * The reject record must be deleted in the same transaction (if this was an update
//			 * of a reject record) 
//			 */
//			if  (requestVO != null && requestObject.getRejectOid()!=null) {
//				try {
//					preTransactionRejectManagement.deleteReject(requestObject.getRejectOid());
//				} catch (DataNotFoundException e) {
//					throw new RequestException("Unable to delete the linked reject record, oid not found '" 
//							+ requestObject.getRejectOid() + "'");
//				}
//			}
//			/*
//			 * Post raise request behaviour, for now specific to DPE/PI.  Both are the same and
//			 * 		concerns removing a reject record.
//			 * TODO jzb0608 - finalise this
//			 */
////			if(requestVO != null && requestVO.getObjectReference() != null && requestVO.getObjectReference().getObjectOid() != 0){
////				if(policyInfoDTO.getRejectOid() != null){
////					preTransactionRejectManagement.deleteReject(policyInfoDTO.getRejectOid());
////				}
////			}
//			return requestVO;
//		} catch (RequestConfigurationException e) {
//			throw new CommunicationException(e);
//		}
		
	}

	/**
	 * Convert a RejectElementDTO in the appropriate transaction DTO
	 *  
	 * @param rejectElementDTO
	 * @return
	 * @throws ValidationException 
	 */
	public RequestTransactionDTO convertRejectToTransaction(RejectElementDTO rejectElementDTO) throws ValidationException {
		
//		throw new RuntimeException("Not supported whilst testing");
		RequestTransactionDTO dto = null;
		
		IaaXmlUtilProxy iaaXmlUtilProxy = new IaaXmlUtilProxy();
		
		
		if (rejectElementDTO == null || rejectElementDTO.getXmlMessage()==null || rejectElementDTO.getXmlMessage().length()==0) {
			throw new ValidationException("Invalid reject transaction, can't convert.");
		}
		
		if (rejectElementDTO.getRequestKind()==RequestKindType.DistributePolicyEarning.getRequestKind()) {
			PreTransactionVO vo = iaaXmlUtilProxy.convertXMLToPreTransaction(rejectElementDTO.getXmlMessage());
			dto = convertPreTransactionVOtoDTO(vo);
		} else if (rejectElementDTO.getRequestKind()==RequestKindType.RecordPolicyInfo.getRequestKind()) {
			PolicyInformationTransactionVO vo = iaaXmlUtilProxy.convertXMLToPolicyInfoTransaction(rejectElementDTO.getXmlMessage());
			dto = convertPolicyInfoVOtoDTO(vo);
		} else if (rejectElementDTO.getRequestKind()==RequestKindType.ProcessVariableEarningsOrDeductions.getRequestKind()) {
			EarningDeductionVO vo = iaaXmlUtilProxy.convertXMLToEarningDeduction(rejectElementDTO.getXmlMessage());
			dto = convertEarningDeductionVOtoDTO(vo);
		} 
		
		if (dto != null && rejectElementDTO.getOid() != 0 ) {
			dto.setRejectOid(rejectElementDTO.getOid());
		}		
		return dto;

	}
	
	/**
	 * Convert between a VO and DTO version for VED transactions
	 * 
	 * @param vo
	 * @return
	 */
	private VEDTransactionDTO convertEarningDeductionVOtoDTO(EarningDeductionVO vo) {
		VEDTransactionDTO dto = new VEDTransactionDTO();
//		dto.setAgreementNumber(vo.get);  // TODO might have to do a conversion here

		dto.setAmount(vo.getAmount());
		dto.setDescription(vo.getDescription());
		dto.setEarningType((long)vo.getEarningType());
//		dto.setRejectOid(vo.get);
		dto.setRequestedDate((vo.getStartDate()!=null)?new java.sql.Date(vo.getStartDate().getTime()) : null);
		dto.setStartDate((vo.getStartDate()!=null)?new java.sql.Date(vo.getStartDate().getTime()) : null);
		
		return dto;
	}
	
	
	/**
	 * Convert between a VO and DTO version of the same type of transaction.
	 * 
	 * TODO Jzb0608 - Change this to use the Annotation converter 
	 * 
	 * @param vo
	 * @return
	 */
	private RequestTransactionDTO convertPolicyInfoVOtoDTO(PolicyInformationTransactionVO vo) {
		RecordPolicyInfoDTO dto = new RecordPolicyInfoDTO();
	
		dto.setFundAssetValue((vo.getFundAssetValue()!=null)?convertNumberToCurrency(vo.getFundAssetValue()):null);
		dto.setFundCode(vo.getFundCode());
		dto.setFundUnitCount((vo.getFundUnitCount()!=null)?new BigDecimal(vo.getFundUnitCount().doubleValue()):null);
		dto.setFundUnitPrice((vo.getFundUnitPrice()!=null)?convertNumberToCurrency(vo.getFundUnitPrice()):null);
		dto.setAmount((vo.getAmount()!=null)?convertNumberToCurrency(vo.getAmount()):null);
		dto.setPremiumFrequency(vo.getPremiumFrequency());
		dto.setInfoKind(vo.getInfoKind());
		dto.setInfoKindType((long)vo.getInfoKindType());
		if (dto.getInfoKindType()==null && dto.getFundCode()!=null || dto.getFundUnitCount()!=null) {
			// Default to AUM (this wasn't set by default when we only had AUM)
			dto.setInfoKindType(PolicyInfoKindType.AssetsUnderManagement.getType());
		}
		dto.setLifeAssured(vo.getLifeAssuredName());
		dto.setPolicyNr(vo.getPolicyNumber());
		dto.setPolicyStartDate(vo.getPolicyStartDate()!=null?new java.sql.Date(vo.getPolicyStartDate().getTime()):null);
		dto.setPricingDate((vo.getPricingDate()!=null)?new java.sql.Date(vo.getPricingDate().getTime()):null);
		dto.setProductCode(vo.getProductCode());
		
		dto.setIsLapse(vo.getIsLapse());
		dto.setActivePolicyMonths(vo.getActivePolicyMonths());
		dto.setPremiumsReceivedCount(vo.getPremiumsReceivedCount());
		dto.setTerm(vo.getTerm());
	
		return dto;
	}



	/**
	 * Convert a DPE VO to a DPE DTO
	 * 
	 * @param vo
	 * @return
	 */
	private RequestTransactionDTO convertPreTransactionVOtoDTO(PreTransactionVO vo) {
		
			DistributePolicyEarningDTO dto = new DistributePolicyEarningDTO();
			dto.setProductReference(vo.getProductReference());
			dto.setPolicyReference(vo.getPolicyNumber());
			dto.setPremiumFrequency(vo.getPremiumFrequency());
			dto.setContributionIncreaseIndicator(vo.getContributionIncreaseIndicator());
			dto.setAmount(vo.getAmount());
			dto.setMovementCode(vo.getMovementCode());
			dto.setContributionIncreaseIndicator(vo.getContributionIncreaseIndicator());
			dto.setDPELifeAssuredName(vo.getLifeAssuredName());
			dto.setCommissionKind(vo.getCommissionKind());
			dto.setCommissionFrequency(vo.getCommissionFrequency());
			dto.setCommissionTerm(vo.getCommissionTerm());
			if(vo.getMovementEffectiveDate() != null) {
				dto.setMovementEffectiveDate(new java.sql.Date(vo.getMovementEffectiveDate().getTime()));
				dto.setEffectiveDate(dto.getMovementEffectiveDate());
			}
			if(vo.getPolicyStartDate() != null)
				dto.setPolicyStartDate(new java.sql.Date(vo.getPolicyStartDate().getTime()));
			
			
			dto.setGlCompanyCode(vo.getGLCompanyCode());     
			
			logger.info("growthpensionindicator " + vo.getGrowthPensionIndicator());
//			if (vo.getGrowthPensionIndicator()!=0)
//				dto.setGrowthPensionIndicator(vo.getGrowthPensionIndicator());  // previously commented out
			dto.setContributionIncreaseIndicator(vo.getContributionIncreaseIndicator());
			dto.setNumberOfMonths(vo.getNumberOfMonths());
			dto.setInForceIndicator(vo.getPolicyInForceIndicator());
			dto.setSourceSystemReference(vo.getSourceSystemReference());
			dto.setBusinessUnit(vo.getBusinessUnit());
			dto.setBenefitType(vo.getBenefitType());
			dto.setUpfrontCommPercentage((vo.getUpfrontCommissionPercentage()!=null)?new Percentage(vo.getUpfrontCommissionPercentage()):null);
			
			logger.info("getCommissionTerm " + vo.getCommissionTerm());
			logger.info("getMaximumCommissionTerm " + vo.getMaximumCommissionTerm());
			if (vo.getCommissionTerm()!=0)
				dto.setCommissionTerm(vo.getCommissionTerm());  			// previously commented out
			if (vo.getMaximumCommissionTerm()!=0)
				dto.setMaxCommissionTerm(vo.getMaximumCommissionTerm());  // previously commented out
			
			return dto;
		
	
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
	 * Retrieve the table data for a given request kind / agreement
	 * 
	 * @throws QueryTimeoutException 
	 * @throws RequestException 
	 */
	public List<Object> findTableData(
			RequestKindType requestKind, long agreementNr) throws RequestException, QueryTimeoutException {
	
		long start = System.currentTimeMillis();
//		if (requestKindType == RequestKindType.)
		
		//return getSessionBean().findAllSegmentsForSegmentNameList(((SegmentNameDTO)selectedObject).getId());
		ArrayList<Object> resultList = new ArrayList<Object>();
		
		if (requestKind == RequestKindType.ProcessAdvance || requestKind == RequestKindType.ManualSettle) {
			
			try {
				RequestEnquiryRowDTO row = new RequestEnquiryRowDTO();
				row.setExecutedDate(DateUtil.getInstance().getDateFromString("2023-01-01"));
				row.setRequestKindType(RequestKindType.ProcessAdvance);
				row.setAdditionalPropertyMap(new HashMap<PropertyKindType, Object>());
				row.getAdditionalPropertyMap().put(PropertyKindType.Amount, new CurrencyAmount(new BigDecimal("200.01")));
				row.getAdditionalPropertyMap().put(PropertyKindType.Description, "Special comment");
				row.setRequestor("JZB0608");
				resultList.add(row);
				
				
				row = new RequestEnquiryRowDTO();
				row.setExecutedDate(DateUtil.getInstance().getDateFromString("2023-02-01"));
				row.setRequestKindType(RequestKindType.ManualSettle);
				row.setAdditionalPropertyMap(new HashMap<PropertyKindType, Object>());
				row.getAdditionalPropertyMap().put(PropertyKindType.Description, "Settling manually");
				row.setRequestor("ABC0101");
				resultList.add(row);
				
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
//		// Set up search attributes, uses the passed request kind
//		List<RequestKindType> requestKinds = new ArrayList<RequestKindType>();
//		requestKinds.add(requestKind);
//		RequestEnquirySearchDTO searchDto = new RequestEnquirySearchDTO(agreementNr,requestKinds, 
//				RequestStatusType.EXECUTED);		
//		try {
//			Date startDate = null;
//			if (requestKind == RequestKindType.ProcessExternalPayments) {
//				startDate = requestTransactionManagement.getTaxYearStartDate();
//			} else {
//				// Others default to 5 days ago
//				DateUtil dateUtil = DateUtil.getInstance();
//				startDate = dateUtil.addDays(dateUtil.getTodayDatePart(), - 5);
//			}
//			searchDto.setStartDate(startDate);
//			
//			// Call enquiry search
//			Collection<PropertyKindType> additionalProperties = Collections.EMPTY_LIST;
//			RequestEnquiryResultDTO resultDto = null;
//			
//			if (requestKind == RequestKindType.ProcessExternalPayments) {
//				// TODO make this nicer, for AUM and DPE it will work differently
//				additionalProperties = Arrays.asList(new PropertyKindType[] {
//					PropertyKindType.Description,PropertyKindType.DirectiveNumber,
//					PropertyKindType.Amount,PropertyKindType.TaxAmount,PropertyKindType.It88Amount, 
//					PropertyKindType.EarningType});
//				resultDto = requestEnquiryManagement.findRequests(searchDto,additionalProperties);
//			} else if (requestKind == RequestKindType.RecordPolicyInfo) {
//			
//				resultDto = requestEnquiryManagement.findRequests(searchDto, 1, 20, RequestEnquiryAUMRowDTO.class, 
//						RequestEnquiryFetchType.PARTY_NAMES, RequestEnquiryFetchType.ROLE_PLAYERS);
//			}
//			
//			// Process results from search
//			if (resultDto != null && resultDto.getResultList()!=null && resultDto.getResultList().size()>0) {
//				
//				for (int i = 0; i < resultDto.getResultList().size();++i) {
//					IRequestEnquiryRow row = resultDto.getResultList().get(i);
//					RequestEnquiryRowDTO rowR = (RequestEnquiryRowDTO) row;
//					
//					// Do something special here
//					// TODO this need to be done in a better way using OO
//					if (requestKind == RequestKindType.ProcessExternalPayments) {
//						ExternalPaymentRequestDTO a = new ExternalPaymentRequestDTO();
//						a.setDescription((String) rowR.getAdditionalProperty(PropertyKindType.Description.getPropertyKind()));
//						a.setDirectiveNumber((String) rowR.getAdditionalProperty(PropertyKindType.DirectiveNumber.getPropertyKind()));
//						a.setExecutedDate((java.sql.Date) rowR.getExecutedDate());
//						BigDecimal v = (BigDecimal) rowR.getAdditionalProperty(PropertyKindType.Amount.getPropertyKind());
//						a.setFullAmount(new CurrencyAmount((v==null)? BigDecimal.ZERO : v,CurrencyEnum.ZAR));
//						
//						v = (BigDecimal) rowR.getAdditionalProperty(PropertyKindType.It88Amount.getPropertyKind());
//						a.setIt88Amount(new CurrencyAmount((v==null)? BigDecimal.ZERO : v,CurrencyEnum.ZAR));
//						
//						v = (BigDecimal) rowR.getAdditionalProperty(PropertyKindType.TaxAmount.getPropertyKind());
//						a.setTaxAmount(new CurrencyAmount((v==null)? BigDecimal.ZERO : v,CurrencyEnum.ZAR));
//						
//						a.setEarningType((Long) rowR.getAdditionalProperty(PropertyKindType.EarningType.getPropertyKind()));
//						a.setRequestedDate((java.sql.Date) rowR.getRequestedDate());
//						
//						resultList.add(a);
//					} else if (requestKind == RequestKindType.RecordPolicyInfo) {
//						// TODO There must be a converter already, change this.
//						RecordPolicyInfoDTO a = new RecordPolicyInfoDTO();
//						RequestEnquiryAUMRowDTO aumRow = (RequestEnquiryAUMRowDTO) rowR;
//						requestEnquiryManagement.converRequestEnquiryToDTO(aumRow, a);
//						resultList.add(a);
//					}
//					
//				}
//				
//			}
//		} finally {
//			logger.info("FindTableData for request " + requestKind.getDescription() + " took " 
//					+ (System.currentTimeMillis()-start) + " millis");
//		}
//		
		return resultList;
	
	}


	/**
	 * Cancel a reject by changing its status code.
	 */
	@Override
	public void cancelReject(long rejectOid) throws ValidationException {
		logger.info("Cancel Reject with " + rejectOid);
	}
	
	
	/**
	 * Retrieve the agreement linked to the reject object.  Return null if no agreement is linked
	 * 
	 * @param rejectElementDTO
	 * @return
	 */
	public ResultAgreementDTO getLinkedAgreement(RejectElementDTO rejectElementDTO) {
		if (rejectElementDTO == null) {
			return null;
		}
//		try {
//			return agreementManagement.findAgreementWithSRSAgreementNr(rejectElementDTO.getSrsAgmtNo());
//		} catch (CommunicationException e) {
//			logger.error("Internal error", e);
//			throw e;
//		} catch (DataNotFoundException e) {
			return null;
//		}
	}

	/**
	 * Retrieve the party linked to the agreement found in the earlier step.  Return null
	 * if not found. 
	 * 
	 * @param resultAgreementDTO
	 * @return
	 */
	public ResultPartyDTO getLinkedParty(ResultAgreementDTO resultAgreementDTO) {
		if (resultAgreementDTO == null || resultAgreementDTO.getAgreementNumber() == null) {
			return null;
		}
//		try {
//			return partyManagement.findPartyIntermediaryWithAgreementNr(resultAgreementDTO.getAgreementNumber());
//		} catch (CommunicationException e) {
//			logger.error("Internal error", e);
//			throw e;
//		} catch (DataNotFoundException e) {
			return null;
//		}
		
	}
	
		
	 /**
	   * Gets the distribution view rating util.
	   * @return DistributionViewRatingUtil
	   */
	  private DistributionRatingUtil getDistributionRatingUtil() {
	    return new DistributionRatingUtil(new CachingRatingBPOFactory(
	    		AgreementContext.getAgreementContext(),
				AgreementHomeMetaDataFactory.RATING_BPO));
	  }
	  

	
}
