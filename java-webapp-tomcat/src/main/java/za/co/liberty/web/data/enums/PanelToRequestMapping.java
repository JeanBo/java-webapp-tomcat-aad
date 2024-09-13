package za.co.liberty.web.data.enums;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import za.co.liberty.interfaces.agreements.requests.RequestKindType;
import za.co.liberty.web.pages.admin.ratingtables.MIRatingFilterPanel;
import za.co.liberty.web.pages.admin.ratingtables.MIRatingGUIPage;
import za.co.liberty.web.pages.advancedPractice.AdvancedPracticePanel;
import za.co.liberty.web.pages.advancedPractice.MaintainAdvancedPracticePage;
import za.co.liberty.web.pages.businesscard.BusinessCardDetailsPage;
import za.co.liberty.web.pages.businesscard.BusinessCardDetailsPanel;
import za.co.liberty.web.pages.businesscard.OtherLinkedDetailsPanel;
import za.co.liberty.web.pages.contactdetail.ContactDetailsPanel;
import za.co.liberty.web.pages.core.BookTransferPage;
import za.co.liberty.web.pages.core.BookTransferPanel;
import za.co.liberty.web.pages.core.ContractTransferPage;
import za.co.liberty.web.pages.core.ContractTransferPanel;
import za.co.liberty.web.pages.core.SegmentedTransferPage;
import za.co.liberty.web.pages.core.SegmentedTransferPanel;
import za.co.liberty.web.pages.franchisetemplates.FranchiseTemplateDetailsPanel;
import za.co.liberty.web.pages.franchisetemplates.FranchiseTemplatePanel;
import za.co.liberty.web.pages.franchisetemplates.MaintainFranchiseTemplatePage;
import za.co.liberty.web.pages.hierarchy.HierarchyNodePanel;
import za.co.liberty.web.pages.hierarchy.MIReportingDetailsPanel;
import za.co.liberty.web.pages.hierarchy.MaintainHierarchyPage;
import za.co.liberty.web.pages.maintainagreement.AddAgreementWizard;
import za.co.liberty.web.pages.maintainagreement.AdvisorQualityCodePanel;
import za.co.liberty.web.pages.maintainagreement.AgencyPoolAccountDetailsPanel;
import za.co.liberty.web.pages.maintainagreement.AgreementCodesPanel;
import za.co.liberty.web.pages.maintainagreement.AgreementDetailsPanel;
import za.co.liberty.web.pages.maintainagreement.AgreementHierarchyLBFPanel;
import za.co.liberty.web.pages.maintainagreement.AgreementHierarchyPanel;
import za.co.liberty.web.pages.maintainagreement.AgreementIncentivesPanel;
import za.co.liberty.web.pages.maintainagreement.AssociatedAgreementsPanel;
import za.co.liberty.web.pages.maintainagreement.DistributionPanel;
import za.co.liberty.web.pages.maintainagreement.DistributionPaysToPanel;
import za.co.liberty.web.pages.maintainagreement.FAISLicensePanel;
import za.co.liberty.web.pages.maintainagreement.FAISLicensePanelOLD;
import za.co.liberty.web.pages.maintainagreement.FranchisePoolAccountPanel;
import za.co.liberty.web.pages.maintainagreement.MaintainAgreementPage;
import za.co.liberty.web.pages.maintainagreement.OtherPartyRolesPanel;
import za.co.liberty.web.pages.maintainagreement.PaymentSchedulerPanel;
import za.co.liberty.web.pages.maintainagreement.PaysToPanel;
import za.co.liberty.web.pages.maintainagreement.ProvidentFundDetailsPanel;
import za.co.liberty.web.pages.maintainagreement.TerminateAgreementWizard;
import za.co.liberty.web.pages.maintainagreement.payroll.FixedDeductionsPanel;
import za.co.liberty.web.pages.maintainagreement.payroll.FixedEarningsAndDeductionsPanel;
import za.co.liberty.web.pages.maintainagreement.payroll.FixedEarningsPanel;
import za.co.liberty.web.pages.party.BankingDetailsPanel;
import za.co.liberty.web.pages.party.MaintainPartyPage;
import za.co.liberty.web.pages.party.MedicalAidDetailsPanel;
import za.co.liberty.web.pages.party.OrganisationDetailsPanel;
import za.co.liberty.web.pages.party.PartyHierarchyPanel;
import za.co.liberty.web.pages.party.PersonDetailsPanel;
import za.co.liberty.web.pages.party.ProvidentFundBeneficiariesPanel;
import za.co.liberty.web.pages.request.tree.RequestTreePanel;
import za.co.liberty.web.pages.salesBCLinking.SalesBCLinkingPage;
import za.co.liberty.web.pages.salesBCLinking.SalesBCLinkingPanel;
import za.co.liberty.web.pages.taxdetails.TaxDetailsPanel;
import za.co.liberty.web.pages.transactions.ManualSettleTransactionPanel;
import za.co.liberty.web.pages.transactions.PolicyTransactionDPEPanel;
import za.co.liberty.web.pages.transactions.PolicyTransactionInfoPanel;
import za.co.liberty.web.pages.transactions.PolicyTransactionsPage;
import za.co.liberty.web.pages.transactions.ProcessAdvanceTransactionPanel;
import za.co.liberty.web.pages.transactions.TransactionExternalPaymentPanel;
import za.co.liberty.web.pages.transactions.TransactionGuiPage;
import za.co.liberty.web.pages.transactions.TransactionVariableEarningDeductionPanel;

