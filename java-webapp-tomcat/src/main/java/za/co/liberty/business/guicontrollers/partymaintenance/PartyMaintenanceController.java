package za.co.liberty.business.guicontrollers.partymaintenance;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.EJBContext;
import javax.ejb.Stateless;

import org.apache.log4j.Logger;

import za.co.liberty.business.agreement.IAgreementManagement;
import za.co.liberty.business.broadcast.IAgreementIntegrationController;
import za.co.liberty.business.broadcast.IPartyIntegrationController;
import za.co.liberty.business.common.IBusinessUtilitiesBean;
import za.co.liberty.business.party.IPartyManagement;
import za.co.liberty.business.party.validator.IPartyValidator;
import za.co.liberty.business.party.validator.PartyValidator;
import za.co.liberty.business.request.IGuiRequestManagement;
import za.co.liberty.business.request.IRequestManagement;
import za.co.liberty.common.domain.ApplicationContext;
import za.co.liberty.dto.agreement.AgreementRoleDTO;
import za.co.liberty.dto.agreement.maintainagreement.ProvidentFundBeneficiariesDTO;
import za.co.liberty.dto.agreement.maintainagreement.ProvidentFundBeneficiaryDetailsDTO;
import za.co.liberty.dto.agreement.request.RaiseGuiRequestResultDTO;
import za.co.liberty.dto.businesscard.BusinessCardDetailsDTO;
import za.co.liberty.dto.common.IDValueDTO;
import za.co.liberty.dto.common.MonthEndDates;
import za.co.liberty.dto.contracting.ResultPartyDTO;
import za.co.liberty.dto.databaseenum.PartyStatusDBEnumDTO;
import za.co.liberty.dto.party.EmployeeDTO;
import za.co.liberty.dto.party.OrganisationDTO;
import za.co.liberty.dto.party.PartyDTO;
import za.co.liberty.dto.party.PartyDetailsRequestConfiguration;
import za.co.liberty.dto.party.PartyRoleDTO;
import za.co.liberty.dto.party.PartyRolesRequestConfiguration;
import za.co.liberty.dto.party.PersonDTO;
import za.co.liberty.dto.party.bankingdetail.BankBranchDTO;
import za.co.liberty.dto.party.bankingdetail.BankNameDTO;
import za.co.liberty.dto.party.bankingdetail.BankVerificationRulesDTO;
import za.co.liberty.dto.party.bankingdetail.BankingDetailsDTO;
import za.co.liberty.dto.party.bankingdetail.BankingDetailsSearchDTO;
import za.co.liberty.dto.party.bankingdetail.BankingDetailsSource;
import za.co.liberty.dto.party.bankingdetail.BankingVerificationResponseDTO;
import za.co.liberty.dto.party.bankingdetail.MaintainBankingDetailResultDTO;
import za.co.liberty.dto.party.bankingdetail.type.AVSStatusType;
import za.co.liberty.dto.party.bankingdetail.type.AccountHolderRelType;
import za.co.liberty.dto.party.bankingdetail.type.AccountType;
import za.co.liberty.dto.party.bankingdetail.type.ActionType;
import za.co.liberty.dto.party.contactdetail.AddressDTO;
import za.co.liberty.dto.party.contactdetail.CommunicationPreferenceDTO;
import za.co.liberty.dto.party.contactdetail.ContactDetailDTO;
import za.co.liberty.dto.party.contactdetail.ContactPreferenceDTO;
import za.co.liberty.dto.party.contactdetail.ContactPreferenceWrapperDTO;
import za.co.liberty.dto.party.contactdetail.TelDetailDTO;
import za.co.liberty.dto.party.contactdetail.type.BusinessProcessType;
import za.co.liberty.dto.party.contactdetail.type.ContactDetailType;
import za.co.liberty.dto.party.contactdetail.type.UsageType;
import za.co.liberty.dto.party.medicalaid.MedicalAidDetailDTO;
import za.co.liberty.dto.party.taxdetails.BBBEEDTO;
import za.co.liberty.dto.party.taxdetails.DirectivesDTO;
import za.co.liberty.dto.party.taxdetails.LabourBrokersDTO;
import za.co.liberty.dto.party.taxdetails.TaxDetailsDTO;
import za.co.liberty.dto.party.taxdetails.TrustCompDTO;
import za.co.liberty.dto.party.taxdetails.VatDTO;
import za.co.liberty.dto.persistence.party.flow.PartyAQCHistoryFLO;
import za.co.liberty.dto.persistence.party.flow.PartyRoleContextFLO;
import za.co.liberty.dto.rating.DescriptionDTO;
import za.co.liberty.dto.userprofiles.ISessionUserProfile;
import za.co.liberty.exceptions.broadcast.PartyDetailsBroadcastException;
import za.co.liberty.exceptions.data.DataNotFoundException;
import za.co.liberty.exceptions.data.ValidationException;
import za.co.liberty.exceptions.error.request.RequestConfigurationException;
import za.co.liberty.exceptions.error.request.RequestException;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.helpers.converters.ObjectChangeHelper;
import za.co.liberty.helpers.util.ComparatorUtil;
import za.co.liberty.helpers.util.ComparatorUtil.ObjectComparisonDifferences;
import za.co.liberty.helpers.util.DateUtil;
import za.co.liberty.interfaces.agreements.AgreementKindType;
import za.co.liberty.interfaces.agreements.requests.RequestKindType;
import za.co.liberty.interfaces.gui.GuiRequestKindType;
import za.co.liberty.interfaces.party.BankingDetailType;
import za.co.liberty.interfaces.party.PartyRoleType;
import za.co.liberty.persistence.agreement.IAgreementEntityManager;
import za.co.liberty.persistence.agreement.entity.DateRange;
import za.co.liberty.persistence.party.IPartyEntityManager;
import za.co.liberty.persistence.rating.IDescriptionEntityManager;
import za.co.liberty.persistence.rating.IRatingEntityManager;
import za.co.liberty.persistence.rating.entity.Description;
import za.co.liberty.srs.type.SRSType;
import za.co.liberty.srs.util.agreement.TaxBasisConstants;

/**
 * Will contain all controller methods for party maintenance
 * 
 * @author DZS2610 
 * 
 */
@Stateless(name = "PartyMaintenanceController")
public class PartyMaintenanceController implements IPartyMaintenanceController {
	
	private static final Logger logger = Logger.getLogger(PartyMaintenanceController.class);		
	
