package za.co.liberty.web.pages.fitprop;

import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.pages.BaseWindowPage;
import za.co.liberty.web.pages.maintainagreement.FAISLicensePanel;
import za.co.liberty.web.pages.maintainagreement.model.FAISLicensePanelModel;

/**
 * FAIS Page to display on the licence details for a given licence
 * @author DZS2610
 *
 */
public class FAISPopupPage  extends BaseWindowPage  {
	private static final long serialVersionUID = 1L;
	
	private long agreementNumber;
	
	public FAISPopupPage(long agreementnumber,FAISLicensePanelModel panelModel){	
		agreementNumber = agreementnumber;
		add(new FAISLicensePanel("licencedetails",EditStateType.VIEW,panelModel));
	}
	
	@Override
	public String getPageName() {	
		return "FAIS details for agreement " + agreementNumber;
	}
	
}
