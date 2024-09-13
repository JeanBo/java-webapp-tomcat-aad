package za.co.liberty.web.pages.maintainagreement.fais;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.naming.NamingException;

import org.apache.commons.lang.SerializationUtils;
import org.apache.log4j.Logger;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.form.AjaxFormSubmitBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.cycle.RequestCycle;

import com.inmethod.grid.DataProviderAdapter;
import com.inmethod.grid.IGridColumn;
import com.inmethod.grid.SizeUnit;
import com.inmethod.grid.column.CheckBoxColumn;

import za.co.liberty.business.guicontrollers.agreement.IAgreementGUIController;
import za.co.liberty.database.enums.DatabaseEnumHelper;
import za.co.liberty.dto.agreement.maintainagreement.fais.SuperVisionEditGridDTO;
import za.co.liberty.dto.databaseenum.FAISSupervisionTypeDBEnumDTO;
import za.co.liberty.dto.party.contactdetail.AddressDTO;
import za.co.liberty.dto.party.fais.FAISLicenseCategoryDTO;
import za.co.liberty.dto.party.fais.supervision.FAISCategorySupervisionDTO;
import za.co.liberty.exceptions.data.ValidationException;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.helpers.lookup.ServiceLocator;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.data.pages.IModalMaintenancePageModel;
import za.co.liberty.web.pages.panels.BasePanel;
import za.co.liberty.web.pages.panels.HelperPanel;
import za.co.liberty.web.wicket.markup.html.form.SRSDateField;
import za.co.liberty.web.wicket.markup.html.form.SRSDropDownChoice;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGrid;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGrid.GridSizeUnit;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGridColumn;
import za.co.liberty.web.wicket.markup.html.grid.SRSGridRowSelectionCheckBox;
import za.co.liberty.web.wicket.markup.repeater.data.ListDataProvider;
import za.co.liberty.web.wicket.modal.SRSModalWindow;


public class SuperVisionEditPanel extends BasePanel{
	
	private transient Logger logger = Logger.getLogger(this.getClass());
	
	private SRSDataGrid supervisionGrid;
	private HashMap<String,SuperVisionEditGridDTO> guiData = new HashMap<String,SuperVisionEditGridDTO>();
	private  ArrayList gridList= null;
	private Collection <FAISCategorySupervisionDTO> supervisionDTOs;
	private List <FAISCategorySupervisionDTO> allSupervisionDTOs=new ArrayList<FAISCategorySupervisionDTO>();
	private List <FAISCategorySupervisionDTO> selectableSupervisionDTOs;
	private Date endDateOfPrevious=new Date();
	 FeedbackPanel feedback ;
	private List<FAISSupervisionTypeDBEnumDTO> enumDTOs=new ArrayList<FAISSupervisionTypeDBEnumDTO>();
	boolean showSelectedCheckBox=false;
	private FAISLicenseCategoryDTO currentFaisLicenseCategoryDTO=null;
	private long priorExperience;
	private boolean init = false;
	
	private Date agreementStartDate;
	/*private FAISCategorySupervisionDTO currentSupervisionDTO;*/
	private SuperVisionEditForm superVisionEditForm;
	CheckBoxColumn col;
	FAISSupervisionTypeDBEnumDTO currentEnumDTO;
	TextField endDatetxt;
	
	private String categoryID;
	private SRSModalWindow modalWindow=null;
		
	private transient IAgreementGUIController guiController;
	Button addButton, cancelButton, removeButton,doneButton;
	
	private IModalMaintenancePageModel<FAISLicenseCategoryDTO> pageModel;
 
