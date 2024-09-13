/**
 * 
 */
package za.co.liberty.web.pages.agreementprivilege;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.naming.NamingException;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow.MaskType;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.Panel;

import za.co.liberty.business.guicontrollers.IAgreementPrivilegesController;
import za.co.liberty.dto.agreementprivileges.AgreementPrivilegesDataDTO;
import za.co.liberty.dto.agreementprivileges.ExplicitAgreementType;
import za.co.liberty.dto.party.PersonDTO;
import za.co.liberty.dto.userprofiles.ContextAgreementDTO;
import za.co.liberty.dto.userprofiles.ContextDTO;
import za.co.liberty.dto.userprofiles.ContextPartyDTO;
import za.co.liberty.exceptions.data.DataNotFoundException;
import za.co.liberty.exceptions.data.InconsistentDataException;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.helpers.lookup.ServiceLocator;
import za.co.liberty.web.data.enums.ContextType;
import za.co.liberty.web.models.PagePanelInfoObject;
import za.co.liberty.web.pages.MaintenanceBasePage;
import za.co.liberty.web.pages.agreementprivilege.model.AgreementLinkingPageModel;
import za.co.liberty.web.pages.panels.BaseModificationButtonsPanel;
import za.co.liberty.web.system.SRSAuthWebSession;

/**
 * Agreement linking page, This page links agreements to users so that users can
 * access these agreements
 * 
 * @author dzs2610
 * 
 */
public class AgreementLinkingPage extends MaintenanceBasePage<Integer> {

	private static final long serialVersionUID = 1L;

	private AgreementLinkingPageModel pageModel;

	private String pageName = "Agreement Privileges";
	ContextPartyDTO partydto;
	private ModalWindow window;
	boolean showButtons = true;
	

	/**
	 * 
	 */
	public AgreementLinkingPage() {
		super(null);
		this.add(window = createModalWindow("roleModelWindow"));
	}

