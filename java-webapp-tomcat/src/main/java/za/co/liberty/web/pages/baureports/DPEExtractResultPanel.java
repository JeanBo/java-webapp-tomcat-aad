package za.co.liberty.web.pages.baureports;

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;

public class DPEExtractResultPanel extends Panel{
	private static final long serialVersionUID = 1L;
	
	
	
	public DPEExtractResultPanel(String id)
	{
		super(id);
		add( new DPEExtractResultForm("dpeExtractForm"));
	}
	
	class DPEExtractResultForm extends Form {
				
		public DPEExtractResultForm(String id) {
			super(id);
			
			
		}
	
	
	}
}
