package za.co.liberty.web.pages.party;

import org.apache.wicket.Page;

import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.pages.interfaces.ISecurityPanel;
import za.co.liberty.web.pages.panels.BasePanel;

/**
 * Created a base for party details as we only want to secure one panel
 * @author DZS2610
 *
 */
public abstract class BasePartyDetailsPanel extends BasePanel implements ISecurityPanel{

	public BasePartyDetailsPanel(String id, EditStateType editState, Page parentPage) {
		super(id, editState,parentPage);		
	}
	
	public Class getPanelClass() {		
		return BasePartyDetailsPanel.class;
	}
}
