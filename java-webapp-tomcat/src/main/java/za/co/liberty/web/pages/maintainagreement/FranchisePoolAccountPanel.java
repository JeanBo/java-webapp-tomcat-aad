package za.co.liberty.web.pages.maintainagreement;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.SerializationUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormChoiceComponentUpdatingBehavior;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.RadioChoice;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.convert.IConverter;

import za.co.liberty.common.domain.Percentage;
import za.co.liberty.dto.agreement.AgreementDTO;
import za.co.liberty.dto.agreement.properties.FranchisePoolAccountDTO;
import za.co.liberty.web.data.enums.ComponentType;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.data.enums.fields.AgreementGUIField;
import za.co.liberty.web.pages.maintainagreement.model.FranchisePoolAccountPanelModel;
import za.co.liberty.web.pages.panels.GUIFieldPanel;
import za.co.liberty.web.pages.panels.HelperPanel;
import za.co.liberty.web.pages.panels.ViewTemplateBasePanel;
import za.co.liberty.web.wicket.convert.converters.agreement.FranchisePoolTransferPercentageConverter;
import za.co.liberty.web.wicket.renderer.YesNoBooleanChoiceRenderer;
import za.co.liberty.web.wicket.view.ContextDrivenViewTemplate;

/**
 * This class represents the UI for the Franchise Pool Account details
 * @author kxd1203
 *
 */
public class FranchisePoolAccountPanel extends ViewTemplateBasePanel<AgreementGUIField, AgreementDTO> {

	private FranchisePoolAccountPanelModel panelModel;
	private RepeatingView franchisePoolAccountLeftPanel;
	private RepeatingView franchisePoolAccountRightPanel;
	private boolean initialised;
	private GUIFieldPanel poolAccountEffectiveDatePanel;
	private GUIFieldPanel poolTransferPercentageEffectiveDateComponent;
	private GUIFieldPanel poolAccountInterestEffectiveDate;
	private GUIFieldPanel poolAccountInterestRateComponent;
	private GUIFieldPanel poolTransferPercentageComponent;
	private AgreementDTO viewTemplateContext;
	private GUIFieldPanel createPoolAccountComponent;
	protected IConverter franchisePoolTransferPercentageConverter;
	private RepeatingView franchisePoolAccountTopPanel;
	private WebMarkupContainer contentMarkup;

	public FranchisePoolAccountPanel(String id, EditStateType editState, 
			FranchisePoolAccountPanelModel panelModel) {
		super(id, editState);
		this.panelModel = panelModel;
	}
	
	@Override
	protected boolean isProcessOutstandingRequestsAllowed() {
		return false;
	}

	@Override
	protected void onBeforeRender() {
		super.onBeforeRender();
		if (!initialised) {
			add(new franchisePoolForm("franchisePoolAccountForm"));
			initialised=true;
		}
	}
	
	/**
	 * This class represents the behaviour that will modify dates related to percentages
	 * so that when a percentage value is changed to a new value a new date will be 
	 * set for the related date field, and when the value is changed to the existing value
	 * the original date will be set for the related date field
	 * 
	 * @author kxd1203
	 *
	 */
	private class FranchisePoolDateBehavior extends AjaxFormComponentUpdatingBehavior {
		
		private final FranchisePoolAccountDTO originalValue;
		
		private final FranchisePoolAccountDTO franchisePoolAccount;

		public FranchisePoolDateBehavior(String type,FranchisePoolAccountDTO franchisePoolAccount) {
			super(type);
			this.franchisePoolAccount=franchisePoolAccount;
			this.originalValue=(FranchisePoolAccountDTO) SerializationUtils.clone(franchisePoolAccount);
		}

