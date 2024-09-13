package za.co.liberty.web.pages.releasefe;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import za.co.liberty.dto.account.DPEDTO;
import za.co.liberty.dto.account.DpeMpeDTO;
import za.co.liberty.dto.account.DpeMpeGridDTO;
import za.co.liberty.dto.account.UnreleaseCommDTO;
//import za.co.liberty.dto.account.UnreleaseCommDTO;
import za.co.liberty.dto.gui.context.ResultContextItemDTO;
//import za.co.liberty.dto.rating.RatingDescriptionDTO;
import za.co.liberty.dto.userprofiles.ContextDTO;
//import za.co.liberty.interfaces.rating.description.IRatingDescriptionFLO;

public class ReleaseFEModel implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private ContextDTO context;
	
	private List<ResultContextItemDTO> selectedIds;
	private ResultContextItemDTO selectedID;
	private List<DPEDTO> allPoliciesPerProductListForRelease;
	private List<DPEDTO> retrieveAllDPEFutureEarnings;
//	private List<UnreleaseCommDTO> selectedPoliciesForRelease;
	private String loggedInUser;
//	private List<IRatingDescriptionFLO> allProductReferencesList;
	//private List<RatingDescriptionDTO> allProductReferencesList;
	private List<DpeMpeDTO> allReleaseFutureEarningList;
	
	List<DpeMpeGridDTO> gridDtoList = new ArrayList<DpeMpeGridDTO>();
	
//	private List<RatingDescriptionDTO> allStringProds;
//	private List<RatingDescriptionDTO> selectedProds;
	
	private List a;
	
	private List<UnreleaseCommDTO> retrieveAllUnreleaseCommission;
	
	
	
	public List<DpeMpeGridDTO> getGridDtoList() {
		if(gridDtoList == null) {
			gridDtoList = new ArrayList<DpeMpeGridDTO>();
		}
		return gridDtoList;
	}




	public void setGridDtoList(List<DpeMpeGridDTO> gridDtoList) {
		this.gridDtoList = gridDtoList;
	}




//	public List<RatingDescriptionDTO> getSelectedProds() {
//		if(selectedProds == null) {
//			return new ArrayList<RatingDescriptionDTO>();
//		}
//		return selectedProds;
//	}




//	public void setSelectedProds(List<RatingDescriptionDTO> selectedProds) {
//		this.selectedProds = selectedProds;
//	}




//	public List<UnreleaseCommDTO> getSelectedPoliciesForRelease() {
//		if(selectedPoliciesForRelease == null){
//			this.selectedPoliciesForRelease = new ArrayList<UnreleaseCommDTO>();
//		}
//		return selectedPoliciesForRelease;
//	}
	
	
	
	
//	public void setSelectedPoliciesForRelease(
//			List<UnreleaseCommDTO> selectedPoliciesForRelease) {
//		this.selectedPoliciesForRelease = selectedPoliciesForRelease;
//	}

	public List<DPEDTO> getAllPoliciesPerProductListForRelease() {
		if(allPoliciesPerProductListForRelease == null){
			this.allPoliciesPerProductListForRelease = new ArrayList<DPEDTO>();
		}
		return allPoliciesPerProductListForRelease;
	}

	public void setAllPoliciesPerProductListForRelease(
			List<DPEDTO> allPoliciesPerProductListForRelease) {
		this.allPoliciesPerProductListForRelease = allPoliciesPerProductListForRelease;
	}

	public List<UnreleaseCommDTO> getRetrieveAllUnreleaseCommission() {
		if(retrieveAllUnreleaseCommission == null){
			this.retrieveAllUnreleaseCommission = new ArrayList<UnreleaseCommDTO>();
		}
			return retrieveAllUnreleaseCommission;
	}

	public void setRetrieveAllUnreleaseCommission(
			List<UnreleaseCommDTO> retrieveAllUnreleaseCommission) {
		this.retrieveAllUnreleaseCommission = retrieveAllUnreleaseCommission;
	}

	public List<DPEDTO> getRetrieveAllDPEFutureEarnings() {
		if(retrieveAllDPEFutureEarnings == null){
			retrieveAllDPEFutureEarnings = new ArrayList<DPEDTO>();
		}
		return retrieveAllDPEFutureEarnings;
	}

	public void setRetrieveAllDPEFutureEarnings(
			List<DPEDTO> retrieveAllDPEFutureEarnings) {
		this.retrieveAllDPEFutureEarnings = retrieveAllDPEFutureEarnings;
	}

	public ResultContextItemDTO getSelectedID() {
		return selectedID;
	}

	public void setSelectedID(ResultContextItemDTO selectedID) {
		this.selectedID = selectedID;
	}

	public List<ResultContextItemDTO> getSelectedIds() {
		return selectedIds;
	}

	public void setSelectedIds(List<ResultContextItemDTO> selectedIds) {
		this.selectedIds = selectedIds;
	}

	public String getLoggedInUser() {
		return loggedInUser;
	}

	public void setLoggedInUser(String loggedInUser) {
		this.loggedInUser = loggedInUser;
	}




//	public List<IRatingDescriptionFLO> getAllProductReferencesList() {
//		return allProductReferencesList;
//	}




//	public void setAllProductReferencesList(
//			List<IRatingDescriptionFLO> allProductReferencesList) {
//		this.allProductReferencesList = allProductReferencesList;
//	}




//	public List<RatingDescriptionDTO> getAllStringProds() {
//		if(allStringProds == null){
//			return new ArrayList<RatingDescriptionDTO>();
//		}
//		return allStringProds;
//	}




//	public void setAllStringProds(List<RatingDescriptionDTO> allStringProds) {
//		this.allStringProds = allStringProds;
//	}




	public List getA() {
		return a;
	}




	public void setA(List a) {
		this.a = a;
	}




	public List<DpeMpeDTO> getAllReleaseFutureEarningList() {
		if(allReleaseFutureEarningList == null) {
			allReleaseFutureEarningList =  new ArrayList<DpeMpeDTO>();
		}
		return allReleaseFutureEarningList;
	}




	public void setAllReleaseFutureEarningList(
			List<DpeMpeDTO> allReleaseFutureEarningList) {
		this.allReleaseFutureEarningList = allReleaseFutureEarningList;
	}




	public ContextDTO getContext() {
		return context;
	}




	public void setContext(ContextDTO context) {
		this.context = context;
	}



//
//	public List<String> getAllStringProds() {
//		return allStringProds;
//	}
//
//
//
//
//	public void setAllStringProds(List<String> allStringProds) {
//		this.allStringProds = allStringProds;
//	}

//	public List<RatingDescriptionDTO> getAllProductReferencesList() {
//		return allProductReferencesList;
//	}
//
//	public void setAllProductReferencesList(
//			List<RatingDescriptionDTO> allProductReferencesList) {
//		this.allProductReferencesList = allProductReferencesList;
//	}

	
	
	

}
