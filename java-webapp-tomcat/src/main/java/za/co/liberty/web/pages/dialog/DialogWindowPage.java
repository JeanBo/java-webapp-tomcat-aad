package za.co.liberty.web.pages.dialog;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxCallListener;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.Panel;

import za.co.liberty.web.pages.BaseWindowPage;
import za.co.liberty.web.pages.panels.ButtonHelperPanel;
import za.co.liberty.web.pages.panels.HelperPanel;

/**
 * Generic page for dialog windows
 * 
 * @author jzb0608
 *
 */
public abstract class DialogWindowPage <MODEL extends Object> extends BaseWindowPage {

	private AjaxButton cancelButton;
	private ModalWindow window;
	private Panel contextPanel;
	private Panel buttonPanel;
	private AjaxButton saveButton;
	private boolean finished = false;
	private MODEL pageModel;
	private Boolean answerObject;
	
	public enum DIALOG_TYPE {INFO,WARN};
	
	
	public DialogWindowPage(ModalWindow window, MODEL pageModel) {
		super();
		this.window = window;
		this.pageModel = pageModel;
		add(new DialogForm("dialogForm"));
		window.setWindowClosedCallback(new ModalWindow.WindowClosedCallback() {
			private static final long serialVersionUID = 1L;			
			public void onClose(AjaxRequestTarget target) {
				processClose(target,finished);
			}
		});
	}
	
	private class DialogForm extends Form {

		public DialogForm(String id) {
			super(id);
			initComponents();
		}

		private void initComponents() {
			add(getContextPanel("contextPanel"));
			add(getButtonPanel("buttonPanel"));
		}
		
	}

	@Override
	public String getPageName() {
		return "Workflow Details";
	}
	
	public Boolean getAnswer() {
		return answerObject;
	}
	
	/**
	 * This method is called when the window is closed, so that post
	 * close processing can be done. The finished successfully flag
	 * indicates if the save was completed successfully before the 
	 * window closed.
	 * 
	 * @param target
	 * @param finishedSuccesfully
	 */
	public abstract void processClose(AjaxRequestTarget target, boolean finishedSuccessfully);
	
	private AjaxButton getSaveButton(String id) {
		if (saveButton==null) {
			saveButton = new AjaxButton(id) {
				@Override
				protected void onSubmit(AjaxRequestTarget target, Form form) {
					System.out.println("Closing save button");
					finished = true;
					answerObject = Boolean.TRUE;
					window.close(target);
				}

				@Override
				protected void onError(AjaxRequestTarget target, Form form) {
					finished = false;
					target.add(getFeedBackPanel());	
					target.add(this);	
				}
				
//				@Override
//				protected IAjaxCallDecorator getAjaxCallDecorator() {
//					return new AjaxCallDecorator() {
//						private static final long serialVersionUID = 1L;
//
//						public CharSequence decorateScript(CharSequence script) {							
//							return "this.disabled=true;overlay(true);parent.overlay(true);" + script;
//						}
//					};
//				}
				
				// #WICKETTEST - tEST THIS CALL
				@Override
				protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
					super.updateAjaxAttributes(attributes);
					attributes.getAjaxCallListeners().add(new AjaxCallListener() {

						@Override
						public CharSequence getInitHandler(Component component) {
							return "this.disabled=true;overlay(true);parent.overlay(true);" + super.getInitHandler(component);
						}
					});
				}
				
				@Override
				protected void onComponentTag(ComponentTag tag) {
					super.onComponentTag(tag);
					tag.getAttributes().put("value", "OK");
					
				}
				
			};
			saveButton.setOutputMarkupId(true);
		}
		return saveButton;
	}
	
	private AjaxButton getCancelButton(String id) {
		if (cancelButton==null) {
			cancelButton = new AjaxButton(id) {
				@Override
				protected void onSubmit(AjaxRequestTarget target, Form form) {
					finished = false;
					answerObject = Boolean.FALSE;
					window.close(target);
				}
				
				@Override
				protected void onComponentTag(ComponentTag tag) {
					super.onComponentTag(tag);
					tag.getAttributes().put("value", "Cancel");
					
				}

			};
			cancelButton.setOutputMarkupId(true);
			cancelButton.setDefaultFormProcessing(false);
		}
		return cancelButton;
	}

	private Panel getContextPanel(String id) {
		if (contextPanel==null) {
//			contextPanel = new EmptyPanel(
//					id);
			contextPanel = HelperPanel.getInstance(id, new Label("value", getDailogMessage()));
		}
		return contextPanel;
	}
	
	/**
	 * Override to provide message
	 * 
	 * @return
	 */
	public abstract String getDailogMessage();
	

	/**
	 * Create the navigation panel
	 * 
	 * @return
	 */
	protected Panel getButtonPanel(String id) {
			Button[] buttons = createButtons();
			if(buttons == null || buttons.length == 0)
				return (Panel) new EmptyPanel(id)
				.setOutputMarkupId(true);
			/* Place the navigational button panel */
			Panel panel = ButtonHelperPanel.getInstance(id, buttons);
			panel.setOutputMarkupId(true);
			return panel;
	}
	
	public Button[] createButtons() {
		return new Button[] {getSaveButton("button1"), getCancelButton("button2")};
	}
	
	

	@Override
	public boolean isShowFeedBackPanel() {
		return true;
	}
	

}
