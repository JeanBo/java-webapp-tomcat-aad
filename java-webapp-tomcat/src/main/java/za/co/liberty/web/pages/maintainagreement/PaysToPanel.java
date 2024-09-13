package za.co.liberty.web.pages.maintainagreement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormChoiceComponentUpdatingBehavior;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.RadioChoice;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.form.validation.IFormValidator;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.convert.IConverter;

import com.inmethod.grid.DataProviderAdapter;
import com.inmethod.grid.IGridColumn;
import com.inmethod.grid.SizeUnit;

import za.co.liberty.business.guicontrollers.agreement.IAgreementGUIController;
import za.co.liberty.dto.agreement.AgreementDTO;
import za.co.liberty.dto.agreement.properties.PartyAccountDetailsDTO;
import za.co.liberty.dto.agreement.properties.PaysToDTO;
import za.co.liberty.dto.contracting.ResultAgreementDTO;
import za.co.liberty.dto.gui.context.ResultContextItemDTO;
import za.co.liberty.exceptions.data.ValidationException;
import za.co.liberty.helpers.lookup.ServiceLocator;
import za.co.liberty.web.data.enums.ComponentType;
import za.co.liberty.web.data.enums.ContextType;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.data.enums.fields.AgreementGUIField;
import za.co.liberty.web.pages.maintainagreement.model.PaysToPanelModel;
import za.co.liberty.web.pages.panels.GUIFieldPanel;
import za.co.liberty.web.pages.panels.HelperPanel;
import za.co.liberty.web.pages.panels.ViewTemplateBasePanel;
import za.co.liberty.web.pages.search.ContextSearchPopUp;
import za.co.liberty.web.wicket.convert.converters.agreement.ThirteenDigitConsCodeConverter;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGrid;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGrid.GridSizeUnit;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGridColumn;
import za.co.liberty.web.wicket.markup.repeater.data.ListDataProvider;
import za.co.liberty.web.wicket.validation.maintainagreement.PaysToFormValidator;
import za.co.liberty.web.wicket.validation.maintainagreement.PaysToValidator;
import za.co.liberty.web.wicket.view.ContextDrivenViewTemplate;

public class PaysToPanel extends ViewTemplateBasePanel<AgreementGUIField, AgreementDTO> {

	private PaysToPanelModel panelModel;
	
	private AgreementDTO viewTemplateContext;
	
	private WebMarkupContainer paysToContainer;
	
	private GUIFieldPanel paysToChoicePanel;
	
	private HelperPanel effectiveFromPanel;
	
	private Button accDetailButton;
	
	private SRSDataGrid linkedAccountGrid;
	
	private List<PartyAccountDetailsDTO> linkedAccountDetails;

	private boolean initialised;

	private transient IAgreementGUIController guiController;
	private FeedbackPanel feedbackPanel;
	private ModalWindow agreementSearchWindow;
	
	
	private PaysToForm pageForm;

	private transient Logger transLogger;

	private ThirteenDigitConsCodeConverter consCodeConverter;
	
	
	// Internal validator
	class PaysToFormValidator implements IFormValidator {

		private static final long serialVersionUID = 6888657645247696068L;

		private FormComponent paysToFormComponent;
		
		private PaysToValidator paysToValidator;

		private EditStateType editState;

		private AgreementDTO paysToContainer;
		
		
		protected Collection<FormComponent> validationComponents = new ArrayList<FormComponent>();
		
		public PaysToFormValidator(
				EditStateType editState,
				Long agreementId,
				AgreementDTO paysToContainer,
				FormComponent paysToFormComponent) {
			super();
			
			this.editState=editState;
			this.paysToFormComponent=paysToFormComponent;
			this.paysToContainer=paysToContainer;
			validationComponents.add(paysToFormComponent);
			paysToValidator = new PaysToValidator(agreementId);
		}

		public FormComponent[] getDependentFormComponents() {
			return null;
		}

