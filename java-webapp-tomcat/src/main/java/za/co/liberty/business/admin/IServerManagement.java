package za.co.liberty.business.admin;

import java.util.Date;

import javax.ejb.Local;

import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.interfaces.system.SystemModeType;

/**
 * Provides info about currently running server as well as access to some
 * system properties.
 * 
 * @author JZB0608 - 10 Jun 2008
 *
 */
@Local
public interface IServerManagement {

	/**
	 * Retrieve the application version information (specified in 
	 * EAR MANIFEST)
	 * 
	 * @return
	 */
	public String getApplicationVersion();
	
	/**
	 * Retrieve the build version
	 * 
	 * @return
	 */
	public String getBuildVersion();
	
	/**
	 * Returns the current system mode.
	 * 
	 * @return  SystemModeType
	 * @throws CommunicationException
	 */
	public SystemModeType getCurrentSystemMode()
		throws CommunicationException;

	/**
	 * Indicates whether the online system is in read only mode.
	 * 
	 * @return  True if the system is in read only mode.
	 * @throws CommunicationException
	 */
	public boolean isOnlineSystemInReadOnlyMode() 
		throws CommunicationException;
	
	/**
	 * Indicates whether the online system is in batch only mode.
	 * 
	 * @return  True if the system is in batch only mode.
	 * @throws CommunicationException
	 */
	public boolean isOnlineSystemInBatchOnlyMode() 
		throws CommunicationException;
	
	/**
	 * Indicates whether the online system is in online mode.
	 * 
	 * @return  True if the system is in read only mode.
	 * @throws CommunicationException
	 */
	public boolean isOnlineSystemInOnlineMode() 
		throws CommunicationException;
	
	/**
	 * Set the system read mode.
	 * 
	 * @param mode  True if the system is in read only mode.
	 * @throws CommunicationException
	 */
	public void setOnlineSystemMode(SystemModeType mode) 
		throws CommunicationException;
	
	/**
	 * Returns the stored Last Scheduled Communication Report Date
	 * 
	 * @return  SystemModeType
	 * @throws CommunicationException
	 */
	public Date getLastScheduledCommunicationReportDate();
	
	/**
	 * Set the Last Scheduled Communication Report Date
	 * @param date
	 * @throws CommunicationException
	 */
	public void setLastScheduledCommunicationReportDate(Date date) throws CommunicationException;
	
}
