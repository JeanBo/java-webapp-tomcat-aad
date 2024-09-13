package za.co.liberty.web.pages.salesBCLinking.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import za.co.liberty.dto.agreement.SalesBCLinking.BranchDetailsDTO;
import za.co.liberty.dto.agreement.SalesBCLinking.ServicingPanelDTO;
import za.co.liberty.dto.agreement.SalesBCLinking.ServicingTypeDTO;
import za.co.liberty.web.data.pages.IMaintenancePageModel;

/**
 * PageModel used for Add and Maintain agreement.
 */
public class SalesBCLinkingPageModel implements
		IMaintenancePageModel<ServicingPanelDTO>, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<BranchDetailsDTO> branchDetails;
	private List<ServicingTypeDTO> servicingTypes;
	private List<ServicingPanelDTO> servicingPanels;
	private List<ServicingPanelDTO> displayServicingPanels;
	private BranchDetailsDTO selectedBranch;
	private ServicingTypeDTO selectedServiceType;
	private ServicingPanelDTO selectedServicingPanel;
	private SalesBCLinkingPanelModel salesBCLinkingPanelModel;
	//private Boolean isRequestRaised;


	public int getCurrentTab() {
		return 0;
	}

	public void setCurrentTab(int currentTab) {

	}

	public void setSelectedItem(ServicingPanelDTO selected) {
		this.selectedServicingPanel = selected;
	}

	public List<ServicingTypeDTO> getServicingTypes() {
		return (servicingTypes == null ? new ArrayList<ServicingTypeDTO>()
				: servicingTypes);

	}

	public void setServicingTypes(List<ServicingTypeDTO> servicingTypes) {
		this.servicingTypes = servicingTypes;

	}

	public List<BranchDetailsDTO> getAccessibleBranchDetails() {
		return (branchDetails == null ? new ArrayList<BranchDetailsDTO>()
				: branchDetails);
	}

	public void setAccessibleBranchDetails(List<BranchDetailsDTO> branchNames) {
		this.branchDetails = branchNames;
	}

	public List<ServicingPanelDTO> getServicingPanels() {
		return (servicingPanels == null ? new ArrayList<ServicingPanelDTO>()
				: servicingPanels);

	}

	public void setServicingPanels(List<ServicingPanelDTO> servicingPanels) {
		this.servicingPanels = servicingPanels;
	}

	public List<ServicingPanelDTO> getDisplayServicingPanels() {
		return (displayServicingPanels == null ? new ArrayList<ServicingPanelDTO>()
				: displayServicingPanels);

	}

	public void setDisplayServicingPanels(
			List<ServicingPanelDTO> displayServicingPanels) {
		this.displayServicingPanels = displayServicingPanels;
	}

	public List<ServicingPanelDTO> getSelectionList() {
		return servicingPanels;
	}

	public BranchDetailsDTO getSelectedBranch() {
		return selectedBranch;
	}

	public void setSelectedBranch(BranchDetailsDTO selectedBranch) {
		this.selectedBranch = selectedBranch;
	}

	public ServicingTypeDTO getSelectedServiceType() {
		return selectedServiceType;
	}

	public void setSelectedServiceType(ServicingTypeDTO selectedServiceType) {
		this.selectedServiceType = selectedServiceType;
	}

	public ServicingPanelDTO getSelectedServicingPanel() {
		return selectedServicingPanel;
	}

	public void setSelectedServicingPanel(
			ServicingPanelDTO selectedServicingPanel) {
		this.selectedServicingPanel = selectedServicingPanel;
	}

	public List<BranchDetailsDTO> getBranchDetails() {
		return branchDetails;
	}

	public void setBranchDetails(List<BranchDetailsDTO> branchDetails) {
		this.branchDetails = branchDetails;
	}


	public ServicingPanelDTO getSelectedItem() {
		return selectedServicingPanel;
	}

	public void setSelectionList(List<ServicingPanelDTO> selectionList) {
				
	}

	public SalesBCLinkingPanelModel getSalesBCLinkingPanelModel() {
		return salesBCLinkingPanelModel;
	}

	public void setSalesBCLinkingPanelModel(
			SalesBCLinkingPanelModel salesBCLinkingPanelModel) {
		this.salesBCLinkingPanelModel = salesBCLinkingPanelModel;
	}

//	public Boolean isRequestRaised() {
//		return isRequestRaised;
//	}
//
//	public void setRequestRaised(Boolean isRequestRaised) {
//		this.isRequestRaised = isRequestRaised;
//	}	
}
