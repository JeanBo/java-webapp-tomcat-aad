package za.co.liberty.web.pages.maintainagreement;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.naming.NamingException;

import org.apache.log4j.Logger;
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
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.convert.IConverter;

import za.co.liberty.business.guicontrollers.partymaintenance.IPartyMaintenanceController;
import za.co.liberty.dto.agreement.AgreementDTO;
import za.co.liberty.dto.party.aqcdetail.AQCDTO;
import za.co.liberty.dto.party.aqcdetail.AQCValueDTO;
import za.co.liberty.dto.persistence.party.flow.PartyAQCHistoryFLO;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.helpers.lookup.ServiceLocator;
import za.co.liberty.helpers.util.DateUtil;
import za.co.liberty.interfaces.rating.difffactor.AQCType;
import za.co.liberty.srs.type.SRSType;
import za.co.liberty.web.data.enums.ComponentType;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.data.enums.fields.AgreementGUIField;
import za.co.liberty.web.pages.maintainagreement.model.SubAdvisorQualityCodePanelModel;
import za.co.liberty.web.pages.panels.GUIFieldPanel;
import za.co.liberty.web.pages.panels.HelperPanel;
import za.co.liberty.web.pages.panels.ViewTemplateBasePanel;
import za.co.liberty.web.wicket.markup.html.form.SRSDropDownChoice;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGrid;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGrid.GridSizeUnit;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGridColumn;
import za.co.liberty.web.wicket.markup.repeater.data.ListDataProvider;
import za.co.liberty.web.wicket.view.ContextDrivenViewTemplate;

import com.inmethod.grid.DataProviderAdapter;
import com.inmethod.grid.IGridColumn;
import com.inmethod.grid.SizeUnit;

public class SubAdvisorQualityCodePanel extends ViewTemplateBasePanel<AgreementGUIField, AgreementDTO>{

	private static final long serialVersionUID = 1L;
	
	private GUIFieldPanel calcAQCValuesPanel;
	private GUIFieldPanel effAQCValuesPanel;
	private Button calcAQCHistoryButton;
	private SRSDataGrid manualAQCValuesGrid;
	private Button manualAQCHistoryBtn;
	private Button addManualAQCBtn;
	private SubAdvisorQualityCodePanelModel panelModel;
	private AgreementDTO viewTemplateContext;
	private SubAdvisorQualityForm pageForm;
	private List<IGridColumn> aqcColumns;
	
	private HelperPanel titlePanel;
	
	private List<FormComponent> validationComponents = new ArrayList<FormComponent>();
	private FeedbackPanel feedbackPanel;
	
	private ModalWindow calcAQCHistoryWindow;
	private ModalWindow manualAQCHistoryWindow;

	private transient IPartyMaintenanceController partyMaintenanceController;
	
	private static final Logger logger = Logger.getLogger(SubAdvisorQualityCodePanel.class);
	
