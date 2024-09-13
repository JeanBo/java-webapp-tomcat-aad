package za.co.liberty.web.pages.transactions;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.convert.IConverter;

import com.inmethod.grid.DataProviderAdapter;
import com.inmethod.grid.IGridColumn;
import com.inmethod.grid.SizeUnit;

import za.co.liberty.dto.transaction.IPolicyTransactionModel;
import za.co.liberty.dto.transaction.PolicyInfoCommissionCalculationDTO;
import za.co.liberty.dto.transaction.RecordPolicyInfoDTO;
import za.co.liberty.interfaces.agreements.PolicyInfoKindType;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.pages.panels.HelperPanel;
import za.co.liberty.web.wicket.convert.converters.DecimalConverter;
import za.co.liberty.web.wicket.markup.html.form.SRSLabel;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGrid;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGrid.GridSizeUnit;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGridColumn;
import za.co.liberty.web.wicket.markup.repeater.data.ListDataProvider;

/**
 * Here we show additional data for Policy Info fields, specifically commission calculations.
 * 
 * This class has been extended to provide for multiple calculations relating to Policy 
 * Info transactions. Hopefully all new calculations will follow this 2nd method.
 * 
 * @author JZB0608
 *
 */
public class AdditionalPolicyInfoFieldsPanel extends	AbstractPolicyTransactionPanel {

	private static final long serialVersionUID = 1L;
	
	final private IPolicyTransactionModel model;
	protected static FeedbackPanel feedbackPanel;
	
	private WebMarkupContainer stiPremiumContainer;

	public AdditionalPolicyInfoFieldsPanel(String id, EditStateType editStateType, IPolicyTransactionModel model, Page parentPage) {
		super(id, editStateType, model, parentPage);
		this.model = model;
		
		addFields();
	}

	private void addFields() {

		stiPremiumContainer = new WebMarkupContainer("stiPremiumContainer")  {
		};
		add(stiPremiumContainer);
		
		stiPremiumContainer.add(createAQCType("aqcType"));
		stiPremiumContainer.add(createAQCCode("aqcCode"));
		stiPremiumContainer.add(createAQCApplicableVersion("applicableVersion"));
		stiPremiumContainer.add(createReferralFeeMax("referralFeeMax"));
		stiPremiumContainer.add(createServiceFeeMax("serviceFeeMax"));
		
		RecordPolicyInfoDTO dto = (RecordPolicyInfoDTO) model.getSelectedObject();
		
		stiPremiumContainer.setVisible(dto.getInfoKindType()!=null
				&& (dto.getInfoKindType()==PolicyInfoKindType.PolicyInfoCollectedPremium.getType()
					|| dto.getInfoKindType()==PolicyInfoKindType.PolicyInfoCollectedPremium.getType() ));
	
		add(createPolicyInfoGrid("policyInfoList"));
		
		
	}

	private Component createReferralFeeMax(String id) {
		return new SRSLabel(id, new Model<Serializable>(model.getSelectedPolicyInfoCalculation().getReferralFeeMax())) {
			@Override
			public <C> IConverter<C> getConverter(Class<C> type) {
		        return (IConverter<C>) new DecimalConverter();
		    } 
		};
	}
	private Component createServiceFeeMax(String id) {
		return new SRSLabel(id, new Model<Serializable>(model.getSelectedPolicyInfoCalculation().getServiceFeeMax())) {
			@Override
			public <C> IConverter<C> getConverter(Class<C> type) {
		        return (IConverter<C>) new DecimalConverter();
		    } 
		};
	}
	
	private Component createAQCType(String id) {
		return new SRSLabel(id, new Model<Serializable>(model.getSelectedPolicyInfoCalculation().getAqcType()));
	}

	private Component createAQCCode(String id) {
		return new SRSLabel(id, new Model<Serializable>(model.getSelectedPolicyInfoCalculation().getAqcCode()));
	}
	
	private Component createAQCApplicableVersion(String id) {
		return new SRSLabel(id, new Model<Serializable>(model.getSelectedPolicyInfoCalculation().getApplicableProducsVersionIdUsed()));
	}
	