/**
 * Keep mappings of Requests to (Panels and Pages)
 * 
 * @author DZS2610
 *
 */
public enum PanelToRequestMapping {

	AGREEMENT_DETAILS(AgreementDetailsPanel.class,MaintainAgreementPage.class,
			new RequestKindType[]{RequestKindType.MaintainIntermediaryAgreement,
								  RequestKindType.TerminateIntermediaryAgreement,
		                          RequestKindType.CreateAgreement,
		                          RequestKindType.ActivateAgreement,
		                          RequestKindType.MaintainCorpCommIndicator,
		                          RequestKindType.MaintainPrimaryAgreement}),// SSM2707 Added for Hierarchy FR3.5 Primary Agreement SWETA MENON
    ADD_AGREEMENT_DETAILS(AGREEMENT_DETAILS.panel,AddAgreementWizard.class,
		AGREEMENT_DETAILS.requestKindTypes),
	TERMINATE_AGREEMENT_DETAILS(AGREEMENT_DETAILS.panel,TerminateAgreementWizard.class,
		AGREEMENT_DETAILS.requestKindTypes),
		                      		                          
	PAYS_TO(PaysToPanel.class,MaintainAgreementPage.class,
			new RequestKindType[]{RequestKindType.MaintainPaysTo,RequestKindType.ActivateAgreement}),
	ADD_PAYS_TO(PAYS_TO.panel,AddAgreementWizard.class,
			PAYS_TO.requestKindTypes),
	TERMINATE_PAYS_TO(PAYS_TO.panel,TerminateAgreementWizard.class,
			PAYS_TO.requestKindTypes),
			
	DISTRIBUTION_PAYSTO_FRANCHISE_POOL(DistributionPaysToPanel.class,MaintainAgreementPage.class,
			new RequestKindType[]{RequestKindType.MaintainPaysTo, 
								  RequestKindType.MaintainDistributionDetails, 
								  RequestKindType.MaintainPoolAccount,
								  RequestKindType.ActivateAgreement}),
								  
	DISTRIBUTION_TEMPLATE(DistributionPanel.class,MaintainAgreementPage.class,
			new RequestKindType[]{RequestKindType.MaintainDistributionDetails}),
	ADD_DISTRIBUTION_TEMPLATE(DISTRIBUTION_TEMPLATE.panel,AddAgreementWizard.class,
			DISTRIBUTION_TEMPLATE.requestKindTypes),
	TERMINATE_DISTRIBUTION_TEMPLATE(DISTRIBUTION_TEMPLATE.panel,TerminateAgreementWizard.class,
			DISTRIBUTION_TEMPLATE.requestKindTypes),
			
	FRANCHISE_POOL(FranchisePoolAccountPanel.class,MaintainAgreementPage.class,
			new RequestKindType[]{RequestKindType.MaintainPoolAccount,RequestKindType.ActivateAgreement}),
	ADD_FRANCHISE_POOL(FRANCHISE_POOL.panel,AddAgreementWizard.class,
			FRANCHISE_POOL.requestKindTypes),
	TERMINATE_FRANCHISE_POOL(FRANCHISE_POOL.panel,TerminateAgreementWizard.class,
			FRANCHISE_POOL.requestKindTypes),
			
