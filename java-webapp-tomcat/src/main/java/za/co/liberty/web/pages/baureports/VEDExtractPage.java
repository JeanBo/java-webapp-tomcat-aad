package za.co.liberty.web.pages.baureports;

import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.Panel;

public class VEDExtractPage extends BAUReportsBasePage{

	public VEDExtractPage() {

		add(new VEDExtractPanel("vedextracts"));		
	}

	@Override
	public String getPageName() {
		return "Generate VED Extracts";
	}

	@Override
	protected Panel getContextPanel() {
		/* Does not require a panel */
		return new EmptyPanel(CONTEXT_PANEL_NAME);
	}



}
