package za.co.liberty.business.guicontrollers.userprofiles;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import za.co.liberty.business.userprofiles.IProfileManagement;
import za.co.liberty.dto.userprofiles.AllowableRequestActionDTO;
import za.co.liberty.dto.userprofiles.ISessionUserProfile;
import za.co.liberty.dto.userprofiles.MenuItemDTO;
import za.co.liberty.dto.userprofiles.PartyProfileDTO;
import za.co.liberty.dto.userprofiles.ProfileRoleDTO;
import za.co.liberty.dto.userprofiles.RuleDTO;
import za.co.liberty.dto.userprofiles.RuleDataDTO;
import za.co.liberty.dto.userprofiles.RunnableRuleDTO;
import za.co.liberty.dto.userprofiles.RuleDTO.RuleSource;
import za.co.liberty.exceptions.data.DataNotFoundException;
import za.co.liberty.interfaces.agreements.requests.RequestKindType;
import za.co.liberty.interfaces.rules.ArithmeticType;
import za.co.liberty.interfaces.rules.RuleDataType;
import za.co.liberty.persistence.party.entity.fastlane.PartyProfileFLO;


/**
 * This bean manages the administration of User Profiles
 *
 * @author JZB0608 - 13 May 2008
 * Modified by Dean Scott(DZS2610) to use the ProfileManagement Bean
 * 
 */
@Stateless 
public class UserAdminManagement implements IUserAdminManagement, Serializable {
	
	private static final long serialVersionUID = -4480152802387509173L;
	
	/* Injected */
	@EJB
	protected IProfileManagement managementBean;
	
		
	/**
	 * Search for users starting with the given text
	 * 
	 * @return List a list of User Profiles
	 */
	public List<PartyProfileDTO> findFastLaneUserStartingWith(String startsWith) {		
		
		List<PartyProfileFLO> list = //managementBean.findUserFastLaneWithUacfStartingWith(startsWith);
				new ArrayList<PartyProfileFLO>();
		PartyProfileFLO ppfOBJ=new PartyProfileFLO();
		ppfOBJ.setOid((long) 86668);
		ppfOBJ.setPartyOid((long) 1717211);
		ppfOBJ.setUacfId("SYM2301");
		list.add(ppfOBJ);
		ppfOBJ=new PartyProfileFLO();
		ppfOBJ.setOid((long) 32536);
		ppfOBJ.setPartyOid((long) 846250);
		ppfOBJ.setUacfId("SYM2311");
		list.add(ppfOBJ);
		List<PartyProfileDTO> returnList = new ArrayList<PartyProfileDTO>();
		
		for (PartyProfileFLO flo : list) {
			PartyProfileDTO dto = new PartyProfileDTO();
			dto.setSecurityID(flo.getUacfId());
			dto.setPartyOID(flo.getPartyOid());
			dto.setProfileOID(flo.getOid());
			returnList.add(dto);
		}
		
		return returnList;
		
	}
	
