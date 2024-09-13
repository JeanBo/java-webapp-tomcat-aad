package za.co.liberty.web.system;

import java.util.Date;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.apache.log4j.Logger;

/**
 * Session Listener
 * 
 * @author jzb0608
 *
 */
public class SRSHttpSessionListener implements HttpSessionListener  {
	
	static Logger logger = Logger.getLogger(SRSHttpSessionListener.class);
	
	@Override
	public void sessionCreated(HttpSessionEvent event) {
//		event.getSession().setMaxInactiveInterval(60*5);
		if (logger.isDebugEnabled())
			logger.debug("CREATE a session (AppWeb) "
				+ "\n sessionId=" + event.getSession().getId()
				+ "\n creationTime="+ event.getSession().getCreationTime()
				+ "\n lastAccessTime="+ event.getSession().getLastAccessedTime() 
					+ "  - " + new Date(event.getSession().getLastAccessedTime())
				+ "\n maxInterval="+ event.getSession().getMaxInactiveInterval());
		
	}

	@Override
	public void sessionDestroyed(HttpSessionEvent event) {
		if (logger.isDebugEnabled())
			logger.debug("DESTROY a session (AppWeb) "
				+ "\n sessionId=" + event.getSession().getId()
				+ "\n creationTime="+ event.getSession().getCreationTime()
				+ "\n lastAccessTime="+ event.getSession().getLastAccessedTime()
				+ "\n maxInterval="+ event.getSession().getMaxInactiveInterval());
		
	}
}
