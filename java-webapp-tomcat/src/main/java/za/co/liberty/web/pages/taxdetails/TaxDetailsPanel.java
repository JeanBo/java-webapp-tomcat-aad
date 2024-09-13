package za.co.liberty.web.pages.taxdetails;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import javax.naming.NamingException;

import org.apache.commons.lang.SerializationUtils;
import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.AttributeModifier;//org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow.MaskType;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.validation.IFormValidator;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import com.inmethod.grid.DataProviderAdapter;
import com.inmethod.grid.IGridColumn;
import com.inmethod.grid.SizeUnit;

import za.co.liberty.business.guicontrollers.partymaintenance.IPartyMaintenanceController;
import za.co.liberty.business.guicontrollers.taxdetails.ITaxDetailsGUIController;
import za.co.liberty.dto.party.taxdetails.BBBEEDTO;
import za.co.liberty.dto.party.taxdetails.DirectivesDTO;
import za.co.liberty.dto.party.taxdetails.LabourBrokersDTO;
import za.co.liberty.dto.party.taxdetails.TaxDetailsDTO;
import za.co.liberty.dto.party.taxdetails.TrustCompDTO;
import za.co.liberty.dto.party.taxdetails.VatDTO;
import za.co.liberty.dto.userprofiles.ContextDTO;
import za.co.liberty.exceptions.data.DataNotFoundException;
import za.co.liberty.exceptions.data.ValidationException;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.helpers.lookup.ServiceLocator;
import za.co.liberty.interfaces.agreements.requests.RequestKindType;
import za.co.liberty.interfaces.party.PartyType;
import za.co.liberty.persistence.agreement.entity.DateRange;
import za.co.liberty.srs.util.DateUtil;
import za.co.liberty.web.data.enums.ComponentType;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.pages.panels.GUIFieldPanel;
import za.co.liberty.web.pages.panels.HelperPanel;
import za.co.liberty.web.pages.party.BasePartyDetailsPanel;
import za.co.liberty.web.pages.party.MaintainPartyPage;
import za.co.liberty.web.pages.party.model.MaintainPartyPageModel;
import za.co.liberty.web.pages.taxdetails.model.TaxDetailsPanelModel;
import za.co.liberty.web.wicket.markup.html.form.SRSDateField;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGrid;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGrid.GridSizeUnit;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGridColumn;
import za.co.liberty.web.wicket.markup.repeater.data.ListDataProvider;

/**
 * Panel containing only tax details
 * @author mzp0801
 */
public class TaxDetailsPanel extends BasePartyDetailsPanel{	

	private static final long serialVersionUID = 1L;
//	private static final Logger logger = Logger.getLogger(TaxDetailsPanel.class);
	
	@SuppressWarnings("unused")
	private FeedbackPanel feedBackPanel;
	
	private transient IPartyMaintenanceController partyMaintenanceController;
	private transient ITaxDetailsGUIController taxGUIController;
	private MaintainPartyPageModel pageModel;
	
	private GUIFieldPanel taxNumPanel;
	
	private SRSDataGrid directivesGrid;
	private SRSDataGrid vatdetailsGrid;
	private SRSDataGrid trustcompGrid;
	private SRSDataGrid labourbrokerGrid;
	private SRSDataGrid bbbeeGrid;
	
	private Button addDirectiveButton;
	private Button addVatButton;
	private Button addPSCButton;
	private Button addLabourButton;
	private Button addBBBEEButton;
	private Button directivesHistoryButton;
	private Button bbbeeHistoryButton;
	
	private ModalWindow directivesHistoryWindow;
	private ModalWindow bbbeeHistoryWindow;
	
	private List<VatDTO> vatdetails; 
	private List<DirectivesDTO> directdetails;
	private List<TrustCompDTO> trustcompdetails;
	private List<LabourBrokersDTO> labourbrokerdetails;
	private List<BBBEEDTO> bbbeedetails;
		
	private Collection<FormComponent> validationComponents = new ArrayList<FormComponent>();

	private TaxDetailsDTO taxDetailsDTO; 
	private TaxDetailsPanelModel taxDetailsPanelModel;
	private boolean initialized;
	
	@SuppressWarnings("unused")
	private TaxDetailsForm form;

	private boolean existingPayTaxRequest;
	private ContextDTO contextDTO;
	
	public enum TaxButtonType{ADDDIRECTIVES,ADDLABOURBROKER,ADDPSC,ADDVAT,ADDBBBEE };
	

/*******************************************************************************************************************/
/*	Initialise
/*******************************************************************************************************************/	
	public TaxDetailsPanel(String tab_panel_id, MaintainPartyPageModel model, EditStateType editState, FeedbackPanel feedpanel, Page parentPage) {
		super(tab_panel_id, editState,parentPage);
		if(parentPage instanceof MaintainPartyPage)
		{
			this.contextDTO = ((MaintainPartyPage)parentPage).getPageContextDTO();
		}
		this.pageModel = model;
		this.feedBackPanel = feedpanel;
		this.taxDetailsPanelModel = model.getTaxDetailsPanelModel() != null?model.getTaxDetailsPanelModel():new TaxDetailsPanelModel();
		this.taxDetailsDTO = this.taxDetailsPanelModel.getTaxDetailsDTO();		
	}
	
	
	@Override
	protected void onBeforeRender() {
		if(!initialized)
		{
			List<RequestKindType> unAuthRequests = getOutStandingRequestKinds();			
			//check for existing requests FIRST as other panels use variables set here
			for (RequestKindType kind : unAuthRequests) {
				if(kind == RequestKindType.MaintainPayrollTaxDetails)
					existingPayTaxRequest = true;
				
			}
			initModelForTaxDetails();
			add(form = new TaxDetailsForm("taxDetailsForm",pageModel));
			initialized = true;
		}
		
		if(feedBackPanel == null){			
			feedBackPanel = this.getFeedBackPanel();		
		}
		super.onBeforeRender();		
	}
	
	
	@SuppressWarnings("unchecked")
	private void initModelForTaxDetails(){
				
		if(pageModel == null || pageModel.getPartyDTO() == null)
		{
			error("Page Model should never be null, Please call support if you continue seeing this error");
			this.taxDetailsDTO = new TaxDetailsDTO();
			return;		
		}
		
		setTaxDetailsModel();		
	}
	
	private void setTaxDetailsModel()
	{
		if(getEditState() != EditStateType.AUTHORISE && this.taxDetailsDTO == null){	
		
		long partyOID = pageModel.getPartyDTO().getOid();
		String partyNotIntermedMsg = "Party is not an Intermediary! Tax Details not available!";
			
			try {					
				if(this.contextDTO == null || this.contextDTO.getAgreementContextDTO() == null || 
						this.contextDTO.getAgreementContextDTO().getAgreementNumber() == null)
					throw new ValidationException(partyNotIntermedMsg);
				
				this.taxDetailsDTO = getPartyMaintenanceController().getTaxDetailsDTO(partyOID,
						this.contextDTO.getAgreementContextDTO().getAgreementNumber(),false);				
				
			} catch (DataNotFoundException e) {
				error(e.getMessage());				
				
			}catch (ValidationException e) {
				for(String str:e.getErrorMessages()){
					error(str);					
				}			
			}finally{
				if(this.taxDetailsDTO == null)
					this.taxDetailsDTO = new TaxDetailsDTO();
			}
			this.taxDetailsPanelModel.setTaxDetailsDTO(this.taxDetailsDTO);
			this.taxDetailsPanelModel.setTaxDetailsBeforeImage((TaxDetailsDTO)SerializationUtils.clone(this.taxDetailsDTO));
			pageModel.setTaxDetailsPanelModel(this.taxDetailsPanelModel);			
		}
	}
	
