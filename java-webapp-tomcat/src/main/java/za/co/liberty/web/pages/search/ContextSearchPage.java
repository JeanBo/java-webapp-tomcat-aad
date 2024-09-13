package za.co.liberty.web.pages.search;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxCallListener;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.link.PopupSettings;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.util.string.Strings;

import za.co.liberty.business.guicontrollers.IContextManagement;
import za.co.liberty.dto.contracting.SearchIndividualNameDTO;
import za.co.liberty.dto.gui.context.AgreementSearchType;
import za.co.liberty.dto.gui.context.ContextSearchOptionsDTO;
import za.co.liberty.dto.gui.context.IContextSearchType;
import za.co.liberty.dto.gui.context.IndividualSearchType;
import za.co.liberty.dto.gui.context.OrganisationPracticeAgreementSearchType;
import za.co.liberty.dto.gui.context.OrganisationPracticeSearchType;
import za.co.liberty.dto.gui.context.OrganisationSearchType;
import za.co.liberty.dto.gui.context.ResultContextItemDTO;
import za.co.liberty.dto.gui.context.ResultContextSearchDTO;
import za.co.liberty.dto.party.HierarchySearchDetailDTO;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.helpers.lookup.ServiceLocator;
import za.co.liberty.web.data.enums.ContextType;
import za.co.liberty.web.pages.BaseWindowPage;
import za.co.liberty.web.pages.search.models.ContextSearchModel;
import za.co.liberty.web.pages.search.models.ContextSearchPageOptions;
import za.co.liberty.web.system.EJBReferences;
import za.co.liberty.web.system.SRSAuthWebSession;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGrid;
import za.co.liberty.web.wicket.markup.html.grid.SRSGridRowSelectionCheckBox;
import za.co.liberty.web.wicket.markup.repeater.data.SortableListDataProvider;

import com.inmethod.grid.DataProviderAdapter;
import com.inmethod.grid.IGridColumn;
import com.inmethod.grid.SizeUnit;
import com.inmethod.grid.column.AbstractColumn;
import com.inmethod.grid.column.PropertyColumn;
import com.inmethod.grid.datagrid.DataGrid;

/**
 * <p>Search for an Agreement/Party and allow for their selection.  The
 * selected items can be retrieved after the window has closed by 
 * calling the method {@link #getSelectedItemList()}.</p>
 * 
 * <p>Please use {@link ContextSearchPopUp} to show this window as it 
 * has defaults (like window size etc.) and has functionality for 
 * processing results as well.</p>
 * 
 * @author JZB0608 - 22 May 2008
 *
 */
public class ContextSearchPage extends BaseWindowPage {

	/* Constants */
	private static final long serialVersionUID = 4008008744919434971L;
	private static final String SEARCH_PANEL_FIELD = "searchPanel";
	private static final String SEARCH_RESULT_PANEL_FIELD = "searchResultPanel";
	private static final Logger logger = Logger.getLogger(ContextSearchPage.class);

	/* Form components */
	protected DropDownChoice searchTypeField;
	protected Panel searchPanelField;
	protected Panel searchResultPanelField;
	protected Button searchButton;
	protected Button selectButton;
	protected Button nextButton;
	protected CheckBox includeAgreementCheck;
	protected CheckBox activeAgreementCheck;
	protected FeedbackPanel feedBackPanel;

	/* Attributes */
	protected transient IContextManagement contextBean;
	protected ContextSearchModel pageModel;
	protected ModalWindow modalWindow;
	protected List<IGridColumn> searchResultColumns;
	protected ContextType contextType;
	
	protected ArrayList<ResultContextItemDTO> selectedItemList;
	
	protected ArrayList<IContextSearchType> searchTypeList;

	
	/* Types (enums) */
	public enum SeperatorType implements IContextSearchType {
		DEFAULT;

		public String toString() {
			return "-----------------------------------------";
		}

		public Class getValueClassType() {
			// This should never happen
			return null;
		}
	}

