package za.co.liberty.web.pages.maintainagreement;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.apache.wicket.markup.html.panel.Panel;

import za.co.liberty.business.agreement.IAgreementManagement;
import za.co.liberty.business.guicontrollers.agreement.IAgreementGUIController;
import za.co.liberty.dto.agreement.AgreementDTO;
import za.co.liberty.dto.agreement.AgreementHomeRoleDTO;
import za.co.liberty.dto.agreement.AgreementRoleDTO;
import za.co.liberty.dto.agreement.AssociatedAgreementsDetailsWrapperDTO;
import za.co.liberty.dto.agreement.IncentiveDetailDTO;
import za.co.liberty.dto.agreement.PaymentSchedulerDTO;
import za.co.liberty.dto.agreement.maintainagreement.ActivateAgreementDTO;
import za.co.liberty.dto.agreement.maintainagreement.AgreementIncentivesRequestConfigurationDTO;
import za.co.liberty.dto.agreement.maintainagreement.BaseAgreementRequestConfigurationDTO;
import za.co.liberty.dto.agreement.maintainagreement.CreateAgreementRequestConfigurationDTO;
import za.co.liberty.dto.agreement.maintainagreement.MaintainAgreementDTO;
import za.co.liberty.dto.agreement.maintainagreement.MaintainFAISLicenseDTO;
import za.co.liberty.dto.agreement.maintainagreement.MaintainFAISLicenseDTOOLD;
import za.co.liberty.dto.agreement.maintainagreement.ValidAgreementValuesDTO;
import za.co.liberty.dto.agreement.properties.FAISLicenseDTO;
import za.co.liberty.dto.gui.request.ViewRequestModelDTO;
import za.co.liberty.dto.party.PartyDTO;
import za.co.liberty.dto.party.PersonDTO;
import za.co.liberty.exceptions.data.DataNotFoundException;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.exceptions.security.TabAccessException;
import za.co.liberty.helpers.lookup.ServiceLocator;
import za.co.liberty.interfaces.agreements.IAgreementDetailFLO;
import za.co.liberty.interfaces.agreements.RoleKindType;
import za.co.liberty.interfaces.agreements.requests.RequestKindType;
import za.co.liberty.interfaces.party.PartyStatusType;
import za.co.liberty.srs.type.SRSType;
import za.co.liberty.web.pages.contactdetail.ContactDetailsPanel;
import za.co.liberty.web.pages.maintainagreement.model.AdvisorQualityCodePanelModel;
import za.co.liberty.web.pages.maintainagreement.model.AgencyPoolAccountDetailsPanelModel;
import za.co.liberty.web.pages.maintainagreement.model.AgreementCodePanelModel;
import za.co.liberty.web.pages.maintainagreement.model.AgreementDetailsPanelModel;
import za.co.liberty.web.pages.maintainagreement.model.DistributionPanelModel;
import za.co.liberty.web.pages.maintainagreement.model.FAISLicensePanelModel;
import za.co.liberty.web.pages.maintainagreement.model.FAISLicensePanelModelOLD;
import za.co.liberty.web.pages.maintainagreement.model.FixedDeductionsPanelModel;
import za.co.liberty.web.pages.maintainagreement.model.FixedEarningsPanelModel;
import za.co.liberty.web.pages.maintainagreement.model.FranchisePoolAccountPanelModel;
import za.co.liberty.web.pages.maintainagreement.model.MaintainAgreementPageModel;
import za.co.liberty.web.pages.maintainagreement.model.MaintainIncentivePanelModel;
import za.co.liberty.web.pages.maintainagreement.model.PaysToPanelModel;
import za.co.liberty.web.pages.maintainagreement.model.ProvidentFundDetailsPanelModel;
import za.co.liberty.web.pages.maintainagreement.payroll.FixedDeductionsPanel;
import za.co.liberty.web.pages.maintainagreement.payroll.FixedEarningsPanel;
import za.co.liberty.web.pages.party.BankingDetailsPanel;
import za.co.liberty.web.pages.party.MedicalAidDetailsPanel;
import za.co.liberty.web.pages.party.OrganisationDetailsPanel;
import za.co.liberty.web.pages.party.PersonDetailsPanel;
import za.co.liberty.web.pages.party.model.MaintainPartyPageModel;
import za.co.liberty.web.pages.party.model.MedicalAidDetailsPanelModel;
import za.co.liberty.web.pages.request.BaseRequestViewAndAuthorisePanel;

