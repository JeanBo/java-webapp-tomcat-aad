package za.co.liberty.web.pages;

import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.naming.NamingException;

import org.apache.wicket.extensions.yui.calendar.DatePicker;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.validation.validator.PatternValidator;
import org.apache.wicket.validation.validator.StringValidator;


import za.co.liberty.business.guicontrollers.contracting.IContractNoEnquiryManagement;
import za.co.liberty.dto.common.ValuesDTO;
import za.co.liberty.dto.contracting.ContractEnquiryDTO;
import za.co.liberty.dto.contracting.ContractSearchResultDTO;
import za.co.liberty.dto.gui.context.PolicyTransactionTypeEnum;
import za.co.liberty.exceptions.UnResolvableException;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.helpers.lookup.ServiceLocator;
import za.co.liberty.helpers.util.SRSUtility;
import za.co.liberty.persistence.rating.IDescriptionEntityManager;
import za.co.liberty.persistence.rating.entity.Description;
import za.co.liberty.web.constants.SRSAppWebConstants;
import za.co.liberty.web.system.EJBReferences;
import za.co.liberty.web.system.SRSAuthWebSession;
import za.co.liberty.web.wicket.markup.html.form.SRSDateField;
import za.co.liberty.web.wicket.markup.html.form.SRSDropDownChoice;

public class ContractNoSearchPanel extends Panel {
	
	private static final long serialVersionUID = 1L;
	private RequiredTextField contractNo;
	private DropDownChoice dateType;
	private SRSDateField dateFrom;
	private SRSDateField dateTo;
	private DropDownChoice commKind; 
	private Button resetBtn;
			
	protected static final List<PolicyTransactionTypeEnum> transactionSearchTypeList;
	
	public ContractNoSearchPanel(String id)
	{
		super(id);
		Form form = new ContractNoSearchForm("contractNoSrchForm"); 
		add(form);
	}
	
	static {

		transactionSearchTypeList = new ArrayList<PolicyTransactionTypeEnum>();
		for (PolicyTransactionTypeEnum transactionSearchType : PolicyTransactionTypeEnum.values()) {
			transactionSearchTypeList.add(transactionSearchType);
		}
	}
	
	class ContractNoSearchForm extends Form {
				
		private static final long serialVersionUID = 1L;

		public ContractNoSearchForm(String id) {
			super(id);
			ContractNoFormSearchModel contractModel = new  ContractNoFormSearchModel();
			setModel(new CompoundPropertyModel(contractModel));

			contractNo = new RequiredTextField("contractNo",new PropertyModel(contractModel, "contractNo"));
			//To validate for alphanumeric
			contractNo.add(new PatternValidator(SRSAppWebConstants.PATTERN_ALPHANUMERIC));
			contractNo.add(StringValidator.maximumLength(15));
			List<ValuesDTO> dateTypeVals = Arrays.asList(new ValuesDTO[]{
											 new ValuesDTO("1", "Request Date"),
											 new ValuesDTO("2", "Requested Date"),
											 new ValuesDTO("3", "Executed Date")});
			
			dateType = new DropDownChoice("dateType",new PropertyModel(contractModel, "dateType"),dateTypeVals,new ChoiceRenderer("text","id"));
			
			dateType.setRequired(true);
						
			dateFrom =  new SRSDateField("dateFrom",new PropertyModel(contractModel, "dateFrom"));
			dateFrom.add(dateFrom.newDatePicker());
			//dateFrom.newDatePicker();
//			add(new PopupDatePicker("dateFromPicker",dateFrom));			
			//To restrict Future From Dates.
			//dateFrom.add(DateValidator.maximum(new java.util.Date()));
			
			dateTo = new SRSDateField("dateTo",new PropertyModel(contractModel, "dateTo"));
			dateTo.add(dateTo.newDatePicker());
			
//			add(new PopupDatePicker("dateToPicker",dateTo));
			//To restrict Future To Dates.
			//dateTo.add(DateValidator.maximum(new java.util.Date()));
			
			List<ValuesDTO> commKindVals = getCommKindList();
			Collections.sort(commKindVals);
						
			commKind = new SRSDropDownChoice("commKind",new PropertyModel(
					contractModel, "commKind"),commKindVals,new ChoiceRenderer("text","id"),"ALL");
			commKind.setOutputMarkupId(true);
			commKind.setNullValid(true);
			
			resetBtn = createResetButton("resetBtn");
			
			add(contractNo);
			add(dateType);
			add(dateFrom);
			add(dateTo);
			add(commKind);
			add(resetBtn);  
		}
		
