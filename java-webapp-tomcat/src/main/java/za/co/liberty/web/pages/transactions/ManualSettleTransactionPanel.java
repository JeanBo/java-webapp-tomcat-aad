package za.co.liberty.web.pages.transactions;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AbstractAutoCompleteRenderer;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.IAutoCompleteRenderer;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.Response;
import org.wicketstuff.select2.ChoiceProvider;
import org.wicketstuff.select2.Select2Choice;

import za.co.liberty.business.guicontrollers.transactions.IRequestTransactionGuiController;
import za.co.liberty.common.domain.CurrencyAmount;
import za.co.liberty.dto.spec.TypeDTO;
import za.co.liberty.dto.transaction.SettleRequestDTO;
import za.co.liberty.dto.transaction.VEDTransactionDTO;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.helpers.lookup.ServiceLocator;
import za.co.liberty.helpers.util.DateUtil;
import za.co.liberty.interfaces.agreements.requests.EarningAndDeductionParentType;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.pages.MaintenanceBasePage;
import za.co.liberty.web.pages.MaintenanceBasePage.SelectionForm;
import za.co.liberty.web.pages.panels.BasePanel;
import za.co.liberty.web.pages.transactions.model.RequestTransactionModel;
import za.co.liberty.web.wicket.markup.html.form.CurrencyAmountTextField;
import za.co.liberty.web.wicket.markup.html.form.SRSDateField;
import za.co.liberty.web.wicket.markup.html.form.SRSTextField;

/**
 * Transaction Panel for Manual Settlements
 * 
 * @author jzb0608 2023-06-27
 *
 */
@SuppressWarnings("unused")
public class ManualSettleTransactionPanel extends BasePanel {
	
	private static final long serialVersionUID = 1L;
	
	private static final int PAGE_SIZE = 10;
	
	private static final Logger logger = Logger.getLogger(ManualSettleTransactionPanel.class);
	
	private RequestTransactionModel pageModel = null;

	private SRSTextField  descriptionTextField = null;

	public EditStateType pageEditState;
	private ModalWindow createSearchWindow;

	private boolean initialised = false;
	
	private transient IRequestTransactionGuiController guiController;
	
	@SuppressWarnings("unchecked")
	public ManualSettleTransactionPanel(String id, EditStateType editState, 
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
		add(descriptionTextField = createDescriptionField("description"));
	}
	

	/**
	 * Internal method to retrieve the selected object of the correct type.
	 * 
	 * @return
	 */
	protected SettleRequestDTO getSelectedObject() {
		return (SettleRequestDTO) pageModel.getSelectedItem();
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
		return "Variable Earnings and Deductions";
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
