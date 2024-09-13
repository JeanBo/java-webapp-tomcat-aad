package za.co.liberty.business.guicontrollers.userprofiles;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.log4j.Logger;

import za.co.liberty.business.userprofiles.IProfileManagement;
import za.co.liberty.dto.userprofiles.AllowableRequestActionDTO;
import za.co.liberty.dto.userprofiles.ISessionUserProfile;
import za.co.liberty.dto.userprofiles.MenuItemDTO;
import za.co.liberty.dto.userprofiles.PartyProfileDTO;
import za.co.liberty.dto.userprofiles.ProfileRoleDTO;
import za.co.liberty.dto.userprofiles.RuleDTO;
import za.co.liberty.dto.userprofiles.RuleDTO.RuleSource;
import za.co.liberty.dto.userprofiles.RuleDataDTO;
import za.co.liberty.dto.userprofiles.RunnableRuleDTO;
import za.co.liberty.entity.userprofile.RolesMenuitems;
import za.co.liberty.exceptions.data.DataNotFoundException;
import za.co.liberty.interfaces.agreements.requests.RequestKindType;
import za.co.liberty.interfaces.rules.ArithmeticType;
import za.co.liberty.interfaces.rules.RuleDataType;
import za.co.liberty.persistence.agreement.IRequestEnumEntityManager;
import za.co.liberty.persistence.agreement.entity.RequestKindEntity;
import za.co.liberty.persistence.party.IRequestActionsEntityManager;


/**
 * Manage Roles.  
 * 
 * Note - The DTO's for Profile Roles does not have the same
 * hierarchy as the entity beans. The dto does not specify the 
 * linking object ({@linkplain RolesMenuitems} but instead uses 
 * an array of {@linkplain MenuItemDTO} items that uses the values
 * from the linking object.
 * 
 * @author jzb0608 - 23 Apr 2008
 * Modified by Dean Scott(30 July 2008) to use the ProfileManagement bean
 * 
 */
@Stateless
public class ProfileRoleManagement implements IProfileRoleManagement {	

	@SuppressWarnings("unused")
	private Logger logger = Logger.getLogger(this.getClass());
	
	/* Injected */	
	//@EJB
	//protected IProfileManagement managementBean;	
	
	
	//@EJB
	//protected IRequestEnumEntityManager requestEntityBean;
	
	//@EJB
	//protected IRequestActionsEntityManager requestActionsEntityManager;
	/**
	 * Retrieve all the roles
	 * 
	 * @return List of all menu items
	 */
	public List<ProfileRoleDTO> findAllRoles() {		
		//return managementBean.findAllRoles();
		System.out.println("MSK------------------findAllRoles");
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
		r.setCreatedBy("sample");
		r.setDataTypeDefault(RuleDataType.STRING);
		rrDTO.add(r);
		dto.setRunnableRuleList(rrDTO);
		dto.setAllowableRequestActionList(new ArrayList<AllowableRequestActionDTO>());
		rolesList.add(dto);
		
		return rolesList;
	}
	
