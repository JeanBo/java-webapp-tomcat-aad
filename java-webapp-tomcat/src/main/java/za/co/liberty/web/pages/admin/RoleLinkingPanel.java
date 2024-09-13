package za.co.liberty.web.pages.admin;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.model.Model;

import za.co.liberty.dto.userprofiles.ProfileRoleDTO;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.data.pages.IMaintenancePageModel;
import za.co.liberty.web.pages.panels.AbstractLinkingPanel;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGridColumn;

import com.inmethod.grid.IGridColumn;

/**
 * <p>This panel is used to display menu items (in a table) that are 
 * linked to a parent object.  It allows links to be added or 
 * removed as well as the display & modification of the linked 
 * objects.  A list of available items that can be added are provided
 * and by default it will only show items that are not already linked.  
 * </p>
 * 
 * @author jzb0608 - 07 May 2008 
 * 
 */
public abstract class RoleLinkingPanel<MODEL extends IMaintenancePageModel> 
	extends AbstractLinkingPanel<MODEL,ProfileRoleDTO,ProfileRoleDTO> {

	/* Constants */
	private static final long serialVersionUID = -8003453537906825671L;



	/**
	 * Default constructor
	 * 
	 */
	public RoleLinkingPanel(String id, MODEL pageModel,
			EditStateType editState) {
		super(id, pageModel, editState, new ChoiceRenderer("roleName","nameBracketShortDescription"));		
	}

	@Override
	protected Comparator<? super ProfileRoleDTO> getAvailableItemComparator() {
		return new Comparator<ProfileRoleDTO>() {
			public int compare(ProfileRoleDTO o1, ProfileRoleDTO o2) {
				return o1.getRoleName().compareTo(
						o2.getRoleName());
			}
		};
	}

	@Override
	protected Comparator<? super ProfileRoleDTO> getLinkedItemComparator() {
		return getAvailableItemComparator();
	}
	
	@Override
	protected ProfileRoleDTO createNewLinkedItem(ProfileRoleDTO dto) {
		ProfileRoleDTO newDto = new ProfileRoleDTO();
		newDto.setRoleName(dto.getRoleName());
		newDto.setProfileRoleID(dto.getProfileRoleID());		
		return newDto;
	}


	/**
	 * Added by Dean(DZS2610) 21 July 2008
	 */
	@Override
	protected List<IGridColumn> getLinkedItemGridColumns() {
		List<IGridColumn> columns = new ArrayList<IGridColumn>();
		
		//add the rule description label panel
		columns.add(new SRSDataGridColumn<ProfileRoleDTO>("roleName",new Model("Role Name"),"roleName","roleName",getEditState()).setInitialSize(280));			
		return columns;
	}
	
	@Override
	protected Object getKeyForAvailableItem(ProfileRoleDTO item) {
		return item.getProfileRoleID();
	}
	
	@Override
	protected Object getKeyForLinkedItem(ProfileRoleDTO item) {
		return item.getProfileRoleID();
	}	
}
