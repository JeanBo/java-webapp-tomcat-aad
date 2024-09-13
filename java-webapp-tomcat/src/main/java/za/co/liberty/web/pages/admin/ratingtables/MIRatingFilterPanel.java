package za.co.liberty.web.pages.admin.ratingtables;


import java.util.ArrayList;
import java.util.List;

import javax.naming.NamingException;

import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.cycle.RequestCycle;

import com.inmethod.grid.DataProviderAdapter;
import com.inmethod.grid.IGridColumn;
import com.inmethod.grid.SizeUnit;
import com.inmethod.grid.column.PropertyColumn;

import za.co.liberty.business.guicontrollers.ratingtable.IMIRatingTableGUIController;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.helpers.lookup.ServiceLocator;
import za.co.liberty.interfaces.rating.IGuiRatingRow;
import za.co.liberty.interfaces.rating.MIRatingTableColumnLayoutType;
import za.co.liberty.interfaces.rating.MIRatingTableColumnType;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.pages.MaintenanceBasePage;
import za.co.liberty.web.pages.admin.models.RatingTablePageModel;
import za.co.liberty.web.pages.interfaces.IChangeableStatefullComponent;
import za.co.liberty.web.pages.panels.BasePanel;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGrid;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGrid.GridSizeUnit;
import za.co.liberty.web.wicket.markup.html.grid.SRSGridRowSelectionCheckBox;
import za.co.liberty.web.wicket.markup.repeater.data.ListDataProvider;

/**
 * Main filter panel for Rating table admin.  Shows a filter section, table and a selection panel.
 *
 */
public class MIRatingFilterPanel extends BasePanel implements IChangeableStatefullComponent {

	private static final long serialVersionUID = 9032746185200994039L;
	private static final String EDIT_PANEL_ID = "editPanel";
	private static final String FILTER_PANEL_ID = "filterPanel";
	private static final String TABLE_PANEL_ID = "tablePanel";
	private transient IMIRatingTableGUIController guiController;
	
	private EditStateType editState;
	private Page parentPage;
	
	FilterForm filterForm;
	private Panel filterPanel;
	private Panel tablePanel;
	private Panel editPanel;
	RatingTablePageModel pageModel;
	String tableName = "";
	
	public MIRatingFilterPanel(String id, EditStateType editState,
			RatingTablePageModel pageModel, Page parentPage) {
		super(id, editState, parentPage);
		this.pageModel = pageModel; 
		this.editState = editState;
		this.parentPage = parentPage;
		tableName = pageModel.getSelectedItem().getTableName();
		if(null != editPanel){
			editPanel.remove();
		}
		if(!editState.equals(EditStateType.AUTHORISE))
			pageModel.setSelectionRow(null);
		updatePageModel();
		add(filterForm = new FilterForm("filterForm"));
	}


	private void updatePageModel() {
		// Initialize initial list
//		---------------------------------------------------------------------------------------------------------------------------
			pageModel.setRowList(getGUIController().getTableData(tableName));
//		---------------------------------------------------------------------------------------------------------------------------
	}


	public class FilterForm extends Form {
		private static final long serialVersionUID = 1L;
		public FilterForm(String id) {
			super(id);
			this.add(filterPanel = createFilterPanel(FILTER_PANEL_ID));
			if(editState.equals(EditStateType.AUTHORISE)){
				this.add(tablePanel = new EmptyPanel(TABLE_PANEL_ID));
				this.add(editPanel = createEditPanel(EDIT_PANEL_ID));
				doSelectRowItem();
			}else{
				this.add(tablePanel = createTablePanel(TABLE_PANEL_ID));
				this.add(editPanel = createEditPanel(EDIT_PANEL_ID));
			}
			
		}
	}
	
	public Panel createFilterPanel(String id) {
		Panel panel = null;
		panel = new EmptyPanel(id);
		return panel;
	}

	public void doTableField_onSelect() {

	}	
	
