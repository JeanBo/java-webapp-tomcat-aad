package za.co.liberty.business.guicontrollers.userprofiles;

import java.util.List;

import javax.ejb.Local;

import za.co.liberty.dto.userprofiles.ISessionUserProfile;
import za.co.liberty.dto.userprofiles.RuleDTO;
import za.co.liberty.exceptions.data.DataNotFoundException;
import za.co.liberty.interfaces.rules.ArithmeticType;
import za.co.liberty.interfaces.rules.RuleDataType;

/**
 * Interface for Rule Management bean
 * 
 * @author jzb0608 - 05 May 2008
 *
 */
@Local
public interface IRuleManagement {

	/**
	 * Retrieve all the rules
	 * 
	 * @return List of all rule items
	 */
	public List<RuleDTO> findAllRules();
	
	/**
	 * Retrieve all the rule arithmetic objects
	 * 
	 * @return
	 */
	public List<ArithmeticType> getArithmeticTypess();
	
	/**
	 * Retrieve all the rule data type objects
	 * 
	 * @return
	 */
	public List<RuleDataType> findAllRuleDataType();
	
	/**
	 * Find a rule with its primary key
	 * 
	 * @param primaryKey
	 * @return
	 * @throws DataNotFoundException 
	 */
	public RuleDTO findRule(long primaryKey) throws DataNotFoundException;
	
	/**
	 * Create a menu item
	 * 
	 * @param dto New rule to store
	 * @param user
	 * @return The newly created rule
	 */
	public RuleDTO createRule(RuleDTO dto, ISessionUserProfile user);
	
	/**
	 * Persist the changes made to a rule
	 * 
	 * @param dto
	 * @return Updated Rule
	 */
	public RuleDTO updateRule(RuleDTO dto);
	
	/**
	 * checks if the given role is linked to any roles or profiles
	 * 
	 * @param ruleID
	 * @return
	 */
	public boolean ruleIsLinkedToRolesOrProfiles(long ruleID);	
}
