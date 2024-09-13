package za.co.liberty.web.wicket.markup.html.grid;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletResponse;

import org.apache.wicket.Component;
import org.apache.wicket.IConverterLocator;
import org.apache.wicket.core.util.lang.PropertyResolver;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.request.IRequestCycle;
import org.apache.wicket.request.IRequestHandler;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.handler.resource.ResourceStreamRequestHandler;
import org.apache.wicket.request.http.WebResponse;
import org.apache.wicket.request.resource.ContentDisposition;
import org.apache.wicket.util.convert.IConverter;
import org.apache.wicket.util.resource.StringResourceStream;
import org.apache.wicket.util.time.Duration;

import com.inmethod.grid.IDataSource;
import com.inmethod.grid.IGridColumn;

import za.co.liberty.helpers.xls.ExcelCell;
import za.co.liberty.helpers.xls.ExcelRows;
import za.co.liberty.helpers.xls.IExcelCell;
import za.co.liberty.helpers.xls.IExcelRows;
import za.co.liberty.helpers.xls.SimpleExcelProvider;
import za.co.liberty.web.system.SRSApplication;
import za.co.liberty.web.system.SRSAuthWebSession;

/**
 * Will have helper methods to allow users to send their data grid to an excel spreadsheet
 * 
 * @author dzs2610
 * @author JZB0608 - Prevent select column from exporting, add formating using converters
 * 			etc.
 *
 */
public class GridToCSVHelper {

	/**
	 * Export a given grid's data to CSV - performance better on time.   
	 * 
	 * <b>ONLY CALL THIS METHOD FROM an HTTP POST and not an AJAX event </b>
	 * 
	 * @param grid
	 * @param fileName
	 */
	public void createCSVFromDataGrid(SRSDataGrid grid, String fileName){
		IDataSource source = grid.getDataSource();
		List<IGridColumn> cols = grid.getAllColumns();
		
		if (!(source instanceof SRSDataProviderAdapter)){
			throw new IllegalArgumentException("Grid datasource must be an SRSDataProvider to be exported to CSV via this method");
		}
		
		IDataProvider provider = ((SRSDataProviderAdapter)source).getDataProvider();
		Iterator objs = provider.iterator(0, provider.size() - 1);
		StringBuilder builder = new StringBuilder(2000);
		
		//get Headings
		List<String> heading = constructHeadersForColumnsExportCSV(cols);
		String gridHeading = "";
		for(String head: heading){
			gridHeading = gridHeading + "\"" +head + "\",";
		}
		gridHeading = gridHeading + "\r\n";
		builder.append(gridHeading);
		
		//get grid data
		while(objs.hasNext()){
			Object rowObject = objs.next();
		    builder.append(constructStringRow(rowObject, cols));
		}
		
		IRequestHandler d;
		ResourceStreamRequestHandler handler = new ResourceStreamRequestHandler(new StringResourceStream(
				builder.toString(), "text/plain")) {
			
			@Override
	        public void respond(IRequestCycle requestCycle) {
	            super.respond(requestCycle);
	        }
			
		};
	
		handler.setContentDisposition(ContentDisposition.ATTACHMENT);
		handler.setCacheDuration(Duration.ONE_MINUTE);
		handler.setFileName(fileName);
				
		RequestCycle.get().scheduleRequestHandlerAfterCurrent(handler);
	    	
	}
	
	
	/**
	 * Create headings from the grid for the CSV export headings
	 * @param cols
	 * @return
	 */
	private List<String> constructHeadersForColumnsExportCSV(List<IGridColumn> cols){
		List<String> columnName = new ArrayList<String>();
		for(IGridColumn col : cols){
			if (col instanceof SRSGridRowSelectionCheckBox || col instanceof SRSCheckBoxColumn) {
				continue;
			}
			Component comp = col.newHeader(col.getId());
			String header = comp.getDefaultModelObjectAsString();
			columnName.add(header);
		}
		
		return columnName;
	}
	
