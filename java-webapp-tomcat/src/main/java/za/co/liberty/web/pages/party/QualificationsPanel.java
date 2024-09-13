package za.co.liberty.web.pages.party;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import com.inmethod.grid.DataProviderAdapter;
import com.inmethod.grid.IGridColumn;
import com.inmethod.grid.SizeUnit;
import com.inmethod.grid.column.AbstractColumn;

import za.co.liberty.dto.party.QualificationDTO;
import za.co.liberty.interfaces.agreements.requests.RequestKindType;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.pages.interfaces.ISecurityPanel;
import za.co.liberty.web.pages.panels.BasePanel;
import za.co.liberty.web.pages.panels.HelperPanel;
import za.co.liberty.web.pages.party.model.QualificationsPanelModel;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGrid;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGridColumn;
import za.co.liberty.web.wicket.markup.html.grid.SRSGridRowSelectionCheckBox;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGrid.GridSizeUnit;
import za.co.liberty.web.wicket.markup.repeater.data.ListDataProvider;

/**
 * Panel containing a person's qualification details
 *
 */
public class QualificationsPanel extends BasePanel implements ISecurityPanel {

	private static final long serialVersionUID = 1L;
	
	@SuppressWarnings("unused")
	private FeedbackPanel feedBackPanel;
	
	//private QualificationsPanelModel panelModel;
	
	@SuppressWarnings("unused")
	private SRSDataGrid qualificationsGrid;
	
	@SuppressWarnings("unused")
	private Form qualificationsForm;
	
	@SuppressWarnings("unused")
	private QualificationsPanelModel panelModel;
	
	@SuppressWarnings("unused")
	private List<QualificationDTO> qualifications = new ArrayList<QualificationDTO>();
	
	
	public QualificationsPanel(String id, EditStateType editState, Page parentPage) {
		super(id, editState, parentPage);
	}
	
	public QualificationsPanel(String id, List<QualificationDTO> qualificationsDetails ,EditStateType editState,
			FeedbackPanel feedBackPanel, boolean agreementCreation, Page parentPage) {
		this(id, qualificationsDetails ,editState, feedBackPanel,false,agreementCreation,parentPage);
	}

	public QualificationsPanel(String id, List<QualificationDTO> qualificationsDetails ,EditStateType editState, FeedbackPanel feedBackPanel, 
			boolean includeSecureSelection, boolean agreementCreation, Page parentPage){
		super(id,editState,parentPage);
		this.feedBackPanel = feedBackPanel;	
	
		panelModel = new QualificationsPanelModel(qualificationsDetails);
		//initPanelModel();				
		//init();
	}
	
	public Class getPanelClass() {
		return null;
	}
	
	public class QualificationsForm extends Form {
		
		private static final long serialVersionUID = 1L;
		List<QualificationDTO> nonSelectable = new ArrayList<QualificationDTO>();
		
		public QualificationsForm(String id) {
			super(id);
			
			qualificationsGrid = new SRSDataGrid(id, new DataProviderAdapter(new ListDataProvider<QualificationDTO>(qualifications)),
												 getQualificationColumns(),getEditState(), nonSelectable);		
			qualificationsGrid.setCleanSelectionOnPageChange(false);
			qualificationsGrid.setClickRowToSelect(false);
			qualificationsGrid.setAllowSelectMultiple(true);
			qualificationsGrid.setGridWidth(99, GridSizeUnit.PERCENTAGE);
			qualificationsGrid.setRowsPerPage(5);
			qualificationsGrid.setContentHeight(100, SizeUnit.PX);
			qualificationsGrid.setAutoCalculateTableHeight(getEditState().isViewOnly());
					
			this.add(qualificationsGrid);
			
		}
		
	}
	
