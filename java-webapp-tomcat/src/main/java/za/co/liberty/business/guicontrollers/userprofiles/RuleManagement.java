package za.co.liberty.business.guicontrollers.userprofiles;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import za.co.liberty.business.userprofiles.IProfileManagement;
import za.co.liberty.dto.userprofiles.ISessionUserProfile;
import za.co.liberty.dto.userprofiles.RuleDTO;
import za.co.liberty.exceptions.data.DataNotFoundException;
import za.co.liberty.interfaces.rules.ArithmeticType;
import za.co.liberty.interfaces.rules.RuleDataType;

/**
 * Manage Rules
 * 
 * @author jzb0608 - 23 Apr 2008
 * Modified by Dean Scott(DZS2610) to use the ProfileManagement Bean
 */
@Stateless
public class RuleManagement implements IRuleManagement {	

	/* Injected */	
	//@EJB 	
	//protected IProfileManagement managementBean;	
	
	/**
	 * Retrieve all the rules
	 * 
	 * @return List of all menu items
	 */
	public List<RuleDTO> findAllRules() {
		System.out.println("MSK------------------findAllRules");
		//MSK:Change-Commented EJB refer logic and adding dummy data
		//return managementBean.findAllRules();
		List<RuleDTO> ruleList=new ArrayList<RuleDTO>();
		RuleDTO dto=new RuleDTO();
		dto.setRuleID((long) 1);
		dto.setRuleName("OWNAGREEMETRULE");
		dto.setRuleDescription("Own Agreement Rule");
		dto.setHasRuleData(true);
		dto.setArithmeticDefault(ArithmeticType.getType("="));
		dto.setDataTypeDefault(RuleDataType.LONG);
		dto.setEnabled(true);
		ruleList.add(dto);
		ruleList=new ArrayList<RuleDTO>();
		dto=new RuleDTO();
		dto.setRuleID((long) 2);
		dto.setRuleName("NOTOWNAGREEMETRULE");
		dto.setRuleDescription("Not Own Agreement Rule");
		dto.setHasRuleData(true);
		dto.setArithmeticDefault(ArithmeticType.getType("="));
		dto.setDataTypeDefault(RuleDataType.LONG);
		dto.setEnabled(true);
		ruleList.add(dto);
		ruleList=new ArrayList<RuleDTO>();
		dto=new RuleDTO();
		dto.setRuleID((long) 3);
		dto.setRuleName("VIEW_AGREEMENT_LIST_RULE");
		dto.setRuleDescription("View Agreements in list only Rule");
		dto.setHasRuleData(true);
		dto.setArithmeticDefault(ArithmeticType.getType("="));
		dto.setDataTypeDefault(RuleDataType.LONG);
		dto.setEnabled(true);
		ruleList.add(dto);
		return ruleList;
	}

	/**
	 * Retrieve all the rule arithmetic objects
	 * 
	 * @return
	 */
	public List<ArithmeticType> getArithmeticTypess() {
		System.out.println("MSK------------------getArithmeticTypess");
		List<ArithmeticType> list = new ArrayList<ArithmeticType>();
		for (ArithmeticType t : ArithmeticType.values()) {
			list.add(t);
		}
		return list;
	}
	
	/**
	 * Retrieve all the rule data type objects
	 * 
	 * @return
	 */
	public List<RuleDataType> findAllRuleDataType() {		
		//return managementBean.findAllRuleDataTypes();
		//MSKChange:
		List<RuleDataType> list=new ArrayList<RuleDataType>();
		for(RuleDataType r:RuleDataType.values()) 
			list.add(r);
		System.out.println("MSK------------------findAllRuleDataType");
		return list;
		
	}
	
	/**
	 * Find a rule with its primary key
	 * 
	 * @param primaryKey
	 * @return
	 * @throws DataNotFoundException 
	 */
	public RuleDTO findRule(long primaryKey) throws DataNotFoundException {		
		//return managementBean.findRule(primaryKey);
		//MSK#Change
		for(RuleDTO d: findAllRules()) {
			System.out.println("MSK------findRule()------------primaryKey"+primaryKey);
			if(d.getRuleID()==primaryKey) {
				return d;
			}
		}
		System.out.println("MSK------findRule()------------");
		RuleDTO dummy=new RuleDTO();
		dummy.setRuleID(primaryKey);
		dummy.setRuleName("DUMMYAGREEMETRULE");
		dummy.setRuleDescription("Dummy Agreement Rule");
		dummy.setHasRuleData(true);
		dummy.setArithmeticDefault(ArithmeticType.getType("="));
		dummy.setDataTypeDefault(RuleDataType.LONG);
		dummy.setEnabled(true);
		System.out.println("MSK------------------findRule");
		return dummy;
	}

	/**
	 * Create a menu item
	 * 
	 * @param dto New rule to store
	 * @param user
	 * @return The newly created rule
	 */
	public RuleDTO createRule(RuleDTO dto, ISessionUserProfile user) {		
		//return managementBean.createRule(dto, user.getPartyOid()+"");
		//MSK#Change
		System.out.println("MSK------------------createRule");
		dto.setRuleID((long)( Math.random()*(201)+100));
		return dto;
	}

	/**
	 * Persist the changes made to a rule
	 * 
	 * @param dto
	 * @return Updated Rule
	 */
	public RuleDTO updateRule(RuleDTO dto) {		
		//return managementBean.updateRule(dto);
		//MSK#Change
		System.out.println("MSK------------------updateRule");
		return dto;
	}
	
	/**
	 * checks if the given role is linked to any roles or profiles
	 * 
	 * @param ruleID
	 * @return
	 */
	public boolean ruleIsLinkedToRolesOrProfiles(long ruleID){
		//return managementBean.ruleIsLinkedToRolesOrProfiles(ruleID);
		//MSK#Change
		System.out.println("MSK------------------ruleIsLinkedToRolesOrProfiles");
		return false;
	}
}
