package za.co.liberty.web.pages.admin.models;

import java.io.Serializable;
import java.util.List;


import za.co.liberty.dto.rating.SegmentDTO;
import za.co.liberty.dto.rating.SegmentNameDTO;
import za.co.liberty.interfaces.rating.difffactor.SegmentContextType;
import za.co.liberty.web.data.pages.IMaintenancePageModel;

public class SegmentNameModel implements Serializable, IMaintenancePageModel<SegmentNameDTO> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private SegmentNameDTO selectedItem;
	private List<SegmentNameDTO> selectionList;
	
	private List<SegmentDTO> selectionFilterList;
	
	private SegmentDTO selectedSegment;
	
	private List<SegmentContextType> allSegmentTypes;
	private SegmentContextType selectedSegmentType;
	
	
	
	
	public List<SegmentContextType> getAllSegmentTypes() {
		return allSegmentTypes;
	}

	public void setAllSegmentTypes(List<SegmentContextType> allSegmentTypes) {
		this.allSegmentTypes = allSegmentTypes;
	}

	public SegmentContextType getSelectedSegmentType() {
		return selectedSegmentType;
	}

	public void setSelectedSegmentType(SegmentContextType selectedSegmentType) {
		this.selectedSegmentType = selectedSegmentType;
	}

	public SegmentDTO getSelectedSegment() {
		if (selectedSegment == null){
			SegmentDTO newo = new SegmentDTO();
			selectedSegment = newo;
		}
		return selectedSegment;
	}

	public void setSelectedSegment(SegmentDTO selectedSegment) {
		this.selectedSegment = selectedSegment;
	}

	public List<SegmentDTO> getSelectionFilterList() {
		return selectionFilterList;
	}

	public void setSelectionFilterList(List<SegmentDTO> selectionFilterList) {
		this.selectionFilterList = selectionFilterList;
	}

	public SegmentNameDTO getSelectedItem() {
		// TODO Auto-generated method stub
		return selectedItem;
	}

	public List<SegmentNameDTO> getSelectionList() {
		// TODO Auto-generated method stub
		return selectionList;
	}

	public void setSelectedItem(SegmentNameDTO selected) {
		this.selectedItem = selected;
		
	}

	public void setSelectionList(List<SegmentNameDTO> selectionList) {
		this.selectionList = selectionList;
		
	}

}
