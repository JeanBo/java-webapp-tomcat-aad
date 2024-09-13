package za.co.liberty.web.pages.admin.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import za.co.liberty.dto.databaseenum.OrganisationExternalTypeEnumDTO;
import za.co.liberty.dto.rating.BranchTypeDTO;
import za.co.liberty.dto.rating.DescriptionDTO;
import za.co.liberty.dto.rating.HierarchyAddressClassificationDTO;
import za.co.liberty.dto.rating.HierarchyNodeCharacteristicsDTO;
import za.co.liberty.dto.rating.MIRatingTableNameDTO;
import za.co.liberty.interfaces.agreements.AgreementKindType;
import za.co.liberty.interfaces.agreements.RoleKindType;
import za.co.liberty.interfaces.rating.IGuiRatingRow;
import za.co.liberty.interfaces.salesCategory.SalesCategoryType;
import za.co.liberty.web.data.pages.IMaintenancePageModel;


public class RatingTablePageModel implements Serializable, IMaintenancePageModel<MIRatingTableNameDTO>  {

	private static final long serialVersionUID = 1L;
	
	private MIRatingTableNameDTO selectedItem;
	private List<MIRatingTableNameDTO> selectionList;
	
	private List<? extends IGuiRatingRow> rowList;
	private IGuiRatingRow selectionRow;
	
//	Lists for Additional Hierarchy Views Table ------------------------------------------------------------
	private List<AgreementKindType>allAgreementKinds = new ArrayList<AgreementKindType>();
	private List<OrganisationExternalTypeEnumDTO>allOrganisationExternalType = new ArrayList<OrganisationExternalTypeEnumDTO>();
	private List<BranchTypeDTO> allBranchTypes = new ArrayList<BranchTypeDTO>();
	private List<SalesCategoryType>allSalesCategories = new ArrayList<SalesCategoryType>();
	private List<RoleKindType>allRoleKinds= new ArrayList<RoleKindType>();
	private List<String> lowerLevels = new ArrayList<String>();
	private List<String>level = new ArrayList<String>();
	
//	Lists for Hierarchy Node Characteristics----------------------------------------------------------------
	private List<DescriptionDTO>allMIDivisions = new ArrayList<DescriptionDTO>();
	private List<DescriptionDTO>allChannelGroups = new ArrayList<DescriptionDTO>();
	private List<DescriptionDTO>allSuperDivisions = new ArrayList<DescriptionDTO>();
	private List<HierarchyNodeCharacteristicsDTO>hierarchyNodeLists = new ArrayList<HierarchyNodeCharacteristicsDTO>();
	
//	Lists for Hierarchy Node Address Classifications---------------------------------------------------------
	private List<HierarchyAddressClassificationDTO>hierarchyClassificationLists = new ArrayList<HierarchyAddressClassificationDTO>();
	private List<String>suburbs = new ArrayList<String>();
	private List<String>towns = new ArrayList<String>();
	private List<String>mainTowns = new ArrayList<String>();
	private List<String>provinces = new ArrayList<String>();
	private List<String>provinceCodes = new ArrayList<String>();
	private List<String>libertyAreas = new ArrayList<String>();
	private Integer metroIndicator = null;
	private IGuiRatingRow guiRatingRowBeforeImage;
	
	public Integer getMetroIndicator() {
		return metroIndicator;
	}
	public void setMetroIndicator(Integer metroIndicator) {
		this.metroIndicator = metroIndicator;
	}
	
