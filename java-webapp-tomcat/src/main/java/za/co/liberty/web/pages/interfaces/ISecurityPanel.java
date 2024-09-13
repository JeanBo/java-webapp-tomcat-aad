package za.co.liberty.web.pages.interfaces;

/**
 * This interface must be implemented by all panels that will need to be secured.<br/>
 * Validation will be done on the gui side to make sure this interface is used for panel level security<br/>
 * Only one method needs to exist, this is the class that this panel's base is which will be used to configure the security too
 * 
 * @author DZS2610 
 *
 */
public interface ISecurityPanel {
	
	/**
	 * Must return the class of the panel that implements this interface<br/>
	 * getClass will not be used as the panel's methods could be overridden and a new class returned<br/>
	 * This method was created for less CPU time spent resolving if a class extends another class<br/>
	 * This method could be overriden by another class when a developer would like to freely create access to a Panel that extends another secured panel<br/>
	 * <strong>PLEASE USE <code>CLASSNAME.class</code> NOT <code>this.getClass()</code> as the classes access is retrieved from a map using the class name as the key</strong>
	 * 
	 * @return
	 */
	public Class getPanelClass();
}
