package za.co.liberty.web.pages.agreementprivilege;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import com.inmethod.grid.DataProviderAdapter;
import com.inmethod.grid.IGridColumn;
import com.inmethod.grid.SizeUnit;

import za.co.liberty.database.enums.DatabaseEnumHelper;
import za.co.liberty.dto.agreementprivileges.AgreementPrivilegesDataDTO;
import za.co.liberty.dto.contracting.ResultPartyDTO;
import za.co.liberty.dto.databaseenum.JobTitleDBEnumDTO;
import za.co.liberty.dto.gui.context.ResultContextItemDTO;
import za.co.liberty.dto.party.EmployeeDTO;
import za.co.liberty.web.data.enums.ContextType;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.pages.agreementprivilege.model.AgreementLinkingPageModel;
import za.co.liberty.web.pages.interfaces.IStatefullComponent;
import za.co.liberty.web.pages.panels.HelperPanel;
import za.co.liberty.web.pages.search.ContextSearchPopUp;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGrid;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGrid.GridSizeUnit;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGridColumn;
import za.co.liberty.web.wicket.markup.html.grid.SRSGridRowSelectionCheckBox;
import za.co.liberty.web.wicket.markup.repeater.data.SortableListDataProvider;

/**
 * This panel contains the ability for users to assign other users access to his
 * agreements
 * 
 * @author DZS2610
 * 
 */
