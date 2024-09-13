package za.co.liberty.web.pages.maintainagreement;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.convert.IConverter;

import za.co.liberty.dto.party.aqcdetail.AQCValueDTO;
import za.co.liberty.dto.persistence.party.flow.PartyAQCHistoryFLO;
import za.co.liberty.web.constants.SRSAppWebConstants;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.pages.BaseWindowPage;
import za.co.liberty.web.pages.panels.GUIFieldPanel;
import za.co.liberty.web.pages.panels.HelperPanel;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGrid;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGridColumn;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGrid.GridSizeUnit;
import za.co.liberty.web.wicket.markup.repeater.data.ListDataProvider;

import com.inmethod.grid.DataProviderAdapter;
import com.inmethod.grid.IGridColumn;
import com.inmethod.grid.SizeUnit;

public class AQCHistoryPage extends BaseWindowPage {
		
	private ModalWindow window;
	private SRSDataGrid aqcHistoryGrid;
	private transient Logger logger = Logger.getLogger(AQCHistoryPage.class);
	private List<IGridColumn> aqcHistoryGridColumns;
	private List<PartyAQCHistoryFLO> partyAQCHistory;
	private String title = SRSAppWebConstants.EMPTY_STRING;
	private HelperPanel titlePanel;
	
		
	public AQCHistoryPage(ModalWindow window, 
			List<PartyAQCHistoryFLO> partyAQCHistory,String title) {
		super();
		this.window = window;
		this.partyAQCHistory=partyAQCHistory;
		this.title = title;
		add(new AQCHistoryForm("aqcHistoryForm"));
	}
	
	private class AQCHistoryForm extends Form {
		public AQCHistoryForm(String id) {
			super(id);
			/**
			 * Components
			 */
			add(getTitlePanel());
			add(getAqcHistoryGrid());
		}
	}	

	@Override
	public String getPageName() {
		return this.window.getTitle().getObject().toString();
	}
	
	public HelperPanel getTitlePanel(){
		if(titlePanel == null){
			Label lbl = new Label("value",title+" AQC HISTORY");
			titlePanel = HelperPanel.getInstance("title", lbl);			
		}
		return titlePanel;
		
	}


	public SRSDataGrid getAqcHistoryGrid() {
		if (aqcHistoryGrid == null) {
			aqcHistoryGrid = new SRSDataGrid("aqcHistoryGrid",
					new DataProviderAdapter(new ListDataProvider<PartyAQCHistoryFLO>(this.partyAQCHistory)),
					getAqcHistoryGridColumns(),EditStateType.VIEW);
			aqcHistoryGrid.setOutputMarkupId(true);
			aqcHistoryGrid.setCleanSelectionOnPageChange(false);
			aqcHistoryGrid.setAllowSelectMultiple(false);
			aqcHistoryGrid.setGridWidth(99, GridSizeUnit.PERCENTAGE);
			aqcHistoryGrid.setRowsPerPage(100);
			aqcHistoryGrid.setContentHeight(300, SizeUnit.PX);
		}
		return aqcHistoryGrid;
	}


	public List<IGridColumn> getAqcHistoryGridColumns() {
		if (aqcHistoryGridColumns == null) {
			aqcHistoryGridColumns = new ArrayList<IGridColumn>();
			
			//AQC Value, Start Date, End Date, Time Replaced
			SRSDataGridColumn aqcCol = new SRSDataGridColumn<PartyAQCHistoryFLO>("aqc",
					new Model("AQC"),"aqc",EditStateType.VIEW);
			aqcCol.setSizeUnit(SizeUnit.PX);
			aqcCol.setMinSize(50);
			aqcCol.setInitialSize(50);
			aqcHistoryGridColumns.add(aqcCol);
			SRSDataGridColumn startDtCol = new SRSDataGridColumn<PartyAQCHistoryFLO>("startDate",
					new Model("Start Date"),"startDate",EditStateType.VIEW);
			startDtCol.setSizeUnit(SizeUnit.PX);
			startDtCol.setMinSize(150);
			startDtCol.setInitialSize(150);
			aqcHistoryGridColumns.add(startDtCol);
			SRSDataGridColumn endDtCol = new SRSDataGridColumn<PartyAQCHistoryFLO>("endDate",
					new Model("End Date"),"endDate",EditStateType.VIEW);
			endDtCol.setSizeUnit(SizeUnit.PX);
			endDtCol.setMinSize(150);
			endDtCol.setInitialSize(150);
			aqcHistoryGridColumns.add(endDtCol);
			SRSDataGridColumn timeReplCol = new SRSDataGridColumn<PartyAQCHistoryFLO>("timeReplaced",
					new Model("Changed on"),"timeReplaced",EditStateType.VIEW){
				
				private static final long serialVersionUID = 1L;

				@Override
				public Panel newCellPanel(WebMarkupContainer parent, String componentId, IModel rowModel, String objectProperty, EditStateType state, final PartyAQCHistoryFLO data) {
					Label label = new Label("value",new PropertyModel(data,objectProperty)){
						
						private static final long serialVersionUID = 1L;

						@Override
						public IConverter getConverter(Class type) {
							return timestampConvertor;
						}
					};				
					return HelperPanel.getInstance(componentId, label);
				}
			};
			timeReplCol.setSizeUnit(SizeUnit.PX);
			timeReplCol.setMinSize(200);
			timeReplCol.setInitialSize(200);
			aqcHistoryGridColumns.add(timeReplCol);
		}
		return aqcHistoryGridColumns;
	}	
	
	/**
	 * This anonymous inner class represents the converter to display
	 * the manual AQC value
	 */
	private IConverter timestampConvertor = new IConverter() {
		
		//Not USED
		public Object convertToObject(String value, Locale locale) {
				return null;
		}
		

		public String convertToString(Object value, Locale locale) {
			if (value!=null && value instanceof java.sql.Timestamp) {
				
				return (( java.sql.Timestamp)value).toString();
			}
			return "";
		}	
		
	};
}	