		public void validate(Form arg0) {
			System.out.println("PaysToPanel.validate " + getEditState());
			if (editState==EditStateType.VIEW) {
				return;
			}
			
			if (getEditState().isAdd()) {
				getLogger().info("Start validation validate.paysToPanel");
//				validateFormComponents(validationComponents, feedbackPanel);
				getLogger().info("End validation validate.paysToPanel");
			}
			/**
			 * Validate Pays To On Form Submit, rather than in the ajax behaviour
			 */
			try {
				paysToValidator.validatePaysToDTO(paysToContainer.getPaymentDetails());
			} catch (ValidationException e) {
				if (paysToFormComponent!=null) {
					arg0.error(e.getErrorMessages().get(0));
				}
			}
		}

	}
	
	/**
	 * This anonymous inner class represents the converter to display
	 * a null/0 agreement number as "None Selected", and a non-null and
	 * non-zero agreement number as is.
	 */
	private IConverter agreementNumberConverter = new IConverter() {
		public Object convertToObject(String value, Locale locale) {
			if (value==null || value.length()==0) {
				return 0L;
			}
			try {
				return Long.parseLong(value);
			} catch (NumberFormatException e) {
				return 0L;
			}
		}

		public String convertToString(Object value, Locale locale) {
			if (value!=null && value instanceof Long) {
				Long val = ((Long)value);
				if (val!=null && val!=0) {
					return ""+val;
				}
			}
			return "None Selected";
		}
		
	};

	

	
	/**
	 * Default constructor 
	 * 
	 * @param id
	 * @param editState
	 * @param panelModel
	 * @param feedbackPanel
	 */
	public PaysToPanel(String id, EditStateType editState, PaysToPanelModel panelModel,
			FeedbackPanel feedbackPanel) {
		super(id, editState);
		this.panelModel = panelModel;
	}
	
	/**
	 * This class represents the page form to be added to the panel
	 * @author kxd1203
	 */
	private class PaysToForm extends Form {

		public PaysToForm(String id) {
			super(id);
			initComponents();
		}

		/**
		 * Add all components to the form
		 */
		private void initComponents() {
			/**
			 * delegated form validation
			 */
			add(new PaysToFormValidator(
					getEditState(),
					panelModel.getAgreementID(),
					getContext(),
					(FormComponent) getPaysToChoicePanel().getComponent()));
			/**
			 * Add components
			 */
			add(getPaysToContainer());
			/**
			 * Check field visibility
			 */
			checkFieldVisibility();
			
		}
	}
	
	@Override
	protected void checkFieldVisibility() {
		super.checkFieldVisibility();
		boolean showLinkedDetails = (getContext()!=null &&
				getContext().getPaymentDetails()!=null &&
				getContext().getPaymentDetails().getPayto()!=null && 
				getContext().getPaymentDetails().getPayto().equals(
						PaysToDTO.PayToType.ORGANISATION));
		getLinkedAccountDetails().setVisible(showLinkedDetails);
		getPaysToContainer().setVisible(
				isVisible(AgreementGUIField.PAY_TO_CHOICE) ||
				isVisible(AgreementGUIField.PAY_TO_EFFECTIVE_FROM));
		/** bradley requested the account details be hidden... */
		getAccDetailsButton().setVisible(false);
	}
	
	/**
	 * Load the AgreementGUIController dynamically if it is null as this is a transient variable.
	 * @return {@link IAgreementGUIController}
	 */
	private IAgreementGUIController getGuiController() {
		if (guiController==null) {
			try {
				guiController = ServiceLocator.lookupService(IAgreementGUIController.class);
			} catch (NamingException e) {
				getLogger().fatal("Could not lookup AgreementGUIController",e);
			}
		}
		return guiController;
	}
	
	private Logger getLogger() {
		if (transLogger == null) {
			transLogger = Logger.getLogger(this.getClass());
		}
		return transLogger;
	}
	
	

