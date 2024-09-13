package za.co.liberty.web.pages.maintainagreement;

import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormChoiceComponentUpdatingBehavior;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow.MaskType;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.Model;

import za.co.liberty.agreement.common.enums.ProductKindEnumeration;
import za.co.liberty.constants.ISRSConstants;
import za.co.liberty.dto.agreement.EstablishAllowanceDTO;
import za.co.liberty.dto.agreement.IncentiveDetailDTO;
import za.co.liberty.dto.agreement.maintainagreement.ValidAgreementValuesDTO;
import za.co.liberty.dto.userprofiles.ISessionUserProfile;
import za.co.liberty.interfaces.agreements.requests.RequestKindType;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.data.enums.PanelToRequestMapping;
import za.co.liberty.web.pages.interfaces.ISecurityPanel;
import za.co.liberty.web.pages.maintainagreement.model.IncentiveModificationPopupModel;
import za.co.liberty.web.pages.maintainagreement.model.MaintainAgreementPageModel;
import za.co.liberty.web.pages.maintainagreement.model.MaintainIncentivePanelModel;
import za.co.liberty.web.pages.panels.BasePanel;
import za.co.liberty.web.system.SRSAuthWebSession;
import za.co.liberty.web.wicket.modal.SRSModalWindow;

/**
 * The Panel containing all the agreement incentive detail
 * @author DZS2610
 *
 */
public class AgreementIncentivesPanel extends BasePanel implements ISecurityPanel 
{
	private static final long serialVersionUID = 1L;
	
	private static final transient Logger logger = Logger.getLogger(AgreementIncentivesPanel.class);
	
	private MaintainIncentivePanelModel panelModel; 
	
	private FeedbackPanel feedBackPanel;
	
	private SRSModalWindow modificationWindow;
	
	private boolean initialised;
	
	private boolean existingIncentiveRequest;
	
	private static final SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
	
	private RadioGroup<IncentiveDetailDTO> modifyGroup;
	
	private RadioGroup<IncentiveDetailDTO> addGroup;
	
	private IncentiveForm incentiveform;
	
	/*
	 * Popup related variables
	 */
	private PopupType currentPopupType = PopupType.MODIFY;
	
	private IncentiveModificationPage currentPopup;
	EditStateType popupEditState;
	
	private IncentiveDetailDTO currentSelectedModify;
	
	private IncentiveDetailDTO currentSelectedAdd;
	
	private enum PopupType{
		MODIFY,ADD
	}
	
	private enum IncentiveTypes{
		CURRENT,AVAILABLE;
		
	}
//
//	/**
//	 * Made static as documentation stated to only have one instance
//	 */
//	private static final MapperIF mapper = new DozerBeanMapper();
//
	
	/**
	 * Default constructor
	 * 
	 * @param id
	 * @param model
	 * @param editState
	 * @param feedBackPanel
	 * @param parentPAge
	 */
	public AgreementIncentivesPanel(String id, MaintainIncentivePanelModel model , 
			EditStateType editState, FeedbackPanel feedBackPanel, Page parentPAge) {
		super(id, editState,parentPAge);
		this.panelModel = model;
		this.feedBackPanel = feedBackPanel;
	}	
	
	public AgreementIncentivesPanel(String id, MaintainAgreementPageModel pageModel , 
			EditStateType editState, FeedbackPanel feedBackPanel, Page parentPage) {
		super(id, editState,parentPage);
		//create panelModel using pagemodel values		
		this.panelModel = createPanelModelFromAgreementPageModel(pageModel);
		this.feedBackPanel = feedBackPanel;
	}	
	
