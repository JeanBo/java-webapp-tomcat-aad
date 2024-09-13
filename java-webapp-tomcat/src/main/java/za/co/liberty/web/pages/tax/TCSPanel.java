package za.co.liberty.web.pages.tax;

import java.io.File;
import java.text.SimpleDateFormat;

import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.validation.IFormValidator;
import org.apache.wicket.model.PropertyModel;

import za.co.liberty.business.guicontrollers.taxgui.ITaxGuiController;
import za.co.liberty.dto.taxxml.TaxLogDTO;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.helpers.lookup.ServiceLocator;
import za.co.liberty.interfaces.agreements.AgreementKindType;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.data.enums.TaxIndicatorType;
import za.co.liberty.web.pages.BasePage;
import za.co.liberty.web.pages.panels.BasePanel;
import za.co.liberty.web.pages.tax.model.TaxGuiModel;
import za.co.liberty.web.system.SRSAuthWebSession;
import za.co.liberty.web.wicket.markup.html.form.SRSAbstractChoiceRenderer;
import za.co.liberty.web.wicket.markup.html.form.SRSDropDownChoice;
import za.co.liberty.xml.tax.Application;
import za.co.liberty.xml.tax.Parameter;

public class TCSPanel extends BasePanel {
	
	private static final long serialVersionUID = 5184011117772142255L;
	
	private String SELECTION_FORM = "xmlForm";
	protected TextField xmlNode;
	protected TextField actionIndicator;
	protected CheckBox saveXML;

	private Button submit; 
	private SRSDropDownChoice xmlSelector;
	private Form form;
	private Parameter execNode;
	transient protected ITaxGuiController guiController;
	public Application application;
		
	private CheckBox cproduce650;
	private CheckBox csendXML;
	private CheckBox ccreateTextCertificate;
	private CheckBox cexportpayments;
	private CheckBox cscheduleJob;
	private CheckBox cforceCertificates;
	private TextField medicalAidFirstTwoMembers;
	private Label taxYear;
	private TextField reconYear;
	private TextField reconMonth;
	private TextField medicalAidRemainingDependants;
	
	private TextField sarsStart;
	private TextField sarsEnd;
	private TextField libStart;
	private TextField libEnd;
	private TextField priorStart;
	private TextField priorEnd;
	private Label taxCodes;
	private TextField agreementKind;
	private TextField exclusions;
	private TextField agreements;
	private Label nonTaxTypes;
	private Label annualTypes;
	private Label fixedEarnings;
	private Label fringeBenefitCombined;
	private Label fringeBenefitService;
	private Label fringeBenefitLoans;
	private Label retirementFundCommission;
	private Label nonRetirementFundCommission;
	private TextField templateName;
	private Label fringeBenefitMedical;
	private Label medicalAidCredits;
	private Label medicalAidContra;
	private Label incomeProtectionEC;
	
	private Label logPath;
	private Label templatePath;
	private Label pdb;
	private Label agmnumbers;
	
	private String masterFullDirectoryLogPath;
	private TaxGuiModel pageModel2;
	private BasePage parentPage;
	
	private Label statusLabel;
	private SRSDropDownChoice<String> agreementKindsCombo;
	private SRSDropDownChoice<TaxIndicatorType> taxIndicatorCombo;
	
	private static final Logger logger = Logger.getLogger(TCSPanel.class);
	
	public TCSPanel(String id, EditStateType editState, Application application, BasePage parentPage, String fullPathName, String sep, TaxGuiModel pageModel2){
		super(id, editState);
		this.parentPage = parentPage;
		this.application = application;
		this.pageModel2 = pageModel2;
		
		//Create the name for the output folder and set the full path for all output files for the current tax run. 
		//The output folder name is made up of the tax year being kicked off and the current timestamp.
		
		//Lookup the tax year from the xml
		String taxYear = mapXMLLookup("TaxYear").getValue();
				
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd-HH_mm_ss");
		String currentDate = sdf.format(new java.util.Date());		
		String outputFolder = taxYear + "_" + currentDate;
		logger.info("outputFolder = " + outputFolder);
		
		masterFullDirectoryLogPath = fullPathName + sep + outputFolder;

		init();
	}
	