	/**
	 * @param obj
	 */
	public AgreementLinkingPage(Object obj) {
		super(obj);			
		ContextDTO dto = SRSAuthWebSession.get().getContextDTO();
		if(dto != null){
			partydto = dto.getPartyContextDTO();			
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see za.co.liberty.web.pages.MaintenanceBasePage#createContainerPanel()
	 */
	@Override
	public Panel createContainerPanel() {
		Panel panel;
		if (pageModel.getPartyOid() == 0) {
			panel = new EmptyPanel(CONTAINER_PANEL_NAME);
		} else {			
			panel = new AgreementLinkingPanel(CONTAINER_PANEL_NAME, pageModel,
						getEditState(),this);			
		}
		panel.setOutputMarkupId(true);
		return panel;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see za.co.liberty.web.pages.MaintenanceBasePage#createNavigationalButtons()
	 */
	@Override
	public Button[] createNavigationalButtons() {
		return new Button[] { createSaveButton("button1"),
				createCancelButton("button2") };
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see za.co.liberty.web.pages.MaintenanceBasePage#createSelectionPanel()
	 */
	@Override
	public Panel createSelectionPanel() {
		return new BaseModificationButtonsPanel<AgreementPrivilegesDataDTO>(
				SELECTION_PANEL_NAME, pageModel, this, selectionForm,
				AgreementPrivilegesDataDTO.class,this.getFeedbackPanel(), false,
				false,false,false,false,false) {

			private static final long serialVersionUID = 1L;

			@Override
			public void resetSelection() {
				// TODO Dean check this out, at the moment this does nothing
			}

			@Override
			protected Button[] replaceButtons(Button[] buttons) {				
				/* I am replacing the modify button now as it was decided to not modify now
			     * once this is changed, one must just replace one of the unused buttons in the array sent in
				 */	
				Button showRoles = new Button(buttons[0].getId());
				showRoles.add(new AttributeModifier("value","Show Roles"));
				showRoles.add(new AjaxFormComponentUpdatingBehavior("click"){					
					private static final long serialVersionUID = 1L;
					@Override
					protected void onUpdate(AjaxRequestTarget target) {
						window.show(target);						
					}					
				});
				buttons[0] = showRoles;
				showRoles.setEnabled(true);
				//now we check if the user has acess to view the roles
//				ISessionUserProfile user = SRSAuthWebSession.get().getSessionUser();
//				if(user == null || !user.isAllowedToViewAllAgreements()){
//					showRoles.setEnabled(false);
//					showRoles.add(new SimpleAttributeModifier("title","You do not have access to view this users roles"));
//				}				
				return buttons;
			}			
		};		
	}
	
	/**
	 * Create the modal window
	 * 
	 * @param id
	 * @return
	 */
	public ModalWindow createModalWindow(String id) {		
		final ModalWindow window = new ModalWindow(id);
		window.setTitle("User Roles");
		
		//window.setCookieName(getCookieName());			
		// Create the page
		window.setPageCreator(new ModalWindow.PageCreator() {
			private static final long serialVersionUID = 1L;
			public Page createPage() {	
				ContextDTO dto = SRSAuthWebSession.get().getContextDTO();
				if(dto != null){
					partydto = dto.getPartyContextDTO();						
				}					
				return new UserRolePage(window,partydto);			
			}
		});		

		// Initialise window settings
		window.setMinimalHeight(300);
		window.setInitialHeight(300);
		window.setMinimalWidth(750);
		window.setInitialWidth(750);
		window.setMaskType(MaskType.SEMI_TRANSPARENT);
		window.setCssClassName(ModalWindow.CSS_CLASS_GRAY);
		
		return window;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see za.co.liberty.web.pages.MaintenanceBasePage#initialisePageModel(java.lang.Object)
	 */
	@Override
	public Object initialisePageModel(Object obj, Integer currentTab) {
		AgreementLinkingPageModel model = new AgreementLinkingPageModel();
		ContextDTO dto = SRSAuthWebSession.get().getContextDTO();// this.getPageContextDTO();
		ContextAgreementDTO agreement = null;
		ContextPartyDTO partydto = null;
		if(dto != null){
			agreement = dto.getAgreementContextDTO();
			partydto = dto.getPartyContextDTO();
		}
		if (partydto == null || partydto.getPartyOid() == 0) {
			error("To view agreement details a Party needs to be selected in the context panel above.");
		}else if(partydto.getUacfID() == null){
			error("The selected party does not have a UACFID attached, Please add one to view the agreements linked");
		} else {
			if (obj != null) {
				model = (AgreementLinkingPageModel) obj;
			} else {
				long partyOID = dto.getPartyContextDTO().getPartyOid();
				model.setPartyOid(partyOID);
				model.setPersonDTOList(new HashSet<PersonDTO>());
				model.setUacfId(dto.getPartyContextDTO().getUacfID());
				model.setOwnAgreementList(getOwnAgreements(partyOID));
				model.setExplicitAgreements(getExplicitAgreements(partyOID));
				model.setReportToAgreements(getReportToAgreements(partyOID));
			}
			if (agreement == null || agreement.getAgreementNumber() == null) {
				//info("To view assigned parties to agreements, please select an agreement into the context");
				// still need to display all agreements linked to person in
				// context
				
			} else {				
				model.setAgreementNo(agreement.getAgreementNumber());
				// only put linked agreements for the current selected
				// agreement
				model.setAcessGrantedOwnAgreementList(getAgreementsTheUserAllowedAccess(
								model.getOwnAgreementList(), agreement
										.getAgreementNumber()));
				model.setAgreementNo(agreement.getAgreementNumber());
				for (AgreementPrivilegesDataDTO agreementPrivilegesDataDTO : model.getOwnAgreementList()) {
					if (agreementPrivilegesDataDTO.getAgreementOID() == agreement.getAgreementNumber()) {
						model.setSelectedItem(agreementPrivilegesDataDTO);
						break;
					}
				}
			}
		}
		pageModel = model;	
		return pageModel;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see za.co.liberty.web.pages.BasePage#getPageName()
	 */
	@Override
	public String getPageName() {
		return pageName;
	}

	@Override
	public void doSave_onSubmit() {
		/* Validate that all components first (all tab panels) */
		String uacfIDOfCurrentUser = SRSAuthWebSession.get().getCurrentUserid();		
		AgreementLinkingPanel linkingPanel = (AgreementLinkingPanel) containerPanel;
		if (linkingPanel.validateAllTabs() == false) {
			return;
		}

		/* Save to db */
		try {
			updateExplicitAgreements(pageModel
					.getAcessGrantedOwnAgreementList(),
					pageModel.getPartyOid(), uacfIDOfCurrentUser);
			invalidatePage();
			this.info(this.getString("success.modify"));
			setResponsePage(new AgreementLinkingPage(pageModel));
			// setResponsePage(new AgreementLinkingPage());
		} catch (DataNotFoundException e) {
			error(e.getUserErrorMessage());
			setResponsePage(this);
		} catch (InconsistentDataException e) {
			error("Could not save due to: " + e.getUserErrorMessage());
			setResponsePage(this);
		}		
	}

	/**
	 * store agreement links
	 * 
	 * @param linkAgreementDataDTOList
	 * @throws DataNotFoundException
	 * @throws InconsistentDataException 
	 */
	private void updateExplicitAgreements(
			List<AgreementPrivilegesDataDTO> linkAgreementDataDTOList,
			long partyOid, String uacfidOfUpdatingUser)
			throws DataNotFoundException, InconsistentDataException {
		IAgreementPrivilegesController agreementPrivilegesController = getAgreementPrivilegesController();
		if (agreementPrivilegesController != null) {
//			jzb0608 - This functionality is disabled (only updated by batch)
//			agreementPrivilegesController.updateExplicitAgreements(
//					linkAgreementDataDTOList, partyOid, uacfidOfUpdatingUser);
		} else {
			throw new RuntimeException(
					"There was a problem getting the AgreementPrivilegesController session Bean");
		}
	}

	/**
	 * Return a list of agreement(People) that have been linked to the given
	 * agreement number (agreementNumberAccessGiven)
	 * 
	 * @param partyOid
	 * @param agreementNumberAccessGiven
	 * @return
	 */
	private List<AgreementPrivilegesDataDTO> getAgreementsTheUserAllowedAccess(
			List<AgreementPrivilegesDataDTO> ownAgreements,
			long agreementNumberAccessGiven) {
		
		AgreementPrivilegesDataDTO ownAgreement = null;
		for (AgreementPrivilegesDataDTO d:ownAgreements) {
			if (agreementNumberAccessGiven == d.getAgreementOID()) {
				ownAgreement = d;
				break;
			}
		}
		return getAgreementsTheUserAllowedAccess(ownAgreement);

	}

	/**
	 * Get all the agreements linked to the parties(partyOid) agreements
	 * 
	 * @param partyOid
	 * @return
	 */
	private List<AgreementPrivilegesDataDTO> getAgreementsTheUserAllowedAccess(
			AgreementPrivilegesDataDTO ownAgreements) {
		List<AgreementPrivilegesDataDTO> results = null;

		try {
			IAgreementPrivilegesController agreementPrivilegesController = getAgreementPrivilegesController();
			results = agreementPrivilegesController.getAgreementsThatTheUserAllowedAccess(ownAgreements);
		} catch (DataNotFoundException e) {
			results = new ArrayList<AgreementPrivilegesDataDTO>();
		}

		return results;
	}

	/**
	 * returns a AgreementPrivilegesDataDTO which represents a list of a parties
	 * own agreements
	 * 
	 * @param partyOid
	 * @return
	 */
	private List<AgreementPrivilegesDataDTO> getOwnAgreements(long partyOid) {
		List<AgreementPrivilegesDataDTO> results = new ArrayList<AgreementPrivilegesDataDTO>();

		try {
			IAgreementPrivilegesController agreementPrivilegesController = getAgreementPrivilegesController();
			results = agreementPrivilegesController
					.getUserOwnAgreements(partyOid);
			// add in the type of agreement
			for (AgreementPrivilegesDataDTO dto : results) {
				dto.setExplicitAgreementType(ExplicitAgreementType.OWN_AGREEMENT);
			}
		} catch (DataNotFoundException e) {
			// do nothing, empty list returned to user
		}catch (InconsistentDataException e) {
			error("Could not retreive you own agreements as inconsistent data was found");
		}
		return results;
	}

	/**
	 * returns a list of AgreementPrivilegesDataDTO which are a parties explicit
	 * agreements
	 * 
	 * @param partyOid
	 * @return
	 */
	private List<AgreementPrivilegesDataDTO> getExplicitAgreements(long partyOid) {
		List<AgreementPrivilegesDataDTO> results = new ArrayList<AgreementPrivilegesDataDTO>();
		try {
			IAgreementPrivilegesController agreementPrivilegesController = getAgreementPrivilegesController();
			results = agreementPrivilegesController
					.getAgreementsTheUserHasBeenAllowedAccess(partyOid);
			// add in the type of agreement
			for (AgreementPrivilegesDataDTO dto : results) {
				dto.setExplicitAgreementType(ExplicitAgreementType.EXPLICT_AGREEMENT);
			}
		} catch (DataNotFoundException e) {
			// do nothing, empty list is returned
		} catch (InconsistentDataException e) {
			error("Could not retreive the explicit agreements as inconsistent data was found");
		}
		return results;
	}

	/**
	 * Gets a list of AgreementPrivilegesDataDTO which contain a parties report
	 * to agreements
	 * 
	 * @param partyOid
	 * @return
	 */
	private List<AgreementPrivilegesDataDTO> getReportToAgreements(long partyOid) {
		List<AgreementPrivilegesDataDTO> reportsToAgreements = new ArrayList<AgreementPrivilegesDataDTO>();

		try {
			IAgreementPrivilegesController agreementPrivilegesController = getAgreementPrivilegesController();
			reportsToAgreements = agreementPrivilegesController
					.getReportsToAgreements(partyOid);
			// add in the type of agreement
			for (AgreementPrivilegesDataDTO dto : reportsToAgreements) {
				dto.setExplicitAgreementType(ExplicitAgreementType.REPORT_TO_AGREEMENT);
			}
		} catch (DataNotFoundException e) {
			// do nothing, empty list is returned
		} catch (InconsistentDataException e) {
			error("Could not retreive the reports to agreements as inconsistent data was found");
		}
		return reportsToAgreements;
	}

	/**
	 * Gets the IAgreementPrivilegesController interface for calls to the
	 * AgreementPrivilegesController session bean
	 * 
	 * @return
	 */
	private IAgreementPrivilegesController getAgreementPrivilegesController() {
		IAgreementPrivilegesController agreementPrivilegesController = null;
		try {
			agreementPrivilegesController = ServiceLocator.lookupService(IAgreementPrivilegesController.class);
		} catch (NamingException e) {
			throw new CommunicationException(e);
		}
		return agreementPrivilegesController;
	}

	@Override
	public ContextType getContextTypeRequired() {
		return ContextType.PARTY;
	}

	@Override
	public List<PagePanelInfoObject> getPagePanelsInfo() {
		//List<PagePanelInfoObject> ret = new ArrayList<PagePanelInfoObject>(2);		
		//ret.add(new PagePanelInfoObject("za.co.liberty.web.pages.agreementprivilege.AgreementAssignmentPanel",0));
		//ret.add(new PagePanelInfoObject("",0));
		//return null as the menu item security configuration will take precedence
		return null;
	}
}
