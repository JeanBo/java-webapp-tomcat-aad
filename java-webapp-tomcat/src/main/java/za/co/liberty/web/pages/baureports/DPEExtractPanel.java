package za.co.liberty.web.pages.baureports;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.ListMultipleChoice;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.validation.validator.EmailAddressValidator;

import za.co.liberty.business.baureports.IBAUReportsManagement;
import za.co.liberty.dto.baureports.DPEExtractDTO;
import za.co.liberty.dto.common.ValuesDTO;
import za.co.liberty.exceptions.UnResolvableException;
import za.co.liberty.exceptions.data.DataNotFoundException;
import za.co.liberty.srs.util.DateUtil;
import za.co.liberty.web.data.enums.ComponentType;
import za.co.liberty.web.pages.baureports.enums.BAUGUIField;
import za.co.liberty.web.pages.baureports.util.BAUReportsUtility;
import za.co.liberty.web.pages.panels.HelperPanel;
import za.co.liberty.web.wicket.markup.html.form.SRSDropDownChoice;
import za.co.liberty.web.wicket.markup.html.form.SRSTextArea;
import za.co.liberty.web.wicket.markup.html.form.SRSTextField;

public class DPEExtractPanel extends Panel{
	private static final long serialVersionUID = 1L;
	
	private HelperPanel qualityCentreId;
	private HelperPanel commissionKindPanel;
	private HelperPanel agreementKindPanel;
	private HelperPanel productReferencePanel;
	private HelperPanel agreementNumPanel;
	private HelperPanel benefitTypePanel;
	private HelperPanel premFreqPanel;
	private HelperPanel contribIncIndicatorPanel;
	private HelperPanel dtFromPanel;
	private HelperPanel dtToPanel;
	private HelperPanel dateTypePanel;
	private HelperPanel emailIDPanel;
	private HelperPanel generateButtonPanel;
	
	private Map<BAUGUIField, Component> labels;
	
	private DPEExtractDTO dpeModel;
			
	
	public DPEExtractPanel(String id)
	{
		super(id);
		add(new DPEExtractForm("dpeExtractForm"));
	}
	
	class DPEExtractForm extends Form {
				
		public DPEExtractForm(String id) {
			super(id);
			
			dpeModel = new DPEExtractDTO();
			setModel(new CompoundPropertyModel(dpeModel));
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
			add(getCommissionKindPanel());
			add(getAgreementKindPanel());
			add(getProductReferencePanel());
			add(getAgreementNumPanel());
			add(getBenefitTypePanel());
			add(getPremFreqPanel());
			add(getContribIncIndicatorPanel());
			add(getDtTypePanel());
			add(getDtFromPanel());
			add(getDtToPanel());
			add(getEmailIDPanel());
			add(getGenerateButtonPanel());
		}
				
		@Override
		protected void onSubmit() {
			// TODO Auto-generated method stub
			CompoundPropertyModel cmpModel = (CompoundPropertyModel)getModel();
			DPEExtractDTO extractDTO = (DPEExtractDTO)cmpModel.getObject();
			
			if(!validateAllFields(extractDTO))
				return;
			
			
			try {
				IBAUReportsManagement  reportsManagement = BAUReportsUtility.getReportsManagement();
				reportsManagement.generateDPEExtract(extractDTO);
				this.getPage().replace(new DPEExtractResultPanel("dpeextracts"));				
			} catch (DataNotFoundException e) {
				error(e.getMessage());
			}catch(Exception e){
				error(e.getMessage());
			}
			
		}
	}
	
	public HelperPanel getAgreementKindPanel() {
		
		if (agreementKindPanel == null) {
			agreementKindPanel = createGUIPageField(BAUGUIField.AGREEMENT_KINDS, 
					getDPEExtractDTO(),ComponentType.LISTMULTIPLECHOICE, false);
							
		}
		
		return agreementKindPanel;
	}

	public HelperPanel getAgreementNumPanel() {
		
		if (agreementNumPanel == null) {
			agreementNumPanel = createGUIPageField(BAUGUIField.AGREEMENT_NUMBERS, 
					getDPEExtractDTO(),ComponentType.TEXTAREA, false);
							
		}
		return agreementNumPanel;
	}