	private boolean createDirectory(String agreementKind){
		String tmpFolder = masterFullDirectoryLogPath+"_"+agreementKind;
		boolean success = (new File(tmpFolder)).mkdirs();
		logger.info("Create directory path:" + tmpFolder + " success:" + success);
		
		if (success) {
			logger.info("Current log:" + mapXMLLookup("LogPath").getValue() + ": change to:" + tmpFolder);
			mapXMLLookup("LogPath").setValue(tmpFolder);
		}	

		return success;
	}
	
	
	
	private void init(){
		add(form = new TaxForm(SELECTION_FORM));		
	}
		
	
	public class TaxForm extends Form {

		private static final long serialVersionUID = 1L;				
		
		public TaxForm(String id){
			super(id);
			
			String log1 = getTaxGuiController().getPdbPath();
			String log2 = getTaxGuiController().getAgmnumPath();
			add(templateName = createTemplateName("templateName"));
			add(logPath = createLogPath("logpath"));
			add(templatePath = createTemplatePath("templatePath"));
			add(pdb = createPdb("pdb"));
			add(agmnumbers = createAgmNumbers("agmnumbers"));
			add(saveXML = createSaveXML("saveXML"));
			add(agreementKindsCombo = createAgreementKindsCombo("agreementKindsCombo") );
			add(taxIndicatorCombo = createTaxIndicator("actionIndicatorCombo"));
			add(csendXML = createSendXML("sendXML"));
			add(cproduce650 = createProduce650("produce650"));
			add(ccreateTextCertificate = createTextCertificate("createTextCertificate"));
			add(cexportpayments = createExportPayments("exportPayments"));
			add(cscheduleJob = createScheduleJob("scheduleJob"));
			add(cforceCertificates = createForceCertificates("forceCertificates"));
			
			add(medicalAidFirstTwoMembers = createMedicalAidFirstTwoMembers("medicalAidFirstTwoMembers"));
			add(taxYear = createTaxYear("taxYear"));
			add(reconMonth = createReconMonth("reconMonth"));
			add(reconYear = createReconYear("reconYear"));
//			add(actionIndicator = createActionIndicator("actionIndicator"));
			add(medicalAidRemainingDependants = createMedicalAidRemainingDependants("medicalAidRemainingDependants"));
			add(sarsStart = createSarsStart("sarsStart"));
			add(sarsEnd = createSarsEnd("sarsEnd"));
			add(libStart = createLibStart("libStart"));
			add(libEnd = createLibEnd("libEnd"));
			add(priorStart = createPriorStart("priorStart"));
			add(priorEnd = createPriorEnd("priorEnd"));
			add(taxCodes = createTaxCodes("taxCodes"));
//			add(agreementKind = createAgreementKind("agreementKinds"));
			add(exclusions = createExclusions("exclusions"));
			add(agreements = createAgreements("agreements"));
			add(nonTaxTypes = createNonTaxTypes("nonTaxTypes"));
			add(annualTypes = createAnnualTypes("annualTypes"));
			add(fixedEarnings = createFixedEarnings("fixedEarnings"));
			add(fringeBenefitCombined = createFringeBenefitCombined("fringeBenefitCombined"));
			add(fringeBenefitService = createFringeBenefitService("fringeBenefitService"));
			add(fringeBenefitLoans = createFringeBenefitLoans("fringeBenefitLoans"));
			
			add(fringeBenefitMedical = createFringeBenefitMedical("fringeBenefitMedical"));
			
			
			add(retirementFundCommission = createRetirementFundCommission("retirementFundCommission"));
			add(nonRetirementFundCommission = createNonRetirementFundCommission("nonRetirementFundCommission"));
			
			add(medicalAidCredits = createMedicalAidCredits("medicalAidCredits"));
			add(medicalAidContra = createMedicalAidContra("medicalAidContra"));
			add(incomeProtectionEC = createIncomeProtectionEC("incomeProtectionEC"));
			
			add(submit = createSubmitButton("submitBut"));			
			
		}
		
		@Override
		public void add(IFormValidator validator) {
			// TODO Auto-generated method stub
			super.add(validator);
		}

		@Override
		protected void onSubmit() {
			
		}
	}		
	
