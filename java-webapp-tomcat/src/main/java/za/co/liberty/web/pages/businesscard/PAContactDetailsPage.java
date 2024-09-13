package za.co.liberty.web.pages.businesscard;

import javax.naming.NamingException;

import org.apache.commons.lang.SerializationUtils;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxCallListener;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.ajax.markup.html.form.AjaxFallbackButton;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.Panel;

import za.co.liberty.business.guicontrollers.businesscard.IBusinessCardGuiController;
import za.co.liberty.business.guicontrollers.partymaintenance.IPartyMaintenanceController;
import za.co.liberty.dto.contracting.ResultAgreementDTO;
import za.co.liberty.dto.contracting.ResultPartyDTO;
import za.co.liberty.dto.party.PartyDTO;
import za.co.liberty.dto.userprofiles.ISessionUserProfile;
import za.co.liberty.exceptions.data.DataNotFoundException;
import za.co.liberty.exceptions.data.ValidationException;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.helpers.lookup.ServiceLocator;
import za.co.liberty.interfaces.gui.IPopupResponseComponent;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.data.enums.PanelToRequestMapping;
import za.co.liberty.web.pages.BaseWindowPage;
import za.co.liberty.web.pages.contactdetail.ContactDetailsPanel;
import za.co.liberty.web.pages.party.model.MaintainPartyPageModel;
import za.co.liberty.web.system.SRSAuthWebSession;

/**
 * Page that will allow ajustment of the Personal assistants contact details
 * @author DZS2610
 *
 */
public class PAContactDetailsPage extends BaseWindowPage {

	private static final long serialVersionUID = 1L;
	private boolean initialised;	
	
	private transient IPartyMaintenanceController partyMaintenanceController;
	
	private transient IBusinessCardGuiController businessCardGuiController;
	
	private Long partyID;	
	
	private MaintainPartyPageModel pageModel;
	
	private ModalWindow popupWindow;
	
	private IPopupResponseComponent popupResponseComponent;
	
	private Button cancelButton;
	
	public PAContactDetailsPage(Long partyID, ModalWindow popupWindow, IPopupResponseComponent popupResponseComponent){
		this.partyID = partyID;		
		this.popupWindow = popupWindow;
		this.popupResponseComponent = popupResponseComponent;
	}
	
	

	@Override
	protected void onBeforeRender() {
		if(!initialised){
			add(new PAForm("contactDetailsForm"));
		}		
		super.onBeforeRender();
	}
	/**
	 * Form used for the panel so we can add validations and on submit method calls
	 * @author DZS2610
	 *
	 */
	public class PAForm extends Form {
		private static final long serialVersionUID = 1L;
		public PAForm(String id) {
			super(id);	
			//add save button
			add(createSaveButton("saveButton",this));
//			add cancel button
			add(cancelButton = createCancelButton("cancelButton",this));
			//add PA panel
			add(createContactDetailsPanel("paContactDetailsPanel"));
						
		}
	}
	
