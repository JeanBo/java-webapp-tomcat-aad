package za.co.liberty.web.pages;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.log4j.Logger;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.tabs.TabbedPanel;
import org.apache.wicket.feedback.FeedbackMessage;
import org.apache.wicket.feedback.FeedbackMessages;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebComponent;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;

import org.jdom2.Document;
import org.jdom2.Element;
import za.co.liberty.dto.userprofiles.MenuItemDTO;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.data.pages.ITabbedPageModel;
import za.co.liberty.web.helpers.javascript.DialogScriptBuilder;
import za.co.liberty.web.helpers.javascript.DialogScriptBuilder.DialogType;
import za.co.liberty.web.pages.interfaces.IStatefullComponent;
import za.co.liberty.web.pages.panels.MaintenanceTabbedPanel;
import za.co.liberty.web.system.ConcurrentSessionForUserRuntimeException;
import za.co.liberty.web.system.SRSApplication;
import za.co.liberty.web.system.SRSAuthWebSession;



/**
 * Render the menu links.
 * 
 * @author DHR1910
 * @author JZB0608 - 29 Sep 2008 - Added edit state & warning when attempting 
 * 				to change page when not viewing.
 * @author DZS2610 - 25 November 2008 - Added in the menu item grouping 
 * @author JZB0608 - 13 September 2011 - Leave group expanded if any of the sub-items were selected
 * @author JZB0608 - 7 Feb 2018 - Fix issue with menu items once we upgraded to Java 6
 */
public class MenuContainer extends Panel {
	private static final long serialVersionUID = 1L;
	private static final String FIRST_LEVEL_MENU_ITEMS = "@@First Level";
	
	protected List<MenuItemDTO> menuItems;
	protected IStatefullComponent statefullComponent;		
	private HashMap<String,WebMarkupContainer> menuLinks = new HashMap<String, WebMarkupContainer>();
	
	private static Logger logger = Logger.getLogger(MenuContainer.class);
	private Class<? extends Page> pageClass;
	private MenuItemDTO selectedMenuItem;
	private String selectedGroup = "";
	private String selectedItem = "";
	private boolean shownGroup = false;
	private Set<String> groupSet = new HashSet<String>();
	
	/**
	 * Default constructor
	 * 
	 * @param id
	 * @param statefullComponent  The parent component that defines the edit state of 
	 * 		the page.
	 * @param menuItems
	 */
	public MenuContainer(String id, IStatefullComponent statefullComponent, 
			List<MenuItemDTO> menuItems, Class<? extends Page> pageClass) {
		super(id);		
		this.setOutputMarkupId(true);
		this.statefullComponent = statefullComponent; 
		this.pageClass = pageClass;
		
//		System.out.println("\n==> Iterator Example...");
//		Iterator<MenuItemDTO> crunchifyIterator = menuItems.iterator();
//		while (crunchifyIterator.hasNext()) {
//			System.out.println(crunchifyIterator.next().getMenuItemDescription());
//		}
		
		/* Remove menu items that don't link to valid classes */
		List<MenuItemDTO> newItems = new ArrayList<MenuItemDTO>(menuItems.size());
		int count = 0;
		for (MenuItemDTO menuItem : menuItems) {
			try {
				logger.debug("Orig " + (++count) 
						+ " - id=" + menuItem.getDbKey() 
						+ " -name=" + menuItem.getMenuItemName());
				
				Class<?> c = Class.forName(menuItem.getImplClazz());
				if (c.equals(pageClass)) {
					selectedMenuItem = menuItem;
					if (logger.isDebugEnabled()) logger.debug("MenuItem : found menu for basePage " + c);
				}
				newItems.add(menuItem);
			} catch (ClassNotFoundException e) {
				Logger.getLogger(this.getClass()).warn("MenuItem Class could not be found for name=\""
							+ menuItem.getMenuItemName() + "\" and class="+menuItem.getImplClazz());
			}
		}
		this.menuItems = newItems;
		add(createMenuItemList("menuitems"));
	}