public class AgreementAssignmentPanel extends Panel implements
		IStatefullComponent {
	// TODO rather use AbstractLinkingPanel -- when rebased
	private static final long serialVersionUID = 1L;

	private SearchForm form;

	AgreementLinkingPageModel pageModel;

	EditStateType editState;

	/**
	 * @param arg0
	 */
	public AgreementAssignmentPanel(String id,
			AgreementLinkingPageModel pageModel, EditStateType editState) {
		super(id);
		this.pageModel = pageModel;
		this.editState = editState;		
		add(form = new SearchForm("searchForm"));

	}

	private class SearchForm extends Form {
		private static final long serialVersionUID = 1L;

		ModalWindow modalWindow;

		private SRSDataGrid grid;

		@SuppressWarnings("unused")
		private Button addButton;

		@SuppressWarnings("unused")
		private Button removeButton;

		private ArrayList<AgreementPrivilegesDataDTO> gridData;;

		private SearchForm(String id) {
			super(id);
			add(modalWindow = createModalWindow("modalWindow"));
			add(grid = getDataGrid());
			add(addButton = getAddButton("add", this));
			add(removeButton = getRemoveButton("remove", this));
		}

		protected ModalWindow createModalWindow(String id) {

			ContextSearchPopUp popUp = new ContextSearchPopUp() {
				private static final long serialVersionUID = 1L;
				
				@Override
				public ContextType getContextType() {
					return ContextType.PARTY_ONLY;
				}

				@Override
				public void doProcessSelectedItems(AjaxRequestTarget target,
						ArrayList<ResultContextItemDTO> selectedItemList) {
					if (selectedItemList.size() == 0) {
						// Nothing was selected
						return;
					}
					//adding party to agreement
					for (ResultContextItemDTO contextItemDTO : selectedItemList) {
						ResultPartyDTO resultPartyDTO = contextItemDTO
								.getPartyDTO();
						if(pageModel.getPartyOid() == resultPartyDTO.getPartyOid()){
							return;
						}				
						
						EmployeeDTO person = new EmployeeDTO();						
						person.setOid(resultPartyDTO.getPartyOid());		
						person.setFirstName(resultPartyDTO.getName());
						person.setSurname(resultPartyDTO.getLastName());
						person.setDateOfBirth(resultPartyDTO.getDateOfBirth());		
						person.setJobTitle((resultPartyDTO.getJobTitle() == null) ? null : (JobTitleDBEnumDTO) DatabaseEnumHelper.getDatabaseEnumUsingName(JobTitleDBEnumDTO.class, resultPartyDTO.getJobTitle()));
						person.setIdentificationNumber(resultPartyDTO.getIdNumber());
						
						pageModel.getPersonDTOList().add(person);
						AgreementPrivilegesDataDTO newDTO = processLinkAgreementToPerson(person);
						if(newDTO != null){
							gridData.add(newDTO);
						}
					}
					if (target != null) {
						target.add(grid);
					}
				}
			};

			return popUp.createModalWindow(id);

		}

		/**
		 * Add AgreementPrivilegesDataDTO object to grid using PersonDTO
		 * Effectivly giving access to agreement
		 * @param person
		 * @return
		 */
		private AgreementPrivilegesDataDTO processLinkAgreementToPerson(EmployeeDTO person)
	    {		   
				if (isAlreadyLinked(person.getOid(), pageModel.getAgreementNo()))
		    		{
		    			error("Person : " + person.getFirstName() + " has already been linked to this agreement : " + pageModel.getAgreementNo());
		    			return null;
		    		}
				AgreementPrivilegesDataDTO newLinkAgreementDTO = (AgreementPrivilegesDataDTO) ((AgreementPrivilegesDataDTO)pageModel.getSelectedItem()).clone();
				//reset values
				newLinkAgreementDTO.setExplicitAgreementID(0L);					
				newLinkAgreementDTO.setPartyProfileOid(0);		
				newLinkAgreementDTO.setAddAccess(false);
				newLinkAgreementDTO.setModifyAccess(false);
				newLinkAgreementDTO.setDelAccess(false);
				newLinkAgreementDTO.setVersion(1);
				
				newLinkAgreementDTO.setPartyOid(person.getOid());	
		    	newLinkAgreementDTO.setName(person.getFirstName());
		    	newLinkAgreementDTO.setSurname(person.getSurname());
		    	newLinkAgreementDTO.setViewerName(person.getFirstName() + " " +person.getSurname());
		    	newLinkAgreementDTO.setViewerJobTitle((person.getJobTitle() == null) ? null : person.getJobTitle().getName());												
		    	newLinkAgreementDTO.setEffectiveFrom(Calendar.getInstance().getTime());
		    	newLinkAgreementDTO.setEffectiveTo(null);
		    	pageModel.getAcessGrantedOwnAgreementList().add(newLinkAgreementDTO);
		    	return newLinkAgreementDTO;		    	
	    }

		/**
		 * Check if a user has already been linked to the given agreement
		 * @param partyOid
		 * @param agreementNo
		 * @return
		 */
		private boolean isAlreadyLinked(long partyOid, long agreementNo) {
			boolean result = false;			
			if(pageModel.getAcessGrantedOwnAgreementList() != null){
			for (AgreementPrivilegesDataDTO agreementPrivilegesDataDTO : pageModel.getAcessGrantedOwnAgreementList()) {
				if ((agreementPrivilegesDataDTO.getPartyOid() == partyOid)
						&& (agreementPrivilegesDataDTO.getAgreementOID() == agreementNo)) {
					result = true;
				}
			}
			}
			return result;
		}
		
		/**
		 * get the add button for this panel
		 * 
		 * @return
		 */
		private Button getAddButton(String id, Form form) {
			return (Button) new AjaxButton(id, form) {
				private static final long serialVersionUID = 1L;

				@Override
				protected void onSubmit(AjaxRequestTarget target, Form form)

				{
					super.onSubmit();
					modalWindow.show(target);
				}

			}.setVisible(editState != EditStateType.VIEW);
		}

		/**
		 * get the remove button for this panel
		 * 
		 * @return
		 */
		private Button getRemoveButton(String id, Form form) {
			return (Button) new AjaxButton(id, form) {
				private static final long serialVersionUID = 1L;

				@Override
				protected void onSubmit(AjaxRequestTarget target, Form form)

				{
					super.onSubmit();
					pageModel.getAcessGrantedOwnAgreementList()
					.removeAll(grid.getSelectedItemObjects());		
					gridData.removeAll(grid.getSelectedItemObjects());
					target.add(grid);
				}

			}.setVisible(editState != EditStateType.VIEW);
		}

		/**
		 * Get the datagrid for this panel
		 * 
		 * @return
		 */
		private SRSDataGrid getDataGrid() {
			if (pageModel.getPersonDTOList() != null) {
				gridData = new ArrayList<AgreementPrivilegesDataDTO>(pageModel.getAcessGrantedOwnAgreementList());
			} else {
				gridData = new ArrayList<AgreementPrivilegesDataDTO>();
			}
			grid = new SRSDataGrid(
					"viewableByOthersGrid",
					new DataProviderAdapter(
							new SortableListDataProvider<AgreementPrivilegesDataDTO>(
									gridData)), getGridColumns(), null);
			grid.setCleanSelectionOnPageChange(false);
			grid.setClickRowToSelect(false);
			grid.setRowsPerPage(10);
			grid.setContentHeight(100, SizeUnit.PX);
			grid.setAllowSelectMultiple(true);
			grid.setGridWidth(630, GridSizeUnit.PIXELS);
			return grid;
		}

		/**
		 * get the grid columns
		 * 
		 * @return
		 */
		private List<IGridColumn> getGridColumns() {
			Vector<IGridColumn> columns = new Vector<IGridColumn>(5);
			if (editState != EditStateType.VIEW) {
				columns.add(new SRSGridRowSelectionCheckBox("checkbox")
						.setInitialSize(30));
			}			
		   columns.add(new SRSDataGridColumn<AgreementPrivilegesDataDTO>("viewerName",new Model("Viewer Name"),"viewerName","viewerName", editState).setInitialSize(200));
	       columns.add(new SRSDataGridColumn<AgreementPrivilegesDataDTO>("viewerJobTitle",new Model("Viewer Job Title"),"viewerJobTitle","viewerJobTitle", editState).setInitialSize(200));
	       columns.add(new SRSDataGridColumn<AgreementPrivilegesDataDTO>("effectiveFrom",new Model("Start Date"),"effectiveFrom","effectiveFrom", editState).setInitialSize(100));
	       if (editState != EditStateType.VIEW) {
	    	   columns.add(new SRSDataGridColumn<AgreementPrivilegesDataDTO>("effectiveTo",new Model("End Date"),"effectiveTo","effectiveTo", editState){
				private static final long serialVersionUID = 1L;
				@Override
				public Panel newCellPanel(WebMarkupContainer parent, String componentId, IModel rowModel, String objectProperty, EditStateType state, AgreementPrivilegesDataDTO data) {
					final TextField endDateSelection = new TextField("value",new PropertyModel(data,objectProperty), Date.class);
					endDateSelection.add(new AttributeModifier("maxlength","10"));
					endDateSelection.add(new AttributeModifier("style","width:85px"));
					//endDateSelection.add(new SimpleAttributeModifier("editable","false"));
					return HelperPanel.getInstance(componentId, endDateSelection, true);
				}	    		   
	    	   }.setInitialSize(200));
			}else{
				columns.add(new SRSDataGridColumn<AgreementPrivilegesDataDTO>("effectiveTo",new Model("End Date"),"effectiveTo","effectiveTo", editState).setInitialSize(100));
			}
	       return columns;
		}
	}

	public EditStateType getEditState() {
		return editState;
	}

	public Class getPanelClass() {		
		return AgreementAssignmentPanel.class;
	}
}