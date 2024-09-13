package za.co.liberty.web.pages.businesscard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.extensions.model.AbstractCheckBoxModel;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidator;

import za.co.liberty.business.guicontrollers.businesscard.IBusinessCardGuiController;
import za.co.liberty.dto.businesscard.PostalServiceLocationsDTO;
import za.co.liberty.dto.contracting.ResultPartyDTO;
import za.co.liberty.dto.databaseenum.LanguagePreferenceDBEnumDTO;
import za.co.liberty.dto.databaseenum.rating.PostalProvinceDBEnumDTO;
import za.co.liberty.dto.databaseenum.rating.PostalRegionDBEnumDTO;
import za.co.liberty.dto.userprofiles.ISessionUserProfile;
import za.co.liberty.exceptions.data.ValidationException;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.helpers.lookup.ServiceLocator;
import za.co.liberty.helpers.util.CompareUtil;
import za.co.liberty.persistence.rating.entity.fastlane.PostalAreaFLO;
import za.co.liberty.persistence.rating.entity.fastlane.PostalPostalCodeFLO;
import za.co.liberty.srs.type.SRSType;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.pages.businesscard.model.BusinessCardPageModel;
import za.co.liberty.web.pages.businesscard.model.MaintainBusinessCardPanelModel;
import za.co.liberty.web.pages.panels.BasePanel;
import za.co.liberty.web.pages.panels.HelperPanel;
import za.co.liberty.web.system.SRSAuthWebSession;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGrid;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataProviderAdapter;
import za.co.liberty.web.wicket.markup.repeater.data.SortableListDataProvider;

import com.inmethod.grid.IGridColumn;
import com.inmethod.grid.SizeUnit;
import com.inmethod.grid.column.AbstractColumn;
import com.inmethod.grid.column.PropertyColumn;

/**
 * Panel for configuring find an FA details for an intermediary.
 * 
 * @author jzb0608 2015/09/11
 *
 */
public class BusinessCardFindAnFADetailsPanel extends BasePanel{ 


	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(BusinessCardFindAnFADetailsPanel.class);

	ResultPartyDTO partyInContext;
	
	private FeedbackPanel feedBackPanel;
	
	private transient IBusinessCardGuiController businessCardGuiController;
		
	private BusinessCardPageModel pageModel;
	
	private boolean initialised;
	
	private Page parentPage;
		
	private Long paPartyOID;
	
	private ISessionUserProfile loggedInUser = SRSAuthWebSession.get().getSessionUser();
	
	private DropDownChoice provinceChoice;
	private DropDownChoice regionChoice;
	private DropDownChoice postalAreaChoice;
	
	private WebMarkupContainer postalPostalCodeList;
	private SRSDataGrid serviceLocationList;
	
	private WebMarkupContainer addRegionButton;
	private WebMarkupContainer addAreaButton;
	private WebMarkupContainer addLocationContainer;
	
	public BusinessCardFindAnFADetailsPanel(String id, BusinessCardPageModel pageModel , 
			EditStateType editState, FeedbackPanel feedBackPanel, Page parentPage) {
		super(id, editState,parentPage);
		this.pageModel = pageModel;		
		this.feedBackPanel = feedBackPanel;
		this.parentPage = parentPage;
	}
	
	/**
	 * Load the components on the page on first render, 
	 * so that the components are only generated when the page is displayed 
	 */
	@Override
	protected void onBeforeRender() {
		logger.info("onRender");
		if(!initialised) {			
			initialised=true;	
			add(new FindAnFAForm("findAnFAForm"));
		}
		
		super.onBeforeRender();
	}
	
	
	/**
	 * Form used for the panel so we can add validations and on submit method calls
	 * @author DZS2610
	 *
	 */
	public class FindAnFAForm extends Form {
		
		private static final long serialVersionUID = 1L;
		
		public FindAnFAForm(String id) {
			super(id);	
			
			/*
			 * Broker
			 * http://localhost:9081/SRSAppWeb/businesscard/BusinessCardDetailsPage?consultantCode=0970095560094&signedOnUACFID=SRS1802
			 * 
			 * Agent
			 * http://localhost:9081/SRSAppWeb/businesscard/BusinessCardDetailsPage?consultantCode=0400072000451&signedOnUACFID=SRS1802
			 * 
			 */
			setOutputMarkupId(true);
			add(createMessageContainer("messageContainer"));
			add(createRequiredQualification("requiredQualification"));
			add(createContactableAdvisor("contactableThroughService"));
			add(createSpokenLanguages("spokenLanguagesRow"));
			add(serviceLocationList=createServicedLocationList("serviceLocationList"));
			
			WebMarkupContainer addLocationContainer = null;	
			addLocationContainer= new WebMarkupContainer("addLocationContainer");			
			add(addLocationContainer);
			
			if (getEditState()==EditStateType.MODIFY && isEditingEnabled()) {
				addLocationContainer.add(provinceChoice=createProvinceList("provinceList"));
				addLocationContainer.add(regionChoice=createRegionList("regionList"));
				addLocationContainer.add(postalAreaChoice=createPostalAreaList("postalAreaList"));
				addLocationContainer.add(postalPostalCodeList=createPostalPostalCodeList("postalCodeList"));
				addLocationContainer.add(addRegionButton=createAddRegionButton("addRegionButton"));
				addLocationContainer.add(addAreaButton=createAddAreaButton("addAreaButton", FindAnFAForm.this));
			} else {
				addLocationContainer.setVisible(false);
			}
			
		}
		
	}	
	
