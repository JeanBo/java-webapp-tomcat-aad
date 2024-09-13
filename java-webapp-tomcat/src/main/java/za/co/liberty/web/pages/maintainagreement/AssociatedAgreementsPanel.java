package za.co.liberty.web.pages.maintainagreement;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxFallbackButton;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow.MaskType;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.ImageButton;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.convert.IConverter;

import com.inmethod.grid.DataProviderAdapter;
import com.inmethod.grid.IGridColumn;
import com.inmethod.grid.SizeUnit;

import za.co.liberty.business.guicontrollers.agreement.IAgreementGUIController;
import za.co.liberty.common.domain.Percentage;
import za.co.liberty.dto.agreement.AgreementDTO;
import za.co.liberty.dto.agreement.AssociatedAgreementDetailsDTO;
import za.co.liberty.dto.agreement.maintainagreement.MaintainAgreementDTO;
import za.co.liberty.dto.agreement.maintainagreement.ValidAgreementValuesDTO;
import za.co.liberty.dto.agreement.properties.CommissionKindsDTO;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.helpers.lookup.ServiceLocator;
import za.co.liberty.interfaces.agreements.requests.RequestKindType;
import za.co.liberty.srs.util.DateUtil;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.data.enums.fields.AgreementGUIField;
import za.co.liberty.web.pages.maintainagreement.model.MaintainAgreementPageModel;
import za.co.liberty.web.pages.panels.ButtonHelperPanel;
import za.co.liberty.web.pages.panels.HelperPanel;
import za.co.liberty.web.pages.panels.ViewTemplateBasePanel;
import za.co.liberty.web.pages.party.model.MaintainPartyPageModel;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGrid;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGrid.GridSizeUnit;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGridColumn;
import za.co.liberty.web.wicket.markup.html.grid.SRSGridRowSelectionCheckBox;
import za.co.liberty.web.wicket.markup.repeater.data.ListDataProvider;
import za.co.liberty.web.wicket.modal.SRSModalWindow;
import za.co.liberty.web.wicket.view.ContextDrivenViewTemplate;

/**
 * This class represents the Associated Agreements Panel for the maintain agreement page
 * @author pks2802
 *
 */
public class AssociatedAgreementsPanel extends ViewTemplateBasePanel<AgreementGUIField, AgreementDTO>{

	private static final long serialVersionUID = 2253771882080568353L;

	protected MaintainAgreementPageModel pageModel;
	
	protected FeedbackPanel feedBackPanel;
	
	private AssociatedAgreementsForm pageForm;
	
	protected transient IAgreementGUIController guiController;
	
	protected static final Logger logger = Logger.getLogger(AssociatedAgreementsPanel.class);
	
	private List<AssociatedAgreementDetailsDTO> associatedAgreementDetails;
	private List<AssociatedAgreementDetailsDTO> originalAssAgmtList;
	private List<AssociatedAgreementDetailsDTO> selectedItemList = new ArrayList<AssociatedAgreementDetailsDTO>();
	private String delImage = "/SRSAppWeb/images/delete_icon.png";
	
	private boolean displayFieldsFlag;
		
	private SRSDataGrid associatedAgreementGrid;
	protected Panel buttonPanel;
	protected Button addButton;
	protected Button editButton;
	private ModalWindow addWindow;

	private ModalWindow editWindow;

	@SuppressWarnings("unused")
	private boolean existingAssAgmtRequest;

	private boolean initialised;
			
	public AssociatedAgreementsPanel(String id, MaintainAgreementPageModel pageModel, 
			FeedbackPanel feedBackPanel, EditStateType editState,Page parentPage) {
		super(id, editState,parentPage);
		this.pageModel=pageModel;
		this.feedBackPanel=feedBackPanel;
		this.setOutputMarkupId(true);
	}
	