	public class TaxDetailsForm extends Form {
		private static final long serialVersionUID = 1L;
		private TaxDetailsForm(String id,final MaintainPartyPageModel pageModel) {
			super(id);
		
			RepeatingView taxnumberPanel = new RepeatingView("taxnumberPanel");
			taxnumberPanel.add(getTaxNumPanel());
			add(taxnumberPanel);
			
			this.add(directivesGrid = createDirectivesgrid("directivesGrid"));
			this.add(addDirectiveButton = createAddDirectivesButton("addDirectivesButton"));
			this.add(directivesHistoryButton = createDirectivesHistoryButton("directivesHistoryButton"));
			this.add(directivesHistoryWindow = createDirectivesHistoryWindow("directivesHistoryWindow"));
			this.add(vatdetailsGrid = createVatdetailsgrid("vatdetailsGrid"));
			this.add(addVatButton = createAddVatButton("addVatButton"));
			this.add(createEndVatButton("endVatButton"));
			this.add(trustcompGrid = createTrustcompgrid("trustcompGrid"));
			this.add(addPSCButton = createAddTrustCompButton("addTrustCompButton"));
			this.add(labourbrokerGrid = createLabourbrokergrid("labourbrokerGrid"));
			this.add(addLabourButton = createAddLabourbrokerButton("addLabourBrokerButton"));
			this.add(bbbeeGrid = createBBBEEgrid("bbbeeGrid"));
			this.add(addBBBEEButton = createAddBBBEEButton("addBBBEEButton"));			
			this.add(bbbeeHistoryButton = createBBBEEHistoryButton("bbbeeHistoryButton"));
			this.add(bbbeeHistoryWindow = createBBBEEHistoryWindow("bbbeeHistoryWindow"));
			
			add(new IFormValidator(){
				
				private static final long serialVersionUID = 4703389463286567204L;
				
				public FormComponent[] getDependentFormComponents() {
					return validationComponents.toArray(new FormComponent[0]) ;
				}
				public void validate(Form arg0) {
					
					if(validationComponents.size() == 0)
						return;
				
					@SuppressWarnings("unused")
					TaxDetailsDTO taxDetailstoValidate = null;
					try {
						
						if(TaxDetailsPanel.this.contextDTO == null || TaxDetailsPanel.this.contextDTO.getPartyContextDTO() == null ||
								TaxDetailsPanel.this.contextDTO.getPartyContextDTO().getIntermediaryType() == null)
							throw new ValidationException("Party Context/Party Type cannot be null");
						
						String partyType = TaxDetailsPanel.this.contextDTO.getPartyContextDTO().getIntermediaryType();
						if(PartyType.PERSON.getDesc().equals(partyType.trim())){
							taxDetailsDTO.setPartyType(PartyType.PERSON);
						}else
						{
							taxDetailsDTO.setPartyType(PartyType.ORGANISATION);
							pageModel.getPartyDTO().getOid();
							long partyId = pageModel.getPartyDTO().getId();
							//List<PartyRoleType> check = new ArrayList<PartyRoleType>();
							
						}
																
						taxDetailstoValidate = getPartyMaintenanceController().validateAllTaxDetails(taxDetailsDTO, 
									TaxDetailsPanel.this.contextDTO.getAgreementContextDTO().getAgreementNumber(),
									TaxDetailsPanel.this.contextDTO.getAgreementContextDTO().getAgreementStartDate());
					} catch (ValidationException e) {
						for(String msg:e.getErrorMessages()){
							arg0.error(msg);
						}
					}catch (CommunicationException e) {
						arg0.error(e.getMessage());
					}	
					TaxDetailsPanel.this.taxDetailsPanelModel.setTaxDetailsDTO(taxDetailsDTO);
					TaxDetailsPanel.this.pageModel.setTaxDetailsPanelModel(taxDetailsPanelModel);					
				}
			});
		}
	}
	
	private EditStateType[] getEditStateTypesForTaxDetails() {
		
		//will disable any modification if there are any requests pending auth
		if(existingPayTaxRequest){
			return new EditStateType[]{};
		}
				
		EditStateType[] editstateTypes = new EditStateType[]{EditStateType.MODIFY,EditStateType.ADD};
		
		return editstateTypes;
	}
	
/*******************************************************************************************************************/
/*	Tax Number Field
/*******************************************************************************************************************/	
		
	public GUIFieldPanel getTaxNumPanel() {

		if(taxNumPanel == null)
		{
			taxNumPanel = createGUIFieldPanel("Income Tax Reference Number","Income Tax Reference Number","Income Tax Reference Number",
					createPageField(this.taxDetailsDTO,"Income Tax Reference Number","panel","incomeTaxNo",ComponentType.TEXTFIELD, false,true,getEditStateTypesForTaxDetails()));
			if(((HelperPanel)taxNumPanel.getComponent()).getEnclosedObject() instanceof FormComponent){
				validationComponents.add((FormComponent)((HelperPanel)taxNumPanel.getComponent()).getEnclosedObject());
			}
		}

		return taxNumPanel;
	}
	
/*******************************************************************************************************************/
/*	Create Grids
/*******************************************************************************************************************/	
	private SRSDataGrid createDirectivesgrid(String id){
		
		directdetails = getDirectives();		
		SRSDataGrid grid = new SRSDataGrid(id, new DataProviderAdapter(
				new ListDataProvider<DirectivesDTO>(directdetails)), getDirectivesColumns(),getEditState());		
		grid.setCleanSelectionOnPageChange(false);
		grid.setClickRowToSelect(false);
		grid.setAllowSelectMultiple(true);
		grid.setGridWidth(99, GridSizeUnit.PERCENTAGE);
		grid.setRowsPerPage(5);
		grid.setContentHeight(100, SizeUnit.PX);
		grid.setAutoCalculateTableHeight(getEditState().isViewOnly());
		return grid;
	} 
	
	private SRSDataGrid createVatdetailsgrid(String id){
		vatdetails = getVATDetails();		
		SRSDataGrid grid = new SRSDataGrid(id, new DataProviderAdapter(
				new ListDataProvider<VatDTO>(vatdetails)), getVatColumns(),getEditState());
					grid.setCleanSelectionOnPageChange(false);
		grid.setClickRowToSelect(false);
		grid.setAllowSelectMultiple(true);
		grid.setGridWidth(99, GridSizeUnit.PERCENTAGE);
		grid.setRowsPerPage(3);
		grid.setContentHeight(100, SizeUnit.PX);
		grid.setAutoCalculateTableHeight(getEditState().isViewOnly());
		
		return grid;
	}  
	
	private SRSDataGrid createTrustcompgrid(String id){
		trustcompdetails = getTrustComp();		
		SRSDataGrid grid = new SRSDataGrid(id, new DataProviderAdapter(
			new ListDataProvider<TrustCompDTO>(trustcompdetails)), getTrustCompColumns(),getEditState());		
		grid.setCleanSelectionOnPageChange(false);
		grid.setClickRowToSelect(false);
		grid.setAllowSelectMultiple(true);
		grid.setGridWidth(99, GridSizeUnit.PERCENTAGE);
		grid.setRowsPerPage(3);
		grid.setContentHeight(100, SizeUnit.PX);
		grid.setAutoCalculateTableHeight(getEditState().isViewOnly());		
		return grid;
	}
	
