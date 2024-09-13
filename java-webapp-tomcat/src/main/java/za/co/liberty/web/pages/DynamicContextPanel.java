package za.co.liberty.web.pages;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.core.util.lang.PropertyResolver;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow.CloseButtonCallback;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow.WindowClosedCallback;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;


import za.co.liberty.business.guicontrollers.IContextManagement;
import za.co.liberty.dto.contracting.ResultAgreementDTO;
import za.co.liberty.dto.gui.context.ResultContextItemDTO;
import za.co.liberty.dto.userprofiles.ContextDTO;
import za.co.liberty.exceptions.SystemException;
import za.co.liberty.exceptions.data.DataNotFoundException;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.helpers.lookup.ServiceLocator;
import za.co.liberty.srs.type.SRSType;
import za.co.liberty.web.data.enums.ContextType;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.pages.interfaces.IChangeableStatefullComponent;
import za.co.liberty.web.pages.maintainagreement.MaintainAgreementPage;
import za.co.liberty.web.pages.panels.HelperPanel;
import za.co.liberty.web.pages.search.ContextSearchPopUp;
import za.co.liberty.web.pages.search.models.ContextSearchPageOptions;
import za.co.liberty.web.system.EJBReferences;
import za.co.liberty.web.system.SRSAuthWebSession;
import za.co.liberty.web.wicket.markup.html.form.SRSDropDownChoice;

/**
 * <p>Represents the logged in user's context panel. Renders a dynamic table of 
 * description and value pairs with button(s) in the last row and most right
 * column.  So if there are three columns and three rows then there can be a 
 * maximum of 11 item pairs.
 * </p>
 * 
 * @author JZB0608 - 21 May 2008
 *
 */
