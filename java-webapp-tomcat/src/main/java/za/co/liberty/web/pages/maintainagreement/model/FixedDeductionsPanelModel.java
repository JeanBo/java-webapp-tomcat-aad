package za.co.liberty.web.pages.maintainagreement.model;

import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import za.co.liberty.dto.agreement.AgreementDTO;
import za.co.liberty.dto.agreement.maintainagreement.MaintainFixedDeductionsDTO;
import za.co.liberty.dto.agreement.maintainagreement.ValidPayrollValuesDTO;
import za.co.liberty.dto.agreement.payroll.FixedDeductionDTO;
import za.co.liberty.dto.agreement.payroll.FixedEarningDTO;
import za.co.liberty.srs.util.DateUtil;
import za.co.liberty.web.data.enums.fields.AgreementGUIField;
import za.co.liberty.web.pages.maintainagreement.template.AgreementTemplate;
import za.co.liberty.web.wicket.view.ContextDrivenViewTemplate;

/**
 * This class represents a panel model for the Fixed Deductions panel.
 * 
 * @author kxd1203
 *
 */
public class FixedDeductionsPanelModel implements Serializable {
	
	/**
	 * Model DTO
	 */
	private List<FixedDeductionDTO> fixedDeductions;
	
	/**
	 * Valid Values
	 */
	private ValidPayrollValuesDTO validPayrollValues;
	
	/**
	 * View template
	 */
	private ContextDrivenViewTemplate<AgreementGUIField, AgreementDTO> viewTemplate;
	
	/**
	 * View Template Context
	 */
	private AgreementDTO viewTemplateContext;
	
	public FixedDeductionsPanelModel() {
		super();
	}
	
	@SuppressWarnings("unchecked")
	public FixedDeductionsPanelModel(MaintainFixedDeductionsDTO maintainFixedDeductionsDTO) {
		this.fixedDeductions = 
			(maintainFixedDeductionsDTO!=null && 
			 maintainFixedDeductionsDTO.getRequestDTO()!=null
				?maintainFixedDeductionsDTO.getRequestDTO()
				:Collections.EMPTY_LIST);
		ValidPayrollValuesDTO validPayrollValues = new ValidPayrollValuesDTO();
		validPayrollValues.setValidBondPercentages(Collections.EMPTY_LIST);
		validPayrollValues.setValidDeductionTypes(Collections.EMPTY_LIST);
		validPayrollValues.setValidDeductionTypes(Collections.EMPTY_LIST);
		this.validPayrollValues = validPayrollValues;
		this.viewTemplate = new AgreementTemplate(maintainFixedDeductionsDTO!=null
			?maintainFixedDeductionsDTO.getAgreementContext():null);
		this.viewTemplateContext = new AgreementDTO();
		this.viewTemplateContext.setFixedDeductions(this.fixedDeductions);
	}
	
	public FixedDeductionsPanelModel(MaintainAgreementPageModel pageModel) {
		this.fixedDeductions = 
			pageModel!=null && pageModel.getMaintainAgreementDTO()!=null &&
			pageModel.getMaintainAgreementDTO().getAgreementDTO()!=null
			?pageModel.getMaintainAgreementDTO().getAgreementDTO().getFixedDeductions()
				:null;
		this.validPayrollValues = 
			pageModel!=null
			?pageModel.getValidAgreementValues().getValidPayrollValues()
					:null;
		this.viewTemplate =
			pageModel!=null
			?pageModel.getViewTemplate()
					:null;
		this.viewTemplateContext=
			pageModel!=null && pageModel.getMaintainAgreementDTO()!=null
				?pageModel.getMaintainAgreementDTO().getAgreementDTO():null;
	}
	
	public void updateModelForTerminate() {
		if (fixedDeductions!=null) {
			for (FixedDeductionDTO dto : fixedDeductions) {
				if (dto.isModifyEndDateEnabled() && dto.getEnd()==null) {
					dto.setEnd(DateUtil.minimizeTime(new Date()));
				}
			}
		}
	}

	/**
	 * this field contains the sequence number for 
	 * new items to be added to the list of 
	 * payroll entries
	 */
	private long guiSequence = 0;
	
	/**
	 * Get the next sequence number to use for adding new
	 * items to the fixed deductions list
	 * @return the next usable sequence number
	 */
	public long getNextGUISequence() {
		return ++guiSequence;
	}

	public List<FixedDeductionDTO> getFixedDeductions() {
		return fixedDeductions;
	}

	public void setFixedDeductions(List<FixedDeductionDTO> fixedDeductions) {
		this.fixedDeductions = fixedDeductions;
	}

	public ValidPayrollValuesDTO getValidPayrollValues() {
		return validPayrollValues;
	}

	public void setValidPayrollValues(ValidPayrollValuesDTO validPayrollValues) {
		this.validPayrollValues = validPayrollValues;
	}

	public ContextDrivenViewTemplate<AgreementGUIField, AgreementDTO> getViewTemplate() {
		return viewTemplate;
	}

	public void setViewTemplate(
			ContextDrivenViewTemplate<AgreementGUIField, AgreementDTO> viewTemplate) {
		this.viewTemplate = viewTemplate;
	}

	public AgreementDTO getViewTemplateContext() {
		return viewTemplateContext;
	}

	public void setViewTemplateContext(AgreementDTO viewTemplateContext) {
		this.viewTemplateContext = viewTemplateContext;
	}
	
}
