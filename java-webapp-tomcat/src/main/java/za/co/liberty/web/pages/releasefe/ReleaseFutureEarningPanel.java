package za.co.liberty.web.pages.releasefe;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.CreateException;
import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxCallListener;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

import com.inmethod.grid.IGridColumn;
import com.inmethod.grid.SizeUnit;

import za.co.liberty.business.guicontrollers.account.IReleaseFutureEarningGuiController;
import za.co.liberty.business.guicontrollers.contracting.ContractNoEnquiryManagement;
import za.co.liberty.dto.account.DpeMpeDTO;
import za.co.liberty.dto.account.DpeMpeGridDTO;
import za.co.liberty.dto.account.ReleaseFutureEarningDTO;
import za.co.liberty.dto.rating.SRSDescriptionDTO;
import za.co.liberty.exceptions.application.ValidationChainedException;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.ftx.domain.exception.InvalidMPEStatusException;
import za.co.liberty.ftx.domain.exception.MPENotFoundException;
import za.co.liberty.ftx.domain.exception.PMPNotFoundException;
import za.co.liberty.helpers.lookup.ServiceLocator;
import za.co.liberty.interfaces.account.IMoneyProvisionElementFLO;
import za.co.liberty.srs.type.SRSType;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.pages.BasePage;
import za.co.liberty.web.pages.panels.BasePanel;
import za.co.liberty.web.pages.panels.HelperPanel;
import za.co.liberty.web.system.SRSAuthWebSession;
import za.co.liberty.web.wicket.markup.html.grid.GridToCSVHelper;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGrid;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGrid.GridSizeUnit;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGridColumn;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataProviderAdapter;
import za.co.liberty.web.wicket.markup.html.grid.SRSGridRowSelectionCheckBox;
import za.co.liberty.web.wicket.markup.repeater.data.SortableListDataProvider;

/**
 * Release future earnings panel. Will contain call to business layer and all logic for releasing future earnings
 * @author JWV2310
 *
 */
