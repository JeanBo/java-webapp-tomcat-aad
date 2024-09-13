package za.co.liberty.web.pages.maintainagreement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.naming.NamingException;

import org.apache.commons.lang.SerializationUtils;
import org.apache.log4j.Logger;
import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.wizard.IWizardStep;
import org.apache.wicket.extensions.wizard.WizardModel;
import org.apache.wicket.extensions.wizard.WizardStep;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

import za.co.liberty.business.guicontrollers.agreement.IAgreementGUIController;
import za.co.liberty.dto.agreement.AgreementDTO;
import za.co.liberty.dto.agreement.AgreementHomeRoleDTO;
import za.co.liberty.dto.agreement.AgreementRoleDTO;
import za.co.liberty.dto.agreement.ConsCodeGenerationDTO;
import za.co.liberty.dto.agreement.PaymentSchedulerDTO;
import za.co.liberty.dto.agreement.maintainagreement.MaintainAgreementDTO;
import za.co.liberty.dto.agreement.maintainagreement.MaintainPartyDTO;
import za.co.liberty.dto.agreement.maintainagreement.SalesCategoryDTO;
import za.co.liberty.dto.agreement.maintainagreement.ValidAgreementValuesDTO;
import za.co.liberty.dto.agreement.properties.PaysToDTO;
import za.co.liberty.dto.agreement.request.RaiseGuiRequestResultDTO;
import za.co.liberty.dto.party.EmployeeDTO;
import za.co.liberty.dto.party.PartyDTO;
import za.co.liberty.dto.party.PersonDTO;
import za.co.liberty.dto.party.aqcdetail.AdvisorQualityCodeDTO;
import za.co.liberty.dto.party.aqcdetail.EffectiveAQCDTO;
import za.co.liberty.dto.party.contactdetail.CommunicationPreferenceDTO;
import za.co.liberty.dto.party.contactdetail.ContactPreferenceDTO;
import za.co.liberty.dto.party.contactdetail.ContactPreferenceWrapperDTO;
import za.co.liberty.dto.rating.SegmentDTO;
import za.co.liberty.dto.rating.SegmentNameDTO;
import za.co.liberty.dto.userprofiles.ContextDTO;
import za.co.liberty.dto.userprofiles.ContextPartyDTO;
import za.co.liberty.dto.userprofiles.ISessionUserProfile;
import za.co.liberty.exceptions.data.ConformanceTypeException;
import za.co.liberty.exceptions.data.DataNotFoundException;
import za.co.liberty.exceptions.data.ValidationException;
import za.co.liberty.exceptions.error.request.RequestException;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.helpers.config.HelperConfigParameterTypes;
import za.co.liberty.helpers.config.HelpersParameterFactory;
import za.co.liberty.helpers.lookup.ServiceLocator;
import za.co.liberty.interfaces.agreements.AgreementKindType;
import za.co.liberty.interfaces.agreements.requests.RequestKindType;
import za.co.liberty.interfaces.party.PartyType;
import za.co.liberty.interfaces.persistence.party.flow.IPartyNameAgreementFLO;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.data.enums.PanelToRequestMapping;
import za.co.liberty.web.pages.BaseWindowPage;
import za.co.liberty.web.pages.contactdetail.ContactDetailsPanel;
import za.co.liberty.web.pages.maintainagreement.model.AdvisorQualityCodePanelModel;
import za.co.liberty.web.pages.maintainagreement.model.AgreementDetailsPanelModel;
import za.co.liberty.web.pages.maintainagreement.model.FAISLicensePanelModel;
import za.co.liberty.web.pages.maintainagreement.model.MaintainAgreementPageModel;
import za.co.liberty.web.pages.maintainagreement.model.MaintainAgreementPageModel.PartyTypeSelection;
import za.co.liberty.web.pages.maintainagreement.model.MaintainAgreementPageModelFactory;
import za.co.liberty.web.pages.party.BankingDetailsPanel;
import za.co.liberty.web.pages.party.MedicalAidDetailsPanel;
import za.co.liberty.web.pages.party.OrganisationDetailsPanel;
import za.co.liberty.web.pages.party.PartyTypePanel;
import za.co.liberty.web.pages.party.PersonDetailsPanel;
import za.co.liberty.web.pages.party.model.MaintainPartyPageModel;
import za.co.liberty.web.pages.party.model.MedicalAidDetailsPanelModel;
import za.co.liberty.web.pages.wizard.SRSPopupWizard;
import za.co.liberty.web.pages.wizard.object.SRSWizardPageDetail;
import za.co.liberty.web.system.SRSAuthWebSession;
import za.co.liberty.web.wicket.modal.SRSModalWindow;
import za.co.liberty.web.wicket.validation.maintainagreement.BankConsultantCodeFormValidator;
import za.co.liberty.web.wicket.validation.maintainagreement.StatusReasonDebarmentFormValidator;
import za.co.liberty.web.wicket.validation.maintainagreement.StatusReasonFraudFormValidator;

/**
 * This wizard is used to add agreements
 * 
 */
public class AddAgreementWizard extends SRSPopupWizard<MaintainAgreementPageModel> {

	private static final long serialVersionUID = -8373423523813458729L;

	private boolean disableFirstStep = false;
	private MaintainAgreementPageModel pageModel;
	private MaintainPartyPageModel partyPageModelNewParty;
	private MaintainPartyPageModel partyPageModelExistingParty;
	private transient IAgreementGUIController guiController;
	private transient Logger logger;
	private SRSModalWindow parentWindow;
	private boolean agreementTypeSelected = false;

////	private FAISLicensePanel faisLicensePanel;
	private DistributionPaysToPanel distributionPaysToPanel;
	private AssociatedAgreementsPanel associatedAgreementsPanel;
	private AgreementHierarchyPanel agreementHierarchyPanel;
	private AgreementCodesPanel associatedCodesPanel;
	private ProvidentFundDetailsPanel providentFundDetailsPanel;

	private ContextPartyDTO partyContext;
	private BankConsultantCodeFormValidator bankConsultantCodeFormValidator;

	private SalesCategoryDTO screenMappingDTO = null;
	String partyType = "";
	boolean check = false;

	// For Differential pricing - 09/10/10
	@SuppressWarnings("unused")
	private AdvisorQualityCodePanel advisorQualityCodePanel;

	private Long targetAgreementNumber;
	private Page parentPage;

	private boolean midSixGenerated = false;

	public AddAgreementWizard(String id, SRSModalWindow parentWindow, ContextPartyDTO partyContext, Page parentPage) {
		super(id, parentWindow, parentPage.getPageReference());
		this.parentWindow = parentWindow;
		this.partyContext = partyContext;
		this.parentPage = parentPage;
		/**
		 * Add party context to the model, as the call to super will automatically
		 * instantiate the model before the party context can ever be used. This manual
		 * step is required to deal with the side effect of the
		 * super->initializePageModel method calls
		 */
		MaintainAgreementPageModelFactory.updateModelWithExistingParty(getGuiController(), partyContext, pageModel);

		/*
		 * UIndicate whether existing party has banking details or not.
		 */
		if (pageModel != null && pageModel.getPartyTypeSelection() != null && pageModel.getPartyTypeSelection()
				.equals(PartyTypeSelection.CURRENT_PARTY)) {
			pageModel.setPartyHasBankingDetails(
					getGuiController().hasPartyBankingDetails(pageModel.getExistingPartyDetails().getOid()));
			getLogger().info("Party hasBankingDetails = " + pageModel.isPartyHasBankingDetails());
		}
		// Update session variable one more time
		parentWindow.setSessionModelForPage(this.pageModel);

		bankConsultantCodeFormValidator = new BankConsultantCodeFormValidator(null,
				getGuiController().getBankAgreementNumbers());
		this.getForm().add(bankConsultantCodeFormValidator);
	}

	public Logger getLogger() {
		if (logger == null) {
			logger = Logger.getLogger(AddAgreementWizard.class);
		}
		return logger;
	}