	@SuppressWarnings("unchecked")
	private SRSDropDownChoice<TaxIndicatorType> createTaxIndicator(String id){
		SRSDropDownChoice tempSRSDropDown = new SRSDropDownChoice(id , 
				new PropertyModel(pageModel2 ,"selectedActionIndicator"),
				pageModel2.getRetrieveActionIndicator() , new SRSAbstractChoiceRenderer<Object>() {
				
							public Object getDisplayValue(Object arg0){
								if (arg0==null) {
									   return null;
								   }
								return ((TaxIndicatorType)arg0).toString();
							}

							@Override
							public String getIdValue(Object arg0, int index) {
								return ((TaxIndicatorType)arg0).getCode()+"";
							}
						
				},"");
				tempSRSDropDown.setOutputMarkupId(true);
				tempSRSDropDown.setRequired(true);
				tempSRSDropDown.setNullValid(false);
				return tempSRSDropDown;
	}
	
	@SuppressWarnings("unchecked")
	private SRSDropDownChoice<String> createAgreementKindsCombo(String id){
		SRSDropDownChoice tempSRSDropDown = new SRSDropDownChoice(id , 
				new PropertyModel(pageModel2 ,"selectedAgreementKind"),
				pageModel2.getRetrieveAllAgmKinds() , new ChoiceRenderer() {
							public Object getDisplayValue(Object arg0){
								if (arg0==null) {
									   return null;
								   }
								return ((AgreementKindType)arg0).toString();
							}
							public Object getIdValue(Object arg0){
								if (arg0==null) {
									   return null;
								   }
								return ((AgreementKindType)arg0).getKind()+"";
							}
						
				},"select one");
				tempSRSDropDown.setRequired(true);
				tempSRSDropDown.setNullValid(false);
				tempSRSDropDown.setOutputMarkupId(true);
				return tempSRSDropDown;
	}		
	
	private TextField createTemplateName(String id){
		TextField temp = new TextField(id, new PropertyModel(application,"name"));
		temp.setOutputMarkupId(true);
		return temp;
	}
	
	
	private Label createPdb(String id){
		Label temp = new Label(id, getTaxGuiController().getPdbPath());
		temp.setOutputMarkupId(true);
		return temp;
	}
	
	private Label createAgmNumbers(String id){
		Label temp = new Label(id, getTaxGuiController().getAgmnumPath());
		temp.setOutputMarkupId(true);
		return temp;
	}
	
	private Label createTemplatePath(String id){
		Label temp = new Label(id, getTaxGuiController().getTemplatePath());
		temp.setOutputMarkupId(true);
		return temp;
	}
	
	private Label createLogPath(String id){
		Label temp = new Label(id, getTaxGuiController().getLogPath());
		temp.setOutputMarkupId(true);
		return temp;
	}
	
	
	private Label createFringeBenefitLoans(String id){
		Label temp = new Label(id, new PropertyModel(mapXMLLookup("FringeBenefitLoans"),"value"));
		temp.setOutputMarkupId(true);
		return temp;
	}
	
	private Label createFringeBenefitMedical(String id){
		Label temp = new Label(id, new PropertyModel(mapXMLLookup("FringeBenefitMedical"),"value"));
		temp.setOutputMarkupId(true);
		return temp;
	}

	private Label createMedicalAidCredits(String id){
		Label temp = new Label(id, new PropertyModel(mapXMLLookup("MedicalAidCredits"),"value"));
		temp.setOutputMarkupId(true);
		return temp;
	}
	private Label createMedicalAidContra(String id){
		Label temp = new Label(id, new PropertyModel(mapXMLLookup("MedicalAidContraTypes"),"value"));
		temp.setOutputMarkupId(true);
		return temp;
	}
	private Label createIncomeProtectionEC(String id){
		Label temp = new Label(id, new PropertyModel(mapXMLLookup("IncomeProtectionEC"),"value"));
		temp.setOutputMarkupId(true);
		return temp;
	}
	
	
	private Label createRetirementFundCommission(String id){
		Label temp = new Label(id, new PropertyModel(mapXMLLookup("RetirementFundCommission"),"value"));
		temp.setOutputMarkupId(true);
		return temp;
	}
	private Label createNonRetirementFundCommission(String id){
		Label temp = new Label(id, new PropertyModel(mapXMLLookup("NonRetirementFundCommission"),"value"));
		temp.setOutputMarkupId(true);
		return temp;
	}
	
	
	private Label createFringeBenefitService(String id){
		Label temp = new Label(id, new PropertyModel(mapXMLLookup("FringeBenefitService"),"value"));
		temp.setOutputMarkupId(true);
		return temp;
	}
	
