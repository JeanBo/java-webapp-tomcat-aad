package za.co.liberty.business.guicontrollers.admin;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.log4j.Logger;

import za.co.liberty.business.broadcast.aqc.IFactorTableBroadcastController;
import za.co.liberty.business.guicontrollers.userprofiles.IDifferentialPricingFactorManagement;
import za.co.liberty.business.guicontrollers.userprofiles.ISegmentManagement;
import za.co.liberty.business.guicontrollers.userprofiles.ISegmentNameManagement;
import za.co.liberty.dto.rating.DifferentialPricingFactorDTO;
import za.co.liberty.dto.rating.ProductDTO;
import za.co.liberty.dto.rating.SegmentDTO;
import za.co.liberty.dto.rating.SegmentNameDTO;
import za.co.liberty.interfaces.rating.difffactor.AQCApplicableType;
import za.co.liberty.interfaces.rating.difffactor.AQCProductType;
import za.co.liberty.interfaces.rating.difffactor.SegmentContextType;


@Stateless
public class DifferentialFactorGuiController implements IDifferentialFactorGuiController {
	static Logger logger = Logger.getLogger(DifferentialFactorGuiController.class);

	//@EJB
	//protected IDifferentialPricingFactorManagement diffFactorManager;
	
	//@EJB
	//protected ISegmentNameManagement filterSegmentName1;
	
	//@EJB
	//protected ISegmentManagement segmentManager;
	
	//@EJB
	//protected ISegmentNameManagement segmentNameManager;
	
	@EJB 
	protected IFactorTableBroadcastController factorBroadcastController;
	
	public List<DifferentialPricingFactorDTO> findAllFromDiffFactor() {
		//return diffFactorManager.findAllFromDiffFactor();
		//return null;
		//MSK#Change
				System.out.println("DifferentialFactorGuiController:findAllFromDiffFactor()------------------------------");
				List<DifferentialPricingFactorDTO> diffDTO=new ArrayList<DifferentialPricingFactorDTO>();
				DifferentialPricingFactorDTO dto=new DifferentialPricingFactorDTO();
				dto.setId(26); 
				dto.setAqcValue("F"); 
				 SegmentNameDTO segmentNameObject=new SegmentNameDTO();
				 segmentNameObject.setId(26);
				 segmentNameObject.setSegmentName("Agency");
				 SegmentDTO sdto=new SegmentDTO();
				 //sdto.setId(26);
				 segmentNameObject.setSelected(sdto);
				 dto.setSegmentNameObject(segmentNameObject);
				
				dto.setAqcProductCode(AQCProductType.E2000_EP);
				dto.setAqcAppCode(1);
				dto.setPremiumDiscountPercent(BigDecimal.valueOf(0)); 
				dto.setDeferredCommPercent(BigDecimal.valueOf(0)); 
				dto.setDeferredPeriodInYrs(0);
				dto.setMonthPremiumAFactor(BigDecimal.valueOf(0));
				dto.setPcrEnhancementPercent(BigDecimal.valueOf(1));
				dto.setMaxUpfrontCommPercent(BigDecimal.valueOf(0)); 
				dto.setMaxCommTermYrs(15);
				dto.setUpfrontCommClawbackInYrs(5);
				dto.setPcrClawbackInMonths(12);
				dto.setStartDate(Date.valueOf("2011-02-19"));
				dto.setEndDate(Date.valueOf("2012-02-19"));;
				//dto.setEndDateActive(endDateActive);
				dto.setCompetitorDiscount(false);
				diffDTO.add(dto);
				
				return diffDTO;
	}

