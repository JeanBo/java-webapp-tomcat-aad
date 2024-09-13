package za.co.liberty.web.pages.request;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.ImageButton;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

import com.inmethod.grid.DataProviderAdapter;
import com.inmethod.grid.IGridColumn;
import com.inmethod.grid.SizeUnit;
import com.inmethod.grid.column.CheckBoxColumn;
import com.inmethod.grid.column.PropertyColumn;

import za.co.liberty.business.guicontrollers.request.IRequestViewGuiController;
import za.co.liberty.dto.agreement.request.RequestEnquiryRowDTO;
import za.co.liberty.dto.gui.request.ViewRequestModelDTO;
import za.co.liberty.exceptions.data.ValidationException;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.exceptions.fatal.InconsistentConfigurationException;
import za.co.liberty.helpers.lookup.ServiceLocator;
import za.co.liberty.interfaces.agreements.requests.RequestActionType;
import za.co.liberty.interfaces.agreements.requests.RequestKindType;
import za.co.liberty.interfaces.agreements.requests.RequestStatusType;
import za.co.liberty.interfaces.gui.GuiRequestKindType;
import za.co.liberty.web.data.enums.EditStateType;
 
import za.co.liberty.web.data.enums.PanelToRequestMapping;
import za.co.liberty.web.pages.interfaces.IStatefullComponent;
import za.co.liberty.web.pages.request.model.RequestPanelModel;
import za.co.liberty.web.system.SRSAuthWebSession;
import za.co.liberty.web.wicket.ajax.attributes.SRSAjaxCallListener;
import za.co.liberty.web.wicket.markup.html.grid.SRSCheckBoxColumn;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGrid;
import za.co.liberty.web.wicket.markup.html.grid.SRSGridRowSelectionCheckBox;
import za.co.liberty.web.wicket.markup.repeater.data.SortableListDataProvider;

/**
 * <p>Serves as the base for all View and Authorise panels.  This class should be extended to enable 
 * the Viewing or Authorisation of requests that were raised with a {@linkplain GuiRequestKindType}.</p>
 * 
 * <p>The {@linkplain GuiRequestKindType} is used to lookup the implementing authorisation panel
 * from the {@linkplain GuiRequestAuthorisationMappingEnum} and each subclass must implement the 
 * the default constructor as defined here.</p>
 * 
 * <p>The class is provided with the {@linkplain GuiRequestKindType}, a list of request 
 * kinds that were raised with the GUI request and an image object (which is the top level
 * object linked to the GUI request) etc.  The concrete subclasses are responsible for the creation
 * of the relevant panels (with their models) for each of the required request kinds.</p>
 * 
 * @author JZB0608 - 15 Feb 2010
 *
 */
public abstract class BaseRequestViewAndAuthorisePanel extends Panel implements IStatefullComponent {
	
	private static final long serialVersionUID = 7555141056360749373L;
	
	private static final Logger logger = Logger.getLogger(BaseRequestViewAndAuthorisePanel.class);
	
	/* Form fields */
	private Button authoriseButton;
	private Button declineButton;
	private SRSDataGrid authoriseTable;
	private FeedbackPanel feedbackPanel;
	
	/* Attributes */
	private transient IRequestViewGuiController guiController;
	private EditStateType editState = EditStateType.AUTHORISE;
	private ViewRequestModelDTO pageModel;
	
	private List<Panel> currentPanelList;
	private List<Panel> beforePanelList;
	private List<RequestPanelModel> panelModelList;  // Please leave this as private, use get method.
	
	// List of items that may not be authorised
	private List<RequestEnquiryRowDTO> noAuthorisationRequiredList;
	
	/**
	 * Default constructor 
	 * 
	 * @param id
	 * @param viewRequestPageModel
	 */
	public BaseRequestViewAndAuthorisePanel(String id, ViewRequestModelDTO viewRequestPageModel) {
		super(id);
		this.pageModel = viewRequestPageModel;
		this.currentPanelList = this.createPanels("panelId", viewRequestPageModel.getCurrentImage());
		if (viewRequestPageModel.getBeforeImage()!=null) {
			this.beforePanelList = this.createPanels("historyPanel", viewRequestPageModel.getBeforeImage());
		}
		this.panelModelList = this.createModelList(currentPanelList, beforePanelList);
		this.noAuthorisationRequiredList = initialiseAuthorisedNotRequiredList();
		add(createRepeatingPanel("repeatingPanel", this.panelModelList));
		add(createAuthorisePanel("authorisePanel"));
	}

	// ========================================================================================
	// Web field section
	// ========================================================================================

