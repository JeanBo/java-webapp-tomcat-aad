package za.co.liberty.web.wicket.validation.maintainagreement.associatedagmt;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.validation.AbstractFormValidator;

import za.co.liberty.business.guicontrollers.agreement.IAgreementGUIController;
import za.co.liberty.common.domain.Percentage;
import za.co.liberty.dto.agreement.AgreementDTO;
import za.co.liberty.dto.agreement.AssociatedAgreementDetailsDTO;
import za.co.liberty.dto.agreement.properties.CommissionKindsDTO;
import za.co.liberty.exceptions.data.ValidationException;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.helpers.lookup.ServiceLocator;
import za.co.liberty.helpers.util.SRSUtility;
import za.co.liberty.srs.util.DateUtil;
import za.co.liberty.web.data.enums.fields.AgreementGUIField;

public class AssociatedAgreementsGUIFormValidator extends AbstractFormValidator{

	
	private AgreementDTO agreementDTO;
	private transient IAgreementGUIController guiController;
	private transient Logger logger = Logger.getLogger(AssociatedAgreementsGUIFormValidator.class);
	private List<FormComponent> validationComponents ; 

 	
   public AssociatedAgreementsGUIFormValidator() {
   					
	}
   	
   	public AssociatedAgreementsGUIFormValidator(List<FormComponent> validationComponents, AgreementDTO agreementDTO) {
   		if(validationComponents != null)
   			this.validationComponents = validationComponents;
   		else
   			this.validationComponents = new ArrayList<FormComponent>();
   		
   		this.agreementDTO = agreementDTO;
}


	public IAgreementGUIController getGuiController() {
		if (guiController==null) {
			try {
				guiController = ServiceLocator.lookupService(IAgreementGUIController.class);
			} catch (NamingException e) {
				logger.fatal("Could not lookup IAgreementGUIController",e);
				throw new CommunicationException("Could not lookup IAgreementGUIController",e);
			}
		}
		return guiController;
	}


	
	public FormComponent[] getDependentFormComponents() {
		
		return this.validationComponents.toArray(new FormComponent[0]);
	}

	public void validate(Form arg0) {
		Long assAgmt = null;
		CommissionKindsDTO commKindDTO = null;
		Percentage percent = null;
		Date startDt = null;
		Date endDt = null;
		TextField endDateField = null;
		TextField assAgmtField = null;
		TextField assPercentageField = null;
		for(FormComponent comp : validationComponents){
		
			if(comp.getParent().getId().equals(AgreementGUIField.COMM_KIND.getFieldId())){
				
				commKindDTO = (CommissionKindsDTO)comp.getConvertedInput();
				
			}else if(comp.getParent().getId().equals(AgreementGUIField.ASSOCIATED_AGMT.getFieldId())){
				
				assAgmtField = (TextField)comp;
				assAgmt = (Long)assAgmtField.getConvertedInput();
				
			}else if(comp.getParent().getId().equals(AgreementGUIField.ASSOCIATED_PERCENTAGE.getFieldId())){
				
				assPercentageField = (TextField)comp;
				percent = (Percentage)assPercentageField.getConvertedInput();
				
			}else if(comp.getParent().getId().equals(AgreementGUIField.START_DATE.getFieldId())){
				
				startDt = (Date)comp.getConvertedInput();
				
			}else if(comp.getParent().getId().equals(AgreementGUIField.END_DATE.getFieldId())){
				
				endDateField = (TextField)comp;
				
				endDt = (Date)endDateField.getConvertedInput();
			}
			
		}
					
		Map<String,Object> map = new HashMap<String,Object>();
		
		if(!validateDates(startDt,endDt))
		{
			error(endDateField,"common.validator.enddtbeforestartdt",map);
			return;
		}
		
						
		if(checkExistingCommKindForAssAgmt(assAgmt,commKindDTO.getId())){
			map.put("input0", commKindDTO.getValue());
			map.put("input1", String.valueOf(assAgmt));
			error(assAgmtField,"associatedagmt.validator.activecommkind",map);	
			return;
		}
		
		if(!validateForPercentagePerCommKind(assAgmt,commKindDTO,percent))
		{
			map = new HashMap<String,Object>();
			error(assPercentageField,"associatedagmt.validator.percentpercommkind",map);	
			return;
		}
		
		validateForCommissionKindsForAssAgmts(assAgmt,commKindDTO,assAgmtField);
		
		
		
	}
	
