package za.co.liberty.web.pages.tax;

import java.util.ArrayList;
import java.util.List;

import javax.naming.NamingException;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import com.inmethod.grid.IGridColumn;
import com.inmethod.grid.SizeUnit;

import za.co.liberty.business.broadcast.IScheduledCommunicationTimerService;
import za.co.liberty.business.guicontrollers.taxgui.ITaxGuiController;
import za.co.liberty.dto.account.AccountEntryDTO;
import za.co.liberty.dto.taxxml.TaxLogDTO;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.helpers.lookup.ServiceLocator;
import za.co.liberty.web.pages.BasePage;
import za.co.liberty.web.pages.tax.model.TaxGuiModel;
import za.co.liberty.web.wicket.markup.html.form.SRSDropDownChoice;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGrid;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGrid.GridSizeUnit;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGridColumn;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataProviderAdapter;
import za.co.liberty.web.wicket.markup.repeater.data.SortableListDataProvider;
import za.co.liberty.xml.tax.Parameter;

public class TaxGuiPage extends BasePage {
	
	transient protected ITaxGuiController guiController;
	private Parameter execNode;
	
	//XML model will be contained on the pageModel
	protected TaxGuiModel pageModel; 
	private Panel selectPanel;
	private BasePage parentPage;
	
	private SRSDropDownChoice xmlSelector;
	private String sep = "";
	
	
	private transient IScheduledCommunicationTimerService timerService;	
	
	public TaxGuiPage(){
		super();
		//instantiate model
		pageModel = new TaxGuiModel();
		sep = getTaxGuiController().getSeperator();
		//populate all LDAP paths - to override XML log values and retrievals
		pageModel.setRetrieveAllTaxLDAPPathsMap(getTaxGuiController().retrieveAllTaxLDAPPathsMap());
		//retrieve all xml files from folder
		List<String> xmlFiles = getTaxGuiController().retrieveXMLFileNames();
		pageModel.setXmlFilesAllLocal(xmlFiles);
		pageModel.setSelectedXMLContext(getTaxGuiController().readXML2(pageModel.getXmlFileSelectedLocal()));
		//retrieve all combo all values - like agreement kinds and action indicator - TODO:phase2
		pageModel.setRetrieveAllAgmKinds(getTaxGuiController().retrieveAllAgreementKinds());
		pageModel.setRetrieveLogList(getTaxGuiController().retrieveTaxLog());
		
		//@TODO enable scheduling
		pageModel.setAllowScheduleJob(false);
		pageModel.setScheduleJob(false);
		
		init();
	}
	
	public TaxGuiPage(String responseMsg){
		this();
		info(responseMsg);
	}
	
	SRSDataGrid taxLogGrid = null;
	
	private void init(){
		add(selectPanel = emptyP("selectPanel"));
		add(xmlSelector = createXmlSelector("xmlSelector"));
		add(taxLogGrid = createTaxLogPanel("taxLogPanel"));
		
	}
	
	private SRSDataGrid createTaxLogPanel(String name){
		SRSDataGrid tempDataGrid 
		= new SRSDataGrid(name,new SRSDataProviderAdapter(
				new SortableListDataProvider<TaxLogDTO>(pageModel.getRetrieveLogList())),createSearchResultColumns(),getEditState());
		tempDataGrid.setAutoResize(true);
		tempDataGrid.setOutputMarkupId(true);
		tempDataGrid.setCleanSelectionOnPageChange(false);
		tempDataGrid.setClickRowToSelect(false);
		tempDataGrid.setAllowSelectMultiple(true);
		tempDataGrid.setGridWidth(99, GridSizeUnit.PERCENTAGE);
		tempDataGrid.setRowsPerPage(11);
		tempDataGrid.setContentHeight(189, SizeUnit.PX);
		return tempDataGrid;
	}
	
	protected List<IGridColumn> createSearchResultColumns() { 
		List<IGridColumn> columns = new ArrayList<IGridColumn>();
		
		columns.add(new SRSDataGridColumn<AccountEntryDTO>("id",
				new Model("id"), "id" ,"id", getEditState())
				.setInitialSize(70));
		columns.add(new SRSDataGridColumn<TaxLogDTO>("scheduled",
				new Model("scheduled"), "scheduled" ,"scheduled", getEditState())
				.setInitialSize(70));
		columns.add(new SRSDataGridColumn<TaxLogDTO>("status",
				new Model("status"), "status" ,"status", getEditState())
				.setInitialSize(40));
		columns.add(new SRSDataGridColumn<TaxLogDTO>("process",
				new Model("process"), "process" ,"process", getEditState())
				.setInitialSize(150));
		columns.add(new SRSDataGridColumn<TaxLogDTO>("agreementKind",
				new Model("kind"), "agreementKind" ,"agreementKind", getEditState())
				.setInitialSize(50));
		columns.add(new SRSDataGridColumn<TaxLogDTO>("currentCount",
				new Model("current"), "currentCount" ,"currentCount", getEditState())
				.setInitialSize(50));
		columns.add(new SRSDataGridColumn<TaxLogDTO>("sentCount",
				new Model("sent"), "sentCount" ,"sentCount", getEditState())
				.setInitialSize(50));
		columns.add(new SRSDataGridColumn<TaxLogDTO>("totalCount",
				new Model("total"), "totalCount" ,"totalCount", getEditState())
				.setInitialSize(50));
		columns.add(new SRSDataGridColumn<AccountEntryDTO>("generatestartdate",
				new Model("generatestartdate"), "generatestartdate" ,"generatestartdate", getEditState())
				.setInitialSize(160));
		columns.add(new SRSDataGridColumn<AccountEntryDTO>("generateenddate",
				new Model("generateenddate"), "generateenddate" ,"generateenddate", getEditState())
				.setInitialSize(160));
		columns.add(new SRSDataGridColumn<AccountEntryDTO>("uacfid",
				new Model("uacfid"), "uacfid" ,"uacfid", getEditState())
				.setInitialSize(80));
		columns.add(new SRSDataGridColumn<TaxLogDTO>("fileName",
				new Model("fileName"), "fileName" ,"fileName", getEditState())
				.setInitialSize(120));
		columns.add(new SRSDataGridColumn<TaxLogDTO>("message",
				new Model("message"), "message" ,"message", getEditState())
				.setInitialSize(300));
		
		return columns;
	}
	
