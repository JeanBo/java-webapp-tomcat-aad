/**
 * 
 */
package za.co.liberty.web.pages.request;

import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.IModel;

import za.co.liberty.web.pages.request.model.RequestEnquiryModel;

/**
 * @author zzt2108
 *
 */
public class RequestDPESearchPanel extends AbstractRequestEnquiryPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2418774824263234791L;
	protected static FeedbackPanel feedbackPanel;

	public RequestDPESearchPanel(String id, IModel<?> model) {
		super(id, model, feedbackPanel);
		pageModel = (RequestEnquiryModel) model.getObject();
		dataModel = pageModel.getDataModel(RequestPolicyTransactionPanel.class);
	}

}
