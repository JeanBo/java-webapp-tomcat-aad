package za.co.liberty.web.pages.core;

import java.util.ArrayList;
import java.util.List;

import javax.naming.NamingException;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.ValidationError;

import za.co.liberty.business.guicontrollers.core.ICoreTransferGuiController;
import za.co.liberty.dto.agreement.AgreementDTO;
import za.co.liberty.dto.agreement.core.CoreConsultantDto;
import za.co.liberty.dto.agreement.core.CoreTransferDto;
import za.co.liberty.dto.contracting.ResultAgreementDTO;
import za.co.liberty.dto.contracting.ResultPartyDTO;
import za.co.liberty.dto.gui.context.ResultContextItemDTO;
import za.co.liberty.dto.userprofiles.ContextDTO;
import za.co.liberty.exceptions.data.DataNotFoundException;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.helpers.lookup.ServiceLocator;
import za.co.liberty.interfaces.core.CoreTransferRequestType;
import za.co.liberty.web.data.enums.ContextType;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.pages.core.model.CoreTransferPageModel;
import za.co.liberty.web.pages.panels.BasePanel;
import za.co.liberty.web.pages.search.ContextSearchPopUp;
import za.co.liberty.web.system.SRSAuthWebSession;
import za.co.liberty.web.wicket.markup.html.form.SRSAbstractChoiceRenderer;

/**
 * Panel used to perform the Core option - Book level transfer
 * 
 *
 */
public class BookTransferPanel extends BasePanel {

	protected Form searchForm;

	private FeedbackPanel feedbackPanel;

	private CoreTransferPageModel pageModel;

	private static final String COMP_SEARCH_FORM = "searchForm";

	private ModalWindow searchWindow;

	protected Component requestKindChoice;

	private RepeatingView fromSearchPanel;

	private ResultAgreementDTO resultAgreementDTO;

	private CoreTransferDto coreTransferDto;

	private Component fromConsultantNameField;

	private Component fromConsultantID;

	private Component fromConsultantStatus;

	private Component toConsultantNameField;

	private Component toConsultantID;

	private Component toConsultantStatus;

	private CheckBox advisorCheckBox;

	private ResultPartyDTO partyDto;

	private AgreementDTO agreementDTO = null;
	
	private EditStateType editState;

	private transient ICoreTransferGuiController guiController;

	public BookTransferPanel(String tab_panel_id, CoreTransferPageModel model,
			EditStateType editState, FeedbackPanel feedBackPanel) {
		super(tab_panel_id, editState);
		
		this.editState=editState;
		pageModel = model;
		this.feedbackPanel = feedBackPanel;
		this.guiController = guiController;
		
		resultAgreementDTO = new ResultAgreementDTO();
		List<CoreTransferDto> coreTransferDtoList = pageModel
				.getCoreTransferDto();
		
		coreTransferDto = new CoreTransferDto();
		if(!editState.isViewOnly()){
				coreTransferDtoList = new ArrayList<CoreTransferDto>();
			
				coreTransferDtoList.add(0, coreTransferDto);
				pageModel.setCoreTransferDto(coreTransferDtoList);

				ContextDTO contextDto =SRSAuthWebSession.get().getContextDTO(); 
				if (contextDto != null
						&& contextDto.getAgreementContextDTO().getConsultantCode() != null) {
					
					ContextDTO dto = SRSAuthWebSession.get().getContextDTO();					
					CoreConsultantDto consultantDto;
					try {
						consultantDto = getGUIController().getCoreConsultantDto(
								contextDto.getAgreementContextDTO().getAgreementNumber());
						pageModel.getConsultantMap().put(consultantDto.getConsultantCode(), consultantDto);
						new CoreHelper().setFromConsultant(consultantDto, coreTransferDto);
					} catch (DataNotFoundException e) {
						// This shouldn't be possible
						throw new CommunicationException(e);
					}
				
				}
		}else{
			coreTransferDto=new CoreTransferDto(model.getCoreTransferDto().get(0));

			if(coreTransferDto.getTransferTypeInd().equalsIgnoreCase("S")){
				pageModel.setRequestCategory(CoreTransferRequestType.Service_Transfer);
			}else if(coreTransferDto.getTransferTypeInd().equalsIgnoreCase("C")){
				pageModel.setRequestCategory(CoreTransferRequestType.Service_Commision_Transfer);
			}
		}

		add(searchForm = new createSearchForm("searchForm"));
		add(searchWindow = createSearchWindow("searchPartyWindow"));
	}

