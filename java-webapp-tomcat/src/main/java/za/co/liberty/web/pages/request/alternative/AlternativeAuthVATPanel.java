package za.co.liberty.web.pages.request.alternative;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.apache.wicket.Page;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.convert.IConverter;

import za.co.liberty.business.guicontrollers.request.IRequestViewGuiController;
import za.co.liberty.dto.gui.request.ViewRequestModelDTO;
import za.co.liberty.dto.gui.request.alternative.AlternativeVATRequestDTO;
import za.co.liberty.dto.gui.request.alternative.AlternativeVATRequestFLO;
import za.co.liberty.exceptions.data.ValidationException;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.helpers.lookup.ServiceLocator;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.pages.panels.HelperPanel;
import za.co.liberty.web.wicket.convert.converters.CurrencyConverter;
import za.co.liberty.web.wicket.convert.converters.DecimalConverter;
import za.co.liberty.web.wicket.markup.html.grid.GridToCSVHelper;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGrid;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGrid.GridSizeUnit;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGridColumn;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataProviderAdapter;
import za.co.liberty.web.wicket.markup.repeater.data.SortableListDataProvider;

import com.inmethod.grid.IGridColumn;
import com.inmethod.grid.SizeUnit;

/**
 * Panel used to show VAT Related transactions linked to VAT payments
 * 
 * @author jzb0608
 *
 */
public class AlternativeAuthVATPanel extends Panel {

	private static final long serialVersionUID = 1L;

	private transient static Logger logger = Logger.getLogger(AlternativeAuthVATPanel.class);
	
	private ViewRequestModelDTO pageModel;
	protected static FeedbackPanel feedbackPanel;
	protected AlternativeVATRequestDTO alternativeDTO = null;
	protected EditStateType editStateType;
	
	protected SRSDataGrid tempDataGrid;

	public AlternativeAuthVATPanel(String id, EditStateType editStateType,
			ViewRequestModelDTO model, Page parentPage) {
		super(id);
		this.pageModel = model;
		this.editStateType = editStateType;
		try {
			alternativeDTO = (AlternativeVATRequestDTO) ServiceLocator.lookupService(IRequestViewGuiController.class)
					.getAlternativeRequestDTO(
							model.getRequestEnquiryRowList().get(0)
									.getRequestId());
			if (alternativeDTO!= null) {
				AlternativeVATRequestDTO vatDTO = (AlternativeVATRequestDTO) alternativeDTO;
				System.out.println("VAT payment size=" + vatDTO.getVatPayments().size());
			}
		} catch (NamingException e) {
			throw new CommunicationException(e);
		} catch (ValidationException e) {
			logger.error(e);
			this.error("Unable to retrieve VAT detail");
		}
		add(createTotalRows("rowValues"));
		add(createVATGrid("vatGrid"));
		add(createExportForm("exportForm"));
		
	}
	
	
	/**
	 * Create the repeating rows
	 * 
	 * @param id
	 * @return
	 */
	public ListView createTotalRows(String id) {
		
		ListView rows = new ListView<AlternativeVATRequestDTO.VATPayment>(id, (alternativeDTO!=null) ?
				alternativeDTO.getVatPaymentTotals() : Collections.EMPTY_LIST) {

			private static final long serialVersionUID = 0L;

			@Override
			protected void populateItem(ListItem<AlternativeVATRequestDTO.VATPayment> item) {
				
				AlternativeVATRequestDTO.VATPayment vatObject = item.getModelObject();
				
				item.add(new Label("paymentID", "" + vatObject.getVatPaymentOid()));
				item.add(new Label("vatType", vatObject.getVatTypeDescription()));
				item.add(new Label("vatAmount", new CurrencyConverter().convertToString(vatObject.getAmount(), null)));
				item.add(new Label("requestID", "0"));
			}
			
		};
		return rows;
	}
	