	AGREEMENT_CODES(AgreementCodesPanel.class,MaintainAgreementPage.class,
			new RequestKindType[]{RequestKindType.MaintainAgreementCodes}),
	ADD_AGREEMENT_CODES(AGREEMENT_CODES.panel,AddAgreementWizard.class,
			AGREEMENT_CODES.requestKindTypes),
	FAIS_LICENSE_OLD(FAISLicensePanelOLD.class,MaintainAgreementPage.class,
					new RequestKindType[]{RequestKindType.MaintainFAISLicense}),
	FAIS_LICENSE(FAISLicensePanel.class,MaintainAgreementPage.class,
			new RequestKindType[]{RequestKindType.MaintainFAISLicense,
		RequestKindType.MaintainFAISLicenseStatus, RequestKindType.MaintainAgreementSupervisors}),
	ADD_FAIS_LICENSE(FAIS_LICENSE.panel,AddAgreementWizard.class,
			FAIS_LICENSE.requestKindTypes),			
	FIXED_EARNINGS(FixedEarningsPanel.class,MaintainAgreementPage.class,
			new RequestKindType[]{RequestKindType.MaintainFixedEarningDetails}),
	ADD_FIXED_EARNINGS(FIXED_EARNINGS.panel,AddAgreementWizard.class,
			FIXED_EARNINGS.requestKindTypes),
	TERMINATE_FIXED_EARNINGS(FIXED_EARNINGS.panel,TerminateAgreementWizard.class,
			FIXED_EARNINGS.requestKindTypes),
			
	FIXED_DEDUCTIONS(FixedDeductionsPanel.class,MaintainAgreementPage.class,
			new RequestKindType[]{RequestKindType.MaintainFixedDeductionDetails}),
	ADD_FIXED_DEDUCTIONS(FIXED_DEDUCTIONS.panel,AddAgreementWizard.class,
			FIXED_DEDUCTIONS.requestKindTypes),
	TERMINATE_FIXED_DEDUCTIONS(FIXED_DEDUCTIONS.panel,TerminateAgreementWizard.class,
			FIXED_DEDUCTIONS.requestKindTypes),
			
	FIXED_EARNINGS_AND_DEDUCTIONS(FixedEarningsAndDeductionsPanel.class,MaintainAgreementPage.class,
			new RequestKindType[]{RequestKindType.MaintainFixedDeductionDetails,RequestKindType.MaintainFixedEarningDetails}),
			
	PARTY_CONTACT_DETAILS(ContactDetailsPanel.class, MaintainPartyPage.class,
			new RequestKindType[]{RequestKindType.MaintainContactDetails, RequestKindType.MaintainSecureContactDetails}),
	PARTY_PERSON_DETAILS(PersonDetailsPanel.class, MaintainPartyPage.class,
			new RequestKindType[]{RequestKindType.MaintainPartyDetails, RequestKindType.MaintainPartyWithApproval,RequestKindType.MaintainOtherSystemAccess}),
	PARTY_ORG_DETAILS(OrganisationDetailsPanel.class, MaintainPartyPage.class,
			new RequestKindType[]{RequestKindType.MaintainPartyDetails, RequestKindType.MaintainOrganisationKnownAsName}),
	HIERARCHY_NODE_DETAILS(HierarchyNodePanel.class, MaintainHierarchyPage.class,
			new RequestKindType[]{RequestKindType.MaintainHierarchyNodeDetails}),
	HIERARCHY_MI_DETAILS(MIReportingDetailsPanel.class, MaintainHierarchyPage.class,
					new RequestKindType[]{RequestKindType.MaintainHierarchyNodeMIDetails}),
	HIERARCHY_CONTACT_DETAILS(ContactDetailsPanel.class, MaintainHierarchyPage.class,
			new RequestKindType[]{RequestKindType.MaintainHierarchyNodeContactDetails}),
	AGREEMENT_HIERARCHY(AgreementHierarchyPanel.class, MaintainAgreementPage.class,
			new RequestKindType[]{RequestKindType.MaintainAgreementHierarchy,RequestKindType.MaintainAgreementHome,
		RequestKindType.MaintainAgreementServicingRelationships,RequestKindType.BranchTransfer, RequestKindType.MaintainAgreementSupervisors}),
	AGREEMENT_HIERARCHY_LBF(AgreementHierarchyLBFPanel.class, MaintainAgreementPage.class,
				new RequestKindType[]{RequestKindType.MaintainAgreementHierarchy,RequestKindType.MaintainAgreementHome,
			RequestKindType.MaintainAgreementServicingRelationships,RequestKindType.BranchTransfer, RequestKindType.MaintainAgreementSupervisors}),
	AGREEMENT_HIERARCHY_PARTYROLES(OtherPartyRolesPanel.class, MaintainAgreementPage.class,
				new RequestKindType[]{RequestKindType.MaintainAgreementSupervisors,RequestKindType.MaintainAgreementHierarchy}),
	
