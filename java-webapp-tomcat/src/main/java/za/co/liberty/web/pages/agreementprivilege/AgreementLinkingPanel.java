package za.co.liberty.web.pages.agreementprivilege;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.extensions.markup.html.tabs.TabbedPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

import za.co.liberty.dto.agreementprivileges.AgreementPrivilegesDataDTO;
import za.co.liberty.dto.userprofiles.ContextDTO;
import za.co.liberty.dto.userprofiles.ContextPartyDTO;
import za.co.liberty.exceptions.security.TabAccessException;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.data.pages.ITabbedPageModel;
import za.co.liberty.web.pages.agreementprivilege.model.AgreementLinkingPageModel;
import za.co.liberty.web.pages.interfaces.IMaintenanceParent;
import za.co.liberty.web.pages.panels.HelperPanel;
import za.co.liberty.web.pages.panels.MaintenanceTabbedPanel;
import za.co.liberty.web.system.SRSAuthWebSession;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGrid;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGridColumn;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGrid.GridSizeUnit;
import za.co.liberty.web.wicket.markup.html.tabs.CachingTab;
import za.co.liberty.web.wicket.markup.html.tabs.StatefullCachingTab;
import za.co.liberty.web.wicket.markup.repeater.data.SortableListDataProvider;

import com.inmethod.grid.DataProviderAdapter;
import com.inmethod.grid.IGridColumn;

/**
 * @author dzs2610
 *
 */
public class AgreementLinkingPanel extends MaintenanceTabbedPanel {
	
	private static final long serialVersionUID = 1L;
	
	//private AgreementLinkingPageModel pageModel;
	
	/**
	 * @param id
	 * @param pageModel
	 * @param editState
	 * @throws TabAccessException 
	 */
	public AgreementLinkingPanel(String id, ITabbedPageModel<AgreementPrivilegesDataDTO> pageModel,
			EditStateType editState,IMaintenanceParent parent){
		super(id, pageModel, editState,parent);		
		//this.pageModel = (AgreementLinkingPageModel) pageModel;
	}

	/* (non-Javadoc)
	 * @see za.co.liberty.web.pages.panels.MaintenanceTabbedPanel#initialiseTabs(java.util.List)
	 */
	@Override
	public void initialiseTabs(List<AbstractTab> tabList) {
//		/* Add role Panel */		
		ContextDTO dto = SRSAuthWebSession.get().getContextDTO();// this.getPageContextDTO();
		ContextPartyDTO partydto = dto.getPartyContextDTO();
		String uacfid = "";	
		if(partydto != null && partydto.getUacfID() != null && !partydto.getUacfID().equals("")){
			uacfid = " ("+partydto.getUacfID()+")";
		}
		tabList.add(new CachingTab(new Model("Viewable by selected Intermediary" + uacfid)) {
			private static final long serialVersionUID = 1L;			
			@Override
			public Panel createPanel(String id) {
				return HelperPanel.getInstance(TabbedPanel.TAB_PANEL_ID, getDataGrid());
			}		
		});	
		if(((AgreementLinkingPageModel)pageModel).getAgreementNo() != 0){
			tabList.add(new StatefullCachingTab(new Model("Access Granted to Others"), AgreementLinkingPanel.this) {
				private static final long serialVersionUID = 1L;
				@Override
				public Panel createPanel(String id) {				
					return new AgreementAssignmentPanel(TabbedPanel.TAB_PANEL_ID,
							(AgreementLinkingPageModel)pageModel, 
							AgreementLinkingPanel.this.getEditState()){

								@Override
								public EditStateType getEditState() {
									// TODO Auto-generated method stub
									return super.getEditState();
								}
						
					};
				}
			});
		}
	}
	
	
	/**
	 * creates the viewable by me grid
	 * @return
	 */
	private SRSDataGrid getDataGrid(){		
		List<AgreementPrivilegesDataDTO> data = new ArrayList<AgreementPrivilegesDataDTO>(0);
		//if (((AgreementLinkingPageModel)pageModel).getAgreementNo() != 0) {			
			data.addAll(((AgreementLinkingPageModel)pageModel).getExplicitAgreements());
			data.addAll(((AgreementLinkingPageModel)pageModel).getOwnAgreementList());
			data.addAll(((AgreementLinkingPageModel)pageModel).getReportToAgreements());
		//}
		SRSDataGrid grid = new SRSDataGrid("value", new DataProviderAdapter(new SortableListDataProvider<AgreementPrivilegesDataDTO>(data)), getViewableAgreementsColumns(),null);            
        grid.setCleanSelectionOnPageChange(false);
        grid.setClickRowToSelect(false);        
        //grid.setContentHeight(100, SizeUnit.PX);
        grid.setAllowSelectMultiple(true);
        grid.setGridWidth(100, GridSizeUnit.PERCENTAGE);
        return grid;
	}
	
