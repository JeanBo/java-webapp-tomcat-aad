package za.co.liberty.web.pages.wizard;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.PageReference;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxCallListener;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.form.AjaxFormSubmitBehavior;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.extensions.wizard.CancelButton;
import org.apache.wicket.extensions.wizard.FinishButton;
import org.apache.wicket.extensions.wizard.IWizard;
import org.apache.wicket.extensions.wizard.LastButton;
import org.apache.wicket.extensions.wizard.NextButton;
import org.apache.wicket.extensions.wizard.Wizard;
import org.apache.wicket.extensions.wizard.WizardButtonBar;
import org.apache.wicket.extensions.wizard.WizardModel;
import org.apache.wicket.extensions.wizard.WizardStep;
import org.apache.wicket.feedback.FeedbackCollector;
import org.apache.wicket.feedback.FeedbackMessage;
import org.apache.wicket.feedback.FeedbackMessages;
import org.apache.wicket.feedback.FeedbackMessagesModel;
import org.apache.wicket.feedback.IFeedbackMessageFilter;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.string.StringList;

import za.co.liberty.dto.party.HierarchyNodeDTO;
import za.co.liberty.helpers.persistence.TemporalityHelper;
import za.co.liberty.web.data.pages.IModalMaintenancePageModel;
import za.co.liberty.web.pages.dialog.DialogWindowPopUp;
import za.co.liberty.web.pages.wizard.object.SRSWizardPageDetail;
import za.co.liberty.web.system.SRSAuthWebSession;
import za.co.liberty.web.wicket.ajax.attributes.SRSAjaxCallListener;
import za.co.liberty.web.wicket.modal.SRSModalWindow;


/**
 * Wizard to be used for SRS Popup pages
 * @author DZS2610
 *
 */
public abstract class SRSPopupWizard<T extends IModalMaintenancePageModel> extends Wizard {	
	
	private static final long serialVersionUID = 1L;	
	private String dialogMessage;
	public static final String SRS_WIZARD_STEP_ID = "wizardStep";
	
	transient Logger transLogger;
	
	private SRSModalWindow parentWindow;
	private WizardModel wizardModel;
	private FeedbackPanel feedback;		
	private boolean finishedSucessfully = false;
	private Collection<SRSWizardPageDetail> steps;
	private T pageModel;
	private PageReference pageReference;
	private ModalWindow dialogWindow;
	private FeedbackMessages feedbackMessages = new FeedbackMessages();
	
	private void init(){		   
	    //Close window call back

	   
	}
	
	
	/**
	 * @deprecated  Remove this from next release as pageReference is now a requirement.
	 * 
	 * @param id
	 * @param parentWindow
	 */
	//TODO this will be removed, only left for compilation issues.
	public SRSPopupWizard(String id,SRSModalWindow parentWindow) {
		this(id, parentWindow,null, null );
	}
	
	//TODO this will be removed, only left for compilation issues.
	public SRSPopupWizard(String id, SRSModalWindow parentWindow, T pageModel) {
		this(id,parentWindow,pageModel, null );
	}
	
