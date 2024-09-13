package za.co.liberty.web.pages.maintainagreement.popup;

import java.util.List;

import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.basic.Label;

import za.co.liberty.web.pages.BaseWindowPage;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGrid;
import za.co.liberty.web.wicket.markup.repeater.data.SortableListDataProvider;

import com.inmethod.grid.DataProviderAdapter;
import com.inmethod.grid.IGridColumn;

/**
 * Page for showing a list of values in a pop-up
 */
public abstract class HistoryTablePopupPage extends BaseWindowPage {
	
	private static final long serialVersionUID = 1L;
	
	
	public HistoryTablePopupPage(ModalWindow modalWindow) {		
		init();
	}
	
	/**
	 * Init all the page variables
	 *
	 */
	private void init(){
		this.add(new Label("title",getPageName()));
		this.add(getDataGrid("grid"));
	}
	
	/**
	 * Get the grid for display on the user role window.
	 * 
	 * 
	 * @return
	 */
	private SRSDataGrid getDataGrid(String id){		
		List<Object> roles = getTableDetails();		
		SRSDataGrid grid = new SRSDataGrid(id, new DataProviderAdapter(new SortableListDataProvider<Object>(roles)), getViewableRolesColumns(),null);            
        grid.setCleanSelectionOnPageChange(false);
        grid.setClickRowToSelect(false);     
        updateGridDimensions(grid);
        return grid;
	}
	
	/**
	 * Override to update the dimensions i.e. rows per page, height, width.
	 * 
	 * @param grid
	 */
	protected abstract void updateGridDimensions(SRSDataGrid grid);
	
	/**
	 * Return the columns
	 * 
	 * @return
	 */
	protected abstract List<IGridColumn> getViewableRolesColumns();	
	
	/**
	 * Get the table data
	 * @return
	 */
	protected abstract List<Object> getTableDetails();
	
	@Override
	public abstract String getPageName();
	
	
}
