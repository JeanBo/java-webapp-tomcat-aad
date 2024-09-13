package za.co.liberty.web.pages.transactions;


import java.util.Date;

import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.model.IModel;

import za.co.liberty.business.guicontrollers.transactions.IRequestTransactionGuiController;
import za.co.liberty.common.domain.CurrencyAmount;
import za.co.liberty.dto.transaction.ProcessAdvanceRequestDTO;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.helpers.lookup.ServiceLocator;
import za.co.liberty.helpers.util.DateUtil;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.pages.MaintenanceBasePage;
import za.co.liberty.web.pages.panels.BasePanel;
import za.co.liberty.web.pages.transactions.model.RequestTransactionModel;
import za.co.liberty.web.wicket.markup.html.form.CurrencyAmountTextField;
import za.co.liberty.web.wicket.markup.html.form.SRSDateField;
import za.co.liberty.web.wicket.markup.html.form.SRSTextField;

/**
 * Transaction Panel for VED's
 * 
 * @author jzb0608 2023-03-27
 *
 */
@SuppressWarnings("unused")
public class ProcessAdvanceTransactionPanel extends BasePanel {
	
	private static final long serialVersionUID = 1L;
	
	private static final int PAGE_SIZE = 10;
	
	private static final Logger logger = Logger.getLogger(ProcessAdvanceTransactionPanel.class);
	
	private RequestTransactionModel pageModel = null;
	
	private SRSDateField  requestedDateField = null;

	private SRSTextField  descriptionTextField = null;
	private SRSTextField  amountTextField = null;

	private ModalWindow createSearchWindow;

	private boolean initialised = false;
	
	private transient IRequestTransactionGuiController guiController;
	
	@SuppressWarnings("unchecked")
	public ProcessAdvanceTransactionPanel(String id, EditStateType editState, 
			RequestTransactionModel pageModel, MaintenanceBasePage parentPage) {
		super(id, editState,parentPage);
		this.pageModel = pageModel;
		initialise();
	}
	
	
	private void initialise(){
		// Initialise the lists of values required
		if (!initialised) {
			initialised = true;
		}
		if (getEditState().isAdd() && getSelectedObject() != null) {
			// Set the requestedDate and startDate and default to today
			getSelectedObject().setRequestedDate(new java.sql.Date( 
					DateUtil.getInstance().getTodayDatePart().getTime()));
		}
	
		add(requestedDateField = createRequestedDateField("requestedDate"));
		add(descriptionTextField = createDescriptionField("description"));
		add(amountTextField = createAmountField("amount"));

	}

	/**
	 * Internal method to retrieve the selected object of the correct type.
	 * 
	 * @return
	 */
	protected ProcessAdvanceRequestDTO getSelectedObject() {
		return (ProcessAdvanceRequestDTO) pageModel.getSelectedItem();
	}
	
	/**
	 * Create reqeuestedDateField
	 * 
	 * @param id
	 * @return
	 */
	protected SRSDateField createRequestedDateField(String id) {
		
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
				// Set both fields (requested and startDate)
				//   They have similar behaviours
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
//		if(getEditState().isViewOnly()){
			text.setEnabled(false);
//		} else {
//			text.addNewDatePicker();
//		}
		return text;
	}

	
	/**
	 * Create createfullAmountField
	 * 
	 * @param id
	 * @return
	 */
	protected SRSTextField createAmountField(String id) {
		
		IModel<CurrencyAmount> fieldModel = new IModel<CurrencyAmount>() {
			private static final long serialVersionUID = 1L;

			public CurrencyAmount getObject() {
				return getSelectedObject().getAmount();
			}

			public void setObject(CurrencyAmount arg0) {
				getSelectedObject().setAmount(arg0);
			}

			public void detach() {
			}
		};
		final CurrencyAmountTextField tempSRSTextField = new CurrencyAmountTextField(id,fieldModel);
		tempSRSTextField.setEnabled(!getEditState().isViewOnly());
//		tempSRSTextField.setRequired(true);
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
				getSelectedObject().setDescription(arg0.toUpperCase());
			}

			public void detach() {
			}
		};
		SRSTextField tempSRSTextField = new SRSTextField(id,fieldModel);
		tempSRSTextField.setEnabled(!getEditState().isViewOnly());
		if(getEditState()== EditStateType.MODIFY){
			tempSRSTextField.setEnabled(false);
		}
		return tempSRSTextField;
	}
	
	public String getPageName() {
		return "Process Advance";
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
