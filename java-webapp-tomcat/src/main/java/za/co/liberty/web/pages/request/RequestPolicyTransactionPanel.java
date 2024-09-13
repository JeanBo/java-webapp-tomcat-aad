/**
 * 
 */
package za.co.liberty.web.pages.request;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import za.co.liberty.dto.agreement.properties.CommissionKindsDTO;
import za.co.liberty.dto.gui.context.AgreementSearchType;
import za.co.liberty.dto.gui.context.InfoKindType;
import za.co.liberty.dto.gui.context.PolicyTransactionTypeEnum;
import za.co.liberty.dto.gui.request.FundCodeDTO;
import za.co.liberty.dto.gui.request.ProductCodeDTO;
import za.co.liberty.dto.gui.templates.DescriptionDTO;
import za.co.liberty.interfaces.agreements.requests.RequestKindType;
import za.co.liberty.web.wicket.markup.html.form.SRSAbstractChoiceRenderer;

/**
 * 
 * @author zzt2108
 * 
 */
public class RequestPolicyTransactionPanel extends AbstractRequestEnquiryPanel {

	private static final long serialVersionUID = 1L;

	public RequestPolicyTransactionPanel(String arg0, IModel<?> model,
			FeedbackPanel feedbackPanel) {
		super(arg0, model, feedbackPanel);
	}

	protected DropDownChoice<?> commissionKindField;
	protected DropDownChoice<?> productReferenceField;
	protected DropDownChoice<?> contributionIncrIndicatorField;
	protected DropDownChoice<?> infoKindField;
	protected DropDownChoice<?> productCodeField;
	protected DropDownChoice<?> fundCodeField;
	protected TextField<?> uacfId;
	protected Panel additionalFiledsPanel;
	protected Form<?> searchForm;

	protected static final List<AgreementSearchType> agreementNumberTypeList;
	protected static final List<String> transactionSearchTypeList;
	protected static final List<InfoKindType> infoKindSearchTypeList;

	static {
		agreementNumberTypeList = new ArrayList<AgreementSearchType>();
		for (AgreementSearchType t : AgreementSearchType.values()) {
			agreementNumberTypeList.add(t);
		}

		transactionSearchTypeList = new ArrayList<String>();
		for (PolicyTransactionTypeEnum transactionSearchType : PolicyTransactionTypeEnum.values()) {
			transactionSearchTypeList.add(transactionSearchType.getDescription());
		}
		infoKindSearchTypeList = new ArrayList<InfoKindType>();
		for (InfoKindType infoKindSearchType : InfoKindType.values()) {
			infoKindSearchTypeList.add(infoKindSearchType);
		}
	}

	/**
	 * Create the HTML form used in the DPE search enquiry screen
	 * 
	 * @param id
	 * @return {@link Form}
	 */
	public Form<?> createSearchForm(String id) {

		searchForm = super.createSearchForm(id);
		searchForm.add(createPolicyReferenceField("policyReference"));
		searchForm.add(commissionKindField = createCommissionKindField("commissionKind"));
		searchForm.add(productReferenceField = createProductRefenceField("productReference"));
		searchForm.add(contributionIncrIndicatorField = createContributionIncrIndicatorField("contributionIncrIndicator"));
		searchForm.add(createAgreementNumberTypeField("agreementNumberType"));
		searchForm.add(createAgreementNumberField("agreementNumber"));
		searchForm.add(createTransactionTypeField("transactionType"));
		searchForm.add(infoKindField = createInfoKindField("infoKind"));
		searchForm.add(productCodeField = createProductCodeField("productCode"));
		searchForm.add(fundCodeField = createFundCodeField("fundCode"));
		searchForm.add(uacfId = createUacfIdField("uacfId"));
		//final Model<RequestEnquiryModel> model = new Model<RequestEnquiryModel>(pageModel);
		//searchForm.add(additionalFiledsPanel = createAddittionalSearchFieldsPanel(PolicyTransactionSearchType.AUM, "aumSearchPanel", model));

		return searchForm;
	}
	
	/**
	 * Override to exclude request kind fields
	 */
	@Override
	protected boolean isIncludeRequestKindFields() {
		return false;
	}