	@SuppressWarnings("unchecked")
	public Panel createPolicyInfoGrid(String id) {
		
		SRSDataGrid tempDataGrid = new SRSDataGrid(id,new DataProviderAdapter(
						new ListDataProvider(model.getSelectedPolicyInfoCalculation().getCalculationList())),
						createInternalTableFieldColumns(),getEditState()) {
							private static final long serialVersionUID = 1L;
		};
		tempDataGrid.setAutoResize(true);
		tempDataGrid.setOutputMarkupId(true);
		tempDataGrid.setCleanSelectionOnPageChange(false);
		tempDataGrid.setClickRowToSelect(false);
		tempDataGrid.setAllowSelectMultiple(false);
		tempDataGrid.setGridWidth(98, GridSizeUnit.PERCENTAGE);		
		tempDataGrid.setRowsPerPage(5);
		tempDataGrid.setContentHeight(90, SizeUnit.PX);
		return tempDataGrid;
	}

	/**
	 * Return all the columns that are required.  This includes the check box.
	 * @return
	 */
	protected List<IGridColumn> createInternalTableFieldColumns() { 
		List<IGridColumn> columns = new ArrayList<IGridColumn>();
		
//		PolicyInfoCommissionCalculationDTO.PolicyInfoListDTO
		
		/**
		 * This will show the columns required per request kind, for now only 
		 * one kind is configured.
		 */
		columns.add(new SRSDataGridColumn<PolicyInfoCommissionCalculationDTO.PolicyInfoListDTO>("commKind",
				new Model("Comm Kind"),"commKind",getEditState()).setInitialSize(70));

		
		SRSDataGridColumn c = new SRSDataGridColumn<PolicyInfoCommissionCalculationDTO.PolicyInfoListDTO>("rateUsed",
				new Model("Rate Used"),"rateUsed",getEditState()) {

			private static final long serialVersionUID = 1L;
			
			@Override
			public Panel newCellPanel(WebMarkupContainer parent,
					String componentId, IModel rowModel,
					String objectProperty, EditStateType state,
					final PolicyInfoCommissionCalculationDTO.PolicyInfoListDTO dataItem) {	
					
					Label label = new Label("value", new PropertyModel(dataItem, componentId)) {

						@Override
						public <C> IConverter<C> getConverter(Class<C> type) {
					        return (IConverter<C>) new DecimalConverter();
					    } 
					};
					
					return HelperPanel.getInstance(componentId, label);
			}
		};
		c.setInitialSize(60);		
		columns.add(c);
		
		/**
		 *  Add the base rate only if it is not STI transactions
		 */
		RecordPolicyInfoDTO dto = (RecordPolicyInfoDTO) model.getSelectedObject();
		
		if (dto != null && !(dto.getInfoKindType()==PolicyInfoKindType.PolicyInfoCollectedPremium.getType()
				|| dto.getInfoKindType()==PolicyInfoKindType.PolicyInfoCollectedPremium.getType() )) {
			
			// This is not STI so it has base rate
			c = new SRSDataGridColumn<PolicyInfoCommissionCalculationDTO.PolicyInfoListDTO>("baserateUsed",
					new Model("Base Rate"),"baserateUsed",getEditState()) {

				private static final long serialVersionUID = 1L;
				
				@Override
				public Panel newCellPanel(WebMarkupContainer parent,
						String componentId, IModel rowModel,
						String objectProperty, EditStateType state,
						final PolicyInfoCommissionCalculationDTO.PolicyInfoListDTO dataItem) {	
						
						Label label = new Label("value", new PropertyModel(dataItem, componentId)) {
							
							@Override
							public <C> IConverter<C> getConverter(Class<C> type) {
						        return (IConverter<C>) new DecimalConverter();
						    } 
						};
						
						return HelperPanel.getInstance(componentId, label);
				}
			};
			c.setInitialSize(60);		
			columns.add(c);
		}
		
		columns.add(new SRSDataGridColumn<PolicyInfoCommissionCalculationDTO.PolicyInfoListDTO>("description",
				new Model("Description"),"description",getEditState()).setInitialSize(350));
		columns.add(new SRSDataGridColumn<PolicyInfoCommissionCalculationDTO.PolicyInfoListDTO>("dpe.productReference",
				new Model("Product Ref"),"dpe.productRef",getEditState()).setInitialSize(65));
		columns.add(new SRSDataGridColumn<PolicyInfoCommissionCalculationDTO.PolicyInfoListDTO>("dpe.dpeAmount",
				new Model("Amount"),"dpe.dpeAmount",getEditState()).setInitialSize(80));
		columns.add(new SRSDataGridColumn<PolicyInfoCommissionCalculationDTO.PolicyInfoListDTO>("dpe.dpeAllocatedAmount",
				new Model("Alloc Amount"),"dpe.dpeAllocatedAmount",getEditState()).setInitialSize(80));
		
		return columns;
	}
}
