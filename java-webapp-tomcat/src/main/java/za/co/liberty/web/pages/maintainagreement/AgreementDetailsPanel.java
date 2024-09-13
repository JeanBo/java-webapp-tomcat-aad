package za.co.liberty.web.pages.maintainagreement;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import javax.naming.NamingException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow.MaskType;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.validation.IFormValidator;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.convert.IConverter;
import org.apache.wicket.validation.validator.DateValidator;

import za.co.liberty.business.agreement.IAgreementManagement;
import za.co.liberty.business.guicontrollers.agreement.IAgreementGUIController;
import za.co.liberty.dto.agreement.AgreementDTO;
import za.co.liberty.dto.agreement.AgreementHomeRoleDTO;
import za.co.liberty.dto.agreement.AgreementRoleDTO;
import za.co.liberty.dto.agreement.maintainagreement.MaintainAgreementDTO;
import za.co.liberty.dto.agreement.maintainagreement.ValidAgreementValuesDTO;
import za.co.liberty.dto.agreement.properties.EarlyDebitsReasonDTO;
import za.co.liberty.dto.agreement.properties.PrimaryAgreementDTO;
import za.co.liberty.dto.common.IDValueDTO;
import za.co.liberty.dto.common.TimePeriod;
import za.co.liberty.dto.party.PartyDTO;
import za.co.liberty.dto.party.taxdetails.TrustCompDTO;
import za.co.liberty.dto.spec.ActualLifeCycleStatusDTO;
import za.co.liberty.exceptions.data.DataNotFoundException;
import za.co.liberty.exceptions.data.ValidationException;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.helpers.lookup.ServiceLocator;
import za.co.liberty.interfaces.agreements.AgreementKindType;
import za.co.liberty.interfaces.agreements.AgreementStatusType;
import za.co.liberty.interfaces.agreements.requests.RequestKindType;
import za.co.liberty.persistence.party.IPartyEntityManager;
import za.co.liberty.srs.util.DateUtil;
import za.co.liberty.web.data.enums.ComponentType;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.data.enums.fields.AgreementGUIField;
import za.co.liberty.web.pages.interfaces.ISecurityPanel;
import za.co.liberty.web.pages.maintainagreement.model.AgreementDetailsPanelModel;
import za.co.liberty.web.pages.maintainagreement.model.MaintainAgreementPageModel;
import za.co.liberty.web.pages.panels.GUIFieldPanel;
import za.co.liberty.web.pages.panels.HelperPanel;
import za.co.liberty.web.pages.panels.ViewTemplateBasePanel;
import za.co.liberty.web.system.SRSAuthWebSession;
import za.co.liberty.web.wicket.convert.converters.YesNoBooleanConverter;
import za.co.liberty.web.wicket.markup.html.form.GenericConversionLabel;
import za.co.liberty.web.wicket.markup.html.form.SRSAbstractChoiceRenderer;
import za.co.liberty.web.wicket.markup.html.form.SRSDateField;
import za.co.liberty.web.wicket.markup.html.form.SRSDropDownChoice;
import za.co.liberty.web.wicket.model.NullReplacementPropertyModel;
import za.co.liberty.web.wicket.renderer.YesNoBooleanChoiceRenderer;
import za.co.liberty.web.wicket.validation.FormattedValidationDate;
import za.co.liberty.web.wicket.validation.maintainagreement.AgreementEndDateValidator;
import za.co.liberty.web.wicket.validation.maintainagreement.AgreementStartDateJobTitleValidator;
import za.co.liberty.web.wicket.validation.maintainagreement.AgreementStatusDateValidator;
import za.co.liberty.web.wicket.validation.maintainagreement.ConsultantCodeValidator;
import za.co.liberty.web.wicket.validation.maintainagreement.EarlyDebitsReasonValidator;
import za.co.liberty.web.wicket.validation.maintainagreement.MyBankingNumberValidator;
import za.co.liberty.web.wicket.validation.maintainagreement.OrgMidSixValidator;
import za.co.liberty.web.wicket.view.ContextDrivenViewTemplate;

/**
 * This panel represents the Agreement Details panel as one of the TABS of
 * Maintain Agreement Panel.
 * 
 * @author pks2802
 * 
 */
