package za.co.liberty.web.data.enums.fields;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import za.co.liberty.interfaces.agreements.requests.PropertyKindType;
import za.co.liberty.interfaces.agreements.requests.RequestKindType;

/**
 * This enum is used to map request kinds to AgreementGUIFields
 * @author kxd1203
 *
 */
public enum AgreementGUIFieldRequestMapping {
	
	MAINTAIN_AGREEMENT(RequestKindType.MaintainIntermediaryAgreement,
			new AgreementGUIField[] {
					AgreementGUIField.BROKER_BRANCH_FRANCHISE_GROUP,
					AgreementGUIField.BROKER_CONSULTANT_PRODUCTION_CLUB_STATUS,
					AgreementGUIField.CONSULTANT_CODE,
					AgreementGUIField.COST_CENTER,
					AgreementGUIField.DEDICATED_SBFC_CONSULTANT_TYPE,
					AgreementGUIField.EARLY_DEBITS_INDICATOR,
					AgreementGUIField.EARLY_DEBITS_REASON,
					AgreementGUIField.EARLY_DEBITS_START_DATE,
					AgreementGUIField.END_DATE,
					AgreementGUIField.ENTITY,
					AgreementGUIField.MANPOWER,
					AgreementGUIField.MONTHLY_GUARANTEED_AMOUNT,
					AgreementGUIField.NETWORK,
					AgreementGUIField.PREAUTH_AMOUNT,
					AgreementGUIField.PREAUTH_CATEGORY,
					AgreementGUIField.PREAUTH_OVERRIDE,
					AgreementGUIField.PRIMARY_COMPANY_CONTRACTED_TO,
					AgreementGUIField.PRODUCTION_CLUB_STATUS,
					AgreementGUIField.SEGMENT,
					AgreementGUIField.START_DATE,
					AgreementGUIField.STATUS,
					AgreementGUIField.STATUS_DATE,
					AgreementGUIField.STATUS_REASON,
					AgreementGUIField.SUPPORT_TYPE,
					AgreementGUIField.TITULAR_LEVEL,
					AgreementGUIField.KROLL_DONE,
					AgreementGUIField.FITPROPSEGMENT,
					AgreementGUIField.FITPROPWAIVECPDCHECK,
					AgreementGUIField.FITPROPWAIVECPDCHECKEND,
					AgreementGUIField.FITPROPWAIVEFAISADVISORCHECK,
					AgreementGUIField.FITPROPWAIVEFAISADVISORCHECKEND,
					AgreementGUIField.FITPROPWAIVEFAISFSPCHECK,
					AgreementGUIField.FITPROPWAIVEFAISFSPCHECKEND,
					AgreementGUIField.FITPROPWAIVEPRODUCTACCREDCHECK,
					AgreementGUIField.FITPROPWAIVEPRODUCTACCREDCHECKEND,
					AgreementGUIField.FITPROPWAIVERECHECK,
					AgreementGUIField.FITPROPWAIVERECHECKEND,					
					AgreementGUIField.FITPROPWAIVEADVICECHECK,
					AgreementGUIField.FITPROPWAIVEADVICECHECKEND,
					AgreementGUIField.FITPROPWAIVEINTERMEDIARYSERVICECHECK,
					AgreementGUIField.FITPROPWAIVEINTERMEDIARYSERVICECHECKEND,
					AgreementGUIField.MY_BANKING_NUM,
					AgreementGUIField.HAS_MEDICAL_CREDITS,
					AgreementGUIField.MANUAL_PRODUCTION_CLUB_STATUS,
					AgreementGUIField.CALCULATED_PRODUCTION_CLUB_STATUS,
					AgreementGUIField.MANUAL_PRODUCTION_CLUB_STATUS_END,
					AgreementGUIField.STOPSTATEMENTDISTRIBUTION,
					AgreementGUIField.SOLE_PROPRIETOR,
					AgreementGUIField.INCLUDE_IN_MANPOWER_REPORTING,
					AgreementGUIField.EMPLOYEE_NUMBER,
					AgreementGUIField.SALES_CATEGORY,
					AgreementGUIField.LBF_REMUNERATION_CATEGORY,
					AgreementGUIField.LIBERTY_TENURE,
					AgreementGUIField.SCRIPTEDADVISORCHECK,
					AgreementGUIField.ORGANISATIONMIDSIX,
					AgreementGUIField.SUBSIDYNEWINDUSTRYTYPE,
					AgreementGUIField.SUBSIDYNEWINDUSTRYSTARTDATE,
					AgreementGUIField.SUBSIDYNEWINDUSTRYENDDATE,
					AgreementGUIField.SUBSIDYNEWINDUSTRYAMOUNT					
			}),
	CREATE_AGREEMENT(RequestKindType.CreateAgreement,
			MAINTAIN_AGREEMENT.fields),
	
