package za.co.liberty.web.pages.search;

import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.PropertyModel;

import za.co.liberty.web.pages.search.models.ContextSearchModel;


/**
 * Capture search values to search for a Party
 * 
 * @author JZB0608 - 23 May 2008
 *
 */
public class SearchPartyNameValuePanel extends Panel {

	/* Constants */
	private static final long serialVersionUID = 4008008744919434971L;
	
	/* Attributes */
	ContextSearchModel pageModel;

	/**
	 * Default constructor
	 * 
	 * @param id
	 * @param pageModel
	 */
	public SearchPartyNameValuePanel(String id, 
			ContextSearchModel pageModel) {
		super(id);
		this.pageModel = pageModel;
		add(createSurnameField("surname"));
		add(createInitialsField("initials"));
		add(createNameField("name"));
		add(createDateOfBirthField("dateOfBirth"));

	}

	/**
	 * Create the search surname field
	 * 
	 * @param id
	 * @return
	 */
	protected TextField createSurnameField(String id) {
		return new TextField(id, new PropertyModel(
				pageModel.getSearchValueObject(), "surname"));
	}
	
	/**
	 * Create the search initials field
	 * 
	 * @param id
	 * @return
	 */
	protected TextField createInitialsField(String id) {
		return new TextField(id, new PropertyModel(
				pageModel.getSearchValueObject(), "initials"));
	}
	
	/**
	 * Create the search first name field
	 * 
	 * @param id
	 * @return
	 */
	protected TextField createNameField(String id) {
		return new TextField(id, new PropertyModel(
				pageModel.getSearchValueObject(), "firstName"));
	}
	
	/**
	 * Create the search date of birth field
	 * 
	 * @param id
	 * @return
	 */
	protected TextField createDateOfBirthField(String id) {
		return new TextField(id, new PropertyModel(
				pageModel.getSearchValueObject(), "dateOfBirth"));
	}
}
