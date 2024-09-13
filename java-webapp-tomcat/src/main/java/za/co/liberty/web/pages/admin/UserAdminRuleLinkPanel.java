package za.co.liberty.web.pages.admin;

import java.util.ArrayList;
import java.util.List;

import javax.naming.NamingException;

import org.apache.log4j.Logger;

import za.co.liberty.business.guicontrollers.userprofiles.IUserAdminManagement;
import za.co.liberty.dto.userprofiles.PartyProfileDTO;
import za.co.liberty.dto.userprofiles.ProfileRoleDTO;
import za.co.liberty.dto.userprofiles.RuleDTO;
import za.co.liberty.dto.userprofiles.RunnableRuleDTO;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.helpers.lookup.ServiceLocator;
import za.co.liberty.interfaces.rules.ArithmeticType;
import za.co.liberty.interfaces.rules.RuleDataType;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.pages.admin.models.UserAdminModel;

/**
 * <p>Allows for the linking of Rules to the User</p>
 * 
 * @author jzb0608 - 08 May 2008
 * 
 */
public class UserAdminRuleLinkPanel extends RuleLinkingPanel<UserAdminModel> {

	/* Constants */
	private static final long serialVersionUID = -8003453537906825676L;

	/**
	 * Default constructor
	 * 
	 */
	public UserAdminRuleLinkPanel(String id, UserAdminModel pageModel,
			EditStateType editState) {
		super(id,pageModel,editState);
	}

	@Override
	protected List<RuleDTO> getCompleteAvailableItemList() {
		return pageModel.getAllAvailableRules();
	}

	@Override
	protected List<RunnableRuleDTO> getCurrentlyLinkedItemList() {
		return
		((PartyProfileDTO) bean).getRunnableRuleList();
	}

	@Override
	protected List<ArithmeticType> getRuleArithmeticList() {
		return pageModel.getAllAvailableRuleArithmetic();
	}

	@Override
	protected List<RunnableRuleDTO> getNotSelectableAdditionalLinkedItemList() {
		List<RunnableRuleDTO> ruleItems = new ArrayList<RunnableRuleDTO>();
		PartyProfileDTO profile = (PartyProfileDTO) bean;
		try {//MSK#Change :commented below method temporarily and adding Servicelocator
			//UserAdminModel.getSessionBean().populateRuleItemsforRole(profile);
			ServiceLocator.lookupService(IUserAdminManagement.class).populateRuleItemsforRole(profile);
		} catch (CommunicationException e) {
			// TODO handle this error, Error Handling still to be cleared up
			Logger.getLogger(this.getClass()).error(
					"Unable to initialise session bean for page", e);
			this.error(e.getMessage());
			//no items will then be added to the list, not serious
			return null;
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			throw new CommunicationException(e);
		} 		
		if(profile.getRoleItemList() != null){
			for(ProfileRoleDTO role : profile.getRoleItemList()){
				ruleItems.addAll(role.getRunnableRuleList());
			}
		}		
		return ruleItems;			
	}

	@Override
	protected List<RuleDataType> getRuleDataTypeList() {		
		return pageModel.getAllAvailableRuleDataTypes();
	}

}
