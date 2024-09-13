package za.co.liberty.web.pages.maintainagreement;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.apache.wicket.model.Model;

import za.co.liberty.rating.vo.TableColumn;
import za.co.liberty.srs.util.rating.TableProxy;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.pages.BaseWindowPage;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGrid;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGridColumn;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGrid.GridSizeUnit;
import za.co.liberty.web.wicket.markup.repeater.data.SortableListDataProvider;

import com.inmethod.grid.DataProviderAdapter;
import com.inmethod.grid.IGridColumn;
import com.inmethod.grid.SizeUnit;

/**
 * Popup page that will display various Rating table details
 * @author dzs2610
 *
 */
public class IncentiveRatingDetailsPopup extends BaseWindowPage {
	private static final long serialVersionUID = 1L;
	
	TableProxy ratingTableData;
	boolean init = false;
	
	public IncentiveRatingDetailsPopup(TableProxy ratingData){
		ratingTableData = ratingData;		
	}	

	@Override
	protected void onBeforeRender() {
		if(!init){
			//set up the rating components and data
			add(getDataGrid("ratingdata"));
			init = true;
		}
		super.onBeforeRender();
	}

	/**
	 * Get the grid for display
	 * @return
	 */
	private SRSDataGrid getDataGrid(String id){						
		List<RatingGridRowData> gridData = new ArrayList<RatingGridRowData>();
		if(ratingTableData != null){
			for (int j = 0; j < ratingTableData.getRowCount(); j++) {
				List<Object> gridRowData = new ArrayList<Object>();			
				RatingGridRowData rowdata = new RatingGridRowData(gridRowData);
				gridData.add(rowdata);
				for (int i = 0; i < ratingTableData.getColumnCount(); i++) {
					Object val = ratingTableData.getValue(i, j);
					if(val instanceof BigDecimal){
						val = ((BigDecimal)val).toString();
					}
					gridRowData.add(val);
				}
			}
		}
		
		SRSDataGrid grid = new SRSDataGrid(id, new DataProviderAdapter(new SortableListDataProvider<RatingGridRowData>(gridData)), getViewableRolesColumns(),null);            
        grid.setCleanSelectionOnPageChange(false);
        grid.setClickRowToSelect(false); 
        grid.setAutoResize(true);          
        //grid.setContentHeight(100, SizeUnit.PX);
        grid.setAllowSelectMultiple(false);
        grid.setGridWidth(99, GridSizeUnit.PERCENTAGE);
        grid.setRowsPerPage(10);
        grid.setContentHeight(150, SizeUnit.PX);
        return grid;
	}
	
	/**
	 * Get columns for grid based on type selected
	 * @return
	 */
	private List<IGridColumn> getViewableRolesColumns() {
		Vector<IGridColumn> cols = new Vector<IGridColumn>();		
			cols = new Vector<IGridColumn>();
			//set up cols based on rating table
			if(ratingTableData != null){
				for (int i = 0; i < ratingTableData.getColumnCount(); i++) {
					TableColumn col = ratingTableData.getColumn(i);
					cols.add(new SRSDataGridColumn<RatingGridRowData>("rowdata"+i+"",
							new Model<String>(col.getName()), "rowdata["+i+"]", "rowdata["+i+"]", EditStateType.VIEW).setInitialSize(700/ (ratingTableData.getColumnCount())));
				}
			}
			return cols;
	}	


	@Override
	public String getPageName() {
		if(ratingTableData != null){
			return "Rating Table Details for " + ratingTableData.getTable().getId();
		}
		return "Rating Details";
	}
	
	/**
	 * Class for the rating row data
	 * @author dzs2610
	 *
	 */
	private class RatingGridRowData implements Serializable{		
		private static final long serialVersionUID = 1L;
		
		private List<Object> rowdata;
		
		RatingGridRowData(List<Object> rowdata){
			this.rowdata = rowdata;
		}

		public List<Object> getRowdata() {
			return rowdata;
		}

		public void setRowdata(List<Object> rowdata) {
			this.rowdata = rowdata;
		}	
	}
}
