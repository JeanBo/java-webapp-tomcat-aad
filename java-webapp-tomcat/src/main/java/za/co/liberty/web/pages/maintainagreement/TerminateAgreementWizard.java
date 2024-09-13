package za.co.liberty.web.pages.maintainagreement;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.naming.NamingException;

import org.apache.commons.lang.SerializationUtils;
import org.apache.log4j.Category;
import org.apache.log4j.Logger;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.extensions.wizard.WizardStep;
import org.apache.wicket.model.Model;


import za.co.liberty.business.guicontrollers.agreement.IAgreementGUIController;
import za.co.liberty.common.domain.Percentage;
import za.co.liberty.dto.agreement.AgreementDTO;
import za.co.liberty.dto.agreement.PaymentSchedulerDTO;
import za.co.liberty.dto.agreement.maintainagreement.ValidAgreementValuesDTO;
import za.co.liberty.dto.agreement.properties.FranchisePoolAccountDTO;
import za.co.liberty.dto.userprofiles.ISessionUserProfile;
import za.co.liberty.exceptions.data.ConformanceTypeException;
import za.co.liberty.exceptions.data.DataNotFoundException;
import za.co.liberty.exceptions.error.request.RequestException;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.helpers.lookup.ServiceLocator;
import za.co.liberty.helpers.util.SRSUtility;
import za.co.liberty.interfaces.agreements.requests.RequestKindType;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.data.enums.PanelToRequestMapping;
import za.co.liberty.web.pages.maintainagreement.model.AgreementDetailsPanelModel;
import za.co.liberty.web.pages.maintainagreement.model.MaintainAgreementPageModel;
import za.co.liberty.web.pages.maintainagreement.model.MaintainAgreementPageModelFactory;
import za.co.liberty.web.pages.maintainagreement.payroll.FixedEarningsAndDeductionsPanel;
import za.co.liberty.web.pages.wizard.SRSPopupWizard;
import za.co.liberty.web.pages.wizard.object.SRSWizardPageDetail;
import za.co.liberty.web.system.SRSAuthWebSession;
import za.co.liberty.web.wicket.modal.SRSModalWindow;

public class TerminateAgreementWizard extends SRSPopupWizard<MaintainAgreementPageModel> {
	
	private MaintainAgreementPageModel pageModel;
	private transient IAgreementGUIController guiController;

	private SRSModalWindow parentWindow;
	private DistributionPaysToPanel distributionPaysToPanel;
	

