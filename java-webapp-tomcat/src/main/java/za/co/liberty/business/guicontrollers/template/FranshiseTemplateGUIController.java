package za.co.liberty.business.guicontrollers.template;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.EJBContext;
import javax.ejb.Stateless;

import za.co.liberty.business.request.IGuiRequestManagement;
import za.co.liberty.business.template.IFranshiseTemplateManagement;
import za.co.liberty.common.domain.ApplicationContext;
import za.co.liberty.dto.gui.templates.DistributionKindGroupRatesDTO;
import za.co.liberty.dto.gui.templates.FranchiseTemplateDTO;
import za.co.liberty.dto.gui.templates.MaintainFranchiseTemplateDTO;
import za.co.liberty.dto.userprofiles.ISessionUserProfile;
import za.co.liberty.exceptions.data.DataNotFoundException;
import za.co.liberty.exceptions.data.ValidationException;
import za.co.liberty.exceptions.error.request.RequestConfigurationException;
import za.co.liberty.exceptions.error.request.RequestException;
import za.co.liberty.helpers.util.ComparatorUtil;
import za.co.liberty.helpers.util.ComparatorUtil.ObjectComparisonDifferences;
import za.co.liberty.interfaces.agreements.distribution.DistributionTemplateKindType;
import za.co.liberty.interfaces.agreements.requests.RequestKindType;
import za.co.liberty.interfaces.gui.GuiRequestKindType;
import za.co.liberty.interfaces.rating.description.DescriptionKindType;
import za.co.liberty.persistence.rating.IDescriptionEntityManager;
import za.co.liberty.persistence.rating.IDistributionKindGroupMappingEntityManager;
import za.co.liberty.persistence.rating.IDpeDistributionVersionEntityManager;
import za.co.liberty.persistence.rating.entity.Description;
import za.co.liberty.persistence.rating.entity.DistributionKindGroupRatesEntity;


@Stateless(name = "FranshiseTemplateGUIController")
public class FranshiseTemplateGUIController implements IFranchiseTemplateGUIController {

	//@EJB
	//IFranshiseTemplateManagement franshiseTemplateManagement;
	
	//@EJB 
	//IGuiRequestManagement guiRequestManager;
	
	//@EJB
	//IDescriptionEntityManager descriptionEntityManager;
	
	//@EJB
	///IDistributionKindGroupMappingEntityManager groupMappingEntityManager;

	//@EJB
	//IDpeDistributionVersionEntityManager distributionVersionEntityManager;
	
	
	//@Resource
	//EJBContext ejbContext;

	
	public void validateTemplateKindGroups(FranchiseTemplateDTO franchiseTemplateDTO) throws ValidationException {
		//franshiseTemplateManagement.validateTemplateKindGroups(franchiseTemplateDTO);
		
		
	}

	public void saveTemplate(MaintainFranchiseTemplateDTO maintainFranchiseTemplateDTO, ISessionUserProfile userProfile, MaintainFranchiseTemplateDTO maintainFranchiseTemplateDTOBeforeImage, RequestKindType[] requestKinds) throws ValidationException {
		
		/*if(requestKinds == null || requestKinds.length == 0){
 			requestKinds = 
 			new RequestKindType[] {
 				RequestKindType.MaintainFranchiseTemplateDetails};
 		}	
		
		boolean validateFranchiseTemplateDetails = false;
		//if ended today or before today then there is no need to validate, no need to validate
		for(RequestKindType type : requestKinds){
			if(type == RequestKindType.MaintainFranchiseTemplateDetails){
				validateFranchiseTemplateDetails = true;
			}
		}	
		
		
		FranchiseTemplateDTO createTemplateObjects = franshiseTemplateManagement.createTemplateObjects(maintainFranchiseTemplateDTO.getFranchiseTemplateDTO());
		
		maintainFranchiseTemplateDTO.setFranchiseTemplateDTO(createTemplateObjects);
		
     	try {
     		// Setup the request kinds that are required
     		
     		// Raise guiRequest
     		guiRequestManager.raiseGuiRequest(new ApplicationContext(), 
	     				userProfile, 
	 					null,
	 					null,
	 					GuiRequestKindType.MaintainFranchiseTemplate,
	 					requestKinds, 
	 					maintainFranchiseTemplateDTO, 
	 					maintainFranchiseTemplateDTOBeforeImage);
     		
     		
		} catch (RequestConfigurationException e) {	
			ejbContext.setRollbackOnly();
			throw new ValidationException(Arrays.asList(new String[]{e.getMessage()}));
		} catch (RequestException e) {
			ejbContext.setRollbackOnly();
			throw new ValidationException(Arrays.asList(new String[]{e.getMessage()}));
		}*/
	}

