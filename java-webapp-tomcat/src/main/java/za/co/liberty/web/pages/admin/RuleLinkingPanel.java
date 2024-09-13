package za.co.liberty.web.pages.admin;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.datetime.StyleDateConverter;
import org.apache.wicket.datetime.markup.html.form.DateTextField;
import org.apache.wicket.extensions.yui.calendar.DatePicker;
//import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import za.co.liberty.dto.userprofiles.RuleDTO;
import za.co.liberty.dto.userprofiles.RuleDataDTO;
import za.co.liberty.dto.userprofiles.RunnableRuleDTO;
import za.co.liberty.interfaces.rules.ArithmeticType;
import za.co.liberty.interfaces.rules.RuleDataType;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.data.pages.IMaintenancePageModel;
import za.co.liberty.web.pages.admin.models.RolesModel;
import za.co.liberty.web.pages.interfaces.IStatefullComponent;
import za.co.liberty.web.pages.panels.AbstractLinkingPanel;
import za.co.liberty.web.pages.panels.HelperPanel;
import za.co.liberty.web.wicket.markup.html.form.SRSDateField;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGridColumn;

import com.inmethod.grid.IGridColumn;

/**
 * <p>
 * This panel is used to display rule items (in a table) that are linked to a
 * parent object. It allows links to be added or removed as well as the display &
 * modification of the linked objects. A list of available items that can be
 * added are provided and by default it will only show items that are not
 * already linked.
 * </p>
 * 
 * @author jzb0608 - 08 May 2008 Modified by Dean Scott to use
 *         AbstractLinkingPanel 24 July 2008
 * 
 */
