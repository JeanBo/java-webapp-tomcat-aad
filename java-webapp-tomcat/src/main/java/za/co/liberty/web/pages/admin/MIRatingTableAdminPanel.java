package za.co.liberty.web.pages.admin;


import java.util.Arrays;

import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.model.PropertyModel;

import za.co.liberty.business.guicontrollers.admin.ISegmentNameGUIController;
import za.co.liberty.dto.rating.MIRatingTableNameDTO;
import za.co.liberty.dto.rating.SegmentNameDTO;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.helpers.lookup.ServiceLocator;
import za.co.liberty.interfaces.rating.difffactor.SegmentContextType;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.pages.MaintenanceBasePage;
import za.co.liberty.web.pages.MaintenanceBasePage.SelectionForm;
import za.co.liberty.web.pages.admin.models.RatingTablePageModel;
import za.co.liberty.web.pages.admin.models.SegmentModel;
import za.co.liberty.web.pages.panels.BasePanel;
import za.co.liberty.web.wicket.markup.html.form.SRSTextField;
/**
 * Panel used by Segment page. Panel will consist of all the fields needed for the modification or creation of segments
 * @author JWV2310
 *
 */
@SuppressWarnings("unused")
public class MIRatingTableAdminPanel extends BasePanel {

	private static final long serialVersionUID = 1L;
	
	private static final Logger logger = Logger.getLogger(MIRatingTableAdminPanel.class);
	private RatingTablePageModel pageModel = null;
    private DropDownChoice  segmentNameChoiceField = null;
	private DropDownChoice segmentTypeChoiceField;     
	private SRSTextField  segmentIDTextField = null;
	private SRSTextField  riskTextField = null;
	private SRSTextField  investTextField = null;
	private SRSTextField  elmRiskTextField = null;
	private SRSTextField  elmInvestTextField = null;      
	
	private CheckBox endDateCheckBox = null;
	private Button searchButton;
	private transient ISegmentNameGUIController guiController;

	
	@SuppressWarnings("deprecation")
	public MIRatingTableAdminPanel(String id, EditStateType editState){
		super (id, editState);
	}
	
	@SuppressWarnings("unchecked")
	public MIRatingTableAdminPanel(String id, EditStateType editState, RatingTablePageModel pageModel, MaintenanceBasePage parentPage) {
		super(id, editState,parentPage);
		this.pageModel = pageModel;
		initialise();
	}
	
	

	protected SelectionForm selectionForm;
	public EditStateType pageEditState;
	private ModalWindow createSearchWindow;
	
	
	
	
	private void initialise(){
//		add(segmentNameChoiceField = createSegmentNameField("segmentName"));
		add(segmentTypeChoiceField = createSegmentTypeField("segmentContextType"));
		add(segmentIDTextField = createSegmentContextIDField("segmentContextID"));
		
		add(riskTextField = createRiskField("riskCode"));
		add(investTextField = createInvestField("investCode"));
		add(elmRiskTextField = createElmRiskField("elmRiskCode"));
		add(elmInvestTextField = createElmInvestField("elmInvestCode"));
//		add(endDateCheckBox = createEndDateCheck("endDate"));

	}
	
	
	
	private CheckBox createEndDateCheck(String id){
				
		endDateCheckBox = new CheckBox(id, new PropertyModel(pageModel.getSelectedItem() ,"endDateActive" )){
			  private static final long serialVersionUID = 1L;
			
		};
		endDateCheckBox.add(new AjaxFormComponentUpdatingBehavior("click"){
			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
			}
			
		});
		if(getEditState() == EditStateType.ADD){
			endDateCheckBox.setVisible(false);
		}
		endDateCheckBox.setEnabled(!getEditState().isViewOnly());
		return endDateCheckBox;
	}

