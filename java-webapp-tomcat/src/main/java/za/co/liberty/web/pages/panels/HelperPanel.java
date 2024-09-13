package za.co.liberty.web.pages.panels;

import java.util.Date;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.extensions.yui.calendar.DatePicker;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebComponent;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.ImageButton;
import org.apache.wicket.markup.html.form.ListMultipleChoice;
import org.apache.wicket.markup.html.form.RadioChoice;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.image.ContextImage;
import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import za.co.liberty.web.wicket.markup.html.form.SRSDateField;

/**
 * <p>Allows for the addition of components where the
 * implementation can change i.e. both Label's and TextField's 
 * can be shown with the same html.</p>
 * 
 * <p><b>Note on usage:</b> Passed component name/id should be "value"</p>
 *  
 * @author JZB0608 - 09 Apr 2008
 * @Author Dean Scott (DZS2610) Added Grid,GridCheckbox,ImageButton, TextArea, Link
 *
 */
public abstract class HelperPanel extends Panel {

	private static final long serialVersionUID = -3376598745075733286L;

	protected HelperPanel(String id) {
		super(id);
	}

	/**
	 * Retrieve the object that is wrapped/enclosed by the Helper Panel
	 * 
	 * @return
	 */
	public abstract Component getEnclosedObject();
	
	/**
	 * Create a Label helper
	 * 
	 * @param id
	 * @param label
	 * @param isFieldView  Adds fieldView to class attribute when true
	 * @return
	 */
	public static HelperPanel getInstance(String id, Label label, boolean isFieldView) {
		return new HelperLabel(id,label, isFieldView);
	}

	/**
	 * Create a HelperPanel instance for one of the defined component
	 * types
	 * 
	 * @param id
	 * @param component
	 * @return
	 */
	public static HelperPanel getInstance(String id, Component component) {
		if (component instanceof Label) {
			return getInstance(id, (Label)component);
		} else if (component instanceof MultiLineLabel) {
			return getInstance(id, (MultiLineLabel)component);
		} else if (component instanceof TextField) {
			return getInstance(id, (TextField)component);
		} else if (component instanceof DropDownChoice) {
			return getInstance(id, (DropDownChoice)component);
		} else if (component instanceof CheckBox) {
			return getInstance(id, (CheckBox)component);
		} else if (component instanceof AbstractLink) {
			return getInstance(id, (AbstractLink)component);
//		} else if (component instanceof DataGrid) {
//			return getInstance(id, (DataGrid)component);
		} else if (component instanceof ImageButton) {
			return getInstance(id, (ImageButton)component);
		} else if (component instanceof TextArea) {
			return getInstance(id, (TextArea)component);
		} else if (component instanceof Button) {
			return getInstance(id, (Button)component);
		}else if (component instanceof RadioChoice) {
			return getInstance(id, (RadioChoice)component);
		}else if (component instanceof ListMultipleChoice) {
			return getInstance(id, (ListMultipleChoice)component);
		}else if (component instanceof Panel)
			return getInstance(id, (Panel)component);
		throw new IllegalArgumentException("This type of component("+((component != null) ? component.getClass() : null)+") is not currently supported");
	}
	/**
	 * Create a Label helper where isFieldView is assumed false
	 * 
	 * @param id
	 * @param label
	 * @return
	 */
	public static HelperPanel getInstance(String id, Label label) {
		return new HelperLabel(id,label, false);
	}
	
	/**
	 * Create a  Multi line Label helper where isFieldView is assumed false
	 * 
	 * @param id
	 * @param label
	 * @return
	 */
	public static HelperPanel getInstance(String id, MultiLineLabel label) {
		return new HelperLabel(id,label, false);
	}
	
	/**
	 * Create a TextField helper
	 * 
	 * @param id
	 * @param text
	 * @return
	 */
	public static HelperPanel getInstance(String id, TextField text) {
		return new HelperTextField(id,text);
	}
	
	/**
	 * Create a DropDownChoice helper
	 * 
	 * @param id
	 * @param choice
	 * @return
	 */
	public static HelperPanel getInstance(String id, DropDownChoice choice) {
		return new HelperDropDownChoice(id,choice);
	}
	
	/**
	 * Create a DropDownChoice helper
	 * 
	 * @param id
	 * @param choice
	 * @return
	 * SSM2707 Agency Pool
	 */
	public static HelperPanel getInstance(String id, DropDownChoice choice,ContextImage image) {
		return new HelperDropDownChoiceWithImage(id,choice,image);
	}
	
