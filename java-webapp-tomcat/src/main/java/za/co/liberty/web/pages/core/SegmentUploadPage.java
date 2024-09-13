package za.co.liberty.web.pages.core;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.NamingException;

import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;

import za.co.liberty.business.guicontrollers.core.ICoreTransferGuiController;
import za.co.liberty.dto.agreement.core.CoreTransferDto;
import za.co.liberty.dto.party.contactdetail.AddressDTO;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.helpers.lookup.ServiceLocator;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.data.pages.IModalMaintenancePageModel;
import za.co.liberty.web.pages.BaseWindowPage;
import za.co.liberty.web.pages.core.model.CoreTransferPageModel;
import za.co.liberty.web.pages.core.model.CoreUploadModel;
import za.co.liberty.web.wicket.modal.SRSModalWindow;

/**
 * Upload page that pop-ups to allow CSV files to load CORE transfer rows
 * 
 *
 */
public class SegmentUploadPage extends BaseWindowPage {

	private static final long serialVersionUID = 1L;

	private SegmentUploadPanel licensePanel;

	private transient ICoreTransferGuiController guiController;

	private transient Logger logger = Logger.getLogger(SegmentUploadPage.class
			.getName());
	
	private SRSModalWindow window;
	
	//private IModalMaintenancePageModel<List<CoreTransferDto>> wizardModel;
	private IModalMaintenancePageModel<CoreUploadModel> wizardModel;
	

	public SegmentUploadPage(EditStateType editStateType, SRSModalWindow window,
			CoreTransferPageModel pageModel) {
		super();
		this.window = window;
		
		wizardModel = new IModalMaintenancePageModel<CoreUploadModel>() {

			private static final long serialVersionUID = 1L;
			private CoreUploadModel selectedItem;
			private boolean success = false;
			
			@Override
			public void setSelectedItem(CoreUploadModel selected) {
				this.selectedItem = selected;
			}

			@Override
			public CoreUploadModel getSelectedItem() {
				return selectedItem;
			}

			@Override
			public boolean isModalWizardSucces() {
				return success;
			}

			@Override
			public void setModalWizardSuccess(boolean success) {
				this.success = success;
			}

			@Override
			public String getModalWizardMessage() {
				return "";
			}
		};
		// Write page model to session
		window.setSessionModelForPage(pageModel);
		
		if (licensePanel == null)
			licensePanel = new SegmentUploadPanel("segmentTransferUpload",
					editStateType, pageModel, window, wizardModel);
		add(licensePanel);
	}

	@Override
	public String getPageName() {
		return "Segmented Transfer Upload Page";
	}

	private ICoreTransferGuiController getGUIController() {
		if (guiController == null) {
			try {
				guiController = ServiceLocator
						.lookupService(ICoreTransferGuiController.class);
			} catch (NamingException e) {
				logger.log(Level.SEVERE,
						"Naming exception looking up Agreement GUI Controller",
						e);
				throw new CommunicationException(
						"Naming exception looking up Agreement GUI Controller",
						e);
			}
		}
		return guiController;
	}
}