	/**
	 * Show a warning message for parties with pays To roles.
	 * 
	 * @param id
	 * @return
	 */
	public WebMarkupContainer createMessageContainer(String id) {
		WebMarkupContainer messageContainer = new WebMarkupContainer(id);	
		messageContainer.add(new Label("warningMessage", "Tab disabled - " 
				+ pageModel.getMaintainBusinessCardPanelModel().getFindAnFAWarningMessage()));
		messageContainer.setVisible(pageModel.getMaintainBusinessCardPanelModel().getFindAnFAWarningMessage()!=null);
		return messageContainer;
	}
	
	private CheckBox createRequiredQualification(String id) {
		CheckBox box = new CheckBox(id, new PropertyModel<Boolean>(
				pageModel.getMaintainBusinessCardPanelModel().getBusinessCardDetails(), "findAnFaQualification"));
		box.setEnabled(false);
		return box;
	}
	
	/**
	 * Create the contactable advisor check box.  When ticked this will enable/disable or 
	 * components that are linked to this logic via the {@linkplain #contactableComponentList}
	 * 
	 * @param id
	 * @return
	 */
	private CheckBox createContactableAdvisor(String id) {
		AbstractCheckBoxModel checkModel = new AbstractCheckBoxModel() {
			
			@Override
			public void unselect() {
				pageModel.getMaintainBusinessCardPanelModel().getBusinessCardDetails()
					.setContactableAdvisor(false);
			}
			
			@Override
			public void select() {
				pageModel.getMaintainBusinessCardPanelModel().getBusinessCardDetails()
					.setContactableAdvisor(true);
			}
			
			@Override
			public boolean isSelected() {
				return pageModel.getMaintainBusinessCardPanelModel().getBusinessCardDetails()
						.isContactableAdvisor();
				
			}
		};
		
		final CheckBox box = new AjaxCheckBox(id, checkModel) {
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
			}
		};
		box.add(new IValidator<Boolean>() {

			public void validate(IValidatable<Boolean> val) {
				if (val.getValue()==true) {
					try {
						getBusinessCardGuiController().validateBusinessCardDetails(pageModel.getMaintainBusinessCardPanelModel().getBusinessCardDetails());
					} catch (ValidationException e) {
						for (String s : e.getErrorMessages()) {
							box.error(s);
						}
					}
				}
			}
			
		});
		box.setEnabled(getEditState()==EditStateType.MODIFY 
				&& pageModel.getMaintainBusinessCardPanelModel().getBusinessCardDetails() != null
				&& pageModel.getMaintainBusinessCardPanelModel().isFindAnFAOptinEnabled());
		box.setOutputMarkupId(true);


