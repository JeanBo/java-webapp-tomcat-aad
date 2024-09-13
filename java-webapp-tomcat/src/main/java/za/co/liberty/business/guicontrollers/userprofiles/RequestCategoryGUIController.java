package za.co.liberty.business.guicontrollers.userprofiles;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.log4j.Logger;

import za.co.liberty.business.party.IPartyManagement;
import za.co.liberty.dto.contracting.ResultPartyDTO;
import za.co.liberty.dto.userprofiles.RequestCategoryDTO;
import za.co.liberty.exceptions.data.DataNotFoundException;
import za.co.liberty.interfaces.agreements.requests.RequestKindType;
import za.co.liberty.persistence.agreement.IRequestEnumEntityManager;


/**
 * Purpose: This class will define the business logic for all the interface defined methods, to act as controller for all
 * 			gui related actions.
 * @author jwv2310
 *
 */
@Stateless
public class RequestCategoryGUIController implements IRequestCategoryGUIController {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

//	@EJB
//	protected IRequestCategoryManagement managementBean;
	
//	@EJB
//	protected IRequestEnumEntityManager requestEntityManager;
	
//	@EJB
//	protected IPartyManagement partyManager;
	
		
	public RequestCategoryDTO findRequestCategory(Long Id) throws DataNotFoundException {
			// return managementBean.findRequestCategory(Id);
		//AYD#Change
		
		for(RequestCategoryDTO d: findAllRequestCategories()) {
					System.out.println("AYD------findAllRequestCategories()------------Id"+Id);
					if(d.getId()==Id) {
						return d;
					}
				}
				List<RequestKindType> requestkindtype = new ArrayList<RequestKindType>();
				requestkindtype.add(RequestKindType.MaintainStandAloneParty);
				requestkindtype.add(RequestKindType.AnnuityValue);
				requestkindtype.add(RequestKindType.SubIllustrativeSurrenderValue);
				System.out.println("AYD------findAllRequestCategories()------------");
				RequestCategoryDTO dummy=new RequestCategoryDTO();
				dummy.setId(Id);
				dummy.setName("DUMMYMAINTENANCEREQUEST");
				dummy.setCreatedBy("DUMMYSRSLOAD");
				dummy.setCreatedTime(null);
				dummy.setModifiedTime(null);	
				dummy.setSelectedRequestKindsList(requestkindtype);
				System.out.println("AYD------------------findAllRequestCategories");
				return dummy;
		
	}
	
	public RequestCategoryDTO newRequestCategory(){
		RequestCategoryDTO newDto = new RequestCategoryDTO();
		return newDto;
	}
	
	public List<RequestKindType> updateModel(List<RequestKindType> allAvailableKinds, List<RequestKindType> selectedKinds){
		List<RequestKindType> result = new ArrayList<RequestKindType>(allAvailableKinds);
		result.removeAll(selectedKinds);
		return result;
	}
	
	public List<RequestCategoryDTO> findAllRequestCategories() {
		System.out.println("AYD------------------findAllRequestCategories");
		//AYD:Change-Commented EJB refer logic and adding dummy data
		
		//return managementBean.findAllRequestCategories();
		
		List<RequestKindType> requestkindtype = new ArrayList<RequestKindType>();
		requestkindtype.add(RequestKindType.MaintainStandAloneParty);
		requestkindtype.add(RequestKindType.AnnuityValue);
		requestkindtype.add(RequestKindType.SubIllustrativeSurrenderValue);
		
		List<RequestCategoryDTO> requestcategorylist = new ArrayList<RequestCategoryDTO>();
		RequestCategoryDTO dto = new RequestCategoryDTO();
		dto.setId((long)1); 
		dto.setName("Maintenance Requests");
		dto.setCreatedBy("SRSLOAD");
		dto.setCreatedTime(null);
		dto.setModifiedTime(null);
		dto.setSelectedRequestKindsList(requestkindtype);
		requestcategorylist.add(dto);
		
		dto = new RequestCategoryDTO();
		dto.setId((long)2); 
		dto.setName("Party Requests");
		dto.setCreatedBy("SRSLOAD");
		dto.setCreatedTime(null);
		dto.setModifiedTime(null);
		dto.setSelectedRequestKindsList(requestkindtype);
		requestcategorylist.add(dto);
		
		dto = new RequestCategoryDTO();
		dto.setId((long)3); 
		dto.setName("Terminate Requests");
		dto.setCreatedBy("SRSLOAD");
		dto.setCreatedTime(null);
		dto.setModifiedTime(null);
		dto.setSelectedRequestKindsList(requestkindtype);
		requestcategorylist.add(dto);
		return requestcategorylist;
	}
	
	/*
	 * @return Find the current logged in user partyOID via the UACF id.
	 */
	public String findLoggedInUserOID(String loggedInUserName) throws DataNotFoundException {
			/*Long loggedInUserNameOID = null;
			List<ResultPartyDTO> loggedIn = new ArrayList<ResultPartyDTO>();
			loggedIn = partyManager.findPartyWithUacfID(loggedInUserName);
			for(ResultPartyDTO e: loggedIn){
				if (loggedIn.size() > 1){
					loggedInUserNameOID = e.getPartyOid();
					break;
				}else{
					loggedInUserNameOID = e.getPartyOid();
				}
			}
			return "" + loggedInUserNameOID;*/
		
		return null;
	}
	
	
	public void updateRequestCategory(RequestCategoryDTO requestCategoryDTO, String createdBy) throws DataNotFoundException {
			//managementBean.updateRequestCategory(requestCategoryDTO, createdBy);
	}
	
	public void removeRequestKind(Set<RequestKindType> removeList, List<RequestKindType> gridList, List<RequestKindType> comboList){
		gridList.removeAll(removeList);
		comboList.addAll(removeList);
	}
	
	public void addRequestKind(RequestKindType requestKind,  List<RequestKindType> workingList, List<RequestKindType> selectedList) {
		workingList.add(requestKind);
		selectedList.remove(requestKind);
	}
	 
	 /*
	  * (non-Javadoc)
	  * @see za.co.liberty.business.guicontrollers.userprofiles.IRequestCategoryManagement#addRequestCategory(za.co.liberty.dto.userprofiles.RequestCategoryDTO, java.lang.String)
	  */
	 // add a new request category
	 public void addRequestCategory(RequestCategoryDTO reqCatDTO, String createdBy) throws DataNotFoundException{
			// managementBean.addRequestCategory(reqCatDTO, createdBy);
	 }
	
}