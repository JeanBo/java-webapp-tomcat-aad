package za.co.liberty.web.pages.hierarchy;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.Page;
import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.extensions.markup.html.tabs.TabbedPanel;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

import za.co.liberty.dto.party.HierarchyNodeDTO;
import za.co.liberty.dto.party.contactdetail.CommunicationPreferenceDTO;
import za.co.liberty.dto.party.contactdetail.ContactPreferenceWrapperDTO;
import za.co.liberty.exceptions.security.TabAccessException;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.data.pages.ITabbedPageModel;
import za.co.liberty.web.pages.contactdetail.ContactDetailsPanel;
import za.co.liberty.web.pages.hierarchy.model.MaintainHierarchyPageModel;
import za.co.liberty.web.pages.interfaces.IMaintenanceParent;
import za.co.liberty.web.pages.panels.MaintenanceTabbedPanel;
import za.co.liberty.web.wicket.markup.html.tabs.StatefullCachingTab;

/**
 * 
 * Panel to maintain a Hierarchy node
 * @author DZS2610
 *
 */
public class MaintainHierarchyNodePanel extends MaintenanceTabbedPanel {
	
	private static final long serialVersionUID = 1L;
	private FeedbackPanel feedBackPanel;
	
	/**
	 * @param id
	 * @param pageModel
	 * @param editState
	 * @throws TabAccessException 
	 */
	public MaintainHierarchyNodePanel(String id, ITabbedPageModel<HierarchyNodeDTO> pageModel,
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
		tabList.add(new StatefullCachingTab(new Model("Hierarchy Details"),MaintainHierarchyNodePanel.this) {
			private static final long serialVersionUID = 1L;			

			@Override
			public Panel createPanel(String id) {
				return new HierarchyNodePanel(TabbedPanel.TAB_PANEL_ID,
						(MaintainHierarchyPageModel)pageModel, 
						MaintainHierarchyNodePanel.this.getEditState(),feedBackPanel,
						(getIMaintenanceParent() instanceof Page) ? (Page)getIMaintenanceParent() : null);
			}		
		});
		tabList.add(new StatefullCachingTab(new Model("Contact Details"),MaintainHierarchyNodePanel.this) {
			private static final long serialVersionUID = 1L;
			@Override
			public Panel createPanel(String id) {
				MaintainHierarchyPageModel model = (MaintainHierarchyPageModel)pageModel;
				ContactDetailsPanel contactDetailsPanel = new ContactDetailsPanel(TabbedPanel.TAB_PANEL_ID,((model != null && model.getHierarchyNodeDTO() != null && model.getHierarchyNodeDTO().getContactPreferences() != null && model.getHierarchyNodeDTO().getContactPreferences().getContactPreferences() != null) ? model.getHierarchyNodeDTO().getContactPreferences().getContactPreferences() : null), new ArrayList<CommunicationPreferenceDTO>(),  
						MaintainHierarchyNodePanel.this.getEditState(),feedBackPanel,false,(getIMaintenanceParent() instanceof Page) ? (Page)getIMaintenanceParent() : null, false);
				if(model != null && model.getHierarchyNodeDTO() != null){
					model.getHierarchyNodeDTO().setContactPreferences(new ContactPreferenceWrapperDTO(contactDetailsPanel.getCurrentContactPreferenceDetails()));
				}
				return contactDetailsPanel;
			}				
		});	
		tabList.add(new StatefullCachingTab(new Model("Linked Agreement Transfer"),MaintainHierarchyNodePanel.this) {
			private static final long serialVersionUID = 1L;
			@Override
			public Panel createPanel(String id) {
				MaintainHierarchyPageModel model = (MaintainHierarchyPageModel)pageModel;
				BulkTransferPanel transferPanel = new BulkTransferPanel(TabbedPanel.TAB_PANEL_ID,((model != null && model.getHierarchyNodeDTO() != null) ? model  : null), 
						MaintainHierarchyNodePanel.this.getEditState(),feedBackPanel,(getIMaintenanceParent() instanceof Page) ? (Page)getIMaintenanceParent() : null);
				return transferPanel;
			}				
		});	
		
		tabList.add(new StatefullCachingTab(new Model("MI Reporting Details"),MaintainHierarchyNodePanel.this) {
			private static final long serialVersionUID = 1L;
			@Override
			public Panel createPanel(String id) {
				MaintainHierarchyPageModel model = (MaintainHierarchyPageModel)pageModel;
				MIReportingDetailsPanel miReportingDetailsPanel = new MIReportingDetailsPanel(TabbedPanel.TAB_PANEL_ID,((model != null && model.getHierarchyNodeDTO() != null) ? model  : null), 
						MaintainHierarchyNodePanel.this.getEditState(),feedBackPanel,(getIMaintenanceParent() instanceof Page) ? (Page)getIMaintenanceParent() : null);
				return miReportingDetailsPanel;
			}				
		});	
	}
		
}