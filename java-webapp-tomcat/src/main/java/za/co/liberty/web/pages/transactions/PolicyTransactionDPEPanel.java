/**
 * 
 */
package za.co.liberty.web.pages.transactions;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidator;

import za.co.liberty.dto.gui.templates.DescriptionDTO;
import za.co.liberty.dto.transaction.DistributePolicyEarningDTO;
import za.co.liberty.dto.transaction.IPolicyTransactionDTO;
import za.co.liberty.dto.transaction.IPolicyTransactionModel;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.pages.transactions.model.PolicyTransactionModel;
import za.co.liberty.web.pages.transactions.model.RequestTransactionModel;
import za.co.liberty.web.wicket.ajax.attributes.SRSAjaxCallListener;
import za.co.liberty.web.wicket.markup.html.form.SRSAbstractChoiceRenderer;
import za.co.liberty.web.wicket.markup.html.form.SRSDateField;

/**
 * Transaction panel for DPE transaction objects.
 * 
 * @author zzt2108
 *
 */
public class PolicyTransactionDPEPanel extends AbstractPolicyTransactionPanel implements Serializable {

	private static final long serialVersionUID = -7975622034133223587L;
	private static final Logger logger = Logger.getLogger(PolicyTransactionDPEPanel.class);
	
	private SRSDateField policyStartDateField;
	private SRSDateField effectiveDateField;
//	private DatePicker effectiveDateFieldDatePicker;
	private TextField<?> policyReferenceField;
//	private DatePicker policyStartDateFieldDatePicker;
	private TextField<?> ownerNameField;
	private DropDownChoice<?> commissionKindField;
	private DropDownChoice<?> movementTypeField;
	
	private DropDownChoice<?> productNameField;
	private TextField<?> movementCodeField;
	private TextField<?> dpeAmountField;

	private Panel additionalDPEFieldsPanel; 
//	private AdditionalDPEFieldsPanel populatedAdditionalDPEFieldsPanel; 
	
	private EditStateType editStateType;
	
	private Page parentPage;
	
	final private PolicyTransactionModel model;
	protected static FeedbackPanel feedbackPanel;
	
	/*protected static final List<FrequencyType> frequencyTypeList;
	
	static{
		frequencyTypeList = new ArrayList<FrequencyType>();
		for (FrequencyType t : FrequencyType.values()) {
			frequencyTypeList.add(t);
		}
	}*/
	
	/**
	 * Constructor used when using new "combined" transaction GUI. Convert the model here.
	 * 
	 * @param id
	 * @param editStateType
	 * @param model
	 * @param parentPage
	 * @param clearFields
	 */
	public PolicyTransactionDPEPanel(String id, EditStateType editStateType, 
			RequestTransactionModel model, Page parentPage, boolean clearFields) {
		this(id,editStateType,(IPolicyTransactionModel) model.getPanelModel() , parentPage, clearFields);
	}
	

	public PolicyTransactionDPEPanel(String id, EditStateType editStateType, IPolicyTransactionModel model, Page parentPage, boolean clearFields) {
		super(id, editStateType, model, parentPage);
		this.model = (PolicyTransactionModel) model;
		this.parentPage = parentPage;
		this.editStateType = editStateType;
		this.setOutputMarkupId(true);
		if (model.getAllContributionIncIndicators()==null) {
			getGuiController().initialisePageModel(model);
		}
		if(clearFields)
			resetFields(true);
		transactionFieldsForm = createForm("dpeFieldsForm");
		
		add(transactionFieldsForm);
	}
	
	private Form<?> createForm(String id) {
		Form form = new Form(id) {		
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit() {
				super.onSubmit();	
			}
		};
		
		form.add(policyReferenceField = createPolicyReferenceField("policyReference"));
		form.add(ownerNameField = createOwnerNameField("ownerName"));
		form.add(policyStartDateField = createPolicyStartDateField("policyStartDate"));
//		form.add(policyStartDateFieldDatePicker = createDatePicker("policyStartDatePicker", policyStartDateField));
		form.add(effectiveDateField = createEffectiveDateField("effectiveDate"));
//		form.add(effectiveDateFieldDatePicker = createDatePicker("effectiveDatePicker", effectiveDateField));
		form.add(commissionKindField = createCommissionKindField("commissionKind"));
		form.add(movementTypeField = createMovementTypeField("movementType"));
		form.add(premiumFrequencyField = createPremiumFrequencyField("premiumFrequency"));
		form.add(productNameField = createProductNameField("productName"));
		form.add(movementCodeField = createMovementCodeField("movementCode"));
		form.add(dpeAmountField = createAmountField("depAmount"));
		
		if (logger.isDebugEnabled())
			logger.info("editState=" + getEditState() 
					+ "     , selectedObject=" + model.getSelectedObject()
					+ "     , rejectOid=" + ((model.getSelectedObject()!=null)?model.getSelectedObject().getRejectOid() : null)
					+ "     , showAdditional=" 
					+ (getEditState()!=EditStateType.MODIFY || (model.getSelectedObject()!=null && model.getSelectedObject().getRejectOid()!=null)));
		if (getEditState()!=EditStateType.MODIFY || (model.getSelectedObject()!=null && model.getSelectedObject().getRejectOid()!=null)) {
			form.add(additionalDPEFieldsPanel= createAdditionalDPEFieldsPanel("dpeAdditionalFields"));
			logger.info("Adding additional");
		} else {
			additionalDPEFieldsPanel = new EmptyPanel("dpeAdditionalFields");
			additionalDPEFieldsPanel.setOutputMarkupId(true);
			form.add(additionalDPEFieldsPanel);
			logger.info("Adding additional - empty");
		}
		
		form.add(createAdditionalFieldsButton("additionalFieldsButton"));
			
		
		form.setOutputMarkupId(true);
		
		return form;
	}


