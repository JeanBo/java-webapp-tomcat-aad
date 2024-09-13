package za.co.liberty.web.pages.maintainagreement.payroll;

import java.text.SimpleDateFormat;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

import za.co.liberty.dto.agreement.payroll.FixedPayrollDTO;
import za.co.liberty.dto.spec.TypeDTO;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.pages.panels.HelperPanel;
import za.co.liberty.web.wicket.markup.html.form.SRSDropDownChoice;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGridColumn;
import za.co.liberty.web.wicket.renderer.TypeDTOChoiceRenderer;

public class PayrollTypeColumn extends SRSDataGridColumn<FixedPayrollDTO> {

	private static final long serialVersionUID = -5649299725696158608L;
	
	private static final String MODIFY_PROPERTY = "type";
	
	private static final String VIEW_PROPERTY = "type.description";
	
	private List<TypeDTO> choices;

		public PayrollTypeColumn(String columnId, IModel headerModel, 
			List<TypeDTO> choices, EditStateType state) {
		super(columnId, headerModel, MODIFY_PROPERTY, state);
		this.choices = choices;
	}

	@Override
	public Panel newCellPanel(WebMarkupContainer parent, String componentId, 
			IModel rowModel, String objectProperty, EditStateType state, FixedPayrollDTO data) {
		Component component = null;
		/**
		 * Modify only 
		 */
		if (PayrollColumnHelper.isCellEditable(state, data)) {
			/**
			 * Create a new drop down list
			 */
			component = new SRSDropDownChoice(
					"value",
					new PropertyModel(data,objectProperty),
					choices,
					(IChoiceRenderer)new TypeDTOChoiceRenderer(),
					null);
			component.add(new AjaxFormComponentUpdatingBehavior("change") {
				@Override
				protected void onUpdate(AjaxRequestTarget arg0) {
				}
			});
			((SRSDropDownChoice)component).setRequired(true);
		} else {
			component = new Label("value",
					new PropertyModel(data,VIEW_PROPERTY));
		}
		return HelperPanel.getInstance(componentId, component);
	}
	
	
	
}
