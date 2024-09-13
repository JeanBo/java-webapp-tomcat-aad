package za.co.liberty.web.wicket.markup.html.form;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.convert.IConverter;

public class GenericConversionLabel extends Label {
	
	IConverter converter;
	
	public GenericConversionLabel(String id,IModel model,IConverter converter) {
		super(id,model);
		this.converter=converter;
	}
	
	@Override
	public IConverter getConverter(Class object) {
		if (converter!=null) {
			return converter;
		} else {
			return super.getConverter(object);
		}
	}

}
