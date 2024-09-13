package za.co.liberty.web.pages.admin.ratingtables;


import javax.naming.NamingException;

import org.apache.commons.lang.SerializationUtils;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import za.co.liberty.business.guicontrollers.ratingtable.IMIRatingTableGUIController;
import za.co.liberty.dto.rating.HierarchyAddressClassificationDTO;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.helpers.lookup.ServiceLocator;
import za.co.liberty.interfaces.rating.IGuiRatingRow;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.pages.admin.models.RatingTablePageModel;
import za.co.liberty.web.pages.hierarchy.MiRegionChoicesType;
import za.co.liberty.web.pages.panels.BasePanel;
import za.co.liberty.web.wicket.markup.html.form.SRSAbstractChoiceRenderer;
import za.co.liberty.web.wicket.markup.html.form.SRSTextField;
/**
 * Main filter panel for Rating table admin.  Shows a filter section, table and a selection panel.
 * 
 * @author rxs1408
 *
 */
public class HierarchyAddressClassificationPanel extends BasePanel {

	private static final long serialVersionUID = 9032746185200994039L;	
	private transient IMIRatingTableGUIController guiController;
	protected static final String YES = "Y";
	protected static final String NO = "N";
	
	RatingTablePageModel pageModel;
	
	public HierarchyAddressClassificationPanel(String id, EditStateType editState,
			RatingTablePageModel pageModel, Page parentPage) {
		super(id, editState, parentPage);
		this.pageModel = pageModel; 
		initialise(); 
		setAllLists();
	}
	
	private void initialise(){
		add(createMetroIndicator("metroIndicator"));	
		add(createSuburbTextField("suburb"));
		add(createTownField("town"));
		add(createProvinceField("province"));
		add(createProvinceCodeField("provinceCode"));
		add(createMainTownField("mainTown"));
		add(createLibertyAreaField("libertyArea"));
	
		if(getEditState()!= EditStateType.ADD){
			IGuiRatingRow beforeImage = (IGuiRatingRow)SerializationUtils.clone((HierarchyAddressClassificationDTO)pageModel.getSelectionRow());
			pageModel.setGuiRatingRowBeforeImage(beforeImage);
		}
		
	}
	
	private void setAllLists(){
		if(pageModel.getHierarchyClassificationLists().isEmpty()){
			pageModel.getHierarchyClassificationLists().addAll(getGUIController().getAllClassificationLists());
			
			pageModel.getTowns().addAll(getGUIController().getTowns(pageModel.getHierarchyClassificationLists()));
			pageModel.getMainTowns().addAll(getGUIController().getMainTowns(pageModel.getHierarchyClassificationLists()));
			pageModel.getProvinces().addAll(getGUIController().getProvinces(pageModel.getHierarchyClassificationLists()));
			pageModel.getProvinceCodes().addAll(getGUIController().getProvinceCodes(pageModel.getHierarchyClassificationLists()));
			pageModel.getLibertyAreas().addAll(getGUIController().getLibertyAreas(pageModel.getHierarchyClassificationLists()));
		}			
	}
	
	private SRSTextField createSuburbTextField(String id){
		SRSTextField tempSRSTextField = new SRSTextField(id,new PropertyModel(pageModel.getSelectionRow(),"suburb" ));
		tempSRSTextField.setEnabled(!getEditState().isViewOnly());
		tempSRSTextField.setRequired(true);
		return tempSRSTextField;
	}
	
