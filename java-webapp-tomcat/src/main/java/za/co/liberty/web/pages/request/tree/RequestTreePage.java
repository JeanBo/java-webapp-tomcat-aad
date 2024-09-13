package za.co.liberty.web.pages.request.tree;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.panel.EmptyPanel;

import za.co.liberty.web.pages.request.tree.model.RequestTreeModel;
import za.co.liberty.web.pages.request.tree.model.RequestTreePanelModel;

/**
 * Request Tree Page which shows requests that are linked in a tree model.
 * 
 * @author JZB0608
 *
 */
public class RequestTreePage extends WebPage{
	private String requestNo;
	private String id;
	public RequestTreePage (String id, String requestNo) {
		this.id = id;
		this.requestNo = requestNo;
		
		add(new EmptyPanel("requestTreePanel"));
//		add(new RequestTreePanel("requestTreePanel", new RequestTreePanelModel()));
		
	}
}
