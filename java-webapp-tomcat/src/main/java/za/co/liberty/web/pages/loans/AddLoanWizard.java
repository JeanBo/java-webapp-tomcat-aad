package za.co.liberty.web.pages.loans;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.extensions.wizard.StaticContentStep;
import org.apache.wicket.extensions.wizard.Wizard;
import org.apache.wicket.extensions.wizard.WizardModel;
import org.apache.wicket.extensions.wizard.WizardStep;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.model.StringResourceModel;
import za.co.liberty.web.helpers.loans.WizardLoanDTO;
import za.co.liberty.web.pages.Home;

public class AddLoanWizard extends Wizard 
{
	private WizardLoanDTO loanDTO;
	
	public AddLoanWizard(String id) 
	{
		super(id);
		loanDTO = new WizardLoanDTO();
		setDefaultModel(new CompoundPropertyModel(loanDTO));
		WizardModel wizardModel = new WizardModel();
		//add your steps into the model
		wizardModel.add(new AddLoanQuestionStep());
		wizardModel.add(new LoanQuoteListStep());
		wizardModel.add(new ConfirmationStep());
		init(wizardModel);
	}	

	private final class AddLoanQuestionStep extends WizardStep
	{		
		public AddLoanQuestionStep()
		{
			setTitleModel(new ResourceModel("addloanquestion.title"));
			
			IModel useQuotesModel = new Model("useQuotes");
			IModel dontUseQuotesModel = new Model("dontUseQuotes");
			
			RadioGroup questionsGroup = new RadioGroup("questionsGroup");
			questionsGroup.setModel(useQuotesModel);
			
			questionsGroup.add(new Radio("useQuotes", useQuotesModel));			
			questionsGroup.add(new Radio("dontUseQuotes", dontUseQuotesModel));
			
			add(questionsGroup);			
		}
	}
	
	private final class LoanQuoteListStep extends WizardStep
	{		
		public LoanQuoteListStep()
		{			
			
			setTitleModel(new StringResourceModel("quotelist.title", this, new Model(loanDTO)));
			setSummaryModel(new ResourceModel("quotelist.summary"));

			long agreementNo = 0;			
//			AgreementLinkingDao loanManagementDao = AgreementLinkingDao.getInstance();
			
//			List quoteList = loanManagementDao.getQuotesForAgreement(agreementNo);
			List quoteList = new ArrayList();
			
			RadioGroup quotesGroup = new RadioGroup("quotesGroup");
			quotesGroup.setModel(new Model((WizardLoanDTO)quoteList.get(0)));
			add(quotesGroup);			
			
			
			ListView quotes = new ListView("quotes",quoteList)
			{					

				protected void populateItem(ListItem item) 
				{	
					item.add(new Label("quoteId", new PropertyModel(item.getModel(), "quoteId")));
					item.add(new Label("loanAmount", new PropertyModel(item.getModel(), "loanAmount")));
					item.add(new Label("interestRate", new PropertyModel(item.getModel(), "interestRate")));
					item.add(new Label("term", new PropertyModel(item.getModel(), "term")));					
					item.add(new Label("paymentAmount", new PropertyModel(item.getModel(), "paymentAmount")));
					item.add(new Radio("radio", item.getModel()));
				}			
			};
			
			quotesGroup.add(quotes);			
		}

	}
	
	private final class ConfirmationStep extends StaticContentStep
	{	
		public ConfirmationStep()
		{
			super(true);
			IModel userModel = new Model(loanDTO);
			setTitleModel(new ResourceModel("confirmation.title"));
			setSummaryModel(new StringResourceModel("confirmation.summary", this, userModel));
			setContentModel(new StringResourceModel("confirmation.content", this, userModel));
		}
	}

	
	public void onCancel()
	{
		setResponsePage(Home.class);
	}
	
	public void onFinish()
	{
		setResponsePage(Home.class);
	}

	public WizardLoanDTO getLoanDTO() {
		return loanDTO;
	}

	public void setLoanDTO(WizardLoanDTO loanDTO) {
		this.loanDTO = loanDTO;
	}
	
//	public void onActiveStepChanged(IWizardStep newStep) 
//	{	
//		if (newStep.getClass() == LoanQuoteListStep.class)
//		{
//			
//			
//		}
//		super.onActiveStepChanged(newStep);
//	}

}
