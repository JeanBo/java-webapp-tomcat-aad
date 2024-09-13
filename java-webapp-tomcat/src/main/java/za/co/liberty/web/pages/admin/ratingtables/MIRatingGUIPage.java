package za.co.liberty.web.pages.admin.ratingtables;

import java.util.ArrayList;

import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.Panel;

import za.co.liberty.business.guicontrollers.ratingtable.IMIRatingTableGUIController;
import za.co.liberty.dto.rating.AdditionalHierarchyViewDTO;
import za.co.liberty.dto.rating.HierarchyAddressClassificationDTO;
import za.co.liberty.dto.rating.HierarchyNodeCharacteristicsDTO;
import za.co.liberty.dto.rating.MIRatingTableNameDTO;
import za.co.liberty.dto.userprofiles.ISessionUserProfile;
import za.co.liberty.exceptions.data.ValidationException;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.helpers.lookup.ServiceLocator;
import za.co.liberty.interfaces.rating.IGuiRatingRow;
import za.co.liberty.interfaces.rating.MIRatingTableColumnLayoutType;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.pages.MaintenanceBasePage;
import za.co.liberty.web.pages.admin.models.RatingTablePageModel;
import za.co.liberty.web.pages.interfaces.IChangeableStatefullComponent;
import za.co.liberty.web.pages.panels.DefaultMaintenanceSelectionPanel;
import za.co.liberty.web.system.SRSAuthWebSession;

/**
 * Admin page used to maintain MI Rating tables. 
 *
 */
public class MIRatingGUIPage extends MaintenanceBasePage<Object> {

	private static final long serialVersionUID = 1L;
	
	private RatingTablePageModel pageModel;
	private transient IMIRatingTableGUIController guiController;
	private static final Logger logger = Logger.getLogger(MIRatingGUIPage.class);
	
	private Button modifyButton;
	private Button addNewButton;
	private Button saveButton;
	private Button cancelButton;
	
	
	public MIRatingGUIPage(){   
		super(null);
	}
	 
	public MIRatingGUIPage(Object obj){   		
		super(obj);
	}
	Panel panel = null;
	public Panel createContainerPanel() {
	
		if (pageModel.getSelectedItem() == null) {
			panel = new EmptyPanel(CONTAINER_PANEL_NAME);
		} else {
			panel = new MIRatingFilterPanel(CONTAINER_PANEL_NAME, getEditState(), pageModel,this);
		}
		panel.setOutputMarkupId(true);
		return panel;
	}

	@Override
	public Button[] createNavigationalButtons() {
		return new Button[] {
				saveButton=createSaveButton("button1"), 
				cancelButton=createCancelButton("button2"),
				modifyButton=createModifyButton("button3"),
				addNewButton=createAddNewButton("button4")};
	}
	
	@Override
	public void doSave_onSubmit() {		
	
		ISessionUserProfile loggedInUser = ((SRSAuthWebSession)this.getSession()).getSessionUser();
		IMIRatingTableGUIController miRatingTableGUIController = getGUIController();
		try {			
			miRatingTableGUIController.raiseMIRatingRequest(pageModel.getSelectionRow(), pageModel.getGuiRatingRowBeforeImage(), 
					loggedInUser,
					null);	
			IGuiRatingRow guiRatingRow = getGUIController().insertOrUpdateRatingRequest(pageModel.getSelectionRow(), String.valueOf(loggedInUser.getPartyOid()));
			
			this.info("Record was saved successfully");
			invalidatePage();				
			setResponsePage(new MIRatingGUIPage(pageModel));
			
		} catch (ValidationException e) {	
			for (String err : e.getErrorMessages()) {
				this.error("Error: " + err);
			}	
		}		
	}
	
		
	@SuppressWarnings("unchecked")
	@Override
	public DefaultMaintenanceSelectionPanel createSelectionPanel() {
		return new DefaultMaintenanceSelectionPanel<MIRatingTableNameDTO>(SELECTION_PANEL_NAME,"Table Name:",pageModel, this, 
					selectionForm, MIRatingTableNameDTO.class, "tableName", "id") {
			private static final long serialVersionUID = -2623730454856120154L;
			
			protected Panel createControlButtonPanel() {
				modifyButton = createModifyButton("button1");
				addNewButton = createAddNewButton("button2");
	
				Panel panel = new EmptyPanel("controlButtonPanel");				
				panel.setOutputMarkupId(true);
				return panel;
			}			
		};
	}
	
	@Override
	public Object initialisePageModel(Object obj, Object pageModelExtraValueObject) {
		if(obj != null){
			pageModel = (RatingTablePageModel)obj;	
		}else{
			RatingTablePageModel object = new RatingTablePageModel();   
			ArrayList<MIRatingTableNameDTO> tableNames = new ArrayList<MIRatingTableNameDTO>();
			tableNames.addAll(getTableNames());
			object.setSelectionList(tableNames);
			pageModel = object;
		}
		
		return pageModel;
	}

	@Override
	public String getPageName() {
		return "MI Rating Table Administration";
	}	
	
	protected ArrayList<MIRatingTableNameDTO> getTableNames(){
		MIRatingTableNameDTO dto = new MIRatingTableNameDTO();
		ArrayList<MIRatingTableNameDTO> tableNames = new ArrayList<MIRatingTableNameDTO>();
		for (MIRatingTableColumnLayoutType details: MIRatingTableColumnLayoutType.values())
	    {
			dto = new MIRatingTableNameDTO();    
			dto.setId(details.getId());
			dto.setTableName(details.getTableName());
			tableNames.add(dto);
	    }		
		return tableNames;
	}

