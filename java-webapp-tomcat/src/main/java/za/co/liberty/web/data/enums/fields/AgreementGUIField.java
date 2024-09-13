package za.co.liberty.web.data.enums.fields;

import java.util.ArrayList;
import java.util.List;

import za.co.liberty.interfaces.agreements.requests.PropertyKindType;


public enum AgreementGUIField implements IGUIField {
	
	/**
	 * Agreement
	 */
	BROKER_BRANCH_FRANCHISE_GROUP("brokerBranchFranchiseGroup","lblBrokerBranchFranchiseGroup",
			"Broker Branch Franchise Group",PropertyKindType.BBFGroup),
	BROKER_CONSULTANT_PRODUCTION_CLUB_STATUS("brokerConsultantProductionClubStatus",
			"lblBrokerConsultantProductionClubStatus","Broker Consultant Production Club Status",
			PropertyKindType.BrokerConsultantProductionClubStatus),
	CONSULTANT_CODE("consultantCode","lblConsultantCode","Consultant Code",PropertyKindType.ThirteenDigitConsCode),
	COST_CENTER("costCenter","lblCostCenter","Cost Center",null),//Cost Centre is SRS will be derived from HAS HOME ROLE HIERARCHY NODE
	DEDICATED_SBFC_CONSULTANT_TYPE("dedicatedSBFCConsultantType",
			"lblDedicatedSBFCConsultantType","Dedicated SBFC Consultant Type",
			PropertyKindType.DedicatedSBFCConsultantType),
	DIRECT_MANPOWER("directManpower","lblDirectManpower","Manpower",PropertyKindType.DirectManPower),
	EARLY_DEBITS_INDICATOR("earlyDebitsIndicator","lblEarlyDebitsIndicator","Early Debits Indicator",
			PropertyKindType.EarlyDebitsIndicator),
	EARLY_DEBITS_REASON("earlyDebitsReason.value","lblEarlyDebitsReason","Early Debits Reason",
			PropertyKindType.EarlyDebitReason),
	EARLY_DEBITS_START_DATE("earlyDebitsReason.effectiveFrom","lblEarlyDebitsStartDate","Early Debits Start Date",
			PropertyKindType.EarlyDebitsIndicator),
	END_DATE("endDate","lblEndDate","End Date",null),
	ENTITY("entity","lblEntity","Entity",PropertyKindType.Entity),
	MANPOWER("limraTenure","lblLimraTenure","LIMRA Tenure",PropertyKindType.ManPower),
	MONTHLY_GUARANTEED_AMOUNT("monthlyGuaranteedAmount",
			"lblMonthlyGuaranteedAmount","Monthly Guaranteed Amount",PropertyKindType.MonthlyGuaranteedAmount),
	NETWORK("network","lblNetwork","Network",PropertyKindType.Network),
	PREAUTH_CATEGORY("preauthLimitCategory.id","lblPreAuthLimitCategory","Pre Authorisation Limit Category",
			PropertyKindType.PreAuthorisationLimitCategory),
	PREAUTH_AMOUNT("preAuthLimitAmount","lblPreAuthLimitAmount","Pre Authorisation Limit Amount",
			PropertyKindType.PreAuthorisationLimitAmount),
	PREAUTH_OVERRIDE("preAuthOverride","lblPreAuthOverride","Pre Authorisation Override",
			PropertyKindType.PreAuthorisationLimitCategory),
	PRIMARY_COMPANY_CONTRACTED_TO("primaryCompanyContractedTo",
			"lblPrimaryCompanyContractedTo","Primary Company Contracted To",
			PropertyKindType.PrimaryCompanyContractedTo),
	PRODUCTION_CLUB_STATUS("productionClubStatus","lblProductionClubStatus","Production Club Status",
			PropertyKindType.ProductionClubStatus),
	SEGMENT("segment","lblSegment","Segment",PropertyKindType.Segment),
	//Added for SE 492-Pritam - 01/06/2011
	DONOTCALCINTEREST("doNotCalcInt","lblDoNotCalcInt","Do Not Calculate Interest",PropertyKindType.DoNotCalculateInterest),
	START_DATE("startDate","lblStartDate","Start Date",null),
	STATUS("status","lblStatus","Status",PropertyKindType.Status),
	STATUS_DATE("currentStatus.effectiveFrom","lblStatusDate","Status Date",PropertyKindType.Status),
	STATUS_REASON("statusReason","lblStatusReason","Status Reason",
			PropertyKindType.StatusReason),
	SUPPORT_TYPE("supportType","lblSupportType","Support Type",PropertyKindType.SupportType),
	TITULAR_LEVEL("titularLevel","lblTitularLevel","Titular Level",PropertyKindType.TitularLevel),
	KROLL_DONE("krollDone","lblKrollDone","Honesty and Integrity Declaration Received",PropertyKindType.KROLLCheckDone),
	FITPROPWAIVERECHECK("fitPropWaiverRECheck.value","lblFitPropWaiverRECheck","Waiver RE Check",PropertyKindType.FitPropWaiverRECheck),
	FITPROPWAIVERECHECKEND("fitPropWaiverRECheck.effectiveTo","lblFitPropWaiverRECheckEnd","End Date",PropertyKindType.FitPropWaiverRECheck),
	FITPROPWAIVECPDCHECK("fitPropWaiverCPDCheck.value","lblFitPropWaiverCPDCheck","Waiver CPD Check",PropertyKindType.FitPropWaiverCPDCheck),
	FITPROPWAIVECPDCHECKEND("fitPropWaiverCPDCheck.effectiveTo","lblFitPropWaiverCPDCheckEnd","End Date",PropertyKindType.FitPropWaiverCPDCheck),
	FITPROPWAIVEPRODUCTACCREDCHECK("fitPropWaiverProductAccreditationCheck.value","lblFitPropWaiverProductAccreditationCheck","Waiver Product Accreditation Check",PropertyKindType.FitPropWaiverProductAccreditationCheck),
	FITPROPWAIVEPRODUCTACCREDCHECKEND("fitPropWaiverProductAccreditationCheck.effectiveTo","lblFitPropWaiverProductAccreditationCheckEnd","End Date",PropertyKindType.FitPropWaiverProductAccreditationCheck),
	FITPROPWAIVEFAISADVISORCHECK("fitPropWaiverFAISAdviserCheck.value","lblFitPropWaiverFAISAdviserCheck","Waiver FAIS Adviser Check",PropertyKindType.FitPropWaiverFAISAdviserCheck),
	FITPROPWAIVEFAISADVISORCHECKEND("fitPropWaiverFAISAdviserCheck.effectiveTo","lblFitPropWaiverFAISAdviserCheckEnd","End Date",PropertyKindType.FitPropWaiverFAISAdviserCheck),
	FITPROPWAIVEFAISFSPCHECK("fitPropWaiverFAISFSPCheck.value","lblFitPropWaiverFAISFSPCheck","Waiver FAIS FSP Check",PropertyKindType.FitPropWaiverFAISFSPCheck),
	FITPROPWAIVEFAISFSPCHECKEND("fitPropWaiverFAISFSPCheck.effectiveTo","lblFitPropWaiverFAISFSPCheckEnd","End Date",PropertyKindType.FitPropWaiverFAISFSPCheck),
	FITPROPSEGMENT("fitPropsegmentProperty.value","lblFitPropSegment","Fit And Proper Segment",PropertyKindType.FitPropSegment),
	FITPROPWAIVEADVICECHECK("fitPropWaiverAdviceCheck.value","lblFitPropWaiverAdviceCheck","Waiver Advice Check",PropertyKindType.FitPropWaiverAdviceCheck),
	FITPROPWAIVEADVICECHECKEND("fitPropWaiverAdviceCheck.effectiveTo","lblFitPropWaiverAdviceCheckEnd","End Date",PropertyKindType.FitPropWaiverAdviceCheck),
	FITPROPWAIVEINTERMEDIARYSERVICECHECK("fitPropWaiverIntermediaryServiceCheck.value","lblFitPropWaiverIntermediaryServiceCheck","Waiver Intermediary Service Check",PropertyKindType.FitPropWaiverIntermediaryServiceCheck),
	FITPROPWAIVEINTERMEDIARYSERVICECHECKEND("fitPropWaiverIntermediaryServiceCheck.effectiveTo","lblFitPropWaiverIntermediaryServiceCheckEnd","End Date",PropertyKindType.FitPropWaiverIntermediaryServiceCheck),
    //	-- MXM1904 ADDED THIS FOR FRS181 Statement Message Enhancements
	STOPSTATEMENTDISTRIBUTION("stopStatementDistribution.value","lblstopStatementDistribution","Stop Statement Distribution",PropertyKindType.StopStatementDistribution),
	//Added for SE 739-Pritam-16/9/11
	MY_BANKING_NUM("myBankingNumber",
			"lblMyBankingNumber","My Banking No.",PropertyKindType.MyBankingNumber),
			
