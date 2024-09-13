package za.co.liberty.web.pages.maintainagreement.fais;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Page;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.PropertyModel;

import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.pages.maintainagreement.model.ExperiencePanelModel;
/**
 * This class does have two textfields objects
 * one for yrs and another one for months. 
 * 
 * @author AAA1210
 *
 */
public class ExperiencePanel extends Panel {
	/**
	 * 
	 */

	private static final long serialVersionUID = 1L;

	private ExperiencePanelModel panelModel;

	private EditStateType editState;

	boolean isRequired;

	private String popUpText;

	private TextField<ExperiencePanelModel> yrsText;

	private Label popUpLbl;

	private TextField<ExperiencePanelModel> mnthsTxt;

	/**
	 * 
	 * @param id 
	 * @param editState
	 * @param panelModel
	 * @param parentPage
	 * @param popUpText
	 * @param isRequired
	 */
	public ExperiencePanel(String id, EditStateType editState,
			ExperiencePanelModel panelModel, Page parentPage, String popUpText,
			boolean isRequired) {
								super(id);
								//super(id, editState, parentPage);
								this.isRequired = isRequired;
								this.popUpText = popUpText;
								this.editState = editState;
								this.panelModel = panelModel;
						
								add(new ExperienceForm("experienceForm"));
	}

	public class ExperienceForm extends Form {
						private static final long serialVersionUID = 5808296649559984427L;
				
						public ExperienceForm(String id) {
							super(id);
							//add(popUpTextLbl());
							add(getYears());
							add(getMonths());
				
						}
	}
	
	

	private Label popUpTextLbl() {
		popUpLbl = new Label("popup", popUpText);

		return popUpLbl;

	}

	
	/**
	 * Returns a textfield Component for year/s
	 * @return
	 */private TextField getYears() {
		yrsText = new TextField("years", new PropertyModel(panelModel, "years"));

		yrsText.add(new AttributeModifier("maxlength", "2"));

		if (editState.isViewOnly()) {
			yrsText.setEnabled(false);
		} else {
			yrsText.setEnabled(true);
		}
		yrsText.setOutputMarkupId(true); 
		return yrsText;
	}

	/**
	 * Returns a textfield Component for month/s
	 * @return
	 */private TextField getMonths() {
		mnthsTxt = new TextField("months", new PropertyModel(
				getPropertyModelTarget(), "months"));
		mnthsTxt.add(new AttributeModifier("maxlength", "2"));
		
		if (editState.isViewOnly()) {
			mnthsTxt.setEnabled(false);
		} else {
			mnthsTxt.setEnabled(true);
		}
		mnthsTxt.setOutputMarkupId(true); 
		return mnthsTxt;
	}

	
	/**
	 * gets you the panelModel object
	 */
	public ExperiencePanelModel getPropertyModelTarget() {
		return panelModel;
	}
	/**
	 * 
	 * @return
	 */
	public TextField<ExperiencePanelModel> getMnthsTxt() {
		return mnthsTxt;
	}
	
	/**
	 * 
	 * @return
	 */
	public TextField<ExperiencePanelModel> getYrsText() {
		return yrsText;
	}
	
}
