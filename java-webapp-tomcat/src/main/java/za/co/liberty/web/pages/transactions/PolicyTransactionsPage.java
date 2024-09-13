/**
 * 
 */
package za.co.liberty.web.pages.transactions;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.naming.NamingException;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormChoiceComponentUpdatingBehavior;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RadioChoice;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.PropertyModel;

import za.co.liberty.business.guicontrollers.transactions.IPolicyTransactionGuiController;
import za.co.liberty.dto.contracting.ResultAgreementDTO;
import za.co.liberty.dto.gui.context.PolicyTransactionTypeEnum;
import za.co.liberty.dto.transaction.DistributePolicyEarningDTO;
import za.co.liberty.dto.transaction.IPolicyTransactionDTO;
import za.co.liberty.dto.transaction.IPolicyTransactionModel;
import za.co.liberty.dto.transaction.RecordPolicyInfoDTO;
import za.co.liberty.dto.transaction.VEDTransactionDTO;
import za.co.liberty.dto.userprofiles.ContextAgreementDTO;
import za.co.liberty.dto.userprofiles.ContextDTO;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.helpers.lookup.ServiceLocator;
import za.co.liberty.web.data.enums.ContextType;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.pages.BasePage;
import za.co.liberty.web.pages.transactions.model.PolicyTransactionModel;
import za.co.liberty.web.system.SRSAuthWebSession;

/**
 * @author zzt2108
 * 
 */
public class PolicyTransactionsPage extends BasePage implements Serializable {

	IPolicyTransactionModel pageModel;
	private Form<?> pageForm;
	private AbstractPolicyTransactionPanel transactionTypePanel;
	private Panel transactionFieldsPanel;

	private final String FIELDS_PANEL = "fieldsPanel";
	private final String PAGE_FORM = "pageForm";
	private final String TRANSACTION_TYPE_PANEL = "transactionTypePanel";

	private transient IPolicyTransactionGuiController guiController;

	private ContextAgreementDTO agreementContextDTO = SRSAuthWebSession.get().getContextDTO().getAgreementContextDTO();
	private ContextDTO contextDTO = SRSAuthWebSession.get().getContextDTO();

	protected static final List<String> transactionSearchTypes;
	static {
		transactionSearchTypes = new ArrayList<String>();
		for (PolicyTransactionTypeEnum t : PolicyTransactionTypeEnum.values()) {
			transactionSearchTypes.add(t.getLabel());
		}
	}
	protected ResultAgreementDTO resultAgreementDTO;

	public PolicyTransactionsPage() {
		if (SRSAuthWebSession.get() != null
				&& SRSAuthWebSession.get().getContextDTO() != null
				&& SRSAuthWebSession.get().getContextDTO().getAgreementContextDTO() != null)
			agreementContextDTO = SRSAuthWebSession.get().getContextDTO().getAgreementContextDTO();
		
		setEditState(EditStateType.VIEW, null);
		pageContextDTO = SRSAuthWebSession.get().getContextDTO();
		pageModel = createModel();
		
		add(transactionTypePanel = createTransactionTypePanel(TRANSACTION_TYPE_PANEL, pageModel));
		add(pageForm = createPageForm(PAGE_FORM, null, true));
	}

	public PolicyTransactionsPage(Object object, EditStateType editStateType, boolean clearFields, Long agreementNr) {
		if (object != null) {
			if (editStateType != null)
				setEditState(editStateType, null);
			pageModel = createModel();// initializeModel((IPolicyTransactionDTO)
										// object);
			pageModel.setTopAgreementId(agreementNr);
			pageModel.setSelectedObject((IPolicyTransactionDTO) object);

			String transactionType = null;

			// TODO jean - should this use the request handler or some other more configurable method
			if (object instanceof RecordPolicyInfoDTO)
				transactionType = PolicyTransactionTypeEnum.RPI.getLabel();
			else if (object instanceof DistributePolicyEarningDTO)
				transactionType = PolicyTransactionTypeEnum.DPE.getLabel();
			else if (object instanceof VEDTransactionDTO);
				transactionType = PolicyTransactionTypeEnum.VED.getLabel();
			
			pageModel.setTransactionTypeLabel(transactionType);
			add(transactionTypePanel = createTransactionTypePanel(TRANSACTION_TYPE_PANEL, pageModel));
			add(pageForm = createPageForm(PAGE_FORM, transactionType, clearFields));
		}
	}

