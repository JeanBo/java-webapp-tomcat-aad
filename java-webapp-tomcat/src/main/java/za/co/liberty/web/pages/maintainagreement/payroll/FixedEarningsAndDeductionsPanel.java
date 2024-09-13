package za.co.liberty.web.pages.maintainagreement.payroll;

import org.apache.wicket.Page;
import org.apache.wicket.markup.html.form.Form;

import za.co.liberty.dto.agreement.AgreementDTO;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.data.enums.fields.AgreementGUIField;
import za.co.liberty.web.pages.interfaces.ISecurityPanel;
import za.co.liberty.web.pages.maintainagreement.model.FixedDeductionsPanelModel;
import za.co.liberty.web.pages.maintainagreement.model.FixedEarningsPanelModel;
import za.co.liberty.web.pages.maintainagreement.model.MaintainAgreementPageModel;
import za.co.liberty.web.pages.panels.ViewTemplateBasePanel;
import za.co.liberty.web.wicket.validation.maintainagreement.PayrollEarningsDeductionsFormValidator;
import za.co.liberty.web.wicket.view.ContextDrivenViewTemplate;

/**
 * This class represents a GUI view of all earnings and deductions panels
 * 
 * @author kxd1203
 *
 */
public class FixedEarningsAndDeductionsPanel extends ViewTemplateBasePanel<AgreementGUIField, AgreementDTO> 
											 implements ISecurityPanel {

	private static final long serialVersionUID = 5830146178644786829L;
	
	MaintainAgreementPageModel pageModel;
	private FixedDeductionsPanel deductionsPanel;
	private FixedEarningsPanel earningsPanel;
	private boolean initialized = false;
	
	public FixedEarningsAndDeductionsPanel(String id, 
			EditStateType editState,MaintainAgreementPageModel pageModel) {
		this(id,editState,pageModel,null);
	}

	public FixedEarningsAndDeductionsPanel(String id, 
			EditStateType editState,MaintainAgreementPageModel pageModel,
			Page parentPage) {
		super(id, editState, parentPage);
		this.pageModel = pageModel;
	}
	
	@Override
	protected void onBeforeRender() {
		super.onBeforeRender();
		if (!initialized) {
			this.add(new DeductionsForm("fixedEarningsAndDeductionsForm"));
			initialized = true;
		}
	}
	
	private class DeductionsForm extends Form {

		private static final long serialVersionUID = 1L;

		public DeductionsForm(String id) {
			super(id);
			initComponents();
			add(new PayrollEarningsDeductionsFormValidator(getEditState(),
					pageModel.getMaintainAgreementDTO().getAgreementDTO()));
		}

		private void initComponents() {
			add(getDeductionsPanel());
			add(getEarningsPanel());
		}

	}

	/**
	 * Get the deductions panel. If an instance object does not 
	 * yet exist then one is created.
	 * @return
	 */
	public FixedDeductionsPanel getDeductionsPanel() {
		if (deductionsPanel == null) {
			FixedDeductionsPanelModel panelModel = 
				new FixedDeductionsPanelModel(pageModel);
			deductionsPanel = new FixedDeductionsPanel(
					"deductionsPanel",
					getEditState(),
					panelModel);
		}
		return deductionsPanel;
	}
	
	/**
	 * Get the earnings panel. If an instance object does not yet 
	 * exist then one is created
	 * 
	 * @return
	 */
	public FixedEarningsPanel getEarningsPanel() {
		if (earningsPanel == null) {
			FixedEarningsPanelModel panelModel = 
				new FixedEarningsPanelModel(pageModel);
			earningsPanel = new FixedEarningsPanel(
					"earningsPanel",
					getEditState(),
					panelModel);
		}
		return earningsPanel;
	}

	@Override
	protected ContextDrivenViewTemplate<AgreementGUIField, AgreementDTO> getViewTemplate() {
		return pageModel!=null
			?pageModel.getViewTemplate()
					:null;
	}

	@Override
	protected AgreementDTO getViewTemplateContext() {
		return pageModel!=null && pageModel.getMaintainAgreementDTO()!=null
			?pageModel.getMaintainAgreementDTO().getAgreementDTO()
					:null;
	}

	public Class getPanelClass() {
		return getClass(); 
	}

}
