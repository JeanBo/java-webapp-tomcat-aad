package za.co.liberty.web.pages.admin;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidationError;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.ValidationError;

import za.co.liberty.dto.userprofiles.MenuItemDTO;
import za.co.liberty.helpers.util.SRSUtility;
import za.co.liberty.web.data.enums.ColumnStyleType;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.pages.BasePage;
import za.co.liberty.web.pages.interfaces.ISecurityPanel;
import za.co.liberty.web.pages.panels.FormRepeatingPanel;
import za.co.liberty.web.pages.panels.MaintenanceBasePanel;
import za.co.liberty.web.wicket.markup.html.form.SRSLabel;
import za.co.liberty.web.wicket.markup.html.form.SRSTextField;

/**
 * Menu Item Administration panel that manages the data.
 * 
 * @author jzb0608 - 24 Apr 2008
 * 
 */
@SuppressWarnings("unchecked")
public class MenuItemAdminPanel extends MaintenanceBasePanel {

	private static final long serialVersionUID = 5615959490908548660L;

	@SuppressWarnings("unused")
	private MenuItemDTO dto;
	
	private CheckBox isPanelBox;
	/**
	 * Default constructor
	 * 
	 */
	public MenuItemAdminPanel(String id, MenuItemDTO dto,
			EditStateType editState) {
		super(id, editState, dto);
		this.dto = dto;
	}

	/**
	 * Get the Menu Id field
	 * 
	 * @return
	 */
	private Component createMenuIDField() {
		return new SRSLabel("value", new Model(""
				+ ((MenuItemDTO) bean).getMenuItemID()), true, true)
				.setStyleColumn(ColumnStyleType.SMALL);

	}

	/**
	 * Get the Menu Name field
	 * 
	 * @return
	 */
	
	private Component createMenuNameField() {
		ColumnStyleType style = ColumnStyleType.DEFAULT;
		if (getEditState() == EditStateType.VIEW) {
			return new SRSLabel("value", new Model(((MenuItemDTO) bean)
					.getMenuItemName()), true).setStyleColumn(style);
		}
		return new SRSTextField("value",
				new PropertyModel(bean, "menuItemName")).setStyleColumn(style)
				.setRequired(true);
	}

	/**
	 * Get the Menu Description field
	 * 
	 * @return
	 */
	private Component createMenuDescriptionField() {
		ColumnStyleType style = ColumnStyleType.VERY_VERY_LARGE;
		if (getEditState() == EditStateType.VIEW) {
			return new SRSLabel("value", new Model(((MenuItemDTO) bean)
					.getMenuItemDescription()), true).setStyleColumn(style);
		}
		return new SRSTextField("value", new PropertyModel(bean,
				"menuItemDescription")).setStyleColumn(style).setRequired(true);
	}
	/**
	 * Get the Menu Long Description field
	 * 
	 * @return
	 */
	private Component createMenuLongDescriptionField() {
		ColumnStyleType style = ColumnStyleType.VERY_VERY_LARGE;
		if (getEditState() == EditStateType.VIEW) {
			return new SRSLabel("value", new Model(((MenuItemDTO) bean)
					.getMenuItemLongDescription()), true).setStyleColumn(style);
		}
		return new SRSTextField("value", new PropertyModel(bean,
				"menuItemLongDescription")).setStyleColumn(style).setRequired(true);
	}
	/**
	 * Get the Menu Implementation class field
	 * 
	 * @return
	 */
	private Component createImplementationClassField() {
		ColumnStyleType style = ColumnStyleType.VERY_VERY_LARGE;
		if (getEditState() == EditStateType.VIEW) {
			return new SRSLabel("value", new Model(((MenuItemDTO) bean)
					.getImplClazz()), true).setStyleColumn(style);
		}

		/**
		 * Validate that the class exist
		 */
		IValidator classValidator = new IValidator() {
			private static final long serialVersionUID = 1L;

			@SuppressWarnings("static-access")
			public void validate(IValidatable val) {				
				try {
					Class cls = this.getClass().forName((String) val.getValue());					
					//check if class is instance of BasePage					
					String value = isPanelBox.getValue();					
					if(value == null && !SRSUtility.extendsOrIsClass(cls,BasePage.class)){
						//it is a page
						IValidationError err = new ValidationError()
						//.addMessageKey("class.not.basepage");
						 .addKey("class.not.basepage");
						val.error(err);
					}
					else if(value != null && !SRSUtility.containsInterface(ISecurityPanel.class,cls)){
						//it is a panel						
						IValidationError err = new ValidationError()
						//.addMessageKey("class.not.securitypanel");
						.addKey("class.not.securitypanel");
						val.error(err);
					}					
				} catch (ClassNotFoundException e) {
					IValidationError err = new ValidationError()
							//.addMessageKey("class.notfound");
							.addKey("class.notfound");		
					val.error(err);
				}
			}
		};		
		return new SRSTextField("value", new PropertyModel(bean, "implClazz"))
				.setStyleColumn(style).setRequired(true).add(classValidator);
	}			

	/**
	 * Create a generic check box
	 * 
	 * @return
	 */
	private CheckBox createCheckBoxField(String field) {
		CheckBox box = new CheckBox("value", new PropertyModel(bean, field));
		if (getEditState() == EditStateType.VIEW) {
			box.setEnabled(false);
		}		
		return box;
	}

	/**
	 * Get the Menu is enabled field
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
	
	/**
	 * Get the Menu is a panel field
	 * 
	 * @return
	 */
	private Component createIsPanelField() {
		isPanelBox = new CheckBox("value", new PropertyModel(bean, "panel"));
		if (getEditState() == EditStateType.VIEW) {
			isPanelBox.setEnabled(false);
		}
		return isPanelBox;
	}

	@Override
	public RepeatingView createRepeatingField() {
		RepeatingView view = new RepeatingView(REPEATING_FIELD_NAME);
		int i = 0;
		if (getEditState() != EditStateType.ADD) {
			view.add(new FormRepeatingPanel(i++ + "", "Menu ID:",
					createMenuIDField()));
		}

		view.add(new FormRepeatingPanel(i++ + "", "Internal Menu Name:",
				createMenuNameField()));

		view.add(new FormRepeatingPanel(i++ + "", "Menu Bar Description:",
				createMenuDescriptionField()));
		
		view.add(new FormRepeatingPanel(i++ + "", "Menu Long Description:",
				createMenuLongDescriptionField()));
		
		view.add(new FormRepeatingPanel(i++ + "", "Implementation Class:",
				createImplementationClassField()));
		view.add(new FormRepeatingPanel(i++ + "", "Is a Panel:",
				createIsPanelField()));
		view.add(new FormRepeatingPanel(i++ + "", "Has Modify:",
				createCheckBoxField("isModifyAccess")));
		view.add(new FormRepeatingPanel(i++ + "", "Has Add:",
				createCheckBoxField("isAddAccess")));
		view.add(new FormRepeatingPanel(i++ + "", "Has Delete:",
				createCheckBoxField("isDeleteAccess")));
		view.add(new FormRepeatingPanel(i++ + "", "Enabled:",
						createEnabledField()));

		return view;
	}

}
