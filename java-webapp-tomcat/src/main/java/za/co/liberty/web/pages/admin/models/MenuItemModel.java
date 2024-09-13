package za.co.liberty.web.pages.admin.models;

import java.io.Serializable;
import java.util.List;

import za.co.liberty.dto.userprofiles.MenuItemDTO;
import za.co.liberty.web.data.pages.IMaintenancePageModel;

/**
 * Menu Item model class
 * 
 * @author jzb0608 - 23 Apr 2008
 * 
 */
public class MenuItemModel implements Serializable, IMaintenancePageModel<MenuItemDTO> {

	private static final long serialVersionUID = 4736602430175462792L;

	private List<MenuItemDTO> selectedList;

	private MenuItemDTO selectedMenuItem;

	public MenuItemDTO getSelectedItem() {
		return selectedMenuItem;
	}

	public void setSelectedItem(MenuItemDTO selectedMenuItem) {
		this.selectedMenuItem = selectedMenuItem;
	}

	public void setSelectionList(List<MenuItemDTO> list) {
		selectedList = list;
	}

	public List<MenuItemDTO> getSelectionList() {
		return selectedList;
	}

}
