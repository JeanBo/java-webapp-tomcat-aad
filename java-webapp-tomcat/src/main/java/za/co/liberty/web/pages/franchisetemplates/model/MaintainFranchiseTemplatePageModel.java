package za.co.liberty.web.pages.franchisetemplates.model;

import java.io.Serializable;
import java.util.List;

import za.co.liberty.business.guicontrollers.hierarchy.IHierarchyGUIController;
import za.co.liberty.dto.common.IDValueDTO;
import za.co.liberty.dto.databaseenum.CostCenterDBEnumDTO;
import za.co.liberty.dto.databaseenum.DatabaseEnumDTO;
import za.co.liberty.dto.gui.templates.FranchiseTemplateDTO;
import za.co.liberty.dto.gui.templates.MaintainFranchiseTemplateDTO;
import za.co.liberty.dto.party.HierarchyNodeDTO;
import za.co.liberty.interfaces.agreements.requests.RequestKindType;
import za.co.liberty.interfaces.party.IPartyNameAndIdFLO;
import za.co.liberty.web.data.pages.ITabbedPageModel;
import za.co.liberty.web.pages.panels.AbstractLinkingPanel.MainForm;
import za.co.liberty.web.system.EJBReferences;
import za.co.liberty.web.system.SRSAuthWebSession;

public class MaintainFranchiseTemplatePageModel implements ITabbedPageModel<MaintainFranchiseTemplateDTO>,
		Serializable , Cloneable {
	
	private int currentTab = -1;	
	
	private Class currentTabClass;
	
	
	private MaintainFranchiseTemplateDTO selectedFranchiseTemplate;	
	
	private MaintainFranchiseTemplateDTO maintainFranchiseTemplateDTO;

	private MaintainFranchiseTemplateDTO maintainFranchiseTemplateDTOBeforeImage;
	
	private FranchiseTemplatePanelModel franchiseTemplatePanelModel;

	/**
	 * 
	 */
	private static final long serialVersionUID = 8983706076567557197L;

	public int getCurrentTab() {

		return currentTab;
	}

	public Class getCurrentTabClass() {

		return currentTabClass;
	}

	public void setCurrentTab(int currentTab) {
		this.currentTab = currentTab;
		
	}

	public void setCurrentTabClass(Class currentTabClass) {
		this.currentTabClass = currentTabClass;
		
	}

	public MaintainFranchiseTemplateDTO getSelectedItem() {

		return selectedFranchiseTemplate;
	}

	public List<MaintainFranchiseTemplateDTO> getSelectionList() {
		return null;
	}

	public void setSelectedItem(MaintainFranchiseTemplateDTO selected) {
		selectedFranchiseTemplate = selected;
		
	}

	public void setSelectionList(List<MaintainFranchiseTemplateDTO> selectionList) {
		
	}

	public MaintainFranchiseTemplateDTO getMaintainFranchiseTemplateDTO() {
		return maintainFranchiseTemplateDTO;
	}

	public void setMaintainFranchiseTemplateDTO(
			MaintainFranchiseTemplateDTO maintainFranchiseTemplateDTO) {
		this.maintainFranchiseTemplateDTO = maintainFranchiseTemplateDTO;
	}

	public void setMaintainFranchiseTemplateDTOBeforeImage(MaintainFranchiseTemplateDTO maintainFranchiseTemplateDTOBeforeImage) {
		this.maintainFranchiseTemplateDTOBeforeImage = maintainFranchiseTemplateDTOBeforeImage;
		
	}

	public MaintainFranchiseTemplateDTO getMaintainFranchiseTemplateDTOBeforeImage() {
		return maintainFranchiseTemplateDTOBeforeImage;
	}

	
	public FranchiseTemplatePanelModel getFranchiseTemplatePanelModel() {
		return franchiseTemplatePanelModel;
	}

	public void setFranchiseTemplatePanelModel(
			FranchiseTemplatePanelModel franchiseTemplatePanelModel) {
		this.franchiseTemplatePanelModel = franchiseTemplatePanelModel;
	}

	public boolean canAdd(){
		List<RequestKindType> unAuthRequestsLocal = getFranchiseTemplatePanelModel().getUnAuthRequests();
		if(unAuthRequestsLocal !=null && unAuthRequestsLocal.size() > 0)	{
			return true;
		}
		else{
			return false;
		}
	}
	
}
