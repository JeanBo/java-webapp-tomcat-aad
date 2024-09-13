package za.co.liberty.web.pages.admin.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import za.co.liberty.dto.userprofiles.RequestCategoryDTO;
import za.co.liberty.interfaces.agreements.requests.RequestKindType;
import za.co.liberty.web.data.pages.IMaintenancePageModel;

public class RequestCategoryModel implements Serializable, IMaintenancePageModel<RequestCategoryDTO> {
	
	/**
	 * Add additional information for page
	 */
	private static final long serialVersionUID = 1L;
	private RequestCategoryDTO selectedItem;
	private List<RequestCategoryDTO> selectedList;
	private String logginUserOID;
	private List<RequestKindType> originalRequestKindsList = null;
	private List<RequestKindType> availableRequestCategoryKindsList = null;
	private RequestKindType requestKindTypeSelected;
	
	
	
	public RequestKindType getRequestKindTypeSelected() {
		return requestKindTypeSelected;
	}
	public void setRequestKindTypeSelected(RequestKindType requestKindTypeSelected) {
		this.requestKindTypeSelected = requestKindTypeSelected;
	}
	public String getLogginUserOID() {
		return logginUserOID;
	}
	public void setLogginUserOID(String logginUserOID) {
		this.logginUserOID = logginUserOID;
	}

	public RequestCategoryDTO getSelectedItem() {
		return selectedItem;
	}
	public void setSelectedItem(RequestCategoryDTO selectedItem) {
		this.selectedItem = selectedItem;
	}
	
	public List<RequestCategoryDTO> getSelectedList() {
		return selectedList;
	}
	public void setSelectedList(List<RequestCategoryDTO> selectedList) {
		this.selectedList = selectedList;
	}
	
	public List<RequestCategoryDTO> getSelectionList() {
		return selectedList;
	}
	public void setSelectionList(List<RequestCategoryDTO> selectionList) {
		selectedList = selectionList;
		
	}
	
	public Comparator getCategoryComparator() {
		return new Comparator<RequestKindType>() {
			public int compare(RequestKindType o1, RequestKindType o2) {
				Integer temp2 = 0;
				Integer temp1 = 0;
				if(o1 != null || o2 != null){
				if((o1.getRequestKind() != 0) || (o2.getRequestKind() != 0)) {
					temp2=o1.getRequestKind();
					temp1=new Integer(o2.getRequestKind());
					return temp2.compareTo(temp1);
				}
				}
				return 0;
			}
		};
	}
	
	public List<RequestKindType> getOriginalRequestKindsList() {
		return originalRequestKindsList;
	}
	public void setOriginalRequestKindsList(
			List<RequestKindType> originalRequestKindsList) {
		this.originalRequestKindsList = originalRequestKindsList;
	}
	
	public List<RequestKindType> getAvailableRequestCategoryKindsList() {
		if (availableRequestCategoryKindsList == null){
			return new ArrayList<RequestKindType>();
		}
		return this.availableRequestCategoryKindsList;
	}
	public void setAvailableRequestCategoryKindsList(
			List<RequestKindType> availableRequestCategoryKindsList) {
		this.availableRequestCategoryKindsList = availableRequestCategoryKindsList;
	}
}