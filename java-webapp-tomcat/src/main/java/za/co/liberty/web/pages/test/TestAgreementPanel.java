package za.co.liberty.web.pages.test;

import java.util.List;

import javax.naming.NamingException;

import org.apache.wicket.Page;
import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.extensions.markup.html.tabs.TabbedPanel;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

import za.co.liberty.business.guicontrollers.agreement.IAgreementGUIController;
import za.co.liberty.dto.agreement.maintainagreement.MaintainAgreementDTO;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.exceptions.security.TabAccessException;
import za.co.liberty.helpers.lookup.ServiceLocator;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.data.pages.ITabbedPageModel;
import za.co.liberty.web.pages.interfaces.IMaintenanceParent;
import za.co.liberty.web.pages.maintainagreement.AdvisorQualityCodePanel;
import za.co.liberty.web.pages.maintainagreement.AgencyPoolAccountDetailsPanel;
import za.co.liberty.web.pages.maintainagreement.AgreementCodesPanel;
import za.co.liberty.web.pages.maintainagreement.AgreementDetailsPanel;
import za.co.liberty.web.pages.maintainagreement.AgreementHierarchyPanel;
import za.co.liberty.web.pages.maintainagreement.AgreementIncentivesPanel;
import za.co.liberty.web.pages.maintainagreement.AssociatedAgreementsPanel;
import za.co.liberty.web.pages.maintainagreement.DistributionPaysToPanel;
import za.co.liberty.web.pages.maintainagreement.FAISLicensePanel;
import za.co.liberty.web.pages.maintainagreement.PaymentSchedulerPanel;
import za.co.liberty.web.pages.maintainagreement.ProvidentFundDetailsPanel;
import za.co.liberty.web.pages.maintainagreement.model.AdvisorQualityCodePanelModel;
import za.co.liberty.web.pages.maintainagreement.model.AgencyPoolAccountDetailsPanelModel;
import za.co.liberty.web.pages.maintainagreement.model.AgreementCodePanelModel;
import za.co.liberty.web.pages.maintainagreement.model.AgreementDetailsPanelModel;
import za.co.liberty.web.pages.maintainagreement.model.FAISLicensePanelModel;
import za.co.liberty.web.pages.maintainagreement.model.MaintainAgreementPageModel;
import za.co.liberty.web.pages.maintainagreement.model.ProvidentFundDetailsPanelModel;
import za.co.liberty.web.pages.maintainagreement.payroll.FixedEarningsAndDeductionsPanel;
import za.co.liberty.web.pages.panels.MaintenanceTabbedPanel;
import za.co.liberty.web.wicket.markup.html.tabs.CachingTab;
import za.co.liberty.web.wicket.markup.html.tabs.StatefullCachingTab;
import za.co.liberty.web.wicket.validation.maintainagreement.BankConsultantCodeFormValidator;

/**
 * This panel represents the Maintain Agreement panel that is contained in the Container 
 * Panel of the base page. This is a tabbed panel.
 * 
 * @author pks2802
 * 
 */
public class TestAgreementPanel extends MaintenanceTabbedPanel {
	
	private static final long serialVersionUID = 1L;
	
	private FeedbackPanel feedBackPanel;
	
	private AgreementDetailsPanel agreementPanel;

	protected DistributionPaysToPanel distributionPanel;

	protected AgreementCodesPanel agreementCodesPanel;

	protected PaymentSchedulerPanel paymentSchedulerPanel;

	protected AssociatedAgreementsPanel associatedAgreementsPanel;
	
	protected AgreementHierarchyPanel agreementhierarchyPanel;
	
	protected AgreementIncentivesPanel agreementIncentivesPanel;

	protected FAISLicensePanel faisLicensePanel;

	protected FixedEarningsAndDeductionsPanel payrollPanel;
	
	protected AdvisorQualityCodePanel advisorQualityCodePanel;
	
	protected ProvidentFundDetailsPanel providentFundDetailsPanel;
	
	protected AgencyPoolAccountDetailsPanel agencyPoolAccountDetailsPanel;
	
	
	
	private transient IAgreementGUIController guiController; // SSM2707 Hierarchy FR3.4 FAIS Details SWETA MENON

