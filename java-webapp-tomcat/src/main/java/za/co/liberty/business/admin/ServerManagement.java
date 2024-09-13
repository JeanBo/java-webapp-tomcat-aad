package za.co.liberty.business.admin;

import java.io.Serializable;
import java.util.Date;

import org.apache.log4j.Logger;

import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.interfaces.system.SystemModeType;

public class ServerManagement implements IServerManagement, Serializable {

	private static final long serialVersionUID = 1L;

	@Override
	public String getApplicationVersion() {
		// TODO Auto-generated method stub
		return "POC";
	}

	@Override
	public String getBuildVersion() {
		// TODO Auto-generated method stub
		return "v2";
	}

	@Override
	public SystemModeType getCurrentSystemMode() throws CommunicationException {
		// TODO Auto-generated method stub
		return SystemModeType.ONLINE_MODE;
	}

	@Override
	public boolean isOnlineSystemInReadOnlyMode() throws CommunicationException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isOnlineSystemInBatchOnlyMode() throws CommunicationException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isOnlineSystemInOnlineMode() throws CommunicationException {
		return true;
	}

	@Override
	public void setOnlineSystemMode(SystemModeType mode) throws CommunicationException {
		Logger.getLogger(this.getClass()).warn("Dummy method setOnlineSystemMode");
		
	}

	@Override
	public Date getLastScheduledCommunicationReportDate() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setLastScheduledCommunicationReportDate(Date date) throws CommunicationException {
		// TODO Auto-generated method stub
		
	}

}
