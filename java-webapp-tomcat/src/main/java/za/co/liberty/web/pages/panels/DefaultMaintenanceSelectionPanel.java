package za.co.liberty.web.pages.panels;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

import za.co.liberty.web.data.pages.IMaintenancePageModel;
import za.co.liberty.web.pages.interfaces.IMaintenanceParent;

/**
 * <p>Defines a default maintenance selection panel that has a 
 * combo box with two buttons (modify, add new).</p>
 * 
 * <p>Remember to override {@linkplain #getChoiceRenderer()} when renderId and 
 * renderValue is null as an exception will be throw if you don't.  You can 
 * also override {@linkplain #getNewDtoInstance()} to implement more 
 * complicated DTO object instantiations (when add new is clicked)</p>
 * 
 * @author JZB0608 - 30 Apr 2008
 *
 */
public class DefaultMaintenanceSelectionPanel <DTO extends Object> 
		extends BaseMaintenanceSelectionPanel<DTO> {

	private static final long serialVersionUID = 6729405618038745608L;
	
	
	/* Additional attributes */
	protected String listDescriptionLabel;
	protected String renderId;
	protected String renderValue;
	
	/**
	 * Calls default constructor {@linkplain #DefaultMaintenanceSelectionPanel(String, IMaintenancePageModel, IMaintenanceParent, Form, Class, null, null)}
	 * Remember to override {@linkplain #getChoiceRenderer()} when renderId and renderValue is null as 
	 * an exception will be throw if you don't.
	 * 
	 * @param id
	 * @param listLabel
	 * @param pageModel
	 * @param parent
	 * @param enclosingForm
	 * @param dtoType
	 */
	public DefaultMaintenanceSelectionPanel(String id, String listLabel, IMaintenancePageModel<DTO> pageModel, 
			IMaintenanceParent parent, Form enclosingForm, Class<DTO> dtoType) {
		this(id, listLabel, pageModel, parent, enclosingForm, dtoType, null, null);
	}

	/**
	 * Default constructor
	 * 
	 * @param id
	 * @param pageModel
	 * @param parent
	 * @param enclosingForm
	 * @param dtoType
	 * @param renderValue
	 * @param renderId
	 */
	public DefaultMaintenanceSelectionPanel(String id, String listLabel, IMaintenancePageModel<DTO> pageModel, 
			IMaintenanceParent parent, Form enclosingForm, Class<DTO> dtoType,
			String renderValue, String renderId) {
		super(id, listLabel, pageModel, parent, enclosingForm, dtoType);
		this.renderId = renderId;
		this.renderValue = renderValue;
		initialiseForm();
	}
	
	
	/**
	 * Implement to define the renderer
	 * 
	 * @return
	 * @throws IllegalStateException when renderValue or renderId is null
	 */
	protected IChoiceRenderer getChoiceRenderer() {
		if (renderValue == null || renderId == null) {
			throw new IllegalStateException("Unable to render selection list. " +
					"Either provide render values or override the relevant method");
		}
		return new ChoiceRenderer(renderValue, renderId);
	}
	
	/**
	 * Create the list of items
	 * 
	 * @param renderer
	 * @return
	 */
	protected DropDownChoice createComboList(String id) {
		return createComboList(id, getChoiceRenderer());
	}

	/**
	 * Create the list of menu items
	 * 
	 * @return
	 */
	public DropDownChoice createComboList(String id, IChoiceRenderer renderer) {
		DropDownChoice list = new DropDownChoice(id, getSelectedItemModel(), getSelectionList(),
					renderer) {
						private static final long serialVersionUID = 1L;

						@Override
						protected void onComponentTag(ComponentTag tag) {
							super.onComponentTag(tag);
							decorateStyleOnTag(tag);
						}
						
		};
		list.setOutputMarkupId(true);
		/* Add select behavior */
		list.add(new AjaxFormComponentUpdatingBehavior("change") {
			private static final long serialVersionUID = 0L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				// Moved the onchange behavior out into a do.. method.
				DefaultMaintenanceSelectionPanel.this.doSelectionListField_onChange(target);
			}
		});
		return list;
	}
	
	/**
	 * Return the model required to get the selected item
	 * @return
	 */
	protected IModel getSelectedItemModel() {
		return new PropertyModel(
				pageModel, "selectedItem");
	}

	/**
	 * The onchange behaviour of the selection list field (combo box)
	 * @param target
	 */
	protected void doSelectionListField_onChange(AjaxRequestTarget target) {
		parent.setEditState(getEditState(), target);
		parent.swapContainerPanel(target);
		parent.swapNavigationPanel(target);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void resetSelection() {
		((DropDownChoice) selectionListField).setChoices(new ArrayList());
	}

	@Override
	public Component createSelectionListField(String id) {
		return createComboList(id);
	}
	
	/**
	 * Return the list of valid items in the selection list.
	 * @return
	 */
	public List<DTO> getSelectionList() {
		return pageModel.getSelectionList();
	}
	
}