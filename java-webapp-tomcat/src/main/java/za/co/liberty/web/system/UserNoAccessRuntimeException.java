package za.co.liberty.web.system;

import org.apache.wicket.WicketRuntimeException;

/**
 * When thrown this indicates that the logged in user is not configured correctly 
 * in the application i.e. not in the DB.
 * 
 * @author jzb0608
 *
 */
public class UserNoAccessRuntimeException extends WicketRuntimeException {

	private static final long serialVersionUID = 1L;
	

}