	/**
	 * Create a context based add agreement wizard model that will control the flow
	 * of the wizard
	 */
	@Override
	protected WizardModel createWizardModel() {
		WizardModel model = new WizardModel() {

			private static final long serialVersionUID = 1L;

			@Override
			public void next() {

				if (getLogger().isDebugEnabled()) {
					getLogger().debug("Next   active.step=" + super.getActiveStep());

				}
				if (super.getActiveStep().getClass().equals(AgreementIntroStep.class)) {
					screenMappingsCall();
					check = false;
					if (getPartyPageModel() == null || getPartyPageModel().getPartyDTO() == null
							|| getPartyPageModel().getPartyDTO() instanceof PersonDTO) {
						partyType = "Person";
					} else {
						partyType = "Organisation";
					}
					if (!partyType.equalsIgnoreCase(screenMappingDTO.getPartyType())) {
						try {
							List<String> errors = new ArrayList<String>();
							errors.add("Cannot create [" + partyType + "] agreement for Sales Category: "
									+ screenMappingDTO.getSalesCategory());
							check = true;
							throw new ValidationException(errors);
						} catch (ValidationException ex) {
							for (String error : ex.getErrorMessages()) {
								getFeedback().error(error);
							}
						}
					}
					if ((pageModel.getPartyTypeSelection() != null
							&& pageModel.getPartyTypeSelection().getDescription() != null)
							&& (pageModel.getPartyTypeSelection().getDescription().equals(
									PartyTypeSelection.CURRENT_PARTY.getDescription()))) {

					}
				}
				
				// AVS Defect fix- SBS0510-Display Warning msg on Banking Details Step only and
				// then proceed to next

				if (super.getActiveStep().getClass().equals(BankingDetailsStep.class)) {
					if (getPartyPageModel() != null) {

						if (getPartyPageModel().isWarningOnlyOnAVSCall())
							check = true;
						else
							check = false;
					}

				}
				
				if (!check) {
					super.next();
					disableFirstStep = true;
				}
				/**
				 * Do additional next operations to skip panels
				 */
				if (isNextAvailable() && skipCurrentPanel()) {
					next();
				}
			}

			@Override
			public void previous() {
				super.previous();
				// check = false;
				/**
				 * Do additional previous operations to skip panels
				 */
				if (isPreviousAvailable() && skipCurrentPanel()) {
					previous();
				}
			}

			// RXS1408 - 3.2 Sales Category
			private void screenMappingsCall() {
				int agreementKindId = pageModel.getMaintainAgreementDTO().getSalesCategoryAgreementKindDTO()
						.getAgreementKindId();
				String SalesCategory = pageModel.getMaintainAgreementDTO().getSalesCategoryDTO().getSalesCategory();
				screenMappingDTO = getGuiController().getSalesCategoryScreensMapping(SalesCategory, agreementKindId,
						partyType);
//				Setting this DTO for AgreementGUICONTROLLER VALIDATIONS for NULL POINTER WHILE CREATING AN AGREEMENT.
				pageModel.getMaintainAgreementDTO().setScreenMappings(screenMappingDTO);
			}

			private boolean skipScreen(int screenMappingVal) {
				if (screenMappingVal == 0) {
					return true; // yes skip
				} else {
					return false;
				}
			}

			private boolean skipCurrentPanel() {
				/*
				 * if (super.getActiveStep().getClass().equals(AgreementIntroStep.class)) {
				 * screenMappingsCall(); if(getPartyPageModel() == null ||
				 * getPartyPageModel().getPartyDTO() == null ||
				 * getPartyPageModel().getPartyDTO() instanceof PersonDTO){ partyType =
				 * "Person"; }else { partyType = "Organisation"; }
				 * if(!partyType.equalsIgnoreCase(screenMappingDTO.getPartyType())){ try{
				 * List<String> errors = new ArrayList<String>(); errors.add("Cannot create ["+
				 * partyType +"] agreement for Sales Category: "+
				 * screenMappingDTO.getSalesCategory()); check = false; throw new
				 * ValidationException(errors); }catch(ValidationException ex){ for(String error
				 * : ex.getErrorMessages()){ error(error); } } }else{ check = true;
				 * super.next(); return false; } }
				 */
				if (super.getActiveStep().getClass().equals(AgreementAssociatedCodesStep.class)
						&& skipScreen(screenMappingDTO.getAssociatedCodes())) {
					return true;
				}
				if (super.getActiveStep().getClass().equals(AssociatedAgreementsStep.class)
						&& skipScreen(screenMappingDTO.getAssociatedAgreements())) {
					return true;
				}
				if (super.getActiveStep().getClass().equals(FAISDetailsStep.class)
						&& skipScreen(screenMappingDTO.getFAISDetails())) {
					return true;
				}
				if (super.getActiveStep().getClass().equals(AdvisorQualityCodeStep.class)
						&& skipScreen(screenMappingDTO.getAQCDetails())) {
					return true;
				}

				if (super.getActiveStep().getClass().equals(PartyTypeSelectionStep.class)) {
					if (pageModel != null && pageModel.getPartyTypeSelection() != null
							&& pageModel.getPartyTypeSelection()
									.equals(PartyTypeSelection.CURRENT_PARTY)) {
						return true;
					}
				}
				if (super.getActiveStep().getClass().equals(PaymentSchedulerStep.class)
						&& skipScreen(screenMappingDTO.getPaymentScheduler())) {
					/**
					 * Skip payment scheduler step when the agreement kind is LAW Salaried Employee
					 */
					/*
					 * if (pageModel!=null && pageModel.getMaintainAgreementDTO()!=null &&
					 * pageModel.getMaintainAgreementDTO().getAgreementDTO()!=null &&
					 * pageModel.getMaintainAgreementDTO().getAgreementDTO().getKind()==
					 * AgreementKindType.LIBERTY_ATWORK_SALARIEDEMP.getKind()) {
					 */
					return true;
//					}
				}

				if (super.getActiveStep().getClass().equals(BankingDetailsChoiceStep.class)) {
					if (skipBankDetChoiceStep()) {
						return true;
					}
				}

				// TODO get this from the PSD
				if (super.getActiveStep().getClass().equals(ProvidentFundDetailsStep.class)
						&& skipScreen(screenMappingDTO.getProvidentFund())) {
					// if agreement kind is not agent then we skip
					/*
					 * if (pageModel.getMaintainAgreementDTO().getAgreementDTO().getKind()!=
					 * AgreementKindType.AGENT.getKind()) {
					 */
					return true;
					/* } */
				}

				if (super.getActiveStep().getClass().equals(BankingDetailsStep.class)) {
					/**
					 * Skip Banking Details if 'No Banking Details to be added' is selected by user
					 * 
					 */
					if (pageModel != null && pageModel.getBankingDetailsRequiredSelection() != null
							&& pageModel.getBankingDetailsRequiredSelection()
									.equals(MaintainAgreementPageModel.BankingDetailsRequiredSelection.NO)) {
						return true;
					} // return false;
				}

				if (super.getActiveStep().getClass().equals(MedicalDetailsStep.class)
						&& skipScreen(screenMappingDTO.getMedicalAidDetails())) {
					// skip if not a person
					MaintainPartyPageModel partyModel = getPartyPageModel();
					if (partyModel == null || partyModel.getPartyDTO() == null
							|| !(partyModel.getPartyDTO() instanceof PersonDTO)) {
						return true;
					}
				}
				return false;
			}

			// Skip Banking Details Choice Panel for New Party for which pay to Org is
			// selected.
			private boolean skipBankDetChoiceStep() {

				if (pageModel == null) {
					return false;
				}
				boolean hasPaysToOrg = pageModel.getMaintainAgreementDTO() != null
						&& pageModel.getMaintainAgreementDTO().getAgreementDTO() != null
						&& pageModel.getMaintainAgreementDTO().getAgreementDTO().getPaymentDetails() != null
						&& pageModel.getMaintainAgreementDTO().getAgreementDTO().getPaymentDetails().getPayto() != null
						&& pageModel.getMaintainAgreementDTO().getAgreementDTO().getPaymentDetails()
								.getPayto() == PaysToDTO.PayToType.ORGANISATION;

				boolean isExistingParty = pageModel.getPartyTypeSelection() != null && pageModel.getPartyTypeSelection()
						.equals(PartyTypeSelection.CURRENT_PARTY);

				return (hasPaysToOrg || (isExistingParty && pageModel.isPartyHasBankingDetails()));

			}

		};
		return model;
	}

