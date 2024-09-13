package za.co.liberty.web.pages.admin;

import java.util.List;

import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.Panel;

import za.co.liberty.business.guicontrollers.userprofiles.ITeamGUIController;
import za.co.liberty.dto.userprofiles.RequestCategoryDTO;
import za.co.liberty.dto.userprofiles.TeamDTO;
import za.co.liberty.exceptions.UnResolvableException;
import za.co.liberty.exceptions.data.DataNotFoundException;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.helpers.lookup.ServiceLocator;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.models.PagePanelInfoObject;
import za.co.liberty.web.pages.MaintenanceBasePage;
import za.co.liberty.web.pages.admin.models.TeamModel;
import za.co.liberty.web.pages.panels.DefaultMaintenanceSelectionPanel;
import za.co.liberty.web.system.SRSAuthWebSession;
import za.co.liberty.web.wicket.markup.html.form.SRSAbstractChoiceRenderer;

/**
 * This is the page for Team Admin which will initiate the framework for using the team admin screen
 * @author JWV2310
 *
 */
public class TeamAdminPage extends MaintenanceBasePage<Object> {
	
	
	private TeamModel pageModel;
	private static final long serialVersionUID = 9067518105519936363L;
	private static final Logger logger = Logger.getLogger(TeamAdminPage.class);
	private transient ITeamGUIController guiController;
	
	/**
	 * Default constructer
	 */
	public TeamAdminPage(){
		super(null);
	}
	
	/**
	 * Constructer with TeamDTO input
	 */
	public TeamAdminPage(TeamDTO dto){
		super(dto);
	}
	
	/**
	 * Default constructer with Request Category DTO input
	 */
	public TeamAdminPage(RequestCategoryDTO dto){
		super(dto);
	}
	
	/**
	 * Initialise the team page model 
	 */
	public Object initialisePageModel(Object object,Object extrainfo) {

		TeamModel obj = new TeamModel();
		ITeamGUIController sessionBean;
		sessionBean = getSessionBean();
		obj.setSelectionList(sessionBean.findAllTeams());
		obj.setSelectedItem((TeamDTO) object);
		obj.setAllRequestCategories(sessionBean.findAllRequestCategories());
		obj.setUacfidAsPartyOID(""+SRSAuthWebSession.get().getSessionUser().getPartyOid());
		pageModel = obj;

		return pageModel;
	}
	
	public Panel createContainerPanel() {
	   
		Panel panel;
		if (pageModel.getSelectedItem() == null) {
			panel = new EmptyPanel(CONTAINER_PANEL_NAME);
		} else {
			TeamDTO dto = pageModel.getSelectedItem();
			if (getEditState() != EditStateType.ADD && dto != null) {
				try {
					   dto = getSessionBean().findTeam(dto.getOid());
					   pageModel.setSelectedItem(dto);

				}catch (DataNotFoundException er){
					this.error("ID of team can't be found by id for panel creation on page:");
					logger.error("Data not found for the team!" + er.getMessage());
					throw new RuntimeException(er);
 			    }
			}

			panel = new TeamAdminPanel(CONTAINER_PANEL_NAME, getEditState(), pageModel,this);
		}
		panel.setOutputMarkupId(true);
		return panel;
	}
	

	@Override
	public DefaultMaintenanceSelectionPanel createSelectionPanel() {
		return new DefaultMaintenanceSelectionPanel(SELECTION_PANEL_NAME,"Team Name:",pageModel, this, 
				selectionForm, TeamDTO.class) {
					private static final long serialVersionUID = -2623730454856120194L;

					@Override
					protected IChoiceRenderer getChoiceRenderer() {
						return new SRSAbstractChoiceRenderer() {
							private static final long serialVersionUID = -6905252137562365820L;

							public Object getDisplayValue(Object obj) {
								return ((TeamDTO) obj).getTeamName();
							}
							public String getIdValue(Object obj, int index) {
								return "" + ((TeamDTO) obj).getOid();
							}
						};
					}
		};
	}
	
	@Override
	public void doSave_onSubmit() {
		TeamDTO newDto = null;
		
		/** Save to db */
			TeamDTO dto = pageModel.getSelectedItem();
			try {
				if (dto.getOid() == null) {
					if(pageModel.getSelectedItem().getSelectedTeamPartiesList().size() > 0) {
						newDto = getSessionBean().addTeam(pageModel.getSelectedItem(),pageModel.getUacfidAsPartyOID());
					}else{
						this.error("Please select one or more party members to link to team");
						return;
					}
				} else {
					newDto = getSessionBean().updateTeam(dto,pageModel.getUacfidAsPartyOID());
				}
			} catch (DataNotFoundException e){
				logger.error("Data could not be retrieved for saving team:" + e.getMessage()+ "---Cause:" + e.getCause());
				this.error(e.getMessage());
				return;
			}
		
		invalidatePage();		
		this.info("Record was saved successfully");
		setResponsePage(new TeamAdminPage(newDto));
	}
	
	@Override
	public Button[] createNavigationalButtons() {
		return new Button[] {createSaveButton("button1"), createCancelButton("button2") };
	}
	
	@Override
	public String getPageName() {
		return "Team Administration";
	}
	
	@Override
	protected Panel getContextPanel() {
		/* Does not require a panel */
		return new EmptyPanel(CONTEXT_PANEL_NAME);
	}
	
	/**
	 * Retrieve the guicontroller interface team management
	 */
	protected ITeamGUIController getSessionBean() {
		if (guiController == null) {
			try {
				guiController = ServiceLocator.lookupService(ITeamGUIController.class);
			} catch (NamingException namingErr) {
				logger.error(this.getPageName()
						+ " ITeamGUIController can not be lookedup:"
						+ namingErr.getMessage());
				CommunicationException comm = new CommunicationException("ITeamGuiController can not be looked up!");
				throw new CommunicationException(comm);
			} 
		}
		return guiController;
	}

	@Override
	public List<PagePanelInfoObject> getPagePanelsInfo() {	
		return null;
	}

}