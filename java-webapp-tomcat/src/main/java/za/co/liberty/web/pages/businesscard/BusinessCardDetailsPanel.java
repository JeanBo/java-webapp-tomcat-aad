package za.co.liberty.web.pages.businesscard;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import javax.naming.NamingException;

import org.apache.commons.lang.SerializationUtils;
import org.apache.log4j.Logger;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow.MaskType;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import com.inmethod.grid.IGridColumn;
import com.inmethod.grid.SizeUnit;

import za.co.liberty.business.guicontrollers.IAgreementPrivilegesController;
import za.co.liberty.business.guicontrollers.agreement.IAgreementGUIController;
import za.co.liberty.business.guicontrollers.businesscard.IBusinessCardGuiController;
import za.co.liberty.dto.agreement.AgreementRoleDTO;
import za.co.liberty.dto.agreement.maintainagreement.AgreementPartnerRolesGridDTO;
import za.co.liberty.dto.businesscard.BusinessCardDetailsDTO;
import za.co.liberty.dto.contracting.ResultPartyDTO;
import za.co.liberty.dto.party.PartyRoleDTO;
import za.co.liberty.dto.party.PersonDTO;
import za.co.liberty.dto.party.contactdetail.CommunicationPreferenceDTO;
import za.co.liberty.dto.party.contactdetail.ContactPreferenceDTO;
import za.co.liberty.dto.userprofiles.ISessionUserProfile;
import za.co.liberty.exceptions.data.DataNotFoundException;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.helpers.lookup.ServiceLocator;
import za.co.liberty.interfaces.agreements.RoleKindType;
import za.co.liberty.interfaces.agreements.requests.RequestKindType;
import za.co.liberty.interfaces.gui.IPopupResponseComponent;
import za.co.liberty.persistence.agreement.IAgreementEntityManager;
import za.co.liberty.web.data.enums.ComponentType;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.pages.BasePage;
import za.co.liberty.web.pages.businesscard.model.BusinessCardPageModel;
import za.co.liberty.web.pages.businesscard.model.MaintainBusinessCardPanelModel;
import za.co.liberty.web.pages.contactdetail.ContactDetailsPanel;
import za.co.liberty.web.pages.panels.BasePanel;
import za.co.liberty.web.pages.panels.GUIFieldPanel;
import za.co.liberty.web.pages.panels.HelperPanel;
import za.co.liberty.web.system.SRSAuthWebSession;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGrid;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGrid.GridSizeUnit;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGridColumn;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataProviderAdapter;
import za.co.liberty.web.wicket.markup.repeater.data.ListDataProvider;

/**
 * Panel for ajusting the business card details of the intermediary
 * @author DZS2610
 *
 */
public class BusinessCardDetailsPanel extends BasePanel{ 

	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(BusinessCardDetailsPanel.class);
	
	ResultPartyDTO partyInContext;
	
	private FeedbackPanel feedBackPanel;
	
	@SuppressWarnings("unused")
	private ContactDetailsPanel contactDetailsPanel;	
	
	private transient IBusinessCardGuiController businessCardGuiController;
	
	private transient IAgreementGUIController guiController;
	
	private transient IAgreementPrivilegesController agreementPrivilegesController;
	
	private BusinessCardPageModel pageModel;
	
	private boolean initialised;
	
	private Page parentPage;
	
	private ModalWindow paAdjustmentPopup;
	
	private ModalWindow paContactDetailsPopup;
	
	private SRSDataGrid paGrid;
	
	private SRSDataGrid partnerGrid;
	
	private Long paPartyOID;
	
	private ISessionUserProfile loggedInUser = SRSAuthWebSession.get().getSessionUser();
	
	private transient IAgreementEntityManager agreementEntityManager;

	
	public BusinessCardDetailsPanel(String id, BusinessCardPageModel pageModel , 
			EditStateType editState, FeedbackPanel feedBackPanel, Page parentPage) {
		super(id, editState,parentPage);
		this.pageModel = pageModel;		
		this.feedBackPanel = feedBackPanel;
		this.parentPage = parentPage;
	}
	
