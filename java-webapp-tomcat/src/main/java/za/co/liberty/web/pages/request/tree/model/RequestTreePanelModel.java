
package za.co.liberty.web.pages.request.tree.model;

import java.io.Serializable;

import org.apache.wicket.model.IModel;

public class RequestTreePanelModel implements  Serializable{
	private long requestNo;

	public long getRequestNo() {
		return requestNo;
	}

	public void setRequestNo(long requestNo) {
		this.requestNo = requestNo;
	}

}