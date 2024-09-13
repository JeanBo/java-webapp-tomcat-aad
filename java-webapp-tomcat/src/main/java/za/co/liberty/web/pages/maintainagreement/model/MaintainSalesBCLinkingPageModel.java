package za.co.liberty.web.pages.maintainagreement.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.SerializationUtils;

import za.co.liberty.common.domain.TypeVO;
import za.co.liberty.dto.agreement.AgreementDTO;
import za.co.liberty.dto.agreement.AgreementRoleDTO;
import za.co.liberty.dto.agreement.RequestDTO;
import za.co.liberty.dto.agreement.SalesBCLinking.ServicingPanelDTO;
import za.co.liberty.dto.agreement.maintainagreement.AgreementRoleGridDTO;
import za.co.liberty.dto.agreement.maintainagreement.MaintainAgreementDTO;
import za.co.liberty.dto.agreement.maintainagreement.SalesCategoryDTO;
import za.co.liberty.dto.agreement.maintainagreement.ValidAgreementValuesDTO;
import za.co.liberty.dto.agreement.maintainagreement.WorkflowDTO;
import za.co.liberty.dto.party.PartyDTO;
import za.co.liberty.interfaces.agreements.RoleKindType;
import za.co.liberty.web.data.enums.fields.AgreementGUIField;
import za.co.liberty.web.data.pages.ITabbedPageModel;
import za.co.liberty.web.pages.maintainagreement.template.AgreementTemplate;
import za.co.liberty.web.wicket.view.ContextDrivenViewTemplate;

/**
 * PageModel used for Add and Maintain agreement.
 */
