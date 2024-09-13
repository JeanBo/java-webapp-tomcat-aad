package za.co.liberty.web.system;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;
import org.apache.wicket.Page;
import org.apache.wicket.authorization.UnauthorizedInstantiationException;
import org.apache.wicket.core.request.handler.IPageRequestHandler;
import org.apache.wicket.core.request.handler.PageProvider;
import org.apache.wicket.core.request.handler.RenderPageRequestHandler;
import org.apache.wicket.core.request.handler.RenderPageRequestHandler.RedirectPolicy;
import org.apache.wicket.core.request.mapper.StalePageException;
import org.apache.wicket.protocol.http.PageExpiredException;
import org.apache.wicket.request.IRequestHandler;
import org.apache.wicket.request.component.IRequestablePage;
import org.apache.wicket.request.cycle.AbstractRequestCycleListener;
import org.apache.wicket.request.cycle.PageRequestHandlerTracker;
import org.apache.wicket.request.cycle.RequestCycle;

import za.co.liberty.exceptions.ExceptionSeverity;
import za.co.liberty.exceptions.SRSRuntimeException;
import za.co.liberty.web.models.ErrorModel;
import za.co.liberty.web.pages.BaseWindowPage;
import za.co.liberty.web.pages.ErrorPage;
import za.co.liberty.web.pages.ErrorWindowPage;
import za.co.liberty.web.pages.Home;
import za.co.liberty.web.pages.Logon;
import za.co.liberty.web.pages.errors.ConcurrentSessionsForUserPage;
import za.co.liberty.web.pages.errors.ConcurrentSessionsForUserWarningPage;
import za.co.liberty.web.pages.errors.InvalidUserConfigurationErrorPage;
import za.co.liberty.web.pages.errors.NoAccessForUserErrorPage;

/**
 * <p>Overrides the default page routing behaviour.  Certain authentication and time-out 
 * exceptions will route the user to the logon page.  All exceptions that are not 
 * catered for by this class will be routed to the error page.</p>  
 * 
 * <p>A slightly different mechanism is however required for pages that are contained in 
 * windows.  For now a message will be displayed in the window in future we will probably
 * want to close the window and apply normal routing.</p>  
 * 
 */
public class SRSRequestCycleProcessor extends  AbstractRequestCycleListener  {
	
	private static Logger logger =  Logger.getLogger(SRSRequestCycleProcessor.class);
	
	/* 
	 * Messages are usually kept in property files but for error processing
	 * it is kept in static variables to ensure messages are always shown.
	 */
	public static String LOGON_AGAIN_MESSAGE = 
		"Your session expired, please log on again.  If you are in a window please close it first.";
	public static String AUTH_FAILED_LOGON_AGAIN_MESSAGE = 
		"The system was unable to authenticate you, please log on again or check with UAM that you exist as an SRS user";
	public static String WINDOW_LOGON_AGAIN_MESSAGE = 
		"Your session expired, please close this window and log on again.";
	public static String SESSION_EXPIRED = 
		"Your session has expired due to inactivity.  All work that was not saved has been lost.";
	

//	protected Page onRuntimeException(Page page, RuntimeException re) {
	// WICKETTEST WICKETFIX 
	// Lots to fix here, unsure on how to retrieve pages
	@Override
	public IRequestHandler onException(RequestCycle cycle, Exception re) {
			
		if (logger.isDebugEnabled())
			logger.debug("#TST - Container response:" + cycle.getResponse());
		
		IPageRequestHandler pageHandler = PageRequestHandlerTracker.getLastHandler(cycle);
		
		Page responsePage = null;
		IRequestablePage requestablePage = null;
		if (pageHandler != null && pageHandler.isPageInstanceCreated()) {
			requestablePage = pageHandler.getPage();
		}
		logger.error("Handle Error from issuePage=" + ((requestablePage!=null)?requestablePage.getClass():null),re);
		

//		if (logger.isDebugEnabled()) {
//			logger.debug("onRuntimeException  page="+((requestablePage!=null)?requestablePage.getClass():null) 
//					+ "   ,runtimeException="+re.getMessage(), re);
//		}
		
		boolean isWindowPage = (requestablePage instanceof BaseWindowPage);
		@SuppressWarnings("unused")
		boolean isSRSRuntime = (re instanceof SRSRuntimeException);
		Throwable runtimeException = null;
		
		if (re instanceof StalePageException) {
			// If the page was stale, just re-render it
			// (the url should always be updated by an redirect in that case)
			return new RenderPageRequestHandler(new PageProvider(((StalePageException)re).getPage()));
		} else if ((runtimeException = checkForRuntimeException(re, 
				UnauthorizedInstantiationException.class)) != null) {
			/* Check if the user has to log on again */
			responsePage = new Logon();
			responsePage.error(AUTH_FAILED_LOGON_AGAIN_MESSAGE);
			logger.warn("Setting error to LOGON AGAIN");
		}else if((runtimeException = checkForRuntimeException(re, 
				ConcurrentSessionForUserRuntimeException.class)) != null) {
			
			if (((ConcurrentSessionForUserRuntimeException)runtimeException).isWarnOnly()) {
				responsePage = new ConcurrentSessionsForUserWarningPage();
			} else {
				responsePage = new ConcurrentSessionsForUserPage();
			}
			logger.warn("Setting error message to Dual session detected");
		}else if((runtimeException = checkForRuntimeException(re, 
				PageExpiredException.class)) != null) {
			if (isWindowPage) {
				responsePage = new Home(WINDOW_LOGON_AGAIN_MESSAGE);
			}else {
				responsePage = new Home(SESSION_EXPIRED);
			}
			logger.warn("Setting error message to "+SESSION_EXPIRED);
		}else if((runtimeException = checkForRuntimeException(re, 
				InvalidUserConfigurationRuntimeException.class)) != null) {
			responsePage = new InvalidUserConfigurationErrorPage();
			logger.warn("Setting error message to InvalidUserConfiguration");
		}
		if((runtimeException = checkForRuntimeException(re, 
				UserNoAccessRuntimeException.class)) != null) {
			responsePage = new NoAccessForUserErrorPage();
			logger.warn("Setting error message to UserNoAccessRuntimeException");
		}

		/* Check for fatal errors */
		if(runtimeException instanceof SRSRuntimeException) {
			SRSRuntimeException srsRe = (SRSRuntimeException) runtimeException;
			
			if (srsRe.getSeverity() == ExceptionSeverity.FATAL) {
				// Log user out of system
				try {
					logger.debug("Fatal error, invalidating session");
					SRSAuthWebSession.get().invalidateNow();
				} catch (Exception e) {
					// Ignore
				}
			}

		}
		
		
		/* 
		 * Route to error page - Done by returning a RequestHandler to route to a new page
		 */
		if (responsePage == null) {
			ErrorModel errorModel = createErrorModelFromException((RuntimeException)re, requestablePage);
			responsePage = (isWindowPage) ? new ErrorWindowPage(errorModel) : 
				new ErrorPage(errorModel);
		}

		// Route to the responsePage
		if (logger.isDebugEnabled())
			logger.debug("Route to error Page :" + responsePage + "  id=" + responsePage.getPageId());
		RenderPageRequestHandler tmpHandler = new RenderPageRequestHandler(new PageProvider(responsePage), 
				RedirectPolicy.AUTO_REDIRECT);
		return tmpHandler;

	}