	/**
	 * Create the Cancel button
	 * 
	 * @param id
	 * @return
	 */
	protected Button createCancelButton(String id, Form enclosingForm) {		
		final Button button = new AjaxFallbackButton(id, enclosingForm) {
			private static final long serialVersionUID = -5330766713711809772L;			

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form form) {
				popupWindow.close(target);
			}

			@Override
			protected void onError(AjaxRequestTarget target, Form form) {
//				still close as we dont care about errors if the user cancels
				popupWindow.close(target);
			}	
		};		
		button.setOutputMarkupId(true);
		button.setOutputMarkupPlaceholderTag(true);
		return button;
	}
	
	/**
	 * Creates the contact details panel that allows PA contact details adjustment
	 * @param id
	 * @return
	 */
	private Panel createContactDetailsPanel(String id){
		pageModel = new MaintainPartyPageModel();
		Panel panel = new EmptyPanel(id);
		PartyDTO party;
		try {
			party = getPartyMaintenanceController().getPartyDTO(partyID);
			pageModel.setPartyBeforeImage((PartyDTO) SerializationUtils.clone(party));
			pageModel.setPartyDTO(party);
			//TODO: UW Comms - Check Comm Pref list
			panel = new ContactDetailsPanel(id,((pageModel.getPartyDTO().getContactPreferences() != null 
					&& pageModel.getPartyDTO().getContactPreferences().getContactPreferences() != null) ?
							pageModel.getPartyDTO().getContactPreferences().getContactPreferences() : null), pageModel.getPartyDTO().getCommunicationPreferences(),
					EditStateType.MODIFY,getFeedBackPanel(),false,false,this, true);
		} catch (DataNotFoundException e) {			
			error("Could not find the party data for id " + partyID);
		}
		return panel;
	}
	
	/**
	 * Create the Save button
	 * 
	 * @param id
	 * @return
	 */
	protected Button createSaveButton(String id, Form enclosingForm) {		
		final Button button = new AjaxFallbackButton(id, enclosingForm) {
			private static final long serialVersionUID = -5330766713711809772L;

			@Override
			protected void onComponentTag(ComponentTag tag) {
				super.onComponentTag(tag);
				tag.getAttributes().put("value", "Save");
				tag.getAttributes().put("type", "button");				
			}
			
//			@Override
//			protected IAjaxCallDecorator getAjaxCallDecorator() {
//				return new AjaxCallDecorator() {
//					private static final long serialVersionUID = 1L;
//
//					public CharSequence decorateScript(CharSequence script) {
//						String disableCancel = "";
//						if(cancelButton != null){
//							disableCancel = "getElementById('"+cancelButton.getMarkupId()+"').disabled=true;";
//						}
//						return "this.disabled=true;"+disableCancel+"overlay(true);" + script;
//					}
//				};
//			}
			
			@Override
			protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
				super.updateAjaxAttributes(attributes);

			        // Way of adding any handler
				attributes.getAjaxCallListeners().add(new AjaxCallListener() {

				  @Override
				  public CharSequence getInitHandler(Component component) {
					CharSequence s =   super.getInitHandler(component);
					
					// disable cancel too
					String disableCancel = "";
					if (cancelButton != null) {
						disableCancel = "document.getElementById('"
								+ cancelButton.getMarkupId()
								+ "').disabled=true;";
					}
					return "overlay(true);" + disableCancel + ((s==null)?"":s);
				  }
							
				  @Override
				  public CharSequence getDoneHandler(Component component) {	
					CharSequence s =  super.getDoneHandler(component);
					return  "hideOverlay();" + ((s==null)?"":s);
				  }
				});
			}

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form form) {
//				raise the pcontact details request
				ISessionUserProfile userProfile = SRSAuthWebSession.get().getSessionUser();
				try {
					Long agmtNumber = null;		
					try{
						ResultAgreementDTO agmt = getBusinessCardGuiController().findBestAgreementForParty(partyID);
						agmtNumber = agmt.getAgreementNumber();
					}catch(DataNotFoundException e){
						//no prob as this would mean the party does not have any and this request does not require one
					}
					// Validate Communication Preference Panel 
					getBusinessCardGuiController().validateCommunicationPreferences(pageModel.getPartyDTO().getCommunicationPreferences());
					getPartyMaintenanceController().storeParty(pageModel.getPartyDTO(),agmtNumber,userProfile,
							pageModel.getPartyBeforeImage(),
							PanelToRequestMapping.BUSINESSCARD_PA_CONTACT_DETAILS.getRequestKindTypes(),
							agmtNumber != null,null);	
					popupResponseComponent.setSuccessful(true);
				} catch (ValidationException e) {
					//will only do below if save not successfull
					for(String error : e.getErrorMessages()){
						error(error);
					}
					target.add(getFeedBackPanel());	
					target.add(this);	
					return;
				}	
				popupWindow.close(target);
			}

			@Override
			protected void onError(AjaxRequestTarget target, Form form) {
				//validation error occured
				if(getFeedBackPanel() != null){
					target.add(getFeedBackPanel());
				}					
				target.add(this);
			}	
		};		
		button.setOutputMarkupId(true);
		button.setOutputMarkupPlaceholderTag(true);
		return button;
	}


	@Override
	public String getPageName() {
		String partyName = "";
		ResultPartyDTO party = null;
		try {
			party = getBusinessCardGuiController().findParty(partyID);
			partyName = " for " + party.getName(); 
		}  catch (DataNotFoundException e) {
			//do nothing, we wont display the name
		}
		return "Contact Details" + partyName;
	}
	
	/**
	 * gets the PartyMaintenanceController bean
	 * @return
	 */
	private IPartyMaintenanceController getPartyMaintenanceController(){
		if(partyMaintenanceController == null){
			try {
				partyMaintenanceController = ServiceLocator.lookupService(IPartyMaintenanceController.class);
			} catch (NamingException e) {
				throw new CommunicationException(e);
			}
		}
		return partyMaintenanceController;
	}
	
	/**
	 * gets the PartyMaintenanceController bean
	 * @return
	 */
	private IBusinessCardGuiController getBusinessCardGuiController(){
		if(businessCardGuiController == null){
			try {
				businessCardGuiController = ServiceLocator.lookupService(IBusinessCardGuiController.class);
			} catch (NamingException e) {
				throw new CommunicationException(e);
			}
		}
		return businessCardGuiController;
	}
}
