package za.co.liberty.web.pages.request.model;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import org.apache.wicket.model.Model;

import za.co.liberty.interfaces.agreements.requests.PropertyKindType;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGridColumn;

import com.inmethod.grid.column.AbstractColumn;
import com.inmethod.grid.column.PropertyColumn;

/**
 * This enum specify all possible search result column types
 * 
 * @author JZB0608 - 24 Mar 2010
 *
 */
public enum ResultTableColumnEnum {
	
    REQUEST_KIND("Request", "requestKindType", "requestKind", 210),
    
    
/*************************** Start of Core Succssion*****************/    
    
    FROM_CONSULTANT_CODE() {
    	@SuppressWarnings({ "unchecked", "rawtypes" })
		public AbstractColumn getPropertyColumn() {
    		return new PropertyColumn(new Model("From Consultant"), "additionalProperty."+PropertyKindType.FromConsultantCode.getPropertyKind(), "additionalProperty."+PropertyKindType.FromConsultantCode.getPropertyKind() ) {

    			private static final long serialVersionUID = 1L;
				private  NumberFormat consultantFormatter = new DecimalFormat("0000000000000");
				
				@Override
				protected CharSequence convertToString(Object object) {
					Long longObj = Long.parseLong(object==null?"0": object.toString());
					if (longObj != null && longObj.longValue()!=0) {
						return  consultantFormatter.format(longObj);						
					} else {
						return "";
					}
				}			
			}.setInitialSize(100);
    	}
    },
    TO_CONSULTANT_CODE() {
    	@SuppressWarnings({ "unchecked", "rawtypes" })
		public AbstractColumn getPropertyColumn() {
    		return new PropertyColumn(new Model("To Consultant"), "additionalProperty."+PropertyKindType.ToConsultantCode.getPropertyKind(), "additionalProperty."+PropertyKindType.ToConsultantCode.getPropertyKind() ) {

    			private static final long serialVersionUID = 1L;
				private  NumberFormat consultantFormatter = new DecimalFormat("0000000000000");
				
				@Override
				protected CharSequence convertToString(Object object) {
					Long longObj = Long.parseLong(object==null?"0":object.toString());	
					if (longObj != null && longObj.longValue()!=0) {
						return  consultantFormatter.format(longObj);						
					} else {
						return "";
					}
				}			
			}.setInitialSize(100);
    	}
    },
    
    FROM_CONSULTANT_REPORT("From Consultant", "fromConsultatCode", "fromConsultatCode", 100),
    FROM_CONSULTANT_STATUS_REPORT("From Cons Status", "fromConsultantStatus", "fromConsultantStatus", 100),
    TO_CONSULTANT_REPORT("To Consultant", "toConsultatCode", "toConsultatCode", 100),
    TO_CONSULTANT_STATUS_REPORT("To Cons Status", "toConsultantStatus", "toConsultantStatus", 100),
	
	PRI_BIBLIFE_REPORT ("Personal Ref", "priBiblifeReference", "priBiblifeReference", 100),
    CONTRACT_NUMBER_REPORT("Contract Number","contractNumber","contractNumber",100),
    CONTRACT_NUMBER("Contract Number","additionalProperty."+PropertyKindType.ContractNumber.getPropertyKind(),"additionalProperty."+PropertyKindType.ContractNumber.getPropertyKind(),100),
 /*   CONTRACT_NUMBER() {
    	@SuppressWarnings({ "unchecked", "rawtypes" })
		public AbstractColumn getPropertyColumn() {
    		return new PropertyColumn(new Model("Contract Number"), "contractNumber", "contractNumber" ) {

    			private static final long serialVersionUID = 1L;
				private  NumberFormat consultantFormatter = new DecimalFormat("0000000000000");
				
				@Override
				protected CharSequence convertToString(Object object) {
					Long longObj = (Long)object;
					if (longObj != null && longObj.longValue()!=0) {
						return  consultantFormatter.format(longObj);						
					} else {
						return "";
					}
				}			
			}.setInitialSize(100);
    	}
    },*/
    SYSTEM_NAME("System Name", "systemName", "systemName", 100),
    TRANSFER_TYPE("Transfer Type", "transferType", "transferType", 80),
    ERROR_FLAG("Success/Failure", "errorFlag", "errorFlag", 90),
    ERROR("Error", "error", "error", 200),
    BRANCH_INDICATOR("Branch Ind", "branchIndicator", "branchIndicator", 65),
    REQUESTED_DATE("Requested Date", "requestedDate", "requestedDate",92),
    EXECUTION_DATE("Execution Date", "executionDate", "executionDate",88),
    
/*************************** End of Core Succssion*****************/      
    