	/**
	 * 
	 * @param id
	 * @param editState
	 * @param currentFaisLicenseCategoryDTO 
	 * 			Base object only used as read only properties with the collection of FaisCategorySupervisionDTO 
	 * 			being the updated value.
	 * @param window
	 * @param categoryID
	 * @param agreementStartDate
	 */
	public SuperVisionEditPanel(String id, EditStateType editState,FAISLicenseCategoryDTO currentFaisLicenseCategoryDTO, 
			final SRSModalWindow window,String categoryID,Date agreementStartDate) {
		super(id, editState);
		
		this.currentFaisLicenseCategoryDTO=currentFaisLicenseCategoryDTO;
		this.priorExperience= priorExperience;
		this.agreementStartDate=agreementStartDate;
		this.supervisionDTOs=currentFaisLicenseCategoryDTO.getFaisCategorySupervisionDTO();
		enumDTOs= DatabaseEnumHelper.getDatabaseDTO(FAISSupervisionTypeDBEnumDTO.class);
		//endDateOfPrevious=this.currentFaisLicenseCategoryDTO.getCategory_StartDate();
		this.modalWindow=window;
		this.categoryID=categoryID;
		
		/*
		 * Page model to respond to calling page due to new issues with ModalWindows.
		 */
		pageModel = new IModalMaintenancePageModel<FAISLicenseCategoryDTO>() {
			private static final long serialVersionUID = 1L;
			private FAISLicenseCategoryDTO selectedItem;
			private boolean success = false;
			
			@Override
			public void setSelectedItem(FAISLicenseCategoryDTO selected) {
				selectedItem = selected;
			}

			@Override
			public FAISLicenseCategoryDTO getSelectedItem() {
				return selectedItem;
			}

			@Override
			public boolean isModalWizardSucces() {
				return success;
			}

			@Override
			public void setModalWizardSuccess(boolean success) {
				this.success = success;
			}

			@Override
			public String getModalWizardMessage() {
				return null;
			}
		};
		pageModel.setSelectedItem(currentFaisLicenseCategoryDTO);
		modalWindow.setSessionModelForPage(pageModel);
		
		
		
//		window.setCloseButtonCallback(new ModalWindow.CloseButtonCallback(){
//			private static final long serialVersionUID = 1L;
//			
//			 public boolean onCloseButtonClicked(AjaxRequestTarget target) {
//				
//				 resetValueOnCloseOrCancel();
//				 return true;
//				
//			}
//
//			
//		});
		
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Override
	protected void onBeforeRender() {
		super.onBeforeRender();
		if (!init) {
			initGrid();
			add(feedback = new FeedbackPanel("feedback"));
			feedback.setOutputMarkupId(true);
			add(superVisionEditForm=new SuperVisionEditForm("superVisionEditForm"));
			init = true;
		}
	}
	
	/**
	 * 
	 *
	 */
	public void initGrid(){
		//get all the supervision from current fais category
		supervisionDTOs=currentFaisLicenseCategoryDTO.getFaisCategorySupervisionDTO();
		
		if(supervisionDTOs!=null && supervisionDTOs.size()>0 ){
		
			//loop through them	
			for(FAISCategorySupervisionDTO supervisionDTO: supervisionDTOs){
				//setup the grid dto
				SuperVisionEditGridDTO gridDTO=new SuperVisionEditGridDTO();
				
				gridDTO.setSelected(true);
				gridDTO.setSupervisionDTO(supervisionDTO);
				
				// clone the supervisionDTO to prevent datamodel update in case if window is closed.  
				gridDTO.setPreviousSupervisionDTO((FAISCategorySupervisionDTO)SerializationUtils.clone(supervisionDTO));
				
				//and put in to the guiData to display on the screen
				Date date =new Date();
				Calendar cal = Calendar.getInstance();
				if(supervisionDTO.getEffectiveTo()!=null){
				cal.setTime(supervisionDTO.getEffectiveTo());
				}
				date.setTime(cal.getTimeInMillis());
				endDateOfPrevious=date;
				guiData.put(supervisionDTO.getSupervisionTypeDBEnumDTO().getKey(), gridDTO);
			}
			
		}
		
	
	}
	
	/**
	 * 
	 * @author aaa1210
	 *
	 */
	public class SuperVisionEditForm extends Form {
		private static final long serialVersionUID = 5808296649559984427L;

		public SuperVisionEditForm(String id) {
			super(id);
			
			add(supervisionGrid = getSupervisionEditGrid());
			add(addButton=createAddButton("addButton"));
			add(cancelButton= createCancelButton("cancelButton"));
			add(removeButton=createRemoveButton("removeButton"));
			add(doneButton=createDoneButton("doneButton",this));
			
		}
		@Override
		protected void onSubmit() {
			
			if(selectableSupervisionDTOs==null && supervisionDTOs==null ){ return;}
			try {
				if(supervisionDTOs!=null && supervisionDTOs.size()>0){
					allSupervisionDTOs.addAll(supervisionDTOs);
				}
				
				//add current and new supervisions to a list and send that one for validation
				if(selectableSupervisionDTOs!=null && selectableSupervisionDTOs.size()>0){
					allSupervisionDTOs.addAll(selectableSupervisionDTOs);
				}
				getGuiController().validateSupervision(allSupervisionDTOs, currentFaisLicenseCategoryDTO.getCategory_StartDate(),categoryID,agreementStartDate);
			} catch (ValidationException e) {
				allSupervisionDTOs.clear();
				
				AjaxRequestTarget target = RequestCycle.get().find(AjaxRequestTarget.class);
				
				for(String msg:e.getErrorMessages()){
									
					if (target!=null) {
						target.add(feedback);
					}
					getFeedBackPanel().error(msg);
//					SuperVisionEditForm.this.error(msg);
				}
			}
		super.onSubmit();
	}

	}
	
	/**
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private List<IGridColumn> getColumns() {
	
		List<IGridColumn> columns = new ArrayList<IGridColumn>(4);
		col = new SRSGridRowSelectionCheckBox(
		"checkBox");
		/*if(true){
			col.getGrid().getAllColumns().get(1).
		}*/
		columns.add(col.setInitialSize(30));

		
		
				
		/**
		 * add option column
		 */ 
		 
		 columns.add(new SRSDataGridColumn<SuperVisionEditGridDTO>("supervisionDTO.supervisionTypeDBEnumDTO",
					new Model("Type"), "supervisionDTO.supervisionTypeDBEnumDTO",
					getEditState()) {
				private static final long serialVersionUID = 1L;

				@SuppressWarnings("serial")
				@Override
				public Panel newCellPanel(WebMarkupContainer parent,
						String componentId, IModel rowModel, String objectProperty,
						EditStateType state, final SuperVisionEditGridDTO data) {					
					
					
					final SRSDropDownChoice choice = new SRSDropDownChoice("value", new PropertyModel(data,objectProperty), enumDTOs, new ChoiceRenderer(),"Select");
					
					//choice.add(new SimpleAttributeModifier("change","javascript: alert('ashok ');"));
					choice.add(new AjaxFormComponentUpdatingBehavior("change"){
						
						@Override
						protected void onUpdate(AjaxRequestTarget target) {
							
							currentEnumDTO=data.getSupervisionDTO().getSupervisionTypeDBEnumDTO();
													
								
						}
				
					});
				
				choice.setEnabled(true);
							
				choice.setNullValid(true);
					return HelperPanel.getInstance(componentId, choice);
				}
			}.setInitialSize(140));
		 /**
		  * add effective from date column
		  */
		 columns.add(new SRSDataGridColumn<SuperVisionEditGridDTO>("supervisionDTO.effectiveFrom", new Model("Start Date"), 
					"supervisionDTO.effectiveFrom", getEditState()){
				private static final long serialVersionUID = 1L;

				@SuppressWarnings("serial")
				@Override
				public Panel newCellPanel(WebMarkupContainer parent,
						String componentId, IModel rowModel, String objectProperty,
						EditStateType state, final SuperVisionEditGridDTO data) {
					SRSDateField startDate = new SRSDateField("value", new PropertyModel(data,
							objectProperty));
					
					startDate.setEnabled(false);
					startDate.add(new AttributeModifier(
							"maxlength", "10"));
					startDate.add(new AttributeModifier("style",
							"width: 67px;"));
					startDate.add(new AjaxFormComponentUpdatingBehavior("change"){

						@Override
						protected void onUpdate(AjaxRequestTarget target) {
							
						}
						
					});
					if (!getEditState().isViewOnly()){
						startDate.setEnabled(true);
						startDate.addNewDatePicker();
					}
					return HelperPanel.getInstance(componentId, startDate);
					
				}
			
			}.setInitialSize(190));
		 
			/**
			 * add effective to date column
			 */
		 columns.add(new SRSDataGridColumn<SuperVisionEditGridDTO>("supervisionDTO.effectiveTo", new Model("End Date"), "supervisionDTO.effectiveTo", getEditState()){
				private static final long serialVersionUID = 1L;

				@SuppressWarnings({ "serial", "unchecked" })
				@Override
				public Panel newCellPanel(WebMarkupContainer parent,
						String componentId, IModel rowModel, String objectProperty,
						EditStateType state, final SuperVisionEditGridDTO data) {
					SRSDateField endDate = new SRSDateField("value", new PropertyModel(data,
							objectProperty));
					endDatetxt=endDate;
					endDate.setEnabled(false);
					endDate.add(new AttributeModifier(
							"maxlength", "10"));
					endDate.add(new AttributeModifier("style",
							"width: 67px;"));
					endDate.add(new AjaxFormComponentUpdatingBehavior("change"){

						@Override
						protected void onUpdate(AjaxRequestTarget target) {
							addButton.setEnabled(true);
							Date date =new Date();
							Calendar cal = Calendar.getInstance();
							if(data.getSupervisionDTO().getEffectiveTo()!=null){							
								cal.setTime(data.getSupervisionDTO().getEffectiveTo());
							}
							date.setTime(cal.getTimeInMillis());
											
							endDateOfPrevious=date;
							target.add(addButton);
										
						}
						
					});
					if (!getEditState().isViewOnly()){
						endDate.setEnabled(true);
						endDate.addNewDatePicker();
					}
					return HelperPanel.getInstance(componentId, endDate);
					
				}
			}.setInitialSize(190));
		 return columns;
		
		
	}
	
