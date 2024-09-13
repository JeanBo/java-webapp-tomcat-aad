package za.co.liberty.web.pages.reports;

import java.io.File;
import java.net.URI;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.naming.NamingException;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.ajax.form.AjaxFormChoiceComponentUpdatingBehavior;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AbstractAutoCompleteRenderer;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteSettings;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.IAutoCompleteRenderer;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow.MaskType;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.image.ContextImage;
import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.Response;
import org.apache.wicket.util.convert.IConverter;
import org.apache.wicket.util.convert.converter.DateConverter;
import org.apache.wicket.util.resource.FileResourceStream;
import org.apache.wicket.util.resource.IResourceStream;

import com.inmethod.grid.DataProviderAdapter;
import com.inmethod.grid.IGridColumn;
import com.inmethod.grid.SizeUnit;

import za.co.liberty.business.guicontrollers.reports.IReportGUIController;
import za.co.liberty.constants.ISRSConstants;
import za.co.liberty.dto.gui.request.RequestEnquiryPeriodDTO;
import za.co.liberty.dto.reports.BranchOrUnitDTO;
import za.co.liberty.dto.reports.ChoiceOfSearchEnum;
import za.co.liberty.dto.reports.DocSearchResultDTO;
import za.co.liberty.dto.reports.HierarchyUsersEmailDTO;
import za.co.liberty.dto.reports.InfoSlipDocumentDTO;
import za.co.liberty.dto.userprofiles.ISessionUserProfile;
import za.co.liberty.exceptions.SystemException;
import za.co.liberty.exceptions.data.DataNotFoundException;
import za.co.liberty.exceptions.data.InconsistentDataException;
import za.co.liberty.helpers.lookup.ServiceLocator;
import za.co.liberty.helpers.util.DateUtil;
import za.co.liberty.interfaces.services.SBIDocumentType;
import za.co.liberty.web.constants.SRSAppWebConstants;
import za.co.liberty.web.data.enums.ComponentType;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.data.enums.fields.ReportGUIField;
import za.co.liberty.web.pages.interfaces.IMaintenanceParent;
import za.co.liberty.web.pages.panels.BasePanel;
import za.co.liberty.web.pages.panels.HelperPanel;
import za.co.liberty.web.pages.reports.model.MaintainReportsPageModel;
import za.co.liberty.web.system.SRSAuthWebSession;
import za.co.liberty.web.wicket.ajax.AjaxDownload;
import za.co.liberty.web.wicket.ajax.attributes.SRSAjaxCallListener;
import za.co.liberty.web.wicket.markup.html.form.SRSDateField;
import za.co.liberty.web.wicket.markup.html.form.SRSDropDownChoice;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGrid;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGrid.GridSizeUnit;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGridColumn;
import za.co.liberty.web.wicket.markup.html.grid.SRSGridRowSelectionCheckBox;
import za.co.liberty.web.wicket.markup.repeater.data.ListDataProvider;

