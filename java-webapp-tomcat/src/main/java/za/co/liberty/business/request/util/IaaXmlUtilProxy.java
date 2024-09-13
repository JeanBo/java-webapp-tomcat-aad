package za.co.liberty.business.request.util;

import javax.ejb.Stateless;

import org.apache.log4j.Logger;

import za.co.liberty.exceptions.data.InconsistentDataException;
import za.co.liberty.exceptions.data.ValidationException;
import za.co.liberty.interfaces.agreements.requests.RequestKindType;
import za.co.liberty.policyinfo.jaxb.PolicyInfoLibertyMessageJXBO;
import za.co.liberty.srs.domain.AcceptController;
import za.co.liberty.srs.integration.bind.EarningOrDeduction;
import za.co.liberty.srs.integration.bind.Message;
import za.co.liberty.srs.integration.bind.PreTransaction;
import za.co.liberty.srs.integration.util.IaaXmlUtil;
import za.co.liberty.srs.vo.earningdeduction.EarningDeductionVO;
import za.co.liberty.srs.vo.policyinfo.PolicyInformationTransactionVO;
import za.co.liberty.srs.vo.pretransaction.PreTransactionVO;

/**
 * The implementation of the proxy class.  Used to authorise old requests from the new GUI.
 * 
 * @author JZB0608 -25/01/2019
 *
 */
@Stateless
public class IaaXmlUtilProxy implements IIaaXmlUtilProxy {

	
	private static Logger logger = Logger.getLogger(IaaXmlUtilProxy.class);

	/**
	 * Convert an IAA XML message to a DPE transaction VO object.
	 * 
	 * @param xml
	 * @return
	 * @throws ValidationException
	 */
	public PreTransactionVO convertXMLToPreTransaction(String xml) throws ValidationException {
		try {
			Message message = IaaXmlUtil.toIAAMessage(xml);
			PreTransaction preTransaction = IaaXmlUtil.navigateToPreTransactionBinding(message);
			PreTransactionVO preTransactionVO = IaaXmlUtil.toPreTransactionVO(preTransaction);
			if (preTransactionVO.getAgreementCodes() == null) {
				throw new InconsistentDataException("Agreement Code not found");
			}
			return preTransactionVO;
		}	catch (Exception e) {
			logger.error("Error getting policy earnings",e);
			throw new ValidationException("Error getting policy earnings");
		}
	}
	
	
	/**
	 * Convert an IAA XML message to a PolicyInfo transaction VO object.
	 * 
	 * getPolicyInfoMessage
	 * getPolicyInfoTransactionVO
	 * 
	 * @param xml
	 * @return
	 * @throws ValidationException
	 */
	public PolicyInformationTransactionVO convertXMLToPolicyInfoTransaction(String xml) throws ValidationException {
		try {
			PolicyInfoLibertyMessageJXBO message = IaaXmlUtil.getPolicyInfoMessage(RequestKindType.RecordPolicyInfo.getRequestKind(), xml);
			if (message ==null) {
				throw new InconsistentDataException("Unable to convert Policy Info message");
			}
			return new AcceptController().getPolicyInfoTransactionVO(message);
		}	catch (Exception e) {
			logger.error("Error getting policy earnings",e);
			throw new ValidationException("Error getting policy info transaction");
		}
	}
	
	
	/**
	 * Convert an IAA XML message to a DPE transaction VO object.
	 * 
	 * @param xml
	 * @return
	 * @throws ValidationException
	 */
	public EarningDeductionVO convertXMLToEarningDeduction(String xml) throws ValidationException {
		try {
			Message message = IaaXmlUtil.toIAAMessage(xml);
			EarningOrDeduction earningOrDeduction = IaaXmlUtil.navigateToEarningOrDeductionBinding(message);
			EarningDeductionVO earningDeductionVO = IaaXmlUtil.toEarningOrDeductionVO(earningOrDeduction);
			System.out.println("earningDeductionVO = " + earningDeductionVO);
			if (earningDeductionVO.getAgreementCodes() == null) {
				throw new InconsistentDataException("Agreement Code not found");
			}
			return earningDeductionVO;
		}	catch (Exception e) {
			logger.error("Error getting earning and deduction transaction from message",e);
			throw new ValidationException("Error getting earning and deduction transaction");
		}
	}


}
