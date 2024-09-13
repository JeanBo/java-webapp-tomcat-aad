package za.co.liberty.web.pages.maintainagreement;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import javax.naming.NamingException;

import org.apache.commons.lang.SerializationUtils;
import org.apache.log4j.Logger;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import com.inmethod.grid.DataProviderAdapter;
import com.inmethod.grid.IGridColumn;
import com.inmethod.grid.SizeUnit;

import za.co.liberty.business.guicontrollers.agreement.IAgreementGUIController;
import za.co.liberty.dto.agreement.AgreementDTO;
import za.co.liberty.dto.agreement.AgreementHomeRoleDTO;
import za.co.liberty.dto.agreement.AgreementRoleDTO;
import za.co.liberty.dto.agreement.maintainagreement.AgreementRoleGridDTO;
import za.co.liberty.dto.common.IDValueDTO;
import za.co.liberty.dto.contracting.ResultAgreementDTO;
import za.co.liberty.dto.contracting.ResultPartyDTO;
import za.co.liberty.dto.gui.context.ResultContextItemDTO;
import za.co.liberty.exceptions.data.DataNotFoundException;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.helpers.lookup.ServiceLocator;
import za.co.liberty.helpers.persistence.TemporalityHelper;
import za.co.liberty.helpers.util.ComparatorUtil;
import za.co.liberty.helpers.util.DateUtil;
import za.co.liberty.interfaces.agreements.RoleKindType;
import za.co.liberty.srs.type.SRSType;
import za.co.liberty.web.data.enums.ContextType;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.pages.interfaces.ISecurityPanel;
import za.co.liberty.web.pages.maintainagreement.model.MaintainAgreementPageModel;
import za.co.liberty.web.pages.panels.BasePanel;
import za.co.liberty.web.pages.panels.HelperPanel;
import za.co.liberty.web.pages.search.ContextSearchPopUp;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGrid;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGrid.GridSizeUnit;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGridColumn;
import za.co.liberty.web.wicket.markup.html.grid.SRSGridRowSelectionCheckBox;
import za.co.liberty.web.wicket.markup.repeater.data.ListDataProvider;

public abstract class AbstractHomeRoleFactory extends BasePanel implements ISecurityPanel {
	
 
	
	
	private static final transient Logger logger = Logger.getLogger(AbstractHomeRoleFactory.class);
	
	private transient IAgreementGUIController guiController;
	
	private MaintainAgreementPageModel pageModel;
	
//	private BasePanel agreementHierarchyPanel;
	
	public AgreementHomeRoleDTO currentHomeRole;
	
	//will be true if there is an existing home request
	private boolean existingHomeRequest;
	
	private FeedbackPanel feedBackPanel;	
	
	private SRSDataGrid homeGrid;
	
	private static ArrayList<IDValueDTO> homeTypes;
	
	private static String HOME_EXTERNAL_REF_GRID_OBJECT_PROPERTY_NAME = "rolePlayerReference.externalReference";
	
	private HashMap<AgreementHomeRoleDTO, HashMap<String,Component>> homeGridComponentMap;
	
	private ModalWindow searchWindow;
	
	private AgreementRoleDTO currentSearchHomeRole;
	
	private AgreementRoleGridDTO currentSearchAgmtRole;
	
	public AbstractHomeRoleFactory(String id, EditStateType editState, Page parentPAge) {
		super(id, editState, parentPAge);
	}
	
	public void setHomeRoleFactoryVariables(String id, EditStateType editState, MaintainAgreementPageModel pageModel , FeedbackPanel feedbackPanel,boolean existingHomeRequest , ArrayList<IDValueDTO> homeTypes , AgreementHomeRoleDTO currentHomeRole, HashMap<AgreementHomeRoleDTO, HashMap<String,Component>> homeGridComponentMap) {
		//super(id, editState);
		this.pageModel = pageModel;
		this.feedBackPanel = feedbackPanel;
		this.existingHomeRequest = existingHomeRequest;
		this.homeTypes = homeTypes;
		this.currentHomeRole = currentHomeRole;
		this.homeGridComponentMap = homeGridComponentMap;
	 
	}
	