public class AgreementDetailsPanel extends ViewTemplateBasePanel<AgreementGUIField, AgreementDTO>
		implements ISecurityPanel {

	private static final String LIBERTY_BRANCH_FRANCHISE_LBF = "Liberty Branch Franchise (LBF)";

	private static final long serialVersionUID = 1L;

	private MaintainAgreementPageModel currentPageModel;

	private MaintainAgreementPageModel maintainAgreementPageModel;

	private String salesCategory;

	private String partyChoice;
	private Long primaryAgreementNumber;
	private AgreementDetailsForm pageForm;

	EditStateType editState;

	@SuppressWarnings("unused")
	private WebMarkupContainer agreementNumberName;

	@SuppressWarnings("unused")
	private HelperPanel agreementNumberPanel;

	@SuppressWarnings("unused")
	private WebMarkupContainer consultantCodeName;

	private HelperPanel consultantCodePanel;

	private HelperPanel krollCheckPanel;

	private WebMarkupContainer fitAndProperContainer;

	private HelperPanel fitPopWaiverRECheckPanel;

	private HelperPanel fitPopWaiverRECheckEndDatePanel;

	private HelperPanel fitPopWaiverCPDCheckPanel;

	private HelperPanel fitPopWaiverCPDCheckEndDatePanel;

	private HelperPanel fitPopWaiverProductAccreditationCheckPanel;

	private HelperPanel fitPopWaiverProductAccreditationCheckEndDatePanel;

	private HelperPanel fitPopWaiverFAISAdviserCheckPanel;

	private HelperPanel fitPopWaiverFAISAdviserCheckEndDatePanel;

	private HelperPanel fitPopWaiverFAISFSPCheckPanel;

	private HelperPanel fitPopWaiverFAISFSPCheckEndDatePanel;

	private HelperPanel fitPropWaiverAdviceCheckPanel;

	private HelperPanel fitPropWaiverAdviceCheckEndDatePanel;

	private HelperPanel fitPropWaiverIntermediaryServiceCheckPanel;

	private HelperPanel fitPropWaiverIntermediaryServiceCheckEndDatePanel;

	private HelperPanel fitPopSegmentPanel;

	private Panel hasMedicalAidCreditsPanel;

	private HelperPanel stopStatementDistributionPanel;

	private HelperPanel scriptedAdvisorCheckPanel;

	private HelperPanel hasMedicalAidCreditsStartDatePanel;

	private HelperPanel hasMedicalAidCreditsEndDatePanel;

	private HelperPanel manProdClubStatusPanel;

	private HelperPanel manualProdClubStatusEndDatePanel;

	private HelperPanel calcProdClubStatusPanel;

	@SuppressWarnings("unused")
	private WebMarkupContainer entityName;

	private HelperPanel entityPanel;

	private HelperPanel networkPanel;

	@SuppressWarnings("unused")
	private WebMarkupContainer statusName;

	private HelperPanel statusPanel;

	private HelperPanel statusReasonPanel;

	private HelperPanel endDatePanel;

	private HelperPanel startDatePanel;

	private HelperPanel statusDatePanel;

	private HelperPanel productionClubStatusPanel;

	private HelperPanel brokerConsultantProductionClubStatusPanel;

	private HelperPanel manpowerPanel;

	private HelperPanel libertyTenurePanel; // SSM2707 ADDED for FR15 Tenure
											// SWETA MENON

	private HelperPanel earlyDebitsReasonPanel;

	private HelperPanel earlyDebitsIndicatorPanel;

	private HelperPanel earlyDebitsStartDatePanel;

	private HelperPanel primaryCompanyContractedToPanel;

	private HelperPanel dedicatedSBFCConsultantTypePanel;

	private HelperPanel titularLevelPanel;

	private HelperPanel supportTypePanel;

	private HelperPanel segmentPanel;

	private HelperPanel bbfGroupPanel;

	private HelperPanel monthlyGuaranteedAmountPanel;

	private HelperPanel costCenterPanel;

	private HelperPanel preAuthLimitCategoryPanel;

	private HelperPanel preAuthLimitAmoutPanel;

	private HelperPanel preAuthOverridePanel;

	private GUIFieldPanel manualProdClubGuiFieldPanel;

	private transient IAgreementGUIController guiController;

	private transient IAgreementManagement agreementManagement;

	private transient IPartyEntityManager partyEntityManager;

	private transient Logger logger;

	private WebMarkupContainer earlyDebitsContainer;

	private RepeatingView earlyDebitsPanel;

	private WebMarkupContainer authLimitsContainer;

	private IConverter earlyDebitsConverter;

	private HelperPanel directManpowerPanel;

	public GUIFieldPanel endDateGUIPanel;

	private AgreementStartDateJobTitleValidator agreementStartDateJobTitleValidator;

	@SuppressWarnings("unused")
	private PartyDTO partyContext;

	private AgreementDetailsPanelModel panelModel;
	private HelperPanel myBankingNumPanel;

	private boolean initialised;

	// Added for SE 492-pritam-01/06/2011
	private HelperPanel doNotCalculateIntPanel;

	// Added for LCB Accreditation Project-Pritam-12/12/12
	private HelperPanel corpAddendumSignedPanel;
	private HelperPanel holdCorpCommissionPanel;
	// Added for Tax project-IRP5 Bi-Annual submission-pks2802-20/08/13
	private HelperPanel soleProprietorPanel;

	// RXS 1408 ADDED for FR2 INCLUDE In Manpower Reporting - RAVISH SEHGAL
	private HelperPanel includeInManpowerReporting;

	//// RXS1408 - Changes to disable checkboxs when there is raised request.
	private boolean getOutstandingRequests = false;

	// RXS 1408 ADDED for Hierarchy FR3.6 Employee Number - RAVISH SEHGAL
	private HelperPanel employeeNumber;
	// RXS 1408 ADDED for Hierarchy FR3.2 Sales Category - RAVISH SEHGAL
	private HelperPanel salesCategoryPanel;
	// MZL 2611 ADDED for FR2.8.2 LBF Remuneration Category - MOHAMMED LORGAT
	private HelperPanel lbfRemunerationCategoryPanel;

	private HelperPanel lbfHomeAddPanel;

	private GUIFieldPanel lbfButtonGUIFieldPanel;

	private ModalWindow addLBFWindow;

	private HelperPanel primaryAgreementPanel; // SSM2707 ADDED for Hierarchy
												// FR3.5 Primary Agreement -
												// SWETA MENON

	private HelperPanel suretyDetailsPanel; // PZM2509 BBBEE LIB4352

	private Panel isPersonalServicesTrustPanel; // PZM2509 BBBEE LIB4352

	private TrustCompDTO trustCompDTO;

	private HelperPanel orgMidSixPanel;//SBS0510
	
	//PZM2509 New to industry 
	private HelperPanel subsidyPanel;
	
	private HelperPanel subsidyStartDatePanel;
	
	private HelperPanel subsidyEndDatePanel;
	
	private HelperPanel subsidyAmountPanel;
	
	private boolean validateSalesCat; //validateSalesCat
	
	private boolean validateEditState; //validateEditState
	
	private static final String NEW_TO_INDUSTRY = "New To Industry Advisor"; // LZL1012

	public AgreementDetailsPanel(String id, EditStateType editState, AgreementDetailsPanelModel panelModel) {
		this(id, editState, panelModel, null);
	}

	/**
	 * @param arg0
	 */
	public AgreementDetailsPanel(String id, EditStateType editState, AgreementDetailsPanelModel panelModel,
			Page parentPage) {
		super(id, editState, parentPage);
		this.editState = editState;
		this.panelModel = panelModel;
		getOutStandingRequest();
	}

	@Override
	protected void onBeforeRender() {
		super.onBeforeRender();
		if (!initialised) {
			add(getPageForm());
			initialised = true;
		}
	}

	public void updatePartyContext(PartyDTO partyDTO) {
		partyContext = partyDTO;
		if (agreementStartDateJobTitleValidator != null) {
			agreementStartDateJobTitleValidator.setPartyDTO(partyDTO);
		}
	}

	/**
	 * Load the AgreementGUIController dynamically if it is null as this is a
	 * transient variable.
	 * 
	 * @return {@link IAgreementGUIController}
	 */
	private IAgreementGUIController getGuiController() {
		if (guiController == null) {
			try {
				guiController = ServiceLocator.lookupService(IAgreementGUIController.class);
			} catch (NamingException e) {
				getLogger().fatal("Could not lookup AgreementGUIController", e);
				throw new CommunicationException("Could not lookup AgreementGUIController", e);
			}
		}
		return guiController;
	}

	@Override
	protected void filterRequestsToBeUsedForSecurityCheckInMaintain(List<RequestKindType> listOfPanelRequests) {
		if (listOfPanelRequests != null) {
			listOfPanelRequests.remove(RequestKindType.ActivateAgreement);
			listOfPanelRequests.remove(RequestKindType.TerminateIntermediaryAgreement);
			listOfPanelRequests.remove(RequestKindType.CreateAgreement);
		}
	}

	/**
	 * Get the logger for this class, instantiate if necessary as this is a
	 * transient field
	 * 
	 * @return
	 */
	private Logger getLogger() {
		if (logger == null) {
			logger = Logger.getLogger(this.getClass());
		}
		return logger;
	}

	private AgreementDTO getContext() {
		return panelModel.getAgreement();
	}

	protected AgreementDTO getViewTemplateContext() {
		return panelModel.getAgreement();
	}

	protected ContextDrivenViewTemplate<AgreementGUIField, AgreementDTO> getViewTemplate() {
		if (getEditState() == EditStateType.MODIFY
				&& SRSAuthWebSession.get().getSessionUser().isUserLimitedToDebtFields()) {

			getLogger().info("getViewTemplate - user is limited to DebtFields");

			final ContextDrivenViewTemplate<AgreementGUIField, AgreementDTO> template = panelModel.getViewTemplate();
			return new ContextDrivenViewTemplate<AgreementGUIField, AgreementDTO>() {

				private static final long serialVersionUID = 1L;

				@Override
				public boolean isAddable(AgreementGUIField field, AgreementDTO context) {
					return template.isAddable(field, context);
				}

				@Override
				public boolean isModifiable(AgreementGUIField field, AgreementDTO context) {
					return (field == AgreementGUIField.DONOTCALCINTEREST);
					// return template.isModifiable(field, context);
				}

				@Override
				public boolean isModifiableForTerminate(AgreementGUIField field, AgreementDTO context) {
					return (field == AgreementGUIField.DONOTCALCINTEREST);
					// return template.isModifiableForTerminate(field, context);
				}

				@Override
				public boolean isRequired(AgreementGUIField field, AgreementDTO context) {
					return template.isRequired(field, context);
				}

				@Override
				public boolean isViewable(AgreementGUIField field, EditStateType editState, AgreementDTO context) {
					return template.isViewable(field, editState, context);
				}

				@Override
				public void setOutstandingRequests(List<RequestKindType> outstandingRequests) {
					template.setOutstandingRequests(outstandingRequests);
				}

			};
		}
		return panelModel.getViewTemplate();
	}

	@SuppressWarnings("unchecked")
	private Component getPageForm() {
		if (pageForm == null) {
			pageForm = new AgreementDetailsForm("searchForm");
			pageForm.add(new IFormValidator() {
				private static final long serialVersionUID = 1L;

				public void validate(Form arg0) {
					if (getEditState() == EditStateType.VIEW) {
						return;
					}
					boolean validate = true;
					if (validate) {
						validateForm();
					}

				}

				public FormComponent[] getDependentFormComponents() {
					return null;
				}
			});
		}
		return pageForm;
	}

	/**
	 * Validations to be done on form.
	 */
	public void validateForm() {
		try {
			// validate party without contact details
			getGuiController().validateWaiverDates(panelModel.getAgreement());
			getGuiController().validateMedicalAidCreditIndicator(panelModel.getAgreement());
			// getGuiController().validateWaiverDates(panelModel.getAgreement());
			getGuiController().validateManualProductionClubStatusProperty(panelModel.getAgreement());
			// Validate Sole Proprietor
			getGuiController().validateSoleProprietorRules(panelModel.getAgreement());
			getGuiController().validateScriptedAdvisor(panelModel.getAgreement());
			//New to industry end date validation
			getGuiController().validateNewToIndustry(panelModel.getAgreement());
			// TODO - RXS 1408 ADDED for FR2 INCLUDE In Manpower Reporting -
			// RAVISH SEHGAL
			if (getEditState() == EditStateType.ADD) {
				getGuiController().validateIncludeInManpowerReportingRulesAddAgmt(panelModel.getAgreement(),
						partyContext);
			} else if (getEditState() == EditStateType.MODIFY) {
				getGuiController().validateIncludeInManpowerReportingRulesMaintainAgmt(panelModel.getAgreement());
			}
			// RXS 1408 ADDED for Hierarchy FR3.6 EmployeeNumber - RAVISH SEHGAL
			getGuiController().validateEmployeeNumber(panelModel.getAgreement());
			// MZL 2611 ADDED for Hierarchy FR2.8.4 LBF Remuneration Category -
			// MOHAMMED LORGAT
			if (panelModel.getAgreement().getKind() == AgreementKindType.BROKER_BRANCH_FRANCHISE.getKind()
					&& panelModel.getAgreement().getSalesCategory().equalsIgnoreCase(LIBERTY_BRANCH_FRANCHISE_LBF)) {
				validateLBFRemuneration();
			}
			
		} catch (ValidationException ex) {
			for (String error : ex.getErrorMessages()) {
				AgreementDetailsPanel.this.error(error);
			}
		}
	}

	/**
	 * Validate LBF remuneration
	 * 
	 * @throws ValidationException
	 */
	private void validateLBFRemuneration() throws ValidationException {
		List<RequestKindType> outStandingRequestTypesForPanel = new ArrayList<RequestKindType>();

		try {
			if (((MaintainAgreementPage) AgreementDetailsPanel.this.getParentPage())
					.getOutStandingRequestTypesForPanel(AgreementHierarchyLBFPanel.class) != null) {

				outStandingRequestTypesForPanel = (List<RequestKindType>) ((MaintainAgreementPage) AgreementDetailsPanel.this
						.getParentPage()).getOutStandingRequestTypesForPanel(AgreementHierarchyLBFPanel.class);
			}
		} catch (NullPointerException npe) {

			outStandingRequestTypesForPanel = new ArrayList<RequestKindType>();
		}

		try {
			getGuiController().validateLBFRemuration(panelModel.getAgreement());

			if (outStandingRequestTypesForPanel.size() > 0
					&& outStandingRequestTypesForPanel.contains(RequestKindType.BranchTransfer)) {
				// do not validate

			} else {
				// if(panelModel.getAgreement().getCurrentAndFutureAgreementRoles()
				// != null){
				getGuiController().validateLBFHomeRole(panelModel.getAgreement());
				// }
			}

		} catch (ValidationException ve) {
			try {
				if (outStandingRequestTypesForPanel.size() > 0
						&& outStandingRequestTypesForPanel.contains(RequestKindType.BranchTransfer)) {
					// do not validate
					throw ve;
				} else {
					getGuiController().validateLBFHomeRole(panelModel.getAgreement());
					throw ve;

				}
			} catch (ValidationException ve2) {
				ve2.getErrorMessages().addAll(ve.getErrorMessages());
				throw ve2;
			}
		}
	}

	@SuppressWarnings({ "unchecked", "unused" })
	private <T> T getEnclosingObject(HelperPanel panel, Class<T> targetClass) {
		T ret = null;
		try {
			ret = (T) panel.getEnclosedObject();
		} catch (ClassCastException e) {
			ret = null;
		}
		return ret;
	}

	@SuppressWarnings("unused")
	private WebMarkupContainer createFieldLabel(String id) {
		WebMarkupContainer container = new WebMarkupContainer(id);
		container.setOutputMarkupId(true);
		container.setOutputMarkupPlaceholderTag(true);
		return container;
	}

	@SuppressWarnings("unchecked")
	private HelperPanel createDropDownChoicePanel(AgreementGUIField field, SRSDropDownChoice dropDownChoice) {
		return createDropDownChoicePanel(field, dropDownChoice, null);
	}

	@SuppressWarnings("unchecked")
	private HelperPanel createDropDownChoicePanel(AgreementGUIField field, SRSDropDownChoice dropDownChoice,
			Label label) {
		/**
		 * Check if AJAX behaviour is added to the dropdown yet
		 */
		boolean ajaxBehaviourLoaded = false;
		for (Object behaviour : dropDownChoice.getBehaviors()) {
			if (behaviour instanceof AjaxFormComponentUpdatingBehavior) {
				ajaxBehaviourLoaded = true;
			}
		}
		/**
		 * Add AJAX behaviour IF it is not already added
		 */
		if (!ajaxBehaviourLoaded) {
			dropDownChoice.add(new AjaxFormComponentUpdatingBehavior("change") {
				private static final long serialVersionUID = 1L;

				@Override
				protected void onUpdate(AjaxRequestTarget arg0) {
					if (getLogger().isDebugEnabled()) {
						getLogger().debug("Default AJAX behaviour for dropdown called");
					}
				}
			});
		}
		dropDownChoice.setOutputMarkupId(true);
		dropDownChoice.setRequired(getViewTemplate().isRequired(field, getContext()));
		dropDownChoice.setLabel(new Model(field.getDescription()));
		HelperPanel panel = createGUIPageField(field, getContext(), dropDownChoice, label);
		dropDownChoice.setOutputMarkupId(true);
		panel.setOutputMarkupId(true);
		return panel;
	}

	@SuppressWarnings("unchecked")
	private HelperPanel getConsultantCodePanel() {
		if (consultantCodePanel == null) {
			consultantCodePanel = createGUIPageField(AgreementGUIField.CONSULTANT_CODE, panelModel.getAgreement(),
					ComponentType.TEXTFIELD, false);
			if (consultantCodePanel.getEnclosedObject() instanceof TextField) {
				TextField field = (TextField) consultantCodePanel.getEnclosedObject();
				field.add(new ConsultantCodeValidator(panelModel.getAgreement().getId()));
			}
		}
		return consultantCodePanel;
	}

	private HelperPanel getKrollCheckPanel() {
		if (krollCheckPanel == null) {
			krollCheckPanel = createGUIPageField(AgreementGUIField.KROLL_DONE, panelModel.getAgreement(),
					ComponentType.CHECKBOX, false);
			final TimePeriod timePeriod = getGuiController().getCurrentHonestyAndIntegrityTimePeriod();
			if (krollCheckPanel.getEnclosedObject() instanceof CheckBox) {
				krollCheckPanel.getEnclosedObject().add(new AjaxFormComponentUpdatingBehavior("click") {

					private static final long serialVersionUID = 1L;

					@Override
					protected void onUpdate(AjaxRequestTarget target) {
						// update the property end date
						if (panelModel.getAgreement().getKrollCheckProperty() != null
								&& panelModel.getAgreement().getKrollCheckProperty().getValue() != null
								&& panelModel.getAgreement().getKrollCheckProperty().getValue()
								&& (panelModel.getAgreement().getKrollCheckProperty().getEffectiveFrom() == null
										|| DateUtil
												.minimizeTime(panelModel.getAgreement().getKrollCheckProperty()
														.getEffectiveFrom())
												.compareTo(DateUtil.minimizeTime(new Date())) == 0)) {
							// value is true and set now, we will adjust the
							// date to be ending on the configured month in
							// rating
							panelModel.getAgreement().getKrollCheckProperty().setEffectiveTo(timePeriod.getEnd());
						}
					}
				});
			}
		}
		return krollCheckPanel;
	}

	/**
	 * create Fit and proper waiver property field
	 * 
	 * @return
	 */
	private HelperPanel getFitPropWaiverRECheckPanel() {
		if (fitPopWaiverRECheckPanel == null) {
			fitPopWaiverRECheckPanel = createGUIPageField(AgreementGUIField.FITPROPWAIVERECHECK,
					panelModel.getAgreement(), ComponentType.CHECKBOX, true);
			fitPopWaiverRECheckPanel.setOutputMarkupId(true);
			fitPopWaiverRECheckPanel.getEnclosedObject().setOutputMarkupId(true);
		}
		return fitPopWaiverRECheckPanel;
	}

	/**
	 * create Fit and proper waiver property field end date
	 * 
	 * @return
	 */
	private HelperPanel getFitPropWaiverRECheckEndDatePanel() {
		if (fitPopWaiverRECheckEndDatePanel == null) {
			fitPopWaiverRECheckEndDatePanel = createGUIPageField(AgreementGUIField.FITPROPWAIVERECHECKEND,
					panelModel.getAgreement(), ComponentType.DATE_SELECTION_TEXTFIELD, true);
			fitPopWaiverRECheckEndDatePanel.setOutputMarkupId(true);
			fitPopWaiverRECheckEndDatePanel.getEnclosedObject().setOutputMarkupId(true);

	        //Santosh:DatePicker Fix		
			if (fitPopWaiverRECheckEndDatePanel.getEnclosedObject() instanceof SRSDateField) {
				fitPopWaiverRECheckEndDatePanel.getEnclosedObject().add(
				((SRSDateField)fitPopWaiverRECheckEndDatePanel.getEnclosedObject()).newDatePicker());
			}
			
		}
		return fitPopWaiverRECheckEndDatePanel;
	}

	/**
	 * create Fit and proper waiver property field
	 * 
	 * @return
	 */
	private HelperPanel getFitPropWaiverCPDCheckPanel() {
		if (fitPopWaiverCPDCheckPanel == null) {
			fitPopWaiverCPDCheckPanel = createGUIPageField(AgreementGUIField.FITPROPWAIVECPDCHECK,
					panelModel.getAgreement(), ComponentType.CHECKBOX, true);
			fitPopWaiverCPDCheckPanel.setOutputMarkupId(true);
			fitPopWaiverCPDCheckPanel.getEnclosedObject().setOutputMarkupId(true);
		}
		return fitPopWaiverCPDCheckPanel;
	}

	/**
	 * create Fit and proper waiver property field end date
	 * 
	 * @return
	 */
	private HelperPanel getFitPropWaiverCPDCheckEndDatePanel() {
		if (fitPopWaiverCPDCheckEndDatePanel == null) {
			fitPopWaiverCPDCheckEndDatePanel = createGUIPageField(AgreementGUIField.FITPROPWAIVECPDCHECKEND,
					panelModel.getAgreement(), ComponentType.DATE_SELECTION_TEXTFIELD, true);
			fitPopWaiverCPDCheckEndDatePanel.setOutputMarkupId(true);
			fitPopWaiverCPDCheckEndDatePanel.getEnclosedObject().setOutputMarkupId(true);
			//Santosh:DatePicker Fix		
			if (fitPopWaiverCPDCheckEndDatePanel.getEnclosedObject() instanceof SRSDateField) {
				fitPopWaiverCPDCheckEndDatePanel.getEnclosedObject().add(
				((SRSDateField)fitPopWaiverCPDCheckEndDatePanel.getEnclosedObject()).newDatePicker());
				}
		}
		return fitPopWaiverCPDCheckEndDatePanel;
	}

	/**
	 * create Fit and proper waiver property field
	 * 
	 * @return
	 */
	private HelperPanel getFitPropWaiverProductAccreditationCheckPanel() {
		if (fitPopWaiverProductAccreditationCheckPanel == null) {
			fitPopWaiverProductAccreditationCheckPanel = createGUIPageField(
					AgreementGUIField.FITPROPWAIVEPRODUCTACCREDCHECK, panelModel.getAgreement(), ComponentType.CHECKBOX,
					true);
			fitPopWaiverProductAccreditationCheckPanel.setOutputMarkupId(true);
			fitPopWaiverProductAccreditationCheckPanel.getEnclosedObject().setOutputMarkupId(true);
		}
		return fitPopWaiverProductAccreditationCheckPanel;
	}

	/**
	 * create Fit and proper waiver property field end date
	 * 
	 * @return
	 */
	private HelperPanel getFitPropWaiverProductAccreditationCheckEndDatePanel() {
		if (fitPopWaiverProductAccreditationCheckEndDatePanel == null) {
			fitPopWaiverProductAccreditationCheckEndDatePanel = createGUIPageField(
					AgreementGUIField.FITPROPWAIVEPRODUCTACCREDCHECKEND, panelModel.getAgreement(),
					ComponentType.DATE_SELECTION_TEXTFIELD, true);
			fitPopWaiverProductAccreditationCheckEndDatePanel.setOutputMarkupId(true);
			fitPopWaiverProductAccreditationCheckEndDatePanel.getEnclosedObject().setOutputMarkupId(true);
			//Santosh:DatePicker Fix		
			if (fitPopWaiverProductAccreditationCheckEndDatePanel.getEnclosedObject() instanceof SRSDateField) {
				fitPopWaiverProductAccreditationCheckEndDatePanel.getEnclosedObject().add(
				((SRSDateField)fitPopWaiverProductAccreditationCheckEndDatePanel.getEnclosedObject()).newDatePicker());
				}
			
		}
		return fitPopWaiverProductAccreditationCheckEndDatePanel;
	}

	/**
	 * create Fit and proper waiver property field
	 * 
	 * @return
	 */
	private HelperPanel getFitPropWaiverFAISAdviserCheckPanel() {
		if (fitPopWaiverFAISAdviserCheckPanel == null) {
			fitPopWaiverFAISAdviserCheckPanel = createGUIPageField(AgreementGUIField.FITPROPWAIVEFAISADVISORCHECK,
					panelModel.getAgreement(), ComponentType.CHECKBOX, true);
			fitPopWaiverFAISAdviserCheckPanel.setOutputMarkupId(true);
			fitPopWaiverFAISAdviserCheckPanel.getEnclosedObject().setOutputMarkupId(true);
		}
		return fitPopWaiverFAISAdviserCheckPanel;
	}

	/**
	 * create Fit and proper waiver property field end date
	 * 
	 * @return
	 */
	private HelperPanel getFitPropWaiverFAISAdviserCheckEndDatePanel() {
		if (fitPopWaiverFAISAdviserCheckEndDatePanel == null) {
			fitPopWaiverFAISAdviserCheckEndDatePanel = createGUIPageField(
					AgreementGUIField.FITPROPWAIVEFAISADVISORCHECKEND, panelModel.getAgreement(),
					ComponentType.DATE_SELECTION_TEXTFIELD, true);
			fitPopWaiverFAISAdviserCheckEndDatePanel.setOutputMarkupId(true);
			fitPopWaiverFAISAdviserCheckEndDatePanel.getEnclosedObject().setOutputMarkupId(true);
			//Santosh:DatePicker Fix		
			if (fitPopWaiverFAISAdviserCheckEndDatePanel.getEnclosedObject() instanceof SRSDateField) {
				fitPopWaiverFAISAdviserCheckEndDatePanel.getEnclosedObject().add(
				((SRSDateField)fitPopWaiverFAISAdviserCheckEndDatePanel.getEnclosedObject()).newDatePicker());
				}
		}
		return fitPopWaiverFAISAdviserCheckEndDatePanel;
	}

	/**
	 * create Fit and proper waiver property field
	 * 
	 * @return
	 */
	private HelperPanel getFitPropWaiverFAISFSPCheckPanel() {
		if (fitPopWaiverFAISFSPCheckPanel == null) {
			fitPopWaiverFAISFSPCheckPanel = createGUIPageField(AgreementGUIField.FITPROPWAIVEFAISFSPCHECK,
					panelModel.getAgreement(), ComponentType.CHECKBOX, true);
			fitPopWaiverFAISFSPCheckPanel.setOutputMarkupId(true);
			fitPopWaiverFAISFSPCheckPanel.getEnclosedObject().setOutputMarkupId(true);
		}
		return fitPopWaiverFAISFSPCheckPanel;
	}

	/**
	 * create Fit and proper waiver property field end date
	 * 
	 * @return
	 */
	private HelperPanel getfitPopWaiverFAISFSPCheckEndDatePanel() {
		if (fitPopWaiverFAISFSPCheckEndDatePanel == null) {
			fitPopWaiverFAISFSPCheckEndDatePanel = createGUIPageField(AgreementGUIField.FITPROPWAIVEFAISFSPCHECKEND,
					panelModel.getAgreement(), ComponentType.DATE_SELECTION_TEXTFIELD, true);
			fitPopWaiverFAISFSPCheckEndDatePanel.setOutputMarkupId(true);
			fitPopWaiverFAISFSPCheckEndDatePanel.getEnclosedObject().setOutputMarkupId(true);
			//Santosh:DatePicker Fix		
			if (fitPopWaiverFAISFSPCheckEndDatePanel.getEnclosedObject() instanceof SRSDateField) {
				fitPopWaiverFAISFSPCheckEndDatePanel.getEnclosedObject().add(
				((SRSDateField)fitPopWaiverFAISFSPCheckEndDatePanel.getEnclosedObject()).newDatePicker());
				}
		}
		return fitPopWaiverFAISFSPCheckEndDatePanel;
	}

	/**
	 * create Fit and proper waiver property field
	 * 
	 * @return
	 */
	private HelperPanel getFitPropWaiverAdviceCheckPanel() {
		if (fitPropWaiverAdviceCheckPanel == null) {
			fitPropWaiverAdviceCheckPanel = createGUIPageField(AgreementGUIField.FITPROPWAIVEADVICECHECK,
					panelModel.getAgreement(), ComponentType.CHECKBOX, true);
			fitPropWaiverAdviceCheckPanel.setOutputMarkupId(true);
			fitPropWaiverAdviceCheckPanel.getEnclosedObject().setOutputMarkupId(true);

		}
		return fitPropWaiverAdviceCheckPanel;
	}

	/**
	 * create Has Medical Aid Credits property field
	 * 
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	private Panel getHasMedicalAidCreditsPanelPanel() {
		// TODO Jean busy here
		if (hasMedicalAidCreditsPanel == null) {

			AgreementDetailsHasMedicalPanel panel = new AgreementDetailsHasMedicalPanel("value", panelModel,
					getEditState());
			panel.setOutputMarkupId(true);
			panel.setOutputMarkupPlaceholderTag(true);

			hasMedicalAidCreditsPanel = panel;
			hasMedicalAidCreditsPanel.setOutputMarkupId(true);
			// hasMedicalAidCreditsPanel.getEnclosedObject().setOutputMarkupId(true);

		}
		return hasMedicalAidCreditsPanel;
	}

	/**
	 * create Stop Statement Distribution property field
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private HelperPanel getStopStatementDistributionPanel() {
		if (stopStatementDistributionPanel == null) {

			CheckBox checkBox = new CheckBox("value",
					new PropertyModel(getContext(), "stopStatementDistribution.value"));
			checkBox.setOutputMarkupId(true);
			checkBox.setOutputMarkupPlaceholderTag(true);
			checkBox.add(new AjaxFormComponentUpdatingBehavior("change") {
				private static final long serialVersionUID = 1L;

				@Override
				protected void onUpdate(AjaxRequestTarget target) {
					// update the Model
				}
			});
			// if(getOutstandingRequests){
			checkBox.setEnabled(editState.equals(EditStateType.MODIFY) || editState.equals(EditStateType.ADD));
			// }else{
			// checkBox.setEnabled(false);
			// }
			stopStatementDistributionPanel = HelperPanel
					.getInstance(AgreementGUIField.STOPSTATEMENTDISTRIBUTION.getFieldId(), checkBox);
		}
		return stopStatementDistributionPanel;
	}
	
	/**
	 * create Scripted Advisor Check property field
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private HelperPanel getScriptedAdvisorCheckPanel() {
		if (scriptedAdvisorCheckPanel == null) {

			CheckBox checkBox = new CheckBox("value",
					new PropertyModel(getContext(), "scriptedAdvisorCheck.value"));
			checkBox.setOutputMarkupId(true);
			checkBox.setOutputMarkupPlaceholderTag(true);
			checkBox.add(new AjaxFormComponentUpdatingBehavior("change") {
				private static final long serialVersionUID = 1L;

				@Override
				protected void onUpdate(AjaxRequestTarget target) {
					// update the Model
				}
			});
			checkBox.setEnabled(editState.equals(EditStateType.MODIFY) || editState.equals(EditStateType.ADD));
			scriptedAdvisorCheckPanel = HelperPanel
					.getInstance(AgreementGUIField.SCRIPTEDADVISORCHECK.getFieldId(), checkBox);
		}
		return scriptedAdvisorCheckPanel;
	}

	/**
	 * create Surety Details property field
	 * 
	 * @return
	 */
	// PZM2509
	@SuppressWarnings("unchecked")
	private HelperPanel getSuretyDetailsPanel() {
		if (suretyDetailsPanel == null) {

			// System.out.println(AgreementGUIField.SURETYDETAILS.toString());

			CheckBox checkBox = new CheckBox("value", new PropertyModel(getContext(), "suretyDetails.value"));
			checkBox.setOutputMarkupId(true);
			checkBox.setOutputMarkupPlaceholderTag(true);
			checkBox.add(new AjaxFormComponentUpdatingBehavior("change") {
				private static final long serialVersionUID = 1L;

				@Override
				protected void onUpdate(AjaxRequestTarget target) {
					// update the Model
				}
			});
			// if(getOutstandingRequests){
			checkBox.setEnabled(editState.equals(EditStateType.MODIFY) || editState.equals(EditStateType.ADD));
			// }else{
			// checkBox.setEnabled(false);
			// }
			suretyDetailsPanel = HelperPanel.getInstance(AgreementGUIField.SURETYDETAILS.getFieldId(), checkBox);
		}
		return suretyDetailsPanel;
	}

	@SuppressWarnings("rawtypes")
	private Panel getIsPersonalServicesTrustPanel() {
		// TODO Jean busy here
		if (isPersonalServicesTrustPanel == null) {

			AgreementDetailsIsPersonalServicesPanel panel = new AgreementDetailsIsPersonalServicesPanel("value",
					panelModel, getEditState());
			panel.setOutputMarkupId(true);
			panel.setOutputMarkupPlaceholderTag(true);

			isPersonalServicesTrustPanel = panel;
			isPersonalServicesTrustPanel.setOutputMarkupId(true);

		}
		return isPersonalServicesTrustPanel;
	}

	/**
	 * create LBF Button Panel Distribution property field
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private HelperPanel getLBFButtonPanel() {
		if (lbfHomeAddPanel == null) {

			Button button = new Button("value", Model.of("Add Home Role"));
			// new PropertyModel("Add lbf ","lbfAddButton.value"));
			button.setOutputMarkupId(true);
			button.setOutputMarkupPlaceholderTag(true);
			button.setVisible(false);
			button.add(new AjaxFormComponentUpdatingBehavior("click") {
				private static final long serialVersionUID = 1L;

				@Override
				protected void onUpdate(AjaxRequestTarget target) {

					addLBFWindow.show(target);
				}
			});
			lbfHomeAddPanel = HelperPanel.getInstance(AgreementGUIField.LBF_HOME_ADD_BUTTON.getFieldId(), button);

			lbfHomeAddPanel.setOutputMarkupId(true);
			lbfHomeAddPanel.setVisible(false);

		}
		return lbfHomeAddPanel;
	}

	/**
	 * create Fit and proper waiver property field end date
	 * 
	 * @return
	 */
	private HelperPanel getFitPropWaiverAdviceCheckEndDatePanel() {
		if (fitPropWaiverAdviceCheckEndDatePanel == null) {
			fitPropWaiverAdviceCheckEndDatePanel = createGUIPageField(AgreementGUIField.FITPROPWAIVEADVICECHECKEND,
					panelModel.getAgreement(), ComponentType.DATE_SELECTION_TEXTFIELD, true);
			fitPropWaiverAdviceCheckEndDatePanel.setOutputMarkupId(true);
			fitPropWaiverAdviceCheckEndDatePanel.getEnclosedObject().setOutputMarkupId(true);
			//Santosh:DatePicker Fix		
			if (fitPropWaiverAdviceCheckEndDatePanel.getEnclosedObject() instanceof SRSDateField) {
				fitPropWaiverAdviceCheckEndDatePanel.getEnclosedObject().add(
				((SRSDateField)fitPropWaiverAdviceCheckEndDatePanel.getEnclosedObject()).newDatePicker());
				}
		}
		return fitPropWaiverAdviceCheckEndDatePanel;
	}

	/**
	 * create Fit and proper waiver property field
	 * 
	 * @return
	 */
	private HelperPanel getFitPropWaiverIntermediaryServiceCheckPanel() {
		if (fitPropWaiverIntermediaryServiceCheckPanel == null) {
			fitPropWaiverIntermediaryServiceCheckPanel = createGUIPageField(
					AgreementGUIField.FITPROPWAIVEINTERMEDIARYSERVICECHECK, panelModel.getAgreement(),
					ComponentType.CHECKBOX, false);
			fitPropWaiverIntermediaryServiceCheckPanel.setOutputMarkupId(true);
			fitPropWaiverIntermediaryServiceCheckPanel.getEnclosedObject().setOutputMarkupId(true);
		}
		return fitPropWaiverIntermediaryServiceCheckPanel;
	}

	/**
	 * create Fit and proper waiver property field end date
	 * 
	 * @return
	 */
	private HelperPanel getFitPropWaiverIntermediaryServiceCheckEndDatePanel() {
		if (fitPropWaiverIntermediaryServiceCheckEndDatePanel == null) {
			fitPropWaiverIntermediaryServiceCheckEndDatePanel = createGUIPageField(
					AgreementGUIField.FITPROPWAIVEINTERMEDIARYSERVICECHECKEND, panelModel.getAgreement(),
					ComponentType.DATE_SELECTION_TEXTFIELD, true);
			fitPropWaiverIntermediaryServiceCheckEndDatePanel.setOutputMarkupId(true);
			fitPropWaiverIntermediaryServiceCheckEndDatePanel.getEnclosedObject().setOutputMarkupId(true);
			//Santosh:DatePicker Fix		
			if (fitPropWaiverIntermediaryServiceCheckEndDatePanel.getEnclosedObject() instanceof SRSDateField) {
				fitPropWaiverIntermediaryServiceCheckEndDatePanel.getEnclosedObject().add(
				((SRSDateField)fitPropWaiverIntermediaryServiceCheckEndDatePanel.getEnclosedObject()).newDatePicker());
				}
		}
		return fitPropWaiverIntermediaryServiceCheckEndDatePanel;
	}

	/**
	 * create Fit and proper segment property field
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private HelperPanel getFitPropSegmentPanel() {
		if (fitPopSegmentPanel == null) {
			List<String> validTransitionsForSegment = new ArrayList<String>();
			if (panelModel != null && panelModel.getValidAgreementValues() != null
					&& panelModel.getValidAgreementValues().getValidValuesForFitpropSegment() != null) {
				validTransitionsForSegment
						.addAll(panelModel.getValidAgreementValues().getValidValuesForFitpropSegment());
			}
			final SRSDropDownChoice dropDownChoice = new SRSDropDownChoice("value",
					new PropertyModel(panelModel.getAgreement(), AgreementGUIField.FITPROPSEGMENT.getFieldId()),
					validTransitionsForSegment, new ChoiceRenderer(), "Select");
			dropDownChoice.setRequired(true);
			dropDownChoice.add(new AjaxFormComponentUpdatingBehavior("change") {

				@Override
				protected void onUpdate(AjaxRequestTarget target) {
					String fitPropSegModel = (String) dropDownChoice.getModelObject();
					if (fitPropSegModel.equalsIgnoreCase("Head Office 029")) {
						warn("You have set the segment to Head Office 029 - this agreement will be exempt from Fit and Proper checks and FAIS Commission Disclosures.");
					}
					if (target != null && getFeedBackPanel() != null) {
						target.add(getFeedBackPanel());
					}

				}

			});
			fitPopSegmentPanel = createDropDownChoicePanel(AgreementGUIField.FITPROPSEGMENT, dropDownChoice);

		}
		return fitPopSegmentPanel;
	}

	/**
	 * The fit and proper waivers are kept in their own section
	 * 
	 * @return
	 */
	private WebMarkupContainer getFitAndProperPropertiesContainer() {
		if (fitAndProperContainer == null) {
			fitAndProperContainer = new WebMarkupContainer("fitAndProperContainer");
			fitAndProperContainer.setOutputMarkupId(true);
			fitAndProperContainer.setOutputMarkupPlaceholderTag(true);
			RepeatingView fitPropLeftPanel = new RepeatingView("fitPropLeftPanel");
			RepeatingView fitPropRightPanel = new RepeatingView("fitPropRightPanel");
			fitAndProperContainer.add(fitPropLeftPanel);
			fitAndProperContainer.add(fitPropRightPanel);
			/**
			 * Components
			 */
			// then all waivers
			GUIFieldPanel fitPropWaiverRECheckEndDatePanel = createGUIFieldPanel(
					AgreementGUIField.FITPROPWAIVERECHECKEND, null,
					getFitPropWaiverRECheckEndDatePanel().getEnclosedObject(), true);
			GUIFieldPanel fitPropWaiverRECheckPanel = createGUIFieldPanel(AgreementGUIField.FITPROPWAIVERECHECK,
					getFitPropWaiverRECheckPanel());
			// addWaiverEndDateAjaxFunctionalitity(fitPropWaiverRECheckPanel,fitPropWaiverRECheckEndDatePanel);

			fitPropLeftPanel.add(fitPropWaiverRECheckPanel);
			fitPropRightPanel.add(fitPropWaiverRECheckEndDatePanel);

			GUIFieldPanel fitPropWaiverCPDCheckPanel = createGUIFieldPanel(AgreementGUIField.FITPROPWAIVECPDCHECK,
					getFitPropWaiverCPDCheckPanel());
			GUIFieldPanel fitPropWaiverCPDCheckEndDatePanel = createGUIFieldPanel(
					AgreementGUIField.FITPROPWAIVECPDCHECKEND, null,
					getFitPropWaiverCPDCheckEndDatePanel().getEnclosedObject(), true);
			// addWaiverEndDateAjaxFunctionalitity(fitPropWaiverCPDCheckPanel,fitPropWaiverCPDCheckEndDatePanel);

			fitPropLeftPanel.add(fitPropWaiverCPDCheckPanel);
			fitPropRightPanel.add(fitPropWaiverCPDCheckEndDatePanel);

			GUIFieldPanel fitPropWaiverProductAccreditationCheckPanel = createGUIFieldPanel(
					AgreementGUIField.FITPROPWAIVEPRODUCTACCREDCHECK, getFitPropWaiverProductAccreditationCheckPanel());
			GUIFieldPanel fitPropWaiverProductAccreditationCheckEndDatePanel = createGUIFieldPanel(
					AgreementGUIField.FITPROPWAIVEPRODUCTACCREDCHECKEND, null,
					getFitPropWaiverProductAccreditationCheckEndDatePanel().getEnclosedObject(), true);
			// addWaiverEndDateAjaxFunctionalitity(fitPropWaiverProductAccreditationCheckPanel,fitPropWaiverProductAccreditationCheckEndDatePanel);

			fitPropLeftPanel.add(fitPropWaiverProductAccreditationCheckPanel);
			fitPropRightPanel.add(fitPropWaiverProductAccreditationCheckEndDatePanel);

			GUIFieldPanel fitPropWaiverFAISAdviserCheckPanel = createGUIFieldPanel(
					AgreementGUIField.FITPROPWAIVEFAISADVISORCHECK, getFitPropWaiverFAISAdviserCheckPanel());
			GUIFieldPanel fitPropWaiverFAISAdviserCheckEndDatePanel = createGUIFieldPanel(
					AgreementGUIField.FITPROPWAIVEFAISADVISORCHECKEND, null,
					getFitPropWaiverFAISAdviserCheckEndDatePanel().getEnclosedObject(), true);
			// addWaiverEndDateAjaxFunctionalitity(fitPropWaiverFAISAdviserCheckPanel,fitPropWaiverFAISAdviserCheckEndDatePanel);

			fitPropLeftPanel.add(fitPropWaiverFAISAdviserCheckPanel);
			fitPropRightPanel.add(fitPropWaiverFAISAdviserCheckEndDatePanel);

			GUIFieldPanel fitPropWaiverFAISFSPCheckPanel = createGUIFieldPanel(
					AgreementGUIField.FITPROPWAIVEFAISFSPCHECK, getFitPropWaiverFAISFSPCheckPanel());
			GUIFieldPanel fitPopWaiverFAISFSPCheckEndDatePanel = createGUIFieldPanel(
					AgreementGUIField.FITPROPWAIVEFAISFSPCHECKEND, null,
					getfitPopWaiverFAISFSPCheckEndDatePanel().getEnclosedObject(), true);
			// addWaiverEndDateAjaxFunctionalitity(fitPropWaiverFAISFSPCheckPanel,fitPopWaiverFAISFSPCheckEndDatePanel);

			fitPropLeftPanel.add(fitPropWaiverFAISFSPCheckPanel);
			fitPropRightPanel.add(fitPopWaiverFAISFSPCheckEndDatePanel);

			GUIFieldPanel fitPropWaiverAdviceCheckPanel = createGUIFieldPanel(AgreementGUIField.FITPROPWAIVEADVICECHECK,
					getFitPropWaiverAdviceCheckPanel());
			GUIFieldPanel fitPropWaiverAdviceCheckEndDatePanel = createGUIFieldPanel(
					AgreementGUIField.FITPROPWAIVEADVICECHECKEND, null,
					getFitPropWaiverAdviceCheckEndDatePanel().getEnclosedObject(), true);
			// addWaiverEndDateAjaxFunctionalitity(fitPropWaiverAdviceCheckPanel,fitPropWaiverAdviceCheckEndDatePanel);

			fitPropLeftPanel.add(fitPropWaiverAdviceCheckPanel);
			fitPropRightPanel.add(fitPropWaiverAdviceCheckEndDatePanel);

			GUIFieldPanel fitPropWaiverIntermediaryServiceCheckPanel = createGUIFieldPanel(
					AgreementGUIField.FITPROPWAIVEINTERMEDIARYSERVICECHECK,
					getFitPropWaiverIntermediaryServiceCheckPanel());
			GUIFieldPanel fitPropWaiverIntermediaryServiceCheckEndDatePanel = createGUIFieldPanel(
					AgreementGUIField.FITPROPWAIVEINTERMEDIARYSERVICECHECKEND, null,
					getFitPropWaiverIntermediaryServiceCheckEndDatePanel().getEnclosedObject(), true);
			// addWaiverEndDateAjaxFunctionalitity(fitPropWaiverIntermediaryServiceCheckPanel,fitPropWaiverIntermediaryServiceCheckEndDatePanel);

			fitPropLeftPanel.add(fitPropWaiverIntermediaryServiceCheckPanel);
			fitPropRightPanel.add(fitPropWaiverIntermediaryServiceCheckEndDatePanel);

		}
		return fitAndProperContainer;
	}

	// /**
	// * Add default ajax functionality to the waiver properties
	// * @param waiverGUIField
	// */
	// private void addWaiverEndDateAjaxFunctionalitity(GUIFieldPanel
	// waiverGUIField, final GUIFieldPanel endDatefield){
	// Component helperPanel = waiverGUIField.getComponent();
	// if(helperPanel instanceof HelperPanel){
	// final Component comp = ((HelperPanel)helperPanel).getEnclosedObject();
	// if(comp instanceof FormComponent){
	// comp.add(new AjaxFormComponentUpdatingBehavior("click"){
	// @Override
	// protected void onUpdate(AjaxRequestTarget target) {
	// //refresh the end date panel
	// if(comp.getDefaultModel().getObject() instanceof Boolean
	// && (Boolean)comp.getDefaultModel().getObject()){
	// endDatefield.getComponent().setVisible(true);
	// }else{
	// //reset the end date and the end date panel
	// FormComponent field = (FormComponent)
	// ((HelperPanel)endDatefield.getComponent()).getEnclosedObject();
	// field.setModelObject(null);
	// endDatefield.getComponent().setVisible(false);
	// }
	// if(endDatefield != null){
	// target.add(endDatefield.getComponent());
	// }
	// }
	// });
	// //also just set enabled here
	// if(!(comp.getDefaultModel().getObject() instanceof Boolean
	// && (Boolean)comp.getDefaultModel().getObject())){
	//// reset the end date and the end date panel
	// endDatefield.getComponent().setVisible(false);
	// }
	// }
	// }
	// }

	@SuppressWarnings("unchecked")
	private HelperPanel getEntityPanel() {
		if (entityPanel == null) {
			entityPanel = createGUIPageField(AgreementGUIField.ENTITY, panelModel.getAgreement(),
					ComponentType.TEXTFIELD, true);
			if (entityPanel.getEnclosedObject() instanceof TextField) {
				@SuppressWarnings("unused")
				TextField field = (TextField) entityPanel.getEnclosedObject();
				// add validation
			}
		}
		return entityPanel;
	}

	@SuppressWarnings("unchecked")
	private HelperPanel getNetworkPanel() {
		if (networkPanel == null) {
			networkPanel = createGUIPageField(AgreementGUIField.NETWORK, panelModel.getAgreement(),
					ComponentType.TEXTFIELD, true);
			if (networkPanel.getEnclosedObject() instanceof TextField) {
				@SuppressWarnings("unused")
				TextField field = (TextField) networkPanel.getEnclosedObject();
				// add validation
			}
		}

		return networkPanel;
	}

	private HelperPanel getPreAuthLimitCategoryPanel() {
		if (preAuthLimitCategoryPanel == null) {
			preAuthLimitCategoryPanel = createGUIPageField(AgreementGUIField.PREAUTH_CATEGORY,
					panelModel.getAgreement(), ComponentType.TEXTFIELD, false);
			preAuthLimitCategoryPanel.setOutputMarkupId(true);
			preAuthLimitCategoryPanel.getEnclosedObject().setOutputMarkupId(true);
		}

		return preAuthLimitCategoryPanel;
	}

	@SuppressWarnings("unchecked")
	private HelperPanel getPreAuthLimitAmountPanel() {
		if (preAuthLimitAmoutPanel == null) {

			TextField field = new TextField("value",
					new PropertyModel(getContext(), "preauthLimitCategory.preauthLimit"));
			field.setOutputMarkupId(true);
			field.setOutputMarkupPlaceholderTag(true);
			field.setEnabled(getContext().getPreauthLimitCategory().isOverridden());
			field.add(new AjaxFormComponentUpdatingBehavior("change") {

				private static final long serialVersionUID = 1L;

				@Override
				protected void onUpdate(AjaxRequestTarget target) {
				}
			});
			preAuthLimitAmoutPanel = createGUIPageField(AgreementGUIField.PREAUTH_AMOUNT, panelModel.getAgreement(),
					field);
			preAuthLimitAmoutPanel.setOutputMarkupId(true);
		}
		return preAuthLimitAmoutPanel;
	}

	@SuppressWarnings("unchecked")
	private HelperPanel getPreAuthOverridePanel() {
		if (preAuthOverridePanel == null) {
			CheckBox checkBox = new CheckBox("value", new PropertyModel(getContext(), "preAuthOverride"));
			getContext().setPreAuthOverride(getContext().getPreauthLimitCategory().isOverridden());
			checkBox.setOutputMarkupId(true);
			checkBox.setOutputMarkupPlaceholderTag(true);
			checkBox.add(new AjaxFormComponentUpdatingBehavior("change") {
				private static final long serialVersionUID = 1L;

				@Override
				protected void onUpdate(AjaxRequestTarget target) {
					configurePreauthDisplayElements();
					target.add(getPreAuthLimitAmountPanel().getEnclosedObject());
					target.add(getPreAuthLimitCategoryPanel().getEnclosedObject());
				}
			});
			EditStateType[] allowedStates = getViewTemplate().getEditStates(AgreementGUIField.PREAUTH_OVERRIDE,
					getViewTemplateContext());
			checkBox.setEnabled(Arrays.asList(allowedStates).contains(getEditState()));
			preAuthOverridePanel = HelperPanel.getInstance(AgreementGUIField.PREAUTH_OVERRIDE.getFieldId(), checkBox);
		}

		return preAuthOverridePanel;
	}

	@SuppressWarnings("unchecked")
	private void configurePreauthDisplayElements() {
		if (getContext().isPreAuthOverride()) {
			getContext()
					.setPreauthLimitCategory(panelModel.getValidAgreementValues().getOverriddenPreauthLimitCategory());
		} else {
			getContext().setPreauthLimitCategory(panelModel.getValidAgreementValues().getDefaultPreauthLimitCategory());
		}
		Component component = getPreAuthLimitAmountPanel().getEnclosedObject();
		if (component instanceof TextField) {
			boolean enable = getContext().getPreauthLimitCategory() != null
					&& getContext().getPreauthLimitCategory().isOverridden();
			((TextField) component).setEnabled(enable);
		}

	}

	@SuppressWarnings("unchecked")
	private HelperPanel getMonthlyGuaranteedAmountPanel() {
		if (monthlyGuaranteedAmountPanel == null) {
			monthlyGuaranteedAmountPanel = createGUIPageField(AgreementGUIField.MONTHLY_GUARANTEED_AMOUNT,
					panelModel.getAgreement(), ComponentType.TEXTFIELD, true);
			if (monthlyGuaranteedAmountPanel.getEnclosedObject() instanceof TextField) {
				@SuppressWarnings("unused")
				TextField field = (TextField) monthlyGuaranteedAmountPanel.getEnclosedObject();
				// add validation
			}
		}
		return monthlyGuaranteedAmountPanel;
	}

	@SuppressWarnings("unchecked")
	private HelperPanel getStatusPanel() {
		if (statusPanel == null) {
			List<ActualLifeCycleStatusDTO> validTransitionsForStatus = new ArrayList<ActualLifeCycleStatusDTO>();
			if (panelModel != null && panelModel.getValidAgreementValues() != null
					&& panelModel.getValidAgreementValues().getValidTransitionsForStatus() != null) {
				validTransitionsForStatus.addAll(panelModel.getValidAgreementValues().getValidTransitionsForStatus());
			}
			if (editState.equals(EditStateType.TERMINATE)) {
				/**
				 * Only retain the termination status types for the termination
				 * process
				 */
				filterStatusForTerminate(validTransitionsForStatus);
			} else if (editState.equals(EditStateType.MODIFY)) {
				filterStatusForMaintain(validTransitionsForStatus);
			}
			SRSDropDownChoice dropDownChoice = new SRSDropDownChoice("value",
					new PropertyModel(panelModel.getAgreement(), "currentStatus"), validTransitionsForStatus,
					new ChoiceRenderer("name", "specId"), "Select");
			dropDownChoice.add(new AjaxFormComponentUpdatingBehavior("change") {
				private static final long serialVersionUID = 1L;

				@Override
				protected void onUpdate(AjaxRequestTarget target) {
					// updateRequiredFields();
					target.add(getStatusDatePanel().getEnclosedObject());
					// target.add(endDateGUIPanel.getComponent());
				}
			});
			statusPanel = createDropDownChoicePanel(AgreementGUIField.STATUS, dropDownChoice);
		}
		return statusPanel;
	}

	/**
	 * Filter the valid transitions for the termination process to exclude
	 * termination status as defined in {@link AgreementStatusType} when the
	 * original value is not a termination status
	 */
	@SuppressWarnings("unused")
	private void filterStatusForMaintain(List<ActualLifeCycleStatusDTO> validTransitionsForStatus) {
		if (panelModel != null && panelModel.getAgreement() != null
				&& panelModel.getAgreement().getCurrentStatus() != null) {
			AgreementStatusType currentStatus = AgreementStatusType
					.getRequestStatusTypeForSpecId(panelModel.getAgreement().getCurrentStatus().getSpecId());
			List<AgreementStatusType> terminationList = Arrays.asList(AgreementStatusType.getTerminationStatus());
			if (terminationList.contains(currentStatus)) {
				return;
			}
			outer: for (Iterator<ActualLifeCycleStatusDTO> it = validTransitionsForStatus.iterator(); it.hasNext();) {
				ActualLifeCycleStatusDTO current = it.next();
				inner: for (AgreementStatusType termination : terminationList) {
					if (current.getSpecId() == termination.getSpecId()) {
						/**
						 * An entry matches, remove the matching entry
						 */
						it.remove();
						continue outer;
					}
				}
			}
		}
	}

	/**
	 * Filter the valid transitions for the termination process to only include
	 * valid termination status as defined in {@link AgreementStatusType}
	 */
	@SuppressWarnings("unused")
	private void filterStatusForTerminate(List<ActualLifeCycleStatusDTO> validTransitionsForStatus) {
		List<AgreementStatusType> terminationList = Arrays.asList(AgreementStatusType.getTerminationStatus());
		outer: for (Iterator<ActualLifeCycleStatusDTO> it = validTransitionsForStatus.iterator(); it.hasNext();) {
			ActualLifeCycleStatusDTO current = it.next();
			inner: for (AgreementStatusType termination : terminationList) {
				if (current.getSpecId() == termination.getSpecId()) {
					/**
					 * An entry matches, continue the outer loop (start
					 * processing next entry)
					 */
					continue outer;
				}
			}
			/**
			 * Remove unmatched entries
			 */
			it.remove();
		}
	}
	

	@SuppressWarnings("unchecked")
	private HelperPanel getStatusReasonPanel() {
		if (statusReasonPanel == null) {
			SRSDropDownChoice dropDownChoice = new SRSDropDownChoice("value",
					new PropertyModel(panelModel.getAgreement(), AgreementGUIField.STATUS_REASON.getFieldId()),
					panelModel.getValidAgreementValues().getValidStatusReason(), new ChoiceRenderer(), "Select");
			statusReasonPanel = createDropDownChoicePanel(AgreementGUIField.STATUS_REASON, dropDownChoice);
		}
		return statusReasonPanel;
	}

	@SuppressWarnings("unchecked")
	private HelperPanel getEarlyDebitsReasonPanel() {
		if (earlyDebitsReasonPanel == null) {
			SRSDropDownChoice dropDownChoice = new SRSDropDownChoice("value",
					new PropertyModel(panelModel.getAgreement(), "earlyDebitsReason"),
					panelModel.getValidAgreementValues().getValidEarlyDebitsReason(), new SRSAbstractChoiceRenderer () {
						private static final long serialVersionUID = 1L;

						public Object getDisplayValue(Object object) {
							return object.toString();
						}

						public String getIdValue(Object object, int index) {
							return "" + index;
						}
					}, "Select");
			dropDownChoice.add(new AjaxFormComponentUpdatingBehavior("change") {
				private static final long serialVersionUID = 1L;

				@Override
				protected void onUpdate(AjaxRequestTarget target) {
					target.add(getEarlyDebitsStartDatePanel().getEnclosedObject());
				}
			});
			dropDownChoice.add(new EarlyDebitsReasonValidator(getEarlyDebitsIndicatorPanel().getEnclosedObject()));
			Label viewLabel = new Label("value", new PropertyModel(getContext(), "earlyDebitsReason")) {
				private static final long serialVersionUID = 1L;

				@Override
				public IConverter getConverter(Class arg0) {
					return getEarlyDebitsConverter();
				}
			};
			earlyDebitsReasonPanel = createDropDownChoicePanel(AgreementGUIField.EARLY_DEBITS_REASON, dropDownChoice,
					viewLabel);
			earlyDebitsReasonPanel.setOutputMarkupId(true);
			earlyDebitsReasonPanel.getEnclosedObject().setOutputMarkupId(true);
		}
		return earlyDebitsReasonPanel;
	}

	@SuppressWarnings("unchecked")
	private HelperPanel getEarlyDebitsIndicatorPanel() {
		if (earlyDebitsIndicatorPanel == null) {
			String nullLabel = "Early Debits Not Applicable";
			AgreementGUIField field = AgreementGUIField.EARLY_DEBITS_INDICATOR;
			SRSDropDownChoice dropDownChoice = new SRSDropDownChoice("value",
					new PropertyModel(panelModel.getAgreement(), field.getFieldId()),
					panelModel.getValidAgreementValues().getValidBoolean(), new YesNoBooleanChoiceRenderer(nullLabel),
					nullLabel);
			dropDownChoice.setLabel(new Model(field.getDescription()));
			dropDownChoice.setRequired(getViewTemplate().isRequired(field, getContext()));
			dropDownChoice.setOutputMarkupId(true);
			dropDownChoice.add(new AjaxFormComponentUpdatingBehavior("change") {
				private static final long serialVersionUID = 1L;

				@Override
				protected void onUpdate(AjaxRequestTarget arg0) {
				}
			});
			NullReplacementPropertyModel nrModel = new NullReplacementPropertyModel(nullLabel, getContext(),
					field.getFieldId());
			Label viewLabel = new GenericConversionLabel("value", nrModel, new YesNoBooleanConverter(nullLabel));
			earlyDebitsIndicatorPanel = createPageField(field.getFieldId(), field.getDescription(), viewLabel,
					dropDownChoice, getViewTemplate().getEditStates(field, getViewTemplateContext()));
			earlyDebitsIndicatorPanel.setOutputMarkupId(true);
			dropDownChoice.setOutputMarkupId(true);
			getFields().put(AgreementGUIField.EARLY_DEBITS_INDICATOR, earlyDebitsIndicatorPanel);
		}
		return earlyDebitsIndicatorPanel;
	}

	// private HelperPan

	@SuppressWarnings("unchecked")
	private HelperPanel getEndDatePanel() {
		if (endDatePanel == null) {
			endDatePanel = createGUIPageField(AgreementGUIField.END_DATE, panelModel.getAgreement(),
					ComponentType.DATE_SELECTION_TEXTFIELD, false);
			if (endDatePanel.getEnclosedObject() instanceof SRSDateField) {
				SRSDateField field = (SRSDateField) endDatePanel.getEnclosedObject();
				field.add(new AjaxFormComponentUpdatingBehavior("change") {
					private static final long serialVersionUID = 1L;

					@Override
					protected void onUpdate(AjaxRequestTarget arg0) {
					}
				});
				// updateRequiredFields();
				field.add(new AgreementEndDateValidator(getStartDatePanel().getEnclosedObject(),
						getStatusPanel().getEnclosedObject()));
				FormattedValidationDate currentDate = new FormattedValidationDate();
				currentDate.setTime(DateUtil.minimizeTime(new Date()).getTime());
				field.add(DateValidator.maximum(currentDate));
				field.setOutputMarkupId(true);
				field.setOutputMarkupPlaceholderTag(true);
				field.add(field.newDatePicker());
			}
		}
		return endDatePanel;
	}

	@SuppressWarnings({ "unused", "unchecked" })
	private void updateRequiredFields() {
		Component endDate = getEndDatePanel().getEnclosedObject();
		if (endDate instanceof TextField) {
			ActualLifeCycleStatusDTO currentStatus = (ActualLifeCycleStatusDTO) getStatusPanel().getEnclosedObject()
					.getDefaultModel();
			((TextField) endDate).setRequired(currentStatus != null && currentStatus.getName() != null
					&& currentStatus.getName().equalsIgnoreCase("Terminated"));
		}

	}

	@SuppressWarnings("unchecked")
	private HelperPanel getStartDatePanel() {
		if (startDatePanel == null) {
			startDatePanel = createGUIPageField(AgreementGUIField.START_DATE, panelModel.getAgreement(),
					ComponentType.DATE_SELECTION_TEXTFIELD, true);
			if (startDatePanel.getEnclosedObject() instanceof SRSDateField) {
				SRSDateField field = (SRSDateField) startDatePanel.getEnclosedObject();
				if (getEditState() == EditStateType.ADD) {
					/**
					 * For the ADD edit state, apply model synchronisation of
					 * dates tied to agreement start date
					 */
					field.add(new AjaxFormComponentUpdatingBehavior("change") {
						private static final long serialVersionUID = 1L;

						@Override
						protected void onUpdate(AjaxRequestTarget arg0) {
							/**
							 * Sync valid values and selected values in the page
							 * model to the agreement start date
							 */
							panelModel.updateModelForCreateAgreementFromAgreementStartDate();
						}
					});
					field.add(field.newDatePicker());
					/**
					 * For the ADD edit state, add validation of start date
					 * against job title start date
					 */
					// NO LONGER REQUIRED
					// agreementStartDateJobTitleValidator = new
					// AgreementStartDateJobTitleValidator(partyContext);
					// field.add(agreementStartDateJobTitleValidator);
				}
			}
		}
		return startDatePanel;
	}

	@SuppressWarnings("unchecked")
	private HelperPanel getEarlyDebitsStartDatePanel() {
		if (earlyDebitsStartDatePanel == null) {
			earlyDebitsStartDatePanel = createGUIPageField(AgreementGUIField.EARLY_DEBITS_START_DATE,
					panelModel.getAgreement(), ComponentType.DATE_SELECTION_TEXTFIELD, false);
			earlyDebitsStartDatePanel.setOutputMarkupId(true);
			earlyDebitsStartDatePanel.getEnclosedObject().setOutputMarkupId(true);
			if (earlyDebitsStartDatePanel.getEnclosedObject() instanceof TextField) {
				@SuppressWarnings("unused")
				TextField field = (TextField) earlyDebitsStartDatePanel.getEnclosedObject();
				// ADD VALIDATION
			}
		}
		return earlyDebitsStartDatePanel;
	}

	@SuppressWarnings("unchecked")
	private HelperPanel getStatusDatePanel() {
		if (statusDatePanel == null) {
			statusDatePanel = createGUIPageField(AgreementGUIField.STATUS_DATE, panelModel.getAgreement(),
					ComponentType.DATE_SELECTION_TEXTFIELD, false);
			statusDatePanel.setOutputMarkupId(true);
			statusDatePanel.getEnclosedObject().setOutputMarkupId(true);
			if (statusDatePanel.getEnclosedObject() instanceof TextField) {
				/**
				 * Add validation to status date
				 */
				((TextField) statusDatePanel.getEnclosedObject())
						.add(new AgreementStatusDateValidator(panelModel.getAgreement()));
			}
		}
		return statusDatePanel;
	}

	@SuppressWarnings("unchecked")
	private HelperPanel getProductionClubStatusPanel() {
		if (productionClubStatusPanel == null) {
			SRSDropDownChoice dropDownChoice = new SRSDropDownChoice("value",
					new PropertyModel(panelModel.getAgreement(), "productionClubStatus"),
					panelModel.getValidAgreementValues().getValidProductionClubStatus(), new ChoiceRenderer(),
					"Select");
			productionClubStatusPanel = createDropDownChoicePanel(AgreementGUIField.PRODUCTION_CLUB_STATUS,
					dropDownChoice);
		}
		return productionClubStatusPanel;
	}

	@SuppressWarnings("unchecked")
	private HelperPanel getManpowerPanel() {
		if (manpowerPanel == null) {
			SRSDropDownChoice dropDownChoice = new SRSDropDownChoice("value",
					new PropertyModel(panelModel.getAgreement(), "manpower"),
					panelModel.getValidAgreementValues().getValidManpower(), new ChoiceRenderer(), "Select");
			manpowerPanel = createDropDownChoicePanel(AgreementGUIField.MANPOWER, dropDownChoice);
		}
		return manpowerPanel;
	}

	// SSM2707 ADDED for FR15 Tenure SWETA MENON Begin
	@SuppressWarnings("unchecked")
	private HelperPanel getLibertyTenurePanel() {
		if (libertyTenurePanel == null) {
			SRSDropDownChoice dropDownChoice = new SRSDropDownChoice("value",
					new PropertyModel(panelModel.getAgreement(), "libertyTenure"),
					panelModel.getValidAgreementValues().getValidLibertyTenure(), new ChoiceRenderer(), "Select");
			libertyTenurePanel = createDropDownChoicePanel(AgreementGUIField.LIBERTY_TENURE, dropDownChoice);
		}
		return libertyTenurePanel;
	}
	// SSM2707 ADDED for FR15 Tenure SWETA MENON End

	@SuppressWarnings("unchecked")
	private HelperPanel getDirectManpowerPanel() {
		if (directManpowerPanel == null) {
			SRSDropDownChoice dropDownChoice = new SRSDropDownChoice("value",
					new PropertyModel(panelModel.getAgreement(), "manpower"),
					panelModel.getValidAgreementValues().getValidDirectManpower(), new ChoiceRenderer(), "Select");
			directManpowerPanel = createDropDownChoicePanel(AgreementGUIField.MANPOWER, dropDownChoice);
		}
		return directManpowerPanel;
	}

	@SuppressWarnings("unchecked")
	private HelperPanel getPrimaryCompanyContractedToPanel() {
		if (primaryCompanyContractedToPanel == null) {
			SRSDropDownChoice dropDownChoice = new SRSDropDownChoice("value",
					new PropertyModel(panelModel.getAgreement(), "primaryCompanyContractedTo"),
					panelModel.getValidAgreementValues().getValidPrimaryCompanyContractedTo(), new ChoiceRenderer(),
					"Select");
			primaryCompanyContractedToPanel = createDropDownChoicePanel(AgreementGUIField.PRIMARY_COMPANY_CONTRACTED_TO,
					dropDownChoice);
		}
		return primaryCompanyContractedToPanel;
	}

	@SuppressWarnings("unchecked")
	private HelperPanel getDedicatedSBFCConsultantTypePanel() {
		if (dedicatedSBFCConsultantTypePanel == null) {
			SRSDropDownChoice dropDownChoice = new SRSDropDownChoice("value",
					new PropertyModel(panelModel.getAgreement(), "dedicatedSBFCConsultantType"),
					panelModel.getValidAgreementValues().getValidDedicatedSBFCConsultantType(), new ChoiceRenderer(),
					"Select");
			dedicatedSBFCConsultantTypePanel = createDropDownChoicePanel(
					AgreementGUIField.DEDICATED_SBFC_CONSULTANT_TYPE, dropDownChoice);
		}
		return dedicatedSBFCConsultantTypePanel;
	}

	@SuppressWarnings("unchecked")
	private HelperPanel getTitularLevelPanel() {
		if (titularLevelPanel == null) {
			SRSDropDownChoice dropDownChoice = new SRSDropDownChoice("value",
					new PropertyModel(panelModel.getAgreement(), "titularLevel"),
					panelModel.getValidAgreementValues().getValidTitularLevel(), new ChoiceRenderer(), "Select");
			titularLevelPanel = createDropDownChoicePanel(AgreementGUIField.TITULAR_LEVEL, dropDownChoice);
		}
		return titularLevelPanel;
	}

	@SuppressWarnings("unchecked")
	private HelperPanel getSupportTypePanel() {
		if (supportTypePanel == null) {
			SRSDropDownChoice dropDownChoice = new SRSDropDownChoice("value",
					new PropertyModel(panelModel.getAgreement(), "supportType"),
					panelModel.getValidAgreementValues().getValidSupportType(), new ChoiceRenderer(), "Select");
			supportTypePanel = createDropDownChoicePanel(AgreementGUIField.SUPPORT_TYPE, dropDownChoice);
		}
		return supportTypePanel;
	}

	@SuppressWarnings("unchecked")
	private HelperPanel getBrokerConsultantProductionClubStatusPanel() {
		if (brokerConsultantProductionClubStatusPanel == null) {
			SRSDropDownChoice dropDownChoice = new SRSDropDownChoice("value",
					new PropertyModel(panelModel.getAgreement(), "brokerConsultantProductionClubStatus"),
					panelModel.getValidAgreementValues().getValidBrokerConsultantProductionClubStatus(),
					new ChoiceRenderer(), "Select");
			brokerConsultantProductionClubStatusPanel = createDropDownChoicePanel(
					AgreementGUIField.BROKER_CONSULTANT_PRODUCTION_CLUB_STATUS, dropDownChoice);
		}
		return brokerConsultantProductionClubStatusPanel;
	}

	@SuppressWarnings("unchecked")
	private HelperPanel getSegmentPanel() {
		if (segmentPanel == null) {
			SRSDropDownChoice dropDownChoice = new SRSDropDownChoice("value",
					new PropertyModel(panelModel.getAgreement(), "segment"),
					panelModel.getValidAgreementValues().getValidSegment(), new ChoiceRenderer(), "Select");
			segmentPanel = createDropDownChoicePanel(AgreementGUIField.SEGMENT, dropDownChoice);
		}
		return segmentPanel;
	}

	@SuppressWarnings("unchecked")
	private HelperPanel getBBFGroupPanel() {
		if (bbfGroupPanel == null) {
			SRSDropDownChoice dropDownChoice = new SRSDropDownChoice("value",
					new PropertyModel(panelModel.getAgreement(), "brokerBranchFranchiseGroup"),
					panelModel.getValidAgreementValues().getValidBBFGroups(), new ChoiceRenderer("value", "id"),
					"Select");
			bbfGroupPanel = createDropDownChoicePanel(AgreementGUIField.BROKER_BRANCH_FRANCHISE_GROUP, dropDownChoice);
		}
		return bbfGroupPanel;
	}

	private HelperPanel getCostCenterPanel() {
		if (costCenterPanel == null) {
			costCenterPanel = createGUIPageField(AgreementGUIField.COST_CENTER, getContext(), ComponentType.LABEL,
					false);
			costCenterPanel.setOutputMarkupId(true);
			costCenterPanel.setOutputMarkupPlaceholderTag(true);
		}
		return costCenterPanel;
	}

	@SuppressWarnings("unchecked")
	private HelperPanel getDoNotCalculateIntPanel() {
		if (doNotCalculateIntPanel == null) {

			CheckBox checkBox = new CheckBox("value", new PropertyModel(getContext(), "doNotCalcInterest"));
			checkBox.setOutputMarkupId(true);
			checkBox.setOutputMarkupPlaceholderTag(true);
			checkBox.add(new AjaxFormComponentUpdatingBehavior("change") {
				private static final long serialVersionUID = 1L;

				@Override
				protected void onUpdate(AjaxRequestTarget target) {
					// update the Model
				}
			});
			// Enable only for Broker Agreements & Stop Order Broker Agreements
			int agmtKind = getContext().getKind();
			checkBox.setEnabled(((getEditState() == EditStateType.ADD) || (getEditState() == EditStateType.MODIFY))
					&& ((agmtKind == AgreementKindType.BROKER.getKind())
							|| (agmtKind == AgreementKindType.STOP_ORDER_BROKER.getKind())));

			doNotCalculateIntPanel = HelperPanel.getInstance(AgreementGUIField.DONOTCALCINTEREST.getFieldId(),
					checkBox);

		}
		return doNotCalculateIntPanel;
	}

	/**
	 * Method to add My Banking No field
	 * 
	 * @author pks2802
	 *
	 */
	@SuppressWarnings("unchecked")
	private HelperPanel getMyBankingNumPanel() {
		if (myBankingNumPanel == null) {
			myBankingNumPanel = createGUIPageField(AgreementGUIField.MY_BANKING_NUM, panelModel.getAgreement(),
					ComponentType.TEXTFIELD, true);
			if (myBankingNumPanel.getEnclosedObject() instanceof TextField) {
				TextField field = (TextField) myBankingNumPanel.getEnclosedObject();
				// add validation
				field.add(new MyBankingNumberValidator(panelModel.getAgreement().getId()));
			}
		}
		return myBankingNumPanel;
	}

	/**
	 * create Manual Production Club Status Panel
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private HelperPanel getManualProdClubStatusPanel() {
		if (manProdClubStatusPanel == null) {
			final List<IDValueDTO> validTransitions = new ArrayList<IDValueDTO>();
			if (panelModel != null && panelModel.getValidAgreementValues() != null
					&& panelModel.getValidAgreementValues().getValidValuesForManualProductionClubStatus() != null) {
				validTransitions
						.addAll(panelModel.getValidAgreementValues().getValidValuesForManualProductionClubStatus());
			} else {
				validTransitions.addAll(getGuiController().getAllProductionClubStatusValues());
			}
			final SRSDropDownChoice dropDownChoice = new SRSDropDownChoice("value", new PropertyModel(
					panelModel.getAgreement(), AgreementGUIField.MANUAL_PRODUCTION_CLUB_STATUS.getFieldId()) {
				private static final long serialVersionUID = 1L;

				@Override
				public Object getObject() {
					Integer val = (Integer) super.getObject();
					if (val == null) {
						return null;
					}
					for (IDValueDTO value : validTransitions) {
						if (value.getOid() == val) {
							return value;
						}
					}
					return null;
				}

				@Override
				public void setObject(Object object) {
					IDValueDTO obj = (IDValueDTO) object;
					if (obj == null) {
						super.setObject(null);
					} else {
						super.setObject((int) obj.getOid());
					}
				}

			}, validTransitions, new ChoiceRenderer<IDValueDTO>("name", "oid"), "No Selection");
			dropDownChoice.setNullValid(getEditState().isAdd());
			dropDownChoice.setRequired(false);
			dropDownChoice.setOutputMarkupId(true);
			dropDownChoice.add(new AjaxFormComponentUpdatingBehavior("change") {
				@Override
				protected void onUpdate(AjaxRequestTarget target) {
					// if a new value is chosen, and there is currently no end
					// date set then change date to year end date
					if (panelModel.getAgreement().getManualProductionClubStatus() != null
							&& panelModel.getAgreement().getManualProductionClubStatus().getValue() != null
							&& panelModel.getAgreement().getManualProductionClubStatus().getEffectiveTo() == null) {
						// make end date the year end date
						try {
							Date libertyYearEndDate = getGuiController().getLibertyYearEndDate(new Date());
							panelModel.getAgreement().getManualProductionClubStatus()
									.setEffectiveTo(libertyYearEndDate);
							target.add(manualProdClubGuiFieldPanel);
						} catch (DataNotFoundException e) {
							// do nothing, leave blank
						}
					} else if (panelModel.getAgreement().getManualProductionClubStatus() != null
							&& panelModel.getAgreement().getManualProductionClubStatus().getValue() == null) {
						panelModel.getAgreement().getManualProductionClubStatus().setEffectiveTo(null);
						target.add(manualProdClubGuiFieldPanel);
					}
				}
			});
			manProdClubStatusPanel = createDropDownChoicePanel(AgreementGUIField.MANUAL_PRODUCTION_CLUB_STATUS,
					dropDownChoice);

		}
		return manProdClubStatusPanel;
	}

	/**
	 * create Manual Production Club End Date Panel
	 * 
	 * @return
	 */
	private HelperPanel getManualProdClubStatusEndDatePanel() {
		if (manualProdClubStatusEndDatePanel == null) {
			manualProdClubStatusEndDatePanel = createGUIPageField(AgreementGUIField.MANUAL_PRODUCTION_CLUB_STATUS_END,
					panelModel.getAgreement(), ComponentType.DATE_SELECTION_TEXTFIELD, true);
			manualProdClubStatusEndDatePanel.setOutputMarkupId(true);
			manualProdClubStatusEndDatePanel.getEnclosedObject().setOutputMarkupId(true);
			//Santosh datepicker fix
			if (manualProdClubStatusEndDatePanel.getEnclosedObject() instanceof SRSDateField) {
				manualProdClubStatusEndDatePanel.getEnclosedObject().add(
			((SRSDateField)manualProdClubStatusEndDatePanel.getEnclosedObject()).newDatePicker());
			}


		}
		return manualProdClubStatusEndDatePanel;
	}

	@SuppressWarnings("unchecked")
	private HelperPanel getCalcProdClubStatusPanel() {
		if (calcProdClubStatusPanel == null) {
			List<IDValueDTO> validTransitions = new ArrayList<IDValueDTO>();
			if (panelModel != null && panelModel.getValidAgreementValues() != null
					&& panelModel.getValidAgreementValues().getAllProductionClubStatuses() != null) {
				validTransitions.addAll(panelModel.getValidAgreementValues().getAllProductionClubStatuses());
			}
			final SRSDropDownChoice dropDownChoice = new SRSDropDownChoice("value",
					new PropertyModel(panelModel.getAgreement(),
							AgreementGUIField.CALCULATED_PRODUCTION_CLUB_STATUS.getFieldId()),
					validTransitions, new ChoiceRenderer<IDValueDTO>("name", "oid"), "Select");
			dropDownChoice.setRequired(true);
			calcProdClubStatusPanel = createDropDownChoicePanel(AgreementGUIField.CALCULATED_PRODUCTION_CLUB_STATUS,
					dropDownChoice);

		}
		return calcProdClubStatusPanel;
	}

	/**
	 * create Corporate Addendum Signed property field
	 * 
	 * @return
	 */
	private HelperPanel getCorpAddendumSignedPanel() {
		if (corpAddendumSignedPanel == null) {

			Boolean enableCheckBox = (editState.equals(EditStateType.MODIFY) || editState.equals(EditStateType.ADD));
			boolean isSubCode = (getContext().getBelongsToCorpAddendumSigned() != null);
			enableCheckBox = (enableCheckBox && !isSubCode);

			CheckBox checkBox = new CheckBox("value", new PropertyModel(getContext(),
					isSubCode ? "belongsToCorpAddendumSigned" : "corpAddendumSigned.value"));
			checkBox.setOutputMarkupId(true);
			checkBox.setOutputMarkupPlaceholderTag(true);
			checkBox.add(new AjaxFormComponentUpdatingBehavior("change") {
				private static final long serialVersionUID = 1L;

				@Override
				protected void onUpdate(AjaxRequestTarget target) {
					// update the Model
				}
			});
			checkBox.setEnabled(enableCheckBox);

			corpAddendumSignedPanel = HelperPanel.getInstance(AgreementGUIField.CORP_ADDENDUM_SIGNED.getFieldId(),
					checkBox);
		}

		return corpAddendumSignedPanel;
	}

	/**
	 * create Hold Corporate Commission property field
	 * 
	 * @return
	 */
	private HelperPanel getHoldCorpCommissionPanel() {
		if (holdCorpCommissionPanel == null) {
			holdCorpCommissionPanel = createGUIPageField(AgreementGUIField.HOLD_CORP_COMMISSION,
					panelModel.getAgreement(), ComponentType.CHECKBOX, true);
			holdCorpCommissionPanel.setOutputMarkupId(true);
			holdCorpCommissionPanel.getEnclosedObject().setOutputMarkupId(true);
		}
		return holdCorpCommissionPanel;
	}

	/**
	 * create is Sole Proprietor field
	 * 
	 * @return
	 */
	private HelperPanel getSoleProprietorPanel() {
		if (soleProprietorPanel == null) {
			soleProprietorPanel = createGUIPageField(AgreementGUIField.SOLE_PROPRIETOR, panelModel.getAgreement(),
					ComponentType.CHECKBOX, true);
			soleProprietorPanel.setOutputMarkupId(true);
			soleProprietorPanel.getEnclosedObject().setOutputMarkupId(true);
		}
		if (soleProprietorPanel.getEnclosedObject() instanceof CheckBox) {
			CheckBox field = (CheckBox) soleProprietorPanel.getEnclosedObject();
			field.setEnabled(panelModel.getAgreement().getSoleProprietor() == null
					|| !panelModel.getAgreement().getSoleProprietor());
		}
		return soleProprietorPanel;
	}

	/**
	 * create Include In Manpower Reporting field - RXS 1408 ADDED for FR2
	 * INCLUDE In Manpower Reporting - RAVISH SEHGAL
	 * 
	 * @return
	 */
	private HelperPanel getIncludeInManpowerReporting() {
		if (includeInManpowerReporting == null) {
			final CheckBox checkBox = new CheckBox("value",
					new PropertyModel(getContext(), "includeInManpowerReporting.value"));

			checkBox.add(new AjaxFormComponentUpdatingBehavior("click") {
				private static final long serialVersionUID = 1L;

				@Override
				protected void onUpdate(AjaxRequestTarget target) {
					if (checkBox.getModelObject() == true) {
						// Sweta Menon SSM2707 Bug fix. This change was not
						// required.
						// try {
						// if (panelModel != null
						// && panelModel.getAgreement() != null
						// && panelModel.getAgreement().getId() > 0) {
						//// guiController
						//// .validateSolohCatchUpAgreementTyping(panelModel
						//// .getAgreement());
						// }
						// } catch (ValidationException e) {
						// //AgreementDetailsPanel.this.info(e.getMessage());
						// List<String> errorMessages = e.getErrorMessages();
						// for (String string : errorMessages) {
						// AgreementDetailsPanel.this.error(string);
						// }
						// target.add(getFeedBackPanel());
						// }
					}
					// update the Model
				}
			});
			checkBox.setOutputMarkupId(true);
			checkBox.setOutputMarkupPlaceholderTag(true);
			if (getOutstandingRequests) {
				checkBox.setEnabled(editState.equals(EditStateType.MODIFY) || editState.equals(EditStateType.ADD));
			} else {
				checkBox.setEnabled(false);
			}
			includeInManpowerReporting = HelperPanel
					.getInstance(AgreementGUIField.INCLUDE_IN_MANPOWER_REPORTING.getFieldId(), checkBox);
		}
		return includeInManpowerReporting;
	}

	/**
	 * create Employee Number field - RXS 1408 ADDED for Hierarchy FR3.6
	 * Employee Number - RAVISH SEHGAL
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private HelperPanel getEmployeeNumber() {
		if (employeeNumber == null) {
			employeeNumber = createGUIPageField(AgreementGUIField.EMPLOYEE_NUMBER, panelModel.getAgreement(),
					ComponentType.TEXTFIELD, false);
			if (employeeNumber.getEnclosedObject() instanceof TextField) {
				TextField field = (TextField) employeeNumber.getEnclosedObject();
				field.setOutputMarkupId(true);
				field.setOutputMarkupPlaceholderTag(true);
				field.add(new AjaxFormComponentUpdatingBehavior("change") {
					private static final long serialVersionUID = 1L;

					@Override
					protected void onUpdate(AjaxRequestTarget target) {
						// update the Model
					}
				});
				employeeNumber = HelperPanel.getInstance(AgreementGUIField.EMPLOYEE_NUMBER.getFieldId(), field);
			}
		}
		return employeeNumber;
	}

	/**
	 * create Sales Category dropdown panel - RXS 1408 ADDED for Hierarchy FR3.2
	 * Sales Category - RAVISH SEHGAL
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private HelperPanel getSalesCategoryPanel() {
		SRSDropDownChoice dropDownChoice = null;
		if (salesCategoryPanel == null) {
			List<String> salesCategories = null;
			String division = null;
			int kind = 0;
			boolean modifyEditState = false;
			if (editState == EditStateType.MODIFY && SRSAuthWebSession.get().getContextDTO() != null
					&& SRSAuthWebSession.get().getContextDTO().getAgreementContextDTO() != null) {
				division = SRSAuthWebSession.get().getContextDTO().getAgreementContextDTO().getDivision().toString()
						.toLowerCase();
				kind = SRSAuthWebSession.get().getContextDTO().getAgreementContextDTO().getAgreementDivision()
						.getKind();
				modifyEditState = true;
			}

			salesCategories = guiController.getValidSalesCategories(kind, division, modifyEditState);
			// salesCategories = guiController.getValidSalesCategories(kind,
			// modifyEditState);

			dropDownChoice = new SRSDropDownChoice("value",
					new PropertyModel(panelModel.getAgreement(), AgreementGUIField.SALES_CATEGORY.getFieldId()),
					salesCategories, new ChoiceRenderer(), "Select");

			// Set Sales Category Value that was selected on the first screen of
			// the wizard.
			if (editState == EditStateType.ADD) {
				panelModel.getAgreement().setSalesCategory(getSalesCategory());
				dropDownChoice.setEnabled(false);
			}

			salesCategoryPanel = createDropDownChoicePanel(AgreementGUIField.SALES_CATEGORY, dropDownChoice);
			dropDownChoice.add(new AjaxFormComponentUpdatingBehavior("change") {

				@Override
				protected void onUpdate(AjaxRequestTarget target) {
					DropDownChoice salesCategroy = (DropDownChoice) salesCategoryPanel.getEnclosedObject();
					if (editState == EditStateType.ADD || editState == EditStateType.MODIFY) {
						String choiceValue = (String) salesCategroy.getModelObject();
						if (choiceValue.equalsIgnoreCase(LIBERTY_BRANCH_FRANCHISE_LBF)) {

							try {
								getGuiController().validateLBFRemuration(panelModel.getAgreement());
							} catch (ValidationException e) {
								AgreementDetailsPanel.this.error(
										"Please select the LBFRemunerationCategory Compulsary for Sales Category LBF");
								List<String> errorMessages = e.getErrorMessages();
								for (String string : errorMessages) {
									AgreementDetailsPanel.this.error(string);
								}
							}

							target.add(getFeedBackPanel());

							// validate the sales category
							// if lbf then must lbfremcat must be avalue
							//
						} else if (!choiceValue.equalsIgnoreCase(LIBERTY_BRANCH_FRANCHISE_LBF)) {
							target.add(getFeedBackPanel());
						}

						/*
						 * Market Integration Sweta Menon SSM2707 1st April 2016
						 */
						try {
							getGuiController().validateOldSalesCategoryForServPanel(choiceValue,
									SRSAuthWebSession.get().getContextDTO().getAgreementContextDTO().getSalesCategory(),
									panelModel.getAgreement().getPartyOid());
						} catch (ValidationException e) {
							List<String> errorMessages = e.getErrorMessages();
							for (String string : errorMessages) {
								AgreementDetailsPanel.this.error(string);
							}
							target.add(getFeedBackPanel());
						}

						try {
							getGuiController().validateNewSalesCategoryForServPanel(choiceValue,
									SRSAuthWebSession.get().getContextDTO().getAgreementContextDTO().getSalesCategory(),
									panelModel.getAgreement().getPartyOid());
						} catch (ValidationException e) {
							List<String> errorMessages = e.getErrorMessages();
							for (String string : errorMessages) {
								AgreementDetailsPanel.this.error(string);
							}
							target.add(getFeedBackPanel());
						}

						try {
							getGuiController().validateSalesCategoryForServByPanel(choiceValue,
									SRSAuthWebSession.get().getContextDTO().getAgreementContextDTO().getSalesCategory(),
									panelModel.getAgreement().getPartyOid());
						} catch (ValidationException e) {
							List<String> errorMessages = e.getErrorMessages();
							for (String string : errorMessages) {
								AgreementDetailsPanel.this.error(string);
							}
							target.add(getFeedBackPanel());
						}

						/*
						 * Market Integration Sweta Menon SSM2707 1st April 2016
						 */
					}
					/* SSM2707 Sweta Menon Agency Pool Begin */
					/*
					 * The sales category cannot be maintained if the agreement
					 * has an active Agency Pool Account. The Pool Account must
					 * be closed prior to Sales Category maintenance.
					 */
					if (editState == EditStateType.MODIFY) {
						String oldValue = editState == EditStateType.MODIFY
								&& SRSAuthWebSession.get().getContextDTO() != null
								&& SRSAuthWebSession.get().getContextDTO().getAgreementContextDTO() != null
								&& SRSAuthWebSession.get().getContextDTO().getAgreementContextDTO()
										.getSalesCategory() != null
												? SRSAuthWebSession.get().getContextDTO().getAgreementContextDTO()
														.getSalesCategory()
												: null;

						String selectedValue = panelModel.getAgreement().getSalesCategory();

						boolean isAgencyPoolClosed = getGuiController()
								.isAgencyPoolClosed(panelModel.getAgreement().getId());
						if (!isAgencyPoolClosed && selectedValue != null && oldValue != null
								&& !selectedValue.equalsIgnoreCase(oldValue)) {
							AgreementDetailsPanel.this.error(
									"Please close the Agency Pool Account before maintaining the Sales Category.");
							target.add(getFeedBackPanel());
						}

					}
					/* SSM2707 Sweta Menon Agency Pool End */

				}

			});
		}
		return salesCategoryPanel;
	}

	// SSM2707 Added for Hierarchy FR3.5 Primary Agreement - SWETA MENON - Begin
	/**
	 * create Primary Agreement property field
	 * 
	 * @return The primaryAgreementPanel object is returned.
	 */
	private HelperPanel getPrimaryAgreementPanel() {
		if (primaryAgreementPanel == null) {
			CheckBox checkBox = null;
			// If the agreement is being maintained
			if (editState.equals(EditStateType.MODIFY)) {

				if (guiController.isContextAgreementInProgressOrDeclined(getContext())) {
					checkBox = new CheckBox("value", new PropertyModel(getContext(), "primaryAgreement"));
					checkBox.setEnabled(false);
				} else {

					panelModel.setAgreement(guiController.getPrimaryAgreementValForAgreement(getContext()));

					checkBox = new CheckBox("value", new PropertyModel(getContext(), "primaryAgreement"));
					/*
					 * If the current agreement is the primary agreement (or)
					 * the primary agreement indicator is set to true the
					 * maintenance is restricted.
					 */
					if (getContext().isPrimaryAgreement()) {
						/*
						 * The maintenance of the Primary agreement field is
						 * disabled if the agreement is set as primary
						 */

						checkBox.setEnabled(false);
					} else if (!getContext().isPrimaryAgreement() && getContext().isprimaryAgreementForced()) {
						checkBox.setEnabled(false);
					} else {
						/*
						 * If the status of the agreement is InProgress or
						 * Declined, the maintenance of the Primary Agreement
						 * field is disabled.
						 */
						checkBox.setEnabled(true);
					}

					if (getContext().isprimaryAgreementForced() && getContext().isPrimaryAgreement()) {
						AgreementDetailsPanel.this.info("The current agreement - " + getContext().getId()
								+ " will be set as the advisers Primary " + AgreementKindType
										.getAgreementKindType(getContext().getKind()).getPrimaryAgreementDivisionType()
								+ " Agreement.");

					}
					checkBox.add(new AjaxFormComponentUpdatingBehavior("change") {

						@Override
						protected void onUpdate(AjaxRequestTarget target) {
							/*
							 * If the primary agreement check-box is ticked, and
							 * there exists an in-force agreement in the same
							 * channel for the party -- display an informational
							 * message stating that primary agreement will be
							 * changed for the current adviser-channel
							 * combination.
							 */
							CheckBox primAgmtCBox = (CheckBox) primaryAgreementPanel.getEnclosedObject();
							boolean newValue = primAgmtCBox.getModelObject();
							if (guiController.getCurrPrimaryAgreementNumber() > 0 && newValue) {
								AgreementDetailsPanel.this
										.info("Agreement Number " + guiController.getCurrPrimaryAgreementNumber()
												+ " will no longer be the advisers Primary "
												+ AgreementKindType.getAgreementKindType(getContext().getKind())
														.getPrimaryAgreementDivisionType()
												+ " Agreement the current agreement " + getContext().getId()
												+ " will be set as primary.");
							}
							if (target != null && getFeedBackPanel() != null) {
								target.add(getFeedBackPanel());
							}
						}
					});
				}
			} else if (editState.equals(EditStateType.ADD)) {
				/*
				 * Check if there exists an agreement in the current party-kind
				 * combination.
				 */
				// Identify if the current party is used to add a new agreement
				if (this.getPartyChoice()
						.equals(MaintainAgreementPageModel.PartyTypeSelection.CURRENT_PARTY.getDescription())) {

					Map<Long, Long> primaryAgreementMap = partyContext.getPrimaryAgreementMap();
					boolean isPrimaryAgmtVal = guiController.getPrimaryAgmntValForAddAgreement(primaryAgreementMap,
							panelModel.getAgreement().getKind(), partyContext.getOid());
					getContext().setPrimaryAgreement(Boolean.valueOf(isPrimaryAgmtVal));

					PrimaryAgreementDTO primaryAgmDTO = getGuiController()
							.determineContextPartiesPrimaryAgmtSOLOHCatchUP(partyContext.getOid());

					getContext().setPrimaryAgreementDTO(primaryAgmDTO);

				} else {
					/*
					 * New party for a new agreement. The current agreement is
					 * always set as the primary agreement.Set the
					 * primaryAgreement boolean to TRUE in the context
					 * AgreementDTO object
					 */
					getContext().setPrimaryAgreement(Boolean.TRUE);
				}
				checkBox = new CheckBox("value", new PropertyModel(getContext(), "primaryAgreement"));
				checkBox.add(new AjaxFormComponentUpdatingBehavior("change") {

					@Override
					protected void onUpdate(AjaxRequestTarget target) {
						/*
						 * If the primary agreement check-box is ticked, and
						 * there exists an in-force agreement in the same
						 * channel for the party -- display an informational
						 * message stating that primary agreement will be
						 * changed for the current adviser-channel combination.
						 */
						CheckBox primAgmtCBox = (CheckBox) primaryAgreementPanel.getEnclosedObject();
						boolean newValue = primAgmtCBox.getModelObject();
						if (guiController.getCurrPrimaryAgreementNumber() > 1 && newValue) {
							AgreementDetailsPanel.this
									.info("Agreement Number " + guiController.getCurrPrimaryAgreementNumber()
											+ " will no longer be the adviser�s Primary "
											+ AgreementKindType.getAgreementKindType(getContext().getKind())
													.getPrimaryAgreementDivisionType()
											+ " Agreement this newly-created agreement will be set as 6sprimary.");

						}
						if (target != null && getFeedBackPanel() != null) {
							target.add(getFeedBackPanel());
						}
					}
				});

				checkBox.setEnabled(true);
			} else if (editState.equals(EditStateType.AUTHORISE)) {
				checkBox = new CheckBox("value", new PropertyModel(getContext(), "primaryAgreement"));
				checkBox.setEnabled(false);
			} else {
				/*
				 * This block is executed for edit states other than
				 * ADD/MODIFY/AUTHORIZE
				 */
				boolean primaryAgmtIndicator = guiController.isCurrentAgreementPrimary(getContext());

				getContext().setPrimaryAgreement(Boolean.valueOf(primaryAgmtIndicator));

				checkBox = new CheckBox("value", new PropertyModel(getContext(), "primaryAgreement"));
				checkBox.setEnabled(false);
			}
			checkBox.setOutputMarkupId(true);
			checkBox.setOutputMarkupPlaceholderTag(true);
			primaryAgreementPanel = HelperPanel.getInstance(AgreementGUIField.PRIMARY_AGREEMENT.getFieldId(), checkBox);
		}

		// Return the Panel object
		return primaryAgreementPanel;
	}

	// SSM2707 Added for Hierarchy FR3.5 Primary Agreement - SWETA MENON - End

	/**
	 * create LBF Remuneration Category dropdown panel - MZL 26118 ADDED for
	 * Hierarchy FR2.8.2 LBF Remeneration Category - MOHAMMED LORGAT
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private HelperPanel getLBFRemunerationCategoryPanel() {
		if (lbfRemunerationCategoryPanel == null) {
			final SRSDropDownChoice dropDownChoice = new SRSDropDownChoice("value",
					new PropertyModel(panelModel.getAgreement(),
							AgreementGUIField.LBF_REMUNERATION_CATEGORY.getFieldId()),
					panelModel.getValidAgreementValues().getValidLBFRemunerationCategory(), new ChoiceRenderer(),
					"Select");
			dropDownChoice.setOutputMarkupId(true);
			dropDownChoice.setVisible(true);
			lbfRemunerationCategoryPanel = createDropDownChoicePanel(AgreementGUIField.LBF_REMUNERATION_CATEGORY,
					dropDownChoice);

			lbfRemunerationCategoryPanel.setOutputMarkupId(true);
			lbfRemunerationCategoryPanel.setVisible(true);

			dropDownChoice.add(new AjaxFormComponentUpdatingBehavior("change") {

				@Override
				protected void onUpdate(AjaxRequestTarget target) {
					DropDownChoice salesCategroy = (DropDownChoice) salesCategoryPanel.getEnclosedObject();

					if (editState == EditStateType.ADD || editState == EditStateType.MODIFY) {

						String choiceValue = (String) salesCategroy.getModelObject();
						if (choiceValue.equalsIgnoreCase(LIBERTY_BRANCH_FRANCHISE_LBF)) {
							System.out.println("lbf selected ");

							if (getEditState() == EditStateType.ADD) {
								AgreementDetailsPanel.this.error("Please add an LBF Main Home Branch");
								target.add(getFeedBackPanel());
								Button button = (Button) lbfHomeAddPanel.getEnclosedObject();
								button.setVisible(true);

								target.add(button);
								target.add(getFeedBackPanel());
								lbfHomeAddPanel.setVisible(true);
								Label label = (Label) lbfButtonGUIFieldPanel.getLabel();
								lbfButtonGUIFieldPanel.setVisible(true);
								label.setVisible(true);
								target.add(label);
								target.add(lbfHomeAddPanel);
								target.add(lbfButtonGUIFieldPanel);

							} else {
								try {
									getGuiController().validateLBFHomeRole(panelModel.getAgreement());
									// allow the save,

								} catch (ValidationException e) {
									for (String error : e.getErrorMessages()) {
										AgreementDetailsPanel.this.error(error);
										target.add(getFeedBackPanel());
										Button button = (Button) lbfHomeAddPanel.getEnclosedObject();
										button.setVisible(true);

										target.add(button);
										target.add(getFeedBackPanel());
										lbfHomeAddPanel.setVisible(true);
										Label label = (Label) lbfButtonGUIFieldPanel.getLabel();
										lbfButtonGUIFieldPanel.setVisible(true);
										label.setVisible(true);
										target.add(label);
										target.add(lbfHomeAddPanel);
										target.add(lbfButtonGUIFieldPanel);

									}
									AgreementDetailsPanel.this
											.error("Please add an LBF Home Role when selecting the LBF Sales Category");
									target.add(getFeedBackPanel());
								}
							}
						} else if (!choiceValue.equalsIgnoreCase(LIBERTY_BRANCH_FRANCHISE_LBF)) {
							Label label = (Label) lbfButtonGUIFieldPanel.getLabel();
							label.setVisible(false);
							lbfButtonGUIFieldPanel.setVisible(false);
							target.add(lbfButtonGUIFieldPanel);
							target.add(label);
						}
					}
				}
			});
		}
		return lbfRemunerationCategoryPanel;
	}
	
	@SuppressWarnings("unchecked")
	private HelperPanel getOrgMidSixPanel() {
		if (orgMidSixPanel == null) {
			orgMidSixPanel = createGUIPageField(AgreementGUIField.ORGANISATIONMIDSIX, panelModel.getAgreement(),
					ComponentType.TEXTFIELD, false);
			if (orgMidSixPanel.getEnclosedObject() instanceof TextField) {
				@SuppressWarnings("unused")
				TextField field = (TextField)orgMidSixPanel.getEnclosedObject();				
				field.add(new OrgMidSixValidator(panelModel.getAgreement().getId()));
				field.setEnabled(false);
			}
		}
					
		return orgMidSixPanel;
	}
	
	
	@SuppressWarnings("unchecked")
	private HelperPanel getSubsidyPanel() {
		SRSDropDownChoice dropDownChoice = null;
		if (subsidyPanel == null) {
			List<String> subsidies = null;
			boolean modifyEditState = false;
			if(panelModel != null && panelModel.getValidAgreementValues() !=null
					&& panelModel.getValidAgreementValues().getValidTransitionsForSubsidy() != null) {
				subsidies.addAll(panelModel.getValidAgreementValues().getValidTransitionsForSubsidy());
			}else {

				subsidies = guiController.getNewToIndustrySubsidyType();
			}
			dropDownChoice = new SRSDropDownChoice("value",
					new PropertyModel(panelModel.getAgreement(), AgreementGUIField.SUBSIDYNEWINDUSTRYTYPE.getFieldId()),
					subsidies, new ChoiceRenderer(), "Select");

			dropDownChoice.add(new AjaxFormComponentUpdatingBehavior("change") {
				private static final long serialVersionUID = 1L;

				@Override
				protected void onUpdate(AjaxRequestTarget target) {
					if(panelModel.getAgreement() != null) {
						
						String subsidyName = panelModel.getAgreement().getSubsidyNewIndustryType();
						int subsidyMonths = getGuiController().getNewToIndustrySubsidyMonth(subsidyName);
						
						//Auto populate the fields using the choice from the dropdown/ the values are fetched from the rating table 
						panelModel.getAgreement().setSubsidyNewIndustryAmount(getGuiController().getNewToIndustrySubsidyAmount(subsidyName));
						panelModel.getAgreement().setSubsidyNewIndustryStartDate(Calendar.getInstance().getTime());
						panelModel.getAgreement().setSubsidyNewIndustryEndDate(Date.from(LocalDate.now().plusMonths(subsidyMonths).
								atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));
						
						target.add(getSubsidyAmountPanel().getEnclosedObject());
						target.add(getSubsidyStartDatePanel().getEnclosedObject());
						target.add(getSubsidyEndDatePanel().getEnclosedObject());
						
					}
				}
			});
			subsidyPanel = createDropDownChoicePanel(AgreementGUIField.SUBSIDYNEWINDUSTRYTYPE, dropDownChoice);
		}
		return subsidyPanel;
	}
	
	@SuppressWarnings("unchecked")
	private HelperPanel getSubsidyAmountPanel() {
		if (subsidyAmountPanel == null) {
			subsidyAmountPanel = createGUIPageField(AgreementGUIField.SUBSIDYNEWINDUSTRYAMOUNT,
					panelModel.getAgreement(), ComponentType.TEXTFIELD, true);
			
			if (subsidyAmountPanel.getEnclosedObject() instanceof TextField) {
				@SuppressWarnings("unused")
				TextField field = (TextField) subsidyAmountPanel.getEnclosedObject();
				// add validation
				subsidyAmountPanel.setEnabled(false);
				field.add(new AttributeModifier("size", "11"));
				field.add(new AttributeModifier("maxlength", "30"));
			}
			
		}
		return subsidyAmountPanel;
	}

	
	private HelperPanel getSubsidyStartDatePanel() {
		if (subsidyStartDatePanel == null) {
			subsidyStartDatePanel = createGUIPageField(AgreementGUIField.SUBSIDYNEWINDUSTRYSTARTDATE,
					panelModel.getAgreement(), ComponentType.DATE_SELECTION_TEXTFIELD, true); 
			subsidyStartDatePanel.setOutputMarkupId(true);
			subsidyStartDatePanel.getEnclosedObject().setOutputMarkupId(true);
		}
		return subsidyStartDatePanel;
	}
	
	private HelperPanel getSubsidyEndDatePanel() {
		if (subsidyEndDatePanel == null) {
			subsidyEndDatePanel = createGUIPageField(AgreementGUIField.SUBSIDYNEWINDUSTRYENDDATE,
					panelModel.getAgreement(), ComponentType.DATE_SELECTION_TEXTFIELD, true);
			subsidyEndDatePanel.setOutputMarkupId(true);
			subsidyEndDatePanel.getEnclosedObject().setOutputMarkupId(true);
		}
		return subsidyEndDatePanel;
	}
	
	
	
	@SuppressWarnings({ "serial", "unchecked" })
	private class AgreementDetailsForm extends Form {

		private AgreementDetailsForm(String id) {
			super(id);
			RepeatingView leftPanel = new RepeatingView("leftPanel");
			RepeatingView rightPanel = new RepeatingView("rightPanel");
			rightPanel.setOutputMarkupId(true);
			add(leftPanel);
			add(rightPanel);
			
			/**
			 * Check validN2I
			 */
			
			if(panelModel.getAgreement().getKind() == AgreementKindType.AGENT.getKind()
					&& panelModel.getAgreement().getSalesCategory().equalsIgnoreCase(NEW_TO_INDUSTRY)) {
				setValidateSalesCat(true);				
			} else {
				setValidateSalesCat(false);
			}
			
			if (panelModel.getAgreement().getSubsidyNewIndustryType() ==null) {
				setValidateEditState(true);
			} else {
				setValidateEditState(false);
			}
			
			/**
			 * Left panel
			 */
			leftPanel.add(createGUIFieldPanel(AgreementGUIField.CONSULTANT_CODE, getConsultantCodePanel()));
			leftPanel.add(createGUIFieldPanel(AgreementGUIField.STATUS, getStatusPanel()));
			leftPanel.add(createGUIFieldPanel(AgreementGUIField.STATUS_REASON, getStatusReasonPanel()));
			leftPanel
					.add(createGUIFieldPanel(AgreementGUIField.PRODUCTION_CLUB_STATUS, getProductionClubStatusPanel()));
			/*
			 * leftPanel.add(createGUIFieldPanel(AgreementGUIField.
			 * PRIMARY_COMPANY_CONTRACTED_TO,
			 * getPrimaryCompanyContractedToPanel()));
			 * leftPanel.add(createGUIFieldPanel(AgreementGUIField.
			 * BROKER_BRANCH_FRANCHISE_GROUP, getBBFGroupPanel()));
			 */
			leftPanel.add(createGUIFieldPanel(AgreementGUIField.DEDICATED_SBFC_CONSULTANT_TYPE,
					getDedicatedSBFCConsultantTypePanel()));
			leftPanel.add(createGUIFieldPanel(AgreementGUIField.BROKER_CONSULTANT_PRODUCTION_CLUB_STATUS,
					getBrokerConsultantProductionClubStatusPanel()));
			leftPanel.add(createGUIFieldPanel(AgreementGUIField.MONTHLY_GUARANTEED_AMOUNT,
					getMonthlyGuaranteedAmountPanel()));
			/*
			 * leftPanel.add(createGUIFieldPanel(AgreementGUIField.SEGMENT,
			 * getSegmentPanel()));
			 */
			leftPanel.add(createGUIFieldPanel(AgreementGUIField.DONOTCALCINTEREST, getDoNotCalculateIntPanel()));
			leftPanel.add(createGUIFieldPanel(AgreementGUIField.FITPROPSEGMENT, getFitPropSegmentPanel()));
			leftPanel.add(createGUIFieldPanel(AgreementGUIField.KROLL_DONE, getKrollCheckPanel()));
			leftPanel.add(createGUIFieldPanel(AgreementGUIField.MANUAL_PRODUCTION_CLUB_STATUS,
					getManualProdClubStatusPanel()));
			leftPanel.add(manualProdClubGuiFieldPanel = createGUIFieldPanel(
					AgreementGUIField.MANUAL_PRODUCTION_CLUB_STATUS_END, null,
					getManualProdClubStatusEndDatePanel().getEnclosedObject(), true));
			leftPanel.add(createGUIFieldPanel(AgreementGUIField.CALCULATED_PRODUCTION_CLUB_STATUS,
					getCalcProdClubStatusPanel().getEnclosedObject()));
			manualProdClubGuiFieldPanel.setOutputMarkupId(true);

			// RXS 1408 ADDED for FR2 INCLUDE In Manpower Reporting - RAVISH
			// SEHGAL
			leftPanel.add(createGUIFieldPanel(AgreementGUIField.INCLUDE_IN_MANPOWER_REPORTING,
					getIncludeInManpowerReporting()));

			// Added for LCB Accreditation Project - 12/12/12-Pritam
			leftPanel.add(createGUIFieldPanel(AgreementGUIField.CORP_ADDENDUM_SIGNED, getCorpAddendumSignedPanel()));

			// PZM2509 - BEEE LIB4352
			leftPanel.add(createGUIFieldPanel(AgreementGUIField.SURETYDETAILS, getSuretyDetailsPanel()));

			//SBS0510-Autogen - Add field Orgmidsix only for Organsiation only if org midsix has any value		
					
			
			//Hide the midsix panel if no value exist
			if(Objects.nonNull(panelModel.getAgreement().getOrganisationMidsix()) &&
					!StringUtils.isEmpty(panelModel.getAgreement().getOrganisationMidsix().getValue())) {
				leftPanel.add(createGUIFieldPanel(AgreementGUIField.ORGANISATIONMIDSIX, getOrgMidSixPanel()));
			} 
			
			
			/**
			 * Right panel
			 */
			rightPanel.add(createGUIFieldPanel(AgreementGUIField.START_DATE, null,
					getStartDatePanel().getEnclosedObject(), true));
			endDateGUIPanel = createGUIFieldPanel(AgreementGUIField.END_DATE, null,
					getEndDatePanel().getEnclosedObject(), true);
			rightPanel.add(endDateGUIPanel);
			rightPanel
					.add(createGUIFieldPanel(AgreementGUIField.STATUS_DATE, getStatusDatePanel().getEnclosedObject()));
			rightPanel.add(createGUIFieldPanel(AgreementGUIField.COST_CENTER, getCostCenterPanel()));
			rightPanel.add(createGUIFieldPanel(AgreementGUIField.DIRECT_MANPOWER, getDirectManpowerPanel()));
			rightPanel.add(createGUIFieldPanel(AgreementGUIField.MANPOWER, getManpowerPanel()));
			// SSM2707 ADDED for FR15 Tenure SWETA MENON Begin
			rightPanel.add(createGUIFieldPanel(AgreementGUIField.LIBERTY_TENURE, getLibertyTenurePanel()));
			// SSM2707 ADDED for FR15 Tenure SWETA MENON End
			rightPanel.add(createGUIFieldPanel(AgreementGUIField.TITULAR_LEVEL, getTitularLevelPanel()));
			/*
			 * rightPanel.add(createGUIFieldPanel(AgreementGUIField.
			 * SUPPORT_TYPE, getSupportTypePanel()));
			 * rightPanel.add(createGUIFieldPanel(AgreementGUIField.ENTITY,
			 * getEntityPanel()));
			 * rightPanel.add(createGUIFieldPanel(AgreementGUIField.NETWORK,
			 * getNetworkPanel()));
			 */
			rightPanel.add(createGUIFieldPanel(AgreementGUIField.MY_BANKING_NUM, getMyBankingNumPanel()));
			rightPanel.add(
					createGUIFieldPanel(AgreementGUIField.HAS_MEDICAL_CREDITS, getHasMedicalAidCreditsPanelPanel()));
			rightPanel.add(createGUIFieldPanel(AgreementGUIField.STOPSTATEMENTDISTRIBUTION,
					getStopStatementDistributionPanel()));
			rightPanel.add(createGUIFieldPanel(AgreementGUIField.SOLE_PROPRIETOR, getSoleProprietorPanel()));

			// RXS 1408 ADDED for Hierarchy FR3.6 EmployeeNumber - RAVISH SEHGAL
			rightPanel.add(createGUIFieldPanel(AgreementGUIField.EMPLOYEE_NUMBER, getEmployeeNumber()));
			// RXS 1408 ADDED for Hierarchy FR3.2 Sales Category - RAVISH SEHGAL
			rightPanel.add(createGUIFieldPanel(AgreementGUIField.SALES_CATEGORY, getSalesCategoryPanel()));
			// MZL 2611 ADDED for FR2.8.2 LBF Remuneration Category - MOHAMMED
			// LORGAT
			rightPanel.add(createGUIFieldPanel(AgreementGUIField.LBF_REMUNERATION_CATEGORY,
					getLBFRemunerationCategoryPanel()));
			rightPanel.add(createGUIFieldPanel(AgreementGUIField.SCRIPTEDADVISORCHECK,
					getScriptedAdvisorCheckPanel()));

			lbfButtonGUIFieldPanel = createGUIFieldPanel(AgreementGUIField.LBF_HOME_ADD_BUTTON, getLBFButtonPanel());
			lbfButtonGUIFieldPanel.setVisible(false);
			lbfButtonGUIFieldPanel.getLabel().setOutputMarkupId(true);
			lbfButtonGUIFieldPanel.setOutputMarkupId(true);
			rightPanel.add(lbfButtonGUIFieldPanel);
			// SSM2707 Added for Hierarchy FR3.5 Primary Agreement - SWETA MENON
			rightPanel.add(createGUIFieldPanel(AgreementGUIField.PRIMARY_AGREEMENT, getPrimaryAgreementPanel()));
			// PZM2509 - BEEE LIB4352
			rightPanel.add(
					createGUIFieldPanel(AgreementGUIField.ISPERSONALSERVICESTRUST, getIsPersonalServicesTrustPanel()));
			
			GUIFieldPanel subsidyNewIndustryTypePanel = createGUIFieldPanel(
					AgreementGUIField.SUBSIDYNEWINDUSTRYTYPE,
					null, getSubsidyPanel().getEnclosedObject(),true);
			subsidyNewIndustryTypePanel.setEnabled(false);
			subsidyNewIndustryTypePanel.setVisible(false);
			rightPanel.add(subsidyNewIndustryTypePanel);
			
			//Add the amount here
			GUIFieldPanel subsidyNewIndustryAmountPanel = createGUIFieldPanel(
					AgreementGUIField.SUBSIDYNEWINDUSTRYAMOUNT,
					null, getSubsidyAmountPanel().getEnclosedObject(),false);
			subsidyNewIndustryAmountPanel.setEnabled(false);
			subsidyNewIndustryAmountPanel.setVisible(false);
			rightPanel.add(subsidyNewIndustryAmountPanel);

			
			GUIFieldPanel subsidyNewIndustryStartDatePanel = createGUIFieldPanel(
					AgreementGUIField.SUBSIDYNEWINDUSTRYSTARTDATE,
					null, getSubsidyStartDatePanel().getEnclosedObject(), false);
			subsidyNewIndustryStartDatePanel.setEnabled(false);
			subsidyNewIndustryStartDatePanel.setVisible(false);
			
			rightPanel.add(subsidyNewIndustryStartDatePanel);
			
			GUIFieldPanel subsidyNewIndustryEndDatePanel = createGUIFieldPanel(
					AgreementGUIField.SUBSIDYNEWINDUSTRYENDDATE, 
					null, getSubsidyEndDatePanel().getEnclosedObject(), true);
			subsidyNewIndustryEndDatePanel.setVisible(false);
			rightPanel.add(subsidyNewIndustryEndDatePanel);
			

			
			if(isValidateSalesCat()) {
				subsidyNewIndustryTypePanel.setVisible(true);
				subsidyNewIndustryStartDatePanel.setVisible(true);
				subsidyNewIndustryEndDatePanel.setVisible(true);
				subsidyNewIndustryAmountPanel.setVisible(true);
			}
			
			if (isValidateEditState()) {
				subsidyNewIndustryTypePanel.setEnabled(true);
				subsidyNewIndustryStartDatePanel.setEnabled(false);
				subsidyNewIndustryEndDatePanel.setEnabled(true);
				subsidyNewIndustryAmountPanel.setEnabled(false);
			}
			
			// rightPanel.add(createGUIFieldPanelLBF(AgreementGUIField.LBF_HOME_ADD_BUTTON,
			// AgreementGUIField.LBF_HOME_ADD_BUTTON.getDescription(),
			// lbfHomeAddPanel.getEnclosedObject(),false, true));

			// Temporarily Commented-30/4/13 - Tobe removed later when LCB goes
			// live-Pritam.
			/*
			 * rightPanel.add(createGUIFieldPanel(AgreementGUIField.
			 * HOLD_CORP_COMMISSION, getHoldCorpCommissionPanel()));
			 */

			/**
			 * Early debits
			 */
			// TODO
			// add(getEarlyDebitsContainer());
			/**
			 * Preauth panel
			 */
			add(getAuthLimitsContainer());
			/**
			 * Fit and proper panel
			 */
			add(getFitAndProperPropertiesContainer());
			add(addLBFWindow = createModalWindow("agreementHierarcyAdd"));

			/**
			 * Update visibility
			 */
			checkFieldVisibility();

		}

	}

	@Override
	public void checkFieldVisibility() {
		super.checkFieldVisibility();
		/**
		 * Early debits will be invisible if no sub-components are visible
		 */
		getEarlyDebitsContainer().setVisible(
				isVisible(AgreementGUIField.EARLY_DEBITS_INDICATOR) || isVisible(AgreementGUIField.EARLY_DEBITS_REASON)
						|| isVisible(AgreementGUIField.EARLY_DEBITS_START_DATE));
		/**
		 * Auth limits will be invisible if no sub-components are visible
		 */
		getAuthLimitsContainer().setVisible(isVisible(AgreementGUIField.PREAUTH_AMOUNT)
				|| isVisible(AgreementGUIField.PREAUTH_CATEGORY) || isVisible(AgreementGUIField.PREAUTH_OVERRIDE));
	}

	public WebMarkupContainer getAuthLimitsContainer() {
		if (authLimitsContainer == null) {
			authLimitsContainer = new WebMarkupContainer("authLimitsContainer");
			authLimitsContainer.setOutputMarkupId(true);
			authLimitsContainer.setOutputMarkupPlaceholderTag(true);
			RepeatingView preauthPanel = new RepeatingView("preauthPanel");
			authLimitsContainer.add(preauthPanel);
			/**
			 * Components
			 */
			preauthPanel.add(createGUIFieldPanel(AgreementGUIField.PREAUTH_CATEGORY, getPreAuthLimitCategoryPanel()));
			preauthPanel.add(createGUIFieldPanel(AgreementGUIField.PREAUTH_AMOUNT, getPreAuthLimitAmountPanel()));
			preauthPanel.add(createGUIFieldPanel(AgreementGUIField.PREAUTH_OVERRIDE, getPreAuthOverridePanel()));

		}
		return authLimitsContainer;
	}

	public EditStateType getEditState() {
		return editState;
	}

	private RepeatingView getEarlyDebitsPanel() {
		if (earlyDebitsPanel == null) {
			earlyDebitsPanel = new RepeatingView("earlyDebitsPanel");
			earlyDebitsPanel
					.add(createGUIFieldPanel(AgreementGUIField.EARLY_DEBITS_REASON, getEarlyDebitsReasonPanel()));
			earlyDebitsPanel
					.add(createGUIFieldPanel(AgreementGUIField.EARLY_DEBITS_INDICATOR, getEarlyDebitsIndicatorPanel()));
			earlyDebitsPanel.add(
					createGUIFieldPanel(AgreementGUIField.EARLY_DEBITS_START_DATE, getEarlyDebitsStartDatePanel()));
		}
		return earlyDebitsPanel;
	}

	public WebMarkupContainer getEarlyDebitsContainer() {
		if (earlyDebitsContainer == null) {
			earlyDebitsContainer = new WebMarkupContainer("earlyDebitsContainer");
			earlyDebitsContainer.add(getEarlyDebitsPanel());
		}
		return earlyDebitsContainer;

	}

	@SuppressWarnings("unchecked")
	public Class getPanelClass() {
		return AgreementDetailsPanel.class;
	}

	@SuppressWarnings("unused")
	private void setStatusVisibility() {
	}

	private IConverter getEarlyDebitsConverter() {
		if (earlyDebitsConverter == null) {
			earlyDebitsConverter = new IConverter() {

				private static final long serialVersionUID = 1L;

				public Object convertToObject(String stringValue, Locale locale) {
					if (stringValue == null) {
						return null;
					}
					return new EarlyDebitsReasonDTO(stringValue);
				}

				public String convertToString(Object objectValue, Locale locale) {
					if (objectValue != null && objectValue instanceof EarlyDebitsReasonDTO) {
						return ((EarlyDebitsReasonDTO) objectValue).getValue();
					}
					return "";
				}

			};

		}
		return earlyDebitsConverter;
	}

	// RXS1408 - Changes to disable checkboxs when there is raised request.
	private boolean getOutStandingRequest() {
		if (getOutStandingRequestKinds().size() == 0) {
			getOutstandingRequests = true;
		}
		return false;
	}

	/**
	 * Create the modal window
	 * 
	 * @param id
	 * @return
	 */
	public ModalWindow createModalWindow(String id) {
		final ModalWindow window = new ModalWindow(id);

		// Create the page
		window.setPageCreator(new ModalWindow.PageCreator() {
			private static final long serialVersionUID = 1L;

			public Page createPage() {
				if (getEditState() == EditStateType.ADD) {
					initPageModel();
				}
				currentPageModel = AgreementDetailsPanel.this.getMaintainAgreementPageModel();
				SIMSLBFAddPage simsLBFAddPage = new SIMSLBFAddPage("lbfPage",
						AgreementDetailsPanel.this.getMaintainAgreementPageModel(), getEditState(), getParentPage(),
						window);
				simsLBFAddPage.setOutputMarkupId(true);
				return simsLBFAddPage;
			}

		});

		// Close window call back
		window.setWindowClosedCallback(new ModalWindow.WindowClosedCallback() {
			private static final long serialVersionUID = 1L;

			public void onClose(AjaxRequestTarget target) {
				maintainAgreementPageModel = currentPageModel;
				initPanelModelHomeRoles();
				panelModel.getAgreement().setCurrentAndFutureHomeRoles(
						currentPageModel.getMaintainAgreementDTO().getAgreementDTO().getCurrentAndFutureHomeRoles());
			}

		});

		// Initialise window settings
		window.setMinimalHeight(320);
		window.setInitialHeight(320);
		window.setMinimalWidth(750);
		window.setInitialWidth(750);
		window.setMaskType(MaskType.SEMI_TRANSPARENT);
		window.setCssClassName(ModalWindow.CSS_CLASS_GRAY);

		return window;
	}

	public MaintainAgreementPageModel getMaintainAgreementPageModel() {
		return maintainAgreementPageModel;
	}

	public void setMaintainAgreementPageModel(MaintainAgreementPageModel maintainAgreementPageModel) {
		this.maintainAgreementPageModel = maintainAgreementPageModel;
	}

	private void initPageModel() {
		if (maintainAgreementPageModel == null) {
			error("Page Model should never be null, Please call support if you continue seeing this error");
			maintainAgreementPageModel = new MaintainAgreementPageModel(new AgreementDTO(),
					new ValidAgreementValuesDTO());
		}
		if (maintainAgreementPageModel.getMaintainAgreementDTO() == null) {
			error("An agreement needs to be selected to adjust the hierarchy");
			maintainAgreementPageModel.setMaintainAgreementDTO(new MaintainAgreementDTO());
		}
		if (maintainAgreementPageModel.getMaintainAgreementDTO().getAgreementDTO() == null) {
			error("An agreement needs to be selected to adjust the hierarchy");
			maintainAgreementPageModel.getMaintainAgreementDTO().setAgreementDTO(new AgreementDTO());
		}
		if (maintainAgreementPageModel.getMaintainAgreementDTO().getAgreementDTO()
				.getCurrentAndFutureHomeRoles() == null) {
			maintainAgreementPageModel.getMaintainAgreementDTO().getAgreementDTO()
					.setCurrentAndFutureHomeRoles(new ArrayList<AgreementHomeRoleDTO>());
		}
		if (maintainAgreementPageModel.getMaintainAgreementDTO().getAgreementDTO()
				.getCurrentAndFutureAgreementRoles() == null) {
			maintainAgreementPageModel.getMaintainAgreementDTO().getAgreementDTO()
					.setCurrentAndFutureAgreementRoles(new ArrayList<AgreementRoleDTO>());
		}
		//
	}

	public String getSalesCategory() {
		return salesCategory;
	}

	public void setSalesCategory(String salesCategory) {
		this.salesCategory = salesCategory;
	}

	private void initPanelModelHomeRoles() {
		if (panelModel.getAgreement().getCurrentAndFutureHomeRoles() == null) {
			panelModel.getAgreement().setCurrentAndFutureHomeRoles(new ArrayList<AgreementHomeRoleDTO>());
		}

		//
	}

	// SSM2707 Added for Hierarchy FR3.5 Primary Agreement - SWETA MENON
	public String getPartyChoice() {
		return partyChoice;
	}

	public void setPartyChoice(String partyChoice) {
		this.partyChoice = partyChoice;
	}

	public Long getPrimaryAgreementNumber() {
		return primaryAgreementNumber;
	}

	public void setPrimaryAgreementNumber(Long primaryAgreementNumber) {
		this.primaryAgreementNumber = primaryAgreementNumber;
	}
	// SSM2707 Added for Hierarchy FR3.5 Primary Agreement - SWETA MENON
	
	public boolean isValidateSalesCat() {
		return validateSalesCat;
	}

	public void setValidateSalesCat(boolean validateSalesCat) {
		this.validateSalesCat = validateSalesCat;
	}

	public boolean isValidateEditState() {
		return validateEditState;
	}

	public void setValidateEditState(boolean validateEditState) {
		this.validateEditState = validateEditState;
	}

	
}
