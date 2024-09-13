package za.co.liberty.web.wicket.model;

import org.apache.wicket.model.PropertyModel;

public class NullReplacementPropertyModel extends PropertyModel {
	
	private static final long serialVersionUID = 4086243392261230704L;
	
	private Object replacement;
	
	public NullReplacementPropertyModel(Object replacement,Object list,String field) {
		super(list,field);
		this.replacement=replacement;
	}
	
	@Override
	public Object getObject() {
		Object ret = super.getObject();
		if (ret==null) {
			ret = replacement;
		}
		return ret;
	}

	public Object getReplacement() {
		return replacement;
	}

	public void setReplacement(Object replacement) {
		this.replacement = replacement;
	}
	
	
	
	
}
