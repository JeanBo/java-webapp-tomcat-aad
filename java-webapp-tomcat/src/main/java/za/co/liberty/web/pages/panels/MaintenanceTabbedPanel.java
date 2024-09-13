package za.co.liberty.web.pages.panels;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxCallListener;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.ajax.markup.html.AjaxLink;

import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.extensions.markup.html.tabs.TabbedPanel;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.SubmitLink;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.request.cycle.RequestCycle;

import za.co.liberty.exceptions.security.TabAccessException;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.data.pages.ITabbedPageModel;
import za.co.liberty.web.helpers.javascript.DialogScriptBuilder;
import za.co.liberty.web.helpers.javascript.DialogScriptBuilder.DialogType;
import za.co.liberty.web.pages.BasePage;
import za.co.liberty.web.pages.interfaces.IMaintenanceParent;
import za.co.liberty.web.pages.interfaces.ISecurityPanel;
import za.co.liberty.web.pages.interfaces.IStatefullComponent;
import za.co.liberty.web.wicket.markup.html.tabs.CachingTab;
import za.co.liberty.web.wicket.validation.TabbedPanelValidator;

/**
 * <p>An abstract tabbed panel that can contain multiple tabs. 
 * Validation occurs whenever a different tab is selected. Call
 * {@linkplain this#validateAllTabs()} to force validation of all
 * tabs and their form components to take place.</p>  
 * 
 * <p>Default behaviour now prevents other tabs from being selected when 
 * the edit state is modify.  Override the method {@linkplain #isLockTabsForModify()}
 * if this behaviour should change.</p>
 *
 * <p>Note - This is a work in progress and as such it is still 
 * not complete.  Subpanels are not notified of state as it is 
 * assumed that this panel will be re-instantiated when state
 * changes.</p>
 * 
 * TODO JZB0608 - Add subpanel state notification
 * TODO JZB0608 - Allow panel state to change without instantiation.
 * 
 * @author JZB0608 - 14 May 2008
 * Modified by Dean Scott, added ITabbedPageModel which should keep track of the current tab
 * This then means the last selected tab should always be selected whenever the page 
 * gets refreshed or submitted
 * @author JZB0608 - 24 Nov 2009 - Allow for the disabling of tab locks when modifying (for admin screens)
 *
 */
public abstract class MaintenanceTabbedPanel  extends Panel implements IStatefullComponent {
	/* Constants */
	private static final long serialVersionUID = -9222355665504615229L;
	public static final String FORM_NAME = "mainForm";
	public static final String TABBED_PANEL_NAME = "tabbedPanel";
	
	/* Other */
	private EditStateType editState;
	protected ITabbedPageModel pageModel;
	protected Form form;
	
	/* Fields */
	protected TabbedPanel tabbedPanelField;	
	
	private int[] tabIndexToDisable;
	
	private Class[] tabPanelsToDisable;
	
	private List<AbstractTab> tabList;
	private IMaintenanceParent parent;
	
	
	/**
	 * Default constructor
	 * @throws TabAccessException 
	 * 
	 */
	public MaintenanceTabbedPanel(String id, ITabbedPageModel pageModel,
			EditStateType editState,IMaintenanceParent parent){
		super(id);
		this.editState = editState;
		this.pageModel = pageModel;
		this.tabIndexToDisable = null;
		this.tabPanelsToDisable = null;
		this.parent = parent;
		try {
			this.add(form = new TabForm(FORM_NAME));
		} catch (TabAccessException e) {
			//will never happen on this constructor as no tabs have been disabled
		}	
	}
	
	
	public MaintenanceTabbedPanel(String id, ITabbedPageModel pageModel,
			EditStateType editState, int[] tabIndexToDisable,IMaintenanceParent parent) throws TabAccessException {
		this(id,pageModel,editState,tabIndexToDisable,null,parent);	
	}	
	
	public MaintenanceTabbedPanel(String id, ITabbedPageModel pageModel,
			EditStateType editState, Class[] tabPanelsToDisable,IMaintenanceParent parent) throws TabAccessException {
		this(id,pageModel,editState,tabPanelsToDisable,null,null,parent);	
	}
	
