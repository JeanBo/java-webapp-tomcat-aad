package za.co.liberty.web.pages.maintainagreement;

import java.io.Serializable;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxFallbackButton;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import za.co.liberty.web.pages.BaseWindowPage;

public class BusinessOwnerValidationErrorPage extends BaseWindowPage implements Serializable {
	private Button ok;
	private Label messageLabel;
	private ModalWindow window;
	private boolean initialised;	

	public BusinessOwnerValidationErrorPage(ModalWindow window,
			String validationErrorMessage) {
		this.window=window;
		this.messageLabel = new Label("message", validationErrorMessage);


	}
	
	@Override
	protected void onBeforeRender() {
		if(!initialised){
		add(new BusinessOwnerValidationForm("businessErrorForm"));
		}
		super.onBeforeRender();
	}

	public class BusinessOwnerValidationForm extends Form<Object>{

		private static final long serialVersionUID = 1L;

		public BusinessOwnerValidationForm(String id)
		{
			super(id);
			add(createOkButton("ok",this));
			add(messageLabel);
		}

		private Button createOkButton(String id,
				BusinessOwnerValidationForm businessOwnerValidationForm) {
			ok = new AjaxFallbackButton(id,businessOwnerValidationForm) {
				private static final long serialVersionUID = 1L;
				@Override
				protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
					window.close(target);	
				}
			};
			return ok;
		}
	}
	
	@Override
	public String getPageName() {
		return "Business Owner Validation Error";
	}

}
