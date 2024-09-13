package za.co.liberty.web.pages.loans;

import java.math.BigDecimal;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.RepeatingView;

import za.co.liberty.dto.loan.InterestRateType;
import za.co.liberty.dto.loan.LoanQuoteDTO;
import za.co.liberty.web.pages.panels.ButtonHelperPanel;


/**
 * Loan Quote page for views
 * 
 * @author JZB0608 - 09 Apr 2008
 *
 */
public class QuoteView extends Quote {

	private static final long serialVersionUID = -5559244907175119492L;

	private Button printButton;
	
	public QuoteView() {
		this(testBean());
	}
	
	public QuoteView(LoanQuoteDTO bean) {
		super(bean);
	}
	
	/**
	 * Create a test bean for view
	 * 
	 * @return
	 */
	protected static LoanQuoteDTO testBean() {
		LoanQuoteDTO bean = new LoanQuoteDTO();
		bean.setOid(101);
		bean.setInterestDifferential(new BigDecimal(12));
		bean.setInterestRateDetermination(InterestRateType.FIXED);
		bean.setLoanAmount(new BigDecimal(100000));
		bean.setPaymentAmount(new BigDecimal(2600));
		bean.setTermMonths(new Integer(48));	
		bean.calcInterestRateTotal();
		return bean;
	}
	
	@Override
	public boolean isView() {
		return true;
	}

	protected Button getPrintButton() {
		Button but = new Button("button1") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onComponentTag(ComponentTag tag) {
				super.onComponentTag(tag);
				tag.put("value", "Print");
				tag.put("type", "submit");
			}
			
		};
		but.setEnabled(false);
		return but;
	}
	
	@Override 
	public String getPageName() {
		return "View Quote";
	}

	@Override
	public void doUpdateFields(AjaxRequestTarget target) {
//		submitButtonComp.setEnabled(false);
//		target.addComponent(submitButtonComp);
	}

	@Override
	protected Panel getButtonPanel() {
		printButton = getPrintButton();
		return ButtonHelperPanel.getInstance("buttonPanel", printButton);
	}
	
}
