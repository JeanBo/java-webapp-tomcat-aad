package za.co.liberty.web.pages.maintainagreement;

import java.util.ArrayList;
import java.util.List;

import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import com.inmethod.grid.DataProviderAdapter;
import com.inmethod.grid.IGridColumn;
import com.inmethod.grid.SizeUnit;

import za.co.liberty.business.guicontrollers.agreement.IAgreementGUIController;
import za.co.liberty.dto.agreement.AgreementDTO;
import za.co.liberty.dto.gui.templates.DistributionKindGroupRatesDTO;
import za.co.liberty.helpers.lookup.ServiceLocator;
import za.co.liberty.persistence.rating.entity.Description;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.pages.maintainagreement.model.DistributionKindGroupDetailsPanelModel;
import za.co.liberty.web.pages.panels.BasePanel;
import za.co.liberty.web.pages.panels.HelperPanel;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGrid;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGrid.GridSizeUnit;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGridColumn;
import za.co.liberty.web.wicket.markup.repeater.data.ListDataProvider;
/**
 * This class represents the page form to be added to the panel
 * @author mzl2611
 */
public class DistributionKindGroupDetailsPanel extends BasePanel{

	private DistributionKindGroupDetailsPanelModel panelModel;
	
	private AgreementDTO viewTemplateContext;
	
	private Button selectTemplateButton;
	
	private Button cancelSelectTemplateButton;
	
	private boolean initialised;

	private transient IAgreementGUIController guiController;
	
	private SRSDataGrid distributionKindGroupDetailsTable;
	
	private List<IGridColumn> distributionKindGroupDetailsColumns;
	
	protected ArrayList<DistributionKindGroupRatesDTO> selectedItemList = 
		new ArrayList<DistributionKindGroupRatesDTO>();
	
	private List<Description> scheduleDescriptions ;
	
	
	private transient Logger logger;

	private EditStateType editState;

	private ModalWindow modalWindow;

	DistributionKindGroupDetailsPanel(String id, EditStateType editState, DistributionKindGroupDetailsPanelModel panelModel, ModalWindow modalWindow, DistributionKindGroupPage distributionKindGroupPage) {
		
		super(id, editState, distributionKindGroupPage);
		
		this.panelModel = panelModel;
		
		this.modalWindow = modalWindow;
		
		scheduleDescriptions = getGuiController().getScheduleDescriptions();
		
		this.add(new DistributionKindGroupDetailsForm("distributionKindGroupDetailsForm"));
		
	}	
	
	/**
	 * 
	 * @author MZL2611
	 *
	 */
	public class DistributionKindGroupDetailsForm extends Form {
		private static final long serialVersionUID = 5808296649559984427L;

		public DistributionKindGroupDetailsForm(String id) {
			super(id);
			this.add(getDistributionKindGroupDetailsTable());
		}
	}
	
	
	/**
	 * Load the AgreementGUIController dynamically if it is null as this is a transient variable.
	 * @return {@link IAgreementGUIController}
	 */
	private IAgreementGUIController getGuiController() {
		if (guiController==null) {
			try {
				guiController = ServiceLocator.lookupService(IAgreementGUIController.class);
			} catch (NamingException e) {
				getLogger().fatal("Could not lookup AgreementGUIController",e);
			}
		}
		return guiController;
	}
	
	private Logger getLogger() {
		if (logger == null) {
			logger = Logger.getLogger(this.getClass());
		}
		return logger;
	}	

	@Override
	protected void onBeforeRender() {
		super.onBeforeRender();
		if (!initialised) {
			if (getLogger().isDebugEnabled()) {
				getLogger().debug("Adding components to the page on first render");
			}
			initialised=true;
		}
	}
	