	private RadioGroup createMetroIndicator(String id) {

		IModel radioModel = new IModel<String>() {
		
			private static final long serialVersionUID = 1L;
			String miRegionChoicesType;
		
			public String getObject() {
				if(null != getDataModelForPanel().getOutlying() && getDataModelForPanel().getOutlying().equals("Y")) {
					miRegionChoicesType = MiRegionChoicesType.OUTLYING.name();
				}else if(null != getDataModelForPanel().getMetro() && getDataModelForPanel().getMetro().equals("Y")){
					miRegionChoicesType = MiRegionChoicesType.METRO.name();
				}
				return miRegionChoicesType;
			}

			public void setObject(String arg0) {
				miRegionChoicesType = arg0;				
			}
			public void detach() {				
			}			
		};
		
		RadioGroup<String> metroIndicator = new RadioGroup<String>(
				"metroIndicatorGroup", radioModel);	
		
		metroIndicator.add(new Radio("outlying", new Model<String>(MiRegionChoicesType.OUTLYING.name())).add(new AjaxEventBehavior("click") {			
			@Override
			protected void onEvent(AjaxRequestTarget arg0) {
				getDataModelForPanel().setMetroIndicator(MiRegionChoicesType.OUTLYING.name());
				getDataModelForPanel().setMetro(NO);
				getDataModelForPanel().setOutlying(YES);
			}
		}));
		
		metroIndicator.add(new Radio("metro", new Model<String>(MiRegionChoicesType.METRO.name())).add(new AjaxEventBehavior("click") {
			
			@Override
			protected void onEvent(AjaxRequestTarget arg0) {
				getDataModelForPanel().setMetroIndicator(MiRegionChoicesType.METRO.name());
				getDataModelForPanel().setMetro(YES);
				getDataModelForPanel().setOutlying(NO);
			}
		}));		
		metroIndicator.setEnabled(!getEditState().isViewOnly());
		metroIndicator.setOutputMarkupId(true);
		return metroIndicator;
	}

	private DropDownChoice createSuburbField(String id){
		DropDownChoice field = new DropDownChoice(id, new PropertyModel(pageModel.getSelectionRow(), "suburb"), 
				pageModel.getSuburbs(), 
		new SRSAbstractChoiceRenderer<Object>() {
			private static final long serialVersionUID = -4367276358153378234L;
			
			public Object getDisplayValue(Object value) {
				return (value==null)?null:((String)value);
			}
			public String getIdValue(Object arg0, int arg1) {
				if(arg0 instanceof HierarchyAddressClassificationDTO){
					return (arg0 == null) ? null : ""+(String)arg0;
				}
				return (arg0 == null) ? null : ""+((String)arg0);
			}
			
		});
		field.setNullValid(true);
		field.setEnabled(!getEditState().isViewOnly());
		if(getEditState()== EditStateType.MODIFY){
			field.setEnabled(true);
		}
		field.setRequired(true);
		return field;
	}

	private DropDownChoice createTownField(String id){
		DropDownChoice field = new DropDownChoice(id, new PropertyModel(pageModel.getSelectionRow(), "town"), 
				pageModel.getTowns(), 
		new SRSAbstractChoiceRenderer<Object>() {
			private static final long serialVersionUID = -4367276358153378234L;
			
			public Object getDisplayValue(Object value) {
				return (value==null)?null:((String)value);
			}
			public String getIdValue(Object arg0, int arg1) {
				if(arg0 instanceof HierarchyAddressClassificationDTO){
					return (arg0 == null) ? null : ""+(String)arg0;
				}
				return (arg0 == null) ? null : ""+((String)arg0);
			}
			
		});
		field.setNullValid(true);
		field.setEnabled(!getEditState().isViewOnly());
		if(getEditState()== EditStateType.MODIFY){
			field.setEnabled(true);
		}
		field.setRequired(true);
		return field;
	}
	
	private DropDownChoice createMainTownField(String id){
		DropDownChoice field = new DropDownChoice(id, new PropertyModel(pageModel.getSelectionRow(), "mainTown"), 
				pageModel.getMainTowns(), 
		new SRSAbstractChoiceRenderer<Object>() {
			private static final long serialVersionUID = -4367276358153378234L;
			
			public Object getDisplayValue(Object value) {
				return (value==null)?null:(String)value;
			}
			public String getIdValue(Object arg0, int arg1) {
				if(arg0 instanceof HierarchyAddressClassificationDTO){
					return (arg0 == null) ? null : ""+(String)arg0;
				}
				return (arg0 == null) ? null : ""+((String)arg0);
			}
			
		});
		field.setNullValid(true);
		field.setEnabled(!getEditState().isViewOnly());
		if(getEditState()== EditStateType.MODIFY){
			field.setEnabled(true);
		}
		field.setRequired(true);
		return field;
	}
		
