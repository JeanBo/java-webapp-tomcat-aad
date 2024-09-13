package za.co.liberty.web.pages.reports;

import java.util.List;

import javax.naming.NamingException;

import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;

import za.co.liberty.business.guicontrollers.reports.IReportGUIController;
import za.co.liberty.dto.reports.ChoiceOfSearchEnum;
import za.co.liberty.dto.reports.InfoSlipDocumentDTO;
import za.co.liberty.dto.reports.ReportMaintenanceDTO;
import za.co.liberty.dto.reports.ReportingStatusSearchDTO;
import za.co.liberty.dto.userprofiles.ContextAgreementDTO;
import za.co.liberty.dto.userprofiles.ContextPartyDTO;
import za.co.liberty.exceptions.SystemException;
import za.co.liberty.exceptions.data.DataNotFoundException;
import za.co.liberty.exceptions.security.TabAccessException;
import za.co.liberty.helpers.lookup.ServiceLocator;
import za.co.liberty.web.data.enums.ContextType;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.pages.MaintenanceBasePage;
import za.co.liberty.web.pages.reports.model.MaintainReportsPageModel;

/**
 * Maintain reports generated in SRS (statements)
 * 
 */
public class MaintainReportsPage extends MaintenanceBasePage<Integer> {


	private static final long serialVersionUID = 1L;

	private String pageName = "Reports Maintenance";
	
	private MaintainReportsPageModel pageModel;
	
	private transient IReportGUIController guiController;
	
	/**
	 * 
	 */
	public MaintainReportsPage() {
		this(null);
	}

	/**
	 * @param obj
	 */
	public MaintainReportsPage(Object obj) {
		super(obj);
	}
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see za.co.liberty.web.pages.MaintenanceBasePage#createContainerPanel()
	 */
	@Override
	public Panel createContainerPanel() {
		Panel panel;
		try {
			int[] disabledPanels = null;
			if(this.getEditState() != EditStateType.VIEW){
				disabledPanels = new int[]{};
			}
			FeedbackPanel feedbackPanel = getFeedbackPanel();
			panel = new MaintainReportsPanel(CONTAINER_PANEL_NAME, pageModel,this,
					getEditState(), disabledPanels, feedbackPanel, containerForm);
		} catch (TabAccessException e) {			
			//display message that all tabs have been disabled
			error(e.getUserMessage());
			panel = new EmptyPanel(CONTAINER_PANEL_NAME);
		}				
//		}
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
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see za.co.liberty.web.pages.MaintenanceBasePage#createSelectionPanel()
	 */
	@Override
	public Panel createSelectionPanel() {
		return new EmptyPanel(SELECTION_PANEL_NAME);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see za.co.liberty.web.pages.MaintenanceBasePage#initialisePageModel(java.lang.Object)
	 */
	@Override
	public Object initialisePageModel(Object obj, Integer currentTab) {
		MaintainReportsPageModel pageModel = new MaintainReportsPageModel();
		ContextAgreementDTO agreement = null;
		if (getPageContextDTO()!=null && getPageContextDTO().getAgreementContextDTO()!=null) {
			agreement = getPageContextDTO().getAgreementContextDTO();
		}
		ContextPartyDTO party = null;
		if (getPageContextDTO()!=null) {
			party = getPageContextDTO().getPartyContextDTO();
		}
		
		/**
		 *  Infoslip specific
		 */
		InfoSlipDocumentDTO infoSlipDocumentDTO =  new InfoSlipDocumentDTO();
		if(agreement!= null){
			if(agreement.getSelectedAdvisor() != null){
			infoSlipDocumentDTO.setSelectedAdvisor(agreement.getSelectedAdvisor());
			infoSlipDocumentDTO.setSelectedUserChoiceOfSearch(ChoiceOfSearchEnum.SELECTED_ADVISOR);
			}
			else if(agreement.getAgreementNumber() != null){
			infoSlipDocumentDTO.setSelectedAdvisor(agreement.getAgreementNumber());	
			infoSlipDocumentDTO.setSelectedUserChoiceOfSearch(ChoiceOfSearchEnum.SELECTED_ADVISOR);
			}

			infoSlipDocumentDTO.setServicedAdvisorsList(agreement.getServicedAdvisorsList());
		}
				
		try {
			infoSlipDocumentDTO.setBranchOrUnitMangedByLoggedUser(getGuiController().getBranchOrUnitMangedByLoggedUser());
		} catch (DataNotFoundException e) {
			// Can be ignored
		}
		pageModel.setInfoslipDocDTO(infoSlipDocumentDTO);
		pageModel.setAllPeriodList(getGuiController().getPeriodList());
		
		List<ChoiceOfSearchEnum> list = infoSlipDocumentDTO.getAvailableSearchChoiceList();
		
		// Option only available if an agreement is selected in the context
		if (pageModel.getInfoslipDocDTO().getSelectedAdvisor()!=null) {
			list.add(ChoiceOfSearchEnum.SELECTED_ADVISOR);
		}  
		// Only available to managers / secretaries of branches etc.
		if (pageModel.getInfoslipDocDTO().getBranchOrUnitMangedByLoggedUser().size()>0) {
			list.add(ChoiceOfSearchEnum.BRANCH_OR_UNITNAME);
		}
		// Only available to BC/Franchise
		if (pageModel.getInfoslipDocDTO().getServicedAdvisorsList().size()>0) {
			list.add(ChoiceOfSearchEnum.ALL_SERVICED_ADVISOR);
		}

		
		ReportMaintenanceDTO maintenance = 
			getGuiController().getReportMaintenanceDTOFromAgreementContext(
					agreement,party);
		pageModel.setSelectedItem(maintenance);
		ReportingStatusSearchDTO reportingStatusSearchDTO = new ReportingStatusSearchDTO();
		reportingStatusSearchDTO.setMaxResults(10);
		pageModel.setSearchCriteria(reportingStatusSearchDTO);
		/**
		 * Set & return the model
		 */
		this.pageModel=pageModel;
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
	public ContextType getContextTypeRequired() {
		return ContextType.AGREEMENT;
	}
	

	@Override
	public void doSave_onSubmit() {		
				
	}
	
	private IReportGUIController getGuiController() {
		if (guiController==null) {
			try {
				guiController = ServiceLocator.lookupService(IReportGUIController.class);
			} catch (NamingException e) {
				SystemException exception = new SystemException("Could not load Report GUI Controller", 0, 0);
				exception.initCause(e);
				throw exception;
			}
		}
		return guiController;
	}

}
