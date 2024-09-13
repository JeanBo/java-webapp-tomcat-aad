package za.co.liberty.web.pages.admin;

import java.util.List;

import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.extensions.markup.html.tabs.TabbedPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

import za.co.liberty.dto.userprofiles.PartyProfileDTO;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.data.pages.ITabbedPageModel;
import za.co.liberty.web.pages.admin.models.UserAdminModel;
import za.co.liberty.web.pages.interfaces.IMaintenanceParent;
import za.co.liberty.web.pages.panels.MaintenanceTabbedPanel;
import za.co.liberty.web.wicket.markup.html.tabs.StatefullCachingTab;

/**
 * User Administration panel that manages the data for users.
 * 
 * @author jzb0608 - 14 May 2008
 * 
 */
public class UserAdminPanel  extends MaintenanceTabbedPanel {

	/* Constants */
	private static final long serialVersionUID = -9222355665504615229L;
	
	/**
	 * Default constructor 
	 * 
	 * @param id
	 * @param pageModel
	 * @param editState
	 */
	public UserAdminPanel(String id, ITabbedPageModel pageModel, EditStateType editState, IMaintenanceParent parent) {
		super(id, pageModel, editState,parent);
	}
	

	@Override
	public void initialiseTabs(List<AbstractTab> tabList) {
		/* Add role Panel */
		tabList.add(new StatefullCachingTab(new Model("Profile"), UserAdminPanel.this) {
			private static final long serialVersionUID = 1L;

			@Override
			public Panel createPanel(String id) {
				return new UserAdminUserPanel(TabbedPanel.TAB_PANEL_ID,
						(PartyProfileDTO)pageModel.getSelectedItem(), 
						UserAdminPanel.this.getEditState());
			}
		});
		
		/* Add menu item link Panel */
		tabList.add(new StatefullCachingTab(new Model("Menu Items"), UserAdminPanel.this) {
			private static final long serialVersionUID = -4841354788999081393L;
			@Override
			public Panel createPanel(String id) {
				return new UserAdminMenuLinkPanel(id,  (UserAdminModel) pageModel, UserAdminPanel.this.getEditState());
			}
		});
		
		/* Add rule link Panel */
		tabList.add(new StatefullCachingTab(new Model("Rules"), UserAdminPanel.this) {
			private static final long serialVersionUID = -4841354788999081253L;
			@Override
			public Panel createPanel(String id) {
				 return new UserAdminRuleLinkPanel(id, (UserAdminModel)pageModel, UserAdminPanel.this.getEditState());
			}
		});
		
		/* Add role link Panel */
		tabList.add(new StatefullCachingTab(new Model("Roles"), UserAdminPanel.this) {
			private static final long serialVersionUID = -4841354788999081252L;
			@Override
			public Panel createPanel(String id) {
				 return new UserAdminRoleLinkPanel(id, (UserAdminModel)pageModel, UserAdminPanel.this.getEditState());
			}
		});
		/* Add allowable requests for user Panel */
		tabList.add(new StatefullCachingTab(new Model("Allowable Requests"), UserAdminPanel.this) {
			
			/**
			 * 
			 */
			private static final long serialVersionUID = 5731149710603833091L;

			@Override
			public Panel createPanel(String id) {
				return new AllowableRequestLinkingPanel<UserAdminModel>(id, (UserAdminModel)pageModel, UserAdminPanel.this.getEditState());
			}
		});
		
	}

	@Override
	public int getDefaultTab() {
//		if (getEditState()==EditStateType.VIEW) {
//			return 1;
//		}
		return -1;
	}


	@Override
	public boolean isLockTabsForModify() {
		// Allow multiple tabs to be editable when modifying.
		return false;
	}
}
