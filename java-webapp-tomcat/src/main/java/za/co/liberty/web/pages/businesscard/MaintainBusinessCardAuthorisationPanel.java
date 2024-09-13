package za.co.liberty.web.pages.businesscard;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.markup.html.panel.Panel;

import za.co.liberty.database.enums.DatabaseEnumHelper;
import za.co.liberty.dto.businesscard.BusinessCardDetailsDTO;
import za.co.liberty.dto.databaseenum.LanguagePreferenceDBEnumDTO;
import za.co.liberty.dto.gui.request.ViewRequestModelDTO;
import za.co.liberty.exceptions.security.TabAccessException;
import za.co.liberty.interfaces.agreements.requests.RequestKindType;
import za.co.liberty.web.pages.businesscard.model.BusinessCardPageModel;
import za.co.liberty.web.pages.businesscard.model.MaintainBusinessCardPanelModel;
import za.co.liberty.web.pages.request.BaseRequestViewAndAuthorisePanel;

/**
 * A view panel for business card rquests
 * @author DZS2610
 *
 */
public class MaintainBusinessCardAuthorisationPanel extends BaseRequestViewAndAuthorisePanel {
	
	private static final long serialVersionUID = 1L;
	//private static Logger logger = Logger.getLogger(MaintainBusinessCardAuthorisationPanel.class);
	
	/**
	 * Default constructor 
	 * 
	 * @param id
	 * @param pageModel
	 * @param editState
	 * @throws TabAccessException 
	 */
	public MaintainBusinessCardAuthorisationPanel(String id, 
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
		List<Panel> panelList = new ArrayList<Panel>();
		
		// Process Party hierarchy Details
		if (requestKindList.contains(RequestKindType.MaintainBusinessCardDetails)) {
			BusinessCardPageModel model = initialiseBusinessCardPageModel(imageObject);	
			Panel panel = new BusinessCardDetailsPanel(id, model, getEditState(),null,null);
			panelList.add(panel);	
			
			// Find an FA panel, only shown if editing was allowed when maintained
			if (model.getMaintainBusinessCardPanelModel()!=null 
					&& model.getMaintainBusinessCardPanelModel().getBusinessCardDetails()!= null) {
				BusinessCardDetailsDTO dto =  model.getMaintainBusinessCardPanelModel().getBusinessCardDetails();
				if (dto.isContactableAdvisor() 
						|| (dto.getSelectedPostalLocations()!=null && dto.getSelectedPostalLocations().size()>0)
						|| (dto.getSpokenLanguages()!=null && dto.getSpokenLanguages().size()>0)
						|| hasCurrentPanel(BusinessCardFindAnFADetailsPanel.class)) {
					// We show this panel only 
					panel = new BusinessCardFindAnFADetailsPanel(id, model, getEditState(),null,null);
					panelList.add(panel);
				}
			}
		}
		
		return panelList;
	}
	
	
	/**
	 * Initialise the model for businesscard
	 * 
	 * @param currentImage
	 * @return
	 */
	protected BusinessCardPageModel initialiseBusinessCardPageModel(Object imageObject) {
		BusinessCardPageModel model = new BusinessCardPageModel();
		if(imageObject instanceof BusinessCardDetailsDTO){
			BusinessCardDetailsDTO bc = (BusinessCardDetailsDTO)imageObject;
			MaintainBusinessCardPanelModel panelModel = new MaintainBusinessCardPanelModel();
			panelModel.setBusinessCardDetails(bc);
			model.setMaintainBusinessCardPanelModel(panelModel);	
			panelModel.setAllSpokenLanguages(DatabaseEnumHelper.getDatabaseDTO(LanguagePreferenceDBEnumDTO.class,
					true,true, false, false));
		}
		return model;
	}

}