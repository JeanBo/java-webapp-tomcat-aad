package za.co.liberty.web.pages.party;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.wicket.markup.html.panel.Panel;

import za.co.liberty.dto.advancedPractice.AdvancedPracticeDTO;
import za.co.liberty.dto.advancedPractice.AdvancedPracticeManagerDTO;
import za.co.liberty.dto.advancedPractice.AdvancedPracticeMemberDTO;
import za.co.liberty.dto.agreement.maintainagreement.AdvancedPracticeRequestConfigurationDTO;
import za.co.liberty.dto.gui.request.ViewRequestModelDTO;
import za.co.liberty.exceptions.security.TabAccessException;
import za.co.liberty.interfaces.agreements.requests.RequestKindType;
import za.co.liberty.web.pages.advancedPractice.AdvancedPracticePanel;
import za.co.liberty.web.pages.advancedPractice.model.AdvancedPracticePanelModel;
import za.co.liberty.web.pages.request.BaseRequestViewAndAuthorisePanel;


public class MaintainAdvancedPracticeAuthorisationPanel  extends BaseRequestViewAndAuthorisePanel{

	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(MaintainAdvancedPracticeAuthorisationPanel.class);
	
	/**
	 * Default constructor 
	 * 
	 * @param id
	 * @param pageModel
	 * @param editState
	 * @throws TabAccessException 
	 */
	public MaintainAdvancedPracticeAuthorisationPanel(String id, 
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
		AdvancedPracticePanelModel model = initialiseModel(imageObject);		
		List<Panel> panelList = new ArrayList<Panel>();
		

		
		
		
		// Process Practice Details
		if (requestKindList.contains(RequestKindType.MaintainAdvancedPractice)) {
			Panel panel = new AdvancedPracticePanel(id, model, getEditState(),null);
//			Panel panel = new AdvancedPracticePanel(model, getEditState(),null,null);
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

	
	protected AdvancedPracticePanelModel initialiseModel(Object imageObject) {
		AdvancedPracticePanelModel model = new AdvancedPracticePanelModel();
		
		if( imageObject instanceof AdvancedPracticeDTO){
			logger.debug("image:  class="+imageObject.getClass().getName());
			model.setAdvancedPracticeDTO((AdvancedPracticeDTO) imageObject);
			//model.setSelectedItem((AdvancedPracticeDTO) imageObject);
			return model;
		}
		AdvancedPracticeRequestConfigurationDTO obj = (AdvancedPracticeRequestConfigurationDTO) imageObject;
		
		if(((AdvancedPracticeRequestConfigurationDTO) imageObject).getAdvancedPracticeDTO().getAdvancedPracticeMemberDTOList() == null){
			((AdvancedPracticeRequestConfigurationDTO) imageObject).getAdvancedPracticeDTO()
			.setAdvancedPracticeMemberDTOList(new ArrayList<AdvancedPracticeMemberDTO>());
		}
		if(((AdvancedPracticeRequestConfigurationDTO) imageObject).getAdvancedPracticeDTO().getAdvancedPracticeManagerDTOlist() == null){
			((AdvancedPracticeRequestConfigurationDTO) imageObject).getAdvancedPracticeDTO()
			.setAdvancedPracticeManagerDTOlist(new ArrayList<AdvancedPracticeManagerDTO>());
		}
		
		logger.debug("image:  class="+obj.getClass().getName());
		model.setAdvancedPracticeDTO(obj.getAdvancedPracticeDTO());
		return model;
	}

}
