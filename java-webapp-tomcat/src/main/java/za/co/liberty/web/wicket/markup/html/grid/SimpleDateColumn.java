package za.co.liberty.web.wicket.markup.html.grid;

import java.text.SimpleDateFormat;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.convert.IConverter;

import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.pages.panels.HelperPanel;
import za.co.liberty.web.wicket.convert.converters.FormattedDateConverter;
import za.co.liberty.web.wicket.markup.html.form.SRSDateField;

public abstract class SimpleDateColumn<T> extends SRSDataGridColumn<T> {
	
	public static final SimpleDateFormat FORMAT_DATE_ONLY = new SimpleDateFormat("dd/MM/yyyy");
	
	public static final SimpleDateFormat FORMAT_DATE_AND_TIME = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
	
	private SimpleDateFormat dateFormat;

	private boolean bold;
	
	private boolean required;
	
	private IModel headerModel;

	public SimpleDateColumn(SimpleDateFormat dateFormat, String columnId,
			IModel headerModel, String objectProperty, EditStateType state) {
		super(columnId, headerModel, objectProperty, state);
		this.dateFormat = dateFormat;
		this.headerModel=headerModel;
	}
	
	public abstract boolean isCellEditable(EditStateType state,T data);

	@Override
	public Panel newCellPanel(WebMarkupContainer parent, String componentId,
			IModel rowModel, String objectProperty, EditStateType state,
			T data) {
		if (isCellEditable(state, data)) {
			SRSDateField component = new SRSDateField("value", new PropertyModel(data, objectProperty));
			updateComponent(component);
			return HelperPanel.getInstance(componentId, component, true);
		} else {
			Label component = new Label("value", new PropertyModel(data, objectProperty)) {
				@Override
				public IConverter getConverter(Class targetClass) {
					return new FormattedDateConverter(dateFormat);
				}
			};
			if (isBold()) {
				component.add(new AttributeModifier("style", "font-weight: bold;"));
			}
			return HelperPanel.getInstance(componentId, component);
		}
	}

	protected void updateComponent(SRSDateField component) {
		component.add(new AttributeModifier("size","10"));
		component.add(new AttributeModifier("maxlength","10"));
		component.setRequired(required);
		component.setLabel(headerModel);
		component.setOutputMarkupId(true);
		component.setOutputMarkupPlaceholderTag(true);
		component.add(component.newDatePicker());
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

}
