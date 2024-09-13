package za.co.liberty.web.pages.party;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.naming.NamingException;

import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.Model;

import za.co.liberty.business.agreement.AgreementManagement;
import za.co.liberty.business.agreement.IAgreementManagement;
import za.co.liberty.business.party.IPartyManagement;
import za.co.liberty.business.party.PartyManagement;
import za.co.liberty.business.party.PartyManagement.RoleReference;
import za.co.liberty.common.domain.ObjectReference;
import za.co.liberty.dto.contracting.ResultPartyDTO;
import za.co.liberty.dto.userprofiles.ContextPartyDTO;
import za.co.liberty.exceptions.data.DataNotFoundException;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.party.util.Constants;
import za.co.liberty.srs.type.SRSType;
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
 * Page displaying the users roles
 * 
 * @author DZS2610
 *
 */
public class HierarchyRolePage extends BaseWindowPage {
	
	private static final long serialVersionUID = 1L;
	
	private ContextPartyDTO party;
	
	/**
	 * Init all the page variables
	 *
	 */
	private void init(){
		if(party != null){
			if(party.getTypeOid() == SRSType.PERSON){
				this.add(new Label("title","Hierarchy roles for "  +party.getName()));
			}else{
				this.add(new Label("title","Agreements linked to "  +party.getName()));
			}			
			this.add(getDataGrid("grid"));
		}else{
			this.add(new Label("title","No valid party passed through to display roles"));
			this.add(new Label("grid",""));
		}
	}
	
	
	public HierarchyRolePage(ModalWindow modalWindow, ContextPartyDTO party){
		this.party = party;		
		init();
	}
	
	/**
	 * Get the grid for display on the user role window
	 * @return
	 */
	private SRSDataGrid getDataGrid(String id){		
		List<TableDisplayObject> roles = getTableDetails();		
		SRSDataGrid grid = new SRSDataGrid(id, new DataProviderAdapter(new SortableListDataProvider<TableDisplayObject>(roles)), getViewableRolesColumns(),null);            
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
			if(party.getTypeOid() == SRSType.PERSON){
				cols.add(new SRSDataGridColumn<TableDisplayObject>("column1",new Model("Role Name"),"column1","column1", EditStateType.VIEW).setInitialSize(160)); 
				cols.add(new SRSDataGridColumn<TableDisplayObject>("column2",new Model("Node Type"),"column2","column2", EditStateType.VIEW).setInitialSize(230)); 
				cols.add(new SRSDataGridColumn<TableDisplayObject>("column3",new Model("Node Name"),"column3","column3", EditStateType.VIEW).setInitialSize(230)); 
			}else{
				cols.add(new SRSDataGridColumn<TableDisplayObject>("column1",new Model("Agreement Number"),"column1","column1", EditStateType.VIEW).setInitialSize(160)); 
				cols.add(new SRSDataGridColumn<TableDisplayObject>("column2",new Model("Party Name"),"column2","column2", EditStateType.VIEW).setInitialSize(230)); 
			}
			return cols;
	}
	
