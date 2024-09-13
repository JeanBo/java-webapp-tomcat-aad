package za.co.liberty.web.pages.maintainagreement;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.Model;

import za.co.liberty.dto.agreement.properties.DistributionTemplateHistoryDTO;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.pages.BaseWindowPage;
import za.co.liberty.web.pages.maintainagreement.model.DistributionDetailPageModel;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGrid;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGridColumn;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGrid.GridSizeUnit;
import za.co.liberty.web.wicket.markup.repeater.data.ListDataProvider;

import com.inmethod.grid.DataProviderAdapter;
import com.inmethod.grid.IGridColumn;
import com.inmethod.grid.SizeUnit;

public class TemplateHistoryPage extends BaseWindowPage {
		
	private ModalWindow window;
	private SRSDataGrid templateHistoryDetailsTable;
	private transient Logger logger = Logger.getLogger(DistributionDetailsPage.class);
	private List<IGridColumn> templateHistoryDetailsColumns;
	private DistributionDetailPageModel pageModel;
	
		
	public TemplateHistoryPage(ModalWindow window, 
			DistributionDetailPageModel pageModel) {
		super();
		this.window = window;
		this.pageModel=pageModel;
		add(new TemplateHistoryForm("templateHistoryForm"));
	}
	
	private class TemplateHistoryForm extends Form {
		public TemplateHistoryForm(String id) {
			super(id);
			/**
			 * Components
			 */
			add(getTemplateHistoryDetailsTable());
		}
	}
	
	
	public SRSDataGrid getTemplateHistoryDetailsTable() {
		if (templateHistoryDetailsTable == null) {
			templateHistoryDetailsTable = new SRSDataGrid("templateHistoryDetailsTable",
					new DataProviderAdapter(new ListDataProvider<DistributionTemplateHistoryDTO>(
							pageModel.getDistribDetailsHistoryList())),
					getTemplateHistoryDetailsColumns(),EditStateType.VIEW);
			templateHistoryDetailsTable.setOutputMarkupId(true);
			templateHistoryDetailsTable.setCleanSelectionOnPageChange(false);
			templateHistoryDetailsTable.setAllowSelectMultiple(false);
			templateHistoryDetailsTable.setGridWidth(99, GridSizeUnit.PERCENTAGE);
			templateHistoryDetailsTable.setRowsPerPage(1000);
			templateHistoryDetailsTable.setContentHeight(300, SizeUnit.PX);
		}
		return templateHistoryDetailsTable;
	}

	
	private List<IGridColumn> getTemplateHistoryDetailsColumns() {
		if (templateHistoryDetailsColumns == null) {
			templateHistoryDetailsColumns = new ArrayList<IGridColumn>();
			
			//Template ID, Template name, Effective From, Effective To
			SRSDataGridColumn templateIDCol = new SRSDataGridColumn<DistributionTemplateHistoryDTO>("templateId",
					new Model("Template Id"),"templateId",EditStateType.VIEW);
			templateIDCol.setSizeUnit(SizeUnit.PX);
			templateIDCol.setMinSize(75);
			templateIDCol.setInitialSize(75);
			templateHistoryDetailsColumns.add(templateIDCol);
			SRSDataGridColumn templateNameCol = new SRSDataGridColumn<DistributionTemplateHistoryDTO>("templateDesc",
					new Model("Template Name"),"templateDesc",EditStateType.VIEW);
			templateNameCol.setSizeUnit(SizeUnit.PX);
			templateNameCol.setMinSize(150);
			templateNameCol.setInitialSize(150);
			templateHistoryDetailsColumns.add(templateNameCol);
			SRSDataGridColumn effectiveFromCol = new SRSDataGridColumn<DistributionTemplateHistoryDTO>("effectiveFrom",
					new Model("Effective From"),"effectiveFrom",EditStateType.VIEW);
			effectiveFromCol.setSizeUnit(SizeUnit.PX);
			effectiveFromCol.setMinSize(150);
			effectiveFromCol.setInitialSize(150);
			templateHistoryDetailsColumns.add(effectiveFromCol);
			SRSDataGridColumn effectiveToCol = new SRSDataGridColumn<DistributionTemplateHistoryDTO>("effectiveTo",
					new Model("Effective To"),"effectiveTo",EditStateType.VIEW);
			effectiveToCol.setSizeUnit(SizeUnit.PX);
			effectiveToCol.setMinSize(150);
			effectiveToCol.setInitialSize(150);
			templateHistoryDetailsColumns.add(effectiveToCol);
		}
		return templateHistoryDetailsColumns;
	}

	@Override
	public String getPageName() {
		return "Distribution Details Template History";
	}
	
}	
