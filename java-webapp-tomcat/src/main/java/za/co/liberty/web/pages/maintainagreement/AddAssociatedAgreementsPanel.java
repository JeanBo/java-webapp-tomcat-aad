package za.co.liberty.web.pages.maintainagreement;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.convert.IConverter;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.ValidationError;

import za.co.liberty.business.guicontrollers.agreement.IAgreementGUIController;
import za.co.liberty.dto.agreement.AssociatedAgreementDetailsDTO;
import za.co.liberty.exceptions.UnResolvableException;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.helpers.lookup.ServiceLocator;
import za.co.liberty.srs.util.DateUtil;
import za.co.liberty.web.data.enums.ComponentType;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.data.enums.fields.AgreementGUIField;
import za.co.liberty.web.data.enums.fields.IGUIField;
import za.co.liberty.web.pages.maintainagreement.model.MaintainAgreementPageModel;
import za.co.liberty.web.pages.panels.BasePanel;
import za.co.liberty.web.pages.panels.HelperPanel;
import za.co.liberty.web.wicket.markup.html.form.SRSDateField;
import za.co.liberty.web.wicket.markup.html.form.SRSDropDownChoice;
import za.co.liberty.web.wicket.markup.html.form.SRSTextField;
import za.co.liberty.web.wicket.validation.maintainagreement.associatedagmt.AssociatedAgreementValidator;
import za.co.liberty.web.wicket.validation.maintainagreement.associatedagmt.AssociatedAgreementsGUIFormValidator;
import za.co.liberty.web.wicket.validation.maintainagreement.associatedagmt.AssociatedPercentageValidator;

/**
 * This class represents the ADD Associated Agreements Panel in the Wizard
 * @author pks2802
 *
 */
public class AddAssociatedAgreementsPanel extends BasePanel{

	private static final long serialVersionUID = 2253771882080568353L;

	private AddAssociatedAgreementsForm pageForm;
	private MaintainAgreementPageModel pageModel;
	
	private FeedbackPanel feedBackPanel;
	
	private transient IAgreementGUIController guiController;
	
	private transient Logger logger = Logger.getLogger(this.getClass());
	
	private HelperPanel commKindPanel;
	private HelperPanel associatedPercentagePanel;
	private HelperPanel associatedAgreementPanel;
	private HelperPanel startDatePanel;
	private HelperPanel endDatePanel;
	
	private Map<AgreementGUIField, Component> fields;

	private Map<AgreementGUIField, Component> labels;
	
	private AssociatedAgreementDetailsDTO assAgreementDetailsDTO;
	private List<AssociatedAgreementDetailsDTO> editListAssAgmt;
	
	private List<FormComponent> validationComponents = new ArrayList<FormComponent>();

	private boolean pageDisplayed;
	
	private boolean editAssAgmtFlag;
				
	public AddAssociatedAgreementsPanel(String id, MaintainAgreementPageModel pageModel, 
			FeedbackPanel feedBackPanel, EditStateType editState,List<AssociatedAgreementDetailsDTO> editListAssAgmt) {
		super(id, editState);
		setPageModel(pageModel);
		setEditListAssAgmt(editListAssAgmt);
		this.editListAssAgmt = editListAssAgmt;
		this.feedBackPanel=feedBackPanel;
		pageDisplayed=false;
	}
	
	@Override
	protected void onBeforeRender() {
		if (!pageDisplayed) {
			add(getAddAssociatedAgreementsForm());
			pageDisplayed=true;
		}
		super.onBeforeRender();
	}

	public void setPageModel(MaintainAgreementPageModel pageModel) {
		this.pageModel=pageModel;
	}
	
	public void setEditListAssAgmt(List<AssociatedAgreementDetailsDTO> editListAssAgmt) {
		this.editListAssAgmt=editListAssAgmt;
		if(editListAssAgmt != null)
			this.editAssAgmtFlag = true;
		else
			this.editAssAgmtFlag = false;
	}
	