    STATUS("Status", "statusType", "statusType", 130),
	AGREEMENT_NR() {
    	@SuppressWarnings({ "unchecked", "rawtypes" })
		public AbstractColumn getPropertyColumn() {
    		return new PropertyColumn(new Model("Agreement Nr"), "agreementNr", "agreementNr" ){
    			private static final long serialVersionUID = 1L;
				@Override
				protected CharSequence convertToString(Object object) {
					Number numberObj = (Number)object;
					if (numberObj == null || numberObj.longValue()==0) {
						return "";
					}
					return object.toString();
				}			
			}.setInitialSize(85);
    	}
    },
    AGREEMENT_KIND("Agreement Kind", "agreementKindType", "agreementKindType", 130),
	CONSULTANT_CODE() {
    	@SuppressWarnings({ "unchecked", "rawtypes" })
		public AbstractColumn getPropertyColumn() {
    		return new PropertyColumn(new Model("Consultant Code"), "consultantCode", "consultantCode" ) {

    			private static final long serialVersionUID = 1L;
				private  NumberFormat consultantFormatter = new DecimalFormat("0000000000000");
				
				@Override
				protected CharSequence convertToString(Object object) {
					Long longObj = (Long)object;
					if (longObj != null && longObj.longValue()!=0) {
						return  consultantFormatter.format(longObj);						
					} else {
						return "";
					}
				}			
			}.setInitialSize(100);
    	}
    },
	POLICY_NR("Policy Nr", "policyNr", "policyNr",80),
	WORKFLOW_NR("Workflow Nr", "workflowNr", "workflowNr", 75),
	WORKFLOW_DESCRIPTION() {
    	@SuppressWarnings({ "unchecked", "rawtypes" })
		public AbstractColumn getPropertyColumn() {
    		return new SRSDataGridColumn("workflowDescription", new Model("Workflow Comment"), 
    				"workflowDescription", EditStateType.VIEW).setInitialSize(100);
    	}
    },

	REQUESTOR("Requestor", "requestor", "requestor",76),
	REQUEST_DATE("Request Date", "requestDate", "requestDate",86),
	AUTHORISER("Authoriser", "authoriser1", "authoriser1" ,70),
	AUTH_DATE("Auth Date", "authoriserDate1", "authoriserDate1" ,80),
	SECOND_AUTH("2nd Auth", "authoriser2", "authoriser2",70),
	SECOND_AUTH_DATE("2nd Auth Date", "authoriserDate2", "authoriserDate2",85),
	REQUEST_ID("Request Id", "requestId", "requestId", 75),
	PARTY_NAME("Party Name", "partyName", "partyName", 110),
	
	AMOUNT("Amount", "amount", "sortAmount", 70),
	REASON("Reason", "reason", "reason", 640),
	
	// Request properties
    PROP_MAINTAIN_THIRTEEN_DIGIT_CONS_CODE("New Cons Code", PropertyKindType.MaintainThirteenDigitConsCode, 110),
    PROP_BRANCH_FROM("Branch From", PropertyKindType.BranchFrom, 140),
    PROP_BRANCH_TO("Branch To", PropertyKindType.BranchTo, 140),
    PROP_TRANSFER_DATE("Transfer Date", PropertyKindType.TransferDate, 100),
    
    PROP_PAYMENT_REFERENCE("Payment Ref", PropertyKindType.PaymentReference, 100),
    PROP_BRATLINK_NUMBER("Bratlink", PropertyKindType.BratLinkNumber,70),
	PROP_PAID_AMOUNT("Paid Amount", PropertyKindType.PaidAmount, 95),
	PROP_REQUESTED_AMOUNT("Requested Amnt", PropertyKindType.RequestedAmount, 100),
	PROP_PRE_AUTH_LIMIT_AMOUNT("PreAuth Limit",	PropertyKindType.PreAuthorisationLimitAmount, 100),
    
