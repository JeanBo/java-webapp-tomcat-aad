package za.co.liberty.web.pages.admin;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import za.co.liberty.web.data.enums.ColumnStyleType;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.pages.admin.models.RulesModel;
import za.co.liberty.web.pages.panels.FormRepeatingPanel;
import za.co.liberty.web.pages.panels.HelperPanel;
import za.co.liberty.web.pages.panels.MaintenanceBasePanel;
import za.co.liberty.web.wicket.markup.html.form.SRSLabel;
import za.co.liberty.web.wicket.markup.html.form.SRSTextField;

/**
 * Rules Administration panel that manages the data.
 * 
 * @author jzb0608 - 05 May 2008
 * 
 */
public class RulesAdminPanel extends MaintenanceBasePanel {

	private static final long serialVersionUID = -9222355665504615629L;
	//private RulesModel model = null;
	private Component arith = null;
	
	private Component dataType = null;
	
	private Component ruleDataFromSystemCheckBox = null;

	/**
	 * Default constructor
	 * 
	 */
	public RulesAdminPanel(String id, RulesModel model, EditStateType editState) {
		super(id, editState, model);
		int arithNumber = 3;
				
		arith = ((HelperPanel)((FormRepeatingPanel)super.repeatingView.get("" + arithNumber++)).get("value")).get("value");
		if(!(arith instanceof DropDownChoice)){
			arith = ((HelperPanel)((FormRepeatingPanel)super.repeatingView.get("" + arithNumber++)).get("value")).get("value");
		}
		
		dataType = ((HelperPanel)((FormRepeatingPanel)super.repeatingView.get("" + arithNumber++)).get("value")).get("value");
		if(!(dataType instanceof DropDownChoice)){
			dataType = ((HelperPanel)((FormRepeatingPanel)super.repeatingView.get("" + arithNumber++)).get("value")).get("value");
		}
		
		ruleDataFromSystemCheckBox = ((HelperPanel)((FormRepeatingPanel)super.repeatingView.get("" + arithNumber++)).get("value")).get("value");
		if(!(ruleDataFromSystemCheckBox instanceof CheckBox)){
			ruleDataFromSystemCheckBox = ((HelperPanel)((FormRepeatingPanel)super.repeatingView.get("" + arithNumber++)).get("value")).get("value");
		}			
	}

	public RepeatingView createRepeatingField() {		
		RepeatingView view = new RepeatingView(REPEATING_FIELD_NAME);
		int i = 0;
		if (getEditState() != EditStateType.ADD) {
			view.add(new FormRepeatingPanel(i++ + "", "Rule ID:",
					createRuleIDField()));
		}

		view.add(new FormRepeatingPanel(i++ + "", "Rule Name:",
				createRuleNameField()));

		view.add(new FormRepeatingPanel(i++ + "", "Rule Description:",
				createRuleDescriptionField()));
		view.add(new FormRepeatingPanel(i++ + "", "Has Rule Data:",
				createHasRuleDataField()));		
		
		view.add(new FormRepeatingPanel(i++ + "", "Rule Arithmetic Default Type:",createArithmeticValueField()));
		view.add(new FormRepeatingPanel(i++ + "", "Rule Default Data Type:",createDataTypeField()));		
		view.add(new FormRepeatingPanel(i++ + "", "Rule Compare Value supplied by User:",
				createRuleDataValueSuppliedBySystemField()));
		view.add(new FormRepeatingPanel(i++ + "", "Enabled:",
				createEnabledField()));			
		return view;
	}

	/**
	 * Get the Menu Id field
	 * 
	 * @return
	 */
	private Component createRuleIDField() {
		return new SRSLabel("value", new Model(""
				+ ((RulesModel) bean).getSelectedItem().getRuleID()), true, true)
				.setStyleColumn(ColumnStyleType.SMALL);
	}

	/**
	 * Get the Rule Name field
	 * 
	 * @return
	 */
	private Component createRuleNameField() {
		ColumnStyleType style = ColumnStyleType.DEFAULT;
		if (getEditState() == EditStateType.VIEW) {
			return new SRSLabel("value", new Model(((RulesModel) bean).getSelectedItem()
					.getRuleName()), true).setStyleColumn(style);
		}
		return new SRSTextField("value", new PropertyModel(((RulesModel) bean).getSelectedItem(), "ruleName"))
				.setStyleColumn(style).setRequired(true).setLabel(
						new Model("Rule name"));
	}

