package za.co.liberty.web.pages.maintainagreement;

import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.apache.wicket.Page;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.validation.IFormValidator;
import org.apache.wicket.markup.repeater.RepeatingView;

import za.co.liberty.business.guicontrollers.agreement.IAgreementGUIController;
import za.co.liberty.dto.agreement.AgreementDTO;
import za.co.liberty.exceptions.data.ValidationException;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.helpers.lookup.ServiceLocator;
import za.co.liberty.interfaces.agreements.AgreementCodeType;
import za.co.liberty.web.data.enums.ComponentType;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.data.enums.fields.AgreementGUIField;
import za.co.liberty.web.pages.interfaces.ISecurityPanel;
import za.co.liberty.web.pages.maintainagreement.model.AgreementCodePanelModel;
import za.co.liberty.web.pages.maintainagreement.model.MaintainAgreementPageModel;
import za.co.liberty.web.pages.panels.HelperPanel;
import za.co.liberty.web.pages.panels.ViewTemplateBasePanel;
import za.co.liberty.web.wicket.validation.maintainagreement.AgreementCodeValidator;
import za.co.liberty.web.wicket.view.ContextDrivenViewTemplate;

/**
 * This class represents the Agreement Codes Panel for the Maintain Agreement Page
 * @author kxd1203
 *
 */
