package za.co.liberty.web.pages.reports;

import java.util.List;

import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.extensions.markup.html.tabs.TabbedPanel;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

import za.co.liberty.exceptions.security.TabAccessException;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.pages.MaintenanceBasePage.ContainerForm;
import za.co.liberty.web.pages.interfaces.IMaintenanceParent;
import za.co.liberty.web.pages.panels.MaintenanceTabbedPanel;
import za.co.liberty.web.pages.panels.SRSSecurityMessagePanel;
import za.co.liberty.web.pages.reports.model.MaintainReportsPageModel;
import za.co.liberty.web.system.SRSAuthWebSession;

public class MaintainReportsPanel extends MaintenanceTabbedPanel {

	private FeedbackPanel feedBackPanel;
	protected ReportGenerationPanel reportGenerationPanel;
	protected ReportTrackingPanel reportTrackingPanel;
	//Added for Infoslip Portal- pritam-01/10/13
	protected ViewInfoSlipPanel viewInfoslipPanel;
	protected SRSSecurityMessagePanel errorMessagePanel;
	
	private IMaintenanceParent page;
	
	public MaintainReportsPanel(String container_panel_name, 
			MaintainReportsPageModel pageModel, IMaintenanceParent page, EditStateType editState, int[] disabledPanels, 
			FeedbackPanel feedBackPanel, ContainerForm containerForm) throws TabAccessException {
		super(container_panel_name, pageModel, editState,disabledPanels,page);	
		this.page = page;
		this.feedBackPanel = feedBackPanel;
	}

	@Override
	public void initialiseTabs(List<AbstractTab> tabList) {
		if (SRSAuthWebSession.get().hasMenuInList(ReportTrackingPanel.class)) {		
			tabList.add(new AbstractTab(new Model("Report Tracking")) {
				private static final long serialVersionUID = 1L;			
	
				@Override
				public Panel getPanel(String arg0) {
					/**
					 * Prevent the panel from being instantiated repeatedly.
					 */
					if (reportTrackingPanel==null) {
						reportTrackingPanel = new ReportTrackingPanel(TabbedPanel.TAB_PANEL_ID,
							(MaintainReportsPageModel)pageModel, 
							MaintainReportsPanel.this.getEditState(),feedBackPanel);
					} else {
						reportTrackingPanel.setFeedBackPanel(feedBackPanel);
					}
					return reportTrackingPanel;
				}		
			});
		}
	

		if (SRSAuthWebSession.get().hasMenuInList(ReportGenerationPanel.class)) {		
			tabList.add(new AbstractTab(new Model("Generate Reports")) {
				private static final long serialVersionUID = 1L;			
	
				@Override
				public Panel getPanel(String arg0) {
					/**
					 * Prevent the panel from being instantiated repeatedly.
					 */
					if (reportGenerationPanel==null) {
						reportGenerationPanel = new ReportGenerationPanel(TabbedPanel.TAB_PANEL_ID,
							(MaintainReportsPageModel)pageModel, page, 
							MaintainReportsPanel.this.getEditState(),feedBackPanel);
					}
					return reportGenerationPanel;
				}		
			});
		}

		if (SRSAuthWebSession.get().hasMenuInList(ViewInfoSlipPanel.class)) {
			tabList.add(new AbstractTab(new Model("View Infoslip/PDF")) {
				private static final long serialVersionUID = 1L;			
	
				@Override
				public Panel getPanel(String arg0) {
					/**
					 * Prevent the panel from being instantiated repeatedly.
					 */
					if (((MaintainReportsPageModel)pageModel).getInfoslipDocDTO().getAvailableSearchChoiceList().size()==0) {
						if (errorMessagePanel==null) {
							errorMessagePanel = new SRSSecurityMessagePanel(TabbedPanel.TAB_PANEL_ID,null, 
									"No agreement in context.  Please search for an agreement first");
						}
						return errorMessagePanel;
					}
					if (viewInfoslipPanel==null) {
						viewInfoslipPanel = new ViewInfoSlipPanel(TabbedPanel.TAB_PANEL_ID,
							(MaintainReportsPageModel)pageModel, page, 
							MaintainReportsPanel.this.getEditState(),feedBackPanel);
					}
					return viewInfoslipPanel;
				}		
			});
		}
		
		/**
		 * Agreement Specific Values
		 */
		Long agreement = ((MaintainReportsPageModel)pageModel).getInfoslipDocDTO().getSelectedAdvisor();
		if (tabList.size()>1 && agreement==null) {
			error("No agreement in context. Please search for an agreement first");
		} 
	}
}