public class DynamicContextPanel extends Panel implements Serializable, 
	IChangeableStatefullComponent {

	/* Constants */
	private static final long serialVersionUID = -8653457109096162346L;
	public static final int MAX_COLUMNS = 3;
	private static final Logger logger = Logger.getLogger(DynamicContextPanel.class);
	
	/* Attributes */
	private List<ContextRow> rowList;
	private BasePage parentPage;
	private ContextDTO contextDTO;
	private ResultAgreementDTO selectedResultAgreementDTO;
	private EditStateType editState;
	
	/* Form components */
	private ModalWindow modalWindow;
	@SuppressWarnings("unused")
	private Button searchButton;
	
	private ContextForm form;
	
	private DropDownChoice agmtDropDown;
	
	private  SRSDropDownChoice<Long> servicedAdvisorsPanel; 
	
	private boolean panelDisabled;
	
	private transient IContextManagement  contextManagement;
	
	/**
	 * Default constructor
	 * 
	 * @param id
	 * @param parentPage
	 */
	public DynamicContextPanel(String id, BasePage parentPage) {
		super(id);
		this.parentPage = parentPage;
		// TODO Pass edit state
		this.editState = EditStateType.VIEW;
		initialiseModel();
		add(form = new ContextForm("contextForm"));
		
	}
	
	/**
	 * Initialise the page data model.  Currently limited to a 
	 * default model for everyone.
	 * 
	 */
	private void initialiseModel() {
		rowList = new ArrayList<ContextRow>();       
		
		/* Ensure required context is selected */
		contextDTO = parentPage.getPageContextDTO();
		if (contextDTO == null) {
			if (parentPage.getContextTypeRequired() == ContextType.AGREEMENT) {
				this.error(this.getString("context.required.agreement"));
			} else if (parentPage.getContextTypeRequired() == ContextType.PARTY) {
				this.error(this.getString("context.required.party"));
			}  else if (parentPage.getContextTypeRequired() == ContextType.PARTY_ORGANISATION_PRACTICE) {
				this.error(this.getString("context.required.party"));
			}
			
			return;
		}
		
		initialiseSelectedResultAgreementFromList();
		
		/* Configure context properties */
		ContextRow row = new ContextRow();
		
		
		//MXM1904 Added This For Advanced Practice PROD00010430 07/02/2012
		if (parentPage.getContextTypeRequired() == ContextType.PARTY_ORGANISATION_PRACTICE) {
			row = new ContextRow();
			if(contextDTO != null && contextDTO.getPartyContextDTO().getTypeOid() == SRSType.ADVANCEDPRACTICE){
				row.addItem("Advanced Practice name:",PropertyResolver.getValue("partyContextDTO.name",contextDTO));
				row.addItem("Advanced Practice number",PropertyResolver.getValue("partyContextDTO.partyOid",contextDTO));
	         }
			rowList.add(row);
		} else{		
		
		row.addItem(new ContextItem("Agmt No:",
		PropertyResolver.getValue("agreementContextDTO.agreementNumber",contextDTO), true));
		row.addItem("Division:",PropertyResolver.getValue("agreementContextDTO.division.name",contextDTO));
		row.addItem("Name:",PropertyResolver.getValue("partyContextDTO.name",contextDTO));
		rowList.add(row);
		
		row = new ContextRow();
		row.addItem("Agmt Start:",PropertyResolver.getValue("agreementContextDTO.agreementStartDate", contextDTO));
		row.addItem("Branch:",PropertyResolver.getValue("agreementContextDTO.branch.name", contextDTO));
		row.addItem("Party Type:",PropertyResolver.getValue("partyContextDTO.intermediaryType", contextDTO));
		rowList.add(row);

		row = new ContextRow();
		row.addItem("Agmt Status:",PropertyResolver.getValue("agreementContextDTO.customAgreementStatus", contextDTO));
		row.addItem("Unit:",PropertyResolver.getValue("agreementContextDTO.unit.name", contextDTO));
		row.addItem("ID/Reg:",PropertyResolver.getValue("partyContextDTO.IdNumber", contextDTO));
		rowList.add(row);

		row = new ContextRow();
		row.addItem("Cons Code:",PropertyResolver.getValue("agreementContextDTO.consultantCode", contextDTO));
		row.addItem("Brokerage:",PropertyResolver.getValue("agreementContextDTO.brokerageName", contextDTO));
		// SSM2707 Market Integration 21/09/2015 Sweta Menon Begin
		//row.addItem("Agmt Type:",PropertyResolver.getValue("agreementContextDTO.agreementDivision", contextDTO));
		row.addItem("Sales Category:",PropertyResolver.getValue("agreementContextDTO.salesCategory", contextDTO));
		// SSM2707 Market Integration 21/09/2015 Sweta Menon End
		rowList.add(row);
		
		row = new ContextRow();
		row.addItem("Legacy Code:",PropertyResolver.getValue("agreementContextDTO.properAgreementNumber", contextDTO));
		if(parentPage.getContextTypeRequired() == ContextType.NONE ||
				parentPage.getContextTypeRequired() == ContextType.AGREEMENT){
		row.addItem("",null);	
		row.addItem(new ContextItem("Serviced Advisor:",
				PropertyResolver.getValue("agreementContextDTO.selectedAdvisor",contextDTO), false,true));
		}
		rowList.add(row);
		
		//MXM Added this to make sure that no advanced prcatice is selected into any other context
			if (contextDTO != null
					&& contextDTO.getPartyContextDTO().getTypeOid() == SRSType.ADVANCEDPRACTICE) {
				rowList.clear();
				row = new ContextRow();
				rowList.add(row);
			}
		
		}
	}
	
	private void initialiseSelectedResultAgreementFromList() {
		/* Determine which agreement has been selected (if any) */
		if (contextDTO.getAgreementContextDTO() != null && contextDTO.getAllAgreementsList().size() > 0
				&& contextDTO.getAgreementContextDTO().getAgreementNumber()!=null) {
			for (ResultAgreementDTO agrItem : contextDTO.getAllAgreementsList()) {
				if (agrItem.getAgreementNumber().longValue() == 
						contextDTO.getAgreementContextDTO().getAgreementNumber().longValue()) {
					selectedResultAgreementDTO = agrItem;
					break;
				}
			}
		}
	}

	/**
	 * Enclosing form
	 * 
	 * @author JZB0608 - 22 May 2008
	 * 
	 */
	public class ContextForm extends Form {
		private static final long serialVersionUID = 1789407547560880272L;

		public ContextForm(String id) {
			super(id);

			/* Add the top rows */
			ContextRow lastRow = null;
			ArrayList<ContextRow> topRows = null;
			if (rowList.size() == 0) {
				// Empty only show button
				topRows = new ArrayList<ContextRow>();
				lastRow = new ContextRow();
			} else if (rowList.size() == 1
					&& rowList.get(0).getItemList().size() < MAX_COLUMNS) {
				// Show one row, enough space for button
				topRows = new ArrayList<ContextRow>();
				lastRow = rowList.get(0);
			} else if (rowList.get(rowList.size() - 1).getItemList().size() == MAX_COLUMNS) {
				// Show one full row and one row just for the button
				topRows = new ArrayList<ContextRow>(rowList.subList(0, rowList
						.size()));
				lastRow = new ContextRow();
			} else {
				topRows = new ArrayList<ContextRow>(rowList.subList(0, rowList
						.size() - 1));
				lastRow = rowList.get(rowList.size() - 1);
			}
			add(createRowValuesField("rowValues", topRows));

			/* Add the last row */
			List<ContextItem> colList = lastRow.getItemList();
			for (int i = 1; i <= (MAX_COLUMNS - 1); ++i) {
				// Append empty items to fill row.
				ContextItem contextItem = (colList.size() >= i) ? colList
						.get(i - 1) : new ContextItem("", "");
				addItemToComponent(ContextForm.this, contextItem, i);
			}

			add(modalWindow = createModalWindow("modalWindow"));
			add(searchButton = createSearchButton("searchButton", this));
		}

	}
	 
	
	/**
	 * Create the search button.
	 * 
	 * @param id
	 * @return
	 */
	protected Button createSearchButton(String id, Form form) {
		Button but = new AjaxButton(id, form) {
			private static final long serialVersionUID = 1L;
			@Override
			protected void onSubmit(AjaxRequestTarget target, Form form) {
				modalWindow.show(target);
			}
		};
		but.setOutputMarkupId(true);
		but.setOutputMarkupPlaceholderTag(true);	
		if(panelDisabled){
			but.setVisible(false);
		}
		//We now get the current system user and check whether he can view all agreements
//		ISessionUserProfile user = SRSAuthWebSession.get().getSessionUser();
//		if(user == null || !user.isAllowedToViewAllAgreements()){
//			but.setEnabled(false);
//			but.add(new SimpleAttributeModifier("title","You do not have access to search all agreements"));
//		}
		return but;
	}
	
	/**
	 * disables all items that can change the context selection
	 *
	 */
	public void disableContext(AjaxRequestTarget target){
		panelDisabled = true;
		searchButton.setVisible(false);	
		if(agmtDropDown != null){
			agmtDropDown.setEnabled(false);
		}
		if(servicedAdvisorsPanel != null){
			servicedAdvisorsPanel.setEnabled(false);
		}
		if(target != null){			
			target.add(searchButton);
			if(agmtDropDown != null){
				target.add(agmtDropDown);
				
			}
			if(servicedAdvisorsPanel != null){
				target.add(servicedAdvisorsPanel);
				
			}
		}
	}

	/**
	 * Add Item on the relevant component
	 * 
	 * @param component
	 * @param contextItem
	 */
	protected void addItemToComponent(WebMarkupContainer component, 
			ContextItem contextItem, int itemNr) {
		
		component.add(new Label("colDescription"+ itemNr, 
				contextItem.getDescription()));
		
		String colValueId = "colValue"+itemNr;
		HelperPanel valueComp = null;
		if (!panelDisabled  && contextItem.isAgreementNr 
				&& contextDTO.getAllAgreementsList().size()>0) {
			// Agreement nr may be 
			agmtDropDown = createAgreementNrChoice("value");
			valueComp = HelperPanel.getInstance(colValueId, agmtDropDown);
		} else 
			if (!panelDisabled  && contextItem.isServicedAdvisor 
					&& contextDTO.getAgreementContextDTO().getServicedAdvisorsList() != null &&
							contextDTO.getAgreementContextDTO().getServicedAdvisorsList().size()>0) {
				 
				servicedAdvisorsPanel = getServicedAdvisorsList("value");
				valueComp = HelperPanel.getInstance(colValueId, servicedAdvisorsPanel);
			}
			else
		{
			Label label = new Label("value", 
				new Model((Serializable) contextItem.getValue()));
			valueComp = HelperPanel.getInstance(colValueId, label);
		}
		component.add(valueComp);
		
	}

	/**
	 * Create a choice on agreement nr
	 * @param id
	 * @return
	 */
	private DropDownChoice createAgreementNrChoice(String id) {
		final DropDownChoice choice = new DropDownChoice(id,
				new PropertyModel(this, "selectedResultAgreementDTO"),
				contextDTO.getAllAgreementsList(),
				new ChoiceRenderer("agreementNumber"));

		choice.add(new AjaxFormComponentUpdatingBehavior("change") {

			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				//Dean added --> a rule check to see if the agreement may be put in the context
				
					try {
						if(parentPage.getSecurityManagement().canUserViewAgreementDetails(
								selectedResultAgreementDTO.getAgreementNumber(),selectedResultAgreementDTO.getHasHomePartyOid(), SRSAuthWebSession.get().getSessionUser())){
							/**
							 * Get the agreement context of the new agreement nr &
							 * reload the page.
							 */
							IContextManagement contextBean = null;
							try {
								contextBean = ServiceLocator.lookupService(IContextManagement.class);
							} catch (NamingException e) {
								throw new CommunicationException(e);
							}
							
							ContextDTO newContextDTO = contextDTO.clone();
							
							ContextDTO dto = contextBean.getContext(selectedResultAgreementDTO);
		
							newContextDTO.setAgreementContextDTO(dto.getAgreementContextDTO());
							
							/* Update the session */
							updateSessionWithContext(newContextDTO);
						}else{		
							//revert selection
							initialiseSelectedResultAgreementFromList();	
							target.add(choice);								
							if(parentPage.getFeedbackPanel() != null){
								error("You may not select the agreement into the context due to rule restrictions");
								target.add(parentPage.getFeedbackPanel());							
							}
						}
					} catch (CommunicationException e) {
						// Cycle processor will deal with this
						throw new RuntimeException(e);
					} catch (CloneNotSupportedException e) {
						throw new RuntimeException(e);
					}			
			}			
		});
		choice.setOutputMarkupId(true);
		choice.setOutputMarkupPlaceholderTag(true);
		return choice;
	}
	
	
	/**
	 * Update the session with the passed context.
	 * 
	 * @param dto
	 */
	@SuppressWarnings("unchecked")
	private void updateSessionWithContext(ContextDTO dto) {
		/* Update the session */
		SRSAuthWebSession.get().setContextDTO(dto);
		
		/* Reload the current page, will automatically load new context from session*/
		MarkupContainer parent = DynamicContextPanel.this.getParent();
		if (parent instanceof MaintainAgreementPage) {
			logger.info("#JB set context in agreement page - start");
			MaintainAgreementPage p = new MaintainAgreementPage();
			logger.info("#JB set context in agreement page - end");
			setResponsePage(p);
			logger.info("#JB set context in agreement page - end after response set");
			if (logger.isDebugEnabled())
				logger.debug("Updated session with context and routhing to response page:" 
						+ parent.getClass());
			
		}else if (parent instanceof BasePage) {	
			if (logger.isDebugEnabled())
				logger.debug("Updated session with context and routhing to response page:" 
						+ parent.getClass());
			setResponsePage((Class<? extends Page>)parent.getClass());
		} else {
			throw new IllegalStateException("Parent should extend BasePage");
		}
	}
	
	/**
	 * Create the modal window
	 * @param id
	 * @return
	 */
	protected ModalWindow createModalWindow(String id) {
		
		final ContextSearchPopUp popUp = new ContextSearchPopUp() {
			
			private static final long serialVersionUID = 1L;

			@Override
			public void doProcessSelectedItems(AjaxRequestTarget target, ArrayList<ResultContextItemDTO> selectedItemList) {
				if (selectedItemList.size()==0) {
					if (logger.isDebugEnabled())
						logger.debug("Process selected msg to context - None selected");
					// Nothing was selected
					return;
				}
				
				try {
					/* Get the context for the selected item */
					IContextManagement contextBean = null;
					try {
						contextBean = ServiceLocator.lookupService(IContextManagement.class);
					} catch (NamingException e) {
						throw new CommunicationException(e);
					}
					if (logger.isDebugEnabled())
						logger.debug("Get context for " + selectedItemList.get(0).getAgreementDTO().getAgreementNumber());
					ContextDTO dto = contextBean.getContext(selectedItemList.get(0));
			
					target.appendJavaScript(";overlay('test');");
					/* Update the session */
					updateSessionWithContext(dto);
			
				} catch (CommunicationException e) {
					// Cycle processor will deal with this
					throw new RuntimeException(e);
				}
				
			}
			
			@Override
			public ContextType getContextType() {
				return parentPage.getContextTypeRequired();
			}
			
			@Override
			public String getWindowTitle() {
				return "Context Search";
			}

			@Override
			public void afterOnClose(AjaxRequestTarget target) {
				super.afterOnClose(target);
				logger.getLogger(this.getClass()).info("Closing Window of search");
				target.appendJavaScript("overlay(true);");
			}
			
			
		};
		
		ModalWindow win = popUp.createModalWindow(id);
		win.setCookieName("dynamic.search.1");
//		win.setCloseButtonCallback(new CloseButtonCallback() {
//			
//			@Override
//			public boolean onCloseButtonClicked(AjaxRequestTarget target) {
//				 target.appendJavaScript("overlay(true);");
//				 return true;
//			}
//		});
		
		
		
		// WICKETTEST
		// WICKETFIX - Where is this method
//		win.setPageMapName("dynamicContextSearchPageMap");
		return win;
	}
	
	/**
	 * Create the repeating rows
	 * 
	 * @param id
	 * @return
	 */
	public ListView createRowValuesField(String id, List<ContextRow> newRowList) {
		
		ListView rows = new ListView(id, newRowList) {

			private static final long serialVersionUID = 0L;

			@Override
			protected void populateItem(ListItem item) {
				if (((ContextRow)item.getModelObject()).getItemList().size()!=3) {
					item.add(new EmptyPanel("colValues"));
					return;
				}
				
				/* Add the columns */
				List<ContextItem> colList = ((ContextRow)item.getModelObject()).getItemList();
				for (int i = 1; i <= 3; ++i) {
				  ContextItem contextItem = 
					  (colList.size()>=i) ?  colList.get(i-1):new ContextItem("","");
				  addItemToComponent(item,contextItem,i);
				}
			}
			
		};
		return rows;
	}
	

	public ResultAgreementDTO getSelectedResultAgreementDTO() {
		return selectedResultAgreementDTO;
	}

	public void setSelectedResultAgreementDTO(
			ResultAgreementDTO selectedResultAgreementDTO) {
		this.selectedResultAgreementDTO = selectedResultAgreementDTO;
	}
	
	/**
	 * Page model that represents a Row
	 * 
	 * @author JZB0608 - 22 May 2008
	 *
	 */
	public class ContextRow implements Serializable{
		private static final long serialVersionUID = 1L;
		
		private List<ContextItem> itemList;
		
		public ContextRow (){
			this(new ArrayList<ContextItem>());
		}
		public ContextRow (List<ContextItem> list){
			this.itemList = list;
		}
		
		public List<ContextItem> getItemList() {
			return itemList;
		}

		public void setItemList(List<ContextItem> itemList) {
			this.itemList = itemList;
		}
		
		public void addItem(String description, Object value) {
			itemList.add(new ContextItem(description, value));
		}
		
		public void addItem(ContextItem item) {
			itemList.add(item);
		}
	}
	
	/**
	 * Used by page model, represents a column
	 * 
	 * @author JZB0608 - 22 May 2008
	 *
	 */
	public class ContextItem  implements Serializable{
		private static final long serialVersionUID = 1L;
		
		private String description;
		private Object value;
		private boolean isAgreementNr;
		private boolean isServicedAdvisor;
		
		public ContextItem(String description, Object value) {
			this(description, value,false,false);
		}
		
		public ContextItem(String description, Object value, boolean isAgreementNr) {
			this(description, value,isAgreementNr,false);
		}
		
		public ContextItem(String description, Object value, boolean isAgreementNr,boolean isServicedAdvisor) {
			this.description = description;
			this.value = value;
			this.isAgreementNr = isAgreementNr;
			this.isServicedAdvisor = isServicedAdvisor;
		}
		
		public String getDescription() {
			return description;
		}
		public void setDescription(String description) {
			this.description = description;
		}
		public Object getValue() {
			return value;
		}
		public void setValue(Object value) {
			this.value = value;
		}

		public boolean isAgreementNr() {
			return isAgreementNr;
		}

		public void setAgreementNr(boolean isAgreementNr) {
			this.isAgreementNr = isAgreementNr;
		}
		
		public boolean isServicedAdvisor() {
			return isServicedAdvisor;
		}

		public void setServicedAdvisor(boolean isServicedAdvisor) {
			this.isServicedAdvisor = isServicedAdvisor;
		}
		
	}


	public void setEditState(EditStateType newState, AjaxRequestTarget target) {
		this.editState = newState;
	}

	public EditStateType getEditState() {
		return this.editState;
	}
	
	/**
	 * @return the servicedAdvisorsList
	 */
	public SRSDropDownChoice getServicedAdvisorsList(String id) {

		if (servicedAdvisorsPanel==null) {
			List<Long> choices = (contextDTO != null && contextDTO.getAgreementContextDTO() != null) ? 
					contextDTO.getAgreementContextDTO().getServicedAdvisorsList():null;
			 servicedAdvisorsPanel = new SRSDropDownChoice(id,
					new PropertyModel(this,"contextDTO.agreementContextDTO.selectedAdvisor"), 
					choices, new ChoiceRenderer()," *** Select *** "
			);
			 
			 servicedAdvisorsPanel.add(new AjaxFormComponentUpdatingBehavior("change") {
		            @Override
		            protected void onUpdate(AjaxRequestTarget target) {
		            	
		            	Long agmtid = (Long)servicedAdvisorsPanel.getConvertedInput();
		            	updateContextForSelectedAgreement(agmtid);
		            	if(agmtDropDown != null && agmtDropDown.isEnabled()){
		            	if(agmtDropDown.getChoices() != null){
		            		agmtDropDown.getChoices().clear();
		            	}		            	
		            	agmtDropDown.setConvertedInput(agmtid);
		            	panelDisabled = true;
		            	agmtDropDown.setEnabled(false);
		            	target.add(agmtDropDown);
		            	}
		            	
		            }
			    });
			 
			 	servicedAdvisorsPanel.setOutputMarkupId(true);
				servicedAdvisorsPanel.setOutputMarkupPlaceholderTag(true);
			}
		
		
		return servicedAdvisorsPanel;	
	}
	
	private void updateContextForSelectedAgreement(Long agmtid) {

		try{
		ContextDTO newContextDTO = SRSAuthWebSession.get().getContextDTO().clone();					
		
		IContextManagement contextBean = getContextManagement();				
		
		ResultAgreementDTO agmtDTO = contextBean.findAgreementWithSRSAgreementNr(agmtid);
		
		ContextDTO dto = contextBean.getContext(agmtDTO);

		newContextDTO.setAgreementContextDTO(dto.getAgreementContextDTO());
		
		/* Update the session */
		SRSAuthWebSession.get().setContextDTO(newContextDTO);
		
		/* Reload the current page, will automatically load new context from session*/
		MarkupContainer parent = (MarkupContainer)this.parentPage;
						
		if (parent instanceof BasePage) {			
			setResponsePage((Class<? extends Page>)parent.getClass());
		} else {
			throw new IllegalStateException("Parent should extend BasePage");
		}
		}catch(CloneNotSupportedException ce){
			ce.printStackTrace();
			
		}catch(DataNotFoundException de){
			de.printStackTrace();
		}		
	}
	
	private IContextManagement getContextManagement() {
		if (contextManagement==null) {
			try {
				contextManagement = ServiceLocator.lookupService(IContextManagement.class);
			} catch (NamingException e) {
				SystemException exception = new SystemException("Could not Context Management", 0, 0);
				exception.initCause(e);
				throw exception;
			}
		}
		return contextManagement;
	}
	
}
