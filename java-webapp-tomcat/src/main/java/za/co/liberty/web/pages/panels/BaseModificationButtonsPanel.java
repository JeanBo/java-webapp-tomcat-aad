package za.co.liberty.web.pages.panels;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxCallListener;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.ajax.markup.html.form.AjaxFallbackButton;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;

import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.data.pages.IMaintenancePageModel;
import za.co.liberty.web.pages.BasePage;
import za.co.liberty.web.pages.interfaces.IChangeableStatefullComponent;
import za.co.liberty.web.pages.interfaces.IMaintenanceParent;
import za.co.liberty.web.wicket.ajax.attributes.SRSAjaxCallListener;

/**
 *  <p>base buttons panel for the top of any page</p>
 * 
 * @author dzs2610
 *
 * @param <DTO>
 */
public abstract class BaseModificationButtonsPanel <DTO extends Object> 
		extends Panel implements IChangeableStatefullComponent {

	/* Constants */
	private static final long serialVersionUID = 6729405618038745608L;
	public static final int SELECTION_WIDTH = 300;
	private static final Logger logger = Logger.getLogger(BaseModificationButtonsPanel.class);
	
	/* Form components */	
	protected Panel buttonPanel;
	protected Button modifyButton;	
	protected Button addNewButton;
	protected Button saveButton;
	protected Button cancelButton;
	protected Form enclosingForm;
	protected Button resetButton;
	protected Button terminateButton;
	protected Button reactivateButton;
	protected Button broadcastButton;
		
	/* Attributes */	
	protected boolean includeReset;
	protected boolean includeTerminate;
	protected boolean includeBroadcast;
	protected boolean includeAddNew;
	protected boolean includeSave;
	protected boolean includeCancel;
	protected boolean includeReactivate;
	
	/* Attributes */
	private EditStateType editState;
	protected IMaintenancePageModel pageModel;
	protected IMaintenanceParent parent;
	protected Class dtoType;
	
	/* Additional attributes */
	protected String listDescriptionLabel;
	
	protected FeedbackPanel feedBackPanel;	
	
	//added for MI Hierarchy
	boolean includeLBFNDPCreateBtn;	
	protected Button lbfNDPCreateBtn;

	public BaseModificationButtonsPanel(String id, IMaintenancePageModel pageModel, 
			IMaintenanceParent parent, Form enclosingForm, Class dtoType,FeedbackPanel feedBackPanel, boolean includeAddNew,boolean includeSave,boolean includeCancel,boolean includeReset,boolean includeTerminate,boolean includeReactivate,boolean includeBroadcast, boolean lbfButton) {
		// added for LBF NDP SIMS Hierarchy
		this(id, pageModel,parent, enclosingForm, dtoType,feedBackPanel, includeAddNew,includeSave,includeCancel,includeReset,includeTerminate,includeBroadcast,includeReactivate,true, lbfButton);
		
	}

	public BaseModificationButtonsPanel(String id, IMaintenancePageModel pageModel, 
			IMaintenanceParent parent, Form enclosingForm, Class dtoType,FeedbackPanel feedBackPanel, boolean includeAddNew,boolean includeSave,boolean includeCancel,boolean includeReset,boolean includeTerminate,boolean includeReactivate,boolean includeBroadcast) {
		this(id, pageModel,parent, enclosingForm, dtoType,feedBackPanel, includeAddNew,includeSave,includeCancel,includeReset,includeTerminate,includeBroadcast,includeReactivate,true, false);
	}
	
	
	/**
	 * Default constructor
	 * 
	 * @param id
	 * @param pageModel
	 * @param parent
	 * @param enclosingForm
	 * @param dtoType
	 */
	public BaseModificationButtonsPanel(String id, IMaintenancePageModel pageModel, 
			IMaintenanceParent parent, Form enclosingForm, Class dtoType,FeedbackPanel feedBackPanel, boolean includeAddNew,boolean includeSave,boolean includeCancel,boolean includeReset,boolean includeTerminate,boolean includeBroadcast) {
		this(id, pageModel,parent, enclosingForm, dtoType,feedBackPanel, includeAddNew,includeSave,includeCancel,includeReset,includeTerminate,includeBroadcast,false,true, false);
	}	

	//Added pks2802 - 18/09/08 (calling the default constructer causes 
	//initiliaseForm in child class to be callled before the flags for other buttons are set)
	public BaseModificationButtonsPanel(String id, IMaintenancePageModel pageModel, 
			IMaintenanceParent parent, Form enclosingForm, Class dtoType,FeedbackPanel feedBackPanel, boolean includeAddNew,boolean includeSave,boolean includeCancel,boolean includeReset,boolean includeTerminate,boolean includeBroadcast,boolean includeReactivate, boolean initialiseFormFlag, boolean lbfButton) {
		super(id);
		this.enclosingForm = enclosingForm;
		this.parent = parent;
		this.pageModel = pageModel;
		this.dtoType = dtoType;		
		this.includeAddNew = includeAddNew;	
		this.includeSave = includeSave;
		this.includeCancel = includeCancel;	
		this.includeReset = includeReset;
		this.includeTerminate = includeTerminate;
		this.includeBroadcast = includeBroadcast;
		this.includeReactivate = includeReactivate;
		this.feedBackPanel = feedBackPanel;
		this.includeLBFNDPCreateBtn = lbfButton;
		if(initialiseFormFlag){
			initialiseForm();
		}
		this.setEditState(parent.getEditState(), null);
	}	

	/**
	 * Override this method to replace buttons with user defined buttons
	 * @param buttons
	 * @return
	 */
	protected Button[] replaceButtons(Button[] buttons){
		return buttons;		
	}
	
	/**
	 * Add the components to the form and must be called after the constructor 
	 * is run. Allows additional attributes to be set before create the
	 * components.
	 */
	protected void initialiseForm() {			
		add(buttonPanel = createControlButtonPanel());
	}	
		
	/**
	 * Retrieve the current edit state
	 */
	public EditStateType getEditState() {
		return editState;
	}
	
	/**
	 * Update the edit state for this panel (enables / disables certain components)
	 */
	public void setEditState(EditStateType newState, AjaxRequestTarget target) {
		this.editState = newState;
		
		/* Set component access */
		if (editState == EditStateType.VIEW) {			
			modifyButton.setEnabled(isModifyButtonEnabled());
			if(includeAddNew){
				addNewButton.setEnabled(parent.hasAddAccess());
			}
			if(includeSave){
				this.saveButton.setEnabled(false);
			}
			if(includeCancel){
				this.cancelButton.setEnabled(false);
			}	
			if(includeReset){
				this.resetButton.setEnabled(false);
			}
			if(includeTerminate){				
				terminateButton.setEnabled(isTerminateButtonenabled());				
			}
			if(includeReactivate){				
				reactivateButton.setEnabled(isReactivateButtonenabled());				
			}
			if (includeBroadcast) {
				this.broadcastButton.setEnabled(isBroadcastButtonEnabled());
			}
			if(includeLBFNDPCreateBtn){
				lbfNDPCreateBtn.setEnabled(parent.hasAddAccess());
			}
			
		} else {
			modifyButton.setEnabled(false);
			if(includeAddNew){
				addNewButton.setEnabled(false);
			}
			if(includeSave){
				this.saveButton.setEnabled(true);
			}
			if(includeCancel){
				this.cancelButton.setEnabled(true);
			}
			if(includeReset){
				this.resetButton.setEnabled(true);
			}
			if(includeTerminate){				
				terminateButton.setEnabled(isTerminateButtonenabled());								
			}
			if(includeReactivate){
				reactivateButton.setEnabled(isReactivateButtonenabled());								
			}
			if (includeBroadcast) {
				this.broadcastButton.setEnabled(false);
			}
			if(includeLBFNDPCreateBtn){
				lbfNDPCreateBtn.setEnabled(false);
			}
		}
		
		/* Update components that might have changed */
		if (target != null) {
			if(includeAddNew){
				target.add(addNewButton);
			}
			if(includeSave){
				target.add(saveButton);
			}
			if(includeCancel){
				target.add(cancelButton);
			}
			if(includeReset){
				target.add(resetButton);
			}
			if(includeTerminate){
				target.add(terminateButton);			
			}
			if(includeReactivate){
				target.add(reactivateButton);			
			}
			if (includeBroadcast) {
				target.add(broadcastButton);
			}
			if(includeLBFNDPCreateBtn){
				target.add(lbfNDPCreateBtn);
			}
			target.add(modifyButton);			
		}	
	}


	/**
	 * Evaluates if the broadcast button should be enabled
	 * @return
	 */
	protected boolean isBroadcastButtonEnabled() {
		return editState.equals(EditStateType.VIEW) &&
			parent.hasModifyAccess();
	}


	/**
	 * Evaluates if the modify button should be enabled
	 * @return
	 */
	protected boolean isModifyButtonEnabled() {
		return editState.equals(EditStateType.VIEW) &&
		 pageModel.getSelectedItem()!=null
				&& parent.hasModifyAccess();
	}
	
	/**
	 * Will return true if reactivate button should be enabled
	 * 
	 * @return
	 */
	protected boolean isReactivateButtonenabled(){
		if(editState != EditStateType.ADD){
			return (pageModel.getSelectedItem()!=null && parent.hasModifyAccess());
		}
		return false;
	}
	
	/**
	 * Will return true if terminate button should be enabled
	 * 
	 * @return
	 */
	protected boolean isTerminateButtonenabled(){
		if(editState.equals(EditStateType.VIEW)){
			return pageModel.getSelectedItem()!=null
					&& parent.hasDeleteAccess();
		}
		return false;		
	}
	

	/**
	 * Add style information to tag
	 * 
	 * @param tag
	 */
	protected void decorateStyleOnTag(ComponentTag tag) {
		String style = (String) tag.getAttributes().get("style");
		style = (style==null)?"" : style;
		style+=";width:"+SELECTION_WIDTH+";";
		tag.put("style", style);
	}	
	
	/**
	 * Create the button panel
	 * 
	 * @return
	 */	
	protected Panel createControlButtonPanel() {
		int i=1;
		List<Button> buttonList = new ArrayList<Button>();
		
		modifyButton = createModifyButton("button"+i);	
		buttonList.add(modifyButton);
		
		if(includeAddNew){
			addNewButton = createAddNewButton("button"+(++i));
			buttonList.add(addNewButton);			
		}
			
		if(includeSave){
			saveButton = createSaveButton("button"+(++i));
			buttonList.add(saveButton);
		}
		if(includeCancel){
			cancelButton = createCancelButton("button"+(++i));
			buttonList.add(cancelButton);
		}
		if(includeReset){
			resetButton = createResetButton("button"+(++i));
			buttonList.add(resetButton);
		}
		if(includeTerminate){
			terminateButton = createTerminateButton("button"+(++i));
			buttonList.add(terminateButton);
		}	
		
		if(includeReactivate){
			reactivateButton = createReactivateButton("button"+(++i));
			buttonList.add(reactivateButton);
		}
		
		if(includeBroadcast){
			broadcastButton = createBroadcastButton("button"+(++i));
			buttonList.add(broadcastButton);
		}
		
		if(includeLBFNDPCreateBtn){
			lbfNDPCreateBtn = createLBFNDPButton("button"+(++i));
			buttonList.add(lbfNDPCreateBtn);
		}
		Button[] buttons = new Button[buttonList.size()];
		
		for(int j=0;j<buttonList.size();j++)
		{
			buttons[j] = buttonList.get(j);
		}
			
		Panel panel = ButtonHelperPanel.getInstance("controlButtonPanel",replaceButtons(buttons));
		panel.setOutputMarkupId(true);
		return panel;
		
	}
	
	/**
	 * Create the add new button
	 * 
	 * @param id
	 * @return
	 */
	protected Button createAddNewButton(String id) {
		// TODO Get form differently
		Button button = new AjaxFallbackButton(id, enclosingForm) {

			private static final long serialVersionUID = -5330766713711809776L;

			@Override
			protected void onComponentTag(ComponentTag tag) {
				super.onComponentTag(tag);
				tag.getAttributes().put("value", "Add New");
				tag.getAttributes().put("type", "submit");
			}

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form form) {
				if (logger.isDebugEnabled()) 
					logger.debug("do.add");
				doAddNew_onSubmit(target, form);
			}
		};
		button.setOutputMarkupId(true);
		return button;
	}
	
	/**
	 * Called when Add new is submitted. Notify parent and 
	 * swap panels.  Ensure that selected item is set before calling.
	 * 
	 * @param target
	 * @param form
	 */
	public void doAddNew_onSubmit(AjaxRequestTarget target, Form form) {
		//first we check access to this function, user might have buttons enabled, code mistake, but should not be allowed to add
		if(parent.hasAddAccess()){
			pageModel.setSelectedItem(getNewDtoInstance());
			parent.setEditState(EditStateType.ADD, target);
			parent.swapContainerPanel(target);
			parent.swapNavigationPanel(target);
		}else{
			//display error
		}
	}
	
	/**
	 * Creates a new instance of the DTO 
	 * 
	 * @return
	 */
	public Object getNewDtoInstance() {
		try {
			return dtoType.newInstance();
		} catch (IllegalAccessException e) {
			throw new IllegalArgumentException("Unable to create a new instance of DTO",e);
		} catch (InstantiationException e) {
			throw new IllegalArgumentException("Unable to create a new instance of DTO",e);
		}
	}

	/**
	 * Create the modify button
	 * 
	 * @param id
	 * @return
	 */
	protected Button createModifyButton(String id) {
		Button button = new AjaxFallbackButton(id, enclosingForm) {

			private static final long serialVersionUID = -5330766713711809772L;

			@Override
			protected void onComponentTag(ComponentTag tag) {
				super.onComponentTag(tag);
				tag.getAttributes().put("value", "Maintain");
				tag.getAttributes().put("type", "submit");
			}			

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form form) {
				if (logger.isDebugEnabled()) 
					logger.debug("do.modify");
				logger.info("#JB - MODIFY!!!!");
				//first we check if the object can be modified using the rules, done here as the doModify_onSubmit could be overrridden				
				if(parent == null || !(parent instanceof BasePage) || ((parent instanceof BasePage) && ((BasePage)parent).checkModificationRules())){
					doModify_onSubmit(target, form);
				}else{
					//display error that user is not allowed to modify the selected context					
					if(feedBackPanel != null){
						error("You may not modify the selected context due to rule restrictions");							
					}
				}
				if(feedBackPanel != null){					
					target.add(feedBackPanel);
				}
			}
		};
		button.setOutputMarkupId(true);
		return button;
	}
	
	/**
	 * Create the Save button
	 * 
	 * @param id
	 * @return
	 */
	protected Button createSaveButton(String id) {		
		final Button button = new AjaxFallbackButton(id, enclosingForm) {
			private static final long serialVersionUID = -5330766713711809772L;

			@Override
			protected void onComponentTag(ComponentTag tag) {
				super.onComponentTag(tag);
				tag.getAttributes().put("value", "Save");
				tag.getAttributes().put("type", "button");				
			}
			
//			//WICKETTEST WICKETFIX - Test this function
//			@Override
//			protected IAjaxCallDecorator getAjaxCallDecorator() {
//				return new AjaxCallDecorator() {
//					private static final long serialVersionUID = 1L;
//
//					public CharSequence decorateScript(CharSequence script) {
//						//disable cancel too			
//						String disableCancel = "";
//						if(cancelButton != null){
//							disableCancel = "getElementById('"+cancelButton.getMarkupId()+"').disabled=true;";
//						}						
//						return "this.disabled=true;"+disableCancel+"overlay(true);" + script;
//					}
//				};
//			}
			
			
			
			// New Way - All options
			@Override
			protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
				super.updateAjaxAttributes(attributes);

			        // Way of adding any handler
				attributes.getAjaxCallListeners().add(new AjaxCallListener() {

				  @Override
				  public CharSequence getInitHandler(Component component) {
						//disable cancel too			
						String disableCancel = "";
						if(cancelButton != null){
							disableCancel = "document.getElementById('"+cancelButton.getMarkupId()+"').disabled=true;";
						}						
						return "overlay(true);this.disabled=true;"+disableCancel
								+ super.getInitHandler(component);
				  }
				  
				  
				});
							

			}

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form form) {
				if (logger.isDebugEnabled()) 
					logger.debug("do.save");
				parent.doSave_onSubmit();
				//will only do below if save not successfull
				target.add(feedBackPanel);	
				target.add(this);
				if(cancelButton != null){
					target.add(cancelButton);
				}
			}

			@Override
			protected void onError(AjaxRequestTarget target, Form form) {
				//validation error occured
				if(feedBackPanel != null){
					target.add(feedBackPanel);
				}					
				target.add(this);
				if(cancelButton != null){
					target.add(cancelButton);
				}
			}	
		};		
		button.setOutputMarkupId(true);
		button.setOutputMarkupPlaceholderTag(true);
		return button;
	}
	
	/**
	 * A generic cancel button that invalidates the page
	 * 
	 * @param id
	 * @return
	 */
	protected Button createCancelButton(String id) {
		Button button = new AjaxFallbackButton(id, enclosingForm) {

			private static final long serialVersionUID = -5330766713711807176L;
			
//			@Override
//			protected IAjaxCallDecorator getAjaxCallDecorator() {
//				return new AjaxCallDecorator() {
//					private static final long serialVersionUID = 1L;
//
//					public CharSequence decorateScript(CharSequence script) {
//						return "this.disabled=true;overlay(true);" + script;
//					}
//				};
//			}
			
			// WICKETTEST WICKETFIX Test overlay
			// New Way - All options
			@Override
			protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
				super.updateAjaxAttributes(attributes);

			        // Way of adding any handler
				attributes.getAjaxCallListeners().add(new AjaxCallListener() {

				  @Override
				  public CharSequence getInitHandler(Component component) {					
						return "this.disabled=true;overlay('test');" + super.getInitHandler(component);
				  }
				  
//				  @Override
//				  public CharSequence getDoneHandler(Component component) {					
//						return "this.disabled=false;" + super.getDoneHandler(component);
//				  }
				  
				  
				});
							
			}
			
			@Override
			protected void onComponentTag(ComponentTag tag) {
				super.onComponentTag(tag);
				tag.getAttributes().put("value", "Cancel");
				tag.getAttributes().put("type", "submit");
			}	

			@Override
			protected void onSubmit(AjaxRequestTarget arg0, Form arg1) {
				if (logger.isDebugEnabled()) 
					logger.debug("do.cancel");
				do_cancel();			
			}		
			
		};
		button.setOutputMarkupId(true);
		button.setDefaultFormProcessing(false);
		return button;
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
		setResponsePage((Class<? extends Page>)parent.getClass());	
	}
	
	/**
	 * Called when Modify button is submitted.  Notify parent and 
	 * swap panels.
	 * 
	 * @param target
	 * @param form
	 */
	public void doModify_onSubmit(AjaxRequestTarget target, Form form) {
		if(parent.hasModifyAccess()){
			parent.setEditState(EditStateType.MODIFY, target);
			parent.swapContainerPanel(target);
			parent.swapNavigationPanel(target);			
			
		}else{
			//display error
		}
	}	
	
	/**
	 * Create the Reset button
	 * 
	 * @param id
	 * @return
	 */
	protected Button createResetButton(String id) {
		Button button = new AjaxFallbackButton(id, enclosingForm) {

			private static final long serialVersionUID = -5330766713711809772L;

			@Override
			protected void onComponentTag(ComponentTag tag) {
				super.onComponentTag(tag);
				tag.getAttributes().put("value", "Reset");
				tag.getAttributes().put("type", "reset");
			}

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form form) {
				if (logger.isDebugEnabled()) 
					logger.debug("do.reset");
				doReset_onSubmit(target, form);
				
			}

		
		};
		button.setOutputMarkupId(true);
		return button;
	}
	
	/**
	 * Create the Terminate button
	 * 
	 * @param id
	 * @return
	 */
	protected Button createReactivateButton(String id) {
		Button button = new AjaxButton(id, enclosingForm) {

			private static final long serialVersionUID = 1L;

			@Override
			protected void onComponentTag(ComponentTag tag) {
				super.onComponentTag(tag);
				tag.getAttributes().put("value", getReactivateButtonText());
				tag.getAttributes().put("type", "submit");
			}

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form form) {	
				if (logger.isDebugEnabled()) 
					logger.debug("do.reactivate");
//				first we check if the object can be deleted using the rules, done here as the doModify_onSubmit could be overrridden				
				if(parent == null || !(parent instanceof BasePage) || ((parent instanceof BasePage) && ((BasePage)parent).checkModificationRules())){
					doReactivate_onSubmit(target, form);					
					target.add(this);
					if(feedBackPanel != null){
						target.add(feedBackPanel);	
					}
				}else{
					//display error that user is not allowed to modify the selected context					
					if(feedBackPanel != null){
						error("You may not modify the selected context due to rule restrictions");	
						target.add(feedBackPanel);
					}
				}				
			}	
			
//			@Override
//			protected IAjaxCallDecorator getAjaxCallDecorator() {
//				return new AjaxCallDecorator() {
//					private static final long serialVersionUID = 1L;
//
//					public CharSequence decorateScript(CharSequence script) {
//						return "var conf = confirm('Are you sure you want to "+getReactivateButtonText()+" the selected context');if(conf != 1){if(typeof hideBusysign=='function') {hideBusysign();} return false;}else{this.disabled=true;overlay(true);};" + script;
//					}
//				};
//			}
			
			// WICKETTEST WICKETFIX Test overlay
			// New Way - All options
			@Override
			protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
				super.updateAjaxAttributes(attributes);

			        // Way of adding any handler
				attributes.getAjaxCallListeners().add(new AjaxCallListener() {

				  @Override
				  public CharSequence getInitHandler(Component component) {		
					  CharSequence script = super.getInitHandler(component);
					   return "var conf = confirm('Are you sure you want to "+getReactivateButtonText()+
							   " the selected context');if(conf != 1){if(typeof hideBusysign=='function') {hideBusysign();} return false;}"
							   + "else{this.disabled=true;overlay(true);};" 
							   + script;
				  }
				  
				  
				});
							
			}

			@Override
			protected void onError(AjaxRequestTarget target, Form form) {
				//validation error occured
				if(feedBackPanel != null){
					target.add(feedBackPanel);
				}					
				target.add(this);
			}	
		};
		button.setOutputMarkupId(true);
		button.setOutputMarkupPlaceholderTag(true);
		return button;
	}
	
	/**
	 * Create the Terminate button
	 * 
	 * @param id
	 * @return
	 */
	protected Button createTerminateButton(String id) {
		Button button = new AjaxButton(id, enclosingForm) {

			private static final long serialVersionUID = -5330766713711809772L;

			@Override
			protected void onComponentTag(ComponentTag tag) {
				super.onComponentTag(tag);
				tag.getAttributes().put("value", getTerminateButtonText());
				tag.getAttributes().put("type", "submit");
			}

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form form) {			
				if (logger.isDebugEnabled()) 
					logger.debug("do.terminate");
//				first we check if the object can be deleted using the rules, done here as the doModify_onSubmit could be overrridden				
				if(parent == null || !(parent instanceof BasePage) || ((parent instanceof BasePage) && ((BasePage)parent).checkModificationRules())){
					doTerminate_onSubmit(target, form);					
					target.add(this);
					if(feedBackPanel != null){
						target.add(feedBackPanel);	
					}
				}else{
					//display error that user is not allowed to modify the selected context					
					if(feedBackPanel != null){
						error("You may not modify the selected context due to rule restrictions");	
						target.add(feedBackPanel);
					}
				}				
			}	
			
//			@Override
//			protected IAjaxCallDecorator getAjaxCallDecorator() {
//				return new AjaxCallDecorator() {
//					private static final long serialVersionUID = 1L;
//
//					public CharSequence decorateScript(CharSequence script) {
//						return "var conf = confirm('Are you sure you want to "+getTerminateButtonText()+" the selected context');if(conf != 1){if(typeof hideBusysign=='function') {hideBusysign();} return false;}else{this.disabled=true;overlay(true);};" + script;
//					}
//				};
//			}
			
			// WICKETTEST WICKETFIX Test overlay
			// New Way - All options
			@Override
			protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
				super.updateAjaxAttributes(attributes);

			        // Way of adding any handler
				attributes.getAjaxCallListeners().add(new AjaxCallListener() {

				  @Override
				  public CharSequence getInitHandler(Component component) {		
					  CharSequence script = super.getInitHandler(component);
					  return "var conf = confirm('Are you sure you want to "+getTerminateButtonText()
					  	+" the selected context');if(conf != 1){if(typeof hideBusysign=='function') {hideBusysign();} "
					  	+ "return false;}else{this.disabled=true;overlay(true);};" + script;
		
				  }
				  
				  
				});
							
			}
			

			@Override
			protected void onError(AjaxRequestTarget target, Form form) {
				//validation error occured
				if(feedBackPanel != null){
					target.add(feedBackPanel);
				}					
				target.add(this);
			}	
		};
		button.setOutputMarkupId(true);
		button.setOutputMarkupPlaceholderTag(true);
		return button;
	}
	
	/**
	 * Returns the terminate button text</br>
	 * Override to change the button text to something other than <strong>Terminate</strong>
	 * @return
	 */
	protected String getTerminateButtonText(){
		return "Terminate";
	}
	
	/**
	 * Returns the reactivate button text</br>
	 * Override to change the button text to something other than <strong>Reactivate</strong>
	 * @return
	 */
	protected String getReactivateButtonText(){
		return "Reactivate";
	}
	
	/**
	 * Create the Broadcast button
	 * 
	 * @param id
	 * @return
	 */
	protected Button createBroadcastButton(String id) {
		Button button = new AjaxFallbackButton(id, enclosingForm) {

			private static final long serialVersionUID = -5330766713711809772L;

			@Override
			protected void onComponentTag(ComponentTag tag) {
				super.onComponentTag(tag);
				tag.getAttributes().put("value", "Broadcast");
				tag.getAttributes().put("type", "submit");
			}

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form form) {
				if (logger.isDebugEnabled()) 
					logger.debug("do.broadcast");
				logger.info("#JB - BROADCAST !!!!");
				doBroadcast_onSubmit(target, form);
			}

//			@Override
//			protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
//				super.updateAjaxAttributes(attributes);
//			        
//			        // SRS Convenience method for overLay hiding/showing
//			        attributes.getAjaxCallListeners().add(new SRSAjaxCallListener());
//			}

			@Override
			protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
				super.updateAjaxAttributes(attributes);
			        
			        // SRS Convenience method for overLay hiding/showing
			        attributes.getAjaxCallListeners().add(new SRSAjaxCallListener());
			}
			
		};
		button.setOutputMarkupId(true);
		
		return button;
	}
	
	/**
	 * Called when Broadcast button is submitted.  Notify parent and 
	 * swap panels.
	 * 
	 * @param target
	 * @param form
	 */
	public void doBroadcast_onSubmit(AjaxRequestTarget target, Form form) {
		//parent.setEditState(EditStateType.MODIFY, target);
		//parent.swapContainerPanel(target);
		//parent.swapNavigationPanel(target);
	}		

	
	/**
	 * Called when Reset button is submitted.  Notify parent and 
	 * swap panels.
	 * 
	 * @param target
	 * @param form
	 */
	public void doReset_onSubmit(AjaxRequestTarget target, Form form) {
		//parent.setEditState(EditStateType.MODIFY, target);
		//parent.swapContainerPanel(target);
		//parent.swapNavigationPanel(target);
	}	
	
	/**
	 * Called when Terminate button is submitted.  Notify parent and 
	 * swap panels.
	 * 
	 * @param target
	 * @param form
	 */
	public void doTerminate_onSubmit(AjaxRequestTarget target, Form form) {
		//parent.setEditState(EditStateType.MODIFY, target);
		//parent.swapContainerPanel(target);
		//parent.swapNavigationPanel(target);
	}	
	
	/**
	 * Called when Reactivate button is submitted.  Notify parent and 
	 * swap panels.
	 * 
	 * @param target
	 * @param form
	 */
	public void doReactivate_onSubmit(AjaxRequestTarget target, Form form) {
		//parent.setEditState(EditStateType.MODIFY, target);
		//parent.swapContainerPanel(target);
		//parent.swapNavigationPanel(target);
	}	

	/**
	 * Reset the selection
	 *
	 */
	public abstract void resetSelection();	
	
	/**
	 * Will return true if terminate button should be enabled
	 * 
	 * @return
	 */
	protected boolean isLBFNDPButtonenabled(){
		if(getEditState().equals(EditStateType.VIEW)){
			return pageModel.getSelectedItem()!=null
					&& parent.hasDeleteAccess();
		}
		return false;		
	}
	
	/**
	 * Create the LBFNDP button
	 * 
	 * @param id
	 * @return
	 */
	protected Button createLBFNDPButton(String id) {
		Button button = new AjaxFallbackButton(id, enclosingForm) {

			private static final long serialVersionUID = -5330766713711809779L;

			@Override
			protected void onComponentTag(ComponentTag tag) {
				super.onComponentTag(tag);
				tag.getAttributes().put("value", "Create Additional Branch");
				tag.getAttributes().put("type", "submit");
				//fixed the size of the button
				tag.getAttributes().put("style", "width: 150px;");
			}

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form form) {
				doAddNewLBFNDP_onSubmit(target, form);
			}
		};
		button.setOutputMarkupId(true);
		return button;
	}
	
	/**
	 * Called when LBF NDB create is submitted. Notify parent and 
	 * swap panels.  Ensure that selected item is set before calling.
	 * 
	 * @param target
	 * @param form
	 */
	public void doAddNewLBFNDP_onSubmit(AjaxRequestTarget target, Form form) {
		//first we check access to this function, user might have buttons enabled, code mistake, but should not be allowed to add
		if(parent.hasAddAccess()){
			pageModel.setSelectedItem(getNewDtoInstance());
			parent.setEditState(EditStateType.ADD, target);
			parent.swapContainerPanel(target);
			parent.swapNavigationPanel(target);
		}else{
			//display error
		}
	}
}