package za.co.liberty.web.pages.maintainagreement;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;

import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.convert.IConverter;

import za.co.liberty.business.guicontrollers.agreement.IAgreementGUIController;
import za.co.liberty.dto.agreement.AgreementDTO;
import za.co.liberty.dto.agreement.properties.DistributionDetailDTO;
import za.co.liberty.dto.agreement.properties.DistributionTemplateDTO;
import za.co.liberty.dto.agreement.properties.DistributionTemplateHistoryDTO;
import za.co.liberty.dto.gui.templates.DistributionKindGroupRatesDTO;
import za.co.liberty.helpers.lookup.ServiceLocator;
import za.co.liberty.interfaces.rating.FranchiseTemplateKindEnum;
import za.co.liberty.persistence.rating.entity.Description;
import za.co.liberty.web.data.enums.ComponentType;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.data.enums.fields.AgreementGUIField;
import za.co.liberty.web.pages.maintainagreement.model.DistributionDetailPageModel;
import za.co.liberty.web.pages.maintainagreement.model.DistributionPanelModel;
import za.co.liberty.web.pages.panels.GUIFieldPanel;
import za.co.liberty.web.pages.panels.HelperPanel;
import za.co.liberty.web.pages.panels.ViewTemplateBasePanel;
import za.co.liberty.web.wicket.markup.html.form.SRSDropDownChoice;
import za.co.liberty.web.wicket.validation.maintainagreement.TemplateDateValidator;
import za.co.liberty.web.wicket.view.ContextDrivenViewTemplate;

public class DistributionPanel extends ViewTemplateBasePanel<AgreementGUIField, AgreementDTO> {
	
	private DistributionPanelModel panelModel;
	
	private AgreementDTO viewTemplateContext;
	
	private boolean initialised;

	private transient Logger logger;
	
	private transient IAgreementGUIController guiController;

	private DistributionForm distributionForm;

	private ModalWindow distributionDetailsWindow;
	
	private ModalWindow templateHistoryWindow;
	
	private DistributionDetailPageModel distributionDetailsModel;

	private Button distributionDetailsButton;
	
	private Button templateHistoryButton;

	private WebMarkupContainer distributionContainer;
	
	private RepeatingView distributionPanel;
	
	private HelperPanel distributionTemplatePanel;
	
	private HelperPanel distributionTemplateEffectiveFromPanel;
	
	private boolean distributionDateChangeAllowed;

	private GUIFieldPanel distributionTemplateEffectiveFromGUIPanel;
	
	private List<DistributionKindGroupRatesDTO> distributionKindGroupDTOs;

	private int agmtKind;
	private int agmKind;

