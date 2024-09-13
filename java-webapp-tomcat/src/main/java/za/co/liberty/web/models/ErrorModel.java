package za.co.liberty.web.models;

import java.io.Serializable;

import org.apache.wicket.Page;

/**
 * Data model for the error page.
 * 
 * @author JZB0608 - 02 Sep 2008
 *
 */
public class ErrorModel implements Serializable {

	private static final long serialVersionUID = 6923069757386718054L;

	/**
	 * Defines the type of error.  Can't use a property file as there might be a system error
	 * preventing the message from being read.  
	 * 
	 * @author JZB0608 - 04 Sep 2008
	 */
	public enum ErrorType {
		ERROR("An unexpected system error occurred while trying to process your request."), 
		FATAL("A fatal system error occurred and you have been logged out of the system.",
				"A fatal system error occured! \n\rPlease close this window.");
		
		private String message;
		private String windowMessage;
		
		ErrorType(String message) {
			this(message, message);
		}
		
		ErrorType(String message, String windowMessage) {
			this.message = message;
			this.windowMessage = windowMessage;
		}
			
		public String getMessage() {
			return message;
		}
		
		public String getWindowMessage() {
			return windowMessage;
		}
	}
	
	private String incidentReference;
	private Page originatingPage;
	private Throwable cause;
	private ErrorType errorType;
	private String errorMessage;
	private String technicalErrorMessage;
	private String applicationVersion;
	private String userName;
	private String serverName;
	private boolean isWindowPage = false;
	
	
	
	public boolean isWindowPage() {
		return isWindowPage;
	}
	public void setWindowPage(boolean isWindowPage) {
		this.isWindowPage = isWindowPage;
	}
	public String getTechnicalErrorMessage() {
		return technicalErrorMessage;
	}
	public void setTechnicalErrorMessage(String technicalErrorMessage) {
		this.technicalErrorMessage = technicalErrorMessage;
	}
	public ErrorType getErrorType() {
		return errorType;
	}
	public void setErrorType(ErrorType errorType) {
		this.errorType = errorType;
	}
	public String getIncidentReference() {
		return incidentReference;
	}
	public void setIncidentReference(String incidentReference) {
		this.incidentReference = incidentReference;
	}
	public String getApplicationVersion() {
		return applicationVersion;
	}
	public void setApplicationVersion(String applicationVersion) {
		this.applicationVersion = applicationVersion;
	}
	public Throwable getCause() {
		return cause;
	}
	public void setCause(Throwable cause) {
		this.cause = cause;
	}
	public String getErrorMessage() {
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	public Page getOriginatingPage() {
		return originatingPage;
	}
	public void setOriginatingPage(Page originatingPage) {
		this.originatingPage = originatingPage;
	}
	public String getServerName() {
		return serverName;
	}
	public void setServerName(String serverName) {
		this.serverName = serverName;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	
	
}
