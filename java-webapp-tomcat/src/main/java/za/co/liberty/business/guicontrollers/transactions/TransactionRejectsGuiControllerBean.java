/**
 * 
 */
package za.co.liberty.business.guicontrollers.transactions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Node;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;
import org.xml.sax.InputSource;

import za.co.liberty.dto.pretransactionreject.ErrorCodeElementDTO;
import za.co.liberty.dto.pretransactionreject.PreTransactionRejectSearchDTO;
import za.co.liberty.dto.pretransactionreject.RejectElementDTO;
import za.co.liberty.exceptions.ApplicationException;
import za.co.liberty.interfaces.agreements.requests.RequestKindType;
import za.co.liberty.srs.integration.util.ErrorCode;

/**
 * GUI Controller for the transaction reject page.
 * 
 * @author zzt2108
 *
 */
@Stateless
public class TransactionRejectsGuiControllerBean implements TransactionRejectsGuiController {
	
	Logger logger = Logger.getLogger(TransactionRejectsGuiControllerBean.class);
	
//	@EJB
//	IPreTransactionRejectManagement transactionRejectManagement;
//	
//	@EJB
//	FundCodeEntityManager fundCodeEntityManager;
//	
//	@EJB
//	ProductCodesEntityManager productCodesEntityManager;
//	
//	@EJB 
//	IRequestEnquiryManagement requestEnquiryManagement;
//	
//	@EJB 
//	IPreTransactionRejectManagement preTransactionRejectManagement;

	
	static List<String> COMPONENT_LIST = Arrays.asList(new String[] {
			"Asset Under Management","Blueprint Corporate Allowance","Blueprint Individual Allowance",
			"Blueprint Online","CAL Commission","Charter Life","CommCalc","Compass","Compass Umbrella","GLA IPP",
			"Group Ipp or Astute","LA Commission","Liber8 Umbrella"
	});

	/* (non-Javadoc)
	 * @see za.co.liberty.business.guicontrollers.transactions.TransactionRejectsGuiController#getComponentIdFromRejectElement()
	 */
	public List<String> getComponentIdFromRejectElement() throws ApplicationException {
		return new ArrayList<String> (COMPONENT_LIST);
	}


	
	/**
	 * Make the XML pretty. 
	 * 
	 * @param xmlMessage
	 * @return
	 */
	public String prettyPrintXML(String xmlMessage) {
		String xml = xmlMessage;
		try {
            final InputSource src = new InputSource(new StringReader(xml));
            final Node document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(src).getDocumentElement();
            final Boolean keepDeclaration = Boolean.valueOf(xml.startsWith("<?xml"));

        //May need this: System.setProperty(DOMImplementationRegistry.PROPERTY,"com.sun.org.apache.xerces.internal.dom.DOMImplementationSourceImpl");
            final DOMImplementationRegistry registry = DOMImplementationRegistry.newInstance();
            final DOMImplementationLS impl = (DOMImplementationLS) registry.getDOMImplementation("LS");
            final LSSerializer writer = impl.createLSSerializer();

            writer.getDomConfig().setParameter("format-pretty-print", Boolean.TRUE); // Set this to true if the output needs to be beautified.
            writer.getDomConfig().setParameter("xml-declaration", keepDeclaration); // Set this to true if the declaration is needed to be outputted.

            return writer.writeToString(document);

		} catch (Exception e) {
			// NEating exceptions
		}
		
		return xml;
	}

	// TODO, use something like this as a test!
	public static void main(String[] args) {
		System.out.println(new TransactionRejectsGuiControllerBean().prettyPrintXML("<h1><h2><h3></h3></h2></h1>"));
	}
	
	public List<RejectElementDTO> searchRejectedElement(PreTransactionRejectSearchDTO rejectSearchDTO) {
		
		List<RejectElementDTO> result = new ArrayList<>();
		
		// DPE
		RejectElementDTO d = new RejectElementDTO();
		d.setOid(51);
		d.setAmount(new BigDecimal("100.00"));
		d.setAmountCurrCode((short) 1);
		d.setCommissionKind(40);
		d.setComponentId("GLA IPP");
		d.setXmlMessage(getStringFromInputstream(this.getClass().getResourceAsStream("dpe.reject.xml")));
		d.setRequestKind((short) RequestKindType.DistributePolicyEarning.getRequestKind());
		d.setSrsAgmtNo(145890);
		d.setRejectTimestamp(new Date());
		d.setExceptionChain("Some serious exception here in za.co.liberty.jean :)");
		d.setErrorCode(new ErrorCodeElementDTO(ErrorCode.COMPONENT_NOT_FOUND.getValue(), "A serious Error", 
				"A GUI error message",(short)0 ));
		
		result.add(d);
		
		
		// VED  with no type configured
		d = new RejectElementDTO();
		d.setOid(101);
		d.setXmlMessage(getStringFromInputstream(this.getClass().getResourceAsStream("ved.reject.xml")));
		d.setRequestKind((short) RequestKindType.ProcessVariableEarningsOrDeductions.getRequestKind());
		d.setSrsAgmtNo(145890);
		d.setRejectTimestamp(new Date());
		d.setExceptionChain("Some serious exception here in za.co.liberty.jean :)");
		d.setErrorCode(new ErrorCodeElementDTO(ErrorCode.COMPONENT_NOT_FOUND.getValue(), "A serious Error", 
				"VED with type 9999",(short)0 ));
		
		result.add(d);
		
		
		// VED with type configured
		d = new RejectElementDTO();
		d.setOid(102);
		d.setXmlMessage(getStringFromInputstream(this.getClass().getResourceAsStream("ved.reject2.xml")));
		d.setRequestKind((short) RequestKindType.ProcessVariableEarningsOrDeductions.getRequestKind());
		d.setSrsAgmtNo(145890);
		d.setRejectTimestamp(new Date());
		d.setExceptionChain("Some serious exception here in za.co.liberty.jean :)");
		d.setErrorCode(new ErrorCodeElementDTO(ErrorCode.COMPONENT_NOT_FOUND.getValue(), "A serious Error", 
				"VED with correct type",(short)0 ));
		
		result.add(d);
		
		
		return result;
		
	}
	
	/**
	 * Convert input stream from file to string
	 * 
	 * internal utility method for testing
	 * 
	 * @param is
	 * @return
	 */
	public String getStringFromInputstream(InputStream is) {

		BufferedReader reader = new BufferedReader(new InputStreamReader(is));

		StringBuffer sb = new StringBuffer();
		String str;
		try {
			while ((str = reader.readLine()) != null) {
				sb.append(str);
				sb.append("\n");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return sb.toString();
	}
	
}
