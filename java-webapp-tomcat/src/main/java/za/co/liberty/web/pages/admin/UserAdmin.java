package za.co.liberty.web.pages.admin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.util.string.Strings;

import za.co.liberty.business.guicontrollers.userprofiles.IProfileRoleManagement;
import za.co.liberty.business.guicontrollers.userprofiles.IUserAdminManagement;
import za.co.liberty.dto.gui.context.ResultContextItemDTO;
import za.co.liberty.dto.userprofiles.MenuItemDTO;
import za.co.liberty.dto.userprofiles.PartyProfileDTO;
import za.co.liberty.exceptions.data.DataNotFoundException;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.helpers.lookup.ServiceLocator;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.models.PagePanelInfoObject;
import za.co.liberty.web.pages.MaintenanceBasePage;
import za.co.liberty.web.pages.admin.models.UserAdminModel;
import za.co.liberty.web.system.SRSAuthWebSession;

/**
 * Controller page used to administrate / maintain Roles
 * 
 * @author JZB0608 - 06 May 2008
 *
 */
public class UserAdmin extends MaintenanceBasePage<Integer> {

	private static final long serialVersionUID = 104936816257820773L;
	private UserAdminModel pageModel;
	
	/**
	 * Default constructor
	 *
	 */
	public UserAdmin() {
		super(null);
	}

	/**
	 * Default constructor that initialises the page
	 * with the given object
	 * 
	 * @param dto
	 */
	public UserAdmin(PartyProfileDTO dto) {
		super(dto);
	}
	
	public UserAdmin(PartyProfileDTO dto, int currentTab) {
		super(dto,(Integer)currentTab);
	}
	
	@Override
	public Panel createContainerPanel() {
		Panel panel;
		if (pageModel.getSelectedItem() == null) {
			panel = new EmptyPanel(CONTAINER_PANEL_NAME);
		} else {
			PartyProfileDTO dto = pageModel.getSelectedItem();
			if (getEditState() != EditStateType.ADD && dto != null) {
				// Refresh from db
				try {
					dto = getSessionBean().findUser(dto.getProfileOID());
				} catch (DataNotFoundException e) {
					//This should really never happen, if it does throw a runtime
					throw new RuntimeException("Unable to retrieve entity for selected User",e);
				}
				pageModel.setSelectedItem(dto);
			}
			panel = new UserAdminPanel(CONTAINER_PANEL_NAME, pageModel, getEditState(),this);
		}
		panel.setOutputMarkupId(true);
		return panel;
	}

	@Override
	public Button[] createNavigationalButtons() {
		return new Button[] {createSaveButton("button1"), 
				createCancelButton("button2")};
	}

	@Override
	public void doSave_onSubmit() {
		/* Validate that all components first (all tab panels) */
		UserAdminPanel adminPanel = (UserAdminPanel) containerPanel;
		//if (adminPanel.validateAllTabs()==false) {
		//	return;
		//}
		
		/* Save to db */
		PartyProfileDTO dto = pageModel.getSelectedItem();
		boolean isAdd = (dto.getProfileOID()==null);
		if (isAdd) {
			throw new UnsupportedOperationException("System users may not be added");
//			dto = getSessionBean().createUser(dto);
		} else {
			dto =getSessionBean().updateUser(dto, SRSAuthWebSession.get().getSessionUser());
		}
	
		
		invalidatePage();
		
		//MSK#Change this.info(this.getString((isAdd)?"success.add" : "success.modify"));
		this.getSession().info(this.getString((isAdd)?"success.add" : "success.modify"));
		setResponsePage(new UserAdmin(dto,(Integer)pageModel.getCurrentTab()));
	}
	