	/**
	 * This will return the requests to raise and whether the actual object has changed<br/>
	 * If the object has not ched, no requests should be raised
	 * @param party
	 * @param agreementContext
	 * @param sessionUserProfile
	 * @param partyDTOBeforeImage
	 * @param requestKinds
	 * @param agreementAttached
	 * @return
	 * @throws ValidationException The data is still validated to make sure that all is ok be ore raising the requests
	 */
	@SuppressWarnings("deprecation")
	public PartyRequestDetails getPartyRequestDetailsForRequest(PartyDTO party, ISessionUserProfile sessionUserProfile, 
			PartyDTO partyDTOBeforeImage, RequestKindType[] requestKinds, boolean addingAgreement,AgreementKindType agreementKindBeingAdded) throws ValidationException{
//		set the created by for the whole hierarchy	
		party.setCreatedByPartyID(sessionUserProfile.getPartyOid());
		if(requestKinds == null || requestKinds.length == 0){
			requestKinds = new RequestKindType[] {					
 				RequestKindType.MaintainPartyDetails,RequestKindType.MaintainPartyWithApproval,RequestKindType.MaintainContactDetails,RequestKindType.MaintainSecureContactDetails,
 				RequestKindType.MaintainPaymentDetails,RequestKindType.MaintainOrganisationKnownAsName };
		}	
		
		boolean validateContactDetails = false;
		boolean validatePartyDetails = false;
		
		//due to the fact that secure contact details require a different request, I will ask the dto if it has secure
		//if it does I will raise that request
		
		//if ended today or before today then there is no need to validate, no need to validate
		for(RequestKindType type : requestKinds){			
			if(type == RequestKindType.MaintainContactDetails || type == RequestKindType.MaintainSecureContactDetails){
				validateContactDetails = true;
			}else if(type == RequestKindType.MaintainPartyDetails){
				validatePartyDetails = true;
			}
		}			
		
		PartyValidator partyValidator = new PartyValidator();
		if(validatePartyDetails){
			if(!(party.getEffectiveTo() != null && party.getEffectiveTo().compareTo(new Date()) <= 0)){
			//first we validate		
				partyValidator.validate(party,null,validateContactDetails,addingAgreement,agreementKindBeingAdded);
			}else{
				//validate the delete criteria
				//should not have any agreements attached 
				//should not be a manager of a branch
				partyValidator.validateTerminationRequirements(party);	
			}
		}else if(validateContactDetails){
			if(party.getContactPreferences() != null && party
					.getContactPreferences().getContactPreferences() != null){
				partyValidator.validateContactPreferenceDetailsAndAllContactDetails(
						party.getOid(),
						party.getContactPreferences().getContactPreferences(),addingAgreement);
			}
		}	
		
//		Now we check if the DTO has changed, only if it has, raise the request
		boolean objectChanged = false;
		List<ObjectComparisonDifferences> diffs = ComparatorUtil.compareObjects(party, partyDTOBeforeImage,true);
		for(ObjectComparisonDifferences dif : diffs){
			if(!dif.getFieldName().equals("actionType")){
				//ActonType difference should be ignored in BankingDetailsDTO 
				//System.err.println("something changed(" + dif.getTraceString() + ") from " + dif.getFieldOneValue() + " to " + dif.getFieldTwoValue());
				objectChanged = true;				
				//great, continue
				break;
			}

		}
				
//		 Setup the request kinds that are required
		requestKinds = doRequestKindCheckonData(requestKinds, party,partyDTOBeforeImage, diffs,addingAgreement);
		
		 //update the language preference chosen to all contact details, 
  	  	//REMOVE THE LINE BELOW WHEN LANGUAGE PREF IS DONE PER CONTACT PREF
		if(party instanceof PersonDTO){
  		  	((PersonDTO)party).updateContactPrefsWithLanguagPref();
  	  	}		
		
		PartyRequestDetails response = new PartyRequestDetails();
		response.objectModified = objectChanged;
		response.requestKindsToRaise = requestKinds;	
		return response;
	}
	
	/**
	 * Store a party, will raise a request to store a new party
	 * @throws ValidationException 
	 */
	public long storeParty(PartyDTO party,Long agreementContext, ISessionUserProfile sessionUserProfile, 
			PartyDTO partyDTOBeforeImage, RequestKindType[] requestKinds, boolean agreementAttached, AgreementKindType agreementKindBeingAdded) throws ValidationException {
		
		logger.info("Storing Party " + party);
		if(party!=null && party instanceof PersonDTO) {
			PersonDTO d = (PersonDTO) party;
			logger.info("  -Person " +d.getName() + " " + d.getFirstName());
		} else if (party!=null && party instanceof OrganisationDTO) {
			OrganisationDTO org = (OrganisationDTO) party;
			logger.info("  -Organisation " +org.getRegistrationNumber() + " " + org.getBusinessName());
		}
		
		PartyRequestDetails detail = getPartyRequestDetailsForRequest(party,sessionUserProfile, 
				partyDTOBeforeImage, requestKinds, agreementAttached,agreementKindBeingAdded);		
		requestKinds = detail.getRequestKindsToRaise();
		if(!detail.isObjectModified()){
			throw new ValidationException("Please only click save if you have changed something otherwise click cancel");
		}		
		
		//now we raise the requests
//		try {     		
     		// Raise guiRequest
			if(requestKinds != null && requestKinds.length > 0 && detail.isObjectModified()){				
				Object beforeRequestImage =  ((partyDTOBeforeImage != null) ? new PartyDetailsRequestConfiguration(partyDTOBeforeImage) : null);
				//if banking details request only then check 
				boolean bankingDetailsRequest = false;				
				for(RequestKindType requestkind : requestKinds){
					if(requestkind == RequestKindType.MaintainPaymentDetails){
						bankingDetailsRequest = true;
						break;
					}
				}
				if(beforeRequestImage != null && bankingDetailsRequest
						&& requestKinds.length  == 1 && ((PartyDetailsRequestConfiguration) beforeRequestImage).getBankingDetailsDTO() == null){					
						beforeRequestImage = null;				
				}
				//Fix for existing Prod Issue duplicate person creation - for Request kinds[MaintainPartyDetails,MaintainContactDetails] FOR loop runs twice creating duplciates-SBS0510-JML
				List<RequestKindType> requestKindList = Arrays.asList( requestKinds);
				
				boolean isBusinessCardDetails = requestKindList.stream().anyMatch(reqkind-> reqkind == RequestKindType.MaintainBusinessCardDetails);
								
	     		RaiseGuiRequestResultDTO response = null;
	     		
	     		if (party instanceof PersonDTO) {
	     		System.out.println("#JB - person = title=" + ((PersonDTO) party).getTitle()
	     				+ "  name=" + ((PersonDTO) party).getName() );
	     		} else {
	     			System.out.println("#JB - organisation = " + ((OrganisationDTO) party).getBusinessName() );
	     		}
	     		//TODO 
	     		return 123;
//	     		
//	     		if(isBusinessCardDetails) {
//	     			BusinessCardDetailsDTO businessCardDetailsDTO = new BusinessCardDetailsDTO();
//					businessCardDetailsDTO.setParty(party);
//					beforeRequestImage = new BusinessCardDetailsDTO();
//					((BusinessCardDetailsDTO) beforeRequestImage).setParty(partyDTOBeforeImage);
//					
//					response = guiRequestManager.raiseGuiRequest(new ApplicationContext(), 
//		 					sessionUserProfile, 
//		 					agreementContext,
//		 					party.getOid(),
//		 					GuiRequestKindType.MaintainParty,
//		 					detail.getRequestKindsToRaise(), 
//		 					businessCardDetailsDTO, 
//		 					beforeRequestImage);
//	     		} else {
//	     			response = guiRequestManager.raiseGuiRequest(new ApplicationContext(), 
//		 					sessionUserProfile, 
//		 					agreementContext,
//		 					party.getOid(),
//		 					GuiRequestKindType.MaintainParty,
//		 					detail.getRequestKindsToRaise(), 
//		 					 new PartyDetailsRequestConfiguration(party), 
//		 					beforeRequestImage);
//	     		}	     		
//	     		
//	     		return response.getTargetPartyOid();   
			}
//		} catch (RequestConfigurationException e) {	
//			logger.info("Error occured while storing party",e);
//			ejbContext.setRollbackOnly();
//			throw new ValidationException(Arrays.asList(new String[]{e.getMessage()}));
//		} catch (RequestException e) {
//			logger.info("Error occured while storing party",e);
//			ejbContext.setRollbackOnly();
//			throw new ValidationException(Arrays.asList(new String[]{e.getMessage()}));
//		}
		return party.getOid();
	}
	