	private SRSDataGrid createLabourbrokergrid(String id){		
		labourbrokerdetails = getLabourBroker();		
		SRSDataGrid grid = new SRSDataGrid(id, new DataProviderAdapter(
			new ListDataProvider<LabourBrokersDTO>(labourbrokerdetails)), getLabourBrokerColumns(),getEditState());		
		grid.setCleanSelectionOnPageChange(false);
		grid.setClickRowToSelect(false);
		grid.setAllowSelectMultiple(true);
		grid.setGridWidth(99, GridSizeUnit.PERCENTAGE);
		grid.setRowsPerPage(3);
		grid.setContentHeight(100, SizeUnit.PX);
		grid.setAutoCalculateTableHeight(getEditState().isViewOnly());		
		return grid;
	} 
	
	private SRSDataGrid createBBBEEgrid(String id){		
		bbbeedetails = getBBBEE();		
		SRSDataGrid grid = new SRSDataGrid(id, new DataProviderAdapter(
			new ListDataProvider<BBBEEDTO>(bbbeedetails)), getBBBEEColumns(),getEditState());
		
		grid.setCleanSelectionOnPageChange(false);
		grid.setClickRowToSelect(false);
		grid.setAllowSelectMultiple(true);
		grid.setGridWidth(99, GridSizeUnit.PERCENTAGE);
		grid.setRowsPerPage(3);
		grid.setContentHeight(100, SizeUnit.PX);
		grid.setAutoCalculateTableHeight(getEditState().isViewOnly());	
		return grid;
	} 
	
/*******************************************************************************************************************/
/*	Create Columns
/*******************************************************************************************************************/	
	private List<IGridColumn> getDirectivesColumns() {
		Vector<IGridColumn> cols = new Vector<IGridColumn>(4);
						
		cols.add(new SRSDataGridColumn<DirectivesDTO>(
				"directiveNo", new Model("Directive Number"), "directiveNo",
				"directiveNo", getEditState()){
					
			private static final long serialVersionUID = 1L;

			@Override
			public Panel newCellPanel(WebMarkupContainer parent, String componentId, IModel rowModel, String objectProperty, EditStateType state, DirectivesDTO direct) {
				if(getEditState().isViewOnly() || validateEffectiveDates(direct)){
					return super.newCellPanel(parent, componentId, rowModel, objectProperty, state,	direct);
				}else{									
					HelperPanel dnumber =  createPageField(direct, "Directive Number", componentId, ComponentType.TEXTFIELD, true,true, new EditStateType[]{EditStateType.ADD,EditStateType.MODIFY});
					Component comp = dnumber.getEnclosedObject();
					if(comp instanceof TextField){
						validationComponents.add((TextField)comp);						
					}
					return dnumber;
				}
			}
		});
		cols.add(new SRSDataGridColumn<DirectivesDTO>(
				"percentage", new Model("Percentage"), "percentage",
				"percentage", getEditState()){
					
			private static final long serialVersionUID = 1L;

			@Override
			public Panel newCellPanel(WebMarkupContainer parent, String componentId, IModel rowModel, String objectProperty, EditStateType state, DirectivesDTO direct) {
				if(getEditState().isViewOnly() || validateEffectiveDates(direct)){
					return super.newCellPanel(parent, componentId, rowModel, objectProperty, state,	direct);
				}else{									
					HelperPanel dpercent =  createPageField(direct, "Percentage", componentId, ComponentType.TEXTFIELD, true,true, new EditStateType[]{EditStateType.ADD,EditStateType.MODIFY});
					Component comp = dpercent.getEnclosedObject();
					if(comp instanceof TextField){
						comp.add(new AttributeModifier("size","6"));//comp.add(new SimpleAttributeModifier("size","6"));	
						comp.add(new AttributeModifier("maxlength","6"));//comp.add(new SimpleAttributeModifier("maxlength","6"));
						validationComponents.add((TextField)comp);						
					}
					return dpercent;
				}
			}
		});
		cols.add(new SRSDataGridColumn<DirectivesDTO>(
				"startDate", new Model("Start Date"), "startDate",
				"startDate", getEditState()){
			private static final long serialVersionUID = 1L;

			@Override
			public Panel newCellPanel(WebMarkupContainer parent, String componentId, IModel rowModel, String objectProperty, EditStateType state, DirectivesDTO direct) {
				if(getEditState().isViewOnly() || validateEffectiveDates(direct)){
					return super.newCellPanel(parent, componentId, rowModel, objectProperty, state,	direct);
				}else{									
					HelperPanel dstartdate =  createPageField(direct, "Start Date", componentId, ComponentType.DATE_SELECTION_TEXTFIELD, true,true, new EditStateType[]{EditStateType.ADD,EditStateType.MODIFY});
					if(dstartdate.getEnclosedObject() instanceof TextField){
						TextField startdte = (TextField)dstartdate.getEnclosedObject();
						startdte.add(new AttributeModifier("readonly","true"));//startdte.add(new SimpleAttributeModifier("readonly","true"));
						validationComponents.add(startdte);
					}
					//Santosh datepicker fix
					if (dstartdate.getEnclosedObject() instanceof SRSDateField) {
						dstartdate.getEnclosedObject().add(
					((SRSDateField)dstartdate.getEnclosedObject()).newDatePicker());
					}
					return dstartdate;
				}
			}
		});
		cols.add(new SRSDataGridColumn<DirectivesDTO>(
				"endDate", new Model("End Date"), "endDate",
				"endDate", getEditState()){
					private static final long serialVersionUID = 1L;

					@Override
					public Panel newCellPanel(WebMarkupContainer parent, String componentId, IModel rowModel, String objectProperty, EditStateType state, DirectivesDTO direct) {
						if(getEditState().isViewOnly() || validateEffectiveDates(direct)){
							return super.newCellPanel(parent, componentId, rowModel, objectProperty, state,	direct);
						}else{									
							HelperPanel denddate =  createPageField(direct, "End Date", componentId, ComponentType.DATE_SELECTION_TEXTFIELD, false,true, new EditStateType[]{EditStateType.ADD,EditStateType.MODIFY});
							if(denddate.getEnclosedObject() instanceof SRSDateField){
								SRSDateField enddate = (SRSDateField)denddate.getEnclosedObject();
								enddate.add(new AttributeModifier("readonly","true"));//enddate.add(new SimpleAttributeModifier("readonly","true"));
								validationComponents.add(enddate);
								enddate.add(enddate.newDatePicker());
							}
							return denddate;
						}
					}
				});
		
		return cols;		
	}
	
	/**
	 * This method is used to validate the Dates to determine whtehr the rown in table should be displayed as View Only.
	 * (if the End date has already surpassed). 
	 * @param Object DTO
	 * @return boolean
	 */
	private boolean validateEffectiveDates(Object dto)
	{
		Date currentDate = new Date(System.currentTimeMillis());
		if(dto == null)
			return true;
		
		if(dto instanceof DirectivesDTO){
			DirectivesDTO directivesDTO = (DirectivesDTO)dto;
			if(directivesDTO.getEndDate()== null || DateUtil.compareDates(currentDate, directivesDTO.getEndDate()) < 1)
				return false;
			
		}else if(dto instanceof VatDTO)	{
			VatDTO vatDTO = (VatDTO)dto;
			if(vatDTO.getVatRegistrationEndDate()== null || DateUtil.compareDates(currentDate, vatDTO.getVatRegistrationEndDate()) < 1)
				return false;			
		}
		
		return true;
		
	}
	
