package za.co.liberty.web.pages.maintainagreement.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import za.co.liberty.dto.agreement.AgreementDTO;
import za.co.liberty.dto.agreement.AgreementRoleDTO;
import za.co.liberty.dto.agreement.maintainagreement.MaintainFAISLicenseDTO;
import za.co.liberty.dto.agreement.maintainagreement.ValidFAISLicenseValuesDTO;
import za.co.liberty.dto.agreement.maintainagreement.fais.FAISLicensePanelGridDTO;
import za.co.liberty.dto.agreement.properties.FAISLicenseDTO;
import za.co.liberty.helpers.util.DateUtil;
import za.co.liberty.interfaces.agreements.RoleKindType;
import za.co.liberty.web.data.enums.fields.AgreementGUIField;
import za.co.liberty.web.pages.maintainagreement.template.AgreementTemplate;
import za.co.liberty.web.wicket.view.ContextDrivenViewTemplate;

/**
 * This class represents the model that will be used for the FAIS license panel.
 * 
 * @author kxd1203
 * 
 */
public class FAISLicensePanelModel implements Serializable {

	private static final long serialVersionUID = -1653141120807831898L;

	private List<FAISLicensePanelGridDTO> selectedFaisCategoryList;

	private Collection<FAISLicensePanelGridDTO> selectableFaisCategoryList;
	
	private List<AgreementRoleDTO> currentAndFutureSupervisionRoles;
	/**
	 * FAIS License details
	 */
	private FAISLicenseDTO faisLicenseDTO;
	/**
	 * 
	 */
	private Date agreementStartDate;
	short currentStatus;
	boolean isFSBUpdated;
	boolean isFsp;
	
	/**
	 * Valid values for FAIS license details
	 */

	private ValidFAISLicenseValuesDTO validFaisLicenseValues;

	/**
	 * View template
	 */
	private ContextDrivenViewTemplate<AgreementGUIField, AgreementDTO> viewTemplate;
	
	private FAISLicenseDTO fspFAISLicence;
	
	/**
	 * 
	 */
	private Long belongsToAgmtNumber;
	
	private int agreementKind;
	
	private Long agreementNumber;
	

	/**
	 * Construct a new FAIS license panel model with the required parameters
	 * 
	 * @param faisLicenseDTO
	 *            FAIS license details
	 * @param validFaisLicenseValues
	 *            valid FAIS license details values
	 * @param viewTemplate
	 */
	
	private FAISLicensePanelModel(
			FAISLicenseDTO faisLicenseDTO,
			ValidFAISLicenseValuesDTO validFaisLicenseValues,
			ContextDrivenViewTemplate<AgreementGUIField, AgreementDTO> viewTemplate, Long belongsToAgmtNumber,
			int agreementKind, Long agreementNumber, Date agreementStartDate) {
		super();
		this.faisLicenseDTO = faisLicenseDTO;
		this.validFaisLicenseValues = validFaisLicenseValues;
		this.viewTemplate = viewTemplate;
		this.belongsToAgmtNumber = belongsToAgmtNumber;
		this.agreementKind = agreementKind;
		this.agreementNumber = agreementNumber;
		this.agreementStartDate = agreementStartDate;
		
		
	}
	//this constructor is called from AuthorisationPanel, FSPLicensepanel
	public FAISLicensePanelModel(MaintainFAISLicenseDTO maintainFAISLicenseDTO, 
			Long belongsToAgmtNumber, int agreementKind,Long agreementNumber, 
			Date agreementStartDate) {
		this(maintainFAISLicenseDTO != null ? maintainFAISLicenseDTO
				.getRequestDTO() : null, new ValidFAISLicenseValuesDTO(),
				maintainFAISLicenseDTO != null ? new AgreementTemplate(
						maintainFAISLicenseDTO.getAgreementContext()) : null
						,belongsToAgmtNumber,agreementKind,agreementNumber, agreementStartDate);
	}
	//this constructor is called from AddAgreementWizard and when initializing tabs
	public FAISLicensePanelModel(MaintainAgreementPageModel pageModel) {
		AgreementDTO agmtDTO = (pageModel != null
		&& pageModel.getMaintainAgreementDTO() != null
		&& pageModel.getMaintainAgreementDTO().getAgreementDTO() != null) ? pageModel
		.getMaintainAgreementDTO().getAgreementDTO() : null;
		
		this.faisLicenseDTO = agmtDTO != null ? agmtDTO.getFaisLicense()
				: null;

		//agreementDTO = pageModel.getMaintainAgreementDTO().getAgreementDTO();

		this.validFaisLicenseValues = pageModel != null
				&& pageModel.getValidAgreementValues() != null ? pageModel
				.getValidAgreementValues().getValidFAISLicenseValues() : null;
		this.viewTemplate = pageModel != null ? pageModel.getViewTemplate()
				: null;
		//get belongs to using the pagemodel
		
		if(agmtDTO != null && agmtDTO.getCurrentAndFutureAgreementRoles() != null){
			//go through roles and get the belongs to
			DateUtil util = DateUtil.getInstance();
			for(AgreementRoleDTO role : agmtDTO.getCurrentAndFutureAgreementRoles()){
				if(role.getKind() == RoleKindType.BELONGSTO.getKind()
						&& util.compareDatePart(role.getEffectiveFrom(), new Date()) <= 0 
						&& (role.getEffectiveTo() == null ||
								util.compareDatePart(role.getEffectiveTo(),new Date()) > 0)){
					//check the end date, if current use this agmt number and break out
					this.belongsToAgmtNumber = role.getRolePlayerID();
					break;
				}				
			}
			//get the agmt kind
			this.agreementKind = agmtDTO.getKind();
		}
		if(agmtDTO.getCurrentAndFutureSupervisorRoles() ==null){
			agmtDTO.setCurrentAndFutureSupervisorRoles(new ArrayList<AgreementRoleDTO>());
		}
		this.currentAndFutureSupervisionRoles=agmtDTO.getCurrentAndFutureSupervisorRoles();
		
		this.agreementStartDate=agmtDTO.getStartDate();
		this.agreementNumber = agmtDTO.getId();
	}

