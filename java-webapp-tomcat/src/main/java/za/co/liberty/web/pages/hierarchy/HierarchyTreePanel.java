package za.co.liberty.web.pages.hierarchy;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.ImageButton;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import com.inmethod.grid.IGridColumn;
import com.inmethod.grid.SizeUnit;

import za.co.liberty.business.guicontrollers.hierarchy.IHierarchyOrganogramGUIController;
import za.co.liberty.dto.agreement.SimpleAgreementDetailDTO;
import za.co.liberty.dto.gui.tree.TreeNodeDTO;
import za.co.liberty.dto.party.HierarchyNodeDTO;
import za.co.liberty.dto.party.PartyDTO;
import za.co.liberty.dto.party.contactdetail.CommunicationPreferenceDTO;
import za.co.liberty.dto.party.contactdetail.ContactPreferenceDTO;
import za.co.liberty.dto.party.contactdetail.ContactPreferenceWrapperDTO;
import za.co.liberty.dto.userprofiles.ContextDTO;
import za.co.liberty.exceptions.data.DataNotFoundException;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.helpers.lookup.ServiceLocator;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.pages.BaseWindowPage;
import za.co.liberty.web.pages.contactdetail.ContactDetailsPanel;
import za.co.liberty.web.pages.hierarchy.model.HierarchyTreeModel;
import za.co.liberty.web.pages.hierarchy.model.MaintainHierarchyPageModel;
import za.co.liberty.web.pages.panels.HelperPanel;
import za.co.liberty.web.system.SRSAuthWebSession;
import za.co.liberty.web.wicket.ajax.attributes.SRSAjaxCallListener;
import za.co.liberty.web.wicket.markup.html.grid.GridToCSVHelper;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGrid;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGrid.GridSizeUnit;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGridColumn;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataProviderAdapter;
import za.co.liberty.web.wicket.markup.repeater.data.SortableListDataProvider;

/**
 * This panel is loaded in a popup screen from a tree node click. Will fetch and display basic subagreement details
 * for the relavent node, representing a party. Address information will also be displayed in a collapsable div
 * @author jwv2310
 *
 */
@SuppressWarnings("unused")
public class HierarchyTreePanel extends BaseWindowPage { 
	
	private static final long serialVersionUID = 1L;
	private Label partyOidLabel = null;
	private Label partyNodeName;
	private Label partyOid;
	private Label partyName;

	private transient IHierarchyOrganogramGUIController hierarchyOrganogramManagement;
	
	private Collection<SimpleAgreementDetailDTO> contextIdList = new ArrayList<SimpleAgreementDetailDTO>();
	private SRSDataGrid nodeAgreementHomeToGrid = null;
	private EditStateType editStateLocal = null;
	private static final Logger logger = Logger.getLogger(HierarchyTreePanel.class);
	private FeedbackPanel feedBackPanel;
	private Panel addressPanel;
	private TreeNodeDTO locTreeNodeDTO = null;
	private ModalWindow modalWin;
	//private boolean enabled;
	private HierarchyTreeModel treeModelInst;
	private MaintainHierarchyPageModel hierarchyPageModel = null;
	private Label tblHeading;
	private Label informLabel;
	private Button buttonExport;
	private Button buttonRetrieveAll;
	private Form gridForm;
	private int totalNumAgreements;
	private WebMarkupContainer nodeDetailsWrapper;
	private boolean displayAgreementList;
	private ImageButton expandImage;
	/**
	 * inner class which will set the enable status of the panel for expand/collapse of hierarchy node details
	 */
	public class PModel implements Serializable {
		
		private static final long serialVersionUID = -2938380980345658258L;
		private boolean enabled;
		private Panel panel;

		public PModel(Panel panel, boolean enabled){
		 	this.panel = panel;
		 	this.enabled = enabled;
		}
		public boolean isEnabled() {			
			return enabled;
		}
		public void setEnabled(boolean enabled) {
			this.enabled = enabled;
		}
		public Panel getPanel(){
			return panel;
		}
		
	}

