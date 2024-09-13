package za.co.liberty.web.pages;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.wicket.Component;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.link.PopupSettings;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.data.EmptyDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;

import com.inmethod.grid.DataProviderAdapter;
import com.inmethod.grid.IGridColumn;
import com.inmethod.grid.SizeUnit;
import com.inmethod.grid.column.AbstractColumn;
import com.inmethod.grid.column.PropertyColumn;

import za.co.liberty.dto.contracting.ContractEnquiryDTO;
import za.co.liberty.dto.contracting.ContractSearchResultDTO;
import za.co.liberty.helpers.xls.ExcelCell;
import za.co.liberty.helpers.xls.ExcelPattern;
import za.co.liberty.helpers.xls.ExcelRows;
import za.co.liberty.helpers.xls.IExcelCell;
import za.co.liberty.helpers.xls.IExcelRows;
import za.co.liberty.web.constants.SRSAppWebConstants;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.helpers.ContractSearchDataProvider;
import za.co.liberty.web.wicket.convert.converters.CurrencyConverter;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGrid;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGrid.GridSizeUnit;

public class ContractNoSearchResultPanel extends Panel {
	private static final long serialVersionUID = 1L;

	private SRSDataGrid searchResults;
	private Label commision;
	private Label fees;
	private Label api;
	private Label policyCount;
	private Label prodCredit;
	private Label blueprintAll;
	private Label policyRef;
	private Button exportBtn;
	private ArrayList<ContractSearchResultDTO> contSrchRes;
	private List<IGridColumn> iColSearchRes ;
	private String policyRefStr = SRSAppWebConstants.EMPTY_STRING;
	private Label warningMsg;
	

	public ContractNoSearchResultPanel(String id,ContractEnquiryDTO contResultDTO)
	{
		super(id);
		Form form = new ContractNoSearchResultForm("contractNoSrchResForm",contResultDTO); 
		add(form);
	}
	
	class ContractNoSearchResultForm extends Form {

		ContractEnquiryDTO contResultDTO = null;
		
		public ContractNoSearchResultForm(String id,ContractEnquiryDTO contResultDTO) {
			super(id);
			this.contResultDTO = contResultDTO;
			commision = new Label("commision",getCurrencyValue(contResultDTO.getCommission()));
			fees = new Label("fees",getCurrencyValue(contResultDTO.getFees()));
			api = new Label("api",getCurrencyValue(contResultDTO.getApi()));
			policyCount = new Label("policyCount",contResultDTO.getPolicyCount().toString());
			prodCredit = new Label("prodCredit",getCurrencyValue(contResultDTO.getProductionCredits()));
			blueprintAll = new Label("blueprintAll",getCurrencyValue(contResultDTO.getBluePrintAll()));
			warningMsg = new Label("warningMsg",contResultDTO.getWarningMessage());
			
			
			
			contSrchRes = contResultDTO.getContractSrchResults();
			for(ContractSearchResultDTO c:contSrchRes){
				policyRefStr = c.getPolicyRef();
				break;
			}
			
					
			policyRef = new Label("policyRef",policyRefStr);
			
			
			
			setOutputMarkupPlaceholderTag(true);
			
			exportBtn = createExportButton("exportBtn");
			
			add(commision);
			add(fees);
			add(api);
			add(policyCount);
			add(prodCredit);
			add(blueprintAll);
			add(policyRef);
			add(new EmptyPanel("searchRes").setOutputMarkupId(true));
			add(exportBtn);
			add(warningMsg);
			
	}
		
		
		@Override
		protected void onInitialize() {
			
			Component c = get("searchRes");
			if (c instanceof EmptyPanel) {
			
				ContractSearchDataProvider contractDataProvider = new ContractSearchDataProvider(contResultDTO);
				iColSearchRes = getColumnsForTable(SRSAppWebConstants.CONTRACT_SRCH_RES);
				
				if(contractDataProvider.size() == 0)
					searchResults = new SRSDataGrid("searchRes", new DataProviderAdapter(new EmptyDataProvider()), iColSearchRes, EditStateType.VIEW);
				else
					searchResults = new SRSDataGrid("searchRes", new DataProviderAdapter(contractDataProvider), iColSearchRes, EditStateType.VIEW);
				
				searchResults.setRowsPerPage(15);
				searchResults.setContentHeight(25, SizeUnit.EM);
				
				searchResults.setAutoResize(false);
				searchResults.setGridWidth(99, GridSizeUnit.PERCENTAGE);
				
				c.replaceWith(searchResults);
			}
			
			super.onInitialize();
		}