//	/**
//	 * Create the name field
//	 * 
//	 * @param id
//	 * @return
//	 */
//	private DropDownChoice createSegmentNameField(String id){
//
//		DropDownChoice field = new DropDownChoice(id, new PropertyModel(pageModel.getSelectedItem(), "segmentNameObject"), 
//				pageModel.getAllAvailableTableNameList(), 
//		new IChoiceRenderer() {
//			private static final long serialVersionUID = -4367276358153378234L;
//			
//			public Object getDisplayValue(Object value) {
//				return (value==null)?null:((MIRatingTableNameDTO)value).getTableName();
//			}
//			public String getIdValue(Object arg0, int arg1) {
//				return (arg0 == null) ? null : ""+((MIRatingTableNameDTO)arg0).getId();
//			}
//			
//		});
//		
//		field.setNullValid(true);
//		field.setEnabled(!getEditState().isViewOnly());
//		if(getEditState()== EditStateType.MODIFY){
//			field.setEnabled(false);
//		}
//		field.setRequired(true);
//		return field;
//	}
	
	/**
	 * Create the segment type field
	 * @param id
	 * @return
	 */
	private DropDownChoice createSegmentTypeField(String id){
		DropDownChoice tempDropDown = new DropDownChoice(id, new PropertyModel(pageModel.getSelectedItem(), "segmentContextType"), 
				Arrays.asList(SegmentContextType.values()));
		tempDropDown.setEnabled(!getEditState().isViewOnly());
		if(getEditState()== EditStateType.MODIFY){
			tempDropDown.setEnabled(false);
		}
		return tempDropDown;
	}
	
	/**
	 * Create the context id field
	 * 
	 * @param id
	 * @return
	 */
	private SRSTextField createSegmentContextIDField(String id){
		SRSTextField tempSRSTextField = new SRSTextField(id,new PropertyModel(pageModel.getSelectedItem(),"segmentContextId" ));
		tempSRSTextField.setEnabled(!getEditState().isViewOnly());
		tempSRSTextField.setRequired(true);
		if(getEditState()== EditStateType.MODIFY){
			tempSRSTextField.setEnabled(false);
		}
		return tempSRSTextField;
	}
	
	/**
	 * Create the ELM Risk field
	 * 
	 * @param id
	 * @return
	 */
	private SRSTextField createElmRiskField(String id){
		SRSTextField tempSRSTextField = new SRSTextField(id,new PropertyModel(pageModel.getSelectedItem(),"elmRiskAqc" ));
		tempSRSTextField.setEnabled(!getEditState().isViewOnly());
		tempSRSTextField.setRequired(true);
		return tempSRSTextField;
	}
	
	/**
	 * Create the ELM invest field
	 * 
	 * @param id
	 * @return
	 */
	private SRSTextField createElmInvestField(String id){
		SRSTextField tempSRSTextField = new SRSTextField(id,new PropertyModel(pageModel.getSelectedItem(),"elmInvAqc" ));
		tempSRSTextField.setEnabled(!getEditState().isViewOnly());
		tempSRSTextField.setRequired(true);
		return tempSRSTextField;
	}

	/**
	 * Create the risk field
	 * 
	 * @param id
	 * @return
	 */
	private SRSTextField createRiskField(String id){
		SRSTextField tempSRSTextField = new SRSTextField(id,new PropertyModel(pageModel.getSelectedItem(),"riskAqc" ));
		tempSRSTextField.setEnabled(!getEditState().isViewOnly());
		tempSRSTextField.setRequired(true);
		return tempSRSTextField;
	}
	
	/**
	 * Create the investment field
	 * @param id
	 * @return
	 */
	private SRSTextField createInvestField(String id){
		SRSTextField tempSRSTextField = new SRSTextField(id,new PropertyModel(pageModel.getSelectedItem(),"invAqc" ));
		tempSRSTextField.setEnabled(!getEditState().isViewOnly());
		tempSRSTextField.setRequired(true);
		return tempSRSTextField;
	}

	public String getPageName() {
		return "Segment Maintenance panel";
	}
	
	protected ISegmentNameGUIController getSessionBean() {
		if (guiController == null) {
			try {
				guiController = ServiceLocator.lookupService(ISegmentNameGUIController.class);
			} catch (NamingException namingErr) {
				logger.error(this.getPageName()
						+ " Segment Admin Panel Gui Controller can not be looked up:"
						+ namingErr.getMessage());
				CommunicationException comm = new CommunicationException("ISegmentNameGuiController for segment panel can not be looked up!");
				throw new CommunicationException(comm);
			} 
		}
		return guiController;
	}
	
	
}
