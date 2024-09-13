package za.co.liberty.web.pages.maintainagreement;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.NamingException;

import org.apache.commons.lang.SerializationUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.validation.AbstractFormValidator;
import org.apache.wicket.markup.html.form.validation.IFormValidator;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;


import za.co.liberty.business.agreement.validator.IAgreementValidator;
import za.co.liberty.business.guicontrollers.partymaintenance.IPartyMaintenanceController;
import za.co.liberty.common.domain.Percentage;
import za.co.liberty.dto.agreement.AgreementDTO;
import za.co.liberty.dto.party.aqcdetail.AQCDTO;
import za.co.liberty.dto.party.aqcdetail.AQCDetailsWithTypeDTO;
import za.co.liberty.dto.party.aqcdetail.AQCValueDTO;
import za.co.liberty.dto.party.aqcdetail.AdvisorQualityCodeDTO;
import za.co.liberty.dto.party.aqcdetail.EffectiveAQCDTO;
import za.co.liberty.dto.persistence.party.flow.PartyAQCHistoryFLO;
import za.co.liberty.dto.rating.SegmentNameDTO;
import za.co.liberty.exceptions.ExceptionUtils;
import za.co.liberty.exceptions.SystemException;
import za.co.liberty.exceptions.data.DataNotFoundException;
import za.co.liberty.exceptions.data.ValidationException;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.helpers.lookup.ServiceLocator;
import za.co.liberty.helpers.util.DateUtil;
import za.co.liberty.interfaces.agreements.requests.RequestKindType;
import za.co.liberty.interfaces.rating.difffactor.AQCProductType;
import za.co.liberty.interfaces.rating.difffactor.AQCType;
import za.co.liberty.persistence.rating.IDifferentialFactorEntityManager;
import za.co.liberty.persistence.rating.entity.DifferentialFactorEntity;
import za.co.liberty.web.constants.SRSAppWebConstants;
import za.co.liberty.web.data.enums.ComponentType;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.data.enums.fields.AgreementGUIField;
import za.co.liberty.web.pages.interfaces.ISecurityPanel;
import za.co.liberty.web.pages.maintainagreement.model.AQCTableItemModel;
import za.co.liberty.web.pages.maintainagreement.model.AdvisorQualityCodePanelModel;
import za.co.liberty.web.pages.maintainagreement.model.SubAdvisorQualityCodePanelModel;
import za.co.liberty.web.pages.panels.HelperPanel;
import za.co.liberty.web.pages.panels.ViewTemplateBasePanel;
import za.co.liberty.web.wicket.markup.html.form.SRSDateField;
import za.co.liberty.web.wicket.markup.html.form.SRSDropDownChoice;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGrid;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGrid.GridSizeUnit;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGridColumn;
import za.co.liberty.web.wicket.markup.repeater.data.ListDataProvider;
import za.co.liberty.web.wicket.view.ContextDrivenViewTemplate;

import com.inmethod.grid.DataProviderAdapter;
import com.inmethod.grid.IGridColumn;
import com.inmethod.grid.SizeUnit;


/**
 * This panel contains all components necessary to render the Advisor Quality Code Details.
 * 
 * @author PKS2802
 * @author jzb0608 2017/11 - Re-wrote parts of the GUI to make it simpler to edit.  Moved
 * 		away from repeating panels to a single table.  Was constrained by existing aqc objects.
 *
 */
