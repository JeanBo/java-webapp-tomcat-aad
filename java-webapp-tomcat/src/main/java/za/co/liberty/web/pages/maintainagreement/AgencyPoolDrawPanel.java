package za.co.liberty.web.pages.maintainagreement;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.image.ContextImage;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import za.co.liberty.business.guicontrollers.agreement.IAgreementGUIController;
import za.co.liberty.common.domain.CurrencyAmount;
import za.co.liberty.common.domain.Percentage;
import za.co.liberty.common.enums.CurrencyEnum;
import za.co.liberty.common.enums.PoolDrawOptionsEnum;
import za.co.liberty.common.enums.PoolDrawRatePercentageEnum;
import za.co.liberty.common.enums.PoolRatePercentageEnum;
import za.co.liberty.dto.agreement.AgreementDTO;
import za.co.liberty.dto.userprofiles.ISessionUserProfile;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.helpers.lookup.ServiceLocator;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.data.enums.fields.AgreementGUIField;
import za.co.liberty.web.pages.maintainagreement.model.AgencyPoolAccountDetailsPanelModel;
import za.co.liberty.web.pages.panels.GUIFieldPanel;
import za.co.liberty.web.pages.panels.HelperPanel;
import za.co.liberty.web.pages.panels.ViewTemplateBasePanel;
import za.co.liberty.web.system.SRSAuthWebSession;
import za.co.liberty.web.wicket.markup.html.form.SRSAbstractChoiceRenderer;
import za.co.liberty.web.wicket.markup.html.form.SRSDropDownChoice;
import za.co.liberty.web.wicket.validation.maintainagreement.AgencyPoolDrawFormValidator;
import za.co.liberty.web.wicket.view.ContextDrivenViewTemplate;

