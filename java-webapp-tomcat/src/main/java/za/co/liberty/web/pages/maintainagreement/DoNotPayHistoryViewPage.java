package za.co.liberty.web.pages.maintainagreement;

import java.util.List;
import java.util.Vector;

import org.apache.wicket.Page;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.model.Model;

import za.co.liberty.dto.agreement.PaymentSchedulerDTO;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.pages.BaseWindowPage;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGrid;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGrid.GridSizeUnit;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGridColumn;
import za.co.liberty.web.wicket.markup.repeater.data.SortableListDataProvider;

import com.inmethod.grid.DataProviderAdapter;
import com.inmethod.grid.IGridColumn;
import com.inmethod.grid.SizeUnit;

/**
 * Page for viewing List of panels within a History in a modal window.
 * 
 * @author PZM2509 - 19 Jan 2018
 * 
 */

public class DoNotPayHistoryViewPage extends BaseWindowPage {
	
	//added from the panel
	 //Constants
	private static final long serialVersionUID = 1L;
	
	//Form components
	protected ModalWindow modalWindow;
	
	private ModalWindow mw;
	
	private EditStateType editState;
	
	
	private Page parentPage;
	
	private List<PaymentSchedulerDTO> dtoList;
	
	public DoNotPayHistoryViewPage(String id, EditStateType editState,			
			Page parentPage , ModalWindow mw,List<PaymentSchedulerDTO> dtoList){
			this.mw = mw;
			this.editState = editState;
			this.parentPage = parentPage;	
			this.dtoList = dtoList;
			init();
	}
	/**
	 * Init all the page variables
	 *
	 */
	private void init(){
		this.add(getDataGrid("historyGrid"));	
	}	

	@Override
	public String getPageName() {
		return "DO NOT PAY";
	}
	
	/**
	 * Get the grid for display on the user role window
	 * @return
	 */
	private SRSDataGrid getDataGrid(String id){			
		SRSDataGrid grid = new SRSDataGrid(id, new DataProviderAdapter(new SortableListDataProvider<PaymentSchedulerDTO>(dtoList)), getViewableRolesColumns(),null);            
        grid.setCleanSelectionOnPageChange(false);
        grid.setClickRowToSelect(false);        
        //grid.setContentHeight(100, SizeUnit.PX);
        grid.setAllowSelectMultiple(true);
        grid.setGridWidth(99, GridSizeUnit.PERCENTAGE);
        grid.setRowsPerPage(10);
        grid.setContentHeight(170, SizeUnit.PX);
        return grid;
	}
	
	/**
	 * Get columns for grid based on type selected
	 * @return
	 */
	private List<IGridColumn> getViewableRolesColumns() {
		Vector<IGridColumn> cols = new Vector<IGridColumn>();
		
			cols = new Vector<IGridColumn>();
						
			cols.add(new SRSDataGridColumn<PaymentSchedulerDTO>("doNotPay",new Model("Do Not Pay"),"doNotPay","doNotPay", EditStateType.VIEW).setInitialSize(80)); 
			cols.add(new SRSDataGridColumn<PaymentSchedulerDTO>("historyEffectiveDate",new Model("Effective Date"),"historyEffectiveDate","historyEffectiveDate", EditStateType.VIEW).setInitialSize(78)); 
			cols.add(new SRSDataGridColumn<PaymentSchedulerDTO>("historyEndDate",new Model("End Date"),"historyEndDate","historyEndDate", EditStateType.VIEW).setInitialSize(78));
			cols.add(new SRSDataGridColumn<PaymentSchedulerDTO>("comments",new Model("Comment"),"comments","comments", EditStateType.VIEW).setInitialSize(100));
			cols.add(new SRSDataGridColumn<PaymentSchedulerDTO>("requestedBy",new Model("Requested By"),"requestedBy","requestedBy", EditStateType.VIEW).setInitialSize(100));
			
			return cols;
	}	


}