	HAS_MEDICAL_CREDITS("hasMedicalAidCredits.value",
			"lblHasMedicalAidCredits","Linked to Medical Aid",PropertyKindType.HasMedicalAidTaxCredits),	
	MANUAL_PRODUCTION_CLUB_STATUS("manualProductionClubStatus.value",
			"lblManualProductionClubStatus","Manual Production Club Status",PropertyKindType.ManualProductionClubStatus),
	MANUAL_PRODUCTION_CLUB_STATUS_END("manualProductionClubStatus.effectiveTo","ManualProductionClubStatusEnd","Manual Production Club Status End Date",PropertyKindType.ManualProductionClubStatus),
			
	CALCULATED_PRODUCTION_CLUB_STATUS("calculatedProductionClubStatus",
			"lblCalculatedProductionClubStatus","Calculated Production Club Status",PropertyKindType.CalculatedProductionClubStatus),

	/**
	 * Associated Codes
	 */
	BANK_CONSULTANT_CODE("associatedCodes.bankConsultantCode","lblBankConsultantCode",
			"Bank Consultant Code",PropertyKindType.BankConsultantCode),
	COMPASS_CODE("associatedCodes.compassCode","lblCompassCode","Compass Code",
			PropertyKindType.CompassCode),
	LCB_QUANTUM_CODE("associatedCodes.lcbQuantumCode","lblLCBQuantum","LCB Quantum",
			PropertyKindType.lcbQuantum),
	LIBERTY_ACTIVE_CODE("associatedCodes.libertyActiveCode","lblLibertyActive",
			"Liberty Active",PropertyKindType.LibertyActiveCode),
	MASTHEAD_MEMBER_NUMBER("associatedCodes.mastheadMembershipNumber",
			"lblMastheadMemberNumber","Masthead membership number",
			PropertyKindType.MastheadMembershipNumber),
	MEDSCHEME_CODE("associatedCodes.medschemeCode","lblMedscheme","Medscheme",
			PropertyKindType.Medscheme),
	PROVIDENT_FUND_NUMBER("associatedCodes.providentFundNumber","lblProvidentFundNumber",
			"Provident Fund",PropertyKindType.ProvidentFundNumber),
	RISK_FUND_CODE("associatedCodes.riskFundCode","lblRiskFundCode","Risk Fund Code",
			 PropertyKindType.RiskFundCode),
	STANLIB_OFFSHORE_UNIT_TRUST_CODE("associatedCodes.stanlibOffshoreUnitTrustCode","lblStanlibOffshoreUnitTrustCode",
			"Stanlib Offshore Unit Trust Code", PropertyKindType.StanlibOffshoreUnitTrustCode),
	//INC390730
	STANLIB_UNIT_TRUST_CODE("associatedCodes.stanlinkUnitTrustCode","lblUnitTrustCode",
			"Stanlib Collective Investments Code(Unit Trust)",PropertyKindType.StanlibUnitTrustCode),
	STANLIB_LINKED_BUSINESS_CODE("associatedCodes.stanlibLinkedBusinessCode",
			"lblStanlibLinkedBusinessCode","Stanlib Wealth Code(Linked)",
			PropertyKindType.StanlibLinkedBusinessCode),
			