		public class StyledColumn extends PropertyColumn implements IExcelCell{

			private static final long serialVersionUID = 1L;	
			private String cssStyle;
	        private  String value;
   			private String propertyExpression;
			
						
			public StyledColumn(IModel displayModel, String sortProperty, String propertyExpression, String style,String value) {
				super(displayModel, propertyExpression ,sortProperty);
				this.cssStyle = style;
				this.value = value;
				this.propertyExpression = propertyExpression;
			}
			
				
			public EnumExcelCellAlign getCellAlignment() {
				return EnumExcelCellAlign.Center; 
			}


			public EnumExcelCellType getCellType() {
				
				return EnumExcelCellType.Text;
			}


			public String getFontName() {
				// TODO Auto-generated method stub
				return null;
			}


			public Integer getFontSize() {
				
				return 12;
			}


			public String getValue() {
				
				return this.value;
			}


			public boolean isBold() {
				// TODO Auto-generated method stub
				return true;
			}


			public boolean isUnderLined() {
				// TODO Auto-generated method stub
				return true;
			}
			
			
		}
		
			public class LinkedStyledColumn extends AbstractColumn implements IExcelCell{

			private PopupSettings popupSettings;
			private  String value;
			
			public LinkedStyledColumn(String columnID,IModel displayModel, String sortProperty, PopupSettings popupSettings,String value) {
				super(columnID,displayModel, sortProperty);
				this.popupSettings = popupSettings;
				this.value = value;
				
			}

			private static final long serialVersionUID = 1L;

			

			/**
			 * {@inheritDoc}
			 */
			@Override
			public Component newCell(WebMarkupContainer parent, String componentId, IModel rowModel) {
				return new LinkedPanel(componentId, rowModel);
			}

			
			/**
			 * Panel with Row that has an anchor on click of which a popup has to be opened.
			 * 
			 * @author Matej Knopp
			 */
			private class LinkedPanel extends Panel {

				private static final long serialVersionUID = 1L;
				Link link = null;

				private LinkedPanel(String componentId, final IModel model) {
					super(componentId, model);
					
					link = new Link(componentId,model)	{
								
							@Override
							public void onComponentTagBody(org.apache.wicket.markup.MarkupStream markupStream, ComponentTag openTag) {
								Object obj = model.getObject();
								if (obj instanceof ContractSearchResultDTO) {
									replaceComponentTagBody(markupStream, openTag, 
											getCurrencyValue(((ContractSearchResultDTO)obj).getAllocAmount()));
								}
							};
	
	
							@Override
							public void onClick() {
								
								setResponsePage(new MPEPopupPage((ContractSearchResultDTO)model.getObject()));
														
							}
							
											              	
			            }.setPopupSettings(popupSettings);
			            
			            link.setOutputMarkupId(true);
			            add(link);
				}
					
			}


			public EnumExcelCellAlign getCellAlignment() {
				return EnumExcelCellAlign.Center; 
			}


			public EnumExcelCellType getCellType() {
				
				return EnumExcelCellType.Text;
			}


			public String getFontName() {
				// TODO Auto-generated method stub
				return null;
			}


			public Integer getFontSize() {
				
				return 12;
			}


			public String getValue() {
				
				return this.value;
			}


			public boolean isBold() {
				// TODO Auto-generated method stub
				return true;
			}


			public boolean isUnderLined() {
				// TODO Auto-generated method stub
				return true;
			}
			
		}

