package za.co.liberty.web.pages.party;

import java.util.List;

import org.apache.log4j.Logger;
import org.apache.wicket.Page;
import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.extensions.markup.html.tabs.TabbedPanel;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

import za.co.liberty.dto.party.PartyDTO;
import za.co.liberty.dto.party.PersonDTO;
import za.co.liberty.dto.party.contactdetail.CommunicationPreferenceDTO;
import za.co.liberty.dto.party.contactdetail.ContactPreferenceWrapperDTO;
import za.co.liberty.exceptions.security.TabAccessException;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.data.pages.ITabbedPageModel;
import za.co.liberty.web.pages.contactdetail.ContactDetailsPanel;
import za.co.liberty.web.pages.interfaces.IMaintenanceParent;
import za.co.liberty.web.pages.panels.MaintenanceTabbedPanel;
import za.co.liberty.web.pages.party.model.MaintainPartyPageModel;
import za.co.liberty.web.pages.taxdetails.TaxDetailsPanel;
import za.co.liberty.web.wicket.markup.html.tabs.StatefullCachingTab;

/**
 * This panel represents the Maintain Party panel that is contained in the Container 
 * Panel of the base page. This is a tabbed panel.
 * 
 * @author pks2802
 * 
 */
public class MaintainPartyPanel extends MaintenanceTabbedPanel {
	
	private static final long serialVersionUID = 1L;
	private FeedbackPanel feedBackPanel;
	
	protected BankingDetailsPanel bankingDetailsPanel;
	protected TaxDetailsPanel taxDetailsPanel;
	
	protected MedicalAidDetailsPanel medicalAidDetailsPanel;
	
	protected ProvidentFundBeneficiariesPanel providentFundPanel;
	
	protected static final Logger logger = Logger.getLogger(MaintainPartyPanel.class);
	
	/**
	 * @param id
	 * @param pageModel
	 * @param editState
	 * @throws TabAccessException 
	 */
	public MaintainPartyPanel(String id, ITabbedPageModel<PartyDTO> pageModel,
			EditStateType editState, int[] tabsToDisable,FeedbackPanel feedBackPanel, IMaintenanceParent parent) throws TabAccessException {
		super(id, pageModel, editState,tabsToDisable,parent);	
		System.out.println("MaintainPartyPanel:after super(id, pageModel, editState,tabsToDisable,parent)==================================="+id+" "+pageModel+" "+editState+" "+tabsToDisable+" "+parent);
		this.feedBackPanel = feedBackPanel;
	}

