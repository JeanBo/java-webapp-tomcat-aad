package za.co.liberty.web.pages.panels;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.extensions.wizard.WizardStep;
import org.apache.wicket.markup.html.WebComponent;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.convert.IConverter;
import org.apache.wicket.util.convert.converter.DateConverter;

import za.co.liberty.exceptions.UnResolvableException;
import za.co.liberty.interfaces.agreements.requests.RequestKindType;
import za.co.liberty.web.constants.SRSAppWebConstants;
import za.co.liberty.web.data.enums.ComponentType;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.data.enums.fields.IGUIField;
import za.co.liberty.web.pages.BasePage;
import za.co.liberty.web.pages.BaseWindowPage;
import za.co.liberty.web.pages.interfaces.IStatefullComponent;
import za.co.liberty.web.pages.wizard.SRSPopupWizard;
import za.co.liberty.web.system.SRSAuthWebSession;
import za.co.liberty.web.wicket.convert.converters.SRSDateConverter;
import za.co.liberty.web.wicket.markup.html.form.SRSDateField;
import za.co.liberty.web.wicket.markup.html.form.SRSDropDownChoice;
import za.co.liberty.web.wicket.markup.html.form.SRSTextField;
import za.co.liberty.web.wicket.markup.html.form.SRSTextArea;


/**
 * Base Panel will have allow a simple panel to easily be created without having
 * to create each field with a method.
 * 
 * @author DZS2610
 * @author Pritam created the methods Dean(DZS2610) added them to a generic base
 *         panel and modified them to have editable states passed in
 */
public class BasePanel extends Panel implements IStatefullComponent {

	private static final long serialVersionUID = 1L;

	private EditStateType editState = EditStateType.VIEW;

	// These are the request kinds which have an outstanding authorisation
	// pending
	private List<RequestKindType> outStandingRequestKinds = null;

	private Label outstandingAuthPanel;

	private Page parentPage;

	protected final String asteriskSymbol = "*";
	protected final String asteriskSymbolWithFormatting = "<font color=\"red\">*</font>";
	protected final String colonSymbol = ":";

	/**
	 * @param arg0
	 * @deprecated use constructor with page as parameter
	 */
	public BasePanel(String id, EditStateType editState) {
		this(id, editState, null);
	}

	/**
	 * @param arg0
	 */
	public BasePanel(String id, EditStateType editState, Page parentPage) {
		super(id);
		this.parentPage = parentPage;
		if (editState != null) {
			this.editState = editState;
		}
		// get the outstanding requests for the context
		add(new EmptyPanel("outstandingAuths"));
		add(new WebComponent("outstandingAuthsHR").setVisible(false));
	}

	@Override
	protected void onBeforeRender() {
		super.onBeforeRender();
		if (getOutStandingRequestKinds().size() != 0) {
			replace(createOutStandingAuthPanel());
			replace(new WebComponent("outstandingAuthsHR").setVisible(true));
		}
	}

	private Label createOutStandingAuthPanel() {
		if (outstandingAuthPanel == null) {
			outstandingAuthPanel = new Label("outstandingAuths",
					"There are outstanding requests" + outStandingRequestKinds
							+ " needing authorisation");
		}
		return outstandingAuthPanel;
	}

	/**
	 * Generic method to create Fields that should be editable in the states
	 * given in editableStates, If editableStates are null or the array is empty
	 * then the field will always be in view mode
	 * 
	 * @param labelId
	 * @param attribute
	 * @param componentType
	 * @param isEditable
	 * @return
	 * 
	 */
	protected HelperPanel createPageField(Object propertyObject,
			String labelId, String attribute, ComponentType componentType,
			boolean isRequired, boolean ajaxUpdateValue,
			EditStateType... editableStates) {
		return createPageField(propertyObject, labelId, attribute, attribute,
				componentType, isRequired, ajaxUpdateValue, editableStates);
	}