	public SRSPopupWizard(String id, SRSModalWindow parentWindow, PageReference pageReference) {
		this(id,parentWindow,null, pageReference );
	}
	
	
	public SRSPopupWizard(String id, SRSModalWindow parentWindow, T pageModel, final PageReference pageReference) {
		super(id);	
		this.setOutputMarkupId(true);
		this.pageReference = pageReference;
		HierarchyNodeDTO dto = new HierarchyNodeDTO();			
		dto.setEffectiveFrom(TemporalityHelper.getInstance().getNewNOWDateWithNoTime());
		this.parentWindow = parentWindow;				
//		setModel(new CompoundPropertyModel(pageModel));		
		wizardModel = createWizardModel();
		this.pageModel = initializePageModel(pageModel);
		
//		if (parentWindow instanceof SRSModalWindow) {
			getLogger().info("SRSModalWindow - store value in session");
			SRSModalWindow w = (SRSModalWindow) parentWindow;
			w.setSessionModelForPage(this.pageModel);
//		}
		
		getLogger().info("SRSPopupWizard.afterInitPagemodel " + pageModel);
		
		steps = getWizardSteps(this.pageModel);
		for(SRSWizardPageDetail step : steps){
			if(step != null){
				if(step.getStep() != null){
					wizardModel.add(step.getStep());
				}else{					
					//create a new generic step
					if(step.getStepPanel().getId().equalsIgnoreCase(SRSPopupWizard.SRS_WIZARD_STEP_ID)){
						//create step
						wizardModel.add(new Step(step.getStepPanel(),step.getTitle()));
					}else{
						//TODO throw error
						getLogger().error("Invalid step, not an SRS Wizard Step " + step.getStepPanel().getId());
					}					
				}				
			}
		}		
		// initialize the wizard with the wizard model we just built
	    init(wizardModel);	   
	    
//	    SRSAuthWebSession.get().clearWizardMessages();
	    
	    //Close window call back
//	    parentWindow.setWindowClosedCallback(new ModalWindow.WindowClosedCallback() {
//			private static final long serialVersionUID = 1L;			
//			public void onClose(AjaxRequestTarget target) {
//				processClose(target,finishedSucessfully);
//			}			
//		});
	    
//	    parentWindow.setWindowClosedCallback(new ModalWindow.WindowClosedCallback() {
//			private static final long serialVersionUID = 1L;			
//			public void onClose(AjaxRequestTarget target) {
//				if (getLogger().isDebugEnabled())
//					getLogger().debug("SRSPopupWizard.setWindowClosedCallback.onClose   ");
//				
//				Page parentPage = pageReference.getPage();
//				System.out.println("Parent page fromTarget=" + parentPage
//						+"\nFromPageReference=" + pageReference.getPage());
//				
//				IWizardPageResponse responsePage = (IWizardPageResponse) parentPage; 
//
//				
//				if (getLogger().isDebugEnabled())
//					getLogger().debug("SRSPopupWizard.setWindowClosedCallback.onClose   isSuccess=" 
//							+ responsePage.isSuccess());
//				processClose(target, finishedSucessfully);
//				processClose(target,responsePage.isSuccess());
//			}			
//		});
	    this.getForm().add(dialogWindow=createDialogWindow("dialogWindow"));
	}

	
	public void processCloseNew() {
		
	}
	
	/**
	 * Create a Wizard Model internally.
	 * 
	 * @return
	 */
	protected WizardModel createWizardModel() {
		return new WizardModel() {

			@Override
			public void cancel() {
				super.cancel();
				if (getLogger().isDebugEnabled())
					getLogger().debug("SRSPopupWizard.wizardmodel.cancel");
			}
			
			@Override
			public void finish() {
				super.finish();
				if (getLogger().isDebugEnabled())
					getLogger().debug("SRSPopupWizard.wizardmodel.finish ");
			}
			
		};
	}
	
	/**
	 * Get all the steps to be used for this wizard
	 * @param pageModel
	 * @return
	 */
	protected abstract Collection<SRSWizardPageDetail> getWizardSteps(T pageModel);
	
	/**
	 * Set all page model variables before all panels are constructed
	 * @param pageModel
	 */
	protected abstract T initializePageModel(T pagemodel);
	
	/**
	 * A generic wizard step used to fit one panel</br>
	 * 
	 * @author DZS2610
	 *
	 */
	private final class Step extends WizardStep
	{			
		private static final long serialVersionUID = 1L;

		public Step(Panel panel, String name)
		{
			setTitleModel(new Model(name));					
			add(panel);					
		}		
	}
	
	@Override
	protected FeedbackPanel newFeedbackPanel(String id) {		
		//feedback = super.newFeedbackPanel(id);				
		FeedbackPanel feedback = new FeedbackPanel(id) {

			@Override
			protected FeedbackMessagesModel newFeedbackMessagesModel() {
				if (getWizardParentPage()!=null) {
					getLogger().info("Using wizard page as parent " + getWizardParentPage());
					return new FeedbackMessagesModel(this) {
						protected List<FeedbackMessage> collectMessages(Component pageResolvingComponent,
								IFeedbackMessageFilter filter){
							
								getLogger().info("Collect feedback messages");
								FeedbackCollector f = new FeedbackCollector(pageResolvingComponent.getPage());
//								SRSFeedbackCollector f = new SRSFeedbackCollector(pageResolvingComponent.getPage()) {
//									
//									
//								};
//								f.setRecursive(false);
								
								return f.collect(filter);
							}
					};
				} 
				return super.newFeedbackMessagesModel();
			}
			
			
			
		};
//		feedback.setFilter(new IFeedbackMessageFilter() {
//			
//			@Override
//			public boolean accept(FeedbackMessage arg0) {
//				feedback.proc
//			}
//		} );
		
		
		
		feedback.setOutputMarkupId(true);
		this.feedback = feedback;
		return feedback;
	}


