package za.co.liberty.web.pages.transactions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.ajax.markup.html.form.AjaxFallbackButton;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.cycle.RequestCycle;

import com.inmethod.grid.IGridColumn;
import com.inmethod.grid.SizeUnit;

import za.co.liberty.agreement.client.vo.RequestVO;
import za.co.liberty.business.guicontrollers.transactions.IRequestTransactionGuiController;
import za.co.liberty.dto.agreement.request.RequestEnquiryRowDTO;
import za.co.liberty.dto.transaction.IPolicyTransactionDTO;
import za.co.liberty.dto.transaction.RequestTransactionDTO;
import za.co.liberty.exceptions.data.DataNotFoundException;
import za.co.liberty.exceptions.data.QueryTimeoutException;
import za.co.liberty.exceptions.data.ValidationException;
import za.co.liberty.exceptions.error.request.RequestException;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.helpers.lookup.ServiceLocator;
import za.co.liberty.interfaces.agreements.requests.PropertyKindType;
import za.co.liberty.interfaces.agreements.requests.RequestKindType;
import za.co.liberty.web.data.enums.ContextType;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.data.enums.RequestTransactionKindType;
import za.co.liberty.web.pages.MaintenanceBasePage;
import za.co.liberty.web.pages.panels.AbstractTableMaintenanceSelectionPanel;
import za.co.liberty.web.pages.panels.ButtonHelperPanel;
import za.co.liberty.web.pages.panels.DefaultMaintenanceSelectionPanel;
import za.co.liberty.web.pages.transactions.model.PolicyTransactionModel;
import za.co.liberty.web.pages.transactions.model.RequestTransactionModel;
import za.co.liberty.web.system.SRSAuthWebSession;
import za.co.liberty.web.wicket.ajax.attributes.SRSAjaxCallListener;
import za.co.liberty.web.wicket.markup.html.form.SRSAbstractChoiceRenderer;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGrid;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGridColumn;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGridColumnUpdated;

/**
 * <p>Generic Transaction GUI page allowing for various different simple transactions to be raised.</p>
 * 
 * <p>All new requests added to this GUI must be done using a similar method to ExternalPayments and NOT 
 * DPE and PI (policy info) transactions.</p>
 * 
 * TODO jzb0608 - Remember to add the option to edit another transaction as per current DPE GUI (low)
 * 
 * 
 * @author jzb0608
 *
 */
public class TransactionGuiPage extends MaintenanceBasePage<Object> {

	private static final long serialVersionUID = 1L;
	
	private RequestTransactionModel pageModel;
	private transient IRequestTransactionGuiController guiController;
	private static final Logger logger = Logger.getLogger(TransactionGuiPage.class);
	
	public TransactionGuiPage(){
		super(null);
	}
	
	public TransactionGuiPage(RequestTransactionDTO e){
		super(e);
	}
	
