package za.co.liberty.web.pages.transactions;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.extensions.yui.calendar.DatePicker;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

import za.co.liberty.common.domain.Percentage;
import za.co.liberty.dto.gui.templates.DescriptionDTO;
import za.co.liberty.dto.transaction.DistributePolicyEarningDTO;
import za.co.liberty.dto.transaction.IPolicyTransactionModel;
import za.co.liberty.interfaces.agreements.FrequencyType;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.pages.transactions.model.UpfrontCommissionPercentageEnum;
import za.co.liberty.web.wicket.markup.html.form.SRSAbstractChoiceRenderer;
import za.co.liberty.web.wicket.markup.html.form.SRSDateField;

public class AdditionalDPEFieldsPanel extends AbstractPolicyTransactionPanel {

	private static final long serialVersionUID = -1968816022934055698L;

	protected DropDownChoice<?> commissionFrequencyField;
	protected CheckBox growthPensionField;
	protected CheckBox commissionBalanceField;
	protected SRSDateField movementEffectiveDateField;
	protected DatePicker movementEffectiveDateFieldDatePicker;
	protected DropDownChoice<?> benefitTypeFiled;
	protected TextField<?> commissionTermField;
	protected TextField<?> maxCommissionTermField;
	protected DropDownChoice<?> upfrontCommissionField;
	protected DropDownChoice<?> glCompanyCodeField;
	protected DropDownChoice<?> businessUnitField;
	protected CheckBox policyInForceField;
	protected DropDownChoice<?> numberOfNumberField;
	protected TextField<?> sourceSystemReferenceField;
	protected DropDownChoice<?> benefitGroupField;
	
	private EditStateType editStateType;

	final private IPolicyTransactionModel model;

	protected static FeedbackPanel feedbackPanel;

	protected static final List<FrequencyType> frequencyTypeList;
	protected static final List<UpfrontCommissionPercentageEnum> upfrontCommissionPercentageList;

	static {
		frequencyTypeList = new ArrayList<FrequencyType>();
		for (FrequencyType t : FrequencyType.values()) {
			frequencyTypeList.add(t);
		}

		upfrontCommissionPercentageList = new ArrayList<UpfrontCommissionPercentageEnum>();
		for (UpfrontCommissionPercentageEnum commPercentage : UpfrontCommissionPercentageEnum.values()) {
			upfrontCommissionPercentageList.add(commPercentage);
		}

	}
	
	public AdditionalDPEFieldsPanel(String id, EditStateType editStateType, IPolicyTransactionModel model, Page parePage) {
		super(id, editStateType, model, parePage);
		this.editStateType = editStateType;
		this.model = model;
		addFields();
	}

	private void addFields() {
		add(commissionFrequencyField = createCommissionFrequencyField("commissionFrequency"));
		add(growthPensionField = createGrowthPensionField("growthPension"));
		add(commissionBalanceField = createCommissionBalanceField("commissionBalance"));
		add(movementEffectiveDateField = createMovementEffectiveDateField("movementEffectiveDate"));
//		add(movementEffectiveDateFieldDatePicker = createDatePicker("movementEffectiveDatePicker", movementEffectiveDateField));
		add(benefitTypeFiled = createBenefitTypeField("benefitType"));
		add(commissionTermField = createCommissionTermField("commissionTerm"));
		add(maxCommissionTermField = createMaxCommissionTermField("maxCommissionTerm"));
		add(upfrontCommissionField = createUpfrontCommissionField("upfrontCommission"));
		add(glCompanyCodeField = createGLCompanyCode("glCompanyCode"));
		add(businessUnitField = createBusinessUnitField("businessUnit"));
		add(policyInForceField = createPolicyInForceField("policyInForce"));
		add(numberOfNumberField = createNumberOfMOnthsField("numberOfMonths"));
		add(sourceSystemReferenceField = createSourceSystemReferenceField("sourceSystemReference"));
		add(benefitGroupField = createBenefitGroupField("benefitGroup"));
	}
	

