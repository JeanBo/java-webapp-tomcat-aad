package za.co.liberty.web.pages;

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.PropertyModel;

import za.co.liberty.web.system.SRSAuthWebSession;

public class LogonPanel extends Panel {
	
	private static final long serialVersionUID = 1L;
	
	private RequiredTextField txtUserid;
	private PasswordTextField txtPassword;
	
	private String userId;
	private String password;		

	public LogonPanel(String id) {
		super(id);
		setRenderBodyOnly(true);
		Form form = new LogonForm("logonForm"); 
		add(form);

	}

	class LogonForm extends Form {
		private static final long serialVersionUID = 1L;
		
		public LogonForm(String id) {
			super(id);
			txtUserid = new RequiredTextField("userid", new PropertyModel(
					LogonPanel.this, "userId"));
			txtPassword = new PasswordTextField("password", new PropertyModel(
					LogonPanel.this, "password"));
			add(txtUserid);
			add(txtPassword);
		}

		@Override
		public void onSubmit() {
			
			if (SRSAuthWebSession.get().authenticate(userId,
					password)) {
				setResponsePage(Home.class);				
			} else {
				// WICKETFIX WICKETTEST
//				if (SRSAuthWebSession.get().isUserValid(userId)) {
//					error("Userid or Password incorrect");
//				}else{
//					error("Please verify that a valid userid has been entered. If it is correct, please contact x2000 (Liberty staff) or (011) 408 2929 (Intermediaries) to create you as a user on SRS");
//				}
			}
//			setRedirect(true);				
		}

	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}	
}