	@Override
	public void onCancel() {		
		if (getLogger().isDebugEnabled())
			getLogger().debug("SRSPopupWizard.onCancel");
				
	}
	
	public abstract boolean onCancel(AjaxRequestTarget target);
	
	@Override
	public void onFinish() {
		//leave out as we need the ajax request to close the window		
		if (getLogger().isDebugEnabled())
			getLogger().debug("SRSPopupWizard.onFinish " + pageModel);
	}	
	
	/**
	 * Fill in method to execut on finish
	 * @param target
	 * @return true if the window must close, false to keep the window open
	 */
	public abstract boolean onFinish(AjaxRequestTarget target);

	/**
	 * Called on finish button clicked
	 * @param target
	 */
	public void onFinishButtonClicked(AjaxRequestTarget target){	
		if (finishedSucessfully || pageModel.isModalWizardSucces()) {
			getLogger().error("Form was already submitted, cancelling submission finishedSuc=" + finishedSucessfully
					+ "  isModelSuccess=" + pageModel.isModalWizardSucces());
			getWizardParentPage().error("Form was already submitted, cancelling submission");
			return;
		}
		if (getLogger().isDebugEnabled())
			getLogger().debug("SRSPopupWizard.onFinishButtonClicked - call.onFinish(target)");
		finishedSucessfully = onFinish(target);
		pageModel.setModalWizardSuccess(finishedSucessfully);
		parentWindow.setSessionModelForPage(this.pageModel);
		if (getLogger().isDebugEnabled())
			getLogger().debug("SRSPopupWizard.onFinishButtonClicked - window.close(target)  ifSuccess=" + finishedSucessfully);
		if(finishedSucessfully){
			parentWindow.close(target);
		}			
	}	
	