	private SRSDataGrid getDistributionKindGroupDetailsTable() {
		if (distributionKindGroupDetailsTable == null) {
			distributionKindGroupDetailsTable = new SRSDataGrid("distributionDetailsTable",
					new DataProviderAdapter(new ListDataProvider<DistributionKindGroupRatesDTO>(panelModel.getDistributionKindGroupDTOs())),
					getDistributionKindGroupDetailsTableColumns(),EditStateType.VIEW);
			distributionKindGroupDetailsTable.setOutputMarkupId(true);
			distributionKindGroupDetailsTable.setCleanSelectionOnPageChange(false);
			distributionKindGroupDetailsTable.setAllowSelectMultiple(false);
			distributionKindGroupDetailsTable.setGridWidth(98, GridSizeUnit.PERCENTAGE);
			distributionKindGroupDetailsTable.setRowsPerPage(1000);
			distributionKindGroupDetailsTable.setContentHeight(300, SizeUnit.PX);
		}
		return distributionKindGroupDetailsTable;
	}
	
	

	@SuppressWarnings("unchecked")
	private List<IGridColumn> getDistributionKindGroupDetailsTableColumns() {
		
		if (distributionKindGroupDetailsColumns == null) {
			distributionKindGroupDetailsColumns = new ArrayList<IGridColumn>();
	
			SRSDataGridColumn descriptionColumn = new SRSDataGridColumn<DistributionKindGroupRatesDTO>("description",
					new Model("Description ID"), "description", EditStateType.VIEW);
			descriptionColumn.setSizeUnit(SizeUnit.PX);
			descriptionColumn.setMinSize(300);
			descriptionColumn.setInitialSize(300);
			distributionKindGroupDetailsColumns.add(descriptionColumn);

			SRSDataGridColumn distributionFactorColumn = new SRSDataGridColumn<DistributionKindGroupRatesDTO>("distributionFactor",
					new Model("Distribution Factor"),"distributionFactor",EditStateType.VIEW);
			distributionFactorColumn.setSizeUnit(SizeUnit.PX);
			distributionFactorColumn.setMinSize(300);
			distributionFactorColumn.setInitialSize(300);
			distributionKindGroupDetailsColumns.add(distributionFactorColumn);
			SRSDataGridColumn distributionScheduleColumn = new SRSDataGridColumn<DistributionKindGroupRatesDTO>("distributionSchedule",
					new Model("Distribution Schedule"),"distributionSchedule",EditStateType.VIEW){
				
				@Override
				public Panel newCellPanel(WebMarkupContainer parent,
						String componentId, IModel rowModel,
						String objectProperty, EditStateType state,
						DistributionKindGroupRatesDTO data) {
					
					if ( getEditState().isViewOnly() ) {
						//create label with type and display						
						Description vDescription = null;
						for (Description description : scheduleDescriptions) {
							
							if(data.getDistributionSchedule() == description.getReference()){
								vDescription = description;
								break;
							}
						}
						return HelperPanel.getInstance(componentId, new Label("value",(vDescription != null) ? vDescription.getDescription() : ""));
					}				
					final DropDownChoice dropdown = new DropDownChoice("value",new PropertyModel(data,objectProperty){
						@Override
							public Object getObject() {
								//return one of the values in the static list						
								Integer id = (Integer) super.getObject();
								if(id == null){
									return null;							
								}
								for(Description description : scheduleDescriptions){
									if(description.getReference() == id){
										return description;
									}
								}
								return null;
							}
							@Override
							public void setObject(Object arg0) {						
								super.setObject(((Description)arg0).getReference());
							}
					},scheduleDescriptions,new ChoiceRenderer("description", "reference"));
					dropdown.add(new AjaxFormComponentUpdatingBehavior("change"){
						@Override
						protected void onUpdate(AjaxRequestTarget target) {
							target.add(dropdown);
						}					
					});
					
					//create dropdown of selectable types				
					HelperPanel dropdownPanel = HelperPanel.getInstance(componentId, dropdown);	
					
					dropdownPanel.setVisible(true);
					
					return dropdownPanel;
					//validationComponents.add(endDate);	
					
				}
			};
			distributionScheduleColumn.setSizeUnit(SizeUnit.PX);
			distributionScheduleColumn.setMinSize(300);
			distributionScheduleColumn.setInitialSize(300);
			distributionKindGroupDetailsColumns.add(distributionScheduleColumn);

		}
		return distributionKindGroupDetailsColumns;
	}
	



}
