package za.co.liberty.web.pages.panels;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.naming.NamingException;

import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxFallbackButton;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow.MaskType;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import za.co.liberty.business.guicontrollers.salesBCLinking.ISalesBCLinkingGuiController;
import za.co.liberty.dto.agreement.SalesBCLinking.BranchDetailsDTO;
import za.co.liberty.dto.agreement.SalesBCLinking.ServicingPanelDTO;
import za.co.liberty.dto.agreement.SalesBCLinking.ServicingTypeDTO;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.helpers.lookup.ServiceLocator;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.data.enums.fields.SalesBCLinkingGUIField;
import za.co.liberty.web.helpers.javascript.DialogScriptBuilder;
import za.co.liberty.web.helpers.javascript.DialogScriptBuilder.DialogType;
import za.co.liberty.web.pages.interfaces.IChangeableStatefullComponent;
import za.co.liberty.web.pages.interfaces.IMaintenanceParent;
import za.co.liberty.web.pages.salesBCLinking.ServicingPanelsViewPage;
import za.co.liberty.web.pages.salesBCLinking.model.SalesBCLinkingPageModel;
import za.co.liberty.web.system.SRSAuthWebSession;
import za.co.liberty.web.wicket.markup.html.form.SRSAbstractChoiceRenderer;

