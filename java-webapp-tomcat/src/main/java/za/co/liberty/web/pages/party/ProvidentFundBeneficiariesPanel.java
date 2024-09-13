package za.co.liberty.web.pages.party;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.naming.NamingException;

import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.AttributeModifier;//org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow.MaskType;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import com.inmethod.grid.DataProviderAdapter;
import com.inmethod.grid.IGridColumn;
import com.inmethod.grid.SizeUnit;

import za.co.liberty.business.guicontrollers.partymaintenance.IPartyMaintenanceController;
import za.co.liberty.dto.agreement.maintainagreement.ProvidentFundBeneficiaryDetailsDTO;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.helpers.lookup.ServiceLocator;
import za.co.liberty.web.data.enums.ComponentType;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.pages.panels.HelperPanel;
import za.co.liberty.web.pages.party.model.MaintainPartyPageModel;
import za.co.liberty.web.pages.party.model.ProvidentFundBeneficiariesModel;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGrid;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGrid.GridSizeUnit;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGridColumn;
import za.co.liberty.web.wicket.markup.html.grid.SRSGridRowSelectionCheckBox;
import za.co.liberty.web.wicket.markup.repeater.data.ListDataProvider;

public class ProvidentFundBeneficiariesPanel extends BasePartyDetailsPanel {
	@SuppressWarnings("unused")
	static final long serialVersionUID = 1L;
	
	
	private FeedbackPanel feedBackPanel;

	protected MaintainPartyPageModel pageModel;
	
	protected ProvidentFundBeneficiariesModel panelModel;

	private BeneficiaryDetailsForm pageForm;
	
	private transient IPartyMaintenanceController partyMaintenanceController;
	
	private List<ProvidentFundBeneficiaryDetailsDTO> beneficiaryDetails;

	private boolean displayFieldsFlag;

	private SRSDataGrid beneficiaryDetailsGrid;

	private boolean initialised;
	
	private List<FormComponent> validationComponents = new ArrayList<FormComponent>();
	

	/**
	 * Default constructor, will not have secure
	 * 
	 * @param arg0
	 */
	public ProvidentFundBeneficiariesPanel(String id, MaintainPartyPageModel pageModel, FeedbackPanel feedBackPanel,
			EditStateType editState, Page parentPage) {
		super(id, editState, parentPage);
		this.pageModel = pageModel;
		this.feedBackPanel = feedBackPanel;
		this.panelModel = pageModel.getProvidentFundBeneficiariesModel();
		init();

	}

	private void init() {
		if (!initialised) {
			initialised = true;
			this.add(pageForm = getBeneficiaryDetailsForm("beneficiariesForm"));
		}
	}

	@Override
	protected void onBeforeRender() {
		if (!initialised) {
			initialised = true;
			// initialize the page model with the agreement data
			initModelForBeneficiaryDetails();
			/*
			 * List<RequestKindType> unAuthRequests =
			 * getOutStandingRequestKinds(); //check for existing requests FIRST
			 * as other panels use variables set here for (RequestKindType kind
			 * : unAuthRequests) {
			 * 
			 * if(kind == RequestKindType.MaintainAssociatedAgreements){
			 * existingAssAgmtRequest = true; break; } }
			 */

			//add(getBeneficiaryDetailsForm());
			/*add(addWindow = createModalWindow("addNewWizzardWindow", "Add Beneficiary Details"));
			add(editWindow = createModalWindow("editWizzardWindow", "Edit Beneficiary Details"));*/

		}

		if (feedBackPanel == null) {
			feedBackPanel = this.getFeedBackPanel();
		}
		super.onBeforeRender();
	};

	@SuppressWarnings("unchecked")
	private void initModelForBeneficiaryDetails() {

		if (pageModel == null || pageModel.getPartyDTO() == null) {
			error("Page Model should never be null, Please call support if you continue seeing this error");
			this.beneficiaryDetails = (List<ProvidentFundBeneficiaryDetailsDTO>) new ProvidentFundBeneficiaryDetailsDTO();
			return;
		}

		// setTaxDetailsModel();
	}
	
	/**
	 * Load the 
	 */

	/**
	 * Get the main page form
	 * 
	 * @return
	 */
	private BeneficiaryDetailsForm getBeneficiaryDetailsForm(String id) {
		if (pageForm == null) {
			pageForm = new BeneficiaryDetailsForm(id);
			/*pageForm.add(beneficiaryDetailsGrid = getBeneficiaryDetailsGrid("providentFundBeneficiaryGrid"));*/
			
		}
		return pageForm;
	}

