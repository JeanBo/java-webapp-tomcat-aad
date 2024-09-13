package za.co.liberty.web.pages.baureports;

import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.Panel;

import za.co.liberty.web.pages.ContractNoSearchPanel;

public class DPEExtractPage extends BAUReportsBasePage{

	public DPEExtractPage() {

		add(new DPEExtractPanel("dpeextracts"));		
	}

	@Override
	public String getPageName() {
		return "Generate DPE Extracts";
	}

	@Override
	protected Panel getContextPanel() {
		/* Does not require a panel */
		return new EmptyPanel(CONTEXT_PANEL_NAME);
	}



}