/**
 * Panel for View Infoslips /ECS documents
 * @author pks2802
 * 01/11/2013
 *
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class ViewInfoSlipPanel extends BasePanel {

	private static final long serialVersionUID = 1L;
	
	private MaintainReportsPageModel pageModel;
	
	private FeedbackPanel feedBackPanel;
	
	private ViewInfoSlipForm viewInfoSlipForm;
	
	private  RadioGroup<ChoiceOfSearchEnum> choiceOfSearchOptions;//Search Option choice // Can be Branch /Unit name or All Serviced Advisors
	
	private  SRSDataGrid docSearchResultsGrid; // DataGrid
	private  List<IGridColumn> gridColumns;
	
	private  AutoCompleteTextField<BranchOrUnitDTO> branchUnitName;//AutoCompleteTextField
	private  SRSDropDownChoice periodPanel;//Dropdown
	private  HelperPanel startDatePanel;//DatePicker	
	private  HelperPanel endDatePanel;// Datepicker - Will be displayed only when Single advisor is selected.
	private  HelperPanel startDtLabelPanel;
	private  HelperPanel endDtLabelPanel;
	
	private List<BranchOrUnitDTO> branchOrUnitChoiceList;
	private BranchOrUnitDTO tempBranchDTO = new BranchOrUnitDTO(null,null,-1);
	private IMaintenanceParent parent;
	
	private AjaxButton searchButton;
	
	private AjaxButton emailIfsButton;
	private AjaxButton emailEcsButton;
	
	private ModalWindow resendInfoslipWindow;
	
	private List<DocSearchResultDTO> docSearchResults;
	
	private HelperPanel commOnlyPanel;
	private HelperPanel commOnlyLabelPanel;
	
		
	private transient IReportGUIController guiController;
	
	private String infoSlipImage = "images/ifs_image1.jpg";
	private String pdfIcon = "images/pdf_icon.png";
	
	
	
	public ViewInfoSlipPanel(String tab_panel_id, MaintainReportsPageModel model, IMaintenanceParent page, EditStateType editState, FeedbackPanel feedBackPanel) {
		super(tab_panel_id,editState,(MaintainReportsPage)page);
		this.pageModel = model;
		this.feedBackPanel = feedBackPanel;
		this.parent = page;
		add(getViewInfoSlipForm());
		add(resendInfoslipWindow = getResendInfoslipWindow());
	}
	
		

	public AjaxButton getEmailIFSButton() {
		if (emailIfsButton==null) {
			
			emailIfsButton = new AjaxButton("emailIfsButton") {
				private static final long serialVersionUID = 1L;
				@Override
				protected void onSubmit(AjaxRequestTarget target, Form form) {
					
					target.add(getFeedBackPanel());
					boolean bool = processForEmail(target, SBIDocumentType.INFOSLIP_DOCUMENT);
					if(!bool){
						target.add(getFeedBackPanel());
						return;
					}
					try {
						getDataModelForPanel().setSelectedDocumentUri(
								getURIOfDocument(getDataModelForPanel().getSelectedDocument(), SBIDocumentType.INFOSLIP_DOCUMENT));
						resendInfoslipWindow.show(target);
					} catch (DataNotFoundException e) {
						error("Data not found exception caused while Loading the document");
					}

				}

//				@Override
//				protected IAjaxCallDecorator getAjaxCallDecorator() {
//					return new AjaxCallDecorator() {
//						private static final long serialVersionUID = 1L;
//
//						public CharSequence decorateScript(CharSequence script) {
//							return "overlay(true);" + script;
//						}
//					};
//				}
				
				@Override
				protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
					super.updateAjaxAttributes(attributes);
				        
				        // SRS Convenience method for overLay hiding/showing
				        attributes.getAjaxCallListeners().add(new SRSAjaxCallListener());
				}
			};

			emailIfsButton.setDefaultFormProcessing(false);
			emailIfsButton.setOutputMarkupId(true);
			emailIfsButton.setOutputMarkupPlaceholderTag(true);
//			emailIfsButton.setEnabled(hasAddAccess());
		}
		return emailIfsButton;
	}
	
	public AjaxButton getEmailECSButton() {
		if (emailEcsButton==null) {
			emailEcsButton = new AjaxButton("emailEcsButton"){
				private static final long serialVersionUID = 1L;
				@Override
				protected void onSubmit(AjaxRequestTarget target, Form form) {
//					resendInfoslipWindow.close(target);
					target.add(getFeedBackPanel());
					boolean bool = processForEmail(target, SBIDocumentType.ECS_DOCUMENT);
					if(!bool){
						target.add(getFeedBackPanel());
						return;
					}
					try {
						getDataModelForPanel().setSelectedDocumentUri(
								getURIOfDocument(getDataModelForPanel().getSelectedDocument(), SBIDocumentType.ECS_DOCUMENT));
						resendInfoslipWindow.show(target);
					} catch (DataNotFoundException e) {
						error("Data not found exception caused while Loading the document");
					}
					
				}
				
//				@Override
//				protected IAjaxCallDecorator getAjaxCallDecorator() {
//					return new AjaxCallDecorator() {
//						private static final long serialVersionUID = 1L;
//
//						public CharSequence decorateScript(CharSequence script) {
//							return "overlay(true);" + script;
//						}
//					};
//				}
				
				@Override
				protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
					super.updateAjaxAttributes(attributes);
				        
				        // SRS Convenience method for overLay hiding/showing
				        attributes.getAjaxCallListeners().add(new SRSAjaxCallListener());
				}
			};
		}
		emailEcsButton.setDefaultFormProcessing(false);
		emailEcsButton.setOutputMarkupId(true);
		emailEcsButton.setOutputMarkupPlaceholderTag(true);
//		emailEcsButton.setEnabled(hasAddAccess());
		return emailEcsButton;
	}
	
	private boolean processForEmail(AjaxRequestTarget target, SBIDocumentType selectedDocument){
		if (docSearchResultsGrid.getSelectedItems().size()==0) {
			error("Please select a record for Resending document via email !");
			return false;
		}
		
		if(! SRSAuthWebSession.get().hasAddAccess(ViewInfoSlipPanel.this.getClass(), true)){
//			Can this even happen now??? Jean
//			if(!hasHierarchicalAccess()){
				error("You have not been allocated permission to use this facility - please contact User Access Management (UAM) for assistance.");
				return false;
//			}
		}
		
		Collection<IModel> collection = docSearchResultsGrid.getSelectedItems();
		
		/* Add selected grid items to selected list */				
		for (IModel model : collection) {
			DocSearchResultDTO itemDTO = (DocSearchResultDTO) model.getObject();
			
			if(selectedDocument == SBIDocumentType.INFOSLIP_DOCUMENT && 
					(itemDTO.getInfoslipId() == null ||itemDTO.getInfoslipId().intValue() == 0)){
				error("No infoslip document present for this record to email !");
				return false;
			}
			
			if(selectedDocument == SBIDocumentType.ECS_DOCUMENT && 
					(itemDTO.getEcsId() == null ||itemDTO.getEcsId().intValue() == 0)){
				error("No pdf document present for this record to email !");
				return false;
			}
			itemDTO.setSelectedDocType(selectedDocument);
			getDataModelForPanel().setSelectedDocument(itemDTO);
		}				
		return true;
	}
	
	private boolean hasHierarchicalAccess() {
		
		ISessionUserProfile userprofile = SRSAuthWebSession.get().getSessionUser();
		
		return userprofile.hasHierarchicalAccess();
		
	}



	public AjaxButton getSearchButton() {
		if (searchButton==null) {
			searchButton = new AjaxButton("searchButton") {
				private static final long serialVersionUID = 1L;
			@Override
			protected void onSubmit(AjaxRequestTarget target, Form form) {
				
				target.add(getFeedBackPanel());
				getDataModelForPanel().getDocSearchResults().clear();
								
				ChoiceOfSearchEnum choice = getDataModelForPanel().getSelectedUserChoiceOfSearch();
				Date startDate = getDataModelForPanel().getStartDate();
				Date endDate = getDataModelForPanel().getEndDate();
								
				if((choice == ChoiceOfSearchEnum.BRANCH_OR_UNITNAME || choice == ChoiceOfSearchEnum.ALL_SERVICED_ADVISOR) &&
						startDate == null){
					error("Please enter  Date !");
					target.add(getFeedBackPanel());
					return;
				}
				
				if(choice == ChoiceOfSearchEnum.BRANCH_OR_UNITNAME && getDataModelForPanel().getSelectedBranchOrUnit() == null){
					
					error("Please enter a Branch/Unit name to Search !");
					target.add(getFeedBackPanel());
					return;					
				}
				
				if(choice == ChoiceOfSearchEnum.SELECTED_ADVISOR){
					
					if(startDate == null || endDate == null){
						error("Please enter Start Date and End Date !");
						target.add(getFeedBackPanel());
						return;}
					else if(DateUtil.getInstance().compareDatePart(startDate, endDate)>0){
						error("Start Date is Greater than End Date! Please change");
						target.add(getFeedBackPanel());
						return;
					}
					Long advisor = getDataModelForPanel().getSelectedAdvisor();
					if(advisor == null || advisor.intValue() == 0){
						error("No Selected Advisor Present !");
						target.add(getFeedBackPanel());
						return;
					}					
					
				}
				
				try {
					docSearchResults = 
							getGuiController().doSearchForDocuments(getDataModelForPanel());
					
					if(docSearchResults == null || docSearchResults.size() == 0){
						error("No Records found");	
						target.add(docSearchResultsGrid);
						target.add(getFeedBackPanel());
						return;
					}
				} catch (DataNotFoundException e) {
					error("Data not found exception caused while retrieving Advisor Name");
					target.add(docSearchResultsGrid);
					target.add(getFeedBackPanel());
					return;
				}
				
				getDataModelForPanel().getDocSearchResults().clear();
				getDataModelForPanel().getDocSearchResults().addAll(docSearchResults);
				
				target.add(docSearchResultsGrid);//Refresh the Datagrid
				
				}
			};
					
			searchButton.setDefaultFormProcessing(false);
			searchButton.setOutputMarkupId(true);
		}
		return searchButton;
	}
	
	
	
	public SRSDropDownChoice getPeriodPanel() {
	if(periodPanel == null){
		
		IModel model = new IModel() {
			private static final long serialVersionUID = 1L;
			
			public Object getObject() {
				return getDataModelForPanel().getRequestEnquiryPeriod();
			}
			public void setObject(Object arg0) {
				getDataModelForPanel().setRequestEnquiryPeriod((RequestEnquiryPeriodDTO) arg0);
			}
			public void detach() {	
			}
		};
		periodPanel = new SRSDropDownChoice(ReportGUIField.PERIOD.getFieldId(), model, pageModel.getAllPeriodList());
		periodPanel.add(new AjaxFormComponentUpdatingBehavior("change") {
			private static final long serialVersionUID = 1L;
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				
				RequestEnquiryPeriodDTO dto = getDataModelForPanel().getRequestEnquiryPeriod();
				if (dto!=null) {
					
					target.add(startDatePanel);
					target.add(startDtLabelPanel);
					target.add(endDatePanel);
				}
			}		
		});
		
		periodPanel.setEnabled(isSingleAdvsisorSelected());		
		
		}
	
	periodPanel.setOutputMarkupId(true);
	periodPanel.setOutputMarkupPlaceholderTag(true);
	
	return periodPanel;
	}
	
	
	private boolean isSingleAdvsisorSelected(){
		
		return (getDataModelForPanel().getSelectedUserChoiceOfSearch() == ChoiceOfSearchEnum.SELECTED_ADVISOR);
		
	}


	public HelperPanel getCommOnlyPanel(){
		
			if (commOnlyPanel == null) {
				commOnlyPanel = createGUIPageField(ReportGUIField.VIEWCOMMONLY, 
						getDataModelForPanel(),
						ComponentType.CHECKBOX, false,true);		
				commOnlyPanel.setOutputMarkupId(true);
				commOnlyPanel.getEnclosedObject().setOutputMarkupId(true);
			}
			return commOnlyPanel;
		}
	
	public HelperPanel getCommOnlyLabelPanel(){
		if(commOnlyLabelPanel == null){
			commOnlyLabelPanel = createGUIFieldLabel(ReportGUIField.VIEWCOMMONLY,true);
		}
		return commOnlyLabelPanel;
	}
	
	/**
	 * @return the docSearchResultsGrid
	 */
	public SRSDataGrid getDocSearchResultsGrid() {
		if (docSearchResultsGrid == null) {
			docSearchResults = getDataModelForPanel().getDocSearchResults();
			if(docSearchResults == null){
				docSearchResults = new ArrayList<DocSearchResultDTO>();
				getDataModelForPanel().setDocSearchResults(docSearchResults);
			}
			
			docSearchResultsGrid = new SRSDataGrid("docSearchResultsGrid",
					new DataProviderAdapter(new ListDataProvider<DocSearchResultDTO>(
							docSearchResults)),
					getColumns(),EditStateType.VIEW);
			docSearchResultsGrid.setCleanSelectionOnPageChange(false);
			docSearchResultsGrid.setOutputMarkupId(true);
			docSearchResultsGrid.setAllowSelectMultiple(false);
			docSearchResultsGrid.setGridWidth(70, GridSizeUnit.PERCENTAGE);
			docSearchResultsGrid.setRowsPerPage(10);
			docSearchResultsGrid.setContentHeight(200, SizeUnit.PX);
			docSearchResultsGrid.setAutoResize(true);
		}
		
		
		docSearchResultsGrid.setOutputMarkupPlaceholderTag(true);
		
		return docSearchResultsGrid;
	}
	
	

	@SuppressWarnings("serial")
	private List<IGridColumn> getColumns() {
		if (gridColumns == null) {
			gridColumns = new ArrayList<IGridColumn>();
			
			/**
			 * Select Column for View/Email
			 */
			
				SRSGridRowSelectionCheckBox col = new SRSGridRowSelectionCheckBox("checkBox") {
					
				};			
				gridColumns.add(0,col.setInitialSize(35));
			
			
			
			SRSDataGridColumn<DocSearchResultDTO> agmtidCol = new SRSDataGridColumn<DocSearchResultDTO>("agreementNo",
					new Model("Agreement Id"),"agreementNo",getEditState()){
				
				private static final long serialVersionUID = 1L;

				@Override
				public Panel newCellPanel(WebMarkupContainer parent, String componentId, IModel rowModel, String objectProperty, EditStateType state, final DocSearchResultDTO data) {
					
					
						Label label = new Label("value",new PropertyModel(data,objectProperty));
						//Set the Currently selected row
						ViewInfoSlipPanel.this.getDataModelForPanel().setSelectedDocument(data);
						return HelperPanel.getInstance(componentId, label);
					}
				
			};
			agmtidCol.setSizeUnit(SizeUnit.PX);
			agmtidCol.setMinSize(100);
			agmtidCol.setInitialSize(100);
			gridColumns.add(agmtidCol);
			
					
			SRSDataGridColumn<DocSearchResultDTO> advFullNameCol = new SRSDataGridColumn<DocSearchResultDTO>("advisorFullName",
					new Model("Advisor Full Name"),"advisorFullName",getEditState()){
				private static final long serialVersionUID = 1L;

				@Override
				public Panel newCellPanel(WebMarkupContainer parent, String componentId, IModel rowModel, String objectProperty, EditStateType state, final DocSearchResultDTO data) {
					
					
					Label label = new Label("value",new PropertyModel(data,objectProperty));				
					return HelperPanel.getInstance(componentId, label);
				}
				
			};
			advFullNameCol.setSizeUnit(SizeUnit.PX);
			advFullNameCol.setMinSize(200);
			advFullNameCol.setInitialSize(200);
			gridColumns.add(advFullNameCol);
			
			SRSDataGridColumn<DocSearchResultDTO> statementDtCol = new SRSDataGridColumn<DocSearchResultDTO>("statementDate",
					new Model("Statement Date"),"statementDate",getEditState()){

				private static final long serialVersionUID = 1L;

				@Override
				public Panel newCellPanel(WebMarkupContainer parent, String componentId, IModel rowModel, String objectProperty, EditStateType state, final DocSearchResultDTO data) {
					
					
					Label label = new Label("value",new PropertyModel(data,objectProperty)){
						
						@Override
						public IConverter getConverter(Class type) {
							return new DateConverter(){
								@Override
								public DateFormat getDateFormat(Locale locale) {
									// TODO Auto-generated method stub
									return new SimpleDateFormat(SRSAppWebConstants.DATE_FORMAT);
								}
							};
						}
					};
				
					return HelperPanel.getInstance(componentId, label);				
				}
				
			
			};
			statementDtCol.setSizeUnit(SizeUnit.PX);
			statementDtCol.setMinSize(120);
			statementDtCol.setInitialSize(120);
			gridColumns.add(statementDtCol);
			
			SRSDataGridColumn<DocSearchResultDTO> statementTypeCol = new SRSDataGridColumn<DocSearchResultDTO>("statementType",
					new Model("Statement Type"),"statementType",getEditState()){

				private static final long serialVersionUID = 1L;

				@Override
				public Panel newCellPanel(WebMarkupContainer parent, String componentId, IModel rowModel, String objectProperty, EditStateType state, final DocSearchResultDTO data) {
					
					
						return super.newCellPanel(parent, componentId, rowModel, objectProperty, state,
							data);					
				}
				
			
			};
			statementTypeCol.setSizeUnit(SizeUnit.PX);
			statementTypeCol.setMinSize(120);
			statementTypeCol.setInitialSize(120);
			gridColumns.add(statementTypeCol);
			
			/**
			 * Infoslip icon to be displayed 
			 */
			SRSDataGridColumn<DocSearchResultDTO> infoslipViewer = new SRSDataGridColumn<DocSearchResultDTO>("infoslipId",
					new Model("View IFS"),"infoslipId",getEditState()) {
				
				@Override
				public Panel newCellPanel(WebMarkupContainer parent, String componentId, IModel rowModel, String objectProperty, EditStateType state, final DocSearchResultDTO data) {
								
					
					if(data.getInfoslipId() == null || data.getInfoslipId().intValue() == 0) {
						return new EmptyPanel(componentId);
					}

					return createDocumentViewPanel(componentId, data, SBIDocumentType.INFOSLIP_DOCUMENT, ".ifs", infoSlipImage);
			
			}
		};
		infoslipViewer.setSizeUnit(SizeUnit.PX);
		infoslipViewer.setInitialSize(60);
		infoslipViewer.setMinSize(60);
		gridColumns.add(infoslipViewer);
						
		
		/**
		 * ECS/pdf icon to be displayed 
		 */
		SRSDataGridColumn<DocSearchResultDTO> ecsViewer = new SRSDataGridColumn<DocSearchResultDTO>("ecsId",
				new Model("View PDF"),"ecsId",getEditState()) {
			
			@Override
			public Panel newCellPanel(WebMarkupContainer parent, String componentId, IModel rowModel, String objectProperty, EditStateType state, final DocSearchResultDTO data) {
							
				if(data.getEcsId() == null || data.getEcsId().intValue() == 0) {
					return new EmptyPanel(componentId);
				}
				return createDocumentViewPanel(componentId, data, SBIDocumentType.ECS_DOCUMENT, ".pdf", pdfIcon);
			}
		};
		ecsViewer.setSizeUnit(SizeUnit.PX);
		ecsViewer.setInitialSize(60);
		ecsViewer.setMinSize(60);
		gridColumns.add(ecsViewer);
		}
		
		return gridColumns;
	}
	
	
	/**
	 * Create the Document view panel, with blocking and download afterwards.
	 * 
	 * @param componentId
	 * @param data
	 * @param doctype
	 * @param extension
	 * @param imageLocation
	 * @return
	 */
	protected Panel createDocumentViewPanel(final String componentId, final DocSearchResultDTO data, 
			final SBIDocumentType doctype, String extension, final String imageLocation) {
	
		final String fileName = new StringBuilder().append(data.getAgreementNo()).append("_")
				.append(DateUtil.getInstance().getNewDateFormat().format(data.getStatementDate()))
				.append("_").append(doctype.getShortCode()).append(extension).toString(); 
		
		// AjaxDownload initiator after retrieve of document
		final AjaxDownload download = new AjaxDownload() {

			@Override
			protected IResourceStream getResourceStream() {
				File file = generateDocument(data, doctype);
				return new FileResourceStream(new org.apache.wicket.util.file.File(file));
			}

			@Override
			protected String getFileName() {
				return fileName;
			}

		};
	
		/**
		 *  Link to initiate the retrieve of the document 
		 */
		AjaxLink but = new AjaxLink<Void>("value") {
		  
			private static final long serialVersionUID = 1L;

			@Override
		    public void onClick(AjaxRequestTarget target) {	 
		        try {
					getURIOfDocument(data, doctype);
				} catch (DataNotFoundException e1) {
					getFeedBackPanel().error("Unable to retrieve Document from SBI. Data not found!!");
					target.add(getFeedBackPanel());
					return;
				}

		        // finally initiate the download
		        download.initiate(target);
		    }

//			@Override
//			protected IAjaxCallDecorator getAjaxCallDecorator() {
//				return new AjaxCallDecorator() {
//					private static final long serialVersionUID = 1L;
//
//					public CharSequence decorateScript(CharSequence script) {
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

		
		ContextImage image = new ContextImage("img", imageLocation); 
		image.add(new AttributeModifier("title", String.valueOf((SBIDocumentType.INFOSLIP_DOCUMENT==doctype) ?  data.getInfoslipId() : data.getEcsId())));
		image.add(new AttributeModifier("align","center"));
		
		but.add(new AttributeModifier("align","center"));
		but.setOutputMarkupId(true);
		
		Panel panel = HelperPanel.getInstance(componentId, (AbstractLink)but, image);
		panel.add(download);
		return panel;
		
	}



	/**
	 * @return the choiceOfSearchOption
	 */
	public RadioGroup<ChoiceOfSearchEnum> getChoiceOfSearchOptions() {
		
		if (choiceOfSearchOptions==null) {
		
		List<ChoiceOfSearchEnum> list = pageModel.getInfoslipDocDTO().getAvailableSearchChoiceList();	
		choiceOfSearchOptions = new RadioGroup<ChoiceOfSearchEnum>(
				"choiceOfSearch", new PropertyModel<ChoiceOfSearchEnum>(getDataModelForPanel(), "selectedUserChoiceOfSearch"));
		
		
		choiceOfSearchOptions.add(new ListView<ChoiceOfSearchEnum>("colValues", list) {

			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(ListItem<ChoiceOfSearchEnum> it) {
				
				it.add(new Radio<ChoiceOfSearchEnum>("colRadio", new Model(it.getModelObject())));
				it.add(new Label("colLabel", new Model(it.getModelObject().getDescription())));
				
				if (it.getModelObject()==ChoiceOfSearchEnum.BRANCH_OR_UNITNAME) {
					it.add(HelperPanel.getInstance("colPanel", getBranchUnitNameOptions("value")));
				} else {
					it.add(new EmptyPanel("colPanel"));
				}
						
			}
			
		});
		
					
		choiceOfSearchOptions.setOutputMarkupId(true);
		choiceOfSearchOptions.setOutputMarkupPlaceholderTag(true);
		
		choiceOfSearchOptions.add(new AjaxFormChoiceComponentUpdatingBehavior() { 
	            private static final long serialVersionUID = 1L; 

	            @Override 
	            protected void onUpdate(AjaxRequestTarget target) {
	            		            	
	            	target.add(getFeedBackPanel());	
	            	updateComponentEnabled(target);	            		                
	            } 
	        }); 
						
		}
		return choiceOfSearchOptions;
	}
	
	/**
	 * @return the statementDateLabelPanel
	 */
	public HelperPanel getStartDateLabelPanel() {
		if(startDtLabelPanel == null){
			startDtLabelPanel = createGUIFieldLabel(ReportGUIField.START_DATE,true);
		}
		return startDtLabelPanel;
	}

	/**
	 * @return the statementDateToLabelPanel
	 */
	public HelperPanel getEndDateToLabelPanel() {
		if(endDtLabelPanel == null){
			endDtLabelPanel = createGUIFieldLabel(ReportGUIField.END_DATE ,isSingleAdvsisorSelected());
		}
		return endDtLabelPanel;
	}


	/**
	 * @return the branchUnitNameOptions
	 */
	public AutoCompleteTextField getBranchUnitNameOptions(String id) {
		
		if(branchUnitName ==null){
		
		/**----------------------------------------------------**/
		if (getDataModelForPanel().getSelectedBranchOrUnit()!=null) {
			// Initialise choices
			branchOrUnitChoiceList = new ArrayList<BranchOrUnitDTO>();
			branchOrUnitChoiceList.add(getDataModelForPanel().getSelectedBranchOrUnit());
		}
		
		Model<BranchOrUnitDTO> model = new Model<BranchOrUnitDTO>(){
			private static final long serialVersionUID = 1L;

			@Override
			public BranchOrUnitDTO getObject() {				
				return getDataModelForPanel().getSelectedBranchOrUnit();
			}

			@Override
			public void setObject(BranchOrUnitDTO object) {
				getDataModelForPanel().setSelectedBranchOrUnit(object);				
			}
			
		};
		
		// Renderer
		IAutoCompleteRenderer<BranchOrUnitDTO> renderer = new AbstractAutoCompleteRenderer<BranchOrUnitDTO>() {

			private static final long serialVersionUID = 1L;

			@Override
			protected String getTextValue(BranchOrUnitDTO object) {
				if (object == null) {
					return "";
				}
				return ((BranchOrUnitDTO)object).toString();
			}

			@Override
			protected void renderChoice(BranchOrUnitDTO object, Response response,
					String criteria) {
				response.write(getTextValue(object));
			}
		};

		List<BranchOrUnitDTO> branchList = pageModel.getInfoslipDocDTO().getBranchOrUnitMangedByLoggedUser();
		final int branchCount = branchList.size();
		final int inputLengthMin = (branchCount>10) ? 2 : 0;
		final int inputLengthMax = (branchCount>10) ? 3 : 0;
		
		// Create the field
		branchUnitName = new AutoCompleteTextField<BranchOrUnitDTO>(id, 
				model, BranchOrUnitDTO.class, renderer, new AutoCompleteSettings().setPreselect(true)) {
			private static final long serialVersionUID = 1L;
			@SuppressWarnings("unchecked")
			@Override
			protected Iterator<BranchOrUnitDTO> getChoices(String input) {
				if (input.length()<=inputLengthMin) {
					return Collections.EMPTY_LIST.iterator();
				}
				branchOrUnitChoiceList = findBranchOrUnitWithNameLike(input);
				return branchOrUnitChoiceList.iterator();
			}
			
			
			
			@Override
			protected void onComponentTag(ComponentTag tag) {
				tag.put("style", "width:250px");
				super.onComponentTag(tag);
			}



			@Override
			public IConverter getConverter(Class type) {
				if(type == BranchOrUnitDTO.class){
					return new IConverter(){						
						private static final long serialVersionUID = 1L;
						public Object convertToObject(String value, Locale locale) {
							String strVal = value;
							strVal = strVal.trim();
							if (strVal.length()>=inputLengthMax) {
								// At least the name should be entered.
								String name = strVal;
//								(strVal.length()==7) ? strVal : strVal.substring(0, 
//										(strVal.length()>7) ? 7 : strVal.length());
								List<BranchOrUnitDTO> list =  findBranchOrUnitWithNameLike(name);
								if (list.size() > 0) {
									for(BranchOrUnitDTO branch : list){
										if(branch.getName().equalsIgnoreCase(name)){
	//										 Yay, found it
											getDataModelForPanel().setSelectedBranchOrUnit(branch);	
											branchOrUnitChoiceList.add(getDataModelForPanel().getSelectedBranchOrUnit());
	//										
											return getDataModelForPanel().getSelectedBranchOrUnit();
										}
									}
								} else {
//									System.out.println("  -- SEARCH returned x items = "+ list.size());
								}
							}
//							System.out.println("  -- dto   -- not found");
							
							/* */
							tempBranchDTO.setName(value);
							getDataModelForPanel().setSelectedBranchOrUnit(null);
							return tempBranchDTO;
						}
						
						public String convertToString(Object value, Locale locale) {							
							if(value != null && value instanceof BranchOrUnitDTO){
								return ((BranchOrUnitDTO)value).toString();
							}else{
								return null;
							}
						}						
					};
				}else{
					return super.getConverter(type);
				}
			}		
		};
		
		// Refresh this component when updated to force IModel logic to be applied.
		AjaxFormComponentUpdatingBehavior behaviour = new AjaxFormComponentUpdatingBehavior("change") {
			private static final long serialVersionUID = 1L;
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				//updateShowNextButton(target);
				//target.add(field);
			}
			@Override
			protected void onError(AjaxRequestTarget target, RuntimeException e) {
				super.onError(target, e);				
				if(getFeedBackPanel() != null){
					target.add(getFeedBackPanel());
				}
			}
			
			
		};
		branchUnitName.add(behaviour);
		branchUnitName.add(new AttributeModifier("autocomplete", "off"));
		branchUnitName.setRequired(true);
		branchUnitName.setLabel(new Model<String>("Branch OR Unit Name"));
		branchUnitName.setEnabled(false);
		branchUnitName.setOutputMarkupId(true);
		branchUnitName.setOutputMarkupPlaceholderTag(true);
		}
		
		return branchUnitName;
}	
	
	/**
	 * @return the statementDatePanel
	 */
	public HelperPanel getStartDatePanel() {
		
		if (startDatePanel == null) {
			startDatePanel = createGUIPageField(ReportGUIField.START_DATE, 
					getDataModelForPanel(),ComponentType.DATE_SELECTION_TEXTFIELD, false,true);
			if (startDatePanel.getEnclosedObject() instanceof SRSDateField) {
				 ((SRSDateField) startDatePanel.getEnclosedObject()).addNewDatePicker();
				
			}		
		}
		startDatePanel.setOutputMarkupId(true);
		startDatePanel.setOutputMarkupPlaceholderTag(true);
		return startDatePanel;			
	}
	
	public HelperPanel getEndDatePanel() {
		
		if (endDatePanel == null) {
			endDatePanel = createGUIPageField(ReportGUIField.END_DATE , 
					getDataModelForPanel(),ComponentType.DATE_SELECTION_TEXTFIELD, false,true);
			if (endDatePanel.getEnclosedObject() instanceof SRSDateField) {
				 ((SRSDateField) endDatePanel.getEnclosedObject()).addNewDatePicker();
			}	
			
			endDatePanel.setVisible(isSingleAdvsisorSelected());
			endDatePanel.setOutputMarkupId(true);
			endDatePanel.setOutputMarkupPlaceholderTag(true);
		}
		
		return endDatePanel;			
	}

	private HelperPanel createGUIPageField(ReportGUIField field,
			InfoSlipDocumentDTO propertyObject, ComponentType componentType,boolean required,
			boolean ajaxUpdateValue){
		
		HelperPanel helperPanel = null;		
	
		helperPanel = createPageField(propertyObject, field.getDescription(), 
				 field.getFieldId(), componentType,required,
				 ajaxUpdateValue, EditStateType.VIEW);
			
		helperPanel.setOutputMarkupId(true);
		helperPanel.setOutputMarkupPlaceholderTag(true);
		
		return helperPanel;
	}
	
	private HelperPanel createGUIFieldLabel(ReportGUIField field,boolean visible){
		
		Label lbl = new Label("value",field.getDescription());
		lbl.setEscapeModelStrings(false);
		HelperPanel panel = HelperPanel.getInstance(field.getLabelId(), lbl);
		
		panel.setOutputMarkupId(true);
		panel.setOutputMarkupPlaceholderTag(true);
		panel.setVisible(visible);
		return panel;
	}

	private Component getViewInfoSlipForm() {
		if (viewInfoSlipForm==null) {
			viewInfoSlipForm = new ViewInfoSlipForm("viewInfoSlipForm");
		}
		return viewInfoSlipForm;
	}
	
	public ModalWindow getResendInfoslipWindow() {
		if (resendInfoslipWindow==null) {
			resendInfoslipWindow = new ModalWindow("resendInfoslipWindow");
			resendInfoslipWindow.setTitle("Resend Document");
			resendInfoslipWindow.setPageCreator(new ModalWindow.PageCreator() {
				private static final long serialVersionUID = 1L;
				public Page createPage() {
					getDataModelForPanel().setHiearchyUsersEmailDTO(getHierarchyUsersForAdvisors(getDataModelForPanel().getSelectedDocument()));
					return new ResendInfoslipDocumentPage(resendInfoslipWindow, 
							getDataModelForPanel().getHiearchyUsersEmailDTO(),
							getDataModelForPanel().getSelectedDocumentUri());
				}
			});
			resendInfoslipWindow.setMinimalHeight(500);
			resendInfoslipWindow.setInitialHeight(500);
			resendInfoslipWindow.setMinimalWidth(800);
			resendInfoslipWindow.setInitialWidth(800);
			
			resendInfoslipWindow.setMaskType(MaskType.SEMI_TRANSPARENT);
			resendInfoslipWindow.setCssClassName(ModalWindow.CSS_CLASS_GRAY);
		}
		return resendInfoslipWindow;
	}
	
	/**
	 * Method to get the Hierarchy Users For Selected Advisors 	
	 * @param selectedDocument
	 * @return
	 */
	private List<HierarchyUsersEmailDTO> getHierarchyUsersForAdvisors(DocSearchResultDTO selectedDocument) {
		
		long agmtNo = selectedDocument.getAgreementNo();
		try {
			return getGuiController().getHiearchyUsersForAdvisor(agmtNo);
		} catch (DataNotFoundException e) {
			return null;
		} catch (InconsistentDataException e) {
			return null;
		}	
		
	}


	private class ViewInfoSlipForm extends Form {
		private static final long serialVersionUID = 1L;

		public ViewInfoSlipForm(String id) {
			super(id);
			add(getChoiceOfSearchOptions());
			add(getPeriodPanel());
			add(getStartDateLabelPanel());
			add(getStartDatePanel());
			add(getEndDateToLabelPanel());
			add(getEndDatePanel());
			add(getCommOnlyPanel());
			add(getCommOnlyLabelPanel());
			add(getDocSearchResultsGrid());
			add(getEmailIFSButton());	
			add(getEmailECSButton());
			add(getSearchButton());			
		}		
		
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
	
	private InfoSlipDocumentDTO getDataModelForPanel(){
		return pageModel.getInfoslipDocDTO();
	}
	
	private List<BranchOrUnitDTO> findBranchOrUnitWithNameLike(String input) {
		
		List<BranchOrUnitDTO> list = pageModel.getInfoslipDocDTO().getBranchOrUnitMangedByLoggedUser();
		List<BranchOrUnitDTO> ret = new ArrayList<BranchOrUnitDTO>();	
		if((list == null && list.size() == 0))
			return Collections.EMPTY_LIST;
		
		for(BranchOrUnitDTO branchOrUnitDTO:list){
			
			if(input.trim() == ISRSConstants.EMPTY_STRING 
					|| (branchOrUnitDTO.getName().toLowerCase().contains(input.trim().toLowerCase())))
				ret.add(branchOrUnitDTO);
		}
		
		
		return ret;		
	}
	
	private void updateComponentEnabled(AjaxRequestTarget target){
		
		ChoiceOfSearchEnum choice = getDataModelForPanel().getSelectedUserChoiceOfSearch();
		
		boolean showOrHide = Boolean.TRUE;
	
		if (branchUnitName!= null) {
			switch(choice){
			
			case SELECTED_ADVISOR:  
									   branchUnitName.setEnabled(false);
									   break;
								   
			case BRANCH_OR_UNITNAME:   
									   showOrHide = Boolean.FALSE;
									   branchUnitName.setEnabled(ifUserHasBranchAccess());
									   break;
			
			case ALL_SERVICED_ADVISOR: 
									   showOrHide = Boolean.FALSE;
									   branchUnitName.setEnabled(false);
									   break;
															
			default: //Not Possible					   
			
			}
		}
	   endDatePanel.setVisible(showOrHide);
	   endDtLabelPanel.setVisible(showOrHide);
	   periodPanel.setEnabled(showOrHide);
	   periodPanel.clearInput();
	   getDataModelForPanel().setRequestEnquiryPeriod(null);
	   getDataModelForPanel().setStartDate(null);
	   getDataModelForPanel().setEndDate(null);
	   ((TextField)startDatePanel.getEnclosedObject()).setModelObject(null);
	   ((TextField)endDatePanel.getEnclosedObject()).setModelObject(null);
	   
	   ((TextField)startDatePanel.getEnclosedObject()).clearInput();
	   ((TextField)endDatePanel.getEnclosedObject()).clearInput();
	   target.add(startDatePanel);
	   if (branchUnitName!=null)
		   target.add(branchUnitName);
	   target.add(endDatePanel);
	   target.add(endDtLabelPanel);
	   target.add(periodPanel);		 	   
		
	}
	
	
	//Branch Or Unit Selection is Only available to Users who either Manages or Administer  a Hierarchy Node
	private boolean ifUserHasBranchAccess() {
		
		ISessionUserProfile userprofile = SRSAuthWebSession.get().getSessionUser();
		
		return userprofile.hasHierarchicalAccess();
		
	}
	
	
	private File generateDocument(DocSearchResultDTO docSearchDTO,SBIDocumentType docType)
	{
		try {
			docSearchDTO.setSelectedDocType(docType);
			
			URI uri = getURIOfDocument(docSearchDTO, docType);
			
			File file = new File(uri);
			
			return file;		
			
		} catch (DataNotFoundException e) {
			error("Unable to retrieve Document from SBI. Data not found !!");			
		}
		
		return null;
	}
	
	
	
	
	private URI getURIOfDocument(DocSearchResultDTO selectedDocument, SBIDocumentType docType) throws DataNotFoundException{

		Long docId  = 0l;
		URI uri = null;

		switch(docType){
			case INFOSLIP_DOCUMENT : 	
				docId = selectedDocument.getInfoslipId();
				uri = selectedDocument.getInfoslipUri();
				break;
			case ECS_DOCUMENT : 
				docId = selectedDocument.getEcsId();
				uri = selectedDocument.getEcsUri();
				break;
		}
		
		if (uri == null && docId != null) {
			// We need to do a call first
			uri = getGuiController().retreiveDocumentFromSBI(SRSAuthWebSession.get().getSessionUser(), docId,docType);
			switch(docType){
				case INFOSLIP_DOCUMENT : 	
					selectedDocument.setInfoslipUri(uri);
					break;
				case ECS_DOCUMENT : 
					selectedDocument.setEcsUri(uri);
					break;
			}
		}		
		
		return uri;
				
	}
}