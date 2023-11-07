package nl.microsoft.tools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;


@ManagedBean
@SessionScoped
public class AppBean {
	
	private static final Logger logger = LoggerFactory.getLogger(AppBean.class);

    
	String serverUrl;
	String serverName;
    String hostname;
    String ipAddr;
    String remoteAddr;
    int counter;
    
    public int getCounter() {
		return counter;
	}

	public void setCounter(int counter) {
		this.counter = counter;
	}

	int remotePort;
    public String getRemoteAddr() {
		return remoteAddr;
	}

	public void setRemoteAddr(String remoteAddr) {
		this.remoteAddr = remoteAddr;
	}

	public int getRemotePort() {
		return remotePort;
	}

	public void setRemotePort(int remotePort) {
		this.remotePort = remotePort;
	}

	public String getRemoteHost() {
		return remoteHost;
	}

	public void setRemoteHost(String remoteHost) {
		this.remoteHost = remoteHost;
	}

	String remoteHost;
    int serverPort;

	
	public String getServerName() {
		return serverName;
	}

	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

	public int getServerPort() {
		return serverPort;
	}

	public void setServerPort(int serverPort) {
		this.serverPort = serverPort;
	}


	//	Apologies for the poor code..demo purpose only :)
	public void init(){
		HttpServletRequest req = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
		serverUrl = req.getRequestURL().toString();
		serverName = req.getServerName();
		serverPort = req.getServerPort();
		remoteAddr = req.getRemoteAddr();
		remotePort = req.getRemotePort();
		remoteHost = req.getRemoteHost();
		counter++;
		logger.info("initializing page with local counter: "+counter);
		
        InetAddress ip;
        try {
            ip = InetAddress.getLocalHost();
            ipAddr = ""+ip;
            hostname = ip.getCanonicalHostName();
            
         
        } catch (UnknownHostException e) {
        	logger.error("Exception getting hostname of this machine: "+e.getMessage());
        }
		
	}
	

	public String getIpAddr() {
		return ipAddr;
	}

	public void setIpAddr(String ipAddr) {
		this.ipAddr = ipAddr;
	}

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public String getServerUrl() {
		return serverUrl;
	}

	public void setServerUrl(String serverUrl) {
		this.serverUrl = serverUrl;
	}
	
}
