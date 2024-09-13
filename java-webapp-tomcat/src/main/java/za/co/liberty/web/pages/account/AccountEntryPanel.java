package za.co.liberty.web.pages.account;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormChoiceComponentUpdatingBehavior;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.ListMultipleChoice;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import com.inmethod.grid.IGridColumn;
import com.inmethod.grid.SizeUnit;

import za.co.liberty.business.account.IAccountEntryGuiController;
import za.co.liberty.dto.account.AccountEntryDTO;
import za.co.liberty.dto.account.AccountEntrySelectionCriteriaDTO;
import za.co.liberty.dto.account.AccountEntryTypeDTO;
import za.co.liberty.dto.account.ProductSpecDTO;
import za.co.liberty.dto.agreement.AgreementRoleDTO;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.helpers.lookup.ServiceLocator;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.pages.BasePage;
import za.co.liberty.web.pages.panels.BasePanel;
import za.co.liberty.web.wicket.markup.html.form.SRSDateField;
import za.co.liberty.web.wicket.markup.html.form.SRSDropDownChoice;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGrid;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGrid.GridSizeUnit;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGridColumn;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataProviderAdapter;
import za.co.liberty.web.wicket.markup.repeater.data.SortableListDataProvider;


/**
 * Panel for the account entry listing display selections
 * @author JWV2310
 *
 */
public class AccountEntryPanel extends BasePanel  {

	private static final long serialVersionUID = 1L;
	
	private static final Logger logger = Logger.getLogger(AccountEntryPanel.class);
	
	transient IAccountEntryGuiController guiController;
	
	private String SELECTION_FORM_NAME = "selectionForm";
	
	private Form selectionForm;
	private AccountEntryModel pageModel;
	private BasePage parentPage;
	
//	protected DatePicker startDatePicker;
//	protected DatePicker endDatePicker;
	protected SRSDateField startDateField;
	protected SRSDateField endDateField;
	
	protected SRSDropDownChoice accountTypesCombo;
	//protected ListMultipleChoice productComboListMultipleChoice;
	protected ListMultipleChoice accountGroupEntryMC;
	protected SRSDropDownChoice productComboListMultipleChoice;
	protected ListMultipleChoice AccountEntryComboListMultipleChoice;
	private Panel accountEntrySelection;
	protected RadioGroup group=new RadioGroup("group");

	
	protected SRSDataGrid accountEntryDataGrid;
	
	protected Button searchButton;
	
	
	public AccountEntryPanel(String id, EditStateType editState, AccountEntryModel pageModel, BasePage parentPage) {
		super(id, editState, parentPage);
		this.pageModel = pageModel;
		this.parentPage = parentPage;
		
		initComponents();
	}
	
	
	protected void initComponents(){
		logger.info("Select agreement:" + pageModel.getSelectedContext().getAgreementContextDTO().getAgreementNumber());
		//populate this agreement account types and display in listview
		pageModel.setAllProducts(getSessionBean().getAllProducts());
		pageModel.setAllAccountEntryTypeList(getSessionBean().getAllAvailableAccountEntryTypes());
		pageModel.setAllIntermediaryAccountRoles(getSessionBean().getSelectionCriteriaIntermediaryAgreementRoleAccountTypesList(pageModel.getSelectedContext()));
		//for an entity to have an account ,they must be an intermediary. In shorter terms, must have an agreement. 
		//if they don't, they won't have account. Thus we check that the agreement context contains an agreement division. Only then
		//go and populate the psd vs type managers to populate account type details. 
		//Why do we still allow the above then to happen instead of this first? Well, the information must be cached, and since it doesn't take 
		//that long, it will plae an illusion that the screen is awesome, due to the user that will enter this screen with there own UACF id the first time
		if(pageModel.getSelectedContext() != null && pageModel.getSelectedContext().getAgreementContextDTO().getAgreementDivision() != null) {
			pageModel.setAllAvailableAccountTypes(getSessionBean().getAllAvailableAccountTypes(pageModel.getSelectedContext()));
			pageModel.setAllGroupedEntryTypeList(getSessionBean().getAllGroupedEntryTypeList());
		}
		
		
		add(selectionForm = new SelectionForm("selectionForm"));
		add(accountEntryDataGrid = createAccountEntryDataGrid("accountEntryDataGrid"));
		
		
		
//		this.add(new AjaxFormComponentUpdatingBehavior({}));
	}
	
	
	//form where components will be selected for submission via ajax
	public class SelectionForm extends Form {
		private static final long serialVersionUID = 1L;

