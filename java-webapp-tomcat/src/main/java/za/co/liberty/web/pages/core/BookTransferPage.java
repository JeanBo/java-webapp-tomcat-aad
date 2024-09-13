package za.co.liberty.web.pages.core;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.NamingException;

import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;

import za.co.liberty.business.guicontrollers.core.ICoreTransferGuiController;
import za.co.liberty.dto.agreement.core.CoreConsultantDto;
import za.co.liberty.dto.agreement.core.CoreTransferDto;
import za.co.liberty.dto.agreement.request.RequestEnquiryResultDTO;
import za.co.liberty.dto.userprofiles.ISessionUserProfile;
import za.co.liberty.exceptions.data.DataNotFoundException;
import za.co.liberty.exceptions.data.QueryTimeoutException;
import za.co.liberty.exceptions.data.ValidationException;
import za.co.liberty.exceptions.error.request.RequestException;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.helpers.lookup.ServiceLocator;
import za.co.liberty.interfaces.agreements.AgreementStatusType;
import za.co.liberty.interfaces.agreements.requests.RequestKindType;
import za.co.liberty.web.data.enums.ContextType;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.pages.MaintenanceBasePage;
import za.co.liberty.web.pages.core.model.CoreTransferPageModel;
import za.co.liberty.web.system.SRSAuthWebSession;

public class BookTransferPage extends MaintenanceBasePage<Integer> {

	private static final long serialVersionUID = 1L;

	private String pageName = "Core Book Transfer";

	private CoreTransferPageModel pageModel;

	private Form pageForm;

	protected Panel panel;

	protected transient ICoreTransferGuiController guiController;

	protected transient Logger logger = Logger.getLogger(BookTransferPage.class
			.getName());

	public BookTransferPage() {
		this(null);
	}

	public BookTransferPage(Object obj) {
		super(obj);
	}

	@Override
	public Panel createContainerPanel() {
		Panel panel = null;
		try {
			//	setEditState(EditStateType.MODIFY,null);
			
			FeedbackPanel feedbackPanel = getFeedbackPanel();
			panel = new BookTransferPanel(CONTAINER_PANEL_NAME, pageModel,
					EditStateType.MODIFY, feedbackPanel);
		} catch (Exception e) {
			// display message that all tabs have been disabled
			error(e.getMessage());
			panel = new EmptyPanel(CONTAINER_PANEL_NAME);
		}
		panel.setOutputMarkupId(true);
		return panel;
	}

	@Override
	public Button[] createNavigationalButtons() {
		return null;
	}

	@Override
	public Panel createSelectionPanel() {
		return new CoreButtonsPanel<CoreTransferDto>(
				SELECTION_PANEL_NAME, pageModel, this, containerForm,
				CoreTransferDto.class, this.getFeedbackPanel(), false, true,
				true, false, false, false) {

			private static final long serialVersionUID = 1L;

			@Override
			public void resetSelection() {
			}

			@Override
			protected boolean isBroadcastButtonEnabled() {
				return false;
			}

			@Override
			protected boolean isModifyButtonEnabled() {
				return false;
			}

			@Override
			protected boolean isTerminateButtonenabled() {
				return false;
			}
		};
	}

	@Override
	public Object initialisePageModel(Object obj, Integer currentTab) {
		CoreTransferPageModel pageModel = new CoreTransferPageModel();
		guiController = getGUIController();
		pageModel.setRequestCategoryDTO(guiController
				.getAllRequestStatusTypeList());
		this.pageModel = pageModel;
		return pageModel;
	}

	@Override
	public String getPageName() {
		return pageName;
	}

	@Override
	public ContextType getContextTypeRequired() {
		return ContextType.AGREEMENT;
	}

	@Override
	public void doSave_onSubmit() {
		CoreTransferDto coreTransferDto=pageModel.getCoreTransferDto().get(0);
		doSearchButtonValidation(coreTransferDto);
		boolean res = feedbackPanel.anyErrorMessage();
		if (!res){ 
			coreTransferDto.setRequester(SRSAuthWebSession.get().getCurrentUserid());
			if(pageModel.getTransferType().equals("1"))
				coreTransferDto.setTransferTypeInd("S");
			else if(pageModel.getTransferType().equals("2"))
				coreTransferDto.setTransferTypeInd("C");
			/*if(coreTransferDto.getAdvisoryFeeIndicator()==null)
				coreTransferDto.setAdvisoryFeeIndicator(false);*/
			raiseRequests();
		
		}
	}

