package za.co.liberty.web.pages.core;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.apache.wicket.markup.html.panel.Panel;

import za.co.liberty.business.guicontrollers.core.ICoreTransferGuiController;
import za.co.liberty.dto.agreement.core.CoreTransferDto;
import za.co.liberty.dto.gui.request.ViewRequestModelDTO;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.exceptions.security.TabAccessException;
import za.co.liberty.helpers.lookup.ServiceLocator;
import za.co.liberty.interfaces.agreements.requests.RequestKindType;
import za.co.liberty.web.pages.core.model.CoreTransferPageModel;
import za.co.liberty.web.pages.request.BaseRequestViewAndAuthorisePanel;

public class CoreTransferAuthorisationPanel extends BaseRequestViewAndAuthorisePanel {
	
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(CoreTransferAuthorisationPanel.class);
	protected transient ICoreTransferGuiController guiController;
	
	
	/**
	 * Default constructor 
	 * 
	 * @param id
	 * @param pageModel
	 * @param editState
	 * @throws TabAccessException 
	 */
	public CoreTransferAuthorisationPanel(String id, 
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
			
		CoreTransferPageModel model = initialiseModel(imageObject);
		
		List<Panel> panelList = new ArrayList<Panel>();
		

		if (requestKindList.contains(RequestKindType.ProcessBookLevelTransfer)){
			Panel panel = new BookTransferPanel(id, model, getEditState(),null);
			panelList.add(panel);
		}
		if (requestKindList.contains(RequestKindType.ProcessContractTransfer)){
			Panel panel = new ContractTransferPanel(id, model, getEditState(),null,null);
			panelList.add(panel);	
		}
		if (requestKindList.contains(RequestKindType.ProcessSegmentedContractTransfer)) {
			Panel panel = new SegmentedTransferPanel(id, model, getEditState(),null,null);
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
	protected CoreTransferPageModel initialiseModel(Object imageObject) {
		CoreTransferPageModel model = new CoreTransferPageModel();
		guiController = getGUIController();
		model.setRequestCategoryDTO(guiController.getAllRequestStatusTypeList());
		
		List<CoreTransferDto> coreTransferDtoList = new ArrayList<CoreTransferDto>();
		coreTransferDtoList.add(0, (CoreTransferDto)imageObject);
		
		model.setCoreTransferDto(coreTransferDtoList);
		return model;
	}
	

	protected ICoreTransferGuiController getGUIController() {
		if (guiController == null) {
			try {
				guiController = ServiceLocator
						.lookupService(ICoreTransferGuiController.class);
			} catch (NamingException e) {
				throw new CommunicationException(
						"Naming exception looking up Agreement GUI Controller",
						e);
			}
		}
		return guiController;
	}
	

}
