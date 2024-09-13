package za.co.liberty.web.pages.request;

/**
 * Defines the type of validation errors that are used for Request Enquiry 
 * 
 * @author JZB0608 - 27 Jan 2010
 *
 */
public enum RequestEnquiryValidationType {
	REQUIRE_REQUEST_KIND_OR_CATEGORY ("required.requestkind"),
	REQUIRE_REQUEST_KIND_OR_CATEGORY_DIVIDER ("required.requestkind.divider"),
	DATE_START_BEFORE_END ("date.start.before.end"),
	VIEW_SELECTION_REQUIRED ("view.selection.required"),
	VIEW_SELECTION_MULTIPLE ("view.selection.multiple"),
	VIEW_PROPERTY_ONLY_REQUEST ("view.property.only.request", "requestKind"),
	
	BULK_STATUS_INVALID ("bulk.status.invalid", "type", "status"),
	BULK_SELECTION_REQUIRED ("bulk.selection.required", "action"),
	BULK_SELECTION_MAX ("bulk.selection.max", "action", "max", "type"),
	BULK_SUCCESS ("bulk.success", "action"),
	BULK_SUCCESS_PARTIAL ("bulk.success.partial", "action", "requestIds"),
    BULK_ERROR ("bulk.error", "requestIds", "message");
	
	
	private String messageKey;
	@SuppressWarnings("unused")
	private String[] parameters;
	
	RequestEnquiryValidationType(String messageKey, String ... parameters) {
		this.messageKey = messageKey;
		this.parameters = parameters;
	}
	
	public String getMessageKey() {
		return messageKey;
	}

}
