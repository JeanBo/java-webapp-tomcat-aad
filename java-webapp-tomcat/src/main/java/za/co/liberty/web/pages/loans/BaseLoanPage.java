package za.co.liberty.web.pages.loans;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.convert.IConverter;
import org.apache.wicket.util.convert.converter.AbstractDecimalConverter;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidationError;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.ValidationError;
//import org.apache.wicket.validation.validator.AbstractValidator;
//import org.apache.wicket.validation.validator.NumberValidator;

import za.co.liberty.dto.loan.InterestRateType;
import za.co.liberty.dto.loan.LoanBaseDTO;
import za.co.liberty.helpers.converters.values.NumberValueConverter;
import za.co.liberty.web.pages.BasePage;
import za.co.liberty.web.pages.panels.HelperPanel;
import za.co.liberty.web.wicket.convert.converters.DecimalConverter;
import za.co.liberty.web.wicket.markup.html.form.CurrencyLabel;
import za.co.liberty.web.wicket.markup.html.form.CurrencyTextField;

public abstract class BaseLoanPage extends BasePage {

	private static final long serialVersionUID = 2084579871883543781L;

	public static final DecimalFormat PERCENTAGE_FORMAT = new DecimalFormat(
			"##0.00'%'");

	/* Define converter and formats */
	public static final DecimalConverter DECIMAL_CONVERTER = new DecimalConverter(2);
	
	
	/* Page components */
	protected Component loanAmountComp;

	protected Component interestRateDeterminationComp;

	protected Component termComp;

	protected Component paymentAmountComp;

	protected Component interestDiffComp;

	protected Label interestRateTotalComp;

//	protected Button submitButtonComp;
//
//	protected Button calculateButtonComp;

	protected Panel buttonPanelComp;
	protected RepeatingView upperRepeaterComp;

	/* Other attributes */
	protected LoanBaseDTO bean;
	protected boolean isCalculateAmount;
	
	protected AbstractDecimalConverter interestConverter;

	/**
	 * Initialise page with given bean.
	 * 
	 * @param bean
	 */
	public BaseLoanPage(LoanBaseDTO bean) {
		this.bean = bean;
		initialisePage();
		add(new LoanQuoteForm("loanQuoteForm"));
	}

	/**
	 * Initialise other page attributes etc.
	 * 
	 */
	private void initialisePage() {
		/* Interest Rate Converter */
		interestConverter = new AbstractDecimalConverter<BigDecimal>() {
			private static final long serialVersionUID = 1L;

			@Override
			protected Class getTargetType() {
				return BigDecimal.class;
			}

			public BigDecimal convertToObject(String value, Locale locale) {
				return null;
			}

			@Override
			public String convertToString(BigDecimal value, Locale locale) {
				if (value == null || !(value instanceof BigDecimal)) {
					return "";
				}
				return PERCENTAGE_FORMAT.format(value);
			}

		};
	}

	/**
	 * The Loan form definition
	 * 
	 */
	public class LoanQuoteForm extends Form {

		private static final long serialVersionUID = -1588576977700727870L;

		/**
		 * Default constructor
		 * 
		 * @param id
		 * @param isView
		 */
		public LoanQuoteForm(String id) {
			super(id);
			
			isCalculateAmount = true;
			
			HelperPanel panel;
			
			// Upper panel
			add(upperRepeaterComp = getUpperRepeaterField());
			
			// LoanAmount
			panel = getLoanAmountField();
			loanAmountComp = panel.getEnclosedObject();
			add(panel);
			
			// Interest rate determination
			panel = getInterestRateDeterminationField();
			interestRateDeterminationComp = panel.getEnclosedObject();
			add(panel);
			
			// Term
			panel = getTermField();
			termComp = panel.getEnclosedObject();
			add(panel);
			
			// Payment amount
			panel = getPaymentAmountField();
			paymentAmountComp = panel.getEnclosedObject();
			add(panel);
			
			// Interest rate total comp
			add(interestRateTotalComp = (Label) getInterestRateTotalField());
			
			// Interest Differential
			panel = getInterestDiffField();
			interestDiffComp = panel.getEnclosedObject();
			add(panel);

			add(new Label("indicator1", new Model(getIndicator())));
			add(new Label("indicator2", new Model(getIndicator())));
			add(new Label("indicatorMessage", new Model(getIndicatorMessage())));
//			Application.get().getDebugSettings().setComponentUseCheck(false);
			
			/* Buttons */
			add(buttonPanelComp = getButtonPanel());
//			add(submitButtonComp = getButton1());
//			add(calculateButtonComp = getButton2());
		}
	}

	@Override
	public String getPageName() {
		return "Loan Quote";
	}

	public String getIndicator() {
		return "*";
	}
	
	public String getIndicatorMessage() {
		return getIndicator() + " Will be calculated when background is grey";
	}
	
	/**
	 * Indicates if this is a view or edit
	 * 
	 * @return
	 */
	public abstract boolean isView();

