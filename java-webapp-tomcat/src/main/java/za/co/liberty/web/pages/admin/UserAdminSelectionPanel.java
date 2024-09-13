package za.co.liberty.web.pages.admin;

import java.util.ArrayList;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxFallbackButton;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;

import za.co.liberty.dto.gui.context.ResultContextItemDTO;
import za.co.liberty.web.data.enums.ContextType;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.data.pages.IMaintenancePageModel;
import za.co.liberty.web.pages.interfaces.IMaintenanceParent;
import za.co.liberty.web.pages.panels.AutoCompleteMaintenanceSelectionPanel;
import za.co.liberty.web.pages.panels.ButtonHelperPanel;
import za.co.liberty.web.pages.search.ContextSearchPopUp;

public abstract class UserAdminSelectionPanel<DTO extends Object> extends 
	AutoCompleteMaintenanceSelectionPanel<DTO> {
	
	//private FeedbackPanel feedBackPanel;
	//private ResultPartyDTO currentSearchEmployee;
	private ModalWindow createSearchWindow;
	//private String uacfID;
	
	protected Button searchButton;
	
	/**
	 * Default constructor
	 * 
	 * @param id
	 * @param listLabel
	 * @param pageModel
	 * @param parent
	 * @param enclosingForm
	 * @param dtoType
	 */
	public UserAdminSelectionPanel(String id, String listLabel,
			IMaintenancePageModel pageModel, IMaintenanceParent parent,
			Form enclosingForm, Class dtoType) {
		super(id, listLabel, pageModel, parent, enclosingForm, dtoType);
	}

	@Override
	protected Panel createControlButtonPanel() {
		modifyButton = createModifyButton("button1");
		addNewButton = createAddNewButton("button2");
		searchButton = createSearchButton("button3");
		
		add(createSearchWindow = createSearchWindow("searchSecurityWindow"));
		
		Panel panel = ButtonHelperPanel.getInstance("controlButtonPanel",
				modifyButton, addNewButton, searchButton);
		panel.setOutputMarkupId(true);
		return panel;

	}
	
	@Override
	public void setEditState(EditStateType newState, AjaxRequestTarget target) {
		super.setEditState(newState, target);
		// edit state for search
	}

	/**
	 * Create the add new button
	 * 
	 * @param id
	 * @return
	 */
	protected Button createSearchButton(String id) {
		// TODO Get form differently
		Button button = new AjaxFallbackButton(id, enclosingForm) {

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
		button.setOutputMarkupId(true);
		return button;
	}
	
	private ModalWindow createSearchWindow(String id) {

		ContextSearchPopUp popUp = new ContextSearchPopUp() {

			@Override
			public ContextType getContextType() {
				return ContextType.PARTY_PERSON_ONLY;
			}

			@Override
			public void doProcessSelectedItems(AjaxRequestTarget target,
					ArrayList<ResultContextItemDTO> selectedItemList) {
				UserAdminSelectionPanel.this.doProcessSearchResult(target, selectedItemList);
			}

		};		
		ModalWindow win = popUp.createModalWindow(id);
		//MSK#Change below method not in wicket 7 so using setCookieName
		win.setCookieName("uacfidSearchPageMap");//win.setPageMapName("uacfidSearchPageMap");
		return win;	
	}
	
	/**
	 * Process the result returned from the search window.
	 * 
	 * @param target
	 * @param selectedItemList
	 */
	public abstract void doProcessSearchResult(AjaxRequestTarget target,
			ArrayList<ResultContextItemDTO> selectedItemList);
	
}
