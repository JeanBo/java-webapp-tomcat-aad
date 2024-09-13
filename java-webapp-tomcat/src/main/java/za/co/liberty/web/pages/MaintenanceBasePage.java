package za.co.liberty.web.pages;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxCallListener;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.authorization.UnauthorizedInstantiationException;
import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.extensions.markup.html.tabs.TabbedPanel;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.settings.ApplicationSettings;

import za.co.liberty.dto.userprofiles.MenuItemDTO;
import za.co.liberty.exceptions.data.ValidationException;
import za.co.liberty.helpers.util.SRSUtility;
import za.co.liberty.interfaces.agreements.requests.RequestKindType;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.models.PagePanelInfoObject;
import za.co.liberty.web.pages.interfaces.IChangeableStatefullComponent;
import za.co.liberty.web.pages.interfaces.IHasAccessPanel;
import za.co.liberty.web.pages.interfaces.IMaintenanceParent;
import za.co.liberty.web.pages.interfaces.ISecurityPanel;

import za.co.liberty.web.pages.panels.ButtonHelperPanel;
import za.co.liberty.web.pages.panels.MaintenanceTabbedPanel;
import za.co.liberty.web.pages.panels.MaintenanceTabbedPanel.TabPanelIndex;
import za.co.liberty.web.pages.panels.SRSSecurityMessagePanel;
import za.co.liberty.web.system.SRSApplication;
import za.co.liberty.web.system.SRSAuthWebSession;

/**
 * <p>Base web page for Maintenance pages.  The contract between this page
 * and it's sub pages as well as the life cycle is explained below.</p>  
 * 
 * <p>Page is split into sections as follows.
 * <ul>
 *   <li>Top "Selection Panel" - Responsible for selecting records 
 *   and edit state (add, modify)</li>
 *   <li>Centre "Container Panel" - Responsible for showing the data</li>
 *   <li>Bottom "Navigation Panel" - Responsible for navigational buttons</li>
 * </ul>
 * Sections should communicate with each other via the interface 
 * {@link IChangeableStatefullComponent} even when inner classes are used for sub 
 * panels.  Sub panels should not call their own interface directly but 
 * should use a reference to {@link IMaintenanceParent} for state change 
 * notifications as this page will automatically notify all sub panels 
 * that implement {@link IChangeableStatefullComponent}. 
 * </p>
 * 
 * <p><b>Life Cycle - </b>On object instantiation the state is set to 
 * VIEW and the page is valid (which can be checked by calling 
 * {@link #isPageValid()}.  A page must be invalidated after a succesfull
 * save or cancel to prevent re-submission (due to back button problem).
 * This can be done by calling the method {@link #invalidatePage()}. 
 * Forward to a new page instance after a succesfull save or cancel as 
 * the current page will not be allowed to change to one of the editable 
 * states. 
 * </p>
 * 
 * <p><strong>Notes / Issues</strong><br/>
 * TODO jzb0608 - Currently a setEditState has to be called to force subpanels 
 * 		to be updated, their interfaces should change to include the edit state
 * 		at creation time.
 * </p>
 * 
 * @author jzb0608 - 30 Apr 2008
 * Modified by Dean Scott 31 July to include PageModelExtraValueObject 
 * which allows A user of this page to specify an extra object
 * and use it in the page model initialization
 * Modified by Dean Scott 03 November to cater for panel,basically tab, level security 
 * 
 */