	private List<IGridColumn> getVatColumns() {
		Vector<IGridColumn> cols = new Vector<IGridColumn>(4);
		
		cols.add(new SRSDataGridColumn<VatDTO>(
				"vatRegistrationNo", new Model("VAT Number"), "vatRegistrationNo",
				"vatRegistrationNo", getEditState()){
			
			private static final long serialVersionUID = 1L;

			@Override
			public Panel newCellPanel(WebMarkupContainer parent, String componentId, IModel rowModel, String objectProperty, EditStateType state, VatDTO vat) {
				if(getEditState().isViewOnly() || validateEffectiveDates(vat)){
					return super.newCellPanel(parent, componentId, rowModel, objectProperty, state,	vat);
				}else{									
					HelperPanel vatno =  createPageField(vat, "VAT Number", componentId, ComponentType.TEXTFIELD, true,true, new EditStateType[]{EditStateType.ADD,EditStateType.MODIFY});
					if(vatno.getEnclosedObject() instanceof TextField){
						
						validationComponents.add((TextField)vatno.getEnclosedObject());
					}
					return vatno;
				}
			}
		});
			
		cols.add(new SRSDataGridColumn<VatDTO>(
				"vatRegistrationCheckedDate", new Model("Last Checked"), "vatRegistrationCheckedDate",
				"vatRegistrationCheckedDate", getEditState()){
			private static final long serialVersionUID = 1L;

			@Override
			public Panel newCellPanel(WebMarkupContainer parent, String componentId, IModel rowModel, String objectProperty, EditStateType state, VatDTO vat) {
				if(getEditState().isViewOnly()|| validateEffectiveDates(vat)){
					return super.newCellPanel(parent, componentId, rowModel, objectProperty, state,	vat);
				}else{	
					return createPageField(vat, "Last Checked", componentId, ComponentType.DATE_SELECTION_TEXTFIELD, false,true, new EditStateType[]{EditStateType.ADD,EditStateType.MODIFY});
				}
			}
		});
		
		cols.add(new SRSDataGridColumn<VatDTO>(
				"vatRegistrationStartDate", new Model("Start Date"), "vatRegistrationStartDate",
				"vatRegistrationStartDate", getEditState()){
			private static final long serialVersionUID = 1L;

			@Override
			public Panel newCellPanel(WebMarkupContainer parent, String componentId, IModel rowModel, String objectProperty, EditStateType state, VatDTO vat) {
				if(getEditState().isViewOnly()|| validateEffectiveDates(vat)){
					return super.newCellPanel(parent, componentId, rowModel, objectProperty, state,	vat);
				}else{		
					HelperPanel p = createPageField(vat, "Start Date", componentId, ComponentType.DATE_SELECTION_TEXTFIELD, false,true, 
							new EditStateType[]{EditStateType.ADD,EditStateType.MODIFY});
					if (p.getEnclosedObject() instanceof SRSDateField) {
						((SRSDateField)p.getEnclosedObject()).add(((SRSDateField)p.getEnclosedObject()).newDatePicker());
					}
					return p;
				}
			}
		});
		
		cols.add(new SRSDataGridColumn<VatDTO>(
				"vatRegistrationEndDate", new Model("End Date"), "vatRegistrationEndDate",
				"vatRegistrationEndDate", getEditState()){
			private static final long serialVersionUID = 1L;

			@Override
			public Panel newCellPanel(WebMarkupContainer parent, String componentId, IModel rowModel, String objectProperty, EditStateType state, VatDTO vat) {
				if(getEditState().isViewOnly()|| validateEffectiveDates(vat)){
					return super.newCellPanel(parent, componentId, rowModel, objectProperty, state,	vat);
				}else{	
					HelperPanel p = createPageField(vat, "End Date", componentId, ComponentType.DATE_SELECTION_TEXTFIELD, false,true, 
							new EditStateType[]{EditStateType.ADD,EditStateType.MODIFY});
					if (p.getEnclosedObject() instanceof SRSDateField) {
						((SRSDateField)p.getEnclosedObject()).add(((SRSDateField)p.getEnclosedObject()).newDatePicker());
					}
					return p;
				}
			}
		});
		
		return cols;		
	}
	
	private List<IGridColumn> getTrustCompColumns() {
		Vector<IGridColumn> cols = new Vector<IGridColumn>(4);
		
		cols.add(new SRSDataGridColumn<TrustCompDTO>(
				"trustStartDate", new Model("Trust Start Date"), "trustStartDate",
				"trustStartDate", getEditState()){
			private static final long serialVersionUID = 1L;
	
			@Override
			public Panel newCellPanel(WebMarkupContainer parent, String componentId, IModel rowModel, String objectProperty, EditStateType state, TrustCompDTO trustcomp) {
				if(getEditState().isViewOnly()){
					return super.newCellPanel(parent, componentId, rowModel, objectProperty, state,	trustcomp);
				}else{	
					return createPageField(trustcomp, "Trust Start Date", componentId, ComponentType.DATE_SELECTION_TEXTFIELD, false,true, new EditStateType[]{EditStateType.ADD,EditStateType.MODIFY});
				}
			}
		});
		
		cols.add(new SRSDataGridColumn<TrustCompDTO>(
				"trustEndDate", new Model("Trust End Date"), "trustEndDate",
				"trustEndDate", getEditState()){
			private static final long serialVersionUID = 1L;
			
			@Override
			public Panel newCellPanel(WebMarkupContainer parent, String componentId, IModel rowModel, String objectProperty, EditStateType state, TrustCompDTO trustcomp) {
				if(getEditState().isViewOnly()){
					return super.newCellPanel(parent, componentId, rowModel, objectProperty, state,	trustcomp);
				}else{	
					return createPageField(trustcomp, "Trust End Date", componentId, ComponentType.DATE_SELECTION_TEXTFIELD, false,true, new EditStateType[]{EditStateType.ADD,EditStateType.MODIFY});
				}
			}			
		});

		cols.add(new SRSDataGridColumn<TrustCompDTO>(
				"compStartDate", new Model("Company Start Date"), "compStartDate",
				"compStartDate", getEditState()){
			private static final long serialVersionUID = 1L;
			
			@Override
			public Panel newCellPanel(WebMarkupContainer parent, String componentId, IModel rowModel, String objectProperty, EditStateType state, TrustCompDTO trustcomp) {
				if(getEditState().isViewOnly()){
					return super.newCellPanel(parent, componentId, rowModel, objectProperty, state,	trustcomp);
				}else{	
					return createPageField(trustcomp, "Company Start Date", componentId, ComponentType.DATE_SELECTION_TEXTFIELD, false,true, new EditStateType[]{EditStateType.ADD,EditStateType.MODIFY});
				}
			}			
		});

		cols.add(new SRSDataGridColumn<TrustCompDTO>(
				"compEndDate", new Model("Company End Date"), "compEndDate",
				"compEndDate", getEditState()){
			private static final long serialVersionUID = 1L;
			
			@Override
			public Panel newCellPanel(WebMarkupContainer parent, String componentId, IModel rowModel, String objectProperty, EditStateType state, TrustCompDTO trustcomp) {
				if(getEditState().isViewOnly()){
					return super.newCellPanel(parent, componentId, rowModel, objectProperty, state,	trustcomp);
				}else{	
					return createPageField(trustcomp, "Company End Date", componentId, ComponentType.DATE_SELECTION_TEXTFIELD, false,true, new EditStateType[]{EditStateType.ADD,EditStateType.MODIFY});
				}
			}
		});
		
		return cols;		
	}
	
