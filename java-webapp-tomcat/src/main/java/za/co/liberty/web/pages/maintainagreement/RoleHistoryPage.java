package za.co.liberty.web.pages.maintainagreement;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.naming.NamingException;

import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import za.co.liberty.business.guicontrollers.agreement.IAgreementGUIController;
import za.co.liberty.common.domain.TypeVO;
import za.co.liberty.dto.agreement.AgreementDTO;
import za.co.liberty.dto.agreement.AgreementRoleDTO;
import za.co.liberty.dto.agreement.maintainagreement.AgreementRoleGridDTO;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.helpers.lookup.ServiceLocator;
import za.co.liberty.interfaces.agreements.RoleKindType;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.pages.BaseWindowPage;
import za.co.liberty.web.pages.panels.HelperPanel;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGrid;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGridColumn;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGrid.GridSizeUnit;
import za.co.liberty.web.wicket.markup.repeater.data.SortableListDataProvider;

import com.inmethod.grid.DataProviderAdapter;
import com.inmethod.grid.IGridColumn;
import com.inmethod.grid.SizeUnit;

/**
 * Page for displaying role history
 * @author DZS2610
 *
 */
public class RoleHistoryPage extends BaseWindowPage {
	
	private static final long serialVersionUID = 1L;
	private EditStateType editState = EditStateType.VIEW;
	
	private AgreementDTO agreementContext;
	private HistoryPageType type = HistoryPageType.HOME;	
	private List<TypeVO> servicingTypes;
	private boolean useAgreementRolesList = false;
	
	/*
	 * The types of histories this page can display
	 */
	public enum HistoryPageType{HOME,OTHER, SUPERVISOR};
	