	/**
	 * Construct the string value for the csv export
	 * @param rowObject
	 * @param cols
	 * @return
	 */
	private String constructStringRow(Object rowObject, List<IGridColumn> cols){
		StringBuilder builder = new StringBuilder(150);
		
		IConverterLocator converterLocator = SRSApplication.get().getConverterLocator();
		Locale locale = SRSAuthWebSession.get().getLocale();
		
		for(IGridColumn col : cols){
			if (col instanceof SRSGridRowSelectionCheckBox || col instanceof SRSCheckBoxColumn) {
				continue;
			}
			
			Object value = PropertyResolver.getValue(col.getId(), rowObject);
			boolean isString = (value != null && value instanceof String); 
			String stringValue =  "";
			if (value != null) {
//				if (col instanceof PropertyColumn) {
//					stringValue = ((PropertyColumn)col).c
//				} else {						
					IConverter converter = converterLocator.getConverter(value.getClass())	;
					stringValue = converter.convertToString(value, locale);
//				}
				if (!isString && stringValue.contains(",")) {
					// Ensure that values that contain the delimiter are contained in quotes 
					isString = true;
				}
			}
			
			if (isString) {
				builder.append("\"");
			}
			builder.append(stringValue);
			if (isString) {
				builder.append("\"");
			}
			builder.append(",");
		}
		builder.deleteCharAt(builder.length()-1);
		builder.append("\r\n");
		return builder.toString();
	}
	
	/**
	 * Create an excel speardsheet from the given grid
	 * @param grid
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public IExcelRows createExcelFromDataGrid(SRSDataGrid grid){
		ExcelRows rows = new ExcelRows();
		IDataSource source = grid.getDataSource();
		List<IGridColumn> cols = grid.getAllColumns();		
		if(source instanceof SRSDataProviderAdapter){
			//first we contruct the header row
			rows.addExcelRow(constructHeadersFromColumns(cols));			
			IDataProvider provider = ((SRSDataProviderAdapter)source).getDataProvider();
			Iterator objs = provider.iterator(0, provider.size() - 1);
			while(objs.hasNext()){
				Object rowObject = objs.next();
				rows.addExcelRow(constructDataRow(rowObject,cols));
			}			
		}else{
			throw new IllegalArgumentException("Grid datasource must be an SRSDataProvider to be exported to Excel via this method");
		}
		return rows;
	}
	/**
	 * Will return the excel spreadsheet to the user using the given WebResponse and DataGrid from Wicket
	 * @param rows
	 * @param fileName
	 * @param webResponse
	 * @throws IOException
	 */
	public void sendSpreadSheetToUser(SRSDataGrid grid, String fileName, WebResponse webResponse) throws IOException{
		sendSpreadSheetToUser(createExcelFromDataGrid(grid),fileName,webResponse);
	}	
	
	/**
	 * Will return the excel spreadsheet to the user using the given WebResponse from Wicket
	 * @param rows
	 * @param fileName
	 * @param webResponse
	 * @throws IOException
	 */
	public void sendSpreadSheetToUser(IExcelRows rows, String fileName, WebResponse webResponse) throws IOException{
		if(webResponse == null || rows == null || fileName == null){
			return;
		}
		SimpleExcelProvider excelProvider = new SimpleExcelProvider(rows);

		// #WICKETTEST #WICKETFIX  - Test this, changed the method.
		HttpServletResponse servletResponse = (HttpServletResponse) webResponse.getContainerResponse();
		servletResponse.setContentType("application/vnd.ms-excel");
		servletResponse.setHeader("Content-Disposition","attachment;filename=" + fileName);
		OutputStream outputStream = servletResponse.getOutputStream();
		excelProvider.getExcelOpject(outputStream);
		outputStream.close();				
	}
	
	/**
	 * Will construct the header row in Excle for the columns used in the grid
	 * @param cols
	 * @return
	 */
	private List<IExcelCell> constructHeadersFromColumns(List<IGridColumn> cols){
		List<IExcelCell> cells = new ArrayList<IExcelCell>(cols.size());
		for(IGridColumn col : cols){
			ExcelCell headerCol = new ExcelCell();
			headerCol.setBold(true);
			headerCol.setUnderLined(true);			
			Component comp = col.newHeader(col.getId());
			String header = comp.getDefaultModelObjectAsString();
			headerCol.setValue(header);
			cells.add(headerCol);
		}
		return cells;
	}
	
	/**
	 * Construct the data row
	 * @param rowObject
	 * @param cols
	 * @return
	 */
	private List<IExcelCell> constructDataRow(Object rowObject, List<IGridColumn> cols){
		List<IExcelCell> cells = new ArrayList<IExcelCell>(cols.size());
		for(IGridColumn col : cols){
			ExcelCell regularCol = new ExcelCell();	
			Object value = PropertyResolver.getValue(col.getId(), rowObject);
			String stringValue = (value != null) ? value.toString() : "";					
			regularCol.setValue(stringValue);			
			cells.add(regularCol);			
		}
		return cells;
	}
}
