package za.co.liberty.web.pages.panels;

import org.apache.log4j.Logger;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxCallListener;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.ajax.markup.html.form.AjaxFallbackButton;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.data.pages.IMaintenancePageModel;
import za.co.liberty.web.pages.interfaces.IChangeableStatefullComponent;
import za.co.liberty.web.pages.interfaces.IMaintenanceParent;

/**
 * <p>Defines a default maintenance selection panel that has a 
 * selection component with two buttons (modify, add new).</p>
 * 
 * <p>Remember to override {@linkplain #getChoiceRenderer()} when renderId and 
 * renderValue is null as an exception will be throw if you don't.  You can 
 * also override {@linkplain #getNewDtoInstance()} to implement more 
 * complicated DTO object instantiations (when add new is clicked)</p>
 * 
 * @author JZB0608 - 30 Apr 2008
 *
 */
public abstract class BaseMaintenanceSelectionPanel <DTO extends Object> 
		extends Panel implements IChangeableStatefullComponent {

	/* Constants */
	private static final long serialVersionUID = 6729405618038745608L;
	public static final int SELECTION_WIDTH = 300;
	private static Logger logger = Logger.getLogger(BaseMaintenanceSelectionPanel.class);
	
	/* Form components */
	protected Panel selectionListPanel;
	protected Component selectionListField;
	protected Panel buttonPanel;
	protected Label listDescriptionField;
	protected Button modifyButton;
	protected Button addNewButton;
	protected Form enclosingForm;
	
	/* Attributes */
	private EditStateType editState;
	protected IMaintenancePageModel<DTO> pageModel;
	protected IMaintenanceParent parent;
	protected Class dtoType;
	
	/* Additional attributes */
	protected String listDescriptionLabel;
	
	/**
	 * Default constructor
	 * 
	 * @param id
	 * @param pageModel
	 * @param parent
	 * @param enclosingForm
	 * @param dtoType
	 */
	public BaseMaintenanceSelectionPanel(String id, String listLabel, IMaintenancePageModel pageModel, 
			IMaintenanceParent parent, Form enclosingForm, Class dtoType) {
		super(id);
		this.enclosingForm = enclosingForm;
		this.parent = parent;
		this.pageModel = pageModel;
		this.dtoType = dtoType;
		this.listDescriptionLabel = listLabel;
		this.editState = parent.getEditState();
	}
	
	/**
	 * Add the components to the form and must be called after the constructor 
	 * is run. Allows additional attributes to be set before create the
	 * components.
	 */
	protected void initialiseForm() {
		add(listDescriptionField = createListDescriptionLabel("listDescription"));
		add(selectionListPanel = HelperPanel.getInstance("selectionPanel",
				selectionListField=createSelectionListField("value")));
		add(buttonPanel = createControlButtonPanel());
		setEditState(this.editState, null);
	}
	
	/**
	 * Add a tooltip on the selection
	 * 
	 * @param text
	 * @return
	 */
	public BaseMaintenanceSelectionPanel<DTO> setTooltipText(String text) {
		selectionListField.add(new AttributeModifier("title", new Model(text)));
		return this;
	}
	
	/**
	 * Create the Combo List selection field
	 * 
	 * @param id
	 * @return
	 */
	protected Label createListDescriptionLabel(String id) {
		return new Label(id, new Model(listDescriptionLabel));
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
			if (modifyButton!=null)
				modifyButton.setEnabled(pageModel.getSelectedItem()!=null
					&& parent.hasModifyAccess());
			addNewButton.setEnabled(true && 
					parent.hasAddAccess());
			selectionListField.setEnabled(true);
		} else {
			if (modifyButton!=null)
				modifyButton.setEnabled(false);
			addNewButton.setEnabled(false);
			selectionListField.setEnabled(false);
		}

		/* Update components that might have changed */
		if (target != null) {
			if (modifyButton!=null)
				target.add(modifyButton);
			target.add(addNewButton);
			target.add(selectionListField);
		}	
	}

	/**
	 * Add style information to tag
	 * 
	 * @param tag
	 */
	protected void decorateStyleOnTag(ComponentTag tag) {
		String style = (String) tag.getAttributes().get("style");
		style = (style==null)?"" : style;
		style+=";width:"+SELECTION_WIDTH+";";
		tag.put("style", style);
	}
	
	/**
	 * Create the button panel
	 * 
	 * @return
	 */
	protected Panel createControlButtonPanel() {

		modifyButton = createModifyButton("button1");
		addNewButton = createAddNewButton("button2");

		Panel panel = ButtonHelperPanel.getInstance("controlButtonPanel",
				modifyButton, addNewButton);
		panel.setOutputMarkupId(true);
		return panel;

	}

	/**
	 * Create the add new button
	 * 
	 * @param id
	 * @return
	 */
	protected Button createAddNewButton(String id) {
		return createAddNewButton(id, false);
	}
	
	/**
	 * Create the add new button
	 * 
	 * @param id
	 * @return
	 */
	protected Button createAddNewButton(String id, final boolean showOverlay) {
		// TODO Get form differently
		Button button = new AjaxFallbackButton(id, enclosingForm) {

			private static final long serialVersionUID = -5330766713711809776L;

			@Override
			protected void onComponentTag(ComponentTag tag) {
				super.onComponentTag(tag);
				tag.getAttributes().put("value", "Add New");
				tag.getAttributes().put("type", "submit");
			}

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form form) {
				doAddNew_onSubmit(target, form);
			}
			
			// #WICKETTEST Test these init handlers 
			@Override
			protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
				super.updateAjaxAttributes(attributes);
				attributes.getAjaxCallListeners().add(new AjaxCallListener() {

					@Override
					public CharSequence getInitHandler(Component component) {
						CharSequence s =   super.getInitHandler(component);
						return "overlay(true);"+ ((s==null)?"":s);
					}
					

					@Override
					public CharSequence getDoneHandler(Component component) {	
						CharSequence s =  super.getDoneHandler(component);
						return  "hideOverlay();" + ((s==null)?"":s);
					}

				});
			}

