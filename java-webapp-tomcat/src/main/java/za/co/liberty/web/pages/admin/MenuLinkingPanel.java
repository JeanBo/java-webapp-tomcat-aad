package za.co.liberty.web.pages.admin;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

//import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import za.co.liberty.dto.userprofiles.MenuItemDTO;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.data.pages.IMaintenancePageModel;
import za.co.liberty.web.pages.admin.models.RolesModel;
import za.co.liberty.web.pages.interfaces.IStatefullComponent;
import za.co.liberty.web.pages.panels.AbstractLinkingPanel;
import za.co.liberty.web.pages.panels.HelperPanel;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGridColumn;

import com.inmethod.grid.IGridColumn;

/**
 * <p>This panel is used to display menu items (in a table) that are 
 * linked to a parent object.  It allows links to be added or 
 * removed as well as the display & modification of the linked 
 * objects.  A list of available items that can be added are provided
 * and by default it will only show items that are not already linked.  
 * </p>
 * 
 * @author jzb0608 - 07 May 2008
 * Modified by Dean Scott to Extend AbstractLinkingPanel
 * 
 */
public abstract class MenuLinkingPanel<MODEL extends IMaintenancePageModel> 
	extends AbstractLinkingPanel<MODEL,MenuItemDTO,MenuItemDTO> implements IStatefullComponent {

	/* Constants */
	private static final long serialVersionUID = -8003453537906825676L;

	/**
	 * Default constructor
	 * 
	 */
	public MenuLinkingPanel(String id, MODEL pageModel,
			EditStateType editState) {
		super(id, pageModel, editState, new ChoiceRenderer("menuItemDescription","menuItemID"));
	}
	
	/**
	 * Check whether a menu item already exists from a role
	 * @return
	 */
	private boolean isDuplicateMenuItem(MenuItemDTO item){		
		List<MenuItemDTO>  roleItems = this.getNotSelectableAdditionalLinkedItemList();
		if(roleItems != null)
		for(MenuItemDTO extraItem : roleItems){
			if(item.getProfileRoleDTO() == null && extraItem.getMenuItemID() == item.getMenuItemID()){
				return true;
			}
		}		
		return false;
	}
	
	
	/**
	 * Added by Dean(DZS2610) 24 July 2008
	 */
	@Override
	protected List<IGridColumn> getLinkedItemGridColumns() {
		List<IGridColumn> columns = new ArrayList<IGridColumn>();		
		//add the modify access checkbox		
		columns.add(new SRSDataGridColumn<MenuItemDTO>("menuItemDescription",new Model("Menu Item"),"menuItemLongDescription","menuItemLongDescription",editState){
			private static final long serialVersionUID = 1L;
			@Override
			public Panel newCellPanel(WebMarkupContainer parent, String componentId, IModel rowModel, String objectProperty, EditStateType state, MenuItemDTO data) {
				Label lab = new Label("value",new PropertyModel(data,objectProperty));
				if(isDuplicateMenuItem(data)){
					lab.add(new AttributeModifier("class","red"));//lab.add(new SimpleAttributeModifier("class","red"));
					//lab.add(new SimpleAttributeModifier("title","Please note: This menu item already exists in a currently selected role"));
					lab.add(new AttributeModifier("title","Please note: This menu item already exists in a currently selected role"));
				}
				return HelperPanel.getInstance(componentId,lab);	
			}				
		}.setInitialSize(250));
		
//		Only display the permission source if it is not in the role screen
		if(!(pageModel instanceof RolesModel)){
			columns.add(new SRSDataGridColumn<MenuItemDTO>("permissionSource",new Model("Permission source"),"permissionSource","permissionSource",editState).setInitialSize(150));
		}
		columns.add(new SRSDataGridColumn<MenuItemDTO>("modifyAccess",new Model("Modify Access"),"modifyAccess",editState){
			private static final long serialVersionUID = 1L;
			@Override
			public Panel newCellPanel(WebMarkupContainer parent, String componentId, IModel rowModel, String objectProperty, EditStateType state, MenuItemDTO data) {
				MenuItemDTO original = MenuLinkingPanel.this.getFullOriginalLinkedItemMap().get(data.getMenuItemID());		
				CheckBox box = new CheckBox("value",new PropertyModel(rowModel.getObject(),objectProperty));
				updateComponentToUpdateWithAjax(box,"click");
				Panel panel = HelperPanel.getInstance(componentId,box,true);
				boolean isEditable = state != EditStateType.VIEW;
				box.setEnabled(isEditable && original.isModifyAccess() && data.getProfileRoleDTO() == null);
				return panel;
			}			
		}.setInitialSize(100));		
		//add the add access checkbox
		columns.add(new SRSDataGridColumn<MenuItemDTO>("addAccess",new Model("Add Access"),"addAccess",editState){
			private static final long serialVersionUID = 1L;
			@Override
			public Panel newCellPanel(WebMarkupContainer parent, String componentId, IModel rowModel, String objectProperty, EditStateType state, MenuItemDTO data) {
				MenuItemDTO original = MenuLinkingPanel.this.getFullOriginalLinkedItemMap().get(data.getMenuItemID());
				CheckBox box = new CheckBox("value",new PropertyModel(rowModel.getObject(),objectProperty));
				updateComponentToUpdateWithAjax(box,"click");
				Panel panel = HelperPanel.getInstance(componentId,box,true);
				boolean isEditable = editState != EditStateType.VIEW;
				box.setEnabled(isEditable && original.isAddAccess() && data.getProfileRoleDTO() == null);
				return panel;
			}
			
		}.setInitialSize(100));
		
		//add the delete checkbox column
		columns.add(new SRSDataGridColumn<MenuItemDTO>("deleteAccess",new Model("Delete Access"),"deleteAccess",editState){
			private static final long serialVersionUID = 1L;
			@Override
			public Panel newCellPanel(WebMarkupContainer parent, String componentId, IModel rowModel, String objectProperty, EditStateType state, MenuItemDTO data) {
				MenuItemDTO original = MenuLinkingPanel.this.getFullOriginalLinkedItemMap().get(data.getMenuItemID());
				CheckBox box = new CheckBox("value",new PropertyModel(rowModel.getObject(),objectProperty));
				updateComponentToUpdateWithAjax(box,"click");
				Panel panel = HelperPanel.getInstance(componentId,box,true);
				boolean isEditable = editState != EditStateType.VIEW;
				box.setEnabled(isEditable && original.isDeleteAccess() && data.getProfileRoleDTO() == null);					
				return panel;
			}
			
		}.setInitialSize(100));		
		return columns;
	}
	
	@Override
	protected MenuItemDTO createNewLinkedItem(MenuItemDTO dto) {
		MenuItemDTO newDto = new MenuItemDTO();
		newDto.setMenuItemID(dto.getMenuItemID());
		newDto.setMenuItemDescription(dto.getMenuItemDescription());		
		return newDto;
	}

	@Override
	protected Object getKeyForAvailableItem(MenuItemDTO item) {
		return item.getMenuItemID();
	}

	@Override
	protected Object getKeyForLinkedItem(MenuItemDTO item) {		
		return item.getMenuItemID();
	}
	

	/**
	 * Implementation of {@link IStatefullComponent#getEditState()}
	 */
	public EditStateType getEditState() {
		return editState;
	}

	/**
	 * Return the list of currently selected items
	 * @return
	 */
	protected abstract List<MenuItemDTO> getCurrentlyLinkedItemList();
	
	/**
	 * Get comparator for sorting the items
	 * 
	 * @return
	 */
	protected Comparator<? super MenuItemDTO> getLinkedItemComparator() {
		return new Comparator<MenuItemDTO>() {
			public int compare(MenuItemDTO o1, MenuItemDTO o2) {
				return o1.getMenuItemDescription().compareTo(
						o2.getMenuItemDescription());
			}
		};
	}
	
	/**
	 * Get comparator for sorting the items
	 * 
	 * @return
	 */
	protected Comparator<? super MenuItemDTO> getAvailableItemComparator() {
		return getLinkedItemComparator();
	}
	
	/**
	 * Get list of all available items
	 * 
	 * @return
	 */
	protected abstract List<MenuItemDTO> getCompleteAvailableItemList();	
}
