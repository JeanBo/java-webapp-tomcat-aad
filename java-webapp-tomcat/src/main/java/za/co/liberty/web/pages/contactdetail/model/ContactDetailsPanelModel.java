package za.co.liberty.web.pages.contactdetail.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import za.co.liberty.business.guicontrollers.contactdetail.IContactDetailsGUIController;
import za.co.liberty.dto.databaseenum.ElectronicCommunicationTypeDBEnumDTO;
import za.co.liberty.dto.databaseenum.PostalAddressTypeDBEnumDTO;
import za.co.liberty.dto.party.contactdetail.AddressDTO;
import za.co.liberty.dto.party.contactdetail.CommunicationPreferenceDTO;
import za.co.liberty.dto.party.contactdetail.ContactDetailDTO;
import za.co.liberty.dto.party.contactdetail.ContactPreferenceDTO;
import za.co.liberty.dto.party.contactdetail.TelDetailDTO;
import za.co.liberty.dto.party.contactdetail.WebAddressDTO;
import za.co.liberty.dto.party.contactdetail.type.UsageType;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.helpers.config.EJBReferencesHelper;
import za.co.liberty.helpers.lookup.ServiceLocator;
import za.co.liberty.web.pages.interfaces.IPanelModel;

/**
 * Panel model keeping all contact details info for the panel<br/>
 * <strong>NOTE: the collection sent in through the constructor is only added to when getPanelData() is called<strong>
 * 
 * @author DZS2610
 *
 */

public class ContactDetailsPanelModel implements IPanelModel<List<ContactPreferenceDTO>> {
	
	private static final long serialVersionUID = 1L;

	private List<ContactPreferenceDTO> contactPreferences = new ArrayList<ContactPreferenceDTO>();
	
	private List<AddressDTO> addresses = new ArrayList<AddressDTO>();
	
	private List<ContactDetailDTO> telDetails = new ArrayList<ContactDetailDTO>();
	
	private List<CommunicationPreferenceDTO> commPreferences = new ArrayList<CommunicationPreferenceDTO>();
	
	HashMap<UsageType,ContactPreferenceDTO> prefs = new HashMap<UsageType,ContactPreferenceDTO>();

	
	/**
	 * List data
	 */
	private List<PostalAddressTypeDBEnumDTO> postalTypes;
	
	private List<ElectronicCommunicationTypeDBEnumDTO> telTypes;
	
	private List<UsageType> allowedUsages = new ArrayList<UsageType>();
	
	public List<ElectronicCommunicationTypeDBEnumDTO> getTelTypes() {
		return telTypes;
	}

	public void setTelTypes(List<ElectronicCommunicationTypeDBEnumDTO> telTypes) {
		this.telTypes = telTypes;
	}

	public List<PostalAddressTypeDBEnumDTO> getPostalTypes() {
		return postalTypes;
	}

	public void setPostalTypes(List<PostalAddressTypeDBEnumDTO> postalTypes) {
		this.postalTypes = postalTypes;
	}

	/**
	 * DEfault constructor requires the initial list to work with
	 * @param initialContactDetailsList
	 */
	public ContactDetailsPanelModel(List<ContactPreferenceDTO> initialContactPreferenceList, List<CommunicationPreferenceDTO> commPrefs){
		if(initialContactPreferenceList != null){
			contactPreferences = initialContactPreferenceList;
			for(ContactPreferenceDTO pref : contactPreferences){
				if(pref.getContactDetails() != null && pref.getContactDetails().size() != 0){
					//we ignore prefs with no detail
					prefs.put(pref.getType(), pref);
				}
			}
		}
		/*CommunicationPreferenceDTO dto = new CommunicationPreferenceDTO();
		dto.setBusinessProcessType(BusinessProcessType.UNDERWRITING);
		dto.setOptedInEmail(Boolean.FALSE);
		dto.setOptedInSMS(Boolean.TRUE)*/;
		commPreferences = commPrefs;
		sortListIntoSublists();
	}
	
	/**
	 * Get the list of modified contact details from the panel 
	 */
	public List<ContactPreferenceDTO> getPanelData() {
		sortContactPointsIntoPreferences();
		return contactPreferences;
	}
	
	/**
	 * Goes through the list of contact points and checks if any have moved, if so it moves them to the correct contact preference
	 *
	 */
	public void sortContactPointsIntoPreferences(){
//		run through all data and sort into correct lists
		//this is due to the fact that the user might have changed the pref type in the object then it must go into another pref list
		//erase all previous contact details lists	
		//null pref for leftovers 
		ContactPreferenceDTO nullPref = new ContactPreferenceDTO();
		nullPref.setContactDetails(new ArrayList<ContactDetailDTO>());
		
		
		for(UsageType type : prefs.keySet()){
			ContactPreferenceDTO pref = prefs.get(type);
			pref.setContactDetails(new ArrayList<ContactDetailDTO>());
		}	
		//add all addresses
		for(AddressDTO address : addresses){			
			if(address.getUsage() != null){
				ContactPreferenceDTO pref = prefs.get(address.getUsage());
				if(pref == null){
					pref = new ContactPreferenceDTO();
					pref.setContactDetails(new ArrayList<ContactDetailDTO>());
					pref.setType(address.getUsage());					
					prefs.put(address.getUsage(), pref);
				}								
				pref.getContactDetails().add(address);
			}else{
				//just add to first pref as it should not pass validation anyway
				nullPref.getContactDetails().add(address);				
			}			
		}
		//add all tel details
		for(ContactDetailDTO tel : telDetails){
			if(tel.getUsage() != null){
			ContactPreferenceDTO pref = prefs.get(tel.getUsage());
			if(pref == null){
				pref = new ContactPreferenceDTO();
				pref.setContactDetails(new ArrayList<ContactDetailDTO>());
				pref.setType(tel.getUsage());					
				prefs.put(tel.getUsage(), pref);
			}								
			pref.getContactDetails().add(tel);
			}else{
				//just add to first pref as it should not pass validation anyway
				nullPref.getContactDetails().add(tel);		
			}
		}
			
		List<ContactPreferenceDTO> newList = new ArrayList<ContactPreferenceDTO>();
		for(UsageType type : prefs.keySet()){
			newList.add(prefs.get(type));
		}
		//lastly add nulpref if it has values
		if(nullPref.getContactDetails().size() != 0){
			newList.add(nullPref);
		}	
		contactPreferences.clear();
		contactPreferences.addAll(newList);
	}
	
