package za.co.liberty.web.pages.franchisetemplates;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.validation.IFormValidator;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.validation.validator.PatternValidator;
import org.apache.wicket.validation.validator.StringValidator;

import com.inmethod.grid.DataProviderAdapter;
import com.inmethod.grid.IGridColumn;
import com.inmethod.grid.SizeUnit;

import za.co.liberty.business.guicontrollers.template.IFranchiseTemplateGUIController;
import za.co.liberty.dto.gui.templates.DistributionKindGroupRatesDTO;
import za.co.liberty.exceptions.data.DataNotFoundException;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.helpers.lookup.ServiceLocator;
import za.co.liberty.interfaces.rating.FranchiseTemplateKindEnum;
import za.co.liberty.persistence.rating.entity.Description;
import za.co.liberty.web.data.enums.ComponentType;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.pages.franchisetemplates.model.FranchiseTemplatePanelModel;
import za.co.liberty.web.pages.panels.BasePanel;
import za.co.liberty.web.pages.panels.GUIFieldPanel;
import za.co.liberty.web.pages.panels.HelperPanel;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGrid;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGrid.GridSizeUnit;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGridColumn;
import za.co.liberty.web.wicket.markup.repeater.data.ListDataProvider;

/**
 * Franchise Template details panel, capturing the Group Kinds details
 * 
 * @author MZL2611
 * 
 */
public class FranchiseTemplateDetailsPanel extends BasePanel{	
	
	private static final Logger logger = Logger.getLogger(FranchiseTemplateDetailsPanel.class);
	
	private FeedbackPanel feedBackPanel;

	private SRSDataGrid distributionKindGroupRatesGrid;			
	
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
	
	private List<Description> scheduleDescriptions ;
	
	private List<Description> franchiseTemplates;
	
	private List<Description> franchiseManagerTemplates;
	
	private DropDownChoice templatesDD;
	
	
	private transient IFranchiseTemplateGUIController franchiseTemplateGUIController;
	/**
	 * @param arg0
	 */
	public FranchiseTemplateDetailsPanel(String id, final FranchiseTemplatePanelModel panelModel,
			EditStateType editState, FeedbackPanel feedBackPanel, Page parentPage) {
		super(id,  editState);			
		this.panelModel = panelModel;		
		this.feedBackPanel = null;
		try {
			
			panelModel.setDistributionKindGroupEnum(FranchiseTemplateKindEnum.FRANCHISE);
			Map<Long, DistributionKindGroupRatesDTO> templateMap = new HashMap<Long, DistributionKindGroupRatesDTO> ();
			List<Description> existingTemplatesStoredOnDKGRates = getFranchiseTemplateGUIController().getExistingTemplatesStoredOnDKGRates();
			List<DistributionKindGroupRatesDTO> dkgDTOList  = getFranchiseTemplateGUIController().getDistributionKindGroupRatesDTOByDKG(panelModel.getDistributionKindGroupEnum().getDistributionKindGroup());
			
			franchiseManagerTemplates = new ArrayList<Description>();
			franchiseTemplates = new ArrayList<Description>();
			
			//create two lists of existingTemplates
			//franchisetemplatelist
			//franchiseManagetemplateList
			if(dkgDTOList != null && dkgDTOList.size() > 0)	{
				for (DistributionKindGroupRatesDTO ratesDTO : dkgDTOList) {
					Long templateKey = new Long(ratesDTO.getTemplateID());
					if(!templateMap.containsKey(templateKey)){
						templateMap.put(templateKey, ratesDTO);
					}
				}
			}
			
			
			for (Description description : existingTemplatesStoredOnDKGRates) {
				Long templateKey = new Long(description.getUniqId());
				Description vDescription = new Description();
				vDescription.setDescription(description.getDescription());
				vDescription.setDescriptionKind(description.getDescriptionKind());
				vDescription.setEndDate(description.getEndDate());
				vDescription.setName(description.getName());
				vDescription.setReference(description.getReference());
				vDescription.setStartDate(description.getStartDate());
				vDescription.setUniqId(description.getUniqId());

				
				if(templateMap.get(templateKey) != null){
				
					franchiseTemplates.add(vDescription);
				}
				if(templateMap.get(templateKey) == null){
					franchiseManagerTemplates.add(vDescription);
				}
			}
			 
			if(panelModel.getTemplates() == null){
				panelModel.setTemplates(new ArrayList<Description>());
			}
			if(panelModel.getTemplate() == null){
				panelModel.setTemplate(new Description());
				
			}
			panelModel.getTemplates().clear();
			panelModel.getTemplates().addAll(franchiseTemplates);
		
		} catch (DataNotFoundException e) {
			error(e.getMessage());
		}
		//check outstanding requests and disable fields		
		add(franchiseTemplateForm = createFranchiseTemplateForm("franchiseTemplateFromExistingForm"));
	}
	
