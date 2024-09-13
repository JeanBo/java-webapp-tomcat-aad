package za.co.liberty.web.wicket.markup.html.form;

import org.apache.wicket.markup.ComponentTag;

import za.co.liberty.web.data.enums.ColumnStyleType;

/**
 * Helps with the decorating of css style information
 * on the html component tag
 * 
 * @author jzb0608 - 25 Apr 2008
 *
 */
public class SRSStyleHelper {

	private static SRSStyleHelper self;
	
	/**
	 * Enforce singleton with private constructor
	 *
	 */
	private SRSStyleHelper() {
		
	}
	
	/**
	 * Get instance
	 * 
	 * @return
	 */
	public static SRSStyleHelper getInstance() {
		if (self ==null) {
			self = new SRSStyleHelper();
		}
		return self;
	}
	
	/**
	 * Applies SRS specif css styles to component tag
	 *  
	 * @param tag
	 */
	public void decorateComponentTag(ComponentTag tag, ColumnStyleType columnStyle, boolean isView, boolean isNumber) {
		String clazz = (String) tag.getAttributes().get("class");
		clazz = (clazz==null)?"":clazz;
		
		if (isView) {
			clazz += " fieldview";
		}
		if (isNumber) {
			clazz += " number";
		}
		if (columnStyle!= null) {
			clazz += " " + columnStyle.getStyleType();
		}
		if (clazz.length()==0) {
			return;
		}
		tag.put("class", clazz);
	}
}