	/**
	 * Will split off addresses and telephone details into separate lists
	 *
	 */
	private void sortListIntoSublists(){
		telDetails.clear();
		addresses.clear();
		for(ContactPreferenceDTO pref : contactPreferences){		
			for(ContactDetailDTO detail : pref.getContactDetails()){
				if(detail instanceof TelDetailDTO || detail instanceof WebAddressDTO){
					telDetails.add((ContactDetailDTO) detail);
				}else if(detail instanceof AddressDTO){
					addresses.add((AddressDTO) detail);
				}
			}
		}
	}

	public List<AddressDTO> getAddresses() {
		return Collections.unmodifiableList(addresses);
	}

	public List<ContactDetailDTO> getTelDetails() {
		return Collections.unmodifiableList(telDetails);
	}
	
	public List<CommunicationPreferenceDTO> getCommunicationPreferenceDetails() {
		if (commPreferences != null)
			return Collections.unmodifiableList(commPreferences);
		else
			return new ArrayList<CommunicationPreferenceDTO>();
	}
	
	/**
	 * Add a new address if this address passed in does not appear in the list
	 * @param newAddress
	 */
	public void addAddress(AddressDTO newAddress){
		boolean found = false;
		if(newAddress != null){
			for(AddressDTO address : addresses){			
				if(newAddress == address){
					found = true;
					break;
				}
			}
			//only add if not in list already
			if(!found){				
				addresses.add(0,newAddress);
				//contactDetails.add(newAddress);
			}
		}
	}
	
	/**
	 * Remove an address
	 * @param addressRemoval
	 */
	public void removeAddress(AddressDTO addressRemoval){
		addresses.remove(addressRemoval);
		//contactDetails.remove(addressRemoval);
	}
	
	/**
	 * Add a new tel detail
	 * @param newAddress
	 */
	public void addTelDetail(ContactDetailDTO newTel){
//		add to the top of the list so it displays first on the grid
		addTelDetail(newTel,0);
	}
	
	/**
	 * Add a new tel detail
	 * @param newAddress
	 */
	public void addTelDetail(ContactDetailDTO newTel, int index){
		boolean found = false;
		if(newTel != null){
			for(ContactDetailDTO tel : telDetails){			
				if(newTel == tel){
					found = true;
					break;
				}
			}
			//only add if not in list already
			if(!found){				
				telDetails.add(index,newTel);
				//contactDetails.add(newTel);
			}
		}		
	}
	
	/**
	 * Add a new commPref detail
	 * 
	 * @param commPref
	 */
	public void addCommPrefDetail(CommunicationPreferenceDTO commPref){
//		add to the top of the list so it displays first on the grid
		if(commPreferences != null){
			commPreferences.add(commPref);
		}
	}
	
	/**
	 * Replace the old business process detail with the new one
	 * @param oldTel
	 * @param newTel
	 */
	public void replaceCommPref(CommunicationPreferenceDTO oldOption, CommunicationPreferenceDTO newOption){
		if(oldOption == null){
			addCommPrefDetail(newOption);
			return;
		}
		commPreferences.remove(oldOption);
		addCommPrefDetail(newOption);
	}
	
	
	/**
	 * Replace the old telephone detail with the new one
	 * @param oldTel
	 * @param newTel
	 */
	public void replaceTelNumber(ContactDetailDTO oldTel, ContactDetailDTO newTel){
		if(oldTel == null){
			addTelDetail(newTel);
			return;
		}
		int index = telDetails.indexOf(oldTel);
		if(index < 0){
			addTelDetail(newTel);
			return;
		}
		removeTelDetail(oldTel);
		addTelDetail(newTel,index);
	}
	
	/**
	 * Remove tel detail
	 * @param addressRemoval
	 */
	public void removeTelDetail(ContactDetailDTO telRemoval){
		telDetails.remove(telRemoval);
		//contactDetails.remove(telRemoval);
	}	

	/**
	 * Remove commPref detail
	 * @param removal
	 */
	public void removeCommPrefDetail(CommunicationPreferenceDTO removal){
		commPreferences.remove(removal);
		//contactDetails.remove(telRemoval);
	}
	
	public void setTelDetails(List<ContactDetailDTO> telDetails) {
		this.telDetails = telDetails;
	}	
	
	/**
	 * get a new ContactDetailsGUIController reference
	 * @return
	 * @throws NamingException 
	 */
	public IContactDetailsGUIController getContactDetailsController(){
		try {
			return ServiceLocator.lookupService(IContactDetailsGUIController.class);
//			return (IContactDetailsGUIController) new InitialContext().lookup(EJBReferencesHelper.CONTACT_DETAILS_GUI_CONTROLLER);
		} catch (NamingException e) {
			throw new CommunicationException(e);
		}
	}

	public List<UsageType> getAllowedUsages() {
		return allowedUsages;
	}

	public void setAllowedUsages(List<UsageType> allowedUsages) {
		this.allowedUsages = allowedUsages;
	}

}