	/**
	 * Create a grid for the home roles
	 * 
	 * @return
	 */
	public SRSDataGrid createHomeGrid(String id, AgreementDTO dto) {
		List<AgreementHomeRoleDTO> homeRoles = dto.getCurrentAndFutureHomeRoles();
		
		if (homeRoles == null) {
			homeRoles = new ArrayList<AgreementHomeRoleDTO>();
		}
		
		//quick doing a check to see if the actual party is still active, the role is active but the party might have been terminated
		for(AgreementHomeRoleDTO home : homeRoles){
			ResultPartyDTO homeDTO = (ResultPartyDTO) home.getRolePlayerReference();
			if(homeDTO.getEffectiveTo() != null && !homeDTO.getEffectiveTo().after(TemporalityHelper.getInstance().getNewNOWDateWithNoTime())){
				//node has been terminated
				warn("Parent node " + homeDTO.getName() + " has been removed, please remove the link to it and add another parent");
			}
		}
		
		List<AgreementHomeRoleDTO> noneSelectable = null;
		if(homeRoles.size() > 0){			
			Date now = TemporalityHelper.getInstance().getNewNOWDateWithNoTime();
			for(AgreementHomeRoleDTO home : homeRoles){
				if( dto.getStartDate() != null){
					if(home.getEffectiveFrom() != null &&  
							(!dto.getStartDate().after(now) && !home.getEffectiveFrom().after(now) && 
							(home.getEffectiveTo() == null || !home.getEffectiveTo().before(now))) || 
							!dto.getStartDate().before(now) && !home.getEffectiveFrom().after(dto.getStartDate())){
						//got current parent
						currentHomeRole = home;
						break;
					}
				}
			}
			if(currentHomeRole == null){
				currentHomeRole = homeRoles.get(0);
			}
			noneSelectable = new ArrayList<AgreementHomeRoleDTO>(1);				
			noneSelectable.add(currentHomeRole);				
		}			
		homeGrid = new SRSDataGrid(id, new DataProviderAdapter(
				new ListDataProvider<AgreementHomeRoleDTO>(homeRoles)),
				getHomeColumns(), ((existingHomeRequest) ? EditStateType.VIEW : getEditState()),noneSelectable);
		
		homeGrid.setCleanSelectionOnPageChange(false);
		homeGrid.setClickRowToSelect(false);
		homeGrid.setAllowSelectMultiple(true);
		//grid.setGridWidth(650, GridSizeUnit.PIXELS);
		homeGrid.setGridWidth(90, GridSizeUnit.PERCENTAGE);
		homeGrid.setRowsPerPage(2);
		homeGrid.setContentHeight(70, SizeUnit.PX);
		
		//homeGrid.setAutoResize(false);
//		homeGrid.setRowsPerPage(10);
		//homeGrid.setContentHeight(228, SizeUnit.PX);
		//grid.setAllowSelectMultiple(pageModel.getSearchOptions().isAllowMultipleSelect());
		return homeGrid;
	}

