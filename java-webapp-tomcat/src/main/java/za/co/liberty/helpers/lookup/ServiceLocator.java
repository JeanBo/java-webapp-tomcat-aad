package za.co.liberty.helpers.lookup;



import javax.naming.NamingException;

import za.co.liberty.business.admin.IServerManagement;
import za.co.liberty.business.admin.ServerManagement;
import za.co.liberty.business.guicontrollers.BasePageGuiController;
import za.co.liberty.business.guicontrollers.ContextManagement;
import za.co.liberty.business.guicontrollers.IBasePageGuiController;
import za.co.liberty.business.guicontrollers.IContextManagement;
import za.co.liberty.business.guicontrollers.admin.DifferentialFactorGuiController;
import za.co.liberty.business.guicontrollers.admin.IDifferentialFactorGuiController;
import za.co.liberty.business.guicontrollers.admin.ISegmentNameGUIController;
import za.co.liberty.business.guicontrollers.admin.SegmentNameGUIController;
import za.co.liberty.business.guicontrollers.advancedPractice.AdvancedPracticeGUIController;
import za.co.liberty.business.guicontrollers.advancedPractice.IAdvancedPracticeGUIController;
import za.co.liberty.business.guicontrollers.agreement.AgreementGUIController;
import za.co.liberty.business.guicontrollers.agreement.IAgreementGUIController;
import za.co.liberty.business.guicontrollers.contactdetail.ContactDetailsGUIController;
import za.co.liberty.business.guicontrollers.contactdetail.IContactDetailsGUIController;
import za.co.liberty.business.guicontrollers.core.CoreTransferGuiController;
import za.co.liberty.business.guicontrollers.core.ICoreTransferGuiController;
import za.co.liberty.business.guicontrollers.hierarchy.HierarchyOrganogramGUIController;
import za.co.liberty.business.guicontrollers.hierarchy.IHierarchyOrganogramGUIController;
import za.co.liberty.business.guicontrollers.partymaintenance.IPartyMaintenanceController;
import za.co.liberty.business.guicontrollers.partymaintenance.PartyMaintenanceController;
import za.co.liberty.business.guicontrollers.request.IRequestEnquiryGuiController;
import za.co.liberty.business.guicontrollers.request.IRequestViewGuiController;
import za.co.liberty.business.guicontrollers.request.RequestEnquiryGuiController;
import za.co.liberty.business.guicontrollers.request.RequestViewGuiController;
import za.co.liberty.business.guicontrollers.template.FranshiseTemplateGUIController;
import za.co.liberty.business.guicontrollers.template.IFranchiseTemplateGUIController;
import za.co.liberty.business.guicontrollers.transactions.IPolicyTransactionGuiController;
import za.co.liberty.business.guicontrollers.transactions.IRequestTransactionGuiController;
import za.co.liberty.business.guicontrollers.transactions.PolicyTransactionGuiControllerBean;
import za.co.liberty.business.guicontrollers.transactions.RequestTransactionGuiController;
import za.co.liberty.business.guicontrollers.transactions.TransactionRejectsGuiController;
import za.co.liberty.business.guicontrollers.transactions.TransactionRejectsGuiControllerBean;
import za.co.liberty.business.guicontrollers.userprofiles.IMenuManagement;
import za.co.liberty.business.guicontrollers.userprofiles.IProfileRoleManagement;
import za.co.liberty.business.guicontrollers.userprofiles.IRequestCategoryGUIController;
import za.co.liberty.business.guicontrollers.userprofiles.IRuleManagement;
import za.co.liberty.business.guicontrollers.userprofiles.ITeamGUIController;
import za.co.liberty.business.guicontrollers.userprofiles.IUserAdminManagement;
import za.co.liberty.business.guicontrollers.userprofiles.MenuManagement;
import za.co.liberty.business.guicontrollers.userprofiles.ProfileRoleManagement;
import za.co.liberty.business.guicontrollers.userprofiles.RequestCategoryGUIController;
import za.co.liberty.business.guicontrollers.userprofiles.RuleManagement;
import za.co.liberty.business.guicontrollers.userprofiles.TeamGUIController;
import za.co.liberty.business.guicontrollers.userprofiles.UserAdminManagement;
import za.co.liberty.business.party.IPartyManagement;
import za.co.liberty.business.party.PartyManagement;
import za.co.liberty.business.party.validator.IPartyValidator;
import za.co.liberty.business.party.validator.PartyValidator;
import za.co.liberty.business.request.tree.IIntermediaryManager;
import za.co.liberty.business.request.tree.IntermediaryManager;
import za.co.liberty.business.security.ISecurityManagement;
import za.co.liberty.business.security.SecurityManagement;
import za.co.liberty.business.userprofiles.IProfileManagement;
import za.co.liberty.business.userprofiles.ProfileManagement;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.persistence.agreement.AgreementEntityManager;
import za.co.liberty.persistence.agreement.IAgreementEntityManager;
import za.co.liberty.persistence.party.IPartyEntityManager;
import za.co.liberty.persistence.party.PartyEntityManager;
import za.co.liberty.persistence.rating.DescriptionEntityManager;
import za.co.liberty.persistence.rating.IDescriptionEntityManager;


