package za.co.liberty.web.wicket.markup.html.form;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.convert.IConverter;

import za.co.liberty.web.wicket.convert.converters.CurrencyConverter;

/**
 * A label field that represents a Currency
 * 
 * @author JZB0608 - 09 Apr 2008
 *
 */
public class CurrencyLabel extends SRSLabel {

	private static final long serialVersionUID = -3043839831397371478L;

	private CurrencyConverter currencyConverter;
	
	/**
	 * @param id
	 *            See Component
	 * @param type
	 *            Type for field validation
	 */
	public CurrencyLabel(final String id) {
		super(id);
		initialise();
	}

	/**
	 * @see org.apache.wicket.Component#Component(String, IModel)
	 */
	public CurrencyLabel(final String id, final IModel object) {
		super(id, object);
		initialise();
	}
	
	/**
	 * Initialises this object (called from constructor)
	 *
	 */
	protected void initialise() {
		/* Converter */
		currencyConverter = new CurrencyConverter();
		
	}
	
	@Override
	public IConverter getConverter(Class type) {
		return currencyConverter;
	}
	
}
