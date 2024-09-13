package za.co.liberty.business.security;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.log4j.Logger;

//import com.ibm.websphere.security.CustomRegistryException;
//import com.ibm.websphere.security.EntryNotFoundException;
//import com.ibm.websphere.security.UserRegistry;

import za.co.liberty.database.DBAccessor;
import za.co.liberty.dto.security.SecurityPageActionDTO;
import za.co.liberty.dto.userprofiles.AllowableRequestActionDTO;
import za.co.liberty.dto.userprofiles.ContextDTO;
import za.co.liberty.dto.userprofiles.ExplicitAgreementDTO;
import za.co.liberty.dto.userprofiles.ISessionUser;
import za.co.liberty.dto.userprofiles.ISessionUserProfile;
import za.co.liberty.dto.userprofiles.MenuItemDTO;
import za.co.liberty.dto.userprofiles.RuleDTO;
import za.co.liberty.dto.userprofiles.RuleDTO.RuleSource;
import za.co.liberty.dto.userprofiles.RuleDataDTO;
import za.co.liberty.dto.userprofiles.RunnableRuleDTO;
import za.co.liberty.dto.userprofiles.SessionUserHierarchyNodeDTO;
import za.co.liberty.dto.userprofiles.SessionUserHierarchyRoleDTO;
import za.co.liberty.dto.userprofiles.SessionUserIdDTO;
import za.co.liberty.exceptions.ErrorExceptionType;
import za.co.liberty.exceptions.SRSBusinessException;
import za.co.liberty.exceptions.UnResolvableException;
import za.co.liberty.exceptions.data.DataNotFoundException;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.exceptions.fatal.InconsistentConfigurationException;
import za.co.liberty.exceptions.security.InvalidUserIdException;
import za.co.liberty.exceptions.security.UserIdNotInSessionException;
import za.co.liberty.helpers.validators.RuleValidator;
import za.co.liberty.interfaces.agreements.requests.RequestKindType;
import za.co.liberty.interfaces.rules.RuleType;
import za.co.liberty.interfaces.rules.RuleType.ApplicableEditState;
import za.co.liberty.interfaces.security.SecurityPageActionType;
import za.co.liberty.persistence.party.entity.fastlane.PartyProfileFLO;
import za.co.liberty.persistence.party.entity.userprofile.AbstractRuleLinkEntity;
import za.co.liberty.persistence.party.entity.userprofile.PartyProfileEntity;
import za.co.liberty.persistence.party.entity.userprofile.RoleEntity;
import za.co.liberty.persistence.party.entity.userprofile.RuleEntity;

import za.co.liberty.xml.queries.Query;

/**
 * Manages Security
 * 
 * @author Dawie Rossouw (dhr1910)
 * @author Dean Scott - Changed TAM update
 * @author Jean Bodemer (JZB0608) - 12 Nov 2008 - Re-work methods to not use class variables
 * 		and use {@link SessionUserProfileDTO} to keep track of user.
 * @author Jean Bodemer (JZB0608) - 19 Oct 2009 - Re-work of tam role updater and the 
 * 		removal of UserProfileEntityFacade
 * @author pks2802 - 01 Oct 2013 - Updated code for Manages Agreement for View Infoslip
 *
 */
@Stateless(name = "SecurityManagement")
public class SecurityManagement implements ISecurityManagement {
	
	/*
	 * Static map holding SessionUserID object for uacf-id's
	 */
	private static Map<String, SessionUserIdDTO> userSessionMap =
 			new ConcurrentHashMap<String, SessionUserIdDTO>();
	
	private static final String[][] FIXED_MENU_ITEMS = new String[][] {
		{"1","HOME","Home","za.co.liberty.web.pages.Home"},
//		{"2","LOGOFF","Logoff","za.co.liberty.web.pages.Logoff"},
//		{"3","OLD_SRS","Old SRS","za.co.liberty.web.pages.OldSRS"},
		{"4","ENQUIRY_CONTRACT_NO","Contract No.","za.co.liberty.web.pages.ContractNoEnquiyPage"},
		{"5","ADMIN_MENU_ITEM","Menu Admin","za.co.liberty.web.pages.admin.MenuItemAdmin"},
		{"6","ADMIN_ROLES","Role Admin","za.co.liberty.web.pages.admin.RoleAdmin"},
		{"7","ADMIN_RULES","Rule Admin","za.co.liberty.web.pages.admin.RulesAdmin"},
		{"8","ADMIN_USER_PROFILE","Profile Admin","za.co.liberty.web.pages.admin.UserAdmin"},
		{"9","TimedRoleUpdate","TAM Updater","za.co.liberty.web.pages.admin.RoleUpdateScheduler"},
		{"10","AGREEMENT_PRIVILEGE","User Access","za.co.liberty.web.pages.agreementprivilege.AgreementLinkingPage"},
		{"21","Hierarchy","Maintain","za.co.liberty.web.pages.hierarchy.MaintainHierarchyPage"},
		{"41","CommissionStatements","Statements","za.co.liberty.web.pages.reports.MaintainReportsPage"},
		{"61","MAINTAIN_PARTY","Maintain","za.co.liberty.web.pages.party.MaintainPartyPage"},
		{"200","REQUEST_STATUS_ENQUIRY","Request Enquiry","za.co.liberty.web.pages.request.RequestEnquiryPage"},
		{"201","TEAM_ADMIN_MENU","Team Admin","za.co.liberty.web.pages.admin.TeamAdminPage"},
		{"202","REQUEST_CATEGORY","Request Category","za.co.liberty.web.pages.admin.RequestCategoryAdminPage"},
		{"203","HIERARCHY_TREE_VIEW","Tree View","za.co.liberty.web.pages.hierarchy.HierarchyTreePage"},
		{"206","AGREEMENT_MAINTAIN","Maintain","za.co.liberty.web.pages.maintainagreement.MaintainAgreementPage"},
		{"215","COMMUNICATION_SCHEDULER","Comm Scheduler","za.co.liberty.web.pages.admin.ScheduledCommunicationSchedulerPage"},
		{"241","BUSINESSCARD_DETAILS_PAGE","Business Card","za.co.liberty.web.pages.businesscard.BusinessCardDetailsPage"},
		{"261","SEGMENT_NAME_ADMIN","Segment Name Admin","za.co.liberty.web.pages.admin.SegmentNameAdminPage"},
		{"262","SEGMENT_ADMIN","Segment Admin","za.co.liberty.web.pages.admin.SegmentGuiPage"},
		{"263","DIFF_FACTOR","Diff Factor","za.co.liberty.web.pages.admin.DifferentialPricingPage"},
		{"280","ASTUTE_DELTAS_REQUEST","Astute Delta Request","za.co.liberty.web.pages.admin.AstuteRepRequestPage"},
		{"300","REL_FE","Release FE","za.co.liberty.web.pages.releasefe.ReleaseFutureEarningPage"},
		{"320","FIT_AND_PROPER_BROADCAST_REQUEST","F&P trigger Request","za.co.liberty.web.pages.admin.MoodleTriggerRequestPage"},
		{"321","FIT_AND_PROPER","Journey to Prof.","za.co.liberty.web.pages.fitprop.FitAndProperPage"},
		{"340","TAX_XML","Tax XML Process","za.co.liberty.web.pages.tax.TaxGuiPage"},
		{"370","FRANCHISE_TEMPLATE","Maintain Template","za.co.liberty.web.pages.franchisetemplates.MaintainFranchiseTemplatePage"},
		{"420","ADVANCED_PRACTICE_PAGE","Advanced Practice","za.co.liberty.web.pages.advancedPractice.MaintainAdvancedPracticePage"},
		{"440","Contract_Transfer","Contract Transfer","za.co.liberty.web.pages.core.ContractTransferPage"},
		{"441","Segmented_Transfer","Segmented Transfer","za.co.liberty.web.pages.core.SegmentedTransferPage"},
		{"442","CORE_REPORT","Core Report","za.co.liberty.web.pages.reports.CoreReportEnquiryPage"},
		{"443","Book_Transfer","Book Transfer","za.co.liberty.web.pages.core.BookTransferPage"},
		{"480","MI_TABLES","MI Tables","za.co.liberty.web.pages.admin.ratingtables.MIRatingGUIPage"},
//		{"500","Policy_Transactions","Policy Transactions","za.co.liberty.web.pages.transactions.PolicyTransactionsPage"},
		{"501","Transaction_Rejects","Transaction Rejects","za.co.liberty.web.pages.transactions.TransactionRejectsPage"},
		{"520","SALES_BC_LINKING","BC Linking","za.co.liberty.web.pages.salesBCLinking.SalesBCLinkingPage"},
		{"580","Generic_Transactions","Transaction Page","za.co.liberty.web.pages.transactions.TransactionGuiPage"}
	};
	
