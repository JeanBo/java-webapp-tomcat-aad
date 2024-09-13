package za.co.liberty.web.pages.admin;

import java.util.Date;
import java.util.List;

import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.apache.wicket.ajax.AbstractAjaxTimerBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.util.time.Duration;

import za.co.liberty.business.security.ITimedRoleUpdate;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.helpers.lookup.ServiceLocator;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.models.PagePanelInfoObject;
import za.co.liberty.web.pages.MaintenanceBasePage;
import za.co.liberty.web.pages.admin.models.RoleUpdateSchedulerModel;
import za.co.liberty.web.system.EJBReferences;
import za.co.liberty.web.system.SRSAuthWebSession;

/**
 * Manages the RoleUpdate scheduler. 
 * 
 * @author JZB0608 - 02 Jun 2008
 *
 */
public class RoleUpdateScheduler extends MaintenanceBasePage<Object>{
	/* Constants*/
	private static final long serialVersionUID = 705031615951918978L;
	private static final long WAIT_TIME = 2000;
	
	/* Attributes */
	protected RoleUpdateSchedulerModel pageModel;
	protected AbstractAjaxTimerBehavior pageTimer;
	
	public RoleUpdateScheduler() {
		super();
		
		add(pageTimer = new AbstractAjaxTimerBehavior(Duration.seconds(pageModel.isActive()?1:5)) {
			private static final long serialVersionUID = 1L;

			/**
			 * @see AbstractAjaxTimerBehavior#onTimer(AjaxRequestTarget)
			 */
			protected void onTimer(AjaxRequestTarget target) {
				do_onTimer(target);

			}

		});

	}
	
	/**
	 * 
	 * @param target
	 */
	protected void do_onTimer(AjaxRequestTarget target) {
		if ((selectionPanel instanceof RoleUpdateSchedulerSelectionPanel) == false 
				|| getEditState() != EditStateType.VIEW) {
			// Do not update
			return;
		}
		
		/* Update the status description */
		String status = getSessionBean().checkServiceStatus();
		if (status!=null && pageModel.isActive()) {
			// Still active
			pageModel.setStatusDescription(status);
			((RoleUpdateSchedulerSelectionPanel)selectionPanel).do_onTimer(target);
		} else if (status==null && pageModel.isActive()) {
			// Not active anymore
			pageModel.setActive(false);
			pageModel.setStatusDescription("Not Active");
			((RoleUpdateSchedulerSelectionPanel)selectionPanel).do_onTimer(target);
		}
			
		
	}
	
	@Override
	public Object initialisePageModel(Object obj, Object extraValue) {
		pageModel = new RoleUpdateSchedulerModel();		
		String status = getSessionBean().checkServiceStatus();
		pageModel.setActive(status!=null);
		pageModel.setStatusDescription(status!=null?status:"Not Active");
		return pageModel;
	}

	@Override
	public Panel createContainerPanel() {
		if (getEditState() != EditStateType.VIEW) {
			return (Panel) new RoleUpdateSchedulerPanel(CONTAINER_PANEL_NAME,pageModel)
				.setOutputMarkupId(true);
		} else {
			return (Panel) new EmptyPanel(CONTAINER_PANEL_NAME).setOutputMarkupId(true);
		}
	}

	@Override
	public Button[] createNavigationalButtons() {
		return new Button[] {createSaveButton("button1"), 
				createCancelButton("button2") };
	}

	@Override
	public Panel createSelectionPanel() {
		return new RoleUpdateSchedulerSelectionPanel(SELECTION_PANEL_NAME,pageModel,
				this, selectionForm);
	}

	@Override
	public String getPageName() {
		return "Role Update Scheduler";
	}
	
	@Override
	protected Panel getContextPanel() {
		/* Does not require a panel */
		return new EmptyPanel(CONTEXT_PANEL_NAME);
	}
	
	/**
	 * Get an instance of the managed session bean
	 * 
	 * @return
	 */
	private ITimedRoleUpdate getSessionBean() {
		try {
			return ServiceLocator.lookupService(ITimedRoleUpdate.class);
		} catch (CommunicationException e) {
			Logger.getLogger(this.getClass()).error(
					"Unable to initialise session bean for page", e);
			this.error(e.getMessage());
			throw e;
		} catch (NamingException e) {
			throw new CommunicationException(e);
		}
	}

	@SuppressWarnings("static-access")
	@Override
	public void doSave_onSubmit() {
		ITimedRoleUpdate bean =getSessionBean();
		Date startDate = pageModel.getStartDate();
		Date minimumStart = new Date(System.currentTimeMillis()+30000);
		if (startDate.getTime()<minimumStart.getTime()) {
			startDate = minimumStart;
		}
		
		if (pageModel.isRepeating()) {
			bean.startService(startDate,pageModel.getInterval(), 
					pageModel.getName());
		} else {
			bean.startService(startDate, pageModel.getName());
		}
		/* Wait for a few seconds */
		try {
			Thread.currentThread().sleep(WAIT_TIME);
		} catch (InterruptedException e) {
			logger.error("Scheduler thread was interrupted",e);
		}
		RoleUpdateScheduler page = new RoleUpdateScheduler();
		page.info("Service was started successfully");
		setResponsePage(page);
		
	}
	
	/**
	 * Stop the timer
	 *
	 */
	@SuppressWarnings("static-access")
	public void stopTimer() {
		getSessionBean().stopService();
		/* Wait for a few seconds */
		try {
			Thread.currentThread().sleep(WAIT_TIME);
		} catch (InterruptedException e) {
			
		}
		setResponsePage(RoleUpdateScheduler.class);
		info("Service was stopped successfully");
	}

	@Override
	public List<PagePanelInfoObject> getPagePanelsInfo() {	
		return null;
	}
}
