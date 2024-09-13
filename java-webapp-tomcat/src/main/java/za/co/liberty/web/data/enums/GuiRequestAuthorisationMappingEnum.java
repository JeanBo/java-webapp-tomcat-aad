package za.co.liberty.web.data.enums;

import java.util.HashMap;
import java.util.Map;

import za.co.liberty.interfaces.gui.GuiRequestKindType;
import za.co.liberty.web.pages.admin.ratingtables.MIRatingFilterAuthorisationPanel;
import za.co.liberty.web.pages.businesscard.MaintainBusinessCardAuthorisationPanel;
import za.co.liberty.web.pages.core.CoreTransferAuthorisationPanel;
import za.co.liberty.web.pages.franchisetemplates.MaintainFranchiseTemplateAuthorisationPanel;
import za.co.liberty.web.pages.hierarchy.MaintainHierarchyNodeAuthorisationPanel;
import za.co.liberty.web.pages.maintainagreement.MaintainAgreementAuthorisationPanel;
import za.co.liberty.web.pages.party.MaintainAdvancedPracticeAuthorisationPanel;
import za.co.liberty.web.pages.party.MaintainPartyAuthorisationPanel;
import za.co.liberty.web.pages.party.MaintainPartyHierarchyAuthorisationPanel;
import za.co.liberty.web.pages.request.BaseRequestViewAndAuthorisePanel;
import za.co.liberty.web.pages.salesBCLinking.ServicingPanelsAuthorisationPanel;

/**
 * Enum used to map gui requests to pages to view
 * @author JZB0608 - 16 Feb 2010
 *
 */
public enum GuiRequestAuthorisationMappingEnum implements IAuthorisationMapping {

	/* Enums */
	MAINTAIN_PARTY (GuiRequestKindType.MaintainParty, MaintainPartyAuthorisationPanel.class),
	MAINTAIN_AGREEMENT (GuiRequestKindType.MaintainAgreement, MaintainAgreementAuthorisationPanel.class),
	MAINTAIN_HIERARCHY_NODE (GuiRequestKindType.MaintainHierarchyNode, MaintainHierarchyNodeAuthorisationPanel.class),
	CREATE_AGREEMENT(
			GuiRequestKindType.CreateAgreement, 
			MaintainAgreementAuthorisationPanel.class),
	CREATE_AGREEMENT_WITH_MAINTAIN_PARTY(
			GuiRequestKindType.CreateAgreementWithMaintainParty, 
			MaintainAgreementAuthorisationPanel.class),
	TERMINATE_AGREEMENT(GuiRequestKindType.TerminateAgreement, MaintainAgreementAuthorisationPanel.class),
	MAINTAIN_PARTY_HIERARCHY (GuiRequestKindType.MaintainPartyHierarchy, MaintainPartyHierarchyAuthorisationPanel.class),
	MAINTAIN_BUSINESS_CARD (GuiRequestKindType.MaintainBusinessCard, MaintainBusinessCardAuthorisationPanel.class),
	MAINTAIN_FRANCHISE_TEMPLATE (GuiRequestKindType.MaintainFranchiseTemplate, MaintainFranchiseTemplateAuthorisationPanel.class),
	MAINTAIN_ADVANCED_PRACTICE (GuiRequestKindType.MaintainAdvancedPractice , MaintainAdvancedPracticeAuthorisationPanel.class),
	TERMINATE_ADVANCED_PRACTICE (GuiRequestKindType.TerminateAdvancedPractice , MaintainAdvancedPracticeAuthorisationPanel.class),
	
	MAINTAIN_MI_TABLE_DATA (GuiRequestKindType.MaintainMITableData , MIRatingFilterAuthorisationPanel.class),
	// Added for Market Integration Sweta Menon 16th Mar 2016
	MAINTAIN_SERVICING_PANEL (GuiRequestKindType.MaintainServicingPanel , ServicingPanelsAuthorisationPanel.class),
	// Added for Market Integration Sweta Menon 16th Mar 2016
//	Added for core succession
	CORE_TRANSFER (GuiRequestKindType.CoreTransfer,CoreTransferAuthorisationPanel.class),
//End of core succession
	BULK_CREATE_AGREEMENT(
			GuiRequestKindType.BulkCreateAgreement, 
			MaintainAgreementAuthorisationPanel.class);
	
	/* Class attributes */
	private static Map<GuiRequestKindType, GuiRequestAuthorisationMappingEnum> guiRequestMap;
	private GuiRequestKindType guiRequestKind; 
	private Class<? extends BaseRequestViewAndAuthorisePanel> authorisationPanelClass;
	
	/**
	 * Default constructor 
	 * 
	 * @param guiRequestKind
	 * @param authorisationPanelClass
	 */
	GuiRequestAuthorisationMappingEnum(GuiRequestKindType guiRequestKind, 
			Class<? extends BaseRequestViewAndAuthorisePanel> authorisationPanelClass) {
		this.guiRequestKind = guiRequestKind; 
		this.authorisationPanelClass = authorisationPanelClass;
	}
	
	/**
	 * Get the Enum mapping for the given GuiRequestKindType
	 * @param type
	 * @return
	 */
	public static GuiRequestAuthorisationMappingEnum getMappingEnumForGuiRequestKind(GuiRequestKindType type) {
		if (guiRequestMap == null) {
			// Initialise the map
			Map<GuiRequestKindType, GuiRequestAuthorisationMappingEnum> map = new HashMap<GuiRequestKindType, 
				GuiRequestAuthorisationMappingEnum>();
			for (GuiRequestAuthorisationMappingEnum e : GuiRequestAuthorisationMappingEnum.values()) {
				if (map.get(e.getGuiRequestKind())!=null) {
					throw new IllegalArgumentException("The Gui Request kind \"" 
							+ e.getGuiRequestKind() 
							+ "\" has already been defined");
				}
				map.put(e.getGuiRequestKind(), e);
			}
			guiRequestMap = map;
		}
		return guiRequestMap.get(type);
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
	 * Get the GuiRequestKind that is catered for.
	 * 
	 * @return
	 */
	public GuiRequestKindType getGuiRequestKind() {
		return guiRequestKind;
	}
	
}