	private List<IGridColumn> getLabourBrokerColumns() {
		Vector<IGridColumn> cols = new Vector<IGridColumn>(5);
		
		cols.add(new SRSDataGridColumn<LabourBrokersDTO>(
				"startDate", new Model("Start Date"), "startDate",
				"startDate", getEditState()){
			private static final long serialVersionUID = 1L;
			
			@Override
			public Panel newCellPanel(WebMarkupContainer parent, String componentId, IModel rowModel, String objectProperty, EditStateType state, LabourBrokersDTO labourbrok) {
				if(getEditState().isViewOnly()){
					return super.newCellPanel(parent, componentId, rowModel, objectProperty, state,	labourbrok);
				}else{	
					return createPageField(labourbrok, "Start Date", componentId, ComponentType.DATE_SELECTION_TEXTFIELD, false,true, new EditStateType[]{EditStateType.ADD,EditStateType.MODIFY});
				}
			}			
		});
		
		cols.add(new SRSDataGridColumn<LabourBrokersDTO>(
				"endDate", new Model("End Date"), "endDate",
				"endDate", getEditState()){
			private static final long serialVersionUID = 1L;
			
			@Override
			public Panel newCellPanel(WebMarkupContainer parent, String componentId, IModel rowModel, String objectProperty, EditStateType state, LabourBrokersDTO labourbrok) {
				if(getEditState().isViewOnly()){
					return super.newCellPanel(parent, componentId, rowModel, objectProperty, state,	labourbrok);
				}else{	
					return createPageField(labourbrok, "End Date", componentId, ComponentType.DATE_SELECTION_TEXTFIELD, false,true, new EditStateType[]{EditStateType.ADD,EditStateType.MODIFY});
				}
			}			
		});
		
		cols.add(new SRSDataGridColumn<LabourBrokersDTO>(
				"exemptionCertNo", new Model("Exemption Certificate Number"), "exemptionCertNo",
				"exemptionCertNo", getEditState()){
			private static final long serialVersionUID = 1L;

			@Override
			public Panel newCellPanel(WebMarkupContainer parent, String componentId, IModel rowModel, String objectProperty, EditStateType state, LabourBrokersDTO labourbrok) {
				if(getEditState().isViewOnly()){
					return super.newCellPanel(parent, componentId, rowModel, objectProperty, state,	labourbrok);
				}else{									
					HelperPanel certno =  createPageField(labourbrok, "Certificate Number", componentId, ComponentType.TEXTFIELD, false,true, new EditStateType[]{EditStateType.ADD,EditStateType.MODIFY});
					if(certno.getEnclosedObject() instanceof TextField){						
						validationComponents.add((TextField)certno.getEnclosedObject());
					}
					return certno;
				}
			}
			
		});
		cols.add(new SRSDataGridColumn<LabourBrokersDTO>(
				"exemptionCertStartDate", new Model("Certificate Start Date"), "exemptionCertStartDate",
				"exemptionCertStartDate", getEditState()){
			private static final long serialVersionUID = 1L;
			
			@Override
			public Panel newCellPanel(WebMarkupContainer parent, String componentId, IModel rowModel, String objectProperty, EditStateType state, LabourBrokersDTO labourbrok) {
				if(getEditState().isViewOnly()){
					return super.newCellPanel(parent, componentId, rowModel, objectProperty, state,	labourbrok);
				}else{	
					return createPageField(labourbrok, "Certificate Start Date", componentId, ComponentType.DATE_SELECTION_TEXTFIELD, false,true, new EditStateType[]{EditStateType.ADD,EditStateType.MODIFY});
				}
			}			
		});
		
		cols.add(new SRSDataGridColumn<LabourBrokersDTO>(
				"exemptionCertEndDate", new Model("Certificate End Date"), "exemptionCertEndDate",
				"exemptionCertEndDate", getEditState()){
			private static final long serialVersionUID = 1L;
			
			@Override
			public Panel newCellPanel(WebMarkupContainer parent, String componentId, IModel rowModel, String objectProperty, EditStateType state, LabourBrokersDTO labourbrok) {
				if(getEditState().isViewOnly()){
					return super.newCellPanel(parent, componentId, rowModel, objectProperty, state,	labourbrok);
				}else{	
					return createPageField(labourbrok, "Certificate End Date", componentId, ComponentType.DATE_SELECTION_TEXTFIELD, false,true, new EditStateType[]{EditStateType.ADD,EditStateType.MODIFY});
				}
			}			
		});
		
		return cols;		
	}
	
