package za.co.liberty.web.pages.party;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.markup.html.panel.Panel;

import za.co.liberty.dto.gui.request.ViewRequestModelDTO;
import za.co.liberty.dto.party.PartyRoleDTO;
import za.co.liberty.dto.party.PartyRolesRequestConfiguration;
import za.co.liberty.exceptions.security.TabAccessException;
import za.co.liberty.interfaces.agreements.requests.RequestKindType;
import za.co.liberty.web.pages.party.model.MaintainPartyHierarchyPanelModel;
import za.co.liberty.web.pages.party.model.MaintainPartyPageModel;
import za.co.liberty.web.pages.request.BaseRequestViewAndAuthorisePanel;

/**
 * A view panel for party hierarchy rquests
 * @author DZS2610
 *
 */
public class MaintainPartyHierarchyAuthorisationPanel extends BaseRequestViewAndAuthorisePanel {
	
	private static final long serialVersionUID = 1L;
	//private static Logger logger = Logger.getLogger(MaintainPartyHierarchyAuthorisationPanel.class);
	
	/**
	 * Default constructor 
	 * 
	 * @param id
	 * @param pageModel
	 * @param editState
	 * @throws TabAccessException 
	 */
	public MaintainPartyHierarchyAuthorisationPanel(String id, 
			ViewRequestModelDTO viewRequestPageModel) {
		super(id, viewRequestPageModel);	
	}

	/**
	 * Initialise the panels required.
	 * 
	 */
	@Override
	public List<Panel> createPanels(String id, Object imageObject) {
		List<RequestKindType> requestKindList = getPageModel().getRequestKindList();
		MaintainPartyPageModel model = initialiseModel(imageObject);		
		List<Panel> panelList = new ArrayList<Panel>();
		
		// Process Party hierarchy Details
		if (requestKindList.contains(RequestKindType.MaintainLinkedAssistants)) {
			Panel panel = new PartyHierarchyPanel(id, model, getEditState(),null,null);
			panelList.add(panel);			
		}
		// Process Party hierarchy Details
		if (requestKindList.contains(RequestKindType.MaintainPartnerships)) {
			Panel panel = new PartyHierarchyPanel(id, model, getEditState(),null,null);
			panelList.add(panel);			
		}		
		return panelList;
	}
	
	
	/**
	 * Initialise the model
	 * 
	 * @param currentImage
	 * @return
	 */
	protected MaintainPartyPageModel initialiseModel(Object imageObject) {
		MaintainPartyPageModel model = new MaintainPartyPageModel();
		if(imageObject instanceof PartyRolesRequestConfiguration){
			PartyRolesRequestConfiguration obj = (PartyRolesRequestConfiguration) imageObject;		
			MaintainPartyHierarchyPanelModel panelModel = new MaintainPartyHierarchyPanelModel();
			ArrayList<PartyRoleDTO> roles = new ArrayList<PartyRoleDTO>();
			roles.addAll(obj.getIsAssistedByPartyRoles());
			roles.addAll(obj.getOtherPartyToPartyRoles());
			panelModel.setPartyToPartyRoles(roles);
			model.setMaintainPartyHierarchyPanelModel(panelModel);
		}
		return model;
	}

}