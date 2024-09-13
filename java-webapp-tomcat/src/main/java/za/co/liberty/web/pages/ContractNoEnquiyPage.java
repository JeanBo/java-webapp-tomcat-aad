package za.co.liberty.web.pages;

import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.Panel;

public class ContractNoEnquiyPage extends BasePage {

	public ContractNoEnquiyPage() {

		add(new ContractNoSearchPanel("contractNoSrchPanel"));
		//By Default adds an empty panel for the Search results.
		add(new EmptyPanel("contractNoSrchResultPanel"));
	}

	@Override
	public String getPageName() {
		return "Enquire by Contract Number";
	}

	@Override
	protected Panel getContextPanel() {
		/* Does not require a panel */
		return new EmptyPanel(CONTEXT_PANEL_NAME);
	}



}
