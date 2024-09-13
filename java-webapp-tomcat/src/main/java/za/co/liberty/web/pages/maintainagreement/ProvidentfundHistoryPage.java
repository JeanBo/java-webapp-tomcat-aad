package za.co.liberty.web.pages.maintainagreement;

import java.util.List;
import java.util.Vector;

import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.Model;

import za.co.liberty.dto.agreement.properties.PropertyDTO;
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
 * Page for displaying provident fund property history
 * @author DZS2610
 *
 */
public class ProvidentfundHistoryPage extends BaseWindowPage {
	
	private static final long serialVersionUID = 1L;
	private EditStateType editState = EditStateType.VIEW;
	
	private long agreementNumber;	
	private List<PropertyDTO> historicalProperties;
	
		
	/**
	 * Init all the page variables
	 *
	 */
	private void init(){
		if(agreementNumber > 0 && historicalProperties != null && historicalProperties.size() > 0){
			this.add(new Label("title","Agreement " + agreementNumber+ " Provident Fund Properties History"));	
			this.add(getDataGrid("grid"));
		} else{
			this.add(new Label("title","No history to display"));
			this.add(new Label("grid",""));
		}
	}	
	
	public EditStateType getEditState() {
		return editState;
	}	

	/**
	 * Use this constructor if one has access to all the servicing types
	 * @param modalWindow
	 * @param agreementDTO
	 * @param type
	 * @param servicingTypes
	 */
	public ProvidentfundHistoryPage(ModalWindow modalWindow, long agreementNumber,List<PropertyDTO> historicalProperties){
		this.agreementNumber = agreementNumber;
		this.historicalProperties = historicalProperties;
		init();
	}
	/**
	 * Get the grid for display on the user role window
	 * @return
	 */
	private SRSDataGrid getDataGrid(String id){			
		SRSDataGrid grid = new SRSDataGrid(id, new DataProviderAdapter(new SortableListDataProvider<PropertyDTO>(historicalProperties)), getViewableRolesColumns(),null);            
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
			cols.add(new SRSDataGridColumn<PropertyDTO>("propertyKind.description",
						new Model("Property Name"), "propertyKind.description", "propertyKind.description", EditStateType.VIEW).setInitialSize(190));			
			cols.add(new SRSDataGridColumn<PropertyDTO>("value",new Model("Value"),"value","value", EditStateType.VIEW).setInitialSize(80)); 
			cols.add(new SRSDataGridColumn<PropertyDTO>("effectiveFrom",new Model("Start Date"),"effectiveFrom","effectiveFrom", EditStateType.VIEW).setInitialSize(78)); 
			cols.add(new SRSDataGridColumn<PropertyDTO>("effectiveTo",new Model("End Date"),"effectiveTo","effectiveTo", EditStateType.VIEW).setInitialSize(78));
			cols.add(new SRSDataGridColumn<PropertyDTO>("creationTime",new Model("Creation Time"),"creationTime","creationTime", EditStateType.VIEW).setInitialSize(100));
			
			return cols;
	}	
	
	@Override
	public String getPageName() {		
		return "Provident Fund History";
	}	
}
