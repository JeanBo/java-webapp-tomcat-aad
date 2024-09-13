package za.co.liberty.web.pages.request;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.IModel;

import za.co.liberty.dto.contracting.ResultAgreementDTO;
import za.co.liberty.dto.userprofiles.ContextDTO;
import za.co.liberty.interfaces.gui.request.RequestEnquiryContextType;

/**
 * This class represents the implementation of the Context tab in the 
 * Request Enquiry page.
 * 
 * @author JZB0608 - 11 Dec 2009
 *
 */
public class RequestContextPanel extends AbstractRequestEnquiryPanel {
	private static final long serialVersionUID = -2744915978242175603L;

	protected static final List<RequestEnquiryContextType> requestContextTypeList;
	static {
		// Initialise all static variables
		requestContextTypeList = new ArrayList<RequestEnquiryContextType>();
		for (RequestEnquiryContextType t : RequestEnquiryContextType.values()) {
			requestContextTypeList.add(t);
		}
	}
	
	private ContextDTO contextDto;
	
	/**
	 * Default constructor 
	 * 
	 * @param arg0
	 * @param model
	 */
	public RequestContextPanel(String id, IModel model, FeedbackPanel feedbackPanel) {
		super(id, model, feedbackPanel);
	}	
	
	/**
	 * Create the search form which holds all the search filter fields.
	 * 
	 * @param id
	 * @return
	 */
	public Form createSearchForm(String id) {
		Form form = super.createSearchForm(id);
		form.add(contextTypeField=createContextTypeField("contextType"));
		return form;
	}
	
	public void setContextDTO(ContextDTO contextDto) {
		this.contextDto = contextDto;
	}
	
	@Override
	protected void doSearchButtonSubmit(AjaxRequestTarget target) {
		// Initialise the agreements being searched for
		if (dataModel.getRequestContextType()==RequestEnquiryContextType.SELECTED_AGREEMENT) {
			List<Long> list = new ArrayList<Long>();
			Long agreementNr = contextDto.getAgreementContextDTO().getAgreementNumber();
			if (agreementNr==null) {
				this.error("An agreement should be selected in context panel when context type is \"" 
						+ RequestEnquiryContextType.SELECTED_AGREEMENT + "\".");
				target.add(feedbackPanel);
				return;
			}
			list.add(agreementNr);
			dataModel.setAgreementNumberList(list);
		} else if (dataModel.getRequestContextType()==RequestEnquiryContextType.ALL_AGREEMENTS) {
			List<Long> list = new ArrayList<Long>();
			for (ResultAgreementDTO agreementObj : contextDto.getAllAgreementsList()) {
				if (agreementObj.getAgreementNumber()!=null) { 
					list.add(agreementObj.getAgreementNumber());
				}
			}
			dataModel.setAgreementNumberList(list);
		}
		
		super.doSearchButtonSubmit(target);
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
	private DropDownChoice createContextTypeField(String id) {
		DropDownChoice field = new DropDownChoice(id, new IModel() {
			private static final long serialVersionUID = -30570602008264258L;

			public Object getObject() {
				return dataModel.getRequestContextType();
			}
			public void setObject(Object arg0) {
				dataModel.setRequestContextType((RequestEnquiryContextType) arg0);
			}
			public void detach() {
			}
			
		}, requestContextTypeList);
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