	public DistributionPanel(String id, EditStateType editState,DistributionPanelModel panelModel, int agmKind) {
		super(id, editState);
		this.panelModel = panelModel;
		this.agmKind = agmKind;
		distributionDetailsModel = new DistributionDetailPageModel();
		
		//QA MZL2611 Franchise Code Begin 18-01-2012
		//Franchise Template assignment code change of Model and valid list model.
		agmtKind = panelModel.getDistributionTemplateContainer().getKind();
		
		if (agmtKind == 0){
			agmtKind = agmKind;
		}
		
		FranchiseTemplateKindEnum franchiseTemplateKindEnum = FranchiseTemplateKindEnum.getFranchiseTemplateKindEnum(agmtKind);
	
		if(franchiseTemplateKindEnum != null){
			
			distributionDetailsModel.setFranchiseTemplateKindEnum(franchiseTemplateKindEnum);
		//Change the model to cater for the templates stored at Distribution Kind Group Level
			if(agmtKind == FranchiseTemplateKindEnum.FRANCHISE.getAgreementKind() ||
					agmtKind == FranchiseTemplateKindEnum.FRANCHISE_MANAGER.getAgreementKind())	{
				
				List<Description> validDistributionKindGroupRates = getGuiController().getValidDistributionKindGroupRatesByDKGKind(franchiseTemplateKindEnum.getDistributionKindGroup());
		
				// for add new agreement put in this check as there is no distribution details.
				if(panelModel.getDistributionTemplateContainer().getDistributionDetails() != null){
					distributionKindGroupDTOs = getGuiController().findDistributionKindGroupDTOTemplateID(panelModel.getDistributionTemplateContainer().getDistributionDetails().getId());
				}
				
				if(getEditState() == EditStateType.VIEW) {
					//This condition is for a template that exists on the new Distribution Kind Group Rates Table
					if(distributionKindGroupDTOs != null && distributionKindGroupDTOs.size() > 0 ) {
						distributionDetailsModel.setDistributionKindGroupRatesDTOs(distributionKindGroupDTOs);
						//convert the  DistributionKindGroupRatesDTO to DistributionTemplateDTO
						setUpFranchiseValidTemplates(panelModel, validDistributionKindGroupRates);
					}
				}else if ( getEditState() == EditStateType.MODIFY || getEditState() ==  EditStateType.AUTHORISE){
					if(validDistributionKindGroupRates != null && validDistributionKindGroupRates.size() > 0 ) {
						if(distributionKindGroupDTOs != null && distributionKindGroupDTOs.size() > 0 ) {
							distributionDetailsModel.setDistributionKindGroupRatesDTOs(distributionKindGroupDTOs);
						}
						//convert the  DistributionKindGroupRatesDTO to DistributionTemplateDTO
						setUpFranchiseValidTemplates(panelModel, validDistributionKindGroupRates);
					}
				}else if (getEditState() == EditStateType.ADD){
					if(validDistributionKindGroupRates != null && validDistributionKindGroupRates.size() > 0 ) {
						distributionKindGroupDTOs = getGuiController().findDistributionKindGroupDTOTemplateID(validDistributionKindGroupRates.get(0).getUniqId());
						if(distributionKindGroupDTOs != null && distributionKindGroupDTOs.size() > 0 ) {
							distributionDetailsModel.setDistributionKindGroupRatesDTOs(distributionKindGroupDTOs);
						}
						//convert the  DistributionKindGroupRatesDTO to DistributionTemplateDTO
						setUpFranchiseValidTemplates(panelModel, validDistributionKindGroupRates);
					}
				}
			}
		}
		//QA MZL2611 Franchise Code END 18-01-2012 
	}

