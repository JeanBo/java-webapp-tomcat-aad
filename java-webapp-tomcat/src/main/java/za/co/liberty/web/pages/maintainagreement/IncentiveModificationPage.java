package za.co.liberty.web.pages.maintainagreement;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.naming.NamingException;

import org.apache.commons.lang.SerializationUtils;
import org.apache.log4j.Logger;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AbstractAjaxTimerBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow.MaskType;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.time.Duration;

import za.co.liberty.agreement.common.AgreementObjectReference;
import za.co.liberty.agreement.common.enums.ProductKindEnumeration;
import za.co.liberty.business.guicontrollers.agreement.IAgreementGUIController;
import za.co.liberty.common.context.ComponentContextI;
import za.co.liberty.common.domain.ApplicationContext;
import za.co.liberty.common.enums.ComponentEnum;
import za.co.liberty.dto.agreement.BaseIntermediaryLevelIncentiveDTO;
import za.co.liberty.dto.agreement.ConsultantFinanceIncentiveDTO;
import za.co.liberty.dto.agreement.EstablishAllowanceDTO;
import za.co.liberty.dto.agreement.GEPIncentiveDTO;
import za.co.liberty.dto.agreement.IncentiveDetailDTO;
import za.co.liberty.dto.agreement.PBSIncentiveDTO;
import za.co.liberty.dto.agreement.SecretarialAllowanceIncentiveDTO;
import za.co.liberty.exceptions.SystemException;
import za.co.liberty.exceptions.data.ValidationException;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.helpers.lookup.ServiceLocator;
import za.co.liberty.srs.util.rating.TableProxy;
import za.co.liberty.srs.util.rating.calculateincentives.ApplicableProductViewRating;
import za.co.liberty.srs.util.rating.calculateincentives.BaseFeeViewRating;
import za.co.liberty.srs.util.rating.calculateincentives.CalculationDateViewRating;
import za.co.liberty.srs.util.rating.calculateincentives.ConglomerateIdentificationViewRating;
import za.co.liberty.srs.util.rating.calculateincentives.IncentiveRatingUtil;
import za.co.liberty.srs.util.rating.calculateincentives.ManpowerViewRating;
import za.co.liberty.srs.util.rating.calculateincentives.MeasureViewRating;
import za.co.liberty.srs.util.rating.calculateincentives.PercentageViewRating;
import za.co.liberty.srs.util.rating.calculateincentives.RateViewRating;
import za.co.liberty.srs.util.rating.calculateincentives.TargetViewRating;
import za.co.liberty.srs.util.rating.calculateincentives.YearsOfServiceViewRating;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.pages.BaseWindowPage;
import za.co.liberty.web.pages.maintainagreement.model.IncentiveModificationPopupModel;
import za.co.liberty.web.pages.maintainagreement.model.MaintainIncentivePanelModel;
import za.co.liberty.web.pages.panels.GUIFieldPanel;
import za.co.liberty.web.pages.panels.HelperPanel;
import za.co.liberty.web.wicket.markup.html.form.SRSDateField;
import za.co.liberty.web.wicket.modal.SRSModalWindow;

/**
 * Page for modifying the incentives on an agreement.   Must be used with SRSModalWindow
 * and uses the model {@linkplain IncentiveModificationPopupModel} to respond.
 * 
 * @author DZS2610
 *
 */
public class IncentiveModificationPage extends BaseWindowPage {
	
	private static final long serialVersionUID = 1L;
	
	private IncentiveModificationPopupModel pageModel;
	
	private EditStateType editState;
	
//	private IncentiveDetailDTO incentiveDetailMain;
	
	private IncentiveDetailDTO incentiveDetail;
	
	private boolean intialized;
	
	private SRSModalWindow parentWindow;
	
	private ModalWindow ratingWindow;
	
	private boolean doneClicked = false;
	
	private static final Logger logger = Logger.getLogger(IncentiveModificationPage.class);
	
	private MaintainIncentivePanelModel panelModel;
	
	private GUIFieldPanel startDate;
	
	private GUIFieldPanel endDate;
	
	private GUIFieldPanel gepMonths;
	
	private Rating_Table ratingTableSelected;
	
	private transient IAgreementGUIController agreementGUIController;
	
	private static String RATING_NAME_META_DATA = "RatingBPO";	
	
	private HashMap<Rating_Table, TableProxy> tablecache = new HashMap<Rating_Table, TableProxy>();
	
	final HashMap<Rating_Table, WebMarkupContainer> componentList = new HashMap<Rating_Table, WebMarkupContainer>();
	
	private transient ComponentContextI componentContext;
	private transient IncentiveRatingUtil incentiveRatingUtil;
	private enum Rating_Table{
		MANPOWER, MEASUREVIEW,RATE,APPLICABLE_PRODUCTS,YEARS_OF_SERVICE,PERCENTAGE,
		CONGLOMERATE_IDENTIFICATION, TARGET_VIEW, BASE_FEE,CALCULATION
	}		
	
//	/**
//	 * Made static as documentation stated to only have one instance
//	 */
//	private static final MapperIF mapper = new DozerBeanMapper();	
	
	/**
	 * Use this constructor if one has access to all the servicing types
	 * @param modalWindow
	 * @param agreementDTO
	 * @param type
	 * @param servicingTypes
	 */
	public IncentiveModificationPage(SRSModalWindow modalWindow, 
			EditStateType editState, IncentiveDetailDTO incentiveDetail,
			MaintainIncentivePanelModel panelModel){
//		this.incentiveDetailMain = incentiveDetail;		
		
		//clone the origional for modification
		this.incentiveDetail = (IncentiveDetailDTO) SerializationUtils.clone(incentiveDetail);
		
		// Init the pageModel and store
		pageModel = new IncentiveModificationPopupModel();
		pageModel.setSelectedItem(this.incentiveDetail);
		modalWindow.setSessionModelForPage(pageModel);
		
		this.editState = editState;
		this.parentWindow = modalWindow;
		this.panelModel = panelModel;
	}
	