	/**
	 * Get the Rule Description field
	 * 
	 * @return
	 */
	private Component createRuleDescriptionField() {
		ColumnStyleType style = ColumnStyleType.DEFAULT;
		if (getEditState() == EditStateType.VIEW) {
			return new SRSLabel("value", new Model(((RulesModel) bean).getSelectedItem().getRuleDescription()), true).setStyleColumn(style);
		}
		return new SRSTextField("value", new PropertyModel(((RulesModel) bean).getSelectedItem(),
				"ruleDescription")).setStyleColumn(style).setRequired(true)
				.setLabel(new Model("Rule description"));
	}
	
	/**
	 * Get the Arithmetic field
	 * 
	 * @return
	 */
	private DropDownChoice createArithmeticValueField() {
		DropDownChoice list = new DropDownChoice("value", new PropertyModel(((RulesModel) bean).getSelectedItem(),
		"arithmeticDefault"), ((RulesModel) bean).getArithmeticTypes(), new ChoiceRenderer(
				"description"));		
		//list.setRequired(true);		
		if (getEditState() == EditStateType.VIEW || getEditState() == EditStateType.ADD || !((RulesModel) bean).getSelectedItem().isHasRuleData()) {
			list.setEnabled(false);
		}
		list.setLabel(new Model("Rule Arithmetic Default Type"));
		list.setOutputMarkupId(true);
		return list;
	}
	
	/**
	 * Get the Data Type field
	 * 
	 * @return
	 */
	private DropDownChoice createDataTypeField() {
		DropDownChoice list = new DropDownChoice("value", new PropertyModel(((RulesModel) bean).getSelectedItem(),
		"dataTypeDefault"), ((RulesModel) bean).getDataTypes(), new ChoiceRenderer(
				"description"));		
		//list.setRequired(true);		
		if (getEditState() == EditStateType.VIEW || getEditState() == EditStateType.ADD || !((RulesModel) bean).getSelectedItem().isHasRuleData()) {
			list.setEnabled(false);
		}
		list.setLabel(new Model("Rule Default Data Type"));
		list.setOutputMarkupId(true);
		return list;
	}

	/**
	 * Get the Has Data field
	 * 
	 * @return
	 */
	private AjaxCheckBox createHasRuleDataField() {
		final boolean selec = ((RulesModel) bean).getSelectedItem().isHasRuleData();	
		final AjaxCheckBox box = new AjaxCheckBox("value", new PropertyModel(((RulesModel) bean).getSelectedItem(),"hasRuleData")){
			boolean selected = selec;
					@Override
					protected void onUpdate(AjaxRequestTarget target) {
						selected = !selected;						
						if(arith != null){						
							if(selected){							
								setRuleDataSelectionsEnables(true);
						//MSK:Change	addComponent()-->add()	
								target.add(arith);//target.addComponent(arith);
								target.add(dataType);//target.addComponent(dataType);
								target.add(ruleDataFromSystemCheckBox);//target.addComponent(ruleDataFromSystemCheckBox);
							}else{
								setRuleDataSelectionsEnables(false);
								target.add(arith);//target.addComponent(arith);
								target.add(dataType);//target.addComponent(dataType);
								target.add(ruleDataFromSystemCheckBox);//target.addComponent(ruleDataFromSystemCheckBox);
							}
						}
					}			
		};		
		if (getEditState() == EditStateType.VIEW) {
			box.setEnabled(false);
		}
		return box;
	}
	
	private void setRuleDataSelectionsEnables(boolean enabled){
		arith.setEnabled(enabled);
		dataType.setEnabled(enabled);
		ruleDataFromSystemCheckBox.setEnabled(enabled);
		((DropDownChoice)arith).setRequired(enabled);
		((DropDownChoice)dataType).setRequired(enabled);
	}
	
	/**
	 * Get the Has Data field
	 * 
	 * @return
	 */
	private CheckBox createRuleDataValueSuppliedBySystemField() {
		final CheckBox box = new CheckBox("value", new PropertyModel(((RulesModel) bean).getSelectedItem(),"ruleDataRetreivedBySystem"));		
		if (getEditState() == EditStateType.VIEW || getEditState() == EditStateType.ADD || !((RulesModel) bean).getSelectedItem().isHasRuleData()) {
			box.setEnabled(false);
		}
		box.setOutputMarkupId(true);		
		return box;
	}

	/**
	 * Get the Enabled field
	 * 
	 * @return
	 */
	private Component createEnabledField() {
		CheckBox box = new CheckBox("value", new PropertyModel(((RulesModel) bean).getSelectedItem(), "enabled"));
		if (getEditState() == EditStateType.VIEW) {
			box.setEnabled(false);
		}
		return box;
	}

}
