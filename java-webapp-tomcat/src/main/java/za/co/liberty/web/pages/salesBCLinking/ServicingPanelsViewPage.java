package za.co.liberty.web.pages.salesBCLinking;

import org.apache.log4j.Logger;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.Model;

import za.co.liberty.business.guicontrollers.request.IRequestEnquiryGuiController;
import za.co.liberty.web.pages.BaseWindowPage;
import za.co.liberty.web.pages.salesBCLinking.model.SalesBCLinkingPageModel;

/**
 * Page for viewing List of panels within a Branch in a modal window.
 * 
 * @author SSM2707 - 11 Nov 2015
 * 
 */
public class ServicingPanelsViewPage extends BaseWindowPage {
	
	


	/* Constants */
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger
			.getLogger(ServicingPanelsAuthorisationPage.class);
	
	private transient IRequestEnquiryGuiController guiController;
	
	
	
	/* Form components */
	protected FeedbackPanel feedBackPanel;
	protected Form<?> pageForm;
	protected SalesBCLinkingPageModel pageModel;
	protected ModalWindow window;
	
	
	/**
	 * Default constructor, adds form
	 */
	public ServicingPanelsViewPage(ModalWindow window,SalesBCLinkingPageModel localPageModel) {
		super();
		pageModel = localPageModel;
		this.window = window;
		this.add(pageForm=createPageFormField("pageForm"));
	}
	
	/**
	 * Create the form and panel
	 * 
	 * @param id
	 * @return
	 */
	private Form<?> createPageFormField(String id) {
		Form<?> form = new Form<Object>(id);
		
		final Model<SalesBCLinkingPageModel> model = new Model<SalesBCLinkingPageModel>(pageModel);
		
		ServicingPanelsViewPanel panel = new ServicingPanelsViewPanel("servicingViewPanel", model,window);
		form.add(panel);
		return form;
	}
	