	/**
	 * Constructor of the panel class
	 * @param treeModel
	 * @param modalWin
	 * @param treeDto
	 * @param editState
	 * @param feedback
	 */
	public HierarchyTreePanel(HierarchyTreeModel treeModel, ModalWindow modalWin, EditStateType editState,  FeedbackPanel feedback ){		
			this.modalWin  = modalWin;
			this.feedBackPanel = feedback;
			locTreeNodeDTO = treeModel.getTreeNodeDTO();
			treeModelInst = treeModel;
			treeModelInst.setAgreementNodeClicked(false);
			displayAgreementList = treeModelInst.isUserViewHierarchyNode();
			initiateComponents();			
	}
	
	
	
	public Collection<SimpleAgreementDetailDTO> getSubAgreements(Integer fromFetch, Integer toFetch) {
		List<SimpleAgreementDetailDTO> agmList = new ArrayList<SimpleAgreementDetailDTO>(getHierarchyOrganogramController().findAllAgreementsLinkedToHierarchyNodeMore(locTreeNodeDTO.getOid(),fromFetch, toFetch));
		Collections.sort(agmList,treeModelInst.getAgreementNumberComparator());
		return agmList;
		 
	}
	
	public HierarchyNodeDTO getHierarchyNodeForAgreement(HierarchyNodeDTO hierarchyDTO){
		try{
			hierarchyDTO = getHierarchyOrganogramController().getHierarchyNodeDTO(locTreeNodeDTO.getOid());   
		}catch(DataNotFoundException e){
			logger.error("Data not found for hierarchy node clicked:" + locTreeNodeDTO.getOid() + "- Reason :" + e.getCause());
			error("An error occured while fetching node details for party id:" + locTreeNodeDTO.getOid());
		}
		return hierarchyDTO;
	}
	
	public HierarchyNodePanel createHierarchyNodePanel(String id){
		return new HierarchyNodePanel(id, hierarchyPageModel, EditStateType.AUTHORISE, feedBackPanel,null);
	}
	
	public void initiateComponents(){
		
		add(partyOidLabel = createPartyOidLabel("partyOidNodeLabel"));
		add(partyOid = createPartyOid("partyOidLabel",treeModelInst.getTreeNodeDTO()));
		add(partyNodeName = createPartyNodeNameLabel("partyNameNodeLabel"));
		add(partyName = createPartyNameLabel("partyNameLabel",treeModelInst.getTreeNodeDTO()));
		
		//get the sub-agreements, note that the parameters passed in will determine the return size.
		contextIdList.clear();		
		contextIdList.addAll(getSubAgreements(0,50));  
		treeModelInst.setAgreementDetails((List<SimpleAgreementDetailDTO>) contextIdList);

		hierarchyPageModel = new MaintainHierarchyPageModel();
		// set hierarchy node on page Model
		hierarchyPageModel.setHierarchyNodeDTO(getHierarchyNodeForAgreement(new HierarchyNodeDTO()));
		HierarchyNodePanel paneltemp = createHierarchyNodePanel("hierarchyNodeDetail");
		
		//call and initialise a panel with the hierarchy node detail information
		Panel tPanel = createHoldPanel("panelHolder");
		
		PModel tempMod = new PModel(tPanel, false);
		nodeDetailsWrapper = createNodeDetailsWrapperContainer("nodeDetailsWrapper", tempMod);
	    //final ImageButton expandImage = createExpandImage("showHistoryImg", tempMod);
		expandImage = createExpandImage("showHistoryImg", tempMod);
	    add(expandImage);
	    add(tblHeading = createTableLabelHeading("tblHeading"));
	    nodeDetailsWrapper.add(addressPanel = createAddressGrid("addressGrid"));
	    add(gridForm = createGridForm("gridForm"));
		add(informLabel = createInformLabel("informLabel"));
		nodeDetailsWrapper.add(paneltemp);
		add(nodeDetailsWrapper);
	}
	
