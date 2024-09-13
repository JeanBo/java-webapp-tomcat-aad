/**
 * 
 */
package za.co.liberty.web.pages.transactions;

import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.model.IModel;

import za.co.liberty.web.pages.BaseWindowPage;

/**
 * @author zzt2108
 *
 */
public class TransactionRejectXMLMessagePage extends BaseWindowPage {
	
	private String pageName;
	
	
	public TransactionRejectXMLMessagePage(ModalWindow window, String displayText, String pageName) {
		this.pageName = pageName;
		add(createXMLMessageTextAreaField("xmlMessage", displayText));
	}
	
	private TextArea<?> createXMLMessageTextAreaField(String id, final String displayText) {
		TextArea<?> textArea = new TextArea<Object>(id, new IModel<Object>() {
			private static final long serialVersionUID = -1060562129103084694L;

			public Object getObject() {
				return (String)displayText;
			}

			public void setObject(Object arg0) {
			}

			public void detach() {
			}
		});
		
		textArea.setOutputMarkupId(true);
		
		return textArea;
	}

	/* (non-Javadoc)
	 * @see za.co.liberty.web.pages.BaseWindowPage#getPageName()
	 */
	@Override
	public String getPageName() {
		return pageName;
	}
	
}