		@Override
		protected void onUpdate(AjaxRequestTarget target) {
			if (franchisePoolAccount!=null && originalValue!=null) {
				Date poolAccInterestEffDate = null;
				Date poolAccTransferEffDate = null;
				if (equals(
						franchisePoolAccount.getPoolAccountInterestRate(),
						originalValue.getPoolAccountInterestRate())) {
					poolAccInterestEffDate = 
						originalValue.getPoolAccountInterestRateEffectiveDate();
				} else {
					poolAccInterestEffDate = new Date();
				}
				if (equals(franchisePoolAccount.getPoolTransferPercentage(),
						originalValue.getPoolTransferPercentage())) {
					poolAccTransferEffDate = 
						originalValue.getPoolTransferPercentageEffectiveDate();
				} else {
					poolAccTransferEffDate = new Date();
				}
				franchisePoolAccount.setPoolAccountInterestRateEffectiveDate(
						poolAccInterestEffDate);
				franchisePoolAccount.setPoolTransferPercentageEffectiveDate(
						poolAccTransferEffDate);
				target.add(getPoolAccountInterestEffectiveDateComponent());
				target.add(getPoolTransferPercentageEffectiveDateComponent());
			}
		}
		
		private boolean equals(Percentage value1,Percentage value2) {
			boolean ret = false;
			if (value1==null) { 
				if (value2==null) {
					ret = true;
				} else {
					ret = false;
				}
			} else {
				ret=value1.equals(value2);
			}
			return ret;
		}
		
	}
	
	private class franchisePoolForm extends Form {

		public franchisePoolForm(String id) {
			super(id);
			add(getFranchisePoolLeftPanel());
			add(getFranchisePoolRightPanel());
			checkFieldVisibility();
		}
	}
	
	public RepeatingView getFranchisePoolLeftPanel() {
		if (franchisePoolAccountLeftPanel == null) {
			franchisePoolAccountLeftPanel = new RepeatingView("franchisePoolAccountLeftPanel");
			franchisePoolAccountLeftPanel.add(getCreatePoolAccountComponent());
			franchisePoolAccountLeftPanel.add(getPoolAccountEffectiveDateComponent());
			franchisePoolAccountLeftPanel.add(getPoolTransferPercentageComponent());
			franchisePoolAccountLeftPanel.add(getPoolTransferPercentageEffectiveDateComponent());
		}
		return franchisePoolAccountLeftPanel;
	}

	public RepeatingView getFranchisePoolRightPanel() {
		if (franchisePoolAccountRightPanel == null) {
			franchisePoolAccountRightPanel = new RepeatingView("franchisePoolAccountRightPanel");
			franchisePoolAccountRightPanel.add(getPoolAccountInterestRateComponent());
			franchisePoolAccountRightPanel.add(getPoolAccountInterestEffectiveDateComponent());
		}
		return franchisePoolAccountRightPanel;
	}
	
	private GUIFieldPanel getCreatePoolAccountComponent() {
		if (createPoolAccountComponent == null) {
			AgreementGUIField field = AgreementGUIField.FRANCHISE_CREATE_POOL_ACCOUNT;
			List<Boolean> validBooleanValues = Arrays.asList(new Boolean[] { false,true });
			PropertyModel propertyModel = new PropertyModel(getContext(),field.getFieldId());
			RadioChoice createPoolAccountChoice = new RadioChoice(
					"panel", propertyModel, validBooleanValues, new YesNoBooleanChoiceRenderer());
			EditStateType[] allowedStates = getViewTemplate().getEditStates(field, 
					getViewTemplateContext());
			List<EditStateType> allowedStateList = Arrays.asList(allowedStates);
			createPoolAccountChoice.setEnabled(allowedStateList.contains(getEditState()));
			createPoolAccountChoice.setRequired(getViewTemplate().isRequired(
					field, 
					getViewTemplateContext()));
			createPoolAccountChoice.setLabel(new Model(field.getDescription()));
			final RadioGroup gp = new RadioGroup("JippoGroup");			
			Radio radio = new Radio("radio1",propertyModel){
				private static final long serialVersionUID = 1L;
				@Override
				protected RadioGroup getGroup() {					
					return gp;
				}
			};
			radio.setEnabled(allowedStateList.contains(getEditState()));
			radio.setOutputMarkupId(true);
			radio.setOutputMarkupPlaceholderTag(true);
			
			createPoolAccountChoice.add(radio);
			createPoolAccountChoice.setOutputMarkupId(true);
			createPoolAccountChoice.setOutputMarkupPlaceholderTag(true);
			createPoolAccountChoice.add(new AjaxFormChoiceComponentUpdatingBehavior() {
				@Override
				protected void onUpdate(AjaxRequestTarget target) {
					updatePoolAccountChoice(target);
				}
			});
			Label label = getGuiFieldLabel(field, null);
			getLabels().put(field,label);
			getFields().put(field,createPoolAccountChoice);
//			Label label = new Label("label",new Model(field.getDescription()));
			createPoolAccountComponent = new GUIFieldPanel(field.getFieldId(),label,createPoolAccountChoice);
			createPoolAccountComponent.setOutputMarkupId(true);
			createPoolAccountComponent.setOutputMarkupPlaceholderTag(true);
		}
		return createPoolAccountComponent;
	}