	/**
	 * Generic method to create Fields that should be editable in the states
	 * given in editableStates, If editableStates are null or the array is empty
	 * then the field will always be in view mode
	 * 
	 * @param labelId
	 * @param attribute
	 * @param componentType
	 * @param isEditable
	 * @return
	 */
	protected HelperPanel createPageField(Object propertyObject,
			String labelId, String componentID, String attribute,
			ComponentType componentType, boolean isRequired,
			boolean ajaxUpdateValue, EditStateType... editableStates) {
		boolean view = isView(editableStates);
		if (view) {
			@SuppressWarnings("rawtypes")
			Label label = new Label("value", new PropertyModel(propertyObject,
					attribute));
			return HelperPanel.getInstance(componentID, label, false);
		}
		return createComponent(propertyObject, labelId, componentID, attribute,
				componentType, isRequired, ajaxUpdateValue);

	}
	
	protected HelperPanel createPageField2(Object propertyObject,
			String labelId, String componentID, String attribute,
			ComponentType componentType, boolean isRequired,
			boolean ajaxUpdateValue, EditStateType... editableStates) {
		boolean view = isView(editableStates);
		if (view) {
			CheckBox label = new CheckBox("value", new PropertyModel<Boolean>(propertyObject,
					attribute));
			HelperPanel instance = HelperPanel.getInstance(componentID, label, false);
			instance.setEnabled(false);			
			return instance;
		}
		return createComponent(propertyObject, labelId, componentID, attribute,
				componentType, isRequired, ajaxUpdateValue);

	}