	/**
	 * Create the menu list
	 * 
	 * @param id
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected ListView createMenuItemList(String id) {	
		List<MenuItem> menuItems = getMenuItemConfig();
		if (menuItems != null) {
			for (MenuItem item : menuItems) {
				if (item.getDto()==null) {
					continue;
				}
				Class pageLink = null;
				try {
					pageLink = Class.forName(item.getDto().getImplClazz());
					if (pageLink.equals(pageClass)) {
						selectedGroup = item.getParentID();
						selectedItem = item.getId();
						if (logger.isDebugEnabled()) logger.debug("SELECTED GROUP IS " + selectedGroup + "  item=" + item.getId() + " - " + item.getName());
					}
				} catch (ClassNotFoundException e) {
					continue;
				}
			}
		}
		
		if (logger.isDebugEnabled()) {
			int count = 0;
			if (menuItems != null) {
			for (MenuItem m : menuItems) {
				count++;
				logger.debug(count + " - id=" + m.getId() + " -name=" + m.getName() + " - parent=" + m.getParentID());
			}
			}
			
		}
		
		ListView items = new ListView(id, menuItems) {
			private static final long serialVersionUID = 1L;

			protected void populateItem(ListItem item) {				
							
				addLink(item);
			}
	
		};
		
		return items;
	}
	
	/**
	 * Add a menu item link.
	 * 
	 * @param item
	 */
	@SuppressWarnings("unchecked")
	private void addLink(ListItem item) {
		final MenuItem menuItem = (MenuItem) item.getModelObject();	
		final WebMarkupContainer link;
		final WebComponent hr = new WebComponent("hr");		
		final Component arrowImg = new WebMarkupContainer("arrowLocation");
		final int linkTabIndex = (menuItem.getPanelConfig() != null) ? menuItem.getPanelConfig().getTabIndex() : -1;		
	
		/**
		 * Group menu items
		 */
		if(menuItem.isGroup()){
			if (logger.isDebugEnabled()) logger.debug("Menu : Adding group " + menuItem.getName() + " id=" + menuItem.getId() + "  parentId=" + menuItem.getParentID() 
					+ "  isSelectedGroup=" + selectedGroup.equalsIgnoreCase(menuItem.getId()));
			
			final boolean isSelected = selectedGroup.equalsIgnoreCase(menuItem.getId());
			
//			if main group menu then we add wicket ajax callback to get sub menus
			link = new WebMarkupContainer("menuitem") {
				private static final long serialVersionUID = 1L;

				@Override
				protected void onComponentTag(ComponentTag tag) {
					super.onComponentTag(tag);						
					tag.put("href", "javascript: return false;");
				}
			};
			link.add(new AjaxEventBehavior("click"){
				private static final long serialVersionUID = 1L;
				
				boolean firstClick = isSelected; 
				// boolean firstClick = false;
//				isSelected = !isSelected;
				List<MenuItem> items = SRSAuthWebSession.get().getMenuItemConfigs().get(menuItem.getId());
				
				@Override
				protected void onEvent(AjaxRequestTarget target) {					
					firstClick = !firstClick;	
					//we use firstClick to determin whether list must be expanded or not
					//if firstclick is true expand list
					
					//add arrows
					if(firstClick){
						arrowImg.add(new AttributeModifier("src","/SRSAppWeb/images/arrow_down.gif"));
						arrowImg.add(new AttributeModifier("style",""));
					}
					else{
						arrowImg.add(new AttributeModifier("src","/SRSAppWeb/images/arrow_side.gif"));
						arrowImg.add(new AttributeModifier("style",""));
					}
					target.add(arrowImg);					
					//make the links visible	
					if(items != null){						
						for(MenuItem item : items){							
							WebMarkupContainer sublink = menuLinks.get(item.getId());							
							if(firstClick){
								//expand list
								//set vistible true								
							 sublink.setVisible(true);
							 sublink.getParent().setVisible(true);								 
							}else{
								//contract list
								//set visible false
							 sublink.setVisible(false);	
							 sublink.getParent().setVisible(false);						 
							}							
							target.add(sublink);							
							target.add(sublink.getParent());
						}
					}					
				}			
			});
			Label title = new Label("title", menuItem.getName());
			link.add(title);	
			arrowImg.add(new AttributeModifier("src",isSelected ? "/SRSAppWeb/images/arrow_down.gif" : "/SRSAppWeb/images/arrow_side.gif"));
			arrowImg.add(new AttributeModifier("style",""));
		}else{
			/* Get the class of underlying page */
		
			final boolean isSelected = selectedGroup.equalsIgnoreCase(menuItem.getParentID());
			if (logger.isDebugEnabled()) logger.debug("  -- Menu : Adding child " + menuItem.getName() + "  parentId=" + menuItem.getParentID() + " - isSelected=" +isSelected);
			Class pageLink = null;
			try {
				pageLink = Class.forName(menuItem.getDto().getImplClazz());
			} catch (ClassNotFoundException e) {
				error("MenuItem Class could not be found = "
						+ menuItem.getDto().getMenuItemName());
				return;
			}
			final Class otherPageLink = pageLink;
			
			
			/* Add link (can't use Wicket link as behavior logic 
			 * does not work correctly with it) 
			 */
			link = new WebMarkupContainer("menuitem") {
				private static final long serialVersionUID = 1L;
	
				@Override
				protected void onComponentTag(ComponentTag tag) {
					super.onComponentTag(tag);
					tag.put("href", "#" + otherPageLink.getSimpleName());				
				}			
			};
			boolean addspacer = false;
			String description = menuItem.getDto().getMenuItemDescription();
			
			if(menuItem.getParentID() != null && !menuItem.getParentID().equalsIgnoreCase(FIRST_LEVEL_MENU_ITEMS)){					
				//child or sub menu			
				//if sub menu set visible to false and change the colour
				if(menuItem.getPanelConfig() == null){
					description += " -";
					String style = "LMChild";
					if (menuItem.getId() != null && selectedItem != null && selectedItem.equalsIgnoreCase(menuItem.getId())) {
						style += " LMChildSelect";
					}
					link.add(new AttributeModifier("class",style));
				}else{
					link.add(new AttributeModifier("class","LMChild LMChildTab"));					
				}
				hr.setVisible(false);
			}else{
				addspacer = true;				
				//spacer.add(new SimpleAttributeModifier("class","arrowSpacer"));
			}
			
			//end if		
			Label title = new Label("title", description);
			if(addspacer){
				title.add(new AttributeModifier("style","padding-right: 12px;"));
			}
			link.add(title);			
			/* Only enable menu when viewing */
			link.add(new AjaxEventBehavior("click") {
				private static final long serialVersionUID = 1L;

				@Override
				protected void onEvent(AjaxRequestTarget target) {
				
					if (statefullComponent.getEditState()!=EditStateType.VIEW) {
						// Show a warning dialog and cancel the action
						target.appendJavaScript(DialogScriptBuilder.buildShowDialog(DialogType.WARNING, 
								"Please save or cancel the current action before using the menu links."));
						target.appendJavaScript(DialogScriptBuilder.buildReturnValue(false));
					} else {
						boolean loadPage = false;
						Page page = null;
						FeedbackMessages messages = null;
						try {
							page = (Page) otherPageLink.newInstance();							
							messages = getSession().getFeedbackMessages();								
						} catch (Exception e) {
							logger.error("Could not get page model to set tab for page " +otherPageLink,e);
							if (e instanceof ConcurrentSessionForUserRuntimeException) {
								throw (ConcurrentSessionForUserRuntimeException)e;
							}
						}	
						//set selected panel using page
						//boolean tabbed = false;
						if(page != null && page instanceof MaintenanceBasePage){
							if(linkTabIndex != -1){
								MaintenanceBasePage newPage = (MaintenanceBasePage) page;
								if(newPage.containerPanel instanceof MaintenanceTabbedPanel){
									((MaintenanceTabbedPanel)newPage.containerPanel).setSelectedTab(linkTabIndex);
									//tabbed = true;
									//loadPage = true;
								}
								else if(newPage.containerPanel instanceof TabbedPanel){
									((TabbedPanel)newPage.containerPanel).setSelectedTab(linkTabIndex);
									//tabbed = true;
									//loadPage = true;
								}							
								Object pageModel = newPage.getPageModel();
								if(pageModel != null && pageModel instanceof ITabbedPageModel){								
									((ITabbedPageModel)pageModel).setCurrentTab(linkTabIndex);
									//tabbed = true;
									//loadPage = true;
								}	
							}
							loadPage = true;
						}
						if(page != null && loadPage){
							setResponsePage(page);
							//do below cause the errors disappear on this action
							if(messages != null){								
								Iterator it  = messages.iterator();
								while(it.hasNext()){									
									getSession().error((String)((FeedbackMessage)it.next()).getMessage());
								}
							}
						}else{							
							setResponsePage(otherPageLink);
						}
					}
				}			
			});	
		}
		if(menuItem.getParentID() != null && !menuItem.getParentID().equalsIgnoreCase(FIRST_LEVEL_MENU_ITEMS)){
			link.setVisible(selectedGroup.equalsIgnoreCase(menuItem.getParentID()));			
		}			
		arrowImg.setOutputMarkupId(true);
		arrowImg.setOutputMarkupPlaceholderTag(true);
		link.setOutputMarkupPlaceholderTag(true);
		link.add(arrowImg);
		link.setOutputMarkupId(true);
		item.setOutputMarkupPlaceholderTag(true);
		item.setVisible(link.isVisible());
		item.add(link);			
		item.add(hr);		
		menuLinks.put(menuItem.getId(), link);
	}	
		
