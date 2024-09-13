package za.co.liberty.web.pages.core;

import org.apache.wicket.Page;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow.MaskType;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;

import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.pages.core.model.CoreTransferPageModel;
import za.co.liberty.web.wicket.modal.SRSModalWindow;

public class ContractTransferPanel extends AbstractTransferPanel {

	public ContractTransferPanel(String tab_panel_id,
			CoreTransferPageModel model, EditStateType editState,
			FeedbackPanel feedBackPanel,
			ContractTransferPage contractTransferPage) {
		super(tab_panel_id, model, editState, feedBackPanel, 
				contractTransferPage);
		add(searchWindow = createSearchWindow("searchPartyWindow"));
		add(uploadWindow = createUploadWindow("uploadWindow"));
	}

	public Form createSearchForm(String id) {
		Form form = super.createSearchForm(id);
		if (formInitialised)
			return form;
		form.add(transferGrid = createTransferGrid("transferList", "contract"));
		formInitialised = true;
		return form;
	}

	private SRSModalWindow createUploadWindow(final String id) {
		final SRSModalWindow window = new SRSModalWindow(id) {

			@Override
			public String getModalSessionIdentifier() {
				return "CORE.contract.upload-";
			}
			
		};
		window.setTitle("Upload File");
		window.setPageCreator(new ModalWindow.PageCreator() {
			private static final long serialVersionUID = 1L;

			public Page createPage() {
				return new SegmentUploadPage(EditStateType.ADD, window,
						pageModel);
			}
		});
		// Initialise window settings
		window.setMinimalHeight(420);
		window.setInitialHeight(420);
		window.setMinimalWidth(750);
		window.setInitialWidth(750);
		window.setMaskType(MaskType.SEMI_TRANSPARENT);
		window.setCssClassName(ModalWindow.CSS_CLASS_GRAY);
		return window;
	}

}