	protected IMIRatingTableGUIController getGUIController() {
		if (guiController == null) {
			try {
				guiController = ServiceLocator.lookupService(IMIRatingTableGUIController.class);
			} catch (NamingException namingErr) {
				logger.error(this.getPageName()
						+ " IMIRatingTableGUIController can not be lookedup:"
						+ namingErr.getMessage());
				CommunicationException comm = new CommunicationException("IMIRatingTableGUIController can not be looked up!");
				throw new CommunicationException(comm);
			} 
		}
		return guiController;
	}
	
	
	/**
	 * Create the add new button
	 */
	protected Button createAddNewButton(String id) {
		// TODO Get form differently
		AjaxButton button = new AjaxButton(id) {

			private static final long serialVersionUID = -5330766713711809776L;

			@Override
			protected void onComponentTag(ComponentTag tag) {
				super.onComponentTag(tag);
				tag.getAttributes().put("value", "Add New");
				tag.getAttributes().put("type", "submit");
			}


			@Override
			protected void onSubmit(AjaxRequestTarget target, Form form) {
				doAddNew_onSubmit(target, form);
			}
		};
		button.setEnabled((hasAddAccess() && null == pageModel.getSelectionRow() && pageModel.getSelectedItem() !=null && !getEditState().equals(EditStateType.ADD)));
		button.setOutputMarkupId(true);
		return button;
	}
	/**
	 * A cancel button that invalidates the page
	 */
	protected Button createCancelButton(String id) {
		AjaxButton button = new AjaxButton(id) {

			private static final long serialVersionUID = -5330766713711807176L;
			
			@Override
			protected void onComponentTag(ComponentTag tag) {
				super.onComponentTag(tag);
				tag.getAttributes().put("value", "Cancel");
				tag.getAttributes().put("type", "submit");
			}
	
			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> arg1) {
				setEditState(EditStateType.VIEW, target);
				pageModel.setSelectionRow(null);
				swapContainerPanel(target);
				swapNavigationPanel(target);				
			}
		};
		button.setEnabled(pageModel.getSelectionRow()!=null && (!getEditState().isViewOnly() || getEditState().equals(EditStateType.ADD)|| getEditState().equals(EditStateType.VIEW)));
		button.setOutputMarkupId(true);
		button.setDefaultFormProcessing(false);
		return button;
	}
	/**
	 * Create the modify button
	 */
	protected Button createModifyButton(String id) {
		Button button = new AjaxButton(id) {

			private static final long serialVersionUID = -5330766713711809772L;

			@Override
			protected void onComponentTag(ComponentTag tag) {
				super.onComponentTag(tag);
				tag.getAttributes().put("value", "Modify");
				tag.getAttributes().put("type", "submit");
			}

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form form) {
				doModify_onSubmit(target, form);
			}

		};
		button.setOutputMarkupId(true);
		button.setEnabled((hasModifyAccess() && pageModel.getSelectionRow()!=null && getEditState() == EditStateType.VIEW));
		
		return button;
	}
	
	protected Button createSaveButton(String id) {
		Button saveButton = super.createSaveButton(id);
		if (saveButton!=null) {
			saveButton.setEnabled(pageModel.getSelectionRow()!=null && !getEditState().isViewOnly());
		}
		return saveButton;
	}
	
	public void doAddNew_onSubmit(AjaxRequestTarget target, Form form) {
			
		pageModel.setSelectionRow((IGuiRatingRow)getNewDtoInstance());
		pageModel.setGuiRatingRowBeforeImage(null);
		setEditState(EditStateType.ADD, target);
		
		swapNavigationPanel(target);		
	}
	
	public Object getNewDtoInstance() {
		String tableName = pageModel.getSelectedItem().getTableName();
		IGuiRatingRow retRow = null;
	
		if(MIRatingTableColumnLayoutType.ADDITIONAL_MI_HIERARCHY_VIEWS.getTableName().equalsIgnoreCase(tableName)){
			retRow = new AdditionalHierarchyViewDTO();
		}else if(MIRatingTableColumnLayoutType.MI_HIERARCHY_NODE_CHARACTERISTICS.getTableName().equalsIgnoreCase(tableName)){
			retRow = new HierarchyNodeCharacteristicsDTO();
		}else if(MIRatingTableColumnLayoutType.HIERARCHY_NODE_ADDRESS_CLASSIFICATION.getTableName().equalsIgnoreCase(tableName)){
			retRow = new HierarchyAddressClassificationDTO();
		}
		
		return retRow;
	}
	
	public void doModify_onSubmit(AjaxRequestTarget target, Form form) {	
		
		setEditState(EditStateType.MODIFY, target);
		swapNavigationPanel(target);	
	}

	@Override
	public void setEditState(EditStateType newState, AjaxRequestTarget target) {
		super.setEditState(newState, target);
		
		if (isNotifyPanels) {
			if (containerPanel != null && containerPanel instanceof IChangeableStatefullComponent) {
				((IChangeableStatefullComponent)containerPanel).setEditState(newState, target);
			}
		} 		
	}
	
	protected boolean isCreateNavigationPanel() {
		return (getEditState()!=null && pageModel.getSelectedItem()!=null);
	}
	
}
