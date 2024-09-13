package za.co.liberty.web.pages.maintainagreement;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
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
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.validation.IFormValidator;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.convert.IConverter;

import com.inmethod.grid.DataProviderAdapter;
import com.inmethod.grid.IGridColumn;
import com.inmethod.grid.SizeUnit;

import za.co.liberty.business.guicontrollers.agreement.IAgreementGUIController;
import za.co.liberty.dto.agreement.AgreementDTO;
import za.co.liberty.dto.agreement.ProvidentFundDetailDTO;
import za.co.liberty.dto.agreement.ProvidentFundOverrideRatesDTO;
import za.co.liberty.dto.agreement.maintainagreement.ProvidentFundBeneficiaryDetailsDTO;
import za.co.liberty.dto.agreement.maintainagreement.ProvidentFundRequestDetailDTO;
import za.co.liberty.dto.agreement.maintainagreement.ProvidentFundRequestDetailDTO.BenefitDetails;
import za.co.liberty.exceptions.data.ValidationException;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.helpers.lookup.ServiceLocator;
import za.co.liberty.interfaces.agreements.requests.RequestKindType;
import za.co.liberty.web.data.enums.ComponentType;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.data.enums.fields.AgreementGUIField;
import za.co.liberty.web.pages.interfaces.ISecurityPanel;
import za.co.liberty.web.pages.maintainagreement.model.MaintainAgreementPageModel;
import za.co.liberty.web.pages.maintainagreement.model.ProvidentFundDetailsPanelModel;
import za.co.liberty.web.pages.panels.GUIFieldPanel;
import za.co.liberty.web.pages.panels.HelperPanel;
import za.co.liberty.web.pages.panels.ViewTemplateBasePanel;
import za.co.liberty.web.wicket.markup.html.form.SRSDateField;
import za.co.liberty.web.wicket.convert.converters.DecimalConverter;
import za.co.liberty.web.wicket.markup.html.form.SRSAbstractChoiceRenderer;
import za.co.liberty.web.wicket.markup.html.form.SRSDropDownChoice;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGrid;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGrid.GridSizeUnit;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGridColumn;
import za.co.liberty.web.wicket.markup.html.grid.SRSGridRowSelectionCheckBox;
import za.co.liberty.web.wicket.markup.repeater.data.ListDataProvider;
import za.co.liberty.web.wicket.markup.repeater.data.SortableListDataProvider;
import za.co.liberty.web.wicket.view.ContextDrivenViewTemplate;


/**
 * Panel for the provident fund details and calculation details
 * @author DZS2610
 *
 */
