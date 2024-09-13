package za.co.liberty.web.pages.advancedPractice.model;

import java.io.Serializable;
import java.util.List;

import za.co.liberty.dto.advancedPractice.AdvancedPracticeDTO;
import za.co.liberty.web.data.pages.IModalMaintenancePageModel;
import za.co.liberty.web.data.pages.ITabbedPageModel;

public class MaintainAdvancedPracticePageModel implements  ITabbedPageModel<AdvancedPracticeDTO>, 
		Serializable , Cloneable, IMaintainAdvancedPracticePageModel, IModalMaintenancePageModel<AdvancedPracticeDTO> {

	boolean modalSuccess = false;
	
	/* (non-Javadoc)
	 * @see za.co.liberty.web.pages.advancedPractice.model.IMaintainAdvancedPracticePageModel#clone()
	 */
	/* (non-Javadoc)
	 * @see za.co.liberty.web.pages.advancedPractice.model.IMaintainAdvancedPracticePageModel#clone()
	 */
	public Object clone() {		
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {			
			e.printStackTrace();
		}
		return null;
	}

	private static final long serialVersionUID = 1643792587546952282L;	
	
	private String uacfID;
	
	private AdvancedPracticeDTO practiceDTO;
	
	private AdvancedPracticeDTO advancedPracticeDTOBeforeImage;

	
	private AdvancedPracticeDTO selectedItem;
	
    private AdvancedPracticePanelModel panelModel;

	
	private int currentTab = -1;
	@SuppressWarnings("unchecked")
	private Class currentTabClass;
	
	/* (non-Javadoc)
	 * @see za.co.liberty.web.pages.advancedPractice.model.IMaintainAdvancedPracticePageModel#getCurrentTabClass()
	 */
	@SuppressWarnings("unchecked")
	public Class getCurrentTabClass() {		
		return currentTabClass;
	}

	
	public MaintainAdvancedPracticePageModel() {
		
	}
	
	/* (non-Javadoc)
	 * @see za.co.liberty.web.pages.advancedPractice.model.IMaintainAdvancedPracticePageModel#setCurrentTabClass(java.lang.Class)
	 */
	@SuppressWarnings("unchecked")
	public void setCurrentTabClass(Class currentTabClass) {
		this.currentTabClass = currentTabClass;		
	}
	

	/* (non-Javadoc)
	 * @see za.co.liberty.web.pages.advancedPractice.model.IMaintainAdvancedPracticePageModel#getCurrentTab()
	 */
	public int getCurrentTab() {
		return currentTab;
	}

	/* (non-Javadoc)
	 * @see za.co.liberty.web.pages.advancedPractice.model.IMaintainAdvancedPracticePageModel#setCurrentTab(int)
	 */
	public void setCurrentTab(int currentTab) {
		this.currentTab = currentTab;
	}

	/* (non-Javadoc)
	 * @see za.co.liberty.web.pages.advancedPractice.model.IMaintainAdvancedPracticePageModel#getCostCenterSelection()
	 */
	public String getCostCenterSelection() {
		// TODO Auto-generated method stub
		return "Matthew Testing";
	}


	/* (non-Javadoc)
	 * @see za.co.liberty.web.pages.advancedPractice.model.IMaintainAdvancedPracticePageModel#getUacfID()
	 */
	public String getUacfID() {
		return uacfID;
	}

	/* (non-Javadoc)
	 * @see za.co.liberty.web.pages.advancedPractice.model.IMaintainAdvancedPracticePageModel#setUacfID(java.lang.String)
	 */
	public void setUacfID(String uacfID) {
		this.uacfID = uacfID;
	}


	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	/* (non-Javadoc)
	 * @see za.co.liberty.web.pages.advancedPractice.model.IMaintainAdvancedPracticePageModel#getSelectedItem()
	 */
	public AdvancedPracticeDTO getSelectedItem() {
		
		return selectedItem;
	}


	/* (non-Javadoc)
	 * @see za.co.liberty.web.pages.advancedPractice.model.IMaintainAdvancedPracticePageModel#getSelectionList()
	 */
	public List<AdvancedPracticeDTO> getSelectionList() {
		// TODO Auto-generated method stub
		return null;
	}


	/* (non-Javadoc)
	 * @see za.co.liberty.web.pages.advancedPractice.model.IMaintainAdvancedPracticePageModel#setSelectedItem(za.co.liberty.dto.advancedPractice.AdvancedPracticeDTO)
	 */
	public void setSelectedItem(AdvancedPracticeDTO selected) {
		this.selectedItem=selected;
	}

	/* (non-Javadoc)
	 * @see za.co.liberty.web.pages.advancedPractice.model.IMaintainAdvancedPracticePageModel#setSelectionList(java.util.List)
	 */
	public void setSelectionList(List<AdvancedPracticeDTO> selectionList) {
		// TODO Auto-generated method stub
		
	}


	/* (non-Javadoc)
	 * @see za.co.liberty.web.pages.advancedPractice.model.IMaintainAdvancedPracticePageModel#setAdvancedPracticeDTO(za.co.liberty.dto.advancedPractice.AdvancedPracticeDTO)
	 */
	public void setAdvancedPracticeDTO(AdvancedPracticeDTO practiceDTO) {
		this.practiceDTO = practiceDTO;		
	}


	/* (non-Javadoc)
	 * @see za.co.liberty.web.pages.advancedPractice.model.IMaintainAdvancedPracticePageModel#getAdvancedPracticeDTO()
	 */
	public AdvancedPracticeDTO getAdvancedPracticeDTO() {
		return practiceDTO;
	}


	/* (non-Javadoc)
	 * @see za.co.liberty.web.pages.advancedPractice.model.IMaintainAdvancedPracticePageModel#getAdvancedPracticeDTOBeforeImage()
	 */
	public AdvancedPracticeDTO getAdvancedPracticeDTOBeforeImage() {
		return advancedPracticeDTOBeforeImage;
	}


	/* (non-Javadoc)
	 * @see za.co.liberty.web.pages.advancedPractice.model.IMaintainAdvancedPracticePageModel#setAdvancedPracticeDTOBeforeImage(za.co.liberty.dto.advancedPractice.AdvancedPracticeDTO)
	 */
	public void setAdvancedPracticeDTOBeforeImage(
			AdvancedPracticeDTO advancedPracticeDTOBeforeImage) {
		this.advancedPracticeDTOBeforeImage = advancedPracticeDTOBeforeImage;
	}


	/* (non-Javadoc)
	 * @see za.co.liberty.web.pages.advancedPractice.model.IMaintainAdvancedPracticePageModel#getPanelModel()
	 */
	public AdvancedPracticePanelModel getPanelModel() {
		return panelModel;
	}


	/* (non-Javadoc)
	 * @see za.co.liberty.web.pages.advancedPractice.model.IMaintainAdvancedPracticePageModel#setPanelModel(za.co.liberty.web.pages.advancedPractice.model.AdvancedPracticePanelModel)
	 */
	public void setPanelModel(AdvancedPracticePanelModel panelModel) {
		this.panelModel = panelModel;
	}


	/* (non-Javadoc)
	 * @see za.co.liberty.web.pages.advancedPractice.model.IMaintainAdvancedPracticePageModel#getPracticeDTO()
	 */
	public AdvancedPracticeDTO getPracticeDTO() {
		return practiceDTO;
	}


	/* (non-Javadoc)
	 * @see za.co.liberty.web.pages.advancedPractice.model.IMaintainAdvancedPracticePageModel#setPracticeDTO(za.co.liberty.dto.advancedPractice.AdvancedPracticeDTO)
	 */
	public void setPracticeDTO(AdvancedPracticeDTO practiceDTO) {
		this.practiceDTO = practiceDTO;
	}



	@Override
	public boolean isModalWizardSucces() {
		return modalSuccess;
	}


	@Override
	public void setModalWizardSuccess(boolean success) {
		modalSuccess = success;
	}

	@Override
	public String getModalWizardMessage() {
		return null;
	}

}
