package za.co.liberty.web.pages.advancedPractice;

import java.util.List;

import org.apache.wicket.Page;
import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.extensions.markup.html.tabs.TabbedPanel;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

import za.co.liberty.exceptions.security.TabAccessException;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.data.pages.ITabbedPageModel;
import za.co.liberty.web.pages.advancedPractice.model.AdvancedPracticePanelModel;
import za.co.liberty.web.pages.advancedPractice.model.IMaintainAdvancedPracticePageModel;
import za.co.liberty.web.pages.interfaces.IMaintenanceParent;
import za.co.liberty.web.pages.panels.MaintenanceTabbedPanel;
import za.co.liberty.web.wicket.markup.html.tabs.StatefullCachingTab;

/**
 * 
 * Panel to maintain a Advanced Practice
 * @author MXM1904
 *
 */
public class MaintainAdvancedPracticePanel  extends MaintenanceTabbedPanel{
	
	private static final long serialVersionUID = 1L;
	private FeedbackPanel feedBackPanel;
	
	
	
	/**
	 * @param id
	 * @param pageModel
	 * @param editState
	 * @throws TabAccessException 
	 */
	public MaintainAdvancedPracticePanel(String id, ITabbedPageModel pageModel,
			EditStateType editState, Class[] tabsToDisable,FeedbackPanel feedbackPanel, 
			IMaintenanceParent parent) throws TabAccessException {

		//ODO fill in panels that must be disabled 
		super(id, pageModel, editState, tabsToDisable,parent);
		this.feedBackPanel=feedbackPanel;
		//List<Long> list = guiController.getBankAgreementNumbers();
		if (editState.equals(EditStateType.MODIFY) || 
				editState.equals(EditStateType.ADD)) {

		}
		
	}

		
		@Override
		public void initialiseTabs(List<AbstractTab> tabList) {
			//		/* Add role Panel */
			tabList.add(new StatefullCachingTab(new Model("Advanced Practice"),MaintainAdvancedPracticePanel.this) {
				private static final long serialVersionUID = 1L;
				
				@Override
				public Panel createPanel(String id) {				
					
						return new AdvancedPracticePanel(TabbedPanel.TAB_PANEL_ID,
							new AdvancedPracticePanelModel((IMaintainAdvancedPracticePageModel)pageModel),
							MaintainAdvancedPracticePanel.this.getEditState(),
							(getIMaintenanceParent() instanceof Page) ? (Page)getIMaintenanceParent() : null);
						

				}		
			});
	}
		
}