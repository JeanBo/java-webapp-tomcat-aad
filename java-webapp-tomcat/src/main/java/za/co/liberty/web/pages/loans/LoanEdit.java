package za.co.liberty.web.pages.loans;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.validation.IValidationError;
import org.apache.wicket.validation.ValidationError;

import za.co.liberty.dto.loan.LoanDTO;
import za.co.liberty.dto.loan.LoanQuoteDTO;
import za.co.liberty.web.pages.panels.ButtonHelperPanel;

/**
 * Edit a loan page
 * 
 * @author JZB0608 - 17 Apr 2008
 *
 */
public class LoanEdit extends BaseLoanPage {
	
	private static final long serialVersionUID = -926046265850604740L;
	
	protected Button submitButtonComp;
	protected Button calculateButtonComp;
	
	/**
	 * Default constructor initialises a new bean with {@link #initDefaultBean()}
	 *
	 */
	public LoanEdit() {
		this(initDefaultBean());
	}
	
	/**
	 * Initialises page with given DTO
	 * 
	 * @param loanDTO
	 */
	public LoanEdit(LoanDTO loanDTO) {
		super(loanDTO);
	}
	
	/**
	 * Constructs a new page based on a Quote 
	 * 
	 * @param loanDTO
	 */
	public LoanEdit(LoanQuoteDTO quoteBean) {
		this(createLoanFromQuote(quoteBean));
		
	}
	
	/**
	 * Creates a Loan DTO from a Quote DTO
	 * @param quoteBean
	 * @return
	 */
	private static LoanDTO createLoanFromQuote(LoanQuoteDTO quoteBean) {
		LoanDTO loanBean = new LoanDTO();
		Integer quoteId = quoteBean.getOid();
		try {
			BeanUtils.copyProperties(loanBean, quoteBean);
			loanBean.setLinkedQuoteOid(quoteId);
		} catch (IllegalAccessException e) {
			throw new IllegalArgumentException("Unable to convert quote to loan");
		} catch (InvocationTargetException e) {
			throw new IllegalArgumentException("Unable to convert quote to loan");
		}
		return loanBean;
	}
	
	/**
	 * Initialse a default bean for this page
	 * 
	 * @return
	 */
	private static LoanDTO initDefaultBean() {
		LoanDTO obj = new LoanDTO();
		obj.setOid(0);
		obj.setInterestDifferential(new BigDecimal(0));
		obj.setInterestRateDetermination(null);
		obj.setLoanAmount(new BigDecimal(0));
		obj.setPaymentAmount(new BigDecimal(0));
		obj.setTermMonths(new Integer(0));
		return obj;
	}
	
	protected Button getSubmitButton() {
		/* The submit button */
		Button buttonObj = new Button("button1") {
			private static final long serialVersionUID = 6210466543466939281L;

			@Override
			protected void onComponentTag(ComponentTag tag) {
				super.onComponentTag(tag);
				tag.getAttributes().put("value", "Save Loan");
				tag.getAttributes().put("type", "submit");
			}

			@Override
			public void onSubmit() {
				bean.setOid((int) (Math.random() * 1000));
				setResponsePage(new LoanView((LoanDTO) bean));
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
					ValidationError err = new ValidationError().addKey("NumberValidator.minimum");
					err.setVariable("minimum","1%");
					err.setVariable("label", "Interest Rate Total");
					this.error(err);
					isValid = false;
				}
				if (isValid == false) {
					return;
				}
				submitButtonComp.setEnabled(true);

				/* calculate value */
				if (LoanEdit.this.isCalculateAmount) {
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
		return "Add Loan";
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

	@Override
	public boolean isView() {
		return false;
	}

	@Override
	public RepeatingView getUpperRepeaterField() {
		RepeatingView view = super.getUpperRepeaterField();
			
		/* Add a loan quote id if set */
		if (((LoanDTO)bean).getLinkedQuoteOid() != null 
				&& ((LoanDTO)bean).getLinkedQuoteOid() > 0) {
			view.add(new LoanRepeatingPanel("LoanEdit.1", "Linked Quote Id:",  
					((LoanDTO)bean).getLinkedQuoteOid().toString(), 
					true, true));
		}
		
		return view;
	}
}