public class MaintainSalesBCLinkingPageModel implements
		ITabbedPageModel<ServicingPanelDTO>, Serializable {

	//private static final long serialVersionUID = 1643792587546952282L;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unused")
	private List<MaintainAgreementDTO> selectionList;

	private MaintainAgreementDTO maintainAgreementDTO;

	private MaintainAgreementDTO previousMaintainAgreementDTO;

	private ValidAgreementValuesDTO validAgreementValues;

	private ContextDrivenViewTemplate<AgreementGUIField, AgreementDTO> viewTemplate;
	
	private List<RequestDTO> outstandingAuthorisations;
	
	private PartyDTO existingPartyDetails;
	
	private boolean agreementKindChangeEnabled;

	private int currentTab = -1;

	private long agreementNo;

	private String uacfId;

	private ServicingPanelDTO selectedItem;
	
	private Class<?> currentTabClass;
	
	private PartyTypeSelection partyTypeSelection;
	
	private BankingDetailsRequiredSelection bankingDetailsRequiredSelection;
	
	private List<AgreementRoleGridDTO> gridRoles = new ArrayList<AgreementRoleGridDTO>();
	
	private List<AgreementRoleGridDTO> otherPartyGridRoles = new ArrayList<AgreementRoleGridDTO>();
	
	private List<TypeVO> servicingTypes = new ArrayList<TypeVO>();
	
	//the selectable roles for the specific agreement in the context
	private List<RoleKindType> selectableRoleKinds = new ArrayList<RoleKindType>();
	
	private List<RoleKindType> selectableSupervisionRoleKinds = new ArrayList<RoleKindType>();
	
	private boolean isPartyHasBankingDetails;
	
	private List<TypeVO> supervisorTypes = new ArrayList<TypeVO>();
	
	private List<SalesCategoryDTO> divisionsList = new ArrayList<SalesCategoryDTO>();
	private List<SalesCategoryDTO> salesCategoryList = new ArrayList<SalesCategoryDTO>();
	private List<SalesCategoryDTO> agreementKindList = new ArrayList<SalesCategoryDTO>();
	// SSM2707 Hierarchy FR3.4 FAIS Details SWETA MENON
	private boolean allowFAIS;
	
	public static enum PartyTypeSelection {
		CURRENT_PARTY("Use Current Party"), NEW_PARTY("Create New Party");
		
		private String description;
		
		private PartyTypeSelection(String description) {
			this.description=description;
		}
		public String getDescription() {
			return description;
		}
	}
	
	public static enum BankingDetailsRequiredSelection {
		YES("Yes"), NO("No");
		
		private String description;
		
		private BankingDetailsRequiredSelection(String description) {
			this.description=description;
		}
		public String getDescription() {
			return description;
		}
	}
	
	
	public PartyTypeSelection getPartyTypeSelection() {
		return partyTypeSelection;
	}


	public void setPartyTypeSelection(PartyTypeSelection partyTypeSelection) {
		this.partyTypeSelection = partyTypeSelection;
	}
	
	public PartyDTO getExistingPartyDetails() {
		return existingPartyDetails;
	}


	public void setExistingPartyDetails(PartyDTO existingPartyDetails) {
		this.existingPartyDetails = existingPartyDetails;
	}


	@SuppressWarnings("unchecked")
	public Class getCurrentTabClass() {		
		return currentTabClass;
	}


	@SuppressWarnings("unchecked")
	public void setCurrentTabClass(Class currentTabClass) {
		this.currentTabClass = currentTabClass;		
	}
	
	public MaintainSalesBCLinkingPageModel() {
		super();
	}

	/**
	 * Create a new MaintainAgreementPageModel
	 * @param agreementDTO the agreement DTO
	 * @param validAgreementValuesDTO the valid agreement values DTO
	 * @param pendingAuthorisation true if the agreement has requests pending authorisation
	 */
	public MaintainSalesBCLinkingPageModel(AgreementDTO agreementDTO,
			ValidAgreementValuesDTO validAgreementValuesDTO) {
		updateModel(agreementDTO, validAgreementValuesDTO);
	}

	/**
	 * Update the model with a new agreementDTO and valid values, keep the base model in tact
	 * @param agreementDTO
	 * @param validAgreementValuesDTO
	 */
	public void updateModel(AgreementDTO agreementDTO, ValidAgreementValuesDTO validAgreementValuesDTO) {
		if (getMaintainAgreementDTO()==null) {
			MaintainAgreementDTO maintainAgreementDTO = new MaintainAgreementDTO();
			maintainAgreementDTO.setWorkflowDTO(new WorkflowDTO());
			setMaintainAgreementDTO(maintainAgreementDTO);
		}
		@SuppressWarnings("unused")
		AgreementDTO currentAgreementDTO = getMaintainAgreementDTO().getAgreementDTO();
		getMaintainAgreementDTO().setAgreementDTO(agreementDTO);
		setValidAgreementValues(validAgreementValuesDTO);
		setViewTemplate(new AgreementTemplate(agreementDTO));
		setPreviousMaintainAgreementDTO(
				(MaintainAgreementDTO) SerializationUtils.clone(maintainAgreementDTO));
	}
	
	Date getCurrentDate() {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.HOUR, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		Date dateToday = c.getTime();	
		return dateToday;
	}

	public int getCurrentTab() {
		return currentTab;
	}

	public void setCurrentTab(int currentTab) {
		this.currentTab = currentTab;
	}

//	public static long getSerialVersionUID() {
//		return serialVersionUID;
//	}

	public long getAgreementNo() {
		return agreementNo;
	}

	public void setAgreementNo(long agreementNo) {
		this.agreementNo = agreementNo;
	}

	public String getUacfId() {
		return uacfId;
	}

	public void setUacfId(String uacfId) {
		this.uacfId = uacfId;
	}

	/**
	 * @return the maintainAgreementDTO
	 */
	public MaintainAgreementDTO getMaintainAgreementDTO() {
		return maintainAgreementDTO;
	}

	/**
	 * @param maintainAgreementDTO
	 *            the maintainAgreementDTO to set
	 */
	public void setMaintainAgreementDTO(
			MaintainAgreementDTO maintainAgreementDTO) {
		this.maintainAgreementDTO = maintainAgreementDTO;
	}

	public ValidAgreementValuesDTO getValidAgreementValues() {
		return validAgreementValues;
	}

	public void setValidAgreementValues(
			ValidAgreementValuesDTO validAgreementValues) {
		this.validAgreementValues = validAgreementValues;
	}

	public ServicingPanelDTO getSelectedItem() {
		return selectedItem;
	}

	public List<ServicingPanelDTO> getSelectionList() {
		return null;
	}

	public void setSelectedItem(ServicingPanelDTO selected) {
		this.selectedItem=selected;
	}

	public MaintainAgreementDTO getPreviousMaintainAgreementDTO() {
		return previousMaintainAgreementDTO;
	}

	public void setPreviousMaintainAgreementDTO(
			MaintainAgreementDTO previousMaintainAgreementDTO) {
		this.previousMaintainAgreementDTO = previousMaintainAgreementDTO;
	}

	public ContextDrivenViewTemplate<AgreementGUIField, AgreementDTO> getViewTemplate() {
		return viewTemplate;
	}

	public void setViewTemplate(
			ContextDrivenViewTemplate<AgreementGUIField, AgreementDTO> viewTemplate) {
		this.viewTemplate = viewTemplate;
	}

	public List<RequestDTO> getOutstandingAuthorisations() {
		return outstandingAuthorisations;
	}

	public void setOutstandingAuthorisations(
			List<RequestDTO> outstandingAuthorisations) {
		this.outstandingAuthorisations = outstandingAuthorisations;
	}

	public boolean isAgreementKindChangeEnabled() {
		return agreementKindChangeEnabled;
	}

	public void setAgreementKindChangeEnabled(boolean agreementKindChangeEnabled) {
		this.agreementKindChangeEnabled = agreementKindChangeEnabled;
	}
	
	public List<AgreementRoleGridDTO> getGridRoles() {
		return gridRoles;
	}	
	
	/**
	 * Warning, do not call this method in the gui when working with the roles, rather call the addGridRole</br>
	 * One call should be done to set the grid roles
	 * @param roles
	 */
	public void setGridRoles(List<AgreementRoleGridDTO> roles) {
		gridRoles = roles;
	}
	
	/**
	 * Call this to add role
	 * @param role
	 */
	public void addGridRole(AgreementRoleGridDTO role){
		if(gridRoles == null){
			gridRoles = new ArrayList<AgreementRoleGridDTO>();			
		}		
		gridRoles.add(role);	
		//also add to the DTO
		this.getMaintainAgreementDTO().getAgreementDTO().getCurrentAndFutureAgreementRoles().add(role.getRole());
	}
	
	/**
	 * Call this to add role
	 * @param role
	 */
	public void removeGridRole(AgreementRoleGridDTO role){
		if(gridRoles == null){
			return;			
		}
		gridRoles.remove(role);	
		//also remove from the dto
		this.getMaintainAgreementDTO().getAgreementDTO().getCurrentAndFutureAgreementRoles().remove(role.getRole());
	}
	
	public List<TypeVO> getServicingTypes() {
		return servicingTypes;
	}


	public void setServicingTypes(List<TypeVO> servicingTypes) {
		this.servicingTypes = servicingTypes;
	}


	public List<RoleKindType> getSelectableRoleKinds() {
		return selectableRoleKinds;
	}


	public void setSelectableRoleKinds(List<RoleKindType> selectableRoleKinds) {
		this.selectableRoleKinds = selectableRoleKinds;
	}
	
	/**
	 * get the servicing type for this typeid
	 * @param typeID
	 * @return
	 */
	public TypeVO getServicingType(long typeID){
		for(TypeVO type : this.getServicingTypes()){
			if(type.getOid() == typeID){
				return type;
			}
		}
		return null;
	}


	public BankingDetailsRequiredSelection getBankingDetailsRequiredSelection() {
		return bankingDetailsRequiredSelection;
	}


	public void setBankingDetailsRequiredSelection(
			BankingDetailsRequiredSelection bankingDetailsRequiredSelection) {
		this.bankingDetailsRequiredSelection = bankingDetailsRequiredSelection;
	}

	/**
	 * Only used for add agreement.
	 * 
	 * @return
	 */
	public boolean isPartyHasBankingDetails() {
		return isPartyHasBankingDetails;
	}

	public void setPartyHasBankingDetails(boolean isPartyHasBankingDetails) {
		this.isPartyHasBankingDetails = isPartyHasBankingDetails;
	}


	public List<TypeVO> getSupervisorTypes() {
		return supervisorTypes;
	}


	public void setSupervisorTypes(List<TypeVO> supervisorTypes) {
		this.supervisorTypes = supervisorTypes;
	}


	public List<AgreementRoleGridDTO> getOtherPartyGridRoles() {
		return otherPartyGridRoles;
	}


	public void setOtherPartyGridRoles(
			List<AgreementRoleGridDTO> otherPartyGridRoles) {
		this.otherPartyGridRoles = otherPartyGridRoles;
	}
	
	/**
	 * Call this to add role
	 * @param role
	 */
	public void addOtherPartyGridRole(AgreementRoleGridDTO role){
		if(otherPartyGridRoles == null){
			otherPartyGridRoles = new ArrayList<AgreementRoleGridDTO>();			
		}		
		otherPartyGridRoles.add(role);	
		//also add to the DTO
		if(this.getMaintainAgreementDTO().getAgreementDTO().getCurrentAndFutureSupervisorRoles() == null)
		{
			this.getMaintainAgreementDTO().getAgreementDTO().setCurrentAndFutureSupervisorRoles(new ArrayList<AgreementRoleDTO>());
		}
		this.getMaintainAgreementDTO().getAgreementDTO().getCurrentAndFutureSupervisorRoles().add(role.getRole());
	}
	
	/**
	 * Call this to add role
	 * @param role
	 */
	public void removeOtherPartyGridRole(AgreementRoleGridDTO role){
		if(otherPartyGridRoles == null){
			return;			
		}
		otherPartyGridRoles.remove(role);	
		//also remove from the dto
		this.getMaintainAgreementDTO().getAgreementDTO().getCurrentAndFutureSupervisorRoles().remove(role.getRole());
	}


	public List<RoleKindType> getSelectableSupervisionRoleKinds() {
		return selectableSupervisionRoleKinds;
	}


	public void setSelectableSupervisionRoleKinds(List<RoleKindType> selectableSupervisionRoleKinds) {
		this.selectableSupervisionRoleKinds = selectableSupervisionRoleKinds;
	}
	
	/**
	 * get the servicing type for this typeid
	 * @param typeID
	 * @return
	 */
	public TypeVO getSupervisorType(long typeID){
		for(TypeVO type : this.getSupervisorTypes()){
			if(type.getOid() == typeID){
				return type;
			}
		}
		return null;
	}


	public List<SalesCategoryDTO> getDivisionsList() {
		return divisionsList;
	}


	public void setDivisionsList(List<SalesCategoryDTO> divisionsList) {
		this.divisionsList = divisionsList;
	}


	public List<SalesCategoryDTO> getSalesCategoryList() {
		return salesCategoryList;
	}


	public void setSalesCategoryList(List<SalesCategoryDTO> salesCategoryList) {
		this.salesCategoryList = salesCategoryList;
	}


	public List<SalesCategoryDTO> getAgreementKindList() {
		return agreementKindList;
	}


	public void setAgreementKindList(List<SalesCategoryDTO> agreementKindList) {
		this.agreementKindList = agreementKindList;
	}
	
	// SSM2707 Hierarchy FR3.4 FAIS Details SWETA MENON Begin
	public void setAllowFAIS(boolean allowFAIS) {
		this.allowFAIS = allowFAIS;
	}

	public boolean isAllowFAIS() {
		return allowFAIS;
	}
	// SSM2707 Hierarchy FR3.4 FAIS Details SWETA MENON End


	public void setSelectedItem(MaintainAgreementDTO selected) {
		// TODO Auto-generated method stub
		
	}


	public void setSelectionList(List<ServicingPanelDTO> selectionList) {
		// TODO Auto-generated method stub
		
	}
}
