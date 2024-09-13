package za.co.liberty.web.pages.maintainagreement;

import java.awt.Dimension;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.NamingException;

import org.apache.wicket.Page;
import org.apache.wicket.PageReference;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow.MaskType;
import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.request.cycle.RequestCycle;

import za.co.liberty.business.guicontrollers.IAgreementPrivilegesController;
import za.co.liberty.business.guicontrollers.agreement.IAgreementGUIController;
import za.co.liberty.business.request.handlers.guirequest.CreateAgreementGuiRequestHandler;
import za.co.liberty.business.request.handlers.guirequest.TerminateAgreementGuiRequestHandler;
import za.co.liberty.dto.agreement.AgreementDTO;
import za.co.liberty.dto.agreement.PaymentSchedulerDTO;
import za.co.liberty.dto.agreement.maintainagreement.MaintainAgreementDTO;
import za.co.liberty.dto.agreement.maintainagreement.WorkflowDTO;
import za.co.liberty.dto.agreement.properties.TemporalPropertyDTO;
import za.co.liberty.dto.userprofiles.ContextAgreementDTO;
import za.co.liberty.dto.userprofiles.ContextPartyDTO;
import za.co.liberty.dto.userprofiles.ISessionUserProfile;
import za.co.liberty.exceptions.SystemException;
import za.co.liberty.exceptions.data.DataNotFoundException;
import za.co.liberty.exceptions.data.ValidationException;
import za.co.liberty.exceptions.error.request.RequestException;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.exceptions.security.TabAccessException;
import za.co.liberty.helpers.lookup.ServiceLocator;
import za.co.liberty.interfaces.agreements.AgreementKindType;
import za.co.liberty.interfaces.agreements.requests.RequestKindType;
import za.co.liberty.security.exceptions.SecurityException;
import za.co.liberty.srs.type.SRSType;
import za.co.liberty.web.data.enums.ContextType;
import za.co.liberty.web.data.enums.PanelToRequestMapping;
import za.co.liberty.web.pages.MaintenanceBasePage;
import za.co.liberty.web.pages.dialog.DialogWindowPopUp;
import za.co.liberty.web.pages.maintainagreement.model.MaintainAgreementPageModel;
import za.co.liberty.web.pages.maintainagreement.model.MaintainAgreementPageModelFactory;
import za.co.liberty.web.pages.maintainagreement.model.WorkflowDialogPopupModel;
import za.co.liberty.web.pages.panels.BaseModificationButtonsPanel;
import za.co.liberty.web.pages.panels.MaintenanceTabbedPanel;
import za.co.liberty.web.pages.request.WorkflowPage;
import za.co.liberty.web.system.SRSAuthWebSession;
import za.co.liberty.web.wicket.modal.SRSModalWindow;

/**
 * This class represents the Maintain Agreement PAGE.
 * 
 * @author pks2802
 * 
 */
public class MaintainAgreementPage extends MaintenanceBasePage<Integer>{

	private static final long serialVersionUID = 1L;

	private MaintainAgreementPageModel pageModel;

	private String pageName = "Maintain Agreement";
	
	private ModalWindow addAgreementWindow;
	
	private ModalWindow terminateWindow;
	
	private transient Logger logger = Logger.getLogger(MaintainAgreementPage.class.getName());
	
	private transient IAgreementGUIController guiController;

	private ContextPartyDTO partyContext;

	private SRSModalWindow workflowWindow;
	private ModalWindow dialogWindow;
	
	private PageReference workflowPageReference;
	
	/**
	 * 
	 */
	public MaintainAgreementPage() {
		this(null,null);
	}
	
	public MaintainAgreementPage(Object obj) {
		this(obj,null);
	}	
		

