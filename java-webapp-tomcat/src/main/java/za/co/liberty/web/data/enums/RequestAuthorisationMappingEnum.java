package za.co.liberty.web.data.enums;

import java.util.HashMap;
import java.util.Map;

import za.co.liberty.interfaces.agreements.requests.RequestKindType;
import za.co.liberty.web.pages.request.BaseRequestViewAndAuthorisePanel;
import za.co.liberty.web.pages.transactions.PolicyTransactionsAuthorisationPanel;
import za.co.liberty.web.pages.transactions.TransactionAuthorisationPanel;

/**
 * Enum used to map requests to pages to view.  This is to support "naked" requests that have all 
 * their data
 * 
 * @author JZB0608 - 21 Jul 2016
 *
 */
public enum RequestAuthorisationMappingEnum implements IAuthorisationMapping {

	DPE (RequestKindType.DistributePolicyEarning,PolicyTransactionsAuthorisationPanel.class),
	AUM (RequestKindType.RecordPolicyInfo,PolicyTransactionsAuthorisationPanel.class),
	PROCESS_EXTERNAL_PAYMENTS(RequestKindType.ProcessExternalPayments, TransactionAuthorisationPanel.class),
	VED(RequestKindType.ProcessVariableEarningsOrDeductions, TransactionAuthorisationPanel.class),
	PROCESS_ADVANCE(RequestKindType.ProcessAdvance, TransactionAuthorisationPanel.class),
	MANUAL_SETTLE(RequestKindType.ManualSettle, TransactionAuthorisationPanel.class),
	VAT_SETTLE_CALCULATION(RequestKindType.VAT, null);

	
	/* Class attributes */
	private static Map<RequestKindType, RequestAuthorisationMappingEnum> requestMap;
	private RequestKindType requestKind; 
	private Class<? extends BaseRequestViewAndAuthorisePanel> authorisationPanelClass;
	
	/**
	 * Default constructor 
	 * 
	 * @param guiRequestKind
	 * @param authorisationPanelClass
	 */
	RequestAuthorisationMappingEnum(RequestKindType guiRequestKind, 
			Class<? extends BaseRequestViewAndAuthorisePanel> authorisationPanelClass) {
		this.requestKind = guiRequestKind; 
		this.authorisationPanelClass = authorisationPanelClass;
	}
	
	/**
	 * Get the Enum mapping for the given RequestKindType
	 * @param type
	 * @return
	 */
	public static RequestAuthorisationMappingEnum getMappingEnumForRequestKind(RequestKindType type) {
		if (requestMap == null) {
			// Initialise the map
			Map<RequestKindType, RequestAuthorisationMappingEnum> map = new HashMap<RequestKindType, 
				RequestAuthorisationMappingEnum>();
			for (RequestAuthorisationMappingEnum e : RequestAuthorisationMappingEnum.values()) {
				if (map.get(e.getRequestKind())!=null) {
					throw new IllegalArgumentException("The Gui Request kind \"" 
							+ e.getRequestKind() 
							+ "\" has already been defined");
				}
				map.put(e.getRequestKind(), e);
			}
			requestMap = map;
		}
		return requestMap.get(type);
	}

	/**
	 * Get the corresponding authorisation panel class
	 * 
	 * @return
	 */
	public Class<? extends BaseRequestViewAndAuthorisePanel> getAuthorisationPanelClass() {
		return authorisationPanelClass;
	}

	/**
	 * Get the RequestKind that is catered for.
	 * 
	 * @return
	 */
	public RequestKindType getRequestKind() {
		return requestKind;
	}
	
}