	STANDARD_BANK_BOND_ACC_NUMBER_1("associatedCodes.standardBankBondAccountNumber1",
			"lblStandardBankBondAccountNumber1","Standard Bank Bond Account Number",
			PropertyKindType.StandardBankBondAccountNumber),
	STANDARD_BANK_BOND_ACC_NUMBER_2("associatedCodes.standardBankBondAccountNumber2",
			"lblStandardBankBondAccountNumber2","Standard Bank Bond Account Number",
			PropertyKindType.StandardBankBondAccountNumber),
	STANDARD_BANK_BOND_ACC_NUMBER_3("associatedCodes.standardBankBondAccountNumber3",
			"lblStandardBankBondAccountNumber3","Standard Bank Bond Account Number",
			PropertyKindType.StandardBankBondAccountNumber),
	STANDARD_BANK_BOND_ACC_NUMBER_4("associatedCodes.standardBankBondAccountNumber4",
			"lblStandardBankBondAccountNumber4","Standard Bank Bond Account Number",
			PropertyKindType.StandardBankBondAccountNumber),
	STANDARD_BANK_BOND_ACC_NUMBER_5("associatedCodes.standardBankBondAccountNumber5",
			"lblStandardBankBondAccountNumber5","Standard Bank Bond Account Number",
			PropertyKindType.StandardBankBondAccountNumber),

