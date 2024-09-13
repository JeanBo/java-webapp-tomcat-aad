package za.co.liberty.web.pages.interfaces;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.panel.Panel;


/**
 * Implements the methods required by the Maintenance Parent contract, 
 * refer to {@link MaintenanceBasePage}
 * 
 * @author JZB0608 - 05 May 2008
 *
 */
public interface IMaintenanceParent extends IChangeableStatefullComponent{

	/**
	 * Swap the container panel with a new instance
	 * @param target
	 */
	public void swapContainerPanel(AjaxRequestTarget target);
	/**
	 * Swap the navigation panel with a new instance
	 * @param target
	 */
	public void swapNavigationPanel(AjaxRequestTarget target);
	
	/**
	 * Update the selection panel with a new instance
	 * 
	 * @param target
	 */
	public void swapSelectionPanel(AjaxRequestTarget target);
	
	/**
	 * get the Selection Panel
	 * @return
	 */
	public Panel getSelectionPanel();

	/**
	 * get the container panel
	 * @return
	 */
	public Panel getContainerPanel();

	/**
	 * get the navigation panel
	 * @return
	 */
	public Panel getNavigationPanel();
	
	/**
	 * True if this page has modify access
	 * @return
	 */
	public boolean hasModifyAccess();
	/**
	 * True if this page has add access
	 * @return
	 */
	public boolean hasAddAccess();
	/**
	 * True if this page has delete access
	 * @return
	 */
	public boolean hasDeleteAccess();
	
	public void doSave_onSubmit();
	
	public void invalidatePage();
	
}
