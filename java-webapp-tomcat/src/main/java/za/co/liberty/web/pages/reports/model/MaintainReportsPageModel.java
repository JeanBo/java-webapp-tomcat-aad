package za.co.liberty.web.pages.reports.model;

import java.io.Serializable;
import java.util.List;

import za.co.liberty.dto.gui.request.RequestEnquiryPeriodDTO;
import za.co.liberty.dto.reports.InfoSlipDocumentDTO;
import za.co.liberty.dto.reports.ReportMaintenanceDTO;
import za.co.liberty.dto.reports.ReportingStatusSearchDTO;
import za.co.liberty.persistence.srs.entity.ElectronicDocumentEntity;
import za.co.liberty.persistence.srs.entity.ReportingStatusEntity;
import za.co.liberty.web.data.pages.ITabbedPageModel;

public class MaintainReportsPageModel implements ITabbedPageModel<ReportMaintenanceDTO>,
	Serializable , Cloneable {

	private static final long serialVersionUID = 9208925872975907164L;

	private int currentTab;
	
	ReportMaintenanceDTO selectedItem;
	
	List<ReportingStatusEntity> reportingStatus;
	
	ReportingStatusEntity currentStatus;
	
	ElectronicDocumentEntity electronicDocument;
	
	ReportingStatusSearchDTO searchCriteria;
	private Class currentTabClass;
	
	InfoSlipDocumentDTO infoSlipDocumentDTO;
	
	private List<RequestEnquiryPeriodDTO> allPeriodList;
	
	public Class getCurrentTabClass() {		
		return currentTabClass;
	}


	public void setCurrentTabClass(Class currentTabClass) {
		this.currentTabClass = currentTabClass;		
	}
	
	
	public int getCurrentTab() {
		return currentTab;
	}

	public void setCurrentTab(int currentTab) {
		this.currentTab = currentTab;
	}

	public ReportMaintenanceDTO getSelectedItem() {
		return selectedItem;
	}

	public List<ReportMaintenanceDTO> getSelectionList() {
		return null;
	}

	public void setSelectedItem(ReportMaintenanceDTO selected) {
		this.selectedItem=selected;
	}

	public void setSelectionList(List<ReportMaintenanceDTO> selectionList) {
	}

	public List<ReportingStatusEntity> getReportingStatus() {
		return reportingStatus;
	}

	public void setReportingStatus(List<ReportingStatusEntity> reportingStatus) {
		this.reportingStatus = reportingStatus;
	}

	public ReportingStatusEntity getCurrentStatus() {
		return currentStatus;
	}

	public void setCurrentStatus(ReportingStatusEntity currentStatus) {
		this.currentStatus = currentStatus;
	}

	public ReportingStatusSearchDTO getSearchCriteria() {
		return searchCriteria;
	}

	public void setSearchCriteria(ReportingStatusSearchDTO searchCriteria) {
		this.searchCriteria = searchCriteria;
	}

	public ElectronicDocumentEntity getElectronicDocument() {
		return electronicDocument;
	}

	public void setElectronicDocument(ElectronicDocumentEntity electronicDocument) {
		this.electronicDocument = electronicDocument;
	}
	
	/**
	 * @return the infoslipDocDTO
	 */
	public InfoSlipDocumentDTO getInfoslipDocDTO() {
		return infoSlipDocumentDTO;
	}


	/**
	 * @param infoslipDocDTO the infoslipDocDTO to set
	 */
	public void setInfoslipDocDTO(InfoSlipDocumentDTO infoslipDocDTO) {
		this.infoSlipDocumentDTO = infoslipDocDTO;
	}

	public List<RequestEnquiryPeriodDTO> getAllPeriodList() {
		return allPeriodList;
	}
	public void setAllPeriodList(List<RequestEnquiryPeriodDTO> allPeriodList) {
		this.allPeriodList = allPeriodList;
	}
	
}