    PROP_ADV_PRACTICE_NUMBER("Practice Number", PropertyKindType.AdvancedPracticeNumber, 140),
    
    PROP_ADV_PRACTICE_NAME("Practice Name", PropertyKindType.AdvancedPracticeName, 140),
    
    //ZZT2108 Additional Columns for Monthly Commission
    COMMISSION_KIND("Commission Kind", "commissionKind", "commissionKind", 120),
    EARNING_TYPE("Earning Type", PropertyKindType.EarningType, 100),
    PRODUCT_REFERENCE("Product Ref", "productReference", "productReference", 120),
    MOVEMENT_CODE("Movement Code", "movementCode", "movementCode", 90),
    PREMIUM_FREQUENCY("Prem Frequency", "premiumFrequency", "premiumFrequency", 90),
    PERSISTENCY("Persistency", "persistency", "persistency", 120),
    LIFE_ASSURED("Life Assured", "lifeAssured", "lifeAssured", 120),
    
    //ZZT208 DPE Search total columns
    COMMISION_TOTAL("Commission", "commission", "commission",180),
    FEES_TOTAL("Fees", "fees", "fees",180),
    API_TOTAL("API", "api", "api",180),
    POLICY_COUNT("Policy Count", "policyCount", "policyCount",180),
    PRODUCTION_CREDITS("Production Credits", "productionCredits", "productionCredits",180),
    BLUEPRINT_ALLOWANCE_TOTAL("Blueprint Allowance", "bluePrintAll", "bluePrintAll",180), 
    
    //ZZT2108 Additional AUM Columns
    INFO_KIND("Info Kind", "infoKind", "infoKind", 80),
    PRODUCT_CODE("Product Code", "productCode", "productCode", 120), 
    FUND_CODE("Fund Code", "fundCode", "fundCode", 120),
    FUND_ASSET_VALUE("Fund Asset Value", "fundAssetValue", "fundAssetValue", 120), 
    DFM_MODEL("DFM Model", "dfmModelField", "dfmModelField", 120),
    POLICY_START_DATE("Policy Start Date", "policyStartDate", "policyStartDate", 90), 
    TIME("Time", "formattedTime", "formattedTime", 130),
    SOURCE_SYSTEM("Source System", "componentId", "componentId", 100),
    SRS_AGMT_NO("Agreement Nr", "srsAgmtNo", "srsAgmtNo", 90),
    REJ_CONTRACT_NR("Contract Number", "policyRef", "policyRef", 90),
    REJ_ERROR_CODE("Error Code", "errorCode.errorCode", "errorCode", 70),
    REJ_BUSINESS_ERROR("Business Error", "errorCode.guiMessage", "errorCode.guiMessage", 200),
    REJ_ERROR_TYPE("Type", "errorFlag", "errorFlag", 40),;
    
    
    private String description;
    private String propertyName;
    private String sortProperty;
    private int size;
    
    /**
     * Remember to override {@linkplain #getPropertyColumn()}
     *
     */
    ResultTableColumnEnum () {
    	
    }
    /**
     * Inits propertyName and sortProperty to the request kind
     * 
     * @param description
     * @param type
     * @param size
     */
    ResultTableColumnEnum (String description, PropertyKindType type, int size) {
    	this(description, "additionalProperty."+type.getPropertyKind(),
    			"additionalProperty."+type.getPropertyKind(), size);
    }
    
    /**
     * Used default imp of {@linkplain #getPropertyColumn()}
     * 
     * @param description
     * @param propertyName
     * @param sortProperty
     * @param size
     */
    ResultTableColumnEnum (String description, String propertyName, String sortProperty, int size) {
    	this.description = description;
    	this.propertyName = propertyName;
    	this.sortProperty = sortProperty;
    	this.size = size;
    }
    
    /**
     * Override to provide a new property column instance.  Default only works if you 
     * passed the values via the constructor.
     * 
     * @return
     */
	public AbstractColumn getPropertyColumn() {
		return new PropertyColumn(
				new Model(description), propertyName, sortProperty).setInitialSize(size);
	}

}