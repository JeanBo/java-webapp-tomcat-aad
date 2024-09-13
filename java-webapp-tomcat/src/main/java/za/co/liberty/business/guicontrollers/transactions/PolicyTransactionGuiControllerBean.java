package za.co.liberty.business.guicontrollers.transactions;

import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.CreateException;
import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.log4j.Logger;

import za.co.liberty.agreement.client.vo.AgreementRoleVO;
import za.co.liberty.agreement.client.vo.RequestVO;
import za.co.liberty.agreement.client.vo.RequestVOImpl;
import za.co.liberty.agreement.common.enums.RequestKindEnumeration;
import za.co.liberty.agreement.common.enums.RequestStatusEnumeration;
import za.co.liberty.agreement.common.enums.RoleKindEnumeration;
import za.co.liberty.agreement.common.exceptions.KindNotFoundException;
import za.co.liberty.agreement.common.exceptions.OnlyActualNotFoundException;
import za.co.liberty.agreement.process.AgreementBPOHome;
import za.co.liberty.agreement.process.AgreementManagerBPOHome;
import za.co.liberty.business.agreement.IValidAgreementValuesFactory;
import za.co.liberty.business.dpe.helper.DPEBenefitGroupHelper;
import za.co.liberty.business.pretransactionreject.IPreTransactionRejectManagement;
import za.co.liberty.business.request.IRequestEnquiryManagement;
import za.co.liberty.business.request.IRequestManagement;
import za.co.liberty.common.domain.ApplicationContext;
import za.co.liberty.common.domain.CurrencyAmount;
import za.co.liberty.dto.agreement.request.RequestEnquiryDPERowDTO;
import za.co.liberty.dto.agreement.request.RequestEnquiryResultDTO;
import za.co.liberty.dto.agreement.request.RequestEnquirySearchDTO;
import za.co.liberty.dto.gui.context.InfoKindType;
import za.co.liberty.dto.gui.request.FundCodeDTO;
import za.co.liberty.dto.gui.request.ProductCodeDTO;
import za.co.liberty.dto.gui.templates.DescriptionDTO;
import za.co.liberty.dto.transaction.DistributePolicyEarningDTO;
import za.co.liberty.dto.transaction.IPolicyTransactionDTO;
import za.co.liberty.dto.transaction.IPolicyTransactionModel;
import za.co.liberty.dto.transaction.PolicyInfoCommissionCalculationDTO;
import za.co.liberty.dto.transaction.PolicyInfoCommissionCalculationDTO.PolicyInfoListDTO;
import za.co.liberty.dto.transaction.RecordPolicyInfoDTO;
import za.co.liberty.exceptions.data.DataNotFoundException;
import za.co.liberty.exceptions.data.QueryTimeoutException;
import za.co.liberty.exceptions.data.ValidationException;
import za.co.liberty.exceptions.error.request.RequestException;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.interfaces.agreements.PolicyInfoKindType;
import za.co.liberty.interfaces.agreements.ProductKindType;
import za.co.liberty.interfaces.agreements.requests.PropertyKindType;
import za.co.liberty.interfaces.agreements.requests.RequestKindType;
import za.co.liberty.interfaces.persistence.agreement.request.IRequestEnquiryDPERow;
import za.co.liberty.interfaces.persistence.agreement.request.IRequestEnquiryRow;
import za.co.liberty.interfaces.rating.description.DescriptionKindType;
import za.co.liberty.interfaces.reporting.ProductReferenceType;
import za.co.liberty.persistence.rating.FundCodeEntityManager;
import za.co.liberty.persistence.rating.IDescriptionEntityManager;
import za.co.liberty.persistence.rating.ProductCodesEntityManager;
import za.co.liberty.persistence.rating.entity.Description;
import za.co.liberty.persistence.rating.entity.FundCodes;
import za.co.liberty.persistence.rating.entity.ProductCodes;
import za.co.liberty.persistence.rating.flo.ProductCodeFLO;
import za.co.liberty.srs.type.SRSType;
import za.co.liberty.srs.util.agreement.PropertyKind;

/**
 *  
 * TODO - Move the initialisation of the model to {@linkplain RequestTransactionGuiController}, then remove this bean.
 *
 */
public class PolicyTransactionGuiControllerBean implements IPolicyTransactionGuiController {

	
	private static final long serialVersionUID = 1L;
	
	Logger logger = Logger.getLogger(PolicyTransactionGuiControllerBean.class);
	
	
	private static List<DescriptionDTO> premiumFrequencyForPolicyInfoList;
	private static Map<Long, List<ProductCodeDTO>> productCodePerTypeForPolicyMap;
	private static Map<Long, List<FundCodeDTO>> fundCodesPerTypeForPolicyMap;
	
	
	private static Map<String, List<FundCodeDTO>> fundCodesListForBatch = new HashMap<String, List<FundCodeDTO>>();
	private static List<FundCodeDTO> allFundCodes;
	private static List<DescriptionDTO> productReferences;
	private static List<DescriptionDTO> fundCodeCategories;

	
	public List<FundCodeDTO> getAllFundCodes() {
		
		if(allFundCodes == null) {
			allFundCodes = new ArrayList<FundCodeDTO>();

			int cnt =0;
			FundCodeDTO fundCodeDTO = null;
	
			for (String batch : new String[] {"GBNK","LIBLISP","STANGBNK","STANWEALTH" }) {
				fundCodeDTO = new FundCodeDTO();				
				fundCodeDTO.setBatch(batch);
				fundCodeDTO.setFundCode("T001");
				fundCodeDTO.setFundDescription(batch + "-"+fundCodeDTO.getFundCode()+"Fund1");
				fundCodeDTO.setId(1L + cnt++);
				fundCodeDTO.setReference(1L);
				fundCodeDTO.setProductCode(0);
				fundCodeDTO.setCategory(null);
				allFundCodes.add(fundCodeDTO);
				
				fundCodeDTO = new FundCodeDTO();				
				fundCodeDTO.setBatch(batch);
				fundCodeDTO.setFundCode("T002");
				fundCodeDTO.setFundDescription(batch + "-"+fundCodeDTO.getFundCode()+"Fund2");
				fundCodeDTO.setId(1L + cnt++);
				fundCodeDTO.setReference(2L);
				fundCodeDTO.setProductCode(0);
				fundCodeDTO.setCategory(null);
				allFundCodes.add(fundCodeDTO);
				
				fundCodeDTO = new FundCodeDTO();				
				fundCodeDTO.setBatch(batch);
				fundCodeDTO.setFundCode("T003");
				fundCodeDTO.setFundDescription(batch + "-"+fundCodeDTO.getFundCode()+"Fund3");
				fundCodeDTO.setId(1L + cnt++);
				fundCodeDTO.setReference(1L);
				fundCodeDTO.setProductCode(0);
				fundCodeDTO.setCategory(null);
				allFundCodes.add(fundCodeDTO);
			}
		
		}
		
		return allFundCodes;
	}
	
