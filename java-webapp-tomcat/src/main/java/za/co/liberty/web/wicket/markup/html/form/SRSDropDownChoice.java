package za.co.liberty.web.wicket.markup.html.form;

import java.util.List;

import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.string.AppendingStringBuffer;
import org.apache.wicket.util.string.Strings;

public class SRSDropDownChoice<T> extends DropDownChoice<T>{
	
	private static final long serialVersionUID = 1L;
	private String defaultChoice = "ALL";
		
	/**
	 * @see org.apache.wicket.markup.html.form.AbstractChoice#AbstractChoice(String)
	 */
	public SRSDropDownChoice(final String id)
	{
		super(id);
	}

	/**
	 * @see org.apache.wicket.markup.html.form.AbstractChoice#AbstractChoice(String, List)
	 */
	public SRSDropDownChoice(final String id, final List<T> choices)
	{
		super(id, choices);
	}

	/**
	 * @see org.apache.wicket.markup.html.form.AbstractChoice#AbstractChoice(String,
	 *      List,IChoiceRenderer)
	 */
	public SRSDropDownChoice(final String id, final List<T> data, final IChoiceRenderer<T> renderer)
	{
		super(id, data, renderer);
	}

	/**
	 * @see org.apache.wicket.markup.html.form.AbstractChoice#AbstractChoice(String, IModel, List)
	 */
	public SRSDropDownChoice(final String id, IModel<T> model, final List<T> choices)
	{
		super(id, model, choices);
	}

	/**
	 * @see org.apache.wicket.markup.html.form.AbstractChoice#AbstractChoice(String, IModel, List,
	 *      IChoiceRenderer)
	 */
	public SRSDropDownChoice(final String id, IModel<T> model, final List<T> data,
			final IChoiceRenderer<T> renderer,String defaultChoice)
	{
		super(id, model, data, renderer);
		this.defaultChoice = defaultChoice;
	}

	/**
	 * @see org.apache.wicket.markup.html.form.AbstractChoice#AbstractChoice(String, IModel)
	 */
	public SRSDropDownChoice(String id, IModel<? extends List<? extends T>> choices)
	{
		super(id, choices);
	}

	/**
	 * @see org.apache.wicket.markup.html.form.AbstractChoice#AbstractChoice(String, IModel,IModel)
	 */
	public SRSDropDownChoice(String id, IModel<T> model, IModel<? extends List<? extends T>> choices)
	{
		super(id, model, choices);
	}

	/**
	 * @see org.apache.wicket.markup.html.form.AbstractChoice#AbstractChoice(String,
	 *      IModel,IChoiceRenderer)
	 */
	public SRSDropDownChoice(String id, IModel<? extends List<? extends T>> choices, IChoiceRenderer<T> renderer)
	{
		super(id, choices, renderer);
	}


	/**
	 * @see org.apache.wicket.markup.html.form.AbstractChoice#AbstractChoice(String, IModel,
	 *      IModel,IChoiceRenderer)
	 */
	public SRSDropDownChoice(String id, IModel<T> model, IModel<? extends List<? extends T>> choices, IChoiceRenderer<T> renderer)
	{
		super(id, model, choices, renderer);
	}
	
	
	
//	@Override
//	protected CharSequence getDefaultChoice(String arg0) {
//		// TODO Auto-generated method stub
//		return super.getDefaultChoice(arg0);
//	}
//
//	protected CharSequence getDefaultChoice22(Object selected) {
//		if(defaultChoice == null){
//			this.setNullValid(false);
//			return null;
//		}		
//		
//		this.setNullValid(true);		
//		// Null is valid, so look up the value for it
//		String option = getLocalizer().getString(getId() + ".nullValid", this, defaultChoice);
//		if (Strings.isEmpty(option))
//		{
//			option = getLocalizer().getString("nullValid", this, defaultChoice);
//		}
//
//		// The <option> tag buffer
//		final AppendingStringBuffer buffer = new AppendingStringBuffer(32 + option.length());
//
//
//		// Add option tag
//		buffer.append("\n<option");
//
//		// If null is selected, indicate that
//		if (selected == null)
//		{
//			buffer.append(" selected=\"selected\"");
//		}
//
//		// Add body of option tag
//		buffer.append(" value=\"\">").append(option).append("</option>");
//		return buffer;		
//	}


	/**
	 * Returns the display value for the null value. The default behavior is to look the value up by
	 * using the key from <code>getNullValidKey()</code>.
	 *
	 * @return The value to display for null
	 */
	protected String getNullValidDisplayValue() {
//		if (defaultChoice != null) {
			return defaultChoice;
//		}
		
//		String option = getLocalizer().getStringIgnoreSettings(getNullValidKey(), this, null, null);
//		System.out.println("Nullvalue 1 = " + option);
//		System.out.println("Nullvalue 2 = " + getLocalizer().getString("nullValid", this, ""));
//		if (Strings.isEmpty(option)) {
//			option = getLocalizer().getString("nullValid", this, "");
//			
//		}
//		System.out.println("Nullvalue 3 return= " + option);
//		return option;
	}
}
