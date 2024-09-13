package za.co.liberty.web.pages.panels;

import org.apache.wicket.Component;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.Model;

import za.co.liberty.dto.loan.LoanBaseDTO;
import za.co.liberty.dto.loan.LoanDTO;

/**
 * <p>This panel is a container for form data entries where each
 * instance of this panel represents one field component. It
 * allows for the dynamic placement of dynamic web components
 * i.e. Label, Text, Choice etc.</p>
 * 
 * <p><b>Note on usage:</b> Passed component name/id should be "value"</p> 
 * 
 * @author jzb0608 - 24 Apr 2008
 *
 */
public class FormRepeatingPanel extends Panel {

	private static final long serialVersionUID = 6481233962480327767L;
	
	Label descriptionLabel;
	
	HelperPanel valueComponent;
	
	/**
	 * Default constructor
	 * 
	 * @param id
	 * @param bean
	 * @param isView
	 */
	public FormRepeatingPanel(String id, String description, Component value) {
		super(id);
		descriptionLabel = new Label("description", new Model(description));
		valueComponent = HelperPanel.getInstance("value", value);
		add(descriptionLabel);
		add(valueComponent);
		
	}
	
	
}
