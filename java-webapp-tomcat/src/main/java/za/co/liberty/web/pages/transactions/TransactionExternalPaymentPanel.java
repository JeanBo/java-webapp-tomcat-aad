package za.co.liberty.web.pages.transactions;


import java.util.Date;
import java.util.List;

import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.model.IModel;

import za.co.liberty.business.guicontrollers.transactions.IRequestTransactionGuiController;
import za.co.liberty.common.domain.CurrencyAmount;
import za.co.liberty.dto.spec.TypeDTO;
import za.co.liberty.dto.transaction.ExternalPaymentRequestDTO;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.helpers.lookup.ServiceLocator;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.pages.MaintenanceBasePage;
import za.co.liberty.web.pages.MaintenanceBasePage.SelectionForm;
import za.co.liberty.web.pages.panels.BasePanel;
import za.co.liberty.web.pages.transactions.model.RequestTransactionModel;
import za.co.liberty.web.wicket.markup.html.form.CurrencyAmountTextField;
import za.co.liberty.web.wicket.markup.html.form.SRSDateField;
import za.co.liberty.web.wicket.markup.html.form.SRSTextField;

/**
 * Panel to enter/view the external payments (Section88) request properties
 * 
 * @author jzb0608 2017-05-30
 *
 */
@SuppressWarnings("unused")
public class TransactionExternalPaymentPanel extends BasePanel {
	
	private static final long serialVersionUID = 1L;
	
	private static final Logger logger = Logger.getLogger(TransactionExternalPaymentPanel.class);
	private RequestTransactionModel pageModel = null;
	
	private SRSDateField  requestedDateField = null;

	private SRSTextField  descriptionTextField = null;
	private SRSTextField  directiveNumberTextField = null;
	private SRSTextField  fullAmountTextField = null;
	private SRSTextField  taxAmountTextField = null;
	private SRSTextField  it88AmountTextField = null;
	private DropDownChoice transactionTypeDropDownField = null;

	protected SelectionForm selectionForm;
	public EditStateType pageEditState;
	private ModalWindow createSearchWindow;

	private List<TypeDTO> validExternalPaymentTransactionTypeList;
	
	private transient IRequestTransactionGuiController guiController;

	
	@SuppressWarnings("deprecation")
	public TransactionExternalPaymentPanel(String id, EditStateType editState){
		super (id, editState);
	}
	

	
	@SuppressWarnings("unchecked")
	public TransactionExternalPaymentPanel(String id, EditStateType editState, 
			RequestTransactionModel pageModel, MaintenanceBasePage parentPage) {
		super(id, editState,parentPage);
		this.pageModel = pageModel;
		initialise();
	}
	
	
	private void initialise(){
		if (validExternalPaymentTransactionTypeList==null) {
			validExternalPaymentTransactionTypeList = getGuiController().getValidExternalPaymentTransactionTypes();
		}
		add(requestedDateField = createRequestedDateField("requestedDate"));
//		add(requestedDatePickerField = createRequestedDatePicker("requestedDatePicker", requestedDateField));
		add(transactionTypeDropDownField=createTransactionTypeDropDownField("transactionType"));
		add(descriptionTextField = createDescriptionField("description"));
		add(directiveNumberTextField = createDirectiveNumberField("directiveNumber"));
		add(fullAmountTextField = createFullAmountField("fullAmount"));
		add(taxAmountTextField = createTaxAmountField("taxAmount"));
		add(it88AmountTextField =  createIt88AmountField("it88Amount"));
	}
	
	/**
	 * Internal method to retrieve the selected object of the correct type.
	 * 
	 * @return
	 */
	protected ExternalPaymentRequestDTO getSelectedObject() {
		return (ExternalPaymentRequestDTO) pageModel.getSelectedItem();
	}
	