	private FranchisePoolAccountDTO getContext(){
		return panelModel.getFranchisePoolAccount();
	}
	
	private void updatePoolAccountChoice(AjaxRequestTarget target) {
		checkFieldVisibility();
		target.add(getPoolAccountEffectiveDateComponent());
		target.add(getPoolAccountInterestEffectiveDateComponent());
		target.add(getPoolAccountInterestRateComponent());
		target.add(getPoolTransferPercentageComponent());
		target.add(getPoolTransferPercentageEffectiveDateComponent());
	}
	
	private GUIFieldPanel getPoolAccountInterestRateComponent() {
		if (poolAccountInterestRateComponent == null) {
			poolAccountInterestRateComponent = getPercentageGUIField(AgreementGUIField.FRANCHISE_POOL_INTEREST_RATE);
			poolAccountInterestRateComponent.setOutputMarkupId(true);
			poolAccountInterestRateComponent.setOutputMarkupPlaceholderTag(true);
			if (poolAccountInterestRateComponent.getComponent() instanceof HelperPanel &&
					((HelperPanel)poolAccountInterestRateComponent.getComponent()).getEnclosedObject() instanceof TextField ) { 
					TextField textField = (TextField) 
						((HelperPanel)poolAccountInterestRateComponent
									.getComponent()).getEnclosedObject();
					textField.add(new FranchisePoolDateBehavior(
							"change",
							panelModel.getFranchisePoolAccount()));
					textField.setOutputMarkupId(true);
					textField.setOutputMarkupPlaceholderTag(true);
					
			}
		}
		return poolAccountInterestRateComponent;
	}

	private GUIFieldPanel getPoolAccountEffectiveDateComponent() {
		if (poolAccountEffectiveDatePanel == null) {
			poolAccountEffectiveDatePanel = 
				getDateGUIField(AgreementGUIField.FRANCHISE_POOL_ACCOUNT_EFFECTIVE_DATE);
			poolAccountEffectiveDatePanel.setOutputMarkupId(true);
			poolAccountEffectiveDatePanel.setOutputMarkupPlaceholderTag(true);
		}
		return poolAccountEffectiveDatePanel;
	}
	