	private Label createFringeBenefitCombined(String id){
		Label temp = new Label(id, new PropertyModel(mapXMLLookup("FringeBenefitCombined"),"value"));
		temp.setOutputMarkupId(true);
		return temp;
	}
	private Label createFixedEarnings(String id){
		Label temp = new Label(id, new PropertyModel(mapXMLLookup("FixedEarnings"),"value"));
		temp.setOutputMarkupId(true);
		return temp;
	}
	
	private Label createAnnualTypes(String id){
		Label temp = new Label(id, new PropertyModel(mapXMLLookup("AnnualTypes"),"value"));
		temp.setOutputMarkupId(true);
		return temp;
	}
	private Label createNonTaxTypes(String id){
		Label temp = new Label(id, new PropertyModel(mapXMLLookup("NonTaxTypes"),"value"));
		temp.setOutputMarkupId(true);
		return temp;
	}
	private TextField createAgreements(String id){
		TextField temp = new TextField(id, new PropertyModel(mapXMLLookup("Agreements"),"value"));
		temp.setOutputMarkupId(true);
		return temp;
	}
	
	private TextField createExclusions(String id){
		TextField temp = new TextField(id, new PropertyModel(mapXMLLookup("exclusions"),"value"));
		temp.setOutputMarkupId(true);
		return temp;
	}
	private TextField createAgreementKind(String id){
		TextField temp = new TextField(id, new PropertyModel(mapXMLLookup("AgreementKinds"),"value"));
		temp.setOutputMarkupId(true);
		return temp;
	}
	private Label createTaxCodes(String id){
		Label temp = new Label(id, new PropertyModel(mapXMLLookup("RENDER_TAX_CODES"),"value"));
		temp.setOutputMarkupId(true);
		return temp;
	}
	private TextField createSarsStart(String id){
		TextField temp = new TextField(id, new PropertyModel(mapXMLLookup("SARS_StartDate"),"value"));
		temp.setOutputMarkupId(true);
		return temp;
	}
	private TextField createSarsEnd(String id){
		TextField temp = new TextField(id, new PropertyModel(mapXMLLookup("SARS_EndDate"),"value"));
		temp.setOutputMarkupId(true);
		return temp;
	}
	private TextField createLibStart(String id){
		TextField temp = new TextField(id, new PropertyModel(mapXMLLookup("Liberty_StartDate"),"value"));
		temp.setOutputMarkupId(true);
		return temp;
	}
	private TextField createLibEnd(String id){
		TextField temp = new TextField(id, new PropertyModel(mapXMLLookup("Liberty_EndDate"),"value"));
		temp.setOutputMarkupId(true);
		return temp;
	}
	private TextField createPriorStart(String id){
		TextField temp = new TextField(id, new PropertyModel(mapXMLLookup("PriorYear_StartDate"),"value"));
		temp.setOutputMarkupId(true);
		return temp;
	}
	private TextField createMedicalAidRemainingDependants(String id){
		TextField temp = new TextField(id, new PropertyModel(mapXMLLookup("MedicalAidRemainingDependants"),"value"));
		temp.setOutputMarkupId(true);
		return temp;
	}
	private TextField createPriorEnd(String id){
		TextField temp = new TextField(id, new PropertyModel(mapXMLLookup("PriorYear_EndDate"),"value"));
		temp.setOutputMarkupId(true);
		return temp;
	}
	private TextField createReconYear(String id){
		TextField temp = new TextField(id, new PropertyModel(mapXMLLookup("ReconYear"),"value"));
		temp.setOutputMarkupId(true);
		return temp;
	}
	private TextField createReconMonth(String id){
		TextField temp = new TextField(id, new PropertyModel(mapXMLLookup("ReconMonth"),"value"));
		temp.setOutputMarkupId(true);
		return temp;
	}
	private Label createTaxYear(String id){
		Label temp = new Label(id, new PropertyModel(mapXMLLookup("TaxYear"),"value"));
		temp.setOutputMarkupId(true);
		return temp;
	}
	