	public ProductCodeDTO getAUMProductCodeById(Long productCodeId){
		ProductCodeDTO productCodeDTO  = null;
		
//		if(productCodeId != null){
//			ProductCodes productCode =productCodesEntityManager.getProductCodeById(productCodeId); 
//			if(productCode != null){
//				productCodeDTO = new ProductCodeDTO();
//				productCodeDTO.setBatch(productCode.getBatch());
//				productCodeDTO.setId(productCode.getId());
//				productCodeDTO.setProductCode(productCode.getProductCode());
//				productCodeDTO.setProductDescription(productCode.getProductDescription());
//				productCodeDTO.setReference((long) productCode.getReference());
//				productCodeDTO.setProductType(productCode.getLibertyProduct());
//			}
//		}
		
		return productCodeDTO;
	}

	/**
	 * Get the type of product types for a policy info kind type.
	 * 
	 * @param infoKindType
	 * @return
	 */
	public List<ProductCodeDTO> getPolicyInfoProductReferencesForInfoKindType(long infoKindType) {
		if (productCodePerTypeForPolicyMap==null) {
			Map<Long, List<ProductCodeDTO>> tmp = new HashMap<Long, List<ProductCodeDTO>>();
			for (ProductCodeDTO p : getAllPolicyInfoProductReferences()) {
				List<ProductCodeDTO> list = tmp.get(p.getProductType());
				if (list==null) {
					list = new ArrayList<ProductCodeDTO>();
					tmp.put(p.getProductType(), list);
					if (logger.isDebugEnabled())
						logger.debug("Add infoKind to prod/type map = " + p.getProductType());
				}
				list.add(p);
			}
			productCodePerTypeForPolicyMap = tmp;
		}
		PolicyInfoKindType kindTypeEnum = PolicyInfoKindType.getPolicyInfoKindWithType(infoKindType);
		if (kindTypeEnum!=null) {	
			// We use the parent grouping except if the Enum type indicate that we
			//     shouldn't.  
			List<ProductCodeDTO> lst = productCodePerTypeForPolicyMap.get(
					(kindTypeEnum.getParent()!=null && kindTypeEnum.isUseParent())
						? kindTypeEnum.getParent().getType() : infoKindType);
			if (logger.isDebugEnabled())
				logger.debug("Get Products For InfoKindType = " + infoKindType + " lst=" 
					+ ((lst==null)?"null" : lst.size()));
			
			return Collections.unmodifiableList(lst);
		}
		
		return new ArrayList<ProductCodeDTO>();
	}
	
	
	public List<ProductCodeDTO> getAllPolicyInfoProductReferences() {
		List<ProductCodeDTO> productCodeDTOs = new ArrayList<ProductCodeDTO>();

		ProductCodeDTO productCodeDTO = null;

//		List<ProductCodeFLO> productCodeList = productCodesEntityManager.getAllProductCodes();
//		if (productCodeList != null && !productCodeList.isEmpty()) {
//			for (ProductCodeFLO productCodes : productCodeList) {
		long cnt = 0;
		for (String batch : new String[] {"GBNK","LIBLISP","STANGBNK","STANWEALTH" }) {
			productCodeDTO = new ProductCodeDTO();
			productCodeDTO.setBatch(batch);
			productCodeDTO.setId(++cnt);
			productCodeDTO.setProductCode("485"+cnt);
			productCodeDTO.setProductDescription(batch + "-Test1");
			productCodeDTO.setReference(Long.valueOf(productCodeDTO.getProductCode()));
			if (batch.equals("STANDSTI") ) {
				productCodeDTO.setProductType(2230L);
			} else {
				productCodeDTO.setProductType(2221L);
			}
			
			productCodeDTOs.add(productCodeDTO);
		}
		

		return productCodeDTOs;
	}
	
	/**
	 * Returns a list of valid commission kinds
	 */
	public List<DescriptionDTO> getAllCommissionKinds() {
		
		List<DescriptionDTO> commissionKinds = new ArrayList<DescriptionDTO>();
		DescriptionDTO descriptionDTO = null;
		
//		List<Description> descriptions = descriptionEntityManager.findValuesByDescriptionKind(DescriptionKindType.PRODUCT_KIND.getKind());
//		if(descriptions != null && !descriptions.isEmpty()){
//			for (Description description : descriptions) {
//				descriptionDTO = new DescriptionDTO();
//				descriptionDTO.setDescription(description.getDescription());
//				descriptionDTO.setUniqId(description.getUniqId());
//				descriptionDTO.setReference(description.getReference());
//				commissionKinds.add(descriptionDTO);
//			}
//		}
//		
		return commissionKinds;
		
	}
	