	/**
	 * Instantiate the steps that will be utilized by the wizard
	 */
	@Override
	protected Collection<SRSWizardPageDetail> getWizardSteps(MaintainAgreementPageModel pageModel) {
		List<SRSWizardPageDetail> ret = new ArrayList<SRSWizardPageDetail>();
		ret.add(new SRSWizardPageDetail(new AgreementIntroStep()));
		ret.add(new SRSWizardPageDetail(new PartyDetailsStep()));
		// Busy testing
		ret.add(new SRSWizardPageDetail(new PartyContactDetailsStep()));
		ret.add(new SRSWizardPageDetail(new MedicalDetailsStep()));
		ret.add(new SRSWizardPageDetail(new AgreementDetailsStep()));

		/*
		 * ret.add(new SRSWizardPageDetail("Associated Codes", associatedCodesPanel =
		 * new AgreementCodesPanel( SRSPopupWizard.SRS_WIZARD_STEP_ID,
		 * EditStateType.ADD, this.pageModel))); ret.add(new
		 * SRSWizardPageDetail("Distribution & Pays To", distributionPaysToPanel = new
		 * DistributionPaysToPanel( SRSPopupWizard.SRS_WIZARD_STEP_ID, this.pageModel,
		 * getFeedback(), EditStateType.ADD)));
		 */

//		To create a step please add AddAgreementWizard$AgreementAssociatedCodesStep.html
		ret.add(new SRSWizardPageDetail(new AgreementAssociatedCodesStep()));
		ret.add(new SRSWizardPageDetail(new DistributionAndPaysToStep()));
		ret.add(new SRSWizardPageDetail(new ProvidentFundDetailsStep()));

		if (HelpersParameterFactory.getInstance().getParameter(
				HelperConfigParameterTypes.ADD_AGREEMENT_BANKING_DETAILS_DISABLE, Boolean.class) == false) {
			ret.add(new SRSWizardPageDetail(new BankingDetailsChoiceStep()));
			ret.add(new SRSWizardPageDetail(new BankingDetailsStep()));
		}

		ret.add(new SRSWizardPageDetail(new PaymentSchedulerStep()));
		ret.add(new SRSWizardPageDetail(new AssociatedAgreementsStep()));
		ret.add(new SRSWizardPageDetail(new HierarchyDetailsStep()));

		/*
		 * ret.add(new SRSWizardPageDetail("Associated Agreements", new
		 * AssociatedAgreementsPanel(SRSPopupWizard.SRS_WIZARD_STEP_ID,
		 * this.pageModel,getFeedback(),EditStateType.ADD,parentPage))); ret.add(new
		 * SRSWizardPageDetail("Hierarchy Details", new
		 * AgreementHierarchyPanel(SRSPopupWizard.SRS_WIZARD_STEP_ID,
		 * this.pageModel,EditStateType.ADD,getFeedback(),null)));
		 */

		ret.add(new SRSWizardPageDetail(new FAISDetailsStep()));
		// For Differential Pricing
		ret.add(new SRSWizardPageDetail(new AdvisorQualityCodeStep()));

		ret.add(new SRSWizardPageDetail("Workflow Details", new WorkflowPanel(SRSPopupWizard.SRS_WIZARD_STEP_ID,
				EditStateType.ADD, this.pageModel.getMaintainAgreementDTO().getWorkflowDTO())));
		return ret;
	}

	private FAISLicensePanelModel createFAISLicensePanelModel() {
		return new FAISLicensePanelModel(this.pageModel);
	}

	private final class PartyTypeSelectionStep extends WizardStep {

		private static final long serialVersionUID = 1L;

		public PartyTypeSelectionStep() {
			add(new PartyTypePanel("partyTypePanel", getPartyPageModel(), EditStateType.ADD));
		}
	}

	private final class AgreementIntroStep extends WizardStep {
		private static final long serialVersionUID = 1L;

		public AgreementIntroStep() {
			setTitleModel(new Model<String>("Add Agreement"));
			add(new AddAgreementIntroPanel("agreementIntroPanel", EditStateType.ADD, pageModel,
					partyPageModelNewParty));
		}

		@Override
		protected void onBeforeRender() {
			super.onBeforeRender();

			if (disableFirstStep) {
				AddAgreementIntroPanel p = (AddAgreementIntroPanel) get("agreementIntroPanel");
				p.setEnabled(false);
				p.info("No changes allowed to initial wizard step");
			}
		}

	}

	/**
	 * Step for the dynamic view on party details that allows viewing of existing
	 * party data and editing of new party data
	 * 
	 * @author kxd1203
	 *
	 */
	private final class PartyDetailsStep extends WizardStep {
		private static final long serialVersionUID = 1L;
		StatusReasonFraudFormValidator statusReasonFraudValidator;
		private StatusReasonDebarmentFormValidator statusReasonDebarmentFormValidator;

		public PartyDetailsStep() {
			setTitleModel(new Model<String>("Party Details"));
			add(new EmptyPanel("partyPanel"));
			add(getStatusReasonFraudFormValidator());
			add(getStatusReasonDebarmentFormValidator());
		}

		private StatusReasonFraudFormValidator getStatusReasonFraudFormValidator() {
			if (statusReasonFraudValidator == null) {
				statusReasonFraudValidator = new StatusReasonFraudFormValidator(EditStateType.ADD,
						getPartyPageModel().getPartyDTO());
			}
			return statusReasonFraudValidator;
		}

		private StatusReasonDebarmentFormValidator getStatusReasonDebarmentFormValidator() {
			if (statusReasonDebarmentFormValidator == null) {
				statusReasonDebarmentFormValidator = new StatusReasonDebarmentFormValidator(EditStateType.ADD,
						getPartyPageModel().getPartyDTO());
			}
			return statusReasonDebarmentFormValidator;
		}

		@Override
		protected void onBeforeRender() {
			if (getPartyPageModel() == null || getPartyPageModel().getPartyDTO() == null
					|| getPartyPageModel().getPartyDTO() instanceof PersonDTO) {
				if (!(this.get("partyPanel") instanceof PersonDetailsPanel)) {
					setTitleModel(new Model<String>("Person Details"));
					get("partyPanel")
							.replaceWith(new PersonDetailsPanel("partyPanel", getPartyPageModel(), getPartyEditState(),
									null, true, pageModel.getMaintainAgreementDTO().getAgreementKindType()));
				}

			} else {
				if (!(this.get("partyPanel") instanceof OrganisationDetailsPanel)) {
					setTitleModel(new Model<String>("Organisation Details"));
					get("partyPanel").replaceWith((new OrganisationDetailsPanel("partyPanel", getPartyPageModel(),
							getPartyEditState(), null)));
				}
			}
			getStatusReasonFraudFormValidator().setParty(getPartyPageModel().getPartyDTO());
			getStatusReasonDebarmentFormValidator().setParty(getPartyPageModel().getPartyDTO());
			super.onBeforeRender();
		}

	}

	private MaintainAgreementPageModel clonePageModel() {
		MaintainAgreementPageModel ret = new MaintainAgreementPageModel();
		ret.setMaintainAgreementDTO(
				(MaintainAgreementDTO) SerializationUtils.clone(pageModel.getMaintainAgreementDTO()));
		ret.setValidAgreementValues(
				(ValidAgreementValuesDTO) SerializationUtils.clone(pageModel.getValidAgreementValues()));
		ret.setViewTemplate(pageModel.getViewTemplate());
		return ret;
	}