//			@Override
//			protected IAjaxCallDecorator getAjaxCallDecorator() {
//				if (showOverlay) {
//					return new AjaxCallDecorator() {
//						private static final long serialVersionUID = 1L;
//	
//						public CharSequence decorateScript(CharSequence script) {
//							return "overlay(true);" + script;
//						}
//						
//						@Override
//						public CharSequence decorateOnFailureScript(
//								CharSequence script) {
//							return "hideOverlay();" + script;
//						}
//	
//						@Override
//						public CharSequence decorateOnSuccessScript(
//								CharSequence script) {
//							return "hideOverlay();" + script;
//						}
//	
//					};
//				} else {
//					return super.getAjaxCallDecorator();
//				}
//			}
			
			
		};
		button.setOutputMarkupId(true);
		return button;
	}

	/**
	 * Create the modify button
	 * 
	 * @param id
	 * @return
	 */
	protected Button createModifyButton(String id) {
		Button button = new AjaxFallbackButton(id, enclosingForm) {

			private static final long serialVersionUID = -5330766713711809772L;

			@Override
			protected void onComponentTag(ComponentTag tag) {
				super.onComponentTag(tag);
				tag.getAttributes().put("value", "Modify");
				tag.getAttributes().put("type", "submit");
			}

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form form) {
				doModify_onSubmit(target, form);
			}
		};
		button.setOutputMarkupId(true);
		return button;
	}
	
	/**
	 * Called when Modify button is submitted.  Notify parent and 
	 * swap panels.
	 * 
	 * @param target
	 * @param form
	 */
	public void doModify_onSubmit(AjaxRequestTarget target, Form form) {
		parent.setEditState(EditStateType.MODIFY, target);
		parent.swapContainerPanel(target);
		parent.swapNavigationPanel(target);
	}

	/**
	 * Called when Add new is submitted. Notify parent and 
	 * swap panels.  Ensure that selected item is set before calling.
	 * 
	 * @param target
	 * @param form
	 */
	public void doAddNew_onSubmit(AjaxRequestTarget target, Form form) {
		pageModel.setSelectedItem((DTO) getNewDtoInstance());
		parent.setEditState(EditStateType.ADD, target);
		parent.swapContainerPanel(target);
		parent.swapNavigationPanel(target);
		
	}

	
	/**
	 * Creates a new instance of the DTO 
	 * 
	 * @return
	 */
	public Object getNewDtoInstance() {
		try {
			return dtoType.newInstance();
		} catch (IllegalAccessException e) {
			throw new IllegalArgumentException("Unable to create a new instance of DTO",e);
		} catch (InstantiationException e) {
			throw new IllegalArgumentException("Unable to create a new instance of DTO",e);
		}
	}
	
	/**
	 * Reset the selection
	 *
	 */
	public abstract void resetSelection();
	
	/**
	 * Create the component that handles the selection
	 * 
	 * @param id
	 * @return
	 */
	public abstract Component createSelectionListField(String id);
}