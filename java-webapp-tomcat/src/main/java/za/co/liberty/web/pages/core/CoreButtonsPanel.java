package za.co.liberty.web.pages.core;

import org.apache.wicket.Component;
import org.apache.wicket.Session;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxCallListener;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.ajax.markup.html.form.AjaxFallbackButton;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;

import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.data.pages.IMaintenancePageModel;
import za.co.liberty.web.pages.interfaces.IMaintenanceParent;
import za.co.liberty.web.pages.panels.BaseModificationButtonsPanel;
import za.co.liberty.web.wicket.markup.html.grid.COREDataGrid;

public abstract class CoreButtonsPanel<DTO extends Object> extends
		BaseModificationButtonsPanel {

	private COREDataGrid transferGrid;

	public CoreButtonsPanel(String id, IMaintenancePageModel pageModel,
			IMaintenanceParent parent, Form enclosingForm, Class dtoType,
			FeedbackPanel feedBackPanel, boolean includeAddNew,
			boolean includeSave, boolean includeCancel, boolean includeReset,
			boolean includeTerminate, boolean includeBroadcast) {

		super(id, pageModel, parent, enclosingForm, dtoType, feedBackPanel,
				includeAddNew, includeSave, includeCancel, includeReset,
				includeTerminate, includeBroadcast);

	}

	/**
	 * Create the Save button
	 * 
	 * @param id
	 * @return
	 */
	protected Button createSaveButton(String id) {
		final Button button = new AjaxFallbackButton(id, enclosingForm) {
			private static final long serialVersionUID = -5330766713711809772L;

			@Override
			public Session getSession() {
				// TODO Auto-generated method stub
				return super.getSession();
			}

			@Override
			protected void onComponentTag(ComponentTag tag) {
				super.onComponentTag(tag);
				tag.getAttributes().put("value", "Save");
				tag.getAttributes().put("type", "button");
			}

//			@Override
//			protected IAjaxCallDecorator getAjaxCallDecorator() {
//				return new AjaxCallDecorator() {
//					private static final long serialVersionUID = 1L;
//
//					public CharSequence decorateScript(CharSequence script) {
//						// disable cancel too
//						String disableCancel = "";
//						if (cancelButton != null) {
//							disableCancel = "getElementById('"
//									+ cancelButton.getMarkupId()
//									+ "').disabled=true;";
//						}
//						return "this.disabled=true;" + disableCancel
//								+ "overlay(true);" + script;
//					}
//				};
//			}
			
			@Override
			protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
				super.updateAjaxAttributes(attributes);

			        // Way of adding any handler
				attributes.getAjaxCallListeners().add(new AjaxCallListener() {

				  @Override
				  public CharSequence getInitHandler(Component component) {
					CharSequence s =   super.getInitHandler(component);
					
					// disable cancel too
					String disableCancel = "";
					if (cancelButton != null) {
						disableCancel = "document.getElementById('"
								+ cancelButton.getMarkupId()
								+ "').disabled=true;";
					}
					return "overlay(true);" + disableCancel + ((s==null)?"":s);
				  }
							
				  @Override
				  public CharSequence getDoneHandler(Component component) {	
					CharSequence s =  super.getDoneHandler(component);
					return  "hideOverlay();" + ((s==null)?"":s);
				  }
				});
			}

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form form) {
				parent.doSave_onSubmit();
				
				// will only do below if save not successfull
				target.add(feedBackPanel);
				if(transferGrid!=null)
					target.add(transferGrid);
				
				target.add(this);
				if (cancelButton != null) {
					target.add(cancelButton);
				}
			}

			@Override
			protected void onError(AjaxRequestTarget target, Form form) {
				// validation error occured
				if (feedBackPanel != null) {
					target.add(feedBackPanel);
				}
				target.add(this);
				if (cancelButton != null) {
					target.add(cancelButton);
				}
			}
		};
		button.setOutputMarkupId(true);
		button.setOutputMarkupPlaceholderTag(true);
		return button;
	}

	
	
	public void setEditState(EditStateType newState, AjaxRequestTarget target) {
		super.setEditState(newState, target);
		modifyButton.setEnabled(isModifyButtonEnabled());
			if(includeAddNew){
				addNewButton.setEnabled(parent.hasAddAccess());
			}
			if(includeSave){
				this.saveButton.setEnabled(true);
			}
			if(includeCancel){
				this.cancelButton.setEnabled(true);
			}	
			if(includeReset){
				this.resetButton.setEnabled(false);
			}
			if(includeTerminate){				
				terminateButton.setEnabled(isTerminateButtonenabled());				
			}
			if(includeReactivate){				
				reactivateButton.setEnabled(isReactivateButtonenabled());				
			}
			if (includeBroadcast) {
				this.broadcastButton.setEnabled(isBroadcastButtonEnabled());
			}
	}

	
	
	public COREDataGrid getTransferGrid() {
		return transferGrid;
	}

	public void setTransferGrid(COREDataGrid transferGrid) {
		this.transferGrid = transferGrid;
	}

	
}