public class AgencyPoolDrawPanel extends
		ViewTemplateBasePanel<AgreementGUIField, AgreementDTO> {

	private static final long serialVersionUID = 1L;

	private AgreementDTO viewTemplateContext;
	private AgencyPoolAccountDetailsPanelModel panelModel;
	private AgencyPoolDrawForm pageForm;
	private GUIFieldPanel poolDrawRateSelectedPanel;
	private HelperPanel poolDrawChoicePanel;
	private GUIFieldPanel requestedPaymentAmountPanel;
	private GUIFieldPanel currentPoolBalancePanel;
	private GUIFieldPanel currentPoolBalanceRandAmtPanel;
	private GUIFieldPanel poolDrawRateDerivedPanel;
	private GUIFieldPanel expectedPaymentPanel;
	private GUIFieldPanel requestedPaymentPercentagePanel;
	private Image expectedPaymentPanelHelp;
	private SRSDropDownChoice poolDrawRateSelectedDD;
	boolean isPercentageOfBalSelected = true;
	private WebMarkupContainer poolDrawPercentageContainer;
	private WebMarkupContainer poolDrawAmountContainer;
	private String poolDrawType;
	private String infoMessage;
	private boolean outstandingRequest;
	boolean isTwentyPercentOfDPEEarning = false;
	boolean isZeroPoolBalance = false;
	
	private transient IAgreementGUIController guiController;

	private static final Logger logger = Logger
			.getLogger(AgencyPoolDrawPanel.class);

	private static List<Object> ratesList;
	private static List<Object> poolDrawOptionsList;


	static {

		ratesList = new ArrayList<Object>();
		for (PoolRatePercentageEnum poolRate : PoolRatePercentageEnum.values()) {
			ratesList.add(poolRate);
		}

		poolDrawOptionsList = new ArrayList<Object>();
		for (PoolDrawOptionsEnum option : PoolDrawOptionsEnum.values()) {
			poolDrawOptionsList.add(option.getValue());
		}
	}

	public AgencyPoolDrawPanel(String id, EditStateType editState) {
		super(id, editState);
	}

	public AgencyPoolDrawPanel(
			String id,
			EditStateType editState,
			AgencyPoolAccountDetailsPanelModel agencyPoolAccountDetailsPanelModel,
			Page parentPage,boolean outstandingRequest) {
		super(id, editState, parentPage);
		this.panelModel = agencyPoolAccountDetailsPanelModel;
		this.outstandingRequest = outstandingRequest;
		add(getPageForm());
	}

	private Component getPageForm() {
		BigDecimal twentyPercentValue = null;	
		
		if (getEditState() == EditStateType.AUTHORISE) {
			if (panelModel.getAgencyPoolAccountDetailDTO() == null
					|| panelModel.getAgencyPoolAccountDetailDTO()
							.getPoolDrawOption() != null
					&& panelModel
							.getAgencyPoolAccountDetailDTO()
							.getPoolDrawOption()
							.equalsIgnoreCase(
									PoolDrawOptionsEnum.SPECIFIC_RAND_AMOUNT
											.getValue())) {
				isPercentageOfBalSelected = false;
			}
		}

		if (pageForm == null) {
			pageForm = new AgencyPoolDrawForm("agencyPoolDrawForm");
			pageForm.setOutputMarkupId(true);
		}
		
		if (getEditState() == EditStateType.AUTHORISE) {
			pageForm.setEnabled(false);
		}

		// Ascertain if the pool balance is >0. If the pool balance is 0, pool
		// draw cannot be made

		isZeroPoolBalance = (panelModel.getAgencyPoolAccountDetailDTO() != null
				&& panelModel.getAgencyPoolAccountDetailDTO()
						.getCurrentPoolBalance() != null && panelModel
				.getAgencyPoolAccountDetailDTO().getCurrentPoolBalance()
				.getValue().compareTo(new BigDecimal(0.00)) <= 0);

		// Ascertain if the current pool balance is 20% of last year's DPE
		// earnings

		if (panelModel.getAgencyPoolAccountDetailDTO() == null
				|| panelModel.getAgencyPoolAccountDetailDTO()
						.getLastyearDPEEarnings() == null) {
			isTwentyPercentOfDPEEarning = true;
		} else {
			twentyPercentValue = percentageValue(panelModel
					.getAgencyPoolAccountDetailDTO().getLastyearDPEEarnings()
					.getValue(), new BigDecimal(20.00));

			if (panelModel.getAgencyPoolAccountDetailDTO()
					.getCurrentPoolBalance() != null
					&& (panelModel.getAgencyPoolAccountDetailDTO()
							.getCurrentPoolBalance().getValue()
							.compareTo(twentyPercentValue) < 0)) {
				isTwentyPercentOfDPEEarning = false;
			} else {
				isTwentyPercentOfDPEEarning = true;
			}
		}
		if (isZeroPoolBalance) {
			pageForm.setEnabled(false);
		}
		
		if (!isTwentyPercentOfDPEEarning && getEditState()!=EditStateType.AUTHORISE) {
			AgencyPoolDrawPanel.this
					.info("Adviser has not met previous year's income validation standard : "
							+ new CurrencyAmount(twentyPercentValue,
									CurrencyEnum.ZAR,
									CurrencyAmount.ROUND_HALF_UP));

		}
		
		if (isZeroPoolBalance && getEditState()!=EditStateType.AUTHORISE) {
			AgencyPoolDrawPanel.this
					.warn("Adviser's current Pool Balance is Zero. Pool Draws cannot be made.");

		}
		
		if (getEditState().equals(EditStateType.MODIFY)) {
			/**
			 * Add the Agency Pool Draw validation
			 */
			this.pageForm.add(new AgencyPoolDrawFormValidator((panelModel
					.getAgencyPoolAccountDetailDTO())));
		}
		return pageForm;
	}

	public class AgencyPoolDrawForm extends Form<Object> {

		private static final long serialVersionUID = 1L;

		public AgencyPoolDrawForm(String id) {
			super(id);
			//add(getSpecificRandAmtFeedbackPanel());
			add(getPoolDrawToChoicePanel());
			add(getPoolDrawPercentageContainer());
			add(getPoolDrawAmountContainer());

			checkFieldVisibility();
		}

	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public GUIFieldPanel getPoolDrawRateSelectedPanel() {

		if (poolDrawRateSelectedPanel == null) {

			IModel model = new IModel() {
				private static final long serialVersionUID = -7036637395813602443L;

				public Object getObject() {
					return panelModel.getAgencyPoolAccountDetailDTO()
							.getPoolDrawRateSelected();
				}

				public void setObject(Object arg0) {
					panelModel.getAgencyPoolAccountDetailDTO()
							.setPoolDrawRateSelected((Percentage) arg0);
				}

				public void detach() {
				}
			};

			poolDrawRateSelectedDD = new SRSDropDownChoice("value", model,
					PoolDrawRatePercentageEnum.getDrawRateList(panelModel
							.getAgencyPoolAccountDetailDTO()
							.getPoolDrawRateDerived()), new SRSAbstractChoiceRenderer<Object>() {

						private static final long serialVersionUID = 3880816096499874020L;

						public Object getDisplayValue(Object value) {
							return (value == null) ? null : value;
						}

						public String getIdValue(Object arg0, int arg1) {
							return arg1 + "";
						}
					}, "Select");

			/* Add select behavior */
			poolDrawRateSelectedDD.add(new AjaxFormComponentUpdatingBehavior(
					"change") {
				private static final long serialVersionUID = 1L;

				@Override
				protected void onUpdate(AjaxRequestTarget target) {
					/*
					 * If the Pool Draw Rate selected is not null and is greater
					 * than 0.00, then use the selected percentage. Else use the
					 * derived percentage.
					 */
					Percentage perc = panelModel
							.getAgencyPoolAccountDetailDTO()
							.getPoolDrawRateSelected() != null
							&& panelModel
									.getAgencyPoolAccountDetailDTO()
									.getPoolDrawRateSelected()
									.isGreaterThan(
											new Percentage(new BigDecimal(0.00))) ? panelModel
							.getAgencyPoolAccountDetailDTO()
							.getPoolDrawRateSelected() : panelModel
							.getAgencyPoolAccountDetailDTO()
							.getPoolDrawRateDerived();

					BigDecimal expectedPayment = percentage(panelModel
							.getAgencyPoolAccountDetailDTO()
							.getCurrentPoolBalance().getValue(),
							perc.getValue());

					panelModel.getAgencyPoolAccountDetailDTO()
							.setExpectedPayment(
									new CurrencyAmount(expectedPayment,
											CurrencyEnum.ZAR,
											CurrencyAmount.ROUND_HALF_UP));

					target.add(getExpectedPaymentPanel());
				}
			});
			poolDrawRateSelectedDD.setOutputMarkupId(true);

			poolDrawRateSelectedPanel = createGUIFieldPanel(
					"Pool Draw Rate Selected", "Pool Draw Rate Selected",
					"poolDrawRateSelected",
					HelperPanel.getInstance("panel", poolDrawRateSelectedDD));

			poolDrawRateSelectedPanel.setOutputMarkupId(true);
			if (getEditState() == EditStateType.MODIFY) {
				poolDrawRateSelectedPanel.setEnabled(true);
			} else {
				poolDrawRateSelectedPanel.setEnabled(false);
			}

		}
		return poolDrawRateSelectedPanel;
	}
	
	
	private HelperPanel getPoolDrawToChoicePanel() {

		IModel model = new IModel() {
			private static final long serialVersionUID = 1L;

			public Object getObject() {

				return panelModel.getAgencyPoolAccountDetailDTO()
						.getPoolDrawOption();
			}

			public void setObject(Object arg0) {
				
				panelModel.getAgencyPoolAccountDetailDTO()
				.setPoolDrawOption((String) arg0);
			}

			public void detach() {
			}
		};

		SRSDropDownChoice poolDrawDD = new SRSDropDownChoice("value", model,
				poolDrawOptionsList, new SRSAbstractChoiceRenderer<Object>() {

			private static final long serialVersionUID = 3880816096499874020L;

			public Object getDisplayValue(Object value) {
				return (value == null) ? null : value;
			}

			public String getIdValue(Object arg0, int arg1) {
				return arg1 + "";
			}
		}, "Select");
		
		/* Add select behavior */
		poolDrawDD.add(new AjaxFormComponentUpdatingBehavior(
				"change") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				poolDrawType = (String) this.getComponent()
						.getDefaultModelObject();

				if (poolDrawType == null
						|| (poolDrawType != null && poolDrawType == PoolDrawOptionsEnum.PERCENTAGE_OF_BALANCE
								.getValue())) {
					isPercentageOfBalSelected = true;
				} else if (poolDrawType != null
						&& poolDrawType == PoolDrawOptionsEnum.SPECIFIC_RAND_AMOUNT
								.getValue()) {
					isPercentageOfBalSelected = false;
				}

				if (isPercentageOfBalSelected) {

					checkFieldVisibility();
					target.add(getPoolDrawPercentageContainer());
					target.add(getPoolDrawAmountContainer());
//					target.addComponent(getSpecificRandAmtFeedbackPanel()
//							.setVisible(false));

				} else if (!isPercentageOfBalSelected) {
					checkFieldVisibility();
					target.add(getPoolDrawPercentageContainer());
					target.add(getPoolDrawAmountContainer());
//					target.addComponent(getSpecificRandAmtFeedbackPanel()
//							.setVisible(true));
				}
			}
		});
		
		poolDrawDD.setOutputMarkupId(true);
		poolDrawDD.setOutputMarkupPlaceholderTag(true);
		poolDrawDD.setEnabled(getEditState() == EditStateType.MODIFY);
		Label label = new Label("labelValue", new Model("<b>Draw Choice</b>")){
			 
		    @Override
		    protected void onComponentTag(final ComponentTag tag){
		        super.onComponentTag(tag);
		        tag.put("width", "5%");
		    }
		};
		label.setVisible(true);
		label.setEscapeModelStrings(false);
		poolDrawChoicePanel =HelperPanel.getInstance("poolDrawChoice", label,poolDrawDD);
		poolDrawChoicePanel.setOutputMarkupId(true);
		poolDrawChoicePanel.setOutputMarkupPlaceholderTag(true);

		return poolDrawChoicePanel;
	}
	
	private GUIFieldPanel getRequestedPaymentAmountPanel() {

		if (requestedPaymentAmountPanel == null) {

			TextField<CurrencyAmount> field = new TextField<CurrencyAmount>(
					"value", new PropertyModel<CurrencyAmount>(
							panelModel.getAgencyPoolAccountDetailDTO(),
							"requestedPaymentAmount")) {
				private static final long serialVersionUID = 1L;

				@Override
				protected void onComponentTag(ComponentTag tag) {
					super.onComponentTag(tag);
					tag.put("align", "left");
					// tag.put("style",
					// "padding-left: 50px; padding-right: 50px;padding-bottom:15px;");
				}
			};
			field.setOutputMarkupId(true);
			field.setOutputMarkupPlaceholderTag(true);
			// field.add(new AgencyPoolRequestedAmtValidator(panelModel));
			field.add(new AjaxFormComponentUpdatingBehavior("change") {

				private static final long serialVersionUID = 1L;

				@Override
				protected void onUpdate(AjaxRequestTarget target) {
					// Update the Percentage label
					CurrencyAmount reqAmt = panelModel
							.getAgencyPoolAccountDetailDTO()
							.getRequestedPaymentAmount();
					CurrencyAmount poolBal = panelModel
							.getAgencyPoolAccountDetailDTO()
							.getCurrentPoolBalance();
					Percentage percAmt = new Percentage(
							reqAmt.getValue().divide(poolBal.getValue(), 4,
									RoundingMode.HALF_UP));
					
					panelModel.getAgencyPoolAccountDetailDTO()
							.setRequestedPaymentPercentage(percAmt);

					/*
					 * Validation #1: The Requested Payment Amount must be less
					 * than or equal to 50% of the current pool balance
					 */
					// Get the draw limit based on the pool account balance
					BigDecimal drawLimit = percentageValue(panelModel
							.getAgencyPoolAccountDetailDTO()
							.getCurrentPoolBalance().getValue(),
							new BigDecimal(50.00));

					/*
					 * check if the draw amount is less than or equal to the
					 * draw limit.
					 */
					
					ISessionUserProfile sessionUser = SRSAuthWebSession.get().getSessionUser();
					
					if(getGuiController().canUserTransferMaxPoolBalance(sessionUser)){
						//Allow it: Fine can withdraw 100%
					} else if (drawLimit.compareTo(reqAmt.getValue()) < 0 && getEditState() != EditStateType.AUTHORISE) {
						AgencyPoolDrawPanel.this.error("The Requested Payment Amount must be less than or equal to 50% of the current pool balance - the value captured exceeds this limit.");
						// target.addComponent(getSpecificRandAmtFeedbackPanel());
						target.add(getFeedBackPanel());
					}
					target.add(getRequestedPaymentPercentagePanel());
				}
			});

			requestedPaymentAmountPanel = createGUIFieldPanel(
					"Requested Payment Amount", "Requested Payment Amount",
					"requestedPaymentAmount",
					HelperPanel.getInstance("panel", field));

			requestedPaymentAmountPanel.setOutputMarkupId(true);
			requestedPaymentAmountPanel.setOutputMarkupPlaceholderTag(true);
		}
		return requestedPaymentAmountPanel;
	}

	public WebMarkupContainer getPoolDrawPercentageContainer() {
		if (poolDrawPercentageContainer == null) {
			poolDrawPercentageContainer = new WebMarkupContainer(
					"poolDrawPercentageContainer");
			poolDrawPercentageContainer.add(getPoolDrawRateDerivedPanel());
			poolDrawPercentageContainer.add(getPoolDrawRateSelectedPanel());
			poolDrawPercentageContainer.add(getCurrentPoolBalancePanel());
			poolDrawPercentageContainer.add(getExpectedPaymentPanel());
			// poolDrawPercentageContainer.add(getExpectedPaymentHelpPanel());

			poolDrawPercentageContainer.setOutputMarkupId(true);
			poolDrawPercentageContainer.setOutputMarkupPlaceholderTag(true);

		}
		return poolDrawPercentageContainer;
	}

	public WebMarkupContainer getPoolDrawAmountContainer() {
		if (poolDrawAmountContainer == null) {
			poolDrawAmountContainer = new WebMarkupContainer(
					"poolDrawAmountContainer");

			poolDrawAmountContainer.add(getCurrentPoolBalanceRandAmtPanel());

			poolDrawAmountContainer.add(getRequestedPaymentAmountPanel());
			poolDrawAmountContainer.add(getRequestedPaymentPercentagePanel());

			poolDrawAmountContainer.setOutputMarkupId(true);
			poolDrawAmountContainer.setOutputMarkupPlaceholderTag(true);
		}
		return poolDrawAmountContainer;
	}

	private GUIFieldPanel getCurrentPoolBalancePanel() {
		if (currentPoolBalancePanel == null) {
			currentPoolBalancePanel = createGUIFieldPanel(
					"Current Pool Balance", "Current Pool Balance",
					"currentPoolBalance", getCurrentPoolBalanceInstance());

			currentPoolBalancePanel.setOutputMarkupId(true);
			currentPoolBalancePanel.setOutputMarkupPlaceholderTag(true);
		}
		return currentPoolBalancePanel;
	}
	
	private GUIFieldPanel getCurrentPoolBalanceRandAmtPanel() {
		if (currentPoolBalanceRandAmtPanel == null) {
			currentPoolBalanceRandAmtPanel = createGUIFieldPanel(
					"Current Pool Balance", "Current Pool Balance",
					"currentPoolBalanceRandAmt",
					getCurrentPoolBalanceInstance());

			currentPoolBalanceRandAmtPanel.setOutputMarkupId(true);
			currentPoolBalanceRandAmtPanel.setOutputMarkupPlaceholderTag(true);
		}
		return currentPoolBalanceRandAmtPanel;
	}
	
	private HelperPanel getCurrentPoolBalanceInstance() {
		return HelperPanel.getInstance("panel",
				new Label("value", new PropertyModel<Object>(
						panelModel.getAgencyPoolAccountDetailDTO(),
						"currentPoolBalance")));
	}

	/**
	 * Panel to display POOL_DRAW_RATE_DERIVED label
	 * 
	 * @return
	 */
	private GUIFieldPanel getPoolDrawRateDerivedPanel() {
		if (poolDrawRateDerivedPanel == null) {

			poolDrawRateDerivedPanel = createGUIFieldPanel(
					"Pool Draw Rate Derived (maximum)",
					"Pool Draw Rate Derived (maximum)", "poolDrawRateDerived",
					HelperPanel.getInstance("panel",
							new Label("value", new PropertyModel<Object>(
									panelModel.getAgencyPoolAccountDetailDTO(),
									"poolDrawRateDerived"))));
			poolDrawRateDerivedPanel
					.setVisible(isPercentageOfBalSelected ? true : false);
			poolDrawRateDerivedPanel.setOutputMarkupId(true);
			poolDrawRateDerivedPanel.setOutputMarkupPlaceholderTag(true);
		}
		return poolDrawRateDerivedPanel;
	}

	/**
	 * 
	 * @return
	 */
	private GUIFieldPanel getExpectedPaymentPanel() {
		if (expectedPaymentPanel == null) {

			Label label = new Label("value", new PropertyModel<Object>(
					panelModel.getAgencyPoolAccountDetailDTO(),
					"expectedPayment"));

			ContextImage image = new ContextImage("img",
					"/SRSAppWeb/images/question.png");
			image.add(new AttributeModifier("title", new Model(this
					.getString("tooltip.expectedPayment"))));
			image.add(new AttributeModifier("align", "center"));

			expectedPaymentPanel = createGUIFieldPanel("Expected Payment",
					"Expected Payment", "expectedPayment",
					HelperPanel.getInstance("panel", label, image));

			// HelperPanel.getInstance(componentId, (AbstractLink)but, image);

			expectedPaymentPanel.setOutputMarkupId(true);
			expectedPaymentPanel.setOutputMarkupPlaceholderTag(true);
			// expectedPaymentPanel.add(getExpectedPaymentHelpPanel());;
		}
		return expectedPaymentPanel;
	}

	/**
	 * 
	 * @return
	 */
	private Image getExpectedPaymentHelpPanel() {
		if (expectedPaymentPanelHelp == null) {
			expectedPaymentPanelHelp = new Image("expectedPaymentHelp") {
				private static final long serialVersionUID = 1L;

				protected void onComponentTag(final ComponentTag tag) {
					checkComponentTag(tag, "img");
					tag.put("src", "/SRSAppWeb/images/untitled.png");
				}
			};

			expectedPaymentPanelHelp.setOutputMarkupId(true);
			expectedPaymentPanelHelp.setOutputMarkupPlaceholderTag(true);
			expectedPaymentPanelHelp.add(new AttributeModifier("title", 
					new Model(this.getString("tooltip.expectedPayment"))));
			;
		}
		return expectedPaymentPanelHelp;
	}

	public GUIFieldPanel getRequestedPaymentPercentagePanel() {
		if (requestedPaymentPercentagePanel == null) {
			
			ContextImage image = new ContextImage("img",
					"/SRSAppWeb/images/question.png");
			image.add(new AttributeModifier("title", new Model(this
					.getString("tooltip.requestedPayment"))));
			image.add(new AttributeModifier("align", "center"));
			

			requestedPaymentPercentagePanel = createGUIFieldPanel(
					"Requested Payment Percentage",
					"Requested Payment Percentage",
					"requestedPaymentPercentage", HelperPanel.getInstance(
							"panel",
							new Label("value", new PropertyModel<Object>(
									panelModel.getAgencyPoolAccountDetailDTO(),
									"requestedPaymentPercentage")),image));
			requestedPaymentPercentagePanel.setOutputMarkupId(true);
			requestedPaymentPercentagePanel.setOutputMarkupPlaceholderTag(true);
		}
		return requestedPaymentPercentagePanel;
	}
	