	/**
	 * Create the intial panel model using the pagemodel values needed
	 * @param pageModel
	 * @return
	 */
	private MaintainIncentivePanelModel createPanelModelFromAgreementPageModel(MaintainAgreementPageModel pageModel){
		MaintainIncentivePanelModel panelModel = new MaintainIncentivePanelModel();
		panelModel.setAgreementnumber(pageModel.getAgreementNo());
		panelModel.setCurrentAndfutureIncentiveDetails(pageModel.getMaintainAgreementDTO().getAgreementDTO().getCurrentAndFutureIncentives());
		//get all the available incentives
		panelModel.setAvailableIncentivesDetails(pageModel.getMaintainAgreementDTO().getAgreementDTO().getAvailableIncentives());
		
		//set allowed values
		ValidAgreementValuesDTO values = pageModel.getValidAgreementValues();
		if(pageModel.getMaintainAgreementDTO().getAgreementDTO().getKind() == ProductKindEnumeration._DIRECTINTERMEDIARYAGREEMENT){
			panelModel.setAllowedManPowerValues(values.getValidDirectManpower());
		}else{
			panelModel.setAllowedManPowerValues(values.getValidManpower());
		}			
		panelModel.setAgreementKind(pageModel.getMaintainAgreementDTO().getAgreementDTO().getKind());		
		panelModel.setAllowedGEPamounts(values.getGepIncentiveAmounts());
		
		panelModel.setAgreementStartDate(pageModel.getMaintainAgreementDTO().getAgreementDTO().getStartDate());		
		if(pageModel.getMaintainAgreementDTO().getAgreementDTO().getCurrentStatus() != null){
			panelModel.setAgreementStatusString(pageModel.getMaintainAgreementDTO().getAgreementDTO().getCurrentStatus().getName());
		}
		return panelModel;
	}
	
	
	/**
	 * Load the components on the page on first render, 
	 * so that the components are only generated when the page is displayed 
	 */
	@Override
	protected void onBeforeRender() {
		if(!initialised) {			
			initialised=true;	
//			initialize the page model data
			initPageModel();
			
			List<RequestKindType> unAuthRequests = getOutStandingRequestKinds();			
			//check for existing requests FIRST as other panels use variables set here
			
			//after the outstanding requests, we check if user can actually raise requests on left of request kinds
			ISessionUserProfile user = getLoggedInUser();
			
			RequestKindType[] requestsForPanel = PanelToRequestMapping.getRequestKindsForPanel(AgreementIncentivesPanel.class);
			Set<RequestKindType> unAvailableRequest = new HashSet<RequestKindType>(requestsForPanel.length);			
			for(RequestKindType kind : requestsForPanel){
				if(!user.isAllowRaise(kind)){
					unAvailableRequest.add(kind);
				}
			}	
			
			unAvailableRequest.addAll(unAuthRequests);			
			for (RequestKindType kind : unAvailableRequest) {
				if(kind == RequestKindType.MaintainIncentiveDetails){
					existingIncentiveRequest = true;					
				}					
			}			
			add(incentiveform = new IncentiveForm("incentiveForm"));
			add(modificationWindow = createIncentiveModificationWindow("incentiveModificationWindow"));						
		}
		if(feedBackPanel == null){			
			feedBackPanel = this.getFeedBackPanel();		
		}
		super.onBeforeRender();
	}
	
	/**
	 * Refresh all form data and redraw on user machine
	 * @param target
	 */
	private void reDrawForm(AjaxRequestTarget target){
		IncentiveForm form = new IncentiveForm("incentiveForm");
		incentiveform.replaceWith(form);
		incentiveform = form;
		target.add(incentiveform);
	}
	
	/**
	 * Create the history popup
	 * @param id
	 * @return
	 */
	private SRSModalWindow createIncentiveModificationWindow(String id) {
		final SRSModalWindow window = new SRSModalWindow(id) {

			@Override
			public String getModalSessionIdentifier() {
				return "AGREEMENT.INCENTIVES1122-";
			}
			
		};
		
		window.setTitle("Incentive Details");		
		// Create the page
		window.setPageCreator(new ModalWindow.PageCreator() {
			private static final long serialVersionUID = 1L;
			public Page createPage() {	
				IncentiveDetailDTO workOnDTO = null;
				if(currentPopupType == PopupType.ADD){
					workOnDTO = currentSelectedAdd;
				}else{
					workOnDTO = currentSelectedModify;
				}
				popupEditState = getEditState();
				if (getEditState().isViewOnly()|| getEditState() == EditStateType.AUTHORISE || existingIncentiveRequest) {
					//make the popup have a view only state
					popupEditState = EditStateType.VIEW;
				}				
				currentPopup = new IncentiveModificationPage(window,popupEditState,workOnDTO,panelModel);;
				return currentPopup;
			}			
		});	
//		Close window call back
		window.setWindowClosedCallback(new ModalWindow.WindowClosedCallback() {
			private static final long serialVersionUID = 1L;			
			public void onClose(AjaxRequestTarget target) {
				IncentiveModificationPopupModel model = (IncentiveModificationPopupModel) 
				window.getSessionModelForPage();
				
				window.clearModalPageModelInSession();
				
				if(model.isModalWizardSucces()){
					//process the changes made
					if(currentPopupType == PopupType.ADD){
						
						copyObjects(target, model.getSelectedItem(), currentSelectedAdd);
						//add the selected item into the in use list						
						panelModel.addIncentive(currentSelectedAdd);						
					} else {
						copyObjects(target, model.getSelectedItem(), currentSelectedModify);
					}
					//now refresh the incentive sections
					reDrawForm(target);
				}				
			}			
		});
		// Initialise window settings
		window.setMinimalHeight(300);
		window.setInitialHeight(300);
		window.setMinimalWidth(500);
		window.setInitialWidth(500);
		window.setMaskType(MaskType.SEMI_TRANSPARENT);
		window.setCssClassName(ModalWindow.CSS_CLASS_GRAY);	
		window.setOutputMarkupId(true);
		window.setOutputMarkupPlaceholderTag(true);
//		window.setPageMapName("IncentiveModificationWindowMap");
		return window;
	}	
		