	public Panel createTablePanel(String id) {
		SRSDataGrid tempDataGrid = new SRSDataGrid(id,new DataProviderAdapter(
				new ListDataProvider(pageModel.getRowList())),createInternalTableFieldColumns(),getEditState()) {
					private static final long serialVersionUID = 1L;
			@Override
			public void update() {
				int cnt = this.getSelectedItems().size();
				if (cnt==0) {
					pageModel.setSelectionRow(null);
					doSelectRowItem();
				} else if (cnt==1) {
					pageModel.setSelectionRow((IGuiRatingRow) this.getSelectedItemObjects().get(0));
					pageModel.setGuiRatingRowBeforeImage(pageModel.getSelectionRow());
					doSelectRowItem();						
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
		tempDataGrid.setRowsPerPage(50);
		tempDataGrid.setContentHeight(300, SizeUnit.PX);
		//tempDataGrid.setEnabled(!getEditState().isViewOnly() && !getEditState().equals(EditStateType.MODIFY) && !getEditState().equals(EditStateType.ADD));
		return tempDataGrid;
	}
	
	protected void doSelectRowItem() {
		Panel panel = createEditPanel(EDIT_PANEL_ID);
		editPanel.replaceWith(panel);
		editPanel = panel;		
	
		AjaxRequestTarget target = RequestCycle.get().find(AjaxRequestTarget.class);
		if (target!=null) {
			target.add(editPanel);
			((MaintenanceBasePage)parentPage).swapNavigationPanel(target);
		}
	}	
	

	private List<IGridColumn> createInternalTableFieldColumns() {  
		List<IGridColumn> colList = new ArrayList<IGridColumn>();
		SRSGridRowSelectionCheckBox col = new SRSGridRowSelectionCheckBox("checkBox");
		col.setInitialSize(30);
		colList.add(col);
//		If not populating table columns please check table name in ENUM.
		for (MIRatingTableColumnType c : MIRatingTableColumnLayoutType.getTypeFromName(tableName).getColumnEnums()) {
			colList.add(new PropertyColumn(new Model(c.getColumnName()), c.getColumnId(), c.getColumnId()).setInitialSize(c.getSize()));
		}
	
		return colList;
	}


	public Panel createEditPanel(String id) {
		Panel panel = null;
		if (pageModel.getSelectionRow() == null) {
			panel = new EmptyPanel(id);
		} else {
//			-----------------------------------------------------------------------------------------------------
			if(MIRatingTableColumnLayoutType.ADDITIONAL_MI_HIERARCHY_VIEWS.getTableName().equals(tableName)){
				panel = new AdditionalHierarchyViewsPanel(id, editState, pageModel, parentPage);
							
			}
//			-----------------------------------------------------------------------------------------------------
			else if(MIRatingTableColumnLayoutType.MI_HIERARCHY_NODE_CHARACTERISTICS.getTableName().equals(tableName)){
				panel = new HierarchyNodeCharacteristicsPanel(id, editState, pageModel, parentPage);
				
//			-----------------------------------------------------------------------------------------------------
			}
			else if(MIRatingTableColumnLayoutType.HIERARCHY_NODE_ADDRESS_CLASSIFICATION.getTableName().equals(tableName)){
				panel = new HierarchyAddressClassificationPanel(id, editState, pageModel, parentPage);
//			-----------------------------------------------------------------------------------------------------
			}
		}
		panel.setOutputMarkupId(true);
		return panel;
	}
	
	protected IMIRatingTableGUIController getGUIController() {
		if (guiController == null) {
			try {
				guiController = ServiceLocator.lookupService(IMIRatingTableGUIController.class);
			} catch (NamingException namingErr) {
				CommunicationException comm = new CommunicationException("IMIRatingTableGUIController can not be looked up!");
				throw new CommunicationException(comm);
			} 
		}
		return guiController;
	}


	public void setEditState(EditStateType newState, AjaxRequestTarget target) {
		editState = newState;
		doSelectRowItem();
		
	}
}

