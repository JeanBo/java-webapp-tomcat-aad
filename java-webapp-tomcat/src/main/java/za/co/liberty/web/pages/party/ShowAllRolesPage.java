package za.co.liberty.web.pages.party;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import javax.naming.NamingException;

import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.Model;

import za.co.liberty.business.party.IPartyManagement;
import za.co.liberty.business.party.PartyManagement;
import za.co.liberty.dto.party.PartyRoleDTO;
import za.co.liberty.dto.rating.DescriptionDTO;
import za.co.liberty.dto.userprofiles.ContextPartyDTO;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.helpers.lookup.ServiceLocator;
import za.co.liberty.interfaces.agreements.ProjectBaseType;
import za.co.liberty.interfaces.party.PartyRoleType;
import za.co.liberty.persistence.rating.IDescriptionEntityManager;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.pages.BaseWindowPage;
import za.co.liberty.web.pages.party.model.MaintainPartyPageModel;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGrid;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGrid.GridSizeUnit;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGridColumn;
import za.co.liberty.web.wicket.markup.repeater.data.SortableListDataProvider;

import com.inmethod.grid.DataProviderAdapter;
import com.inmethod.grid.IGridColumn;
import com.inmethod.grid.SizeUnit;

public class ShowAllRolesPage  extends BaseWindowPage{
private static final long serialVersionUID = 1L;
	
	private ContextPartyDTO party;
	private MaintainPartyPageModel pageModel;
	private IDescriptionEntityManager descriptionEntityManager;
	public ShowAllRolesPage (ModalWindow modalWindow, ContextPartyDTO party, MaintainPartyPageModel pageModel)
	{
		this.party=party;
		this.pageModel=pageModel;
		init();
	}
	
	private void init()
	{
		if (party !=null)
		{
			this.add(new Label("title","Group Channel Roles History for : " +party.getName()));
			this.add(getDataGrid("grid"));
		}
	}
	
	private SRSDataGrid getDataGrid(String id) {
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
		Vector<IGridColumn> cols = new Vector<IGridColumn>();
		cols.add(new SRSDataGridColumn<TableDisplayObject>("column1",new Model("Role Kind"),"column1","column1", EditStateType.VIEW).setInitialSize(250)); 
		cols.add(new SRSDataGridColumn<TableDisplayObject>("column2",new Model("Start Date"),"column2","column2", EditStateType.VIEW).setInitialSize(230)); 
		cols.add(new SRSDataGridColumn<TableDisplayObject>("column3",new Model("End Date"),"column3","column3", EditStateType.VIEW).setInitialSize(230)); 
		return cols;
	}

	private List<TableDisplayObject> getTableDetails() {
		List<TableDisplayObject> displayObjects = new ArrayList<TableDisplayObject>();
		IPartyManagement partyManager = this.getPartyManager();
			String role ="";
		List<PartyRoleType> roleTypes = new ArrayList<PartyRoleType>();
		roleTypes.add(PartyRoleType.ISDISTRIBUTIONGROUPADMINSTRATOR);
		roleTypes.add(PartyRoleType.ISDISTRIBUTIONGROUPMANAGER);
////MSK:Change :Need to delete at the time of migrate	,uncommente List<PartyRoleDTO> partyRolesHistoryForPartyOID entry
//		List<PartyRoleDTO> rolDTO=new ArrayList<PartyRoleDTO>();
//		PartyRoleDTO prDTO=new PartyRoleDTO();
//		prDTO.setPartyRoleType(PartyRoleType.EMPLOYEE);
//		prDTO.setSubTypeId(1651);
//		prDTO.setType(1651l);
//		rolDTO.add(prDTO);
//		//List<PartyRoleDTO> partyRolesHistoryForPartyOID = partyManager.getPartyRolesHistoryForPartyOID(pageModel.getPartyDTO().getOid(), true, roleTypes);
//		List<PartyRoleDTO> partyRolesHistoryForPartyOID = rolDTO;
		
		List<PartyRoleDTO> partyRolesHistoryForPartyOID = partyManager.getPartyRolesHistoryForPartyOID(pageModel.getPartyDTO().getOid(), true, roleTypes);
		List<PartyRoleType> types = PartyRoleType.getPartyRoleTypes(ProjectBaseType.PARTY, ProjectBaseType.PARTY, false);
		IDescriptionEntityManager descriptionManager = getDescriptionManager();
		for (PartyRoleDTO partyDto:partyRolesHistoryForPartyOID)
		{
	
			Long subType = partyDto.getSubTypeId();
			for (PartyRoleType type:types){
				if (type.getType()==partyDto.getType())
				{
					role = (type.getDescription());
					break;
				}
			}
			DescriptionDTO descriptionDTO = descriptionManager.findByUniqueID(Integer.valueOf(subType.toString()));
			displayObjects.add(new TableDisplayObject(role+" - "+descriptionDTO.getDescription(),(partyDto.getEffectiveFrom() != null) ? partyDto.getEffectiveFrom() : "",(partyDto.getEffectiveTo() != null) ? partyDto.getEffectiveTo() : ""));
		}
	
		return displayObjects;
	}
	
	private class TableDisplayObject implements Serializable{
		private static final long serialVersionUID = 1L;
		private String column1;
		private Object column2;
		private Object column3;
		
		private TableDisplayObject(String column1, Object column2, Object column3){
			this.column1 = column1;
			this.column2 = column2;
			this.column3 = column3;
		}
		
		public String getColumn1() {
			return column1;
		}

		public Object getColumn2() {
			return column2;
		}

		public Object getColumn3() {
			return column3;
		}
	}
	private IPartyManagement getPartyManager() {	
		try {
			//return PartyManagement.getInstance();
	//MSK#Change :need to remove while migrate	
			return ServiceLocator.lookupService(IPartyManagement.class);
		} catch (NamingException e) {
			throw new CommunicationException("Could not get AgreementManagement Bean",e);
		}		
	}
	
	private IDescriptionEntityManager getDescriptionManager()
	{
		try {
			descriptionEntityManager = ServiceLocator.lookupService(IDescriptionEntityManager.class);
		} catch (NamingException e) {
			throw new CommunicationException(e);
		}
		return descriptionEntityManager;
	}
	
	@Override
	public String getPageName() {
		return "Group Channel Roles History";
	}

}