	@Override
	protected boolean isProcessOutstandingRequestsAllowed() {
		return false;
	}

	/**
	 * Load the components on the page on first render, 
	 * so that the components are only generated when the page is displayed 
	 */
	@Override
	protected void onBeforeRender() {
		super.onBeforeRender();
		if (!initialised) {
			if (getLogger().isDebugEnabled()) {
				getLogger().debug("Adding components to the page on first render");
			}
			add(getSearchWindow());
			add(getPaysToForm());
			initialised=true;
		}
	}
	
	/**
	 * Get the main page form
	 * @return
	 */
	private PaysToForm getPaysToForm() {
		if (pageForm==null) {
			pageForm = new PaysToForm("paysToForm");
		}
		return pageForm;
	}
	
	public WebMarkupContainer getPaysToContainer() {
		if (paysToContainer == null) {
			paysToContainer = new WebMarkupContainer("paysToContainer");
			RepeatingView leftPanel = new RepeatingView("leftPanel");
			paysToContainer.add(leftPanel);
			/**
			 * Left panel content
			 */
			leftPanel.add(getPaysToChoicePanel());		// lblPayTo
			leftPanel.add(createGUIFieldPanel(AgreementGUIField.PAY_TO_EFFECTIVE_FROM, null,  
					getEffectiveFromPanel().getEnclosedObject(),true));
//			leftPanel.add(getEffectiveFromPanel());
			/**
			 * General content
			 */
			paysToContainer.add(getAccDetailsButton());
			paysToContainer.add(getLinkedAccountDetails());
//			paysToContainer.add(getEffectiveFromPanel());
		}
		return paysToContainer;
	}
	
	/**
	 * Get the choices panel for the Pays to choices
	 * @return the choices panel
	 */
	@SuppressWarnings("serial")
	private GUIFieldPanel getPaysToChoicePanel() {
		if (paysToChoicePanel==null) {
			
			AgreementGUIField field = AgreementGUIField.PAY_TO_CHOICE;   // paysTo  - ID
			/**
			 * Determine from the view template if PAYS TO ORG is allowed in the current context
			 */
			boolean payToOrgAllowed = getViewTemplate().isViewable(AgreementGUIField.PAY_TO_ORGANISATION, 
					getEditState(), 
					getViewTemplateContext());
			PropertyModel model = new PropertyModel(getContext(),"paymentDetails");
			/**
			 * Valid PAYS TO choices
			 */
			List<PaysToDTO> validPaysToChoices = panelModel.getValidPaysToValues();
			if (!payToOrgAllowed) {//IF PAY TO ORG IS NOT ALLOWED
				/**
				 * Safely remove the PAYS TO ORG from the list 
				 * of valid values using an iterator
				 */
				for (Iterator<PaysToDTO> iter = validPaysToChoices.iterator();iter.hasNext();) {
					PaysToDTO next = iter.next();
					if (next.getPayto()==PaysToDTO.PayToType.ORGANISATION) {
						iter.remove();
						break;
					}
				}
			}
			final RadioGroup group = new RadioGroup("placeholdergroup");
			
			RadioChoice paysToChoice = new RadioChoice("panel", model, 
					validPaysToChoices) {
				@Override
				protected void onComponentTag(ComponentTag tag) {
					super.onComponentTag(tag);
					tag.put("colspan", 2);
					tag.put("style", "padding-left: 50px; padding-right: 50px;padding-bottom:15px;");
				}
			};
			Behavior changeBehaviour = new AjaxFormChoiceComponentUpdatingBehavior() {
				@Override
				protected void onUpdate(AjaxRequestTarget target) {
					checkFieldVisibility();
					/**
					 * Update the model so that effective from only changes
					 * when there is a valid change from the previous 
					 * pays to type
					 */
					panelModel.updateModelForMaintainPaysTo();
					target.add(getLinkedAccountDetails());
					target.add(getEffectiveFromPanel().getEnclosedObject());
				}
				
			};
			paysToChoice.add(changeBehaviour);
			EditStateType[] allowedStates = getViewTemplate().getEditStates(field, getContext());
			List<EditStateType> allowedStateList = Arrays.asList(allowedStates);
			paysToChoice.setEnabled(allowedStateList.contains(getEditState()));
			paysToChoice.setRequired(getViewTemplate().isRequired(AgreementGUIField.PAY_TO_CHOICE, getViewTemplateContext()));
			paysToChoice.setLabel(new Model(AgreementGUIField.PAY_TO_CHOICE.getDescription()));
//			if (payToOrgAllowed) {
//				Radio radio = new Radio("radio1",model){
//					@Override
//					protected RadioGroup getGroup() {
//						return group;
//					}					
//				};
//				radio.setEnabled(allowedStateList.contains(getEditState()));
//				radio.setOutputMarkupId(true);
//				radio.setOutputMarkupPlaceholderTag(true);
//				paysToChoice.add(radio);
//			}
			paysToChoice.setOutputMarkupId(true);
			paysToChoice.setOutputMarkupPlaceholderTag(true);
			Label label = new Label("label",new Model(""));
			label.setVisible(false);
			paysToChoicePanel = new GUIFieldPanel("paysTo",label,paysToChoice);
			paysToChoicePanel.setOutputMarkupId(true);
			paysToChoicePanel.setOutputMarkupPlaceholderTag(true);
			getFields().put(field, paysToChoicePanel);
		}
		return paysToChoicePanel;
	}
	
