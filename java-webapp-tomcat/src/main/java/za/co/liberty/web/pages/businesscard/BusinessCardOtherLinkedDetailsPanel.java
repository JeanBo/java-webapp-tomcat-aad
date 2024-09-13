package za.co.liberty.web.pages.businesscard;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.naming.NamingException;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow.MaskType;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.Model;

import com.inmethod.grid.IGridColumn;
import com.inmethod.grid.SizeUnit;

import za.co.liberty.business.guicontrollers.IAgreementPrivilegesController;
import za.co.liberty.business.guicontrollers.businesscard.IBusinessCardGuiController;
import za.co.liberty.dto.agreementprivileges.AgreementPrivilegesDataDTO;
import za.co.liberty.dto.agreementprivileges.ExplicitAgreementType;
import za.co.liberty.dto.businesscard.BusinessCardDetailsDTO;
import za.co.liberty.dto.contracting.ResultPartyDTO;
import za.co.liberty.dto.rating.DescriptionDTO;
import za.co.liberty.dto.userprofiles.ContextDTO;
import za.co.liberty.dto.userprofiles.ContextPartyDTO;
import za.co.liberty.dto.userprofiles.ISessionUserProfile;
import za.co.liberty.exceptions.data.DataNotFoundException;
import za.co.liberty.exceptions.data.InconsistentDataException;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.helpers.lookup.ServiceLocator;
import za.co.liberty.interfaces.agreements.requests.RequestKindType;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.pages.BasePage;
import za.co.liberty.web.pages.businesscard.model.BusinessCardPageModel;
import za.co.liberty.web.pages.panels.BasePanel;
import za.co.liberty.web.system.SRSAuthWebSession;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGrid;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGrid.GridSizeUnit;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGridColumn;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataProviderAdapter;
import za.co.liberty.web.wicket.markup.repeater.data.ListDataProvider;


public class BusinessCardOtherLinkedDetailsPanel extends BasePanel{ 


	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(BusinessCardOtherLinkedDetailsPanel.class);

	static {
		logger.setLevel(Level.DEBUG);
		
	}
	ResultPartyDTO partyInContext;
	
	private FeedbackPanel feedBackPanel;
	
	private transient IAgreementPrivilegesController agreementPrivilegesController;
	
	private transient IBusinessCardGuiController businessCardGuiController;
		
	private BusinessCardPageModel pageModel;
	
	private boolean initialised;
	
	private ModalWindow otherLinedPartiesPopup;
	
	private ISessionUserProfile loggedInUser = SRSAuthWebSession.get().getSessionUser();
	
	
	private SRSDataGrid otherLinkedPartiesGrid;
	
	private Page parentPage;
	
	private EditStateType editState;
	
	private String panelId;
	
	static {
		logger.setLevel(Level.DEBUG);
	}
	
	public BusinessCardOtherLinkedDetailsPanel(String id, BusinessCardPageModel pageModel , 
			EditStateType editState, FeedbackPanel feedBackPanel, Page parentPage) {
		super(id, editState,parentPage);
		this.pageModel = pageModel;		
		this.parentPage=parentPage;
		this.editState=editState;
		this.panelId=id;
		this.feedBackPanel = feedBackPanel;
	}
	
	/**
	 * Load the components on the page on first render, 
	 * so that the components are only generated when the page is displayed 
	 */
	@Override
	protected void onBeforeRender() {
		logger.info("onRender");
		if(!initialised) {			
			initialised=true;	
			initPanelModel();
			add(new OtherLinkedPartiesDetailsForm("otherLinkedPartiesDetailsForm"));
			add(otherLinedPartiesPopup = createOtherLinkedPartiesModalWindow("otherLinedPartiesPopup"));
		}
		
		super.onBeforeRender();
	}

	private void initPanelModel() {
		retrievePartyExplicitAgreements();
	}

	public class OtherLinkedPartiesDetailsForm extends Form<Object> {
		
		private static final long serialVersionUID = 1L;
		
