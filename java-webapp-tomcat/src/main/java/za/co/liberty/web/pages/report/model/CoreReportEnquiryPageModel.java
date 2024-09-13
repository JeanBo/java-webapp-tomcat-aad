package za.co.liberty.web.pages.report.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import za.co.liberty.dto.gui.report.ReportEnquiryDTO;
import za.co.liberty.dto.gui.request.RequestEnquiryPeriodDTO;
import za.co.liberty.dto.report.ReportEnquiryRequestDto;
import za.co.liberty.dto.report.ReportEnquiryResponseDto;
import za.co.liberty.interfaces.agreements.requests.RequestKindType;

public class CoreReportEnquiryPageModel implements Serializable{

	
	private List<RequestKindType> allRequestKindTypeList;
	private List<RequestEnquiryPeriodDTO> allPeriodList;
	private List<ReportEnquiryResponseDto> reportEnquiryResponseList;
	
	private Map<Class, ReportEnquiryDTO> dataModelMap = new HashMap<Class, ReportEnquiryDTO>();
	
	
	public ReportEnquiryDTO getDataModel(Class type) {
		return dataModelMap.get(type);
	}
	public void setDataModel(ReportEnquiryDTO dataModel, Class type) {
		dataModelMap.put(type, dataModel);
	}
	
	public List<RequestEnquiryPeriodDTO> getAllPeriodList() {
		return allPeriodList;
	}
	public void setAllPeriodList(List<RequestEnquiryPeriodDTO> allPeriodList) {
		this.allPeriodList = allPeriodList;
	}
	public List<RequestKindType> getAllRequestKindTypeList() {
		return allRequestKindTypeList;
	}
	public void setAllRequestKindTypeList(
			List<RequestKindType> allRequestKindTypeList) {
		this.allRequestKindTypeList = allRequestKindTypeList;
	}
	public List<ReportEnquiryResponseDto> getReportEnquiryResponseList() {
		return reportEnquiryResponseList;
	}
	public void setReportEnquiryResponseList(
			List<ReportEnquiryResponseDto> reportEnquiryResponseList) {
		this.reportEnquiryResponseList = reportEnquiryResponseList;
	}
	
	
	
}