/**
 * Auth panel for Agreement Hierarchy
 * @author DZS2610
 *
 */
public class MaintainAgreementAuthorisationPanel extends BaseRequestViewAndAuthorisePanel {
	
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(MaintainAgreementAuthorisationPanel.class);
	
	private transient IAgreementGUIController agreementGUIController;
	
	/**
	 * Default constructor 
	 * 
	 * @param id
	 * @param pageModel
	 * @param editState
	 * @throws TabAccessException 
	 */
	public MaintainAgreementAuthorisationPanel(String id, 
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
		List<Panel> panelList = new ArrayList<Panel>();		
		// Process Party Details
		if (requestKindList.contains(RequestKindType.MaintainPartyDetails)
				|| requestKindList.contains(RequestKindType.MaintainPartyWithApproval)) {
			MaintainPartyPageModel model = initialisePartyModel(imageObject);
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
			MaintainPartyPageModel model = initialisePartyModel(imageObject);
			Panel panel = new ContactDetailsPanel(id,
					model.getPartyDTO().getContactPreferences().getContactPreferences(), 
					model.getPartyDTO().getCommunicationPreferences(), 
					getEditState(), null,false,null, false);
			panelList.add(panel);
		}
		
		if (requestKindList.contains(RequestKindType.MaintainMedicalAidDetails)) {
			CreateAgreementRequestConfigurationDTO config = (CreateAgreementRequestConfigurationDTO) imageObject;	
			MedicalAidDetailsPanelModel model = new MedicalAidDetailsPanelModel();
			model.setCurrentPartyID(config.getMedicalAidDetailDTO().getPartyoid());
			model.setCurrentPartyType(SRSType.PERSON);//always person
			model.setMedicalAidDetail(config.getMedicalAidDetailDTO());				
			Panel panel = new MedicalAidDetailsPanel(id,getEditState(),
					model,null);
			panelList.add(panel);
		}
		
		//Process BankinG details
		if (requestKindList.contains(RequestKindType.MaintainPaymentDetails)) {
			MaintainPartyPageModel model = initialisePartyModel(imageObject);
			Panel panel = new BankingDetailsPanel(id,model,getEditState(), null, null);
				panelList.add(panel);}
		
		
		if (requestKindList.contains(RequestKindType.MaintainAgreementHierarchy)
				|| requestKindList.contains(RequestKindType.BranchTransfer)
				|| requestKindList.contains(RequestKindType.MaintainAgreementServicingRelationships)
				|| requestKindList.contains(RequestKindType.MaintainAgreementHome)
				|| requestKindList.contains(RequestKindType.MaintainAgreementSupervisors)) {
				MaintainAgreementPageModel model = initialiseAgreementHierarchyModel(imageObject);
				Panel panel = new AgreementHierarchyPanel(id, model, getEditState(),null,null);
				panelList.add(panel);			
		} 
		if (requestKindList.contains(RequestKindType.MaintainPaymentSchedulerDetails)||
				requestKindList.contains(RequestKindType.MaintainPaymentSchedulerDetailsWithApproval)) {
				MaintainAgreementPageModel model = initialisePaymentSchedulerModel(imageObject);

				long agreementNumber =0;
			if (getPageModel() != null
					&& getPageModel().getViewRequestContextDto() != null
					&& getPageModel().getViewRequestContextDto()
							.getRequestDto() != null) {
				agreementNumber = getPageModel().getViewRequestContextDto()
						.getRequestDto().getAgreementNr();
			}
				
			Panel panel = new PaymentSchedulerPanel(id, model, getEditState(),
					null, null, agreementNumber);
			panelList.add(panel);				
			}
		
		if (requestKindList.contains(RequestKindType.MaintainAssociatedAgreements)) {
			MaintainAgreementPageModel model = initialiseAssociatedAgmtModel(imageObject);
			Panel panel = new AssociatedAgreementsPanel(id, model, null,getEditState(),null);
			panelList.add(panel);			
		}
		
		if (requestKindList.contains(RequestKindType.MaintainPoolAccount)) {
			if (imageObject instanceof BaseAgreementRequestConfigurationDTO) {
				BaseAgreementRequestConfigurationDTO baseConfig = (BaseAgreementRequestConfigurationDTO)imageObject;
				FranchisePoolAccountPanelModel panelModel = 
					new FranchisePoolAccountPanelModel(baseConfig.getMaintainPoolAccount());
				panelList.add(new FranchisePoolAccountPanel(id,getEditState(),panelModel));
			}
		}
		
		if (requestKindList.contains(RequestKindType.ActivateAgreement)) {
			/**
			 * Add pays to for activate
			 */
			if (imageObject instanceof CreateAgreementRequestConfigurationDTO) {
				CreateAgreementRequestConfigurationDTO baseConfig = 
					(CreateAgreementRequestConfigurationDTO)imageObject;
				PaysToPanelModel panelModel = 
					new PaysToPanelModel(baseConfig.getActivateAgreementDTO().getMaintainPaysTo());
				panelList.add(new PaysToPanel(id,getEditState(),panelModel, null));
			}
			/**
			 * Add pool account for activate
			 */
			if (imageObject instanceof CreateAgreementRequestConfigurationDTO) {
				CreateAgreementRequestConfigurationDTO baseConfig = 
					(CreateAgreementRequestConfigurationDTO)imageObject;
				if (baseConfig.getActivateAgreementDTO().getMaintainPoolAccount()!=null) {
					FranchisePoolAccountPanelModel panelModel = 
						new FranchisePoolAccountPanelModel(
								baseConfig.getActivateAgreementDTO().getMaintainPoolAccount());
					panelList.add(new FranchisePoolAccountPanel(id,getEditState(),panelModel));
				}
			}
			/**
			 * add payment scheduler for activate
			 */
			if (imageObject instanceof CreateAgreementRequestConfigurationDTO) {
				CreateAgreementRequestConfigurationDTO baseConfig = 
					(CreateAgreementRequestConfigurationDTO)imageObject;
				if (baseConfig.getActivateAgreementDTO().getMaintainPaymentScheduler()!=null) {
					MaintainAgreementPageModel model = createPaymentSchedulerModel(
							baseConfig.getActivateAgreementDTO().getMaintainPaymentScheduler());
					Panel panel = new PaymentSchedulerPanel(id, model, getEditState(),null,null);
					panelList.add(panel);			
				}
			}
			
		}
		
		if (requestKindList.contains(RequestKindType.MaintainIntermediaryAgreement) ||
				requestKindList.contains(RequestKindType.CreateAgreement) ||
				requestKindList.contains(RequestKindType.TerminateIntermediaryAgreement)||
				requestKindList.contains(RequestKindType.MaintainCorpCommIndicator) ||
				// SSM2707 Added for hierarchy FR3.5 PRimary Agreement SWETA MENON
				requestKindList.contains(RequestKindType.MaintainPrimaryAgreement)) {
			if (imageObject instanceof BaseAgreementRequestConfigurationDTO) {
				BaseAgreementRequestConfigurationDTO baseConfig = (BaseAgreementRequestConfigurationDTO)imageObject;
				AgreementDetailsPanelModel panelModel = 
					new AgreementDetailsPanelModel(baseConfig.getMaintainAgreementDTO());
				panelList.add(new AgreementDetailsPanel(id,getEditState(),panelModel));
			}
			// SSM2707 Added for hierarchy FR3.5 PRimary Agreement SWETA MENON Begin
			if (requestKindList
					.contains(RequestKindType.MaintainPrimaryAgreement)
					&& imageObject instanceof ActivateAgreementDTO) {
				ActivateAgreementDTO baseConfig = (ActivateAgreementDTO) imageObject;
				AgreementDetailsPanelModel panelModel = new AgreementDetailsPanelModel(
						baseConfig);
				panelList.add(new AgreementDetailsPanel(id, getEditState(),
						panelModel));
			}
			// SSM2707 Added for hierarchy FR3.5 PRimary Agreement SWETA MENON End
			
		}
		
		if (requestKindList.contains(RequestKindType.MaintainAgreementCodes)) {
			if (imageObject instanceof BaseAgreementRequestConfigurationDTO) {
				BaseAgreementRequestConfigurationDTO baseConfig = (BaseAgreementRequestConfigurationDTO)imageObject;
				AgreementCodePanelModel panelModel = 
					new AgreementCodePanelModel(baseConfig.getMaintainAgreementDTO());
				panelList.add(new AgreementCodesPanel(id,getEditState(),panelModel));
			}
		}
		
		if (requestKindList.contains(RequestKindType.MaintainPaysTo)) {
			if (imageObject instanceof BaseAgreementRequestConfigurationDTO) {
				BaseAgreementRequestConfigurationDTO baseConfig = (BaseAgreementRequestConfigurationDTO)imageObject;
				PaysToPanelModel panelModel = 
					new PaysToPanelModel(baseConfig.getMaintainPaysTo());
				panelList.add(new PaysToPanel(id,getEditState(),panelModel, null));
			}
		}
		
		if (requestKindList.contains(RequestKindType.MaintainDistributionDetails)) {
			if (imageObject instanceof BaseAgreementRequestConfigurationDTO) {
				int agmKind = 0;
				BaseAgreementRequestConfigurationDTO baseConfig = (BaseAgreementRequestConfigurationDTO)imageObject;
				if (baseConfig.getMaintainAgreementDTO() != null &&  baseConfig.getMaintainAgreementDTO().getAgreementDTO() != null) {
					agmKind =  baseConfig.getMaintainAgreementDTO().getAgreementDTO().getKind();
				} else {
					agmKind = getPageModel().getViewRequestContextDto().getAgreementDto().getAgreementDivision().getKind();
					
				}
				
				//int agmKind =  baseConfig.getMaintainAgreementDTO().getAgreementDTO().getKind();
				DistributionPanelModel panelModel = 
					new DistributionPanelModel(baseConfig.getMaintainDistributionTemplate(), agmKind);
				panelList.add(new DistributionPanel(id,getEditState(),panelModel, agmKind));
			}
		}
		
		if (requestKindList.contains(RequestKindType.MaintainFixedDeductionDetails)) {
			if (imageObject instanceof BaseAgreementRequestConfigurationDTO) {
				BaseAgreementRequestConfigurationDTO baseConfig = (BaseAgreementRequestConfigurationDTO)imageObject;
				FixedDeductionsPanelModel panelModel = 
					new FixedDeductionsPanelModel(baseConfig.getMaintainFixedDeductions());
				panelList.add(new FixedDeductionsPanel(id,getEditState(),panelModel));
			}
		}
		
		if (requestKindList.contains(RequestKindType.MaintainFixedEarningDetails)) {
			if (imageObject instanceof BaseAgreementRequestConfigurationDTO) {
				BaseAgreementRequestConfigurationDTO baseConfig = (BaseAgreementRequestConfigurationDTO)imageObject;
				FixedEarningsPanelModel panelModel = 
					new FixedEarningsPanelModel(baseConfig.getMaintainFixedEarnings());
				panelList.add(new FixedEarningsPanel(id,getEditState(),panelModel));
			}
		}
		
		if (requestKindList.contains(RequestKindType.MaintainFAISLicense)) {
			if ((imageObject instanceof BaseAgreementRequestConfigurationDTO 
					&& ((BaseAgreementRequestConfigurationDTO)imageObject).getMaintainFAISLicenseDTOOLD() != null)
					|| imageObject instanceof MaintainFAISLicenseDTOOLD) {				
				MaintainFAISLicenseDTOOLD mntlicence = null;
				if(imageObject instanceof BaseAgreementRequestConfigurationDTO){
					BaseAgreementRequestConfigurationDTO baseConfig = (BaseAgreementRequestConfigurationDTO)imageObject;
					mntlicence = baseConfig.getMaintainFAISLicenseDTOOLD();
				}else{
					mntlicence = (MaintainFAISLicenseDTOOLD) imageObject;
				}	
//				get the current licence from the db and change the code
				try {
					FAISLicensePanelModelOLD panelModel = 
						new FAISLicensePanelModelOLD(mntlicence);
					panelList.add(new FAISLicensePanelOLD(id,getEditState(),panelModel));
				} catch (Exception e) {	
					logger.error("Error occured while displaying request history for agreement " + getTargetAgreementNumber() + " msg : " + e.getMessage(),e);
					//should never happen but display error if it does
					throw new CommunicationException(e);
				}
			}else{			
				long agmt = getTargetAgreementNumber();
				
				Long belongsToagmtNumber = null;
				try {
					belongsToagmtNumber = getAgreementGUIController().getBelongsToAgreement(agmt);
				} catch (DataNotFoundException e1) {
					//do nothing, no belongs to found
				}
				int kind = 0;
				Date agreementStartdate = null;
				try {
					IAgreementDetailFLO flo = getAgreementGUIController().getAgreementsSimpleDetail(agmt);
					if(flo != null){
						Long knd = flo.getKind();
						agreementStartdate = flo.getStartDate();
						if(knd != null){
							kind = knd.intValue();
						}
					}
				} catch (DataNotFoundException e1) {
					//do nothing, no kind found so we send through 0
				}
				
				
				
				if (imageObject instanceof BaseAgreementRequestConfigurationDTO) {
					BaseAgreementRequestConfigurationDTO baseConfig = (BaseAgreementRequestConfigurationDTO)imageObject;
					FAISLicensePanelModel panelModel = 
						new FAISLicensePanelModel(baseConfig.getMaintainFAISLicenseDTO(),belongsToagmtNumber, kind,agmt,agreementStartdate);				
					panelList.add(new FAISLicensePanel(id,getEditState(),panelModel));
					
				}else if (imageObject instanceof MaintainFAISLicenseDTO) {
					MaintainFAISLicenseDTO baseConfig = (MaintainFAISLicenseDTO)imageObject;
					FAISLicensePanelModel panelModel = 
						new FAISLicensePanelModel(baseConfig,belongsToagmtNumber, kind,agmt,agreementStartdate);
					panelList.add(new FAISLicensePanel(id,getEditState(),panelModel));
				}
			}
		}
		if (requestKindList.contains(RequestKindType.MaintainFAISLicenseStatus)) {
			long agmt = getTargetAgreementNumber();
			Long belongsToagmtNumber = null;
			try {
				belongsToagmtNumber = getAgreementGUIController().getBelongsToAgreement(agmt);
			} catch (DataNotFoundException e1) {
				//do nothing, no belongs to found
			}			
			int kind = 0;
			Date agreementStartdate = null;
			try {
				IAgreementDetailFLO flo = getAgreementGUIController().getAgreementsSimpleDetail(agmt);
				if(flo != null){
					Long knd = flo.getKind();
					agreementStartdate = flo.getStartDate();
					if(knd != null){
						kind = knd.intValue();
					}
				}
			} catch (DataNotFoundException e1) {
				//do nothing, no kind found so we send through 0
			}
			if ((imageObject instanceof BaseAgreementRequestConfigurationDTO 
					&& ((BaseAgreementRequestConfigurationDTO)imageObject).getMaintainFAISLicenseDTO() != null)
					|| imageObject instanceof MaintainFAISLicenseDTO) {				
				MaintainFAISLicenseDTO mntlicence = null;
				if(imageObject instanceof BaseAgreementRequestConfigurationDTO){
					BaseAgreementRequestConfigurationDTO baseConfig = (BaseAgreementRequestConfigurationDTO)imageObject;
					mntlicence = baseConfig.getMaintainFAISLicenseDTO();
				}else{
					mntlicence = (MaintainFAISLicenseDTO) imageObject;
				}
				
				//only need the status code
				long statusCodeChange = mntlicence.getFaisLicenceNewStatusCode();				
				//get the current licence from the db and change the code
				try {
					IAgreementManagement agmtManagment = ServiceLocator.lookupService(IAgreementManagement.class);
					FAISLicenseDTO licence = agmtManagment.getFAISLicenceDTOForAgreementNumber(getTargetAgreementNumber());	
					if(licence == null){
						logger.error("Error displaying request history as a licence could not be found for agreement " + getTargetAgreementNumber());
						throw new DataNotFoundException("A licence could not be found for agreement " + getTargetAgreementNumber());
					}
//					change the licence to the new status
					PartyStatusType statusType = PartyStatusType.getStatusWithCode((short)statusCodeChange);					
					licence.setFaisLicenseStatus(statusType);
					MaintainFAISLicenseDTO model  = new MaintainFAISLicenseDTO();
					model.setRequestDTO(licence);				
					FAISLicensePanelModel panelModel = new FAISLicensePanelModel(model,belongsToagmtNumber,kind,agmt,agreementStartdate);
					panelList.add(new FAISLicensePanel(id,getEditState(),panelModel));
				} catch (Exception e) {	
					logger.error("Error occured while displaying request history for agreement " + getTargetAgreementNumber() + " msg : " + e.getMessage(),e);
					//should never happen but display error if it does
					throw new CommunicationException(e);
				}
			}			
		}		
		//Added for Differential Pricing-pks2802-07/10/10
		if (requestKindList.contains(RequestKindType.MaintainAdvisorQualityCode) || requestKindList.contains(RequestKindType.MaintainMaxUpfrontCommPercent)) {
			if (imageObject instanceof BaseAgreementRequestConfigurationDTO) {
				BaseAgreementRequestConfigurationDTO baseConfig = (BaseAgreementRequestConfigurationDTO)imageObject;
				AdvisorQualityCodePanelModel panelModel = 
					new AdvisorQualityCodePanelModel(baseConfig.getMaintainAQCDetailsDTO());
				panelList.add(new AdvisorQualityCodePanel(id,getEditState(),panelModel));
			}
		}
		if (requestKindList.contains(RequestKindType.MaintainIncentiveDetails)) {
			//new incetive screen
			AgreementIncentivesRequestConfigurationDTO requestObject = null;			
			if (imageObject instanceof BaseAgreementRequestConfigurationDTO) {
				BaseAgreementRequestConfigurationDTO baseConfig = (BaseAgreementRequestConfigurationDTO)imageObject;
				requestObject = baseConfig.getAgreementIncentives();
			}else if(imageObject instanceof AgreementIncentivesRequestConfigurationDTO){
				requestObject = (AgreementIncentivesRequestConfigurationDTO) imageObject;				
			}
			MaintainIncentivePanelModel panelModel = 
				new MaintainIncentivePanelModel();
			panelModel.setAgreementnumber(getTargetAgreementNumber());
			panelModel.setAvailableIncentivesDetails(new ArrayList<IncentiveDetailDTO>());
			panelModel.setCurrentAndfutureIncentiveDetails(requestObject.getIncentiveDetails());			
			panelList.add(new AgreementIncentivesPanel(id,panelModel,getEditState(),null,null));
					
		}		
		
		if (requestKindList.contains(RequestKindType.MaintainProvidentFundOptions) ||
				requestKindList.contains(RequestKindType.MaintainProvidentFundBeneficiaries)) {
			if (imageObject instanceof BaseAgreementRequestConfigurationDTO) {
				BaseAgreementRequestConfigurationDTO baseConfig = (BaseAgreementRequestConfigurationDTO)imageObject;
				ProvidentFundDetailsPanelModel panelModel = 
					new ProvidentFundDetailsPanelModel(baseConfig.getMaintainAgreementDTO());
				panelList.add(new ProvidentFundDetailsPanel(id,getEditState(),panelModel));
			}			
		}
		
		
		// SSM2707 Sweta Menon Agency Pool Begin
		if (requestKindList.contains(RequestKindType.CloseAgencyPool) ||
				requestKindList.contains(RequestKindType.StopAgencyPoolTransfer) ||
				requestKindList.contains(RequestKindType.AdhocAgencyPoolDraw)||
				requestKindList.contains(RequestKindType.SetOverrideIntoPoolRate)) {
			if (imageObject instanceof BaseAgreementRequestConfigurationDTO) {
				BaseAgreementRequestConfigurationDTO baseConfig = (BaseAgreementRequestConfigurationDTO)imageObject;
				AgencyPoolAccountDetailsPanelModel panelModel = 
					new AgencyPoolAccountDetailsPanelModel(baseConfig.getMaintainAgencyPoolDetail(),0);
				panelList.add(new AgencyPoolAccountDetailsPanel(id,getEditState(),panelModel));
			}
		}
		// SSM2707 Sweta Menon Agency Pool End
		return panelList;
	}
	