	/**
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private SRSDataGrid getSupervisionEditGrid() {
	gridList=new ArrayList<SuperVisionEditGridDTO>(guiData.values());	        
		  SRSDataGrid innerGrid=new SRSDataGrid("SupervisionEditPanel", new DataProviderAdapter(
					new ListDataProvider<SuperVisionEditGridDTO>(
							gridList)), getColumns(), getEditState(),null);
		  innerGrid.setOutputMarkupId(true);
		  innerGrid.setCleanSelectionOnPageChange(false);
		  innerGrid.setClickRowToSelect(false);
		  innerGrid.setAllowSelectMultiple(true);
		  innerGrid.setGridWidth(100, GridSizeUnit.PERCENTAGE);
		  innerGrid.setRowsPerPage(5);
		  innerGrid.setContentHeight(100, SizeUnit.PX);
		  innerGrid.setOutputMarkupPlaceholderTag(true);
		  innerGrid.setVisible(true);
		  return innerGrid;
	}
	
	/**
	 * 
	 * @param id
	 * @return
	 */
	private Button createAddButton(String id) {
	final Button button = new Button(id);
	
	@SuppressWarnings("unused")
	final Calendar cal = Calendar.getInstance();
	button.add(new AjaxFormComponentUpdatingBehavior("click") {
		private static final long serialVersionUID = 0L;

		@SuppressWarnings("unchecked")
		@Override
		protected void onUpdate(AjaxRequestTarget target) {
			 
			//add only enumDTOs has atleast one element
			if (enumDTOs.size() > 0) {
					//
					SuperVisionEditGridDTO gridDTO = new SuperVisionEditGridDTO();
					FAISCategorySupervisionDTO supervisionDTO = new FAISCategorySupervisionDTO();
					supervisionDTO.setCreatedBy("");
					supervisionDTO.setCreationTime(new Date());
					Date date=new Date();
					Calendar cal = Calendar.getInstance();
					if(supervisionDTO.getEffectiveTo()!=null){
					cal.setTime(supervisionDTO.getEffectiveTo());
					}
					
					endDateOfPrevious=date;
					if(( supervisionDTOs==null || supervisionDTOs.size()<1) && (selectableSupervisionDTOs==null || selectableSupervisionDTOs.size()<1 )){
						endDateOfPrevious=new Date();
					}else if(selectableSupervisionDTOs!=null){
						if(selectableSupervisionDTOs.size()>0){
						cal.setTime(selectableSupervisionDTOs.get( (selectableSupervisionDTOs.size()-1)).getEffectiveTo());
						date.setTime(cal.getTimeInMillis());
						endDateOfPrevious=date;
						}
					}else if(supervisionDTOs!=null){
						
						cal.setTime(new ArrayList<FAISCategorySupervisionDTO>(supervisionDTOs).get((supervisionDTOs.size()-1)).getEffectiveTo());
						date.setTime(cal.getTimeInMillis());
						endDateOfPrevious=date;
					}
					
					supervisionDTO.setEffectiveFrom(endDateOfPrevious);
								

					//FAISSupervisionTypeDBEnumDTO supervisionTypeDBEnumDTO=
					supervisionDTO
							.setFaisCategoryid(currentFaisLicenseCategoryDTO
									.getOid());
					
					supervisionDTO.setVersion(0);
					supervisionDTO.setSupervisionTypeDBEnumDTO(currentEnumDTO);
					gridDTO.setSupervisionDTO(supervisionDTO);

					gridDTO.setSelected(false);

					if (selectableSupervisionDTOs == null) {
						// and if it is then initialize it
						selectableSupervisionDTOs = new ArrayList<FAISCategorySupervisionDTO>();

					}
					selectableSupervisionDTOs.add(supervisionDTO);
					gridList.add(gridDTO);
					//guiData.put(supervisionDTO.getSupervisionTypeDBEnumDTO().getKey(), gridDTO);
				}
													
				addButton.setEnabled(false);
				removeButton.setEnabled(true);
				doneButton.setEnabled(true);
				target.add(removeButton);
				target.add(doneButton);
				target.add(addButton);
		target.add(supervisionGrid);
		}
	});		
	if(getEditState() == EditStateType.AUTHORISE){			
		button.setVisible(false);
	}
	button.setOutputMarkupId(true);
	button.setOutputMarkupPlaceholderTag(true);
	
	return button;
}
	/**
	 * 
	 * @param id
	 * @return
	 */
	private Button createRemoveButton(String id) {
		final Button button = new Button(id);
		button.add(new AjaxFormComponentUpdatingBehavior("click") {
			private static final long serialVersionUID = 0L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				List<Object> supervisions = supervisionGrid.getSelectedItemObjects();
				if(supervisions!=null){
				for(Object supervision : supervisions ){
					SuperVisionEditGridDTO gridDTO=(SuperVisionEditGridDTO) supervision;
										
					if (selectableSupervisionDTOs != null) {
							if (selectableSupervisionDTOs.contains(gridDTO.getSupervisionDTO())) {
								selectableSupervisionDTOs.remove(gridDTO.getSupervisionDTO());
								gridList.remove(supervision);
							}
						} //the else if below is commented out as we dont want to delete the existing record 
						else if (supervisionDTOs.contains(gridDTO.getSupervisionDTO())) {
							/*AjaxRequestTarget.get().addComponent(feedback);
							superVisionEditForm.error("You can not remove "+gridDTO.getSupervisionDTO().getSupervisionTypeDBEnumDTO().getName()+" type.");*/
							//throw new CommunicationException("You can not remove "+gridDTO.getSupervisionDTO().getSupervisionTypeDBEnumDTO().getName()+" type.");
							supervisionDTOs.remove(supervision);
							if(currentFaisLicenseCategoryDTO.getFaisCategorySupervisionDTO().contains(gridDTO.getSupervisionDTO())){
								currentFaisLicenseCategoryDTO.getFaisCategorySupervisionDTO().remove(gridDTO.getSupervisionDTO());
							}
							gridList.remove(supervision);
						}
					}
				}
				addButton.setEnabled(true);
				target.add(addButton);
				target.add(supervisionGrid);
				
			}
		});		
		if(getEditState() == EditStateType.AUTHORISE){			
			button.setVisible(false);
		}
		