	private MaintenanceTabbedPanel(String id, ITabbedPageModel pageModel,
			EditStateType editState, int[] tabIndexToDisable, List<AbstractTab> tabList, IMaintenanceParent parent) throws TabAccessException {
		this(id,pageModel,editState,null,tabIndexToDisable,null,parent);
	}
	
	
	private MaintenanceTabbedPanel(String id, ITabbedPageModel pageModel,
			EditStateType editState, Class[] tabPanelsToDisable, int[] tabIndexToDisable, List<AbstractTab> tabList, IMaintenanceParent parent) throws TabAccessException {
		super(id);
		this.editState = editState;
		this.pageModel = pageModel;
		this.tabIndexToDisable = tabIndexToDisable;
		this.tabPanelsToDisable = tabPanelsToDisable;
		this.tabList = tabList;
		this.parent = parent;
		this.add(form = new TabForm(FORM_NAME));		
	}

	/**
	 * Validate all form components on all tabs.
	 */
	public boolean validateAllTabs() {
		if (editState==EditStateType.VIEW) {
			throw new IllegalStateException("Tabs can only be validated when editing");
		}
//		pageModel.getSelectedItem().setRoleName("");  // Testing
		return new TabbedPanelValidator(tabbedPanelField).validate();
	}
	
	/**
	 * Form that contains all data for this panel
	 * 
	 * @author jzb0608 - 05 May 2008
	 * 
	 */
	public class TabForm extends Form {
		private static final long serialVersionUID = 8207665439577865351L;

		public TabForm(String id) throws TabAccessException {
			super(id);			
			add(tabbedPanelField = createTabbedPanel());
		}		
	}

	/**
	 * Part of {@linkplain IStatefullComponent} implementation
	 */
	public EditStateType getEditState() {
		return editState;
	}

	/**
	 * Only the selected tab will be shown if this value is true.  No other tab
	 * will then be able to be selected in modify mode.
	 * @return
	 */
	public boolean isLockTabsForModify() {
		return true;
	}
	