	/**
	 * do a check on the request kinds and only raise those that need to be raised 
	 * @param partyRoles
	 * @param partyRolesBeforeImage
	 * @param requestkinds
	 * @return
	 */
	private RequestKindType[] getRequestToRaise(PartyRolesRequestConfiguration newRoles,PartyRolesRequestConfiguration beforeImage,RequestKindType[] requestkinds){
		ArrayList<RequestKindType> ret = new ArrayList<RequestKindType>();
		for(RequestKindType kind : requestkinds){
			if(kind == RequestKindType.MaintainLinkedAssistants){
				//get all isAssisitedByRoles and check for changes
				List<ObjectComparisonDifferences> secDiffs = ComparatorUtil.compareObjects(newRoles.getIsAssistedByPartyRoles(), beforeImage.getIsAssistedByPartyRoles(),true);
				if(secDiffs.size() != 0){
					ret.add(kind);
				}
			}
			if(kind == RequestKindType.MaintainPartnerships){
				//get all partnerToRoles and check for changes
				List<ObjectComparisonDifferences> secDiffs1 = ComparatorUtil.compareObjects(newRoles.getPartnerToRoles(), beforeImage.getPartnerToRoles(),true);
				if(secDiffs1.size() != 0){
					ret.add(kind);
				}
			}
			if (kind == RequestKindType.MaintainPartyRoleHierarchy)
			{
				List<ObjectComparisonDifferences> secDiffs2 = ComparatorUtil.compareObjects(newRoles.getDistributionGroupAdminRoles(), beforeImage.getDistributionGroupAdminRoles(),true);
				if(secDiffs2.size() != 0){
					ret.add(kind);
				}
				List<ObjectComparisonDifferences> secDiffs3 = ComparatorUtil.compareObjects(newRoles.getDistributionGroupManagerRoles(), beforeImage.getDistributionGroupManagerRoles(),true);
				if(secDiffs3.size() != 0){
					ret.add(kind);
				}
			}
			
		}	
		return ret.toArray(new RequestKindType[0]);
	}
	
	/**
	 * Raise the party hierarchy request
	 * @param partyRoles
	 * @return
	 * @throws ValidationException 
	 */
	public void raisePartyhierarchyRequest(List<PartyRoleDTO> partyRoles,List<PartyRoleDTO> partyRolesBeforeImage,
			ISessionUserProfile sessionUserProfile,
			Long targetAgreementNumber, Long targetPartyOID,RequestKindType[] requestkinds) throws ValidationException{

	}
	
	/**
	 * Validate all the party roles
	 *
	 */
	public void validatePartyRoles(List<PartyRoleDTO> partyRoles) throws ValidationException{
		new PartyValidator().validatePartyRoles(partyRoles);
	}
	
	/**
	 * Store Tax Details for a party, will raise a request to store the Tax Details</br>
	 * Will return the key of the party as a response
	 */
	public long storeTaxDetails(TaxDetailsDTO taxDetails,Long agreementContext, ISessionUserProfile sessionUserProfile, 
			TaxDetailsDTO taxDetailBeforeImage, RequestKindType[] requestKinds, boolean agreementAttached) throws ValidationException {
		
		return 123;
			
	}	
	
	/**
	 * Store Medical Aid Details for a party, will raise a request to store the Medical Aid Details	
	 * @param medicalAidDetailDTO
	 * @param agreementContext
	 * @param sessionUserProfile
	 * @param medicalAidDetailDTOBeforeImage
	 * @param requestKinds
	 * @param agreementAttached
	 * @throws ValidationException
	 */
	public void storeMedicalAidDetails(MedicalAidDetailDTO medicalAidDetailDTO,Long agreementContext, ISessionUserProfile sessionUserProfile, 
			MedicalAidDetailDTO medicalAidDetailDTOBeforeImage, RequestKindType[] requestKinds, boolean agreementAttached) throws ValidationException {
		
		logger.info("storeMedicalAidDetails");
	
	}
	
	@Override
	/*
	 * (non-Javadoc)
	 * 
	 * @see za.co.liberty.business.guicontrollers.partymaintenance.
	 * IPartyMaintenanceController#storeProvidentFundBeneficiaries(java.util.
	 * List, java.lang.Long, za.co.liberty.dto.userprofiles.ISessionUserProfile,
	 * za.co.liberty.interfaces.agreements.requests.RequestKindType[], boolean)
	 */
	public void storeProvidentFundBeneficiaries(List<ProvidentFundBeneficiaryDetailsDTO> beneficiaryDetailsList, 
			List<ProvidentFundBeneficiaryDetailsDTO> beneficiaryDetailsBeforeImageList, 
			Long agreementContext, ISessionUserProfile sessionUserProfile, RequestKindType[] requestKinds, 
			boolean agreementAttached) throws ValidationException {
		
		logger.info("storeProvidentFundBeneficiaries");
		
	}

