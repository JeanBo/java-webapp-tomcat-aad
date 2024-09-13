package za.co.liberty.web.pages.panels;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.PropertyModel;

import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.data.pages.IMaintenancePageModel;
import za.co.liberty.web.pages.interfaces.IStatefullComponent;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGrid;
import za.co.liberty.web.wicket.markup.html.grid.SRSGridRowSelectionCheckBox;
import za.co.liberty.web.wicket.markup.repeater.data.SortableListDataProvider;

import com.inmethod.grid.DataProviderAdapter;
import com.inmethod.grid.IGridColumn;
import com.inmethod.grid.datagrid.DataGrid;

/**
 * <p> This panel is used to display items (in a table) that are linked to a
 * parent object. It allows links to be added or removed as well as the display &
 * modification of the linked objects in the table. A list of available items that 
 * can be added are provided and by default it will only show items that are not
 * already linked in the table. </p>
 * 
 * <p>The list of available items are shown in a combo box which is only visible in
 * add or modification mode.  Items that can be selected from the combo box and added
 * to the selected list which is shown in a table (all edit modes).  The table and combo
 * lists are mutually exclusive so no item that is linked in the table will be shown 
 * in the combo box etc.</p>
 * 
 * <p>Note:  Please ensure that that the methods {@linkplain #getKeyForAvailableItem(Object)} and
 * {@linkplain #getKeyForLinkedItem(Object)} return the same object type or you will have
 * class cast exceptions.</p>
 * 
 * @author jzb0608 - 16 May 2008 - Initial
 * Modified by Dean to include the Datagrid 23 July 2008
 * Modified by Dean to have non selectable extra items 12 August 2008
 * @author jzb0608 - 18 Nov 2009 - Refactor generic parameters names, method names etc. to make the code
 * 		easier to read and implement.  Also fixed some minor bugs.
 *
 * @param <MODEL> 			Defines the type for the pageModel 
 * @param <AVAILABLE_ITEM>	Defines the type for the list of available items that are shown 
 * 			in the combo box.
 * @param <LINKED_ITEM>		Defines the type for the list of linked items that are shown in
 * 			the grid table.
 */
