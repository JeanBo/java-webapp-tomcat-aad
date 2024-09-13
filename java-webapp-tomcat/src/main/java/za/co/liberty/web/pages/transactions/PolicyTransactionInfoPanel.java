/**
 * 
 */
package za.co.liberty.web.pages.transactions;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.extensions.yui.calendar.DatePicker;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

import za.co.liberty.agreement.domain.spec.util.agreement.CurrencyAmountUtil;
import za.co.liberty.dto.dpenum.GIPTransactionType;
import za.co.liberty.dto.dpenum.INN8TransactionType;
import za.co.liberty.dto.dpenum.InvestmentSourceType;
import za.co.liberty.dto.gui.request.FundCodeDTO;
import za.co.liberty.dto.gui.request.ProductCodeDTO;
import za.co.liberty.dto.gui.templates.DescriptionDTO;
import za.co.liberty.dto.transaction.IPolicyTransactionDTO;
import za.co.liberty.dto.transaction.IPolicyTransactionModel;
import za.co.liberty.dto.transaction.RecordPolicyInfoDTO;
import za.co.liberty.interfaces.agreements.PolicyInfoKindType;
import za.co.liberty.interfaces.agreements.requests.RequestKindType;
import za.co.liberty.srs.type.SRSType;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.pages.transactions.model.RequestTransactionModel;
import za.co.liberty.web.wicket.markup.html.form.SRSAbstractChoiceRenderer;
import za.co.liberty.web.wicket.markup.html.form.SRSDateField;

/**
 * Panel used to show and record Policy Info requests {@linkplain RequestKindType#RecordPolicyInfo}
 * @author zzt2108
 * 
 */
public class PolicyTransactionInfoPanel extends AbstractPolicyTransactionPanel implements Serializable {

	private static final long serialVersionUID = 6850950395106624974L;
	private final static Logger logger = Logger.getLogger(PolicyTransactionInfoPanel.class);
	
	private SRSDateField policyStartDateField;
	private DatePicker policyStartDateFieldDatePicker;
	private SRSDateField effectiveDateField;
	private DatePicker effectiveDateFieldDatePicker;
	private SRSDateField pricingDateField;
	private DatePicker pricingDateFieldDatePicker;
	private TextField<?> fundAssetValueField;
	private TextField<?> policyReferenceField;
	private DropDownChoice<?> fundCodeField;
	private TextField<?> ownerNameField;
	private TextField<?> unitCountField;
	private TextField<?> unitPriceField;
	private DropDownChoice<?> productReferenceField;
	private TextField<?> amountField;
	private WebMarkupContainer premiumContainer;
	private WebMarkupContainer riskPremiumContainer;
	private WebMarkupContainer aumContainer;
	private EditStateType editStateType;
	private DropDownChoice<?> infoKindTypeField;
	
	private TextField<?> activePolicyMonthsField;
	private TextField<?> premiumsReceivedCountField;
	private TextField<?> termField;
	private CheckBox isLapseField;
	
	private boolean isInitialised = false;
	
	
	final private IPolicyTransactionModel model;
	private Panel additionalAUMRequestFields;
	
	protected static FeedbackPanel feedbackPanel;
	
	//Added SBS0510 For Guardbank GUI
	private WebMarkupContainer guardBankContainer;
	private DropDownChoice<?> gbankfundCodeField;	
	private SRSDateField gbankPricingDateField;
	private DatePicker gBankPricingDatePicker;
	private DropDownChoice<?> pcrCodeField;
	private TextField<?> transCodeField;
	private TextField<?> commAmountField;
	private Label productLabelField;	

	//Added SBS0510 For INN8 PCR GUI
	
	private WebMarkupContainer inn8PcrContainer;
	private WebMarkupContainer inn8CommContainer;

	private DropDownChoice<?> inn8fundCodeField;	
    private DropDownChoice<?> transactionTypeField;
	private TextField<?> conversionRateField;
	private Label fundCategoryLblField;
	private DropDownChoice<?> dfmModelField;
	private DropDownChoice<?> commTypeCodeField;	
	private TextField<?> inn8CommConversionRateField;
	
	//Added by Santosh for GIP
	private WebMarkupContainer gipPcrContainer;
	private WebMarkupContainer gipCommContainer;
	private DropDownChoice<?> gipfundCodeField;
	private Label gipfundCategoryLblField;
	private DropDownChoice<?> gipdfmModelField;
	private DropDownChoice<?> giptransactionTypeField;
	private TextField<?> gipconversionRateField;
	private DropDownChoice<?> gipinvestmentSourceField;
	private DropDownChoice<?> gipciiField;
	private DropDownChoice<?> gipcommTypeCodeField;
	private TextField<?> gipCommConversionRateField;
	private DropDownChoice<?> gipcommISField;
	private DropDownChoice<?> gipcommciiField;
	// added by toon for AUM model
	private DropDownChoice<?> aumdfmModelField;
	
	/**
	 * Constructor used when using new "combined" transaction GUI. Convert the model here.
	 * 
	 * @param id
	 * @param editStateType
	 * @param model
	 * @param parentPage
	 * @param clearFields
	 */
	public PolicyTransactionInfoPanel(String id, EditStateType editStateType, 
			RequestTransactionModel model, Page parentPage, boolean clearFields) {
		this(id,editStateType,(IPolicyTransactionModel) model.getPanelModel() , parentPage, clearFields);
	}

	/**
	 * Original constructor 
	 * 
	 * @param id
	 * @param editStateType
	 * @param model
	 * @param parentPage
	 * @param clearFields
	 */
	public PolicyTransactionInfoPanel(String id, EditStateType editStateType, 
			IPolicyTransactionModel model, Page parentPage, boolean clearFields) {
		super(id, editStateType, model, parentPage);
		this.model = model;
		// TODO jzb0608 - remove this once init has been sorted
		if (model.getAllContributionIncIndicators()==null) {
			getGuiController().initialisePageModel(model);
		}
		
		this.editStateType = editStateType;
		if (logger.isDebugEnabled())
			logger.debug("P-I - editState=" + getEditState());
		
		if(clearFields)
			resetFields(true);
		transactionFieldsForm = createForm("fieldsForm");
		transactionFieldsForm.setOutputMarkupId(true);
		// Add Request details only if the Edit State Type is VIEW
		if (getEditState().isViewOnly() && model.getSelectedPolicyInfoCalculation()!=null) {
			additionalAUMRequestFields = new AdditionalPolicyInfoFieldsPanel("additionalFieldsPanel", editStateType, model, parentPage);
			additionalAUMRequestFields.setOutputMarkupId(true);
			transactionFieldsForm.add(additionalAUMRequestFields);
		} else {
			additionalAUMRequestFields = new EmptyPanel("additionalFieldsPanel");
			additionalAUMRequestFields.setOutputMarkupId(true);
			transactionFieldsForm.add(additionalAUMRequestFields);
		}
		add(transactionFieldsForm);
		this.setOutputMarkupId(true);
		isInitialised = true;
		setProductNameOnModel(model,editStateType);//Added SBS0510 to display Product Name label on Authorise RPI page for Guardbank
		setFundCategoryOnModel(model,editStateType);//Added SBS0510 to display Fund Category label on Authorise RPI page for INN8
	}	

