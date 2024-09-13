package za.co.liberty.web.pages.hierarchy;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.wicket.markup.html.panel.Panel;

import za.co.liberty.dto.gui.request.ViewRequestModelDTO;
import za.co.liberty.dto.party.HierarchyNodeDTO;
import za.co.liberty.exceptions.security.TabAccessException;
import za.co.liberty.interfaces.agreements.requests.RequestKindType;
import za.co.liberty.web.pages.contactdetail.ContactDetailsPanel;
import za.co.liberty.web.pages.hierarchy.model.MaintainHierarchyPageModel;
import za.co.liberty.web.pages.request.BaseRequestViewAndAuthorisePanel;

/**
 * Auth panel for Hierarchy node data
 * @author DZS2610
 *
 */
public class MaintainHierarchyNodeAuthorisationPanel extends BaseRequestViewAndAuthorisePanel {
	
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(MaintainHierarchyNodeAuthorisationPanel.class);
	
	/**
	 * Default constructor 
	 * 
	 * @param id
	 * @param pageModel
	 * @param editState
	 * @throws TabAccessException 
	 */
	public MaintainHierarchyNodeAuthorisationPanel(String id, 
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
		logger.debug("Valid request kinds:"+requestKindList);
			
		MaintainHierarchyPageModel model = initialiseModel(imageObject);
		
		List<Panel> panelList = new ArrayList<Panel>();
		
		// Process Party Details
		if (requestKindList.contains(RequestKindType.MaintainHierarchyNodeDetails)
				|| requestKindList.contains(RequestKindType.TerminateHierarchyNode)
				|| requestKindList.contains(RequestKindType.ReactivateHierarchyNode)) {
			Panel panel = new HierarchyNodePanel(id, model, getEditState(),null,null);
				panelList.add(panel);			
		}
		
		// Process contact details
		if (requestKindList.contains(RequestKindType.MaintainHierarchyNodeContactDetails)) {
			Panel panel = new ContactDetailsPanel(id,
					model.getHierarchyNodeDTO().getContactPreferences().getContactPreferences(),
					model.getHierarchyNodeDTO().getCommunicationPreferences(),
					getEditState(), null,false,null, false);
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
	protected MaintainHierarchyPageModel initialiseModel(Object imageObject) {
		MaintainHierarchyPageModel model = new MaintainHierarchyPageModel();
		HierarchyNodeDTO obj = (HierarchyNodeDTO) imageObject;
		logger.debug("image:  class="+obj.getClass().getName());
		model.setHierarchyNodeDTO(obj);
		model.setSelectedItem(obj);
		return model;
	}

}