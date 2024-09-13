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
import za.co.liberty.dto.agreement.maintainagreement.AgreementPartnerRolesGridDTO;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.helpers.lookup.ServiceLocator;
import za.co.liberty.interfaces.agreements.ProjectBaseType;
import za.co.liberty.interfaces.agreements.RoleKindType;
import za.co.liberty.persistence.agreement.IAgreementEntityManager;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.pages.BaseWindowPage;
import za.co.liberty.web.pages.panels.HelperPanel;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGrid;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGrid.GridSizeUnit;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGridColumn;
import za.co.liberty.web.wicket.markup.repeater.data.SortableListDataProvider;

import com.inmethod.grid.DataProviderAdapter;
import com.inmethod.grid.IGridColumn;
import com.inmethod.grid.SizeUnit;

/**
 * Page for displaying inverse agreement roles
 * @author MXM1904
 *
 */
public class InverseAgreementRolesPage extends BaseWindowPage {

	private static final long serialVersionUID = 1L;

	private EditStateType editState = EditStateType.VIEW;

	private transient IAgreementEntityManager agreementEntityManager;

	private ModalWindow modalWindow;

	private AgreementDTO agreementContext;

	private InverseAgreementRolesPageType type = InverseAgreementRolesPageType.HOME;
	
	private RoleKindType roleKinds = RoleKindType.ALL;

	private List<TypeVO> servicingTypes;

	
    

	/*
	 * The types of histories this page can display
	 */
	public enum InverseAgreementRolesPageType {
		HOME, OTHER
	};



	/**
	 * Init all the page variables
	 *
	 */
	private void init() {

		if(agreementContext != null && (agreementContext.getPastHomeRoles() != null || agreementContext.getPastAgreementRoles() != null)){
			if(type == InverseAgreementRolesPageType.HOME){
				this.add(new Label("title", "Agreement " + agreementContext.getId()	+ " - "));
				this.add(getDataGrid("inverseRoleGrids"));
			}else{
				this.add(new Label("title","No valid agreement exists to display Inverse Roles"));
				this.add(new Label("inverseRoleGrids", ""));
			}
			
		}
		
		
	}

	//	public RoleHistoryPage(ModalWindow modalWindow, AgreementDTO agreementDTO,HistoryPageType type){
	//		this(modalWindow,agreementDTO,type,null);
	//	}

	public EditStateType getEditState() {
		return editState;
	}

	/**
	 * Get the agreement entity manager
	 * @return
	 */
	private IAgreementEntityManager getAgreementEntityManager() {
		if (agreementEntityManager == null) {
			try {
				agreementEntityManager = ServiceLocator
						.lookupService(IAgreementEntityManager.class);
			} catch (NamingException e) {
				throw new CommunicationException(e);
			}
		}
		return agreementEntityManager;
	}

	/**
	 * Use this constructor is and over loaded version of the above
	 * @param modalWindow
	 * @param agreementDTO
	 * @param type
	 * @param servicingTypes
	 * @author MXM1904
	 * 	 * 
	 */
	public InverseAgreementRolesPage(ModalWindow modalWindow,
			AgreementDTO agreementDTO, InverseAgreementRolesPageType type,List<TypeVO> servicingTypes) {
		this.modalWindow = modalWindow;
		this.agreementContext = agreementDTO;
		this.servicingTypes = servicingTypes;
		this.type = type;
		
		init();

	}