	/**
	 * Initialise the model for Associated Agreements
	 * 
	 * @param currentImage
	 * @return
	 */
	
	private MaintainAgreementPageModel initialiseAssociatedAgmtModel(Object imageObject) {
		BaseAgreementRequestConfigurationDTO obj = (BaseAgreementRequestConfigurationDTO) imageObject;
		AgreementDTO dto = new AgreementDTO();
		MaintainAgreementDTO mDTO = new MaintainAgreementDTO();
		mDTO.setAgreementDTO(dto);
		
		if(obj.getMaintainAssociatedAgreementsWrapper() != null){
			
			AssociatedAgreementsDetailsWrapperDTO wrapperDTO = obj.getMaintainAssociatedAgreementsWrapper().getRequestDTO();
			dto.setAssociatedAgreementDetailsList(wrapperDTO.getAssociatedAgmtList());
		}		
		
		dto.setStartDate(new Date());
		MaintainAgreementPageModel model = new MaintainAgreementPageModel(dto,new ValidAgreementValuesDTO());
		model.setSelectedItem(mDTO);	    	    
		return model;
	}
	
	/**
	 * Initialise the model
	 * 
	 * @param currentImage
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected MaintainAgreementPageModel initialiseAgreementHierarchyModel(Object imageObject) {
		BaseAgreementRequestConfigurationDTO obj = (BaseAgreementRequestConfigurationDTO) imageObject;
		AgreementDTO dto = new AgreementDTO();
		MaintainAgreementDTO mDTO = new MaintainAgreementDTO();
		mDTO.setAgreementDTO(dto);
		MaintainAgreementPageModel model = new MaintainAgreementPageModel(dto,new ValidAgreementValuesDTO());
		dto.setCurrentAndFutureAgreementRoles(new ArrayList<AgreementRoleDTO>());
		dto.setCurrentAndFutureHomeRoles(new ArrayList<AgreementHomeRoleDTO>());
		dto.setCurrentAndFutureSupervisorRoles(new ArrayList<AgreementRoleDTO>());
		if(obj.getAgreementHomeRoles() != null && obj.getAgreementHomeRoles().getAgreementRoles() != null){
			dto.setCurrentAndFutureHomeRoles((List)obj.getAgreementHomeRoles().getAgreementRoles());
		}
		if(obj.getAgreementServicingRoles() != null && obj.getAgreementServicingRoles().getAgreementRoles() != null){
			dto.getCurrentAndFutureAgreementRoles().addAll(obj.getAgreementServicingRoles().getAgreementRoles());
		}
		if(obj.getOtherAgreementRoles() != null && obj.getOtherAgreementRoles().getAgreementRoles() != null){
			for (AgreementRoleDTO agreementRoleDTO:obj.getOtherAgreementRoles().getAgreementRoles())
			{
			if (agreementRoleDTO.getAgreementRoleKind()!=RoleKindType.HASBUSINESSSTAKEHOLDER.getKind()){
			dto.getCurrentAndFutureAgreementRoles().add(agreementRoleDTO);
				}
			}
		}
		if((obj.getSupervisionAgreementRoles() != null && obj.getSupervisionAgreementRoles().getAgreementRoles() != null)||
				(obj.getOtherAgreementRoles() != null && obj.getOtherAgreementRoles().getAgreementRoles() != null)){
			if (obj.getSupervisionAgreementRoles() != null && obj.getSupervisionAgreementRoles().getAgreementRoles() != null)
			{
			dto.getCurrentAndFutureSupervisorRoles().addAll(obj.getSupervisionAgreementRoles().getAgreementRoles());
			}
			if (obj.getOtherAgreementRoles() != null && obj.getOtherAgreementRoles().getAgreementRoles() != null){
			for (AgreementRoleDTO agreementRoleDTO:obj.getOtherAgreementRoles().getAgreementRoles())
			{
				if (agreementRoleDTO.getAgreementRoleKind()==RoleKindType.HASBUSINESSSTAKEHOLDER.getKind()){
			dto.getCurrentAndFutureSupervisorRoles().add(agreementRoleDTO);
				}
			}
			}
		}

		 dto.setStartDate(new Date());
		model.setSelectedItem(mDTO);	    	    
		return model;
	}
	
	/**
	 * Initialise the model for Payment Scheduler
	 * 
	 * @param currentImage
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected MaintainAgreementPageModel initialisePaymentSchedulerModel(Object imageObject) {
		BaseAgreementRequestConfigurationDTO obj = (BaseAgreementRequestConfigurationDTO) imageObject;
		PaymentSchedulerDTO paymentScheduler = obj.getMaintainPaymentScheduler().getRequestDTO();
		MaintainAgreementPageModel model = createPaymentSchedulerModel(paymentScheduler);	    	    
		return model;
	}

	private MaintainAgreementPageModel createPaymentSchedulerModel(PaymentSchedulerDTO paymentScheduler) {
		AgreementDTO dto = new AgreementDTO();
		MaintainAgreementDTO mDTO = new MaintainAgreementDTO();
		mDTO.setAgreementDTO(dto);
		if(paymentScheduler != null) {
			dto.setPaymentSchedulerDTO(paymentScheduler);
		}
		dto.setStartDate(new Date());
		MaintainAgreementPageModel model = new MaintainAgreementPageModel(dto,new ValidAgreementValuesDTO());
		model.setSelectedItem(mDTO);
		return model;
	}
	
	/**
	 * Initialise the model
	 * 
	 * @param currentImage
	 * @return
	 */
	protected MaintainPartyPageModel initialisePartyModel(Object imageObject) {
		CreateAgreementRequestConfigurationDTO config = (CreateAgreementRequestConfigurationDTO) imageObject;
		MaintainPartyPageModel model = new MaintainPartyPageModel();
		if(config.getPartyDTO() != null){
			config.getPartyDTO().setContactPreferences(config.getContactDetails());
			model.setPartyDTO(config.getPartyDTO());
			model.setSelectedItem(config.getPartyDTO());			
		}else{
			PartyDTO party = new PartyDTO();
			party.setContactPreferences(config.getContactDetails());
			model.setPartyDTO(party);
			model.setSelectedItem(party);
		}
		if(config.getBankingDetailWrapperDTO() != null)
			model.getPartyDTO().setBankingDetailsDTO(config.getBankingDetailWrapperDTO().getRequestDTO());
		
		return model;
	}
	
	
	private IAgreementGUIController getAgreementGUIController(){
		if(agreementGUIController == null){
			try {
				agreementGUIController = ServiceLocator.lookupService(IAgreementGUIController.class);
			} catch (NamingException e) {
				throw new CommunicationException(e);
			}
		}
		return agreementGUIController;
	}
}