	/**
	 * Find a user with its primary key
	 * 
	 * @param primaryKey
	 * @return
	 * @throws DataNotFoundException 
	 */
	public PartyProfileDTO findUser(long primaryKey) throws DataNotFoundException {	
		
		//return managementBean.findUserForUpdate(primaryKey);
		//MSK#Change
		PartyProfileDTO dto=new PartyProfileDTO();
		dto.setProfileOID((long) 86668);
		//private boolean enabled;
		dto.setPartyOID((long)1717211); 
		dto.setSecurityID("SYM2301");
		dto.setTimeCreated(new Date(System.currentTimeMillis()));
		dto.setTimeModified(new Date(System.currentTimeMillis()));
		dto.setCreatedBy("Santosh");
		Map<String,RuleDTO> ruleMap=new HashMap<String,RuleDTO>();
			RuleDTO ruleDTO=new RuleDTO();
			ruleDTO.setRuleName("HIERARCHY_MODIFY_AGREEMENT_RULE");
			ruleDTO.setRuleSource(RuleSource.USER);
			ruleDTO.setArithmeticDefault(ArithmeticType.EQUAL);
			ruleDTO.setDataTypeDefault(RuleDataType.LONG);
			ruleDTO.setCreatedBy("Santosh");
			ruleDTO.setRuleID((long) 123);
			ruleDTO.setTimeCreated(new Date(System.currentTimeMillis()));
			ruleDTO.setTimeModified(new Date(System.currentTimeMillis()));
			ruleDTO.setEnabled(true);
			ruleDTO.setHasRuleData(true);
			ruleDTO.setRuleDataRetreivedBySystem(false);
			ruleDTO.setRuleDataDTO(new RuleDataDTO());
		dto.setRuleMap((HashMap<String, RuleDTO>) ruleMap);
			List<MenuItemDTO> menuDTO=new ArrayList<MenuItemDTO>();
				MenuItemDTO menu=new MenuItemDTO();
				menu.setMenuItemDescription("Advanced Practice Panel");
				menu.setMenuItemID((long) 421);
				menu.setMenuItemLongDescription("Advanced Practice Panel");
				menu.setImplClazz("za.co.liberty.web.pages.advancedPractice.AdvancedPracticePanel");
				menu.setAddAccess(true);
				menu.setPanel(true);
				menu.setModifyAccess(false);
				menu.setMenuItemName("Advanced Practice Panel");
				menu.setDeleteAccess(false);
				menuDTO.add(menu);
				menu=new MenuItemDTO();
				menu.setMenuItemDescription("Segment Name Admin");
				menu.setMenuItemID((long) 261);
				menu.setMenuItemLongDescription("Segment Name Admin");
				menu.setImplClazz("za.co.liberty.web.pages.admin.SegmentNameAdminPage");
				menu.setAddAccess(true);
				menu.setPanel(false);
				menu.setModifyAccess(false);
				menu.setMenuItemName("Segment Name Admin");
				menu.setDeleteAccess(false);
				menuDTO.add(menu);
		dto.setMenuItemList(menuDTO);
				List<RunnableRuleDTO> rrDTO=new ArrayList<RunnableRuleDTO>();
				RunnableRuleDTO r=new RunnableRuleDTO();
				r.setVersion(1);
					r.setCreationTime(Timestamp.valueOf("2022-04-21 09:01:15"));
					r.setReplacementTime(Timestamp.valueOf("2022-04-21 09:01:15"));
					r.setEffectiveTo(new Date());
					r.setCreatedBy("samples");
					r.setDataTypeDefault(RuleDataType.STRING);
					
					r.setRuleDescription("Insert by Script");
					r.setRuleName("MODIFY_ALL_AGREEMENT_EXCEPTION_OWN");
					r.setRuleID((long)1);
					r.setTimeCreated(new Date());
					r.setCreatedBy("SRS");
					r.setTimeModified(new Date());
					r.setEnabled(true);
					r.setHasRuleData(true);
					r.setArithmeticDefault(ArithmeticType.EQUAL);
					r.setRuleSource(RuleSource.ROLE);
					r.setRuleDataRetreivedBySystem(true);
					r.setDataTypeDefault(RuleDataType.LONG);
					r.setVersion(1);
					r.setEffectiveFrom(new Date());
					r.setDbKey((long)12);
					
					
					
					rrDTO.add(r);
		dto.setRunnableRuleList(rrDTO);
			List<ProfileRoleDTO> proList=new ArrayList<ProfileRoleDTO>();
				ProfileRoleDTO pDto=new ProfileRoleDTO();
				//Role
				pDto.setProfileRoleID((long)1);
				pDto.setRoleName("srsrulerole1");
				pDto.setRoleShortDescription("Update-All request kinds");
				pDto.setRoleLongDescription("Commissions Admin-Team Leader or higher position");
				pDto.setEnabled(true);
				pDto.setTimeCreated(new Date());
				pDto.setTimeModified(new Date());
				pDto.setRuleMap((HashMap<String, RuleDTO>) ruleMap);
				//Menu
				pDto.setDefaultMenuItemList(findAllAvailableMenuItems());
				pDto.setRunnableRuleList(rrDTO);
				pDto.setAllowableRequestActionList(new ArrayList<AllowableRequestActionDTO>());
				proList.add(pDto);
		dto.setRoleItemList(proList);
				List<AllowableRequestActionDTO> allowableDTO=new ArrayList<AllowableRequestActionDTO>();
					AllowableRequestActionDTO aDTO=new AllowableRequestActionDTO();
					aDTO.setAllowAuthorise(true);
					aDTO.setAllowRaise(true);
					aDTO.setAllowDecline(true);
					aDTO.setAllowView(true);
					aDTO.setRequestKind(RequestKindType.DistributePolicyEarning);
					allowableDTO.add(aDTO);
					aDTO.setAllowAuthorise(false);
					aDTO.setAllowRaise(true);
					aDTO.setAllowDecline(false);
					aDTO.setAllowView(false);
					aDTO.setRequestKind(RequestKindType.CreateAgreement);
		dto.setAllowableRequestActionList(allowableDTO);
		return dto;
	}
	
