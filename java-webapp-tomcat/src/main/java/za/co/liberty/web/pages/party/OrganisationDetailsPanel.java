package za.co.liberty.web.pages.party;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.naming.NamingException;

import org.apache.wicket.Page;
import org.apache.wicket.feedback.FeedbackMessage;
import org.apache.wicket.feedback.FeedbackMessages;
import org.apache.wicket.feedback.IFeedbackMessageFilter;
import org.apache.log4j.Logger;
import org.apache.wicket.AttributeModifier;//org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.validation.AbstractFormValidator;
import org.apache.wicket.markup.html.form.validation.IFormValidator;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.ValidationError;

import za.co.liberty.business.party.validator.IPartyValidator;
import za.co.liberty.database.enums.DatabaseEnumHelper;
import za.co.liberty.dto.databaseenum.LegalFormDBEnumDTO;
import za.co.liberty.dto.party.PartyDTO;
import za.co.liberty.exceptions.data.ValidationException;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.helpers.lookup.ServiceLocator;
import za.co.liberty.interfaces.agreements.AgreementKindType;
import za.co.liberty.web.data.enums.ComponentType;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.pages.panels.GUIFieldPanel;
import za.co.liberty.web.pages.panels.HelperPanel;
import za.co.liberty.web.pages.party.model.MaintainPartyPageModel;
import za.co.liberty.web.wicket.markup.html.form.SRSDateField;

/**
 * This panel represents the Organisation Details
 * 
 * @author dzs2610
 * 
 */
public class OrganisationDetailsPanel extends BasePartyDetailsPanel {	
	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unused")
	private OrganisationDetailsForm form;

	@SuppressWarnings("unused")
	private MaintainPartyPageModel pageModel;

	private EditStateType editState;
	
	private Collection<FormComponent> validationComponents = new ArrayList<FormComponent>();
	
	private transient IPartyValidator partyValidator;
	
	private boolean addingAgreement = false;
	
	private AgreementKindType agreementKindBeingAdded;
	
	protected static final Logger logger = Logger.getLogger(OrganisationDetailsPanel.class);

	 
	/**
	 * @param arg0
	 */
	public OrganisationDetailsPanel(String id,
			MaintainPartyPageModel pageModel, EditStateType editState, Page parentPage) {
		this(id, pageModel, editState, parentPage, false,null);
	}
	
	/**
	 * @param arg0
	 */
	public OrganisationDetailsPanel(String id,
			MaintainPartyPageModel pageModel, EditStateType editState, Page parentPage,boolean addingAgreement,
			AgreementKindType agreementKindBeingAdded) {
		super(id,editState,parentPage);
		this.pageModel = pageModel;
		this.editState = editState;	
		this.addingAgreement = addingAgreement;
		this.agreementKindBeingAdded = agreementKindBeingAdded;
		add(form = new OrganisationDetailsForm("pageForm",pageModel));

	}

	private class OrganisationDetailsForm extends Form {
		
		private static final long serialVersionUID = 1L;
		private PartyDTO maintainPartyDTO;
		
