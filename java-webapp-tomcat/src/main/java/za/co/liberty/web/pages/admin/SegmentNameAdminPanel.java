package za.co.liberty.web.pages.admin;

import org.apache.wicket.Page;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.PropertyModel;

import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.pages.MaintenanceBasePage;
import za.co.liberty.web.pages.admin.models.SegmentNameModel;
import za.co.liberty.web.pages.panels.BasePanel;
import za.co.liberty.web.wicket.markup.html.form.SRSTextField;



public class SegmentNameAdminPanel extends BasePanel {
	
	private SegmentNameModel pageModel = null;
	private TextField segmentName = null;
	
	public SegmentNameAdminPanel(String id, EditStateType editState, SegmentNameModel page, MaintenanceBasePage parentPage) {
		super(id, editState, parentPage);
		System.out.println("Selected combo segment name:" + page.getSelectedItem().getId());
		pageModel = page;
		initiate();
	}

	public void initiate(){
		add(segmentName = createSegmentName("segmentName"));
		
	}
	
	private TextField createSegmentName(String name){
		SRSTextField tempSRSTextField = new SRSTextField(name,new PropertyModel(pageModel.getSelectedItem(),"segmentName" ));
		tempSRSTextField.setEnabled(!getEditState().isViewOnly());
		return tempSRSTextField;
	}

}