	/**
	 * Get the account details button
	 * @return the button
	 */
	private Button getAccDetailsButton() {
		if (accDetailButton==null) {
			accDetailButton = new Button("accDetails");
			accDetailButton.add(new AjaxFormComponentUpdatingBehavior("click") {
				@Override
				protected void onUpdate(AjaxRequestTarget target) {
					
				}
			});
			accDetailButton.setOutputMarkupId(true);
		}
		return accDetailButton;
	}
	
	/**
	 * Get the data grid for the linked details for the pays to organisation type
	 * @return the data grid
	 */
	private SRSDataGrid getLinkedAccountDetails() {
		if (linkedAccountGrid==null) {
			linkedAccountDetails = getPartyAccountDetailsList();
			linkedAccountGrid = new SRSDataGrid("agreementDetails", new DataProviderAdapter(
					new ListDataProvider<PartyAccountDetailsDTO>(linkedAccountDetails)),
					getAccountColumns(), getEditState(),null);
			linkedAccountGrid.setOutputMarkupId(true);
			linkedAccountGrid.setOutputMarkupPlaceholderTag(true);
			linkedAccountGrid.setCleanSelectionOnPageChange(false);
			linkedAccountGrid.setClickRowToSelect(false);
			linkedAccountGrid.setAllowSelectMultiple(false);
			linkedAccountGrid.setGridWidth(99, GridSizeUnit.PERCENTAGE);
			linkedAccountGrid.setRowsPerPage(1);
			linkedAccountGrid.setContentHeight(40, SizeUnit.PX);
		}
		return linkedAccountGrid;
	}
	
	/**
	 * get the linked details for the pays to organisation type
	 * @return a list of {@link PartyAccountDetailsDTO} that represents the linked details for the 
	 * pays to organisation type
	 */
	private List<PartyAccountDetailsDTO> getPartyAccountDetailsList() {
		if (linkedAccountDetails == null) {
			linkedAccountDetails = new ArrayList<PartyAccountDetailsDTO>();
		}
		linkedAccountDetails.clear();
		if (getContext()==null || getContext().getPaymentDetails()==null ||
				getContext().getPaymentDetails().getOrgAgreementNumber()==null || 
				getContext().getPaymentDetails().getOrgAgreementNumber()==0) {
			linkedAccountDetails.add(new PartyAccountDetailsDTO());
			return linkedAccountDetails;
		}
		PartyAccountDetailsDTO acc = 
			getGuiController().getPartyAccountDetails(getContext().getPaymentDetails().getOrgAgreementNumber());
		if (acc==null) {
			linkedAccountDetails.add(new PartyAccountDetailsDTO());
			return linkedAccountDetails;
		} else {
			linkedAccountDetails.add(acc);
		}
		return linkedAccountDetails;
	}
	
