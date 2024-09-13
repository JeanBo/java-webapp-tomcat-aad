package za.co.liberty.web.pages.admin.models;

import java.io.Serializable;
import java.util.List;

import za.co.liberty.dto.rating.DifferentialPricingFactorDTO;
import za.co.liberty.dto.rating.SegmentDTO;
import za.co.liberty.dto.rating.SegmentNameDTO;
import za.co.liberty.interfaces.rating.difffactor.AQCProductType;
import za.co.liberty.web.data.pages.IMaintenancePageModel;

public class DifferentialFactorModel implements Serializable, IMaintenancePageModel<DifferentialPricingFactorDTO> {
	
	private static final long serialVersionUID = 1L;
	
	private DifferentialPricingFactorDTO selectedItem;
	private List<DifferentialPricingFactorDTO> selectionList;
	
	private List<SegmentNameDTO> filterSegmentName;
	
	private List<SegmentDTO> availableSegments;
	SegmentDTO selected;
	
	public AQCProductType[] aqcProductType = AQCProductType.values();

	
	private List<SegmentNameDTO> allAvailableSegmentNameList;
	

	public List<SegmentDTO> getAvailableSegments() {
		return availableSegments;
	}

	public void setAvailableSegments(List<SegmentDTO> availableSegments) {
		this.availableSegments = availableSegments;
	}

	public SegmentDTO getSelected() {
		return selected;
	}

	public void setSelected(SegmentDTO selected) {
		this.selected = selected;
	}

	public List<SegmentNameDTO> getFilterSegmentName() {
		return filterSegmentName;
	}

	public void setFilterSegmentName(List<SegmentNameDTO> filterSegmentName) {
		this.filterSegmentName = filterSegmentName;
	}

	public DifferentialPricingFactorDTO getSelectedItem() {
		return selectedItem;
	}
	
	public void setSelectedItem(DifferentialPricingFactorDTO selectedItem) {
		this.selectedItem = selectedItem;
	}
	public List<DifferentialPricingFactorDTO> getSelectionList() {
		return selectionList;
	}
	public void setSelectionList(List<DifferentialPricingFactorDTO> selectionList) {
		this.selectionList = selectionList;
	}
	
	public List<SegmentNameDTO> getAllAvailableSegmentNameList() {
		return allAvailableSegmentNameList;
	}

	public void setAllAvailableSegmentNameList(
			List<SegmentNameDTO> allAvailableSegmentNameList) {
		this.allAvailableSegmentNameList = allAvailableSegmentNameList;
	}
	
}
