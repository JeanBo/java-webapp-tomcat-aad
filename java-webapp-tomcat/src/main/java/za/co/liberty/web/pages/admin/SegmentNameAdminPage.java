package za.co.liberty.web.pages.admin;

import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.Panel;

import za.co.liberty.business.guicontrollers.admin.ISegmentNameGUIController;
import za.co.liberty.dto.rating.SegmentNameDTO;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.helpers.lookup.ServiceLocator;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.pages.MaintenanceBasePage;
import za.co.liberty.web.pages.admin.models.SegmentNameModel;
import za.co.liberty.web.pages.panels.DefaultMaintenanceSelectionPanel;

/**
 * Admin page for Segment Name objects.
 * 
 * @author JZB0608
 *
 */
public class SegmentNameAdminPage extends MaintenanceBasePage<Object> {

private static final long serialVersionUID = 1L;
	
	//private TeamModel pageModel;
	private SegmentNameModel pageModel;
	//private transient ITeamGUIController guiController;
	private transient ISegmentNameGUIController guiController;
	private static final Logger logger = Logger.getLogger(SegmentNameAdminPage.class);
	
	public SegmentNameAdminPage(){
		super(null);
	}
	
	public SegmentNameAdminPage(SegmentNameDTO e){
		super(e);
	}
	
	public Panel createContainerPanel() {
		
		
		logger.info("CreateContainerPanel " + pageModel.getSelectedItem());
		
		Panel panel = null;
		if (pageModel.getSelectedItem() == null) {
			panel = new EmptyPanel(CONTAINER_PANEL_NAME);
		} else {
			SegmentNameDTO dto = pageModel.getSelectedItem();
			//List<SegmentDTO> filterAll = getSessionBean().findAllSegmentsForSegmentNameList(segmentNameId);
			
			if (getEditState() != EditStateType.ADD && dto != null) {
				try {
					   //dto = getSessionBean().findTeam(dto.getOid());
						dto = getSessionBean().findSegmentNamePerID(dto.getId());
					  pageModel.setSelectedItem(dto);
					   //pageModel.setSelectedItem(dto);
					  // pageModel.setSelectionFilterList(filterAll);
					  // System.out.println("Finding:" + pageModel.getSelectionFilterList().size());
				//}catch (DataNotFoundException er){
				}catch(Exception er){
					this.error("");
					logger.error("" + er.getMessage());
					throw new RuntimeException(er);
 			    }
			}else{
//				logger.error("----------------------------------------------------- add name");
//				logger.error("DTO:" + pageModel.getSelectedItem().getSegmentName());
//				SegmentNameDTO temp = new SegmentNameDTO();
//				temp.setSegmentName(pageModel.getSelectedItem().getSegmentName());
//				pageModel.setSelectedItem(temp);
				
			}

			//panel = new TeamAdminPanel(CONTAINER_PANEL_NAME, getEditState(), pageModel,this);
			panel = new SegmentNameAdminPanel(CONTAINER_PANEL_NAME, getEditState(), pageModel,this);
		}
		panel.setOutputMarkupId(true);
		return panel;
	}
	
	@Override
	public void doSave_onSubmit() {
		//TeamDTO newDto = null;
		SegmentNameDTO segmentNameDTO = null;
		
		/** Save to db */
			SegmentNameDTO dto = pageModel.getSelectedItem();
			System.out.println("And:"+ dto.getId());
			try {
				if (dto.getId() == null ){
					System.out.println("add");
					segmentNameDTO = getSessionBean().addSegmentName(dto);
					System.out.println("llll:" + segmentNameDTO.getId());
				} else {
					System.out.println("Update");
					 getSessionBean().updateSegmentName(dto);  //updateTeam(dto,pageModel.getUacfidAsPartyOID());
				}
			} catch (Exception e){
				logger.error("Data could not be retrieved for saving team:" + e.getMessage()+ "---Cause:" + e.getCause());
				this.error(e.getMessage());
				return;
			}
		
		invalidatePage();		
		this.info("Record was saved successfully");
		//setResponsePage(new TeamAdminPage(newDto));
		setResponsePage(new SegmentNameAdminPage(segmentNameDTO));
	}
	
	@Override
	public Button[] createNavigationalButtons() {
		return new Button[] {createSaveButton("button1"), createCancelButton("button2") };

	}
	
	@Override
	public DefaultMaintenanceSelectionPanel createSelectionPanel() {
		return new DefaultMaintenanceSelectionPanel(SELECTION_PANEL_NAME,"Segment Name:",pageModel, this, 
				selectionForm, SegmentNameDTO.class,"segmentName", "id");
	}
	

	@Override
	public Object initialisePageModel(Object obj, Object pageModelExtraValueObject) {
	
		SegmentNameModel object = new SegmentNameModel();
		ISegmentNameGUIController sessionBean;
		sessionBean = getSessionBean();
		System.out.println(sessionBean.findAllSegmentNamesList().size());
		object.setSelectionList(sessionBean.findAllSegmentNamesList());
		object.setSelectedItem((SegmentNameDTO) obj);
		pageModel = object;

		return pageModel;
	}

	@Override
	public String getPageName() {
		
		return null;
	}
	
	protected ISegmentNameGUIController getSessionBean() {
		if (guiController == null) {
			try {
				guiController = ServiceLocator.lookupService(ISegmentNameGUIController.class);
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
	
}
