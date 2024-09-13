package za.co.liberty.web.pages.admin;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.jar.Manifest;

import org.apache.log4j.Logger;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.convert.ConversionException;
import org.apache.wicket.util.convert.IConverter;
import org.apache.wicket.util.string.Strings;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidationError;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.ValidationError;
import org.apache.wicket.validation.validator.DateValidator;

import za.co.liberty.web.pages.admin.models.RoleUpdateSchedulerModel;

/**
 * Panel for Role Update Schedule. 
 * 
 * @author JZB0608 - 03 Jun 2008
 *
 */
public class RoleUpdateSchedulerPanel extends Panel {

	/* Constants */
	private static final long serialVersionUID = 5615959490908548662L;
	private static final long DAY_MILLISECONDS = 1000 * 60 * 60 * 24; 
	private static final SimpleDateFormat DATE_FORMAT = 
		new SimpleDateFormat("dd/MM/yyyy HH:mm");
	
	/* Form components */
	protected Form form;
	
	/* Attributes */
	protected RoleUpdateSchedulerModel bean;
	protected Label intervalLabel;
	protected TextField intervalField;
	
	/**
	 * Default constructor
	 * 
	 * @param id
	 * @param model
	 */
	public RoleUpdateSchedulerPanel(String id, RoleUpdateSchedulerModel model) {
		super(id); Manifest a;
		this.bean = model;
//		add(form = new RoleUpdateForm("mainForm"));
		add(createNameField("name"));
		add(createStartDateField("startDate"));
		add(intervalLabel = createIntervalLabel("intervalLabel"));
		add(intervalField = createIntervalField("interval"));
		add(createIsRepeatingField("isRepeating"));
	}

	/**
	 * The form for this panel
	 * 
	 * @author JZB0608 - 03 Jun 2008
	 *
	 */
	public class RoleUpdateForm extends Form {
		private static final long serialVersionUID = 1L;
		public RoleUpdateForm(String id) {
			super(id);
		}
	}
	
	/**
	 * Create the name field 
	 * 
	 * @param id
	 * @return
	 */
	private Component createNameField(String id) {
		return new TextField(id, new PropertyModel(bean, "name"))
			.setRequired(true).setLabel(new Model("Name"));
	}

	/**
	 * Get the Start Date field
	 * 
	 * @param id
	 * @return
	 */
	private Component createStartDateField(String id) {
		TextField text = new TextField(id, new PropertyModel(bean, "startDate")) {
			private static final long serialVersionUID = 1L;

			@Override
			public IConverter getConverter(Class type) {
				return (IConverter) new DateTimeConverter();
			}
			
		};
		text.setLabel(new Model("Start date"));
		text.setRequired(true);
		text.add(DateValidator.range(new Date(System.currentTimeMillis()-120000),
				new Date(System.currentTimeMillis()+(DAY_MILLISECONDS*2))));
		
		return text;
	}
	
	

	/**
	 * Create the isRepeating field
	 * 
	 * @return
	 */
	private CheckBox createIsRepeatingField(String id) {
		CheckBox box = new CheckBox(id, new PropertyModel(bean, "isRepeating"));
		box.add(new AjaxFormComponentUpdatingBehavior("click") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				if (target != null) {
					target.add(intervalField);
					target.add(intervalLabel);
				}
			}
		});
		box.setOutputMarkupId(true);
		return box;
	}
	
	/**
	 * Create the interval label 
	 * 
	 * @param id
	 * @return
	 */
	private Label createIntervalLabel(String id) {
		Label label = new Label(id, new Model("Interval (milliseconds)")) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onComponentTag(ComponentTag tag) {
				super.onComponentTag(tag);
				decorateComponentStyle(!bean.isRepeating(), tag);
			}
		};
		label.setOutputMarkupId(true);
		return label;
	}
	
	/**
	 * Get the interval input field
	 * 
	 * @return
	 */
	private TextField createIntervalField(String id) {
		TextField text = new TextField(id, new PropertyModel(bean,"interval")){
			private static final long serialVersionUID = 1L;

			@Override
			protected void onComponentTag(ComponentTag tag) {
				super.onComponentTag(tag);
				decorateComponentStyle(!bean.isRepeating(), tag);
			}
		};
		text.setLabel(new Model("Interval"));
		text.setOutputMarkupId(true);
		text.setRequired(true);
		
		/* Validate */
		IValidator validator = new IValidator() {
			private static final long serialVersionUID = 1L;

			public void validate(IValidatable val) {
				if (bean.isRepeating() && (Long)val.getValue() < 60000) {
					IValidationError err = new ValidationError()
						.addKey("NumberValidator.minimum")
						.setVariable("minimum", 60000);
					val.error(err);
				}
			}
		};
		text.add(validator);
		return text;
	}
	
	/**
	 * Decorate the style tag to hide the component
	 * 
	 * @param isHidden Hide component if true. 
	 * @param tag
	 */
	private void decorateComponentStyle(boolean isHidden, ComponentTag tag) {
		if (!isHidden) {
			return;
		}
		String val = (String) tag.getAttributes().get("style");
		val = (val ==null) ? "" : val;
		val += " ;visibility:hidden;";
		tag.put("style", val);
	}
	
	/**
	 * Temporary DateTime converter. 
	 * 
	 * TODO jzb0608 - Move to own class later
	 * 
	 * @author JZB0608 - 09 Jun 2008
	 *
	 */
	public class DateTimeConverter implements IConverter {

		private static final long serialVersionUID = -2222271935848647782L;

		/* Local variables */
		private transient Logger logger = Logger.getLogger(this.getClass());
		
//		@Override
//		protected Class getTargetType() {
//			return Date.class;
//
//		}

		
		/**
		 * Create a date from the specified string value
		 */
		public Object convertToObject(String value, Locale locale) {
			if (Strings.isEmpty(value)) {
				return null;
			}
			
			/* Attempt a match */
			try {
				Date date = DATE_FORMAT.parse(value);
				return date;
			} catch (ParseException e) {
			}
			
			throw new ConversionException("")
				.setResourceKey("validator.invalid")
				.setSourceValue(value)
				.setVariable("validator.type", "date");

		}

		@Override
		public String convertToString(Object value, Locale locale) {
			if (value == null) {
				return null;
			}
			return DATE_FORMAT.format(value);
		}

	}
}
