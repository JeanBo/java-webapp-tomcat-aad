package za.co.liberty.web.pages.taxdetails;

import java.util.List;
import java.util.Vector;

import javax.naming.NamingException;

import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import za.co.liberty.business.guicontrollers.partymaintenance.IPartyMaintenanceController;
import za.co.liberty.business.party.IPartyManagement;
import za.co.liberty.dto.party.medicalaid.MedicalAidDetailDTO;
import za.co.liberty.dto.party.taxdetails.BBBEEDTO;
import za.co.liberty.dto.party.taxdetails.DirectivesDTO;
import za.co.liberty.dto.userprofiles.ContextDTO;
import za.co.liberty.exceptions.data.DataNotFoundException;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.helpers.lookup.ServiceLocator;
import za.co.liberty.web.data.enums.ComponentType;
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
 * History Page for BBBEE
 * @author AJM0308
 *
 */
public class BBBEEHistoryPage extends BaseWindowPage {

	private static final long serialVersionUID = -5393105408566103515L;
	
	private long partyoid;	
	private ContextDTO contextDTO;
	
	private transient IPartyMaintenanceController partyMaintenanceController;

	private void init(){
		if(partyoid > 0){			
			this.add(new Label("title","BBBEE History"));
			this.add(getDataGrid("bbbeeHistoryGrid"));
		}else{
			this.add(new Label("title","No valid party passed through to display history"));
			this.add(new Label("bbbeeHistoryGrid",""));
		}
	}	
		
	public BBBEEHistoryPage(ModalWindow window, long partyOID,ContextDTO contextDTO) {
		this.partyoid = partyOID;
		this.contextDTO = contextDTO;
		init();				
	}

	/* (non-Javadoc)
	 * @see za.co.liberty.web.pages.BaseWindowPage#getPageName()
	 */
	@Override
	public String getPageName() {
		return "BBBEE History";
	}

	/**
	 * Get the grid for display on the role window
	 * @return
	 */
	private SRSDataGrid getDataGrid(String id){		
       List <BBBEEDTO> beedetails = getBBBEEHistory();		
		SRSDataGrid grid = new SRSDataGrid(id, new DataProviderAdapter(
				new ListDataProvider<BBBEEDTO>(beedetails)), getBBBEEColumns(),null);		
		grid.setCleanSelectionOnPageChange(false);
		grid.setClickRowToSelect(false);
		grid.setAllowSelectMultiple(true);
		grid.setGridWidth(99, GridSizeUnit.PERCENTAGE);
		grid.setRowsPerPage(15);
		grid.setContentHeight(245, SizeUnit.PX);
		return grid;       
	}

	@SuppressWarnings("unchecked")
	private List<IGridColumn> getBBBEEColumns() {
		Vector<IGridColumn> cols = new Vector<IGridColumn>(4);
		cols.add(new SRSDataGridColumn<BBBEEDTO>("beeLevel", new Model("BBBEE Level"), "beeLevel", "beeLevel",getEditState()).setInitialSize(150));
		cols.add(new SRSDataGridColumn<BBBEEDTO>("companySize", new Model("Company Size"), "companySize", "companySize", getEditState()).setInitialSize(150));
		cols.add(new SRSDataGridColumn<BBBEEDTO>("blackOwnership", new Model("Black Ownership"), "blackOwnership",  "blackOwnership", getEditState()).setInitialSize(150));
		cols.add(new SRSDataGridColumn<BBBEEDTO>("blackWomenOwnership", new Model("Black Women Ownership"), "blackWomenOwnership", "blackWomenOwnership", getEditState()).setInitialSize(150));
		cols.add(new SRSDataGridColumn<BBBEEDTO>("effectiveFromDate", new Model("Effective From Date"), "effectiveFromDate", "effectiveFromDate", getEditState()).setInitialSize(150));
		cols.add(new SRSDataGridColumn<BBBEEDTO>("effectiveToDate", new Model("Effective To Date"), "effectiveToDate", "effectiveToDate", getEditState()).setInitialSize(150));
//		cols.add(new SRSDataGridColumn<BBBEEDTO>("createdBy", new Model("Created By Party ID"), "createdBy",  "createdBy", getEditState()).setInitialSize(150));
		cols.add(new SRSDataGridColumn<BBBEEDTO>("designatedGroup", new Model("Designated Group"), "designatedGroup", "designatedGroup", getEditState()).setInitialSize(150));
		cols.add(new SRSDataGridColumn<BBBEEDTO>("designatedGroupPerc", new Model("Designated Group %"), "designatedGroupPerc", "designatedGroupPerc", getEditState()).setInitialSize(150));
		cols.add(new SRSDataGridColumn<BBBEEDTO>("beeVerificationAgency", new Model("Bee Verification Agency"), "beeVerificationAgency", "beeVerificationAgency", getEditState()).setInitialSize(150));
//		cols.add(new SRSDataGridColumn<BBBEEDTO>("spend", new Model("Spend"), "spend", "spend",  getEditState()).setInitialSize(150));

		
		return cols;
	}
	
	private EditStateType getEditState() {		
		return EditStateType.VIEW;
	}
	
	private List<BBBEEDTO> getBBBEEHistory() {
		IPartyManagement partyManagement = getPartyManagement();		
		return partyManagement.getBBBEEHistoryForPartyOID(partyoid);		 
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