	/**
	 * @param id
	 * @param pageModel
	 * @param editState
	 * @throws TabAccessException 
	 */
	public TestAgreementPanel(String id, ITabbedPageModel<MaintainAgreementDTO> pageModel,
			EditStateType editState, Class[] tabsToDisable,FeedbackPanel feedbackPanel, 
			IMaintenanceParent parent,
			IAgreementGUIController guiController) throws TabAccessException {
		//TODO fill in panels that must be disabled 
		super(id, pageModel, editState, tabsToDisable,parent);
		this.feedBackPanel=feedbackPanel;
		List<Long> list = guiController.getBankAgreementNumbers();
		if (editState.equals(EditStateType.MODIFY) || 
				editState.equals(EditStateType.ADD)) {
			/**
			 * Add bank consultant code validation in add or maintain
			 */
			this.form.add(new BankConsultantCodeFormValidator(
				((MaintainAgreementPageModel)pageModel).getMaintainAgreementDTO().getAgreementDTO(),
				list));
		}
		
	}

	/* (non-Javadoc)
	 * @see za.co.liberty.web.pages.panels.MaintenanceTabbedPanel#initialiseTabs(java.util.List)
	 */
	@Override
	public void initialiseTabs(List<AbstractTab> tabList) {
		// /* Add role Panel */
//		tabList.add(new StatefullCachingTab(new Model("Agreement"),
//				TestAgreementPanel.this) {
//			private static final long serialVersionUID = 1L;
//
//			@Override
//			public Panel createPanel(String id) {
//				if (agreementPanel == null) {
//					agreementPanel = new AgreementDetailsPanel(
//							TabbedPanel.TAB_PANEL_ID,
//							TestAgreementPanel.this.getEditState(),
//							new AgreementDetailsPanelModel(
//									(MaintainAgreementPageModel) pageModel),
//							(getIMaintenanceParent() instanceof Page) ? (Page) getIMaintenanceParent()
//									: null);
//				}
//				agreementPanel
//						.setMaintainAgreementPageModel((MaintainAgreementPageModel) pageModel);
//				return (Panel)agreementPanel;
//			}
//		});
		tabList.add(new CachingTab(new Model("Distribution & PaysTo")) {
			private static final long serialVersionUID = 1L;

			@Override
			public Panel createPanel(String id) {
				if (distributionPanel == null) {
					distributionPanel = new DistributionPaysToPanel(
							TabbedPanel.TAB_PANEL_ID,
							(MaintainAgreementPageModel) pageModel,
							feedBackPanel,
							TestAgreementPanel.this.getEditState(),
							(getIMaintenanceParent() instanceof Page) ? (Page) getIMaintenanceParent()
									: null);
				}
				return distributionPanel;
			}
		});
		tabList.add(new CachingTab(new Model("Associated Codes")) {
			private static final long serialVersionUID = 1L;

			@Override
			public Panel createPanel(String id) {
				if (agreementCodesPanel == null) {
					agreementCodesPanel = new AgreementCodesPanel(
							TabbedPanel.TAB_PANEL_ID,
							TestAgreementPanel.this.getEditState(),
							new AgreementCodePanelModel(
									(MaintainAgreementPageModel) pageModel),
							(getIMaintenanceParent() instanceof Page) ? (Page) getIMaintenanceParent()
									: null);
				}
				return agreementCodesPanel;
			}
		});
//		tabList.add(new CachingTab(new Model("FAIS")) {
//			private static final long serialVersionUID = 1L;
//
//			@Override
//			public Panel createPanel(String id) {
//
//				if (faisLicensePanel == null) {
//
//					MaintainAgreementPageModel thisPageModel = ((MaintainAgreementPageModel) pageModel);
//					// SSM2707 Hierarchy FR3.4 FAIS Details Begin
//					/*
//					 * Check if the sales category of the context agreement
//					 * requires FAIS details
//					 */
//					String salesCat = null;
//					if (thisPageModel.getMaintainAgreementDTO() != null
//							&& thisPageModel.getMaintainAgreementDTO()
//									.getAgreementDTO() != null
//							&& thisPageModel.getMaintainAgreementDTO()
//									.getAgreementDTO().getSalesCategory() != null) {
//						salesCat = thisPageModel.getMaintainAgreementDTO()
//								.getAgreementDTO().getSalesCategory();
//					}
//
//					if (salesCat != null && !isSalesCategoryValid(salesCat)) {
//						thisPageModel.setAllowFAIS(false);
//					} else {
//						thisPageModel.setAllowFAIS(true);
//					}
//
//					faisLicensePanel = new FAISLicensePanel(
//							TabbedPanel.TAB_PANEL_ID,
//							TestAgreementPanel.this.getEditState(),
//							thisPageModel,
//							new FAISLicensePanelModel(thisPageModel),
//							(getIMaintenanceParent() instanceof Page) ? (Page) getIMaintenanceParent()
//									: null, salesCat);
//
//				}
//				// SSM2707 Hierarchy FR3.4 FAIS Details End
//				return faisLicensePanel;
//			}
//		});
//		tabList.add(new CachingTab(new Model("Payroll Earnings & Deductions")) {
//			private static final long serialVersionUID = 1L;
//
//			@Override
//			public Panel createPanel(String id) {
//				if (payrollPanel == null) {
//					payrollPanel = new FixedEarningsAndDeductionsPanel(
//							TabbedPanel.TAB_PANEL_ID,
//							TestAgreementPanel.this.getEditState(),
//							(MaintainAgreementPageModel) pageModel,
//							(getIMaintenanceParent() instanceof Page) ? (Page) getIMaintenanceParent()
//									: null);
//				}
//				return payrollPanel;
//			}
//		});
//		tabList.add(new CachingTab(new Model("Payment Scheduler")) {
//			private static final long serialVersionUID = 1L;
//
//			@Override
//			public Panel createPanel(String id) {
//				if (paymentSchedulerPanel == null) {
//					FeedbackPanel feedpanel = (feedBackPanel != null) ? feedBackPanel
//							: getFeedBackPanelFromParent();
//					paymentSchedulerPanel = new PaymentSchedulerPanel(
//							TabbedPanel.TAB_PANEL_ID,
//							(MaintainAgreementPageModel) pageModel,
//							TestAgreementPanel.this.getEditState(),
//							feedpanel,
//							(getIMaintenanceParent() instanceof Page) ? (Page) getIMaintenanceParent()
//									: null);
//				}
//				return paymentSchedulerPanel;
//			}
//		});
//
//		tabList.add(new CachingTab(new Model("Hierarchy")) {
//			private static final long serialVersionUID = 1L;
//
//			@Override
//			public Panel createPanel(String id) {
//				if (agreementhierarchyPanel == null) {
//					FeedbackPanel panel = (feedBackPanel != null) ? feedBackPanel
//							: getFeedBackPanelFromParent();
//					agreementhierarchyPanel = new AgreementHierarchyPanel(
//							TabbedPanel.TAB_PANEL_ID,
//							(MaintainAgreementPageModel) pageModel,
//							TestAgreementPanel.this.getEditState(),
//							panel,
//							(getIMaintenanceParent() instanceof Page) ? (Page) getIMaintenanceParent()
//									: null);
//
//				}
//				return agreementhierarchyPanel;
//			}
//		});
//
//		tabList.add(new CachingTab(new Model("Associated Agreements")) {
//			private static final long serialVersionUID = 1L;
//
//			@Override
//			public Panel createPanel(String id) {
//				if (associatedAgreementsPanel == null) {
//					FeedbackPanel feedpanel = (feedBackPanel != null) ? feedBackPanel
//							: getFeedBackPanelFromParent();
//					associatedAgreementsPanel = new AssociatedAgreementsPanel(
//							TabbedPanel.TAB_PANEL_ID,
//							(MaintainAgreementPageModel) pageModel,
//							feedpanel,
//							TestAgreementPanel.this.getEditState(),
//							(getIMaintenanceParent() instanceof Page) ? (Page) getIMaintenanceParent()
//									: null);
//				}
//				return associatedAgreementsPanel;
//			}
//		});
//
//		tabList.add(new CachingTab(new Model("Advisor Quality Code")) {
//			private static final long serialVersionUID = 1L;
//
//			@Override
//			public Panel createPanel(String id) {
//				if (advisorQualityCodePanel == null) {
//					FeedbackPanel feedpanel = (feedBackPanel != null) ? feedBackPanel
//							: getFeedBackPanelFromParent();
//					MaintainAgreementPageModel thisPageModel = ((MaintainAgreementPageModel) pageModel);
//					advisorQualityCodePanel = new AdvisorQualityCodePanel(
//							TabbedPanel.TAB_PANEL_ID,
//							TestAgreementPanel.this.getEditState(),
//							new AdvisorQualityCodePanelModel(thisPageModel),
//							(getIMaintenanceParent() instanceof Page) ? (Page) getIMaintenanceParent()
//									: null, feedpanel);
//				}
//				return advisorQualityCodePanel;
//			}
//		});
//
//		tabList.add(new CachingTab(new Model("Incentives")) {
//			private static final long serialVersionUID = 1L;
//
//			@Override
//			public Panel createPanel(String id) {
//				if (agreementIncentivesPanel == null) {
//					FeedbackPanel panel = (feedBackPanel != null) ? feedBackPanel
//							: getFeedBackPanelFromParent();
//					agreementIncentivesPanel = new AgreementIncentivesPanel(
//							TabbedPanel.TAB_PANEL_ID,
//							(MaintainAgreementPageModel) pageModel,
//							TestAgreementPanel.this.getEditState(),
//							panel,
//							(getIMaintenanceParent() instanceof Page) ? (Page) getIMaintenanceParent()
//									: null);
//				}
//				return agreementIncentivesPanel;
//			}
//		});
//
//		tabList.add(new CachingTab(new Model("Provident Fund")) {
//			private static final long serialVersionUID = 1L;
//
//			@Override
//			public Panel createPanel(String id) {
//				if (providentFundDetailsPanel == null) {
//					FeedbackPanel panel = (feedBackPanel != null) ? feedBackPanel
//							: getFeedBackPanelFromParent();
//					providentFundDetailsPanel = new ProvidentFundDetailsPanel(
//							TabbedPanel.TAB_PANEL_ID,
//							TestAgreementPanel.this.getEditState(),
//							new ProvidentFundDetailsPanelModel(
//									(MaintainAgreementPageModel) pageModel),
//							(getIMaintenanceParent() instanceof Page) ? (Page) getIMaintenanceParent()
//									: null);
//				}
//				return providentFundDetailsPanel;
//			}
//		});
//		// SSM2707 Sweta Menon Agency Pool 12/10/2017
//		MaintainAgreementPageModel pgModel = (MaintainAgreementPageModel) pageModel;
//		if (isAgencyPoolPanel()) {
//
//			tabList.add(new CachingTab(new Model("RDR Bridge Account")) {
//				private static final long serialVersionUID = 1L;
//
//				@Override
//				public Panel createPanel(String id) {
//					if (agencyPoolAccountDetailsPanel == null) {
//						FeedbackPanel feedpanel = (feedBackPanel != null) ? feedBackPanel
//								: getFeedBackPanelFromParent();
//						agencyPoolAccountDetailsPanel = new AgencyPoolAccountDetailsPanel(
//								TabbedPanel.TAB_PANEL_ID,
//								new AgencyPoolAccountDetailsPanelModel(
//										(MaintainAgreementPageModel) pageModel),
//								feedpanel,
//								TestAgreementPanel.this.getEditState(),
//								(getIMaintenanceParent() instanceof Page) ? (Page) getIMaintenanceParent()
//										: null);
//					}
//
//					return agencyPoolAccountDetailsPanel;
//				}
//			});
//		}

	}
	// SSM2707 Hierarchy FR3.4 FAIS Details SWETA MENON Begin
	/**
	 * Load the AgreementGUIController dynamically if it is null as this is a transient variable.
	 * @return {@link IAgreementGUIController}
	 */
	private IAgreementGUIController getGuiController() {
		if (guiController==null) {
			try {
				guiController = ServiceLocator.lookupService(IAgreementGUIController.class);
			} catch (NamingException e) {
				throw new CommunicationException("Could not lookup AgreementGUIController",e);
				
			}
		}
		return guiController;
	}

	private boolean isSalesCategoryValid(String salesCat) {
		List<String> exclSalesCatList = getGuiController()
				.getSalesCategoryWithoutFAIS();
		if (exclSalesCatList != null && exclSalesCatList.contains(salesCat)) {
			return false;
		} else
			return true;
	}
	// SSM2707 Hierarchy FR3.4 FAIS Details SWETA MENON End
	
	private boolean isAgencyPoolPanel() {
		MaintainAgreementPageModel thisPageModel = ((MaintainAgreementPageModel) pageModel);

		if (thisPageModel.getMaintainAgreementDTO() != null
				&& thisPageModel.getMaintainAgreementDTO().getAgreementDTO() != null) {

			return getGuiController().hasAgencyPoolAccount(
					thisPageModel.getMaintainAgreementDTO().getAgreementDTO()
							.getId());
		} else {
			return false;
		}
	}
}