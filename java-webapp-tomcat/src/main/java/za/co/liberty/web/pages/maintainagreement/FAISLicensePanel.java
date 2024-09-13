package za.co.liberty.web.pages.maintainagreement;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxCallListener;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow.MaskType;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.validation.IFormValidator;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.convert.IConverter;

import za.co.liberty.business.guicontrollers.agreement.IAgreementGUIController;
import za.co.liberty.common.domain.TypeVO;
import za.co.liberty.common.domain.util.FAISCategoryTypeVOComparator;
import za.co.liberty.constants.ISRSConstants;
import za.co.liberty.dto.agreement.AgreementDTO;
import za.co.liberty.dto.agreement.AgreementRoleDTO;
import za.co.liberty.dto.agreement.maintainagreement.fais.FAISLicensePanelGridDTO;
import za.co.liberty.dto.agreement.properties.FAISLicenseDTO;
import za.co.liberty.dto.contracting.ResultPartyDTO;
import za.co.liberty.dto.party.contactdetail.AddressDTO;
import za.co.liberty.dto.party.fais.FAISLicenseCategoryDTO;
import za.co.liberty.dto.party.fais.supervision.FAISCategorySupervisionDTO;
import za.co.liberty.dto.userprofiles.ISessionUserProfile;
import za.co.liberty.exceptions.data.DataNotFoundException;
import za.co.liberty.exceptions.data.ValidationException;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.helpers.lookup.ServiceLocator;
import za.co.liberty.helpers.util.DateUtil;
import za.co.liberty.interfaces.agreements.requests.RequestKindType;
import za.co.liberty.interfaces.party.PartyStatusType;
import za.co.liberty.web.data.enums.ComponentType;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.data.enums.fields.AgreementGUIField;
import za.co.liberty.web.data.pages.IModalMaintenancePageModel;
import za.co.liberty.web.pages.interfaces.IHasAccessPanel;
import za.co.liberty.web.pages.interfaces.ISecurityPanel;
import za.co.liberty.web.pages.maintainagreement.fais.ExperiencePanel;
import za.co.liberty.web.pages.maintainagreement.fais.FSPFAISLicenseDetailsPage;
import za.co.liberty.web.pages.maintainagreement.fais.SuperVisionPage;
import za.co.liberty.web.pages.maintainagreement.fais.SuperVisionPanel;
import za.co.liberty.web.pages.maintainagreement.fais.SupervisorPage;
import za.co.liberty.web.pages.maintainagreement.model.ExperiencePanelModel;
import za.co.liberty.web.pages.maintainagreement.model.FAISLicensePanelModel;
import za.co.liberty.web.pages.maintainagreement.model.MaintainAgreementPageModel;
import za.co.liberty.web.pages.panels.GUIFieldPanel;
import za.co.liberty.web.pages.panels.HelperPanel;
import za.co.liberty.web.pages.panels.ViewTemplateBasePanel;
import za.co.liberty.web.system.SRSAuthWebSession;
import za.co.liberty.web.wicket.convert.converters.YesNoBooleanConverter;
import za.co.liberty.web.wicket.markup.html.form.SRSAbstractChoiceRenderer;
import za.co.liberty.web.wicket.markup.html.form.SRSDateField;
import za.co.liberty.web.wicket.markup.html.form.SRSDropDownChoice;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGrid;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGrid.GridSizeUnit;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGridColumn;
import za.co.liberty.web.wicket.markup.repeater.data.ListDataProvider;
import za.co.liberty.web.wicket.modal.SRSModalWindow;
import za.co.liberty.web.wicket.view.ContextDrivenViewTemplate;

import com.inmethod.grid.DataProviderAdapter;
import com.inmethod.grid.IGridColumn;
import com.inmethod.grid.SizeUnit;

/**
 * This panel contains all components necessary to render the 
 * FAIS License details and associated details
 * @author kxd1203
 * @author aaa1210
 *
 */