	/**
	 * Get a PartyDTO using its id
	 * 
	 * @param partyOID
	 * @return
	 * @throws DataNotFoundException
	 * @throws CommunicationException
	 */
	public PartyDTO getPartyDTO(long partyOID) throws DataNotFoundException {
		
		//return partyManagment.getPartyDTOWithObjectOid(partyOID);
		logger.info("PartyMaintenanceController:1:getPartyDTO(long partyOID)----------------"+partyOID);
// to show PersonDetail tab
		EmployeeDTO pDto=new EmployeeDTO();
		pDto.setFirstName("FirstName");
		pDto.setMiddleName("MiddleName");
		pDto.setKnowAsName("KnownName");
		pDto.setSurname("surName");
		pDto.setDateOfBirth(new Date());
		pDto.setMaidenName("MName");
		pDto.setInitials("Initials");
		pDto.setTaxAdvisor(false);
		pDto.setEmployeeNumber("ABC1123");
		pDto.setUPN("upn");
		
		
	/*	OrganisationDTO pDto=new OrganisationDTO();
		pDto.setBusinessName("SBFC");*/
		
			ContactPreferenceWrapperDTO contactPreferences =new ContactPreferenceWrapperDTO();
				List<ContactPreferenceDTO> contactPreferencesList = new ArrayList<ContactPreferenceDTO>();
				ContactPreferenceDTO cDTO=new ContactPreferenceDTO();
				cDTO.setLanguagePreference(1l);
				cDTO.setOid(partyOID);
				cDTO.setRolePlayerAnchor(1l);
				cDTO.setType(UsageType.SECURE);
				List<ContactDetailDTO> contactList=new ArrayList<ContactDetailDTO>();
				AddressDTO aDto=new AddressDTO(ContactDetailType.PHYSICAL_ADDRESS);
				aDto.setAddressLine("Address Line1");
				aDto.setCity("City");
				aDto.setPostal(true);
				aDto.setPostCode("12345");
				aDto.setUsage(UsageType.SECURE);
				aDto.setEffectiveFrom(new Date());
				contactList.add(aDto);
				
				TelDetailDTO tDTO=new TelDetailDTO();
				tDTO.setCode("123");
				tDTO.setNumber("123456789");
				tDTO.setUsage(UsageType.BUSINESS);
				tDTO.setType(ContactDetailType.CELLPHONE_NUMBER);
				tDTO.setEffectiveTo(new Date());
				contactList.add(tDTO);
				/*	ContactDetailDTO contactDetails=new ContactDetailDTO();
					contactDetails.setType(ContactDetailType.CELLPHONE_NUMBER);
					contactDetails.setUsage(UsageType.BUSINESS);
					contactDetails.setCreatedByPartyID(1l);
					contactDetails.setCreationTime(new Date());
					contactDetails.setEffectiveFrom(new Date());
					contactDetails.setEffectiveTo(new Date());
					contactDetails.setId(partyOID);
					contactDetails.setSubTypeId(partyOID);
					contactList.add(contactDetails);
					
					contactDetails=new ContactDetailDTO();
					contactDetails.setType(ContactDetailType.POSTAL_ADDRESS);
					contactDetails.setUsage(UsageType.HOME);
					contactDetails.setCreatedByPartyID(1l);
					contactDetails.setCreationTime(new Date());
					contactDetails.setEffectiveFrom(new Date());
					contactDetails.setEffectiveTo(new Date());
					contactDetails.setId(partyOID);
					contactDetails.setSubTypeId(partyOID);
					contactList.add(contactDetails);*/
			cDTO.setContactDetails(contactList);
			contactPreferencesList.add(cDTO);
			contactPreferences.setContactPreferences(contactPreferencesList);
		pDto.setContactPreferences(contactPreferences);
		
			List<CommunicationPreferenceDTO> cpDTOList=new ArrayList<CommunicationPreferenceDTO>();
			CommunicationPreferenceDTO cpDTO=new CommunicationPreferenceDTO();
			cpDTO.setBusinessProcessType(BusinessProcessType.VALUE_SERVICES);
			cpDTO.setOptedInSMS(true);
			cpDTO.setOptedInEmail(false);
			cpDTO.setCreatedByPartyID(partyOID);
			cpDTO.setCreationTime(new Date());
			cpDTO.setEffectiveFrom(new Date());
			cpDTO.setEffectiveTo(new Date());
			cpDTO.setId(partyOID);
			cpDTO.setOid(partyOID);
			cpDTO.setSubTypeId(1l);
			cpDTO.setVersion(0);
			cpDTOList.add(cpDTO);
		pDto.setCommunicationPreferences(cpDTOList);
		
			BankingDetailsDTO bnkDTO=new BankingDetailsDTO();
			bnkDTO.setAccHolderRel(AccountHolderRelType.OWN);
			bnkDTO.setAccHolderRelProp(123);
			bnkDTO.setAccountHolderInitials("M");
			bnkDTO.setAccountHolderSurname("surname");
			bnkDTO.setAccountNumber(12345l);
			bnkDTO.setAccountType(AccountType.SAVINGS);
			bnkDTO.setActionType(ActionType.VIEW);
			bnkDTO.setAgreementSpecific(true);
			bnkDTO.setBankingDetailsSource(BankingDetailsSource.EXTERNAL_TEST_FILE);
			bnkDTO.setBankingDetailType(BankingDetailType.PARTY);
			bnkDTO.setBankingVerificationResponseDTO(new BankingVerificationResponseDTO(
					AVSStatusType.YES,AVSStatusType.YES,AVSStatusType.YES,AVSStatusType.NO,AVSStatusType.YES,
					AVSStatusType.YES,AVSStatusType.YES,AVSStatusType.YES,AVSStatusType.NO,AVSStatusType.NO,
					AVSStatusType.YES,AVSStatusType.NO));
			bnkDTO.setBankName(new BankNameDTO(1,"DummyBank"));
			bnkDTO.setBranch(new BankBranchDTO(1l,"sampleLocation"));
			bnkDTO.setBratLinkNo(1l);
			bnkDTO.setCompanyRegistration("registratio");
			bnkDTO.setErrorMsgFromMQSI("erro msg mq");
			bnkDTO.setHoursValid(1);
			bnkDTO.setIdentityNumber(0l);;
			bnkDTO.setPartyoid(123l);
			bnkDTO.setPassportNumber("R123456");
			bnkDTO.setRequestOid(0l);
			bnkDTO.setSelectedAgreementNumber(1l);
		pDto.setBankingDetailsDTO(bnkDTO);
		pDto.setTypeDescription("Description");
		return pDto;
	}	
	