	PAYMENT_SCHEDULER_DETAILS(PaymentSchedulerPanel.class,MaintainAgreementPage.class,
			new RequestKindType[]{RequestKindType.MaintainPaymentSchedulerDetails,RequestKindType.MaintainPaymentSchedulerDetailsWithApproval,
		RequestKindType.MaintainPaysTo,RequestKindType.ActivateAgreement}),
	ADD_PAYMENT_SCHEDULER_DETAILS(PAYMENT_SCHEDULER_DETAILS.panel,AddAgreementWizard.class,
			PAYMENT_SCHEDULER_DETAILS.requestKindTypes),
	
	ASSOCIATED_AGREEMENT_DETAILS(AssociatedAgreementsPanel.class,MaintainAgreementPage.class,
			new RequestKindType[]{RequestKindType.MaintainAssociatedAgreements}),
			/**Added for Banking Details-pks2802-27/04/10*/
	PARTY_BANKING_DETAILS(BankingDetailsPanel.class,MaintainPartyPage.class,
			new RequestKindType[]{RequestKindType.MaintainPaymentDetails}),
	/**Added for tax details.  mzp0801*/
	PARTY_TAX_DETAILS(TaxDetailsPanel.class,MaintainPartyPage.class,
			new RequestKindType[]{RequestKindType.MaintainPayrollTaxDetails}),			
	PARTY_HIERARCHY_DETAILS(PartyHierarchyPanel.class,MaintainPartyPage.class,
			new RequestKindType[]{RequestKindType.MaintainLinkedAssistants,RequestKindType.MaintainPartnerships,RequestKindType.MaintainPartyRoleHierarchy}),
	BUSINESSCARD_DETAILS(BusinessCardDetailsPanel.class,BusinessCardDetailsPage.class,
			new RequestKindType[]{RequestKindType.MaintainBusinessCardDetails}),
	BUSINESSCARD_PA_DETAILS(PartyHierarchyPanel.class,BusinessCardDetailsPage.class,
			new RequestKindType[]{RequestKindType.MaintainLinkedAssistants,RequestKindType.MaintainPartnerships}),
	BUSINESSCARD_OTHERLINKEDPARTIES_DETAILS(OtherLinkedDetailsPanel.class,BusinessCardDetailsPage.class,
					new RequestKindType[]{RequestKindType.MaintainExplicitAgreementPrivileges}),
	BUSINESSCARD_PA_CONTACT_DETAILS(ContactDetailsPanel.class,BusinessCardDetailsPage.class,
			new RequestKindType[]{RequestKindType.MaintainContactDetails, RequestKindType.MaintainBusinessCardDetails}),
	//For Differential Pricing
	AQC_DETAILS(AdvisorQualityCodePanel.class,MaintainAgreementPage.class,
			new RequestKindType[]{RequestKindType.MaintainAdvisorQualityCode, RequestKindType.MaintainMaxUpfrontCommPercent}),
	//For AQC on Add Agreement Wizard
	ADD_AQC_DETAILS(AQC_DETAILS.panel,AddAgreementWizard.class,
			AQC_DETAILS.requestKindTypes),	
	INCENTIVE_DETAILS(AgreementIncentivesPanel.class,MaintainAgreementPage.class,
			new RequestKindType[]{RequestKindType.MaintainIncentiveDetails}),
	//For Franchise Template Details Maintanainced
	FRANCHISE_TEMPLATE_DETAILS(FranchiseTemplatePanel.class,MaintainFranchiseTemplatePage.class,
			new RequestKindType[]{RequestKindType.MaintainFranchiseTemplateDetails}),
	FRANCHISE_TEMPLATE_NEW_DETAILS(FranchiseTemplateDetailsPanel.class,MaintainFranchiseTemplatePage.class,
			new RequestKindType[]{RequestKindType.MaintainFranchiseTemplateDetails}),
	MEDICAL_AID_DETAILS(MedicalAidDetailsPanel.class, MaintainPartyPage.class,
					new RequestKindType[]{RequestKindType.MaintainMedicalAidDetails}),
	//For Medical Aid Details on Add Agreement Wizard
	ADD_MEDICAL_AID_DETAILS(MEDICAL_AID_DETAILS.panel,AddAgreementWizard.class,
			MEDICAL_AID_DETAILS.requestKindTypes),
	PROVIDENT_FUND(ProvidentFundDetailsPanel.class,MaintainAgreementPage.class,
			new RequestKindType[]{RequestKindType.MaintainProvidentFundOptions, RequestKindType.MaintainProvidentFundBeneficiaries}),

