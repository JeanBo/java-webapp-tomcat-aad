package za.co.liberty.web.pages.tax.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import za.co.liberty.dto.taxxml.TaxGuiPathDTO;
import za.co.liberty.dto.taxxml.TaxLogDTO;
import za.co.liberty.interfaces.agreements.AgreementKindType;
import za.co.liberty.web.data.enums.TaxIndicatorType;
import za.co.liberty.xml.tax.Application;

public class  TaxGuiModel implements Serializable {

	/**
	 * Model for XML tax processes. 
	 */
	private static final long serialVersionUID = 1L;
	
	private Application selectedXMLContext;
	private List<TaxGuiPathDTO> retrieveAllLDAPPaths;
	private Map<String,String> retrieveAllTaxLDAPPathsMap;
	private List<TaxLogDTO> retrieveLogList;
	private String selectedXMLTemplateFullPath;
	private List<AgreementKindType> retrieveAllAgmKinds;
	private AgreementKindType selectedAgreementKind;
	private List<TaxIndicatorType> retrieveActionIndicator;
	private TaxIndicatorType selectedActionIndicator;

	private String logPath;
	private String templatePath;
	
	private Application xmlContext;
	private List<String> xmlFilesAllLocal;
	private String xmlFileSelectedLocal;
	private String xmlFileOutputBasicPath;
	
	private String fileName;
	private boolean isScheduleJob;
	private boolean isAllowScheduleJob;
	
	
	public List<TaxLogDTO> getRetrieveLogList() {
		return retrieveLogList;
	}
	public void setRetrieveLogList(List<TaxLogDTO> retrieveLogList) {
		this.retrieveLogList = retrieveLogList;
	}
	public List<AgreementKindType> getRetrieveAllAgmKinds() {
		return retrieveAllAgmKinds;
	}
	public void setRetrieveAllAgmKinds(List<AgreementKindType> retrieveAllAgmKinds) {
		this.retrieveAllAgmKinds = retrieveAllAgmKinds;
	}
	public AgreementKindType getSelectedAgreementKind() {
		return selectedAgreementKind;
	}
	public void setSelectedAgreementKind(AgreementKindType selectedAgreementKind) {
		this.selectedAgreementKind = selectedAgreementKind;
	}
	public List<TaxIndicatorType> getRetrieveActionIndicator() {
		if(retrieveActionIndicator == null) {
			List<TaxIndicatorType> b = new ArrayList<TaxIndicatorType>();
			for(TaxIndicatorType inst: TaxIndicatorType.values()){
				b.add(inst);
			}
			retrieveActionIndicator = b;
		}
		return retrieveActionIndicator;
	}
	public void setRetrieveActionIndicator(List<TaxIndicatorType> retrieveActionIndicator) {
		this.retrieveActionIndicator = retrieveActionIndicator;
	}
	public TaxIndicatorType getSelectedActionIndicator() {
		return selectedActionIndicator;
	}
	public void setSelectedActionIndicator(TaxIndicatorType selectedActionIndicator) {
		this.selectedActionIndicator = selectedActionIndicator;
	}
	public String getXmlFileOutputBasicPath() {
		return xmlFileOutputBasicPath;
	}
	public void setXmlFileOutputBasicPath(String xmlFileOutputBasicPath) {
		this.xmlFileOutputBasicPath = xmlFileOutputBasicPath;
	}
	public String getSelectedXMLTemplateFullPath() {
		return selectedXMLTemplateFullPath;
	}
	public void setSelectedXMLTemplateFullPath(String selectedXMLTemplateFullPath) {
		this.selectedXMLTemplateFullPath = selectedXMLTemplateFullPath;
	}
	public Map<String, String> getRetrieveAllTaxLDAPPathsMap() {
		return retrieveAllTaxLDAPPathsMap;
	}
	public void setRetrieveAllTaxLDAPPathsMap(
			Map<String, String> retrieveAllTaxLDAPPathsMap) {
		this.retrieveAllTaxLDAPPathsMap = retrieveAllTaxLDAPPathsMap;
	}
	
	
	public List<TaxGuiPathDTO> getRetrieveAllLDAPPaths() {
		if(retrieveAllLDAPPaths == null) {
			return new ArrayList<TaxGuiPathDTO>();
		}
		return retrieveAllLDAPPaths;
	}
	public void setRetrieveAllLDAPPaths(List<TaxGuiPathDTO> retrieveAllLDAPPaths) {
		this.retrieveAllLDAPPaths = retrieveAllLDAPPaths;
	}
	public Application getSelectedXMLContext() {
		return selectedXMLContext;
	}
	public void setSelectedXMLContext(Application selectedXMLContext) {
		this.selectedXMLContext = selectedXMLContext;
	}
	public List<String> getXmlFilesAllLocal() {
		return xmlFilesAllLocal;
	}
	public void setXmlFilesAllLocal(List<String> xmlFilesAllLocal) {
		this.xmlFilesAllLocal = xmlFilesAllLocal;
	}
	public String getXmlFileSelectedLocal() {
		if(xmlFileSelectedLocal == null){
			return "";
		}
		return xmlFileSelectedLocal;
	}
	public void setXmlFileSelectedLocal(String xmlFileSelectedLocal) {
		this.xmlFileSelectedLocal = xmlFileSelectedLocal;
	}
	public Application getXmlContext() {
		return xmlContext;
	}
	public void setXmlContext(Application xmlContext) {
		this.xmlContext = xmlContext;
	}
	public boolean isScheduleJob() {
		return isScheduleJob;
	}
	public void setScheduleJob(boolean isScheduleJob) {
		this.isScheduleJob = isScheduleJob;
	}
	public boolean isAllowScheduleJob() {
		return isAllowScheduleJob;
	}
	public void setAllowScheduleJob(boolean isAllowScheduleJob) {
		this.isAllowScheduleJob = isAllowScheduleJob;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

}
