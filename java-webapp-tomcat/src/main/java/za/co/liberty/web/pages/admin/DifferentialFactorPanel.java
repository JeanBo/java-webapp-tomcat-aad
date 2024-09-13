package za.co.liberty.web.pages.admin;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.convert.IConverter;
import org.apache.wicket.util.convert.converter.BigDecimalConverter;//org.apache.wicket.util.convert.converters.BigDecimalConverter;

import com.inmethod.grid.DataProviderAdapter;
import com.inmethod.grid.IGridColumn;
import com.inmethod.grid.SizeUnit;

import za.co.liberty.dto.rating.DifferentialPricingFactorDTO;
import za.co.liberty.dto.rating.SegmentNameDTO;
import za.co.liberty.interfaces.rating.difffactor.AQCProductType;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.pages.MaintenanceBasePage;
import za.co.liberty.web.pages.MaintenanceBasePage.SelectionForm;
import za.co.liberty.web.pages.admin.models.DifferentialFactorModel;
import za.co.liberty.web.pages.panels.BasePanel;
import za.co.liberty.web.wicket.markup.html.form.SRSAbstractChoiceRenderer;
import za.co.liberty.web.wicket.markup.html.form.SRSDropDownChoice;
import za.co.liberty.web.wicket.markup.html.form.SRSTextField;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGrid;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGrid.GridSizeUnit;
import za.co.liberty.web.wicket.markup.html.grid.SRSGridRowSelectionCheckBox;
import za.co.liberty.web.wicket.markup.repeater.data.ListDataProvider;

/**
 * Differential factor panel 
 * @author jwv2310
 *
 */
public class DifferentialFactorPanel extends BasePanel {
	
	protected SelectionForm selectionForm;
	
	private SRSDataGrid factorGrid = null;
	private SRSTextField factorId = null;
	
	private SRSTextField segmentId = null;
	private DropDownChoice segmentNameCombo = null;
	
	private SRSTextField aqcValue = null;
	private SRSDropDownChoice aqcProductCombo = null;
	
	private SRSTextField aqcProdCode = null;
//	private SRSTextField premDiscPerc = null;
	private TextField premDiscPerc = null;
	
//	private SRSTextField aqcAppCode = null;
	private SRSTextField defCommPerc = null;
	
	private SRSTextField deffPerYears = null;
	private SRSTextField monthPremAf= null;
	private SRSTextField pcrEnhPercent = null;
	private SRSTextField maxUpfrontCommPerc = null;
	private SRSTextField maxCommTermYrs = null;
	private SRSTextField upfrontCommClawbackYrs = null;
	private SRSTextField pcrClawbackMnths = null;
	private SRSTextField startDate = null;
	private CheckBox endDate = null;
	private CheckBox competitorDiscount=null;
	private static final long serialVersionUID = 1L;
	private DifferentialFactorModel pageModel;
	
	public DifferentialFactorPanel(String id, EditStateType editState) {
		super(id, editState);

	}

	public DifferentialFactorPanel(String id, EditStateType editState, DifferentialFactorModel page, MaintenanceBasePage parentPage) {
		super(id, editState,parentPage);
		pageModel = page;
		initialize();
	}
	

	private void initialize(){
		add(aqcValue = createAQCValue("aqcValue"));
		add(segmentNameCombo = createSegmentNameField("segmentName"));
		add(aqcProductCombo = createAQCCombo("aqcProductCombo"));
		add(premDiscPerc =createPremDiscPercent("premDiscPercent"));
		//add(aqcAppCode = createAqcAppCode("aqcAppCode"));
		add(defCommPerc = createDefCommPercent("defCommPercent"));
		add(this.maxCommTermYrs = this.createMaxCommTermYrs("maxCommTermYrs"));
		add(this.maxUpfrontCommPerc = this.createMaxUpfrontCommPerc("maxUpfrontCommPerc"));
		add(this.pcrEnhPercent= this.createPcrEnhPercent("pcrEnhPercent"));
		add(this.monthPremAf= this.createMonthPremAf("monthPremAf"));
		add(deffPerYears=this.createDeffPerYears("defPeriodYrs"));
		add(upfrontCommClawbackYrs = createUpfrontCommClawbackYrs("upfrontCommClawbackYrs"));
		add(pcrClawbackMnths = createPcrClawbackMnths("pcrClawbackMnths"));
		add(endDate = createEndDate("endDate"));
		add(competitorDiscount= createCompetitorDiscount("competitorDiscount"));
	}
	
