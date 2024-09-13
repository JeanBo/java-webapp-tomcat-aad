package za.co.liberty.web.pages.maintainagreement;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow.MaskType;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.Model;

import com.inmethod.grid.DataProviderAdapter;
import com.inmethod.grid.IGridColumn;
import com.inmethod.grid.SizeUnit;

import za.co.liberty.business.guicontrollers.agreement.IAgreementGUIController;
import za.co.liberty.dto.agreement.AgreementDTO;
import za.co.liberty.dto.agreement.properties.DistributionDetailDTO;
import za.co.liberty.dto.gui.templates.DistributionKindGroupRatesDTO;
import za.co.liberty.helpers.lookup.ServiceLocator;
import za.co.liberty.persistence.rating.entity.Description;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.data.enums.fields.AgreementGUIField;
import za.co.liberty.web.pages.maintainagreement.model.DistributionKindGroupPageModel;
import za.co.liberty.web.pages.maintainagreement.model.DistributionKindGroupPanelModel;
import za.co.liberty.web.pages.panels.ViewTemplateBasePanel;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGrid;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGrid.GridSizeUnit;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGridColumn;
import za.co.liberty.web.wicket.markup.repeater.data.ListDataProvider;
import za.co.liberty.web.wicket.view.ContextDrivenViewTemplate;
/**
 * This class represents the page form to be added to the panel
 * @author mzl2611
 */
public class DistributionKindGroupPanel extends ViewTemplateBasePanel<AgreementGUIField, AgreementDTO> {

	private DistributionKindGroupPanelModel panelModel;
	
	private AgreementDTO viewTemplateContext;
	
	private Button updateAgmtTemplateButton;
	
	private ModalWindow distributionKindGroupWindow;

	private boolean initialised;

	private SRSDataGrid distributionKindGroupDetailsTable;
	
	private List<IGridColumn> distributionKindGroupDetailsColumns;

	private DistributionKindGroupForm pageForm;

	private transient Logger logger;

	private transient IAgreementGUIController  guiController;

	public DistributionKindGroupPanel(String id, EditStateType editState, DistributionKindGroupPanelModel panelModel) {
		super(id, editState);
		this.panelModel = panelModel;
		
		initialise();
		
	}
	
	private void initialise() {
		if(panelModel.getDistributionKindGroupContainer().getDistributionDetails() != null)
		{
			List<DistributionKindGroupRatesDTO> distributionKindGroupDTOs = getGuiController().findDistributionKindGroupDTOTemplateID(panelModel.getDistributionKindGroupContainer().getDistributionDetails().getId());
			if(distributionKindGroupDTOs != null  )
			{
				panelModel.getDistributionKindGroupDTOs().clear();
				panelModel.getDistributionKindGroupDTOs().addAll(distributionKindGroupDTOs);
			}
		}
		add(new DistributionKindGroupForm("distributionKindGroupForm"));
	}

	private class DistributionKindGroupForm extends Form {

		public DistributionKindGroupForm(String id) {
			super(id);
			initComponents();
		}

		/**
		 * Add all components to the form
		 */
		private void initComponents() {
			/**
			 * Add components
			 */
			add(distributionKindGroupDetailsTable = getDistributionKindGroupDetailsTable());
			add(updateAgmtTemplateButton = getUpdateTemplateButton());
			
		}
	}
	
	private Logger getLogger() {
		if (logger == null) {
			logger = Logger.getLogger(this.getClass());
		}
		return logger;
	}

	@Override
	protected boolean isProcessOutstandingRequestsAllowed() {
		return false;
	}

	/**
	 * Load the components on the page on first render, 
	 * so that the components are only generated when the page is displayed 
	 */
	@Override
	protected void onBeforeRender() {
		super.onBeforeRender();
		if (!initialised) {
			if (getLogger().isDebugEnabled()) {
				getLogger().debug("Adding components to the page on first render");
			}
			//get the Distribution Kind Group Rates to populate on the Table
			
			add(distributionKindGroupWindow = createModalWindow(panelModel, "distributionKindGroupWindow"));
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
			distributionKindGroupDetailsTable.setRowsPerPage(5);
			distributionKindGroupDetailsTable.setContentHeight(100, SizeUnit.PX);
		}
		return distributionKindGroupDetailsTable;
	}

