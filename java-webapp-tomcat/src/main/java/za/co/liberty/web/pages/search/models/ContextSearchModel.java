package za.co.liberty.web.pages.search.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import za.co.liberty.dto.gui.context.ContextSearchModelDTO;
import za.co.liberty.dto.gui.context.IContextSearchType;
import za.co.liberty.dto.gui.context.ResultContextItemDTO;

/**
 * Page model for the search. 
 * 
 * @author JZB0608 - 23 May 2008
 * 
 */
public class ContextSearchModel implements Serializable {
	private static final long serialVersionUID = 1L;
 
	private boolean showAgreements;
	private boolean allowShowPartyOnly;
	private boolean allowShowPartyPersonOnly;
	private boolean allowShowPartyOrganisationOnly;
	private boolean allowShowPartyOrganisationPractice;
	private boolean hasMoreData;
	private IContextSearchType searchType;
	private Object searchValueObject;
	private List<ResultContextItemDTO> searchResultList;
	private List<ResultContextItemDTO> disabledSelectionList;
	private ContextSearchPageOptions searchOptions;
	private ContextSearchModelDTO searchModelDTO;
	
	/**
	 * Creates a list of items that may not be selected.
	 * 
	 * @return
	 */
	public List<ResultContextItemDTO> createDisabledSelectionList(List<ResultContextItemDTO> processList) {
		List<ResultContextItemDTO> l = new ArrayList<ResultContextItemDTO>();
		for (ResultContextItemDTO dto : processList) {
			if (!dto.isMayBeSelected()) {
				l.add(dto);
			}
		}
		return l;
	}

	public List<ResultContextItemDTO> getDisabledSelectionList() {
		return disabledSelectionList;
	}

	public void setDisabledSelectionList(
			List<ResultContextItemDTO> disabledSelectionList) {
		this.disabledSelectionList = disabledSelectionList;
	}

	public ContextSearchModelDTO getSearchModelDTO() {
		return searchModelDTO;
	}

	public void setSearchModelDTO(ContextSearchModelDTO searchModelDTO) {
		this.searchModelDTO = searchModelDTO;
	}

	public boolean isShowAgreements() {
		return showAgreements;
	}

	public void setShowAgreements(boolean showAgreements) {
		this.showAgreements = showAgreements;
	}

	public IContextSearchType getSearchType() {
		return searchType;
	}

	public void setSearchType(IContextSearchType searchOption) {
		this.searchType = searchOption;
	}

	public Object getSearchValueObject() {
		return searchValueObject;
	}

	public void setSearchValueObject(Object searchValueObject) {
		this.searchValueObject = searchValueObject;
	}

	public List<ResultContextItemDTO> getSearchResultList() {
		return searchResultList;
	}

	public void setSearchResultList(List<ResultContextItemDTO> searchResultList) {
		this.searchResultList = searchResultList;
		this.disabledSelectionList =  createDisabledSelectionList(searchResultList);
	}

	public void setAllowShowPartyOnly(boolean b) {
		this.allowShowPartyOnly = b;
	}
	
	public boolean isAllowShowPartyOnly() {
		return allowShowPartyOnly;
	}
	
	//MZP0801 Party Person Only
	public void setAllowShowPartyPersonOnly(boolean allowShowPartyPersonOnly) {
		this.allowShowPartyPersonOnly = allowShowPartyPersonOnly;
	}
	
	public boolean isAllowShowPartyPersonOnly() {
		return allowShowPartyPersonOnly;
	}	
	
	//MZP0801 Party Organisation Only
	public void setAllowShowPartyOrganisationOnly(boolean allowShowPartyOrganisationOnly) {
		this.allowShowPartyOrganisationOnly = allowShowPartyOrganisationOnly;
	}
	
	public boolean isAllowShowPartyOrganisationOnly() {
		return allowShowPartyOrganisationOnly;
	}
	//MXM1904 Added This For Advanced Practice PROD00010430 07/02/2012
	public void setAllowShowPartyOrganisationPractice(boolean allowShowPartyOrganisationPractice) {
		this.allowShowPartyOrganisationPractice = allowShowPartyOrganisationPractice;
	}
		
	public boolean isAllowShowPartyOrganisationPractice() {
		return allowShowPartyOrganisationPractice;
	}
	public void setSearchOptions(ContextSearchPageOptions searchOptions) {
		this.searchOptions = searchOptions;
	}

	public ContextSearchPageOptions getSearchOptions() {
		return searchOptions;
	}

	public boolean isHasMoreData() {
		return hasMoreData;
	}

	public void setHasMoreData(boolean b) {
		hasMoreData = b;
	}
	
}