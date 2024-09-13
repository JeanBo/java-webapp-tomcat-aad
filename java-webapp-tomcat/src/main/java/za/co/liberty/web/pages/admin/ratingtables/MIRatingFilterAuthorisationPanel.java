package za.co.liberty.web.pages.admin.ratingtables;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.markup.html.panel.Panel;

import za.co.liberty.dto.gui.request.ViewRequestModelDTO;
import za.co.liberty.dto.rating.MIRatingTableNameDTO;
import za.co.liberty.interfaces.agreements.requests.RequestKindType;
import za.co.liberty.interfaces.rating.IGuiRatingRow;
import za.co.liberty.web.pages.admin.models.RatingTablePageModel;
import za.co.liberty.web.pages.request.BaseRequestViewAndAuthorisePanel;

public class MIRatingFilterAuthorisationPanel extends BaseRequestViewAndAuthorisePanel {
	
	private static final long serialVersionUID = 1L;

	public MIRatingFilterAuthorisationPanel(String id, 
			ViewRequestModelDTO viewRequestPageModel) {
		super(id, viewRequestPageModel);	
	}

	@Override
	public List<Panel> createPanels(String id, Object imageObject) {
		List<RequestKindType> requestKindList = getPageModel().getRequestKindList();
		List<Panel> panelList = new ArrayList<Panel>();
		
		// Process MI table Details
		if (requestKindList.contains(RequestKindType.MaintainMITableData)) {
			RatingTablePageModel model = initialiseRatingTablePageModel(imageObject);	
			Panel panel = new MIRatingFilterPanel(id,getEditState(),model,new MIRatingGUIPage());
			panelList.add(panel);			
		}		
		return panelList;
	}
		
	protected RatingTablePageModel initialiseRatingTablePageModel(Object imageObject) {
		RatingTablePageModel model = new RatingTablePageModel();
		if(imageObject instanceof IGuiRatingRow){
			MIRatingTableNameDTO tableNameDTO = new MIRatingTableNameDTO(((IGuiRatingRow)imageObject).getTableType().getId(),((IGuiRatingRow)imageObject).getTableType().getTableName());
			model.setSelectedItem(tableNameDTO); 
			model.setSelectionRow((IGuiRatingRow)imageObject);	
		}
		return model;
	}

}
