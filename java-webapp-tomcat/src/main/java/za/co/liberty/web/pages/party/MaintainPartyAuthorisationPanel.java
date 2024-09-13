package za.co.liberty.web.pages.party;

import java.util.ArrayList;
import java.util.List;

import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.apache.wicket.markup.html.panel.Panel;

import za.co.liberty.business.party.IPartyManagement;
import za.co.liberty.dto.gui.request.ViewRequestModelDTO;
import za.co.liberty.dto.party.EmployeeDTO;
import za.co.liberty.dto.party.PartyDTO;
import za.co.liberty.dto.party.PartyDetailsRequestConfiguration;
import za.co.liberty.dto.party.PartyRoleDTO;
import za.co.liberty.dto.party.PartyRolesRequestConfiguration;
import za.co.liberty.dto.party.PersonDTO;
import za.co.liberty.dto.party.contactdetail.ContactPreferenceWrapperDTO;
import za.co.liberty.dto.party.medicalaid.MedicalAidDetailDTO;
import za.co.liberty.dto.party.taxdetails.TaxDetailsDTO;
import za.co.liberty.exceptions.data.DataNotFoundException;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.exceptions.security.TabAccessException;
import za.co.liberty.helpers.lookup.ServiceLocator;
import za.co.liberty.interfaces.agreements.requests.RequestKindType;
import za.co.liberty.interfaces.party.PartyRoleType;
import za.co.liberty.interfaces.party.PartyType;
import za.co.liberty.web.pages.contactdetail.ContactDetailsPanel;
import za.co.liberty.web.pages.party.model.MaintainPartyPageModel;
import za.co.liberty.web.pages.party.model.MedicalAidDetailsPanelModel;
import za.co.liberty.web.pages.request.BaseRequestViewAndAuthorisePanel;
import za.co.liberty.web.pages.taxdetails.TaxDetailsPanel;
import za.co.liberty.web.pages.taxdetails.model.TaxDetailsPanelModel;

/**
 * This panel is used to view & authorise requests relating to the 
 * {@link MaintainPartyPage}
 * 
 * @author JZB0608 - 15 Feb 2010
 *
 */
public class MaintainPartyAuthorisationPanel extends BaseRequestViewAndAuthorisePanel {
	
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(MaintainPartyAuthorisationPanel.class);
	
	private transient IPartyManagement partyManagement;
	
	/**
	 * Default constructor 
	 * 
	 * @param id
	 * @param pageModel
	 * @param editState
	 * @throws TabAccessException 
	 */
	public MaintainPartyAuthorisationPanel(String id, 
			ViewRequestModelDTO viewRequestPageModel) {
		super(id, viewRequestPageModel);	
	}