	/**
	 * The tabbed panel component gets created here
	 * 
	 * @return
	 * @throws TabAccessException 
	 */
	@SuppressWarnings("unchecked")
	protected TabbedPanel createTabbedPanel() throws TabAccessException {	
		if(tabList == null){
			tabList = new ArrayList<AbstractTab>();		
			initialiseTabs(tabList);
		}		
		
		/* Create the panel */
		final TabbedPanel tab = new TabbedPanel(TABBED_PANEL_NAME, (List)tabList) {	
			
			private static final long serialVersionUID = 1L;

			protected WebMarkupContainer newLink(String linkId, final int index) {								
				if((!isLockTabsForModify() || editState != EditStateType.MODIFY) && !panelMustBeDisabled(index)) {
					//only add link if the tab is not disabled					
					/* 
					 * This is to force the form to be submitted and
					 * the validation to be done when changing panels
					 */
					SubmitLink link = new SubmitLink(linkId) 
					{
						private static final long serialVersionUID = 1L;
	
						@Override
						public void onSubmit() {							
							setSelectedTab(index);
							if(parent != null){
								//swap the selection panel as security might have to be altered
								// WICKETFIX WICKETTEST
								parent.swapSelectionPanel(RequestCycle.get().find(AjaxRequestTarget.class));							
							}
							super.onSubmit();							
						}
					};						
					return link;
				}					
				WebMarkupContainer ret = new WebMarkupContainer(linkId);					
				if(isLockTabsForModify() && editState == EditStateType.MODIFY){
					if(index == pageModel.getCurrentTab()){
						ret.add(new AttributeModifier("style","background-color: #1F2241; color: white;"));
					}else{
						//replace the behaviour with error message
						ret.add(new AjaxEventBehavior("click"){							
							private static final long serialVersionUID = 1L;
//							@Override
//							protected void onEvent(AjaxRequestTarget target) {
//								
//								target.appendJavascript(DialogScriptBuilder.buildShowDialog(DialogType.WARNING, "Please cancel or save before clicking on another tab"));							
//							}	
//							

							// WICKETTEST WICKETFIX 
							// Needs to be tested to confirm if it works
							@Override
							protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
								super.updateAjaxAttributes(attributes);
								attributes.getAjaxCallListeners().add(new AjaxCallListener() {

									@Override
									public CharSequence getBeforeHandler(Component component) {
										return DialogScriptBuilder.buildShowDialog(DialogType.WARNING, "Please cancel or save before clicking on another tab")
												+ ";" + super.getBeforeHandler(component);
									}
									
									

								});
							}

							@Override
							protected void onEvent(AjaxRequestTarget target) {
								
								
							}
						});						
					}
				}else{
					ret = new AjaxLink(linkId){						
						private static final long serialVersionUID = 1L;
						@Override
						public void onClick(AjaxRequestTarget arg0) {
							//do nothing							
						}												
					}; 
					ret.add(new AttributeModifier("style"," background-color: #eef4f1; color: #bbbbbb;"));
				}				
				return ret;				
			}

			// WICKETTEST WICKETFIX
			// Method now returns a tabbed panel, to be tesed
			@Override
			public TabbedPanel setSelectedTab(int index) {
				//store selection for use when state changes
				pageModel.setCurrentTab(index);
				if(tabList != null){
					Object selectedTab = tabList.get(index).getPanel(TabbedPanel.TAB_PANEL_ID);
					pageModel.setCurrentTabClass(selectedTab.getClass());
				}				
				return super.setSelectedTab(index);
			}						
		};			
		/* Set the default tab */
		int selectedTab = getDefaultTab();
		if(pageModel.getCurrentTab() != -1){
			selectedTab = pageModel.getCurrentTab();
		}
		if(selectedTab < 0){
			selectedTab = 0;
		}		
		if(panelMustBeDisabled(selectedTab)){
			//find the first selectable one
			int j = 0;
			if((j = firstSelectableTab(tabList)) != -1){				
				tab.setSelectedTab(j);
			}else{
				//display error as all tabs have been disabled
				throw new TabAccessException("All tabs have been disabled, Please consult your system security consultant to gain access");				
			}			
		}else{
			tab.setSelectedTab(selectedTab);
		}		
		tab.setOutputMarkupId(true);	
		return tab;
	}	
	
	/**
	 * Set the current selected tab
	 * @param index
	 */
	public void setSelectedTab(int index){
		tabbedPanelField.setSelectedTab(index);
	}
	
	/**
	 * Will return the first tab not disabled
	 * @return
	 */
	private int firstSelectableTab(List<AbstractTab> tabList){		
		int tabSize = tabList.size();
		 for(int i = 0;i<tabSize;i++){			
			boolean disabled = panelMustBeDisabled(i);
			if(!disabled){
				return i;
			} 
		}
		return -1;
	}
	
	/**
	 * Checks if a tab must not be allowed to be clicked on
	 * @param panelIndex
	 * @return
	 */
	private boolean panelMustBeDisabled(int panelIndex){
		if(this.tabPanelsToDisable != null && tabPanelsToDisable.length != 0){
			AbstractTab tab = tabList.get(panelIndex);			
			Panel tabPanel = (Panel) tab.getPanel(TabbedPanel.TAB_PANEL_ID);
			for(Class panel : tabPanelsToDisable){
				if(panel == tabPanel.getClass()){
					return true;
				}
			}			
		}else if(this.tabIndexToDisable != null && tabIndexToDisable.length != 0){			
			for(int disabledTab : tabIndexToDisable){
				if(disabledTab == panelIndex){
					return true;
				}
			}			
		}		
		return false;
	}

	/**
	 * Determines the default selected tab
	 * 
	 * @return Return -1 if unknown
	 */
	public int getDefaultTab() {
		return -1;
	}
	
	
	/**
	 * Implement this method to add tabs to the Tabbed Panel
	 * 
	 * @param tabList
	 */
	public abstract void initialiseTabs(List<AbstractTab> tabList);
	
	/**
	 * Replace a tab with another panel
	 * @param index
	 * @param replacement
	 */
	@SuppressWarnings("unchecked")
	public List replaceTab(int index, final Panel replacement){		
		List tabs = this.tabbedPanelField.getTabs();		
		Object removal = tabs.get(index);		
		if(removal instanceof Panel){
			((Panel)removal).replaceWith(replacement);
			tabs.remove(index);
			tabs.add(replacement);
		}else if (removal instanceof ITab){
			Panel change = (Panel) ((ITab)removal).getPanel(TabbedPanel.TAB_PANEL_ID);
			if(change.getParent() != null){
				change.replaceWith(replacement);
			}
			tabs.remove(index);
			tabs.add(index, new CachingTab(((ITab)removal).getTitle(), replacement) {
				private static final long serialVersionUID = 1L;
				@Override
				public Panel createPanel(String id) {				
					return replacement;
				}
			});
		}
		return tabs;
	}
	
	public void removeTab(int i) {
		this.tabbedPanelField.getTabs().remove(i);
	}
	
	/**
	 * Will return a list of Security Panels that are kept in the tabs of this panel
	 * @return
	 */
	public ArrayList<TabPanelIndex> getSecuredPanelIndexes(){
		ArrayList<TabPanelIndex> ret = new ArrayList<TabPanelIndex>();
		List tabs = this.tabbedPanelField.getTabs();
		int index = 0;
		for(Object tab : tabs){
			Panel panel = null;			
			if(tab instanceof Panel){				
				panel = ((Panel)tab);
			}else if (tab instanceof ITab){				
				panel = (Panel) ((ITab)tab).getPanel(TabbedPanel.TAB_PANEL_ID);
			}
			if(panel != null && panel instanceof ISecurityPanel){
				ret.add(new TabPanelIndex(index,panel,null));
			}
			++index;
		}
		return ret;
	}
	
	/**
	 * Will return a list of all the Panels that are kept in the tabs of this panel
	 * @return
	 */
	public ArrayList<TabPanelIndex> getAllPanelIndexes(){
		ArrayList<TabPanelIndex> ret = new ArrayList<TabPanelIndex>();
		List tabs = this.tabbedPanelField.getTabs();
		int index = 0;
		for(Object tab : tabs){
			Panel panel = null;
			String name = null;
			if(tab instanceof Panel){				
				panel = ((Panel)tab);
				//TODO get name
			}else if (tab instanceof ITab){				
				panel = (Panel) ((ITab)tab).getPanel(TabbedPanel.TAB_PANEL_ID);
				name = (String)((ITab)tab).getTitle().getObject();
			}
			if(panel != null){
				ret.add(new TabPanelIndex(index,panel,name));
			}
			++index;
		}
		return ret;
	}
	
	/**
	 * Replace a tabs that are instanceof clazz with another panel
	 * @param index
	 * @param replacement
	 */
	@SuppressWarnings("unchecked")
	public List replaceTabs(Class clazz, final Panel replacement){		
		List tabs = this.tabbedPanelField.getTabs();		
		ArrayList<Integer> replacements = new ArrayList<Integer>();
		for(int index = 0; index<tabs.size();index++){
			Object tab = tabs.get(index);				
			if(tab instanceof Panel){	
				if(tab.getClass() == clazz){
					((Panel)tab).replaceWith(replacement);
					replacements.add(index);
					tabs.remove(index);
					tabs.add(index,replacement);
				}
			}else if (tab instanceof ITab){				
				Panel change = (Panel) ((ITab)tab).getPanel(TabbedPanel.TAB_PANEL_ID);
				if(change.getClass() == clazz){
					change.replaceWith(replacement);
					tabs.remove(index);
					tabs.add(index, new CachingTab(((ITab)tab).getTitle(), replacement) {
						private static final long serialVersionUID = 1L;
						@Override
						public Panel createPanel(String id) {				
							return replacement;
						}
					});
				}
			}			
		}		
		return tabs;
	}
	
	/**
	 * Returns the currently selected tab panel
	 * @return
	 */
	public Panel getSelectedTabPanel(){
		Object tab = tabbedPanelField.getTabs().get(tabbedPanelField.getSelectedTab());
		if(tab instanceof Panel){
			return (Panel) tab;
		}else if (tab instanceof ITab){
			// WICKETTEST WICKETFIX - May have issues with this cast (panel)
			return (Panel) ((ITab)tab).getPanel(TabbedPanel.TAB_PANEL_ID);			
		}
		return null;
	}
	
	/**
	 * Keeps track of security panels in this tabbed panel
	 * @author DZS2610
	 *
	 */
	public class TabPanelIndex{
		int index;
		Panel panel;
		String panelName;
		private TabPanelIndex(int index,Panel panel,String panelName){
			this.index = index;
			this.panel = panel;
			this.panelName = panelName;
		}
		public Panel getPanel() {
			return panel;
		}
		public int getIndex() {
			return index;
		}
		public String getPanelName() {			
			return panelName;
		}
		
	}
	
	/**
	 * If the parent was paased in this method will return its feedback panel
	 * @return
	 */
	public FeedbackPanel getFeedBackPanelFromParent(){
		if(parent != null && parent instanceof BasePage){
			return ((BasePage)parent).getFeedbackPanel();
		}
		return null;
	}


	public IMaintenanceParent getIMaintenanceParent() {
		return parent;
	}
}
