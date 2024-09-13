package za.co.liberty.web.pages.businesscard;

import java.util.List;

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
import za.co.liberty.dto.agreementprivileges.AgreementPrivilegesDataDTO;
import za.co.liberty.dto.businesscard.BusinessCardDetailsDTO;
import za.co.liberty.dto.party.PartyDTO;
import za.co.liberty.dto.userprofiles.ISessionUserProfile;
import za.co.liberty.exceptions.data.DataNotFoundException;
import za.co.liberty.exceptions.data.ValidationException;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.helpers.lookup.ServiceLocator;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.data.enums.PanelToRequestMapping;
import za.co.liberty.web.pages.BaseWindowPage;
import za.co.liberty.web.pages.businesscard.model.BusinessCardPageModel;
import za.co.liberty.web.pages.party.model.MaintainPartyPageModel;
import za.co.liberty.web.system.SRSAuthWebSession;

public class AddOtherLinkedParties extends BaseWindowPage{
	private boolean initialised;
	private Button cancelButton;
	private ModalWindow popupWindow;
	private MaintainPartyPageModel pageModel;
	private Long partyID;
	private BusinessCardPageModel businssCardpageModel;
	private Long agreementNumber;
	private transient IPartyMaintenanceController partyMaintenanceController;
	private transient IBusinessCardGuiController businessCardGuiController;
	
	public AddOtherLinkedParties(Long partyID, BusinessCardPageModel businssCardpageModel,
			ModalWindow popupWindow, Long agreementNumber) {
		this.partyID = partyID;
		this.businssCardpageModel = businssCardpageModel;
		this.popupWindow = popupWindow;
		this.agreementNumber=agreementNumber;
	}
	@Override
	protected void onBeforeRender() {
		if(!initialised){
			add(new OtherLinkedPartiesForm("otherLinkedPartiesForm"));
		}		
		super.onBeforeRender();
	}
	public class OtherLinkedPartiesForm extends Form {
		private static final long serialVersionUID = 1L;
		public OtherLinkedPartiesForm(String id) {
			super(id);	
			//add save button
			add(createSaveButton("saveButton",this));
			add(cancelButton = createCancelButton("cancelButton",this));
			//add PA panel
			add(createOtherLinkedPartiesPanel("otherLinkedDetailsPanel"));
						
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
				//still close as we dont care about errors if the user cancels
				popupWindow.close(target);
			}	
		};		
		button.setOutputMarkupId(true);
		button.setOutputMarkupPlaceholderTag(true);
		return button;
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
						disableCancel = "getElementById('"
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
//				raise the party hierarchy request
				ISessionUserProfile userProfile = SRSAuthWebSession.get().getSessionUser();
				List<AgreementPrivilegesDataDTO> partyRolesToStore = businssCardpageModel.getMaintainBusinessCardPanelModel().getBusinessCardDetails().getAgreementPriviledges();
				partyRolesToStore.addAll(businssCardpageModel.getMaintainBusinessCardPanelModel().getDeletePrivilegesDataDTOs());
						try {
					getBusinessCardGuiController().raiseBusinessCardRequest(businssCardpageModel.getMaintainBusinessCardPanelModel().getBusinessCardDetails(), businssCardpageModel.getMaintainBusinessCardPanelModel().getBeforeImage(), userProfile,
							agreementNumber, partyID,PanelToRequestMapping.getMappingForPageAndPanel(BusinessCardDetailsPage.class,OtherLinkedDetailsPanel.class));
				} catch (ValidationException e) {
					//will only do below if save not successfull
					for(String error : e.getErrorMessages()){
						error(error);
					}
					target.add(getFeedBackPanel());	
					target.add(this);	
					return;
				}	
			    businssCardpageModel.getMaintainBusinessCardPanelModel().getDeletePrivilegesDataDTOs().clear();
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
	
	public IBusinessCardGuiController getBusinessCardGuiController() {
		if (businessCardGuiController == null)
		{
			try{
				businessCardGuiController = ServiceLocator.lookupService(IBusinessCardGuiController.class);
			}catch (NamingException ex)
			{
				throw new CommunicationException(ex);
			}
		}
		return businessCardGuiController;
	}

	private Panel createOtherLinkedPartiesPanel(String id){
		pageModel = new MaintainPartyPageModel();
		Panel panel = new EmptyPanel(id);
		PartyDTO party;
			try {
			party = getPartyMaintenanceController().getPartyDTO(partyID);
			pageModel.setPartyBeforeImage((PartyDTO) SerializationUtils.clone(party));
			pageModel.setPartyDTO(party);	
			BusinessCardDetailsDTO businessCardDetails = (BusinessCardDetailsDTO) SerializationUtils.clone(businssCardpageModel.getMaintainBusinessCardPanelModel().getBusinessCardDetails());
			businssCardpageModel.getMaintainBusinessCardPanelModel().setBeforeImage(businessCardDetails);
			panel = new OtherLinkedDetailsPanel(id,businssCardpageModel,EditStateType.MODIFY,getFeedBackPanel(),this,businessCardDetails.getDescriptionDTOs(),agreementNumber);	
			panel.setOutputMarkupId(true);
		} catch (DataNotFoundException e) {			
			error("Could not find the party data for id " + partyID);
		}
		return panel;
	}
	@Override
	public String getPageName() {
		return "Add/Remove Other Linked Parties";
	}

}