	/**
	 * Default constructor.
	 * 
	 * @param modalWindow
	 * @param contextType
	 * @param pageOptions
	 */
	public ContextSearchPage(ModalWindow modalWindow, ContextType contextType, ContextSearchPageOptions pageOptions) {
		super();
		this.selectedItemList = pageOptions.getSelectedItemList();
		
		if (logger.isDebugEnabled())
			logger.debug("#JB2 - ContextSearchPage.constructor " + this + " - " + System.identityHashCode(this)
				+ "    optionList= " + pageOptions.getSelectedItemList()
				+ System.identityHashCode(pageOptions.getSelectedItemList()) + "    selectedItemList="
				+ System.identityHashCode(selectedItemList));
		
		setVersioned(false);
		this.modalWindow = modalWindow;
		this.contextType = contextType;

		pageModel = new ContextSearchModel();
		pageModel.setSearchOptions(pageOptions);
		/**
		 * avoid showing the agreement columns on grid creation
		 */
		if (!pageModel.isAllowShowPartyOrganisationPractice()) {
			pageModel.setShowAgreements(
					contextType == ContextType.AGREEMENT || contextType == ContextType.AGREEMENT_ONLY);
		}

		pageModel.setAllowShowPartyOnly(contextType == ContextType.PARTY_ONLY || contextType == ContextType.PARTY
				|| pageOptions.isLimitToPartyOnly());
//		MZP0801 Party Person Only
		pageModel.setAllowShowPartyPersonOnly(contextType == ContextType.PARTY_PERSON_ONLY);
//		MZP0801 Organisation Only
		pageModel.setAllowShowPartyOrganisationOnly(contextType == ContextType.PARTY_ORGANISATION_ONLY);

		// MXM1904 Added This For Advanced Practice PROD00010430 07/02/2012
		pageModel.setAllowShowPartyOrganisationPractice(contextType == ContextType.PARTY_ORGANISATION_PRACTICE);

		/* Initialise search type list */
		searchTypeList = new ArrayList<IContextSearchType>();

//		MZP0801 Party Person Only
		if (pageModel.isAllowShowPartyPersonOnly()) {
			searchTypeList.addAll(Arrays.asList(IndividualSearchType.values()));
//		MZP0801 Party Organisation Only			
		} else if (pageModel.isAllowShowPartyOrganisationOnly()) {
			searchTypeList.addAll(Arrays.asList(OrganisationSearchType.values()));
//			MXM1904 Added This For Advanced Practice PROD00010430 07/02/2012			
		} else if (pageModel.isAllowShowPartyOrganisationPractice()) {
			searchTypeList.addAll(Arrays.asList(OrganisationPracticeSearchType.values()));
			searchTypeList.add(SeperatorType.DEFAULT);
			searchTypeList.addAll(Arrays.asList(OrganisationPracticeAgreementSearchType.values()));

		} else if (contextType != ContextType.PARTY_ONLY && pageOptions.isLimitToPartyOnly() == false) {
			searchTypeList.addAll(Arrays.asList(AgreementSearchType.values()));
			searchTypeList.add(SeperatorType.DEFAULT);
			searchTypeList.addAll(Arrays.asList(IndividualSearchType.values()));
			searchTypeList.add(SeperatorType.DEFAULT);
			searchTypeList.addAll(Arrays.asList(OrganisationSearchType.values()));
		} else {
			searchTypeList.addAll(Arrays.asList(IndividualSearchType.values()));
			searchTypeList.add(SeperatorType.DEFAULT);
			searchTypeList.addAll(Arrays.asList(OrganisationSearchType.values()));
		}

		/* Set the default type */
		IContextSearchType defaultSearchType = SRSAuthWebSession.get().getDefaultContextSearchType();
		if (defaultSearchType != null) {
			// Is it in the available list?
			if (searchTypeList.contains(defaultSearchType)) {
				pageModel.setSearchType(defaultSearchType);
			}
		}
		add(createSelectButton("selectButton" ));
		/* Add components */
		add(feedBackPanel = (FeedbackPanel) new FeedbackPanel("searchMessages").setOutputMarkupId(true));
		add(new SearchForm("searchForm"));
		add(new SelectForm("selectForm"));
		
		add(searchResultPanelField = createSearchResultPanel("searchResultPanel"));

	}

	/**
	 * Encapsulates the search form
	 * 
	 * @author JZB0608 - 22 May 2008
	 *
	 */
	public class SearchForm extends Form {
		private static final long serialVersionUID = 1L;

