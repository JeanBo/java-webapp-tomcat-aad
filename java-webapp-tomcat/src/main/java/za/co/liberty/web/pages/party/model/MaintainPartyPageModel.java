package za.co.liberty.web.pages.party.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.naming.NamingException;

import za.co.liberty.business.guicontrollers.partymaintenance.IPartyMaintenanceController;
import za.co.liberty.business.party.IPartyManagement;
import za.co.liberty.dto.agreement.AgreementRoleDTO;
import za.co.liberty.dto.party.PartyDTO;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.helpers.lookup.ServiceLocator;
import za.co.liberty.interfaces.party.BankingDetailType;
import za.co.liberty.web.data.pages.IModalMaintenancePageModel;
import za.co.liberty.web.data.pages.ITabbedPageModel;
import za.co.liberty.web.pages.taxdetails.model.TaxDetailsPanelModel;

/**
 * Pagemodel for party maintenance
 * 
 * @author dzs2610
 *
 */
public class MaintainPartyPageModel implements ITabbedPageModel<PartyDTO>,
		IModalMaintenancePageModel<PartyDTO>,
		Serializable, Cloneable {

	private static final long serialVersionUID = 1643792587546952282L;

	private String uacfID;

	private PartyDTO partyDTO;

	private TaxDetailsPanelModel taxDetailsPanelModel;

	private MaintainPartyHierarchyPanelModel maintainPartyHierarchyPanelModel;

	private OtherLinkedDetailsPanelModel otherLinkedDetailsPanelModel;

	private MedicalAidDetailsPanelModel medicalAidDetailsPanelModel;

	private ProvidentFundBeneficiariesModel providentFundBeneficiariesModel;

	private BankingDetailsPanelModel bankingDetailsPanelModel;

	private Long selectedAgreementNr;
	
	private boolean warningOnlyOnAVSCall;
	
	/**
	 * The origional DTO before any changes occur
	 */
	private PartyDTO partyBeforeImage;

	private int currentTab = -1;
	@SuppressWarnings("unchecked")
	private Class currentTabClass;

	private List<AgreementRoleDTO> paysToAgreementList;

	private BankingDetailType bankingDetailType;
	
	private boolean modalSuccess;
	
	

	public BankingDetailType getBankingDetailType() {
		return bankingDetailType;
	}

	public void setBankingDetailType(BankingDetailType bankingDetailType) {
		this.bankingDetailType = bankingDetailType;
	}

	public MaintainPartyPageModel() {
		paysToAgreementList = new ArrayList<AgreementRoleDTO>();
		setWarningOnlyOnAVSCall(false); 
	}

	@SuppressWarnings("unchecked")
	public Class getCurrentTabClass() {
		return currentTabClass;
	}

	@SuppressWarnings("unchecked")
	public void setCurrentTabClass(Class currentTabClass) {
		this.currentTabClass = currentTabClass;
	}

	public int getCurrentTab() {
		return currentTab;
	}

	public void setCurrentTab(int currentTab) {
		this.currentTab = currentTab;
	}

	public PartyDTO getSelectedItem() {
		return partyDTO;
	}

	public List<PartyDTO> getSelectionList() {
		return null;
	}

	public void setSelectedItem(PartyDTO selected) {
		// TODO Auto-generated method stub

	}

	public void setSelectionList(List<PartyDTO> selectionList) {
		// TODO Auto-generated method stub

	}

	public PartyDTO getPartyDTO() {
		return partyDTO;
	}

	public void setPartyDTO(PartyDTO partyDTO) {
		this.partyDTO = partyDTO;
	}

	public String getUacfID() {
		return uacfID;
	}

	public void setUacfID(String uacfID) {
		this.uacfID = uacfID;
	}

	/**
	 * get an instance of IPartyMaintenanceController
	 * 
	 * @return
	 */
	public static IPartyMaintenanceController getPartyMaintenanceController() {
		try {
			return ServiceLocator.lookupService(IPartyMaintenanceController.class);
		} catch (NamingException e) {
			throw new CommunicationException(e);
		}

	}

	/**
	 * get an instance of IPartyManagement
	 * 
	 * @return
	 */
	public static IPartyManagement getPartyManagement() {
		try {
			return ServiceLocator.lookupService(IPartyManagement.class);
		} catch (NamingException e) {
			throw new CommunicationException(e);
		}

	}

	public PartyDTO getPartyBeforeImage() {
		return partyBeforeImage;
	}

	public void setPartyBeforeImage(PartyDTO partyBeforeImage) {
		this.partyBeforeImage = partyBeforeImage;
	}

	public BankingDetailsPanelModel getBankingDetailsPanelModel() {
		return bankingDetailsPanelModel;
	}

	public void setTaxDetailsPanelModel(BankingDetailsPanelModel bankingDetailsPanelModel) {
		this.bankingDetailsPanelModel = bankingDetailsPanelModel;
	}

	public MaintainPartyHierarchyPanelModel getMaintainPartyHierarchyPanelModel() {
		return maintainPartyHierarchyPanelModel;
	}

	public void setMaintainPartyHierarchyPanelModel(MaintainPartyHierarchyPanelModel maintainPartyHierarchyPanelModel) {
		this.maintainPartyHierarchyPanelModel = maintainPartyHierarchyPanelModel;
	}

	public List<AgreementRoleDTO> getPaysToAgreementList() {
		return paysToAgreementList;
	}

	public void setPaysToAgreementList(List<AgreementRoleDTO> paysToAgreementList) {
		this.paysToAgreementList = paysToAgreementList;
	}

	public void setSelectedAgreementNr(Long selectedAgreementNr) {
		this.selectedAgreementNr = selectedAgreementNr;
	}

	public Long getSelectedAgreementNr() {
		return selectedAgreementNr;
	}

	public TaxDetailsPanelModel getTaxDetailsPanelModel() {
		return taxDetailsPanelModel;
	}

	public void setTaxDetailsPanelModel(TaxDetailsPanelModel taxDetailsPanelModel) {
		this.taxDetailsPanelModel = taxDetailsPanelModel;
	}

	/**
	 * Return true if the party has any agreements with active pays to roles.
	 * 
	 * @return
	 */
	public boolean hasAgreementsWithPaysToRoles() {
		return (getPaysToAgreementList().size() > 0);

	}

	/**
	 * Return true if selected agreement has an active paysToRole
	 * 
	 * @return
	 */
	public boolean isSelectedAgreementHasPaysTo() {

		if (getSelectedAgreementNr() == null) {
			return false;
		}

		for (AgreementRoleDTO dto : getPaysToAgreementList()) {
			if (dto.getAgreementNumber().equals(getSelectedAgreementNr())) {
				return true;
			}
		}

		return false;

	}

	public MedicalAidDetailsPanelModel getMedicalAidDetailsPanelModel() {
		return medicalAidDetailsPanelModel;
	}

	public void setMedicalAidDetailsPanelModel(MedicalAidDetailsPanelModel medicalAidDetailsPanelModel) {
		this.medicalAidDetailsPanelModel = medicalAidDetailsPanelModel;
	}

	public OtherLinkedDetailsPanelModel getOtherLinkedDetailsPanelModel() {
		return otherLinkedDetailsPanelModel;
	}

	public void setOtherLinkedDetailsPanelModel(OtherLinkedDetailsPanelModel otherLinkedDetailsPanelModel) {
		this.otherLinkedDetailsPanelModel = otherLinkedDetailsPanelModel;
	}

	public ProvidentFundBeneficiariesModel getProvidentFundBeneficiariesModel() {
		return providentFundBeneficiariesModel;
	}

	public void setProvidentFundBeneficiariesModel(ProvidentFundBeneficiariesModel providentFundBeneficiariesModel) {
		this.providentFundBeneficiariesModel = providentFundBeneficiariesModel;
	}



	@Override
	public boolean isModalWizardSucces() {
		return modalSuccess;
	}

	@Override
	public void setModalWizardSuccess(boolean success) {
		modalSuccess = success;
	}


	@Override
	public String getModalWizardMessage() {
		return null;
	}public boolean isWarningOnlyOnAVSCall() {
		return warningOnlyOnAVSCall;
	}

	public void setWarningOnlyOnAVSCall(boolean warningOnlyOnAVSCall) {
		this.warningOnlyOnAVSCall = warningOnlyOnAVSCall;
	}




}
