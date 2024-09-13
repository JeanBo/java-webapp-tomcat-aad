package za.co.liberty.web.pages.contactdetail;

import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AbstractAutoCompleteRenderer;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteSettings;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.Response;
import org.apache.wicket.util.string.Strings;

import za.co.liberty.business.guicontrollers.contactdetail.IContactDetailsGUIController;
import za.co.liberty.dto.party.contactdetail.AddressDTO;
import za.co.liberty.dto.party.contactdetail.PhysicalAddressDTO;
import za.co.liberty.dto.party.contactdetail.search.AddressSearchType;
import za.co.liberty.dto.party.contactdetail.search.SuburbSearchDTO;
import za.co.liberty.dto.party.contactdetail.type.ContactDetailType;
import za.co.liberty.interfaces.rating.SuburbSearchType;
import za.co.liberty.web.data.enums.ComponentType;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.pages.contactdetail.model.ContactDetailsPanelModel;
import za.co.liberty.web.pages.panels.BasePanel;
import za.co.liberty.web.pages.panels.HelperPanel;
import za.co.liberty.web.wicket.markup.html.form.SRSDateField;

/**
 * Base Panel for addresses
 * 
 * @author DZS2610
 * 
 */
public class AddressBasePanel extends BasePanel {		
	
	private ContactDetailsPanelModel panelModel;
	
	private AddressDTO dtoToWorkOn;
	
	private transient IContactDetailsGUIController controller = null;	

	private static final long serialVersionUID = 1L;
	
	
	private HelperPanel postCodePanel;
	
	private HelperPanel suburbPanel;
	
	private HelperPanel cityPanel;
	
	//Keep so we dont need to retreive again
	private transient List<SuburbSearchDTO> currentSuburbList = null;	


	/**
	 * @param arg0
	 */
	public AddressBasePanel(String id, ContactDetailsPanelModel panelModel,AddressDTO dtoToWorkOn,
			EditStateType editState) {
		super(id,editState);			
		this.dtoToWorkOn = dtoToWorkOn;
		if(panelModel != null){
			this.panelModel = panelModel;
		}else{
			panelModel = new ContactDetailsPanelModel(null, null);
		}
		if(dtoToWorkOn == null){
			dtoToWorkOn = new PhysicalAddressDTO();
			error("Please provide an address for the address panel to function correctly");
		}				
		init();
	}
	
	/**
	 * Get the gui controller
	 * @return
	 */
	private IContactDetailsGUIController getController(){
		if(controller == null){
			controller = panelModel.getContactDetailsController();
		}
		return controller;
	}
	
	
	/**
	 * Initialize the panel
	 *
	 */
	private void init(){
		add(suburbPanel = createSuburbfield("suburb"));
		add(postCodePanel = createPostCodefield("postCode"));//this.createComponent(dtoToWorkOn, "Post Code", "postCode", ComponentType.TEXTFIELD, true));
		add(cityPanel = createCityfield("city"));//this.createComponent(dtoToWorkOn, "City", "city", ComponentType.TEXTFIELD, true));
		//TODO Removing selection on modify as this still needs to be done				
		//Remove modify from below once done
		if(getEditState() == EditStateType.ADD || dtoToWorkOn.getOid() <= 0){
			dtoToWorkOn.setEffectiveFrom(new Date());
			HelperPanel startPanel = this.createComponent(dtoToWorkOn, "Start Date", "effectiveFrom", ComponentType.DATE_SELECTION_TEXTFIELD, true,false);
			if(startPanel.getEnclosedObject() instanceof TextField){
				((TextField)startPanel.getEnclosedObject()).add(new AttributeModifier("readonly","true"));
			}
			if(startPanel.getEnclosedObject() instanceof SRSDateField) {
				startPanel.getEnclosedObject().add(
						((SRSDateField)startPanel.getEnclosedObject()).newDatePicker());
			}
			add(startPanel);
			//add(this.createComponent(dtoToWorkOn, "End Date", "effectiveTo", ComponentType.DATE_SELECTION_TEXTFIELD, false,false));
			HelperPanel endPanel = this.createComponent(dtoToWorkOn, "End Date", "effectiveTo", ComponentType.DATE_SELECTION_TEXTFIELD, false,false);
			if(endPanel.getEnclosedObject() instanceof SRSDateField) {
				endPanel.getEnclosedObject().add(
						((SRSDateField)endPanel.getEnclosedObject()).newDatePicker());
			}
			add(endPanel);
		}else{	
			//TEMP TODO remove below
			Label start = new Label("effectiveFrom",new PropertyModel(dtoToWorkOn,"effectiveFrom"));
			Label end = new Label("effectiveTo",new PropertyModel(dtoToWorkOn,"effectiveTo"));
			add(start);
			add(end);
		}
	}
	