	/**
	 * Pool transfer percentage is stored as follows in the database:
	 * GUI VALUE	DB VALUE
	 * 50%			50.00
	 * 12%			12.00
	 * 
	 * This is different to the standard percentage storage, and must use custom converters.
	 * @return
	 */
	private GUIFieldPanel getPoolTransferPercentageComponent() {
		if (poolTransferPercentageComponent == null) {
			AgreementGUIField field = AgreementGUIField.FRANCHISE_POOL_TRANSFER_PERCENTAGE;
			PropertyModel propertyModel = new PropertyModel(getContext(),field.getFieldId());
			TextField text = new TextField("value",propertyModel) {
				@Override
				public IConverter getConverter(Class arg0) {
					return getFranchisePoolTransferPercentageConverter();
				}
			};
			text.add(new FranchisePoolDateBehavior(
					"change",
					panelModel.getFranchisePoolAccount()));
			text.setOutputMarkupId(true);
			text.setOutputMarkupPlaceholderTag(true);
			text.setRequired(getViewTemplate().isRequired(field, getViewTemplateContext()));
			Label viewLabel = new Label("value",propertyModel) {
				@Override
				public IConverter getConverter(Class arg0) {
					return getFranchisePoolTransferPercentageConverter();
				}
			};
			HelperPanel ret = 
				createGUIPageField(field, getContext(), text, viewLabel);
			poolTransferPercentageComponent = createGUIFieldPanel(field, ret.getEnclosedObject()); 
			poolTransferPercentageComponent.setOutputMarkupId(true);
			poolTransferPercentageComponent.setOutputMarkupPlaceholderTag(true);
		}
		return poolTransferPercentageComponent;
	}
	
	protected IConverter getFranchisePoolTransferPercentageConverter() {
		if (franchisePoolTransferPercentageConverter == null) {
			franchisePoolTransferPercentageConverter = 
				new FranchisePoolTransferPercentageConverter();
		}
		return franchisePoolTransferPercentageConverter;
	}

	private GUIFieldPanel getPoolTransferPercentageEffectiveDateComponent() {
		if (poolTransferPercentageEffectiveDateComponent == null) {
			poolTransferPercentageEffectiveDateComponent = 
				getDateGUIField(AgreementGUIField.FRANCHISE_POOL_TRANSFER_EFFECTIVE_DATE);
			poolTransferPercentageEffectiveDateComponent.setOutputMarkupId(true);
			poolTransferPercentageEffectiveDateComponent.setOutputMarkupPlaceholderTag(true);
		}
		return poolTransferPercentageEffectiveDateComponent;
	}
	
	private GUIFieldPanel getPoolAccountInterestEffectiveDateComponent() {
		if (poolAccountInterestEffectiveDate == null) {
			poolAccountInterestEffectiveDate = 
				getDateGUIField(AgreementGUIField.FRANCHISE_POOL_INTEREST_RATE_EFFECTIVE_DATE);
			poolAccountInterestEffectiveDate.setOutputMarkupId(true);
			poolAccountInterestEffectiveDate.setOutputMarkupPlaceholderTag(true);
		}
		return poolAccountInterestEffectiveDate;
	}
	
	/**
	 * Create a date based GUI field panel
	 * @param description
	 * @param fieldName
	 * @return
	 */
	private GUIFieldPanel getDateGUIField(AgreementGUIField field) {
		HelperPanel ret = 
			createGUIPageField(field, getContext(), ComponentType.DATE_SELECTION_TEXTFIELD, false);
		return createGUIFieldPanel(field, null, ret.getEnclosedObject(), true);
	}
	
	/**
	 * Create a text based GUI field panel
	 * @param description
	 * @param fieldName
	 * @return
	 */
	private GUIFieldPanel getPercentageGUIField(AgreementGUIField field) {
		HelperPanel ret =
			createGUIPageField(field, getContext(), ComponentType.TEXTFIELD, false);
		return createGUIFieldPanel(field, ret.getEnclosedObject());
	}

	@Override
	protected ContextDrivenViewTemplate<AgreementGUIField, AgreementDTO> getViewTemplate() {
		return panelModel.getViewTemplate();
	}

	@Override
	protected AgreementDTO getViewTemplateContext() {
		if (viewTemplateContext == null) {
			viewTemplateContext = new AgreementDTO();
			viewTemplateContext.setFranchisePoolAccount(panelModel.getFranchisePoolAccount());
		}
		return viewTemplateContext;
	}
	
	

}
