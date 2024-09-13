package za.co.liberty.web.pages.examples.helloworld;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.repeater.data.ListDataProvider;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;

import com.inmethod.grid.DataProviderAdapter;
import com.inmethod.grid.IGridColumn;
import com.inmethod.grid.SizeUnit;
import com.inmethod.grid.column.PropertyColumn;
import com.inmethod.grid.datagrid.DataGrid;
import com.inmethod.grid.datagrid.DefaultDataGrid;

import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGrid;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataProviderAdapter;
import za.co.liberty.web.wicket.markup.repeater.data.SortableListDataProvider;

public class GridTestPage extends WebPage {
    public GridTestPage() {
    	System.out.println("Hey");
        add(new Label("message", "Hello World! - Why " + new Date()));
        
//        add(getGrid1("grid1"));
        add(getGridOther("grid1"));
//        add(new EmptyPanel("grid1"));
        
//        add(new EmptyPanel("grid2"));
        add(getGrid1("grid2"));
        
        add(getGrid3("grid3"));
        
        add(createButton("but1"));
    }
    
    private Component createButton(String id) {
    	Button buttonObj = new Button(id) {
			private static final long serialVersionUID = -669885354868892015L;
		
			@Override
			protected void onComponentTag(ComponentTag tag) {
				super.onComponentTag(tag);
				tag.getAttributes().put("value", "Calculate");
			}
    	};
    	buttonObj.add(new AjaxEventBehavior("click") {
			
			private static final long serialVersionUID = 1L;

			@Override
			protected void onEvent(AjaxRequestTarget target) {
				System.out.println("click");
			}
		});
    	return buttonObj;

	}

	private Component getGrid3(String id) {
    	
    	List columns = new ArrayList();

    	columns.add(new PropertyColumn (
    			new Model<String>("id"), "id"));
    	columns.add(new PropertyColumn(new Model<String>(
    			"First Name"), "firstName", "firstName"));
		columns.add(new PropertyColumn(new Model<String>(
			"Last Name"), "lastName", "lastName"));
		columns.add(new PropertyColumn(new Model<String>(
			"Home Nr"), "homePhone"));
		columns.add(new PropertyColumn(new Model<String>(
			"Cell Nr"), "cellPhone"));
    	
    	
//    	SRSDataGrid grid = new SRSDataGrid(id, new SRSDataProviderAdapter(
//				new SortableListDataProvider(ContactDOT.getExampleList())), 
//				new ArrayList<IGridColumn>(), EditStateType.VIEW);
//    	new ListDataProvider<ContactDOT>(list)
    	
		
//    	SRSDataGrid grid = new SRSDataGrid(id, new SRSDataProviderAdapter(
//				new ListDataProvider<ContactDOT>(ContactDOT.getExampleList())), 
//				columns, EditStateType.VIEW);
//    	
    	SRSDataGrid grid = new SRSDataGrid(id, new SRSDataProviderAdapter(
				new SortableListDataProvider<ContactDOT>(ContactDOT.getExampleList())), 
				columns, EditStateType.VIEW);
    	
    	
		grid.setAutoResize(false);
		grid.setRowsPerPage(10);
		grid.setContentHeight(200, SizeUnit.PX);
		grid.setAllowSelectMultiple(true);
		grid.setCleanSelectionOnPageChange(true);
		grid.setGridWidth(500, SRSDataGrid.GridSizeUnit.PIXELS);
		return grid;
	}

	public DataGrid getGridOther(String id) {
    	
    	final List<ContactDOT> personList = ContactDOT.getExampleList();
		final ListDataProvider listDataProvider = new ListDataProvider(personList);
			//define grid's columns
		List<IGridColumn> cols = (List) Arrays.asList(
		             new PropertyColumn(new Model("First Name"), "firstName"),
		             new PropertyColumn(new Model("Last Name"), "lastName"));

		DataGrid grid = new DefaultDataGrid(id, new DataProviderAdapter(listDataProvider), cols);
		grid.setContentHeight(300, SizeUnit.PX);
		grid.setRowsPerPage(10);
		return grid;
    }
    
    public DataGrid getGrid1(String id) {

    	
//    	List<IGridColumn<IDataSource<ContactDOT>, ContactDOT, ContactDOT>> columns = new ArrayList<IGridColumn<IDataSource<ContactDOT>, ContactDOT, ContactDOT>>();
    	
    	List columns = new ArrayList();
    	
    	
    	columns.add(new PropertyColumn (
    			new ResourceModel("id"), "id"));
    	
//    	columns.add(new PropertyColumn (
//    			new ResourceModel("id"), "id"));
    	columns.add(new PropertyColumn(new Model<String>(
    			"firstName"), "firstName", "firstName"));
		columns.add(new PropertyColumn(new Model<String>(
			"lastName"), "lastName", "lastName"));
		columns.add(new PropertyColumn(new Model<String>(
			"homePhone"), "homePhone"));
		columns.add(new PropertyColumn(new Model<String>(
			"cellPhone"), "cellPhone"));
    	
//    	columns.a
//		columns.add(new PropertyColumn<IDataSource<ContactDOT>, ContactDOT, Long, Long>(
//			new ResourceModel("id"), "id"));
//		columns.add(new PropertyColumn<IDataSource<ContactDOT>, ContactDOT, String, String>(new ResourceModel(
//			"firstName"), "firstName", "firstName"));
//		columns.add(new PropertyColumn<IDataSource<ContactDOT>, ContactDOT, String, String>(new ResourceModel(
//			"lastName"), "lastName", "lastName"));
//		columns.add(new PropertyColumn<IDataSource<ContactDOT>, ContactDOT, String, String>(new ResourceModel(
//			"homePhone"), "homePhone"));
//		columns.add(new PropertyColumn<IDataSource<ContactDOT>, ContactDOT, String, String>(new ResourceModel(
//			"cellPhone"), "cellPhone"));

//		final DataGrid<IDataSource<ContactDOT>, ContactDOT, ContactDOT> grid = new DefaultDataGrid<IDataSource<ContactDOT>, ContactDOT, ContactDOT>(
//			"grid", new DataProviderAdapter<T, S>(new SortableDataProvider<T, S>()) {
//			}), columns);
//		final DataGrid  grid = new DefaultDataGrid (
//				"grid", new DataProviderAdapter<T, S>(new SortableDataProvider<T, S>()) {
//				}, columns), columns);

		final DataGrid  grid = new DefaultDataGrid(id, new DataProviderAdapter(new ListDataProvider<ContactDOT>(
				ContactDOT.getExampleList())), columns);
		grid.setRowsPerPage(10);
		return grid;
    }
}