	public List<DescriptionDTO> getAllContributionIncIndicatorList() {
		List<DescriptionDTO> contributionIncrIndicators = new ArrayList<DescriptionDTO>();
		DescriptionDTO descriptionDTO = null;
		
//		List<Description> descriptions = descriptionEntityManager.findValuesByDescriptionKind(DescriptionKindType.CONTRIBUTION_INCREASE_INDICATOR.getKind());
//		if(descriptions != null && !descriptions.isEmpty()){
//			for (Description description : descriptions) {
//				descriptionDTO = new DescriptionDTO();
//				descriptionDTO.setDescription(description.getDescription());
//				descriptionDTO.setUniqId(description.getUniqId());
//				descriptionDTO.setReference(description.getReference());
//				contributionIncrIndicators.add(descriptionDTO);
//			}
//		}
		
		return contributionIncrIndicators;
		
	}
	
	/**
	 * Returns a list of product references
	 */
	public List<DescriptionDTO> getAllProductReferences() {
		
		if(productReferences == null) {
			productReferences = new ArrayList<DescriptionDTO>();
			
			DescriptionDTO descriptionDTO = null;
			descriptionDTO = new DescriptionDTO();
			descriptionDTO.setDescription("Product 1");
			descriptionDTO.setUniqId(111);
			descriptionDTO.setReference(1);
			productReferences.add(descriptionDTO);
			
			descriptionDTO = null;
			descriptionDTO = new DescriptionDTO();
			descriptionDTO.setDescription("Product 2");
			descriptionDTO.setUniqId(112);
			descriptionDTO.setReference(2);
			productReferences.add(descriptionDTO);
			
			descriptionDTO = null;
			descriptionDTO = new DescriptionDTO();
			descriptionDTO.setDescription("Product 3");
			descriptionDTO.setUniqId(113);
			descriptionDTO.setReference(3);
			productReferences.add(descriptionDTO);
		}
		
		return productReferences;
		
	}

	/*
	 * (non-Javadoc)
	 * @see za.co.liberty.business.guicontrollers.transactions.PolicyTransactionGuiController#getAllBenefitTypes()
	 */
	public List<DescriptionDTO> getAllBenefitTypes() {

		List<DescriptionDTO> benefitTypes = new ArrayList<DescriptionDTO>();
		DescriptionDTO descriptionDTO = null;
		
		descriptionDTO = new DescriptionDTO();
		descriptionDTO.setDescription("Ben1");
		descriptionDTO.setUniqId(111);
		descriptionDTO.setReference(1);
		benefitTypes.add(descriptionDTO);
		
		descriptionDTO = null;
		descriptionDTO = new DescriptionDTO();
		descriptionDTO.setDescription("Ben2");
		descriptionDTO.setUniqId(112);
		descriptionDTO.setReference(2);
		benefitTypes.add(descriptionDTO);
		
		descriptionDTO = null;
		descriptionDTO = new DescriptionDTO();
		descriptionDTO.setDescription("Ben3");
		descriptionDTO.setUniqId(113);
		descriptionDTO.setReference(3);
		benefitTypes.add(descriptionDTO);
		
//		List<Description> descriptions = descriptionEntityManager.findValuesByDescriptionKind(DescriptionKindType.BENEFIT_TYPE.getKind());
//		if(descriptions != null && !descriptions.isEmpty()){
//			for (Description description : descriptions) {
//				descriptionDTO = new DescriptionDTO();
//				descriptionDTO.setDescription(description.getDescription());
//				descriptionDTO.setUniqId(description.getUniqId());
//				descriptionDTO.setReference(description.getReference());
//				benefitTypes.add(descriptionDTO);
//			}
//		}
		
		return benefitTypes;
	}

	/*
	 * (non-Javadoc)
	 * @see za.co.liberty.business.guicontrollers.transactions.PolicyTransactionGuiController#getDescription(int, int)
	 */
	public DescriptionDTO getDescription(int descriptionKind, int refetence) {
		DescriptionDTO descriptionDTO = null;

//		Description description = descriptionEntityManager.findDescrtionByDescKindAndMaxRef(descriptionKind, refetence);
//
//		if (description != null) {
//			descriptionDTO = new DescriptionDTO();
//			descriptionDTO.setDescription(description.getDescription());
//			descriptionDTO.setUniqId(description.getUniqId());
//			descriptionDTO.setReference(description.getReference());
//
//		}
		return descriptionDTO;
	}
	
	/*
	 * (non-Javadoc)
	 * @see za.co.liberty.business.guicontrollers.transactions.PolicyTransactionGuiController#getGLCompanyCodeList()
	 */
	public List<String> getGLCompanyCodeList() {
		
		return Arrays.asList(new String[] {"GL1", "GL2", "GL3"});
		
//		return Collections.EMPTY_LIST;
//		return agreementValuesFactory.getValidValuesForGLCompanyCode();
		
	}

	/*
	 * (non-Javadoc)
	 * @see za.co.liberty.business.guicontrollers.transactions.PolicyTransactionGuiController#getBusinessUnitList()
	 */
	public List<String> getBusinessUnitList() {
//		return agreementValuesFactory.getValidValuesForBusinessUnit();
		return Arrays.asList(new String[] {"BU1", "BU2", "BU3"});
	}

	/*
	 * (non-Javadoc)
	 * @see za.co.liberty.business.guicontrollers.transactions.PolicyTransactionGuiController#getNumberOfMonthsList()
	 */
	public List<Integer> getNumberOfMonthsList() {
//		return agreementValuesFactory.getValidValuesForNumberOfMonths();
		return Arrays.asList(new Integer[] {1,2,3,4,5,6,7,8,9,10,11,12});
	}

	/*
	 * (non-Javadoc)
	 * @see za.co.liberty.business.guicontrollers.transactions.PolicyTransactionGuiController#getCommissionFrequencyList()
	 */
	public List<Integer> getCommissionFrequencyList() {
//		return agreementValuesFactory.getValidValuesForCommissionFrequency();
		return Arrays.asList(new Integer[] {1,2,3});
	}
	
	public List<Integer> getPCRCodeList() {
//		return agreementValuesFactory.getValidValuesForPCRCodes();
		return Arrays.asList(new Integer[] {1112,2221,3331});
	}