	private List<IGridColumn> getBBBEEColumns() {
		
		//LABEL,TEXTFIELD, CHECKBOX,DROPDOWNCHOICE,LINK,DATAGRID,IMAGEBUTTON,TEXTAREA, DATE_SELECTION_TEXTFIELD,LISTMULTIPLECHOICE,BUTTON;
		
		Vector<IGridColumn> cols = new Vector<IGridColumn>(9);
		SRSDataGridColumn c = null;
		cols.add(c =new SRSDataGridColumn<BBBEEDTO>(
				"beeLevel", new Model("BBBEE Level"), "beeLevel",
				"beeLevel", getEditState()) {
			private static final long serialVersionUID = 1L;
			
			@Override
			public Panel newCellPanel(WebMarkupContainer parent, String componentId, IModel rowModel, String objectProperty, EditStateType state, BBBEEDTO bee) {
			
				List<Integer> beeLevelList = new ArrayList<Integer>(Arrays.asList(getTaxGUIController().getBBBEELevel()));
						
				if(getEditState().isViewOnly()){
					return super.newCellPanel(parent, componentId, rowModel, objectProperty, state,	bee);
				}

				
				HelperPanel panel = createDropdownField(bee, "BBBEE Level", componentId, beeLevelList, new ChoiceRenderer<Integer>(),
						null, true, true, new EditStateType[]{EditStateType.ADD,EditStateType.MODIFY} );
				
				return panel;
			}			
		});
		c.setInitialSize(90);
		
		cols.add(c = new SRSDataGridColumn<BBBEEDTO>(
				"companySize", new Model("Company Size"), "companySize",
				"companySize", getEditState()){
			private static final long serialVersionUID = 1L;
			
			@Override
			public Panel newCellPanel(WebMarkupContainer parent, String componentId, IModel rowModel, String objectProperty, EditStateType state, BBBEEDTO bee) {
				
				List<String> compSizeList = new ArrayList(Arrays.asList(getTaxGUIController().getBBBEECompSize()));
				
				if(getEditState().isViewOnly()){
					return super.newCellPanel(parent, componentId, rowModel, objectProperty, state,	bee);
				}else{	
					return createDropdownField(bee, "Company Size", componentId, compSizeList, new ChoiceRenderer(),
							null, true,new EditStateType[]{EditStateType.ADD,EditStateType.MODIFY} );
					//return createPageField(bee, "Company Size", componentId, ComponentType.TEXTFIELD, false,true, new EditStateType[]{EditStateType.ADD,EditStateType.MODIFY});
				}
			}			
		});
		c.setInitialSize(90);
		
		cols.add(c = new SRSDataGridColumn<BBBEEDTO>(
				"blackOwnership", new Model("Black Ownership"), "blackOwnership",
				"blackOwnership", getEditState()){
			private static final long serialVersionUID = 1L;
			
			@Override
			public Panel newCellPanel(WebMarkupContainer parent, String componentId, IModel rowModel, String objectProperty, EditStateType state, BBBEEDTO bee) {
				if(getEditState().isViewOnly()){
					return super.newCellPanel(parent, componentId, rowModel, objectProperty, state,	bee);
				}else{	
					return createPageField(bee, "Black Ownership", componentId, ComponentType.TEXTFIELD, false,true, new EditStateType[]{EditStateType.ADD,EditStateType.MODIFY});
				}
			}			
		});
		c.setInitialSize(100);
		
		cols.add(new SRSDataGridColumn<BBBEEDTO>(
				"blackWomenOwnership", new Model("Black Women Ownership"), "blackWomenOwnership",
				"blackWomenOwnership", getEditState()){
			private static final long serialVersionUID = 1L;
			
			@Override
			public Panel newCellPanel(WebMarkupContainer parent, String componentId, IModel rowModel, String objectProperty, EditStateType state, BBBEEDTO bee) {
				if(getEditState().isViewOnly()){
					return super.newCellPanel(parent, componentId, rowModel, objectProperty, state,	bee);
				}else{	
					return createPageField(bee, "Black Women Ownership", componentId, ComponentType.TEXTFIELD, false,true, new EditStateType[]{EditStateType.ADD,EditStateType.MODIFY});
				}
			}			
		});
		
		cols.add(c = new SRSDataGridColumn<BBBEEDTO>(
				"effectiveFromDate", new Model("Registration Date"), "effectiveFromDate",
				"effectiveFromDate", getEditState()){
			private static final long serialVersionUID = 1L;
			
			@Override
			public Panel newCellPanel(WebMarkupContainer parent, String componentId, IModel rowModel, String objectProperty, EditStateType state, BBBEEDTO bee) {
				if(getEditState().isViewOnly()){
					return super.newCellPanel(parent, componentId, rowModel, objectProperty, state,	bee);
				}else{	
					HelperPanel p = createPageField(bee, "Effective From Date", componentId, ComponentType.DATE_SELECTION_TEXTFIELD, false,true, 
							new EditStateType[]{EditStateType.ADD,EditStateType.MODIFY});
					if (p.getEnclosedObject() instanceof SRSDateField) {
						((SRSDateField)p.getEnclosedObject()).add(((SRSDateField)p.getEnclosedObject()).newDatePicker());
					}
					return p;
				}
			}			
		});
		c.setInitialSize(100);
		
		cols.add(new SRSDataGridColumn<BBBEEDTO>(
				"effectiveToDate", new Model("Expiry Date"), "effectiveToDate",
				"effectiveToDate", getEditState()){
			private static final long serialVersionUID = 1L;
			
			@Override
			public Panel newCellPanel(WebMarkupContainer parent, String componentId, IModel rowModel, String objectProperty, EditStateType state, BBBEEDTO bee) {
				if(getEditState().isViewOnly()){
					return super.newCellPanel(parent, componentId, rowModel, objectProperty, state,	bee);
				}else{	
					HelperPanel p = createPageField(bee, "Effective To Date", componentId, ComponentType.DATE_SELECTION_TEXTFIELD, false,true, 
							new EditStateType[]{EditStateType.ADD,EditStateType.MODIFY});
					if (p.getEnclosedObject() instanceof SRSDateField) {
						((SRSDateField)p.getEnclosedObject()).add(((SRSDateField)p.getEnclosedObject()).newDatePicker());
					}
					return p;
				}
			}			
		});

		
		cols.add(c = new SRSDataGridColumn<BBBEEDTO>(
				"designatedGroup", new Model("Designated Group"), "designatedGroup",
				"designatedGroup", getEditState()){
			private static final long serialVersionUID = 1L;
			
			@Override
			public Panel newCellPanel(WebMarkupContainer parent, String componentId, IModel rowModel, String objectProperty, EditStateType state, BBBEEDTO bee) {
				if(getEditState().isViewOnly()){
					return super.newCellPanel(parent, componentId, rowModel, objectProperty, state,	bee);
				}else{	
					return createPageField(bee, "Designated Group", componentId, ComponentType.CHECKBOX, false,true, new EditStateType[]{EditStateType.ADD,EditStateType.MODIFY});
				}
			}			
		});
		c.setInitialSize(110);
		
		cols.add(c = new SRSDataGridColumn<BBBEEDTO>(
				"designatedGroupPerc", new Model("Designated Group %"), "designatedGroupPerc",
				"designatedGroupPerc", getEditState()){
			private static final long serialVersionUID = 1L;
			
			@Override
			public Panel newCellPanel(WebMarkupContainer parent, String componentId, IModel rowModel, String objectProperty, EditStateType state, BBBEEDTO bee) {
				if(getEditState().isViewOnly()){
					return super.newCellPanel(parent, componentId, rowModel, objectProperty, state,	bee);
				}else{	
					return createPageField(bee, "Designated Group %", componentId, ComponentType.TEXTFIELD, false,true, new EditStateType[]{EditStateType.ADD,EditStateType.MODIFY});
				}
			}			
		});
		c.setInitialSize(110);
		
		cols.add(new SRSDataGridColumn<BBBEEDTO>(
				"beeVerificationAgency", new Model("Bee Verification Agency"), "beeVerificationAgency",
				"beeVerificationAgency", getEditState()){
			private static final long serialVersionUID = 1L;
			
			@Override
			public Panel newCellPanel(WebMarkupContainer parent, String componentId, IModel rowModel, String objectProperty, EditStateType state, BBBEEDTO bee) {
				if(getEditState().isViewOnly()){
					return super.newCellPanel(parent, componentId, rowModel, objectProperty, state,	bee);
				}else{	
					return createPageField(bee, "Bee Verification Agency", componentId, ComponentType.TEXTFIELD, false,true, new EditStateType[]{EditStateType.ADD,EditStateType.MODIFY});
				}
			}			
		});		

		
		return cols;		
	}
	
	
/*******************************************************************************************************************/
/*	Return DTO's
/*******************************************************************************************************************/	
	private List<DirectivesDTO> getDirectives() {
		if(this.taxDetailsDTO.getDirectivesDTOList() == null) {
			return new ArrayList<DirectivesDTO>();
		}
		Collections.sort(this.taxDetailsDTO.getDirectivesDTOList());
		return this.taxDetailsDTO.getDirectivesDTOList();
	} 
	
	private List<VatDTO> getVATDetails() {
		if(this.taxDetailsDTO.getVatDTOList() == null) {
			return new ArrayList<VatDTO>();
		}
		Collections.sort(this.taxDetailsDTO.getVatDTOList());
		return this.taxDetailsDTO.getVatDTOList();
	}
	
	private List<TrustCompDTO> getTrustComp() {
		 if(this.taxDetailsDTO.getTrustCompDTOList() == null) {
				return new ArrayList<TrustCompDTO>();
			}		 
		return this.taxDetailsDTO.getTrustCompDTOList();
	}
	
	private List<LabourBrokersDTO> getLabourBroker() {
		if(this.taxDetailsDTO.getLabourBrokersDTOList() == null) {
			return new ArrayList<LabourBrokersDTO>();
		}
		return this.taxDetailsDTO.getLabourBrokersDTOList();
		
	}
	
	private List<BBBEEDTO> getBBBEE() {
		if(this.taxDetailsDTO.getBBBEEDTOList() == null) {
			ArrayList<BBBEEDTO> list = new ArrayList<BBBEEDTO>();
			taxDetailsDTO.setBBBEEDTOList(list);
		}

		return this.taxDetailsDTO.getBBBEEDTOList();
	
	}
	