public class ServiceLocator {
	
	/**
	 * Convenience method to perform a JNDI lookup of an EJB
	 * using the name of the specified class as the basis 
	 * of the lookup.
	 * @param <T> The object type that will be returned
	 * @param lookupClass The class of the specified type that will be used for the lookup
	 * @return The object that has been lookup up
	 * @throws NamingException
	 */
	@SuppressWarnings("unchecked")
	public static <T> T lookupService(Class<T> lookupClass) throws NamingException {
//		InitialContext ictx = new InitialContext();
//		return (T)ictx.lookup("ejblocal:"+lookupClass.getName());
		
		if (lookupClass == ISecurityManagement.class) {
			return (T) new SecurityManagement();
		} else if (lookupClass == IBasePageGuiController.class) {
			return (T) new BasePageGuiController();
		} else if (lookupClass == IServerManagement.class) {
			return (T) new ServerManagement();
		} else if (lookupClass == IContextManagement.class) {
			return (T) new ContextManagement();
		} else if (lookupClass == ISegmentNameGUIController.class) {
			return (T) new SegmentNameGUIController();
		} else if (lookupClass == IMenuManagement.class) {
			return (T) new MenuManagement();
		} else if (lookupClass == IRuleManagement.class) {
			return (T) new RuleManagement();
		} else if (lookupClass == ITeamGUIController.class) {
			return (T) new TeamGUIController();
		} else if (lookupClass == IRequestEnquiryGuiController.class) {
			return (T) new RequestEnquiryGuiController();
		} else if (lookupClass == IProfileRoleManagement.class) {
			return (T) new ProfileRoleManagement();	
		} else if (lookupClass == IRequestTransactionGuiController.class) {
			return (T) new RequestTransactionGuiController();
		} else if (lookupClass == IPolicyTransactionGuiController.class) {
			return (T) new PolicyTransactionGuiControllerBean();
		} else if (lookupClass == IDifferentialFactorGuiController.class) {
			return (T) new DifferentialFactorGuiController();
		} else if (lookupClass == IUserAdminManagement.class) {
			return (T) new UserAdminManagement();
		} else if (lookupClass == IRequestCategoryGUIController.class) {
			return (T) new RequestCategoryGUIController();
		} else if (lookupClass == IRequestViewGuiController.class) {
			return (T) new RequestViewGuiController();
		} else if (lookupClass == IHierarchyOrganogramGUIController.class) {
			return (T) new HierarchyOrganogramGUIController();
		} else if (lookupClass == IIntermediaryManager.class) {
			return (T) new IntermediaryManager();
		} else if (lookupClass == IPartyMaintenanceController.class) {
			return (T) new PartyMaintenanceController();
		} else if (lookupClass == IPartyManagement.class) {
			return (T) new PartyManagement();
		} else if (lookupClass == IProfileManagement.class) {
			return (T) new ProfileManagement();
		} else if (lookupClass == IDescriptionEntityManager.class) {
			return (T) new DescriptionEntityManager();
		} else if (lookupClass == IAgreementGUIController.class) {
			return (T) new AgreementGUIController();
		} else if (lookupClass == IAdvancedPracticeGUIController.class) {
			return (T) new AdvancedPracticeGUIController();
		} else if (lookupClass == IAgreementEntityManager.class) {
			return (T) new AgreementEntityManager();
		} else if (lookupClass == IPartyEntityManager.class) {
			return (T) new PartyEntityManager();
		} else if (lookupClass == IFranchiseTemplateGUIController.class) {
			return (T) new FranshiseTemplateGUIController();
		} else if (lookupClass == IPartyValidator.class) {
			return (T) new PartyValidator();
		} else if (lookupClass == IContactDetailsGUIController.class) {
			return (T) new ContactDetailsGUIController();
		} else if (lookupClass == ICoreTransferGuiController.class) {
			return (T) new CoreTransferGuiController();
		} else if (lookupClass == TransactionRejectsGuiController.class) {
			return (T) new TransactionRejectsGuiControllerBean();
		}
		
			
		throw new CommunicationException("Unsupported class : " + lookupClass);
	}
	

}
