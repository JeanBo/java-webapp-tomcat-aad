package za.co.liberty.web.pages.core.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import za.co.liberty.dto.agreement.core.CoreConsultantDto;
import za.co.liberty.dto.agreement.core.CoreTransferDto;
import za.co.liberty.interfaces.core.CoreTransferRequestType;
import za.co.liberty.web.data.pages.IMaintenancePageModel;
import za.co.liberty.web.data.pages.IModalMaintenancePageModel;

/**
 * Page model for Core Transfer Page
 *
 */
public class CoreTransferPageModel  implements IMaintenancePageModel, Serializable, IModalMaintenancePageModel{
	
	private static final long serialVersionUID = 1L;
	
	private List<CoreTransferDto> coreTransferDto;
	private List<CoreTransferDto> segTransferDto;
	private List<CoreTransferRequestType> coreTransferRequestType;
	private CoreTransferRequestType requestCategory;
	private String transferType;
	private String requester;
	private String authorizer;
	
	private boolean isModalWizardSucces = false;
	private String wizardMessage;
	
	private Map<Long, CoreConsultantDto> consultantMap; 
	
	public CoreTransferPageModel() {
		consultantMap = new HashMap<Long, CoreConsultantDto>(); 
	}
	
	public List<CoreTransferDto> getSegTransferDto() {
		return segTransferDto;
	}

	public void setSegTransferDto(List<CoreTransferDto> segTransferDto) {
		this.segTransferDto = segTransferDto;
	}

	public List<CoreTransferDto> getCoreTransferDto() {
		return coreTransferDto;
	}

	public void setCoreTransferDto(List<CoreTransferDto> coreTransferDto) {
		this.coreTransferDto = coreTransferDto;
	}

	public CoreTransferRequestType getRequestCategory() {
		return requestCategory;
	}

	public void setRequestCategory(CoreTransferRequestType requestCategory) {
		this.requestCategory = requestCategory;
	}

	public List<CoreTransferRequestType> getRequestCategoryDTO() {
		return coreTransferRequestType;
	}

	public void setRequestCategoryDTO(List<CoreTransferRequestType> requestCategoryDTO) {
		this.coreTransferRequestType = requestCategoryDTO;
	}

	public Object getSelectedItem() {
		// TODO Auto-generated method stub
		return null;
	}

	public List getSelectionList() {
		return coreTransferDto;
	}

	public void setSelectedItem(Object selected) {
		
	}

	public void setSelectionList(List object) {
		this.coreTransferDto=(List<CoreTransferDto>)object;
		
	}

	public List<CoreTransferRequestType> getCoreTransferRequestType() {
		return coreTransferRequestType;
	}

	public void setCoreTransferRequestType(
			List<CoreTransferRequestType> coreTransferRequestType) {
		this.coreTransferRequestType = coreTransferRequestType;
	}

	public String getTransferType() {
		return transferType;
	}

	public void setTransferType(String transferType) {
		this.transferType = transferType;
	}
	
	public String getAuthorizer() {
		return authorizer;
	}
	public void setAuthorizer(String authorizer) {
		this.authorizer = authorizer;
	}
	public String getRequester() {
		return requester;
	}
	public void setRequester(String requester) {
		this.requester = requester;
	}

	public Map<Long, CoreConsultantDto> getConsultantMap() {
		return consultantMap;
	}

	public void setConsultantMap(Map<Long, CoreConsultantDto> consultantMap) {
		this.consultantMap = consultantMap;
	}

	@Override
	public boolean isModalWizardSucces() {
		return isModalWizardSucces;
	}

	@Override
	public void setModalWizardSuccess(boolean success) {
		isModalWizardSucces = success;
	}

	@Override
	public String getModalWizardMessage() {
		return wizardMessage;
	}
	
	public void setModalWizardMessage(String message) {
		wizardMessage = message;
	}
}
