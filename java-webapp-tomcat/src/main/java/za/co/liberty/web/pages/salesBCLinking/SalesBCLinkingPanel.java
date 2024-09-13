package za.co.liberty.web.pages.salesBCLinking;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.naming.NamingException;

import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.ajax.markup.html.form.AjaxFallbackButton;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow.MaskType;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.cycle.RequestCycle;

import com.inmethod.grid.DataProviderAdapter;
import com.inmethod.grid.IGridColumn;
import com.inmethod.grid.SizeUnit;
import com.inmethod.grid.column.PropertyColumn;

import za.co.liberty.agreement.common.exceptions.LogicExecutionException;
import za.co.liberty.business.common.IBusinessUtilitiesBean;
import za.co.liberty.business.guicontrollers.salesBCLinking.ISalesBCLinkingGuiController;
import za.co.liberty.business.request.IRequestEnquiryManagement;
import za.co.liberty.dto.agreement.SalesBCLinking.LinkedAdviserDTO;
import za.co.liberty.dto.agreement.SalesBCLinking.PanelAdviserSearchDTO;
import za.co.liberty.dto.agreement.SalesBCLinking.ServicingPanelDTO;
import za.co.liberty.dto.agreement.request.RequestEnquiryResultDTO;
import za.co.liberty.dto.agreement.request.RequestEnquirySearchDTO;
import za.co.liberty.dto.common.MonthEndDates;
import za.co.liberty.dto.gui.context.AgreementSearchType;
import za.co.liberty.dto.userprofiles.ISessionUserProfile;
import za.co.liberty.exceptions.data.DataNotFoundException;
import za.co.liberty.exceptions.data.ValidationException;
import za.co.liberty.exceptions.error.request.RequestException;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.helpers.lookup.ServiceLocator;
import za.co.liberty.interfaces.agreements.AgreementStatusType;
import za.co.liberty.interfaces.agreements.requests.RequestKindType;
import za.co.liberty.interfaces.agreements.requests.RequestStatusType;
import za.co.liberty.interfaces.party.PartyStatusType;
import za.co.liberty.interfaces.persistence.agreement.request.IRequestEnquiryRow;
import za.co.liberty.interfaces.rating.ci.CIDateType;
import za.co.liberty.srs.type.SRSType;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.data.enums.fields.SalesBCLinkingGUIField;
import za.co.liberty.web.helpers.javascript.DialogScriptBuilder;
import za.co.liberty.web.helpers.javascript.DialogScriptBuilder.DialogType;
import za.co.liberty.web.pages.MaintenanceBasePage;
import za.co.liberty.web.pages.panels.BasePanel;
import za.co.liberty.web.pages.salesBCLinking.model.SalesBCLinkingPageModel;
import za.co.liberty.web.pages.salesBCLinking.model.SalesBCLinkingPanelModel;
import za.co.liberty.web.system.SRSAuthWebSession;
import za.co.liberty.web.wicket.ajax.attributes.SRSAjaxCallListener;
import za.co.liberty.web.wicket.markup.html.form.SRSLabel;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGrid;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGrid.GridSizeUnit;
import za.co.liberty.web.wicket.markup.html.grid.SRSGridRowSelectionCheckBox;
import za.co.liberty.web.wicket.markup.repeater.data.ListDataProvider;
import za.co.liberty.web.wicket.markup.repeater.data.SortableListDataProvider;

