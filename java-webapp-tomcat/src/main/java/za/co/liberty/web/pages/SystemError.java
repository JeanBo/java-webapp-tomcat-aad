package za.co.liberty.web.pages;

import java.util.Iterator;
import java.util.Set;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.value.ValueMap;


public class SystemError extends BasePage {

	private static final long serialVersionUID = 1L;
	private final ValueMap properties = new ValueMap();
	
	public SystemError(final PageParameters pageParameters) {
		SystemErrorForm errorForm = new SystemErrorForm("errorForm");
		Set<String> set = pageParameters.getNamedKeys();
		Iterator<String> iter = set.iterator();
		for (Iterator<String> iterator = set.iterator(); iterator.hasNext();) {
			String element =  iterator.next();
			errorForm.add(new Label(element,pageParameters.get(element)));
		}
		add(errorForm);
	}
	
	class SystemErrorForm extends Form {
	
    public SystemErrorForm (String id) {
       super(id);
    }

	@Override
	public void onSubmit() {
//		   System.out.println(getModelObject().toString());
//	       setResponsePage(Home.class);
	}
	
  }

	@Override 
	public boolean isCheckAuthentication() {
		return false;
	}
	
	@Override
	public String getPageName() {
		return "System Error";
	}
	
	@Override
	protected Panel getContextPanel() {
		/* Does not require a panel */
		return new EmptyPanel(CONTEXT_PANEL_NAME);
	}
}