	/**
	 * Provident Fund properties
	 */
	ISPROVIDENTFUNDMEMBER("providentFundDetail.providentFundMember",
					"lblIsProvidentFundMember","Provident Fund Member",PropertyKindType.IsProvidentFundMember),
	HASGROUPLIFEBENEFIT(
			"providentFundDetail.groupLifeBenefit", "lblHasGroupLifeBenefit",
			"Group Life Benefit", PropertyKindType.HasGroupLifeBenefit), 
	HASIPPDISABILITYBENEFIT(
			"providentFundDetail.iPPDisabilityBenefit", "lblHasIPPDisabilityBenefit",
			"IPP Benefit(Disability)", PropertyKindType.HasIPPDisabilityBenefit), 
	HASIPPPLUSDISABILITYBENEFIT(
			"providentFundDetail.iPPPlusDisabilityBenefit", "lblHasIPPPlusDisabilityBenefit",
			"IPP Plus Benefit(Disability)", PropertyKindType.HasIPPPlusDisabilityBenefit), 
	HASDREADDISEASEBENEFIT(
			"providentFundDetail.dreadDiseaseBenefit", "lblHasDreadDiseaseBenefit",
			"Dread Disease Benefit", PropertyKindType.HasDreadDiseaseBenefit),
	HASSPOUSEDEATHBENEFIT(
			"providentFundDetail.spouseDeathBenefit", "lblHasSpouseDeathBenefit",
			"Spouse's Death Benefit", PropertyKindType.HasSpouseDeathBenefit), 
	HASFAMILYBENEFIT(
			"providentFundDetail.familyBenefit", "lblHasFamilyBenefit",
			"Family Benefit", PropertyKindType.HasFamilyBenefit),
	ISONEPERCENTBONDMEMBER(
			"providentFundDetail.onePercentBondMember", "lblIsOnePercentBondMember",
			"1% Bond Member", PropertyKindType.IsOnePercentBondMember),
	ANNUALPENSIONABLEEARNINGS(
			"providentFundDetail.annualPensionableEarnings", "lblAnnualPensionableEarnings",
			"Annual Pensionable Earnings(IPP)", PropertyKindType.AnnualPensionableEarnings),
	APPROVEDGROUPLIFEBENEFIT(
			"providentFundDetail.approvedGroupLifeBenefit", "lblApprovedGroupLifeBenefit",
			"Approved Group Life Benefit", PropertyKindType.HasApprovedGroupLifeBenefit),
	UNAPPROVEDGROUPLIFEBENEFIT(
			"providentFundDetail.unapprovedGroupLifeBenefit", "lblUnapprovedGroupLifeBenefit",
			"Unapproved Group Life Benefit", PropertyKindType.HasUnapprovedGroupLifeBenefit),
		
	/**
	 * Payment Details
	 */
	PAY_TO_CHOICE("paysTo","lblPayTo","Pays To",null),
	PAY_TO_ORGANISATION,
	PAY_TO_EFFECTIVE_FROM("paymentDetails.effectiveFrom","lblEffectiveFrom","Effective From",null),
	/**
	 * FAIS License details
	 */
	FAIS_LICENSE_NUMBER("faisLicenseDTO.licenseNumber","lblLicenseNumber","FAIS License Number",null),
	FSP_FAIS_LICENSE_NUMBER("fspFAISLicence.licenseNumber","lblLicenseNumber","FSP FAIS License Number",null),
	FAIS_FSP("faisLicenseDTO.fsp","lblFsp","FSP",null),
	
