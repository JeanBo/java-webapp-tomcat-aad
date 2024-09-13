package za.co.liberty.web.pages.request;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Set;

import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.apache.wicket.Component;
import org.apache.wicket.core.request.handler.PageProvider;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.pages.RedirectPage;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.http.WebRequest;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.StringValue;
import org.apache.wicket.util.string.interpolator.MapVariableInterpolator;

import za.co.liberty.business.guicontrollers.request.IRequestEnquiryGuiController;
import za.co.liberty.business.guicontrollers.request.IRequestViewGuiController;
import za.co.liberty.business.request.IRequestEnquiryManagement;
import za.co.liberty.dto.agreement.request.RequestEnquiryResultDTO;
import za.co.liberty.dto.agreement.request.RequestEnquiryRowDTO;
import za.co.liberty.dto.agreement.request.RequestEnquirySearchDTO;
import za.co.liberty.dto.gui.request.ViewRequestContextModelDTO;
import za.co.liberty.dto.gui.request.ViewRequestModelDTO;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.exceptions.fatal.InconsistentConfigurationException;
import za.co.liberty.helpers.lookup.ServiceLocator;
import za.co.liberty.interfaces.agreements.requests.RequestKindType;
import za.co.liberty.web.data.enums.GuiRequestAuthorisationMappingEnum;
import za.co.liberty.web.data.enums.IAuthorisationMapping;
import za.co.liberty.web.data.enums.RequestAuthorisationMappingEnum;
import za.co.liberty.web.pages.BaseWindowPage;
import za.co.liberty.web.pages.request.model.RequestEnquiryModel;
import za.co.liberty.web.pages.request.tree.RequestTreePanel;
import za.co.liberty.web.pages.request.tree.model.RequestTreePanelModel;
import za.co.liberty.web.system.SRSApplication;
import za.co.liberty.web.system.SRSAuthWebSession;
import za.co.liberty.web.wicket.markup.html.link.SRSInlineFrame;

/**
 * Page for viewing requests in a modal window.
 * 
 * @author JZB0608 - 10 Feb 2010
 *
 */
public class ViewRequestWindowPage extends BaseWindowPage {

	/* Constants */
	private static final long serialVersionUID = 4008008244319434921L;
	private static final Logger logger = Logger.getLogger(ViewRequestWindowPage.class);
	
	/* Form components */
	protected FeedbackPanel feedBackPanel;
	protected ViewRequestContextPanel viewRequestContextPanel; 
	protected ModalWindow modalWindow;
	
	/* Attributes */
	private transient IRequestViewGuiController guiController;
	
	private ViewRequestModelDTO pageModel;
	@SuppressWarnings("unused")
	private Component contentPanel;
	
	private transient IRequestEnquiryGuiController enquiryController;
//	/**
//	 * Rather use subclass for testing.  If you uncomment this method it causes
//	 * this page to be treated as a stateless Page and impacts the ModalWindow function.
//	 * 
//	 * @param parms
//	 * 
//	 * @deprecated Do not use this, it is for testing only!
//	 */
//	public ViewRequestWindowPage(PageParameters parms) {
//		this(null, createRequestEnquiryDtoForOid((parms.get("requestOid")!=null && !parms.get("requestOid").isNull())
//				?parms.get("requestOid").toLong() : null));
//	}
	
	/**
	 * Default constructor. 
	 * 
	 * @param modalWindow
	 * @param dto 
	 * @param contextType
	 * @param pageOptions
	 */
	public ViewRequestWindowPage(ModalWindow modalWindow, RequestEnquiryRowDTO dto) {
		super();
		this.modalWindow = modalWindow;
		
		if (dto==null) {
			logger.warn("Unable to initialise model due to null enquiryRowDto being passed");

//			pageModel = new ViewRequestModelDTO(RequestKindType.
		} else {
			initialiseModel(dto);
		}
		
		/* Add components */
		add(feedBackPanel = (FeedbackPanel) new FeedbackPanel("searchMessages")
				.setOutputMarkupId(true));
		
		
		if (dto==null) {
			add(new EmptyPanel("contextPanel"));
			add(new EmptyPanel("contentPanel"));
			return;
		}
		add(viewRequestContextPanel= createViewRequestContextPanel("contextPanel", 
				pageModel.getViewRequestContextDto()));
		add(contentPanel=createContentPanel("contentPanel"));
		
	}

