package za.co.liberty.web.pages.hierarchy;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.naming.NamingException;

import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.Model;

import com.inmethod.grid.DataProviderAdapter;
import com.inmethod.grid.IGridColumn;
import com.inmethod.grid.SizeUnit;

import za.co.liberty.agreement.common.exceptions.LogicExecutionException;
import za.co.liberty.business.guicontrollers.hierarchy.IHierarchyGUIController;
import za.co.liberty.dto.party.HierarchyEmployeeLinkDTO;
import za.co.liberty.dto.party.HierarchyNodeLinkDTO;
import za.co.liberty.dto.party.RolePlayerDTO;
import za.co.liberty.exceptions.SystemException;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.helpers.lookup.ServiceLocator;
import za.co.liberty.party.exceptions.InvalidPartyRegException;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.pages.BaseWindowPage;
import za.co.liberty.web.pages.hierarchy.model.MaintainHierarchyPageModel;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGrid;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGrid.GridSizeUnit;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGridColumn;
import za.co.liberty.web.wicket.markup.repeater.data.SortableListDataProvider;

/**
 * Page for displaying role history
 * @author DZS2610
 *
 */
public class RoleHistoryPage extends BaseWindowPage {
	
	private static final long serialVersionUID = 1L;
	
	private ModalWindow modalWindow;
	private MaintainHierarchyPageModel pageModel;
	private HistoryType type = HistoryType.PARENT;
	
	/*
	 * The types of histories this page can display
	 */
	public enum HistoryType{PARENT,EMPLOYEE};
	
	/**
	 * Init all the page variables
	 *
	 */
	private void init(){
		if(pageModel != null && pageModel.getHierarchyNodeDTO() != null && pageModel.getHierarchyNodeDTO().getOid() > 0){
			if(type == HistoryType.PARENT){
				this.add(new Label("title",pageModel.getHierarchyNodeDTO().getBusinessName()+ " Parent History"));
			}else{
				this.add(new Label("title",pageModel.getHierarchyNodeDTO().getBusinessName()+ " Employee History"));
			}
			this.add(getDataGrid("grid"));
		}else{
			this.add(new Label("title","No valid organisation exists to display history"));
			this.add(new Label("grid",""));
		}
	}
	
	/**
	 * Default constructor
	 * @param modalWindow
	 * @param pageModel
	 * @param type
	 */
	public RoleHistoryPage(ModalWindow modalWindow, MaintainHierarchyPageModel pageModel,HistoryType type){
		this.modalWindow = modalWindow;	
		this.pageModel = pageModel;	
		this.type = type;
		init();
	}
	
	/**
	 * Get the grid for display on the user role window
	 * @return
	 */
	private SRSDataGrid getDataGrid(String id){		
		List<RolePlayerDTO> roles = null;		
		try {
			if(type == HistoryType.PARENT){
				roles = new ArrayList<RolePlayerDTO>(getHierarchyGUIController().getNodeParentHistory(pageModel.getHierarchyNodeDTO().getOid()));
			}else if(type == HistoryType.EMPLOYEE){
				roles = new ArrayList<RolePlayerDTO>(getHierarchyGUIController().getNodeEmployeeHistory(pageModel.getHierarchyNodeDTO().getOid()));
			}
		} catch (SystemException e) {
			e.printStackTrace();
			error("An error occured getting the history: " + e.getMessage());
		} catch (LogicExecutionException e) {
			e.printStackTrace();
			error("An error occured getting the history: " + e.getMessage());
		} catch (InvalidPartyRegException e) {			
			e.printStackTrace();
			error("An error occured getting the history: " + e.getMessage());
		}		
		if(roles == null){
			roles = new ArrayList<RolePlayerDTO>(0);
		}
		SRSDataGrid grid = new SRSDataGrid(id, new DataProviderAdapter(new SortableListDataProvider<RolePlayerDTO>(roles)), getViewableRolesColumns(),null);            
        grid.setCleanSelectionOnPageChange(false);
        grid.setClickRowToSelect(false);        
        //grid.setContentHeight(100, SizeUnit.PX);
        grid.setAllowSelectMultiple(true);
        grid.setGridWidth(100, GridSizeUnit.PERCENTAGE);
        grid.setRowsPerPage(10);
        grid.setContentHeight(150, SizeUnit.PX);
        return grid;
	}
	
	/**
	 * Get columns for grid based on type selected
	 * @return
	 */
	private List<IGridColumn> getViewableRolesColumns() {
			Vector<IGridColumn> cols = new Vector<IGridColumn>(6);
			if(type == HistoryType.PARENT){
				//HierarchyNodeLinkDTO
				cols.add(new SRSDataGridColumn<HierarchyNodeLinkDTO>("type",
						new Model("Parent Type"), "type", "type", EditStateType.VIEW).setInitialSize(100)); 
				cols.add(new SRSDataGridColumn<HierarchyNodeLinkDTO>("partyLink",
						new Model("Parent Name"), "partyLink", "partyLink", EditStateType.VIEW).setInitialSize(200)); 				
			}else if(type == HistoryType.EMPLOYEE){
				//HierarchyEmployeeLinkDTO
				cols.add(new SRSDataGridColumn<HierarchyEmployeeLinkDTO>(
						"hierarchyType", new Model("Type"), "hierarchyType",
						"hierarchyType", EditStateType.VIEW).setInitialSize(100)); 
				cols.add(new SRSDataGridColumn<HierarchyEmployeeLinkDTO>("selectedParty.name",
						new Model("Name"), "selectedParty.name", "selectedParty.name",EditStateType.VIEW).setInitialSize(180)); 
				cols.add(new SRSDataGridColumn<HierarchyEmployeeLinkDTO>("selectedParty.uacfID",
						new Model("UACFID"), "selectedParty.uacfID", "selectedParty.uacfID", EditStateType.VIEW).setInitialSize(70)); 
     		}
			cols.add(new SRSDataGridColumn<RolePlayerDTO>("effectiveFrom",new Model("Start Date"),"effectiveFrom","effectiveFrom", EditStateType.VIEW).setInitialSize(100)); 
			cols.add(new SRSDataGridColumn<RolePlayerDTO>("effectiveTo",new Model("End Date"),"effectiveTo","effectiveTo", EditStateType.VIEW).setInitialSize(100)); 
			return cols;
	}	
	
	@Override
	public String getPageName() {		
		return "Roles";
	}
	
	/**
	 * Gets the IAgreementPrivilegesController interface for calls to the
	 * AgreementPrivilegesController session bean
	 * 
	 * @return
	 */
	private IHierarchyGUIController getHierarchyGUIController() {
		try {
			return ServiceLocator.lookupService(IHierarchyGUIController.class);
		} catch (NamingException e) {
			throw new CommunicationException(e);
		}
	}
}
