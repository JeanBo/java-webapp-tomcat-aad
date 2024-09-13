package za.co.liberty.web.pages.maintainagreement;

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.repeater.RepeatingView;

import za.co.liberty.dto.agreement.maintainagreement.WorkflowDTO;
import za.co.liberty.web.data.enums.ComponentType;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.pages.panels.BasePanel;
import za.co.liberty.web.pages.panels.GUIFieldPanel;
import za.co.liberty.web.pages.panels.HelperPanel;

public class WorkflowPanel extends BasePanel {

	private WorkflowDTO model;
	private GUIFieldPanel workflowNumberPanel;
	private GUIFieldPanel workflowCommentPanel;
	private RepeatingView workflowPanel;
	private boolean initialised;
		
	public WorkflowPanel(String id, EditStateType editState,WorkflowDTO model) {
		super(id, editState);
		this.model=model;
		initialised=false;
	}
	
	@Override
	protected void onBeforeRender() {
		if (!initialised) {
			add(new WorkflowForm("workflowForm"));
			initialised=true;
		}
		super.onBeforeRender();
	}
	
	public class WorkflowForm extends Form {

		public WorkflowForm(String id) {
			super(id);
			add(getWorkflowPanel());
		}
	}

	public GUIFieldPanel getWorkflowNumberPanel() {
		if (workflowNumberPanel == null) {
			HelperPanel ret = createPageField(getWorkflowModel(), "Workflow Number", 
					"workflowNumber", ComponentType.TEXTFIELD,
					false,
					false, new EditStateType[] {
						EditStateType.ADD,EditStateType.MODIFY,EditStateType.TERMINATE 
					}); 
			workflowNumberPanel = createGUIFieldPanel(
					"Workflow Number", 
					"Workflow Number", 
					"workflowNumber", 
					ret.getEnclosedObject(),
					false);
		}
		return workflowNumberPanel;
	}
	
	public RepeatingView getWorkflowPanel() {
		if (workflowPanel == null) {
			workflowPanel = new RepeatingView("workflowPanel");
			workflowPanel.add(getWorkflowNumberPanel());
			workflowPanel.add(getWorkflowCommentPanel());
		}
		return workflowPanel;
	}

	public GUIFieldPanel getWorkflowCommentPanel() {
		if (workflowCommentPanel == null) {
			HelperPanel ret = createPageField(getWorkflowModel(), "Workflow Comment", 
					"workflowComment", ComponentType.TEXTFIELD,
					true,
					false, new EditStateType[] {
						EditStateType.ADD,EditStateType.TERMINATE,EditStateType.MODIFY
					}); 
//			if (ret.getEnclosedObject() instanceof TextField) {
				((TextField)ret.getEnclosedObject()).setRequired(true);
//			}
			workflowCommentPanel = createGUIFieldPanel(
					"Workflow Comment", 
					"Workflow Comment", 
					"workflowComment", 
					ret.getEnclosedObject(),
					false);
		}
		return workflowCommentPanel;
	}

	private WorkflowDTO getWorkflowModel() {
		return model;
	}

}