	private TextField<?> createUacfIdField(String id) {
		IModel<Object> model = new IModel<Object>() {
			private static final long serialVersionUID = 1L;

			public Object getObject() {
				return dataModel.getUacfId();
			}

			public void setObject(Object arg0) {
				dataModel.setUacfId((String) arg0);
			}

			public void detach() {
			}
		};

		TextField<Object> field = new TextField<Object>(id, model);
		field.add(new AjaxFormComponentUpdatingBehavior("change") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				updateShowNextButton(target);
			}
		});
		return field;
	}

	private TextField<?> createPolicyReferenceField(String id) {
		IModel<Object> model = new IModel<Object>() {
			private static final long serialVersionUID = 1L;

			public Object getObject() {
				return dataModel.getPolicyReference();
			}

			public void setObject(Object arg0) {
				dataModel.setPolicyReference((String) arg0);
			}

			public void detach() {
			}
		};

		TextField<Object> field = new TextField<Object>(id, model);
		field.add(new AjaxFormComponentUpdatingBehavior("change") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				updateShowNextButton(target);
			}
		});
		return field;
	}

	private DropDownChoice<?> createTransactionTypeField(String id) {
		final DropDownChoice<?> field = new DropDownChoice<Object>(id, new IModel<Object>() {

			private static final long serialVersionUID = 489570069636191260L;

			public Object getObject() {
				return dataModel.getTransactionSearchType();
			}

			public void setObject(Object arg0) {
				dataModel.setTransactionSearchType(PolicyTransactionTypeEnum.getPolicyTransactionTypeByDescription((String) arg0));
			}

			public void detach() {
			}

		}, transactionSearchTypeList);
		field.setNullValid(true);
		field.add(new AjaxFormComponentUpdatingBehavior("change") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				updateShowNextButton(target);
				PolicyTransactionTypeEnum value = (PolicyTransactionTypeEnum) field.getModelObject();// getModelValue());
				if (value != null) {
					if (value.equals(PolicyTransactionTypeEnum.RPI)) {
						dataModel.setRequestKind(RequestKindType.RecordPolicyInfo);
						commissionKindField.setEnabled(false);
						productReferenceField.setEnabled(false);
						contributionIncrIndicatorField.setEnabled(false);
						
						infoKindField.setEnabled(true);
						productCodeField.setEnabled(true);
						fundCodeField.setEnabled(true);
						
						//totalsPanelField.setVisible(false);
						
						target.add(commissionKindField);
						target.add(productReferenceField);
						target.add(contributionIncrIndicatorField);
						
						target.add(infoKindField);
						target.add(productCodeField);
						target.add(fundCodeField);
						
						//target.addComponent(totalsPanelField);
					} else if (value.equals(PolicyTransactionTypeEnum.DPE)) {
						dataModel.setRequestKind(RequestKindType.DistributePolicyEarning);
						
						commissionKindField.setEnabled(true);
						productReferenceField.setEnabled(true);
						contributionIncrIndicatorField.setEnabled(true);
						
						infoKindField.setEnabled(false);
						productCodeField.setEnabled(false);
						fundCodeField.setEnabled(false);
						
						//totalsPanelField.setVisible(true);
						
						target.add(commissionKindField);
						target.add(productReferenceField);
						target.add(contributionIncrIndicatorField);
						
						target.add(infoKindField);
						target.add(productCodeField);
						target.add(fundCodeField);
						
						//target.addComponent(totalsPanelField);
					}
				}
			}
		});
		field.setOutputMarkupId(true);
		field.setRequired(true);
		
		return field;
	}

	/**
	 * Create dropdown field for Contribution Increment Indicator
	 * 
	 * @param id
	 *            name of the dropdown
	 * @return {@link DropDownChoice}
	 */
	private DropDownChoice<?> createContributionIncrIndicatorField(String id) {
		IModel<Object> model = new IModel<Object>() {
			private static final long serialVersionUID = 1L;

			public Object getObject() {
				return dataModel.getContributionIncreaseIndicator();
			}

			public void setObject(Object arg0) {
				dataModel.setContributionIncreaseIndicator((DescriptionDTO) arg0);
			}

			public void detach() {
			}
		};

		// Changed to ChoiceRenderer
		DropDownChoice<?> field = new DropDownChoice<Object>(id, model, pageModel.getAllContributionIncIndicators(), 
				new SRSAbstractChoiceRenderer<Object>() {
			
			private static final long serialVersionUID = 1L;

			public Object getDisplayValue(Object value) {
				return (value == null) ? null : ((DescriptionDTO) value).getDescription();
			}

			public String getIdValue(Object value, int arg1) {
				return (value == null) ? null : ((DescriptionDTO) value).getUniqId() + "";
			}
		});
		field.setOutputMarkupId(true);
		return field;
	}

	/**
	 * Create dropdown field for the Product Reference
	 * 
	 * @param id
	 *            name of the dropdown
	 * @return {@link DropDownChoice}
	 */
	private DropDownChoice<?> createProductRefenceField(String id) {

		IModel<Object> model = new IModel<Object>() {
			private static final long serialVersionUID = 1L;

			public Object getObject() {
				return dataModel.getProductReference();
			}

			public void setObject(Object arg0) {
				dataModel.setProductReference((DescriptionDTO) arg0);
			}

			public void detach() {
			}
		};

		DropDownChoice<?> field = new DropDownChoice<Object>(id, model, pageModel.getAllProductReferences(), new SRSAbstractChoiceRenderer<Object>() {

			private static final long serialVersionUID = 1L;

			public Object getDisplayValue(Object value) {
				return (value == null) ? null : ((DescriptionDTO) value).getDescription();
			}

			public String getIdValue(Object value, int arg1) {
				return (value == null) ? null : ((DescriptionDTO) value).getUniqId() + "";
			}
		});
		field.setOutputMarkupId(true);
		
		return field;
	}

	/**
	 * Create dropdown field for the Commission Kind
	 * 
	 * @param id
	 *            name of the dropdown
	 * @return {@link DropDownChoice}
	 */
	private DropDownChoice<?> createCommissionKindField(String id) {
		IModel<Object> model = new IModel<Object>() {
			private static final long serialVersionUID = 1L;

			public Object getObject() {
				return dataModel.getCommissionKind();
			}

			public void setObject(Object arg0) {
				dataModel.setCommissionKind((CommissionKindsDTO) arg0);
			}

			public void detach() {
			}
		};

		DropDownChoice<?> field = new DropDownChoice<Object>(id, model, pageModel.getAllCommissionKinds());
		field.setOutputMarkupId(true);

		return field;
	}

	/**
	 * Create the agreement number type field
	 * 
	 * @param id
	 * @return {@link DropDownChoice}
	 */
	private DropDownChoice<?> createAgreementNumberTypeField(String id) {
		DropDownChoice<?> field = new DropDownChoice<Object>(id, new IModel<Object>() {

			private static final long serialVersionUID = 489570069636191260L;

			public Object getObject() {
				return dataModel.getAgreementNumberType();
			}

			public void setObject(Object arg0) {
				dataModel.setAgreementNumberType((AgreementSearchType) arg0);
			}

			public void detach() {
			}

		}, agreementNumberTypeList);
		field.setNullValid(true);
		field.add(new AjaxFormComponentUpdatingBehavior("change") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				updateShowNextButton(target);
			}
		});
		return field;
	}

	/**
	 * Create text input field for the agreement number
	 * 
	 * @param id
	 *            name of the text field (wicket id)
	 * @return {@link TextField}
	 */
	private TextField<Object> createAgreementNumberField(String id) {
		IModel<Object> model = new IModel<Object>() {
			private static final long serialVersionUID = 1L;

			public Object getObject() {
				return dataModel.getAgreementNumber();
			}

			public void setObject(Object arg0) {
				dataModel.setAgreementNumber((String) arg0);
			}

			public void detach() {
			}
		};

		TextField<Object> field = new TextField<Object>(id, model);
		field.add(new AjaxFormComponentUpdatingBehavior("change") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				updateShowNextButton(target);
			}
		});
		return field;
	}
	
	private DropDownChoice<?> createFundCodeField(String id) {
		IModel<Object> model = new IModel<Object>() {

			private static final long serialVersionUID = 1784178155243764510L;

			public Object getObject() {
				return dataModel.getFundCode();
			}

			public void setObject(Object arg0) {
				dataModel.setFundCode((FundCodeDTO) arg0);
			}

			public void detach() {
			}
		};

		DropDownChoice<?> field = new DropDownChoice<Object>(id, model, pageModel.getAllFundCodes(), new SRSAbstractChoiceRenderer<Object>() {

			private static final long serialVersionUID = 147879826508304070L;

			public Object getDisplayValue(Object value) {
				return (value == null) ? null :
					(((FundCodeDTO) value).getFundDescription() != null ?((FundCodeDTO) value).getFundDescription():((FundCodeDTO) value).getFundCode())+ " - " + ((FundCodeDTO) value).getBatch();
			}

			public String getIdValue(Object value, int arg1) {
				return (value == null) ? null : ((FundCodeDTO) value).getId() + "";
			}
		});
		field.setOutputMarkupId(true);
		
		return field;
	}

	private DropDownChoice<?> createProductCodeField(String id) {
		IModel<Object> model = new IModel<Object>() {
			private static final long serialVersionUID = 1L;

			public Object getObject() {
				return dataModel.getProductCode();
			}

			public void setObject(Object arg0) {
				dataModel.setProductCode((ProductCodeDTO) arg0);
			}

			public void detach() {
			}
		};

		DropDownChoice<?> field = new DropDownChoice<Object>(id, model, pageModel.getAllProductCodes(), new SRSAbstractChoiceRenderer<Object>() {

			private static final long serialVersionUID = 1L;

			public Object getDisplayValue(Object value) {
				return (value == null) ? null : ((ProductCodeDTO) value).getProductDescription();
			}

			public String getIdValue(Object value, int arg1) {
				return (value == null) ? null : ((ProductCodeDTO) value).getId() + "";
			}
		});
		field.setOutputMarkupId(true);

		return field;
	}

	private DropDownChoice<?> createInfoKindField(String id) {
		DropDownChoice<?> field = new DropDownChoice<Object>(id, new IModel<Object>() {

			private static final long serialVersionUID = 489570069636191260L;

			public Object getObject() {
				return dataModel.getInfoKindSearchType();
			}

			public void setObject(Object arg0) {
				dataModel.setInfoKindSearchType((InfoKindType) arg0);
			}

			public void detach() {
			}

		}, infoKindSearchTypeList);
		field.setNullValid(true);
		field.setOutputMarkupId(true);
		
		return field;
	}


}
