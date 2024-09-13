package za.co.liberty.web.pages.releasefe;

import org.apache.wicket.markup.html.panel.Panel;

import za.co.liberty.dto.userprofiles.ContextDTO;
import za.co.liberty.web.data.enums.ContextType;
import za.co.liberty.web.pages.BasePage;

/**
 * The Release Future Earnings page will contain the div to hold the release future earnings panel.
 * Override context type for top panel in page
 * @author JWV2310
 *
 */
public class ReleaseFutureEarningPage extends BasePage {
	
	private static final long serialVersionUID = 1L;
	public String SELECTION_PANEL_NAME = "selectionPanel";
	private ReleaseFEModel pageModel;
	private Panel panelSelect = null;
	
	
	public ReleaseFutureEarningPage(){
		super();
		initializeModel();
	}

	@Override
	public ContextType getContextTypeRequired() {
		return ContextType.AGREEMENT;
	}

	private void initializeModel(){
		ReleaseFEModel rfeModel = new ReleaseFEModel();
		pageModel = rfeModel;
		final ContextDTO context = getPageContextDTO();
		if(context == null) {
			pageModel.setContext(new ContextDTO());
		}else{
			pageModel.setContext(context);
		}
		panelSelect = new ReleaseFutureEarningPanel(SELECTION_PANEL_NAME, getEditState(), pageModel,this);
		add(panelSelect);
	}
	
	@Override
	public String getPageName() {
		return "Release Future Earnings Page";
	}

}
