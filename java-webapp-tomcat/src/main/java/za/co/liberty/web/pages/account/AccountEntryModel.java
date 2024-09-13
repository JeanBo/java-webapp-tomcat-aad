package za.co.liberty.web.pages.account;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import za.co.liberty.dto.account.AccountEntryDTO;
import za.co.liberty.dto.account.AccountEntrySelectionCriteriaDTO;
import za.co.liberty.dto.account.AccountEntryTypeDTO;
import za.co.liberty.dto.account.ProductSpecDTO;
import za.co.liberty.dto.agreement.AgreementRoleDTO;
import za.co.liberty.dto.party.bankingdetail.type.AccountType;
import za.co.liberty.dto.userprofiles.ContextDTO;
import za.co.liberty.party.domain.enumeration.AccountTypeEnumeration;


/**
 * Model for the account entry listing screen on the new SRS screen. Basic accounts search functionality using FLO's.
 * @author JWV2310
 *
 */
public class AccountEntryModel implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	
	//hold current selected context dto from the search agreement button - generic usage
	private ContextDTO selectedContext;
	
	//hold the selected criteria from the selection form
	private AccountEntrySelectionCriteriaDTO selectionCriteria;
	
	
	
	//initiate list before hand with all products
	private List<ProductSpecDTO> allProducts;
	private List<AccountEntryTypeDTO> allAccountEntryTypeList;
	private List<AccountEntryTypeDTO> allGroupedEntryTypeList;
	
	private List<AccountEntryTypeDTO> allAvailableAccountTypes;
	
	private List<AgreementRoleDTO> allIntermediaryAccountRoles;
	
	private List<AccountEntryDTO> allReturnedAccountEntries;
	
	
	public List<AccountEntryTypeDTO> getAllGroupedEntryTypeList() {
		return allGroupedEntryTypeList;
	}
	public void setAllGroupedEntryTypeList(List<AccountEntryTypeDTO> allGroupedEntryTypeList) {
		this.allGroupedEntryTypeList = allGroupedEntryTypeList;
	}
	public List<AgreementRoleDTO> getAllIntermediaryAccountRoles() {
		return allIntermediaryAccountRoles;
	}
	public void setAllIntermediaryAccountRoles(
			List<AgreementRoleDTO> allIntermediaryAccountRoles) {
		this.allIntermediaryAccountRoles = allIntermediaryAccountRoles;
	}
	public List<AccountEntryTypeDTO> getAllAvailableAccountTypes() {
		return allAvailableAccountTypes;
	}
	public void setAllAvailableAccountTypes(List<AccountEntryTypeDTO> allAvailableAccountTypes) {
		this.allAvailableAccountTypes = allAvailableAccountTypes;
	}
	public List<AccountEntryDTO> getAllReturnedAccountEntries() {
		if(allReturnedAccountEntries == null) {
			return new ArrayList<AccountEntryDTO>();
		}
		return allReturnedAccountEntries;
	}
	public void setAllReturnedAccountEntries(
			List<AccountEntryDTO> allReturnedAccountEntries) {
		this.allReturnedAccountEntries = allReturnedAccountEntries;
	}
	public List<AccountEntryTypeDTO> getAllAccountEntryTypeList() {
		return allAccountEntryTypeList;
	}
	public void setAllAccountEntryTypeList(List<AccountEntryTypeDTO> allAccountEntryTypeList) {
		this.allAccountEntryTypeList = allAccountEntryTypeList;
	}

	public List<ProductSpecDTO> getAllProducts() {
		return allProducts;
	}
	public void setAllProducts(List<ProductSpecDTO> allProducts) {
		this.allProducts = allProducts;
	}

	public AccountEntrySelectionCriteriaDTO getSelectionCriteria() {
		if(selectionCriteria == null){
			return new AccountEntrySelectionCriteriaDTO();
		}
		return selectionCriteria;
	}

	public void setSelectionCriteria(
			AccountEntrySelectionCriteriaDTO selectionCriteria) {
		this.selectionCriteria = selectionCriteria;
	}

	public ContextDTO getSelectedContext() {
		if(selectedContext == null) {
			return new ContextDTO();
		}
		return selectedContext;
	}

	public void setSelectedContext(ContextDTO selectedContext) {
		this.selectedContext = selectedContext;
	}
	
	

}