		return box;
	}
	

	/**
	 * Create the list of provinces
	 * 
	 * @param id
	 * @return
	 */
	private DropDownChoice createProvinceList(String id) {

		IModel model = new IModel<PostalProvinceDBEnumDTO>() {
			private static final long serialVersionUID = 1L;
			
			public PostalProvinceDBEnumDTO getObject() {
				return pageModel.getMaintainBusinessCardPanelModel().getSelectedProvince();
			}
			public void setObject(PostalProvinceDBEnumDTO arg0) {
				pageModel.getMaintainBusinessCardPanelModel().setSelectedProvince(arg0);
			}
			public void detach() {	
			}
		};
		final DropDownChoice<PostalProvinceDBEnumDTO> field = new DropDownChoice<PostalProvinceDBEnumDTO>(id, model, 
				pageModel.getMaintainBusinessCardPanelModel().getAllProvinces()) {

					private static final long serialVersionUID = 1L;

//					@Override
//					public boolean isEnabled() {
//						return isEditingEnabled();
//					}			
		};
//		contactableComponentList.add(field);
		field.setOutputMarkupId(true);
		field.setNullValid(true);
		
		
		// Update the request kinds field when changing this one
		AjaxFormComponentUpdatingBehavior behaviour = new AjaxFormComponentUpdatingBehavior("change") {
			private static final long serialVersionUID = 1L;
			
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
//				updateShowNextButton(target);
				
			
				pageModel.getMaintainBusinessCardPanelModel().getAllRegions().clear();
				pageModel.getMaintainBusinessCardPanelModel().getAllRegions().addAll(
						getBusinessCardGuiController().getAllPostalRegionsForProvince(
								pageModel.getMaintainBusinessCardPanelModel().getSelectedProvince()));
				target.add(regionChoice);
				
				pageModel.getMaintainBusinessCardPanelModel().getAllPostalAreas().clear();
				target.add(postalAreaChoice);
				
				pageModel.getMaintainBusinessCardPanelModel().getAllPostalPostalCodes().clear();
				target.add(postalPostalCodeList);
				
				addRegionButton.setEnabled(false);
				target.add(addRegionButton);
				addAreaButton.setEnabled(false);
				target.add(addAreaButton);
			}

		};
		field.add(behaviour);
		return field;
	}
	
	/**
	 * Create the list of regions
	 * 
	 * @param id
	 * @return
	 */
	private DropDownChoice createRegionList(String id) {

		IModel<PostalRegionDBEnumDTO> model = new IModel<PostalRegionDBEnumDTO>() {
			private static final long serialVersionUID = 1L;
			
			public PostalRegionDBEnumDTO getObject() {
				return pageModel.getMaintainBusinessCardPanelModel().getSelectedRegion();
			}
			public void setObject(PostalRegionDBEnumDTO arg0) {
				pageModel.getMaintainBusinessCardPanelModel().setSelectedRegion(arg0);
			}
			public void detach() {	
			}
		};
		DropDownChoice<PostalRegionDBEnumDTO> field = new DropDownChoice<PostalRegionDBEnumDTO>(
				id, model, pageModel.getMaintainBusinessCardPanelModel().getAllRegions()) {

					private static final long serialVersionUID = 1L;

//					@Override
//					public boolean isEnabled() {
//						return isEditingEnabled();
//					}			
		};
		field.setOutputMarkupId(true);
		field.setNullValid(true);
		field.add(new AjaxFormComponentUpdatingBehavior("change") {
			private static final long serialVersionUID = 1L;
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
//							
				pageModel.getMaintainBusinessCardPanelModel().getAllPostalAreas().clear();
				
				PostalRegionDBEnumDTO selected = 	pageModel.getMaintainBusinessCardPanelModel()
						.getSelectedRegion();
				if (selected!=null) {
					pageModel.getMaintainBusinessCardPanelModel().getAllPostalAreas().addAll(
						getBusinessCardGuiController().getAllPostalAreasForRegion(selected
								));
				}
				pageModel.getMaintainBusinessCardPanelModel().setSelectedArea(null);
				target.add(postalAreaChoice);
				
				pageModel.getMaintainBusinessCardPanelModel().getAllPostalPostalCodes().clear();
				target.add(postalPostalCodeList);
				
				addRegionButton.setEnabled(true);
				target.add(addRegionButton);
				addAreaButton.setEnabled(false);
				target.add(addAreaButton);
			}		
		});