	private void setUpFranchiseValidTemplates(DistributionPanelModel panelModel, List<Description> validDistributionKindGroupRates) {
		List<DistributionTemplateDTO> distributionTemplateDTOs = new ArrayList<DistributionTemplateDTO>();
		
		for (Description description : validDistributionKindGroupRates) {
			DistributionTemplateDTO distributionTemplateDTO = new DistributionTemplateDTO();
			distributionTemplateDTO.setDateChangeAllowed(false);
			distributionTemplateDTO.setDescription(description.getDescription());
			Date vEffectiveFrom = description.getStartDate() != null ? new Date(description.getStartDate().getTime()) : new Date();
			distributionTemplateDTO.setEffectiveFrom(vEffectiveFrom);
			distributionTemplateDTO.setId(description.getUniqId());
			distributionTemplateDTOs.add(distributionTemplateDTO);

		}
		//QA MZL2611 fix to add the existing template into the list box. 10-04-2012
		 List<DistributionTemplateDTO> existingValidDistributionTemplates = panelModel.getValidDistributionTemplates();
			 if (getEditState() == EditStateType.MODIFY){			 
				 if(panelModel.getDistributionTemplateContainer() != null && panelModel.getDistributionTemplateContainer().getDistributionDetails() != null ){
					for (DistributionTemplateDTO description : existingValidDistributionTemplates) {
						if(panelModel.getDistributionTemplateContainer().getDistributionDetails().getId().longValue() == description.getId().longValue()){
							DistributionTemplateDTO distributionTemplateDTO = new DistributionTemplateDTO();
							distributionTemplateDTO.setDateChangeAllowed(false);
							distributionTemplateDTO.setDescription(panelModel.getDistributionTemplateContainer().getDistributionDetails().getDescription());
							Date vEffectiveFrom = description.getEffectiveFrom() != null ? new Date(description.getEffectiveFrom().getTime()) : new Date();
							distributionTemplateDTO.setEffectiveFrom(vEffectiveFrom);
							distributionTemplateDTO.setId(panelModel.getDistributionTemplateContainer().getDistributionDetails().getId());
							distributionTemplateDTOs.add(distributionTemplateDTO);
							break;
						}
					}
				 }
			 }
		 

		existingValidDistributionTemplates.clear();
		existingValidDistributionTemplates.addAll(distributionTemplateDTOs);
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
			}
		}
		return guiController;
	}
	
	@Override
	protected boolean isProcessOutstandingRequestsAllowed() {
		return false;
	}

	/**
	 * Load the components on the page on first render, 
	 * so that the components are only generated when the page is displayed 
	 */
	@Override
	protected void onBeforeRender() {
		super.onBeforeRender();
		if (!initialised) {
			if (getLogger().isDebugEnabled()) {
				getLogger().debug("Adding components to the page on first render");
			}
			add(getDistributionForm());
			add(getDistributionDetailsWindow());
			add(getTemplateHistoryWindow());
			initialised=true;
		}
	}

	private Logger getLogger() {
		if (logger == null) {
			logger = Logger.getLogger(this.getClass());
		}
		return logger;
	}
	
	/**
	 * Get the main page form
	 * @return
	 */
	private DistributionForm getDistributionForm() {
		if (distributionForm==null) {
			distributionForm = new DistributionForm("distributionForm");
		}
		return distributionForm;
	}
	
	/**
	 * This class represents the page form to be added to the panel
	 * @author kxd1203
	 */
	private class DistributionForm extends Form {

		public DistributionForm(String id) {
			super(id);
			initComponents();
		}

		/**
		 * Add all components to the form
		 */
		private void initComponents() {
			/**
			 * Add components
			 */
			
			add(getDistributionDetailsWindow());
			add(getTemplateHistoryWindow());
			add(getDistributionContainer());
			/**
			 * Check field visibility
			 */
			checkFieldVisibility();
			
		}
	}
	
	@Override
	protected void checkFieldVisibility() {
		super.checkFieldVisibility();
		getDistributionContainer().setVisible(
				isVisible(AgreementGUIField.DISTRIBUTION_TEMPLATE) ||
				isVisible(AgreementGUIField.DISTRIBUTION_TEMPLATE_EFFECTIVE_FROM));
	}
	
	private Button getDistributionDetailsButton() {
		
		if (distributionDetailsButton==null) {
			distributionDetailsButton = new Button("distributionDetails");
			distributionDetailsButton.add(new AjaxFormComponentUpdatingBehavior("click") {
				

				@Override
				protected void onUpdate(AjaxRequestTarget target) {
					/**
					 * Update the distribution details model with the selected
					 * distribution templates details before showing the dialog
					 */
					int agmKindFin = 0;
					if (agmtKind == 0){
						agmKindFin = agmKind;
					}else{
						agmKindFin = agmtKind;
						
					}

					List<DistributionDetailDTO> list = 
						getGuiController().getDistributionDetails(
								agmKindFin,
								panelModel.getDistributionTemplateContainer().getDistributionDetails());
					
					if(distributionDetailsModel.getFranchiseTemplateKindEnum() != null && distributionDetailsModel.getDistributionKindGroupRatesDTOs() !=null && distributionDetailsModel.getDistributionKindGroupRatesDTOs().size() > 0){
						if(distributionDetailsModel.getFranchiseTemplateKindEnum() == FranchiseTemplateKindEnum.FRANCHISE ||
								distributionDetailsModel.getFranchiseTemplateKindEnum() == FranchiseTemplateKindEnum.FRANCHISE_MANAGER) {
					
							List<DistributionKindGroupRatesDTO> vDistributionKindGroupDTOs = getGuiController().findDistributionKindGroupDTOTemplateID(panelModel.getDistributionTemplateContainer().getDistributionDetails().getId());
							distributionDetailsModel.getDistributionKindGroupRatesDTOs().clear();
							distributionDetailsModel.getDistributionKindGroupRatesDTOs().addAll(vDistributionKindGroupDTOs);
						}
					}
					
					distributionDetailsModel.getDistributionDetails().clear();
					distributionDetailsModel.getDistributionDetails().addAll(list);
							
					/**
					 * Show the dialog
					 */
					getDistributionDetailsWindow().show(target);
			
				}
			});
		}
		return distributionDetailsButton;
	}
	
	private RepeatingView getDistributionPanel() {
		if (distributionPanel==null) {
			distributionPanel = new RepeatingView("distributionPanel");
			
			distributionPanel.add(createGUIFieldPanel(AgreementGUIField.DISTRIBUTION_TEMPLATE, 
					getDistributionTemplatePanel()));
			distributionTemplateEffectiveFromGUIPanel = createGUIFieldPanel(
								AgreementGUIField.DISTRIBUTION_TEMPLATE_EFFECTIVE_FROM,
								null,
								getDistributionTemplateEffectiveFromPanel().getEnclosedObject(),
								true);
			distributionTemplateEffectiveFromGUIPanel.setOutputMarkupId(true);
			distributionTemplateEffectiveFromGUIPanel.setOutputMarkupPlaceholderTag(true);
			distributionPanel.add(distributionTemplateEffectiveFromGUIPanel);
		}
		return distributionPanel;
	}
	
	private HelperPanel getDistributionTemplateEffectiveFromPanel() {
		if (distributionTemplateEffectiveFromPanel==null) {
			distributionDateChangeAllowed = getDistributionDateChangeAllowedFromModel();
			distributionTemplateEffectiveFromPanel = createGUIPageField(
					AgreementGUIField.DISTRIBUTION_TEMPLATE_EFFECTIVE_FROM,
					getContext(), ComponentType.DATE_SELECTION_TEXTFIELD ,true);
			System.out.println("Date component class: "+distributionTemplateEffectiveFromPanel
						.getEnclosedObject().getClass());
			if (distributionTemplateEffectiveFromPanel.getEnclosedObject() instanceof TextField) {
				((TextField)distributionTemplateEffectiveFromPanel
						.getEnclosedObject()).add(new TemplateDateValidator());
			}
			distributionTemplateEffectiveFromPanel.setOutputMarkupId(true);
			distributionTemplateEffectiveFromPanel.getEnclosedObject().setOutputMarkupId(true);
		}
		return distributionTemplateEffectiveFromPanel;
	}
	
	/**
	 * Swap date components between modify and view states where applicable.
	 * 
	 * The {@link DistributionTemplateDTO} holds a field that is set when converting from the property,
	 * to determine if the date is allowed to change. This is not set on the valid values, so that 
	 * the date is only allowed to change when value other than the currently selected value is selected. 
	 *
	 */
	protected void swapDateComponents() {
		/**
		 * If the state should change, then swap the component. State will be determined by the 
		 * view template.
		 */
		if (distributionDateChangeAllowed!=getDistributionDateChangeAllowedFromModel()) {
			distributionTemplateEffectiveFromPanel = null;
			distributionTemplateEffectiveFromGUIPanel.remove("panel");
			Component component = getHelperPanelForGuiFieldComponent(
					getDistributionTemplateEffectiveFromPanel().getEnclosedObject(), 
					true);
			distributionTemplateEffectiveFromGUIPanel.add(component);		
		}
	}
	
	private HelperPanel createDropDownChoicePanel(AgreementGUIField field,SRSDropDownChoice dropDownChoice,Label viewLabel) {
		dropDownChoice.setOutputMarkupId(true);
		dropDownChoice.setRequired(getViewTemplate().isRequired(field, getContext()));
		dropDownChoice.setLabel(new Model(field.getDescription()));
		dropDownChoice.add(new AjaxFormComponentUpdatingBehavior("change") {
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				updateThedistributionDetailsModel(target);
				swapDateComponents();
				target.add(distributionTemplateEffectiveFromGUIPanel);
			}
		});
		HelperPanel panel = createGUIPageField(field,getContext(),dropDownChoice, viewLabel);
		dropDownChoice.setOutputMarkupId(true);
		panel.setOutputMarkupId(true);
		return panel;
	}
	
	private boolean getDistributionDateChangeAllowedFromModel() {
		return panelModel!=null && 
		panelModel.getDistributionTemplateContainer()!=null &&
		panelModel.getDistributionTemplateContainer().getDistributionDetails()!=null
		?panelModel.getDistributionTemplateContainer().getDistributionDetails().isDateChangeAllowed()
		:false;
	}
	
	private Button getTemplateHistoryButton() {
		if (templateHistoryButton==null) {
			templateHistoryButton = new Button("templateHistoryButton");
			templateHistoryButton.add(new AjaxFormComponentUpdatingBehavior("click") {
				@Override
				protected void onUpdate(AjaxRequestTarget target) {
					
					/**
					 * Update the distribution details model with the 
					 * distribution templates History details before showing the dialog
					 */
					List<DistributionTemplateHistoryDTO> list = 
						getGuiController().getTemplateHistoryForAgreementNo(
								panelModel.getDistributionTemplateContainer());
					distributionDetailsModel.getDistribDetailsHistoryList().clear();
					distributionDetailsModel.getDistribDetailsHistoryList().addAll(list);
					/**
					 * Show the dialog
					 */
					getTemplateHistoryWindow().show(target);
				}
			});
		}
		return templateHistoryButton;
	}
	
	private ModalWindow getTemplateHistoryWindow() {
		if (templateHistoryWindow==null) {
			templateHistoryWindow = DistributionDetailsPopup.createModalWindow(
					distributionDetailsModel,"templateHistoryWindow");
		}
		return templateHistoryWindow;
	}
	
	@SuppressWarnings("serial")
	public HelperPanel getDistributionTemplatePanel() {
		if (distributionTemplatePanel==null) {
			SRSDropDownChoice dropDownChoice = new SRSDropDownChoice("value",
					new PropertyModel(
							panelModel.getDistributionTemplateContainer(),
							"distributionDetails"),
					panelModel.getValidDistributionTemplates(),
					new ChoiceRenderer("description","id"), "Select");
			dropDownChoice.add(new AttributeModifier("width", "150"));
		

			Label label = new Label("value", new PropertyModel(
					panelModel.getDistributionTemplateContainer(),
					 "distributionDetails")) {
				@Override
				public IConverter getConverter(Class arg0) {
					return new IConverter() {
						public Object convertToObject(String stringval, Locale arg1) {
							return null;
						}
						public String convertToString(Object objectval, Locale arg1) {
							if (objectval==null) {
								return null;
							}
							return ((DistributionTemplateDTO)objectval).getDescription();
						}
					};
				}
			};
			
			
			distributionTemplatePanel = createDropDownChoicePanel(AgreementGUIField.DISTRIBUTION_TEMPLATE, dropDownChoice, label); 
			distributionTemplatePanel.setOutputMarkupId(true);
		}
		return distributionTemplatePanel;
	}
	
	
	protected void updateThedistributionDetailsModel(AjaxRequestTarget target) {
		
		if(getEditState() == EditStateType.MODIFY){
			if(agmtKind == FranchiseTemplateKindEnum.FRANCHISE.getAgreementKind() ||
					agmtKind == FranchiseTemplateKindEnum.FRANCHISE_MANAGER.getAgreementKind()){
				distributionKindGroupDTOs = getGuiController().findDistributionKindGroupDTOTemplateID(panelModel.getDistributionTemplateContainer().getDistributionDetails().getId());
				distributionDetailsModel.setDistributionKindGroupRatesDTOs(distributionKindGroupDTOs);
			}
		}
	
	}

	public WebMarkupContainer getDistributionContainer() {
		if (distributionContainer == null) {
			distributionContainer = new WebMarkupContainer("distributionContainer");
			/**
			 * Distribution Panel
			 */
			RepeatingView distributionPanel = getDistributionPanel();
			distributionContainer.add(distributionPanel);
			distributionContainer.add(getDistributionDetailsButton());
			distributionContainer.add(getTemplateHistoryButton());
			
		}
		return distributionContainer;
	}
	
	private ModalWindow getDistributionDetailsWindow() {
		if (distributionDetailsWindow==null) {
		
				distributionDetailsWindow = DistributionDetailsPopup.createModalWindow(
					distributionDetailsModel,"distributionDetailsWindow");
		
		}
		return distributionDetailsWindow;
	}
	

	@Override
	protected ContextDrivenViewTemplate<AgreementGUIField, AgreementDTO> getViewTemplate() {
		return panelModel.getViewTemplate();
	}

	@Override
	protected AgreementDTO getViewTemplateContext() {
		if (viewTemplateContext == null) {
			viewTemplateContext = panelModel.getDistributionTemplateContainer();
		}
		return viewTemplateContext;
	}
	
	private AgreementDTO getContext() {
		return getViewTemplateContext();
	}

}