	private TextField createMedicalAidFirstTwoMembers(String id){
		TextField temp = new TextField(id, new PropertyModel(mapXMLLookup("MedicalAidFirstTwoMembers"),"value"));
		temp.setOutputMarkupId(true);
		return temp;
	}
	private CheckBox createExportPayments(String id){		
		   CheckBox temp = new CheckBox(id, new PropertyModel<Boolean>(mapXMLLookup("ExportPayments"), "value"){

			private static final long serialVersionUID = 1L;

			@Override
			public Boolean getObject() {				
				return new Boolean(mapXMLLookup("ExportPayments").getValue());
			}

			@Override
			public void setObject(Boolean val) {				
				mapXMLLookup("ExportPayments").setValue(val.toString());
			}
			
		});
		temp.add(new AjaxFormComponentUpdatingBehavior("click"){

			@Override
			protected void onUpdate(AjaxRequestTarget arg0) {
			}
			
		});
		temp.setOutputMarkupId(true);
		return temp;
	}
	
	private CheckBox createScheduleJob(String id){		
		
		CheckBox temp = new CheckBox(id, new PropertyModel<Boolean>(pageModel2, "scheduleJob"));
		   
		temp.add(new AjaxFormComponentUpdatingBehavior("click"){

			@Override
			protected void onUpdate(AjaxRequestTarget arg0) {
			}
			
		});
		temp.setOutputMarkupId(true);
		temp.setEnabled(pageModel2.isAllowScheduleJob());
		return temp;
	}
	
	private CheckBox createForceCertificates(String id){			
		   CheckBox temp = new CheckBox(id, new PropertyModel<Boolean>(mapXMLLookup("ForceCertificates"), "value"){

			private static final long serialVersionUID = 1L;

			@Override
			public Boolean getObject() {				
				return new Boolean(mapXMLLookup("ForceCertificates").getValue());
			}

			@Override
			public void setObject(Boolean val) {				
				mapXMLLookup("ForceCertificates").setValue(val.toString());
			}
			
		});
		temp.add(new AjaxFormComponentUpdatingBehavior("click"){

			@Override
			protected void onUpdate(AjaxRequestTarget arg0) {
			}
			
		});
		temp.setOutputMarkupId(true);
		return temp;
	}
	
	private CheckBox createTextCertificate(String id){		
		   CheckBox temp = new CheckBox(id, new PropertyModel<Boolean>(mapXMLLookup("CreateTextCertificate"), "value"){

			private static final long serialVersionUID = 1L;

			@Override
			public Boolean getObject() {				
				return new Boolean(mapXMLLookup("CreateTextCertificate").getValue());
			}

			@Override
			public void setObject(Boolean val) {				
				mapXMLLookup("CreateTextCertificate").setValue(val.toString());
			}
			
		});
		temp.add(new AjaxFormComponentUpdatingBehavior("click"){

			@Override
			protected void onUpdate(AjaxRequestTarget arg0) {
			}
			
		});
		temp.setOutputMarkupId(true);
		return temp;
	}
	private CheckBox createProduce650(String id){		
		   CheckBox temp = new CheckBox(id, new PropertyModel<Boolean>(mapXMLLookup("Produce650"), "value"){

			private static final long serialVersionUID = 1L;

			@Override
			public Boolean getObject() {				
				return new Boolean(mapXMLLookup("Produce650").getValue());
			}

			@Override
			public void setObject(Boolean val) {				
				mapXMLLookup("Produce650").setValue(val.toString());
			}
			
		});
		temp.add(new AjaxFormComponentUpdatingBehavior("click"){

			@Override
			protected void onUpdate(AjaxRequestTarget arg0) {
			}
			
		});
		temp.setOutputMarkupId(true);
		return temp;
	}
	
	private CheckBox createSendXML(String id){		
		   CheckBox temp = new CheckBox(id, new PropertyModel<Boolean>(mapXMLLookup("SendXML"), "value"){

			private static final long serialVersionUID = 1L;

			@Override
			public Boolean getObject() {				
				return new Boolean(mapXMLLookup("SendXML").getValue());
			}

			@Override
			public void setObject(Boolean val) {				
				mapXMLLookup("SendXML").setValue(val.toString());
			}
			
		});
		temp.add(new AjaxFormComponentUpdatingBehavior("click"){

			@Override
			protected void onUpdate(AjaxRequestTarget arg0) {
				logger.error("val = " + application.getParameters().getParameter().get(0).getValue());
			}
			
		});
		temp.setOutputMarkupId(true);
		return temp;
	}
	public Parameter mapXMLLookup(String name){
		for(Parameter instNode: application.getParameters().getParameter()){
			if(instNode.getName().equalsIgnoreCase(name)){
				execNode = instNode;
			}
		}
		return execNode;
	}	
	