	/**
	 * Load the components on the page on first render, 
	 * so that the components are only generated when the page is displayed 
	 */
	@Override
	protected void onBeforeRender() {
		logger.info("onRender");
		if(!initialised) {			
			initialised=true;	
			initPanelModel();
			add(new BCForm("businessCardForm"));
			add(paAdjustmentPopup = createPAAdjustmentModalWindow("paAdjustmentPopup"));
			add(paContactDetailsPopup = createPAContactDetailsModalWindow("paContactDetailsPopup"));
		}
		super.onBeforeRender();
	}
	/**
	 * Get the agreement manager
	 * @return
	 */
	private IAgreementGUIController getAgreementGUIController() {
		if(guiController == null){
			try{
				guiController = ServiceLocator.lookupService(IAgreementGUIController.class);
			} catch (NamingException e) {
					throw new CommunicationException(e);
			}	
		}
		return guiController;
	}
	
	
	/**
	 * Get the agreement entity manager
	 * @return
	 */
	private IAgreementEntityManager getAgreementEntityManager() {
		if(agreementEntityManager == null){
			try{
				agreementEntityManager = ServiceLocator.lookupService(IAgreementEntityManager.class);
			} catch (NamingException e) {
					throw new CommunicationException(e);
			}	
		}
		return agreementEntityManager;
	}
	
	
	/**
	 * Form used for the panel so we can add validations and on submit method calls
	 * @author DZS2610
	 *
	 */
	public class BCForm extends Form {
		private static final long serialVersionUID = 1L;
		public BCForm(String id) {
			super(id);	
			
			BusinessCardDetailsDTO businessCardDetails  = pageModel.getMaintainBusinessCardPanelModel().getBusinessCardDetails();		
			
			List<ContactPreferenceDTO> prefs = (businessCardDetails != null && businessCardDetails.getParty() != null 
					&& businessCardDetails.getParty().getContactPreferences() != null && businessCardDetails.getParty().getContactPreferences().getContactPreferences() != null) 
					? businessCardDetails.getParty().getContactPreferences().getContactPreferences() : new ArrayList<ContactPreferenceDTO>(0);
					
			List<CommunicationPreferenceDTO> commPrefs = (businessCardDetails != null && businessCardDetails.getParty() != null 
					&& businessCardDetails.getParty().getCommunicationPreferences() != null) 
					? businessCardDetails.getParty().getCommunicationPreferences() : new ArrayList<CommunicationPreferenceDTO>();
					
			//List<CommunicationPreferenceDTO> commPrefs = businessCardDetails.getParty().getCommunicationPreferences();
			boolean isSecureRequestAllowed=SRSAuthWebSession.get().getSessionUser().isAllowRaise(RequestKindType.MaintainSecureContactDetails);
			
			//init contact details panel
			// We are passing true for the createAgreement as we require the same validation here
			//	as we do for create i.e. require home physical.
			
			add(contactDetailsPanel = new ContactDetailsPanel(
					"addressPanel",prefs,commPrefs,
					getEditState(),feedBackPanel,isSecureRequestAllowed,true,parentPage, false));
			
			//add the PA grid
			Label paLabel = new Label("paLabel","PAs Linked");	
			paLabel.setOutputMarkupId(true);
			paLabel.setOutputMarkupPlaceholderTag(true);
			if(getEditState() == EditStateType.AUTHORISE){
				add(new EmptyPanel("paPanel"));		
				paLabel.setVisible(false);
			}else{				
				add(paGrid = createPARolesGrid("paPanel",pageModel.getMaintainBusinessCardPanelModel().getBusinessCardDetails()));
			}
			add(paLabel);
			//add the PA additions button
			add(createAddRemovePAButton("addRemovePAButton",pageModel.getPartyOID()));
			
			//Added for the partner to Role
			Label partnerLabel = new Label("partnerLabel","Partner Linked");	
			partnerLabel.setOutputMarkupId(true);
			partnerLabel.setOutputMarkupPlaceholderTag(true);
			if(getEditState() == EditStateType.AUTHORISE){
				add(new EmptyPanel("partnerPanel"));	
				partnerLabel.setVisible(false);
			}else{	
				add(partnerGrid = createPartnerRolesGrid("partnerPanel",pageModel.getMaintainBusinessCardPanelModel().getBusinessCardDetails()));
			}
			add(partnerLabel);
			//other BC details panels
			RepeatingView leftPanel1 = new RepeatingView("leftPanel1");
			RepeatingView leftPanel2 = new RepeatingView("leftPanel2");
			RepeatingView middlePanel2 = new RepeatingView("middlePanel2");
			RepeatingView rightPanel2 = new RepeatingView("rightPanel2");
			
			add(leftPanel1);
			add(leftPanel2);
			add(middlePanel2);
			add(rightPanel2);		 
			if(businessCardDetails.getParty() instanceof PersonDTO)	{
				GUIFieldPanel knownASPanel = createGUIFieldPanel("Known as Name","Known as Name","Known as Name",createPageField(businessCardDetails,"Known as Name","panel","party.knowAsName",ComponentType.TEXTFIELD,false,true, new EditStateType[]{EditStateType.MODIFY}),20);
				//if(((HelperPanel)knownASPanel.getComponent()).getEnclosedObject() instanceof FormComponent){
				//	validationComponents.add((FormComponent)((HelperPanel)titlePanel.getComponent()).getEnclosedObject());
				//}
				leftPanel1.add(knownASPanel);
			}			
			GUIFieldPanel wtdPanel = createGUIFieldPanel("WTD ID","WTD ID","WTD ID",createPageField(businessCardDetails,"WTD ID","panel","wTDID",ComponentType.TEXTFIELD,false,true, new EditStateType[]{EditStateType.MODIFY}),21);
			leftPanel1.add(wtdPanel);			
			GUIFieldPanel presentedByNamePanel = createGUIFieldPanel("Presented By Name","Presented By Name","Presented By Name",createPageField(businessCardDetails,"Presented By Name","panel","presentedByName",ComponentType.TEXTFIELD,false,true, new EditStateType[]{EditStateType.MODIFY}),22);
			leftPanel2.add(presentedByNamePanel);
			GUIFieldPanel presentedByInitPanel = createGUIFieldPanel("Initials","Initials","Initials",createPageField(businessCardDetails,"Initials","panel","presentedByInitials",ComponentType.TEXTFIELD,false,true, new EditStateType[]{EditStateType.MODIFY}),23);
			middlePanel2.add(presentedByInitPanel);
			GUIFieldPanel presentedBySurnamePanel = createGUIFieldPanel("Surname","Surname","Surname",createPageField(businessCardDetails,"Surname","panel","presentedBySurname",ComponentType.TEXTFIELD,false,true, new EditStateType[]{EditStateType.MODIFY}),24);
			rightPanel2.add(presentedBySurnamePanel);
			
//			add(new IFormValidator() {
//				private static final long serialVersionUID = 1L;
//
//				public void validate(Form arg0) {
//					if (getEditState().isViewOnly()) {
//						return;
//					}
//					//validate the panel					
//					try {							
//						getBusinessCardGuiController().validateBusinessCardDetails(pageModel.getMaintainBusinessCardPanelModel().getBusinessCardDetails());
//					} catch (ValidationException e) {
//						for(String error : e.getErrorMessages()){
//							BCForm.this.error(error);	
//						}
//					}					
//				}
//				
//				public FormComponent[] getDependentFormComponents() {
//					return null;
//				}
//			});
		}
	}	
	
