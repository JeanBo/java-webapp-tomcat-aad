package za.co.liberty.web.pages.admin.models;

import java.io.Serializable;
import java.util.List;

import za.co.liberty.dto.rating.SegmentDTO;
import za.co.liberty.dto.rating.SegmentNameDTO;
import za.co.liberty.web.data.pages.IMaintenancePageModel;

/**
 * Model for the Segment admin page
 * 
 * @author JZB0608 - 08 Nov 2010
 *
 */
public class SegmentModel implements Serializable, IMaintenancePageModel<SegmentDTO>  {

	private static final long serialVersionUID = 1L;
	
	private SegmentDTO selectedItem;
	private List<SegmentDTO> selectionList;
	private List<SegmentNameDTO> allAvailableSegmentNameList;
	
	private SegmentNameDTO selectedBoxName;
	
	
	
	public SegmentNameDTO getSelectedBoxName() {
		return selectedBoxName;
	}

	public void setSelectedBoxName(SegmentNameDTO selectedBoxName) {
		this.selectedBoxName = selectedBoxName;
	}

	public SegmentDTO getSelectedItem() {
		return selectedItem;
	}

	public List<SegmentDTO> getSelectionList() {
		return selectionList;
	}

	public void setSelectedItem(SegmentDTO selected) {
		this.selectedItem = selected;
	}

	public void setSelectionList(List<SegmentDTO> selectionList) {
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
