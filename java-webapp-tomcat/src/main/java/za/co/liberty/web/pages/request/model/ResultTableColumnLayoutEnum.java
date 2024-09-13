package za.co.liberty.web.pages.request.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import za.co.liberty.interfaces.gui.request.RequestEnquiryBulkAuthoriseType;

import com.inmethod.grid.column.AbstractColumn;

/**
 * Define the selection of column layouts, maps to bulk authorisation 
 * selections.
 * 
 * @author JZB0608 - 24 Mar 2010
 *
 */
public enum ResultTableColumnLayoutEnum {
	REPORT(null,
//			ResultTableColumnEnum.REQUEST_ID,
			ResultTableColumnEnum.FROM_CONSULTANT_REPORT,
			ResultTableColumnEnum.FROM_CONSULTANT_STATUS_REPORT, 
			ResultTableColumnEnum.TO_CONSULTANT_REPORT,
			ResultTableColumnEnum.TO_CONSULTANT_STATUS_REPORT,
			ResultTableColumnEnum.CONTRACT_NUMBER_REPORT,
			ResultTableColumnEnum.SYSTEM_NAME,
			ResultTableColumnEnum.TRANSFER_TYPE,
			ResultTableColumnEnum.BRANCH_INDICATOR,
			ResultTableColumnEnum.ERROR_FLAG,
			ResultTableColumnEnum.ERROR,
			ResultTableColumnEnum.PRI_BIBLIFE_REPORT,
			ResultTableColumnEnum.REQUEST_DATE,
			ResultTableColumnEnum.REQUESTED_DATE,
			ResultTableColumnEnum.EXECUTION_DATE,
			ResultTableColumnEnum.REQUESTOR,
			ResultTableColumnEnum.AUTHORISER),
	DEFAULT(null,
			ResultTableColumnEnum.REQUEST_KIND, 
			ResultTableColumnEnum.STATUS, 
			ResultTableColumnEnum.AGREEMENT_NR, 
			ResultTableColumnEnum.CONSULTANT_CODE,
			ResultTableColumnEnum.PARTY_NAME,
			ResultTableColumnEnum.POLICY_NR, 
			ResultTableColumnEnum.WORKFLOW_NR, 
			ResultTableColumnEnum.WORKFLOW_DESCRIPTION, 
			ResultTableColumnEnum.REQUESTOR, 
			ResultTableColumnEnum.REQUEST_DATE, 
			ResultTableColumnEnum.AUTHORISER,
			ResultTableColumnEnum.AUTH_DATE,
			ResultTableColumnEnum.SECOND_AUTH,
			ResultTableColumnEnum.SECOND_AUTH_DATE,
			ResultTableColumnEnum.REQUEST_ID,
			ResultTableColumnEnum.AMOUNT,
			ResultTableColumnEnum.REASON),
	ADVANCED_PRACTICE_BALANCE_TRANSFER (RequestEnquiryBulkAuthoriseType.ADVANCED_PRACTICE_BALANCE_TRANSFER,
			ResultTableColumnEnum.REQUEST_KIND, 
			ResultTableColumnEnum.STATUS, 
			ResultTableColumnEnum.AGREEMENT_NR, 
			ResultTableColumnEnum.CONSULTANT_CODE,
			ResultTableColumnEnum.PROP_ADV_PRACTICE_NAME,
			ResultTableColumnEnum.PROP_ADV_PRACTICE_NUMBER, 
			ResultTableColumnEnum.WORKFLOW_NR, 
			ResultTableColumnEnum.WORKFLOW_DESCRIPTION, 
			ResultTableColumnEnum.REQUESTOR, 
			ResultTableColumnEnum.REQUEST_DATE, 
			ResultTableColumnEnum.AUTHORISER,
			ResultTableColumnEnum.AUTH_DATE,
			ResultTableColumnEnum.SECOND_AUTH,
			ResultTableColumnEnum.SECOND_AUTH_DATE,
			ResultTableColumnEnum.REQUEST_ID,
			ResultTableColumnEnum.AMOUNT),		
	BRANCH_TRANSFER(RequestEnquiryBulkAuthoriseType.BRANCH_TRANSFER,
			ResultTableColumnEnum.PROP_MAINTAIN_THIRTEEN_DIGIT_CONS_CODE, 
			ResultTableColumnEnum.PROP_BRANCH_FROM, 
			ResultTableColumnEnum.PROP_BRANCH_TO,
			ResultTableColumnEnum.PROP_TRANSFER_DATE,
			ResultTableColumnEnum.REQUEST_KIND, 
			ResultTableColumnEnum.STATUS, 
			ResultTableColumnEnum.AGREEMENT_NR, 
			ResultTableColumnEnum.CONSULTANT_CODE,
			ResultTableColumnEnum.PARTY_NAME,
			ResultTableColumnEnum.POLICY_NR, 
			ResultTableColumnEnum.WORKFLOW_NR, 
			ResultTableColumnEnum.WORKFLOW_DESCRIPTION, 
			ResultTableColumnEnum.REQUESTOR, 
			ResultTableColumnEnum.REQUEST_DATE, 
			ResultTableColumnEnum.AUTHORISER,
			ResultTableColumnEnum.AUTH_DATE,
			ResultTableColumnEnum.SECOND_AUTH,
			ResultTableColumnEnum.SECOND_AUTH_DATE,
			ResultTableColumnEnum.REQUEST_ID),
	ADVISOR_QUALITY_CODE(RequestEnquiryBulkAuthoriseType.ADVISOR_QUALITY_CODE,
			ResultTableColumnEnum.REQUEST_KIND, 
			ResultTableColumnEnum.STATUS, 
			ResultTableColumnEnum.AGREEMENT_NR, 
			ResultTableColumnEnum.CONSULTANT_CODE,
			ResultTableColumnEnum.PROP_ADV_PRACTICE_NAME,
			ResultTableColumnEnum.PROP_ADV_PRACTICE_NUMBER, 
			ResultTableColumnEnum.WORKFLOW_NR, 
			ResultTableColumnEnum.WORKFLOW_DESCRIPTION, 
			ResultTableColumnEnum.REQUESTOR, 
			ResultTableColumnEnum.REQUEST_DATE, 
			ResultTableColumnEnum.AUTHORISER,
			ResultTableColumnEnum.AUTH_DATE,
			ResultTableColumnEnum.SECOND_AUTH,
			ResultTableColumnEnum.SECOND_AUTH_DATE,
			ResultTableColumnEnum.REQUEST_ID,
			ResultTableColumnEnum.AMOUNT),		
	SEGMENT_TRANSFER(RequestEnquiryBulkAuthoriseType.SEGMENTED_TRANSFER,
			ResultTableColumnEnum.REQUEST_KIND, 
			ResultTableColumnEnum.FROM_CONSULTANT_CODE,
			ResultTableColumnEnum.TO_CONSULTANT_CODE,
			ResultTableColumnEnum.CONTRACT_NUMBER,
			ResultTableColumnEnum.STATUS, 
			ResultTableColumnEnum.AGREEMENT_NR, 
			ResultTableColumnEnum.REQUESTOR, 
			ResultTableColumnEnum.REQUEST_DATE, 
			ResultTableColumnEnum.AUTHORISER,
			ResultTableColumnEnum.AUTH_DATE,
			ResultTableColumnEnum.REQUEST_ID),
	RELEASE_PAYMENT(RequestEnquiryBulkAuthoriseType.RELEASE_PAYMENT,
			ResultTableColumnEnum.AGREEMENT_NR, 
			ResultTableColumnEnum.CONSULTANT_CODE,
			
			ResultTableColumnEnum.PROP_BRATLINK_NUMBER, 
			ResultTableColumnEnum.PROP_PAYMENT_REFERENCE,
			ResultTableColumnEnum.PROP_PAID_AMOUNT,
			ResultTableColumnEnum.PROP_REQUESTED_AMOUNT,
			ResultTableColumnEnum.PROP_PRE_AUTH_LIMIT_AMOUNT,
			ResultTableColumnEnum.REQUEST_KIND, 
			ResultTableColumnEnum.STATUS, 
			ResultTableColumnEnum.AGREEMENT_KIND,
			
			ResultTableColumnEnum.PARTY_NAME,
			ResultTableColumnEnum.POLICY_NR, 
			ResultTableColumnEnum.WORKFLOW_NR, 
			ResultTableColumnEnum.WORKFLOW_DESCRIPTION, 
			ResultTableColumnEnum.REQUESTOR, 
			ResultTableColumnEnum.REQUEST_DATE, 
			ResultTableColumnEnum.AUTHORISER,
			ResultTableColumnEnum.AUTH_DATE,
			ResultTableColumnEnum.SECOND_AUTH,
			ResultTableColumnEnum.SECOND_AUTH_DATE,
			ResultTableColumnEnum.REQUEST_ID),
	DPE_SEARCH(null,
			ResultTableColumnEnum.COMMISSION_KIND,
			ResultTableColumnEnum.REQUEST_KIND, 
			ResultTableColumnEnum.STATUS,
			ResultTableColumnEnum.EARNING_TYPE, 
			ResultTableColumnEnum.POLICY_NR,
			ResultTableColumnEnum.LIFE_ASSURED,
			ResultTableColumnEnum.AMOUNT, 
			ResultTableColumnEnum.REQUESTOR, 
			ResultTableColumnEnum.PRODUCT_REFERENCE, 
			ResultTableColumnEnum.PROP_PAYMENT_REFERENCE,
			ResultTableColumnEnum.PERSISTENCY,
			ResultTableColumnEnum.PREMIUM_FREQUENCY,
			ResultTableColumnEnum.MOVEMENT_CODE,
			ResultTableColumnEnum.REQUEST_DATE,
			ResultTableColumnEnum.CONSULTANT_CODE,
			ResultTableColumnEnum.AGREEMENT_NR,
			ResultTableColumnEnum.AUTHORISER,
			ResultTableColumnEnum.AUTH_DATE,
			ResultTableColumnEnum.REQUEST_ID
			),
	DPE_SEARCH_TOTAL(null,
			ResultTableColumnEnum.COMMISION_TOTAL,
			ResultTableColumnEnum.FEES_TOTAL,
			ResultTableColumnEnum.API_TOTAL,
			ResultTableColumnEnum.POLICY_COUNT,
			ResultTableColumnEnum.PRODUCTION_CREDITS,
			ResultTableColumnEnum.BLUEPRINT_ALLOWANCE_TOTAL
			),
	AUM_SEARCH(null,
			ResultTableColumnEnum.REQUEST_KIND,
			ResultTableColumnEnum.INFO_KIND,
			ResultTableColumnEnum.STATUS,
			ResultTableColumnEnum.POLICY_NR,
			ResultTableColumnEnum.LIFE_ASSURED,
			ResultTableColumnEnum.FUND_ASSET_VALUE,
			ResultTableColumnEnum.FUND_CODE, 
			ResultTableColumnEnum.PRODUCT_CODE, 
			ResultTableColumnEnum.REQUEST_DATE,
			ResultTableColumnEnum.CONSULTANT_CODE,
			ResultTableColumnEnum.AGREEMENT_NR,
			ResultTableColumnEnum.POLICY_START_DATE,
			ResultTableColumnEnum.REQUESTOR,
			ResultTableColumnEnum.AUTHORISER,
			ResultTableColumnEnum.AUTH_DATE,
			ResultTableColumnEnum.REQUEST_ID
			),
	TRANSACTION_REJECTS(null,
			ResultTableColumnEnum.SOURCE_SYSTEM,
			ResultTableColumnEnum.SRS_AGMT_NO,
			ResultTableColumnEnum.REJ_CONTRACT_NR,
			ResultTableColumnEnum.REJ_ERROR_CODE,
			ResultTableColumnEnum.REJ_BUSINESS_ERROR,
			ResultTableColumnEnum.REJ_ERROR_TYPE,
			ResultTableColumnEnum.TIME
			),
	PANEL_TRANSFER(RequestEnquiryBulkAuthoriseType.PANEL_TRANSFER,
			ResultTableColumnEnum.PARTY_NAME,
			ResultTableColumnEnum.AGREEMENT_NR, 
			ResultTableColumnEnum.CONSULTANT_CODE,
			ResultTableColumnEnum.STATUS, 
			ResultTableColumnEnum.REQUESTOR, 
			ResultTableColumnEnum.REQUEST_DATE, 
			ResultTableColumnEnum.AUTHORISER,
			ResultTableColumnEnum.AUTH_DATE,
			ResultTableColumnEnum.REQUEST_ID)
			;
	