	public SubAdvisorQualityCodePanel(String id, EditStateType editState, SubAdvisorQualityCodePanelModel panelModel,Page parentPage,FeedbackPanel feedbackPanel) {
		super(id, editState,parentPage);
		this.panelModel = panelModel;
		this.feedbackPanel =feedbackPanel;
		add(getSubAqcForm());	
		add(getCalcAQCHistoryWindow());
		add(getManualAQCHistoryWindow());
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
	
	
	public enum CalcManualAQCType{
		
		CALCULATED,MANUAL;
		
	}
	

	public SubAdvisorQualityForm getSubAqcForm(){
		
		if(pageForm == null){			
			pageForm = new SubAdvisorQualityForm("subAqcForm");			
		}
		return pageForm;
	}
	
	
	public class SubAdvisorQualityForm extends Form {
		private static final long serialVersionUID = 5808296649559984427L;

		public SubAdvisorQualityForm(String id) {
			super(id);			
			add(getTitlePanel());
			add(getCalcAQCValuesPanel());
			add(getEffAQCValuesPanel());					
			add(getCalcAQCHistoryButton());
			add(getManualAQCValuesGrid());
			add(getAddManualAQCBtn());
			add(getManualAQCHistoryBtn());	
			
		}		
	
		
	}
	
	
	public GUIFieldPanel getCalcAQCValuesPanel() {

		if (calcAQCValuesPanel == null) {
			AgreementGUIField field = AgreementGUIField.CALC_AQC_VALUES;
			calcAQCValuesPanel = 
				createGUIFieldPanel(
						field,
						createPageField(
								getPropertyModelTarget(), 
								field.getDescription(),field.getFieldId(), 
								ComponentType.LABEL, 
								false,false,new EditStateType[0]).getEnclosedObject());
		}
		return calcAQCValuesPanel;		
		
	}	
	
	public Button getCalcAQCHistoryButton() {
		if (calcAQCHistoryButton==null) {
			calcAQCHistoryButton = new Button("calAQCHistBtn");
			calcAQCHistoryButton.add(new AjaxFormComponentUpdatingBehavior("click") {
			
				private static final long serialVersionUID = 1L;
				@Override
				protected void onUpdate(AjaxRequestTarget target) {
					
					boolean isHistoryFetched = panelModel.isCalcAQCHistoryFetched();
					/**
					 * Update the Panel model with the 
					 * Calculated AQC History details before showing the dialog first time
					 */
					if(!isHistoryFetched){
						List<PartyAQCHistoryFLO> list = null;
						
						Long partyOid = panelModel.getParentPanelModel().getPartyOid();
						if(partyOid == null){
							error("Sever error caused as PartyOid passed is null");
							target.add(getFeedBackPanel());
						}else{
						
						list = getPartyMaintenanceController().getAQCHistoryForPartyAndAQCType(partyOid, 
								getPartyRegTypeForAQCType(panelModel.getAqcType(),CalcManualAQCType.CALCULATED));
						
						panelModel.getCalculatedAQCHistory().clear();
						panelModel.getCalculatedAQCHistory().addAll(list);
						panelModel.setCalcAQCHistoryFetched(true);
						}
					}
					/**
					 * Show the dialog
					 */
					getCalcAQCHistoryWindow().show(target);
				}
			});
		}
		calcAQCHistoryButton.setVisible(getEditState() !=EditStateType.AUTHORISE);
		return calcAQCHistoryButton;
	}

	public GUIFieldPanel getEffAQCValuesPanel() {

		if (effAQCValuesPanel == null) {
			AgreementGUIField field = AgreementGUIField.EFFECTIVE_AQC_VALUES;
			effAQCValuesPanel =  
					createGUIFieldPanel(
							field,
							createPageField(
									getPropertyModelTarget(), 
									field.getDescription(),field.getFieldId(), 
									ComponentType.LABEL, 
									false,false,new EditStateType[0]).getEnclosedObject());
			
		}
		return effAQCValuesPanel;		
		
	}
	
	
	public HelperPanel getTitlePanel() {
		if(titlePanel == null){
			Label lbl = new Label("value",new PropertyModel(getPropertyModelTarget(),"title"));
			titlePanel = HelperPanel.getInstance("title",lbl);
		}
		return titlePanel;
	}
	
	public SRSDataGrid getManualAQCValuesGrid() {
		if (manualAQCValuesGrid == null) {
			List<AQCDTO> manualAQCDTOList = panelModel.getManualAQCDTOs();
			if(manualAQCDTOList == null)
				manualAQCDTOList = new ArrayList<AQCDTO>();
			
			manualAQCValuesGrid = new SRSDataGrid("manualAQCValuesGrid",
					new DataProviderAdapter(new ListDataProvider<AQCDTO>(
							manualAQCDTOList)),
					getManualAQCColumn(),EditStateType.VIEW);
			manualAQCValuesGrid.setOutputMarkupId(true);
			manualAQCValuesGrid.setCleanSelectionOnPageChange(false);
			manualAQCValuesGrid.setAllowSelectMultiple(false);
			manualAQCValuesGrid.setGridWidth((getEditState() == EditStateType.ADD)?70:55, GridSizeUnit.PERCENTAGE);
			manualAQCValuesGrid.setRowsPerPage(4);
			manualAQCValuesGrid.setContentHeight(75, SizeUnit.PX);
			manualAQCValuesGrid.setAutoCalculateTableHeight(getEditState().isViewOnly());
			manualAQCValuesGrid.setAutoResize(true);
		}
		return manualAQCValuesGrid;
	}
	
	private List<IGridColumn> getManualAQCColumn() {
		if (aqcColumns == null) {
			aqcColumns = new ArrayList<IGridColumn>();
			
			
			SRSDataGridColumn manualAQCCol = new SRSDataGridColumn<AQCDTO>("aqcValueDTO",
					new Model("Manual AQC"),"aqcValueDTO",getEditState()){
				
				private static final long serialVersionUID = 1L;

				@Override
				public Panel newCellPanel(WebMarkupContainer parent, String componentId, IModel rowModel, String objectProperty, EditStateType state, final AQCDTO data) {
					
					if(state.isViewOnly() || !data.isMaintainable()){
						Label label = new Label("value",new PropertyModel(data,objectProperty)){
							
							private static final long serialVersionUID = 1L;

							@Override
							public IConverter getConverter(Class type) {
								return manualAQCConverter;
							}
						};				
						return HelperPanel.getInstance(componentId, label);
					
					}else{
					//create Manual AQC dropdown
					HelperPanel panel = createDropdownField(data, "Manual AQC", componentId, panelModel.getManualAQCValidValues(),  new ChoiceRenderer("text", "id"), "Select", true, getEditState());
					if(panel.getEnclosedObject() instanceof DropDownChoice){
						final SRSDropDownChoice dropDownChoice = (SRSDropDownChoice) panel.getEnclosedObject();
						
						dropDownChoice.setOutputMarkupId(true);
						dropDownChoice.setOutputMarkupPlaceholderTag(true);
						dropDownChoice.add(new AjaxFormComponentUpdatingBehavior("change"){
								private static final long serialVersionUID = 1L;
								
								@Override
								protected void onUpdate(AjaxRequestTarget target) {	
									
									String inp = dropDownChoice.getInput();										
									
									List<AQCDTO> list = SubAdvisorQualityCodePanel.this.panelModel.getManualAQCDTOs();
									for(AQCDTO aqcdto:list){
										if(aqcdto.isMaintainable()){
											aqcdto.setAqcValueDTO(new AQCValueDTO(inp));
										}										
									}
								}												
						});
//						dropDownChoice
						validationComponents.add(dropDownChoice);		
					}
					return panel;					
					}
				}
				
			};
			manualAQCCol.setSizeUnit(SizeUnit.PX);
			manualAQCCol.setMinSize(100);
			manualAQCCol.setInitialSize(100);
			aqcColumns.add(manualAQCCol);
			
					
			SRSDataGridColumn startDtCol = new SRSDataGridColumn<AQCDTO>("startDate",
					new Model("Start Date"),"startDate",getEditState()){
				private static final long serialVersionUID = 1L;

				@Override
				public Panel newCellPanel(WebMarkupContainer parent, String componentId, IModel rowModel, String objectProperty, EditStateType state, final AQCDTO data) {
					
					return super.newCellPanel(parent, componentId, rowModel, objectProperty, state,
							data);				
				}
				
			};
			startDtCol.setSizeUnit(SizeUnit.PX);
			startDtCol.setMinSize(120);
			startDtCol.setInitialSize(120);
			aqcColumns.add(startDtCol);
			
			SRSDataGridColumn endDateCol = new SRSDataGridColumn<AQCDTO>("endDate",
					new Model("End Date"),"endDate",getEditState()){

				private static final long serialVersionUID = 1L;

				@Override
				public Panel newCellPanel(WebMarkupContainer parent, String componentId, IModel rowModel, String objectProperty, EditStateType state, final AQCDTO data) {
					
					if(state == EditStateType.VIEW || !data.isMaintainable()){
						return super.newCellPanel(parent, componentId, rowModel, objectProperty, state,
							data);
					} else{
					//create End Date picker
					HelperPanel panel = createPageField(data, "End Date", componentId, ComponentType.DATE_SELECTION_TEXTFIELD, false, true, getEditState());
					
					if(panel.getEnclosedObject() instanceof TextField){
						
						validationComponents.add((TextField)panel.getEnclosedObject());		
						}	
					
					return panel;					
					}
				}
				
			
			};
			endDateCol.setSizeUnit(SizeUnit.PX);
			endDateCol.setMinSize(120);
			endDateCol.setInitialSize(120);
			aqcColumns.add(endDateCol);
			
			if(!getEditState().isViewOnly()){
				//Create End button
				SRSDataGridColumn endActionCol = new SRSDataGridColumn<AQCDTO>("endAction",
						new Model("End"),null,getEditState()){

					private static final long serialVersionUID = 1L;

					@Override
					public Panel newCellPanel(WebMarkupContainer parent, String componentId, IModel rowModel, String objectProperty, EditStateType state, final AQCDTO data) {
						//create Manual AQC dropdown
						
						Button endBtn =  new Button("value", new Model("End"));
						endBtn.add(new AjaxFormComponentUpdatingBehavior("click"){									
							private static final long serialVersionUID = 1L;
							@Override
							protected void onUpdate(AjaxRequestTarget target) {
								//this means the end buttton on AQC has been pressed. End date changed.
								//To invoke the party registration process for persistency, setting 
								//isMaintainable to true, as a change did happen.
								logger.info("AQC code ended- AQC ID:"+data.getAqcValueDTO().getId()+" - from end date:"+ data.getEndDate() + " to " + new Date(System.currentTimeMillis()));
								data.setEndDate(new Date(System.currentTimeMillis()));
								data.setMaintainable(true);
								target.add(manualAQCValuesGrid);	
								target.add(getFeedBackPanel());
							}									
						});
						
						if (data.isMaintainable()){
							endBtn.setVisible(false);
						}	
						endBtn.setOutputMarkupPlaceholderTag(true);
						endBtn.setOutputMarkupId(true);
						
						HelperPanel panel = HelperPanel.getInstance(componentId, endBtn);
											
						return panel;					
					}
					
				
				};
				
				endActionCol.setSizeUnit(SizeUnit.PX);
				endActionCol.setMinSize(75);
				endActionCol.setInitialSize(75);
				aqcColumns.add(endActionCol);
			}		
		}
		return aqcColumns;
	}
	
	private Button getAddManualAQCBtn() {
				
		if (addManualAQCBtn==null) {
			addManualAQCBtn = new Button("addManualAQCBtn");
			addManualAQCBtn.add(new AjaxFormComponentUpdatingBehavior("click") {
			
				private static final long serialVersionUID = 1L;
				@Override
				protected void onUpdate(AjaxRequestTarget target) {
					target.add(getFeedBackPanel());
					List<AQCDTO> aqcDTOs = panelModel.getManualAQCDTOs();
					boolean addManualAQCExists = false;
					
					for(AQCDTO aqcDTO:aqcDTOs){
						if(aqcDTO.isMaintainable()){
							addManualAQCExists = true;
							break;
						}
					}
					
					if(!addManualAQCExists){
						if(aqcDTOs.size() == 1 && DateUtil.getInstance().compareDatePart(new Date(), aqcDTOs.get(0).getEndDate()) != 0)
							error("Please End the existing Manual AQC before adding a new one...");	
						else
						{
							AQCDTO aqcdto = new AQCDTO();
							aqcdto.setStartDate(new Date());
							aqcdto.setMaintainable(true);
							panelModel.getManualAQCDTOs().add(aqcdto);
							target.add(manualAQCValuesGrid);
						}
//					}	
					} else{
						AQCDTO aqcdto = new AQCDTO();
						aqcdto.setStartDate(new Date());
						aqcdto.setMaintainable(true);
						panelModel.getManualAQCDTOs().add(aqcdto);
						target.add(manualAQCValuesGrid);
					}
			}
			});
		}
		
		if(getEditState().isViewOnly()){
			addManualAQCBtn.setVisible(false);
		}
		addManualAQCBtn.setOutputMarkupId(true);
		addManualAQCBtn.setOutputMarkupPlaceholderTag(true);
		return addManualAQCBtn;
	}
	

	

	
	public Button getManualAQCHistoryBtn() {
		if (manualAQCHistoryBtn==null) {
			manualAQCHistoryBtn = new Button("manAQCHistBtn");
			manualAQCHistoryBtn.add(new AjaxFormComponentUpdatingBehavior("click") {
			
				private static final long serialVersionUID = 1L;
				@Override
				protected void onUpdate(AjaxRequestTarget target) {
					
					boolean isHistoryFetched = panelModel.isManualAQCHistoryFetched();
					/**
					 * Update the Panel model with the 
					 * Manual AQC History details before showing the dialog first time
					 */
					if(!isHistoryFetched){
						
						List<PartyAQCHistoryFLO> list = null;

						Long partyOid = panelModel.getParentPanelModel().getPartyOid();
						if(partyOid == null){
							error("Sever error caused as PartyOid passed is null");
							target.add(getFeedBackPanel());
						}else{
						
						list  = getPartyMaintenanceController().getAQCHistoryForPartyAndAQCType(partyOid, 
									getPartyRegTypeForAQCType(panelModel.getAqcType(),CalcManualAQCType.MANUAL));
							
							panelModel.getManualAQCHistory().clear();
							panelModel.getManualAQCHistory().addAll(list);
							panelModel.setManualAQCHistoryFetched(true);	
						}
												
					}
					/**
					 * Show the dialog
					 */
					getManualAQCHistoryWindow().show(target);
				}
			});
		}
		manualAQCHistoryBtn.setVisible(getEditState() !=EditStateType.AUTHORISE);
		return manualAQCHistoryBtn;
	}
	
	
	
	private long getPartyRegTypeForAQCType(AQCType aqcType, CalcManualAQCType calcManualAqcType) {
		
		long partyRegType = 0l;
		switch(aqcType){
		
			case RISK: partyRegType = (calcManualAqcType == CalcManualAQCType.CALCULATED)?SRSType.AQCCALCULATEDRISK:SRSType.AQCMANUALRISK;break;
			case INVESTMENT:partyRegType = (calcManualAqcType == CalcManualAQCType.CALCULATED)?SRSType.AQCCALCULATEDINVEST:SRSType.AQCMANUALINVEST;break;
			case ELM_RISK:partyRegType = (calcManualAqcType == CalcManualAQCType.CALCULATED)?SRSType.AQCCALCULATEDELMRISK:SRSType.AQCMANUALELMRISK;break;
			case ELM_INVESTMENT:partyRegType = (calcManualAqcType == CalcManualAQCType.CALCULATED)?SRSType.AQCCALCULATEDELMINVEST:SRSType.AQCMANUALELMINVEST;
		}
		return partyRegType;		
	}

	private ModalWindow getCalcAQCHistoryWindow() {
		// TODO this will have to be fixed if it must be used again
		if (calcAQCHistoryWindow==null) {
			calcAQCHistoryWindow = AQCHistoryPopup.createModalWindow(
					panelModel.getCalculatedAQCHistory(),"calcAQCHistory",null);
		}
		return calcAQCHistoryWindow;
	}
	
	private ModalWindow getManualAQCHistoryWindow() {
		// TODO this will have to be fixed if it must be used again
		if (manualAQCHistoryWindow==null) {
			manualAQCHistoryWindow = AQCHistoryPopup.createModalWindow(
					panelModel.getManualAQCHistory(),"manualAQCHistory",null);
		}
		return manualAQCHistoryWindow;
	}

	

	public SubAdvisorQualityCodePanelModel getPropertyModelTarget() {
		
		return panelModel;
	}
	
	/**
	 * This anonymous inner class represents the converter to display
	 * the manual AQC value
	 */
	private IConverter manualAQCConverter = new IConverter() {
		
		public Object convertToObject(String value, Locale locale) {
			return new AQCValueDTO(value);			
		}

		public String convertToString(Object value, Locale locale) {
			if (value!=null && value instanceof AQCValueDTO) {
				
				return ((AQCValueDTO)value).getText();
			}
			return "";
		}
		
	};
	
	@Override
	protected ContextDrivenViewTemplate<AgreementGUIField, AgreementDTO> getViewTemplate() {
		return panelModel.getViewTemplate();
	}

	@Override
	protected AgreementDTO getViewTemplateContext() {
		if (viewTemplateContext == null) {
			viewTemplateContext = new AgreementDTO();
		}
		return viewTemplateContext;
	}

	public Class getPanelClass() {
		return getClass();
	}
	
	
	@Override
	public FeedbackPanel getFeedBackPanel() {
	
		if(this.feedbackPanel == null)
			this.feedbackPanel = super.getFeedBackPanel();
		return this.feedbackPanel;
	}
		
}