	public FundCodeDTO getFundCodeById(Long fundCodeId) {

		FundCodeDTO fundCodeDTO = null;
		
//		if(fundCodeId != null){
//			FundCodes fundCode = fundCodeEntityManager.getFundCodeById(fundCodeId);
//			if(fundCode != null){
//				fundCodeDTO = new FundCodeDTO();
//				fundCodeDTO.setBatch(fundCode.getBatch());
//				fundCodeDTO.setFundCode(fundCode.getFundCode());
//				fundCodeDTO.setFundDescription(fundCode.getFundDescription());
//				fundCodeDTO.setId(fundCode.getId());
//				fundCodeDTO.setReference((long) fundCode.getReference());
//				fundCodeDTO.setCategory(fundCode.getCategory());
//				
//			}
//		}
		return fundCodeDTO;
	}

	/*
	 * (non-Javadoc)
	 * @see za.co.liberty.business.guicontrollers.transactions.PolicyTransactionGuiController#updateAUMRequest(za.co.liberty.dto.transaction.RecordPolicyInfoDTO)
	 */
	public void updateAUMRequest(RecordPolicyInfoDTO policyInfoRequestDto) {
		
		logger.info("UpdateAUMRequest");
		
	}

	public void updateDPERequest(DistributePolicyEarningDTO distributePolicyEarning) {

		logger.info("UpdateDPERequest");
		
	}
	
	/**
	 * Initialise the policy transaction model.
	 * 
	 * @param model
	 */
	public void initialisePageModel(IPolicyTransactionModel model) {
		model.setAllFundCodes(getAllFundCodes());
		model.setAllProductCodes(getAllPolicyInfoProductReferences());
		model.setCurrentProductCodes(new ArrayList<ProductCodeDTO>());
		model.setCommissionKinds(getAllCommissionKinds());
		model.setAllContributionIncIndicators(getAllContributionIncIndicatorList());
		model.setAllProductReferences(getAllProductReferences());
		model.setBenefitTypes(getAllBenefitTypes());
		model.setGlCompanyList(getGLCompanyCodeList());
		model.setBusinessUnitList(getBusinessUnitList());
		model.setNumberOfMonthsList(getNumberOfMonthsList());
		model.setCommissionFrequencyList(getCommissionFrequencyList());
		model.setAllFrequencyTypes(getPremiumFrequencyList());
		model.setAllFrequencyTypesForPolicyInfo(getPremiumFrequencyListForPolicyInfo());
		model.setAllInfoKindTypes(getInfoKindTypeList());
		model.setBenefitGroups(getAllBenefitGroups());
		//SBS0510 - For Guardbank
		model.setPcrCodes(getPCRCodeList());
		model.setCurrentFundCodes(new ArrayList<FundCodeDTO>());
		
	}
	
