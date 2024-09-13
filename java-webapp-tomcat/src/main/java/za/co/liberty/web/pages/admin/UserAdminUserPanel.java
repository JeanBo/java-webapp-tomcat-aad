package za.co.liberty.web.pages.admin;

import org.apache.wicket.Component;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.validation.validator.StringValidator;

import za.co.liberty.dto.userprofiles.PartyProfileDTO;
import za.co.liberty.web.data.enums.ColumnStyleType;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.pages.panels.MaintenanceBasePanel;
import za.co.liberty.web.wicket.markup.html.form.SRSLabel;
import za.co.liberty.web.wicket.markup.html.form.SRSTextField;

/**
 * User Administration panel that manages the main 
 * user data.
 * 
 * @author jzb0608 - 14 May 2008
 * 
 */
public class UserAdminUserPanel extends MaintenanceBasePanel {

	private static final long serialVersionUID = -4790466882543161783L;
	

	/**
	 * Default constructor
	 * 
	 */
	public UserAdminUserPanel(String id, PartyProfileDTO dto,
			EditStateType editState) {
		super(id, editState, dto);
		this.bean = dto;
	}


	@Override
	public RepeatingView createRepeatingField() {
		RepeatingView view = new RepeatingView(REPEATING_FIELD_NAME);
		if (getEditState() != EditStateType.ADD) {
			view.add(createFormComponent("Profile OID:",
					createProfileIDField()));
			view.add(createFormComponent("Party OID:",
					createPartyIDField()));
		}
		view.add(createFormComponent("Security ID:",
				createSecurityIDField()));

		return view;
	}

	/**
	 * Get the Profile Id field
	 * 
	 * @return
	 */
	private Component createProfileIDField() {
		return new SRSLabel("value",
				new Model("" + ((PartyProfileDTO)bean).getProfileOID()), true, true)
				.setStyleColumn(ColumnStyleType.SMALL);
	}
	
	/**
	 * Get the Party Id field
	 * 
	 * @return
	 */
	private Component createPartyIDField() {
		return new SRSLabel("value",
				new Model("" + ((PartyProfileDTO)bean).getPartyOID()), true, true)
				.setStyleColumn(ColumnStyleType.SMALL);
	}

	/**
	 * Get the Security ID field
	 * 
	 * @return
	 */
	private Component createSecurityIDField() {
		ColumnStyleType style = ColumnStyleType.DEFAULT;
		if (getEditState() == EditStateType.VIEW) {
			return new SRSLabel("value", new Model(((PartyProfileDTO)bean).getSecurityID()),
					true).setStyleColumn(style);
		}
		SRSTextField field = new SRSTextField("value", new PropertyModel(
				bean, "securityID"));
		field.setLabel(new Model("Security ID"));
		field.setStyleColumn(style).setRequired(true);
		field.add(StringValidator.minimumLength(6));
		return field;
	}
}