	/**
	 * Get the data grid for the Beneficiary details
	 * 
	 * @return the data grid
	 */
	private SRSDataGrid getBeneficiaryDetailsGrid(String id) {
		if (beneficiaryDetailsGrid == null) {
			beneficiaryDetails = panelModel.getProvidentFundBeneficiariesList();
			if (panelModel.getProvidentFundBeneficiariesList() == null) {
				panelModel.setProvidentFundBeneficiariesList(new ArrayList<ProvidentFundBeneficiaryDetailsDTO>());
				beneficiaryDetails = panelModel.getProvidentFundBeneficiariesList();
			}

			setFlag(beneficiaryDetails);
			// cloneOriginalList(beneficiaryDetails);
			beneficiaryDetailsGrid = new SRSDataGrid(id, new DataProviderAdapter(new ListDataProvider<ProvidentFundBeneficiaryDetailsDTO>(beneficiaryDetails)), getColumns(), getEditState());
			beneficiaryDetailsGrid.setOutputMarkupId(true);
			beneficiaryDetailsGrid.setCleanSelectionOnPageChange(false);
			beneficiaryDetailsGrid.setClickRowToSelect(false);
			beneficiaryDetailsGrid.setAllowSelectMultiple(false);
			beneficiaryDetailsGrid.setGridWidth(100, GridSizeUnit.PERCENTAGE);
			beneficiaryDetailsGrid.setRowsPerPage(10);
			beneficiaryDetailsGrid.setContentHeight(100, SizeUnit.PX);
		}
		return beneficiaryDetailsGrid;
	}

	private void setFlag(List<ProvidentFundBeneficiaryDetailsDTO> list) {
		if (list != null && list.size() != 0)
			this.displayFieldsFlag = true;
		else
			this.displayFieldsFlag = false;
	}

	/**
	 * Get the columns for the Beneficiary Details
	 * 
	 * @return the data columns
	 */
	private List<IGridColumn> getColumns() {
		List<IGridColumn> columns = new ArrayList<IGridColumn>();
		
		if (!getEditState().isViewOnly()) {
			SRSGridRowSelectionCheckBox col = new SRSGridRowSelectionCheckBox("checkBox");
			columns.add(col.setInitialSize(30));
		}

		 //Beneficiary Full Name Column
		columns.add(createFullNameColumn());

		 // Beneficiary ID Number Column
		columns.add(createIDNumberColumn());

		 //Beneficiary Date Of Birth Column
		columns.add(createDateOfBirthColumn());

		
		 //Beneficiary Percentage allocation Column
		columns.add(createPercatageColumn());

		 //Beneficiary Relationship Column
		columns.add(createRelationshipColumn());

		return columns;

	}

	/**
	 * @return
	 */
	private SRSDataGridColumn<ProvidentFundBeneficiaryDetailsDTO> createRelationshipColumn() {
		SRSDataGridColumn<ProvidentFundBeneficiaryDetailsDTO> relationship = new SRSDataGridColumn<ProvidentFundBeneficiaryDetailsDTO>("relationship", new Model<String>("Relationship"), "relationship", getEditState()){
			private static final long serialVersionUID = 1L;

			@Override
			public Panel newCellPanel(WebMarkupContainer parent, String componentId, IModel rowModel, String objectProperty, EditStateType state, ProvidentFundBeneficiaryDetailsDTO data) {
				if(getEditState() == EditStateType.VIEW){
					return super.newCellPanel(parent, componentId, rowModel, objectProperty, state, data);
				}else{
					HelperPanel panel = createPageField(data, "Relationship", componentId, ComponentType.TEXTFIELD, true, true, new EditStateType[]{EditStateType.ADD,EditStateType.MODIFY});
					if(panel.getEnclosedObject() instanceof TextField){
						validationComponents.add((TextField)panel.getEnclosedObject());
					}					
					return panel;
				}
			}	
		};
		relationship.setMaxSize(200);
		return relationship;
	}