	/**
	 * Let us do some testing
	 */
	public PolicyInfoCommissionCalculationDTO initialisePolicyInfoCalculation(Long requestId) {

		return null;
		
//		/*
//		 * Find the calculation request
//		 */
//		RequestVO v = new RequestVOImpl(requestId);
//		v.getObjectReference().setObjectOid(requestId);
//		try {
//			RequestVO[] requestArray = agreementBPOHome.create().getRequests(new ApplicationContext(), v,
//				new RequestStatusEnumeration[] {
//					RequestStatusEnumeration.EXECUTED
//				}, new RequestKindEnumeration[] {
//					RequestKindEnumeration.CALCULATE_COMMISSION_FOR_POLICYINFO_PREMIUM,
//					RequestKindEnumeration.CALCULATECOMMISSIONFORPOLICYINFORISKPREMIUM,
//					RequestKindEnumeration.CALCULATECOMMISSIONFORPOLICYINFOGUARDBANKPREMIUM,
//					RequestKindEnumeration.CALCULATECOMMISSIONFORPOLICYINFOINN8PCRPREMIUM
//			});
//			// TODO - check why it didn't find it here.
//			logger.info("PolicyInfo Comm calc FOUND " + ((requestArray!=null)?requestArray.length : -1));
//			
////			IRequestEnquiryDPERow
//			
//			/*
//			 * Found the comm calc request, now convert to the relevant DTO.
//			 */
//			if (requestArray!=null && requestArray.length>0) {
//				RequestVO vo = requestArray[0];
//				if (logger.isDebugEnabled())
//						logger.debug("PolicyInfo Comm calc - Request id = " + vo.getObjectReference().getObjectOid());
//				
//				
//				
//				List<IRequestEnquiryRow> dpeList = new ArrayList<IRequestEnquiryRow>();
//				/*
//				 * GET the linked DPE's
//				 */
//				try {
//					List<Long> linkedDPEList = new ArrayList<Long>();
//					AgreementRoleVO[] roleArray = vo.getRolesOfKind(RoleKindEnumeration.RELATEDSOURCEREQUEST
//							.getValue());
//					if (roleArray != null && roleArray.length>0) {
//						for (AgreementRoleVO r : roleArray) {
//							linkedDPEList.add(r.getRolePlayerRef().getObjectOid());
//						}
//						
//						
//						RequestEnquirySearchDTO searchDTO = new RequestEnquirySearchDTO((List<Long>)null, 	
//								Arrays.asList(RequestKindType.DistributePolicyEarning), null);
//						searchDTO.setRequestIdList(linkedDPEList);
//						try {
//							RequestEnquiryResultDTO result = requestEnquiryManagement.findRequests(searchDTO,1, 50, RequestEnquiryDPERowDTO.class);
//							logger.info("PolicyInfo Comm calc - Found DPE's count " + result.getResultList().size());
//							dpeList = result.getResultList();
//						} catch (RequestException e) {
//							logger.error("PolicyInfo Comm calc - Could not find linked DPE's for request (request) " 
//									+ vo.getObjectReference().getObjectOid(),e);
//						} catch (QueryTimeoutException e) {
//							logger.error("PolicyInfo Comm calc - Could not find linked DPE's for request (timeout) " 
//									+ vo.getObjectReference().getObjectOid(),e);
//						}
//								
//						
//					}
//					
//					
//				} catch (KindNotFoundException e) {
//					logger.error("PolicyInfo Comm calc - Could not find linked DPE's for request " 
//							+ vo.getObjectReference().getObjectOid());
//				}
//				
//				
//				/*
//				 * Get the properties required
//				 */
//				PolicyInfoCommissionCalculationDTO dto = new PolicyInfoCommissionCalculationDTO();
//				
//				if (logger.isDebugEnabled()) logger.debug("Calculation - set properties");
//				
//				List<String> descriptionList = (List<String>) vo.getValueOfPropertyOfKind(
//						PropertyKindType.DescriptionList.getPropertyKind());
//				List<BigDecimal> rateList = (List<BigDecimal>) vo.getValueOfPropertyOfKind(
//						PropertyKindType.RateUsedList.getPropertyKind());
//				List<Integer> commList = (List<Integer>) vo.getValueOfPropertyOfKind(
//						PropertyKindType.CommKindList.getPropertyKind());
//				List<BigDecimal> baserateList = null;
//				
//				dto.setCalculationList(new ArrayList<PolicyInfoCommissionCalculationDTO.PolicyInfoListDTO>());
//				
//				try {
//					dto.setApplicableProducsVersionIdUsed((Long) vo.getPropertyOfKind(
//							PropertyKindType.ApplicableProductsVersionIdUsed.getPropertyKind())
//								.getValue());
//					dto.setAqcType((String) vo.getPropertyOfKind(
//							PropertyKindType.AQCType.getPropertyKind())
//								.getValue());
//					dto.setAqcCode((String) vo.getPropertyOfKind(
//							PropertyKindType.AQCCode.getPropertyKind())
//								.getValue());
//					dto.setReferralFeeMax((BigDecimal) vo.getValueOfPropertyOfKind(PropertyKind.STI_REFERRALFEE_MAXRATE));
//					dto.setServiceFeeMax((BigDecimal) vo.getValueOfPropertyOfKind(PropertyKind.STI_SERVICEFEE_MAXRATE));
//				} catch (KindNotFoundException e) {
//					logger.warn("Some AQC related properties not found for calculation.  reqId = " 
//							+ vo.getObjectReference());
//				}
//
//				try {
//					baserateList = (List<BigDecimal>) vo.getValueOfPropertyOfKind(
//							PropertyKindType.BaserateUsedList.getPropertyKind());
//				} catch (KindNotFoundException e) {
//					// can be ignored, not on all calculations
//					logger.warn("Some baserate related properties not found for calculation.  reqId = " 
//							+ vo.getObjectReference());
//				}
//				
//				
//				
//				/*
//				 * Only process the description properties if the numbers are exactly the same
//				 */
//				if (logger.isDebugEnabled()) logger.debug("Calculation - set description values");
//				
//				if (descriptionList!=null &&  rateList!=null && rateList!=null &&
//						descriptionList.size()==rateList.size() && rateList.size() == commList.size()) {
//					
//					if (logger.isDebugEnabled()) logger.debug("Calculation - Description counts match");
//					
//					/**
//					 * Loop through all the descriptions and set the linked comm rates
//					 */
//					for (int i = 0; i < descriptionList.size();++i) {
//						PolicyInfoCommissionCalculationDTO.PolicyInfoListDTO p = dto.new PolicyInfoListDTO();
//						p.setCommKind(commList.get(i));
//						p.setRateUsed(rateList.get(i));
//						p.setDescription(descriptionList.get(i));
//						
//						if (baserateList!=null && i < baserateList.size() && baserateList.size() == rateList.size()) {
//							p.setBaserateUsed(baserateList.get(i));
//						}
//						
//						if (dpeList.size()== descriptionList.size()) {
//							
//							if (IRequestEnquiryDPERow.class.isAssignableFrom(dpeList.get(i).getClass())) {
//								p.setDpe((IRequestEnquiryDPERow)dpeList.get(i));
//								if (logger.isDebugEnabled()) logger.debug("Calculation - add dpe nr " + i 
//										+ " - " + dpeList.get(i).getRequestId());
//							}else {
//								if (logger.isDebugEnabled())
//									logger.debug("PolicyInfo Comm calc - Non DPE requeest returned for DPE linked to Request id = " 
//											+ vo.getObjectReference().getObjectOid());
//							}
//						} else {
//							if (logger.isDebugEnabled())
//								logger.debug("PolicyInfo Comm calc - Request id = " + vo.getObjectReference().getObjectOid()
//										+ " dpe list size " + dpeList.size() + " != " + commList.size());
//						}
//						
//						dto.getCalculationList().add(p);
//						
//					}
//				} else {
//					logger.error("PolicyInfo Comm calc - The property list sized do not match for request " 
//							+ vo.getObjectReference().getObjectOid()
//							+ "  desc=" + descriptionList.size()
//							+ "  rate=" + rateList.size()
//							+ "  kind=" + commList.size());
//				}
//				
//				/*
//				 * Sort the list by comm kind
//				 */
//				Collections.sort(dto.getCalculationList(), new Comparator<PolicyInfoCommissionCalculationDTO.PolicyInfoListDTO>() {
//
//					public int compare(PolicyInfoListDTO o1,
//							PolicyInfoListDTO o2) {
//						return o1.getCommKind().compareTo(o2.getCommKind());
//					}
//				});
//				
////				for (PropertyVO prop : vo.getProperties()) {
////					System.out.println(prop.getKind() + " - " + prop.getValue());
////				}
//				
//				return dto; 
//			}
//		} catch (OnlyActualNotFoundException e) {
//			// Ignore
//			logger.warn("Only actual issue",e);
//		} catch (RemoteException e) {
//			throw new CommunicationException(e);
//		} catch (CreateException e) {
//			throw new CommunicationException(e);
//		} catch (KindNotFoundException e) {
//			// We will ignore this
//			logger.warn("Unable to find kind",e);
//		}
//		
//		return null;
	}
	
