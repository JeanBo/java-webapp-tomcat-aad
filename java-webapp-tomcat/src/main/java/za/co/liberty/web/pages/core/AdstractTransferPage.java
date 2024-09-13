package za.co.liberty.web.pages.core;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.NamingException;

import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;

import za.co.liberty.business.guicontrollers.core.ICoreTransferGuiController;
import za.co.liberty.dto.agreement.core.CoreTransferDto;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.helpers.lookup.ServiceLocator;
import za.co.liberty.web.data.enums.ContextType;
import za.co.liberty.web.pages.MaintenanceBasePage;
import za.co.liberty.web.pages.core.model.CoreTransferPageModel;

public abstract class AdstractTransferPage extends MaintenanceBasePage<Integer> {

	private static final long serialVersionUID = 1L;

	protected CoreTransferPageModel pageModel;

	protected Form pageForm;

	protected Panel panel;

	protected Panel buttonPanel;

	protected transient ICoreTransferGuiController guiController;

	protected transient Logger logger = Logger
			.getLogger(AdstractTransferPage.class.getName());

	public AdstractTransferPage() {
		this(null);
	}

	public AdstractTransferPage(Object obj) {
		super(obj);
	}

/*	public AdstractTransferPage(Object obj,CoreTransferPageModel pageModel) {
		super(obj);
		this.pageModel=pageModel;
	}*/
	
	@Override
	public Button[] createNavigationalButtons() {
		return null;
	}

	public Panel getButtonPanel() {
		return buttonPanel;
	}

	public void setButtonPanel(Panel buttonPanel) {
		this.buttonPanel = buttonPanel;
	}

	@Override
	public Panel createSelectionPanel() {
		buttonPanel = new CoreButtonsPanel<CoreTransferDto>(
				SELECTION_PANEL_NAME, pageModel, this, containerForm,
				CoreTransferDto.class, this.getFeedbackPanel(), false, true,
				true, false, false, false) {

			private static final long serialVersionUID = 1L;
			
			@Override
			public void resetSelection() {
			}

			@Override
			protected boolean isBroadcastButtonEnabled() {
				return false;
			}

			@Override
			protected boolean isModifyButtonEnabled() {
				return false;
			}

			@Override
			protected boolean isTerminateButtonenabled() {
				return false;
			}
		};
		return buttonPanel;

	}

	@Override
	public Object initialisePageModel(Object obj, Integer currentTab) {
		CoreTransferPageModel pageModel = new CoreTransferPageModel();
		guiController = getGUIController();
		pageModel.setRequestCategoryDTO(guiController
				.getAllRequestStatusTypeList());
		this.pageModel = pageModel;
		return pageModel;
	}

	@Override
	public ContextType getContextTypeRequired() {
		return ContextType.NONE;
	}

	protected ICoreTransferGuiController getGUIController() {
		if (guiController == null) {
			try {
				guiController = ServiceLocator
						.lookupService(ICoreTransferGuiController.class);
			} catch (NamingException e) {
				logger.log(Level.SEVERE,
						"Naming exception looking up CoreTransferGUIController",
						e);
				throw new CommunicationException(
						"Naming exception looking up CoreTransferGUIController",
						e);
			}
		}
		return guiController;
	}

}
