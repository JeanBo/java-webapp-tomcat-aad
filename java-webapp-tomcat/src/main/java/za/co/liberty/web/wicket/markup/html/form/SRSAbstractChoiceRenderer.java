package za.co.liberty.web.wicket.markup.html.form;

import org.apache.wicket.markup.html.form.ChoiceRenderer;

/**
 * SRS specific choice renderer where the display and id values are abstract and must be provided
 * as part of the implementation.   This forces the implementation of thw display and id value method
 * where the actual method i.e. myObject.getId() or myObject.getDisplayValue() can be called, this assists
 * greatly with refactoring.
 * 
 * @author JZB0608
 *
 * @param <T>
 */
public abstract class SRSAbstractChoiceRenderer<T extends Object> extends ChoiceRenderer<T> {

	private static final long serialVersionUID = -1010577872137850983L;

	@Override
	public abstract Object getDisplayValue(T id);

	@Override
	public abstract String getIdValue(T object, int index); 
	
}
