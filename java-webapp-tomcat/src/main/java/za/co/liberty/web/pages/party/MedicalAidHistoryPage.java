package za.co.liberty.web.pages.party;

import java.util.List;
import java.util.Vector;

import javax.naming.NamingException;

import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.Model;

import za.co.liberty.business.party.IPartyManagement;
import za.co.liberty.dto.party.medicalaid.MedicalAidDetailDTO;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.helpers.lookup.ServiceLocator;
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
 * History panel for the medical aid details
 * @author dzs2610
 *
 */
public class MedicalAidHistoryPage extends BaseWindowPage {
	private static final long serialVersionUID = 1L;

	private long partyoid;	
	
	/**
	 * Init all the page variables
	 *
	 */
	private void init(){
		if(partyoid > 0){			
			this.add(new Label("title","Medical Aid History"));
			this.add(getDataGrid("grid"));
		}else{
			this.add(new Label("title","No valid party passed through to display history"));
			this.add(new Label("grid",""));
		}
	}	
	
	public MedicalAidHistoryPage(ModalWindow modalWindow, long partyoid){
		this.partyoid = partyoid;			
		init();
	}
	
	/**
	 * Get the grid for display on the role window
	 * @return
	 */
	private SRSDataGrid getDataGrid(String id){		
		List<MedicalAidDetailDTO> roles = getHistory();		
		SRSDataGrid grid = new SRSDataGrid(id, new DataProviderAdapter(new SortableListDataProvider<MedicalAidDetailDTO>(roles)), getViewableRolesColumns(),null);            
        grid.setCleanSelectionOnPageChange(false);
        grid.setClickRowToSelect(false);        
        //grid.setContentHeight(100, SizeUnit.PX);
        grid.setAllowSelectMultiple(true);
        grid.setGridWidth(98, GridSizeUnit.PERCENTAGE);
       	grid.setRowsPerPage(15);
        grid.setContentHeight(245, SizeUnit.PX);               
        return grid;
	}

	
	private List<IGridColumn> getViewableRolesColumns() {
		Vector<IGridColumn> cols = new Vector<IGridColumn>(6);
		cols.add(new SRSDataGridColumn<MedicalAidDetailDTO>("medicalAidNumber",
				new Model("Medical Aid Number"), "medicalAidNumber", "medicalAidNumber",
				getEditState()).setInitialSize(150));
		cols.add(new SRSDataGridColumn<MedicalAidDetailDTO>("effectiveDate",
				new Model("Effective From"), "effectiveDate", "effectiveDate", getEditState()).setInitialSize(100));
		cols.add(new SRSDataGridColumn<MedicalAidDetailDTO>("effectiveToDate",
				new Model("Effective To"), "effectiveToDate", "effectiveToDate", getEditState()).setInitialSize(100));
		cols.add(new SRSDataGridColumn<MedicalAidDetailDTO>("primaryMember",
				new Model("Primary Mem"), "primaryMember", "primaryMember", getEditState()).setInitialSize(80));
		cols.add(new SRSDataGridColumn<MedicalAidDetailDTO>("numberDependants",
				new Model("No Dependants"), "numberDependants", "numberDependants",
				getEditState()).setInitialSize(100));
		cols.add(new SRSDataGridColumn<MedicalAidDetailDTO>("currentContribution",
				new Model("Contribution"), "currentContribution", "currentContribution",
				getEditState()).setInitialSize(100));
		return cols;
	}
	
	private EditStateType getEditState() {		
		return EditStateType.VIEW;
	}


	/**
	 * Get the history 
	 * @return
	 */
	private List<MedicalAidDetailDTO> getHistory(){
		IPartyManagement partyManagement = getPartyManagement();		
		return partyManagement.getMedicalAidHistoryForPartyOID(partyoid);		
	}
	
	@Override
	public String getPageName() {		
		return "Medical Aid History";
	}
	
	/**
	 * Get the Party management bean
	 * @return
	 */
	private IPartyManagement getPartyManagement(){
		try {
			return ServiceLocator.lookupService(IPartyManagement.class);
		} catch (NamingException e) {
			throw new CommunicationException(e);
		}
	}
}
