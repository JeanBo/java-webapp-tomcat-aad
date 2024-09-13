package za.co.liberty.web.pages.loans;

import org.apache.wicket.markup.repeater.RepeatingView;

import za.co.liberty.dto.loan.LoanQuoteDTO;

/**
 * Web page that allows a user to create a Quote for a loan.
 * 
 * @author JZB0608
 * 
 */
public abstract class Quote extends BaseLoanPage {

	public Quote(LoanQuoteDTO loanQuoteDTO) {
		super(loanQuoteDTO);
	}

	@Override
	public RepeatingView getUpperRepeaterField() {
		RepeatingView view = super.getUpperRepeaterField();
		
		/* Show loan id for view and edit */
		if (isView() || (bean.getOid()!=null && bean.getOid() > 0)) {
			view.add(new LoanRepeatingPanel("LoanQuoteEdit.1", "Loan Id:", bean
				.getOid().toString(), true, !isView()));
		}
		return view;
	}
}
