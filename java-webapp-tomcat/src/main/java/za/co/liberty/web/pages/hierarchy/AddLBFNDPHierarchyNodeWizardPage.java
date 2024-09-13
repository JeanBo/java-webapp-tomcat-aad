package za.co.liberty.web.pages.hierarchy;

import za.co.liberty.dto.party.HierarchyNodeDTO;
import za.co.liberty.web.pages.BaseWindowPage;
import za.co.liberty.web.pages.MaintenanceBasePage;
import za.co.liberty.web.pages.hierarchy.model.MaintainHierarchyPageModel;
import za.co.liberty.web.wicket.modal.SRSModalWindow;

/**
 * Page displaying the a wizzard to add a new LBF or NDP branch hierarchy node to the system
 * 
 * @author MZL2610
 *
 */
public class AddLBFNDPHierarchyNodeWizardPage extends BaseWindowPage {
	
	private static final long serialVersionUID = 1L;
	
	private SRSModalWindow parentWindow;	
	private AddLBFNDPHierarchyWizard wizard;
	private MaintenanceBasePage parentPage;
	private MaintainHierarchyPageModel model;
	
	/**
	 * Init all the page variables
	 *
	 */
	private void init(){		
		wizard = new AddLBFNDPHierarchyWizard("addLBFNDPHierarchyWizard",model,parentWindow,parentPage);
		add(wizard);    	
    }
	
	
	public AddLBFNDPHierarchyNodeWizardPage(SRSModalWindow modalWindow, MaintainHierarchyPageModel model,MaintenanceBasePage parentPage){
		this.parentWindow = modalWindow;
		this.parentPage = parentPage;
		//we clone so that the data on the screen below is not impacted upon
		this.model = (MaintainHierarchyPageModel) model.clone();
		init();
	}
	
	
	@Override
	public String getPageName() {		
		return "Add LBF NDP Hierarchy Node";
	}	
	
	public boolean isShowFeedBackPanel() {
		return false;
	}


	public HierarchyNodeDTO getLbfNDPPartyDTO() {
		return wizard.getLbfNDPPartyDTO();
	}
}
