package za.co.liberty.web.pages.maintainagreement;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.extensions.wizard.Wizard;

import za.co.liberty.dto.contracting.ResultPartyDTO;
import za.co.liberty.dto.userprofiles.ContextPartyDTO;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.pages.BaseWindowPage;
import za.co.liberty.web.pages.maintainagreement.model.MaintainAgreementPageModel;
import za.co.liberty.web.pages.wizard.SRSPopupWizard;
import za.co.liberty.web.pages.wizard.SRSPopupWizardPage;
import za.co.liberty.web.pages.wizard.object.SRSWizardPageDetail;
import za.co.liberty.web.wicket.modal.SRSModalWindow;

public class TerminateAgreementWizardPage extends BaseWindowPage {
	
	private SRSModalWindow parentWindow;
	
	private static final long serialVersionUID = 5966777694740706438L;
	
	private MaintainAgreementPageModel pageModel;
	
	public TerminateAgreementWizardPage(SRSModalWindow parentWindow,MaintainAgreementPageModel pageModel) {
		super();
		this.parentWindow = parentWindow;
		this.pageModel=pageModel;
		initComponents();
	}

	private void initComponents() {
		add(getTerminateAgreementWizard());
	}

	private Wizard getTerminateAgreementWizard() {
		Wizard agreementWizard = new TerminateAgreementWizard(
				"terminateAgreementWizard",
				parentWindow,
				pageModel);
		return agreementWizard;
	}

	@Override
	public String getPageName() {
		return "Terminate Agreement";
	}
	
	@Override
	public boolean isShowFeedBackPanel() {
		return false;
	}

}