	/**
	 * Return the policy info kind types available.
	 * 
	 * @return
	 */
	public List<DescriptionDTO> getInfoKindTypeList() {
		List<DescriptionDTO> infoKindList = new ArrayList<DescriptionDTO>();
		
		for (PolicyInfoKindType p : PolicyInfoKindType.getAllSelectable()) {
			DescriptionDTO descriptionDTO = new DescriptionDTO();
			descriptionDTO.setDescription(p.getDescription());
			descriptionDTO.setReference((int) p.getType());
			infoKindList.add(descriptionDTO);
		}
		
//		DescriptionDTO descriptionDTO = new DescriptionDTO();
//		descriptionDTO.setDescription("Assets Under Management");
//		descriptionDTO.setReference(2221);
//		infoKindList.add(descriptionDTO);
//		
//		descriptionDTO = new DescriptionDTO();
//		descriptionDTO.setDescription("Contractual Premium");
//		descriptionDTO.setReference(2231);
//		infoKindList.add(descriptionDTO);
//		
//		descriptionDTO = new DescriptionDTO();
//		descriptionDTO.setDescription("Collected Premium");
//		descriptionDTO.setReference(2232);
//		infoKindList.add(descriptionDTO);
		
		return infoKindList;
		
	}


	public void doValidation(RequestKindType requestKind, IPolicyTransactionModel pageModel) throws ValidationException {
		List<String> errors = new ArrayList<String>();
		IPolicyTransactionDTO selectedObject = pageModel.getSelectedObject();
		
		String policyRefNr = (selectedObject instanceof DistributePolicyEarningDTO) ? 
				((DistributePolicyEarningDTO)selectedObject).getPolicyReference() 
					: ((RecordPolicyInfoDTO)selectedObject).getPolicyNr();
		if (policyRefNr == null || policyRefNr.trim().length() == 0) {
			errors.add("Policy Reference is Required");
		}
		
		if (pageModel.getSelectedObject().getPolicyStartDate() == null) {
			errors.add("Policy Start Date is Required");
		}
		if (pageModel.getSelectedObject().getEffectiveDate() == null) {
			errors.add("Effective Date is Required");
		}
		
		// Owner name
		String ownerName = null;
		if (pageModel.getSelectedObject() instanceof DistributePolicyEarningDTO) {
			ownerName = ((DistributePolicyEarningDTO)pageModel.getSelectedObject()).getDPELifeAssuredName();
		} else {
			ownerName = ((RecordPolicyInfoDTO)pageModel.getSelectedObject()).getLifeAssured();
		}
		if (ownerName == null || ownerName.trim().length() == 0) {
			errors.add("Owner Name is Required");
		}
		
		
		if (RequestKindType.RecordPolicyInfo.getRequestKind() == requestKind.getRequestKind()) {
			/*
			 * AUM specific
			 */
			RecordPolicyInfoDTO aumDto = (RecordPolicyInfoDTO) selectedObject;
			if (aumDto.getProductCode() == null) {
				errors.add("Product Reference is Required");
			}
			
			if (aumDto.getInfoKindType()==null) {
				errors.add("Info Kind is required");
			} else {
				if (aumDto.getInfoKindType()==SRSType.POLICYINFORMATION_ASSETSUNDERMANAGEMENT) {
				
					if (aumDto.getFundCode() == null) {
						errors.add("Fund Code is Required");
					}
		
					if (aumDto.getFundUnitCount() == null || aumDto.getFundUnitCount().doubleValue() <= 0) {
						errors.add("Fund Unit Count is Rrequired");
					}
		
					if (aumDto.getFundUnitPrice() == null || aumDto.getFundUnitPrice().getValue() == null) {
						errors.add("Fund Unit Price is Rrequired");
					}
		
					if (aumDto.getPricingDate() == null) {
						errors.add("Fund Pricing Date is Required");
					}
		
					if (aumDto.getFundAssetValue() == null || aumDto.getFundAssetValue().getValue() == null) {
						errors.add("Fund Asset Value is Rrequired");
					}
	
				} else {
					// Premium types
					if (aumDto.getAmount()==null || aumDto.getAmount().getValue().compareTo(BigDecimal.ZERO)==0) {
						errors.add("A non-zero amount is required");
					}
					if (aumDto.getPremiumFrequency()== null)
						errors.add("Premium Frequency is required");
					
					if (aumDto.getEffectiveDate()== null)
						errors.add("Effective date is required");
					
					// One more check, ensure that the component exists
					
				}
			}
			
		} else if (RequestKindType.DistributePolicyEarning.getRequestKind() == requestKind.getRequestKind()) {
			/*
			 * DPE specific
			 */
			DistributePolicyEarningDTO dpeDto = (DistributePolicyEarningDTO) selectedObject;
			if (dpeDto.getCommissionKind() == null || dpeDto.getCommissionKind()==0)
				errors.add("Commission Kind is required");

			if (dpeDto.getContributionIncreaseIndicator() == null)
				errors.add("Movement Type is required");

			if (dpeDto.getPremiumFrequency()== null)
				errors.add("Premium Frequency is required");

			int productReference = 0;
			
			if (dpeDto.getProductReference() == null || dpeDto.getProductReference()==0)
				errors.add("Product Name is required");
			else
				productReference = dpeDto.getProductReference();
			
			if(dpeDto.getMovementCode() == null)
				errors.add("Movement Code is required");
			else if(dpeDto.getMovementCode().length() > 4)
				errors.add("The maximum lenght of the Movement Code value is 4");

			CurrencyAmount amount = dpeDto.getAmount();
			if (amount == null)
				errors.add("DPE Amount is required");

			if (productReference != 0 && productReference == ProductReferenceType.LIFESTYLE_PROTECTOR.getProductRef()) {
				if (dpeDto.getBenefitType() == null || dpeDto.getBenefitType().intValue() == 0)
					errors.add("Benefit Type is required for the " + ProductReferenceType.LIFESTYLE_PROTECTOR.getDescription() + " product" );
			// TODO Jean - you'll have to add a lookup here
			} else if (dpeDto.getBenefitType() != null && dpeDto.getBenefitType().intValue() != 0) {
				errors.add("Benefit Type is only required for product Liberty Lifestyle Protector");
			}
			
			//SBS0510-Check if Benefit Group is applicable for the Product, then validate
			if (productReference != 0){
				DescriptionDTO prodDTO = getDescription(DescriptionKindType.PRODUCT_REFERENCE_KIND.getKind(), dpeDto.getProductReference());				
				String prodName = prodDTO != null ? prodDTO.getDescription():"";
						
				if(DPEBenefitGroupHelper.getInstance().isBenefitGroupApplicableForProduct(productReference)){
					if (dpeDto.getBenefitGroup() == null || dpeDto.getBenefitGroup().intValue() == 0)
							errors.add("Benefit Group is required for the selected product - "+prodName);
					
				} else {
					if (dpeDto.getBenefitGroup() != null && dpeDto.getBenefitGroup().intValue() != 0) {
							errors.add("Benefit Group is not applicable for the selected product - "+prodName);
						}
					
				}	
			}
			
			
			if(dpeDto.getCommissionTerm() != null && (dpeDto.getCommissionTerm().intValue() < 1 || dpeDto.getCommissionTerm().intValue() > 25)){
				errors.add("Invalid Commission Term value - minimum allowed value is 1 and the maximum is 25 for this field");
			}
			
			if(dpeDto.getMaxCommissionTerm() != null && (dpeDto.getMaxCommissionTerm().intValue() < 1 || dpeDto.getMaxCommissionTerm().intValue() > 25)){
				errors.add("Invalid Max Commission Term value - minimum allowed value is 1 and the maximum is 25 for this field");
			}
			
			//Validation for Commission Term,Maximum Commission Term and Upfront Commission percentage
			//All 3 fields are mandatory only if Product reference is of Kind Excelsior 2000.
			if(isEP2000Product(productReference)){
//			TODO fix the product lookup	
				if(dpeDto.getCommissionTerm() == null || dpeDto.getCommissionTerm().intValue() < 1){
					errors.add("Commission Term is required for product " + getDescription(DescriptionKindType.PRODUCT_REFERENCE_KIND.getKind(), dpeDto.getProductReference()).getDescription());
				}
				
				if(dpeDto.getMaxCommissionTerm() == null || dpeDto.getMaxCommissionTerm().intValue() < 1){
					errors.add("Max Commission Term is required for product " + getDescription(DescriptionKindType.PRODUCT_REFERENCE_KIND.getKind(), dpeDto.getProductReference()).getDescription());
				}
				
				if (dpeDto.getUpfrontCommPercentage() == null || dpeDto.getUpfrontCommPercentage().getValue().intValue() < 1) {
					errors.add("Upfront Commission % is required for product " + getDescription(DescriptionKindType.PRODUCT_REFERENCE_KIND.getKind(), dpeDto.getProductReference()).getDescription());
				}
			}
			
			if(dpeDto.getCommissionKind()!= null && ProductKindType.POLICYCOUNT.getKind() == dpeDto.getCommissionKind()){
				if(dpeDto.getNumberOfMonths() == null){
					errors.add("Number of months is required when selecting the " + ProductKindType.getProductKindType(dpeDto.getCommissionKind()).getDescription() + " Commission");
				}
				
				BigDecimal validAmounts[] = { BigDecimal.ONE, new BigDecimal(0.5), new BigDecimal(0.33), new BigDecimal(0.34), 
						BigDecimal.ONE.negate(), new BigDecimal(0.5).negate(), new BigDecimal(0.33).negate(), new BigDecimal(0.34).negate() };

				//CurrencyAmount cAmount = preTransaction.getAmount();
				BigDecimal amountValue = amount.getValue();
				boolean amountValid = false;
				for (BigDecimal bigDecimal : validAmounts) {
					if (amountValue.equals(bigDecimal.setScale(2, BigDecimal.ROUND_HALF_EVEN))) {
						amountValid = true;
						break;
					}
				}
				if(!amountValid){
					errors.add("Accepted amounts are 1, 0.5, 0.33, 0.34, -1, -0.5, -0.33, -0.34 for " + ProductKindType.getProductKindType(dpeDto.getCommissionKind()).getDescription() + " Commission");
				}
			}
		}

		if (!errors.isEmpty()) {
			throw new ValidationException(errors);
		}
		
	}
	private boolean isEP2000Product(int productReference){
		
		
		int [] ep2000Products = {177, 178, 179};
		for (int reference : ep2000Products) {
			if(reference == productReference)
				return true;
		}
		
		return false;
		
	}
	
