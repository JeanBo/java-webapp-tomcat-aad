package za.co.liberty.web.pages.admin;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxFallbackButton;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AbstractAutoCompleteRenderer;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.Response;
import org.apache.wicket.util.string.Strings;

import za.co.liberty.business.converter.party.profile.TeamConverter;
import za.co.liberty.business.guicontrollers.userprofiles.ITeamGUIController;
import za.co.liberty.dto.contracting.ResultPartyDTO;
import za.co.liberty.dto.gui.context.ResultContextItemDTO;
import za.co.liberty.dto.userprofiles.PartyProfileDTO;
import za.co.liberty.dto.userprofiles.RequestCategoryDTO;
import za.co.liberty.dto.userprofiles.TeamDTO;
import za.co.liberty.dto.userprofiles.TeamPartiesDTO;
import za.co.liberty.exceptions.data.DataNotFoundException;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.helpers.lookup.ServiceLocator;
import za.co.liberty.web.data.enums.ContextType;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.pages.MaintenanceBasePage;
import za.co.liberty.web.pages.MaintenanceBasePage.SelectionForm;
import za.co.liberty.web.pages.admin.models.TeamModel;
import za.co.liberty.web.pages.panels.BasePanel;
import za.co.liberty.web.pages.search.ContextSearchPopUp;
import za.co.liberty.web.wicket.markup.html.form.SRSDropDownChoice;
import za.co.liberty.web.wicket.markup.html.form.SRSTextField;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGrid;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGridColumn;
import za.co.liberty.web.wicket.markup.html.grid.SRSGridRowSelectionCheckBox;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGrid.GridSizeUnit;
import za.co.liberty.web.wicket.markup.repeater.data.ListDataProvider;

import com.inmethod.grid.DataProviderAdapter;
import com.inmethod.grid.IGridColumn;
import com.inmethod.grid.SizeUnit;

/**
 * This will create and display the admin team's information in a panel, where team members can be added or removed per team
 * @author JWV2310
 *
 */
public class TeamAdminPanel extends BasePanel {
	
	private static final long serialVersionUID = 5615959490908548660L;

	private TeamModel pageModel;
	protected Object bean;
	protected SelectionForm selectionForm;
	public EditStateType pageEditState;
	private ModalWindow createSearchWindow;
	private static final Logger logger = Logger.getLogger(TeamAdminPanel.class);
	
	private transient ITeamGUIController guiController;
	
	//Initialise components
	protected List<IGridColumn> searchResultColumns;
	private SRSDropDownChoice teamCategoryCombo = null;
	private SRSTextField teamName = null;
	private SRSDataGrid teamGrid = null;
	private Label teamNameLabel = null;
	private Label teamCategoryNameLabel = null;
	private Label uacfIdLabel = null;
	private AutoCompleteTextField field = null;
	private MaintenanceBasePage parentPage;
	private Button addButton;
	private Button removeButton;
	private Button searchButton;
	
	private boolean status;
	
	/*
	 * Default constructor
	 */
	public TeamAdminPanel(String id, EditStateType editState, TeamModel pageModel, MaintenanceBasePage parentPage) {
			super(id, editState);
			this.pageModel = pageModel;
			this.parentPage = parentPage;
			status = getEditState() == EditStateType.ADD || getEditState() == EditStateType.MODIFY;
			initialise();
	}
	
	public void visibilityCheck(Component comp, boolean value){
		comp.setVisible(value);
	}
	
	public void enableCheck(Component comp, boolean value){
		comp.setEnabled(value);
	}

	/**
	 * Initialise the page components
	 */
	protected void initialise() {
		
		add(teamCategoryCombo = createRequestCategoryCombo());
		add(field = createAutoCombo());
		add(addButton = createAddButton("addButton"));
		add(uacfIdLabel = createUacfIdLabel("uacfId"));
		add(teamCategoryNameLabel = createTeamCategoryNameLabel("teamCategoryNameLabel"));
		add(teamName = createTeamName("teamName"));
		add(teamNameLabel = createTeamNameLabel("teamNameLabel"));
		add(createSearchWindow = createSearchWindow("searchSecurityWindow"));
		add(searchButton = createSearchButton("searchButton"));
		add(removeButton = createRemoveButton("removeButton"));
		add(teamGrid = createGrid());
	}
	