	/**
	 * Load the components on the page on first render, 
	 * so that the components are only generated when the page is displayed 
	 */
	@Override
	protected void onBeforeRender() {
		if(!initialised) {			
			initialised=true;				
	
			scheduleDescriptions = getFranchiseTemplateGUIController().getScheduleDescriptions();
			
			//check for existing requests FIRST as other panels use variables set here
			
			//after the outstanding requests, we check if user can actually raise requests on left of request kinds
		
				
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

			RepeatingView templateSinglePanel = new RepeatingView("templateSinglePanel");
			this.add(templateSinglePanel);
			
			EditStateType[] editstates = new EditStateType[]{EditStateType.MODIFY,EditStateType.ADD};
			
			GUIFieldPanel templateNamePanel = createGUIFieldPanel("Template Name","Template Name","Template Name",createPageField(panelModel.getFranchiseTemplateDTO(),"Template Name","panel","templateName",ComponentType.TEXTFIELD, true,true,editstates));
			if(((HelperPanel)templateNamePanel.getComponent()).getEnclosedObject() instanceof TextField){
				TextField field = (TextField)((HelperPanel)templateNamePanel.getComponent()).getEnclosedObject();
				field.add(  StringValidator.lengthBetween(5, 99));
				field.add( new PatternValidator("^[a-zA-Z_0-9\\s]{1,59}$"));

				field.add(new AttributeModifier("size","60"));
				validationComponents.add(field);
			}
			if(getEditState() == EditStateType.VIEW){
				templateNamePanel.setVisible(false);
			}
			templateSinglePanel.add(templateNamePanel);
		
			this.add(distributionKindGroupRatesGrid = createDistributionKindGroupRatesGrid("distributionKindGroupRatesList", panelModel));
			
			this.add(createAgreementKindField("agreementKind"));
			
			this.add(templatesDD = createDistributionKindGroup("Templates"));

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
							//do nothing being validated in FranchiseTemplatePanel
					}					
				}
			});
		}
	}	
	
	private DropDownChoice createDistributionKindGroup(String id) {
		List<Description> templates = null;
		if( panelModel.getTemplates() == null){
			templates = Collections.EMPTY_LIST;
		}
		else {
			templates = panelModel.getTemplates();
		}
		final DropDownChoice field = new DropDownChoice(id, new IModel() {
			private static final long serialVersionUID = -30570602008264258L;

			public Object getObject() {
				return panelModel.getTemplate();
			}
			public void setObject(Object arg0) {
				panelModel.setTemplate((Description) arg0);
			}
			public void detach() {
			}
			
		}, templates);
		field.setNullValid(true);
		field.add(new AjaxFormComponentUpdatingBehavior("onchange") {
			private static final long serialVersionUID = 1L;
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				updateTheModel((Description) field.getModelObject());
				target.add(distributionKindGroupRatesGrid);
 			}		
		});
		if(getEditState() == EditStateType.AUTHORISE)
			field.setVisible(false);
		return field;
	}
	
	private void updateTheModel(Description template) {
		try {
			if(template != null)
			{
				List<DistributionKindGroupRatesDTO> distributionKindGroupRates = getFranchiseTemplateGUIController().getDistributionKindGroupRatesByTemplateId(panelModel.getTemplate().getUniqId());
				panelModel.getFranchiseTemplateDTO().getDistributionKindGroupRatesDTOs().clear();
				panelModel.getFranchiseTemplateDTO().getDistributionKindGroupRatesDTOs().addAll(distributionKindGroupRates);
			}
		} catch (DataNotFoundException e) {
			
		}
	}	
	
	private DropDownChoice createAgreementKindField(String id) {
		List<FranchiseTemplateKindEnum> franchiseTemplateKindEnums = Arrays.asList(FranchiseTemplateKindEnum.values());
		final DropDownChoice field = new DropDownChoice(id, new IModel() {
			private static final long serialVersionUID = -30570602008264258L;

			public Object getObject() {
				return panelModel.getDistributionKindGroupEnum();
			}
			public void setObject(Object arg0) {
				panelModel.setDistributionKindGroupEnum((FranchiseTemplateKindEnum) arg0);
			}
			public void detach() {
			}
			
		}, franchiseTemplateKindEnums);
		field.setNullValid(true);
		field.add(new AjaxFormComponentUpdatingBehavior("onchange") {
			private static final long serialVersionUID = 1L;
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				updateTheModelForAgreementKind((FranchiseTemplateKindEnum) field.getModelObject());
				target.add(templatesDD);
				target.add(distributionKindGroupRatesGrid);
 			}		
		});
		if(getEditState() == EditStateType.AUTHORISE)
			field.setVisible(false);
		return field;
	}
	
	private void updateTheModelForAgreementKind(FranchiseTemplateKindEnum agreementKind) {
		if(agreementKind != null){
				panelModel.getTemplates().clear();
				if(agreementKind == FranchiseTemplateKindEnum.FRANCHISE){
					panelModel.getTemplates().addAll(franchiseTemplates);
				}else {
					panelModel.getTemplates().addAll(franchiseManagerTemplates);
				}
			
				List<DistributionKindGroupRatesDTO> list = getFranchiseTemplateGUIController().createDistributionKindGroupDTOsFromDKGDefaultValues(agreementKind.getDistributionKindGroup());
				panelModel.getFranchiseTemplateDTO().getDistributionKindGroupRatesDTOs().clear();
				panelModel.getFranchiseTemplateDTO().getDistributionKindGroupRatesDTOs().addAll(list);
		}
	}	

	
	
	/**
	 * Create a grid for the distribution kind groups
	 * 
	 * @return
	 */
	private SRSDataGrid createDistributionKindGroupRatesGrid(String id, FranchiseTemplatePanelModel model) {
		List<DistributionKindGroupRatesDTO> distributionGroupKindDTOs = model.getFranchiseTemplateDTO().getDistributionKindGroupRatesDTOs();
		if (distributionGroupKindDTOs == null) {
			distributionGroupKindDTOs = new ArrayList<DistributionKindGroupRatesDTO>();
		}
		
		SRSDataGrid grid = new SRSDataGrid(id, new DataProviderAdapter(
				new ListDataProvider<DistributionKindGroupRatesDTO>(distributionGroupKindDTOs)),
				getDistributionKindGroupRatesColumns(), getEditState(),null);
		grid.setOutputMarkupId(true);
		grid.setCleanSelectionOnPageChange(true);
		grid.setClickRowToSelect(false);
		grid.setAllowSelectMultiple(true);		
		//grid.setGridWidth(650, GridSizeUnit.PIXELS);
		grid.setGridWidth(99, GridSizeUnit.PERCENTAGE);
		if(getEditState() == EditStateType.VIEW){
			grid.setRowsPerPage(16);
			grid.setContentHeight(240, SizeUnit.PX);
		}else {
			grid.setRowsPerPage(16);
			grid.setContentHeight(380, SizeUnit.PX);
		}
		
		return grid;
	}

	/**
	 * Get the list of distributionking groups columns for the grid
	 * @return
	 */
	private List<IGridColumn> getDistributionKindGroupRatesColumns() {
		Vector<IGridColumn> cols = new Vector<IGridColumn>(6);
		
		
		cols.add(new SRSDataGridColumn<DistributionKindGroupRatesDTO>("description",
				new Model("Description"), "description", "description",
				getEditState()) {

			@Override
			public Panel newCellPanel(WebMarkupContainer parent,
					String componentId, IModel rowModel,
					String objectProperty, EditStateType state,
					DistributionKindGroupRatesDTO data) {				
				//HierarchyNodeLinkDTO currentParent = getCurrentParentNode();				
				if (getEditState().isViewOnly() || getEditState() ==  EditStateType.TERMINATE ) {
					return super.newCellPanel(parent, componentId,
							rowModel, objectProperty, state, data);
				}
				TextField distributionKindGroup = new TextField("value",
						new PropertyModel(data, objectProperty));
				distributionKindGroup.add(new AttributeModifier("size", "70"));
				distributionKindGroup.add(new AttributeModifier("maxlength", "10"));
				distributionKindGroup.add(new AttributeModifier("readonly","true"));
				distributionKindGroup.setRequired(true);
				distributionKindGroup.setLabel(new Model("description"));
				distributionKindGroup.add(new AjaxFormComponentUpdatingBehavior("onchange") {
					@Override
					protected void onUpdate(AjaxRequestTarget target) {
						//do nothing just want the object value updated
					}
				});			
				validationComponents.add(distributionKindGroup);				
				return HelperPanel.getInstance(componentId, distributionKindGroup, false);
			}

		}.setInitialSize(315));

		

		cols.add(new SRSDataGridColumn<DistributionKindGroupRatesDTO>("distributionFactor",
				new Model("Distribution Factor"), "distributionFactor", "distributionFactor", getEditState()) {

			@Override
			public Panel newCellPanel(WebMarkupContainer parent,
					String componentId, IModel rowModel,
					String objectProperty, EditStateType state,
					DistributionKindGroupRatesDTO data) {
				if (getEditState().isViewOnly() || getEditState() ==  EditStateType.TERMINATE) {
					return super.newCellPanel(parent, componentId,
							rowModel, objectProperty, state, data);
				}
				TextField distributionFactor = new TextField("value",
						new PropertyModel(data, objectProperty));
				distributionFactor.add(new AttributeModifier("size", "11"));
				distributionFactor.add(new AttributeModifier("maxlength", "10"));
				distributionFactor.setLabel(new Model("Distribution Factor"));
				distributionFactor.add(new AjaxFormComponentUpdatingBehavior("onchange") {
					@Override
					protected void onUpdate(AjaxRequestTarget target) {
						//do nothing just want the object value updated
					}
				});
				//validationComponents.add(endDate);	
				return HelperPanel.getInstance(componentId, distributionFactor, false);
			}

		}.setInitialSize(150));
		cols.add(new SRSDataGridColumn<DistributionKindGroupRatesDTO>("distributionSchedule",
				new Model("Distribution Schedule"), "distributionSchedule", "distributionSchedule", getEditState()) {

			@Override
			public Panel newCellPanel(WebMarkupContainer parent,
					String componentId, IModel rowModel,
					String objectProperty, EditStateType state,
					DistributionKindGroupRatesDTO data) {
				
				if (existingMaintenanceRequest 
						|| getEditState().isViewOnly() ) {
					//create label with type and display						
					Description vDescription = null;
					for (Description description : scheduleDescriptions) {
						
						if(data.getDistributionSchedule() == description.getReference()){
							vDescription = description;
							break;
						}
					}
					return HelperPanel.getInstance(componentId, new Label("value",(vDescription != null) ? vDescription.getDescription() : ""));
				}				
				final DropDownChoice dropdown = new DropDownChoice("value",new PropertyModel(data,objectProperty){
					@Override
						public Object getObject() {
							//return one of the values in the static list						
							Integer id = (Integer) super.getObject();
							if(id == null){
								return null;							
							}
							for(Description description : scheduleDescriptions){
								if(description.getReference() == id){
									return description;
								}
							}
							return null;
						}
						@Override
						public void setObject(Object arg0) {						
							super.setObject(((Description)arg0).getReference());
						}
				},scheduleDescriptions,new ChoiceRenderer("description", "reference"));
				dropdown.add(new AjaxFormComponentUpdatingBehavior("onchange"){
					@Override
					protected void onUpdate(AjaxRequestTarget target) {
						target.add(dropdown);
					}					
				});
				
//				if (getEditState().isViewOnly() || getEditState() ==  EditStateType.TERMINATE) {
//					return super.newCellPanel(parent, componentId,
//							rowModel, objectProperty, state, data);
//				}
//				
//				TextField distributionSchedule = new TextField("value",
//						new PropertyModel(data, objectProperty));
//				distributionSchedule.add(new SimpleAttributeModifier("size", "11"));
//				distributionSchedule.add(new SimpleAttributeModifier("maxlength", "10"));
//				distributionSchedule.setLabel(new Model("Distribution Schedule"));
//				distributionSchedule.add(new AjaxFormComponentUpdatingBehavior("onchange") {
//					@Override
//					protected void onUpdate(AjaxRequestTarget target) {
//						//do nothing just want the object value updated
//					}
//				});
				//create dropdown of selectable types				
				HelperPanel dropdownPanel = HelperPanel.getInstance(componentId, dropdown);	
				
				dropdownPanel.setVisible(true);
				
				return dropdownPanel;
				//validationComponents.add(endDate);	
				
			}
		}.setInitialSize(300));
		return cols;
	}

	
	public Class getPanelClass() {
		return FranchiseTemplateDetailsPanel.class;
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
	
}
