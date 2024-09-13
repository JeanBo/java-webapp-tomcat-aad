package za.co.liberty.web.pages.request;

import java.io.Serializable;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

import za.co.liberty.dto.agreement.request.RequestEnquiryRowDTO;
import za.co.liberty.dto.contracting.ResultAgreementDTO;
import za.co.liberty.dto.contracting.ResultPartyDTO;
import za.co.liberty.dto.gui.request.ViewRequestContextModelDTO;

/**
 * Context panel for request related data
 * 
 * @author JZB0608 - 10 Feb 2010
 *
 */
public class ViewRequestContextPanel extends Panel {
	
	private static final long serialVersionUID = -2977732378799646854L;

	protected ViewRequestContextModelDTO requestContextModel;
	
	/**
	 * Default constructor 
	 * 
	 * @param id
	 * @param pageModel
	 */
	public ViewRequestContextPanel(String id, ViewRequestContextModelDTO requestContextModel) {
		super(id);
		
		RequestEnquiryRowDTO dto = requestContextModel.getRequestDto();
		
		add(new Label("requestId", new Model(dto.getRequestId())));
		add(new Label("requestKind", new Model(dto.getRequestKindType())));
		add(new Label("requestStatus", new Model(dto.getStatusType())));
		add(new Label("guiRequestKind", new Model(convertNullValues(
				requestContextModel.getGuiRequestKind()))));
		
		add(new Label("dateRequested", new Model(dto.getRequestDate())));
		add(new Label("dateExecuted", new Model(dto.getExecutedDate())));
		add(new Label("requestor", new Model(dto.getRequestor())));
		add(new Label("authoriser", new Model(dto.getAuthoriser1())));
		add(new Label("authoriser2", new Model(dto.getAuthoriser2())));
		
		ResultAgreementDTO agreementDto = requestContextModel.getAgreementDto();
		if (agreementDto == null) {
			agreementDto = new ResultAgreementDTO();
		}
		ResultPartyDTO partyDto = requestContextModel.getPartyDto();

		Long agreementNr = agreementDto.getAgreementNumber();
		if (agreementNr!=null && agreementNr.longValue()==0) {
			agreementNr = null;
		}
		
		add(new Label("agreementNr", new Model(convertNullValues(agreementNr))));
		add(new Label("consultantCode", new Model(convertNullValues(agreementDto.getConsultantCodeFormatted()))));
		add(new Label("agreementKind", new Model(convertNullValues(agreementDto.getAgreementDivision()))));
		add(new Label("partyName", new Model(convertNullValues((partyDto==null) ? null : 
					partyDto.getName()))));
		add(new Label("partyType", new Model(convertNullValues((partyDto==null) ? null : 
			partyDto.getTypeName()))));
	}

	private Serializable convertNullValues(Serializable obj) {
		return (obj==null)?"N/A":obj;
	}	
	
}
