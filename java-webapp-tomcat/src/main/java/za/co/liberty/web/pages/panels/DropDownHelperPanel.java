package za.co.liberty.web.pages.panels;

import java.util.List;

import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.panel.Panel;

/**
 * Helper that helps with the placement of various number of buttons on a panel.
 * 
 * @author SSM2707 - 08th Sept 2015
 * 
 */
public class DropDownHelperPanel extends Panel {

	private static final long serialVersionUID = 1L;

	/**
	 * Default constructor
	 * 
	 * @param id
	 * @param butList
	 */
	DropDownHelperPanel(String id, List<DropDownChoice> dropDownList) {
		super(id);
		for (DropDownChoice ddChoice : dropDownList) {
			add(ddChoice);
		}
	}

}
