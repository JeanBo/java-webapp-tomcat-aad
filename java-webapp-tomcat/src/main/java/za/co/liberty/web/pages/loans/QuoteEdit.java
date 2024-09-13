package za.co.liberty.web.pages.loans;

import java.math.BigDecimal;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.validation.IValidationError;
import org.apache.wicket.validation.ValidationError;


import za.co.liberty.dto.loan.LoanQuoteDTO;
import za.co.liberty.web.pages.panels.ButtonHelperPanel;

/**
 * Loan Quote page for edits
 * 
 * @author JZB0608 - 09 Apr 2008
 * 
 */
public class QuoteEdit extends Quote {

	private static final long serialVersionUID = 5498038611022069760L;

	protected Button submitButtonComp;

	protected Button calculateButtonComp;

	/**
	 * Default constructor initialises a new bean with {@link #initDefaultBean()}
	 *
	 */
	public QuoteEdit() {
		this(initDefaultBean());
	}
	
	/**
	 * Edit the passed loan
	 * 
	 * @param loanQuoteDTO
	 */
	public QuoteEdit(LoanQuoteDTO loanQuoteDTO) {
		super(loanQuoteDTO);
	}

	/**
	 * Initialse a default bean for this page
	 * 
	 * @return
	 */
	private static LoanQuoteDTO initDefaultBean() {
		LoanQuoteDTO obj = new LoanQuoteDTO();
		obj.setOid(0);
		obj.setInterestDifferential(new BigDecimal(0));
		obj.setInterestRateDetermination(null);
		obj.setLoanAmount(new BigDecimal(0));
		obj.setPaymentAmount(new BigDecimal(0));
		obj.setTermMonths(new Integer(0));
		return obj;
	}

	@Override
	public boolean isView() {
		return false;
	}

	protected Button getSubmitButton() {
		/* The submit button */
		Button buttonObj = new Button("button1") {
			private static final long serialVersionUID = 6210466543466939281L;

			@Override
			protected void onComponentTag(ComponentTag tag) {
				super.onComponentTag(tag);
				tag.getAttributes().put("value", "Save Quote");
				tag.getAttributes().put("type", "submit");
			}

			@Override
			public void onSubmit() {
				bean.setOid((int) (Math.random() * 1000));
				QuoteView page = new QuoteView((LoanQuoteDTO) bean);
				setResponsePage(page);
			}
		};
		buttonObj.setOutputMarkupId(true);
		buttonObj.setEnabled(false);
		return buttonObj;
	}

	protected Button getCalculateButton() {
		/* The calculate button */
		Button buttonObj = new Button("button2") {
			private static final long serialVersionUID = -669885354868892015L;
		
			@Override
			protected void onComponentTag(ComponentTag tag) {
				super.onComponentTag(tag);
				tag.getAttributes().put("value", "Calculate");
				tag.getAttributes().put("type", "submit");
			}

			@Override
			public void onSubmit() {
				bean.calcInterestRateTotal();

				/* some last minute validation */
				boolean isValid = true;
				if (bean.getInterestRateTotal().doubleValue() <= 0) {
					IValidationError err = new ValidationError().addKey(
							"NumberValidator.minimum").setVariable("minimum",
							"1%").setVariable("label", "Interest Rate Total");
					this.error(err);
					isValid = false;
				}
				if (isValid == false) {
					return;
				}
				submitButtonComp.setEnabled(true);

				/* calculate value */
				if (QuoteEdit.this.isCalculateAmount) {
					double amount = calculatePaymentAmount(bean.getLoanAmount()
							.doubleValue(), bean.getInterestRateTotal()
							.doubleValue() / 100 / 12, bean.getTermMonths());
					bean.setPaymentAmount(new BigDecimal(amount));
				} else {
					double term = calculatePeriod(bean.getLoanAmount()
							.doubleValue(), bean.getInterestRateTotal()
							.doubleValue() / 100 / 12, bean.getPaymentAmount()
							.doubleValue());
					bean.setTermMonths((int) term);
				}

			}

		};
		buttonObj.setOutputMarkupId(true);
		buttonObj.setEnabled(true);
		return buttonObj;
	}

	@Override
	public String getPageName() {
		return "Edit Quote";
	}

	@Override
	public void doUpdateFields(AjaxRequestTarget target) {
		submitButtonComp.setEnabled(false);
		target.add(submitButtonComp);
	}

	@Override
	protected Panel getButtonPanel() {
		submitButtonComp = getSubmitButton();
		calculateButtonComp = getCalculateButton();

		Panel panel = ButtonHelperPanel.getInstance("buttonPanel",
				submitButtonComp, calculateButtonComp);
		panel.setOutputMarkupId(true);
		return panel;
	}
	
}