	private SRSDropDownChoice<String> createXmlSelector(String id){
		SRSDropDownChoice tempSRSDropDown = new SRSDropDownChoice(id , 
				new PropertyModel(pageModel ,"xmlFileSelectedLocal"),
				pageModel.getXmlFilesAllLocal(), new ChoiceRenderer() {
							public Object getDisplayValue(Object arg0){
								if (arg0==null) {
									   return null;
								   }
								return ((String)arg0).toString();
							}
							public Object getIdValue(Object arg0){
								if (arg0==null) {
									   return null;
								   }
								return ((String)arg0);
							}
						
				},"select one");
				tempSRSDropDown.setOutputMarkupId(true);
				
				tempSRSDropDown.add(new AjaxFormComponentUpdatingBehavior("change"){

					@Override
					protected void onUpdate(AjaxRequestTarget arg0) {
						//read the selected xml. But which xml was selected?

						pageModel.setXmlContext(getTaxGuiController().readXML2(pageModel.getXmlFileSelectedLocal()));
						pageModel.setSelectedXMLTemplateFullPath(getTaxGuiController().getCurrentFullSelectedTemplatePath());
						
						System.out.println("Full path pageModel of selected:" + pageModel.getSelectedXMLTemplateFullPath());
						System.out.println("Name of XML:" + pageModel.getXmlFileSelectedLocal());
						//test
						
						
						if(pageModel.getXmlContext().getName().indexOf("TCS") >= 0){
							//load the tcs panel. We don't have to pass in the path, as the session variable in gui controller contains the master path which
							//is set everytime a new xml is parsed on the read xml. We will add a seperator at the end, as we know that a new directory will be created
							//with that, we pass in the xml model from the page model. 
							Panel tcs = new TCSPanel("selectPanel",getEditState(),pageModel.getXmlContext(),parentPage, pageModel.getRetrieveAllTaxLDAPPathsMap().get("tcs"), sep,pageModel);
							tcs.setOutputMarkupId(true);
							selectPanel.replaceWith(tcs);
							selectPanel = tcs;
							if(arg0 != null){
								arg0.add(selectPanel);
							}
						
						}else if (pageModel.getXmlContext().getName().indexOf("SDL") >= 0){
							
							Panel sdlPanel = new SDLReportPanel("selectPanel",getEditState(),pageModel.getXmlContext(),parentPage,pageModel.getRetrieveAllTaxLDAPPathsMap().get("sdl"),sep);
							sdlPanel.setOutputMarkupId(true);
							selectPanel.replaceWith(sdlPanel);
							selectPanel = sdlPanel;
							if(arg0 != null){
								arg0.add(selectPanel);
							}
						
						}else if (pageModel.getXmlContext().getName().indexOf("PAYE") >= 0){
							System.out.println("Load PAYE Panel");
							Panel payePanel = new PAYEReportPanel("selectPanel",getEditState(),pageModel.getXmlContext(),parentPage, pageModel.getRetrieveAllTaxLDAPPathsMap().get("paye"),sep);
							payePanel.setOutputMarkupId(true);
							selectPanel.replaceWith(payePanel);
							selectPanel = payePanel;
							if(arg0 != null){
								arg0.add(selectPanel);
							}
						}
					}	
					});
				
				return (SRSDropDownChoice) tempSRSDropDown;
	
	}
	
	public Parameter mapXMLLookup(String name){
		System.out.println(name);
		for(Parameter instNode: pageModel.getSelectedXMLContext().getParameters().getParameter()){
			if(instNode.getName().equalsIgnoreCase(name)){
				execNode = instNode;
			}
		}
		return execNode;
	}	
	protected Panel emptyP(String id){
		selectPanel= new EmptyPanel("selectPanel");
		selectPanel.setOutputMarkupId(true);
		return selectPanel;
		
	}
	private Label statusLabel;
	private Label createStatusLabel(String id){
		Label temp = new Label(id);
		temp.setOutputMarkupId(true);
		return temp;
	}
	
	private boolean serviceActive;	
	
	@Override
	public String getPageName() {
		return "TaxGuiPage";
	}
	
	//get the gui controller instance
	protected ITaxGuiController getTaxGuiController() {
		if (guiController == null) {
			try {
				guiController = ServiceLocator.lookupService(ITaxGuiController.class);
			} catch (NamingException namingErr) {
				logger.error(this.getPageName()
						+ " Lookup of ITaxGuiController failed: "
						+ namingErr.getMessage());
				CommunicationException comm = new CommunicationException("Lookup of ITaxGuiController failed!");
				throw new CommunicationException(comm);
			} 
		}
		return guiController;
	}
	
}
