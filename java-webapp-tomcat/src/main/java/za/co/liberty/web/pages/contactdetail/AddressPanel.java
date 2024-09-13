package za.co.liberty.web.pages.contactdetail;

import java.util.ArrayList;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.Panel;

import za.co.liberty.dto.party.contactdetail.AddressDTO;
import za.co.liberty.dto.party.contactdetail.PhysicalAddressDTO;
import za.co.liberty.dto.party.contactdetail.PostalAddressDTO;
import za.co.liberty.dto.party.contactdetail.type.ContactDetailType;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.data.pages.IModalMaintenancePageModel;
import za.co.liberty.web.pages.contactdetail.model.ContactDetailsPanelModel;
import za.co.liberty.web.pages.panels.BasePanel;
import za.co.liberty.web.pages.panels.HelperPanel;

/**
 * Base Panel for the Address Types
 * @author DZS2610
 *
 */
public class AddressPanel extends BasePanel {
	
	private AddressDTO objectToUse;
	
	private ContactDetailsPanelModel panelModel;
	
	private IModalMaintenancePageModel<AddressDTO> pageModel;
	
	private Panel addressPanel = null;
	
	/**
	 * Current address type
	 */
	private ContactDetailType type = ContactDetailType.PHYSICAL_ADDRESS;
	
	private static final long serialVersionUID = 1L;

	/**
	 * Default constructor
	 * @param id
	 * @param editState
	 * @param panelModel
	 * @param objectToUse
	 * @param pageModel 
	 */
	public AddressPanel(String id, EditStateType editState,ContactDetailsPanelModel panelModel, AddressDTO objectToUse, 
			IModalMaintenancePageModel<AddressDTO> pageModel) {
		super(id, editState);
		this.objectToUse = objectToUse;
		if(objectToUse == null){
			error("The address sent through was null");
			objectToUse = new PhysicalAddressDTO();
		}
		type = objectToUse.getType();
		this.panelModel = panelModel;
		this.pageModel = pageModel;
		init();
	}	
	
	/**
	 * Initialize page params
	 *
	 */
	private void init(){
		add(createTypeSelection());
		add(getCorrectaddressPanelForObject(objectToUse));
	}
	
	/**
	 * Create the address type selection
	 * @return
	 */
	private HelperPanel createTypeSelection(){
		//Only want the physical address and postal address types
		ArrayList<ContactDetailType> list = new ArrayList<ContactDetailType>(2);
		list.add(ContactDetailType.PHYSICAL_ADDRESS);
		list.add(ContactDetailType.POSTAL_ADDRESS);
		//Arrays.asList(UsageType.values())
		EditStateType[] editstates = new EditStateType[]{EditStateType.ADD,EditStateType.MODIFY};
		if(objectToUse.getOid() != 0){
			//always view on a set address, type can not change
			editstates = new EditStateType[]{};
		}
		HelperPanel dropdown = createDropdownField(this, "Address Type", "type",
				list, new ChoiceRenderer("typeName",
						"typeID"), null, true,editstates);
		
		if(dropdown.getEnclosedObject() instanceof DropDownChoice){
			DropDownChoice actualDropDown = (DropDownChoice) dropdown.getEnclosedObject();
			actualDropDown.add(new AjaxFormComponentUpdatingBehavior("change"){
				private static final long serialVersionUID = 1L;

				@Override
				protected void onUpdate(AjaxRequestTarget target) {
					//as this can only change on add we can change the object based on the type selected
					if(type == ContactDetailType.PHYSICAL_ADDRESS){
						objectToUse = new PhysicalAddressDTO();						
					}else{
						objectToUse = new PostalAddressDTO();
					}		
					pageModel.setSelectedItem(objectToUse);
					target.add(getCorrectaddressPanelForObject(objectToUse));
				}				
			});
		}
		dropdown.setOutputMarkupId(true);
		dropdown.setOutputMarkupPlaceholderTag(true);
		return dropdown;
	}
	
	/**
	 * Return the correct Address panel based on the type
	 * @param dto
	 * @return
	 */
	private Panel getCorrectaddressPanelForObject(AddressDTO dto){
		Panel ret = new EmptyPanel("addressPanel");		
		if(dto != null){
			if(dto.getType() == ContactDetailType.PHYSICAL_ADDRESS){				
				//we swap the panel to the approporiat panel
				ret = new PhysicalAddressPanel("addressPanel",panelModel,(PhysicalAddressDTO)objectToUse,getEditState());
			}else{				
				ret = new PostalAddressPanel("addressPanel",panelModel,(PostalAddressDTO)objectToUse,getEditState());
			}
		}
		ret.setOutputMarkupId(true);
		ret.setOutputMarkupPlaceholderTag(true);
		if(addressPanel != null){
			addressPanel.replaceWith(ret);
		}
		addressPanel = ret;						
		return addressPanel;
	}
	
	/**
	 * Get the dto that is being used on the panel
	 * @return
	 */
	public AddressDTO getAddressObjectOnPanel(){
		return objectToUse;
	}
}