	/* (non-Javadoc)
	 * @see za.co.liberty.web.pages.panels.MaintenanceTabbedPanel#initialiseTabs(java.util.List)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void initialiseTabs(List<AbstractTab> tabList) {
		if (logger.isDebugEnabled())
			logger.debug("MaintainPartyPanel:initialiseTabs==================================="
					+(((MaintainPartyPageModel)pageModel).getPartyDTO() instanceof PersonDTO));
		//if organissation show org panel/ if person, show person details	
		if(((MaintainPartyPageModel)pageModel) == null || ((MaintainPartyPageModel)pageModel).getPartyDTO() == null || ((MaintainPartyPageModel)pageModel).getPartyDTO() instanceof PersonDTO){
			tabList.add(new StatefullCachingTab(new Model("Person Details"),MaintainPartyPanel.this) {
				private static final long serialVersionUID = 1L;
						
				@Override
				public Panel createPanel(String id) {	
					if (logger.isDebugEnabled())
						logger.debug("MaintainPartyPanel:initialiseTabs===================================");
					return new PersonDetailsPanel(TabbedPanel.TAB_PANEL_ID,
							(MaintainPartyPageModel)pageModel, 
							MaintainPartyPanel.this.getEditState(),(getIMaintenanceParent() instanceof Page) ? (Page)getIMaintenanceParent() : null);
				}		
			});
		} else{
			tabList.add(new StatefullCachingTab(new Model("Organisation Details"),MaintainPartyPanel.this) {
				private static final long serialVersionUID = 1L;
				
				@Override
				public Panel createPanel(String id) {				
					return new OrganisationDetailsPanel(TabbedPanel.TAB_PANEL_ID,
							(MaintainPartyPageModel)pageModel, 
							MaintainPartyPanel.this.getEditState(),(getIMaintenanceParent() instanceof Page) ? (Page)getIMaintenanceParent() : null);
				}		
			});
		}
		tabList.add(new StatefullCachingTab(new Model("Contact Details"),MaintainPartyPanel.this) {
			private static final long serialVersionUID = 1L;			

			@Override
			public Panel createPanel(String id) {
				MaintainPartyPageModel model = (MaintainPartyPageModel)pageModel;
				ContactDetailsPanel contactDetailsPanel = new ContactDetailsPanel(TabbedPanel.TAB_PANEL_ID,((model != null && model.getPartyDTO() != null && model.getPartyDTO().getContactPreferences() != null && model.getPartyDTO().getContactPreferences().getContactPreferences() != null) ? model.getPartyDTO().getContactPreferences().getContactPreferences() : null),
						getCommunicationPreferences(model),
						MaintainPartyPanel.this.getEditState(),feedBackPanel,true,false,(getIMaintenanceParent() instanceof Page) ? (Page)getIMaintenanceParent() : null, false);
				if(model != null && model.getPartyDTO() != null){
					model.getPartyDTO().setContactPreferences(new ContactPreferenceWrapperDTO(contactDetailsPanel.getCurrentContactPreferenceDetails()));
				}
				return contactDetailsPanel;
			}		
		});

		tabList.add(new StatefullCachingTab(new Model("Party Hierarchy"),MaintainPartyPanel.this) {
			private static final long serialVersionUID = 1L;			

			@Override
			public Panel createPanel(String id) {
				MaintainPartyPageModel model = (MaintainPartyPageModel)pageModel;
				PartyHierarchyPanel panel = new PartyHierarchyPanel(TabbedPanel.TAB_PANEL_ID,model,getEditState(),feedBackPanel,(getIMaintenanceParent() instanceof Page) ? (Page)getIMaintenanceParent() : null);
				panel.setOutputMarkupId(true);
				return panel;
			}		
		});

		tabList.add(new StatefullCachingTab(new Model("Banking Details"),MaintainPartyPanel.this) {
			private static final long serialVersionUID = 1L;
			
			@Override
			public Panel createPanel(String id) {
				
				if (bankingDetailsPanel==null) {
					FeedbackPanel feedpanel = (feedBackPanel != null) ? feedBackPanel : getFeedBackPanelFromParent();
					bankingDetailsPanel = new BankingDetailsPanel(TabbedPanel.TAB_PANEL_ID,
					(MaintainPartyPageModel)pageModel, 
					MaintainPartyPanel.this.getEditState(),feedpanel,
					(getIMaintenanceParent() instanceof Page) ? (Page)getIMaintenanceParent() : null);
			}
			return bankingDetailsPanel;
			
			}	
		});
		
		tabList.add(new StatefullCachingTab(new Model("Tax Details"),MaintainPartyPanel.this) {
			private static final long serialVersionUID = 1L;
			
			@Override
			public Panel createPanel(String id) {
				
				if (taxDetailsPanel==null) {
					FeedbackPanel feedpanel = (feedBackPanel != null) ? feedBackPanel : getFeedBackPanelFromParent();
					taxDetailsPanel = new TaxDetailsPanel(TabbedPanel.TAB_PANEL_ID,
					(MaintainPartyPageModel)pageModel, 
					MaintainPartyPanel.this.getEditState(),feedpanel,
					(getIMaintenanceParent() instanceof Page) ? (Page)getIMaintenanceParent() : null);
			}
			return taxDetailsPanel;
			
			}	
		});
		
		tabList.add(new StatefullCachingTab(new Model("Medical Aid Details"),MaintainPartyPanel.this) {
			private static final long serialVersionUID = 1L;
			
			@Override
			public Panel createPanel(String id) {
				
				if (medicalAidDetailsPanel==null) {
					//FeedbackPanel feedpanel = (feedBackPanel != null) ? feedBackPanel : getFeedBackPanelFromParent();
					medicalAidDetailsPanel = new MedicalAidDetailsPanel(TabbedPanel.TAB_PANEL_ID,
							MaintainPartyPanel.this.getEditState(),
					((MaintainPartyPageModel)pageModel).getMedicalAidDetailsPanelModel(),
					(getIMaintenanceParent() instanceof Page) ? (Page)getIMaintenanceParent() : null);
			}
			return medicalAidDetailsPanel;
			
			}	
		});
		
		

		//pzm2509 removing for beneficiary tab on party
	/*	tabList.add(new StatefullCachingTab(new Model("Provident Fund Beneficiaries"), MaintainPartyPanel.this) {
			private static final long serialVersionUID = 1L;

			@Override
			public Panel createPanel(String id) {
				if (providentFundPanel == null) {
					FeedbackPanel feedpanel = (feedBackPanel != null) ? feedBackPanel : getFeedBackPanelFromParent();
					providentFundPanel = new ProvidentFundBeneficiariesPanel(
							TabbedPanel.TAB_PANEL_ID,
							(MaintainPartyPageModel) pageModel,
							feedpanel,
							MaintainPartyPanel.this.getEditState(),
							(getIMaintenanceParent() instanceof Page) ? (Page) getIMaintenanceParent() : null);
				}
				return providentFundPanel;
			}
		});
		*/
		
	
		/*
		tabList.add(new CachingTab(new Model("FICA Details")) {
			private static final long serialVersionUID = 1L;
			
			@Override
			public Panel createPanel(String id) {
				
				return new EmptyPanel(TabbedPanel.TAB_PANEL_ID);
			}		
		});
		
		tabList.add(new CachingTab(new Model("FAIS Details")) {
			private static final long serialVersionUID = 1L;
			
			@Override
			public Panel createPanel(String id) {
				
				return new EmptyPanel(TabbedPanel.TAB_PANEL_ID);
			}		
		});
		*/
	}
		
	private List<CommunicationPreferenceDTO> getCommunicationPreferences(MaintainPartyPageModel model) {
		return (model != null && model.getPartyDTO() != null && model.getPartyDTO().getCommunicationPreferences() != null) ? model.getPartyDTO().getCommunicationPreferences() : null;
	}	
	
}