	/**
	 * Create the main container panel ensuring the correct panels are switched depending on the selection.
	 * 
	 */
	public Panel createContainerPanel() {
		Panel panel = null;
		if (pageModel.getSelectedItem() == null) {
			if (logger.isDebugEnabled())
				logger.debug("Setting container panel to Empty");
			panel = new EmptyPanel(CONTAINER_PANEL_NAME);
			if (getPageContextDTO().getAgreementContextDTO().getAgreementNumber() == null) {
				panel.warn("Please select an agreement in the context");
			}
		} else {
			RequestTransactionKindType type = pageModel.getSelectedBoxName();
			if (type == RequestTransactionKindType.EXTERNAL_PAYMENT) {
				if (logger.isDebugEnabled())
					logger.debug("Setting container panel to \"TransactionExternalPaymentPanel\"");
				panel = new TransactionExternalPaymentPanel(CONTAINER_PANEL_NAME, getEditState(), 
						pageModel,this);
			} else if (type == RequestTransactionKindType.RECORD_POLICY_INFO) {
				// TODO jzb0608 - remove this, must be automatically set
				pageModel.setPanelModel(new PolicyTransactionModel());
				((PolicyTransactionModel)pageModel.getPanelModel()).setSelectedObject((IPolicyTransactionDTO) pageModel.getSelectedItem());
				panel = new PolicyTransactionInfoPanel(CONTAINER_PANEL_NAME, getEditState(), 
						pageModel, this, false);	//getEditState().isAdd());
			} else if (type == RequestTransactionKindType.DISTRIBUTED_POLICY_EARNING) {
				// TODO jzb0608 - remove this, must be automatically set
				pageModel.setPanelModel(new PolicyTransactionModel());
				((PolicyTransactionModel)pageModel.getPanelModel()).setSelectedObject((IPolicyTransactionDTO) pageModel.getSelectedItem());
				panel = new PolicyTransactionDPEPanel(CONTAINER_PANEL_NAME, getEditState(), 
						pageModel, this, false); 	//getEditState().isAdd());
			} else if (type == RequestTransactionKindType.VARIABLE_EARNING_DEDUCTION) {
				pageModel.setPanelModel(null);
				panel = new TransactionVariableEarningDeductionPanel(CONTAINER_PANEL_NAME, getEditState(), 
						pageModel, this); 	
			} else if (type == RequestTransactionKindType.MANUAL_SETTLE) {
				pageModel.setPanelModel(null);
				panel = new ManualSettleTransactionPanel(CONTAINER_PANEL_NAME, getEditState(), 
						pageModel, this); 	
			} else if (type == RequestTransactionKindType.PROCESS_ADVANCE) {
				pageModel.setPanelModel(null);
				panel = new ProcessAdvanceTransactionPanel(CONTAINER_PANEL_NAME, getEditState(), 
						pageModel, this); 	
			}
		}
		panel.setOutputMarkupId(true);
		return panel;
	}
	
	
	
	@Override
	public Button[] createNavigationalButtons() {
		return new Button[] {
				createSaveButton("button1", true), 
				createCancelButton("button2") };

	}
	
	/**
	 * A generic cancel button that invalidates the page. Overridden to produce 
	 * an Ajax button as cancel was not always working. 
	 * 
	 * @param id
	 * @return
	 */
	@Override
	protected Button createCancelButton(String id) {
		
		Button button = new AjaxButton(id) {
			private static final long serialVersionUID = 5123766713711807176L;
			@Override
			protected void onSubmit(AjaxRequestTarget arg0, Form<?> arg1) {
				invalidatePage();
				setResponsePage(TransactionGuiPage.class);					
			}
			
			@Override
			protected void onComponentTag(ComponentTag tag) {
				super.onComponentTag(tag);
				tag.getAttributes().put("value", "Cancel");
				tag.getAttributes().put("type", "submit");
			}

		};
		button.setOutputMarkupId(true);
		button.setDefaultFormProcessing(false);
		return button;
		
	}

	/**
	 * Create a default save button that calls {@link #doSave_onSubmit()}
	 * 
	 * @param id
	 * @return
	 */
	protected Button createSaveButton(String id,
			final boolean isShowOverlay) {
		
		Button button = new AjaxButton(id) {
			private static final long serialVersionUID = 1L;
			@Override
			protected void onSubmit(AjaxRequestTarget arg0, Form<?> arg1) {
				doSave_onSubmit();
				
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
//			
			@Override
			protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
				super.updateAjaxAttributes(attributes);
			        
			        // SRS Convenience method for overLay hiding/showing
			        attributes.getAjaxCallListeners().add(new SRSAjaxCallListener());
			}
			
			@Override
			protected void onComponentTag(ComponentTag tag) {
				super.onComponentTag(tag);
				tag.getAttributes().put("value", "Save");
				tag.getAttributes().put("type", "submit");
			}

		};
		button.setOutputMarkupId(true);
		return button;

	}
	