	/**
	 * Create a CheckBox helper
	 * 
	 * @param id
	 * @param choice
	 * @return
	 */
	public static HelperPanel getInstance(String id, CheckBox box) {
		return new HelperCheckBox(id,box);
	}
	
	/**
	 * Create a Grid CheckBox helper
	 * 
	 * @param id
	 * @param choice
	 * @return
	 */
	public static HelperPanel getInstance(String id, CheckBox box, boolean centered) {
		return new HelperGridCheckBox(id,box,centered);
	}
	
	/**
	 * Create a Link helper
	 * 
	 * @param id
	 * @param choice
	 * @return
	 */
	public static HelperPanel getInstance(String id, AbstractLink link) {
		return new HelperLink(id,link);
	}
	
	/**
	 * Create a Link with Image helper
	 * 
	 * @param id
	 * @param choice
	 * @return
	 */
	public static HelperPanel getInstance(String id, AbstractLink link,ContextImage image) {
		return new HelperLinkWithImage(id,link,image);
	}
	
	/**
	 * Create a Label helper with an image next to it.
	 * 
	 * @param id
	 * @param label
	 * @param image
	 * @return HelperPanel object
	 * 
	 * SSM2707
	 */
	public static HelperPanel getInstance(String id, Label label,ContextImage image) {
		return new HelperLabelWithImage(id,label, image);
	}
	
	
	/**
	 * Create a CheckBox helper
	 * 
	 * @param id
	 * @param choice
	 * @return
	 */
	public static HelperPanel getInstance(String id, CheckBox box,ContextImage image) {
		return new HelperCheckBoxWithImage(id,box,image);
	}
	
//	/**
//	 * Create a grid helper
//	 * 
//	 * @param id
//	 * @param choice
//	 * @return
//	 */
//	public static HelperPanel getInstance(String id, DataGrid grid) {
//		return new HelperGrid(id,grid);
//	}
	
	/**
	 * Create a image button helper
	 * 
	 * @param id
	 * @param choice
	 * @return
	 */
	public static HelperPanel getInstance(String id, ImageButton button) {
		return new HelperImageButton(id,button);
	}
	
	/**
	 * Create a text area
	 * 
	 * @param id
	 * @param choice
	 * @return
	 */
	public static HelperPanel getInstance(String id, TextArea textArea) {
		return new HelperTextArea(id,textArea);
	}
	
	/**
	 * Create a date picker textfield if includeDatePicker is true else just a regular textfield
	 * 
	 * @param id
	 * @param choice
	 * @return
	 * 
	 * TODO WICKETTEST WICKETFIX
	 */
	public static HelperPanel getInstance(String id, TextField field, boolean includeDatePicker) {
		if(includeDatePicker){
			return new HelperDatePickerTextField(id,field);
		}
		else{
			return new HelperTextField(id,field);
		}
	}
	
//	/**
//	 * Create a grid helper with datapicker
//	 * 
//	 * @param id
//	 * @param choice
//	 * @return
//	 */
//	public static HelperPanel getInstance(String id, DataGrid grid,boolean includeDatePicker) {
//		if(includeDatePicker){
//			return new HelperGridWithDatePickerPanel(id,grid);
//		}
//		else{
//			return new HelperGrid(id,grid);
//		}
//	}
//	
//	/**
//	 * Get a label that has the dojo tooltip attached
//	 *  
//	 * @param id
//	 * @param choice
//	 * @return
//	 */
//	public static HelperPanel getInstance(String id, Label field, DojoTooltip toolTip) {
//		return new HelperLabelWithDojoTooltip(id,field,toolTip);
//	}
	
	/**
	 * Create a text area
	 * 
	 * @param id
	 * @param choice
	 * @return
	 */
	public static HelperPanel getInstance(String id, Button button) {
		return new HelperButton(id,button);
	}
	
	/**
	 * Create a radio choice
	 * 
	 * @param id
	 * @param choice
	 * @return
	 */
	public static HelperPanel getInstance(String id, RadioChoice radio) {
		return new HelperRadioChoice(id,radio);
	}
	

	/**
	 * Create a Multi List Select Box
	 * 
	 * @param id
	 * @param choice
	 * @return
	 */
	
	public static HelperPanel getInstance(String id, ListMultipleChoice listMultipleChoice) {
		return new HelperListMultipleChoice(id,listMultipleChoice);
	}
	