	private IPartyMaintenanceController getPartyMaintenanceController()
	{
		if(partyMaintenanceController == null){
				try {
					partyMaintenanceController = ServiceLocator.lookupService(IPartyMaintenanceController.class);
				} catch (NamingException e) {
					throw new CommunicationException(e);
				}
			}
			return partyMaintenanceController;
		
	}
	
	private ITaxDetailsGUIController getTaxGUIController()
	{
		if(taxGUIController == null){
			try {
				taxGUIController = ServiceLocator.lookupService(ITaxDetailsGUIController.class);
			} catch (NamingException e) {
				throw new CommunicationException(e);
			}
		}
		return taxGUIController;		
	}
	
	
	
/*******************************************************************************************************************/
/*	Create Buttons
/*******************************************************************************************************************/	
	private Button createAddDirectivesButton(String id){
		final Button button = new Button(id);
		if(this.getEditState().isViewOnly() || isAddButtonNotVisible(TaxButtonType.ADDDIRECTIVES)){
			button.setVisible(false);
		}
		button.add(new AjaxFormComponentUpdatingBehavior("click"){
			private static final long serialVersionUID = 1L;
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
				Date startDate = null;
				Date endDate = null;
				DateRange sarsTaxYearRange = getTaxGUIController().getCurrentSARSTaxYear();
				try {					
					startDate = sdf.parse(sdf.format(sarsTaxYearRange.getStartDate()));
					endDate = sdf.parse(sdf.format(sarsTaxYearRange.getEndDate()));
				} catch (ParseException e) {
					error(e.getMessage());
				}
				
				DirectivesDTO direct = new DirectivesDTO();				
				direct.setStartDate(startDate);
				direct.setEndDate(endDate);
				directdetails.add(direct);
				taxDetailsDTO.setDirectivesDTOList(directdetails);				
				target.add(directivesGrid);//target.addComponent(directivesGrid);
				addDirectiveButton.setVisible(false);
				target.add(addDirectiveButton);//target.addComponent(addDirectiveButton);
			}			
		});
		