	/**
	 * Get the table data
	 * @return
	 */
	private List<TableDisplayObject> getTableDetails(){		
		List<TableDisplayObject> ret = new ArrayList<TableDisplayObject>();
		IPartyManagement partyManager = this.getPartyManager();
		if(party.getTypeOid() == SRSType.PERSON){
			//we get the manager and Secretary roles to display
			ObjectReference partyRef = new ObjectReference(Constants.PARTY_COMPONENT_ID,party.getTypeOid(),party.getPartyOid());
			List<RoleReference> rolesWhereContext = partyManager.getActivePartyRolesWherePartyIsContext(partyRef);
			for(RoleReference ref : rolesWhereContext){
				String type = "Unknown";
				if(ref.getRoleLinkType() == SRSType.ISRUNBY){
					String branchName = "Party Name could not be retrieved, id " + ref.getRolePlayerLinked().getObjectOid();					
					try {
						ResultPartyDTO branch = partyManager.findPartyWithObjectOid(ref.getRolePlayerLinked().getObjectOid());
						branchName = branch.getName();
						type = branch.getHierarchyOrganisationTypeName();
					} catch (DataNotFoundException e) {
						//shouldn't happen unless data is corrupted
					}
					ret.add(new TableDisplayObject("Manager",type,branchName));
				}
				if(ref.getRoleLinkType() == SRSType.ISADMINISTEREDBY ){
					String branchName = "Party Name could not be retrieved, id " + ref.getRolePlayerLinked().getObjectOid();				
					try {
						ResultPartyDTO branch = partyManager.findPartyWithObjectOid(ref.getRolePlayerLinked().getObjectOid());
						branchName = branch.getName();
						type = branch.getHierarchyOrganisationTypeName();
					} catch (DataNotFoundException e) {
						//shouldn't happen unless data is corrupted
					}
					ret.add(new TableDisplayObject("Secretary",branchName,type));
				}
				if( ref.getRoleLinkType() == SRSType.ISCOMPLIENCEOFFICEROF){
					String branchName = "Party Name could not be retrieved, id " + ref.getRolePlayerLinked().getObjectOid();				
					try {
						ResultPartyDTO branch = partyManager.findPartyWithObjectOid(ref.getRolePlayerLinked().getObjectOid());
						branchName = branch.getName();
						type = branch.getHierarchyOrganisationTypeName();
					} catch (DataNotFoundException e) {
						//shouldn't happen unless data is corrupted
					}
					ret.add(new TableDisplayObject("Compliance Officer",branchName,type));
				}
			}
		}else{
			//we get the agreements linked to branch to display
			List<RoleReference> rolesWhereRolePlayer = partyManager.getActivePartyRolesWherePartyIsRolePlayer(party.getPartyOid());
			for(RoleReference ref : rolesWhereRolePlayer){
				String partyName = "Unknown";
				try {
					ResultPartyDTO party = partyManager.findPartyIntermediaryWithAgreementNr(ref.getRolePlayerLinked().getObjectOid());
					partyName = party.getName();
				} catch (DataNotFoundException e) {
					//no worries, will display unknown
				}
				if(ref.getRoleLinkType() == SRSType.ISHOMETO){
					//agreement that the branch is home to
					ret.add(new TableDisplayObject(String.valueOf(ref.getRolePlayerLinked().getObjectOid()),partyName));
				}
			}
		}
		return ret;
	}
	
	@Override
	public String getPageName() {		
		return "Hierarchy Roles";
	}	
	
	/**
	 * Gets the IAgreementPrivilegesController interface for calls to the
	 * AgreementPrivilegesController session bean
	 * 
	 * @return
	 */
	private IPartyManagement getPartyManager() {	
		try {
			return PartyManagement.getInstance();
		} catch (NamingException e) {
			throw new CommunicationException("Could not get AgreementManagement Bean",e);
		}		
	}
	
	/**
	 * Contains values to display in the table
	 * @author DZS2610
	 *
	 */
	private class TableDisplayObject implements Serializable{		
		private static final long serialVersionUID = 1L;
		
		private String column1;
		private String column2;
		private String column3;
		private String column4;
		private String column5;
		
		private TableDisplayObject(String column1, String column2){
			this(column1, column2, null,null,null);
			
		}
		
		private TableDisplayObject(String column1, String column2, String column3){
			this(column1, column2, column3,null,null);
			
		}
		
		private TableDisplayObject(String column1, String column2, String column3, String column4){
			this(column1, column2, column3,column4,null);		
		}
		
		private TableDisplayObject(String column1, String column2, String column3, String column4, String column5){
			this.column1 = column1;
			this.column2 = column2;
			this.column3 = column3;
			this.column4 = column4;
			this.column5 = column5;
		}

		public String getColumn1() {
			return column1;
		}

		public String getColumn2() {
			return column2;
		}

		public String getColumn3() {
			return column3;
		}

		public String getColumn4() {
			return column4;
		}

		public String getColumn5() {
			return column5;
		}		
	
	}
}