	@Override
	public Panel createSelectionPanel() {
		/* Add an auto complete selection panel */
		return new UserAdminSelectionPanel<PartyProfileDTO>(SELECTION_PANEL_NAME, "Security ID:",
				pageModel, this, selectionForm, PartyProfileDTO.class) {
			
			private static final long serialVersionUID = -6645144485356877886L;
			private String selectedValue;
			private List<PartyProfileDTO> dto;
			
			@Override
			public Object getNewDtoInstance() {
				PartyProfileDTO dto = new PartyProfileDTO();
				return dto;
			}
			
			@SuppressWarnings("unchecked")
			@Override
			public List<PartyProfileDTO> retrieveChoices(String input) {
				if (Strings.isEmpty(input) || input.length()<3) {
					return Collections.EMPTY_LIST;
				}
				if (selectedValue != null && input.equalsIgnoreCase(selectedValue)) {
					return dto;
				}				
				selectedValue = input;				
				List<PartyProfileDTO> list = new ArrayList(getSessionBean().findFastLaneUserStartingWith(input));
				dto = list;
				return list;
			}
			@Override
			public String renderChoiceValue(PartyProfileDTO dto) {
				return dto.getSecurityID();
			}
			@Override
			public String renderTextValue(PartyProfileDTO dto) {
				return dto.getSecurityID();
			}

			@Override
			public void doProcessSearchResult(AjaxRequestTarget target, ArrayList<ResultContextItemDTO> selectedItemList) {

				if (selectedItemList.size() != 1) {
					// Nothing was selected
					return;
				}					
				
				ResultContextItemDTO selected = selectedItemList.get(0);

				try{
					setResponsePage(new UserAdmin(getSessionBean().findUserByPartyOID(selected.getPartyDTO().getPartyOid())));
				}catch(DataNotFoundException e){
					Logger.getLogger(this.getClass()).error(
							"Not able to find Security ID");
					UserAdmin.this.error("User profile not defined for selected party");
					//UserAdmin.this.error(this.getString("context.required.party"));
					return;
				}

			}
			
		}.setTooltipText(this.getString("tooltip.selection.text"));
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object initialisePageModel(Object object,Integer currentTab) {
		UserAdminModel obj = new UserAdminModel();
		IUserAdminManagement sessionBean = getSessionBean();
		obj.setSelectionList(Collections.EMPTY_LIST);
		obj.setSelectedItem((PartyProfileDTO) object);
		obj.setAllAvailableMenuItems(sessionBean.findAllAvailableMenuItems());
		obj.setAllAvailableRules(sessionBean.findAllAvailableRules());
		obj.setAllAvailableRuleArithmetic(sessionBean.getArithmeticTypess());
		obj.setAllAvailableRuleDataTypes(sessionBean.findAllRuleDataTypes());
		obj.setAllAvailableRoles(sessionBean.findAllAvailableRoles());
		Collections.sort(obj.getAllAvailableMenuItems(), new Comparator<MenuItemDTO>() {
			public int compare(MenuItemDTO o1, MenuItemDTO o2) {
				return o1.getMenuItemDescription().compareTo(
						o2.getMenuItemDescription());
			}
		});
		obj.setCurrentTab((currentTab != null) ? currentTab : -1);
		pageModel = obj;	
		return pageModel;
	}

	@Override
	public String getPageName() {
		return "User Administration";
	}

	@Override
	protected Panel getContextPanel() {
		/* Does not require a panel */
		return new EmptyPanel(CONTEXT_PANEL_NAME);
	}
	
	/**
	 * Get an instance of the managed session bean
	 * 
	 * @return
	 */
	protected IUserAdminManagement getSessionBean() {
		try {
			//IUserAdminManagement sessionBean = UserAdminModel.getSessionBean();
			//MSK:Change
			IUserAdminManagement sessionBean = ServiceLocator.lookupService(IUserAdminManagement.class);
			return sessionBean;
		} /*
			 * catch (CommunicationException e) { Logger.getLogger(this.getClass()).error(
			 * "Unable to initialise session bean for page", e); this.error(e.getMessage());
			 * throw e; }
			 */ catch (NamingException e) {
			// TODO Auto-generated catch block
				 throw new CommunicationException(e);
		}

	}

	@Override
	public List<PagePanelInfoObject> getPagePanelsInfo() {
		// TODO Auto-generated method stub
		return null;
	}	
}
