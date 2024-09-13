package za.co.liberty.web.pages.request;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import za.co.liberty.dto.gui.context.AgreementSearchType;

/**
 * This class represents the implementation of the Generic search tab in the 
 * Request Enquiry page.
 * 
 * @author JZB0608 - 14 Dec 2009
 *
 */
@SuppressWarnings("unused")
public class RequestGenericPanel extends AbstractRequestEnquiryPanel {

	private static final long serialVersionUID = -2744915978242135201L;
	
	protected static final List<AgreementSearchType> agreementNumberTypeList;
	static {
		// Initialise all static variables
		agreementNumberTypeList = new ArrayList<AgreementSearchType>();
		for (AgreementSearchType t : AgreementSearchType.values()) {
			agreementNumberTypeList.add(t);
		}
	}
	
	
	private DropDownChoice agreementNumberTypeField;
	private TextField agreementNumberField;
	private TextField workflowNumberField;
	
	/**
	 * Default constructor 
	 * 
	 * @param arg0
	 * @param model
	 */
	public RequestGenericPanel(String arg0, IModel model, FeedbackPanel feedbackPanel) {
		super(arg0, model,feedbackPanel);
	}	
	
	/**
	 * Create the search form which holds all the search filter fields.
	 * 
	 * @param id
	 * @return
	 */
	public Form createSearchForm(String id) {
		Form form = super.createSearchForm(id);
		form.add(agreementKindTypeField=createAgreementKindTypeField("agreementKindType"));
		form.add(agreementNumberTypeField=createAgreementNumberTypeField("agreementNumberType"));
		form.add(agreementNumberField=createAgreementNumberField("agreementNumber"));
		form.add(workflowNumberField=createWorkflowNumberField("workflowNumber"));
//		form.add(createFormValidator());
		
		return form;
	}

	// ==============================================================================================
	// Generate fields
	// ==============================================================================================
	/**
	 * Create the agreement number type field
	 * 
	 * @param id
	 * @return
	 */
	private DropDownChoice createAgreementNumberTypeField(String id) {
		DropDownChoice field = new DropDownChoice(id, new IModel() {
			private static final long serialVersionUID = -30570602008264258L;

			public Object getObject() {
				return dataModel.getAgreementNumberType();
			}
			public void setObject(Object arg0) {
				dataModel.setAgreementNumberType((AgreementSearchType) arg0);
			}
			public void detach() {
			}
			
		}, agreementNumberTypeList);
		field.setNullValid(true);
		field.add(new AjaxFormComponentUpdatingBehavior("change") {
			private static final long serialVersionUID = 1L;
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				updateShowNextButton(target);
			}		
		});
		return field;
	}
	
	/**
	 * Create the workflow number field
	 * 
	 * @param string
	 * @return
	 */
	private TextField createWorkflowNumberField(String id) {
		IModel model = new IModel() {
			private static final long serialVersionUID = 1L;
			public Object getObject() {
				return dataModel.getWorkflowNumber();
			}
			public void setObject(Object arg0) {
				dataModel.setWorkflowNumber((String)arg0);
			}
			public void detach() {
			}
		};
		TextField field = new TextField(id, model, String.class);
		field.add(new AjaxFormComponentUpdatingBehavior("change") {
			private static final long serialVersionUID = 1L;
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				updateShowNextButton(target);
			}		
		});
		return field;
	}

	/**
	 * Create the agreement number field.  Valid values for this
	 * field is determined by the agreementNumberType.
	 * 
	 * @param id
	 * @return
	 */
	private TextField createAgreementNumberField(String id) {
		IModel model = new IModel() {
			private static final long serialVersionUID = 1L;
			public Object getObject() {
				return dataModel.getAgreementNumber();
			}
			public void setObject(Object arg0) {
				dataModel.setAgreementNumber((String)arg0);
			}
			public void detach() {
			}
		};
		TextField field = new TextField(id, model, String.class);
		field.add(new AjaxFormComponentUpdatingBehavior("change") {
			private static final long serialVersionUID = 1L;
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				updateShowNextButton(target);
			}		
		});
		return field;
	}
}
