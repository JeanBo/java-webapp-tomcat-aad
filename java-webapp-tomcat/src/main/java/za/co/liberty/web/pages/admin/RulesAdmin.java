package za.co.liberty.web.pages.admin;

import java.util.List;

import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.Panel;

import za.co.liberty.business.guicontrollers.userprofiles.IRuleManagement;
import za.co.liberty.dto.userprofiles.RuleDTO;
import za.co.liberty.exceptions.UnResolvableException;
import za.co.liberty.exceptions.data.DataNotFoundException;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.helpers.lookup.ServiceLocator;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.models.PagePanelInfoObject;
import za.co.liberty.web.pages.MaintenanceBasePage;
import za.co.liberty.web.pages.admin.models.RulesModel;
import za.co.liberty.web.pages.panels.DefaultMaintenanceSelectionPanel;
import za.co.liberty.web.system.EJBReferences;
import za.co.liberty.web.system.SRSAuthWebSession;

/**
 * Controller page used to administrate / maintain Rules
 * 
 * @author JZB0608 - 05 May 2008
 *
 */
public class RulesAdmin extends MaintenanceBasePage<Object> {

	private static final long serialVersionUID = 1L;
	private RulesModel pageModel;
	private boolean ruleHasRuleData = false;
	
	/**
	 * Default constructor
	 *
	 */
	public RulesAdmin() {
		super(null);
	}

	/**
	 * Default constructor that initialises the page
	 * with the given object
	 * 
	 * @param dto
	 */
	public RulesAdmin(RuleDTO dto) {
		super(dto);
		ruleHasRuleData = dto.isHasRuleData();
	}
	
	@Override
	public Panel createContainerPanel() {
		System.out.println("RuleAdmin:createContainerPanel()--------------------------------");
		Panel panel;
		if (pageModel.getSelectedItem() == null) {
			System.out.println("RuleAdmin:createContainerPanel()--------------------------------1");
			panel = new EmptyPanel(CONTAINER_PANEL_NAME);
		} else {
			System.out.println("RuleAdmin:createContainerPanel()--------------------------------2");
			RuleDTO dto = pageModel.getSelectedItem();
			if (getEditState() != EditStateType.ADD && dto != null) {
				// Refresh from db
				try {
					System.out.println("RuleAdmin:createContainerPanel()-------dto.getRuleID()------"+dto.getRuleID());
					dto = getSessionBean().findRule(dto.getRuleID());
				} catch (DataNotFoundException e) {
					// this should really not happen, but if it does throw a runtime
					throw new RuntimeException(
							"Unable to find the selected rule entity.",e);
				}
				pageModel.setSelectedItem(dto);
			}
			panel = new RulesAdminPanel(CONTAINER_PANEL_NAME, pageModel, getEditState());
		}
		panel.setOutputMarkupId(true);
		return panel;
	}

	@Override
	public Button[] createNavigationalButtons() {
		return new Button[] {createSaveButton("button1"), 
				createCancelButton("button2")};
	}

	@Override
	public void doSave_onSubmit() {
		RuleDTO newDto = null;
				
		/* Save to db */
		RuleDTO dto = pageModel.getSelectedItem();
		if(validate(dto)){	
			System.out.println("doSave_onSubmit----------dto.getRuleID()--------------"+dto.getRuleID());
			if (dto.getRuleID()==null) {
				newDto = getSessionBean().createRule(dto, SRSAuthWebSession.get().getSessionUser());
			} else {
				System.out.println("doSave_onSubmit-------------else");
				newDto =getSessionBean().updateRule(dto);
			}
	
			invalidatePage();
			
			this.info("Record was saved successfully");
			setResponsePage(new RulesAdmin(newDto));
		}else{
			setResponsePage(this);
		}
	}
	
	/**
	 * Validate the rule
	 * Returns true if valid
	 * @param dto
	 */
	public boolean validate(RuleDTO dto){
		//we check that no roles/PartyProfiles have this rule linked
		//if we has rule data is true, this will mess up the running of the rules
		if(dto.isHasRuleData() && !ruleHasRuleData&&dto.getRuleID()!=null){
			if(this.getSessionBean().ruleIsLinkedToRolesOrProfiles(dto.getRuleID())){
				error("The rule you are editing has already been linked to Profiles/Roles, You can not change the has rule data to true if it has been linked already");
				return false;
			}			
		}	
		return true;
	}
	
	@Override
	public Panel createSelectionPanel() {
		return new DefaultMaintenanceSelectionPanel(SELECTION_PANEL_NAME,
				"Rule Name:", pageModel, this, 
				selectionForm,RuleDTO.class,"ruleDescription","ruleID");		
	}

	@Override
	public Object initialisePageModel(Object object,Object extraObject) {
		RulesModel obj = new RulesModel();
		obj.setSelectionList(getSessionBean().findAllRules());
		obj.setArithmeticTypes(this.getSessionBean().getArithmeticTypess());
		obj.setDataTypes(this.getSessionBean().findAllRuleDataType());
		obj.setSelectedItem((RuleDTO) object);
		pageModel = obj;
		return pageModel;
	}
	
	@Override
	public String getPageName() {
		return "Rule Administration";
	}

	@Override
	protected Panel getContextPanel() {
		/* Does not require a panel */
		return new EmptyPanel(CONTEXT_PANEL_NAME);
	}
	
	/**
	 * Get an instance of the managed session bean
	 * 
	 * @return
	 */
	private IRuleManagement getSessionBean() {
		try {
			//MSK:Change,For POC commenting below logic and intro Servicelocator inthis place
			/*
			 * return (IRuleManagement) SRSAuthWebSession.get().getEJBReference(
			 * EJBReferences.RULE_MANAGEMENT);
			 */
			return ServiceLocator.lookupService(IRuleManagement.class);
		} /*
			 * catch (CommunicationException e) { Logger.getLogger(this.getClass()).error(
			 * "Unable to initialise session bean for page", e); this.error(e.getMessage());
			 * throw e; } catch (UnResolvableException e) {
			 * Logger.getLogger(this.getClass()).error(
			 * "Unable to initialise session bean for page", e); this.error(e.getMessage());
			 * throw e; }
			 */ catch (NamingException e) {
			// TODO Auto-generated catch block
			throw new CommunicationException(e);
		}
	}

	@Override
	public List<PagePanelInfoObject> getPagePanelsInfo() {		
		return null;
	}

	@Override
	public boolean hasAddAccess(Object callingObject) {
		return false;
	}

	@Override
	public boolean hasModifyAccess(Object callingObject) {
		return false;
	}
	
	
	
	
}
