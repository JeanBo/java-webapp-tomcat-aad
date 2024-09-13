package za.co.liberty.web.pages.request.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import za.co.liberty.dto.agreement.properties.CommissionKindsDTO;
import za.co.liberty.dto.gui.request.FundCodeDTO;
import za.co.liberty.dto.gui.request.ProductCodeDTO;
import za.co.liberty.dto.gui.request.RequestEnquiryDTO;
import za.co.liberty.dto.gui.request.RequestEnquiryPeriodDTO;
import za.co.liberty.dto.gui.templates.DescriptionDTO;
import za.co.liberty.dto.userprofiles.RequestCategoryDTO;
import za.co.liberty.dto.userprofiles.TeamDTO;
import za.co.liberty.interfaces.agreements.AgreementKindType;
import za.co.liberty.interfaces.agreements.requests.RequestDateType;
import za.co.liberty.interfaces.agreements.requests.RequestKindType;
import za.co.liberty.interfaces.agreements.requests.RequestStatusType;

/**
 * Data model for the RequestEnquiry screen.
 * 
 * @author JZB0608 - 01 Dec 2009
 *
 */
public class RequestEnquiryModel implements Serializable {

	private static final long serialVersionUID = -6597792486792356426L;
	
	private List<RequestCategoryDTO> allRequestCategoryList;
	private List<TeamDTO> allTeamList;
	private List<RequestKindType> allRequestKindTypeList;
	private List<AgreementKindType> allAgreementKindTypeList;
	private List<RequestStatusType> allRequestStatusTypeList;
	private List<RequestEnquiryPeriodDTO> allPeriodList;
	private List<RequestDateType> allRequestDateType;
	
	private Set<RequestKindType> allPropertyOnlyRequestKindSet;
	
	private Map<Class, RequestEnquiryDTO> dataModelMap = new HashMap<Class, RequestEnquiryDTO>();
	
	private List<CommissionKindsDTO> allCommissionKinds;	
	private List<DescriptionDTO> allProductReferences;
	private List<DescriptionDTO> allContributionIncIndicators;
	
	private List<ProductCodeDTO> allProductCodes;
	private List<FundCodeDTO> allFundCodes;
	
	public RequestEnquiryDTO getDataModel(Class type) {
		return dataModelMap.get(type);
	}
	public void setDataModel(RequestEnquiryDTO dataModel, Class type) {
		dataModelMap.put(type, dataModel);
	}
	public List<AgreementKindType> getAllAgreementKindTypeList() {
		return allAgreementKindTypeList;
	}
	public void setAllAgreementKindTypeList(
			List<AgreementKindType> allAgreementKindTypeList) {
		this.allAgreementKindTypeList = allAgreementKindTypeList;
	}
	public List<RequestCategoryDTO> getAllRequestCategoryList() {
		return allRequestCategoryList;
	}
	public void setAllRequestCategoryList(
			List<RequestCategoryDTO> allRequestCategoryList) {
		this.allRequestCategoryList = allRequestCategoryList;
	}
	public List<RequestKindType> getAllRequestKindTypeList() {
		return allRequestKindTypeList;
	}
	public void setAllRequestKindTypeList(
			List<RequestKindType> allRequestKindTypeList) {
		this.allRequestKindTypeList = allRequestKindTypeList;
	}
	public List<TeamDTO> getAllTeamList() {
		return allTeamList;
	}
	public void setAllTeamList(List<TeamDTO> allTeamList) {
		this.allTeamList = allTeamList;
	}
	public List<RequestStatusType> getAllRequestStatusTypeList() {
		return allRequestStatusTypeList;
	}
	public void setAllRequestStatusTypeList(
			List<RequestStatusType> allRequestStatusTypeList) {
		this.allRequestStatusTypeList = allRequestStatusTypeList;
	}
	public List<RequestEnquiryPeriodDTO> getAllPeriodList() {
		return allPeriodList;
	}
	public void setAllPeriodList(List<RequestEnquiryPeriodDTO> allPeriodList) {
		this.allPeriodList = allPeriodList;
	}
	public List<RequestDateType> getAllRequestDateType() {
		return allRequestDateType;
	}
	public void setAllRequestDateType(List<RequestDateType> allRequestDateType) {
		this.allRequestDateType = allRequestDateType;
	}
	public Set<RequestKindType> getAllPropertyOnlyRequestKindSet() {
		return allPropertyOnlyRequestKindSet;
	}
	public void setAllPropertyOnlyRequestKindSet(
			Set<RequestKindType> allPropertyOnlyRequestKindSet) {
		this.allPropertyOnlyRequestKindSet = allPropertyOnlyRequestKindSet;
	}
	public List<CommissionKindsDTO> getAllCommissionKinds() {
		return allCommissionKinds;
	}
	public void setAllCommissionKinds(List<CommissionKindsDTO> allCommissionKinds) {
		this.allCommissionKinds = allCommissionKinds;
	}
	public List<DescriptionDTO> getAllProductReferences() {
		return allProductReferences;
	}
	public void setAllProductReferences(List<DescriptionDTO> allProductReferences) {
		this.allProductReferences = allProductReferences;
	}
	public List<DescriptionDTO> getAllContributionIncIndicators() {
		return allContributionIncIndicators;
	}
	public void setAllContributionIncIndicators(List<DescriptionDTO> allContributionIncIndicators) {
		this.allContributionIncIndicators = allContributionIncIndicators;
	}
	public List<ProductCodeDTO> getAllProductCodes() {
		return allProductCodes;
	}
	public void setAllProductCodes(List<ProductCodeDTO> allProductCodes) {
		this.allProductCodes = allProductCodes;
	}
	public List<FundCodeDTO> getAllFundCodes() {
		return allFundCodes;
	}
	public void setAllFundCodes(List<FundCodeDTO> allFundCodes) {
		this.allFundCodes = allFundCodes;
	}

}