	/**
	 * Checks if a request should be made using the data provided
	 *
	 */
	private RequestKindType[] doRequestKindCheckonData(RequestKindType[] requestKinds,PartyDTO party, PartyDTO origParty,List<ObjectComparisonDifferences> diffs, boolean addingAgreement){
		@SuppressWarnings("unused")
		int newArrayLength = requestKinds.length;
		//due to the fact that secure contact details require a different request, I will ask the dto if it has secure
		//if it does I will raise that request		
		//also, if id is adjusted and the party is an intermediary then the party with aproval request must be raised
		ArrayList<RequestKindType> ret = new ArrayList<RequestKindType>();		
		boolean partyIsIntermediary =  false;
		//if ended today or before today then there is no need to validate
		for(int i = 0; i < requestKinds.length ; i++){
			RequestKindType type = requestKinds[i];
			if(type == RequestKindType.MaintainContactDetails){				
				if(party != null){
					List<ContactPreferenceDTO> nonSecure = party.getNonSecureContactDetails();
					List<ContactPreferenceDTO> origNonSecure = null;
					if(origParty != null){
						origNonSecure = origParty.getNonSecureContactDetails();
					}else{
						origNonSecure = new ArrayList<ContactPreferenceDTO>(0);
					}
					List<ObjectComparisonDifferences> secDiffs = ComparatorUtil.compareObjects(nonSecure, origNonSecure,true);
					boolean remove = true;
					for(ObjectComparisonDifferences diff : secDiffs){
						if(!diff.getFieldName().equalsIgnoreCase("createdByPartyID")){
							remove = false;
							break;
						}
					}
					if(!remove){
						ret.add(RequestKindType.MaintainContactDetails);
					}
					
				}
			}else if(type == RequestKindType.MaintainSecureContactDetails){					
				if(party != null){
					List<ContactPreferenceDTO> secure = party.getSecureContactDetails();
					List<ContactPreferenceDTO> origSecure = null;
					if(origParty != null){
						origSecure = origParty.getSecureContactDetails();
					}else{
						origSecure = new ArrayList<ContactPreferenceDTO>(0);
					}
					List<ObjectComparisonDifferences> secDiffs = ComparatorUtil.compareObjects(secure, origSecure,true);
					boolean remove = (secDiffs.size() == 0);					
					if(!remove){
						ret.add(RequestKindType.MaintainSecureContactDetails);
					}									
				}
			}else if(type == RequestKindType.MaintainPartyWithApproval){
				boolean remove = true;
				if(party != null && party instanceof PersonDTO){					
					if(origParty != null){										
						PersonDTO person = (PersonDTO) party;
						PersonDTO origPerson = (PersonDTO) origParty;
						//check if id changed and party is intermediary
						if(partyIsIntermediary && (ObjectChangeHelper.objectChanged(person.getIdentificationNumber(), origPerson.getIdentificationNumber())
										|| ObjectChangeHelper.objectChanged(person.getIdentificationNumberType(), origPerson.getIdentificationNumberType())
										|| ObjectChangeHelper.objectChanged(person.getPassportCountry(), origPerson.getPassportCountry()))){
							//party is an intermediary and the id did not change
							remove = false;
						}
					}					
				}
				if(!remove){
					ret.add(RequestKindType.MaintainPartyWithApproval);
				}
			}else if(type == RequestKindType.MaintainOrganisationKnownAsName){
				boolean remove = true;
				if(party != null && party instanceof OrganisationDTO){					
					List<ObjectComparisonDifferences> secDiffs = ComparatorUtil.compareObjects(((OrganisationDTO) party).getKnownAsName(),
							((origParty != null)?((OrganisationDTO) origParty).getKnownAsName():null),true);
					//boolean remove = true;
					for(ObjectComparisonDifferences diff : secDiffs){
						if(!diff.getFieldName().equalsIgnoreCase("createdByPartyID")){
							remove = false;
							break;
						}
					}
													
				}
				if(!remove){
					ret.add(RequestKindType.MaintainOrganisationKnownAsName);
				}
			}
			else if(type == RequestKindType.MaintainOtherSystemAccess){
				boolean add = true;
				if(party != null && party instanceof EmployeeDTO){					
					if(origParty != null){										
						EmployeeDTO person = (EmployeeDTO) party;
						EmployeeDTO origPerson = (EmployeeDTO) origParty;
						PartyRoleDTO currentRole = person.getWealthConnectionRole();
						PartyRoleDTO origRole = origPerson.getWealthConnectionRole();
						
						PartyRoleDTO currentTestRole = person.getWealthConnectionTestRole();
						PartyRoleDTO origTestRole = origPerson.getWealthConnectionTestRole();
                        
						if(!person.isWealthConnectionUser() && !origPerson.isWealthConnectionUser()
								&& !person.isWealthConnectionTestUser() && !origPerson.isWealthConnectionTestUser()){
							add = false;
						}else if(ComparatorUtil.compareObjects(currentRole, origRole,true).size() == 0
                                && ComparatorUtil.compareObjects(currentTestRole, origTestRole,true).size() == 0){
							add = false;
						}												
					}else if(origParty == null && !((EmployeeDTO) party).isWealthConnectionUser()){
						add = false;
					}					
				}else{
					add = false;
				}
				if(add){
					ret.add(RequestKindType.MaintainOtherSystemAccess);
				}
			}else if(type == RequestKindType.MaintainPartyDetails){
				//check that not only the id changed
				boolean remove = true;
				boolean partyWithApproval = false;
				boolean onlyBirthDateChanged = true;
				@SuppressWarnings("unused")
				boolean oterSystemAccess = false;
				for(ObjectComparisonDifferences dif : diffs){					
					if(!dif.getTraceString().contains("contactPreferences") && partyIsIntermediary && (dif.getFieldName().equals("identificationNumberType") 
							|| dif.getFieldName().equals("identificationNumber") || dif.getTraceString().contains("passportCountry"))){
						//secure should be raised, if only birthdate changed then remove this request
						partyWithApproval = true;
					}else if(!dif.getTraceString().contains("contactPreferences") && !dif.getFieldName().equals("dateOfBirth")){
						onlyBirthDateChanged = false;
					}					
					if(!dif.getTraceString().contains("contactPreferences") 
							&& !dif.getTraceString().contains("wealthConnectionUser") 
							&& !dif.getTraceString().contains("wealthConnectionRole")  
							&& ((!dif.getFieldName().equals("identificationNumberType")
							&& !dif.getFieldName().equals("identificationNumber") 
							&& !dif.getTraceString().contains("passportCountry")) || !partyIsIntermediary)){
						remove = false;
					}
					
				}
				if(partyWithApproval && onlyBirthDateChanged){
					remove = true;
				}				
				if(!remove){
					ret.add(RequestKindType.MaintainPartyDetails);
				}
			}
			else if(type == RequestKindType.MaintainPaymentDetails)
			{	
				boolean bankDetChanged = false;
				if(partyIsIntermediary){
				BankingDetailsDTO newBankingDetailsDTO = (party != null)?party.getBankingDetailsDTO():null;
				BankingDetailsDTO origBankinGDetailsDTO = (origParty != null)?origParty.getBankingDetailsDTO():null;
				List<ObjectComparisonDifferences> secDiffs = ComparatorUtil.compareObjects(newBankingDetailsDTO, origBankinGDetailsDTO,true);
				for(ObjectComparisonDifferences dif : secDiffs){
						//ActonType difference should be ignored in BankingDetailsDTO
						if(!dif.getFieldName().equals("actionType")){
							bankDetChanged = true;				
							break;
						}
				
					}
				}
				
			if(bankDetChanged){

					ret.add(RequestKindType.MaintainPaymentDetails);
				}

			}else if(type == RequestKindType.MaintainBusinessCardDetails){
				boolean commPreferencesChanged = false;
				
				if(party != null && origParty != null){
					
					List<CommunicationPreferenceDTO> newCommPreferencelList = party.getCommunicationPreferences();
					List<CommunicationPreferenceDTO> origCommPreferencelList = origParty.getCommunicationPreferences();
					
					List<ObjectComparisonDifferences> secDiffs = ComparatorUtil.compareObjects(newCommPreferencelList, origCommPreferencelList,true);
					for(ObjectComparisonDifferences dif : secDiffs){
						//ActonType difference should be ignored in BankingDetailsDTO
						if(!dif.getFieldName().equals("actionType")){
							commPreferencesChanged = true;				
							break;
						}						
					}
					
					if(commPreferencesChanged){

						ret.add(RequestKindType.MaintainBusinessCardDetails);
					}
				}
				
				/*
				CommunicationPreferenceDTO newCommPreferenceDTO = (party != null) ? party.getCommunicationPreferences().get(0) : null;
				CommunicationPreferenceDTO origCommPreferenceDTO = (origParty != null) ? origParty.getCommunicationPreferences().get(0) : null;
				
				List<ObjectComparisonDifferences> secDiffs = ComparatorUtil.compareObjects(newCommPreferenceDTO, origCommPreferenceDTO,true);
				for(ObjectComparisonDifferences dif : secDiffs){
					//ActonType difference should be ignored in BankingDetailsDTO
					if(!dif.getFieldName().equals("actionType")){
						commPreferencesChanged = true;				
						break;
					}
			
				}
				if(commPreferencesChanged){

					ret.add(RequestKindType.MaintainBusinessCardDetails);
				}*/
			}

		}		
		return ret.toArray(new RequestKindType[0]);
	}	