	FAIS_LICENSE_EFFECTIVE_DATE("faisLicenseDTO.effectiveFrom",
			"lblEeffectiveFrom","FAIS License Effective From",null),
	FAIS_LICENSE_STATUS("faisLicenseDTO.faisLicenseStatus",
			"lblFAISLicenseStatus","FAIS License Status",null),
	FAIS_LICENSE_CATEGORY("faisLicenseDTO.longTermInsuranceCategory",
					"lblLongTermInsuranceCategory","Long Term Insurance Category",null),
	FAIS_FSB_UPDATED("faisLicenseDTO.fsbUpdated",
					"lblFsbUpdated","FSB Updated",null),
	FAIS_RETAIL_PENSION_BENFIT("faisLicenseDTO.retailPensionBenefit",
			"lblRetailPensionBenefit","Retail Pension Benefits",null),
	FAIS_PENSION_BENFIT("faisLicenseDTO.pensionBenefit",
			"lblPensionBenefit","Pension Fund Benefits",null),
	FAIS_HEALTH_BENEFIT("faisLicenseDTO.healthBenefit",
			"lblHealthBenefit","Health Service Benefits",null),
	FAIS_PARTIC_COLL_INVESTMENTS("faisLicenseDTO.collectiveInvestmentParticip",
			"lblCollectiveInvestmentParticip",
			"Participatory Interest In One Or More Collective Investment",null),
	FAIS_CLICK_HERE("clickHereForMoreInfo",
					"lblClickHere",
					"Click here for more info",null),
	FAIS_LICENSE_MEDICAL_ACCREDITATION("faisLicenseDTO.medicalAccreditation",
							"lblMedicalAccreditation","Medical Accreditation Number",null),
	/**
	 * Franchise pool account details 
	 */
	FRANCHISE_POOL_ACCOUNT_PANEL,
	FRANCHISE_CREATE_POOL_ACCOUNT("createPoolAccount",
			"lblCreatePoolAccount","Create Pool Account",null),
	FRANCHISE_POOL_ACCOUNT_EFFECTIVE_DATE("poolAccountEffectiveDate",
			"lblPoolAccountEffectiveDate","Pool Account Effective Date",null),
	FRANCHISE_POOL_TRANSFER_PERCENTAGE("poolTransferPercentage",
			"lblPoolTransferPercentage","Pool Transfer Percentage",null),
	FRANCHISE_POOL_TRANSFER_EFFECTIVE_DATE("poolTransferPercentageEffectiveDate",
			"lblPoolTransferPercentageEffectiveDate","Pool Transfer Percentage Effective Date",null),
	FRANCHISE_POOL_INTEREST_RATE("poolAccountInterestRate",
			"lblPoolAccountInterestRate","Pool Account Interest Rate",null),
	FRANCHISE_POOL_INTEREST_RATE_EFFECTIVE_DATE("poolAccountInterestRateEffectiveDate",
			"lblPoolAccountInterestRateEffectiveDate","Pool Account Interest Rate Effective Date",null),
	/**
	 * Distribution Details
	 */
	DISTRIBUTION_TEMPLATE("distributionDetails","lblDistributionDetails","Distribution Template",
			PropertyKindType.Template),
	DISTRIBUTION_TEMPLATE_EFFECTIVE_FROM("distributionDetails.effectiveFrom",
			"lblDistributionEffectiveFrom","Effective From",
			PropertyKindType.Template),
	/**
	 * Fixed Earnings/Deductions
	 */
	FIXED_EARNING_CONGLOMERATE_WARNING(null,null,null,null),
	FIXED_EARNING_GRID(null,null,null,null),
	FIXED_EARNING_BUTTONS(null,null,null,null),
	
	FIXED_DEDUCTION_CONGLOMERATE_WARNING(null,null,null,null),
	FIXED_DEDUCTION_GRID(null,null,null,null),
	FIXED_DEDUCTION_BUTTONS(null,null,null,null),
	/**
	 * Enums for Payment Scheduler
	 */
	DO_NOT_PAY("doNotPay","lblDoNotPay","Do Not Pay",null),
	REQUESTED_BY("requestedBy","lblRequestedBy","Requested By",null),
	EFFECTIVE_DT_SUSPEND("effDateSuspend","lblEffDateSuspend","Effective From",null),
	EFFECTIVE_DT_CUSTOM("effDateCustom","lblEffDateCustom","Effective Date",null),
	PRE_ISSUE_STATUS("preIssueStatus","lblPreIssueStatus","Pre-Issue Status",PropertyKindType.PreIssueStatus),
	MONTH_END("monthEnd","lblMonthEnd","Month End",null),
	CALENDER_SCHEDULE("calSchedule","lblCalSchedPanel","Calendar Schedule",null),
	PAY_WEEKLY("payWeekly","lblPayWeekly","Pay Weekly(Weekday)",null),
	PAY_DAILY("payDaily","lblPayDaily","Pay Daily",null),
	NEXT_DUE_DT("nextDueDate","lblNextDueDate","Next Due Date",null),
	ADD_SCHEDULES("addSchedDate","lblAddSchedDate","Add Schedules",null),
	COMMENTS("comments","lblComments","Comments",null),
	/**
	 * Enums for Associated Agreement Details
	 */
	COMM_KIND("commissionKind","lblCommissionKind","Commission Kind",null),
	ASSOCIATED_AGMT("associatedAgreement","lblAssociatedAgmt","Associated Agreement",null),
	ASSOCIATED_PERCENTAGE("associatedPercentage","lblAssociatedPercent","Associated Percentage",null),
	
