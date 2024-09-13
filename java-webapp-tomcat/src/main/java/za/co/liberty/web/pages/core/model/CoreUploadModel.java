package za.co.liberty.web.pages.core.model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import za.co.liberty.dto.agreement.core.CoreConsultantDto;
import za.co.liberty.dto.agreement.core.CoreTransferDto;

public class CoreUploadModel implements Serializable {
	private static final long serialVersionUID = 1L;
	private List<CoreTransferDto> coreTransferDto;
	private Map<Long, CoreConsultantDto> consultantMap; 
	public List<CoreTransferDto> getCoreTransferDto() {
		return coreTransferDto;
	}

	public void setCoreTransferDto(List<CoreTransferDto> coreTransferDto) {
		this.coreTransferDto = coreTransferDto;
	}

	public Map<Long, CoreConsultantDto> getConsultantMap() {
		return consultantMap;
	}

	public void setConsultantMap(Map<Long, CoreConsultantDto> consultantMap) {
		this.consultantMap = consultantMap;
	}


}