	/**
	 * Initialise the panels required.
	 * 
	 */
	@Override
	public List<Panel> createPanels(String id, Object imageObject) {
		List<RequestKindType> requestKindList = getPageModel().getRequestKindList();
		logger.debug("Valid request kinds:"+requestKindList);
			
		MaintainPartyPageModel model = initialiseModel(imageObject,requestKindList);
		
		List<Panel> panelList = new ArrayList<Panel>();
		
		// Process Party Details
		if (requestKindList.contains(RequestKindType.MaintainPartyDetails)
				|| requestKindList.contains(RequestKindType.MaintainPartyWithApproval)
						|| requestKindList.contains(RequestKindType.MaintainOtherSystemAccess)) {
			if (model.getPartyDTO() instanceof PersonDTO) {
				Panel panel = new PersonDetailsPanel(id, model, getEditState(),null);
				panelList.add(panel);
			} else {
				Panel panel = new OrganisationDetailsPanel(id, model, getEditState(),null);
				panelList.add(panel);
			}
		}
		
		// Process contact details
		if (requestKindList.contains(RequestKindType.MaintainContactDetails) 
				|| requestKindList.contains(RequestKindType.MaintainSecureContactDetails)) {
			Panel panel = new ContactDetailsPanel(id,
					model.getPartyDTO().getContactPreferences().getContactPreferences(), 
					model.getPartyDTO().getCommunicationPreferences(),
					getEditState(), null,false,null, false);
			panelList.add(panel);
		}
		
		//Process Banking Details
		if (requestKindList.contains(RequestKindType.MaintainPaymentDetails)) {
			Panel panel = new BankingDetailsPanel(id, model, getEditState(),null,null);
			panelList.add(panel);}
		
		if (requestKindList.contains(RequestKindType.MaintainPayrollTaxDetails)) {
			Panel panel = new TaxDetailsPanel(id, model, getEditState(),null,null);
			panelList.add(panel);}
		
		if (requestKindList.contains(RequestKindType.MaintainMedicalAidDetails)) {			
			Panel panel = new MedicalAidDetailsPanel(id,getEditState(),model.getMedicalAidDetailsPanelModel(),null);
			panelList.add(panel);
		}
		
		return panelList;
	}
	
	
	/**
	 * Initialise the model
	 * 
	 * @param currentImage
	 * @return
	 */
	protected MaintainPartyPageModel initialiseModel(Object imageObject,List<RequestKindType> requestKindList) {
		MaintainPartyPageModel model = new MaintainPartyPageModel();
		
		if(imageObject instanceof PartyDTO){
			PartyDTO obj = (PartyDTO) imageObject;		
			model.setPartyDTO(obj);
			model.setSelectedItem(obj);
		}else if(imageObject instanceof PartyDetailsRequestConfiguration){
			//check the requestKinds
			boolean partyIncluded = (requestKindList.contains(RequestKindType.MaintainPartyDetails)
					|| requestKindList.contains(RequestKindType.MaintainPartyWithApproval));
			boolean otherSystemAccessRequest = (requestKindList.contains(RequestKindType.MaintainOtherSystemAccess));				
			PartyDetailsRequestConfiguration config = (PartyDetailsRequestConfiguration)imageObject;
			PartyDTO obj = config.getPartyData();			
			if(!partyIncluded && otherSystemAccessRequest 
					&& config.getPartyRolesRequestDetails() != null 
					&& config.getPartyRolesRequestDetails().getOtherPartyToPartyRoles() != null
					&& config.getPartyRolesRequestDetails().getOtherPartyToPartyRoles().size() > 0){
				//get party data from DB
				try {
					Long oid = (obj == null) ? config.getPartyRolesRequestDetails().getOtherPartyToPartyRoles().get(0).getMainRolePlayerID() : obj.getOid();
					if(oid != null){
						obj = getPartyManagement().getPartyDTOWithObjectOid(oid);
					}					
				}catch (DataNotFoundException e) {
					//leave, will just use the data on the request
				}
			}					
			if(obj instanceof EmployeeDTO && otherSystemAccessRequest){
			  PartyRolesRequestConfiguration rolesConfig = config.getPartyRolesRequestDetails();
			  if(rolesConfig.getOtherPartyToPartyRoles() != null){
				  for(PartyRoleDTO role : rolesConfig.getOtherPartyToPartyRoles()){
					  if(role.getPartyRoleType() == PartyRoleType.WEALTHCONNECTIONUSER){
						  ((EmployeeDTO)obj).setWealthConnectionRole(role);						  
					  }else if(role.getPartyRoleType() == PartyRoleType.WEALTHCONNECTIONTESTUSER){
						  ((EmployeeDTO)obj).setWealthConnectionTestRole(role);						  
					  }
				  }
			  }			 
			}
			if(config.getContactPreferences() != null){
				if(obj == null){
					obj = new PartyDTO();
				}
				obj.setContactPreferences(config.getContactPreferences());
			}
			if(config.getBankingDetailsDTO() != null){
				if(obj == null){
					obj = new PartyDTO();
				}
				obj.setBankingDetailsDTO(config.getBankingDetailsDTO());
			}
			model.setPartyDTO(obj);
			model.setSelectedItem(obj);
		}else if(imageObject instanceof ContactPreferenceWrapperDTO){
			ContactPreferenceWrapperDTO obj = (ContactPreferenceWrapperDTO) imageObject;		
			PartyDTO party = new PartyDTO();
			party.setContactPreferences(obj);
			model.setPartyDTO(party);
			model.setSelectedItem(party);
		}else if(imageObject instanceof TaxDetailsDTO){
			TaxDetailsDTO obj = (TaxDetailsDTO) imageObject;	
			PartyDTO party = new PartyDTO();
			TaxDetailsPanelModel detailsPanelModel = new TaxDetailsPanelModel();
			party.setOid(obj.getPartyOID());
			detailsPanelModel.setTaxDetailsDTO(obj);
			model.setTaxDetailsPanelModel(detailsPanelModel);
			model.setPartyDTO(party);
			model.setSelectedItem(party);			
		}else if(imageObject instanceof MedicalAidDetailDTO){
			MedicalAidDetailDTO obj = (MedicalAidDetailDTO) imageObject;
			MedicalAidDetailsPanelModel panelModel = new MedicalAidDetailsPanelModel();
			panelModel.setCurrentPartyID(obj.getPartyoid());
			panelModel.setMedicalAidDetail(obj);
			panelModel.setCurrentPartyType(PartyType.PERSON.getType());//will always be a person
			model.setMedicalAidDetailsPanelModel(panelModel);			
		}		
		return model;
	}
	
	
	/**
	 * Get the party Management bean
	 * @return
	 */
	private IPartyManagement getPartyManagement(){
		 if(partyManagement == null){
			 try {
				partyManagement = ServiceLocator.lookupService(IPartyManagement.class);
			} catch (NamingException e) {
				throw new CommunicationException(e);
			}
		 }
		 return partyManagement;
	}

}