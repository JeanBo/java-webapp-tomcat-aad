package za.co.liberty.web.pages.panels;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.panel.Panel;

/**
 * This class represents a panel to be used to layout
 * GUI fields in a standard way.
 * @author kxd1203
 *
 */
public class GUIFieldPanel extends Panel {

	private static final long serialVersionUID = 8291156270559784238L;

	private Component label;

	private Component component;
	
	public GUIFieldPanel(String id, Component label, Component component) {
		super(id);
		this.label = label;
		this.component = component;
		add(label);
		add(component);
	}

	public Component getLabel() {
		return label;
	}

	public Component getComponent() {
		return component;
	}	
	
	

}