	private TextField createExclude(String id){
		TextField temp = new TextField(id, new PropertyModel(mapXMLLookup("Exclusions"),"value"));
		temp.setOutputMarkupId(true);
		return temp;
	}
	
	private TextField createActionIndicator(String id){
		TextField temp = new TextField(id, new PropertyModel(mapXMLLookup("ActionIndicator"),"value"));
		temp.setOutputMarkupId(true);
		return temp;
		
	}
	
	private Button createSubmitButton(String id){
		Button temp = new Button(id){
		private static final long serialVersionUID = 1L;

		@Override
		public void onSubmit() {
			super.onSubmit();
			

			createDirectory(pageModel2.getSelectedAgreementKind().getKind()+"");
			logger.info("Inter Tax Value:" + pageModel2.getSelectedActionIndicator().getCode());
			logger.info("Get Agm Kind:" + pageModel2.getSelectedAgreementKind().getKind());
			
			mapXMLLookup("ActionIndicator").setValue(pageModel2.getSelectedActionIndicator().getCode());
			mapXMLLookup("AgreementKinds").setValue(pageModel2.getSelectedAgreementKind().getKind()+"");
			
			// Always gets updated, regardless of setting
			Application modifiedApplication = getTaxGuiController().saveXML(application);
			TaxLogDTO taxSessionLogDTO = new TaxLogDTO();
			taxSessionLogDTO.setScheduled(pageModel2.isScheduleJob());
			taxSessionLogDTO.setProcess(application.getName());
			taxSessionLogDTO.setUacfid(SRSAuthWebSession.get().getSessionUser().getPartyOid()+"");
			taxSessionLogDTO.setFileName(pageModel2.getXmlFileSelectedLocal());
			taxSessionLogDTO.setAgreementKind(pageModel2.getSelectedAgreementKind().getKind()+"");
			getTaxGuiController().execute(modifiedApplication, taxSessionLogDTO);
			setResponsePage(new TaxGuiPage(modifiedApplication.getName()
				+ " has been " + (taxSessionLogDTO.isScheduled() ? "scheduled" : "kicked off")
				+ ". For more info please check Public Folder. "
				+ " Emails to be sent to "	+ getTaxGuiController().getTcsMailToAddress()));
//			setRedirect(true);
			
		}};

		return temp;
	}
	
	private CheckBox createSaveXML(String id){		
		   CheckBox temp = new CheckBox(id, new PropertyModel<Boolean>(mapXMLLookup("SaveXML"), "value"){

			private static final long serialVersionUID = 1L;

			@Override
			public Boolean getObject() {				
				return new Boolean(mapXMLLookup("SaveXML").getValue());
			}

			@Override
			public void setObject(Boolean val) {				
				//pageModel.getParameters().getParameter().get(0).setValue(val.toString());
				mapXMLLookup("SaveXML").setValue(val.toString());
			}
			
		});
		temp.add(new AjaxFormComponentUpdatingBehavior("click"){

			@Override
			protected void onUpdate(AjaxRequestTarget arg0) {
				submit.setVisible(false);
				arg0.add(submit);
			}
			
		});
		temp.setOutputMarkupId(true);
		return temp;
	}
	
	private TextField createXmlNode(String id){		
		TextField temp = new TextField(id,new PropertyModel(application.getParameters().getParameter().get(0),"name"));
		temp.setOutputMarkupId(true);
		return temp;
	}
	
	
	protected ITaxGuiController getTaxGuiController() {
		if (guiController == null) {
			try {
				guiController = ServiceLocator.lookupService(ITaxGuiController.class);
			} catch (NamingException namingErr) {
				logger.error(""
						+ " Lookup of ITaxGuiController failed: "
						+ namingErr.getMessage());
				CommunicationException comm = new CommunicationException("Lookup of ITaxGuiController failed!");
				throw new CommunicationException(comm);
			} 
		}
		return guiController;
	}
	

	
}
