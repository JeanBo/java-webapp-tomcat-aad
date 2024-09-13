package za.co.liberty.web.pages.admin;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxFallbackButton;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.ConverterLocator;//org.apache.wicket.util.convert.ConverterLocator;
import org.apache.wicket.util.convert.IConverter;
import org.apache.wicket.util.convert.converter.BigDecimalConverter;//org.apache.wicket.util.convert.converters.BigDecimalConverter;

import za.co.liberty.business.broadcast.aqc.IFactorTableBroadcastController;
import za.co.liberty.business.guicontrollers.admin.IDifferentialFactorGuiController;
import za.co.liberty.dto.rating.DifferentialPricingFactorDTO;
import za.co.liberty.dto.rating.SegmentNameDTO;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.helpers.lookup.ServiceLocator;
import za.co.liberty.helpers.util.DateUtil;
import za.co.liberty.web.pages.MaintenanceBasePage;
import za.co.liberty.web.pages.admin.models.DifferentialFactorModel;
import za.co.liberty.web.pages.panels.AbstractTableMaintenanceSelectionPanel;
import za.co.liberty.web.pages.panels.DefaultMaintenanceSelectionPanel;
import za.co.liberty.web.wicket.markup.html.form.SRSAbstractChoiceRenderer;

import com.inmethod.grid.IGridColumn;
import com.inmethod.grid.column.PropertyColumn;

/**
 * The Differential factor admin screen page
 * @author jwv2310
 *
 */
public class DifferentialPricingPage extends MaintenanceBasePage<Object>   {
	
	private static final long serialVersionUID = 1L;
	
	private DifferentialFactorModel pageModel;
	private transient IDifferentialFactorGuiController guiController;
	private static final Logger logger = Logger.getLogger(DifferentialPricingPage.class);
	
	public DifferentialPricingPage() {
		super(null);
	}
	
	public DifferentialPricingPage(DifferentialPricingFactorDTO dto){
		super(dto);
	}
	
	@Override
	public Panel createContainerPanel() {
		Panel panel = null;
		if (pageModel.getSelectedItem() == null) {
			panel = new EmptyPanel(CONTAINER_PANEL_NAME);
		} else {
			panel = new DifferentialFactorPanel(CONTAINER_PANEL_NAME, getEditState(), pageModel,this);
		}
		panel.setOutputMarkupId(true);
		return panel;
	}
	@Override
	public Button[] createNavigationalButtons() {
		return new Button[] {createSaveButton("button1"), createCancelButton("button2") };
	}
	
