package za.co.liberty.web.pages.admin;

import java.util.ArrayList;
import java.util.List;

import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import za.co.liberty.business.guicontrollers.admin.ISegmentNameGUIController;
import za.co.liberty.dto.rating.SegmentDTO;
import za.co.liberty.dto.rating.SegmentNameDTO;
import za.co.liberty.exceptions.data.DataNotFoundException;
import za.co.liberty.exceptions.data.ValidationException;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.helpers.lookup.ServiceLocator;
import za.co.liberty.interfaces.rating.difffactor.SegmentContextType;
import za.co.liberty.web.pages.MaintenanceBasePage;
import za.co.liberty.web.pages.admin.models.SegmentModel;
import za.co.liberty.web.pages.panels.AbstractTableMaintenanceSelectionPanel;
import za.co.liberty.web.pages.panels.DefaultMaintenanceSelectionPanel;
import za.co.liberty.web.wicket.markup.html.form.SRSAbstractChoiceRenderer;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGridColumn;

import com.inmethod.grid.IGridColumn;
import com.inmethod.grid.column.PropertyColumn;

/**
 * Admin page used to maintain segments. 
 *
 */
public class SegmentGuiPage extends MaintenanceBasePage<Object> {

	private static final long serialVersionUID = 1L;
	
	private SegmentModel pageModel;
	private transient ISegmentNameGUIController guiController;
	private static final Logger logger = Logger.getLogger(SegmentGuiPage.class);
	
	public SegmentGuiPage(){
		super(null);
	}
	
	public SegmentGuiPage(SegmentDTO e){
		super(e);
	}
	
	public Panel createContainerPanel() {
		Panel panel = null;
		if (pageModel.getSelectedItem() == null) {
			panel = new EmptyPanel(CONTAINER_PANEL_NAME);
		} else {
			panel = new SegmentAdminPanel(CONTAINER_PANEL_NAME, getEditState(), pageModel,this);
		}
		panel.setOutputMarkupId(true);
		return panel;
	}
	
	
	
	@Override
	public Button[] createNavigationalButtons() {
		return new Button[] {createSaveButton("button1"), createCancelButton("button2") };

	}
	
	@Override
	public void doSave_onSubmit() {
		SegmentDTO newDto = null;
		try {
		   if (pageModel.getSelectedItem().getId() == null) {
			  newDto = getSessionBean().addSegment(pageModel.getSelectedItem());
		    }else{
		  	  newDto = getSessionBean().updateSegment(pageModel.getSelectedItem());
		    }
			invalidatePage();		
			this.info("Record was saved successfully");
			setResponsePage(new SegmentGuiPage(newDto));
		
		}catch (RuntimeException e){
			logger.info("Excpetion in Segment update or add:" + e.getMessage());
		} catch (ValidationException e) {
			for (String err : e.getErrorMessages()) {
				this.error("Error:" + err);
			}	
		}
	}
	
	
	
	@SuppressWarnings("unchecked")
	@Override
	public DefaultMaintenanceSelectionPanel createSelectionPanel() {
		
		return new AbstractTableMaintenanceSelectionPanel<SegmentDTO>(SELECTION_PANEL_NAME,"Segment Name:",pageModel, this, 
					selectionForm, SegmentDTO.class) {
			
			private static final long serialVersionUID = -2623730454856120154L;

			@Override
			protected IChoiceRenderer getChoiceRenderer() {
				return new SRSAbstractChoiceRenderer<Object>() {

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
				list.add(new PropertyColumn(new Model("Segment Name"), "segmentNameObject.segmentName", null).setInitialSize(100));
				list.add(new PropertyColumn(new Model("Segment Context Type"), "segmentContextType", null).setInitialSize(150));
				list.add(new PropertyColumn(new Model("Segment Context Id"), "segmentContextId", null).setInitialSize(150));
				
				list.add(new SRSDataGridColumn<SegmentDTO>("RiskAQC",
						new Model("Risk Aqc"),"riskAqc",getEditState()).setInitialSize(100));
				list.add(new SRSDataGridColumn<SegmentDTO>("InvAQC",
						new Model("Inv Aqc"),"invAqc",getEditState()).setInitialSize(100));
				list.add(new SRSDataGridColumn<SegmentDTO>("ELMRiskAQC",
						new Model("ELM Risk"),"elmRiskAqc",getEditState()).setInitialSize(100));
				list.add(new SRSDataGridColumn<SegmentDTO>("ELMInvAQC",
						new Model("ELM Invest"),"elmInvAqc",getEditState()).setInitialSize(100));

				list.add(new SRSDataGridColumn<SegmentDTO>("StartDate",
						new Model("Start date"),"startDate",getEditState()).setInitialSize(80));
				list.add(new SRSDataGridColumn<SegmentDTO>("EndDate",
						new Model("End Date"),"endDate",getEditState()).setInitialSize(80));
				return list;
			}
			
			@Override
			public List<Object> filterTableData() {
				if(selectedObject != null) {
					return (List)getSessionBean().findAllSegmentsForSegmentNameList(((SegmentNameDTO)selectedObject).getId());
				}
				return new ArrayList<Object>();
			}

			
			
			@Override
			public List getSelectionList() {
				// Return the segment names for the combo list
				return getSessionBean().findAllSegmentNamesList();
			}

			@Override
			protected IModel getSelectedItemModel() {
				return super.getSelectedItemModel();
			}
			
			
								
		};
	}
	
	@Override
	public Object initialisePageModel(Object obj, Object pageModelExtraValueObject) {
	
		SegmentModel object = new SegmentModel();
		ISegmentNameGUIController sessionBean = getSessionBean();
		object.setAllAvailableSegmentNameList(sessionBean.findAllSegmentNamesList());
		
		object.setSelectedItem((SegmentDTO) obj);
		
		SegmentContextType[] segTypeArray = SegmentContextType.values();
		List<SegmentContextType> segmentContextTypeList = new ArrayList<SegmentContextType>();
		for(SegmentContextType aqcProdTyp : segTypeArray){
			segmentContextTypeList.add(aqcProdTyp);
		}
		
		pageModel = object;

		return pageModel;
	}

	@Override
	public String getPageName() {
		return "Segment Maintenance";
	}
	
	protected ISegmentNameGUIController getSessionBean() {
		if (guiController == null) {
			try {
				guiController = ServiceLocator.lookupService(ISegmentNameGUIController.class);
			} catch (NamingException namingErr) {
				logger.error(this.getPageName()
						+ " ISegmentNameGuiController can not be lookedup:"
						+ namingErr.getMessage());
				CommunicationException comm = new CommunicationException("ISegmentNameGuiController can not be looked up!");
				throw new CommunicationException(comm);
			} 
		}
		return guiController;
	}

	
}