//		field.setEnabled(isEditingEnabled());
//		contactableComponentList.add(field);
		return field;
	}
	
	/**
	 * Create the add region button
	 * 
	 * @param id
	 * @return
	 */
	private WebMarkupContainer createAddRegionButton(String id) {
						
		final AjaxLink button = new AjaxLink(id, new Model("Add Region")) {
		
			@Override
			public void onClick(AjaxRequestTarget target) {
//			@Override
//			protected void onSubmit(AjaxRequestTarget target, Form<?> arg1) {
				if (logger.isDebugEnabled())
					logger.debug("Adding Region");
				try {
					pageModel.getMaintainBusinessCardPanelModel().getBusinessCardDetails().getSelectedPostalLocations()
						.add(addPostalServiceLocationWithSelection(null));
				} catch (ValidationException e) {
					this.error(e.getFirstErrorMessage());
					target.appendJavaScript("alert('" + e.getFirstErrorMessage() + "')");
				}
				
				target.add(this);
				target.add(serviceLocationList);
				target.add(feedBackPanel); 
			}

			@Override
			public boolean isEnabled() {
				return (pageModel.getMaintainBusinessCardPanelModel().getSelectedRegion()!=null);
			}			
		};
		
//		button.add(new AjaxFormComponentUpdatingBehavior("click"){									
//			private static final long serialVersionUID = 1L;
//			@Override
//			protected void onUpdate(AjaxRequestTarget target) {				
//				if (logger.isDebugEnabled())
//					logger.debug("Adding Region");
//				try {
//					pageModel.getMaintainBusinessCardPanelModel().getBusinessCardDetails().getSelectedPostalLocations()
//						.add(addPostalServiceLocationWithSelection(null));
//				} catch (ValidationException e) {
//					button.error(e.getFirstErrorMessage());
//					target.appendJavascript("alert('" + e.getFirstErrorMessage() + "')");
//				}
//				
//				target.add(button);
//				target.add(serviceLocationList);
//				target.add(feedBackPanel); 
//			}									
//		});
		

//		contactableComponentList.add(button);	
//		button.setEnabled(isEditingEnabled());
		button.setOutputMarkupId(true);
		return button;
	}
	
	
	
	/**
	 * Requires that at least province and region are set.
	 * 
	 * @param obj
	 * @return
	 * @throws ValidationException 
	 */
	private PostalServiceLocationsDTO addPostalServiceLocationWithSelection(Object obj) throws ValidationException {
		MaintainBusinessCardPanelModel model = pageModel.getMaintainBusinessCardPanelModel();
		PostalServiceLocationsDTO dto = new PostalServiceLocationsDTO();
		dto.setProvinceDescription(model.getSelectedProvince().getName());
		dto.setProvinceId(model.getSelectedProvince().getKeyInt());
		
		dto.setRegionDescription(model.getSelectedRegion().getName());
		dto.setRegionId(model.getSelectedRegion().getKeyInt());
		
		if (obj == null) {
			// Is a Region
			dto.setPartyRoleContextType(SRSType.SERVICELOCATION_REGION);
		}
		
		if (obj instanceof PostalAreaFLO || obj instanceof  PostalPostalCodeFLO) {
			// Is a Region
			dto.setPostalAreaDescription(model.getSelectedArea().getPostalArea());
			dto.setPostalAreaId(model.getSelectedArea().getId());
			dto.setPartyRoleContextType(SRSType.SERVICELOCATION_AREA);
		}
		
		if (obj instanceof  PostalPostalCodeFLO) {
			// Is a postal code
			PostalPostalCodeFLO flo = (PostalPostalCodeFLO)obj;
			dto.setPostalCodeDescription(flo.getSuburbDescription());
			dto.setPostalCodeId(flo.getId());
			dto.setPostalCode(flo.getPostalCode());
			dto.setPartyRoleContextType(SRSType.SERVICELOCATION_POSTALCODE);
			
			// also add area data
			dto.setPostalAreaDescription(model.getSelectedArea().getPostalArea());
			dto.setPostalAreaId(model.getSelectedArea().getId());
		}
		
		if (logger.isDebugEnabled()) {
			logger.debug(" dto = id=" + dto.getPartyRoleOid()
					+ " ,type=" + dto.getPartyRoleContextType()
					+ " ,regId=" + dto.getRegionId()
					+ " ,areaId="+ dto.getPostalAreaId()
					+ " ,postalId="+ dto.getPostalCodeId()
					+ " ,desc=" +dto.getRegionDescription() + " " + dto.getPostalAreaDescription() + " " + dto.getPostalCodeDescription());
		}
		
		/*
		 * Check if it exists
		 */
		CompareUtil compareUtil = CompareUtil.getInstance();
		// TODO Jean - Fix this later
		for (PostalServiceLocationsDTO selDto : model.getBusinessCardDetails().getSelectedPostalLocations()) {
		
			if (logger.isDebugEnabled()){
				logger.debug("Selected = id=" + selDto.getPartyRoleOid()
						+ " ,type=" + selDto.getPartyRoleContextType()
						+ " ,regId=" + selDto.getRegionId()
						+ " ,areaId="+ selDto.getPostalAreaId()
						+ " ,postalId="+ selDto.getPostalCodeId()
						+ " ,desc=" +selDto.getRegionDescription() + "-" + selDto.getPostalAreaDescription() + "-" + selDto.getPostalCodeDescription()
							);
				logger.debug("  type="+(selDto.getPartyRoleContextType()==dto.getPartyRoleContextType())
						+ "  ,region=" +(selDto.getPartyRoleContextType()==SRSType.SERVICELOCATION_REGION 
								&& compareUtil.isEqual(selDto.getRegionId(),dto.getRegionId()))
						+ "  ,area=" +(selDto.getPartyRoleContextType()==SRSType.SERVICELOCATION_AREA 
							&& compareUtil.isEqual(selDto.getPostalAreaId(),dto.getPostalAreaId()))
						+ "  ,postalCode=" + (selDto.getPartyRoleContextType()==SRSType.SERVICELOCATION_POSTALCODE 
							&& compareUtil.isEqual(selDto.getPostalCodeId(),dto.getPostalCodeId())));
			}
			
			// Check if the type and relevant id is the same
			if (selDto.getPartyRoleContextType().equals(dto.getPartyRoleContextType())) {
				// Region related checks
				if (selDto.getPartyRoleContextType()==SRSType.SERVICELOCATION_REGION
						&& compareUtil.isEqual(selDto.getRegionId(),dto.getRegionId()))  {
					throw new ValidationException("Selected location has already been selected");
				}
				
				// Area related checks
				if (selDto.getPartyRoleContextType()==SRSType.SERVICELOCATION_AREA) {
					// Check area
					if (compareUtil.isEqual(selDto.getPostalAreaId(),dto.getPostalAreaId())) {
						throw new ValidationException("Selected location has already been selected");
					}
				}
				
				// Postal code related checks 
				if (selDto.getPartyRoleContextType()==SRSType.SERVICELOCATION_POSTALCODE ) {
					if (compareUtil.isEqual(selDto.getPostalCodeId(),dto.getPostalCodeId())) {
						throw new ValidationException("Selected location has already been selected");
					}
				}
			}
			
			// Add region check
			if ((dto.getPartyRoleContextType()==SRSType.SERVICELOCATION_REGION)  
					&& compareUtil.isEqual(selDto.getRegionId(),dto.getRegionId())) {
				throw new ValidationException("An area within this region has already been added and must first be removed \"" 
						+ selDto.getPostalAreaDescription()+ "\"");
			}
			
			// Add region check
			if ((dto.getPartyRoleContextType()==SRSType.SERVICELOCATION_POSTALCODE 
					|| dto.getPartyRoleContextType()==SRSType.SERVICELOCATION_AREA)  
					&& compareUtil.isEqual(selDto.getRegionId(),dto.getRegionId())
					&& selDto.getPartyRoleContextType()==SRSType.SERVICELOCATION_REGION) {
				throw new ValidationException("Item is already included in the postal region \"" 
						+ selDto.getRegionDescription()+ "\"");
			}
			
			// Add Area check
			if ((dto.getPartyRoleContextType()==SRSType.SERVICELOCATION_POSTALCODE)  
					&& compareUtil.isEqual(selDto.getPostalAreaId(),dto.getPostalAreaId())
					&& selDto.getPartyRoleContextType()==SRSType.SERVICELOCATION_AREA) {
				throw new ValidationException("Item is already included in the postal area \"" 
						+ selDto.getPostalAreaDescription() + "\"");
			}
		}
		return dto;
	}
	
	/**
	 * Add an area
	 * @param id
	 * @param findAnFAForm 
	 * @return
	 */
	private WebMarkupContainer createAddAreaButton(String id, FindAnFAForm findAnFAForm) {
		
		final AjaxLink button = new AjaxLink(id, new Model("Add Area")) {
			
			@Override
			public void onClick(AjaxRequestTarget target) {
				if (logger.isDebugEnabled())
					logger.debug("Adding area - " + pageModel.getMaintainBusinessCardPanelModel().getSelectedArea());
				try {
					pageModel.getMaintainBusinessCardPanelModel().getBusinessCardDetails().getSelectedPostalLocations()
						.add(addPostalServiceLocationWithSelection(
								pageModel.getMaintainBusinessCardPanelModel().getSelectedArea()));
				} catch (ValidationException e) {
					this.error(e.getFirstErrorMessage());
					target.appendJavaScript("alert('" + e.getFirstErrorMessage() + "')");
				}
				
				target.add(this);
				target.add(serviceLocationList);
				target.add(feedBackPanel); 
			}

			@Override
			public boolean isEnabled() {
				return (pageModel.getMaintainBusinessCardPanelModel().getSelectedArea()!=null);
			}			
		};
		
//		final Button button = new AjaxButton(id, new Model("Add Area"), findAnFAForm) {
//
//			@Override
//			protected void onSubmit(AjaxRequestTarget target, Form<?> arg1) {
//				if (logger.isDebugEnabled())
//					logger.debug("Adding area - " + pageModel.getMaintainBusinessCardPanelModel().getSelectedArea());
//				try {
//					pageModel.getMaintainBusinessCardPanelModel().getBusinessCardDetails().getSelectedPostalLocations()
//						.add(addPostalServiceLocationWithSelection(
//								pageModel.getMaintainBusinessCardPanelModel().getSelectedArea()));
//				} catch (ValidationException e) {
//					this.error(e.getFirstErrorMessage());
//					target.appendJavascript("alert('" + e.getFirstErrorMessage() + "')");
//				}
//				
//				target.add(this);
//				target.add(serviceLocationList);
//				target.add(feedBackPanel); 
//				
//			}

//			@Override
//			public boolean isEnabled() {
//				return (pageModel.getMaintainBusinessCardPanelModel().getSelectedArea()!=null);
//			}
//			
//		};	
//		contactableComponentList.add(button);
		button.setOutputMarkupId(true);
		return button;
//	}
	}
	
	/**
	 * Create the list of postal area's
	 * 
	 * @param id
	 * @return
	 */
	private DropDownChoice createPostalAreaList(String id) {

		IModel model = new IModel<PostalAreaFLO>() {
			private static final long serialVersionUID = 1L;
			
			public PostalAreaFLO getObject() {
				return pageModel.getMaintainBusinessCardPanelModel().getSelectedArea();
			}
			public void setObject(PostalAreaFLO arg0) {
				pageModel.getMaintainBusinessCardPanelModel().setSelectedArea(arg0);
			}
			public void detach() {	
			}
		};
		DropDownChoice<PostalAreaFLO> field = new DropDownChoice<PostalAreaFLO>(id, model, pageModel.getMaintainBusinessCardPanelModel().getAllPostalAreas()) {

			private static final long serialVersionUID = 1L;

//			@Override
//			public boolean isEnabled() {
//				return isEditingEnabled();
//			}			
		};
//		contactableComponentList.add(field);
		
		field.setOutputMarkupId(true);
		field.setNullValid(true);
		field.add(new AjaxFormComponentUpdatingBehavior("change") {
			private static final long serialVersionUID = 1L;
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
//				updateShowNextButton(target);
				pageModel.getMaintainBusinessCardPanelModel().getAllPostalPostalCodes().clear();
				PostalAreaFLO selected = 	pageModel.getMaintainBusinessCardPanelModel()
						.getSelectedArea();
				if (selected!=null) {
					pageModel.getMaintainBusinessCardPanelModel().getAllPostalPostalCodes().addAll(
						getBusinessCardGuiController().getAllPostalPostalCodesForArea(selected));
				}
				target.add(postalPostalCodeList);
				
				addAreaButton.setEnabled(true);
				target.add(addAreaButton);
			}		
		});
		field.setEnabled(isEditingEnabled());
		return field;
	}
	
	/**
	 * Create the postal postalcode list for selection
	 * 
	 * @param id
	 * @return
	 */
	private WebMarkupContainer createPostalPostalCodeList(String id) {
	
		/* 
		 * Create the columns
		 */
		List<IGridColumn> columns = new ArrayList<IGridColumn>();

		columns.add( new PropertyColumn(new Model("Postcode"),"postalCode", "postalCode").setInitialSize(50));
		columns.add( new PropertyColumn(new Model("Suburbs"),"suburbDescription", "suburbDescription").setInitialSize(400).setWrapText(true));
		columns.add(new AbstractColumn("edit", new Model("Edit")){
			
			private static final long serialVersionUID = 1L;
			@Override
			public Component newCell(WebMarkupContainer parent, String componentId, IModel rowModel) {
				
				final PostalPostalCodeFLO flo = (PostalPostalCodeFLO) rowModel.getObject();
				
//				if(address.getUsage() == UsageType.SECURE 
//						&& (!ContactDetailsPanel.this.includeSecureSelection || outstandingSecureRequest)){
//					return new EmptyPanel(componentId);
//				}else{				
				//if(address.getOid() != 0){
					final Button searchButton = new Button("value", new Model("Add"));	
					searchButton.add(new AjaxFormComponentUpdatingBehavior("click"){									
						private static final long serialVersionUID = 1L;
						@Override
						protected void onUpdate(AjaxRequestTarget target) {
							if (logger.isDebugEnabled())
								logger.debug("Adding postalCode FLO " + flo.getPostalCode());
							searchButton.setEnabled(false);
							
							try {
								pageModel.getMaintainBusinessCardPanelModel().getBusinessCardDetails().getSelectedPostalLocations()
									.add(addPostalServiceLocationWithSelection(flo));
							} catch (ValidationException e) {
								error(e.getFirstErrorMessage());
								target.appendJavaScript("alert('" + e.getFirstErrorMessage() + "')");
							}
							
							target.add(feedBackPanel); 						
							target.add(serviceLocationList);
							target.add(searchButton);
							
						}									
					});
					searchButton.setOutputMarkupId(true);
					return HelperPanel.getInstance(componentId,searchButton);		
//				}
			}				
		}.setInitialSize(45));
		
		/*
		 * Create the search result table
		 */
		SRSDataGrid grid = new SRSDataGrid(id, new SRSDataProviderAdapter(
				new SortableListDataProvider<PostalPostalCodeFLO>(pageModel.getMaintainBusinessCardPanelModel().getAllPostalPostalCodes())), 
				columns, EditStateType.VIEW){

			private static final long serialVersionUID = 1L;

//			@Override
//			public boolean isEnabled() {
//				return isEditingEnabled();
//			}			
		};
//		contactableComponentList.add(grid);
		
		grid.setAutoResize(true);
		grid.setRowsPerPage(10);
		grid.setContentHeight(220, SizeUnit.PX);
//		grid.setAllowSelectMultiple(dataModel.getBulkAuthoriseType()!=null);
//		grid.setCleanSelectionOnPageChange(dataModel.getBulkAuthoriseType()==null);
//		grid.setGridWidth(99, za.co.liberty.web.wicket.markup.html.grid.SRSDataGrid.GridSizeUnit.PERCENTAGE);
		grid.setGridWidth(700, SRSDataGrid.GridSizeUnit.PIXELS);
		return grid;
	}
	
	
	/**
	 * Create the postal postalcode list for selection
	 * 
	 * @param id
	 * @return
	 */
	private SRSDataGrid createServicedLocationList(String id) {
	
		/* 
		 * Create the columns
		 */
		List<IGridColumn> columns = new ArrayList<IGridColumn>();

		columns.add( new PropertyColumn(new Model("Province"),"provinceDescription", "provinceDescription").setInitialSize(80));
		columns.add( new PropertyColumn(new Model("Region"),"regionDescription", "regionDescription").setInitialSize(100));
		columns.add( new PropertyColumn(new Model("Area"),"postalAreaDescription", "postalAreaDescription").setInitialSize(100));
		columns.add( new PropertyColumn(new Model("Postalcode"),"postalCode", "postalCode").setInitialSize(80));
		columns.add( new PropertyColumn(new Model("Postal Description"),"postalCodeDescription", "postalCodeDescription").setInitialSize(300).setWrapText(true));
		
		if (getEditState()==EditStateType.MODIFY) {
			columns.add(new AbstractColumn("remove", new Model("Remove")){
				
				private static final long serialVersionUID = 1L;
				@Override
				public Component newCell(WebMarkupContainer parent, String componentId, IModel rowModel) {
					
					final PostalServiceLocationsDTO dto = (PostalServiceLocationsDTO) rowModel.getObject();
					
					final Button removeButton = new Button("value", new Model("Remove"));	
					removeButton.add(new AjaxFormComponentUpdatingBehavior("click"){									
						private static final long serialVersionUID = 1L;
						@Override
						protected void onUpdate(AjaxRequestTarget target) {
							if (logger.isDebugEnabled())
								logger.debug("Removing service location " + dto);
						
							pageModel.getMaintainBusinessCardPanelModel().getBusinessCardDetails().getSelectedPostalLocations()
									.remove(dto);							
							target.add(feedBackPanel); 						
							target.add(serviceLocationList);
							
						}									
					});
					removeButton.setOutputMarkupId(true);
					return HelperPanel.getInstance(componentId,removeButton);		
				}				
			}.setInitialSize(60));
		}
		
		/*
		 * Create the search result table
		 */
		SRSDataGrid grid = new SRSDataGrid(id, new SRSDataProviderAdapter(
				new SortableListDataProvider<PostalServiceLocationsDTO>(pageModel.getMaintainBusinessCardPanelModel().getBusinessCardDetails()
						.getSelectedPostalLocations())), 
				columns, EditStateType.VIEW);
		grid.setAutoResize(true);
		grid.setRowsPerPage(10);
		grid.setContentHeight(220, SizeUnit.PX);
//		grid.setAllowSelectMultiple(dataModel.getBulkAuthoriseType()!=null);
//		grid.setCleanSelectionOnPageChange(dataModel.getBulkAuthoriseType()==null);
//		grid.setGridWidth(99, za.co.liberty.web.wicket.markup.html.grid.SRSDataGrid.GridSizeUnit.PERCENTAGE);
		grid.setGridWidth(790, SRSDataGrid.GridSizeUnit.PIXELS);
//		grid.setEnabled(isEditingEnabled());
		return grid;
	}
	
	/**
	 * Create the spoken languages check boxes.  This is done by distributing the array of available languages
	 * over multiple rows and columns of a table.  Currently it is set to 4 columns over multiple lines but 
	 * can be changed in the code below..
	 * 
	 * @param id
	 * @return
	 */
	private ListView<LanguagePreferenceDBEnumDTO[]> createSpokenLanguages(String id) {
		
		/*
		 * Take the list of languages and break up into an array of lists such
		 * as to place 4 languages per row.
		 */
		List<LanguagePreferenceDBEnumDTO[]> rowList = new ArrayList<LanguagePreferenceDBEnumDTO[]>();
		
		List<LanguagePreferenceDBEnumDTO> list = pageModel.getMaintainBusinessCardPanelModel().getAllSpokenLanguages();
		
		// Number of columns, zero based array
		int cols = 3;
		
		/*
		 * Convert the array of languages into a matrix of 4 columns by however many rows required. 
		 */
		LanguagePreferenceDBEnumDTO[] array = new LanguagePreferenceDBEnumDTO[cols + 1];
		rowList.add(array);
		
		for (int i = 0, j = 0; i < list.size();++i, ++j) {
			if (j==(cols+1)) {
				array = new LanguagePreferenceDBEnumDTO[cols + 1];
				j=0;
				rowList.add(array);
			}
			array[j] = list.get(i);
		}
		
		ListView<LanguagePreferenceDBEnumDTO[]> rows = new ListView<LanguagePreferenceDBEnumDTO[]>(id, rowList) {
			private static final long serialVersionUID = 0L;
			@Override
			protected void populateItem(final ListItem<LanguagePreferenceDBEnumDTO[]> item) {
				item.add(createSpokenLanguagesCol("spokenLanguagesCol", item.getModelObject()));
			}		
		};
	
		rows.setOutputMarkupId(true);
		return rows;
		
	}
	
	/**
	 * Create the columns for the spoken language table 
	 * 
	 * @param id
	 * @param cols
	 * @return
	 */
	private ListView<LanguagePreferenceDBEnumDTO> createSpokenLanguagesCol(String id,LanguagePreferenceDBEnumDTO[] cols) {
		

		List<LanguagePreferenceDBEnumDTO> colList = new ArrayList<LanguagePreferenceDBEnumDTO>();
		
		colList.addAll(Arrays.asList(cols));
				
		ListView<LanguagePreferenceDBEnumDTO> colListView = new ListView<LanguagePreferenceDBEnumDTO>(id, colList) {

			private static final long serialVersionUID = 0L;

			@Override
			protected void populateItem(final ListItem<LanguagePreferenceDBEnumDTO> item) {
				
				/*
				 * Create a hidden check box and description if the column data is null.
				 */
				if (item.getModelObject()==null) {
					item.add(new CheckBox("langCheck").setVisible(false));
					item.add(new Label("langDescription", ""));
					return;
				}

				/*
				 * Define a special model to update the list of selected spoken languages
				 */
				AbstractCheckBoxModel checkModel2 = new AbstractCheckBoxModel() {
					
					@Override
					public void unselect() {
						pageModel.getMaintainBusinessCardPanelModel().getBusinessCardDetails()
							.getSpokenLanguages().remove(item.getModelObject());
					}
					
					@Override
					public void select() {
						pageModel.getMaintainBusinessCardPanelModel().getBusinessCardDetails()
							.getSpokenLanguages().add(item.getModelObject());
					}
					
					@Override
					public boolean isSelected() {
						return pageModel.getMaintainBusinessCardPanelModel().getBusinessCardDetails()
								.getSpokenLanguages().contains(item.getModelObject());
						
					}
				};
						
				/*
				 * Create the check box.
				 */
				final CheckBox box = new CheckBox("langCheck", checkModel2);
				box.setEnabled( isEditingEnabled());
				box.setOutputMarkupId(true);
				box.add(new AjaxFormComponentUpdatingBehavior("change") {
					private static final long serialVersionUID = 1L;
					@Override
					protected void onUpdate(AjaxRequestTarget target) {
					}		
				});
				item.add(box);
				item.add(new Label("langDescription", item.getModelObject().getDescription()));
				
			}
			
		};
		colListView.setOutputMarkupId(true);
		return colListView;
		
	}
	
	/*
	 * Indicate if editing is enabled.
	 */
	private boolean isEditingEnabled() {
		return (getEditState()==EditStateType.MODIFY 
				&& pageModel.getMaintainBusinessCardPanelModel().getBusinessCardDetails() != null
				&& pageModel.getMaintainBusinessCardPanelModel().isFindAnFAOptinEnabled()
				&& pageModel.getMaintainBusinessCardPanelModel().isShowFindAnFAPanel()
				//&& pageModel.getMaintainBusinessCardPanelModel().getBusinessCardDetails().isContactableAdvisor()
				);
	}
	
	
	
	/**
	 * Get the BusinessCardGuiController bean
	 * @return
	 */
	private IBusinessCardGuiController getBusinessCardGuiController(){
		if(businessCardGuiController == null){
			try {
				businessCardGuiController = ServiceLocator.lookupService(IBusinessCardGuiController.class);
			} catch (NamingException e) {
				throw new CommunicationException(e);
			}
		}
		return businessCardGuiController;
	}
	
	
	public Class getPanelClass() {		
		return BusinessCardFindAnFADetailsPanel.class;
	}	
}