	/**
	 * Create the Add/remove button that poups the party roles panel allowing additions/removals of IsAssitedBy links
	 * @param id
	 * @param partyID
	 * @return
	 */
	private Button createAddRemovePAButton(String id, Long partyID) {
		Button but = new Button(id);	
		but.add(new AjaxFormComponentUpdatingBehavior("click"){									
			private static final long serialVersionUID = 1L;
			@Override
			protected void onUpdate(AjaxRequestTarget target) {					
				paAdjustmentPopup.show(target);
			}									
		});		
		but.setOutputMarkupId(true);
		but.setOutputMarkupPlaceholderTag(true);
		Page page = getPage();
		boolean enabled = false;
		if(page instanceof BasePage){
			BasePage basePage = ((BasePage)page);
			enabled = basePage.hasModifyAccess() && pageModel.getSelectedItem()!=null && basePage.checkModificationRules();
		}		
		if(!loggedInUser.isAllowRaise(RequestKindType.MaintainLinkedAssistants)){
			but.setEnabled(false);
			but.add(new AttributeModifier("title","You do not have access to the request MaintainLinkedAssistants, "
					+ "please consult support if you need access"));
		}
		
		but.setVisible(getEditState().isViewOnly() && enabled);
		return but;
	}
	/**
	 * Create the modal window
	 * 
	 * @param id
	 * @return
	 */
	private ModalWindow createPAAdjustmentModalWindow(String id) {		
		final ModalWindow window = new ModalWindow(id);
		window.setTitle("Add/Remove PAs");	
				
		// Create the page
		window.setPageCreator(new ModalWindow.PageCreator() {
			private static final long serialVersionUID = 1L;
			public Page createPage() {				
				return new AddPAPage(pageModel.getPartyOID(),pageModel.getAgreementNumber(),paAdjustmentPopup);		
			}
		});		
//		 Close window call back
		window.setWindowClosedCallback(new ModalWindow.WindowClosedCallback() {
			private static final long serialVersionUID = 1L;
			
			public void onClose(AjaxRequestTarget target) {
				refreshPAList(target);
			}
			
		});
		// Initialise window settings
		window.setMinimalHeight(350);
		window.setInitialHeight(400);
		window.setMinimalWidth(750);
		window.setInitialWidth(750);
		window.setMaskType(MaskType.SEMI_TRANSPARENT);
		window.setCssClassName(ModalWindow.CSS_CLASS_GRAY);			
		return window;
	}


