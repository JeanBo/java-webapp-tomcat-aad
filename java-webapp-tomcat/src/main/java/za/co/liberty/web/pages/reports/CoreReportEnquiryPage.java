package za.co.liberty.web.pages.reports;

import java.util.ArrayList;
import java.util.List;

import javax.naming.NamingException;

import org.apache.wicket.extensions.ajax.markup.html.tabs.AjaxTabbedPanel;
import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.extensions.markup.html.tabs.TabbedPanel;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

import za.co.liberty.business.guicontrollers.core.ICoreReportGuiController;
import za.co.liberty.business.guicontrollers.request.IRequestEnquiryGuiController;
import za.co.liberty.dto.agreement.request.RequestEnquiryRowDTO;
import za.co.liberty.dto.gui.report.ReportEnquiryDTO;
import za.co.liberty.dto.report.ReportEnquiryRequestDto;
import za.co.liberty.dto.report.ReportEnquiryResponseDto;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.helpers.lookup.ServiceLocator;
import za.co.liberty.interfaces.agreements.requests.RequestKindType;
import za.co.liberty.web.data.enums.ContextType;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.pages.BasePage;
import za.co.liberty.web.pages.report.model.CoreReportEnquiryPageModel;
import za.co.liberty.web.system.SRSAuthWebSession;
import za.co.liberty.web.wicket.markup.html.tabs.CachingTab;

public class CoreReportEnquiryPage extends BasePage{

	
	private static final long serialVersionUID = -3659488245709157662L;

	private transient ICoreReportGuiController guiController;
	
	private static final String COMP_TABBED_PANEL = "tabbedPanel";
	
	@SuppressWarnings("unused")
	private Form pageForm;
	protected TabbedPanel tabbedPanel;
	protected CoreReportEnquiryPageModel pageModel;
	
	/**
	 * Default constructor.
	 *
	 */
	public CoreReportEnquiryPage() {
		pageModel = createPageModel();
		this.add(pageForm=createPageFormField("pageForm"));
		this.setEditState(EditStateType.VIEW, null);
	}
	
	/**
	 * Create the form and 
	 * @param id
	 * @return
	 */
	private Form createPageFormField(String id) {
		Form form = new Form(id);
		form.add(tabbedPanel=createTabbedPanelField(COMP_TABBED_PANEL));
		return form;
	}

	/**
	 * Return an instance to the GuiController bean for this page.
	 * 
	 * @return
	 */
	private ICoreReportGuiController getGuiController() {
		if (guiController==null) {
			try {
				guiController = ServiceLocator.lookupService(ICoreReportGuiController.class);
			} catch (NamingException e) {
				throw new CommunicationException(e);
			}
			
		}
		return guiController;
	}

	/**
	 * Initialise the pageModel
	 * 
	 * @return
	 */
	private CoreReportEnquiryPageModel createPageModel() {
		ICoreReportGuiController controller = getGuiController();
		CoreReportEnquiryPageModel model = new CoreReportEnquiryPageModel();
		
		model.setAllRequestKindTypeList(new ArrayList<RequestKindType>(controller.getAllRequestKindTypeList()));
		model.setAllPeriodList(controller.getPeriodList());
		model.setReportEnquiryResponseList(new ArrayList<ReportEnquiryResponseDto>());
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
		final Model model = new Model(pageModel);
			
			tabList.add(new CachingTab(new Model("Core Report")) {
				private static final long serialVersionUID = -7254885301327316998L;
	
				@Override
				public Panel createPanel(String id) {
					ReportEnquiryDTO dataModel = new ReportEnquiryDTO();
					dataModel.setSearchResultList(new ArrayList<RequestEnquiryRowDTO>());
					pageModel.setDataModel(dataModel, CoreReportEnquiryPanel.class);

					dataModel.setRequestEnquiryPeriod(pageModel.getAllPeriodList().get(0));

					CoreReportEnquiryPanel p =  new CoreReportEnquiryPanel(id, model, getFeedbackPanel(),getGuiController());
					return p;
				}
			});
			
		TabbedPanel panel = new AjaxTabbedPanel(id, tabList);
		return panel;
	}

	@Override
	public String getPageName() {
		return "Report Enquiry";
	}

	@Override
	public ContextType getContextTypeRequired() {
		return ContextType.AGREEMENT_ONLY;
	}
}
