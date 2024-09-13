package za.co.liberty.web.pages.admin.models;

import java.io.Serializable;
import java.util.List;

import za.co.liberty.dto.userprofiles.MenuItemDTO;
import za.co.liberty.dto.userprofiles.PartyProfileDTO;
import za.co.liberty.dto.userprofiles.ProfileRoleDTO;
import za.co.liberty.dto.userprofiles.RuleDTO;
import za.co.liberty.interfaces.rules.ArithmeticType;
import za.co.liberty.interfaces.rules.RuleDataType;
import za.co.liberty.web.data.pages.ITabbedPageModel;

/**
 * User Admin model class
 * 
 * @author jzb0608 - 13 May 2008
 * 
 */
public class UserAdminModel implements Serializable, ITabbedPageModel<PartyProfileDTO> {

	private static final long serialVersionUID = 1643792587546952282L;

	private List<PartyProfileDTO> selectionList;

	private PartyProfileDTO selectedItem; 

	private List<MenuItemDTO> allAvailableMenuItems;
	private List<RuleDTO> allAvailableRules;
	private List<ArithmeticType> allAvailableRuleArithmetic;
	private List<RuleDataType> allAvailableRuleDataTypes;
	private List<ProfileRoleDTO> allAvailableRoles;
	private List<MenuItemDTO> allRoleMenuItems;
	private int currentTab = -1;
	private Class currentTabClass;
	
	public Class getCurrentTabClass() {		
		return currentTabClass;
	}


	public void setCurrentTabClass(Class currentTabClass) {
		this.currentTabClass = currentTabClass;		
	}
	
	
	public List<ProfileRoleDTO> getAllAvailableRoles() {
		return allAvailableRoles;
	}

	public void setAllAvailableRoles(List<ProfileRoleDTO> allAvailableRoles) {
		this.allAvailableRoles = allAvailableRoles;
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

	public List<PartyProfileDTO> getSelectionList() {
		return selectionList;
	}

	public void setSelectionList(List<PartyProfileDTO> selectionList) {
		this.selectionList = selectionList;
	}

	public PartyProfileDTO getSelectedItem() {
		return selectedItem;
	}

	public void setSelectedItem(PartyProfileDTO selectedItem) {
		this.selectedItem = selectedItem;
	}

	public int getCurrentTab() {			
		return currentTab;
	}

	public void setCurrentTab(int currentTab) {
		this.currentTab = currentTab;			
	}

	public List<MenuItemDTO> getAllRoleMenuItems() {
		return allRoleMenuItems;
	}

	public void setAllRoleMenuItems(List<MenuItemDTO> allRoleMenuItems) {
		this.allRoleMenuItems = allRoleMenuItems;
	}	
	
//	/**
//	 * Get an instance of the managed session bean
//	 * 
//	 * @return
//	 * @throws UnResolvableException 
//	 * @throws CommunicationException 
//	 */
//	public static IUserAdminManagement getSessionBean() throws CommunicationException, UnResolvableException {		
//			IUserAdminManagement sessionBean = (IUserAdminManagement) SRSAuthWebSession.get().getEJBReference(
//					EJBReferences.USER_ADMIN_MANAGEMENT);			
//			return sessionBean;			
//	}

	public List<RuleDataType> getAllAvailableRuleDataTypes() {
		return allAvailableRuleDataTypes;
	}

	public void setAllAvailableRuleDataTypes(
			List<RuleDataType> allAvailableRuleDataTypes) {
		this.allAvailableRuleDataTypes = allAvailableRuleDataTypes;
	}		
}