	/**
	 * Create the contentPanel
	 * 
	 * @param string
	 * @return
	 */
	private Component createContentPanel(String id) {
		if (pageModel == null) {
			return new EmptyPanel(id);
		} else if (pageModel.isNewGuiRequest()) {
			if (logger.isDebugEnabled())
				logger.debug("New Request");
			return createAuthorisationPanel(id);	
		} else if (pageModel.isAlternativeRequest()){
			if (logger.isDebugEnabled())
				logger.debug("Alternative");

			return new AlternativeRequestEnquiryPanel(id, pageModel);
		} else {
			if (logger.isDebugEnabled())
				logger.debug("Properties");

//			 Ensure that we support these request kinds.
			RequestEnquiryModel vPageModel = new RequestEnquiryModel();
			Set<RequestKindType> allPropertyOnlyRequestKindSet = getEnquiryController().getAllPropertyOnlyRequestKindSet();
			String url = null;
			if(allPropertyOnlyRequestKindSet.contains(pageModel.getSelectedRequestKind())){
//				url = "/SRSBusWeb/secure/tree/RequestTree.jsp?requestNo="
//					+pageModel.getViewRequestContextDto().getRequestDto().getRequestId();
				
				if (logger.isDebugEnabled())
					logger.debug("SHOWING TREE PANEL for request");
				
					RequestTreePanelModel model = new RequestTreePanelModel();
					model.setRequestNo(pageModel.getViewRequestContextDto().getRequestDto().getRequestId());
					return new RequestTreePanel("contentPanel", model );
			} else{
				if (logger.isDebugEnabled())
					logger.debug("SHOWING OLD GUI for request");
				
				url =  SRSApplication.getBusWebRequestActionPath() + "?action=go&requestID="
						+pageModel.getViewRequestContextDto().getRequestDto().getRequestId();

				if (logger.isDebugEnabled())
					logger.debug("SHOWING OLD GUI for request - URL = " + url);
				 
			}

			return new SRSInlineFrame(id, new PageProvider(new RedirectPage(url))
					).setHeightAttribute("500px");
		}
	}

	/**
	 * Create the authorisation panel
	 * 
	 * @param id
	 * @return
	 */
	private Panel createAuthorisationPanel(String id) {
		// Find the relevant GuiRequest Mapping enum
		IAuthorisationMapping authEnum = null;
		
		if (pageModel.getGuiRequestKind() != null) {
			// GUI request mapping
			authEnum = GuiRequestAuthorisationMappingEnum.getMappingEnumForGuiRequestKind(pageModel.getGuiRequestKind());
		}
		if (authEnum==null && pageModel.getRequestKindList().size()==1) {
			// Normal request mapping
			authEnum = RequestAuthorisationMappingEnum.getMappingEnumForRequestKind(pageModel.getRequestKindList().get(0));
		}
		
		// Issue occurred 
		if (authEnum==null) {
			throw new InconsistentConfigurationException("The GUI request kind \""+pageModel
					+"\" has not been configured correctly.  Unable to find Auth Mapping.");
		}
		if (logger.isDebugEnabled()) 
			logger.debug("Mapping Enum:"+authEnum);
		
		// Get the authorisation page
		Class<? extends BaseRequestViewAndAuthorisePanel> authPanelClass = authEnum.getAuthorisationPanelClass();
		if (authPanelClass==null) { 
			throw new InconsistentConfigurationException("The GUI request or request kind \""+pageModel
					+"\" has not been configured correctly. Unable to find auth panel.");
		}
		if (logger.isDebugEnabled())
			logger.debug("Auth panel class:"+authPanelClass.getName());
		
	
		try {
			// Initialise the auth panel & show
			Constructor constructor = authPanelClass.getConstructor(new Class[] {
					String.class, ViewRequestModelDTO.class} );
			BaseRequestViewAndAuthorisePanel panel = (BaseRequestViewAndAuthorisePanel) 
				constructor.newInstance(id, pageModel);
			return panel;
		} catch (NoSuchMethodException e) {
			throw new InconsistentConfigurationException(
					"Unable to view request due to a configuration error", e);
		} catch (InstantiationException e) {
			throw new InconsistentConfigurationException(
					"Unable to view request due to a configuration error", e);
		} catch (IllegalAccessException e) {
			throw new InconsistentConfigurationException(
					"Unable to view request due to a configuration error", e);
		} catch (InvocationTargetException e) {
			throw new InconsistentConfigurationException(
					"Unable to view request due to a configuration error", e);
		}

	}	
	