		public SelectionForm(String id) {
			super(id);
			AccountEntrySelectionCriteriaDTO submitNewCriteriaDTO = new AccountEntrySelectionCriteriaDTO();
			pageModel.setSelectionCriteria(submitNewCriteriaDTO);
			
			add(startDateField=createStartDateField("startDate"));
			add(endDateField=createEndDateField("endDate"));
//			add(endDatePicker=createEndDatePicker("dateToPicker", endDateField));
//			add(startDatePicker=createStartDatePicker("dateFromPicker", startDateField));
			
			
			
			add(accountTypesCombo = createAccountTypesCombo("accountTypeCombo"));
			
			add(accountEntrySelection = emptyP("accountEntrySelection")); //new EmptyPanel("accountEntrySelection"));
			
			add(group = createGroup3("group2"));
			
			add(productComboListMultipleChoice = createProductComboMultiList("productCombo"));
//			add(AccountEntryComboListMultipleChoice = createAccountEntryComboListMultipleChoice("accountEntryMC"));
//			add(accountGroupEntryMC = createAccountGroupEntryMC("accountGroupEntryMC"));
			add(searchButton = createSearchButton("searchButton"));
		}
		
		
		
		@Override
		protected void onSubmit() {
		

			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd"); //please notice the capital M
			Date fromDate;
			try {
				fromDate = formatter.parse(formatter.format(pageModel.getSelectionCriteria().getFromDate()));
				Date toDate = formatter.parse(formatter.format(pageModel.getSelectionCriteria().getToDate()));
				System.out.println(fromDate +":"+toDate);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			pageModel.getSelectionCriteria().setSrsId(pageModel.getSelectedContext().getAgreementContextDTO().getAgreementNumber());
//			pageModel.getSelectionCriteria().setSelectionCriteriaIntermediaryAgreementRoleAccountTypesList(getSessionBean().getSelectionCriteriaIntermediaryAgreementRoleAccountTypesList(pageModel.getSelectedContext()));
			System.out.println("ALL INTER ACCOUNT ROLES SIZE:" + pageModel.getAllIntermediaryAccountRoles().size());
			for(AgreementRoleDTO roleDTO: pageModel.getAllIntermediaryAccountRoles()){
				AgreementRoleDTO temp = roleDTO;
				System.out.println("Check:" + pageModel.getSelectionCriteria().getSelectedAccountTypeRole().getAccountEntryTypeId() +":"+roleDTO.getAgreementRoleKind()); 
				if(pageModel.getSelectionCriteria().getSelectedAccountTypeRole().getAccountEntryTypeId() == 
					roleDTO.getRolePlayerType()){
					System.out.println("-----------------------------------");
					System.out.println("Role player selected Role:" + temp.getRolePlayerID()+":"+ temp.getRolePlayerType());
					pageModel.getSelectionCriteria().setSelectedAccountRole(temp);
				}
			}
			
			
			//retrieve all account entries
			List<AccountEntryDTO> tempList = (getSessionBean().getAllReturnedAccountEntries(pageModel.getSelectionCriteria()));
			pageModel.setAllReturnedAccountEntries(tempList);
			System.out.println("Please:" + pageModel.getAllReturnedAccountEntries().size());
			System.out.println(tempList.size());
			List<AccountEntryDTO> newOne = new ArrayList<AccountEntryDTO>();
			for(AccountEntryDTO inst:tempList){
				AccountEntryDTO n = inst;
				newOne.add(n);
			}
//			List<AccountEntryDTO> tempList = pageModel.getAllReturnedAccountEntries();
//			List<AccountEntryDTO> newOne = tempList;
			pageModel.getAllReturnedAccountEntries().clear();
			System.out.println("Before clear:"+newOne.size()+":" + tempList.size()+":"+pageModel.getAllReturnedAccountEntries());
			for(AccountEntryDTO inst:newOne){
				pageModel.getAllReturnedAccountEntries().add(inst);
				System.out.println("add:" + inst.getOid());
			}
			SRSDataGrid grid = createAccountEntryDataGrid("accountEntryDataGrid");
			accountEntryDataGrid.replaceWith(grid);
			accountEntryDataGrid = grid;
			
//			SRSDataGrid grid = createFeGrid("feGrid");
//			feGrid.replaceWith(grid);
//			feGrid = grid;
//			if (target!=null) {
//				target.addComponent(feGrid);
//			}
			
			
		}};
		
