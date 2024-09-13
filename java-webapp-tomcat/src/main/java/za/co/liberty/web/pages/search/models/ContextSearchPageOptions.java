package za.co.liberty.web.pages.search.models;

import java.io.Serializable;
import java.util.ArrayList;

import za.co.liberty.dto.gui.context.ResultContextItemDTO;

/**
 * Container bean for Context Search Options for the search page
 * 
 * @author JZB0608 - 30 Jul 2008
 *
 */
public class ContextSearchPageOptions implements Serializable {

	/* Constants */
	private static final long serialVersionUID = -8838512113286446705L;
	
	/* Attributes */
	private boolean isLimitToPartyOnly;
	private boolean isLimitToPartyPersonOnly;
	private boolean isAllowMultipleSelect; 
	private String pageName;
	private ArrayList<ResultContextItemDTO> selectedItemList = new ArrayList<ResultContextItemDTO>();
	
	/**
	 * Default constructor
	 * 
	 * @param pageName
	 */
	public ContextSearchPageOptions(String pageName) {
		this.pageName = pageName;
	}
	
	public boolean isAllowMultipleSelect() {
		return isAllowMultipleSelect;
	}

	public void setAllowMultipleSelect(boolean isAllowMultipleSelect) {
		this.isAllowMultipleSelect = isAllowMultipleSelect;
	}

	public boolean isLimitToPartyOnly() {
		return isLimitToPartyOnly;
	}
	public void setLimitToPartyOnly(boolean isLimitToPartyOnly) {
		this.isLimitToPartyOnly = isLimitToPartyOnly;
	}

//	MZP0801 Party Person Only
	public boolean isLimitToPartyPersonOnly() {
		return isLimitToPartyPersonOnly;
	}
	public void setLimitToPartyPersonOnly(boolean isLimitToPartyPersonOnly) {
		this.isLimitToPartyPersonOnly = isLimitToPartyPersonOnly;
	}	
	
	public String getPageName() {
		return pageName;
	}
	public void setPageName(String pageName) {
		this.pageName = pageName;
	}

	public ArrayList<ResultContextItemDTO> getSelectedItemList() {
		return selectedItemList;
	}

	public void setSelectedItemList(ArrayList<ResultContextItemDTO> selectedItemList) {
		this.selectedItemList = selectedItemList;
	}
}
