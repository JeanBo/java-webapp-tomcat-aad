package za.co.liberty.web.pages.panels;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.Model;

import za.co.liberty.dto.userprofiles.ISessionUserProfile;
import za.co.liberty.interfaces.agreements.requests.RequestKindType;
import za.co.liberty.web.data.enums.ComponentType;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.data.enums.PanelToRequestMapping;
import za.co.liberty.web.data.enums.fields.IGUIField;
import za.co.liberty.web.system.SRSAuthWebSession;
import za.co.liberty.web.wicket.view.ContextDrivenViewTemplate;

/**
 * This class represents the common functionality for all agreement
 * maintenance pages, using a common view template to control the 
 * presentation of components. 
 * 
 * @author kxd1203
 *
 * @param <F> The class representing the FIELD for the view template
 * @param <T> The class representing the CONTEXT for the view template
 */
public abstract class ViewTemplateBasePanel<F extends IGUIField,T> extends BasePanel {
	
	private Map<F, Component> fields = new HashMap<F, Component>();

	private Map<F, Component> labels = new HashMap<F, Component>();
	
	/**
	 * Get the view template for the page.
	 * @return The view template
	 */
	protected abstract ContextDrivenViewTemplate<F, T> getViewTemplate();
	
	/**
	 * Get the view template context for the page
	 * @return the view template
	 */
	protected abstract T getViewTemplateContext();
	
	/**
	 * Create a new {@link ViewTemplateBasePanel} with the specified ID and edit state
	 * @param id The wicket ID
	 * @param editState the current edit state
	 */
	public ViewTemplateBasePanel(String id, EditStateType editState) {
		this(id, editState,null);
	}
	
	/**
	 * Create a new {@link ViewTemplateBasePanel} with the specified ID,edit state,Parent page
	 * @param id The wicket ID
	 * @param editState the current edit state
	 */
	public ViewTemplateBasePanel(String id, EditStateType editState,Page parentPage) {
		super(id, editState,parentPage);
	}
	
	/**
	 * Method to determine if processing of outstanding requests against the
	 * view template is allowed. Override and return false where there are
	 * multiple panels being displayed in a single panel, where you only
	 * want one master panel to process all requests that are linked to the 
	 * various independant panels
	 * 
	 * @return
	 */
	protected boolean isProcessOutstandingRequestsAllowed() {
		return true;
	}
	
	
	@Override
	protected void onBeforeRender() {
		super.onBeforeRender();
		if (getViewTemplate()!=null &&
				isProcessOutstandingRequestsAllowed()) {
			/**
			 * Disable fields for outstanding requests
			 */
			List<RequestKindType> disabledRequestKinds = new ArrayList<RequestKindType>();
			disabledRequestKinds.addAll(getOutStandingRequestKinds());
			/**
			 * Additionally process security checks to find if any other fields
			 * must be disabled for this class based on request security restrictions
			 * for the maintain process
			 */
			if (getEditState().equals(EditStateType.MODIFY)) {
				RequestKindType[] requestKindTypes = 
					PanelToRequestMapping.getMappingForPageAndPanel(
							getParentPage()!=null?getParentPage().getClass():null,
							getClass());
				if (requestKindTypes!=null) {
					List<RequestKindType> listOfPanelRequests = 
						new ArrayList<RequestKindType>(
								Arrays.asList(requestKindTypes));
					filterRequestsToBeUsedForSecurityCheckInMaintain(listOfPanelRequests);
					ISessionUserProfile sessionUser = SRSAuthWebSession.get().getSessionUser();
					for (RequestKindType requestKind : listOfPanelRequests) {
						boolean allow = sessionUser.isAllowRaise(requestKind);
						if (!allow && !disabledRequestKinds.contains(requestKind)) {
							disabledRequestKinds.add(requestKind);
						}
					}
				}
			}
			/**
			 * Set the final value of request fields to disable
			 */
			getViewTemplate().setOutstandingRequests(disabledRequestKinds);
		}
	}

	/**
	 * Override this method to filter the list of requests that will be used
	 * to determine which fields should be disabled based on access
	 * to raise the specified requests
	 * 
	 * @param listOfPanelRequests
	 */
	protected void filterRequestsToBeUsedForSecurityCheckInMaintain(List<RequestKindType> listOfPanelRequests) {
		
	}

