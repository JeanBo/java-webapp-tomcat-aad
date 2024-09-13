package za.co.liberty.web.pages.advancedPractice.model;

import java.util.List;

import za.co.liberty.dto.advancedPractice.AdvancedPracticeDTO;

public interface IMaintainAdvancedPracticePageModel {

	/* (non-Javadoc)
	 * @see za.co.liberty.web.pages.advancedPractice.model.IMaintainAdvancedPracticePageModel#clone()
	 */
	public abstract Object clone();

	@SuppressWarnings("unchecked")
	public abstract Class getCurrentTabClass();

	@SuppressWarnings("unchecked")
	public abstract void setCurrentTabClass(Class currentTabClass);

	public abstract int getCurrentTab();

	public abstract void setCurrentTab(int currentTab);

	public abstract String getCostCenterSelection();

	public abstract String getUacfID();

	public abstract void setUacfID(String uacfID);

	public abstract AdvancedPracticeDTO getSelectedItem();

	public abstract List<AdvancedPracticeDTO> getSelectionList();

	public abstract void setSelectedItem(AdvancedPracticeDTO selected);

	public abstract void setSelectionList(
			List<AdvancedPracticeDTO> selectionList);

	public abstract void setAdvancedPracticeDTO(AdvancedPracticeDTO practiceDTO);

	public abstract AdvancedPracticeDTO getAdvancedPracticeDTO();

	public abstract AdvancedPracticeDTO getAdvancedPracticeDTOBeforeImage();

	public abstract void setAdvancedPracticeDTOBeforeImage(
			AdvancedPracticeDTO advancedPracticeDTOBeforeImage);

	public abstract AdvancedPracticePanelModel getPanelModel();

	public abstract void setPanelModel(AdvancedPracticePanelModel panelModel);

	public abstract AdvancedPracticeDTO getPracticeDTO();

	public abstract void setPracticeDTO(AdvancedPracticeDTO practiceDTO);

}