public class FAISLicensePanel extends
		ViewTemplateBasePanel<AgreementGUIField, AgreementDTO> implements
		ISecurityPanel,IHasAccessPanel {	

	private static final long serialVersionUID = -3527102887999213064L;
	private transient IAgreementGUIController guiController;
	private RepeatingView faisLicensePanel;
	private static Logger logger = Logger.getLogger(FAISLicensePanel.class);

	//private Collection <FAISCategorySupervisionDTO> supervisionDTO;

	private GUIFieldPanel fspComponent;

	private GUIFieldPanel fsbUpdatedComponent;

	List<FAISLicensePanelGridDTO> gridList;

	private GUIFieldPanel faisLicenseEffectiveFromComponent;

	private FAISLicensePanelModel panelModel;
	private boolean isTied=false; 
	private boolean initialised;

	private GUIFieldPanel faisLicenseStatus;

	private FAISLicenseDTO faisLicenseDTO;
	private String categoryId="";
	private long priorExperience=0;
	
	private long typeOid;
	
	private ModalWindow supervisionWindow;
	private ModalWindow supervisorWindow;
	private ModalWindow fspFAISLicenseWindow;

	private GUIFieldPanel medicalAccreditation;

	private IModel<FAISLicensePanelGridDTO> currentFaisLicenseGridRowModel;
	boolean outstandingSupervisorRequest = false;
	boolean outstandingFaisLicenseRequest = false;

	boolean outstandingFaisLicenseStatusRequest = false;
	/*private long currentExperienceInMonths=0;*/
/*	private long priorExperienceInMonths=0;*/
	
	boolean canRaiseSupervisorRequest = true;
	boolean canRaiseFaisLicenseRequest = true;
	boolean canRaiseFaisLicenseStatusRequest = true;
	private List<TypeVO> allCategories=null;
	private List<TypeVO> categories=null;
	boolean enableComponent = true;
	WebMarkupContainer clickHereLinkComponent;
	boolean enableSupervisorcomponent=true;
	boolean enableStatusComponent = true;
	private Collection<FormComponent> validationComponents = new ArrayList<FormComponent>();

	private HashMap<String, FAISLicensePanelGridDTO> guiData = new HashMap<String, FAISLicensePanelGridDTO>();
	private HashMap<FAISLicensePanelGridDTO, ExperiencePanel> experienceHashMap = new HashMap<FAISLicensePanelGridDTO, ExperiencePanel>();
	private HashMap<Long, ResultPartyDTO> resultPartyDTOcache = new HashMap<Long, ResultPartyDTO>();
	
	protected SRSDataGrid srsGridComponent;
	
	private AgreementDTO viewTemplateContext;
	private SupervisorPage supervisorPage;
	FAISLicenseForm faisLicenseForm;
	// SSM2707 Hierarchy FR3.4 FAIS Details SWETA MENON
	private MaintainAgreementPageModel maintainAgreementPageModel;
	private String salesCategory;

	public FAISLicensePanel(String id, EditStateType editState,
			FAISLicensePanelModel panelModel) {
		this(id, editState, panelModel, null);
	}

	public FAISLicensePanel(String id, EditStateType editState,
			FAISLicensePanelModel panelModel, Page parentPage) {
		super(id, editState, parentPage);
		initialised = false;
		this.panelModel = panelModel;
	}
	
	// SSM2707 Hierarchy FR3.4 FAIS Details SWETA MENON Begin
	public FAISLicensePanel(String id, EditStateType editState,
			MaintainAgreementPageModel maintainAgreementPageModel,
			FAISLicensePanelModel panelModel, Page parentPage,
			String salesCategory) {
		super(id, editState, parentPage);
		initialised = false;
		this.panelModel = panelModel;
		this.maintainAgreementPageModel = maintainAgreementPageModel;
		this.salesCategory = salesCategory;
	}
	// SSM2707 Hierarchy FR3.4 FAIS Details SWETA MENON End

	/**
	 * Custom renderer to render PartyStatusType objects in a drop down
	 */
	IChoiceRenderer partyStatusTypeRenderer = new SRSAbstractChoiceRenderer<Object>() {

		private static final long serialVersionUID = 1L;

		public Object getDisplayValue(Object object) {
			if (object == null) {
				return null;
			}
			return ((PartyStatusType) object).getDescription();
		}

		public String getIdValue(Object object, int index) {
			return "" + index;
		}

	};
	
	/**
	 * Create the modal window for supervision
	 * 
	 * @param id
	 * @return
	 */
	private ModalWindow createSuperVisionWindow(String id) {
		final SRSModalWindow window = new SRSModalWindow(id) {

			@Override
			public String getModalSessionIdentifier() {
				return "FAIS.SUPERVISSION1122-";
			}
			
		};
		window.setTitle("Category supervision");

		window.setPageCreator(new ModalWindow.PageCreator() {
			private static final long serialVersionUID = 1L;

			public Page createPage() {
				return new SuperVisionPage(EditStateType.MODIFY,
						currentFaisLicenseGridRowModel.getObject().getFaisLicenseCategoryDTO(), 
							window,categoryId,panelModel.getAgreementStartDate());
			}
		});

		// Close window call back
		window.setWindowClosedCallback(new ModalWindow.WindowClosedCallback() {
			private static final long serialVersionUID = 1L;
			
			public void onClose(AjaxRequestTarget target) {
				
				IModalMaintenancePageModel<FAISLicenseCategoryDTO> model = window.getSessionModelForPage();
				window.clearModalPageModelInSession();
				
				if (model.isModalWizardSucces() && model.getSelectedItem() != null) {
					logger.info("SupervisionWindow - Success");
					getFeedBackPanel().info("Updated Supervision Edit");
					target.add(getFeedBackPanel());
					currentFaisLicenseGridRowModel.getObject().getFaisLicenseCategoryDTO().getFaisCategorySupervisionDTO().clear();
					currentFaisLicenseGridRowModel.getObject().getFaisLicenseCategoryDTO().getFaisCategorySupervisionDTO().addAll(
							model.getSelectedItem().getFaisCategorySupervisionDTO());
					
					logger.info("Updated category id = " + currentFaisLicenseGridRowModel.getObject().getCategoryId());
					
					srsGridComponent.markItemDirty(currentFaisLicenseGridRowModel);
					srsGridComponent.update();
					target.add(srsGridComponent.findRowComponent(currentFaisLicenseGridRowModel));
					
				} else {
					logger.info("SupervisionWindow - Cancel");
					getFeedBackPanel().info("Cancel Supervision Edit");
					target.add(getFeedBackPanel());
				}
				
			}
			
		});

		// Initialise window settings
		window.setMinimalHeight(420);
		window.setInitialHeight(420);
		window.setMinimalWidth(750);
		window.setInitialWidth(750);
		window.setMaskType(MaskType.SEMI_TRANSPARENT);
		window.setCssClassName(ModalWindow.CSS_CLASS_GRAY);
		return window;
	}
	
	/**
	 * Create the modal window for supervisor
	 * 
	 * @param id
	 * @return
	 */
	private SRSModalWindow createSuperVisorWindow(String id) {
		
		
		final SRSModalWindow window = new SRSModalWindow(id) {

			@Override
			public String getModalSessionIdentifier() {
				return "FAIS.SUPERVISOR112121-";
			}
			
		};
		window.setTitle("Category "+categoryId+" Supervisor");

		window.setPageCreator(new ModalWindow.PageCreator() {
			private static final long serialVersionUID = 1L;
	
				public Page createPage() {
	
					long agreementNo = 0;
					if(panelModel.getAgreementNumber() == null)	{
						agreementNo =0;
					} else {
						agreementNo = panelModel.getAgreementNumber().longValue();
					}
						return supervisorPage = new SupervisorPage(EditStateType.MODIFY, 
								panelModel.getCurrentAndFutureSupervisionRoles(), window, typeOid, agreementNo, 
								panelModel.getAgreementKind(), panelModel.getAgreementStartDate()); 
				}
			});

		// Close window call back
		window.setWindowClosedCallback(new ModalWindow.WindowClosedCallback() {
			private static final long serialVersionUID = 1L;
			
			public void onClose(AjaxRequestTarget target) {
				
				IModalMaintenancePageModel<FAISLicenseCategoryDTO> model = window.getSessionModelForPage();
				window.clearModalPageModelInSession();
				
				if (model.isModalWizardSucces() && model.getSelectedItem() != null) {			
					logger.info("SupervisorWindow - Success");
						panelModel.getCurrentAndFutureSupervisionRoles().clear();
						panelModel.getCurrentAndFutureSupervisionRoles().addAll(
								(Collection<? extends AgreementRoleDTO>) model.getSelectedItem());
						
						AgreementRoleDTO supervisorRole = panelModel.getActiveSupervisorRole(
								currentFaisLicenseGridRowModel.getObject().getFaisLicenseCategoryDTO().getTypeOid());
						
						if(supervisorRole != null){
							currentFaisLicenseGridRowModel.getObject().setSupervisor((ResultPartyDTO)supervisorRole.getRolePlayerReference());
						} else {
							currentFaisLicenseGridRowModel.getObject().setSupervisor(null);
						}
													
					srsGridComponent.markItemDirty(currentFaisLicenseGridRowModel);
					srsGridComponent.update();
//					target.add(srsGridComponent.findRowComponent(currentFaisLicenseGridRowModel));

					
				} else {
					logger.info("SupervisorWindow - cancel");
					getFeedBackPanel().info("Cancel Supervisor Edit");
					target.add(getFeedBackPanel());
				}
				
			}
			
		});
		
		
		// Initialise window settings
		window.setMinimalHeight(420);
		window.setInitialHeight(400);
		window.setMinimalWidth(800);
		window.setInitialWidth(800);
		window.setMaskType(MaskType.SEMI_TRANSPARENT);
		window.setCssClassName(ModalWindow.CSS_CLASS_GRAY);
		return window;
	}
	/**
	 * Create the modal window FSP FAIS License Details
	 * 
	 * @param id
	 * @return
	 */
	private ModalWindow createFSPFAISLicenseWindow(final String id) {
		final ModalWindow window = new ModalWindow(id);
		window.setTitle("FSP FAIS License Details");

		window.setPageCreator(new ModalWindow.PageCreator() {
			private static final long serialVersionUID = 1L;

			public Page createPage() {
				return new FSPFAISLicenseDetailsPage(EditStateType.VIEW,
						panelModel, window);
			}
		});

		// Initialise window settings
		window.setMinimalHeight(420);
		window.setInitialHeight(420);
		window.setMinimalWidth(750);
		window.setInitialWidth(750);
		window.setMaskType(MaskType.SEMI_TRANSPARENT);
		window.setCssClassName(ModalWindow.CSS_CLASS_GRAY);
		return window;
	}
	/**
	 * View label converter for PartyStatusType
	 */
	IConverter partyStatusTypeConverterForViewLabel = new IConverter() {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public Object convertToObject(String object, Locale locale) {
			return null;
		}

		public String convertToString(Object object, Locale locale) {
			if (object != null && object instanceof PartyStatusType) {
				return ((PartyStatusType) object).getDescription();
			}
			return null;
		}

	};

	

	/**
	 * Set/update the panel model
	 * 
	 * This method can be used prior to rendering the panel to ensure
	 * that the model is up to date.
	 * 
	 * @param panelModel
	 */
	public void setPanelModel(FAISLicensePanelModel panelModel) {
		this.panelModel = panelModel;
		/**
		 * When updating the model, reset the view template context
		 * so that it will be re-created
		 */
		this.viewTemplateContext = null;
	}

	@Override
	protected void onBeforeRender() {
		super.onBeforeRender();
		if (!initialised) {
			/**
			 * Only construct the component tree when the panel is
			 * rendered the first time
			 */
			if (panelModel == null) {
				throw new IllegalArgumentException(
						"FAIS Panel Model must be set prior to viewing the panel");
			}

			//get FSP licence details
			try {
				panelModel.setFspFAISLicence(getGuiController().getFSPLicenceDetails(panelModel.getAgreementKind(), panelModel.getBelongsToAgmtNumber()));
				 isTied=getGuiController().isTied(panelModel.getAgreementKind());
			} catch (DataNotFoundException e) {
				panelModel.setFspFAISLicence(null);
			}			
			initGrid();
			add(faisLicenseForm=new FAISLicenseForm("faisLicenseForm"));
			//use rowObject to populate page
			add(supervisionWindow = createSuperVisionWindow("modelWindow"));
			add(supervisorWindow = createSuperVisorWindow("supervisorWindow"));
			add(fspFAISLicenseWindow=createFSPFAISLicenseWindow("fspFaisLicenseDet"));
			initialised = true;
			
		}
	}

	public class FAISLicenseForm extends Form {
		private static final long serialVersionUID = 5808296649559984427L;

		public FAISLicenseForm(String id) {
			super(id);

			for (RequestKindType type : getOutStandingRequestKinds()) {
				if (type == RequestKindType.MaintainFAISLicense) {
					outstandingFaisLicenseRequest = true;
				} else if (type == RequestKindType.MaintainFAISLicenseStatus) {
					outstandingFaisLicenseStatusRequest = true;
				}else if (type == RequestKindType.MaintainAgreementSupervisors){
					outstandingSupervisorRequest=true;
				}
			}

			ISessionUserProfile loggedInUser = SRSAuthWebSession.get()
					.getSessionUser();
						
			canRaiseFaisLicenseRequest = loggedInUser
					.isAllowRaise(RequestKindType.MaintainFAISLicense);
			canRaiseFaisLicenseStatusRequest = loggedInUser
					.isAllowRaise(RequestKindType.MaintainFAISLicenseStatus);
			canRaiseSupervisorRequest = loggedInUser
					.isAllowRaise(RequestKindType.MaintainAgreementSupervisors);
		

			if (outstandingFaisLicenseRequest) {
				enableComponent = false;
			}
			if (!canRaiseFaisLicenseRequest) {
				enableComponent = false;
			}
			if (outstandingFaisLicenseStatusRequest) {
				enableStatusComponent = false;
			}
			if (!canRaiseFaisLicenseStatusRequest) {
				enableStatusComponent = false;
			}
			if(outstandingSupervisorRequest){
				enableSupervisorcomponent=false;
			} 
			if (!canRaiseSupervisorRequest){
				enableSupervisorcomponent=false;
			}
			if (maintainAgreementPageModel != null
					&& !maintainAgreementPageModel.isAllowFAIS()
					&& salesCategory != null) {
				getFAISLicensePanel()
						.info("Agreements with sales category value "
								+ salesCategory
								+ " do not require FAIS details maintenance has been disabled.");
			}
			
			add(getFAISLicensePanel());
			add(clickHereLinkComponent=getClickHereComponentLabel());
			//add(clickHereLink=getClickHereComponent());
			add(srsGridComponent=getFitnProperGrid());
			
			if(((HelperPanel)fspComponentGUIField.getComponent()).getEnclosedObject() instanceof FormComponent){
				validationComponents.add((FormComponent)((HelperPanel)fspComponentGUIField.getComponent()).getEnclosedObject());
			}
			if(((HelperPanel)licenseNumberComponentGUIField.getComponent()).getEnclosedObject() instanceof FormComponent){
				validationComponents.add((FormComponent)((HelperPanel)licenseNumberComponentGUIField.getComponent()).getEnclosedObject());
			}
			if(((HelperPanel)faisLicenseStatusComponentGUIField.getComponent()).getEnclosedObject() instanceof FormComponent){
				validationComponents.add((FormComponent)((HelperPanel)faisLicenseStatusComponentGUIField.getComponent()).getEnclosedObject());
			}
			if(((HelperPanel)fsbUpdatedComponentGUIField.getComponent()).getEnclosedObject() instanceof FormComponent){
				validationComponents.add((FormComponent)((HelperPanel)fsbUpdatedComponentGUIField.getComponent()).getEnclosedObject());
			}
			if(((HelperPanel)faisLicenseEffectiveDatePanelGUIField.getComponent()).getEnclosedObject() instanceof FormComponent){
				validationComponents.add((FormComponent)((HelperPanel)faisLicenseEffectiveDatePanelGUIField.getComponent()).getEnclosedObject());
			}
			if(((HelperPanel)medicalAccreditationComponentGUIField.getComponent()).getEnclosedObject() instanceof FormComponent){
				validationComponents.add((FormComponent)((HelperPanel)medicalAccreditationComponentGUIField.getComponent()).getEnclosedObject());
			}
			
			
			
			add(new IFormValidator() {

				private static final long serialVersionUID = 1L;

				@SuppressWarnings("unchecked")
				public FormComponent[] getDependentFormComponents() {				
					return null;
				}

				public void validate(final Form form) {				
					if (getEditState().isViewOnly() ) {
						return;
					}
					boolean validate = true;
					for(FormComponent comp : validationComponents){
						if(!comp.isValid()){
							validate = false;
						} 
					}
					if(validate){
						try{				
							//validate 
							if( panelModel.getFaisLicenseDTO()==null){
								throw new ValidationException("Please Add Fais License Details");
							}
														
								//validate supervision data as category start date may have changed.
								//for each category in guiData
								// the code below is commented out and should not be deleted.till line number 583
								
							for(String key : guiData.keySet()){
									// loop through the guidata
									FAISLicensePanelGridDTO panelGridDTO=guiData.get(key);
									
									FAISLicenseCategoryDTO licenseCategoryDTO=panelGridDTO.getFaisLicenseCategoryDTO();
									String catId="";
									
									DateUtil dateUtil= DateUtil.getInstance();
									try{
										catId=getGuiController().getType(licenseCategoryDTO.getTypeOid()).getName();
									}catch (DataNotFoundException dNFE) {
										error("No category Id found");
										
									}
									if(panelGridDTO.isSelected() && licenseCategoryDTO
											.getCategory_StartDate()==null){
										throw new ValidationException("Please Put in the category start date for category "+catId);
									}
																	
									
									//check if agreementDate is not null and is after category start date
									if (panelModel.getAgreementStartDate() != null && panelGridDTO.isSelected()){
										
										if(dateUtil
												.getDatePart(
														licenseCategoryDTO
																.getCategory_StartDate())
												.before(
														dateUtil
																.getDatePart(panelModel
																		.getAgreementStartDate()))) {
									
									
								}
								}	
									
									//get the category name
									
									// validate supervision if 
									//1. selected,
									//2. has total experience less than 24 months,
									//3. and is tied.
									
									if (panelGridDTO.isSelected()
										&& (dateUtil
												.getDifferenceInMonths(
														dateUtil
																.getDatePart(licenseCategoryDTO
																		.getCategory_StartDate()),
														dateUtil
																.getDatePart(new Date())) < 24)
										&& isTied) {
									if (licenseCategoryDTO
											.getFaisCategorySupervisionDTO() == null) {
										getGuiController()
												.validateSupervision(
														new ArrayList<FAISCategorySupervisionDTO>(),licenseCategoryDTO
														.getCategory_StartDate(), catId, panelModel.getAgreementStartDate());
									} else {
										getGuiController()
												.validateSupervision(new ArrayList<FAISCategorySupervisionDTO>(
														licenseCategoryDTO
														.getFaisCategorySupervisionDTO()),licenseCategoryDTO
														.getCategory_StartDate(), catId, panelModel.getAgreementStartDate());
									}
									}
								}
								
							// validate Faislicense Details 
							 getGuiController().	validateFAISLicenceDetails(panelModel.getFaisLicenseDTO(), panelModel.getBelongsToAgmtNumber(),isTied);		
						}catch(ValidationException ex){
							for(String error : ex.getErrorMessages()){
								FAISLicenseForm.this.error(error);								
							}
						} 
					}
				}
				
			});

		}
	}
	GUIFieldPanel fspComponentGUIField,licenseNumberComponentGUIField,faisLicenseStatusComponentGUIField,fsbUpdatedComponentGUIField,faisLicenseEffectiveDatePanelGUIField
	,medicalAccreditationComponentGUIField;
	public RepeatingView getFAISLicensePanel() {
		if (faisLicensePanel == null) {
			faisLicensePanel = new RepeatingView("faisLicensePanel");
			faisLicensePanel.add(fspComponentGUIField=getFspComponent());
			faisLicensePanel.add(licenseNumberComponentGUIField=getLicenseNumberComponent());
			faisLicensePanel.add(faisLicenseStatusComponentGUIField=getFAISLicenseStatusComponent());
			faisLicensePanel.add(fsbUpdatedComponentGUIField=getFsbUpdatedComponent());
	        faisLicensePanel.add(faisLicenseEffectiveDatePanelGUIField=getFaisLicenseEffectiveDatePanel());
			faisLicensePanel.add(medicalAccreditationComponentGUIField=getMedicalAccreditationComponent());

		}
		return faisLicensePanel;
	}
	
	/**
	 * 
	 * @return
	 */
		private GUIFieldPanel getFspComponent() {
			
			if (fspComponent == null) {
			PropertyModel propertyModel = new PropertyModel(
					getPropertyModelTarget(), "faisLicenseDTO.fsp");
			CheckBox fsbUpdatedBox = new CheckBox("value", propertyModel);
			
			Label viewLabel = new Label("value", propertyModel) {
				@Override
				public IConverter getConverter(Class arg0) {
					return new YesNoBooleanConverter();
				}
			};
			AgreementGUIField field = AgreementGUIField.FAIS_FSP;
			fspComponent = createGUIFieldPanel(field, createGUIPageField(field,
					getPropertyModelTarget(), fsbUpdatedBox, viewLabel)
					.getEnclosedObject());
			
			if(panelModel.getFaisLicenseDTO()!=null){
			if(!panelModel.getFaisLicenseDTO().isFsp()){
				if(getEditState()== EditStateType.ADD){
					panelModel.getFaisLicenseDTO().setLicenseNumber(ISRSConstants.REP_LICENCE_NUMBER);	
				}
				
			}
		}
			if(panelModel.getFaisLicenseDTO()!=null){
			panelModel.setFsp(panelModel.getFaisLicenseDTO().isFsp());
			
			panelModel.setFSBUpdated(panelModel.getFaisLicenseDTO().isFsbUpdated());
			if(panelModel.getFaisLicenseDTO().getFaisLicenseStatus()!=null){
			panelModel.setCurrentStatus((short)panelModel.getFaisLicenseDTO().getFaisLicenseStatus().getStatusCode());
			}
			}
			fsbUpdatedBox.add(new AjaxFormComponentUpdatingBehavior("click"){

				@Override
				protected void onUpdate(AjaxRequestTarget target) {				
					//when changed, change licence number field
					
					GUIFieldPanel newLicenseNumberField = getLicenseNumberComponent();
					licenseNumberComponentGUIField.replaceWith(newLicenseNumberField);
					licenseNumberComponentGUIField = newLicenseNumberField;
					target.add(licenseNumberComponentGUIField);
					
					GUIFieldPanel newEffectiveDate = getFaisLicenseEffectiveDatePanel();
					faisLicenseEffectiveDatePanelGUIField.replaceWith(newEffectiveDate);
					faisLicenseEffectiveDatePanelGUIField = newEffectiveDate;
					target.add(faisLicenseEffectiveDatePanelGUIField);
					
					initGrid();
						SRSDataGrid dataGrid=getFitnProperGrid();
						srsGridComponent.replaceWith(dataGrid);
						srsGridComponent=dataGrid;
						target.add(srsGridComponent);

						try{
						 isTied=getGuiController().isTied(panelModel.getAgreementKind());
						}catch (DataNotFoundException dnFe) {
							// TODO: handle exception
						}
						
					//we need to change the DTO with the new values
					if(panelModel.getFaisLicenseDTO().isFsp()){
						licenseNumberComponentGUIField.setEnabled(true);
						fspComponent.setEnabled(true);
						panelModel.getFaisLicenseDTO().setLicenseNumber(null);
											
					}else{
						licenseNumberComponentGUIField.setEnabled(false);
						panelModel.getFaisLicenseDTO().setLicenseNumber(ISRSConstants.REP_LICENCE_NUMBER);
					}
					
				
						if(panelModel.isFsp()==panelModel.getFaisLicenseDTO().isFsp()){
							panelModel.getFaisLicenseDTO().setFsbUpdated(panelModel.isFSBUpdated());
							panelModel.getFaisLicenseDTO().setFaisLicenseStatus(PartyStatusType.getStatusWithCode(panelModel.getCurrentStatus()));
							
							
						}else{
							
							panelModel.getFaisLicenseDTO().setFsbUpdated(false);
							panelModel.getFaisLicenseDTO().setFaisLicenseStatus(PartyStatusType.PENDING_FSB_UPDATE);
						}
				
					
					
					target.add(faisLicenseStatus);
					target.add(clickHereLinkComponent);
				}			
			});
		}
			
			
		if (panelModel.getFaisLicenseDTO() != null
				&& !panelModel.getFaisLicenseDTO().isFsp()) {
			fspComponent.setEnabled(false);
		}else {
			fspComponent.setEnabled(enableComponent);
		}
		if (getEditState() == EditStateType.ADD ||getEditState() ==EditStateType.MODIFY ) {
			fspComponent.setEnabled(true);
		}		
		return fspComponent;
	}
		
	/**
	 * 
	 * @return
	 */	
	private GUIFieldPanel getLicenseNumberComponent() {
		String popUpText="";
		EditStateType[] editStateType=new EditStateType[]{};
		FAISLicenseDTO licence=null;
		if (panelModel.getFaisLicenseDTO()!=null && panelModel.getFaisLicenseDTO().isFsp()) {
			licence=panelModel.getFaisLicenseDTO();
			popUpText="FAIS License Number";
			editStateType= new EditStateType[]{EditStateType.ADD,EditStateType.MODIFY};
		
			
		}else if(panelModel.getFaisLicenseDTO()!=null && !panelModel.getFaisLicenseDTO().isFsp() ){
			licence = panelModel.getFspFAISLicence();
			editStateType= new EditStateType[]{};
			popUpText="FSP FAIS License Number";
			
			
		}else if(licence == null){
			error("The FSP licence for this agreement could not be found, please consult support to rectify the belongs to roles");
			
		}
		if(licence!=null && licence.getLicenseNumber()==null){
			licence.setLicenseNumber("");
			
		}
		if(licence==null){
			licence=new FAISLicenseDTO();
			licence.setLicenseNumber("");
		}	
		GUIFieldPanel licenseNumberComponent=createGUIFieldPanel(popUpText,popUpText,"faisLicenseNumber",
				createPageField(licence,popUpText,"panel","licenseNumber",ComponentType.TEXTFIELD, true,true,editStateType));
		licenseNumberComponent.setOutputMarkupId(true);
		return licenseNumberComponent;
	}

	
	private GUIFieldPanel getFAISLicenseStatusComponent() {
		String status="Select";
		try {
			if(!(getGuiController().isTied(panelModel.getAgreementKind()))){
				
				status=PartyStatusType.PENDING_FSB_UPDATE.getDescription();
			}else if(getGuiController().isTied(panelModel.getAgreementKind())){

				status=PartyStatusType.AUTHORIZED.getDescription();
			}
				
		} catch (DataNotFoundException e) {
			
		}
		if (faisLicenseStatus == null) {
		
			PropertyModel propertyModel = new PropertyModel(
					getPropertyModelTarget(),
					"faisLicenseDTO.faisLicenseStatus");
			SRSDropDownChoice dropDownChoice = new SRSDropDownChoice("value",
					propertyModel, PartyStatusType.getStatusForFAISOnly(),
					partyStatusTypeRenderer, status);
			Label viewLabel = new Label("value", propertyModel) {
				@Override
				public IConverter getConverter(Class arg0) {
					return partyStatusTypeConverterForViewLabel;
				}
			};
			AgreementGUIField field = AgreementGUIField.FAIS_LICENSE_STATUS;
			faisLicenseStatus = createGUIFieldPanel(field, createGUIPageField(
					field, getPropertyModelTarget(), dropDownChoice, viewLabel)
					.getEnclosedObject());
		}
		if(getEditState()==EditStateType.MODIFY){
			faisLicenseStatus.setEnabled(enableStatusComponent);
			
		}else{
			faisLicenseStatus.setEnabled(false);
		}
		faisLicenseStatus.setOutputMarkupId(true);
		return faisLicenseStatus;
	}

	private GUIFieldPanel getFaisLicenseEffectiveDatePanel() {
		boolean enable = false;

		if (panelModel.getFaisLicenseDTO() != null && panelModel.getFaisLicenseDTO().isFsp()) {
			enable = true;

		} else if (panelModel.getFaisLicenseDTO() != null&& !panelModel.getFaisLicenseDTO().isFsp()) {

			enable = false;
			
			if (panelModel.getFspFAISLicence()!=null && panelModel.getFspFAISLicence().getEffectiveFrom() != null) {
				panelModel.getFaisLicenseDTO().setEffectiveFrom(panelModel.getFspFAISLicence().getEffectiveFrom());
			}

		}

		if (faisLicenseEffectiveFromComponent == null) {
			AgreementGUIField field = AgreementGUIField.FAIS_LICENSE_EFFECTIVE_DATE;
			
			Component comp = createGUIPageField(field, getPropertyModelTarget(),
					ComponentType.DATE_SELECTION_TEXTFIELD, true)
					.getEnclosedObject();
			if (comp instanceof SRSDateField ) {
				((SRSDateField)comp).addNewDatePicker();
			}
					
			faisLicenseEffectiveFromComponent = createGUIFieldPanel(field,null,
					comp,true);
		}

		if (!getEditState().isViewOnly()) {
			faisLicenseEffectiveFromComponent.setEnabled(enable);
		} else {

			faisLicenseEffectiveFromComponent.setEnabled(enableComponent);

		}
		faisLicenseEffectiveFromComponent.setOutputMarkupId(true);
		return faisLicenseEffectiveFromComponent;
		}


	/**
	 * 
	 * @return
	 */
	private GUIFieldPanel getFsbUpdatedComponent() {
		if (fsbUpdatedComponent == null) {
			PropertyModel propertyModel = new PropertyModel(
					getPropertyModelTarget(), "faisLicenseDTO.fsbUpdated");
			CheckBox fsbUpdatedBox = new CheckBox("value", propertyModel);
			Label viewLabel = new Label("value", propertyModel) {
				@Override
				public IConverter getConverter(Class arg0) {
					return new YesNoBooleanConverter();
				}
			};
			AgreementGUIField field = AgreementGUIField.FAIS_FSB_UPDATED;
			fsbUpdatedComponent = createGUIFieldPanel(field,
					createGUIPageField(field, getPropertyModelTarget(),
							fsbUpdatedBox, viewLabel).getEnclosedObject());

		}
		fsbUpdatedComponent.setEnabled(false);
		return fsbUpdatedComponent;
	}
/**
 * 
 * @return
 */
	private GUIFieldPanel getMedicalAccreditationComponent() {
		if (medicalAccreditation == null) {
			AgreementGUIField field = AgreementGUIField.FAIS_LICENSE_MEDICAL_ACCREDITATION;
			medicalAccreditation = createGUIFieldPanel(field,
					createGUIPageField(field, getPropertyModelTarget(),
							ComponentType.TEXTFIELD, true).getEnclosedObject());
		}
		medicalAccreditation.setEnabled(enableComponent);
		return medicalAccreditation;
	}
	 
	/**
	 * 
	 * @return
	 */
	private WebMarkupContainer getClickHereComponentLabel(){
		WebMarkupContainer component=new WebMarkupContainer("linkComponent");
		component.add(getClickHereComponent());
		if((panelModel.getFaisLicenseDTO()!=null && panelModel.getFaisLicenseDTO().isFsp())||panelModel.getFaisLicenseDTO()==null){
			component.setVisible(false);
		}else{
			component.setVisible(true);
		}
		component.setOutputMarkupId(true);
		return component;
		
	}
	
	/**
	 * 
	 * @return
	 */
	private Link getClickHereComponent() {
		Link but = new AjaxFallbackLink("link") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				fspFAISLicenseWindow.show(target);
			}
		};
		if((panelModel.getFaisLicenseDTO()!=null && panelModel.getFaisLicenseDTO().isFsp())||panelModel.getFaisLicenseDTO()==null){
			but.setEnabled(false);
		}else{
			but.setEnabled(true);
		}
		return but;
	}	