	/**
	 * This method is called to control the visibility of all fields and labels
	 * that have been added to the fields and labels maps, using the view template
	 * to determine if they are visible or not.	 *
	 */
	protected void checkFieldVisibility() {
		for (Entry<F, Component> entry : labels.entrySet()) {
			entry.getValue().setVisible(isVisible(entry.getKey()));
		}
		for (Entry<F, Component> entry : fields.entrySet()) {
			entry.getValue().setVisible(isVisible(entry.getKey()));
		}
	}

	protected boolean isVisible(F guiField) {
		return getViewTemplate().isViewable(guiField, getEditState(), 
				getViewTemplateContext());
	}
	
	/**
	 * Convenience method to create a new {@link GUIFieldPanel} 
	 * @param field The {@link IGUIField} that represents the field
	 * @param helperPanel The HelperPanel to add
	 * @return the {@link GUIFieldPanel}
	 */
	protected GUIFieldPanel createGUIFieldPanel(F field, HelperPanel helperPanel) {
		return createGUIFieldPanel(field, helperPanel.getEnclosedObject());
	}
	
	/**
	 * Convenience method to create a new {@link GUIFieldPanel} 
	 * @param field The {@link IGUIField} that represents the field
	 * @param component The component to add
	 * @return the {@link GUIFieldPanel}
	 */
	protected GUIFieldPanel createGUIFieldPanel(F field, Component component) {
		return createGUIFieldPanel(field,null,component,false);
	}
	
	/**
	 * Convenience method to create a new {@link GUIFieldPanel}
	 * @param field The {@link IGUIField} that represents the field
	 * @param labelText null to use the FIELD description, or non-null value 
	 * to override the FIELD description
	 * @param component the component to add
	 * @param isDatePicker true if the component is a date picker
	 * @return the {@link GUIFieldPanel}
	 */
	protected GUIFieldPanel createGUIFieldPanel(F field, String labelText, Component component, boolean isDatePicker) {
		GUIFieldPanel ret = null;
		Component comp = null;
		comp = getHelperPanelForGuiFieldComponent(component, isDatePicker);
		comp.setOutputMarkupId(true);
		comp.setOutputMarkupPlaceholderTag(true);
		Label lbl = getGuiFieldLabel(field, labelText);
		ret = new GUIFieldPanel(field.getFieldId(),lbl,comp);
		getLabels().put(field, lbl);
		getFields().put(field, comp);
		ret.setOutputMarkupId(true);
		ret.setOutputMarkupPlaceholderTag(true);
		return ret;
	}

	/**
	 * Convenience method to create a new {@link GUIFieldPanel}
	 * @param field The {@link IGUIField} that represents the field
	 * @param labelText null to use the FIELD description, or non-null value 
	 * to override the FIELD description
	 * @param component the component to add
	 * @param isDatePicker true if the component is a date picker
	 * @return the {@link GUIFieldPanel}
	 */
	protected GUIFieldPanel createGUIFieldPanelLBF(F field, String labelText, Component component, boolean isDatePicker, boolean isButton) {
		GUIFieldPanel ret = null;
		Component comp = null;
		comp = getHelperPanelForGuiFieldComponent(component, isDatePicker);
		comp.setOutputMarkupId(true);
		comp.setOutputMarkupPlaceholderTag(true);
		Label lbl = getGuiFieldLabelCustom(field, labelText);
		ret = new GUIFieldPanel(field.getFieldId(),lbl,comp);
		getLabels().put(field, lbl);
		getFields().put(field, comp);
		ret.setOutputMarkupId(true);
		ret.setOutputMarkupPlaceholderTag(true);
		return ret;
	}
	/**
	 * Create the standard label used in a GUIFieldPanel
	 * @param field
	 * @param labelText
	 * @return
	 */
	protected Label getGuiFieldLabel(F field, String labelText) {
		boolean required = getViewTemplate().isRequired(field, getViewTemplateContext());
		String labelDescription = field.getDescription()+((required && (getEditState() == EditStateType.MODIFY || 
				getEditState() == EditStateType.ADD))?asteriskSymbolWithFormatting:"")+colonSymbol;
		Label lbl = new Label("label",new Model(labelText!=null?labelText:labelDescription));
		lbl.setEscapeModelStrings(false);
		lbl.setOutputMarkupId(true);
		return lbl;
	}
	
	
	/**
	 * Create the standard label used in a GUIFieldPanel
	 * @param field
	 * @param labelText
	 * @return
	 */
	protected Label getGuiFieldLabelCustom(F field, String labelText) {
		boolean required = getViewTemplate().isRequired(field, getViewTemplateContext());
		String labelDescription = field.getDescription()+((required && (getEditState() == EditStateType.MODIFY || 
				getEditState() == EditStateType.ADD))?asteriskSymbolWithFormatting:"")+" ";
		Label lbl = new Label("label",new Model(labelText!=null?labelText:labelDescription));
		lbl.setEscapeModelStrings(false);
		lbl.setOutputMarkupId(true);
		return lbl;
	}

