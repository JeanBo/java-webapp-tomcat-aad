package za.co.liberty.web.wicket.markup.html.form;

import org.apache.wicket.model.IModel;
import org.apache.wicket.util.convert.IConverter;

import za.co.liberty.web.wicket.convert.converters.CurrencyConverter;

/**
 * A field that represents a Decimal value
 * 
 * @author JZB0608 - 04 Apr 2008
 *
 */
public class DecimalField extends SRSTextField {

	private static final long serialVersionUID = -3241437367903217954L;
	
	private CurrencyConverter currencyConverter;
	
	/**
	 * @param id
	 *            See Component
	 * @param type
	 *            Type for field validation
	 */
	public DecimalField(final String id) {
		super(id);
		initialise();
	}

	/**
	 * @see org.apache.wicket.Component#Component(String, IModel)
	 */
	public DecimalField(final String id, final IModel object) {
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