		private OrganisationDetailsForm(String id,final MaintainPartyPageModel pageModel) {
			super(id);
			
			maintainPartyDTO = pageModel.getPartyDTO();		
			
			RepeatingView topSinglePanel = new RepeatingView("topSinglePanel");
			RepeatingView bottomLeftPanel = new RepeatingView("bottomLeftPanel");
			RepeatingView bottomRightPanel = new RepeatingView("bottomRightPanel");
			add(topSinglePanel);
			add(bottomLeftPanel);
			add(bottomRightPanel);
			
			EditStateType[] editstates = new EditStateType[]{EditStateType.MODIFY,EditStateType.ADD};
			if(addingAgreement){
				editstates = new EditStateType[]{};
			}			
			
			/**
			 * Add the page fields
			 */
			GUIFieldPanel businessNamePanel = createGUIFieldPanel("Business Name","Business Name","Business Name",createPageField(maintainPartyDTO,"Business Name","panel","businessName",ComponentType.TEXTFIELD, true,true,editstates));
			if(((HelperPanel)businessNamePanel.getComponent()).getEnclosedObject() instanceof TextField){
				TextField field = (TextField)((HelperPanel)businessNamePanel.getComponent()).getEnclosedObject();
				field.add(new AttributeModifier("size","60"));
				validationComponents.add(field);
			}
			topSinglePanel.add(businessNamePanel);
			List dataList=new ArrayList();
	
			dataList.add("PTY");dataList.add("LTD");dataList.add("(PTY) LTD");dataList.add("PVT");dataList.add("(PVT) LTD");dataList.add("CC");	dataList.add("CC IN LIQDTN");
			GUIFieldPanel suffixPanel = createGUIFieldPanel("Suffix","Suffix","Suffix",createDropdownField(maintainPartyDTO,"Suffix","panel","legalForm",
					DatabaseEnumHelper.getDatabaseDTO(LegalFormDBEnumDTO.class,true,true),new ChoiceRenderer("name","key"),"Select one",true,true,editstates));
//			GUIFieldPanel suffixPanel = createGUIFieldPanel("Suffix","Suffix","Suffix",createDropdownField(maintainPartyDTO,"Suffix","panel","legalForm",dataList,new ChoiceRenderer("name","key"),"Select one",true,true,editstates));
			if(((HelperPanel)suffixPanel.getComponent()).getEnclosedObject() instanceof FormComponent){
				validationComponents.add((FormComponent)((HelperPanel)suffixPanel.getComponent()).getEnclosedObject());
			}
			topSinglePanel.add(suffixPanel);
			
			GUIFieldPanel registrationPanel = createGUIFieldPanel("Registration Number","Registration Number","Registration Number",createPageField(maintainPartyDTO,"Registration Number","panel","registrationNumber",ComponentType.TEXTFIELD, false,true,editstates));
			if(((HelperPanel)registrationPanel.getComponent()).getEnclosedObject() instanceof FormComponent){
				validationComponents.add((FormComponent)((HelperPanel)registrationPanel.getComponent()).getEnclosedObject());
			}
			topSinglePanel.add(registrationPanel);
			
			GUIFieldPanel registrationDateFromPanel = createGUIFieldPanel("Reg. Date From","Reg. Date From","Reg. Date From",createPageField(maintainPartyDTO,"Reg. Date From","panel","regDateFrom",ComponentType.DATE_SELECTION_TEXTFIELD, false,true,editstates));
			if(((HelperPanel)registrationDateFromPanel.getComponent()).getEnclosedObject() instanceof FormComponent){
				validationComponents.add((FormComponent)((HelperPanel)registrationDateFromPanel.getComponent()).getEnclosedObject());
			}
			if(((HelperPanel)registrationDateFromPanel.getComponent()).getEnclosedObject() instanceof SRSDateField){
				((SRSDateField)((HelperPanel)registrationDateFromPanel.getComponent()).getEnclosedObject()).addNewDatePicker();
			}
			bottomLeftPanel.add(registrationDateFromPanel);
			
			GUIFieldPanel registrationDateToPanel = createGUIFieldPanel("Reg. Date To","Reg. Date To","Reg. Date To",createPageField(maintainPartyDTO,"Reg. Date To","panel","regDateTo",ComponentType.DATE_SELECTION_TEXTFIELD, false,true,editstates));
			if(((HelperPanel)registrationDateToPanel.getComponent()).getEnclosedObject() instanceof FormComponent){
				validationComponents.add((FormComponent)((HelperPanel)registrationDateToPanel.getComponent()).getEnclosedObject());
			}
			if(((HelperPanel)registrationDateToPanel.getComponent()).getEnclosedObject() instanceof SRSDateField){
				((SRSDateField)((HelperPanel)registrationDateToPanel.getComponent()).getEnclosedObject()).addNewDatePicker();
			}
			
			bottomRightPanel.add(registrationDateToPanel);			
			
			GUIFieldPanel knownAsNamePanel = createGUIFieldPanel("Known as Name","Known as Name","Known as Name",createPageField(maintainPartyDTO,"Known as Name","panel","knownAsName",ComponentType.TEXTFIELD, false,true,editstates));
			if(((HelperPanel)knownAsNamePanel.getComponent()).getEnclosedObject() instanceof FormComponent){
				validationComponents.add((FormComponent)((HelperPanel)knownAsNamePanel.getComponent()).getEnclosedObject());
			}

			bottomRightPanel.add(knownAsNamePanel);				
			
			add(new AbstractFormValidator() {
				


				private static final long serialVersionUID = 1L;

				@SuppressWarnings("unchecked")
				public FormComponent[] getDependentFormComponents() {		
					if (getEditState().isAdd()) {
						// In add we have issues with showing form validation.
						return null;
					}
					return validationComponents.toArray(new FormComponent[] {});
				}

				public void validate(final Form form) {	
					System.out.println("validate");
					if (logger.isDebugEnabled())
						logger.info("Validated called " + getEditState());
					if (getEditState()==EditStateType.VIEW || getEditState()==EditStateType.AUTHORISE) {
						return;
					}
					boolean validate = true;
//					boolean validate = validateFormComponents(validationComponents, getFeedBackPanel());
					for(FormComponent comp : validationComponents){
						if(!comp.isValid()){
							validate = false;
//							comp.error("Form validation issue");
							break;
						}
					}
					if(validate){
						try{	
							if (logger.isDebugEnabled())
								logger.debug("validate party :");
							//validate party without contact details
							getPartyValidator().validate(pageModel.getPartyDTO(),null,false,addingAgreement,agreementKindBeingAdded);					
						}catch(ValidationException ex){
							for(String error : ex.getErrorMessages()){
								if (logger.isDebugEnabled())
									logger.debug("Validation error :" + error);
								getFeedBackPanel().error(error);								
							}
						}
					}
					
				}
				
			});
		
		}


	}
	
	
		
	/**
	 * get the PartyValidator bean
	 * @return
	 */
	private IPartyValidator getPartyValidator() {
		if(partyValidator == null){
			try {
				partyValidator = ServiceLocator.lookupService(IPartyValidator.class);
			} catch (NamingException e) {
				throw new CommunicationException(e);
			}
		}
		return partyValidator;
	}




	public EditStateType getEditState() {
		return editState;
	}	
}