	/**
	 * get ComponentContextI
	 * @return
	 */
	private ComponentContextI getComponentContextI(){
		if(componentContext == null){
			componentContext = getAgreementGUIController().getSRSContext();
		}
		return componentContext;		
	}
	
	/**
	 * get IncentiveRatingUtil
	 * @return
	 */
	private IncentiveRatingUtil getIncentiveRatingUtil(){
		if(incentiveRatingUtil == null){
			incentiveRatingUtil =
				new IncentiveRatingUtil(getComponentContextI(), RATING_NAME_META_DATA);
		}
		return incentiveRatingUtil;
	}

	@Override
	protected void onBeforeRender() {
		if(!intialized){
			intialized = true;
			init();
		}
		super.onBeforeRender();
	}



	/**
	 * Init all the page variables
	 *
	 */
	private void init(){
		this.add(new Label("title","Incentive Detail for " + incentiveDetail.getIncentiveProductKindType().getDescription()+ ""));	
		this.add(createIncentiveForm("incentiveForm"));
		this.add(ratingWindow = createIncentiveRatingDataWindow("ratingPopup"));
		doneClicked = false;
	}
	
	/**
	 * Create the incentive form
	 * @param id
	 * @return
	 */
	private Form createIncentiveForm(String id){
		Form form = new Form(id);
		RepeatingView leftPanel = new RepeatingView("leftPanel");
		RepeatingView rightPanel = new RepeatingView("rightPanel");
		RepeatingView bottomNotes = new RepeatingView("bottomNotes");
		form.add(leftPanel);
		form.add(rightPanel);
		form.add(bottomNotes);
		boolean viewStartDate = false;
		if(incentiveDetail instanceof GEPIncentiveDTO){
			viewStartDate = true;
		}
		
		boolean isEstablishViewOnly = (incentiveDetail instanceof EstablishAllowanceDTO && ((EstablishAllowanceDTO)incentiveDetail).getEndDate() != null)?true:false;
		
		if(incentiveDetail instanceof EstablishAllowanceDTO){
			viewStartDate = isEstablishViewOnly;
		}
		
		leftPanel.add(startDate = getDateComponent("startDate","startDate","Start Date",true,viewStartDate));	
		
		boolean endDateIsViewOnly = (incentiveDetail instanceof EstablishAllowanceDTO)? true:false;
		
		
		
		leftPanel.add(endDate = getDateComponent("endDate","endDate","End Date",false,endDateIsViewOnly));
		if(incentiveDetail instanceof ConsultantFinanceIncentiveDTO){
			endDate.setVisible(false);
		}	
		
		//add in all additional components
		leftPanel.add(createRetiredIndicator("retired"));
		leftPanel.add(createDisabledIndicator("disabled"));
		leftPanel.add(createManPowerIndicator("manPower"));
		createSecretarialAllowanceYearQualifiedIndicator(leftPanel);
		//gep fields
		leftPanel.add(gepMonths = createGEPMonths("gepMonths"));
		leftPanel.add(createGEPCurrentAmount("gepCurrentAmount"));		
		leftPanel.add(createGEPNewPayAmount("gepNewAmount"));
		leftPanel.add(createGEPPayed("gepPayIndicator"));
		bottomNotes.add(createGEPNote("gepNote"));
		
		//cons finance
		leftPanel.add(getConsFinanceEndDateComponent("consFinanceEndDate"));
		leftPanel.add(createConsFinanceOverrider("consFinanceOverrider"));
		leftPanel.add(createConsFinanceDPERator("dpeRator"));	
		
		//PBS
		leftPanel.add(createPBSValidationAmount("pbsValidationAmount"));
		leftPanel.add(createPBSPayCaseBonusField("pbsPayCaseBonusField"));	
		form.add(createDoneButton("okButton"));
		
		//Establishment Allowance Component
		leftPanel.add(createEstablishAllowanceAmount("establishAllowanceAmt",isEstablishViewOnly));
		leftPanel.add(createStopPayment("stopPayment",isEstablishViewOnly));
		leftPanel.add(createSettleFlag("settleFlag",isEstablishViewOnly));
		
//		add in all the popup rating views			
		form.add(getClickHereComponent("manPowerClick","manPowerPopup",Rating_Table.MANPOWER));
		form.add(getClickHereComponent("measureClick","measurePopup",Rating_Table.MEASUREVIEW));
		form.add(getClickHereComponent("rateClick","ratePopup",Rating_Table.RATE));
		form.add(getClickHereComponent("applicProductsClick","applicProductsPopup",Rating_Table.APPLICABLE_PRODUCTS));
		form.add(getClickHereComponent("yearsOfServiceClick","yearsOfServicePopup",Rating_Table.YEARS_OF_SERVICE));
		form.add(getClickHereComponent("percentageClick","percentagePopup",Rating_Table.PERCENTAGE));		
		form.add(getClickHereComponent("conglomClick","conglomPopup",Rating_Table.CONGLOMERATE_IDENTIFICATION));
		form.add(getClickHereComponent("targetViewClick","targetViewPopup",Rating_Table.TARGET_VIEW));
		form.add(getClickHereComponent("baseFeeClick","baseFeePopup",Rating_Table.BASE_FEE));
		form.add(getClickHereComponent("calcClick","calcPopup",Rating_Table.CALCULATION));
			
		//add update of all click here using a timer service
		Duration dur = Duration.milliseconds(100L);
		form.add(new AbstractAjaxTimerBehavior(dur){
			private static final long serialVersionUID = 1L;
			@Override
			protected void onTimer(AjaxRequestTarget target) {	
				//go through all rating data and set up for user				
				for(Rating_Table val : Rating_Table.values()){								
					WebMarkupContainer comp = componentList.get(val);
					if(comp == null){
						continue;
					}
					//get rating data and set visible if rating data is not null
					TableProxy tabledata = getTableProxy(val);					
					if(tabledata != null && tabledata.getRowCount() > 0){					
						comp.setVisible(true);
						target.add(comp);
					}
				}
				this.stop(target);
			}
						
		});
		
		return form;
	}
	
