package za.co.liberty.web.pages.contactdetail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
//import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow.MaskType;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.validation.IFormValidator;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import com.inmethod.grid.DataProviderAdapter;
import com.inmethod.grid.IGridColumn;
import com.inmethod.grid.SizeUnit;
import com.inmethod.grid.column.AbstractColumn;

import za.co.liberty.business.guicontrollers.contactdetail.IContactDetailsGUIController;
import za.co.liberty.database.enums.DatabaseEnumHelper;
import za.co.liberty.dto.databaseenum.PostalAddressTypeDBEnumDTO;
import za.co.liberty.dto.party.PartyDTO;
import za.co.liberty.dto.party.contactdetail.AddressDTO;
import za.co.liberty.dto.party.contactdetail.CommunicationPreferenceDTO;
import za.co.liberty.dto.party.contactdetail.ContactDetailDTO;
import za.co.liberty.dto.party.contactdetail.ContactPreferenceDTO;
import za.co.liberty.dto.party.contactdetail.ContactPreferenceWrapperDTO;
import za.co.liberty.dto.party.contactdetail.EmailAddressDTO;
import za.co.liberty.dto.party.contactdetail.PhysicalAddressDTO;
import za.co.liberty.dto.party.contactdetail.TelDetailDTO;
import za.co.liberty.dto.party.contactdetail.WebAddressDTO;
import za.co.liberty.dto.party.contactdetail.type.BusinessProcessType;
import za.co.liberty.dto.party.contactdetail.type.ContactDetailType;
import za.co.liberty.dto.party.contactdetail.type.UsageType;
import za.co.liberty.exceptions.data.ValidationException;
import za.co.liberty.interfaces.agreements.requests.RequestKindType;
import za.co.liberty.web.data.enums.ComponentType;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.data.pages.IModalMaintenancePageModel;
import za.co.liberty.web.pages.contactdetail.model.ContactDetailsPanelModel;
import za.co.liberty.web.pages.interfaces.ISecurityPanel;
import za.co.liberty.web.pages.panels.BasePanel;
import za.co.liberty.web.pages.panels.HelperPanel;
import za.co.liberty.web.wicket.markup.html.form.SRSDateField;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGrid;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGrid.GridSizeUnit;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGridColumn;
import za.co.liberty.web.wicket.markup.html.grid.SRSGridRowSelectionCheckBox;
import za.co.liberty.web.wicket.markup.repeater.data.ListDataProvider;
import za.co.liberty.web.wicket.modal.SRSModalWindow;

/**
 * Contact details panel capturing all the contact details of a party
 * 
 * @author DZS2610
 * 
 */
public class ContactDetailsPanel extends BasePanel implements ISecurityPanel{		
	
	@SuppressWarnings("unused")
	private FeedbackPanel feedBackPanel;
	
	private ContactDetailsPanelModel panelModel;
	
	private SRSDataGrid addressGrid;
	
	private SRSDataGrid telGrid;
	
	private SRSDataGrid commPreferenceGrid;
	
	private SRSModalWindow addressPopup;	
	
	/**
	 * Kept here for edit popup
	 */
	private AddressDTO currentAddressDTO = null;	
	
	private AddressDTO beforeImageAddress = null;
	
	private List<FormComponent> validationComponents = new ArrayList<FormComponent>();
	
//	private Form nodeForm;
	@SuppressWarnings("unused")
	private Form contactForm;
	
	/**
	 * If true secure selection will be available in the contact details selection
	 */
	private boolean includeSecureSelection;
	
	//will be true if there is an unauthorised secure contact details request
	private boolean outstandingSecureRequest;
	
//	will be true if there is an unauthorised contact details request
	@SuppressWarnings("unused")
	private boolean outstandingContactRequest;
	
	/**
	 * If set to true then extra validations will be done for agreement purposes
	 */
	private boolean agreementCreation;
	
	//Keep the current address popup page
	private AddressPage currentPage;	
	
	private EditStateType addressEditState;

	private static final Logger logger = Logger.getLogger(ContactDetailsPanel.class);
	
	private static final long serialVersionUID = 1L;	

	/**
	 * Default constructor, will not have secure 
	 * @param arg0
	 */
	public ContactDetailsPanel(String id, List<ContactPreferenceDTO> contactPreferenceDetails, List<CommunicationPreferenceDTO> commPefs, EditStateType editState,
			FeedbackPanel feedBackPanel, boolean agreementCreation, Page parentPage, boolean isPA) {
		this(id, contactPreferenceDetails, commPefs, editState, feedBackPanel,false,agreementCreation,parentPage, isPA);
	}
	
	/**
	 * 
	 * @param arg0
	 */
	public ContactDetailsPanel(String id, List<ContactPreferenceDTO> contactPreferenceDetails, List<CommunicationPreferenceDTO> commPrefs, EditStateType editState, FeedbackPanel feedBackPanel, 
			boolean includeSecureSelection, boolean agreementCreation, Page parentPage, boolean isPA) {
		super(id,editState,parentPage);
		this.includeSecureSelection = includeSecureSelection;
		this.feedBackPanel = feedBackPanel;	
		this.agreementCreation = agreementCreation;
//		check for outstanding requests requiring authorisation
		for(RequestKindType type : getOutStandingRequestKinds()){
			if(type == RequestKindType.MaintainSecureContactDetails){
				outstandingSecureRequest = true;
			}else if(type == RequestKindType.MaintainContactDetails){
				outstandingContactRequest = true;
			}
		}		
		panelModel = new ContactDetailsPanelModel(contactPreferenceDetails, commPrefs);
		initPanelModel();				
		init(isPA);
	}
	
	/**
	 * Get this panels currently configured addresses
	 * @return
	 */
	public List<ContactPreferenceDTO> getCurrentContactPreferenceDetails(){
		return panelModel.getPanelData();
	}
	