	/**
	 * Enums for Advisor Quality Code Details
	 */
	CALC_AQC_VALUES("calcAQCDTO.aqcValueDTO.text","lblCalcAqcValue","Calculated AQC Value",null),
	EFFECTIVE_AQC_VALUES("effAqcValue","lblEffAqcValue","Effective AQC Value",null),
	EFFECTIVE_SEGMENT("segment","lblSegment","EFFECTIVE SEGMENT",null),

	//Added for LCB Accreditation Project - Pritam -12/12/12
	CORP_ADDENDUM_SIGNED("corpAddendumSigned.value",
			"lblCorpAddendumSigned","Corporate Addendum Signed",PropertyKindType.CorpAddendumSigned),
	HOLD_CORP_COMMISSION("holdCorpCommission.value",
			"lblHoldCorpCommission","Hold Corporate Commission",PropertyKindType.HoldCorpCommission),
	/**
	 * Enums for FICA Details Panel
	 */
	
	CURRENT_CATEGORY("currentCategory","lblCurrentCategory","Current Category",null),
	CAT_EFFECTIVE_DT("catEffDate","lblCatEffDate","Category Effective Date",null),
	LAST_MODIFIED_BY("lastModifiedBy","lblLastModifiedBy","Last Modified By",null),
	LAST_MODIFIED_DT("lastModifiedDate","lblLastModifiedDt","Last Modfied Date",null),
	AUTHORISED_BY("authroizedBy","lblAuthorisedBy","Authorized By",null),
	COMPLIANCE_OFFICER("complianceOfficer","lblComplianceOfficer","Compliance Officer",null),
	MOST_RECENT_CERT_RECD_ON_DT("ficaCertificateDTO.mostRecentCertRecdOn","lblMostRecCertRecdOn","Most Recent Certificate Received On",null),
	CERT_END_DT("ficaCertificateDTO.endDate","lblCertEndDate","End Date",null),
	CERT_DECLINE_REASON("ficaCertificateDTO.declineReason","lblCertDeclineReason","Decline Reason",null),
	//Added for Tax project-IRP5 Bi-Annual submission-pks2802-20/08/13
	SOLE_PROPRIETOR("soleProprietor","lblIsSoleProprietor","Sole Proprietor",null),
	
	//RXS 1408 ADDED for FR2 INCLUDE In Manpower Reporting - RAVISH SEHGAL
	INCLUDE_IN_MANPOWER_REPORTING("includeInManpowerReporting","lblIncludeInManpowerReporting","Include in Manpower Reporting",null),
	
