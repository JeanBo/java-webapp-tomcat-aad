/**
 * 
 */
package za.co.liberty.web.pages.agreementprivilege.model;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import za.co.liberty.dto.agreementprivileges.AgreementPrivilegesDataDTO;
import za.co.liberty.dto.party.PersonDTO;
import za.co.liberty.web.data.pages.ITabbedPageModel;

/**
 * A model to keep all page variables
 * 
 * @author dzs2610
 *
 */
public class AgreementLinkingPageModel implements ITabbedPageModel<AgreementPrivilegesDataDTO>,
		Serializable {
	
	private static final long serialVersionUID = 1643792587546952282L;	

	private List<AgreementPrivilegesDataDTO> selectionList;
	
	private AgreementPrivilegesDataDTO selectedItem;
	
	private int currentTab = -1;
	
	private long agreementNo;
	private String uacfId;	
	private long partyOid;	
	private List<AgreementPrivilegesDataDTO> ownAgreementList;	
	private List<AgreementPrivilegesDataDTO> reportToAgreements;
	private List<AgreementPrivilegesDataDTO> explicitAgreements;
	private List<AgreementPrivilegesDataDTO> userGrantedAgreements;	
	private Set<PersonDTO> personDTOList;
	private List<AgreementPrivilegesDataDTO> accessGrantedOwnAgreementList;	
	private Class currentTabClass;
	
	public Class getCurrentTabClass() {		
		return currentTabClass;
	}


	public void setCurrentTabClass(Class currentTabClass) {
		this.currentTabClass = currentTabClass;		
	}

	public AgreementPrivilegesDataDTO getSelectedItem() {		
		return selectedItem;
	}

	public List<AgreementPrivilegesDataDTO> getSelectionList() {		
		return selectionList;
	}

	public void setSelectedItem(AgreementPrivilegesDataDTO selected) {
		this.selectedItem = selected;
	}

	public void setSelectionList(List<AgreementPrivilegesDataDTO> selectionList) {
		this.selectionList = selectionList;		
	}	

	public int getCurrentTab() {
		return currentTab;
	}

	public void setCurrentTab(int currentTab) {
		this.currentTab = currentTab;
	}

	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	public List<AgreementPrivilegesDataDTO> getAcessGrantedOwnAgreementList() {
		return accessGrantedOwnAgreementList;
	}

	public void setAcessGrantedOwnAgreementList(
			List<AgreementPrivilegesDataDTO> acessGrantedOwnAgreementList) {
		accessGrantedOwnAgreementList = acessGrantedOwnAgreementList;
	}

	public long getAgreementNo() {
		return agreementNo;
	}

	public void setAgreementNo(long agreementNo) {
		this.agreementNo = agreementNo;
	}	

	public List<AgreementPrivilegesDataDTO> getExplicitAgreements() {
		return explicitAgreements;
	}

	public void setExplicitAgreements(
			List<AgreementPrivilegesDataDTO> explicitAgreements) {
		this.explicitAgreements = explicitAgreements;
	}	

	public List<AgreementPrivilegesDataDTO> getOwnAgreementList() {
		return ownAgreementList;
	}

	public void setOwnAgreementList(
			List<AgreementPrivilegesDataDTO> ownAgreementList) {
		this.ownAgreementList = ownAgreementList;
	}

	public long getPartyOid() {
		return partyOid;
	}

	public void setPartyOid(long partyOid) {
		this.partyOid = partyOid;
	}

	public Set<PersonDTO> getPersonDTOList() {
		return personDTOList;
	}

	public void setPersonDTOList(Set<PersonDTO> personDTOList) {
		this.personDTOList = personDTOList;
	}

	public List<AgreementPrivilegesDataDTO> getReportToAgreements() {
		return reportToAgreements;
	}

	public void setReportToAgreements(
			List<AgreementPrivilegesDataDTO> reportToAgreements) {
		this.reportToAgreements = reportToAgreements;
	}

	public String getUacfId() {
		return uacfId;
	}

	public void setUacfId(String uacfId) {
		this.uacfId = uacfId;
	}

	public List<AgreementPrivilegesDataDTO> getUserGrantedAgreements() {
		return userGrantedAgreements;
	}

	public void setUserGrantedAgreements(
			List<AgreementPrivilegesDataDTO> userGrantedAgreements) {
		this.userGrantedAgreements = userGrantedAgreements;
	}
	
}