	public TaxDetailsDTO getTaxDetailsDTO(long partyOID,long contextAgno,boolean includeHistory) throws DataNotFoundException {
		return null;
	}	
	
//	/**
//	 * Get the banking details.  Will return the banking details of the paysTo role if it exists on the
//	 * selected agreement.
//	 */
//	public BankingDetailsDTO getAgreementSpecificBankingDetailsDTO(Long agreementNumber) throws DataNotFoundException {
//		
//		List<PartyRoleContextFLO> partyRoleContextList = partyEntityManager.findActivePartyRolesWithContextRoleType(agreementNumber,PartyRoleType.INTERMEDIARY);
//		
//		if(partyRoleContextList == null || partyRoleContextList.size() <= 0) {
//			throw new DataNotFoundException("Could not find the agreement party role oid for agreement -> " + agreementNumber);
//		}
//		
//		long agreementPartyOID =  partyRoleContextList.get(0).getRoleOid();
//		
//		BankingDetailsDTO bankingDetailsDTO = new BankingDetailsDTO();
//		PartyBPOLocal partyBPO = PartyEJBLookupUtils.getInstance().getLocalPartyBPO();
//		FinancialAccountDetailEJBLocal  finAccDetail = partyBPO.getFinancialAccountDetail(agreementPartyOID);
//  		
//		if (finAccDetail != null){
//			FinancialAccountDetailVO financialAccDetVO = finAccDetail.getFinancialAccountDetailVO();
//	    	if(financialAccDetVO != null && financialAccDetVO.getExternalReference() != null 
//	    			&& financialAccDetVO.getExternalReference().trim() != ""){
//	  
//	    		String bratlinkNo = financialAccDetVO.getExternalReference();
//	    		bankingDetailsDTO.setBratLinkNo(new Long(bratlinkNo));
//	    		int accHolder = financialAccDetVO.getAccHolderRel();
//	    		bankingDetailsDTO.setAccHolderRel(AccountHolderRelType.getNameUsingId(accHolder));
//	    	}else{
//	    	     throw new DataNotFoundException("Financial Account Details not found for party");
//	    	}
//	    	
//	    	bankingDetailsDTO.setActionType(ActionType.VIEW);
//	    	
//	    	
//	    	try {
//				bankingDetailsDTO = BankingDetailsIntegrationHelper.getInstance().getBankingDetailsFromMQSI(bankingDetailsDTO);
//				bankingDetailsDTO.setDisplayFinancialDetails(DisplayFinancialDetails.AGREEMENTSPECIFIC.getDescription());
//			} catch (CommunicationException e) {
//				e.printStackTrace();
//			} catch (ValidationException e) {
//				e.printStackTrace();
//			}
//		} else {
//			bankingDetailsDTO = new BankingDetailsDTO();
//			bankingDetailsDTO.setDisplayFinancialDetails(DisplayFinancialDetails.AGREEMENTSPECIFIC.getDescription());
//			bankingDetailsDTO.setBankName(new BankNameDTO());
//			bankingDetailsDTO.setBranch(new BankBranchDTO());
//		}
//		return bankingDetailsDTO;
//	}
	
	
	/**
	 * Get the banking details.  Will return the banking details of the paysTo role if it exists on the
	 * selected agreement.
	 * 
	 * Now also returns the banking details linked to the agreement or party (only if it's not paysTo)
	 */
	public MaintainBankingDetailResultDTO getBankingDetailsDTO(long partyOid, final Long searchedAgreementNr,
			BankingDetailType bankingDetailType) throws DataNotFoundException {
		
		logger.info("getBankingDetail   partyOid=" + partyOid + "  ,searchedAgreementNr=" 
				+ searchedAgreementNr + "  ,bankingDetailType=" + bankingDetailType);		
		MaintainBankingDetailResultDTO resultDto = new MaintainBankingDetailResultDTO();
		
		List<AgreementRoleDTO> paysToAgreementRoleList = getPaysToAgreementRolesForPartyOid(partyOid);
		long searchPartyOid = partyOid;
		Long agreementPartyRoleOid = null;
		Long selectedAgreementNr = searchedAgreementNr;
		
		if (selectedAgreementNr==null && paysToAgreementRoleList.size()>0) {
			// This should never happen as the GUI should not get to this point without an agreement in the context
//			throw new IllegalArgumentException("Selected agreement nr is required when paysTo roles exist on any of the intermediaries agreements");
			logger.warn("Selected agreement nr is required when paysTo roles exist on any of the intermediaries agreements");
		} 
		

		
		/*
		 * Get details linked to the agreement
		 */
		BankingDetailsDTO agreementBankDto = null;
		
		
		/*
		 * Get the details linked to the party
		 */
		BankingDetailsDTO partyBankDto = null;
		try {
// #WICKETTEST MSK Commented below temp,need to revert while migrate			
//			partyBankDto = partyManagment.getBankingDetailsDTOWithObjectOid(new BankingDetailsSearchDTO(BankingDetailType.PARTY,searchPartyOid,0));
			BankingDetailsDTO bnkDTO=new BankingDetailsDTO();
			bnkDTO.setAccountNumber(12345l);
			bnkDTO.setAccountType(AccountType.SAVINGS);
			bnkDTO.setAccountHolderInitials("I");
			bnkDTO.setAccountHolderSurname("SurName");
			bnkDTO.setAccHolderRel(AccountHolderRelType.OWN);
			bnkDTO.setErrorMsgFromMQSI("errorMsgFromMQSI");
			bnkDTO.setActionType(ActionType.VIEW);
			bnkDTO.setAccHolderRelProp(12);
			bnkDTO.setBankingDetailType(BankingDetailType.PARTY);
			bnkDTO.setSelectedAgreementNumber(123);
			bnkDTO.setIdentityNumber(1111l);
			bnkDTO.setPassportNumber("0123456");
			bnkDTO.setCompanyRegistration("CompanyRegistration");
			bnkDTO.setHoursValid(10);
			bnkDTO.setPartyoid(123);
			bnkDTO.setBratLinkNo(10101l);
			bnkDTO.setRequestOid(0011l);
			bnkDTO.setBankingDetailsSource(BankingDetailsSource.INTEGRATION);
			bnkDTO.setBankName(new BankNameDTO(1,"SBFC"));
			bnkDTO.setBranch(new BankBranchDTO(11l,"SBFC"));
			bnkDTO.setBankingVerificationResponseDTO(new BankingVerificationResponseDTO());
		} catch (Exception e) {//catch (DataNotFoundException e) {
			// Ignore this error
		}
		
		/*
		 * Identify the effective details and return
		 */
		resultDto.setAgreementBankingDetailsObject(agreementBankDto);
		resultDto.setPartyBankingDetailsObject(partyBankDto);
		
		if (agreementBankDto!=null) {
			resultDto.setBankingDetailsObject(new BankingDetailsDTO(agreementBankDto));
			resultDto.setPartyOid(agreementPartyRoleOid);
		} else if (partyBankDto!=null){
			resultDto.setBankingDetailsObject(new BankingDetailsDTO(partyBankDto));
			resultDto.setPartyOid(searchPartyOid);
		} else {
			resultDto.setBankingDetailsObject(new BankingDetailsDTO());
			resultDto.setPartyOid(searchPartyOid);
		}
		
		resultDto.setAgreementNumber(selectedAgreementNr);  // agreement nr of shown banking details (could be pays to agreement)
//		resultDto.setPaysToDetails(isPaysToDetails);
		
//		/*
//		 * For non isPaysTo we first check if we can find the agreement specific banking details
//		 */
//		if (!isPaysToDetails && selectedAgreementNr != null && (bankingDetailType == null || bankingDetailType == BankingDetailType.AGREEMENT)) {
//			logger.info("  -- Retrieve paysTo info for agreement " + selectedAgreementNr);
//			// Is there financial details linked to the 
//			List<PartyRoleContextFLO> partyRoleContextList = partyEntityManager.findActivePartyRolesWithContextRoleType(selectedAgreementNr,
//					PartyRoleType.INTERMEDIARY);
//			
//			if(partyRoleContextList != null && partyRoleContextList.size() > 0) {
//				agreementPartyRoleOid =  partyRoleContextList.get(0).getRoleOid();
//				
//				BankingDetailsDTO dto = partyManagment.getBankingDetailsDTOWithObjectOid(searchPartyOid);
//				resultDto.setAgreementNumber(selectedAgreementNr);  // agreement nr of shown banking details (could be pays to agreement)
//				resultDto.setPartyOid(searchPartyOid);
//				resultDto.setBankingDetailsObject(dto);
//				resultDto.setPaysToDetails(isPaysToDetails);
//				dto.setAgreementSpecific(true);
//				return resultDto;
//			}
//			
//		}
//		
//		BankingDetailsDTO dto = null;
//		if (bankingDetailType == null || bankingDetailType == BankingDetailType.PARTY) {
//			logger.info("  -- Retrieve party banking detail");
//			dto = partyManagment.getBankingDetailsDTOWithObjectOid(searchPartyOid);
//		} else {
//			logger.info("  -- Retrieve empty banking detail");
//			dto = new BankingDetailsDTO();
//		}
//		
//		resultDto.setAgreementNumber(selectedAgreementNr);  // agreement nr of shown banking details (could be pays to agreement)
//		resultDto.setPartyOid(searchPartyOid);
//		resultDto.setBankingDetailsObject(dto);
//		resultDto.setPaysToDetails(isPaysToDetails);
		
		// Check if there is requestOid, if it is set call the request manager 
		
		/* Find the request */

		getLatestVerificationResults(resultDto);

		
		return resultDto;
		
	}