	public DifferentialPricingFactorDTO findDiffFactorForIndex(int index) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<DifferentialPricingFactorDTO> findDiffFactorForSegment(SegmentNameDTO segmentType) {
		//return diffFactorManager.findDiffFactorForSegment(segmentType);
		//MSK#Change
				System.out.println("DifferentialFactorGuiController:findDiffFactorForSegment()--------------------");
				List<DifferentialPricingFactorDTO> diffDTO=new ArrayList<DifferentialPricingFactorDTO>();
				DifferentialPricingFactorDTO dto=new DifferentialPricingFactorDTO();
				dto.setId(26); 
				dto.setAqcValue("F"); 
				SegmentNameDTO segmentNameObject=new SegmentNameDTO();
				 segmentNameObject.setId(26);
				 segmentNameObject.setSegmentName("Agency");
				 SegmentDTO sdto=new SegmentDTO();
				 //sdto.setId(26);
				 segmentNameObject.setSelected(sdto);
				 dto.setSegmentNameObject(segmentNameObject);
				
				dto.setAqcProductCode(AQCProductType.E2000_EP);
				dto.setAqcAppCode(1);
				dto.setPremiumDiscountPercent(BigDecimal.valueOf(0)); 
				dto.setDeferredCommPercent(BigDecimal.valueOf(0)); 
				dto.setDeferredPeriodInYrs(0);
				dto.setMonthPremiumAFactor(BigDecimal.valueOf(0));
				dto.setPcrEnhancementPercent(BigDecimal.valueOf(1));
				dto.setMaxUpfrontCommPercent(BigDecimal.valueOf(0)); 
				dto.setMaxCommTermYrs(15);
				dto.setUpfrontCommClawbackInYrs(5);
				dto.setPcrClawbackInMonths(12);
				dto.setStartDate(Date.valueOf("2011-02-19"));
				dto.setEndDate(Date.valueOf("2012-02-19"));;
				//dto.setEndDateActive(endDateActive);
				dto.setCompetitorDiscount(false);
				diffDTO.add(dto);
				
				DifferentialPricingFactorDTO dto1=new DifferentialPricingFactorDTO();
				dto1.setId(27); 
				dto1.setAqcValue("F"); 
				 SegmentNameDTO segmentNameObject1=new SegmentNameDTO();
				 segmentNameObject1.setId(26);
				 segmentNameObject1.setSegmentName("Agency");
				 SegmentDTO sdto1=new SegmentDTO();
				 //sdto.setId(26);
				 segmentNameObject1.setSelected(sdto1);
				 dto1.setSegmentNameObject(segmentNameObject1);
				
				dto1.setAqcProductCode(AQCProductType.E2000_IB);
				dto1.setAqcAppCode(1);
				dto1.setPremiumDiscountPercent(BigDecimal.valueOf(0)); 
				dto1.setDeferredCommPercent(BigDecimal.valueOf(0)); 
				dto1.setDeferredPeriodInYrs(0);
				dto1.setMonthPremiumAFactor(BigDecimal.valueOf(11.4));
				dto1.setPcrEnhancementPercent(BigDecimal.valueOf(1));
				dto1.setMaxUpfrontCommPercent(BigDecimal.valueOf(0.5)); 
				dto1.setMaxCommTermYrs(15);
				dto1.setUpfrontCommClawbackInYrs(5);
				dto1.setPcrClawbackInMonths(12);
				dto1.setStartDate(Date.valueOf("2011-02-19"));
				dto1.setEndDate(Date.valueOf("2012-02-19"));;
				//dto.setEndDateActive(endDateActive);
				dto1.setCompetitorDiscount(false);
				diffDTO.add(dto1);
				return diffDTO;
	}

	public List<DifferentialPricingFactorDTO> findDiffFactorForSegmentAndAqcAppType(SegmentNameDTO segmentType, AQCApplicableType applicableType) {
		return null;
	}

	public List<SegmentNameDTO> filterAllSegmentName() {
		//List<SegmentNameDTO> allNames = filterSegmentName1.findAllSegmentNames();
		//return allNames;
		//MSK#Change
		List<SegmentNameDTO> allNames =new ArrayList<SegmentNameDTO>();
		 SegmentNameDTO segmentNameObject=new SegmentNameDTO();
		 segmentNameObject.setId(26);
		 segmentNameObject.setSegmentName("Agency");
		 SegmentDTO sdto=new SegmentDTO();
		 //sdto.setId(26);
		 segmentNameObject.setSelected(sdto);
		 allNames.add(segmentNameObject);
		 
		 SegmentNameDTO segmentNameObject1=new SegmentNameDTO();
		 segmentNameObject1.setId(56);
		 segmentNameObject1.setSegmentName("Axcess");
		 sdto=new SegmentDTO();
		 //sdto.setId(26);
		 segmentNameObject1.setSelected(sdto);
		 allNames.add(segmentNameObject1);
		 
		return allNames;
	}
	
