package za.co.liberty.web.pages.salesBCLinking;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import javax.naming.NamingException;

import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.panel.Panel;

import za.co.liberty.business.guicontrollers.salesBCLinking.ISalesBCLinkingGuiController;
import za.co.liberty.dto.agreement.SalesBCLinking.BranchDetailsDTO;
import za.co.liberty.dto.agreement.SalesBCLinking.PanelAdviserSearchDTO;
import za.co.liberty.dto.agreement.SalesBCLinking.ServicingPanelDTO;
import za.co.liberty.dto.agreement.SalesBCLinking.ServicingTypeDTO;
import za.co.liberty.dto.agreement.SalesBCLinking.ServicingTypeEnum;
import za.co.liberty.dto.userprofiles.ISessionUserProfile;
import za.co.liberty.dto.userprofiles.SessionUserHierarchyNodeDTO;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.helpers.lookup.ServiceLocator;
import za.co.liberty.interfaces.party.IPartyNameAndIdFLO;
import za.co.liberty.interfaces.party.OrganisationType;
import za.co.liberty.persistence.party.IPartyEntityManager;
import za.co.liberty.web.pages.MaintenanceBasePage;
import za.co.liberty.web.pages.panels.SalesBCLinkingSelectionPanel;
import za.co.liberty.web.pages.salesBCLinking.model.SalesBCLinkingPageModel;
import za.co.liberty.web.pages.salesBCLinking.model.SalesBCLinkingPanelModel;
import za.co.liberty.web.system.SRSAuthWebSession;

/**
 * This class represents the Maintain Agreement PAGE.
 * 
 * @author ssm2707
 * 
 */
public class SalesBCLinkingPage extends MaintenanceBasePage<Object> {

	private SalesBCLinkingPageModel pageModel;
	
	private transient ISalesBCLinkingGuiController guiController;
	private transient IPartyEntityManager partyEntityManager;
	
	
	/**
	 * @param obj
	 */
	public SalesBCLinkingPage(Object obj) {
		super(obj);
		getFeedbackPanel().setVisible(false);
	}
	
	/**
	 * 
	 */
	public SalesBCLinkingPage() {
		this(null);
		getFeedbackPanel().setVisible(false);
	}
	
	@Override
	@SuppressWarnings(value = { "unchecked", "rawtypes" })
	public Panel createSelectionPanel() {
		if (pageModel == null) {
			pageModel = new SalesBCLinkingPageModel();
		}
		 Panel panel = new SalesBCLinkingSelectionPanel(SELECTION_PANEL_NAME,
		 pageModel, this, selectionForm, ServicingPanelDTO.class);
		 panel.setOutputMarkupId(true);
		
		 return panel;
	}

	@Override
	public Panel createContainerPanel() {
		if (pageModel == null) {
			pageModel = new SalesBCLinkingPageModel();
		}

		//FeedbackPanel feedbackPanel = getFeedbackPanel();
		
		Panel panel = null;
		if (pageModel.getSelectedItem() == null) {
			panel = new SalesBCLinkingPanel(CONTAINER_PANEL_NAME,
					getEditState(), pageModel, this, containerForm);
			// }
			panel.setOutputMarkupId(true);
			return panel;
		}
		if (pageModel.getSalesBCLinkingPanelModel() == null) {
			SalesBCLinkingPanelModel panelModel = new SalesBCLinkingPanelModel();
			panelModel.setPanelAdviserSearchDTO(new PanelAdviserSearchDTO());
			pageModel.setSalesBCLinkingPanelModel(panelModel);
		}
		
		panel = new SalesBCLinkingPanel(CONTAINER_PANEL_NAME, getEditState(),
				pageModel, this, containerForm);
		
		panel.setOutputMarkupId(true);
		return panel;
	}

	@Override
	public Button[] createNavigationalButtons() {
		return new Button[0];
	}

	@Override
	public Object initialisePageModel(Object obj,
			Object pageModelExtraValueObject) {

		if (obj != null && obj instanceof SalesBCLinkingPageModel) {
			pageModel = (SalesBCLinkingPageModel) obj;
			
		} else {
			pageModel = new SalesBCLinkingPageModel();

			/* Set the Branch Names in the page model */
			pageModel.setAccessibleBranchDetails(getAccessibleBranchNames());
			if (pageModel.getAccessibleBranchDetails() != null
					&& pageModel.getAccessibleBranchDetails().size() > 0
					&& pageModel.getSelectedBranch() == null
					&& pageModel.getAccessibleBranchDetails().get(0) != null) {
				pageModel.setSelectedBranch(pageModel
						.getAccessibleBranchDetails().get(0));
			}
			
			/* Call the guiController to obtain the servicing panels. */
			List<ServicingPanelDTO> panels = null;
			if (pageModel.getSelectedBranch()!=null) {
				panels = getGUIController()
						.findAllServicingPanels(
								pageModel.getSelectedBranch().getOid());
				Collections.sort(panels,
						new Comparator<ServicingPanelDTO>() {
							public int compare(ServicingPanelDTO s1,
									ServicingPanelDTO s2) {
								if (s1.getPanelName() == null && s2.getPanelName() == null) {
									return -1;
								} else if (s1.getPanelName() != null && s2.getPanelName() == null) {
									return -1;
								} else if (s1.getPanelName() == null && s2.getPanelName() != null) {
									return 1;
								} else {
								return s1.getPanelName().compareToIgnoreCase(
										s2.getPanelName());
								}
							}
						});
			}
			
			if (panels == null) {
				panels = new ArrayList<ServicingPanelDTO>();	
			}
			
			if (panels!= null && panels.size()>0) {				
				pageModel.setSelectedServicingPanel(panels.get(0));
			} else {
				pageModel.setSelectedServicingPanel(new ServicingPanelDTO());
			}
			
			pageModel.setServicingPanels(panels);
			pageModel.setDisplayServicingPanels(panels);
			
			/* Set the Servicing Types in the page model */
			pageModel.setServicingTypes(getServicingTypeList());

			/* Set the Servicing Types in the page model */
			/*SET Default load value for branch and panels End*/
			PanelAdviserSearchDTO panelAdvDTO = new PanelAdviserSearchDTO();
			panelAdvDTO.setSearchPanelList(new ArrayList<ServicingPanelDTO>());
			panelAdvDTO.setSearchPanelDetails(new ServicingPanelDTO());
			pageModel.setSalesBCLinkingPanelModel(new SalesBCLinkingPanelModel());
		}
		
//		if (pageModel.isRequestRaised()) {
//			getSession().info("Record was saved successfully");
//			pageModel.setRequestRaised(false);
//		} else {
//			SRSAuthWebSession.get().getFeedbackMessages().clear();
//		}
		
		return pageModel;
	}

