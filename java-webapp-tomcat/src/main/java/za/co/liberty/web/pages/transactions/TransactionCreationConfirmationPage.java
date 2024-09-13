/**
 * 
 */
package za.co.liberty.web.pages.transactions;

import java.io.Serializable;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.Model;

import za.co.liberty.dto.transaction.IPolicyTransactionModel;
import za.co.liberty.web.pages.BaseWindowPage;
import za.co.liberty.web.wicket.markup.html.form.SRSLabel;

/**
 * A page used to if the user wants to retain data that was used to create a Policy Transaction
 * @author zzt2108
 *
 */
public class TransactionCreationConfirmationPage extends BaseWindowPage {

	private ModalWindow modalWindow;
	private Form<?> form;
	
	final private IPolicyTransactionModel model;
	
	public TransactionCreationConfirmationPage(ModalWindow modalWindow, Form<?> form, IPolicyTransactionModel model, String confirmationText) {
		this.modalWindow = modalWindow;
		this.form = form;
		this.model = model;
		add(createConfirmationLabel("confirmText", confirmationText));
		add(createYesButton("yesBtn"));
		add(createNoButton("noBtn"));
		this.setOutputMarkupId(true);
	}

	private Component createNoButton(String id) {
		Button button = new Button(id);	
		button.setOutputMarkupId(true);
		
		button.add(new AjaxFormComponentUpdatingBehavior("click"){			
			private static final long serialVersionUID = 1L;
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				
				if(form.getParent() instanceof PolicyTransactionInfoPanel)
					((PolicyTransactionInfoPanel)form.getParent()).resetFields(true);
				else if(form.getParent() instanceof PolicyTransactionDPEPanel)
					((PolicyTransactionDPEPanel)form.getParent()).resetFields(true);
				
				modalWindow.close(target);
				
			}			
		});	
		
		return button;
	}

	private Component createYesButton(String id) {
		Button button = new Button(id);
		button.add(new AjaxFormComponentUpdatingBehavior("click") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {

				if (form.getParent() instanceof PolicyTransactionInfoPanel)
					((PolicyTransactionInfoPanel) form.getParent()).resetFields(false);
				else if (form.getParent() instanceof PolicyTransactionDPEPanel)
					((PolicyTransactionDPEPanel) form.getParent()).resetFields(false);

				modalWindow.close(target);
			}
		});
		button.setOutputMarkupId(true);
		return button;
	}

	private Component createConfirmationLabel(String id, String confirmationText) {
		return new SRSLabel(id, new Model<Serializable>(confirmationText));
	}

	/* (non-Javadoc)
	 * @see za.co.liberty.web.pages.BaseWindowPage#getPageName()
	 */
	@Override
	public String getPageName() {
		// TODO Auto-generated method stub
		return null;
	}

}
