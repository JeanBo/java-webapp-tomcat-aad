package za.co.liberty.web.pages.loans;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.RepeatingView;

import za.co.liberty.dto.loan.LoanDTO;
import za.co.liberty.dto.loan.LoanQuoteDTO;
import za.co.liberty.web.pages.panels.ButtonHelperPanel;

public class LoanView extends BaseLoanPage {

	private Button printButton;
	
	/**
	 * Default constructor
	 * 
	 * @param bean
	 */
	public LoanView(LoanDTO bean) {
		super(bean);
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
		return "View Loan";
	}

	@Override
	public void doUpdateFields(AjaxRequestTarget target) {
	}

	@Override
	protected Panel getButtonPanel() {
		printButton = getPrintButton();
		return ButtonHelperPanel.getInstance("buttonPanel", printButton);
	}

	@Override
	public RepeatingView getUpperRepeaterField() {
		RepeatingView view = super.getUpperRepeaterField();
		
		view.add(new LoanRepeatingPanel("LoanEdit.1", "Loan Id:",  
				bean.getOid().toString(), true, false));
		
		/* Add a loan quote id if set */
		if (((LoanDTO)bean).getLinkedQuoteOid() != null 
				&& ((LoanDTO)bean).getLinkedQuoteOid() > 0) {
			view.add(new LoanRepeatingPanel("LoanEdit.2", "Linked Quote Id:",  
					((LoanDTO)bean).getLinkedQuoteOid().toString(), 
					true, false));
		}
		
		return view;
	}
}