	public List<DistributionKindGroupRatesDTO> getDistributionKindGroupRatesByTemplateId(int templateId) throws DataNotFoundException {
		 //List<DistributionKindGroupRatesDTO> distributionKindGroupRates = franshiseTemplateManagement.getDistributionKindGroupRatesByTemplateId(templateId);
		 
		//return distributionKindGroupRates;

List<DistributionKindGroupRatesDTO> distributionKindGroupRatesList = new ArrayList<DistributionKindGroupRatesDTO> ();
		
		DistributionKindGroupRatesDTO distributionKindGroupRates = new DistributionKindGroupRatesDTO();
		distributionKindGroupRates.setDescription("test distribution group 1");
		distributionKindGroupRates.setDistributionFactor(1.0f);
		distributionKindGroupRates.setDistributionKindGroup(524);
		distributionKindGroupRates.setDistributionKindGroupReference(13);
		distributionKindGroupRates.setId(1);
		distributionKindGroupRates.setTemplateID(4792);
		distributionKindGroupRates.setDistributionSchedule(439);
		distributionKindGroupRatesList.add(distributionKindGroupRates);
		
		distributionKindGroupRates = new DistributionKindGroupRatesDTO();
		distributionKindGroupRates.setDescription("test distribution group 2");
		distributionKindGroupRates.setDistributionFactor(0.25f);
		distributionKindGroupRates.setDistributionKindGroup(524);
		distributionKindGroupRates.setDistributionKindGroupReference(13);
		distributionKindGroupRates.setId(2);
		distributionKindGroupRates.setTemplateID(4792);
		distributionKindGroupRates.setDistributionSchedule(439);
		distributionKindGroupRatesList.add(distributionKindGroupRates);
		
		distributionKindGroupRates = new DistributionKindGroupRatesDTO();
		distributionKindGroupRates.setDescription("test distribution group 3");
		distributionKindGroupRates.setDistributionFactor(0.50f);
		distributionKindGroupRates.setDistributionKindGroup(524);
		distributionKindGroupRates.setDistributionKindGroupReference(13);
		distributionKindGroupRates.setId(3);
		distributionKindGroupRates.setTemplateID(4804);
		distributionKindGroupRates.setDistributionSchedule(3969);
		distributionKindGroupRatesList.add(distributionKindGroupRates);
		
		distributionKindGroupRates = new DistributionKindGroupRatesDTO();
		distributionKindGroupRates.setDescription("test distribution group 4");
		distributionKindGroupRates.setDistributionFactor(0.75f);
		distributionKindGroupRates.setDistributionKindGroup(524);
		distributionKindGroupRates.setDistributionKindGroupReference(13);
		distributionKindGroupRates.setId(4);
		distributionKindGroupRates.setTemplateID(4804);
		distributionKindGroupRates.setDistributionSchedule(3969);
		distributionKindGroupRatesList.add(distributionKindGroupRates);
		
		
		List<DistributionKindGroupRatesDTO> distributionKindGroupRatesListByTemplateId = new ArrayList<DistributionKindGroupRatesDTO> ();
		
		for (DistributionKindGroupRatesDTO distributionKindGroupRatesDTO : distributionKindGroupRatesList)
		{
				if(distributionKindGroupRatesDTO.getTemplateID() == templateId)
					distributionKindGroupRatesListByTemplateId.add(distributionKindGroupRatesDTO);
		}
		
		return distributionKindGroupRatesListByTemplateId;
	}

