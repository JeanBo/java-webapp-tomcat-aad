/**
 * 
 */
package za.co.liberty.web.pages;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.Model;

import za.co.liberty.web.models.ErrorModel;
import za.co.liberty.web.system.SRSAuthWebSession;

/**
 * Page to display a message when the system is in Batch Only Mode.
 * 
 * @author SZM0905 28 October 2009
 *
 */
public class BatchModePage extends WebPage {
	private static final long serialVersionUID = 2L;
	
	/**
	 * Default constructor 
	 * 
	 */
	public BatchModePage() {
		add(new Label("systemMode", new Model(
				(SRSAuthWebSession.get().isSystemInBatchOnlyMode()) ? " - BATCH Only Mode" : "")));
	}
	

	
}
