package za.co.liberty.web.pages.fitprop;

import java.util.Collection;
import java.util.List;

import javax.naming.NamingException;

import org.apache.wicket.authorization.UnauthorizedInstantiationException;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import za.co.liberty.business.guicontrollers.IContextManagement;
import za.co.liberty.business.guicontrollers.fitprop.IFitAndProperGuiController;
import za.co.liberty.dto.contracting.ResultAgreementDTO;
import za.co.liberty.dto.contracting.ResultPartyDTO;
import za.co.liberty.dto.userprofiles.ContextAgreementDTO;
import za.co.liberty.dto.userprofiles.ContextDTO;
import za.co.liberty.dto.userprofiles.ContextPartyDTO;
import za.co.liberty.dto.userprofiles.ISessionUserProfile;
import za.co.liberty.dto.userprofiles.SessionUserHierarchyNodeDTO;
import za.co.liberty.exceptions.data.DataNotFoundException;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.helpers.lookup.ServiceLocator;
import za.co.liberty.interfaces.agreements.ProductKindType;
import za.co.liberty.srs.type.SRSType;
import za.co.liberty.web.data.enums.ContextType;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.pages.BasePage;
import za.co.liberty.web.pages.DynamicContextPanel;
import za.co.liberty.web.pages.fitprop.model.FitAndProperPageModel;
import za.co.liberty.web.system.EJBReferences;
import za.co.liberty.web.system.SRSAuthWebSession;

/**
 * Fit and Proper page, view only page for all fit and proper details
 * @author DZS2610
 *
 */
public class FitAndProperPage extends BasePage {
	private static final long serialVersionUID = 1L;
		
	private FitAndProperPageModel pageModel;
	
	private transient IFitAndProperGuiController guiController;	
	
	private boolean initialised;
	
	private boolean disableContextPanel;
		
	
	/**
	 * Constructor called via blueprintonline
	 * @param parameters
	 */
	public FitAndProperPage(PageParameters parameters) {	
		super(true);
//		Check that the user has been authenticated correctly
		if(!SRSAuthWebSession.get().isAuthenticated()){
			SRSAuthWebSession.get().getFeedbackMessages().clear();
			throw new UnauthorizedInstantiationException(FitAndProperPage.class);
		}
		
		SRSAuthWebSession.get().setMenuItemsDisabledForUser(true);
		String signedOnUACFID = SRSAuthWebSession.get().getCurrentUserid();
		String consultantCode = parameters.get("consultantCode").toString();
		//disable the menu bar
		getLogger().info("Fit and Proper Gui called via external system by user " + signedOnUACFID + " for consultant code " + consultantCode);
		//put the agreement into context		
		ResultAgreementDTO agmt;
		try {
			agmt = getFitAndProperGuiController().findAgreementWithConsCode(consultantCode);		
//			check if user is actually allowed to view this agreement
			if(this.getSecurityManagement().canUserViewAgreementDetails(agmt.getAgreementNumber(), agmt.getHasHomePartyOid(), SRSAuthWebSession.get().getSessionUser())){			
				ResultPartyDTO party = getFitAndProperGuiController().findPartyWithAgreementNumber(agmt.getOid());				
				ContextDTO newContextDTO = SRSAuthWebSession.get().getContextDTO();			
				IContextManagement contextBean;
				try {
					contextBean = ServiceLocator.lookupService(IContextManagement.class);
				} catch (NamingException e) {
					throw new CommunicationException(e);
				}

				newContextDTO = contextBean.getContext(party,agmt);				
				SRSAuthWebSession.get().setContextDTO(newContextDTO);
				pageContextDTO = newContextDTO;
				//now that we have set the context, refresh the page
				initialisePageModel();
				SRSAuthWebSession.get().getFeedbackMessages().clear();			
			}else{
				error("You may not view consultant " + agmt.getConsultantCodeFormatted() + "'s fit and proper details due to rule restrictions");
			}
			setResponsePage(new FitAndProperPage(false));	
		} catch (NumberFormatException e) {
			error("Could not convert consultant code " + consultantCode + " into a number");			
		} catch (DataNotFoundException e) {
			error("Could not find any agreement with consultant code " + consultantCode);			
		}
	}
	
	/**
	 * Constructor called via blueprintonline
	 * @param parameters
	 */
	public FitAndProperPage(boolean disableContextPanel,boolean comingFromExternalSystem) {	
		super(true);	
//		Check that the user has been authenticated correctly
		if(!SRSAuthWebSession.get().isAuthenticated()){
			SRSAuthWebSession.get().getFeedbackMessages().clear();
			throw new UnauthorizedInstantiationException(BasePage.class);
		}
		this.disableContextPanel = disableContextPanel;
		SRSAuthWebSession.get().setMenuItemsDisabledForUser(true);
		String signedOnUACFID = SRSAuthWebSession.get().getCurrentUserid();		
		//disable the menu bar
		getLogger().info("Fit and Proper Gui called via external system by user " + signedOnUACFID);				
	}
	