	TERMINATE_AGREEMENT(RequestKindType.TerminateIntermediaryAgreement,
			MAINTAIN_AGREEMENT.fields),
	MAINTAIN_AGREEMENT_CODES(RequestKindType.MaintainAgreementCodes,
			new AgreementGUIField[] {
				AgreementGUIField.BANK_CONSULTANT_CODE,
				AgreementGUIField.COMPASS_CODE,
				AgreementGUIField.LCB_QUANTUM_CODE,
				AgreementGUIField.LIBERTY_ACTIVE_CODE,
				AgreementGUIField.MASTHEAD_MEMBER_NUMBER,
				AgreementGUIField.MEDSCHEME_CODE,
				AgreementGUIField.PROVIDENT_FUND_NUMBER,
				AgreementGUIField.STANDARD_BANK_BOND_ACC_NUMBER_1,
				AgreementGUIField.STANDARD_BANK_BOND_ACC_NUMBER_2,
				AgreementGUIField.STANDARD_BANK_BOND_ACC_NUMBER_3,
				AgreementGUIField.STANDARD_BANK_BOND_ACC_NUMBER_4,
				AgreementGUIField.STANDARD_BANK_BOND_ACC_NUMBER_5,
				AgreementGUIField.STANLIB_LINKED_BUSINESS_CODE,
				AgreementGUIField.STANLIB_UNIT_TRUST_CODE,
				AgreementGUIField.STOPSTATEMENTDISTRIBUTION,
				AgreementGUIField.RISK_FUND_CODE,
				AgreementGUIField.STANLIB_OFFSHORE_UNIT_TRUST_CODE,
				AgreementGUIField.SCRIPTEDADVISORCHECK
				
	}),
	MAINTAIN_PAYS_TO(RequestKindType.MaintainPaysTo,
			new AgreementGUIField[] {
				AgreementGUIField.PAY_TO_CHOICE,
				AgreementGUIField.PAY_TO_EFFECTIVE_FROM
	}),
	MAINTAIN_FRANCHISE_POOL(RequestKindType.MaintainPoolAccount,
			new AgreementGUIField[] {
				AgreementGUIField.FRANCHISE_CREATE_POOL_ACCOUNT,
				AgreementGUIField.FRANCHISE_POOL_ACCOUNT_EFFECTIVE_DATE,
				AgreementGUIField.FRANCHISE_POOL_ACCOUNT_PANEL,
				AgreementGUIField.FRANCHISE_POOL_INTEREST_RATE,
				AgreementGUIField.FRANCHISE_POOL_INTEREST_RATE_EFFECTIVE_DATE,
				AgreementGUIField.FRANCHISE_POOL_TRANSFER_EFFECTIVE_DATE,
				AgreementGUIField.FRANCHISE_POOL_TRANSFER_PERCENTAGE
	}),
	MAINTAIN_DISTRIBUTION_DETAILS(RequestKindType.MaintainDistributionDetails,
			new AgreementGUIField[] {
				AgreementGUIField.DISTRIBUTION_TEMPLATE,
				AgreementGUIField.DISTRIBUTION_TEMPLATE_EFFECTIVE_FROM
	}),
	MAINTAIN_FAIS(RequestKindType.MaintainFAISLicense,
			new AgreementGUIField[] {
				AgreementGUIField.FAIS_HEALTH_BENEFIT,
				AgreementGUIField.FAIS_LICENSE_CATEGORY,
				AgreementGUIField.FAIS_LICENSE_EFFECTIVE_DATE,
				AgreementGUIField.FAIS_LICENSE_NUMBER,
				AgreementGUIField.FAIS_PARTIC_COLL_INVESTMENTS,
				AgreementGUIField.FAIS_PENSION_BENFIT,
				AgreementGUIField.FAIS_RETAIL_PENSION_BENFIT
	}),
	MAINTAIN_FIXED_DEDUCTIONS(RequestKindType.MaintainFixedDeductionDetails,
			new AgreementGUIField[] {
				AgreementGUIField.FIXED_DEDUCTION_GRID,
				AgreementGUIField.FIXED_DEDUCTION_BUTTONS
	}),
	MAINTAIN_FIXED_EARNINGS(RequestKindType.MaintainFixedEarningDetails,
			new AgreementGUIField[] {
				AgreementGUIField.FIXED_EARNING_GRID,
				AgreementGUIField.FIXED_EARNING_BUTTONS
	}),
	
	ACTIVATE_AGREEMENT(RequestKindType.ActivateAgreement,
			group(MAINTAIN_AGREEMENT.fields,MAINTAIN_PAYS_TO.fields,MAINTAIN_FRANCHISE_POOL.fields));
	
	
	private AgreementGUIField[] fields;
	private RequestKindType requestType;
	
	private AgreementGUIFieldRequestMapping(RequestKindType requestType,AgreementGUIField[] fields) {
		this.requestType =requestType;
		this.fields=fields;
	}
	
	
	
	private static AgreementGUIField[] group(AgreementGUIField[]... guiFieldSet) {
		Set<AgreementGUIField> groupSet = new HashSet<AgreementGUIField>();
		if (guiFieldSet!=null) {
			for (AgreementGUIField[] set : guiFieldSet) {
				if (set!=null) {
					groupSet.addAll(Arrays.asList(set));
				}
			}
		}
		return groupSet.toArray(new AgreementGUIField[0]);
	}



	public AgreementGUIField[] getFields() {
		return fields;
	}



	public RequestKindType getRequestType() {
		return requestType;
	}

	public static AgreementGUIFieldRequestMapping getRequestMappingForRequestKind(int kind) {
		for (AgreementGUIFieldRequestMapping mapping : AgreementGUIFieldRequestMapping.values()) {
			if (mapping.getRequestType().getRequestKind()==kind) {
				return mapping;
			}
		}
		return null;
	}

	public static List<RequestKindType> getRequestKindTypesForAgreementGUIField(AgreementGUIField field) {
		List<RequestKindType> ret = new ArrayList<RequestKindType>();
		for (AgreementGUIFieldRequestMapping mapping : AgreementGUIFieldRequestMapping.values()) {
			for (AgreementGUIField mapped : mapping.getFields()) {
				if (mapped.equals(field)) {
					ret.add(mapping.getRequestType());
					break;
				}
			}
		}
		return ret;
		
	}
	
}