	@Override
	public String getPageName() {
		return "Servicing Consultant Management";
	}

	private List<BranchDetailsDTO> getAccessibleBranchNames() {

		ISessionUserProfile userProfile = SRSAuthWebSession.get()
				.getSessionUser();
		
		List<Long> partyIds = new ArrayList<Long>();
		List<BranchDetailsDTO> branchNameList = new ArrayList<BranchDetailsDTO>();

		Collection<SessionUserHierarchyNodeDTO> nodesManaged = SRSAuthWebSession
				.get().getSessionUser().getHierarchicalNodeAccessList();
		if (nodesManaged == null || nodesManaged.size() <= 0) {
			nodesManaged = SRSAuthWebSession
					.get().getSessionUser().getAllHierarchicalNodeAccessList();
		}
			
		if (nodesManaged != null && nodesManaged.size() > 0) {
			Iterator<SessionUserHierarchyNodeDTO> iter = nodesManaged.iterator();
			while (iter.hasNext()) {
				SessionUserHierarchyNodeDTO val = iter.next();
				if (val.getOrganisationType()!= null && val.getOrganisationType().equals(OrganisationType.BRANCH)) {
					partyIds.add(val.getPartyOid());
				}
				
			}
		}
				
		if (partyIds != null && partyIds.size() > 0) {
			List<IPartyNameAndIdFLO> partyNameAndIdFlos = getPartyEntityManager()
					.findPartyNamesForIds(partyIds);

			if (partyNameAndIdFlos == null || partyNameAndIdFlos.size() == 0) {
				logger.error(this.getPageName()
						+ "Error in retriving branch information for the logged in user(UACFID):"
						+ userProfile.getUacfId());
				CommunicationException comm = new CommunicationException(
						"Error in retriving branch information for the logged in user(UACFID):"
								+ userProfile.getUacfId()
								+ " Party Names can not be looked up for the Branch Party OIDs");
				throw new CommunicationException(comm);
			}
			for (IPartyNameAndIdFLO flo : partyNameAndIdFlos) {
				BranchDetailsDTO dto = new BranchDetailsDTO();
				dto.setName(flo.getName());
				dto.setOid(flo.getOid());
				dto.setType(flo.getType());
				dto.setExternalReference(flo.getExternalReference());
				branchNameList.add(dto);
			}
		}
		

		Collections.sort(branchNameList, new Comparator<BranchDetailsDTO>() {
			public int compare(BranchDetailsDTO s1, BranchDetailsDTO s2) {
				if (s1.getName() == null && s2.getName() == null) {
					return -1;
				} else if (s1.getName() != null && s2.getName() == null) {
					return -1;
				} else if (s1.getName() == null && s2.getName() != null) {
					return 1;
				} else {
					return s1.getName().compareToIgnoreCase(s2.getName());
				}
			}
		});

		return branchNameList;

	}

	private List<ServicingTypeDTO> getServicingTypeList() {

		List<ServicingTypeDTO> typeList = new ArrayList<ServicingTypeDTO>();
		ServicingTypeEnum[] enums = ServicingTypeEnum.class.getEnumConstants();
		for (int i = 0; i < enums.length; i++) {

			ServicingTypeDTO dto = new ServicingTypeDTO(enums[i].getId(),enums[i].getName());
			typeList.add(dto);
		}

		return typeList;
	}

	private IPartyEntityManager getPartyEntityManager() {
		if (partyEntityManager == null) {
			try {
				partyEntityManager = ServiceLocator
						.lookupService(IPartyEntityManager.class);
			} catch (NamingException namingErr) {
				logger.error(this.getPageName()
						+ " IPartyEntityManager can not be lookedup:"
						+ namingErr.getMessage());
				CommunicationException comm = new CommunicationException(
						"IPartyEntityManager can not be looked up!");
				throw new CommunicationException(comm);
			}
		}
		return partyEntityManager;
	}

	protected ISalesBCLinkingGuiController getGUIController() {
		if (guiController == null) {
			try {
				guiController = ServiceLocator
						.lookupService(ISalesBCLinkingGuiController.class);
			} catch (NamingException namingErr) {
				logger.error(this.getPageName()
						+ " ISalesBCLinkingGuiController can not be lookedup:"
						+ namingErr.getMessage());
				CommunicationException comm = new CommunicationException(
						"ISalesBCLinkingGuiController can not be looked up!");
				throw new CommunicationException(comm);
			}
		}
		return guiController;
	}
	public void setPageModel(SalesBCLinkingPageModel pageModel) {
		this.pageModel = pageModel;
	}
}