	/**
	 * Create the button to remove a home
	 * 
	 * @return
	 */
	public  Button createRemoveHomeButton(String id) {
		final Button button = new Button(id);
		button.add(new AjaxFormComponentUpdatingBehavior("click") {
			private static final long serialVersionUID = 0L;
	
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				List<Object> selections = homeGrid.getSelectedItemObjects();	
				if(getEditState() == EditStateType.ADD){
					if(pageModel.getMaintainAgreementDTO().getAgreementDTO().getCurrentAndFutureHomeRoles().size() == 1){
						pageModel.getMaintainAgreementDTO().getAgreementDTO().getCurrentAndFutureHomeRoles().clear();
						target.add(homeGrid);
					}
				}
					
				boolean found = false;
				if(currentHomeRole != null){
					for(AgreementRoleDTO home : pageModel.getMaintainAgreementDTO().getAgreementDTO().getCurrentAndFutureHomeRoles()){
						if(currentHomeRole == home){
							found = true;
						}
					}
				}
				if(currentHomeRole == null || !found){
					if(pageModel.getMaintainAgreementDTO().getAgreementDTO().getCurrentAndFutureHomeRoles().size() > 0){
						currentHomeRole = pageModel.getMaintainAgreementDTO().getAgreementDTO().getCurrentAndFutureHomeRoles().get(0);
					}
				}				
				for (Object selection : selections) {
					// check that we are not removing the main home link,
					// users must change the main home but can not remove
					// it
					if (selection != currentHomeRole) {
						pageModel.getMaintainAgreementDTO().getAgreementDTO().getCurrentAndFutureHomeRoles().remove(selection);
					}
				}
				if (pageModel.getMaintainAgreementDTO().getAgreementDTO().getCurrentAndFutureHomeRoles().size() == 1
						&& selections.size() != 0) {
					// we erase the end date
					currentHomeRole.setEffectiveTo(null);
				}
				if ( feedBackPanel != null) {
					target.add( feedBackPanel);
				}
				target.add(homeGrid);
			}
		});		
		if(getEditState() == EditStateType.AUTHORISE){			
			button.setVisible(false);
		}
		button.setOutputMarkupId(true);
		button.setOutputMarkupPlaceholderTag(true);
		setUpbutton(button, !existingHomeRequest);
		return button;
	}

	/**
		 * Get the list of node columns for the grid
		 * @return
		 */
		private List<IGridColumn> getHomeColumns() {
			Vector<IGridColumn> cols = new Vector<IGridColumn>(7);
			if (!getEditState().isViewOnly() && !existingHomeRequest) {
				SRSGridRowSelectionCheckBox col = new SRSGridRowSelectionCheckBox(
						"checkBox");
				cols.add(col.setInitialSize(30));
			}
			//add in the type selection column
			cols.add(new SRSDataGridColumn<AgreementHomeRoleDTO>("rolePlayerReference.typeOid",
					new Model("Type"), "rolePlayerReference.typeOid", "rolePlayerReference.typeOid", getEditState()) {		
				private static final long serialVersionUID = 1L;
				@Override
				public Panel newCellPanel(WebMarkupContainer parent,
						String componentId, IModel rowModel,
						String objectProperty, EditStateType state,
						final AgreementHomeRoleDTO data) {					
					if (existingHomeRequest || getEditState().isViewOnly() || data.getRoleID() != 0) {
						return super.newCellPanel(parent, componentId,
								rowModel, "rolePlayerReference.hierarchyOrganisationTypeName", state, data);
					}					
					HelperPanel dropdown = createDropdownField("Node Type",componentId,new PropertyModel(data,objectProperty){
						@Override
						public Object getObject() {
							//return one of the values in the static list						
							Long id = (Long) super.getObject();
							if(id == null){
								return null;							
							}
							for(IDValueDTO type : homeTypes){
								if(type.getOid() == id){
									return type;
								}
							}
							return null;
						}
						@Override
						public void setObject(Object arg0) {						
							super.setObject(((IDValueDTO)arg0).getOid());
						}					
					},homeTypes, new ChoiceRenderer("name","oid"), 
							 "Select", true,false,new EditStateType[]{EditStateType.ADD,EditStateType.MODIFY});
					DropDownChoice dropdownComp = (DropDownChoice) dropdown
							.getEnclosedObject();
					dropdownComp.add(new AjaxFormComponentUpdatingBehavior(
							"change") {					
						private static final long serialVersionUID = 1L;
	
						@Override
						protected void onUpdate(AjaxRequestTarget target) {
							//refresh table						
							//remove all detail in row	
							long type = data.getRolePlayerReference().getTypeOid();
							ResultPartyDTO party = new ResultPartyDTO();
							party.setTypeOid(type);
							data.setRolePlayerReference(party);						
							target.add(homeGrid);
							updateExternalRefCompSize(data,target);
						}
					});
					dropdownComp.setNullValid(false);	
					dropdownComp.setOutputMarkupId(true);
					addToHomeRowComponentsList(data,dropdownComp,"rolePlayerReference.hierarchyOrganisationTypeName");
					return dropdown;
				}
			}.setInitialSize(80));
			//add in the name column(Display only col)
			cols.add(new SRSDataGridColumn<AgreementHomeRoleDTO>("rolePlayerReference.name",
					new Model("Name"), "rolePlayerReference.name", "rolePlayerReference.name", getEditState()).setInitialSize(230));
			
			//adding the external reference column
			cols.add(new SRSDataGridColumn<AgreementHomeRoleDTO>(HOME_EXTERNAL_REF_GRID_OBJECT_PROPERTY_NAME,
					new Model("Code"), HOME_EXTERNAL_REF_GRID_OBJECT_PROPERTY_NAME, HOME_EXTERNAL_REF_GRID_OBJECT_PROPERTY_NAME, getEditState()) {
	
				@Override
				public Panel newCellPanel(WebMarkupContainer parent,
						String componentId, IModel rowModel,
						String objectProperty, EditStateType state,
						final AgreementHomeRoleDTO data) {				
					
					if (existingHomeRequest || getEditState().isViewOnly() || data.getRoleID() != 0) {
						return super.newCellPanel(parent, componentId,
								rowModel, objectProperty, state, data);
					}		
	
					final TextField externalRef = new TextField("value", new PropertyModel(data, objectProperty));
					externalRef.setOutputMarkupId(true);
					externalRef.setLabel(new Model("External Reference"));
					externalRef.setRequired(true);				
					//validationComponents.add(uacfid);		
					addToHomeRowComponentsList(data,externalRef,HOME_EXTERNAL_REF_GRID_OBJECT_PROPERTY_NAME);
					updateExternalRefCompSize(data,null);
					return HelperPanel.getInstance(componentId, externalRef);
				}
			}.setInitialSize(50));
			
	//		add search button, don't display this column on view
			if((getEditState() == null || !getEditState().isViewOnly()) && !existingHomeRequest){
				cols.add(new SRSDataGridColumn<AgreementHomeRoleDTO>("searchParty",
						new Model("Search"), "searchParty", getEditState()){	
					
							private static final long serialVersionUID = 1L;							
	
							@Override
							public Panel newCellPanel(WebMarkupContainer parent, String componentId, IModel rowModel, String objectProperty, EditStateType state, final AgreementHomeRoleDTO data) {
								if(data.getRoleID() == 0){
									Button searchButton = new Button("value", new Model("Search"));	
									searchButton.add(new AjaxFormComponentUpdatingBehavior("click"){									
										private static final long serialVersionUID = 1L;
										@Override
										protected void onUpdate(AjaxRequestTarget target) {
											currentSearchHomeRole = data;
											currentSearchAgmtRole = null;										
											searchWindow.show(target);										
										}									
									});
									return HelperPanel.getInstance(componentId,searchButton);	
								}else{
									return new EmptyPanel(componentId);
								}
							}				
					
				}.setInitialSize(67));
			}	
			
			cols.add(new SRSDataGridColumn<AgreementHomeRoleDTO>("consultantCode",
					new Model("Consultant Code"), "consultantCode", "consultantCode",
					getEditState()) {
				private static final long serialVersionUID = 1L;
	
				@Override
				public Panel newCellPanel(WebMarkupContainer parent,
						String componentId, IModel rowModel,
						String objectProperty, EditStateType state,
						final AgreementHomeRoleDTO data) {	
					
					if (existingHomeRequest || getEditState().isViewOnly() || (data.getRoleID() != 0 && !currentHomeRole.equals(data)) 
							|| (data.getRoleID() != 0 && currentHomeRole.equals(data) && pageModel.getMaintainAgreementDTO().getAgreementDTO().getCurrentAndFutureHomeRoles().size() > 1)) {
						return super.newCellPanel(parent, componentId,
								rowModel, objectProperty, state, data);
					}
					TextField consCode = new TextField("value",
							new PropertyModel(data, objectProperty));				
					consCode.add(new AttributeModifier("size", "16"));
					consCode.add(new AttributeModifier("maxlength", "13"));				
					consCode.setRequired(true);
					consCode.setLabel(new Model("Thirteen Digit Consultant Code"));
					consCode.setOutputMarkupId(true);
					consCode.setOutputMarkupPlaceholderTag(true);
					//make sure that the code is updated when typing as row gets refreshed
					consCode.add(new AjaxFormComponentUpdatingBehavior("keyup"){					
						private static final long serialVersionUID = 1L;
						@Override
						protected void onUpdate(AjaxRequestTarget target) {
							//check that the first three digits of the code match the branch code
							check13DigitCodeAgainstBranch(data,target);			
						}					
					});				
					//will validate thirteen digit code once submitted as it takes a bit of time
					//consCode.add(new ConsultantCodeValidator(pageModel.getPreviousMaintainAgreementDTO().getAgreementDTO().getId()));
					addToHomeRowComponentsList(data,consCode,objectProperty);
					return HelperPanel.getInstance(componentId, consCode);
				}
			}.setInitialSize(115));
			
			
			//the effective dates of the role
			cols.add(new SRSDataGridColumn<AgreementHomeRoleDTO>("effectiveFrom",
					new Model("Start Date"), "effectiveFrom", "effectiveFrom",
					getEditState()) {
				private static final long serialVersionUID = 1L;
	
				@Override
				public Panel newCellPanel(WebMarkupContainer parent,
						String componentId, IModel rowModel,
						String objectProperty, EditStateType state,
						final AgreementHomeRoleDTO data) {	
					if (existingHomeRequest || getEditState().isViewOnly() || (data.getRoleID() != 0 && currentHomeRole.equals(data))) {
						return super.newCellPanel(parent, componentId,
								rowModel, objectProperty, state, data);
					}
					TextField startDate = new TextField("value",
							new PropertyModel(data, objectProperty));
					startDate.add(new AttributeModifier("size", "12"));
					startDate.add(new AttributeModifier("maxlength", "10"));
					startDate.add(new AttributeModifier("readonly","true"));
					startDate.setOutputMarkupId(true);
					startDate.setRequired(true);
					startDate.setLabel(new Model("Parent Start Date"));
					startDate.add(new AjaxFormComponentUpdatingBehavior("change") {
						@Override
						protected void onUpdate(AjaxRequestTarget target) {
							setCurrentHomeRole(pageModel.getMaintainAgreementDTO().getAgreementDTO(),target);
							if(data != currentHomeRole){
								adjustCurrentHomeEndDate(target, data);
							}
						}
					});						
					HelperPanel panel = HelperPanel.getInstance(componentId, startDate, true);
					panel.setOutputMarkupId(true);
					addToHomeRowComponentsList(data,panel,"effectiveFrom");
					return panel;
				}
	
			}.setInitialSize(115));
	
			cols.add(new SRSDataGridColumn<AgreementHomeRoleDTO>("effectiveTo",
					new Model("End Date"), "effectiveTo", "effectiveTo", getEditState()) {
	
				@Override
				public Panel newCellPanel(WebMarkupContainer parent,
						String componentId, IModel rowModel,
						String objectProperty, EditStateType state,
						final AgreementHomeRoleDTO data) {
					if (existingHomeRequest || getEditState().isViewOnly()) {
						return super.newCellPanel(parent, componentId,
								rowModel, objectProperty, state, data);
					}
					TextField endDate = new TextField("value",
							new PropertyModel(data, objectProperty));
					endDate.add(new AttributeModifier("size", "12"));
					endDate.add(new AttributeModifier("maxlength", "10"));
					endDate.setLabel(new Model("Parent End Date"));
					endDate.setOutputMarkupId(true);
					endDate.add(new AjaxFormComponentUpdatingBehavior("change") {
						@Override
						protected void onUpdate(AjaxRequestTarget target) {
							//if(data.getEffectiveTo() != null && data.getEffectiveTo().compareTo(new Date()) < current ){
							setCurrentHomeRole(pageModel.getMaintainAgreementDTO().getAgreementDTO(),target);
							//}
							if(data == currentHomeRole){
								adjustFutureHomeStartDate(target);
							}
						}
					});					
					HelperPanel panel = HelperPanel.getInstance(componentId, endDate, true);
					panel.setOutputMarkupId(true);
					addToHomeRowComponentsList(data,panel,"effectiveTo");
					return panel;
				}
	
			}.setInitialSize(115));
			return cols;
		}

	/**
	 * Add a componenet to the row
	 * @param data
	 * @param comp
	 */
	public void addToHomeRowComponentsList(AgreementHomeRoleDTO data,Component comp, String compID){
		if(comp != null){
			getComponentsForHomeGrid(data).put(compID, comp);			
		}
	}

	/**
	 * Update the external refs size
	 * @param data
	 */
	public void updateExternalRefCompSize(final AgreementHomeRoleDTO data, AjaxRequestTarget target){
		Component externalRefComp = getComponentsForHomeGrid(data).get(HOME_EXTERNAL_REF_GRID_OBJECT_PROPERTY_NAME);
		if(externalRefComp != null && externalRefComp instanceof TextField){
			final TextField externalRef = (TextField) externalRefComp;
			String ref = ((ResultPartyDTO)data.getRolePlayerReference()).getExternalReference();
			externalRef.setModelObject(ref);
			externalRef.modelChanged();
			int size = 3;
			if(data.getRolePlayerReference().getTypeOid() == SRSType.BRANCH){
				 size = 3;
			}else if(data.getRolePlayerReference().getTypeOid() == SRSType.UNIT){
				 size = 5;
			}
			final int maxSize = size;
			externalRef.add(new AttributeModifier("size", "" + size));
			externalRef.add(new AttributeModifier("maxlength", "" + size));			
			/* Add behavior to update selected item */
			externalRef.add(new AjaxFormComponentUpdatingBehavior("keyup") {					
				private static final long serialVersionUID = 1L;
				@Override
				protected void onUpdate(AjaxRequestTarget target) {
					String input = (String) externalRef.getModelObject();
					if(input != null && input.length() == maxSize){
						updateHomeSelection(input,data,target);				
					}
				}
			});	
			externalRef.add(new AjaxFormComponentUpdatingBehavior("onblur") {					
				private static final long serialVersionUID = 1L;
				@Override
				protected void onUpdate(AjaxRequestTarget target){
					//make sure code input is valid
					String input = (String) externalRef.getModelObject();
					updateHomeSelection(input,data,target);	
				}						
			});
			if(target != null){
				target.add(externalRef);
			}
		}
	}

	/**
	 * Based on the keys input, this method will update the home node in the home grid
	 * @param target
	 */
	public void updateHomeSelection(String input, AgreementHomeRoleDTO data ,AjaxRequestTarget target){
		AgreementHomeRoleDTO before = (AgreementHomeRoleDTO) SerializationUtils.clone(data);
		List<ResultPartyDTO> nodes = new ArrayList<ResultPartyDTO>(0);
		try {
			nodes = getAgreementGUIController().findHierarchyNodeWithExternalReference(input, null);
		} catch (DataNotFoundException e) {
			//display that code could not be found
			ResultPartyDTO party = (ResultPartyDTO) data.getRolePlayerReference();
			party.setName("Code Not Found");
			party.setOid(0);			
		}
		for(ResultPartyDTO node : nodes){
			if(node.getExternalReference().equalsIgnoreCase(input)){								
				data.setRolePlayerReference(node);
				fillIn13Digitcode(data);
				check13DigitCodeAgainstBranch(data,target);
				setCurrentHomeRole(pageModel.getMaintainAgreementDTO().getAgreementDTO(), target);
				
			}
		}	
		//update only if grid object changed
		if(target != null && ComparatorUtil.compareObjects(before, data).size() != 0){
			target.add(homeGrid);
		}
		
	}

	/**
	 * Will look at the current home end date and ajust the start date of the future home
	 * @param target
	 */
	public void adjustFutureHomeStartDate(AjaxRequestTarget target){
			if(currentHomeRole.getEffectiveTo() != null){
				for(AgreementHomeRoleDTO role : pageModel.getMaintainAgreementDTO().getAgreementDTO().getCurrentAndFutureHomeRoles()){
					if(role != currentHomeRole){
						role.setEffectiveFrom(currentHomeRole.getEffectiveTo());
						Component comp = getComponentsForHomeGrid(role).get("effectiveFrom");
						if(comp != null & comp instanceof HelperPanel && ((HelperPanel)comp).getEnclosedObject() instanceof TextField){
							TextField txt = (TextField)((HelperPanel)comp).getEnclosedObject();					
							txt.setModelObject(role.getEffectiveFrom());
							txt.modelChanged();
							if(target != null){								
								target.add(comp);
								
							}
						}
						break;
					}
				}
			}	
	}

	/**
	 * Will look at the current home end date and ajust the start date of the future home
	 * @param target
	 */
	public void adjustCurrentHomeEndDate(AjaxRequestTarget target, AgreementHomeRoleDTO futureRole){
			if(futureRole != null && futureRole.getEffectiveFrom() != null){
				currentHomeRole.setEffectiveTo(futureRole.getEffectiveFrom());
				if(target != null){							
					//rather update indiv comp
					//update the date comp only		
					Component comp = getComponentsForHomeGrid(currentHomeRole).get("effectiveTo");
					if(comp != null & comp instanceof HelperPanel && ((HelperPanel)comp).getEnclosedObject() instanceof TextField){
						TextField txt = (TextField)((HelperPanel)comp).getEnclosedObject();
						txt.setModelObject(currentHomeRole.getEffectiveTo());
						txt.modelChanged();
						if(target != null){
							target.add(comp);
						}
					}		
				}
			}	
	}

	/**
	 * Create the button to add a home
	 * 
	 * @return
	 */
	public Button createAddHomeButton(String id) {
		final Button button = new Button(id);
		button.add(new AjaxFormComponentUpdatingBehavior("click") {
			private static final long serialVersionUID = 0L;
	
			@Override
			protected void onUpdate(AjaxRequestTarget target) {					
				if (pageModel.getMaintainAgreementDTO().getAgreementDTO().getCurrentAndFutureHomeRoles().size() < 2) {					
					AgreementHomeRoleDTO dto = new AgreementHomeRoleDTO();
					if(pageModel.getMaintainAgreementDTO().getAgreementDTO().getCurrentAndFutureHomeRoles().size() == 0){
						dto.setEffectiveFrom(pageModel.getMaintainAgreementDTO().getAgreementDTO().getStartDate());
					}else if(currentHomeRole != null){
						//set current home end date
						currentHomeRole.setEffectiveTo(new Date());
					}
					dto.setKind(new Long(RoleKindType.HASHOME.getKind()));
					dto.setAgreementRoleKind(new Long(RoleKindType.HASHOME.getKind()));
					dto.setType(SRSType.PARTYAGREEMENTROLE);
					dto.setRolePlayerReference(new ResultPartyDTO());
					dto.setAgreementNumber(pageModel.getMaintainAgreementDTO().getAgreementDTO().getId());
					pageModel.getMaintainAgreementDTO().getAgreementDTO().getCurrentAndFutureHomeRoles().add(dto);	
					adjustFutureHomeStartDate(null);
					target.add(homeGrid);
				} else {
					 error("Only one future home is allowed");
					if (feedBackPanel != null) {
						target.add(feedBackPanel);
					}
				}
			}
		});		
		if(getEditState() == EditStateType.AUTHORISE){			
			button.setVisible(false);
		}
		button.setOutputMarkupId(true);
		button.setOutputMarkupPlaceholderTag(true);
		setUpbutton(button,!existingHomeRequest);
		return button;
	}

	/**
	 * Gest the row componenets for the grid row data object
	 * @param data
	 * @return
	 */
	private HashMap<String,Component> getComponentsForHomeGrid(AgreementHomeRoleDTO data){
		HashMap<String,Component> ret = homeGridComponentMap.get(data);
		if(ret == null){
			ret = new HashMap<String, Component>();
			homeGridComponentMap.put(data, ret);
		}
		return ret;
	}

	protected ModalWindow createSearchWindow(String id) {
	
		ContextSearchPopUp popUp = new ContextSearchPopUp() {
	
			@Override
			public ContextType getContextType() {
				if(currentSearchHomeRole != null){
					return ContextType.PARTY_ORGANISATION_ONLY;
				}else {
					return ContextType.AGREEMENT_ONLY;
				}
			}
	
			@Override
			public void doProcessSelectedItems(AjaxRequestTarget target,
					ArrayList<ResultContextItemDTO> selectedItemList) {
				IAgreementGUIController agreementGUIController = getAgreementGUIController();
				if (selectedItemList.size() == 0) {
					// Nothing was selected
					return;
				}					
				//adding party to agreement
				for (ResultContextItemDTO contextItemDTO : selectedItemList) {	
					ResultPartyDTO resultPartyDTO = contextItemDTO.getPartyDTO();
					if(currentSearchHomeRole != null){											
						if(resultPartyDTO.getTypeOid() == SRSType.PERSON){
							error("Please only select Organisations as Homes");		
							if(feedBackPanel != null){
								target.add(feedBackPanel);
								break;
							}
						}else if(!(resultPartyDTO.getTypeOid() == SRSType.BRANCH 
								|| resultPartyDTO.getTypeOid() == SRSType.UNIT)){
							//remove this if agreements can be on other nodes
							error("Please only select Branchs/Units as Homes");		
							if(feedBackPanel != null){
								target.add(feedBackPanel);
								break;
							}
						}
						
						
						currentSearchHomeRole.setRolePlayerReference(resultPartyDTO);
						setCurrentHomeRole(pageModel.getMaintainAgreementDTO().getAgreementDTO(), target);
						fillIn13Digitcode((AgreementHomeRoleDTO)currentSearchHomeRole);
						check13DigitCodeAgainstBranch((AgreementHomeRoleDTO)currentSearchHomeRole,target);
						if (target != null) {
							target.add(homeGrid);
							if(currentSearchHomeRole instanceof AgreementHomeRoleDTO){
								updateExternalRefCompSize((AgreementHomeRoleDTO)currentSearchHomeRole,target);
								//make sure the dropdown of type knows its model changed
								Component comp = getComponentsForHomeGrid((AgreementHomeRoleDTO)currentSearchHomeRole).get("rolePlayerReference.hierarchyOrganisationTypeName");
								if(comp != null){
									comp.modelChanged();
									target.add(comp);
								}
							}							
						}
						break;
					}else if(currentSearchAgmtRole != null){
						ResultAgreementDTO resultAgmtDTO = contextItemDTO.getAgreementDTO();					
						if(resultAgmtDTO == null || resultAgmtDTO.getOid() < 1){
							 error("Please only select Agreements for agreement roles");		
							if(feedBackPanel != null){
								target.add(feedBackPanel);
								break;
							}
						}else{
							currentSearchAgmtRole.getRole().setRolePlayerReference(resultAgmtDTO);		
							currentSearchAgmtRole.setAgreementParty(resultPartyDTO);
							agreementGUIController.setUpAgreementGridRoleData(currentSearchAgmtRole);
							if (target != null) {
							//	target.add(agreementRoleGrid);
							}
						}
						break;
					}					
				}
				
			}
		};		
		searchWindow = popUp.createModalWindow(id);
//		searchWindow.setPageMapName("homeSearchPageMap");
		return searchWindow;	
	}
	
	/**
	 * Set up the button to be enabled or disabled
	 * @param button
	 */
	private void setUpbutton(Button button, boolean enabled){
		if(!getEditState().isViewOnly() && enabled){
			button.setEnabled(true);
		}else{
			button.setEnabled(false);
		}
	}

	/**
		 * Checks that the first 3 characters match the branch code selected, this is just to popup a warning message
		 * @param role
		 * @param target
		 */
		protected void check13DigitCodeAgainstBranch(AgreementHomeRoleDTO data,AjaxRequestTarget target){
			if(data.getConsultantCode() != null && data.getConsultantCode().length() >= 3 
					&& data.getRolePlayerReference() != null && data.getRolePlayerReference().getOid() > 0){
				//first we check if the selected roleplayer is a branch			
				ResultPartyDTO branch = null;			
				if(((ResultPartyDTO)data.getRolePlayerReference()).getTypeOid() == SRSType.BRANCH){
					branch = ((ResultPartyDTO)data.getRolePlayerReference());
				}else if(((ResultPartyDTO)data.getRolePlayerReference()).getTypeOid() == SRSType.UNIT){
					 try {
						branch = getAgreementGUIController().findParentOfHierarchyNode(data.getRolePlayerReference().getOid(),data.getRolePlayerReference().getTypeOid());
					} catch (DataNotFoundException e) {
						//no problem, warning will not happen
					}
				}			
				if(branch != null && branch.getExternalReference() != null && branch.getExternalReference().length() == 3){
					//currentBranch = partyManager.findParentOfHierarchyNode(party.getOid(),party.getTypeOid());
					String branchCode = data.getConsultantCode().substring(0,3);				
					if(!branchCode.equals(branch.getExternalReference())){
						if(((ResultPartyDTO)data.getRolePlayerReference()).getTypeOid() == SRSType.UNIT){
							 warn("Warning: Unit selected belongs to Branch ["+branch.getName()+"] with code [" +branch.getExternalReference() +"], the first 3 characters entered ["+branchCode+"] does not match this code");
						}else{					
							 warn("Warning: The first 3 characters entered ["+branchCode+"] does not match the branch's code[" +branch.getExternalReference() + "]");
						}
	//					update save to include a confirm box before user can continue
					}
					//always refresh the feedback panel
					if (target != null && feedBackPanel != null) {					
						target.add(feedBackPanel);
					}
				}
			}
		}

	/**
	 * Will check if a home role should have the same 13 digit code
	 * @param homeRole
	 */
	private void fillIn13Digitcode(AgreementHomeRoleDTO homeRole){
		if(currentHomeRole != null && homeRole != null){
			ResultPartyDTO party = (ResultPartyDTO) currentHomeRole.getRolePlayerReference();
			ResultPartyDTO newParty = (ResultPartyDTO) homeRole.getRolePlayerReference();
			if(party != null && newParty != null){
				//check if the branch/unit is the same branch as before
				try {
					//first we check if the two parties are branches
					if(party.getTypeOid() == SRSType.BRANCH && newParty.getTypeOid() == SRSType.BRANCH 
							&& party.getOid() == newParty.getOid()){
						//same branch was selected, duplicated 13 digit code
						homeRole.setConsultantCode(currentHomeRole.getConsultantCode());					
					}else if(party.getTypeOid() == SRSType.BRANCH && newParty.getTypeOid() == SRSType.BRANCH 
							&& party.getOid() != newParty.getOid()){
						//different branches, leave blank					
					}else{	
						//now check if movement in same branch					
						//first get branch of current
						ResultPartyDTO currentBranch = null;
						if(party.getTypeOid() == SRSType.BRANCH){
							//party is branch
							currentBranch = party;
						}else if(party.getTypeOid() == SRSType.UNIT){
							//get the branch of this unit
							currentBranch = getAgreementGUIController().findParentOfHierarchyNode(party.getOid(),party.getTypeOid());
						}else{
							logger.warn("current home is not a branch or unit, check code in this panel at method fillIn13Digitcode");
						}
						//now get branch of new
						ResultPartyDTO newBranch = null;
						if(newParty.getTypeOid() == SRSType.BRANCH){
							//party is branch
							newBranch = newParty;
						}else if(newParty.getTypeOid() == SRSType.UNIT){
							//get the branch of this unit
							newBranch = getAgreementGUIController().findParentOfHierarchyNode(newParty.getOid(),newParty.getTypeOid());
						}else{
							logger.warn("new home is not a branch or unit, check code in this panel at method fillIn13Digitcode");
						}
						if(currentBranch != null && newBranch != null && currentBranch.getOid() == newBranch.getOid()){
							//same branch so 13 digit code canstay the same
							homeRole.setConsultantCode(currentHomeRole.getConsultantCode());
						}						
					}
				} catch (DataNotFoundException e) {
					//leave blank, not a big problem
					e.printStackTrace();
				}
			}
		}
	}
	/**
	 * Get the agreement manager
	 * @return
	 */
	private IAgreementGUIController getAgreementGUIController() {
		if(guiController == null){
			try{
				guiController = ServiceLocator.lookupService(IAgreementGUIController.class);
			} catch (NamingException e) {
					throw new CommunicationException(e);
			}	
		}
		return guiController;
	}

	/**
		 * Go through the home roles and set the current home role<br/>
		 * This will also set all the page labels to refresh if ajax target is not null
		 * @param dto
		 */
		protected void setCurrentHomeRole(AgreementDTO dto, AjaxRequestTarget target){
			//set all objects to blank to display blank		
			if(currentHomeRole != null && currentHomeRole.getRoleID() > 0){
	//			skip if currentHomeRole has already been selected and the current home id is not 0
				return;
			}		
			 Date now = TemporalityHelper.getInstance().getNewNOWDateWithNoTime();
			 currentHomeRole = new AgreementHomeRoleDTO();
			 currentHomeRole.setRolePlayerReference(new ResultPartyDTO());			
			// currentHomeParent = new ResultPartyDTO();			
			// currentHomeManager = new ResultPartyDTO();			
			// currentHomeParentManager = new ResultPartyDTO();
			if(dto.getCurrentAndFutureHomeRoles() != null && dto.getCurrentAndFutureHomeRoles().size() > 0){
				for(AgreementHomeRoleDTO role : dto.getCurrentAndFutureHomeRoles()){
					Date effectiveFrom = DateUtil.getInstance().getDatePart(role.getEffectiveFrom());
					if(effectiveFrom.compareTo(now) <= 0 && (role.getEffectiveTo() == null || role.getEffectiveTo().after(now))){
						//found
						currentHomeRole = role;
						//now we need to set the parent details
						//get the parent
						ResultPartyDTO node = (ResultPartyDTO) role.getRolePlayerReference();
						if(node != null && node.getOid() > 0){
//							try {						
////								currentHomeParent = getAgreementGUIController().findParentOfHierarchyNode(node.getOid(),node.getTypeOid());					
//							} catch (DataNotFoundException e) {
//								//do nothing, will just not be shown
//							}
//							try {						
//	//							currentHomeManager = getAgreementGUIController().findHierarchyNodeManager(node.getOid(),node.getTypeOid());
//							} catch (DataNotFoundException e) {
//								//do nothing, will just not be shown
//							}
//							if(currentHomeParent != null && currentHomeParent.getOid() > 0){
//								try {
////									currentHomeParentManager = getAgreementGUIController().findHierarchyNodeManager(currentHomeParent.getOid(),currentHomeParent.getTypeOid());
//								} catch (DataNotFoundException e) {
//									//do nothing, will just not be shown
//								}
//							}
						}
					}
				}
			}
			if(target != null){			
				String home = "";
				String parent = "";
				if(currentHomeRole != null && currentHomeRole.getRolePlayerReference() != null && currentHomeRole.getRolePlayerReference() instanceof ResultPartyDTO
						&& currentHomeRole.getRolePlayerReference().getOid() > 0){
					ResultPartyDTO homeParty = (ResultPartyDTO)currentHomeRole.getRolePlayerReference();
					home = homeParty.getName() + " (" + homeParty.getHierarchyOrganisationTypeName()+ " " +homeParty.getExternalReference()+")";	
				}	
//				if(currentHomeParent != null && currentHomeParent.getOid() > 0){
//					parent = currentHomeParent.getName() + " (" + currentHomeParent.getHierarchyOrganisationTypeName()+ " " +currentHomeParent.getExternalReference()+")";	
//				}
				
//				Label homeTypeLabel1 = (Label) new Label("currentHome",home).setOutputMarkupId(true).setOutputMarkupPlaceholderTag(true);
//				Label homeManagerLabel1 = (Label)new Label("currentHomeManager",new PropertyModel(currentHomeManager,"name")).setOutputMarkupId(true).setOutputMarkupPlaceholderTag(true);
//				Label homeParentTypeLabel1 = (Label)new Label("currentHomeParent",parent).setOutputMarkupId(true).setOutputMarkupPlaceholderTag(true);			
//				Label homeParentManagerLabel1 = (Label)new Label("currentHomeParentManager",new PropertyModel(currentHomeParentManager,"name")).setOutputMarkupId(true).setOutputMarkupPlaceholderTag(true);
//				homeTypeLabel.replaceWith(homeTypeLabel1);
//				homeTypeLabel = homeTypeLabel1;			
//				homeManagerLabel.replaceWith(homeManagerLabel1);
//				homeManagerLabel = homeManagerLabel1;
//				homeParentTypeLabel.replaceWith(homeParentTypeLabel1);
//				homeParentTypeLabel = homeParentTypeLabel1;			
//				homeParentManagerLabel.replaceWith(homeParentManagerLabel1);
//				homeParentManagerLabel = homeParentManagerLabel1;			
//				target.add(homeTypeLabel);
//				target.add(homeManagerLabel);
//				target.add(homeParentTypeLabel);
//				target.add(homeParentManagerLabel);			
			}
		}
		
	

		public Class getPanelClass() {
			
			return AbstractHomeRoleFactory.class;
		}	
		
		/**
		 * Create the button to show the home history for this node
		 * 
		 * @return
		 */
		
		
}
