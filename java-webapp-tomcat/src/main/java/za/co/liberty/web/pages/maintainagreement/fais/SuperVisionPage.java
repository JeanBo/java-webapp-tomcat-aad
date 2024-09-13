package za.co.liberty.web.pages.maintainagreement.fais;

import java.util.Date;

import za.co.liberty.dto.party.fais.FAISLicenseCategoryDTO;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.pages.BaseWindowPage;
import za.co.liberty.web.wicket.modal.SRSModalWindow;

public class SuperVisionPage extends BaseWindowPage {
	private static final long serialVersionUID = 1L;
	String categoryId="";
	
	public SuperVisionPage(EditStateType editStateType , FAISLicenseCategoryDTO currentFaisLicenseCategoryDTO, 
			SRSModalWindow window ,String categoryId,Date agreementStartDate) {
		super();
		this.categoryId=categoryId;
		
		SuperVisionEditPanel superVisionPanel=new SuperVisionEditPanel("SuperVision", editStateType, 
				currentFaisLicenseCategoryDTO,window,categoryId,agreementStartDate );
		add(superVisionPanel);
	}

	@Override
	public String getPageName() {		
		return "Supervison for category "+ categoryId;
	}

}
