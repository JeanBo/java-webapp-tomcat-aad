/**
 * 
 */
package za.co.liberty.web.pages.transactions;

import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.request.cycle.RequestCycle;

import za.co.liberty.agreement.client.vo.RequestVO;
import za.co.liberty.business.guicontrollers.transactions.IRequestTransactionGuiController;
import za.co.liberty.dto.contracting.ResultAgreementDTO;
import za.co.liberty.dto.contracting.ResultPartyDTO;
import za.co.liberty.dto.pretransactionreject.RejectElementDTO;
import za.co.liberty.dto.transaction.DistributePolicyEarningDTO;
import za.co.liberty.dto.transaction.ExternalPaymentRequestDTO;
import za.co.liberty.dto.transaction.IPolicyTransactionDTO;
import za.co.liberty.dto.transaction.RecordPolicyInfoDTO;
import za.co.liberty.dto.transaction.RequestTransactionDTO;
import za.co.liberty.dto.transaction.VEDTransactionDTO;
import za.co.liberty.exceptions.data.ValidationException;
import za.co.liberty.exceptions.error.request.RequestException;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.helpers.lookup.ServiceLocator;
import za.co.liberty.interfaces.agreements.requests.RequestKindType;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.pages.BaseWindowPage;
import za.co.liberty.web.pages.panels.HelperPanel;
import za.co.liberty.web.pages.transactions.model.PolicyTransactionModel;
import za.co.liberty.web.pages.transactions.model.RequestTransactionModel;
import za.co.liberty.web.system.SRSAuthWebSession;

/**
 * Enables for the editing and saving of a rejected transaction.  Automatically determines the appropriate panel to use for
 * the transaction type.
 * 
 * @author jzb0608 2019-01-28
 *
 */
public class TransactionRejectEditMessagePage extends BaseWindowPage {

	private transient IRequestTransactionGuiController guiController;
	private static final Logger logger = Logger.getLogger(TransactionRejectEditMessagePage.class);
	private String pageName;
	private RequestTransactionModel pageModel;
	private RejectElementDTO rejectElementDTO;
//	private RequestTransactionDTO transactionDTO;
	
	private Panel contentPanel;
	private Button saveButton;
	private Button cancelRejectButton;
	private Form form;
	private FeedbackPanel feedbackPanel;
	private EditStateType editState;
	
	private ResultAgreementDTO resultAgreementDTO;
	private ResultPartyDTO resultPartyDTO;
	
	/**
	 * Default constructor 
	 * 
	 * @param window
	 * @param rejectElementDTO
	 * @param pageName
	 */
	public TransactionRejectEditMessagePage(ModalWindow window, RejectElementDTO rejectElementDTO, String pageName) {
		this.pageName = pageName;
		this.rejectElementDTO = rejectElementDTO;
		this.rejectElementDTO = rejectElementDTO;
		
		// Default action is modify, changes to view after an update
		editState = EditStateType.MODIFY;
		initPageModel(rejectElementDTO);
		
		
		/**
		 * Initialise the form labels 
		 */
		form = new Form("pageForm");
		form.setOutputMarkupId(true);
		add(new Label("requestKind", rejectElementDTO.getRequestKindType()));
		add(new Label("rejectId", ""+rejectElementDTO.getOid()));
		add(new Label("agreementNr", ""+rejectElementDTO.getSrsAgmtNo()));
		add(new Label("contractNr", rejectElementDTO.getPolicyRef()));
		add(new Label("time", rejectElementDTO.getFormattedTime()));
		add(new Label("sourceSystem", rejectElementDTO.getComponentId()));
		add(new Label("errorMessage", rejectElementDTO.getErrorCode().getGuiMessage()));
		add(new Label("batchId", "N/A"));
		add(new Label("batchLineNr", "N/A"));
		add(new Label("partyName", resultPartyDTO != null ? resultPartyDTO.getName() : ""));
		add(new Label("agreementStatus", resultAgreementDTO != null ? resultAgreementDTO.getAgreementStatusType().toString() : ""));
		add(new Label("consCode", resultAgreementDTO != null ? resultAgreementDTO.getConsultantCode().toString() : ""));
		
		add(form);
		
		// Feedback panel, need our own to change position
		feedbackPanel = new FeedbackPanel("errorMessages");
		feedbackPanel.setOutputMarkupId(true);
		form.add(feedbackPanel);
		
	
		form.add(contentPanel = createContentPanel("fieldsPanel"));
		
		form.add(saveButton = createSaveButton("saveButton"));
		form.add(cancelRejectButton = createCancelButton("cancelButton"));
		
		form.add(HelperPanel.getInstance("editmessage", new Label("value", "")));  // Not used for now
		// add(createXMLMessageTextAreaField("xmlMessage", displayText));

	}