/*grid fields

	*/
	HelperPanel isSelected,
	 catEffDate,isAdvisor,AdEffDate,isIntermediary,intermediaryEffDate;
	/**
	 * 
	 * @return
	 */
	private List<IGridColumn> getColumns() {
		List<IGridColumn> columns = new ArrayList<IGridColumn>();

		columns.add(new SRSDataGridColumn<FAISLicensePanelGridDTO>(
				"categoryId",
				new Model("Code"),
				"categoryId",
				"categoryId", getEditState())
				.setInitialSize(30));

		columns.add(new SRSDataGridColumn<FAISLicensePanelGridDTO>(
				"description", new Model("Description"), "description",
				getEditState()).setInitialSize(240).setWrapText(true));

		columns.add(new SRSDataGridColumn<FAISLicensePanelGridDTO>("selected",
				new Model(""), "selected", getEditState()) {
			private static final long serialVersionUID = 1L;

			@Override
			public Panel newCellPanel(WebMarkupContainer parent,
					String componentId, IModel rowModel, String objectProperty,
					EditStateType state, final FAISLicensePanelGridDTO data) {
				final CheckBox selectCheckBox = new CheckBox("value",
						new PropertyModel(data, objectProperty));

				selectCheckBox.setEnabled(false);

				if (!getEditState().isViewOnly()) {
					selectCheckBox.setEnabled(enableComponent);

				}
				selectCheckBox.add(new AjaxFormComponentUpdatingBehavior(
						"click") {

					@Override
					protected void onUpdate(AjaxRequestTarget target) {
						if (data.isSelected()) {// check if the row is selected
							//if Selected then is it null
							if(panelModel.getFaisLicenseDTO()
							.getFaisLicenseCategoryDTOs()==null){
								//if null then add to collection
								panelModel.getFaisLicenseDTO().setFaisLicenseCategoryDTOs(new ArrayList<FAISLicenseCategoryDTO>());	
							}
							//check if FaisCategoryDTO exist in faislicense category
							if (!panelModel.getFaisLicenseDTO()
									.getFaisLicenseCategoryDTOs().contains(
											data.getFaisLicenseCategoryDTO())) {
								//if not, add to fais license DTO
								panelModel.getFaisLicenseDTO()
										.getFaisLicenseCategoryDTOs()
										.add(data.getFaisLicenseCategoryDTO());
								
							}
							try {
								categoryId= getGuiController().getType(data.getFaisLicenseCategoryDTO().getTypeOid()).getName();	
							} catch (DataNotFoundException e) {
								error("no categories found for This FSP License");
							}
							
							
								

						} else if (!data.isSelected()) {//check if its Unselected
							//check if FaisCategoryDTO exist in faislicense category
							if (panelModel.getFaisLicenseDTO()
									.getFaisLicenseCategoryDTOs().contains(
											data.getFaisLicenseCategoryDTO())) {
								//if yes delete from faislicenseDTO
								panelModel
										.getFaisLicenseDTO()
										.getFaisLicenseCategoryDTOs()
										.remove(
												data
														.getFaisLicenseCategoryDTO());

							}
							categoryId="";
						}

						//target.add(fitAndProperGrid);

					}
					@Override
					protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
						super.updateAjaxAttributes(attributes);
						attributes.getAjaxCallListeners().add(new AjaxCallListener() {

							@Override
							public CharSequence getInitHandler(Component component) {
								return "overlay(true);" + super.getInitHandler(component);
							}

						});
					}
				});
				 isSelected= HelperPanel.getInstance(componentId, selectCheckBox);
				 if(isSelected.getEnclosedObject() instanceof FormComponent){
						validationComponents.add((FormComponent)(isSelected).getEnclosedObject());
				}
					
				return isSelected;
			}
		}.setInitialSize(30));
		
		/**
		 * 
		 * adding column for prior experience
		 */
		 	
		columns.add(new SRSDataGridColumn<FAISLicensePanelGridDTO>(
				"faisLicenseCategoryDTO.priorExperinceTotalMonths", new Model("Prior Experience Field"),
				"faisLicenseCategoryDTO.priorExperinceTotalMonths",
				getEditState()){
					private static final long serialVersionUID = 1L;
					@Override
					public Panel newCellPanel(WebMarkupContainer parent,
							String componentId, IModel rowModel, String objectProperty,
							EditStateType state, final FAISLicensePanelGridDTO data) {
									//creating panel model for experience panel
									 final ExperiencePanelModel priorExpPanelModel=new ExperiencePanelModel();
									 //create the experience panel it self 
									 ExperiencePanel experiencePanel = new ExperiencePanel(componentId, EditStateType.VIEW,priorExpPanelModel,null,"", true);
									//get the textfields of experience panel so later you can add behaviour to them 
									TextField<ExperiencePanelModel> yrsText= experiencePanel.getYrsText();
									TextField<ExperiencePanelModel> mnthsText= experiencePanel.getMnthsTxt();
									//get the prior experience from DTO for the category
									long priorExp=data.getFaisLicenseCategoryDTO().getPriorExperinceTotalMonths();
									//set years and months to experience panelmodel
									priorExpPanelModel.setMonths(priorExp%12);
									priorExpPanelModel.setYears(priorExp/12);
									/*
									 * adding updating behaviour to textfield which represents years in experincepanel
									 */
									yrsText.add( new AjaxFormComponentUpdatingBehavior("change") {
			
										@Override
										protected void onUpdate(AjaxRequestTarget target) {
											//get the prior experience in total month if maintaining prior experience.
											//for ease of understaing lets take a local variable and put prior experience in that
											long priorExp=((priorExpPanelModel.getYears()*12)+priorExpPanelModel.getMonths());
											//
											data.getFaisLicenseCategoryDTO().setPriorExperinceTotalMonths(priorExp);
											// get the Accumelated panel from hashmap
											ExperiencePanel accumExpe= experienceHashMap.get(data);
											//
											long monthintoal=(data.getCurrentExperience()+data.getFaisLicenseCategoryDTO().getPriorExperinceTotalMonths());
											accumExpe.getPropertyModelTarget().setYears(monthintoal/12);
											accumExpe.getPropertyModelTarget().setMonths(monthintoal%12);
											//referesh the yrs textfield of it.
											target.add(accumExpe.getMnthsTxt() );
											target.add(accumExpe.getYrsText());
										}
									});
									/*
									 * adding updating behaviour to textfield which represents months in experincepanel
									 */
									mnthsText.add(new AjaxFormComponentUpdatingBehavior("change") {
			
										@Override
										protected void onUpdate(AjaxRequestTarget target) {
											//get the prior experience in total month if maintaining prior experience.
											//for ease of understaing lets take a local variable and put prior experience in that
											long priorExp=((priorExpPanelModel.getYears()*12)+priorExpPanelModel.getMonths());
											data.getFaisLicenseCategoryDTO().setPriorExperinceTotalMonths(priorExp);
											ExperiencePanel accumExpe= experienceHashMap.get(data);
																						
											long monthintoal=(data.getCurrentExperience()+priorExp);
										
											accumExpe.getPropertyModelTarget().setYears(monthintoal/12);
											accumExpe.getPropertyModelTarget().setMonths(monthintoal%12);
											target.add(accumExpe.getMnthsTxt() );
											target.add(accumExpe.getYrsText());
											
										}
									});
									
																		
								
									return experiencePanel;
					}
					
					
				}.setInitialSize(122));
		
		/**
		 * adding column for Current experience
		 */
		columns.add(new SRSDataGridColumn<FAISLicensePanelGridDTO>(
				"curExp", new Model("Current Experience"), "curExp",
				getEditState()){
					private static final long serialVersionUID = 1L;
					@Override
					public Panel newCellPanel(WebMarkupContainer parent,
							String componentId, IModel rowModel, String objectProperty,
							EditStateType state, FAISLicensePanelGridDTO data) {
						 
										ExperiencePanelModel experiencePanelModel = new ExperiencePanelModel();
						
										
										experiencePanelModel.setYears(data.getCurrentExperience() /12);
										experiencePanelModel.setMonths(data.getCurrentExperience()%12);
										
										ExperiencePanel experiencePanel = new ExperiencePanel(
												componentId, EditStateType.VIEW, experiencePanelModel,
												null, "", true);
						
										return experiencePanel;
					}
					
				}.setInitialSize(115));
		
		
		/**
		 * adding column for accumulated experience
		 */
		columns
				.add(new SRSDataGridColumn<FAISLicensePanelGridDTO>("accumExp",
						new Model("Accumulated Experience"), "accumExp",
						getEditState()) {
					private static final long serialVersionUID = 1L;

					@Override
					public Panel newCellPanel(WebMarkupContainer parent,
							String componentId, IModel rowModel,
							String objectProperty, EditStateType state,
							FAISLicensePanelGridDTO data) {
														ExperiencePanelModel accuExperiencePanelModel = new ExperiencePanelModel();
														
														long accumMonths=data.getCurrentExperience()+ data.getFaisLicenseCategoryDTO().getPriorExperinceTotalMonths();
														priorExperience=data.getFaisLicenseCategoryDTO().getPriorExperinceTotalMonths();
														accuExperiencePanelModel.setYears(accumMonths/12);
														accuExperiencePanelModel.setMonths(accumMonths %12);
														ExperiencePanel experiencePanel = new ExperiencePanel(
																componentId, EditStateType.VIEW,
																accuExperiencePanelModel, null,"", true);
														experienceHashMap.put(data, experiencePanel);
														return experiencePanel;
					}

				}.setInitialSize(136));
		
		
		
		
		if (isTied) {
			columns.add(new SRSDataGridColumn<FAISLicensePanelGridDTO>("faisLicenseCategoryDTO", new Model("Supervision"), "faisLicenseCategoryDTO", getEditState()) {
				private static final long serialVersionUID = 1L;

				@Override
				public Panel newCellPanel(WebMarkupContainer parent, String componentId, IModel rowModel, String objectProperty, EditStateType state, FAISLicensePanelGridDTO data) {

					SuperVisionPanel superVisionPanel = new SuperVisionPanel(componentId, getEditState(), data);
					superVisionPanel.setOutputMarkupId(true);
					superVisionPanel.setOutputMarkupPlaceholderTag(true);
					return superVisionPanel;
				}
			}.setInitialSize(250));
		}
	if (!getEditState().isViewOnly() && isTied) {
			columns.add(new SRSDataGridColumn<FAISLicensePanelGridDTO>("Edit",
					new Model("Edit"), null, "bool", getEditState()) {
				private static final long serialVersionUID = 1L;

				@Override
				public Panel newCellPanel(WebMarkupContainer parent,
						String componentId, final IModel rowModel,
						String objectProperty, EditStateType state,
						final FAISLicensePanelGridDTO data) {
					Button editButton = new Button("value", new Model("Edit"));
					//editButton.add(validators)
					//check.add(new SimpleAttributeModifier("click","javascript: alert(' "+data.getDescription()+"');"));
					editButton.setEnabled(false);
					editButton.add(new AjaxFormComponentUpdatingBehavior(
							"click") {

						@Override
						protected void onUpdate(AjaxRequestTarget target) {
							currentFaisLicenseGridRowModel = rowModel;
							priorExperience=data.getFaisLicenseCategoryDTO().getPriorExperinceTotalMonths();
							if(data.getFaisLicenseCategoryDTO().getCategory_StartDate()==null){
								
								faisLicenseForm.error("Please put in the category start date");
								if(getFeedBackPanel()!=null){
									target.add(getFeedBackPanel());
								}
								return;
							}
							try {
								categoryId= getGuiController().getType(data.getFaisLicenseCategoryDTO().getTypeOid()).getName();
							} catch (DataNotFoundException e) {
								categoryId= "";
							}
							supervisionWindow.show(target);
							}

					});

					if (!getEditState().isViewOnly()) {

						editButton.setEnabled(enableComponent);

					}

					return HelperPanel.getInstance(componentId, editButton);
				}
			}.setInitialSize(45));
	}
			/**
			 * Display supervisor
			 */
	if (isTied ) {
		columns.add(new SRSDataGridColumn<FAISLicensePanelGridDTO>(
				"supervisor", new Model(
						"Supervisor"),
				null,"supervisor.name",
				getEditState()).setInitialSize(250));
		
}
	if (!getEditState().isViewOnly() && isTied && enableSupervisorcomponent) {
			
			columns.add(new SRSDataGridColumn<FAISLicensePanelGridDTO>("EditSupervisor",
					new Model("Edit Supervisor"), null, "bool", getEditState()) {
				private static final long serialVersionUID = 1L;

				@Override
				public Panel newCellPanel(WebMarkupContainer parent,
						String componentId, final IModel rowModel,
						String objectProperty, EditStateType state,
						final FAISLicensePanelGridDTO data) {
					Button editButton = new Button("value", new Model("Edit"));
					//check.add(new SimpleAttributeModifier("click","javascript: alert(' "+data.getDescription()+"');"));
					editButton.setEnabled(false);
					editButton.add(new AjaxFormComponentUpdatingBehavior(
							"click") {

						@Override
						protected void onUpdate(AjaxRequestTarget target) {
							currentFaisLicenseGridRowModel = rowModel;
							typeOid= data
									.getFaisLicenseCategoryDTO().getTypeOid();
							srsGridComponent.modelChanging();
							supervisorWindow.show(target);
						}

					});

					if (!getEditState().isViewOnly()) {

						editButton.setEnabled(enableComponent);

					}

					return HelperPanel.getInstance(componentId, editButton);
				}
			}.setInitialSize(45));
		}
		columns.add(new SRSDataGridColumn<FAISLicensePanelGridDTO>(
				"faisLicenseCategoryDTO.category_StartDate", new Model(
						"Category Start"),
				"faisLicenseCategoryDTO.category_StartDate", getEditState()) {
			private static final long serialVersionUID = 1L;

			@Override
			public Panel newCellPanel(WebMarkupContainer parent,
					String componentId, IModel rowModel, String objectProperty,
					EditStateType state, final FAISLicensePanelGridDTO data) {
				final SRSDateField categoryEffDateText = new SRSDateField("value", new PropertyModel(data, objectProperty));
				
				categoryEffDateText.setEnabled(false);
				categoryEffDateText.add(new AttributeModifier("maxlength", "10"));
				categoryEffDateText.add(new AttributeModifier("style", "width: 67px;"));

				categoryEffDateText.add(new AjaxFormComponentUpdatingBehavior("change") {

					@Override
					protected void onUpdate(AjaxRequestTarget target) {
						if (guiData.containsValue(data.getFaisLicenseCategoryDTO().getCategory_StartDate())) {
						} else {
							// Set category start date if not set
							guiData
								.get("" + data.getFaisLicenseCategoryDTO().getTypeOid())
								.getFaisLicenseCategoryDTO()
								.setCategory_StartDate(data.getFaisLicenseCategoryDTO().getCategory_StartDate());
						}
						logger.info("Set cat start - start -" + data.getFaisLicenseCategoryDTO().getTypeOid());
						getGuiController().setSupervision(data);
//						new SuperVisionPanel("faisLicenseCategoryDTO", getEditState(), data.getFaisLicenseCategoryDTO());
						
						srsGridComponent.markItemDirty(rowModel);
						srsGridComponent.update();
						
						logger.info("Set cat start - end -" + data.getFaisLicenseCategoryDTO().getTypeOid());
					}

					@Override
					protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
						super.updateAjaxAttributes(attributes);
						attributes.getAjaxCallListeners().add(new AjaxCallListener() {

							@Override
							public CharSequence getInitHandler(Component component) {
								return "overlay(true);" + super.getInitHandler(component);
							}

						});
					}
					
					

				});

				if (!getEditState().isViewOnly()) {
					categoryEffDateText.setEnabled(enableComponent);
					categoryEffDateText.add(categoryEffDateText.newDatePicker());
					return HelperPanel.getInstance(componentId,
							categoryEffDateText, enableComponent);
				}
				 catEffDate=HelperPanel
						.getInstance(componentId, categoryEffDateText);
				 if(catEffDate.getEnclosedObject() instanceof FormComponent){
						validationComponents.add((FormComponent)(catEffDate).getEnclosedObject());
				}
				return catEffDate;
			}
		}.setInitialSize(100));

	columns.add(new SRSDataGridColumn<FAISLicensePanelGridDTO>(
				"faisLicenseCategoryDTO.advisor", new Model("Advice"),
				"faisLicenseCategoryDTO.advisor", getEditState()) {
			private static final long serialVersionUID = 1L;

			@Override
			public Panel newCellPanel(WebMarkupContainer parent,
					String componentId, IModel rowModel, String objectProperty,
					EditStateType state, FAISLicensePanelGridDTO data) {
				CheckBox advisorCheckBox = new CheckBox("value",
						new PropertyModel(data, objectProperty));

				advisorCheckBox.setEnabled(false);
				advisorCheckBox.add(new AjaxFormComponentUpdatingBehavior(
				"click") {

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				//target.add(component);
			}

		});
				if (!getEditState().isViewOnly()) {

					advisorCheckBox.setEnabled(enableComponent);

				}
				
				 isAdvisor=HelperPanel.getInstance(componentId, advisorCheckBox);
				 if(isAdvisor.getEnclosedObject() instanceof FormComponent){
						validationComponents.add((FormComponent)(isAdvisor).getEnclosedObject());
				}
				 return isAdvisor;
			}
		}.setInitialSize(45));
		columns.add(new SRSDataGridColumn<FAISLicensePanelGridDTO>(
				"faisLicenseCategoryDTO.advisor_StartDate", new Model(
						"Advice Start"),
				"faisLicenseCategoryDTO.advisor_StartDate", getEditState()) {
			private static final long serialVersionUID = 1L;

			@Override
			public Panel newCellPanel(WebMarkupContainer parent,
					String componentId, IModel rowModel, String objectProperty,
					EditStateType state, FAISLicensePanelGridDTO data) {
				SRSDateField advisorEffDateText = new SRSDateField("value",
						new PropertyModel(data, objectProperty));

				advisorEffDateText.setEnabled(false);
				advisorEffDateText.add(new AttributeModifier("maxlength",
						"10"));
				advisorEffDateText.add(new AttributeModifier("style",
						"width: 67px;"));
				
				advisorEffDateText.add(new AttributeModifier("align","center"));
				advisorEffDateText.add(new AjaxFormComponentUpdatingBehavior(
				"change") {

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				//target.add(srsGridComponent);
			}

		});
				if (!getEditState().isViewOnly()) {
					advisorEffDateText.setEnabled(enableComponent);
					advisorEffDateText.add(advisorEffDateText.newDatePicker());
					return HelperPanel.getInstance(componentId,
							advisorEffDateText, enableComponent);
				}
			
				 AdEffDate=HelperPanel.getInstance(componentId, advisorEffDateText);
				 if(AdEffDate.getEnclosedObject() instanceof FormComponent){
						validationComponents.add((FormComponent)(AdEffDate).getEnclosedObject());
				}
				return AdEffDate;
			}
		}.setInitialSize(110));

		columns.add(new SRSDataGridColumn<FAISLicensePanelGridDTO>(
				"faisLicenseCategoryDTO.intermediary",
				new Model("Intmed Serv"),
				"faisLicenseCategoryDTO.intermediary", getEditState()) {
			private static final long serialVersionUID = 1L;

			@Override
			public Panel newCellPanel(WebMarkupContainer parent,
					String componentId, IModel rowModel, String objectProperty,
					EditStateType state, FAISLicensePanelGridDTO data) {
				CheckBox interCheck = new CheckBox("value", new PropertyModel(data,
						objectProperty));

				interCheck.setEnabled(false);
				interCheck.add(new AjaxFormComponentUpdatingBehavior(
				"click") {

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				//target.add(component);
			}

		});
				if (!getEditState().isViewOnly()) {
					interCheck.setEnabled(enableComponent);

				}
				
				 isIntermediary=HelperPanel.getInstance(componentId, interCheck);
				 if(isIntermediary.getEnclosedObject() instanceof FormComponent){
						validationComponents.add((FormComponent)(isIntermediary).getEnclosedObject());
				}
				 return isIntermediary;
			}
		}.setInitialSize(75));

		columns
				.add(new SRSDataGridColumn<FAISLicensePanelGridDTO>(
						"faisLicenseCategoryDTO.intermediary_StartDate",
						new Model("Intermediary Start"),
						"faisLicenseCategoryDTO.intermediary_StartDate",
						getEditState()) {
					private static final long serialVersionUID = 1L;

					@Override
					public Panel newCellPanel(WebMarkupContainer parent,
							String componentId, IModel rowModel,
							String objectProperty, EditStateType state,
							FAISLicensePanelGridDTO data) {
						SRSDateField intermediaryEffDateText = new SRSDateField(
								"value",
								new PropertyModel(data, objectProperty));

						intermediaryEffDateText.setEnabled(false);
						intermediaryEffDateText
								.add(new AttributeModifier("maxlength",
										"10"));
						intermediaryEffDateText
								.add(new AttributeModifier("style",
										"width: 67px;"));
						intermediaryEffDateText.add(new AttributeModifier("align","center"));
						intermediaryEffDateText.add(new AjaxFormComponentUpdatingBehavior(
						"change") {

					@Override
					protected void onUpdate(AjaxRequestTarget target) {
						//target.add(srsGridComponent);
					}

				});
						if (!getEditState().isViewOnly()) {
							intermediaryEffDateText.setEnabled(enableComponent);
							intermediaryEffDateText.add(intermediaryEffDateText.newDatePicker());
							return HelperPanel.getInstance(componentId,
									intermediaryEffDateText, enableComponent);
						}
						
						
						 intermediaryEffDate=HelperPanel.getInstance(componentId,
								intermediaryEffDateText);
						 if(intermediaryEffDate.getEnclosedObject() instanceof FormComponent){
								validationComponents.add((FormComponent)(intermediaryEffDate).getEnclosedObject());
						}
						 return intermediaryEffDate;

					}
				}.setInitialSize(120));

		return columns;
	}
	
	private int getNumberOfMonths(Date startDate, Date endDate) {

		int monthsBetween = 0;
		if (startDate != null && endDate != null) {
			Calendar start = Calendar.getInstance();
			start.setTime(startDate);

			Calendar end = Calendar.getInstance();
			end.setTime(endDate);

			monthsBetween = end.get(Calendar.DAY_OF_MONTH) - start.get(Calendar.DAY_OF_MONTH);
		}
		return monthsBetween;
	}

	private SRSDataGrid getFitnProperGrid() {

		SRSDataGrid srsGridComponent = new SRSDataGrid(
				"fitNProperPanel",
				new DataProviderAdapter(
						new ListDataProvider<FAISLicensePanelGridDTO>(gridList)),
				getColumns(), getEditState(), null);
		srsGridComponent.setOutputMarkupId(true);
		srsGridComponent.setCleanSelectionOnPageChange(false);
		srsGridComponent.setClickRowToSelect(false);
		srsGridComponent.setAllowSelectMultiple(true);
		srsGridComponent.setGridWidth(99, GridSizeUnit.PERCENTAGE);
		//if (getEditState() == EditStateType.ADD) {
		srsGridComponent.setRowsPerPage(20);
		srsGridComponent.setContentHeight(200, SizeUnit.PX);
		//} else {
		//	fitAndProperGrid.setRowsPerPage(10);
		//}

		//fitAndProperGrid.setAutoCalculateTableHeight(true);
		srsGridComponent.setEnabled(true);
		return srsGridComponent;
	}
	


	/***
	 * this is to initialize the grid data
	 * 
	 * @author AAA1210
	 *
	 */

	@SuppressWarnings("unchecked")
	private void initGrid() {
		// Panel Model is created in constructor of class FAISLicensePanelModel
		FAISLicenseDTO licenseDTO = panelModel.getFaisLicenseDTO();
		
		try {
			categories = getGuiController().getAllAllowedFaisLicenseCategoriesUsingFSPLicenceDetail(panelModel.getFspFAISLicence());
		} catch (DataNotFoundException e) {
			error("no categories found for This FSP License");
		}
		
		
		//check if panel is null and the size of it is 0
		if (panelModel.getSelectedFaisCategoryList() == null
				|| panelModel.getSelectedFaisCategoryList().size() == 0) {
			//get the faislicenseDTO from panel model
			
			setFaisLicenseDTO(licenseDTO);
			if (licenseDTO != null// check if its not null and faiscategories inside faislicenseDTO is not null too
					&& licenseDTO.getFaisLicenseCategoryDTOs() != null) {
				//create a list
				try {
					if(licenseDTO.isFsp()){
						
						if(!(getGuiController().isTied(panelModel.getAgreementKind()))){
							categories=getGuiController().getFaisLicenseSubTypeList();
						
						}else{
							categories = getGuiController().getAllAllowedFaisLicenseCategoriesUsingFSPLicenceDetail(licenseDTO);
						
						}
					}else{
						categories = getGuiController().getAllAllowedFaisLicenseCategoriesUsingFSPLicenceDetail(panelModel.getFspFAISLicence());	
					}
				} catch (DataNotFoundException e1) {
					error("Problem retrieving categories");
				}	
				
				/*
				 * loop through each FAISLicenseCategoryDTO in faisLicenseDto
				 * 
				 * NB - The wrapper object FAISLicensePanelGridDTO is used in the grid so care must be taken to which 
				 * 		references are updated when changing object values.
				 */
				for (FAISLicenseCategoryDTO categoryDTO : licenseDTO
						.getFaisLicenseCategoryDTOs()) {
					
					FAISLicensePanelGridDTO gridDTO = new FAISLicensePanelGridDTO();					
//					set up the supervisors
					if(panelModel.getCurrentAndFutureSupervisionRoles() != null){
						AgreementRoleDTO role = panelModel.getActiveSupervisorRole(categoryDTO.getTypeOid());																	
							if(role!=null){
								if (role.getRolePlayerReference() instanceof ResultPartyDTO) {
									gridDTO.setSupervisor(((ResultPartyDTO) role.getRolePlayerReference()));	

								}else{
									if( resultPartyDTOcache.containsKey(new Long(role.getRolePlayerReference().getOid())))		{
										gridDTO.setSupervisor(resultPartyDTOcache.get(new Long(role.getRolePlayerReference().getOid())));
									} else {
										try {
											ResultPartyDTO partyDTO = getGuiController()
											.findPartyWithObjectOid(
													role.getRolePlayerReference()
													.getOid());
											gridDTO.setSupervisor(partyDTO);
										} catch (CommunicationException e) {
											throw new CommunicationException("There was a communication problem, fetching the supervisor for this Category");
										} catch (DataNotFoundException e) {
											error("No Supervisor found for this category");
										}
									}
								}							

						}
					}
					
					//populate the grid dto
					gridDTO.setFaisLicenseCategoryDTO(categoryDTO);
					// get the agreementstart date from context						
					
					if(panelModel.getAgreementStartDate()==null && !getEditState().isViewOnly()){
						logger.error("Agreement Start date is not set, please call support if this error persists");						
					}
					
					if(panelModel.getAgreementStartDate()!=null){
						
						
					if (categoryDTO.getCategory_StartDate() != null) {
							if (DateUtil
									.getInstance()
									.getDatePart(
											categoryDTO.getCategory_StartDate())
									.before(
											DateUtil
													.getInstance()
													.getDatePart(
															panelModel
																	.getAgreementStartDate()))) {
								// find out the priorexperience by calculating
								// category start date- agreementdate
								// and set it to the categoryDTO
								categoryDTO
										.setPriorExperinceTotalMonths(DateUtil
												.getInstance()
												.getDifferenceInMonths(
														categoryDTO
																.getCategory_StartDate(),
														panelModel
																.getAgreementStartDate()));
								// crrent experience is agreement start date-
								// today
								gridDTO.setCurrentExperience(getMonths(
										new Date(), panelModel
												.getAgreementStartDate()));

							} else {
								// this means that category start date is either
								// after agreement start date or
								// equal to agreement start date.
								// hence prior experience is 0
								categoryDTO.setPriorExperinceTotalMonths(0);

								gridDTO.setCurrentExperience(getMonths(
										new Date(), categoryDTO
												.getCategory_StartDate()));
							}
						} else if (getEditState().isViewOnly()) {

							gridDTO.setCurrentExperience(getMonths(new Date(),
									categoryDTO.getCategory_StartDate()));
						}
					}	
					/*}*/
					// set current experience
					
					TypeVO typeVO=getGuiController().getSupervisorType(categoryDTO.getTypeOid(),categories);
					if(typeVO!=null){
					gridDTO.setDescription(typeVO.getDescription());
					gridDTO.setCategoryId(typeVO.getName());
							//remove the category from selectable list
					}
					categories
							.remove(""+categoryDTO.getTypeOid());
					

					gridDTO.setSelected(true);
					if(!guiData.containsKey(""+categoryDTO.getTypeOid())){
						guiData.put(""+categoryDTO.getTypeOid(), gridDTO);
					}else if(guiData.containsKey(""+categoryDTO.getTypeOid())){
						guiData.remove(""+categoryDTO.getTypeOid());
						guiData.put(""+categoryDTO.getTypeOid(), gridDTO);
					}
					
					
				}
			}

		}
			
		//setup the selected category in panelmodel.
		//panelModel.setSelectedFaisCategoryList(guiData);

		//now add rest of the categories to the GUIdata
		if (!getEditState().isViewOnly()) {
			for (TypeVO category : categories) {
				FAISLicensePanelGridDTO gridDTO = new FAISLicensePanelGridDTO();
				FAISLicenseCategoryDTO categoryDTO = new FAISLicenseCategoryDTO();
				categoryDTO.setAdvisor(false);
				categoryDTO.setAdvisor_StartDate(null);
				categoryDTO.setCategory_StartDate(null);
				categoryDTO.setIntermediary(false);
				categoryDTO.setIntermediary_StartDate(null);
				categoryDTO.setTypeOid(category.getOid());
				// gridDTO.setUnderSuperVision(categoryDTO.getFaisCategorySupervisionDTO())
				categoryDTO.setFaisCategorySupervisionDTO(null);

				gridDTO.setFaisLicenseCategoryDTO(categoryDTO);
				gridDTO.setSelected(false);

				gridDTO.setDescription(category.getDescription());
				gridDTO.setCategoryId(category.getName());
				if(!guiData.containsKey(""+category.getOid())){
					guiData.put(""+category.getOid(), gridDTO);
				}else if(guiData.containsKey(category.getOid())){
					guiData.remove(""+category.getOid());
					guiData.put(""+category.getOid(), gridDTO);
				}
				
			}
		}
		gridList = new ArrayList<FAISLicensePanelGridDTO>(guiData.values());
		//private HashMap<String, FAISLicensePanelGridDTO> guiData = new HashMap<String, FAISLicensePanelGridDTO>();
//		Map<String, FAISLicensePanelGridDTO> sortedMap = new TreeMap<String, FAISLicensePanelGridDTO>(guiData);
			
		/*
		 * Sort the grid using category ID
		 */
		Collections.sort(gridList, new Comparator<FAISLicensePanelGridDTO>() {
			FAISCategoryTypeVOComparator comp = new FAISCategoryTypeVOComparator();

			@Override
			public int compare(FAISLicensePanelGridDTO object1, FAISLicensePanelGridDTO object2) {
				return comp.compare(object1.getCategoryId(), object2.getCategoryId());
			}
		});
//		gridList = new ArrayList<FAISLicensePanelGridDTO>(sortedMap.values());
		
//		Collections.sort(gridList, new FAISCategoryTypeVOComparator());
	}
	
	
	private FAISLicensePanelModel getPropertyModelTarget() {
		return panelModel;
	}

	@Override
	protected ContextDrivenViewTemplate<AgreementGUIField, AgreementDTO> getViewTemplate() {
		return panelModel.getViewTemplate();
	}

	@Override
	protected AgreementDTO getViewTemplateContext() {
		if (viewTemplateContext == null) {
			viewTemplateContext = new AgreementDTO();
		}
		return viewTemplateContext;
	}

	public Class getPanelClass() {
		return getClass();
	}

	public FAISLicenseDTO getFaisLicenseDTO() {
		return faisLicenseDTO;
	}

	public void setFaisLicenseDTO(FAISLicenseDTO faisLicenseDTO) {
		this.faisLicenseDTO = faisLicenseDTO;
	}
	/**
	 * 
	 */
	/**
	 * Load the AgreementGUIController dynamically if it is null as this is a transient variable.
	 * @return {@link IAgreementGUIController}
	 */
	private IAgreementGUIController getGuiController() {
		if (guiController==null) {
			try {
				guiController = ServiceLocator.lookupService(IAgreementGUIController.class);
			} catch (NamingException e) {
				logger.fatal("Could not lookup AgreementGUIController",e);
				throw new CommunicationException("Could not lookup AgreementGUIController",e);
				
			}
		}
		return guiController;
	}
	
	/**
	 * This method returns number of months difference between date1 and date2
	 * date2 being the least date and date1 being latest.
	 * 
	 * @param date1
	 * @param date2
	 * @return
	 */
	private long getMonths(Date date1, Date date2) {
		long months = 0;
		if(date1!=null && date2!=null){
		Calendar cal1 = Calendar.getInstance();
		Calendar cal2 = Calendar.getInstance();

		cal1.setTime(date1);
		cal2.setTime(date2);
		months = cal1.get(Calendar.MONTH) - cal2.get(Calendar.MONTH);
		months = months
				+ ((cal1.get(Calendar.YEAR) - cal2.get(Calendar.YEAR)) * 12);
	}
		return months;

	}
	
	// SSM2707 Hierarchy FR3.4 FAIS Details Begin
	/**
	 * Override the modify access behavior. Can modify FAIS Details only if the
	 * Sales Category is of an entity that provides advice services to customers
	 */
	public boolean hasModifyAccess(boolean originalAccess) {

		return (originalAccess) ? maintainAgreementPageModel.isAllowFAIS()
				: originalAccess;
	}
	//SSM2707 Hierarchy FR3.4 FAIS Details End
	
	
}
