package za.co.liberty.web.pages.maintainagreement;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.naming.NamingException;

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

import za.co.liberty.business.guicontrollers.agreement.IAgreementGUIController;
import za.co.liberty.dto.agreement.properties.DistributionDetailDTO;
import za.co.liberty.helpers.lookup.ServiceLocator;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.pages.BaseWindowPage;
import za.co.liberty.web.pages.maintainagreement.model.DistributionDetailPageModel;
import za.co.liberty.web.pages.panels.HelperPanel;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGrid;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGridColumn;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGrid.GridSizeUnit;
import za.co.liberty.web.wicket.markup.repeater.data.ListDataProvider;

import com.inmethod.grid.DataProviderAdapter;
import com.inmethod.grid.IGridColumn;
import com.inmethod.grid.SizeUnit;

/**
 * View/Maintain the distribution details for an agreement
 * 
 *
 */
public class DistributionDetailsPage extends BaseWindowPage {
		
	@SuppressWarnings("unused")
	private ModalWindow window;
	private SRSDataGrid distributionDetailsTable;
	private transient IAgreementGUIController guiController;
	private transient Logger logger = Logger.getLogger(DistributionDetailsPage.class);
	private List<IGridColumn> distributionDetailsColumns;
	private DistributionDetailPageModel pageModel;
	
	private IConverter factorConverter = new IConverter() {

		private static final long serialVersionUID = 1L;

		public Object convertToObject(String display, Locale locale) {
			try {
				return Float.parseFloat(display);
			} catch (NumberFormatException e) {
				return null;
			}
		}

		public String convertToString(Object object, Locale locale) {
			if (object==null || !(object instanceof Float)) {
				return "";
			}
			return new DecimalFormat("0.0000000").format((Float)object);
		}
		
	};

	public DistributionDetailsPage(ModalWindow window, 
			DistributionDetailPageModel pageModel) {
		super();
		this.window = window;
		this.pageModel=pageModel;
		add(new DistributionDetailsForm("distributionDetailsForm"));
	}

	private class DistributionDetailsForm extends Form<Object> {

		private static final long serialVersionUID = 1L;

		public DistributionDetailsForm(String id) {
			super(id);
			/**
			 * Components
			 */
			add(getDistributionDetailsTable());
		}
	}

	private SRSDataGrid getDistributionDetailsTable() {
		if (distributionDetailsTable == null) {
			distributionDetailsTable = new SRSDataGrid("distributionDetailsTable",
					new DataProviderAdapter(new ListDataProvider<DistributionDetailDTO>(pageModel.getDistributionDetails())),
					getDistributionDetailsColumns(),EditStateType.VIEW);
			distributionDetailsTable.setOutputMarkupId(true);
			distributionDetailsTable.setCleanSelectionOnPageChange(false);
			distributionDetailsTable.setAllowSelectMultiple(false);
			distributionDetailsTable.setGridWidth(98, GridSizeUnit.PERCENTAGE);
			distributionDetailsTable.setRowsPerPage(1000);
			distributionDetailsTable.setContentHeight(300, SizeUnit.PX);
		}
		return distributionDetailsTable;
	}
	
	@SuppressWarnings("unchecked")
	private List<IGridColumn> getDistributionDetailsColumns() {
		if (distributionDetailsColumns == null) {
			distributionDetailsColumns = new ArrayList<IGridColumn>();
			SRSDataGridColumn distributionKindColumn = new SRSDataGridColumn<DistributionDetailDTO>("distributionKind",
					new Model("Distribution Kind"),"distributionKind",EditStateType.VIEW);
			distributionKindColumn.setSizeUnit(SizeUnit.PX);
			distributionKindColumn.setMinSize(300);
			distributionKindColumn.setInitialSize(300);
			distributionDetailsColumns.add(distributionKindColumn);
			SRSDataGridColumn distributionFactorColumn = new SRSDataGridColumn<DistributionDetailDTO>("factor",
					new Model("Factor"),"factor",EditStateType.VIEW) {

				private static final long serialVersionUID = 1L;

				@Override
				public Panel newCellPanel(WebMarkupContainer parent, String componentId, IModel rowModel, 
						String objectProperty, EditStateType state, DistributionDetailDTO data) {
					/**
					 * Agreement number column will have a custom label with a renderer to render
					 * no selection as "None Selected" 
					 */
					Label lbl = new Label("value",new PropertyModel(data,objectProperty)) {
						
						private static final long serialVersionUID = 1L;

						@Override
						public IConverter getConverter(Class targetClass) {
							return factorConverter;
						}
					};
					return HelperPanel.getInstance(componentId, lbl);
				}
			};
			distributionFactorColumn.setSizeUnit(SizeUnit.PX);
			distributionFactorColumn.setMinSize(75);
			distributionFactorColumn.setInitialSize(75);
			distributionDetailsColumns.add(distributionFactorColumn);
			SRSDataGridColumn distributionPaymentScheduleColumn = new SRSDataGridColumn<DistributionDetailDTO>("paymentSchedule",
					new Model("Payment Schedule"),"paymentSchedule",EditStateType.VIEW);
			distributionPaymentScheduleColumn.setSizeUnit(SizeUnit.PX);
			distributionPaymentScheduleColumn.setMinSize(300);
			distributionPaymentScheduleColumn.setInitialSize(300);
			distributionDetailsColumns.add(distributionPaymentScheduleColumn);
		}
		return distributionDetailsColumns;
	}

	@Override
	public String getPageName() {
		return "Distribution Details";
	}
	
	/**
	 * Load the AgreementGUIController dynamically if it is null as this is a transient variable.
	 * @return {@link IAgreementGUIController}
	 */
	@SuppressWarnings("unused")
	private IAgreementGUIController getGuiController() {
		if (guiController==null) {
			try {
				guiController = ServiceLocator.lookupService(IAgreementGUIController.class);
			} catch (NamingException e) {
				logger.fatal("Could not lookup AgreementGUIController",e);
			}
		}
		return guiController;
	}
	
	

}
