package za.co.liberty.web.pages.admin;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import za.co.liberty.business.guicontrollers.userprofiles.IRequestCategoryGUIController;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.helpers.lookup.ServiceLocator;
import za.co.liberty.interfaces.agreements.requests.RequestKindType;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.pages.MaintenanceBasePage.SelectionForm;
import za.co.liberty.web.pages.admin.models.RequestCategoryModel;
import za.co.liberty.web.pages.panels.BasePanel;
import za.co.liberty.web.wicket.markup.html.form.SRSDropDownChoice;
import za.co.liberty.web.wicket.markup.html.form.SRSTextField;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGrid;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGridColumn;
import za.co.liberty.web.wicket.markup.html.grid.SRSGridRowSelectionCheckBox;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGrid.GridSizeUnit;
import za.co.liberty.web.wicket.markup.repeater.data.ListDataProvider;

import com.inmethod.grid.DataProviderAdapter;
import com.inmethod.grid.IGridColumn;
import com.inmethod.grid.SizeUnit;

public class RequestCategoryAdminPanel extends BasePanel implements Serializable {
	

	private static final long serialVersionUID = 1L;

	private RequestCategoryModel pageModel;
	protected Object bean;
	protected SelectionForm selectionForm;
	public String SELECTION_PANEL_NAME = "selectionPanel";
	
	// ** Initiate all components for the screen
	private Button addButton = null;
	private Button removeButton = null;
	private SRSTextField categoryName = null;
	private Label requestCategoryNameLabel = null;
	private Label RequestKindsComboLabel = null;
	private SRSDataGrid teamGrid = null;	
	private SRSDropDownChoice reqDropDown = null;
	protected List<IGridColumn> searchResultColumns;
	
	private transient IRequestCategoryGUIController guiController;
	private static final Logger logger = Logger.getLogger(RequestCategoryAdminPage.class);
	
	public RequestCategoryAdminPanel(String id, EditStateType editState,RequestCategoryModel pageModel) {
		super(id, editState);
		this.pageModel = pageModel;
		initialise();
	}
	
	/*
	 * @Return Set the visibility of any component passed in with the state of visiblity
	 * @Param Component comp
	 * @Param boolean value - value of visiblity on the passed in component
	 */
	public void visibilityCheck(Component comp, boolean value){
		comp.setVisible(value);
	}
	
	/*
	 * @Return Set the enabled state of any component passed in
	 * @Param Component comp
	 * @Param boolean value - boolean will set the enabled state
	 */
	public void enableCheck(Component comp, boolean value){
		comp.setEnabled(value);
	}
	
	/*
	 * Initiase all components on the page
	 */
	protected void initialise() { 
		add(createRequestCategoryNameLabel());
		add(createRequestname());
		add(createRequestKindsComboLabel());
		add(createRequestCategoryCombo());
		add(createAddButton());
		addButtonCheck();
		add(createRemoveButton());
		add(createGrid());
	}
	
	/*
	 * @Return Create the label for the category name - only visible in Add state
	 */
	public Label createRequestCategoryNameLabel(){
		requestCategoryNameLabel = new Label("lblcatName", "Category Name");
		requestCategoryNameLabel.setEscapeModelStrings(false);
        add(requestCategoryNameLabel);
        if(getEditState() == EditStateType.ADD || getEditState() == EditStateType.MODIFY){
        	requestCategoryNameLabel.setVisible(true);
        }else{
        	requestCategoryNameLabel.setVisible(false);
        }
        return requestCategoryNameLabel;
	}
	
	/*
	 * @Return create the request kind combo box label - visible in add and modify state
	 */
	public Label createRequestKindsComboLabel(){
		RequestKindsComboLabel = new Label("lblRequestKinds", "All Request Kinds:");
		RequestKindsComboLabel.setEscapeModelStrings(false);
        add(RequestKindsComboLabel);
        if(getEditState() == EditStateType.ADD || getEditState() == EditStateType.MODIFY){
        	RequestKindsComboLabel.setVisible(true);
        }else{
        	RequestKindsComboLabel.setVisible(false);
        }
        return RequestKindsComboLabel;
	}
	
