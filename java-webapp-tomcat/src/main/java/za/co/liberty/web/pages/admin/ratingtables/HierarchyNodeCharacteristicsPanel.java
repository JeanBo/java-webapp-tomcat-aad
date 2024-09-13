package za.co.liberty.web.pages.admin.ratingtables;


import javax.naming.NamingException;

import org.apache.commons.lang.SerializationUtils;
import org.apache.wicket.Page;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

import za.co.liberty.business.guicontrollers.ratingtable.IMIRatingTableGUIController;
import za.co.liberty.dto.rating.BranchTypeDTO;
import za.co.liberty.dto.rating.DescriptionDTO;
import za.co.liberty.dto.rating.HierarchyNodeCharacteristicsDTO;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.helpers.lookup.ServiceLocator;
import za.co.liberty.interfaces.rating.IGuiRatingRow;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.pages.admin.models.RatingTablePageModel;
import za.co.liberty.web.pages.panels.BasePanel;
import za.co.liberty.web.wicket.markup.html.form.SRSAbstractChoiceRenderer;
import za.co.liberty.web.wicket.markup.html.form.SRSTextField;
/**
 * Main filter panel for Rating table admin.  Shows a filter section, table and a selection panel.
 *
 */
public class HierarchyNodeCharacteristicsPanel extends BasePanel {

	private static final long serialVersionUID = 9032746185200994039L;
	private transient IMIRatingTableGUIController guiController;
	
	
	private SRSTextField  oidLabel = null;
	private SRSTextField  branchTypeId = null;
	private DropDownChoice  channelGroupChoice = null;
	private DropDownChoice  superDivisionChoice = null;
	private DropDownChoice  branchTypeChoice = null;	
	private DropDownChoice  miDivisionChoice = null;
	
	RatingTablePageModel pageModel;
	
	public HierarchyNodeCharacteristicsPanel(String id, EditStateType editState,
			RatingTablePageModel pageModel, Page parentPage) {
		super(id, editState, parentPage);
		this.pageModel = pageModel; 
		initialise(); 
		setAllLists();
	}
	
	private void initialise(){
		add(oidLabel = createOIDField("oid"));
		add(branchTypeChoice = createBranchTypeField("branchType"));
		add(createMiDivisionField("miDivision"));
		//add(miDivisionChoice = createMiDivisionField("miDivision"));
		add(channelGroupChoice = createChannelGroupField("channelGroup"));
		add(superDivisionChoice = createSuperDivisionField("superDivision"));
		if(getEditState()!= EditStateType.ADD){
			IGuiRatingRow beforeImage = (IGuiRatingRow)SerializationUtils.clone((HierarchyNodeCharacteristicsDTO)pageModel.getSelectionRow());
			pageModel.setGuiRatingRowBeforeImage(beforeImage);
		}
	}
	
	private void setAllLists(){
		if(pageModel.getAllBranchTypes().isEmpty()){
			pageModel.getAllBranchTypes().addAll(getGUIController().geBranchTypes());
			pageModel.getAllOrganisationExternalType().addAll(getGUIController().getOrganisationTypes());
		}
		if(pageModel.getHierarchyNodeLists().isEmpty()){
			pageModel.getHierarchyNodeLists().addAll(getGUIController().getAllLists());
			pageModel.getAllMIDivisions().addAll(getGUIController().getMIDivisions());
			pageModel.getAllSuperDivisions().addAll(getGUIController().getSuperDivisions());
			pageModel.getAllChannelGroups().addAll(getGUIController().getChannelGroups());
		}
	}
	
	private SRSTextField createOIDField(String id){
		SRSTextField tempSRSTextField = new SRSTextField(id,new PropertyModel(pageModel.getSelectionRow(),"oid" ));
		tempSRSTextField.setEnabled(false);
		tempSRSTextField.setRequired(true);
		return tempSRSTextField;     
	}
	
	private DropDownChoice createSuperDivisionField(String id){
		DropDownChoice field = new DropDownChoice(id, new PropertyModel(pageModel.getSelectionRow(), "superDivision"), 
				pageModel.getAllSuperDivisions(), 
		new SRSAbstractChoiceRenderer<Object>() {
			private static final long serialVersionUID = -4367276358153378234L;
			
			public Object getDisplayValue(Object value) {
				return (value==null)?null:((DescriptionDTO)value).getDescription();
			}
			public String getIdValue(Object arg0, int arg1) {
					return (arg0 == null) ? null : ((DescriptionDTO)arg0).getDescription()+"";
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
	
	private DropDownChoice createChannelGroupField(String id){
		DropDownChoice field = new DropDownChoice(id, new PropertyModel(pageModel.getSelectionRow(), "channelGroup"),pageModel.getAllChannelGroups(), 
		new SRSAbstractChoiceRenderer<Object>() {
			private static final long serialVersionUID = -4367276358153378234L;
			
			public Object getDisplayValue(Object value) {
				return (value==null)?null:((DescriptionDTO)value).getDescription();
			}
			public String getIdValue(Object arg0, int arg1) {
				return (arg0 == null) ? null : ((DescriptionDTO)arg0).getDescription();
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
	private DropDownChoice createMiDivisionField(String id){
		IModel model = new IModel() {
			private static final long serialVersionUID = 1L;
			
			public Object getObject() {
				return ((HierarchyNodeCharacteristicsDTO) pageModel.getSelectionRow()).getMiDivision();
			}
			public void setObject(Object arg0) {
				((HierarchyNodeCharacteristicsDTO) pageModel.getSelectionRow()).setMiDivision((DescriptionDTO)arg0);
			}
			public void detach() {	
			}
		};
		
		DropDownChoice field = new DropDownChoice(id, model, pageModel.getAllMIDivisions(), 
				new SRSAbstractChoiceRenderer<Object>() {
					private static final long serialVersionUID = -4367276358153378234L;
					
					public Object getDisplayValue(Object value) {
						return (value==null)?null:((DescriptionDTO)value).getDescription();
					}
					public String getIdValue(Object arg0, int arg1) {
						return ((DescriptionDTO)arg0).getDescription()+"";
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
	
	private DropDownChoice createBranchTypeField(String id){
		IModel model = new IModel() {
			private static final long serialVersionUID = 1L;
			
			public Object getObject() {
				return ((HierarchyNodeCharacteristicsDTO) pageModel.getSelectionRow()).getBranchType();
			}
			public void setObject(Object arg0) {
				((HierarchyNodeCharacteristicsDTO) pageModel.getSelectionRow()).setBranchType((BranchTypeDTO)arg0);
			}
			public void detach() {	
			}
		};
		
		DropDownChoice field = new DropDownChoice(id, model, pageModel.getAllBranchTypes(), 
				new SRSAbstractChoiceRenderer<Object>() {
					private static final long serialVersionUID = -4367276358153378234L;
					
					public Object getDisplayValue(Object value) {
						return (value==null)?null:((BranchTypeDTO)value).getName();
					}
					public String getIdValue(Object arg0, int arg1) {
						return ((BranchTypeDTO)arg0).getValue()+"";
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