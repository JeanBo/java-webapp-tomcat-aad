package za.co.liberty.web.pages.baureports;

import java.util.HashMap;
import java.util.Map;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidationError;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.ValidationError;
import org.apache.wicket.validation.validator.EmailAddressValidator;
import org.apache.wicket.validation.validator.StringValidator;

import za.co.liberty.business.baureports.IBAUReportsManagement;
import za.co.liberty.dto.baureports.VEDExtractDTO;
import za.co.liberty.exceptions.UnResolvableException;
import za.co.liberty.exceptions.data.DataNotFoundException;
import za.co.liberty.srs.util.DateUtil;
import za.co.liberty.web.data.enums.ComponentType;
import za.co.liberty.web.pages.baureports.enums.BAUGUIField;
import za.co.liberty.web.pages.baureports.util.BAUReportsUtility;
import za.co.liberty.web.pages.panels.HelperPanel;
import za.co.liberty.web.wicket.markup.html.form.SRSTextField;

public class VEDExtractPanel extends Panel{
	private static final long serialVersionUID = 1L;
	
	private HelperPanel qualityCentreId;
	private HelperPanel thirteenDigitConsCodePanel;
	private HelperPanel agreementNumPanel;
	private HelperPanel dtFromPanel;
	private HelperPanel dtToPanel;
	private HelperPanel emailIDPanel;
	private HelperPanel generateButtonPanel;
	
	private Map<BAUGUIField, Component> labels;
	
	private VEDExtractDTO vedModel;
			
	
	public VEDExtractPanel(String id)
	{
		super(id);
		add(new VEDExtractForm("vedExtractForm"));
	}
	
	class VEDExtractForm extends Form {
				
		public VEDExtractForm(String id) {
			super(id);
			
			vedModel = new VEDExtractDTO();
			setModel(new CompoundPropertyModel(vedModel));
			labels = new HashMap<BAUGUIField, Component>();
			
			/**
			 * Add all labels
			 */
			for (BAUGUIField field : BAUGUIField.values()) {
				WebMarkupContainer label = createFieldLabel(field.getLabelId());
				labels.put(field, label);
				this.add(label);
			}
			
			add(getQualityCentreId());
			add(getThirteenDigitConsCodePanel());
			add(getAgreementNumPanel());
			add(getDtFromPanel());
			add(getDtToPanel());
			add(getEmailIDPanel());
			add(getGenerateButtonPanel());
		}
				
		@Override
		protected void onSubmit() {
			// TODO Auto-generated method stub
			CompoundPropertyModel cmpModel = (CompoundPropertyModel)getModel();
			VEDExtractDTO extractDTO = (VEDExtractDTO)cmpModel.getObject();
			
			if(!validateAllFields(extractDTO))
				return;
			
			
			try {
				IBAUReportsManagement  reportsManagement = BAUReportsUtility.getReportsManagement();
				reportsManagement.generateVEDExtract(extractDTO);
				this.getPage().replace(new VEDExtractResultPanel("vedextracts"));				
			} catch (DataNotFoundException e) {
				error(e.getMessage());
			}catch(Exception e){
				error(e.getMessage());
			}
			
		}
	}
	
	public HelperPanel getThirteenDigitConsCodePanel() {
		
		if (thirteenDigitConsCodePanel == null) {
			thirteenDigitConsCodePanel = createGUIPageField(BAUGUIField.THIRTEENDIGCONSCODE, 
					getVEDExtractDTO(),ComponentType.TEXTFIELD, false,true,true,13);
							
		}
		return thirteenDigitConsCodePanel;
	}
	
	public HelperPanel getAgreementNumPanel() {
		
		if (agreementNumPanel == null) {
			agreementNumPanel = createGUIPageField(BAUGUIField.AGREEMENT_NUMBER, 
					getVEDExtractDTO(),ComponentType.TEXTFIELD, false,true,false,0);
							
		}
		return agreementNumPanel;
	}

	public HelperPanel getEmailIDPanel() {
		
		if (emailIDPanel == null) {
			emailIDPanel = createGUIPageField(BAUGUIField.EMAIL_ID, 
					getVEDExtractDTO(),ComponentType.TEXTFIELD, true,false,false,0);
			
			if(emailIDPanel.getEnclosedObject() instanceof SRSTextField){
				
				SRSTextField textField  = (SRSTextField)emailIDPanel.getEnclosedObject() ;
				textField.add(new AttributeModifier("size","50"));
				textField.add(EmailAddressValidator.getInstance());
				
				emailIDPanel = HelperPanel.getInstance(BAUGUIField.EMAIL_ID.getFieldId(), textField);
				
			}							
		}
		
		return emailIDPanel;		
	}