	/**
	 * Get the inverseRolesGrids for display on the user role window
	 * @return
	 */
	private SRSDataGrid getDataGrid(String id) {
		List<AgreementPartnerRolesGridDTO> gridRoles = new ArrayList<AgreementPartnerRolesGridDTO>();
		IAgreementGUIController agreementGUIController = getAgreementGUIController();

		//Show all the roles for the selected agreement number
		List<RoleKindType> list = RoleKindType.getAllRoleKindType(ProjectBaseType.AGREEMENT,ProjectBaseType.AGREEMENT);
		RoleKindType [] typeList = (RoleKindType[]) list.toArray(new RoleKindType[0]);
		 

		List<AgreementRoleDTO> agreementRolesList = new ArrayList<AgreementRoleDTO>(0);
		List<Long> rolePlayerIdList = new ArrayList<Long>();
		rolePlayerIdList.add(agreementContext.getId());
		
		// * SSM2707 Market Integration 28/09/2015 SWETA MENON Begin */
		// Check if the agreement in context is a BC. If yes, HasServicingPanel
		// role needs to be added to the list of roles displayed.
		List<AgreementRoleDTO> hasServicingPanelAgmtRoleList = getAgreementEntityManager()
				.findAgreementRolesOfTypeForAgreements(rolePlayerIdList,
						RoleKindType.HASSERVICINGPANEL, AgreementRoleDTO.class,
						true);

		if (hasServicingPanelAgmtRoleList != null && !hasServicingPanelAgmtRoleList.isEmpty()) {
			AgreementRoleDTO agreementRoleDTO = hasServicingPanelAgmtRoleList.get(0);
			if (agreementRoleDTO != null
					&& agreementRoleDTO.getRolePlayerID() != null) {
				rolePlayerIdList.add(agreementRoleDTO.getRolePlayerID());
			}

		}
		// * SSM2707 Market Integration 28/09/2015 SWETA MENON End */
		
		
		agreementRolesList = getAgreementEntityManager().findAgreementRolesOfTypeForRolePlayers(rolePlayerIdList, typeList,AgreementRoleDTO.class);
		
		
		if (agreementRolesList != null) {
			for (AgreementRoleDTO role : agreementRolesList) {
				// * SSM2707 Market Integration 28/09/2015 SWETA MENON Begin */
				// If the role is HASSERVICINGPANEL, skip the role
				if (role.getKind() == RoleKindType.HASSERVICINGPANEL.getKind()) {
					continue;
				}
				// * SSM2707 Market Integration 28/09/2015 SWETA MENON End */
				AgreementPartnerRolesGridDTO gridRole = new AgreementPartnerRolesGridDTO();
				//Filter all the Role so that only specific ones can be seen 
			    gridRole.setRole(role);
				gridRole.setAgreementNr(agreementContext.getId());
				agreementGUIController.setUpAgreementPartnerRolesGridData(gridRole);
				gridRoles.add(gridRole);

			}
		}

		SRSDataGrid inverseRolesGrids = new SRSDataGrid(id, new DataProviderAdapter(
				new SortableListDataProvider<AgreementPartnerRolesGridDTO>(gridRoles)),
				getPartnerRoleColumns(), null);
		inverseRolesGrids.setCleanSelectionOnPageChange(false);
		inverseRolesGrids.setClickRowToSelect(false);
		//grid.setContentHeight(100, SizeUnit.PX);
		inverseRolesGrids.setAllowSelectMultiple(true);
		inverseRolesGrids.setGridWidth(100, GridSizeUnit.PERCENTAGE);
		inverseRolesGrids.setRowsPerPage(10);
		inverseRolesGrids.setVisible(true);
		inverseRolesGrids.setContentHeight(150, SizeUnit.PX);
		return inverseRolesGrids;
	}

	/**
	 * Get the columns for the Partner inverseRolesGrids
	 * @return
	 */
	private List<IGridColumn> getPartnerRoleColumns() {				
		Vector<IGridColumn> cols = new Vector<IGridColumn>();	
		
	
//		add in the uacfid column
		cols.add(new SRSDataGridColumn<AgreementPartnerRolesGridDTO>("kind",
				new Model("Role Description "), "kind", "kind",
				EditStateType.VIEW) {

					@Override
					public Panel newCellPanel(WebMarkupContainer parent,
							String componentId, IModel rowModel,
							String objectProperty, EditStateType state,
							final AgreementPartnerRolesGridDTO data) {	
							//create label with type and display	
						    //check to see if the role kind has an inverse name and if not then use the RoleKindType class					
						    String roleInverseName = "";
						    RoleKindType role = RoleKindType.getRoleKindTypeForKind(data.getRole().getKind().intValue());
						    if(role != null){
						    	roleInverseName = role.getInverseName();
						    }else{
						    	roleInverseName = "";
						    }
						    
							return HelperPanel.getInstance(componentId, new Label("value",(roleInverseName != null) ? roleInverseName : ""));
					}

				}.setInitialSize(120));
//		add in the name column
		cols.add(new SRSDataGridColumn<AgreementPartnerRolesGridDTO>("Name",
				new Model("Name"), "Name", "Name", EditStateType.VIEW).setInitialSize(140).setWrapText(true));			
		//		add in the start date column
		cols.add(new SRSDataGridColumn<AgreementPartnerRolesGridDTO>("agreementRoleKind",
				new Model("Agreement number"), "agreementRoleKind", "agreementRoleKind",EditStateType.VIEW).setInitialSize(120));
		//		add in the start date column

		cols.add(new SRSDataGridColumn<AgreementPartnerRolesGridDTO>("effectiveFrom",
				new Model("Start Date"), "effectiveFrom", "effectiveFrom", EditStateType.VIEW).setInitialSize(80));	
//		add in the end date column
		cols.add(new SRSDataGridColumn<AgreementPartnerRolesGridDTO>("role.effectiveTo",
				new Model("End Date"), "effectiveTo", "effectiveTo", EditStateType.VIEW).setInitialSize(80));

		return cols;
	}

	@Override
	public String getPageName() {
		return "Roles where this agreement is the roleplayer";
	}

	private TypeVO getServicingType(long typeOID) {
		if (servicingTypes != null) {
			for (TypeVO type : servicingTypes) {
				if (type.getOid() == typeOID) {
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
		try {
			return ServiceLocator.lookupService(IAgreementGUIController.class);
		} catch (NamingException e) {
			throw new CommunicationException(e);
		}
	}

}
