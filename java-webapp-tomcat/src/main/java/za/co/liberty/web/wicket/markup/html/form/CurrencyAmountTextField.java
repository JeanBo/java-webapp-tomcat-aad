package za.co.liberty.web.wicket.markup.html.form;

import org.apache.wicket.model.IModel;
import org.apache.wicket.util.convert.IConverter;

import za.co.liberty.common.domain.CurrencyAmount;
import za.co.liberty.web.wicket.convert.converters.CurrencyAmountConverter;

/**
 * A field that represents a Currency Amounts {@linkplain CurrencyAmount}
 * 
 * @author JZB0608 - 2 June 2017
 *
 */
public class CurrencyAmountTextField extends SRSTextField {

	private static final long serialVersionUID = -3043839831397371476L;

	private CurrencyAmountConverter currencyConverter;
	
	/**
	 * @param id
	 *            See Component
	 * @param type
	 *            Type for field validation
	 */
	public CurrencyAmountTextField(final String id) {
		super(id);
		initialise();
	}

	/**
	 * @see org.apache.wicket.Component#Component(String, IModel)
	 */
	public CurrencyAmountTextField(final String id, final IModel object) {
		super(id, object);
		initialise();
	}
	
	/**
	 * Initialises this object (called from constructor)
	 *
	 */
	protected void initialise() {
		/* Converter */
		currencyConverter = new CurrencyAmountConverter();
		setType(CurrencyAmount.class);
		
	}
	
	@Override
	public IConverter getConverter(Class type) {
		return currencyConverter;
	}
	
}