	/**
	 * Initialize the panel model
	 *
	 */
	@SuppressWarnings("unchecked")
	private void initPanelModel(){
		//List<PostalAddressTypeDBEnumDTO> postalTypes = new ArrayList<PostalAddressTypeDBEnumDTO>();
		
// #WICKETTEST #WICKETFIX	:MSK commented below entry to skip DB and intr 	
		List<PostalAddressTypeDBEnumDTO> postalTypes = DatabaseEnumHelper.getDatabaseDTO(PostalAddressTypeDBEnumDTO.class);
	
				
		//need to exclude STREET and CLUSTER
		List<PostalAddressTypeDBEnumDTO> postalTypesNew = new ArrayList<PostalAddressTypeDBEnumDTO>(postalTypes.size());
		for(PostalAddressTypeDBEnumDTO type : postalTypes){
			if(!type.getName().equalsIgnoreCase("STREET") && !type.getName().equalsIgnoreCase("CLUSTER") && !type.getName().equalsIgnoreCase("RESERVED VALUE")){
				postalTypesNew.add(type);
			}
		}
		panelModel.setPostalTypes(postalTypesNew);		
		//panelModel.setTelTypes(DatabaseEnumHelper.getDatabaseDTO(ElectronicCommunicationTypeDBEnumDTO.class));
		
		 
		List<UsageType> allowedUsageTypes = new ArrayList<UsageType>(Arrays.asList(UsageType.values()));
		if(!includeSecureSelection || outstandingSecureRequest){
			allowedUsageTypes.remove(UsageType.SECURE);
		}
		panelModel.setAllowedUsages(allowedUsageTypes);			
	}	
	
	
	/**
	 * Initialize the panel
	 *
	 */
	private void init(boolean isPA){
		//create the radio group above
		//defaultSelectionGroup = new RadioGroup("defaultSelectionGroup",new PropertyModel(this,"defaultAddress"));
		//this.add(defaultSelectionGroup);
		this.add(contactForm = createContactForm("contactForm", isPA));
	}
	
	
	public class ContactForm extends Form {
		private static final long serialVersionUID = 1L;
		public ContactForm(String id, boolean isPA) {
			super(id);
			this.add(addressGrid = createAddressgrid("addressGrid"));
			this.add(createAddAddressButton("addAddressButton"));
			this.add(createRemoveAddressButton("removeAddressButton"));
			this.add(telGrid = createTelgrid("telGrid"));
			this.add(createAddTelButton("addTelButton"));
			this.add(createRemoveTelButton("removeTelButton"));
			this.add(commPreferenceGrid = createCommPreferenceGrid("commPreference", isPA));
			this.add(createAddCommPrefButton("addCommPrefButton"));
			this.add(createRemoveCommPrefButton("removeCommPrefButton"));
			this.add(addressPopup = createModalWindow("addressPopup"));
			this.add(new IFormValidator() {

				private static final long serialVersionUID = 1L;

				public FormComponent[] getDependentFormComponents() {
					return validationComponents.toArray(new FormComponent[] {});
				}

				public void validate(final Form arg0) {		
					if (logger.isDebugEnabled())
						logger.debug("Validated called " + getEditState());
					if (getEditState().isViewOnly()) {
						return;
					}
					List<ContactPreferenceDTO> newContactPrefs = panelModel.getPanelData();
					List<CommunicationPreferenceDTO> commPreferences = panelModel.getCommunicationPreferenceDetails();
					boolean validate = true;
					for(FormComponent comp : validationComponents){
						if(!comp.isValid()){
							validate = false;
							break;
						}
					}
					if(validate){
						IContactDetailsGUIController controller = panelModel.getContactDetailsController();
						PartyDTO tempParty = new PartyDTO();							
						tempParty.setContactPreferences(new ContactPreferenceWrapperDTO(newContactPrefs));	
						tempParty.setCommunicationPreferences(commPreferences);
						try {
							controller.validateContactDetail(tempParty,agreementCreation,includeSecureSelection);
						} catch (ValidationException e) {
							for(String error : e.getErrorMessages()){
								if (logger.isDebugEnabled())
									logger.debug("Validation error:"+error);
								getFeedBackPanel().error(error);
							}
						}
					}
				}			
			});
		}
		
	}



	private Form createContactForm(String id, boolean isPA) {
		ContactForm form = new ContactForm(id, isPA);		
		return form;
		
	}
	
	/**
	 * Create the modal window
	 * 
	 * @param id
	 * @return
	 */
	private SRSModalWindow createModalWindow(String id) {		
		final SRSModalWindow window = new SRSModalWindow(id) {

			String pId = null;
			
			@Override
			public String getModalSessionIdentifier() {
				return "ContactDetailsPanel.add.address121-";
			}

			@Override
			public boolean isAllowMultiplePageModels() {
				// True as this window must support multiple pages.
				return true;
			}

			@Override
			public String getParentPageID() {
				if (pId ==null) {
					Page p = ContactDetailsPanel.this.getParentPage();
					if (p!=null) {
						pId = p.getPageId()+"";
					} else {
						pId = ""+getRandomId();
					}	
				}
				return pId;
			}

			
		};
		window.setTitle("Address");		
		// Create the page
		window.setPageCreator(new ModalWindow.PageCreator() {
			private static final long serialVersionUID = 1L;
			public Page createPage() {	
				try {
					beforeImageAddress = currentAddressDTO;
					currentAddressDTO = (AddressDTO) beforeImageAddress.clone();
				} catch (CloneNotSupportedException e) {
					logger.error("Internal error when adding address",e);
					//should never happen	
					//will do nothing as we will check later if beforeImageAddress is null;
					beforeImageAddress = null;
				}
				currentPage = new AddressPage(panelModel,addressEditState,currentAddressDTO,window);
				return currentPage;
			}			
		});	
		window.setWindowClosedCallback(new ModalWindow.WindowClosedCallback() {
			private static final long serialVersionUID = 1L;			
			public void onClose(AjaxRequestTarget target) {
				IModalMaintenancePageModel<AddressDTO> model = window.getSessionModelForPage();
				window.clearModalPageModelInSession();
				
				if (model.isModalWizardSucces() && model.getSelectedItem() != null) {
					if (addressEditState.isAdd()) {
						panelModel.addAddress(model.getSelectedItem());
					} else {
						// Remove previous as we kept the original
						panelModel.removeAddress(beforeImageAddress);
						
						// Add new one returned from modal
						panelModel.addAddress(model.getSelectedItem());
					}
					processAddressPopupClosed(target);
				} else {
					getFeedBackPanel().info("Cancel Add Address");
					target.add(getFeedBackPanel());
				}
	
			}			
		});
		
		
		// Initialise window settings
		window.setMinimalHeight(500);
		window.setInitialHeight(500);
		window.setMinimalWidth(350);
		window.setInitialWidth(350);
		window.setMaskType(MaskType.SEMI_TRANSPARENT);
		window.setCssClassName(ModalWindow.CSS_CLASS_GRAY);	
		window.setOutputMarkupId(true);
		window.setOutputMarkupPlaceholderTag(true);
		window.setCookieName("EditAddressPageMap");//window.setPageMapName("EditAddressPageMap");
		return window;
	}
	