	/**
	 * Create reqeuestedDateField
	 * 
	 * @param id
	 * @return
	 */
	protected SRSDateField createRequestedDateField(String id) {
//		SRSTextField tempSRSTextField = new SRSTextField(id,new PropertyModel(pageModel.getSelectedItem(),"segmentContextId" ));
		
		IModel<Date> fieldModel = new IModel<Date>() {
			private static final long serialVersionUID = 1L;

			public Date getObject() {
				return getSelectedObject().getRequestedDate();
			}

			public void setObject(Date arg0) {
				java.sql.Date dte = null;
				if (arg0!=null) {
					if (arg0 instanceof Date) {
						dte = new java.sql.Date(((Date)arg0).getTime());
					} else {
						dte = (java.sql.Date) arg0;
					}
				}
				getSelectedObject().setRequestedDate(dte);
			}

			public void detach() {
			}
		};
		
		SRSDateField text = new SRSDateField(id, fieldModel);
		
		
		AjaxFormComponentUpdatingBehavior a = new AjaxFormComponentUpdatingBehavior("change") {
			
			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {

			}
		};
		
		text.setOutputMarkupId(true);
		if(getEditState().isViewOnly()){
			text.setEnabled(false);
		}
		return text;
	}

	/**
	 * Create transaction type field
	 * 
	 * @param string
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private DropDownChoice createTransactionTypeDropDownField(String id) {
		// Model for getting and setting the value in the selected object
		//   from the list of objects shown.
		IModel<TypeDTO> model = new IModel<TypeDTO>() {
			private static final long serialVersionUID = 1L;
			
			public TypeDTO getObject() {
				if (getSelectedObject()!=null && getSelectedObject().getEarningType()!=null) {
					for (TypeDTO t : validExternalPaymentTransactionTypeList) {
						if (t.getOid()==getSelectedObject().getEarningType()) {
							return t;
						}
					}
				}
				return null;
			}
			public void setObject(TypeDTO arg0) {
				getSelectedObject().setEarningType((arg0==null)?null:arg0.getOid());
			}
			public void detach() {	
			}
		};
		DropDownChoice field = new DropDownChoice(id, model, validExternalPaymentTransactionTypeList);
		field.setOutputMarkupId(true);
		field.setNullValid(true);
		field.add(new AjaxFormComponentUpdatingBehavior("change") {
			private static final long serialVersionUID = 1L;
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
//				updateShowNextButton(target);
			}		
		});
		if(getEditState().isViewOnly()){
			field.setEnabled(false);
		}
		return field;
	}
	
//	/** 
//	 * Create the end date picker, includes the endDateField.
//	 * 
//	 * @param string
//	 * @return
//	 */
//	protected DatePicker createRequestedDatePicker(String id, SRSDateField dateField) {
//		DatePicker picker = new PopupDatePicker(id, dateField);
//		picker.setOutputMarkupId(true);
//		if(getEditState().isViewOnly()){
//			picker.setEnabled(false);
//			picker.setVisible(false);
//		}
//		return picker;
//	}
	
	/**
	 * Create createfullAmountField
	 * 
	 * @param id
	 * @return
	 */
	protected SRSTextField createFullAmountField(String id) {
		
		IModel<CurrencyAmount> fieldModel = new IModel<CurrencyAmount>() {
			private static final long serialVersionUID = 1L;

			public CurrencyAmount getObject() {
				return getSelectedObject().getFullAmount();
			}

			public void setObject(CurrencyAmount arg0) {
				getSelectedObject().setFullAmount(arg0);
			}

			public void detach() {
			}
		};
		final CurrencyAmountTextField tempSRSTextField = new CurrencyAmountTextField(id,fieldModel);
		tempSRSTextField.setEnabled(!getEditState().isViewOnly());
		tempSRSTextField.setRequired(true);
		tempSRSTextField.add(new AjaxFormComponentUpdatingBehavior("change") {

			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(AjaxRequestTarget arg0) {
				// Force format to be updated
				arg0.add(tempSRSTextField);
			}
			
			
		});
		tempSRSTextField.setOutputMarkupId(true);
		
		if(getEditState()== EditStateType.MODIFY){
			tempSRSTextField.setEnabled(false);
		}
		return tempSRSTextField;
	}