	public TerminateAgreementWizard(String id, SRSModalWindow parentWindow,MaintainAgreementPageModel pageModel) { 
		super(id,parentWindow);
		this.parentWindow=parentWindow;
		
		// Retrieve the payment scheduler detail
		/*MBM0309
		 * Terminate agreement details
		 */
		try {
			getLogger().info("Initialise payment scheduler for agreement " + pageModel.getMaintainAgreementDTO().getAgreementDTO().getId());
			pageModel.getMaintainAgreementDTO().getAgreementDTO().setPaymentSchedulerDTO(
					getGuiController().getPaymentSchedulerDTOForPaysTO(pageModel.getMaintainAgreementDTO().getAgreementDTO().getId()));

			PaymentSchedulerDTO schedDTO = pageModel.getMaintainAgreementDTO().getAgreementDTO().getPaymentSchedulerDTO();
            getLogger().info("  -- payment scheduler for agreement " + pageModel.getMaintainAgreementDTO().getAgreementDTO().getId()
                        + "   isHasPaysto=" + schedDTO.isHasPaysTo()
                        + "   isDoNotPay=" +  schedDTO.isDoNotPay() );
		} catch (DataNotFoundException e1) {
			getLogger().warn("Unable to retrieve payment scheduler for " 
					+ pageModel.getMaintainAgreementDTO().getAgreementDTO().getId(), e1);
		} catch (ConformanceTypeException e1) {
			getLogger().warn("Unable to retrieve payment scheduler for " 
					+ pageModel.getMaintainAgreementDTO().getAgreementDTO().getId(), e1);
		}
		
		/**
		 * Create a cloned model to use for
		 * the terminate agreement wizard
		 * so that changes are not reflected
		 * in current view
		 */
		
		try {
			
			pageModel.getMaintainAgreementDTO().getAgreementDTO().setPaymentSchedulerDTO(getGuiController().getPaymentSchedulerDTOForPaysTO(pageModel.getMaintainAgreementDTO().getAgreementDTO().getId()));
		} catch (DataNotFoundException e1) {
			e1.printStackTrace();
		} catch (ConformanceTypeException e1) {
			e1.printStackTrace();
		}
		
		AgreementDTO clonedAgreement = 
			(AgreementDTO) SerializationUtils.clone(
				pageModel.getMaintainAgreementDTO().getAgreementDTO());
		ValidAgreementValuesDTO clonedValidValues = 
			(ValidAgreementValuesDTO) SerializationUtils.clone(
				pageModel.getValidAgreementValues());
		
		/**
		 * update the model with the cloned objects
		 * Set original to False. 
		 */
		//clonedAgreement.getPaymentSchedulerDTO().setDoNotPay(true);
		
		this.pageModel.updateModel(
				clonedAgreement, 
				clonedValidValues);
		
		// Update the model we are using at this point
			parentWindow.setSessionModelForPage(this.pageModel);
		
		 PaymentSchedulerDTO schedDTO = this.pageModel.getMaintainAgreementDTO().getAgreementDTO().getPaymentSchedulerDTO();
         getLogger().info("  -- payment scheduler for agreement (this. after clone) " + pageModel.getMaintainAgreementDTO().getAgreementDTO().getId()
                     + "   isHasPaysto=" + schedDTO.isHasPaysTo()
                     + "   isDoNotPay=" +  schedDTO.isDoNotPay() );
		/**
		 * This is to ensure that the original get's changed, as to when compared to previous, the change is detected. 
		 * note this for future change. And because the Do not pay is rule enlinked with comments, the two is together. 
		 * this is just a safety precaushing. When model updates, it will be updated with values. 
		 */
//		pageModel.getMaintainAgreementDTO().getAgreementDTO().getPaymentSchedulerDTO().setDoNotPay(true);
//		pageModel.getMaintainAgreementDTO().getAgreementDTO().getPaymentSchedulerDTO().setComments("Terminated");
//		pageModel.getMaintainAgreementDTO().getAgreementDTO().getPaymentSchedulerDTO().setEffDateSuspend( new java.sql.Timestamp(new java.util.Date().getTime()));
//		pageModel.getMaintainAgreementDTO().getAgreementDTO().getPaymentSchedulerDTO().setRequestedBy(SRSAuthWebSession.get().getSessionUser().getUacfId()+"");

		
		/**
		 * Load all valid values for the terminate agreement process
		 */
		Set<RequestKindType> panelRequestValues = new HashSet<RequestKindType>();
		for (PanelToRequestMapping mapping : PanelToRequestMapping.values()) {
			if (mapping.getPage() != null && mapping.getPage().equals(TerminateAgreementWizard.class)) {
				panelRequestValues.addAll(Arrays.asList(mapping.getRequestKindTypes()));
			}
		}
		try {
			getGuiController().loadDeferredDataForRequest(
					this.pageModel.getMaintainAgreementDTO().getAgreementDTO(), 
					this.pageModel.getPreviousMaintainAgreementDTO().getAgreementDTO(),
					this.pageModel.getValidAgreementValues(), 
					panelRequestValues.toArray(new RequestKindType[0]));
		} catch (DataNotFoundException e) {
			error("Could not create the agreement");
		}
		
		 schedDTO = pageModel.getMaintainAgreementDTO().getAgreementDTO().getPaymentSchedulerDTO();
         getLogger().info("  -- payment scheduler for agreement (after defer) " + pageModel.getMaintainAgreementDTO().getAgreementDTO().getId()
                     + "   isHasPaysto=" + schedDTO.isHasPaysTo()
                     + "   isDoNotPay=" +  schedDTO.isDoNotPay() );
         
		
		/**
		 * update model for termination process
		 */
		updateModelForTerminate();
		
		// Update the model we are using at this point
		parentWindow.setSessionModelForPage(this.pageModel);
	}
	