	private static final String[][] FIXED_PANEL_ARRAY = new String[][] {
			{"62","MAINTAIN_PARTY_PERSON_OR_ORGANISATION_DETAILS_PANEL","Person/Organisation Details","za.co.liberty.web.pages.party.BasePartyDetailsPanel"},
			{"63","MAINTAIN_ORG_HIERARCHY_DETAILS_PANEL","Hierarchy Details","za.co.liberty.web.pages.hierarchy.HierarchyNodePanel"},
			{"64","MAINTAIN_CONTACT_DETAILS","Contact Details","za.co.liberty.web.pages.contactdetail.ContactDetailsPanel"},
			{"204","BULK_AGREEMENT_TRANSFER","Linked Agreement Transfer","za.co.liberty.web.pages.hierarchy.BulkTransferPanel"},
			{"205","BANKING_DETAILS","Banking Details","za.co.liberty.web.pages.party.BankingDetailsPanel"},
			{"207","FAIS_LICENSE","FAIS License","za.co.liberty.web.pages.maintainagreement.FAISLicensePanel"},
			{"208","DISTRIBUTION_PAYS_TO","Distribution, Pays To, Pool Account","za.co.liberty.web.pages.maintainagreement.DistributionPaysToPanel"},
			{"209","AGREEMENT_DETAILS","Agreement Details","za.co.liberty.web.pages.maintainagreement.AgreementDetailsPanel"},
			{"210","AGREEMENT_CODES","Agreeement Codes","za.co.liberty.web.pages.maintainagreement.AgreementCodesPanel"},
			{"211","FIXED_EARNING_DEDUCTION","Fixed Earnings & Deductions","za.co.liberty.web.pages.maintainagreement.payroll.FixedEarningsAndDeductionsPanel"},
			{"212","PAYMENT_SCHEDULER","Payment Scheduler Panel","za.co.liberty.web.pages.maintainagreement.PaymentSchedulerPanel"},
			{"213","AGREEMENT_HIERARCHY","Agreement Hierarchy","za.co.liberty.web.pages.maintainagreement.AgreementHierarchyPanel"},
			{"214","ASSOCIATED_AGREEMENTS","Associated Agreements","za.co.liberty.web.pages.maintainagreement.AssociatedAgreementsPanel"},
			{"220","TAX_DETAILS","Tax Details","za.co.liberty.web.pages.taxdetails.TaxDetailsPanel"},
			{"240","PARTY_HIERARCHY_PANEL","Party Hierarchy Panel","za.co.liberty.web.pages.party.PartyHierarchyPanel"},
			{"260","AQC_PANEL","Advisor Quality Code","za.co.liberty.web.pages.maintainagreement.AdvisorQualityCodePanel"},
			{"360","INCENTIVE_PANEL","Incentives","za.co.liberty.web.pages.maintainagreement.AgreementIncentivesPanel"},
			{"371","MAINTAIN_FRANCHISE_TEMPLATE_DETAILS_PANEL","Template Details","za.co.liberty.web.pages.franchisetemplates.FranchiseTemplatePanel"},
			{"380","MEDICAL_AID_PANEL","Medical Aid","za.co.liberty.web.pages.party.MedicalAidDetailsPanel"},
			{"400","PROVIDENT_FUND_PANEL","Provident Fund","za.co.liberty.web.pages.maintainagreement.ProvidentFundDetailsPanel"},
			{"421","MAINTAIN_ADVANCED_PRACTICE_PANEL","Advanced Practice Panel","za.co.liberty.web.pages.advancedPractice.AdvancedPracticePanel"},
			{"460","STATEMENT_TRACKING","Statement Tracking","za.co.liberty.web.pages.reports.ReportTrackingPanel"},
			{"461","STATEMENT_GENERATION","Statement Generation","za.co.liberty.web.pages.reports.ReportGenerationPanel"},
			{"462","STATEMENT_VIEWING","Statement Viewing","za.co.liberty.web.pages.reports.ViewInfoSlipPanel"},
			{"481","MAINTAIN_ORG_MI_REPORTING_DETAILS_PANEL","MI Reporting Details Panel","za.co.liberty.web.pages.hierarchy.MIReportingDetailsPanel"},
			{"540","AGENCY_POOL_DETAILS","Agency Pool Details","za.co.liberty.web.pages.maintainagreement.AgencyPoolAccountDetailsPanel"}
	};
	
	
	/* Injected */
//	@EJB
//	SecurityContextBPOHome securityContextBPOHome;
//
//	@EJB
//	IPartyManagement partyManager;
//
//	@EJB
//	IAgreementManagement agreementManagement;
//	
//	@EJB
//	IServerManagement serverManagementBean;
//
//	@EJB
//	IAgreementPrivilegeManagement agreementPrivilegeManagementBean;
//	
//	@EJB
//	IRoleEntityManager roleEntitymanager;
//	
//	@EJB
//	IPartyProfileEntityManager partyProfileEntityManager;
//	
//	@EJB 
//	IPartyEntityManager partyEntityManager;
//	
//	@EJB
//	ISecurityManagement securityManagementBean;
//	
//	@EJB
//	IAgreementEntityManager agreementEntityManager;
//	
//	@EJB
//	IExplicitAgreementEntityManager explicitAgreementEntityManager;
//	
//	@Resource(name = "PartyDatasource")
//	DataSource partyDatasource;
//
//	@Resource
//	SessionContext context;
	
	static Boolean isDevelopmentPc;
	
	DBAccessor acc = new DBAccessor();
	
	Query partyQuery = null;
	
//	private Map<String, SessionUserIdDTO> 
	
	public static long SYSTEM_PARTY_OID = 236;

	
	/* Attributes */	
	private static final Logger logger = Logger.getLogger(SecurityManagement.class);
	
	public SecurityManagement() {
//		logger.setLevel(Level.DEBUG);
	}
	
	/**
	 * Will verify if a user's party exist, resulting in checking if the user 
	 * exist on the srs system via security id 
	 * 
	 * @param userid
	 */
	public boolean isUserValid(String userid)  {
		return false;
	}
	
	/**
	 * User logged on
	 * 
	 * @param user
	 */
	public void logUserLogonAction(ISessionUser user) {
		logger.info("UserLogon user=" + ((user!=null)? user.getUacfId() :null)
				+ " ,partyOid="+((user!=null)? user.getPartyOid() :null));
		
		// Ignore if user is null, play it safe
		if (user == null) {
			logger.warn("NO USER Defined - UserLogon user=" + ((user!=null)? user.getUacfId() :null)
					+ " ,partyOid="+((user!=null)? user.getPartyOid() :null));
			return;
		}
		
	}
	
	/**
	 * User has concurrent sessions
	 * 
	 * @param sessionUser
	 */
	public void logUserConcurrentSession(ISessionUser user) {
		logger.info("CONCURRENT SESSION user=" + ((user!=null)? user.getUacfId() :null)
				+ " ,partyOid="+((user!=null)? user.getPartyOid() :null));
		
		if (user == null) {
			// THis should be impossible, just playing safe
			logger.info("NO USER Defined - CONCURRENT SESSION user=" + ((user!=null)? user.getUacfId() :null)
				+ " ,partyOid="+((user!=null)? user.getPartyOid() :null));
			return;
		}
		
	}
	
	
	
	/**
	 * User logged on
	 * 
	 * @param user
	 */
	public void logUserPageAction(Class pageClass, ISessionUserProfile user, SecurityPageActionType userActionType, ContextDTO contextDto) {
		logUserPageAction(pageClass, user, userActionType, contextDto, null);
	}
	
	/**
	 * User opened a page
	 * 
	 * @param user
	 */
	public void logUserPageAction(Class pageClass, ISessionUserProfile user, 
			SecurityPageActionType userPageType, ContextDTO contextDto, 
			SecurityPageActionDTO pageActionDto) {
		String userId = (user!=null) ? user.getUacfId() : null;
		Long agreementNr = null;
		Long partyId = null;
		if (contextDto!=null) {
			if (contextDto.getPartyContextDTO()!=null) {
				partyId = contextDto.getPartyContextDTO().getPartyOid();
			}
			if (contextDto.getAgreementContextDTO()!=null) {
				agreementNr = contextDto.getAgreementContextDTO().getAgreementNumber();
			}
		}
		
		logger.info("PageAction  user="+userId + " ,pageAction=" + userPageType
				+ " ,pageName=" + ((pageClass!=null)?pageClass.getName() : null)
				+ " ,contextAgreement=" + agreementNr + " ,partyId=" + partyId);
		
		// Can't log if the user has not been set as yet 
		if (user==null) {
			logger.warn("NO User defined for PageAction  user="+userId + " ,pageAction=" + userPageType
					+ " ,pageName=" + ((pageClass!=null)?pageClass.getName() : null)
					+ " ,contextAgreement=" + agreementNr + " ,partyId=" + partyId);
			return;
		}
		
		
	}
	
	
	/**
	 * DEFAULT LOGIN METHOD.
	 * Automatically logs a user in if their user id is already in the
	 * session.  Can throw the following BusinessExceptions types
	 * 
	 * {@linkplain ErrorExceptionType#USER_ID_NOT_IN_SESSION}, 
	 * {@linkplain ErrorExceptionType#INVALID_USER_ID}
	 * 
	 * @return The session user object
	 * @throws SRSBusinessException
	 * @throws UnResolvableException
	 */
	public ISessionUserProfile getUserInSession(String sessionId) 
		throws InvalidUserIdException, UserIdNotInSessionException {
		
		String uacfID = "SRS1802";
		if (uacfID == null) {
			if (logger.isDebugEnabled())
				logger.debug("getUserInSession() :" + uacfID + ",  uacfID == null");
			throw new UserIdNotInSessionException();
		} else if (uacfID.toUpperCase().equals("UNAUTHENTICATED")) {
			if (logger.isDebugEnabled())
				logger.debug("getUserInSession :" + uacfID + ",  UNAUTHENTICATED");
			return null;
		}
		uacfID = uacfID.toUpperCase();
		
		if (logger.isDebugEnabled())
			logger.debug("getUserInSession :" + uacfID);
		
		return getSessionUser(uacfID, sessionId);
	}
	
