package za.co.liberty.web.pages.taxdetails;

import java.util.List;
import java.util.Vector;

import javax.naming.NamingException;

import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.Model;

import za.co.liberty.business.guicontrollers.partymaintenance.IPartyMaintenanceController;
import za.co.liberty.dto.party.taxdetails.DirectivesDTO;
import za.co.liberty.dto.userprofiles.ContextDTO;
import za.co.liberty.exceptions.data.DataNotFoundException;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.helpers.lookup.ServiceLocator;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.pages.BaseWindowPage;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGrid;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGridColumn;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGrid.GridSizeUnit;
import za.co.liberty.web.wicket.markup.repeater.data.ListDataProvider;

import com.inmethod.grid.DataProviderAdapter;
import com.inmethod.grid.IGridColumn;
import com.inmethod.grid.SizeUnit;

/**
 * History Page for Tax Directives
 * @author SZM0905
 *
 */
public class TaxDirectivesHistoryPage extends BaseWindowPage {

	private static final long serialVersionUID = -5393105408566103515L;
	
	private long partyoid;	
	private ContextDTO contextDTO;
	
	private transient IPartyMaintenanceController partyMaintenanceController;

	private void init(){
		if(partyoid > 0){			
			this.add(new Label("title","Tax Directives History"));
			this.add(getDataGrid("taxDirectivesHistoryGrid"));
		}else{
			this.add(new Label("title","No valid party passed through to display history"));
			this.add(new Label("taxDirectivesHistoryGrid",""));
		}
	}	
		
	public TaxDirectivesHistoryPage(ModalWindow window, long partyOID,ContextDTO contextDTO) {
		this.partyoid = partyOID;
		this.contextDTO = contextDTO;
		init();				
	}

	/* (non-Javadoc)
	 * @see za.co.liberty.web.pages.BaseWindowPage#getPageName()
	 */
	@Override
	public String getPageName() {
		return "Tax Directives History";
	}

	/**
	 * Get the grid for display on the role window
	 * @return
	 */
	private SRSDataGrid getDataGrid(String id){		
       List <DirectivesDTO> directdetails = getDirectivesHistory();		
		SRSDataGrid grid = new SRSDataGrid(id, new DataProviderAdapter(
				new ListDataProvider<DirectivesDTO>(directdetails)), getDirectivesColumns(),null);		
		grid.setCleanSelectionOnPageChange(false);
		grid.setClickRowToSelect(false);
		grid.setAllowSelectMultiple(true);
		grid.setGridWidth(99, GridSizeUnit.PERCENTAGE);
		grid.setRowsPerPage(15);
		grid.setContentHeight(245, SizeUnit.PX);
		return grid;       
	}

	@SuppressWarnings("unchecked")
	private List<IGridColumn> getDirectivesColumns() {
		Vector<IGridColumn> cols = new Vector<IGridColumn>(4);
		cols.add(new SRSDataGridColumn<DirectivesDTO>("directiveNo",
				new Model("Directive Number"), "directiveNo", "directiveNo",
				getEditState()).setInitialSize(150));
		cols.add(new SRSDataGridColumn<DirectivesDTO>("percentage",
				new Model("Percentage"), "percentage", "percentage", getEditState()).setInitialSize(100));
		cols.add(new SRSDataGridColumn<DirectivesDTO>("startDate",
				new Model("Start Date"), "startDate", "startDate", getEditState()).setInitialSize(100));
		cols.add(new SRSDataGridColumn<DirectivesDTO>("endDate",
				new Model("End Date"), "endDate", "endDate", getEditState()).setInitialSize(80));
		return cols;
	}
	
	private EditStateType getEditState() {		
		return EditStateType.VIEW;
	}
	
	private List<DirectivesDTO> getDirectivesHistory() {
		try {
			return (List<DirectivesDTO>) getPartyMaintenanceController().getTaxDetailsDTO(partyoid,
					contextDTO.getAgreementContextDTO().getAgreementNumber(),true).getDirectivesDTOList();
		} catch (DataNotFoundException e) {
			error(e.getMessage());
		}
		return null;		 
	}
	
	private IPartyMaintenanceController getPartyMaintenanceController()	{
		if(partyMaintenanceController == null){
			try {
				partyMaintenanceController = ServiceLocator.lookupService(IPartyMaintenanceController.class);
			} catch (NamingException e) {
				throw new CommunicationException(e);
			}
		}
		return partyMaintenanceController;
	}
	
}