	private void updateModelForTerminate() {
		if (pageModel!=null && pageModel.getMaintainAgreementDTO()!=null &&
				pageModel.getMaintainAgreementDTO().getAgreementDTO()!=null &&
				pageModel.getValidAgreementValues()!=null &&
				pageModel.getValidAgreementValues().getOverriddenPreauthLimitCategory()!=null) {
			/**
			 * Set pre auth to 0.00 - overridden
			 */
			AgreementDTO agreementDTO = pageModel.getMaintainAgreementDTO().getAgreementDTO();
			agreementDTO.setPreauthLimitCategory(pageModel.getValidAgreementValues().getOverriddenPreauthLimitCategory());
			agreementDTO.getPreauthLimitCategory().setPreauthLimit(BigDecimal.ZERO);
			
			/** 
			 * populate the payment scheduler on maintain agreement dto to Do Not Pay
			 */		 
			PaymentSchedulerDTO schedDTO = pageModel.getMaintainAgreementDTO().getAgreementDTO().getPaymentSchedulerDTO();
			getLogger().info("  -- payment scheduler for agreement (Update Model) " + agreementDTO.getId()
                    + "   isHasPaysto=" + schedDTO.isHasPaysTo()
                    + "   isDoNotPay=" +  schedDTO.isDoNotPay() );
			
			if(schedDTO.isHasPaysTo() == false  // Can't terminate paysTo sched details
					&& schedDTO.isDoNotPay() == false) {
			 pageModel.getMaintainAgreementDTO().getAgreementDTO().getPaymentSchedulerDTO().setDoNotPay(true);
			 pageModel.getMaintainAgreementDTO().getAgreementDTO().getPaymentSchedulerDTO().setComments("Terminated Agreement");
			 pageModel.getMaintainAgreementDTO().getAgreementDTO().getPaymentSchedulerDTO().setEffDateSuspend( new java.sql.Timestamp(new java.util.Date().getTime()));
			 pageModel.getMaintainAgreementDTO().getAgreementDTO().getPaymentSchedulerDTO().setRequestedBy(SRSAuthWebSession.get().getSessionUser().getUacfId()+"");
			} 
		}
		if (pageModel!=null && pageModel.getMaintainAgreementDTO()!=null &&
				pageModel.getMaintainAgreementDTO().getAgreementDTO()!=null &&
				pageModel.getMaintainAgreementDTO().getAgreementDTO().getFranchisePoolAccount()!=null) {
			/**
			 * Change Pool draw to 100% and Interest rate to 0% 
			 */
			FranchisePoolAccountDTO poolAccount = 
				pageModel.getMaintainAgreementDTO().getAgreementDTO().getFranchisePoolAccount();
			if (poolAccount.getPoolAccountInterestRate()!=null) {
				poolAccount.setPoolAccountInterestRate(new Percentage(BigDecimal.ZERO));
			}
			if (poolAccount.getPoolTransferPercentage()!=null) {
				poolAccount.setPoolTransferPercentage(new Percentage(new BigDecimal("100.00")));
			}
		}
		
	}

	/**
	 * Instantiate the steps that will be utilized by the wizard
	 */
	@Override
	protected Collection<SRSWizardPageDetail> getWizardSteps(MaintainAgreementPageModel pageModel) {
		List<SRSWizardPageDetail> ret = new ArrayList<SRSWizardPageDetail>();
		ret.add(new SRSWizardPageDetail(new AgreementDetailsStep()));
		ret.add(new SRSWizardPageDetail("Distribution & Pays To",
				distributionPaysToPanel = new DistributionPaysToPanel(
						SRSPopupWizard.SRS_WIZARD_STEP_ID,
						this.pageModel,
						getFeedback(),
						EditStateType.TERMINATE)));
		ret.add(new SRSWizardPageDetail("Fixed Earnings & Deductions",
				new FixedEarningsAndDeductionsPanel(
						SRSPopupWizard.SRS_WIZARD_STEP_ID,
						EditStateType.TERMINATE,
						this.pageModel)));
		ret.add(new SRSWizardPageDetail("Workflow Details",
				new WorkflowPanel(SRSPopupWizard.SRS_WIZARD_STEP_ID,
						EditStateType.TERMINATE,
						this.pageModel.getMaintainAgreementDTO().getWorkflowDTO())));
		return ret;
	}

	
	
	private final class AgreementDetailsStep extends WizardStep {
		
		private AgreementDetailsPanel agreementDetailsPanel;
		
		public AgreementDetailsStep() {
			setTitleModel(new Model("Agreement Details"));
		}
		
		@Override
		protected void onBeforeRender() {
			if (agreementDetailsPanel==null) {
				agreementDetailsPanel = getAgreementDetailsPanel(pageModel);
				add(agreementDetailsPanel);
			}
			super.onBeforeRender();
		}
		
	}
	
	private AgreementDetailsPanel getAgreementDetailsPanel(MaintainAgreementPageModel pageModel) {
		return new AgreementDetailsPanel(
				"agreementDetailsPanel",
				EditStateType.TERMINATE, 
				new AgreementDetailsPanelModel(pageModel));
	}

	@Override
	protected MaintainAgreementPageModel initializePageModel(MaintainAgreementPageModel model) {
		pageModel = MaintainAgreementPageModelFactory
			.createPageModelForTerminate(getGuiController());
		return pageModel;
	}
	
	@Override
	public boolean onFinish(AjaxRequestTarget target) {
		ISessionUserProfile userProfile = SRSAuthWebSession.get().getSessionUser();
		try {
			getGuiController().raiseTerminateAgreementRequest(
					userProfile, 
					pageModel.getMaintainAgreementDTO().getAgreementDTO().getPartyOid(), 
					pageModel.getMaintainAgreementDTO(), 
					pageModel.getPreviousMaintainAgreementDTO());
		} catch (RequestException e) {
			error("Could not raise the request: "+e.getMessage());
			target.add (getFeedback());
			return false;
		}
		return true;
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
				getLogger().fatal("Could not lookup AgreementGUIController",e);
				throw new CommunicationException("Could not lookup AgreementGUIController",e);
				
			}
		}
		return guiController;
	}


	@Override
	public boolean onCancel(AjaxRequestTarget target) {
		return false;
	}

}