	/**
	 * Returns a SessionUserIdDTO object that is unique per uacf id.  This object
	 * holds the most recent session id
	 * 
	 * @param uacfId
	 * @param sessionId
	 * @return
	 */
	public SessionUserIdDTO getUserSessionIdForUacf(String uacfId, String sessionId) {
		
		// TODO Jean - remember to remove
		logger.info("#JB - uacf-id=" + uacfId + " ,sessionId="+ sessionId);
		
		SessionUserIdDTO sessionIdDto = userSessionMap.get(uacfId);
		
		/*
		 * First session for user
		 */
		if (sessionIdDto == null) {
			sessionIdDto = new SessionUserIdDTO(uacfId, sessionId);
			userSessionMap.put(uacfId, sessionIdDto);
		}
		
		/*
		 * Update to the latest session id 
		 */
		if (!sessionIdDto.isCurrentSessionId(sessionId) && !sessionIdDto.isExpiredSessionId(sessionId)) {
			// This is not the current session id
			sessionIdDto.updateNewSessionId(sessionId);
		}
		
		return sessionIdDto;
		
	}
	
	/**
	 * This method should only be used for testing
	 * 
	 * @param uacfId
	 * @return
	 * @throws InvalidUserIdException 
	 */
	public ISessionUserProfile getUserForTesting(String uacfId) throws InvalidUserIdException {
		return getSessionUser(uacfId.toUpperCase(), null);
	}
	
	
	public boolean isUserUnauthenticated() throws UserIdNotInSessionException {
		String uacfID = "SRS1802";
		if (uacfID == null) {
			if (logger.isDebugEnabled())
				logger.debug("isUserUnauthenticated :" + uacfID + ",  uacfID == null");
			throw new UserIdNotInSessionException();
		} else if (uacfID.toUpperCase().equals("UNAUTHENTICATED")) {
			if (logger.isDebugEnabled())
				logger.debug("isUserUnauthenticated :" + uacfID + ",  UNAUTHENTICATED");
			return true;
		}
		if (logger.isDebugEnabled())
			logger.debug("isUserUnauthenticated :" + uacfID + ",  false");
		return false;

	}
	
	
		
	/**
	 * Will get all the relavent User details and store a reference to them here
	 * @param uacfID
	 * @throws InvalidUserIdException 
	 */
	private ISessionUserProfile getSessionUser(String uacfID, String sessionId) 
				throws InvalidUserIdException {	
		
		long startTime = System.currentTimeMillis();
		
//		if (true) {
//			logger.info("Return system user profile");
//			return new SystemUserProfileDTO();
//		}
		
		SessionUserProfileDTO user = new SessionUserProfileDTO();
		uacfID = uacfID.toUpperCase();
		user.uacfId=uacfID;
		
		/* Retrieve logged-in user */
//		PartyProfileEntity partyProfile;
//        try {
//        	partyProfile = partyProfileEntityManager.findPartyProfileWithUacfID(uacfID, 
//        			PartyProfileFetchType.DETAIL);
//		} catch (DataNotFoundException e1) {
//			throw new InvalidUserIdException(e1);
//		}
	    
       	user.partyOid=112;
       	user.profileOid=112;
 
       	/* Get hierarchy data */
       	getHierarchyData(user);
       	
       	/* Set up rule list map */
		Map<RuleType, RuleDTO> ruleListMap= user.ruleListMap;
		ruleListMap.clear();		
		
		List<RuleDTO> ruleList = getRules(null);
		
		if(ruleList != null){
			for(RuleDTO rule : ruleList){	
				//NB all RULE names must be in upper case
				//TODO MUST USE MOST RESTRICTIVE LIMIT, Hashmap will only use the latest but we must extract the rule, check the limit and put the most restricitve limit back
				RuleType ruleType = RuleType.getRuleTypeWithDatabaseName(rule.getRuleName().toUpperCase());
				
				if (ruleType == null) {
					throw new InconsistentConfigurationException(
							"A rule that is linked to this user is not configured as valid runtime rule type." +
							" Database rule type = " + rule.getRuleName().toUpperCase());
				}
				ruleListMap.put(ruleType, rule);
			}
		}
		/* Remove the conflicting user rules, cloud be that user has more than one role with conflicting rules
		 * It was decided to use the most restrictive rules
		 * */
		removeConflictingRulesForUser(ruleListMap);

		// Explicit list

		user.explicitAgreementMap = new HashMap<Long, ExplicitAgreementDTO>();
		user.reportsToAgreementList=new ArrayList<Long>();


		// Own agreement list
		user.ownAgreementList = new ArrayList<Long>();

		
       	/* Set isServicedBy data */
       	user.servicesAgreementList = new ArrayList<Long>();

       	
       	/* Set isManagedBy data */

       	user.managesAgreementList = new ArrayList<Long>();

		RuleType[] ruleTypeArr = new RuleType[1];
		ruleTypeArr[0] = RuleType.VIEW_ALL_HIERARCHY_NODES;
		
		

       	
		/* Menu item list */
		user.menuItemList= getMenuItems(null);	
		if (user.menuItemList==null) {
			user.menuItemList = new ArrayList<MenuItemDTO>();
		}
//		if (logger.isDebugEnabled()) {
			logger.info("--Menu items");
			for (MenuItemDTO menu : user.menuItemList) {
				logger.info("  -- menu item:"+menu.toString() + " menu.class=" + menu.getImplClazz());
			}
//		}
		
		/* Request auth rules */
		user.requestActionMap = getRequestActionItems(null);
		if (logger.isDebugEnabled()) {
			logger.debug("--Request Actions");
			for (RequestKindType kind : user.requestActionMap.keySet()) {
				logger.debug("  -- requestAction : " 
						+ user.requestActionMap.get(kind).toString());
			}
		}
		
		user.ruleValidator=getRuleValidator(user);		
		SessionUserHierarchyNodeDTO hierarchyNode = (user.hierarchyNodeRoleList!=null && user.hierarchyNodeRoleList.size()>0) ?
			user.hierarchicalNodeAccessList.iterator().next() : null;
		
		logger.info("User authentication of \"" + uacfID + "\" took "
					+ (System.currentTimeMillis()-startTime) + " millis." 
					+ "\n  --ownAgreements="+user.ownAgreementList
					+ "\n  --rules="+user.ruleListMap.keySet()
					+ "\n  --explicitAgreements="+user.explicitAgreementMap.keySet()
					+ "\n  --hasHierarchicalAccess="+user.hasHierarchicalAccess()
					+ "\n  --topHierarchyNode=" + ((hierarchyNode==null) ? null : "  orgType=" + hierarchyNode.getOrganisationType()
							+ "  ,partyOid=" + hierarchyNode.getPartyOid()));
		return user;
	}

