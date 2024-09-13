package za.co.liberty.web.data.enums;

/**
 * Applies the appropriate css style to a form column
 * 
 * @author jzb0608 - 25 Apr 2008
 *
 */
public enum ColumnStyleType {
	VERY_SMALL("verySmallCol"), SMALL("smallCol"), 
	DEFAULT("col"), LARGE("largeCol"), VERY_LARGE("veryLargeCol"),
	VERY_VERY_LARGE("veryVeryLargeCol");

	private String type;
	
	ColumnStyleType(String type) {
		this.type = type;
	}
	
	/**
	 * Return the css style type
	 * @return
	 */
	public String getStyleType() {
		return type;
	}
}
