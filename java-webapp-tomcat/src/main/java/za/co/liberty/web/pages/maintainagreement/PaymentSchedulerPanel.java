package za.co.liberty.web.pages.maintainagreement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow.MaskType;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.ImageButton;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.validation.AbstractFormValidator;
import org.apache.wicket.markup.html.form.validation.IFormValidator;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.ValidationError;

import com.inmethod.grid.DataProviderAdapter;
import com.inmethod.grid.IGridColumn;
import com.inmethod.grid.SizeUnit;

import za.co.liberty.business.guicontrollers.agreement.IAgreementGUIController;
import za.co.liberty.dto.agreement.AgreementDTO;
import za.co.liberty.dto.agreement.PaymentSchedulerDTO;
import za.co.liberty.dto.agreement.maintainagreement.MaintainAgreementDTO;
import za.co.liberty.dto.agreement.maintainagreement.ValidAgreementValuesDTO;
import za.co.liberty.dto.agreement.properties.PaysToDTO;
import za.co.liberty.dto.agreement.properties.PaysToDTO.PayToType;
import za.co.liberty.dto.agreement.properties.PreIssueStatusDTO;
import za.co.liberty.dto.agreement.request.RequestEnquiryResultDTO;
import za.co.liberty.dto.agreement.request.RequestEnquiryRowDTO;
import za.co.liberty.dto.agreement.request.RequestEnquirySearchDTO;
import za.co.liberty.dto.party.bankingdetail.BankBranchDTO;
import za.co.liberty.exceptions.SystemException;
import za.co.liberty.exceptions.data.DataNotFoundException;
import za.co.liberty.exceptions.data.QueryTimeoutException;
import za.co.liberty.exceptions.error.request.RequestException;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.helpers.lookup.ServiceLocator;
import za.co.liberty.interfaces.agreements.AgreementKindType;
import za.co.liberty.interfaces.agreements.IAgreementDetailFLO;
import za.co.liberty.interfaces.agreements.requests.RequestDateType;
import za.co.liberty.interfaces.agreements.requests.RequestKindType;
import za.co.liberty.persistence.agreement.IRequestEntityManager;
import za.co.liberty.persistence.agreement.entity.DateRange;
import za.co.liberty.persistence.rating.IRatingEntityManager;
import za.co.liberty.srs.util.DateUtil;
import za.co.liberty.srs.util.agreement.TaxBasisConstants;
import za.co.liberty.web.constants.SRSAppWebConstants;
import za.co.liberty.web.data.enums.ComponentType;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.data.enums.fields.AgreementGUIField;
import za.co.liberty.web.pages.BasePage;
import za.co.liberty.web.pages.interfaces.ISecurityPanel;
import za.co.liberty.web.pages.maintainagreement.model.MaintainAgreementPageModel;
import za.co.liberty.web.pages.panels.HelperPanel;
import za.co.liberty.web.pages.panels.ViewTemplateBasePanel;
import za.co.liberty.web.wicket.markup.html.form.SRSDateField;
import za.co.liberty.web.wicket.markup.html.form.SRSDropDownChoice;
import za.co.liberty.web.wicket.markup.html.form.SRSTextField;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGrid;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGrid.GridSizeUnit;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGridColumn;
import za.co.liberty.web.wicket.markup.repeater.data.ListDataProvider;
import za.co.liberty.web.wicket.view.ContextDrivenViewTemplate;

/**
 * This panel represents the Payment Scheduler Panel as one of the TABS of
 * Maintain Agreement Panel.
 * 
 * @author pks2802
 * 
 */