	/**
	 * Initialise the hierarchical data
	 * 
	 * @param user
	 */
	private void getHierarchyData(SessionUserProfileDTO user) {
		
		/* Get data required for Hiearchy security */
//       	List<PartyRoleContextFLO> contextList= partyEntityManager.findActivePartyRolesWherePartyIsContext(
//       			user.getPartyOid(), Constants.ROLEPLAYER_TYPE_PERSON);
   
       	// Build a list of isRunBy && isAdministeredBy
       	List<SessionUserHierarchyRoleDTO> hierarchyRoleList = new ArrayList<SessionUserHierarchyRoleDTO>();
       	
//		for (PartyRoleContextFLO flo : contextList) {
//			
//			OrganisationType orgType = OrganisationType.getOrganisationType(flo.getLinkType().longValue());
//			PartyRoleType roleType = PartyRoleType.getPartyRoleType(flo.getType().longValue(), false);
//			
//			logger.debug("## Manager flo: orgType="+orgType+" ,roleType="+roleType + " ,linkOid="+flo.getLinkOid());
//			
//			if (orgType!=null) {
//				
//				if (roleType == PartyRoleType.ISRUNBY || roleType == PartyRoleType.ISADMINISTEREDBY || roleType == PartyRoleType.ISSUPERVISEDBY || roleType == PartyRoleType.ISCOMPLIENCEOFFICEROF || roleType == PartyRoleType.ISCOMANAGER) {
//					hierarchyRoleList.add(new SessionUserHierarchyRoleDTO(flo.getLinkOid(), roleType, orgType)); //MZP0801 added ISADMINISTEREDBY and ISSUPERVISEDBY, as well as changed the If to || instead of else if
//				} /*else if (roleType == PartyRoleType.ISADMINISTEREDBY) {
//					hierarchyRoleList.add(new SessionUserHierarchyRoleDTO(flo.getLinkOid(), roleType, orgType));
//				}**/ 
//			}
//		}    

		
		Collections.sort(hierarchyRoleList);
		Set<SessionUserHierarchyNodeDTO> hierarchySet = new LinkedHashSet<SessionUserHierarchyNodeDTO>();
		Set<Long> hierarchyNodePartyOidSet = new HashSet<Long>();
		
		SessionUserHierarchyRoleDTO parentDto = null;
	
//		if (hierarchyRoleList.size() >=2 && hierarchyRoleList.get(0).get)
		for (SessionUserHierarchyRoleDTO dto : hierarchyRoleList) {
			
			if (parentDto!=null && 
					parentDto.getOrganisationType()!=dto.getOrganisationType()) {
				// We only process the top hierarchy role with the highest organisation type.
				break;
			}
			
			logger.debug("  ## Retrieve access list for flo: roleType="
					+dto.getRoleType()+" ,orgType="+dto.getOrganisationType() + " ,partyOid="+dto.getPartyOid());
			
			// Add node to hierarchy
			// Build the rest of the hierarchy nodes
			// Retrieve the hierarchy nodes below this node.
			hierarchySet.add(new SessionUserHierarchyNodeDTO(
					dto.getPartyOid(), 
					dto.getOrganisationType()));
			hierarchyNodePartyOidSet.add(dto.getPartyOid());
			
//			List<IPartyRoleFLO> roleList = partyEntityManager.findRolePlayerChildrenForNode(
//					dto.getPartyOid(), 
//					PartyRoleType.ISPARTOF,
//					Arrays.asList(OrganisationType.values()), 
//					PartyRoleFLO.class);
			
			
			
//			logger.debug("  ## retrieved access list=" + roleList.size());
//			for (IPartyRoleFLO flo : roleList) {
//				logger.debug("    ## add roleType=" + flo.getRoleType()
//						+ " ,partyOid="+flo.getPartyOid());
//				hierarchySet.add(new SessionUserHierarchyNodeDTO(flo.getPartyOid(), 
//						OrganisationType.getOrganisationType(flo.getRoleType())));
//				hierarchyNodePartyOidSet.add(dto.getPartyOid());
//			}
//			
			parentDto = dto;
		}
		

		//Add for SIMS HIERARCHY LBF  Access to Additional Branch of Roles
//		List<AdditionalBranchOFFLO>  additionalBranchOFFLOs = partyManager.getAddBrchOfParentsAndChildren(user.partyOid);
//		for (AdditionalBranchOFFLO additionalBranchOFFLO : additionalBranchOFFLOs) {
//			SessionUserHierarchyNodeDTO additionalBranchchildNode = new SessionUserHierarchyNodeDTO(additionalBranchOFFLO.getChildOID(), 
//					OrganisationType.getOrganisationType(additionalBranchOFFLO.getChildType() ));
//			SessionUserHierarchyRoleDTO additionalBranchParenNode = new SessionUserHierarchyRoleDTO(additionalBranchOFFLO.getParentOID(), 
//					PartyRoleType.getPartyRoleType(additionalBranchOFFLO.getManagerRoleType(), false), OrganisationType.getOrganisationType(additionalBranchOFFLO.getParentType()));
//			if(hierarchyRoleList.contains(additionalBranchParenNode)){
//				hierarchySet.add(additionalBranchchildNode);
//			}
//		}
		user.hierarchicalNodeAccessList = Collections.unmodifiableSet(hierarchySet);
		user.hierarchicalNodePartyOidAccessSet = Collections.unmodifiableSet(hierarchyNodePartyOidSet);
		user.hierarchyNodeRoleList = Collections.unmodifiableList(hierarchyRoleList);
		
//		if (logger.isDebugEnabled()) {
//			logger.debug("  user hierarchy  partyOid="+user.hierarchyNodePartyOid
//					+ "  ,isRuns="+user.isRunsHierarchyNode
//					+ "  ,isAdmin="+user.isAdministersHierarchyNode
//					+ "  ,orgType="+user.hierarchyNodeOrganisationType
//					+ "  ,hasHierarchyAccess="+user.hasHierarchicalAccess());
//		}
//				
		
		//TODO 
//		if (user.hasHierarchicalAccess()) {
//			// Build the rest of the hierarchy nodes
//			// Retrieve the hierarchy nodes below this node.			
//			hierarchySet.add(new SessionUserHierarchyNodeDTO(
//					user.hierarchyNodePartyOid, 
//					user.hierarchyNodeOrganisationType));
//			
//			List<IPartyRoleFLO> roleList = partyEntityManager.findRolePlayerChildrenForNode(
//					user.hierarchyNodePartyOid, 
//					PartyRoleType.ISPARTOF,
//					Arrays.asList(OrganisationType.values()), 
//					PartyRoleFLO.class);
//			
//			for (IPartyRoleFLO flo : roleList) {
//				hierarchySet.add(new SessionUserHierarchyNodeDTO(flo.getPartyOid(), 
//						OrganisationType.getOrganisationType(flo.getRoleType())));
//			}
//			
//		}
//		if (flo.getType().longValue()== Constants.PARTYROLE_TYPE_ISRUNBY) {
//		
//		user.hierarchyNodePartyOid = flo.getLinkOid();
//		user.isRunsHierarchyNode=true;
//		user.hierarchyNodeOrganisationType=orgType;
//		break;
//	} else if (flo.getType().longValue()== Constants.PARTYROLE_TYPE_ISADMINISTEREDBY) {
//		user.hierarchyNodePartyOid = flo.getLinkOid();
//		user.isAdministersHierarchyNode=true;
//		user.hierarchyNodeOrganisationType=orgType;
//		break;
//	}
	}

	/**
	 * Validate a rule on a DataObject using annotations
	 * 
	 * @param sessionUser The currently logged in user
	 * @param dataObject Data object with Rule annotations
	 * @param ruleTypes  List of rules to apply
	 * @return
	 */
	public boolean validateRuleOnDataObject(ISessionUserProfile sessionUser, Object dataObject, 
			RuleType ... ruleTypes) {
		if ((sessionUser instanceof SessionUserProfileDTO) == false) {
			// Only allow user profiles created by this bean
			throw new IllegalArgumentException("Invalid type of session user");
		}
		if (ruleTypes==null || ruleTypes.length==0) {
			throw new IllegalArgumentException("At least one rule type is required when validating rules.");
		}
		return ((SessionUserProfileDTO)sessionUser).ruleValidator.validateWithAnnotatedObject(dataObject,
				ruleTypes);
	}
	
	/**
	 * Validate a rule on a value
	 * 
	 * @param sessionUser The currently logged in user
	 * @param dataObject Data object with Rule annotations
	 * @param ruleTypes  List of rules to apply
	 * @return
	 */
	public boolean validateRuleOnValue(ISessionUserProfile sessionUser, Object value, 
			RuleType ... ruleTypes) {
		if ((sessionUser instanceof SessionUserProfileDTO) == false) {
			// Only allow user profiles created by this bean
			throw new IllegalArgumentException("Invalid type of session user");
		}
		if (ruleTypes==null || ruleTypes.length==0) {
			throw new IllegalArgumentException("At least one rule type is required when validating rules.");
		}
		return ((SessionUserProfileDTO)sessionUser).ruleValidator.validate(value, ruleTypes);
	}
	
	
	/**
	 * Check all users on TAM and their roles exist on our DB
	 * 
	 */
	@SuppressWarnings("unchecked")
	public void syncPartyProfilesAndRolesToTAM() {
		logger.info("Starting TAM Role updater");
	}
	

	/**
	 * Create a new System Tam Role
	 * 
	 * @param roleName
	 */
	@TransactionAttribute (TransactionAttributeType.REQUIRES_NEW)
	public void createAndCommitSystemRole(String roleName) {

	}		
	
	/**
	 * Wraps the call to {@linkplain #syncPartyProfileToTam(PartyProfileFLO, Map)} in a new
	 * transaction that forces a commit.
	 * 
	 * @param partyProfileFLO
	 * @param systemRoleMap
	 * @return
	 */
	@TransactionAttribute (TransactionAttributeType.REQUIRES_NEW)
	public boolean syncPartyProfileToTamAndCommit(PartyProfileFLO partyProfileFLO, 
			Map<String, RoleEntity> systemRoleMap) {
		return syncPartyProfileToTam(partyProfileFLO, systemRoleMap);
	}
	
	/**
	 * Calls the method {@linkplain #syncPartyProfileToTam(PartyProfileFLO, Map)}.  
	 * 
	 * @param partyProfileFLO
	 * @return  true if changed
	 */
	public boolean syncPartyProfileToTam(PartyProfileFLO partyProfileFLO) {
		return syncPartyProfileToTam(partyProfileFLO, getSystemRoleMap());
	}
	
