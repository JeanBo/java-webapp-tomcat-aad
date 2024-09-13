package za.co.liberty.web.pages.panels;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.cycle.RequestCycle;

import com.inmethod.grid.DataProviderAdapter;
import com.inmethod.grid.IGridColumn;
import com.inmethod.grid.SizeUnit;

import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.data.pages.IMaintenancePageModel;
import za.co.liberty.web.pages.interfaces.IMaintenanceParent;
import za.co.liberty.web.wicket.markup.html.grid.SRSCheckBoxColumn;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGrid;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGrid.GridSizeUnit;
import za.co.liberty.web.wicket.markup.html.grid.SRSGridRowSelectionCheckBox;
import za.co.liberty.web.wicket.markup.repeater.data.ListDataProvider;

/**
 * This selection panel has a selection list with a table that gets refreshed when the list is updated.  You then have to select
 * an item on the table to select a value.
 * 
 * <p>Works a bit differently from the combo selection panel as the combo object can be different from the 
 * table list object.</p>
 * 
 * <p>2023 JZB0608 - Updated to support a table that does not result in selection, table is named infoTable.  Only one table
 * can be shown at a time for now.  When selection is disabled different types of objects may be shown in the table.  This is 
 * determined per selection list type ({@linkplain AbstractTableMaintenanceSelectionPanel#getSelectionList()} </p>
 * 
 * doSelectionListField_onChange
 * 
 * @author JZB0608 - 30 Oct 2010
 * @author JZB0608 - June 2023 - Updated with support for non selectable table data for info purposes only.
 *
 * @param <DTO>
 */