	/**
	 * Create a Multi List Select Box
	 * 
	 * @param id
	 * @param choice
	 * @return
	 */
	
	public static HelperPanel getInstance(String id, Panel panel) {
		return new HelperPanelPanel(id,panel);
	}
	
	/**
	 * Create a DropDownChoice helper
	 * 
	 * @param id
	 * @param choice
	 * @return
	 */
	public static HelperPanel getInstance(String id, Label field, DropDownChoice choice) {
		return new HelperLabelWithDropDownChoice(id,field,choice);
	}
}

/**
 * Allows for the placement of Labels
 * 
 * @author JZB0608 - 09 Apr 2008
 *
 */
class HelperLabel extends HelperPanel {

	private static final long serialVersionUID = 5322802739221944044L;
	private WebComponent label;
	private boolean isFieldView;
	
	public HelperLabel(String id, WebComponent label, boolean isFieldView) {
		super(id);
		this.isFieldView = isFieldView;
		add(label);
		this.label = label;
	}

	@Override
	protected void onComponentTag(ComponentTag tag) {
		// TODO Auto-generated method stub
		super.onComponentTag(tag);
		if (isFieldView) {
			tag.put("class", 
				(String)tag.getAttributes().get("class")
				+" fieldView");
		}
	}

	@Override
	public WebComponent getEnclosedObject() {
		return label;
	}
}

///**
// * Allows for the placement of a dojo tooltip label
// * 
// * @author DZS2610
// *
// */
//class HelperLabelWithDojoTooltip extends HelperPanel {
//
//	private static final long serialVersionUID = 1L;
//	private Label label;	
//	
//	public HelperLabelWithDojoTooltip(String id, Label label, DojoTooltip toolTip) {
//		super(id);		
//		add(label);
//		add(toolTip);
//		this.label = label;
//	}
//	
//	@Override
//	public Label getEnclosedObject() {
//		return label;
//	}
//}

/**
 * Allows for the placement of TextFields
 * 
 * @author JZB0608 - 09 Apr 2008
 *
 */
class HelperTextField extends HelperPanel {

	private static final long serialVersionUID = 5322802739221944012L;

	private TextField textField;
	
	public HelperTextField(String id, TextField text) {
		super(id);
		add(text);
		this.textField = text;
	}
	
	@Override
	public TextField getEnclosedObject() {
		return textField;
	}
}

/**
 * Allows for the placement of DropDownChoice
 * 
 * @author JZB0608 - 09 Apr 2008
 *
 */
class HelperDropDownChoice extends HelperPanel  {
	
	private static final long serialVersionUID = 5322802739221944012L;

	private DropDownChoice choiceField;
	
	public HelperDropDownChoice(String id, DropDownChoice choiceField) {
		super(id);
		add(choiceField);
		this.choiceField = choiceField;
	}
	
	@Override
	public DropDownChoice getEnclosedObject() {
		return choiceField;
	}
}

/**
 * Allows for the placement of CheckBox
 * 
 * @author JZB0608 - 30 Apr 2008
 *
 */
class HelperCheckBox extends HelperPanel  {
	
	private static final long serialVersionUID = 5322802739221944012L;

	private CheckBox checkField;
	
	public HelperCheckBox(String id, CheckBox checkField) {
		super(id);				
		add(checkField);		
		this.checkField = checkField;
	}
	
	@Override
	public CheckBox getEnclosedObject() {
		return checkField;
	}
}

/**
 * Allows for the placement of CheckBox in the DataGrid
 * 
 * @author JZB0608 - 30 Apr 2008
 *
 */
class HelperGridCheckBox extends HelperPanel  {
	
	private static final long serialVersionUID = 5322802739221944012L;

	private CheckBox checkField;
	
	public HelperGridCheckBox(String id, CheckBox checkField, boolean centeredinGrid) {
		super(id);		
		WebMarkupContainer comp = new WebMarkupContainer("center");		
		add(comp);
		comp.add(checkField);		
		if(centeredinGrid){			
			comp.add(new AttributeModifier("align","center"));
		}
		this.checkField = checkField;
	}
	
	@Override
	public CheckBox getEnclosedObject() {
		return checkField;
	}
}

/**
 * Allows for the placement of Link
 * 
 * @author DZS2610 - 21 July 2008
 *
 */
class HelperLink extends HelperPanel  {
	
	private static final long serialVersionUID = 5322802739221944012L;