	/**
	 * Copy the returned object into an existing one
	 * 
	 * @param target
	 * @param selectedItem
	 * @param destinationItem
	 */
	protected void copyObjects(AjaxRequestTarget target, IncentiveDetailDTO selectedItem, IncentiveDetailDTO destinationItem) {

//		//we update the origional object using dozer so if objects change all fields are copied
//		try {
//			mapper.map(
//					selectedItem,
//					destinationItem);
//		} catch (Exception e1) {
//			logger.error("Error occured while copying the incentive object",e1);
//
//			FeedbackPanel feedBack = getFeedBackPanel();
//			feedBack.error("Could not copy the changes, if this persists please call support");
//
//			if(feedBack != null){
//				target.add(feedBack);
//			}
//			return;
//		}
	}

	/*
	 * Add in all extra detail to the pagemodel that this panel requires
	 */
	private void initPageModel(){
//		if(panelModel.get == null){
//			error("Page Model should never be null, Please call support if you continue seeing this error");
//			pageModel = new MaintainAgreementPageModel(new AgreementDTO(),new ValidAgreementValuesDTO()); 
//		}
	}	
	
	
	/**
	 * Form used for the panel so we can add validations and on submit method calls
	 * @author DZS2610
	 *
	 */
	public class IncentiveForm extends Form {
		private static final long serialVersionUID = 1L;
		@SuppressWarnings("unchecked")
		public IncentiveForm(String id) {
			super(id);
			
			WebMarkupContainer addGroupTH = new WebMarkupContainer("addGroupTH");
			add(addGroupTH);
			
			WebMarkupContainer addGroupTD = new WebMarkupContainer("addGroupTD");
			add(addGroupTD);
			
			WebMarkupContainer addGroupButtons = new WebMarkupContainer("addGroupButtons");
			add(addGroupButtons);					
			
			//go through the incentives and put it into the various sections of repeaters
			modifyGroup = new RadioGroup<IncentiveDetailDTO>("modifyGroup", new Model<IncentiveDetailDTO>());
			modifyGroup.setOutputMarkupId(true);			
			add(modifyGroup);
			addGroup = new RadioGroup<IncentiveDetailDTO>("addGroup", new Model<IncentiveDetailDTO>());
			addGroup.setOutputMarkupId(true);	
			if(getEditState() == EditStateType.AUTHORISE){
				//hide all non needed info
				addGroupTH.setVisible(false);
				addGroupTD.setVisible(false);				
				addGroupButtons.setVisible(false);
			}
			
			addGroupTD.add(addGroup);	
			modifyGroup.add(new AjaxFormChoiceComponentUpdatingBehavior(){				
				private static final long serialVersionUID = 1L;

				@Override
				protected void onUpdate(AjaxRequestTarget target) {					
					currentSelectedModify = modifyGroup.getModel().getObject();
				}				
			});		
			addGroup.add(new AjaxFormChoiceComponentUpdatingBehavior(){				
				private static final long serialVersionUID = 1L;

				@Override
				protected void onUpdate(AjaxRequestTarget target) {					
					currentSelectedAdd = addGroup.getModel().getObject();					
				}				
			});	
			modifyGroup.add(createIncentivesSelection("intIncentives",panelModel.getCurrentAndFutureIntermediaryLevelIncentives(),panelModel.getAgreementStatusString(),IncentiveTypes.CURRENT,true));
			modifyGroup.add(createIncentivesSelection("manIncentives",panelModel.getCurrentAndFutureManagerLevelIncentives(),panelModel.getAgreementStatusString(),IncentiveTypes.CURRENT,true));
			addGroup.add(createIncentivesSelection("intIncentives",panelModel.getAvailableIntermediaryLevelIncentives(),panelModel.getAgreementStatusString(),IncentiveTypes.AVAILABLE,false));
			addGroup.add(createIncentivesSelection("manIncentives",panelModel.getAvailableManagerLevelIncentives(),panelModel.getAgreementStatusString(),IncentiveTypes.AVAILABLE,false));
			addGroupButtons.add(createAddButton("addButton"));
			add(createEditButton("editButton"));
			this.setOutputMarkupId(true);
		}			
	}		
	
