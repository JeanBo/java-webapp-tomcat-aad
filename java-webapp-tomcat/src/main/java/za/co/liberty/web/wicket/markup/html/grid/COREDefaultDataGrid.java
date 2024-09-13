package za.co.liberty.web.wicket.markup.html.grid;

import java.util.List;

import org.apache.wicket.model.IModel;

import com.inmethod.grid.IDataSource;
import com.inmethod.grid.IGridColumn;
import com.inmethod.grid.datagrid.DataGrid;
import com.inmethod.grid.toolbar.NoRecordsToolbar;

public class COREDefaultDataGrid extends DataGrid{
	 private static final long serialVersionUID = 1L;
	 
	    public COREDefaultDataGrid(String id, IModel model, List<IGridColumn> columns)
	   {
	      super(id, model, columns);
	      init();
	    }
	  
	    public COREDefaultDataGrid(String id, IDataSource dataSource, List<IGridColumn> columns)
	    {
	      super(id, dataSource, columns);
	     init();
	    }
	  
	    private void init() {
	      addBottomToolbar(new NoRecordsToolbar(this));
	   //   addBottomToolbar(new PagingToolbar(this));
	    }
}
