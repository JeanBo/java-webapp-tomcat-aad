package za.co.liberty.web.pages.maintainagreement;

import org.apache.log4j.Logger;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.image.ContextImage;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import za.co.liberty.dto.agreement.AgreementDTO;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.data.enums.fields.AgreementGUIField;
import za.co.liberty.web.pages.maintainagreement.model.AgencyPoolAccountDetailsPanelModel;
import za.co.liberty.web.pages.maintainagreement.model.MaintainAgreementPageModel;
import za.co.liberty.web.pages.panels.GUIFieldPanel;
import za.co.liberty.web.pages.panels.HelperPanel;
import za.co.liberty.web.pages.panels.ViewTemplateBasePanel;
import za.co.liberty.web.wicket.validation.maintainagreement.StopAgencyPoolTransferFormValidator;
import za.co.liberty.web.wicket.view.ContextDrivenViewTemplate;

public class StopAgencyPoolTransferPanel extends
		ViewTemplateBasePanel<AgreementGUIField, AgreementDTO> {

	private static final long serialVersionUID = 1L;

	private AgreementDTO viewTemplateContext;
	private AgencyPoolAccountDetailsPanelModel panelModel;

	private StopAgencyPoolTransferForm pageForm;
	private GUIFieldPanel closeAndReleasePoolBalancePanel;
	private GUIFieldPanel stopIntoPoolTransfersPanel;
	private GUIFieldPanel commentsPanel;
	private Boolean stopPoolIndAtLoad;
	private String stopPoolCommentAtLoad;

	private static final Logger logger = Logger
			.getLogger(StopAgencyPoolTransferPanel.class);

	public StopAgencyPoolTransferPanel(String id, EditStateType editState,
			AgencyPoolAccountDetailsPanelModel panelModel, Page parentPage) {
		super(id, editState, parentPage);
		//this.pageModel = panelModel;
		this.panelModel = panelModel;		
		add(getPageForm());

	}

	public StopAgencyPoolTransferPanel(String id,
			MaintainAgreementPageModel pageModel2,
			FeedbackPanel feedbackPanel2, EditStateType editState,
			Page parentPage) {
		super(id, editState, parentPage);
	}

	private Component getPageForm() {
		if (pageForm == null) {
			pageForm = new StopAgencyPoolTransferForm("stopAgencyPoolTransferForm");
		}
		
		if (getEditState().equals(EditStateType.MODIFY)) {
			/**
			 * Add the Stop Pool Draw validation
			 */
			this.pageForm.add(new StopAgencyPoolTransferFormValidator(
					(panelModel.getAgencyPoolAccountDetailDTO()),
					stopPoolIndAtLoad));
		}
		return pageForm;
	}

	public class StopAgencyPoolTransferForm extends Form<Object> {

		private static final long serialVersionUID = 1L;

		public StopAgencyPoolTransferForm(String id) {
			super(id);
			
			add(getStopIntoPoolTransfersPanel());
			add(getStopAgencyPoolCommentsPanel());
			add(getCloseAndReleasePoolBalancePanel());
		}

	}

	@SuppressWarnings("unchecked")
	private GUIFieldPanel getStopIntoPoolTransfersPanel() {
		if (stopIntoPoolTransfersPanel == null) {

			CheckBox checkBox = new CheckBox("value", new PropertyModel(
					panelModel.getAgencyPoolAccountDetailDTO(),
					"stopIntoPoolTransfers"));
			checkBox.setOutputMarkupId(true);
			checkBox.setOutputMarkupPlaceholderTag(true);
			
			// The the load value
			stopPoolIndAtLoad = panelModel.getAgencyPoolAccountDetailDTO()
					.getStopIntoPoolTransfers();
			// Add the tool tip and the image
			ContextImage image = new ContextImage("img",
					"/SRSAppWeb/images/question.png");
			image.add(new AttributeModifier("title", new Model(this
					.getString("tooltip.stopIntoAgencyPool"))));
			image.add(new AttributeModifier("align", "center"));

			checkBox.add(new AjaxFormComponentUpdatingBehavior("change") {
				private static final long serialVersionUID = 1L;

				@Override
				protected void onUpdate(AjaxRequestTarget target) {
					
				}
			});

			if (getEditState() == EditStateType.MODIFY) {
				checkBox.setEnabled(true);
			} else {
				checkBox.setEnabled(false);
			}
			stopIntoPoolTransfersPanel = createGUIFieldPanel("Stop Into Pool Transfers",
					"Stop Into Pool Transfers", "stopIntoPoolTransfers",
					 HelperPanel.getInstance("panel",
								checkBox,image));

		}

		return stopIntoPoolTransfersPanel;
	}

	@SuppressWarnings("unchecked")
	private GUIFieldPanel getCloseAndReleasePoolBalancePanel() {
		if (closeAndReleasePoolBalancePanel == null) {

			CheckBox checkBox = new CheckBox("value", new PropertyModel(
					panelModel.getAgencyPoolAccountDetailDTO(),
					"closeAgencyPool"));
			checkBox.setOutputMarkupId(true);
			checkBox.setOutputMarkupPlaceholderTag(true);
			
			// Add the tool tip and the image
			ContextImage image = new ContextImage("img",
					"/SRSAppWeb/images/question.png");
			image.add(new AttributeModifier("title", new Model(this
					.getString("tooltip.closeAgencyPool"))));
			image.add(new AttributeModifier("align", "center"));
			
			checkBox.add(new AjaxFormComponentUpdatingBehavior("change") {
				private static final long serialVersionUID = 1L;

				@Override
				protected void onUpdate(AjaxRequestTarget target) {
					
				}
			});
			
			if (getEditState() == EditStateType.MODIFY) {
				checkBox.setEnabled(true);
			} else {
				checkBox.setEnabled(false);
			}
						
			closeAndReleasePoolBalancePanel = createGUIFieldPanel("Close Pool Account",
					"Close Pool Account", "closeAgencyPool",
					 HelperPanel.getInstance("panel",
								checkBox,image));
		}
		return closeAndReleasePoolBalancePanel;
	}
	
	private GUIFieldPanel getStopAgencyPoolCommentsPanel() {
		
		// The the load value
		if (stopPoolCommentAtLoad == null) {
			stopPoolCommentAtLoad = (String) panelModel.getAgencyPoolAccountDetailDTO()
					.getStopIntoPoolTransferComment();
		}	

		IModel model = new IModel<Object>() {
			private static final long serialVersionUID = 1L;

			public Object getObject() {
				return (String) panelModel.getAgencyPoolAccountDetailDTO()
						.getStopIntoPoolTransferComment();
			}

			public void setObject(Object arg0) {
				panelModel.getAgencyPoolAccountDetailDTO()
						.setStopIntoPoolTransferComment((String) arg0);
			}

			public void detach() {
			}
		};

		TextArea<String> textArea = new TextArea<String>("value", model);
		textArea.add(new OnChangeAjaxBehavior() {

			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(final AjaxRequestTarget target) {

				// Maybe you want to update some components here?

				// Access the updated model object:
				String valueAsString = ((TextArea<String>) getComponent())
						.getModelObject();

				panelModel.getAgencyPoolAccountDetailDTO()
						.setStopIntoPoolTransferComment(valueAsString);
			}
		});
		textArea.setOutputMarkupId(true);
		textArea.setOutputMarkupPlaceholderTag(true);

		textArea.setEnabled(getEditState() == EditStateType.MODIFY);

		commentsPanel = createGUIFieldPanel("Comment", "Comment",
				"stopIntoPoolTransferComment",
				HelperPanel.getInstance("panel", textArea));

		return commentsPanel;
	}
	
	

	@Override
	protected ContextDrivenViewTemplate<AgreementGUIField, AgreementDTO> getViewTemplate() {
		return panelModel.getViewTemplate();
	}

	@Override
	protected AgreementDTO getViewTemplateContext() {
		if (viewTemplateContext == null) {
			viewTemplateContext = new AgreementDTO();
		}
		return viewTemplateContext;
	}

	private AgreementDTO getContext() {
		return getViewTemplateContext();
	}

}