	private TextField<?> createMovementCodeField(String id) {
		IModel<String> fieldModel = new IModel<String>() {
			private static final long serialVersionUID = 1L;

			public String getObject() {
				return ((DistributePolicyEarningDTO) model.getSelectedObject()).getMovementCode();
			}

			public void setObject(String arg0) {
				((DistributePolicyEarningDTO) model.getSelectedObject()).setMovementCode(arg0);
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
		if (getEditState() == EditStateType.AUTHORISE)
			field.setEnabled(false);
		return field;
	}

	private Button createAdditionalFieldsButton(String id) {

		Button button = new AjaxButton(id, transactionFieldsForm) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form form) {

				
				Panel panel = (Panel) transactionFieldsForm.get("dpeAdditionalFields");
				System.out.println("In onSubmit Event " + panel +  "  - " + panel.getClass() );
				if(panel instanceof EmptyPanel) {
					Panel tmpPanel = createAdditionalDPEFieldsPanel("dpeAdditionalFields");
					additionalDPEFieldsPanel.replaceWith(tmpPanel);
					additionalDPEFieldsPanel = tmpPanel;
				}
				target.add(additionalDPEFieldsPanel);
//				target.addComponent(populatedAdditionalDPEFieldsPanel);
				//target.addComponent(panel);
				
			}

			

			@Override
			protected void onComponentTag(ComponentTag tag) {
				super.onComponentTag(tag);
			}
			
			@Override
			protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
				super.updateAjaxAttributes(attributes);
			        
			        // SRS Convenience method for overLay hiding/showing
			        attributes.getAjaxCallListeners().add(new SRSAjaxCallListener());
			}
			
//			@Override
//			protected IAjaxCallDecorator getAjaxCallDecorator() {
//				return new AjaxCallDecorator() {
//					private static final long serialVersionUID = 1L;
//
//					public CharSequence decorateScript(CharSequence script) {
//						return "overlay(true);" + script;
//					}
//				};
//			}
		};