public class AgreementCodesPanel extends ViewTemplateBasePanel<AgreementGUIField, AgreementDTO> 
								 implements ISecurityPanel {
	
	private static final long serialVersionUID = -4289050102598622086L;

	private AgreementCodesForm pageForm;
	
	private transient IAgreementGUIController guiController;
	
	private static final Logger logger = Logger.getLogger(AgreementCodesPanel.class);

	AgreementCodePanelModel panelModel;
	
	HelperPanel compassCodePanel;
	
	HelperPanel mastheadMemberNumberPanel;
	
	HelperPanel lcbQuantumCodePanel;
	
	HelperPanel libertyActivePanel;
	
	HelperPanel stanlibLinkedBusinessCodePanel;
	
	HelperPanel stanlibUnitTrustCodePanel;
	
	HelperPanel providentFundNumberPanel;
	
	HelperPanel medschemePanel;
	
	HelperPanel bankConsultantCodePanel;
	
	HelperPanel standardBankBondAccountNumberPanel1;
	
	HelperPanel standardBankBondAccountNumberPanel2;
	
	HelperPanel standardBankBondAccountNumberPanel3;
	
	HelperPanel standardBankBondAccountNumberPanel4;
	
	HelperPanel standardBankBondAccountNumberPanel5;
	
	HelperPanel riskFundCode;
	
	HelperPanel stanlibOffshoreUnitTrustCode;

	private boolean initialised = false;

	private AgreementDTO viewTemplateContext;

	public AgreementCodesPanel(String id, EditStateType editState,
			MaintainAgreementPageModel pageModel) {
		this(id,editState,(AgreementCodePanelModel)null);
		setPageModel(pageModel);
	}
	
	public AgreementCodesPanel(String id, EditStateType editState,
			AgreementCodePanelModel panelModel) {
		this(id,editState,panelModel,null);
	}

	public AgreementCodesPanel(String id, EditStateType editState,
			AgreementCodePanelModel panelModel,Page parentPage) {
		super(id, editState,parentPage);
		this.panelModel = panelModel;
	}
	
	public void setPageModel(MaintainAgreementPageModel pageModel) {
		this.panelModel = new AgreementCodePanelModel(pageModel);
	}

	@Override
	protected void onBeforeRender() {
		super.onBeforeRender();
		if (!initialised) {
			add(getAgreementCodesForm());
			initialised=true;
		}
	}
	
	private AgreementDTO getContext() {
		return getViewTemplateContext();
	}
	
	private HelperPanel getCompassCodePanel() {
		if (compassCodePanel==null) {
			compassCodePanel = createGUIPageField(AgreementGUIField.COMPASS_CODE, 
					getContext(), ComponentType.TEXTFIELD, true);
			addAgreementCodeValidatorToComponent(
					compassCodePanel,
					AgreementCodeType.COMPASS);
		}
		return compassCodePanel;
	}
	
	private HelperPanel getMastheadMemberNumberPanel() {
		if (mastheadMemberNumberPanel==null) {
			mastheadMemberNumberPanel = createGUIPageField(AgreementGUIField.MASTHEAD_MEMBER_NUMBER,
					getContext(),
					ComponentType.TEXTFIELD, true);
		}
		return mastheadMemberNumberPanel;
	}
	
	private HelperPanel getLCBQuantumCodePanel() {
		if (lcbQuantumCodePanel==null) {
			lcbQuantumCodePanel = createGUIPageField(AgreementGUIField.LCB_QUANTUM_CODE,
					getContext(),
					ComponentType.TEXTFIELD, true);
			addAgreementCodeValidatorToComponent(lcbQuantumCodePanel,AgreementCodeType.LCB_QUANTUM);
		}
		return lcbQuantumCodePanel;
	}

	private HelperPanel getLibertyActivePanel() {
		if (libertyActivePanel==null) {
			libertyActivePanel = createGUIPageField(AgreementGUIField.LIBERTY_ACTIVE_CODE, 
					getContext(), ComponentType.TEXTFIELD, true);
			addAgreementCodeValidatorToComponent(libertyActivePanel,AgreementCodeType.LIBERTY_ACTIVE);
		}
		return libertyActivePanel;
	}
	
	private HelperPanel getStanlibUnitTrustPanel() {
		if (stanlibUnitTrustCodePanel==null) {
			stanlibUnitTrustCodePanel = createGUIPageField(AgreementGUIField.STANLIB_UNIT_TRUST_CODE, 
					getContext(), ComponentType.TEXTFIELD, true);
			addAgreementCodeValidatorToComponent(
					stanlibUnitTrustCodePanel,
					AgreementCodeType.STANLIB_UNIT_TRUST);
		}
		return stanlibUnitTrustCodePanel;
	}
	
	private HelperPanel getProvidentFundNumberPanel() {
		if (providentFundNumberPanel==null) {
			providentFundNumberPanel = createGUIPageField(AgreementGUIField.PROVIDENT_FUND_NUMBER, 
					getContext(), ComponentType.TEXTFIELD, true);
			/**
			 * PROVIDENT FUND VALIDATION DISABLED, AS PER THE ORIGINAL CODE COMMENTS BELOW
			 * SZA0208. 30.03.2006.
			 * Comment this code because: Provident fund number should now allowed to be duplicated again,
			 * as it needs to reflect against more than one agreement.
			 */
//			addAgreementCodeValidatorToComponent(
//					providentFundNumberPanel,
//					AgreementCodeType.PROVIDENT_FUND_NO);
		}
		return providentFundNumberPanel;
	}
	
	private HelperPanel getMedschemePanel() {
		if (medschemePanel==null) {
			medschemePanel = createGUIPageField(AgreementGUIField.MEDSCHEME_CODE, 
					getContext(), ComponentType.TEXTFIELD, true);
			addAgreementCodeValidatorToComponent(
					medschemePanel,
					AgreementCodeType.MEDSCHEME);
		}
		return medschemePanel;
	}
	
	private HelperPanel getStanlibLinkedBusinessCodePanel() {
		if (stanlibLinkedBusinessCodePanel==null) {
			stanlibLinkedBusinessCodePanel = createGUIPageField(AgreementGUIField.STANLIB_LINKED_BUSINESS_CODE, 
					getContext(), ComponentType.TEXTFIELD, true);
			addAgreementCodeValidatorToComponent(
					stanlibLinkedBusinessCodePanel,
					AgreementCodeType.STANLIB_LINKED_BUSINESS);
		}
		return stanlibLinkedBusinessCodePanel;
	}
	
	private HelperPanel getBankConsultantCodePanel() {
		if (bankConsultantCodePanel==null) {
			bankConsultantCodePanel = createGUIPageField(AgreementGUIField.BANK_CONSULTANT_CODE, 
					getContext(), ComponentType.TEXTFIELD, true);
		}
		return bankConsultantCodePanel;
	}
	
	private HelperPanel getStandardBankBondAccountNumberPanel1() {
		if (standardBankBondAccountNumberPanel1==null) {
			standardBankBondAccountNumberPanel1 = createGUIPageField(
					AgreementGUIField.STANDARD_BANK_BOND_ACC_NUMBER_1, 
					getContext(), ComponentType.TEXTFIELD, true);
		}
		return standardBankBondAccountNumberPanel1;
	}
	
	private HelperPanel getStandardBankBondAccountNumberPanel2() {
		if (standardBankBondAccountNumberPanel2==null) {
			standardBankBondAccountNumberPanel2 = createGUIPageField(
					AgreementGUIField.STANDARD_BANK_BOND_ACC_NUMBER_2, 
					getContext(), ComponentType.TEXTFIELD, true);
		}
		return standardBankBondAccountNumberPanel2;
	}
	
	private HelperPanel getStandardBankBondAccountNumberPanel3() {
		if (standardBankBondAccountNumberPanel3==null) {
			standardBankBondAccountNumberPanel3 = createGUIPageField(
					AgreementGUIField.STANDARD_BANK_BOND_ACC_NUMBER_3, 
					getContext(), ComponentType.TEXTFIELD, true);
		}
		return standardBankBondAccountNumberPanel3;
	}
	
	private HelperPanel getStandardBankBondAccountNumberPanel4() {
		if (standardBankBondAccountNumberPanel4==null) {
			standardBankBondAccountNumberPanel4 = createGUIPageField(
					AgreementGUIField.STANDARD_BANK_BOND_ACC_NUMBER_4, 
					getContext(), ComponentType.TEXTFIELD, true);
		}
		return standardBankBondAccountNumberPanel4;
	}
	
	private HelperPanel getStandardBankBondAccountNumberPanel5() {
		if (standardBankBondAccountNumberPanel5==null) {
			standardBankBondAccountNumberPanel5 = createGUIPageField(
					AgreementGUIField.STANDARD_BANK_BOND_ACC_NUMBER_5, 
					getContext(), ComponentType.TEXTFIELD, true);
		}
		return standardBankBondAccountNumberPanel5;
	}
	
	private HelperPanel getRiskFundCode(){
		if(riskFundCode==null){
			riskFundCode = createGUIPageField(AgreementGUIField.RISK_FUND_CODE, getContext(), ComponentType.TEXTFIELD, true);
		}
		return riskFundCode;
	}
	
	private HelperPanel getStanlibOffshoreUnitTrustCode(){
		if(stanlibOffshoreUnitTrustCode==null){
			stanlibOffshoreUnitTrustCode = createGUIPageField(AgreementGUIField.STANLIB_OFFSHORE_UNIT_TRUST_CODE, getContext(), ComponentType.TEXTFIELD, true);
		}
		return stanlibOffshoreUnitTrustCode;
	}
	
	
	private AgreementCodesForm getAgreementCodesForm() {
		if (pageForm==null) {
			pageForm = new AgreementCodesForm("pageForm");
		}
		return pageForm;
	}
	
	private class AgreementCodesForm extends Form {

		public AgreementCodesForm(String id) {
			super(id);
			initComponents();
			
			this.add(new IFormValidator() {
				private static final long serialVersionUID = 1L;

				
				public void validate(Form form) {
					if (getEditState() == EditStateType.VIEW) {
						return;
					}					
					boolean validate = true;
//					for(FormComponent comp : validationComponents){
//						if(!comp.isValid()){
//							validate = false;
//						} 
//					}
					if(validate){
						try{				
							//validate party without contact details
							getGuiController().validateAgreementCodes(panelModel.getAgreementCodes(), panelModel.getAgreementId(), panelModel.getAgreementPartyID());
						}catch(ValidationException ex){
							for(String error : ex.getErrorMessages()){
								AgreementCodesForm.this.error(error);								
							}
						}
					}
					
				}

				public FormComponent[] getDependentFormComponents() {
					return null;
				}				
			});
		}

		private void initComponents() {
			RepeatingView leftPanel = new RepeatingView("leftPanel");
			RepeatingView rightPanel = new RepeatingView("rightPanel");
			add(leftPanel);
			add(rightPanel);
			/**
			 * Left panel
			 */
			leftPanel.add(createGUIFieldPanel(AgreementGUIField.COMPASS_CODE, 
					getCompassCodePanel()));
			leftPanel.add(createGUIFieldPanel(AgreementGUIField.LCB_QUANTUM_CODE, 
					getLCBQuantumCodePanel()));
			leftPanel.add(createGUIFieldPanel(AgreementGUIField.STANLIB_LINKED_BUSINESS_CODE,
					getStanlibLinkedBusinessCodePanel()));
			leftPanel.add(createGUIFieldPanel(AgreementGUIField.BANK_CONSULTANT_CODE,
					getBankConsultantCodePanel()));
			leftPanel.add(createGUIFieldPanel(AgreementGUIField.STANDARD_BANK_BOND_ACC_NUMBER_1, 
					getStandardBankBondAccountNumberPanel1()));
			/**
			 * Add the remaining Standard Bank Bond Account Numbers will no labels
			 */
			leftPanel.add(createGUIFieldPanel(AgreementGUIField.STANDARD_BANK_BOND_ACC_NUMBER_2, "", 
					getStandardBankBondAccountNumberPanel2().getEnclosedObject(),false));
			leftPanel.add(createGUIFieldPanel(AgreementGUIField.STANDARD_BANK_BOND_ACC_NUMBER_3, "",
					getStandardBankBondAccountNumberPanel3().getEnclosedObject(),false));
			leftPanel.add(createGUIFieldPanel(AgreementGUIField.STANDARD_BANK_BOND_ACC_NUMBER_4, "",
					getStandardBankBondAccountNumberPanel4().getEnclosedObject(),false));
			leftPanel.add(createGUIFieldPanel(AgreementGUIField.STANDARD_BANK_BOND_ACC_NUMBER_5, "",
					getStandardBankBondAccountNumberPanel5().getEnclosedObject(),false));
			/**
			 * Right panel
			 */
			rightPanel.add(createGUIFieldPanel(AgreementGUIField.LIBERTY_ACTIVE_CODE, 
					getLibertyActivePanel()));
			rightPanel.add(createGUIFieldPanel(AgreementGUIField.MEDSCHEME_CODE,
					getMedschemePanel()));
			rightPanel.add(createGUIFieldPanel(AgreementGUIField.STANLIB_UNIT_TRUST_CODE, 
					getStanlibUnitTrustPanel()));
			rightPanel.add(createGUIFieldPanel(AgreementGUIField.PROVIDENT_FUND_NUMBER, 
					getProvidentFundNumberPanel()));
			rightPanel.add(createGUIFieldPanel(AgreementGUIField.MASTHEAD_MEMBER_NUMBER, 
					getMastheadMemberNumberPanel()));
			rightPanel.add(createGUIFieldPanel(AgreementGUIField.RISK_FUND_CODE,
					getRiskFundCode()));
			rightPanel.add(createGUIFieldPanel(AgreementGUIField.STANLIB_OFFSHORE_UNIT_TRUST_CODE,
					getStanlibOffshoreUnitTrustCode()));
			/**
			 * Set field visibility
			 */
			checkFieldVisibility();
		}
		
	}
	
	@Override
	protected ContextDrivenViewTemplate<AgreementGUIField, AgreementDTO> getViewTemplate() {
		return panelModel.getViewTemplate();
	}

	@Override
	protected AgreementDTO getViewTemplateContext() {
		if (viewTemplateContext == null) {
			viewTemplateContext = new AgreementDTO();
			viewTemplateContext.setId(panelModel.getAgreementId());
			viewTemplateContext.setAssociatedCodes(panelModel.getAgreementCodes());
		}
		return viewTemplateContext;
	}

	/**
	 * Convenience method to attach a Agreement Code Validator of a specific kind
	 * to the component helper panel's enclosed text field, if the enclosed object is 
	 * an instance of text field
	 * @param componentPanel
	 * @param agreementCodeType
	 */
	private void addAgreementCodeValidatorToComponent(
			HelperPanel componentPanel,
			AgreementCodeType agreementCodeType) {
		if (componentPanel.getEnclosedObject() instanceof TextField) {
			addAgreementCodeValidatorToComponent(
					(TextField)componentPanel.getEnclosedObject(),
					agreementCodeType);
		}
	}
	
	/**
	 * Method to attacha a Agreement Code Validator of a specific kind 
	 * to the component text field.
	 */
	private void addAgreementCodeValidatorToComponent(
			TextField componentField,
			AgreementCodeType agreementCodeType) {
		componentField.add(
				new AgreementCodeValidator(
						agreementCodeType,
						panelModel.getAgreementId()));
	}

	public Class getPanelClass() {
		return getClass();
	}
	
	/**
	 * Load the AgreementGUIController dynamically if it is null as this is a transient variable.
	 * @return {@link IAgreementGUIController}
	 */
	private IAgreementGUIController getGuiController() {
		if (guiController==null) {
			try {
				guiController = ServiceLocator.lookupService(IAgreementGUIController.class);
			} catch (NamingException e) {
				logger.fatal("Could not lookup AgreementGUIController",e);
				throw new CommunicationException("Could not lookup AgreementGUIController",e);
			}
		}
		return guiController;
	}

}