	public List<Description> getExistingTemplatesStoredOnDKGRates() throws DataNotFoundException {
		
		//List<Description> existingTemplates = franshiseTemplateManagement.getExistingTemplatesStoredOnDKGRates();
		
		//return existingTemplates;
		
		List<Description> existingTemplates = new ArrayList<Description> ();
		
		Description templateDescription = new Description();
		templateDescription.setDescription("Temp 1");
		templateDescription.setDescriptionKind(25);
		templateDescription.setEndDate(null);
		templateDescription.setName("franchise_default_distrib_kind_detailset");
		templateDescription.setReference(847);
		templateDescription.setStartDate(null);
		templateDescription.setUniqId(4792);
		existingTemplates.add(templateDescription);
		
		templateDescription = new Description();
		templateDescription.setDescription("Temp 2");
		templateDescription.setDescriptionKind(25);
		templateDescription.setEndDate(null);
		templateDescription.setName("franchise_default_distrib_kind_detailset");
		templateDescription.setReference(859);
		templateDescription.setStartDate(null);
		templateDescription.setUniqId(4804);
		existingTemplates.add(templateDescription);
		
		
		return existingTemplates;
	}

	public List<DistributionKindGroupRatesDTO> createDistributionKindGroupDTOsFromDKGDefaultValues(int distributionKindGroup) {
		
	//	List<DistributionKindGroupRatesDTO> list = franshiseTemplateManagement.createDistributionKindGroupDTOsFromDKGDefaultValues(distributionKindGroup);
	//	return list;

		List<DistributionKindGroupRatesDTO> distributionKindGroupRatesList = new ArrayList<DistributionKindGroupRatesDTO> ();
		
		DistributionKindGroupRatesDTO distributionKindGroupRates = new DistributionKindGroupRatesDTO();
		distributionKindGroupRates.setDescription("test distribution group 1");
		distributionKindGroupRates.setDistributionFactor(1.0f);
		distributionKindGroupRates.setDistributionKindGroup(524);
		distributionKindGroupRates.setDistributionKindGroupReference(13);
		distributionKindGroupRates.setId(1);
		distributionKindGroupRates.setTemplateID(4792);
		distributionKindGroupRates.setDistributionSchedule(439);
		distributionKindGroupRatesList.add(distributionKindGroupRates);
		
		distributionKindGroupRates = new DistributionKindGroupRatesDTO();
		distributionKindGroupRates.setDescription("test distribution group 2");
		distributionKindGroupRates.setDistributionFactor(0.25f);
		distributionKindGroupRates.setDistributionKindGroup(524);
		distributionKindGroupRates.setDistributionKindGroupReference(13);
		distributionKindGroupRates.setId(2);
		distributionKindGroupRates.setTemplateID(4792);
		distributionKindGroupRates.setDistributionSchedule(439);
		distributionKindGroupRatesList.add(distributionKindGroupRates);
		
		distributionKindGroupRates = new DistributionKindGroupRatesDTO();
		distributionKindGroupRates.setDescription("test distribution group 3");
		distributionKindGroupRates.setDistributionFactor(0.50f);
		distributionKindGroupRates.setDistributionKindGroup(524);
		distributionKindGroupRates.setDistributionKindGroupReference(13);
		distributionKindGroupRates.setId(3);
		distributionKindGroupRates.setTemplateID(4804);
		distributionKindGroupRates.setDistributionSchedule(3969);
		distributionKindGroupRatesList.add(distributionKindGroupRates);
		
		distributionKindGroupRates = new DistributionKindGroupRatesDTO();
		distributionKindGroupRates.setDescription("test distribution group 4");
		distributionKindGroupRates.setDistributionFactor(0.75f);
		distributionKindGroupRates.setDistributionKindGroup(524);
		distributionKindGroupRates.setDistributionKindGroupReference(13);
		distributionKindGroupRates.setId(4);
		distributionKindGroupRates.setTemplateID(4804);
		distributionKindGroupRates.setDistributionSchedule(3969);
		distributionKindGroupRatesList.add(distributionKindGroupRates);
		
		List<DistributionKindGroupRatesDTO> distributionKindGroupRatesListBydistributionKindGroup = new ArrayList<DistributionKindGroupRatesDTO> ();
		
		for (DistributionKindGroupRatesDTO distributionKindGroupRatesDTO : distributionKindGroupRatesList)
		{
				if(distributionKindGroupRatesDTO.getDistributionKindGroup() == distributionKindGroup)
					distributionKindGroupRatesListBydistributionKindGroup.add(distributionKindGroupRatesDTO);
		}
		
		return distributionKindGroupRatesListBydistributionKindGroup;
	}