	/**
	 * @return
	 */
	private SRSDataGridColumn<ProvidentFundBeneficiaryDetailsDTO> createPercatageColumn() {
		SRSDataGridColumn<ProvidentFundBeneficiaryDetailsDTO> percentAlloc = new SRSDataGridColumn<ProvidentFundBeneficiaryDetailsDTO>("percentage", new Model<String>("Percentage"), "percentage", getEditState()){
			private static final long serialVersionUID = 1L;

			@Override
			public Panel newCellPanel(WebMarkupContainer parent, String componentId, IModel rowModel, String objectProperty, EditStateType state, ProvidentFundBeneficiaryDetailsDTO data) {
				if(getEditState() == EditStateType.VIEW){
					return super.newCellPanel(parent, componentId, rowModel, objectProperty, state, data);
				}else{
					HelperPanel panel = createPageField(data, "Percentage", componentId, ComponentType.TEXTFIELD, true, true, new EditStateType[]{EditStateType.ADD,EditStateType.MODIFY});
					if(panel.getEnclosedObject() instanceof TextField){
						validationComponents.add((TextField)panel.getEnclosedObject());
					}					
					return panel;
				}
			}	
		};
		percentAlloc.setMaxSize(200);
		return percentAlloc;
	}

	/**
	 * @return
	 */
	private SRSDataGridColumn<ProvidentFundBeneficiaryDetailsDTO> createDateOfBirthColumn() {
		SRSDataGridColumn<ProvidentFundBeneficiaryDetailsDTO> dateOfBirth = new SRSDataGridColumn<ProvidentFundBeneficiaryDetailsDTO>("benefeciaryDateOfBirth", new Model<String>("Date Of Birth"), "benefeciaryDateOfBirth", getEditState()){
			private static final long serialVersionUID = 1L;
			
			@Override
			public Panel newCellPanel(WebMarkupContainer parent, String componentId, IModel rowModel, String objectProperty, EditStateType state, ProvidentFundBeneficiaryDetailsDTO data) {
				if(state == EditStateType.VIEW || data.getOid() != 0){
					return super.newCellPanel(parent, componentId, rowModel, objectProperty, state, data);
				}else{						
					HelperPanel panel =  createPageField(data, "Date Of Birth", componentId, ComponentType.DATE_SELECTION_TEXTFIELD, true,true, new EditStateType[]{EditStateType.ADD,EditStateType.MODIFY});
					if(panel.getEnclosedObject() instanceof TextField){
						TextField birthdte = (TextField)panel.getEnclosedObject(); 
						//startdte.add(new SimpleAttributeModifier("readonly","true"));
						validationComponents.add(birthdte);
					}					
					return panel;
				}
			}	
		};	
		dateOfBirth.setMaxSize(200);
		return dateOfBirth;
	}

	/**
	 * @return
	 */
	private SRSDataGridColumn<ProvidentFundBeneficiaryDetailsDTO> createIDNumberColumn() {
		SRSDataGridColumn<ProvidentFundBeneficiaryDetailsDTO> idNumberColumn = new SRSDataGridColumn<ProvidentFundBeneficiaryDetailsDTO>("benefeciaryIDNumber", new Model<String>("ID/Passport Number"), "benefeciaryIDNumber", getEditState()){
			private static final long serialVersionUID = 1L;

			@Override
			public Panel newCellPanel(WebMarkupContainer parent, String componentId, IModel rowModel, String objectProperty, EditStateType state, ProvidentFundBeneficiaryDetailsDTO data) {
				if(getEditState() == EditStateType.VIEW){
					return super.newCellPanel(parent, componentId, rowModel, objectProperty, state, data);
				}else{
					HelperPanel panel = createPageField(data, "ID/Passport Number", componentId, ComponentType.TEXTFIELD, true, true, new EditStateType[]{EditStateType.ADD,EditStateType.MODIFY});
					if(panel.getEnclosedObject() instanceof TextField){
						validationComponents.add((TextField)panel.getEnclosedObject());
					}					
					return panel;
				}
			}	
		};
		idNumberColumn.setMaxSize(200);
		return idNumberColumn;
	}

