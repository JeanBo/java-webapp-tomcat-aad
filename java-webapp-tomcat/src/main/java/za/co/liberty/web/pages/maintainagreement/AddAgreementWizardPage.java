package za.co.liberty.web.pages.maintainagreement;

import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.extensions.wizard.Wizard;

import za.co.liberty.dto.userprofiles.ContextPartyDTO;
import za.co.liberty.web.pages.BaseWindowPage;
import za.co.liberty.web.wicket.modal.SRSModalWindow;

/**
 * Add agreement wizard page container which is usually within a modal window.
 * 
 * @author JZB0608
 *
 */
public class AddAgreementWizardPage extends BaseWindowPage {
	
	private SRSModalWindow parentWindow;
	private ContextPartyDTO partyContext;
	
	private static final long serialVersionUID = 5966777694740706438L;
	
	public AddAgreementWizardPage(SRSModalWindow parentWindow, ContextPartyDTO partyContext) {
		super();
		this.parentWindow = parentWindow;
		this.partyContext=partyContext;
		initComponents();
	}

	private void initComponents() {
		add(getAddAgreementWizard());
	}

	private Wizard getAddAgreementWizard() {
		Wizard agreementWizard = new AddAgreementWizard("addAgreementWizard",
				parentWindow,partyContext,this);
		return agreementWizard;
	}

	@Override
	public String getPageName() {
		return "Add Agreement";
	}
	
	@Override
	public boolean isShowFeedBackPanel() {
		return false;
	}

}
