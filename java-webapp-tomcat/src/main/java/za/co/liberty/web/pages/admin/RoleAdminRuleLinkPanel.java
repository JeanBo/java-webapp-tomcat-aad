package za.co.liberty.web.pages.admin;

import java.util.List;

import za.co.liberty.dto.userprofiles.ProfileRoleDTO;
import za.co.liberty.dto.userprofiles.RuleDTO;
import za.co.liberty.dto.userprofiles.RunnableRuleDTO;
import za.co.liberty.interfaces.rules.ArithmeticType;
import za.co.liberty.interfaces.rules.RuleDataType;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.pages.admin.models.RolesModel;

/**
 * <p>
 * A subpanel of {@link RoleAdmin} that is shown on 
 * a tab.  This panel displays all Rules linked to a 
 * roll.
 * </p>
 * 
 * @author jzb0608 - 08 May 2008
 * 
 */
public class RoleAdminRuleLinkPanel extends RuleLinkingPanel<RolesModel> {

	/* Constants */
	private static final long serialVersionUID = -8003453537906825676L;

	/**
	 * Default constructor
	 * 
	 */
	public RoleAdminRuleLinkPanel(String id, RolesModel pageModel,
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
		((ProfileRoleDTO) bean).getRunnableRuleList();
	}

	@Override
	protected List<ArithmeticType> getRuleArithmeticList() {
		return pageModel.getAllAvailableRuleArithmetic();
	}

	@Override
	protected List<RuleDataType> getRuleDataTypeList() {
		return pageModel.getAllAvailableRuleDataTypes();
	}	
}