	/**
	 * Create the panel form and add the fields
	 * 
	 * @param id
	 * @return
	 */
	private Form<?> createForm(String id) {
		Form<Object> aumForm = new Form<Object>(id) {
			private static final long serialVersionUID = -6308633210871154462L;

			@Override
			protected void onSubmit() {
				super.onSubmit();
			}
		}; 
		aumForm.setOutputMarkupId(true);
		
		aumForm.add(infoKindTypeField = createInfoKindTypeField("infoKindType"));
		aumForm.add(policyReferenceField = createPolicyReferenceField("policyReference"));
		aumForm.add(ownerNameField = createOwnerNameField("ownerName"));
		aumForm.add(policyStartDateField = createPolicyStartDateField("policyStartDate"));
//		aumForm.add(policyStartDateFieldDatePicker = createPopupDatePicker("policyStartDatePicker", policyStartDateField));
		aumForm.add(productReferenceField = createProductRefenceField("productReference"));
		aumForm.add(effectiveDateField = createEffectiveDateField("effectiveDate"));
//		aumForm.add(effectiveDateFieldDatePicker = createPopupDatePicker("effectiveDatePicker", effectiveDateField));
		
		// Premium container
		premiumContainer = new WebMarkupContainer("premiumContainer")  {
		};
		premiumContainer.add(amountField = createAmountField("amount"));
		
		premiumContainer.add(premiumFrequencyField = createPremiumFrequencyFieldForPolicyInfo("premiumFrequency"));
		premiumContainer.setOutputMarkupId(true);
		premiumContainer.setOutputMarkupPlaceholderTag(true);
		aumForm.add(premiumContainer);
		
		// Risk premium container
		riskPremiumContainer = new WebMarkupContainer("riskPremiumContainer")  {
		};
		riskPremiumContainer.add(termField = createTermField("term"));	
		riskPremiumContainer.add(activePolicyMonthsField = createActivePolicyMonthsField("activePolicyMonths"));
		riskPremiumContainer.add(premiumsReceivedCountField = createPremiumsReceivedCountField("premiumsReceivedCount"));
		riskPremiumContainer.add(isLapseField = createIsLapseField("isLapse"));   
		
		
		riskPremiumContainer.setOutputMarkupId(true);
		riskPremiumContainer.setOutputMarkupPlaceholderTag(true);
		aumForm.add(riskPremiumContainer);
		
		// AUM container
		aumContainer = new WebMarkupContainer("aumContainer") {
		};
		
		aumContainer.add(fundCodeField = createFundCodeField("fundCode"));
		aumContainer.add(unitCountField = createUnitCountField("fundUnitCount"));
		aumContainer.add(unitPriceField = createFundUnitPriceField("fundUnitPrice"));
		aumContainer.add(fundAssetValueField = createFundAssetVauleField("fundAssetValue"));
		aumContainer.add(aumdfmModelField = createDFMModelField("aumdfmModelField"));
		aumContainer.add(pricingDateField = createPricingDateField("pricingDate"));
//		aumContainer.add(pricingDateFieldDatePicker = createPopupDatePicker("pricingDatePicker", pricingDateField));
		aumContainer.setOutputMarkupId(true);
		aumContainer.setOutputMarkupPlaceholderTag(true);
		aumForm.add(aumContainer);
		
		//Guardbank Container
		guardBankContainer = new WebMarkupContainer("guardBankContainer") {
		};
		
		guardBankContainer.add(gbankfundCodeField = createFundCodeField("gbankfundCode"));
		guardBankContainer.add(productLabelField = createProductNameLabel("productLbl"));
		guardBankContainer.add(gbankPricingDateField = createPricingDateField("gbankPricingDate"));
//		guardBankContainer.add(gBankPricingDatePicker = createPopupDatePicker("gBankPricingDatePicker",gbankPricingDateField));
		
		guardBankContainer.add(pcrCodeField = createPcrCodeField("pcrCode"));
		guardBankContainer.add(transCodeField = createTransCodeField("transCode"));
		guardBankContainer.add(commAmountField = createCommAmountField("commAmount"));
		
		guardBankContainer.setOutputMarkupId(true);
		guardBankContainer.setOutputMarkupPlaceholderTag(true);
		aumForm.add(guardBankContainer);
		
		//INN8 Container
		inn8PcrContainer = new WebMarkupContainer("inn8PcrContainer") {
		};
		inn8PcrContainer.add(inn8fundCodeField = createFundCodeField("inn8fundCode"));
		inn8PcrContainer.add(fundCategoryLblField = createFundCategoryLabel("fundCategoryLbl"));
		inn8PcrContainer.add(dfmModelField = createDFMModelField("dfmModel"));
		inn8PcrContainer.add(transactionTypeField = createTransactionTypeField("transactionType"));
		inn8PcrContainer.add(conversionRateField = createConversionRateField("conversionRate"));
		inn8PcrContainer.setOutputMarkupId(true);
		inn8PcrContainer.setOutputMarkupPlaceholderTag(true);
		aumForm.add(inn8PcrContainer);
		
		//INN8 Container
		inn8CommContainer = new WebMarkupContainer("inn8CommContainer") {
		};
		//changeInfoKindType();
		inn8CommContainer.add(commTypeCodeField = createCommissionTypeField("commTypeCode"));
		inn8CommContainer.add(inn8CommConversionRateField = createConversionRateField("inn8CommConversionRate"));
		inn8CommContainer.setOutputMarkupId(true);
		inn8CommContainer.setOutputMarkupPlaceholderTag(true);
		aumForm.add(inn8CommContainer);
		
		//GIP Changes   container below
		gipPcrContainer = new WebMarkupContainer("gipPcrContainer") {
		};
		//changeInfoKindType();
		gipPcrContainer.add(gipfundCodeField = createFundCodeField("gipfundCode"));
		gipPcrContainer.add(gipfundCategoryLblField = createFundCategoryLabel("gipfundCategoryLblField"));
		gipPcrContainer.add(gipdfmModelField = createDFMModelField("gipdfmModelField"));
		gipPcrContainer.add(giptransactionTypeField = createGIPPTransactionCodeField("giptransactionTypeField")); //Use different method and different ENUM for GIPP Txn code field-SBS0510
		gipPcrContainer.add(gipconversionRateField = createConversionRateField("gipconversionRate"));
		gipPcrContainer.add(gipinvestmentSourceField = createInvestmentSourceField("gipinvestmentSourceField"));
		gipPcrContainer.add(gipciiField = createContributionIncreaseIndicatorField("gipciiField"));
		gipPcrContainer.setOutputMarkupId(true);
		gipPcrContainer.setOutputMarkupPlaceholderTag(true);
		aumForm.add(gipPcrContainer);
		
		gipCommContainer = new WebMarkupContainer("gipCommContainer") {
		};
		changeInfoKindType();
		gipCommContainer.add(gipcommTypeCodeField = createCommissionTypeField("gipcommTypeCode"));
		gipCommContainer.add(gipCommConversionRateField = createConversionRateField("gipCommConversionRate"));
		gipCommContainer.add(gipcommISField = createInvestmentSourceField("gipcommIS"));
		gipCommContainer.add(gipcommciiField = createContributionIncreaseIndicatorField("gipcommCII"));
		gipCommContainer.setOutputMarkupId(true);
		gipCommContainer.setOutputMarkupPlaceholderTag(true);
		aumForm.add(gipCommContainer);
		
		aumForm.setOutputMarkupId(true);
		return aumForm;
	}
	

	private DropDownChoice<?> createCommissionTypeField(String id) {

		IModel<?> fieldModel = new IModel<Object>() {
			private static final long serialVersionUID = 1L;
			public Object getObject() {
				return ((RecordPolicyInfoDTO)model.getSelectedObject()).getCommTypeCode() == null ? null 
																					   : ((RecordPolicyInfoDTO)model.getSelectedObject()).getCommTypeCode();
				
			}
			public void setObject(Object arg0) {
				((RecordPolicyInfoDTO)model.getSelectedObject()).setCommTypeCode((Integer)arg0);
			}
			public void detach() {
			}
		};
				
		DropDownChoice<?> field = new DropDownChoice(id,fieldModel,Arrays.asList(new Integer[] {1,18}), new SRSAbstractChoiceRenderer<Object>() {

			@Override
			public Object getDisplayValue(Object object) {
				return object == null ? null :((Integer)object).intValue() ;
			}

			@Override
			public String getIdValue(Object object, int index) {
				return object == null ? null : ((Integer)object).intValue() +"";
			}
			
			
		});
		
		field.add(new AjaxFormComponentUpdatingBehavior("change") {
			private static final long serialVersionUID = 1L;
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
			}		
		});	

