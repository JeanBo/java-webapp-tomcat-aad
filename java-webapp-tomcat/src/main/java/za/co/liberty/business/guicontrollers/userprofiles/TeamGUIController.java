package za.co.liberty.business.guicontrollers.userprofiles;

import java.util.ArrayList;
import java.util.List;

import za.co.liberty.business.converter.party.profile.TeamConverter;
import za.co.liberty.business.guicontrollers.request.RequestEnquiryGuiController;
import za.co.liberty.dto.contracting.ResultPartyDTO;
import za.co.liberty.dto.gui.context.ResultContextItemDTO;
import za.co.liberty.dto.rating.SegmentNameDTO;
import za.co.liberty.dto.userprofiles.PartyProfileDTO;
import za.co.liberty.dto.userprofiles.RequestCategoryDTO;
import za.co.liberty.dto.userprofiles.TeamDTO;
import za.co.liberty.dto.userprofiles.TeamPartiesDTO;
import za.co.liberty.exceptions.data.DataNotFoundException;
import za.co.liberty.persistence.party.entity.TeamEntity;

public class TeamGUIController implements ITeamGUIController {

	@Override
	public TeamDTO addTeam(TeamDTO arg0, String arg1) throws DataNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<RequestCategoryDTO> findAllRequestCategories() {
		// TODO Auto-generated method stub
			return new RequestEnquiryGuiController().getAllRequestCategoryList();
	}

	@Override
	public List<TeamDTO> findAllTeams() {
		// TODO Auto-generated method stub
		
		
		List<TeamDTO> teamList = new ArrayList<TeamDTO>();
		TeamDTO team = new TeamDTO();
		team.setTeamName("Dummy Team1");
		team.setOid((long) 99999991);
		teamList.add(team);
		
		team = new TeamDTO();
		team.setTeamName("Dummy Team2");
		team.setOid((long) 99999992);
		teamList.add(team);
		
		return teamList;
		
	}

	@Override
	public RequestCategoryDTO findRequestCategory(Long arg0) throws DataNotFoundException {
		// TODO Auto-generated method stub
		throw new IllegalStateException();
	}

	@Override
	public TeamDTO findTeam(Long arg0) throws DataNotFoundException {
		// TODO Auto-generated method stub
		TeamDTO team = new TeamDTO();
		team.setTeamName("Dummy Team");
		team.setOid((long) 99999990);
		return team;
	}

	@Override
	public List<PartyProfileDTO> findUserStartingWithFastLane(String arg0) {
		// TODO Auto-generated method stub
		throw new IllegalStateException();
	}

