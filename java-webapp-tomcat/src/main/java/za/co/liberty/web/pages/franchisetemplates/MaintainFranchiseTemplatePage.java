package za.co.liberty.web.pages.franchisetemplates;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.naming.NamingException;

import org.apache.commons.lang.SerializationUtils;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow.MaskType;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.Panel;

import za.co.liberty.business.guicontrollers.template.IFranchiseTemplateGUIController;
import za.co.liberty.dto.gui.templates.DistributionKindGroupRatesDTO;
import za.co.liberty.dto.gui.templates.FranchiseTemplateDTO;
import za.co.liberty.dto.gui.templates.MaintainFranchiseTemplateDTO;
import za.co.liberty.dto.userprofiles.ISessionUserProfile;
import za.co.liberty.exceptions.data.ValidationException;
import za.co.liberty.exceptions.security.TabAccessException;
import za.co.liberty.helpers.lookup.ServiceLocator;
import za.co.liberty.interfaces.agreements.RoleKindType;
import za.co.liberty.interfaces.agreements.requests.RequestKindType;
import za.co.liberty.interfaces.rating.FranchiseTemplateKindEnum;
import za.co.liberty.web.data.enums.ContextType;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.data.enums.PanelToRequestMapping;
import za.co.liberty.web.data.pages.ITabbedPageModel;
import za.co.liberty.web.pages.BasePage;
import za.co.liberty.web.pages.BaseWindowPage;
import za.co.liberty.web.pages.MaintenanceBasePage;
import za.co.liberty.web.pages.franchisetemplates.model.FranchiseTemplatePanelModel;
import za.co.liberty.web.pages.franchisetemplates.model.MaintainFranchiseTemplatePageModel;
import za.co.liberty.web.pages.interfaces.IPageDataLoaded;
import za.co.liberty.web.pages.maintainagreement.AgreementHierarchyPanel;
import za.co.liberty.web.pages.panels.BaseModificationButtonsPanel;
import za.co.liberty.web.system.SRSAuthWebSession;

/**
 * Franchise Template maintenance page
 * 
 * @author MZL2611
 *
 */
public class MaintainFranchiseTemplatePage extends MaintenanceBasePage<Integer> implements IPageDataLoaded{


	private static final long serialVersionUID = 1L;

	private MaintainFranchiseTemplatePageModel pageModel;

	private String pageName = "Maintain Template";
	
	private BaseModificationButtonsPanel buttonsPanel;
	
	private ModalWindow window;
	
	private transient IFranchiseTemplateGUIController franchiseTemplateGUIController;
	
//	by default it is set to true, uses should set to false when intializing their pages and set back to true once all data is loaded
	private boolean pageDataLoaded = true;
	
	/**
	 * 
	 */
	public MaintainFranchiseTemplatePage() {
		this(null);
		//setEditState(EditStateType.MODIFY, null);
	}

	/**
	 * @param obj
	 */
	public MaintainFranchiseTemplatePage(Object obj) {
		super(obj);
		this.add(window = createModalWindow("addNewWizzardWindow"));
	}
	