	public HelperPanel getBenefitTypePanel() {
		
		if (benefitTypePanel == null) {
			benefitTypePanel = createGUIPageField(BAUGUIField.BENEFIT_TYPES, 
					getDPEExtractDTO(),ComponentType.LISTMULTIPLECHOICE, false);
							
		}
		
		return benefitTypePanel;
	}

	public HelperPanel getCommissionKindPanel() {
		
		if (commissionKindPanel == null) {
			commissionKindPanel = createGUIPageField(BAUGUIField.COMMISSION_KINDS, 
					getDPEExtractDTO(),ComponentType.LISTMULTIPLECHOICE, false);
							
		}
		return commissionKindPanel;
	}

	public HelperPanel getContribIncIndicatorPanel() {
		
		if (contribIncIndicatorPanel == null) {
			contribIncIndicatorPanel = createGUIPageField(BAUGUIField.CONTR_INCR_INDICATORS, 
					getDPEExtractDTO(),ComponentType.LISTMULTIPLECHOICE, false);
							
		}
		
		return contribIncIndicatorPanel;		
	}

	public HelperPanel getEmailIDPanel() {
		
		if (emailIDPanel == null) {
			emailIDPanel = createGUIPageField(BAUGUIField.EMAIL_ID, 
					getDPEExtractDTO(),ComponentType.TEXTFIELD, true);
			
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

	public HelperPanel getPremFreqPanel() {
	
		if (premFreqPanel == null) {
			premFreqPanel = createGUIPageField(BAUGUIField.PREMIUM_FREQUENCYS, 
					getDPEExtractDTO(),ComponentType.LISTMULTIPLECHOICE, false);
							
		}
		return premFreqPanel;		
	}

	public HelperPanel getProductReferencePanel() {
	
		if (productReferencePanel == null) {
			productReferencePanel = createGUIPageField(BAUGUIField.PRODUCT_REFERENCES, 
					getDPEExtractDTO(),ComponentType.LISTMULTIPLECHOICE, false);
							
		}
		return productReferencePanel;		
	}

	public HelperPanel getQualityCentreId() {
	
		if (qualityCentreId == null) {
			qualityCentreId = createGUIPageField(BAUGUIField.QUALITY_CENTRE_ID, 
					getDPEExtractDTO(),ComponentType.TEXTFIELD, true);
							
		}
		return qualityCentreId;		
	}

	private DPEExtractDTO getDPEExtractDTO() {
		if(dpeModel == null){
			dpeModel = new DPEExtractDTO();
		}
		return dpeModel;
	}

	public HelperPanel getDtFromPanel() {
		
		if (dtFromPanel == null) {
			dtFromPanel = createGUIPageField(BAUGUIField.DATE_FROM, 
					getDPEExtractDTO(),ComponentType.DATE_SELECTION_TEXTFIELD, true);
							
		}
		return dtFromPanel;				
	}

	public HelperPanel getDtToPanel() {
		
		if (dtToPanel == null) {
			dtToPanel = createGUIPageField(BAUGUIField.DATE_TO, 
					getDPEExtractDTO(),ComponentType.DATE_SELECTION_TEXTFIELD, true);
							
		}
		return dtToPanel;	
	}
	
	public HelperPanel getDtTypePanel() {
		
		if (dateTypePanel == null) {
			
			List<ValuesDTO> dateTypeVals = Arrays.asList(new ValuesDTO[]{
					 new ValuesDTO("1", "Request Date"),
					 new ValuesDTO("2", "Requested Date"),
					 new ValuesDTO("3", "Executed Date")});
			
			SRSDropDownChoice dateType = new SRSDropDownChoice("value",new PropertyModel(
					getDPEExtractDTO(), "dateType"),dateTypeVals,new ChoiceRenderer("text","id"),"-----Select-----");
			dateType.setRequired(true);
			dateType.setLabel(new Model(BAUGUIField.DATE_TYPE.getDescription()));	
			dateTypePanel = HelperPanel.getInstance(BAUGUIField.DATE_TYPE.getFieldId(), dateType);
							
		}
		return dateTypePanel;	
	}
	
	private HelperPanel createGUIPageField(BAUGUIField field,
			DPEExtractDTO propertyObject, ComponentType componentType,
			boolean required) {
		
		HelperPanel helperPanel = null;		
	
		switch(componentType){
		
		case TEXTFIELD: SRSTextField txtField = new SRSTextField("value",
						new PropertyModel(propertyObject,field.getFieldId()));
						txtField.setLabel(new Model(field.getDescription()));	
						if(required)
							txtField.setRequired(true);
						helperPanel = HelperPanel.getInstance(field.getFieldId(),txtField);
						break;
				
		case TEXTAREA : SRSTextArea txtArea = new SRSTextArea("value",
						new PropertyModel(propertyObject,field.getFieldId()));
						if(required)
							txtArea.setRequired(true);
							helperPanel = 	HelperPanel.getInstance(field.getFieldId(),txtArea);
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
						
				
		case LISTMULTIPLECHOICE  : ListMultipleChoice  listMultipleChoice = new ListMultipleChoice("value",
													new PropertyModel(propertyObject, field.getFieldId()),
													getListMultiChoiceOptionsForGUIField(field),
													new ChoiceRenderer("text","id"));
																		
										if (required) {
											listMultipleChoice.setRequired(true);
										}
										helperPanel = HelperPanel.getInstance(field.getFieldId(), listMultipleChoice);
										break;
										
		default: throw new UnResolvableException("Component type not known");
		 
			
		}
			
		return helperPanel;
	}
	
	
	private List<ValuesDTO> getListMultiChoiceOptionsForGUIField(BAUGUIField field)
	{
		List<ValuesDTO> list = null;
		
		switch (field){
		
		case COMMISSION_KINDS: list = BAUReportsUtility.getAllCommisionKindsList();
							   	break;
							   
		case AGREEMENT_KINDS:  list = BAUReportsUtility.getAllAgreementKinds();
							  	break;
							  
		case PRODUCT_REFERENCES: list = BAUReportsUtility.getAllProductReferences();
								 break;
		
		case BENEFIT_TYPES: list = BAUReportsUtility.getAllBenefitTypes();
		 					 break;
		 					 
		case PREMIUM_FREQUENCYS : list = BAUReportsUtility.getAllPremFrequencies();
		 						  break;
		 						  
		case CONTR_INCR_INDICATORS : list = BAUReportsUtility.getAllContribIncrIndicators();
		  						   break;
		 
		default: throw new UnResolvableException("BAU GUI Field not known");
		}
		//Sort the Collection
		Collections.sort(list);
				
		return list;
		
	}
	
	private WebMarkupContainer createFieldLabel(String id) {
		WebMarkupContainer container = new WebMarkupContainer(id);
		container.setOutputMarkupId(true);
		container.setOutputMarkupPlaceholderTag(true);
		return container;
	}
	
	private boolean validateAllFields(DPEExtractDTO extractDTO){
		
		if(extractDTO == null)
			return true;
		//Validate that at least one Search Criteria is selected
		if((extractDTO.getCommKinds() == null || extractDTO.getCommKinds().size() == 0)&&
				(extractDTO.getAgreementKinds() == null || extractDTO.getAgreementKinds().size() == 0)&&
				(extractDTO.getProductReferences() == null || extractDTO.getProductReferences().size() == 0)&&
				(extractDTO.getAgreementNumbers() == null || extractDTO.getAgreementNumbers().trim().equals(""))&&
						(extractDTO.getBenefitTypes() == null || extractDTO.getBenefitTypes().size() == 0)&&
						(extractDTO.getPremiumFrequencies() == null || extractDTO.getPremiumFrequencies().size() == 0)&&
						(extractDTO.getContrIncrIndicators() == null || extractDTO.getContrIncrIndicators().size() == 0)){
			error("Please select at least one Search Criteria !!");
			return false;
		}
		
		//Validate for Valid Agreement Numbers
		if(extractDTO.getAgreementNumbers() !=  null && 
				!BAUReportsUtility.validateForInputTextAreas(extractDTO.getAgreementNumbers())){
			error("Please enter valid Agreement Numbers (seperated by ','... if more than one)");
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
