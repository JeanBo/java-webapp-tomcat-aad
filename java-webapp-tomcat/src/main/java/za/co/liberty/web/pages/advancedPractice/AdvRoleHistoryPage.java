package za.co.liberty.web.pages.advancedPractice;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Vector;

import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.Model;

import za.co.liberty.business.guicontrollers.advancedPractice.IAdvancedPracticeGUIController;
import za.co.liberty.business.guicontrollers.hierarchy.IHierarchyGUIController;
import za.co.liberty.business.guicontrollers.request.IRequestEnquiryGuiController;
import za.co.liberty.dto.advancedPractice.AdvancedPracticeDTOGrid;
import za.co.liberty.dto.advancedPractice.AdvancedPracticeManagerDTO;
import za.co.liberty.dto.advancedPractice.AdvancedPracticeMemberDTO;
import za.co.liberty.exceptions.SystemException;
import za.co.liberty.exceptions.data.DataNotFoundException;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.helpers.lookup.ServiceLocator;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.pages.BaseWindowPage;
import za.co.liberty.web.pages.advancedPractice.model.AdvancedPracticePanelModel;
import za.co.liberty.web.pages.maintainagreement.model.MaintainAgreementPageModel;
import za.co.liberty.web.pages.panels.HelperPanel;
import za.co.liberty.web.system.EJBReferences;
import za.co.liberty.web.system.SRSAuthWebSession;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGrid;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGridColumn;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGrid.GridSizeUnit;
import za.co.liberty.web.wicket.markup.repeater.data.SortableListDataProvider;

import com.inmethod.grid.DataProviderAdapter;
import com.inmethod.grid.IGridColumn;
import com.inmethod.grid.SizeUnit;

public class AdvRoleHistoryPage extends BaseWindowPage {
	
	private static final long serialVersionUID = 1L;
	
	private ModalWindow modalWindow;
	private AdvancedPracticePanelModel panelModel;
	private HistoryType type = HistoryType.MEMBER;
	private long agreementNum = 0L;
	private List<? extends AdvancedPracticeMemberDTO> advPractList;
	
	private transient IAdvancedPracticeGUIController advGUIController = getAdvancedPracticeGUIController();
	
	public static final Logger logger = Logger.getLogger(AdvRoleHistoryPage.class);
	
	/**
	 * The types of histories this page can display
	 *
	 * Added for the need to show the advanced practiceRole types in agreement
	 * screen which an agreements has
	 */ 
	public enum HistoryType{MANAGER,MEMBER,MANAGERROLE,MEMBERROLE};