	/**
	 * Determins if the field should be in view mode
	 * 
	 * @param editableStates
	 * @return
	 */
	protected boolean isView(EditStateType[] editableStates) {
		if (editState == EditStateType.AUTHORISE) {
			return true;
		} else if (editableStates != null && editableStates.length != 0) {
			// check the edit states for view
			for (EditStateType state : editableStates) {
				if (editState == state) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * 
	 * Generic method to create Dropdown fields that should be editable on
	 * Maintain and Non-editable on View(Label).
	 * 
	 * @param labelId
	 * @param attribute
	 * @param componentType
	 * @param dataList
	 * @param renderer
	 * @param defaultChoice
	 * @param isEditable
	 * @return
	 */
	protected HelperPanel createDropdownField(Object propertyObject,
			String labelId, String attribute, final List<?> dataList,
			final IChoiceRenderer<?> renderer, String defaultChoice,
			boolean isRequired, EditStateType... editableStates) {
		return createDropdownField(propertyObject, labelId, attribute,
				dataList, renderer, defaultChoice, isRequired, false,
				editableStates);
	}

	/**
	 * 
	 * Generic method to create Dropdown fields that should be editable on
	 * Maintain and Non-editable on View(Label).
	 * 
	 * @param labelId
	 * @param attribute
	 * @param componentType
	 * @param dataList
	 * @param renderer
	 * @param defaultChoice
	 * @param isEditable
	 * @return
	 */
	protected HelperPanel createDropdownField(Object propertyObject,
			String labelId, String attribute, final List<?> dataList,
			final IChoiceRenderer<?> renderer, String defaultChoice,
			boolean isRequired, boolean updateWithAjax,
			EditStateType... editableStates) {
		return createDropdownField(propertyObject, labelId, attribute,
				attribute, dataList, renderer, defaultChoice, isRequired,
				updateWithAjax, editableStates);
	}

	/**
	 * 
	 * Generic method to create Dropdown fields that should be editable on
	 * Maintain and Non-editable on View(Label).
	 * 
	 * @param labelId
	 * @param attribute
	 * @param componentType
	 * @param dataList
	 * @param renderer
	 * @param defaultChoice
	 * @param isEditable
	 * @return
	 */
	protected HelperPanel createDropdownField(Object propertyObject,
			String labelId, String componentID, String attribute,
			final List dataList, final IChoiceRenderer renderer,
			String defaultChoice, boolean isRequired,
			EditStateType... editableStates) {
		return createDropdownField(propertyObject, labelId, componentID,
				attribute, dataList, renderer, defaultChoice, isRequired,
				false, editableStates);
	}

	/**
	 * 
	 * Generic method to create Dropdown fields that should be editable on
	 * Maintain and Non-editable on View(Label).
	 * 
	 * @param labelId
	 * @param attribute
	 * @param componentType
	 * @param dataList
	 * @param renderer
	 * @param defaultChoice
	 * @param isEditable
	 * @return
	 */
	protected HelperPanel createDropdownField(final Object propertyObject,
			String labelId, String componentID, final String attribute,
			final List dataList, final IChoiceRenderer renderer,
			String defaultChoice, boolean isRequired, boolean updateWithAjax,
			EditStateType... editableStates) {
		return createDropdownField(labelId, componentID, new PropertyModel(
				propertyObject, attribute), dataList, renderer, defaultChoice,
				isRequired, updateWithAjax, editableStates);
	}

	/**
	 * 
	 * Generic method to create Dropdown fields that should be editable on
	 * Maintain and Non-editable on View(Label).
	 * 
	 * @param labelId
	 * @param attribute
	 * @param componentType
	 * @param dataList
	 * @param renderer
	 * @param defaultChoice
	 * @param isEditable
	 * @return
	 */
	protected HelperPanel createDropdownField(String labelId,
			String componentID, IModel propertyModel, final List dataList,
			final IChoiceRenderer renderer, String defaultChoice,
			boolean isRequired, boolean updateWithAjax,
			EditStateType... editableStates) {
		if (isView(editableStates)) {
			Label label = new Label("value", propertyModel);

			return HelperPanel.getInstance(componentID, label, false);
		}

		final SRSDropDownChoice dropDownChoice = new SRSDropDownChoice("value",
				propertyModel, dataList, renderer, defaultChoice);
		dropDownChoice.setLabel(new Model<String>(labelId));
		if (isRequired) {
			dropDownChoice.setRequired(true);
		}
		if (updateWithAjax) {
			dropDownChoice
					.add(new AjaxFormComponentUpdatingBehavior("change") {
						private static final long serialVersionUID = 1L;

						@Override
						protected void onUpdate(AjaxRequestTarget arg0) {
							// do nothing, update value through ajax
						}

						@Override
						protected void onError(AjaxRequestTarget target,
								RuntimeException arg1) {
							// print feedback on feedback panel
						}
					});
		}
		return HelperPanel.getInstance(componentID, dropDownChoice);

	}

	/**
	 * Create a HelperPanel for the form field that should be editable on
	 * Maintain and Non-editable on View(Label)
	 * 
	 * @param id
	 * @param component
	 * @return
	 */
	protected HelperPanel createPageField(String id, String labelID,
			Component component, EditStateType... editableStates) {
		return createPageField(id, labelID, null, component, editableStates);
	}

	/**
	 * Create a HelperPanel for the form field that should be editable on
	 * Maintain and Non-editable on View(Label). The non editable label that is
	 * generated on view can optionally be set
	 * 
	 * @param id
	 * @param component
	 * @return
	 */
	protected HelperPanel createPageField(String id, String labelID,
			Label viewLabel, Component component,
			EditStateType... editableStates) {
		if (isView(editableStates)) {
			Label label = viewLabel != null ? viewLabel : new Label("value",
					component.getDefaultModel());
			return HelperPanel.getInstance(id, label);
		}
		if (component instanceof FormComponent) {
			((FormComponent) component).setLabel(new Model<String>(labelID));
		}
		return HelperPanel.getInstance(id, component);

	}

	/**
	 * Create a wicket component to add to another component</br> Currently only
	 * caters for dropdowns and text fields
	 * 
	 * @param labelId
	 * @param attribute
	 * @param componentType
	 * @return
	 */
	protected HelperPanel createComponent(Object propertyObject,
			String labelId, String attribute, ComponentType componentType,
			boolean isRequired, boolean ajaxUpdateValue) {
		return createComponent(propertyObject, labelId, attribute, attribute,
				componentType, isRequired, ajaxUpdateValue);
	}

	/**
	 * Create a wicket component to add to another component</br> Currently only
	 * caters for dropdowns and text fields
	 * 
	 * @param labelId
	 * @param attribute
	 * @param componentType
	 * @return
	 */
	protected HelperPanel createComponent(Object propertyObject,
			String labelId, String componentID, String attribute,
			ComponentType componentType, boolean isRequired,
			boolean ajaxUpdateValue) {
		HelperPanel helper = null;
		switch (componentType) {
		case TEXTFIELD:
			SRSTextField field = new SRSTextField("value", new PropertyModel(
					propertyObject, attribute));
			field.setLabel(new Model<String>(labelId));
			if (isRequired) {
				field.setRequired(true);
			}
			helper = HelperPanel.getInstance(componentID, field);
			helper.setOutputMarkupId(true);
			helper.setOutputMarkupPlaceholderTag(true);
			if (ajaxUpdateValue) {
				field.add(new AjaxFormComponentUpdatingBehavior("change") {
					private static final long serialVersionUID = 1L;

					@Override
					protected void onUpdate(AjaxRequestTarget arg0) {
						// do nothing, update value through ajax
					}
				});

				field.add(new AjaxFormComponentUpdatingBehavior("keyup") {
					private static final long serialVersionUID = 1L;

					@Override
					protected void onUpdate(AjaxRequestTarget arg0) {
						// do nothing, update value through ajax
					}
				});
			}
			return helper;

		case DATE_SELECTION_TEXTFIELD:
			SRSDateField field3 = new SRSDateField("value", new PropertyModel(
					propertyObject, attribute));// {
//				/**
//				 * The converter for the TextField
//				 */
//				private final org.apache.wicket.datetime.DateConverter converter = SRSDateField.newDefaultDateConverter();
//			
//				/**
//				 * @return The specialized converter.
//				 * @see org.apache.wicket.Component#createConverter(java.lang.Class)
//				 */
//				@Override
//				protected IConverter<?> createConverter(Class<?> clazz)
//				{
//					if (Date.class.isAssignableFrom(clazz))
//					{
//						return converter;
//					}
//					return null;
//				}
//				
				
//
//				/**
//				 * @see org.apache.wicket.markup.html.form.AbstractTextComponent.ITextFormatProvider#getTextFormat()
//				 */
//				@Override
//				public final String getTextFormat()
//				{
//					return converter.getDatePattern(getLocale());
//				}
//				
//			};
			field3.setLabel(new Model<String>(labelId));
			if (isRequired) {
				field3.setRequired(true);
			}
			field3.add(new AttributeModifier("size", "11"));
			field3.add(new AttributeModifier("maxlength", "10"));
			// field3.add(new SimpleAttributeModifier("readonly","true"));
			if (ajaxUpdateValue) {
				field3.add(new AjaxFormComponentUpdatingBehavior("change") {
					private static final long serialVersionUID = 1L;

					@Override
					protected void onUpdate(AjaxRequestTarget arg0) {
						// do nothing, update value through ajax
					}
				});
				field3.add(new AjaxFormComponentUpdatingBehavior("keyup") {
					private static final long serialVersionUID = 1L;

					@Override
					protected void onUpdate(AjaxRequestTarget arg0) {
						// do nothing, update value through ajax
					}
				});
			}
			helper = HelperPanel.getInstance(componentID, field3, true);
			helper.setOutputMarkupId(true);
			helper.setOutputMarkupPlaceholderTag(true);
			return helper;
		case CHECKBOX:
			CheckBox field2 = new CheckBox("value", new PropertyModel<Boolean>(
					propertyObject, attribute));
			field2.setLabel(new Model<String>(labelId));
			if (ajaxUpdateValue) {
				field2.add(new AjaxFormComponentUpdatingBehavior("change") {
					private static final long serialVersionUID = 1L;

					@Override
					protected void onUpdate(AjaxRequestTarget arg0) {
						// do nothing, update value through ajax
					}
				});
			}
			helper = HelperPanel.getInstance(componentID, field2);
			helper.setOutputMarkupId(true);
			helper.setOutputMarkupPlaceholderTag(true);
			return helper;
		case LABEL:
			Label lbl = new Label("value", new PropertyModel(propertyObject,
					attribute));
			helper = HelperPanel.getInstance(componentID, lbl);
			return helper;
			
		case TEXTAREA:
			SRSTextArea srstextarea = new SRSTextArea("value", new PropertyModel(
					propertyObject, attribute));
			
			srstextarea.setLabel(new Model<String>(labelId));
			srstextarea.setRequired(isRequired);
			
			helper = HelperPanel.getInstance(componentID, srstextarea);
			helper.setOutputMarkupId(true);
			helper.setOutputMarkupPlaceholderTag(true);
			if (ajaxUpdateValue) {
				srstextarea.add(new AjaxFormComponentUpdatingBehavior("change") {
					private static final long serialVersionUID = 1L;

					@Override
					protected void onUpdate(AjaxRequestTarget arg0) {
						// do nothing, update value through ajax
					}
				});

				srstextarea.add(new AjaxFormComponentUpdatingBehavior("keyup") {
					private static final long serialVersionUID = 1L;

					@Override
					protected void onUpdate(AjaxRequestTarget arg0) {
						// do nothing, update value through ajax
					}
				});
			}
			return helper;

		default:
			throw new UnResolvableException("Component type not known");
		}
	}

	/**
	 * Convenience method to create a new {@link GUIFieldPanel}
	 * 
	 * @param field
	 *            The {@link IGUIField} that represents the field
	 * @param labelText
	 *            null to use the FIELD description, or non-null value to
	 *            override the FIELD description
	 * @param component
	 *            the component to add
	 * @param isDatePicker
	 *            true if the component is a date picker
	 * @return the {@link GUIFieldPanel}
	 */
	protected GUIFieldPanel createGUIFieldPanel(String popupText,
			String fieldDescription, String fieldID, Component component,
			boolean isDatePicker) {
		return createGUIFieldPanel(popupText, fieldDescription, fieldID,
				component, isDatePicker, null);
	}

	/**
	 * Convenience method to create a new {@link GUIFieldPanel}
	 * 
	 * @param field
	 *            The {@link IGUIField} that represents the field
	 * @param labelText
	 *            null to use the FIELD description, or non-null value to
	 *            override the FIELD description
	 * @param component
	 *            the component to add
	 * @param isDatePicker
	 *            true if the component is a date picker
	 * @return the {@link GUIFieldPanel}
	 */
	protected GUIFieldPanel createGUIFieldPanel(String popupText,
			String fieldDescription, String fieldID, Component component,
			boolean isDatePicker, Integer tabindex) {
		GUIFieldPanel ret = null;
		HelperPanel comp = null;

		if (component instanceof TextField && isDatePicker) {
			comp = (HelperPanel.getInstance("panel", (TextField) component,
					isDatePicker));
		} else {
			comp = HelperPanel.getInstance("panel", component);
		}
		ret = createGUIFieldPanel(popupText, fieldDescription, fieldID, comp,
				tabindex);
		return ret;
	}

	/**
	 * Convenience method to create a new {@link GUIFieldPanel} using a
	 * {@link HelperPanel}
	 * 
	 * @param popupText
	 * @param fieldDescription
	 * @param fieldID
	 * @param component
	 * @param requiredField
	 * @return
	 */
	protected GUIFieldPanel createGUIFieldPanel(String popupText,
			String fieldDescription, String fieldID, HelperPanel component) {
		return createGUIFieldPanel(popupText, fieldDescription, fieldID,
				component, null);
	}

	/**
	 * Convenience method to create a new {@link GUIFieldPanel} using a
	 * {@link HelperPanel}
	 * 
	 * @param popupText
	 * @param fieldDescription
	 * @param fieldID
	 * @param component
	 * @param requiredField
	 * @return
	 */

	protected GUIFieldPanel createGUIFieldPanel(String popupText,
			String fieldDescription, String fieldID, HelperPanel component,
			Integer tabindex) {
		GUIFieldPanel ret = null;
		component.setOutputMarkupId(true);
		component.setOutputMarkupPlaceholderTag(true);
		StringBuilder labelDescription = new StringBuilder(fieldDescription);
		// add popup text
		boolean required = false;
		if (component != null
				&& component.getEnclosedObject() instanceof FormComponent) {
			((FormComponent) component.getEnclosedObject()).setLabel(new Model<String>(
					popupText));
			required = ((FormComponent) component.getEnclosedObject())
					.isRequired();
			if (tabindex != null) {
				((FormComponent) component.getEnclosedObject())
						.add(new AttributeModifier("tabindex", String
								.valueOf(tabindex)));
			}
		}
		if (getEditState() != EditStateType.VIEW
				&& getEditState() != EditStateType.AUTHORISE) {

			boolean bool = false;

			if (labelDescription.indexOf(asteriskSymbol) > 0) {
				labelDescription = new StringBuilder(
						labelDescription.substring(0,
								labelDescription.indexOf(asteriskSymbol)));
				bool = true;
			}

			labelDescription.append(
					required ? asteriskSymbolWithFormatting
							: (bool ? asteriskSymbolWithFormatting
									: SRSAppWebConstants.EMPTY_STRING)).append(
					colonSymbol);

		}
		Label lbl = new Label("label", new Model<StringBuilder>(labelDescription));
		// Setting this will escape the formatting of the <font>, <b> tags etc.
		// and preserve it in rendered html
		lbl.setEscapeModelStrings(false);
		lbl.setOutputMarkupId(true);
		ret = new GUIFieldPanel(fieldID, lbl, component);
		return ret;
	}

	public EditStateType getEditState() {
		return editState;
	}

	/**
	 * Will try to get the feedback panel, no guarantee here
	 * 
	 */
	public FeedbackPanel getFeedBackPanel() {
		Page page = this.getPage();
		Object parent = this.getParent();
		// #WICKETFIX  - Uncomment this code
		if (parent instanceof SRSPopupWizard) {
			return ((SRSPopupWizard) parent).getFeedback();
		} else if (parent instanceof WizardStep
				&& ((WizardStep) parent).getParent() instanceof SRSPopupWizard) {
			return ((SRSPopupWizard) ((WizardStep) parent).getParent())
					.getFeedback();
		} else if (parent instanceof WizardStep
				&& ((WizardStep) parent).getParent() instanceof Form
				&& ((Form) ((WizardStep) parent).getParent()).getParent() instanceof SRSPopupWizard) {
			return ((SRSPopupWizard) ((Form) ((WizardStep) parent).getParent())
					.getParent()).getFeedback();
		} else 
			if (page instanceof BasePage) {
			return ((BasePage) page).getFeedbackPanel();
		} else if (page instanceof BaseWindowPage) {
			return ((BaseWindowPage) page).getFeedBackPanel();
		}
		return null;
	}

	/**
	 * return the RequestKindTypes that are unauthorised for the context<br/>
	 * If a user sent in a null page in the constructor or the page was not an
	 * instance of BasePage then and empty list will be returned
	 * 
	 * @return
	 */
	public List<RequestKindType> getOutStandingRequestKinds() {
		if (outStandingRequestKinds == null) {
			if (this.parentPage != null) {
				if (parentPage instanceof BasePage) {
					outStandingRequestKinds = ((BasePage) parentPage)
							.getOutStandingRequestTypesForPanel(this.getClass());
				} else if (parentPage instanceof BaseWindowPage) {
					outStandingRequestKinds = ((BaseWindowPage) parentPage)
							.getOutStandingRequestTypesForPanel(this.getClass());
				}
			} else {
				outStandingRequestKinds = Collections.EMPTY_LIST;
			}
		}
		return outStandingRequestKinds;
	}

	protected Page getParentPage() {
		return parentPage;
	}

	/**
	 * True if this page has modify access
	 * 
	 * @return
	 */
	public boolean hasModifyAccess() {
		return hasModifyAccess(this);
	}

	/**
	 * True if the calling object has modify access
	 * 
	 * @param callingObject
	 * @return
	 */
	public boolean hasModifyAccess(Object callingObject) {
		return hasModifyAccess(callingObject.getClass());
	}

	/**
	 * True if the calling object has modify access
	 * 
	 * @param callingObject
	 * @return
	 */
	public boolean hasModifyAccess(Class<? extends Object> clazz) {
		return SRSAuthWebSession.get().hasModifyAccess(clazz);
	}

	/**
	 * True if this page has delete access
	 * 
	 * @return
	 */
	public boolean hasDeleteAccess() {
		return hasDeleteAccess(this);
	}

	/**
	 * True if the calling object has delete access
	 * 
	 * @param callingObject
	 * @return
	 */
	public boolean hasDeleteAccess(Object callingObject) {
		return hasDeleteAccess(callingObject.getClass());
	}

	/**
	 * True if the calling object has delete access
	 * 
	 * @param callingObject
	 * @return
	 */
	public boolean hasDeleteAccess(Class<? extends Object> clazz) {
		return SRSAuthWebSession.get().hasDeleteAccess(clazz);
	}

	/**
	 * True if this page has add access
	 * 
	 * @return
	 */
	public boolean hasAddAccess() {
		return hasAddAccess(this);
	}

	/**
	 * True if the calling object has add access
	 * 
	 * @param callingObject
	 * @return
	 */
	public boolean hasAddAccess(Object callingObject) {
		return hasAddAccess(callingObject.getClass());
	}

	/**
	 * True if the calling object has add access
	 * 
	 * @param callingObject
	 * @return
	 */
	public boolean hasAddAccess(Class<? extends Object> clazz) {
		return SRSAuthWebSession.get().hasAddAccess(clazz);
	}
	
	/**
	 * Manual form validation due to issues with Wicket migration upgrade to 1.7
	 * 
	 * 
	 * @param validationComponents
	 * @param feedbackPanel
	 * @return
	 */
	public boolean validateFormComponents(Collection<FormComponent> validationComponents, FeedbackPanel feedbackPanel) {
		Logger logger = Logger.getLogger(this.getClass());
		boolean validate = true;
		for(FormComponent comp : validationComponents){
			if(!comp.isValid()){
				validate = false;
				if (logger.isDebugEnabled())
					logger.debug("  -- validation error  " + comp
							+ "   --validators=" + comp.getValidators()
							+ "    --keyPrefix=" + comp.getValidatorKeyPrefix());
				
//				comp.newValidatable().error(arg0);
				
				if (!comp.checkRequired()) {
					feedbackPanel.error(comp.getLabel().getObject() + " is required..");
					continue;
				} else {
					if (logger.isDebugEnabled())
						logger.debug("  -- (not required, different validation) -- validation error  " + comp
								+ "   --keyPrefix=" + comp.getValidators());
					feedbackPanel.error(comp.getLabel().getObject() + " issue processing form value.");
					
				}
			}
		}
		return validate;
	}
	

}