	/**
	 * Find a user with its primary key
	 * 
	 * @param primaryKey
	 * @return
	 */
	public PartyProfileDTO findUserByPartyOID(long partyOID)
		throws DataNotFoundException {		
		//return managementBean.findUserForUpdateByPartyOID(partyOID)  ;
		//MSK#Change
		return findUser(partyOID);
	}  //mzp0801 Admin search	
	
	
	/**
	 * Persist the changes made to a user
	 * 
	 * @param dto
	 * @param user
	 * @return Updated Role
	 */
	public PartyProfileDTO updateUser(PartyProfileDTO dto, ISessionUserProfile user) {		
		//return managementBean.updateUser(dto, user.getPartyOid()+"");
		//MSK#Cahnge
		return dto;
	}
	
	

	/**
	 * Retrieve a list of all menu items that are available
	 * for selection
	 *  
	 * @return
	 */
	public List<MenuItemDTO> findAllAvailableMenuItems() {
		//return managementBean.findAllMenuItems();
		//MSK:Change
		
		List<MenuItemDTO> list = new ArrayList<MenuItemDTO>();
		MenuItemDTO menu=new MenuItemDTO();
		menu.setMenuItemDescription("Advanced Practice Panel1");
		menu.setMenuItemID((long) 421);
		menu.setMenuItemLongDescription("Advanced Practice Panel1");
		menu.setImplClazz("za.co.liberty.web.pages.advancedPractice.AdvancedPracticePanel");
		menu.setAddAccess(true);
		menu.setPanel(true);
		menu.setModifyAccess(false);
		menu.setMenuItemName("Advanced Practice Panel");
		menu.setDeleteAccess(false);
		list.add(menu);
		menu=new MenuItemDTO();
		menu.setMenuItemDescription("Segment Name Admin1");
		menu.setMenuItemID((long) 261);
		menu.setMenuItemLongDescription("Segment Name Admin1");
		menu.setImplClazz("za.co.liberty.web.pages.admin.SegmentNameAdminPage");
		menu.setAddAccess(true);
		menu.setPanel(false);
		menu.setModifyAccess(false);
		menu.setMenuItemName("Segment Name Admin");
		menu.setDeleteAccess(false);
		list.add(menu);
		return list;
	}