public abstract class AbstractTableMaintenanceSelectionPanel <DTO extends Object>
	extends DefaultMaintenanceSelectionPanel<DTO>  {

	private static final long serialVersionUID = 1L;
	protected List<Object> tableList;

	protected Panel tableField;
	private transient Logger logger = Logger.getLogger(this.getClass());
	// Used to temporarily store the selected combo item.
	protected Object selectedObject;
	
	/**
	 * Calls default constructor {@linkplain #DefaultMaintenanceSelectionPanel(String, IMaintenancePageModel, IMaintenanceParent, Form, Class, null, null)}
	 * Remember to override {@linkplain #getChoiceRenderer()} when renderId and renderValue is null as 
	 * an exception will be throw if you don't.
	 * 
	 * @param id
	 * @param listLabel
	 * @param pageModel
	 * @param parent
	 * @param enclosingForm
	 * @param dtoType
	 */
	@SuppressWarnings("unchecked")
	public AbstractTableMaintenanceSelectionPanel(String id, String listLabel, IMaintenancePageModel<DTO> pageModel, 
			IMaintenanceParent parent, Form enclosingForm, Class dtoType) {
		this(id, listLabel, pageModel, parent, enclosingForm, dtoType, null, null);
	}

	/**
	 * Default constructor
	 * 
	 * @param id
	 * @param pageModel
	 * @param parent
	 * @param enclosingForm
	 * @param dtoType
	 * @param renderValue
	 * @param renderId
	 */
	public AbstractTableMaintenanceSelectionPanel(String id, String listLabel, IMaintenancePageModel<DTO> pageModel, 
			IMaintenanceParent parent, Form enclosingForm, Class<DTO> dtoType,
			String renderValue, String renderId) {
		super(id, listLabel, pageModel, parent, enclosingForm, dtoType, renderId, renderValue);
	}
	
	/**
	 * Add the components to the form and must be called after the constructor 
	 * is run. Allows additional attributes to be set before create the
	 * components.
	 */
	protected void initialiseForm() {
		tableList = new ArrayList<Object>();
		add(listDescriptionField = createListDescriptionLabel("listDescription"));
		add(selectionListPanel = HelperPanel.getInstance("selectionPanel",
				selectionListField=createSelectionListField("value")));
		add(buttonPanel = createControlButtonPanel());
		add(tableField = createTableField("tablePanel"));
		setEditState(this.getEditState(), null);
//		super.initialiseForm();
	}

	/**
	 * The onchange behaviour of the selection list field (combo box)
	 * @param target
	 */
	protected void doSelectionListField_onChange(AjaxRequestTarget target) {
		/* We will now refresh the table data here */
		List<Object> tempList = filterTableData();
		tableList.clear();
		tableList.addAll(tempList);
	
		target.add(tableField);
	}
	
	/**
	 * Override this method to filter the table data here
	 * 
	 * @param target
	 */
	public abstract List<Object> filterTableData();
	
	
	
	/**
	 * Implement this method to create your table
	 */
	@SuppressWarnings("unchecked")
	public Panel createTableField(String id) {

		if (logger.isDebugEnabled())
			logger.debug("CreateTableField");
		
		SRSDataGrid tempDataGrid = new SRSDataGrid(id,new DataProviderAdapter(
						new ListDataProvider(tableList)),createInternalTableFieldColumns(),getEditState()) {
							private static final long serialVersionUID = 1L;

			@Override
			public void update() {
				if (logger.isDebugEnabled())
					logger.debug("CreateTableField-update event - tableSelectionAllowed=" + isAllowTableSelection());
				if (isAllowTableSelection()) {
					int cnt = this.getSelectedItems().size();
					if (cnt==0) {
						pageModel.setSelectedItem(null);
						doTableField_onSelect();
					} else if (cnt==1) {
						pageModel.setSelectedItem((DTO) this.getSelectedItemObjects().get(0));
						doTableField_onSelect();
					}
				}
				super.update();
			}				
			
		};
		tempDataGrid.setAutoResize(true);
		tempDataGrid.setOutputMarkupId(true);
		tempDataGrid.setCleanSelectionOnPageChange(false);
		tempDataGrid.setClickRowToSelect(false);
		tempDataGrid.setAllowSelectMultiple(false);
		tempDataGrid.setGridWidth(98, GridSizeUnit.PERCENTAGE);		
		tempDataGrid.setRowsPerPage(15);
		tempDataGrid.setContentHeight(200, SizeUnit.PX);
		return tempDataGrid;
	}

	/**
	 * Swap the table field.  Re-initialise and then swap.
	 * 
	 * @param target
	 */
	public void swapTableField(AjaxRequestTarget target) {
		if (logger.isDebugEnabled())
			logger.debug("swapTableField");
		Panel panel = createTableField("tablePanel");
		tableField.replaceWith(panel);
		tableField = panel;
		if (target!=null) {
			target.add(tableField);
		}
	}
	
	/**
	 * Return all the columns that are required.  This includes the check box.
	 * @return
	 */
	protected List<IGridColumn> createInternalTableFieldColumns() { 
		List<IGridColumn> columns = new ArrayList<IGridColumn>();
		
		if (logger.isDebugEnabled())
			logger.debug("CreateInternalTbleFieldColumns = " + isAllowTableSelection() + " for ");
		
		// Only add the check box when selection is allowed
		if (isAllowTableSelection()) {
			SRSCheckBoxColumn col = new SRSCheckBoxColumn("checkBox");
			col.setInitialSize(30);
			columns.add(col);
			
		}
		
		// Add all the other columns
		columns.addAll(createTableFieldColumns());
		
		return columns;
	}
	
	/**
	 * Return all the additional columns required.
	 * 
	 * @return
	 */
	public abstract List<IGridColumn> createTableFieldColumns();
	
	/**
	 * 
	 */
	public void doTableField_onSelect() {
		parent.setEditState(getEditState(), RequestCycle.get().find(AjaxRequestTarget.class));
		parent.swapContainerPanel(RequestCycle.get().find(AjaxRequestTarget.class));
		parent.swapNavigationPanel(RequestCycle.get().find(AjaxRequestTarget.class));
	}
	
	/**
	 * Return the model required to get the selected item.  We have to override
	 * this to only set a local field.
	 * 
	 * @return
	 */
	@Override
	protected IModel getSelectedItemModel() {
		return new IModel() {

			private static final long serialVersionUID = 1L;

			public Object getObject() {
				return selectedObject;
			}

			public void setObject(Object object) {
				selectedObject = object;
			}

			public void detach() {
				
			}
			
		};
	}

	/**
	 * Provide the list of values 
	 */
	@SuppressWarnings("unchecked")
	@Override
	public abstract List getSelectionList();

	/**
	 * Update the edit state for this panel (enables / disables certain components)
	 */
	@Override
	public void setEditState(EditStateType newState, AjaxRequestTarget target) {
		super.setEditState(newState, target);
		/* Set component access */
		if (newState == EditStateType.VIEW) {
			tableField.setEnabled(true);
		} else {
			tableField.setEnabled(false);
		}
		
		/* Update components that might have changed */
		if (target != null) {
			target.add(tableField);
		}	
	}
	
	/**
	 * Create the button panel
	 * 
	 * @return
	 */
	@Override
	protected Panel createControlButtonPanel() {
		modifyButton = createModifyButton("button1");
		addNewButton = createAddNewButton("button2");
		List<Button> buttonList  = new ArrayList<Button>();
		buttonList.add(modifyButton);
		buttonList.add(addNewButton);
		createAdditionalControlButtons(2,buttonList);
		Panel panel = ButtonHelperPanel.getInstance("controlButtonPanel",
				buttonList.toArray(new Button[]{}));
		panel.setOutputMarkupId(true);
		return panel;
	}	
	
	/**
	 * This method should be overridden to add any additional control buttons that
	 * are required. 
	 * 
	 * @param lastbuttonNr
	 * @return
	 */
	protected void createAdditionalControlButtons(int lastbuttonNr, List<Button> buttonList) {
	}
	
	/**
	 * Override to enable or disable table selection.  Selection of a table entry may only be allowed on selection kinds that 
	 * have tableLists of the appropriate object type of this model.   InfoTable lists that does not allow selection may 
	 * store any objects.
	 * 
	 * @return
	 */
	protected boolean isAllowTableSelection() {
		return true;
	}
	
}