	/**
	 * Retrieve button panel
	 * 
	 * @return
	 */
	protected abstract Panel getButtonPanel();
	

	/**
	 * Return the upper panel
	 * 
	 * @return
	 */
	protected RepeatingView getUpperRepeaterField() {
		RepeatingView obj = new RepeatingView("upperRepeater");
		return obj;
	}

	/**
	 * Get the interest differential field
	 * 
	 * @return
	 */
	protected HelperPanel getInterestDiffField() {
		

		if (isView()) {
			return HelperPanel.getInstance("interestDiff", new Label("value",
					new PropertyModel(bean, "interestDifferential")) {
				private static final long serialVersionUID = 795725527990723245L;

				@Override
				public IConverter getConverter(Class type) {
					return DECIMAL_CONVERTER;
				}
			},true);
		}

		/* Define field */
		TextField interestDiffObj = new TextField("value",
				new PropertyModel(bean, "interestDifferential")) {

			private static final long serialVersionUID = 795725527990723241L;

			@Override
			public IConverter getConverter(Class type) {
				return DECIMAL_CONVERTER;
			}

		};
		interestDiffObj.add(new ResetCalculateBehavior("change"));
		interestDiffObj.setLabel(new Model("Interest Difference"));
		interestDiffObj.setOutputMarkupId(true);

		return HelperPanel.getInstance("interestDiff", interestDiffObj);
	}

	/**
	 * Instantiate the term field
	 * 
	 * @return
	 */
	protected HelperPanel getTermField() {

		if (isView()) {
			return HelperPanel.getInstance("term", 
					new Label("value", new PropertyModel(bean, "termMonths")),true);
		}
		TextField termObj = new TextField("value", new PropertyModel(bean,
				"termMonths")) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onComponentTag(ComponentTag tag) {
				super.onComponentTag(tag);
				String value = (String) tag.getAttributes().get("class");
				value = (value==null) ? "" : value;

				/* Change tag style to indicate calculated field */
				if (isCalculateAmount) {
					if (value.contains("calculate")) {
						value = value.replace("calculate", "");
						tag.put("class", value);
					}

				} else {
					if (value.contains("calculate") == false) {
						value += " calculate";
						tag.put("class", value);
					}
				}

			}

		};
		termObj.setLabel(new Model("Term"));