	private TextField<?> createSourceSystemReferenceField(String id) {
		@SuppressWarnings("unchecked")
		TextField<?> text = new TextField(id, new PropertyModel(model.getSelectedObject(), "sourceSystemReference"));
		text.add(new AjaxFormComponentUpdatingBehavior("change") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
			}
		});
		
		text.setOutputMarkupId(true);
		text.setEnabled(getEditState()!=EditStateType.AUTHORISE);
		return text;
	}

   
	/**
	 * Create the benefit group field
	 * 
	 * @param id
	 * @return
	 */
	private DropDownChoice<?> createBenefitGroupField(String id) {

		final Map<Integer, DescriptionDTO> map = new HashMap<Integer, DescriptionDTO>();
		for (DescriptionDTO d : model.getBenefitGroups()) {
			map.put(d.getReference(), d);
		}
		
		IModel<Object> fieldModel = new IModel<Object>() {
			private static final long serialVersionUID = 1L;

			public Object getObject() {
				return map.get(((DistributePolicyEarningDTO)model.getSelectedObject()).getBenefitGroup());
			}

			public void setObject(Object arg0) {
				((DistributePolicyEarningDTO)model.getSelectedObject()).setBenefitGroup((arg0 == null) ? null : ((DescriptionDTO)arg0).getReference());
//				model.setBenefitType((DescriptionDTO) arg0);
			}

			public void detach() {
			}
		};

		DropDownChoice<?> field = new DropDownChoice<Object>(id, fieldModel, model.getBenefitGroups(), new SRSAbstractChoiceRenderer<Object>() {

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
		field.setEnabled(getEditState()!=EditStateType.AUTHORISE);
		return field;
	}
			
	/**
	 
	 * Create number of months field
	 * 
	 */
	private DropDownChoice<?> createNumberOfMOnthsField(String id) {
		IModel<Object> fieldModel = new IModel<Object>() {
			private static final long serialVersionUID = 1L;

			public Object getObject() {
				return ((DistributePolicyEarningDTO)model.getSelectedObject()).getNumberOfMonths();
			}

			public void setObject(Object arg0) {
				((DistributePolicyEarningDTO)model.getSelectedObject()).setNumberOfMonths((Integer) arg0);
			}

			public void detach() {
			}
		};

		DropDownChoice<?> field = new DropDownChoice<Object>(id, fieldModel, model.getNumberOfMonthsList(), new SRSAbstractChoiceRenderer<Object>() {

			private static final long serialVersionUID = 1L;

			public Object getDisplayValue(Object value) {
				return (value == null) ? null : ((Integer) value);
			}

			public String getIdValue(Object value, int arg1) {
				return (value == null) ? null : ((Integer) value) + "";
			}
		});
		
		field.add(new AjaxFormComponentUpdatingBehavior("change") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
			}
		});
		field.setEnabled(getEditState()!=EditStateType.AUTHORISE);
		return field;
	}

	/**
	 * Policy in force field
	 * @param id
	 * @return
	 */
	private CheckBox createPolicyInForceField(String id) {
		
		IModel<Boolean> fieldModel = new IModel<Boolean>() {
			private static final long serialVersionUID = 1L;

			public Boolean getObject() {
				return (((DistributePolicyEarningDTO)model.getSelectedObject()).isInForceIndicator()==1?true:false);
			}

			public void setObject(Boolean arg0) {
				((DistributePolicyEarningDTO)model.getSelectedObject()).setInForceIndicator((arg0)?1:0);
			}

			public void detach() {
			}
		};
		final CheckBox field = new CheckBox(id, new PropertyModel<Boolean>(model.getSelectedObject(), "inForceIndicator"));

		field.setOutputMarkupId(true);
		field.setOutputMarkupPlaceholderTag(true);

		field.add(new AjaxFormComponentUpdatingBehavior("change") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				((DistributePolicyEarningDTO) model.getSelectedObject()).setInForceIndicator(Integer.parseInt(field.getValue()));
			}
		});
		field.setEnabled(getEditState()!=EditStateType.AUTHORISE);
		return field;
	}

	/**
	 * business unit field
	 * 
	 * @param id
	 * @return
	 */
	private DropDownChoice<?> createBusinessUnitField(String id) {
		DropDownChoice<?> field = new DropDownChoice<Object>(id, new PropertyModel(model.getSelectedObject(), "businessUnit"), 
				model.getBusinessUnitList(), new SRSAbstractChoiceRenderer<Object>() {

			private static final long serialVersionUID = 1L;

			public Object getDisplayValue(Object value) {
				return (value == null) ? null : ((String) value);
			}

			public String getIdValue(Object value, int arg1) {
				return (value == null) ? null : ((String) value);
			}
		});
		
		field.add(new AjaxFormComponentUpdatingBehavior("change") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
			}
		});
		field.setEnabled(getEditState()!=EditStateType.AUTHORISE);
		return field;
	}

	/**
	 * Company code 
	 * @param id
	 * @return
	 */
	private DropDownChoice<?> createGLCompanyCode(String id) {
		IModel<Object> fieldModel = new IModel<Object>() {
			private static final long serialVersionUID = 1L;

			public Object getObject() {
				return ((DistributePolicyEarningDTO)model.getSelectedObject()).getGlCompanyCode();
			}

			public void setObject(Object arg0) {
				((DistributePolicyEarningDTO)model.getSelectedObject()).setGlCompanyCode((String) arg0);
			}

			public void detach() {
			}
		};

		DropDownChoice<?> field = new DropDownChoice<Object>(id, fieldModel, model.getGlCompanyList(), new SRSAbstractChoiceRenderer<Object>() {

			private static final long serialVersionUID = 1L;

			public Object getDisplayValue(Object value) {
				return (value == null) ? null : ((String) value);
			}

			public String getIdValue(Object value, int arg1) {
				return (value == null) ? null : ((String) value);
			}
		});
		
		field.add(new AjaxFormComponentUpdatingBehavior("change") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
			}
		});
		field.setEnabled(getEditState()!=EditStateType.AUTHORISE);
		return field;
	}

	/**
	 * Create up froun comm field
	 * 
	 * @param id
	 * @return
	 */
	private DropDownChoice<?> createUpfrontCommissionField(String id) {
		IModel<Object> fieldModel = new IModel<Object>() {
			private static final long serialVersionUID = 1L;

			public Object getObject() {
				if (((DistributePolicyEarningDTO) model.getSelectedObject()).getUpfrontCommPercentage() != null
						&& ((DistributePolicyEarningDTO) model.getSelectedObject()).getUpfrontCommPercentage().getValue() != null){
				return UpfrontCommissionPercentageEnum.getCommPercentageByValue(((DistributePolicyEarningDTO) model.getSelectedObject()).getUpfrontCommPercentage().getValue().intValue());
				}else
					return null;
			}

			public void setObject(Object arg0) {
				((DistributePolicyEarningDTO) model.getSelectedObject()).setUpfrontCommPercentage(new Percentage(new BigDecimal(((UpfrontCommissionPercentageEnum) arg0).getValue())));
			}

			public void detach() {
			}
		};

		DropDownChoice<?> field = new DropDownChoice<Object>(id, fieldModel, upfrontCommissionPercentageList, new SRSAbstractChoiceRenderer<Object>() {

			private static final long serialVersionUID = 1L;

			public Object getDisplayValue(Object value) {
				return (value == null) ? null : ((UpfrontCommissionPercentageEnum) value).getValue();
			}

			public String getIdValue(Object value, int arg1) {
				return (value == null) ? null : (value)+ "";
			}
		});
		
		field.add(new AjaxFormComponentUpdatingBehavior("change") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
			}
		});

		field.setEnabled(getEditState()!=EditStateType.AUTHORISE);
		return field;
	}

	/**
	 * Create max comm term
	 * 
	 * @param id
	 * @return
	 */
	private TextField<?> createMaxCommissionTermField(String id) {
		TextField<?> text = new TextField(id, new IModel<Object>() {
			private static final long serialVersionUID = -1060562129103084694L;

			public Integer getObject() {
				return (Integer)((DistributePolicyEarningDTO)model.getSelectedObject()).getMaxCommissionTerm();
			}

			public void setObject(Object arg0) {
				if (arg0 != null)
					((DistributePolicyEarningDTO)model.getSelectedObject()).setMaxCommissionTerm(Integer.parseInt((String)arg0));
				else
					((DistributePolicyEarningDTO)model.getSelectedObject()).setMaxCommissionTerm(null);
			}

			public void detach() {
			}
		});
		text.add(new AjaxFormComponentUpdatingBehavior("change") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
			}
		});
		text.setOutputMarkupId(true);
		text.setEnabled(getEditState()!=EditStateType.AUTHORISE);
		return text;
	}

	private DropDownChoice<?> createCommissionFrequencyField(String id) {
		IModel<Object> fieldModel = new IModel<Object>() {
			private static final long serialVersionUID = 1L;

			public Object getObject() {
				return ((DistributePolicyEarningDTO)model.getSelectedObject()).getCommissionFrequency();
			}

			public void setObject(Object arg0) {
				((DistributePolicyEarningDTO)model.getSelectedObject()).setCommissionFrequency((Integer) arg0);
			}

			public void detach() {
			}
		};

		DropDownChoice<?> field = new DropDownChoice<Object>(id, fieldModel, model.getCommissionFrequencyList(), new SRSAbstractChoiceRenderer<Object>() {

			private static final long serialVersionUID = 1L;

			public Object getDisplayValue(Object value) {
				return (value == null) ? null : ((Integer) value);
			}

			public String getIdValue(Object value, int arg1) {
				return (value == null) ? null : ((Integer) value) + "";
			}
		});
		
		field.add(new AjaxFormComponentUpdatingBehavior("change") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
			}
		});
		field.setEnabled(getEditState()!=EditStateType.AUTHORISE);
		return field;
	}

	/**
	 * Growth pension field
	 * 
	 * @param id
	 * @return
	 */
	private CheckBox createGrowthPensionField(String id) {
		
		IModel<Boolean> fieldModel = new IModel<Boolean>() {
			private static final long serialVersionUID = 1L;

			public Boolean getObject() {
				return ((DistributePolicyEarningDTO)model.getSelectedObject()).getGrowthPensionIndicator();
			}

			public void setObject(Boolean arg0) {
				((DistributePolicyEarningDTO)model.getSelectedObject()).setGrowthPensionIndicator(arg0);
			}

			public void detach() {
			}
		};
		
		CheckBox field = new CheckBox(id, fieldModel);

		field.setOutputMarkupId(true);
		field.setOutputMarkupPlaceholderTag(true);

		field.add(new AjaxFormComponentUpdatingBehavior("change") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				// update the Model
			}
		});
		field.setEnabled(getEditState()!=EditStateType.AUTHORISE);
		return field;
	}

	/**
	 * comm balance field
	 * @param id
	 * @return
	 */
	private CheckBox createCommissionBalanceField(String id) {
		
		IModel<Boolean> fieldModel = new IModel<Boolean>() {
			private static final long serialVersionUID = 1L;

			public Boolean getObject() {
				return ((DistributePolicyEarningDTO)model.getSelectedObject()).getCommissionBalanceIndicator();
			}

			public void setObject(Boolean arg0) {
				((DistributePolicyEarningDTO)model.getSelectedObject()).setCommissionBalanceIndicator(arg0);
			}

			public void detach() {
			}
		};
		
		CheckBox field = new CheckBox(id, fieldModel);

		field.setOutputMarkupId(true);
		field.setOutputMarkupPlaceholderTag(true);

		field.add(new AjaxFormComponentUpdatingBehavior("change") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				// update the Model
			}
		});
		field.setEnabled(getEditState()!=EditStateType.AUTHORISE);
		return field;
	}

	/**
	 * Movement eff date
	 * 
	 * @param id
	 * @return
	 */
	private SRSDateField createMovementEffectiveDateField(String id) {
		SRSDateField text = new SRSDateField(id, new IModel<Date>() {
			private static final long serialVersionUID = -1060562129103084694L;

			public Date getObject() {
				return ((DistributePolicyEarningDTO)model.getSelectedObject()).getMovementEffectiveDate();
			}

			public void setObject(Date arg0) {
				if (arg0 != null) 
					((DistributePolicyEarningDTO)model.getSelectedObject()).setMovementEffectiveDate(new java.sql.Date(arg0.getTime()));
			}

			public void detach() {
			}
		});
		text.add(new AjaxFormComponentUpdatingBehavior("change") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
			}
		});
		text.setOutputMarkupId(true);
		text.setEnabled(getEditState()!=EditStateType.AUTHORISE);
		return text;
	}

	/**
	 * Benefit type 
	 * 
	 * TODO Jean - Change description
	 * 
	 * @param id
	 * @return
	 */
	private DropDownChoice<?> createBenefitTypeField(String id) {
		
		final Map<Integer, DescriptionDTO> benefitMap = new HashMap<Integer, DescriptionDTO>();
		for (DescriptionDTO d : model.getBenefitTypes()) {
			benefitMap.put(d.getReference(), d);
		}
		
		IModel<Object> fieldModel = new IModel<Object>() {
			private static final long serialVersionUID = 1L;

			public Object getObject() {
				return benefitMap.get(((DistributePolicyEarningDTO)model.getSelectedObject()).getBenefitType());
			}

			public void setObject(Object arg0) {
				if (arg0!=null)
				{
				((DistributePolicyEarningDTO)model.getSelectedObject()).setBenefitType(((DescriptionDTO)arg0).getReference());
//				model.setBenefitType((DescriptionDTO) arg0);
				}else{
					((DistributePolicyEarningDTO)model.getSelectedObject()).setBenefitType(null);
				}
			}

			public void detach() {
			}
		};

		DropDownChoice<?> field = new DropDownChoice<Object>(id, fieldModel, model.getBenefitTypes(), new SRSAbstractChoiceRenderer<Object>() {

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
		field.setEnabled(getEditState()!=EditStateType.AUTHORISE);
		return field;
	}

	/**
	 * Create commission term field
	 * 
	 * @param id
	 * @return
	 */
	private TextField<?> createCommissionTermField(String id) {
		@SuppressWarnings("unchecked")
		TextField<?> text = new TextField(id, new IModel() {
			private static final long serialVersionUID = -1060562129103084694L;

			public Object getObject() {
				return ((DistributePolicyEarningDTO)model.getSelectedObject()).getCommissionTerm();
			}

			public void setObject(Object arg0) {
				if (arg0 != null)
					((DistributePolicyEarningDTO)model.getSelectedObject()).setCommissionTerm(Integer.parseInt(new String(arg0.toString())));
				else
					((DistributePolicyEarningDTO)model.getSelectedObject()).setCommissionTerm(null);
			}

			public void detach() {
			}
		});
		
		text.add(new AjaxFormComponentUpdatingBehavior("change") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
			}
		});
		// text.add(createDateFieldUpdateBehavior("change"));
		text.setOutputMarkupId(true);
		text.setEnabled(getEditState()!=EditStateType.AUTHORISE);
		return text;
	}

}