public abstract class AbstractLinkingPanel<MODEL extends IMaintenancePageModel, AVAILABLE_ITEM extends Object, LINKED_ITEM extends Object>
		extends Panel implements IStatefullComponent {

	/* Constants */
	private static final long serialVersionUID = -8003453537906825676L;
	public static final String FORM_NAME = "panelForm";

	/* Other */
	protected EditStateType editState;
	protected Object bean;
	protected MODEL pageModel;
	protected Form form;
	protected IChoiceRenderer renderer;
	protected SRSDataGrid grid;
	private int removeColSize = 30;//30 pixels by default
	
	/* Attributes */
	private AVAILABLE_ITEM selectedAvailableItem;
	
	/* Complete list of menu items that are available */
	private HashMap<Object, AVAILABLE_ITEM> fullOriginalItemMap;
	
	private ArrayList<AVAILABLE_ITEM> availableList;
	
	/* Extra items */
	private List<LINKED_ITEM> extras;

	/**
	 * Default constructor
	 * 
	 */
	public AbstractLinkingPanel(String id, MODEL pageModel,
			EditStateType editState, IChoiceRenderer listRenderer) {
		super(id);
		
		/* Init properties */
		this.editState = editState;
		this.pageModel = pageModel;
		this.bean = pageModel.getSelectedItem();
		this.renderer = listRenderer;
		
		/* Set up internal collections */		
		fullOriginalItemMap = new HashMap<Object, AVAILABLE_ITEM>();
		for (AVAILABLE_ITEM item : (List<AVAILABLE_ITEM>) getCompleteAvailableItemList()) {			
			fullOriginalItemMap.put(getKeyForAvailableItem(item), item);
		}
		availableList = new ArrayList<AVAILABLE_ITEM>(getCompleteAvailableItemList());
			
		//Remove selected menu items from the available list
		for (int i = 0; i < availableList.size(); ++i) {
			AVAILABLE_ITEM item = availableList.get(i);
			for (LINKED_ITEM subItem : getCurrentlyLinkedItemList()) {
				if (getKeyForAvailableItem(item).equals(getKeyForLinkedItem(subItem))) {
					availableList.remove(i);
					--i;
				}
			}
		}
		
		Collections.sort(availableList, getAvailableItemComparator());
		Collections.sort(getCurrentlyLinkedItemList(), getLinkedItemComparator());
		
		/* Components */
		this.add(form = new MainForm(FORM_NAME));		
	}
	
	/**
	 * Create a datagrid panel with data supplied by getColumns method
	 * 
	 * @param id
	 * @return
	 */
	SRSDataGrid createLinkedItemGridField(String id){		
		List<IGridColumn> cols = getLinkedItemGridColumns();
		
		//add the remove col
		if(getEditState() != EditStateType.VIEW){
			SRSGridRowSelectionCheckBox col = new SRSGridRowSelectionCheckBox("checkBox");			
			cols.add(0,col.setInitialSize(this.getRemoveColSize()));
		}	
		extras = getNotSelectableAdditionalLinkedItemList();
		DataProviderAdapter gridDataProvider = new DataProviderAdapter(
				new SortableListDataProvider<LINKED_ITEM>(getCurrentlyLinkedItemList()){
					private static final long serialVersionUID = 1L;
					/**
					 * Overriden to include the extras that are not part of the modification
					 */
					@SuppressWarnings("unchecked")
					@Override
					protected List<LINKED_ITEM> getGridData() {		
						//extras = getExtraItemList();
						ArrayList<LINKED_ITEM> ret = new ArrayList<LINKED_ITEM>(getCurrentlyLinkedItemList());
						if(extras != null && extras.size() > 0){														
							ret.addAll(extras);		
						}
						Collections.sort(ret, getLinkedItemComparator());												
						return ret;
					}					
				});
		
		//extras list should not change						
		grid = new SRSDataGrid(id, gridDataProvider, 
				cols, getEditState(),extras){					
					private static final long serialVersionUID = 1L;

					/**
					 * Overriding as the list can change
					 */
					@SuppressWarnings("unchecked")
					@Override
					public Map<Object, Object> getNonSelectableRowObjects() {						
						Map<Object, Object> map = super.getNonSelectableRowObjects();
						map.clear();						
						if(extras != null){
							for(LINKED_ITEM item : extras){
								map.put(item, item);
							}
						}
						return map;
					}				

					@Override
					protected void onBeforeRender() {
						//get the extras list before render as 
						//they might have changed due to roles added or removed
						extras = getNotSelectableAdditionalLinkedItemList();
						super.onBeforeRender();
					}			
		};		
		grid.setCleanSelectionOnPageChange(false);
		grid.setRowsPerPage(100);
		grid.setClickRowToSelect(false);	
		grid.setAllowSelectMultiple(true);	
		grid.setGridWidth(getGridWidth(),SRSDataGrid.GridSizeUnit.PIXELS);		
		//grid.setAutoResize(false);
		return grid;
	}	
	
	/**
	 * This method should be overridden to change the grid width
	 * The grid defaults to 640px
	 * @return
	 */
	public int getGridWidth(){
		return 640;
	}
	
	/**
	 * gets the Datagrid for the panel so the users may adjust sizes etc
	 * @return
	 */
	protected DataGrid getDataGrid(){
		return grid;
	}
	
	/**
	 * Users of this panel must send back the list of columns for use with the Grid
	 * 
	 * @return
	 */	
	protected abstract List<IGridColumn> getLinkedItemGridColumns();
	
	/**
	 * gets the size in pixels that the remove column should be
	 * @return
	 */
	protected int getRemoveColSize(){
		return removeColSize;
	}
	
	protected void setRemoveColSize(int removeColSize){
		this.removeColSize = removeColSize;
	}
	
	
	/**
	 * Form that contains all data for this panel
	 * 
	 * @author jzb0608 - 07 May 2008
	 * 
	 */
	public class MainForm extends Form {

		private static final long serialVersionUID = 1L;

		ListView selectedListViewField;
		DropDownChoice availableItemsListField;
		Button addButtonField;	
		Button removeButtonField;
		//AbstractLinkingTablePanel tablePanel;
		
		public MainForm(String id) {
			super(id);
			add(addButtonField = createAddItemButton("addItemButton"));
			//add(tablePanel = createTablePanel("tablePanel"));
			add(grid = AbstractLinkingPanel.this.createLinkedItemGridField("tablePanel"));
			add(availableItemsListField = createAvailableItemsList("availableItemsList"));
			add(removeButtonField = createRemoveButton("removeItemsButton"));
		}		

		private Button createRemoveButton(String id) {
			Button button = new Button(id);
			
			button.add(new AjaxFormComponentUpdatingBehavior("click") {
				private static final long serialVersionUID = 0L;
				
				@SuppressWarnings("unchecked")
				@Override
				protected void onUpdate(AjaxRequestTarget target) {					
					removeSelectedItems((List<LINKED_ITEM>)grid.getSelectedItemObjects());
					grid.resetSelectedItems();
					target.add(grid);//target.addComponent(grid);					
					target.add(availableItemsListField);//target.addComponent(availableItemsListField);					
				}
			});	
			button.setOutputMarkupId(true);
			if (editState == EditStateType.VIEW) {
				button.setVisible(false);
			}
			return button;
		}

		/**
		 * Add the selected items from the "available list" to the "selected
		 * list"
		 * 
		 * @param id
		 * @return
		 */
		Button createAddItemButton(String id) {
			final Button button = new Button(id);
			button.add(new AjaxFormComponentUpdatingBehavior("click") {
			private static final long serialVersionUID = 0L;
			
			@Override
			protected void onUpdate(AjaxRequestTarget target) {					
				AVAILABLE_ITEM dto = getSelectedAvailableItem();				
				moveItemToSelected(dto);
				selectedAvailableItem = null;
				button.setEnabled(false);					
				target.add(button);//target.addComponent(button);					
				target.add(grid);//target.addComponent(grid);
				target.add(availableItemsListField);//target.addComponent(availableItemsListField);				
			}
			});				
			button.setOutputMarkupId(true);
			button.setEnabled(false);

			if (editState == EditStateType.VIEW) {
				button.setVisible(false);
			}
			return button;
		}

		/**
		 * Get list of Menu Items that can still be selected
		 * 
		 * @param id
		 * @return
		 */
		DropDownChoice createAvailableItemsList(String id) {
			DropDownChoice list = new DropDownChoice(id, new PropertyModel(
					AbstractLinkingPanel.this, "selectedAvailableItem"),
					availableList, renderer);
			list.setOutputMarkupId(true);
			if (editState == EditStateType.VIEW) {
				list.setVisible(false);
			}
			/* Add behavior to enable addItemButton when selected */
			list.add(new AjaxFormComponentUpdatingBehavior("change") {
				private static final long serialVersionUID = 0L;

				@Override
				protected void onUpdate(AjaxRequestTarget target) {
					addButtonField.setEnabled(getSelectedAvailableItem()!=null);
					if (target!=null) {
						target.add(addButtonField);//target.addComponent(addButtonField);
					}
				}
			});
			
			list.setOutputMarkupId(true);
			return list;
		}

	}
	
	/**
	 * Removes all the passed in dto's from the grid and adds back to the available
	 * list.
	 * 
	 * @param dtos the list of DTO objects
	 */
	public void removeSelectedItems(List<LINKED_ITEM> removeList) {
		HashMap<AVAILABLE_ITEM, AVAILABLE_ITEM> map = new HashMap<AVAILABLE_ITEM, AVAILABLE_ITEM>();
		
		if(removeList != null){			
			List<LINKED_ITEM> linkedList = getCurrentlyLinkedItemList();			
			for(LINKED_ITEM linkedItem : removeList){				
					linkedList.remove(linkedItem);
					AVAILABLE_ITEM availableItem = fullOriginalItemMap.get(getKeyForLinkedItem(linkedItem));
					//check we arnt adding duplicates into the list
					if(map.get(availableItem) == null){
						availableList.add(availableItem);	
						map.put(availableItem, availableItem);
					}
			}
			Collections.sort(availableList, getAvailableItemComparator());
		}
		map = null;
	}

	/**
	 * Move an item from the available list to the selected list
	 * 
	 * @param dto
	 */
	public void moveItemToSelected(AVAILABLE_ITEM dto) {		
		List<LINKED_ITEM> linkedList = getCurrentlyLinkedItemList();
		availableList.remove(dto);
		linkedList.add(createNewLinkedItem(dto));
		Collections.sort(linkedList, getLinkedItemComparator());			
	}

	/**
	 * Get the currently selected item
	 * 
	 * @return
	 */
	public AVAILABLE_ITEM getSelectedAvailableItem() {
		return selectedAvailableItem;
	}

	/**
	 * Set the currently selected item
	 * 
	 * @param selectedAvailableItem
	 */
	public void setSelectedAvailableItem(AVAILABLE_ITEM selectedAvailableItem) {
		this.selectedAvailableItem = selectedAvailableItem;
	}

	/**
	 * Implementation of {@link IStatefullComponent#getEditState()}
	 */
	public EditStateType getEditState() {
		return editState;
	}

	/**
	 * Return a map with all the original items
	 * 
	 * @return
	 */
	public HashMap<Object, AVAILABLE_ITEM> getFullOriginalLinkedItemMap() {
		return fullOriginalItemMap;
	}
	
	
	/**
	 * Return the list of currently selected items
	 * @return
	 */
	protected abstract List<LINKED_ITEM> getCurrentlyLinkedItemList();
	
	/**
	 * Override and return the list of items you want displayed but not selectable or editable
	 * @return
	 */
	protected List<LINKED_ITEM> getNotSelectableAdditionalLinkedItemList(){
		return extras;
	}
	
	/**
	 * Get comparator for sorting the items
	 * 
	 * @return
	 */
	protected abstract Comparator<? super LINKED_ITEM> getLinkedItemComparator();

	/**
	 * Get comparator for sorting the available items
	 * 
	 * @return
	 */
	protected abstract Comparator<? super AVAILABLE_ITEM> getAvailableItemComparator();
	
	
	/**
	 * Get list of all available items
	 * 
	 * @return
	 */
	protected abstract List<AVAILABLE_ITEM> getCompleteAvailableItemList();
	
	/**
	 * Create a new DTO from the original
	 * 
	 * @param dto
	 * @return
	 */
	protected abstract LINKED_ITEM createNewLinkedItem(AVAILABLE_ITEM dto);
	
	/**
	 * Return the unique key object for the available item object
	 * @param item
	 * @return
	 */
	protected abstract Object getKeyForAvailableItem(AVAILABLE_ITEM item);
	
	/**
	 * Return the unique key object for the linked item object
	 * 
	 * @param item
	 * @return
	 */
	protected abstract Object getKeyForLinkedItem(LINKED_ITEM item);
	
	/**
	 * update the compoenent ot have an ajax update
	 * @param comp
	 * @param updateBehaviourAction This will be the onclick etc actions of the component
	 */
	protected void updateComponentToUpdateWithAjax(FormComponent comp, String updateBehaviourAction){
		comp.add(new AjaxFormComponentUpdatingBehavior(updateBehaviourAction){
			private static final long serialVersionUID = 1L;
			@Override
			protected void onUpdate(AjaxRequestTarget arg0) {
				//do nothing, auto update the model				
			}			
		});
	}

}
