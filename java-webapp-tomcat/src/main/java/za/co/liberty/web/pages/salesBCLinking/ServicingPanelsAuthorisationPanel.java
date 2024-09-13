package za.co.liberty.web.pages.salesBCLinking;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import za.co.liberty.dto.agreement.SalesBCLinking.SalesPanelDTO;
import za.co.liberty.dto.agreement.SalesBCLinking.ServicingPanelDTO;
import za.co.liberty.dto.agreement.maintainagreement.BaseAgreementRequestConfigurationDTO;
import za.co.liberty.dto.gui.context.AgreementSearchType;
import za.co.liberty.dto.gui.request.ViewRequestModelDTO;
import za.co.liberty.dto.rating.MIRatingTableNameDTO;
import za.co.liberty.interfaces.agreements.requests.RequestKindType;
import za.co.liberty.interfaces.rating.IGuiRatingRow;
import za.co.liberty.web.pages.admin.models.RatingTablePageModel;
import za.co.liberty.web.pages.admin.ratingtables.MIRatingFilterPanel;
import za.co.liberty.web.pages.admin.ratingtables.MIRatingGUIPage;
import za.co.liberty.web.pages.maintainagreement.ProvidentFundDetailsPanel;
import za.co.liberty.web.pages.maintainagreement.model.ProvidentFundDetailsPanelModel;
import za.co.liberty.web.pages.request.AbstractRequestEnquiryPanel;
import za.co.liberty.web.pages.request.BaseRequestViewAndAuthorisePanel;
import za.co.liberty.web.pages.salesBCLinking.model.SalesBCLinkingPageModel;
import za.co.liberty.web.pages.salesBCLinking.model.SalesBCLinkingPanelModel;

/**
 * Authorisation panel specifically catering for service panel request
 * authorisation only.
 * 
 * @author jzb0608
 * 
 */
@SuppressWarnings("unused")
public class ServicingPanelsAuthorisationPanel extends
		BaseRequestViewAndAuthorisePanel {

	private static final long serialVersionUID = 1L;

	public ServicingPanelsAuthorisationPanel(String id,
			ViewRequestModelDTO viewRequestPageModel) {
		super(id, viewRequestPageModel);
	}

	@Override
	public List<Panel> createPanels(String id, Object imageObject) {
		List<RequestKindType> requestKindList = getPageModel()
				.getRequestKindList();
		List<Panel> panelList = new ArrayList<Panel>();

		if (requestKindList
				.contains(RequestKindType.MaintainCrossRegionServicingRelationships) || (requestKindList
						.contains(RequestKindType.MaintainSingleRegionServicingRelationships))) {
			if (imageObject instanceof ServicingPanelDTO) {
				ServicingPanelDTO baseConfig = (ServicingPanelDTO) imageObject;
				SalesBCLinkingPageModel pageModel = new SalesBCLinkingPageModel();
				pageModel.setSelectedItem(baseConfig);
				pageModel.setSelectedServicingPanel(baseConfig);
				SalesBCLinkingPanel panel = new SalesBCLinkingPanel(id,
						getEditState(), pageModel, null, null);
				panelList.add(panel);
			}
		}

		return panelList;
	}

}