		private List<ValuesDTO> getCommKindList()
		{
			HashMap<String,String> commKindMap = null;
			ArrayList<ValuesDTO> valuesDTOList = new ArrayList<ValuesDTO>();
			
			try {
				IContractNoEnquiryManagement contEnq;
				try {
					contEnq = ServiceLocator.lookupService(IContractNoEnquiryManagement.class);
				} catch (NamingException e) {
					throw new CommunicationException(e);
				}
					
				 
				ContractEnquiryDTO  outContEnq = contEnq.getCommisionKindsList();
				commKindMap = outContEnq.getCommKindList();
				
				for (Iterator iter = commKindMap.entrySet().iterator(); iter.hasNext();) {
					Map.Entry element = (Map.Entry) iter.next();
					String key = (String)element.getKey();
					String value =  (String)element.getValue();
					
					ValuesDTO valuesDTO = new ValuesDTO(key,value);
					valuesDTOList.add(valuesDTO);
					
				}				
			} catch (CommunicationException e) {
				// Pass to cycle processor
				throw e;
			} catch (UnResolvableException e) {
				// Pass to cycle processor
				throw e;
			}
			return valuesDTOList;
		}
		
		@Override
		protected void onSubmit() {
			Date dateFrom = null;
			Date dateTo = null;
			Date today = new Date();
			CompoundPropertyModel cmpModel = (CompoundPropertyModel)getModel();
			ContractNoFormSearchModel cModel =
				(ContractNoFormSearchModel)cmpModel.getObject();
			
			ValuesDTO dateType = cModel.getDateType();
			String dateFromStr = cModel.getDateFrom();
			String dateToStr = cModel.getDateTo();
			
			if(dateType != null && (dateFromStr == null||dateToStr == null)){
					error("Please enter valid Date Range");
					return;
			}
		
			if(!validateForDate(dateFromStr.trim()))
			{
				error("Please enter valid 'From Date' in the format 'DDMMYYYY'");
				return;
			}
			
			if(!validateForDate(dateToStr.trim()))
			{
				error("Please enter valid 'To Date' in the format 'DDMMYYYY'");
				return;
			}
				
			
			try {
				dateFrom = convertStringToDate(dateFromStr.trim());
				dateTo = convertStringToDate(dateToStr.trim());
			} catch (ParseException e1) {
				// Ignore
				error("Parse Exception caused");
			}
			
			
			if(dateFrom.compareTo(dateTo)>0){
				error("Please enter valid Date Range! 'To Date' must be on or after 'From Date'");
				return;
			}
			
			if(dateFrom.compareTo(today)>0){
				error("'From Date' cannot be later than today's date");
				return;
			}
			
			if(dateTo.compareTo(today)>0){
				error("'To Date' cannot be later than today's date");
				return;
			}
			
				
			try {
				ContractEnquiryDTO inpContEnqDTO = populateContractDTOFromModel(cModel);
				IContractNoEnquiryManagement contEnq = ServiceLocator.lookupService(IContractNoEnquiryManagement.class);
				
				ContractEnquiryDTO  outContEnq = contEnq.searchForContractNo(
						SRSAuthWebSession.get().getSessionUser(),
						inpContEnqDTO);
				
				//QA MZL2611 Gateway Investment Project 31-05-2013
				//Add the movement code description to the table 
				ArrayList<ContractSearchResultDTO> contractSrchResults = outContEnq.getContractSrchResults();
				for (ContractSearchResultDTO resultDTO : contractSrchResults) {
					
					//GatewayProductTransCodesEnum gatewayProductTransCodesEnum = GatewayProductTransCodesEnum.getGatewayProductTransCodesEnum(resultDTO.getMovementCode());
					IDescriptionEntityManager descriptionEntityManager;
					try {
						descriptionEntityManager = ServiceLocator.lookupService(IDescriptionEntityManager.class);
					} catch (NamingException e) {

						throw e;
					}
					List<Description> portfoliokinds = descriptionEntityManager.findValuesByName("portfolio_kind");
					String vDescription = "";
					try {

						for (Description description : portfoliokinds) {
							if(description.getReference() == Integer.parseInt(resultDTO.getMovementCode())){
								vDescription = description.getDescription();
							}
						}
						if(portfoliokinds != null){
							resultDTO.setMovementDescription(vDescription);
						}
					}
					catch(NumberFormatException nfe){
						
					}
					
//					for (Description description : portfoliokinds) {
//						if(description.getReference() == Integer.parseInt(resultDTO.getMovementCode())){
//							vDescription = description.getDescription();
//						}
//					}
//					if(portfoliokinds != null){
//						resultDTO.setMovementDescription(vDescription);
//					}
				}
				this.getPage().replace(new ContractNoSearchResultPanel("contractNoSrchResultPanel",outContEnq));
				
			} catch (CommunicationException e) {
				// Pass to cycle processor
				throw e;
			} catch (UnResolvableException e) {
				// Pass to cycle processor
				throw e;
			} catch (ParseException e) {
				error("Parse Exception caused");
			
			} catch (NamingException e) {
				error("naming exception");
				throw new CommunicationException(e);
			}
						
		}
		
		private boolean validateForDate(String date)
		{
			StringBuffer formattedStr = null;
			if(date.length() < 8|| date.length()>10){
				return false;
				
			}
			
			if(date.length() == 8){
				
				String days = date.substring(0,2);
				String month = date.substring(2,4);
				String year = date.substring(4);
				
				formattedStr = new StringBuffer(days).append("/").append(month).append("/").append(year);
				
				try {
					SRSUtility.format(formattedStr.toString(), SRSAppWebConstants.DATE_FORMAT);
				} catch (ParseException e) {
					
					return false;}
				
			}
			
				if(date.length() > 8){
				
				try {
					SRSUtility.format(date, SRSAppWebConstants.DATE_FORMAT);
				} catch (ParseException e) {
					
					return false;
					
					}
				
			}
				return true;
			
		}
		