	/**
	 * Create the save button and route all submits to {@linkplain #doSave_onSubmit()}
	 * 
	 * @param id
	 * @return
	 */
	private Button createSaveButton(String id) {
		Button button = new AjaxButton(id) {
			private static final long serialVersionUID = 1L;
			@Override
			protected void onSubmit(AjaxRequestTarget arg0, Form<?> arg1) {
				doSave_onSubmit();
				
			}
			
		
			@Override
			protected void onComponentTag(ComponentTag tag) {
				super.onComponentTag(tag);
				tag.getAttributes().put("value", "Save");
				tag.getAttributes().put("type", "submit");
			}

		};
		button.setOutputMarkupId(true);
		button.setEnabled(!editState.isViewOnly());
		return button;
	}
	
	/**
	 * Create the reject button and route all submits to {@linkplain #doSave_onSubmit()}
	 * 
	 * @param id
	 * @return
	 */
	private Button createCancelButton(String id) {
		Button button = new Button(id) {
		
			@Override
			protected void onComponentTag(ComponentTag tag) {
				super.onComponentTag(tag);
				tag.getAttributes().put("value", "Cancel Reject");
				tag.getAttributes().put("type", "button");
			}

		};
		button.add(new AjaxEventBehavior("click") {
			
			private static final long serialVersionUID = 9167426871406627023L;

			@Override
			protected void onEvent(AjaxRequestTarget arg0) {
				doCancelReject_onSubmit();
			}
			
		});
		button.setOutputMarkupId(true);
		button.setEnabled(!editState.isViewOnly());
		button.setDefaultFormProcessing(false);
//		button.
		return button;
	}
	
	/**
	 * Create the content panel for the rejects.  This will show the appropriate 
	 * panel for the request kind type.
	 * 
	 * @param id
	 * @return
	 */
	private Panel createContentPanel(String id) {
		
		Panel panel = null;
		
		if (pageModel.getSelectedItem() instanceof ExternalPaymentRequestDTO) {
			if (logger.isDebugEnabled())
				logger.debug("Setting container panel to \"TransactionExternalPaymentPanel\"");
			panel = new TransactionExternalPaymentPanel(id, editState, pageModel,null);
		} else if (pageModel.getSelectedItem() instanceof RecordPolicyInfoDTO) {
			pageModel.setPanelModel(new PolicyTransactionModel());
			((PolicyTransactionModel)pageModel.getPanelModel()).setSelectedObject((IPolicyTransactionDTO) pageModel.getSelectedItem());
			panel = new PolicyTransactionInfoPanel(id, editState, 
					pageModel, null, false);	//getEditState().isAdd());
		} else if (pageModel.getSelectedItem() instanceof DistributePolicyEarningDTO) {
			pageModel.setPanelModel(new PolicyTransactionModel());
			((PolicyTransactionModel)pageModel.getPanelModel()).setSelectedObject((IPolicyTransactionDTO) pageModel.getSelectedItem());
			panel = new PolicyTransactionDPEPanel(id, editState, 
					pageModel, null, false); 	//getEditState().isAdd());
		} else if (pageModel.getSelectedItem() instanceof VEDTransactionDTO) {
			pageModel.setPanelModel(null);
			panel = new TransactionVariableEarningDeductionPanel(id, editState, 
					pageModel, null ); 	
		}
		
		
		if (panel == null ) {
			panel = new EmptyPanel(id);
		}
		panel.setOutputMarkupId(true);
		return panel;
	}
	
	/**
	 * Initialise the page model
	 * 
	 * @param rejectElementDTO
	 */
	private void initPageModel(RejectElementDTO rejectElementDTO) {
		
		pageModel = new RequestTransactionModel();

		try {
			logger.info("Convert reject to transaction");
			RequestTransactionDTO transactionDTO = getGuiController().convertRejectToTransaction(rejectElementDTO);
			resultAgreementDTO =  getGuiController().getLinkedAgreement(rejectElementDTO);
			if (resultAgreementDTO != null) {
				resultPartyDTO =  getGuiController().getLinkedParty(resultAgreementDTO);
			}
			pageModel.setSelectedItem(transactionDTO);
		} catch (ValidationException e) {
			logger.error("Validation error occurred while trying to initialise reject " + rejectElementDTO.getOid(), e);
		}
		
		
	}
	
