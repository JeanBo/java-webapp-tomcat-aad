package za.co.liberty.business.guicontrollers.userprofiles;


import java.util.List;
import java.util.Set;

import javax.ejb.Local;

import za.co.liberty.dto.userprofiles.RequestCategoryDTO;
import za.co.liberty.exceptions.data.DataNotFoundException;
import za.co.liberty.interfaces.agreements.requests.RequestKindType;


/**
 * 
 * @author jwv2310
 * 
 * This interface defines the business methods for retrieving request categories. Will act as the Gui controller.
 * Calling: RequestCategoryGuiController.java
 */

@Local
public interface IRequestCategoryGUIController {
	
	public List<RequestCategoryDTO> findAllRequestCategories();
	
	public RequestCategoryDTO newRequestCategory();
	
	public RequestCategoryDTO findRequestCategory(Long Id)throws DataNotFoundException;

	public void updateRequestCategory(RequestCategoryDTO reqCatDTO, String createdBy) throws DataNotFoundException;
	
	public String findLoggedInUserOID(String loggedInUserName) throws DataNotFoundException;

	public void addRequestCategory(RequestCategoryDTO reqCatDTO, String createdBy) throws DataNotFoundException ;
	
	public void addRequestKind(RequestKindType requestKind, List<RequestKindType> workingList, List<RequestKindType> selectedList);
	
	public void removeRequestKind(Set<RequestKindType> removeList, List<RequestKindType> gridList, List<RequestKindType> comboList);
	
	public List<RequestKindType> updateModel(List<RequestKindType> allAvailableKinds, List<RequestKindType> selectedKinds);

}