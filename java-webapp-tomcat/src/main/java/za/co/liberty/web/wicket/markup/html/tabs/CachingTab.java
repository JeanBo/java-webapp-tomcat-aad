package za.co.liberty.web.wicket.markup.html.tabs;

import org.apache.log4j.Logger;
import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.extensions.markup.html.tabs.TabbedPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

/**
 * <p>A tab that keeps a reference to a panel so that the
 * panel doesn't have to be instantiated everytime its 
 * selected.</p>
 * 
 * <p>A panel can be loaded lazily, please refer to the
 * constructors for more information.</p>
 * 
 * @author JZB0608 - 14 May 2008
 *
 */
public class CachingTab extends AbstractTab {

	private static final Logger logger = Logger.getLogger(CachingTab.class);
	
	/* Constants */
	private static final long serialVersionUID = 5450879541232043452L;
	
	/**
	 * Required id for a tab panel refer to 
	 * {@linkplain TabbedPanel.#TAB_PANEL_ID}
	 */
	@SuppressWarnings("unused")
	private static final String TAB_PANEL_ID = TabbedPanel.TAB_PANEL_ID;
	
	/* Attributes */
	protected Panel panel;
	
	/**
	 * <p>Using this will load the panel lazily</p>
	 * 
	 * <p>Note - <strong>An exception will be thrown unless 
	 * {@linkplain this#createPanel(String)} is overridden</strong>.</p>
	 * 
	 * @param title Name of the tab
	 */
	public CachingTab(IModel title) {
		this(title, null);
	}

	/**
	 * <p>Use {@linkplain this#CachingTab(IModel)} if you want to 
	 * load the panel lazily</p>
	 * 
	 * <p>When using this contstructor 
	 * {@linkplain this#createPanel(String)} does not have to be 
	 * overridden.</p>
	 * 
	 * @param title
	 * @param panel
	 */
	public CachingTab(IModel title, Panel panel) {
		super(title);
		this.panel = panel;
	}

	@Override
	public Panel getPanel(String id) {
		if (panel == null) {
			panel = createPanel(id);
			if (logger.isDebugEnabled())
				logger.debug("Create panel = "
						+((panel==null)?null:panel.getClass().getName()));
			return panel;
		}
		return (Panel) panel;
	}
	
	/**
	 * Implement this to provide the new instance, returns
	 * null by default.
	 * 
	 * @param id
	 * @return
	 */
	public Panel createPanel(String id) {
		return null;		
	}

}
