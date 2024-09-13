package za.co.liberty.web.pages.wizard.object;

import java.io.Serializable;

import org.apache.wicket.extensions.wizard.WizardStep;
import org.apache.wicket.markup.html.panel.Panel;

/**
 * Object to keep all Wizard step data</br/>
 * NOTE: to use this with the SRSpopupWizard, panel id's must be SRSPopupWizard.SRS_WIZARD_STEP_ID
 * @author DZS2610
 *
 */
public class SRSWizardPageDetail implements Serializable {		
	private static final long serialVersionUID = 1L;

	private String title = "";
	
	private Panel stepPanel;
	
	private WizardStep step;
	
	public SRSWizardPageDetail(String title,Panel stepPanel){
		this.title = title;
		this.stepPanel = stepPanel;
	}
	
	public SRSWizardPageDetail(WizardStep step){
		this.step = step;
	}

	public WizardStep getStep(){		
		return step;		
	}
	
	public Panel getStepPanel() {
		return stepPanel;
	}	

	public String getTitle() {
		return title;
	}
}