		public SearchForm(String id) {
			super(id);
			add(createSearchTypeField("searchType"));
			add(searchPanelField = createSearchPanel(SEARCH_PANEL_FIELD));
			add(includeAgreementCheck = includeAgreementCheck("includeAgreementCheck"));
			add(searchButton = createSearchButton("searchButton"));
		}

		@Override
		protected void onSubmit() {
			super.onSubmit();
			doContextSearch();
		}

	}

	/**
	 * Encapsulates the select form
	 * 
	 * @author JZB0608 - 30 June 2008
	 *
	 */
	public class SelectForm extends Form {
		private static final long serialVersionUID = 1L;

		public SelectForm(String id) {
			super(id);
//			add(selectButton = createSelectButton("selectButton", this));
			add(nextButton = createNextButton("nextButton", this));
		}

		@Override
		protected void onSubmit() {
			System.out.println("#JB4- SelectForm.submit");
			super.onSubmit();
		}
		
		

	}

	/**
	 * Create the search type field
	 * 
	 * @param id
	 * @return
	 */
	protected DropDownChoice createSearchTypeField(String id) {

		/* Create field */
		DropDownChoice choice = new DropDownChoice(id, new PropertyModel(pageModel, "searchType"), searchTypeList);

		/* Behavior */
		choice.add(new AjaxFormComponentUpdatingBehavior("change") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				swapSearchPanel(target);
				// need to also update the agreement selection checkbox
				// if an agreement type serach is slected then we need to get all agreement
				// detail back anyway
				updateAgreementSelectionCheckBox(target);
				searchButton.setEnabled((pageModel.getSearchType() != null
						&& (pageModel.getSearchType() instanceof SeperatorType) == false));
				target.add(searchButton);
			}

		});

		return choice;
	}

	/**
	 * Based on the search type selected, the checkbox will change to be selected or
	 * deselected
	 *
	 */
	private void updateAgreementSelectionCheckBox(AjaxRequestTarget target) {
		boolean includeAgreementDetails = pageModel.isShowAgreements();
		if (pageModel.getSearchType() instanceof AgreementSearchType
				|| pageModel.getSearchType() instanceof OrganisationPracticeAgreementSearchType) {
			includeAgreementDetails = true;
		}
		pageModel.setShowAgreements(includeAgreementDetails);
		target.add(includeAgreementCheck);

	}

	/**
	 * Create include agreement check box.
	 * 
	 * @param id
	 * @return
	 */
	protected CheckBox includeAgreementCheck(String id) {
		return (CheckBox) new CheckBox(id, new PropertyModel(pageModel, "showAgreements")).setOutputMarkupId(true)
				.setEnabled(pageModel.isAllowShowPartyOnly());
	}

	/**
	 * Create the search panel
	 * 
	 * @param id
	 * @return
	 */
	protected Panel createSearchPanel(String id) {
		if (pageModel.getSearchType() == null || pageModel.getSearchType() instanceof SeperatorType) {
			// Ignore this option
			pageModel.setSearchValueObject(null);
			return (Panel) new EmptyPanel(id).setOutputMarkupId(true);
		} else if (pageModel.getSearchType() == IndividualSearchType.PERSON_DETAIL) {
			// A person search
			pageModel.setSearchValueObject(new SearchIndividualNameDTO());
			return (Panel) new SearchPartyNameValuePanel(id, pageModel).setOutputMarkupId(true);
		}
		else if (pageModel.getSearchType() == OrganisationSearchType.HIERARCHY_NODE_DETAIL) {
			// A node search
			pageModel.setSearchValueObject(new HierarchySearchDetailDTO());
			return (Panel) new SearchHierarchyNodePanel(id, pageModel).setOutputMarkupId(true);
		}

		// All other searches
		pageModel.setSearchValueObject(null);
		return (Panel) new SearchValuePanel(id, pageModel).setOutputMarkupId(true);
	}

	/**
	 * Create the search button
	 * 
	 * @param id
	 * @return
	 */
	protected Button createSearchButton(String id) {
		Button but = new Button(id) {
			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
				super.onSubmit();
			}
		};
		but.setOutputMarkupId(true);
		but.setEnabled(
				pageModel.getSearchType() != null && (pageModel.getSearchType() instanceof SeperatorType) == false);
		return but;
	}

	/**
	 * Create the select button
	 * 
	 * @param id
	 * @param form
	 * @return
	 */
	protected AjaxLink createSelectButton(String id ) {

		AjaxLink link = new AjaxLink(id) {

				
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target ) {

				if (((DataGrid) searchResultPanelField).getSelectedItems().size() == 0) {
					// Give an error???
					warn(this.getString("selection.required"));
					target.add(getFeedBackPanel());
					return;
				}

				/* Add selected grid items to selected list */
				selectedItemList.clear();
				DataGrid d = (DataGrid) searchResultPanelField;
				for (IModel model : ((Collection<IModel>) d.getSelectedItems())) {
					ResultContextItemDTO itemDTO = (ResultContextItemDTO) model.getObject();
					selectedItemList.add(itemDTO);
				}
				
//				System.out.println("#JB1 - onSelect + " + ContextSearchPage.this + " - hashCode.this="
//						+ System.identityHashCode(ContextSearchPage.this) + "   - selectedItemList=" + selectedItemList
//						+ System.identityHashCode(selectedItemList));
//
//				if (pageModel.getSearchOptions() != null) {
//					System.out.println("#JB1 - onSelect Updated SearchOptions " + selectedItemList.size()
//							+ "  " +System.identityHashCode(pageModel.getSearchOptions()));
//					pageModel.getSearchOptions().getSelectedItemList().addAll(selectedItemList);
//				}
//				
//				System.out.println("#JB4 - instCreated=" + target.isPageInstanceCreated()
//						+ "  page=" + target.getPage()
//						+ "  render=" + target.getRenderCount()
//						+ "  sameObj=" + (target.getPage()==ContextSearchPage.this));
//				
				modalWindow.close(target);
			}

			@Override
			protected void onComponentTag(ComponentTag tag) {
				super.onComponentTag(tag);
				decorateComponentStyle(
						pageModel.getSearchResultList() == null || pageModel.getSearchResultList().size() == 0, tag);
			}

		};
		link.setOutputMarkupId(true);