	private class createSearchForm extends Form {

		public createSearchForm(String id) {
			super(id);
			add(requestKindChoice = createRequestKindChoice("requestKind"));
			add(createContextSearchButton("contextSearchButton"));
			add(advisorCheckBox = getAdvisoryFeePanel());
			add(fromConsultantNameField = getFromConsultantName());
			add(fromConsultantID = getFromConsultantID());
			add(fromConsultantStatus = getFromConsultantStatus());
			add(toConsultantNameField = getToConsultantName());
			add(toConsultantID = getToConsultantID());
			add(toConsultantStatus = getToConsultantStatus());
			// add(createSaveButton("saveButton"));
		}

	}
	
	
	private Component createRequestKindChoice(String id) {


		IModel model = new IModel() {
			private static final long serialVersionUID = 1L;

			public Object getObject() {
				return pageModel.getRequestCategory();
			}

			public void setObject(Object arg0) {
				
				pageModel.setRequestCategory((CoreTransferRequestType) arg0);
			}

			public void detach() {
			}
		};

		DropDownChoice field = new DropDownChoice(id, model, pageModel
				.getRequestCategoryDTO(), new SRSAbstractChoiceRenderer<Object>() {
			
			private static final long serialVersionUID = -4367276358153378234L;

			public Object getDisplayValue(Object value) {
				return (value == null) ? null
						: ((CoreTransferRequestType) value).getName();
			}

			public String getIdValue(Object arg0, int arg1) {
				return arg1 + "";
			}
		});

		// Update the request kinds field when changing this one
		AjaxFormComponentUpdatingBehavior behaviour = new AjaxFormComponentUpdatingBehavior(
				"change") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				// The request category has changed, update the list of request
				// kinds.
				CoreTransferRequestType dto = pageModel.getRequestCategory();
				if (dto != null && dto.name() != null) {
					pageModel.setTransferType(String.valueOf(dto.getId()));
				} else {
					pageModel.setTransferType("");
				}
			}

		};
		field.add(behaviour);
		field.setNullValid(true);
		
		if(editState.isViewOnly()){
			field.setEnabled(false);
		}
		return field;
	}

	private CheckBox getAdvisoryFeePanel() {
		CheckBox advisorCheckBox=null;
		if(editState.isViewOnly()){
			if(coreTransferDto.getAdvisoryFeeIndicator()){
				advisorCheckBox = new CheckBox("advisoryFee", Model.of(Boolean.TRUE));
			}else{
				advisorCheckBox = new CheckBox("advisoryFee", Model.of(Boolean.FALSE));
			}
			advisorCheckBox.setEnabled(false);
		}else{
			advisorCheckBox = new CheckBox("advisoryFee", Model.of(Boolean.FALSE));
			advisorCheckBox.setEnabled(true);
		}
		
		advisorCheckBox.add(new AjaxFormComponentUpdatingBehavior("click") {

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				advisorCheckEvent();
			}
		});
		
		
		return advisorCheckBox;
	}

	private void advisorCheckEvent() {
		coreTransferDto.setAdvisoryFeeIndicator(advisorCheckBox
				.getModelObject());
	}

	private Component getFromConsultantName() {
		PropertyModel propertyModel = new PropertyModel(this,
				"coreTransferDto.fromConsultantName");
		Label viewLabel = new Label("fromConsultantName", propertyModel);
		viewLabel.setOutputMarkupId(true);
		return viewLabel;
	}

	private Component getFromConsultantID() {
		PropertyModel propertyModel = new PropertyModel(this,
				"coreTransferDto.fromConsultantCode");
		Label viewLabel = new Label("fromConsultantID", propertyModel);
		viewLabel.setOutputMarkupId(true);
		return viewLabel;
	}

	private Component getFromConsultantStatus() {
		PropertyModel propertyModel = new PropertyModel(this,
				"coreTransferDto.fromConsultantStatus");
		Label viewLabel = new Label("fromConsultantStatus", propertyModel);
		viewLabel.setOutputMarkupId(true);
		return viewLabel;
	}

	private Component getToConsultantName() {
		PropertyModel propertyModel = new PropertyModel(this,
				"coreTransferDto.toConsultantName");
		Label viewLabel = new Label("toConsultantName", propertyModel);
		viewLabel.setOutputMarkupId(true);
		return viewLabel;
	}

	private Component getToConsultantID() {
		PropertyModel propertyModel = new PropertyModel(this,
				"coreTransferDto.toConsultantCode");
		Label viewLabel = new Label("toConsultantID", propertyModel);
		viewLabel.setOutputMarkupId(true);
		return viewLabel;
	}

	private Component getToConsultantStatus() {
		PropertyModel propertyModel = new PropertyModel(this,
				"coreTransferDto.toConsultantStatus");
		Label viewLabel = new Label("toConsultantStatus", propertyModel);
		viewLabel.setOutputMarkupId(true);
		return viewLabel;
	}

	private ModalWindow createSearchWindow(String id) {
		ContextSearchPopUp popUp = new ContextSearchPopUp() {
			@Override
			public ContextType getContextType() {
				return ContextType.AGREEMENT_ONLY;
			}

			@Override
			public void doProcessSelectedItems(AjaxRequestTarget target,
					ArrayList<ResultContextItemDTO> selectedItemList) {
				if (selectedItemList.size() == 0) {
					return;
				}
				
				// adding party to agreement
				resultAgreementDTO = (selectedItemList.get(0))
						.getAgreementDTO();
				partyDto = (selectedItemList.get(0)).getPartyDTO();
				
				// Check valid
				if (resultAgreementDTO == null || resultAgreementDTO.getAgreementNumber() == null 
						|| resultAgreementDTO.getAgreementNumber().equals(0L)) {
					warn("Standalone parties can not be selected");
					target.add(feedbackPanel);
					return;
				}
				
				// Retrieve the cons code dto
				CoreConsultantDto consultantDto = pageModel.getConsultantMap().get(resultAgreementDTO.getConsultantCode());
				if (consultantDto == null) {
					consultantDto = getGUIController().getCoreConsultantDto(resultAgreementDTO, partyDto);
					pageModel.getConsultantMap().put(consultantDto.getConsultantCode(), consultantDto);
				}
				
				new CoreHelper().setToConsultant(consultantDto, coreTransferDto);
				
				target.add(toConsultantNameField);
				target.add(toConsultantID);
				target.add(toConsultantStatus);
			}
		};
		ModalWindow win = popUp.createModalWindow(id);
//		win.setPageMapName("homeSearchPageMap");
		return win;
	}
	


	protected Button createContextSearchButton(String id) {
		Button but = new AjaxButton(id) {
			private static final long serialVersionUID = 1L;

			@SuppressWarnings("unchecked")
			@Override
			protected void onSubmit(AjaxRequestTarget target, Form form) {
				searchWindow.show(target);
			}
		};
		if(editState.isViewOnly())
			but.setEnabled(false);
		
		return but;
	}

	protected Button createSaveButton(String id) {
		Button but = new AjaxButton(id) {
			private static final long serialVersionUID = 1L;

			@SuppressWarnings("unchecked")
			@Override
			protected void onSubmit(AjaxRequestTarget target, Form form) {
			}
		};
		but.setOutputMarkupId(true);
		but.add(new IValidator() {
			private static final long serialVersionUID = 1L;

			@Override
			public void validate(IValidatable val) {
				doSearchButtonValidation(val);
			}
		});
		return but;
	}

	protected void doSearchButtonValidation(IValidatable val) {
		if (pageModel.getTransferType() == null
				|| pageModel.getTransferType().equals("")) {
			val
					.error(new ValidationError()
							.addKey(CoreTransferValidationType.REQUIRE_TRANSFER_TYPE
									.getMessageKey()));
		}
		if (coreTransferDto.getFromConsultantCode() == null
				|| coreTransferDto.getFromConsultantCode().equals("")) {
			val
					.error(new ValidationError()
							.addKey(CoreTransferValidationType.REQUIRE_FROM_CONSULTANT
									.getMessageKey()));
		}
		if (coreTransferDto.getToConsultantCode() == null
				|| coreTransferDto.getToConsultantCode().equals("")) {
			val
					.error(new ValidationError()
							.addKey(CoreTransferValidationType.REQUIRE_TO_CONSULTANT
									.getMessageKey()));
		}
		AjaxRequestTarget target = RequestCycle.get().find(AjaxRequestTarget.class);
		if (target!=null)
			target.add(feedbackPanel);
	}
	
	/**
	 * Get the GUI Controller
	 * 
	 * @return
	 */
	protected ICoreTransferGuiController getGUIController() {
		if (guiController == null) {
			try {
				guiController = ServiceLocator
						.lookupService(ICoreTransferGuiController.class);
			} catch (NamingException e) {
				throw new CommunicationException(
						"Naming exception looking up CoreTransferGUIController",
						e);
			}
		}
		return guiController;
	}
}
