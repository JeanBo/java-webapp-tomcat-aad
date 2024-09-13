package za.co.liberty.web.pages.account;

import org.apache.log4j.Logger;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.Panel;

import za.co.liberty.dto.userprofiles.ContextDTO;
import za.co.liberty.web.data.enums.ContextType;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.pages.BasePage;

public class AccountEntryPage extends BasePage {
	
	private static final Logger logger = Logger.getLogger(AccountEntryPage.class);
	private Panel selectPanel = null;
	private String SELECTION_PANEL_NAME = "selectionPanel";
	
	public AccountEntryPage(){
		super();
		initiateModel();
	}
	
	private void initiateModel(){
		//get context
		AccountEntryModel pageModel = new AccountEntryModel();
		EditStateType editState = getEditState();
		final ContextDTO context = getPageContextDTO();
		//if nothing loaded display nothing
		if(context == null){
			pageModel.setSelectedContext(new ContextDTO());
		}else{
			if(getPageContextDTO().getAgreementContextDTO().getAgreementDivision() == null) {
				selectPanel = new EmptyPanel(SELECTION_PANEL_NAME);
			}else{
				pageModel.setSelectedContext(context);
				selectPanel = new AccountEntryPanel(SELECTION_PANEL_NAME, editState, pageModel, this);
			}
			
		}
		
		
		add(selectPanel);
	}
	
	@Override
	public String getPageName() {
		return "AccountEntryPage";
	}

	@Override
	public ContextType getContextTypeRequired() {
		return ContextType.AGREEMENT;
	}
	
	
}