	public Panel createHoldPanel(String id){
		Panel tPanel = new EmptyPanel(id);
		tPanel.setOutputMarkupId(true);
		return tPanel;
	}
	
	private WebMarkupContainer createNodeDetailsWrapperContainer(String id, final PModel tempModel){
		WebMarkupContainer wmContainer = new WebMarkupContainer(id) {
			private static final long serialVersionUID = 1L;
			
			@Override
			protected void onComponentTag(ComponentTag tag) {
				super.onComponentTag(tag);
				if (totalNumAgreements != 0 ){
				   decorateComponentStyleToHide(!tempModel.isEnabled(), tag);
				}
				if(displayAgreementList == false){
					decorateComponentStyleToHide(tempModel.isEnabled(), tag);
				}
			}	
		};
		wmContainer.setOutputMarkupId(true);
		return wmContainer;
	}
	
	private ImageButton createExpandImage(String id, final PModel tempMod){
		    final ImageButton expandImageButton = new ImageButton(id, "Expand"){
				
		    	@Override
				public Form<?> getForm() {					
					return gridForm;
				}
		    	
		    };
			
		    expandImageButton.add(new AjaxEventBehavior("click"){
			private static final long serialVersionUID = 1L;
			
			
			@Override
			protected void onEvent(AjaxRequestTarget target) {
				nodeDetailsWrapper.setEnabled(!nodeDetailsWrapper.isEnabled());
				tempMod.setEnabled(!nodeDetailsWrapper.isEnabled());
				expandImageButton.add(new AttributeModifier("src",
						getExpandImageSrc(nodeDetailsWrapper.isEnabled())));
				target.add(expandImageButton);
				target.add(nodeDetailsWrapper);
				target.add(addressPanel);
			}
		}
		);
			expandImageButton.setOutputMarkupId(true);
			expandImageButton.add(new AttributeModifier("src",getExpandImageSrc(nodeDetailsWrapper.isEnabled())));
		return expandImageButton;
	}
	
	private void setButtonActionPanelVisible(boolean visible){
		buttonExport.setVisible(visible);
		buttonRetrieveAll.setVisible(visible);
	}
	
	/**
	 * Inner class which will contain the sub agreements on a form, so that output can be submitted for export to excel
	 * @author jwv2310
	 *
	 */
	public class GridForm extends Form {
		
		private static final long serialVersionUID = 1L;

		public GridForm(String id){
			super(id);
			
			add(nodeAgreementHomeToGrid = createNodeAgreementHomeToGrid("subAgreementGrid"));
			nodeAgreementHomeToGrid.setOutputMarkupId(true);
			add(buttonRetrieveAll = createButtonRetrieveAll("buttonRetrieveAll"));
			add(buttonExport = createButtonPanelExport("buttonExport"));
			
			if(displayAgreementList == true) {
				if(contextIdList.size() == 0 || contextIdList.size() <= 0){
					System.err.println("set visibilty allowed = false");
					nodeAgreementHomeToGrid.setVisibilityAllowed(false);
					nodeAgreementHomeToGrid.setVisible(false);
					setButtonActionPanelVisible(false);
				}else if(totalNumAgreements <= 50) {
					buttonRetrieveAll.setVisible(false);
					buttonExport.setVisible(true);
				}else if(totalNumAgreements > 50){
					buttonRetrieveAll.setVisible(true);
					buttonExport.setVisible(false);
				}
			}else{
				nodeAgreementHomeToGrid.setVisible(false);
				buttonRetrieveAll.setVisible(false);
				buttonExport.setVisible(false);
			}
		}
		
		
	}
	
	public Label createInformLabel(String id){
		Label informLabel = new Label(id,"Please note that the retrieval may take a few minutes, pending on the amount of agreements."  
			+ " Exporting to Excel may take minutes to complete...PLEASE WAIT for the save/open option."	);
		if (this.displayAgreementList == false) {
			informLabel.setVisible(false);
		}
		return informLabel;
	}
	
