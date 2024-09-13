package za.co.liberty.web.pages.reports;

import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.form.Form;

import za.co.liberty.web.pages.BaseWindowPage;

public class ReportGenerationNotificationPage extends BaseWindowPage {
	
	private AjaxButton closeButton;
	private ModalWindow window;
	private Form notificationForm;

	public ReportGenerationNotificationPage(ModalWindow window) {
		this.window = window;
		this.add(getNotificationForm());
	}

	private Form getNotificationForm() {
		if (notificationForm==null) {
			notificationForm = new NotificationForm("notificationForm");
		}
		return notificationForm;
	}
	
	private class NotificationForm extends Form {

		public NotificationForm(String id) {
			super(id);
			this.add(getCloseButton());
		}
		
	}

	private AjaxButton getCloseButton() {
		if (closeButton==null) {
			closeButton = new AjaxButton("closeButton") {
				@Override
				protected void onSubmit(AjaxRequestTarget target, Form form) {
					window.close(target);
				}
				
			};
		}
		return closeButton;
	}

	@Override
	public String getPageName() {
		return "Report Generation In Progress";
	}

}