	//RXS 1408 ADDED for Hierarchy FR3.6 Employee Number- RAVISH SEHGAL
	EMPLOYEE_NUMBER("employeeNumber","lblEmployeeNumber","Employee Number",null),
	//RXS 1408 ADDED for Hierarchy FR3.2 salesCategory - RAVISH SEHGAL  	
	SALES_CATEGORY("salesCategory","lblSalesCategory","Sales Category", PropertyKindType.SalesCategory),
	LIBERTY_TENURE("libertyTenure","lblLibertyTenure","Liberty Tenure", PropertyKindType.LibertyTenure),// SSM2707 ADDED for FR15 Tenure SWETA MENON
	//MZL 2611 ADDED for FR2.8.2 LBF Remuneration Category - MOHAMMED LORGAT
	LBF_REMUNERATION_CATEGORY("lbfRemunerationCategory","lblLBFRemunerationCategory","LBF Remuneration Category", PropertyKindType.LbfRemunerationCategory),
	LBF_HOME_ADD_BUTTON("lbfAddButton.value", "lbfAddButton", "Add LBF Home Role", null),
	//STOPSTATEMENTDISTRIBUTION("stopStatementDistribution.value","lblstopStatementDistribution","Stop Statement Distribution",PropertyKindType.StopStatementDistribution),
	//SSM2707 ADDED for Hierarchy FR3.5 PrimaryAgreement - SWETA MENON	
	PRIMARY_AGREEMENT("primaryAgreement","lblPrimaryAgreement","Primary Agreement", null),
	//pzm2509 added for AML history button
	DISPLAY_HISTORY_BUTTON("displayHistoryBtn",null,"History",null),
	// Payment Scheduler Columns AML
	PAYSCHED_HISTORY_BUTTON("dnpHistoryButton", "dnpHistoryButton", "History", null),
	PAY_SCHED_DO_NOT_PAY("doNotPay",null,"Suspend Settlement",null),
	EFFECTIVE_FROM ("historyEffectiveDate",null,"Effective From",null),
	EFFECTIVE_DATE ("historyEndDate",null,"Effective Date",null),
	PAY_SCHED_COMMENT ("comments",null,"Comment",null),
	REQUESTED_BY_HISTORY ("requestedBy",null,"Requested By",null),
	/* Columns for History button pop-up  */
	//ZZT2108 ADDED FOR AGENCY POOL ACCOUNT
	INTO_POOL_RATE_OVERRIDE("overrideIntoAgencyPoolRate", "lblIntoPoolRateOverride", "Into Pool Rate Override", null),
	NEXT_INTO_POOL_RATE_OVERRIDE("nextOverrideIntoAgencyPoolRate", "lblNextIntoPoolRateOverride", "Into Pool Rate Override For Next Month", null),
	POOL_DRAW_RATE_SELECTED("poolDrawRateSelected", "lblPoolDrawRateSelected", "Pool Draw Rate Selected", null),
	POOL_DRAW_RATE_DERIVED("poolDrawRateDerived", "lblPoolDrawRateDerived", "Pool Draw Rate Derived (maximum)", null),
	RELEASE_POOL_DRAW_IN_MONTH_END("releasePoolDrawInNextMonthEnd","lblReleasePoolDrawInNextMonthEnd", "Release Pool Draw In Next Month-End",null),
	POOL_DRAW_OPTION("poolDrawOption", "lblPoolDrawOption", "Pool Draw Option", null),
	STOP_INTO_POOL_TRANSFERS("stopIntopoolTransfers", "lblStopIntopoolTransfers", "Stop Into Pool Transfers", null),
	RELEASE_POOL_BALANCE("releasePoolBalance", "lblReleasePoolBalance", "Release Pool Balance", null),
	REQUESTED_PAYMENT_AMOUNT("requestedPaymentAmount", "lblRequestedPaymentAmount", "Requested Payment Amount", null),
	REQUESTED_PAYMENT_PERCENTANGE("requestedPaymentPercentage", "lblRequestedPaymentPercentage", "Requested Payment as Percentage of Current Balance", null),
	CURRENT_POOL_BALANCE("currentPoolBalance", "lblCurrentPoolBalance", "Current Pool Balance", null),
	EXPECTED_PAYMENT("expectedPayment", "lblExpectedPayment", "Expected Payment", null),
	NEXT_INTO_POOL_RATE_OVERRIDE_END("nextOverrideEnd", "lblnextOverrideEnd", "Next End Date", null),
	STOP_AGENCY_POOL_TRANSFER_COMMENTS("comments","lblComments","Comments",null),
	//PZM2509 -  BBBEE LIB4352
	SURETYDETAILS("SuretyDetails.value","lblSuretyDetails","Signed Surety ",PropertyKindType.SuretyDetails),
	ISPERSONALSERVICESTRUST("isPersonalServicesTrust.value", "lblIsPersonalServicesTrust","Personal Services Trust/Company",PropertyKindType.IsPersonalServicesTrust),
	SCRIPTEDADVISORCHECK("scriptedAdvisorCheck.value","lblScriptedAdvisorCheck","Scripted Advisor Check",PropertyKindType.ScriptedAdvisorCheck),