	@Override
	public DefaultMaintenanceSelectionPanel createSelectionPanel() {
		return new AbstractTableMaintenanceSelectionPanel<DifferentialPricingFactorDTO>(SELECTION_PANEL_NAME,"Segment Name:",pageModel, this, 
				selectionForm, DifferentialPricingFactorDTO.class) {
					
				private static final long serialVersionUID = -2623730454856120194L;
				protected Button broadcastButton;
					
				@Override
				protected SRSAbstractChoiceRenderer getChoiceRenderer() {
					return new SRSAbstractChoiceRenderer() {

						private static final long serialVersionUID = 1L;
						public Object getDisplayValue(Object obj) {
							return ((SegmentNameDTO) obj).getSegmentName();
						}
						public String getIdValue(Object obj, int index) {
							return "" + ((SegmentNameDTO) obj).getId();
						}
					};
				}
				
				@Override
				public List<IGridColumn>  createTableFieldColumns() {
					List<IGridColumn> list = new ArrayList<IGridColumn>();
					list.add(new PropertyColumn(new Model("Id"), "Id", "Id").setInitialSize(30));
					list.add(new PropertyColumn(new Model("Segment Name"), "segmentNameObject.segmentName", null).setInitialSize(100) );
					list.add(new PropertyColumn(new Model("AQC Code"), "aqcValue", "aqcValue").setInitialSize(70) );
					list.add(new PropertyColumn(new Model("Product"), "aqcProductCode", null).setInitialSize(110) );
					list.add(new PropertyColumn(new Model("Premium Discount"), "premiumDiscountPercent", "premiumDiscountPercent")
					{
						//TODO: Converter must default to BigDecimal, without the currency sign. 
						// By default bigdecimal uses the current converter(SRSApplication - line 272
						//Wrote a default BigDecimal converter for normal display. 
						//Override the default bigdecimal converter using the currency with the normal bigdecimal converter.
						//This way the other bigdecimals will use the currency converter as per normal, these will be displayed as normal without currency sign
						private static final long serialVersionUID = 1L;
						@Override
						protected IConverter getConverter(Class arg0) {//MSK#Change:protected IConverter getConverter(Class<?> arg0) {
							return new BigDecimalConverter();
						}
					}.setInitialSize(110));
					list.add(new PropertyColumn(new Model("Deferred Comm"), "deferredCommPercent", "deferredCommPercent")
					{
						private static final long serialVersionUID = 1L;
						@Override
						protected IConverter getConverter(Class arg0) {//MSK#change protected IConverter getConverter(Class<?> arg0) {
							return new BigDecimalConverter();
						}
					}.setInitialSize(110));
					list.add(new PropertyColumn(new Model("Deferred Period Yrs"), "deferredPeriodInYrs", "deferredPeriodInYrs").setInitialSize(110));   
					list.add(new PropertyColumn(new Model("Monthly Prem Factor"), "monthPremiumAFactor","monthPremiumAFactor")
					{
						private static final long serialVersionUID = 1L;
						@Override
						protected IConverter getConverter(Class arg0) {//MSK#change:protected IConverter getConverter(Class<?> arg0) {
							return new BigDecimalConverter();
						}
					}.setInitialSize(110));
					
					list.add(new PropertyColumn(new Model("PCR Enhancement Rate"), "pcrEnhancementPercent","pcrEnhancementPercent")
					{
						private static final long serialVersionUID = 1L;
						@Override
						protected IConverter getConverter(Class arg0) {//MSK#Change :protected IConverter getConverter(Class<?> arg0) {
							return new BigDecimalConverter();
						}
					}.setInitialSize(110));
					list.add(new PropertyColumn(new Model("Max Upfront Comm Percent"), "maxUpfrontCommPercent","maxUpfrontCommPercent")
					{
						private static final long serialVersionUID = 1L;
						@Override
						protected IConverter getConverter(Class arg0) {//MSK#Change :protected IConverter getConverter(Class<?> arg0) {
							return new BigDecimalConverter();
						}
					}.setInitialSize(110));
					list.add(new PropertyColumn(new Model("Max Comm Term Yrs"), "maxCommTermYrs","maxCommTermYrs").setInitialSize(110));
					list.add(new PropertyColumn(new Model("Upfront Comm Clawback in Yrs"), "upfrontCommClawbackInYrs","upfrontCommClawbackInYrs").setInitialSize(110));
					list.add(new PropertyColumn(new Model("Pcr Clawback in Months"), "pcrClawbackInMonths","pcrClawbackInMonths").setInitialSize(110));
					list.add(new PropertyColumn(new Model("Start Date"), "startDate","startDate").setInitialSize(110));
					list.add(new PropertyColumn(new Model("End Date"), "endDate","endDate").setInitialSize(110));
					list.add(new PropertyColumn(new Model("Competitor Discount"), "competitorDiscount","competitorDiscount").setInitialSize(110));
					return list;
				}
				
				@Override
				public List<Object> filterTableData() {
					return (List)getSessionBean().findDiffFactorForSegment((SegmentNameDTO)selectedObject);
				}

				@Override
				public List getSelectionList() {
					// Return the segment names for the combo list
					return ((DifferentialFactorModel)pageModel).getFilterSegmentName();
				}
				
				@Override
				protected void createAdditionalControlButtons(int lastbuttonNr, List<Button> buttonList) {
					broadcastButton=createBroadcastButton("button"+ (++lastbuttonNr));
					buttonList.add(broadcastButton);
				}
				
				/**
				 * Create the add new button
				 * 
				 * @param id
				 * @return
				 */
				protected Button createBroadcastButton(String id) {
				
					
					Button button = new AjaxFallbackButton(id, enclosingForm) {

						private static final long serialVersionUID = -5330766713711809772L;

						@Override
						protected void onComponentTag(ComponentTag tag) {
							super.onComponentTag(tag);
							tag.getAttributes().put("value", "Broadcast");
							tag.getAttributes().put("type", "submit");
						}

						@Override
						protected void onSubmit(AjaxRequestTarget target, Form form) {
							getSessionBean().doFactorBroadcast();
							this.info("Broadcast sent");
							target.add(getFeedbackPanel());
						}
					};
					button.setOutputMarkupId(true);
					return button;
				}				
				
					
		};
	}	
	@Override
	public void doSave_onSubmit() {
			DifferentialPricingFactorDTO newDto = null;
			if (pageModel.getSelectedItem().getId() == null ) {
				newDto = getSessionBean().addDiffFactor(pageModel.getSelectedItem());
			}else{
				newDto = getSessionBean().updDiffFactor(pageModel.getSelectedItem());
			}
			invalidatePage();		
			//MSK#Change: this.info("Record was saved successfully");
			this.getSession().info("Record was saved successfully");
			setResponsePage(new DifferentialPricingPage(newDto));
	}

	@Override
	public Object initialisePageModel(Object obj, Object pageModelExtraValueObject) {
		DifferentialFactorModel object = new DifferentialFactorModel();
		IDifferentialFactorGuiController sessionBean;
		sessionBean = getSessionBean();
		object.setSelectionList(sessionBean.findAllFromDiffFactor());
		object.setFilterSegmentName(getSessionBean().filterAllSegmentName());
		object.setAvailableSegments(sessionBean.allAvailableSegmentName());
		object.setAllAvailableSegmentNameList(sessionBean.findAllSegmentNamesList());
		pageModel = object;

		return pageModel;
	}
	@Override
	public String getPageName() {
		return "Differential Factor";
	}
	
	@Override
	public boolean hasModifyAccess(Object callingObject) {
		boolean hasAccess = super.hasModifyAccess(callingObject);
		
		DateUtil dateUtil = DateUtil.getInstance();
		if (hasAccess && pageModel.getSelectedItem()!=null 
				&& dateUtil.compareDatePart(pageModel.getSelectedItem().getEndDate(), dateUtil.getMaxDatePart())!=0) {
			return false;
		}
		
		return hasAccess;
	}

	protected IDifferentialFactorGuiController getSessionBean() {
		if (guiController == null) {
			try {
				guiController = ServiceLocator.lookupService(IDifferentialFactorGuiController.class);
			} catch (NamingException namingErr) {
				logger.error(this.getPageName()
						+ " IDifferentialFactorGUIController can not be lookedup:"
						+ namingErr.getMessage());
				CommunicationException comm = new CommunicationException("IDifferentialFactorGUIController can not be looked up!");
				throw new CommunicationException(comm);
			} 
		}
		return guiController;
	}

}