	/**
	 * Create createTaxAmountField
	 * 
	 * @param id
	 * @return
	 */
	protected SRSTextField createTaxAmountField(String id) {
		
		IModel<CurrencyAmount> fieldModel = new IModel<CurrencyAmount>() {
			private static final long serialVersionUID = 1L;

			public CurrencyAmount getObject() {
				return getSelectedObject().getTaxAmount();
			}

			public void setObject(CurrencyAmount arg0) {
				getSelectedObject().setTaxAmount(arg0);
			}

			public void detach() {
			}
		};
		final CurrencyAmountTextField tempSRSTextField = new CurrencyAmountTextField(id,fieldModel);
		tempSRSTextField.setEnabled(!getEditState().isViewOnly());
		tempSRSTextField.setRequired(true);
		tempSRSTextField.add(new AjaxFormComponentUpdatingBehavior("change") {

			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(AjaxRequestTarget arg0) {
				arg0.add(tempSRSTextField);
			}
			
			
		});
		tempSRSTextField.setOutputMarkupId(true);
		if(getEditState()== EditStateType.MODIFY){
			tempSRSTextField.setEnabled(false);
		}
		return tempSRSTextField;
	}
	/**
	 * Create createIt88AmountField
	 * 
	 * @param id
	 * @return
	 */
	protected SRSTextField createIt88AmountField(String id) {
		
		IModel<CurrencyAmount> fieldModel = new IModel<CurrencyAmount>() {
			private static final long serialVersionUID = 1L;

			public CurrencyAmount getObject() {
				return getSelectedObject().getIt88Amount();
			}

			public void setObject(CurrencyAmount arg0) {
				getSelectedObject().setIt88Amount(arg0);
			}

			public void detach() {
			}
		};
		final CurrencyAmountTextField tempSRSTextField = new CurrencyAmountTextField(id,fieldModel);
		tempSRSTextField.setEnabled(!getEditState().isViewOnly());
		tempSRSTextField.setRequired(true);
		tempSRSTextField.add(new AjaxFormComponentUpdatingBehavior("change") {

			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(AjaxRequestTarget arg0) {
				// Force format to be updated
				arg0.add(tempSRSTextField);
			}
			
			
		});
		tempSRSTextField.setOutputMarkupId(true);
		if(getEditState()== EditStateType.MODIFY){
			tempSRSTextField.setEnabled(false);
		}
		return tempSRSTextField;
	}
	/**
	 * Create description
	 * 
	 * @param id
	 * @return
	 */
	protected SRSTextField createDescriptionField(String id) {
		
		IModel<String> fieldModel = new IModel<String>() {
			private static final long serialVersionUID = 1L;

			public String getObject() {
				return getSelectedObject().getDescription();
			}

			public void setObject(String arg0) {
				getSelectedObject().setDescription(arg0);
			}

			public void detach() {
			}
		};
		SRSTextField tempSRSTextField = new SRSTextField(id,fieldModel);
		tempSRSTextField.setEnabled(!getEditState().isViewOnly());
		tempSRSTextField.setRequired(true);
		if(getEditState()== EditStateType.MODIFY){
			tempSRSTextField.setEnabled(false);
		}
		return tempSRSTextField;
	}
	/**
	 * Create directive number
	 * 
	 * @param id
	 * @return
	 */
	protected SRSTextField createDirectiveNumberField(String id) {
		
		IModel<String> fieldModel = new IModel<String>() {
			private static final long serialVersionUID = 1L;

			public String getObject() {
				return getSelectedObject().getDirectiveNumber();
			}

			public void setObject(String arg0) {
				getSelectedObject().setDirectiveNumber(arg0);
			}

			public void detach() {
			}
		};
		SRSTextField tempSRSTextField = new SRSTextField(id,fieldModel);
		tempSRSTextField.setEnabled(!getEditState().isViewOnly());
		tempSRSTextField.setRequired(true);
		if(getEditState()== EditStateType.MODIFY){
			tempSRSTextField.setEnabled(false);
		}
		return tempSRSTextField;
	}
	public String getPageName() {
		return "External Payments";
	}
	
	/**
	 * Return the gui controller for this page
	 * @return
	 */
	protected IRequestTransactionGuiController getGuiController() {
		if (guiController == null) {
			try {
				guiController = ServiceLocator.lookupService(IRequestTransactionGuiController.class);
			} catch (NamingException namingErr) {
				logger.error(this.getPageName()
						+ " IRequestTransactionGuiController can not be lookedup:"
						+ namingErr.getMessage());
				CommunicationException comm = new CommunicationException("IRequestTransactionGuiController can not be looked up!");
				throw new CommunicationException(comm);
			} 
		}
		return guiController;
	}
	
	
}
