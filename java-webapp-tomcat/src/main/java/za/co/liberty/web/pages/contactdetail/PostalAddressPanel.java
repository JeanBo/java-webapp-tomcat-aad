package za.co.liberty.web.pages.contactdetail;

import java.util.ArrayList;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.model.Model;

import za.co.liberty.dto.party.contactdetail.PostalAddressDTO;
import za.co.liberty.dto.party.contactdetail.type.UsageType;
import za.co.liberty.web.data.enums.ComponentType;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.pages.contactdetail.model.ContactDetailsPanelModel;
import za.co.liberty.web.pages.panels.HelperPanel;

/**
 * Contact details panel capturing all the contact details of a party
 * 
 * @author DZS2610
 * 
 */
public class PostalAddressPanel extends AddressBasePanel {		
	
	private ContactDetailsPanelModel panelModel;
	
	private PostalAddressDTO dtoToWorkOn;	

	private static final long serialVersionUID = 1L;	
	
	private Component addressComp;
	private Label addressName;

	/**
	 * @param arg0
	 */
	public PostalAddressPanel(String id, ContactDetailsPanelModel panelModel,PostalAddressDTO dtoToWorkOn,
			EditStateType editState) {
		super(id,panelModel,dtoToWorkOn,editState);			
		this.dtoToWorkOn = dtoToWorkOn;
		if(panelModel != null){
			this.panelModel = panelModel;
		}else{
			panelModel = new ContactDetailsPanelModel(null, null);
		}
		if(dtoToWorkOn == null){
			dtoToWorkOn = new PostalAddressDTO();
			error("Please provide a postal address for the postal address panel to function correctly");
		}
		init();
	}
	
	
	/**
	 * Initialize the panel
	 *
	 */
	private void init(){
		//remove secure from address
		ArrayList<UsageType> usages = new ArrayList<UsageType>(panelModel.getAllowedUsages());
		usages.remove(UsageType.SECURE);
		add(this.createDropdownField(dtoToWorkOn, "Usage", "usage", usages,  new ChoiceRenderer("typeName", "typeID"), null, true, new EditStateType[]{EditStateType.ADD,EditStateType.MODIFY}));
		HelperPanel boxTypePanel = this.createDropdownField(dtoToWorkOn, "Box Type", "boxType", panelModel.getPostalTypes(),  new ChoiceRenderer("name", "key"), null, true, new EditStateType[]{EditStateType.ADD,EditStateType.MODIFY});
		Component boxTypeComp = boxTypePanel.getEnclosedObject();
		if(boxTypeComp instanceof DropDownChoice){
			//add ajax abilities
			boxTypeComp.add(new AjaxFormComponentUpdatingBehavior("change"){				
				private static final long serialVersionUID = 1L;
				@Override
				protected void onUpdate(AjaxRequestTarget target) {
//					alter dropdown to change the properties of the field, enabled and required
					setAddressLineBasedOnData(target);					
				}				
			});
		}
		add(boxTypePanel);
		add(this.createComponent(dtoToWorkOn, "Box Number", "boxNumber", ComponentType.TEXTFIELD, true,false));
		
		addressName = new Label("addressLineName", new Model("Address *"));
		addressName.setOutputMarkupId(true);
		addressName.setOutputMarkupPlaceholderTag(true);
		add(addressName);
		
		add(createAddressLine());
	}	
	
	
	/**
	 * Create the addressLine component
	 * @return
	 */
	private Component createAddressLine(){		
		HelperPanel panel = this.createComponent(dtoToWorkOn, "Address Line", "addressLine", ComponentType.TEXTFIELD, true,false);
		addressComp = panel.getEnclosedObject();
		addressComp.setOutputMarkupId(true);
		addressComp.setOutputMarkupPlaceholderTag(true);
		setAddressLineBasedOnData(null);
		return panel;
	}
	
	/**
	 * Will set the address line componenet attributes based on the current data selected
	 *
	 */
	private void setAddressLineBasedOnData(AjaxRequestTarget target){
		if(dtoToWorkOn.getBoxType() != null && dtoToWorkOn.getBoxType().getName() != null && dtoToWorkOn.getBoxType().getName().equalsIgnoreCase("POSTNET SUITES")){
			addressComp.setVisible(true);
			addressName.setVisible(true);
//			if(addressComp instanceof TextField){
//				((TextField)addressComp).setRequired(true);
//			}
		}else{	
			dtoToWorkOn.setAddressLine(null);
			addressComp.setVisible(false);
			addressName.setVisible(false);
//			if(addressComp instanceof TextField){
//				((TextField)addressComp).setRequired(false);
//			}
		}
		if(target != null){
			target.add(addressComp);
			target.add(addressName);
		}
	}
}