	/**
	 * Get the columns for the linked details for the pays to organisation type
	 * @return the data columns
	 */
	@SuppressWarnings("serial")
	private List<IGridColumn> getAccountColumns() {
		List<IGridColumn> ret = new ArrayList<IGridColumn>();
		/**
		 * Search Column
		 */
		EditStateType[] allowedStates = getViewTemplate().getEditStates(
				AgreementGUIField.PAY_TO_CHOICE, 
				getContext());
		final List<EditStateType> allowedStateList = Arrays.asList(allowedStates);
		if (allowedStateList.contains(getEditState())) {
			SRSDataGridColumn<PartyAccountDetailsDTO> searchCol =
				new SRSDataGridColumn<PartyAccountDetailsDTO>(
						"search",new Model("Search"),"search",
						getEditState()) {
				@Override
				public Panel newCellPanel(WebMarkupContainer parent, String componentId, IModel rowModel, String objectProperty, EditStateType state, PartyAccountDetailsDTO data) {
					/**
					 * Search column contains a button that will display the modal context search window
					 */
					Button searchButton = new Button("value",new Model("Search"));
					searchButton.add(new AjaxFormComponentUpdatingBehavior("click") {
						@Override
						protected void onUpdate(AjaxRequestTarget target) {
							agreementSearchWindow.show(target);
						}
					});
					searchButton.setEnabled(allowedStateList.contains(getEditState()));
					return HelperPanel.getInstance(componentId, searchButton);
				}
				
			};
			searchCol.setInitialSize(75);
			ret.add(searchCol);
		}
		/**
		 * Agreement Number Column
		 */
		SRSDataGridColumn<PartyAccountDetailsDTO> agmntNumber = 
			new SRSDataGridColumn<PartyAccountDetailsDTO>(
				"agmntNumber", new Model("Agmnt Number"), 
				"agreementNumber",getEditState()) {
			@Override
			public Panel newCellPanel(WebMarkupContainer parent, String componentId, IModel rowModel, String objectProperty, EditStateType state, PartyAccountDetailsDTO data) {
				/**
				 * Agreement number column will have a custom label with a renderer to render
				 * no selection as "None Selected" 
				 */
				Label lbl = new Label("value",new PropertyModel(data,objectProperty)) {
					@Override
					public IConverter getConverter(Class targetClass) {
						return agreementNumberConverter;
					}
				};
				return HelperPanel.getInstance(componentId, lbl);
			}
		};
		agmntNumber.setMinSize(100);
		agmntNumber.setInitialSize(100);
		ret.add(agmntNumber);
		/**
		 * Consultant Code Column
		 */
		SRSDataGridColumn<PartyAccountDetailsDTO> consCode = 
			new SRSDataGridColumn<PartyAccountDetailsDTO>(
				"consultantCode", new Model("Consultant Code"), 
				"consultantCode",getEditState()) {
			@Override
			public Panel newCellPanel(WebMarkupContainer parent, String componentId, IModel rowModel, String objectProperty, EditStateType state, PartyAccountDetailsDTO data) {
				/**
				 * Agreement number column will have a custom label with a renderer to render
				 * no selection as "None Selected" 
				 */
				Label lbl = new Label("value",new PropertyModel(data,objectProperty)) {
					@Override
					public IConverter getConverter(Class targetClass) {
						return getConsCodeConverter();
					}
					
				};
				return HelperPanel.getInstance(componentId, lbl);
			}
		};
		consCode.setMinSize(100);
		consCode.setInitialSize(100);
		ret.add(consCode);
		/**
		 * Party Name Column
		 */
		SRSDataGridColumn<PartyAccountDetailsDTO> nameCol = 
			new SRSDataGridColumn<PartyAccountDetailsDTO>(
				"name", new Model("Name"), 
				"name",getEditState());
		nameCol.setMinSize(200);
		nameCol.setInitialSize(200);
		ret.add(nameCol);
		/**
		 * Agreement Status Column
		 */
		ret.add(new SRSDataGridColumn<PartyAccountDetailsDTO>(
				"status", new Model("Status"), 
				"status",getEditState()));
		return ret;
	}
	
