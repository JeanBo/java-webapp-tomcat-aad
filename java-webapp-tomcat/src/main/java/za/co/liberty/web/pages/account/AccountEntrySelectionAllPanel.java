package za.co.liberty.web.pages.account;

import org.apache.wicket.markup.html.form.ListMultipleChoice;

import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.pages.BasePage;
import za.co.liberty.web.pages.panels.BasePanel;

public class AccountEntrySelectionAllPanel extends BasePanel {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private AccountEntrySelectionAllModel pageModel;
	private ListMultipleChoice allEntryList = null;
	
	
	public AccountEntrySelectionAllPanel(String id, EditStateType editState, AccountEntrySelectionAllModel pageModel, BasePage parentPage){
		super(id, editState);
		this.pageModel = pageModel;
		init();
		
	}
	
	protected void init(){
		
	}
	

}