	public FitAndProperPage(boolean disableContextPanel){
		this();
		this.disableContextPanel = disableContextPanel;		
	}	
	
	public FitAndProperPage(){
		super();
	}	
	
	@Override
	public ContextType getContextTypeRequired() {
		return ContextType.AGREEMENT;
	}
	
	/**
	 * Load the components on the page on first render, 
	 * so that the components are only generated when the page is displayed 
	 */
	@Override
	protected void onBeforeRender() {
		if(disableContextPanel && contextPanel != null && contextPanel instanceof DynamicContextPanel){
			((DynamicContextPanel)contextPanel).disableContext(null);
		}
		if(!initialised){
			initialisePageModel();
			add(createFitAndProperPanel("fitAndPropPanel"));	
			initialised = true;
		}
		super.onBeforeRender();
	}
		
	@Override
	public String getPageName() {	
		return "Journey to Professionalism Tracker";
	}

	
	public Panel createFitAndProperPanel(String id) {		
		//create the fit and proper panel 
		Panel panel = null;
		if(pageModel.getAgreementNumber() <= 0){
			panel = new EmptyPanel(id);
		}else{
			panel = new FitAndProperPanel(id,pageModel,
					EditStateType.VIEW,this.getFeedbackPanel(),this);
		}
		return panel;
	}	
	
	public Object initialisePageModel() {
		FitAndProperPageModel pageModel = new FitAndProperPageModel();
		ContextPartyDTO partyContext = pageContextDTO.getPartyContextDTO();			
		ContextAgreementDTO agmtContext = pageContextDTO.getAgreementContextDTO();
		List<ResultAgreementDTO> agmts = pageContextDTO.getAllAgreementsList();		
		if(!((agmtContext != null && agmtContext.getAgreementNumber() != null) || (agmts != null && agmts.size() != 0))){
			error("Please select an agreement using the Search Button above");				
		}
		if (partyContext == null || partyContext.getPartyOid() == 0) {
			error("There is no party selected in the context above, please find one using the search");
		}else if(partyContext.getTypeOid() == SRSType.DIVISION || partyContext.getTypeOid() == SRSType.REGION ||
				partyContext.getTypeOid() == SRSType.SUBREGION || partyContext.getTypeOid() == SRSType.BRANCH ||
				partyContext.getTypeOid() == SRSType.UNIT){
				error("Please go to the hierachy option to maintain hierarchy nodes, or search for an agreement");
		}else if(partyContext.getTypeOid() != SRSType.PERSON){
				error("Only persons can be viewed on this gui, organisations do not have a fit and proper status");
		}else{				
			if(agmtContext != null && agmtContext.getAgreementNumber() != null){
				pageModel.setAgreementNumber(agmtContext.getAgreementNumber());
				pageModel.setAgreementStartdate(agmtContext.getAgreementStartDate());
				ProductKindType agreementKind = agmtContext.getAgreementDivision();
				pageModel.setAgreementKind(agreementKind.getKind());
				//check if the logged in user is a manager of this agreement
				ISessionUserProfile loggedInUser = SRSAuthWebSession.get().getSessionUser();
				pageModel.setUserManagesAgreement(false);
              /*if(loggedInUser.hasHierarchicalAccess()){
					//manges a branch, check if the branch is this agreements branch
					ResultPartyDTO agmtBranch = agmtContext.getBranch();
					ResultPartyDTO agmtUnit = agmtContext.getUnit();
					Collection<SessionUserHierarchyNodeDTO> nodesManaged = loggedInUser.getHierarchicalNodeAccessList();
					for(SessionUserHierarchyNodeDTO node : nodesManaged){
						if(node.getPartyOid() == agmtBranch.getPartyOid()
								// jzb0608 - Added support for units
								|| (agmtUnit != null && agmtUnit.getPartyOid() == node.getPartyOid()) ){
							pageModel.setUserManagesAgreement(true);
							break;
						}
					}
					if (!pageModel.isUserManagesAgreement()) {
						// jzb0608 - Adding messages as there is a lot of confusion regarding edit access
						warn("Agreement may not be maintained as the current agreement does not belong " +
								"to one of your linked hierarchy branches or units");
					}
				} else {
					// jzb0608 - Adding messages as there is a lot of confusion regarding edit access
					info("Some of these values can be modified by linked branch or unit managers, secretaries or compliance officers");
				}*/
				info("Modifications can no longer be made on this Screen");
			}
			if(partyContext != null){
				pageModel.setPartyoid(partyContext.getPartyOid());
			}		
		}
		this.pageModel = pageModel;
		return this.pageModel;	
	}
	
	/**
	 * Get the gui controller for the Panel
	 * 
	 * @return
	 */
	private IFitAndProperGuiController getFitAndProperGuiController() {
		if (guiController == null) {
			try {
				guiController = ServiceLocator
						.lookupService(IFitAndProperGuiController.class);
			} catch (NamingException e) {
				throw new CommunicationException(e);
			}
		}
		return guiController;
	}
}
