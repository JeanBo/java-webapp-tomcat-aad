package za.co.liberty.web.pages.franchisetemplates;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.markup.html.panel.Panel;

import za.co.liberty.dto.gui.request.ViewRequestModelDTO;
import za.co.liberty.dto.gui.templates.MaintainFranchiseTemplateDTO;
import za.co.liberty.dto.gui.templates.MaintainFranchiseTemplateRequestConfiguration;
import za.co.liberty.dto.party.PartyRoleDTO;
import za.co.liberty.dto.party.PartyRolesRequestConfiguration;
import za.co.liberty.exceptions.security.TabAccessException;
import za.co.liberty.interfaces.agreements.requests.RequestKindType;
import za.co.liberty.web.pages.franchisetemplates.model.FranchiseTemplatePanelModel;
import za.co.liberty.web.pages.franchisetemplates.model.MaintainFranchiseTemplatePageModel;
import za.co.liberty.web.pages.party.model.MaintainPartyHierarchyPanelModel;
import za.co.liberty.web.pages.party.model.MaintainPartyPageModel;
import za.co.liberty.web.pages.request.BaseRequestViewAndAuthorisePanel;

/**
 * A view panel for party hierarchy rquests
 * @author MZL2611
 *
 */
public class MaintainFranchiseTemplateAuthorisationPanel extends BaseRequestViewAndAuthorisePanel {
	
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
	public MaintainFranchiseTemplateAuthorisationPanel(String id, 
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
		MaintainFranchiseTemplatePageModel model = initialiseModel(imageObject);		
		List<Panel> panelList = new ArrayList<Panel>();
		
		// Process Franchise Template Details
		if (requestKindList.contains(RequestKindType.MaintainFranchiseTemplateDetails)) {
			FranchiseTemplatePanelModel franchiseTemplatePanelModel = new FranchiseTemplatePanelModel(model);
			Panel panel = new FranchiseTemplatePanel(id, franchiseTemplatePanelModel, getEditState(),null,null);
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
	protected MaintainFranchiseTemplatePageModel initialiseModel(Object imageObject) {
		MaintainFranchiseTemplatePageModel model = new MaintainFranchiseTemplatePageModel();
		if(imageObject instanceof MaintainFranchiseTemplateDTO){
			MaintainFranchiseTemplateDTO maintainFranchiseTemplateDTO = (MaintainFranchiseTemplateDTO) imageObject;
			model.setMaintainFranchiseTemplateDTO(maintainFranchiseTemplateDTO);	
			model.setSelectedItem(maintainFranchiseTemplateDTO);
		}
		return model;
	}

}