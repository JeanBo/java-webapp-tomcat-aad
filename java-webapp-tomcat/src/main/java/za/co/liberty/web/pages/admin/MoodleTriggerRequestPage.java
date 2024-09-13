package za.co.liberty.web.pages.admin;
import java.util.Date;

import javax.naming.NamingException;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.Model;

import za.co.liberty.business.broadcast.IFitAndProperIntegrationController;
import za.co.liberty.dto.broadcast.FitAndProperBroadcastResponseDTO;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.helpers.lookup.ServiceLocator;
import za.co.liberty.web.pages.BasePage;
import za.co.liberty.web.system.SRSAuthWebSession;

/**
 * GUI to request a replication of a moodle trigger
 * 
 * @author DZS2610
 *
 */
public class MoodleTriggerRequestPage extends BasePage{
	private static final long serialVersionUID = 1L;	
	private transient IFitAndProperIntegrationController fitAndProperIntegrationController;	
	private TextField<Long> partyIDtextField;	
	
	Model<Long> partyIDmodel = new Model<Long>();
	
	public MoodleTriggerRequestPage(){			
		add(partyIDtextField = createPartyIDtextField("partyID"));
		add(createUpdateButton("updates"));		
		add(createBBpBroadcastButton("bbpUpdates"));
		add(createAllBroadcastButton("allUpdates"));
	}
	
	/**
	 * Create start button
	 * @param id
	 * @return
	 */
	private TextField<Long> createPartyIDtextField(final String id){		
		TextField<Long> textfield = new TextField<Long>(id,partyIDmodel);
		textfield.setType(Long.class);
		textfield.add(new AjaxFormComponentUpdatingBehavior("keyup"){			
			private static final long serialVersionUID = 1L;
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				//just update model
			}			
		});	
		return textfield;
	}	
	
	/**
	 * Create update button for one party
	 * @param id
	 * @return
	 */
	private Button createUpdateButton(final String id){
		Button button = new Button(id);	
		button.add(new AjaxFormComponentUpdatingBehavior("click"){			
			private static final long serialVersionUID = 1L;
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				//submit the update				
				try {					
					FitAndProperBroadcastResponseDTO response = getFitAndProperIntegrationController().broadcastFitAndProperData(
							partyIDmodel.getObject(), SRSAuthWebSession.get().getSessionUser().getPartyOid());
					if (response.isSuccess()) {
						info("Data successfully updated for party " + partyIDmodel.getObject());
					} else {
						warn("Warning issued: " + ((response.getMessage()==null) ? " Check Logs " : response.getMessage()));
					}
				} catch (Exception e) {					
					error("Data not successfully updated for party " + partyIDmodel.getObject() + " msg : " + e.getMessage());
				}				
				target.add(getFeedbackPanel());
			}			
		});	
		button.setOutputMarkupId(true);
		//button.add(new SimpleAttributeModifier("value",id));
		if(!hasModifyAccess()){
			button.setEnabled(false);
		}
		return button;
	}
	
	/**
	 * Create BBP broadcast button
	 * @param id
	 * @return
	 */
	private Button createBBpBroadcastButton(final String id){
		Button button = new Button(id);	
		button.add(new AjaxFormComponentUpdatingBehavior("click"){			
			private static final long serialVersionUID = 1L;
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				//submit the update				
				try {			
					//Schedule now
					getFitAndProperIntegrationController().scheduleAllBBPUsersBroadcasts(SRSAuthWebSession.get().getSessionUser().getPartyOid(), new Date());
					info("All BBP broadcasts successfully scheduled. They will be processed with the next run of the broadcast scheduler");
				} catch (Exception e) {					
					error("All BBP broadcasts could not be scheduled ue to error msg : " + e.getMessage());
				}				
				target.add(getFeedbackPanel());
			}			
		});	
		button.setOutputMarkupId(true);
		//button.add(new SimpleAttributeModifier("value",id));
		if(!hasModifyAccess()){
			button.setEnabled(false);
		}
		return button;
	}
	
	/**
	 * Create all broadcast button
	 * @param id
	 * @return
	 */
	private Button createAllBroadcastButton(final String id){
		Button button = new Button(id);	
		button.add(new AjaxFormComponentUpdatingBehavior("click"){			
			private static final long serialVersionUID = 1L;
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				//submit the update				
				try {			
					//Schedule now
					getFitAndProperIntegrationController().scheduleFPUpdateForStoredParties(SRSAuthWebSession.get().getSessionUser().getPartyOid(), new Date());
					info("All broadcasts successfully scheduled. They will be processed with the next run of the broadcast scheduler");
				} catch (Exception e) {					
					error("All broadcasts could not be scheduled ue to error msg : " + e.getMessage());
				}				
				target.add(getFeedbackPanel());
			}			
		});	
		button.setOutputMarkupId(true);
		//button.add(new SimpleAttributeModifier("value",id));
		if(!hasModifyAccess()){
			button.setEnabled(false);
		}
		return button;
	}

	@Override
	public String getPageName() {		
		return "Fit And Proper Data Update Request Page";
	}
	
	/**
	 * Get the FitAndProper Integration controller bean
	 * @return
	 */
	private IFitAndProperIntegrationController getFitAndProperIntegrationController(){
		if(fitAndProperIntegrationController == null){
			try {
				fitAndProperIntegrationController = ServiceLocator.lookupService(IFitAndProperIntegrationController.class);
			} catch (NamingException e) {
				throw new CommunicationException(e);
			}
		}
		return fitAndProperIntegrationController;
	}
	
}