		protected Panel emptyP(String id){
			accountEntrySelection = new EmptyPanel("accountEntrySelection");
			accountEntrySelection.setOutputMarkupId(true);
			return accountEntrySelection;
			
		}
		
		
		private List<Options> getList(){
			Options temp = new Options();
			temp.setOption("Basic");
			List<Options> b = new ArrayList<Options>();
			b.add(temp);
			Options temp2 = new Options();
			temp.setOption("AA");
			b.add(temp2);
			
			final List Options = Arrays.asList(new Options[] { temp, temp2 });

			
			return Options;
		}
		
		
		private Options model = new Options();
		
		private List<String> siteList = Arrays.asList(new String[] {"All", "Groups"});
		
		private String site = "";
		
		public AccountEntrySelectionAllModel accEntryAllModel = new AccountEntrySelectionAllModel();
		public AccountEntrySelectionGroupModel accEntryGroupModel = new AccountEntrySelectionGroupModel();
		
		private RadioGroup createGroup3(String name){
			accountEntrySelection.setOutputMarkupId(true);
			RadioGroup group = new RadioGroup(name,new PropertyModel(this, "site"));
			add(group);
			ListView sites=new ListView("sites", siteList) {

				@Override
				protected void populateItem(ListItem item) {
					Radio rad = new Radio("radio", item.getModel());
					item.add(rad);
				}
			   
			};
			group.add(sites);
			group.add(new AjaxFormChoiceComponentUpdatingBehavior() {

				@Override
				protected void onUpdate(AjaxRequestTarget arg0) {
					
//					Panel panel = createContainerPanel();
//					containerPanel.replaceWith(panel);
//					containerPanel = panel;
//					if (target!=null) {
//						target.addComponent(containerPanel);
//					}
					
					//populate the panels models- this will move to a once off thing in page init
					
					accEntryAllModel.setAllAccountEntryTypeList(pageModel.getAllAccountEntryTypeList());
					accEntryGroupModel.setAllGroupedEntryTypeList(pageModel.getAllGroupedEntryTypeList());
					
					if (site == "All"){
						System.out.println("DONE");
//						HelperPanel b = null;
//						b.getInstance("accountEntrySelection", createAccountGroupEntryMC("accountEntrySelection2"));
//						add(b);
//						Panel c = new Panel("c");
//						add(c);
//						c.add(new TextField("wack"));
						Panel taccountEntry1 = new AccountEntrySelectionAllPanel("accountEntrySelection",getEditState(),accEntryAllModel,parentPage);
						taccountEntry1.setOutputMarkupId(true);
						accountEntrySelection.replaceWith(taccountEntry1);
						accountEntrySelection = taccountEntry1;
						if(arg0 != null){
							arg0.add(accountEntrySelection);
						}else{
							System.out.println("Waor");
						}
						
					}else{
						System.out.println("DONE2");
						//String id, EditStateType editState, AccountEntryModel pageModel, BasePage parentPage
						System.out.println("Account Entry Group model:" + accEntryGroupModel.getAllGroupedEntryTypeList().size());
						Panel taccountEntry = new AccountEntrySelectionGroupPanel("accountEntrySelection", getEditState(), accEntryGroupModel
								,parentPage);
						System.out.println(taccountEntry.getClassRelativePath());
						taccountEntry.setOutputMarkupId(true);
						
						accountEntrySelection.replaceWith(taccountEntry);
						accountEntrySelection = taccountEntry;
						if(arg0 != null){
							arg0.add(accountEntrySelection);
						}else{
							System.out.println("Waor");
						}
					}
				}
				
			});
			
			return group;
			
		}
		
		private void replace(Panel obj){
			
		}
				