	/**
	 * Create the modal window
	 * 
	 * @param id
	 * @return
	 */
	private ModalWindow createPAContactDetailsModalWindow(String id) {		
		final ModalWindow window = new ModalWindow(id);
		window.setTitle("Contact Details");	
		
		final IPopupResponseComponent comp = new IPopupResponseComponent(){			
			private static final long serialVersionUID = 1L;
			
			boolean successProcessed;
			
			public boolean isSuccessful() {				
				return successProcessed;
			}

			public void setSuccessful(boolean successful) {
				successProcessed = successful;
				
			}
			
		};
				
		// Create the page
		window.setPageCreator(new ModalWindow.PageCreator() {
			private static final long serialVersionUID = 1L;
			public Page createPage() {				
				return new PAContactDetailsPage(paPartyOID,paContactDetailsPopup,comp);		
			}
		});		
//		 Close window call back
		window.setWindowClosedCallback(new ModalWindow.WindowClosedCallback() {
			private static final long serialVersionUID = 1L;
			
			public void onClose(AjaxRequestTarget target) {
				if(comp.isSuccessful()){
					info("Contact details updated successfuly");
					target.add(getFeedBackPanel());
				}
			}
			
		});
		// Initialise window settings
		window.setMinimalHeight(500);
		window.setInitialHeight(550);
		window.setMinimalWidth(800);
		window.setInitialWidth(800);
		window.setMaskType(MaskType.SEMI_TRANSPARENT);
		window.setCssClassName(ModalWindow.CSS_CLASS_GRAY);			
		return window;
	}
	
	
	