	/**
	 * Create the authentication panel.
	 * 
	 * @param id
	 * @param panelModel
	 * @return
	 */
	protected Component createAuthorisePanel(String id) {
		WebMarkupContainer component = new WebMarkupContainer(id);
	
		boolean isEnabled = isRequiresAuthorisation(pageModel.getRequestEnquiryRowList());
		
		// Add form & auth button
		Form form = new Form("authoriseForm");
		form.add((authoriseButton=createRequestActionButton("authoriseButton", form, 
				RequestActionType.AUTHORISE)).setEnabled(isEnabled));
		form.add((declineButton=createRequestActionButton("declineButton", form,
				RequestActionType.DECLINE)).setEnabled(isEnabled));
		form.add((authoriseTable=createAuthoriseTable("authoriseTable")).setEnabled(isEnabled));
		form.add((feedbackPanel=new FeedbackPanel("messages")).setOutputMarkupId(true));
		component.add(form);
		return component;
	}
	
	/**
	 * Returns true if any of the passed requests still requires authorisation.
	 *  
	 * @param requestEnquiryRowList
	 * @return
	 */
	private boolean isRequiresAuthorisation(List<RequestEnquiryRowDTO> requestEnquiryRowList) {
		for (RequestEnquiryRowDTO dto : requestEnquiryRowList) {
			if (dto.getStatusType()==RequestStatusType.REQUIRES_AUTHORISATION) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Create the generic request action button
	 * 
	 * @param id
	 * @param form
	 * @return
	 */
	protected Button createRequestActionButton(String id, Form form, final RequestActionType actionType) {
		Button but = new AjaxButton(id, form) {
			private static final long serialVersionUID = 1L;

			@SuppressWarnings("unchecked")
			@Override
			protected void onSubmit(AjaxRequestTarget target, Form form) {
				if (authoriseTable.getSelectedItems().size()==0) {
					// Give an error???
					error("At least one item needs to be selected");
					target.add(feedbackPanel);
					return;
				}
				
				// TODO ensure that panel is visible before authorising
				List<?> selectList = authoriseTable.getSelectedItemObjects();
				try {
					if (actionType==RequestActionType.AUTHORISE) {
						doAuthoriseRequest(selectList);
					} else if (actionType==RequestActionType.DECLINE) {
						doDeclineRequest(selectList);
					} else {
						// Oops
						throw new UnsupportedOperationException("The specified request action \""+
								actionType + "\" is not supported");
					}
					authoriseTable.addAllToNonSelectableRowObjectMap(selectList);					
					authoriseTable.resetSelectedItems();
					
					if (!isRequiresAuthorisation(pageModel.getRequestEnquiryRowList())) {
						declineButton.setEnabled(false);
						authoriseButton.setEnabled(false);
					}
				} catch (ValidationException e) {
					// output the validation errors
					for (String str : e.getErrorMessages()) {
						error(str);
					}
				}
				
				target.add(feedbackPanel);
				target.add(authoriseButton);
				target.add(declineButton);
				target.add(authoriseTable);
			}

			@Override
			protected void onComponentTag(ComponentTag tag) {
				super.onComponentTag(tag);
			}
			
			@Override
			protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
				super.updateAjaxAttributes(attributes);
			        
			        // SRS Convenience method for overLay hiding/showing
			        attributes.getAjaxCallListeners().add(new SRSAjaxCallListener());
			}
		};
		but.setOutputMarkupId(true);
		return but;
	}
	

	/**
	 * Perform the autorisation action
	 * 
	 * @param selectList
	 * @throws ValidationException
	 */
	@SuppressWarnings("unchecked")
	protected void doAuthoriseRequest(List<?> selectList) throws ValidationException {
		getGuiController().authoriseRequests(
				SRSAuthWebSession.get().getSessionUser(),
				pageModel.getGuiRequestId(),
				(List<RequestEnquiryRowDTO>) selectList);	
		info("The selected record"
				+ ((selectList.size()==1)? " was" : "s were")
				+ " authorised successfully.");
	}

	/**
	 * 
	 * @return
	 */
	public boolean isRequireRefreshAfterAuth() {
		return false;
	}
	
	/**
	 * Perform the decline action
	 * 
	 * @param selectList
	 * @throws ValidationException
	 */
	@SuppressWarnings("unchecked")
	private void doDeclineRequest(List<?> selectList) throws ValidationException {
		getGuiController().declineRequests(
				SRSAuthWebSession.get().getSessionUser(),
				pageModel.getGuiRequestId(),
				(List<RequestEnquiryRowDTO>) selectList);	
		info("The selected record"
				+ ((selectList.size()==1)? " was" : "s were")
				+ " declined successfully.");
		
	}
	
	/**
	 * Create the search result panel (Empty when no search has been done)
	 * 
	 * @param id
	 * @return
	 */
	protected SRSDataGrid createAuthoriseTable(String id) {
		/* Create the Grid Columns */
		List<IGridColumn> columns = new ArrayList<IGridColumn>();
//		columns.add(new SRSGridRowSelectionCheckBox("check"));
		columns.add(new SRSCheckBoxColumn("check"));
//		columns.add(new CheckBoxColumn("check"));
		columns.add(new PropertyColumn(new Model("Request"), "requestKindType", "requestKind" ).setInitialSize(210));
		columns.add(new PropertyColumn(new Model("Status"), "statusType", "statusType" ).setInitialSize(125));
		columns.add(new PropertyColumn(new Model("Requestor"), "requestor", "requestor" ).setInitialSize(76));
		columns.add(new PropertyColumn(new Model("Request Date"), "requestDate", "requestDate" ).setInitialSize(86));
		columns.add(new PropertyColumn(new Model("Authoriser"), "authoriser1", "authoriser1" ).setInitialSize(70));
		columns.add(new PropertyColumn(new Model("Auth Date"), "authoriserDate1", "authoriserDate1" ).setInitialSize(80));
		columns.add(new PropertyColumn(new Model("2nd Auth"), "authoriser2", "authoriser2" ).setInitialSize(70));
		columns.add(new PropertyColumn(new Model("2nd Auth Date"), "authoriserDate2", "authoriserDate2" ).setInitialSize(85));
		columns.add(new PropertyColumn(new Model("Request Id"), "requestId", "requestId" ).setInitialSize(75));

		
		/* Create the search result table */
		SRSDataGrid grid = new SRSDataGrid(id, new DataProviderAdapter(
				new SortableListDataProvider<RequestEnquiryRowDTO>(pageModel
						.getRequestEnquiryRowList())), 
				columns, null);
		grid.setAutoResize(false);

		int gridSize = isRequiresAuthorisation(pageModel.getRequestEnquiryRowList()) ? 22 : 19;
		gridSize = gridSize * pageModel.getRequestKindList().size();
		gridSize += 15;  // Scroll bar
		
		grid.setContentHeight(gridSize, SizeUnit.PX);
		grid.setAllowSelectMultiple(true);
		grid.setNonSelectableRowObjects(noAuthorisationRequiredList);
		return grid;
	}
	
	
	/**
	 * Create the repeating panels.
	 * 
	 * @param id
	 * @return
	 */
	public ListView createRepeatingPanel(String id, List<RequestPanelModel> modelList) {
		
		ListView<RequestPanelModel> rows = new ListView<RequestPanelModel>(id, modelList) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(ListItem item) {
				final RequestPanelModel pModel = (RequestPanelModel) item.getModelObject();
				
				/* Add the wrapper div for hiding/showing a panel */
				final WebMarkupContainer panelWrapper = new WebMarkupContainer("panelIdWrapper") {
					private static final long serialVersionUID = 1L;

					@Override
					protected void onComponentTag(ComponentTag tag) {
						super.onComponentTag(tag);
						decorateComponentStyleToHide(!pModel.isEnabled(), tag);
					}				
					
				};
				panelWrapper.add(pModel.getPanel());
				panelWrapper.setOutputMarkupId(true);
				panelWrapper.add(createHistorySection("historySection", pModel));
				item.add(panelWrapper);
				
				/* Add the expand/hide image */
				final ImageButton expandImage = new ImageButton("showPanelImg", "Expand"){
					private static final long serialVersionUID = 1L;

					@Override
					public Form<?> getForm() {
						return new Form("blablaform2");
					}
					
				};
				expandImage.add(new AjaxEventBehavior("click") {
					private static final long serialVersionUID = 1L;

					@Override
					protected void onEvent(AjaxRequestTarget target) {
						pModel.setEnabled(!pModel.isEnabled());
						expandImage.add(new AttributeModifier("src",
								getExpandImageSrc(pModel.isEnabled())));
						target.add(expandImage);
						target.add(panelWrapper);
					}
					
				});
				expandImage.setOutputMarkupId(true);
				expandImage.add(new AttributeModifier("src",
						getExpandImageSrc(pModel.isEnabled())));
				item.add(expandImage);
				
				
 
				
				/* Other components */
				item.add(createRepeatingRequestRow("requestRow", pModel.getRequestRowList()));
				item.add(new WebMarkupContainer("bottomBar").setVisible(!pModel.isLastRow()));
				
				
			}

		};
		return rows;
	}

