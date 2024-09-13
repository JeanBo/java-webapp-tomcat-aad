package za.co.liberty.web.pages.admin;

import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.Panel;

import za.co.liberty.business.guicontrollers.userprofiles.IMenuManagement;
import za.co.liberty.dto.userprofiles.MenuItemDTO;
import za.co.liberty.exceptions.UnResolvableException;
import za.co.liberty.exceptions.data.DataNotFoundException;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.exceptions.fatal.InconsistentConfigurationException;
import za.co.liberty.helpers.lookup.ServiceLocator;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.pages.MaintenanceBasePage;
import za.co.liberty.web.pages.admin.models.MenuItemModel;
import za.co.liberty.web.pages.panels.DefaultMaintenanceSelectionPanel;
import za.co.liberty.web.system.EJBReferences;
import za.co.liberty.web.system.SRSAuthWebSession;

/**
 * Base web page for Menu Item Administration
 * 
 * @author jzb0608 - 22 Apr 2008
 * 
 */
public class MenuItemAdmin extends MaintenanceBasePage<Object> {

	private static final long serialVersionUID = 9067518105519936363L;

	/* Data model for page */
	private MenuItemModel pageModel;

	
	/**
	 * Default constructor
	 * 
	 */
	public MenuItemAdmin() {
		this(null);
	}

	/**
	 * 
	 * @param dto
	 */
	public MenuItemAdmin(MenuItemDTO dto) {
		super(dto);
	}
	
	@Override
	public void doSave_onSubmit() {
		MenuItemDTO newDto = null;
		
		/* Save to db */
		try {
			MenuItemDTO dto = pageModel.getSelectedItem();
			System.out.println("MSK#dto.getMenuItemID()--------------------------------------------"+dto.getMenuItemID());
			if (dto.getMenuItemID()==null) {
				newDto = getSessionBean().createMenuItem(dto, SRSAuthWebSession.get().getSessionUser());
			} else {
				newDto =getSessionBean().updateMenuItem(dto);
			}
			/* Will now reinitialize the page panels */
			//((SRSApplication)this.getApplication()).INTIALIZE_PAGE_MENU_ITEMS = true;
		} catch (Exception e) {
			String message = (e.getMessage()!=null)? e.getMessage() : 
				"An error occurred while processing your request";
			this.error(message);
			logger.error("An error occured while storing a Menu Item",e);
			return;
		}
		
		invalidatePage();		
		this.info("Record was saved successfully");
		setResponsePage(new MenuItemAdmin(newDto));
	}

	/**
	 * Get an instance of the managed session bean
	 * 
	 * @return
	 */
	private IMenuManagement getSessionBean() {
		try {
			/*MSK#need to uncomment later 
			 * return (IMenuManagement) SRSAuthWebSession.get().getEJBReference(
			 * EJBReferences.MENU_MANAGEMENT);
			 */
			return ServiceLocator.lookupService(IMenuManagement.class);
		} /*
			 * catch (CommunicationException e) { Logger.getLogger(this.getClass()).error(
			 * "Unable to initialise session bean for page", e); this.error(e.getMessage());
			 * throw e; } catch (UnResolvableException e) {
			 * Logger.getLogger(this.getClass()).error(
			 * "Unable to initialise session bean for page", e); this.error(e.getMessage());
			 * throw e; }
			 */ catch (NamingException e) {
			// TODO Auto-generated catch block
			throw new CommunicationException(e);
		}

	}

	@Override
	public String getPageName() {
		return "Menu Item Administration";
	}

	@Override
	protected Panel getContextPanel() {
		/* Does not require a panel */
		return new EmptyPanel(CONTEXT_PANEL_NAME);
	}
	


	@Override
	public Panel createContainerPanel() {
		Panel panel;
		if (pageModel.getSelectedItem() == null) {
			panel = new EmptyPanel(CONTAINER_PANEL_NAME);
		} else {
			MenuItemDTO dto = pageModel.getSelectedItem();
			if (getEditState() != EditStateType.ADD && dto != null) {
				// Refresh from db
				try {
					dto = getSessionBean().findMenuItem(dto.getMenuItemID());
				} catch (DataNotFoundException e) {
					// This should not happen, if it does throw this
					throw new InconsistentConfigurationException(
							"Could not retrieve selected menu item",e);
				}
				pageModel.setSelectedItem(dto);
			}
			
			panel = new MenuItemAdminPanel(CONTAINER_PANEL_NAME, dto, getEditState());
			
		}
		panel.setOutputMarkupId(true);
		return panel;
	}

	@SuppressWarnings("unchecked")
	@Override
	public DefaultMaintenanceSelectionPanel createSelectionPanel() {
		return new DefaultMaintenanceSelectionPanel(SELECTION_PANEL_NAME,"Menu Name:",pageModel, this, 
				selectionForm, MenuItemDTO.class, "menuItemName",
				"menuItemID");/*       MSK#commented old Impl,no need this logic
								 * {
								 * 
								 * private static final long serialVersionUID = -2623730454856120194L;
								 * 
								 * @Override protected IChoiceRenderer getChoiceRenderer() { return new
								 * IChoiceRenderer() { private static final long serialVersionUID =
								 * -6905252137562365820L;
								 * 
								 * public Object getDisplayValue(Object obj) { return ((MenuItemDTO)
								 * obj).getMenuItemLongDescription(); }
								 * 
								 * public String getIdValue(Object obj, int index) { return "" + ((MenuItemDTO)
								 * obj).getMenuItemID(); } }; } };
								 */
	}

	@Override
	public Button[] createNavigationalButtons() {
		return new Button[] {createSaveButton("button1"), createCancelButton("button2") };
	}

	@Override
	public Object initialisePageModel(Object object,Object extrainfo) {
		MenuItemModel obj = new MenuItemModel();
		obj.setSelectionList(getSessionBean().findAllMenuItems());
		obj.setSelectedItem((MenuItemDTO) object);
		pageModel = obj;	
		return pageModel;
	}
}
