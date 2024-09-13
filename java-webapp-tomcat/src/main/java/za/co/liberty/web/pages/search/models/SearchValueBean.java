package za.co.liberty.web.pages.search.models;

import java.io.Serializable;

/**
 * <p>Wrapper for search values.</p>
 * 
 * @author JZB0608 - 29 May 2008
 *
 */
public class SearchValueBean<T extends Object> implements Serializable{

	private static final long serialVersionUID = 1L;
	
	protected T value;

	
	public T getValue() {
		return value;
	}

	public void setValue(T value) {
		this.value = value;
	}
	
	
}
