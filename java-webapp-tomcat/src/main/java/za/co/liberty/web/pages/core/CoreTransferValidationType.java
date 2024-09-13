package za.co.liberty.web.pages.core;

/**
 * Defines the type of validation errors that are used for Request Enquiry 
 * 
 * @author SSS1107 - 27 Jun 2012
 *
 */
public enum CoreTransferValidationType {

	REQUIRE_FROM_CONSULTANT("required.fromconsultant"),
	REQUIRE_TO_CONSULTANT("required.toconsultant"),
	REQUIRE_TRANSFER_TYPE("required.transfertype"),
	
	FROM_CONSULTANT_TYPE("from.consultant.type"),
	FROM_CONSULTANT_STATUS("from.consultant.status"),
	FROM_CONSULTANT_COMPASS("from.consultant.compass"),
	
	TO_CONSULTANT_TYPE("to.consultant.type"),
	TO_CONSULTANT_STATUS("to.consultant.status"),
	TO_CONSULTANT_COMPASS("to.consultant.compass");
	
//	From consultant may not be an organisation (for specified channel)
	
	private String messageKey;
	@SuppressWarnings("unused")
	private String[] parameters;
	
	CoreTransferValidationType(String messageKey, String ... parameters) {
		this.messageKey = messageKey;
		this.parameters = parameters;
	}
	
	public String getMessageKey() {
		return messageKey;
	}

}