public abstract class RuleLinkingPanel<MODEL extends IMaintenancePageModel>
		extends AbstractLinkingPanel<MODEL, RuleDTO, RunnableRuleDTO> implements
		IStatefullComponent {
	
	//private SimpleDateFormat dteFormat = new SimpleDateFormat("dd/MM/yyyy");

	/* Constants */
	private static final long serialVersionUID = -8003453537906825676L;

	/**
	 * Default constructor
	 * 
	 */
	public RuleLinkingPanel(String id, MODEL pageModel, EditStateType editState) {
		super(id, pageModel, editState, new ChoiceRenderer("ruleDescription",
				"ruleID"));		
	}

	/**
	 * Return the rule data value field
	 * 
	 * @param id
	 * @param dto
	 * @return
	 */
	private Component createRuleDataValueField(String id, RunnableRuleDTO dto) {
		RuleDTO original = getFullOriginalLinkedItemMap().get(dto.getRuleID());
		/*
		 * Below done due to the decision that only one RuleDataDTO is required
		 * per rule
		 */
		dto.setHasRuleData((dto.isHasRuleData() || original != null
				&& original.isHasRuleData()));
		RuleDataDTO dataDTO = dto.getRuleDataDTO();
		if (editState == EditStateType.VIEW || dto.getProfileRoleDTO() != null
				|| (original != null && (!original.isHasRuleData() || !original.isRuleDataRetreivedBySystem()))) {
			return HelperPanel.getInstance(id, new Label("value", new Model(
					(dataDTO == null) ? "" : dataDTO.getRuleDataValue())));
		}
		
		
		if (dataDTO==null) {
			/* Show ruledata - is requried */
			dataDTO = new RuleDataDTO(dto);
			dto.setRuleDataDTO(dataDTO);
		}
		TextField text = null;
//		try {
			text = (TextField) new TextField("value", new PropertyModel(
							dataDTO, "ruleDataValue"),
//							Class.forName(dto.getDataTypeDefault().getClazzName()
							dto.getDataTypeDefault().getClassType()).setRequired(true);
//		} catch (ClassNotFoundException e) {
//			// only problem here is no validation takes place
//			//e.printStackTrace();
//			text = (TextField) new TextField("value", new PropertyModel(
//							dataDTO, "ruleDataValue")).setRequired(true);
//		}
		
		
		final String ruleDecrip = dto.getRuleDescription();
		
//			/**
//			 * Validate that the rule Data Is a Number
//			 */		
//			IValidator numberValidator = new IValidator() {
//				private static final long serialVersionUID = 1L;
//	
//				// @SuppressWarnings("static-access")
//				public void validate(IValidatable val) {
//					
//					try {
//						String value = (String) val.getValue();
//						System.err.println("value1 = " + value);
//						value = value.replace(",", "");		
//						System.err.println("value2 = " + value);
//						double dblValue = Long.parseLong(value);
//					} catch (Exception e) {
//						e.printStackTrace();
//						IValidationError err = new ValidationError().setMessage("Compare Value for Rule : " + ruleDecrip + " - must be a number");
//						val.error(err);
//					}
//				}
//			};
//			text.add(numberValidator);
//		}
		text.add(new AttributeModifier("size","15"));//text.add(new SimpleAttributeModifier("size","15"));
		text.setRequired(true);
		text.setLabel(new Model("Compare Value for Rule : " + ruleDecrip));
		text.add(new AjaxFormComponentUpdatingBehavior("change"){			
			private static final long serialVersionUID = 1L;
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				
			}			
		});
		return HelperPanel.getInstance(id, text);
	}
	
	/**
	 * Return the rule data type list field
	 * 
	 * @param string
	 * @return
	 */
	private Panel createDescriptionField(String id, RunnableRuleDTO dto) {
		RuleDTO original = getFullOriginalLinkedItemMap().get(dto.getRuleID());
		/*
		 * Below done due to the decision that only one RuleDataDTO is required
		 * per rule
		 */
		dto.setHasRuleData((dto.isHasRuleData() || original != null
				&& original.isHasRuleData()));
		RuleDataDTO dataDTO = dto.getRuleDataDTO();
		if (editState == EditStateType.VIEW || dto.getProfileRoleDTO() != null
				|| (original != null && !original.isHasRuleData())) {
			String str = (dataDTO == null) ? "" : dataDTO.getRuleDataDescription();
			return HelperPanel.getInstance(id, new Label("value",
					new Model(str)));
		}
		
		if (dataDTO==null) {
			/* Show ruledata - is requried */
			dataDTO = new RuleDataDTO(dto);
			dto.setRuleDataDTO(dataDTO);
		}
		TextField descripField = new TextField("value",new PropertyModel(dataDTO,"ruleDataDescription"));	
		descripField.add(new AttributeModifier("size","40"));//descripField.add(new SimpleAttributeModifier("size","40"));
		descripField.setLabel(new Model("Description for Rule : " + dto.getRuleDescription()));
		descripField.setRequired(true);
		descripField.setEnabled((editState != EditStateType.VIEW));
		descripField.add(new AjaxFormComponentUpdatingBehavior("change"){			
			private static final long serialVersionUID = 1L;
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				
			}			
		});
		return HelperPanel.getInstance(id, descripField);
	}
	
	/**
	 * Return the rule data type list field
	 * 
	 * @param string
	 * @return
	 */
	@SuppressWarnings("unused")
	private Panel createDataTypeListField(String id, RunnableRuleDTO dto) {
		RuleDTO original = getFullOriginalLinkedItemMap().get(dto.getRuleID());
		/*
		 * Below done due to the decision that only one RuleDataDTO is required
		 * per rule
		 */
		dto.setHasRuleData((dto.isHasRuleData() || original != null
				&& original.isHasRuleData()));
		RuleDataDTO dataDTO = dto.getRuleDataDTO();
		
		if (editState == EditStateType.VIEW || dto.getProfileRoleDTO() != null
				|| (original != null && !original.isHasRuleData())) {
			String str = (dataDTO == null) ? "" : dataDTO.getRuleDataType().getDescription();
			return HelperPanel.getInstance(id, new Label("value",
					new Model(str)));
		}
		
		if (dataDTO==null) {
			/* Show ruledata - is requried */
			dataDTO = new RuleDataDTO(dto);
			dto.setRuleDataDTO(dataDTO);
		}
		List<RuleDataType> dataList = getRuleDataTypeList();
		DropDownChoice list = new DropDownChoice("value", new PropertyModel(
				dataDTO, "ruleDataType"), dataList, new ChoiceRenderer(
				"description", "description"));
		//just want the choose one size to be the standard
		list.add(new AttributeModifier("style","width: 126; max-width: 126"));//list.add(new SimpleAttributeModifier("style","width: 126; max-width: 126"));
		list.setLabel(new Model("Rule Data Type for Rule : " + dto.getRuleDescription()));
		list.setRequired(true);	
		list.setEnabled((editState != EditStateType.VIEW));
		return HelperPanel.getInstance(id, list);
	}


	/**
	 * Return the arithmetic list field
	 * 
	 * @param string
	 * @return
	 */
	@SuppressWarnings("unused")
	private Panel createArithmeticListField(String id, RunnableRuleDTO dto) {
		RuleDTO original = getFullOriginalLinkedItemMap().get(dto.getRuleID());
		/*
		 * Below done due to the decision that only one RuleDataDTO is required
		 * per rule
		 */
		dto.setHasRuleData((dto.isHasRuleData() || original != null
				&& original.isHasRuleData()));
		RuleDataDTO dataDTO = dto.getRuleDataDTO();
		if (editState == EditStateType.VIEW || dto.getProfileRoleDTO() != null
				|| (original != null && !original.isHasRuleData())) {
			
			String str = (dataDTO == null) ? "" : dataDTO.getRuleArithmeticType().toString();
			return HelperPanel.getInstance(id, new Label("value",
					new Model(str)));
		}
		
		if (dataDTO==null) {
			/* Show ruledata - is requried */
			dataDTO = new RuleDataDTO(dto);
			dto.setRuleDataDTO(dataDTO);
		}
		List<ArithmeticType> arithList = getRuleArithmeticList();
		DropDownChoice list = new DropDownChoice("value", new PropertyModel(
				dataDTO, "ruleArithmeticType"), arithList, new ChoiceRenderer(
				"description"));
		list.add(new AttributeModifier("style","width: 126; max-width: 126"));//list.add(new SimpleAttributeModifier("style","width: 126; max-width: 126"));
		list.setLabel(new Model("Rule Arithmetic Type for Rule : " + dto.getRuleDescription()));
		list.setRequired(true);
		list.setEnabled((editState != EditStateType.VIEW));
		return HelperPanel.getInstance(id, list);
	}

	protected abstract List<ArithmeticType> getRuleArithmeticList();
	
	protected abstract List<RuleDataType> getRuleDataTypeList();

	/**
	 * Check whether a menu item already exists from a role
	 * 
	 * @return
	 */
	private boolean isDuplicateMenuItem(RunnableRuleDTO item) {
		List<RunnableRuleDTO> ruleItems = this.getNotSelectableAdditionalLinkedItemList();
		if (ruleItems != null)
			for (RunnableRuleDTO extraItem : ruleItems) {
				if (item.getProfileRoleDTO() == null
						&& extraItem.getRuleID() == item.getRuleID()) {
					return true;
				}
			}
		return false;
	}

	/**
	 * Added by Dean(DZS2610) 21 July 2008
	 */
	@Override
	protected List<IGridColumn> getLinkedItemGridColumns() {		
		List<IGridColumn> columns = new ArrayList<IGridColumn>();

		// add the rule description label panel
		columns.add(new SRSDataGridColumn<RunnableRuleDTO>("ruleName",
				new Model("Rule Name"), "ruleName", "ruleName",
				editState) {
			private static final long serialVersionUID = 1L;

			@Override
			public Panel newCellPanel(WebMarkupContainer parent,
					String componentId, IModel rowModel, String objectProperty,
					EditStateType state, RunnableRuleDTO data) {
				Label lab = new Label("value", new PropertyModel(data,
						objectProperty));
				if (isDuplicateMenuItem(data)) {
					lab.add(new AttributeModifier("class", "red"));//lab.add(new SimpleAttributeModifier("class", "red"));
					lab.add(new AttributeModifier("title",//lab.add(new SimpleAttributeModifier("title",
									"Please note: This rule already exists in a currently selected role"));
				}
				return HelperPanel.getInstance(componentId, lab);
			}
		}.setInitialSize(160));
		
		//Only display the permission source if it is not in the role screen
		if(!(pageModel instanceof RolesModel)){
			columns.add(new SRSDataGridColumn<RunnableRuleDTO>("permissionSource",
				new Model("Permission source"), "permissionSource",
				"permissionSource", editState).setInitialSize(150));
		}
		
			//create description column
			columns.add(new SRSDataGridColumn<RunnableRuleDTO>("ruleDescrip",
				new Model("Description"), "ruleDataDTOSet[0].ruleDescription", editState) {
			private static final long serialVersionUID = 1L;

			@Override
			public Panel newCellPanel(WebMarkupContainer parent,
					String componentId, IModel rowModel, String objectProperty,
					EditStateType state, RunnableRuleDTO data) {				
				Panel panel = createDescriptionField(componentId, data);
				RuleDTO original = getFullOriginalLinkedItemMap().get(
						data.getRuleID());				
				panel = (Panel) panel.setVisible((original != null) ? original.isHasRuleData() : false);				
				return panel;
			}

			}.setInitialSize(270));		
			
		//create data type column
//		columns.add(new SRSDataGridColumn<RunnableRuleDTO>("ruleDataType",
//				new Model("Data Type"), "ruleDataDTOSet[0].ruleDataType", editState) {
//			private static final long serialVersionUID = 1L;
//
//			@Override
//			public Panel newCellPanel(WebMarkupContainer parent,
//					String componentId, IModel rowModel, String objectProperty,
//					EditStateType state, RunnableRuleDTO data) {				
//				Panel panel = createDataTypeListField(componentId, data);
//				RuleDTO original = getFullOriginalItemMap().get(
//						data.getRuleID());
//				panel = (Panel) panel.setVisible(original.isHasRuleData());
//				// boolean isEditable = state != EditStateType.VIEW;
//				return panel;
//			}
//
//		}.setInitialSize(140));		
	
			/* Dean  Taken out as it was decided that only a default arithmetic type should be used */
		// add arithmetic values
//		columns.add(new SRSDataGridColumn<RunnableRuleDTO>("ruleArithmetic",
//				new Model("Compare Type"), "ruleDataDTOSet[0].ruleArithmetic", editState) {
//			private static final long serialVersionUID = 1L;
//
//			@Override
//			public Panel newCellPanel(WebMarkupContainer parent,
//					String componentId, IModel rowModel, String objectProperty,
//					EditStateType state, RunnableRuleDTO data) {				
//				Panel panel = createArithmeticListField(componentId, data);
//				RuleDTO original = getFullOriginalItemMap().get(
//						data.getRuleID());
//				panel = (Panel) panel.setVisible(original.isHasRuleData());
//				// boolean isEditable = state != EditStateType.VIEW;
//				return panel;
//			}
//
//		}.setInitialSize(140));

		// add the rule data value cell
		columns.add(new SRSDataGridColumn<RunnableRuleDTO>("dataValue",
				new Model("Compare Value"), "dataValue", editState) {
			private static final long serialVersionUID = 1L;

			@Override
			public Panel newCellPanel(WebMarkupContainer parent,
					String componentId, IModel rowModel, String objectProperty,
					EditStateType state, RunnableRuleDTO data) {
				RuleDTO original = getFullOriginalLinkedItemMap().get(
						data.getRuleID());
				Panel panel = (Panel) createRuleDataValueField(componentId,data)
				.setVisible((original != null) ? original.isHasRuleData() : false);
				return panel;
			}

		}.setInitialSize(120));
		
		//add effective from column
		columns.add(new SRSDataGridColumn<RunnableRuleDTO>("effectiveFrom",new Model("Effective From"),"effectiveTo","effectiveTo", editState){
				private static final long serialVersionUID = 1L;
				@Override
				public Panel newCellPanel(WebMarkupContainer parent, String componentId, IModel rowModel, String objectProperty, EditStateType state, RunnableRuleDTO data) {
					Panel panel = (Panel)createEffectiveFromComponent(componentId, data);
					return panel;
				}	    		   
	    }.setInitialSize(110));		
		
		//add effective to column
		columns.add(new SRSDataGridColumn<RunnableRuleDTO>("effectiveTo",new Model("End Date"),"effectiveTo","effectiveTo", editState){
				private static final long serialVersionUID = 1L;
				@Override
				public Panel newCellPanel(WebMarkupContainer parent, String componentId, IModel rowModel, String objectProperty, EditStateType state, RunnableRuleDTO data) {
					Panel panel = (Panel)createEffectiveToComponent(componentId, data);
					return panel;
				}	    		   
	    	   }.setInitialSize(110));
		return columns;
	}
	
	/**
	 * Return the effective from data value field
	 * 
	 * @param id
	 * @param dto
	 * @return
	 */
	private Component createEffectiveFromComponent(String id, RunnableRuleDTO dto) {
		/*
		 * Below done due to the decision that only one RuleDataDTO is required
		 * per rule
		 */
		RuleDataDTO dataDTO = dto.getRuleDataDTO();
		if (editState == EditStateType.VIEW) {
			return HelperPanel.getInstance(id, new Label("value", new Model(
					(dataDTO == null) ? ((dto.getEffectiveFrom() != null) ? dto.getEffectiveFrom() : "") : dataDTO.getEffectiveFrom())));
		}
		//MSK#Change :Commented TextField and used DateTextField
		DateTextField effectiveFromDateSelection = null;
		//TextField effectiveFromDateSelection = null;
		
		//effectiveFromDateSelection = DateTextField.forDatePattern("value", new PropertyModel(dataDTO,"effectiveFrom"),"dd/MM/yyyy");
		if(dataDTO != null){
			if(dataDTO.getEffectiveFrom() == null){
				dataDTO.setEffectiveFrom(new Date());
			}
			//MSK#Change:commented SRSDateField and used own Impl
			//effectiveFromDateSelection = new SRSDateField("value",new PropertyModel(dataDTO,"effectiveFrom"));
			
			  effectiveFromDateSelection = getDateTextField(null, dataDTO);		 
		}else{
			if(dto.getEffectiveFrom() == null){
				dto.setEffectiveFrom(new Date());
			}
			//MSK#Change:commented SRSDateField and used own Impl
			//effectiveFromDateSelection = new SRSDateField("value",new PropertyModel(dto,"effectiveFrom"));
			
			effectiveFromDateSelection = getDateTextField(dto, null);
		}
		effectiveFromDateSelection.add(new AttributeModifier("maxlength","10"));//effectiveFromDateSelection.add(new SimpleAttributeModifier("maxlength","10"));
		effectiveFromDateSelection.add(new AttributeModifier("style","width:65px"));//effectiveFromDateSelection.add(new SimpleAttributeModifier("style","width:85px"));		
		effectiveFromDateSelection.setRequired(true);
		effectiveFromDateSelection.setLabel(new Model("Effective From Date for Rule: " + dto.getRuleDescription()));
		//endDateSelection.add(new SimpleAttributeModifier("editable","false"));
//MSK#Change	added below Date pick logic	
		        effectiveFromDateSelection.add(getDatePicker());
//MSK#Change End		
		effectiveFromDateSelection.add(new AjaxFormComponentUpdatingBehavior("change"){			
			private static final long serialVersionUID = 1L;
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				
			}			
		});
		return HelperPanel.getInstance(id, effectiveFromDateSelection, true);
	}
	
	/**
	 * Return the effective from data value field
	 * 
	 * @param id
	 * @param dto
	 * @return
	 */
	private Component createEffectiveToComponent(String id, RunnableRuleDTO dto) {
		/*
		 * Below done due to the decision that only one RuleDataDTO is required
		 * per rule
		 */
		RuleDataDTO dataDTO = dto.getRuleDataDTO();
		if (editState == EditStateType.VIEW) {
			return HelperPanel.getInstance(id, new Label("value", new Model(
					(dataDTO == null) ? ((dto.getEffectiveTo() != null) ? dto.getEffectiveTo() : "") : dataDTO.getEffectiveTo())));
		}
		//MSK#Change :Commented TextField and used DateTextField
				DateTextField effectiveToDateSelection = null;
		//TextField effectiveToDateSelection = null;
		if(dataDTO != null){
			//MSK#Change : comented SRSDateField and used DateTextField
			//effectiveToDateSelection = new SRSDateField("value",new PropertyModel(dataDTO,"effectiveTo"));
			effectiveToDateSelection= getDateTextField(null, dataDTO);
		}else{
		//MSK#Change : comented SRSDateField and used DateTextField		
			//effectiveToDateSelection = new SRSDateField("value",new PropertyModel(dto,"effectiveTo"));
			effectiveToDateSelection =  getDateTextField(dto, null);
		}
		effectiveToDateSelection.add(new AttributeModifier("maxlength","10"));//effectiveToDateSelection.add(new SimpleAttributeModifier("maxlength","10"));
		effectiveToDateSelection.add(new AttributeModifier("style","width:65px"));//effectiveToDateSelection.add(new SimpleAttributeModifier("style","width:85px"));
		//endDateSelection.add(new SimpleAttributeModifier("editable","false"));
//MSK#Change : Intro own DatePicker Impl
		        effectiveToDateSelection.add(getDatePicker());
//MSK#Change End		
		effectiveToDateSelection.add(new AjaxFormComponentUpdatingBehavior("change"){			
			private static final long serialVersionUID = 1L;
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				
			}			
		});
		return HelperPanel.getInstance(id, effectiveToDateSelection, true);
	}

	@Override
	protected RunnableRuleDTO createNewLinkedItem(RuleDTO dto) {
		RunnableRuleDTO newDto = new RunnableRuleDTO();
		newDto.setRuleID(dto.getRuleID());
		newDto.setRuleName(dto.getRuleName());
		newDto.setRuleDescription(dto.getRuleDescription());
		newDto.setArithmeticDefault(dto.getArithmeticDefault());
		newDto.setDataTypeDefault(dto.getDataTypeDefault());
		
		System.out.println("CreateNewDTO  ruleName="+dto.getRuleName()
				+ " ,arithDefault="+dto.getArithmeticDefault()
				+ " ,dataTypeDefault="+dto.getDataTypeDefault());
		
		return newDto;
	}

	@Override
	protected Object getKeyForAvailableItem(RuleDTO item) {
		return item.getRuleID();
	}

	@Override
	protected Object getKeyForLinkedItem(RunnableRuleDTO item) {
		return item.getRuleID();
	}

	/**
	 * Get comparator for sorting the linked items
	 * 
	 * @return
	 */
	protected Comparator<? super RunnableRuleDTO> getLinkedItemComparator() {
		return getAvailableItemComparator();
	}

	/**
	 * Get comparator for sorting the available items
	 * 
	 * @return
	 */
	protected Comparator<? super RuleDTO> getAvailableItemComparator() {
		return new Comparator<RuleDTO>() {
			public int compare(RuleDTO o1, RuleDTO o2) {
				return o1.getRuleName().compareTo(
						o2.getRuleName());
			}
		};
	}
	
	
	/**
	 * Get list of all available items
	 * 
	 * @return
	 */
	protected abstract List<RuleDTO> getCompleteAvailableItemList();

	@Override
	protected List<RunnableRuleDTO> getCurrentlyLinkedItemList() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getGridWidth() {	
		if(this.editState != EditStateType.VIEW){
			this.grid.setAutoResize(false);
		}		
		else{
			this.grid.setAutoResize(true);
		}
		return 800;
	}
	
	private DateTextField getDateTextField(RunnableRuleDTO dto,RuleDataDTO dataDTO) {
		
		if(dataDTO!=null) {
			return new DateTextField("value",new
					  PropertyModel(dataDTO,"effectiveFrom"),new StyleDateConverter("S-", true)) {
					  @Override public Locale getLocale() { return new Locale("en"); } };
		}
		return new DateTextField("value",new
				  PropertyModel(dto,"effectiveFrom"),new StyleDateConverter("S-", true)) {
				  @Override public Locale getLocale() { return new Locale("en"); } };
	}
	
	private DatePicker getDatePicker() {
		 DatePicker datePicker = new DatePicker()
	        {
	            @Override
	            protected String getAdditionalJavaScript()
	            {
	                return "${calendar}.cfg.setProperty(\"navigator\",true,false); ${calendar}.render();";
	            }
	        };
	        
	        datePicker.setShowOnFieldClick(true);
	        datePicker.setAutoHide(true);
	        
	        return datePicker;
	}
	
	
}
