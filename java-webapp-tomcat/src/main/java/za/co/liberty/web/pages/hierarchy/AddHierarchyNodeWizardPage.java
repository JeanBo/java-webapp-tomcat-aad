package za.co.liberty.web.pages.hierarchy;

import za.co.liberty.dto.party.HierarchyNodeDTO;
import za.co.liberty.web.pages.BaseWindowPage;
import za.co.liberty.web.pages.MaintenanceBasePage;
import za.co.liberty.web.pages.hierarchy.model.MaintainHierarchyPageModel;
import za.co.liberty.web.wicket.modal.SRSModalWindow;

/**
 * Page displaying the a wizzard to add a hierarchy node to the system
 * 
 * @author DZS2610
 *
 */
public class AddHierarchyNodeWizardPage extends BaseWindowPage {
	
	private static final long serialVersionUID = 1L;
	
	private SRSModalWindow parentWindow;	
	private AddHierarchyWizard wizard;
	private MaintenanceBasePage parentPage;
	private MaintainHierarchyPageModel model;
	
	/**
	 * Init all the page variables
	 *
	 */
	private void init(){		
		wizard = new AddHierarchyWizard("addHierarchyWizard",model,parentWindow,parentPage);
		add(wizard);    	
    }
	
	
	public AddHierarchyNodeWizardPage(SRSModalWindow modalWindow, MaintainHierarchyPageModel model,MaintenanceBasePage parentPage){
		this.parentWindow = modalWindow;
		this.parentPage = parentPage;
		//we clone so that the data on the screen below is not impacted upon
		this.model = (MaintainHierarchyPageModel) model.clone();
		init();
	}
	
	
	@Override
	public String getPageName() {		
		return "Add Hierarchy Node";
	}	
	
	public boolean isShowFeedBackPanel() {
		return false;
	}


	public HierarchyNodeDTO getHierarchyNodeDTO() {
		return wizard.getHierarchyNodeDTO();
	}
}