	/**
	 * Disable editing after succesful changes
	 * 
	 * @param target
	 */
	private void disableEditing(AjaxRequestTarget target) {
		editState = EditStateType.VIEW;
		Panel tmp = createContentPanel("fieldsPanel");
		contentPanel.replaceWith(tmp);
		contentPanel = tmp;
		
		saveButton.setEnabled(false);
		cancelRejectButton.setEnabled(false);
		form.setEnabled(false);
		
		if (target != null) {
			target.add(contentPanel);
			target.add(saveButton);
			target.add(cancelRejectButton);
			target.add(form);
			target.add(feedbackPanel);
			
		}
	}

	/**
	 * Do the cancel reject action.
	 */
	public void doCancelReject_onSubmit() {
		RequestKindType requestKind = RequestKindType.getRequestKindTypeForKind(rejectElementDTO.getRequestKind());
		logger.info("Do Cancel Reject for " + requestKind + " and reject oid " + rejectElementDTO.getOid());
		try {
			getGuiController().cancelReject(pageModel.getSelectedItem().getRejectOid());
			getSession().info("Record was cancelled successfully");
			disableEditing(RequestCycle.get().find(AjaxRequestTarget.class));

		}catch (RuntimeException e){
			logger.info("An internal error occurred:" + e.getMessage(), e);
			error("An internal error occurred:" + e.getMessage());
		} catch (ValidationException e) {
			logger.warn("Had validation errors " + e.getErrorMessages().size());
			for (String err : e.getErrorMessages()) {
				this.error(err);
			}
//		} catch (RequestException e) {
//			logger.error("Unable to raise request - " + e.getMessage(),e);
//			error("Unable to raise request - " + e.getMessage());			
		} finally { 
			AjaxRequestTarget t = RequestCycle.get().find(AjaxRequestTarget.class);
			if (t != null)
				t.add( feedbackPanel);
		}
	}
	/**
	 * Do the save action by validating and then raising the request.
	 */
	public void doSave_onSubmit() {
		RequestKindType requestKind = RequestKindType.getRequestKindTypeForKind(rejectElementDTO.getRequestKind());
		logger.info("Do Save for " + requestKind);
		try {
			// Do not send the session user as this is a reject and additional validation should not be done.
			getGuiController().doTransactionValidation(null,
				rejectElementDTO.getSrsAgmtNo(),
				requestKind, 
				pageModel.getSelectedItem());
			
			RequestVO requestVO = getGuiController().raiseRequest(SRSAuthWebSession.get()
					.getSessionUser(), 
					rejectElementDTO.getSrsAgmtNo(), 
					requestKind, 
					pageModel.getSelectedItem());
				
			String requestDesc = (requestVO!=null && requestVO.getObjectReference()!= null)? 
					" - with oid " + requestVO.getObjectReference().getObjectOid() : "";
			getSession().info("Record was saved successfully" + requestDesc);		
		
			disableEditing(RequestCycle.get().find(AjaxRequestTarget.class));
		}catch (RuntimeException e){
			logger.info("An internal error occurred:" + e.getMessage(), e);
			error("An internal error occurred:" + e.getMessage());
		} catch (ValidationException e) {
			logger.warn("Had validation errors " + e.getErrorMessages().size());
			for (String err : e.getErrorMessages()) {
				this.error(err);
			}
		} catch (RequestException e) {
			logger.error("Unable to raise request - " + e.getMessage(),e);
			error("Unable to raise request - " + e.getMessage());			
		} finally { 
			AjaxRequestTarget t = RequestCycle.get().find(AjaxRequestTarget.class);
			if (t != null)
				t.add( feedbackPanel);
		}
	}
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see za.co.liberty.web.pages.BaseWindowPage#getPageName()
	 */
	@Override
	public String getPageName() {
		return pageName;
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
	
	@Override
	public boolean isShowFeedBackPanel() {
		// suppress the default panel and show our own in the correct location on the GUI.
		return false;
	}
}