	/**
	 * Create the context panel
	 * 
	 * @param id
	 * @param dto
	 * @return
	 */
	private ViewRequestContextPanel createViewRequestContextPanel(String id, 
			ViewRequestContextModelDTO dto) {
		ViewRequestContextPanel panel = new ViewRequestContextPanel(id, dto);
		return panel;
	}

	/**
	 * Initialise the data models used for this page.
	 * 
	 * @param dto
	 */
	private void initialiseModel(RequestEnquiryRowDTO rowDto) {		
		// Initialise model for request
		pageModel = getGuiController().initialiseRequestViewModel(rowDto);
	}
	
	/**
	 * Return an instance to the GuiController bean for this page.
	 * 
	 * @return
	 */
	protected IRequestViewGuiController getGuiController() {
		if (guiController==null) {
			try {
				guiController = ServiceLocator.lookupService(IRequestViewGuiController.class);
			} catch (NamingException e) {
				throw new CommunicationException(e);
			}
			
		}
		return guiController;
	}
	
	@Override
	public String getPageName() {
		return "Manage Request";
	}
	
	/**
	 * Decorate the style tag to hide the component
	 * 
	 * @param isHidden Hide component if true. 
	 * @param tag
	 */
	@SuppressWarnings("unused")
	private void decorateComponentStyle(boolean isHidden, ComponentTag tag) {
		if (!isHidden) {
			return;
		}
		String val = (String) tag.getAttributes().get("style");
		val = (val ==null) ? "" : val;
		val += " ;visibility:hidden;";
		tag.put("style", val);
	}
	
	@Override
	public FeedbackPanel getFeedBackPanel() {
		return feedBackPanel;
	}

	@Override
	public boolean isShowFeedBackPanel() {
		return false;
	}
	
	/**
	 * NB! This is a test method only and should never be used in PRODUCTION.
	 * 
	 * @param requestId
	 * @return
	 * @deprecated For testing only!
	 */
	protected static RequestEnquiryRowDTO createRequestEnquiryDtoForOid(Long requestId) {
		
		if (requestId == null) {
			logger.warn("Initialised ViewRequest with no parameter");
			return null;
		}
		
		// Get controller
		IRequestEnquiryManagement cont;
		try {
			cont = ServiceLocator.lookupService(IRequestEnquiryManagement.class);
		} catch (NamingException e1) {
			throw new CommunicationException(e1);
		}
		
		// 
		RequestEnquirySearchDTO searchDto = new RequestEnquirySearchDTO();
		searchDto.setRequestIdList(Arrays.asList(new Long[] {requestId}));
		try {
			RequestEnquiryResultDTO result = cont.findRequests(searchDto);
			// There can only be one!!! (Duncan McCloud) :)
			return (RequestEnquiryRowDTO) result.getResultList().get(0);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	/**
	 * Return an instance to the GuiController bean for this page.
	 * 
	 * @return
	 */
	private IRequestEnquiryGuiController getEnquiryController() {
		if (enquiryController==null) {
			try {
				enquiryController = ServiceLocator.lookupService(IRequestEnquiryGuiController.class);
			} catch (NamingException e1) {
				throw new CommunicationException(e1);
			}
		}
		return enquiryController;
	}
	
}