	/**
	 * Retrieve a list of all rules that are available
	 * for selection
	 *  
	 * @return
	 */
	public List<RuleDTO> findAllAvailableRules() {
		
		//return managementBean.findAllAvailableRules();
		//MSK#Change 2
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
	 * Retrieve a list of all roles that are available
	 * for selection
	 *  
	 * @return
	 */
	public List<ProfileRoleDTO> findAllAvailableRoles() {
		//return managementBean.findAllRoles();
		//MSK#Change 3
		List<ProfileRoleDTO> rolesList=new ArrayList<ProfileRoleDTO>();
		ProfileRoleDTO dto=new ProfileRoleDTO();
		//Role
			dto.setProfileRoleID((long)1);
			dto.setRoleName("srsrulerole1");
			dto.setRoleShortDescription("Update-All request kinds");
			dto.setRoleLongDescription("Commissions Admin-Team Leader or higher position");
			dto.setEnabled(true);
			dto.setTimeCreated(new Date());
			dto.setTimeModified(new Date());
			Map<String,RuleDTO> ruleMap=new HashMap<String, RuleDTO>();
				RuleDTO rdto=new RuleDTO();
				rdto.setRuleDescription("Insert by Script");
				rdto.setRuleName("MODIFY_ALL_AGREEMENT_EXCEPTION_OWN");
				rdto.setRuleID((long)1);
				rdto.setTimeCreated(new Date());
				rdto.setCreatedBy("SRS");
				rdto.setTimeModified(new Date());
				rdto.setEnabled(true);
				rdto.setHasRuleData(true);
				rdto.setArithmeticDefault(ArithmeticType.EQUAL);
				rdto.setRuleSource(RuleSource.ROLE);
				rdto.setRuleDataRetreivedBySystem(true);
				rdto.setDataTypeDefault(RuleDataType.LONG);
				//RuleDataDTO ruleDataDTO=new RuleDataDTO();
				rdto.setRuleDataDTO(new RuleDataDTO(rdto));
				ruleMap.put("MODIFY_ALL_AGREEMENT_EXCEPTION", rdto);
		dto.setRuleMap((HashMap<String, RuleDTO>) ruleMap);
		//Menu
		dto.setDefaultMenuItemList(findAllAvailableMenuItems());
		List<RunnableRuleDTO> rrDTO=new ArrayList<RunnableRuleDTO>();
		RunnableRuleDTO r=new RunnableRuleDTO();
		
		r.setVersion(1);
		r.setCreationTime(Timestamp.valueOf("2022-04-21 09:01:15"));
		r.setReplacementTime(Timestamp.valueOf("2022-04-21 09:01:15"));
		r.setEffectiveTo(new Date());
		r.setCreatedBy("sampleOOO");
		r.setDataTypeDefault(RuleDataType.STRING);
		rrDTO.add(r);
		dto.setRunnableRuleList(rrDTO);
		dto.setAllowableRequestActionList(new ArrayList<AllowableRequestActionDTO>());
		rolesList.add(dto);
		
		return rolesList;
		
	}
	
	/**
	 * Retrieve all the rule arithmetic objects
	 * 
	 * @return
	 */
	public List<ArithmeticType> getArithmeticTypess() {
		List<ArithmeticType> list = new ArrayList<ArithmeticType>();
		for (ArithmeticType t : ArithmeticType.values()) {
			list.add(t);
		}
		return list;
	}
	
	/**
	 * Add MenuItemDTO to all userProfileDTO ProfileRoles
	 * @param userProfileDTO
	 */
	public void populateMenuItemsforRole(PartyProfileDTO userProfileDTO){
		//managementBean.populateMenuItemsforRole(userProfileDTO);
		//MSK#Change
        System.out.println("populateMenuItemsforRole---------------------------:::");
		
	}
	
	/**
	 * Adds RunnableRuleDTO to all userProfileDTO ProfileRoles
	 * @param userProfileDTO
	 */
	public void populateRuleItemsforRole(PartyProfileDTO userProfileDTO){
		//managementBean.populateRuleItemsforRole(userProfileDTO);
		//MSK#Change
		
		List<ProfileRoleDTO> profileRoles = userProfileDTO.getRoleItemList();
		if (profileRoles != null) {
			for (ProfileRoleDTO role : profileRoles) {
				ProfileRoleDTO newRoleDTO=null;
				try {
					newRoleDTO = new ProfileRoleDTO();//this.findRole(role.getProfileRoleID());
					newRoleDTO.setProfileRoleID((long)1);
					newRoleDTO.setRoleName("srsrulerole1");
					newRoleDTO.setRoleShortDescription("Update-All request kinds");
					newRoleDTO.setRoleLongDescription("Commissions Admin-Team Leader or higher position");
					newRoleDTO.setEnabled(true);
					newRoleDTO.setTimeCreated(new Date());
					newRoleDTO.setTimeModified(new Date());
					List<RunnableRuleDTO> rrDTO= new ArrayList<RunnableRuleDTO>();
					RunnableRuleDTO r=new RunnableRuleDTO();
					r.setRuleID((long) 2);
					r.setRuleName("NOTOWNAGREEMETRULE1");
					r.setRuleDescription("Not Own Agreement Rule");
					r.setHasRuleData(true);
					r.setArithmeticDefault(ArithmeticType.getType("="));
					r.setDataTypeDefault(RuleDataType.LONG);
					r.setEnabled(true);
					
					r.setVersion(1);
					r.setCreationTime(Timestamp.valueOf("2022-04-21 09:01:15"));
					r.setReplacementTime(Timestamp.valueOf("2022-04-21 09:01:15"));
					r.setEffectiveTo(new Date());
					r.setCreatedBy("sample1");
					r.setDataTypeDefault(RuleDataType.STRING);
					r.setEffectiveFrom(new Date());
					r.setArithmeticDefault(ArithmeticType.EQUAL);
					r.setDbKey((long)10);
					r.setEnabled(true);
					rrDTO.add(r);
					r=new RunnableRuleDTO();
					
					r.setRuleDescription("Insert by Script");
					r.setRuleName("MODIFY_ALL_AGREEMENT_EXCEPTION_OWN");
					r.setRuleID((long)1);
					r.setTimeCreated(new Date());
					r.setCreatedBy("SRS");
					r.setTimeModified(new Date());
					r.setEnabled(true);
					r.setHasRuleData(true);
					r.setArithmeticDefault(ArithmeticType.EQUAL);
					r.setRuleSource(RuleSource.ROLE);
					r.setRuleDataRetreivedBySystem(true);
					r.setDataTypeDefault(RuleDataType.LONG);
								
					r.setVersion(1);
					r.setCreationTime(Timestamp.valueOf("2022-04-21 09:01:15"));
					r.setReplacementTime(Timestamp.valueOf("2022-04-21 09:01:15"));
					r.setEffectiveTo(new Date());
					r.setEffectiveFrom(new Date());
					r.setDbKey((long)12);
					rrDTO.add(r);
					
					
					
					newRoleDTO.setRunnableRuleList(rrDTO);
					
				} catch (Exception e) {
					
					continue;
				}
				role.setRunnableRuleList(newRoleDTO.getRunnableRuleList());
				if (role.getRunnableRuleList() != null) {
					for (RunnableRuleDTO rule : role.getRunnableRuleList()) {
						rule.setProfileRoleDTO(role);
						rule.setUserProfileDTO(userProfileDTO);
					}
				}
			}
		}
		// also set the userProfileDTO in all the rules
		if (userProfileDTO.getRunnableRuleList() != null) {
			for (RunnableRuleDTO rule : userProfileDTO.getRunnableRuleList()) {
				rule.setUserProfileDTO(userProfileDTO);
			}
		}
	
		
		
	}

	public List<RuleDataType> findAllRuleDataTypes() {		
		//return managementBean.findAllRuleDataTypes();
		//MSK#Change 2
		List<RuleDataType> list=new ArrayList<RuleDataType>();
		for(RuleDataType r:RuleDataType.values()) 
			list.add(r);
		return list;
	}
}