	private void validateForCommissionKindsForAssAgmts(long agNo,CommissionKindsDTO commissionKindsDTO,TextField assAgmtField){

		IAgreementGUIController agreementGUIController = getGuiController();
		try {
			agreementGUIController.validateCommKindsForAddAssociatedAgmt(agNo, commissionKindsDTO.getId());
		} catch (ValidationException e) {
			/*error(validatable,e.getMessage());*/
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("input1", commissionKindsDTO.getValue());
			map.put("input0", String.valueOf(agNo));
			error(assAgmtField,e.getErrorMessages().get(0),map);
			return;
		}	

	}
	
	private boolean checkExistingCommKindForAssAgmt(long assAgmt,int commKind){

		if(agreementDTO == null || 	agreementDTO.getAssociatedAgreementDetailsList() == null)
			return false;
		List<AssociatedAgreementDetailsDTO> existingAssAgmtList = agreementDTO.getAssociatedAgreementDetailsList();
		
		for(AssociatedAgreementDetailsDTO detailsDTO:existingAssAgmtList){

			if((detailsDTO.getAssociatedAgreement() == assAgmt) && (detailsDTO.getCommissionKind().getId() == commKind)
					&& ((detailsDTO.getEndDate() == null) || (DateUtil.compareDates(detailsDTO.getEndDate(), detailsDTO.getStartDate())> -1 && 
							DateUtil.compareDates(detailsDTO.getEndDate(), new Date())>-1)))
			{
				return true;
			}
		}
		return false;
	}
	
	private boolean validateForPercentagePerCommKind(long assAgmt,CommissionKindsDTO commKindDTO,Percentage percentVal)
	{
		
		if(agreementDTO == null || 	agreementDTO.getAssociatedAgreementDetailsList() == null)
			return false;
        
        List<AssociatedAgreementDetailsDTO> existingAssAgmtList = agreementDTO.getAssociatedAgreementDetailsList();
        Map<CommissionKindsDTO,Percentage> commisionKindsMap = new HashMap<CommissionKindsDTO,Percentage>();
        
        Date currentDate = DateUtil.minimizeTime(new Date());
        //Group all the Existing AssociatedAgreements Details first
        for(AssociatedAgreementDetailsDTO detailsDTO:existingAssAgmtList){
        	CommissionKindsDTO commissionKindsDTO = detailsDTO.getCommissionKind();
        	Percentage percentage = (Percentage) commisionKindsMap.get(new Long(commissionKindsDTO.getId()));
        	  if (percentage == null) {
        		
        		  if(detailsDTO.getEndDate() == null || DateUtil.compareDates(detailsDTO.getEndDate(),currentDate) > -1)
        			  commisionKindsMap.put(commissionKindsDTO, detailsDTO.getAssociatedPercentage());
        	  } else
        	  {
        		  if(detailsDTO.getEndDate() == null || DateUtil.compareDates(detailsDTO.getEndDate(), currentDate) > -1)
        		  {
        			  BigDecimal addPercentVal = detailsDTO.getAssociatedPercentage().getValue().add(percentage.getValue());
        			  
        			  commisionKindsMap.put(commissionKindsDTO,SRSUtility.convertToPercentageDiv100(addPercentVal.toString()));
        		  }
        	  }
        }
        
        //Iterate through the Map and add percnetage value for matching CommKindDTO and validate
        Set<CommissionKindsDTO> keySet = commisionKindsMap.keySet();
        Iterator<CommissionKindsDTO> iterator = keySet.iterator();
        while (iterator.hasNext()) {
        	CommissionKindsDTO commKind = iterator.next();
        	Percentage existPercentVal = commisionKindsMap.get(commKind);
        	if(commKind.equals(commKindDTO)){
        	      	BigDecimal newPercentVal =  existPercentVal.getValue().add(percentVal.getValue());
        	      	Percentage xPercent = SRSUtility.convertToPercentageDiv100(newPercentVal.toString());
        	      	if (xPercent.isGreaterThan(Percentage.ONE_HUNDRED_PERCENT)) 
        	      		return false;
             }
        }
        
        return true;
        
	}


	
	private boolean validateDates(Date startDt,Date endDt)
	{
		if(startDt == null || endDt == null)
			return true;
		
		if(DateUtil.compareDates(endDt,startDt) < 0){
				return false;
			}		
		
		return true;
	}
}
