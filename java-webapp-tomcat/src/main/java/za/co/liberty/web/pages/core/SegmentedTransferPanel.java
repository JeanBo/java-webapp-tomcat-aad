package za.co.liberty.web.pages.core;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow.MaskType;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.IModel;

import za.co.liberty.dto.agreement.core.CoreTransferDto;
import za.co.liberty.interfaces.core.CoreTransferRequestType;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.data.pages.IModalMaintenancePageModel;
import za.co.liberty.web.pages.core.model.CoreTransferPageModel;
import za.co.liberty.web.pages.core.model.CoreUploadModel;
import za.co.liberty.web.wicket.markup.html.form.SRSAbstractChoiceRenderer;
import za.co.liberty.web.wicket.modal.SRSModalWindow;

public class SegmentedTransferPanel extends AbstractTransferPanel {

	private static final long serialVersionUID = 1L;

	protected DropDownChoice requestKindChoice;

	public SegmentedTransferPanel(String tab_panel_id,
			CoreTransferPageModel model, EditStateType editState,
			FeedbackPanel feedBackPanel, SegmentedTransferPage page) {
		super(tab_panel_id, model, editState, feedBackPanel, page);
		add(searchWindow = createSearchWindow("searchPartyWindow"));
		add(uploadWindow = createUploadWindow("uploadWindow"));
	}

	public Form createSearchForm(String id) {
		Form form = super.createSearchForm(id);
		if (formInitialised)
			return form;
		form.add(transferGrid = createTransferGrid("transferList", "segmented"));
		form.add(createUploadButton("uploadButton"));
		form.add(createRequestKindChoice("requestKind"));
		// form.add(createBulkRequestButton("requestButton"));
		formInitialised = true;
		return form;
	}

	private DropDownChoice createRequestKindChoice(String id) {
		IModel model = new IModel() {
			private static final long serialVersionUID = 1L;

			public Object getObject() {
				return pageModel.getRequestCategory();
			}

			public void setObject(Object arg0) {
				pageModel.setRequestCategory((CoreTransferRequestType) arg0);
			}

			public void detach() {
			}
		};
		DropDownChoice field = new DropDownChoice(id, model,
				pageModel.getRequestCategoryDTO(), new SRSAbstractChoiceRenderer<Object>() {
		
					private static final long serialVersionUID = -4367276358153378234L;

					public Object getDisplayValue(Object value) {
						return (value == null) ? null
								: ((CoreTransferRequestType) value).getName();
					}

					public String getIdValue(Object arg0, int arg1) {
						return arg1 + "";
					}
				});

		// Update the request kinds field when changing this one
		AjaxFormComponentUpdatingBehavior behaviour = new AjaxFormComponentUpdatingBehavior(
				"change") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				// The request category has changed, update the list of request
				// kinds.
				CoreTransferRequestType dto = pageModel.getRequestCategory();
				if (dto != null && dto.name() != null) {
					pageModel.setTransferType(String.valueOf(dto.getId()));
				} else {
					pageModel.setTransferType("");
				}
			}

		};
		if (editState.isViewOnly()) {
			field.setEnabled(false);
		}
		field.add(behaviour);
		field.setNullValid(true);
		return field;
	}

	protected Button createUploadButton(String id) {
		final Button button = new Button(id);
		button.add(new AjaxFormComponentUpdatingBehavior("click") {
			private static final long serialVersionUID = 0L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				uploadWindow.show(target);
			}
		});
		return button;
	}

	private SRSModalWindow createUploadWindow(final String id) {
		final SRSModalWindow window = new SRSModalWindow(id) {

			@Override
			public String getModalSessionIdentifier() {
				return "CORE.segment.upload-";
			}
			
		};
		window.setTitle("Upload File");
		window.setPageCreator(new ModalWindow.PageCreator() {
			private static final long serialVersionUID = 1L;

			public Page createPage() {
				return new SegmentUploadPage(EditStateType.MODIFY, window,
						pageModel);
			}
		});
		window.setWindowClosedCallback(new ModalWindow.WindowClosedCallback() {
			private static final long serialVersionUID = 1L;

			public void onClose(AjaxRequestTarget target) {
				IModalMaintenancePageModel<CoreUploadModel> model = window.getSessionModelForPage();
				window.clearModalPageModelInSession();
				
				System.out.println("Result isModalWizardSuccess=" + model.isModalWizardSucces()
					+ "  - " + model.getSelectedItem());
				if (model.isModalWizardSucces() && model.getSelectedItem() != null) {
					pageModel.getSegTransferDto().clear();
					pageModel.getSegTransferDto().addAll(model.getSelectedItem().getCoreTransferDto());
					pageModel.getConsultantMap().clear();
					pageModel.getConsultantMap().putAll(model.getSelectedItem().getConsultantMap());
					doProcessSelectedItems(target);
//					target.add(transferGrid);
				} else {
					getFeedBackPanel().info("Upload cancelled");
					target.add(getFeedBackPanel());
				}
	
			}			
		});
		// Initialise window settings
		window.setMinimalHeight(150);
		window.setInitialHeight(150);
		window.setMinimalWidth(520);
		window.setInitialWidth(520);
		window.setMaskType(MaskType.SEMI_TRANSPARENT);
		window.setCssClassName(ModalWindow.CSS_CLASS_GRAY);
		return window;
	}

	public void doProcessSelectedItems(AjaxRequestTarget target) {
		CoreHelper coreHelper = getCoreHelper();
		List<CoreTransferDto> segList = pageModel.getSegTransferDto();
		List<CoreTransferDto> blankRowList = new ArrayList<CoreTransferDto>();
		if (segList == null || segList.size() == 0) {
			feedbackPanel.error("Please add record/records to transfer");
		} else {
			refreshPageModel();
			List<CoreTransferDto> coreTransferList = pageModel
					.getCoreTransferDto();

			coreTransferList.removeAll(coreTransferList);
			int count = coreTransferList.size();
			for (CoreTransferDto dto : segList) {
				pageModel.getCoreTransferDto().add(dto);
				if (++count >= CoreHelper.pageSize)
					break;
			}
			segList.removeAll(coreTransferList);

			coreHelper.validateSegmentGrid(pageModel, feedbackPanel);
			/*
			 * grid.setRowsPerPage(50); grid.setContentHeight(450, SizeUnit.PX);
			 */
		}
		target.add(transferGrid);
		target.add(feedbackPanel);
	}

}