	/**
	 * Get a Map of all systemRoles, keyed by (upper case) role name. 
	 * 
	 * @return
	 */
	protected Map<String, RoleEntity> getSystemRoleMap() {

		return Collections.EMPTY_MAP;
	}
	
	/**
	 * Sync the passed partyProfile with the roles on Tam
	 * 
	 * @param partyProfileFLO
	 * @param systemRoleMap
	 */
	@SuppressWarnings("unchecked")
	public boolean syncPartyProfileToTam(PartyProfileFLO partyProfileFLO, Map<String, RoleEntity> systemRoleMap) {
			
		return false;
		
	}
	
	/**
	 * Extract the role name from the TAM role name
	 * 
	 * @param tamRole
	 * @return
	 */
	private String extractRoleNameFromTamRole(String tamRole) {
		if (tamRole==null || tamRole.indexOf(",") <=0) {
			return "";
		}
		return tamRole.substring(3, tamRole.indexOf(",")).toUpperCase();
	}

	/**
	 * Retrieve the application version information (specified in EAR MANIFEST)
	 * 
	 * @return
	 */
	public String getApplicationVersion() {
		return "JEAN.TEST.1";
	}

	/**
	 * This is the implementation of the Rule Validator. Loads its data from the
	 * User Profile. If data has to be loaded from another place treat this as a
	 * exception. OwnAgreementRule, NotOwnAgreementRule and AgreementListRule
	 * were treated as exceptions as the data was loaded from the explicit list.
	 * 
	 * @return RuleValidator
	 */
	@SuppressWarnings("unchecked")
	private RuleValidator getRuleValidator(SessionUserProfileDTO user) {
		if (logger.isDebugEnabled()) 
				logger.debug("Initialising rule validator for - userId="
					+user.getUacfId());
			
		Collection<ExplicitAgreementDTO> explicitAgreementCollection = null;
		if (user.explicitAgreementMap != null)
			explicitAgreementCollection = user.explicitAgreementMap.values();
		
		/**
		 * Attach explicit agreement data for rules that require it
		 */
		Map<RuleType, RuleDTO> ruleListMap= user.ruleListMap;
		for (RuleType ruleType : ruleListMap.keySet()) {
			RuleDTO ruleDTO = ruleListMap.get(ruleType);
			if (logger.isDebugEnabled()) 
				logger.debug("  -- ruleType="+ruleType 
						+ "  ,isUsingAgreementRuleData="
						+ ruleType.isUsingRuleData());
			
			if (ruleType.getApplicableTo() == RuleType.ApplicableTo.HIERARCHY) {
				List<Long> hierarchyList = new ArrayList<Long>();
				for (SessionUserHierarchyNodeDTO dto : user.hierarchicalNodeAccessList) {
					hierarchyList.add(dto.getPartyOid());
				}
				if (logger.isDebugEnabled()) 
					logger.debug("  -- setting hierarchy rule="+ruleType 
							+ "  ,isUsingAgreementRuleData="
							+ ruleType.isUsingRuleData()
							+ " ,list="+hierarchyList);
				ruleDTO.getRuleDataDTO().setRuleDataValueList(hierarchyList);
				continue;
			}
			
			if(ruleType == RuleType.VIEW_ALL_PARTY_RULE || ruleType == RuleType.VIEW_ALL_AGREEMENTS_RULE
					|| ruleType == RuleType.MODIFY_ALL_AGREEMENTS_RULE || ruleType == RuleType.MODIFY_ALL_PARTY_RULE){
				 //all rules here are negative rules with no data
				ruleDTO.getRuleDataDTO().setRuleDataValueList(Collections.EMPTY_LIST);
				continue;
			}
			
			if(ruleType == RuleType.MODIFY_OWN_PARTY_RULE || ruleType == RuleType.MODIFY_ALL_PARTY_EXCEPT_OWN){
				ArrayList<Long> ownPartyList = new ArrayList<Long>(1);
				ownPartyList.add(user.getPartyOid());
				ruleDTO.getRuleDataDTO().setRuleDataValueList(ownPartyList);					
				continue;
			}			
			
			if (ruleType.isUsingRuleData()==false) {
				// Currently only dealing with explicit agreement rules and thier party lists, 
				//   reject others
				continue;
			}				
						
			/**
			 * start party list rules
			 */
			/*partyList contains the party list for the party rules, will contain the partyid's of all the explicit agreements that are relavent*/
			if (ruleType == RuleType.MODIFY_PARTY_LIST_RULE || ruleType == RuleType.VIEW_PARTY_LIST_RULE) {
				HashSet<Long> partyList = new HashSet<Long>();
			
				if (explicitAgreementCollection!=null) {
					for (ExplicitAgreementDTO agreementDTO : explicitAgreementCollection) {							
						//get the party from the agreement using fast lane query
						try {
							partyList.add(getPartyIDFromAgreementNumber(agreementDTO.getAgreementOID()));
						} catch (DataNotFoundException e) {
							//dont want user to continue
							throw new CommunicationException("Party data could not be found",e);
						}
					}
				}
				//we by default add the party to his list
				partyList.add(user.getPartyOid());				
				
				//if he is an intermediary who has linked other parties in seculink then that party should be in his list so he can view them
				//we will only add them to the view role as this party is not technically in his list
				if(ruleType == RuleType.VIEW_PARTY_LIST_RULE){

					
				}
				
				ruleDTO.getRuleDataDTO().setRuleDataValueList(new ArrayList<Long>(partyList));	
				continue;
			}	
			//end party list rules
			
			/**
			 * Agreement list rules
			 */
			List<Long> agreementList = new ArrayList<Long>();
			
			//put own agreements on own agreement rules
			if (ruleType == RuleType.MODIFY_OWN_AGREEMENT_RULE 
					|| ruleType == RuleType.MODIFY_ALL_AGREEMENTS_EXCEPT_OWN) {
				agreementList.addAll(user.ownAgreementList);	
			}	
					
			if (ruleType == RuleType.MODIFY_AGREEMENT_LIST_RULE || ruleType == RuleType.VIEW_AGREEMENT_LIST_RULE) {
				if (logger.isDebugEnabled()) 
					logger.debug("  -- Adding reports to agreement numbers   size="
							+user.reportsToAgreementList.size());
				for (ExplicitAgreementDTO agreementDTO : explicitAgreementCollection) {
					agreementList.add(agreementDTO.getAgreementOID());
				}
				// Adding reports to agreements
				agreementList.addAll(user.reportsToAgreementList);
				agreementList.addAll(user.ownAgreementList);
				agreementList.addAll(user.servicesAgreementList);
				
				//PA will have agreements where they are PA in list
//				we also check if he is a PA to an intermediary, if so we add the intermediaries party id to the list
				
			}	
			
			ruleDTO.getRuleDataDTO().setRuleDataValueList(agreementList);
			//end agreement rules
			
			if (logger.isDebugEnabled()) 
				logger.debug("  -- ruleData size="+ruleDTO.getRuleDataDTO().getRuleDataValueList().size());
		}
		return new RuleValidator(ruleListMap);
	}	
	
	/**
	 * Quick fast lane query to get the partyid for the agreement number
	 * @return
	 * @throws DataNotFoundException 
	 * 
	 * TODO jzb0608 - remove or fix?  We need to add a faster query as this retrieves a party
	 * 		with reg details.
	 */
	private long getPartyIDFromAgreementNumber(long agreementNumber) throws DataNotFoundException{
		return 1;
	}

	/**
	 * Will check if a user exists on tam with the given uacfid
	 * <br/>Throws a InvalidUserIdException if the user does not exist on TAM
	 * 
	 * @param uacfID
	 * @return
	 * @throws InvalidUserIdException
	 */
	public List<String> getTamRolesForUacfId(String uacfID) throws InvalidUserIdException , CommunicationException{
//		String userName = null;
//		try {
//			InitialContext ctx = new InitialContext();
//			UserRegistry userRegistry = (UserRegistry) ctx.lookup("UserRegistry");
//			userName = userRegistry.getUserDisplayName("cn="
//					+ uacfID + ",O=LIBERTY");
//			List<String> list = userRegistry.getGroupsForUser("cn="+uacfID+ ",O=LIBERTY");
//			List<String> newList = new ArrayList<String>();
//			if (list!=null && list.size()>0) {
//				for (String s : list) {
//					newList.add(extractRoleNameFromTamRole(s));
//				}
//			}
//			return newList;
//
//		} catch (NamingException e) {
//			throw new CommunicationException(e);
//		} catch (EntryNotFoundException e) {
//			throw new InvalidUserIdException("User("+uacfID+") not found on TAM");
//		} catch (CustomRegistryException e) {
//			throw new CommunicationException(e);
//		} catch (RemoteException e) {
//			throw new CommunicationException(e);
//		}
		return Collections.emptyList();
	}
	
