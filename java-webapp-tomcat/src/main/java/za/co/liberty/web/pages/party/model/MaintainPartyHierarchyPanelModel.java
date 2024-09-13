package za.co.liberty.web.pages.party.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import za.co.liberty.dto.party.PartyRoleDTO;
import za.co.liberty.dto.rating.DescriptionDTO;

/**
 * Model for the party hierarchy panel
 * @author DZS2610
 *
 */
public class MaintainPartyHierarchyPanelModel implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private List<PartyRoleDTO> partyToPartyRoles;
	
	private List<PartyRoleDTO> partyToPartyRolesBeforeImage;
	
	//Kept here as the behaviour logic could not check this due to inverse roles being used
	private List<PartyRoleDTO> partyToPartyRoleRemovals = new ArrayList<PartyRoleDTO>();

	
	private List<PartyRoleDTO> partyRoles =new ArrayList<PartyRoleDTO>();
	
	private List<PartyRoleDTO> partyRolesBeforeImage;
	
	private List<DescriptionDTO> distributionGroupSubType;
	
	//Kept here as the behaviour logic could not check this due to inverse roles being used
	private List<PartyRoleDTO> partyRoleRemovals = new ArrayList<PartyRoleDTO>();
	
	public List<PartyRoleDTO> getPartyToPartyRoleRemovals() {
		return partyToPartyRoleRemovals;
	}

	public void setPartyToPartyRoleRemovals(
			List<PartyRoleDTO> partyToPartyRoleRemovals) {
		this.partyToPartyRoleRemovals = partyToPartyRoleRemovals;
	}

	public List<PartyRoleDTO> getPartyToPartyRoles() {
		return partyToPartyRoles;
	}

	public void setPartyToPartyRoles(List<PartyRoleDTO> partyToPartyRoles) {
		this.partyToPartyRoles = partyToPartyRoles;
	}

	public List<PartyRoleDTO> getPartyToPartyRolesBeforeImage() {
		return partyToPartyRolesBeforeImage;
	}

	public void setPartyToPartyRolesBeforeImage(
			List<PartyRoleDTO> partyToPartyRolesBeforeImage) {
		this.partyToPartyRolesBeforeImage = partyToPartyRolesBeforeImage;
	}

	public List<PartyRoleDTO> getPartyRoles() {
		return partyRoles;
	}

	public void setPartyRoles(List<PartyRoleDTO> partyRoles) {
		this.partyRoles = partyRoles;
	}

	public List<PartyRoleDTO> getPartyRolesBeforeImage() {
		return partyRolesBeforeImage;
	}

	public void setPartyRolesBeforeImage(List<PartyRoleDTO> partyRolesBeforeImage) {
		this.partyRolesBeforeImage = partyRolesBeforeImage;
	}

	public List<PartyRoleDTO> getPartyRoleRemovals() {
		return partyRoleRemovals;
	}

	public void setPartyRoleRemovals(List<PartyRoleDTO> partyRoleRemovals) {
		this.partyRoleRemovals = partyRoleRemovals;
	}

	public List<DescriptionDTO> getDistributionGroupSubType() {
		return distributionGroupSubType;
	}

	public void setDistributionGroupSubType(
			List<DescriptionDTO> distributionGroupSubType) {
		this.distributionGroupSubType = distributionGroupSubType;
	}
	
	
}
