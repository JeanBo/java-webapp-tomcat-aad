package za.co.liberty.web.pages.maintainagreement.payroll;

import java.util.ArrayList;
import java.util.Arrays;
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
import za.co.liberty.interfaces.agreements.FrequencyType;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.pages.panels.HelperPanel;
import za.co.liberty.web.wicket.markup.html.form.SRSDropDownChoice;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGridColumn;
import za.co.liberty.web.wicket.renderer.FrequencyTypeChoiceRenderer;

public class PayrollFrequencyColumn extends SRSDataGridColumn<FixedPayrollDTO> {

	private static final long serialVersionUID = -5649299725696158608L;
	
	private static final String MODIFY_PROPERTY = "frequency";
	
	private static final String VIEW_PROPERTY = "frequency.key";
	
	private IModel headerModel;

	public PayrollFrequencyColumn(String columnId, IModel headerModel,
				EditStateType state) {
		super(columnId, headerModel, MODIFY_PROPERTY, state);
		this.headerModel=headerModel;
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
					getValidFrequencyType(),
					(IChoiceRenderer)new FrequencyTypeChoiceRenderer(),
					null);
			component.add(new AjaxFormComponentUpdatingBehavior("change") {
				@Override
				protected void onUpdate(AjaxRequestTarget arg0) {
					System.out.println("#JB-frequency updated to " + data.getFrequency()
							+ "   - ");
				}
			});
			((SRSDropDownChoice)component).setLabel(headerModel);
			((SRSDropDownChoice)component).setRequired(true);
		} else {
			component = new Label("value",
					new PropertyModel(data,VIEW_PROPERTY));
		}
		return HelperPanel.getInstance(componentId, component);
	}

	private List<FrequencyType> getValidFrequencyType() {
		
		List<FrequencyType> ret = new ArrayList<FrequencyType>();
//		ret.add(null);
		ret.addAll(Arrays.asList(FrequencyType.values()));
		return ret;
	}
	

}
