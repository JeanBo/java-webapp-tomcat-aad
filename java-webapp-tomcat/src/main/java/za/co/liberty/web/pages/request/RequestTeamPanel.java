package za.co.liberty.web.pages.request;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import za.co.liberty.dto.userprofiles.TeamDTO;
import za.co.liberty.web.wicket.markup.html.form.SRSAbstractChoiceRenderer;

/**
 * This class represents the implementation of the Team tab in the 
 * Request Enquiry page.
 * 
 * @author JZB0608 - 14 Dec 2009
 *
 */
public class RequestTeamPanel extends AbstractRequestEnquiryPanel {

	private static final long serialVersionUID = -2744915978242175603L;

	@SuppressWarnings("unused")
	private DropDownChoice teamTypeField;
	
	/**
	 * Default constructor 
	 * 
	 * @param arg0
	 * @param model
	 */
	public RequestTeamPanel(String arg0, IModel model, FeedbackPanel feedbackPanel) {
		super(arg0, model, feedbackPanel);
	}	
	
	/**
	 * Create the search form which holds all the search filter fields.
	 * 
	 * @param id
	 * @return
	 */
	public Form createSearchForm(String id) {
		Form form = super.createSearchForm(id);
		form.add(teamTypeField=createTeamTypeField("teamType"));
		form.add(agreementKindTypeField=createAgreementKindTypeField("agreementKindType"));
		return form;
	}
	
	// ==============================================================================================
	// Generate fields
	// ==============================================================================================
	
	/**
	 * Create the context type field
	 * 
	 * @param id
	 * @return
	 */
	private DropDownChoice createTeamTypeField(String id) {
		IModel model = new IModel() {
			private static final long serialVersionUID = 1L;
			
			public Object getObject() {
				return dataModel.getTeam();
			}
			public void setObject(Object arg0) {
				dataModel.setTeam((TeamDTO) arg0);
			}
			public void detach() {	
			}
		};
		DropDownChoice field = new DropDownChoice(id, model, 
				pageModel.getAllTeamList(),
				new SRSAbstractChoiceRenderer() {
					private static final long serialVersionUID = 1L;
					
					public Object getDisplayValue(Object value) {
						return (value==null)?null:((TeamDTO)value).getTeamName();
					}
					public String getIdValue(Object value, int arg1) {
						return (value==null)?null:((TeamDTO)value).getOid()+"";
					}
		});
		field.setLabel(new Model("Team"));
		field.setRequired(true);
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
