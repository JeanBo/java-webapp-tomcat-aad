package za.co.liberty.web.pages.panels;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxFallbackButton;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;

import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.data.pages.IMaintenancePageModel;
import za.co.liberty.web.pages.interfaces.IMaintenanceParent;

public abstract class LBFNDPButtonsPanel<DTO extends Object>  extends BaseModificationButtonsPanel{

	boolean includeLBFNDPCreateBtn;
	
	protected Button lbfNDPCreateBtn;
	
	public LBFNDPButtonsPanel(String id, IMaintenancePageModel pageModel,
			IMaintenanceParent parent, Form enclosingForm, Class dtoType,
			FeedbackPanel feedBackPanel, boolean includeAddNew,
			boolean includeSave, boolean includeCancel, boolean includeReset,
			boolean includeTerminate, boolean includeBroadcast, boolean includeLBFNDPCreateBtn) {
		super(id, pageModel,parent, enclosingForm, dtoType,feedBackPanel, includeAddNew,includeSave,includeCancel,includeReset,includeTerminate,includeBroadcast,false,true);
		this.includeLBFNDPCreateBtn = includeLBFNDPCreateBtn;
		//super.createControlButtonPanel();
		this.createControlButtonPanel();
		//super.setEditState(parent.getEditState(), null);
		this.setEditState(parent.getEditState(), null);
		 
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Create the button panel
	 * 
	 * @return
	 */	
	@Override
	protected Panel createControlButtonPanel() {
		super.createControlButtonPanel();
		int i=1;
		List<Button> buttonList = new ArrayList<Button>();
		
		modifyButton = createModifyButton("button"+i);	
		buttonList.add(modifyButton);
		
		if(includeAddNew){
			addNewButton = createAddNewButton("button"+(++i));
			buttonList.add(addNewButton);			
		}
			
		if(includeSave){
			saveButton = createSaveButton("button"+(++i));
			buttonList.add(saveButton);
		}
		if(includeCancel){
			cancelButton = createCancelButton("button"+(++i));
			buttonList.add(cancelButton);
		}
		if(includeReset){
			resetButton = createResetButton("button"+(++i));
			buttonList.add(resetButton);
		}
		if(includeTerminate){
			terminateButton = createTerminateButton("button"+(++i));
			buttonList.add(terminateButton);
		}	
		
		if(includeReactivate){
			reactivateButton = createReactivateButton("button"+(++i));
			buttonList.add(reactivateButton);
		}
		
		if(includeBroadcast){
			broadcastButton = createBroadcastButton("button"+(++i));
			buttonList.add(broadcastButton);
		}
		
		if(includeLBFNDPCreateBtn){
			lbfNDPCreateBtn = createLBFNDPButton("button"+(++i));
			buttonList.add(lbfNDPCreateBtn);
		}

		Button[] buttons = new Button[buttonList.size()];
		
		for(int j=0;j<buttonList.size();j++)
		{
			buttons[j] = buttonList.get(j);
		}
			
		Panel panel = ButtonHelperPanel.getInstance("controlButtonPanel",replaceButtons(buttons));
		panel.setOutputMarkupId(true);
		return panel;
		
	}

	
	@Override
	public void resetSelection() {
	
		
	}
	
	@Override
	public void setEditState(EditStateType newState, AjaxRequestTarget target){
		//super.setEditState(newState, target);
		if (super.getEditState() == EditStateType.VIEW) {			
			super.modifyButton.setEnabled(isModifyButtonEnabled());
			if(includeLBFNDPCreateBtn){
				lbfNDPCreateBtn.setEnabled(parent.hasAddAccess());
			}
		}else {
			super.modifyButton.setEnabled(false);
			if(includeLBFNDPCreateBtn){
				lbfNDPCreateBtn.setEnabled(false);
			}
		}
		if (target != null) {
			if(includeLBFNDPCreateBtn){
				target.add(lbfNDPCreateBtn);
			}
		}
		//target.addComponent(super.modifyButton);		
		
	}

	/**
	 * Will return true if terminate button should be enabled
	 * 
	 * @return
	 */
	protected boolean isLBFNDPButtonenabled(){
		if(getEditState().equals(EditStateType.VIEW)){
			return pageModel.getSelectedItem()!=null
					&& parent.hasDeleteAccess();
		}
		return false;		
	}
	
	/**
	 * Create the LBFNDP button
	 * 
	 * @param id
	 * @return
	 */
	protected Button createLBFNDPButton(String id) {
		Button button = new AjaxFallbackButton(id, enclosingForm) {

			private static final long serialVersionUID = -5330766713711809779L;

			@Override
			protected void onComponentTag(ComponentTag tag) {
				super.onComponentTag(tag);
				tag.getAttributes().put("value", "LBFNDPCreate");
				tag.getAttributes().put("type", "submit");
			}

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form form) {
				doAddNewLBFNDP_onSubmit(target, form);
			}
		};
		button.setOutputMarkupId(true);
		return button;
	}
	
	/**
	 * Called when LBF NDB create is submitted. Notify parent and 
	 * swap panels.  Ensure that selected item is set before calling.
	 * 
	 * @param target
	 * @param form
	 */
	public void doAddNewLBFNDP_onSubmit(AjaxRequestTarget target, Form form) {
		//first we check access to this function, user might have buttons enabled, code mistake, but should not be allowed to add
		if(parent.hasAddAccess()){
			pageModel.setSelectedItem(getNewDtoInstance());
			parent.setEditState(EditStateType.ADD, target);
			parent.swapContainerPanel(target);
			parent.swapNavigationPanel(target);
		}else{
			//display error
		}
	}
}
