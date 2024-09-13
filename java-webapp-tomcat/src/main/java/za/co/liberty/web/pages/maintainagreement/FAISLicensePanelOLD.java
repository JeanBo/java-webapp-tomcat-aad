package za.co.liberty.web.pages.maintainagreement;

import java.util.Locale;

import org.apache.wicket.Page;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.convert.IConverter;

import za.co.liberty.dto.agreement.AgreementDTO;
import za.co.liberty.dto.agreement.properties.LongTermInsuranceCategoryDTO;
import za.co.liberty.web.data.enums.ComponentType;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.data.enums.fields.AgreementGUIField;
import za.co.liberty.web.pages.interfaces.ISecurityPanel;
import za.co.liberty.web.pages.maintainagreement.model.FAISLicensePanelModelOLD;
import za.co.liberty.web.pages.panels.GUIFieldPanel;
import za.co.liberty.web.pages.panels.ViewTemplateBasePanel;
import za.co.liberty.web.wicket.convert.converters.YesNoBooleanConverter;
import za.co.liberty.web.wicket.markup.html.form.SRSAbstractChoiceRenderer;
import za.co.liberty.web.wicket.markup.html.form.SRSDropDownChoice;
import za.co.liberty.web.wicket.view.ContextDrivenViewTemplate;

/**
 * This panel contains all components necessary to render the 
 * FAIS License details and associated details
 * @author kxd1203
 *
 */
