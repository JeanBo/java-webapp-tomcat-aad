package za.co.liberty.web.wicket.markup.html.tabs;

import org.apache.log4j.Logger;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import za.co.liberty.web.pages.interfaces.IStatefullComponent;

/**
 * <p>A caching tab that loads a new panel only when the edit
 * state of the parent and child is different.</p>
 * 
 * TODO Jean jzb0608 - Perhaps I should provide a way of forcing
 * a new panel to load???
 * 
 * @author JZB0608 - 14 May 2008
 *
 */
public abstract class StatefullCachingTab extends CachingTab {
	
	/* Constants */
	private static final long serialVersionUID = 2056239411122898203L;
	private static final Logger logger = Logger.getLogger(StatefullCachingTab.class);
	
	/* Attributes */
	protected IStatefullComponent parent;
	
	public StatefullCachingTab(IModel title, IStatefullComponent parent) {
		super(title);
		this.parent = parent;
	}

	@Override
	public Panel getPanel(String id) {
		if (panel == null
				|| ((IStatefullComponent) panel).getEditState() != parent
						.getEditState()) {
			
			panel = (Panel)createPanel(id);
			
			if (logger.isDebugEnabled()) {
				logger.debug("Creating a new panel for tab "
					+ this.getTitle()
					+ " ,panelClass="+((panel==null)?null:panel.getClass().getName()));
			}
			return  panel;
		}
		return (Panel) panel;
	}
	
	/**
	 * Implement this to provide a new instance. This
	 * panel should implement {@link IStatefullComponent} or
	 * a class cast exception will be thrown.
	 * 
	 * @param id
	 * @return
	 */
	public abstract Panel createPanel(String id);	
	
}