	public MIRatingTableNameDTO getSelectedItem() {
		return selectedItem;
	}
	public void setSelectedItem(MIRatingTableNameDTO selectedItem) {
		this.selectedItem = selectedItem;
	}
	public List<MIRatingTableNameDTO> getSelectionList() {
		return selectionList;
	}
	public void setSelectionList(List<MIRatingTableNameDTO> selectionList) {
		this.selectionList = selectionList;
	}
	public List<? extends IGuiRatingRow> getRowList() {
		return rowList;
	}
	public void setRowList(List<? extends IGuiRatingRow> rowList) {
		this.rowList = rowList;
	}
	public IGuiRatingRow getSelectionRow() {
		return selectionRow;
	}
	public void setSelectionRow(IGuiRatingRow selectionRow) {
		this.selectionRow = selectionRow;
	}
	public List<AgreementKindType> getAllAgreementKinds() {
		return allAgreementKinds;
	}
	public void setAllAgreementKinds(List<AgreementKindType> allAgreementKinds) {
		this.allAgreementKinds = allAgreementKinds;
	}
	public List<SalesCategoryType> getAllSalesCategories() {
		return allSalesCategories;
	}
	public void setAllSalesCategories(List<SalesCategoryType> allSalesCategories) {
		this.allSalesCategories = allSalesCategories;
	}
	public List<RoleKindType> getAllRoleKinds() {
		return allRoleKinds;
	}
	public void setAllRoleKinds(List<RoleKindType> allRoleKinds) {
		this.allRoleKinds = allRoleKinds;
	}
	public List<String> getLowerLevels() {
		return lowerLevels;
	}
	public void setLowerLevels(List<String> lowerLevels) {
		this.lowerLevels = lowerLevels;
	}
	public List<DescriptionDTO> getAllMIDivisions() {
		return allMIDivisions;
	}
	public void setAllMIDivisions(List<DescriptionDTO> allMIDivisions) {
		this.allMIDivisions = allMIDivisions;
	}
	public List<DescriptionDTO> getAllChannelGroups() {
		return allChannelGroups;
	}
	public void setAllChannelGroups(List<DescriptionDTO> allChannelGroups) {
		this.allChannelGroups = allChannelGroups;
	}
	public List<DescriptionDTO> getAllSuperDivisions() {
		return allSuperDivisions;
	}
	public void setAllSuperDivisions(List<DescriptionDTO> allSuperDivisions) {
		this.allSuperDivisions = allSuperDivisions;
	}
	public List<HierarchyNodeCharacteristicsDTO> getHierarchyNodeLists() {
		return hierarchyNodeLists;
	}
	public void setHierarchyNodeLists(
			List<HierarchyNodeCharacteristicsDTO> hierarchyNodeLists) {
		this.hierarchyNodeLists = hierarchyNodeLists;
	}
	public List<String> getSuburbs() {
		return suburbs;
	}
	public void setSuburbs(List<String> suburbs) {
		this.suburbs = suburbs;
	}
	public List<String> getTowns() {
		return towns;
	}
	public void setTowns(List<String> towns) {
		this.towns = towns;
	}
	public List<String> getMainTowns() {
		return mainTowns;
	}
	public void setMainTowns(List<String> mainTowns) {
		this.mainTowns = mainTowns;
	}
	public List<String> getProvinces() {
		return provinces;
	}
	public void setProvinces(List<String> provinces) {
		this.provinces = provinces;
	}
	public List<String> getProvinceCodes() {
		return provinceCodes;
	}
	public void setProvinceCodes(
			List<String> provinceCodes) {
		this.provinceCodes = provinceCodes;
	}
	public List<String> getLibertyAreas() {
		return libertyAreas;
	}
	public void setLibertyAreas(List<String> libertyAreas) {
		this.libertyAreas = libertyAreas;
	}
	public List<HierarchyAddressClassificationDTO> getHierarchyClassificationLists() {
		return hierarchyClassificationLists;
	}
	public void setHierarchyClassificationLists(
			List<HierarchyAddressClassificationDTO> hierarchyClassificationLists) {
		this.hierarchyClassificationLists = hierarchyClassificationLists;
	}
	public List<String> getLevel() {
		return level;
	}
	public void setLevel(List<String> level) {
		this.level = level;
	}
	
	public IGuiRatingRow getGuiRatingRowBeforeImage() {
		return guiRatingRowBeforeImage;
	}
	public void setGuiRatingRowBeforeImage(IGuiRatingRow guiRatingRowBeforeImage) {
		this.guiRatingRowBeforeImage = guiRatingRowBeforeImage;
	}
	public List<BranchTypeDTO> getAllBranchTypes() {
		return allBranchTypes;
	}
	public void setAllBranchTypes(List<BranchTypeDTO> allBranchTypes) {
		this.allBranchTypes = allBranchTypes;
	}
	public List<OrganisationExternalTypeEnumDTO> getAllOrganisationExternalType() {
		return allOrganisationExternalType;
	}
	public void setAllOrganisationExternalType(
			List<OrganisationExternalTypeEnumDTO> allOrganisationExternalType) {
		this.allOrganisationExternalType = allOrganisationExternalType;
	}
	
	
}