	private MaintainPartyPageModel clonePartyPageModel() {
		MaintainPartyPageModel ret = new MaintainPartyPageModel();
		ret.setPartyDTO((PartyDTO) SerializationUtils.clone(getPartyPageModel().getPartyDTO()));
		ret.setPartyBeforeImage((PartyDTO) SerializationUtils.clone(getPartyPageModel().getPartyBeforeImage()));
		return ret;
	}

	private final class PaymentSchedulerStep extends WizardStep {
		private static final long serialVersionUID = 1L;
		private PaysToDTO lastPaysTo;
		private boolean init = false;
		private EditStateType editState;
		private final String panelId = "paymentSchedulerPanel";

		private MaintainAgreementPageModel clonedModel;

		public PaymentSchedulerStep() {
			PaysToDTO payTo = getPaysTo();
			setTitleModel(new Model<String>("Payment Scheduler"));
			add(new EmptyPanel(panelId));
//			if(payTo==null || payTo.getPayto()==null || payTo.getPayto().equals(PaysToDTO.PayToType.OWN_ACCOUNT)) {
//				setTitleModel(new Model<String>("Payment Scheduler"));
//				add(new PaymentSchedulerPanel(
//						"paymentSchedulerPanel",
//						(MaintainAgreementPageModel)pageModel, 
//						EditStateType.ADD,
//						getFeedback(),
//						null));		
//			}else{
//				setTitleModel(new Model<String>("Organisation Payment Scheduler"));
//				add(new PaymentSchedulerPanel(
//						"paymentSchedulerPanel",
//						getModelToUse(), 
//						EditStateType.VIEW,
//						getFeedback(),
//						null));	
//			}
		}

		@SuppressWarnings("unused")
		private AgreementDTO getAgreementDTO() {
			return pageModel != null && pageModel.getMaintainAgreementDTO() != null
					&& pageModel.getMaintainAgreementDTO().getAgreementDTO() != null
							? pageModel.getMaintainAgreementDTO().getAgreementDTO()
							: null;
		}

		private PaysToDTO getPaysTo() {
			return pageModel != null && pageModel.getMaintainAgreementDTO() != null
					&& pageModel.getMaintainAgreementDTO().getAgreementDTO() != null
							? pageModel.getMaintainAgreementDTO().getAgreementDTO().getPaymentDetails()
							: null;
		}

		private MaintainAgreementPageModel getModelToUse() {
			PaysToDTO paysTo = getPaysTo();
			try {
				if (paysTo.getPayto() != null && paysTo.getPayto().equals(PaysToDTO.PayToType.OWN_ACCOUNT)) {
					return pageModel;
				}
				if (lastPaysTo == null || !lastPaysTo.equals(getPaysTo())) {
					clonedModel = clonePageModel();
					try {
						PaymentSchedulerDTO paymentScheduler = getGuiController()
								.getPaymentSchedulerDTOForPaysTO(paysTo.getOrgAgreementNumber());
						AgreementDTO agreement = clonedModel != null && clonedModel.getMaintainAgreementDTO() != null
								? clonedModel.getMaintainAgreementDTO().getAgreementDTO()
								: null;
						if (agreement != null) {
							agreement.setPaymentSchedulerDTO(paymentScheduler);
						}
					} catch (DataNotFoundException e) {
						getLogger().fatal("DataNotFoundException trying to get org payment scheduler", e);
						getFeedback().error("Data not found when trying to find ORG payment scheduler");
					} catch (ConformanceTypeException e) {
						getLogger().fatal("ConformanceTypeException trying to get org payment scheduler", e);
						getFeedback().error("Payment Scheduler for ORG could not be set up");
					}
					return clonedModel;
				} else {
					return clonedModel;
				}
			} finally {
				lastPaysTo = paysTo;
			}
		}

		@Override
		protected void onBeforeRender() {

			PaysToDTO payTo = getPaysTo();

			// Determine if panel has changed
			// Uncomment for now as this panel may always have to regenerate
			Component comp = get(panelId);
//			EditStateType state = null;
//			if (comp instanceof PaymentSchedulerPanel) {
//				state = ((PaymentSchedulerPanel) comp).getEditState();
//			}

			// Own account is normal situation, add with edit state ADD
			if (payTo == null || payTo.getPayto() == null || payTo.getPayto().equals(PaysToDTO.PayToType.OWN_ACCOUNT)) {
				if (!init || (editState != EditStateType.ADD)) {
					setTitleModel(new Model<String>("Payment Scheduler"));
					comp.replaceWith(new PaymentSchedulerPanel("paymentSchedulerPanel",
							(MaintainAgreementPageModel) pageModel, EditStateType.ADD, getFeedback(), null));
					editState = EditStateType.ADD;
					init = true;
				}

			} else {
				// Else for Organisation only view as it may not be changed.
				if (!init || (editState != EditStateType.VIEW)) {
					setTitleModel(new Model<String>("Organisation Payment Scheduler"));
					comp.replaceWith(new PaymentSchedulerPanel("paymentSchedulerPanel", getModelToUse(),
							EditStateType.VIEW, getFeedback(), null));
					editState = EditStateType.VIEW;
					init = true;
				}
			}

			super.onBeforeRender();
		}

	}

	/**
	 * Banking details step
	 * 
	 * @author JZB0608
	 *
	 */
	private final class BankingDetailsStep extends WizardStep {
		private static final long serialVersionUID = 1L;
		private PaysToDTO lastPaysTo;

		private MaintainPartyPageModel clonedModel;
		public boolean initialized = false;

		public BankingDetailsStep() {
			PaysToDTO payTo = getPaysTo();
			if (payTo == null || payTo.getPayto() == null || payTo.getPayto().equals(PaysToDTO.PayToType.OWN_ACCOUNT)) {
				setTitleModel(new Model<String>("Banking Details"));
				add(new BankingDetailsPanel("bankingDetailsPanel", getPartyPageModel(), EditStateType.ADD,
						getFeedback(), getParentPage(payTo)));
			} else {
				setTitleModel(new Model<String>("Organisation Banking Details"));
				add(new BankingDetailsPanel("bankingDetailsPanel", getModelToUse(), EditStateType.VIEW, getFeedback(),
						getParentPage(payTo)));
			}
		}

		private PaysToDTO getPaysTo() {
			return pageModel != null && pageModel.getMaintainAgreementDTO() != null
					&& pageModel.getMaintainAgreementDTO().getAgreementDTO() != null
							? pageModel.getMaintainAgreementDTO().getAgreementDTO().getPaymentDetails()
							: null;
		}

		private Page getParentPage(PaysToDTO payTo) {
			if (parentPage == null)
				return null;
			if (!(parentPage instanceof BaseWindowPage))
				return null;

			if (payTo == null || payTo.getPayto() == null || payTo.getPayto().equals(PaysToDTO.PayToType.OWN_ACCOUNT)) {
				if (getPartyPageModel() != null && getPartyPageModel().getPartyDTO() != null) {
					((BaseWindowPage) parentPage).setPartyContextId(
							getPartyPageModel().getPartyDTO().getOid() > 0 ? getPartyPageModel().getPartyDTO().getOid()
									: null);
				}

			} else {
				if (getModelToUse() != null && getModelToUse().getPartyDTO() != null) {
					((BaseWindowPage) parentPage).setPartyContextId(
							getModelToUse().getPartyDTO().getOid() > 0 ? getModelToUse().getPartyDTO().getOid() : null);
				}
			}
			((BaseWindowPage) parentPage).setAgreementContextId(null);

			return parentPage;

		}