	/**
	 * Create the repeating request info row which is shown with each panel
	 * 
	 * @param id
	 * @param requestRowList
	 * @return
	 */
	private ListView createRepeatingRequestRow(String id, List<RequestEnquiryRowDTO> requestRowList) {
		ListView<RequestEnquiryRowDTO> rows = new ListView<RequestEnquiryRowDTO>(id, requestRowList) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(ListItem item) {
				RequestEnquiryRowDTO dto = (RequestEnquiryRowDTO) item.getModelObject();
				item.add(new Label("requestId", new Model(dto.getRequestId())));
				item.add(new Label("requestKind", new Model(dto.getRequestKindType())));
				item.add(new Label("requestStatus", new Model(dto.getStatusType())));
			}

		};
		return rows;
	}	
	
	/**
	 * Create the History section
	 * 
	 * @param id
	 * @return
	 */
	protected WebMarkupContainer createHistorySection(String id, final RequestPanelModel pModel) {
		if (pageModel.getBeforeImage()==null) {
			// There is no before image
			return (WebMarkupContainer) new WebMarkupContainer(id).setVisible(false);
		}
		
		WebMarkupContainer container = new WebMarkupContainer(id);
		
//		if (true)
//			return (WebMarkupContainer) container.setVisible(false);
		
		/* Add the wrapper div for hiding/showing a panel */
		final WebMarkupContainer historyPanelWrapper = new WebMarkupContainer("historyPanelWrapper") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onComponentTag(ComponentTag tag) {
				super.onComponentTag(tag);
				decorateComponentStyleToHide(!pModel.isHistoryEnabled(), tag);
			}				
			
		};
		historyPanelWrapper.add(pModel.getHistoryPanel());
		
		historyPanelWrapper.setOutputMarkupId(true);
		container.add(historyPanelWrapper);
		
		/* Add the expand/hide image */
		final ImageButton expandImage = new ImageButton("showHistoryImg", "History"){
			private static final long serialVersionUID = 1L;

			@Override
			public Form<?> getForm() {
				return new Form("blablaForm");
			}
			
		};
		expandImage.add(new AjaxEventBehavior("click") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onEvent(AjaxRequestTarget target) {
				pModel.setHistoryEnabled(!pModel.isHistoryEnabled());
				expandImage.add(new AttributeModifier("src",
						getExpandImageSrc(pModel.isHistoryEnabled())));
				target.add(expandImage);
				target.add(historyPanelWrapper);
			}
			
		});
		expandImage.setDefaultFormProcessing(false);
		expandImage.setOutputMarkupId(true);
		expandImage.setOutputMarkupPlaceholderTag(true);
		expandImage.add(new AttributeModifier("src",
				getExpandImageSrc(pModel.isHistoryEnabled())));
		container.add(expandImage);
		container.setOutputMarkupId(true);
		container.setOutputMarkupPlaceholderTag(true);
		
