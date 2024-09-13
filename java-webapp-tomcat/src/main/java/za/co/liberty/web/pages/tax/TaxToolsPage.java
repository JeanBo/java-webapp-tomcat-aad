package za.co.liberty.web.pages.tax;

import javax.naming.NamingException;

import org.apache.wicket.markup.html.panel.Panel;

import za.co.liberty.business.broadcast.IScheduledCommunicationTimerService;
import za.co.liberty.business.guicontrollers.taxgui.ITaxGuiController;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.helpers.lookup.ServiceLocator;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.pages.BasePage;
import za.co.liberty.web.pages.tax.model.TaxToolsModel;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGrid;
import za.co.liberty.xml.tax.Parameter;

public class TaxToolsPage extends BasePage {
	
	transient protected ITaxGuiController guiController;
	private Parameter execNode;
	
	//XML model will be contained on the pageModel
	protected TaxToolsModel pageModel; 
	private Panel taxToolsPanel;
	private BasePage parentPage;
	
	private String sep = "";
	
	private transient IScheduledCommunicationTimerService timerService;	
	
	public TaxToolsPage(){
		super();
		//instantiate model
		pageModel = new TaxToolsModel();
		sep = getTaxGuiController().getSeperator();
		getTaxGuiController().updateTaxQueueInformation(pageModel);
		//populate all LDAP paths - to override XML log values and retrievals
		pageModel.setRetrieveAllTaxLDAPPathsMap(getTaxGuiController().retrieveAllTaxLDAPPathsMap());

		pageModel.setRetrieveLogList(getTaxGuiController().retrieveTaxLog());
		
		init();
	}
	
	public TaxToolsPage(String responseMsg){
		this();
		info(responseMsg);
	}
	
	SRSDataGrid taxLogGrid = null;
	
	private void init(){
		add(taxToolsPanel = createTaxToolsPanel("taxToolsPanel"));

	}
	
	private Panel createTaxToolsPanel(String id) {
		Panel p = new TaxToolsPanel(id, EditStateType.VIEW, this, pageModel);
		return p;
	}
	
//	private SRSDataGrid createTaxLogPanel(String name){
//		SRSDataGrid tempDataGrid 
//		= new SRSDataGrid(name,new SRSDataProviderAdapter(
//				new SortableListDataProvider<TaxLogDTO>(pageModel.getRetrieveLogList())),createSearchResultColumns(),getEditState());
//		tempDataGrid.setAutoResize(true);
//		tempDataGrid.setOutputMarkupId(true);
//		tempDataGrid.setCleanSelectionOnPageChange(false);
//		tempDataGrid.setClickRowToSelect(false);
//		tempDataGrid.setAllowSelectMultiple(true);
//		tempDataGrid.setGridWidth(99, GridSizeUnit.PERCENTAGE);
//		tempDataGrid.setRowsPerPage(10);
//		tempDataGrid.setContentHeight(189, SizeUnit.PX);
//		return tempDataGrid;
//	}
//	
//	protected List<IGridColumn> createSearchResultColumns() { 
//		List<IGridColumn> columns = new ArrayList<IGridColumn>();
//		
//		columns.add(new SRSDataGridColumn<AccountEntryDTO>("id",
//				new Model("id"), "id" ,"id", getEditState())
//				.setInitialSize(70));
//		columns.add(new SRSDataGridColumn<TaxLogDTO>("scheduled",
//				new Model("scheduled"), "scheduled" ,"scheduled", getEditState())
//				.setInitialSize(70));
//		columns.add(new SRSDataGridColumn<TaxLogDTO>("status",
//				new Model("status"), "status" ,"status", getEditState())
//				.setInitialSize(40));
//		columns.add(new SRSDataGridColumn<TaxLogDTO>("process",
//				new Model("process"), "process" ,"process", getEditState())
//				.setInitialSize(200));
//		columns.add(new SRSDataGridColumn<AccountEntryDTO>("generatestartdate",
//				new Model("generatestartdate"), "generatestartdate" ,"generatestartdate", getEditState())
//				.setInitialSize(160));
//		columns.add(new SRSDataGridColumn<AccountEntryDTO>("generateenddate",
//				new Model("generateenddate"), "generateenddate" ,"generateenddate", getEditState())
//				.setInitialSize(160));
//		columns.add(new SRSDataGridColumn<AccountEntryDTO>("uacfid",
//				new Model("uacfid"), "uacfid" ,"uacfid", getEditState())
//				.setInitialSize(80));
//		columns.add(new SRSDataGridColumn<TaxLogDTO>("fileName",
//				new Model("fileName"), "fileName" ,"fileName", getEditState())
//				.setInitialSize(120));
//		columns.add(new SRSDataGridColumn<TaxLogDTO>("message",
//				new Model("message"), "message" ,"message", getEditState())
//				.setInitialSize(300));
//		
//		return columns;
//	}
		
	
	@Override
	public String getPageName() {
		return "TaxToolsPage";
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