	public List<DescriptionDTO> getPremiumFrequencyList() {
		List<DescriptionDTO> contributionIncrIndicators = new ArrayList<DescriptionDTO>();
		DescriptionDTO descriptionDTO = null;
		
//		List<Description> descriptions = descriptionEntityManager.findValuesByDescriptionKind(DescriptionKindType.FREQUENCY_KIND.getKind());
//		if(descriptions != null && !descriptions.isEmpty()){
//			for (Description description : descriptions) {
//				descriptionDTO = new DescriptionDTO();
//				descriptionDTO.setDescription(description.getDescription());
//				descriptionDTO.setUniqId(description.getUniqId());
//				descriptionDTO.setReference(description.getReference());
//				contributionIncrIndicators.add(descriptionDTO);
//			}
//		}
		
		return contributionIncrIndicators;
		
	}
	
	public List<DescriptionDTO> getPremiumFrequencyListForPolicyInfo() {
		if (premiumFrequencyForPolicyInfoList==null) {
			List<DescriptionDTO> freqList = getPremiumFrequencyList();
			List<DescriptionDTO> newFreqList = new ArrayList<DescriptionDTO>();
			for (DescriptionDTO d : freqList) {
				if (d.getReference()<=5 && d.getReference()!=0) {
					newFreqList.add(d);
				}
			}
			premiumFrequencyForPolicyInfoList = Collections.unmodifiableList(newFreqList);
		}
		return premiumFrequencyForPolicyInfoList;
		
		
	}