		private MaintainPartyPageModel getModelToUse() {
			PaysToDTO paysTo = getPaysTo();
			try {
				if (paysTo.getPayto() != null && paysTo.getPayto().equals(PaysToDTO.PayToType.OWN_ACCOUNT)) {
					return getPartyPageModel();
				}
				if (lastPaysTo == null || !lastPaysTo.equals(getPaysTo())) {
					clonedModel = clonePartyPageModel();
					try {

						IPartyNameAgreementFLO partyName = getGuiController()
								.findPartyNameForAgreement(paysTo.getOrgAgreementNumber());
						if (partyName == null)
							throw new DataNotFoundException();
						long partyOid = partyName.getPartyOid();

						if (clonedModel != null && clonedModel.getPartyDTO() != null) {
							clonedModel.getPartyDTO().setOid(partyOid);
							clonedModel.setSelectedAgreementNr(paysTo.getOrgAgreementNumber());
							clonedModel.getPartyDTO().setBankingDetailsDTO(null);
						}

					} catch (DataNotFoundException e) {
						getLogger().fatal("DataNotFoundException trying to get Org Banking Details", e);
						getFeedback().error("Data not found when trying to find ORG Banking Details");
					}
					return clonedModel;
				} else {
					return clonedModel;
				}
			} finally {
				lastPaysTo = paysTo;
			}
		}

		@Override
		protected void onBeforeRender() {
			if (!initialized) {
				PaysToDTO payTo = getPaysTo();
				if (payTo == null || payTo.getPayto() == null
						|| payTo.getPayto().equals(PaysToDTO.PayToType.OWN_ACCOUNT)) {
					remove("bankingDetailsPanel");
					setTitleModel(new Model<String>("Banking Details"));
					add(new BankingDetailsPanel("bankingDetailsPanel", getPartyPageModel(), EditStateType.ADD,
							getFeedback(), getParentPage(payTo)));
					if (pageModel.getPartyTypeSelection() != null
							&& pageModel.getPartyTypeSelection()
									.equals(PartyTypeSelection.CURRENT_PARTY)
							&& pageModel.isPartyHasBankingDetails()) {
						getLogger().info("Party already has banking details, any changes "
								+ "made to banking details will affect all linked agreements that are pay to own.  partyOid="
								+ pageModel.getExistingPartyDetails().getOid());
						warn("Party already has banking details, any changes made to banking details on this screen will affect all linked agreements that are pay to own.");
					}
				} else {
					remove("bankingDetailsPanel");
					setTitleModel(new Model<String>("Organisation Banking Details"));

					add(new BankingDetailsPanel("bankingDetailsPanel", getModelToUse(), EditStateType.VIEW,
							getFeedback(), getParentPage(payTo)));
				}
				initialized = true;
			}

			super.onBeforeRender();
		}

	}

	/**
	 * Step for the dynamic view on contact details that allows viewing of existing
	 * party details or addition of new contact details
	 * 
	 * @author kxd1203
	 *
	 */
	private final class PartyContactDetailsStep extends WizardStep {
		private static final long serialVersionUID = 1L;

		boolean init = false;

		public PartyContactDetailsStep() {
			setTitleModel(new Model<String>("Party Contact Details"));
			add(new EmptyPanel("partyContactDetailsPanel"));
		}

		@Override
		protected void onBeforeRender() {
			if (!init) {
				get("partyContactDetailsPanel").replaceWith(getPartyContactDetailsPanel());
			}
			super.onBeforeRender();
		}

		private Component getPartyContactDetailsPanel() {
			return new ContactDetailsPanel("partyContactDetailsPanel", getContactDetailsModel(),
					getCommunicationPreferences(), getPartyEditState(), AddAgreementWizard.this.getFeedback(), true,
					true, null, false);
		}

		private List<CommunicationPreferenceDTO> getCommunicationPreferences() {
			// TODO: UW Comms - Check Comm Pref list
			return new ArrayList<CommunicationPreferenceDTO>();
		}

		private List<ContactPreferenceDTO> getContactDetailsModel() {
			MaintainPartyPageModel partyModel = getPartyPageModel();
			return ((partyModel != null && partyModel.getPartyDTO() != null
					&& partyModel.getPartyDTO().getContactPreferences() != null)
							? partyModel.getPartyDTO().getContactPreferences().getContactPreferences()
							: null);
		}
	}

	/**
	 * Step for the Medical aid details
	 * 
	 * @author dzs2610
	 *
	 */
	private final class MedicalDetailsStep extends WizardStep {
		private static final long serialVersionUID = 1L;
		boolean init = false;

		public MedicalDetailsStep() {
			setTitleModel(new Model<String>("Medical Aid Details"));
			add(new EmptyPanel("medicalAidDetailsPanel"));
		}

		@Override
		protected void onBeforeRender() {
			if (!init) {
				// refresh for changes
				init = true;
				get("medicalAidDetailsPanel").replaceWith(getMedicalDetailsPanel());
			}
			super.onBeforeRender();
		}

		private Component getMedicalDetailsPanel() {
			return new MedicalAidDetailsPanel("medicalAidDetailsPanel", getPartyEditState(), getMedicalAidPanelModel(),
					null);
		}

		private MedicalAidDetailsPanelModel getMedicalAidPanelModel() {
			MaintainPartyPageModel partyModel = getPartyPageModel();
			MedicalAidDetailsPanelModel panelModel = partyModel.getMedicalAidDetailsPanelModel();
			if (panelModel == null) {
				panelModel = new MedicalAidDetailsPanelModel();
			}
			panelModel.setCurrentPartyID((partyModel.getPartyDTO() != null) ? partyModel.getPartyDTO().getOid() : 0);
			panelModel.setCurrentPartyType(
					(partyModel.getPartyDTO() != null) ? partyModel.getPartyDTO().getTypeOID() : 0);
			if (panelModel.getCurrentPartyType() == 0 && partyModel.getPartyDTO() != null
					&& partyModel.getPartyDTO() instanceof PartyDTO) {
				panelModel.setCurrentPartyType(PartyType.PERSON.getType());
			}
			partyModel.setMedicalAidDetailsPanelModel(panelModel);
			return panelModel;
		}
	}

	/**
	 * Step for the dynamic view on Advisor Quality Code Details for a party or
	 * addition of new contact details
	 * 
	 * @author pks2802
	 *
	 */
	private final class AdvisorQualityCodeStep extends WizardStep {
		private static final long serialVersionUID = 1L;

		boolean initialised = false;

		public AdvisorQualityCodeStep() {
			setTitleModel(new Model<String>("Advisor Quality Code"));
			add(new EmptyPanel("advisorQualityCodePanel"));
		}

		@Override
		protected void onBeforeRender() {
			if (!initialised) {
				initialised = true;
				get("advisorQualityCodePanel").replaceWith(getAdvisorQualityCodePanel());
			}
			super.onBeforeRender();
		}

		private Component getAdvisorQualityCodePanel() {
			return new AdvisorQualityCodePanel("advisorQualityCodePanel", EditStateType.ADD,
					getAdvisorQualityCodePanelModel(), parentPage, AddAgreementWizard.this.getFeedback());
		}