public class PaymentSchedulerPanel extends
		ViewTemplateBasePanel<AgreementGUIField, AgreementDTO> implements
		ISecurityPanel {

	private static final long serialVersionUID = 1L;

	private PaymentSchedulerForm pageForm;

	private FeedbackPanel feedBackPanel;

	private MaintainAgreementPageModel pageModel;

	private HelperPanel doNotPayPanel;
	private HelperPanel requestedByPanel;
	private HelperPanel effDateSuspendPanel;
	private HelperPanel effDateCustomPanel;
	private HelperPanel preIssueStatusPanel;
	private HelperPanel monthEndPanel;
	private HelperPanel calSchedPanel;
	private HelperPanel payWeeklyPanel;
	private HelperPanel payDailyPanel;
	private HelperPanel nextDueDatePanel;
	private HelperPanel calSchedulerGridPanel;
	private SRSDataGrid calSchedulerGrid;
	private HelperPanel commentsPanel;
	private HelperPanel addSchedPanel;
	private ModalWindow addHistoryWindow;// pzm2509 Enhance Payment Scheduler
											// AML
	private HelperPanel historyPanel;

	private List<Date> calScheduleDates;
	private WebMarkupContainer paySchedulerContainer;

	private String delImage = "/SRSAppWeb/images/delete_icon.png";
	private EditStateType editState; // SSM2707 Market Integration 21/09/2015
										// Sweta Menon

	private Map<AgreementGUIField, Component> fields;

	private Map<AgreementGUIField, Component> labels;

	private transient IAgreementGUIController guiController;
	private static final Logger logger = Logger.getLogger(PaymentSchedulerPanel.class);

	private AgreementDTO getContext() {
		return pageModel.getMaintainAgreementDTO().getAgreementDTO();
	}

	private List<FormComponent> validationComponents = new ArrayList<FormComponent>();

	private boolean initialised;

	private boolean existingPaySchedRequest;

	private IRequestEntityManager requestEntityManager;

	private transient IRatingEntityManager ratingEntityManager;// SSM2707 Market
																// Integration
																// 21/09/2015
																// Sweta Menon
	private List<String> bcAllowedSalesCategories;
	private long agreementNo;

	/**
	 * Load the AgreementGUIController dynamically if it is null as this is a
	 * transient variable.
	 * 
	 * @return {@link IAgreementGUIController}
	 */
	private IAgreementGUIController getGuiController() {
		if (guiController == null) {
			try {
				guiController = ServiceLocator
						.lookupService(IAgreementGUIController.class);
			} catch (NamingException e) {
				logger.fatal("Could not lookup AgreementGUIController", e);
			}
		}
		return guiController;
	}

	/**
	 * @param arg0
	 */
	public PaymentSchedulerPanel(String id,
			MaintainAgreementPageModel pageModel, EditStateType editState,
			FeedbackPanel feedbackPanel, Page parentPage) {
		super(id, editState, parentPage);
		this.pageModel = pageModel;
		this.feedBackPanel = feedbackPanel;
		this.editState = editState; // SSM2707 Market Integration 21/09/2015
									// Sweta Menon
		// add(getPaymentSchedulerForm());

	}

	public PaymentSchedulerPanel(String id,
			MaintainAgreementPageModel pageModel, EditStateType editState,
			FeedbackPanel feedbackPanel, Page parentPage, long agmtNo) {
		super(id, editState, parentPage);
		this.pageModel = pageModel;
		this.feedBackPanel = feedbackPanel;
		this.editState = editState;
		this.agreementNo = agmtNo;

	}

	@Override
	protected void onBeforeRender() {
		if (!initialised) {
			initialised = true;
			// if(pageModel.getMaintainAgreementDTO().getAgreementDTO().getCurrentAndFutureAgreementRoles()
			// == null){
			// pageModel.getMaintainAgreementDTO().getAgreementDTO().setCurrentAndFutureAgreementRoles(new
			// ArrayList<AgreementRoleDTO>());
			// }
			// initialize the page model with the agreement role data
			initPageModel();
			List<RequestKindType> unAuthRequests = getOutStandingRequestKinds();
			PaymentSchedulerDTO paymentSchedulerDTO = getContext()
					.getPaymentSchedulerDTO();
			String paysToAgm = paymentSchedulerDTO != null ? paymentSchedulerDTO
					.getPaysToAgreement() : null;
			boolean hasPaysTo = paymentSchedulerDTO != null ? paymentSchedulerDTO
					.isHasPaysTo() : false;
			// check for existing requests FIRST as other panels use variables
			// set here
			for (RequestKindType kind : unAuthRequests) {
				if (kind == RequestKindType.ActivateAgreement) {
					existingPaySchedRequest = true;
					break;
				}

				if (kind == RequestKindType.MaintainPaymentSchedulerDetails
						|| kind == RequestKindType.MaintainPaymentSchedulerDetailsWithApproval) {
					existingPaySchedRequest = true;
					break;
				}
				if (kind == RequestKindType.MaintainPaysTo) {
					existingPaySchedRequest = true;
					debug("There is an UnAuthorized Maintain Pays To Request which needs to be authorized first to proceed");
					break;
				}

			}

			if (!existingPaySchedRequest && hasPaysTo) {
				existingPaySchedRequest = true;
				debug("Agreement has 'PaysTo' role with agreement " + paysToAgm
						+ "-this is " + paysToAgm
						+ " agreement�s payment scheduler");
			}

			// Set Eff Date Custom for PayMent Scheduler default to Agreement
			// start date
			// for pays to Own in Add Agreement Wizard
			PaysToDTO paysToDTO = getContext().getPaymentDetails();
			if (paysToDTO != null
					&& paysToDTO.getPayto() != null
					&& paysToDTO.getPayto() == PayToType.OWN_ACCOUNT
					&& getContext().getPaymentSchedulerDTO().getEffDateCustom() == null) {
				getContext().getPaymentSchedulerDTO().setEffDateCustom(
						getContext().getStartDate());
			}

			add(getPaymentSchedulerForm());

		}
		if (feedBackPanel == null) {
			feedBackPanel = this.getFeedBackPanel();
		}

		super.onBeforeRender();
	};

	private void initPageModel() {
		if (pageModel == null) {
			error("Page Model should never be null, Please call support if you continue seeing this error");
			pageModel = new MaintainAgreementPageModel(new AgreementDTO(),
					new ValidAgreementValuesDTO());
		}
		if (pageModel.getMaintainAgreementDTO() == null) {
			error("An agreement needs to be selected to adjust the Payment Scheduler");
			pageModel.setMaintainAgreementDTO(new MaintainAgreementDTO());
		}
		if (pageModel.getMaintainAgreementDTO().getAgreementDTO() == null) {
			error("An agreement needs to be selected to adjust the Payment Scheduler");
			pageModel.getMaintainAgreementDTO().setAgreementDTO(
					new AgreementDTO());
		}
	}

	/**
	 * Get the main page form
	 * 
	 * @return
	 */
	private PaymentSchedulerForm getPaymentSchedulerForm() {
		if (pageForm == null) {
			pageForm = new PaymentSchedulerForm("searchForm");
		}
		return pageForm;
	}

	private HelperPanel getDoNotPayPanel() {

		if (doNotPayPanel == null) {
			// SSM2707 Market Integration 21/09/2015 Sweta Menon Begin
			/*
			 * The Do Not Pay indicator must be set to true for Broker
			 * consultant agreements.
			 */
			if (editState.equals(EditStateType.ADD)
					&& pageModel.getMaintainAgreementDTO()
							.getSalesCategoryDTO() != null
					&& pageModel.getMaintainAgreementDTO()
							.getSalesCategoryDTO().getSalesCategory() != null) {
				/* Get the list of Sales Categories for BCs */
				List<String> bcSalesCategories = getBCSalesCatAllowed();

				if (bcSalesCategories != null
						&& bcSalesCategories.size() > 0
						&& bcSalesCategories.contains(pageModel
								.getMaintainAgreementDTO()
								.getSalesCategoryDTO().getSalesCategory())) {
					getContext().getPaymentSchedulerDTO().setDoNotPay(true);
				}

			}
			// SSM2707 Market Integration 21/09/2015 Sweta Menon Begin
			doNotPayPanel = createGUIPageField(AgreementGUIField.DO_NOT_PAY,
					getContext().getPaymentSchedulerDTO(),
					ComponentType.CHECKBOX, true);
			// System.out.println(getContext().getPaymentSchedulerDTO().isDoNotPay());
			if (doNotPayPanel.getEnclosedObject() instanceof CheckBox) {
				validationComponents.add((CheckBox) doNotPayPanel
						.getEnclosedObject());

				// Set Comments field Mandatory if Do not pay indicator changed
				final boolean oldDoNotPayValue = getContext()
						.getPaymentSchedulerDTO().isDoNotPay();

				final CheckBox dnpBox = ((CheckBox) doNotPayPanel
						.getEnclosedObject());
				dnpBox.add(new AjaxFormComponentUpdatingBehavior("click") {
                    private static final long serialVersionUID = 1L;


					@Override
					protected void onUpdate(AjaxRequestTarget target) {
						// clear comment box
						getContext().getPaymentSchedulerDTO().setComments("");

						GregorianCalendar dnp = new GregorianCalendar();
						Date currDate = dnp.getTime();

						GregorianCalendar dnp2 = new GregorianCalendar();
						dnp2.set(2018, 02, 14);
						Date startDate = dnp2.getTime();

						// getGuiController().getPaySchedHistoryDataList(new
						// Long(22348),currDate,
						// pageModel.getMaintainAgreementDTO().getAgreementDTO().getStartDate());

						List<BankBranchDTO> list = null;
						Object inp = dnpBox.getConvertedInput();

						if (inp instanceof Boolean) {
							boolean inpVal = (Boolean) inp;
							boolean required = false;
							Label lbl = null;

							/*
							 * mbm0309 AML Tax Warning Message
							 */
							if (inpVal == false && inpVal != oldDoNotPayValue) {
								boolean isTaxWarnMsg = taxWarningMessage();
								if (isTaxWarnMsg) {
									warn("This agreement has earnings pre-dating the current tax year - a prior year tax calculation will need to be done. Please contact SRS production support for assistance.");
									target.add(getFeedbackPanel());
								}
							}

							if (inpVal != oldDoNotPayValue) {
								lbl = new Label(
										"value",
										AgreementGUIField.COMMENTS
												.getDescription()
												.concat(asteriskSymbolWithFormatting)
												.concat(colonSymbol));
								lbl.setEscapeModelStrings(false);
								required = true;
							} else
								lbl = new Label("value",
										AgreementGUIField.COMMENTS
												.getDescription().concat(
														colonSymbol));
							HelperPanel panel = HelperPanel.getInstance(
									AgreementGUIField.COMMENTS.getLabelId(),
									lbl);
							panel.setOutputMarkupId(true);
							panel.setOutputMarkupPlaceholderTag(true);
							getPaymentSchedulerForm().get(
									AgreementGUIField.COMMENTS.getLabelId())
									.replaceWith(panel);
							target.add(getCommentsPanel(required));
							target.add(panel);
							target.add(feedBackPanel);
						}
					}

				});

			}
		}
		/*
		 * MBM0309 warning message
		 * 
		 * 02/03/2018
		 */
		if (getEditState() == EditStateType.AUTHORISE && (!getContext().getPaymentSchedulerDTO().isDoNotPay())) {
		 boolean isTaxWarnMsg = taxWarningMessage();
		 if (isTaxWarnMsg) {
		 warn("This agreement has earnings pre-dating the current tax year - a prior year tax calculation will need to be done. Please contact SRS production support for assistance.");
		 // target.add(getFeedbackPanel());
		 }
		}
		return doNotPayPanel;
	}

	/**
	 * mbm0309 AML Tax Warning Message This method performs Calendar Arithmetic
	 * for previous tax year DateRange 02/03/2018 begin
	 */
	private boolean taxWarningMessage() {

		DateRange range = getPreviousLibertyTaxYearDateRange();
		Date agmtStartDate = getPageModelObject().getMaintainAgreementDTO()
				.getAgreementDTO().getStartDate();
		
		if (getEditState() == EditStateType.AUTHORISE) {
			
		
			try {
				IAgreementDetailFLO flo = getGuiController()
						.getAgreementsSimpleDetail(agreementNo);
				if (flo != null) {
					agmtStartDate = flo.getStartDate();

				}
			} catch (DataNotFoundException e1) {
				// do nothing, no kind found so we send through 0
			}
		}
		
		

		GregorianCalendar gc = new GregorianCalendar();
		// subtract 4 days
		gc.setTime(range.getEndDate());
		gc.add(Calendar.DAY_OF_MONTH, -4);
		Date result = gc.getTime();
		long agmtNo;

		// check that the agreement start date is before the end date of the tax
		// year
		if (agmtStartDate.after(range.getEndDate())) {
			return false;
		}

		agmtNo = getContext().getId();
		if (getEditState() == EditStateType.AUTHORISE) {
			agmtNo = agreementNo;
		}

		RequestEnquirySearchDTO searchDto = new RequestEnquirySearchDTO(agmtNo,
				Arrays.asList(new RequestKindType[] { RequestKindType.Settle,
						RequestKindType.ManualSettle }), null);
		searchDto.setDateType(RequestDateType.REQUESTED);
		searchDto.setStartDate(result);
		searchDto.setEndDate(range.getEndDate());

		try {
			RequestEnquiryResultDTO resultDto = getRequestEntityManager()
					.findRequests(searchDto, 1, 1, RequestEnquiryRowDTO.class);
			if (resultDto == null
					|| (resultDto.getResultList() != null && resultDto
							.getResultList().size() <= 0)) {
				return true;
			}

		} catch (RequestException e) {
			throw new SystemException(
					"Could not find Liberty tax year end date, msg "
							+ e.getMessage(), 0, 0, e);
		} catch (QueryTimeoutException e) {
			throw new SystemException(
					"Query timed out while searching for previous requests "
							+ e.getMessage(), 0, 0, e);
		}

		return false;
	}

	/*
	 * Tax warning End
	 */

	// pzm2509
	protected IRequestEntityManager getRequestEntityManager() {
		if (requestEntityManager == null) {

			try {
				requestEntityManager = ServiceLocator
						.lookupService(IRequestEntityManager.class);
			} catch (NamingException e) {
				throw new CommunicationException(e.getMessage());
			}
		}
		return requestEntityManager;
	}

	private HelperPanel getCommentsPanel(boolean required) {

		if (commentsPanel == null) {
			commentsPanel = createGUIPageField(AgreementGUIField.COMMENTS,
					getContext().getPaymentSchedulerDTO(),
					ComponentType.TEXTAREA, true);
			if (commentsPanel.getEnclosedObject() instanceof TextArea) {
				TextArea textArea = (TextArea) commentsPanel
						.getEnclosedObject();

				// SSM2707 Market Integration 21/09/2015 Sweta Menon Begin
				/*
				 * The Do Not Pay indicator must be set to true for Broker
				 * consultant agreements.
				 */
				if (editState.equals(EditStateType.ADD)
						&& pageModel.getMaintainAgreementDTO()
								.getSalesCategoryDTO() != null
						&& pageModel.getMaintainAgreementDTO()
								.getSalesCategoryDTO().getSalesCategory() != null) {
					/* Get the list of Sales Categories for BCs */
					List<String> bcSalesCategories = getBCSalesCatAllowed();

					if (bcSalesCategories != null
							&& bcSalesCategories.size() > 0
							&& bcSalesCategories.contains(pageModel
									.getMaintainAgreementDTO()
									.getSalesCategoryDTO().getSalesCategory())) {
						IModel<String> textModel = Model
								.of("SIMS Project Business Requirement");
						getContext().getPaymentSchedulerDTO().setComments(
								"SIMS Project Business Requirement");
						textArea.setDefaultModel(textModel);
					}

				}
				// SSM2707 Market Integration 21/09/2015 Sweta Menon End
				textArea.setRequired(required);
				textArea.setEnabled(!isViewOnly());

				validationComponents.add(textArea);

			}

		}
		// pzm2509 changed the editstatetype to view
		if (getEditState() == EditStateType.VIEW && required) {
			TextArea textArea = (TextArea) commentsPanel.getEnclosedObject();

			IModel<String> textModel = Model.of(getContext()
					.getPaymentSchedulerDTO().getComments());
			// getContext().getPaymentSchedulerDTO().setComments("");
			textArea.setDefaultModel(textModel);
			// SSM2707 Market Integration 21/09/2015 Sweta Menon End
			textArea.setRequired(required);
			textArea.setEnabled(!isViewOnly());

			validationComponents.add(textArea);
		}
		// pzm2509 changed the editstatetype to view
		if (getEditState() == EditStateType.VIEW && !required) {
			TextArea textArea = (TextArea) commentsPanel.getEnclosedObject();

			IModel<String> textModel = Model.of(getContext()
					.getPaymentSchedulerDTO().getComments());
			// getContext().getPaymentSchedulerDTO().setComments("");
			textArea.setDefaultModel(textModel);
			// SSM2707 Market Integration 21/09/2015 Sweta Menon End
			textArea.setRequired(required);
			textArea.setEnabled(!isViewOnly());

			validationComponents.add(textArea);
		}
		return commentsPanel;

	}

	private HelperPanel getRequestedByPanel() {

		if (requestedByPanel == null) {
			requestedByPanel = createGUIPageField(
					AgreementGUIField.REQUESTED_BY, getContext()
							.getPaymentSchedulerDTO(), ComponentType.TEXTFIELD,
					true);
			if (requestedByPanel.getEnclosedObject() instanceof TextField) {
				TextField field = (TextField) requestedByPanel
						.getEnclosedObject();
				// add validation
			}
		}
		return requestedByPanel;

	}

	private HelperPanel getEffDateSuspendPanel() {

		if (effDateSuspendPanel == null) {

			if (getEditState() != null
					&& getEditState() == EditStateType.MODIFY) {
				/*
				 * A new object is created just to display the current date on
				 * the screen. This code must be changed.
				 */
				PaymentSchedulerDTO newDTO = new PaymentSchedulerDTO();
				newDTO.setEffDateSuspend(Calendar.getInstance().getTime());
				effDateSuspendPanel = createGUIPageField(
						AgreementGUIField.EFFECTIVE_DT_SUSPEND, newDTO,
						ComponentType.LABEL, true);
			} else {
				effDateSuspendPanel = createGUIPageField(
						AgreementGUIField.EFFECTIVE_DT_SUSPEND, getContext()
								.getPaymentSchedulerDTO(), ComponentType.LABEL,
						true);
			}
		}

		if (getEditState() != null && getEditState() == EditStateType.MODIFY) {

			PaymentSchedulerDTO newDTO = new PaymentSchedulerDTO();
			newDTO.setEffDateSuspend(Calendar.getInstance().getTime());
			effDateSuspendPanel = createGUIPageField(
					AgreementGUIField.EFFECTIVE_DT_SUSPEND, newDTO,
					ComponentType.LABEL, true);
		}

		return effDateSuspendPanel;
	}

	private HelperPanel getEffDateCustomPanel() {
		if (effDateCustomPanel == null) {
			effDateCustomPanel = createGUIPageField(
					AgreementGUIField.EFFECTIVE_DT_CUSTOM, getContext()
							.getPaymentSchedulerDTO(),
					ComponentType.DATE_SELECTION_TEXTFIELD, true);
			if (effDateCustomPanel.getEnclosedObject() instanceof TextField) {
				TextField field = (TextField) effDateCustomPanel
						.getEnclosedObject();
				field.setRequired(getViewTemplate().isRequired(
						AgreementGUIField.EFFECTIVE_DT_CUSTOM, getContext()));
				validationComponents.add(field);
			}
			//Santosh datepicker fix
			if ( effDateCustomPanel.getEnclosedObject() instanceof SRSDateField) {
				((SRSDateField)effDateCustomPanel.getEnclosedObject()).add(((SRSDateField)effDateCustomPanel.getEnclosedObject()).newDatePicker());
		}
		}

		return effDateCustomPanel;
	}

	private HelperPanel getPreIssueStatusPanel() {

		if (preIssueStatusPanel == null) {

			SRSDropDownChoice dropDownChoice = new SRSDropDownChoice("value",
					new PropertyModel(getContext().getPaymentSchedulerDTO(),"preIssueStatus"),
					pageModel.getValidAgreementValues().getValidPreIssueStatus(), new ChoiceRenderer(),
					"Select");
			dropDownChoice.setRequired(false);
			preIssueStatusPanel = createDropDownChoicePanel(
					AgreementGUIField.PRE_ISSUE_STATUS, dropDownChoice);
		}

		if (preIssueStatusPanel.getEnclosedObject() instanceof DropDownChoice) {
			DropDownChoice dropDownChoice = (DropDownChoice) preIssueStatusPanel
					.getEnclosedObject();
			dropDownChoice.setRequired(false);
			dropDownChoice.setNullValid(true);
			
			dropDownChoice.add(new IValidator() {

				@Override
				public void validate(IValidatable validatable) {

					if (getEditState().isAdd()) {
						return;
					}
					PreIssueStatusDTO preIssueStatusDTO = (PreIssueStatusDTO) validatable
							.getValue();
					if (preIssueStatusDTO == null)
						return;
					int agmtKind = getContext().getKind();

					for (AgreementKindType obj : getTestListForPreIssueStatus()) {
						if (obj.getKind() == agmtKind
								&& preIssueStatusDTO.getId() != 0) {
							
							validatable.error(new ValidationError(
									"PreIssue status not eligible"));
							return;
						}
					}

				}

				

			});

		}
		return preIssueStatusPanel;
	}

	private HelperPanel getMonthEndPanel() {

		if (monthEndPanel == null) {
			monthEndPanel = createGUIPageField(AgreementGUIField.MONTH_END,
					getContext().getPaymentSchedulerDTO(),
					ComponentType.CHECKBOX, true);
		}
		if (monthEndPanel.getEnclosedObject() instanceof CheckBox) {
			CheckBox checkBox = (CheckBox) monthEndPanel.getEnclosedObject();
			// Greyed for all Agreements.
			checkBox.setEnabled(false);
		}

		return monthEndPanel;
	}

	private HelperPanel getCalendarSchedulePanel() {

		if (calSchedPanel == null) {
			calSchedPanel = createGUIPageField(
					AgreementGUIField.CALENDER_SCHEDULE, getContext()
							.getPaymentSchedulerDTO(), ComponentType.CHECKBOX,
					true);
		}

		if (calSchedPanel.getEnclosedObject() instanceof CheckBox) {
			final CheckBox checkBox = (CheckBox) calSchedPanel
					.getEnclosedObject();
			checkBox.add(new IValidator() {

				@Override
				public void validate(IValidatable validatable) {
					
					if (getEditState().isAdd()) {
						return;
					}
					
					boolean calSchedVal = (Boolean) validatable.getValue();

					if (calSchedVal
							&& (calScheduleDates == null || calScheduleDates
									.size() == 0)) {
						
						validatable.error(new ValidationError(
								"There needs to be at least one date selected in the Payment Scheduler calendar"));
//						pageForm.error("There needs to be at least one date selected in the Payment Scheduler calendar 2");
						return;
					}

					
					
					
				}
			});

			checkBox.add(new AjaxFormComponentUpdatingBehavior("click") {

				@Override
				protected void onUpdate(AjaxRequestTarget target) {

					getContext().getPaymentSchedulerDTO().setPayDaily(false);
					target.add(payDailyPanel);

				}
			});
		}

		return calSchedPanel;
	}

	private HelperPanel getPayWeeklyPanel() {

		if (payWeeklyPanel == null) {
			SRSDropDownChoice dropDownChoice = new SRSDropDownChoice("value",
					new PropertyModel(getContext().getPaymentSchedulerDTO(),
							"payWeekly"), pageModel.getValidAgreementValues()
							.getValidDaysofTheWeek(), new ChoiceRenderer(),
					"*** None ***");
			dropDownChoice.setNullValid(true);
			payWeeklyPanel = createDropDownChoicePanel(
					AgreementGUIField.PAY_WEEKLY, dropDownChoice);

		}

		if (payWeeklyPanel.getEnclosedObject() instanceof DropDownChoice) {
			final DropDownChoice dropDownChoice = (DropDownChoice) payWeeklyPanel
					.getEnclosedObject();
			// Enable only for Broker Agreements & Stop Order Broker Agreements
			int agmtKind = getContext().getKind();
			dropDownChoice.setEnabled((agmtKind == AgreementKindType.BROKER
					.getKind())
					|| (agmtKind == AgreementKindType.STOP_ORDER_BROKER
							.getKind())
					|| (agmtKind == AgreementKindType.AGENT.getKind())
					|| (agmtKind == AgreementKindType.FRANCHISE.getKind()));

			dropDownChoice
					.add(new AjaxFormComponentUpdatingBehavior("change") {

						@Override
						protected void onUpdate(AjaxRequestTarget target) {

							getContext().getPaymentSchedulerDTO().setPayDaily(
									false);
							target.add(payDailyPanel);

						}
					});
		}
		return payWeeklyPanel;
	}

	private HelperPanel getPayDailyPanel() {
		if (payDailyPanel == null) {
			payDailyPanel = createGUIPageField(AgreementGUIField.PAY_DAILY,
					getContext().getPaymentSchedulerDTO(),
					ComponentType.CHECKBOX, true);

		}

		if (payDailyPanel.getEnclosedObject() instanceof CheckBox) {
			final CheckBox checkBox = (CheckBox) payDailyPanel
					.getEnclosedObject();
			// Enable only for Broker Agreements. For all other Agreement Types
			// this is disabled
			checkBox.setEnabled(getContext().getKind() == AgreementKindType.BROKER
					.getKind() && getEditState() != EditStateType.VIEW);
			checkBox.add(new AjaxFormComponentUpdatingBehavior("click") {

				@Override
				protected void onUpdate(AjaxRequestTarget target) {

					getContext().getPaymentSchedulerDTO().setCalSchedule(false);
					getContext().getPaymentSchedulerDTO().setPayWeekly(null);
					target.add(payWeeklyPanel);
					target.add(calSchedPanel);

				}
			});
		}

		return payDailyPanel;
	}

	private HelperPanel getNextDueDatePanel() {
		if (nextDueDatePanel == null) {
			nextDueDatePanel = createGUIPageField(
					AgreementGUIField.NEXT_DUE_DT, getContext()
							.getPaymentSchedulerDTO(), ComponentType.LABEL,
					true);

		}
		return nextDueDatePanel;
	}

	private HelperPanel getCalSchedulerGridPanel() {

		if (calSchedulerGridPanel == null) {
			calScheduleDates = getContext().getPaymentSchedulerDTO()
					.getAllPaymentScheduleColl();
			if (calScheduleDates == null)
				calScheduleDates = new ArrayList<Date>();
			calSchedulerGrid = new SRSDataGrid("value",
					new DataProviderAdapter(new ListDataProvider<Date>(
							calScheduleDates)), getColumns(), getEditState(),
					null);

			calSchedulerGrid.setOutputMarkupId(true);
			calSchedulerGrid.setCleanSelectionOnPageChange(false);
			calSchedulerGrid.setClickRowToSelect(false);
			calSchedulerGrid.setAllowSelectMultiple(false);
			calSchedulerGrid.setGridWidth(30, GridSizeUnit.PERCENTAGE);
			calSchedulerGrid.setRowsPerPage(5);
			calSchedulerGrid.setContentHeight(100, SizeUnit.PX);

			calSchedulerGridPanel = HelperPanel.getInstance("paySchedCalGrid",
					calSchedulerGrid);

		}

		calSchedulerGridPanel.setOutputMarkupId(true);
		calSchedulerGridPanel.setOutputMarkupPlaceholderTag(true);
		return calSchedulerGridPanel;
	}

	/**
	 * Get the columns for the Payment Scheduler Grid
	 * 
	 * @return the data columns
	 */
	@SuppressWarnings("serial")
	private List<IGridColumn> getColumns() {
		List<IGridColumn> ret = new ArrayList<IGridColumn>();
		/**
		 * Payment Schedules
		 */
		SRSDataGridColumn<Date> paymentSchedulers = new SRSDataGridColumn<Date>(
				"paymentSched", new Model("Payment Schedules"), null,
				getEditState());
		paymentSchedulers.setMinSize(200);
		ret.add(paymentSchedulers);

		/**
		 * Remove icon ( to be displayed for all future Dates and only if Edit
		 * state is not view)
		 */
		SRSDataGridColumn<Date> userAction = new SRSDataGridColumn<Date>(
				"removeAction", new Model("Remove"), null, getEditState()) {

			@Override
			public Panel newCellPanel(WebMarkupContainer parent,
					String componentId, IModel rowModel, String objectProperty,
					EditStateType state, final Date data) {

				// If the Edit State type is not view and the Payment Schedule
				// Dates >= current Date then only display the Remove icon in
				// panel...else return emty panel.
				// On Click of Remove icon, remove the rows from the Grid.
				if (DateUtil.compareDates(data, new Date()) >= 0
						&& !isViewOnly()) {
//					#WICKETTEST #WICKETFIX Added model.of which was not required before, check impact
					ImageButton removeIcon = new ImageButton("value", Model.of("Remove")) {
						private static final long serialVersionUID = 1L;

						@Override
						public void onSubmit() {
							super.onSubmit();
						}
					};
					removeIcon.add(new AttributeModifier("title",
							"Remove"));
					removeIcon
							.add(new AttributeModifier("src", delImage));
					removeIcon.add(new AjaxEventBehavior("click") {
						private static final long serialVersionUID = 1L;

						@Override
						protected void onEvent(AjaxRequestTarget target) {

							if (calScheduleDates != null) {
								for (Date d : calScheduleDates) {
									if (d.equals(data)) {
										calScheduleDates.remove(data);
										break;
									}
								}
							}
							target.add(calSchedulerGridPanel);

						}
						
						

//						#WICKETTEST #WICKETFIX How to migrate this as there is no such function anymore
//						@Override
//						protected IAjaxCallDecorator getAjaxCallDecorator() {
//							return new CancelEventIfNoAjaxDecorator();
//						}
					});

					return HelperPanel.getInstance(componentId, removeIcon);
				} else
					return new EmptyPanel(componentId);
			}
		};
		userAction.setMinSize(70);
		ret.add(userAction);

		return ret;
	}

	private MaintainAgreementPageModel getPageModelObject() {
		final MaintainAgreementPageModel localPageModel = (MaintainAgreementPageModel) pageModel;

		return localPageModel;
	}

	private class PaymentSchedulerForm extends Form {

		private PaymentSchedulerForm(String id) {
			super(id);

			labels = getLabels();
			fields = getFields();
			String str = null;
			boolean isMandatory = (getEditState() == EditStateType.MODIFY || getEditState() == EditStateType.ADD);

			/**
			 * Add all labels
			 */
			for (AgreementGUIField field : AgreementGUIField
					.getPaymentSchedulerLabels()) {
				boolean isReq = getViewTemplate().isRequired(field,
						getContext());
				if (field == AgreementGUIField.DO_NOT_PAY) {
					str = (isReq && isMandatory) ? field.getDescription()
							.concat(asteriskSymbolWithFormatting) : field
							.getDescription();
				} else
					str = (isReq && isMandatory) ? field.getDescription()
							.concat(asteriskSymbolWithFormatting)
							.concat(colonSymbol) : field.getDescription()
							.concat(colonSymbol);
				Label lbl = new Label("value", str);
				lbl.setEscapeModelStrings(false);
				HelperPanel panel = HelperPanel.getInstance(field.getLabelId(),
						lbl);
				labels.put(field, lbl);
				panel.setOutputMarkupId(true);
				panel.setOutputMarkupPlaceholderTag(true);
				this.add(panel);
			}

			add(getDoNotPayPanel());
			add(getRequestedByPanel());
			add(getEffDateSuspendPanel());
			add(getEffDateCustomPanel());
			add(getPreIssueStatusPanel());
			add(getMonthEndPanel());
			add(getCalendarSchedulePanel());
			add(getPayWeeklyPanel());
			add(getPayDailyPanel());
			add(getNextDueDatePanel());
			add(getPaySchedulerContainer());
			// Comments Panel not mandatory initially
			add(getCommentsPanel(false));
			add(addHistoryWindow = createModalWindow("paySchedHistory"));
			add(getHistoryButtonPanel());

//			add(new IFormValidator() {
				
			add(new AbstractFormValidator() {

				private static final long serialVersionUID = 1L;

				private boolean doNotpay;
				private Date doNotPayStartDt;
				private Date customEffDt;
				private String comments;

				public FormComponent[] getDependentFormComponents() {
					return null;
				}

				public void validate(final Form frm) {
					if (isViewOnly()) {
						return;
					}

					for (FormComponent comp : validationComponents) {

						if (comp.getParent()
								.getId()
								.equals(AgreementGUIField.DO_NOT_PAY
										.getFieldId())) {
							doNotpay = (Boolean) comp.getConvertedInput();
						} else if (comp
								.getParent()
								.getId()
								.equals(AgreementGUIField.EFFECTIVE_DT_SUSPEND
										.getFieldId())) {
							doNotPayStartDt = (Date) comp.getConvertedInput();
						} else if (comp
								.getParent()
								.getId()
								.equals(AgreementGUIField.EFFECTIVE_DT_CUSTOM
										.getFieldId())) {
							customEffDt = (Date) comp.getConvertedInput();
						} else if (comp
								.getParent()
								.getId()
								.equals(AgreementGUIField.COMMENTS.getFieldId())) {
							comments = (String) comp.getConvertedInput();
						}
					}

					if (doNotpay) {
						if (doNotPayStartDt != null
								&& customEffDt != null
								&& DateUtil.compareDates(doNotPayStartDt,
										customEffDt) < 0)
							PaymentSchedulerForm.this
									.error("Do Not Pay Effective From Date must be after Effective Date");

					}

					if ((getPreviousPaymentSchedulerDTO().isDoNotPay() != doNotpay)
							&& (comments == null || comments.trim().length() == 0)) {
						PaymentSchedulerForm.this
								.error("Comments is Mandatory since 'Do Not Pay' indicator has been modified");
					}
					 
					/**
					 * Add specific validation due to issues with form component validators for ADD state only
					 */
					if (!getEditState().isAdd()) {
						return;
					}
					
					/*
					 *  Validate calendar schedule
					 *  TODO this really should be in the guiController!!
					 */
//					if (getContext().getPaymentSchedulerDTO()!=null) {
						
					List lst = getContext().getPaymentSchedulerDTO().getAllPaymentScheduleColl();
					
					if (logger.isDebugEnabled())
						logger.debug("validate.calschedule is " + getContext().getPaymentSchedulerDTO().isCalSchedule()
							+ " - paymentLilst="+ lst + " -" +  ((lst!=null)?lst.size():null));
					
					if (getContext().getPaymentSchedulerDTO().isCalSchedule() 
							&& (lst == null || lst.size()==0)) {
						frm.error("There needs to be at least one date selected in the Payment Scheduler calendar.");
					}
//					}
					
					/*
					 * Validate pre-issue
					 */
					if (getContext().getPaymentSchedulerDTO().getPreIssueStatus() != null) {
						
						int agmtKind = getContext().getKind();
						PreIssueStatusDTO preIssueStatusDTO = getContext().getPaymentSchedulerDTO().getPreIssueStatus();
	
						if (logger.isDebugEnabled())
							logger.debug("validate.preIssuestatus  kind=" + agmtKind 
									+ "  preIssue=" + preIssueStatusDTO.getId());
						for (AgreementKindType obj : getTestListForPreIssueStatus()) {
							if (obj.getKind() == agmtKind
									&& preIssueStatusDTO.getId() != 0) {
								frm.error("PreIssue status not eligible");
								break;
							}
						}
					} else {
						if (logger.isDebugEnabled())
							logger.debug("validate.preIssuestatus preIssue null, skip check" );
					}
					
					
				}

			});

		}

		public WebMarkupContainer getPaySchedulerContainer() {
			if (paySchedulerContainer == null) {
				paySchedulerContainer = new WebMarkupContainer(
						"paySchedulerContainer");

				Label lbl = new Label("value",
						AgreementGUIField.ADD_SCHEDULES.getDescription() + " :");
				HelperPanel panel = HelperPanel.getInstance(
						AgreementGUIField.ADD_SCHEDULES.getLabelId(), lbl);
				getLabels().put(AgreementGUIField.ADD_SCHEDULES, lbl);
				panel.setVisible(!isViewOnly());
				paySchedulerContainer.add(panel);
				paySchedulerContainer.add(getAddSchedulesPanel());
				paySchedulerContainer.add(getCalSchedulerGridPanel());
			}
			paySchedulerContainer.setOutputMarkupId(true);
			paySchedulerContainer.setOutputMarkupPlaceholderTag(true);

			return paySchedulerContainer;

		}

	}

	// AML Begin
	/**
	 * Create the modal window
	 * 
	 * @param id
	 * @return
	 */
	public ModalWindow createModalWindow(String id) {
		final ModalWindow window = new ModalWindow(id);

		// Create the page
		window.setPageCreator(new ModalWindow.PageCreator() {
			private static final long serialVersionUID = 1L;

			public Page createPage() {

				// Get the grid data to be displayed
				List<PaymentSchedulerDTO> dtoList = getGuiController()
						.getPaySchedHistoryDataList(
								getPageModelObject().getMaintainAgreementDTO()
										.getAgreementDTO().getId(),
								Calendar.getInstance().getTime(),
								getPageModelObject().getMaintainAgreementDTO()
										.getAgreementDTO().getStartDate());
				if (dtoList == null) {
					dtoList = new ArrayList<PaymentSchedulerDTO>();
				}
				DoNotPayHistoryViewPage dnpHistoryPage = new DoNotPayHistoryViewPage(
						"historyPage", getEditState(), getParentPage(), window,
						dtoList);
				dnpHistoryPage.setOutputMarkupId(true);
				return dnpHistoryPage;
			}

		});

		// Close window call back
		window.setWindowClosedCallback(new ModalWindow.WindowClosedCallback() {
			private static final long serialVersionUID = 1L;

			public void onClose(AjaxRequestTarget target) {
			}

		});

		window.setMinimalHeight(300);
		window.setInitialHeight(300);
		window.setMinimalWidth(600);
		window.setInitialWidth(600);
		window.setMaskType(MaskType.SEMI_TRANSPARENT);
		window.setCssClassName(ModalWindow.CSS_CLASS_GRAY);
		window.setOutputMarkupId(true);
		window.setOutputMarkupPlaceholderTag(true);

		return window;
	}

	/**
	 * create LBF Button Panel Distribution property field
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private HelperPanel getHistoryButtonPanel() {
		if (historyPanel == null) {

			Button button = new Button("value", Model.of("History"));
			button.setOutputMarkupId(true);
			button.setOutputMarkupPlaceholderTag(true);
			button.setVisible(true);
			button.add(new AjaxFormComponentUpdatingBehavior("click") {
				private static final long serialVersionUID = 1L;

				@Override
				protected void onUpdate(AjaxRequestTarget target) {

					addHistoryWindow.show(target);
				}
			});
			historyPanel = HelperPanel.getInstance(
					AgreementGUIField.PAYSCHED_HISTORY_BUTTON.getFieldId(),
					button);

			historyPanel.setOutputMarkupId(true);
			// Button will be visible on read mode and maintain
			button.setEnabled(getEditState() == EditStateType.MODIFY
					|| getEditState() == EditStateType.VIEW);
			historyPanel.setVisible(true);

		}
		return historyPanel;
	}

	// AML End

	private HelperPanel getAddSchedulesPanel() {

		if (addSchedPanel == null) {

			final SRSDateField field = new SRSDateField("value",
					new PropertyModel(getContext().getPaymentSchedulerDTO(),
							AgreementGUIField.ADD_SCHEDULES.getFieldId()));

			field.add(new AjaxFormComponentUpdatingBehavior("change") {
				private static final long serialVersionUID = 1L;

				@Override
				protected void onUpdate(AjaxRequestTarget target) {
					target.add(getFeedbackPanel());
					Date val = (Date) field.getConvertedInput();

					if (calScheduleDates != null
							&& calScheduleDates.contains(val)) {
						error("Payment Schedule for "
								+ DateUtil.formatDate(
										SRSAppWebConstants.DATE_FORMAT, val)
								+ " already exists !");
						target.add(getFeedbackPanel());
						return;
					}

					if (DateUtil.compareDates(val, new Date()) >= 0) {
						calScheduleDates.add(val);
						Collections.sort(calScheduleDates);
						Collections.reverse(calScheduleDates);
						getContext().getPaymentSchedulerDTO()
								.setAllPaymentScheduleColl(calScheduleDates);
						target.add(calSchedulerGridPanel);
					} else {
						error("Schedule Date prior to Current date !");
						target.add(getFeedbackPanel());
					}
				}
			});

			field.add(new AttributeModifier("size", "10"));
			field.add(new AttributeModifier("maxlength", "10"));
			field.setOutputMarkupId(true);
			field.setOutputMarkupPlaceholderTag(true);
			addSchedPanel = HelperPanel.getInstance(
					AgreementGUIField.ADD_SCHEDULES.getFieldId(), field, true);
		}

		/*
		 * if(addSchedPanel.getEnclosedObject() instanceof TextField) {
		 * TextField txtFld = (TextField)addSchedPanel.getEnclosedObject();
		 * txtFld.setEnabled(false); }
		 */

		addSchedPanel.setVisible(!isViewOnly());

		addSchedPanel.setOutputMarkupId(true);
		addSchedPanel.setOutputMarkupPlaceholderTag(true);
		//Santosh datepicker fix
				if (addSchedPanel.getEnclosedObject() instanceof SRSDateField) {
					addSchedPanel.getEnclosedObject().add(
				((SRSDateField)addSchedPanel.getEnclosedObject()).newDatePicker());
				}
		return addSchedPanel;

	}

	public Class getPanelClass() {
		return PaymentSchedulerPanel.class;
	}

	/* These 4 methods should be moved to BasePanel- */

	private WebMarkupContainer createFieldLabel(String id) {
		WebMarkupContainer container = new WebMarkupContainer(id);
		container.setOutputMarkupId(true);
		container.setOutputMarkupPlaceholderTag(true);
		return container;
	}

	private HelperPanel createGUIPageField(AgreementGUIField field,
			PaymentSchedulerDTO propertyObject, Component component) {
		HelperPanel ret = createPageField(field.getFieldId(),
				field.getDescription(), component, new EditStateType[] {
						EditStateType.ADD, EditStateType.MODIFY });
		fields.put(field, ret);
		return ret;
	}

	private HelperPanel createGUIPageField(AgreementGUIField field,
			PaymentSchedulerDTO propertyObject, ComponentType componentType,
			boolean ajaxUpdateValue) {

		HelperPanel helperPanel = null;

		switch (componentType) {

		case CHECKBOX:
			CheckBox checkBox = new CheckBox("value", new PropertyModel(
					propertyObject, field.getFieldId()));
			checkBox.setEnabled((getEditState().equals(EditStateType.MODIFY) && getViewTemplate()
					.isModifiable(field, getViewTemplateContext()))
					|| (getEditState().equals(EditStateType.ADD) && getViewTemplate()
							.isAddable(field, getViewTemplateContext())));
			checkBox.setOutputMarkupId(true);
			checkBox.setOutputMarkupPlaceholderTag(true);
			helperPanel = HelperPanel.getInstance(field.getFieldId(), checkBox);
			break;

		case LABEL:
			Label label = new Label("value", new PropertyModel(propertyObject,
					field.getFieldId()));
			label.setOutputMarkupId(true);
			label.setOutputMarkupPlaceholderTag(true);
			helperPanel = HelperPanel.getInstance(field.getFieldId(), label);
			break;

		case TEXTAREA:
			TextArea area = new TextArea("value", new PropertyModel(
					propertyObject, field.getFieldId()));
			area.setOutputMarkupId(true);
			area.setOutputMarkupPlaceholderTag(true);
			helperPanel = HelperPanel.getInstance(field.getFieldId(), area);
			break;

		default:
			helperPanel = createPageField(propertyObject,
					field.getDescription(), field.getFieldId(), componentType,
					getViewTemplate().isRequired(field, getContext()),
					ajaxUpdateValue, new EditStateType[] { EditStateType.ADD,
							EditStateType.MODIFY });
		}

		fields.put(field, helperPanel);
		helperPanel.setOutputMarkupId(true);
		helperPanel.setOutputMarkupPlaceholderTag(true);

		return helperPanel;
	}

	private HelperPanel createDropDownChoicePanel(AgreementGUIField field,
			SRSDropDownChoice dropDownChoice) {
		dropDownChoice.setOutputMarkupId(true);
		dropDownChoice.setRequired(getViewTemplate().isRequired(field,
				getContext()));
		dropDownChoice.setLabel(new Model(field.getDescription()));
		HelperPanel panel = createGUIPageField(field, getContext()
				.getPaymentSchedulerDTO(), dropDownChoice);
		dropDownChoice.setOutputMarkupId(true);
		dropDownChoice.setOutputMarkupPlaceholderTag(true);
		panel.setOutputMarkupId(true);
		panel.setOutputMarkupPlaceholderTag(true);
		return panel;
	}

	@Override
	protected ContextDrivenViewTemplate<AgreementGUIField, AgreementDTO> getViewTemplate() {
		return pageModel.getViewTemplate();
	}

	@Override
	protected AgreementDTO getViewTemplateContext() {
		return pageModel.getPreviousMaintainAgreementDTO().getAgreementDTO();
	}

	private FeedbackPanel getFeedbackPanel() {
		if (this.feedBackPanel == null) {
			Page page = getPage();
			if (page != null && page instanceof BasePage) {
				this.feedBackPanel = ((BasePage) page).getFeedbackPanel();

			}
		}

		return feedBackPanel;
	}

	@Override
	public EditStateType getEditState() {
		// will disable any modification if there are any requests pending auth
		if (existingPaySchedRequest) {
			return EditStateType.VIEW; 
		}
		return super.getEditState();
	}

	@Override
	protected boolean isView(EditStateType[] editableStates) {
		// will disable any modification if there are any requests pending auth
		if (existingPaySchedRequest) {
			return true;
		}
		return super.isView(editableStates);
	}

	private boolean isViewOnly() {
		if (getEditState() == EditStateType.VIEW
				|| getEditState() == EditStateType.AUTHORISE)
			return true;
		if (existingPaySchedRequest)
			return true;
		return false;
	}

	private PaymentSchedulerDTO getPreviousPaymentSchedulerDTO() {
		PaymentSchedulerDTO previousPaySchedulerDTO = new PaymentSchedulerDTO();
		if (pageModel == null
				|| pageModel.getPreviousMaintainAgreementDTO() == null
				|| pageModel.getPreviousMaintainAgreementDTO()
						.getAgreementDTO() == null)
			return previousPaySchedulerDTO;

		previousPaySchedulerDTO = pageModel.getPreviousMaintainAgreementDTO()
				.getAgreementDTO().getPaymentSchedulerDTO();

		return previousPaySchedulerDTO;

	}

	/* Market Integration 30/05/2016 SSM2707 Sweta Menon Begin */

	/**
	 * Method to obtain the allowed values of the Sales Categories that the BC
	 * must/can have to have the 'Do Not Pay' indicator to be set to TRUE as
	 * default.
	 */
	private List<String> getBCSalesCatAllowed() {
		if (bcAllowedSalesCategories == null
				|| bcAllowedSalesCategories.size() <= 0) {
			// write a query to obtain the allowed values.
			bcAllowedSalesCategories = getRatingEntityManager()
					.getBCSalesCatAllowed();
			return bcAllowedSalesCategories;
		} else {
			return bcAllowedSalesCategories;
		}
	}

	/**
	 * Get the rating entity manager
	 * 
	 * @return
	 */
	protected IRatingEntityManager getRatingEntityManager() {
		if (ratingEntityManager == null) {
			try {
				ratingEntityManager = ServiceLocator
						.lookupService(IRatingEntityManager.class);
			} catch (NamingException e) {
				throw new SystemException(
						"Could not find the RatingEntityManager, msg "
								+ e.getMessage(), 0, 0, e);
			}
		}
		return ratingEntityManager;
	}

	/* Market Integration 30/05/2016 SSM2707 Sweta Menon End */

	/**
	 * MBM0309 Prior year Tax Validation
	 * 
	 * @return
	 */
	private DateRange getPreviousLibertyTaxYearDateRange() {
		GregorianCalendar gc = new GregorianCalendar();
		gc.add(Calendar.YEAR, -1);
		Date result = gc.getTime();

		DateRange range = getRatingEntityManager().getTaxYearDates(result,
				TaxBasisConstants.TAX_YEAR_LIBERTY);
		return range;
	}
	
	/**
	 * Get agreement kind types for pre-issue status
	 * 
	 * @return
	 */
	private List<AgreementKindType> getTestListForPreIssueStatus() {
		List<AgreementKindType> list = new ArrayList<AgreementKindType>();
		list.add(AgreementKindType.AGENT);
		list.add(AgreementKindType.BROKER_BRANCH_FRANCHISE);
		list.add(AgreementKindType.DIRECT);
		list.add(AgreementKindType.FRANCHISE);
		list.add(AgreementKindType.BROKER_CONSULTANT);
		list.add(AgreementKindType.FRANCHISE_MANAGER);

		return list;
	}
}
