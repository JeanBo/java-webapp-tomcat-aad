package za.co.liberty.web.pages.maintainagreement;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.feedback.ContainerFeedbackMessageFilter;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.image.ContextImage;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import za.co.liberty.business.guicontrollers.agreement.IAgreementGUIController;
import za.co.liberty.common.domain.Percentage;
import za.co.liberty.common.enums.PoolRatePercentageEnum;
import za.co.liberty.dto.agreement.AgreementDTO;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.helpers.lookup.ServiceLocator;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.data.enums.fields.AgreementGUIField;
import za.co.liberty.web.pages.maintainagreement.model.AgencyPoolAccountDetailsPanelModel;
import za.co.liberty.web.pages.maintainagreement.model.MaintainAgreementPageModel;
import za.co.liberty.web.pages.panels.GUIFieldPanel;
import za.co.liberty.web.pages.panels.HelperPanel;
import za.co.liberty.web.pages.panels.ViewTemplateBasePanel;
import za.co.liberty.web.wicket.convert.converters.SRSDateConverter;
import za.co.liberty.web.wicket.markup.html.form.SRSAbstractChoiceRenderer;
import za.co.liberty.web.wicket.markup.html.form.SRSDropDownChoice;
import za.co.liberty.web.wicket.validation.maintainagreement.AgencyIntoPoolFormValidator;
import za.co.liberty.web.wicket.view.ContextDrivenViewTemplate;