	/**
	 * Init all the page variables
	 *
	 */
	private void init(){
		if(panelModel != null && panelModel.getAdvancedPracticeDTO() != null && panelModel.getAdvancedPracticeDTO().getOid() > 0){
			if(type == HistoryType.MANAGER){
				this.add(new Label("title",panelModel.getAdvancedPracticeDTO().getBusinessName()+ " Manager History"));
			}else if(type == HistoryType.MEMBER){
				this.add(new Label("title",panelModel.getAdvancedPracticeDTO().getBusinessName()+ " Member History"));
			}
			this.add(getDataGrid("grid"));
		} else if(this.getAgreementNum() > 0 && type == HistoryType.MANAGERROLE ||type == HistoryType.MEMBERROLE){
			
			//Make sure that no grid is build if no roles exists
			try {
				advPractList = this.getUpPracticeRoles(this.getAgreementNum());
				if(advPractList == null ){
					this.add(new Label("title"," No active Advanced Practice Roles exists to display for this agreement."));
					this.add(new Label("grid",""));
				}else{
				this.add(new Label("title"," Agreement number [ " + this.getAgreementNum()+ " ] has  Advanced Practice Role(s) "));	
				this.add(getDataGrid("grid"));
				}
				
			} catch (DataNotFoundException e) {
			}
			

		}
		
		else{
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
	public AdvRoleHistoryPage(ModalWindow modalWindow,Object panelModelObj ,HistoryType type){
		
		if (panelModelObj instanceof AdvancedPracticePanelModel) {
			this.modalWindow = modalWindow;	
			this.panelModel = (AdvancedPracticePanelModel)panelModelObj;				
		} else if(panelModelObj instanceof MaintainAgreementPageModel){
			long  longAgm =  ((MaintainAgreementPageModel)panelModelObj).getMaintainAgreementDTO().getAgreementDTO().getId();
			this.setAgreementNum(longAgm);
		}
		this.type = type;
		init();
	}
	
	/**
	 * Get the grid for display on the user role window
	 * @return
	 */
	private SRSDataGrid getDataGrid(String id){		
		List<AdvancedPracticeDTOGrid> managersMembersDTO = null;//panelModel.getManagersGrids();	
		try {

			logger.info("About to print gridRoles");
		
			
			if(type == HistoryType.MANAGER){
				managersMembersDTO = panelModel.getManagersHistoryGrids();
			}else if(type == HistoryType.MEMBER){
				 managersMembersDTO = panelModel.getMembersHistoryGrids();
			} if(this.getAgreementNum()> 0 && type == HistoryType.MANAGERROLE ||type == HistoryType.MEMBERROLE){
				
					 if (advPractList != null) {
						managersMembersDTO  = new ArrayList<AdvancedPracticeDTOGrid>();
						for (Object obj : advPractList) {


							if (obj instanceof AdvancedPracticeManagerDTO) {
								AdvancedPracticeDTOGrid practiceDTOGrid = new AdvancedPracticeDTOGrid();
								practiceDTOGrid.setRole((AdvancedPracticeManagerDTO)obj);
								managersMembersDTO.add(practiceDTOGrid);
								break;
							}
							if (obj instanceof AdvancedPracticeMemberDTO) {
								
								AdvancedPracticeDTOGrid practiceDTOGrid = new AdvancedPracticeDTOGrid();
								practiceDTOGrid.setRole((AdvancedPracticeMemberDTO)obj);
								managersMembersDTO.add(practiceDTOGrid);
								break;
							}
						}

					}
				
			}
			
			
			
		} catch (SystemException e) {
			error("An error occured getting the history: " + e.getMessage());
		}		

		SRSDataGrid grid = new SRSDataGrid(id, new DataProviderAdapter(new SortableListDataProvider<AdvancedPracticeDTOGrid>(managersMembersDTO)), getManagerMemberColumns(type),null);       
        grid.setCleanSelectionOnPageChange(false);
        grid.setClickRowToSelect(false);        
        //grid.setContentHeight(100, SizeUnit.PX);
        grid.setAllowSelectMultiple(true);
        grid.setGridWidth(100, GridSizeUnit.PERCENTAGE);
        grid.setRowsPerPage(10);
        grid.setContentHeight(150, SizeUnit.PX);
        return grid;
	}
	
	HelperPanel ispracticeShare;
	/**
	 * Get the list of node columns for the grid
	 * 
	 * @return
	 */ 
	private List<IGridColumn> getManagerMemberColumns(final HistoryType columType) {
		Vector<IGridColumn> cols = new Vector<IGridColumn>();

		// add in the name column(Display only col)
		if (type == HistoryType.MANAGER || type == HistoryType.MEMBER) {
		cols.add(new SRSDataGridColumn<AdvancedPracticeDTOGrid>(
				"agreementParty.name", new Model("Name"),
				"agreementParty.name", "agreementParty.name", getEditState())
				.setInitialSize(230));
		cols.add(new SRSDataGridColumn<AdvancedPracticeDTOGrid>(
				"role.agreementNumber", new Model("Agreement Number"),
				"role.agreementNumber", "role.agreementNumber", getEditState())
				.setInitialSize(111));

		if (type == HistoryType.MANAGER) {

			cols.add(new SRSDataGridColumn<AdvancedPracticeDTOGrid>(
					"practiceSharePercentage", new Model("Practice Share"),
					"practiceSharePercentage", "practiceSharePercentage",
					getEditState()).setInitialSize(60));

		}
		cols.add(new SRSDataGridColumn<AdvancedPracticeDTOGrid>(
				"role.effectiveFrom", new Model("Start Date"),
				"role.effectiveFrom", "role.effectiveFrom", getEditState())
				.setInitialSize(95));

		cols.add(new SRSDataGridColumn<AdvancedPracticeDTOGrid>(
				"role.effectiveTo", new Model("End Date"), "role.effectiveTo",
				"role.effectiveTo", getEditState()).setInitialSize(95));
		
	} else  if(type == HistoryType.MANAGERROLE || type == HistoryType.MEMBERROLE) {
		
		cols.add(new SRSDataGridColumn<AdvancedPracticeDTOGrid>(
				"role.roleKindDescription", new Model("Role Kind"),
				"role.roleKindDescription", "role.roleKindDescription", getEditState())
				.setInitialSize(260));
		cols.add(new SRSDataGridColumn<AdvancedPracticeDTOGrid>(
				"role.rolePlayerReference.name", new Model("Practice Name"),
				"role.rolePlayerReference.name", "role.rolePlayerReference.name", getEditState())
				.setInitialSize(260));
		cols.add(new SRSDataGridColumn<AdvancedPracticeDTOGrid>(
				"role.effectiveFrom", new Model("Start Date"),
				"role.effectiveFrom", "role.effectiveFrom", getEditState())
				.setInitialSize(95));

		cols.add(new SRSDataGridColumn<AdvancedPracticeDTOGrid>(
				"role.effectiveTo", new Model("End Date"), "role.effectiveTo",
				"role.effectiveTo", getEditState()).setInitialSize(95));
	}
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
	
	
	/**
	 * Get the AdvancedPracticeGUIController bean
	 * 
	 * @return
	 */
	private IAdvancedPracticeGUIController getAdvancedPracticeGUIController() {
		if (advGUIController == null) {
			try {
				advGUIController = ServiceLocator
						.lookupService(IAdvancedPracticeGUIController.class);
			} catch (NamingException e) {
				throw new CommunicationException(e);
			}
		}
		return advGUIController;
	}


	public EditStateType getEditState() {
		// will disable any modification if there are any requests pending auth
			return EditStateType.VIEW;			
	}

	public long getAgreementNum() {
		return agreementNum;
	}

	public void setAgreementNum(long agreementNum) {
		this.agreementNum = agreementNum;
	}

	private List<? extends AdvancedPracticeMemberDTO> getUpPracticeRoles(long agreementNum) throws DataNotFoundException{ //this.getAgreementNum()
		
		return advGUIController.getUpPracticeRoles(agreementNum);
	}
	
}