//		/* History panel */
//		historyPanelWrapper.add(new WebMarkupContainer("historyPanel").setVisible(false));
		
		return container;
	}
	
	// ========================================================================================
	// Initialisation methods
	// ========================================================================================
	/**
	 * Initialise the list of requests that don't require authorisation.
	 * 
	 * @return
	 */
	private List<RequestEnquiryRowDTO> initialiseAuthorisedNotRequiredList() {
		List<RequestEnquiryRowDTO> list = new ArrayList<RequestEnquiryRowDTO>();
		for (RequestEnquiryRowDTO rowDto : pageModel.getRequestEnquiryRowList()) {
			if (rowDto.getStatusType()!=RequestStatusType.REQUIRES_AUTHORISATION) {
				list.add(rowDto);
			}
		}
		return list;
	}
	
	/**
	 * Create the internal model list which keeps the data for
	 * each panel that is displayed.
	 * 
	 * @return
	 */
	private List<RequestPanelModel> createModelList(
			List<Panel> currentPanelList, 
			List<Panel> beforePanelList) {
		
		List<RequestPanelModel> modelList = new ArrayList<RequestPanelModel>();

		if (currentPanelList == null || currentPanelList.size()==0) {
			throw new InconsistentConfigurationException("The View/Authorise panels are not configured " +
					"correctly for gui request id \""+ pageModel.getGuiRequestId() + "\"");
		}
		ArrayList<Panel> historyPanels = null;
		if (beforePanelList!=null) {
			historyPanels = new ArrayList<Panel>(beforePanelList);
		}		
		for (Panel p : currentPanelList) {
			if (logger.isDebugEnabled())
				logger.debug("--Panel - createModel :" + p.getClass().getName());
			
			Panel beforePanel = null;
			if (beforePanelList!=null) {				
				for (Panel tmpBeforePanel : historyPanels) {
					if (tmpBeforePanel.getClass().equals(p.getClass())) {
						//this assumes all panels are in order
						beforePanel = tmpBeforePanel;
						historyPanels.remove(beforePanel);
						break;
					}
				}
			}
			
			boolean isShowPanel = false;
			
			// Determine which request does this apply to
			RequestKindType[] requestKinds = PanelToRequestMapping.getRequestKindsForPanel(p.getClass());
			List<RequestEnquiryRowDTO> panelRequestRowList = new ArrayList<RequestEnquiryRowDTO>();
			
			// Check all possible request kinds for this panel
			for (RequestKindType type :requestKinds) {
				if (logger.isDebugEnabled())
					logger.debug("  --attempting to map type :" + type
							+ "   ,nr of DTO rows="+pageModel.getRequestEnquiryRowList().size());

				// If request kind apply, add request to panel model 
				for (RequestEnquiryRowDTO rowDTO : pageModel.getRequestEnquiryRowList()) {
					
					if (logger.isDebugEnabled())
						logger.debug("    --enquiryRowType:" + rowDTO.getRequestKindType());
					if (rowDTO.getRequestKindType()==type) {
						panelRequestRowList.add(rowDTO);
						if (type==pageModel.getSelectedRequestKind() && isShowPanelType(p.getClass())) {
							
							isShowPanel = true;
						}
						break;
					}
				}

			}
			
			// Sanity check, could we map this panel?
			if (panelRequestRowList.size()==0) {
				List<RequestKindType> typeList = new ArrayList<RequestKindType>();
				Collections.addAll(typeList, requestKinds);
				logger.warn("  -- unable to map request DTO to panel.  Panel =" + p.getClass().getName()
						+ "   ,valid kinds for panel="+typeList);
				//TODO finalise error handling for this
			}
			modelList.add(new RequestPanelModel(p, panelRequestRowList, isShowPanel, beforePanel));
		}
		modelList.get(modelList.size()-1).setLastRow(true);
		return modelList;
	}
	
	/**
	 * By default we always show a panel if the request is in the list.  This provides
	 * a way to override this
	 * 
	 * @param class1
	 * @return
	 */
	public boolean isShowPanelType(Class<? extends Panel> class1) {
		return true;
	}

	/**
	 * Return a list of instatiated panels to be drawn.
	 * 
	 * @param id
	 * @return
	 */
	public abstract List<Panel> createPanels(String id, Object imageObject);

	// ========================================================================================
	// Other General methods
	// ========================================================================================
	/**
	 * Return the current edit state.
	 * 
	 */
	public EditStateType getEditState() {
		return editState;
	}

	/**
	 * Get the pageModel
	 * 
	 * @return
	 */
	protected ViewRequestModelDTO getPageModel() {
		return pageModel;
	}	
	
	/**
	 * Get the gui controller for this panel
	 * 
	 * @return
	 */
	protected IRequestViewGuiController getGuiController() {
		if (guiController == null) {
			try {
				guiController = ServiceLocator.lookupService(IRequestViewGuiController.class);
			} catch (NamingException e) {
				throw new CommunicationException(e);
			}
		}
		return guiController;
	}
	
	/**
	 * Decorate the style tag to hide the component
	 * 
	 * @param isHidden Hide component if true. 
	 * @param tag
	 */
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
		return (isEnabled) ? "/SRSAppWeb/images/minus.png" 
				: "/SRSAppWeb/images/plus.png";
	}
	
	/**
	 * Get the target agreement number (might be null)
	 * 
	 * @return
	 */
	@SuppressWarnings("unused")
	protected Long getTargetAgreementNumber() {
		return (pageModel.getViewRequestContextDto().getAgreementDto()!=null) ? 
				pageModel.getViewRequestContextDto().getAgreementDto().getAgreementNumber() : null;
	}
	
	/**
	 * Get the target party number (might be null)
	 * 
	 * @return
	 */
	@SuppressWarnings("unused")
	protected Long getTargetPartyOid() {
		return (pageModel.getViewRequestContextDto().getPartyDto()!=null) ? 
				pageModel.getViewRequestContextDto().getPartyDto().getPartyOid() : null;
	}
	
	/**
	 * True if this panel class exists in the list of current panels
	 * 
	 * @param clazz
	 * @return
	 */
	protected boolean hasCurrentPanel(Class<? extends Panel> clazz ) {
		if (currentPanelList!=null) {
			for (Panel p : currentPanelList) {
				if (p!=null && p.getClass() != null && p.getClass().equals(clazz)) {
					return true;
				}
			}
		}
		return false;
	}
}
