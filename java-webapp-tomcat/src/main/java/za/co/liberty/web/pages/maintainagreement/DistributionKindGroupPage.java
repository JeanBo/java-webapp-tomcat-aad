package za.co.liberty.web.pages.maintainagreement;

import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;

import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.pages.BaseWindowPage;
import za.co.liberty.web.pages.maintainagreement.model.DistributionKindGroupDetailsPanelModel;
import za.co.liberty.web.pages.maintainagreement.model.DistributionKindGroupPageModel;
/**
 * This class represents the page 
 * @author mzl2611
 */
public class DistributionKindGroupPage extends BaseWindowPage {

	private DistributionKindGroupPageModel pageModel;
	private ModalWindow window;
	private DistributionKindGroupDetailsPanel distributionKindGroupDetailsPanel;
	private EditStateType editState;
	private FeedbackPanel feedBackPanel;

	public DistributionKindGroupPage(ModalWindow window, DistributionKindGroupPageModel pageModel, EditStateType editState) {
		this.pageModel = pageModel;
		this.window = window;
		this.editState = editState;
		add(feedBackPanel =  new FeedbackPanel("searchMessages"));
		add(new DistributionKindGroupPageForm("distributionKindGroupPageForm"));
	}

	private DistributionKindGroupDetailsPanel createDistributionKindGroupDetailsPanel(String id) {
		DistributionKindGroupDetailsPanelModel distributionKindGroupDetailsPanelModel = new DistributionKindGroupDetailsPanelModel();
		distributionKindGroupDetailsPanelModel.setDistributionKindGroupDTOs(pageModel.getDistributionKindGroupDTOs());
		DistributionKindGroupDetailsPanel detailsPanel = new DistributionKindGroupDetailsPanel(id,this.editState, distributionKindGroupDetailsPanelModel, this.window, this);
		return detailsPanel;
	}
	
	public class DistributionKindGroupPageForm extends Form {

		private static final long serialVersionUID = 5028542911895183587L;

		public DistributionKindGroupPageForm(String id) {
			super(id);
			add(createDistributionKindGroupDetailsPanel("distributionKindGroupDetailsPanel"));
		
		}
	}

	@Override
	public String getPageName() {
		
		return DistributionKindGroupPage.class.getName();
	}
	
	

}