	private Form<?> createPageForm(String id, String transactionType, boolean clearFields) {

		if (pageForm == null)
			pageForm = new Form<Object>(id);

		if (transactionType == null) {
			pageForm.add(transactionFieldsPanel = new EmptyPanel(FIELDS_PANEL));
			return pageForm;
		}

		if (pageForm.get(FIELDS_PANEL) == null)
			pageForm.add(transactionFieldsPanel = createTransactionFieldsPanel(FIELDS_PANEL, pageModel, transactionType, clearFields));
		else {
			Panel panel = createTransactionFieldsPanel(FIELDS_PANEL, pageModel, transactionType, clearFields);

			transactionFieldsPanel.replaceWith(panel);
			transactionFieldsPanel = panel;
		}
		pageForm.setOutputMarkupId(true);
		return pageForm;
	}

	private AbstractPolicyTransactionPanel createTransactionTypePanel(String id, IPolicyTransactionModel model) {

		transactionTypePanel = new PolicyTransactionTypePanel(id, EditStateType.ADD, model, this);
		transactionTypePanel.add(createTransactionTypeChoiceField("transactionTypeGroup"));
		transactionTypePanel.get("transactionTypeGroup");
		return transactionTypePanel;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private RadioChoice<?> createTransactionTypeChoiceField(String id) {
		RadioChoice<?> field = new RadioChoice(id, new PropertyModel<String>(pageModel, "transactionTypeLabel"), transactionSearchTypes);

		field.add(new AjaxFormChoiceComponentUpdatingBehavior() {

			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				String value = (String) this.getComponent().getDefaultModelObject();

				if (agreementContextDTO == null
						|| agreementContextDTO.getAgreementNumber() == null) {
					error("There is no agreement in context. Please search for one from the Context Panel above");
					target.add(getFeedbackPanel());
					return;
				} else {
					if (PolicyTransactionTypeEnum.DPE.getLabel().equals(value)) {
						if (pageModel.getSelectedObject() == null)
							pageModel.setSelectedObject(new DistributePolicyEarningDTO());
					} else if (PolicyTransactionTypeEnum.RPI.getLabel().equals(value)) {
						if (pageModel.getSelectedObject() == null)
							pageModel.setSelectedObject(new RecordPolicyInfoDTO());
					}
					pageModel.setTopAgreementId(agreementContextDTO.getAgreementNumber());
				}

				createPageForm("pageForm", value, true);
				target.add(transactionFieldsPanel);
				target.add(pageForm);

			}
		});
		field.setOutputMarkupId(true);
		if (getEditState()==EditStateType.MODIFY) {
			field.setEnabled(false);
		}
		return field;
	}

	private Panel createTransactionFieldsPanel(String id, IPolicyTransactionModel model, String type, boolean clearFields) {

		Panel panel = null;

		if (type != null) {
			if (PolicyTransactionTypeEnum.DPE.getLabel().equals(type)) {
				if (model.getSelectedObject() == null)
					model.setSelectedObject(new DistributePolicyEarningDTO());
				panel = new PolicyTransactionDPEPanel(id, getEditState(), model, this, clearFields);
			} else if (PolicyTransactionTypeEnum.RPI.getLabel().equals(type)) {
				if (model.getSelectedObject() == null)
					model.setSelectedObject(new RecordPolicyInfoDTO());
				panel = new PolicyTransactionInfoPanel(id, getEditState(), model, this, clearFields);
			}
		} else {
			panel = new EmptyPanel(id);
		}

		return (AbstractPolicyTransactionPanel) panel;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see za.co.liberty.web.pages.BasePage#getPageName()
	 */
	@Override
	public String getPageName() {
		return "Policy Transaction Page";
	}

	private PolicyTransactionModel createModel() {
		PolicyTransactionModel model = new PolicyTransactionModel();
		if (guiController == null)
			guiController = getGuiController();

		guiController.initialisePageModel(model);

		return model;
	}

	/**
	 * Return an instance to the GuiController bean for this page.
	 * 
	 * @return
	 */
	protected IPolicyTransactionGuiController getGuiController() {
		if (guiController == null) {
			try {
				guiController = ServiceLocator.lookupService(IPolicyTransactionGuiController.class);
			} catch (NamingException e) {
				throw new CommunicationException(e);
			}
		}
		return guiController;
	}

	@Override
	public ContextType getContextTypeRequired() {
		return ContextType.AGREEMENT;
	}

}