	public Long getAgreementNumber() {
		return agreementNumber;
	}
	public void setAgreementNumber(Long agreementNumber) {
		this.agreementNumber = agreementNumber;
	}
	public FAISLicenseDTO getFaisLicenseDTO() {
		return faisLicenseDTO;
	}

	public void setFaisLicenseDTO(FAISLicenseDTO faisLicenseDTO) {
		this.faisLicenseDTO = faisLicenseDTO;
	}

	public ValidFAISLicenseValuesDTO getValidFaisLicenseValues() {
		return validFaisLicenseValues;
	}

	public void setValidFaisLicenseValues(
			ValidFAISLicenseValuesDTO validFaisLicenseValues) {
		this.validFaisLicenseValues = validFaisLicenseValues;
	}

	public ContextDrivenViewTemplate<AgreementGUIField, AgreementDTO> getViewTemplate() {
		return viewTemplate;
	}

	public void setViewTemplate(
			ContextDrivenViewTemplate<AgreementGUIField, AgreementDTO> viewTemplate) {
		this.viewTemplate = viewTemplate;
	}

	public List<FAISLicensePanelGridDTO> getSelectedFaisCategoryList() {
		return selectedFaisCategoryList;
	}

	public void setSelectedFaisCategoryList(List<FAISLicensePanelGridDTO> selectedFaisCategoryList) {
		
		this.selectedFaisCategoryList = selectedFaisCategoryList;
	}

		public Collection<FAISLicensePanelGridDTO> getSelectableFaisCategoryList() {
		return selectableFaisCategoryList;
	}

	public void setSelectableFaisCategoryList(
			Collection<FAISLicensePanelGridDTO> selectableFaisCategoryList) {
		this.selectableFaisCategoryList = selectableFaisCategoryList;
	}

	public Long getBelongsToAgmtNumber() {
		return belongsToAgmtNumber;
	}

	public void setBelongsToAgmtNumber(Long belongsToAgmtNumber) {
		this.belongsToAgmtNumber = belongsToAgmtNumber;
	}

	public FAISLicenseDTO getFspFAISLicence() {
		return fspFAISLicence;
	}

	public void setFspFAISLicence(FAISLicenseDTO fspFAISLicence) {
		this.fspFAISLicence = fspFAISLicence;
	}

	public int getAgreementKind() {
		return agreementKind;
	}

	public void setAgreementKind(int agreementKind) {
		this.agreementKind = agreementKind;
	}

	
	public boolean isFsp() {
		return isFsp;
	}

	public void setFsp(boolean isFsp) {
		this.isFsp = isFsp;
	}

	public boolean isFSBUpdated() {
		return isFSBUpdated;
	}

	public void setFSBUpdated(boolean isFSBUpdated) {
		this.isFSBUpdated = isFSBUpdated;
	}

	public short getCurrentStatus() {
		return currentStatus;
	}

	public void setCurrentStatus(short currentStatus) {
		this.currentStatus = currentStatus;
	}

	public List<AgreementRoleDTO> getCurrentAndFutureSupervisionRoles() {
		return currentAndFutureSupervisionRoles;
	}

	public void setCurrentAndFutureSupervisionRoles(
			List<AgreementRoleDTO> currentAndFutureSupervisionRoles) {
		this.currentAndFutureSupervisionRoles = currentAndFutureSupervisionRoles;
	}

	
    public AgreementRoleDTO getActiveSupervisorRole(long categoryTypeOid){
    	if(currentAndFutureSupervisionRoles!=null){
	    	for (AgreementRoleDTO agreementRoleDTO: currentAndFutureSupervisionRoles){
	    		
	    		if(agreementRoleDTO.getType()==categoryTypeOid ){
	    			if(agreementRoleDTO.getEffectiveTo()==null || DateUtil.getInstance().getDatePart(agreementRoleDTO.getEffectiveTo()).after(DateUtil.getInstance().getDatePart(new Date()))){
	    			return agreementRoleDTO;
	    		}
	    	}
    	}
    	}
    	return null;
    }

	public Date getAgreementStartDate() {
		return agreementStartDate;
	}

	public void setAgreementStartDate(Date agreementStartDate) {
		this.agreementStartDate = agreementStartDate;
	}
	

	

}