	/**
	 * Create the repeating view for the incentives
	 * @return
	 */
	private RepeatingView createIncentivesSelection(String id, List<IncentiveDetailDTO> incentiveDetail,String agmtStatus, IncentiveTypes types,boolean includeDatesDisplay){		
		RepeatingView repeater = new RepeatingView(id);
		//add the options to the the repeater		
		int counter = 0;
		
		if(incentiveDetail != null && incentiveDetail.size() >0){
			for(final IncentiveDetailDTO incentive : incentiveDetail){
				if(incentive instanceof EstablishAllowanceDTO){
					if(agmtStatus != null && !ISRSConstants.STATUS_ACTIVE.equals(agmtStatus) && types != IncentiveTypes.CURRENT)
						continue;
				}
				Item item = new Item(repeater.newChildId(),counter);
				Radio<IncentiveDetailDTO> incetiveChoice = new Radio<IncentiveDetailDTO>("incentive", new Model<IncentiveDetailDTO>(incentive));
				if(getEditState().isViewOnly()|| getEditState() == EditStateType.AUTHORISE || existingIncentiveRequest){
					//incetiveChoice.setRenderBodyOnly(true);
				}
				//add description to selection
				String datesSelection = "";
				if(includeDatesDisplay){
					datesSelection = " ["+((incentive.getStartDate() != null) ? dateFormatter.format(incentive.getStartDate()) : "No Start Date")
					+" - "+((incentive.getEndDate() != null) ? dateFormatter.format((incentive.getEndDate())) : "Infinity")+"]";
				}

				incetiveChoice.add(new Label("name",((incentive.getIncentiveProductKindType() != null) ? incentive.getIncentiveProductKindType().getDescription() : "") 
						+ datesSelection));			
				item.add(incetiveChoice);
				counter++;
				repeater.add(item);

			}
		}
		
		if(incentiveDetail == null || incentiveDetail.size() == 0 || counter == 0){
			Item item = new Item(repeater.newChildId(),0);
			//return a lable with a message
			Radio incetiveChoice = new Radio("incentive", new Model(""));	
			item.add(incetiveChoice.setRenderBodyOnly(true));
			incetiveChoice.add(new Label("name","There are no available incentives to choose"));
			repeater.add(item);
		}
				
		return repeater;
	}
	
	/**
	 * Create the modify button
	 * @param id
	 * @return
	 */
	private Button createAddButton(String id){	
		final Button button = new Button(id);
		button.add(new AjaxFormComponentUpdatingBehavior("click") {
			private static final long serialVersionUID = 0L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				
				if(currentSelectedAdd == null)
				{
					error("Please select an Incentive to Add");
					target.add(AgreementIncentivesPanel.this.getFeedBackPanel());
					return;
				}
				currentPopupType = PopupType.ADD;
				modificationWindow.show(target);
				target.add(AgreementIncentivesPanel.this.getFeedBackPanel());
			}
		});
		if (getEditState().isViewOnly()|| getEditState() == EditStateType.AUTHORISE || existingIncentiveRequest ||isNoAvailableIncentivesToAdd()) {
			button.setEnabled(false);
			//button.setVisible(false);
		}
		button.setOutputMarkupId(true);
		button.setOutputMarkupPlaceholderTag(true);
		return button;		
	}
	
	//This is to DISABLE the Add button if there is no Available Incentives to add on Incentive Details GUI-pritam-01/07/13
	
	private boolean isNoAvailableIncentivesToAdd() {
		
		return (this.panelModel.getAvailableIncentivesDetails() == null || this.panelModel.getAvailableIncentivesDetails().size() == 0);
		
	}

	/**
	 * Create the edit button
	 * @param id
	 * @return
	 */
	private Button createEditButton(String id){	
		final Button button = new Button(id);
		button.add(new AjaxFormComponentUpdatingBehavior("click") {
			private static final long serialVersionUID = 0L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				if(currentSelectedModify == null)
				{
					error("Please select an Incentive to View Detail/Modify");
					target.add(AgreementIncentivesPanel.this.getFeedBackPanel());
					return;
				}
				
				currentPopupType = PopupType.MODIFY;
				modificationWindow.show(target);
				target.add(AgreementIncentivesPanel.this.getFeedBackPanel());
			}
		});
		if (getEditState().isViewOnly() || getEditState() == EditStateType.AUTHORISE || existingIncentiveRequest) {
			//we just change the name to view			
			button.add(new AttributeModifier("value","View Detail"));			
			//button.setEnabled(false);
			//button.setVisible(false);
		}
		button.setOutputMarkupId(true);
		button.setOutputMarkupPlaceholderTag(true);
		return button;		
	}
	
	
	/**
	 * Get the logged in user
	 * @return
	 */
	private ISessionUserProfile getLoggedInUser(){
		return SRSAuthWebSession.get().getSessionUser();
	}
	
	/**
	 * Set the feedback panel to use for errors
	 * @param feedBackPanel
	 */
	public void setFeedBackPanel(FeedbackPanel feedBackPanel) {
		this.feedBackPanel = feedBackPanel;
	}

	public Class getPanelClass() {		
		return AgreementIncentivesPanel.class;
	}	
	

	
	
	



}
