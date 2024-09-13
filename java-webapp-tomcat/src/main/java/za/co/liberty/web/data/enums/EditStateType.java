package za.co.liberty.web.data.enums;

/**
 * Defines the edit state of Page, Form etc.
 * 
 * @author jzb0608 - 24 Apr 2008
 *
 */
public enum EditStateType {

	ADD, MODIFY, VIEW, AUTHORISE, TERMINATE;

	/**
	 * Returns true if the edit state is considered non-editable
	 * 
	 * @return
	 */
	public boolean isViewOnly() {
		return this==VIEW || this==AUTHORISE;
	}
	
	/**
	 * Returns true if the edit state is considered add
	 * 
	 * @return
	 */
	public boolean isAdd() {
		return this==ADD;
	}
	
	/**
	 * Returns true if the edit state is considered MODIFY
	 * @return
	 */
	public boolean isModify() {
		return this==MODIFY;
	}
}
