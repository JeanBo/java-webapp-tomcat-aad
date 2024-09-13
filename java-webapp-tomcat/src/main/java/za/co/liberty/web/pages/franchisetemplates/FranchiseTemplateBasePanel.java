package za.co.liberty.web.pages.franchisetemplates;

import org.apache.log4j.Logger;
import org.apache.wicket.Page;
import org.apache.wicket.markup.html.panel.FeedbackPanel;

import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.pages.franchisetemplates.model.FranchiseTemplatePanelModel;
import za.co.liberty.web.pages.interfaces.ISecurityPanel;
import za.co.liberty.web.pages.panels.BasePanel;

/**
 * Franchise Template details panel, capturing the Group Kinds details
 * 
 * @author MZL2611
 * 
 */
public class FranchiseTemplateBasePanel extends BasePanel implements ISecurityPanel{	
	
	private static final Logger logger = Logger.getLogger(FranchiseTemplateBasePanel.class);
	
	private FeedbackPanel feedBackPanel;

	
	
	

	private static final long serialVersionUID = 1L;
	/**
	 * booleans set to true if there are existing requests that still need to be authorised, screen must then be disabled
	 */
	private boolean existingMaintenanceRequest;
	
	private boolean existingTerminationRequest;
	
	private boolean existingReactivationRequest;
	
	private FranchiseTemplatePanelModel panelModel;
	
	private boolean initialised;
	
	private Page parentPage;
	/**
	 * @param arg0
	 */
	public FranchiseTemplateBasePanel(String id, final FranchiseTemplatePanelModel panelModel,
			EditStateType editState, FeedbackPanel feedBackPanel, Page parentPage) {
		super(id,editState,parentPage);		
		this.panelModel = panelModel;		
		this.feedBackPanel = feedBackPanel;
		this.parentPage = parentPage;
	}
	public Class getPanelClass() {
		
		return FranchiseTemplateBasePanel.class;
	}
	
}
