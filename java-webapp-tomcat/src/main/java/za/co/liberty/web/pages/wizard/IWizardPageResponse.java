package za.co.liberty.web.pages.wizard;

import org.apache.wicket.model.Model;

public interface IWizardPageResponse {

	
	public boolean isSuccess();
	
	public Model getWizardModelObject();
	
}