	@Override
	protected void onBeforeRender() {
		if(!initialised) {			
			initialised=true;					
		//initialize the page model with the agreement data
			initPageModel();	
			List<RequestKindType> unAuthRequests = getOutStandingRequestKinds();			
			//check for existing requests FIRST as other panels use variables set here
			for (RequestKindType kind : unAuthRequests) {
							
				if(kind == RequestKindType.MaintainAssociatedAgreements){
					existingAssAgmtRequest = true;
					break;
				}
			}
			
			add(getAssociatedAgreementsForm());		
			add(addWindow = createModalWindowAdd("addNewWizzardWindow","Add Associated Agreements"));
			add(editWindow = createModalWindowEdit("editWizzardWindow","Edit Associated Agreements"));
			
		}
		
		if(feedBackPanel == null){			
			feedBackPanel = this.getFeedBackPanel();		
		}
		super.onBeforeRender();
	};
	
	
	private void initPageModel() {
		if(pageModel == null){
			error("Page Model should never be null, Please call support if you continue seeing this error");
			pageModel = new MaintainAgreementPageModel(new AgreementDTO(),new ValidAgreementValuesDTO()); 
		}				
		if(pageModel.getMaintainAgreementDTO() == null){
			error("An agreement needs to be selected to adjust the Associated Agreements");
			pageModel.setMaintainAgreementDTO(new MaintainAgreementDTO());
		}
		if(pageModel.getMaintainAgreementDTO().getAgreementDTO() == null){
			error("An agreement needs to be selected to adjust the Associated Agreements");
			pageModel.getMaintainAgreementDTO().setAgreementDTO(new AgreementDTO());
		}		
	}
	
