package za.co.liberty.web.pages.panels;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.WebComponent;
import org.apache.wicket.markup.html.basic.MultiLineLabel;

import za.co.liberty.web.pages.interfaces.ISecurityPanel;

/**
 * Holds one message for a user telling them they do not have access to another panel
 * @author DZS2610
 *
 */
public final class SRSSecurityMessagePanel extends HelperPanel {
	private static final long serialVersionUID = 1L;
	private MultiLineLabel label;	
	private ISecurityPanel origionalPanel;
	
	public SRSSecurityMessagePanel(String id, ISecurityPanel origionalPanel) {
		this (id, origionalPanel, "You do not have access to view this Tab, Please contact your supervisor or SRS support to gain access.");
	}
		
	public SRSSecurityMessagePanel(String id, ISecurityPanel origionalPanel, String message) {
		super(id);
		label = new MultiLineLabel("value","\n" + message);
		label.add(new  AttributeModifier("class","red"));
		add(label);	
		this.origionalPanel = origionalPanel;
	}	

	@Override
	public WebComponent getEnclosedObject() {
		return label;
	}

	public ISecurityPanel getOrigionalPanel() {
		return origionalPanel;
	}
	
	
}
