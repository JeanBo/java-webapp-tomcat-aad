package za.co.liberty.web.pages.admin;

import java.util.List;

import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.extensions.markup.html.tabs.TabbedPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

import za.co.liberty.dto.userprofiles.ProfileRoleDTO;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.data.pages.ITabbedPageModel;
import za.co.liberty.web.pages.admin.models.RolesModel;
import za.co.liberty.web.pages.interfaces.IMaintenanceParent;
import za.co.liberty.web.pages.panels.MaintenanceTabbedPanel;
import za.co.liberty.web.wicket.markup.html.tabs.StatefullCachingTab;

/**
 * Roles Administration panel that manages the data.
 * 
 * @author jzb0608 - 06 May 2008
 * 
 */
public class RoleAdminPanel  extends MaintenanceTabbedPanel {

	/* Constants */
	private static final long serialVersionUID = -9222355665504615229L;	
	
	/**
	 * Default constructor 
	 * 
	 * @param id
	 * @param pageModel
	 * @param editState
	 */
	public RoleAdminPanel(String id, ITabbedPageModel pageModel, EditStateType editState,IMaintenanceParent parent) {
		super(id, pageModel, editState,parent);
	}
	

	@Override
	public void initialiseTabs(List<AbstractTab> tabList) {
		/* Add role Panel */
		tabList.add(new StatefullCachingTab(new Model("Role"), RoleAdminPanel.this) {
			private static final long serialVersionUID = 1L;

			@Override
			public Panel createPanel(String id) {
				return new RoleAdminRolePanel(TabbedPanel.TAB_PANEL_ID,
						(ProfileRoleDTO)pageModel.getSelectedItem(), 
						RoleAdminPanel.this.getEditState());
			}
		});
		
		/* Add menu item link Panel */
		tabList.add(new StatefullCachingTab(new Model("Menu Items"), RoleAdminPanel.this) {
			private static final long serialVersionUID = -4841354788999081293L;
			@Override
			public Panel createPanel(String id) {
				return new RoleAdminMenuLinkPanel(id, (RolesModel) pageModel, RoleAdminPanel.this.getEditState());
			}			
		});
		/* Add rule link Panel */
		tabList.add(new StatefullCachingTab(new Model("Rules"), RoleAdminPanel.this) {
			private static final long serialVersionUID = -4841354788999081253L;
			@Override
			public Panel createPanel(String id) {				
				 return new RoleAdminRuleLinkPanel(id, (RolesModel)pageModel, RoleAdminPanel.this.getEditState());
				//return new RoleAdminRuleLinkPanel(id, (RolesModel)pageModel, RoleAdminPanel.this.getEditState());
			}
		});
		
		tabList.add(new StatefullCachingTab(new Model("Allowable Requests"), RoleAdminPanel.this) {
			private static final long serialVersionUID = -4841354788999081252L;
			@Override
			public Panel createPanel(String id) {
				 //return new RoleAdminRequestLinkingPanel(id, (RolesModel)pageModel, RoleAdminPanel.this.getEditState());
				return new AllowableRequestLinkingPanel<RolesModel>(id, (RolesModel)pageModel, RoleAdminPanel.this.getEditState());
			}
		});
		
	}

	@Override
	public int getDefaultTab() {
		if (getEditState()==EditStateType.VIEW) {
			return 1;
		}
		return -1;
	}

	@Override
	public boolean isLockTabsForModify() {
		// Allow multiple tabs to be editable when modifying.
		return false;
	}
}