	/**
	 * Creates the grid for the PA roles 
	 * @param panelModel
	 * @return
	 */
	@SuppressWarnings("unused")
	private SRSDataGrid createPARolesGrid(String id, BusinessCardDetailsDTO data){
		List<PartyRoleDTO> paRoles = data.getPaRoles();
		if(paRoles == null){
			paRoles = new ArrayList<PartyRoleDTO>(0);
		}			
		
		SRSDataGrid grid = new SRSDataGrid(id, new SRSDataProviderAdapter(
				new ListDataProvider<PartyRoleDTO>(paRoles)),
				getPARoleColumns(), getEditState(),null);		
		grid.setCleanSelectionOnPageChange(false);
		grid.setClickRowToSelect(false);
		grid.setAllowSelectMultiple(true);
		//grid.setGridWidth(650, GridSizeUnit.PIXELS);
		grid.setGridWidth(99, GridSizeUnit.PERCENTAGE);
		grid.setRowsPerPage(10);
		grid.setContentHeight(70, SizeUnit.PX);
		return grid;
	}
	/**
	 * Creates the grid for the Partner roles 
	 * @param panelModel
	 * @return
	 */
	@SuppressWarnings("unused")
	private SRSDataGrid createPartnerRolesGrid(String id, BusinessCardDetailsDTO data){
		List<AgreementRoleDTO> partnerRoles = data.getPartnerRoles();
		List<AgreementPartnerRolesGridDTO> gridRoles = new ArrayList<AgreementPartnerRolesGridDTO>();
		IAgreementGUIController agreementGUIController = getAgreementGUIController();
	
		if(partnerRoles == null ){
			partnerRoles = new ArrayList<AgreementRoleDTO>(0);
		}
		//If the part in context does not have a inAssociation role 
		//then check for the inverse
		if(partnerRoles.isEmpty()){		
		List<Long> rolePlayerIdList  = new ArrayList<Long>();
		 
		 rolePlayerIdList.add(pageModel.getAgreementNumber());
		  if(pageModel.getAgreementNumber() != null && !rolePlayerIdList.isEmpty()){
		   Date currentDate = new Date(System.currentTimeMillis());
		                                              
		   partnerRoles = getAgreementEntityManager().findAgreementRolesOfTypeForRolePlayers(rolePlayerIdList, RoleKindType.INASSOCIATIONWITH , AgreementRoleDTO.class);

		  }else{
			  partnerRoles = new ArrayList<AgreementRoleDTO>(0);
		  }
		 }
		
		for(AgreementRoleDTO role : partnerRoles){
			if( role != null && role.getKind() == RoleKindType.INASSOCIATIONWITH.getKind()){
				AgreementPartnerRolesGridDTO gridRole = new AgreementPartnerRolesGridDTO();
				gridRole.setRole(role);
				gridRole.setAgreementNr(pageModel.getAgreementNumber());
				//set the other grid display data
				agreementGUIController.setUpAgreementPartnerRolesGridData(gridRole);
				gridRoles.add(gridRole);
			}
			
		}
		SRSDataGrid grid = new SRSDataGrid(id, new SRSDataProviderAdapter(
				new ListDataProvider<AgreementPartnerRolesGridDTO>(gridRoles)),
				getPartnerRoleColumns(), getEditState(),null);		
		grid.setCleanSelectionOnPageChange(false);
		grid.setClickRowToSelect(false);
		grid.setAllowSelectMultiple(true);
		//grid.setGridWidth(650, GridSizeUnit.PIXELS);
		grid.setGridWidth(99, GridSizeUnit.PERCENTAGE);
		grid.setRowsPerPage(2);
		grid.setContentHeight(20, SizeUnit.PX);
		return grid;
	}
	/**
	 * Get the columns for the PA grid
	 * @return
	 */
	private List<IGridColumn> getPARoleColumns() {				
		Vector<IGridColumn> cols = new Vector<IGridColumn>();	
		
//		add in the name column
		cols.add(new SRSDataGridColumn<PartyRoleDTO>("rolePlayerReference.name",
				new Model("Party Name"), "rolePlayerReference.name", "rolePlayerReference.name", getEditState()).setInitialSize(140).setWrapText(true));
//		add in the uacfid column
		cols.add(new SRSDataGridColumn<PartyRoleDTO>("rolePlayerReference.uacfID",
				new Model("UACFID"), "rolePlayerReference.uacfID", "rolePlayerReference.uacfID", getEditState()).setInitialSize(80));
		
//		add in the start date column
		cols.add(new SRSDataGridColumn<PartyRoleDTO>("effectiveFrom",
				new Model("Start Date"), "effectiveFrom", "effectiveFrom", getEditState()).setInitialSize(80));
		
//		add in the end date column
		cols.add(new SRSDataGridColumn<PartyRoleDTO>("effectiveTo",
				new Model("UACFID"), "effectiveTo", "effectiveTo", getEditState()).setInitialSize(80));
		
//		add search button, don't display this column on view
		boolean editEnabled = false;
		Page page = getPage();
		if(page instanceof BasePage){
			BasePage basePage = ((BasePage)page);
			editEnabled = basePage.hasModifyAccess() && pageModel.getSelectedItem()!=null && basePage.checkModificationRules();
			
		}	
		
		
		if(getEditState() == null ||  getEditState().isViewOnly() && editEnabled){
			cols.add(new SRSDataGridColumn<PartyRoleDTO>("edit",
					new Model("Edit"), "edit", getEditState()){					
						private static final long serialVersionUID = 1L;	
						@Override
						public Panel newCellPanel(WebMarkupContainer parent, String componentId, IModel rowModel, String objectProperty, EditStateType state, final PartyRoleDTO data) {
						Button editButton = new Button("value", new Model("Edit"));	
						editButton.add(new AjaxFormComponentUpdatingBehavior("click"){									
							private static final long serialVersionUID = 1L;
							@Override
							protected void onUpdate(AjaxRequestTarget target) {	
								paPartyOID = data.getRolePlayerReference().getOid();
								paContactDetailsPopup.show(target);							
							}									
						});
						
						if(!loggedInUser.isAllowRaise(RequestKindType.MaintainContactDetails)){
							editButton.setEnabled(false);
							editButton.add(new AttributeModifier("title","You do not have access to the request MaintainContactDetails, please consult support if you need access"));
						}						
						return HelperPanel.getInstance(componentId,editButton);								
						}				
				
			}.setInitialSize(64));
		}	
		return cols;
	}