	public void cancelReject(long rejectId, short errorCode) throws DataNotFoundException {
		logger.info("Cancel reject");
//			preTransactionRejectManagement.cancelReject(rejectId);
		
	}

	/**
	 * Returns a list of valid Benefit Groups
	 */
	public List<DescriptionDTO> getAllBenefitGroups() {
		
		List<DescriptionDTO> benefitGroups = new ArrayList<DescriptionDTO>();
//		DescriptionDTO descriptionDTO = null;
//		
//		List<Description> descriptions = descriptionEntityManager.findValuesByDescriptionKind(DescriptionKindType.BENEFIT_GROUP.getKind());
//		if(descriptions != null && !descriptions.isEmpty()){
//			for (Description description : descriptions) {
//				descriptionDTO = new DescriptionDTO();
//				descriptionDTO.setDescription(description.getDescription());
//				descriptionDTO.setUniqId(description.getUniqId());
//				descriptionDTO.setReference(description.getReference());
//				benefitGroups.add(descriptionDTO);
//			}
//		}
		
		return benefitGroups;
		
	}
	
	//Added SBS0510 for Guardbank GUI
	/**
	 * Method to get All FundCodes for Given Batch Code from RATING SRS.FUND_CODES
	 */
	public List<FundCodeDTO> getAllFundCodesForBatchCode(String batchCode) {
		List<FundCodeDTO> fundCodes = new ArrayList<FundCodeDTO>();
//		if(batchCode == null)
//			return fundCodes;
//
//		FundCodeDTO fundCodeDTO = null;
//		if(fundCodesListForBatch.get(batchCode) == null) {
//
//		List<FundCodes> fundCodesList = fundCodeEntityManager.getAllFundCodesForBatchCode(batchCode);
//		if (fundCodesList != null && !fundCodesList.isEmpty()) {
//			for (FundCodes fundCode : fundCodesList) {
//				fundCodeDTO = new FundCodeDTO();
//				fundCodeDTO.setBatch(fundCode.getBatch());
//				fundCodeDTO.setFundCode(fundCode.getFundCode());
//				fundCodeDTO.setFundDescription(fundCode.getFundDescription());
//				fundCodeDTO.setId(fundCode.getId());
//				fundCodeDTO.setReference(Long.valueOf(fundCode.getReference()));
//				fundCodeDTO.setProductCode(fundCode.getProductCode());
//				fundCodeDTO.setCategory(fundCode.getCategory());
//				fundCodes.add(fundCodeDTO);
//				}
//			fundCodesListForBatch.put(batchCode, fundCodes);
//			}
//		} else {
//			fundCodes = fundCodesListForBatch.get(batchCode);
//		}
			

		return fundCodes;
	}	
	
	/**
	 * Private Method to populate Fund Codes for Each applicable PolicyInfoKind Type like GBNK & AUM currently only
	 * @return Map<Long, List<FundCodeDTO>>
	 */
	private Map<Long, List<FundCodeDTO>> getFundCodesPerTypeForPolicyMap() {
		if(fundCodesPerTypeForPolicyMap == null) {
			fundCodesPerTypeForPolicyMap = new HashMap<Long, List<FundCodeDTO>>();		
			List<FundCodeDTO> guardbankFundCodes = getAllFundCodesForBatchCode(PolicyInfoKindType.PolicyInfoGuardbankPremium.getBatchCode());
			List<FundCodeDTO> inn8FundCodes = getAllFundCodesForBatchCode(PolicyInfoKindType.PolicyInformationINN8PCRPremium.getBatchCode());
			fundCodesPerTypeForPolicyMap.put(PolicyInfoKindType.PolicyInfoGuardbankPremium.getType(), guardbankFundCodes);
			fundCodesPerTypeForPolicyMap.put(PolicyInfoKindType.PolicyInformationINN8PCRPremium.getType(), inn8FundCodes);
			
			List<FundCodeDTO> copyOfAllFundCodesDTO = new ArrayList<FundCodeDTO>(getAllFundCodes());//So that doesnt change the Original allFundCode List
			copyOfAllFundCodesDTO.removeAll(guardbankFundCodes);
			copyOfAllFundCodesDTO.removeAll(inn8FundCodes);
			fundCodesPerTypeForPolicyMap.put(PolicyInfoKindType.AssetsUnderManagement.getType(), copyOfAllFundCodesDTO);			
			
		}
		
		return fundCodesPerTypeForPolicyMap;		
		
	}
	
   public List<FundCodeDTO> getFundCodesForInfoKindType(long infoKindType) {
		
		List<FundCodeDTO> fundCodeDTOs = getFundCodesPerTypeForPolicyMap().get(infoKindType);
		
		if(fundCodeDTOs == null)
			fundCodeDTOs = new ArrayList<FundCodeDTO>();
		
		return fundCodeDTOs;	
		
	}
   
   //Added fr INN8- SBS0510

	@Override
	public List<DescriptionDTO> getAllFundCategories() {

//		if (fundCodeCategories == null) {
//			fundCodeCategories = new ArrayList<DescriptionDTO>();
//			DescriptionDTO descriptionDTO = null;
//
//			List<Description> descriptions = descriptionEntityManager
//					.findValuesByDescriptionKind(DescriptionKindType.FUNDCODE_CATEGORY.getKind());
//			if (descriptions != null && !descriptions.isEmpty()) {
//				for (Description description : descriptions) {
//					descriptionDTO = new DescriptionDTO();
//					descriptionDTO.setDescription(description.getDescription());
//					descriptionDTO.setUniqId(description.getUniqId());
//					descriptionDTO.setReference(description.getReference());
//					fundCodeCategories.add(descriptionDTO);
//				}
//			}
//		}
//
//		return fundCodeCategories;
		
		return Collections.EMPTY_LIST;
	}
}