	private void addButtonCheck(){
		if(pageModel.getRequestKindTypeSelected() == null) {
			enableCheck(addButton, false);
		}else{
			enableCheck(addButton, true);
		}
	}
	
		
	/*
	 * Create the request category combo box- contain list from pagemodel of all the request kinds which can be selected
	 * When NOT in add mode, the model for the component will change, as it will exclude all existing request kinds linked to the category
	 */
	@SuppressWarnings("unchecked")
	public SRSDropDownChoice createRequestCategoryCombo() {
		//if view is in add, we will populate the combo box with a different propertymodel. This list is initiated on
		//requestcategoryadmin, and is set to the page model. Reason for this: to avoid using unneccessary lists, I work 
		//with the original list in modify mode. Thus, any request kinds loaded from the grid before, can still be non-present
		//in the list grid.
	
		PropertyModel models = null;
		List<RequestKindType> populateList = new ArrayList<RequestKindType>();
		models = new PropertyModel(pageModel ,"requestKindTypeSelected");
		populateList = pageModel.getAvailableRequestCategoryKindsList();
		reqDropDown = new SRSDropDownChoice("CategoryLink", models,populateList,
		new ChoiceRenderer() {

			private static final long serialVersionUID = 3371423967058938834L;
			public Object getDisplayValue(Object arg0) {
				   if (arg0==null) {
					   return null;
				   }
				   return arg0.toString();
			}	
		    public String getIdValue(Object arg0, int arg1) {
		    	   if (arg0==null) {
					   return null;
				   }
		    	   if (arg1 == 0){
		    		   return ""+0;
		    	   }
				    return ""+((RequestKindType)arg0).getRequestKind();

			}},"**SELECT ONE**");
		    
		
		reqDropDown.setOutputMarkupId(true);
		
		if (getEditState() == EditStateType.VIEW){
			this.visibilityCheck(reqDropDown, false);
			this.enableCheck(reqDropDown, false);
		}else{
			this.visibilityCheck(reqDropDown, true);
			this.enableCheck(reqDropDown, true);
		}
		reqDropDown.add(new AjaxFormComponentUpdatingBehavior("change"){
			
			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(AjaxRequestTarget target){
				addButtonCheck();
				target.add(addButton);
			}	  
		});

		return reqDropDown;
	}
	
	
	/*
	 * Component: Create text field for category name - only in add state
	 */
	public SRSTextField createRequestname(){
		categoryName = new SRSTextField("catName",new PropertyModel(pageModel.getSelectedItem(),"name"));
		categoryName.setRequired(true);
		if (getEditState()== EditStateType.ADD || getEditState() == EditStateType.MODIFY){
			categoryName.setVisible(true);
		}else{
			categoryName.setVisible(false);
		}
		
		return categoryName;
	}
	
	/*
	 * @Return grid for displaying the request kinds linked to the request category
	 */
	public SRSDataGrid createGrid(){
		Collections.sort(pageModel.getSelectedItem().getSelectedRequestKindsList(),pageModel.getCategoryComparator());
		teamGrid = new SRSDataGrid("RequestKindLink",new DataProviderAdapter(
				new ListDataProvider<RequestKindType>(pageModel.getSelectedItem().getSelectedRequestKindsList())),createSearchResultColumns(),getEditState());
		teamGrid.setAutoResize(true);
		teamGrid.setOutputMarkupId(true);
		teamGrid.setCleanSelectionOnPageChange(false);
		teamGrid.setClickRowToSelect(false);
		teamGrid.setAllowSelectMultiple(true);
		teamGrid.setGridWidth(699, GridSizeUnit.PIXELS);
		teamGrid.setRowsPerPage(10);
		teamGrid.setContentHeight(199, SizeUnit.PX);

	return teamGrid;
	}
	