	/**
	 * @param obj
	 */
	public MaintainAgreementPage(Object obj, Integer currentTab) {
		super(obj,currentTab);
		
		Form f = new Form("wrappingForm");
		this.add(f);
		
		f.add(addAgreementWindow = createModalAddAgreementWindow("addNewWizzardWindow"));
		f.add(terminateWindow = createTerminateAgreementWindow("terminateAgreementWindow"));
		f.add(workflowWindow = createWorkflowWindow("workflowWindow"));
		f.add(dialogWindow = createDialogWindow("dialogWindow"));
		try {
			processDeferredLoading();
		} catch (DataNotFoundException e) {
			error("Data not found when trying to load agreement data: "+e.getMessage());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see za.co.liberty.web.pages.MaintenanceBasePage#createContainerPanel()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Panel createContainerPanel() {
		Panel panel = null;
		try {
			final boolean excludePaymentScheduler = pageModel!=null &&
			pageModel.getMaintainAgreementDTO()!=null &&
			pageModel.getMaintainAgreementDTO().getAgreementDTO()!=null &&
			pageModel.getMaintainAgreementDTO().getAgreementDTO().getKind() ==
					AgreementKindType.LIBERTY_ATWORK_SALARIEDEMP.getKind();
			
			Class[] disabledPanels = null;
			
			if (excludePaymentScheduler) {
				disabledPanels = new Class[] { PaymentSchedulerPanel.class };
			}
			
			panel = new MaintainAgreementPanel(CONTAINER_PANEL_NAME, pageModel,
					getEditState(), disabledPanels, this.getFeedbackPanel(),
					this,getGUIController());
		} catch (TabAccessException e) {			
			//display message that all tabs have been disabled
			error(e.getUserMessage());
			panel = new EmptyPanel(CONTAINER_PANEL_NAME);
		}		
		panel.setOutputMarkupId(true);
		return panel;
	}

	/**
	 * Create the modal window
	 * 
	 * @param id
	 * @return
	 */
	private ModalWindow createModalAddAgreementWindow(String id) {		
		final SRSModalWindow window = new SRSModalWindow(id) {
			
			@Override
			public String getModalSessionIdentifier() {
				return "MAINTAIN.AGREEMENT.ADD-";
			}
		};
		
		window.setTitle("Add New Agreement");	
		window.setPageCreator(new ModalWindow.PageCreator() {
			private static final long serialVersionUID = 1L;
			public Page createPage() {	
				window.clearModalPageModelInSession();
				return new AddAgreementWizardPage(
						window, 
						MaintainAgreementPage.this.partyContext);
			}			
		});		
		
		window.setWindowClosedCallback(new ModalWindow.WindowClosedCallback() {
			private static final long serialVersionUID = 1L;			
			public void onClose(AjaxRequestTarget target) {
				if (getLogger().isDebugEnabled())
					getLogger().debug("MaintainAgreementPage.add.setWindowClosedCallback.onClose   ");
				
				MaintainAgreementPageModel model = (MaintainAgreementPageModel) window.getSessionModelForPage();
				
				
				/**
				 * Now we do the stuff that would usually be done here.
				 */				
				if (model.isModalWizardSucces()) {
					if (getLogger().isDebugEnabled())
						getLogger().debug("  Reloading Page!!!   ");
					
					if (model.getModalWizardMessage()!=null) {
						getSession().info(model.getModalWizardMessage());
						getFeedbackPanel().info(model.getModalWizardMessage());
					} else {
						getFeedbackPanel().info("Add Agreement Requests have been raised.");
					}
					setResponsePage(MaintainAgreementPage.class);
					target.add(feedbackPanel);

				} else {
					feedbackPanel.info("Cancelled Add Agreement");
					target.add(feedbackPanel);
				}
				
				window.clearModalPageModelInSession();
			}			
		});
		
		
		
		
		// Initialise window settings
		window.setMinimalHeight(500);
		window.setInitialHeight(500);
		window.setMinimalWidth(850);
		window.setInitialWidth(850);
		window.setMaskType(MaskType.SEMI_TRANSPARENT);
		window.setCssClassName(ModalWindow.CSS_CLASS_GRAY);	
		window.setOutputMarkupId(true);
		window.setOutputMarkupPlaceholderTag(true);
		window.setCookieName("MAINTAIN_AGREEMENT_ADD_WINDOW");
		return window;
	}
	
	/**
	 * Create the modal window
	 * 
	 * @param id
	 * @return
	 */
	private ModalWindow createTerminateAgreementWindow(String id) {		
		final SRSModalWindow window = new SRSModalWindow(id) {

			@Override
			public String getModalSessionIdentifier() {
				return "MAINTAIN.AGREEMENT.TERMINATE-";
			}
			
		};
		window.setTitle("Terminate Agreement");				
		// Create the page
		window.setPageCreator(new ModalWindow.PageCreator() {
			private static final long serialVersionUID = 1L;
			public Page createPage() {				
				return new TerminateAgreementWizardPage(
						window, 
						MaintainAgreementPage.this.pageModel);
			}			
		});		
		
		window.setWindowClosedCallback(new ModalWindow.WindowClosedCallback() {
			private static final long serialVersionUID = 1L;			
			public void onClose(AjaxRequestTarget target) {
				if (getLogger().isDebugEnabled())
					getLogger().debug("MaintainAgreementPage.setWindowClosedCallback.onClose   ");
				
				MaintainAgreementPageModel model = (MaintainAgreementPageModel) window.getSessionModelForPage();
				
				
				/**
				 * Now we do the stuff that would usually be done here.
				 */				
				if (model.isModalWizardSucces()) {
					if (getLogger().isDebugEnabled())
						getLogger().debug("  Reloading Page!!!   ");
					getSession().info("Terminate Agreement Requests have been raised.");
					setResponsePage(MaintainAgreementPage.class);
//					onAfterAddParty(target, model.getPartyDTO());
				} else {
					feedbackPanel.info("Cancelled Terminate Agreement");
					target.add(feedbackPanel);
				}
				
				window.clearModalPageModelInSession();
			}			
		});
		
		// Initialise window settings
		window.setMinimalHeight(500);
		window.setInitialHeight(500);
		window.setMinimalWidth(850);
		window.setInitialWidth(850);
		window.setMaskType(MaskType.SEMI_TRANSPARENT);
		window.setCssClassName(ModalWindow.CSS_CLASS_GRAY);	
		window.setOutputMarkupId(true);
		window.setOutputMarkupPlaceholderTag(true);
		window.setCookieName("MAINTAIN.AGREEMENT.TERMINATE");
		return window;
	}
	
	/**
	 * Create a dialog pop-up window.  Default behaviour is empty, can be overridden.
	 * 
	 * 
	 * @param id
	 * @return
	 */
	public ModalWindow createDialogWindow(String id) {
		DialogWindowPopUp popUp = new DialogWindowPopUp() {
			
			@Override
			public void processAnswer(AjaxRequestTarget target, Boolean answer) {
//				processDialogAnswer(target, answer);
				
				doSave_showWorkflow();
			}

			@Override
			public String getDialogMessage() {
				TemporalPropertyDTO<Boolean> f = pageModel.getMaintainAgreementDTO().getAgreementDTO().getHasMedicalAidCredits();
				if (f != null && f.getValue() != null && f.getValue().booleanValue()) {
					return "The linked to medical aid property has been enabled, " +
							"please ensure that the appropriate medical aid fixed deductions are loaded (if required).";
				} else {
					return "The linked to medical aid property has been disabled, " +
							"please ensure that the appropriate medical aid fixed deductions are ended (if loaded).";
				}
			}
			
			public Dimension getMinimumWindowDimension() {
				return new Dimension(300, 100 );
			}
			
			public Dimension getInitialWindowDimension() {
				return new Dimension(500, 180 );
			}
		};
		ModalWindow w = popUp.createModalWindow(id);
//		w.setPageMapName("maintainAgreementDialog");
		return w;
	}
	
	/**
	 * Create the modal window
	 * 
	 * @param id
	 * @return
	 */
	private SRSModalWindow createWorkflowWindow(String id) {		
		final SRSModalWindow workFlowWindow = new SRSModalWindow(id) {

			String pId = null;
			
			@Override
			public String getModalSessionIdentifier() {
				return "MaintainAgreementPage.workflowdialog-";
			}

			@Override
			public boolean isAllowMultiplePageModels() {
				// True as this window must support multiple pages.
				return true;
			}

			@Override
			public String getParentPageID() {
				// This ensures a unique session variable is being used
				if (pId ==null) {
						pId = MaintainAgreementPage.this.getPageId()+""+getRandomId();
				}
				return pId;
			}

			
		};
		workFlowWindow.setTitle("Workflow Details");				
		
		workFlowWindow.setPageCreator(new ModalWindow.PageCreator() {
			private static final long serialVersionUID = 1L;
			public Page createPage() {				
				WorkflowPage page = new WorkflowPage(
						workFlowWindow,
						MaintainAgreementPage.this.pageModel.getMaintainAgreementDTO().getWorkflowDTO());
				return page;
			}			
		});		
		
		workFlowWindow.setWindowClosedCallback(new ModalWindow.WindowClosedCallback() {
			private static final long serialVersionUID = 1L;	
			WorkflowDTO dto = MaintainAgreementPage.this.pageModel.getMaintainAgreementDTO().getWorkflowDTO();
			public void onClose(AjaxRequestTarget target) {
				// Process answer
				WorkflowDialogPopupModel model = (WorkflowDialogPopupModel) workflowWindow.getSessionModelForPage();
				if (getLogger().isDebugEnabled())
					getLogger().debug("Close workflow page with "  
						+ "  success=" + model.isModalWizardSucces()
						+ "  comment=" + dto.getWorkflowComment());
				
				workflowWindow.clearModalPageModelInSession();
				
				if (model.isModalWizardSucces() && model.getSelectedItem() != null) {
					MaintainAgreementPage.this.pageModel.getMaintainAgreementDTO().setWorkflowDTO( model.getSelectedItem());
					target.appendJavaScript("overlay('Test')");
					raiseRequests(target);
				} else {
					getFeedbackPanel().info("Cancel Workflow");
					target.add(getFeedbackPanel());
				}
			}
		});

		
		// Initialise window settings
		workFlowWindow.setMinimalHeight(250);
		workFlowWindow.setInitialHeight(250);
		workFlowWindow.setMinimalWidth(500);
		workFlowWindow.setInitialWidth(500);
		workFlowWindow.setMaskType(MaskType.SEMI_TRANSPARENT);
		workFlowWindow.setCssClassName(ModalWindow.CSS_CLASS_GRAY);	
		workFlowWindow.setOutputMarkupId(true);
		workFlowWindow.setOutputMarkupPlaceholderTag(true);
		return workFlowWindow;
	}
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see za.co.liberty.web.pages.MaintenanceBasePage#createNavigationalButtons()
	 */
	@Override
	public Button[] createNavigationalButtons() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see za.co.liberty.web.pages.MaintenanceBasePage#createSelectionPanel()
	 */
	@Override
	public Panel createSelectionPanel() {
		return new BaseModificationButtonsPanel<MaintainAgreementDTO>(
				SELECTION_PANEL_NAME, pageModel, this, containerForm,
				MaintainAgreementDTO.class,this.getFeedbackPanel(), true,true,true,true,true,true) {

			private static final long serialVersionUID = 1L;

			@Override
			public void resetSelection() {
				
			}
			
			/**
			 * Will run when cancel is clicked
			 *
			 */
			@SuppressWarnings("unchecked")
			protected void do_cancel(){
				System.out.println("Setting response page to " + parent.getClass());
				parent.invalidatePage();
				// Setting response page 
				System.out.println("Setting response page to " + parent.getClass());
				setResponsePage(new MaintainAgreementPage());	
				System.out.println("Setting response page to " + parent.getClass());
			}

			@Override
			protected boolean isBroadcastButtonEnabled() {
				boolean ret = super.isBroadcastButtonEnabled();
				MaintainAgreementPageModel thisPageModel = 
					MaintainAgreementPage.this.pageModel;
				if (ret &&
						thisPageModel!=null &&
						thisPageModel.getMaintainAgreementDTO()!=null && 
						thisPageModel.getMaintainAgreementDTO().getAgreementDTO()!=null &&
						thisPageModel.getMaintainAgreementDTO().getAgreementDTO()
						.getCurrentStatus()!=null) {
					/**
					 * Broadcast only allowed if agreement in context
					 */
					ret &= thisPageModel.getMaintainAgreementDTO().getAgreementDTO().getId()>0;
					
					/**
					 * Broadcast not allowed when agreement 
					 * in progress or declined
					 */
					String currentStatus = 
						thisPageModel.getMaintainAgreementDTO()
						.getAgreementDTO()
						.getCurrentStatus()
						.getName();
					
					ret &= currentStatus!=null && !currentStatus.equals("InProgress")
						&& !currentStatus.equals("Declined");
					return ret;
				}
				return false;
			}



			@SuppressWarnings("unchecked")
			@Override
			public void doBroadcast_onSubmit(AjaxRequestTarget target, Form form) {
				MaintainAgreementPageModel thisPageModel = 
					MaintainAgreementPage.this.pageModel;
				if (thisPageModel!=null &&
						thisPageModel.getMaintainAgreementDTO()!=null && 
						thisPageModel.getMaintainAgreementDTO().getAgreementDTO()!=null &&
						thisPageModel.getMaintainAgreementDTO().getAgreementDTO().getId()>0) {
					try {
						getGUIController().broadcastAgreement(
								thisPageModel.getMaintainAgreementDTO().getAgreementDTO().getId());
						info("Broadcast successful");
						target.add(getFeedbackPanel());
					} catch (DataNotFoundException e) {
						error("Could not find the agreement to broadcast");
						target.add(getFeedbackPanel());
						return;
					} catch (SecurityException e) {
						error("Security Exception - you must be logged in to broadcast");
						target.add(getFeedbackPanel());
						return;
					}
				} else {
					error("There is no agreement in context to broadcast");
					target.add(getFeedbackPanel());
					return;
				}
			}
			
			
			@Override
			protected boolean isModifyButtonEnabled() {
				boolean ret = super.isModifyButtonEnabled();
				MaintainAgreementPageModel thisPageModel = 
					MaintainAgreementPage.this.pageModel;
				if (ret && 
						thisPageModel!=null &&
						thisPageModel.getMaintainAgreementDTO()!=null && 
						thisPageModel.getMaintainAgreementDTO().getAgreementDTO()!=null &&
						thisPageModel.getMaintainAgreementDTO().getAgreementDTO()
							.getCurrentStatus()!=null) {
					String currentStatus = 
						thisPageModel.getMaintainAgreementDTO()
						.getAgreementDTO()
						.getCurrentStatus()
						.getName();
					
					ret &= currentStatus!=null 
					//&& !currentStatus.equals("InProgress")
						&& !currentStatus.equals("Declined");
				}
				return ret;
			}



			/**
			 * Only allow termination if the agreement is not already terminated
			 * 
			 * @return
			 */
			@Override
			protected boolean isTerminateButtonenabled() {
				boolean ret = super.isTerminateButtonenabled();
				MaintainAgreementPageModel thisPageModel = 
					MaintainAgreementPage.this.pageModel;
				if (ret &&
						thisPageModel!=null &&
						thisPageModel.getMaintainAgreementDTO()!=null && 
						thisPageModel.getMaintainAgreementDTO().getAgreementDTO()!=null &&
						thisPageModel.getMaintainAgreementDTO().getAgreementDTO()
							.getCurrentStatus()!=null) {
					String currentStatus = 
						thisPageModel.getMaintainAgreementDTO()
						.getAgreementDTO()
						.getCurrentStatus()
						.getName();
					ret &= currentStatus!=null && 
						!currentStatus.equals("InProgress") &&
						!currentStatus.equals("Declined") && 
						!currentStatus.equals("Terminated");
				}
				return ret;
			}
			
			

			@SuppressWarnings("unchecked")
			@Override
			public void doAddNew_onSubmit(AjaxRequestTarget target, Form form) {
				if(!parent.hasAddAccess()){
					MaintainAgreementPage.this.error("You do not have the required access to add an agreement.");
					target.add(getFeedbackPanel());
					return;
				}
				/**
				 * Check the security to ensure you can raise all the 
				 * requests for the terminate process first otherwise 
				 * fail with error
				 */
				ISessionUserProfile sessionUser = SRSAuthWebSession.get().getSessionUser();
				Set<RequestKindType> requestsForAdd =
					new CreateAgreementGuiRequestHandler().getOrderedListOfAllowableRequestKinds();
				for (RequestKindType requestKind : requestsForAdd) {
//					if (!sessionUser.isAllowRaise(requestKind)) {
//						MaintainAgreementPage.this.error("You do not have the required access to add an agreement as " +
//								"you are not allowed to raise the request [" + requestKind.getDescription() +"]");
//						target.add(getFeedbackPanel());
//						return;
//					}
				}
				/**
				 * Show the window if there are no errors
				 */
				addAgreementWindow.show(target);
			}
			
			@SuppressWarnings("unchecked")
			@Override
			public void doTerminate_onSubmit(AjaxRequestTarget target, Form form) {
				if(!parent.hasDeleteAccess()){
					MaintainAgreementPage.this.error("You do not have the required access to terminate this agreement.");
					target.add(getFeedbackPanel());
					return;
				}
				/**
				 * Check the security to ensure you can raise all the 
				 * requests for the terminate process first otherwise 
				 * fail with error
				 */
				ISessionUserProfile sessionUser = SRSAuthWebSession.get().getSessionUser();
				Set<RequestKindType> requestsForTerminate =
					new TerminateAgreementGuiRequestHandler().getOrderedListOfAllowableRequestKinds();
				for (RequestKindType requestKind : requestsForTerminate) {
					if (!sessionUser.isAllowRaise(requestKind)) {
						MaintainAgreementPage.this.error("You do not have the required access to terminate this agreement.");
						target.add(getFeedbackPanel());
						return;
					}
				}
				/**
				 * Check all requests for all panels to ensure there are 
				 * no requests outstanding authorisation before proceeding
				 */
				List<AbstractTab> tabList = new ArrayList<AbstractTab>();
				if (getContainerPanel() instanceof MaintenanceTabbedPanel) {
					((MaintenanceTabbedPanel)getContainerPanel()).initialiseTabs(tabList);
				}
				List<String> errors = new ArrayList<String>();
				for (AbstractTab tab : tabList) {
					Class panelClass = tab.getPanel(null).getClass();
					List<RequestKindType> outstandingRequests = getOutStandingRequestTypesForPanel(panelClass);
					for (RequestKindType requestKind : outstandingRequests) {
						errors.add("The following request must be authorised/declined before proceeding: "+requestKind.getDescription());
					}
				}
				if (errors.size()>0) {
					for (String error : errors) {
						MaintainAgreementPage.this.error(error);
					}
					target.add(getFeedbackPanel());
					return;
				}
				/**
				 * Show the window if there are no errors
				 */
				terminateWindow.show(target);
			}
			
			@SuppressWarnings("unchecked")
			@Override
			public void doModify_onSubmit(AjaxRequestTarget target, Form form) {
				/**
				 * Convenience parameter to link to page model in MaintainAgreementPage.this
				 */
				MaintainAgreementPageModel thisPageModel = 
					MaintainAgreementPage.this.pageModel;
				if (thisPageModel!=null &&
					thisPageModel.getMaintainAgreementDTO()!=null &&
					thisPageModel.getMaintainAgreementDTO().getAgreementDTO()!=null) {
				}
				super.doModify_onSubmit(target, form);
			}
		};
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see za.co.liberty.web.pages.MaintenanceBasePage#initialisePageModel(java.lang.Object)
	 */
	@Override
	public Object initialisePageModel(Object obj, Integer currentTab) {
		if (getLogger().isDebugEnabled())
			getLogger().debug("Initialising the pageModel - start");
		long time = System.currentTimeMillis();
		
		if (pageContextDTO!=null) {
			partyContext = pageContextDTO.getPartyContextDTO();
		}
		MaintainAgreementPageModel model = null;
		if (obj != null && obj instanceof MaintainAgreementPageModel) {
			model = (MaintainAgreementPageModel)obj;
		} else {
			model = createPageModel();
		}
		pageModel = model;
		if(currentTab != null){
			pageModel.setCurrentTab(currentTab);
		}
		
		if (getLogger().isDebugEnabled())
			getLogger().debug("Initialising the pageModel - done  - took " 
					+ (System.currentTimeMillis() - time) + " millis");
		return pageModel;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see za.co.liberty.web.pages.BasePage#getPageName()
	 */
	@Override
	public String getPageName() {
		return pageName;
	}


	/**
	 * Gets the IAgreementPrivilegesController interface for calls to the
	 * AgreementPrivilegesController session bean
	 * 
	 * @return
	 */
	@SuppressWarnings("unused")
	private IAgreementPrivilegesController getAgreementPrivilegesController() {
		IAgreementPrivilegesController agreementPrivilegesController;
		try {
			agreementPrivilegesController = ServiceLocator.lookupService(IAgreementPrivilegesController.class);
		} catch (NamingException e) {
			throw new CommunicationException(e);
		}
		return agreementPrivilegesController;
	}

	@Override
	public ContextType getContextTypeRequired() {
		return ContextType.AGREEMENT;
	}
	
	
	
	@Override
	public void doSave_onSubmit() {
		if (pageModel.getMaintainAgreementDTO()!=null) {
			AgreementDTO currentDTO = pageModel.getMaintainAgreementDTO().getAgreementDTO();
			AgreementDTO previousDTO = (pageModel.getPreviousMaintainAgreementDTO() != null) 
					? pageModel.getPreviousMaintainAgreementDTO().getAgreementDTO() : null;
					//setPfContributionRate(currentDTO,previousDTO);	//santosh :workaround to maintain contribution rate
			if (getGUIController().isMedicalLinkChanged(currentDTO, previousDTO)) {
				dialogWindow.show(RequestCycle.get().find(AjaxRequestTarget.class));
				return;
			}
		}
		doSave_showWorkflow();
	}
	
	public void doSave_showWorkflow() {
		workflowWindow.show(RequestCycle.get().find(AjaxRequestTarget.class));
	}

	private void raiseRequests(AjaxRequestTarget target) {

		if (getLogger().isDebugEnabled())
			getLogger().debug("Raise requests");
		//AgreementDTO dto = this.pageModel.getMaintainAgreementDTO().getAgreementDTO();		
		genericRaiseRequest(target);
		invalidatePage();		
		getSession().info("Record was saved succesfully");			
		setResponsePage(new MaintainAgreementPage(null,pageModel.getCurrentTab()));
	}

	public void genericRaiseRequest(AjaxRequestTarget target) {
		ISessionUserProfile userProfile = SRSAuthWebSession.get().getSessionUser();
		Long partyOid = pageContextDTO!=null && pageContextDTO.getPartyContextDTO()!=null
							?pageContextDTO.getPartyContextDTO().getPartyOid():null;
		try{					
			getGUIController().raiseMaintainAgreementRequest(
						userProfile, partyOid , 
						pageModel.getMaintainAgreementDTO(), pageModel.getPreviousMaintainAgreementDTO(),PanelToRequestMapping.getMappingForPageAndPanel(MaintainAgreementPage.class,pageModel.getCurrentTabClass()));
			// SSM2707 Hierarchy FR3.4 FAIS Details SWETA MENON Begin
			List<String> messageList = getGUIController().getInfoMessageList();
			if (messageList != null && messageList.size() > 0) {
				for (String message : messageList)
				getSession().info(message);	
				// Clear the message list
				getGUIController().clearInfoMessageList();
			}
			// SSM2707 Hierarchy FR3.4 FAIS Details SWETA MENON End
		}catch(ValidationException e){
			for(String error : e.getErrorMessages()){
				error(error);
			}
			target.add(getFeedbackPanel());
			return;
		} catch (DataNotFoundException e) {
			error("Data not found when trying to raise a request: "+e.getMessage());
			target.add(getFeedbackPanel());
			return;
		} catch (RequestException e) {
			error("Could not raise the request: "+e.getMessage());
			target.add(getFeedbackPanel());
			return;
		}
	}


	private MaintainAgreementPageModel createPageModel()
	{
		AgreementDTO agreementDTO = null;
		PaymentSchedulerDTO paymentSchedulerDTO = new PaymentSchedulerDTO();
		ContextAgreementDTO agreement = null;
		if (pageContextDTO!=null) {
			agreement = pageContextDTO.getAgreementContextDTO();
		}
		if (agreement==null || agreement.getAgreementNumber()==null ||
				agreement.getAgreementNumber()==0
				|| pageContextDTO.getPartyContextDTO().getTypeOid() == SRSType.ADVANCEDPRACTICE) {
			agreementDTO = new AgreementDTO();
			agreementDTO.setPaymentSchedulerDTO(paymentSchedulerDTO);
			error("There is no agreement selected in the current context");
		}
		else { 
			try {
				agreementDTO = getGUIController().getAgreementDTOForObjectOID(agreement.getAgreementNumber());
				if (partyContext!=null) {
					agreementDTO.setPartyOid(partyContext.getPartyOid());
				}
				
				// SSM2707 Begin
				// Add the primary agreement details to the agreementDTO.
				
				// SSM2707 End
			} catch (CommunicationException e) {
				SystemException sys = new SystemException("CommunicationException trying to load agreement ["+
						agreement.getAgreementNumber()+"]",0,0);
				sys.initCause(e);
				throw sys;
			} catch (DataNotFoundException e) {
				SystemException sys = new SystemException("DataNotFoundException trying to load agreement ["+
						agreement.getAgreementNumber()+"]",0,0);
				sys.initCause(e);
				throw sys;
			}
		}
		/**
		 * Use the MaintainAgreementPageModelFactory to 
		 * instantiate the page model 
		 */
		MaintainAgreementPageModel ret =
			MaintainAgreementPageModelFactory.createPageModelForMaintenance(
					getGUIController(), 
					agreementDTO);
		return ret;
	}
	
	@Override
	public void swapSelectionPanel(AjaxRequestTarget target) {
		/**
		 * Process any lazy load requirements
		 */
		try {
			processDeferredLoading();
		} catch (DataNotFoundException e) {
			error("Data not found when trying to load agreement data: "+e.getMessage());
			//EXIT the method, don't swap the selection panel
			return;
		}
		/**
		 * Call super method to swap panel
		 */
		super.swapSelectionPanel(target);
		
		
	}

	private void processDeferredLoading() throws DataNotFoundException {
		if (getLogger().isDebugEnabled())
			getLogger().debug("Process Deferred Loading - Start");
		RequestKindType[] requestKindTypes = 
			PanelToRequestMapping.getMappingForPageAndPanel(MaintainAgreementPage.class,pageModel.getCurrentTabClass());
		if (getLogger().isDebugEnabled())
			getLogger().debug("Process Deferred Loading - for kinds " + requestKindTypes);
		if (requestKindTypes!=null) {
			getGUIController().loadDeferredDataForRequest(
					pageModel.getMaintainAgreementDTO().getAgreementDTO(),
					pageModel.getPreviousMaintainAgreementDTO().getAgreementDTO(),
					pageModel.getValidAgreementValues(),
					requestKindTypes);
			
		}
		if (getLogger().isDebugEnabled())
			getLogger().debug("Process Deferred Loading - End");
	}

	private IAgreementGUIController getGUIController() {
		if (guiController==null) {
			/**
			 * Load agreement controller
			 */
			try {
				guiController = ServiceLocator.lookupService(IAgreementGUIController.class);
			} catch (NamingException e) {
				logger.log(Level.SEVERE,"Naming exception looking up Agreement GUI Controller",e);
				throw new CommunicationException("Naming exception looking up Agreement GUI Controller",e);
			}
		}
		return guiController;
	}
	
	/**
	 * This need to be removed once we get proper solution,As part of Contribution rate,
	 * when we modified any of the value on pf maintainscreen override value is changing to null
	 * to maintain rate adding this logic
	 * @param currentDTO
	 * @param previousDTO
	 */
	private void setPfContributionRate(AgreementDTO currentDTO,AgreementDTO previousDTO) {
		if(previousDTO!=null) {
			BigDecimal currentRate =currentDTO.getProvidentFundDetail().getOverrideRate();
			BigDecimal prvRate =previousDTO.getProvidentFundDetail().getOverrideRate();
			
			logger.info(
					"currentDTO Values:--OverrideReason :" + currentDTO.getProvidentFundDetail().getPfOverrideReason()
							+ "--OverrideRate:" + currentDTO.getProvidentFundDetail().getOverrideRate() + "--StartDate:"
							+ currentDTO.getProvidentFundDetail().getOverrideStartDate() + "--EndDate:"
							+ currentDTO.getProvidentFundDetail().getOverrideEndDate());
			logger.info(
					"previousDTO Values:--OverrideReason :" + previousDTO.getProvidentFundDetail().getPfOverrideReason()
							+ "--OverrideRate:" + previousDTO.getProvidentFundDetail().getOverrideRate() + "--StartDate:"
							+ previousDTO.getProvidentFundDetail().getOverrideStartDate() + "--EndDate:"
							+ previousDTO.getProvidentFundDetail().getOverrideEndDate());
		
			
			if(prvRate !=null && currentRate==null) {
				if(currentDTO.getProvidentFundDetail().getPfOverrideReason().equalsIgnoreCase(previousDTO.getProvidentFundDetail().getPfOverrideReason()) &&
						currentDTO.getProvidentFundDetail().getOverrideStartDate().equals(previousDTO.getProvidentFundDetail().getOverrideStartDate())) {
					currentDTO.getProvidentFundDetail().setOverrideRate(prvRate);
				}
			logger.info("After setting the value, OverrideRate--:"+currentDTO.getProvidentFundDetail().getOverrideRate());	
			}
		}
	}
}