//		but.setEnabled(false);
		return link;
	}
	
	/**
	 * Create the next button
	 * 
	 * @param id
	 * @param form
	 * @return
	 */
	protected Button createNextButton(String id, Form form) {
		Button but = new AjaxButton(id, form) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form form) {
				if (target != null) {
					target.add(getFeedBackPanel());
				}

				try {
					/* Initialise ejb bean */
					long time = System.currentTimeMillis();

					/* Do the relevant search */
					ContextSearchOptionsDTO options = new ContextSearchOptionsDTO();
					options.setRetrievePartyOnly(!pageModel.isShowAgreements());

					ResultContextSearchDTO searchDTO = contextBean.next(SRSAuthWebSession.get().getSessionUser(),
							pageModel.getSearchModelDTO());

					/* Set the result */
					pageModel.getSearchResultList().addAll(searchDTO.getResultList());
					pageModel.getDisabledSelectionList()
							.addAll(pageModel.createDisabledSelectionList(searchDTO.getResultList()));
					pageModel.setHasMoreData(searchDTO.isHasMoreData());
					pageModel.setSearchModelDTO(searchDTO.getSearchModel());

					if (searchDTO.isHasMoreData()) {
						this.info(getString("search.has.more.data"));
					}
					if (pageModel.getDisabledSelectionList().size() > 0) {
						this.info(getString("search.disabled.selection"));
					}
					if (Logger.getLogger(this.getClass()).isDebugEnabled()) {
						Logger.getLogger(this.getClass())
								.debug("Context Search next()  time=" + (System.currentTimeMillis() - time));
					}
					swapSearchResultPanel(target);
					target.add(nextButton);
				} catch (CommunicationException e) {
					throw new RuntimeException(e);
				}
			}

			@Override
			protected void onComponentTag(ComponentTag tag) {
				super.onComponentTag(tag);
				decorateComponentStyle(pageModel.isHasMoreData() == false, tag);
			}