	public void onNextButtonClicked(AjaxRequestTarget target) {
		if (getLogger().isDebugEnabled())
			getLogger().debug("SRSPopupWizard.onNextButtonClicked");
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected Component newButtonBar(String id) {		 
		WizardButtonBar bar = new WizardButtonBar(id, this) {
			
			CancelButton cancelButton;
			
			/**
			 * Creates a new {@link LastButton}
			 * 
			 * @param id the button's markup-id
			 * @param wizard the {@link IWizard}
			 * @return a new {@code LastButton}
			 */
			protected LastButton newLastButton(final String id, final IWizard wizard){
				return new LastButton(id, wizard) {

					@Override
					public void onClick() {
						super.onClick();
						if (getLogger().isDebugEnabled())
							getLogger().debug("SRSPopupWizard.LASTbutton.click");
					}
					
				};
			}

			/**
			 * Creates a new {@link CancelButton}
			 * 
			 * Calls wizard.getWizardModel().cancel() ->
			 * 
			 * @param id the button's markup-id
			 * @param wizard the {@link IWizard}
			 * @return a new {@code CancelButton}
			 */
			protected CancelButton newCancelButton(final String id, final IWizard wizard){
				CancelButton c =  new CancelButton(id, wizard);
				c.add(new AjaxFormComponentUpdatingBehavior("click"){
				   private static final long serialVersionUID = 1L;
					@Override
					protected void onUpdate(AjaxRequestTarget target) {		
						if (getLogger().isDebugEnabled())
							getLogger().debug("SRSPopupWizard.CANCELbutton.behaviour.click");
						pageModel.setModalWizardSuccess(false);
						SRSPopupWizard.this.parentWindow.close(target);
					}			
				});	
				cancelButton = c;
				return c;
			}


			/**
			 * Creates a new {@link FinishButton}
			 * 
			 * @param id the button's markup-id
			 * @param wizard the {@link IWizard}
			 * @return a new {@code FinishButton}
			 */
			protected FinishButton newFinishButton(final String id, final IWizard wizard){
				FinishButton b = new FinishButton(id, wizard) {

					@Override
					public void onClick() {
						super.onClick();
						if (getLogger().isDebugEnabled())
							getLogger().debug("SRSPopupWizard.FINISHbutton.behaviour.click");
						
					}
					
				};
				b.setDefaultFormProcessing(true);
				
				// Needed in case we want to close the window
				b.add(new AjaxFormSubmitBehavior("click"){
					   private static final long serialVersionUID = 1L;

					@Override
					protected void onAfterSubmit(AjaxRequestTarget target) {
						if (getLogger().isDebugEnabled())
							getLogger().debug("AFTER SUBMIT 1");
						super.onAfterSubmit(target);
						if (getLogger().isDebugEnabled())
							getLogger().debug("AFTER SUBMIT 2");
						onFinishButtonClicked(target);
						
						if (finishedSucessfully = false) {
							getLogger().warn("Finish was clicked but had errors, re-enabling Finish button");
							b.setEnabled(true);
							target.add(b);
						}
					}

					@Override
					protected void onError(AjaxRequestTarget target) {
						// Enable the finish button if there were any errors processing the forms.
						getLogger().info("Finish.onerror");
						b.setEnabled(true);
						target.add(b);
						target.add(feedback);
						super.onError(target);
					}

					@Override
					protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
						super.updateAjaxAttributes(attributes);

					        // Way of adding any handler
						attributes.getAjaxCallListeners().add(new AjaxCallListener() {

						  @Override
						  public CharSequence getInitHandler(Component component) {
							CharSequence s =   super.getInitHandler(component);
							
							// disable cancel too
							String disableCancel = "";
							if (cancelButton != null) {
								disableCancel = "document.getElementById('"
										+ b.getMarkupId()
										+ "').disabled=true;";
							}
							return "overlay('test');" + disableCancel + ((s==null)?"":s);
						  }

						@Override
						public CharSequence getFailureHandler(Component component) {
							return super.getFailureHandler(component);
						}
						  
						  
//										
//							  @Override
//							  public CharSequence getDoneHandler(Component component) {	
//								CharSequence s =  super.getDoneHandler(component);
//								return  "hideOverlay();" + ((s==null)?"":s);
//							  }
						});
					}
				});	
				return b;
			}
//			//replace their finish button with mine
//			final Button finishButton = (Button) bar.get("finish");			
//			finishButton.add(new AjaxFormSubmitBehavior(this.getForm(),"click"){
//				   private static final long serialVersionUID = 1L;
//				   
//					@Override
//					protected void onError(AjaxRequestTarget target) {
//						if(feedback != null){
//							target.add(feedback);
//						}
//						target.add(finishButton);
//						if(cancelButton != null){
//							target.add(cancelButton);
//						}
//					}
//					@Override
//					protected void onSubmit(AjaxRequestTarget target) {						
//						onFinishButtonClicked(target);
//						target.add(finishButton);
//						if(cancelButton != null){
//							target.add(cancelButton);
//						}
//					}	
//					
//					@Override
//					protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
//						super.updateAjaxAttributes(attributes);
//						attributes.getAjaxCallListeners().add(new AjaxCallListener() {
	//
//							// #WICKETTEST - Test that this works
//							@Override
//							public CharSequence getInitHandler(Component component) {
//								String disableCancel = "";
//								if(cancelButton != null){
//									disableCancel = "getElementById('"+cancelButton.getMarkupId()+"').disabled=true;";
//								}
//								return "this.disabled=true;"+disableCancel+"overlay(true);" + super.getInitHandler(component);
//							}
//							
//							
//						});
//					}
//					
////					@Override
////					protected IAjaxCallDecorator getAjaxCallDecorator() {
////						return new AjaxCallDecorator() {
////							private static final long serialVersionUID = 1L;						
////							public CharSequence decorateScript(CharSequence script) {
////								//disable cancel too		
////								String disableCancel = "";
////								if(cancelButton != null){
////									disableCancel = "getElementById('"+cancelButton.getMarkupId()+"').disabled=true;";
////								}
////								return "this.disabled=true;"+disableCancel+"overlay(true);" + script;
////							}
////						};
////					}
//			});		
//			finishButton.add(new AttributeModifier("type","button"));
//			bar.add(finishButton);
			

			@Override
			protected NextButton newNextButton(String id, IWizard wizard) {
				NextButton nextButton = new NextButton(id, wizard) {

					@Override
					public void onClick() {
						if (getLogger().isDebugEnabled())
							getLogger().debug("SRSPopupWizard.NEXTbutton.behaviour.click.beforesuper - " 
									); //+ StringList.valueOf(SRSAuthWebSession.get().getWizardMessages()));
						super.onClick();
						if (getLogger().isDebugEnabled())
							getLogger().debug("SRSPopupWizard.NEXTbutton.behaviour.click.aftersuper - "
									); //+ StringList.valueOf(SRSAuthWebSession.get().getWizardMessages()));
						
//						processMessages(SRSAuthWebSession.get().getWizardMessages());
//						target.add(getFeedback());
					}
					
				};
				
				// Needed in case we want to close the window
				nextButton.add(new AjaxFormComponentUpdatingBehavior("click"){
					   private static final long serialVersionUID = 1L;
					   
					   
					   
						@Override
					protected void onError(AjaxRequestTarget target, RuntimeException e) {
							if (getLogger().isDebugEnabled())
								getLogger().info("#JB6.1 next.onError bef-");
							target.add(getFeedback());
//							SRSAuthWebSession.get().clearWizardMessages();
							super.onError(target, e);
							if (getLogger().isDebugEnabled())
								getLogger().info("#JB6.2 next.onError  aft-"); // + StringList.valueOf(SRSAuthWebSession.get().getWizardMessages()));
					}

						@Override
						protected void onUpdate(AjaxRequestTarget target) {	
							if (getLogger().isDebugEnabled())
								getLogger().info("#JB6.2 next.onUpdate -"); // + StringList.valueOf(SRSAuthWebSession.get().getWizardMessages()));
							
							target.add(getFeedback());
							onNextButtonClicked(target);
						}	
						

						@Override
						protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
							super.updateAjaxAttributes(attributes);
						        // SRS Convenience method for overLay hiding/showing
						        attributes.getAjaxCallListeners().add(SRSAjaxCallListener.newShowOverlayNoHideWithDisableSelf());
						}
					});	
				
				
				
//				Leaving the code commented as its my backup for dealing with validation issues.
//				// Needed in case we want to close the window
//				AjaxFormSubmitBehavior behaviour = new AjaxFormSubmitBehavior("click") {
//					
//					
//
//					@Override
//					protected Form<?> findForm() {
//						// TODO Auto-generated method stub
//						Form f = super.findForm();
//						Form f2 = getCurrentStepForm();
//						getLogger().info("Findform   origF = " + f
//								+ "  altForm=" + f2);
//						if (f2!=null) {
//							return f2;
//						}
//						return f;
//					}
//
//					@Override
//					protected void onEvent(AjaxRequestTarget target) {
//						getLogger().info("#JB5.1 next.ON EVENT -" + StringList.valueOf(SRSAuthWebSession.get().getWizardMessages()));
//					
////						if ()
////						SRSAuthWebSession.get().clearWizardMessages();
//						
////						FeedbackMessages msg = getFeedbackMessages();
////						getLogger().info("Msgs from feedbackMsgs = " + msg.size());
//						
////						FeedbackMessagesModel mod = getFeedback().getFeedbackMessagesModel();
////						
////						List<FeedbackMessage> msgList = mod.getObject();
////						getLogger().info("Msgs from feedback.model = " + msgList);
//
//					
////						super.onEvent(target);
////						target.add(getFeedback());
//					}
//
//					@Override
//					protected void onError(AjaxRequestTarget target) {
//						super.onError(target);
//						
//						if (getLogger().isDebugEnabled())
//							getLogger().info("#JB5.1.error - FeedbackMesssages.onerror.click=" 
//									+ StringList.valueOf(SRSAuthWebSession.get().getWizardMessages()));
//						
//						processMessages(SRSAuthWebSession.get().getWizardMessages());
//						target.add(getFeedback());
//						
//						SRSAuthWebSession.get().clearWizardMessages();
//						
//					}
//
//					@Override
//					protected void onSubmit(AjaxRequestTarget target) {
//						getLogger().info("#JB5.2.next.ON SUBMIT" + StringList.valueOf(SRSAuthWebSession.get().getWizardMessages()));
////						target.add(getFeedback());
//						super.onSubmit(target);
//					}
//
//					@Override
//					protected void onAfterSubmit(AjaxRequestTarget target) {
//						target.add(getFeedback());
//						getLogger().info("#JB5.3.next.AFTER SUBMIT 1 - " + StringList.valueOf(SRSAuthWebSession.get().getWizardMessages()));
//						super.onAfterSubmit(target);
//						getLogger().info("#JB5.3.next.AFTER SUBMIT 2 " + StringList.valueOf(SRSAuthWebSession.get().getWizardMessages()) );
//						
//						
//						
//						onNextButtonClicked(target);
//						SRSAuthWebSession.get().clearWizardMessages();
//					}
//
//					private static final long serialVersionUID = 1L;
//
//
//					@Override
//					protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
//						super.updateAjaxAttributes(attributes);
//						// SRS Convenience method for overLay hiding/showing
//						attributes.getAjaxCallListeners().add(SRSAjaxCallListener.newShowOverlayNoHideOnDone());
//					}
//				};
////				behaviour.setDefaultProcessing(false);
//				nextButton.add(behaviour);
//
				
				
				
				
				
				return nextButton;
			}
			

		};