    	// MXM1904 Added This For Advanced Practice PROD00010430 07/02/2012  -->		
	MAINTAIN_ADVANCED_PRACTICE_PANEL(AdvancedPracticePanel.class,MaintainAdvancedPracticePage.class,
					new RequestKindType[]{RequestKindType.MaintainAdvancedPractice}),
	
//	Add for Core Succession 
	BOOK_TRANSFER(BookTransferPanel.class,BookTransferPage.class,
			new RequestKindType[]{RequestKindType.ProcessBookLevelTransfer}),
	
	SEGMENTED_TRANSFER(SegmentedTransferPanel.class,SegmentedTransferPage.class,
			new RequestKindType[]{RequestKindType.ProcessSegmentedContractTransfer}),
	
	CONTRACT_TRANSFER(ContractTransferPanel.class,ContractTransferPage.class,
			new RequestKindType[]{RequestKindType.ProcessContractTransfer}),

	MAINTAIN_MI_TABLE_DATA(MIRatingFilterPanel.class,MIRatingGUIPage.class,
			new RequestKindType[]{RequestKindType.MaintainMITableData}),
	DISTRIBUTE_POLICY_EARNING(PolicyTransactionDPEPanel.class,PolicyTransactionsPage.class,
			new RequestKindType[]{RequestKindType.DistributePolicyEarning}),
	RECORD_POLICY_INFO(PolicyTransactionInfoPanel.class,PolicyTransactionsPage.class,
					new RequestKindType[]{RequestKindType.RecordPolicyInfo}),
	
	/*SSM2707 Market Integration 14/12/2015 Begin*/
	MAINTAIN_SERVICING_RELATIONSHIPS(SalesBCLinkingPanel.class,
			SalesBCLinkingPage.class, new RequestKindType[] {
					RequestKindType.MaintainSingleRegionServicingRelationships,
					RequestKindType.MaintainCrossRegionServicingRelationships,
					RequestKindType.CreateServicingPanel }),
					/*SSM2707 Market Integration 14/12/2015 End*/
					
					/*ZZT2108: Agency Pool Account changes - 2017-08-31*/ 
					SET_INTO_POOL_RATE(AgencyPoolAccountDetailsPanel.class,
							MaintainAgreementPage.class, new RequestKindType[] {
									RequestKindType.SetOverrideIntoPoolRate,
									RequestKindType.AdhocAgencyPoolDraw,
									RequestKindType.StopAgencyPoolTransfer,
									RequestKindType.CloseAgencyPool}),
	/*SSM2707 Market Integration 14/12/2015 End*/
	
	//For Provident Fund Beneficiries Details
	PROVIDENT_FUND_BENEFICIRIES(ProvidentFundBeneficiariesPanel.class,MaintainAgreementPage.class,
			new RequestKindType[]{RequestKindType.MaintainProvidentFundBeneficiaries, RequestKindType.MaintainProvidentFundBeneficiaries}),
	
	/*MAINTAIN_PROVIDENT_FUND_BENEFICIARIES(ProvidentFundBeneficiariesPanel.class, MaintainPartyPage.class,
			new RequestKindType[]{RequestKindType.MaintainProvidentFundBeneficiaries});*/
	
//End of Core Succession
	