//			@Override
//			protected IAjaxCallDecorator getAjaxCallDecorator() {
//				return new AjaxCallDecorator() {
//					private static final long serialVersionUID = 1L;
//
//					public CharSequence decorateScript(CharSequence script) {
//						return "this.disabled=true;" + script;
//					}
//				};
//			}

			@Override
			protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
				super.updateAjaxAttributes(attributes);
				attributes.getAjaxCallListeners().add(new AjaxCallListener() {

					@Override
					public CharSequence getInitHandler(Component component) {
						return "this.disabled=true;" + super.getInitHandler(component);
					}
				});
			}
		};

		but.add(new AttributeModifier("disabled", new Model(AttributeModifier.VALUELESS_ATTRIBUTE_REMOVE)));
		but.setOutputMarkupId(true);

		return but;
	}

	/**
	 * Update the panel search panel
	 * 
	 * @param target
	 */
	public void swapSearchPanel(AjaxRequestTarget target) {
		Panel panel = createSearchPanel(SEARCH_PANEL_FIELD);
		searchPanelField.replaceWith(panel);
		searchPanelField = panel;
		if (target != null) {
			target.add(searchPanelField);
		}
	}

	/**
	 * Update the panel search result panel
	 * 
	 * @param target
	 */
	public void swapSearchResultPanel(AjaxRequestTarget target) {
		Panel panel = createSearchResultPanel(SEARCH_RESULT_PANEL_FIELD);
		searchResultPanelField.replaceWith(panel);
		searchResultPanelField = panel;
		if (target != null) {
			target.add(searchResultPanelField);
		}
	}

	@Override
	public String getPageName() {
		return pageModel.getSearchOptions().getPageName();
	}

	/**
	 * Decorate the style tag to hide the component
	 * 
	 * @param isHidden Hide component if true.
	 * @param tag
	 */
	private void decorateComponentStyle(boolean isHidden, ComponentTag tag) {
		if (!isHidden) {
			return;
		}
		String val = (String) tag.getAttributes().get("style");
		val = (val == null) ? "" : val;
		val += " ;visibility:hidden;";
		tag.put("style", val);
	}

	/**
	 * Create the search result panel (Empty when no search has been done)
	 * 
	 * @param id
	 * @return
	 */
	protected Panel createSearchResultPanel(String id) {
		if (pageModel.getSearchResultList() == null) {
			return new EmptyPanel(id);
		}

		List<ResultContextItemDTO> resultContextItemDTOList = pageModel.getSearchResultList();

		/* Create the search result table */
		searchResultColumns = createSearchResultColumns();

		SRSDataGrid grid = new SRSDataGrid(id,
				new DataProviderAdapter(new SortableListDataProvider<ResultContextItemDTO>(resultContextItemDTOList)),
				searchResultColumns, null, pageModel.getDisabledSelectionList()) {

			private static final long serialVersionUID = 1L;

			/**
			 * Override the sanity check to ensure both square ('[' & ']') brackets are
			 * allowed. Used for specifying the key or index in maps and arrays
			 * respectively.
			 * 
			 * TODO jzb0608 Move to a new class This isn't needed anymore as I stopped using
			 * maps in my properties but I don't want to move it to it's own class until I'm
			 * sure we still require it (especially after updating Grid Component)
			 */
			@Override
			protected void columnSanityCheck(IGridColumn column) {
				String id = column.getId();
				if (Strings.isEmpty(id)) {
					throw new IllegalStateException("Column id must be a non-empty string.");
				}
				for (int i = 0; i < id.length(); ++i) {
					char c = id.charAt(i);
					if (!Character.isLetterOrDigit(c) && c != '.' && c != '-' && c != '_' && c != '[' && c != ']') {
						throw new IllegalStateException("Column id contains invalid character(s).");
					}
				}
				if (column.isResizable() && column.getSizeUnit() != SizeUnit.PX) {
					throw new IllegalStateException("Resizable columns size must be in the PX unit.");
				}
			}

		};
		grid.setAutoResize(false);
		grid.setRowsPerPage(10);
		grid.setContentHeight(228, SizeUnit.PX);
		grid.setAllowSelectMultiple(pageModel.getSearchOptions().isAllowMultipleSelect());

		return grid;
	}

	/**
	 * Create the search result grid column configuration
	 * 
	 * @return
	 */
	protected List<IGridColumn> createSearchResultColumns() {
		List<IGridColumn> columns = new ArrayList<IGridColumn>();

		SRSGridRowSelectionCheckBox box = new SRSGridRowSelectionCheckBox("check");

		columns.add(box);

		if (pageModel.isAllowShowPartyOrganisationPractice()) {

			// Hyperlink to be provided on Allocated amount which opens a popup
			PopupSettings popupSettings = new PopupSettings(PopupSettings.RESIZABLE);
			popupSettings.setWindowName("ManagerPopupPage").setWidth(490).setHeight(160).setTop(450).setLeft(650);

			columns.add(new PopupColumn("linkPanel", new Model("Advanced Practice number"), "partyDTO.partyOid",
					popupSettings, "partyDTO.partyOid"));
			columns.add(new PropertyColumn(new Model("Advanced Practice name"), "partyDTO.name", "partyDTO.name")
					.setInitialSize(250));

		} else {
			columns.add(new PropertyColumn(new Model("Party Id"), "partyDTO.partyOid", "partyDTO.partyOid")
					.setInitialSize(60));
			columns.add(new PropertyColumn(new Model("Name"), "partyDTO.name", "partyDTO.name"));
			columns.add(new PropertyColumn(new Model("Date of Birth"), "partyDTO.dateOfBirth", "partyDTO.dateOfBirth")
					.setInitialSize(90));
			columns.add(new PropertyColumn(new Model("ID/Company Reg"), "partyDTO.idNumber", "partyDTO.idNumber")
					.setInitialSize(120));
			columns.add(new PropertyColumn(new Model("Job Title"), "partyDTO.jobTitle", "partyDTO.jobTitle")
					.setInitialSize(180));
		}

		if (pageModel.isShowAgreements() && !pageModel.isAllowShowPartyOrganisationPractice()) {
			columns.add(new PropertyColumn(new Model("Agreement Status"), "agreementDTO.agreementStatus",
					"agreementDTO.agreementStatus").setInitialSize(120));
			columns.add(new PropertyColumn(new Model("Agreement No"), "agreementDTO.agreementNumber",
					"agreementDTO.agreementNumber").setInitialSize(100));
			columns.add(new PropertyColumn(new Model("Consultant Code"), "agreementDTO.consultantCodeFormatted",
					"agreementDTO.consultantCodeFormatted").setInitialSize(100));
			// SSM2707 Market Integration 21/07/2015 Sweta Menon Begin
//			columns.add(new PropertyColumn(new Model("Agreement Division"), 
//					"agreementDTO.agreementDivision", "agreementDTO.agreementDivision")
//						.setInitialSize(180));
			columns.add(new PropertyColumn(new Model("Sales Category"), "agreementDTO.salesCategory",
					"agreementDTO.salesCategory").setInitialSize(180));
			// SSM2707 Market Integration 21/07/2015 Sweta Menon End
			columns.add(new PropertyColumn(new Model("Agreement Branch"), "agreementDTO.branchName",
					"agreementDTO.branchName").setInitialSize(180));
		}

		if (pageModel.isShowAgreements() && !pageModel.isAllowShowPartyOrganisationPractice()) {
			columns.add(new PropertyColumn(new Model("Brokerage Name"), "agreementDTO.brokerageName",
					"agreementDTO.brokerageName").setInitialSize(180));
		}
		return columns;
	}

	/**
	 * Implement the search functionality on form submit
	 *
	 */
	protected void doContextSearch() {
		try {
			/* Do additional validation */
			if (pageModel.getSearchType() == IndividualSearchType.PERSON_DETAIL) {
				// Ensure at least one value is entered
				SearchIndividualNameDTO nameObj = (SearchIndividualNameDTO) pageModel.getSearchValueObject();
				if (nameObj.getDateOfBirth() == null && nameObj.getFirstName() == null && nameObj.getInitials() == null
						&& nameObj.getSurname() == null) {
					this.error(this.getString("person.detail.required"));
					return;
				}
			} else if (pageModel.getSearchType() == OrganisationSearchType.HIERARCHY_NODE_DETAIL) {
				// Ensure at least one value is entered
				HierarchySearchDetailDTO nameObj = (HierarchySearchDetailDTO) pageModel.getSearchValueObject();
				if ((nameObj.getType() == null || nameObj.getType().getOid() == 0)
						&& (nameObj.getChannel() == null || nameObj.getChannel().getPartyOid() == 0)) {
					this.error(this.getString("node.detail.required"));
					return;
				}
			}

			/* Initialise ejb bean */
			if (contextBean == null) {
//			 contextBean = (IContextManagement) SRSAuthWebSession.get()
//				.getEJBReference(EJBReferences.CONTEXT_MANAGEMENT);			
				try {
					contextBean = ServiceLocator.lookupService(IContextManagement.class);
				} catch (NamingException e) {
					throw new CommunicationException(e);
				}
			}
			long time = System.currentTimeMillis();

			/* Store last search type in session */
//			SRSAuthWebSession.get().setDefaultContextSearchType(pageModel.getSearchType());

			/* Do the relevant search */
			ContextSearchOptionsDTO options = new ContextSearchOptionsDTO();
			options.setRetrievePartyOnly(!pageModel.isShowAgreements());

			ResultContextSearchDTO searchDTO = contextBean.searchForContext(SRSAuthWebSession.get().getSessionUser(),
					pageModel.getSearchType(), pageModel.getSearchValueObject(), options);

			/* Set the result */
			pageModel.setSearchResultList(searchDTO.getResultList());
			pageModel.setHasMoreData(searchDTO.isHasMoreData());
			pageModel.setSearchModelDTO(searchDTO.getSearchModel());

			if (searchDTO.isHasMoreData()) {
				this.info(getString("search.has.more.data"));
			}
			if (pageModel.getDisabledSelectionList().size() > 0) {
				this.info(getString("search.disabled.selection"));
			}
			if (Logger.getLogger(this.getClass()).isDebugEnabled()) {
				Logger.getLogger(this.getClass()).debug("Context Search  time=" + (System.currentTimeMillis() - time)
						+ " ,type=" + pageModel.getSearchType() + " ,value=" + pageModel.getSearchValueObject());
			}
			swapSearchResultPanel(null);
		} catch (CommunicationException e) {
			/* To be handled by cycle processor */
			throw e;
		}

	}

	/**
	 * Returns the list of items that were selected. Empty if none were selected.
	 * 
	 * @return
	 */
	public ArrayList<ResultContextItemDTO> getSelectedItemList() {
		return selectedItemList;
	}

	@Override
	public FeedbackPanel getFeedBackPanel() {
		return feedBackPanel;
	}

	@Override
	public boolean isShowFeedBackPanel() {
		return false;
	}

}

