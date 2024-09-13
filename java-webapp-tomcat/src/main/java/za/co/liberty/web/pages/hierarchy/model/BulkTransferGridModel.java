package za.co.liberty.web.pages.hierarchy.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import za.co.liberty.dto.agreement.AgreementHomeRoleDTO;
import za.co.liberty.dto.agreement.SimpleAgreementDetailDTO;
import za.co.liberty.interfaces.agreements.requests.RequestKindType;

/**
 * grid model for the bulk agreement transfer panel
 * @author dzs2610
 *
 */
public class BulkTransferGridModel implements Serializable{
	private static final long serialVersionUID = 1L;

	private AgreementHomeRoleDTO homeRole;
	
	private AgreementHomeRoleDTO origImage;
	
	private SimpleAgreementDetailDTO agreementDetails;		
	
	private List<RequestKindType> outstandingRequestkinds = new ArrayList<RequestKindType>();	

	public List<RequestKindType> getOutstandingRequestkinds() {
		return Collections.unmodifiableList(outstandingRequestkinds);
	}	

	public SimpleAgreementDetailDTO getAgreementDetails() {
		return agreementDetails;
	}

	public void setAgreementDetails(SimpleAgreementDetailDTO agreementDetails) {
		this.agreementDetails = agreementDetails;
	}

	public AgreementHomeRoleDTO getHomeRole() {
		return homeRole;
	}

	public AgreementHomeRoleDTO getOrigImage() {
		return origImage;
	}

	public void setOrigImage(AgreementHomeRoleDTO origImage) {
		this.origImage = origImage;
	}

	public void setHomeRole(AgreementHomeRoleDTO homeRole) {
		this.homeRole = homeRole;
	}	
	
	public void addOutstandingRequestkind(RequestKindType requestKind){
		outstandingRequestkinds.add(requestKind);
	}
}