		private Date convertStringToDate(String date) throws ParseException
		{
			Date date2 =  null;
			StringBuffer formattedStr = null;
						
			if(date.length() == 8){
				
				String days = date.substring(0,2);
				String month = date.substring(2,4);
				String year = date.substring(4);
				
				formattedStr = new StringBuffer(days).append("/").append(month).append("/").append(year);
				
				try {
					date2 = SRSUtility.format(formattedStr.toString(), SRSAppWebConstants.DATE_FORMAT);
				} catch (ParseException e) {
					
					throw new ParseException("ParseException caused in convertStringToDate",0);
					
					
					}
				
				}
			
				if(date.length() > 8){
				
				try {
					date2 = SRSUtility.format(date, SRSAppWebConstants.DATE_FORMAT);
				} catch (ParseException e) {
					
					throw new ParseException("ParseException caused in convertStringToDate",0);
					
					}
				
			}
				return date2;
			
		}
		
		
		
		private ContractEnquiryDTO populateContractDTOFromModel(ContractNoFormSearchModel cModel) throws ParseException
		{
			ContractEnquiryDTO contractDTO = new ContractEnquiryDTO();
			Date dateFrm = null;
			Date dateTo = null;
			
			 dateFrm = convertStringToDate(cModel.getDateFrom());
			 cModel.setDateFrom(SRSUtility.format(dateFrm, SRSAppWebConstants.DATE_FORMAT));
			 dateTo = convertStringToDate(cModel.getDateTo());
			 cModel.setDateTo(SRSUtility.format(dateTo, SRSAppWebConstants.DATE_FORMAT));						
			contractDTO.setContractNo(cModel.getContractNo());
			contractDTO.setDateType((cModel.getDateType() != null)? (cModel.getDateType().getId()):null);
			contractDTO.setDateFrom(dateFrm);
			contractDTO.setDateTo(dateTo);
			contractDTO.setCommKind((cModel.getCommKind() != null)? (cModel.getCommKind().getId()):null);
			
			return contractDTO;
						
		}
		
		/**
		 * Create a Reset button that calls {@link #doSave_onSubmit()}
		 * 
		 * @param id
		 * @return
		 */
		private Button createResetButton(String id) {
			Button button = new Button(id) {

				private static final long serialVersionUID = -5330766713711809176L;
				
				@Override
				protected void onComponentTag(ComponentTag tag) {
					super.onComponentTag(tag);
					tag.getAttributes().put("value", "Reset");
					tag.getAttributes().put("type", "submit");
				}

				@Override
				public void onSubmit() {
					
					clearAllFields();
					setResponsePage(this.getPage().getClass());
				}
			};
			
			button.setOutputMarkupId(true);
			button.setDefaultFormProcessing(false);
			return button;
		}
		
		private void clearAllFields(){
			
			CompoundPropertyModel cmpModel = (CompoundPropertyModel)getModel();
			ContractNoFormSearchModel contractNoFormSearchModel =
				(ContractNoFormSearchModel)cmpModel.getObject();
			contractNoFormSearchModel.setCommKind(null);
			contractNoFormSearchModel.setContractNo(null);
			contractNoFormSearchModel.setDateType(null);
			contractNoFormSearchModel.setDateFrom(null);
			contractNoFormSearchModel.setDateTo(null);
			this.getPage().replace(new EmptyPanel("contractNoSrchResultPanel"));
			
		}
		

	}
	
	public class ContractNoFormSearchModel implements Serializable
	{
		private String contractNo;
		private ValuesDTO dateType;
		private String dateFrom;
		private String dateTo;
		private ValuesDTO commKind;
		
		
		public ValuesDTO getCommKind() {
			return commKind;
		}
		public void setCommKind(ValuesDTO commKind) {
			this.commKind = commKind;
		}
		public String getContractNo() {
			return contractNo;
		}
		public void setContractNo(String contractNo) {
			this.contractNo = contractNo;
		}
		public String getDateFrom() {
			return dateFrom;
		}
		public void setDateFrom(String dateFrom) {
			this.dateFrom = dateFrom;
		}
		public String getDateTo() {
			//Date To Field should show current date by default
			if(dateTo == null||dateTo.trim() == "")
				dateTo = SRSUtility.format(new Date(),SRSAppWebConstants.DATE_FORMAT);
			return dateTo;
		}
		public void setDateTo(String dateTo) {
			this.dateTo = dateTo;
		}
		public ValuesDTO getDateType() {
			//Date Type Field to show Requested Date by default
			if(dateType == null)
				dateType =  new ValuesDTO("2", "Requested Date");
			return dateType;
		}
		public void setDateType(ValuesDTO dateType) {
			this.dateType = dateType;
		}
		
	}
}