		public OtherLinkedPartiesDetailsForm(String id) {
			super(id);	
			setOutputMarkupId(true);
			add(createMessageContainer("messageContainer"));
			add(otherLinkedPartiesGrid = createOtherPartiesLinkedGrid("otherPartiesLinkedPanel",pageModel.getMaintainBusinessCardPanelModel().getBusinessCardDetails()));
			add(createAddRemoveOtherPartiesButton("addRemoveOtherLinkedPartiesButton",pageModel.getPartyOID()));
		}
		
	}	
	private SRSDataGrid createOtherPartiesLinkedGrid(String id, BusinessCardDetailsDTO data){
		List<AgreementPrivilegesDataDTO> agreementPriviledges =data.getAgreementPriviledges();
			
		SRSDataGrid grid = new SRSDataGrid(id, new SRSDataProviderAdapter(
				new ListDataProvider<AgreementPrivilegesDataDTO>(agreementPriviledges)),
				getOtherPartiesLinkedColumns(), getEditState(),null);		
		grid.setCleanSelectionOnPageChange(false);
		grid.setClickRowToSelect(false);
		grid.setAllowSelectMultiple(true);
		grid.setOutputMarkupId(true);
		grid.setGridWidth(99, GridSizeUnit.PERCENTAGE);
		grid.setRowsPerPage(10);
		grid.setContentHeight(70, SizeUnit.PX);
		return grid;
		}
	
	private List<IGridColumn> getOtherPartiesLinkedColumns() {				
		Vector<IGridColumn> cols = new Vector<IGridColumn>();	
		
//		add in the name column
		cols.add(new SRSDataGridColumn<AgreementPrivilegesDataDTO>("name",
				new Model<String>("Party Name"), "name", "name", getEditState()).setInitialSize(140).setWrapText(true));
//		add in the uacfid column
		cols.add(new SRSDataGridColumn<AgreementPrivilegesDataDTO>("ucafID",
				new Model<String>("UACFID"), "uacfID", "ucafID", getEditState()).setInitialSize(80));
		
//		add in the start date column
		cols.add(new SRSDataGridColumn<AgreementPrivilegesDataDTO>("effectiveFrom",
				new Model<String>("Start Date"), "effectiveFrom", "effectiveFrom", getEditState()).setInitialSize(80));
		
//		add in the Consultant Code column
		cols.add(new SRSDataGridColumn<AgreementPrivilegesDataDTO>("thirteenDigitCode",
				new Model<String>("Consultant Code"), "thirteenDigitCode", "thirteenDigitCode", getEditState()).setInitialSize(140).setWrapText(true));
//		add in the Consultant Code column
		cols.add(new SRSDataGridColumn<AgreementPrivilegesDataDTO>("descriptionDTO",
				new Model<String>("Relationship"), "descriptionDTO", "descriptionDTO", getEditState()).setInitialSize(140).setWrapText(true));
		//add in the end date column
		cols.add(new SRSDataGridColumn<AgreementPrivilegesDataDTO>("effectiveTo",
				new Model<String>("End Date"), "effectiveTo", "effectiveTo", getEditState()).setInitialSize(80));
		
			return cols;
	}
	/**
	 * Show a warning message for parties with pays To roles.
	 * 
	 * @param id
	 * @return
	 */
	public WebMarkupContainer createMessageContainer(String id) {
		WebMarkupContainer messageContainer = new WebMarkupContainer(id);	
		messageContainer.add(new Label("warningMessage", "This tab is for creating and maintaining explict relationships" ));
		messageContainer.setVisible(true);
		return messageContainer;
	}

	private Button createAddRemoveOtherPartiesButton(String id, Long partyID) {
		Button but = new Button(id);	
		but.add(new AjaxFormComponentUpdatingBehavior("click"){									
			private static final long serialVersionUID = 1L;
			@Override
			protected void onUpdate(AjaxRequestTarget target) {					
				otherLinedPartiesPopup.show(target);
			}									
		});		
		but.setOutputMarkupId(true);
		but.setOutputMarkupPlaceholderTag(true);
		Page page = getPage();
		boolean enabled = false;
		if(page instanceof BasePage){
			BasePage basePage = ((BasePage)page);
			enabled = basePage.hasModifyAccess() && pageModel.getSelectedItem()!=null && basePage.checkModificationRules();
		}		
		if(!loggedInUser.isAllowRaise(RequestKindType.MaintainExplicitAgreementPrivileges)){
			but.setEnabled(false);
			but.add(new AttributeModifier("title","You do not have access to the request MaintainOtherLinkedParties, please consult support if you need access"));
		}
		
		but.setVisible(getEditState().isViewOnly() && enabled);
		return but;
	}
	
