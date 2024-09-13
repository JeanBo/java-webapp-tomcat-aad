package za.co.liberty.web.system;

import org.apache.wicket.WicketRuntimeException;

/**
 * When thrown this indicates that there are multiple active sessions for the same
 * user.
 * 
 * @author jzb0608
 *
 */
public class ConcurrentSessionForUserRuntimeException extends WicketRuntimeException {

	private static final long serialVersionUID = 1L;
	
	private boolean isWarnOnly = false;
	
	public ConcurrentSessionForUserRuntimeException() {
		this (false);
	}
	
	public ConcurrentSessionForUserRuntimeException(boolean warnOnly) {
		this.isWarnOnly = warnOnly;
	}

	public boolean isWarnOnly() {
		return isWarnOnly;
	}

}