	/**
	 * Convert the exception to an error model for the
	 * @param re
	 * @return
	 */
	private ErrorModel createErrorModelFromException(RuntimeException re, IRequestablePage page) {
		
		SimpleDateFormat format = new SimpleDateFormat("yy/MM/dd-HH:mm:ss:SSS");
		
		/* Build error model */
		ErrorModel errorModel = new ErrorModel();
		errorModel.setCause(re);
		errorModel.setOriginatingPage((page instanceof Page) ? (Page)page : null);
		errorModel.setErrorType(ErrorModel.ErrorType.ERROR);
		errorModel.setErrorMessage(re.getMessage());
		errorModel.setWindowPage(page instanceof BaseWindowPage);
		
		try {
			SRSAuthWebSession session = SRSAuthWebSession.get();
			errorModel.setApplicationVersion(session.getApplicationVersionNumber());
			errorModel.setUserName(session.getCurrentUserid());
			errorModel.setServerName(session.getServerHostName());
		} catch (Exception e) {
			// Ignore
		}

		if (re instanceof SRSRuntimeException) {
			/* This is a business specific error */
			SRSRuntimeException srsRe = (SRSRuntimeException) re;
			errorModel.setErrorMessage(srsRe.getMessage());
			errorModel.setTechnicalErrorMessage(
					srsRe.getTechnicalErrorMessage()
					+ "  \n\rAdvised Remedy: "+srsRe.getExceptionType().getAdvisedRemedy());
			if (srsRe.getExceptionType().getSeverity()==ExceptionSeverity.FATAL) {
				/* Fatal errors causes this user to be logged out */
				errorModel.setErrorType(ErrorModel.ErrorType.FATAL);
			}
		} else {
			/* Find the lowest Exception with a message*/
			Throwable cause = re;
			String message = null;
			while ((cause = cause.getCause()) != null) {
				message = (cause.getMessage()!=null) ? cause.getMessage() : message;
			}
			errorModel.setTechnicalErrorMessage(message);
			
		}
		
		errorModel.setIncidentReference(
				errorModel.getErrorType().name()+"-"+format.format(new Date()));
		
		/* Log to file */
		ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
		PrintStream printStream = new PrintStream(arrayOutputStream);
		printStream.println("\n\\/\\/\\/\\/\\/ *************** \\/\\/\\/\\/\\/");
		printStream.println("Incident Nr: "+errorModel.getIncidentReference());
		printStream.println("Page: "+errorModel.getOriginatingPage());
		printStream.println("Error Message: "+errorModel.getErrorMessage());
		printStream.println("Cause: " + errorModel.getTechnicalErrorMessage());
		re.printStackTrace(printStream);
		printStream.println("/\\/\\/\\/\\/\\ *************** /\\/\\/\\/\\/\\");
		
		
		logger.error(arrayOutputStream.toString());
		
		return errorModel;
	}


	/**
	 * Find a runtime exception in the cause hierarchy
	 * 
	 * @param runtimeException
	 * @param exceptionToCheck
	 * @return
	 */
	private Throwable checkForRuntimeException(Throwable runtimeException, Class<? extends RuntimeException> exceptionToCheck) {
		Throwable cause = runtimeException;
		
		if(cause == null)
			return null;
		
		if(cause.getClass().getName().equalsIgnoreCase(exceptionToCheck.getName()))
			return cause;
		else
		   return checkForRuntimeException(cause.getCause(), exceptionToCheck);
			
	}
	
}