	protected Component getHelperPanelForGuiFieldComponent(Component component, boolean isDatePicker) {
		Component comp;
		if (component instanceof TextField && isDatePicker) {
//			System.out.println("Is textField " + component);
			comp = (HelperPanel.getInstance("panel", (TextField)component, isDatePicker));
		} else {
//			if (isDatePicker) {
//				System.out.println("Is NOT textField && isDatePicker " + component.getClass());
//			}
			comp = HelperPanel.getInstance("panel", component);
		}

		return comp;
	}	

	/**
	 * Create a {@link HelperPanel} using definitions from the specified field
	 * @param field The {@link IGUIField} that represents the field
	 * @param propertyObject The context
	 * @param component the component to add
	 * @return a new {@link HelperPanel}
	 */
	protected HelperPanel createGUIPageField(F field,
			Object propertyObject, Component component) {
		return createGUIPageField(field,propertyObject,component,null);
	}

	/**
	 * Create a {@link HelperPanel} using definitions from the specified field
	 * @param field The {@link IGUIField} that represents the field
	 * @param propertyObject The context
	 * @param component the component to add
	 * @param viewLabel null to use a new {@link Label}, or non-null to 
	 * override the label in view mode
	 * @return a new {@link HelperPanel}
	 */
	protected HelperPanel createGUIPageField(F field,
			Object propertyObject, Component component, Label viewLabel) {
		HelperPanel ret = createPageField(field.getFieldId(), field.getDescription(), 
				viewLabel, component, 
				getViewTemplate().getEditStates(field,getViewTemplateContext()));
		if (ret.getEnclosedObject() instanceof FormComponent) {
			((FormComponent)ret.getEnclosedObject()).setRequired(
					getViewTemplate().isRequired(field, getViewTemplateContext()));
		}
		fields.put(field, ret);
		return ret;
	}

	/**
	 * Create a {@link HelperPanel} using definitions from the specified field
	 * @param field The {@link IGUIField} that represents the field
	 * @param propertyObject The context
	 * @param componentType The type of component to create
	 * @param ajaxUpdateValue true if ajax update is required
	 * @return a new {@link HelperPanel}
	 */
	protected HelperPanel createGUIPageField(F field,
			Object propertyObject, ComponentType componentType,
			boolean ajaxUpdateValue) {
		HelperPanel ret = createPageField(propertyObject, field.getDescription(), 
				field.getFieldId(), componentType,
				getViewTemplate().isRequired(field, getViewTemplateContext()),
				ajaxUpdateValue, getViewTemplate().getEditStates(field,
						getViewTemplateContext()));
		
//		protected HelperPanel createPageField(Object propertyObject,
//				String labelId, String attribute, ComponentType componentType,
//				boolean isRequired, boolean ajaxUpdateValue,
//				EditStateType... editableStates) {
//			return createPageField(propertyObject, labelId, attribute, attribute,
//					componentType, isRequired, ajaxUpdateValue, editableStates);
		fields.put(field, ret);
		return ret;
	}
	
	/**
	 * Get the fields for this page
	 * @return the fields
	 */
	public Map<F, Component> getFields() {
		return fields;
	}

	/**
	 * Set the fields for this page
	 * @param fields the fields
	 */
	public void setFields(Map<F, Component> fields) {
		this.fields = fields;
	}

	/**
	 * Get the labels for this page
	 * @return the labels
	 */
	public Map<F, Component> getLabels() {
		return labels;
	}

	/**
	 * Set the labels for this page
	 * @param labels the labels
	 */
	public void setLabels(Map<F, Component> labels) {
		this.labels = labels;
	}
	
}