public class AdvisorQualityCodePanel extends ViewTemplateBasePanel<AgreementGUIField, AgreementDTO> 
implements ISecurityPanel {

	private static final long serialVersionUID = 1L;

	private static Logger logger = Logger.getLogger(AdvisorQualityCodePanel.class);
	
	private AdvisorQualityCodePanelModel  panelModel;
	private EditStateType editState;
	private boolean initialised;
	
	private AdvisorQualityCodeForm pageForm ; 
	private Page parentPage;
	
	/* Fields */
	private boolean existingAQCRequest;		
	private AgreementDTO viewTemplateContext;
	private FeedbackPanel feedbackPanel;
	
	private HelperPanel effSegmentPanel;
	private List<AQCTableItemModel> tableModelList= new ArrayList<AQCTableItemModel>();
	
	private WebMarkupContainer dateContainer;
	protected Label startDateLabel;
	protected SRSDateField endDateField;
	private ModalWindow calcAQCHistoryWindow;
	private ModalWindow manualAQCHistoryWindow;
	
	private Date startDate;
	private Date endDate;
	private transient IAgreementValidator agreementValidator;

	private List<FormComponent> validationComponents = new ArrayList<FormComponent>();
	private Panel aqcGridPanel;
	
	private transient IPartyMaintenanceController partyMaintenanceController;

	private transient IDifferentialFactorEntityManager differentialFactorEntityManager;
	
	static {
		
	}
	
	/**
	 * Constructor (initialises with null parent and feedback) 
	 * 
	 * @param id
	 * @param editState
	 * @param panelModel
	 */
	public AdvisorQualityCodePanel(String id, EditStateType editState,
			AdvisorQualityCodePanelModel panelModel) {
		this(id,editState,panelModel,null,null);
	}

	/**
	 * Main constructor 
	 * 
	 * @param id
	 * @param editState
	 * @param panelModel
	 * @param parentPage
	 * @param feedbackPanel
	 */
	public AdvisorQualityCodePanel(String id, EditStateType editState,
			AdvisorQualityCodePanelModel panelModel,Page parentPage,FeedbackPanel feedbackPanel) {
		super(id, editState, parentPage);
		this.panelModel = panelModel;
		this.editState = editState;
		this.parentPage = parentPage;
		this.feedbackPanel = feedbackPanel;
	}

	
	@Override
	protected void onBeforeRender() {
		/*
		 * Check for unauthorised requests
		 */
		super.onBeforeRender();
		
		if (!initialised) {
				
				List<RequestKindType> unAuthRequests = getOutStandingRequestKinds();			
				//check for existing AQC requests 
				for (RequestKindType kind : unAuthRequests) {
								
					if(kind == RequestKindType.MaintainAdvisorQualityCode){
						existingAQCRequest = true;
						break;
					}
				}
				
				add(getPageForm());
				add(getCalcAQCHistoryWindow());
				add(getManualAQCHistoryWindow());
				initialised = true;
			}
	 		
	}
	
	/**
	 * Get the page form
	 * @return
	 */
	private Component getPageForm(){
		if (pageForm == null) {
			initialiseModel();
			pageForm = new AdvisorQualityCodeForm("aqcForm");			
		}
		return pageForm;
	}
	
	private ModalWindow getManualAQCHistoryWindow() {
		if (manualAQCHistoryWindow==null) {
			manualAQCHistoryWindow = AQCHistoryPopup.createModalWindow(
					panelModel.getManualAQCHistory(),"manualAQCHistory",panelModel);
		}
		return manualAQCHistoryWindow;
	}
	
	private ModalWindow getCalcAQCHistoryWindow() {
		if (calcAQCHistoryWindow==null) {
			calcAQCHistoryWindow = AQCHistoryPopup.createModalWindow(
					panelModel.getCalculatedAQCHistory(),"calcAQCHistory",panelModel);
		}
		return calcAQCHistoryWindow;
	}
	
	/**
	 * Initialise the internal models used
	 */
	private void initialiseModel() {
		/*
		 * Initialise the models 
		 */
		tableModelList= new ArrayList<AQCTableItemModel>();
//		tableModelList
		AdvisorQualityCodeDTO  advisorQualityCodeDTO = panelModel.getAdvisorQualityCodeDTO();
		
		Map<AQCType, AQCTableItemModel> aqcMap = new HashMap<AQCType, AQCTableItemModel>();
		
		
		List<AQCDetailsWithTypeDTO> aqcDetailsWithTypeDTOs = advisorQualityCodeDTO != null?advisorQualityCodeDTO.getAqcDetailsWithTypeDTO():null;
		List<EffectiveAQCDTO> effectiveAQCValues = advisorQualityCodeDTO != null?advisorQualityCodeDTO.getEffectiveAQCValues():null;
		
		/*
		 * Set according to calculated / manual AQC codes
		 */
		if(aqcDetailsWithTypeDTOs != null){
			
			for(AQCDetailsWithTypeDTO typeDto : aqcDetailsWithTypeDTOs){
				AQCTableItemModel dto = new AQCTableItemModel();
				dto.setCalculcatedCodeDTO(typeDto.getCalculatedAQCDTO());
				
				dto.setAqcType(typeDto.getAqcType());
				if(typeDto.getMaxUpfrontCommPercent() != null){
					dto.setMaxUpfrontCommPercent(typeDto.getMaxUpfrontCommPercent());
					dto.setMaxUpfrontCommPercentEndDate(typeDto.getMaxUpfrontCommPercentEndDate());
				}
				
				if (typeDto.getManualAQCDTOs()!=null && typeDto.getManualAQCDTOs().size()>0) {
					// Get the last one (should always be one)
					dto.setMaxUpfrontCommPercent(typeDto.getMaxUpfrontCommPercent());
					dto.setMaxUpfrontCommPercentEndDate(typeDto.getMaxUpfrontCommPercentEndDate());
					dto.setManualCodeDTO(typeDto.getManualAQCDTOs().get(typeDto.getManualAQCDTOs().size()-1));
					dto.setOriginalManualCodeDTO((AQCDTO) SerializationUtils.clone(dto.getOriginalManualCodeDTO()));
				
				} else {
					dto.setManualCodeDTO(new AQCDTO());
					dto.getManualCodeDTO().setAqcValueDTO(new AQCValueDTO());
					dto.getManualCodeDTO().setStartDate(DateUtil.getInstance().getTodayDatePart());
				}
				aqcMap.put(typeDto.getAqcType(), dto);
				tableModelList.add(dto);
			}
		}
		
		/*
		 * Set according to effective values, this will always be available.
		 */
		if(effectiveAQCValues != null){
			
			for(EffectiveAQCDTO effectiveAQCDTO:effectiveAQCValues){
				AQCTableItemModel dto = aqcMap.get(effectiveAQCDTO.getAqcType());
				if (dto == null) {
					dto = new AQCTableItemModel();
					dto.setAqcType(effectiveAQCDTO.getAqcType());
					dto.setManualCodeDTO(new AQCDTO());
					tableModelList.add(dto);
				}
				if(effectiveAQCDTO.getAqcType().isRisk()){
					dto.setManualAQCValidValues( panelModel.getValidManualAQCValues() != null
							? panelModel.getValidManualAQCValues().getValidManualRiskAQCValues(): new ArrayList<AQCValueDTO>());
					dto.setManualAQCValidValuesForAll(panelModel.getValidManualAQCValues() != null
							?panelModel.getValidManualAQCValues().getValidManualRiskAQCValuesForAll(): new ArrayList<AQCValueDTO>());
					
					if(dto.getMaxUpfrontCommPercent() == null && AQCType.RISK.equals(effectiveAQCDTO.getAqcType())){
						DifferentialFactorEntity diffFactor;
						try {
							diffFactor = getDifferentialFactorEntityManager().findDiffFactorForProductSegmentNameAQC(AQCProductType.LLP, advisorQualityCodeDTO.getSegment().getId(), effectiveAQCDTO.getValue());
							if(diffFactor != null){
								dto.setMaxUpfrontCommPercent(new Percentage(diffFactor.getMaxUpfrontCommPercent()));
								dto.setMaxUpfrontCommPercentEndDate(diffFactor.getEndDate());
							}
						} catch (DataNotFoundException e) {
							logger.error(e.getMessage(), e);
							throw new SystemException(e.getMessage(), 0, ExceptionUtils.ERROR);
						}
					}
				}else if (effectiveAQCDTO.getAqcType().isInvest()) {
					dto.setManualAQCValidValues(panelModel.getValidManualAQCValues() != null
							?panelModel.getValidManualAQCValues().getValidManualInvAQCValues(): new ArrayList<AQCValueDTO>());
					dto.setManualAQCValidValuesForAll(panelModel.getValidManualAQCValues() != null
							?panelModel.getValidManualAQCValues().getValidManualInvAQCValuesForAll(): new ArrayList<AQCValueDTO>());
				} else if (effectiveAQCDTO.getAqcType().isShortTerm()) {
					dto.setManualAQCValidValues(panelModel.getValidManualAQCValues() != null
							?panelModel.getValidManualAQCValues().getValidManualShortTermAQCValues(): new ArrayList<AQCValueDTO>());
					dto.setManualAQCValidValuesForAll(panelModel.getValidManualAQCValues() != null
							?panelModel.getValidManualAQCValues().getValidManualShortTermAQCValuesForAll(): new ArrayList<AQCValueDTO>());
				}
				dto.setEffectiveValue(effectiveAQCDTO.getValue());
			}
		}
	}
	
	/**
	 * Initialise the internal models used
	 */
	private void updateDTOFromModel() {
		boolean isChanged = false;
		/*
		 * Copy from the models back into the DTO. 
		 * 
		 * Remember that the original DTO is in manual field and a clone of the original is in the 
		 * original field.
		 * 
		 */
//		tableModelList
		AdvisorQualityCodeDTO  advisorQualityCodeDTO = panelModel.getAdvisorQualityCodeDTO();
		
		// Update the map for easy lookup.
		Map<AQCType, AQCTableItemModel> aqcMap = new HashMap<AQCType, AQCTableItemModel>();
		for (AQCTableItemModel mod : tableModelList) {
			aqcMap.put(mod.getAqcType(), mod);
		}
		
		List<AQCDetailsWithTypeDTO> aqcDetailsWithTypeDTOs = advisorQualityCodeDTO != null?advisorQualityCodeDTO.getAqcDetailsWithTypeDTO():null;
		
		for (AQCDetailsWithTypeDTO aqcDetailsWithTypeDTO : aqcDetailsWithTypeDTOs) {
			if (!aqcMap.isEmpty()) {
				if (aqcDetailsWithTypeDTO != null && aqcDetailsWithTypeDTO.getAqcType().equals(AQCType.RISK)) {

					aqcDetailsWithTypeDTO.setMaxUpfrontCommPercent(aqcMap.get(AQCType.RISK).getMaxUpfrontCommPercent());
					if(aqcMap.get(AQCType.RISK).getMaxUpfrontCommPercentEndDate() != null)
						aqcDetailsWithTypeDTO.setMaxUpfrontCommPercentEndDate(new java.sql.Date(aqcMap.get(AQCType.RISK).getMaxUpfrontCommPercentEndDate().getTime()));
					break;

				}
			}

		}
		
		/*
		 * Set according to calculated / manual AQC codes
		 */
		if(aqcDetailsWithTypeDTOs != null){
			
			for(AQCDetailsWithTypeDTO typeDto : aqcDetailsWithTypeDTOs){
				
				AQCTableItemModel dto = aqcMap.get(typeDto.getAqcType());
				aqcMap.remove(typeDto.getAqcType());
				
				// It wasn't changed so ignore it
				if (!dto.getManualCodeDTO().isMaintainable()) {
					if (typeDto.getManualAQCDTOs() != null 
							&& typeDto.getManualAQCDTOs().size()>0 && dto.getOriginalManualCodeDTO()==null) {
						// If we added it and then removed it again after a save.  Clear it
						typeDto.getManualAQCDTOs().clear();
					}
					continue;
				}
				
				// Changes were done, check if this was existing
				if (typeDto.getManualAQCDTOs() == null || typeDto.getManualAQCDTOs().size()==0) {
					// add it, there was no manual before
					if (typeDto.getManualAQCDTOs() == null)
						typeDto.setManualAQCDTOs(new ArrayList<AQCDTO>());
					typeDto.getManualAQCDTOs().add(dto.getManualCodeDTO());
				}
				
				// Original one was replaced
				//   do nothing as same reference was used.
			}
		}
		
		/*
		 * Check if there are codes set that doesn't have calculated
		 */
		if (aqcMap.size()>0) {
			for (AQCType aqcType : aqcMap.keySet()) {
				AQCTableItemModel mod = aqcMap.get(aqcType);
				
				if (!mod.getManualCodeDTO().isMaintainable()) {
					continue;
				}
				AQCDetailsWithTypeDTO typeDto = new AQCDetailsWithTypeDTO(aqcType);
				List<AQCDTO> manualList = new ArrayList<AQCDTO>();
				manualList.add(mod.getManualCodeDTO());
				typeDto.setManualAQCDTOs(manualList);
				aqcDetailsWithTypeDTOs.add(typeDto);
			}
			
		}
		
	}



	/**
	 * Get the form definition 
	 * 
	 * @author jzb0608
	 *
	 */
	public class AdvisorQualityCodeForm extends Form {
		private static final long serialVersionUID = 5808296649559984427L;

		public AdvisorQualityCodeForm(String id){
			super(id);
			add(getDateContainer());
			add(getEffSegmentPanel());	
			add(aqcGridPanel = getAQCGrid());
			add(new AbstractFormValidator() {
				
				@Override
				public void validate(Form<?> form) {
					if (getEditState().isViewOnly())
						return;
					
					if (logger.isDebugEnabled())
						logger.debug("Validate ");
						
					updateDTOFromModel();
					
					AdvisorQualityCodeDTO advisorQualityCodeDTO = AdvisorQualityCodePanel.this.panelModel.getAdvisorQualityCodeDTO();
					
					if(advisorQualityCodeDTO == null) return;
								
					try {
						getAgreementValidator().validateMaintainAQCDetails(advisorQualityCodeDTO, panelModel.getPartyOid(),
								panelModel.getValidManualAQCValues());
					} catch (ValidationException e) {
						for(String msg:e.getErrorMessages()){
							form.error(msg);
						}
						return;
					}
					
				}
				
				@Override
				public FormComponent<?>[] getDependentFormComponents() {
					return null;
				}
			});
		}
	

		@Override
		protected void onSubmit() {
	
			
			super.onSubmit();
		}

		private void updateParentModelWithSubModelData(AQCDetailsWithTypeDTO typeDTO, SubAdvisorQualityCodePanelModel modelToUse) {
			if(modelToUse == null)
				return;
			List<AQCDTO> manAqcDTOs = modelToUse.getManualAQCDTOs();
			if(manAqcDTOs == null || manAqcDTOs.size() == 0)
				return;
			typeDTO.setManualAQCDTOs(manAqcDTOs);
			
		}

	}
	
	private WebMarkupContainer getDateContainer() {
		if (dateContainer==null) {
//			if (getEditState().isViewOnly()) {
//				dateContainer = new EmptyPanel("dateContainer");
//				System.out.println("viewOnly");
//				return dateContainer;
//			}
			System.out.println("NOT viewOnly");
			dateContainer = new WebMarkupContainer("dateContainer");
//			dateContainer.add(new Label("startDate", DateUtil.getInstance().getNewDateFormat().format(new Date())));
			dateContainer.add(createStartDateField("startDate"));
			dateContainer.add(endDateField=createEndDateField("endDate"));
//			dateContainer.add(createEndDatePicker("endDatePicker",endDateField));
//			dateContainer.setVisible(!getEditState().isViewOnly());
			dateContainer.setVisible(false);
		}
		return dateContainer;
	}
	
	/**
	 * Create Start Date field
	 * 
	 * @param id
	 * @return
	 */
	protected SRSDateField createStartDateField(String id) {
		SRSDateField text = new SRSDateField(id,  new IModel() {
			private static final long serialVersionUID = -1060562129103084694L;

			public Object getObject() {
				return startDate;
			}
			public void setObject(Object arg0) {
				startDate = ((Date) arg0);			
			}
			public void detach() {			
			}
		});
//		text.add(createDateFieldUpdateBehavior("change"));
		text.setOutputMarkupId(true);
		text.setEnabled(false);
		return text;
	}
	
	/**
	 * Create Start Date field
	 * 
	 * @param id
	 * @return
	 */
	protected SRSDateField createEndDateField(String id) {
		SRSDateField text = new SRSDateField(id,  new IModel() {
			private static final long serialVersionUID = -1060562129103084694L;

			public Object getObject() {
				return endDate;
			}
			public void setObject(Object arg0) {
				endDate = ((Date) arg0);			
			}
			public void detach() {			
			}
		});
//		text.add(createDateFieldUpdateBehavior("change"));
		text.setOutputMarkupId(true);
		text.add(text.newDatePicker());
		return text;
	}
	
	@SuppressWarnings("unchecked")
	public Panel getAQCGrid() {
		
		SRSDataGrid tempDataGrid = new SRSDataGrid("aqcGrid",new DataProviderAdapter(
						new ListDataProvider(tableModelList)),createInternalTableFieldColumns(),getEditState()) {
							private static final long serialVersionUID = 1L;
		};
		tempDataGrid.setAutoResize(true);
		tempDataGrid.setOutputMarkupId(true);
		tempDataGrid.setCleanSelectionOnPageChange(false);
		tempDataGrid.setClickRowToSelect(false);
		tempDataGrid.setAllowSelectMultiple(false);
		tempDataGrid.setGridWidth(90, GridSizeUnit.PERCENTAGE);		
		tempDataGrid.setRowsPerPage(15);
		tempDataGrid.setContentHeight(200, SizeUnit.PX);
		return tempDataGrid;
	}

	/**
	 * Return all the columns that are required.  This includes the check box.
	 * @return
	 */
	protected List<IGridColumn> createInternalTableFieldColumns() { 
		List<IGridColumn> columns = new ArrayList<IGridColumn>();
		
		
		/**
		 * This will show the columns required per request kind, for now only 
		 * one kind is configured.
		 */
		columns.add(new SRSDataGridColumn<AQCTableItemModel>("aqcType.pretty",
				new Model("AQC Type"),"aqcType.pretty",getEditState()).setInitialSize(130));

		/*
		 * The manual one has a drop down selection in edit mode
		 */
		columns.add(new SRSDataGridColumn<AQCTableItemModel>("manualCodeDTO.aqcValueDTO",
				new Model("Manual Value"),"manualCodeDTO.aqcValueDTO",getEditState()) {

			@Override
			public Panel newCellPanel(WebMarkupContainer parent,
					String componentId, IModel rowModel,
					String objectProperty, EditStateType state,
					final AQCTableItemModel data) {	
					
					if(state.isViewOnly()){
						Label label = new Label("value", data.getManualCodeDTO().getAqcValueDTO().getText());				
						return HelperPanel.getInstance(componentId, label);
					
					}
					
					//create Manual AQC dropdown
					
					HelperPanel panel = createDropdownField(data, 
							"Manual AQC", componentId, data.getManualAQCValidValues(), 
								new ChoiceRenderer("text", "id"), "Select", false, getEditState());
					
					if(panel.getEnclosedObject() instanceof DropDownChoice){
						final SRSDropDownChoice dropDownChoice = (SRSDropDownChoice) panel.getEnclosedObject();
						
						dropDownChoice.setOutputMarkupId(true);
						dropDownChoice.setOutputMarkupPlaceholderTag(true);
						// Fix - On add allow to be set to null
						dropDownChoice.setNullValid(getEditState().isAdd());
						
						dropDownChoice.add(new AjaxFormComponentUpdatingBehavior("change"){
								private static final long serialVersionUID = 1L;
								
								@Override
								protected void onUpdate(AjaxRequestTarget target) {	
									
									boolean isNull = (data.getManualCodeDTO().getAqcValueDTO()==null 
											|| data.getManualCodeDTO().getAqcValueDTO().getText()==null);
									
									
									
									boolean isMaintain = data.getManualCodeDTO().isMaintainable();
									
									if (getEditState().isAdd() && isNull && data.getManualCodeDTO().getEndDate()!=null) {
										// Only on Add do we allow this
										data.getManualCodeDTO().setEndDate(null);
										target.add(aqcGridPanel);
										isMaintain = false;
									} else if (isNull && isMaintain) {
										isMaintain = false;
									} else if (!isNull && !isMaintain && data.getOriginalManualCodeDTO()!=null
											&& data.getOriginalManualCodeDTO().getAqcValueDTO()!=null
											&& data.getOriginalManualCodeDTO().getAqcValueDTO().equals(
													data.getManualCodeDTO().getAqcValueDTO())){
										isMaintain = false;
									} else {
										isMaintain = true;
									}
									
									data.getManualCodeDTO().setMaintainable(isMaintain);

									if (logger.isDebugEnabled())
										logger.debug("Manual changed isMaint="
												+ data.getManualCodeDTO().isMaintainable()
												+ "  val=" + data.getManualCodeDTO().getAqcValueDTO());
								}												
						});
		
					}
						return panel;					
				}

		}.setInitialSize(100));
		
		SRSDataGridColumn manualHistoryCol = new SRSDataGridColumn<AQCTableItemModel>("manualHistory",
				new Model("Manual History"),"manualHistory",getEditState()) {
			
			private static final long serialVersionUID = 1L;
			
			@Override
			public Panel newCellPanel(WebMarkupContainer parent,
					String componentId, IModel rowModel,
					String objectProperty, EditStateType state,
					final AQCTableItemModel dataItem) {	
	
				
				Button but = new Button("value");
				but.setEnabled(getEditState()!=EditStateType.AUTHORISE);
				but.add(new AttributeModifier("value","History"));
				but.add(new AjaxFormComponentUpdatingBehavior("click") {
					
					private static final long serialVersionUID = 1L;
					@Override
					protected void onUpdate(AjaxRequestTarget target) {
				
						boolean isHistoryFetched = dataItem.isManualAQCHistoryFetched();
						/**
						 * Update the Panel model with the 
						 * Calculated AQC History details before showing the dialog first time
						 */
						if(!isHistoryFetched){
							List<PartyAQCHistoryFLO> list = null;
							
							Long partyOid = panelModel.getPartyOid();
							if(partyOid == null){
								error("Sever error caused as PartyOid passed is null");
								target.add(getFeedBackPanel());
							}else{
							
							
							list = getPartyMaintenanceController().getAQCHistoryForPartyAndAQCType(partyOid, 
									dataItem.getAqcType().getManualType());
							
							if (logger.isDebugEnabled())
								logger.debug("Searched for Manual History "
										+ "   partyOid=" +partyOid
										+ "   aqcType=" +dataItem.getAqcType()
										+ "   aqcType=" +dataItem.getAqcType().getManualType()
										+ "   list=" + list.size()
										);
							dataItem.getManualAQCHistory().clear();
							dataItem.getManualAQCHistory().addAll(list);
							dataItem.setManualAQCHistoryFetched(true);
							}
						}
						
						panelModel.getManualAQCHistory().clear();
						panelModel.getManualAQCHistory().addAll(dataItem.getManualAQCHistory());
						panelModel.setTitle("Manual " + dataItem.getAqcType().getPretty());
						/**
						 * Show the dialog
						 */
						getManualAQCHistoryWindow().show(target);
					}
				});
//				calcAQCHistoryButton.setVisible(getEditState() !=EditStateType.AUTHORISE);
				
				return HelperPanel.getInstance(componentId, but);
								
//				}
			}
		
		};
		manualHistoryCol.setInitialSize(90);
		columns.add(manualHistoryCol);

		/*
		 * End date has a date picker in edit mode
		 */
		SRSDataGridColumn endDateCol = new SRSDataGridColumn<AQCTableItemModel>("manualCodeDTO.endDate",
				new Model("End Date"),"manualCodeDTO.endDate",getEditState()) {
			
			private static final long serialVersionUID = 1L;
			
			@Override
			public Panel newCellPanel(WebMarkupContainer parent,
					String componentId, IModel rowModel,
					String objectProperty, EditStateType state,
					final AQCTableItemModel dataItem) {	
					
				if(state.isViewOnly()){
					Label label = new Label("value", new PropertyModel(dataItem, componentId));			
					return HelperPanel.getInstance(componentId, label);
				
				}
				
				HelperPanel panel = createPageField(dataItem, "End Date", componentId, ComponentType.DATE_SELECTION_TEXTFIELD, false, false, getEditState());
					
				panel.getEnclosedObject().setOutputMarkupId(true);
				panel.getEnclosedObject().setOutputMarkupPlaceholderTag(true);
				
				if ( panel.getEnclosedObject() instanceof SRSDateField) {
						((SRSDateField)panel.getEnclosedObject()).add(((SRSDateField)panel.getEnclosedObject()).newDatePicker());
				}
				panel.getEnclosedObject().add(new AjaxFormComponentUpdatingBehavior("change"){
						private static final long serialVersionUID = 1L;
						
						@Override
						protected void onUpdate(AjaxRequestTarget target) {	
							
							boolean isNull = (dataItem.getManualCodeDTO().getEndDate()==null);
							boolean isMaintain = dataItem.getManualCodeDTO().isMaintainable();
							
							if (isNull && isMaintain) {
								// should never happen
								isMaintain = false;
							} else if (!isNull && isMaintain && dataItem.getOriginalManualCodeDTO()!=null
									&& dataItem.getOriginalManualCodeDTO().getEndDate()!=null) {
								
								if (logger.isDebugEnabled())
									logger.debug("Check if same as original   original EndDate ="
											+ dataItem.getOriginalManualCodeDTO().getEndDate()
											+ "  current end date=" + dataItem.getManualCodeDTO().getEndDate());
						
								if (DateUtil.getInstance().compareDatePart(dataItem.getManualCodeDTO().getEndDate(), 
										dataItem.getOriginalManualCodeDTO().getEndDate())==0) {
									isMaintain = false;
								}
							} else {
								isMaintain = true;
							}
							
							dataItem.getManualCodeDTO().setMaintainable(isMaintain);

							if (logger.isDebugEnabled())
								logger.debug("Manual changed isMaint="
										+ dataItem.getManualCodeDTO().isMaintainable()
										+ "  val=" + dataItem.getManualCodeDTO().getEndDate());
						}												
				});	
				panel.getEnclosedObject().add(new AjaxFormComponentUpdatingBehavior("keyup"){
					private static final long serialVersionUID = 1L;
					
					@Override
					protected void onUpdate(AjaxRequestTarget target) {	
						
//						boolean isNull = (dataItem.getManualCodeDTO().getEndDate()==null);
//						boolean isMaintain = dataItem.getManualCodeDTO().isMaintainable();
//						
//						if (isNull && isMaintain) {
//							// should never happen
//							isMaintain = false;
//						} else if (!isNull && !isMaintain && dataItem.getOriginalManualCodeDTO()!=null
//								&& dataItem.getOriginalManualCodeDTO().getEndDate()!=null
//								&& dataItem.getManualCodeDTO().equals(dataItem.getOriginalManualCodeDTO().getEndDate()!=null)){
//							isMaintain = false;
//						} else {
//							isMaintain = true;
//						}
//						
//						dataItem.getManualCodeDTO().setMaintainable(isMaintain);

						if (logger.isDebugEnabled())
							logger.debug("Manual changed (keyup) isMaint="
									+ dataItem.getManualCodeDTO().isMaintainable()
									+ "  val=" + dataItem.getManualCodeDTO().getEndDate());
					}												
				});				
				return panel;					
//				}
			}
		
		};
		endDateCol.setSizeUnit(SizeUnit.PX);
		endDateCol.setMinSize(140);
		endDateCol.setInitialSize(140);
		columns.add(endDateCol);
		
		/*
		 * Calculated has a label and a history button
		 */
		columns.add(new SRSDataGridColumn<AQCTableItemModel>("calculcatedCodeDTO.text",
				new Model("Calculated"),"calculcatedCodeDTO.aqcValueDTO.text",getEditState()).setInitialSize(80));
			
		SRSDataGridColumn calculatedCol = new SRSDataGridColumn<AQCTableItemModel>("calcHistory",
				new Model("Calc History"),"calcHistory",getEditState()) {
			
			private static final long serialVersionUID = 1L;
			
			@Override
			public Panel newCellPanel(WebMarkupContainer parent,
					String componentId, IModel rowModel,
					String objectProperty, EditStateType state,
					final AQCTableItemModel dataItem) {	
				
				Button but = new Button("value");
				but.setEnabled(getEditState()!=EditStateType.AUTHORISE);
				but.add(new AttributeModifier("value","History"));
				but.add(new AjaxFormComponentUpdatingBehavior("click") {
					
					private static final long serialVersionUID = 1L;
					@Override
					protected void onUpdate(AjaxRequestTarget target) {
				
						System.out.println("Clicked the button");
						boolean isHistoryFetched = dataItem.isCalcAQCHistoryFetched();
						/**
						 * Update the Panel model with the 
						 * Calculated AQC History details before showing the dialog first time
						 */
						if(!isHistoryFetched){
							List<PartyAQCHistoryFLO> list = null;
							
							Long partyOid = panelModel.getPartyOid();
							if(partyOid == null){
								error("Sever error caused as PartyOid passed is null");
								target.add(getFeedBackPanel());
							}else{
							
							// Retrieve calc history, including replaced objects due to loading file issues
							list = getPartyMaintenanceController().getAQCHistoryForPartyAndAQCType(partyOid, 
									dataItem.getAqcType().getCalculatedType(), true);
							
							if (logger.isDebugEnabled())
								logger.debug("Searched for Calculated History "
										+ "   partyOid=" +partyOid
										+ "   aqcType=" +dataItem.getAqcType()
										+ "   aqcType=" +dataItem.getAqcType().getCalculatedType()
										+ "   list=" + list.size()
										);
							dataItem.getCalculatedAQCHistory().clear();
							dataItem.getCalculatedAQCHistory().addAll(list);
							dataItem.setCalcAQCHistoryFetched(true);
							}
						}
						
						panelModel.setTitle("Calculated " + dataItem.getAqcType().getPretty());
						panelModel.getCalculatedAQCHistory().clear();
						panelModel.getCalculatedAQCHistory().addAll(dataItem.getCalculatedAQCHistory());
						panelModel.setTitle("Calculated " + dataItem.getAqcType().getPretty());
						/**
						 * Show the dialog
						 */
						getCalcAQCHistoryWindow().show(target);
					}
				});
//				calcAQCHistoryButton.setVisible(getEditState() !=EditStateType.AUTHORISE);
				
				return HelperPanel.getInstance(componentId, but);
								
//				}
			}
		
		};
		calculatedCol.setInitialSize(90);
		columns.add(calculatedCol);
		
		
		
		
		columns.add(new SRSDataGridColumn<AQCTableItemModel>("effAqcValue",
				new Model("Effective Value"),"effectiveValue",getEditState()).setInitialSize(90));
		
		columns.add(getMaxUpfrontCommPercentage());
		columns.add(getMaxUpfrontCommPercentageEndDate());
		
		return columns;
	}
	
	@SuppressWarnings("unchecked")
	private SRSDataGridColumn<AQCTableItemModel> getMaxUpfrontCommPercentage() {

		SRSDataGridColumn maxUpfrontCommPercentColumn = new SRSDataGridColumn<AQCTableItemModel>("maxUpfrontCommPercent", new Model("Max Upfront Percentage"), "maxUpfrontCommPercent", getEditState()) {
			private static final long serialVersionUID = 1L;

			@Override
			public Panel newCellPanel(WebMarkupContainer parent, String componentId, IModel rowModel, String objectProperty, EditStateType state, final AQCTableItemModel dataItem) {

				if (state.isViewOnly()) {
					Label label = new Label("value", new PropertyModel(dataItem, componentId));
					return HelperPanel.getInstance(componentId, label);

				}

				HelperPanel panel = createPageField(dataItem, "Max Upfront Percentage", componentId, ComponentType.TEXTFIELD, false, false, getEditState());

				panel.getEnclosedObject().setOutputMarkupId(true);
				panel.getEnclosedObject().setOutputMarkupPlaceholderTag(true);
				panel.getEnclosedObject().add(new AjaxFormComponentUpdatingBehavior("change"){

					@Override
					protected void onUpdate(AjaxRequestTarget target) {
					}		
				});
				
				//Users should not be able to updat this feld when creating a new agreement
				if(EditStateType.ADD.equals(getEditState())){
					panel.setEnabled(false);
				}
				
				//The override is only allowed for the RISK AQC type
				if (AQCType.RISK.equals(dataItem.getAqcType())) {
					panel.setVisible(true);
				} else {
					panel.setVisible(false);
				}
				return panel;
			}
		};

		return maxUpfrontCommPercentColumn;
	}
	
	private SRSDataGridColumn<AQCTableItemModel> getMaxUpfrontCommPercentageEndDate() {

		SRSDataGridColumn maxUpfrontCommPercentColumnEndDate = new SRSDataGridColumn<AQCTableItemModel>("maxUpfrontCommPercentEndDate", new Model("Max Upfront Percentage End Date"), "maxUpfrontCommPercentEndDate", getEditState()) {
			private static final long serialVersionUID = 1L;

			@Override
			public Panel newCellPanel(WebMarkupContainer parent, String componentId, IModel rowModel, String objectProperty, EditStateType state, final AQCTableItemModel dataItem) {

				if (state.isViewOnly()) {
					Label label = new Label("value", new PropertyModel(dataItem, componentId));
					return HelperPanel.getInstance(componentId, label);

				}

				HelperPanel panel = createPageField(dataItem, "Max Upfront Percentage End Date", componentId, ComponentType.DATE_SELECTION_TEXTFIELD, false, false, getEditState());

				
				panel.getEnclosedObject().setOutputMarkupId(true);
				panel.getEnclosedObject().setOutputMarkupPlaceholderTag(true);
				if ( panel.getEnclosedObject() instanceof SRSDateField) {
					((SRSDateField)panel.getEnclosedObject()).add(((SRSDateField)panel.getEnclosedObject()).newDatePicker());
				}
				
				panel.getEnclosedObject().add(new AjaxFormComponentUpdatingBehavior("change"){

					@Override
					protected void onUpdate(AjaxRequestTarget target) {		
						//validatePercentage(dataItem, target);
					}		
				});
				
				//Users should not be able to updat this feld when creating a new agreement
				if(EditStateType.ADD.equals(getEditState())){
					panel.setEnabled(false);
				}
				
				//The override is only allowed for the RISK AQC type
				if (AQCType.RISK.equals(dataItem.getAqcType())) {
					panel.setVisible(true);
				} else {
					panel.setVisible(false);
				}
				return panel;
			}
		};

		return maxUpfrontCommPercentColumnEndDate;
	}
	
	/*private void validatePercentage(final AQCTableItemModel dataItem, AjaxRequestTarget target) {
		if(dataItem.getMaxUpfrontCommPercent() != null 
				&& (dataItem.getMaxUpfrontCommPercent().isGreaterThan(new Percentage(new BigDecimal(1.00)))
						|| dataItem.getMaxUpfrontCommPercent().isLessThan(new Percentage(new BigDecimal(0.00))))){
			error("Percentage value should be between 1 and 100");
			target.addComponent(getFeedBackPanel());
		}
	}*/

	/**
	 * Get the effective segment panel
	 * @return
	 */
	public HelperPanel getEffSegmentPanel() {

		if (effSegmentPanel == null) {
			String segment = SRSAppWebConstants.EMPTY_STRING;
			AgreementGUIField field = AgreementGUIField.EFFECTIVE_SEGMENT;
			if(this.panelModel != null && this.panelModel.getAdvisorQualityCodeDTO()!= null){
				SegmentNameDTO segmentName =  this.panelModel.getAdvisorQualityCodeDTO().getSegment();
				if(segmentName != null)
					segment = segmentName.getSegmentName();
			}
			
			Label label = new Label("value", segment);
			effSegmentPanel = HelperPanel.getInstance(field.getFieldId(), label);			
		}
		return effSegmentPanel;		
	}
	
	
	public Class getPanelClass() {
		return getClass();
	}
		
		
	@Override
	public EditStateType getEditState() {
		//will disable any modification if there are any requests pending auth
		if(existingAQCRequest){
			return EditStateType.VIEW;
		}
		return super.getEditState();
	}
	
	/**
	 * Set/update the panel model
	 * 
	 * This method can be used prior to rendering the panel to ensure
	 * that the model is up to date.
	 * 
	 * @param panelModel
	 */
	public void setPanelModel(AdvisorQualityCodePanelModel panelModel) {
		this.panelModel = panelModel;
		/**
		 * When updating the model, reset the view template context
		 * so that it will be re-created
		 */
		this.viewTemplateContext = null; 
	}

	@Override
	protected ContextDrivenViewTemplate<AgreementGUIField, AgreementDTO> getViewTemplate() {
		return this.panelModel.getViewTemplate();
	}

	@Override
	protected AgreementDTO getViewTemplateContext() {
		if (viewTemplateContext == null) {
			viewTemplateContext = new AgreementDTO();
		}
		return viewTemplateContext;
	}
	
	protected IAgreementValidator getAgreementValidator(){
		if(agreementValidator == null){
			try {
				agreementValidator = ServiceLocator.lookupService(IAgreementValidator.class);
			} catch (NamingException e) {
				throw new CommunicationException(e);
			}
		}
		return agreementValidator;
	}
	
	
	private IPartyMaintenanceController getPartyMaintenanceController() {
		if(partyMaintenanceController == null){
				try {
					partyMaintenanceController = ServiceLocator.lookupService(IPartyMaintenanceController.class);
				} catch (NamingException e) {
					throw new CommunicationException(e);
				}
			}
			return partyMaintenanceController;
		
	}
	
	private IDifferentialFactorEntityManager getDifferentialFactorEntityManager() {

		if (differentialFactorEntityManager == null) {
			try {
				differentialFactorEntityManager = ServiceLocator.lookupService(IDifferentialFactorEntityManager.class);
			} catch (NamingException e) {
				throw new CommunicationException(e);
			}
		}

		return differentialFactorEntityManager;
	}
}