	private ModalWindow createOtherLinkedPartiesModalWindow(String id) {		
		final ModalWindow window = new ModalWindow(id);
		window.setTitle("Add/Remove Other Linked Parties");	
				
		// Create the page
		window.setPageCreator(new ModalWindow.PageCreator() {
			private static final long serialVersionUID = 1L;
			public Page createPage() {				
				return new AddOtherLinkedParties(pageModel.getPartyOID(),pageModel,otherLinedPartiesPopup,pageModel.getAgreementNumber());		
			}
		});		
//		 Close window call back
		window.setWindowClosedCallback(new ModalWindow.WindowClosedCallback() {
			private static final long serialVersionUID = 1L;
			
			public void onClose(AjaxRequestTarget target) {
				refreshOtherLinkedPartiesList();
				target.add(otherLinkedPartiesGrid);
			}
			
		});
		// Initialise window settings
		setOutputMarkupId(true);
		window.setMinimalHeight(350);
		window.setInitialHeight(400);
		window.setMinimalWidth(750);
		window.setInitialWidth(750);
		window.setMaskType(MaskType.SEMI_TRANSPARENT);
		window.setCssClassName(ModalWindow.CSS_CLASS_GRAY);			
		return window;
	}

	protected void refreshOtherLinkedPartiesList() {
		retrievePartyExplicitAgreements();
	}
	private void retrievePartyExplicitAgreements()  {
		List<DescriptionDTO> explicitPartyRelationsType = new ArrayList<DescriptionDTO>();
		explicitPartyRelationsType.addAll(getBusinessCardGuiController().getExplicitPartyRelationsType());
		pageModel.getMaintainBusinessCardPanelModel().getBusinessCardDetails().setDescriptionDTOs(explicitPartyRelationsType);
		try {
			ContextDTO dto = SRSAuthWebSession.get().getContextDTO();
			ContextPartyDTO partydto = null;
			if(dto != null){
				partydto = dto.getPartyContextDTO();
			}
			if (partydto == null || partydto.getPartyOid() == 0) {
				error("To view agreement details a Party needs to be selected in the context panel above.");
			}else if(partydto.getUacfID() == null){
				error("The selected party does not have a UACFID attached, Please add one to view the agreements linked");
			}
			long partyOID = dto.getPartyContextDTO().getPartyOid();
			List<AgreementPrivilegesDataDTO> agreementPriviledges = getAgreementPrivilegesController().getAgreementsThatTheUserAllowedAccess(partyOID);
			for (AgreementPrivilegesDataDTO agreementPrivilegesDataDTO:agreementPriviledges)
			{
				if (agreementPrivilegesDataDTO.getRelationshipId()>0){
				for (DescriptionDTO descriptionDTO:explicitPartyRelationsType)
				{
						if (descriptionDTO.getUniqId()==agreementPrivilegesDataDTO.getRelationshipId())
						{
							agreementPrivilegesDataDTO.setDescriptionDTO(descriptionDTO);
						}
				}
				}
				agreementPrivilegesDataDTO.setExplicitAgreementType(ExplicitAgreementType.EXPLICT_AGREEMENT);
			}
			pageModel.getMaintainBusinessCardPanelModel().getBusinessCardDetails().getAgreementPriviledges().clear();
			pageModel.getMaintainBusinessCardPanelModel().getBusinessCardDetails().getAgreementPriviledges().addAll(agreementPriviledges);
		} catch (CommunicationException e) {
			logger.error("Communication exception whist retrieving explicit agreements for party OID: "+pageModel.getPartyOID());
			e.printStackTrace();
		} catch (InconsistentDataException e) {
			logger.error("Inconsistance data exception whist retrieving explicit agreements for party OID: "+pageModel.getPartyOID());
		} catch (DataNotFoundException e) {
			logger.info("no explicit agreements found for party OID: "+pageModel.getPartyOID());
		}
	}
	private IAgreementPrivilegesController getAgreementPrivilegesController()
	{
		if (agreementPrivilegesController == null)	{
			try {
				agreementPrivilegesController = ServiceLocator.lookupService(IAgreementPrivilegesController.class);
			} catch (NamingException e) {
				throw new CommunicationException(e);
			}
			return agreementPrivilegesController;
		}
		return agreementPrivilegesController;
	}
	/**
	 * Get the BusinessCardGuiController bean
	 * @return
	 */
	private IBusinessCardGuiController getBusinessCardGuiController(){
		if(businessCardGuiController == null){
			try {
				businessCardGuiController = ServiceLocator.lookupService(IBusinessCardGuiController.class);
			} catch (NamingException e) {
				throw new CommunicationException(e);
			}
		}
		return businessCardGuiController;
	}
	
	
	public Class<BusinessCardOtherLinkedDetailsPanel> getPanelClass() {		
		return BusinessCardOtherLinkedDetailsPanel.class;
	}	
}