	/**
	 * Work through the xml configured menu items and the user configured menu items as well as the page panels to errect a complete menu item list for the logged on user 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private List<MenuItem> getMenuItemConfig(){	
		
		if(SRSAuthWebSession.get().isAuthenticated() && SRSAuthWebSession.get().getUsersActualMenus() == null){	
			
			if (logger.isDebugEnabled()) logger.debug("Menu items not in session - retrieve");
			
			List<MenuItem> allMenuItemConfigs = new ArrayList<MenuItem>();
			HashMap<String, List<MenuItem>> menuItemConfigs = SRSAuthWebSession.get().getMenuItemConfigs();
			//will get the document
			//will go through and call each page to get panels
			//once panels retreived, will store to variable so that all future clicks are quick
			//all sub menu items and panels are configured so javascript can alter menu items			
			Document menuItemStructureDocument = ((SRSApplication)getApplication()).getMenuItemDocument();
			if(menuItemStructureDocument != null){
				//TODO Dean--> replace with objects from jaxb
				//final Document doc = getMenuItemConfig();
				Element root = menuItemStructureDocument.getRootElement();
				if(menuItems != null && menuItems.size() != 0 && menuItemStructureDocument != null){
					HashMap<String,MenuItemDTO> menuItemsMap = new HashMap<String,MenuItemDTO>(menuItems.size());			
					for(MenuItemDTO dto : menuItems){
						//ignore user configured panels as all panels can be seen
						if(!dto.isPanel()){
							menuItemsMap.put(dto.getMenuItemName(), dto);						
						}
					}			
					List<Element> firstLevelItems = root.getChildren();	
					
					if (logger.isDebugEnabled()) {
						for (Element e : firstLevelItems) {
							logger.debug("XML - First level " + e.getName()
									+ "  - ID - " + e.getAttributeValue("ID"));
						}
					}
					
					addMenuItemsForSubList(firstLevelItems,menuItemConfigs,menuItemsMap,FIRST_LEVEL_MENU_ITEMS);
					//now we add all extras from map to first list
					for(String key : menuItemsMap.keySet()){
						MenuItemDTO dto = menuItemsMap.get(key);
						menuItemConfigs.get(FIRST_LEVEL_MENU_ITEMS).add(new MenuItem(dto,FIRST_LEVEL_MENU_ITEMS,null, null));
					}
				}	
				
				if (logger.isDebugEnabled()) {
					StringBuilder builder = new StringBuilder();
//					for 
				}
				
				
				
				Map<String, String> usedKeys = new HashMap<String, String>();
				
				
				for(String key : new TreeSet<String>(menuItemConfigs.keySet()) ){	
					if (logger.isDebugEnabled())
						logger.debug("KEY SET : " + key
								+ "  size=" + menuItemConfigs.get(key).size());
					
					if(usedKeys.get(key) == null){
						//allMenuItemConfigs.addAll(menuItemConfigs.get(key));
						for(MenuItem item : menuItemConfigs.get(key)){
							if (logger.isDebugEnabled())
								logger.debug("   -- Child Item being added " + key
										+ "  id=" + item.getId());
							allMenuItemConfigs.add(item);	
							//now add sub list
							if(menuItemConfigs.get(item.getId()) != null){
								if (logger.isDebugEnabled())
									logger.debug("      -- GranChild Item being added " + key
											+ "  size=" + menuItemConfigs.get(item.getId()).size());
								usedKeys.put(item.getId(), item.getId());
								allMenuItemConfigs.addAll(menuItemConfigs.get(item.getId()));	
							}				
						}
					}
					usedKeys.put(key, key);
				}	
				
				
				// Jean stuff - Hope to fix this silly menu
				
			}			
			SRSAuthWebSession.get().setUsersActualMenus(allMenuItemConfigs);
		}				
		return SRSAuthWebSession.get().getUsersActualMenus();
	}
	
	/**
	 * Will return a list of MenuItems using the configuration from the xml
	 * @param items
	 * @return true if the child class is the selected menu item
	 */
	@SuppressWarnings("unchecked")
	private boolean addMenuItemsForSubList(List<Element> items, Map<String, List<MenuItem>> menuItemConfigs,Map<String,MenuItemDTO> menuItemsMap,String currentGroup){		
		ArrayList<MenuItem> level = new ArrayList<MenuItem>();
		boolean response = false;
		if(items != null && items.size() != 0){
			for(Element item : items){
				if(item.getName().equalsIgnoreCase("Group")){
					if (logger.isDebugEnabled()) logger.debug("  -- Adding group " + item.getAttributeValue("ID"));
					//group item	
					addMenuItemsForSubList(item.getChildren(),menuItemConfigs,menuItemsMap,item.getAttributeValue("ID"));	
					
					if(menuItemConfigs.get(item.getAttributeValue("ID")) != null && menuItemConfigs.get(item.getAttributeValue("ID")).size() != 0){
						level.add(new MenuItem(item.getAttributeValue("name"),item.getAttributeValue("ID")));
					}
				}else if(item.getName().equalsIgnoreCase("MenuItem")){
					if (logger.isDebugEnabled()) logger.debug("  -- Adding menuItem " + item.getAttributeValue("ID"));
					//normal item
					MenuItemDTO menuItemDTO = menuItemsMap.get(item.getAttributeValue("ID"));
					if(menuItemDTO != null){
						menuItemsMap.remove(item.getAttributeValue("ID"));

						level.add(new MenuItem(menuItemDTO,currentGroup,item.getAttributeValue("ID"),null));

						if (menuItemDTO == selectedMenuItem) {
							response = true;
						}
					}
				}
			}
		}
		menuItemConfigs.put(currentGroup, level);
		return response;
	}
	

