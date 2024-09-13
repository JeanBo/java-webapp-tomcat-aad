package za.co.liberty.web.pages.loans;

import java.util.Calendar;
import java.util.Date;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import za.co.liberty.web.pages.BasePage;
import za.co.liberty.web.pages.Home;

public class SettleLoanPage extends BasePage 
{
	private static String pageName = "Settle Loan";

	
	public SettleLoanPage()
	{
		//get the agreement id
		//call the busness logic to get the currentAccountBalance - using the agreementNo
		//call the business logic to get the loan balance - using the agreementNo
		add(new SettleLoanForm("settleLoanForm"));
	}
	
	private class SettleLoanForm extends Form
	{
		Date date = Calendar.getInstance().getTime();
		private String settlementDate = date.getDate() + "/" + (date.getMonth() + 1) + "/" + (date.getYear() + 1900);
		private String currentAccountBalance = "R50 000";
		private String loanBalance = "R20 000";
		private long requestOid = 0;
		
		
		public SettleLoanForm(String id) 
		{
			super(id);
			final Label requestLabel =  new Label("requestLabel", new Model("Request Id"));
			final Label requestOid = new Label("requestOid", new PropertyModel(this, "requestOid"));
			add(requestLabel.setVisible(false));
			add(requestOid.setVisible(false));
			add(new Label("settlementDate", new PropertyModel(this, "settlementDate")));
			add(new Label("currentAccountBalance", new PropertyModel(this, "currentAccountBalance")));
			add(new Label("loanBalance", new PropertyModel(this, "loanBalance")));
			
			final Button  backButton = new Button("back")
			{
				public void onSubmit() 
				{
					setResponsePage(Home.class);
				}
				
			};
			add(backButton);
			
			add(new Button("save")
			{
				public void onSubmit() 
				{	
//					submit the request and get back the OID
					setRequestOid(1289700);
					
					requestOid.setVisible(true);
					requestLabel.setVisible(true);					
					backButton.setVisible(false);
					this.setVisible(false);
					
					setResponsePage(SettleLoanPage.this);					
				}				
			});
		}
		
		public String getCurrentAccountBalance() {
			return currentAccountBalance;
		}

		public void setCurrentAccountBalance(String currentAccountBalance) {
			this.currentAccountBalance = currentAccountBalance;
		}

		public String getLoanBalance() {
			return loanBalance;
		}

		public void setLoanBalance(String loanBalance) {
			this.loanBalance = loanBalance;
		}

		public String getSettlementDate() {
			return settlementDate;
		}

		public void setSettlementDate(String settlementDate) {
			this.settlementDate = settlementDate;
		}	
		
		public long getRequestOid() {
			return requestOid;
		}

		public void setRequestOid(long oid) {
			this.requestOid = oid;
		}
	}
	
	public String getPageName() 
	{
		return pageName;
	}
}
