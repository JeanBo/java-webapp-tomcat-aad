package za.co.liberty.web.pages.admin.models;

import java.io.Serializable;
import java.util.Comparator;
import java.util.List;

import za.co.liberty.dto.userprofiles.MenuItemDTO;
import za.co.liberty.dto.userprofiles.ProfileRoleDTO;
import za.co.liberty.dto.userprofiles.RuleDTO;
import za.co.liberty.interfaces.agreements.requests.RequestKindType;
import za.co.liberty.interfaces.rules.ArithmeticType;
import za.co.liberty.interfaces.rules.RuleDataType;
import za.co.liberty.persistence.agreement.entity.RequestKindEntity;
import za.co.liberty.web.data.pages.ITabbedPageModel;

/**
 * Roles model class
 * 
 * @author jzb0608 - 23 Apr 2008
 * 
 */
public class RolesModel implements Serializable, ITabbedPageModel<ProfileRoleDTO> {

	private static final long serialVersionUID = 1643792587546952282L;

	private List<ProfileRoleDTO> selectionList;

	private ProfileRoleDTO selectedItem;

	private List<MenuItemDTO> allAvailableMenuItems;
	private List<RuleDTO> allAvailableRules;
	private List<ArithmeticType> allAvailableRuleArithmetic;
	private List<RuleDataType> allAvailableRuleDataTypes;
//	private ArrayList<RoleRequestActionsEntity>roleRequests;
	private List<RequestKindType> allRequestKinds;	
	private int currentTab;
	private Class currentTabClass;
	
	public Class getCurrentTabClass() {		
		return currentTabClass;
	}


	public void setCurrentTabClass(Class currentTabClass) {
		this.currentTabClass = currentTabClass;		
	}
		
	/**
	 * Get an instance of the menu comparator.  
	 * @return
	 */
	public Comparator getRequestKindComparator() {
		return new Comparator<RequestKindEntity>() {
			public int compare(RequestKindEntity o1, RequestKindEntity o2) {
				return o1.getId().compareTo(o2.getId());
			}
		};
	}
	
	public List<ArithmeticType> getAllAvailableRuleArithmetic() {
		return allAvailableRuleArithmetic;
	}


	public void setAllAvailableRuleArithmetic(
			List<ArithmeticType> allAvailableRuleArithmetic) {
		this.allAvailableRuleArithmetic = allAvailableRuleArithmetic;
	}

	public void setAllAvailableRules(List<RuleDTO> allAvailableRules) {
		this.allAvailableRules = allAvailableRules;
		
	}
	public List<RuleDTO> getAllAvailableRules() {
		return allAvailableRules;
	}

	public List<MenuItemDTO> getAllAvailableMenuItems() {
		return allAvailableMenuItems;
	}

	public void setAllAvailableMenuItems(List<MenuItemDTO> allAvailableMenuItems) {
		this.allAvailableMenuItems = allAvailableMenuItems;
	}

	public List<ProfileRoleDTO> getSelectionList() {
		return selectionList;
	}

	public void setSelectionList(List<ProfileRoleDTO> selectionList) {
		this.selectionList = selectionList;
	}

	public ProfileRoleDTO getSelectedItem() {
		return selectedItem;
	}

	public void setSelectedItem(ProfileRoleDTO selectedItem) {
		this.selectedItem = selectedItem;
	}

	public int getCurrentTab() {			
		return currentTab;
	}

	public void setCurrentTab(int currentTab) {
		this.currentTab = currentTab;
	}


	public List<RuleDataType> getAllAvailableRuleDataTypes() {
		return allAvailableRuleDataTypes;
	}


	public void setAllAvailableRuleDataTypes(
			List<RuleDataType> allAvailableRuleDataTypes) {
		this.allAvailableRuleDataTypes = allAvailableRuleDataTypes;
	}	
	
	public List<RequestKindType> getAllRequestKinds(){
		return allRequestKinds;
	}
	
	public void setAllRequestKinds(List<RequestKindType> list) {
		this.allRequestKinds=list;
	}
	
//	public ArrayList<RoleRequestActionsEntity> getRoleRequests(){
//		if(this.roleRequests==null) {
//			this.roleRequests=new ArrayList<RoleRequestActionsEntity>();
//		}
//		return this.roleRequests;
//	}
//	
//	public void setRequests(ArrayList<RoleRequestActionsEntity> list) {
//		this.roleRequests=list;
//	}
	
}