	/**
	 * Create the modal window
	 * 
	 * @param id
	 * @return
	 */
	private ModalWindow createModalWindow(String id) {		
		final ModalWindow window = new ModalWindow(id);
		window.setTitle("Add Franchise Template Row");				
		// Create the page
		window.setPageCreator(new ModalWindow.PageCreator() {
			private static final long serialVersionUID = 1L;
			public Page createPage() {					
				return null; 
				
			}			
		});		
		
		// Initialise window settings
		window.setMinimalHeight(500);
		window.setInitialHeight(500);
		window.setMinimalWidth(750);
		window.setInitialWidth(750);
		window.setMaskType(MaskType.SEMI_TRANSPARENT);
		window.setCssClassName(ModalWindow.CSS_CLASS_GRAY);	
		window.setOutputMarkupId(true);
		window.setOutputMarkupPlaceholderTag(true);
		return window;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see za.co.liberty.web.pages.MaintenanceBasePage#createContainerPanel()
	 */
	@Override
	public Panel createContainerPanel() {
		Panel panel;
		try {			
			int[] disabledPanels = null;
			if(this.getEditState() != EditStateType.VIEW){
				disabledPanels = new int[]{};
			}
			panel = new MaintainFranchiseTemplatePanel(CONTAINER_PANEL_NAME, pageModel,
					getEditState(), disabledPanels, this.getFeedbackPanel(), containerForm,this);
		} catch (TabAccessException e) {			
			//display message that all tabs have been disabled
			error(e.getUserMessage());
			panel = new EmptyPanel(CONTAINER_PANEL_NAME);
		}				
		panel.setOutputMarkupId(true);
		return panel;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see za.co.liberty.web.pages.MaintenanceBasePage#createNavigationalButtons()
	 */
	@Override
	public Button[] createNavigationalButtons() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see za.co.liberty.web.pages.MaintenanceBasePage#createSelectionPanel()
	 */
	@Override
	public Panel createSelectionPanel() {
		
		buttonsPanel =  new BaseModificationButtonsPanel<MaintainFranchiseTemplateDTO>(
				SELECTION_PANEL_NAME, pageModel, this, containerForm,
				MaintainFranchiseTemplateDTO.class,this.getFeedbackPanel(), true,true,true,false,false,false,false) {

			private static final long serialVersionUID = 1L;

			@Override
			public void resetSelection() {
				
			}	

			@Override
			public void doModify_onSubmit(AjaxRequestTarget target, Form form) {
				//This method does nothing in case the user has modify access.				
			}

			/**
			 * Called when Add new is submitted. Notify parent and 
			 * swap panels.  Ensure that selected item is set before calling.
			 * 
			 * @param target
			 * @param form
			 */
			public void doAddNew_onSubmit(AjaxRequestTarget target, Form form) {							
				//add new is different here as we will display a popup wizzard and users will not input anything into the current panel
				//first we check access to this function, user might have buttons enabled, code mistake, but should not be allowed to add
				if(((MaintainFranchiseTemplatePageModel)pageModel).canAdd()  ){
						//getSession().error("Franchise Template cannot be created as Requests Exists to be Authorized");
				 		return;
				}
				if(!MaintainFranchiseTemplatePage.this.isPageDataLoaded()){
					error("The Page data is still busy loading, please wait until finished before clicking the modify button");
					target.add(getFeedbackPanel());					
				}else{
					super.doAddNew_onSubmit(target, form);
				}
			}	

						
		};		
		return buttonsPanel;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see za.co.liberty.web.pages.MaintenanceBasePage#initialisePageModel(java.lang.Object)
	 */
	@Override
	public Object initialisePageModel(Object obj, Integer currentTab) {
		
		
		MaintainFranchiseTemplatePageModel model = null;
		if (obj != null && obj instanceof MaintainFranchiseTemplatePageModel) {
			model = (MaintainFranchiseTemplatePageModel)obj;
		} else {
			model = createPageModel();
		}
		pageModel = model;
		
		if(currentTab != null){
			pageModel.setCurrentTab(currentTab);
		}
		return pageModel;
		
		
	}

	private MaintainFranchiseTemplatePageModel createPageModel() {
			List<DistributionKindGroupRatesDTO> distributionKindGroupDTOs = null;
			 try {
				 distributionKindGroupDTOs = getFranchiseTemplateGUIController().createDistributionKindGroupDTOsFromDKGDefaultValues(FranchiseTemplateKindEnum.FRANCHISE.getDistributionKindGroup());
			} catch (NamingException e) {
				error(e.getMessage());
			}
			
			MaintainFranchiseTemplatePageModel model = new MaintainFranchiseTemplatePageModel();
			MaintainFranchiseTemplateDTO maintainFranchiseTemplateDTO = new MaintainFranchiseTemplateDTO();
			FranchiseTemplateDTO franchiseTemplateDTO = new FranchiseTemplateDTO();
			
			franchiseTemplateDTO.setDistributionKindGroupDTOs(distributionKindGroupDTOs);
			maintainFranchiseTemplateDTO.setFranchiseTemplateDTO(franchiseTemplateDTO);
			List<RequestKindType> unAuthRequests = new ArrayList<RequestKindType>();
			
			model.setMaintainFranchiseTemplateDTO(maintainFranchiseTemplateDTO);
			model.setSelectedItem(maintainFranchiseTemplateDTO);
			model.setMaintainFranchiseTemplateDTOBeforeImage((MaintainFranchiseTemplateDTO) SerializationUtils.clone(maintainFranchiseTemplateDTO));
			FranchiseTemplatePanelModel franchiseTemplatePanelModel = new FranchiseTemplatePanelModel(model);
			model.setFranchiseTemplatePanelModel(franchiseTemplatePanelModel);
			return model;
	}

	

	/*
	 * (non-Javadoc)
	 * 
	 * @see za.co.liberty.web.pages.BasePage#getPageName()
	 */
	@Override
	public String getPageName() {
		return pageName;
	}


	@Override
	public ContextType getContextTypeRequired() {
		return ContextType.FRANCHISE_TEMPLATE;  //MZL2611 Franchise Template Only
	}

	@Override
	public void doSave_onSubmit() {	
		save(false);
	}
	
	private void save(boolean reactivate){
		ISessionUserProfile userProfile = SRSAuthWebSession.get().getSessionUser();		
		
		try {
			 	if(this.pageModel.canAdd()){
			 		//getSession().error("Franchise Template cannot be created as Requests Exists to be Authorized");
			 		return;
			 	}
				franchiseTemplateGUIController = getFranchiseTemplateGUIController();	
				franchiseTemplateGUIController.saveTemplate(this.pageModel.getMaintainFranchiseTemplateDTO(),userProfile,
							this.pageModel.getMaintainFranchiseTemplateDTOBeforeImage(),PanelToRequestMapping.getMappingForPageAndPanel(MaintainFranchiseTemplatePage.class,pageModel.getCurrentTabClass()));	
			
				//reload context as details have changed			
				
			
			invalidatePage();		
			getSession().info("Record was saved successfully");			
			setResponsePage(new MaintainFranchiseTemplatePage(pageModel));
		}
		catch(Exception e)
		{
			if(e  instanceof ValidationException) {
				getSession().error(((ValidationException) e).getErrorMessages().get(0));
			}
		}
	}
	
	/**
	 * get an instance of IFranchiseTemplateGUIController
	 * @return
	 * @throws NamingException 
	 */
	private IFranchiseTemplateGUIController getFranchiseTemplateGUIController() throws NamingException {
		if(franchiseTemplateGUIController == null){
			try {
				franchiseTemplateGUIController = ServiceLocator.lookupService(IFranchiseTemplateGUIController.class);
			} catch (NamingException e) {
				throw e;
			} 
		}
		return franchiseTemplateGUIController;		
	}
	
	/**
	 * Will always return true unless the coder has set it to false
	 */
	public boolean isPageDataLoaded() {
		return pageDataLoaded;
	}

	public void setPageDataLoaded(boolean pageDataLoaded) {
		this.pageDataLoaded = pageDataLoaded;
	}
}
