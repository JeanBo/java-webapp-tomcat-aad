package za.co.liberty.web.pages.account;

import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.ListMultipleChoice;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.PropertyModel;

import za.co.liberty.dto.account.AccountEntryTypeDTO;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.pages.BasePage;
import za.co.liberty.web.pages.panels.BasePanel;
import za.co.liberty.web.wicket.markup.html.form.SRSTextField;

public class AccountEntrySelectionGroupPanel extends BasePanel {
	
	public AccountEntrySelectionGroupPanel(String id, EditStateType editState, AccountEntrySelectionGroupModel pageModel, BasePage parentPage) {
		super(id, editState);
		System.out.println("Run");
		this.pageModel = pageModel;
		initComponents();
	}

	

	protected AccountEntrySelectionGroupModel pageModel;
	private ListMultipleChoice allGroupList = null;
	protected Panel tempPanel = null;
	private SRSTextField teamName = null;
	
//	public AccountEntrySelectionGroupPanel(){
//		
//	}
//	public AccountEntrySelectionGroupPanel(String id, AccountEntrySelectionGroupModel pageModel){
//			this.pageModel = pageModel;
//			tempPanel = initComponents(id);
//			
//	}
	
	protected void initComponents(){
//		tempPanel = new Panel(id);
		add(allGroupList = createAccountGroupEntryMC("accountEntrySelection2"));
		
		add(teamName = createTeamName("bla"));
		
//		add(tempPanel);
//		add(new EmptyPanel("accountEntrySelection"));
	}
	
	public SRSTextField createTeamName(String id){
		SRSTextField tempSRSTextField = new SRSTextField(id,new PropertyModel(pageModel,"teamName" ));
		tempSRSTextField.setVisible(true);
		
		return tempSRSTextField;
	}

	
	
//	new ListMultipleChoice<AccountEntryTypeDTO>(name,
//			new PropertyModel(pageModel.getSelectionCriteria() ,"selectedAccountEntryTypeList"),
//			pageModel.getAllAccountEntryTypeList(),
	private ListMultipleChoice createAccountGroupEntryMC(String name){
		ListMultipleChoice tempMC = new ListMultipleChoice<AccountEntryTypeDTO>(name,
				new PropertyModel(pageModel ,"selectedGroupedEntryTypeList"),
				pageModel.getAllGroupedEntryTypeList(),
				new ChoiceRenderer() {
					@Override
					public Object getDisplayValue(Object object) {
						if(object == null){
							return null;
						}
						return ((AccountEntryTypeDTO)object).getAccountEntryTypeName();
					}
					@Override
					public String getIdValue(Object object, int index) {
						if(object == null){
							return null;
						}
						return ""+((AccountEntryTypeDTO)object).getAccountEntryTypeId();
					}
				}
		);
		tempMC.setOutputMarkupId(true);
		return tempMC;
		
	}

	
}