@SuppressWarnings("rawtypes")
public class SalesBCLinkingPanel extends BasePanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private SalesBCLinkingPageModel pageModel = null;
	private SalesBCLinkingPanelModel panelModel;
	private EditStateType editState;
	private transient ISalesBCLinkingGuiController guiController = null;
	private MaintenanceBasePage parentPage;
	private Form enclosingForm;
	private ModalWindow modalViewWindow;
	private Button maintainButton;
	private SRSDataGrid adviserSearchGrid;
	private SRSDataGrid currRelationGrid;
	private Button addNewLinkBtn;
	private FeedbackPanel validationFeedBackPanel;
	protected FeedbackPanel linkingFeedBackPanel;
	protected FeedbackPanel currenRelFeedBackPanel;
	protected Page authWindowPage;
	public static final String VIEW_WINDOW_PAGE_MAP = "PANEL_AUTHREQ_WINDOW_PAGE_MAP";
	public static final String VIEW_WINDOW_COOKIE_NAME = "PANEL_AUTHREQ_WINDOW_COOKIE";
	public static final int SELECTION_WIDTH = 300;
	List<String> messageList;

	public SalesBCLinkingPanel(String id, EditStateType editState,
			SalesBCLinkingPageModel page, MaintenanceBasePage parentPage,
			Form enclosingForm ){
			//, FeedbackPanel feedBackPanel) {
		super(id, editState, parentPage);
		pageModel = page;
		messageList = new ArrayList<String>();
		if (pageModel.getSalesBCLinkingPanelModel() != null) {
			panelModel = pageModel.getSalesBCLinkingPanelModel();
		} else {
			panelModel = new SalesBCLinkingPanelModel();
		}
		
		this.editState = editState;
		this.parentPage = parentPage;
		this.enclosingForm = enclosingForm;
		//this.feedBackPanel = feedBackPanel;
		initiate();
//		if (pageModel.isRequestRaised()!= null && pageModel.isRequestRaised()) {
//			pageModel.setRequestRaised(false);
//			SalesBCLinkingPanel.this.info("Record was saved successfully");
//			getCurrLinkingFeedBackPanel().info("Record was saved successfully");
//			if (AjaxRequestTarget.get() != null) {
//				AjaxRequestTarget.get().add(getCurrLinkingFeedBackPanel());
//				((MaintenanceBasePage)parentPage).swapContainerPanel(AjaxRequestTarget.get());
//			}
//			
//			
//			
//		}
	}

	private void initiate() {

		this.add(createBCLinkPanelForm("bcPanelForm"));
	}

	private Form createBCLinkPanelForm(String id) {
		BCPanelForm form = new BCPanelForm(id);
		return form;

	}

	public class BCPanelForm extends Form {
		private static final long serialVersionUID = 1L;

		public BCPanelForm(String id) {
			super(id);

			add(maintainButton = createMaintainButton("maintainBtn"));
			add(createCancelButton("cancelBtn"));
			add(createAuthorisationButton("authBtn"));
			add(modalViewWindow = createModalViewWindow("requestViewWindow"));
			add(createPanelDetailsField("panelName", "panelName"));
			add(createPanelDetailsField("consultantName", "consultantName"));
			add(createPanelDetailsField("consultantCode", "consultantCode"));
			add(createPanelDetailsField("mcccode", "mcccode"));
			add(createPanelDetailsField("serviceType", "serviceType.typeName"));
			add(createPanelDetailsField("branchName", "branchName"));
			add(createPanelDetailsStatusField("status", "statusCode"));
			add(createRelLabelField("currServRelLabel"));

			add(createLabelField("srsAgmt", "SRS Agreement", true));
			add(createLabelField("thirteenDC", "Thirteen Digit Code", true));
			add(createLabelField("or", "Or",true));
			add(createLabelField("servPanelName", "Servicing Panel Name",true));
			add(createLabelField("advSearch", "Create New Servicing Relationships", true));
			add(createPanelResLabelField("panelResult", "Panel Result"));
			
			// Current Servicing Relationship Table
			add(currRelationGrid = createRelTablePanel("adviserRelTablePanel"));
			add(createEndLinkBtn("endLinkBtn", enclosingForm));
			add(createLabelField("endDateLabel", "End Date: ", false));
			add(createEndDateField("endDateField"));
			
			// Adviser Search Area
			add(createAgreementSearchField("agreementSearch", "SRS Agreement"));
			add(createThirteenDigitCodeSearchField("tdcSearch",
					"Thirteen Digit Code"));
			add(createPanelNameSearchField("panelNameSearch",
					"Servicing Panel Name"));
			add(createServicingPanelDDC("searchServicingPanel"));
			add(adviserSearchGrid = createAdviserSearchTablePanel("adviserTable"));
			add(addNewLinkBtn = createAddNewLinkBtn("addNewLinkBtn", enclosingForm));
			add(createLabelField("startDateLabel", "Start Date: ", true));
			add(createStartDateField("startDateField"));			
			add(validationFeedBackPanel = createLinkingFeedbackPanel("feedback"));
			add(linkingFeedBackPanel = createLinkingFeedbackPanel("searchMessages"));
			add(currenRelFeedBackPanel = createCurrentLinkFeedbackPanel("currenRelMessages"));
		}
	}

	private Label createPanelDetailsField(String name, String attributeName) {
		SRSLabel tempSRSLabelField = new SRSLabel(name, new PropertyModel(
				pageModel.getSelectedServicingPanel(), attributeName));
		return tempSRSLabelField;
	}

	private Label createLabelField(String name, String value, boolean isModifyOnly) {
		IModel model;
		final String val = value;
		model = new IModel() {
			private static final long serialVersionUID = 1L;

			public Object getObject() {
				return (val);
			}

			public void setObject(Object arg0) {

			}

			public void detach() {
			}
		};

		SRSLabel tempSRSLabelField = new SRSLabel(name, model);
		tempSRSLabelField
				.setVisible(editState.equals(EditStateType.MODIFY)
						&& pageModel.getSelectedServicingPanel() != null
						&& pageModel.getSelectedServicingPanel()
								.getStatusCode() != null
						&& pageModel.getSelectedServicingPanel()
								.getStatusCode() == PartyStatusType.ACTIVE);
		return tempSRSLabelField;
	}

	private Label createPanelResLabelField(String name, String value) {
		IModel model;
		final String val = value;
		model = new IModel() {
			private static final long serialVersionUID = 1L;

			public Object getObject() {
				return (val);
			}

			public void setObject(Object arg0) {

			}

			public void detach() {
			}
		};

		SRSLabel tempSRSLabelField = new SRSLabel(name, model);
		tempSRSLabelField
				.setVisible(panelModel != null
						&& panelModel.getPanelAdviserSearchDTO() != null
						&& panelModel.getPanelAdviserSearchDTO()
								.getSearchPanelList() != null
						&& panelModel.getPanelAdviserSearchDTO()
								.getSearchPanelList().size() > 1
						&& (!editState.equals(EditStateType.AUTHORISE)));

		return tempSRSLabelField;
	}
	
	private FeedbackPanel createLinkingFeedbackPanel(String id) {
		FeedbackPanel feedbackPnl = new FeedbackPanel(id);

		feedbackPnl.setEnabled(editState == EditStateType.MODIFY);
		feedbackPnl.setVisible(editState == EditStateType.MODIFY);
		feedbackPnl.setOutputMarkupId(true);

		return feedbackPnl;
	}
	
	private FeedbackPanel createCurrentLinkFeedbackPanel(String id) {
		FeedbackPanel feedbackPnl = new FeedbackPanel(id);
		feedbackPnl.setOutputMarkupId(true);
		feedbackPnl.setEnabled(editState == EditStateType.VIEW);
		feedbackPnl.setVisible(editState == EditStateType.VIEW);

		return feedbackPnl;
	}

	private Label createRelLabelField(String name) {
		IModel model;
		if (editState == EditStateType.AUTHORISE) {
			model = new IModel() {
				private static final long serialVersionUID = 1L;

				public Object getObject() {
					return ("Servicing Relationships");
				}

				public void setObject(Object arg0) {

				}

				public void detach() {
				}
			};
		} else {
			model = new IModel() {
				private static final long serialVersionUID = 1L;

				public Object getObject() {
					return ("Current Servicing Relationships");
				}

				public void setObject(Object arg0) {

				}

				public void detach() {
				}
			};
		}

		SRSLabel tempSRSLabelField = new SRSLabel(name, model);
		return tempSRSLabelField;
	}

	@SuppressWarnings("serial")
	private SRSDataGrid createAdviserSearchTablePanel(String id) {
		List<LinkedAdviserDTO> dispList = panelModel.getSearchAdvisersList();
		boolean diffRegion = false;
		boolean raisedReq = false;
		boolean statusrestriction = false;
		boolean noSalesCatRestriction = false;
		boolean salesCatRestriction = false;
		boolean currPanelRestriction = false;
		boolean supportToSalesLink = false;
		boolean futureDatedLink = false;
		Calendar today = Calendar.getInstance();
		Calendar endOfTime = Calendar.getInstance();

		// set Date portion to December 31st, 9999
		endOfTime.set(endOfTime.YEAR, 9999);
		endOfTime.set(endOfTime.MONTH, endOfTime.DECEMBER);
		endOfTime.set(endOfTime.DATE, 31);
		
		if (dispList != null && dispList.size() > 1) {
			Collections.sort(dispList, new Comparator<LinkedAdviserDTO>() {
				public int compare(LinkedAdviserDTO s1, LinkedAdviserDTO s2) {
					if (s1.getAdviserName() == null && s2.getAdviserName() == null) {
						return -1;
					} else if (s1.getAdviserName() != null && s2.getAdviserName() == null) {
						return -1;
					} else if (s1.getAdviserName() == null && s2.getAdviserName() != null) {
						return 1;
					} else {
					return s1.getAdviserName().compareToIgnoreCase(
							s2.getAdviserName());
					}
				}
			});
		}
				
		long panelRegionPartyOID = -1;
		/*Test if the selected adviser belongs to a separate region to display warning.*/
		if (pageModel.getSelectedServicingPanel() != null
				&& pageModel.getSelectedServicingPanel().getPanelOID() != null
				&& dispList != null && dispList.size() > 0) {
			panelRegionPartyOID = getGUIController().getRegionPartyOID(
					pageModel.getSelectedBranch().getOid());
		}

		/*
		 * If the list contains advisers who have outstanding request against it,
		 * disable the select and display an informational message.
		 */
		List<LinkedAdviserDTO> nonSelectionList = new ArrayList<LinkedAdviserDTO>();
		for (LinkedAdviserDTO adv : dispList) {

			long advRegionPartyOID = getGUIController().getRegionPartyOID(
					adv.getBranchOID());

			if (panelRegionPartyOID != -1
					&& panelRegionPartyOID != advRegionPartyOID) {
				diffRegion = true;
//				SalesBCLinkingPanel.this
//						.warn("The selected context panel and the adviser chosen to be linked do not belong to the same region.");
//				if (AjaxRequestTarget.get() != null) {
//					AjaxRequestTarget.get().add(getFeedBackPanel());
//				}
			}

			RequestEnquirySearchDTO searchDto = new RequestEnquirySearchDTO();
			searchDto.setAgreementIdList(Arrays.asList(new Long[] { adv
					.getAgreementNumber() }));

			/* Form the request kind list */
			List<RequestKindType> reqKindList = new ArrayList<RequestKindType>();
			reqKindList
					.add(RequestKindType.MaintainCrossRegionServicingRelationships);
			reqKindList
					.add(RequestKindType.MaintainSingleRegionServicingRelationships);
			searchDto.setRequestKindList(reqKindList);

			// searchDto.setRequestStatus(requestStatus)
			try {
				RequestEnquiryResultDTO result = getRequestEnqManagement()
						.findRequests(searchDto);
				// There can only be one
				if (result != null && result.getResultList() != null
						&& result.getResultList().size() > 0) {

					for (IRequestEnquiryRow row : result.getResultList()) {
						if (row != null
								&& (row.getStatus() == RequestStatusType.RAISED
										.getSpecId() || row.getStatus() == RequestStatusType.REQUIRES_AUTHORISATION
										.getSpecId())) {
							/*
							 * If there is a raised request or request needing
							 * authorization against the searched adviser.
							 */
							raisedReq = true;
							if (!nonSelectionList.contains(adv)) {
								nonSelectionList.add(adv);
							}
						}
					}

				}

			} catch (Exception e) {
				SalesBCLinkingPanel.this
				.error("Error encountered while fetching Adviser details. Please try again.");
			}
			
			List<String> agreementStatusTypes = new ArrayList<String>();
			agreementStatusTypes.add(AgreementStatusType.ACTIVE
										.getDescription().toLowerCase());
			agreementStatusTypes.add(AgreementStatusType.BROKER_ACTIVE
					.getDescription().toLowerCase());
			agreementStatusTypes.add(AgreementStatusType.CLOSED_TO_BN
					.getDescription().toLowerCase());
			agreementStatusTypes.add(AgreementStatusType.BROKER_CLOSED_TO_BN
					.getDescription().toLowerCase());
			agreementStatusTypes.add(AgreementStatusType.FRANCHISE_MANAGER_CLOSED_TO_BN
					.getDescription().toLowerCase());
			
			
			try {
				if ((adv.getAdviserStatus() != null)
						&& (!agreementStatusTypes.contains(adv
								.getAdviserStatus().toLowerCase()))) {
					if (!nonSelectionList.contains(adv)) {
						nonSelectionList.add(adv);	
					}
					
					statusrestriction = true;
					
				}
			} catch (Exception e) {
				SalesBCLinkingPanel.this
				.error("Error encountered while fetching Adviser details. Please try again.");
			}
			
			
			/*Check if the adviser belongs to a support panel but is being linked to a sales panel (i.e. panel in context is a Sales panel)*/
			try {
				if ((adv.getServiceType() != null)
						&& (adv.getServiceType().getTypeID() == SRSType.SUPPORT_SERVICING_PANEL)
						&& pageModel.getSelectedServicingPanel()!=null && pageModel.getSelectedServicingPanel().getServiceType().getTypeID() == SRSType.SALES_SERVICING_PANEL) {
					/*
					 * Taking off validation. Adviser linked to the Support
					 * panel can now be linked to a Sales panel. Validation
					 * taken off post (Market Integration) project implementation.
					 */
//					if (!nonSelectionList.contains(adv)) {
//						nonSelectionList.add(adv);	
//					}
					
					supportToSalesLink = true;
					
				}
			} catch (Exception e) {
				SalesBCLinkingPanel.this
				.error("Error encountered while fetching Adviser details. Please try again.");
			}
			
			
			/*Check if the adviser is linked to the current panel*/
			List<Long> panelOIDs = getGUIController().getAdviserPanels(adv.getAdviserOID());
			
			if (panelOIDs != null
					&& panelOIDs.contains(pageModel
							.getSelectedServicingPanel()
							.getConsultantCode())) {
				currPanelRestriction = true;
				if (!nonSelectionList.contains(adv)) {
					nonSelectionList.add(adv);	
				}
				
			}
			
			/*
			 * Test to check if the Sales Category of the adviser is an
			 * allowed value.
			 */
			if (!getGUIController().isAdviserSalesCatAllowed(
					adv.getAdviserSalesCat())) {

				if (adv.getAdviserSalesCat() == null) {
					
					if (!nonSelectionList.contains(adv)) {
						nonSelectionList.add(adv);	
					}
					
					noSalesCatRestriction = true;
				} else {
					if (!nonSelectionList.contains(adv)) {
						nonSelectionList.add(adv);	
					}
					
					salesCatRestriction = true;
					
				}
			}	
			
			if (adv.getStartDate() != null
					&& adv.getStartDate().after(today.getTime())) {
				/* check if the current panel type is same as the adviser panel */
				if ((adv.getServiceType() != null
						&& adv.getServiceType().getTypeID() != null
						&& pageModel.getSelectedServicingPanel() != null
						&& pageModel.getSelectedServicingPanel()
								.getServiceType() != null && pageModel
						.getSelectedServicingPanel().getServiceType()
						.getTypeID() != null)
						&& (adv.getServiceType().getTypeID().equals(pageModel
								.getSelectedServicingPanel().getServiceType()
								.getTypeID()))) {
					if (!nonSelectionList.contains(adv)) {
						nonSelectionList.add(adv);
					}
					futureDatedLink = true;
				}

			}
			
		}
		
		
		
		final boolean regionWarn =  diffRegion;
		final boolean statusWarn =  statusrestriction;	
		final boolean reqWarn =  raisedReq;
		final boolean noSalesCatWarn = noSalesCatRestriction;
		final boolean salesCatWarn = salesCatRestriction;
		final boolean currPanelWarn = currPanelRestriction;
		final boolean supportToSalesLinkWarn = supportToSalesLink;
		final boolean futureDatedLinkWarn = futureDatedLink;
		
		SRSDataGrid grid = new SRSDataGrid(id, new DataProviderAdapter(
				new SortableListDataProvider<LinkedAdviserDTO>(dispList)),
				createInternalTableFieldColumns("adviserTable"), null,nonSelectionList) {
			@Override
			protected void onBeforeRender() {
				if (reqWarn) {
			
					// If there are advisers (one or more) that have outstanding
					// requests, display a warning message.
					SalesBCLinkingPanel.this
							.info("One or more Advisers have outstanding requests needing authorisation. These Advisers cannot be linked to the panel.");
					//jzb0608 
//					AjaxRequestTarget target = RequestCycle.get().find(AjaxRequestTarget.class);
//					if (target!=null) {
//						target.add(getLinkingFeedBackPanel());
//						getValidationFeedbackPanel().setVisible(false);
//						getValidationFeedbackPanel().setOutputMarkupId(false);
//						
//					}
				}
				
				if (supportToSalesLinkWarn) {

					/*
					 * If there are advisers (one or more) that are currently
					 * linked to a Support Panel, they cannot be linked to a
					 * Sales Servicing panel.
					 */
					SalesBCLinkingPanel.this
							.info("One or more Advisers are linked to a Support Panel.");
					//jzb0608 
//					AjaxRequestTarget target = RequestCycle.get().find(AjaxRequestTarget.class);
//					if (target!=null) {
//						target.add(getLinkingFeedBackPanel());
//						getValidationFeedbackPanel().setVisible(false);
//						getValidationFeedbackPanel().setOutputMarkupId(false);
//					}
				}
				if (regionWarn) {
					// If there are advisers belonging to regions outside the
					// current logged in user, display a warning message.
					SalesBCLinkingPanel.this
							.warn("The selected context panel and the adviser chosen to be linked do not belong to the same region.");
					//jzb0608 
//					AjaxRequestTarget target = RequestCycle.get().find(AjaxRequestTarget.class);
//					if (target!=null) {
//						target.add(getLinkingFeedBackPanel());
//						getValidationFeedbackPanel().setVisible(false);
//						getValidationFeedbackPanel().setOutputMarkupId(false);
//					}

				}

				if (currPanelWarn) {
					SalesBCLinkingPanel.this
							.warn("One or more Advisers is already linked to the panel.");
					//jzb0608 
//					AjaxRequestTarget target = RequestCycle.get().find(AjaxRequestTarget.class);
//					if (target!=null) {
//						target.add(
//								getLinkingFeedBackPanel());
//						getValidationFeedbackPanel().setVisible(false);
//						getValidationFeedbackPanel().setOutputMarkupId(false);
//					}
				}

				if (statusWarn) {
					// If the advisers are not status Active/ClosedToNB, display
					// a warning message indicating the advisers cannot be
					// selected.
					getLinkingFeedBackPanel()
							.warn("One or more Advisers are not in the status Active/ClosedToNB. These Advisers cannot be linked to the panel.");
					getValidationFeedbackPanel().setVisible(false);
					getValidationFeedbackPanel().setOutputMarkupId(false);
					//jzb0608 
//					AjaxRequestTarget target = RequestCycle.get().find(AjaxRequestTarget.class);
//					if (target!=null) {
//						//jzb0608 target.add(getLinkingFeedBackPanel());
//						getValidationFeedbackPanel().setVisible(false);
//						getValidationFeedbackPanel().setOutputMarkupId(false);
//					}
				}
				
				if (salesCatWarn) {
					// If there are advisers belonging to regions outside the
					// current logged in user, display a warning message.
					SalesBCLinkingPanel.this
							.warn("One or more Advisers do not belong to the Sales Category that can be linked to a panel.");

//					AjaxRequestTarget target = RequestCycle.get().find(AjaxRequestTarget.class);
//					if (target!=null) {
//						//jzb0608 target.add(getLinkingFeedBackPanel());
//						getValidationFeedbackPanel().setVisible(false);
//						getValidationFeedbackPanel().setOutputMarkupId(false);
//					}

				}
				
				if (noSalesCatWarn) {
					// If there are advisers belonging to regions outside the
					// current logged in user, display a warning message.
					SalesBCLinkingPanel.this
							.warn("One or more Advisers does not contain a Sales Category. These Advisers cannot be linked to the panel.");

//					AjaxRequestTarget target = RequestCycle.get().find(AjaxRequestTarget.class);
//					if (target!=null) {
////jzb0608						target.add(getLinkingFeedBackPanel());
//						getValidationFeedbackPanel().setVisible(false);
//						getValidationFeedbackPanel().setOutputMarkupId(false);
//					}

				}
				
				if (futureDatedLinkWarn) {
					
					/*
					 * If there are advisers that have the link start date set
					 * to a future date, these advisers cannot be selected in
					 * the grid as their links cannot be ended.
					 */
					SalesBCLinkingPanel.this
							.info("One or more Advisers have future dated links to a panel. These Advisers cannot be selected.");
//					AjaxRequestTarget target = RequestCycle.get().find(AjaxRequestTarget.class);
//					if (target!=null) {
//						target.add(
//								getLinkingFeedBackPanel());
//						getValidationFeedbackPanel().setVisible(false);
//						getValidationFeedbackPanel().setOutputMarkupId(false);
//					}
				}


				super.onBeforeRender();
			}
			
			@Override
			public void selectItem(IModel itemModel, boolean selected) {
				// allowing max 50 requests at a time for timeouts
				// SalesBCLinkingPanel.this.getSession().cleanupFeedbackMessages();
				super.selectItem(itemModel, selected);
				// if (getSelectionCount() > 50) {
				// error("You have selected "
				// + selectionCount
				// + " advisers. A max of "
				// +
				// HierarchyGUIController.MAX_SIMULTANEOUS_BRANCH_TRANSFER_REQUESTS
				// + " is allowed, please unselect "
				// + (selectionCount -
				// HierarchyGUIController.MAX_SIMULTANEOUS_BRANCH_TRANSFER_REQUESTS)
				// + " to proceed");
				// }
				// AjaxRequestTarget.get().addComponent(feedBackPanel);
			}
		};

		grid.setVisible(editState.equals(EditStateType.MODIFY)
				&& pageModel.getSelectedServicingPanel() != null
				&& pageModel.getSelectedServicingPanel().getStatusCode() != null
				&& pageModel.getSelectedServicingPanel().getStatusCode() == PartyStatusType.ACTIVE);
		grid.setAutoResize(true);
		grid.setOutputMarkupId(true);
		grid.setCleanSelectionOnPageChange(false);
		grid.setClickRowToSelect(false);
		grid.setAllowSelectMultiple(true);
		grid.setGridWidth(98, GridSizeUnit.PERCENTAGE);
		grid.setRowsPerPage(50);
		grid.setContentHeight(300, SizeUnit.PX);

		return grid;
	}

	/**
	 * A generic cancel button that invalidates the page
	 * 
	 * @param id
	 * @return
	 */
	protected Button createCancelButton(String id) {
		Button button = new AjaxFallbackButton(id, enclosingForm) {

			private static final long serialVersionUID = 1L;

//			@Override
//			protected IAjaxCallDecorator getAjaxCallDecorator() {
//				return new AjaxCallDecorator() {
//					private static final long serialVersionUID = 1L;
//
//					public CharSequence decorateScript(CharSequence script) {
//						return "this.disabled=true;overlay(true);" + script;
//					}
//				};
//			}
			
			@Override
			protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
				super.updateAjaxAttributes(attributes);
			        
			        // SRS Convenience method for overLay hiding/showing
			        attributes.getAjaxCallListeners().add(new SRSAjaxCallListener(true));
			}

			@Override
			protected void onComponentTag(ComponentTag tag) {
				super.onComponentTag(tag);
				tag.getAttributes().put("value", "Cancel");
				tag.getAttributes().put("type", "submit");
			}

			@Override
			protected void onSubmit(AjaxRequestTarget arg0, Form arg1) {
				do_cancel(arg0);
			}

		};

		button.setEnabled((editState != null) && !(editState.isViewOnly()));
		button.setOutputMarkupId(true);
		button.setDefaultFormProcessing(false);
		button.setVisible(!editState.equals(EditStateType.AUTHORISE));
		return button;
	}

	/**
	 * A generic cancel button that invalidates the page
	 * 
	 * @param id
	 * @return
	 */
	protected Button createAuthorisationButton(String id) {
		Button button = new AjaxFallbackButton(id, enclosingForm) {

			private static final long serialVersionUID = 1L;

			@Override
			protected void onComponentTag(ComponentTag tag) {
				super.onComponentTag(tag);
				tag.getAttributes().put("value", "Authorisation");
				tag.getAttributes().put("type", "submit");
			}

			@Override
			protected void onSubmit(AjaxRequestTarget arg0, Form arg1) {
				if (editState != EditStateType.VIEW) {
					// Show a warning dialog and cancel the action
					arg0.appendJavaScript(DialogScriptBuilder.buildShowDialog(
							DialogType.WARNING,
							"Please save or cancel the current action."));
					arg0.appendJavaScript(DialogScriptBuilder
							.buildReturnValue(false));
					return;
				}

				if (parentPage.hasModifyAccess()) {
					modalViewWindow.show(arg0);
				} else {
					SalesBCLinkingPanel.this
							.error("You do not have access to the Authorise outstanding BC Linking Requests, please consult support if you need access.");
					arg0.add(getCurrLinkingFeedBackPanel());

				}
			}
		};
		button.setEnabled(parentPage!= null && parentPage.hasModifyAccess());
		button.setOutputMarkupId(true);
		button.setDefaultFormProcessing(false);
		button.setVisible(!editState.equals(EditStateType.AUTHORISE));
		return button;
	}

	/**
	 * Create the modify button
	 * 
	 * @param id
	 * @return
	 */
	protected Button createMaintainButton(String id) {
		Button button = new AjaxFallbackButton(id, enclosingForm) {

			private static final long serialVersionUID = -5330766713711809772L;

			@Override
			protected void onComponentTag(ComponentTag tag) {
				super.onComponentTag(tag);
				tag.getAttributes().put("value", "Maintain");
				tag.getAttributes().put("type", "submit");
			}

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form form) {
				// Clear session messages if any
				SRSAuthWebSession.get().getFeedbackMessages().clear();
				doMaintain_onSubmit(target, form);
			}
		};

		boolean allValuesSet = pageModel.getSelectedBranch() != null
				&& pageModel.getSelectedServicingPanel() != null
				&& pageModel.getSelectedServicingPanel().getBranchOID() != null
				&& !(parentPage.getEditState() == EditStateType.MODIFY);

		/*
		 * The maintain button is enabled when the Branch,ServiceType and
		 * ServicingPanel is selected and the Cancel button is not enabled. If
		 * the Cancel button is enabled - it is concluded that the
		 * screen/context object is currently being maintained.
		 */
		button.setEnabled(allValuesSet && parentPage.hasModifyAccess());
		button.setVisible(!editState.equals(EditStateType.AUTHORISE));
		button.setOutputMarkupId(true);
		return button;
	}

	private Label createPanelDetailsStatusField(String name,
			String attributeName) {
		Model<String> strMdl;
		PartyStatusType status = pageModel.getSelectedServicingPanel()
				.getStatusCode();
		if (status == null) {
			strMdl = Model.of("");
		} else {
			strMdl = Model.of(status.getDescription());
		}

		SRSLabel tempSRSLabelField = new SRSLabel(name, strMdl);
		return tempSRSLabelField;
	}

	@SuppressWarnings("unchecked")
	protected DropDownChoice createServicingPanelDDC(String id) {
		IModel model = new IModel() {
			private static final long serialVersionUID = 1L;

			public Object getObject() {
				return (((ServicingPanelDTO) panelModel
						.getPanelAdviserSearchDTO().getSearchPanelDetails()) != null ? ((ServicingPanelDTO) panelModel
						.getPanelAdviserSearchDTO().getSearchPanelDetails())
						: new ServicingPanelDTO());
			}

			public void setObject(Object arg0) {
				panelModel.getPanelAdviserSearchDTO().setSearchPanelDetails(
						(ServicingPanelDTO) arg0);
			}

			public void detach() {
			}
		};

		List<ServicingPanelDTO> dispList = panelModel.getPanelAdviserSearchDTO()
				.getSearchPanelList();

		if (dispList != null && dispList.size() > 1) {
			Collections.sort(dispList, new Comparator<ServicingPanelDTO>() {
				public int compare(ServicingPanelDTO s1, ServicingPanelDTO s2) {
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

		DropDownChoice servicingPanelDDC = new DropDownChoice(id, model,
				dispList, new ChoiceRenderer<ServicingPanelDTO>("panelName",
						"panelOID")) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onComponentTag(ComponentTag tag) {
				super.onComponentTag(tag);
				decorateStyleOnTag(tag);
			}

		};
		servicingPanelDDC.setOutputMarkupId(true);
		/* Add select behavior */
		servicingPanelDDC
				.add(new AjaxFormComponentUpdatingBehavior("change") {
					private static final long serialVersionUID = 0L;

					@Override
					protected void onUpdate(AjaxRequestTarget target) {
						List<LinkedAdviserDTO> adviserList = new ArrayList<LinkedAdviserDTO>();
						/* Find and Display the advisers in the adviser grid */
						if (panelModel.getPanelAdviserSearchDTO()
								.getSearchPanelDetails() != null
								&& panelModel.getPanelAdviserSearchDTO()
										.getSearchPanelDetails().getPanelOID() != null) {

							try {
								adviserList = getGUIController()
										.findAdviserDetailsWithPanelOid(
												panelModel
														.getPanelAdviserSearchDTO()
														.getSearchPanelDetails()
														.getPanelOID());
								
								if (adviserList == null || adviserList.size() <1) {
									SalesBCLinkingPanel.this.info("The panel "
											+ panelModel
											.getPanelAdviserSearchDTO()
											.getSearchPanelDetails()
											.getPanelName()
											+ " does not contain advisers.");
								}
							} catch (CommunicationException e) {
								SalesBCLinkingPanel.this
										.error("Error encountered while fetching panel details. Please try again.");
								target.add(getCurrLinkingFeedBackPanel());
							} catch (DataNotFoundException e) {
								SalesBCLinkingPanel.this
										.error("Error encountered while fetching panel details. Please try again.");
								target.add(getCurrLinkingFeedBackPanel());
							} catch (NamingException e) {
								SalesBCLinkingPanel.this
										.error("Error encountered while fetching panel details. Please try again.");
								target.add(getCurrLinkingFeedBackPanel());
							}
							
							panelModel.setSearchAdvisersList(adviserList);
							parentPage.swapContainerPanel(target);
						}
					}
				});
		servicingPanelDDC.setLabel(new Model(
				SalesBCLinkingGUIField.SEARCH_SERVICING_PANEL.getName()));

		servicingPanelDDC.setEnabled(panelModel.getPanelAdviserSearchDTO()
				.getSearchPanelList() != null
				&& panelModel.getPanelAdviserSearchDTO()
						.getSearchPanelList().size() > 1);

		servicingPanelDDC.setVisible(panelModel.getPanelAdviserSearchDTO()
				.getSearchPanelList() != null
				&& panelModel.getPanelAdviserSearchDTO()
						.getSearchPanelList().size() > 1
				&& (editState.equals(EditStateType.MODIFY)
						&& pageModel.getSelectedServicingPanel() != null
						&& pageModel.getSelectedServicingPanel().getStatusCode() != null
						&& pageModel.getSelectedServicingPanel().getStatusCode() == PartyStatusType.ACTIVE));

		return servicingPanelDDC;
	}

	/**
	 * Create the Add New Link button
	 * 
	 * @param id
	 * @return
	 */
	private Button createAddNewLinkBtn(String id, Form form) {
		Button button = new AjaxButton(id, form) {

			private static final long serialVersionUID = 1L;

			@Override
			protected void onComponentTag(ComponentTag tag) {
				super.onComponentTag(tag);
			}

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form form) {

				/*
				 * Ensure that at least one adviser is selected from the Adviser
				 * Table
				 */

				List<Object> adviserSelections = adviserSearchGrid
						.getSelectedItemObjects();

				/*
				 * Get all selected rows and set it in the selected Servicing
				 * Panel
				 */
				if (adviserSelections == null || adviserSelections.size() <= 0) {

					SalesBCLinkingPanel.this
							.error("Please select one or more advisers to link.");
					getValidationFeedbackPanel().setVisible(false);
					getValidationFeedbackPanel().setOutputMarkupId(false);
					target.add(getLinkingFeedBackPanel());
					return;

				}
				
				/*
				 * Check if the Start Date is set
				 */
				if (panelModel == null || panelModel.getNewStartDate() == null) {

					SalesBCLinkingPanel.this
							.error("Please select the Start Date to create the new relationship.");
					getValidationFeedbackPanel().setVisible(false);
					getValidationFeedbackPanel().setOutputMarkupId(false);
					target.add(getLinkingFeedBackPanel());
					return;

				}

				List<LinkedAdviserDTO> advList = new ArrayList<LinkedAdviserDTO>();
				for (Object selection : adviserSelections) {
					LinkedAdviserDTO advDTO = (LinkedAdviserDTO) selection;
					advDTO.setNewStartDate(panelModel.getNewStartDate());
					
					/*Add the BranchTo and branchFrom information*/
					advDTO.setBranchToOID(pageModel.getSelectedServicingPanel()
							.getBranchOID());
					
					advList.add(advDTO);
				}
				panelModel.setNewLinkAdvisersList(advList);

				// If the advisers from the same panel are being linked to the
				// selected panel, throw an error
				/*
				 * Check if the Start Date is set
				 */
				try {
					raiseRequests(target);

				} catch (Exception e) {
					SalesBCLinkingPanel.this
							.error(e.getMessage());
					getValidationFeedbackPanel().setVisible(false);
					getValidationFeedbackPanel().setOutputMarkupId(false);
					target.add(getLinkingFeedBackPanel());
					
					SalesBCLinkingPageModel newModel = pageModel;
					List<LinkedAdviserDTO> refreshedAdvList = getGUIController().getAdviserDetails(pageModel.getSelectedServicingPanel());
					newModel.getSelectedServicingPanel().setLinkedAdvisersList(refreshedAdvList);
					
					panelModel = new SalesBCLinkingPanelModel();
					newModel.setSalesBCLinkingPanelModel(panelModel);
					
					target.add(adviserSearchGrid);
					
					return;
				}
				
				
				//target.add(getFeedBackPanel());
				//messageList.clear();
				
				/*Refresh the Grid that where we display adviser information*/
				// Refresh the Grid
				editState = EditStateType.VIEW;
				
//				SRSDataGrid tmpGrid = createRelTablePanel("adviserRelTablePanel");
//				currRelationGrid.replaceWith(tmpGrid);
//				currRelationGrid = tmpGrid;
//				target.add(currRelationGrid);
//				
				SalesBCLinkingPageModel newModel = pageModel;
				List<LinkedAdviserDTO> refreshedAdvList = getGUIController().getAdviserDetails(pageModel.getSelectedServicingPanel());
				newModel.getSelectedServicingPanel().setLinkedAdvisersList(refreshedAdvList);
				
				panelModel = new SalesBCLinkingPanelModel();
				newModel.setSalesBCLinkingPanelModel(panelModel);
				//newModel.setRequestRaised(true);
				target.add(adviserSearchGrid);
				
				//newModel.setRequestRaised(true);				
				setResponsePage(new SalesBCLinkingPage(newModel));
			}
			
//			@Override
//			protected IAjaxCallDecorator getAjaxCallDecorator() {
//				return new AjaxCallDecorator() {
//					private static final long serialVersionUID = 1L;						
//					public CharSequence decorateScript(CharSequence script) {
//						//disable cancel too		
////						String disableAddLinkBtn = "";
////						if(addNewLinkBtn != null){
////							disableAddLinkBtn = "getElementById('"+addNewLinkBtn.getMarkupId()+"').disabled=true;";
////						}
//						return "overlay(true);" + script;
//					}
//				};
//			}
			
			@Override
			protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
				super.updateAjaxAttributes(attributes);
			        
			        // SRS Convenience method for overLay hiding/showing
			        attributes.getAjaxCallListeners().add(new SRSAjaxCallListener());
			}
		};
		
		/*
		 * Button is enabled when the EditState is not VIEW. The new End Date is
		 * set in the new End Date drop down and there are items selected in the
		 * grid.
		 */
		if (panelModel.getSearchAdvisersList() != null
				&& panelModel.getSearchAdvisersList().size() > 0) {
			button.setEnabled(true);
		} else {
			button.setEnabled(false);
		}

		button.setOutputMarkupId(true);
		button.setOutputMarkupPlaceholderTag(true);
		button.setVisible(editState.equals(EditStateType.MODIFY)
				&& pageModel.getSelectedServicingPanel() != null
				&& pageModel.getSelectedServicingPanel().getStatusCode() != null
				&& pageModel.getSelectedServicingPanel().getStatusCode() == PartyStatusType.ACTIVE);
		return button;
	}

	private void raiseRequests(AjaxRequestTarget target) throws ValidationException, RequestException {
		/*------ Raise the request ------*/
		ISessionUserProfile userProfile = SRSAuthWebSession.get()
				.getSessionUser();
		try {
			int requestKind = getGUIController()
					.raiseAdviserLinkingRequest(userProfile,
							pageModel.getSelectedServicingPanel(),
							panelModel.getNewLinkAdvisersList(),
							getAllowableRequests());
			getSession().info("Record was saved successfully");
			if (requestKind == RequestKindType.MaintainCrossRegionServicingRelationships
					.getRequestKind()) {
				getSession()
						.info("The request "
								+ RequestKindType.MaintainCrossRegionServicingRelationships
										.getDescription()
								+ " raised successfully. Request needs authorisation.");
			}
			
			target.add(getCurrLinkingFeedBackPanel());

		} catch (ValidationException e) {
			throw e;
		} catch (RequestException e) {
			throw e;
			
		}
	}
	/**
	 * Create the Add New Link button
	 * 
	 * @param id
	 * @return
	 */
	private Button createEndLinkBtn(String id, Form form) {
		Button button = new AjaxButton(id, form) {

			private static final long serialVersionUID = 1L;

			@Override
			protected void onComponentTag(ComponentTag tag) {
				super.onComponentTag(tag);
			}

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form form) {

				/*
				 * Ensure that the End date is set in the page model object. set
				 * the nature of the request by setting isEndLink to true.
				 */
				if (panelModel.getEndDate()!= null) {
					ServicingPanelDTO selectedPanel = pageModel.getSelectedServicingPanel();
					
					selectedPanel.setEndLinkDate(panelModel.getEndDate());
					
					selectedPanel.setIsEndLinkAction(true);
					// Is this correct?
					pageModel.setSelectedServicingPanel(selectedPanel);
				} else {
					/*
					 * Check if the Start Date is set
					 */
					SalesBCLinkingPanel.this
								.error("Please select an End Date to end the link.");
//					getCurrLinkingFeedBackPanel().setEnabled(true);
//					getCurrLinkingFeedBackPanel().setVisible(true);
					getValidationFeedbackPanel().setVisible(true);
					getValidationFeedbackPanel().setOutputMarkupId(true);
					target.add(getValidationFeedbackPanel());
						return;
				}
				
				/*
				 * Ensure that at least one adviser is selected from the Adviser
				 * Table
				 */

				List<Object> adviserSelections = currRelationGrid
						.getSelectedItemObjects();

				/*
				 * Get all selected rows and set it in the selected Servicing
				 * Panel
				 * 
				 * Set all the advisers to be de-linked to the newAdvisersList
				 */
				if (adviserSelections == null || adviserSelections.size() <= 0) {

					SalesBCLinkingPanel.this
							.error("Please select one or more advisers.");
//					currenRelFeedBackPanel.setEnabled(true);
//					currenRelFeedBackPanel.setVisible(true);
//					currenRelFeedBackPanel.info("Please select one or more advisers.");
//					currenRelFeedBackPanel.setOutputMarkupId(true);
//					FeedbackPanel pnl = new FeedbackPanel("feedback");
//					pnl.setVisible(true);
//					pnl.setOutputMarkupId(true);
//					this.add(pnl);
					getValidationFeedbackPanel().setVisible(true);
					getValidationFeedbackPanel().setOutputMarkupId(true);
					target.add(getValidationFeedbackPanel());
					
					return;

				}

				List<LinkedAdviserDTO> advList = new ArrayList<LinkedAdviserDTO>();
				for (Object selection : adviserSelections) {
					LinkedAdviserDTO advDTO = (LinkedAdviserDTO) selection;
					advList.add(advDTO);
				}
				panelModel.setNewLinkAdvisersList(advList);
				
				/*---- Raise the request ----*/

				ISessionUserProfile userProfile = SRSAuthWebSession.get()
						.getSessionUser();
				try {
					getGUIController()
							.raiseAdviserLinkingRequest(userProfile,
									pageModel.getSelectedServicingPanel(),
									panelModel.getNewLinkAdvisersList(),
									getAllowableRequests());
					
					/*Refresh the Grid that where we display adviser information*/
					// Refresh the Grid
					editState = EditStateType.VIEW;
					
					SalesBCLinkingPageModel newModel = pageModel;
					List<LinkedAdviserDTO> refreshedAdvList = getGUIController().getAdviserDetails(pageModel.getSelectedServicingPanel());
					newModel.getSelectedServicingPanel().setLinkedAdvisersList(refreshedAdvList);
					//newModel.setRequestRaised(true);
					getSession().info("Record was saved successfully");
					target.add(getCurrLinkingFeedBackPanel());
					/*
					 * We do not want to set the response page as this reinitializes the
					 * page and all user selections are lost.
					 */
					setResponsePage(new SalesBCLinkingPage(newModel));
					
					
				} catch (ValidationException e) {
					editState = EditStateType.VIEW;
					for (String error : e.getErrorMessages()) {
						getCurrLinkingFeedBackPanel().setEnabled(true);
						getCurrLinkingFeedBackPanel().setVisible(true);
						getSession().error(error);
						getValidationFeedbackPanel().setVisible(true);
						getValidationFeedbackPanel().setOutputMarkupId(true);
						target.add(getValidationFeedbackPanel());
						
						/*Resetting the advisers in the page model*/
						SalesBCLinkingPageModel newModel = pageModel;
						List<LinkedAdviserDTO> refreshedAdvList = getGUIController().getAdviserDetails(pageModel.getSelectedServicingPanel());
						newModel.getSelectedServicingPanel().setLinkedAdvisersList(refreshedAdvList);
						setResponsePage(new SalesBCLinkingPage(newModel));
						
					}
				} catch (RequestException e) {
					editState = EditStateType.VIEW;
					this.error("Could not raise the request "
							+ e.getMessage());
					getValidationFeedbackPanel().setVisible(true);
					getValidationFeedbackPanel().setOutputMarkupId(true);
					target.add(getValidationFeedbackPanel());
					getSession().error("Could not raise the request "
							+ e.getMessage());
					target.add(getCurrLinkingFeedBackPanel());
					/*
					 * We do not want to set the response page as this reinitializes the
					 * page and all user selections are lost.
					 */
					
					/*Resetting the advisers in the page model*/
					SalesBCLinkingPageModel newModel = pageModel;
					List<LinkedAdviserDTO> refreshedAdvList = getGUIController().getAdviserDetails(pageModel.getSelectedServicingPanel());
					newModel.getSelectedServicingPanel().setLinkedAdvisersList(refreshedAdvList);
					setResponsePage(new SalesBCLinkingPage(newModel));
				}
				
				
			}
			
//			@Override
//			protected IAjaxCallDecorator getAjaxCallDecorator() {
//				return new AjaxCallDecorator() {
//					private static final long serialVersionUID = 1L;						
//					public CharSequence decorateScript(CharSequence script) {
//						//disable cancel too		
////						String disableAddLinkBtn = "";
////						if(addNewLinkBtn != null){
////							disableAddLinkBtn = "getElementById('"+addNewLinkBtn.getMarkupId()+"').disabled=true;";
////						}
//						return "overlay(true);" + script;
//					}
//				};
//			}
			
			@Override
			protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
				super.updateAjaxAttributes(attributes);
			        
			        // SRS Convenience method for overLay hiding/showing
			        attributes.getAjaxCallListeners().add(new SRSAjaxCallListener());
			}
		};
		button.setOutputMarkupId(true);
		button.setOutputMarkupPlaceholderTag(true);
		if (pageModel.getSelectedServicingPanel() != null && pageModel.getSelectedServicingPanel().getLinkedAdvisersList() != null 
				&& pageModel.getSelectedServicingPanel().getLinkedAdvisersList().size()>0) {
			button.setEnabled(true);
		} else {
			button.setEnabled(false);
		}
		button.setVisible(editState.equals(EditStateType.MODIFY));
		return button;
	}

	@SuppressWarnings("unchecked")
	private DropDownChoice createEndDateField(String id) {
		IModel model = new IModel() {
			private static final long serialVersionUID = 1L;

			public Object getObject() {
				return (panelModel.getEndDate());
			}

			public void setObject(Object arg0) {
				panelModel.setEndDate((Date) arg0);
			}

			public void detach() {
			}
		};
		Calendar cal = Calendar.getInstance(); 
		cal.add(Calendar.MONTH, 1);
		MonthEndDates monthEnds = null;
		try {
			monthEnds = getBusinessUtil().getLibertyMonthEndDates(new Date());
			if (monthEnds.getMonthEnd().before(new Date())) {
				monthEnds = getBusinessUtil().getLibertyMonthEndDates(cal.getTime());
			}
		} catch (DataNotFoundException e1) {
			SalesBCLinkingPanel.this
			.error("The liberty month end dates could not be found on the database, Please contact support");
			//target.add(getFeedBackPanel());
		} catch (LogicExecutionException e) {
			SalesBCLinkingPanel.this
			.error("The liberty month end dates could not be found on the database, Please contact support");
		}

		List<Date> dates = new ArrayList<Date>();
		dates.add(monthEnds.getMonthEnd());

		DropDownChoice endDateDDC = new DropDownChoice(id, model, dates) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onComponentTag(ComponentTag tag) {
				super.onComponentTag(tag);
				decorateStyleOnTag(tag);
			}

		};
		endDateDDC.setOutputMarkupId(true);
		
		if (pageModel.getSelectedServicingPanel() != null && pageModel.getSelectedServicingPanel().getLinkedAdvisersList() != null 
				&& pageModel.getSelectedServicingPanel().getLinkedAdvisersList().size()>0) {
			endDateDDC.setEnabled(true);
		} else {
			endDateDDC.setEnabled(false);
		}
		endDateDDC.setVisible(editState.equals(EditStateType.MODIFY));

		return endDateDDC;

	}
	
	
	@SuppressWarnings("unchecked")
	private DropDownChoice createStartDateField(String id) {

		IModel model = new IModel() {
			private static final long serialVersionUID = 1L;

			public Object getObject() {
				return (panelModel.getNewStartDate());
			}

			public void setObject(Object arg0) {
				panelModel.setNewStartDate((Date) arg0);
			}

			public void detach() {
			}
		};

		Calendar cal = Calendar.getInstance(); 	
		//START VZT2503 : INC000003328385 - on BC Linking screen,changes are to get next open month end directly rather adding adding 1 month to current date.
		//cal.add(Calendar.MONTH, 1);		
		MonthEndDates monthEnds = null;		
		int months=0;		
		try {
			monthEnds = getBusinessUtil().getLibertyMonthEndDates(new Date());			
			if (monthEnds.getMonthStart().before(new Date()) || monthEnds.getMonthStart().equals(new Date())) {
				//monthEnds = getBusinessUtil().getLibertyMonthEndDates(cal.getTime());
				cal.setTime(monthEnds.getMonthStart());
				cal.add(Calendar.MONTH, 1);
				months = cal.get(GregorianCalendar.MONTH) + 1;
				monthEnds = getBusinessUtil().getLibertyMonthEndDates(CIDateType.SRS_MONTHLY.getVersion(), cal.get(GregorianCalendar.YEAR), months);
				//END : INC000003328385
			}
		} catch (DataNotFoundException e1) {
			SalesBCLinkingPanel.this
			.error("The liberty month end dates could not be found on the database, Please contact support");
			//target.add(getFeedBackPanel());
		} catch (LogicExecutionException e) {
			SalesBCLinkingPanel.this
			.error("The liberty month end dates could not be found on the database, Please contact support");
		}

		List<Date> dates = new ArrayList<Date>();
		dates.add(monthEnds.getMonthStart());

		DropDownChoice startDateDDC = new DropDownChoice(id, model, dates) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onComponentTag(ComponentTag tag) {
				super.onComponentTag(tag);
				decorateStyleOnTag(tag);
			}

		};
		
		startDateDDC.add(new AjaxFormComponentUpdatingBehavior("change") {
			private static final long serialVersionUID = 0L;

			@Override
			protected void onUpdate(AjaxRequestTarget arg0) {
				if (panelModel.getNewStartDate()!=null) {
					addNewLinkBtn.setEnabled(true);
				}
			}
		});
		startDateDDC.setOutputMarkupId(true);
		
		if (panelModel.getSearchAdvisersList() != null
				&& panelModel.getSearchAdvisersList().size() > 0) {
			startDateDDC.setEnabled(true);
		} else {
			startDateDDC.setEnabled(false);
		}

		startDateDDC
				.setVisible(editState.equals(EditStateType.MODIFY)
						&& pageModel.getSelectedServicingPanel() != null
						&& pageModel.getSelectedServicingPanel()
								.getStatusCode() != null
						&& pageModel.getSelectedServicingPanel()
								.getStatusCode() == PartyStatusType.ACTIVE);

		return startDateDDC;

	}

	@SuppressWarnings("serial")
	private SRSDataGrid createRelTablePanel(String id) {

		List<LinkedAdviserDTO> dispList = pageModel.getSelectedServicingPanel()
				.getLinkedAdvisersList();
		if (dispList != null && dispList.size() > 1) {
			Collections.sort(dispList, new Comparator<LinkedAdviserDTO>() {
				public int compare(LinkedAdviserDTO s1, LinkedAdviserDTO s2) {
					if (s1.getAdviserName() == null
							&& s2.getAdviserName() == null) {
						return -1;
					} else if (s1.getAdviserName() != null
							&& s2.getAdviserName() == null) {
						return -1;
					} else if (s1.getAdviserName() == null
							&& s2.getAdviserName() != null) {
						return 1;
					} else {
						return s1.getAdviserName().compareToIgnoreCase(
								s2.getAdviserName());
					}
				}
			});
		}
		ListDataProvider<LinkedAdviserDTO> dataProvider = new ListDataProvider<LinkedAdviserDTO>(
				dispList);
		List<LinkedAdviserDTO> nonSelectibleList = new ArrayList<LinkedAdviserDTO>();
		/* Create non selectable object list */
		for (LinkedAdviserDTO linkedAdvDto : dispList) {

			if (linkedAdvDto.getAdviserStatus() != null
					&& linkedAdvDto.getAdviserStatus().equalsIgnoreCase(
							PartyStatusType.TERMINATED.getDescription())
					&& linkedAdvDto.getStatusStartDate() != null) {
				Calendar now = Calendar.getInstance();
				Calendar statusStartDate = Calendar.getInstance();
				statusStartDate.setTime(linkedAdvDto.getStatusStartDate());
				statusStartDate.add(Calendar.MONTH, 12);
				if (statusStartDate.compareTo(now) > 0) {
					nonSelectibleList.add(linkedAdvDto);
					continue;
				}
			}
			if (linkedAdvDto.getEndDate() != null) {
				Calendar calendar = Calendar.getInstance();
				calendar.set(9999, 11, 31);
				za.co.liberty.helpers.util.DateUtil datUtil = za.co.liberty.helpers.util.DateUtil
						.getInstance();
				if (datUtil.compareDatePart(linkedAdvDto.getEndDate(),
						calendar.getTime()) != 0) {
					nonSelectibleList.add(linkedAdvDto);
				}
			}
			
			/*
			 * If the link start date of the adviser is future dated, the row
			 * must be non-selectable as the link cannot be ended.
			 */
			Calendar today = Calendar.getInstance();
			if (linkedAdvDto.getStartDate() != null
					&& linkedAdvDto.getStartDate().after(today.getTime())) {
				if (!nonSelectibleList.contains(linkedAdvDto)) {
					nonSelectibleList.add(linkedAdvDto);
				}

			}
		}

		SRSDataGrid grid = new SRSDataGrid(id, new DataProviderAdapter(
				dataProvider),
				createInternalTableFieldColumns("AdviserDetailsTable"), null,nonSelectibleList) {
			@Override
			public void selectItem(IModel itemModel, boolean selected) {
				// allowing max 50 requests at a time for timeouts
				// SalesBCLinkingPanel.this.getSession().cleanupFeedbackMessages();
				super.selectItem(itemModel, selected);
				// if (getSelectionCount() > 50) {
				// error("You have selected "
				// + selectionCount
				// + " advisers. A max of "
				// +
				// HierarchyGUIController.MAX_SIMULTANEOUS_BRANCH_TRANSFER_REQUESTS
				// + " is allowed, please unselect "
				// + (selectionCount -
				// HierarchyGUIController.MAX_SIMULTANEOUS_BRANCH_TRANSFER_REQUESTS)
				// + " to proceed");
				// }
				// AjaxRequestTarget.get().add(feedBackPanel);
			}
		};
		grid.setAutoResize(true);
		grid.setOutputMarkupId(true);
		grid.setCleanSelectionOnPageChange(false);
		grid.setClickRowToSelect(false);
		grid.setAllowSelectMultiple(true);
		grid.setGridWidth(98, GridSizeUnit.PERCENTAGE);
		grid.setRowsPerPage(50);
		grid.setContentHeight(300, SizeUnit.PX);

		return grid;
	}

	@SuppressWarnings("unchecked")
	private List<IGridColumn> createInternalTableFieldColumns(String id) {
		List<IGridColumn> colList = new ArrayList<IGridColumn>();

		if (editState == EditStateType.MODIFY) {
			SRSGridRowSelectionCheckBox col = new SRSGridRowSelectionCheckBox("checkBox");
			col.setInitialSize(30);
			colList.add(col);
			
//			colList.add(new SRSDataGridColumn<LinkedAdviserDTO>(id, new Model(
//					""), "adviserOID", editState) {
//				private static final long serialVersionUID = 1L;
//
//				@Override
//				public Panel newCellPanel(WebMarkupContainer parent,
//						String componentId, IModel rowModel,
//						String objectProperty, EditStateType state,
//						LinkedAdviserDTO data) {
//					CheckBox box = new CheckBox("value", new PropertyModel(
//							rowModel.getObject(), objectProperty));
//					Panel panel = HelperPanel.getInstance(componentId, box,
//							true);
//
//					if (data.getAdviserStatus() != null
//							&& data.getAdviserStatus().equalsIgnoreCase(
//									PartyStatusType.TERMINATED.getDescription())
//							&& data.getStatusStartDate() != null) {
//						Calendar now = Calendar.getInstance();
//						Calendar statusStartDate = Calendar.getInstance();
//						statusStartDate.setTime(data.getStatusStartDate());
//						statusStartDate.add(Calendar.MONTH, 12);
//						if (statusStartDate.compareTo(now) > 0) {
//							box.setEnabled(false);
//						} else {
//							box.setEnabled(true);
//						}
//					} else {
//						box.setEnabled(true);
//					}
//					return panel;
//				}
//			}.setInitialSize(30));
		}

		// If not populating table columns please check table name in ENUM.
		for (SalesBCLinkingGUIField c : SalesBCLinkingGUIField
				.getEnumForTable("AdviserDetailsTable")) {
			colList.add(new PropertyColumn(new Model(c.getName()), c.getId(), c
					.getId()));
		}
		return colList;
	}

	/**
	 * Creates a text field for a search by the agreement number.
	 */
	@SuppressWarnings("unchecked")
	private TextField createAgreementSearchField(String id, String labelText) {
		final TextField field;
		if (panelModel.getPanelAdviserSearchDTO() == null) {
			panelModel.setPanelAdviserSearchDTO(new PanelAdviserSearchDTO());
		}
		field = new TextField(id, new PropertyModel(
				panelModel.getPanelAdviserSearchDTO(), "srsAgreementNumber"));

		/*
		 * Ensure that the context panel is of status "Active". Advisers cannot
		 * be linked to panels with status "Pending Termination" or "Terminated"
		 */

		field.setEnabled((editState != null) && !(editState.isViewOnly()));
		field.setType(Long.class);
		field.setLabel(new Model("Agreement nr"));
		field.add(new AjaxFormComponentUpdatingBehavior("change") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				final String inputValue = ((TextField<String>) getComponent())
						.getModelObject();

				String regex = "[0-9]+";
				if ((inputValue == null) || !(inputValue.matches(regex))) {
					field.error("Invalid 'SRS Agreement Number' supplied. Please enter numbers only.");
					getValidationFeedbackPanel().setVisible(false);
					getValidationFeedbackPanel().setOutputMarkupId(false);
					target.add(getLinkingFeedBackPanel());
					return;
				}
				long agreementNumber = 0;
				try {
					agreementNumber = Integer.parseInt(inputValue);
				} catch (NumberFormatException e) {
					SalesBCLinkingPanel.this
							.error("Invalid 'SRS Agreement Number' supplied. Please enter valid numbers only.");
					getValidationFeedbackPanel().setVisible(false);
					getValidationFeedbackPanel().setOutputMarkupId(false);
					target.add(getLinkingFeedBackPanel());
				}
				
				try {
					List<LinkedAdviserDTO> advisers = getGUIController()
							.findAdviserDetailsWithAgmtNumber(agreementNumber,
									AgreementSearchType.SRS_AGREEMENT);

					panelModel.setSearchAdvisersList(advisers);
					parentPage.swapContainerPanel(target);
				} catch (CommunicationException e) {
					SalesBCLinkingPanel.this
							.error("Error encountered during 'SRS Agreement' search. Please retry.");
					getValidationFeedbackPanel().setVisible(false);
					getValidationFeedbackPanel().setOutputMarkupId(false);
					target.add(getLinkingFeedBackPanel());
				} catch (DataNotFoundException e) {
					SalesBCLinkingPanel.this
							.error("Error encountered during 'SRS Agreement' search. Please retry.");
					getValidationFeedbackPanel().setVisible(false);
					getValidationFeedbackPanel().setOutputMarkupId(false);
					target.add(getLinkingFeedBackPanel());
				} catch (NamingException e) {
					SalesBCLinkingPanel.this
							.error("Error encountered during 'SRS Agreement' search. Please retry.");
					getValidationFeedbackPanel().setVisible(false);
					getValidationFeedbackPanel().setOutputMarkupId(false);
					target.add(getLinkingFeedBackPanel());
				}

			}
		});

		field.setVisible(editState.equals(EditStateType.MODIFY)
				&& pageModel.getSelectedServicingPanel() != null
				&& pageModel.getSelectedServicingPanel().getStatusCode() != null
				&& pageModel.getSelectedServicingPanel().getStatusCode() == PartyStatusType.ACTIVE);

		return field;

	}

	/**
	 * Creates a text field for a search by the Thirteen digit code.
	 */
	@SuppressWarnings("unchecked")
	private TextField createThirteenDigitCodeSearchField(String id,
			String labelText) {

		TextField field;
		if (panelModel.getPanelAdviserSearchDTO() == null) {
			panelModel.setPanelAdviserSearchDTO(new PanelAdviserSearchDTO());
		}
		field = new TextField(id, new PropertyModel(
				panelModel.getPanelAdviserSearchDTO(), "thirteenDigitCode"));
		field.setEnabled((editState != null) && !(editState.isViewOnly()));

		field.add(new AjaxFormComponentUpdatingBehavior("change") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				String inputValue = ((TextField<String>) getComponent())
						.getModelObject();

				String regex = "[0-9]+";
				if ((inputValue == null) || !(inputValue.matches(regex))) {
					SalesBCLinkingPanel.this
							.error("Invalid 'Thirteen Digit Code' supplied. Please enter numbers only.");
					getValidationFeedbackPanel().setVisible(false);
					getValidationFeedbackPanel().setOutputMarkupId(false);
					target.add(getLinkingFeedBackPanel());
					return;
				}

				long thirteenDigitCode = 0; 
				try {
					thirteenDigitCode = Long.valueOf(inputValue).longValue();
				} catch (NumberFormatException e) {
					SalesBCLinkingPanel.this
							.error("Invalid 'Thirteen Digit Code' supplied. Please enter valid numbers only.");
					getValidationFeedbackPanel().setVisible(false);
					getValidationFeedbackPanel().setOutputMarkupId(false);
					target.add(getLinkingFeedBackPanel());
				}

				try {
					List<LinkedAdviserDTO> advisers = getGUIController()
							.findAdviserDetailsWithAgmtNumber(
									thirteenDigitCode,
									AgreementSearchType.CONSULTANT_CODE);

					panelModel.setSearchAdvisersList(advisers);
					parentPage.swapContainerPanel(target);

				} catch (CommunicationException e) {
					SalesBCLinkingPanel.this
							.error("Error encountered during 'Thirteen Digit Code' search. Please retry.");
					getValidationFeedbackPanel().setVisible(false);
					getValidationFeedbackPanel().setOutputMarkupId(false);
					target.add(getLinkingFeedBackPanel());
				} catch (DataNotFoundException e) {
					SalesBCLinkingPanel.this
							.error("Error encountered during 'Thirteen Digit Code' search. Please retry.");
					getValidationFeedbackPanel().setVisible(false);
					getValidationFeedbackPanel().setOutputMarkupId(false);
					target.add(getLinkingFeedBackPanel());
				} catch (NamingException e) {
					SalesBCLinkingPanel.this
							.error("Error encountered during 'Thirteen Digit Code' search. Please retry.");
					getValidationFeedbackPanel().setVisible(false);
					getValidationFeedbackPanel().setOutputMarkupId(false);
					target.add(getLinkingFeedBackPanel());
				}

			}
		});

		field.setVisible(editState.equals(EditStateType.MODIFY)
				&& pageModel.getSelectedServicingPanel() != null
				&& pageModel.getSelectedServicingPanel().getStatusCode() != null
				&& pageModel.getSelectedServicingPanel().getStatusCode() == PartyStatusType.ACTIVE);

		return field;

	}

	/**
	 * Creates a text field for a search by the agreement number.
	 */
	@SuppressWarnings("unchecked")
	private TextField createPanelNameSearchField(String id, String labelText) {

		TextField field;

		if (panelModel.getPanelAdviserSearchDTO() == null) {
			panelModel.setPanelAdviserSearchDTO(new PanelAdviserSearchDTO());
		}
		field = new TextField(id, new PropertyModel(
				panelModel.getPanelAdviserSearchDTO(), "servicingPanelName"));

		field.setEnabled(editState != null && !(editState.isViewOnly()));

		field.add(new AjaxFormComponentUpdatingBehavior("change") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				final String valueAsString = ((TextField<String>) getComponent())
						.getModelObject();
				try {

					// Find the panels with the input panel name string pattern

					List<ServicingPanelDTO> panels = getGUIController()
							.findPanelOIDFromPanelName(valueAsString);
					
					
					/*If the current panel selected in the context and the panels in the search */
					
					/*
					 * If only one panel is returned, the adviser list can be
					 * displayed in the UI grid
					 */
					if (panels != null && panels.size() == 1
							&& panels.get(0).getPanelOID() != null) {
						List<LinkedAdviserDTO> adviserList = getGUIController()
								.findAdviserDetailsWithPanelOid(
										panels.get(0).getPanelOID());
						if (adviserList == null || adviserList.size() < 1) {
							SalesBCLinkingPanel.this.info("The panel "
									+ panels.get(0).getPanelName()
									+ " does not contain advisers.");
						}
						
						panelModel.setSearchAdvisersList(adviserList);
						panelModel.getPanelAdviserSearchDTO()
								.setSearchPanelList(panels);
						parentPage.swapContainerPanel(target);
					} else if (panels != null && panels.size() > 1) {
						panelModel.getPanelAdviserSearchDTO()
								.setSearchPanelList(panels);
						parentPage.swapContainerPanel(target);
						SalesBCLinkingPanel.this
						.info("Please select a panel from the 'Panel Result' drop down.");
					} else if (panels == null || panels.size() <= 0) {
						SalesBCLinkingPanel.this
								.error("No panels found for the provided input string. Please try again.");
						getValidationFeedbackPanel().setVisible(false);
						getValidationFeedbackPanel().setOutputMarkupId(false);
						target.add(getLinkingFeedBackPanel());
						return;
					}

				} catch (CommunicationException e) {
					SalesBCLinkingPanel.this
							.error("Error encountered during 'Panel Name' search. Message:"
									+ e.getMessage());
					getValidationFeedbackPanel().setVisible(false);
					getValidationFeedbackPanel().setOutputMarkupId(false);
					target.add(getLinkingFeedBackPanel());
				} catch (DataNotFoundException e) {
					SalesBCLinkingPanel.this
							.error("Error encountered during 'Panel Name' search. Message:"
									+ e.getMessage());
					getValidationFeedbackPanel().setVisible(false);
					getValidationFeedbackPanel().setOutputMarkupId(false);
					target.add(getLinkingFeedBackPanel());
				} catch (NamingException e) {
					SalesBCLinkingPanel.this
							.error("Error encountered during 'Panel Name' search. Message:"
									+ e.getMessage());
					getValidationFeedbackPanel().setVisible(false);
					getValidationFeedbackPanel().setOutputMarkupId(false);
					target.add(getLinkingFeedBackPanel());
				}

			}
		});

		field.setVisible(editState.equals(EditStateType.MODIFY)
				&& pageModel.getSelectedServicingPanel() != null
				&& pageModel.getSelectedServicingPanel().getStatusCode() != null
				&& pageModel.getSelectedServicingPanel().getStatusCode() == PartyStatusType.ACTIVE);

		return field;
	}


	private RequestKindType[] getAllowableRequests() {
		RequestKindType[] returnTypes = new RequestKindType[] {
				RequestKindType.MaintainSingleRegionServicingRelationships,
				RequestKindType.MaintainCrossRegionServicingRelationships };
		return returnTypes;
	}

	protected ISalesBCLinkingGuiController getGUIController() {
		if (guiController == null) {
			try {
				guiController = ServiceLocator
						.lookupService(ISalesBCLinkingGuiController.class);
			} catch (NamingException namingErr) {
				CommunicationException comm = new CommunicationException(
						"ISalesBCLinkingGuiController can not be looked up!");
				throw new CommunicationException(comm);
			}
		}
		return guiController;
	}

	/**
	 * Will run when cancel is clicked
	 * 
	 */
	protected void do_cancel(AjaxRequestTarget target) {
		panelModel.setSearchAdvisersList(new ArrayList<LinkedAdviserDTO>());
		if (panelModel.getPanelAdviserSearchDTO() == null) {
			panelModel.setPanelAdviserSearchDTO(new PanelAdviserSearchDTO());
		}
		panelModel.getPanelAdviserSearchDTO().setSearchPanelDetails(
				new ServicingPanelDTO());
		panelModel.getPanelAdviserSearchDTO().setSearchPanelList(
				new ArrayList<ServicingPanelDTO>());
		panelModel.setPanelAdviserSearchDTO(new PanelAdviserSearchDTO());
		
		parentPage.setEditState(EditStateType.VIEW, target);
		parentPage.swapContainerPanel(target);
		parentPage.swapNavigationPanel(target);
	}

	/**
	 * Called when Modify button is submitted. Notify parent and swap panels.
	 * 
	 * @param target
	 * @param form
	 */
	public void doMaintain_onSubmit(AjaxRequestTarget target, Form form) {
		if (parentPage.hasModifyAccess()) {
			maintainButton.setEnabled(false);
			parentPage.setEditState(EditStateType.MODIFY, target);
			parentPage.swapContainerPanel(target);

		} else {
			SalesBCLinkingPanel.this
					.error("You do not have access to the Maintain BC Linking, please consult support if you need access.");
			getCurrLinkingFeedBackPanel().setEnabled(true);
			getCurrLinkingFeedBackPanel().setVisible(true);
			target.add(getCurrLinkingFeedBackPanel());

		}
	}

	/**
	 * Create the modal view window
	 * 
	 * @param id
	 * @return
	 */
	public ModalWindow createModalViewWindow(String id) {
		final ModalWindow window = new ModalWindow(id);

		window.setTitle("Servicing Panel - Outstanding Requests");

		window.setCookieName(VIEW_WINDOW_COOKIE_NAME);

		// Create the page
		window.setPageCreator(new ModalWindow.PageCreator() {
			private static final long serialVersionUID = 1L;

			public Page createPage() {
				return createAuthWindowPage(window);

			}
		});

		// Initialise window settings
		window.setMinimalHeight(420);
		window.setInitialHeight(520);
		window.setMinimalWidth(850);
		window.setInitialWidth(850);
		window.setMaskType(MaskType.SEMI_TRANSPARENT);
		window.setCssClassName(ModalWindow.CSS_CLASS_GRAY);
//		window.setPageMapName(VIEW_WINDOW_PAGE_MAP);

		return window;
	}

	/**
	 * Create an the ViewRequest window page.
	 * 
	 * @param window
	 * @return
	 */
	protected Page createAuthWindowPage(ModalWindow window) {
		return new ServicingPanelsAuthorisationPage();
	}

	/**
	 * Add style information to tag
	 * 
	 * @param tag
	 */
	protected void decorateStyleOnTag(ComponentTag tag) {
		String style = (String) tag.getAttributes().get("style");
		style = (style == null) ? "" : style;
		style += ";width:" + SELECTION_WIDTH + ";";
		tag.put("style", style);
	}
	
	private IBusinessUtilitiesBean getBusinessUtil() throws LogicExecutionException {
		IBusinessUtilitiesBean businessUtil = null;
		try {
			businessUtil = ServiceLocator.lookupService(IBusinessUtilitiesBean.class);					
		} catch (NamingException e) {
			throw new LogicExecutionException("Could not get the BusinessUtilitiesBean MSG: " + e.getMessage(),0,0,e);
		}
		
		return businessUtil;
	}
	

	/**
	 * Get an instance of the RequestManagement bean
	 * 
	 * @return
	 * @throws NamingException
	 */

	private IRequestEnquiryManagement getRequestEnqManagement() throws NamingException {
		return ServiceLocator.lookupService(IRequestEnquiryManagement.class);

	}
	
	public FeedbackPanel getCurrLinkingFeedBackPanel() {
		return currenRelFeedBackPanel;
	}
	
	public FeedbackPanel getLinkingFeedBackPanel() {
		return linkingFeedBackPanel;
	}
	
	public FeedbackPanel getValidationFeedbackPanel() {
		return validationFeedBackPanel;
	}

}