	/**
	 * Load the AgreementGUIController dynamically if it is null as this is a transient variable.
	 * @return {@link IAgreementGUIController}
	 */
	protected IAgreementGUIController getGuiController() {
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
	 * Get the main page form
	 * @return
	 */
	private AssociatedAgreementsForm getAssociatedAgreementsForm() {
		if (pageForm==null) {
			pageForm = new AssociatedAgreementsForm("pageForm");
		}
		return pageForm;
	}
	
	/**
	 * Get the data grid for the Associated Agreement details 
	 * @return the data grid
	 */
	private SRSDataGrid getAssociatedAgreementDetailsGrid() {
		if (associatedAgreementGrid==null) {
			associatedAgreementDetails = getContext().getAssociatedAgreementDetailsList(); 
			if(associatedAgreementDetails == null){
				associatedAgreementDetails = new ArrayList<AssociatedAgreementDetailsDTO>();
				getContext().setAssociatedAgreementDetailsList(associatedAgreementDetails);
			}
						
			setFlag(associatedAgreementDetails);
			cloneOriginalList(associatedAgreementDetails);
			associatedAgreementGrid = new SRSDataGrid("associatedAgDetails", new DataProviderAdapter(
					new ListDataProvider<AssociatedAgreementDetailsDTO>(associatedAgreementDetails)),
					getColumns(), getEditState(),getNonSelectableAssAgmtDetails(associatedAgreementDetails));
			associatedAgreementGrid.setOutputMarkupId(true);
			associatedAgreementGrid.setCleanSelectionOnPageChange(false);
			associatedAgreementGrid.setClickRowToSelect(false);
			associatedAgreementGrid.setAllowSelectMultiple(false);
			associatedAgreementGrid.setGridWidth(80, GridSizeUnit.PERCENTAGE);
			associatedAgreementGrid.setRowsPerPage(10);
			associatedAgreementGrid.setContentHeight(100, SizeUnit.PX);
		}
		return associatedAgreementGrid;
	}
	
	private void cloneOriginalList(List<AssociatedAgreementDetailsDTO> assAgmtListFromContext) {
		if(assAgmtListFromContext == null)
			return;
		
		if(this.originalAssAgmtList == null)
		{
			originalAssAgmtList = new ArrayList<AssociatedAgreementDetailsDTO>(assAgmtListFromContext);
			
			Collections.copy(originalAssAgmtList, assAgmtListFromContext);
			
			
		}
				
	}

	private void setFlag(List<AssociatedAgreementDetailsDTO> list) {
		
		if(list != null && list.size() != 0)
			this.displayFieldsFlag = true;
		else
			this.displayFieldsFlag = false;

	}

	/**
	 * Get the columns for the Associated Agreement Details
	 * @return the data columns
	 */
	@SuppressWarnings("serial")
	private List<IGridColumn> getColumns() {
		List<IGridColumn> ret = new ArrayList<IGridColumn>();
		/**
		 * Select Column for Remove
		 */
		if(!isViewOnly()){
			SRSGridRowSelectionCheckBox col = new SRSGridRowSelectionCheckBox("checkBox");			
			ret.add(0,col.setInitialSize(35));
		}	
		
		/**
		 * Commission Kind Column
		 */
		SRSDataGridColumn<AssociatedAgreementDetailsDTO> commKind = 
			new SRSDataGridColumn<AssociatedAgreementDetailsDTO>(
				"commissionKind", new Model("Commission Kind"), 
				"commissionKind",getEditState()){
			
			@Override
			public Panel newCellPanel(WebMarkupContainer parent, String componentId, IModel rowModel, String objectProperty, EditStateType state, AssociatedAgreementDetailsDTO data) {
					/**
					 * Commission Kind column will have a custom label with a renderer to render
					 * 0 as None and int values as the appropriate Commission Kind  
					 */
					Label lbl = new Label("value",new PropertyModel(data,objectProperty)) {
						@Override
						public IConverter getConverter(Class targetClass) {
							return commKindConverter;
						}
					};
					return HelperPanel.getInstance(componentId, lbl);
				}
		};
		commKind.setMinSize(350);
		ret.add(commKind);
		/**
		 * Associated Percentage Column
		 */
		SRSDataGridColumn<AssociatedAgreementDetailsDTO> associatedPercentage = 
			new SRSDataGridColumn<AssociatedAgreementDetailsDTO>(
				"associatedPercentage", new Model("Associated Percentage"), 
				"associatedPercentage",getEditState()){

			
			@Override
			public Panel newCellPanel(WebMarkupContainer parent, String componentId, IModel rowModel, String objectProperty, EditStateType state, AssociatedAgreementDetailsDTO data) {
					/**
					 * Percentage column will have a custom label with a renderer to render
					 * 0 as 0% and percentage values with 2 decimal places. 
					 */
					Label lbl = new Label("value",new PropertyModel(data,objectProperty)) {
						@Override
						public IConverter getConverter(Class targetClass) {
							
							return percentageConvertor;
						}
					};
					return HelperPanel.getInstance(componentId, lbl);
				}
		
		};
		associatedPercentage.setMinSize(200);
		ret.add(associatedPercentage);
		/**
		 * Associated Agreement Column
		 */
		SRSDataGridColumn<AssociatedAgreementDetailsDTO> associatedAgreement = 
			new SRSDataGridColumn<AssociatedAgreementDetailsDTO>(
				"associatedAgreement", new Model("Associated Agreement"), 
				"associatedAgreement",getEditState());
		associatedAgreement.setMinSize(200);
		ret.add(associatedAgreement);
		
		/**
		 * Start Date
		 */
		
		SRSDataGridColumn<AssociatedAgreementDetailsDTO> startDate = 
			new SRSDataGridColumn<AssociatedAgreementDetailsDTO>(
				"startDate", new Model("Start Date"), 
				"startDate",getEditState());
		startDate.setMinSize(200);
		ret.add(startDate);
		
		/**
		 * End Date
		 */
		
		SRSDataGridColumn<AssociatedAgreementDetailsDTO> endDate = 
			new SRSDataGridColumn<AssociatedAgreementDetailsDTO>(
				"endDate", new Model("End Date"), 
				"endDate",getEditState());
		endDate.setMinSize(200);
		ret.add(endDate);
		
		/**
		 * Remove icon ( to be displayed for all future Dates and only if Edit state is not view)
		 */
		SRSDataGridColumn<AssociatedAgreementDetailsDTO> removeAction = new SRSDataGridColumn<AssociatedAgreementDetailsDTO>("removeAction", new Model("Remove"),null,
				isViewOnly()?EditStateType.VIEW:getEditState()) {
			
			@Override
			public Panel newCellPanel(WebMarkupContainer parent, String componentId, IModel rowModel, String objectProperty, EditStateType state, final AssociatedAgreementDetailsDTO data) {
							
				
				if(!originalAssAgmtList.contains(data)){
													
					ImageButton removeIcon = new ImageButton("value", "Remove"); 
					removeIcon.add(new AttributeModifier("title", "Remove"));
					removeIcon.add(new AttributeModifier("src",delImage));
					removeIcon.add(new AttributeModifier("align","center"));
					removeIcon.add(new AjaxEventBehavior("click") {
						private static final long serialVersionUID = 1L;			
						@Override
						protected void onEvent(AjaxRequestTarget target) {
							
							
						associatedAgreementDetails.remove(data);
						pageModel.getMaintainAgreementDTO().getAgreementDTO().setAssociatedAgreementDetailsList(associatedAgreementDetails);
							
						   target.add(associatedAgreementGrid);						
						}		

//						@Override
//						protected IAjaxCallDecorator getAjaxCallDecorator() {
//							return new CancelEventIfNoAjaxDecorator();
//						}
					});

								
				return HelperPanel.getInstance(componentId, removeIcon);
				} else return new EmptyPanel(componentId);
			}
		};
		removeAction.setMinSize(70);
		ret.add(removeAction);
		
		return ret;
	}
	
	/**
	 * This class represents the page form to be added to the panel
	 * @author pks2802
	 */
	private class AssociatedAgreementsForm extends Form {

		public AssociatedAgreementsForm(String id) {
			super(id);
			this.add(getAssociatedAgreementDetailsGrid());
			this.add(buttonPanel = createNavigationButtonPanel());
		}		
	}
	
	/**
	 * Create the button panel
	 * 
	 * @return
	 */
	protected Panel createNavigationButtonPanel() {
		
		Panel panel = null;
		
		if(isViewOnly())
			return new EmptyPanel("navButtonPanel");

		addButton = createAddButton("button1");
		editButton = createEditSelectedButton("button2");
		//If No Associated Agreements exist then display only the Add button.
		if(!displayFieldsFlag)
		{
			panel = ButtonHelperPanel.getInstance("navButtonPanel",
					addButton);
			panel.setOutputMarkupId(true);
			return panel;
		}
		
		panel = ButtonHelperPanel.getInstance("navButtonPanel",
				addButton, editButton);
		panel.setOutputMarkupId(true);
		return panel;

	}
	/**
	 * Create the add button
	 * 
	 * @param id
	 * @return
	 */
	protected Button createAddButton(String id) {
		Button button = new AjaxFallbackButton(id, pageForm) {

			private static final long serialVersionUID = -5330766713711809772L;

			@Override
			protected void onComponentTag(ComponentTag tag) {
				super.onComponentTag(tag);
				tag.getAttributes().put("value", "Add");
				tag.getAttributes().put("type", "submit");
			}

			@Override
			protected void onSubmit(AjaxRequestTarget target, final Form form) {
				
				target.add(getFeedBackPanel());
				selectedItemList.clear();  // TODO check this still
				addWindow.show(target);
			}
		};
		button.setOutputMarkupId(true);
		return button;
	}
	
	/**
	 * Create the add button
	 * 
	 * @param id
	 * @return
	 */
	protected Button createEditSelectedButton(String id) {
		Button button = new AjaxFallbackButton(id, pageForm) {

			private static final long serialVersionUID = -5330766713711809772L;

			@Override
			protected void onComponentTag(ComponentTag tag) {
				super.onComponentTag(tag);
				tag.getAttributes().put("value", "Edit Selected");
				tag.getAttributes().put("type", "submit");
			}

			@Override
			protected void onSubmit(AjaxRequestTarget target, final Form form) {
				
				
				if (associatedAgreementGrid.getSelectedItems().size()==0) {
					warn("Please select a record to edit !");
					target.add(getFeedBackPanel());
					return;
				}
				target.add(getFeedBackPanel());
				/* Add selected grid items to selected list */
				selectedItemList.clear();
				
				 ;
				for (IModel model : (Collection<IModel>)associatedAgreementGrid.getSelectedItems()) {
					AssociatedAgreementDetailsDTO itemDTO = (AssociatedAgreementDetailsDTO) model.getObject();
					selectedItemList.add(itemDTO);
				}
							
				// TODO assume I will have to update
			
				editWindow.show(target);
			}			
		};
		button.setOutputMarkupId(true);
		return button;
	}	
	
	/**
	 * Create the modal window for Add Associated Agreements
	 * 
	 * @param id
	 * @return
	 */
	private ModalWindow createModalWindowAdd(String id,String title) {		
		final SRSModalWindow window = new SRSModalWindow(id) {  //<MaintainAgreementPageModel, List>

			@Override
			public String getModalSessionIdentifier() {
				return "Associated.add-";
			}
			
		};
		window.setTitle(title);				
		// Initialise window settings
		window.setMinimalHeight(500);
		window.setInitialHeight(500);
		window.setMinimalWidth(500);
		window.setInitialWidth(500);
		window.setMaskType(MaskType.SEMI_TRANSPARENT);
		window.setCssClassName(ModalWindow.CSS_CLASS_GRAY);	
		window.setOutputMarkupId(true);
		window.setOutputMarkupPlaceholderTag(true);
		
		//Create the page
		window.setPageCreator(new ModalWindow.PageCreator() {
			private static final long serialVersionUID = 1L;
			public Page createPage() {	
//				window.setAdditionalSessionModelForPage(selectedItemList);
				return new AddAssociatedAgreementWizardPage(window, pageModel,null);
			}			
		});	
		
		window.setWindowClosedCallback(new ModalWindow.WindowClosedCallback() {
			private static final long serialVersionUID = 1L;			
			public void onClose(AjaxRequestTarget target) {
				if (logger.isDebugEnabled())
					logger.debug("AssociatedAgreementsPanel.add.setWindowClosedCallback.onClose   ");

//
				MaintainAgreementPageModel model = (MaintainAgreementPageModel) window.getSessionModelForPage();
//				
//				testStuff(model);
//				
				/**
				 * Now we do the stuff that would usually be done here.
				 */				
				if (model.isModalWizardSucces()) {
					if (logger.isDebugEnabled())
						logger.debug("  Update the associated list   ");
					target.add(AssociatedAgreementsPanel.this);
					getFeedBackPanel().info("Added to Associated List");
					 pageModel.getMaintainAgreementDTO().getAgreementDTO().getAssociatedAgreementDetailsList().clear();
					 pageModel.getMaintainAgreementDTO().getAgreementDTO().getAssociatedAgreementDetailsList().addAll(
							 model.getMaintainAgreementDTO().getAgreementDTO().getAssociatedAgreementDetailsList());
					target.add(getFeedBackPanel());
				} else {
					getFeedBackPanel().info("Cancelled Add to Associated List");
					target.add(getFeedBackPanel());
				}
				window.clearModalPageModelInSession();
			}			
		});
		
		return window;
	}
	
	
	
	/**
	 * Create the modal window for Add Associated Agreements
	 * 
	 * @param id
	 * @return
	 */
	private ModalWindow createModalWindowEdit(String id,String title) {		
		final SRSModalWindow window = new SRSModalWindow(id) {

			@Override
			public String getModalSessionIdentifier() {
				return "Associated.edit-";
			}
			
		};
		window.setTitle(title);				
		// Initialise window settings
//		window.setPageMapName(title+"pageMap");
		window.setMinimalHeight(500);
		window.setInitialHeight(500);
		window.setMinimalWidth(500);
		window.setInitialWidth(500);
		window.setMaskType(MaskType.SEMI_TRANSPARENT);
		window.setCssClassName(ModalWindow.CSS_CLASS_GRAY);	
		window.setOutputMarkupId(true);
		window.setOutputMarkupPlaceholderTag(true);
		
		
		//Create the page
		window.setPageCreator(new ModalWindow.PageCreator() {
			private static final long serialVersionUID = 1L;
			//Updates the Grid if we are passing addWindow...???
			public Page createPage() {					
				return new AddAssociatedAgreementWizardPage(window, pageModel,selectedItemList);						
			}			
		});	
		
		
		window.setWindowClosedCallback(new ModalWindow.WindowClosedCallback() {
			private static final long serialVersionUID = 1L;			
			public void onClose(AjaxRequestTarget target) {
				if (logger.isDebugEnabled())
					logger.debug("AssociatedAgreementsPanel.edit.setWindowClosedCallback.onClose   ");

//
				MaintainAgreementPageModel model = (MaintainAgreementPageModel) window.getSessionModelForPage();
//				
//				testStuff(model);
//				
				/**
				 * Now we do the stuff that would usually be done here.
				 */				
				if (model.isModalWizardSucces()) {
					if (logger.isDebugEnabled())
						logger.debug("  Update the associated list   ");
					target.add(AssociatedAgreementsPanel.this);
					getFeedBackPanel().info("Modified the Associated List");
					 pageModel.getMaintainAgreementDTO().getAgreementDTO().getAssociatedAgreementDetailsList().clear();
					 pageModel.getMaintainAgreementDTO().getAgreementDTO().getAssociatedAgreementDetailsList().addAll(
							 model.getMaintainAgreementDTO().getAgreementDTO().getAssociatedAgreementDetailsList());
					target.add(getFeedBackPanel());
				} else {
					getFeedBackPanel().info("Cancelled Edit to Associated List");
					target.add(getFeedBackPanel());
				}
				window.clearModalPageModelInSession();
			}			
		});
		return window;
	}
	
	@Override
	protected ContextDrivenViewTemplate<AgreementGUIField, AgreementDTO> getViewTemplate() {
		return pageModel.getViewTemplate();
	}

	@Override
	protected AgreementDTO getViewTemplateContext() {
		return pageModel.getPreviousMaintainAgreementDTO().getAgreementDTO();
	}
	
	private AgreementDTO getContext() {
		return pageModel.getMaintainAgreementDTO().getAgreementDTO();
	}
	
	/**
	 * If the Associated Agreements End Date is before Current Date then we disable the checkbox.
	 * @return
	 */
	private List<AssociatedAgreementDetailsDTO> getNonSelectableAssAgmtDetails(List<AssociatedAgreementDetailsDTO> assList){
		if(assList == null){
			return null;
		}
		List<AssociatedAgreementDetailsDTO> details = new ArrayList<AssociatedAgreementDetailsDTO>();
		for(AssociatedAgreementDetailsDTO assoList : assList){
			if(assoList.getEndDate() == null)
				continue;
			if(DateUtil.compareDates(assoList.getEndDate(), new java.util.Date()) < 0){
				details.add(assoList);
			}
		}
				
		return details;		
	}
	
	/**
	 * This anonymous inner class represents the converter to display
	 * the CommissionKind using the ComponentKindType enum
	 */
	private IConverter commKindConverter = new IConverter() {
		
		public Object convertToObject(String value, Locale locale) {
			if (value==null || value.length()==0) {
				return 0;
			}
			try {
				return Integer.parseInt(value);
			} catch (NumberFormatException e) {
				return 0;
			}
		}

		public String convertToString(Object value, Locale locale) {
			if (value!=null && value instanceof CommissionKindsDTO) {
				
				return ((CommissionKindsDTO)value).getValue();
			}
			return "None";
		}
		
	};
	
	/**
	 * This anonymous inner class represents the converter to display
	 * the Percentage value with truncated decimal places.
	 */
	private IConverter percentageConvertor = new IConverter() {
		
		public String convertToString(Object value, Locale locale) {
			if (value!=null && value instanceof Percentage) {
				Percentage val = ((Percentage)value);
				
				return val.toString(2);
				
			}
			return Percentage.ZERO_PERCENT.toString(2);
		}

		public Object convertToObject(String arg0, Locale arg1) {
			// TODO Auto-generated method stub
			return null;
		}
		
	};	
	
	private boolean isViewOnly()
	{
		if(getEditState() == EditStateType.VIEW || getEditState() == EditStateType.AUTHORISE)
			return true;
		if(existingAssAgmtRequest)
			return true;
		
		return false;
	}
}
