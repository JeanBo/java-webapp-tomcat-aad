package za.co.liberty.web.pages.party;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.Form;

import za.co.liberty.dto.party.EmployeeDTO;
import za.co.liberty.dto.party.OrganisationDTO;
import za.co.liberty.dto.party.PartyDTO;
import za.co.liberty.dto.party.contactdetail.ContactPreferenceDTO;
import za.co.liberty.dto.party.contactdetail.ContactPreferenceWrapperDTO;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.pages.interfaces.ISecurityPanel;
import za.co.liberty.web.pages.interfaces.IStatefullComponent;
import za.co.liberty.web.pages.panels.BasePanel;
import za.co.liberty.web.pages.party.model.MaintainPartyPageModel;

/**
 * Selection of the type of party, only used when adding a new party
 * @author DZS2610
 *
 */
public class PartyTypePanel extends BasePanel implements
		IStatefullComponent, ISecurityPanel {	
	private static final long serialVersionUID = 1L;

	private TypeForm form;

	MaintainPartyPageModel pageModel;
	
	
	/**
	 * @param arg0
	 */
	public PartyTypePanel(String id,
			MaintainPartyPageModel pageModel, EditStateType editState) {
		super(id, editState);
		this.pageModel = pageModel;		
		add(form = new TypeForm("typeForm",pageModel));

	}

	private class TypeForm extends Form {
		
		private static final long serialVersionUID = 1L;
		private TypeForm(String id,MaintainPartyPageModel pageModel) {		
			super(id);
			List<PartyDTO> choices = new ArrayList<PartyDTO>(2);			
			PartyDTO modelParty = pageModel.getPartyDTO();
			
			//add employee selection
			EmployeeDTO emp = null;
			if(modelParty instanceof EmployeeDTO){
				emp = (EmployeeDTO) modelParty;
			}else{
				emp = new EmployeeDTO();
				emp.setEffectiveFrom(new Date());
				//add in the empty contact details list
				ContactPreferenceWrapperDTO contactDetails = new ContactPreferenceWrapperDTO();
				contactDetails.setContactPreferences(new ArrayList<ContactPreferenceDTO>());				
				emp.setContactPreferences(contactDetails);
			}			
			choices.add(emp);
			
			//add organisation selection
			OrganisationDTO org = null;
			if(modelParty instanceof OrganisationDTO){
				org = (OrganisationDTO) modelParty;
			}else{
				org = new OrganisationDTO();
				org.setEffectiveFrom(new Date());
//				add in the empty contact details list
				ContactPreferenceWrapperDTO contactDetails = new ContactPreferenceWrapperDTO();
				contactDetails.setContactPreferences(new ArrayList<ContactPreferenceDTO>());				
				org.setContactPreferences(contactDetails);
			}			
			choices.add(org);		
			add(createDropdownField(pageModel,"Party Type","partyDTO",choices,new ChoiceRenderer("typeDescription"),null,true, new EditStateType[]{EditStateType.MODIFY,EditStateType.ADD}));
		}									
	}

	public Class getPanelClass() {		
		return PartyTypePanel.class;
	}
}