		button.setOutputMarkupId(true);
		button.setOutputMarkupPlaceholderTag(true);
		
		return button;
	}
	/**
	 * 
	 * @param id
	 * @return
	 */
	private Button createCancelButton(String id) {
		final Button button = new Button(id);
		button.add(new AjaxFormComponentUpdatingBehavior("click") {
			private static final long serialVersionUID = 0L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				List<Object> supervisions = supervisionGrid.getSelectedItemObjects();
			/*	if(supervisions!=null){
				for(Object supervision : supervisions ){
					SuperVisionEditGridDTO gridDTO=(SuperVisionEditGridDTO) supervision;
				if (selectableSupervisionDTOs != null) {
					if (selectableSupervisionDTOs.contains(gridDTO.getSupervisionDTO())) {
						selectableSupervisionDTOs.remove(gridDTO.getSupervisionDTO());
						gridList.remove(supervision);
					}
				}
				}
				}*/
//				resetValueOnCloseOrCancel();
				modalWindow.close(target);
			}
		});		
		if(getEditState() == EditStateType.AUTHORISE){			
			button.setVisible(false);
		}
		button.setOutputMarkupId(true);
		button.setOutputMarkupPlaceholderTag(true);
			
		return button;
	}
	
	
	/**
	 * 
	 * @param id
	 * @return
	 */
	
	private Button createDoneButton(String id,Form form) {
		final Button button ;//= new Button(id);
		button = new Button(id);
		button.setVisible(getEditState()!= EditStateType.VIEW);
		
		button.add(new AjaxFormSubmitBehavior("click") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onError(AjaxRequestTarget target) {
				pageModel.setModalWizardSuccess(false);
				modalWindow.setSessionModelForPage(pageModel);
				// error occured, add to feedback and display
				target.add(getFeedBackPanel());

			}

			@Override
			protected void onSubmit(AjaxRequestTarget target) {
				
				
				if(selectableSupervisionDTOs!=null && selectableSupervisionDTOs.size()>0){
					// loop through and add if validation is successfull
					for (FAISCategorySupervisionDTO selectableSupervisionDTO :selectableSupervisionDTOs){
						
						if(supervisionDTOs==null){
							// and if it is then add it to faislicensecategory
							
							supervisionDTOs=new ArrayList<FAISCategorySupervisionDTO>();
							
							currentFaisLicenseCategoryDTO.setFaisCategorySupervisionDTO(supervisionDTOs);
						}
						
						supervisionDTOs.add(selectableSupervisionDTO);
					}
					
				}
				pageModel.setModalWizardSuccess(true);
				modalWindow.setSessionModelForPage(pageModel);
				
				target.add(getFeedBackPanel());
				modalWindow.close(target);
//				do_Ok(target);
			}
		});
		
