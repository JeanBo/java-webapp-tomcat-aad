package za.co.liberty.web.wicket.markup.html.grid;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.convert.IConverter;
import org.apache.wicket.validation.IValidator;

import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.pages.panels.HelperPanel;
import za.co.liberty.web.wicket.markup.html.form.SRSTextField;

/**
 * Special Text field specifically for columns 
 * 
 * @author JZB0608
 *
 * @param <T>
 */
public abstract class SRSTextFieldColumn<T> extends SRSDataGridColumn<T> {
	
	private static final long serialVersionUID = 1L;

	private boolean bold;
	
	private boolean required;
	
	private IConverter converter;
	
	private IModel headerModel;

	private IValidator validator;
	
	public SRSTextFieldColumn(String columnId,
			IModel headerModel, 
			String objectProperty, 
			EditStateType state) {
		this(columnId,headerModel,objectProperty,state,null);
	}

	public SRSTextFieldColumn(String columnId,
			IModel headerModel, 
			String objectProperty, 
			EditStateType state,
			IValidator validator) {
		super(columnId, headerModel, objectProperty, state);
		this.headerModel=headerModel;
		this.validator = validator;
	}
	
	public abstract boolean isCellEditable(EditStateType state,T data);

	@Override
	public Panel newCellPanel(WebMarkupContainer parent, String componentId,
			IModel rowModel, String objectProperty, EditStateType state,
			T data) {
		Component component = null;
		if (isCellEditable(state, data)) {
			component = new SRSTextField("value", new PropertyModel(data, objectProperty)) {
				@Override
				public IConverter getConverter(Class targetClass) {
					if (converter==null) {
						return super.getConverter(targetClass);
					} else {
						return converter;
					}
				}
			};
			updateTextFieldComponent((SRSTextField)component);
		} else {
			component = new Label("value", new PropertyModel(data, objectProperty)) {
				@Override
				public IConverter getConverter(Class targetClass) {
					if (converter==null) {
						return super.getConverter(targetClass);
					} else {
						return converter;
					}
				}
			};
			if (isBold()) {
				component.add(new AttributeModifier("style", "font-weight: bold;"));
			}
		}
		return HelperPanel.getInstance(componentId, component);
	}

	/**
	 * Update the text field component with any additional requirements
	 * 
	 * @param component
	 */
	protected void updateTextFieldComponent(SRSTextField component) {
		component.setOutputMarkupId(true);
		component.setOutputMarkupPlaceholderTag(true);
		component.setRequired(required);
		component.setLabel(headerModel);
		if (validator!=null) {
			component.add(validator);
		}
	}

	public boolean isBold() {
		return bold;
	}

	public void setBold(boolean bold) {
		this.bold = bold;
	}

	public boolean isRequired() {
		return required;
	}

	public void setRequired(boolean required) {
		this.required = required;
	}

	public IConverter getConverter() {
		return converter;
	}

	public void setConverter(IConverter converter) {
		this.converter = converter;
	}
	
}