	public HelperPanel getGenerateButtonPanel() {
		
		if(generateButtonPanel == null){
		
			Button button = new Button("value"){
				private static final long serialVersionUID = -5330766713711809176L;
				
				@Override
				protected void onComponentTag(ComponentTag tag) {
					super.onComponentTag(tag);
					tag.getAttributes().put("value", "Generate");
					tag.getAttributes().put("type", "submit");
				}
		};
		generateButtonPanel = HelperPanel.getInstance("generateBtn", button);
		}
		
		return generateButtonPanel;		
	}
	
	public HelperPanel getQualityCentreId() {
	
		if (qualityCentreId == null) {
			qualityCentreId = createGUIPageField(BAUGUIField.QUALITY_CENTRE_ID, 
					getVEDExtractDTO(),ComponentType.TEXTFIELD, true,false,false,0);
							
		}
		return qualityCentreId;		
	}

	private VEDExtractDTO getVEDExtractDTO() {
		if(vedModel == null){
			vedModel = new VEDExtractDTO();
		}
		return vedModel;
	}

	public HelperPanel getDtFromPanel() {
		
		if (dtFromPanel == null) {
			dtFromPanel = createGUIPageField(BAUGUIField.DATE_FROM, 
					getVEDExtractDTO(),ComponentType.DATE_SELECTION_TEXTFIELD, true,false,false,0);
							
		}
		return dtFromPanel;				
	}

	public HelperPanel getDtToPanel() {
		
		if (dtToPanel == null) {
			dtToPanel = createGUIPageField(BAUGUIField.DATE_TO, 
					getVEDExtractDTO(),ComponentType.DATE_SELECTION_TEXTFIELD, true,false,false,0);
							
		}
		return dtToPanel;	
	}
	
	private HelperPanel createGUIPageField(final BAUGUIField field,
			VEDExtractDTO propertyObject, ComponentType componentType,
			boolean required,boolean checkNumeric,boolean checkLength,int exactLengthVal) {
		
		HelperPanel helperPanel = null;		
	
		switch(componentType){
		
		case TEXTFIELD: SRSTextField txtField = new SRSTextField("value",
						new PropertyModel(propertyObject,field.getFieldId()));
						txtField.setLabel(new Model(field.getDescription()));	
						if(required)
							txtField.setRequired(true);
						if(checkNumeric){
							txtField.add(new IValidator(){

								public void validate(IValidatable arg0) {
									// TODO Auto-generated method stub
									String value = (String)arg0.getValue();
									if(value != null && !value.equals(""))
									{
										try{
											long l = Long.parseLong(value);
										}catch(NumberFormatException ne)
										{
											IValidationError err = new ValidationError()
											.setMessage(field.getDescription()+" is not Numeric");
											arg0.error(err);
										}
									}									
								}
								
							});
						}
						if(checkLength)
							txtField.add(StringValidator.exactLength(exactLengthVal));
						helperPanel = HelperPanel.getInstance(field.getFieldId(),txtField);
						break;
				
		case DATE_SELECTION_TEXTFIELD  :
			
						SRSTextField txtField2 = new SRSTextField("value",
								new PropertyModel(propertyObject, field.getFieldId()));
						txtField2.setLabel(new Model(field.getDescription()));			
						if (required) {
							txtField2.setRequired(true);
						}
						txtField2.add(new AttributeModifier("size","10"));
						txtField2.add(new AttributeModifier("maxlength","10"));
						
				
						helperPanel = HelperPanel.getInstance(field.getFieldId(), txtField2, true);
						helperPanel.setOutputMarkupId(true);
						helperPanel.setOutputMarkupPlaceholderTag(true);
						break;
						
		default: throw new UnResolvableException("Component type not known");
		 
			
		}
			
		return helperPanel;
	}
		
	private WebMarkupContainer createFieldLabel(String id) {
		WebMarkupContainer container = new WebMarkupContainer(id);
		container.setOutputMarkupId(true);
		container.setOutputMarkupPlaceholderTag(true);
		return container;
	}
	
	private boolean validateAllFields(VEDExtractDTO extractDTO){
		
		if(extractDTO == null)
			return true;
		//Validate that at least one Search Criteria is selected
		if((extractDTO.getAgreementNumber() == null || extractDTO.getAgreementNumber().trim().equals(""))&&
				extractDTO.getThriteendigCode() == null || extractDTO.getThriteendigCode().trim().equals("")){
			error("Please select at least one Search Criteria !!");
			return false;
		}
		
		//Validate for From Date to be <= To Date
		if(DateUtil.compareDates(extractDTO.getDateFrom(), extractDTO.getDateTo())>0){
			error("From Date should be <=  To Date !");
			return false;
		}
		
		return true;		
			
	}	

}