	/**
	 * Create Beneficiary Full Name Column
	 * 
	 * @return {@link SRSDataGridColumn}
	 */
	private SRSDataGridColumn<ProvidentFundBeneficiaryDetailsDTO> createFullNameColumn() {
		SRSDataGridColumn<ProvidentFundBeneficiaryDetailsDTO> fullNameColumn = new SRSDataGridColumn<ProvidentFundBeneficiaryDetailsDTO>("benefeciaryFullName", new Model<String>("Full Name"), "benefeciaryFullName", "benefeciaryFullName", getEditState()) {
			private static final long serialVersionUID = 1L;

			@Override
			public Panel newCellPanel(WebMarkupContainer parent, String componentId, IModel rowModel, String objectProperty, EditStateType state, ProvidentFundBeneficiaryDetailsDTO data) {
				if(getEditState() == EditStateType.VIEW){
					return super.newCellPanel(parent, componentId, rowModel, objectProperty, state, data);
				}else{
					HelperPanel panel = createPageField(data, "Full Name", componentId, ComponentType.TEXTFIELD, true, true, new EditStateType[]{EditStateType.ADD,EditStateType.MODIFY});
					if(panel.getEnclosedObject() instanceof TextField){
						validationComponents.add((TextField)panel.getEnclosedObject());
					}					
					return panel;
				}
			}	
		};
		return fullNameColumn;
	}
	
	/**
	 * Create the Add beneficiary button
	 */
	private Button createAddBeneficiaryButton(String id){
		Button button = new Button(id);
		if(this.getEditState().isViewOnly()){
			button.setVisible(false);
		}
		button.add(new AjaxFormComponentUpdatingBehavior("click"){
			
			private static final long serialVersionUID = 1L;
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
					ProvidentFundBeneficiaryDetailsDTO beneficiaryDetailsDTO = new ProvidentFundBeneficiaryDetailsDTO();
					beneficiaryDetailsDTO.setEffectiveFrom(new Date());
					panelModel.addBeneficiaryDetail(beneficiaryDetailsDTO);
					target.add(beneficiaryDetailsGrid);//target.addComponent(beneficiaryDetailsGrid);
			}			
		});
		
		button.setOutputMarkupId(true);
		button.setOutputMarkupPlaceholderTag(true);
		return button;
	}
	
	/**
	 * Create the remove Beneficiary button
	 * @return
	 */
	private Button createRemoveBeneficiaryButton(String id) {
		Button button = new Button(id);
		if (this.getEditState().isViewOnly()) {
			button.setVisible(false);
		}
		button.add(new AjaxFormComponentUpdatingBehavior("click") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				List<Object> beneficiaries = beneficiaryDetailsGrid.getSelectedItemObjects();
				for (Object beneficiary : beneficiaries) {
					panelModel.removeBenficiary((ProvidentFundBeneficiaryDetailsDTO) beneficiary);
				}
				target.add(beneficiaryDetailsGrid);//target.addComponent(beneficiaryDetailsGrid);
			}
		});

		button.setOutputMarkupId(true);
		button.setOutputMarkupPlaceholderTag(true);
		return button;
	}

	/**
	 * Create the modal window for Add Beneficiary Details
	 * 
	 * @param id
	 * @return
	 */
	private ModalWindow createModalWindow(String id, String title) {
		final ModalWindow window = new ModalWindow(id);
		window.setTitle(title);
		// Initialise window settings
		window.setCookieName(title + "pageMap");//window.setPageMapName(title + "pageMap");
		window.setMinimalHeight(500);
		window.setInitialHeight(500);
		window.setMinimalWidth(1000);
		window.setInitialWidth(750);
		window.setMaskType(MaskType.SEMI_TRANSPARENT);
		window.setCssClassName(ModalWindow.CSS_CLASS_GRAY);
		window.setOutputMarkupId(true);
		window.setOutputMarkupPlaceholderTag(true);
		return window;
	}

	/**
	 * This class represents the page form to be added to the panel
	 * 
	 * @author pzm2509
	 */
	private class BeneficiaryDetailsForm extends Form<Object> {

		private static final long serialVersionUID = 1L;

		public BeneficiaryDetailsForm(String id) {
			super(id);
			this.add(beneficiaryDetailsGrid = getBeneficiaryDetailsGrid("providentFundBeneficiaryGrid"));
			this.add(createAddBeneficiaryButton("addBeneficiaryButton"));
			this.add(createRemoveBeneficiaryButton("removeBeneficiaryButton"));
		}
	}

	/**
	 * set the feedback panel to use for errors
	 * 
	 * @param feedBackPanel
	 */
	public void setFeedBackPanel(FeedbackPanel feedBackPanel) {
		this.feedBackPanel = feedBackPanel;
	}

	@Override
	public Class<ProvidentFundBeneficiariesPanel> getPanelClass() {
		// TODO Auto-generated method stub
		return ProvidentFundBeneficiariesPanel.class;
	}
	
	/**
	 * Get the PartyMaintenanceController bean
	 * @return
	 */
	public IPartyMaintenanceController getPartyMaintenanceController() {
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