public class ProvidentFundDetailsPanel extends ViewTemplateBasePanel<AgreementGUIField, AgreementDTO> 
								   implements ISecurityPanel {

	private static final long serialVersionUID = 1L;
	
	private ProvidentFundDetailsForm pageForm;	

	private transient IAgreementGUIController guiController;
	
	private static final Logger logger = Logger.getLogger(ProvidentFundDetailsPanel.class);	

	private ProvidentFundDetailsPanelModel panelModel;	

	private boolean initialised;	
	
	private AgreementDTO viewTemplateContext;
	
	private GUIFieldPanel approvedGroupLifeBenefit;
	private GUIFieldPanel unApprovedGroupLifeBenefit;
	private GUIFieldPanel pfOverrideReason;
	
	private ModalWindow historyWindow;
	
	private SRSDataGrid beneficiaryDetailsGrid;
	
	private boolean outstandingRequest;
	
	private MaintainAgreementPageModel pageModel;
	
	private GUIFieldPanel overrideStartDatePanel;
	private GUIFieldPanel overrideEndDatePanel;
	
	
	//private EditStateType[] editstateTypes = new EditStateType[] {EditStateType.MODIFY,EditStateType.AUTHORISE}; 
	private EditStateType[] editstateTypes = new EditStateType[]{EditStateType.MODIFY,EditStateType.ADD};
	private EditStateType[] dateEditstateTypes = new EditStateType[] {EditStateType.MODIFY }; 

	
	private Collection<FormComponent> validationComponents = new ArrayList<FormComponent>();

	private List<ProvidentFundBeneficiaryDetailsDTO> beneficiaryDetails;
	
	public ProvidentFundDetailsPanel(String id, EditStateType editState,
			MaintainAgreementPageModel pageModel) {
		this(id,editState,(ProvidentFundDetailsPanelModel)null);
		setPageModel(pageModel);
	}
	
	public ProvidentFundDetailsPanel(String id, EditStateType editState,
			ProvidentFundDetailsPanelModel panelModel) {
		this(id,editState,panelModel,null);
	}

	public ProvidentFundDetailsPanel(String id, EditStateType editState,
			ProvidentFundDetailsPanelModel panelModel,Page parentPage) {
		super(id, editState,parentPage);
		this.panelModel = panelModel;
	}
	
	public void setPageModel(MaintainAgreementPageModel pageModel) {
		this.panelModel = new ProvidentFundDetailsPanelModel(pageModel);
	}
	
	@Override
	protected void onBeforeRender() {
		super.onBeforeRender();
		if (!initialised) {
			//check for outstadning requests
			List<RequestKindType> outstandingRequestKinds =  this.getOutStandingRequestKinds();
			for(RequestKindType requestKind : outstandingRequestKinds){
				if(requestKind == RequestKindType.MaintainProvidentFundOptions){
					outstandingRequest = true;
				}
			}			
			initPanelModel();
			add(getPageForm());
			add(historyWindow = createHistoryWindow("historyWindow"));	
			initialised = true;
		}
	}
	
	/**
	 * initialise any additional data required by the panel model
	 *
	 */
	private void initPanelModel(){
		if(panelModel == null){
			panelModel = new ProvidentFundDetailsPanelModel();
		}
		if(panelModel.getAgreementId() > 0){
			//try get the latest request
			ProvidentFundRequestDetailDTO requestDetail = getGuiController().getLatestProvidentFundRequestDetail(panelModel.getAgreementId());
			panelModel.setLatestRequestDetail(requestDetail);
			panelModel.setProvidentFundOverrideRatesList(getGuiController().getAllOverrideRates());
		}		
	}
	
	
	/**
	 * Load the AgreementGUIController dynamically if it is null as this is a transient variable.
	 * @return {@link IAgreementGUIController}
	 */
	private IAgreementGUIController getGuiController() {
		if (guiController==null) {
			try {
				guiController = ServiceLocator.lookupService(IAgreementGUIController.class);
			} catch (NamingException e) {
				logger.fatal("Could not lookup AgreementGUIController",e);
				throw new CommunicationException("Could not lookup AgreementGUIController",e);
			}
		}
		return guiController;
	}
	
	/**
	 * Get the view template for the selected context
	 */
	protected ContextDrivenViewTemplate<AgreementGUIField, AgreementDTO> getViewTemplate() {		
		return panelModel.getViewTemplate();
	}
	
	@SuppressWarnings("unchecked")
	private Component getPageForm() {
		if (pageForm == null) {
			pageForm = new ProvidentFundDetailsForm("searchForm");
			pageForm.add(new IFormValidator() {
				private static final long serialVersionUID = 1L;
				
				private BigDecimal overrideRate;
				private String overrideReason;

				
				public void validate(Form arg0) {
					if (getEditState() == EditStateType.VIEW) {
						return;
					}					
					boolean validate = true;
//					for(FormComponent comp : validationComponents){
//						if(!comp.isValid()){
//							validate = false;
//						} 
//					}
					if(validate){
						try{				
							//validate provident fund details
							getGuiController().validateProvidentFundDetails(panelModel.getProvidentFundDetailDTO(),panelModel.getAgreementId(),panelModel.getCurrentProvidentFundNumber());		
							//getGuiController().validateProvidentBeneficiaries(panelModel.getProvidentFundBeneficiariesDTO());
						}catch(ValidationException ex){
							for(String error : ex.getErrorMessages()){
								ProvidentFundDetailsPanel.this.error(error);								
							}
						}
					}
					
//					if((getPreviousProvidentFundDetailDTO().getOverrideRate() != overrideRate)
//							&& (overrideReason == null || overrideReason.trim().length() == 0)) {
////						ProvidentFundDetailsForm.this
////						.error("Override reason is Mandatory since override rate has been modified");
//						
//					}
					
				}

				public FormComponent[] getDependentFormComponents() {
					return null;
				}				
			});
		}
		return pageForm;
	}
	
	/**
	 * create the check box fields from the gui field definition
	 * @return
	 */
	private HelperPanel createCheckBoxGuiField(AgreementGUIField field, boolean reloadComponent) {
        HelperPanel panel;
        /*if(outstandingRequest){
               panel = createGUIPageField(field, 
                            getContext(),
                            ComponentType.LABEL, true);      
        }else{
               panel = createGUIPageField(field, 
                                   getContext(),
                                   ComponentType.CHECKBOX, true);   
               panel.setOutputMarkupId(true);
               panel.getEnclosedObject().setOutputMarkupId(true);
        }*/
        
        CheckBox checkBox = new CheckBox("value", new PropertyModel(getContext(),field.getFieldId()));
        if (reloadComponent) {
               checkBox.add(new AjaxFormComponentUpdatingBehavior("click") {

                      @Override
                      protected void onUpdate(AjaxRequestTarget target) {
                             target.add(this.getComponent().getParent());

                      }
               });
        }
        checkBox.setOutputMarkupId(true);
        checkBox.setOutputMarkupPlaceholderTag(true);
        checkBox.setEnabled(true);
        boolean view = isView(getViewTemplate().getEditStates(field,getViewTemplateContext()));
        if((view || outstandingRequest)){
               checkBox.setEnabled(false);
        }
        panel = HelperPanel.getInstance(field.getFieldId(),checkBox);
        return panel;       
  }      

	
	private HelperPanel createTextFieldGuiField(AgreementGUIField field) {
		HelperPanel panel;
		if(outstandingRequest){
			panel = createGUIPageField(field, 
					getContext(),
					ComponentType.LABEL, true);	
		}else{
			panel = createGUIPageField(field, 
					getContext(),
					ComponentType.TEXTFIELD, true);
			panel.setOutputMarkupId(true);
			panel.getEnclosedObject().setOutputMarkupId(true);	
		}		
		return panel;		
	}
	
	@SuppressWarnings({ "serial", "unchecked" })
	private class ProvidentFundDetailsForm extends Form {
		

		private ProvidentFundDetailsForm(String id) {
			super(id);
			RepeatingView leftPanel = new RepeatingView("leftPanel");
			RepeatingView rightPanel = new RepeatingView("rightPanel");
			RepeatingView middleRowPanel = new RepeatingView("middleRowPanel");
			WebMarkupContainer container = new WebMarkupContainer("requestDetail");
			if(!getEditState().isViewOnly()){
				container.setOutputMarkupId(true);
				container.setVisible(false);
			}
			
			RepeatingView leftCalcPanel = new RepeatingView("leftCalcPanel");
			RepeatingView rightCalcPanel = new RepeatingView("rightCalcPanel");
			add(leftPanel);
			add(rightPanel);
			add(middleRowPanel);
			add(createHistoryButton("historyButton"));
			add(container);
			
			container.add(leftCalcPanel);
			container.add(rightCalcPanel);
			container.add(getDataGrid("calcGrid"));
			add(getBeneficiaryDetailsGrid("providentFundBeneficiaryGrid"));
			add(createAddBeneficiaryButton("addBeneficiaryButton"));
			add(createRemoveBeneficiaryButton("removeBeneficiaryButton"));
			
			
//			ProvidentFundDetailDTO providentFundDTO = panelModel.getProvidentFundDetailDTO();
//			ProvidentFundOverrideRatesDTO providentFundOverrideRatesDTO = panelModel.getProvidentFundOverrideRatesDTO();
			//Provident Fund override rates
		
			
			/**
			 * Left panel
			 */
			leftPanel.add(createGUIFieldPanel(AgreementGUIField.ISPROVIDENTFUNDMEMBER, 
					createCheckBoxGuiField(AgreementGUIField.ISPROVIDENTFUNDMEMBER, true)));	
			
			GUIFieldPanel groupLifePanel = createGUIFieldPanel(AgreementGUIField.HASGROUPLIFEBENEFIT, 
					createCheckBoxGuiField(AgreementGUIField.HASGROUPLIFEBENEFIT, false));
			Component groupLifeComponent = ((HelperPanel) groupLifePanel.getComponent()).getEnclosedObject();
			if(groupLifeComponent instanceof CheckBox){
				((CheckBox)groupLifeComponent).add(new AjaxFormComponentUpdatingBehavior("click"){
					@Override
					protected void onUpdate(AjaxRequestTarget target) {
						//update the other 2 fields linked to this field					
						target.add(approvedGroupLifeBenefit);
						target.add(unApprovedGroupLifeBenefit);
					}					
				});
			}	
			
		
			
			
			leftPanel.add(groupLifePanel);
			//this checkbox 
			approvedGroupLifeBenefit = createGUIFieldPanel(AgreementGUIField.APPROVEDGROUPLIFEBENEFIT, 
					createCheckBoxGuiField(AgreementGUIField.APPROVEDGROUPLIFEBENEFIT, false));
			approvedGroupLifeBenefit.setEnabled(false);
			leftPanel.add(approvedGroupLifeBenefit);
			
			leftPanel.add(createGUIFieldPanel(AgreementGUIField.HASIPPDISABILITYBENEFIT, 					
					createCheckBoxGuiField(AgreementGUIField.HASIPPDISABILITYBENEFIT, true)));	
			leftPanel.add(createGUIFieldPanel(AgreementGUIField.HASIPPPLUSDISABILITYBENEFIT, 
					createCheckBoxGuiField(AgreementGUIField.HASIPPPLUSDISABILITYBENEFIT, true)));
			
			leftPanel.add(createGUIFieldPanel(AgreementGUIField.ANNUALPENSIONABLEEARNINGS, 
					createTextFieldGuiField(AgreementGUIField.ANNUALPENSIONABLEEARNINGS)));			
			
			/**
			 * Right panel
			 */
			rightPanel.add(createGUIFieldPanel(AgreementGUIField.HASDREADDISEASEBENEFIT, 
					createCheckBoxGuiField(AgreementGUIField.HASDREADDISEASEBENEFIT, true)));	
			rightPanel.add(createGUIFieldPanel(AgreementGUIField.HASSPOUSEDEATHBENEFIT, 
					createCheckBoxGuiField(AgreementGUIField.HASSPOUSEDEATHBENEFIT, true)));
			
			unApprovedGroupLifeBenefit = createGUIFieldPanel(AgreementGUIField.UNAPPROVEDGROUPLIFEBENEFIT, 
					createCheckBoxGuiField(AgreementGUIField.UNAPPROVEDGROUPLIFEBENEFIT, false));
			unApprovedGroupLifeBenefit.setEnabled(false);
			rightPanel.add(unApprovedGroupLifeBenefit);		
			
			
			rightPanel.add(createGUIFieldPanel(AgreementGUIField.HASFAMILYBENEFIT, 
					createCheckBoxGuiField(AgreementGUIField.HASFAMILYBENEFIT, true)));	
			rightPanel.add(createGUIFieldPanel(AgreementGUIField.ISONEPERCENTBONDMEMBER, 
					createCheckBoxGuiField(AgreementGUIField.ISONEPERCENTBONDMEMBER, true)));	
			
			boolean isEmptyRatesList = true;
			if(panelModel.getProvidentFundOverrideRatesList() == null) {
				panelModel.setProvidentFundOverrideRatesList(getGuiController().getAllOverrideRates());
				isEmptyRatesList = false;
			}
	       //AjaxFormComponentUpdatingBehavior panelModel.getProvidentFundDetailDTO().getOverrideRate()
			//Santosh:To dispaly Override rate when maintain screen render,This need to be removed once proper solution in place
//			String selectdRate = panelModel.getProvidentFundDetailDTO().getOverrideRate()!=null && isEmptyRatesList ?panelModel.getProvidentFundDetailDTO().getOverrideRate().setScale(3, BigDecimal.ROUND_HALF_UP).toString():null;
			
			GUIFieldPanel overrideRatePanel = createGUIFieldPanel(AgreementGUIField.PFOVERRIDERATE, 
					createDropdownField(panelModel.getProvidentFundDetailDTO(), "Provident Fund Override Rates", "panel", "overrideRate",
							panelModel.getProvidentFundOverrideRatesList(),
							new SRSAbstractChoiceRenderer<BigDecimal>() {

								@Override
								public Object getDisplayValue(BigDecimal id) {
									if (id == null) {
										return "Not Selected";
									}
									return id;
								}

								@Override
								public String getIdValue(BigDecimal object, int index) {
									if (object == null) {
										return null;
									}
													
									for (int i = 0; i< panelModel.getProvidentFundOverrideRatesList().size();++i) {
										BigDecimal b = panelModel.getProvidentFundOverrideRatesList().get(i);
										if (b.compareTo(object)==0) {
											return "" + i;
										}
									}

									return "" + index;
								}
					
							}
							, "Select one",
							false, true, editstateTypes));

			Component overrideRateComponent = ((HelperPanel) overrideRatePanel.getComponent()).getEnclosedObject();
			if(overrideRateComponent instanceof DropDownChoice<?>) {
				((DropDownChoice<?>)overrideRateComponent).setNullValid(true); // Allow nulls to be set after setting full value
				((DropDownChoice<?>)overrideRateComponent).add(new AjaxFormComponentUpdatingBehavior("change") {
					@Override
					protected void onUpdate(AjaxRequestTarget target) {
						//hide the override reason when dropdown has nothing selected else show it 
						if(panelModel.getProvidentFundOverrideRatesList() != null ) {
							getPFOverrideReason().setVisible(false);
						}
						
						target.add(getPFOverrideReason());
						updateOveriderDatesOnChangeOfRates();
						target.add(overrideStartDatePanel);
						target.add(overrideEndDatePanel);

					}
					
				});
				overrideRatePanel.setOutputMarkupId(true);
			}
			
					
			rightPanel.add(overrideRatePanel);
				
			//Override reason
			rightPanel.add(getPFOverrideReason());
			
			rightPanel.add(getOverrideStartDatePanel());			
			  
			rightPanel.add(getOverrideEndDatePanel());
			//add request detail
			leftCalcPanel.add(createGUIFieldPanel("Request Date", "Request Date", "requestedDate", HelperPanel.getInstance("panel", new Label("value",new PropertyModel(
					panelModel.getLatestRequestDetail(), "requestedDate")))));		
			leftCalcPanel.add(createGUIFieldPanel("Risk AQC Score", "Risk AQC Score", "aqcScore", HelperPanel.getInstance("panel", new Label("value",new PropertyModel(
					panelModel.getLatestRequestDetail(), "aqcScore")))));	
			leftCalcPanel.add(createGUIFieldPanel("Risk AQC Group", "Risk AQC Group", "aqcGroup", HelperPanel.getInstance("panel", new Label("value",new PropertyModel(
					panelModel.getLatestRequestDetail(), "aqcGroup")))));
			leftCalcPanel.add(createGUIFieldPanel("Production Club Status", "Production Club Status", "productionClubStatus", HelperPanel.getInstance("panel", new Label("value",new PropertyModel(
					panelModel.getLatestRequestDetail(), "productionClubStatus")))));
			leftCalcPanel.add(createGUIFieldPanel("Contribution Rate", "Contribution Rate", "calculationRate", HelperPanel.getInstance("panel", new Label("value",new PropertyModel(
					panelModel.getLatestRequestDetail(), "calculationRate")))));
//			leftCalcPanel.add(createGUIFieldPanel("Override Rate", "Override Rate", "overrideRate", HelperPanel.getInstance("panel", new Label("value",new PropertyModel(
//					panelModel.getLatestRequestDetail(), "overrideRate")))));


			rightCalcPanel.add(createGUIFieldPanel("Calculation Period Start Date", "Calculation Period Start Date", "calcStart", HelperPanel.getInstance("panel", new Label("value",new PropertyModel(
					panelModel.getLatestRequestDetail(), "calcStart")))));
			rightCalcPanel.add(createGUIFieldPanel("Calculation Period End Date", "Calculation Period End Date", "calcEnd", HelperPanel.getInstance("panel", new Label("value",new PropertyModel(
					panelModel.getLatestRequestDetail(), "calcEnd")))));
			rightCalcPanel.add(createGUIFieldPanel("Annual Pensionable Earnings", "Annual Pensionable Earnings", "annualPensionableEarnings", HelperPanel.getInstance("panel", new Label("value",new PropertyModel(
					panelModel.getLatestRequestDetail(), "annualPensionableEarnings")))));
			rightCalcPanel.add(createGUIFieldPanel("Monthly Pensionable Earnings", "Monthly Pensionable Earnings", "monthlyPensionableEarnings", HelperPanel.getInstance("panel", new Label("value",new PropertyModel(
					panelModel.getLatestRequestDetail(), "monthlyPensionableEarnings")))));
			rightCalcPanel.add(createGUIFieldPanel("Contribution Amount", "Contribution Amount", "contributionAmount", HelperPanel.getInstance("panel", new Label("value",new PropertyModel(
					panelModel.getLatestRequestDetail(), "contributionAmount")))));
			rightCalcPanel.add(createGUIFieldPanel("One Percent Bond Member Earnings", "One Percent Bond Member Earnings", "onePercentBondMemberEarnings", HelperPanel.getInstance("panel", new Label("value",new PropertyModel(
					panelModel.getLatestRequestDetail(), "onePercentBondMemberEarnings")))));
			
			/**
			 * Update visibility
			 */
			checkFieldVisibility();

		}	

	}
	
	private GUIFieldPanel getOverrideStartDatePanel() {
		//Override start date;
		overrideStartDatePanel = createGUIFieldPanel(AgreementGUIField.PFOVERRIDESTARTDATE, 
					createPageField(panelModel.getProvidentFundDetailDTO(), "Override Start Date", "panel",
			  "overrideStartDate", ComponentType.DATE_SELECTION_TEXTFIELD, false, true,
			  dateEditstateTypes));
		
		overrideStartDatePanel.setEnabled(false);
		overrideStartDatePanel.setVisible(isVisible(AgreementGUIField.PFOVERRIDESTARTDATE));
		overrideStartDatePanel.setOutputMarkupId(true);
		
		return overrideStartDatePanel;
	}
	
	private GUIFieldPanel getOverrideEndDatePanel() {

		//Override End date
		 overrideEndDatePanel = createGUIFieldPanel(AgreementGUIField.PFOVERRIDEENDDATE, 
				createPageField(panelModel.getProvidentFundDetailDTO(), "Override End Date", "panel",
						"overrideEndDate", ComponentType.DATE_SELECTION_TEXTFIELD, false, true,
						dateEditstateTypes));
		 
		overrideEndDatePanel.setEnabled(false);
		overrideEndDatePanel.setVisible(isVisible(AgreementGUIField.PFOVERRIDEENDDATE));
		overrideEndDatePanel.setOutputMarkupId(true);
		
		return overrideEndDatePanel;
	}
	
	
	private void updateOveriderDatesOnChangeOfRates() {
		
		Date startDt = Calendar.getInstance().getTime();
		Date endDt = Date.from(LocalDate.now().plusMonths(3).
				atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
		if(panelModel.getProvidentFundDetailDTO().getOverrideRate() != null) {
			panelModel.getProvidentFundDetailDTO().setOverrideStartDate(startDt);
			panelModel.getProvidentFundDetailDTO().setOverrideEndDate(endDt);
		}else {
			panelModel.getProvidentFundDetailDTO().setOverrideStartDate(null);
			panelModel.getProvidentFundDetailDTO().setOverrideEndDate(null);
		}
			
		
	}
	
	
	
	 /** Get the properties to to show the override rate, date and comment 
	  * using this on this panel instead of using the Base panel as I want to override the Bigdecimal value that is passed 
	  * Base panel is called by different Panels so having the converter override in it will cause issues  
	 *@return
	 */
	
	protected HelperPanel createDropdownField(final Object propertyObject,
			String labelId, String componentID, final String attribute,
			final List dataList, final IChoiceRenderer renderer,
			String defaultChoice, boolean isRequired, boolean updateWithAjax,
			EditStateType... editableStates) {
		
		return createDropdownFieldOverrideRate(labelId, componentID, new PropertyModel(
				propertyObject, attribute), dataList, renderer, defaultChoice,
				isRequired, updateWithAjax, editableStates);
	}
	
	
	protected HelperPanel createDropdownFieldOverrideRate(String labelId,
			String componentID, IModel propertyModel, final List dataList,
			final IChoiceRenderer renderer, String defaultChoice,
			boolean isRequired, boolean updateWithAjax,
			EditStateType... editableStates) {
		if (isView(editableStates)) {
			Label label = new Label("value", propertyModel);

			return HelperPanel.getInstance(componentID, label, false);
		}

		final SRSDropDownChoice dropDownChoice = new SRSDropDownChoice("value",
				propertyModel, dataList, renderer, defaultChoice) {

					@Override
					public IConverter getConverter(Class  type) {
						return new DecimalConverter(3);
					}
		};
		
		dropDownChoice.setLabel(new Model<String>(labelId));
		if (isRequired) {
			dropDownChoice.setRequired(true);
		}
		if (updateWithAjax) {
			dropDownChoice
					.add(new AjaxFormComponentUpdatingBehavior("change") {
						private static final long serialVersionUID = 1L;

						@Override
						protected void onUpdate(AjaxRequestTarget target) {
							// do nothing, update value through ajax
							
						}

						@Override
						protected void onError(AjaxRequestTarget target,
								RuntimeException arg1) {
							// print feedback on feedback panel
						}
					});
			dropDownChoice.setOutputMarkupId(true);
		}
		return HelperPanel.getInstance(componentID, dropDownChoice);

	}
	
	
	
	 
	private GUIFieldPanel getPFOverrideReason() {
		
		IModel model  = new IModel<Object>() {
			private static final long serialVersionUID = 1L;
			
			public Object getObject() {
				return (String) panelModel.getProvidentFundDetailDTO()
						.getPfOverrideReason();
			}
			
			public void setObject(Object arg0) {
				panelModel.getProvidentFundDetailDTO()
				.setPfOverrideReason((String) arg0);
			}
			
			public void detach() {
				
			}
		};
		
		TextArea<String> textArea = new TextArea<>("value", model);
		textArea.add(new OnChangeAjaxBehavior() {
			
			private static final long serialVersionUID = 1L;
			
			@Override
			protected void onUpdate(AjaxRequestTarget arg0) {
				// TODO Auto-generated method stub// Access the updated model object:
				String valueAsString = ((TextArea<String>) getComponent())
						.getModelObject();
				
				panelModel.getProvidentFundDetailDTO().setPfOverrideReason(valueAsString);
				
				
				
			}
		});
		textArea.setOutputMarkupId(true);
		textArea.setOutputMarkupPlaceholderTag(true);
		
		if(getEditState().isViewOnly() && getEditState() == EditStateType.MODIFY) {
			textArea.setVisible(false);
			textArea.setEnabled(false);
		}
		
		
		pfOverrideReason = createGUIFieldPanel("Override Reason", "Override Reason",
				"pfOverrideReason",
				HelperPanel.getInstance("panel", textArea));
		
		pfOverrideReason.setOutputMarkupId(true);
		return pfOverrideReason;
		
	}
	
	/** 
	 * Get the grid for display on the user role window
	 * @return
	 */
	private SRSDataGrid getDataGrid(String id){	
		List<BenefitDetails> gridData = (panelModel != null && panelModel.getLatestRequestDetail() != null) ? panelModel.getLatestRequestDetail().getBenefitDetails() : null;
		if(gridData == null){
			gridData = new ArrayList<BenefitDetails>(0);
		}
		
		SRSDataGrid grid = new SRSDataGrid(id, new DataProviderAdapter(new SortableListDataProvider<BenefitDetails>(gridData)), getViewableRolesColumns(),null);            
        grid.setCleanSelectionOnPageChange(false);
        grid.setClickRowToSelect(false);        
        //grid.setContentHeight(100, SizeUnit.PX);
        grid.setAllowSelectMultiple(true);
        grid.setGridWidth(750, GridSizeUnit.PIXELS);               
        grid.setRowsPerPage(10);
        grid.setContentHeight(170, SizeUnit.PX);
        return grid;
	}
	
	/**
	 * Get columns for grid based on type selected
	 * @return
	 */
	private List<IGridColumn> getViewableRolesColumns() {
		Vector<IGridColumn> cols = new Vector<IGridColumn>();		
			cols = new Vector<IGridColumn>();
			cols.add(new SRSDataGridColumn<BenefitDetails>("benefitName", new Model("Risk Benefit Type"), "benefitName", "benefitName", EditStateType.VIEW).setInitialSize(200));			
			cols.add(new SRSDataGridColumn<BenefitDetails>("benefitAmount",new Model("Benefit Amount"),"benefitAmount","benefitAmount", EditStateType.VIEW).setInitialSize(110)); 
			cols.add(new SRSDataGridColumn<BenefitDetails>("benefitRate",new Model("Benefit Rate"),"benefitRate","benefitRate", EditStateType.VIEW).setInitialSize(110)); 
			cols.add(new SRSDataGridColumn<BenefitDetails>("benefitBase",new Model("Benefit Base"),"benefitBase","benefitBase", EditStateType.VIEW).setInitialSize(110));
			cols.add(new SRSDataGridColumn<BenefitDetails>("benefitPremium",new Model("Benefit Premium"),"benefitPremium","benefitPremium", EditStateType.VIEW).setInitialSize(110));
			return cols;				
	}	
	
	/**
	 * Create the button to show the home history for this node
	 * 
	 * @return
	 */
	private Button createHistoryButton(String id) {
		final Button button = new Button(id);
		button.add(new AjaxFormComponentUpdatingBehavior("click") {
			private static final long serialVersionUID = 0L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {				
				historyWindow.show(target);
			}
		});
		if (!getEditState().isViewOnly()|| getEditState() == EditStateType.AUTHORISE) {
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
	private ModalWindow createHistoryWindow(String id) {
		final ModalWindow window = new ModalWindow(id);
		window.setTitle("History");		
		// Create the page
		window.setPageCreator(new ModalWindow.PageCreator() {
			private static final long serialVersionUID = 1L;
			public Page createPage() {	
				return new ProvidentfundHistoryPage(window,panelModel.getAgreementId(),panelModel.getPropertyHistory());
			}			
		});			
		// Initialise window settings
		window.setMinimalHeight(300);
		window.setInitialHeight(300);
		window.setMinimalWidth(600);
		window.setInitialWidth(600);
		window.setMaskType(MaskType.SEMI_TRANSPARENT);
		window.setCssClassName(ModalWindow.CSS_CLASS_GRAY);	
		window.setOutputMarkupId(true);
		window.setOutputMarkupPlaceholderTag(true);
//		window.setPageMapName("ProvidentFundHistoryPageMap");
		return window;
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

			// cloneOriginalList(beneficiaryDetails);
			beneficiaryDetailsGrid = new SRSDataGrid(id, new DataProviderAdapter(new ListDataProvider<ProvidentFundBeneficiaryDetailsDTO>(beneficiaryDetails)), getColumns(), getEditState());
			beneficiaryDetailsGrid.setOutputMarkupId(true);
			beneficiaryDetailsGrid.setCleanSelectionOnPageChange(false);
			beneficiaryDetailsGrid.setClickRowToSelect(false);
			beneficiaryDetailsGrid.setAllowSelectMultiple(false);
			//beneficiaryDetailsGrid.setGridWidth(100, GridSizeUnit.PERCENTAGE);
			beneficiaryDetailsGrid.setRowsPerPage(10);
			beneficiaryDetailsGrid.setContentHeight(100, SizeUnit.PX);
		}
		return beneficiaryDetailsGrid;
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
						//validationComponents.add((TextField)panel.getEnclosedObject());
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
						//validationComponents.add((TextField)panel.getEnclosedObject());
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
			@Override
			public Panel newCellPanel(WebMarkupContainer parent, String componentId, IModel rowModel, String objectProperty, EditStateType state, ProvidentFundBeneficiaryDetailsDTO data) {
				if(state == EditStateType.VIEW || data.getOid() != 0){
					return super.newCellPanel(parent, componentId, rowModel, objectProperty, state, data);
				}else{						
					HelperPanel panel =  createPageField(data, "Date Of Birth", componentId, ComponentType.DATE_SELECTION_TEXTFIELD, true,true, new EditStateType[]{EditStateType.ADD,EditStateType.MODIFY});
					if(panel.getEnclosedObject() instanceof SRSDateField){
						SRSDateField startdte = (SRSDateField)panel.getEnclosedObject();
//						startdte.add(new AttributeModifier("readonly","true"));
						startdte.addNewDatePicker();
						//validationComponents.add(startdte);
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
						//validationComponents.add((TextField)panel.getEnclosedObject());
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
						//validationComponents.add((TextField)panel.getEnclosedObject());
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
					target.add(beneficiaryDetailsGrid);
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
				target.add(beneficiaryDetailsGrid);
			}
		});

		button.setOutputMarkupId(true);
		button.setOutputMarkupPlaceholderTag(true);
		return button;
	}

	
	private AgreementDTO getContext() {
		return getViewTemplateContext();
	}
	
	private AgreementDTO getContextProv() {
		return pageModel.getMaintainAgreementDTO().getAgreementDTO();
	}

	@SuppressWarnings("unchecked")
	public Class getPanelClass() {
		return ProvidentFundDetailsPanel.class;
	}
	
	@Override
	protected AgreementDTO getViewTemplateContext() {
		if (viewTemplateContext == null) {
			viewTemplateContext = new AgreementDTO();
			viewTemplateContext.setId(panelModel.getAgreementId());
			viewTemplateContext.setProvidentFundDetail(panelModel.getProvidentFundDetailDTO());			
		}
		return viewTemplateContext;
	}
	
	//Pzm2509 get current properties if they were updated 
	private ProvidentFundDetailDTO getPreviousProvidentFundDetailDTO() {
		ProvidentFundDetailDTO previousProvidentFundDetailDTO = new ProvidentFundDetailDTO(); 
	    if (pageModel == null || pageModel.getPreviousMaintainAgreementDTO() == null
	    		|| pageModel.getPreviousMaintainAgreementDTO().getAgreementDTO() == null)
	    	
	    	return previousProvidentFundDetailDTO;
	    
	    previousProvidentFundDetailDTO = pageModel.getPreviousMaintainAgreementDTO()
	    		.getAgreementDTO().getProvidentFundDetail();
	    
	    return previousProvidentFundDetailDTO;
		
	}
}