	private AbstractLink link;
	
	public HelperLink(String id, AbstractLink link) {
		super(id);
		add(link);
		this.link = link;
	}
	
	@Override
	public AbstractLink getEnclosedObject() {
		return link;
	}
}

///**
// * Allows for the placement of Grid
// * 
// * @author DZS2610 - 21 July 2008
// *
// */
//class HelperGrid extends HelperPanel  {
//	
//	private static final long serialVersionUID = 5322802739221944012L;
//
//	private DataGrid grid;
//	
//	public HelperGrid(String id, DataGrid grid) {
//		super(id);
//		add(grid);
//		this.grid = grid;
//	}
//	
//	@Override
//	public DataGrid getEnclosedObject() {
//		return grid;
//	}
//}

/**
 * Allows for the placement of a panel
 * 
 * @author jzb0608 
 *
 */
class HelperPanelPanel extends HelperPanel  {
	
	private static final long serialVersionUID = 5322802739221931012L;

	private Panel panel;
	
	public HelperPanelPanel(String id, Panel panel) {
		super(id);
		add(panel);
		this.panel = panel;
	}
	
	@Override
	public Panel getEnclosedObject() {
		return panel;
	}
}

/**
 * Allows for the placement of an image button
 * 
 * @author DZS2610 - 21 July 2008
 *
 */
class HelperImageButton extends HelperPanel  {
	
	private static final long serialVersionUID = 5322802739221944012L;

	private ImageButton button;
	
	public HelperImageButton(String id, ImageButton button) {
		super(id);
		add(button);
		this.button = button;
	}
	
	@Override
	public ImageButton getEnclosedObject() {
		return button;
	}
}

/**
 * Allows for the placement of a textfield with a datepicker attached
 * 
 * @author DZS2610 - 21 July 2008
 *
 */
class HelperDatePickerTextField extends HelperPanel  {
	
	private static final long serialVersionUID = 5322802739221944012L;

	private TextField textField;
	private Date date;
	
	public HelperDatePickerTextField(String id, TextField textField) {
		super(id);
		add(textField);
//		DatePicker pk = new PopupDatePicker("picker",textField);
//		if(textField != null){
//			if(!textField.isEnabled()){
//				pk.setEnabled(false);
//			}
//			if(!textField.isVisible()){
//				pk.setVisible(false);
//			}			
//		}
//		add(pk);
//#WICKETFIX::Agreement tab -> Fit and proper waivers column (end date)	
		SRSDateField text = new SRSDateField(id, new IModel<Date>() {
			
			@Override
			public void detach() {
							
			}
			
			@Override
			public void setObject(Date arg0) {
				date = ((Date) arg0);
				
			}
			
			@Override
			public Date getObject() {
				return date;
			}
		});		
//		DatePicker pk =text.newDatePicker();
//		textField.add(pk);
		this.textField = textField;
	}
	
	@Override
	public TextField getEnclosedObject() {
		return textField;
	}
}

///**
// * Allows for the placement of a DATAGRID with a datepicker attached
// * 
// * @author PKS2802
// *
// */
//class HelperGridWithDatePickerPanel extends HelperPanel  {
//	
//	private static final long serialVersionUID = 5322802739221944012L;
//
//	private DataGrid grid;
//	
//	public HelperGridWithDatePickerPanel(String id, DataGrid grid) {
//		super(id);
//		add(grid);
//		DatePicker pk = new PopupDatePicker("picker",grid);
//		if(grid != null){
//			if(!grid.isEnabled()){
//				pk.setEnabled(false);
//			}
//			if(!grid.isVisible()){
//				pk.setVisible(false);
//			}			
//		}
//		add(pk);
//		this.grid = grid;
//	}
//	
//	@Override
//	public DataGrid getEnclosedObject() {
//		return grid;
//	}
//}

/**
 * Allows for the placement of an text area
 * 
 * @author DZS2610 - 21 July 2008
 *
 */
class HelperTextArea extends HelperPanel  {
	
	private static final long serialVersionUID = 1L;
	
	private TextArea textArea;
	
	public HelperTextArea(String id, TextArea textArea) {
		super(id);
		add(textArea);
		this.textArea = textArea;
	}
	
	@Override
	public TextArea getEnclosedObject() {
		return textArea;
	}
}

/**
 * Allows for the placement of a button
 * 
 * @author DZS2610 - 21 July 2008
 *
 */
