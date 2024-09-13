package za.co.liberty.web.data.enums.fields;


public enum ReportGUIField implements IGUIField{
	
	/**
	 * Agreement
	 */
	SERVICED_ADVISOR_ALL("servicedAdvisorsList.value","lblServicedAdvisorAll","All Serviced Advisor"),
	SERVICED_ADVISOR("servicedAdvisorsPanel","lblServicedAdvisorPanel","Serviced Advisor"),
			
	BRANCH_UNIT_NAME("branchUnitName","lblBranchUnitName","Branch Or Unit Name"),
	
	START_DATE("startDate","lblStartDate","Start Date"),
	END_DATE("endDate","lblEndDate","End Date"),
	PERIOD("period","lblPeriod","Period"),
	VIEWCOMMONLY("viewCommissionOnly","lblViewCommOnly","Show Only Commission Statements");
	
	private String fieldId;
	private String labelId;
	private String description;

	
	private ReportGUIField() {
		
	}
	
	private ReportGUIField(String fieldId,String labelId,String description) {
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