	/**
	 * Init all the page variables
	 *
	 */
	private void init(){
		if(agreementContext != null && (agreementContext.getPastHomeRoles() != null || agreementContext.getPastAgreementRoles() != null)){
			if(type == HistoryPageType.HOME){
				this.add(new Label("title","Agreement " + agreementContext.getId()+ " Home Role History"));
			}else{
				this.add(new Label("title","Agreement " + agreementContext.getId()+ " Agreement Role History"));
			}
			this.add(getDataGrid("grid"));
		} else{
			this.add(new Label("title","No valid organisation exists to display history"));
			this.add(new Label("grid",""));
		}
	}	
	
//	public RoleHistoryPage(ModalWindow modalWindow, AgreementDTO agreementDTO,HistoryPageType type){
//		this(modalWindow,agreementDTO,type,null);
//	}
	
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
	public RoleHistoryPage(ModalWindow modalWindow, AgreementDTO agreementDTO,HistoryPageType type,List<TypeVO> servicingTypes){
		this.agreementContext = agreementDTO;
		this.type = type;
		this.servicingTypes = servicingTypes;
		init();
	}
	/**
	 * Get the grid for display on the user role window
	 * @return
	 */
	private SRSDataGrid getDataGrid(String id){		
		List<AgreementRoleGridDTO> gridRoles = new ArrayList<AgreementRoleGridDTO>();			
		IAgreementGUIController agreementGUIController = getAgreementGUIController();
		
		//Cheack which role list to use
		if(!useAgreementRolesList ){
			
		if(type == HistoryPageType.HOME){
			List<AgreementRoleDTO> roles = new ArrayList<AgreementRoleDTO>(agreementContext.getPastHomeRoles());
			if(roles != null){
				for(AgreementRoleDTO role : roles){
					AgreementRoleGridDTO gridRole = new AgreementRoleGridDTO();
					gridRole.setRole(role);				
					gridRoles.add(gridRole);
				}	
			}
		}else if(type == HistoryPageType.OTHER ){
			List<AgreementRoleDTO> roles = new ArrayList<AgreementRoleDTO>(agreementContext.getPastAgreementRoles());
			if(roles != null){
				for(AgreementRoleDTO role : roles){
					if(role.getKind() == RoleKindType.PAYSTO.getKind()){
						//pays to is used in another screen, also used here for defualt on belongs to so we keep this role separate
						//do nothing as the pays to is set up for that tab, if set up on the tab then add it here to display
					}else{
						AgreementRoleGridDTO gridRole = new AgreementRoleGridDTO();
						gridRole.setRole(role);
						//set the other grid display data
						agreementGUIController.setUpAgreementGridRoleData(gridRole);
						gridRoles.add(gridRole);
					}
				}
			}
		}if(type == HistoryPageType.SUPERVISOR){
			List<AgreementRoleDTO> roles = new ArrayList<AgreementRoleDTO>(agreementContext.getPastSupervisorRoles());
			if(roles != null){
				for(AgreementRoleDTO role : roles){
					if(role.getKind() == RoleKindType.PAYSTO.getKind()){
						//pays to is used in another screen, also used here for defualt on belongs to so we keep this role separate
						//do nothing as the pays to is set up for that tab, if set up on the tab then add it here to display
					}else{
						AgreementRoleGridDTO gridRole = new AgreementRoleGridDTO();
						gridRole.setRole(role);
						//set the other grid display data
						agreementGUIController.setUpOtherPartyGridRoleData(gridRole);
						gridRoles.add(gridRole);
					}
				}
			}
		}
	}

		
		SRSDataGrid grid = new SRSDataGrid(id, new DataProviderAdapter(new SortableListDataProvider<AgreementRoleGridDTO>(gridRoles)), getViewableRolesColumns(),null);            
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
		Vector<IGridColumn> cols = new Vector<IGridColumn>();
		
			cols = new Vector<IGridColumn>();				
			if(type == HistoryPageType.HOME ){	
				cols.add(new SRSDataGridColumn<AgreementRoleGridDTO>("role.rolePlayerReference.hierarchyOrganisationTypeName",
						new Model("Type"), "role.rolePlayerReference.hierarchyOrganisationTypeName", "role.rolePlayerReference.hierarchyOrganisationTypeName", EditStateType.VIEW).setInitialSize(140));
				cols.add(new SRSDataGridColumn<AgreementRoleGridDTO>("role.rolePlayerReference.name",
						new Model("Name"), "role.rolePlayerReference.name", "role.rolePlayerReference.name", EditStateType.VIEW).setInitialSize(230));
				cols.add(new SRSDataGridColumn<AgreementRoleGridDTO>("role.rolePlayerReference.externalReference",
						new Model("External Reference"), "role.rolePlayerReference.externalReference", "role.rolePlayerReference.externalReference", EditStateType.VIEW).setInitialSize(140)); 				
			}else if(type == HistoryPageType.OTHER ){	
				cols.add(new SRSDataGridColumn<AgreementRoleGridDTO>("role.rolePlayerReference.oid",
						new Model("SRS ID"), "role.rolePlayerReference.oid", "role.rolePlayerReference.oid", EditStateType.VIEW).setInitialSize(140));
				cols.add(new SRSDataGridColumn<AgreementRoleGridDTO>("agreementParty.name",
						new Model("Name"), "agreementParty.name", "agreementParty.name", EditStateType.VIEW).setInitialSize(230));
//				adding the agreement branch name
				cols.add(new SRSDataGridColumn<AgreementRoleGridDTO>("role.rolePlayerReference.branchName",
						new Model("Branch Name"), "role.rolePlayerReference.branchName", "role.rolePlayerReference.branchName", EditStateType.VIEW).setInitialSize(230));
			   
//				adding the agreement unit name
				cols.add(new SRSDataGridColumn<AgreementRoleGridDTO>("agreementHome.name",
						new Model("Home Name"), "agreementHome.name", "agreementHome.name", EditStateType.VIEW).setInitialSize(230));
				
				//add in the role kind selection
//				add in the role kind selection
				cols.add(new SRSDataGridColumn<AgreementRoleGridDTO>("role.kind",
						new Model("Role Kind"), "role.kind", "role.kind",
						EditStateType.VIEW) {
					private static final long serialVersionUID = 1L;
					@Override
					public Panel newCellPanel(WebMarkupContainer parent,
							String componentId, IModel rowModel,
							String objectProperty, EditStateType state,
							final AgreementRoleGridDTO data) {	
						//create label with type and display	
							RoleKindType role = RoleKindType.getRoleKindTypeForKind(data.getRole().getKind().intValue());
							return HelperPanel.getInstance(componentId, new Label("value",(role != null) ? role.getDescription() : ""));
					}
				}.setInitialSize(150));
				cols.add(new SRSDataGridColumn<AgreementRoleGridDTO>("role.type",
						new Model("Relationship Type"), "role.type", "role.type",
						EditStateType.VIEW) {
					private static final long serialVersionUID = 1L;

					@Override
					public Panel newCellPanel(WebMarkupContainer parent,
							String componentId, IModel rowModel,
							String objectProperty, EditStateType state,
							AgreementRoleGridDTO data) {	
						//create label with type and display	
						TypeVO type = getServicingType(data.getRole().getType());						
						return HelperPanel.getInstance(componentId, new Label("value",(type != null) ? type.getDescription(): ""));
					}
				}.setInitialSize(215));
    		}
			else if(type == HistoryPageType.SUPERVISOR){
				cols.add(new SRSDataGridColumn<AgreementRoleGridDTO>("agreementParty.name",
						new Model("Name"), "agreementParty.name", "agreementParty.name", EditStateType.VIEW).setInitialSize(230));

				cols.add(new SRSDataGridColumn<AgreementRoleGridDTO>("agreementParty.uacfID",
						new Model("UACF ID"), "agreementParty.uacfID", "agreementParty.uacfID", EditStateType.VIEW).setInitialSize(140));
				
				//add in the role kind selection
//				add in the role kind selection
				cols.add(new SRSDataGridColumn<AgreementRoleGridDTO>("role.kind",
						new Model("Role Kind"), "role.kind", "role.kind",
						EditStateType.VIEW) {
					private static final long serialVersionUID = 1L;
					@Override
					public Panel newCellPanel(WebMarkupContainer parent,
							String componentId, IModel rowModel,
							String objectProperty, EditStateType state,
							final AgreementRoleGridDTO data) {	
						//create label with type and display	
							RoleKindType role = RoleKindType.getRoleKindTypeForKind(data.getRole().getKind().intValue());
							return HelperPanel.getInstance(componentId, new Label("value",(role != null) ? role.getDescription() : ""));
					}
				}.setInitialSize(150));
				cols.add(new SRSDataGridColumn<AgreementRoleGridDTO>("role.type",
						new Model("Sub Type"), "role.type", "role.type",
						EditStateType.VIEW) {
					private static final long serialVersionUID = 1L;

					@Override
					public Panel newCellPanel(WebMarkupContainer parent,
							String componentId, IModel rowModel,
							String objectProperty, EditStateType state,
							AgreementRoleGridDTO data) {	
						//create label with type and display	
						TypeVO type = getServicingType(data.getRole().getType());						
						return HelperPanel.getInstance(componentId, new Label("value",(type != null) ? type.getName(): ""));
					}
				}.setInitialSize(215));
			}
			cols.add(new SRSDataGridColumn<AgreementRoleGridDTO>("role.effectiveFrom",new Model("Start Date"),"role.effectiveFrom","role.effectiveFrom", EditStateType.VIEW).setInitialSize(100)); 
			cols.add(new SRSDataGridColumn<AgreementRoleGridDTO>("role.effectiveTo",new Model("End Date"),"role.effectiveTo","role.effectiveTo", EditStateType.VIEW).setInitialSize(100)); 
	
			return cols;
	}	
	
	@Override
	public String getPageName() {		
		return "Roles";
	}	
	
	
	private TypeVO getServicingType(long typeOID){
		if(servicingTypes != null){
			for(TypeVO type : servicingTypes){
				if(type.getOid() == typeOID){
					return type;
				}
			}
		}
		return null;
	}
	
	/**
	 * Get the agreement manager
	 * @return
	 */
	private IAgreementGUIController getAgreementGUIController() {
		try{
			return ServiceLocator.lookupService(IAgreementGUIController.class);
		} catch (NamingException e) {
				throw new CommunicationException(e);
		}		
	}


}