class HelperButton extends HelperPanel  {
	
	private static final long serialVersionUID = 1L;
	
	private Button button;
	
	public HelperButton(String id, Button button) {
		super(id);
		add(button);
		this.button = button;
	}
	
	@Override
	public Button getEnclosedObject() {
		return button;
	}
}

/**
 * Allows for the placement of a radio box
 * @author DZS2610
 *
 */
class HelperRadioChoice extends HelperPanel  {
	
	private static final long serialVersionUID = 1L;
	
	private RadioChoice radio;
	
	public HelperRadioChoice(String id, RadioChoice radio) {
		super(id);
		add(radio);
		this.radio = radio;
	}
	
	@Override
	public RadioChoice getEnclosedObject() {
		return radio;
	}
}
	
	/**
	 * Allows for the placement of a list multiple select box
	 * @author PKS2802
	 *
	 */
	class HelperListMultipleChoice extends HelperPanel  {
		
		private static final long serialVersionUID = 1L;
		
		private ListMultipleChoice listMultipleChoice;
		
		public HelperListMultipleChoice(String id, ListMultipleChoice listMultipleChoice) {
			super(id);
			add(listMultipleChoice);
			this.listMultipleChoice = listMultipleChoice;
		}
		
		@Override
		public ListMultipleChoice getEnclosedObject() {
			return listMultipleChoice;
		}
}
	
	/**
	 * Allows for the placement of Link With Image
	 * 
	 * @author PKS2802 - 11/01/2014
	 *
	 */
	class HelperLinkWithImage extends HelperPanel  {
		
		private static final long serialVersionUID = 5322802739221944012L;

		private AbstractLink link;		
		
		public HelperLinkWithImage(String id, AbstractLink link,ContextImage image) {
			super(id);
			link.add(image);
			add(link);
			this.link = link;
		}
		
		@Override
		public AbstractLink getEnclosedObject() {
			return link;
		}
	}
	
	
	/**
	 * Allows for the placement of an Image along with a label
	 * 
	 * @author SSM2707
	 *
	 */
	class HelperLabelWithImage extends HelperPanel {

		private static final long serialVersionUID = 1L;
		private Label label;
		private ContextImage img;
		
		public HelperLabelWithImage(String id, Label label,ContextImage image) {
			super(id);		
			add(label);
			add(image);
			this.label = label;
			this.img = image;
		}
		
		@Override
		public Label getEnclosedObject() {
			return label;
		}
	}
	
	/**
	 * Allows for the placement of CheckBox with an Image
	 * 
	 * @author SSM2707 - 30 Apr 2008
	 *
	 */
	class HelperCheckBoxWithImage extends HelperPanel  {
		
		private static final long serialVersionUID = 1L;

		private CheckBox checkField;
		private ContextImage image;
		
		public HelperCheckBoxWithImage(String id, CheckBox checkField, ContextImage image) {
			super(id);				
			add(checkField);
			add(image);
			this.checkField = checkField;
			this.image = image;
		}
		
		@Override
		public CheckBox getEnclosedObject() {
			return checkField;
		}
	}
	
	/**
	 * Allows for the placement of DropDownChoice
	 * 
	 * @author SSM2707 - 10/11/2017
	 *
	 */
	class HelperDropDownChoiceWithImage extends HelperPanel  {
		
		private static final long serialVersionUID = 5322802739221944012L;

		private DropDownChoice choiceField;
		private ContextImage image;
		
		public HelperDropDownChoiceWithImage(String id, DropDownChoice choiceField,ContextImage image) {
			super(id);
			add(choiceField);
			add(image);
			this.choiceField = choiceField;
			this.image = image;
		}
		
		@Override
		public DropDownChoice getEnclosedObject() {
			return choiceField;
		}
	}
	
	/**
	 * Allows for the placement of DropDownChoice
	 * 
	 * @author JZB0608 - 09 Apr 2008
	 *
	 */
	class HelperLabelWithDropDownChoice extends HelperPanel  {
		
		private static final long serialVersionUID = 5322802739221944012L;

		private DropDownChoice choiceField;
		private Label field;
		public HelperLabelWithDropDownChoice(String id, Label field, DropDownChoice choiceField) {
			super(id);
			add(field);
			add(choiceField);
			this.choiceField = choiceField;
			this.field = field;
		}
		
		@Override
		public DropDownChoice getEnclosedObject() {
			return choiceField;
		}
	}
	
	


