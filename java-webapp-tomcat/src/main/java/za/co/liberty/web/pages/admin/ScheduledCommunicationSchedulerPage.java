package za.co.liberty.web.pages.admin;
import javax.naming.NamingException;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AbstractAjaxTimerBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.util.time.Duration;

import za.co.liberty.business.broadcast.IScheduledCommunicationTimerService;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.helpers.lookup.ServiceLocator;
import za.co.liberty.web.pages.BasePage;

/**
 * GUI to start and stop the CommunicationSchedule Timer Service
 * 
 * @author DZS2610
 *
 */
public class ScheduledCommunicationSchedulerPage extends BasePage{
	private static final long serialVersionUID = 1L;	
	private transient IScheduledCommunicationTimerService timerService;	
	private Button controlButton;
	private Label statusLabel;
	private boolean serviceActive;
	
	public ScheduledCommunicationSchedulerPage(){
		IScheduledCommunicationTimerService timerService = getTimerService();		
		add(statusLabel = (Label) new Label("status", timerService.getCurrentStatus()).setOutputMarkupId(true));
		if(!timerService.isCurrentlyRunning()){
			add(controlButton = createStartButton("controlButton"));
		}else{
			add(controlButton = createStopButton("controlButton"));
		}
		add(createUpdateTimer());		
	}
	
	/**
	 * Create page update timer
	 * @return
	 */
	private AbstractAjaxTimerBehavior createUpdateTimer(){
		return new AbstractAjaxTimerBehavior(Duration.seconds(2)) {
			private static final long serialVersionUID = 1L;

			/**
			 * @see AbstractAjaxTimerBehavior#onTimer(AjaxRequestTarget)
			 */
			protected void onTimer(AjaxRequestTarget target) {
				Label label = (Label) new Label("status", getTimerService().getCurrentStatus()).setOutputMarkupId(true);
				statusLabel.replaceWith(label);	
				statusLabel = label;
				target.add(statusLabel);
				boolean active = getTimerService().isCurrentlyRunning();				
				if(serviceActive != active){
					target.add(controlButton);
				}
				serviceActive = active;
			}
		};
	}
	
	/**
	 * Create start button
	 * @param id
	 * @return
	 */
	private Button createStartButton(final String id){
		Button button = new Button(id);	
		button.add(new AjaxFormComponentUpdatingBehavior("click"){			
			private static final long serialVersionUID = 1L;
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				getTimerService().startService();
				Button newButton = createStopButton(id);
				controlButton.replaceWith(newButton);
				controlButton = newButton;
				target.add(statusLabel);
				target.add(controlButton);
			}			
		});	
		button.setOutputMarkupId(true);
		button.add(new AttributeModifier("value","Start"));
		if(!hasModifyAccess()){
			button.setEnabled(false);
		}
		return button;
	}
	
	/**
	 * Create stop button
	 * @param id
	 * @return
	 */
	private Button createStopButton(final String id){
		Button button = new Button(id);	
		button.add(new AjaxFormComponentUpdatingBehavior("click"){			
			private static final long serialVersionUID = 1L;
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				getTimerService().stopService();
				Button newButton = createStartButton(id);
				controlButton.replaceWith(newButton);
				controlButton = newButton;
				target.add(controlButton);	
				target.add(statusLabel);	
			}			
		});		
		button.setOutputMarkupId(true);
		button.add(new AttributeModifier("value","Stop"));
		if(!hasModifyAccess()){
			button.setEnabled(false);
		}
		return button;
	}
	
	

	@Override
	public String getPageName() {		
		return "Scheduled Communication Service Page";
	}
	
	/**
	 * Get the timerService controller bean
	 * @return
	 */
	private IScheduledCommunicationTimerService getTimerService(){
		if(timerService == null){
			try {
				timerService = ServiceLocator.lookupService(IScheduledCommunicationTimerService.class);
			} catch (NamingException e) {
				throw new CommunicationException(e);
			}
		}
		return timerService;
	}
	
}
