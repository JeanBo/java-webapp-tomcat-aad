package za.co.liberty.web.pages.maintainagreement.model;

import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import za.co.liberty.dto.agreement.AgreementDTO;
import za.co.liberty.dto.agreement.maintainagreement.MaintainFixedEarningsDTO;
import za.co.liberty.dto.agreement.maintainagreement.ValidPayrollValuesDTO;
import za.co.liberty.dto.agreement.payroll.FixedDeductionDTO;
import za.co.liberty.dto.agreement.payroll.FixedEarningDTO;
import za.co.liberty.dto.agreement.payroll.OnePercentBondDTO;
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
public class FixedEarningsPanelModel implements Serializable {
	
	/**
	 * Model DTO
	 */
	private List<FixedEarningDTO> fixedEarnings;
	
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
	
	public FixedEarningsPanelModel() {
		super();
	}
	
	public FixedEarningsPanelModel(MaintainFixedEarningsDTO maintainFixedEarningsDTO) {
		this.fixedEarnings = (maintainFixedEarningsDTO!=null && 
				maintainFixedEarningsDTO.getRequestDTO()!=null
				?maintainFixedEarningsDTO.getRequestDTO()
				:Collections.EMPTY_LIST);
		ValidPayrollValuesDTO validPayrollValues = new ValidPayrollValuesDTO();
		validPayrollValues.setValidBondPercentages(Collections.EMPTY_LIST);
		validPayrollValues.setValidDeductionTypes(Collections.EMPTY_LIST);
		validPayrollValues.setValidDeductionTypes(Collections.EMPTY_LIST);
		this.validPayrollValues = (validPayrollValues);
		this.viewTemplateContext = new AgreementDTO();
		this.viewTemplateContext.setFixedEarnings(this.fixedEarnings);
		this.viewTemplate = new AgreementTemplate(
				maintainFixedEarningsDTO.getAgreementContext()!=null
				?maintainFixedEarningsDTO.getAgreementContext():null);
	}
	
	public FixedEarningsPanelModel(MaintainAgreementPageModel pageModel) {
		this.fixedEarnings = 
			pageModel!=null && pageModel.getMaintainAgreementDTO()!=null &&
			pageModel.getMaintainAgreementDTO().getAgreementDTO()!=null
			?pageModel.getMaintainAgreementDTO().getAgreementDTO().getFixedEarnings()
			:null;
		this.validPayrollValues = 
			pageModel!=null && pageModel.getValidAgreementValues()!=null
			?pageModel.getValidAgreementValues().getValidPayrollValues()
			:null;
		this.viewTemplateContext = pageModel!=null && pageModel.getMaintainAgreementDTO()!=null
			?pageModel.getMaintainAgreementDTO().getAgreementDTO()
					:null;
		this.viewTemplate = pageModel!=null
			?pageModel.getViewTemplate():null;
	}
	
	public void updateModelForTerminate() {
		if (fixedEarnings!=null) {
			for (FixedEarningDTO dto : fixedEarnings) {
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

	public ValidPayrollValuesDTO getValidPayrollValues() {
		return validPayrollValues;
	}

	public void setValidPayrollValues(ValidPayrollValuesDTO validPayrollValues) {
		this.validPayrollValues = validPayrollValues;
	}

	public List<FixedEarningDTO> getFixedEarnings() {
		return fixedEarnings;
	}

	public void setFixedEarnings(List<FixedEarningDTO> fixedEarnings) {
		this.fixedEarnings = fixedEarnings;
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