	private void getLatestVerificationResults(MaintainBankingDetailResultDTO resultDto) throws DataNotFoundException {

	}	
	
	
	/**
	 * A class to keep the party request data used if another process needs to raise party requests agains another gui request
	 * @author DZS2610
	 *
	 */
	public class PartyRequestDetails{
		private boolean objectModified;
		private RequestKindType[] requestKindsToRaise;
		
		public boolean isObjectModified() {
			return objectModified;
		}

		public RequestKindType[] getRequestKindsToRaise() {
			return requestKindsToRaise;
		}		
	}


	public BankingDetailsDTO validateFinancialAccountDetails(BankingDetailsDTO bankingDetailsDTO, PartyDTO partyDTO) throws ValidationException, DataNotFoundException {
		
		return  null;
		
	}
	/**
	 * This Method Validates the Tax Details DTO for a Party
	 */
	
	public TaxDetailsDTO validateAllTaxDetails(TaxDetailsDTO taxDetailsDTO, long srsId, 
			Date agreementStartDate) throws ValidationException {


		return taxDetailsDTO;

	}
	
	/**
	 * Gets all the party roles for a party
	 * @param partyoid
	 * @return
	 */
	public List<PartyRoleDTO> getPartyRolesForPartyOIDForTheHierarchyPanel(long partyoid,
			List<PartyRoleType> roleTypes){							
		//return partyManagment.getPartyRolesForPartyOID(partyoid, true, roleTypes);
//MSK#Change for test data ,need to delete while migrating
		List<PartyRoleDTO> rolDTO=new ArrayList<PartyRoleDTO>();
		PartyRoleDTO prDTO=new PartyRoleDTO();
		prDTO.setPartyRoleType(PartyRoleType.EMPLOYEE);
		
		rolDTO.add(prDTO);
		return rolDTO;
	}
	