		/* Select calculate field behavior */
		termObj.add(new AjaxFormComponentUpdatingBehavior("change") {
			private static final long serialVersionUID = 0L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				doUpdateFields(target);

				if (isCalculateAmount == false) {
					bean.setPaymentAmount(BigDecimal.ZERO);
					isCalculateAmount=true;

					target.add(termComp);
					target.add(paymentAmountComp);
				}
			}

		});
		termObj.add(new IValidator() {

			private static final long serialVersionUID = 1L;

			public void validate(IValidatable val) {
				Integer myVal = (Integer) val.getValue();
				if (isCalculateAmount
						&& ( myVal <= 0 || myVal > 48)) {
					ValidationError err = new ValidationError().addKey(
							"NumberValidator.range")
							.setVariable("minimum", "1").setVariable("maximum",
									"48");
					val.error(err);
//					isValid = false;
				}
				
			}
			
		});
		termObj.setOutputMarkupId(true);
		return HelperPanel.getInstance("term", termObj);
	}

	/**
	 * Instantiate the payment amount field
	 * 
	 * @return
	 */
	protected HelperPanel getPaymentAmountField() {
		if (isView()) {
			return HelperPanel.getInstance("paymentAmount", 
					new CurrencyLabel("value",
							new PropertyModel(bean, "paymentAmount")),true);
		}
		
		CurrencyTextField paymentAmountObj = new CurrencyTextField(
				"value", new PropertyModel(bean, "paymentAmount")) {

			private static final long serialVersionUID = 1L;

			@Override
			protected void onComponentTag(ComponentTag tag) {
				super.onComponentTag(tag);

				/* Change tag style to indicate calculated field */
				if (isCalculateAmount) {
					tag.put("class", "number smallCol calculate");
				} else {
					tag.put("class", "number smallCol");
				}
			}

		};
		paymentAmountObj.setLabel(new Model("Payment Amount"));
		paymentAmountObj.setOutputMarkupId(true);

		/* Select calculate field behavior */
		paymentAmountObj.add(new AjaxFormComponentUpdatingBehavior("change") {
			private static final long serialVersionUID = 0L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				doUpdateFields(target);

				if (isCalculateAmount) {
					bean.setTermMonths(0);
					isCalculateAmount=false;

					target.add(termComp);
					target.add(paymentAmountComp);
				}
			}

		});

		paymentAmountObj.add(new IValidator() {

			private static final long serialVersionUID = 1L;

			public void validate(IValidatable val) {
				BigDecimal valObj = (BigDecimal) val.getValue();
				if (isCalculateAmount == false
						&& valObj.doubleValue() <= 0) {
					ValidationError err = new ValidationError()
							.addKey("Required");
					val.error(err);
				}
			}
			
		});
		
		return HelperPanel.getInstance("paymentAmount", paymentAmountObj);
	}

	/**
	 * Field values have updated, disable submit
	 * 
	 * @param target
	 */
	public abstract void doUpdateFields(AjaxRequestTarget target);

	/**
	 * Instantiate interest rate determination field
	 * 
	 * @return
	 */
	protected HelperPanel getInterestRateDeterminationField() {
		if (isView()) {
			return HelperPanel.getInstance("interestRateDetermination",
					new Label("value",new PropertyModel(bean,
						"interestRateDetermination")),true);
		}
	
		DropDownChoice<InterestRateType> interestRateDetObj = new DropDownChoice<InterestRateType>(
				"value", new PropertyModel<InterestRateType>(bean,
						"interestRateDetermination"), Arrays.asList(InterestRateType.values()),
				new IChoiceRenderer<InterestRateType>() {

					private static final long serialVersionUID = -4395497872848286816L;

					public Object getDisplayValue(InterestRateType arg0) {
						getLogger().info("Interest.GetDisplay " + arg0);
						return arg0.toString();
					}

					public String getIdValue(InterestRateType arg0, int arg1) {
						getLogger().info("Interest.getIdValue " + arg1 + " - " + arg0);
						return "" + ((InterestRateType) arg0).getId();
					}

					@Override
					public InterestRateType getObject(String arg0,
							IModel<? extends List<? extends InterestRateType>> arg1) {
						getLogger().info("Interest.getObject " + arg0 + " = "+ arg1);
						for (InterestRateType t : arg1.getObject()) {
							if (t.getId()==Integer.parseInt(arg0)) {
								return t;
							}
						}
						return null;
					}

				});
		interestRateDetObj.setRequired(true);
		interestRateDetObj.setLabel(new Model<String>("Interest Rate Determination"));
		interestRateDetObj.setOutputMarkupId(true);

		/* Behaviour - update interest rate total */
		interestRateDetObj.add(new ResetCalculateBehavior("change"));

		/* Validation */
		interestRateDetObj.add(new IValidator<InterestRateType>() {

			@Override
			public void validate(IValidatable<InterestRateType> validatable) {
				if (validatable.getValue() == null) { 
					validatable.error(new ValidationError().addKey("Required"));
				}

				
			}

			

		});
		return HelperPanel.getInstance("interestRateDetermination", 
				interestRateDetObj);
	}

	/**
	 * Instantiate the Loan Amount Field
	 * 
	 * @return
	 */
	protected HelperPanel getLoanAmountField() {
		if (isView()) {
			return HelperPanel.getInstance("loanAmount", new CurrencyLabel(
					"value", new PropertyModel(bean, "loanAmount")),true);
		}

		CurrencyTextField loanAmountObj = new CurrencyTextField("value",
				new PropertyModel(bean, "loanAmount"));
		loanAmountObj.setLabel(new Model("Loan Amount"));
		loanAmountObj.setRequired(true);
		loanAmountObj.add(new ResetCalculateBehavior("change"));

//		loanAmountObj.add(new IValidator<Currency>);
		return HelperPanel.getInstance("loanAmount", loanAmountObj);
	}

	/**
	 * Instantiate the interest rate total field
	 * 
	 * @return
	 */
	protected Component getInterestRateTotalField() {
		Label label = new Label("interestTotal", new PropertyModel(bean,
				"interestRateTotal")) {

			private static final long serialVersionUID = -4487409669214210466L;

			@Override
			public IConverter getConverter(Class type) {
				return interestConverter;
			}

		};
		label.setOutputMarkupId(true);
		return label;
	}
	
	/**
	 * Calculate period for loan
	 * 
	 * @param p
	 * @param i
	 * @param a
	 * @return
	 */
	public double calculatePeriod(double p, double i, double a) {
		double n = -1 * (Math.log(1 - ((i * p) / a)) / Math.log(1 + i));
		return n;
	}

	/**
	 * Calculate payment amount for loan
	 * 
	 * @param p
	 * @param i
	 * @param n
	 * @return
	 */
	public double calculatePaymentAmount(double p, double i, double n) {
		double a = (p * i) / (1.0 - Math.pow((1.0 + i), -n));
		return a;
	}

	/**
	 * Set field that is being calculated
	 * 
	 * @author JZB0608 - 09 Apr 2008
	 * 
	 */
	public class ResetCalculateBehavior extends
			AjaxFormComponentUpdatingBehavior {

		private static final long serialVersionUID = -7704717370530037686L;

		public ResetCalculateBehavior(String event) {
			super(event);
		}

		@Override
		protected void onUpdate(AjaxRequestTarget target) {
			bean.calcInterestRateTotal();
			target.add(interestRateTotalComp);
			target.add(interestDiffComp);
			doUpdateFields(target);
		}

	}

}