		private AdvisorQualityCodePanelModel getAdvisorQualityCodePanelModel() {

			AdvisorQualityCodePanelModel advisorQualityCodePanelModel = new AdvisorQualityCodePanelModel(
					AddAgreementWizard.this.pageModel);

			PartyDTO party = AddAgreementWizard.this.pageModel.getExistingPartyDetails();

			/* Reload the valid values */

			if (pageModel.getPartyTypeSelection() != null) {
				if (pageModel.getPartyTypeSelection() == PartyTypeSelection.CURRENT_PARTY) {
					if (pageModel.getExistingPartyDetails() != null
							&& pageModel.getMaintainAgreementDTO().getAgreementDTO().getPartyOid() == null) {
						pageModel.getMaintainAgreementDTO().getAgreementDTO()
								.setPartyOid(pageModel.getExistingPartyDetails().getOid());
					}
				} else {
					pageModel.getMaintainAgreementDTO().getAgreementDTO().setPartyOid(null);
				}
			}
			try {
				getGuiController().loadValidValuesForRequest(pageModel.getMaintainAgreementDTO().getAgreementDTO(),
						pageModel.getValidAgreementValues(),
						PanelToRequestMapping.getRequestKindsForPanel(AdvisorQualityCodePanel.class));
			} catch (DataNotFoundException e1) {
				// Ignore
			}

			List<AgreementHomeRoleDTO> agmtHomeRoles = AddAgreementWizard.this.pageModel.getMaintainAgreementDTO()
					.getAgreementDTO().getCurrentAndFutureHomeRoles();
			List<AgreementRoleDTO> agmtRoles = AddAgreementWizard.this.pageModel.getMaintainAgreementDTO()
					.getAgreementDTO().getCurrentAndFutureAgreementRoles();

			if (agmtHomeRoles == null)
				return advisorQualityCodePanelModel;

			int agmtKind = AddAgreementWizard.this.pageModel.getMaintainAgreementDTO().getAgreementDTO().getKind();

			SegmentDTO segment = getGuiController().getApplicableSegmentTypeForAddAgreement(agmtHomeRoles, agmtRoles,
					agmtKind);
			SegmentNameDTO segmentNameDTO = segment != null ? segment.getSegmentNameId() : null;
			AdvisorQualityCodeDTO advisorQualityCodeDTO = null;

			if (party != null) {

				try {
					advisorQualityCodeDTO = getGuiController().getAdvisorQualityCodeDetailsForParty(party);

					if (advisorQualityCodeDTO != null) {

						if (advisorQualityCodeDTO.getAqcDetailsWithTypeDTO() != null
								&& advisorQualityCodeDTO.getAqcDetailsWithTypeDTO().size() > 0)

							AddAgreementWizard.this.pageModel.getMaintainAgreementDTO().getAgreementDTO()
									.getAdvisorQualityCodeDTO()
									.setAqcDetailsWithTypeDTO(advisorQualityCodeDTO.getAqcDetailsWithTypeDTO());

						advisorQualityCodeDTO.setSegment(segmentNameDTO);
					}

				} catch (DataNotFoundException e) {
					getLogger().fatal("DataNotFoundException trying to get advisor Quality Code Details", e);
					getFeedback().error("Data not found when trying to find advisor Quality Code Details");
				} catch (ConformanceTypeException e) {
					getLogger().fatal("ConformanceTypeException trying to get advisor Quality Code Details", e);
					getFeedback().error("advisor Quality Code Details could not be set up");
				}

				advisorQualityCodePanelModel.setPartyOid(party.getOid());
			}

			AddAgreementWizard.this.pageModel.getMaintainAgreementDTO().getAgreementDTO().getAdvisorQualityCodeDTO()
					.setSegment(segmentNameDTO);

			List<EffectiveAQCDTO> effectiveAQCValues = getGuiController()
					.getEffectiveAQCValuesForAddAgreement(advisorQualityCodeDTO, segment);
			AddAgreementWizard.this.pageModel.getMaintainAgreementDTO().getAgreementDTO().getAdvisorQualityCodeDTO()
					.setEffectiveAQCValues(effectiveAQCValues);

			return advisorQualityCodePanelModel;

		}
	}

	/**
	 * Associated codes step
	 * 
	 * @author JZB0608
	 *
	 */
	private final class AgreementAssociatedCodesStep extends WizardStep {
		private static final long serialVersionUID = 1L;
		private static final String panelId = "agreementAssociatedCodesPanel";
		boolean init = false;

		public AgreementAssociatedCodesStep() {
			setTitleModel(new Model<String>("Associated Codes"));
			add(new EmptyPanel("agreementAssociatedCodesPanel"));
		}

		@Override
		protected void onBeforeRender() {
			if (!init) {
				init = true;
				get(panelId).replaceWith(getAgreementCodesPanel());
			}
			super.onBeforeRender();
		}

		public AgreementCodesPanel getAgreementCodesPanel() {
			associatedCodesPanel = new AgreementCodesPanel(panelId, EditStateType.ADD, getPageModel());
			return associatedCodesPanel;
		}
	}

	/**
	 * Distribution and pays to step
	 * 
	 * @author JZB0608
	 *
	 */
	private final class DistributionAndPaysToStep extends WizardStep {
		private static final long serialVersionUID = 1L;
		private static final String panelId = "distributionAndPaysToPanel";
		boolean init = false;

		public DistributionAndPaysToStep() {
			setTitleModel(new Model<String>("Distribution & Pays To"));
			add(new EmptyPanel(panelId));
		}

		@Override
		protected void onBeforeRender() {
			if (!init) {
				init = true;
				get(panelId).replaceWith(getDistributionPaysToPanel());
			}
			super.onBeforeRender();
		}

		public DistributionPaysToPanel getDistributionPaysToPanel() {
			distributionPaysToPanel = new DistributionPaysToPanel(panelId, getPageModel(), getFeedback(),
					EditStateType.ADD);
			return distributionPaysToPanel;
		}
	}

	private final class AssociatedAgreementsStep extends WizardStep {
		private static final long serialVersionUID = 1L;
		private static final String panelId = "associatedAgreementsPanel";
		boolean init = false;

		public AssociatedAgreementsStep() {
			setTitleModel(new Model<String>("Associated Agreements"));
			add(new EmptyPanel(panelId));
		}

		@Override
		protected void onBeforeRender() {
			if (!init) {
				init = true;
				get(panelId).replaceWith(getAssociatedAgreementsPanel());
			}
			super.onBeforeRender();
		}

		public AssociatedAgreementsPanel getAssociatedAgreementsPanel() {
			associatedAgreementsPanel = new AssociatedAgreementsPanel(panelId, getPageModel(), getFeedback(),
					EditStateType.ADD, parentPage);
			return associatedAgreementsPanel;
		}
	}

	private final class HierarchyDetailsStep extends WizardStep {
		private static final long serialVersionUID = 1L;
		private static final String panelId = "hierarchyDetailsPanel";
		private boolean init = false;

		public HierarchyDetailsStep() {
			setTitleModel(new Model<String>("Hierarchy Details New Screen"));
			add(new EmptyPanel(panelId));
		}

		@Override
		protected void onBeforeRender() {
			if (!init) {
				init = true;
				get(panelId).replaceWith(getAgreementHierarchyPanel());
			}
			super.onBeforeRender();
		}

		public AgreementHierarchyPanel getAgreementHierarchyPanel() {
			agreementHierarchyPanel = new AgreementHierarchyPanel(panelId, getPageModel(), EditStateType.ADD,
					getFeedback(), null);
			return agreementHierarchyPanel;
		}
	}

	/**
	 * Step for the dynamic view on Provident Fund Details
	 *
	 */
	private final class ProvidentFundDetailsStep extends WizardStep {
		private static final long serialVersionUID = 1L;
		private boolean init = false;

		public ProvidentFundDetailsStep() {
			setTitleModel(new Model<String>("Provident Fund Details"));
			add(new EmptyPanel("providentFundDetailsPanel"));
		}

		@Override
		protected void onBeforeRender() {
			if (!init) {
				init = true;
				get("providentFundDetailsPanel").replaceWith(getProvidentFundDetailsPanel());
			}

			super.onBeforeRender();
		}

		private Component getProvidentFundDetailsPanel() {
			return new ProvidentFundDetailsPanel("providentFundDetailsPanel", EditStateType.ADD, getPageModel());
		}
	}

	/**
	 * Step for the dynamic view on Banking Details choice Viewable only for New
	 * party.
	 * 
	 * @author pks2802
	 *
	 */
	private final class BankingDetailsChoiceStep extends WizardStep {
		private static final long serialVersionUID = 1L;
		boolean init = false;

		public BankingDetailsChoiceStep() {
			setTitleModel(new Model<String>("Add Banking Details ?"));

			add(new EmptyPanel("bankingDetailsChoicePanel"));
		}

