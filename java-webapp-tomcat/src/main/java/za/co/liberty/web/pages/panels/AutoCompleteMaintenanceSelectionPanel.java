package za.co.liberty.web.pages.panels;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.wicket.Component;
import org.apache.wicket.request.Response;//org.apache.wicket.Response;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormSubmitBehavior;
import org.apache.wicket.AttributeModifier;//org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AbstractAutoCompleteRenderer;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.IAutoCompleteRenderer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.Model;

import za.co.liberty.web.data.pages.IMaintenancePageModel;
import za.co.liberty.web.pages.interfaces.IMaintenanceParent;

/**
 * <p>
 * Defines a default maintenance selection panel that has a auto-complete
 * selection with two buttons (modify, add new).
 * </p>
 * 
 * <p>
 * You have to implement the render methods to provide the choice text and the
 * text to enter when selecting. Most importantly you have to implement the
 * choice list.
 * </p>
 * 
 * TODO jzb0608 - Add performance enhancement to use a cached list to limit
 * choices i.e do a db retrieve after entering 'jzb' keep the list while the
 * first part stays and filter result(jzb1 will be in cached list)
 * 
 * @author JZB0608 - 30 May 2008
 * 
 */
public abstract class AutoCompleteMaintenanceSelectionPanel<DTO extends Object>
		extends BaseMaintenanceSelectionPanel<DTO> {

	private static final long serialVersionUID = 6729405618038745608L;

	/* Additional attributes */
	protected String selectedText;

	protected Map<String, DTO> map;

	/*
	 * This keeps track of whether the user has been selected already or not ,
	 * if true a search will not be done
	 */
	protected boolean searchForList = true;

	/**
	 * Default constructor
	 * 
	 * @param id
	 * @param listLabel
	 * @param pageModel
	 * @param parent
	 * @param enclosingForm
	 * @param dtoType
	 */
	public AutoCompleteMaintenanceSelectionPanel(String id, String listLabel,
			IMaintenancePageModel pageModel, IMaintenanceParent parent,
			Form enclosingForm, Class dtoType) {
		super(id, listLabel, pageModel, parent, enclosingForm, dtoType);
		initialiseForm();
	}

	@Override
	public void resetSelection() {
		// TODO jzb0608 - I might have to add something here
	}

	/**
	 * Create the auto complete renderer. There is no need to override this, use
	 * the render methods.
	 * 
	 * @return
	 */
	final protected IAutoCompleteRenderer getAutoCompleteRenderer() {
		return new AbstractAutoCompleteRenderer() {

			private static final long serialVersionUID = 1L;

			@Override
			protected String getTextValue(Object object) {
				if (object == null) {
					return "";
				}
				return renderTextValue((DTO) object);
			}

			@Override
			protected void renderChoice(Object object, Response response,
					String criteria) {
				response.write(renderChoiceValue((DTO) object));
			}
		};
	}

	/**
	 * Creates an auto complete text field.
	 */
	@Override
	public Component createSelectionListField(String id) {
		/* Create the component */
		final AutoCompleteTextField field = new AutoCompleteTextField(id,
				new Model((pageModel.getSelectedItem() == null) ? ""
						: renderTextValue((DTO) pageModel.getSelectedItem())),
				getAutoCompleteRenderer()) {

			private static final long serialVersionUID = 1L;

			@Override
			protected Iterator getChoices(String input) {
				// if (pageModel.getSelectedItem()!=null) {
				// // Reset selection
				// // this.getRequestCycle().
				// // pageModel.setSelectedItem(null);
				// }
				if (!searchForList) {
					return Collections.EMPTY_LIST.iterator();
				}
				List<DTO> list = retrieveChoices(input);
				// put in so selection box does not display when one value is
				// returned
				if (input.length() >= 7 && list.size() == 1) {
					return Collections.EMPTY_LIST.iterator();
				}
				/* Keep a list of value for easy retrieval after selection */
				map = new HashMap<String, DTO>(list.size());
				for (DTO dto : list) {
					map.put(renderTextValue(dto), dto);
				}
				return list.iterator();
			}

		};
		field.setOutputMarkupId(true);

		/* Add behavior to update selected item */
		field.add(new AjaxFormSubmitBehavior(enclosingForm, "change") {//field.add(new AjaxFormSubmitBehavior(enclosingForm, "change") {

			private static final long serialVersionUID = 1L;

			protected void onSubmit(AjaxRequestTarget target) {
				String str = (String) field.getModelObject();
				getDtoFromSearchCriteriaAndSwapPanels(str, target, true);
			}

			@Override
			protected void onError(AjaxRequestTarget target) {
			}
		});

		field.add(new AjaxFormSubmitBehavior(enclosingForm, "keyup") {//field.add(new AjaxFormSubmitBehavior(enclosingForm, "keyup") {
			private static final long serialVersionUID = 1L;

			protected void onSubmit(AjaxRequestTarget target) {
				String str = (String) field.getModelObject();
				getDtoFromSearchCriteriaAndSwapPanels(str, target, false);

			}

			@Override
			protected void onError(AjaxRequestTarget target) {
			}
		});
		field.add(new AttributeModifier("autocomplete", "off"));//field.add(new SimpleAttributeModifier("autocomplete", "off"));
		return field;
	}

	/**
	 * Search for the DTO and swap the panels if one matches If there is more
	 * than one match then nothing happens and the list will popup for selection
	 * <br/>searchForList is used to tell the autocomplete to render or not
	 * 
	 * @param str
	 * @param target
	 * @param excludeCharCheck
	 * @return
	 */
	private DTO getDtoFromSearchCriteriaAndSwapPanels(String str,
			AjaxRequestTarget target, boolean excludeCharCheck) {
		DTO dto = null;
		if (str != null) {
			boolean search = false;
			if (map != null) {
				dto = map.get(str);
				if (dto != null) {
					swapPanels(dto, str, target);
				} else {
					search = true;
				}
			} else {
				search = true;
			}
			if (search) {
				// try get value before a search is done
				List<DTO> list = (excludeCharCheck || str.length() >= 7) ? retrieveChoices(str)
						: Collections.EMPTY_LIST;
				map = new HashMap<String, DTO>(list.size());
				if (list.size() == 1) {
					dto = list.get(0);
				} else {
					dto = getMatchingDTOFromList(list, str);
				}
				if (dto != null) {
					searchForList = false;
					map.put(renderTextValue(dto), dto);
					swapPanels(dto, str, target);
				} else {
					searchForList = true;
				}
			}
		}
		return dto;
	}

	/**
	 * Switch the panels to include the new searched for DTO
	 * 
	 * @param dto
	 * @param searchString
	 * @param target
	 */
	private void swapPanels(DTO dto, String searchString,
			AjaxRequestTarget target) {
		pageModel.setSelectedItem(dto);
		selectedText = searchString;
		parent.setEditState(getEditState(), target);
		parent.swapContainerPanel(target);
		parent.swapNavigationPanel(target);
	}

	/**
	 * Use the given list to see if the search string matches exactly one DTO If
	 * a match is found the object is returned otherwise a null is returned
	 * 
	 * @param list
	 * @param selection
	 * @return
	 */
	private DTO getMatchingDTOFromList(List<DTO> list, String selection) {
		for (DTO dto : list) {
			if (renderTextValue(dto).equalsIgnoreCase(selection)) {
				return dto;
			}
		}
		return null;
	}

	/**
	 * Returns a list of choices for the given input
	 * 
	 * @param input
	 * @return
	 */
	public abstract List<DTO> retrieveChoices(String input);

	/**
	 * Render the the value that should be put in the text box once selected
	 * 
	 * @param dto
	 * @return
	 */
	public abstract String renderTextValue(DTO dto);

	/**
	 * Render the value that should be displayed in the list of available items.
	 * 
	 * @param dto
	 * @return
	 */
	public abstract String renderChoiceValue(DTO dto);

	@Override
	public void doModify_onSubmit(AjaxRequestTarget target, Form form) {
		super.doModify_onSubmit(target, form);
		// Update the text field with last selected value
		// as it might have altered on screen.
		selectionListField.setDefaultModelObject(renderTextValue((DTO) pageModel
				.getSelectedItem()));
		target.add(selectionListField);//target.addComponent(selectionListField);
	}

}