	PROCESS_EXTERNAL_PAYMENTS(TransactionExternalPaymentPanel.class, TransactionGuiPage.class,
			new RequestKindType[]{RequestKindType.ProcessExternalPayments}),
			
	DPE_PROPERTIES(RequestTreePanel.class, null, new RequestKindType[]{RequestKindType.DistributePolicyEarning}),

	VED(TransactionVariableEarningDeductionPanel.class, TransactionGuiPage.class,
			new RequestKindType[]{RequestKindType.ProcessVariableEarningsOrDeductions}),
	
	MANUAL_SETTLE(ManualSettleTransactionPanel.class, TransactionGuiPage.class,
			new RequestKindType[]{RequestKindType.ManualSettle}),
	
	PROCESS_ADVANCE(ProcessAdvanceTransactionPanel.class, TransactionGuiPage.class,
			new RequestKindType[]{RequestKindType.ProcessAdvance});
		
	
	
	/* Static */
	private static Map<Class, PanelToRequestMapping> panelMap;
	private static Map<Class, RequestKindType[]> panelToKindMap;
	
	/* Attributes */
	private Class panel;
	private Class page;
	private RequestKindType[] requestKindTypes;
	
	
	
	private PanelToRequestMapping(Class panel,Class page,RequestKindType[] requestKindTypes){
		this.panel = panel;
		this.page = page;
		this.requestKindTypes = requestKindTypes;
	}

	public Class getPage() {
		return page;
	}

	public Class getPanel() {
		return panel;
	}

	public RequestKindType[] getRequestKindTypes() {
		if(requestKindTypes == null){return null;}
		RequestKindType[] ret = new RequestKindType[requestKindTypes.length];
		int count = 0;
		for(RequestKindType type : requestKindTypes){
			ret[count++] = type;
		}
		return ret;
	}
	
	/**
	 * Returns RequestKindTypes using the page and panel classes for a lookup
	 * @param pageClass
	 * @param panelClass
	 * @return
	 */
	public static RequestKindType[] getMappingForPageAndPanel(Class pageClass,Class panelClass){
		for(PanelToRequestMapping mapping : PanelToRequestMapping.values()){
			if(mapping.getPanel() == panelClass && mapping.getPage() == pageClass){
				return mapping.getRequestKindTypes();
			}
		}
		return null;
	}
	
//	/**
//	 * Return the enum for the specified panel class.  Null if no such panel is defined.
//	 * 
//	 * @param panelClass
//	 * @return
//	 */
//	public static PanelToRequestMapping[] getMappingEnumForPanel(Class panelClass) {
//		if (panelMap == null) {
//			Map<Class, PanelToRequestMapping> map = new HashMap<Class, PanelToRequestMapping>();
//			for (PanelToRequestMapping e : PanelToRequestMapping.values()) {
//				PanelToRequestMapping
//				map.put(e.getPanel(), e);
//			}
//			panelMap = map;
//		}
//		return panelMap.get(panelClass);
//	}
	
	/**
	 * Return the request kinds for the specified panel class.  Null if no such panel is defined.
	 * 
	 * @param panelClass
	 * @return
	 */
	public static RequestKindType[] getRequestKindsForPanel(Class panelClass) {
		if (panelMap == null) {
			/* Initialise the static map */
			
			// Do a temp map with a Set (ensure no duplicate request kinds)
			Map<Class, Set<RequestKindType>> tempMap = new HashMap<Class, Set<RequestKindType>>();
			for (PanelToRequestMapping e : PanelToRequestMapping.values()) {
				Set<RequestKindType> set = tempMap.get(e.getPanel());
				if (set==null) {
					// first time
					set = new HashSet<RequestKindType>();
					
				} 
				Collections.addAll(set, e.getRequestKindTypes());
				tempMap.put(e.getPanel(), set);
			}

			// Now we update the final map
			Map<Class, RequestKindType[]> map = new HashMap<Class, RequestKindType[]>();
			for (Class c : tempMap.keySet()) {
				RequestKindType[] arr = tempMap.get(c).toArray(new RequestKindType[] {});
				map.put(c, arr);
			}
			panelToKindMap = map;
		}
		return panelToKindMap.get(panelClass);
	}

}