	public SRSDropDownChoice createRequestCategoryCombo() {
		SRSDropDownChoice tempSRSDropDown = new SRSDropDownChoice("CategoryLink",
				new PropertyModel(pageModel.getSelectedItem(),"defaultRequestCategoryDTO"),
				pageModel.getAllRequestCategories(),new ChoiceRenderer() {
				private static final long serialVersionUID = 1L;
					public Object getDisplayValue(Object arg0) {
					   if (arg0==null) {
						   return null;
					   }
					   return ((RequestCategoryDTO)arg0).getName();
					}
					public String getIdValue(Object arg0, int arg1) {
						   if (arg0==null) {
							   return null;
						   }
						   return ""+((RequestCategoryDTO)arg0).getId();
					}
			
			},"Select One");
			tempSRSDropDown.setOutputMarkupId(true);
			tempSRSDropDown.add(new AjaxFormComponentUpdatingBehavior("change"){
			private static final long serialVersionUID = 1L;
						
			@Override
			protected void onUpdate(AjaxRequestTarget arg0) {
				
			}	
		});
		tempSRSDropDown.setVisible(status);

		return (SRSDropDownChoice) tempSRSDropDown;
	}
	
	public Label createTeamNameLabel(String id){
		Label tempTeamLabel = new Label(id, "Team Name");
		tempTeamLabel.setEscapeModelStrings(false);
        if(getEditState() == EditStateType.ADD || getEditState() == EditStateType.MODIFY){
        	tempTeamLabel.setVisible(true);
        }else{
        	tempTeamLabel.setVisible(false);
        }
        return tempTeamLabel;
	}
	
	public Label createTeamCategoryNameLabel(String id){
		Label tempTeamCategoryLabel = new Label(id, "Request Category:");
		tempTeamCategoryLabel.setEscapeModelStrings(false);
        tempTeamCategoryLabel.setVisible(status);
        
        return tempTeamCategoryLabel;
	}
	//UACF
	public Label createUacfIdLabel(String id){
		Label uacfIdLabel = new Label(id, "UACF ID:");
		uacfIdLabel.setEscapeModelStrings(false);
        add(uacfIdLabel);
        uacfIdLabel.setVisible(status);

        return uacfIdLabel;
	}
	
	/*
	 * Create Team textbox for when a new Team name must be entered. (Only applicable when add edit state is active)
	 */
	public SRSTextField createTeamName(String id){
		SRSTextField tempSRSTextField = new SRSTextField(id,new PropertyModel(pageModel.getSelectedItem(),"teamName" ));
		tempSRSTextField.setVisible(status);
		
		return tempSRSTextField;
	}

	/**
	 * Create the autocomplete wicket combo box - will be for selecting the UACF id
	 * Validation: When a UACF id exists in the grid, it will be excluded from the grid. 
	 * Adding a UACF id to the grid, one can not select this id again from the auto complete comb
	 * Removing a UACF id from the grid,  
	 */
	public AutoCompleteTextField createAutoCombo()  {
			AutoCompleteTextField tempAutoCompleteText = 
		           new AutoCompleteTextField("gridButtonPanel",
						new PropertyModel(pageModel,"autoUacfid"),new AbstractAutoCompleteRenderer<PartyProfileDTO>(){
						private static final long serialVersionUID = 1L;
						
						@Override
							protected String getTextValue(PartyProfileDTO resultParty) {
							  return ((PartyProfileDTO) resultParty).getSecurityID();
							}
						@Override
							protected void renderChoice(PartyProfileDTO resultParty, Response res, String arg2) {
								res.write(((PartyProfileDTO) resultParty).getSecurityID() + "");
							}
						}
				   )
		    {			
			private static final long serialVersionUID = 1L;

			@SuppressWarnings("unchecked")
			protected Iterator getChoices(String input)
			{	

				if (Strings.isEmpty(input) || input.length() < 3 || input.length() > 7)
				{	
					return Collections.EMPTY_LIST.iterator();
				}
				List<PartyProfileDTO> list = null;
				list = getSessionBean().findUserStartingWithFastLane(input);
				list = getSessionBean().subtractTeamPartiesFromAutoBox(list, pageModel.getSelectedItem().getSelectedTeamPartiesList());
				
				return list.iterator();
			}
		};
		tempAutoCompleteText.add(new AjaxFormComponentUpdatingBehavior("change"){
			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(AjaxRequestTarget arg0) {
				if(pageModel.getAutoUacfid() == null || pageModel.getAutoUacfid() == "" || pageModel.getAutoUacfid().length() < 7) {
					error("Selected UACF must be a valid selected UACF id! Please search for a valid uacf id.");
					arg0.add(parentPage.getFeedbackPanel());
					enableCheck(addButton, false);
					arg0.add(addButton);
				}else{
					enableCheck(addButton, true);
					arg0.add(addButton);
				}	
			}
			
			protected void onComponentTag(ComponentTag tag) {
				super.onComponentTag(tag);
				tag.put("field", getId());
				//disable browser's autocompletion on this field
				tag.put("autocomplete", "off");
			}
		});
		tempAutoCompleteText.setVisible(status);
		tempAutoCompleteText.setOutputMarkupId(true);
		
		return tempAutoCompleteText;
	}
	
