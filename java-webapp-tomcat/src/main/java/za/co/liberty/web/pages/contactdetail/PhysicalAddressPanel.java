package za.co.liberty.web.pages.contactdetail;

import java.util.ArrayList;

import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.TextField;

import za.co.liberty.dto.party.contactdetail.PhysicalAddressDTO;
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
public class PhysicalAddressPanel extends AddressBasePanel {		
	
	private ContactDetailsPanelModel panelModel;
	
	private PhysicalAddressDTO dtoToWorkOn;

	private static final long serialVersionUID = 1L;


	/**
	 * @param arg0
	 */
	public PhysicalAddressPanel(String id, ContactDetailsPanelModel panelModel,PhysicalAddressDTO dtoToWorkOn,
			EditStateType editState) {
		super(id,panelModel,dtoToWorkOn,editState);			
		this.dtoToWorkOn = dtoToWorkOn;
		if(panelModel != null){
			this.panelModel = panelModel;
		}else{
			panelModel = new ContactDetailsPanelModel(null, null);
		}
		if(dtoToWorkOn == null){
			dtoToWorkOn = new PhysicalAddressDTO();
			error("Please provide a physical address for the physical address panel to function correctly");
		}			
		init();
	}
	
	
	/**
	 * Initialize the panel
	 *
	 */
	@SuppressWarnings("unchecked")
	private void init(){
		//remove secure from address
		ArrayList<UsageType> usages = new ArrayList<UsageType>(panelModel.getAllowedUsages());
		usages.remove(UsageType.SECURE);
		add(this.createDropdownField(dtoToWorkOn, "Usage", "usage", usages,  new ChoiceRenderer("typeName", "typeID"), null, true, new EditStateType[]{EditStateType.ADD,EditStateType.MODIFY}));
		add(this.createComponent(dtoToWorkOn, "Unit Number", "unitNumber", ComponentType.TEXTFIELD, false,false));
		add(this.createComponent(dtoToWorkOn, "Floor Number", "floorNumber", ComponentType.TEXTFIELD, false,false));
		
		add(this.createComponent(dtoToWorkOn, "Building Name", "buildingName", ComponentType.TEXTFIELD, false,false));
		add(this.createComponent(dtoToWorkOn, "House Number", "houseNumber", ComponentType.TEXTFIELD, false,false));
		add(this.createComponent(dtoToWorkOn, "Is Postal", "isPostal", ComponentType.CHECKBOX, false,false));
		HelperPanel street = this.createComponent(dtoToWorkOn, "Street Name", "streetName", ComponentType.TEXTFIELD, false,false);
		if(street.getEnclosedObject() instanceof TextField){
//			street.getEnclosedObject().add(new SimpleAttributeModifier("size","30"));
//			((TextField)street.getEnclosedObject()).add(StringValidator.maximumLength(
//					(dtoToWorkOn.getType() == ContactDetailType.PHYSICAL_ADDRESS) ? 25 : 35));
		}
		add(street);
		}	
}