	public Form createGridForm(String id){
		Form form = new GridForm(id);
		form.setOutputMarkupId(true);
		return form;
	}
	
	public Button createButtonRetrieveAll(String id){
		Button tempButton = new Button(id);
		tempButton.add(new AjaxFormComponentUpdatingBehavior("click") {
			private static final long serialVersionUID = 1L;
			
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
			  	 contextIdList.clear();		
				 contextIdList.addAll(getSubAgreements(null,null));
				 Collections.sort(treeModelInst.getAgreementDetails(),treeModelInst.getAgreementNumberComparator());
				 treeModelInst.setAgreementDetails((List<SimpleAgreementDetailDTO>) contextIdList);
				 target.add(feedBackPanel);
				 target.add(nodeAgreementHomeToGrid);
				 target.add(gridForm);
				 buttonRetrieveAll.setVisible(false);
				 buttonExport.setVisible(true);
			}
//			@Override
//			protected IAjaxCallDecorator getAjaxCallDecorator() {
//				return new AjaxCallDecorator() {
//					private static final long serialVersionUID = 1L;
//
//					public CharSequence decorateScript(CharSequence script) {
//						return "overlay(true);" + script;
//					}
//				};
//			}
			
			@Override
			protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
				super.updateAjaxAttributes(attributes);
			        
			        // SRS Convenience method for overLay hiding/showing
			        attributes.getAjaxCallListeners().add(new SRSAjaxCallListener());
			}
			
			
		});		
		return tempButton;
	}
	
	private Button createButtonPanelExport(String id){
		Button tempButton = new Button(id){

			private static final long serialVersionUID = 1L;
			@Override
			protected void onComponentTag(ComponentTag tag) {
				super.onComponentTag(tag);
				tag.getAttributes().put("type", "submit");
			}
			
			@Override
			public void onSubmit() {
				super.onSubmit();
				try {
					new GridToCSVHelper().createCSVFromDataGrid(nodeAgreementHomeToGrid,"agreement_for_node_"+ locTreeNodeDTO.getFullname()+".csv");
				} catch (Exception e) {	
					logger.error("An error occured when trying to generate the excel document",e);
					feedBackPanel.error("Error occurred during export:" + e.getCause());
				}				
			}
			
		};
		tempButton.add(new AjaxFormComponentUpdatingBehavior("click"){			
			private static final long serialVersionUID = 1L;
			
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				//update feedback so busy indicater can disappear and might display error generated above
				target.add(feedBackPanel);
			}
			
//			@Override
//			protected IAjaxCallDecorator getAjaxCallDecorator() {
//				return new AjaxCallDecorator() {
//					private static final long serialVersionUID = 1L;
//
//					public CharSequence decorateScript(CharSequence script) {
//						return "overlay(true);" + script;
//					}
//				};
//			}
			
			@Override
			protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
				super.updateAjaxAttributes(attributes);
			        
			        // SRS Convenience method for overLay hiding/showing
			        attributes.getAjaxCallListeners().add(new SRSAjaxCallListener());
			}
		});

		return tempButton;
	}
	