	/**
	 * Create the grid which will display the Team Parties for this team
	 */
	public SRSDataGrid createGrid(){
		
		SRSDataGrid tempDataGrid 
		= new SRSDataGrid("PartyLink",new DataProviderAdapter(
					new ListDataProvider<TeamPartiesDTO>(pageModel.getSelectedItem().getSelectedTeamPartiesList())),createSearchResultColumns(),getEditState());
		tempDataGrid.setAutoResize(true);
		tempDataGrid.setOutputMarkupId(true);
		tempDataGrid.setCleanSelectionOnPageChange(false);
		tempDataGrid.setClickRowToSelect(false);
		tempDataGrid.setAllowSelectMultiple(true);
		tempDataGrid.setGridWidth(450, GridSizeUnit.PIXELS);		
		tempDataGrid.setRowsPerPage(10);
		tempDataGrid.setContentHeight(199, SizeUnit.PX);
		
		return tempDataGrid;
	}
	
	/**
	 * Create add functionality for adding the UAICF id from the auto complete text box, into the grid
	 */
	private Button createAddButton(String id) {
		Button button = new Button(id);
		enableCheck(button, false);
		button.add(new AjaxFormComponentUpdatingBehavior("click") {
		private static final long serialVersionUID = 6702951764601149491L;
		@Override
			protected void onUpdate(AjaxRequestTarget target) {
			   try {
				    
					ResultPartyDTO selectedUsersDetail = null;
					TeamPartiesDTO selectedUserTeamPartiesDTO = new TeamPartiesDTO();
					TeamConverter teamConverter = new TeamConverter();
					//when a uacf id is passed back from the search screen, and modified, it must adhere to the rules
					if(pageModel.getAutoUacfid().length() > 7) {
						   error("Please select a valid UACF id:");
						   target.add(parentPage.getFeedbackPanel());
						   enableCheck(addButton, false);
						   return;
					}
					selectedUsersDetail = getSessionBean().prepareSelectedUserToAdd(pageModel.getAutoUacfid());
					teamConverter.convertResultPartyDTOtoTeamPartyDTO(pageModel.getSelectedItem(), selectedUsersDetail,selectedUserTeamPartiesDTO,pageModel.getAutoUacfid());
					pageModel.getSelectedItem().getSelectedTeamPartiesList().add(selectedUserTeamPartiesDTO);
					target.add(teamGrid);
					if(pageModel.getAutoUacfid() != null){
					   pageModel.setAutoUacfid("");
					}
					enableCheck(addButton, false);
					target.add(addButton);
					target.add(field);
			   }catch(DataNotFoundException e){
				    error("An error occurred adding the party to the grid:" + e.getMessage());
				    target.add(parentPage.getFeedbackPanel());	
			   }
			}
		});
		button.setOutputMarkupId(true);
		button.setVisibilityAllowed(status);

		return button;
	}
	