	/**
	 * Initialize the panel
	 *
	 */
	/*private void init(){
		this.add(qualificationsForm = createContactForm("contactForm"));
	}*/
	
	
	/**
	 * Get the columns for the address grid
	 * @return
	 */
	private List<IGridColumn> getQualificationColumns() {
		Vector<IGridColumn> cols = new Vector<IGridColumn>(7);		
		/*if (!getEditState().isViewOnly()) {
			SRSGridRowSelectionCheckBox col = new SRSGridRowSelectionCheckBox(
					"checkBox");
			cols.add(col.setInitialSize(30));
		}
		//all cols display only, add button will bring up popup
		cols.add(new SRSDataGridColumn<AddressDTO>(
				"usage", new Model("Usage"), "usage",
				"usage", getEditState()).setInitialSize(70));
		//add in defualt selection for business types
		cols.add(new SRSDataGridColumn<AddressDTO>(
				"defaultAddress", new Model("Default"), "defaultAddress",
				"defaultAddress", getEditState()){
		private static final long serialVersionUID = 1L;

		@Override
		public Panel newCellPanel(WebMarkupContainer parent, String componentId, IModel rowModel, String objectProperty, EditStateType state, final AddressDTO data) {
			if(getEditState().isViewOnly() && data.getUsage() != UsageType.BUSINESS){
				Label lab = new Label("value","");
				return HelperPanel.getInstance(componentId, lab);
			}else{				
				//business type so give radio to select default
				CheckBox check = new CheckBox("value",new PropertyModel(data,objectProperty));
				check.add(new AjaxFormComponentUpdatingBehavior("click"){
					private static final long serialVersionUID = 1L;

					@Override
					protected void onUpdate(AjaxRequestTarget target) {
						//update the value
						//must also remove other selections and update grid
						if(panelModel.getAddresses().size() > 1){
							for(AddressDTO address : panelModel.getAddresses()){
								if(address != data){
									address.setDefaultAddress(false);
								}
							}
							if(target != null){
								target.addComponent(addressGrid);
							}
						}
					}					
				});
				if(getEditState().isViewOnly()){
					check.setEnabled(false);
				}
				HelperPanel radioPanel = HelperPanel.getInstance(componentId, check);
				return radioPanel;
			}
		}		
			
		}.setInitialSize(30));
		
		cols.add(new SRSDataGridColumn<AddressDTO>(
				"type", new Model("Type"), "type",
				"type", getEditState()).setInitialSize(100));
		
		cols.add(new SRSDataGridColumn<AddressDTO>(
				"isPostal", new Model("IsPostal"), "isPostal",
				"isPostal", getEditState()){
				private static final long serialVersionUID = 1L;
				@Override
				public Panel newCellPanel(WebMarkupContainer parent, String componentId, IModel rowModel, String objectProperty, EditStateType state, AddressDTO data) {
					CheckBox check = new CheckBox("value",new PropertyModel(data,objectProperty));
					check.setEnabled(false);
					return HelperPanel.getInstance(componentId, check);
				}			
		}.setInitialSize(40));
		
		SRSDataGridColumn col = new SRSDataGridColumn<AddressDTO>(
				"description", new Model("Description"), "description",
				"description", getEditState());
		
		if(getEditState() == EditStateType.ADD){
			col.setInitialSize(150);
		}else{
			col.setInitialSize(250);
		}
		cols.add(col);			
		cols.add(new SRSDataGridColumn<AddressDTO>(
				"effectiveFrom", new Model("Start Date"), "effectiveFrom",
				"effectiveFrom", getEditState()).setInitialSize(100));
		cols.add(new SRSDataGridColumn<AddressDTO>(
				"effectiveTo", new Model("End Date"), "effectiveTo",
				"effectiveTo", getEditState()).setInitialSize(100));
		if(!getEditState().isViewOnly()){
			//add edit button
			cols.add(new AbstractColumn("edit", new Model("Edit")){				
				private static final long serialVersionUID = 1L;
				@Override
				public Component newCell(WebMarkupContainer parent, String componentId, IModel rowModel) {
					final AddressDTO address = (AddressDTO) rowModel.getObject();
					if(address.getUsage() == UsageType.SECURE 
							&& (!ContactDetailsPanel.this.includeSecureSelection || outstandingSecureRequest)){
						return new EmptyPanel(componentId);
					}else{				
					//if(address.getOid() != 0){
						Button searchButton = new Button("value", new Model("Edit"));	
						searchButton.add(new AjaxFormComponentUpdatingBehavior("click"){									
							private static final long serialVersionUID = 1L;
							@Override
							protected void onUpdate(AjaxRequestTarget target) {
								currentAddressDTO = address;
								addressPopup.show(target);										
							}									
						});
						return HelperPanel.getInstance(componentId,searchButton);	
					//}else{
					//	return new EmptyPanel(componentId);
					//}		
					}
				}				
			}.setInitialSize(50));
		}*/
		return cols;
		
	}


}
