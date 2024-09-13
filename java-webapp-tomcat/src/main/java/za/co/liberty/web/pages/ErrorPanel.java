package za.co.liberty.web.pages;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.apache.log4j.Logger;
import org.apache.wicket.Page;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import za.co.liberty.web.models.ErrorModel;
import za.co.liberty.web.system.SRSApplication;
import za.co.liberty.web.wicket.markup.html.form.SRSLabel;
import za.co.liberty.web.wicket.markup.html.form.SRSTextArea;

/**
 * Defines the panel that shows error information.  Cause is only shown 
 * if the machine is in development mode.
 * 
 * @author JZB0608 - 02 Sep 2008
 *
 */
public class ErrorPanel extends Panel {

	private static final Logger logger = Logger.getLogger(ErrorPanel.class);
	private static final long serialVersionUID = 677922864972919320L;
	private boolean showTechnical = false;
	protected ErrorModel errorModel;
	
	/**
	 * Default constructor
	 * 
	 * @param id
	 * @param errorModel
	 */
	public ErrorPanel(String id, ErrorModel errorModel) {
		super(id);
		this.errorModel = errorModel;
		initialise();
	}
	
	/**
	 * Initialise the components required on this page.
	 *
	 */
	protected void initialise() {
		
		try {
			showTechnical = ((SRSApplication)SRSApplication.get()).isDevelopmentMode();
		} catch (Exception e) {
			// Ignore
		}
		
		add(new SRSLabel("errorType", new Model(
				(errorModel.isWindowPage()) 
					? errorModel.getErrorType().getWindowMessage()
							: errorModel.getErrorType().getMessage())));
		add(new SRSLabel("errorMessage", new PropertyModel(errorModel, "errorMessage")));
		add(new SRSLabel("incidentReference", new PropertyModel(errorModel, "incidentReference")));
		String pageName = null;
		if (errorModel.getOriginatingPage()!=null) {
			Page page = errorModel.getOriginatingPage();
			System.err.println("Page name = "+page.getClass().getName());
			try {
				pageName = (page instanceof BasePage) ? ((BasePage)page).getPageName() : page.getClass().getSimpleName();
			} catch (Exception e) {
				// Do nothing
			}
		}
		add(new SRSLabel("pageName", new Model(pageName)));
		add(new SRSLabel("technicalMessage", 
				new PropertyModel(errorModel, "technicalErrorMessage")));
		
		/* more section */
		final WebMarkupContainer moreDivContainer = new WebMarkupContainer("moreDiv");
		moreDivContainer.setVisible(showTechnical);
		add(moreDivContainer);
		
		// Cause
		String cause = "";
		if (errorModel.getCause()!= null) {
			/* Convert cause to String */
			ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
			PrintStream printStream = new PrintStream(arrayOutputStream);
			errorModel.getCause().printStackTrace(printStream);
			cause = arrayOutputStream.toString();
		}
		moreDivContainer.add(new SRSTextArea("causeText", new Model(cause)));
		
	}

}