//	public boolean isEnabled() {
//		return enabled;
//	}

	private Label createTableLabelHeading(String id) {
		totalNumAgreements = 0;
		int countSubAgm = getHierarchyOrganogramController().findSubAgreementCount(locTreeNodeDTO.getOid());
		totalNumAgreements = countSubAgm;
		Label tempTableHeadingLabel = new Label(id,"Contact details for: " + locTreeNodeDTO + " - Number of Sub-agreements:" + countSubAgm);
		
		return tempTableHeadingLabel;
	}
	
	private Panel createReplacementGridLabel(String id){
		Panel tempGridRepLabel = new Panel(id) {
			
		};
		Label tempLab = new Label("No agreements found");
		tempGridRepLabel.add(tempLab);
		
		return tempGridRepLabel;
	}
	
	private void decorateComponentStyleToHide(boolean isHidden, ComponentTag tag) {
		String val = (String) tag.getAttributes().get("style");
		val = (val ==null) ? "" : val;
		val += (isHidden) ? " ;display:none;" : " ;display:block;";
		tag.put("style", val);
	}
	
	/**
	 * Get the source tag for the expand image.
	 * 
	 * @param isEnabled
	 * @return
	 */
	private String getExpandImageSrc(boolean isEnabled) {
		return (isEnabled) ? "/SRSAppWeb/images/plus.png" 
				: "/SRSAppWeb/images/minus.png";
	}
	
	public Panel createAddressGrid(String id) {
		Panel tempPanel = null; 
		try {

			PartyDTO partyDto = getHierarchyOrganogramController().getPartyDTOWithObjectOid(locTreeNodeDTO.getOid());
			ContactPreferenceWrapperDTO e = partyDto.getContactPreferences();
			List<ContactPreferenceDTO> contPrefList =  e.getContactPreferences();
			List<CommunicationPreferenceDTO> commPrefs = partyDto.getCommunicationPreferences();
			ContactDetailsPanel hierarchyNodeDetail;
			hierarchyNodeDetail = new ContactDetailsPanel("addressGrid",contPrefList, commPrefs, EditStateType.VIEW ,feedBackPanel,false,null, false);
			tempPanel = (Panel)hierarchyNodeDetail;
			tempPanel.setOutputMarkupId(true);
			tempPanel.setEscapeModelStrings(false);
		}catch(DataNotFoundException e){
		    logger.error("Can not lookup party DTO with party id:" + e);
		    error("Could not find the address data of this node:" + locTreeNodeDTO.getOid());
		}
		return tempPanel;
	}
	
	public Label createPartyNameLabel(String id, TreeNodeDTO treeDto) {
		Label tempLabel = new Label(id, treeDto.getFullname());
		tempLabel.setEscapeModelStrings(false);
		return tempLabel;
	}
	
	public Label createPartyOid(String id, TreeNodeDTO treeDto) {
		Label tempLabel = new Label(id, ""+treeDto.getOid());
		tempLabel.setEscapeModelStrings(false);
		return tempLabel;
	}
	
	public Label createPartyNodeNameLabel(String id) {
		Label tempLabel = new Label(id, "Party Name:");
		tempLabel.setEscapeModelStrings(false);
		return tempLabel;
	}
	
	public Label createPartyOidLabel(String id){
		Label tempLabel = new Label(id, "Party OID");
		tempLabel.setEscapeModelStrings(false);
        return tempLabel;
	}
	
	@Override
	protected void onBeforeRender() {		
		super.onBeforeRender();
		if(feedBackPanel == null){
			feedBackPanel = getFeedBackPanel();
		}
	}
	
	public SRSDataGrid createNodeAgreementHomeToGrid(String id) {
		//Collections.sort(treeModelInst.getAgreementDetails(),treeModelInst.getAgreementNumberComparator());
		List<SimpleAgreementDetailDTO> a = new ArrayList<SimpleAgreementDetailDTO>(contextIdList);
		SRSDataGrid tempDataGrid 
		= new SRSDataGrid(id,new SRSDataProviderAdapter(
				new SortableListDataProvider<SimpleAgreementDetailDTO>(treeModelInst.getAgreementDetails())),createSearchResultColumns(),EditStateType.VIEW );
				//new ListDataProvider<SimpleAgreementDetailDTO>(treeModelInst.getAgreementDetails())),createSearchResultColumns(),EditStateType.VIEW );
		tempDataGrid.setOutputMarkupId(true);
		tempDataGrid.setCleanSelectionOnPageChange(false);
		tempDataGrid.setClickRowToSelect(false);
		tempDataGrid.setAllowSelectMultiple(true);
		tempDataGrid.setGridWidth(99, GridSizeUnit.PERCENTAGE);		
		tempDataGrid.setRowsPerPage(50);
		tempDataGrid.setContentHeight(399, SizeUnit.PX);
		tempDataGrid.setPreLight(false);
		return tempDataGrid;
	}
	
	/**
	 * @Return create the contents of the columns of the grid displaying team party details.
	 */
	protected List<IGridColumn> createSearchResultColumns() { 
			List<IGridColumn> columns = new ArrayList<IGridColumn>();
			columns.add(new SRSDataGridColumn<SimpleAgreementDetailDTO>("agreementNumber",
					new Model("SRS ID"), "agreementNumber","agreementNumber" , editStateLocal) {
						private static final long serialVersionUID = 1L;					
						
						@Override
						public Panel newCellPanel(final WebMarkupContainer parent, String componentId, IModel rowModel, 
								final String objectProperty, EditStateType state, final SimpleAgreementDetailDTO data) {
								if(data == null){
									return super.newCellPanel(parent, componentId, rowModel, objectProperty, state,data);	
								}
//								SecurityManagement a;
//								a.canUserViewAgreementDetails(data.getAgreementNumber(),data.getPartyOID(), SRSAuthWebSession.get().getSessionUser());
																
								AjaxFallbackLink link = new AjaxFallbackLink("value", new Model("Text")){

									private static final long serialVersionUID = 1L;

									@Override
									public void onClick(AjaxRequestTarget target) {
										//set the context
										try {
											
											ContextDTO newContextDTO = null;
											
											if (SRSAuthWebSession.get().getContextDTO() != null){
												newContextDTO = SRSAuthWebSession.get().getContextDTO().clone();
											}else{
												newContextDTO = new ContextDTO();
											}
											
											newContextDTO = getHierarchyOrganogramController().setContext(data.getAgreementNumber(),newContextDTO);
											SRSAuthWebSession.get().setContextDTO(newContextDTO);
											treeModelInst.setAgreementNodeClicked(true);
											modalWin.close(target);

										}catch (CloneNotSupportedException e){
											logger.error("Clone errors: " + e);
											return;
											
										}catch (DataNotFoundException e){
											logger.error("Model window lookup errors:" + e);
											error("Data not found to load context for srs id:" + data.getAgreementNumber());
										}
										
									}

									@Override
									public void onComponentTagBody(MarkupStream markup, ComponentTag tag) {										
										replaceComponentTagBody(markup, tag, "" + data.getAgreementNumber());									
									}									
								};			
								return HelperPanel.getInstance(componentId, link);
							}
					}.setInitialSize(100)
			);
			
			columns.add(new SRSDataGridColumn<SimpleAgreementDetailDTO>("consCode",
					new Model("CONS CODE"),"consCode", "consCodeString" , editStateLocal)
					.setInitialSize(100)
			);
			
			columns.add(new SRSDataGridColumn<SimpleAgreementDetailDTO>("status",
					new Model("STATUS"), "status" , "status" , editStateLocal)
					.setInitialSize(150)
			);
			
			columns.add(new SRSDataGridColumn<SimpleAgreementDetailDTO>("name",
					new Model("NAME"), "name" ,"name" , editStateLocal)
			.setInitialSize(520)
			);
			return columns;
	}
	
	public String getPageName() {
		return "HierarchyTreePage Tree Information";
	}
	
	protected IHierarchyOrganogramGUIController getHierarchyOrganogramController() {
		if (hierarchyOrganogramManagement == null) {
			try {
				hierarchyOrganogramManagement = ServiceLocator.lookupService(IHierarchyOrganogramGUIController.class);
			} catch (NamingException namingErr) {
				logger.error(this.getPageName()
						+ " hierarchyOrganogramManagement can not be looked up:"
						+ namingErr);
				CommunicationException comm = new CommunicationException(" hierarchyOrganogramManagement can not be looked up",namingErr);
				throw comm;
			}
		}
		return hierarchyOrganogramManagement;
	}
	
}