class PopupColumn extends AbstractColumn {

	private PopupSettings popupSettings;
	private String value;

	public PopupColumn(String columnID, IModel displayModel, String sortProperty, PopupSettings popupSettings,
			String value) {
		super(columnID, displayModel, sortProperty);
		this.popupSettings = popupSettings;
		this.value = value;

	}

	private static final long serialVersionUID = 1L;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Component newCell(WebMarkupContainer parent, String componentId, IModel rowModel) {
		return new LinkedPanel(componentId, rowModel);
	}

	/**
	 * Panel with Row that has an anchor on click of which a popup has to be opened.
	 * 
	 * @author Matej Knopp
	 */
	private class LinkedPanel extends Panel {

		private static final long serialVersionUID = 1L;
		Link link = null;

		@SuppressWarnings("unchecked")
		private LinkedPanel(String componentId, final IModel model) {
			super(componentId, model);

			link = new Link(componentId, model) {

				@Override
				public void onComponentTagBody(org.apache.wicket.markup.MarkupStream markupStream,
						ComponentTag openTag) {
					Object obj = model.getObject();
					if (obj instanceof ResultContextItemDTO) {
						if (((ResultContextItemDTO) obj).getPartyDTO().getPartyOid() == 0) {
							replaceComponentTagBody(markupStream, openTag, "--");
						} else {

							replaceComponentTagBody(markupStream, openTag, "--" + (((ResultContextItemDTO) obj) != null
									? (((ResultContextItemDTO) obj).getPartyDTO() != null
											? ((ResultContextItemDTO) obj).getPartyDTO().getPartyOid()
											: "")
									: ""));
						}
					}
				};

				@Override
				public void onClick() {
					setResponsePage(new SearchAdvancePracticePopupPage((ResultContextItemDTO) model.getObject()));
				}

			}.setPopupSettings(popupSettings);

			link.setOutputMarkupId(true);
			add(link);
		}

	}
}