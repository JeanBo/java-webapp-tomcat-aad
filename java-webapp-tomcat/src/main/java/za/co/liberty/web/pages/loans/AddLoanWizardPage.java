package za.co.liberty.web.pages.loans;

import za.co.liberty.web.pages.BasePage;

public class AddLoanWizardPage extends BasePage
{
	public AddLoanWizardPage() 
	{
		add(new AddLoanWizard("addLoanWizard"));
	}	
	
	private static String pageName= "Add Loan";
	
	public String getPageName() 
	{		
		return pageName;
	}
}
