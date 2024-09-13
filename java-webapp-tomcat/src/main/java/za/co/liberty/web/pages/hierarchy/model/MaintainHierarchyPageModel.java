package za.co.liberty.web.pages.hierarchy.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.naming.NamingException;

import za.co.liberty.business.guicontrollers.hierarchy.IHierarchyGUIController;
import za.co.liberty.dto.common.IDValueDTO;
import za.co.liberty.dto.databaseenum.BranchCategoryEnumDTO;
import za.co.liberty.dto.databaseenum.CostCenterDBEnumDTO;
import za.co.liberty.dto.databaseenum.DatabaseEnumDTO;
import za.co.liberty.dto.databaseenum.OrganisationExternalTypeEnumDTO;
import za.co.liberty.dto.party.HierarchyNodeDTO;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.helpers.lookup.ServiceLocator;
import za.co.liberty.interfaces.party.IPartyNameAndIdFLO;
import za.co.liberty.srs.type.SRSType;
import za.co.liberty.web.data.pages.IModalMaintenancePageModel;
import za.co.liberty.web.data.pages.ITabbedPageModel;
import za.co.liberty.web.system.EJBReferences;
import za.co.liberty.web.system.SRSAuthWebSession;

public class MaintainHierarchyPageModel implements ITabbedPageModel<HierarchyNodeDTO>,
		IModalMaintenancePageModel<HierarchyNodeDTO>,
		Serializable , Cloneable {
	
	private transient IHierarchyGUIController hierarchyMaintenanceController;
	
	public Object clone() {		
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {			
			e.printStackTrace();
		}
		return null;
	}

	private static final long serialVersionUID = 1643792587546952282L;	

	private String uacfID;
	
	private HierarchyNodeDTO partyDTO;
	
	private HierarchyNodeDTO selectedNode;	
	
	/* Keeps a before image of the DTO */
	private HierarchyNodeDTO partyDTOBeforeImage;
	
	/* Keeps a before image of the DTO */
	private HierarchyNodeDTO lbfNDPPartyDTO;
	
	private int currentTab = -1;	
	
	/**
	 * Below used as we use db enum objects in a list and in the object but users can type in a string
	 */
	private String costCenterSelection;	
	
	/*
	 * Dropdown lists
	 */
	private List<IDValueDTO> hierarchyTypeList;
	
	private List<IPartyNameAndIdFLO> hierarchyChannelList;
	
	private List<IDValueDTO> employeeTypeList;
	
	private List<CostCenterDBEnumDTO> costCenters;
	private Class currentTabClass;
	
	//panel model for the bulk transfer process
	private BulkTransferPanelModel bulkTransferPanelModel;

	private List<OrganisationExternalTypeEnumDTO> organisationTypes;
	
	private List<BranchCategoryEnumDTO>  branchCategories;
	
	private boolean modalSuccess;

	
	public BulkTransferPanelModel getBulkTransferPanelModel() {
		return bulkTransferPanelModel;
	}


	public void setBulkTransferPanelModel(
			BulkTransferPanelModel bulkTransferPanelModel) {
		this.bulkTransferPanelModel = bulkTransferPanelModel;
	}


	public Class getCurrentTabClass() {		
		return currentTabClass;
	}


	public void setCurrentTabClass(Class currentTabClass) {
		this.currentTabClass = currentTabClass;		
	}
	
	
		
	public List<CostCenterDBEnumDTO> getCostCenters() {
		return costCenters;
	}
	
	public void setCostCenters(List<CostCenterDBEnumDTO> costCenters) {
		this.costCenters = costCenters;
	}

	public List<IDValueDTO> getEmployeeTypeList() {
		return employeeTypeList;
	}
	
	public List<IDValueDTO> getAddBranchTypeList() {
		List<IDValueDTO> addBranchTypes = new ArrayList<IDValueDTO>();
		IDValueDTO idvalType = new IDValueDTO();
		idvalType.setName("Addtional Branch of");
		idvalType.setOid(SRSType.ADDITIONALBRANCHOF);
		addBranchTypes.add(idvalType);
		return addBranchTypes;
	}

	public void setEmployeeTypeList(List<IDValueDTO> employeeTypeList) {
		this.employeeTypeList = employeeTypeList;
	}

	public List<IPartyNameAndIdFLO> getHierarchyChannelList() {
		return hierarchyChannelList;
	}

	public void setHierarchyChannelList(
			List<IPartyNameAndIdFLO> hierarchyChannelList) {
		this.hierarchyChannelList = hierarchyChannelList;
	}

	public int getCurrentTab() {
		return currentTab;
	}

	public void setCurrentTab(int currentTab) {
		this.currentTab = currentTab;
	}

	public HierarchyNodeDTO getSelectedItem() {		
		return selectedNode;
	}

	public List<HierarchyNodeDTO> getSelectionList() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setSelectedItem(HierarchyNodeDTO selected) {
		selectedNode = selected;	
	}

	public void setSelectionList(List<HierarchyNodeDTO> selectionList) {
		// TODO Auto-generated method stub
		
	}

	public HierarchyNodeDTO getHierarchyNodeDTO() {
		return partyDTO;
	}

	public void setHierarchyNodeDTO(HierarchyNodeDTO partyDTO) {
		this.partyDTO = partyDTO;
	}	

	public HierarchyNodeDTO getPartyDTOBeforeImage() {
		return partyDTOBeforeImage;
	}

	public HierarchyNodeDTO getLbfNDPPartyDTO() {
		return lbfNDPPartyDTO;
	}


	public void setLbfNDPPartyDTO(HierarchyNodeDTO lbfNDPPartyDTO) {
		this.lbfNDPPartyDTO = lbfNDPPartyDTO;
	}


	public void setPartyDTOBeforeImage(HierarchyNodeDTO partyDTOBeforeImage) {
		this.partyDTOBeforeImage = partyDTOBeforeImage;
	}

	public String getUacfID() {
		return uacfID;
	}

	public void setUacfID(String uacfID) {
		this.uacfID = uacfID;
	}

	public List<IDValueDTO> getHierarchyTypeList() {
		return hierarchyTypeList;
	}

	public void setHierarchyTypeList(List<IDValueDTO> hierarchyTypeList) {
		this.hierarchyTypeList = hierarchyTypeList;
	}
	
	/**
	 * loop through array and return back IDValueDTO maching given id
	 * @param type
	 * @return
	 */
	public IDValueDTO getTypeFromList(long typeid){
		if(this.getHierarchyTypeList() != null){
			for(IDValueDTO val : this.getHierarchyTypeList()){
				if(val.getOid() == typeid){
					return val;
				}
			}
		}
		return null;
	}	
	
	/**
	 * get an instance of IHierarchyGUIController
	 * @return
	 */
	public IHierarchyGUIController getHierarchyGUIController() {
		if(hierarchyMaintenanceController == null){
			try {
				hierarchyMaintenanceController = ServiceLocator.lookupService(IHierarchyGUIController.class);
			} catch (NamingException e) {
				throw new CommunicationException(e);
			}
				
		}
		return hierarchyMaintenanceController;
	}

	/**
	 * Get the cost center name
	 * @return
	 */
	public String getCostCenterSelection() {
		if(this.getHierarchyNodeDTO() != null && this.getHierarchyNodeDTO().getCostCenter() != null && this.getHierarchyNodeDTO().getCostCenter().getKeyInt() != 0){
			costCenterSelection = this.getHierarchyNodeDTO().getCostCenter().getName();
		}
		return costCenterSelection;
	}

	/**
	 * Set the cost center selection
	 * @param costCenterSelection
	 */
	public void setCostCenterSelection(String costCenterSelection) {
		if(this.getHierarchyNodeDTO() != null){
			if(costCenterSelection != null && !costCenterSelection.equals("")){
				//update page dto
				if(this.getCostCenters() != null){
					boolean found = false;
					for(DatabaseEnumDTO dbEnum : this.getCostCenters()){
						if(dbEnum.getName().equalsIgnoreCase(costCenterSelection)){
							this.getHierarchyNodeDTO().setCostCenter((CostCenterDBEnumDTO) dbEnum);
							found = true;
							break;
						}
					}
					if(!found){
						//add new one 
						try {
							CostCenterDBEnumDTO center = new CostCenterDBEnumDTO(costCenterSelection);
							this.getHierarchyNodeDTO().setCostCenter(center);
						} catch (Exception e) {
							e.printStackTrace();
							throw new InstantiationError("Could not create new cost center, refer to logs for more details");
						}
					}
				}
			}else{
				this.getHierarchyNodeDTO().setCostCenter(null);
			}
		}
		this.costCenterSelection = costCenterSelection;
	}


	public List<OrganisationExternalTypeEnumDTO> getOrganisationTypeList() {
		
		return this.organisationTypes;
	}
	
	
	
	public void setOrganisationTypeList(List<OrganisationExternalTypeEnumDTO> organisationTypes) {
		OrganisationExternalTypeEnumDTO externalTypeEnumDTO = null;
		for (OrganisationExternalTypeEnumDTO enumDTO : organisationTypes) {
			if(enumDTO.getKeyInt() == 0){
				externalTypeEnumDTO = enumDTO;
				break;
			}
		}
		
		this.organisationTypes = organisationTypes;
		this.organisationTypes.remove(externalTypeEnumDTO);
	}


	public List<BranchCategoryEnumDTO> getBranchCategories() {
		return branchCategories;
	}


	public void setBranchCategories(List<BranchCategoryEnumDTO> branchCategories) {
		BranchCategoryEnumDTO branchCategoryEnumDTO = null;
		for (BranchCategoryEnumDTO enumDTO : branchCategories) {
			if(enumDTO.getKeyInt() == 0){
				branchCategoryEnumDTO = enumDTO;
				break;
			}
		}
		
		
		
		this.branchCategories = branchCategories;
		this.branchCategories.remove(branchCategoryEnumDTO);
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
	}
}