		button.setOutputMarkupId(true);
		button.setOutputMarkupPlaceholderTag(true);
		return button;
	}
	
	@SuppressWarnings("serial")
	private Button createAddVatButton(String id){
		
		final Button button = new Button(id);
		if(this.getEditState().isViewOnly()|| isAddButtonNotVisible(TaxButtonType.ADDVAT)){
		 	button.setVisible(false);
		}
		
		button.add(new AjaxFormComponentUpdatingBehavior("click"){
			@Override
				protected void onUpdate(AjaxRequestTarget target) {
				VatDTO vat = new VatDTO();				
				vatdetails.add(vat);
				taxDetailsDTO.setVatDTOList(vatdetails);
				target.add(vatdetailsGrid);//target.addComponent(vatdetailsGrid);
				addVatButton.setVisible(false);
				target.add(addVatButton);//target.addComponent(addVatButton);
				
				}
			});
		
		button.setOutputMarkupId(true);
		button.setOutputMarkupPlaceholderTag(true);
		return button;
	}
	
	@SuppressWarnings("serial")
	private Button createEndVatButton(String id){
		Button button = new Button(id);
		
		if(this.getEditState().isViewOnly() || isVatDetailsNonEditable()){
		 	button.setVisible(false);
		}
		
		button.add(new AjaxFormComponentUpdatingBehavior("click"){
			@Override
				protected void onUpdate(AjaxRequestTarget target) {
								
					endVatdetails();
					
					target.add(vatdetailsGrid);//target.addComponent(vatdetailsGrid);
				
				}
			});
		
		button.setOutputMarkupId(true);
		button.setOutputMarkupPlaceholderTag(true);
		return button;
	}
	
	/**
	 * Private method to determine the Visisbility of END button on VAT Details section
	 * @return boolean
	 */
	private boolean isVatDetailsNonEditable() {
		
		if(this.taxDetailsDTO == null ||this.taxDetailsDTO.getVatDTOList() == null ||this.taxDetailsDTO .getVatDTOList().size()==0)
			return true;
		
		Date currDate = new Date(System.currentTimeMillis());
		Date vatEndDate = this.taxDetailsDTO .getVatDTOList().get(0).getVatRegistrationEndDate();
		
		if(vatEndDate == null ||DateUtil.compareDates(currDate, vatEndDate) < 1 )
			return false;
		else
			return true;		
	}


	@SuppressWarnings("serial")
	private Button createAddTrustCompButton(String id){
		final Button button = new Button(id);
		if(this.getEditState().isViewOnly() || isAddButtonNotVisible(TaxButtonType.ADDPSC)){
			button.setVisible(false);
		}
		button.add(new AjaxFormComponentUpdatingBehavior("click"){
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
			TrustCompDTO trustcomp = new TrustCompDTO();
			trustcompdetails.add(trustcomp);
			taxDetailsDTO.setTrustCompDTOList(trustcompdetails);
			target.add(trustcompGrid);//target.addComponent(trustcompGrid);
			addPSCButton.setVisible(false);
			target.add(addPSCButton);//target.addComponent(addPSCButton);
			}	
		});
		
		button.setOutputMarkupId(true);
		button.setOutputMarkupPlaceholderTag(true);
		return button;
	}
	
	@SuppressWarnings("serial")
	private Button createAddLabourbrokerButton(String id){
		final Button button = new Button(id);
		if(this.getEditState().isViewOnly()|| isAddButtonNotVisible(TaxButtonType.ADDLABOURBROKER)){
			button.setVisible(false);
		}
		button.add(new AjaxFormComponentUpdatingBehavior("click"){
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
			LabourBrokersDTO labourbrok = new LabourBrokersDTO();
			labourbrokerdetails.add(labourbrok);
			taxDetailsDTO.setLabourBrokersDTOList(labourbrokerdetails);			
			target.add(labourbrokerGrid);//target.addComponent(labourbrokerGrid);
			addLabourButton.setVisible(false);
			target.add(addLabourButton);//target.addComponent(addLabourButton);
			}		
		});
		
		button.setOutputMarkupId(true);
		button.setOutputMarkupPlaceholderTag(true);
		return button;
	}
	
	@SuppressWarnings("serial")
	private Button createAddBBBEEButton(String id){
		final Button button = new Button(id);
		String partyType = (TaxDetailsPanel.this.contextDTO != null && TaxDetailsPanel.this.contextDTO.getPartyContextDTO() != null) 
				? TaxDetailsPanel.this.contextDTO.getPartyContextDTO().getIntermediaryType() : "";

		if(this.getEditState().isViewOnly()|| isAddButtonNotVisible(TaxButtonType.ADDBBBEE)|| PartyType.PERSON.getDesc().equals(partyType.trim())){
			button.setVisible(false);
		}
		button.add(new AjaxFormComponentUpdatingBehavior("click"){
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				BBBEEDTO bee = new BBBEEDTO();
				getBBBEE().add(bee);
//				This works slightly differently so no need to reset the list.
//				taxDetailsDTO.setBBBEEDTOList(bbbeedetails);			
				target.add(bbbeeGrid);//target.addComponent(bbbeeGrid);
				addBBBEEButton.setVisible(false);
				target.add(addBBBEEButton);//target.addComponent(addBBBEEButton);
			}		
		});
		
		button.setOutputMarkupId(true);
		button.setOutputMarkupPlaceholderTag(true);
		return button;
	}
	
	/**
	 * Create a history button
	 * @param id
	 * @return
	 */
	private Button createDirectivesHistoryButton(String id) {
		final Button button = new Button(id);
		button.add(new AjaxFormComponentUpdatingBehavior("click") {
			private static final long serialVersionUID = 0L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				
				directivesHistoryWindow.show(target);
			}
		});
		if (!getEditState().isViewOnly() 
				|| getEditState() == EditStateType.AUTHORISE) {
			button.setEnabled(false);
			button.setVisible(false);
		}		
		button.setOutputMarkupId(true);
		button.setOutputMarkupPlaceholderTag(true);		
		return button;
	}
	
	/**
	 * Create a bbbee history button
	 * @param id
	 * @return
	 */
	private Button createBBBEEHistoryButton(String id) {
		final Button button = new Button(id);
		button.add(new AjaxFormComponentUpdatingBehavior("click") {
			private static final long serialVersionUID = 0L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				
				bbbeeHistoryWindow.show(target);
			}
		});
		if (!getEditState().isViewOnly() 
				|| getEditState() == EditStateType.AUTHORISE) {
			button.setEnabled(false);
			button.setVisible(false);
		}		
		button.setOutputMarkupId(true);
		button.setOutputMarkupPlaceholderTag(true);		
		return button;
	}
	
	
	/**
	 * Create the history popup
	 * @param id
	 * @return
	 */
	private ModalWindow createDirectivesHistoryWindow(String id) {
		final ModalWindow window = new ModalWindow(id);
		window.setTitle("History");		
		// Create the page
		window.setPageCreator(new ModalWindow.PageCreator() {
			private static final long serialVersionUID = 1L;
			public Page createPage() {	
				return new TaxDirectivesHistoryPage(window,taxDetailsPanelModel.getTaxDetailsDTO().getPartyOID(), contextDTO);
			}			
		});			
		// Initialise window settings
		window.setMinimalHeight(400);
		window.setInitialHeight(400);
		window.setMinimalWidth(700);
		window.setInitialWidth(700);
		window.setMaskType(MaskType.SEMI_TRANSPARENT);
		window.setCssClassName(ModalWindow.CSS_CLASS_GRAY);	
		window.setOutputMarkupId(true);
		window.setOutputMarkupPlaceholderTag(true);
		window.setCookieName("TaxDirectivesHistoryPage");//window.setPageMapName("TaxDirectivesHistoryPage");
		return window;
	}		
	
	
	/**
	 * Create the bbbee history popup
	 * @param id
	 * @return
	 */
	private ModalWindow createBBBEEHistoryWindow(String id) {
		final ModalWindow window = new ModalWindow(id);
		window.setTitle("History");		
		// Create the page
		window.setPageCreator(new ModalWindow.PageCreator() {
			private static final long serialVersionUID = 1L;
			public Page createPage() {					
				return new BBBEEHistoryPage(window,taxDetailsPanelModel.getTaxDetailsDTO().getPartyOID(), contextDTO);
				
			}			
		});			
		// Initialise window settings
		window.setMinimalHeight(400);
		window.setInitialHeight(400);
		window.setMinimalWidth(700);
		window.setInitialWidth(700);
		window.setMaskType(MaskType.SEMI_TRANSPARENT);
		window.setCssClassName(ModalWindow.CSS_CLASS_GRAY);	
		window.setOutputMarkupId(true);
		window.setOutputMarkupPlaceholderTag(true);
		window.setCookieName("BBBEEHistoryPage");//window.setPageMapName("BBBEEHistoryPage");
		return window;
	}
	
	/**
	 * private method to End Vat Details if User clicks on END button	 * 
	 */
	
	private void endVatdetails(){
		Date currDate = new Date(System.currentTimeMillis());
		/** Change to not remove but set defaults  (current data, and set the end date to todays date)  */
		for(VatDTO vatDTO:vatdetails){
			vatDTO.setVatRegistrationCheckedDate(currDate);
			vatDTO.setVatRegistrationEndDate(currDate);		
		}		
		this.taxDetailsDTO.setVatDTOList(vatdetails);	
	}
	
	/**
	 * Private method to determine the Visisbility of ADD Button for the Different Sections of VAT, Directives, PSC and Labour broker.
	 * @param TaxButton Type enum
	 * @return boolean
	 */
	
	private boolean isAddButtonNotVisible(TaxButtonType type){
		
		if(this.taxDetailsDTO == null)
			return true;
		Date currDate = new Date(System.currentTimeMillis());
		
		switch(type){
		
		case ADDDIRECTIVES: if(this.taxDetailsDTO.getDirectivesDTOList() == null|| this.taxDetailsDTO.getDirectivesDTOList().size() == 0)
							return false;
							
							for(DirectivesDTO directivesDTO:this.taxDetailsDTO.getDirectivesDTOList()){
								//Active Row
								if(directivesDTO.getEndDate() == null || DateUtil.compareDates(currDate, directivesDTO.getEndDate())< 1)
									return true;							
							}
							break;
							
		case ADDVAT:	if(this.taxDetailsDTO.getVatDTOList() == null|| this.taxDetailsDTO.getVatDTOList().size() == 0)
								return false;
		
						for(VatDTO vatDTO :this.taxDetailsDTO.getVatDTOList()){
								//Active Row
								if(vatDTO.getVatRegistrationEndDate() == null || DateUtil.compareDates(currDate, vatDTO.getVatRegistrationEndDate())< 1)
								return true;								
							}
							break;
						
		case ADDLABOURBROKER  : if(this.taxDetailsDTO.getLabourBrokersDTOList() == null|| this.taxDetailsDTO.getLabourBrokersDTOList().size() == 0)
								return false;
							
							for(LabourBrokersDTO  labourBrokersDTO :this.taxDetailsDTO.getLabourBrokersDTOList()){
									//Active Row
								if(labourBrokersDTO.getEndDate() == null || labourBrokersDTO.getExemptionCertEndDate() == null)
									return true;
								if(labourBrokersDTO.getEndDate() != null && DateUtil.compareDates(currDate, labourBrokersDTO.getEndDate())< 1)
									return true;
								if(labourBrokersDTO.getExemptionCertEndDate() != null && DateUtil.compareDates(currDate, labourBrokersDTO.getExemptionCertEndDate())< 1)
									return true;								
							}
							break;
							
		case ADDBBBEE: if(this.taxDetailsDTO.getBBBEEDTOList() == null|| this.taxDetailsDTO.getBBBEEDTOList().size() == 0)
			return false;
			
			for(BBBEEDTO bbbeeDTO:this.taxDetailsDTO.getBBBEEDTOList()){
				//Active Row
				if(bbbeeDTO.getEffectiveToDate() == null)
					return true;	
				if(bbbeeDTO.getEffectiveToDate() != null && DateUtil.compareDates(currDate, bbbeeDTO.getEffectiveToDate())< 1)
					return true;
			}
			break;
			
 				
		case ADDPSC :  		if(this.taxDetailsDTO.getTrustCompDTOList() == null|| this.taxDetailsDTO.getTrustCompDTOList().size() == 0)
								return false;
							
							for(TrustCompDTO  trustCompDTO :this.taxDetailsDTO.getTrustCompDTOList()){
									//Active Row
								if(trustCompDTO.getTrustEndDate() == null || trustCompDTO.getCompEndDate() == null)
									return true;
								if(trustCompDTO.getTrustEndDate() != null && DateUtil.compareDates(currDate, trustCompDTO.getTrustEndDate())< 1)
									return true;
								if(trustCompDTO.getCompEndDate() != null && DateUtil.compareDates(currDate, trustCompDTO.getCompEndDate())< 1)
									return true;								
							}								
		
		}
		
		return false;	
	}	
}