		return bar;
	}		

	public FeedbackPanel getFeedback() {
		return feedback;
	}	
	
	
	/**
	 * Get a panels from the steps matching the class sent through	
	 * @param panelClass
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <Q extends Panel> Q getStepPanelOfType(Class<Q> panelClass,String componentID){		
		for(SRSWizardPageDetail step : steps){
			if(step.getStepPanel() != null && step.getStepPanel().getClass() == panelClass && step.getStepPanel().getId().equals(componentID)){
				return (Q)step.getStepPanel();
			}else if(step.getStep() != null && step.getStep().get(componentID) != null && step.getStep().get(componentID).getClass() == panelClass){
				return (Q)step.getStep().get(componentID);
			}
		}
		return null;
	}	
	
	
	
	/**
	 * Returns the page model used for the wizard
	 * @return
	 */
	public T  getPageModel(){
		return pageModel;
	}
	
	
	/**
	 * Create a dialog pop-up window.  Default behaviour is empty, can be overridden.
	 * 
	 * <br/>
	 * Call {@linkplain #setDialogMessage(String)} and then override {@linkplain #processDialogAnswer(AjaxRequestTarget, Boolean)}
	 * 
	 * @param id
	 * @return
	 */
	public ModalWindow createDialogWindow(String id) {
		DialogWindowPopUp popUp = new DialogWindowPopUp() {
			
			@Override
			public void processAnswer(AjaxRequestTarget target, Boolean answer) {
				processDialogAnswer(target, answer);
			}

			@Override
			public String getDialogMessage() {
				return dialogMessage;
			}
			
		};
		ModalWindow w = popUp.createModalWindow(id);
		w.setCookieName("clearMQPageMap");
		// #WICKETTEST - Changed pagename to cookie, test if this works.
//		w.setPageMapName("clearMQPageMap");
		return w;
	}
	
	public void setDialogMessage(String message) {
		dialogMessage = message;
	}
	
	/**
	 * Override this to process the required answer, ensure to always call super
	 */
	public void processDialogAnswer(AjaxRequestTarget target, Boolean answer){
		
	}
	/**
	 * Show the dialog, ensure message is set up before this.
	 */
	public void showDialog(AjaxRequestTarget target) {
		dialogWindow.show(target);
	}

	
	protected Logger getLogger() {
		if (transLogger==null) {
			transLogger = Logger.getLogger(this.getClass());
		}
		return transLogger;
	}
	
	public boolean isFinishedSuccesfully() {
		return finishedSucessfully;
	}
	
	protected PageReference getPageReference() {
		return pageReference;
	}
	
	
	protected Form getCurrentStepForm() {
		return null;
	}
	
	/**
	 * The feedback messages should only be used when {@link #isApplyOwnFeedbackMessages()} returns true
	 * and requires feedback messages to then be passed to panels.
	 * 
	 */
	public FeedbackMessages getFeedbackMessages() {
		return feedbackMessages;
	}
	
	public boolean isApplyOwnFeedbackMessages() {
		return false;
	}
	
	/**
	 * Process feedback messages, remove duplicates.
	 * 
	 * @param wizardMessages
	 */
	protected void processMessages(List<FeedbackMessage> wizardMessages) {
		
		if (true) return;
		
		Set<String> messageSet = new HashSet<String>();
		
		List<FeedbackMessage> newList = new ArrayList<FeedbackMessage>();
		
		for (FeedbackMessage m : wizardMessages) {
			if (messageSet.contains(m.getMessage().toString())) {
				continue;
			}
			messageSet.add(m.getMessage().toString());
			newList.add(m);
		}
		
		for (FeedbackMessage m : newList) {
			feedback.error(m.getMessage());
		}
		
		
	}
	
	public Page getWizardParentPage() {
		if (pageReference != null) {
			return pageReference.getPage();
		}
		return null;
	}
}