	public List<Description> getScheduleDescriptions() {
	//	return franshiseTemplateManagement.getDescriptionByKind(DescriptionKindType.DISTRIBUTION_SCHEDULE_KIND.getKind());
		
		List<Description> distributionScheduleList= new ArrayList<Description> ();
		
		Description distributionSchedule = new Description();
		distributionSchedule.setDescription("100% Now");
		distributionSchedule.setDescriptionKind(22);
		distributionSchedule.setEndDate(null);
		distributionSchedule.setName("distribution_schedule_kind");
		distributionSchedule.setReference(5);
		distributionSchedule.setStartDate(null);
		distributionSchedule.setUniqId(439);
		distributionScheduleList.add(distributionSchedule);
		
		distributionSchedule = new Description();
		distributionSchedule.setDescription("25% immediately, 25% in months 7,13 and 25");
		distributionSchedule.setDescriptionKind(22);
		distributionSchedule.setEndDate(null);
		distributionSchedule.setName("distribution_schedule_kind");
		distributionSchedule.setReference(20);
		distributionSchedule.setStartDate(null);
		distributionSchedule.setUniqId(3969);
		distributionScheduleList.add(distributionSchedule);
		
		
		return distributionScheduleList;	
	}

	public List<DistributionKindGroupRatesDTO> getDistributionKindGroupRatesDTOByDKG(int distributionKindGroup) {
		
	//	return franshiseTemplateManagement.getDistributionKindGroupRatesByDKGKind(distributionKindGroup);

List<DistributionKindGroupRatesDTO> distributionKindGroupRatesList = new ArrayList<DistributionKindGroupRatesDTO> ();
		
		DistributionKindGroupRatesDTO distributionKindGroupRates = new DistributionKindGroupRatesDTO();
		distributionKindGroupRates.setDescription("test distribution group 1");
		distributionKindGroupRates.setDistributionFactor(1.0f);
		distributionKindGroupRates.setDistributionKindGroup(524);
		distributionKindGroupRates.setDistributionKindGroupReference(13);
		distributionKindGroupRates.setId(1);
		distributionKindGroupRates.setTemplateID(4792);
		distributionKindGroupRates.setDistributionSchedule(439);
		distributionKindGroupRatesList.add(distributionKindGroupRates);
		
		distributionKindGroupRates = new DistributionKindGroupRatesDTO();
		distributionKindGroupRates.setDescription("test distribution group 2");
		distributionKindGroupRates.setDistributionFactor(0.25f);
		distributionKindGroupRates.setDistributionKindGroup(524);
		distributionKindGroupRates.setDistributionKindGroupReference(13);
		distributionKindGroupRates.setId(2);
		distributionKindGroupRates.setTemplateID(4792);
		distributionKindGroupRates.setDistributionSchedule(439);
		distributionKindGroupRatesList.add(distributionKindGroupRates);
		
		distributionKindGroupRates = new DistributionKindGroupRatesDTO();
		distributionKindGroupRates.setDescription("test distribution group 3");
		distributionKindGroupRates.setDistributionFactor(0.50f);
		distributionKindGroupRates.setDistributionKindGroup(524);
		distributionKindGroupRates.setDistributionKindGroupReference(13);
		distributionKindGroupRates.setId(3);
		distributionKindGroupRates.setTemplateID(4804);
		distributionKindGroupRates.setDistributionSchedule(3969);
		distributionKindGroupRatesList.add(distributionKindGroupRates);
		
		distributionKindGroupRates = new DistributionKindGroupRatesDTO();
		distributionKindGroupRates.setDescription("test distribution group 4");
		distributionKindGroupRates.setDistributionFactor(0.75f);
		distributionKindGroupRates.setDistributionKindGroup(524);
		distributionKindGroupRates.setDistributionKindGroupReference(13);
		distributionKindGroupRates.setId(4);
		distributionKindGroupRates.setTemplateID(4804);
		distributionKindGroupRates.setDistributionSchedule(3969);
		distributionKindGroupRatesList.add(distributionKindGroupRates);
		
		List<DistributionKindGroupRatesDTO> distributionKindGroupRatesListBydistributionKindGroup = new ArrayList<DistributionKindGroupRatesDTO> ();
		
		for (DistributionKindGroupRatesDTO distributionKindGroupRatesDTO : distributionKindGroupRatesList)
		{
				if(distributionKindGroupRatesDTO.getDistributionKindGroup() == distributionKindGroup)
					distributionKindGroupRatesListBydistributionKindGroup.add(distributionKindGroupRatesDTO);
		}
		
		return distributionKindGroupRatesListBydistributionKindGroup;
	}
	
}
