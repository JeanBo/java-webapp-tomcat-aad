package za.co.liberty.web.pages.admin;

import org.apache.log4j.Logger;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.Panel;

import za.co.liberty.business.guicontrollers.admin.IDifferentialFactorGuiController;
import za.co.liberty.dto.rating.DifferentialPricingFactorDTO;
import za.co.liberty.web.pages.MaintenanceBasePage;
import za.co.liberty.web.pages.admin.models.DifferentialFactorModel;

/**
 * Admin 
 * 
 * @author jzb0608
 *
 */
public class BroadcastMessagesAdminPage extends MaintenanceBasePage<Object>   {
	
	private static final long serialVersionUID = 1L;
	
	private DifferentialFactorModel pageModel;
	private transient IDifferentialFactorGuiController guiController;
	private static final Logger logger = Logger.getLogger(BroadcastMessagesAdminPage.class);
	
	public BroadcastMessagesAdminPage() {
		super(null);
	}
	
	public BroadcastMessagesAdminPage(DifferentialPricingFactorDTO dto){
		super(dto);
	}
	
	@Override
	public Panel createContainerPanel() {
		Panel panel = null;
		if (pageModel.getSelectedItem() == null) {
			panel = new EmptyPanel(CONTAINER_PANEL_NAME);
		} else {
			panel = new DifferentialFactorPanel(CONTAINER_PANEL_NAME, getEditState(), pageModel,this);
		}
		panel.setOutputMarkupId(true);
		return panel;
	}
	@Override
	public Button[] createNavigationalButtons() {
		return new Button[] {};
	}
	
	@Override
	public Panel createSelectionPanel() {
		return new EmptyPanel(SELECTION_PANEL_NAME);	
	}
	
	@Override
	public void doSave_onSubmit() {
	}

	@Override
	public Object initialisePageModel(Object obj, Object pageModelExtraValueObject) {
		return null;
	}
	
	@Override
	public String getPageName() {
		return "Broadcast Messages Admin";
	}
	
//	@Override
//	public ContextType getContextTypeRequired() {
//		return ContextType.;
//	}

}