public class AgencyIntoPoolAccountPanel extends
		ViewTemplateBasePanel<AgreementGUIField, AgreementDTO> {

	private static final long serialVersionUID = 1L;

	private AgreementDTO viewTemplateContext;
	private AgencyPoolAccountDetailsPanelModel panelModel;
	//private MaintainAgreementPageModel pageModel;

	private AgencyPoolAccountIntoPoolForm pageForm;
	private GUIFieldPanel futureIntoPoolRateOverridePanel;
	private GUIFieldPanel futureIntoPoolRateOverrideEndDatePanel;
	private GUIFieldPanel futureIntoPoolRateOverrideStartDatePanel;
	private SRSDropDownChoice<Percentage> futureIntoPoolRateOverrideDD;
	private IAgreementGUIController guiController;
	private boolean overrideDisabled;
	private FeedbackPanel poolIntoFeedbackPanel;

	private static final Logger logger = Logger
			.getLogger(AgencyIntoPoolAccountPanel.class);

	private boolean initialised;

	private SRSDropDownChoice<Date> futureOverrideRateEndDatedDD;

	

	public static final int SELECTION_WIDTH = 300;
	
	private static String DATE_FORMAT = SRSDateConverter.DATE_FORMAT_PATTERN;

	// private MaintainAgreementPageModel pageModel;

	public AgencyIntoPoolAccountPanel(String id,
			AgencyPoolAccountDetailsPanelModel panelModel,EditStateType editState,
			Page parentPage) {
		super(id, editState, parentPage);
		//this.pageModel = pageModel;
		this.panelModel = panelModel;

	}

	public AgencyIntoPoolAccountPanel(String id,
			MaintainAgreementPageModel pageModel2, EditStateType editState,
			Page parentPage) {
		super(id, editState, parentPage);
	}

	@Override
	protected void onBeforeRender() {
		if (!initialised) {
			initialised = true;
			// initialize the page model with the agreement data
			initPageModel();

			add(getAgencyIntoPoolPageForm());

		}

		super.onBeforeRender();
	};

	private void initPageModel() {
		if (panelModel == null) {
			panelModel = new AgencyPoolAccountDetailsPanelModel();
		}
		
		
		
		
	}

	private Component getAgencyIntoPoolPageForm() {
		if ((panelModel.getAgencyPoolAccountDetailDTO()
				.getEffectiveIntoAgencyPoolRate() != null)
				&& (panelModel.getAgencyPoolAccountDetailDTO()
						.getEffectiveIntoAgencyPoolRate().getValue()
						.compareTo(new BigDecimal(0.50)) >= 0)) {
			overrideDisabled = true;
		}
		if (pageForm == null) {
			pageForm = new AgencyPoolAccountIntoPoolForm(
					"agencyIntoPoolAccountForm");
		}

		if (overrideDisabled && getEditState()!=EditStateType.AUTHORISE) {
			pageForm.setEnabled(false);
			AgencyIntoPoolAccountPanel.this
					.info("The Into Pool Rate cannot exceed 50%. The Into Pool Rate cannot be overridden.");
		}
		
		if (getEditState().equals(EditStateType.MODIFY)) {
			/**
			 * Add the Stop Pool Draw validation
			 */
			this.pageForm.add(new AgencyIntoPoolFormValidator((panelModel
					.getAgencyPoolAccountDetailDTO())));
		}
		return pageForm;
	}

	public class AgencyPoolAccountIntoPoolForm extends Form<Object> {

		private static final long serialVersionUID = 1L;

		public AgencyPoolAccountIntoPoolForm(String id) {
			super(id);
			add(createGUIFieldPanel("Tenure", "Tenure", "tenure",
					HelperPanel.getInstance("panel",
							new Label("value", new PropertyModel<Object>(
									panelModel.getAgencyPoolAccountDetailDTO(),
									"tenure")))));
			add(createGUIFieldPanel("Current AQC", "Current AQC", "currentAQC",
					HelperPanel.getInstance("panel",
							new Label("value", new PropertyModel<Object>(
									panelModel.getAgencyPoolAccountDetailDTO(),
									"currentAQC")))));

			add(createGUIFieldPanel("Into Pool Rate Derived",
					"Into Pool Rate Derived", "intoPoolRateDerived",
					HelperPanel.getInstance("panel",
							new Label("value", new PropertyModel<Object>(
									panelModel.getAgencyPoolAccountDetailDTO(),
									"derivedIntoAgencyPoolRate")))));

			add(createGUIFieldPanel("Into Pool Rate Override",
					"Into Pool Rate Override", "override",
					HelperPanel.getInstance("panel",
							new Label("value", new PropertyModel<Object>(
									panelModel.getAgencyPoolAccountDetailDTO(),
									"overrideIntoAgencyPoolRate")))));
			add(createGUIFieldPanel("Override Start Date",
					"Override Start Date", "overrideStart",
					HelperPanel.getInstance("panel",
							new Label("value", new PropertyModel<Object>(
									panelModel.getAgencyPoolAccountDetailDTO(),
									"overrideIntoAgencyPoolStartDate")))));
			add(createGUIFieldPanel("Override End Date", "Override End Date",
					"overrideEnd", HelperPanel.getInstance("panel",
							new Label("value", new PropertyModel<Object>(
									panelModel.getAgencyPoolAccountDetailDTO(),
									"overrideIntoAgencyPoolEndDate")))));
			add(createGUIFieldPanel("Effective Into Pool Rate",
					"Effective Into Pool Rate", "effectiveIntoPoolRate",
					HelperPanel.getInstance("panel",
							new Label("value", new PropertyModel<Object>(
									panelModel.getAgencyPoolAccountDetailDTO(),
									"effectiveIntoAgencyPoolRate")))));

			// Future Override
			add(getFutureIntoPoolRateOverridePanel());			
			add(createFutureStartDatePanel());
			add(createFutureEndDatePanel());
			add(getPoolIntoFeedbackPanel());

			/*
			 * WebMarkupContainer container = new
			 * WebMarkupContainer("requestDetail");
			 * if(!getEditState().isViewOnly()){
			 * container.setOutputMarkupId(true); container.setVisible(false); }
			 */
		}

	}
	/**
	 * Creates the icon component for the node
	 * 
	 * @param componentId
	 * @param tree
	 * @param model
	 * @return icon image component
	 */
	protected Component newImageComponent(String componentId)
 {
		return new Image(componentId) {
			private static final long serialVersionUID = 1L;

			protected void onComponentTag(final ComponentTag tag) {
				checkComponentTag(tag, "img");
				tag.put("src", "/SRSAppWeb/images/question.png");
			}

		};

	}
	
	private FeedbackPanel getPoolIntoFeedbackPanel() {
		if (poolIntoFeedbackPanel == null) {
			poolIntoFeedbackPanel = new FeedbackPanel("poolIntofeedBack");
		}
		poolIntoFeedbackPanel.setEnabled(true);
		poolIntoFeedbackPanel.setVisible(true);
		poolIntoFeedbackPanel.setOutputMarkupId(true);

		poolIntoFeedbackPanel.setFilter(new ContainerFeedbackMessageFilter(this));		

		return poolIntoFeedbackPanel;
	}


	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public GUIFieldPanel getFutureIntoPoolRateOverridePanel() {

		if (futureIntoPoolRateOverridePanel == null) {

			IModel model = new IModel() {
				private static final long serialVersionUID = -7036637395813602443L;

				public Object getObject() {
					return panelModel.getAgencyPoolAccountDetailDTO()
							.getFutureOverrideIntoAgencyPoolRate();
				}

				public void setObject(Object arg0) {
					panelModel.getAgencyPoolAccountDetailDTO()
							.setFutureOverrideIntoAgencyPoolRate(
									(Percentage) arg0);
				}

				public void detach() {
				}
			};

			futureIntoPoolRateOverrideDD = new SRSDropDownChoice("value",
					model, PoolRatePercentageEnum.getRateList(panelModel
							.getAgencyPoolAccountDetailDTO()
							.getDerivedIntoAgencyPoolRate()),
					new SRSAbstractChoiceRenderer<Object>() {
			
						private static final long serialVersionUID = 3880816096499874020L;

						public Object getDisplayValue(Object value) {
							return (value == null) ? null : value;
						}

						public String getIdValue(Object arg0, int arg1) {
							return arg1 + "";
						}
					}, "Select");
			
			futureIntoPoolRateOverrideDD.add(new AjaxFormComponentUpdatingBehavior(
					"change") {
				private static final long serialVersionUID = -6347930149876339644L;

				@Override
				protected void onUpdate(AjaxRequestTarget target) {	
							Percentage input = (Percentage) getFormComponent()
									.getConvertedInput();
							panelModel.getAgencyPoolAccountDetailDTO()
									.setFutureOverrideIntoAgencyPoolRate(
											(Percentage) input);
				}
			});		
			futureIntoPoolRateOverrideDD.setOutputMarkupId(true);
			
			// Add the tool tip and the image
			ContextImage image = new ContextImage("img",
					"/SRSAppWeb/images/question.png");
			image.add(new AttributeModifier("title",new Model(this
					.getString("tooltip.intoAgencyPoolRateOverride"))));
			image.add(new AttributeModifier("align", "center"));

			futureIntoPoolRateOverridePanel = createGUIFieldPanel(
					"Future Into Pool Rate Override",
					"Future Into Pool Rate Override", "futureOverride",
					HelperPanel.getInstance("panel",
							futureIntoPoolRateOverrideDD, image));

		}
		futureIntoPoolRateOverridePanel.setOutputMarkupId(true);
		if (getEditState() == EditStateType.MODIFY) {
			futureIntoPoolRateOverridePanel.setEnabled(true);
		} else {
			futureIntoPoolRateOverridePanel.setEnabled(false);
		}
		
		
		if ((panelModel.getAgencyPoolAccountDetailDTO()
				.getFutureOverrideIntoAgencyPoolRate() != null && panelModel
				.getAgencyPoolAccountDetailDTO()
				.getFutureOverrideIntoAgencyPoolRate().getValue()
				.compareTo(new BigDecimal(0.00)) > 0)
				|| (getEditState() == EditStateType.MODIFY && !overrideDisabled)) {
			futureIntoPoolRateOverridePanel.setVisible(true);
		} else {
			futureIntoPoolRateOverridePanel.setVisible(false);
		}
		return futureIntoPoolRateOverridePanel;
	}
	

	
	/** 
	 * Create date label
	 * 
	 * @param 
	 * @return
	 */
	public GUIFieldPanel createFutureStartDatePanel() {

		if (futureIntoPoolRateOverrideStartDatePanel == null) {

			IModel<String> model = new IModel<String>() {
				private static final long serialVersionUID = 1L;

				public String getObject() {
					return calcFutureOverrideIntoAgencyPoolStartDate();
				}

				public void setObject(String arg0) {
					// Not implemented
				}

				public void detach() {
				}
			};

			futureIntoPoolRateOverrideStartDatePanel = createGUIFieldPanel(
					"Future Override Start Date", "Future Override Start Date",
					"futureOverrideStart",
					HelperPanel.getInstance("panel", new Label("value", model)));			
			calcFutureOverrideIntoAgencyPoolStartDate();
		}

		if (getEditState() == EditStateType.MODIFY
				&& panelModel.getAgencyPoolAccountDetailDTO()
						.getFutureOverrideIntoAgencyPoolStartDate() != null) {
			futureIntoPoolRateOverrideStartDatePanel.setEnabled(true);
		} else {
			futureIntoPoolRateOverrideStartDatePanel.setEnabled(false);
		}

		if ((panelModel.getAgencyPoolAccountDetailDTO()
				.getFutureOverrideIntoAgencyPoolRate() != null && panelModel
				.getAgencyPoolAccountDetailDTO()
				.getFutureOverrideIntoAgencyPoolRate().getValue()
				.compareTo(new BigDecimal(0.00)) > 0)
				|| (getEditState() == EditStateType.MODIFY && !overrideDisabled)) {
			futureIntoPoolRateOverrideStartDatePanel.setVisible(true);
		} else {
			futureIntoPoolRateOverrideStartDatePanel.setVisible(false);
		}

		return futureIntoPoolRateOverrideStartDatePanel;
	}
	
	/** 
	 * Create date label
	 * 
	 * @param id
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public GUIFieldPanel createFutureEndDatePanel() {


		if (futureIntoPoolRateOverrideEndDatePanel == null) {

			IModel model = new IModel() {
				private static final long serialVersionUID = 1L;

				public Object getObject() {

//					String resp = "";
//
//					SimpleDateFormat f = new SimpleDateFormat(DATE_FORMAT);
//					if (panelModel.getAgencyPoolAccountDetailDTO()
//							.getFutureOverrideIntoAgencyPoolRate() != null
//							&& panelModel.getAgencyPoolAccountDetailDTO()
//									.getFutureOverrideIntoAgencyPoolRate().getValue()
//									.compareTo(new BigDecimal(0.00)) != 0) {
//						resp = f.format(panelModel.getAgencyPoolAccountDetailDTO()
//								.getFutureOverrideIntoAgencyPoolEndDate());
//					} else if (getEditState() == EditStateType.MODIFY
//							&& panelModel.getAgencyPoolAccountDetailDTO()
//									.getFutureOverrideIntoAgencyPoolRate() == null
//							&& panelModel.getAgencyPoolAccountDetailDTO()
//									.getFutureOverrideIntoAgencyPoolEndDate() != null) {
//						resp = f.format(panelModel.getAgencyPoolAccountDetailDTO()
//								.getFutureOverrideIntoAgencyPoolEndDate());
//					} else {
//						resp = "";
//					}
//
//					return resp;
					
					return panelModel.getAgencyPoolAccountDetailDTO()
							.getFutureOverrideIntoAgencyPoolEndDate();
				
				}

				public void setObject(Object arg0) {
					panelModel.getAgencyPoolAccountDetailDTO()
							.setFutureOverrideIntoAgencyPoolEndDate((Date) arg0);
				}

				public void detach() {
				}
			};
			
			// Get the liberty month end dates
			List<Date> finalEndDates = new ArrayList<Date>();
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.YEAR, 1);
			cal.set(cal.get(Calendar.YEAR), 11, 31);
			List<Date> endDates = getGuiController()
					.getLibertyMonthDatesForCurrentYear(new Date(),cal.getTime(),12, 1);
			
			// If any end dates is less than the start date, take it off the list.
			for (Date endDate : endDates) {
				if (endDate != null
						&& panelModel.getAgencyPoolAccountDetailDTO()
								.getFutureOverrideIntoAgencyPoolStartDate() != null
						&& endDate.compareTo(panelModel
								.getAgencyPoolAccountDetailDTO()
								.getFutureOverrideIntoAgencyPoolStartDate()) < 0) {
				} else {
					finalEndDates.add(endDate);
				}

			}

			futureOverrideRateEndDatedDD = new SRSDropDownChoice("value", model,
					finalEndDates, 	new SRSAbstractChoiceRenderer<Object>() {
						private static final long serialVersionUID = 3880816096499874020L;

						public Object getDisplayValue(Object value) {
							return (value == null) ? null : value;
						}

						public String getIdValue(Object arg0, int arg1) {
							return arg1 + "";
						}
					}, "Select");
			
			futureOverrideRateEndDatedDD.setOutputMarkupId(true);
			
			futureOverrideRateEndDatedDD.add(new AjaxFormComponentUpdatingBehavior(
					"change") {
				private static final long serialVersionUID = -6347930149876339644L;

				@Override
				protected void onUpdate(AjaxRequestTarget target) {	
					Date input = (Date) getFormComponent()
									.getConvertedInput();
							panelModel.getAgencyPoolAccountDetailDTO()
									.setFutureOverrideIntoAgencyPoolEndDate(
											(Date) input);
				}
			});	

			futureIntoPoolRateOverrideEndDatePanel = createGUIFieldPanel(
					"Future Override End Date", "Future Override End Date",
					"futureOverrideEnd",
					HelperPanel.getInstance("panel", futureOverrideRateEndDatedDD));

			futureIntoPoolRateOverrideEndDatePanel.setOutputMarkupId(true);
			if (getEditState() == EditStateType.MODIFY) {
				futureIntoPoolRateOverrideEndDatePanel.setEnabled(true);
			} else {
				futureIntoPoolRateOverrideEndDatePanel.setEnabled(false);
			}
			
			if ((panelModel.getAgencyPoolAccountDetailDTO()
					.getFutureOverrideIntoAgencyPoolRate() != null && panelModel
					.getAgencyPoolAccountDetailDTO()
					.getFutureOverrideIntoAgencyPoolRate().getValue()
					.compareTo(new BigDecimal(0.00)) > 0)
					|| (getEditState() == EditStateType.MODIFY && !overrideDisabled)) {
				futureIntoPoolRateOverrideEndDatePanel.setVisible(true);
			} else {
				futureIntoPoolRateOverrideEndDatePanel.setVisible(false);
			}

		}
		return futureIntoPoolRateOverrideEndDatePanel;
	}
	
	/** This method calculates the Future Override Start Date. */
	private String calcFutureOverrideIntoAgencyPoolStartDate () {
		String resp = "";

		SimpleDateFormat f = new SimpleDateFormat(DATE_FORMAT);
		if (panelModel.getAgencyPoolAccountDetailDTO()
				.getFutureOverrideIntoAgencyPoolRate() != null
				&& panelModel.getAgencyPoolAccountDetailDTO()
						.getFutureOverrideIntoAgencyPoolRate()
						.getValue().compareTo(new BigDecimal(0.00)) != 0) {
			/* If the Future Override Start Date is already populated in the
			 object, return the value.*/
			resp = f.format(panelModel
					.getAgencyPoolAccountDetailDTO()
					.getFutureOverrideIntoAgencyPoolStartDate());
		} else if (getEditState() == EditStateType.MODIFY
				&& (panelModel.getAgencyPoolAccountDetailDTO()
						.getFutureOverrideIntoAgencyPoolRate()==null || (panelModel.getAgencyPoolAccountDetailDTO()
				.getFutureOverrideIntoAgencyPoolRate()!=null && panelModel.getAgencyPoolAccountDetailDTO()
						.getFutureOverrideIntoAgencyPoolRate()
						.getValue().compareTo(new BigDecimal(0.00)) == 0))) {
			/*
			 * If the Edit state is modify and the Future Override Start Date is
			 * NOT set, then proceed to calculate the value.
			 */
			
			/*
			 * Set the start date as the next month start date. Passing
			 * Indicator value 0 represents month start
			 */
			Date dt = getGuiController()
					.getNextLibertyMonthDates(0);
			
			/*Set the month start date in the panel model*/
			panelModel.getAgencyPoolAccountDetailDTO()
					.setFutureOverrideIntoAgencyPoolStartDate(dt);
			
			/* Format and return the date value. */
			resp = f.format(panelModel
					.getAgencyPoolAccountDetailDTO()
					.getFutureOverrideIntoAgencyPoolStartDate());
		} else {
			resp = "";
		}

		return resp;
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

	/**
	 * Add style information to tag
	 * 
	 * @param tag
	 */
	protected void decorateStyleOnTag(ComponentTag tag) {
		String style = (String) tag.getAttributes().get("style");
		style = (style == null) ? "" : style;
		style += ";width:" + SELECTION_WIDTH + ";";
		tag.put("style", style);
	}

	/**
	 * Load the AgreementGUIController dynamically if it is null as this is a
	 * transient variable.
	 * 
	 * @return {@link IAgreementGUIController}
	 */
	private IAgreementGUIController getGuiController() {
		if (guiController == null) {
			try {
				guiController = ServiceLocator
						.lookupService(IAgreementGUIController.class);
			} catch (NamingException e) {
				logger.fatal("Could not lookup AgreementGUIController", e);
				throw new CommunicationException(
						"Could not lookup AgreementGUIController", e);
			}
		}
		return guiController;
	}

}