	private DropDownChoice createProvinceField(String id){
		DropDownChoice field = new DropDownChoice(id, new PropertyModel(pageModel.getSelectionRow(), "province"), 
				pageModel.getProvinces(), 
		new SRSAbstractChoiceRenderer<Object>() {
			private static final long serialVersionUID = -4367276358153378234L;
			
			public Object getDisplayValue(Object value) {
				return (value==null)?null:(String)value;
			}
			public String getIdValue(Object arg0, int arg1) {
				if(arg0 instanceof HierarchyAddressClassificationDTO){
					return (arg0 == null) ? null : ""+(String)arg0;
				}
				return (arg0 == null) ? null : ""+((String)arg0);
			}
			
		});
		field.setNullValid(true);
		field.setEnabled(!getEditState().isViewOnly());
		if(getEditState()== EditStateType.MODIFY){
			field.setEnabled(true);
		}
		field.setRequired(true);
		return field;
	}
	
	private DropDownChoice createProvinceCodeField(String id){
		DropDownChoice field = new DropDownChoice(id, new PropertyModel(pageModel.getSelectionRow(), "provinceCode"), 
				pageModel.getProvinceCodes(), 
		new SRSAbstractChoiceRenderer<Object>() {
			private static final long serialVersionUID = -4367276358153378234L;
			
			public Object getDisplayValue(Object value) {
				return (value==null)?null:(String)value;
			}
			public String getIdValue(Object arg0, int arg1) {
				if(arg0 instanceof HierarchyAddressClassificationDTO){
					return (arg0 == null) ? null : ""+(String)arg0;
				}
				return (arg0 == null) ? null : ""+((String)arg0);
			}
			
		});
		field.setNullValid(true);
		field.setEnabled(!getEditState().isViewOnly());
		if(getEditState()== EditStateType.MODIFY){
			field.setEnabled(true);
		}
		//field.setRequired(true);
		return field;
	}
	
	private DropDownChoice createLibertyAreaField(String id){
		DropDownChoice field = new DropDownChoice(id, new PropertyModel(pageModel.getSelectionRow(), "libertyArea"), 
				pageModel.getLibertyAreas(), 
		new SRSAbstractChoiceRenderer<Object>() {
			private static final long serialVersionUID = -4367276358153378234L;
			
			public Object getDisplayValue(Object value) {
				return (value==null)?null:(String)value;
			}
			public String getIdValue(Object arg0, int arg1) {
				if(arg0 instanceof HierarchyAddressClassificationDTO){
					return (arg0 == null) ? null : ""+(String)arg0;
				}
				return (arg0 == null) ? null : ""+((String)arg0);
			}
			
		});
		field.setNullValid(true);
		field.setEnabled(!getEditState().isViewOnly());
		if(getEditState()== EditStateType.MODIFY){
			field.setEnabled(true);
		}
		//field.setRequired(true);
		return field;
	}
	
	private HierarchyAddressClassificationDTO getDataModelForPanel(){
		return (HierarchyAddressClassificationDTO)pageModel.getSelectionRow();
	}
	
	protected IMIRatingTableGUIController getGUIController() {
		if (guiController == null) {
			try {
				guiController = ServiceLocator.lookupService(IMIRatingTableGUIController.class);
			} catch (NamingException namingErr) {
				CommunicationException comm = new CommunicationException("IMIRatingTableGUIController can not be looked up!");
				throw new CommunicationException(comm);
			} 
		}
		return guiController;
	}
}