	/**
	 * Returns true only if the party has an active agreement
	 * @param partyOID
	 * @return
	 */
	public boolean isPartyAnIntermediary(long partyOID){
		//return partyManagment.isPartyAnIntermediary(partyOID);
	// #WICKETTEST #WICKETFIX  need remove later 
		return true;
	}

	/**
	 * Broadcast a party, if its an intermediary and agreementNumberInContext is not null then broadcast the agreement
	 * @param partyOID
	 * @throws ValidationException 
	 * @throws DataNotFoundException 
	 * @throws PartyDetailsBroadcastException 
	 */
	public void broadcastPartySynchronous(Long partyOID, Long agreementNumberInContext, long requestorPartyOId) throws ValidationException, PartyDetailsBroadcastException {
	}
	
	/**
	 * This method returns AQC History for a Party and AQC Type (Calculated /Manual) 
	 * @param partyOid
	 * @param partyRegAqcType
	 * @return List of PartyAQCHistoryFLO
	 */
	public List<PartyAQCHistoryFLO> getAQCHistoryForPartyAndAQCType(long partyOid, long partyRegAqcType){
		
		return this.getAQCHistoryForPartyAndAQCType(partyOid, partyRegAqcType, false);
	}
	
	/**
	 * This method returns AQC History for a Party and AQC Type (Calculated /Manual) 
	 * @param partyOid
	 * @param partyRegAqcType
	 * @param includeReplaced
	 * @return List of PartyAQCHistoryFLO
	 */
	public List<PartyAQCHistoryFLO> getAQCHistoryForPartyAndAQCType(long partyOid, long partyRegAqcType, boolean includeReplaced){
		
		return Collections.EMPTY_LIST;
	}
	
	/**
	 * Get the pays to agreement roles for all the agreements linked to the given
	 * party oid
	 * 
	 * @param partyOid
	 * @return
	 */
	public List<AgreementRoleDTO> getPaysToAgreementRolesForPartyOid(long partyOid) {
// #WICKETTEST	MSK Commented ,need to be revert in migration	
		//return partyManagment.getPaysToAgreementRolesForPartyOid(partyOid);
		logger.info("PartyMaintenanceController:2:getPaysToAgreementRolesForPartyOid(long partyOid)------"+partyOid);
		return new ArrayList<AgreementRoleDTO>();
	}
	
	/**
	 * Get the current medical aid details for the given partyid
	 * @param partyID
	 * @return
	 */
	public MedicalAidDetailDTO getMedicalAidDetailDTO(long partyID){
		//return partyManagment.getMedicalAidDetailsForParty(partyID);
// #WICKETTEST	MSK Commented ,need to be revert in migration		
		MedicalAidDetailDTO mDTO=new MedicalAidDetailDTO();
		mDTO.setPartyRegitrationOID(partyID);
		mDTO.setPartyRegitrationVersion(0);
		mDTO.setCreatedByPartyID(111l);
		mDTO.setMedicalAidNumber("12345");
		mDTO.setPrimaryMember(true);
		mDTO.setNumberDependants(0);
		mDTO.setPartyoid(partyID);
		mDTO.setHasMedicalAid(true);
		mDTO.setEffectiveDate(new Date());
		mDTO.setEffectiveToDate(new Date());
		return mDTO;
	}

	/**
	 * Get all configured medical aid details form rating table SRS.MEDICALAIDS
	 * @return
	 */
	public List<IDValueDTO> getAllMedicalAids() {		
		//return ratingEntityManager.getAllMedicalAids();
// #WICKETTEST	MSK Commented ,need to be revert in migration
		List<IDValueDTO> IDValueDTO=new ArrayList<IDValueDTO>();
		IDValueDTO idValDto=new IDValueDTO("Name-One", 123l);
		IDValueDTO.add(idValDto);
		idValDto=new IDValueDTO("Name-Two", 124l);
		IDValueDTO.add(idValDto);
		return IDValueDTO;
	}
	
	/**
	 * Validate the medical aid details
	 * @throws ValidationException 
	 *
	 */
	public void validateMedicalAidDetail(MedicalAidDetailDTO medicalAidDetailDTO) throws ValidationException{
	 
	}
	
	/**
	 * get a list of liberty month start dates
	 * @return
	 */
	public List<Date> getLibertyMonthStartDatesfromTaxYearStart(){
		return Collections.EMPTY_LIST;
	}
	
	public List<DescriptionDTO> findValuesByDescriptionKind (int descriptionKind)	{
		List<DescriptionDTO> descriptionDTOs = new ArrayList<DescriptionDTO>();
		//List<Description> descriptions = descriptionEntityManager.findValuesByDescriptionKind(descriptionKind);
//MSK#Commented :need to remove while migration		
		List<Description> descriptions = new ArrayList<Description>();
		Description desc=new Description();
		desc.setDescription("Description");
		desc.setDescriptionKind(1);
		desc.setEndDate(new Date());
		desc.setName("Name");
		desc.setReference(1);
		desc.setStartDate(new Date());
		desc.setUniqId(1);
		descriptions.add(desc);
//MSK#Commented :need to remove while migration
		for (Description des:descriptions)
		{
			DescriptionDTO descriptionDTO = new DescriptionDTO();
			descriptionDTO.setDescription(des.getDescription());
			descriptionDTO.setDescriptionKind(des.getDescriptionKind());
			descriptionDTO.setName(des.getName());
			descriptionDTO.setReference(des.getReference());
			descriptionDTO.setUniqId(des.getUniqId());
			descriptionDTOs.add(descriptionDTO);
		}
		return descriptionDTOs;
	}

	public void raisePartyhierarchyAndRoleRequest(
			ArrayList<PartyRoleDTO> partyRolesToStore,
			List<PartyRoleDTO> partyToPartyRolesBeforeImage,
			ISessionUserProfile userProfile, Long agmtNoContext, long oid,
			RequestKindType[] requestkinds,
			ArrayList<PartyRoleDTO> partyRoles,
			List<PartyRoleDTO> partyRolesBeforeImage)
			throws ValidationException {
		
	}


	@Override
	public List<BankVerificationRulesDTO> verifyAVSResponse(BankingVerificationResponseDTO arg0) {
		// TODO Auto-generated method stub
		return Collections.EMPTY_LIST;
	}
}