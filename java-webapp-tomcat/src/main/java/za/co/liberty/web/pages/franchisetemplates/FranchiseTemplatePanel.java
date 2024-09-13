package za.co.liberty.web.pages.franchisetemplates;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.apache.wicket.Page;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.validation.IFormValidator;
import org.apache.wicket.markup.html.panel.FeedbackPanel;

import za.co.liberty.business.guicontrollers.template.IFranchiseTemplateGUIController;
import za.co.liberty.dto.userprofiles.ISessionUserProfile;
import za.co.liberty.exceptions.data.ValidationException;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.helpers.lookup.ServiceLocator;
import za.co.liberty.interfaces.agreements.requests.RequestKindType;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.pages.franchisetemplates.model.FranchiseTemplatePanelModel;
import za.co.liberty.web.pages.interfaces.ISecurityPanel;
import za.co.liberty.web.pages.panels.BasePanel;
import za.co.liberty.web.system.SRSAuthWebSession;

/**
 * Franchise Template details panel, capturing the Group Kinds details
 * 
 * @author MZL2611
 * 
 */
public class FranchiseTemplatePanel extends BasePanel implements ISecurityPanel{	
	
	private static final Logger logger = Logger.getLogger(FranchiseTemplatePanel.class);
	
	private FeedbackPanel feedBackPanel;

	private Form franchiseTemplateForm;
	
	private List<FormComponent> validationComponents = new ArrayList<FormComponent>();
	
	private final SimpleDateFormat dteFormatter = new SimpleDateFormat("dd/MM/yyyy");	

	private static final long serialVersionUID = 1L;
	/**
	 * booleans set to true if there are existing requests that still need to be authorised, screen must then be disabled
	 */
	private boolean existingMaintenanceRequest;
	
	private boolean existingTerminationRequest;
	
	private boolean existingReactivationRequest;
	
	private FranchiseTemplatePanelModel panelModel;
	
	private boolean initialised;
	
	private transient IFranchiseTemplateGUIController franchiseTemplateGUIController;
	
	private FranchiseTemplateDetailsPanel franchiseTemplateDetailPanel;
	
	private Page parentPage;
	/**
	 * @param arg0
	 */
	public FranchiseTemplatePanel(String id, final FranchiseTemplatePanelModel panelModel,
			EditStateType editState, FeedbackPanel feedBackPanel, Page parentPage) {
		super(id,editState,parentPage);		
		this.panelModel = panelModel;		
		this.feedBackPanel = feedBackPanel;
		this.parentPage = parentPage;
		List<RequestKindType> unAuthRequests = getOutStandingRequestKinds();

		panelModel.getUnAuthRequests().clear();
		panelModel.getUnAuthRequests().addAll(unAuthRequests);
		//check outstanding requests and disable fields		
		for (RequestKindType kind : unAuthRequests) {
			if(kind == RequestKindType.MaintainFranchiseTemplateDetails){
				existingMaintenanceRequest = true;
				
			}			
		}				
		add(franchiseTemplateForm = createFranchiseTemplateForm("franchiseTemplateForm"));
	}
	
	/**
	 * Load the components on the page on first render, 
	 * so that the components are only generated when the page is displayed 
	 */
	@Override
	protected void onBeforeRender() {
		if(!initialised) {			
			initialised=true;				
	
			
		}
		if(feedBackPanel == null){			
			feedBackPanel = this.getFeedBackPanel();		
		}
		super.onBeforeRender();
	}
	
	/**
	 * create a new node form with validations attached
	 * @param id
	 * @return
	 */
	private Form createFranchiseTemplateForm(String id) {
		Form form = new FranchiseTemplateForm(id);		
		return form;
		
	}
	
	@Override
	public EditStateType getEditState() {
		//will disable any modification if there are any requests pending auth
		if(existingMaintenanceRequest || existingTerminationRequest || existingReactivationRequest){
			return EditStateType.VIEW;
		}
		return super.getEditState();
	}

	@Override
	protected boolean isView(EditStateType[] editableStates) {	
		//will disable any modification if there are any requests pending auth
		if(existingMaintenanceRequest || existingTerminationRequest || existingReactivationRequest){
			return true;
		}
		return super.isView(editableStates);
	}	
	
	/**
	 * Form used for the panel so we can add validations and on submit method calls
	 * @author MZL2611
	 *
	 */
	public class FranchiseTemplateForm extends Form {
		private static final long serialVersionUID = 1L;
		public FranchiseTemplateForm(String id) {
			super(id);
			this.add(franchiseTemplateDetailPanel = createFranchiseTemplateDetailsPanel("templateAddPanel"));
			
			add(new IFormValidator() {

				private static final long serialVersionUID = 1L;

				@SuppressWarnings("unchecked")
				public FormComponent[] getDependentFormComponents() {				
					return null;
				}
				
				public void validate(final Form form) {				
					if (getEditState().isViewOnly()) {
						return;
					}
					boolean validate = true;
					for(FormComponent comp : validationComponents){
						if(!comp.isValid()){
							validate = false;
						} 
					}
					if(validate){
							
						try{	
						
							//To get this to work I had to add ajax update for every single component
							if (panelModel.getDistributionKindGroupEnum() == null){
								List<String> errors = new ArrayList<String>();
								errors.add("Please select an Agreement Kind");
								throw new ValidationException(errors);
							}
							//validate franchise template 
							
							 getFranchiseTemplateGUIController().validateTemplateKindGroups(panelModel.getFranchiseTemplateDTO());					
						}catch(ValidationException ex){							
							for(String error : ex.getErrorMessages()){
								error(error);								
							}
						}
					}					
				}
				
			});
		}
	}	
	
	private FranchiseTemplateDetailsPanel createFranchiseTemplateDetailsPanel(String id){
		FranchiseTemplateDetailsPanel franchiseTemplateDetailsPanel = new FranchiseTemplateDetailsPanel(id,panelModel,getEditState(),feedBackPanel,parentPage );
		if(getEditState() == EditStateType.AUTHORISE){
			franchiseTemplateDetailsPanel.setVisible(true);
			
		}else if (getEditState().isViewOnly() || getEditState() ==  EditStateType.TERMINATE) {
			franchiseTemplateDetailsPanel.setEnabled(true);
			//addNewFromExistingTemplatePanel.setVisible(false);
		}
		else if ( getEditState() ==  EditStateType.MODIFY) {
			franchiseTemplateDetailsPanel.setEnabled(true);		
			//addNewFromExistingTemplatePanel.setVisible(false);
		}
		franchiseTemplateDetailsPanel.setOutputMarkupId(true);
		return franchiseTemplateDetailsPanel;
		
	}

	public Class getPanelClass() {
		return FranchiseTemplatePanel.class;
	}

	/**
	 * Set the feedback panel to use for errors
	 * @param feedBackPanel
	 */
	public void setFeedBackPanel(FeedbackPanel feedBackPanel) {
		this.feedBackPanel = feedBackPanel;
	}
	
	/**
	 * Get the FranchiseTemplateGUIController bean 
	 * @return
	 */
	private IFranchiseTemplateGUIController getFranchiseTemplateGUIController(){
		if(franchiseTemplateGUIController == null){
			try {
				franchiseTemplateGUIController = ServiceLocator.lookupService(IFranchiseTemplateGUIController.class);
			} catch (NamingException e) {
				throw new CommunicationException(e);
			}
		}
		return franchiseTemplateGUIController;
	}

	private ISessionUserProfile getLoggedInUser(){
		return SRSAuthWebSession.get().getSessionUser();
	}
	
}
