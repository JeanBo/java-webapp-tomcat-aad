package za.co.liberty.web.pages.maintainagreement.fais;

import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;

import za.co.liberty.dto.agreement.maintainagreement.MaintainFAISLicenseDTO;
import za.co.liberty.dto.agreement.properties.FAISLicenseDTO;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.pages.BaseWindowPage;
import za.co.liberty.web.pages.maintainagreement.FAISLicensePanel;
import za.co.liberty.web.pages.maintainagreement.model.FAISLicensePanelModel;

public class FSPFAISLicenseDetailsPage extends BaseWindowPage {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	FAISLicensePanelModel licensePanelModel;
	//public SuperVisionPage(EditStateType editStateType , FAISLicenseCategoryDTO currentFaisLicenseCategoryDTO, ModalWindow window ) {
	public FSPFAISLicenseDetailsPage(EditStateType editStateType, FAISLicensePanelModel  panelmodel, ModalWindow window) {
		super();
		
		licensePanelModel = new FAISLicensePanelModel(new MaintainFAISLicenseDTO(),0L,0,0L,null);
		licensePanelModel.setFaisLicenseDTO(panelmodel.getFspFAISLicence());
		
		FAISLicensePanel licensePanel=new FAISLicensePanel("fspFaisLicenseDetails",editStateType,licensePanelModel);
		
		add(licensePanel);
	}


	@Override
	public String getPageName() {
		
		return "FSP FAIS License Details Page";
	}

}