public class ReleaseFutureEarningPanel extends BasePanel implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private transient IReleaseFutureEarningGuiController guiController = null;
	private String SELECTION_FORM_NAME = "selectionForm";
	protected ReleaseFEModel pageModel;
	protected Form selectionForm;
	protected Form enclosingForm;
	
	private HelperPanel panelAGM;
	private Label agmDetail;
	
	private ModalWindow createSearchWindow;
	private Button searchButton = null;
	private SRSDataGrid feGrid = null;
	private Button releaseFEButton = null;
	private BasePage parentPage;
	private EditStateType pageEdit;
	private EditStateType pageState;
	private Button exportResultsButton;
	private boolean status;
	private Panel panelGrid;
	private Form exportForm;
	private static final Logger logger = Logger.getLogger(ReleaseFutureEarningPanel.class);
	private static List<SRSDescriptionDTO> allProdList;
			
	public ReleaseFutureEarningPanel(String id, EditStateType editState, ReleaseFEModel pageModel, BasePage parentPage){
		super(id,editState, parentPage);
		this.parentPage = parentPage;
		pageModel.setLoggedInUser(SRSAuthWebSession.get().getSessionUser().getUacfId());
		guiController = getSessionBean();
		this.pageModel = pageModel;
		status = false;
		if (allProdList == null) {
			ContractNoEnquiryManagement cnoM = new ContractNoEnquiryManagement();
		}
		initiatePanel();
	}
	
	public void initiatePanel(){
		
		add(exportForm = createExportForm("exportForm"));
		add(feGrid = createFeGrid("feGrid"));
		add(releaseFEButton = createReleaseFEButton("releaseFEButton"));		
		populatePanel();
	}

	
	private void populatePanel(){
		status = true;
		ReleaseFutureEarningPanel.this.pageState = EditStateType.MODIFY;
		//we have the selected agreement id
		//now take this id and get all potensial future earnings
		List<ReleaseFutureEarningDTO> allMoneyProvision = new ArrayList<ReleaseFutureEarningDTO>();
		if((pageModel.getContext().getAgreementContextDTO() == null) || pageModel.getContext().getAgreementContextDTO().getAgreementNumber() == null) {
			ReleaseFutureEarningPanel.this.exportResultsButton.setEnabled(false);
			error("Please select agreement into context!");
			return;
		}
			allMoneyProvision = guiController.allMoneyProvisionInclMoneyProvisionElementsList(
					pageModel.getContext().getAgreementContextDTO().getAgreementNumber(), SRSType.FUTUREEARNINGMONEYSCHEDULERFINANCIALTRANSACTIONROLE);
			if(allMoneyProvision.size() < 1){
				error("There was no future earnings found to release for:");
				ReleaseFutureEarningPanel.this.exportResultsButton.setEnabled(false);
				return;
			}else{
				ReleaseFutureEarningPanel.this.exportResultsButton.setEnabled(true);
			}
		//now link this to the DPEs
		List<DpeMpeDTO> allReleFE = guiController.allDPELinkedFutureEarningsRelease(allMoneyProvision);
		pageModel.getAllReleaseFutureEarningList().clear();
		for(DpeMpeDTO e: allReleFE){
			pageModel.getAllReleaseFutureEarningList().add(e);
		}
		List<DpeMpeGridDTO> gridDtoList = new ArrayList<DpeMpeGridDTO>();
		status = true;
		releaseFEButton.setEnabled(true);
		//move to converter - maybe ok due to being called 4 times a month
		for(DpeMpeDTO inst: allReleFE){
			DpeMpeGridDTO instDTO = new DpeMpeGridDTO();
			for(IMoneyProvisionElementFLO mpeInst : inst.getPmpFLO().getMoneyProvisionElements()){
				instDTO.setAccEntryTypeId(mpeInst.getAccEntryTypeId());
				instDTO.setMpeOid(mpeInst.getMpeOid());
				instDTO.setMpeTypeId(mpeInst.getMpeTypeId());
				instDTO.setProductReference(inst.getProductReference());
				instDTO.setAmount(inst.getAmount());
				instDTO.setBaseAmount(mpeInst.getBaseAmount());
				instDTO.setDueDate(mpeInst.getDueDate());
				instDTO.setEndDate(mpeInst.getEndDate());
				instDTO.setPolicyReference(inst.getPolicyReference());
				instDTO.setRequestedDate(inst.getRequestedDate());
				instDTO.setExecutedDate(inst.getExecutedDate());
				instDTO.setNumOfMonths(inst.getPmpFLO().getMoneyProvisionElements().size());
				instDTO.setLifeAssured(inst.getLifeAssured());
				instDTO.setSrsId(inst.getComposite_id());
				instDTO.setReqId(inst.getId());
				instDTO.setNextExecutionDate(inst.getPmpFLO().getNextExecutionDate());
				instDTO.setMpoid(inst.getPmpFLO().getOid());
				instDTO.setProdRefEnum(inst.getProdRefEnum());
				gridDtoList.add(instDTO);
			}
			
		}
		//populate pageModel with new release list options
		pageModel.getGridDtoList().clear();
		for(DpeMpeGridDTO pop: gridDtoList){
			pageModel.getGridDtoList().add(pop);
		}

		releaseFEButton.setEnabled(true);
		pageEdit = EditStateType.MODIFY;

	}
	
	private Form createExportForm(String id) {
		Form expFrm = new Form(id);
		expFrm.add(exportResultsButton = createExportResultButton("exportResultButton"));
		return expFrm;
	}
	private Button createExportResultButton(String id){
		Button ret = new Button(id){
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
					new GridToCSVHelper().createCSVFromDataGrid(feGrid,"agreement_for_node_.csv");
				} catch (Exception e) {	
					logger.error("An error occured when trying to generate the csv file",e);
					e.printStackTrace();
					error("An error occured when trying to generate the csv file");
				}
			}			
		};	

		ret.setOutputMarkupId(true);
		return ret;
	}
	
	//release all selected future earnings on agreement
	private Button createReleaseFEButton(String id){
	 final Button tempButton = new Button(id);
	 tempButton.add(new AjaxFormComponentUpdatingBehavior("click") {
		private static final long serialVersionUID = 6702951764601149491L;
//		@Override
//		protected IAjaxCallDecorator getAjaxCallDecorator() {
//			return new AjaxCallDecorator() {
//				private static final long serialVersionUID = 1L;
//
//				public CharSequence decorateScript(CharSequence script) {
//					String disableCancel = "";
//					if(tempButton != null){
//						disableCancel = "getElementById('"+ tempButton.getMarkupId()+"').disabled=true;";
//					}
//					return "this.disabled=true;"+disableCancel+"overlay(true);" + script;
//				}
//			};
//		}
		
		@Override
		protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
			super.updateAjaxAttributes(attributes);

		        // Way of adding any handler
			attributes.getAjaxCallListeners().add(new AjaxCallListener() {

			  @Override
			  public CharSequence getInitHandler(Component component) {
				CharSequence s =   super.getInitHandler(component);
				
				String disableCancel = "";
				//Fixed FE Release button issue
				if(tempButton != null){
					disableCancel = "document.getElementById('"+ tempButton.getMarkupId()+"').disabled=true;";
				}
				return "this.disabled=true;"+disableCancel+"overlay(true);"+ ((s==null)?"":s);
			  }
						
			  @Override
			  public CharSequence getDoneHandler(Component component) {	
				CharSequence s =  super.getDoneHandler(component);
				return  "hideOverlay();" + ((s==null)?"":s);
			  }
			});
		}
			
			
		@SuppressWarnings("unchecked")
		@Override
			protected void onUpdate(AjaxRequestTarget target) {
				//get only the selected policies to release
				List<Object> selectedItems = feGrid.getSelectedItemObjects();
				if(selectedItems.size() == 0){
					error("Please select something");
					return;
				}
				List<DpeMpeGridDTO> selectedFEReleaseList = (List<DpeMpeGridDTO>)(List<?>)selectedItems;
				List<DpeMpeGridDTO> updReleaseCommList = new ArrayList<DpeMpeGridDTO>(); 
				try {
					updReleaseCommList = guiController.releaseAllFutureEarningsForPolicyList3(selectedFEReleaseList, pageModel.getLoggedInUser());
					pageState = EditStateType.VIEW;
				} catch (PMPNotFoundException e1) {
					error("PMP could not be found:" + e1.getMessage());
				} catch (MPENotFoundException e1) {
					error("MPE could not be found:" + e1.getMessage());
				} catch (ValidationChainedException e1) {
					error("Validation failed as elements was not found:" + e1.getMessage());
				} catch (InvalidMPEStatusException e1) {
					error("Money provision Status exception:" + e1.getMessage());
				} catch (CreateException e1) {
					error("Error releasing commission:"+ e1.getMessage());
				}
				
				pageModel.getRetrieveAllUnreleaseCommission().clear();
				pageModel.getGridDtoList().clear();
				for(DpeMpeGridDTO commInst: updReleaseCommList){
					pageModel.getGridDtoList().add(commInst);
				}
				pageState = EditStateType.VIEW;
				info("Successfully released + " + pageModel.getGridDtoList().size() +" future earnings");
				info("Note: The money scheduler will have to run on the next batch to release these earnings to the current account");
				target.add(parentPage.getFeedbackPanel());
				
				ReleaseFutureEarningPanel.this.pageState = EditStateType.VIEW;
				
				SRSDataGrid grid = createFeGrid("feGrid");
				feGrid.replaceWith(grid);
				feGrid = grid;
				if (target!=null) {
					target.add(feGrid);
				}
				releaseFEButton.setEnabled(false);
				
				target.add(feGrid);
				target.add(releaseFEButton);
		}
	 	});
	 	
	 	tempButton.setOutputMarkupId(true);
	 	status = false;
	 	tempButton.setEnabled(false);
	 return tempButton;
		 
	 }
	

	
	private String getPageName(){
		return "Release Future Earning Panel";
	}
	
	protected IReleaseFutureEarningGuiController getSessionBean() {
		if (guiController == null) {
			try {
				guiController = ServiceLocator.lookupService(IReleaseFutureEarningGuiController.class);
			} catch (NamingException namingErr) {
				logger.error(this.getPageName()
						+ " IReleaseFutureEarningsGUIController can not be lookedup:"
						+ namingErr.getMessage());
				CommunicationException comm = new CommunicationException("IReleaseFutureEarningGuiController can not be looked up!");
				throw new CommunicationException(comm);
			} 
		}
		return guiController;
	}
	
	public void enableCheck(Component comp, boolean value){
		comp.setEnabled(value);
	}
	
	public SRSDataGrid createFeGrid(String id){
		SRSDataGrid tempDataGrid 
			= new SRSDataGrid(id,new SRSDataProviderAdapter(
					new SortableListDataProvider<DpeMpeGridDTO>(pageModel.getGridDtoList())),createSearchResultColumns(),getEditState());
		tempDataGrid.setAutoResize(true);
		tempDataGrid.setOutputMarkupId(true);
		tempDataGrid.setCleanSelectionOnPageChange(false);
		tempDataGrid.setClickRowToSelect(false);
		tempDataGrid.setAllowSelectMultiple(true);
		tempDataGrid.setGridWidth(850, GridSizeUnit.PIXELS);		
		tempDataGrid.setRowsPerPage(10);
		tempDataGrid.setContentHeight(199, SizeUnit.PX);

		return tempDataGrid;
	}
	@SuppressWarnings("unchecked")
	protected List<IGridColumn> createSearchResultColumns() { 
		List<IGridColumn> columns = new ArrayList<IGridColumn>();
		
		//only display this check box if edit state not in view state- will be applicable for add and edit
		if(pageState != EditStateType.VIEW) {
				columns.add(new SRSGridRowSelectionCheckBox("check")
				.setInitialSize(30)
			    );
		}	
		//nextExecutionDate
		//startdate
		columns.add(new SRSDataGridColumn<DpeMpeGridDTO>("nextExecutionDate",
				new Model("nextExecutionDate"), "nextExecutionDate" ,"nextExecutionDate", getEditState())
		.setInitialSize(120)
		);
		columns.add(new SRSDataGridColumn<DpeMpeGridDTO>("startDate",
				new Model("startDate"), "startDate" ,"startDate", getEditState())
		.setInitialSize(120)
		);
		columns.add(new SRSDataGridColumn<DpeMpeGridDTO>("mpeOid",
				new Model("mpeOid"), "mpeOid" ,"mpeOid", getEditState())
		.setInitialSize(120)
		);
		columns.add(new SRSDataGridColumn<DpeMpeGridDTO>("endDate",
				new Model("endDate"), "endDate" ,"endDate", getEditState())
		.setInitialSize(120)
		);
		columns.add(new SRSDataGridColumn<DpeMpeGridDTO>("mpoid",
				new Model("mpoid"), "mpoid" ,"mpoid", getEditState())
		.setInitialSize(120)
		);
		columns.add(new SRSDataGridColumn<DpeMpeGridDTO>("dueDate",
				new Model("Due Date"), "dueDate" ,"dueDate", getEditState())
		.setInitialSize(120)
		);
		columns.add(new SRSDataGridColumn<DpeMpeGridDTO>("policyReference",
				new Model("policyReference"), "policyReference" , "policyReference",getEditState())
		.setInitialSize(120)
		);
		columns.add(new SRSDataGridColumn<DpeMpeGridDTO>("amount",
				new Model("amount"), "amount" ,"amount", getEditState())
		.setInitialSize(120)
		);	
		columns.add(new SRSDataGridColumn<DpeMpeGridDTO>("baseAmount",
				new Model("baseAmount"), "baseAmount" ,"baseAmount" , getEditState())
		.setInitialSize(120)
		);
		columns.add(new SRSDataGridColumn<DpeMpeGridDTO>("executedDate",
				new Model("Req exec Date"), "executedDate" ,"executedDate" , getEditState())
		.setInitialSize(120)
		);
		columns.add(new SRSDataGridColumn<DpeMpeGridDTO>("productReference",
				new Model("productreference"), "productReference" ,"productReference" , getEditState())
		.setInitialSize(120)
		);
		columns.add(new SRSDataGridColumn<DpeMpeGridDTO>("lifeAssured",
				new Model("lifeAssured"), "lifeAssured" ,"lifeAssured" , getEditState())
		.setInitialSize(120)
		);
		columns.add(new SRSDataGridColumn<DpeMpeGridDTO>("numOfMonths",
				new Model("Num of Months"), "numOfMonths" , getEditState())
		.setInitialSize(120)
		);
		columns.add(new SRSDataGridColumn<DpeMpeGridDTO>("prodRefEnum",
				new Model("prodRefEnum"), "prodRefEnum" , getEditState())
		.setInitialSize(120)
		);
		
		return columns;
	}
	
}
