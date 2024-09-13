package za.co.liberty.web.pages.party;

import za.co.liberty.dto.party.PartyDTO;
import za.co.liberty.web.pages.BaseWindowPage;
import za.co.liberty.web.pages.wizard.SRSPopupWizard;
import za.co.liberty.web.wicket.modal.SRSModalWindow;

/**
 * Page displaying the a wizzard to add a new party to the system
 * 
 * @author DZS2610
 *
 */
public class AddPartyWizardPage extends BaseWindowPage  {
	
	private static final long serialVersionUID = 1L;
	
	private SRSModalWindow parentWindow;
	private PartyDTO party;	
	private SRSPopupWizard wizard = null;
	/**
	 * Init all the page variables
	 *
	 */
	private void init(){			
		wizard = new AddPartyWizard("addUserWizard",parentWindow, this.getPageReference());		
		add(wizard); 
    }
	
	
	public AddPartyWizardPage(SRSModalWindow modalWindow, PartyDTO party) {
		this.parentWindow = modalWindow;	
		this.party = party;			
		init();
	}
	
	
	@Override
	public String getPageName() {		
		return "Add Party";
	}	
	
	public boolean isShowFeedBackPanel() {
		return false;
	}


}
