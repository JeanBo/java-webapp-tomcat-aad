package za.co.liberty.web.pages.request;

import java.util.ArrayList;
import java.util.List;

import javax.naming.NamingException;

import org.apache.wicket.extensions.ajax.markup.html.tabs.AjaxTabbedPanel;
import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.extensions.markup.html.tabs.TabbedPanel;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

import za.co.liberty.business.guicontrollers.request.IRequestEnquiryGuiController;
import za.co.liberty.dto.agreement.request.RequestEnquiryRowDTO;
import za.co.liberty.dto.gui.request.RequestEnquiryDTO;
import za.co.liberty.dto.userprofiles.ContextDTO;
import za.co.liberty.dto.userprofiles.ISessionUserProfile;
import za.co.liberty.dto.userprofiles.TeamDTO;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.helpers.lookup.ServiceLocator;
import za.co.liberty.interfaces.agreements.requests.RequestDateType;
import za.co.liberty.interfaces.agreements.requests.RequestKindType;
import za.co.liberty.interfaces.agreements.requests.RequestStatusType;
import za.co.liberty.interfaces.gui.request.RequestEnquiryContextType;
import za.co.liberty.web.data.enums.ContextType;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.pages.BasePage;
import za.co.liberty.web.pages.request.model.RequestEnquiryModel;
import za.co.liberty.web.system.SRSAuthWebSession;
import za.co.liberty.web.wicket.markup.html.tabs.CachingTab;

/**
 * <p>This page deals with all searches relating to Requests and also allows
 * for the approval of them.</p>
 * 
 * @author JZB0608 - 24 Nov 2009
 *
 */
public class RequestEnquiryPage extends BasePage {

	private static final long serialVersionUID = -3659488245709157662L;

	private transient IRequestEnquiryGuiController guiController;
	
	private static final String COMP_TABBED_PANEL = "tabbedPanel";
	
	@SuppressWarnings("unused")
	private Form<?> pageForm;
	protected TabbedPanel tabbedPanel;
	protected RequestEnquiryModel pageModel;
	
	/**
	 * Default constructor.
	 *
	 */
	public RequestEnquiryPage() {
		pageModel = createPageModel();
		this.add(pageForm=createPageFormField("pageForm"));
		this.setEditState(EditStateType.VIEW, null);
	}
	
	/**
	 * Create the form and 
	 * @param id
	 * @return
	 */
	private Form<?> createPageFormField(String id) {
		Form<?> form = new Form<Object>(id);
		form.add(tabbedPanel=createTabbedPanelField(COMP_TABBED_PANEL));
		return form;
	}

	/**
	 * Return an instance to the GuiController bean for this page.
	 * 
	 * @return
	 */
	private IRequestEnquiryGuiController getGuiController() {
		if (guiController==null) {
			try {
				guiController = ServiceLocator.lookupService(IRequestEnquiryGuiController.class);
			} catch (NamingException e) {
				throw new CommunicationException();
			}
		}
		return guiController;
	}

	/**
	 * Initialise the pageModel
	 * 
	 * @return
	 */
	private RequestEnquiryModel createPageModel() {

		IRequestEnquiryGuiController controller = getGuiController();
		RequestEnquiryModel model = new RequestEnquiryModel();
		
		model.setAllAgreementKindTypeList(controller.getAllAgreementKindTypeList());
		model.setAllRequestCategoryList(controller.getAllRequestCategoryList());
		model.setAllRequestKindTypeList(new ArrayList<RequestKindType>(controller.getAllRequestKindTypeList()));
		model.setAllTeamList(controller.getAllTeamList());
		model.setAllRequestStatusTypeList(controller.getAllRequestStatusTypeList());
		model.setAllPeriodList(controller.getPeriodList());
		model.setAllRequestDateType(controller.getAllRequestDateTypeList());
		model.setAllPropertyOnlyRequestKindSet(controller.getAllPropertyOnlyRequestKindSet());
		model.setAllCommissionKinds(controller.getAllCommissionKinds());
		model.setAllProductReferences(controller.getAllProductReferences());
		model.setAllContributionIncIndicators(controller.getAllContributionIncIndicatorList());
		model.setAllProductCodes(controller.getAllProductCodes());
		model.setAllFundCodes(controller.getAllFundCodes());
		
		return model;
	}


	/**
	 * Create the tabbed panel for this page.
	 * 
	 * @param id
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private TabbedPanel createTabbedPanelField(String id) {
		List<ITab> tabList = new ArrayList<ITab>();

		final Model<RequestEnquiryModel> model = new Model<RequestEnquiryModel>(pageModel);
		final ContextDTO context = getPageContextDTO();
		
		/* Add context search Panel */
		if (context!=null
				&& context.getAllAgreementsList().size()>0) {
			// Only added if there is a record in context
			
			tabList.add(new CachingTab(new Model<String>("Context Search")) {
				private static final long serialVersionUID = -7254885301327316998L;
	
				@Override
				public Panel createPanel(String id) {
					RequestEnquiryDTO dataModel = new RequestEnquiryDTO();
					dataModel.setSearchResultList(new ArrayList<RequestEnquiryRowDTO>());
					//dataModel.setDPETotals(new ArrayList<ContractEnquiryDTO>());
					pageModel.setDataModel(dataModel, RequestContextPanel.class);
					
					// Initialse default values
					dataModel.setRequestStatus(RequestStatusType.REQUIRES_AUTHORISATION);
					dataModel.setRequestDateType(RequestDateType.REQUEST);
					dataModel.setRequestEnquiryPeriod(pageModel.getAllPeriodList().get(0));
					dataModel.setRequestContextType((getPageContextDTO().getAgreementContextDTO()!=null)
							? RequestEnquiryContextType.SELECTED_AGREEMENT : RequestEnquiryContextType.ALL_AGREEMENTS);
					
					RequestContextPanel p =  new RequestContextPanel(id, model, getFeedbackPanel());
					p.setContextDTO(context);
					return p;
				}
			});
		}
		
