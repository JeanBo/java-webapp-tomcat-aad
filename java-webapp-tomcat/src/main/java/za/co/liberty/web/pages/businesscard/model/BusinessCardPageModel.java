package za.co.liberty.web.pages.businesscard.model;

import java.io.Serializable;
import java.util.List;

import org.apache.wicket.request.mapper.parameter.PageParameters;

import za.co.liberty.web.data.pages.ITabbedPageModel;

/**
 * Business card page model
 * @author DZS2610
 *
 */
public class BusinessCardPageModel implements ITabbedPageModel<Long>, Serializable{	
	
	private static final long serialVersionUID = 1L;
	private int currentTab;
	private Class currentTabClass;
	
	private Long partyOID;
	
	private Long agreementNumber;
	
	private MaintainBusinessCardPanelModel maintainBusinessCardPanelModel;
	
	private PageParameters passedInParams;

	public MaintainBusinessCardPanelModel getMaintainBusinessCardPanelModel() {
		return maintainBusinessCardPanelModel;
	}

	public void setMaintainBusinessCardPanelModel(
			MaintainBusinessCardPanelModel maintainBusinessCardPanelModel) {
		this.maintainBusinessCardPanelModel = maintainBusinessCardPanelModel;
	}

	public int getCurrentTab() {		
		return currentTab;
	}

	public Class getCurrentTabClass() {		
		return currentTabClass;
	}

	public void setCurrentTab(int currentTab) {
		this.currentTab = currentTab;	
	}

	public void setCurrentTabClass(Class currentTabClass) {
		this.currentTabClass = currentTabClass;
	}

	public Long getSelectedItem() {	
		return partyOID;
	}

	public List<Long> getSelectionList() {		
		return null;
	}

	public void setSelectedItem(Long selected) {		
		partyOID = selected;
	}

	public void setSelectionList(List<Long> selectionList) {
				
	}
	
	public Long getPartyOID() {
		return partyOID;
	}

	public void setPartyOID(Long partyOID) {
		this.partyOID = partyOID;
	}

	public Long getAgreementNumber() {
		return agreementNumber;
	}

	public void setAgreementNumber(Long agreementNumber) {
		this.agreementNumber = agreementNumber;
	}

	public PageParameters getPassedInParams() {
		return passedInParams;
	}

	public void setPassedInParams(PageParameters passedInParams) {
		this.passedInParams = passedInParams;
	}	
			
}
