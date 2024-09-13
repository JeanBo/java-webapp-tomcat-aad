package za.co.liberty.web.pages.admin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.Panel;

import za.co.liberty.business.guicontrollers.userprofiles.IProfileRoleManagement;
import za.co.liberty.dto.userprofiles.AllowableRequestActionDTO;
import za.co.liberty.dto.userprofiles.MenuItemDTO;
import za.co.liberty.dto.userprofiles.ProfileRoleDTO;
import za.co.liberty.dto.userprofiles.RunnableRuleDTO;
import za.co.liberty.exceptions.UnResolvableException;
import za.co.liberty.exceptions.data.DataNotFoundException;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.helpers.lookup.ServiceLocator;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.models.PagePanelInfoObject;
import za.co.liberty.web.pages.MaintenanceBasePage;
import za.co.liberty.web.pages.admin.models.RolesModel;
import za.co.liberty.web.pages.panels.DefaultMaintenanceSelectionPanel;
import za.co.liberty.web.system.EJBReferences;
import za.co.liberty.web.system.SRSAuthWebSession;

/**
 * Controller page used to administrate / maintain Roles
 * 
 * @author JZB0608 - 06 May 2008
 *
 */
public class RoleAdmin extends MaintenanceBasePage<Integer> {

	private static final long serialVersionUID = 104936816257820773L;
	private RolesModel pageModel;	
	
	
	/**
	 * Default constructor
	 *
	 */
	public RoleAdmin() {
		super(null);
	}
	
	/**
	 * This constructor is only used internally when save is done 
	 * to keep track of the tab the user was on
	 *
	 */
	private RoleAdmin(ProfileRoleDTO dto, int tabUsed) {
		super(dto, (Integer)tabUsed);		
		pageModel.setCurrentTab(tabUsed);
	}

	/**
	 * Default constructor that initialises the page
	 * with the given object
	 * 
	 * @param dto
	 */
	public RoleAdmin(ProfileRoleDTO dto) {
		super(dto);
	}
	
	@Override
	public Panel createContainerPanel() {
		Panel panel;
		if (pageModel.getSelectedItem() == null) {
			panel = new EmptyPanel(CONTAINER_PANEL_NAME);
		} else {
			ProfileRoleDTO dto = pageModel.getSelectedItem();
			if (getEditState() != EditStateType.ADD && dto != null) {
				// Refresh from db
				try {
					dto = getSessionBean().findRole(dto.getProfileRoleID());
				} catch (DataNotFoundException e) {
					throw new RuntimeException(
							"Unable to retrieve role for selected entity",e);
				}
				pageModel.setSelectedItem(dto);
			}
			panel = new RoleAdminPanel(CONTAINER_PANEL_NAME, pageModel, getEditState(),this);
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
		/* Validate that all components first (all tab panels) */
		RoleAdminPanel adminPanel = (RoleAdminPanel) containerPanel;
		//if (adminPanel.validateAllTabs()==false) {
		//	return;
		//}
		
		/* Save to db */
		ProfileRoleDTO newDto = null;
		ProfileRoleDTO dto = pageModel.getSelectedItem();
		if (dto.getProfileRoleID()==null) {
			newDto = getSessionBean().createRole(dto, SRSAuthWebSession.get().getSessionUser());
		} else {
			newDto = getSessionBean().updateRole(dto, SRSAuthWebSession.get().getSessionUser());
			newDto = dto;
		}

		invalidatePage();		
		this.info("Record was saved successfully");
		setResponsePage(new RoleAdmin(newDto, pageModel.getCurrentTab()));
	}
	
	@Override
	public Panel createSelectionPanel() {
		return new DefaultMaintenanceSelectionPanel(SELECTION_PANEL_NAME, "Role Name:",
				pageModel, this, selectionForm, ProfileRoleDTO.class, "nameBracketShortDescription", "profileRoleID") {
			private static final long serialVersionUID = -6645144485356877886L;
			@Override
			public Object getNewDtoInstance() {
				ProfileRoleDTO dto = new ProfileRoleDTO();
				dto.setDefaultMenuItemList(new ArrayList<MenuItemDTO>());
				dto.setRunnableRuleList(new ArrayList<RunnableRuleDTO>());
				dto.setAllowableRequestActionList(new ArrayList<AllowableRequestActionDTO>());
				return dto;
			}

		};
	}

	@Override
	public Object initialisePageModel(Object object,Integer currentTab) {
		RolesModel obj = new RolesModel();
		IProfileRoleManagement sessionBean = getSessionBean();
		obj.setSelectionList(sessionBean.findAllRoles());
		obj.setSelectedItem((ProfileRoleDTO) object);
		obj.setAllAvailableMenuItems(sessionBean.findAllAvailableMenuItems());
		obj.setAllAvailableRules(sessionBean.findAllAvailableRules());
		obj.setAllAvailableRuleArithmetic(sessionBean.getArithmeticTypes());
		obj.setAllAvailableRuleDataTypes(sessionBean.findAllRuleDataTypes());
		obj.setAllRequestKinds(sessionBean.getRequestKindTypes());
		
//		obj.setAllRequestKinds(sessionBean.findAllRequestKinds());
		//obj.setDefaultRequests(new ArrayList<RoleRequestActionsEntity>());
		Collections.sort(obj.getAllAvailableMenuItems(), new Comparator<MenuItemDTO>() {
			public int compare(MenuItemDTO o1, MenuItemDTO o2) {
				return o1.getMenuItemDescription().compareTo(
						o2.getMenuItemDescription());
			}
		});
		obj.setCurrentTab((currentTab != null) ? currentTab : -1);
		pageModel = obj;	
		return pageModel;
	}

	@Override
	public String getPageName() {
		return "Role Administration";
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
	protected IProfileRoleManagement getSessionBean() {
		try {
			/*
			 * return (IProfileRoleManagement) SRSAuthWebSession.get().getEJBReference(
			 * EJBReferences.PROFILE_ROLE_MANAGEMENT);
			 */
			//MSK#Change
			return ServiceLocator.lookupService(IProfileRoleManagement.class);
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
}
