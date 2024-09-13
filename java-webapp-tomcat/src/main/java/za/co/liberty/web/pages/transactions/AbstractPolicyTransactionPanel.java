package za.co.liberty.web.pages.transactions;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.naming.NamingException;

import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow.MaskType;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import za.co.liberty.business.guicontrollers.transactions.IPolicyTransactionGuiController;
import za.co.liberty.common.domain.CurrencyAmount;
import za.co.liberty.dto.gui.templates.DescriptionDTO;
import za.co.liberty.dto.transaction.DistributePolicyEarningDTO;
import za.co.liberty.dto.transaction.IPolicyTransactionDTO;
import za.co.liberty.dto.transaction.IPolicyTransactionModel;
import za.co.liberty.dto.transaction.RecordPolicyInfoDTO;
import za.co.liberty.exceptions.data.ValidationException;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.helpers.lookup.ServiceLocator;
import za.co.liberty.interfaces.agreements.requests.RequestKindType;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.pages.panels.BasePanel;
import za.co.liberty.web.wicket.markup.html.form.SRSAbstractChoiceRenderer;
import za.co.liberty.web.wicket.markup.html.form.SRSDateField;

/**
 * Base abstract policy transaction panel
 * 
 */
public abstract class AbstractPolicyTransactionPanel extends BasePanel {

	private static final long serialVersionUID = 1238515194710946734L;
	
	protected Panel transactionFieldsPanel;
	protected Form<?> transactionFieldsForm;
	final private IPolicyTransactionModel pageModel;
	final private EditStateType editStateType;
//	protected ModalWindow confirmationWindow;
	protected DropDownChoice<?> premiumFrequencyField;
	
	private transient IPolicyTransactionGuiController guiController;
	FeedbackPanel feedbackPanel;
	
	public AbstractPolicyTransactionPanel(String id, EditStateType editStateType, IPolicyTransactionModel model, Page parentPage) {
		super(id, editStateType, parentPage);
		this.pageModel = model;
		this.editStateType = editStateType;
		this.setOutputMarkupId(true);
		// Remove as it is not used by any subclasses and causing issues with HTML hierarchy
//		add(confirmationWindow = createConfirmationModalWindow("submitConfirmation", null));
	}
	
	/**
	 * 
	 * @return
	 */
	public boolean addConfirmationWindow() {
		return true;
	}
	
	/**
	 * Create the amount field 
	 * 
	 * @param id
	 * @return
	 */
	protected TextField<?> createAmountField(String id) {
		TextField<CurrencyAmount> text = new TextField<CurrencyAmount>(id, new IModel<CurrencyAmount>() {
			private static final long serialVersionUID = -1060562129103084694L;

			public CurrencyAmount getObject() {
				return pageModel.getSelectedObject().getAmount();
			}

			public void setObject(CurrencyAmount arg0) {
				pageModel.getSelectedObject().setAmount(arg0);
			}

			public void detach() {
			}
		}, CurrencyAmount.class);
		text.add(new AjaxFormComponentUpdatingBehavior("change") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
			}
		});
		text.setOutputMarkupId(true);
		text.setEnabled(!getEditState().isViewOnly() && pageModel.getSelectedObject().getRejectOid() == null);
		return text;
	}
	/**
	 * Create premium frequency
	 * 
	 * @param id
	 * @return
	 */
	protected DropDownChoice<?> createPremiumFrequencyField(String id) {
		
		final Map<Integer, DescriptionDTO> map = new HashMap<Integer, DescriptionDTO>();
		for (DescriptionDTO d : pageModel.getAllFrequencyTypes()) {
			map.put(d.getReference(), d);
		}
		
		IModel<Object> fieldModel = new IModel<Object>() {
			private static final long serialVersionUID = 1L;

			public Object getObject() {
				return map.get((pageModel.getSelectedObject()).getPremiumFrequency());
			}

			public void setObject(Object arg0) {
				(pageModel.getSelectedObject()).setPremiumFrequency((arg0 == null) ? null : ((DescriptionDTO)arg0).getReference());
//				model.setBenefitType((DescriptionDTO) arg0);
			}

			public void detach() {
			}
		};

		DropDownChoice<?> field = new DropDownChoice<Object>(id, fieldModel, pageModel.getAllFrequencyTypes(), new SRSAbstractChoiceRenderer<Object>() {

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
	
	protected TextField<?> createOwnerNameField(String id) {
		IModel<String> fieldModel = new IModel<String>() {
			private static final long serialVersionUID = 1L;
			public String getObject() {
				if (pageModel.getSelectedObject() instanceof DistributePolicyEarningDTO) {
					return ((DistributePolicyEarningDTO)pageModel.getSelectedObject()).getDPELifeAssuredName();
				} else {
					return ((RecordPolicyInfoDTO)pageModel.getSelectedObject()).getLifeAssured();
				}
			}
			public void setObject(String arg0) {
				if (pageModel.getSelectedObject() instanceof DistributePolicyEarningDTO) {
					((DistributePolicyEarningDTO)pageModel.getSelectedObject()).setDPELifeAssuredName(arg0);
				} else {
					((RecordPolicyInfoDTO)pageModel.getSelectedObject()).setLifeAssured(arg0);
				}
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
	
	protected SRSDateField createEffectiveDateField(String id) {
		//TODO jean, have to update this as it won't work for aum
		IModel<Date> fieldModel = new IModel<Date>() {
			private static final long serialVersionUID = 1L;

			public Date getObject() {
					return ((IPolicyTransactionDTO) pageModel.getSelectedObject()).getEffectiveDate();
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
					((IPolicyTransactionDTO) pageModel.getSelectedObject()).setEffectiveDate(dte);
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
	 * Create Start Date field
	 * 
	 * @param id
	 * @return
	 */
	protected SRSDateField createPolicyStartDateField(String id) {
		
		IModel<Date> fieldModel = new IModel<Date>() {
			private static final long serialVersionUID = 1L;
			public Date getObject() {
				return pageModel.getSelectedObject().getPolicyStartDate();
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
				pageModel.getSelectedObject().setPolicyStartDate(dte);
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

	

	
	
	protected void doValidation(RequestKindType requestKind) throws ValidationException {
		getGuiController().doValidation(requestKind,pageModel);
	}
	
	protected FeedbackPanel getFeedbackPanel() {
		return getFeedBackPanel();
	}	
	
	protected ModalWindow createConfirmationModalWindow(String id, final String displayText){
		
		final ModalWindow window = new ModalWindow(id);
		window.setPageCreator(new ModalWindow.PageCreator() {
			
			private static final long serialVersionUID = 1L;

			public Page createPage() {
				return new TransactionCreationConfirmationPage(window, transactionFieldsForm, pageModel, displayText);
			}
		});
		
		window.setTitle("Confirm");
		window.setMinimalHeight(150);
		window.setInitialHeight(150);
		window.setMinimalWidth(300);
		window.setInitialWidth(300);
		window.setMaskType(MaskType.SEMI_TRANSPARENT);
		window.setCssClassName(ModalWindow.CSS_CLASS_GRAY);	
		window.setOutputMarkupId(true);
		window.setOutputMarkupPlaceholderTag(true);
//		window.setPageMapName("TransactionRejectXMLMessagePage");
		window.setCookieName("TransactionRejectXMLMessagePage");
		
		return window;
		
	}
	

	/**
	 * Return the GUI controller
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
}