	@SuppressWarnings("unchecked")
	private SRSDropDownChoice createAQCCombo(String id) {
		PropertyModel models = null;
		models = new PropertyModel(pageModel.getSelectedItem() ,"aqcProductCode");
		AQCProductType[] aqcProdType = AQCProductType.values();
		List<AQCProductType> aqcProdList = new ArrayList<AQCProductType>();
		for(AQCProductType aqcProdTyp : aqcProdType){
			aqcProdList.add(aqcProdTyp);
		}
		
		SRSDropDownChoice field = new SRSDropDownChoice("AQCProductCombo", models,aqcProdList,
				new ChoiceRenderer() {

					private static final long serialVersionUID = 3371423967058938834L;
					public Object getDisplayValue(Object arg0) {
						   if (arg0==null) {
							   return null;
						   }
						   return arg0;
					}	
				    public String getIdValue(Object arg0, int arg1) {
				    	   if (arg0==null) {
							   return null;
						   }
						    return ""+arg1;

					}},"**SELECT ONE**");
				    
				
				field.setOutputMarkupId(true);
				field.add(new AjaxFormComponentUpdatingBehavior("change"){
					private static final long serialVersionUID = 1L;

					@Override
					protected void onUpdate(AjaxRequestTarget target){
					}	  
				});
				field.setEnabled(!getEditState().isViewOnly());
				field.setRequired(true);
				return field;
	}
	
	/**
	 * Create the name field
	 * 
	 * @param id
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private DropDownChoice createSegmentNameField(String id){
		DropDownChoice field = new DropDownChoice(id, new PropertyModel(pageModel.getSelectedItem(), "segmentNameObject"), 
				pageModel.getAllAvailableSegmentNameList(), 
		new SRSAbstractChoiceRenderer() {
			private static final long serialVersionUID = -4367276358153378234L;
			
			public Object getDisplayValue(Object value) {
				return (value==null)?null:((SegmentNameDTO)value).getSegmentName();
			}
			public String getIdValue(Object arg0, int arg1) {
				return (arg0 == null) ? null : ""+((SegmentNameDTO)arg0).getId();
			}
		});
		
		field.setNullValid(true);
		field.setEnabled(!getEditState().isViewOnly());
		field.setRequired(true);
		return field;
	}
	
	private SRSTextField createStartDate(String id){
		SRSTextField tempSRSTextField = new SRSTextField(id,new PropertyModel(pageModel,"selectedItem.startDate" ));
		tempSRSTextField.setOutputMarkupId(true);
		tempSRSTextField.setEnabled(!getEditState().isViewOnly());
		
		return tempSRSTextField;
	}
	
	private CheckBox createEndDate(String id){
			endDate = new CheckBox(id, new PropertyModel(pageModel.getSelectedItem() ,"endDateActive" )){
				  private static final long serialVersionUID = 1L;
			};
			endDate.add(new AjaxFormComponentUpdatingBehavior("click"){
				private static final long serialVersionUID = 1L;

				@Override
				protected void onUpdate(AjaxRequestTarget target) {
				}
			});
			if(getEditState() == EditStateType.ADD){
				endDate.setVisible(false);
			}
			endDate.setEnabled(!getEditState().isViewOnly());
			
			return endDate;
	}
	
	
	private CheckBox createCompetitorDiscount(String id){
		competitorDiscount = new CheckBox(id, new PropertyModel(pageModel.getSelectedItem() ,"competitorDiscount" )){
				  private static final long serialVersionUID = 1L;
			};
			competitorDiscount.add(new AjaxFormComponentUpdatingBehavior("click"){
				private static final long serialVersionUID = 1L;

				@Override
				protected void onUpdate(AjaxRequestTarget target) {
				}
			});
			if(getEditState() == EditStateType.ADD){
				competitorDiscount.setVisible(false);
			}
			competitorDiscount.setEnabled(!getEditState().isViewOnly());
			
			return competitorDiscount;
	}
	
	
	/*PropertyModel propertyModel = new PropertyModel(
			getPropertyModelTarget(), "faisLicenseDTO.fsp");
	CheckBox fsbUpdatedBox = new CheckBox("value", propertyModel);
	
	Label viewLabel = new Label("value", propertyModel) {
		@Override
		public IConverter getConverter(Class arg0) {
			return new YesNoBooleanConverter();
		}
	};*/