		/* Add User search Panel */
		tabList.add(new CachingTab(new Model<String>("User Search")) {
			private static final long serialVersionUID = -7254885311327316931L;
			@Override
			public Panel createPanel(String id) {
				RequestEnquiryDTO dataModel = new RequestEnquiryDTO();
				dataModel.setSearchResultList(new ArrayList<RequestEnquiryRowDTO>());
				//dataModel.setDPETotals(new ArrayList<ContractEnquiryDTO>());
				pageModel.setDataModel(dataModel, RequestUserPanel.class);
				
				// Initialse default values
				dataModel.setRequestStatus(RequestStatusType.REQUIRES_AUTHORISATION);
				dataModel.setRequestDateType(RequestDateType.REQUEST);
				dataModel.setRequestEnquiryPeriod(pageModel.getAllPeriodList().get(0));
				ISessionUserProfile sessionUser = ((SRSAuthWebSession)getSession()).getSessionUser();
				if (sessionUser != null) {
					dataModel.setUser(getGuiController().findUserWithPartyOid(
							sessionUser.getPartyOid()));
				}
				return new RequestUserPanel(id, model, getFeedbackPanel());
			}
		});
		
		/* Add Team search Panel */
		tabList.add(new CachingTab(new Model<String>("Team Search")) {
			private static final long serialVersionUID = -7254885311327316991L;

			@Override
			public Panel createPanel(String id) {
				RequestEnquiryDTO dataModel = new RequestEnquiryDTO();
				dataModel.setSearchResultList(new ArrayList<RequestEnquiryRowDTO>());
				//dataModel.setDPETotals(new ArrayList<ContractEnquiryDTO>());
				pageModel.setDataModel(dataModel, RequestTeamPanel.class);
				
				// Initialse default values
				dataModel.setRequestDateType(RequestDateType.REQUEST);
				dataModel.setRequestEnquiryPeriod(pageModel.getAllPeriodList().get(0));
				dataModel.setRequestStatus(RequestStatusType.REQUIRES_AUTHORISATION);
				
				ISessionUserProfile sessionUser = ((SRSAuthWebSession)getSession()).getSessionUser();
				if (sessionUser != null) {
					TeamDTO teamDto = getGuiController().findUserTeamWithPartyOid(sessionUser.getPartyOid());
					dataModel.setTeam(teamDto);
				}
				return new RequestTeamPanel(id, model, getFeedbackPanel());
			}
		});
		
		/* Add Policy Transaction Search Panel*/
		
		tabList.add(new CachingTab(new Model<String>("Policy Transaction Search")){

			private static final long serialVersionUID = 1L;
			
			public Panel createPanel(String id){
				
				RequestEnquiryDTO dataModel = new RequestEnquiryDTO();
				dataModel.setSearchResultList(new ArrayList<RequestEnquiryRowDTO>());
				dataModel.setRequestEnquiryPeriod(pageModel.getAllPeriodList().get(0));
				pageModel.setDataModel(dataModel, RequestPolicyTransactionPanel.class);
				
				return new RequestPolicyTransactionPanel(id, model, getFeedbackPanel());
			}
			
		});
		
		/* Add Generic search Panel */
		tabList.add(new CachingTab(new Model<String>("Generic Search")) {
			private static final long serialVersionUID = -7254885311327316921L;
			@Override
			public Panel createPanel(String id) {
				RequestEnquiryDTO dataModel = new RequestEnquiryDTO();
				dataModel.setSearchResultList(new ArrayList<RequestEnquiryRowDTO>());
				//dataModel.setDPETotals(new ArrayList<ContractEnquiryDTO>());
				pageModel.setDataModel(dataModel, RequestGenericPanel.class);
				
				// Initialse default values
				dataModel.setRequestDateType(RequestDateType.REQUEST);
				dataModel.setRequestEnquiryPeriod(pageModel.getAllPeriodList().get(0));
				dataModel.setRequestStatus(RequestStatusType.REQUIRES_AUTHORISATION);
				return new RequestGenericPanel(id, model, getFeedbackPanel());
			}
		});
		
		TabbedPanel panel = new AjaxTabbedPanel(id, tabList);
		return panel;
	}

	@Override
	public String getPageName() {
		return "Request Enquiry";
	}

	@Override
	public ContextType getContextTypeRequired() {
		return ContextType.AGREEMENT;
	}

}