//	private FeedbackPanel getSpecificRandAmtFeedbackPanel() {
//		if (specificRandAmtFeedbackPanel == null) {
//			specificRandAmtFeedbackPanel = new FeedbackPanel("specificRandAmtFeedback");
//		}
//		specificRandAmtFeedbackPanel.setEnabled(true);
//		specificRandAmtFeedbackPanel.setVisible(true);
//		specificRandAmtFeedbackPanel.setOutputMarkupId(true);
//
//		specificRandAmtFeedbackPanel.setFilter(new ContainerFeedbackMessageFilter(this));
//
//		return specificRandAmtFeedbackPanel;
//	}
	
	@Override
	protected void checkFieldVisibility() {
		super.checkFieldVisibility();
		getPoolDrawToChoicePanel().setVisible(true);
		if (isPercentageOfBalSelected) {
			getPoolDrawPercentageContainer().setVisible(true);
			getPoolDrawAmountContainer().setVisible(false);
		} else {
			getPoolDrawPercentageContainer().setVisible(false);
			getPoolDrawAmountContainer().setVisible(true);
		}
	}
	
	private BigDecimal getEarningValue() {
		/*
		 * Calculate if the agreement's Pool Balance is greater than or equal to
		 * 20% of the previous year's monetary DPE earnings
		 */

		// Get 20% of last year's DPE Earnings
		CurrencyAmount earnings = panelModel.getAgencyPoolAccountDetailDTO()
				.getLastyearDPEEarnings();
		if (earnings == null) {
			return null;
		}
		BigDecimal earningValue = percentage(earnings.getValue(),
				new BigDecimal(20)).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);

		return earningValue;
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

	public BigDecimal percentage(BigDecimal base, BigDecimal pct) {
		return base.multiply(pct);
		// base.multiply(pct).divide(new BigDecimal(100))
	}
	
	public BigDecimal percentageValue(BigDecimal base, BigDecimal pct){
	    return base.multiply(pct).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);
	}

	@SuppressWarnings("unused")
	private AgencyPoolAccountDetailsPanelModel getModel() {
		if (panelModel == null) {
			panelModel = new AgencyPoolAccountDetailsPanelModel();
		}
		return panelModel;
	}

	public String getPoolDrawType() {
		return poolDrawType;
	}

	public void setPoolDrawType(String poolDrawType) {
		this.poolDrawType = poolDrawType;
	}

	/**
	 * @return the outstandingRequest
	 */
	public boolean isOutstandingRequest() {
		return outstandingRequest;
	}

	/**
	 * @param outstandingRequest
	 *            the outstandingRequest to set
	 */
	public void setOutstandingRequest(boolean outstandingRequest) {
		this.outstandingRequest = outstandingRequest;
	}

	public String getInfoMessage() {
		return infoMessage;
	}

	public void setInfoMessage(String infoMessage) {
		this.infoMessage = infoMessage;
	}
	
	/**
	 * Load the AgreementGUIController dynamically if it is null as this is a transient variable.
	 * @return {@link IAgreementGUIController}
	 */
	protected IAgreementGUIController getGuiController() {
		if (guiController==null) {
			try {
				guiController = ServiceLocator.lookupService(IAgreementGUIController.class);
			} catch (NamingException e) {
				logger.fatal("Could not lookup AgreementGUIController",e);
				throw new CommunicationException("Could not lookup AgreementGUIController",e);
			}
		}
		return guiController;
	}
	
}