	//SBS0510 - AutoGen 13
	ORGANISATIONMIDSIX("organisationMidsix.value","lblOrganisationMidSix","Organisation Identifier ",PropertyKindType.OrganisationMidsix),
	//PZM2509 - New to industry
	SUBSIDYNEWINDUSTRYTYPE("subsidyNewIndustryType", "lblSubsidyNewIndustryType", "Subsidy New Industry Type",PropertyKindType.SubsidyNewIndustryType),
	SUBSIDYNEWINDUSTRYSTARTDATE("subsidyNewIndustryStartDate", "lblSubsidyNewIndustryStartDate", "Subsidy New Industry StartDate",PropertyKindType.SubsidyNewIndustryStartDate),
	SUBSIDYNEWINDUSTRYENDDATE("subsidyNewIndustryEndDate", "lblSubsidyNewIndustryEndDate", "Subsidy New Industry EndDate",PropertyKindType.SubsidyNewIndustryEndDate),
	SUBSIDYNEWINDUSTRYAMOUNT("subsidyNewIndustryAmount", "lblSubsidyNewIndustryAmount", "Subsidy New Industry Amount",PropertyKindType.SubsidyNewIndustryAmount),
			
	PFOVERRIDERATE("providentFundDetail.overrideRate.value", "lblpfoverrideRate","Override Rate", PropertyKindType.PFOverrideRate),
	PFOVERRIDEREASON("providentFundDetail.pfOverrideReason.value", "lblpfoverridereason","Override Reason:", PropertyKindType.PFOverrideReason),
	PFOVERRIDESTARTDATE("overrideStartDate", "lblpfoverrideStartDate","Override Start Date", PropertyKindType.PFOverrideStartDate),
	PFOVERRIDEENDDATE("overrideEndDate", "lblpfoverrideEndDate","Override End Date", PropertyKindType.PFOverrideEndDate);
		 

	private String fieldId;
	private String labelId;
	private String description;
	private PropertyKindType propertyKind;
	
	private AgreementGUIField() {
		
	}
	
	private AgreementGUIField(String fieldId,String labelId,String description,PropertyKindType propertyKind) {
		this.fieldId=fieldId;
		this.labelId=labelId;
		this.description=description;
		this.propertyKind=propertyKind;
	}
	
	public String getFieldId() {
		return fieldId;
	}
	
	public String getLabelId() {
		return labelId;
	}
	
	public String getDescription() {
		return description;
	}

	public PropertyKindType getPropertyKind() {
		return propertyKind;
	}
	
	public static List<AgreementGUIField> getPaymentSchedulerLabels()
	{
		List<AgreementGUIField> paymentSchedulerList = new ArrayList<AgreementGUIField>();
		paymentSchedulerList.add(DO_NOT_PAY);
		paymentSchedulerList.add(REQUESTED_BY);
		paymentSchedulerList.add(EFFECTIVE_DT_SUSPEND);
		paymentSchedulerList.add(EFFECTIVE_DT_CUSTOM);
		paymentSchedulerList.add(PRE_ISSUE_STATUS);
		paymentSchedulerList.add(MONTH_END);
		paymentSchedulerList.add(CALENDER_SCHEDULE);
		paymentSchedulerList.add(PAY_WEEKLY);
		paymentSchedulerList.add(PAY_DAILY);
		paymentSchedulerList.add(NEXT_DUE_DT);
		paymentSchedulerList.add(COMMENTS);	
		
		return paymentSchedulerList;
		
	}
	
	public static List<AgreementGUIField> getPaymentSchedulerHistoryLabels()
	{
		List<AgreementGUIField> paymentSchedulerList = new ArrayList<AgreementGUIField>();
		paymentSchedulerList.add(PAY_SCHED_DO_NOT_PAY);
		paymentSchedulerList.add(EFFECTIVE_FROM);
		paymentSchedulerList.add(EFFECTIVE_DATE);
		paymentSchedulerList.add(PAY_SCHED_COMMENT);
		paymentSchedulerList.add(REQUESTED_BY_HISTORY);
		
		return paymentSchedulerList;
		
	}
	
	public static List<AgreementGUIField> getAssociatedAgreementsLabels()
	{
		List<AgreementGUIField> assoAgmts = new ArrayList<AgreementGUIField>();
		assoAgmts.add(COMM_KIND);
		assoAgmts.add(ASSOCIATED_AGMT);
		assoAgmts.add(ASSOCIATED_PERCENTAGE);
		assoAgmts.add(START_DATE);
		assoAgmts.add(END_DATE);
			
		return assoAgmts;
		
	}

}
