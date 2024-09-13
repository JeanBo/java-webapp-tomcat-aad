package za.co.liberty.web.pages.franchisetemplates;

import java.util.List;

import org.apache.wicket.Page;
import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.extensions.markup.html.tabs.TabbedPanel;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

import za.co.liberty.dto.gui.templates.FranchiseTemplateDTO;
import za.co.liberty.dto.gui.templates.MaintainFranchiseTemplateDTO;
import za.co.liberty.exceptions.security.TabAccessException;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.data.pages.ITabbedPageModel;
import za.co.liberty.web.pages.franchisetemplates.model.FranchiseTemplatePanelModel;
import za.co.liberty.web.pages.franchisetemplates.model.MaintainFranchiseTemplatePageModel;
import za.co.liberty.web.pages.interfaces.IMaintenanceParent;
import za.co.liberty.web.pages.panels.MaintenanceTabbedPanel;
import za.co.liberty.web.wicket.markup.html.tabs.StatefullCachingTab;

/**
 * 
 * Panel to maintain a Hierarchy node
 * @author MZL2611
 *
 */
public class MaintainFranchiseTemplatePanel extends MaintenanceTabbedPanel {
	
	private static final long serialVersionUID = 1L;
	private FeedbackPanel feedBackPanel;
	
	/**
	 * @param id
	 * @param pageModel
	 * @param editState
	 * @throws TabAccessException 
	 */
	public MaintainFranchiseTemplatePanel(String id, ITabbedPageModel<MaintainFranchiseTemplateDTO> pageModel,
			EditStateType editState, int[] tabsToDisable, FeedbackPanel feedBackPanel, 
			Form form,IMaintenanceParent parent) throws TabAccessException {
		super(id, pageModel, editState,tabsToDisable,parent);	
		this.feedBackPanel = feedBackPanel;
	}

	/* (non-Javadoc)
	 * @see za.co.liberty.web.pages.panels.MaintenanceTabbedPanel#initialiseTabs(java.util.List)
	 */
	@Override
	public void initialiseTabs(List<AbstractTab> tabList) {		
		tabList.add(new StatefullCachingTab(new Model("Template"),MaintainFranchiseTemplatePanel.this) {
			private static final long serialVersionUID = 1L;			

			@Override
			public Panel createPanel(String id) {
				return new FranchiseTemplatePanel(TabbedPanel.TAB_PANEL_ID,
						((MaintainFranchiseTemplatePageModel) pageModel).getFranchiseTemplatePanelModel(), 
						MaintainFranchiseTemplatePanel.this.getEditState(),feedBackPanel,
						(getIMaintenanceParent() instanceof Page) ? (Page)getIMaintenanceParent() : null);
			}		
		});

	}
		
}