package za.co.liberty.web.pages.hierarchy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Vector;

import javax.naming.NamingException;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.Model;

import za.co.liberty.business.guicontrollers.hierarchy.IHierarchyGUIController;
import za.co.liberty.dto.agreement.SimpleAgreementDetailDTO;
import za.co.liberty.dto.party.HierarchyNodeDTO;
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

public class HierarchyLinkedAgreementsPopupPage extends BaseWindowPage {

	private static final long serialVersionUID = 1L;
	
	private HierarchyNodeDTO node;
	private transient IHierarchyGUIController hierarchyGUIController;	
	
	
	/**
	 * Init all the page variables
	 *
	 */
	private void init(){
		if(node != null && node.getOid() > 0){
			this.add(new Label("title","Agreements linked to " +node.getBusinessName()));						
			this.add(getDataGrid("grid"));
		}else{
			this.add(new Label("title","No valid hierarchy node passed through to display agreements"));
			this.add(new Label("grid",""));
		}
	}
	
	public HierarchyLinkedAgreementsPopupPage(HierarchyNodeDTO node){
		this.node = node;
		init();
	}
	
	/**
	 * Get the grid for display on the user role window
	 * @return
	 */
	private SRSDataGrid getDataGrid(String id){		
		Collection<SimpleAgreementDetailDTO> agmts = getHierarchyGUIController().findAllAgreementsLinkedToHierarchyNode(node.getOid(),null,null);		
		SRSDataGrid grid = new SRSDataGrid(id, new DataProviderAdapter(new SortableListDataProvider<SimpleAgreementDetailDTO>(new ArrayList<SimpleAgreementDetailDTO>(agmts))), getAgreementColumns(),null);            
        grid.setCleanSelectionOnPageChange(false);
        grid.setClickRowToSelect(false);        
        //grid.setContentHeight(100, SizeUnit.PX);
        grid.setAllowSelectMultiple(true);
        grid.setGridWidth(100, GridSizeUnit.PERCENTAGE);
       	grid.setRowsPerPage(15);
        grid.setContentHeight(245, SizeUnit.PX);               
        return grid;
	}
	
	/**
	 * Create the columns used in the grid
	 * @return
	 */
	private List<IGridColumn> getAgreementColumns() {
		Vector<IGridColumn> cols = new Vector<IGridColumn>(5);		
		cols.add(new SRSDataGridColumn<SimpleAgreementDetailDTO>(
				"agreementNumber", new Model("Agreement Number"), "agreementNumber",
				"agreementNumber", EditStateType.VIEW));
		cols.add(new SRSDataGridColumn<SimpleAgreementDetailDTO>(
				"name", new Model("Name"), "name",
				"name", EditStateType.VIEW));
		cols.add(new SRSDataGridColumn<SimpleAgreementDetailDTO>(
				"ConsCodeString", new Model("Consultant Code"), "ConsCodeString",
				"ConsCodeString", EditStateType.VIEW));
		cols.add(new SRSDataGridColumn<SimpleAgreementDetailDTO>(
				"status", new Model("Status"), "status",
				"status", EditStateType.VIEW));
		cols.add(new SRSDataGridColumn<SimpleAgreementDetailDTO>(
				"agreementKindType", new Model("Kind"), "agreementKindType",
				"agreementKindType", EditStateType.VIEW));		
		return cols;
	}
	
	@Override
	public String getPageName() {		
		return "Agreements Linked";
	}
	
	/**
	 * Get the HierarchyGUIController bean 
	 * @return
	 */
	private IHierarchyGUIController getHierarchyGUIController(){
		if(hierarchyGUIController == null){
			try {
				hierarchyGUIController = ServiceLocator.lookupService(IHierarchyGUIController.class);
			} catch (NamingException e) {
				throw new CommunicationException(e);
			}
		}
		return hierarchyGUIController;
	}

}