	/*
	 * @Return Create the column context for the request category kinds grid to display
	 * data source: RequestCategoryKindDTO
	 */
	protected List<IGridColumn> createSearchResultColumns() { 
		List<IGridColumn> columns = new ArrayList<IGridColumn>();
		if(getEditState() !=  EditStateType.VIEW){
  		  columns.add(new SRSGridRowSelectionCheckBox("check")
		  .setInitialSize(30)
		  
		  );
		}
		
		columns.add(new SRSDataGridColumn<RequestKindType>("reqKindTypeId",
				new Model("Req Kind ID"), "getRequestKind()" ,"getRequestKind()", getEditState())
				.setInitialSize(40)
		);

		columns.add(new SRSDataGridColumn<RequestKindType>("reqKindTypeName",
				new Model("Req Kind Description"), "getDescription()" , getEditState())
				.setInitialSize(200)
		);

		return columns;
	}
	
	private Button createAddButton() {
		addButton = new Button("addButton");
		addButton.add(new AjaxFormComponentUpdatingBehavior("click") {
			private static final long serialVersionUID = 8783888601344868130L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {	
				  pageModel.getSelectedItem().getSelectedRequestKindsList().add(pageModel.getRequestKindTypeSelected());	
				  pageModel.getAvailableRequestCategoryKindsList().remove(pageModel.getRequestKindTypeSelected());
				  Collections.sort(pageModel.getAvailableRequestCategoryKindsList(),pageModel.getCategoryComparator());
				  Collections.sort(pageModel.getSelectedItem().getSelectedRequestKindsList(),pageModel.getCategoryComparator());
				  target.add(reqDropDown);
				  target.add(teamGrid);
				  pageModel.setRequestKindTypeSelected(null);
				  addButtonCheck();
				  target.add(addButton);
			}
		});
		addButton.setOutputMarkupId(true);
		if(getEditState() == EditStateType.VIEW){
			this.visibilityCheck(addButton, false);
			this.enableCheck(addButton, false);
		}else{
			this.visibilityCheck(addButton, true);
			this.enableCheck(addButton, true);
		}
		
		return addButton;
	}

	private Button createRemoveButton() {
		removeButton = new Button("removeButton");
		removeButton.add(new AjaxFormComponentUpdatingBehavior("click") {

			private static final long serialVersionUID = -4298021708653928342L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				List<Object> objectGrid = null; 
				objectGrid = teamGrid.getSelectedItemObjects();
				pageModel.getSelectedItem().getSelectedRequestKindsList().removeAll(objectGrid);
				List<RequestKindType> newList = new ArrayList<RequestKindType>(pageModel.getOriginalRequestKindsList());
				newList.removeAll(pageModel.getSelectedItem().getSelectedRequestKindsList());
				pageModel.getAvailableRequestCategoryKindsList().clear();
				pageModel.getAvailableRequestCategoryKindsList().addAll(newList);
				Collections.sort(pageModel.getAvailableRequestCategoryKindsList(),pageModel.getCategoryComparator());
				Collections.sort(pageModel.getSelectedItem().getSelectedRequestKindsList(),pageModel.getCategoryComparator());
				target.add(reqDropDown);
				target.add(teamGrid);
			}
		});
		removeButton.setOutputMarkupId(true);
		
		if (getEditState() == EditStateType.VIEW){
			this.visibilityCheck(removeButton, false);
			this.enableCheck(removeButton, false);
		}else{
			this.visibilityCheck(removeButton, true);
			this.enableCheck(removeButton, true);
		}
		return removeButton;
	}

	public RequestCategoryModel getPageModel() {		
		return this.pageModel;
	}
	
	protected IRequestCategoryGUIController getGuiController() {
		if (guiController == null) {
			try {
				guiController = ServiceLocator.lookupService(IRequestCategoryGUIController.class);
			} catch (NamingException e) {
				Logger.getLogger(this.getClass()).error("Lookup of gui controller failed:" + e);
				CommunicationException comm = new CommunicationException("Lookup of Request Category GUI controller failed!" , e);
				throw comm;
			} 
		}
		return guiController;
	}

	
}