	@Override
	public String getPageName() {
		return "Servicing Relationship Authorisation Page";
	}

//	/**
//	 * Return an instance to the GuiController bean for this page.
//	 * 
//	 * @return
//	 */
//	private IRequestEnquiryGuiController getGuiController() {
//		if (guiController==null) {
//			guiController = (IRequestEnquiryGuiController) SRSAuthWebSession.get().getEJBReference(
//					"ejblocal:"+IRequestEnquiryGuiController.class.getName());
//		}
//		return guiController;
//	}
//
//	private RequestEnquiryDTO createRequestModel() {
//		RequestEnquiryDTO dto = new RequestEnquiryDTO();
//		dto.setRequestKind(RequestKindType.MaintainCrossRegionServicingRelationships);
//		DateUtil dateUtil = DateUtil.getInstance();
//		dto.setStartDate(dateUtil.addMonths(new Date(),-2));
//		dto.setEndDate(new Date());
//		dto.setRequestDateType(RequestDateType.REQUEST);
//		dto.setRequestStatus(RequestStatusType.REQUIRES_AUTHORISATION);
//		dto.setSearchResultList(new ArrayList<RequestEnquiryRowDTO>());
//		dto.setBulkAuthoriseType(RequestEnquiryBulkAuthoriseType.PANEL_TRANSFER);
//		return dto;
//	}
//	
//	/**
//	 * Initialise the pageModel
//	 * 
//	 * @return
//	 */
//	private RequestEnquiryModel createPageModel() {
//
//		IRequestEnquiryGuiController controller = getGuiController();
//		RequestEnquiryModel model = new RequestEnquiryModel();
//		
//		/*
//		 * Set the lists
//		 */
//		model.setAllRequestDateType(controller.getAllRequestDateTypeList());
//		model.setAllPropertyOnlyRequestKindSet(controller.getAllPropertyOnlyRequestKindSet());
//		
//		
//		
//		return model;
//	}
	


//	/* Constants */
//	private static final long serialVersionUID = 1L;
//
//	/* Form components */
//	protected FeedbackPanel feedBackPanel;
//	protected ModalWindow modalWindow;
//
//	/* Attributes */
//	private transient ISalesBCLinkingGuiController guiController;
//
//	private BranchDetailsDTO branchDTO;
//	@SuppressWarnings("unused")
//	private Component contentPanel;
//	private Form panelViewForm;
//	private SalesBCLinkingPageModel model;
//	
//	private List<LinkedAdviserDTO> panelDataList;
//	
//	private SRSDataGrid panelDataGrid;
//
//	
//	private Form exportForm;
//	
//	protected Button exportButton;
	
	
	
	
	
	
	
//	/**
//	 * Default constructor.
//	 * 
//	 * @param modalWindow
//	 * @param dto
//	 * @param contextType
//	 * @param pageOptions
//	 */
//	public ServicingPanelsViewPage(ModalWindow modalWindow,
//			BranchDetailsDTO branchOID, SalesBCLinkingPageModel model) {
//		super();
//		this.modalWindow = modalWindow;
//		this.branchDTO = branchOID;
//		this.model = model;
//
//		/*Add Form*/
//		add(panelViewForm = new PanelViewForm("panelViewForm"));
//		/* Add components */
//		panelViewForm.add(feedBackPanel = (FeedbackPanel) new FeedbackPanel("searchMessages")
//				.setOutputMarkupId(true));
//		panelViewForm.add(createBranchNameField("branchName_display", "name"));
//		panelViewForm.add(createRelTablePanel("panelListTable"));
//		panelViewForm.add(createCloseButton("closeBtn",panelViewForm));
//		panelViewForm.add(exportForm=createExportForm("exportForm"));
//		
//
//	}
//
//	private Panel createRelTablePanel(String id) {
//
//		/*
//		 * Get the data list to be displayed in the table from the GUI
//		 * Controller
//		 */
//		
//		if (panelDataList==null) {
//			panelDataList = getPanelDataList(branchDTO.getOid());
//		}
//		
//		
//		panelDataGrid = new SRSDataGrid(id, new SRSDataProviderAdapter(
//				new ListDataProvider(panelDataList)),
//				createInternalTableFieldColumns("panelListTable"),
//				EditStateType.VIEW) {
//			private static final long serialVersionUID = 1L;
//		};
//		panelDataGrid.setAutoResize(true);
//		panelDataGrid.setOutputMarkupId(true);
//		panelDataGrid.setCleanSelectionOnPageChange(false);
//		panelDataGrid.setClickRowToSelect(false);
//		panelDataGrid.setAllowSelectMultiple(false);
//		panelDataGrid.setGridWidth(98, GridSizeUnit.PERCENTAGE);
//		panelDataGrid.setRowsPerPage(50);
//		panelDataGrid.setContentHeight(300, SizeUnit.PX);
//		return panelDataGrid;
//	}
//	
//
//	@Override
//	public String getPageName() {
//		return "Servicing Panels";
//	}
//
//	private List<LinkedAdviserDTO> getPanels(long branchOID) {
//		List<ServicingPanelDTO> panels = new ArrayList<ServicingPanelDTO>();
//		if (model.getServicingPanels() == null
//				|| model.getServicingPanels().size() <= 0) {
//			panels = getGUIController().findAllServicingPanels(branchOID);
//		} else {
//			panels = model.getServicingPanels();
//		}
//
//		/* Generate the list of advisers and panel data for display */
//
//		List<LinkedAdviserDTO> advList = new ArrayList<LinkedAdviserDTO>();
//
//		for (ServicingPanelDTO servPanelDTo : panels) {
//			if (servPanelDTo.getLinkedAdvisersList() != null
//					&& servPanelDTo.getLinkedAdvisersList().size() > 0) {
//				for (LinkedAdviserDTO adv : servPanelDTo
//						.getLinkedAdvisersList()) {
//					adv.setPanelStatus(servPanelDTo.getStatusCode());
//					adv.setPanelName(servPanelDTo.getPanelName());
//					advList.add(adv);
//				}
//			} else if (servPanelDTo.getLinkedAdvisersList() == null
//					|| servPanelDTo.getLinkedAdvisersList().size() <= 0) {
//				/*
//				 * If the panel does not contain advisers linked to it, display
//				 * the panel information without the advisers.
//				 */
//				LinkedAdviserDTO adv = new LinkedAdviserDTO();
//				adv.setPanelStatus(servPanelDTo.getStatusCode());
//				adv.setPanelName(servPanelDTo.getPanelName());
//				adv.setServiceType(servPanelDTo.getServiceType());
//				advList.add(adv);
//			}
//		}
//
//		return advList;
//	}
//	
//	private List<LinkedAdviserDTO> getPanelDataList(long branchOID) {
//		panelDataList = getPanels(branchOID);
//
//		Collections.sort(panelDataList, new Comparator<LinkedAdviserDTO>() {
//			public int compare(LinkedAdviserDTO s1, LinkedAdviserDTO s2) {
//				if (s1.getPanelName() == null
//						&& s2.getPanelName() == null) {
//					return -1;
//				} else if (s1.getPanelName() != null
//						&& s2.getPanelName() == null) {
//					return -1;
//				} else if (s1.getPanelName() == null
//						&& s2.getPanelName() != null) {
//					return 1;
//				} else {
//					return s1.getPanelName().compareToIgnoreCase(
//							s2.getPanelName());
//				}
//			}
//		});
//		
//		return panelDataList;
//	}
//
//	/**
//	 * Decorate the style tag to hide the component
//	 * 
//	 * @param isHidden
//	 *            Hide component if true.
//	 * @param tag
//	 */
//	@SuppressWarnings("unused")
//	private void decorateComponentStyle(boolean isHidden, ComponentTag tag) {
//		if (!isHidden) {
//			return;
//		}
//		String val = (String) tag.getAttributes().get("style");
//		val = (val == null) ? "" : val;
//		val += " ;visibility:hidden;";
//		tag.put("style", val);
//	}
//
//	@Override
//	public FeedbackPanel getFeedBackPanel() {
//		return feedBackPanel;
//	}
//
//	@Override
//	public boolean isShowFeedBackPanel() {
//		return false;
//	}
//
//	protected ISalesBCLinkingGuiController getGUIController() {
//		if (guiController == null) {
//			try {
//				guiController = ServiceLocator
//						.lookupService(ISalesBCLinkingGuiController.class);
//			} catch (NamingException namingErr) {
//				CommunicationException comm = new CommunicationException(
//						"ISalesBCLinkingGuiController can not be looked up!");
//				throw new CommunicationException(comm);
//			}
//		}
//		return guiController;
//	}
//	
//	/**
//	 * A generic cancel button that invalidates the page
//	 * 
//	 * @param id
//	 * @return
//	 */
//	protected Button createCloseButton(String id, Form form) {
//		
//		Button button = new Button(id);
//		button.add(new AjaxFormComponentUpdatingBehavior("click") {
//			private static final long serialVersionUID = 1L;
//
//			@Override
//			protected void onUpdate(AjaxRequestTarget target) {					
//				//currentAction = ButtonSelected.CANCEL;
//				modalWindow.close(target);
//			}				
//			
//		});
//		return button;
//	}
////		
////		AjaxButton button = new AjaxButton(id, form) {
////
////			private static final long serialVersionUID = 1L;
////
////			@Override
////			protected IAjaxCallDecorator getAjaxCallDecorator() {
////				return new AjaxCallDecorator() {
////					private static final long serialVersionUID = 1L;
////
////					public CharSequence decorateScript(CharSequence script) {
////						return "this.disabled=true;overlay(true);" + script;
////					}
////				};
////			}
////
////			@Override
////			protected void onComponentTag(ComponentTag tag) {
////				super.onComponentTag(tag);
////				tag.getAttributes().put("value", "Close");
////				tag.getAttributes().put("type", "onSubmit");
////			}
////
////			@Override
////			protected void onSubmit(AjaxRequestTarget arg0, Form arg1) {
////				modalWindow.close(arg0);
////			}
////
////		};
////		button.setEnabled(true);
////		button.setOutputMarkupId(true);
////		button.setDefaultFormProcessing(false);
////		return button;
////	}
//	
//	private class PanelViewForm extends Form {
//		public PanelViewForm(String id) {
//			super(id);
//		}
//	}
//	
//	
//	/**
//	 * Create the form and panel
//	 * 
//	 * @param id
//	 * @return
//	 */
//	private Form<?> createPageFormField(String id) {
//		Form<?> form = new Form<Object>(id);
//		ServicingPanelsViewPanel panel = new ServicingPanelsViewPanel("mainPanel", 
//				branchDTO,model, getFeedBackPanel());
//		form.add(panel);
//		return form;
//	}
//
//	@SuppressWarnings("unchecked")
//	private List<IGridColumn> createInternalTableFieldColumns(String id) {
//		List<IGridColumn> colList = new ArrayList<IGridColumn>();
//		// If not populating table columns please check table name in ENUM.
//		for (SalesBCLinkingGUIField c : SalesBCLinkingGUIField
//				.getEnumForTable("panelListTable")) {
//			colList.add(new PropertyColumn(new Model(c.getName()), c.getId(), c
//					.getId()));
//		}
//		return colList;
//	}
//
//	private Label createBranchNameField(String name, String attributeName) {
//		SRSLabel tempSRSLabelField = new SRSLabel(name, new PropertyModel(
//				branchDTO, attributeName));
//		return tempSRSLabelField;
//	}
//	
//	
//	
//	/**
//	 * Create the export button
//	 * 
//	 * @param id
//	 * @param form
//	 * @return
//	 */
//	@SuppressWarnings("unchecked")
////	protected Button createExportButton(String id, Form form) {
////		Button but = new Button(id) {
////			private static final long serialVersionUID = 1L;
////			
////			@Override
////			protected void onComponentTag(ComponentTag tag) {
////				super.onComponentTag(tag);
////				decorateComponentStyleToHide(panelDataList == null
////						|| panelDataList.size() == 0, tag);
////			}
////			
////			@Override
////			public void onSubmit() {
////				super.onSubmit();	
////				try {
////					new GridToCSVHelper().createCSVFromDataGrid(panelDataGrid,
////							"ServicingPanelView_"+ branchDTO.getName()+ ".csv");
////				} catch (Exception e) {	
////					Logger.getLogger(this.getClass()).error(
////							"An error occured when trying to generate the excel document",e);
////					this.error("Error occurred during export:" + e.getCause());
////				}				
////			}
////		};
////		but.setOutputMarkupId(true);
////		return but;
////	}
//	
//	/**
//	 * Create the Document view panel, with blocking and download afterwards.
//	 * 
//	 * @param componentId
//	 * @return
//	 */
//	protected Panel createExportPanel(final String componentId) {
//	
//		final String fileName = new StringBuilder().append("ServicingPanelView_").append(branchDTO.getName()).append(".csv").toString(); 
//		
//		// AjaxDownload initiator after retrieve of document
//		final AjaxDownload download = new AjaxDownload() {
//
//			private static final long serialVersionUID = 1L;
//
//			@Override
//			protected IResourceStream getResourceStream() {
//				
//				
//				File file = createFileFromGrid(panelDataGrid,
//						"ServicingPanelView_"+ branchDTO.getName()+ ".csv");
//				return new FileResourceStream(new org.apache.wicket.util.file.File(file));
//			}
//
//			@Override
//			protected String getFileName() {
//				return fileName;
//			}
//
//		};
//	
//		/**
//		 *  Button to initiate the retrieve of the document 
//		 */
//		AjaxButton but = new AjaxButton(componentId) {
//		  
//			private static final long serialVersionUID = 1L;
//
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
//
//			@Override
//			protected void onSubmit(AjaxRequestTarget arg0, Form<?> arg1) {
//				// finally initiate the download
//				System.out.println("HEREEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE");
//		        download.initiate(arg0);
//				
//			}
//		};
//
//		
////		ContextImage image = new ContextImage("img", imageLocation); 
////		image.add(new SimpleAttributeModifier("title", String.valueOf((SBIDocumentType.INFOSLIP_DOCUMENT==doctype) ?  data.getInfoslipId() : data.getEcsId())));
////		image.add(new SimpleAttributeModifier("align","center"));
//		
//		but.add(new SimpleAttributeModifier("align","center"));
//		but.setOutputMarkupId(true);
//		
//		Panel panel = HelperPanel.getInstance(componentId, but);
//		panel.add(download);
//		return panel;
//		
//	}
//	
//	
//	
//	public File createFileFromGrid(SRSDataGrid grid, String fileName){
//		IDataSource source = grid.getDataSource();
//		List<IGridColumn> cols = grid.getAllColumns();
//		File file;
////		List<String> gridData = new ArrayList<String>();
//		
//		if (!(source instanceof SRSDataProviderAdapter)){
//			throw new IllegalArgumentException("Grid datasource must be an SRSDataProvider to be exported to CSV via this method");
//		}
//		
//		IDataProvider provider = ((SRSDataProviderAdapter)source).getDataProvider();
//		Iterator objs = provider.iterator(0, provider.size() - 1);
//		StringBuilder builder = new StringBuilder(2000);
//		
//		//get Headings
//		List<String> heading = constructHeadersForColumnsExportCSV(cols);
//		String gridHeading = "";
//		for(String head: heading){
//			gridHeading = gridHeading + "\"" +head + "\",";
//		}
//		gridHeading = gridHeading + "\r\n";
//		builder.append(gridHeading);
//		
//		//get grid data
//		while(objs.hasNext()){
//			Object rowObject = objs.next();
//		    builder.append(constructStringRow(rowObject, cols));
//		}
//
//		file = new File (fileName);
//		BufferedWriter writer = null;
//		try {
//		    writer = new BufferedWriter(new FileWriter(file));
//		    writer.write(builder.toString());
//		} catch (IOException e) {
//			Logger.getLogger(this.getClass()).error(
//					"An error occured when trying to generate the excel document",e);
//			this.error("Error occurred during export:" + e.getCause());
//		} finally {
//		    if (writer != null)
//				try {
//					writer.close();
//				} catch (IOException e) {
//					Logger.getLogger(this.getClass()).error(
//							"An error occured when trying to generate the excel document",e);
//					this.error("Error occurred during export:" + e.getCause());
//				}
//		}
//		
////		ResourceStreamRequestTarget target = new ResourceStreamRequestTarget(new StringResourceStream(
////				builder.toString(), "text/plain"));
////		target.setFileName(fileName);
////    	RequestCycle.get().setRequestTarget(target);
//		return file;
//	    	
//		
//	}
//	
//	/**
//	 * Create headings from the grid for the CSV export headings
//	 * @param cols
//	 * @return
//	 */
//	private List<String> constructHeadersForColumnsExportCSV(List<IGridColumn> cols){
//		List<String> columnName = new ArrayList<String>();
//		for(IGridColumn col : cols){
//			if (col instanceof SRSGridRowSelectionCheckBox) {
//				continue;
//			}
//			Component comp = col.newHeader(col.getId());
//			String header = comp.getDefaultModelObjectAsString();
//			columnName.add(header);
//		}
//		
//		return columnName;
//	}
//	
//	/**
//	 * Construct the string value for the csv export
//	 * @param rowObject
//	 * @param cols
//	 * @return
//	 */
//	private String constructStringRow(Object rowObject, List<IGridColumn> cols){
//		StringBuilder builder = new StringBuilder(150);
//		
//		IConverterLocator converterLocator = SRSApplication.get().getConverterLocator();
//		Locale locale = SRSAuthWebSession.get().getLocale();
//		
//		for(IGridColumn col : cols){
//			if (col instanceof SRSGridRowSelectionCheckBox) {
//				continue;
//			}
//			
//			Object value = PropertyResolver.getValue(col.getId(), rowObject);
//			boolean isString = (value != null && value instanceof String); 
//			String stringValue =  "";
//			if (value != null) {
////				if (col instanceof PropertyColumn) {
////					stringValue = ((PropertyColumn)col).c
////				} else {
//					stringValue = converterLocator.getConverter(value.getClass()).convertToString(value, locale);
////				}
//				if (!isString && stringValue.contains(",")) {
//					// Ensure that values that contain the delimiter are contained in quotes 
//					isString = true;
//				}
//			}
//			
//			if (isString) {
//				builder.append("\"");
//			}
//			builder.append(stringValue);
//			if (isString) {
//				builder.append("\"");
//			}
//			builder.append(",");
//		}
//		builder.deleteCharAt(builder.length()-1);
//		builder.append("\r\n");
//		return builder.toString();
//	}
//	
//	
//	
//	/**
//	 * Decorate the style tag to hide the component
//	 * 
//	 * @param isHidden Hide component if true. 
//	 * @param tag
//	 */
//	private void decorateComponentStyleToHide(boolean isHidden, ComponentTag tag) {
//		if (!isHidden) {
//			return;
//		}
//		String val = (String) tag.getAttributes().get("style");
//		val = (val ==null) ? "" : val;
//		val += " ;visibility:hidden;";
//		tag.put("style", val);
//	}
//	
//	private Form createExportForm(String id) {
//		Form form = new Form(id);
//		form.add(createExportPanel("exportButton"));
//		return form;
//	}
}