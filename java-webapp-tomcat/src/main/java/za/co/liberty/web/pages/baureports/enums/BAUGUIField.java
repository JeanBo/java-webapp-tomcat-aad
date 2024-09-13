package za.co.liberty.web.pages.baureports.enums;

import za.co.liberty.web.data.enums.fields.IGUIField;

public enum BAUGUIField implements IGUIField {
	
	/**
	 * For DPE Extracts
	 */
	QUALITY_CENTRE_ID("qualityCentreId","lblQualityCentreId","Quality Centre Id"),
	COMMISSION_KINDS("commKinds","lblCommKinds","Commission Kinds"),
	AGREEMENT_KINDS("agreementKinds","lblAgreementKinds","Agreement Kinds"),
	PRODUCT_REFERENCES("productReferences","lblProductReferences","Product references"),
	AGREEMENT_NUMBERS("agreementNumbers","lblAgreementNumbers","Agreement Numbers"),
	BENEFIT_TYPES("benefitTypes","lblBenefitTypes","Benefit types"),
	PREMIUM_FREQUENCYS("premiumFrequencies","lblPremiumFrequencies","Premium Frequencies"),
	CONTR_INCR_INDICATORS("contrIncrIndicators","lblContrIncrIndicators","Contribution Increase Indicators"),
	DATE_FROM("dateFrom","lblDateFrom","Date From"),
	DATE_TO("dateTo","lblDateTo","Date To"),
	DATE_TYPE("dateType","lblDateType","Date Type"),
	EMAIL_ID("emailId","lblEmailId","Email id"),
	/**
	 * For VED Extracts
	 */
	THIRTEENDIGCONSCODE("thriteendigCode","lblThirteenDigCode","13 Digit Consultant Code"),
	AGREEMENT_NUMBER("agreementNumber","lblAgreementNumber","Agreement Number");
		
	private String fieldId;
	private String labelId;
	private String description;
		
	private BAUGUIField(String fieldId,String labelId,String description) {
		this.fieldId=fieldId;
		this.labelId=labelId;
		this.description=description;
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
	
}