	/**
	 * Create the city field
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private HelperPanel createCityfield(String id){	
		//removing autocomplete for city as we validate on rating and city does not bring back suburbs and postal codes
		TextField cityField = new TextField("value",new PropertyModel(dtoToWorkOn,id));
//		final AutoCompleteTextField cityField = new AutoCompleteTextField(
//				"value", new PropertyModel(dtoToWorkOn,id),
//				new AbstractAutoCompleteRenderer() {
//					private static final long serialVersionUID = 1L;
//
//					@Override
//					protected String getTextValue(Object object) {
//						return (String)object;
//					}
//
//					@Override
//					protected void renderChoice(Object object,
//							Response response, String arg2) {
//						response.write((String)object);
//					}
//				}) {		
//
//			private static final long serialVersionUID = 1L;
//			
//			@Override
//			protected Iterator getChoices(String input) {
//				if (Strings.isEmpty(input)) {
//					return Collections.EMPTY_LIST.iterator();
//				}					
//				return getController().findCity(input).iterator();
//			}			
//		};			
		cityField.add(new AttributeModifier("size","30"));
		cityField.setLabel(new Model("City"));		
//		cityField.add(StringValidator.maximumLength(
//				(dtoToWorkOn.getType() == ContactDetailType.PHYSICAL_ADDRESS) ? 23 : 35));
		HelperPanel panel = createPageField(id,"City", cityField, new EditStateType[]{EditStateType.ADD,EditStateType.MODIFY});
		panel.setOutputMarkupId(true);
		panel.setOutputMarkupPlaceholderTag(true);						
		return panel;		
	}	
	
	/**
	 * Create the post code field
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private HelperPanel createPostCodefield(String id){		
		final AutoCompleteTextField postCodeField = new AutoCompleteTextField(
				"value", new PropertyModel(dtoToWorkOn,id),String.class,
				new AbstractAutoCompleteRenderer() {
					private static final long serialVersionUID = 1L;

					@Override
					protected String getTextValue(Object object) {
						return ((SuburbSearchDTO) object).getSuburbName() + ((SuburbSearchDTO) object).getCity()  + ((SuburbSearchDTO) object).getPostalCode();
					}

					@Override
					protected void renderChoice(Object object,
							Response response, String arg2) {
						response.write(((SuburbSearchDTO) object).getSuburbName());
					}
				},new AutoCompleteSettings().setPreselect(true)) {		

			private static final long serialVersionUID = 1L;
			
			@Override
			protected Iterator getChoices(String input) {
				if (Strings.isEmpty(input) || input.length() < 4) {
					return Collections.EMPTY_LIST.iterator();
				}	
				if(dtoToWorkOn.getType() != null){
					if(dtoToWorkOn.getType() == ContactDetailType.PHYSICAL_ADDRESS){
						currentSuburbList  = getController().findSuburb(SuburbSearchType.POST_CODE, input,AddressSearchType.STREET);
					}else if(dtoToWorkOn.getType() == ContactDetailType.POSTAL_ADDRESS){
						currentSuburbList  = getController().findSuburb(SuburbSearchType.POST_CODE, input,AddressSearchType.BOX);
					}
				}else{
					currentSuburbList  = getController().findSuburb(SuburbSearchType.POST_CODE, input);
				}
				return currentSuburbList.iterator();
			}			
		};	
		postCodeField.add(new AjaxFormComponentUpdatingBehavior("change"){
			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				//update postcode and city	
				if(dtoToWorkOn.getPostCode() != null && !dtoToWorkOn.getPostCode().equals("") && currentSuburbList != null){					
					//get from list and populate other fields					
					for(SuburbSearchDTO search : currentSuburbList){
						String selection = search.getSuburbName() + search.getCity()  + search.getPostalCode();
						if(selection.equalsIgnoreCase(dtoToWorkOn.getPostCode())){
							updateSurbfields(search,target);												
							break;
						}
					}				
				}							
			}			
		});
		
		postCodeField.setLabel(new Model("Postal Code"));	
//		postCodeField.setRequired(true);
		//postCodeField.add(new SimpleAttributeModifier("size","5"));
		postCodeField.add(new AttributeModifier("maxlength","4"));
//		postCodeField.add(StringValidator.maximumLength(9));
		HelperPanel panel = createPageField(id,"Postal Code", postCodeField, new EditStateType[]{EditStateType.ADD,EditStateType.MODIFY});
		panel.setOutputMarkupId(true);
		panel.setOutputMarkupPlaceholderTag(true);						
		return panel;		
	}	
	
	/**
	 * Create the suburb field
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private HelperPanel createSuburbfield(String id){		
		final AutoCompleteTextField suburbField = new AutoCompleteTextField(
				"value", new PropertyModel(dtoToWorkOn,id),
				new AbstractAutoCompleteRenderer() {
					private static final long serialVersionUID = 1L;

					@Override
					protected String getTextValue(Object object) {
						return ((SuburbSearchDTO) object).getSuburbName()  + ((SuburbSearchDTO) object).getPostalCode();
					}

					@Override
					protected void renderChoice(Object object,
							Response response, String arg2) {
						response.write(((SuburbSearchDTO) object).getSuburbName() + "("+((SuburbSearchDTO) object).getCity()+")");
					}
				}) {		

			private static final long serialVersionUID = 1L;
			
			@Override
			protected Iterator getChoices(String input) {
				currentSuburbList = Collections.EMPTY_LIST;
				if (Strings.isEmpty(input)) {
					return currentSuburbList.iterator();
				}				
				if(dtoToWorkOn.getType() != null){
					if(dtoToWorkOn.getType() == ContactDetailType.PHYSICAL_ADDRESS){
						currentSuburbList  = getController().findSuburb(SuburbSearchType.SUBURB_NAME, input,AddressSearchType.STREET);
					}else if(dtoToWorkOn.getType() == ContactDetailType.POSTAL_ADDRESS){
						currentSuburbList  = getController().findSuburb(SuburbSearchType.SUBURB_NAME, input,AddressSearchType.BOX);
					}
				}else{
					currentSuburbList  = getController().findSuburb(SuburbSearchType.SUBURB_NAME, input);
				}				
				return currentSuburbList.iterator();
			}			
		};	
		suburbField.add(new AjaxFormComponentUpdatingBehavior("change"){
			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				//update postcode and city	
				if(dtoToWorkOn.getSuburb() != null && !dtoToWorkOn.getSuburb().equals("")){					
					//get from list and populate other fields	
					if(currentSuburbList != null){
						for(SuburbSearchDTO search : currentSuburbList){
							String key = search.getSuburbName() + search.getPostalCode();
							if(key.equalsIgnoreCase(dtoToWorkOn.getSuburb())){
								updateSurbfields(search,target);												
								break;
							}
						}
					}
				}							
			}			
		});
		
		suburbField.setLabel(new Model("Suburb"));	
//		suburbField.setRequired(true);
		suburbField.add(new AttributeModifier("size","30"));
		suburbField.add(new AttributeModifier("autocomplete", "off"));
//		suburbField.add(StringValidator.maximumLength(
//				(dtoToWorkOn.getType() == ContactDetailType.PHYSICAL_ADDRESS) ? 34 : 35));
		HelperPanel panel = createPageField(id,"Suburb", suburbField, new EditStateType[]{EditStateType.ADD,EditStateType.MODIFY});
		panel.setOutputMarkupId(true);
		panel.setOutputMarkupPlaceholderTag(true);		
		return panel;		
	}	
	
	/**
	 * Update the fields using the search data
	 * @param values
	 * @param target
	 */
	private void updateSurbfields(SuburbSearchDTO values, AjaxRequestTarget target){
		if(values != null){			
			dtoToWorkOn.setCity(values.getCity());
			dtoToWorkOn.setPostCode(values.getPostalCode());
			dtoToWorkOn.setSuburb(values.getSuburbName());
//			update the fields with ajax
			target.add(postCodePanel);
			target.add(suburbPanel);
			target.add(cityPanel);	
		}		
	}
}
