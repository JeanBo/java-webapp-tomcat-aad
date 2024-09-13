package za.co.liberty.web.pages.loans;

import org.apache.wicket.Component;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.Model;

import za.co.liberty.dto.loan.LoanBaseDTO;
import za.co.liberty.dto.loan.LoanDTO;

/**
 * Used to show values that are not shown when adding 
 * 
 * @author JZB0608 - 09 Apr 2008
 *
 */
public class LoanRepeatingPanel extends Panel {

	private static final long serialVersionUID = 629606326311533055L;

	Label descriptionLabel;
	Label valueLabel;
	
	/**
	 * Default constructor
	 * 
	 * @param id
	 * @param bean
	 * @param isView
	 */
	public LoanRepeatingPanel(String id, String description, String value,
			final boolean isView, final boolean isNumber) {
		super(id);
		descriptionLabel = new Label("description", new Model(description));
		valueLabel = new Label("value", new Model(value)) {
			
			private static final long serialVersionUID = 1L;
			
			@Override
			protected void onComponentTag(ComponentTag tag) {
				super.onComponentTag(tag);
				String clazz = (String) tag.getAttributes().get("class");
				
				if (isView) {
					clazz += " fieldview";
				}
				if (isNumber) {
					clazz += " number";
				}
				tag.put("class", clazz);
			}
		};
		add(descriptionLabel);
		add(valueLabel);
		
	}
	
	
}