		@Override
		protected void onBeforeRender() {
			if (!init) {
				init = true;
				get("bankingDetailsChoicePanel").replaceWith(getBankingDetailsChoicePanel());
			}
			super.onBeforeRender();
		}

		private Component getBankingDetailsChoicePanel() {
			return new BankingDetailsChoicePanel("bankingDetailsChoicePanel", getPageModel(),
					AddAgreementWizard.this.getFeedback(), EditStateType.ADD);
		}
	}

	/**
	 * Get the maintain party page model, based on the party type selection
	 */
	private MaintainPartyPageModel getPartyPageModel() {
		if (isPartyAddStateEnabled()) {
			return partyPageModelNewParty;
		} else {
			if (partyPageModelExistingParty == null || partyPageModelExistingParty.getPartyDTO() == null) {
				partyPageModelExistingParty = new MaintainPartyPageModel();
				partyPageModelExistingParty.setPartyDTO(pageModel.getExistingPartyDetails());
				PartyDTO party = (PartyDTO) SerializationUtils.clone(pageModel.getExistingPartyDetails());
				partyPageModelExistingParty.setPartyBeforeImage(party);

			}
			return partyPageModelExistingParty;
		}
	}

	/**
	 * Return add state for new party, and view state for existing party
	 * 
	 * @return
	 */
	private EditStateType getPartyEditState() {
		if (isPartyAddStateEnabled()) {
			return EditStateType.ADD;
		} else {
			return EditStateType.MODIFY;
		}
	}

	private boolean isPartyAddStateEnabled() {
		if (pageModel == null || pageModel.getPartyTypeSelection() == null) {
			return false;
		}
		switch (pageModel.getPartyTypeSelection()) {
		case CURRENT_PARTY:
			return false;
		case NEW_PARTY:
			return true;
		default:
			return false;
		}
	}

	private final class FAISDetailsStep extends WizardStep {
		private static final long serialVersionUID = 1L;
		private Panel faisLicensePanel;

		boolean init = false;
		MaintainAgreementPageModel thisPageModel = AddAgreementWizard.this.pageModel;

		public FAISDetailsStep() {
			setTitleModel(new Model<String>("FAIS License"));
			add(faisLicensePanel = new EmptyPanel("faisLicensePanel"));
		}

		@Override
		protected void onBeforeRender() {

			if (!init) {
				faisLicensePanel.replaceWith(faisLicensePanel = getFAISLicensePanel());
				// remove("faisLicensePanel");
				get("faisLicensePanel").replaceWith(faisLicensePanel = getFAISLicensePanel());
				init = true;
			}
			super.onBeforeRender();
		}

		private FAISLicensePanel getFAISLicensePanel() {
			FAISLicensePanel licensePanel = new FAISLicensePanel("faisLicensePanel", EditStateType.ADD,
					new FAISLicensePanelModel(thisPageModel));
			return licensePanel;
		}

	}

	/**
	 * Step for the first agreement details panel.
	 * 
	 * This step will regenerate models/recreate GUIs for agreement specific
	 * content, as the this is the first screen in the agreement process, and the
	 * dynamic nature of the agreement screens requires that the GUIs/models are
	 * created after the selection of the agreement kind.
	 * 
	 * The following panels are generated when getWizardSteps is called, and have to
	 * be regenerated once a selection is made on the agreement kind to be used for
	 * this wizard: 1> AgreementDetailsPanel
	 * 
	 * The following panels only generate their GUIS on first render, and thus do
	 * not have to be regenerated. Distribution details & Pays To Panel 1>
	 * DistributionPaysToPanel 2> AgreementCodesPanel
	 * 
	 * 
	 * @author kxd1203
	 *
	 */
	private final class AgreementDetailsStep extends WizardStep {
		private static final long serialVersionUID = 1L;
		private AgreementDetailsPanel agreementDetailsPanel;

		public AgreementDetailsStep() {
			setTitleModel(new Model<String>("Agreement Details"));
			agreementDetailsPanel = getAgreementDetailsPanel(pageModel);
			agreementDetailsPanel.setMaintainAgreementPageModel(pageModel);
			add(agreementDetailsPanel);
		}

		@Override
		protected void onBeforeRender() {

			String midSix = null;

			MaintainAgreementPageModel thisPageModel = AddAgreementWizard.this.pageModel;
			SalesCategoryDTO salesCategoryDTO = thisPageModel.getMaintainAgreementDTO().getSalesCategoryDTO();
			String salesCategory = salesCategoryDTO.getSalesCategory();

			// SBS0510
			// Call AgreementGui Controller to get Next Mid Six for New Organisation added(
			// Only for Those agreement Kinds for which Auto Gen is enabled)

			if (AgreementKindType
					.isAutoGenConsCodeEnabled(thisPageModel.getMaintainAgreementDTO().getAgreementKindType().getKind())
					&& !midSixGenerated && PartyType.ORGANISATION.getDesc().equalsIgnoreCase(partyType)
			// && thisPageModel.getPartyTypeSelection() == PartyTypeSelection.NEW_PARTY) {
			) {
				try {

					AgreementDTO agreementDTO = thisPageModel.getMaintainAgreementDTO().getAgreementDTO();
					// At this point Kind is not set on AgreementDTO so we set from
					// MaintainAgreementDTO
					agreementDTO.setKind(thisPageModel.getMaintainAgreementDTO().getAgreementKindType().getKind());

					ConsCodeGenerationDTO codeGenerationDTO = getMidSix(agreementDTO, PartyType.ORGANISATION);
					midSix = Objects.nonNull(codeGenerationDTO) ? codeGenerationDTO.getCode() : null;

					midSixGenerated = true;
					thisPageModel.getMaintainAgreementDTO().getAgreementDTO().getOrganisationMidsix().setValue(midSix);
					thisPageModel.getMaintainAgreementDTO().getAgreementDTO().setMiddleSix(midSix);
					thisPageModel.getMaintainAgreementDTO().getAgreementDTO().setLastFour("0000");
				} catch (DataNotFoundException e1) {
					getLogger().fatal("Next MidSix cannot be found");
				}
			}

			if (!agreementTypeSelected) {
				/**
				 * Delegate update of the model to the factory. Do not recreate the model, but
				 * just update minimal amount of information that is affected by the changing of
				 * agreement kind.
				 */
				MaintainAgreementPageModelFactory.updatePageModelWithNewAgreementKind(getGuiController(),
						thisPageModel.getMaintainAgreementDTO().getAgreementKindType(), pageModel);
				if (distributionPaysToPanel != null) {
					distributionPaysToPanel.setPageModel(thisPageModel);
				} else {
					getLogger().warn("Null distribution panel while adding agreement ");
				}
				if (associatedCodesPanel != null) {
					associatedCodesPanel.setPageModel(thisPageModel);
				}

				// Reset the Midsix since updatePageModelWithNewAgreementKind looses the value
				if (midSixGenerated) {
					thisPageModel.getMaintainAgreementDTO().getAgreementDTO().getOrganisationMidsix().setValue(midSix);
					thisPageModel.getMaintainAgreementDTO().getAgreementDTO().setMiddleSix(midSix);
					thisPageModel.getMaintainAgreementDTO().getAgreementDTO().setLastFour("0000");
				}

				get("agreementDetailsPanel")
						.replaceWith(agreementDetailsPanel = getAgreementDetailsPanel(thisPageModel));
				agreementTypeSelected = true;
				pageModel.setAgreementKindChangeEnabled(false);
				bankConsultantCodeFormValidator
						.setAgreementDTO(thisPageModel.getMaintainAgreementDTO().getAgreementDTO());

				/**
				 * Load all valid values for the add agreement process
				 */
				Set<RequestKindType> panelRequestValues = new HashSet<RequestKindType>();
				for (PanelToRequestMapping mapping : PanelToRequestMapping.values()) {
					if (mapping.getPage() != null && mapping.getPage().equals(AddAgreementWizard.class)) {
						panelRequestValues.addAll(Arrays.asList(mapping.getRequestKindTypes()));
					}
				}
				try {
					getGuiController().loadDeferredValidValuesForRequest(
							pageModel.getMaintainAgreementDTO().getAgreementDTO(), pageModel.getValidAgreementValues(),
							panelRequestValues.toArray(new RequestKindType[0]));
				} catch (DataNotFoundException e) {
					getFeedback().error("Could not create the agreement");
				}
				// pageModel.getMaintainAgreementDTO().getAgreementDTO().setCurrentAndFutureAgreementRoles(new
				// ArrayList<AgreementRoleDTO>());
				// pageModel.getMaintainAgreementDTO().getAgreementDTO().setCurrentAndFutureHomeRoles(new
				// ArrayList<AgreementRoleDTO>());
			}
			/**
			 * Update the party context which is used for validation components
			 */
			agreementDetailsPanel.setSalesCategory(salesCategory);
			agreementDetailsPanel.updatePartyContext(getPartyPageModel().getPartyDTO());
			super.onBeforeRender();
		}
	}