	@Override
	public void doSave_onSubmit() {
		if (logger.isDebugEnabled())
			logger.debug("Do Save transaction");
		
		try {
			getGuiController().doTransactionValidation(SRSAuthWebSession.get().getSessionUser(),
				getPageContextDTO().getAgreementContextDTO().getAgreementNumber(),
				pageModel.getSelectedBoxName().getRequestKind(), 
				pageModel.getSelectedItem());
			
			RequestVO requestVO = getGuiController().raiseRequest(SRSAuthWebSession.get()
					.getSessionUser(), 
					getPageContextDTO().getAgreementContextDTO().getAgreementNumber(), 
					pageModel.getSelectedBoxName().getRequestKind(), 
					pageModel.getSelectedItem());
			
			// TODO jzb0608 - think we should clear the object instead or something
			invalidatePage();	
			String requestDesc = (requestVO!=null && requestVO.getObjectReference()!= null)? 
					" - with oid " + requestVO.getObjectReference().getObjectOid() : "";
			getSession().info("Record was saved successfully" + requestDesc);			
			setResponsePage(this.getClass());
		}catch (RuntimeException e){
			logger.error("An internal error occurred:" + e.getMessage(), e);
			error("An internal error occurred:" + e.getMessage());
		} catch (ValidationException e) {
			for (String err : e.getErrorMessages()) {
				this.error(err);
			}
		} catch (RequestException e) {
			logger.error("Unable to raise request - " + e.getMessage(),e);
			error("Unable to raise request - " + e.getMessage());			
		} finally { 
			AjaxRequestTarget t = RequestCycle.get().find(AjaxRequestTarget.class);
			if (t != null)
				t.add( getFeedbackPanel());
		}
	}
	
	
	