	/**
	 * Get the columns for the Partner grid
	 * @return
	 */
	private List<IGridColumn> getPartnerRoleColumns() {				
		Vector<IGridColumn> cols = new Vector<IGridColumn>();	
		
	
//			add in the name column
			cols.add(new SRSDataGridColumn<AgreementPartnerRolesGridDTO>("Name",
					new Model("Partner Name"), "Name", "Name", getEditState()).setInitialSize(140).setWrapText(true));
		

//		add in the uacfid column
		cols.add(new SRSDataGridColumn<AgreementPartnerRolesGridDTO>("kind",
				new Model("Role Kind"), "kind", "kind",
				getEditState()) {

					@Override
					public Panel newCellPanel(WebMarkupContainer parent,
							String componentId, IModel rowModel,
							String objectProperty, EditStateType state,
							final AgreementPartnerRolesGridDTO data) {	
							//create label with type and display	
							RoleKindType role = RoleKindType.getRoleKindTypeForKind(data.getRole().getKind().intValue());
							return HelperPanel.getInstance(componentId, new Label("value",(role != null) ? role.getDescription() : ""));
					}

				}.setInitialSize(120));
		//		add in the start date column
		cols.add(new SRSDataGridColumn<AgreementPartnerRolesGridDTO>("agreementRoleKind",
				new Model("Agreement number"), "agreementRoleKind", "agreementRoleKind", getEditState()).setInitialSize(120));
		//		add in the start date column
		cols.add(new SRSDataGridColumn<AgreementPartnerRolesGridDTO>("effectiveFrom",
				new Model("Start Date"), "effectiveFrom", "effectiveFrom", getEditState()).setInitialSize(80));	
//		add in the end date column
		cols.add(new SRSDataGridColumn<AgreementPartnerRolesGridDTO>("role.effectiveTo",
				new Model("End Date"), "effectiveTo", "effectiveTo", getEditState()).setInitialSize(80));

		return cols;
	}
	private void initPanelModel(){
		MaintainBusinessCardPanelModel panelModel = new MaintainBusinessCardPanelModel();
		if(pageModel.getMaintainBusinessCardPanelModel() != null
				&& pageModel.getMaintainBusinessCardPanelModel().getBusinessCardDetails() != null){
			 panelModel = pageModel.getMaintainBusinessCardPanelModel();	
		}		
		 panelModel = pageModel.getMaintainBusinessCardPanelModel();			
		if(pageModel.getPartyOID() != null){
//			get the partyDTO and set into the model
			try {
				Long agreementNumber = pageModel.getAgreementNumber();
				if (agreementNumber==null)
				{
					agreementNumber = new Long(0);
				}
				BusinessCardDetailsDTO bcDetails = getBusinessCardGuiController().getBusinessCardDetailsDTO(pageModel.getPartyOID(),agreementNumber);
				//before we clone, we insert data from the external system if it exists
				if(pageModel.getPassedInParams() != null){
					PageParameters parameters = pageModel.getPassedInParams();											
					bcDetails.setConsultantUACFID(parameters.get("consultantUACFID").toString());
					bcDetails.setPresentedByInitials(parameters.get("currentPBInitials").toString());
					bcDetails.setPresentedByName(parameters.get("currentPBName").toString());
					bcDetails.setPresentedBySurname(parameters.get("currentPBSurname").toString());
					bcDetails.setPromotionalDetails(parameters.get("promoDetails").toString());
					bcDetails.setReferenceNo(parameters.get("referenceNo").toString());
					bcDetails.setWTDID(parameters.get("currentWTDID").toString());
				}				
				BusinessCardDetailsDTO beforeImage = (BusinessCardDetailsDTO)SerializationUtils.clone(bcDetails);
				panelModel.setBusinessCardDetails(bcDetails);
				panelModel.setBeforeImage(beforeImage);
			} catch (DataNotFoundException e) {
				//party could not be found
				error("Party details could not be found");
			}			
		}else{
			panelModel.setBusinessCardDetails(new BusinessCardDetailsDTO());
		}		
		pageModel.setMaintainBusinessCardPanelModel(panelModel);
	}

	
	/**
	 * Will refresh the PA list in the grid from the DB
	 *
	 */
	public void refreshPAList(AjaxRequestTarget target){
		List<PartyRoleDTO> partyRoles = pageModel.getMaintainBusinessCardPanelModel().getBusinessCardDetails().getPaRoles();
		partyRoles.clear();
		partyRoles.addAll(getBusinessCardGuiController().getPARolesForParty(pageModel.getPartyOID()));
		//now refresh the grid
		target.add(paGrid);
		target.add(partnerGrid);
	}
	
	/**
	 * Get the BusinessCardGuiController bean
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
	
	private IAgreementPrivilegesController getAgreementPrivilegesController() {
		if (agreementPrivilegesController == null) 	{
			try {
				agreementPrivilegesController = ServiceLocator.lookupService(IAgreementPrivilegesController.class);
			} catch (NamingException e) {
				throw new CommunicationException(e);
			}
		}
		return agreementPrivilegesController;
	}

	public Class getPanelClass() {		
		return BusinessCardDetailsPanel.class;
	}	
}