		field.setOutputMarkupId(true);
		field.setEnabled(!getEditState().isViewOnly());
		
		return field;
	}

	/**
	 * Create premium frequency specific to policy info
	 * 
	 * @param id
	 * @return
	 */
	protected DropDownChoice<?> createPremiumFrequencyFieldForPolicyInfo(String id) {
		
		final Map<Integer, DescriptionDTO> map = new HashMap<Integer, DescriptionDTO>();
		for (DescriptionDTO d : model.getAllFrequencyTypesForPolicyInfo()) {
			map.put(d.getReference(), d);
		}
		
		IModel<Object> fieldModel = new IModel<Object>() {
			private static final long serialVersionUID = 1L;

			public Object getObject() {
				return map.get((model.getSelectedObject()).getPremiumFrequency());
			}

			public void setObject(Object arg0) {
				(model.getSelectedObject()).setPremiumFrequency((arg0 == null) ? null : ((DescriptionDTO)arg0).getReference());
//				model.setBenefitType((DescriptionDTO) arg0);
			}

			public void detach() {
			}
		};

		DropDownChoice<?> field = new DropDownChoice<Object>(id, fieldModel, model.getAllFrequencyTypesForPolicyInfo(), new SRSAbstractChoiceRenderer<Object>() {

			private static final long serialVersionUID = 1L;

			public Object getDisplayValue(Object value) {
				return (value == null) ? null : ((DescriptionDTO) value).getDescription();
			}

			public String getIdValue(Object value, int arg1) {
				return (value == null) ? null : ((DescriptionDTO) value).getUniqId() + "";
			}
		});
		
		field.add(new AjaxFormComponentUpdatingBehavior("change") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
			}
		});
		
		field.setOutputMarkupId(true);
		field.setEnabled(!getEditState().isViewOnly());
		return field;
	}
	
	
	
	

	/**
	 * Set hide values for panels depending on transaction type selected.
	 */
	private void changeInfoKindType() {
		Long infoKindType = ((RecordPolicyInfoDTO)model.getSelectedObject()).getInfoKindType();
		
		if (logger.isDebugEnabled())
				logger.debug("Info Kind = " +infoKindType
					+ "   premiumVisible=" + (infoKindType!=null
						&& ((long)infoKindType != SRSType.POLICYINFORMATION_ASSETSUNDERMANAGEMENT))
					
					+ "   riskPremiumVisible="
					+ (infoKindType!=null
						&& ((long)infoKindType == PolicyInfoKindType.PolicyInfoRiskPremium.getType()))
						
					+ "   aumVisible="
					+ (infoKindType!=null
						&& ( (long)infoKindType == SRSType.POLICYINFORMATION_ASSETSUNDERMANAGEMENT))
					
					+ "   inn8PcrVisible="
					+ (infoKindType!=null
						&& ( (long)infoKindType == PolicyInfoKindType.PolicyInformationINN8PCRPremium.getType()))
					+ "   inn8CommVisible="
					+ (infoKindType!=null
						&& ( (long)infoKindType == PolicyInfoKindType.PolicyInformationINN8CommissionPremium.getType()))
					
					+ "   guardbankPremiumVisible="
					+ (infoKindType !=null
						&& ((long)infoKindType == PolicyInfoKindType.PolicyInfoGuardbankPremium.getType()))
						
					+ "  gipPcrPremiumVisible="
					+ (infoKindType !=null
						&& ((long)infoKindType == PolicyInfoKindType.PolicyInformationGIPPCRPremium.getType()))	
				
					+ "  gipCommPremiumVisible="
					+ (infoKindType !=null
						&& ((long)infoKindType == PolicyInfoKindType.PolicyInformationGIPCommissionPremium.getType()))	
						
						);
		
		premiumContainer.setVisible(infoKindType!=null
				&& ((long)infoKindType != SRSType.POLICYINFORMATION_ASSETSUNDERMANAGEMENT));	
		
		riskPremiumContainer.setVisible(infoKindType!=null
				&& ((long)infoKindType == PolicyInfoKindType.PolicyInfoRiskPremium.getType()));	
		
		aumContainer.setVisible(infoKindType!=null
				&& ( (long)infoKindType == SRSType.POLICYINFORMATION_ASSETSUNDERMANAGEMENT));
		
		guardBankContainer.setVisible(infoKindType!=null
				&& ( (long)infoKindType == PolicyInfoKindType.PolicyInfoGuardbankPremium.getType()));

		inn8PcrContainer.setVisible(infoKindType!=null
				&& ( (long)infoKindType == PolicyInfoKindType.PolicyInformationINN8PCRPremium.getType()));
		
		inn8CommContainer.setVisible(infoKindType!=null
				&& ( (long)infoKindType == PolicyInfoKindType.PolicyInformationINN8CommissionPremium.getType()));
		
	//GIP Changes 
		gipPcrContainer.setVisible(infoKindType!=null
				&& ( (long)infoKindType == PolicyInfoKindType.PolicyInformationGIPPCRPremium.getType()));
		gipCommContainer.setVisible(infoKindType!=null
				&& ( (long)infoKindType == PolicyInfoKindType.PolicyInformationGIPCommissionPremium.getType()));
		
		if(guardBankContainer.isVisible())
			productReferenceField.setEnabled(false);
		else
			productReferenceField.setEnabled(!getEditState().isViewOnly());//Visible only if Edit mode- SBS0510
		
		// Reset defaults if the object isn't null
		RecordPolicyInfoDTO dto = ((RecordPolicyInfoDTO)model.getSelectedObject());
		if (dto==null || infoKindType == null || !isInitialised) {
			return;
		}
		
		// Set some defaults (note that this only runs after initialisation
		if (infoKindType != PolicyInfoKindType.PolicyInfoRiskPremium.getType()) {
			if (logger.isDebugEnabled())
				logger.debug("Reset PolicyInfo Risk DTO fields");
			dto.setTerm(null);
			dto.setIsLapse(null);
			dto.setActivePolicyMonths(null);
			dto.setPremiumsReceivedCount(null);
		} else {
			if (logger.isDebugEnabled())
				logger.debug("Reset PolicyInfo Other DTO fields");
			dto.setIsLapse(0);
		}
		
		if(infoKindType != PolicyInfoKindType.PolicyInfoGuardbankPremium.getType()) {
			if (logger.isDebugEnabled())
				logger.debug("Reset Guardbank DTO fields");
			dto.setPcrCode(null);
			dto.setTransCode(null);
			dto.setCommissionAmount(null);
			dto.setFundCode(null);
			dto.setAmount(null);
			dto.setPricingDate(null);
			dto.setPremiumFrequency(null);
			model.setCurrentProductName(null);
		}
		
		if(infoKindType != PolicyInfoKindType.PolicyInformationINN8PCRPremium.getType()) {
			if (logger.isDebugEnabled())
				logger.debug("Reset INN8 PCR DTO fields");
			dto.setTransCode(null);
			dto.setFundCode(null);
			dto.setDfmModelCode(null);
			dto.setAmount(null);
			dto.setConversionRate(null);
			dto.setPremiumFrequency(null);
			model.setCurrentFundCategory(null);
		}
		
		if(infoKindType != PolicyInfoKindType.PolicyInformationINN8CommissionPremium.getType()) {
			if (logger.isDebugEnabled())
				logger.debug("Reset INN8 Commission DTO fields");
			dto.setCommTypeCode(null);
			dto.setConversionRate(null);
			dto.setPremiumFrequency(null);
			dto.setAmount(null);
		}
		
		// GIP Change
		if(infoKindType != PolicyInfoKindType.PolicyInformationGIPPCRPremium.getType()) {
			if (logger.isDebugEnabled())
				logger.debug("Reset GIP PCR DTO fields");
			dto.setTransCode(null);
			dto.setFundCode(null);
			dto.setDfmModelCode(null);
			dto.setAmount(null);
			dto.setConversionRate(null);
			dto.setPremiumFrequency(null);
			model.setCurrentFundCategory(null);
		}
		
		if(infoKindType != PolicyInfoKindType.PolicyInformationGIPCommissionPremium.getType()) {
			if (logger.isDebugEnabled())
				logger.debug("Reset GIP Commission DTO fields");
			dto.setCommTypeCode(null);
			dto.setConversionRate(null);
			dto.setPremiumFrequency(null);
			dto.setAmount(null);
		}
		
	}
	
	
	/**
	 * Decorate the style tag to hide the component
	 * 
	 * @param isHidden Hide component if true. 
	 * @param tag
	 */
	private void decorateComponentStyle(boolean isHidden, ComponentTag tag) {
		String val = (String) tag.getAttributes().get("style");
		val = (val ==null) ? "" : val;
		val += " ;visibility:" + ((isHidden)?"hidden":"visible") + ";";
		tag.put("style", val);
	}

	/**
	 * Create info kind type.  Chaning this value will constrain the available product kind 
	 * types.
	 * 
	 * @param id
	 * @return
	 */
	protected DropDownChoice<?> createInfoKindTypeField(String id) {
		// Define a lookup map
		final Map<Integer, DescriptionDTO> map = new HashMap<Integer, DescriptionDTO>();
		for (DescriptionDTO d : model.getAllInfoKindTypes()) {
			map.put(d.getReference(), d);
		}
		
		// Model
		IModel<Object> fieldModel = new IModel<Object>() {
			private static final long serialVersionUID = 1L;

			public Object getObject() {
				Long t = ((RecordPolicyInfoDTO)model.getSelectedObject()).getInfoKindType();
				return map.get((t==null)?-1:t.intValue());
			}

			public void setObject(Object arg0) {
				((RecordPolicyInfoDTO)model.getSelectedObject()).setInfoKindType(
						(arg0 == null) ? null : (long)((DescriptionDTO)arg0).getReference());
			}

			public void detach() {
			}
		};

		// Define the field
		DropDownChoice<?> field = new DropDownChoice<Object>(id, fieldModel, model.getAllInfoKindTypes(), new SRSAbstractChoiceRenderer<Object>() {

			private static final long serialVersionUID = 1L;

			public Object getDisplayValue(Object value) {
				return (value == null) ? null : ((DescriptionDTO) value).getDescription();
			}

			public String getIdValue(Object value, int arg1) {
				return (value == null) ? null : ((DescriptionDTO) value).getReference() + "";
			}
		});
		
		// Behaviour on change
		
		updateAvailableProducts();  // do it first time, if values are already set
		updateAvailableFundCodes();
		
		field.add(new AjaxFormComponentUpdatingBehavior("change") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				// Hide/show the correct panels for the type
				changeInfoKindType();
				target.add(aumContainer);
				target.add(premiumContainer);
				target.add(riskPremiumContainer);
				target.add(guardBankContainer);
				target.add(inn8PcrContainer);
				target.add(inn8CommContainer);
				//GIP Change
				target.add(gipPcrContainer);
				target.add(gipCommContainer);

				
				model.getCurrentProductCodes().clear();
				model.getCurrentFundCodes().clear();
				
				updateAvailableProducts();
				updateAvailableFundCodes();

				setFieldsForInn8(target);
				target.add(productReferenceField);
			}

			
		});
		
		field.setOutputMarkupId(true);
		field.setEnabled(!getEditState().isViewOnly());
		return field;
	}
	
	/**
	 * Update the available products
	 */
	private void updateAvailableProducts() {
		model.getCurrentProductCodes().clear();
		
		if (((RecordPolicyInfoDTO)model.getSelectedObject())!=null 
				&& ((RecordPolicyInfoDTO)model.getSelectedObject()).getInfoKindType()!=null
				&& ((RecordPolicyInfoDTO)model.getSelectedObject()).getInfoKindType() != PolicyInfoKindType.PolicyInfoGuardbankPremium.getType()) {
			model.getCurrentProductCodes().addAll(
					getGuiController().getPolicyInfoProductReferencesForInfoKindType(
							((RecordPolicyInfoDTO)model.getSelectedObject()).getInfoKindType()));
		}
	}
	
	/**
	 * Update the available fundcodes
	 */
	private void updateAvailableFundCodes() {
		model.getCurrentFundCodes().clear();
		List<FundCodeDTO> fundCodes = null;	
			
		if (((RecordPolicyInfoDTO)model.getSelectedObject())!=null 
				&& ((RecordPolicyInfoDTO)model.getSelectedObject()).getInfoKindType()!=null) {

			PolicyInfoKindType selectedInfoKindType = PolicyInfoKindType.getPolicyInfoKindWithType(((RecordPolicyInfoDTO)model.getSelectedObject()).getInfoKindType());
			if(!selectedInfoKindType.hasFundCodes())
				return;// Do not load fund codes from DB if No fund codes required for selected product
			
			if(selectedInfoKindType == PolicyInfoKindType.AssetsUnderManagement) {
				Long selectedProdCode = ((RecordPolicyInfoDTO)model.getSelectedObject()).getProductCode() != null ?Long.valueOf(((RecordPolicyInfoDTO)model.getSelectedObject()).getProductCode().longValue()):null;
				if(selectedProdCode != null) {
					ProductCodeDTO selectedProduct = getGuiController().getAUMProductCodeById(selectedProdCode);
					if(selectedProduct != null) {
						fundCodes = getGuiController().getAllFundCodesForBatchCode(selectedProduct.getBatch());						
					}
				}

			} else {				
				fundCodes = getGuiController().getAllFundCodesForBatchCode(selectedInfoKindType.getBatchCode());					

			}
			
			if(CollectionUtils.isNotEmpty(fundCodes)) {
				model.getCurrentFundCodes().addAll(fundCodes);
			}

		}
	}


	private TextField createFundAssetVauleField(String id) {
		IModel<?> fieldModel = new IModel<Object>() {
			private static final long serialVersionUID = 1L;

			public Object getObject() {
				return ((RecordPolicyInfoDTO)model.getSelectedObject()).getFundAssetValue();
			}

			public void setObject(Object arg0) {
//				jean - disabled as this is calculated and read-only anyway.  The Wicket system keeps resetting it to null
//				((RecordPolicyInfoDTO)model.getSelectedObject()).setFundAssetValue((CurrencyAmount) arg0);
			}

			public void detach() {
			}
		};
		TextField field = new TextField(id, fieldModel, BigDecimal.class);
		field.add(new AjaxFormComponentUpdatingBehavior("change") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
			}
		});

		field.setOutputMarkupId(true);
		if (EditStateType.VIEW.equals(editStateType))
			field.setEnabled(false);

		return field;
	}

	private SRSDateField createPricingDateField(String id) {
		IModel<java.util.Date> fieldModel = new IModel<java.util.Date>() {
			private static final long serialVersionUID = 1L;

			public java.util.Date getObject() {
				return ((RecordPolicyInfoDTO)model.getSelectedObject()).getPricingDate();
			}

			public void setObject(java.util.Date arg0) {
				long time = ((java.util.Date)arg0).getTime();
				Date date = new Date(time);
				((RecordPolicyInfoDTO)model.getSelectedObject()).setPricingDate(date);
			}

			public void detach() {
			}
		};
		SRSDateField field = new SRSDateField(id, fieldModel);
		field.add(new AjaxFormComponentUpdatingBehavior("change") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
			}
		});
		field.add(field.newDatePicker());
		field.setOutputMarkupId(true);
		field.setEnabled(!getEditState().isViewOnly());
		return field;
	}

	/**
	 * Create the product reference field.  List is constrained depending on the info kind selected.
	 * 
	 * @param id
	 * @return
	 */
	private DropDownChoice<?> createProductRefenceField(String id) {
		
		// Define a lookup map for product code descriptions
		final Map<Integer, ProductCodeDTO> map = new HashMap<Integer, ProductCodeDTO>();
		for (ProductCodeDTO d : model.getAllProductCodes()) {
			if (logger.isDebugEnabled())
				logger.debug("Add Product List : prod=" + d.getReference().intValue() 
						+ "  ,d" + d.getProductCode() + "-" + d.getProductDescription());
			
			map.put(d.getReference().intValue(), d);
		}
		
		IModel<Object> fieldModel = new IModel<Object>() {

			private static final long serialVersionUID = 1L;

			public Object getObject() {
				Integer t = ((RecordPolicyInfoDTO)model.getSelectedObject()).getProductCode();
				if (logger.isDebugEnabled())
					logger.debug("getObject=" + t);
				return map.get((t==null)?-1:t.intValue());
			}

			public void setObject(Object arg0) {
				if (logger.isDebugEnabled())
						logger.debug("ProductRef.setObject = " + ((arg0!=null)?((ProductCodeDTO) arg0).getId().intValue():null)
										+ " - " + arg0);
				//((DistributePolicyEarningDTO)model.getSelectedObject()).setCommissionKind((arg0 == null) ? null : ((DescriptionDTO)arg0).getReference());
				((RecordPolicyInfoDTO)model.getSelectedObject()).setProductCode((arg0 == null)? null : ((ProductCodeDTO) arg0).getId().intValue());
			}

			public void detach() {
			}
		};

		DropDownChoice<?> field = new DropDownChoice<Object>(id, fieldModel, model.getCurrentProductCodes(), new SRSAbstractChoiceRenderer<Object>() {

			private static final long serialVersionUID = 147879826508304070L;

			public Object getDisplayValue(Object value) {
				return (value == null) ? null : ((ProductCodeDTO) value).getProductDescription() + " - " + ((ProductCodeDTO) value).getBatch();
			}

			public String getIdValue(Object value, int arg1) {
				return (value == null) ? null : ((ProductCodeDTO) value).getId() + "";
			}
		});
		field.add(new AjaxFormComponentUpdatingBehavior("change") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				//SBS0510
				//Load Fund Code for AUM on Product Selection
				PolicyInfoKindType selectedInfoKindType = PolicyInfoKindType.getPolicyInfoKindWithType(((RecordPolicyInfoDTO)model.getSelectedObject()).getInfoKindType());
				if(selectedInfoKindType == PolicyInfoKindType.AssetsUnderManagement) {
					updateAvailableFundCodes();
					target.add(fundCodeField);
					
			}
			}
		});
		field.setOutputMarkupId(true);
		field.setEnabled(!getEditState().isViewOnly());
		
		return field;
	}

	private TextField createFundUnitPriceField(String id) {
		IModel fieldModel = new IModel() {
			private static final long serialVersionUID = 1L;

			public Object getObject() {
				return ((RecordPolicyInfoDTO)model.getSelectedObject()).getFundUnitPrice();
			}

			public void setObject(Object arg0) {
				((RecordPolicyInfoDTO)model.getSelectedObject()).setFundUnitPrice(CurrencyAmountUtil.create((BigDecimal) arg0));
			}

			public void detach() {
			}
		};
		TextField field = new TextField(id, fieldModel, BigDecimal.class);
		field.add(new AjaxFormComponentUpdatingBehavior("change") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				if (((RecordPolicyInfoDTO)model.getSelectedObject()).getFundUnitPrice() != null && ((RecordPolicyInfoDTO)model.getSelectedObject()).getFundUnitCount() != null)
					if (((RecordPolicyInfoDTO)model.getSelectedObject()).getFundUnitCount() != BigDecimal.ZERO){
						BigDecimal fundAssetValue = ((RecordPolicyInfoDTO)model.getSelectedObject()).getFundUnitCount().multiply(((RecordPolicyInfoDTO)model.getSelectedObject()).getFundUnitPrice().getValue()).setScale(8, RoundingMode.UP); 
						((RecordPolicyInfoDTO)model.getSelectedObject()).setFundAssetValue(CurrencyAmountUtil.create(fundAssetValue));
					}

				target.add(fundAssetValueField);

			}
		});

		field.setOutputMarkupId(true);
		field.setEnabled(!getEditState().isViewOnly());

		return field;
	}

	private TextField createUnitCountField(String id) {
		IModel fieldModel = new IModel() {
			private static final long serialVersionUID = 1L;

			public Object getObject() {
				return ((RecordPolicyInfoDTO)model.getSelectedObject()).getFundUnitCount();
			}

			public void setObject(Object arg0) {
				((RecordPolicyInfoDTO)model.getSelectedObject()).setFundUnitCount((BigDecimal) arg0);
			}

			public void detach() {
			}
		};
		TextField field = new TextField(id, fieldModel, BigDecimal.class);
		field.add(new AjaxFormComponentUpdatingBehavior("change") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				if (((RecordPolicyInfoDTO)model.getSelectedObject()).getFundUnitPrice() != null && ((RecordPolicyInfoDTO)model.getSelectedObject()).getFundUnitCount() != null)
					if (((RecordPolicyInfoDTO)model.getSelectedObject()).getFundUnitPrice() != null && ((RecordPolicyInfoDTO)model.getSelectedObject()).getFundUnitPrice().getValue() != BigDecimal.ZERO){
						BigDecimal fundAssetValue = ((RecordPolicyInfoDTO)model.getSelectedObject()).getFundUnitCount().multiply(((RecordPolicyInfoDTO)model.getSelectedObject()).getFundUnitPrice().getValue()).setScale(8, RoundingMode.UP);
						((RecordPolicyInfoDTO)model.getSelectedObject()).setFundAssetValue(CurrencyAmountUtil.create(fundAssetValue));
					}

				target.add(fundAssetValueField);
			}
		});
		field.setOutputMarkupId(true);
		field.setEnabled(!getEditState().isViewOnly());

		return field;
	}

	private DropDownChoice<?> createFundCodeField(String id) {
		IModel<Object> fieldModel = new IModel<Object>() {

			private static final long serialVersionUID = 1784178155243764510L;

			public Object getObject() {
				FundCodeDTO fundCodeDTO = ((RecordPolicyInfoDTO)model.getSelectedObject()).getFundCode() == null ? null : getGuiController().getFundCodeById(((RecordPolicyInfoDTO)model.getSelectedObject()).getFundCode().longValue());
				return fundCodeDTO;//((RecordPolicyInfoDTO)model.getSelectedObject()).getFundCode();
			}

			public void setObject(Object arg0) {
				((RecordPolicyInfoDTO)model.getSelectedObject()).setFundCode(arg0 == null ? null :((FundCodeDTO) arg0).getId().intValue());
				
				//Set Product code that are mapped in Fund Code for Guardbank as each fund code is mapped to differnt product code for GBNK - SBS0510
				if(arg0 != null && ((FundCodeDTO) arg0).getBatch().equalsIgnoreCase(PolicyInfoKindType.PolicyInfoGuardbankPremium.getBatchCode())) {
					((RecordPolicyInfoDTO)model.getSelectedObject()).setProductCode(arg0 == null ? null :((FundCodeDTO) arg0).getProductCode().intValue());
					model.setCurrentProductName(getDescriptionOfProductCode(((FundCodeDTO) arg0).getProductCode().intValue()));
				}
				
				if(arg0 != null && ((FundCodeDTO) arg0).getBatch().equalsIgnoreCase(PolicyInfoKindType.PolicyInformationINN8PCRPremium.getBatchCode())) {
					
					Integer fundCategory = ((FundCodeDTO) arg0).getCategory();
					model.setCurrentFundCategory(getFundCodeCategory(fundCategory));
				}
				//GIP Change by santosh
				if(arg0 != null && ((FundCodeDTO) arg0).getBatch().equalsIgnoreCase(PolicyInfoKindType.PolicyInformationGIPPCRPremium.getBatchCode())) {
					
					Integer fundCategory = ((FundCodeDTO) arg0).getCategory();
					model.setCurrentFundCategory(getFundCodeCategory(fundCategory));
				}
			}

			public void detach() {
			}
		};

		DropDownChoice<?> field = new DropDownChoice<Object>(id, fieldModel, model.getCurrentFundCodes(), new SRSAbstractChoiceRenderer<Object>() {

			private static final long serialVersionUID = 147879826508304070L;

			public Object getDisplayValue(Object value) {
				
				String val = (value == null) ? null :
					(((FundCodeDTO) value).getFundDescription() != null ?((FundCodeDTO) value).getFundDescription():((FundCodeDTO) value).getFundCode())+ " - " + ((FundCodeDTO) value).getBatch();
							
				return val;
			}

			public String getIdValue(Object value, int arg1) {
				return (value == null) ? null : ((FundCodeDTO) value).getId() + "";
			}
		});
		
		field.add(new AjaxFormComponentUpdatingBehavior("change") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {		
				    target.add(productLabelField);
					target.add(fundCategoryLblField);
					target.add(gipfundCategoryLblField); //Added Sujata for GIPPCR Category display
			}
		});
		field.setOutputMarkupId(true);
		field.setEnabled(!getEditState().isViewOnly());
		return field;
	}

	private TextField createPolicyReferenceField(String id) {
		IModel<String> fieldModel = new IModel<String>() {
			private static final long serialVersionUID = 1L;
			public String getObject() {
					return ((RecordPolicyInfoDTO)model.getSelectedObject()).getPolicyNr();
			}
			public void setObject(String arg0) {
					((RecordPolicyInfoDTO)model.getSelectedObject()).setPolicyNr(arg0);
			}
			public void detach() {
			}
		};
		TextField<Object> field = new TextField(id, fieldModel, String.class);
		field.add(new AjaxFormComponentUpdatingBehavior("change") {
			private static final long serialVersionUID = 1L;
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
			}		
		});
		
		field.setOutputMarkupId(true);
		field.setEnabled(!getEditState().isViewOnly());
		
		
		return field;

	}
	
	/**
	 * Create check box field for Is Lapse.  It is converted to Integer.
	 * 
	 * @param id
	 * @return
	 */
	protected CheckBox createIsLapseField(String id) { 
		IModel<Boolean> fieldModel = new IModel<Boolean>() {
			private static final long serialVersionUID = 1L;
			public Boolean getObject() {
				Integer val = ((RecordPolicyInfoDTO)model.getSelectedObject()).getIsLapse();
				return (val!=null && val.intValue() == 1);
			}
			public void setObject(Boolean arg0) {
				((RecordPolicyInfoDTO)model.getSelectedObject()).setIsLapse(
						((arg0 == null)? null : (arg0) ? 1 : 0));
			}
			public void detach() {
			}
		};
		CheckBox field = new CheckBox(id, fieldModel);
		field.add(new AjaxFormComponentUpdatingBehavior("change") {
			private static final long serialVersionUID = 1L;
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
			}		
		});

		field.setOutputMarkupId(true);
		field.setEnabled(!getEditState().isViewOnly());


		return field;
	}

	/**
	 * Create premiums received for Risk Premiums only
	 * 
	 * @param id
	 * @return
	 */
	protected TextField<?> createPremiumsReceivedCountField(String id) {
		IModel<Integer> fieldModel = new IModel<Integer>() {
			private static final long serialVersionUID = 1L;
			public Integer getObject() {
				return ((RecordPolicyInfoDTO)model.getSelectedObject()).getPremiumsReceivedCount();
			}
			public void setObject(Integer arg0) {
				((RecordPolicyInfoDTO)model.getSelectedObject()).setPremiumsReceivedCount(arg0);
			}
			public void detach() {
			}
		};
		TextField<Object> field = new TextField(id, fieldModel, Integer.class);
		field.add(new AjaxFormComponentUpdatingBehavior("change") {
			private static final long serialVersionUID = 1L;
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
			}		
		});

		field.setOutputMarkupId(true);
		field.setEnabled(!getEditState().isViewOnly());


		return field;

	}

	/**
	 * Create active policy months field
	 * 
	 * @param id
	 * @return
	 */
	protected TextField<?> createActivePolicyMonthsField(String id) {
		IModel<Integer> fieldModel = new IModel<Integer>() {
			private static final long serialVersionUID = 1L;
			public Integer getObject() {
				return ((RecordPolicyInfoDTO)model.getSelectedObject()).getActivePolicyMonths();
			}
			public void setObject(Integer arg0) {
				((RecordPolicyInfoDTO)model.getSelectedObject()).setActivePolicyMonths(arg0);
			}
			public void detach() {
			}
		};
		TextField<Object> field = new TextField(id, fieldModel, Integer.class);
		field.add(new AjaxFormComponentUpdatingBehavior("change") {
			private static final long serialVersionUID = 1L;
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
			}		
		});

		field.setOutputMarkupId(true);
		field.setEnabled(!getEditState().isViewOnly());


		return field;
	}

	/**
	 * Create term field
	 * 
	 * @param id
	 * @return
	 */
	protected TextField<?> createTermField(String id) {
		IModel<Integer> fieldModel = new IModel<Integer>() {
			private static final long serialVersionUID = 1L;
			public Integer getObject() {
				return ((RecordPolicyInfoDTO)model.getSelectedObject()).getTerm();
			}
			public void setObject(Integer arg0) {
				((RecordPolicyInfoDTO)model.getSelectedObject()).setTerm(arg0);
			}
			public void detach() {
			}
		};
		TextField<Object> field = new TextField(id, fieldModel, Integer.class);
		field.add(new AjaxFormComponentUpdatingBehavior("change") {
			private static final long serialVersionUID = 1L;
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
			}		
		});

		field.setOutputMarkupId(true);
		field.setEnabled(!getEditState().isViewOnly());


		return field;
	}
	
	//Added for Guarbdank GUI-SBS0510
	private TextField<?> createCommAmountField(String id) {
		IModel fieldModel = new IModel() {
			private static final long serialVersionUID = 1L;

			public Object getObject() {
				return ((RecordPolicyInfoDTO)model.getSelectedObject()).getCommissionAmount();
			}

			public void setObject(Object arg0) {
				((RecordPolicyInfoDTO)model.getSelectedObject()).setCommissionAmount(CurrencyAmountUtil.create((BigDecimal) arg0));
			}

			public void detach() {
			}
		};
		TextField field = new TextField(id, fieldModel, BigDecimal.class);
		field.add(new AjaxFormComponentUpdatingBehavior("change") {
			private static final long serialVersionUID = 1L;
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
			}		
		});

		field.setOutputMarkupId(true);
		field.setEnabled(!getEditState().isViewOnly());

		return field;

	}

	private TextField<?> createTransCodeField(String id) {
		IModel<Integer> fieldModel = new IModel<Integer>() {
			private static final long serialVersionUID = 1L;
			public Integer getObject() {
				return ((RecordPolicyInfoDTO)model.getSelectedObject()).getTransCode();
			}
			public void setObject(Integer arg0) {
				((RecordPolicyInfoDTO)model.getSelectedObject()).setTransCode(arg0);
			}
			public void detach() {
			}
		};
		TextField<Object> field = new TextField(id, fieldModel, Integer.class);
		field.add(new AjaxFormComponentUpdatingBehavior("change") {
			private static final long serialVersionUID = 1L;
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
			}		
		});

		field.setOutputMarkupId(true);
		field.setEnabled(!getEditState().isViewOnly());
		
		return field;

	}

	private DropDownChoice<?> createPcrCodeField(String id) {
		
		IModel<Integer> fieldModel = new IModel<Integer>() {
			private static final long serialVersionUID = 1L;
			public Integer getObject() {
				return ((RecordPolicyInfoDTO)model.getSelectedObject()).getPcrCode();
			}
			public void setObject(Integer arg0) {
				((RecordPolicyInfoDTO)model.getSelectedObject()).setPcrCode(arg0);
			}
			public void detach() {
			}
		};
		
		DropDownChoice<Object> field = new DropDownChoice(id, fieldModel, model.getPcrCodes());
		field.add(new AjaxFormComponentUpdatingBehavior("change") {
			private static final long serialVersionUID = 1L;
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
			}		
		});	

		field.setOutputMarkupId(true);
		field.setEnabled(!getEditState().isViewOnly());
		
		return field;
	}

	private TextField<?> createConversionRateField(String id) {

		IModel<?> fieldModel = new IModel<Object>() {
			private static final long serialVersionUID = 1L;

			public Object getObject() {
				return ((RecordPolicyInfoDTO) model.getSelectedObject()).getConversionRate();
			}

			public void setObject(Object arg0) {
				((RecordPolicyInfoDTO) model.getSelectedObject()).setConversionRate((BigDecimal) arg0);
			}

			public void detach() {
			}
		};
		TextField<Object> field = new TextField(id, fieldModel, BigDecimal.class);
		field.add(new AjaxFormComponentUpdatingBehavior("change") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
			}
		});

		field.setOutputMarkupId(true);
		field.setEnabled(!getEditState().isViewOnly());

		return field;

	}

	private DropDownChoice<?> createTransactionTypeField(String id) {
		IModel<?> fieldModel = new IModel<Object>() {
			private static final long serialVersionUID = 1L;
			public Object getObject() {
				return ((RecordPolicyInfoDTO)model.getSelectedObject()).getTransCode() == null ? null 
																					   : INN8TransactionType.getForType(((RecordPolicyInfoDTO)model.getSelectedObject()).getTransCode());
				
			}
			public void setObject(Object arg0) {
				((RecordPolicyInfoDTO)model.getSelectedObject()).setTransCode(((INN8TransactionType)arg0).getType());
			}
			public void detach() {
			}
		};
				
		DropDownChoice<?> field = new DropDownChoice(id,fieldModel,Arrays.asList(INN8TransactionType.values()), new SRSAbstractChoiceRenderer<Object>() {

			@Override
			public Object getDisplayValue(Object object) {
				return object == null ? null :((INN8TransactionType)object).getDescription() ;
			}

			@Override
			public String getIdValue(Object object, int index) {
				return object == null ? null : ((INN8TransactionType)object).getType() +"";
			}
			
			
		});
		
		field.add(new AjaxFormComponentUpdatingBehavior("change") {
			private static final long serialVersionUID = 1L;
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
			}		
		});	

		field.setOutputMarkupId(true);
		field.setEnabled(!getEditState().isViewOnly());
		
		return field;
	}
	
	/**
	 * Method to add GIPP Transaction code field - SBS0510
	 * @param id
	 * @return DropDownChoice
	 */
	private DropDownChoice<?> createGIPPTransactionCodeField(String id) {
		IModel<?> fieldModel = new IModel<Object>() {
			private static final long serialVersionUID = 1L;
			public Object getObject() {
				return ((RecordPolicyInfoDTO)model.getSelectedObject()).getTransCode() == null ? null 
																					   : GIPTransactionType.getForType(((RecordPolicyInfoDTO)model.getSelectedObject()).getTransCode());
				
			}
			public void setObject(Object arg0) {
				((RecordPolicyInfoDTO)model.getSelectedObject()).setTransCode(((GIPTransactionType)arg0).getType());
			}
			public void detach() {
			}
		};
				
		DropDownChoice<?> field = new DropDownChoice(id,fieldModel,Arrays.asList(GIPTransactionType.values()), new SRSAbstractChoiceRenderer<Object>() {

			@Override
			public Object getDisplayValue(Object object) {
				return object == null ? null :((GIPTransactionType)object).getDescription() ;
			}

			@Override
			public String getIdValue(Object object, int index) {
				return object == null ? null : ((GIPTransactionType)object).getType() +"";
			}
			
			
		});
		
		field.add(new AjaxFormComponentUpdatingBehavior("change") {
			private static final long serialVersionUID = 1L;
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
			}		
		});	

		field.setOutputMarkupId(true);
		field.setEnabled(!getEditState().isViewOnly());
		
		return field;
	}
	
	private Label createProductNameLabel(String id) {
		
		Label field =  new Label(id,  new PropertyModel(model,"currentProductName")) ;
		field.setOutputMarkupId(true);
		return field;
	
	}
	
    private Label createFundCategoryLabel(String id) {
		
		Label field =  new Label(id,  new PropertyModel(model,"currentFundCategory")) ;
		field.setOutputMarkupId(true);
		return field;
	
	}


	public void resetFields(boolean clearFields) {
		((IPolicyTransactionDTO)model.getSelectedObject()).setOid(null);
		if (clearFields) {
			model.setSelectedObject(new RecordPolicyInfoDTO());
		}else{
			((RecordPolicyInfoDTO)model.getSelectedObject()).setFundCode(null);
			((RecordPolicyInfoDTO)model.getSelectedObject()).setFundUnitCount(null);
			((RecordPolicyInfoDTO)model.getSelectedObject()).setFundUnitPrice(null);
			((RecordPolicyInfoDTO)model.getSelectedObject()).setFundAssetValue(null);
			//Added sbs0510 GBANK
			((RecordPolicyInfoDTO)model.getSelectedObject()).setPcrCode(null);
			((RecordPolicyInfoDTO)model.getSelectedObject()).setTransCode(null);
			((RecordPolicyInfoDTO)model.getSelectedObject()).setCommissionAmount(null);
		}
	}
	
	
	 /**
	    * Private Method to get Product Descripton for passed Product code
	    * @param productCode
	    * @return String
	    */
	   public String getDescriptionOfProductCode(Integer productCode) {

		   if(productCode != null && productCode.intValue() > 0) {
			   
			   List<DescriptionDTO> descriptionList = getGuiController().getAllProductReferences();
			   if(descriptionList == null)
				   return null;

			   for(DescriptionDTO dto: descriptionList) {

				   if(dto.getReference() == productCode)
					   return dto.getDescription();				
			   }


		   }		   
		   return null;
	   }
	   
	   /**
	    * Private Method to get Product Descripton for passed Product code
	    * @param productCode
	    * @return String
	    */
	   public String getFundCodeCategory(Integer category) {

		   if(category != null && category.intValue() > 0) {
			   
			   List<DescriptionDTO> descriptionList = getGuiController().getAllFundCategories();
			   if(descriptionList == null)
				   return null;

			   for(DescriptionDTO dto: descriptionList) {

				   if(dto.getReference() == category.intValue())
					   return dto.getDescription();				
			   }

		   }		   
		   return null;
	   }
	      	   
	   
	   //Set Product Name on AUthorise page for Guardbank
	   private void setProductNameOnModel(IPolicyTransactionModel model, EditStateType editStateType) {
			if(editStateType == EditStateType.AUTHORISE && model != null && 
					model.getSelectedObject() != null && model.getSelectedObject() instanceof RecordPolicyInfoDTO
					&& ((RecordPolicyInfoDTO)model.getSelectedObject()).getInfoKindType() != null && 
					 ((RecordPolicyInfoDTO)model.getSelectedObject()).getInfoKindType().longValue() == PolicyInfoKindType.PolicyInfoGuardbankPremium.getType()) {			
				
				this.model.setCurrentProductName(getDescriptionOfProductCode(((RecordPolicyInfoDTO)model.getSelectedObject()).getProductCode()));
			}		
			
		}
	   
	 //Set FundCategory on AUthorise page for INN8
	   //Modified for GIP-SBS0510
	   private void setFundCategoryOnModel(IPolicyTransactionModel model, EditStateType editStateType) {
			if(editStateType == EditStateType.AUTHORISE && model != null && 
					model.getSelectedObject() != null && model.getSelectedObject() instanceof RecordPolicyInfoDTO
					&& ((RecordPolicyInfoDTO)model.getSelectedObject()).getInfoKindType() != null && 
					(((RecordPolicyInfoDTO)model.getSelectedObject()).getInfoKindType().longValue() == PolicyInfoKindType.PolicyInformationINN8PCRPremium.getType()
					 || ((RecordPolicyInfoDTO)model.getSelectedObject()).getInfoKindType().longValue() == PolicyInfoKindType.PolicyInformationGIPPCRPremium.getType())) {			
				
				FundCodeDTO fundCodeDTO = ((RecordPolicyInfoDTO)model.getSelectedObject()).getFundCode() == null ? null : 
					getGuiController().getFundCodeById(((RecordPolicyInfoDTO)model.getSelectedObject()).getFundCode().longValue());

				
				this.model.setCurrentFundCategory(Objects.isNull(fundCodeDTO) ? null : getFundCodeCategory(fundCodeDTO.getCategory()));
			}		
			

		} 
	   
	   
	   private DropDownChoice<?> createDFMModelField(String id) {

			IModel<Object> fieldModel = new IModel<Object>() {

				private static final long serialVersionUID = 1784178155243764510L;

				public Object getObject() {
					FundCodeDTO fundCodeDTO = ((RecordPolicyInfoDTO)model.getSelectedObject()).getDfmModelCode() == null ? null : getGuiController().getFundCodeById(((RecordPolicyInfoDTO)model.getSelectedObject()).getDfmModelCode().longValue());
					return fundCodeDTO;
				}

				public void setObject(Object arg0) {
					((RecordPolicyInfoDTO)model.getSelectedObject()).setDfmModelCode(arg0 == null ? null :((FundCodeDTO) arg0).getId().intValue());				
				
				}

				public void detach() {
				}
			};

			DropDownChoice<?> field = new DropDownChoice<Object>(id, fieldModel, model.getAllDfmModelCodes(), new SRSAbstractChoiceRenderer<Object>() {

				private static final long serialVersionUID = 147879826508304070L;

				public Object getDisplayValue(Object value) {
					
					return (value == null) ? null :((FundCodeDTO) value).getDfmModel();
								
				}

				public String getIdValue(Object value, int arg1) {
					return (value == null) ? null : ((FundCodeDTO) value).getId() + "";
				}
			});
			
			field.add(new AjaxFormComponentUpdatingBehavior("change") {
				private static final long serialVersionUID = 1L;

				@Override
				protected void onUpdate(AjaxRequestTarget target) {				
						//do nothing
				}
			});
			field.setOutputMarkupId(true);
			field.setEnabled(!getEditState().isViewOnly());
			return field;
		
		}	   
	   
	   /**
	    * Set Product Reference and Premium Frequency for Inn8 - As per Email from SUgen / Thiro - 10/06/2022
	    * @param target
	    */
	   private void setFieldsForInn8(AjaxRequestTarget target) {

		   Long infoKindType = ((RecordPolicyInfoDTO)model.getSelectedObject()).getInfoKindType();
		   
		   if(infoKindType != null && (infoKindType.longValue() == PolicyInfoKindType.PolicyInformationINN8PCRPremium.getType() ||
				   infoKindType.longValue() == PolicyInfoKindType.PolicyInformationINN8CommissionPremium.getType() )) {
			   
			   ((RecordPolicyInfoDTO)model.getSelectedObject()).setPremiumFrequency(5);
			   if(!CollectionUtils.isEmpty(model.getCurrentProductCodes())) {
				   ((RecordPolicyInfoDTO)model.getSelectedObject()).setProductCode(model.getCurrentProductCodes().get(0).getId().intValue());			   
			   }
			   
		   } else {
			   ((RecordPolicyInfoDTO)model.getSelectedObject()).setPremiumFrequency(null);
			   ((RecordPolicyInfoDTO)model.getSelectedObject()).setProductCode(null);

		   }
		   
		   target.add(premiumFrequencyField);
		   target.add(productReferenceField);
		   
		}
	 
	   //this one used to hold Investment sourse value for GIP products
	   //Modified - SBS0510 to display from InvestmentSourceType ENUM  
	   private DropDownChoice<?> createInvestmentSourceField(String id) {
			IModel<?> fieldModel = new IModel<Object>() {
				private static final long serialVersionUID = 1L;
				public Object getObject() {
					return ((RecordPolicyInfoDTO)model.getSelectedObject()).getInvestmentSource() == null ? null 
																						   : InvestmentSourceType.getForType(((RecordPolicyInfoDTO)model.getSelectedObject()).getInvestmentSource());
					
				}
				public void setObject(Object arg0) {
					((RecordPolicyInfoDTO)model.getSelectedObject()).setInvestmentSource(((InvestmentSourceType)arg0).getType());
				}
				public void detach() {
				}
			};
					
			DropDownChoice<?> field = new DropDownChoice(id,fieldModel,Arrays.asList(InvestmentSourceType.values()), new SRSAbstractChoiceRenderer<Object>() {

				@Override
				public Object getDisplayValue(Object object) {
					return object == null ? null :((InvestmentSourceType)object).getType()+ "-"+(((InvestmentSourceType)object).getDescription()) ;
				}

				@Override
				public String getIdValue(Object object, int index) {
					return object == null ? null : ((InvestmentSourceType)object).getType() +"";
				}
				
				
			});
			
			field.add(new AjaxFormComponentUpdatingBehavior("change") {
				private static final long serialVersionUID = 1L;
				@Override
				protected void onUpdate(AjaxRequestTarget target) {
				}		
			});	

			field.setOutputMarkupId(true);
			field.setEnabled(!getEditState().isViewOnly());
			
			return field;
		}
	   
	   //For GIPP This field is same as "Movement Type" field on PolicyTransactionDPEPanel. So copied the same logic - SBS0510
	   private DropDownChoice<?> createContributionIncreaseIndicatorField(String id) {
		   
			final Map<Integer, DescriptionDTO> map = new HashMap<Integer, DescriptionDTO>();
			for (DescriptionDTO d : model.getAllContributionIncIndicators()) {
				map.put(d.getReference(), d);
			}
			
			IModel<Object> fieldModel = new IModel<Object>() {
				private static final long serialVersionUID = 1L;

				public Object getObject() {
					return map.get(((RecordPolicyInfoDTO)model.getSelectedObject()).getContributionIncreaseIndicator());
				}

				public void setObject(Object arg0) {
					((RecordPolicyInfoDTO)model.getSelectedObject()).setContributionIncreaseIndicator((arg0 == null) ? null : ((DescriptionDTO)arg0).getReference());
				}

				public void detach() {
				}
			};



			DropDownChoice<?> field = new DropDownChoice<Object>(id, fieldModel, model.getAllContributionIncIndicators(), new SRSAbstractChoiceRenderer<Object>() {

				private static final long serialVersionUID = 1L;

				public Object getDisplayValue(Object value) {
					return (value == null) ? null : ((DescriptionDTO) value).getDescription();
				}

				public String getIdValue(Object value, int arg1) {
					return (value == null) ? null : ((DescriptionDTO) value).getUniqId() + "";
				}
			});
			
			field.add(new AjaxFormComponentUpdatingBehavior("change") {

				private static final long serialVersionUID = 1L;

				@Override
				protected void onUpdate(AjaxRequestTarget arg0) {
					// TODO Auto-generated method stub
				}

			});
			field.setOutputMarkupId(true);
			field.setEnabled(!getEditState().isViewOnly());
			return field;			
		}
	 
}
