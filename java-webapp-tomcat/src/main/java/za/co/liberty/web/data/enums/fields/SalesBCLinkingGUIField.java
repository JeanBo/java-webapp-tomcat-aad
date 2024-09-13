package za.co.liberty.web.data.enums.fields;

import java.util.ArrayList;
import java.util.List;

	public enum SalesBCLinkingGUIField {

		BRANCH_NAME_CONTEXTPANEL("branchName","Branch Name","SelectionPanel"),
		SERVICING_TYPE_CONTEXTPANEL("servicingType","Servicing Type","SelectionPanel"),
		SERVICING_PANEL_CONTEXTPANEL("servicingPanel","Servicing Panel","SelectionPanel"),
		/* Columns for Adviser Relationship Table */
		ADVISER_NAME("adviserName","Adviser Name","AdviserDetailsTable"),
		ADVISER_CONSULTANT_CODE("adviserConsultantCode","Adviser Consultant Code","AdviserDetailsTable"),
		STATUS("adviserStatus","Status","AdviserDetailsTable"),
		STATUS_START_DATE("statusStartDate","Status Start Date","AdviserDetailsTable"),
		SERVICING_CONSULTANT_NAME("servicingConsultantName","Consultant Name","AdviserDetailsTable"),
		CONSULTANT_CODE("consultantCode","Consultant Code","AdviserDetailsTable"),
		SERVICE("serviceType.typeName","Service Type","AdviserDetailsTable"),
		BRANCH_NAME("branchName","Branch Name","AdviserDetailsTable"),
		START_DATE("startDate","Link Start Date","AdviserDetailsTable"),
		END_DATE("endDate","Link End Date","AdviserDetailsTable"),		
		/* Columns for Panel List Table (Display Servicing Panels pop up window) */
		PANEL_NAME_PANELDETAILS("panelName","Panel Name","panelListTable"),
		PANEL_STATUS_PANELDETAILS("panelStatus.description","Panel Status","panelListTable"),
		SERVICE_PANELDETAILS("serviceType.typeName","Service Type","panelListTable"),
		ADVISER_NAME_PANELDETAILS("adviserName","Adviser Name","panelListTable"),
		ADVISER_CONSULTANT_CODE_PANELDETAILS("adviserConsultantCode","Adviser Consultant Code","panelListTable"),
		STATUS_PANELDETAILS("adviserStatus","Status","panelListTable"),
		STATUS_START_DATE_PANELDETAILS("statusStartDate","Status Start Date","panelListTable"),
		SERVICING_CONSULTANT_NAME_PANELDETAILS("servicingConsultantName","Consultant Name","panelListTable"),
		CONSULTANT_CODE_PANELDETAILS("consultantCode","Consultant Code","panelListTable"),
		BRANCH_NAME_PANELDETAILS("branchName","Branch Name","panelListTable"),
		START_DATE_PANELDETAILS("startDate","Link Start Date","panelListTable"),
		END_DATE_PANELDETAILS("endDate","Link End Date","panelListTable"),
		/* Columns for Request Authorisation Table (Authorisation pop up window) */
		AUTHORISATION_ADVISERNAME("authAdviserName","Adviser Name","authorisationTable"),
		AUTHORISATION_ADV_CONSCODE("authAdviserConsCode","Adviser Consultant Code","authorisationTable"),
		AUTHORISATION_PANELFROM("authPanelFrom","Servicing Panel From","authorisationTable"),
		AUTHORISATION_PANELTO("authPanelTo","Servicing Panel To","authorisationTable"),
		AUTHORISATION_NEWPANELSTARTDATE("authPanelStartDate","New Panel Start Date","authorisationTable"),
		AUTHORISATION_REQUESTOR("authRequestor","Requestor","authorisationTable"),
		AUTHORISATION_REQUESTDATE("authRequestDate","Request Date","authorisationTable"),	
		AUTHORISATION_REQUESTID("authRequestID","Request ID","authorisationTable"),
		/*Search*/
		SEARCH_SERVICING_PANEL("searchServicingPanel","Servicing Panel","searchPanel");
		
		private String id;
		private String name;
		private String displayContainer;

		SalesBCLinkingGUIField(String id,String name, String displayContainer) {    
			this.id = id;
			this.name = name;			
			this.displayContainer = displayContainer;
		}

		public String getId() {
			return id;
		}

		public String getName() {
			return name;
		}
		
		public String getDisplayContainer() {
			return displayContainer;
		}

	public static List<SalesBCLinkingGUIField> getEnumForTable(String tableName) {
		List<SalesBCLinkingGUIField> fieldList = new ArrayList<SalesBCLinkingGUIField>();

		for (SalesBCLinkingGUIField type : SalesBCLinkingGUIField.values()) {
			if (type.displayContainer.equals(tableName))
				fieldList.add(type);
		}
		return fieldList;
	}

}
