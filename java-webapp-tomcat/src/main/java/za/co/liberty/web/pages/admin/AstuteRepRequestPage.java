package za.co.liberty.web.pages.admin;
import javax.naming.NamingException;

import org.apache.wicket.ajax.AbstractAjaxTimerBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
//import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.util.time.Duration;

import za.co.liberty.business.broadcast.IAstuteIntegrationController;
import za.co.liberty.business.broadcast.IScheduledCommunicationTimerService;
import za.co.liberty.business.broadcast.AstuteIntegrationController.DeltasRequestType;
import za.co.liberty.exceptions.broadcast.BroadcastException;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.helpers.lookup.ServiceLocator;
import za.co.liberty.web.pages.BasePage;

/**
 * GUI to request the astute deltas
 * 
 * @author DZS2610
 *
 */
public class AstuteRepRequestPage extends BasePage{
	private static final long serialVersionUID = 1L;	
	private transient IAstuteIntegrationController astuteIntegrationController;	
	private Button allButton;
	private Button updateButton;		
	
	public AstuteRepRequestPage(){		
		add(allButton = createUpdateButton("all",DeltasRequestType.ALL));
		add(updateButton = createUpdateButton("updates",DeltasRequestType.LATEST));		
	}
	
	/**
	 * Create start button
	 * @param id
	 * @return
	 */
	private Button createUpdateButton(final String id, final DeltasRequestType type ){
		Button button = new Button(id);	
		button.add(new AjaxFormComponentUpdatingBehavior("click"){			
			private static final long serialVersionUID = 1L;
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				//submit the update				
				
				try {
					getAstuteIntegrationController().requestAstuteDeltas(type);
					info("Broadcast successfully sent with type " + type.getAstuteName());
				} catch (BroadcastException e) {					
					error("Broadcast unsuccessfull, msg :" + e.getMessage());
				}				
				target.add(getFeedbackPanel());
			}			
		});	
		button.setOutputMarkupId(true);
		button.add(new AttributeModifier("value",id));
		if(!hasModifyAccess()){
			button.setEnabled(false);
		}
		return button;
	}	

	@Override
	public String getPageName() {		
		return "Astute Update Request Page";
	}
	
	/**
	 * Get the AstuteIntegration controller bean
	 * @return
	 */
	private IAstuteIntegrationController getAstuteIntegrationController(){
		if(astuteIntegrationController == null){
			try {
				astuteIntegrationController = ServiceLocator.lookupService(IAstuteIntegrationController.class);
			} catch (NamingException e) {
				throw new CommunicationException(e);
			}
		}
		return astuteIntegrationController;
	}
	
}
