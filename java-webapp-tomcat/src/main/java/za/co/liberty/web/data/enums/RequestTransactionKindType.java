package za.co.liberty.web.data.enums;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.wicket.markup.html.panel.Panel;

import za.co.liberty.interfaces.agreements.requests.RequestKindType;
import za.co.liberty.web.pages.transactions.PolicyTransactionsAuthorisationPanel;

/**
 * Enum used to map request kinds to their panels for the Request transaction page
 * 
 * @author JZB0608 - 29 May 2017
 *
 */
public enum RequestTransactionKindType  {

	EXTERNAL_PAYMENT ("External Payments - Section8C",
			RequestKindType.ProcessExternalPayments,
			PolicyTransactionsAuthorisationPanel.class,
			Flags.SHOW_TABLE, Flags.SELECTION_TABLE), 
	RECORD_POLICY_INFO ("Record Policy Info",
			RequestKindType.RecordPolicyInfo,
			PolicyTransactionsAuthorisationPanel.class,Flags.SHOW_TABLE, Flags.SELECTION_TABLE),
	DISTRIBUTED_POLICY_EARNING ("Distributed Policy Earning",
			RequestKindType.DistributePolicyEarning,
			PolicyTransactionsAuthorisationPanel.class),
	VARIABLE_EARNING_DEDUCTION ("Process Variable Earning & Deductions",
			RequestKindType.ProcessVariableEarningsOrDeductions,
			PolicyTransactionsAuthorisationPanel.class),
	// Newest items
	MANUAL_SETTLE ("Manual Settle",
			RequestKindType.ManualSettle,
			PolicyTransactionsAuthorisationPanel.class, Flags.SHOW_TABLE, Flags.INFO_TABLE),
	PROCESS_ADVANCE ("Process Advance",
			RequestKindType.ProcessAdvance,
			PolicyTransactionsAuthorisationPanel.class, Flags.SHOW_TABLE, Flags.INFO_TABLE);

	enum Flags {
		SHOW_TABLE,
		INFO_TABLE,
		SELECTION_TABLE;
	}
	
	/* Class attributes */
	private static Map<RequestKindType, RequestTransactionKindType> requestMap;
	private RequestKindType requestKind; 
	private Class<? extends Panel> panelClass;
	private String description;
	private Set<Flags> flagSet;
	
	/**
	 * Default constructor 
	 * 
	 * @param guiRequestKind
	 * @param authorisationPanelClass
	 */
	RequestTransactionKindType(String description, 
			RequestKindType requestKind, 
			Class<? extends Panel> panelClass,
			Flags ... flags) {
		this.description = description;
		this.requestKind = requestKind; 
		this.panelClass = panelClass;
		this.flagSet = new HashSet<Flags>();
		if (flags!=null) {
			flagSet.addAll(Arrays.asList(flags));
		}
	}
	
	/**
	 * Get the Enum mapping for the given RequestKindType
	 * @param type
	 * @return
	 */
	public static RequestTransactionKindType getMappingEnumForRequestKind(RequestKindType type) {
		if (requestMap == null) {
			// Initialise the map
			Map<RequestKindType, RequestTransactionKindType> map = new HashMap<RequestKindType, 
				RequestTransactionKindType>();
			for (RequestTransactionKindType e : RequestTransactionKindType.values()) {
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
	public Class<? extends Panel> getPanelClass() {
		return panelClass;
	}

	/**
	 * Get the RequestKind that is catered for.
	 * 
	 * @return
	 */
	public RequestKindType getRequestKind() {
		return requestKind;
	}
	
	public String toString() {
		return description;
	}
	
	/**
	 * Indicates if the table should be shown.
	 * 
	 * @return
	 */
	public boolean isShowTable() {
		return flagSet.contains(Flags.SHOW_TABLE); 
	}
	
	/**
	 * Indicates if the table only shows INFORMATION, not selection
	 * 
	 * @return
	 */
	public boolean isShowInfoTable() {
		return flagSet.contains(Flags.INFO_TABLE); 
	}
	
	/**
	 * Indicates if the table should allow selections.
	 * 
	 * @return
	 */
	public boolean isShowSelectionTable() {
		return flagSet.contains(Flags.SELECTION_TABLE); 
	}
}