		private RadioGroup createGroup2(String name){
			
			final MyObject bean = new MyObject();
			
			RadioGroup radioGroup = new RadioGroup(name,new PropertyModel(bean,"myInt")){

				
				@Override
				protected void onSelectionChanged(Object newSelection) {
					super.onSelectionChanged(newSelection);
				}
//
//				@Override
//				protected boolean wantOnSelectionChangedNotifications() {
//					super.wantOnSelectionChangedNotifications();
//					return true;
//				}
				
			};
//			Radio internalRadio = new Radio("radio", new PropertyModel(model,"option"));
//			Radio externalRadio = new Radio("radio2", new PropertyModel(model,"option"));
			
			radioGroup.add(new Radio("radio1", new Model(1)));
			radioGroup.add(new Radio("radio2", new Model(2)));

			
//			radioGroup.add(internalRadio);
			//radioGroup.add(externalRadio);
			
			
			
			radioGroup.add(new AjaxFormChoiceComponentUpdatingBehavior(){

				@Override
				protected void onUpdate(AjaxRequestTarget arg0) {
					System.out.println("Change Clicked " + bean.getMyInt());
				}
				
			});
						
			return radioGroup;
			
//			final RadioChoice radioGroupYesNo = new RadioChoice(groupName, new PropertyModel(modelObject, propFieldName), Arrays.asList(optionsArr)) {           @Override    public boolean wantOnSelectionChangedNotifications() {    
			
		}
		
		//working
		private RadioGroup createGroup(String name){
			RadioGroup temp = new RadioGroup(name, new PropertyModel(model , "option")) {

				@Override
				protected void onSelectionChanged(Object newSelection) {
					super.onSelectionChanged(newSelection);
					System.out.println("onSelection changed ");
					System.out.println(newSelection.toString()+":"+model.getOption().toString());
					System.out.println(((Options)newSelection).getOption());
				}

				@Override
				protected boolean wantOnSelectionChangedNotifications() {
					return true;
				}
				
				
				
			};

			Options selected = new Options();
			
			List<Options> option= new ArrayList<Options>();
			selected.setOption("Basic");
			option.add(selected);
			selected.setOption("Groups");
			option.add(selected);
			
			ListView a = new ListView<Options>("persons", getList()){
				@Override
				protected void populateItem(ListItem item) {
					item.add(new Radio("radio", item.getModel()));
					item.add(new Label("name", new PropertyModel(item.getModel(), "option")));
				}
			};
			temp.add(a);
		
			
//			RadioChoice<String> hostingType = new RadioChoice<String>(
////					"hosting", new PropertyModel<String>(this, "selected"), TYPES);
//				
			return temp;
		}
//		
//		private ListMultipleChoice createAccountEntryComboListMultipleChoice(String name){
//			ListMultipleChoice tempMC = new ListMultipleChoice<AccountEntryTypeDTO>(name,
//					new PropertyModel(pageModel.getSelectionCriteria() ,"selectedAccountEntryTypeList"),
//					pageModel.getAllAccountEntryTypeList(),
//					new ChoiceRenderer() {
//						@Override
//						public Object getDisplayValue(Object object) {
//							if(object == null){
//								return null;
//							}
//							return ((AccountEntryTypeDTO)object).getAccountEntryTypeName();
//						}
//
//						@Override
//						public String getIdValue(Object object, int index) {
//							if(object == null){
//								return null;
//							}
//							return ""+((AccountEntryTypeDTO)object).getAccountEntryTypeId();
//						}
//					}
//			);
//			
//			return tempMC;
//		}
		
		
		
