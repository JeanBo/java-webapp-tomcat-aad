package za.co.liberty.web.pages.request;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AbstractAutoCompleteRenderer;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteSettings;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.IAutoCompleteRenderer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.Response;
import org.apache.wicket.util.convert.IConverter;

import za.co.liberty.dto.gui.request.RequestUserDTO;

/**
 * This class represents the implementation of the User tab in the 
 * Request Enquiry page.
 * 
 * @author JZB0608 - 14 Dec 2009
 *
 */
public class RequestUserPanel extends AbstractRequestEnquiryPanel {

	private static final long serialVersionUID = -2744915978242175603L;

	private List<RequestUserDTO> userChoiceList;
	
	private RequestUserDTO tempUserDTO = new RequestUserDTO(-1,null,null);
	
	@SuppressWarnings("unused")
	private AutoCompleteTextField userTypeField;	
	
	/**
	 * Default constructor 
	 * 
	 * @param arg0
	 * @param model
	 */
	public RequestUserPanel(String arg0, IModel model, FeedbackPanel feedbackPanel) {
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
		form.add(userTypeField=createUserTypeField("userType"));
		form.add(agreementKindTypeField=createAgreementKindTypeField("agreementKindType"));
		return form;
	}
	
	// ==============================================================================================
	// Generate fields
	// ==============================================================================================
	/**
	 * Creates an auto complete text field.
	 */
	public AutoCompleteTextField createUserTypeField(String id) {
		if (dataModel.getUser()!=null) {
			// Initialise choices
			userChoiceList = new ArrayList<RequestUserDTO>();
			userChoiceList.add(dataModel.getUser());
		}
		
		Model<RequestUserDTO> model = new Model<RequestUserDTO>(){
			private static final long serialVersionUID = 1L;

			@Override
			public RequestUserDTO getObject() {				
				return dataModel.getUser();
			}

			@Override
			public void setObject(RequestUserDTO object) {
				dataModel.setUser(object);				
			}
			
		};
		
		// Renderer
		IAutoCompleteRenderer<RequestUserDTO> renderer = new AbstractAutoCompleteRenderer<RequestUserDTO>() {
			private static final long serialVersionUID = 1L;

			@Override
			protected String getTextValue(RequestUserDTO object) {
				if (object == null) {
					return "";
				}
				return ((RequestUserDTO)object).toString();
			}

			@Override
			protected void renderChoice(RequestUserDTO object, Response response,
					String criteria) {
				response.write(getTextValue(object));
			}
		};

		
		
		// Create the field
		final AutoCompleteTextField<RequestUserDTO> field = new AutoCompleteTextField<RequestUserDTO>(id, model, RequestUserDTO.class, renderer, new AutoCompleteSettings().setPreselect(true)) {
			private static final long serialVersionUID = 1L;
			@SuppressWarnings("unchecked")
			@Override
			protected Iterator<RequestUserDTO> getChoices(String input) {
				if (input.length()<=2) {
					return Collections.EMPTY_LIST.iterator();
				}
				userChoiceList = getGuiController().findUsersWithUacfStartingWith(input);
				return userChoiceList.iterator();
			}
			
			
			
			@Override
			public <C> IConverter<C> getConverter(Class<C> type) {

				if(type == RequestUserDTO.class){
					return new IConverter(){						
						private static final long serialVersionUID = 1L;
						public Object convertToObject(String value, Locale locale) {
							String strVal = value;
							strVal = strVal.trim();
							if (strVal.length()>=3) {
								// At least the uacfID should be entered.
								String uacfId = (strVal.length()==7) ? strVal : strVal.substring(0, 
										(strVal.length()>7) ? 7 : strVal.length());
								List<RequestUserDTO> list =  getGuiController().findUsersWithUacfStartingWith(uacfId);
								if (list.size() > 0) {
									for(RequestUserDTO user : list){
										if(user.getUacfId().equalsIgnoreCase(uacfId)){
	//										 Yay, found it
											dataModel.setUser(user);
											userChoiceList.add(dataModel.getUser());
	//										System.out.println("     -- found DTO with FIND "+ dataModel.getUser());
											return dataModel.getUser();
										}
									}
								} else {
//									System.out.println("  -- SEARCH returned x items = "+ list.size());
								}
							}
//							System.out.println("  -- dto   -- not found");
							
							/* */
							tempUserDTO.setName(value);
							dataModel.setUser(null);
							return tempUserDTO;
						}
						public String convertToString(Object value, Locale locale) {							
							if(value != null && value instanceof RequestUserDTO){
								return ((RequestUserDTO)value).toString();
							}else{
								return null;
							}
						}						
					};
				}else{
					return super.getConverter(type);
				}
			}		
		};
		
		
		
		// Refresh this component when updated to force IModel logic to be applied.
		AjaxFormComponentUpdatingBehavior behaviour = new AjaxFormComponentUpdatingBehavior("change") {
			private static final long serialVersionUID = 1L;
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				updateShowNextButton(target);
				//target.addComponent(field);
			}
			@Override
			protected void onError(AjaxRequestTarget target, RuntimeException e) {
				super.onError(target, e);				
				if(feedbackPanel != null){
					target.add(feedbackPanel);
				}
			}
			
			
		};
		field.add(behaviour);
		field.setOutputMarkupId(true);
		field.add(new AttributeModifier("autocomplete", "off"));
		field.setRequired(true);
		field.setLabel(new Model<String>("User"));
		return field;
	}

}