		button.setOutputMarkupId(true);
		button.add(new IValidator() {
			private static final long serialVersionUID = 1L;

			@Override
			public void validate(IValidatable val) {
				
			}		
		});
		if (getEditState()==EditStateType.AUTHORISE) {
			button.setVisible(false);
			button.setEnabled(false);
		}
		// Disable this button
		button.setVisible(false);
		button.setEnabled(false);
		return button;
		
	}
	
	/**
	 * Create the optional additional fields panel
	 * 
	 * @param id
	 * @return
	 */
	private Panel createAdditionalDPEFieldsPanel(String id) {
		Panel panel = new AdditionalDPEFieldsPanel(id, editStateType, model, parentPage);
		panel.setOutputMarkupId(true);
		return panel;
	}

	

	protected DropDownChoice<?> createProductNameField(String id) {
		
		final Map<Integer, DescriptionDTO> map = new HashMap<Integer, DescriptionDTO>();
		for (DescriptionDTO d : model.getAllProductReferences()) {
			map.put(d.getReference(), d);
		}
		
		IModel<Object> fieldModel = new IModel<Object>() {
			private static final long serialVersionUID = 1L;

			public Object getObject() {
				return map.get(((DistributePolicyEarningDTO)model.getSelectedObject()).getProductReference());
			}

			public void setObject(Object arg0) {
				((DistributePolicyEarningDTO)model.getSelectedObject()).setProductReference((arg0 == null) ? null : ((DescriptionDTO)arg0).getReference());
//				model.setBenefitType((DescriptionDTO) arg0);
			}

			public void detach() {
			}
		};
		
//		IModel<Object> fieldModel = new IModel<Object>() {
//			private static final long serialVersionUID = 1L;
//
//			public Object getObject() {
//				return model.getProductReference();
//			}
//
//			public void setObject(Object arg0) {
//				model.setProductReference((DescriptionDTO) arg0);
//			}
//
//			public void detach() {
//			}
//		};

		DropDownChoice<?> field = new DropDownChoice<Object>(id, fieldModel, model.getAllProductReferences(), new SRSAbstractChoiceRenderer<Object>() {

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
	 * Create contribution increase indicator
	 * @param id
	 * @return
	 */
	private DropDownChoice<?> createMovementTypeField(String id) {

		final Map<Integer, DescriptionDTO> map = new HashMap<Integer, DescriptionDTO>();
		for (DescriptionDTO d : model.getAllContributionIncIndicators()) {
			map.put(d.getReference(), d);
		}
		
		IModel<Object> fieldModel = new IModel<Object>() {
			private static final long serialVersionUID = 1L;

			public Object getObject() {
				return map.get(((DistributePolicyEarningDTO)model.getSelectedObject()).getContributionIncreaseIndicator());
			}

			public void setObject(Object arg0) {
				((DistributePolicyEarningDTO)model.getSelectedObject()).setContributionIncreaseIndicator((arg0 == null) ? null : ((DescriptionDTO)arg0).getReference());
//				model.setBenefitType((DescriptionDTO) arg0);
			}

			public void detach() {
			}
		};

//		IModel<Object> fieldModel = new IModel<Object>() {
//			private static final long serialVersionUID = 1L;
//
//			public Object getObject() {
//				return model.getContributionIncIndicator();
//			}
//
//			public void setObject(Object arg0) {
//				model.setContributionIncIndicator((DescriptionDTO) arg0);
//			}
//
//			public void detach() {
//			}
//		};

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
			protected void onUpdate(AjaxRequestTarget target) {
			}
		});
		field.setEnabled(getEditState()!=EditStateType.AUTHORISE);
		return field;
	}

	/**
	 * Create the comm kind field
	 * 
	 * @param id
	 * @return
	 */
	protected DropDownChoice<?> createCommissionKindField(String id) {

		final Map<Integer, DescriptionDTO> map = new HashMap<Integer, DescriptionDTO>();
		for (DescriptionDTO d : model.getCommissionKinds()) {
			map.put(d.getReference(), d);
		}
		
		IModel<Object> fieldModel = new IModel<Object>() {
			private static final long serialVersionUID = 1L;

			public Object getObject() {
				return map.get(((DistributePolicyEarningDTO)model.getSelectedObject()).getCommissionKind());
			}

			public void setObject(Object arg0) {
				((DistributePolicyEarningDTO)model.getSelectedObject()).setCommissionKind((arg0 == null) ? null : ((DescriptionDTO)arg0).getReference());
//				model.setBenefitType((DescriptionDTO) arg0);
			}

			public void detach() {
			}
		};

		DropDownChoice<?> field = new DropDownChoice<Object>(id, fieldModel, model.getCommissionKinds(), new SRSAbstractChoiceRenderer<Object>() {

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

	private TextField<?> createPolicyReferenceField(String id) {
		IModel<String> fieldModel = new IModel<String>() {
			private static final long serialVersionUID = 1L;
			public String getObject() {
					return ((DistributePolicyEarningDTO)model.getSelectedObject()).getPolicyReference();
			}
			public void setObject(String arg0) {
					((DistributePolicyEarningDTO)model.getSelectedObject()).setPolicyReference(arg0);
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
		if(getEditState()==EditStateType.AUTHORISE)
			field.setEnabled(false);
		return field;
		
			/*@SuppressWarnings("unchecked")
			TextField field = new TextField(id,new PropertyModel<Object>(model.getSelectedObject(), "policyReference"));
			field.add(new AjaxFormComponentUpdatingBehavior("change") {
				private static final long serialVersionUID = 1L;
				@Override
				protected void onUpdate(AjaxRequestTarget target) {
					//updateShowNextButton(target);
				}		
			});
			field.setEnabled(getEditState()!=EditStateType.AUTHORISE);
			return field;*/
		
	}
		

	public void resetFields(boolean clearFields) {
		((IPolicyTransactionDTO) model.getSelectedObject()).setOid(null);
		if (clearFields) {
			model.setSelectedObject(new DistributePolicyEarningDTO());
		} else {
			((DistributePolicyEarningDTO) model.getSelectedObject()).setMaxCommissionTerm(null);
			((DistributePolicyEarningDTO) model.getSelectedObject()).setCommissionTerm(null);
			((DistributePolicyEarningDTO) model.getSelectedObject()).setBenefitType(null);
			((DistributePolicyEarningDTO) model.getSelectedObject()).setCommissionKind(null);
			((DistributePolicyEarningDTO) model.getSelectedObject()).setAmount(null);
			((DistributePolicyEarningDTO) model.getSelectedObject()).setBenefitGroup(null);
		}
	}


}