	/**
	 * Process the address popup window once closed
	 *
	 */
	private void processAddressPopupClosed(AjaxRequestTarget target){
		//refresh the address list
		panelModel.sortContactPointsIntoPreferences();
		target.add(addressGrid);//target.addComponent(addressGrid);
	}
	
	/**
	 * Create the add address button
	 * @return
	 */
	private Button createAddAddressButton(String id){
		Button button = new Button(id);
		if(this.getEditState().isViewOnly()){
			button.setVisible(false);
		}
		button.add(new AjaxFormComponentUpdatingBehavior("click"){
			private static final long serialVersionUID = 1L;
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
					currentAddressDTO = new PhysicalAddressDTO();
					currentAddressDTO.setEffectiveFrom(new Date());
					addressEditState = EditStateType.ADD;
					addressPopup.show(target);
			}			
		});
		
		button.setOutputMarkupId(true);
		button.setOutputMarkupPlaceholderTag(true);
		return button;
	}
	
	/**
	 * Create the remove address button
	 * @return
	 */
	private Button createRemoveAddressButton(String id){
		Button button = new Button(id);
		if(this.getEditState().isViewOnly()){
			button.setVisible(false);
		}
		button.add(new AjaxFormComponentUpdatingBehavior("click"){
			private static final long serialVersionUID = 1L;
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				List<Object> addresses = addressGrid.getSelectedItemObjects();
				for (Object address : addresses) {
					panelModel.removeAddress((AddressDTO) address);
				}
				target.add(addressGrid);//target.addComponent(addressGrid);
			}			
		});		
		button.setOutputMarkupId(true);
		button.setOutputMarkupPlaceholderTag(true);
		return button;
	}
	
	/**
	 * Create the add address button
	 * @return
	 */
	private Button createAddTelButton(String id){
		Button button = new Button(id);
		if(this.getEditState().isViewOnly()){
			button.setVisible(false);
		}
		button.add(new AjaxFormComponentUpdatingBehavior("click"){
			private static final long serialVersionUID = 1L;
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
					TelDetailDTO tel = new TelDetailDTO();
					tel.setEffectiveFrom(new Date());
					panelModel.addTelDetail(tel);
					target.add(telGrid);//target.addComponent(telGrid);
			}			
		});
		
		button.setOutputMarkupId(true);
		button.setOutputMarkupPlaceholderTag(true);
		return button;
	}
	
	/**
	 * Create the remove address button
	 * @return
	 */
	private Button createRemoveTelButton(String id){
		Button button = new Button(id);
		if(this.getEditState().isViewOnly()){
			button.setVisible(false);
		}
		button.add(new AjaxFormComponentUpdatingBehavior("click"){
			private static final long serialVersionUID = 1L;
			@Override
			protected void onUpdate(AjaxRequestTarget target) {				
				List<Object> tels = telGrid.getSelectedItemObjects();
				for (Object tel : tels) {
					panelModel.removeTelDetail((ContactDetailDTO) tel);
				}
				target.add(telGrid);//target.addComponent(telGrid);
			}			
		});
		
		button.setOutputMarkupId(true);
		button.setOutputMarkupPlaceholderTag(true);
		return button;
	}
	/**
	 * Create the add CommPref button
	 * @return
	 */
	private Button createAddCommPrefButton(String id){
		Button button = new Button(id);
		if(this.getEditState().isViewOnly()){
			button.setVisible(false);
		}
		button.add(new AjaxFormComponentUpdatingBehavior("click"){
			private static final long serialVersionUID = 1L;
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
					CommunicationPreferenceDTO commPref = new CommunicationPreferenceDTO();
					commPref.setEffectiveFrom(new Date());
					panelModel.addCommPrefDetail(commPref);
					target.add(commPreferenceGrid);//target.addComponent(commPreferenceGrid);
			}			
		});
		
		button.setOutputMarkupId(true);
		button.setOutputMarkupPlaceholderTag(true);
		return button;
	}
	
	/**
	 * Create the remove CommPref button
	 * @return
	 */
	private Button createRemoveCommPrefButton(String id){
		Button button = new Button(id);
		if(this.getEditState().isViewOnly()){
			button.setVisible(false);
		}
		button.add(new AjaxFormComponentUpdatingBehavior("click"){
			private static final long serialVersionUID = 1L;
			@Override
			protected void onUpdate(AjaxRequestTarget target) {				
				List<Object> commPref = commPreferenceGrid.getSelectedItemObjects();
				for (Object commPrefs : commPref) {
					panelModel.removeCommPrefDetail((CommunicationPreferenceDTO)commPrefs);
				}
				target.add(commPreferenceGrid);//target.addComponent(commPreferenceGrid);
			}			
		});
		
		button.setOutputMarkupId(true);
		button.setOutputMarkupPlaceholderTag(true);
		return button;
	}
	
	/**
	 * Create the address grid
	 * @param id
	 * @return
	 */
	private SRSDataGrid createAddressgrid(String id){		
		List<AddressDTO> addresses = panelModel.getAddresses();		
		//SRSDataGrid grid = new SRSDataGrid(id, new DataProviderAdapter(
		//		new SortableListDataProvider<AddressDTO>(
		//				addresses)), getAddressColumns(), null);
		List<AddressDTO> nonSelectable = new ArrayList<AddressDTO>();
		
		if(outstandingSecureRequest){
			//all secure must be non selectable
			for(AddressDTO address : addresses){
				if(address.getUsage() == UsageType.SECURE){
					nonSelectable.add(address);
				}
			}
		}
		
		SRSDataGrid grid = new SRSDataGrid(id, new DataProviderAdapter(
				new ListDataProvider<AddressDTO>(
						addresses)), getAddressColumns(),getEditState(), nonSelectable);		
		grid.setCleanSelectionOnPageChange(false);
		grid.setClickRowToSelect(false);
		grid.setAllowSelectMultiple(true);
//		if (getEditState()==EditStateType.AUTHORISE) {
//			grid.setGridWidth(550, GridSizeUnit.PIXELS);
//		} else {
			grid.setGridWidth(99, GridSizeUnit.PERCENTAGE);
//		}
		grid.setRowsPerPage(15);
		grid.setContentHeight(120, SizeUnit.PX);
		grid.setAutoCalculateTableHeight(getEditState().isViewOnly());
		return grid;
	}
	
	
	/**
	 * Get the columns for the address grid
	 * @return
	 */
	private List<IGridColumn> getAddressColumns() {
		Vector<IGridColumn> cols = new Vector<IGridColumn>(7);		
		if (!getEditState().isViewOnly()) {
			SRSGridRowSelectionCheckBox col = new SRSGridRowSelectionCheckBox(
					"checkBox");
			cols.add(col.setInitialSize(30));
		}
		//all cols display only, add button will bring up popup
		cols.add(new SRSDataGridColumn<AddressDTO>(
				"usage", new Model("Usage"), "usage",
				"usage", getEditState()).setInitialSize(70));
		//add in defualt selection for business types
		cols.add(new SRSDataGridColumn<AddressDTO>(
				"defaultAddress", new Model("Default"), "defaultAddress",
				"defaultAddress", getEditState()){
		private static final long serialVersionUID = 1L;

		@Override
		public Panel newCellPanel(WebMarkupContainer parent, String componentId, IModel rowModel, String objectProperty, EditStateType state, final AddressDTO data) {
			if(getEditState().isViewOnly() && data.getUsage() != UsageType.BUSINESS){
				Label lab = new Label("value","");
				return HelperPanel.getInstance(componentId, lab);
			}else{				
				//business type so give radio to select default
				CheckBox check = new CheckBox("value",new PropertyModel(data,objectProperty));
				check.add(new AjaxFormComponentUpdatingBehavior("click"){
					private static final long serialVersionUID = 1L;

					@Override
					protected void onUpdate(AjaxRequestTarget target) {
						//update the value
						//must also remove other selections and update grid
						if(panelModel.getAddresses().size() > 1){
							for(AddressDTO address : panelModel.getAddresses()){
								if(address != data){
									address.setDefaultAddress(false);
								}
							}
							if(target != null){
								target.add(addressGrid);//target.addComponent(addressGrid);
							}
						}
					}					
				});
				if(getEditState().isViewOnly()){
					check.setEnabled(false);
				}
				HelperPanel radioPanel = HelperPanel.getInstance(componentId, check);
				radioPanel.add(new AttributeModifier("align","center"));//radioPanel.add(new SimpleAttributeModifier("align","center"));
				return radioPanel;
			}
		}		
			
		}.setInitialSize(50));
		
		cols.add(new SRSDataGridColumn<AddressDTO>(
				"type", new Model("Type"), "type",
				"type", getEditState()).setInitialSize(100));
		
		cols.add(new SRSDataGridColumn<AddressDTO>(
				"isPostal", new Model("IsPostal"), "isPostal",
				"isPostal", getEditState()){
				private static final long serialVersionUID = 1L;
				@Override
				public Panel newCellPanel(WebMarkupContainer parent, String componentId, IModel rowModel, String objectProperty, EditStateType state, AddressDTO data) {
					CheckBox check = new CheckBox("value",new PropertyModel(data,objectProperty));
					check.setEnabled(false);
					HelperPanel ret =HelperPanel.getInstance(componentId, check);
					ret.add(new AttributeModifier("align","center"));//(new SimpleAttributeModifier("align","center"));
					return ret;
				}			
		}.setInitialSize(50));
		
		SRSDataGridColumn col = new SRSDataGridColumn<AddressDTO>(
				"description", new Model("Description"), "description",
				"description", getEditState());
		
		if(getEditState() == EditStateType.ADD){
			col.setInitialSize(190);
		}else{
			col.setInitialSize(250);
		}
		col.setWrapText(true);
		cols.add(col);			
		cols.add(new SRSDataGridColumn<AddressDTO>(
				"effectiveFrom", new Model("Start Date"), "effectiveFrom",
				"effectiveFrom", getEditState()).setInitialSize(70));
		cols.add(new SRSDataGridColumn<AddressDTO>(
				"effectiveTo", new Model("End Date"), "effectiveTo",
				"effectiveTo", getEditState()).setInitialSize(70));
		if(!getEditState().isViewOnly()){
			//add edit button
			cols.add(new AbstractColumn("edit", new Model("Edit")){				
				private static final long serialVersionUID = 1L;
				@Override
				public Component newCell(WebMarkupContainer parent, String componentId, IModel rowModel) {
					final AddressDTO address = (AddressDTO) rowModel.getObject();
					if(address.getUsage() == UsageType.SECURE 
							&& (!ContactDetailsPanel.this.includeSecureSelection || outstandingSecureRequest)){
						return new EmptyPanel(componentId);
					}else{				
					//if(address.getOid() != 0){
						Button searchButton = new Button("value", new Model("Edit"));	
						searchButton.add(new AjaxFormComponentUpdatingBehavior("click"){									
							private static final long serialVersionUID = 1L;
							@Override
							protected void onUpdate(AjaxRequestTarget target) {
								currentAddressDTO = address;
								addressEditState = EditStateType.MODIFY;
								addressPopup.show(target);										
							}									
						});
						return HelperPanel.getInstance(componentId,searchButton);	
					//}else{
					//	return new EmptyPanel(componentId);
					//}		
					}
				}				
			}.setInitialSize(45));
		}
		return cols;
		
	}
	
	/**
	 * Create the address grid
	 * @param id
	 * @return
	 */
	private SRSDataGrid createTelgrid(String id){
		List<ContactDetailDTO> tels = panelModel.getTelDetails();		
		//SRSDataGrid grid = new SRSDataGrid(id, new DataProviderAdapter(
		//		new SortableListDataProvider<TelDetailDTO>(
		//				tels)), getTelColumns(), null);
		SRSDataGrid grid = new SRSDataGrid(id, new DataProviderAdapter(
				new ListDataProvider<ContactDetailDTO>(
						tels)), getTelColumns(), getEditState(),getNonSelectableTelDetails(tels));
		grid.setCleanSelectionOnPageChange(false);
		grid.setClickRowToSelect(false);
		grid.setAllowSelectMultiple(true);
//		if (getEditState()==EditStateType.AUTHORISE) {
//			grid.setGridWidth(550, GridSizeUnit.PIXELS);
//		} else {
			grid.setGridWidth(99, GridSizeUnit.PERCENTAGE);
//		}		
		grid.setRowsPerPage(15);
		grid.setContentHeight(100, SizeUnit.PX);
		grid.setAutoCalculateTableHeight(getEditState().isViewOnly());
		return grid;
	}

	/**
	 * Get the columns for the telephone/email grid
	 * @return
	 */
	private List<IGridColumn> getTelColumns() {
		Vector<IGridColumn> cols = new Vector<IGridColumn>(7);
		if (!getEditState().isViewOnly()) {
			SRSGridRowSelectionCheckBox col = new SRSGridRowSelectionCheckBox(
					"checkBox");
			cols.add(col.setInitialSize(30));
		}
		//all cols display only, add button will bring up popup
		cols.add(new SRSDataGridColumn<ContactDetailDTO>(
				"usage", new Model("Usage"), "usage",
				"usage", getEditState()){
					private static final long serialVersionUID = 1L;

					@Override
					public Panel newCellPanel(WebMarkupContainer parent, String componentId, IModel rowModel, String objectProperty, EditStateType state, ContactDetailDTO data) {
						if(state == EditStateType.VIEW || (data.getUsage() == UsageType.SECURE && (!ContactDetailsPanel.this.includeSecureSelection || outstandingSecureRequest))){
							return super.newCellPanel(parent, componentId, rowModel, objectProperty, state,
								data);
						}else{			
							
							//create usage dropdown
							HelperPanel panel = createDropdownField(data, "Usage", componentId, panelModel.getAllowedUsages() ,  new ChoiceRenderer("typeName", "typeID"), "Select", true, new EditStateType[]{EditStateType.ADD,EditStateType.MODIFY});
							if(panel.getEnclosedObject() instanceof DropDownChoice){
								panel.getEnclosedObject().add(new AjaxFormComponentUpdatingBehavior("change"){
									private static final long serialVersionUID = 1L;
									@Override
									protected void onUpdate(AjaxRequestTarget arg0) {									
										//update the contact details list
										panelModel.sortContactPointsIntoPreferences();
									}									
								});
								validationComponents.add((DropDownChoice)panel.getEnclosedObject());		
							}
							return panel;
						}
					}					
			
		}.setInitialSize(95));
		cols.add(new SRSDataGridColumn<ContactDetailDTO>(
				"type", new Model("Type"), "type",
				"type", getEditState()){
					private static final long serialVersionUID = 1L;

					@Override
					public Panel newCellPanel(WebMarkupContainer parent, String componentId, IModel rowModel, String objectProperty, EditStateType state, final ContactDetailDTO data) {
						if(state == EditStateType.VIEW || data.getOid() != 0){
							return super.newCellPanel(parent, componentId, rowModel, objectProperty, state,
								data);
						}else{
							List<ContactDetailType> types = new ArrayList<ContactDetailType>(2);
							types.add(ContactDetailType.TELEPHONE_NUMBER);
							types.add(ContactDetailType.CELLPHONE_NUMBER);	
							types.add(ContactDetailType.FAX_NUMBER);
							types.add(ContactDetailType.EMAIL_ADDRESS);	
							types.add(ContactDetailType.WEB_LINKED_IN);
							
							//create type dropdown
							HelperPanel panel = createDropdownField(data, "Type", componentId, types,  new ChoiceRenderer("typeName", "typeName"), 
									null, true, new EditStateType[]{EditStateType.ADD,EditStateType.MODIFY});
							
							if(panel.getEnclosedObject() instanceof DropDownChoice){
								panel.getEnclosedObject().add(new AjaxFormComponentUpdatingBehavior("change"){									
									private static final long serialVersionUID = 1L;
									@Override
									protected void onUpdate(AjaxRequestTarget target) {										
										telTypeSelected(data,data.getType(),target);					
									}									
								});
								validationComponents.add((DropDownChoice)panel.getEnclosedObject());								
							}
							
							return panel;
						}
					}					
			
		}.setInitialSize(150));
		cols.add(new SRSDataGridColumn<ContactDetailDTO>(
				"code", new Model("Code"), "code",
				"code", getEditState()){
			private static final long serialVersionUID = 1L;

			@Override
			public Panel newCellPanel(WebMarkupContainer parent, String componentId, IModel rowModel, String objectProperty, EditStateType state, ContactDetailDTO data) {
				if(state == EditStateType.VIEW || data instanceof EmailAddressDTO || data instanceof WebAddressDTO || (data.getUsage() == UsageType.SECURE 
						&& (!ContactDetailsPanel.this.includeSecureSelection  || outstandingSecureRequest))){
					
					if (data instanceof WebAddressDTO) {
						return new EmptyPanel(componentId);
					} else {
						return super.newCellPanel(parent, componentId, rowModel, objectProperty, state,
								data);
					}
					// TODO change here
				}else{									
					HelperPanel code =  createPageField(data, "Code", componentId, ComponentType.TEXTFIELD, true,true, new EditStateType[]{EditStateType.ADD,EditStateType.MODIFY});
					Component comp = code.getEnclosedObject();
					if(comp instanceof TextField){
						comp.add(new AttributeModifier("size","5"));//comp.add(new SimpleAttributeModifier("size","5"));	
						comp.add(new AttributeModifier("maxlength","4"));//comp.add(new SimpleAttributeModifier("maxlength","4"));
						validationComponents.add((TextField)comp);						
					}
					return code;
				}
			}	
		}.setInitialSize(60));
		
		AbstractColumn col = new SRSDataGridColumn<ContactDetailDTO>(
				"number", new Model("Number/Email/URL"), "number",
				"number", getEditState()){
			private static final long serialVersionUID = 1L;

			@Override
			public Panel newCellPanel(WebMarkupContainer parent, String componentId, IModel rowModel, String objectProperty, EditStateType state, ContactDetailDTO data) {
				if(state == EditStateType.VIEW || (data.getUsage() == UsageType.SECURE && (!ContactDetailsPanel.this.includeSecureSelection  || outstandingSecureRequest))){
					return super.newCellPanel(parent, componentId, rowModel, objectProperty, state,
						data);
				}else{
					HelperPanel panel = createPageField(data, "Number/Email/URL", componentId, ComponentType.TEXTFIELD, true,true, new EditStateType[]{EditStateType.ADD,EditStateType.MODIFY});
					if(panel.getEnclosedObject() instanceof TextField){
						if(data.getType() == ContactDetailType.EMAIL_ADDRESS){
							((TextField)panel.getEnclosedObject()).add(new AttributeModifier("size","25"));//(new SimpleAttributeModifier("size","25"));
							((TextField)panel.getEnclosedObject()).add(new AttributeModifier("maxlength","100"));//(new SimpleAttributeModifier("maxlength","100"));
						} else if (data.getType() == ContactDetailType.WEB_LINKED_IN) {
							((TextField)panel.getEnclosedObject()).add(new AttributeModifier("size","50"));//(new SimpleAttributeModifier("size","50"));
							((TextField)panel.getEnclosedObject()).add(new AttributeModifier("maxlength","100"));//(new SimpleAttributeModifier("maxlength","100"));
						}else{
							((TextField)panel.getEnclosedObject()).add(new AttributeModifier("size","8"));//(new SimpleAttributeModifier("size","8"));
							((TextField)panel.getEnclosedObject()).add(new AttributeModifier("maxlength","7"));//(new SimpleAttributeModifier("maxlength","7"));
						}												
						validationComponents.add((TextField)panel.getEnclosedObject());
					}					
					return panel;
				}
			}	
		};
//		col.setInitialSize(143);	
		col.setInitialSize(200);
		if(getEditState().isViewOnly()){
			col.setInitialSize(200);
			col.setWrapText(true);
		}
		cols.add(col);
		cols.add(new SRSDataGridColumn<ContactDetailDTO>(
				"effectiveFrom", new Model("Start Date"), "effectiveFrom",
				"effectiveFrom", getEditState()){
			private static final long serialVersionUID = 1L;

			@Override
			public Panel newCellPanel(WebMarkupContainer parent, String componentId, IModel rowModel, String objectProperty, EditStateType state, ContactDetailDTO data) {
				//TODO Removing selection on modify as this still needs to be done				
				//Remove modify from below once done
				if(state == EditStateType.VIEW || data.getOid() != 0){
					return super.newCellPanel(parent, componentId, rowModel, objectProperty, state,
						data);
				}else{						
					HelperPanel panel =  createPageField(data, "Start Date", componentId, ComponentType.DATE_SELECTION_TEXTFIELD, true,true, new EditStateType[]{EditStateType.ADD,EditStateType.MODIFY});
					if(panel.getEnclosedObject() instanceof SRSDateField){
						SRSDateField startdte = (SRSDateField)panel.getEnclosedObject();
						startdte.add(new AttributeModifier("readonly","true"));//startdte.add(new SimpleAttributeModifier("readonly","true"));
						validationComponents.add(startdte);
					}	
					if (panel.getEnclosedObject() instanceof SRSDateField) {
						panel.getEnclosedObject().add(
					((SRSDateField)panel.getEnclosedObject()).newDatePicker());
					}
					return panel;
				}
			}	
		}.setInitialSize(120));
		cols.add(new SRSDataGridColumn<ContactDetailDTO>(
				"effectiveTo", new Model("End Date"), "effectiveTo",
				"effectiveTo", getEditState()){
			private static final long serialVersionUID = 1L;

			@Override
			public Panel newCellPanel(WebMarkupContainer parent, String componentId, IModel rowModel, String objectProperty, EditStateType state, ContactDetailDTO data) {
				//TODO Removing selection on modify as this still needs to be done				
				//Remove modify from below once done				
				if(state == EditStateType.VIEW || state == EditStateType.MODIFY  || (data.getUsage() == UsageType.SECURE && !ContactDetailsPanel.this.includeSecureSelection) ){
					return super.newCellPanel(parent, componentId, rowModel, objectProperty, state,
						data);
				}else{
					return createPageField(data, "End Date", componentId, ComponentType.DATE_SELECTION_TEXTFIELD, false,true, new EditStateType[]{EditStateType.ADD,EditStateType.MODIFY});
				}
			}	
		}.setInitialSize(120));
		return cols;
		
	}
	
	private SRSDataGrid createCommPreferenceGrid(String id, boolean isPA) {
		List<CommunicationPreferenceDTO> commPreferences = panelModel.getCommunicationPreferenceDetails();
		
		List<CommunicationPreferenceDTO> nonSelectable = new ArrayList<CommunicationPreferenceDTO>();
	/*	if(outstandingSecureRequest){
			//all secure must be non selectable
			for(CommunicationPreferenceDTO commPreference : commPreferences){
				if(commPreference.getUsage() == UsageType.SECURE){
					nonSelectable.add(commPreferences);
				}
			}
		}*/
		SRSDataGrid grid = new SRSDataGrid(id, new DataProviderAdapter(new ListDataProvider<CommunicationPreferenceDTO>(commPreferences)), getCommPreferenceColumns(isPA), getEditState(), commPreferences);
		grid.setCleanSelectionOnPageChange(false);
		grid.setClickRowToSelect(false);
		grid.setAllowSelectMultiple(true);
		grid.setGridWidth(99, GridSizeUnit.PERCENTAGE);
		grid.setRowsPerPage(15);
		grid.setContentHeight(100, SizeUnit.PX);
		grid.setAutoCalculateTableHeight(getEditState().isViewOnly());
		return grid;
	}
	
	/**
	 * Get the columns for communication preference grid
	 * @return
	 */
	private List<IGridColumn> getCommPreferenceColumns(final boolean isPA) {
		Vector<IGridColumn> cols = new Vector<IGridColumn>(4);
		if (!getEditState().isViewOnly()) {
			SRSGridRowSelectionCheckBox col = new SRSGridRowSelectionCheckBox(
					"checkBox");
			cols.add(col.setInitialSize(30));
		}
		cols.add(new SRSDataGridColumn<CommunicationPreferenceDTO>("businessProcessType", new Model("Business Process"), "businessProcessType", "businessProcessType.process", getEditState()) {
			private static final long serialVersionUID = 1L;

			@Override
			public Panel newCellPanel(WebMarkupContainer parent, String componentId, IModel rowModel, String objectProperty, EditStateType state, final CommunicationPreferenceDTO data) {
				if (state == EditStateType.VIEW || data.getOid() != 0){
					return super.newCellPanel(parent, componentId, rowModel, objectProperty, state, data);
				}
				else{
					
					List<BusinessProcessType> types = new ArrayList<BusinessProcessType>();
					
					types.addAll(Arrays.asList(BusinessProcessType.values()));
					/*
					types.add(BusinessProcessType.DISBURSEMENT);
					types.add(BusinessProcessType.VALUE_SERVICES);
					*/
					//create type dropdown
					HelperPanel panel = createDropdownField(data, "Business Process Type", componentId, types , new ChoiceRenderer("process", "process"), "Select", true, new EditStateType[]{EditStateType.ADD,EditStateType.MODIFY});
					
					if(panel.getEnclosedObject() instanceof DropDownChoice){
						panel.getEnclosedObject().add(new AjaxFormComponentUpdatingBehavior("change"){									
							private static final long serialVersionUID = 1L;
							@Override
							protected void onUpdate(AjaxRequestTarget target) {										
								businessProcessTypeSelected(new CommunicationPreferenceDTO(), data.getBusinessProcessType(), target);	
							}									
						});
						validationComponents.add((DropDownChoice)panel.getEnclosedObject());								
					}
				return panel;
				}
			}
		}.setInitialSize(100));
		
		//Add SMS Option Column;
		cols.add(new SRSDataGridColumn<CommunicationPreferenceDTO>("optedInSMS", new Model("SMS"), "optedInSMS", "optedInSMS", getEditState()) {
		
			@Override
			public Panel newCellPanel(WebMarkupContainer parent, String componentId, IModel rowModel, String objectProperty, EditStateType state, final CommunicationPreferenceDTO data) {

				// business type so give radio to select default
				CheckBox check = new CheckBox("value", new PropertyModel(data, objectProperty));
				check.setEnabled(false);
				check.add(new AjaxFormComponentUpdatingBehavior("click") {
					private static final long serialVersionUID = 1L;
					
					@Override
					protected void onUpdate(AjaxRequestTarget target) {
						// update the value
						// must also remove other selections and update grid
						/*
						 * if(panelModel.getAddresses().size() > 1){
						 * for(AddressDTO address : panelModel.getAddresses()){
						 * if(address != data){
						 * address.setDefaultAddress(false); } } if(target !=
						 * null){ target.addComponent(addressGrid); } }
						 */
						//Set Feedback Panel to check if Cellphone number is correct
						if(data.getBusinessProcessType()!=null && data.getOptedInSMS() != null && data.getOptedInSMS().booleanValue() ==true){
							String message = null;
							List<ContactDetailDTO> contacts = panelModel.getTelDetails();
							for (ContactDetailDTO contact : contacts) {
								if(contact!=null && contact instanceof TelDetailDTO){
									TelDetailDTO dto = (TelDetailDTO) contact;
									if(dto.getType() != null && dto.getType().equals(ContactDetailType.CELLPHONE_NUMBER)){
										message = "Please note that the SMSes will be sent to your current cellphone number (" + dto.getCode() + " " + dto.getNumber() +")" + " as per your telephone/ email details, if this is not correct please update accordingly";
										break;
									}
								}					
							}
							if(message!=null){
							info(message);
							target.add(getFeedBackPanel());//target.addComponent(getFeedBackPanel());
							}
						}
						//Do not display anything if Business Process Type is not opted in
						if(data.getBusinessProcessType()!=null && data.getOptedInSMS() != null && data.getOptedInSMS().booleanValue() ==false){
							//info(" ");
							target.add(getFeedBackPanel());//target.addComponent(getFeedBackPanel());
						}
					}
				});
				if (getEditState().isViewOnly()) {
					check.setEnabled(false);
				}
				if(isPA){
			// #WICKETTEST #WICKETFIX
					//check.add(new AttributeModifier("title", true, new Model(("Please note that the functionality to opt-in/opt-out for SMS is not available as there are currently no SMSes for this process."))));
					check.add(new AttributeModifier("title", new Model(("Please note that the functionality to opt-in/opt-out for SMS is not available as there are currently no SMSes for this process."))));
				}
				else if(!isPA && !getEditState().isViewOnly()) {
					check.setEnabled(true);
				}
				HelperPanel radioPanel = HelperPanel.getInstance(componentId, check);
				radioPanel.add(new AttributeModifier("align", "center"));//(new SimpleAttributeModifier("align", "center"));
				return radioPanel;
			}

		}.setInitialSize(50));
		
		//Add Email Option Column
		cols.add(new SRSDataGridColumn<CommunicationPreferenceDTO>("optedInEmail", new Model("Email"), "optedInEmail", "optedInEmail", getEditState()) {

			@Override
			public Panel newCellPanel(WebMarkupContainer parent, String componentId, IModel rowModel, String objectProperty, EditStateType state, final CommunicationPreferenceDTO data) {
				// business type so give radio to select default
				CheckBox check = new CheckBox("value", new PropertyModel(data, objectProperty));
				check.setEnabled(false);
				check.add(new AjaxFormComponentUpdatingBehavior("click") {
					private static final long serialVersionUID = 1L;

					@Override
					protected void onUpdate(AjaxRequestTarget target) {
						// update the value
						// must also remove other selections and update grid
						/*
						 * if(panelModel.getAddresses().size() > 1){
						 * for(AddressDTO address : panelModel.getAddresses()){
						 * if(address != data){
						 * address.setDefaultAddress(false); } } if(target !=
						 * null){ target.addComponent(addressGrid); } }
						 */
						//Set Feedback Panel to check if Cellphone number is correct
						if(data.getBusinessProcessType()!=null && data.getOptedInEmail() != null && data.getOptedInEmail().booleanValue() ==true){
							String message = null;
							List<ContactDetailDTO> contacts = panelModel.getTelDetails();
							if (contacts!= null){
							for (ContactDetailDTO contact : contacts) {
								if(contact!=null && contact instanceof TelDetailDTO){
									TelDetailDTO dto = (TelDetailDTO) contact;
									if(dto.getType() != null && dto.getType().equals(ContactDetailType.EMAIL_ADDRESS)){
										message = "Please note that the emails will be sent to your current email address: (" + dto.getNumber() +") " + "as per your telephone/ email details, if this is not correct please update accordingly";
										break;
									}
								}					
							}
							if(message!=null){
							info(message);
							target.add(getFeedBackPanel());//target.addComponent(getFeedBackPanel());
							}
						}}
						//Do not display anything if Business Process Type is not opted in
						if(data.getBusinessProcessType()!=null && data.getOptedInEmail() != null && data.getOptedInEmail().booleanValue() ==false){
							//info(" ");
							target.add(getFeedBackPanel());//target.addComponent(getFeedBackPanel());
						}
					}
				});
				if (getEditState().isViewOnly()) {
					check.setEnabled(false);
				}
				check.setEnabled(isPA);
				if(!isPA){
				// #WICKETTEST #WICKETFIX	
					//check.add(new AttributeModifier("title", true, new Model(("Please note that the functionality to opt-in/opt-out for email is not available as there are currently no emails for this process."))));
					check.add(new AttributeModifier("title",  new Model(("Please note that the functionality to opt-in/opt-out for email is not available as there are currently no emails for this process."))));
				}
				HelperPanel radioPanel = HelperPanel.getInstance(componentId, check);
				radioPanel.add(new AttributeModifier("align", "center"));//radioPanel.add(new SimpleAttributeModifier("align", "center"));
				return radioPanel;
			}

		}.setInitialSize(50));
		return cols;
		
	}
	
	/**
	 * Change the telType
	 *
	 */
	private void telTypeSelected(ContactDetailDTO origional, ContactDetailType selectedType,AjaxRequestTarget target){
		boolean changed = false;
		if(selectedType == ContactDetailType.EMAIL_ADDRESS && !(origional instanceof EmailAddressDTO)){
			for(ContactDetailDTO detail : panelModel.getTelDetails()){
				if(detail == origional){
					//panelModel.removeTelDetail(detail);
					EmailAddressDTO email = new EmailAddressDTO();
					email.setUsage(origional.getUsage());
					email.setEffectiveFrom(origional.getEffectiveFrom());
					panelModel.replaceTelNumber(detail,email);
					changed = true;
					break;
				}
			}
		}else if((selectedType == ContactDetailType.TELEPHONE_NUMBER || selectedType == ContactDetailType.FAX_NUMBER 
				|| selectedType == ContactDetailType.CELLPHONE_NUMBER) && (origional.getClass().equals(TelDetailDTO.class))){
			for(ContactDetailDTO detail : panelModel.getTelDetails()){
				if(detail == origional){
					//panelModel.removeTelDetail(origional);
					TelDetailDTO tel = new TelDetailDTO();
					tel.setType(selectedType);
					tel.setUsage(origional.getUsage());
					tel.setEffectiveFrom(origional.getEffectiveFrom());
					//panelModel.addTelDetail(tel);
					panelModel.replaceTelNumber(origional,tel);
					changed = true;
					break;
				}
			}
		}else if(selectedType == ContactDetailType.WEB_LINKED_IN && !(origional instanceof WebAddressDTO)){
			for(ContactDetailDTO detail : panelModel.getTelDetails()){
				if(detail == origional){
					//panelModel.removeTelDetail(origional);
					WebAddressDTO tel = new WebAddressDTO();
					tel.setType(selectedType);
					tel.setUsage(origional.getUsage());
					tel.setEffectiveFrom(origional.getEffectiveFrom());
					//panelModel.addTelDetail(tel);
					panelModel.replaceTelNumber(origional,tel);
					changed = true;
					break;
				}
			}
		}
		
		
		if(changed){
			target.add(this.telGrid);//target.addComponent(this.telGrid);
		}
	}
	
	/**
	 * Change the Business Process Type
	 *
	 */
	private void businessProcessTypeSelected(CommunicationPreferenceDTO origional, BusinessProcessType selectedType,AjaxRequestTarget target){
		boolean changed = false;
		if(selectedType == BusinessProcessType.UNDERWRITING && !(origional instanceof CommunicationPreferenceDTO)){
			for(CommunicationPreferenceDTO detail : panelModel.getCommunicationPreferenceDetails()){
				if(detail == origional){
					CommunicationPreferenceDTO underwriting = new CommunicationPreferenceDTO();
					panelModel.replaceCommPref(detail, underwriting);
					changed = true;
					break;
				}
		}
		}
		
		if(selectedType == BusinessProcessType.DISBURSEMENT && !(origional instanceof CommunicationPreferenceDTO)){
			for(CommunicationPreferenceDTO detail : panelModel.getCommunicationPreferenceDetails()){
				if(detail == origional){
					CommunicationPreferenceDTO disbursement = new CommunicationPreferenceDTO();
					panelModel.replaceCommPref(detail, disbursement);
					changed = true;
					break;
				}
		}
		}
		
		if(selectedType == BusinessProcessType.VALUE_SERVICES && !(origional instanceof CommunicationPreferenceDTO)){
			for(CommunicationPreferenceDTO detail : panelModel.getCommunicationPreferenceDetails()){
				if(detail == origional){
					CommunicationPreferenceDTO value = new CommunicationPreferenceDTO();
					panelModel.replaceCommPref(detail, value);
					changed = true;
					break;
				}
		}
		}
		
		if(selectedType == BusinessProcessType.BONUS_PAYMENT && !(origional instanceof CommunicationPreferenceDTO)){
			for(CommunicationPreferenceDTO detail : panelModel.getCommunicationPreferenceDetails()){
				if(detail == origional){
					CommunicationPreferenceDTO value = new CommunicationPreferenceDTO();
					panelModel.replaceCommPref(detail, value);
					changed = true;
					break;
				}
		}
		}
		
		if(changed){
			target.add(this.commPreferenceGrid);//target.addComponent(this.commPreferenceGrid);
		}
	}
	
	/**
	 * set the feedback panel to use for errors
	 * @param feedBackPanel
	 */
	public void setFeedBackPanel(FeedbackPanel feedBackPanel) {
		this.feedBackPanel = feedBackPanel;
	}

	public Class getPanelClass() {		
		return ContactDetailsPanel.class;
	}	
	
	/**
	 * if the secure contact details must be excluded then we just disable selection
	 * @return
	 */
	private List<ContactDetailDTO> getNonSelectableTelDetails(List<ContactDetailDTO> tels){
		if(includeSecureSelection || tels == null && !outstandingSecureRequest){
			return null;
		}		
		List<ContactDetailDTO> details = new ArrayList<ContactDetailDTO>();
		for(ContactDetailDTO tel : tels){
			if(tel.getUsage() == UsageType.SECURE){
				details.add(tel);
			}
		}
		return details;		
	}
}

