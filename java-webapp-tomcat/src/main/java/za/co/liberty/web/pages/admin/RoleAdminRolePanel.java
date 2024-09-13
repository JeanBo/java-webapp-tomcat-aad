package za.co.liberty.web.pages.admin;

import org.apache.wicket.Component;
//import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import za.co.liberty.dto.userprofiles.ProfileRoleDTO;
import za.co.liberty.web.data.enums.ColumnStyleType;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.pages.panels.FormRepeatingPanel;
import za.co.liberty.web.pages.panels.MaintenanceBasePanel;
import za.co.liberty.web.wicket.markup.html.form.SRSLabel;
import za.co.liberty.web.wicket.markup.html.form.SRSTextArea;
import za.co.liberty.web.wicket.markup.html.form.SRSTextField;

/**
 * Roles Administration panel that manages the data.
 * 
 * @author jzb0608 - 05 May 2008
 * 
 */
public class RoleAdminRolePanel extends MaintenanceBasePanel {

	private static final long serialVersionUID = -9222355665504615629L;

	/**
	 * Default constructor
	 * 
	 */
	public RoleAdminRolePanel(String id, ProfileRoleDTO dto,
			EditStateType editState) {
		super(id, editState, dto);
	}

	@Override
	public RepeatingView createRepeatingField() {
		RepeatingView view = new RepeatingView(REPEATING_FIELD_NAME);
		int i = 0;
		if (getEditState() != EditStateType.ADD) {
			view.add(new FormRepeatingPanel(i++ + "", "Role ID:",
					createRoleIDField()));
		}
		view.add(new FormRepeatingPanel(i++ + "", "Role Name:",
				createRoleNameField()));		
		view.add(new FormRepeatingPanel(i++ + "", "Role Short Description:",
				createRoleShortDescriptionField()));			
		view.add(new FormRepeatingPanel(i++ + "", "Role Long Description:",
				createRoleLongDescriptionField()));	
		view.add(new FormRepeatingPanel(i++ + "", "Enabled:",
				createEnabledField()));
		return view;
	}

	/**
	 * Get the Role Id field
	 * 
	 * @return
	 */
	private Component createRoleIDField() {
		return new SRSLabel("value", new Model(""
				+ ((ProfileRoleDTO) bean).getProfileRoleID()), true, true)
				.setStyleColumn(ColumnStyleType.SMALL);
	}

	/**
	 * Get the Role Name field
	 * 
	 * @return
	 */
	private Component createRoleNameField() {
		ColumnStyleType style = ColumnStyleType.DEFAULT;
		if (getEditState() == EditStateType.VIEW) {
			return new SRSLabel("value", new Model(((ProfileRoleDTO) bean)
					.getRoleName()), true).setStyleColumn(style);
		}
		SRSTextField field = new SRSTextField("value", new PropertyModel(bean,
				"roleName"));
		field.setLabel(new Model("Role Name"));
		return field.setStyleColumn(style).setRequired(true);
	}
	
	/**
	 * Get the Role short description field
	 * 
	 * @return
	 */
	private Component createRoleShortDescriptionField() {
		ColumnStyleType style = ColumnStyleType.DEFAULT;
		SRSTextArea field = new SRSTextArea("value", new PropertyModel(bean,
				"roleShortDescription"));
		field.setLabel(new Model("Role Short Description"));
		/*
		 * field.setStyleColumn(style).add(new SimpleAttributeModifier("cols","60"))
		 * .add(new SimpleAttributeModifier("rows","2"));
		 */
		field.setStyleColumn(style).add(new AttributeModifier("cols","60"))
		.add(new AttributeModifier("rows","2"));
		
		if (getEditState() == EditStateType.VIEW) {
			field.setRequired(false);
			field.setEnabled(false);
			field.setView(true);
			return field;
		}
		field.setRequired(true);
		field.setEnabled(true);
		field.setView(false);
		return field;
	}
	
	/**
	 * Get the Role long description field
	 * 
	 * @return
	 */
	private Component createRoleLongDescriptionField() {
		//ColumnStyleType style = ColumnStyleType.DEFAULT;
		SRSTextArea field = new SRSTextArea("value", new PropertyModel(bean,
		"roleLongDescription"));
		field.setLabel(new Model("Role Long Description"));		
		//field.setStyleColumn(style)
		/*
		 * field.add(new SimpleAttributeModifier("cols","60")) .add(new
		 * SimpleAttributeModifier("rows","3"));
		 */
		field.add(new AttributeModifier("cols","60"))
		.add(new AttributeModifier("rows","3"));
		if (getEditState() == EditStateType.VIEW) {			
			field.setEnabled(false);
			field.setView(true);
			return field;
		}		
		field.setEnabled(true);
		field.setView(false);
		return field;		
	}

	/**
	 * Create the enabled field
	 * 
	 * @return
	 */
	private Component createEnabledField() {
		CheckBox box = new CheckBox("value", new PropertyModel(bean, "enabled"));
		if (getEditState() == EditStateType.VIEW) {
			box.setEnabled(false);
		}
		return box;
	}
}