	/**
	 * Interal menu item object
	 *
	 */
	public class MenuItem implements Serializable, Comparable<MenuItem>{	
		
		private static final long serialVersionUID = 1L;

		private MenuItem(String groupName, String groupid){
			this.group = true;
			this.name = groupName;
			id = groupid;
		}
		
		private MenuItem(MenuItemDTO dto,String parentID,String id, MenuItemPanelConfig panelConfig){
			this.dto = dto;
			group = false;
			name = dto.getMenuItemDescription();
			this.parentID = parentID;			
			this.id = id;		
			this.panelConfig = panelConfig;
		}
		
		private boolean group;
		private String name; 			
		private MenuItemDTO dto;
		private String parentID;
		private String id;		
		private MenuItemPanelConfig panelConfig;

		public String getId() {
			return id;
		}

		public boolean isGroup() {
			return group;
		}	
		
		public String getName() {
			return name;
		}

		public MenuItemDTO getDto() {
			return dto;
		}

		public String getParentID() {
			return parentID;
		}

		public MenuItemPanelConfig getPanelConfig() {
			return panelConfig;
		}

		@Override
		public int compareTo(MenuItem o) {
			return id.compareTo(o.id);
		}
		
	}
	
	/**
	 * Holds menu item panel information
	 * @author DZS2610
	 *
	 */
	private class MenuItemPanelConfig implements Serializable{
		
		private static final long serialVersionUID = 1L;

		MenuItemPanelConfig(int tabIndex){
			this.tabIndex = tabIndex;			
		}
		
		private int tabIndex;			

		public int getTabIndex() {
			return tabIndex;
		}
		
	}
}