//		 button=(Button) new AjaxButton(id, form) {
//			private static final long serialVersionUID = 1L;
//
//			@Override
//			protected void onSubmit(AjaxRequestTarget target, Form form)
//
//			{
//				
//								
//				super.onSubmit();
//				if(selectableSupervisionDTOs!=null && selectableSupervisionDTOs.size()>0){
//					// loop through and add if validation is successfull
//					for (FAISCategorySupervisionDTO selectableSupervisionDTO :selectableSupervisionDTOs){
//						
//						if(supervisionDTOs==null){
//							// and if it is then add it to faislicensecategory
//							
//							supervisionDTOs=new ArrayList<FAISCategorySupervisionDTO>();
//							
//							currentFaisLicenseCategoryDTO.setFaisCategorySupervisionDTO(supervisionDTOs);
//						}
//						
//						supervisionDTOs.add(selectableSupervisionDTO);
//					}
//					
//				}
//				
//				target.add(getFeedBackPanel());
//				modalWindow.setSessionModelForPage(pageModel);
//				//
//			}
//			
//		}.setVisible(getEditState()!= EditStateType.VIEW);
			
		
		//button.setEnabled(false);
		return button;
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
		

	// jzb0608 - removed as we are now using session beans to move data between modal window and the base page.
//	/**
//	 * Resets the value to its original values if closed or canceled.
//	 * 
//	 * 
//	 */
//	private void resetValueOnCloseOrCancel() {
//		for(String key : guiData.keySet()){
//			SuperVisionEditGridDTO superVisionEditGridDTO=guiData.get(key);
//			FAISCategorySupervisionDTO supervisionDTO=superVisionEditGridDTO.getSupervisionDTO();
//			FAISCategorySupervisionDTO previousSupervisionDTO=superVisionEditGridDTO.getPreviousSupervisionDTO();
//				
//			if(supervisionDTO.getSupervisionTypeDBEnumDTO().getKey()!=previousSupervisionDTO.getSupervisionTypeDBEnumDTO().getKey()){
//				supervisionDTO.setSupervisionTypeDBEnumDTO(previousSupervisionDTO.getSupervisionTypeDBEnumDTO());
//				}
//			
//			if(supervisionDTO.getEffectiveFrom()!=previousSupervisionDTO.getEffectiveFrom()){
//				supervisionDTO.setEffectiveFrom(previousSupervisionDTO.getEffectiveFrom());
//			}
//			if(supervisionDTO.getEffectiveTo()!=previousSupervisionDTO.getEffectiveTo()){
//				supervisionDTO.setEffectiveTo(previousSupervisionDTO.getEffectiveTo());
//			}
//		}
//	}
}