	public DifferentialPricingFactorDTO addDiffFactor(DifferentialPricingFactorDTO add){
		//return diffFactorManager.addDiffFactor(add);
		//MSK#Change
				System.out.println("DifferentialFactorGuiController:addDiffFactor()--------------------");
				DifferentialPricingFactorDTO dto1=new DifferentialPricingFactorDTO();
				dto1.setId(27); 
				dto1.setAqcValue("F"); 
				 SegmentNameDTO segmentNameObject1=new SegmentNameDTO();
				 segmentNameObject1.setId(28);
				 segmentNameObject1.setSegmentName("Agency");
				 SegmentDTO sdto1=new SegmentDTO();
				 //sdto.setId(26);
				 segmentNameObject1.setSelected(sdto1);
				 dto1.setSegmentNameObject(segmentNameObject1);
				
				dto1.setAqcProductCode(AQCProductType.E2000_IB);
				dto1.setAqcAppCode(1);
				dto1.setPremiumDiscountPercent(BigDecimal.valueOf(0)); 
				dto1.setDeferredCommPercent(BigDecimal.valueOf(0)); 
				dto1.setDeferredPeriodInYrs(0);
				dto1.setMonthPremiumAFactor(BigDecimal.valueOf(11.4));
				dto1.setPcrEnhancementPercent(BigDecimal.valueOf(1));
				dto1.setMaxUpfrontCommPercent(BigDecimal.valueOf(0.5)); 
				dto1.setMaxCommTermYrs(15);
				dto1.setUpfrontCommClawbackInYrs(5);
				dto1.setPcrClawbackInMonths(12);
				dto1.setStartDate(Date.valueOf("2011-02-19"));
				dto1.setEndDate(Date.valueOf("2012-02-19"));;
				//dto.setEndDateActive(endDateActive);
				dto1.setCompetitorDiscount(false);
				System.out.println("DifferentialFactorGuiController:addDiffFactor()--------------------End");
				return dto1;
	}
    public DifferentialPricingFactorDTO updDiffFactor(DifferentialPricingFactorDTO upt){
    	//return diffFactorManager.updDiffFactor(upt);
    	//MSK#Cahnge
    	logger.info("DifferentialFactorGuiController:updDiffFactor()--------------------");
    	return null;

    }

	public List<SegmentDTO> allAvailableSegmentName() {
		//return segmentManager.findAllSegments();
		//MSK#Cahnge
		logger.info("DifferentialFactorGuiController:allAvailableSegmentName()--------------------");
    	List<SegmentDTO> lDto=new ArrayList<SegmentDTO>();
    	SegmentDTO dto=new SegmentDTO();
    	dto.setId(26);
    	dto.setSegmentContextId(1l);
    	dto.setSegmentAnchorId(26);
    	dto.setSegmentType(SegmentContextType.AGREEMENT);

    	SegmentNameDTO segmentNameObject=new SegmentNameDTO();
    	segmentNameObject.setId(26);
    	segmentNameObject.setSegmentName("Agreement");
    	dto.setSegmentNameId(segmentNameObject);
    	dto.setModificationTime(Timestamp.valueOf("2018-09-01 09:01:15"));
    	dto.setRiskAqc("V");
    	dto.setInvAqc("V1");;
    	dto.setElmRiskAqc("Elm");
    	dto.setElmInvAqc("elmInv");
    	dto.setShortTermAqc("Short");
    	dto.setStartDate(Date.valueOf("2015-03-31"));
    	dto.setEndDate(Date.valueOf("2015-03-31"));
    	dto.setEndDateActive("endACT");
    	lDto.add(dto);
    	
    	return lDto;
	}
	
	public List<ProductDTO> allAvailableProducts(){
		return null;
	}
	
	public List<SegmentNameDTO> findAllSegmentNamesList() {
		List<SegmentNameDTO> segNameManager = null;
		try {
			//segNameManager = segmentNameManager.findAllSegmentNames();
			//MSK#Change
			segNameManager = filterAllSegmentName();
			if (segNameManager.size() < 0) {
				segNameManager = new ArrayList<SegmentNameDTO>();
			}
		}catch (Exception ex){
			System.err.println("Nothing found in find all segment Names");
		}
		return segNameManager;
	}
	
	public void doFactorBroadcast() {
		logger.info("Broadcast messaging--------");
		//factorBroadcastController.broadcastFactorTable();
	}
}
