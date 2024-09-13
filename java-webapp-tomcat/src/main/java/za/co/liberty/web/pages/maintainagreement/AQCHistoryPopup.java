package za.co.liberty.web.pages.maintainagreement;

import java.util.List;

import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow.MaskType;

import za.co.liberty.dto.persistence.party.flow.PartyAQCHistoryFLO;
import za.co.liberty.web.pages.maintainagreement.model.AdvisorQualityCodePanelModel;

public class AQCHistoryPopup {
	
	public static ModalWindow createModalWindow(final List<PartyAQCHistoryFLO> aqcHistoryList ,final String id,
			final AdvisorQualityCodePanelModel panelModel) {
		final ModalWindow window = new ModalWindow(id);
		
		if(id.equals("calcAQCHistory"))
			window.setTitle("Calculated AQC History");
		else 
			window.setTitle("Manual AQC History");
		
		// Create the page
		window.setPageCreator(new ModalWindow.PageCreator() {
			private static final long serialVersionUID = 1L;
			public Page createPage() {
				return new AQCHistoryPage(window,aqcHistoryList,panelModel.getTitle());
			}
		});
		
		// Close window call back
		window.setWindowClosedCallback(new ModalWindow.WindowClosedCallback() {
			private static final long serialVersionUID = 1L;
			
			public void onClose(AjaxRequestTarget target) {
				
			}
			
		});

		// Initialise window settings
		window.setMinimalHeight(420);
		window.setInitialHeight(520);
		window.setMinimalWidth(750);
		window.setInitialWidth(750);
		window.setMaskType(MaskType.SEMI_TRANSPARENT);
		window.setCssClassName(ModalWindow.CSS_CLASS_GRAY);
		
		return window;
	}

}
