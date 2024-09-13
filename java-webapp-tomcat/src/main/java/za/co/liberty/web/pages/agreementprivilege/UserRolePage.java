package za.co.liberty.web.pages.agreementprivilege;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import javax.naming.NamingException;

import org.apache.wicket.Component;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import za.co.liberty.business.guicontrollers.IAgreementPrivilegesController;
import za.co.liberty.dto.userprofiles.ContextPartyDTO;
import za.co.liberty.dto.userprofiles.ISessionUserProfile;
import za.co.liberty.dto.userprofiles.ProfileRoleDTO;
import za.co.liberty.exceptions.data.DataNotFoundException;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.helpers.lookup.ServiceLocator;
import za.co.liberty.interfaces.agreements.requests.RequestKindType;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.pages.BaseWindowPage;
import za.co.liberty.web.pages.panels.HelperPanel;
import za.co.liberty.web.system.EJBReferences;
import za.co.liberty.web.system.SRSAuthWebSession;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGrid;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGrid.GridSizeUnit;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGridColumn;
import za.co.liberty.web.wicket.markup.repeater.data.SortableListDataProvider;

import com.inmethod.grid.DataProviderAdapter;
import com.inmethod.grid.IGridColumn;
import com.inmethod.grid.SizeUnit;
import com.inmethod.grid.column.AbstractColumn;

/**
 * Page displaying the users roles
 * 
 * @author DZS2610
 *
 */
public class UserRolePage extends BaseWindowPage {
	
	private static final long serialVersionUID = 1L;
	
	private ModalWindow modalWindow;
	private ContextPartyDTO party;
	
	/**
	 * Init all the page variables
	 *
	 */
	private void init(){
		if(party != null){
			this.add(new Label("title",party.getName()+ ((party.getUacfID() != null ) ? " ("+party.getUacfID()+")" : "")));
			this.add(getDataGrid("grid"));
			this.add(getTamGrid("tam"));
		}else{
			this.add(new Label("title","No valid user exists to display roles"));
			this.add(new Label("grid",""));
			this.add(new Label("tam",""));
		}
	}
	
	
	public UserRolePage(ModalWindow modalWindow, ContextPartyDTO party){
		this.modalWindow = modalWindow;	
		this.party = party;		
		init();
	}
	
	/**
	 * Get the grid for display on the user role window
	 * @return
	 */
	private SRSDataGrid getDataGrid(String id){		
		List<ProfileRoleDTO> roles = null;
		try {
			roles = getAgreementPrivilegesController().getProfileRoleDTOsForParty(party.getPartyOid());
		} catch (DataNotFoundException e) {
			
		}
		if(roles == null){
			roles = new ArrayList<ProfileRoleDTO>(0);
		}
		SRSDataGrid grid = new SRSDataGrid(id, new DataProviderAdapter(new SortableListDataProvider<ProfileRoleDTO>(roles)), getViewableRolesColumns(),null);            
        grid.setCleanSelectionOnPageChange(false);
        grid.setClickRowToSelect(false);        
        //grid.setContentHeight(100, SizeUnit.PX);
        grid.setAllowSelectMultiple(true);
        grid.setGridWidth(100, GridSizeUnit.PERCENTAGE);
        grid.setRowsPerPage(6);
        grid.setContentHeight(110, SizeUnit.PX);
        return grid;
	}
	
	private List<IGridColumn> getViewableRolesColumns() {
			Vector<IGridColumn> cols = new Vector<IGridColumn>(2);
			cols.add(new SRSDataGridColumn<ProfileRoleDTO>("roleName",new Model("Role Name"),"roleName","roleName", EditStateType.VIEW).setInitialSize(160)); 
			cols.add(new SRSDataGridColumn<ProfileRoleDTO>("roleShortDescription",new Model("Role Short Description"),"roleShortDescription","roleShortDescription", EditStateType.VIEW).setInitialSize(230)); 
			cols.add(new SRSDataGridColumn<ProfileRoleDTO>("roleLongDescription",new Model("Role Long Description"),"roleLongDescription","roleLongDescription", EditStateType.VIEW).setInitialSize(300)); 
			return cols;
	}	
	

	/**
	 * Get the grid for display on the user role window
	 * @return
	 */
	private Panel getTamGrid(String id){		
		
		/*
		 * Retrieve the TAM roles linked to a uacf-id but only if it is configured
		 * 
		 * Only shown for those that can modify or are prod support
		 */
		ISessionUserProfile userProfile = SRSAuthWebSession.get().getSessionUser();
		
		if (!userProfile.isUserProductionSupport() && 
				!userProfile.isAllowRaise(RequestKindType.MaintainPartyDetails) ) {
			return new EmptyPanel(id);
		}
		List<String> roles = null;
		try {
			if (party.getUacfID()!=null && party.getUacfID().length()>1) {
				roles = getAgreementPrivilegesController().getTamRolesForUacfID(party.getUacfID());
			}
		} catch (Exception e) {
			e.printStackTrace();
			// Ignore
			
		}
		if(roles == null){
			roles = new ArrayList<String>(0);
		}
		Collections.sort(roles);
		
		// Show the grid
		SRSDataGrid grid = new SRSDataGrid(id, new DataProviderAdapter(new SortableListDataProvider<String>(roles)), getViewableTamColumns(),null);            
        grid.setCleanSelectionOnPageChange(false);
        grid.setClickRowToSelect(false);        
        //grid.setContentHeight(100, SizeUnit.PX);
        grid.setAllowSelectMultiple(true);
        grid.setGridWidth(80, GridSizeUnit.PERCENTAGE);
        grid.setRowsPerPage(10);
        grid.setContentHeight(140, SizeUnit.PX);
        return grid;
	}
	
	/**
	 * Configure the columns for tam roles
	 * 
	 * @return
	 */
	private List<IGridColumn> getViewableTamColumns() {
			Vector<IGridColumn> cols = new Vector<IGridColumn>(2);

			cols.add(new AbstractColumn("roleName", new Model("Tam Role")){
				
				private static final long serialVersionUID = 1L;
				@Override
				public Component newCell(WebMarkupContainer parent, String componentId, IModel rowModel) {
					
					final String s = (String) rowModel.getObject();
					Label label = new Label("value",new Model(s));
					return HelperPanel.getInstance(componentId,label);		
				}				
			}.setInitialSize(250));
						
			
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
	private IAgreementPrivilegesController getAgreementPrivilegesController() {
		IAgreementPrivilegesController agreementPrivilegesController = null;
		try {
			agreementPrivilegesController = ServiceLocator.lookupService(IAgreementPrivilegesController.class);
		} catch (NamingException e) {
			throw new CommunicationException(e);
		}
		return agreementPrivilegesController;
	}
}