	/**
	 * Will check if a user exists on tam with the given uacfid
	 * <br/>Throws a InvalidUserIdException if the user does not exist on TAM
	 * 
	 * @param uacfID
	 * @return
	 * @throws InvalidUserIdException
	 */
	public String userExistsOnTam(String uacfID) throws InvalidUserIdException , CommunicationException{
//		String userName = null;
//		try {
//			InitialContext ctx = new InitialContext();
//			UserRegistry userRegistry = (UserRegistry) ctx.lookup("UserRegistry");
//			userName = userRegistry.getUserDisplayName("cn="
//					+ uacfID + ",O=LIBERTY");
//		} catch (NamingException e) {
//			throw new CommunicationException(e);
//		} catch (EntryNotFoundException e) {
//			throw new InvalidUserIdException("User("+uacfID+") not found on TAM");
//		} catch (CustomRegistryException e) {
//			throw new CommunicationException(e);
//		} catch (RemoteException e) {
//			throw new CommunicationException(e);
//		}
//		if(userName == null || userName.equals("")){
//			throw new InvalidUserIdException("User("+uacfID+") not found on TAM");
//		}
//		return userName;
		return uacfID;
	}
	
	/**
	 * Will return true if the current user can modify the party with oid sent in otherwise false
	 * @param partyoid
	 * @param currentUser
	 * @return
	 */
	public boolean canUserModifyPartyDetails(Long partyoid, ISessionUserProfile currentUser){		
		RuleType[] ruleTypes = RuleType.getPartyRules(ApplicableEditState.MODIFY);
		SessionUserProfileDTO currentUserObj = ((SessionUserProfileDTO)currentUser);
		if(!hasOneRuleOfTypes(currentUserObj,ruleTypes)){
			return false;
		}
		//check for the modify party with agreement rule, we then validate this separatly as it has too much data to keep in one list
		for(RuleType rule : currentUserObj.ruleListMap.keySet()){

		}			
		return ((SessionUserProfileDTO)currentUser).ruleValidator.validate(partyoid,ruleTypes);		
	}	 
	
	/**
	 * Will return true if the current user can view the party with oid sent in otherwise false
	 * @param partyoid
	 * @param currentUser
	 * @return
	 */
	public boolean canUserViewPartyDetails(Long partyoid, ISessionUserProfile currentUser){	
		long startTime = System.currentTimeMillis();
		try {
			//some additional rules, one being if its your own party you can view it
			if(partyoid == currentUser.getPartyOid()){
				return true;
			}
			RuleType[] ruleTypes = RuleType.getPartyRules(ApplicableEditState.VIEW);
			SessionUserProfileDTO currentUserObj = ((SessionUserProfileDTO)currentUser);
			if(!hasOneRuleOfTypes(currentUserObj,ruleTypes)){
				return false;
			}

			return currentUserObj.ruleValidator.validate(partyoid, ruleTypes);	
		} finally {
			if (logger.isDebugEnabled())
				logger.debug("canUserViewPartyDetails  time="+(System.currentTimeMillis()-startTime));
		}
	}
	
	/**
	 *  Checks that the user has at least one of the rule types sent through in his list
	 * @return
	 */
	private boolean hasOneRuleOfTypes(SessionUserProfileDTO currentUserObj,RuleType[] ruleTypes){		
		//check that user has at least one view party rule
		if(currentUserObj.ruleListMap == null || currentUserObj.ruleListMap.size() == 0){
			//users must have one rule to do this function
			return false;
		}		
		boolean continu = false;
		for(RuleType ruleType : ruleTypes){
			if(currentUserObj.ruleListMap.get(ruleType) != null){
				continu = true;
				break;
			}
		}		
		return continu;
	}
	
	/**
	 * Will return true if the current user can modify the agreement with oid sent in otherwise false.
	 * 
	 * @param agreementNumber
	 * @param hasHomePartyOid
	 * @param currentUser
	 * @returngetHierarchicalNodeAccessList
	 */
	public boolean canUserModifyAgreementDetails(Long agreementNumber, 
			Long hasHomePartyOid,
			ISessionUserProfile currentUser){
		
		// Check agreement rules
				RuleType[] ruleTypes = RuleType.getAgreementRules(ApplicableEditState.MODIFY);
				SessionUserProfileDTO currentUserObj = ((SessionUserProfileDTO)currentUser);
				boolean value = false;
				if(hasOneRuleOfTypes(currentUserObj,ruleTypes)){
					value = ((SessionUserProfileDTO)currentUser).ruleValidator.validate(agreementNumber, ruleTypes);
				}
						
				// Check the hierarchy rules
				if (!value && hasHomePartyOid != null &&  currentUser.hasHierarchicalAccess()) {
					ruleTypes = RuleType.getRules(RuleType.ApplicableTo.HIERARCHY, ApplicableEditState.MODIFY).toArray(new RuleType[] {});
					if (!hasOneRuleOfTypes(currentUserObj, ruleTypes)) {
						return false;
					}
					return ((SessionUserProfileDTO)currentUser).ruleValidator.validate(hasHomePartyOid, ruleTypes);
				}
				return value;
		
	}
	
	/**
	 * Will return true if the current user can view the agreement with oid sent in otherwise false.
	 * 
	 * @param agreementNumber
	 * @param hasHomePartyOid
	 * @param currentUser
	 * @return
	 */
	public boolean canUserViewAgreementDetails(Long agreementNumber, Long hasHomePartyOid, 
			ISessionUserProfile currentUser){	
		if (logger.isDebugEnabled()) {
			logger.debug("canUserViewAgreement " +agreementNumber + " ,hasHomePartyOid="+hasHomePartyOid);
		}
		long startTime = System.currentTimeMillis();
		try {
			// For performance (all users can view their own agreements)
			if (((SessionUserProfileDTO)currentUser).ownAgreementList.contains(agreementNumber)) {
				return true;
			}
			
			// Check the agreement view rules
			RuleType[] ruleTypes = RuleType.getAgreementRules(ApplicableEditState.VIEW);
			SessionUserProfileDTO currentUserObj = ((SessionUserProfileDTO)currentUser);
			boolean value = false;
			if(hasOneRuleOfTypes(currentUserObj,ruleTypes)){
			  value = ((SessionUserProfileDTO)currentUser).ruleValidator.validate(agreementNumber, ruleTypes);
			}
			
			
			// Check the hierarchy rules
			if (!value && hasHomePartyOid != null &&  currentUser.hasHierarchicalAccess()) {
				ruleTypes = RuleType.getRules(RuleType.ApplicableTo.HIERARCHY, ApplicableEditState.VIEW).toArray(new RuleType[] {});
			
				if (!hasOneRuleOfTypes(currentUserObj, ruleTypes)) {
					if (logger.isDebugEnabled()) {
						logger.debug("  -- does not have one of rule types ="+ Arrays.asList(ruleTypes));
					}
					return false;
				}
				if (logger.isDebugEnabled()) {
					logger.debug("  -- has one of rule types ="+ Arrays.asList(ruleTypes));
				}
				return ((SessionUserProfileDTO)currentUser).ruleValidator.validate(hasHomePartyOid, ruleTypes);
			}
			return value;
		} finally {
			if (logger.isDebugEnabled())
				logger.debug("canUserViewAgreementDetails  time="+(System.currentTimeMillis()-startTime));
		}
	}
	
	/**
	 * Will return true if the user is allowed to view the passed hierarchy node with party
	 * oid.
	 * @param hasHomePartyOid
	 * @param currentUser
	 * @return
	 */
	public boolean canUserViewHierarchyNode(Long hasHomePartyOid, ISessionUserProfile currentUser) {
		
		
		if (((SessionUserProfileDTO)currentUser).ruleListMap.containsKey(RuleType.VIEW_ALL_AGREEMENTS_RULE)) {
			return true;
		}
		
		// Check the hierarchy view rules
		if (hasHomePartyOid != null && currentUser.hasHierarchicalAccess()) {
			RuleType[] ruleTypes = RuleType.getRules(RuleType.ApplicableTo.HIERARCHY, ApplicableEditState.VIEW).toArray(new RuleType[] {});
			
			if (!hasOneRuleOfTypes(((SessionUserProfileDTO)currentUser), ruleTypes)) {
				return false;
			}
			return ((SessionUserProfileDTO)currentUser).ruleValidator.validate(hasHomePartyOid, ruleTypes);
		}
		return false;
	}
	
	/*
	 * (non-Javadoc)
	 * @see za.co.liberty.business.security.ISecurityManagement#canUserTransferMaxPoolBalance(za.co.liberty.dto.userprofiles.ISessionUserProfile)
	 */
	public boolean canUserTransferMaxPoolBalance(ISessionUserProfile currentUser) {

		if (((SessionUserProfileDTO) currentUser).ruleListMap.containsKey(RuleType.TRANSFER_MAX_POOL_BALANCE)) {
			return true;
		}

		return false;
	}
	
	/**
	 * Get SecurityManagement from the container
	 * @return
	 * @throws NamingException 
	 */
	public static ISecurityManagement getInstance() throws NamingException{
		return (ISecurityManagement) new InitialContext().lookup("ejblocal:"+ISecurityManagement.class.getName());
	}
	
