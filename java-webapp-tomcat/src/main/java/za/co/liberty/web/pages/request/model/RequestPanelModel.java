package za.co.liberty.web.pages.request.model;

import java.io.Serializable;
import java.util.List;

import org.apache.wicket.markup.html.panel.Panel;

import za.co.liberty.dto.agreement.request.RequestEnquiryRowDTO;

/**
 * Object model that is held for each panel that is being viewed.
 * 
 * @author JZB0608 - 17 Feb 2010
 */
public class RequestPanelModel implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private Panel panel;
	private Panel historyPanel;
	private List<RequestEnquiryRowDTO> requestRowList;
	private boolean enabled;
	private boolean historyEnabled;
	private boolean lastRow;
	
	public RequestPanelModel(Panel panel, List<RequestEnquiryRowDTO> requestRowList, 
			boolean enabled, Panel beforePanel) {
		this.panel = panel;
		this.historyPanel = beforePanel;
		this.requestRowList = requestRowList;
		this.enabled = enabled;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public Panel getPanel() {
		return panel;
	}

	public List<RequestEnquiryRowDTO> getRequestRowList() {
		return requestRowList;
	}
	
	public boolean isLastRow() {
		return lastRow;
	}

	public void setLastRow(boolean lastRow) {
		this.lastRow = lastRow;
	}

	public Panel getHistoryPanel() {
		return historyPanel;
	}

	public void setHistoryPanel(Panel historyPanel) {
		this.historyPanel = historyPanel;
	}

	public boolean isHistoryEnabled() {
		return historyEnabled;
	}

	public void setHistoryEnabled(boolean historyEnabled) {
		this.historyEnabled = historyEnabled;
	}
	
}