	/**
	 * Override the next button and implement some logic for the step that just
	 * completed.
	 */
	@Override
	public void onNextButtonClicked(AjaxRequestTarget target) {

////		if (getActiveStep() instanceof AgreementDetailsStep && !getFeedback().anyErrorMessage()) {
//		if (getActiveStep() instanceof PartyDetailsStep && !getFeedback().anyErrorMessage()) {
//		
//			// We only show the pop-up if it has medical
////			MedicalAidDetailsPanelModel medicalPanelModel = getPartyPageModel().getMedicalAidDetailsPanelModel();
////			if (medicalPanelModel != null && medicalPanelModel.getMedicalAidDetail() != null) {
////				
////				AgreementDTO agreementDTO = pageModel.getMaintainAgreementDTO().getAgreementDTO();
////				
////				if (getGuiController().isShowMedicalAidWarningOnAdd(agreementDTO, medicalPanelModel.getMedicalAidDetail())) {
//					setDialogMessage("Active medical aid details exist for this party, " +
//							"please ensure that at least one agreement is actively linked to medical aid.");
//					showDialog(target);
////				}
////			} 			
//		}
	}

	private AgreementDetailsPanel getAgreementDetailsPanel(MaintainAgreementPageModel pageModel) {
		AgreementDetailsPanel agmtDetailsPanel = new AgreementDetailsPanel("agreementDetailsPanel", EditStateType.ADD,
				new AgreementDetailsPanelModel(pageModel));

		if (pageModel != null && pageModel.getPartyTypeSelection() != null) {
			agmtDetailsPanel.setPartyChoice(pageModel.getPartyTypeSelection().getDescription());
		}

		return agmtDetailsPanel;
	}

	@Override
	protected MaintainAgreementPageModel initializePageModel(MaintainAgreementPageModel model) {
		/**
		 * Delegate model creation to the factory
		 */
		pageModel = MaintainAgreementPageModelFactory.createPageModelForCreate(getGuiController(),
				AgreementKindType.AGENT, partyContext);
		/**
		 * init party model
		 */
		if (partyPageModelNewParty == null) {
			partyPageModelNewParty = new MaintainPartyPageModel();
			EmployeeDTO emp = new EmployeeDTO();
			emp.setEffectiveFrom(new Date());
			ContactPreferenceWrapperDTO wrapper = new ContactPreferenceWrapperDTO();
			wrapper.setContactPreferences(new ArrayList<ContactPreferenceDTO>());
			emp.setContactPreferences(wrapper);
			partyPageModelNewParty.setPartyDTO(emp);
		}

		return pageModel;
	}

	@Override
	public boolean onCancel(AjaxRequestTarget target) {
		// Called on cancel of the
		AgreementDTO agreementDTO = pageModel.getMaintainAgreementDTO().getAgreementDTO();

		if (agreementDTO == null) {
			logger.warn("Null agreement DTO on cancel of wizard");
			return true;
		}

		if (agreementDTO.isLastFourReserved()) {
			try {
				getGuiController().releaseLastFour(agreementDTO);
			} catch (DataNotFoundException e) {
				logger.warn("Unable to release last four for " + agreementDTO.getConsultantCode(), e);
			}
		}

		if (agreementDTO.isMiddleSixReserved()) {
			try {
				getGuiController().releaseMiddleSix(agreementDTO);
			} catch (DataNotFoundException e) {
				logger.warn("Unable to release middle six for " + agreementDTO.getConsultantCode(), e);
			}
		}

		return true;
	}

	@Override
	public boolean onFinish(AjaxRequestTarget target) {
		getPartyPageModel().getPartyDTO()
				.setContactPreferences(new ContactPreferenceWrapperDTO(
						getStepPanelOfType(ContactDetailsPanel.class, "partyContactDetailsPanel")
								.getCurrentContactPreferenceDetails()));
		ISessionUserProfile userProfile = SRSAuthWebSession.get().getSessionUser();
		try {
			MedicalAidDetailsPanelModel medicalPanelModel = getPartyPageModel().getMedicalAidDetailsPanelModel();
			MaintainPartyDTO maintainPartyDTO = new MaintainPartyDTO();
			if (medicalPanelModel != null) {
				maintainPartyDTO.setMedicalAidDetailDTO(medicalPanelModel.getMedicalAidDetail());
				maintainPartyDTO.setMedicalAidDetailDTOBeforeImage(medicalPanelModel.getMedicalAidDetailBeforeImage());
			}
			maintainPartyDTO.setPartyDTO(getPartyPageModel().getPartyDTO());
			maintainPartyDTO.setPartyDTObeforeImage(getPartyPageModel().getPartyBeforeImage());

			RaiseGuiRequestResultDTO request = getGuiController().raiseCreateAgreementRequest(userProfile,
					pageModel.getMaintainAgreementDTO(), maintainPartyDTO);
			targetAgreementNumber = request.getTargetAgreementNr();

//			if (finishedSucessfully) {
//				if (targetAgreementNumber!=null) {
			try {
				ContextDTO context = getGuiController().getAgreementContext(targetAgreementNumber);
				SRSAuthWebSession.get().setContextDTO(context);
			} catch (CommunicationException e) {
				getSession().error("Critical Failure when trying to set agreement in context");
			} catch (DataNotFoundException e) {
				getSession().error("Could not find agreement to set in context");
			}
//				}
			String msg = "Create Agreement Requests have been raised. SRS Agreement ID: " + targetAgreementNumber;
			getSession().info(msg);
			pageModel.setModalWizardMessage(msg);
			parentWindow.setSessionModelForPage(pageModel);

		} catch (ValidationException e) {
			for (String error : e.getErrorMessages()) {
				getFeedback().error(error);
			}
			target.add(getFeedback());
			return false;
		} catch (RequestException e) {
			getFeedback().error("Could not raise the request: " + e.getMessage());
			target.add(getFeedback());
			return false;
		}
		return true;
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

	/**
	 * Call the controller to retrieve a new MID Six range for this organisation.
	 * 
	 * @param agreementDTO
	 * @param partyType
	 * @return
	 * @throws DataNotFoundException
	 */
	private ConsCodeGenerationDTO getMidSix(AgreementDTO agreementDTO, PartyType partyType)
			throws DataNotFoundException {
		return getGuiController().getMidSix(agreementDTO, partyType);
	}

	protected Form getCurrentStepForm() {
		IWizardStep s = super.getActiveStep();
		if (s instanceof PartyDetailsStep) {
			PartyDetailsStep ps = (PartyDetailsStep) s;
			Component c = ps.get("partyPanel");
			if (c instanceof OrganisationDetailsPanel) {
				OrganisationDetailsPanel p = (OrganisationDetailsPanel) c;
				Form frm = (Form) p.get("pageForm");
				return frm;
			}
		}
		return null;
	}

}
