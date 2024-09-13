package za.co.liberty.web.helpers.javascript;

/**
 * A generic javascript helper class used to generate the javascript 
 * required for some Wicket Ajax calls.  An example use would be to 
 * return javascript with an Ajax call to cancel the action after 
 * clicking on a link.
 * 
 * @author Jean Bodemer (JZB0608) - 30 Sep 2008
 *
 */
public class ScriptBuilder {

	/**
	 * Return the script required to return the passed value
	 * 
	 * @param value
	 * @return
	 */
	public static String buildReturnValue(boolean value) {
		return "wcall="+value+";";
	}
	
}
