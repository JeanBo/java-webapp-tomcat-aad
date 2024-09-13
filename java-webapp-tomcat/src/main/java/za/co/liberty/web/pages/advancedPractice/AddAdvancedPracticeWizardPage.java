package za.co.liberty.web.pages.advancedPractice;

import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;

import za.co.liberty.dto.advancedPractice.AdvancedPracticeDTO;
import za.co.liberty.web.pages.BaseWindowPage;
import za.co.liberty.web.pages.MaintenanceBasePage;
import za.co.liberty.web.pages.advancedPractice.model.MaintainAdvancedPracticePageModel;
import za.co.liberty.web.wicket.modal.SRSModalWindow;

public class AddAdvancedPracticeWizardPage extends BaseWindowPage {
	
	private static final long serialVersionUID = 1L;
	
	private SRSModalWindow parentWindow;	
	private AddAdvancedPracticeWizard wizard;
//	private MaintenanceBasePage parentPage;
	private MaintainAdvancedPracticePageModel model;
	
	/**
	 * Init all the page variables
	 *
	 */
	private void init(){		
		wizard = new AddAdvancedPracticeWizard("addAdvancedPracticeWizard",model,parentWindow);
		add(wizard);    	
    }
	
	
	public AddAdvancedPracticeWizardPage(SRSModalWindow modalWindow, MaintainAdvancedPracticePageModel model){
		this.parentWindow = modalWindow;
//		this.parentPage = parentPage;
		//we clone so that the data on the screen below is not impacted upon
		this.model = (MaintainAdvancedPracticePageModel) model.clone();
		init();
	}
	
	
	@Override
	public String getPageName() {		
		return "Add Advanced Practice";
	}	
	
	public boolean isShowFeedBackPanel() {
		return false;
	}


	public AdvancedPracticeDTO getAdvancedPracticeDTO() {
		return wizard.getAdvancedPracticeDTO();
	}
}