	@Override
	public boolean isPartyDetailsInGrid(List<TeamPartiesDTO> arg0, ArrayList<ResultContextItemDTO> arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public ResultPartyDTO prepareSelectedUserToAdd(String arg0) throws DataNotFoundException {
		// TODO Auto-generated method stub
		throw new IllegalStateException();
	}

	@Override
	public List<PartyProfileDTO> subtractTeamPartiesFromAutoBox(List<PartyProfileDTO> arg0, List<TeamPartiesDTO> arg1) {
		// TODO Auto-generated method stub
		throw new IllegalStateException();
	}

	@Override
	public TeamDTO updateTeam(TeamDTO arg0, String arg1) throws DataNotFoundException {
		// TODO Auto-generated method stub
		//throw new IllegalStateException();
		System.out.println("updated" + arg0 + " " + arg1);
		return arg0;
		
	}

}


//****

/*
 * package za.co.liberty.business.guicontrollers.userprofiles;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.log4j.Logger;

import za.co.liberty.business.party.IPartyManagement;
import za.co.liberty.business.userprofiles.ITeamManagement;
import za.co.liberty.dto.contracting.ResultPartyDTO;
import za.co.liberty.dto.gui.context.ResultContextItemDTO;
import za.co.liberty.dto.userprofiles.PartyProfileDTO;
import za.co.liberty.dto.userprofiles.RequestCategoryDTO;
import za.co.liberty.dto.userprofiles.TeamDTO;
import za.co.liberty.dto.userprofiles.TeamPartiesDTO;
import za.co.liberty.exceptions.data.DataNotFoundException;
import za.co.liberty.persistence.agreement.IRequestEnumEntityManager;
import za.co.liberty.persistence.party.IPartyProfileEntityManager;
import za.co.liberty.persistence.party.ITeamEntityManager;

/**
 * Gui controller for Team Administrative screens. Will call methods from the business bean, as this simply acts as 
 * a management bean
 * @author JWV2310
 *
 *
@Stateless
public class TeamGuiController implements ITeamGUIController {
	
	private static final Logger logger = Logger.getLogger(TeamGuiController.class);
	
	@EJB
	protected IUserAdminManagement userAdminManager;
	
	@EJB
	protected IPartyManagement partyManager;
	
	@EJB
	protected ITeamManagement managementBean;
	
	@EJB
	protected IRequestCategoryManagement requestCategoryManager;
	
	public List<TeamDTO> findAllTeams() {
		return managementBean.findAllTeams();
	}

	public RequestCategoryDTO findRequestCategory(Long id) throws DataNotFoundException {
		return requestCategoryManager.findRequestCategory(id);
	}

	public TeamDTO findTeam(Long id) throws DataNotFoundException {
		return managementBean.findTeam(id);
	}

	public List<RequestCategoryDTO> findAllRequestCategories() {
		return requestCategoryManager.findAllRequestCategories();
	}

	public boolean isPartyDetailsInGrid(List<TeamPartiesDTO> gridList, ArrayList<ResultContextItemDTO> selectedItemList){
		boolean flag = true;
		for(TeamPartiesDTO teamParty : gridList){
		  TeamPartiesDTO teamPartyObj = (TeamPartiesDTO) teamParty;
		   for(ResultContextItemDTO resultPopUp : selectedItemList){
			  ResultContextItemDTO resultPopUpObj = (ResultContextItemDTO)resultPopUp;
			  if(teamPartyObj.getParty().getPartyOid() == resultPopUpObj.getPartyDTO().getPartyOid()){
				flag = false;
				break;
			  }
		   }
	    }
		
		return flag;
	}
	
	public List<PartyProfileDTO> findUserStartingWithFastLane(String userName) {
		List<PartyProfileDTO> userPartyProfDetailsList = null; 
		 if(userName != null) {
				 userPartyProfDetailsList = userAdminManager.findFastLaneUserStartingWith(userName);
		 }	 
		return userPartyProfDetailsList;
	}
	
	public ResultPartyDTO prepareSelectedUserToAdd(String selectUserUICF) throws DataNotFoundException {
			ResultPartyDTO resultPartyDto = null;
			List<PartyProfileDTO> userPartyProfDetailsList = null;
			userPartyProfDetailsList = findUserStartingWithFastLane(selectUserUICF);
			
			try{
				if ((userPartyProfDetailsList.get(0).getPartyOID() != 0 || userPartyProfDetailsList.get(0).getPartyOID() != -1)){
				   resultPartyDto = partyManager.findPartyWithObjectOid(userPartyProfDetailsList.get(0).getPartyOID());
				}
				if(resultPartyDto == null){
					logger.error("Party could not be found with object oid for Team Admin party addition:");
					return null;
				}
			}catch(DataNotFoundException e){
				logger.error("Party could not be found with object oid for Team Admin party addition:" + e.getMessage());
				return null;
			}
		return resultPartyDto;
				
	}
	
	public void prepareSelectedUserToDelete(List<TeamPartiesDTO> gridList, List<Object> terminateList){
		gridList.removeAll(terminateList);
	}
	
	public List<PartyProfileDTO> subtractTeamPartiesFromAutoBox(List<PartyProfileDTO> selectedPartyList, List<TeamPartiesDTO> currentList){
		
		List<PartyProfileDTO> tempList = new ArrayList<PartyProfileDTO>(selectedPartyList);
		
		for(TeamPartiesDTO teamParty :currentList) {
			   for(PartyProfileDTO partyProfile: tempList){
				   if(partyProfile.getPartyOID() == teamParty.getPartyOID()){
					   selectedPartyList.remove(partyProfile);
				   }
			   }
		}
		return selectedPartyList;
	}
	
	
	public TeamDTO addTeam(TeamDTO teamDto, String createdBy) throws DataNotFoundException{
		return managementBean.addTeam(teamDto, createdBy);
	}

	public TeamDTO updateTeam(TeamDTO teamDto, String createdBy) throws DataNotFoundException{
		return managementBean.updateTeam(teamDto, createdBy);
	}

}
 */