	/**
	 * Find a role with its primary key. More expensive than a find
	 * as the mappings are also retrieved.
	 * 
	 * @param primaryKey
	 * @return
	 * @throws DataNotFoundException 
	 */
	public ProfileRoleDTO findRole(long primaryKey) throws DataNotFoundException {		
		//return managementBean.findRole(primaryKey);
		//msk#change
		System.out.println("findRole----------------"+primaryKey);
		ProfileRoleDTO dto=new ProfileRoleDTO();
		//Role
			dto.setProfileRoleID(primaryKey);
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
				rdto.setRuleID(primaryKey);
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
		PartyProfileDTO pDTO=new PartyProfileDTO();
			RunnableRuleDTO r=new RunnableRuleDTO();
		r.setVersion(1);
		r.setCreationTime(Timestamp.valueOf("2022-04-21 09:01:15"));
		r.setReplacementTime(Timestamp.valueOf("2022-04-21 09:01:15"));
		r.setEffectiveTo(new Date());
		r.setEffectiveFrom(new Date());
		r.setCreatedBy("sample");
		r.setDataTypeDefault(RuleDataType.STRING);
		rrDTO.add(r);
		dto.setRunnableRuleList(rrDTO);
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
	 * Create a menu item
	 * 
	 * @param dto New role to store
	 * @param user
	 * @return The newly created rule
	 */
	public ProfileRoleDTO createRole(ProfileRoleDTO dto, ISessionUserProfile user) {		
		//return managementBean.createRole(dto, user.getPartyOid()+"");
		//MSK#Cahnge
		dto.setProfileRoleID((long)( Math.random()*(201)+100));
		
		return dto;
	}
	
	/**
	 * Persist the changes made to a role. 
	 * 
	 * @param dto
	 * @return Updated Role
	 */
	public ProfileRoleDTO updateRole(ProfileRoleDTO dto, ISessionUserProfile user) {	
		//return managementBean.updateRole(dto, user.getPartyOid()+"");
		//MSK#Change
		System.out.println("MSK--------------updateRole");
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
		//MSK#Change
				
		List<MenuItemDTO> menu=new ArrayList<MenuItemDTO>();
		MenuItemDTO mdto=new MenuItemDTO();
		mdto.setAddAccess(true);
		mdto.setDeleteAccess(false);
		mdto.setModifyAccess(true);
		mdto.setMenuItemDescription("Maintain Practice ");
		mdto.setMenuItemID((long)1);
		mdto.setMenuItemName("Maintain Practice");
		mdto.setMenuItemLongDescription("Maintain Practice");
		mdto.setImplClazz("za.co.liberty.web.pages.AdvancedPracticePanel");
		mdto.setPanel(true);
		mdto.setEnabled(true);
		mdto.setDeleteAccess(false);
		mdto.setAddAccess(true);
		mdto.setModifyAccess(false);
		mdto.setTimeCreated(new Date());
		mdto.setTimeModified(new Date());
		mdto.setCreatedBy("");
		mdto.setDbKey((long)1);
		menu.add(mdto);	
		mdto=new MenuItemDTO();
		mdto.setAddAccess(true);
		mdto.setDeleteAccess(false);
		mdto.setModifyAccess(true);
		mdto.setMenuItemDescription("Advance Practice Panel1");
		mdto.setMenuItemID((long)2);
		mdto.setMenuItemName("Advance Practice Panel1");
		mdto.setMenuItemLongDescription("Advance Practice Panel1");
		mdto.setImplClazz("za.co.liberty.web.pages.AdvancedPracticePanel");
		mdto.setPanel(true);
		mdto.setEnabled(true);
		mdto.setDeleteAccess(false);
		mdto.setAddAccess(true);
		mdto.setModifyAccess(false);
		mdto.setTimeCreated(new Date());
		mdto.setTimeModified(new Date());
		mdto.setCreatedBy("");
		mdto.setDbKey((long)1);
		menu.add(mdto);	
		
		return menu;
	}

	public List<RequestKindEntity> findAllRequestKinds() {
		//return requestEntityBean.findAllRequestKinds();
		//MSK#Change
		List<RequestKindEntity> rke=new ArrayList<RequestKindEntity>();
		RequestKindEntity rk=new RequestKindEntity();
		rk.setConversionTime(Timestamp.valueOf("2022-04-21 09:01:15"));
		rk.setId((short) 1);
		rk.setName("sample");
		rk.setRequiredAuthorisers(2);
		rke.add(rk);
		return rke;
	}
	
	
	/**
	 * Retrieve a list of all rules that are available
	 * for selection
	 * 
	 * @return
	 */
	public List<RuleDTO> findAllAvailableRules() {
		//return managementBean.findAllAvailableRules();
		//MSK#Change
		System.out.println("ProfileRoleManagement:findAllAvailableRules()--------------");
		List<RuleDTO> dto=new ArrayList<RuleDTO>();
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
		dto.add(rdto);
		return dto;
	}
	
	/**
	 * Retrieve all the rule arithmetic objects
	 * 
	 * @return
	 */
	public List<ArithmeticType> getArithmeticTypes() {
		List<ArithmeticType> list = new ArrayList<ArithmeticType>();
		for (ArithmeticType t : ArithmeticType.values()) {
			list.add(t);
		}
		return list;
	}
	
	/**
	 * Retrieve all the request kind type objects
	 * 
	 * @return
	 */
	public List<RequestKindType> getRequestKindTypes() {
		List<RequestKindType> list = new ArrayList<RequestKindType>();
		for (RequestKindType t : RequestKindType.values()) {
			list.add(t);
		}
		return list;
	}

	/**
	 * Retrieve all the rule data type objects
	 * 
	 * @return
	 */
	public List<RuleDataType> findAllRuleDataTypes(){
		//return managementBean.findAllRuleDataTypes();
		//MSK#Change
		List<RuleDataType> rdt=new ArrayList<RuleDataType>();
		for(RuleDataType r:RuleDataType.values())
			rdt.add(r);
		
		return rdt;
	}	
	
}