	     private List<IGridColumn> getColumnsForTable(String tableType){
	    	 List<IGridColumn> col = new ArrayList<IGridColumn>();
            if(tableType.equals(SRSAppWebConstants.CONTRACT_SRCH_RES)){
            	
            col.add(new StyledColumn(new ResourceModel(SRSAppWebConstants.PRODUCT_REF),SRSAppWebConstants.PRODUCT_REF,
            		SRSAppWebConstants.PRODUCT_REF, null,ContractNoSearchResultPanel.this.getString(SRSAppWebConstants.PRODUCT_REF)));
            
            col.add(new StyledColumn(new ResourceModel(SRSAppWebConstants.POLICY_STARTDT),SRSAppWebConstants.POLICY_STARTDT,
            		SRSAppWebConstants.POLICY_STARTDT, null,ContractNoSearchResultPanel.this.getString(SRSAppWebConstants.POLICY_STARTDT)));
            
            col.add(new StyledColumn(new ResourceModel(SRSAppWebConstants.LIFE_ASSURED_NAME),SRSAppWebConstants.LIFE_ASSURED_NAME,
                    SRSAppWebConstants.LIFE_ASSURED_NAME,null,ContractNoSearchResultPanel.this.getString(SRSAppWebConstants.LIFE_ASSURED_NAME)));
            col.add(new StyledColumn(new ResourceModel(SRSAppWebConstants.PREMIUM_FREQUENCY),SRSAppWebConstants.PREMIUM_FREQUENCY,
                    SRSAppWebConstants.PREMIUM_FREQUENCY, null,ContractNoSearchResultPanel.this.getString(SRSAppWebConstants.PREMIUM_FREQUENCY)));
            col.add(new StyledColumn(new ResourceModel(SRSAppWebConstants.THIRTEEN_DIGIT_CODE),SRSAppWebConstants.THIRTEEN_DIGIT_CODE,
                    "formatted13DigCodeStr",null,ContractNoSearchResultPanel.this.getString(SRSAppWebConstants.THIRTEEN_DIGIT_CODE)));
            col.add(new StyledColumn(new ResourceModel(SRSAppWebConstants.SRS_AGMT_CODE),SRSAppWebConstants.SRS_AGMT_CODE,
                    SRSAppWebConstants.SRS_AGMT_CODE,null,ContractNoSearchResultPanel.this.getString(SRSAppWebConstants.SRS_AGMT_CODE)));
            col.add(new StyledColumn(new ResourceModel(SRSAppWebConstants.AGMT_PARTY_NAME),SRSAppWebConstants.AGMT_PARTY_NAME,
                    SRSAppWebConstants.AGMT_PARTY_NAME,null,ContractNoSearchResultPanel.this.getString(SRSAppWebConstants.AGMT_PARTY_NAME)));
            col.add(new StyledColumn(new ResourceModel(SRSAppWebConstants.COMM_KIND),SRSAppWebConstants.COMM_KIND,
                    SRSAppWebConstants.COMM_KIND,null,ContractNoSearchResultPanel.this.getString(SRSAppWebConstants.COMM_KIND)));
            col.add(new StyledColumn(new ResourceModel(SRSAppWebConstants.DPE_AMT),SRSAppWebConstants.DPE_AMT,
            		SRSAppWebConstants.DPE_AMT,null,ContractNoSearchResultPanel.this.getString(SRSAppWebConstants.DPE_AMT)));
            
            //Hyperlink to be provided on Allocated amount which opens a popup            
            PopupSettings popupSettings = new PopupSettings(PopupSettings.RESIZABLE);
             popupSettings.setWindowName("MPEPopupPage").setWidth(175).setHeight(250).setTop(250).setLeft(350);
            
             col.add(new LinkedStyledColumn("linkPanel",new ResourceModel(SRSAppWebConstants.ALLOCATED_AMT),SRSAppWebConstants.ALLOCATED_AMT,
                  popupSettings,ContractNoSearchResultPanel.this.getString(SRSAppWebConstants.ALLOCATED_AMT)));
            
            col.add(new StyledColumn(new ResourceModel(SRSAppWebConstants.SSF_AMT),SRSAppWebConstants.SSF_AMT,
            		SRSAppWebConstants.SSF_AMT,null,ContractNoSearchResultPanel.this.getString(SRSAppWebConstants.SSF_AMT)));
            col.add(new StyledColumn(new ResourceModel(SRSAppWebConstants.CONT_INC_INDICATOR),SRSAppWebConstants.CONT_INC_INDICATOR,
                    SRSAppWebConstants.CONT_INC_INDICATOR,null,ContractNoSearchResultPanel.this.getString(SRSAppWebConstants.CONT_INC_INDICATOR)));
            col.add(new StyledColumn(new ResourceModel(SRSAppWebConstants.BENEFIT_TYPE),SRSAppWebConstants.BENEFIT_TYPE,
                    SRSAppWebConstants.BENEFIT_TYPE, null,ContractNoSearchResultPanel.this.getString(SRSAppWebConstants.BENEFIT_TYPE)));
            col.add(new StyledColumn(new ResourceModel(SRSAppWebConstants.NO_OF_MONTHS),SRSAppWebConstants.NO_OF_MONTHS,
                    SRSAppWebConstants.NO_OF_MONTHS,null,ContractNoSearchResultPanel.this.getString(SRSAppWebConstants.NO_OF_MONTHS)));
            col.add(new StyledColumn(new ResourceModel(SRSAppWebConstants.COMM_DISC_FACTOR),SRSAppWebConstants.COMM_DISC_FACTOR,
                    SRSAppWebConstants.COMM_DISC_FACTOR,null,ContractNoSearchResultPanel.this.getString(SRSAppWebConstants.COMM_DISC_FACTOR)));
            col.add(new StyledColumn(new ResourceModel(SRSAppWebConstants.BALANCE_DUE),SRSAppWebConstants.BALANCE_DUE,
            		SRSAppWebConstants.BALANCE_DUE,null,ContractNoSearchResultPanel.this.getString(SRSAppWebConstants.BALANCE_DUE)));
            col.add(new StyledColumn(new ResourceModel(SRSAppWebConstants.AMT_PAID_TO_DT),SRSAppWebConstants.AMT_PAID_TO_DT,
            		SRSAppWebConstants.AMT_PAID_TO_DT,null,ContractNoSearchResultPanel.this.getString(SRSAppWebConstants.AMT_PAID_TO_DT)));
            col.add(new StyledColumn(new ResourceModel(SRSAppWebConstants.NEXT_PAY_AMT),SRSAppWebConstants.NEXT_PAY_AMT,
            		SRSAppWebConstants.NEXT_PAY_AMT,null,ContractNoSearchResultPanel.this.getString(SRSAppWebConstants.NEXT_PAY_AMT)));
            col.add(new StyledColumn(new ResourceModel(SRSAppWebConstants.NEXT_PAY_DUE_DT),SRSAppWebConstants.NEXT_PAY_DUE_DT,
            		SRSAppWebConstants.NEXT_PAY_DUE_DT,null,ContractNoSearchResultPanel.this.getString(SRSAppWebConstants.NEXT_PAY_DUE_DT)));
            col.add(new StyledColumn(new ResourceModel(SRSAppWebConstants.REQUESTED_DT),SRSAppWebConstants.REQUESTED_DT,
              		SRSAppWebConstants.REQUESTED_DT,null,ContractNoSearchResultPanel.this.getString(SRSAppWebConstants.REQUESTED_DT)));
            col.add(new StyledColumn(new ResourceModel(SRSAppWebConstants.EXECUTED_DT),SRSAppWebConstants.EXECUTED_DT,
               		SRSAppWebConstants.EXECUTED_DT,null,ContractNoSearchResultPanel.this.getString(SRSAppWebConstants.EXECUTED_DT)));
            col.add(new StyledColumn(new ResourceModel("movementDescription"),"movementDescription",
                    "movementDescription",null,ContractNoSearchResultPanel.this.getString("movementDescription")));

            col.add(new StyledColumn(new ResourceModel(SRSAppWebConstants.MOVEMENT_CODE),SRSAppWebConstants.MOVEMENT_CODE,
                    SRSAppWebConstants.MOVEMENT_CODE,null,ContractNoSearchResultPanel.this.getString(SRSAppWebConstants.MOVEMENT_CODE)));
            col.add(new StyledColumn(new ResourceModel(SRSAppWebConstants.MOVEMENT_EFF_DT),SRSAppWebConstants.MOVEMENT_EFF_DT,
            		SRSAppWebConstants.MOVEMENT_EFF_DT,null,ContractNoSearchResultPanel.this.getString(SRSAppWebConstants.MOVEMENT_EFF_DT)));
            col.add(new StyledColumn(new ResourceModel(SRSAppWebConstants.REQUEST_STATUS),SRSAppWebConstants.REQUEST_STATUS,
                    SRSAppWebConstants.REQUEST_STATUS,null,ContractNoSearchResultPanel.this.getString(SRSAppWebConstants.REQUEST_STATUS)));
            col.add(new StyledColumn(new ResourceModel(SRSAppWebConstants.REQUESTOR_UACF),SRSAppWebConstants.REQUESTOR_UACF,
                    SRSAppWebConstants.REQUESTOR_UACF,null,ContractNoSearchResultPanel.this.getString(SRSAppWebConstants.REQUESTOR_UACF)));
            col.add(new StyledColumn(new ResourceModel(SRSAppWebConstants.AUTHORISOR_UACF),SRSAppWebConstants.AUTHORISOR_UACF,
                    SRSAppWebConstants.AUTHORISOR_UACF,null,ContractNoSearchResultPanel.this.getString(SRSAppWebConstants.AUTHORISOR_UACF)));
             }
           
             return col;
	           }
		