	 /* Will go through the list and remove the conflicting rules<br/>
	  * Only currently applies to party/agreement view and modification rules
	  * @return
	  */
	private void removeConflictingRulesForUser(Map<RuleType, RuleDTO> ruleListMap){		
		RuleDTO mostRestrictivePartyViewRule = null;		
		RuleDTO mostRestrictiveAgreementViewRule = null;
		RuleDTO mostRestrictivePartyModificationRule = null;
		RuleDTO mostRestrictiveAgreementModificationRule = null;		
		
		ArrayList<RuleType> keys = new ArrayList<RuleType>(ruleListMap.keySet());
		for(RuleType ruleType : keys){
			RuleDTO dto = ruleListMap.get(ruleType);
			if(ruleType.getApplicableTo() == RuleType.ApplicableTo.PARTY){
				//party rule
				if(ruleType.getApplicableEditState() == ApplicableEditState.VIEW){
					//party view rule
					if(mostRestrictivePartyViewRule == null){
						mostRestrictivePartyViewRule = dto;
					}else{
						//check restriction
						RuleType currentRestricitiveType = RuleType.getRuleTypeWithDatabaseName(mostRestrictivePartyViewRule.getRuleName());
						//first we check if it one user and one role, if so then user is most restricitve
						if(dto.getRuleSource() != RuleSource.USER && mostRestrictivePartyViewRule.getRuleSource() == RuleSource.USER){
//							user rules are default
							ruleListMap.remove(ruleType);
						}else if(dto.getRuleSource() == RuleSource.USER &&  mostRestrictivePartyViewRule.getRuleSource() != RuleSource.USER){
//							user rules are default
							ruleListMap.remove(currentRestricitiveType);
							mostRestrictivePartyViewRule = dto;						
						}else{						
							RuleType mostRestrictive = RuleType.getMoreRestrictiveRule(ruleType,currentRestricitiveType);
							if(ruleType == mostRestrictive){
								//current type is the most restrictive so change
								ruleListMap.remove(currentRestricitiveType);
								mostRestrictivePartyViewRule = dto;							
							}else{
								//dto is still most restrictive so remove current
								ruleListMap.remove(ruleType);
							}	
						}
					}		
				}else if(ruleType.getApplicableEditState() == ApplicableEditState.MODIFY){
					//party modification rule
					if(mostRestrictivePartyModificationRule == null){
						mostRestrictivePartyModificationRule = dto;
					}else{						
						//check restriction
						RuleType currentRestricitiveType = RuleType.getRuleTypeWithDatabaseName(mostRestrictivePartyModificationRule.getRuleName());						
						//first we check if it one user and one role, if so then user is most restricitve
						if(dto.getRuleSource() != RuleSource.USER && mostRestrictivePartyModificationRule.getRuleSource() == RuleSource.USER){
//							user rules are default
							ruleListMap.remove(ruleType);
						}else if(dto.getRuleSource() == RuleSource.USER &&  mostRestrictivePartyModificationRule.getRuleSource() != RuleSource.USER){
//							user rules are default
							ruleListMap.remove(currentRestricitiveType);
							mostRestrictivePartyModificationRule = dto;						
						}else{
							RuleType mostRestrictive = RuleType.getMoreRestrictiveRule(ruleType,currentRestricitiveType);
							if(ruleType == mostRestrictive){
								//current type is the most restrictive so change
								ruleListMap.remove(currentRestricitiveType);
								mostRestrictivePartyModificationRule = dto;
							}else{
								//dto is still most restrictive so remove current
								ruleListMap.remove(ruleType);
							}
						}
					}	
				}				
			}else if(ruleType.getApplicableTo() == RuleType.ApplicableTo.AGREEMENT){
				//agreement rule
				if(ruleType.getApplicableEditState() == ApplicableEditState.VIEW){
					//agreement view rule
					if(mostRestrictiveAgreementViewRule == null){
						mostRestrictiveAgreementViewRule = dto;
					}else{
//						check restriction
						RuleType currentRestricitiveType = RuleType.getRuleTypeWithDatabaseName(mostRestrictiveAgreementViewRule.getRuleName());
						//first we check if it one user and one role, if so then user is most restricitve
						if(dto.getRuleSource() != RuleSource.USER && mostRestrictiveAgreementViewRule.getRuleSource() == RuleSource.USER){
//							user rules are default
							ruleListMap.remove(ruleType);
						}else if(dto.getRuleSource() == RuleSource.USER &&  mostRestrictiveAgreementViewRule.getRuleSource() != RuleSource.USER){
//							user rules are default
							ruleListMap.remove(currentRestricitiveType);
							mostRestrictiveAgreementViewRule = dto;						
						}else{
							RuleType mostRestrictive = RuleType.getMoreRestrictiveRule(ruleType,currentRestricitiveType);
							if(ruleType == mostRestrictive){
								//current type is the most restrictive so change
								ruleListMap.remove(currentRestricitiveType);
								mostRestrictiveAgreementViewRule = dto;
							}else{
								//dto is still most restrictive so remove current
								ruleListMap.remove(ruleType);
							}			
						}
					}	
				}else if(ruleType.getApplicableEditState() == ApplicableEditState.MODIFY){
					//agreement modification rule
					if(mostRestrictiveAgreementModificationRule == null){
						mostRestrictiveAgreementModificationRule = dto;
					}else{
//						check restriction
						RuleType currentRestricitiveType = RuleType.getRuleTypeWithDatabaseName(mostRestrictiveAgreementModificationRule.getRuleName());
						//first we check if it one user and one role, if so then user is most restricitve
						if(dto.getRuleSource() != RuleSource.USER && mostRestrictiveAgreementModificationRule.getRuleSource() == RuleSource.USER){
//							user rules are default
							ruleListMap.remove(ruleType);
						}else if(dto.getRuleSource() == RuleSource.USER &&  mostRestrictiveAgreementModificationRule.getRuleSource() != RuleSource.USER){
//							user rules are default
							ruleListMap.remove(currentRestricitiveType);
							mostRestrictiveAgreementModificationRule = dto;						
						}else{
							RuleType mostRestrictive = RuleType.getMoreRestrictiveRule(ruleType,currentRestricitiveType);
							if(ruleType == mostRestrictive){
								//current type is the most restrictive so change
								ruleListMap.remove(currentRestricitiveType);
								mostRestrictiveAgreementModificationRule = dto;
							}else{
								//dto is still most restrictive so remove current
								ruleListMap.remove(ruleType);
							}			
						}
					}	
				}
			}
			//else I am not bothered with this right now
		}
	}
	
	/**
	 * This method is for testing perposes only, DO NOT use for development!
	 * 
	 * This method must stay package protected!
	 * @param requestActionMap 
	 * 
	 * @return
	 */
	@SuppressWarnings("unused")
	@Deprecated 
	protected ISessionUserProfile createTestUserForTesting(String uacfId,long partyOid,
			Map<Long, ExplicitAgreementDTO> explicitAgreementMap,
			List<Long> ownAgreementsList,
			Map<RuleType,RuleDTO> ruleListMap,
			List<MenuItemDTO> menuItemList,List<Long> reportsToAgreementList,
			RuleValidator ruleValidator, 
			Map<RequestKindType, AllowableRequestActionDTO> requestActionMap){
		
		SessionUserProfileDTO user = new SessionUserProfileDTO();			
		user.uacfId = uacfId;
		user.partyOid = partyOid;
		user.explicitAgreementMap = explicitAgreementMap;
		user.ownAgreementList = ownAgreementsList;
		user.ruleListMap = ruleListMap;
		user.menuItemList = menuItemList;
		user.reportsToAgreementList = reportsToAgreementList;
		user.ruleValidator = ruleValidator;
		user.hierarchyNodeRoleList = new ArrayList<SessionUserHierarchyRoleDTO>();
		user.requestActionMap = requestActionMap;
		return user;
	} 
	
