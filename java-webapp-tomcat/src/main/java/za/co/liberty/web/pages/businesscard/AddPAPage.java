package za.co.liberty.web.pages.businesscard;

import java.util.ArrayList;
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

import za.co.liberty.business.guicontrollers.partymaintenance.IPartyMaintenanceController;
import za.co.liberty.dto.party.PartyDTO;
import za.co.liberty.dto.party.PartyRoleDTO;
import za.co.liberty.dto.userprofiles.ISessionUserProfile;
import za.co.liberty.exceptions.data.DataNotFoundException;
import za.co.liberty.exceptions.data.ValidationException;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.helpers.lookup.ServiceLocator;
import za.co.liberty.interfaces.party.PartyRoleType;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.data.enums.PanelToRequestMapping;
import za.co.liberty.web.pages.BaseWindowPage;
import za.co.liberty.web.pages.party.PartyHierarchyPanel;
import za.co.liberty.web.pages.party.model.MaintainPartyPageModel;
import za.co.liberty.web.system.SRSAuthWebSession;

/**
 * Page that will allow ajustment of the Personal assistants for an intermediary
 * @author DZS2610
 *
 */
public class AddPAPage extends BaseWindowPage {

	private static final long serialVersionUID = 1L;
	private boolean initialised;	
	
	private transient IPartyMaintenanceController partyMaintenanceController;
	
	private Long partyID;
	
	private Long agreementNumber;
	
	private MaintainPartyPageModel pageModel;
	
	private ModalWindow popupWindow;
	
	private Button cancelButton;
	
	
	public AddPAPage(Long partyID, Long agreementNumber, ModalWindow popupWindow){
		this.partyID = partyID;
		this.agreementNumber = agreementNumber;
		
		this.popupWindow = popupWindow;
	}
	
	

	@Override
	protected void onBeforeRender() {
		if(!initialised){
			add(new PAForm("paForm"));
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
			add(cancelButton = createCancelButton("cancelButton",this));
			//add PA panel
			add(createPartyHierarchyPanel("paDetailsPanel"));
						
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
	 * Creates the party heirarchy panel that allows PA adjustment
	 * @param id
	 * @return
	 */
	private Panel createPartyHierarchyPanel(String id){
		pageModel = new MaintainPartyPageModel();
		Panel panel = new EmptyPanel(id);
		PartyDTO party;
		try {
			party = getPartyMaintenanceController().getPartyDTO(partyID);
			pageModel.setPartyBeforeImage((PartyDTO) SerializationUtils.clone(party));
			pageModel.setPartyDTO(party);		
			List<PartyRoleType> typesToUse = new ArrayList<PartyRoleType>(2);
			typesToUse.add(PartyRoleType.ISASSISTEDBY);
			typesToUse.add(PartyRoleType.PARTNERTO);
			panel = new PartyHierarchyPanel(id,pageModel,EditStateType.MODIFY,getFeedBackPanel(),this,typesToUse);			
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
					//Add remove paye issue fix
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
//				raise the party hierarchy request
				ISessionUserProfile userProfile = SRSAuthWebSession.get().getSessionUser();
				ArrayList<PartyRoleDTO> partyRolesToStore = new ArrayList<PartyRoleDTO>(pageModel.getMaintainPartyHierarchyPanelModel().getPartyToPartyRoles());
				partyRolesToStore.addAll(pageModel.getMaintainPartyHierarchyPanelModel().getPartyToPartyRoleRemovals());
				try {
					getPartyMaintenanceController().raisePartyhierarchyRequest(
							partyRolesToStore,
							pageModel.getMaintainPartyHierarchyPanelModel().getPartyToPartyRolesBeforeImage(),
							userProfile,agreementNumber,partyID,
							PanelToRequestMapping.getMappingForPageAndPanel(BusinessCardDetailsPage.class,PartyHierarchyPanel.class));
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
		return "Add/Remove Person Assistants";
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
}