	/**
	 * Load the AgreementGUIController dynamically if it is null as this is a transient variable.
	 * @return {@link IAgreementGUIController}
	 */
	protected IAgreementGUIController getGuiController() {
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
	 * Get the main page form
	 * @return
	 */
	private AddAssociatedAgreementsForm getAddAssociatedAgreementsForm() {
		if (pageForm==null) {
			pageForm = new AddAssociatedAgreementsForm("pageForm");
		}
		return pageForm;
	}
	
	/**
	 * This class represents the page form to be added to the panel
	 * @author pks2802
	 */
	private class AddAssociatedAgreementsForm extends Form {

		public AddAssociatedAgreementsForm(String id) {
			
			super(id);
			
			labels = new HashMap<AgreementGUIField, Component>();
			fields = new HashMap<AgreementGUIField, Component>();
			StringBuilder str = null;
			
			/**
			 * Add all labels
			 */
			for (AgreementGUIField field : AgreementGUIField.getAssociatedAgreementsLabels()) {
				str = new StringBuilder("");
				if(field == AgreementGUIField.END_DATE)
					str.append(field.getDescription()).append(colonSymbol);
				else
					str.append(field.getDescription()).append(asteriskSymbolWithFormatting).append(colonSymbol);
					
				Label lbl = new Label("value",str.toString());
				lbl.setEscapeModelStrings(false);
				HelperPanel panel = HelperPanel.getInstance(field.getLabelId(), lbl);
				labels.put(field, lbl);
				this.add(panel);
			}
			assAgreementDetailsDTO = new AssociatedAgreementDetailsDTO();
			this.setModel(new CompoundPropertyModel(assAgreementDetailsDTO));
			
			this.add(commKindPanel = getCommKindPanel());
			this.add(associatedAgreementPanel = getAssociatedAgreementPanel());
			this.add(associatedPercentagePanel = getAssociatedPercentagePanel());
			this.add(startDatePanel = getStartDatePanel());
			this.add(endDatePanel = getEndDatePanel());	
			if(!editAssAgmtFlag){
			this.add(new AssociatedAgreementsGUIFormValidator(validationComponents,
					pageModel.getMaintainAgreementDTO().getAgreementDTO()));
			}			
			
		}		
	}
	
	
	private HelperPanel getCommKindPanel(){
		
		if(commKindPanel == null){
			
						SRSDropDownChoice dropDownChoice = new SRSDropDownChoice("value",
					new PropertyModel(getAssociatedAgmtModel(), "commissionKind"), pageModel
							.getValidAgreementValues()
							.getValidCommKindsForAssociatedAgmt(),
					new ChoiceRenderer("value", "id"), "------------------------------Select-----------------------------");
		
		commKindPanel = createDropDownChoicePanel(AgreementGUIField.COMM_KIND, dropDownChoice,true);
		
			if(commKindPanel.getEnclosedObject() instanceof DropDownChoice)
			{
				validationComponents.add((DropDownChoice)commKindPanel.getEnclosedObject());
			}
		}
		

		
		return commKindPanel;
	}

	private HelperPanel getAssociatedAgreementPanel(){
		
		if (associatedAgreementPanel == null) {
			associatedAgreementPanel = createGUIPageField(AgreementGUIField.ASSOCIATED_AGMT,
					getAssociatedAgmtModel(),ComponentType.TEXTFIELD, true);
			
			if(associatedAgreementPanel.getEnclosedObject() instanceof TextField)
			{
				TextField textField = (TextField)associatedAgreementPanel.getEnclosedObject();
				textField.add(new AssociatedAgreementValidator(
						pageModel.getMaintainAgreementDTO()
						.getAgreementDTO()));	
				
				validationComponents.add(textField);
				
			}
			
		}
		return associatedAgreementPanel;
	}

	private HelperPanel getAssociatedPercentagePanel(){
		
		if (associatedPercentagePanel == null) {
			associatedPercentagePanel = createGUIPageField(AgreementGUIField.ASSOCIATED_PERCENTAGE,
					getAssociatedAgmtModel(),ComponentType.TEXTFIELD, true);
			
			if(associatedPercentagePanel.getEnclosedObject() instanceof TextField)
			{
				TextField textField = (TextField)associatedPercentagePanel.getEnclosedObject();
				textField.add(new AssociatedPercentageValidator());
				
				validationComponents.add(textField);
			}
		}
		return associatedPercentagePanel;
	}

	private HelperPanel getStartDatePanel() {
		if (startDatePanel == null) {
			startDatePanel = createGUIPageField(AgreementGUIField.START_DATE,
					getAssociatedAgmtModel(),
					ComponentType.DATE_SELECTION_TEXTFIELD, true);	
			
			if(editAssAgmtFlag)
			{
				startDatePanel = createPageField(AgreementGUIField.START_DATE.getFieldId(), AgreementGUIField.START_DATE.getLabelId(), 
						startDatePanel.getEnclosedObject(),getApplicableEditStates());
			}
			
			if(startDatePanel.getEnclosedObject() instanceof SRSDateField)
			{
				SRSDateField textField = (SRSDateField)startDatePanel.getEnclosedObject();
				textField.add(textField.newDatePicker());
				
				textField.add( new IValidator(){

					@Override
					public void validate(IValidatable validatable) {
						
						Map<String,String> map = new HashMap<String, String>();
						
						Date currDate = new Date();
						Object obj = validatable.getValue();
						if(obj == null)
							return;
						
						Date startDt = (Date)obj;
						
						if(DateUtil.compareDates(startDt, currDate) < 0){
							validatable.error(new ValidationError().addKey("common.validator.startdtbeforecurrdt"));
							return;
						}
						
						Date oneMonthPostDate = getOneMonthPostDate(currDate);
						
						if(DateUtil.compareDates(startDt, oneMonthPostDate) > 0){
							validatable.error(new ValidationError().addKey("common.validator.startdtnotmoreby1mnth"));
							return;
						}						
					}
					
				});
				
				validationComponents.add(textField);
			}
			}
		
		return startDatePanel;
				
	}

	private HelperPanel getEndDatePanel() {
		if (endDatePanel == null) {
			endDatePanel = createGUIPageField(AgreementGUIField.END_DATE,
					getAssociatedAgmtModel(),
					ComponentType.DATE_SELECTION_TEXTFIELD, false);

			if(endDatePanel.getEnclosedObject() instanceof SRSDateField)
			{
				SRSDateField textField = (SRSDateField)endDatePanel.getEnclosedObject();
				textField.add(textField.newDatePicker());
				
				textField.add( new IValidator(){

					@Override
					public void validate(IValidatable validatable) {
						
						Map<String,String> map = new HashMap<String, String>();

						Date currDate = new Date();
						Object obj = validatable.getValue();
						if(obj == null)
							return;
						
						Date endDt = (Date)obj;

						if(DateUtil.compareDates(endDt, currDate) < 0){
							validatable.error(new ValidationError().addKey("common.validator.enddtbeforecurrdt"));
							return;
						}
						
						if(AddAssociatedAgreementsPanel.this.getAssociatedAgmtModel() != null)
						{
							Date startDate = AddAssociatedAgreementsPanel.this.getAssociatedAgmtModel().getStartDate();
							if(startDate != null && DateUtil.compareDates(endDt, startDate) < 0){
								validatable.error(new ValidationError().addKey("common.validator.enddtbeforestartdt"));
								return;
							}
						}
					}

				});
								
				if(editAssAgmtFlag)
					((TextField)endDatePanel.getEnclosedObject()).setRequired(true);
				validationComponents.add(textField);
			}
		}
		return endDatePanel;
	}
	
	public AssociatedAgreementDetailsDTO getAssociatedAgmtModel() {
		if(editListAssAgmt != null && editListAssAgmt.size() >0){
			assAgreementDetailsDTO =  editListAssAgmt.get(0);
		}
		
		if(assAgreementDetailsDTO == null){
			assAgreementDetailsDTO = new AssociatedAgreementDetailsDTO();
		}
		return assAgreementDetailsDTO;
	}
	
	private HelperPanel createGUIPageField(AgreementGUIField field,
			AssociatedAgreementDetailsDTO propertyObject, ComponentType componentType,
			boolean required) {
		
		HelperPanel helperPanel = null;		
	
		switch(componentType){
		
		case TEXTFIELD: SRSTextField txtField = new SRSTextField("value",
						new PropertyModel(propertyObject,field.getFieldId())) {
				@Override
				public IConverter getConverter(Class arg0) {
					return super.getConverter(arg0);
				}
		};
						txtField.setRequired(required);
						txtField.setLabel(new Model(field.getDescription()));
						
						helperPanel = createPageField(field.getFieldId(), field.getDescription(), 
								txtField, getApplicableEditStates()); 
						break;
				
		case DATE_SELECTION_TEXTFIELD  :
						SRSDateField txtField2 = new SRSDateField("value", new PropertyModel(
								propertyObject, field.getFieldId()));// {
						
//						SRSTextField txtField2 = new SRSTextField("value",
//								new PropertyModel(propertyObject, field.getFieldId()));
						txtField2.setLabel(new Model(field.getDescription()));			
						txtField2.setRequired(required);
						
						txtField2.add(new AttributeModifier("size","10"));
						txtField2.add(new AttributeModifier("maxlength","10"));
							
						helperPanel = HelperPanel.getInstance(field.getFieldId(), txtField2, true);
						break;
						
		default: throw new UnResolvableException("Component type not known");
		
		}
		
		helperPanel.setOutputMarkupId(true);
		helperPanel.setOutputMarkupPlaceholderTag(true);
		return helperPanel;
	}
	
		
	private HelperPanel createDropDownChoicePanel(AgreementGUIField field,SRSDropDownChoice dropDownChoice,boolean required) {
	
		dropDownChoice.setOutputMarkupId(true);
		dropDownChoice.setRequired(required);
		dropDownChoice.setLabel(new Model(field.getDescription()));
		HelperPanel panel = createGUIPageField(field,getAssociatedAgmtModel(),dropDownChoice);
		dropDownChoice.setOutputMarkupId(true);
		panel.setOutputMarkupId(true);
		return panel;
	}
	
		
	/**
	 * Create a {@link HelperPanel} using definitions from the specified field
	 * @param field The {@link IGUIField} that represents the field
	 * @param propertyObject The context
	 * @param component the component to add
	 * @param viewLabel null to use a new {@link Label}, or non-null to 
	 * override the label in view mode
	 * @return a new {@link HelperPanel}
	 */
	private HelperPanel createGUIPageField(AgreementGUIField field,
			AssociatedAgreementDetailsDTO propertyObject, Component component) {
		
		HelperPanel ret = createPageField(field.getFieldId(), field.getDescription(), 
					 component, getApplicableEditStates()); 
		fields.put(field, ret);
		return ret;
	}	
	
	private Date getOneMonthPostDate(Date date)
	{
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.MONTH, 1);
		return calendar.getTime();
	}	
	
	private EditStateType[] getApplicableEditStates()
	{
		EditStateType[] editStateTypes = null;
		if(editAssAgmtFlag)
			editStateTypes = new EditStateType[] {EditStateType.VIEW};
		else
			editStateTypes = new EditStateType[] {EditStateType.ADD};
		
		return editStateTypes;
	}
}