	private List<IGridColumn> getViewableAgreementsColumns() {
			Vector<IGridColumn> cols = new Vector<IGridColumn>(8);
			cols.add(new SRSDataGridColumn<AgreementPrivilegesDataDTO>("explicitLinkedAgreementDescription",new Model("Explicitly Linked Agreement"),"explicitLinkedAgreementDescription","explicitLinkedAgreementDescription", EditStateType.VIEW).setInitialSize(160)); 
			cols.add(new SRSDataGridColumn<AgreementPrivilegesDataDTO>("ownAgreementDescription",new Model("Own Agreement"),"ownAgreementDescription","ownAgreementDescription", EditStateType.VIEW).setInitialSize(100)); 
			cols.add(new SRSDataGridColumn<AgreementPrivilegesDataDTO>("reportingStaffAgreementDescription",new Model("Reporting Staff Agreement"),"reportingStaffAgreementDescription","reportingStaffAgreementDescription", EditStateType.VIEW).setInitialSize(160)); 
			cols.add(new SRSDataGridColumn<AgreementPrivilegesDataDTO>("thirteenDigitCode",new Model("Thirteen Digit Code"),"thirteenDigitCode","thirteenDigitCode", EditStateType.VIEW).setInitialSize(120)); 
			cols.add(new SRSDataGridColumn<AgreementPrivilegesDataDTO>("agreementStatus",new Model("Agreement Status"),"agreementStatus","agreementStatus", EditStateType.VIEW).setInitialSize(110)); 
			cols.add(new SRSDataGridColumn<AgreementPrivilegesDataDTO>("name",new Model("Name"),"name","name", EditStateType.VIEW).setInitialSize(150)); 
			cols.add(new SRSDataGridColumn<AgreementPrivilegesDataDTO>("agreementType",new Model("Agreement Description"),"agreementType","agreementType", EditStateType.VIEW).setInitialSize(150)); 
			//only add this column when admin person logs in
//			cols.add(new SRSDataGridColumn<AgreementPrivilegesDataDTO>("agreementRolesCommaDelimitedString",new Model("Roles"),"agreementRolesCommaDelimitedString","agreementRolesCommaDelimitedString", EditStateType.VIEW){
//				@Override
//				public Panel newCellPanel(WebMarkupContainer parent, String componentId, IModel rowModel, String objectProperty, EditStateType state, AgreementPrivilegesDataDTO data) {
//					Label lab = new Label("value",new PropertyModel(data,objectProperty));
//					//add a tooltip panel to display the rules
//					List<ProfileRoleDTO> roles = data.getPartyProfileRoles();
//					StringBuilder rolesBuilder = new StringBuilder(100);
//					if(roles != null){
//						for(ProfileRoleDTO role : roles){
//							rolesBuilder.append(role.getRoleName() + " (" + role.getRoleShortDescription() +")\n\n");
//						}
//					}
//					lab.add(new AttributeModifier("title", true, new Model(rolesBuilder.toString())));
//					return HelperPanel.getInstance(componentId,lab);			
//				}
//				
//			}.setInitialSize(100)); 
			return cols;
	}	
}
