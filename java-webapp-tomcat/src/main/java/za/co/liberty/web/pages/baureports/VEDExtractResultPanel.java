package za.co.liberty.web.pages.baureports;

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;

public class VEDExtractResultPanel extends Panel{
	private static final long serialVersionUID = 1L;
	
	
	
	public VEDExtractResultPanel(String id)
	{
		super(id);
		add( new VEDExtractResultForm("vedExtractForm"));
	}
	
	class VEDExtractResultForm extends Form {
				
		public VEDExtractResultForm(String id) {
			super(id);
			
			
		}
	
	
	}
}
