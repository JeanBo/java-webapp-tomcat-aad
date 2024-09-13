package za.co.liberty.business.guicontrollers.template;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import javax.ejb.Local;

import za.co.liberty.agreement.common.exceptions.LogicExecutionException;
import za.co.liberty.common.domain.ObjectReference;
import za.co.liberty.dto.agreement.AgreementHomeRoleDTO;
import za.co.liberty.dto.agreement.AgreementRoleDTO;
import za.co.liberty.dto.agreement.SimpleAgreementDetailDTO;
import za.co.liberty.dto.agreement.maintainagreement.BulkAgreementTransferDTO;
import za.co.liberty.dto.agreement.request.RequestEnquiryResultDTO;
import za.co.liberty.dto.common.IDValueDTO;
import za.co.liberty.dto.contracting.ResultPartyDTO;
import za.co.liberty.dto.databaseenum.CostCenterDBEnumDTO;
import za.co.liberty.dto.gui.templates.DistributionKindGroupRatesDTO;
import za.co.liberty.dto.gui.templates.FranchiseTemplateDTO;
import za.co.liberty.dto.gui.templates.MaintainFranchiseTemplateDTO;
import za.co.liberty.dto.party.HierarchyEmployeeLinkDTO;
import za.co.liberty.dto.party.HierarchyNodeDTO;
import za.co.liberty.dto.party.HierarchyNodeLinkDTO;
import za.co.liberty.dto.userprofiles.ISessionUserProfile;
import za.co.liberty.exceptions.SystemException;
import za.co.liberty.exceptions.data.DataNotFoundException;
import za.co.liberty.exceptions.data.QueryTimeoutException;
import za.co.liberty.exceptions.data.ValidationException;
import za.co.liberty.exceptions.error.request.RequestException;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.interfaces.agreements.requests.RequestKindType;
import za.co.liberty.interfaces.party.IPartyNameAndIdFLO;
import za.co.liberty.interfaces.persistence.agreement.request.IRequestEnquiryRow;
import za.co.liberty.party.exceptions.InvalidPartyRegException;
import za.co.liberty.persistence.party.entity.fastlane.PartyProfileFLO;
import za.co.liberty.persistence.rating.entity.Description;
import za.co.liberty.persistence.rating.entity.DistributionKindGroupRatesEntity;

@Local
public interface IFranchiseTemplateGUIController {

	
	public List<Description> getExistingTemplatesStoredOnDKGRates( )
			throws DataNotFoundException;
	
	public List<DistributionKindGroupRatesDTO> getDistributionKindGroupRatesByTemplateId(
			int templateId )
			throws DataNotFoundException;

	public void validateTemplateKindGroups(FranchiseTemplateDTO franchiseTemplateDTO) throws ValidationException;

	public void saveTemplate(MaintainFranchiseTemplateDTO maintainFranchiseTemplateDTO, ISessionUserProfile userProfile, MaintainFranchiseTemplateDTO maintainFranchiseTemplateDTOBeforeImage, RequestKindType[] mappingForPageAndPanel) throws ValidationException;

	public List<DistributionKindGroupRatesDTO> createDistributionKindGroupDTOsFromDKGDefaultValues(int distributionKindGroup);

	public List<Description> getScheduleDescriptions();

	public List<DistributionKindGroupRatesDTO> getDistributionKindGroupRatesDTOByDKG(int distributionKindGroup);
	
	
}