	/**
	 * CReate the grid
	 * 
	 * @param id
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Panel createVATGrid(String id) {
		
		
		tempDataGrid = new SRSDataGrid(id, new SRSDataProviderAdapter(
						new SortableListDataProvider<AlternativeVATRequestFLO>(
								(alternativeDTO!=null) ?
								new ArrayList<AlternativeVATRequestFLO>(alternativeDTO.getVatPayments()) : Collections.EMPTY_LIST)),
						createInternalTableFieldColumns(),EditStateType.VIEW) {
							private static final long serialVersionUID = 1L;
		};
		tempDataGrid.setAutoResize(true);
		tempDataGrid.setOutputMarkupId(true);
		tempDataGrid.setCleanSelectionOnPageChange(false);
		tempDataGrid.setClickRowToSelect(false);
		tempDataGrid.setAllowSelectMultiple(false);
		tempDataGrid.setGridWidth(90, GridSizeUnit.PERCENTAGE);		
		tempDataGrid.setRowsPerPage(25);
		tempDataGrid.setContentHeight(400, SizeUnit.PX);
		return tempDataGrid;
	}

	/**
	 * Return all the columns that are required.  This includes the check box.
	 * @return
	 */
	protected List<IGridColumn> createInternalTableFieldColumns() { 
		List<IGridColumn> columns = new ArrayList<IGridColumn>();
		
//		AlternativeVATRequestFLO
		
		/**
		 * This will show the columns required per request kind, for now only 
		 * one kind is configured.
		 */
		columns.add(new SRSDataGridColumn<AlternativeVATRequestFLO>("vatOid",
				new Model("VAT Payment ID"),"vatOid","vatOid",editStateType).setInitialSize(70));

		columns.add(new SRSDataGridColumn<AlternativeVATRequestFLO>("vatTypeDescription",
				new Model("VAT Type"),"vatTypeDescription","vatTypeDescription",editStateType).setInitialSize(150));
	
	
		columns.add(new SRSDataGridColumn<AlternativeVATRequestFLO>("linkedPaymentDate",
				new Model("Date"),"linkedPaymentDate","linkedPaymentDate",editStateType).setInitialSize(110));
		
		columns.add(new SRSDataGridColumn<AlternativeVATRequestFLO>("requestId",
				new Model("Request ID"),"requestId",editStateType).setInitialSize(70));
		columns.add(new SRSDataGridColumn<AlternativeVATRequestFLO>("policyNumber",
				new Model("Policy Number"),"policyNumber",editStateType).setInitialSize(85));
		columns.add(new SRSDataGridColumn<AlternativeVATRequestFLO>("agreementNr",
				new Model("Agreement Nr"),"agreementNr",editStateType).setInitialSize(85));
		
		columns.add(new SRSDataGridColumn<AlternativeVATRequestFLO>("linkedTypeDescription",
				new Model("Transaction Type"),"linkedTypeDescription","linkedTypeDescription",editStateType).setInitialSize(220));
		
		SRSDataGridColumn c = new SRSDataGridColumn<AlternativeVATRequestFLO>("amount",
				new Model("Trans Amount"),"amount","amount",editStateType) {

			private static final long serialVersionUID = 1L;
			
			@Override
			public Panel newCellPanel(WebMarkupContainer parent,
					String componentId, IModel rowModel,
					String objectProperty, EditStateType state,
					final AlternativeVATRequestFLO dataItem) {	
					
					Label label = new Label("value", new PropertyModel(dataItem, componentId)) {
						
						@Override
					    public IConverter getConverter(Class  type) {
					        return new CurrencyConverter();
					    } 
					};
					
					return HelperPanel.getInstance(componentId, label);
			}
		};
		c.setInitialSize(90);		
		columns.add(c);
		
		c = new SRSDataGridColumn<AlternativeVATRequestFLO>("vatRate",
				new Model("VAT Rate"),"vatRate",editStateType) {
			private static final long serialVersionUID = 1L;
			
			@Override
			public Panel newCellPanel(WebMarkupContainer parent,
					String componentId, IModel rowModel,
					String objectProperty, EditStateType state,
					final AlternativeVATRequestFLO dataItem) {	
					
					Label label = new Label("value", new PropertyModel(dataItem, componentId)) {
						
						@Override
					    public IConverter getConverter(Class  type) {
					        return new DecimalConverter();
					    } 
					};
					
					return HelperPanel.getInstance(componentId, label);
			}
		};
		c.setInitialSize(60);		
		columns.add(c);
		
		c = new SRSDataGridColumn<AlternativeVATRequestFLO>("vatAmount",
				new Model("VAT Amount"),"vatAmount",editStateType) {
			private static final long serialVersionUID = 1L;
			
			@Override
			public Panel newCellPanel(WebMarkupContainer parent,
					String componentId, IModel rowModel,
					String objectProperty, EditStateType state,
					final AlternativeVATRequestFLO dataItem) {	
					
					Label label = new Label("value", new PropertyModel(dataItem, componentId)) {
						
						@Override
					    public IConverter getConverter(Class  type) {
					        return new CurrencyConverter();
					    } 
					};
					
					return HelperPanel.getInstance(componentId, label);
			}
		};
		c.setInitialSize(90);		
		columns.add(c);
		return columns;
	}
	
	/**
	 * Create the export to CSV form
	 * 
	 * @param string
	 * @return
	 */
	@SuppressWarnings("unchecked")
	/* SSM2707 Market Integration 28/09/2015 SWETA MENON Begin */
	/*
	 * Access modifier changed to Protected to allow
	 * ServicingPanelsAuthorisationSearchPanel to access the method.
	 */
	protected Form createExportForm(String id) {
		Form form = new Form(id);
		form.add(createExportButton("exportButton", form));
		return form;
	}
	
	/**
	 * Create the export button
	 * 
	 * @param id
	 * @param form
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected Button createExportButton(String id, Form form) {
		Button but = new Button(id) {
			private static final long serialVersionUID = 1L;
			
			@Override
			protected void onComponentTag(ComponentTag tag) {
				super.onComponentTag(tag);
				decorateComponentStyleToHide(alternativeDTO.getVatPayments() == null
						|| alternativeDTO.getVatPayments().size() == 0, tag);
			}
			
			@Override
			public void onSubmit() {
				super.onSubmit();
				try {
					new GridToCSVHelper().createCSVFromDataGrid(tempDataGrid,
							"Request_Enquiry.csv");
				} catch (Exception e) {	
					Logger.getLogger(this.getClass()).error(
							"An error occured when trying to generate the excel document",e);
					this.error("Error occurred during export:" + e.getCause());
				}				
			}
		};
		but.setOutputMarkupId(true);
		return but;
	}
	
	/**
	 * Decorate the style tag to hide the component
	 * 
	 * @param isHidden Hide component if true. 
	 * @param tag
	 */
	private void decorateComponentStyleToHide(boolean isHidden, ComponentTag tag) {
		if (!isHidden) {
			return;
		}
		String val = (String) tag.getAttributes().get("style");
		val = (val ==null) ? "" : val;
		val += " ;visibility:hidden;";
		tag.put("style", val);
	}
	
}