	/**
	 * Based on the incetive kind we will show or hide certain rating detail
	 * @return
	 */
	private boolean showRatingDetail(Rating_Table selectedView){
		if(incentiveDetail == null){
			return false;
		}
		int incentiveKind = incentiveDetail.getKind();
		if(selectedView == Rating_Table.MANPOWER){
			return true;			
		}else if(selectedView == Rating_Table.MEASUREVIEW){
			return true;
		}else if(selectedView == Rating_Table.RATE){
			return true;
		}else if(selectedView == Rating_Table.APPLICABLE_PRODUCTS){
			return true;
		}else if(selectedView == Rating_Table.YEARS_OF_SERVICE){
			return true;
		}else if(selectedView == Rating_Table.PERCENTAGE){
			return true;
		}else if(selectedView == Rating_Table.CONGLOMERATE_IDENTIFICATION){
			if(incentiveKind == ProductKindEnumeration._PRODUCTIONOVERRIDERPRODUCT){
				return true;
			}
		}else if(selectedView == Rating_Table.TARGET_VIEW){
			return true;
		}else if(selectedView == Rating_Table.BASE_FEE){
			return true;
		}else if(selectedView == Rating_Table.CALCULATION){
			return true;
		}
		return false;
	}
	
	/**
	 * Create the click here for the rating view tables
	 * @return
	 */
	private WebMarkupContainer getClickHereComponent(String placeHolderID,String componentid, final Rating_Table ratingTable) {
		final WebMarkupContainer ret = new WebMarkupContainer(placeHolderID);		
		Link but = new AjaxFallbackLink(componentid) {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				ratingTableSelected = ratingTable;
				ratingWindow.show(target);
			}
		};
		ret.add(but);
		//first we set not visible, will make visible when we gui is loaded and we ahve seen that there are values
		ret.setOutputMarkupId(true);
		ret.setOutputMarkupPlaceholderTag(true);
		ret.setVisible(false);
		componentList.put(ratingTable, ret);
		return ret;
	}
	
	/**
	 * Create the history popup
	 * @param id
	 * @return
	 */
	private ModalWindow createIncentiveRatingDataWindow(String id) {
		final ModalWindow window = new ModalWindow(id);		
		window.setTitle("Incentive Details");		
		// Create the page
		window.setPageCreator(new ModalWindow.PageCreator() {
			private static final long serialVersionUID = 1L;
			public Page createPage() {	
					//get rating data based on type selected										
					return new IncentiveRatingDetailsPopup(getTableProxy(ratingTableSelected));			
			}			
		});	
		// Initialise window settings
		window.setMinimalHeight(300);
		window.setInitialHeight(300);
		window.setMinimalWidth(800);
		window.setInitialWidth(800);
		window.setMaskType(MaskType.SEMI_TRANSPARENT);
		window.setCssClassName(ModalWindow.CSS_CLASS_GRAY);	
		window.setOutputMarkupId(true);
		window.setOutputMarkupPlaceholderTag(true);
//		window.setPageMapName("IncentiveRatingDataPageMap");
		return window;
	}	
	
	/**
	 * Get the rating data for the applicalbe type
	 * @param ratingTableSelected
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private TableProxy getTableProxy(Rating_Table ratingTableSelected){	
		TableProxy cache = tablecache.get(ratingTableSelected);
		if(cache != null){
			return cache;
		}
		ApplicationContext appContext = new ApplicationContext();
		AgreementObjectReference agmtReference = new AgreementObjectReference(ComponentEnum.AGREEMENT.getValue(),
				0,panelModel.getAgreementnumber());
		int agmtKind = (int)panelModel.getAgreementKind();
		int incentiveKind = (int)incentiveDetail.getKind();
		Date today = new Date();
		TableProxy tableData = null;
		if(ratingTableSelected == Rating_Table.MANPOWER){
			//get manpower values
			try {
				tableData =
					getIncentiveRatingUtil().getManPower(
						appContext,
						appContext.getEffectiveDate(),
						agmtReference,
						agmtKind,
						incentiveKind);
			} catch (SystemException e) {
				ManpowerViewRating manpowerRating =
					new ManpowerViewRating(getComponentContextI(), RATING_NAME_META_DATA);

				List manpowerViewKeyList = new ArrayList();
				manpowerViewKeyList.add(
					manpowerRating.createManpowerOIDQueryKey(
							appContext,
						new Long(Long.MIN_VALUE)));

				tableData = manpowerRating.getTable(appContext, manpowerViewKeyList);
			}
		}else if(ratingTableSelected == Rating_Table.YEARS_OF_SERVICE){
			try {
				tableData =
					getIncentiveRatingUtil().getYearsOfService(
						appContext,
						today,
						agmtReference,
						agmtKind,
						incentiveKind);

			} catch (SystemException e) {
				YearsOfServiceViewRating yosRating =
					new YearsOfServiceViewRating(
						componentContext,
						RATING_NAME_META_DATA);

				List yosViewKeyList = new ArrayList();
				yosViewKeyList.add(
					yosRating.createYearsOfServiceQueryKey(
						appContext,
						new Long(Long.MIN_VALUE)));

				tableData = yosRating.getTable(appContext, yosViewKeyList);

			}			
		}else if(ratingTableSelected == Rating_Table.MEASUREVIEW){
			try {
				tableData =
					getIncentiveRatingUtil().getMeasure(
						appContext,
						today,
						agmtReference,
						agmtKind,
						incentiveKind);
			} catch (SystemException e) {
				MeasureViewRating measRating =
					new MeasureViewRating(getComponentContextI(), RATING_NAME_META_DATA);
				List measViewKeyList = new ArrayList();
				measViewKeyList.add(
					measRating.createMeasureOIDQueryKey(
						appContext,
						new Long(Long.MIN_VALUE)));

				tableData = measRating.getTable(appContext, measViewKeyList);
			}
		}else if(ratingTableSelected == Rating_Table.RATE){
			try {
				tableData =
					getIncentiveRatingUtil().getRate(
						appContext,
						today,
						agmtReference,
						agmtKind,
						incentiveKind);
			} catch (SystemException e) {
				RateViewRating rateRating =
					new RateViewRating(getComponentContextI(), RATING_NAME_META_DATA);

				List rateViewKeyList = new ArrayList();
				rateViewKeyList.add(
					rateRating.createRateOIDQueryKey(
						appContext,
						new Long(Long.MIN_VALUE)));

				tableData = rateRating.getTable(appContext, rateViewKeyList);
			}
		}else if(ratingTableSelected == Rating_Table.PERCENTAGE){
			try {
				tableData =
					getIncentiveRatingUtil().getPercentage(
						appContext,
						today,
						agmtReference,
						agmtKind,
						incentiveKind);
			} catch (SystemException e) {
				PercentageViewRating percentageRating =
					new PercentageViewRating(
						componentContext,
						RATING_NAME_META_DATA);

				List percentageViewKeyList = new ArrayList();
				percentageViewKeyList.add(
					percentageRating.createPercentageVersionOIDQueryKey(
						appContext,
						new Long(Long.MIN_VALUE)));

				tableData = percentageRating.getTable(appContext, percentageViewKeyList);
			}
		}else if(ratingTableSelected == Rating_Table.APPLICABLE_PRODUCTS){
			try {
				tableData =
					getIncentiveRatingUtil().getApplicableProduct(
						appContext,
						today,
						agmtReference,
						agmtKind,
						incentiveKind);
			} catch (SystemException e) {
				ApplicableProductViewRating applicableRating =
					new ApplicableProductViewRating(
						componentContext,
						RATING_NAME_META_DATA);

				List applicableViewKeyList = new ArrayList();
				applicableViewKeyList.add(
					applicableRating.createApplicableProductQueryKey(
						appContext,
						new Long(Long.MIN_VALUE)));

				tableData =
					applicableRating.getTable(appContext, applicableViewKeyList);
			}
		}else if(ratingTableSelected == Rating_Table.CONGLOMERATE_IDENTIFICATION){			
			try {
				tableData =
					getIncentiveRatingUtil().getConglomerateIdentification(
						appContext,
						today,
						agmtReference,
						agmtKind,
						incentiveKind);

			} catch (SystemException e) {

				ConglomerateIdentificationViewRating conglRating =
					new ConglomerateIdentificationViewRating(
							getComponentContextI(),
						RATING_NAME_META_DATA);

				List conglViewKeyList = new ArrayList();
				conglViewKeyList.add(
					conglRating.createConglomerateIdentificationOIDQueryKey(
						appContext,
						new Long(Long.MIN_VALUE)));

				tableData = conglRating.getTable(appContext, conglViewKeyList);

			}
		}else if(ratingTableSelected == Rating_Table.TARGET_VIEW){
			try {
				tableData =
					getIncentiveRatingUtil().getTarget(
						appContext,
						today,
						agmtReference,
						agmtKind,
						incentiveKind);

			} catch (SystemException e) {
				TargetViewRating targetRating =
					new TargetViewRating(getComponentContextI(), RATING_NAME_META_DATA);

				List targetViewKeyList = new ArrayList();
				targetViewKeyList.add(
					targetRating.createTargetVersionOIDQueryKey(
						appContext,
						new Long(Long.MIN_VALUE)));

				tableData = targetRating.getTable(appContext, targetViewKeyList);
			}
			
		}else if(ratingTableSelected == Rating_Table.BASE_FEE){
			try {

				tableData =
					getIncentiveRatingUtil().getBaseFee(
						appContext,
						today,
						agmtReference,
						agmtKind,
						incentiveKind);
			} catch (SystemException e) {
				BaseFeeViewRating baseRating =
					new BaseFeeViewRating(getComponentContextI(), RATING_NAME_META_DATA);

				List baseViewKeyList = new ArrayList();
				baseViewKeyList.add(
					baseRating.createBaseFeeOIDQueryKey(
						appContext,
						new Long(Long.MIN_VALUE)));

				tableData = baseRating.getTable(appContext, baseViewKeyList);
			}
		}else if(ratingTableSelected == Rating_Table.CALCULATION){
			CalculationDateViewRating calcDateRating =
				new CalculationDateViewRating(
						getComponentContextI(),
					RATING_NAME_META_DATA);
			List calcDateViewKeyList = new ArrayList();
			calcDateViewKeyList.add(
				calcDateRating.createCalculationDateVersionOIDQueryKey(
					appContext,
					new Long(Long.MIN_VALUE)));
			tableData = calcDateRating.getTable(appContext, calcDateViewKeyList);
		}
		
		if(tableData != null){
			tablecache.put(ratingTableSelected, tableData);
		}
		return tableData;
	}
	
	/**
	 * Create the manpowerIndicator indicator -- Will not be visible if the object in the model does not contain the value
	 * @return
	 */
	private GUIFieldPanel createManPowerIndicator(String id){		
		boolean visible = true;		
		boolean viewOnly = true;//currently only viewable
		if(!(incentiveDetail instanceof BaseIntermediaryLevelIncentiveDTO)){
			//does not have the field so disable
			visible = false;		
		}
		IModel<String> model = new PropertyModel<String>(incentiveDetail,"manpowerIndicator");
		Component comp;
		if(editState.isViewOnly() || viewOnly){
			comp = HelperPanel.getInstance("panel",new Label("value",model));
		}else{
			DropDownChoice<String> dropDown = new DropDownChoice<String>("value",model,panelModel.getAllowedManPowerValues());
			addAjaxModelUpdate(dropDown);
			comp = HelperPanel.getInstance("panel", dropDown); 
		}				
		return createGUIFieldIndicator(id,"Manpower",comp,visible,visible);		
	}
	
	/**
	 * Create the new amount to pay
	 * @return
	 */
	private GUIFieldPanel createGEPNewPayAmount(String id){		
		boolean visible = true;
		IModel<Long> model = new PropertyModel<Long>(incentiveDetail,"gepAmount");
		if(panelModel.getAllowedGEPamounts() == null){
			panelModel.setAllowedGEPamounts(new ArrayList<Long>(0));
		}
		Component comp;
		if(editState.isViewOnly()){
			comp = HelperPanel.getInstance("panel",new Label("value",model));
		}else{
			DropDownChoice<Long> dropDown = new DropDownChoice<Long>("value",model,panelModel.getAllowedGEPamounts());
			addAjaxModelUpdate(dropDown);
			comp = HelperPanel.getInstance("panel", dropDown);
		}
		if(!(incentiveDetail instanceof GEPIncentiveDTO)){
			//does not have the field so disable
			visible = false;			
		}
		return createGUIFieldIndicator(id,"New Amount",comp,visible,visible);		
	}
	
	/**
	 * Create the disabled indicator -- Will not be visible if the object in the model does not contain the value
	 * @return
	 */
	private GUIFieldPanel createDisabledIndicator(String id){			
		boolean visible = true;		
		boolean viewOnly = false;//true;//currently only viewable
		if(!(incentiveDetail instanceof BaseIntermediaryLevelIncentiveDTO)){
			//does not have the field so disable
			visible = false;		
		}
		IModel<Boolean> model = new PropertyModel<Boolean>(incentiveDetail,"disabled");
		Component comp;
		if(editState.isViewOnly() || viewOnly){
			comp = HelperPanel.getInstance("panel",new Label("value",model));
		}else{
			CheckBox retiredCheck = new CheckBox("value", model);
			addAjaxModelUpdate(retiredCheck);
			comp = HelperPanel.getInstance("panel", retiredCheck); 
		}				
		return createGUIFieldIndicator(id,"Disabled",comp,visible,visible);		
	}
	
	/**
	 * Create the retired indicator -- Will not be visible if the object in the model does not contain the value
	 * @return
	 */
	private GUIFieldPanel createRetiredIndicator(String id){
		boolean visible = true;		
		boolean viewOnly = false;//true;//currently only viewable
		if(!(incentiveDetail instanceof BaseIntermediaryLevelIncentiveDTO)){
			//does not have the field so disable
			visible = false;		
		}
		IModel<Boolean> model = new PropertyModel<Boolean>(incentiveDetail,"retired");
		Component comp;
		if(editState.isViewOnly() || viewOnly){
			comp = HelperPanel.getInstance("panel",new Label("value",model));
		}else{
			CheckBox retiredCheck = new CheckBox("value", model);
			addAjaxModelUpdate(retiredCheck);
			comp = HelperPanel.getInstance("panel", retiredCheck); 
		}				
		return createGUIFieldIndicator(id,"Retired",comp,visible,visible);		
	}
	
	/**
	 * Create the PBS Pay Case Bonus Field
	 * @return
	 */
	private GUIFieldPanel createPBSPayCaseBonusField(String id){			
		boolean visible = true;
		IModel<Boolean> model = new PropertyModel<Boolean>(incentiveDetail,"payCaseCountBonus");
		if(!(incentiveDetail instanceof PBSIncentiveDTO)){
			//does not have the field so disable
			visible = false;
			model = new Model<Boolean>();
		}
		Component comp;
		if(editState.isViewOnly()){
			comp = HelperPanel.getInstance("panel",new Label("value",model));
		}else{		
			CheckBox retiredCheck = new CheckBox("value", model);
			addAjaxModelUpdate(retiredCheck);
			comp = HelperPanel.getInstance("panel", retiredCheck); 
		}
		return createGUIFieldIndicator(id,"Pay Case Count Bonus",comp,visible,false);		
	}
	
	/**
	 * Create the gep months field
	 * @return
	 */
	private GUIFieldPanel createGEPMonths(String id){			
		boolean visible = true;
		IModel<Integer> model = new PropertyModel<Integer>(incentiveDetail,"gepMonths");
		if(!(incentiveDetail instanceof GEPIncentiveDTO)){
			//does not have the field so disable
			visible = false;
			model = new Model<Integer>();
		}
		Component comp;
		if(editState.isViewOnly()){
			comp = HelperPanel.getInstance("panel",new Label("value",model));
		}else{		
			TextField<Integer> gep = new TextField<Integer>("value", model);
			gep.add(new AjaxFormComponentUpdatingBehavior("change"){			
				private static final long serialVersionUID = 1L;
	
				@Override
				protected void onUpdate(AjaxRequestTarget target) {
					updateDatefields(target);				
				}			
			});		
			//also onkeyup
			gep.add(new AjaxFormComponentUpdatingBehavior("keyup"){
				private static final long serialVersionUID = 1L;
				@Override
				protected void onUpdate(AjaxRequestTarget target) {
					updateDatefields(target);				
				}			
			});
			comp = HelperPanel.getInstance("panel", gep); 
		}
		return createGUIFieldIndicator(id,"Months",comp,visible,visible);		
	}
	
	/**
	 * Create the PBS Validation Amount field
	 * @return
	 */
	private GUIFieldPanel createPBSValidationAmount(String id){			
		boolean visible = true;
		IModel<BigDecimal> model = new PropertyModel<BigDecimal>(incentiveDetail,"pbsValidationAmt");
		if(!(incentiveDetail instanceof PBSIncentiveDTO)){
			//does not have the field so disable
			visible = false;
			model = new Model<BigDecimal>();
		}
		Component comp;
		if(editState.isViewOnly()){
			comp = HelperPanel.getInstance("panel",new Label("value",model));
		}else{	
			TextField<BigDecimal> gep = new TextField<BigDecimal>("value", model);		
			addAjaxModelUpdate(gep);
			comp = HelperPanel.getInstance("panel", gep); 
		}
		return createGUIFieldIndicator(id,"PBS Validation Amount",comp,visible,true);		
	}
	
	
	/**
	 * Create the Cons finance overrider field
	 * @return
	 */
	private GUIFieldPanel createConsFinanceOverrider(String id){			
		boolean visible = true;
		IModel<String> model = new PropertyModel<String>(incentiveDetail,"overrideVariablePercentageAsMarkedUpString");
		if(!(incentiveDetail instanceof ConsultantFinanceIncentiveDTO)){
			//does not have the field so disable
			visible = false;
			model = new Model<String>();
		}
		Component comp;
		if(editState.isViewOnly()){
			comp = HelperPanel.getInstance("panel",new Label("value",model));
		}else{
			TextField<String> gep = new TextField<String>("value", model);		
			addAjaxModelUpdate(gep);
			comp = HelperPanel.getInstance("panel", gep); 
		}
		return createGUIFieldIndicator(id,"Override Variable Percentage ",comp,visible,false);		
	}
	
	/**
	 * Create the date components
	 * @param id
	 * @return
	 */
	private GUIFieldPanel getConsFinanceEndDateComponent(String id){
		boolean visible = true;		
		if(!(incentiveDetail instanceof ConsultantFinanceIncentiveDTO)){
			//does not have the field so disable
			visible = false;			
		}
		GUIFieldPanel field = getDateComponent("incentiveAdvanceEndDate","incentiveAdvanceEndDate","Consultant's Finance Advance End Date",true,false);	
		field.setOutputMarkupId(true);
		field.setOutputMarkupPlaceholderTag(true);
		field.setVisible(visible);
		return field;
	}
	
	/**
	 * Create the DPE Rator field
	 * @return
	 */
	private GUIFieldPanel createConsFinanceDPERator(String id){			
		boolean visible = true;
		IModel<String> model = new PropertyModel<String>(incentiveDetail,"dpeRatorForAgreement.toString");
		if(!(incentiveDetail instanceof ConsultantFinanceIncentiveDTO)){
			//does not have the field so disable
			visible = false;
			model = new Model<String>();
		}
		Label gep = new Label("value", model);		
		HelperPanel panel = HelperPanel.getInstance("panel", gep); 
		return createGUIFieldIndicator(id,"Servicing Rator",panel,visible,visible);		
	}
	
	
	
	/**
	 * Update the GEP 
	 * @param target
	 */
	private void updateGEPDateMonths(AjaxRequestTarget target){		
		target.add(gepMonths);	
	}
	
	/**
	 * Update the date fields
	 * @param target
	 */
	private void updateDatefields(AjaxRequestTarget target){
		target.add(startDate);
		target.add(endDate);		
	}
	
	/**
	 * Create the GEPPayed field 
	 * @return
	 */
	private GUIFieldPanel createGEPPayed(String id){		
		boolean visible = true;
		IModel<Boolean> model = new PropertyModel<Boolean>(incentiveDetail,"doNotPayInd");
		if(!(incentiveDetail instanceof GEPIncentiveDTO)){
			//does not have the field so disable
			visible = false;			
		}
		Component comp;
		if(editState.isViewOnly()){
			comp = HelperPanel.getInstance("panel",new Label("value",model));
		}else{
			CheckBox check = new CheckBox("value",model);		
			addAjaxModelUpdate(check);
			comp = HelperPanel.getInstance("panel", check); 	
		}
		return createGUIFieldIndicator(id,"Do Not Pay GEP",comp,visible,visible);		
	}
	
	/**
	 * Create the first note field
	 * @return
	 */
	private GUIFieldPanel createGEPNote(String id){		
		Label label = new Label("label", "<b>Note: GEP is a monthly calculation. " +
				"GEP will not be calculated for Previous months.</b><br/>" +
				"<b>Note: This calculation does not include manual GEP payments.</b>");
		label.setEscapeModelStrings(false);
		label.add(new AttributeModifier("align","left"));
		boolean visible = true;
		if(!(incentiveDetail instanceof GEPIncentiveDTO)){
			//does not have the field so disable
			visible = false;
		}
		Label gep = new Label("value", "");		
		HelperPanel panel = HelperPanel.getInstance("panel", gep); 
		panel.setOutputMarkupId(true);
		panel.setVisible(false);
		return createGUIFieldIndicator(id,label,panel,visible,false);		
	}
	
	/**
	 * Create the gep current Amount field
	 * @return
	 */
	private GUIFieldPanel createGEPCurrentAmount(String id){			
		boolean visible = true;
		IModel<String> model = new PropertyModel<String>(incentiveDetail,"gepAmountExist");
		if(!(incentiveDetail instanceof GEPIncentiveDTO)){
			//does not have the field so disable
			visible = false;
			model = new Model<String>();
		}
		Label gep = new Label("value", model);		
		HelperPanel panel = HelperPanel.getInstance("panel", gep); 
		return createGUIFieldIndicator(id,"Current Amount",panel,visible,visible);		
	}
	
	
	/**
	 * Create the SecretarialAllowanceYearQualified indicators
	 * @return
	 */
	private void createSecretarialAllowanceYearQualifiedIndicator(RepeatingView repeatingview){			
		boolean visible = true;		
//		we need a Repeater panel with multiple GUi fields
		//RepeatingView repeater = new RepeatingView("panel");
		if(!(incentiveDetail instanceof SecretarialAllowanceIncentiveDTO)){
			//does not have the field so disable
			visible = false;			
		}else{			
			//add label
			Label label = new Label("label","Secretarial Allowance Year Qualified:");
			label.add(new AttributeModifier("rowspan","4"));
			Label label2 = new Label("panel","");
			label2.setVisible(false);		
			label2.setOutputMarkupId(true);
			repeatingview.add(createGUIFieldIndicator("secAllYear",label,label2,visible,visible));		
			
			//now add the checkboxes as individual rows
			final SecretarialAllowanceIncentiveDTO sec = (SecretarialAllowanceIncentiveDTO) incentiveDetail;
			if(sec.getSelectedQualifiedList() == null){
				sec.setSelectedQualifiedList(new ArrayList<Integer>());
			}			
			int year = Calendar.getInstance().get(Calendar.YEAR);
			for (int y = year - 2; y <= year; y++) {
				//we add a checkbox for each year -- the checkbox model will be altered to cater for string selection
				final int yearFinal = y;
				final Model checkModel = new Model<Boolean>(){
					private static final long serialVersionUID = 1L;
					@Override
					public Boolean getObject() {
						//we use the year and look at current list -- if a year exists then checked
						for(Integer all : sec.getSelectedQualifiedList()){							
							if(all != null && all == yearFinal){
								//value exists so true
								return true;
							}
						}
						return false;
					}					
					@Override
					public void setObject(Boolean object) {					
						//we add the year to the list
						sec.getSelectedQualifiedList().add(yearFinal);						
					}				
				};
				Component comp;
				if(editState.isViewOnly()){
					comp = HelperPanel.getInstance("panel",new Label("value",checkModel));
				}else{
					CheckBox secCheck = new CheckBox("value",checkModel);
					addAjaxModelUpdate(secCheck);
					comp = HelperPanel.getInstance("panel", secCheck);
				}
				Label yearLabel = new Label("label","" + yearFinal);				
				//repeater.add(createGUIFieldIndicator("" + yearFinal,label,panel,true,false));								
				repeatingview.add(createGUIFieldIndicator("box" + y,yearLabel,comp,visible,visible));
				
			}
		}	
		
		
	}
	
	/**
	 * 
	 *update comp model via ajax
	 */
	private void addAjaxModelUpdate(FormComponent comp){
		comp.add(new AjaxFormComponentUpdatingBehavior("change"){
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				//do nothing, just update model via ajax				
			}			
		});
		//also onclick
		comp.add(new AjaxFormComponentUpdatingBehavior("click"){
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				//do nothing, just update model via ajax				
			}			
		});
		//also onkeyup
		comp.add(new AjaxFormComponentUpdatingBehavior("keyup"){
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				//do nothing, just update model via ajax				
			}			
		});
	}
	
	/**
	 * Create the a gui field
	 * @return
	 */
	private GUIFieldPanel createGUIFieldIndicator(String id,String title,Component component, boolean visible,boolean required){
		Label label = new Label("label","<b>"+title.trim()+((required)? "*" : " ")+":</b> ");
		label.setEscapeModelStrings(false);	
		return createGUIFieldIndicator(id, label, component, visible, required);
	}
	
	/**
	 * Create the a gui field
	 * @return
	 */
	private GUIFieldPanel createGUIFieldIndicator(String id,Label title,Component component, boolean visible,boolean required){				
		GUIFieldPanel guiField = new GUIFieldPanel(id,title,component);		
		guiField.setOutputMarkupId(true);
		guiField.setOutputMarkupPlaceholderTag(true);	
		guiField.setVisible(visible);
		if(component instanceof FormComponent){
			((FormComponent)component).setRequired(required);
		}
		return guiField;
	}
	
	/**
	 * Create the done button
	 * @param id
	 * @return
	 */
	private Button createDoneButton(String id){	
		final Button button = new Button(id);
		button.add(new AjaxFormComponentUpdatingBehavior("click") {
			private static final long serialVersionUID = 0L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				if (getEditState().isViewOnly()) {
					parentWindow.close(target);
					//only close when in view state
					return;
				}
				//first we validate the data
				try{				
					//validate
					getAgreementGUIController().validateIncentiveDetail(incentiveDetail, panelModel.getAgreementStartDate(), panelModel.getAgreementStatusString());			
				}catch(ValidationException ex){
					for(String error : ex.getErrorMessages()){
						IncentiveModificationPage.this.error(error);								
					}
					target.add(getFeedBackPanel());
					return;
				}
				
				pageModel.setModalWizardSuccess(true);
				parentWindow.setSessionModelForPage(pageModel);
				
				// Copy logic moved to main page
				parentWindow.close(target);
			}
		});		
		if (getEditState().isViewOnly()) {
			//change name to close
			button.add(new AttributeModifier("value","Close"));
		}
		button.setOutputMarkupId(true);
		button.setOutputMarkupPlaceholderTag(true);
		return button;		
	}
	
	/**
	 * Create the date components
	 * @param id
	 * @return
	 */
	private GUIFieldPanel getDateComponent(String id, String property,String labelValue, boolean required, boolean viewOnly){		
		HelperPanel comp = null;
		if(editState.isViewOnly() || viewOnly){
			comp = HelperPanel.getInstance("panel",new Label("value",new PropertyModel<Date>(incentiveDetail,property)));
		}else{
			SRSDateField textField = new SRSDateField("value",new PropertyModel<Date>(incentiveDetail,property));			
			textField.add(new AjaxFormComponentUpdatingBehavior("change"){				
				private static final long serialVersionUID = 1L;
				@Override
				protected void onUpdate(AjaxRequestTarget target) {
					if(incentiveDetail instanceof GEPIncentiveDTO){
						updateGEPDateMonths(target);		
					}
				}			
			});	
			textField.add(textField.newDatePicker());
			comp = HelperPanel.getInstance("panel", textField,true);
			textField.setRequired(required);			
		}
		Label label = new Label("label","<b>"+labelValue.trim()+((required)? "*" : " ")+":</b> ");
		label.setEscapeModelStrings(false);				
		GUIFieldPanel guiField = new GUIFieldPanel(id,label,comp);		
		guiField.setOutputMarkupId(true);
		guiField.setOutputMarkupPlaceholderTag(true);		
		return guiField;		
	}
	
	public EditStateType getEditState() {
		return editState;
	}		
	
	@Override
	public String getPageName() {		
		return "Incentive Details";
	}
	
	/**
	 * *********************************************Added for Liberty @ Work Establishment Allowance**********************************************
	 */
		
	/**
	 * Create the establishmentAllowanceAmount field
	 * @return
	 */
	private GUIFieldPanel createEstablishAllowanceAmount(String id,boolean isEstablishViewOnly){
		
		boolean visible = true;
		IModel<BigDecimal> model = new PropertyModel<BigDecimal>(incentiveDetail,"establishAllowanceAmt");
		if(!(incentiveDetail instanceof EstablishAllowanceDTO)){
			//does not have the field so disable
			visible = false;
			model = new Model<BigDecimal>();
		}
		Component comp;
		if(editState.isViewOnly() || isEstablishViewOnly){
			comp = HelperPanel.getInstance("panel",new Label("value",model));
		}else{	
			TextField<BigDecimal> establishAmt = new TextField<BigDecimal>("value", model);		
			addAjaxModelUpdate(establishAmt);			
			comp = HelperPanel.getInstance("panel", establishAmt); 
		}
		return createGUIFieldIndicator(id,"Amount",comp,visible,true);		
	}
	
	/**
	 * Create the Stop Payment field
	 * @return
	 */
	private GUIFieldPanel createStopPayment(String id,boolean isEstablishViewOnly){
		
		boolean visible = true;
		IModel<Boolean> model = new PropertyModel<Boolean>(incentiveDetail,"stopPayment");
		if(!(incentiveDetail instanceof EstablishAllowanceDTO)){
			//does not have the field so disable
			visible = false;
			model = new Model<Boolean>();
		}
		Component comp;
		if(editState.isViewOnly() || isEstablishViewOnly){
			comp = HelperPanel.getInstance("panel",new Label("value",model));
		}else{	
			CheckBox stopPay = new CheckBox("value", model);		
			addAjaxModelUpdate(stopPay);
			comp = HelperPanel.getInstance("panel", stopPay); 
		}
		return createGUIFieldIndicator(id,"Stop Payment",comp,visible,false);		
	}
	
	/**
	 * Create the Settle field
	 * @return
	 */
	private GUIFieldPanel createSettleFlag(String id,boolean isEstablishViewOnly){
		
		boolean visible = true;
		IModel<Boolean> model = new PropertyModel<Boolean>(incentiveDetail,"settleFlag");
		if(!(incentiveDetail instanceof EstablishAllowanceDTO)){
			//does not have the field so disable
			visible = false;
			model = new Model<Boolean>();
		}
		Component comp;
		if(editState.isViewOnly()|| isEstablishViewOnly){
			comp = HelperPanel.getInstance("panel",new Label("value",model));
		}else{	
			CheckBox settle = new CheckBox("value", model);		
			addAjaxModelUpdate(settle);
			comp = HelperPanel.getInstance("panel", settle); 
		}
		return createGUIFieldIndicator(id,"Settle",comp,visible,false);		
	}
	
	/**
	 * *********************************************End - Establishment Allowance**********************************************
	 */
	
	
	/**
	 * Get the agreement manager
	 * @return
	 */
	private IAgreementGUIController getAgreementGUIController() {
		if(agreementGUIController == null){
			try{
				agreementGUIController = ServiceLocator.lookupService(IAgreementGUIController.class);
			} catch (NamingException e) {
					throw new CommunicationException(e);
			}	
		}
		return agreementGUIController;
	}

	public boolean isDoneClicked() {
		return doneClicked;
	}

}
