package za.co.liberty.web.pages.request;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Set;

import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.apache.wicket.Component;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.pages.RedirectPage;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import za.co.liberty.business.guicontrollers.request.IRequestEnquiryGuiController;
import za.co.liberty.business.guicontrollers.request.IRequestViewGuiController;
import za.co.liberty.business.request.IRequestEnquiryManagement;
import za.co.liberty.dto.agreement.request.RequestEnquiryResultDTO;
import za.co.liberty.dto.agreement.request.RequestEnquiryRowDTO;
import za.co.liberty.dto.agreement.request.RequestEnquirySearchDTO;
import za.co.liberty.dto.gui.request.RequestEnquiryDTO;
import za.co.liberty.dto.gui.request.RequestEnquiryPageModelDTO;
import za.co.liberty.dto.gui.request.ViewRequestContextModelDTO;
import za.co.liberty.dto.gui.request.ViewRequestModelDTO;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.exceptions.fatal.InconsistentConfigurationException;
import za.co.liberty.helpers.lookup.ServiceLocator;
import za.co.liberty.interfaces.agreements.requests.RequestKindType;
import za.co.liberty.interfaces.persistence.agreement.request.IRequestEnquiryRow;
import za.co.liberty.web.data.enums.GuiRequestAuthorisationMappingEnum;
import za.co.liberty.web.data.enums.IAuthorisationMapping;
import za.co.liberty.web.data.enums.RequestAuthorisationMappingEnum;
import za.co.liberty.web.pages.request.model.RequestEnquiryModel;
import za.co.liberty.web.pages.request.tree.RequestTreePanel;
import za.co.liberty.web.pages.request.tree.model.RequestTreePanelModel;
import za.co.liberty.web.system.SRSApplication;
import za.co.liberty.web.wicket.markup.html.link.SRSInlineFrame;

/**
 * Test page for requests which allows you to call the page with a variable indicating
 * the requestOid that we are testing with.
 * 
 * 
 * @author JZB0608
 *
 */
public class ViewRequestWindowPageTest extends ViewRequestWindowPage {

	/* Constants */
	private static final long serialVersionUID = 4008008244319434312L;
	private static final Logger logger = Logger.getLogger(ViewRequestWindowPageTest.class);
	

	/**
	 * 
	 * @param parms
	 * 
	 */
	public ViewRequestWindowPageTest(PageParameters parms) {
		super(null, createRequestEnquiryDtoForOid((parms.get("requestOid")!=null && !parms.get("requestOid").isNull())
				?parms.get("requestOid").toLong() : null));
	}
	
	
//	/**
//	 * NB! This is a test method only and should never be used in PRODUCTION.
//	 * 
//	 * This method is specifically only for TOMCAT server with dummy results
//	 * 
//	 * @param requestId
//	 * @return
//	 * @deprecated For testing only!
//	 */
//	protected static RequestEnquiryRowDTO createRequestEnquiryDtoForOid(Long requestId) {
//		
//		if (requestId == null) {
//			logger.warn("Initialised ViewRequest with no parameter");
//			return null;
//		}
//		logger.info("Load Test for RequestOid = " + requestId);
//		
//		// Get controller
//		IRequestEnquiryGuiController cont;
//		try {
//			cont = ServiceLocator.lookupService(IRequestEnquiryGuiController.class);
//		} catch (NamingException e1) {
//			throw new CommunicationException(e1);
//		}
//		
//		// 
//		RequestEnquirySearchDTO searchDto = new RequestEnquirySearchDTO();
//		searchDto.setRequestIdList(Arrays.asList(new Long[] {requestId}));
//		try {
//			RequestEnquiryDTO d = new RequestEnquiryDTO();
////			d.setRequestKind(RequestKindType.ProcessExternalPayments);
////			d.setrequest
//			cont.findRequests(searchValues, isDPERequest)
//			RequestEnquiryPageModelDTO resultDto = cont.findRequests(d, false);
//			
//			for (IRequestEnquiryRow r : resultDto.getEnquiryResultDto().getResultList()) {
//				if (r.getRequestId().equals(requestId)) {
//					return (RequestEnquiryRowDTO) r;
//				}
//			}
//			logger.warn("Unable to find requested requestId=" + requestId + " for view page");
//			return null;
////			 RequestEnquiryResultDTO result
////			// There can only be one!!! (Duncan McCloud) :)
////			return (RequestEnquiryRowDTO) result.getResultList().get(0);
//		} catch (Exception e) {
//			throw new RuntimeException(e);
//		}
//	}
	
	
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
	
	
}