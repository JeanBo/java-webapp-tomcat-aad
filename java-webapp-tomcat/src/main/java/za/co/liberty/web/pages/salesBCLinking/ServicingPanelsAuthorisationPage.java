package za.co.liberty.web.pages.salesBCLinking;

import java.util.ArrayList;
import java.util.Date;

import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.Model;

import za.co.liberty.business.guicontrollers.request.IRequestEnquiryGuiController;
import za.co.liberty.dto.agreement.request.RequestEnquiryRowDTO;
import za.co.liberty.dto.gui.request.RequestEnquiryDTO;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.helpers.lookup.ServiceLocator;
import za.co.liberty.helpers.util.DateUtil;
import za.co.liberty.interfaces.agreements.requests.RequestDateType;
import za.co.liberty.interfaces.agreements.requests.RequestKindType;
import za.co.liberty.interfaces.agreements.requests.RequestStatusType;
import za.co.liberty.interfaces.gui.request.RequestEnquiryBulkAuthoriseType;
import za.co.liberty.web.pages.BaseWindowPage;
import za.co.liberty.web.pages.request.model.RequestEnquiryModel;

/**
 * Page for viewing List of panels within a Branch in a modal window.
 * 
 * We are using a variation of the request enquiry panels to allow for the authorisation
 * of requests.
 * 
 * @author jzb0608
 *
 */
public class ServicingPanelsAuthorisationPage extends BaseWindowPage {

	/* Constants */
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger
			.getLogger(ServicingPanelsAuthorisationPage.class);
	
	private transient IRequestEnquiryGuiController guiController;
	
	
	
	/* Form components */
	protected FeedbackPanel feedBackPanel;
	protected Form<?> pageForm;
	protected RequestEnquiryModel pageModel;
	
	
	/**
	 * Default constructor, adds form
	 */
	public ServicingPanelsAuthorisationPage() {
		super();
		pageModel = createPageModel();
		pageModel.setDataModel(createRequestModel(), ServicingPanelsAuthorisationSearchPanel.class);
		this.add(pageForm=createPageFormField("pageForm"));
	}
	
	/**
	 * Create the form and panel
	 * 
	 * @param id
	 * @return
	 */
	private Form<?> createPageFormField(String id) {
		Form<?> form = new Form<Object>(id);
		final Model<RequestEnquiryModel> model = new Model<RequestEnquiryModel>(pageModel);
		ServicingPanelsAuthorisationSearchPanel panel = new ServicingPanelsAuthorisationSearchPanel("mainPanel", 
				model, getFeedBackPanel());
		form.add(panel);
		return form;
	}
	
	@Override
	public String getPageName() {
		return "Servicing Relationship Authorisation Page";
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
				throw new CommunicationException(e);
			}

		}
		return guiController;
	}

	private RequestEnquiryDTO createRequestModel() {
		RequestEnquiryDTO dto = new RequestEnquiryDTO();
		dto.setRequestKind(RequestKindType.MaintainCrossRegionServicingRelationships);
		DateUtil dateUtil = DateUtil.getInstance();
		dto.setStartDate(dateUtil.addMonths(new Date(),-2));
		dto.setEndDate(new Date());
		dto.setRequestDateType(RequestDateType.REQUEST);
		dto.setRequestStatus(RequestStatusType.REQUIRES_AUTHORISATION);
		dto.setSearchResultList(new ArrayList<RequestEnquiryRowDTO>());
		dto.setBulkAuthoriseType(RequestEnquiryBulkAuthoriseType.PANEL_TRANSFER);
		return dto;
	}
	
	/**
	 * Initialise the pageModel
	 * 
	 * @return
	 */
	private RequestEnquiryModel createPageModel() {

		IRequestEnquiryGuiController controller = getGuiController();
		RequestEnquiryModel model = new RequestEnquiryModel();
		
		/*
		 * Set the lists
		 */
		model.setAllRequestDateType(controller.getAllRequestDateTypeList());
		model.setAllPropertyOnlyRequestKindSet(controller.getAllPropertyOnlyRequestKindSet());
		
		
		
		return model;
	}
	
}