public class FAISLicensePanelOLD extends
		ViewTemplateBasePanel<AgreementGUIField, AgreementDTO> implements
		ISecurityPanel {

	private static final long serialVersionUID = -3527102887999213064L;

	private RepeatingView faisLicensePanel;

	private GUIFieldPanel licenseNumberComponent;

	private GUIFieldPanel faisLicenseEffectiveFromComponent;

	private FAISLicensePanelModelOLD panelModel;

	private boolean initialised;

	private GUIFieldPanel longTermInsuranceCategoryComponent;

	private RepeatingView faisComponentsPanel;

	private GUIFieldPanel participatoryComponent;

	private GUIFieldPanel healthBenefits;

	private GUIFieldPanel pensionFundBenefits;

	private GUIFieldPanel retailPensionFundBenefits;

	/**
	 * Custom renderer to render LongTermInsuranceCategoryDTO objects in a drop down
	 */
	IChoiceRenderer longTermInsCatRenderer = new SRSAbstractChoiceRenderer<Object>() {

		private static final long serialVersionUID = 1L;

		public Object getDisplayValue(Object object) {
			if (object == null) {
				return null;
			}
			return ((LongTermInsuranceCategoryDTO) object).getName();
		}

		public String getIdValue(Object object, int index) {
			return "" + index;
		}

	};

	/**
	 * View label converter for long term insurance category
	 */
	IConverter longTermInsCatConverterForViewLabel = new IConverter() {

		public Object convertToObject(String object, Locale locale) {
			return null;
		}

		public String convertToString(Object object, Locale locale) {
			if (object != null
					&& object instanceof LongTermInsuranceCategoryDTO) {
				return ((LongTermInsuranceCategoryDTO) object).getName();
			}
			return null;
		}

	};

	private AgreementDTO viewTemplateContext;

	public FAISLicensePanelOLD(String id, EditStateType editState,
			FAISLicensePanelModelOLD panelModel) {
		this(id, editState, panelModel, null);
	}

	public FAISLicensePanelOLD(String id, EditStateType editState,
			FAISLicensePanelModelOLD panelModel, Page parentPage) {
		super(id, editState, parentPage);
		initialised = false;
		this.panelModel = panelModel;
	}

	/**
	 * Set/update the panel model
	 * 
	 * This method can be used prior to rendering the panel to ensure
	 * that the model is up to date.
	 * 
	 * @param panelModel
	 */
	public void setPanelModel(FAISLicensePanelModelOLD panelModel) {
		this.panelModel = panelModel;
		/**
		 * When updating the model, reset the view template context
		 * so that it will be re-created
		 */
		this.viewTemplateContext = null;
	}

	@Override
	protected void onBeforeRender() {
		super.onBeforeRender();
		if (!initialised) {
			/**
			 * Only construct the component tree when the panel is
			 * rendered the first time
			 */
			if (panelModel == null) {
				throw new IllegalArgumentException(
						"FAIS Panel Model must be set prior to viewing the panel");
			}

			add(new FAISLicenseForm("faisLicenseForm"));
			initialised = true;

		}
	}

	public class FAISLicenseForm extends Form {
		private static final long serialVersionUID = 5808296649559984427L;

		public FAISLicenseForm(String id) {
			super(id);

			add(getFAISLicensePanel());
			add(getFAISComponentsPanel());
		}
	}

	public RepeatingView getFAISLicensePanel() {
		if (faisLicensePanel == null) {
			faisLicensePanel = new RepeatingView("faisLicensePanel");
			faisLicensePanel.add(getLicenseNumberComponent());
			faisLicensePanel.add(getFaisLicenseEffectiveDatePanel());
			faisLicensePanel.add(getLongTermInsuranceCategoryComponent());

		}
		return faisLicensePanel;
	}

	public RepeatingView getFAISComponentsPanel() {
		if (faisComponentsPanel == null) {
			faisComponentsPanel = new RepeatingView("faisComponentsPanel");
			faisComponentsPanel.add(getParticipatoryComponent());
			faisComponentsPanel.add(getHealthBenefitsComponent());
			faisComponentsPanel.add(getPensionBenefitsComponent());
			faisComponentsPanel.add(getRetailPensionBenefitsComponent());
		}
		return faisComponentsPanel;
	}

	private GUIFieldPanel getRetailPensionBenefitsComponent() {
		if (retailPensionFundBenefits == null) {
			PropertyModel propertyModel = new PropertyModel(
					getPropertyModelTarget(),
					"faisLicenseDTO.retailPensionBenefit");
			CheckBox retailPensionFundBox = new CheckBox("value", propertyModel);
			Label viewLabel = new Label("value", propertyModel) {
				@Override
				public IConverter getConverter(Class arg0) {
					return new YesNoBooleanConverter();
				}
			};
			AgreementGUIField field = AgreementGUIField.FAIS_RETAIL_PENSION_BENFIT;
			retailPensionFundBenefits = createGUIFieldPanel(field,
					createGUIPageField(field, getPropertyModelTarget(),
							retailPensionFundBox, viewLabel)
							.getEnclosedObject());
		}
		return retailPensionFundBenefits;
	}

	private GUIFieldPanel getPensionBenefitsComponent() {
		if (pensionFundBenefits == null) {
			PropertyModel propertyModel = new PropertyModel(
					getPropertyModelTarget(), "faisLicenseDTO.pensionBenefit");
			CheckBox pensionFundBox = new CheckBox("value", propertyModel);
			Label viewLabel = new Label("value", propertyModel) {
				@Override
				public IConverter getConverter(Class arg0) {
					return new YesNoBooleanConverter();
				}
			};
			AgreementGUIField field = AgreementGUIField.FAIS_PENSION_BENFIT;
			pensionFundBenefits = createGUIFieldPanel(field,
					createGUIPageField(field, getPropertyModelTarget(),
							pensionFundBox, viewLabel).getEnclosedObject());
		}
		return pensionFundBenefits;
	}

	private GUIFieldPanel getHealthBenefitsComponent() {
		if (healthBenefits == null) {
			PropertyModel propertyModel = new PropertyModel(
					getPropertyModelTarget(), "faisLicenseDTO.healthBenefit");
			CheckBox healthBenefitsBox = new CheckBox("value", propertyModel);
			Label viewLabel = new Label("value", propertyModel) {
				@Override
				public IConverter getConverter(Class arg0) {
					return new YesNoBooleanConverter();
				}
			};
			AgreementGUIField field = AgreementGUIField.FAIS_HEALTH_BENEFIT;
			healthBenefits = createGUIFieldPanel(field, createGUIPageField(
					field, getPropertyModelTarget(), healthBenefitsBox,
					viewLabel).getEnclosedObject());
		}
		return healthBenefits;
	}

	private GUIFieldPanel getParticipatoryComponent() {
		if (participatoryComponent == null) {
			PropertyModel propertyModel = new PropertyModel(
					getPropertyModelTarget(),
					"faisLicenseDTO.collectiveInvestmentParticip");
			CheckBox particCheckBox = new CheckBox("value", propertyModel);
			Label viewLabel = new Label("value", propertyModel) {
				@Override
				public IConverter getConverter(Class arg0) {
					return new YesNoBooleanConverter();
				}
			};
			AgreementGUIField field = AgreementGUIField.FAIS_PARTIC_COLL_INVESTMENTS;
			participatoryComponent = createGUIFieldPanel(field,
					createGUIPageField(field, getPropertyModelTarget(),
							particCheckBox, viewLabel).getEnclosedObject());
		}
		return participatoryComponent;
	}

	private GUIFieldPanel getLongTermInsuranceCategoryComponent() {
		if (longTermInsuranceCategoryComponent == null) {
			PropertyModel propertyModel = new PropertyModel(
					getPropertyModelTarget(),
					"faisLicenseDTO.longTermInsuranceCategory");

			SRSDropDownChoice dropDownChoice = new SRSDropDownChoice("value",
					propertyModel, panelModel.getValidFaisLicenseValues()
							.getValidLongTermInsuranceCategory(),
					longTermInsCatRenderer, "Select");
			Label viewLabel = new Label("value", propertyModel) {
				@Override
				public IConverter getConverter(Class arg0) {
					return longTermInsCatConverterForViewLabel;
				}
			};
			AgreementGUIField field = AgreementGUIField.FAIS_LICENSE_CATEGORY;
			longTermInsuranceCategoryComponent = createGUIFieldPanel(field,
					createGUIPageField(field, getPropertyModelTarget(),
							dropDownChoice, viewLabel).getEnclosedObject());
		}
		return longTermInsuranceCategoryComponent;
	}

	private GUIFieldPanel getFaisLicenseEffectiveDatePanel() {

		if (faisLicenseEffectiveFromComponent == null) {
			AgreementGUIField field = AgreementGUIField.FAIS_LICENSE_EFFECTIVE_DATE;
			faisLicenseEffectiveFromComponent = createGUIFieldPanel(field,
					null, createGUIPageField(field, getPropertyModelTarget(),
							ComponentType.DATE_SELECTION_TEXTFIELD, false)
							.getEnclosedObject(), true);
		}

		return faisLicenseEffectiveFromComponent;
	}

	private GUIFieldPanel getLicenseNumberComponent() {
		if (licenseNumberComponent == null) {
			AgreementGUIField field = AgreementGUIField.FAIS_LICENSE_NUMBER;
			licenseNumberComponent = createGUIFieldPanel(field,
					createGUIPageField(field, getPropertyModelTarget(),
							ComponentType.TEXTFIELD, false).getEnclosedObject());
		}
		return licenseNumberComponent;
	}

	private FAISLicensePanelModelOLD getPropertyModelTarget() {
		return panelModel;
	}

	@Override
	protected ContextDrivenViewTemplate<AgreementGUIField, AgreementDTO> getViewTemplate() {
		return panelModel.getViewTemplate();
	}

	@Override
	protected AgreementDTO getViewTemplateContext() {
		if (viewTemplateContext == null) {
			viewTemplateContext = new AgreementDTO();
		}
		return viewTemplateContext;
	}

	public Class getPanelClass() {
		return getClass();
	}

}