	private SRSTextField createUpfrontCommClawbackYrs(String id){
		SRSTextField tempSRSTextField = new SRSTextField(id,new PropertyModel(pageModel,"selectedItem.upfrontCommClawbackInYrs" ));
		tempSRSTextField.setOutputMarkupId(true);
		tempSRSTextField.setEnabled(!getEditState().isViewOnly());
		return tempSRSTextField;
	}
	private SRSTextField createPcrClawbackMnths(String id){
		SRSTextField tempSRSTextField = new SRSTextField(id,new PropertyModel(pageModel,"selectedItem.pcrClawbackInMonths" ));
		tempSRSTextField.setOutputMarkupId(true);
		tempSRSTextField.setEnabled(!getEditState().isViewOnly());
		return tempSRSTextField;
	}
	
	private SRSTextField createDeffPerYears(String id){
		SRSTextField tempSRSTextField = new SRSTextField(id,new PropertyModel(pageModel,"selectedItem.deferredPeriodInYrs" )){			
			private static final long serialVersionUID = 1L;
			@SuppressWarnings("unused")
			public IConverter getConverter(Class cls) { //MSK#Change:getConverter(Class<?> cls) {
				if(cls == BigDecimal.class){
					return new BigDecimalConverter();
				}
				return super.getConverter(cls);
			}				
		};
		tempSRSTextField.setOutputMarkupId(true);
		tempSRSTextField.setEnabled(!getEditState().isViewOnly());
		return tempSRSTextField;
	}
	private SRSTextField createMonthPremAf(String id){
		SRSTextField tempSRSTextField = new SRSTextField(id,new PropertyModel(pageModel,"selectedItem.monthPremiumAFactor" )){			
				private static final long serialVersionUID = 1L;
				@SuppressWarnings("unused")
				public IConverter getConverter(Class cls) { //MSK#Change:getConverter(Class<?> cls) {
					if(cls == BigDecimal.class){
						return new BigDecimalConverter();
					}
					return super.getConverter(cls);
				}				
		};
		tempSRSTextField.setOutputMarkupId(true);
		tempSRSTextField.setEnabled(!getEditState().isViewOnly());
		return tempSRSTextField;
	}
	private SRSTextField createPcrEnhPercent(String id){
		SRSTextField tempSRSTextField = new SRSTextField(id,new PropertyModel(pageModel,"selectedItem.pcrEnhancementPercent" )){
			@Override
			public IConverter getConverter(Class cls) { //MSK#Change:getConverter(Class<?> cls) {
				if(cls == BigDecimal.class){
					return new BigDecimalConverter();
				}
				return super.getConverter(cls);
			}			
		};
		tempSRSTextField.setOutputMarkupId(true);
		tempSRSTextField.setEnabled(!getEditState().isViewOnly());
		tempSRSTextField.getConverter(BigDecimal.class);
		return tempSRSTextField;
	}
	