	protected void doSearchButtonValidation(CoreTransferDto coreTransferDto) {
		CoreHelper coreHelper=new CoreHelper();
		/**Added for outstanding request validation*/
		RequestEnquiryResultDTO requestEnquiryResultDTO=null;
		Map contractNumberMap=null;
		if(coreTransferDto!=null && coreTransferDto.getFromAgreementCode()!=null){
			try {
				requestEnquiryResultDTO=getGUIController().getOutstandingRequests(coreTransferDto.getFromAgreementCode()
					 ,RequestKindType.ProcessBookLevelTransfer);
			} catch (QueryTimeoutException e) {
				feedbackPanel.error("A background query timed out, please try again");
			} catch (RequestException e) {
				feedbackPanel.error("A background query had an error - " + e.getMessage());
			}
		}
		
		if(requestEnquiryResultDTO!=null && requestEnquiryResultDTO.getResultList()!=null && requestEnquiryResultDTO.getResultList().size()!=0){
			feedbackPanel.error("Outstanding Book Transfer Request is Pending  ");
		}
		if(coreTransferDto!=null && coreTransferDto.getFromAgreementCode()!=null){
			try {
				requestEnquiryResultDTO=getGUIController().getOutstandingRequests((coreTransferDto.getFromAgreementCode()),
						RequestKindType.ProcessSegmentedContractTransfer);
			} catch (QueryTimeoutException e) {
				feedbackPanel.error("A background query timed out, please try again");
			} catch (RequestException e) {
				feedbackPanel.error("A background query had an error - " + e.getMessage());
			}
			 contractNumberMap=coreHelper.getFromConsultantMap(requestEnquiryResultDTO);
		}
		 
		
		if(contractNumberMap!=null && contractNumberMap.get(coreTransferDto.getFromConsultantCode())!=null){
			feedbackPanel.error("Outstanding Segmented Transfer request is Pending  ");
		}
		
		/**End for outstanding request validation*/
		
		
		if (pageModel.getTransferType() == null
				|| pageModel.getTransferType().equals("")) {
			feedbackPanel.error("Please select Request Kind");
		}
		
		if (coreTransferDto.getFromConsultantCode() == null
				|| coreTransferDto.getFromConsultantCode().equals(0L)) {
			feedbackPanel.error("Please select From Consultant ");
			return;
		}
		if (coreTransferDto.getToConsultantCode() == null
				|| coreTransferDto.getToConsultantCode().equals(0L)) {
			feedbackPanel.error("Please select To Consultant ");
			return;
		}
		
		CoreConsultantDto fromConsDto = pageModel.getConsultantMap().get(coreTransferDto.getFromConsultantCode());
		CoreConsultantDto toConsDto = pageModel.getConsultantMap().get(coreTransferDto.getToConsultantCode());
		CoreHelper helper = new CoreHelper();
		
		if (!helper.isConsultantActiveStatus(toConsDto)) {
			feedbackPanel.error("To Consutant Status is Inactive ");
		}
		
		if (coreTransferDto.getToConsultantCode() != null
				&& coreTransferDto.getToConsultantCode().equals(
							coreTransferDto.getFromConsultantCode())) {
			feedbackPanel.error("To Consultant and From consultant are same");
		}
		
		if (fromConsDto.getAgreementStatus()==AgreementStatusType.DECLINED
				|| fromConsDto.getAgreementStatus()==AgreementStatusType.IN_PROGRESS) {
			feedbackPanel.error("From Consultant agreement may not have a status of declined or in-progress");
		}
		
		if (fromConsDto.isOrganisation() && CoreHelper.AGREEMENT_KIND_ORGANISATION_SET.contains(fromConsDto.getAgreementKind())) {
			feedbackPanel.error("From consultant for the current channel may not be an organisation");
		}
		
		if (fromConsDto.getCompassCode()==null || fromConsDto.getCompassCode()==0L) {
			feedbackPanel.error("From consultant agreement has no compass code defined");
		}
	
		if (fromConsDto.getAgreementStatus()==AgreementStatusType.DECLINED
				|| fromConsDto.getAgreementStatus()==AgreementStatusType.IN_PROGRESS) {
			feedbackPanel.error("From Consultant agreement may not have a status of declined or in-progress");
		}
		
		if (toConsDto.isOrganisation() && CoreHelper.AGREEMENT_KIND_ORGANISATION_SET.contains(toConsDto.getAgreementKind())) {
			feedbackPanel.error("To consultant for the current channel may not be an organisation");
		}
		
		if (toConsDto.getCompassCode()==null || toConsDto.getCompassCode()==0L) {
			feedbackPanel.error("To consultant agreement has no compass code defined");
		}
		
		if (helper.isBranchCodeExcluded(toConsDto)) {
			feedbackPanel.error("To consultant branch code is specifically excluded and not allowed " + toConsDto.getBranchCode());
		}
		if (helper.isBranchCodeExcluded(fromConsDto)) {
			feedbackPanel.error("From consultant branch code is specifically excluded and not allowed " + fromConsDto.getBranchCode());
		}
	}

	protected ICoreTransferGuiController getGUIController() {
		if (guiController == null) {
			try {
				guiController = ServiceLocator
						.lookupService(ICoreTransferGuiController.class);
			} catch (NamingException e) {
				logger.log(Level.SEVERE,
						"Naming exception looking up Agreement GUI Controller",
						e);
				throw new CommunicationException(
						"Naming exception looking up Agreement GUI Controller",
						e);
			}
		}
		return guiController;
	}
	
	
	
	private void raiseRequests() {
		ISessionUserProfile userProfile = SRSAuthWebSession.get().getSessionUser();
		Long partyOid = pageContextDTO!=null && pageContextDTO.getPartyContextDTO()!=null
							?pageContextDTO.getPartyContextDTO().getPartyOid():null;
		try{					
			getGUIController().raiseCoreTransferRequest(
						userProfile, partyOid , 
						pageModel.getCoreTransferDto().get(0),getAllowableRequestsForBookTransfer());
			
			getSession().info("Record was saved succesfully");
			setResponsePage(new BookTransferPage());
		}catch(ValidationException e){
			for(String error : e.getErrorMessages()){
				feedbackPanel.error(error);
			}
		} catch (DataNotFoundException e) {
			feedbackPanel.error("Data not found when trying to raise a request ");
		} catch (RequestException e) {
			feedbackPanel.error("Could not raise the request " + e.getMessage());
		}

	}
	
	
	private Set<RequestKindType> getAllowableRequestsForBookTransfer() {
		Set<RequestKindType> ret = new HashSet<RequestKindType>();
	
		ret.add(RequestKindType.ProcessBookLevelTransfer);
		return ret;
	}
}
