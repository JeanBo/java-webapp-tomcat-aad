package za.co.liberty.web.pages.fitprop.model;

import java.io.Serializable;

import za.co.liberty.srs.integration.api.moodle.qualification.GetQualifications;
import za.co.liberty.web.pages.maintainagreement.model.FAISLicensePanelModel;

/**
 * Panel model for the Fit and Proper Panel
 * 
 * @author DZS2610
 *
 */
public class FitAndProperPanelModel implements Serializable {

    private static final long serialVersionUID = 1L;

    private FAISLicensePanelModel faisLicensePanelModel;

    private GetQualifications webServiceResponseData;

    public GetQualifications getWebServiceResponseData() {
	return webServiceResponseData;
    }

    public void setWebServiceResponseData(GetQualifications webServiceResponseData) {
	this.webServiceResponseData = webServiceResponseData;
    }

    public FAISLicensePanelModel getFaisLicensePanelModel() {
	return faisLicensePanelModel;
    }

    public void setFaisLicensePanelModel(FAISLicensePanelModel faisLicensePanelModel) {
	this.faisLicensePanelModel = faisLicensePanelModel;
    }

}