	private SRSTextField createMaxUpfrontCommPerc(String id){
		SRSTextField tempSRSTextField = new SRSTextField(id,new PropertyModel(pageModel,"selectedItem.maxUpfrontCommPercent" )){
			private static final long serialVersionUID = 1L;
			@SuppressWarnings("unused")
			public IConverter getConverter(Class cls) { //MSK#Change:getConverter(Class<?> cls) {
				if(cls == BigDecimal.class){
					return new BigDecimalConverter();
				}
				return super.getConverter(cls);
			}				
			};
		
		tempSRSTextField.setOutputMarkupId(true);
		tempSRSTextField.setEnabled(!getEditState().isViewOnly());
		return tempSRSTextField;
	}
	private SRSTextField createMaxCommTermYrs(String id){
		SRSTextField tempSRSTextField = new SRSTextField(id,new PropertyModel(pageModel,"selectedItem.maxCommTermYrs" )){
			private static final long serialVersionUID = 1L;
			@SuppressWarnings("unused")
			public IConverter getConverter(Class cls) { //MSK#Change:getConverter(Class<?> cls) {
				if(cls == BigDecimal.class){
					return new BigDecimalConverter();
				}
				return super.getConverter(cls);
			}				
			};
		tempSRSTextField.setOutputMarkupId(true);
		tempSRSTextField.setEnabled(!getEditState().isViewOnly());
		return tempSRSTextField;
	}	
	
		
	private SRSTextField createAqcAppCode(String id){
		SRSTextField tempSRSTextField = new SRSTextField(id,new PropertyModel(pageModel,"selectedItem.aqcAppCode" ));
		tempSRSTextField.setOutputMarkupId(true);
		tempSRSTextField.setEnabled(!getEditState().isViewOnly());
		tempSRSTextField.setRequired(true);
		return tempSRSTextField;
	}
	private TextField createPremDiscPercent(String id){
		TextField tempSRSTextField = new TextField<BigDecimal>(id,new PropertyModel(pageModel,"selectedItem.premiumDiscountPercent")){
			private static final long serialVersionUID = 1L;
			@SuppressWarnings("unused")
			public IConverter getConverter(Class cls) { //MSK#Change:getConverter(Class<?> cls) {
				if(cls == BigDecimal.class){
					return new BigDecimalConverter();
				}
				return super.getConverter(cls);
			}				
			};
		tempSRSTextField.setOutputMarkupId(true);
		tempSRSTextField.setEnabled(!getEditState().isViewOnly());
				
		return tempSRSTextField;
	}
	private SRSTextField createDefCommPercent(String id){
		SRSTextField tempSRSTextField = new SRSTextField(id,new PropertyModel(pageModel,"selectedItem.deferredCommPercent" )){
			private static final long serialVersionUID = 1L;
			@SuppressWarnings("unused")
			public IConverter getConverter(Class cls) { //MSK#Change:getConverter(Class<?> cls) {
				if(cls == BigDecimal.class){
					return new BigDecimalConverter();
				}
				return super.getConverter(cls);
			}				
		};	
		tempSRSTextField.setOutputMarkupId(true);
		tempSRSTextField.setEnabled(!getEditState().isViewOnly());
		return tempSRSTextField;
	}
	
	private SRSTextField createAQCValue(String id){
		SRSTextField tempSRSTextField = new SRSTextField(id,new PropertyModel(pageModel,"selectedItem.aqcValue" ));
		tempSRSTextField.setOutputMarkupId(true);
		tempSRSTextField.setEnabled(!getEditState().isViewOnly());
		tempSRSTextField.setRequired(true);
		return tempSRSTextField;

}

	private SRSDataGrid createGrid(String name){
		SRSDataGrid tempDataGrid
		= new SRSDataGrid("factorGrid",new DataProviderAdapter(
					new ListDataProvider<DifferentialPricingFactorDTO>(pageModel.getSelectionList())),createSearchResultColumns(),getEditState()) {
						private static final long serialVersionUID = 1L;

						@Override
						public void update() {
							super.update();
						}
		};
		tempDataGrid.setAutoResize(true);
		tempDataGrid.setOutputMarkupId(true);
		tempDataGrid.setCleanSelectionOnPageChange(false);
		tempDataGrid.setClickRowToSelect(false);
		tempDataGrid.setAllowSelectMultiple(false);
		tempDataGrid.setGridWidth(950, GridSizeUnit.PIXELS);		
		tempDataGrid.setRowsPerPage(10);
		tempDataGrid.setContentHeight(100, SizeUnit.PX);

		return tempDataGrid;
	}
	protected List<IGridColumn> createSearchResultColumns() { 
		List<IGridColumn> columns = new ArrayList<IGridColumn>();
		SRSGridRowSelectionCheckBox col = new SRSGridRowSelectionCheckBox("checkBox");
		col.setInitialSize(30);
		columns.add(col);

		return columns;
	}
		
	
	

}