/**
 * <p>Defines a default maintenance selection panel that has a 
 * combo box with two buttons (modify, add new).</p>
 * 
 * <p>Remember to override {@linkplain #getChoiceRenderer()} when renderId and 
 * renderValue is null as an exception will be throw if you don't.  You can 
 * also override {@linkplain #getNewDtoInstance()} to implement more 
 * complicated DTO object instantiations (when add new is clicked)</p>
 * 
 * @author SSM2707 - 8th Sept 2015
 *
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class SalesBCLinkingSelectionPanel <DTO extends Object> 
extends DefaultMaintenanceSelectionPanel implements IChangeableStatefullComponent {

	private static final long serialVersionUID = 6729405618038745608L;
	
	/*Components*/
	private Component branchNameSelection;
	private Component servicingTypeSelection;
	private Component servicingPanelSelection;
	
	/*Panels*/
	private Button displayPanelBtn;
	private Form enclosingForm;
	

	/* Attributes */
	private EditStateType editState;
	private IMaintenanceParent parent;
	
	public static final int SELECTION_WIDTH = 300;
	
	private transient ISalesBCLinkingGuiController guiController;
	
	public static final String VIEW_WINDOW_PAGE_MAP = "DISPLAYPANELS_WINDOW_PAGE_MAP";
	public static final String VIEW_WINDOW_COOKIE_NAME = "DISPLAYPANELS_WINDOW_COOKIE";
	protected Page viewWindowPage;
	private ModalWindow modalViewWindow; 
	
	/**
	 * Calls default constructor {@linkplain #DefaultMaintenanceSelectionPanel(String, IMaintenancePageModel, IMaintenanceParent, Form, Class, null, null)}
	 * Remember to override {@linkplain #getChoiceRenderer()} when renderId and renderValue is null as 
	 * an exception will be throw if you don't.
	 * 
	 * @param id
	 * @param listLabel
	 * @param pageModel
	 * @param parent
	 * @param enclosingForm
	 * @param dtoType
	 */
	public SalesBCLinkingSelectionPanel(String id, SalesBCLinkingPageModel pageModel, 
			IMaintenanceParent parent, Form enclosingForm, Class<DTO> dtoType) {
		this(id, pageModel, parent, enclosingForm, dtoType, null, null);
		this.parent = parent;
	}

	/**
	 * Default constructor
	 * 
	 * @param id
	 * @param pageModel
	 * @param parent
	 * @param enclosingForm
	 * @param dtoType
	 * @param renderValue
	 * @param renderId
	 */
	public SalesBCLinkingSelectionPanel(String id,
			SalesBCLinkingPageModel pageModel, IMaintenanceParent parent,
			Form enclosingForm, Class<DTO> dtoType, String renderValue,
			String renderId) {
		super(id, null, (SalesBCLinkingPageModel)pageModel, parent, enclosingForm, dtoType, renderValue,
				renderId);
		this.pageModel = pageModel;
		this.parent = parent;
		this.renderId = renderId;
		this.renderValue = renderValue;
	}
	
	
	/**
	 * Update the edit state for this panel (enables / disables certain components)
	 */
	public void setEditState(EditStateType newState, AjaxRequestTarget target) {
		this.editState = newState;
		/* Set component access */
//		displayPanelBtn.setEnabled(getPageModelObject().getSelectedBranch() != null);
//		branchNameSelection.setEnabled(true);
//		servicingTypeSelection.setEnabled(getPageModelObject()
//				.getServicingPanels() != null
//				&& getPageModelObject().getServicingPanels().size() > 0);
//		servicingPanelSelection.setEnabled(getPageModelObject().getSelectedBranch() != null);	

		/* Update components that might have changed */
		if (target != null) {
			target.add(displayPanelBtn);
			target.add(branchNameSelection);
			target.add(servicingTypeSelection);
			target.add(servicingPanelSelection);
		}
	}


	protected DropDownChoice createBranchNameDDC(String id) {
		IModel model = new IModel() {
			private static final long serialVersionUID = 1L;

			public Object getObject() {
				return (((BranchDetailsDTO) getPageModelObject().getSelectedBranch()) != null ? ((BranchDetailsDTO) getPageModelObject()
						.getSelectedBranch()) : new BranchDetailsDTO());
			}

			public void setObject(Object arg0) {
				getPageModelObject().setSelectedBranch((BranchDetailsDTO) arg0);
			}

			public void detach() {
			}
		};
		
		List<BranchDetailsDTO> dispList =  getPageModelObject().getAccessibleBranchDetails();

		if (dispList != null && dispList.size() > 1) {
			Collections.sort(dispList, new Comparator<BranchDetailsDTO>() {
				public int compare(BranchDetailsDTO s1, BranchDetailsDTO s2) {
					if (s1.getName() == null && s2.getName() == null) {
						return -1;
					} else if (s1.getName() != null && s2.getName() == null) {
						return -1;
					} else if (s1.getName() == null && s2.getName() != null) {
						return 1;
					} else {
					return s1.getName().compareToIgnoreCase(
							s2.getName());
					}
				}
			});
		}
		
		DropDownChoice branchNameDDC = new DropDownChoice(id, model,
				dispList,
				new SRSAbstractChoiceRenderer<Object>() {
					private static final long serialVersionUID = 1L;

					public Object getDisplayValue(Object value) {
						return (value == null) ? null
								: ((BranchDetailsDTO) value).getName();
					}

					public String getIdValue(Object arg0, int arg1) {
						return ((BranchDetailsDTO) arg0).getOid() + "";
					}
				});
		if (getPageModelObject().getAccessibleBranchDetails() != null
				&& getPageModelObject().getAccessibleBranchDetails().size() > 0
				&& getPageModelObject().getSelectedBranch() == null
				&& getPageModelObject().getAccessibleBranchDetails().get(0) != null) {
			getPageModelObject().setSelectedBranch(
					getPageModelObject().getAccessibleBranchDetails().get(0));
		}
		branchNameDDC.setOutputMarkupId(true);
		/* Add select behavior */
		branchNameDDC.add(new AjaxFormComponentUpdatingBehavior("change") {
			private static final long serialVersionUID = 0L;

			@Override
			protected void onUpdate(AjaxRequestTarget arg0) {
				onChange_DDC(arg0);
			}
		});
		branchNameDDC.setRequired(true);
		branchNameDDC.setLabel(new Model(
				SalesBCLinkingGUIField.BRANCH_NAME_CONTEXTPANEL.getName()));
		return branchNameDDC;
	}

	protected DropDownChoice createServicingTypeDDC(String id) {
		IModel model = new IModel() {
			private static final long serialVersionUID = 1L;

			public Object getObject() {
				return (((ServicingTypeDTO) getPageModelObject().getSelectedServiceType()) != null ? ((ServicingTypeDTO) getPageModelObject()
						.getSelectedServiceType()) : new ServicingTypeDTO(null,null));
			}

			public void setObject(Object arg0) {
				getPageModelObject().setSelectedServiceType((ServicingTypeDTO) arg0);
			}

			public void detach() {
			}
		};
		
		DropDownChoice servicingTypeDDC = new DropDownChoice(id, model,
				getPageModelObject().getServicingTypes(), new ChoiceRenderer<ServicingTypeDTO>("typeName","typeID"));	

		servicingTypeDDC.setOutputMarkupId(true);
		/* Add select behavior */
		servicingTypeDDC.add(new AjaxFormComponentUpdatingBehavior("change") {
			private static final long serialVersionUID = 0L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				// Clear session messages if any
				// #WICKETTEST #WICKETFIX  Unsure if this is really required or how to replace it
				getFeedbackMessages().clear();
				
//				SRSAuthWebSession.get().getFeedbackMessages().clear();
				
				if (parent.getEditState() != EditStateType.VIEW) {
					// Show a warning dialog and cancel the action
					target.appendJavaScript(DialogScriptBuilder.buildShowDialog(
							DialogType.WARNING,
							"Please save or cancel the current action."));
					target.appendJavaScript(DialogScriptBuilder.buildReturnValue(false));
					return;
				}
				
				final SalesBCLinkingPageModel localPageModel = (SalesBCLinkingPageModel) pageModel;
				boolean allValuesSet = localPageModel.getSelectedBranch() != null;

				if (localPageModel.getServicingPanels() == null && allValuesSet) {

					/*
					 * Call the guiController to obtain the servicing panels.
					 */
					localPageModel
							.setServicingPanels(getGUIController()
									.findAllServicingPanels(
											localPageModel.getSelectedBranch()
													.getOid()));
				}
				/*
				 * Check the list of servicing panels and form the list of
				 * Display Servicing panels based on service type selected
				 */
				if (localPageModel.getServicingPanels() == null
						|| localPageModel.getServicingPanels().size() < 1) {
					localPageModel
							.setDisplayServicingPanels(new ArrayList<ServicingPanelDTO>());
				} else {
					List<ServicingPanelDTO> displayPanels = new ArrayList<ServicingPanelDTO>();
					int serviceType = localPageModel.getSelectedServiceType()
							.getTypeID();

					for (ServicingPanelDTO dto : localPageModel
							.getServicingPanels()) {
						if (dto.getServiceType().getTypeID() == serviceType) {
							displayPanels.add(dto);
						}
					}
				    
					localPageModel.setDisplayServicingPanels(displayPanels);
					if (displayPanels != null && displayPanels.size() > 0) {
						localPageModel.setSelectedServicingPanel(displayPanels
								.get(0));
					} else if (displayPanels == null
							|| displayPanels.size() <= 0) {
						localPageModel
								.setDisplayServicingPanels(new ArrayList<ServicingPanelDTO>());
						localPageModel
								.setSelectedServicingPanel(new ServicingPanelDTO());

					}
					
					pageModel = localPageModel;
					parent.setEditState(EditStateType.VIEW, target);
					parent.swapContainerPanel(target);
					//parent.swapNavigationPanel(target);
					parent.swapSelectionPanel(target);
				}
			}
		});
		
		servicingTypeDDC.setLabel(new Model(SalesBCLinkingGUIField.SERVICING_TYPE_CONTEXTPANEL.getName()));
		servicingTypeDDC
				.setEnabled(getPageModelObject().getSelectedBranch() != null);
		return servicingTypeDDC;
	}
	
	private void onChange_DDC(AjaxRequestTarget target) {
		
		// Clear session messages if any
		getSession().getFeedbackMessages().clear();
		
		if (parent.getEditState() != EditStateType.VIEW) {
			// Show a warning dialog and cancel the action
			target.appendJavaScript(DialogScriptBuilder.buildShowDialog(
					DialogType.WARNING,
					"Please save or cancel the current action."));
			target.appendJavaScript(DialogScriptBuilder.buildReturnValue(false));
			return;
		}
		
		
		final SalesBCLinkingPageModel localPageModel = (SalesBCLinkingPageModel) pageModel;
		boolean allValuesSet = localPageModel.getSelectedBranch() != null;

		if (allValuesSet) {
			/* Call the guiController to obtain the servicing panels. */
			List<ServicingPanelDTO> panels = getGUIController()
					.findAllServicingPanels(
							localPageModel.getSelectedBranch().getOid());
			if (panels != null && panels.size() > 1) {
				Collections.sort(panels, new Comparator<ServicingPanelDTO>() {
					public int compare(ServicingPanelDTO s1, ServicingPanelDTO s2) {
						if (s1.getPanelName() == null && s2.getPanelName() == null) {
							return -1;
						} else if (s1.getPanelName() != null && s2.getPanelName() == null) {
							return -1;
						} else if (s1.getPanelName() == null && s2.getPanelName() != null) {
							return 1;
						} else {
						return s1.getPanelName().compareToIgnoreCase(
								s2.getPanelName());
						}
					}
				});
			}
			
			localPageModel.setServicingPanels(panels);
			localPageModel.setDisplayServicingPanels(panels);
			
			if (panels!= null && panels.size()>0) {
				localPageModel.setSelectedServicingPanel(panels.get(0));
			} else {
				localPageModel.setSelectedServicingPanel(new ServicingPanelDTO());
			}
			//localPageModel.setSelectedServiceType(null);
			/*Reset the edit state to view*/
			pageModel = localPageModel;
			
			//parent.setEditState(EditStateType.VIEW, target);
			parent.swapSelectionPanel(target);
			parent.swapContainerPanel(target);
			//parent.swapNavigationPanel(target);
		}
	}
	
	protected DropDownChoice createServicingPanelDDC(String id) {
		IModel model = new IModel() {
			private static final long serialVersionUID = 1L;

			public Object getObject() {
				return (((ServicingPanelDTO) getPageModelObject()
						.getSelectedServicingPanel()) != null ? ((ServicingPanelDTO) getPageModelObject()
						.getSelectedServicingPanel()) : new ServicingPanelDTO());
			}

			public void setObject(Object arg0) {
				getPageModelObject().setSelectedServicingPanel(
						(ServicingPanelDTO) arg0);
			}

			public void detach() {
			}
		};

		List<ServicingPanelDTO> dispList = getPageModelObject()
				.getDisplayServicingPanels();

		if (dispList != null && dispList.size() > 1) {
			Collections.sort(dispList, new Comparator<ServicingPanelDTO>() {
				public int compare(ServicingPanelDTO s1, ServicingPanelDTO s2) {
					if (s1.getPanelName() == null && s2.getPanelName() == null) {
						return -1;
					} else if (s1.getPanelName() != null && s2.getPanelName() == null) {
						return -1;
					} else if (s1.getPanelName() == null && s2.getPanelName() != null) {
						return 1;
					} else {
					return s1.getPanelName().compareToIgnoreCase(
							s2.getPanelName());
					}
				}
			});
		}

		DropDownChoice servicingPanelDDC = new DropDownChoice(id, model,
				dispList, new ChoiceRenderer<ServicingPanelDTO>("panelName",
						"panelOID")) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onComponentTag(ComponentTag tag) {
				super.onComponentTag(tag);
				decorateStyleOnTag(tag);
			}

		};
		servicingPanelDDC.setOutputMarkupId(true);
		/* Add select behavior */
		servicingPanelDDC
				.add(new AjaxFormComponentUpdatingBehavior("change") {
					private static final long serialVersionUID = 0L;

					@Override
					protected void onUpdate(AjaxRequestTarget target) {
						// Moved the onchange behavior out into a do.. method.
						SalesBCLinkingSelectionPanel.this
								.doServicingPanelField_onChange(target);
						
					}
				});
		servicingPanelDDC.setLabel(new Model(
				SalesBCLinkingGUIField.SERVICING_PANEL_CONTEXTPANEL.getName()));

		if (getPageModelObject().getDisplayServicingPanels() != null
				&& getPageModelObject().getSelectedServicingPanel() == null
				&& getPageModelObject().getDisplayServicingPanels().size() > 0
				&& getPageModelObject().getDisplayServicingPanels().get(0) != null) {
			getPageModelObject().setSelectedServicingPanel(
					getPageModelObject().getDisplayServicingPanels().get(0));
		} else if (getPageModelObject().getDisplayServicingPanels() == null
				&& getPageModelObject().getDisplayServicingPanels().size() <= 0
				&& getPageModelObject().getSelectedServicingPanel() == null) {
			getPageModelObject().setSelectedServicingPanel(
					new ServicingPanelDTO());
		}
		
		servicingPanelDDC
				.setEnabled(getPageModelObject().getSelectedBranch() != null);
		return servicingPanelDDC;
	}
	

	protected Button createDisplayPanelBtn(String id) {
		Button button = new AjaxFallbackButton(id, enclosingForm) {

			private static final long serialVersionUID = 1L;
					
			@Override
			protected void onComponentTag(ComponentTag tag) {
				super.onComponentTag(tag);
				tag.getAttributes().put("value", "Display Servicing Panels");
				tag.getAttributes().put("type", "submit");
			}	

			@Override
			protected void onSubmit(AjaxRequestTarget arg0, Form arg1) {
				if (parent.getEditState() != EditStateType.VIEW) {
					// Show a warning dialog and cancel the action
					arg0.appendJavaScript(DialogScriptBuilder.buildShowDialog(
							DialogType.WARNING,
							"Please save or cancel the current action."));
					arg0.appendJavaScript(DialogScriptBuilder
							.buildReturnValue(false));
					return;
				}
				
				modalViewWindow.show(arg0);	
			}		
			
		};
		button.setEnabled(getPageModelObject().getAccessibleBranchDetails() != null
				&& getPageModelObject().getAccessibleBranchDetails().size() > 0);
		button.setOutputMarkupId(true);
		button.setDefaultFormProcessing(false);
		return button;
	}	
	
	/**
	 * Return the model required to get the selected item
	 * @return
	 */
	protected IModel getSelectedItemModel() {
		return new PropertyModel(
				pageModel, "selectedBranch");
	}

	/**
	 * The onchange behaviour of the selection list field (combo box)
	 * @param target
	 */
	private void doServicingPanelField_onChange(AjaxRequestTarget target) {
		if (parent.getEditState() != EditStateType.VIEW) {
			// Show a warning dialog and cancel the action
			target.appendJavaScript(DialogScriptBuilder.buildShowDialog(
					DialogType.WARNING,
					"Please save or cancel the current action."));
			target.appendJavaScript(DialogScriptBuilder.buildReturnValue(false));
			return;
		}
		// Clear session messages if any
		
		parent.setEditState(EditStateType.VIEW, target);
		parent.swapContainerPanel(target);
		//parent.swapNavigationPanel(target);
	}
	
	public void resetSelection() {
		
	}
	
	/**
	 * Create the modal view window
	 * 
	 * @param id
	 * @return
	 */
	public ModalWindow createModalViewWindow(String id) {
		final ModalWindow window = new ModalWindow(id);

		window.setTitle("Servicing Panels");

		// Create the page
		window.setPageCreator(new ModalWindow.PageCreator() {
			private static final long serialVersionUID = 1L;

			public Page createPage() {
				final SalesBCLinkingPageModel localPageModel = (SalesBCLinkingPageModel) pageModel;
				if (localPageModel.getSelectedBranch() != null
						&& localPageModel.getSelectedBranch().getOid() > 0) {
					return createViewWindowPage(window,
							localPageModel.getSelectedServiceType());
				} else {
					return null;
				}

			}
		});

		// Initialise window settings
		window.setMinimalHeight(420);
		window.setInitialHeight(520);
		window.setMinimalWidth(850);
		window.setInitialWidth(850);
		window.setMaskType(MaskType.SEMI_TRANSPARENT);
		window.setCssClassName(ModalWindow.CSS_CLASS_GRAY);
//		window.setPageMapName(VIEW_WINDOW_PAGE_MAP);

		return window;
	}
	
	/**
	 * Create an the ViewRequest window page.
	 * 
	 * @param window
	 * @return
	 */
	protected Page createViewWindowPage(ModalWindow window,
			ServicingTypeDTO serviceType) {
		return new ServicingPanelsViewPage(window,getPageModelObject());
	}
	
	/**
	 * Add the components to the form and must be called after the constructor 
	 * is run. Allows additional attributes to be set before create the
	 * components.
	 */
	@Override
	protected void initialiseForm() {
		add(new Label("branchNameLabel", new Model(SalesBCLinkingGUIField.BRANCH_NAME_CONTEXTPANEL.getName())));
		add(branchNameSelection = createBranchNameDDC("branchNameSelection"));
		add(new Label("servicingTypeLabel", new Model(SalesBCLinkingGUIField.SERVICING_TYPE_CONTEXTPANEL.getName())));
		add(servicingTypeSelection = createServicingTypeDDC("servicingTypeSelection"));
		add(new Label("servicingPanelLabel", new Model(SalesBCLinkingGUIField.SERVICING_PANEL_CONTEXTPANEL.getName())));
		add(servicingPanelSelection = createServicingPanelDDC("servicingPanelSelection"));
		add(displayPanelBtn = createDisplayPanelBtn("displayPanelBtn"));
		add(modalViewWindow = createModalViewWindow("panelViewWindow"));

		setEditState(this.editState, null);
	}
	
	protected ISalesBCLinkingGuiController getGUIController() {
		if (guiController == null) {
			try {
				guiController = ServiceLocator
						.lookupService(ISalesBCLinkingGuiController.class);
			} catch (NamingException namingErr) {
				CommunicationException comm = new CommunicationException(
						"ISalesBCLinkingGuiController can not be looked up!");
				throw new CommunicationException(comm);
			}
		}
		return guiController;
	}
	
	private SalesBCLinkingPageModel getPageModelObject() {
		final SalesBCLinkingPageModel localPageModel = (SalesBCLinkingPageModel) pageModel;
		
		return localPageModel;
	}
}