public abstract class MaintenanceBasePage<PageModelExtraValueObject extends Object> extends BasePage
		implements IChangeableStatefullComponent , IMaintenanceParent {

	private static final long serialVersionUID = 9067518105519936312L;
	
	/* Constants */
	public static final String CONTAINER_PANEL_NAME = "containerPanel";
	public static final String NAGIVATION_PANEL_NAME = "navigationButtonPanel";
	public static final String SELECTION_PANEL_NAME = "selectionPanel";
	public static final String SELECTION_FORM_NAME = "selectionForm";
	public static final String CONTAINER_FORM_NAME = "containerForm";
	
	/* Forms */
	protected SelectionForm selectionForm;
	protected ContainerForm containerForm;

	/* Panels */
	protected Panel selectionPanel;
	protected Panel containerPanel;
	protected Panel navigationPanel;	
	
	/*Feedback panel*/
	protected FeedbackPanel feedbackPanel;

	/* State variables */
	private boolean isValid; // Leave scope as private
	protected boolean isNotifyPanels;  // Sub panels are notified of state changes
	
	/* extra detail Object for use on page model creation*/
	PageModelExtraValueObject pageModelExtraValueObject;	
	
	private Object pageModel = null;
	
	private List<PagePanelInfoObject> panelInfoObjects;
	
	/**
	 * Default constructor
	 * 
	 */
	public MaintenanceBasePage() {
		this(null);
	}
	
	public MaintenanceBasePage(boolean removeMenuItems) {
		this(removeMenuItems,null,null);
	}
	
	public MaintenanceBasePage(Object obj){
		this(false,obj,null);		
	}	
	
	/**
	 * 
	 * @param dto
	 */
	public MaintenanceBasePage(Object obj,PageModelExtraValueObject pageModelExtraValueObject) {
		this(false,obj,pageModelExtraValueObject);
	}

	/**
	 * 
	 * @param dto
	 */
	public MaintenanceBasePage(boolean removeMenuItems,Object obj,PageModelExtraValueObject pageModelExtraValueObject) {
		super(removeMenuItems);	
		
//		getLogger().info("Base Page Construct - 1");
		//Check that the user has been authenticated correctly
		if(!SRSAuthWebSession.get().isAuthenticated()){
			// WICKETTEST WICKETFIX  - THis should work by default now, no replacement for it.
//			SRSAuthWebSession.get().cleanupFeedbackMessages();
			throw new UnauthorizedInstantiationException(MaintenanceBasePage.class);
		}	
		this.pageModelExtraValueObject = pageModelExtraValueObject;		
		isValid = true;
		pageModel = initialisePageModel(obj, pageModelExtraValueObject);	
//		getLogger().info("Base Page Construct - 2");
		containerForm = new ContainerForm(CONTAINER_FORM_NAME);
		checkPanelAccess();		
		add(selectionForm = new SelectionForm(SELECTION_FORM_NAME));
		add(containerForm);
		add(getFeedbackPanel());
		isNotifyPanels=true;
		// This forces subpanels to be updated with the edit state	
		setEditState(getEditState(), null);
//		getLogger().info("Base Page Construct - 3 end");
		//apply panel level security check on each panel		
	}
	
	/**
	 * Will check what panel access the user has<br/>
	 * If a user does not contain access to a panel it will be disabled
	 *
	 */
	@SuppressWarnings("unchecked")
	private void checkPanelAccess(){
		
		if(containerPanel instanceof TabbedPanel){
			//we will use tabs to get to the panels
			List<ITab> tabs = ((TabbedPanel)containerPanel).getTabs();			
			for(ITab tab : tabs){
				Panel removal = (Panel) tab.getPanel(TabbedPanel.TAB_PANEL_ID);//tabs.get(panelInfoObject.getTabIndexOfPanel());
				if(removal != null && removal instanceof ISecurityPanel && !userHasAccessToPanel(((ISecurityPanel)removal).getPanelClass().getName())){
//					user does not have access to tab so we replace tabe with error message								
					((Panel)removal).replaceWith(new SRSSecurityMessagePanel(removal.getId(),(ISecurityPanel)removal));					
				}
			}												
		}else if(containerPanel instanceof MaintenanceTabbedPanel){				
			MaintenanceTabbedPanel tabPanel = ((MaintenanceTabbedPanel)containerPanel);
			int removeCount=0;
			for(TabPanelIndex securedPanel : tabPanel.getSecuredPanelIndexes()){
				if(securedPanel.getPanel() != null && securedPanel.getPanel() instanceof ISecurityPanel  && !userHasAccessToPanel(((ISecurityPanel)securedPanel.getPanel()).getPanelClass().getName())){
//					user does not have access to tab so we replace tabe with error message
//					if (securedPanel.getPanel() instanceof ISecurityPanelConfiguration && ((ISecurityPanelConfiguration)securedPanel.getPanel()).isRemoveNoAccessPanel() ) {
//						tabPanel.removeTab(securedPanel.getIndex()-removeCount++);
//					} else {
						tabPanel.replaceTab(securedPanel.getIndex(), new SRSSecurityMessagePanel(TabbedPanel.TAB_PANEL_ID,(ISecurityPanel)securedPanel.getPanel()));
//					}
				}
			}			
		}				
	}	
	
	/**
	 *Returns a list of PagePanelInfoObject which detail the tabs in this page if there are tabs, This is used to display menu items</br>
	 *Ovveride this method if you would like to specify other names for selection on the menu bar
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<PagePanelInfoObject> getPagePanelsInfo(){
		if(panelInfoObjects == null){		
			panelInfoObjects = new ArrayList<PagePanelInfoObject>();			
			if(containerPanel instanceof TabbedPanel){
				//we will use tabs to get to the panels
				List<ITab> tabs = ((TabbedPanel)containerPanel).getTabs();				
				int index = 0;
				for(ITab tab : tabs){					
					if(tab != null){
						Panel panel = (Panel) tab.getPanel(TabbedPanel.TAB_PANEL_ID);
						if(panel != null){
							//TODO,Dean get name of panel
							panelInfoObjects.add(new PagePanelInfoObject(tab.getClass().getName(),index,panel.getId()));
						}
					}
					index++;
				}												
			}else if(containerPanel instanceof MaintenanceTabbedPanel){				
				MaintenanceTabbedPanel tabPanel = ((MaintenanceTabbedPanel)containerPanel);
				for(TabPanelIndex securedPanel : tabPanel.getAllPanelIndexes()){
					if(securedPanel.getPanel() != null && securedPanel.getPanelName() != null){
						panelInfoObjects.add(new PagePanelInfoObject(securedPanel.getPanel().getClass().getName(),securedPanel.getIndex(),securedPanel.getPanelName()));
					}
				}			
			}			
		}
		return panelInfoObjects;
	}	
		
	private boolean userHasAccessToPanel(String  className){
		if(menuMap != null){
			MenuItemDTO menuItem = menuMap.get(className);		
			if(menuItem != null){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Update the panel
	 * 
	 * @param target
	 */
	public void swapContainerPanel(AjaxRequestTarget target) {
		Panel panel = createContainerPanel();
		containerPanel.replaceWith(panel);
		containerPanel = panel;
		if (target!=null) {
			target.add(containerPanel);
		}
	}

	/**
	 * Update the panel
	 * 
	 * @param target
	 */
	public void swapNavigationPanel(AjaxRequestTarget target) {
		Panel panel = createNavigationPanel();
		navigationPanel.replaceWith(panel);
		navigationPanel = panel;
		if (target!=null) {
		target.add(navigationPanel);
		}
	}
	
	/**
	 * Update the panel
	 * 
	 * @param target
	 */
	public void swapSelectionPanel(AjaxRequestTarget target) {
		Panel panel = createSelectionPanel();
		selectionPanel.replaceWith(panel);
		selectionPanel = panel;
		if (target!=null) {
		target.add(selectionPanel);
		}
	}
	
	/**
	 * Update the panel
	 * 
	 * @param target
	 */
	public void swapSelectionPanel(AjaxRequestTarget target, Class selectedTab) {
		swapSelectionPanel(target);
	}

	/**
	 * Update the form edit state and notify all subpanels
	 * 
	 * @param newState New state of page
	 * @param target Ajax target
	 */
	public void setEditState(EditStateType newState, AjaxRequestTarget target) {
		super.setEditState(newState, target);
		if (isNotifyPanels) {
			if (selectionPanel != null && selectionPanel instanceof IChangeableStatefullComponent) {
				((IChangeableStatefullComponent)selectionPanel).setEditState(newState, target);
			} 
			if (navigationPanel != null && navigationPanel instanceof IChangeableStatefullComponent) {
				((IChangeableStatefullComponent)navigationPanel).setEditState(newState, target);
			} 
		}
	}
		
	/**
	 * Should be called after a succesfull save or cancel!
	 *
	 */
	public void invalidatePage() {
		isValid = false;
	}
	
	/**
	 * Indicates if the page is valid.  If false, an error message
	 * will by written to the component and this method will return 
	 * false.
	 * 
	 * @return
	 */
	public boolean isPageValid() {
		if (isValid==false) { 
			this.error(this.getString("page.expired"));
		}
		return isValid;
	}
	
	/**
	 * A generic cancel button that invalidates the page
	 * 
	 * @param id
	 * @return
	 */
	protected Button createCancelButton(String id) {
		Button button = new Button(id) {

			private static final long serialVersionUID = -5330766713711807176L;
			
			@Override
			protected void onComponentTag(ComponentTag tag) {
				super.onComponentTag(tag);
				tag.getAttributes().put("value", "Cancel");
				tag.getAttributes().put("type", "submit");
			}

			@Override
			public void onSubmit() {
				invalidatePage();
				setResponsePage(MaintenanceBasePage.this.getClass());	
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
	protected Button createSaveButton(String id) {
		Button button = new Button(id) {

			private static final long serialVersionUID = -5330766713711809176L;
			
			@Override
			protected void onComponentTag(ComponentTag tag) {
				super.onComponentTag(tag);
				tag.getAttributes().put("value", "Save");
				tag.getAttributes().put("type", "submit");
			}

			@Override
			public void onSubmit() {
				if (!isPageValid()) {
					return;
				}
				super.onSubmit();
				doSave_onSubmit();
			}
			
		};
		button.setOutputMarkupId(true);
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
		
		
		
		Button button = createSaveButton(id);
		if (isShowOverlay) {
			button.add(new AjaxFormComponentUpdatingBehavior("click") {
				protected void onUpdate(AjaxRequestTarget target) {
					
				}
				
				// WICKETTEST WICKETFIX 
				// Needs to be tested to confirm if it works
				@Override
				protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
					super.updateAjaxAttributes(attributes);
					attributes.getAjaxCallListeners().add(new AjaxCallListener() {

						@Override
						public CharSequence getInitHandler(Component component) {
							return "overlay(true);" + super.getInitHandler(component);
						}

					});
				}
			});
		}
		return button;
	}
	
	/**
	 * This should be overridden when the default save button is used
	 *
	 */
	 public void doSave_onSubmit() {
		 
	}

	/**
	 * Retrieve the controller panel<br/>
	 * Modified by Dean Scott to default to the MaintainenceButtonsSelectionPanel
	 * 
	 * @return
	 */
	public abstract Panel createSelectionPanel();

	/**
	 * Retrieve the container panel
	 * 
	 * @return
	 */
	public abstract Panel createContainerPanel();
	
	/**
	 * Create the navigation panel
	 * 
	 * @return
	 */
	protected Panel createNavigationPanel() {
			EditStateType editState = getEditState();
			
			/* Always returns an empty panel when in view state */
			if (!isCreateNavigationPanel()) {
				return (Panel) new EmptyPanel(NAGIVATION_PANEL_NAME)
					.setOutputMarkupId(true);
			}
			//Modified - pks2802 
			Button[] buttons = createNavigationalButtons();
			if(buttons == null || buttons.length == 0)
				return (Panel) new EmptyPanel(NAGIVATION_PANEL_NAME)
				.setOutputMarkupId(true);
			/* Place the navigational button panel */
			Panel panel = ButtonHelperPanel.getInstance(NAGIVATION_PANEL_NAME, buttons);
			panel.setOutputMarkupId(true);
			return panel;
	}

	/**
	 * True if the navigation panel must be shown.
	 * @return
	 */
	protected boolean isCreateNavigationPanel() {
		return !(getEditState() == EditStateType.VIEW || getEditState() == null);
	}

	/**
	 * Initialse the navigational buttons that are required
	 * 
	 * @return
	 */
	public abstract Button[] createNavigationalButtons();


	/**
	 * Initialise the form data model
	 * 
	 * @param obj - selected object
	 */
	public abstract Object initialisePageModel(Object obj,PageModelExtraValueObject pageModelExtraValueObject);
	
	/**
	 * Disable as we will be showing the panel ourselves
	 */
	@Override
	protected boolean isShowFeedBackPanel() {
		return false;
	}

	/**
	 * Form that encloses control panel
	 * 
	 * @author jzb0608 - 24 Apr 2008
	 * 
	 */
	public class SelectionForm extends Form {

		private static final long serialVersionUID = 6824317351935308007L;

		public SelectionForm(String id) {
			super(id);
			add(selectionPanel = createSelectionPanel());			
		}

		@Override
		protected void onSubmit() {
			super.onSubmit();
		}

	}
	
	/**
	 * Form that encloses the inner form, inner panel and
	 * navigation buttons
	 * 
	 * @author JZB0608 - 29 Apr 2008
	 *
	 */
	public class ContainerForm extends Form {

		private static final long serialVersionUID = 8696500303858391077L;

		public ContainerForm(String id) {
			super(id);
			add(containerPanel = createContainerPanel());
			add(navigationPanel = createNavigationPanel());
		}
		
		public ContainerForm(String id, Panel containerPanel, Panel navigationPanel) {
			super(id);
			add(MaintenanceBasePage.this.containerPanel = containerPanel);
			add(MaintenanceBasePage.this.navigationPanel = navigationPanel);
		}
	}		

	@Override
	public boolean hasAddAccess(Object callingObject) {
		//Check the current system mode 
		if (SRSAuthWebSession.get().isSystemInReadOnlyMode()|| SRSAuthWebSession.get().isSystemInBatchOnlyMode()) {
			return false;
		}		
		//for add, if a panel does not contain add access option then we default to the main page
		boolean addAccess = super.hasAddAccess(callingObject);		
		if(menuMap != null){			
			if(callingObject instanceof MaintenanceBasePage){							
				Panel panel = getCurrentPanel(callingObject);		
				if(panel != null && SRSUtility.containsInterface(ISecurityPanel.class, (panel != null) ? panel.getClass() : null)){					
					MenuItemDTO menuItem = menuMap.get(((ISecurityPanel)panel).getPanelClass().getName());					
					//we get the menu item from the DB
					MenuItemDTO systemPanelConfig = SRSAuthWebSession.get().getPanelConfigurationInDB(((ISecurityPanel)panel).getPanelClass());
					//we only check the panel config if it has the option available
					if(systemPanelConfig != null && !systemPanelConfig.isAddAccess()){
						return addAccess;
					}
					if(menuItem != null){					 
						return menuItem.isAddAccess();
					}else{
						return false;
					}
				}
			}
		}
		return addAccess;		
	}	

	@Override
	public boolean hasDeleteAccess(Object callingObject) {
		//Check the current system mode 
		if (SRSAuthWebSession.get().isSystemInReadOnlyMode()|| SRSAuthWebSession.get().isSystemInBatchOnlyMode()) {
			return false;
		}
		boolean hasDelete = super.hasDeleteAccess(callingObject);
		if(menuMap != null){
			if(callingObject instanceof MaintenanceBasePage){				
				Panel panel = getCurrentPanel(callingObject);		
				if(panel != null && SRSUtility.containsInterface(ISecurityPanel.class, (panel != null) ? panel.getClass() : null)){					
					MenuItemDTO menuItem = menuMap.get(((ISecurityPanel)panel).getPanelClass().getName());					
					//we get the menu item from the DB
					MenuItemDTO systemPanelConfig = SRSAuthWebSession.get().getPanelConfigurationInDB(((ISecurityPanel)panel).getPanelClass());
					//we only check the panel config if it has the option available
					
					if(systemPanelConfig != null && !systemPanelConfig.isDeleteAccess()){
						return hasDelete;
					}
					if(menuItem != null){					 
						return menuItem.isDeleteAccess();
					}else{
						return false;
					}
				}
			}
		}
		return hasDelete;		
	}
	
	/**
	 * Will return the current selected panel or page
	 * @return
	 */
	private Panel getCurrentPanel(Object callingObject){
		Panel container = ((MaintenanceBasePage)callingObject).containerPanel;	
		if(container instanceof TabbedPanel){
			//we will use tabs to get to the panels otherwise it will be a panel on its own
			Object current = ((TabbedPanel)container).getTabs().get(((TabbedPanel)container).getSelectedTab());
			if(current != null && current instanceof SRSSecurityMessagePanel){
				current = ((SRSSecurityMessagePanel)current).getOrigionalPanel();
			}
			return (current instanceof Panel) ? (Panel)current : null;
		}if(container instanceof MaintenanceTabbedPanel){
			//we will use tabs to get to the panels otherwise it will be a panel on its own
			Object selected = ((MaintenanceTabbedPanel)container).getSelectedTabPanel();
			if(selected != null && selected instanceof SRSSecurityMessagePanel){
				selected = ((SRSSecurityMessagePanel)selected).getOrigionalPanel();
			}
			return ((selected != null && selected instanceof Panel) ? (Panel)selected : null);
		}else{
			//container is not in a tab, will use this panel to check security
			return container;
		}	
	}
	
	@Override
	public boolean hasModifyAccess(Object callingObject) {	
		//Check the current system mode 
		if (SRSAuthWebSession.get().isSystemInReadOnlyMode()|| SRSAuthWebSession.get().isSystemInBatchOnlyMode()) {
			return false;
		}

		if(menuMap != null){
			if(callingObject instanceof MaintenanceBasePage){
				Panel panel = getCurrentPanel(callingObject);		
				if(panel != null && SRSUtility.containsInterface(ISecurityPanel.class, (panel != null) ? panel.getClass() : null)){					
					MenuItemDTO menuItem = menuMap.get(((ISecurityPanel)panel).getPanelClass().getName());	
					boolean value = false;
					if(menuItem != null){					 
						value = menuItem.isModifyAccess();
					}
					if (panel instanceof IHasAccessPanel) {
						value = ((IHasAccessPanel)panel).hasModifyAccess(value);
					}
					return value;
				}
			}
		}
		return super.hasModifyAccess(callingObject);
	}

	protected Object getPageModel() {
		return pageModel;
	}

	@Override
	public FeedbackPanel getFeedbackPanel() {
		if(feedbackPanel == null){
			//done like this so that if this is called before the constructor gets to the init of the panel then we have the same ref later
			feedbackPanel = new FeedbackPanel("internalMessages");
			feedbackPanel.setOutputMarkupId(true);
			feedbackPanel.setOutputMarkupPlaceholderTag(true);
		}
		return feedbackPanel;
	}

	public Panel getSelectionPanel() {
		return selectionPanel;
	}	

	public Panel getContainerPanel() {
		return containerPanel;
	}

	public Panel getNavigationPanel() {
		return navigationPanel;
	}
	
	
}