	@SuppressWarnings("unchecked")
	@Override
	public DefaultMaintenanceSelectionPanel createSelectionPanel() {

		/**
		 * Create the selection panel.  Must show a pre-selection table in some cases as well as a reverse
		 * button, both depend on request kind type.
		 * 
		 */
		return new AbstractTableMaintenanceSelectionPanel<RequestTransactionDTO>(SELECTION_PANEL_NAME,"Transaction Kind:",pageModel, this, 
				selectionForm, RequestTransactionDTO.class) {
					
				private static final long serialVersionUID = -2623730454856120331L;
				
				protected Button reverseButton;
				
				/**
				 * Select the type of transaction (disabled if no agreement)
				 */
				@Override
				public Component createSelectionListField(String id) {
					Component c = createComboList(id);
					c.setEnabled(getPageContextDTO().getAgreementContextDTO().getAgreementNumber() != null && getEditState().isViewOnly());
					return c;
				}
				
				/**
				 * Update the edit state for this panel (enables / disables certain components)
				 */
				@Override
				public void setEditState(EditStateType newState, AjaxRequestTarget target) {
					super.setEditState(newState, target);
					selectionListField.setEnabled(getPageContextDTO()
							.getAgreementContextDTO().getAgreementNumber() != null && getEditState().isViewOnly());
					if (target!=null)
						target.add(selectionListField);

				}
				
				/**
				 * By default this button must be disabled, only enabled once agreement and type is selected.
				 */
				@Override
				protected Button createAddNewButton(String id) {
					Button b = super.createAddNewButton(id, true);
					b.setEnabled(false);
					return b;
				}
				/**
				 * Called when Add new is submitted. Notify parent and 
				 * swap panels.  Ensure that selected item is set before calling.
				 * 
				 * @param target
				 * @param form
				 */
				@Override
				public void doAddNew_onSubmit(AjaxRequestTarget target, Form form) {
					if (logger.isDebugEnabled())
						logger.debug("Hey - doAddNew_onSubmit - pre");
					selectionListField.setEnabled(false);
					if (target!=null)
						target.add(selectionListField);
					super.doAddNew_onSubmit(target, form);
				}
				
				@Override
				public void doTableField_onSelect() {
					if (logger.isDebugEnabled())
							logger.debug("Select was clicked, object = " 
									+ ((RequestTransactionModel)this.pageModel).getSelectedItem());
					// TODO jean - do an auto retrieve for the request id using the 
					// 			normal request converters (via guicontroller)
					super.doTableField_onSelect();
				}

				/**
				 * Creating the table, is an empty panel at first and gets swapped back in on 
				 * selections.
				 *
				 */
				@Override
				public Panel createTableField(String id) {
					RequestTransactionKindType type = TransactionGuiPage.this.pageModel.getSelectedBoxName();
					if (type == null || !type.isShowTable()) {
						Panel p = new EmptyPanel(id);
						p.setOutputMarkupId(true);
						return p;
					}
					SRSDataGrid tempDataGrid = (SRSDataGrid) super.createTableField(id);
					tempDataGrid.setRowsPerPage(5);
					tempDataGrid.setContentHeight(80, SizeUnit.PX);
					return tempDataGrid;					
				}
				
				/**
				 * This is called after the table changed
				 * 
				 */
				@Override
				public List<Object> filterTableData() {
					/*
					 * Retrieve a pre-selection list of values
					 */
					List<Object> tmpList =new ArrayList<Object>();
					RequestTransactionKindType type = TransactionGuiPage.this.pageModel.getSelectedBoxName();
					// Only search if show table is allowed
					if (type != null && type.isShowTable()) {
						 try {
							tmpList = getGuiController().findTableData(
									TransactionGuiPage.this.pageModel.getSelectedBoxName().getRequestKind(), 
									getPageContextDTO().getAgreementContextDTO().getAgreementNumber());
						} catch (RequestException e) {
							getFeedbackPanel().warn("Unable to retrieve historic transactions due to an internal error - " 
									+ e.getMessage());
						} catch (QueryTimeoutException e) {
							getFeedbackPanel().warn("Unable to retrieve historic transactions due to a query timeout");
						}	
					}
					return tmpList ;
				}

				@Override
				public List<IGridColumn> createTableFieldColumns() {
					/**
					 * This will show the columns required per request kind, for now only 
					 * one kind is configured.
					 */
					if (logger.isDebugEnabled())
						logger.debug("creatingTableFieldColumns - "
								+ TransactionGuiPage.this.pageModel.getSelectedBoxName());
					
					List<IGridColumn> list = new ArrayList<IGridColumn>();
					
					if (TransactionGuiPage.this.pageModel.getSelectedBoxName() != null) {

						RequestKindType requestKind = TransactionGuiPage.this.pageModel.getSelectedBoxName().getRequestKind();
						if (requestKind==RequestKindType.ProcessExternalPayments) {
							// External payments - in particular section8c
							list.add(new SRSDataGridColumn<RequestTransactionDTO>("date",
									new Model("Date"),"requestedDate",getEditState()).setInitialSize(120));
							list.add(new SRSDataGridColumn<RequestTransactionDTO>("description",
									new Model("Description"),"description",getEditState()).setInitialSize(120));			
							list.add(new SRSDataGridColumn<RequestTransactionDTO>("fullAmount",
									new Model("Full amount"),"fullAmount",getEditState()).setInitialSize(120));
							list.add(new SRSDataGridColumn<RequestTransactionDTO>("taxAmount",
									new Model("Tax Amount"),"taxAmount",getEditState()).setInitialSize(120));
							list.add(new SRSDataGridColumn<RequestTransactionDTO>("it88Amount",
									new Model("IT88 Amount"),"it88Amount",getEditState()).setInitialSize(120));
							list.add(new SRSDataGridColumn<RequestTransactionDTO>("directiveNumber",
								new Model("Directive"),"directiveNumber",getEditState()).setInitialSize(80));
							
						} else if (requestKind==RequestKindType.RecordPolicyInfo) {
							// Policy info transactions
							list.add(new SRSDataGridColumn<RequestTransactionDTO>("date",
									new Model("Date"),"requestedDate",getEditState()).setInitialSize(120));
							
							list.add(new SRSDataGridColumn<RequestTransactionDTO>("infoKind",
									new Model("Info Kind"),"infoKind",getEditState()).setInitialSize(120));
							
							list.add(new SRSDataGridColumn<RequestTransactionDTO>("policyNr",
									new Model("Policy Nr"),"policyNr",getEditState()).setInitialSize(120));
							
							list.add(new SRSDataGridColumn<RequestTransactionDTO>("lifeAssured",
									new Model("Life Assured"),"lifeAssured",getEditState()).setInitialSize(250));

							list.add(new SRSDataGridColumn<RequestTransactionDTO>("amount",
								new Model("Amount"),"amount",getEditState()).setInitialSize(80));
						} else if (requestKind == RequestKindType.ProcessAdvance || requestKind == RequestKindType.ManualSettle) {
							list.add(new SRSDataGridColumnUpdated<RequestEnquiryRowDTO>( "executedDate",
									new Model("Executed Date"),"executedDate","executedDate",getEditState()).setInitialSize(120));
							
							list.add(new SRSDataGridColumnUpdated<RequestEnquiryRowDTO>("requestKindType",
									new Model("Request Kind"),"requestKindType",getEditState()).setInitialSize(120));
							
							// Comment
//							RequestEnquiryRowDTO d = new RequestEnquiryRowDTO();
//							d.getAdditionalProperty
							String propStr = "additionalProperty[" + PropertyKindType.Description.getPropertyKind() + "]";
							list.add(
									new SRSDataGridColumnUpdated<RequestEnquiryRowDTO>("description",
									new Model("Comment"),propStr,getEditState()) {
										public Object getPropertyValue(RequestEnquiryRowDTO data, String objectProperty) {
											return data.getAdditionalPropertyMap().get(PropertyKindType.Description);
										}
									}.setInitialSize(250));
							
							// Amount
							propStr = "additionalProperty[" + PropertyKindType.Amount.getPropertyKind() + "]";
							list.add(
									new SRSDataGridColumnUpdated<RequestEnquiryRowDTO>("amount",
									new Model("Amount"),propStr,getEditState()) {
										public Object getPropertyValue(RequestEnquiryRowDTO data, String objectProperty) {
											return data.getAdditionalPropertyMap().get(PropertyKindType.Amount);
										}
									}.setInitialSize(160));
						
						}
						// TODO add more types here
						// TODO might want to put these configurations on enum??
					}
					return list;
				}

				@Override
				public List getSelectionList() {
					return Arrays.asList(RequestTransactionKindType.values());
				}
				
			
				/**
				 * Creates a new instance of the DTO 
				 * 
				 * @return
				 */
				@Override
				public Object getNewDtoInstance() {
					return getGuiController().initialiseDTO(
							TransactionGuiPage.this.pageModel.getSelectedBoxName().getRequestKind());
				}
				
				/**
				 * Override to enable or disable table selection.  Selection of a table entry may only be allowed on selection kinds that 
				 * have tableLists of the appropriate object type of this model.   InfoTable lists that does not allow selection may 
				 * store any objects.
				 * 
				 * @return
				 */
				@Override
				protected boolean isAllowTableSelection() {
					if (TransactionGuiPage.this.pageModel.getSelectedBoxName()==null) {
						return false;
					} else {
						RequestTransactionKindType t = TransactionGuiPage.this.pageModel.getSelectedBoxName();
						if (logger.isDebugEnabled())
							logger.debug("Changed Type '" + t + "' = isShowSelectionTable");
						return t.isShowSelectionTable();
					}
				}
				
				/**
				 * Called on selection type change i.e. type of transaction is changed.
				 *   This results in filterTableData being called
				 */
				@Override
				protected void doSelectionListField_onChange(
						AjaxRequestTarget target) {
					
					/* 
					 * Enable the add button once selected
					 */
					if (logger.isDebugEnabled())
						logger.debug("doSelectionListField_onChange  - before");
					
					super.doSelectionListField_onChange(target);
					super.swapTableField(target);
					pageModel.setSelectedItem(null); // reset content screen 
					swapContainerPanel(target);
					
					if (logger.isDebugEnabled())
						logger.debug("doSelectionListField_onChange  - after "
								+ "  ,selectedBoxName="+TransactionGuiPage.this.pageModel.getSelectedBoxName()
								+ "  ,selectedObject=");
					
					boolean hasAccess = TransactionGuiPage.this.pageModel.getSelectedBoxName()!=null 
							&& parent.hasAddAccess();
					
					if (TransactionGuiPage.this.pageModel.getSelectedBoxName()!=null) {
						/*
						 * Ensure that the user has access to raise this request
						 */
						if (!isAllowRaise(TransactionGuiPage.this.pageModel.getSelectedBoxName().getRequestKind())) {
							this.warn("Add button disabled as you do not have access to raise the request '" 
									+ TransactionGuiPage.this.pageModel.getSelectedBoxName().getRequestKind().getDescription()
									+ "'");
						}
						
						
						if (hasAccess) {
							try {
								getGuiController().doAgreementValidation(pageContextDTO.getAgreementContextDTO(),
											TransactionGuiPage.this.pageModel.getSelectedBoxName().getRequestKind());
								
							} catch (ValidationException e) {
								this.error("The selected agreement has failed validation for reasons stated below, add button disabled.");
								for (String err : e.getErrorMessages()) {
									this.error(err);
								}	
								hasAccess = false;
							} catch (DataNotFoundException e) {
								logger.error("Unable to find selected agreement, add button disabled.", e);
								// This should really not happen as there is always a valid agreement in context
								this.error("Unable to find selected agreement, add button disabled.");
							}
						}
						
						target.add(getFeedbackPanel());
						
					}
					
					// Enable the add only when combo selected and add access is allowed.
					addNewButton.setEnabled(hasAccess);
					target.add(addNewButton);

				}

				/**
				 * Override this to update the relevant type on the page model
				 * 
				 * @return
				 */
				@Override
				protected IModel getSelectedItemModel() {
					return new IModel<RequestTransactionKindType>() {

						private static final long serialVersionUID = 1L;
						public RequestTransactionKindType getObject() {
							return TransactionGuiPage.this.pageModel.getSelectedBoxName();
						}
						public void setObject(RequestTransactionKindType object) {
							selectedObject = object;
							TransactionGuiPage.this.pageModel.setSelectedBoxName(object);
						}

						public void detach() {							
						}
						
					};
				}

				/**
				 * Create the button panel
				 * 
				 * @return
				 */
				protected Panel createControlButtonPanel() {
					addNewButton = createAddNewButton("button2");
					reverseButton = createReverseButton("button1");
					
					Panel panel = ButtonHelperPanel.getInstance("controlButtonPanel",
							addNewButton, reverseButton);
					panel.setOutputMarkupId(true);
					return panel;

				}

				/**
				 * Create the reverse button
				 * 
				 * @param id
				 * @return
				 */
				protected Button createReverseButton(String id) {
					Button button = new AjaxFallbackButton(id, enclosingForm) {

						private static final long serialVersionUID = -5330766713711809776L;

						@Override
						protected void onComponentTag(ComponentTag tag) {
							super.onComponentTag(tag);
							tag.getAttributes().put("value", "Reverse");
							tag.getAttributes().put("type", "submit");
						}

						@Override
						protected void onSubmit(AjaxRequestTarget target, Form form) {
							if (logger.isDebugEnabled())
								logger.debug("Submit the reverse button");
						}
					};
					button.setOutputMarkupId(true);
					button.setVisible(false);
					button.setEnabled(false);
					return button;
				}
				
				
				@Override
				protected IChoiceRenderer getChoiceRenderer() {
					return new SRSAbstractChoiceRenderer() {
	
						private static final long serialVersionUID = 1L;
						public Object getDisplayValue(Object obj) {
							return obj.toString();
						}
						public String getIdValue(Object obj, int index) {
							return "" + ((RequestTransactionKindType) obj).ordinal();
						}
					};
				}
				
		};
		
	}
	
	@Override
	public Object initialisePageModel(Object obj, Object pageModelExtraValueObject) {
	
		RequestTransactionModel object = new RequestTransactionModel();
//		getGuiController().initialisePageModel(object);
		pageModel = object;
		return pageModel;
	}

	@Override
	public String getPageName() {
		return "Transactions";
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
	public ContextType getContextTypeRequired() {
		return ContextType.AGREEMENT;
	}

	@Override
	public boolean hasModifyAccess() {
		return true;
	}

	@Override
	public boolean hasAddAccess() {
		if (logger.isDebugEnabled())
			logger.debug("Checking has add access. viewOnly=" + getEditState().isViewOnly()
					+ "  ,selectedBoxName="+pageModel.getSelectedBoxName());
		if (pageModel.getSelectedBoxName()==null) { // || getEditState().isViewOnly()) {
			return false;
		}
		if (logger.isDebugEnabled())
			logger.debug("Checking has add access. isAllowRaise " +isAllowRaise(pageModel.getSelectedBoxName().getRequestKind()));
		
		return isAllowRaise(pageModel.getSelectedBoxName().getRequestKind());
	}	
	
}