		/**
		 * Create an Export Button that handles the export to Excel utility}
		 * 
		 * @param id
		 * @return
		 */
		private Button createExportButton(String id) {
			
			Button button = new Button(id) {

				private static final long serialVersionUID = 1L;

				@Override
				protected void onComponentTag(ComponentTag tag) {
					super.onComponentTag(tag);
					tag.getAttributes().put("type", "submit");
				}

				@Override
				public void onSubmit() {
					
					((BasePage)this.getPage()).streamToExcel(getExcelRow());
					
				}
			};
			
			button.setOutputMarkupId(true);
			button.setDefaultFormProcessing(false);
			return button;
		}

	}
	
	public IExcelRows getExcelRow() {
		ArrayList<ContractSearchResultDTO> contractsList = this.contSrchRes;
		//For the Headers in the Excel Sheet
		ExcelRows excelRow = new ExcelRows();
		//For Policy Reference header in Excel
		excelRow.addExcelRow(getPolicyReferenceRow());
		excelRow.addExcelRow(getHeaderRow());
		ExcelPattern getPattern = new ExcelPattern();
		getPattern.setPattern("get");
		getPattern.setIncluded(true);
		getPattern.setStartsWith(true);
		
		ExcelPattern listPattern = new ExcelPattern();
		listPattern.setPattern("List");
		
		ExcelPattern classPattern = new ExcelPattern();
		classPattern.setPattern("Class");
		
		ExcelPattern strPattern = new ExcelPattern();
		classPattern.setPattern("Str");
		
		ExcelPattern policyPattern = new ExcelPattern();
		policyPattern.setPattern("PolicyRef");
		List<String> propertyNameList = new ArrayList(); 
		List<IGridColumn> columnList = this.searchResults.getAllColumns();
		for (IGridColumn column : columnList) {
			propertyNameList.add((String) column.getSortProperty());
		}
		for(ContractSearchResultDTO contractSearchResultDTO:contractsList){
			List<IExcelCell> iExcelCellList;
					
			try {
				iExcelCellList = excelRow.createRowWithIntroSpection(contractSearchResultDTO,propertyNameList, getPattern,listPattern,classPattern,policyPattern,strPattern);
				excelRow.addExcelRow(iExcelCellList);
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		}
		
		return excelRow;
	}


	
	private  List getHeaderRow() {
		return this.iColSearchRes;
				
	}
	
	private  List<IExcelCell> getPolicyReferenceRow() {
		List<IExcelCell> headerPolicyRefList  = new ArrayList<IExcelCell>();
		
		ExcelCell policyReftLabelCell = new ExcelCell();
		policyReftLabelCell.setBold(true);
		policyReftLabelCell.setUnderLined(true);
		policyReftLabelCell.setValue(SRSAppWebConstants.POLICY_REF_LABLE);
		headerPolicyRefList.add(policyReftLabelCell);
		
		ExcelCell policyReftValueCell = new ExcelCell();
		policyReftValueCell.setBold(false);
		policyReftLabelCell.setUnderLined(false);
		policyReftValueCell.setValue(this.policyRefStr);
		headerPolicyRefList.add(policyReftValueCell);
		
			
		return headerPolicyRefList;
				
	}
	
	private String getCurrencyValue(BigDecimal obj){
		
		String currFormat = SRSAppWebConstants.EMPTY_STRING;
		CurrencyConverter converter = new CurrencyConverter();
		currFormat = converter.convertToString(obj, Locale.getDefault());
		 return currFormat;
		
	}

}