	private ThirteenDigitConsCodeConverter getConsCodeConverter() {
		if (consCodeConverter == null) {
			consCodeConverter = new ThirteenDigitConsCodeConverter();
		}
		return consCodeConverter;
	}
	
	/**
	 * Get the modal agreement search window 
	 * @return the modal window
	 */
	private ModalWindow getSearchWindow() {
		if (agreementSearchWindow==null) {

			ContextSearchPopUp popUp = new ContextSearchPopUp() {
	
				@Override
				public ContextType getContextType() {
					return ContextType.AGREEMENT_ONLY; 
				}
	
				@Override
				public void doProcessSelectedItems(AjaxRequestTarget target,
						ArrayList<ResultContextItemDTO> selectedItemList) {
					if (selectedItemList.size() == 0) {
						// Nothing was selected
						return;
					}					
					//adding party to agreement
					for (ResultContextItemDTO contextItemDTO : selectedItemList) {
						ResultAgreementDTO resultAgreementDTO = 
							contextItemDTO.getAgreementDTO();
						if (resultAgreementDTO!=null) {
							getContext().getPaymentDetails().setOrgAgreementNumber(resultAgreementDTO.getAgreementNumber());					
						} else {
							getContext().getPaymentDetails().setOrgAgreementNumber(0L);
						}
						linkedAccountDetails = getPartyAccountDetailsList();
						/**
						 * Update the model so that effective from only changes
						 * when there is a valid change from the previous 
						 * pays to type
						 */
						panelModel.updateModelForMaintainPaysTo();
						target.add(getLinkedAccountDetails());
						target.add(getEffectiveFromPanel().getEnclosedObject());
					}
					
				}
			};		
			agreementSearchWindow = popUp.createModalWindow("agreementSearchWindow");
//			agreementSearchWindow.setPageMapName("agreementSearchPageMap");
		}
		return agreementSearchWindow;	
	}
	
	/**
	 * Get the effective from date panel 
	 * @return the effective from panel
	 */
	private HelperPanel getEffectiveFromPanel() {
		if (effectiveFromPanel==null) {
			effectiveFromPanel = createGUIPageField(AgreementGUIField.PAY_TO_EFFECTIVE_FROM,
					getViewTemplateContext(),
					ComponentType.DATE_SELECTION_TEXTFIELD, true);
			effectiveFromPanel.setOutputMarkupId(true);
			effectiveFromPanel.getEnclosedObject().setOutputMarkupId(true);
			effectiveFromPanel.getEnclosedObject().setOutputMarkupPlaceholderTag(true);
			System.out.println("Enclosed object = " + effectiveFromPanel.getEnclosedObject());
			System.out.println("Parent panel = " + effectiveFromPanel);
		}
		return effectiveFromPanel;
	}

	@Override
	protected ContextDrivenViewTemplate<AgreementGUIField, AgreementDTO> getViewTemplate() {
		return panelModel.getViewTemplate();
	}

	@Override
	protected AgreementDTO getViewTemplateContext() {
		if (viewTemplateContext == null) {
			viewTemplateContext = panelModel.getPaysToContainer();
		}
		return viewTemplateContext;
	}
	
	private AgreementDTO getContext() {
		return getViewTemplateContext();
	}
	
}