	/**
	 * Remove the selected instance objects from the grid (add back to the combo box for selection)
	 */
	private Button createRemoveButton(String id) {
		Button button =  new Button(id);
		     
		button.add(new AjaxFormComponentUpdatingBehavior("click") {
			private static final long serialVersionUID = 0L;
			
			@Override
			protected void onUpdate(AjaxRequestTarget target) {					
				target.add(teamGrid);					
				List<Object> objGrid = teamGrid.getSelectedItemObjects();
				pageModel.getSelectedItem().getSelectedTeamPartiesList().removeAll(objGrid);
				target.add(teamGrid);			
			}
		});	
		button.setOutputMarkupId(true);
		button.setVisible(status);
		return button;
	}
	
	/**
	 * @Return create the contents of the columns of the grid displaying team party details.
	 */
	protected List<IGridColumn> createSearchResultColumns() { 
			List<IGridColumn> columns = new ArrayList<IGridColumn>();
			
			//only display this check box if edit state not in view state- will be applicable for add and edit
			if(getEditState() !=  EditStateType.VIEW){
				columns.add(new SRSGridRowSelectionCheckBox("check")
				.setInitialSize(30)
			    );
			}
			//display the party (@Override toString of ResultPartyDTO feedback on search for party with partyoid)
			columns.add(new SRSDataGridColumn<ResultPartyDTO>("party",
					new Model("Party Name"), "party" , getEditState())
			.setInitialSize(320)
			);
			
			//display the UACF id for this party. (Search completed in business layer which retrieved the search results and
			columns.add(new SRSDataGridColumn<TeamPartiesDTO>("autoUacfID",
					new Model("UACF ID"),"party.uacfID",getEditState())
			.setInitialSize(100)
			);

			return columns;
	}

	/**
	 * Retrieve the guicontroller interface team management
	 */
	protected ITeamGUIController getSessionBean()  {
		if (guiController == null) {
			try {
				guiController = ServiceLocator.lookupService(ITeamGUIController.class);
			} catch (NamingException namingErr) {
				logger.error("ITeam GuiController can't be lookedup:" + namingErr);
				CommunicationException comm = new CommunicationException("Error retrieving team gui controller!");
				comm.initCause(namingErr);
				throw comm;
			}
		}
		return guiController;
	}
	
	private ModalWindow createSearchWindow(String id){
		ContextSearchPopUp popUp = new ContextSearchPopUp() {

			private static final long serialVersionUID = 1L;

			@Override
			public ContextType getContextType() {
				return ContextType.PARTY_PERSON_ONLY;
			}

			@Override
			public void doProcessSelectedItems(AjaxRequestTarget target,
				ArrayList<ResultContextItemDTO> selectedItemList) {
				boolean flag = true;
				guiController = getSessionBean();
				flag = getSessionBean().isPartyDetailsInGrid(pageModel.getSelectedItem().getSelectedTeamPartiesList(), selectedItemList);
								
				if(flag){
					for(ResultContextItemDTO resultPopUp : selectedItemList){
					   pageModel.setAutoUacfid(resultPopUp.getPartyDTO().getUacfID());
					   target.add(field);
					   enableCheck(addButton, true);
					   target.add(addButton);
					}
				}else{
					pageModel.setAutoUacfid(null);
					error("The selected party already exists in the grid.");
				}
				target.add(parentPage.getFeedbackPanel());				
				target.add(field);
			}
		};		
		
		ModalWindow win = popUp.createModalWindow(id);
//		win.setPageMapName("uacfidSearchPageMap"); 
		// #WICKETTEST #WICKETFIX Ensure this is tested as part of the modal window
		win.setCookieName("uacfidSearchPageMap");
		return win;
	}
	
	public void doProcessSearchResult(AjaxRequestTarget target,
			ArrayList<TeamDTO> selectedItemList){
	}
	
	protected Form enclosingForm; 
	
	protected Button createSearchButton(String id) {
		
		    Button searchButton = new AjaxFallbackButton(id, this.selectionForm) {
			private static final long serialVersionUID = -5330766713711809772L;

			@Override
			protected void onComponentTag(ComponentTag tag) {
				super.onComponentTag(tag);
				tag.getAttributes().put("value", "Search");
				tag.getAttributes().put("type", "submit");
			}
			@Override
			protected void onSubmit(AjaxRequestTarget target, Form form) {
				createSearchWindow.show(target);
			}
		};
		searchButton.setOutputMarkupId(true);
		searchButton.setVisible(status);
		return searchButton;
	}

}