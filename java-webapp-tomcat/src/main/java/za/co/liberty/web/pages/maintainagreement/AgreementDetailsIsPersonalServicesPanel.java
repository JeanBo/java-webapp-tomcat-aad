package za.co.liberty.web.pages.maintainagreement;

import java.util.Calendar;
import java.util.Date;

import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.form.AjaxFormValidatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.validation.IValidator;

import za.co.liberty.business.guicontrollers.agreement.IAgreementGUIController;
import za.co.liberty.dto.agreement.AgreementDTO;
import za.co.liberty.dto.agreement.properties.TemporalPropertyDTO;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.helpers.lookup.ServiceLocator;
import za.co.liberty.srs.util.DateUtil;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.data.enums.fields.AgreementGUIField;
import za.co.liberty.web.pages.maintainagreement.model.AgreementDetailsPanelModel;
import za.co.liberty.web.pages.panels.HelperPanel;
import za.co.liberty.web.wicket.convert.converters.SRSDateConverter;

/**
 * A mini panel used in the GUI panel field on agreement details.
 * 
 * @author
 *
 */
public class AgreementDetailsIsPersonalServicesPanel extends Panel {

	private static final long serialVersionUID = 8291156270559784238L;

	private static final IValidator<Boolean> AjaxCheckBox = null;

	AgreementDetailsPanelModel panelModel = null;
	EditStateType editState;
	CheckBox isPSPCheckBox;
	// Needed to facilitate add and end logic.

	boolean propertySelected = false;
	boolean propertyOriginalValue = false;
	Date originalStartDate = null;
	Date originalEndDate = null;
	private transient IAgreementGUIController guiController;

	public AgreementDetailsIsPersonalServicesPanel(String id, AgreementDetailsPanelModel panelModel,
			EditStateType editState) {
		super(id);
		this.panelModel = panelModel;
		this.editState = editState;
		TemporalPropertyDTO<Boolean> originalProperty = panelModel.getAgreement().getPersonalServicesCompany();
		originalStartDate = originalProperty.getEffectiveFrom();
		originalEndDate = originalProperty.getEffectiveTo();
		if (originalProperty.getValue() == null) {
			// never been set
			propertySelected = true;
		} else if (originalProperty.getValue().equals(Boolean.TRUE)) {
			propertyOriginalValue = true;
		}

		Logger.getLogger(this.getClass())
				.debug("Original Property = " + originalProperty.getValue() + "  - propOrigVal=" + propertyOriginalValue
						+ "   - propertyUnselected=" + propertySelected + "  effFrom = " + originalStartDate
						+ "  effTo = " + originalEndDate);

		if (!editState.isViewOnly()) {
			// Calculate the combo values.
			// TODO
		}
		add(createPSPCheck("pspCheck"));
	}

	/**
	 * Create the has PSP checkbox
	 * 
	 * @param id
	 * @return
	 */
	public CheckBox createPSPCheck(String id) {
		@SuppressWarnings("unchecked")
		CheckBox checkBox = new CheckBox(id,
				new PropertyModel(panelModel.getAgreement(), AgreementGUIField.ISPERSONALSERVICESTRUST.getFieldId()));
		checkBox.setOutputMarkupId(true);
		checkBox.setOutputMarkupPlaceholderTag(true);

		// Get the original property and determine if tick for PSC should be
		// there or not
		TemporalPropertyDTO<Boolean> originalProperty = (panelModel.getAgreement().getPersonalServicesCompany());
		if (originalProperty.getValue() != null && originalProperty.getValue() == (true)) {
			// Tick box value to true if property = true
			checkBox.setModelObject(true);
		}
		if (originalProperty.getValue() != null) {
			// Get the original property and determine if tick for PST should be
			// there or not
			TemporalPropertyDTO<Boolean> originalPSTProperty = panelModel.getAgreement().getPersonalServicesTrust();
			if (originalPSTProperty.getValue() != null && originalPSTProperty.getValue() == (true)) {
				// Tick box value to true if property = true
				checkBox.setModelObject(true);
			}

		}

		checkBox.setEnabled(false);
		return checkBox;
	}

	/**
	 * Create the request kind type field
	 * 
	 * @param string
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private WebMarkupContainer createDateCombo(String id) {

		if (editState.isViewOnly() || isNoChangeInValue()) {
			EmptyPanel p = new EmptyPanel(id);
			p.setOutputMarkupId(true);
			return p;
		}

		/**
		 * The combo selection fluctuates depending on the original state of the
		 * property. if original true then end date is selected if original
		 * false then start date is selected
		 */
		IModel model = new IModel() {
			private static final long serialVersionUID = 1L;
			Date date = null;
			// DateUtil.getInstance().getTodayDatePart();

			public Object getObject() {
				return (propertyOriginalValue) ? panelModel.getAgreement().getPersonalServicesCompany().getEffectiveTo()
						: panelModel.getAgreement().getPersonalServicesCompany().getEffectiveFrom();
				// return date;
			}

			public void setObject(Object arg0) {
				if (propertyOriginalValue) {
					panelModel.getAgreement().getPersonalServicesCompany().setEffectiveTo(((Date) arg0));
				} else {
					panelModel.getAgreement().getPersonalServicesCompany().setEffectiveFrom(((Date) arg0));
				}
			}

			public void detach() {
			}
		};

		DropDownChoice field = new DropDownChoice("value", model,
				panelModel.getValidAgreementValues().getValidHasMedicalDates()) {
			@Override
			public boolean isEnabled() {
				return !editState.isViewOnly();
			}
		};

		field.setOutputMarkupId(true);
		field.setNullValid(true);
		field.add(new AjaxFormComponentUpdatingBehavior("change") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
			}
		});
		HelperPanel panel = HelperPanel.getInstance(id, field);
		panel.setOutputMarkupId(true);
		return panel;
	}

	/**
	 * True if there is no change
	 * 
	 * @return
	 */
	private boolean isNoChangeInValue() {
		Boolean newValue = panelModel.getAgreement().getPersonalServicesCompany().getValue();
		if (newValue == null) {
			newValue = false;
		}
		return ((Boolean) propertyOriginalValue).equals(newValue);
	}

	/**
	 * Load the AgreementGUIController dynamically if it is null as this is a
	 * transient variable.
	 * 
	 * @return {@link IAgreementGUIController}
	 */
	private IAgreementGUIController getGuiController() {
		if (guiController == null) {
			try {
				guiController = ServiceLocator.lookupService(IAgreementGUIController.class);
			} catch (NamingException e) {
				throw new CommunicationException("Could not lookup AgreementGUIController", e);

			}
		}
		return guiController;
	}
}