	@SuppressWarnings("unchecked")
	private List<IGridColumn> getDistributionKindGroupDetailsTableColumns() {
		
		//private long templateID;
		//private long distributionKindGroup;
		//private long distributionKindGroupReference;
		//private float distributionFactor;
		//private int distributionSchedule;
		if (distributionKindGroupDetailsColumns == null) {
			distributionKindGroupDetailsColumns = new ArrayList<IGridColumn>();
			SRSDataGridColumn templateIDColumn = new SRSDataGridColumn<DistributionKindGroupRatesDTO>("templateID",
					new Model("Template ID"),"templateID",EditStateType.VIEW);
			templateIDColumn.setSizeUnit(SizeUnit.PX);
			templateIDColumn.setMinSize(300);
			templateIDColumn.setInitialSize(300);
			distributionKindGroupDetailsColumns.add(templateIDColumn);
			SRSDataGridColumn distributionKindGroupColumn = new SRSDataGridColumn<DistributionKindGroupRatesDTO>("distributionKindGroup",
					new Model("Distribution Kind Group"),"distributionKindGroup",EditStateType.VIEW);
			distributionKindGroupColumn.setSizeUnit(SizeUnit.PX);
			distributionKindGroupColumn.setMinSize(300);
			distributionKindGroupColumn.setInitialSize(300);
			distributionKindGroupDetailsColumns.add(distributionKindGroupColumn);
			SRSDataGridColumn distributionKindGroupReferenceColumn = new SRSDataGridColumn<DistributionDetailDTO>("distributionKindGroupReference",
					new Model("Distribution Kind Group Reference"),"distributionKindGroupReference",EditStateType.VIEW);

			distributionKindGroupReferenceColumn.setSizeUnit(SizeUnit.PX);
			distributionKindGroupReferenceColumn.setMinSize(75);
			distributionKindGroupReferenceColumn.setInitialSize(75);
			distributionKindGroupDetailsColumns.add(distributionKindGroupReferenceColumn);
			SRSDataGridColumn distributionFactorColumn = new SRSDataGridColumn<DistributionDetailDTO>("distributionFactor",
					new Model("Distribution Factor"),"distributionFactor",EditStateType.VIEW);
			distributionFactorColumn.setSizeUnit(SizeUnit.PX);
			distributionFactorColumn.setMinSize(300);
			distributionFactorColumn.setInitialSize(300);
			distributionKindGroupDetailsColumns.add(distributionFactorColumn);
			SRSDataGridColumn distributionScheduleColumn = new SRSDataGridColumn<DistributionDetailDTO>("distributionSchedule",
					new Model("Distribution Schedule"),"distributionSchedule",EditStateType.VIEW);
			distributionScheduleColumn.setSizeUnit(SizeUnit.PX);
			distributionScheduleColumn.setMinSize(300);
			distributionScheduleColumn.setInitialSize(300);
			distributionKindGroupDetailsColumns.add(distributionScheduleColumn);
		}
		return distributionKindGroupDetailsColumns;
	}
	
	private ModalWindow createModalWindow(final DistributionKindGroupPanelModel panelModel,final String id) {
		final ModalWindow window = new ModalWindow(id);
		
		window.setTitle("Distribution Kind Group Details");
		
		// Create the page
		window.setPageCreator(new ModalWindow.PageCreator() {
			private static final long serialVersionUID = 1L;
			public Page createPage() {
				
				DistributionKindGroupPageModel pageModel = new DistributionKindGroupPageModel();
				pageModel.setDistributionKindGroupDTOs(panelModel.getDistributionKindGroupDTOs() == null ? new ArrayList<DistributionKindGroupRatesDTO>():panelModel.getDistributionKindGroupDTOs()); 
					return new DistributionKindGroupPage(window,pageModel, getEditState());
			}
		});
		
		// Close window call back
		window.setWindowClosedCallback(new ModalWindow.WindowClosedCallback() {
			private static final long serialVersionUID = 1L;
			
			public void onClose(AjaxRequestTarget target) {
				//panelModel.getAgreementDTO().getKind()
				panelModel.getDistributionKindGroupContainer().getDistributionDetails().setId(new Long(panelModel.getDistributionKindGroupDTOs().get(0).getTemplateID()).intValue());
				Description templateDescription = getGuiController().getTemplateDescriptionByTemplateID(((Long)panelModel.getDistributionKindGroupDTOs().get(0).getTemplateID()).intValue());
				panelModel.getDistributionKindGroupContainer().getDistributionDetails().setDescription(templateDescription.getDescription());
				panelModel.getDistributionKindGroupContainer().getDistributionDetails().setEffectiveFrom(new Date());
				target.add(distributionKindGroupDetailsTable);
			}
		});

		// Initialise window settings
		window.setMinimalHeight(420);
		window.setInitialHeight(520);
		window.setMinimalWidth(750);
		window.setInitialWidth(750);
		window.setMaskType(MaskType.SEMI_TRANSPARENT);
		window.setCssClassName(ModalWindow.CSS_CLASS_GRAY);
		
		return window;
	}
	
	/**
	 * Get the account details button
	 * @return the button
	 */
	private Button getUpdateTemplateButton() {
		
		
		if (updateAgmtTemplateButton==null) {
			updateAgmtTemplateButton = new Button("updateAgmtTemplateButton");
			updateAgmtTemplateButton.add(new AjaxFormComponentUpdatingBehavior("click") {
				@Override
				protected void onUpdate(AjaxRequestTarget target) {
					distributionKindGroupWindow.show(target);
				}
			});
			updateAgmtTemplateButton.setOutputMarkupId(true);
		}
		if(getEditState() == EditStateType.VIEW)
		{
			updateAgmtTemplateButton.setVisible(false);
		}
		return updateAgmtTemplateButton;
	}
	
	private AgreementDTO getContext() {
		return getViewTemplateContext();
	}
	

	@Override
	protected ContextDrivenViewTemplate<AgreementGUIField, AgreementDTO> getViewTemplate() {
		return panelModel.getViewTemplate();
	}

	@Override
	protected AgreementDTO getViewTemplateContext() {
		if (viewTemplateContext == null) {
			viewTemplateContext = panelModel.getDistributionKindGroupContainer();
		}
		return viewTemplateContext;
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
}