		private ListMultipleChoice createAccountGroupEntryMC(String name){
			ListMultipleChoice tempMC = new ListMultipleChoice<AccountEntryTypeDTO>(name,
					new PropertyModel(pageModel.getSelectionCriteria() ,"selectedGroupedEntryTypeList"),
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
	
		private SRSDataGrid createAccountEntryDataGrid(String name){
			SRSDataGrid tempDataGrid 
			= new SRSDataGrid(name,new SRSDataProviderAdapter(
					new SortableListDataProvider<AccountEntryDTO>(pageModel.getAllReturnedAccountEntries())),createSearchResultColumns(),getEditState());
			tempDataGrid.setAutoResize(true);
			tempDataGrid.setOutputMarkupId(true);
			tempDataGrid.setCleanSelectionOnPageChange(false);
			tempDataGrid.setClickRowToSelect(false);
			tempDataGrid.setAllowSelectMultiple(true);
			tempDataGrid.setGridWidth(850, GridSizeUnit.PIXELS);		
			tempDataGrid.setRowsPerPage(10);
			tempDataGrid.setContentHeight(199, SizeUnit.PX);
			return tempDataGrid;
		}
		
		protected List<IGridColumn> createSearchResultColumns() { 
			List<IGridColumn> columns = new ArrayList<IGridColumn>();
			
			//only display this check box if edit state not in view state- will be applicable for add and edit
//			if(pageState != EditStateType.VIEW) {
//					columns.add(new SRSGridRowSelectionCheckBox("check")
//					.setInitialSize(30)
//				    );
//			}	
			//nextExecutionDate
			//startdate
		
			columns.add(new SRSDataGridColumn<AccountEntryDTO>("oid",
					new Model("oid"), "oid" ,"oid", getEditState())
			.setInitialSize(120)
			);
			columns.add(new SRSDataGridColumn<AccountEntryDTO>("aeoid",
					new Model("aeoid"), "aeoid" ,"aeoid", getEditState())
			.setInitialSize(120)
			);
			columns.add(new SRSDataGridColumn<AccountEntryDTO>("postedDate",
					new Model("postedDate"), "postedDate" ,"postedDate", getEditState())
			.setInitialSize(120)
			);
			columns.add(new SRSDataGridColumn<AccountEntryDTO>("typeOid",
					new Model("typeOid"), "typeOid" ,"typeOid", getEditState())
			.setInitialSize(120)
			);
			columns.add(new SRSDataGridColumn<AccountEntryDTO>("productReference",
					new Model("productReference"), "productReference" ,"productReference", getEditState())
			.setInitialSize(120)
			);
			columns.add(new SRSDataGridColumn<AccountEntryDTO>("externalReference",
					new Model("externalReference"), "externalReference" ,"externalReference", getEditState())
			.setInitialSize(120)
			);
			columns.add(new SRSDataGridColumn<AccountEntryDTO>("debitCreditIndicator",
					new Model("debitCreditIndicator"), "debitCreditIndicator" ,"debitCreditIndicator", getEditState())
			.setInitialSize(120)
			);
			columns.add(new SRSDataGridColumn<AccountEntryDTO>("amount",
					new Model("amount"), "amount" ,"amount", getEditState())
			.setInitialSize(120)
			);
			columns.add(new SRSDataGridColumn<AccountEntryDTO>("moneyProvisionElementId",
					new Model("moneyProvisionElementId"), "moneyProvisionElementId" ,"moneyProvisionElementId", getEditState())
			.setInitialSize(120)
			);
			
			return columns;
		}
		
		public SRSDropDownChoice createAccountTypesCombo(String id) {
			SRSDropDownChoice tempSRSDropDown = new SRSDropDownChoice("accountTypesCombo" , 
			new PropertyModel(pageModel.getSelectionCriteria() ,"selectedAccountTypeRole"),
			pageModel.getAllAvailableAccountTypes(), new ChoiceRenderer() {
						public Object getDisplayValue(Object arg0){
							if (arg0==null) {
								   return null;
							   }
							return ((AccountEntryTypeDTO)arg0).getAccountEntryTypeName();
						}
						public Object getIdValue(Object arg0){
							if (arg0==null) {
								   return null;
							   }
							return ((AccountEntryTypeDTO)arg0).getAccountEntryTypeId();
						}
					
			},"select one");
			tempSRSDropDown.setOutputMarkupId(true);
			
			return (SRSDropDownChoice) tempSRSDropDown;
		}
		
		
		
		
		
//		private ListMultipleChoice createProductComboMultiList(String name) {
//			ListMultipleChoice tempMC = new ListMultipleChoice<ProductSpecDTO>(name,
//					new PropertyModel(pageModel.getSelectionCriteria(),"selectedProductList"),
//					pageModel.getAllProducts(),
//					new ChoiceRenderer() {
//						@Override
//						public Object getDisplayValue(Object object) {
//							if(object == null){
//								return null;
//							}
//							return ((ProductSpecDTO)object).getProductName();
//						}
//
//						@Override
//						public String getIdValue(Object object, int index) {
//							if(object == null){
//								return null;
//							}
//							return (((ProductSpecDTO)object).getProductId())+"";
//						}
//					}
//			);
//			tempMC.setOutputMarkupId(true);
//			return tempMC;
//		}
		
		
		private SRSDropDownChoice createProductComboMultiList(String name){
			
			SRSDropDownChoice tempSRSDropDown = new SRSDropDownChoice(name,
					new PropertyModel(pageModel.getSelectionCriteria(),"selectedProductList"),
					pageModel.getAllProducts(),new ChoiceRenderer() {
					private static final long serialVersionUID = 1L;
						public Object getDisplayValue(Object arg0) {
						   if (arg0==null) {
							   return null;
						   }
						   return ((ProductSpecDTO)arg0).getProductName();
						}
						public String getIdValue(Object arg0, int arg1) {
							   if (arg0==null) {
								   return null;
							   }
							   return ""+((ProductSpecDTO)arg0).getProductId();
						}
				
				},"All");
			tempSRSDropDown.setOutputMarkupId(true);
			return tempSRSDropDown;
		}
		
//		//end date picker
//		private DatePicker createEndDatePicker(String id, SRSDateField dateField) {
//			DatePicker picker = new PopupDatePicker(id, dateField);
//			picker.setOutputMarkupId(true);
//			return picker;
//		}
		
		private SRSDateField createEndDateField(String id) {
			SRSDateField text = new SRSDateField(id,  new IModel() {
				private static final long serialVersionUID = -1060562129103084694L;
				public Object getObject() {
					return pageModel.getSelectionCriteria().getToDate();
				}
				public void setObject(Object arg0) {
					pageModel.getSelectionCriteria().setToDate((Date) arg0);			
				}
				public void detach() {			
				}
			});
			text.setOutputMarkupId(true);
			text.add(text.newDatePicker());
			return text;
		}
		
		
//		private DatePicker createStartDatePicker(String id, SRSDateField dateField) {	
//			DatePicker picker = new PopupDatePicker(id, dateField);
//			picker.setOutputMarkupId(true);
//			return picker;
//		}
		
		private SRSDateField createStartDateField(String id) {
			SRSDateField text = new SRSDateField(id,  new IModel() {
				private static final long serialVersionUID = -1060562129103084694L;

				public Object getObject() {
					return pageModel.getSelectionCriteria().getFromDate();
				}
				public void setObject(Object arg0) {
					pageModel.getSelectionCriteria().setFromDate((Date) arg0);			
				}
				public void detach() {			
				}
			});
			text.setOutputMarkupId(true);
			text.add(text.newDatePicker());
			return text;
		}
		
		private Button createSearchButton(String id){
			Button tempButton = new Button(id){
				
				@Override
				public void onSubmit() {
					super.onSubmit();
					System.out.println("Submitting");
					System.out.println("");
					
				}};
			tempButton.add(new AjaxFormComponentUpdatingBehavior("update"){

				@Override
				protected void onUpdate(AjaxRequestTarget arg0) {
					arg0.add(accountEntryDataGrid);
					
				}
				
			});
				tempButton.setOutputMarkupId(true);
			
			return tempButton;
		}
//	} //end form - move to the top
	
	protected String getPageName(){
		return "Account Entry Panel Page";
	}
	
	protected IAccountEntryGuiController getSessionBean() {
		if (guiController == null) {
			try {
				guiController = ServiceLocator.lookupService(IAccountEntryGuiController.class);
			} catch (NamingException namingErr) {
				logger.error(this.getPageName()
						+ " IAccountEntryListingGuiController can not be lookedup:"
						+ namingErr.getMessage());
				CommunicationException comm = new CommunicationException("IAccountEntryListingGuiController can not be looked up!");
				throw new CommunicationException(comm);
			} 
		}
		return guiController;
	}

}

class Options implements Serializable{
	private String option;

	public String getOption() {
		return option;
	}

	public void setOption(String option) {
		this.option = option;
	}
	
}

class MyObject implements Serializable{
	private int myInt;

	public int getMyInt() {
		return myInt;
	}

	public void setMyInt(int myInt) {
		this.myInt = myInt;
	}
	
}
