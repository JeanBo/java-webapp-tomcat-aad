package za.co.liberty.web.pages.panels;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.RepeatingView;

import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.pages.MaintenanceBasePage;
import za.co.liberty.web.pages.interfaces.IStatefullComponent;

/**
 * This is the base panel for Maintenance detail panels 
 * and is generally used with {@link MaintenanceBasePage}.  
 * This panel places components with labels dynamically and there
 * is no need to provide a html page.
 * 
 * @author JZB0608 - 05 May 2008
 *
 */
public abstract class MaintenanceBasePanel extends Panel implements IStatefullComponent {

	/* Constants */
	public static String FORM_NAME = "mainForm";
	public static String REPEATING_FIELD_NAME = "repeatingField";
	private static final long serialVersionUID = -4051405435435999070L;
	
	/* Components */
	protected RepeatingView repeatingView;
	protected Form form;
	
	/* Other */
	private EditStateType editState;
	private int componentCount =0;
	protected Object bean;
	
	/**
	 * Default constructor 
	 * 
	 * @param id
	 * @param editState
	 */
	public MaintenanceBasePanel(String id, EditStateType editState, Object bean) {
		super(id);
		this.bean = bean;
		this.editState = editState;
		this.add(form = new MaintenanceBasePanelForm(FORM_NAME));
	}

	/**
	 * Implementation of {@link IStatefullComponent#getEditState()}
	 */
	public EditStateType getEditState() {
		return editState;
	}

	/**
	 * Form that contains all data for this panel
	 * 
	 * @author jzb0608 - 06 May 2008
	 * 
	 */
	public class MaintenanceBasePanelForm extends Form {

		private static final long serialVersionUID = 8202665439577865351L;

		public MaintenanceBasePanelForm(String id) {
			super(id);
			add(repeatingView = createRepeatingField());
		}

	}
	/**
	 * Create a new component.
	 * 
	 * @param description
	 * @param component
	 * @return
	 */
	protected FormRepeatingPanel createFormComponent(String description, Component component) {
		return new FormRepeatingPanel(componentCount++ + "", description,component);
	}
	
	/**
	 * Create a repeating view of {@linkplain FormRepeatingPanel}
	 * 
	 * @return
	 */
	public abstract RepeatingView createRepeatingField();
}
