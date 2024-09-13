package za.co.liberty.web.pages.admin;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.Panel;

import za.co.liberty.business.guicontrollers.userprofiles.IRequestCategoryGUIController;
import za.co.liberty.dto.userprofiles.RequestCategoryDTO;
import za.co.liberty.exceptions.data.DataNotFoundException;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.helpers.lookup.ServiceLocator;
import za.co.liberty.interfaces.agreements.requests.RequestKindType;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.pages.MaintenanceBasePage;
import za.co.liberty.web.pages.admin.models.RequestCategoryModel;
import za.co.liberty.web.pages.panels.DefaultMaintenanceSelectionPanel;
import za.co.liberty.web.system.SRSAuthWebSession;

public class RequestCategoryAdminPage extends MaintenanceBasePage<Object>
		implements Serializable {

	private static final long serialVersionUID = 1L;
	private RequestCategoryModel pageModel;
	public static final String SELECTION_PANEL_NAME = "selectionPanel";
	public static final String CONTAINER_PANEL_NAME = "containerPanel";
	private static final Logger logger = Logger
			.getLogger(RequestCategoryAdminPage.class);
	private transient IRequestCategoryGUIController guiController;

	/*
	 * Default constructor
	 */
	public RequestCategoryAdminPage() {
		super(null);
	}

	public RequestCategoryAdminPage(RequestCategoryDTO reqCatDTO) {
		super(reqCatDTO);
	}

	public RequestCategoryModel getPageModel() {		
		return this.pageModel;
	}
	/*
	 * Initialise the page model
	 * 
	 */
	public Object initialisePageModel(Object object, Object extrainfo) {
		
		RequestCategoryModel rcm = new RequestCategoryModel();
		try {
			
			rcm.setSelectedList(getGuiController().findAllRequestCategories());
			rcm.setSelectedItem((RequestCategoryDTO) object);
			String loggedInUserName = SRSAuthWebSession.get().getCurrentUserid();
			rcm.setLogginUserOID(getGuiController().findLoggedInUserOID(loggedInUserName));
			rcm.setOriginalRequestKindsList(new ArrayList(Arrays.asList(RequestKindType.values())));
		}catch (DataNotFoundException e) {;
			logger.error("Current logged in party can not be located:" + e.getMessage());
			this.error("Current logged in party can not be located:" + e.getMessage());
		}
		pageModel = rcm;
		
		return pageModel;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see za.co.liberty.web.pages.MaintenanceBasePage#createSelectionPanel()
	 *      @return Initiate framework's selection panel, drop down combo box
	 *      with listing all request categories. Modify and Add buttons created.
	 *      With selection of request category it will populate page model
	 *      selected item.
	 */
	@Override
	public DefaultMaintenanceSelectionPanel createSelectionPanel() {
		return new DefaultMaintenanceSelectionPanel(SELECTION_PANEL_NAME,
				"Request Category:", pageModel, this, selectionForm,
				RequestCategoryDTO.class, "name", "id");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see za.co.liberty.web.pages.MaintenanceBasePage#createContainerPanel()
	 */
	public Panel createContainerPanel() {
		  Panel panel;
		  panel = new EmptyPanel(CONTAINER_PANEL_NAME);
		  if (pageModel.getSelectedItem() == null) {
			panel = new EmptyPanel(CONTAINER_PANEL_NAME);
		  } else {
			  RequestCategoryDTO dto = pageModel.getSelectedItem();
			  if (getEditState() != EditStateType.ADD) {
				  try {
					  if (pageModel.getSelectedItem() == null) {
						panel = new EmptyPanel(CONTAINER_PANEL_NAME);
					  } else {
						dto = getGuiController().findRequestCategory(dto.getId());
						pageModel.setSelectedItem(dto);
						List<RequestKindType> comboList = getGuiController().updateModel(pageModel.getOriginalRequestKindsList(),pageModel.getSelectedItem().getSelectedRequestKindsList());
					    pageModel.setAvailableRequestCategoryKindsList(comboList);
					}
				  } catch (DataNotFoundException er) {
					this.error("ID of team can't be found by id for panel creation on page:"+ dto.getId());
					logger.error(er.getMessage());
				  }
			  }else{
					    dto = getGuiController().newRequestCategory();
					    pageModel.setSelectedItem(dto);
					    pageModel.setAvailableRequestCategoryKindsList(pageModel.getOriginalRequestKindsList());
					    List<RequestKindType> comboList = getGuiController().updateModel(pageModel.getOriginalRequestKindsList(),pageModel.getSelectedItem().getSelectedRequestKindsList());
					    pageModel.setAvailableRequestCategoryKindsList(comboList);
			  }
			panel = new RequestCategoryAdminPanel(CONTAINER_PANEL_NAME,getEditState(), pageModel);
		  }
		panel.setOutputMarkupId(true);

		return panel;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see za.co.liberty.web.pages.MaintenanceBasePage#createNavigationalButtons()
	 */
	@Override
	public Button[] createNavigationalButtons() {
		return new Button[] { createSaveButton("button1"),
				createCancelButton("button2") };
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see za.co.liberty.web.pages.BasePage#getPageName()
	 */
	@Override
	public String getPageName() {
		return "Request Administration";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see za.co.liberty.web.pages.BasePage#getContextPanel()
	 */
	@Override
	protected Panel getContextPanel() {
		/* Does not require a panel */
		return new EmptyPanel(CONTEXT_PANEL_NAME);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see za.co.liberty.web.pages.MaintenanceBasePage#doSave_onSubmit() Save
	 *      the request category to db. Either persist or merge
	 */
	@Override
	public void doSave_onSubmit() {
		RequestCategoryDTO newDto = null;
		/* Save to db */
		try {
			// Retrieve the pageModel DTO
			RequestCategoryDTO dto = pageModel.getSelectedItem();
			
			if (dto.getId() == null) {
				if (pageModel.getSelectedItem().getSelectedRequestKindsList().size() > 0) {
					getGuiController().addRequestCategory(pageModel.getSelectedItem(),pageModel.getLogginUserOID());
				} else {
					this.error("It is advised to link request category kinds to this category.");
					return;
				}
			} else{
				getGuiController().updateRequestCategory(pageModel.getSelectedItem(),pageModel.getLogginUserOID());
			}
		}catch(DataNotFoundException e){
			this.error(e.getMessage());
			logger.error("An error occurred storing the request category" + e.getMessage());
			return;
		}
		invalidatePage();
		// reset the page with the updated dto information
		//this.info("Record was saved successfully");
		this.getSession().info("Record was saved successfully");
		setResponsePage(new RequestCategoryAdminPage(newDto));
	}
	
	protected IRequestCategoryGUIController getGuiController() {
		if (guiController == null) {
			try {
				guiController = ServiceLocator.lookupService(IRequestCategoryGUIController.class);
				
			} catch (NamingException namingErr) {
				logger.error(this.getPageName()
						+ " IRequestCategoryGUIController can not be looked up:"
						+ namingErr);
				CommunicationException comm = new CommunicationException(" IRequestCategoryGUIController can not be looked up",namingErr);
				throw comm;
			}
		}
		return guiController;
	}
}