	/**
	 * Return the menu items that are linked to the specified uacfId. Includes
	 * the menu items linked to the users roles.
	 * If a role is disabled or the menu item is disabled it is not returned in the list<br/>
	 * If a menu item exists more than once for the user, linked to two different roles etc, then the least restrictive menu item is returned<br/>
	 * If a menu item has been configured for a user specifically then it will override any role
	 * @param securityID
	 * @return
	 * @throws InvalidUserIdException
	 */
	public List<MenuItemDTO> getMenuItems(PartyProfileEntity profileEntity)
			throws InvalidUserIdException {
	
		Vector<MenuItemDTO> menuItemsList = new Vector<MenuItemDTO>();
		
		for (int i = 0; i < FIXED_MENU_ITEMS.length; ++i) {
			MenuItemDTO d = new MenuItemDTO();
			d.setMenuItemID(Long.getLong(FIXED_MENU_ITEMS[i][0]));
			d.setDbKey(d.getMenuItemID());
			d.setMenuItemDescription(FIXED_MENU_ITEMS[i][2]);
			d.setMenuItemName(FIXED_MENU_ITEMS[i][1]);
			d.setAddAccess(true);
			d.setModifyAccess(true);
			d.setEnabled(true);
			d.setImplClazz(FIXED_MENU_ITEMS[i][3]);
			menuItemsList.add(d);
			System.out.println ("Added menu " + d);
		}

		
		long count = 2000;
		
//		// Add all items in array
//		for (String str : FIXED_PANEL_ARRAY) {	
//			count++;
//		// Party tab specifics
//			// This one is the base security for some of those panels
//			MenuItemDTO d = new MenuItemDTO();
//			d.setMenuItemID(count);
//			d.setDbKey(d.getMenuItemID());
//			d.setMenuItemDescription("Base panel");
//			d.setMenuItemName("auto");
//			d.setAddAccess(true);
//			d.setModifyAccess(true);
//			d.setEnabled(true);
//			d.setImplClazz(str);
//			menuItemsList.add(d);	
//		
//		}
				
		for (int i = 0; i < FIXED_PANEL_ARRAY.length; ++i) {
			MenuItemDTO d = new MenuItemDTO();
			d.setMenuItemID(Long.getLong(FIXED_MENU_ITEMS[i][0]));
			d.setDbKey(d.getMenuItemID());
			d.setMenuItemDescription(FIXED_MENU_ITEMS[i][2]);
			d.setMenuItemName(FIXED_MENU_ITEMS[i][1]);
			d.setAddAccess(true);
			d.setModifyAccess(true);
			d.setEnabled(true);
			d.setPanel(true);
			d.setImplClazz(FIXED_MENU_ITEMS[i][3]);
			menuItemsList.add(d);
			System.out.println ("Added menu " + d);
		}
		
		return menuItemsList;
	}

	
	/**
	 * Takes two menu items and merges their restriciton into One, the first one will be the one returned 
	 * @param one
	 * @param two
	 */
	private MenuItemDTO mergeMenuItemToLeastRestrictive(MenuItemDTO one, MenuItemDTO two){
		if(one != null && two != null){
			if(!one.isAddAccess() && two.isAddAccess()){
				one.setAddAccess(true);
			}
			if(!one.isDeleteAccess() && two.isDeleteAccess()){
				one.setDeleteAccess(true);
			}
			if(!one.isModifyAccess() && two.isModifyAccess()){
				one.setModifyAccess(true);
			}			
			return one;
		}
		if(one != null){
			return one;
		}else{
			return two;
		}		
	}

	/**
	 * Return the rules that are linked to the specified uacfId. Includes the
	 * rules linked to the users roles.
	 * 
	 * @param securityID
	 * @return
	 * @throws InvalidUserIdException
	 * @throws DataNotFoundException 
	 */
	public ArrayList<RuleDTO> getRules(PartyProfileEntity profileEntity)
			throws InvalidUserIdException {
				
		/* Initialise */
		ArrayList<RuleDTO> ruleDTOList = new ArrayList<RuleDTO>();
//		List<PartyProfileRulesEntity> profileRuleList = profileEntity.getRuleList();
//		List<PartyProfileRolesEntity> profileRoleList = profileEntity.getRoleList();
//		HashMap<String, RunnableRuleDTO> userRulesMap = new HashMap<String, RunnableRuleDTO>();	
//
//		/* Process the list of Rules linked to a Profile */
//		for (PartyProfileRulesEntity profilePartyRule : profileRuleList) {
//			RuleEntity ruleEntity = profilePartyRule.getRuleEntity();				
//			RunnableRuleDTO ruleDTO = new RunnableRuleDTO();
//			/* Is this the first time processing this rule? */
//			populateRuleDTO(ruleDTO, ruleEntity, profilePartyRule);	
//	
//			ruleDTO.setRuleSource(RuleSource.USER);
//			userRulesMap.put(ruleDTO.getRuleName(), ruleDTO);				
//			ruleDTOList.add(ruleDTO);
//		}
//
//		
//		/* Process the roles linked to a profile */
//		if (profileRoleList != null) {
//
//			for (PartyProfileRolesEntity profileRolesParty : profileRoleList) {
//
//				if (logger.isDebugEnabled())
//					logger.debug("Checking rules linked to role " + profileRolesParty.getRoleEntity().getName()
//							+ "  - rules is null = " + (profileRolesParty.getRoleEntity().getRuleList()==null) );
//				
//				List<RoleRulesEntity> profileRoleRulesList = profileRolesParty.getRoleEntity().getRuleList();
//				
//				if (profileRoleRulesList==null) {
//					logger.warn("Profile id " + profileRolesParty.getOid() + " has role " 
//							+ profileRolesParty.getRoleEntity().getName()
//							+ " has null rules.");
//					continue;
//				}
//					
//				/* Process all linked rules */
//				for (RoleRulesEntity profileRolesRule : profileRoleRulesList) {
//					
//					if (profileRolesRule.getRoleEntity().isEnabled()==false) {
//						continue;
//					}					
//					RuleEntity ruleEntity = profileRolesRule.getRuleEntity();			
//					if(userRulesMap.get(ruleEntity.getName()) != null){
//						// User rules have a higher security, so ignore.
//						continue;
//					}
//					RunnableRuleDTO ruleDTO = new RunnableRuleDTO();
//					populateRuleDTO(ruleDTO, ruleEntity, profileRolesRule);	
//					ruleDTO.setRuleSource(RuleSource.ROLE);
//					ruleDTOList.add(ruleDTO);
//				}
//			}
//
//		}
//		if (ruleDTOList.size() < 1)
//			return null;
		return ruleDTOList;
	}
	
	/**
	 * Populate the RunnableRuleDTO from the relevant entities.
	 * 
	 * @param ruleDTO
	 * @param ruleEntity
	 * @param ruleLinkEntity
	 */
	private void populateRuleDTO(RunnableRuleDTO ruleDTO, RuleEntity ruleEntity, 
			AbstractRuleLinkEntity ruleLinkEntity) {
		
		// Set rule data
		ruleDTO.setEnabled(ruleEntity.isEnabled());
		ruleDTO.setRuleName(ruleEntity.getName());
		ruleDTO.setRuleDescription(ruleEntity.getDescription());
		ruleDTO.setRuleID(ruleEntity.getOid());
		ruleDTO.setHasRuleData(ruleEntity.isHasRuleData());

		// Set partyProfileRule data
		ruleDTO.setDbKey(ruleLinkEntity.getOid());
		ruleDTO.setCreatedBy(ruleLinkEntity.getCreatedBy());
		ruleDTO.setCreationTime(ruleLinkEntity.getCreationTime());
		ruleDTO.setReplacementTime(ruleLinkEntity.getReplacementTime());
		ruleDTO.setVersion(ruleLinkEntity.getVersion());
		ruleDTO.setEffectiveFrom(ruleLinkEntity.getEffectiveFrom());
		ruleDTO.setEffectiveTo(ruleLinkEntity.getEffectiveTo());

		// Process rule data
		if (ruleEntity.isHasRuleData()) {
			RuleDataDTO ruleDataDTO = new RuleDataDTO();
			ruleDataDTO.setEnabled(ruleLinkEntity.isEnabled());
			ruleDataDTO.setRuleDataDescription(ruleLinkEntity.getRuleDataDescr());
			ruleDataDTO.setRuleDataType(ruleLinkEntity.getRuleDataType());
			ruleDataDTO.setRuleDataValue(ruleLinkEntity.getRuleDataValue());
			
			ruleDataDTO.setRuleArithmeticType(ruleLinkEntity.getArithmeticType());	
			ruleDataDTO.setDbKey(ruleLinkEntity.getOid());
			
			ruleDataDTO.setCreatedBy(ruleLinkEntity.getCreatedBy());
			ruleDataDTO.setCreationTime(ruleLinkEntity.getCreationTime());
			ruleDataDTO.setReplacementTime(ruleLinkEntity.getReplacementTime());
			ruleDataDTO.setVersion(ruleLinkEntity.getVersion());

			ruleDataDTO.setEffectiveFrom(ruleLinkEntity.getEffectiveFrom());
			ruleDataDTO.setEffectiveTo(ruleLinkEntity
						.getEffectiveTo());

			ruleDTO.setRuleDataDTO(ruleDataDTO);
		}
	}
	
	/**
	 * <p>Convert the request action items to a map of allowabe request action rules.</p>
	 * 
	 * <p>Rules attached to a profile always take precedence.  When there are rules attached 
	 * to more than one role then the least restrictive will apply.</p>
	 * 
	 * @param partyProfile
	 * @return
	 */
	private Map<RequestKindType, AllowableRequestActionDTO> getRequestActionItems(PartyProfileEntity partyProfile) {
		
		Map<RequestKindType, AllowableRequestActionDTO> actionMap = new HashMap<RequestKindType, AllowableRequestActionDTO>();
		return actionMap;
	}
	
	/**
	 * get a session profile with all access which links to
	 * @return
	 */
	public ISessionUserProfile getSystemProfile(){		
		return new SystemUserProfileDTO();
	}
	
	/**
	 * Indicates if this is a developers PC.
	 * 
	 * @return
	 */
	public static boolean isDevelopmentPc() {
		return true;
	
	}
}


