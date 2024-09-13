package za.co.liberty.web.pages.party;

import java.util.List;
import java.util.Vector;

import javax.naming.NamingException;

import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.Model;

import za.co.liberty.business.party.IPartyManagement;
import za.co.liberty.dto.party.PartyRoleDTO;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.helpers.lookup.ServiceLocator;
import za.co.liberty.interfaces.party.PartyRoleType;
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
 * This page will show all the party roles history for a user
 * @author DZS2610
 *
 */
public class PartyRolesHistoryPage extends BaseWindowPage {
	private static final long serialVersionUID = 1L;

	private long partyoid;
	private List<PartyRoleType> roleTypes;
	
	/**
	 * Init all the page variables
	 *
	 */
	private void init(){
		if(partyoid > 0){			
			this.add(new Label("title","Party roles history"));
			this.add(getDataGrid("grid"));
		}else{
			this.add(new Label("title","No valid party passed through to display roles"));
			this.add(new Label("grid",""));
		}
	}
	
	
	public PartyRolesHistoryPage(ModalWindow modalWindow, long partyoid, 
			List<PartyRoleType> roleTypes){
		this.partyoid = partyoid;	
		this.roleTypes = roleTypes;
		init();
	}
	
	/**
	 * Get the grid for display on the role window
	 * @return
	 */
	private SRSDataGrid getDataGrid(String id){		
		List<PartyRoleDTO> roles = getPartyRolesHistory();		
		SRSDataGrid grid = new SRSDataGrid(id, new DataProviderAdapter(new SortableListDataProvider<PartyRoleDTO>(roles)), getViewableRolesColumns(),null);            
        grid.setCleanSelectionOnPageChange(false);
        grid.setClickRowToSelect(false);        
        //grid.setContentHeight(100, SizeUnit.PX);
        grid.setAllowSelectMultiple(true);
        grid.setGridWidth(100, GridSizeUnit.PERCENTAGE);
       	grid.setRowsPerPage(15);
        grid.setContentHeight(245, SizeUnit.PX);               
        return grid;
	}
	
	private List<IGridColumn> getViewableRolesColumns() {
		Vector<IGridColumn> cols = new Vector<IGridColumn>(2);
		//the type of role
		cols.add(new SRSDataGridColumn<PartyRoleDTO>("partyRoleType.description",
				new Model("Type"), "partyRoleType.description", "partyRoleType.description",
				getEditState()).setInitialSize(150));
//		add in the name column
		cols.add(new SRSDataGridColumn<PartyRoleDTO>("rolePlayerReference.name",
				new Model("Party Name"), "rolePlayerReference.name", "rolePlayerReference.name", getEditState()).setInitialSize(200).setWrapText(true));
//		add in the uacfid column
		cols.add(new SRSDataGridColumn<PartyRoleDTO>("rolePlayerReference.uacfID",
				new Model("UACFID"), "rolePlayerReference.uacfID", "rolePlayerReference.uacfID", getEditState()).setInitialSize(80));
//		the effective dates of the role
		cols.add(new SRSDataGridColumn<PartyRoleDTO>("effectiveFrom",
				new Model("Start Date"), "effectiveFrom", "effectiveFrom",
				getEditState()).setInitialSize(100));
		cols.add(new SRSDataGridColumn<PartyRoleDTO>("effectiveTo",
				new Model("End Date"), "effectiveTo", "effectiveTo", getEditState()).setInitialSize(100));		
		return cols;
	}
	
	private EditStateType getEditState() {		
		return EditStateType.VIEW;
	}


	/**
	 * Get the history of the 
	 * @return
	 */
	private List<PartyRoleDTO> getPartyRolesHistory(){
		IPartyManagement partyManagement = getPartyManagement();		
		return partyManagement.getPartyRolesHistoryForPartyOID(				
				partyoid, true, roleTypes);		
	}
	
	@Override
	public String getPageName() {		
		return "Party Roles History";
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
