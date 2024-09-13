package za.co.liberty.web.pages.hierarchy;

import java.io.Serializable;
import java.util.List;

import za.co.liberty.dto.gui.tree.TreeNodeDTO;


public class TreePageModel implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private TreeNodeDTO selectedItem;
	private List<TreeNodeDTO> selectedList;
	
	
	public TreeNodeDTO getSelectedItem() {
		if(selectedItem == null) {
			selectedItem = new TreeNodeDTO();
		}
		return selectedItem;
	}
	public void setSelectedItem(TreeNodeDTO selectedItem) {
		
		this.selectedItem = selectedItem;
	}
	public List<TreeNodeDTO> getSelectedList() {
		
		return selectedList;
	}
	public void setSelectedList(List<TreeNodeDTO> selectedList) {
		this.selectedList = selectedList;
	}
	
	

}