	private ResultTableColumnEnum[] columnEnums;
	private RequestEnquiryBulkAuthoriseType bulkAuthType;
	private static Map<RequestEnquiryBulkAuthoriseType, ResultTableColumnLayoutEnum> map;
	
	ResultTableColumnLayoutEnum(RequestEnquiryBulkAuthoriseType bulkAuthType, 
			ResultTableColumnEnum ...columnEnums) {
		this.columnEnums = columnEnums;
		this.bulkAuthType = bulkAuthType;
	}
	
	public ResultTableColumnEnum[] getColumnEnums() {
		return columnEnums;
	}
	
	public List<AbstractColumn> getColumnList() {
		List<AbstractColumn> columnList = new ArrayList<AbstractColumn>(); 
		for (ResultTableColumnEnum colE : columnEnums) {
			columnList.add(colE.getPropertyColumn());
		}
		return columnList;
	}

	public static ResultTableColumnLayoutEnum getLayoutForBulkAuthoriseType(RequestEnquiryBulkAuthoriseType bulkAuthoriseType) {
		if (map==null) {
			Map<RequestEnquiryBulkAuthoriseType, ResultTableColumnLayoutEnum> tmpMap = 
				new HashMap<RequestEnquiryBulkAuthoriseType, ResultTableColumnLayoutEnum>();
			for (ResultTableColumnLayoutEnum e : ResultTableColumnLayoutEnum.values()) {
				tmpMap.put(e.bulkAuthType, e);
			}
			map = tmpMap;
		}
		return map.get(bulkAuthoriseType);
	}
}
