package za.co.liberty.web.pages.admin;

import java.util.Date;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxFallbackButton;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.PropertyModel;

import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.pages.admin.models.RoleUpdateSchedulerModel;
import za.co.liberty.web.pages.interfaces.IChangeableStatefullComponent;

/**
 * Allows for the stopping of the current timer service or
 * the adding of a new schedule.
 * 
 * @author JZB0608 - 02 Jun 2008
 *
 */
public class RoleUpdateSchedulerSelectionPanel  
		extends Panel implements IChangeableStatefullComponent {

	/* Constants */
	private static final long serialVersionUID = 6729405618038745608L;
	public static final int SELECTION_WIDTH = 300;
	
	/* Form components */
	protected Label statusField;
	protected Button doButton;
	protected Form enclosingForm;
	
	/* Attributes */
	private EditStateType editState;
	protected RoleUpdateSchedulerModel pageModel;
	protected RoleUpdateScheduler parent;
	
	/**
	 * Default constructor
	 * 
	 * @param id
	 * @param pageModel
	 * @param parent
	 * @param enclosingForm
	 * @param dtoType
	 */
	public RoleUpdateSchedulerSelectionPanel(String id, RoleUpdateSchedulerModel pageModel, 
			RoleUpdateScheduler parent, Form enclosingForm) {
		super(id);
		this.enclosingForm = enclosingForm;
		this.parent = parent;
		this.pageModel = pageModel;
		
		add(statusField = createStatusField("status"));
		add(doButton = createDoButton("doButton"));
	}
		
	/**
	 * Retrieve the current edit state
	 */
	public EditStateType getEditState() {
		return editState;
	}
	
	/**
	 * Update the edit state for this panel (enables / disables certain components)
	 */
	public void setEditState(EditStateType newState, AjaxRequestTarget target) {
		this.editState = newState;
		
		/* Set component access */
		if (editState == EditStateType.VIEW) {
			doButton.setEnabled(parent.hasModifyAccess());
		} else {
			doButton.setEnabled(false);
		}

		/* Update components that might have changed */
		if (target != null) {
			target.add(doButton);
		}	
	}

	/**
	 * Create the do button
	 * 
	 * @param id
	 * @return
	 */
	protected Button createDoButton(String id) {
		Button button = new AjaxFallbackButton(id, enclosingForm) {
			private static final long serialVersionUID = -5330766713711809776L;

			@Override
			protected void onComponentTag(ComponentTag tag) {
				super.onComponentTag(tag);
				tag.getAttributes().put("value", (pageModel.isActive())? "Stop" 
						: "Add Service");
				tag.getAttributes().put("type", "submit");
			}

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form form) {
				doButton_onSubmit(target, form);
			}
		};
		button.setOutputMarkupId(true);
		return button;
	}


	/**
	 * Called when button is submitted. Notify parent and 
	 * swap panels.  Ensure that selected item is set before calling.
	 * 
	 * @param target
	 * @param form
	 */
	public void doButton_onSubmit(AjaxRequestTarget target, Form form) {

		if (pageModel.isActive()) {
			parent.stopTimer();
			return;
		} 
		pageModel.setEditing(true);
		pageModel.setStartDate(new Date());
		
		parent.setEditState(EditStateType.MODIFY, target);
		parent.swapContainerPanel(target);
		parent.swapNavigationPanel(target);
		
	}

	
	/**
	 * Creates a new instance of the DTO 
	 * 
	 * @return
	 */
	public Object getNewDtoInstance() {
		return null;
	}
	
	
	/**
	 * Create the status label field
	 * 
	 * @param id
	 * @return
	 */
	public Label createStatusField(String id) {
		String field = null;
		return (Label) new Label(id, new PropertyModel(pageModel,"statusDescription"))
			.setOutputMarkupId(true);
	}

	/**
	 * Method that is called at certain time intervals
	 * 
	 * @param target
	 */
	public void do_onTimer(AjaxRequestTarget target) {
		setEditState(getEditState(), target);
		target.add(statusField);
	}
}