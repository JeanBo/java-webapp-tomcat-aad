package za.co.liberty.web.pages.tax.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import za.co.liberty.dto.taxxml.TaxGuiPathDTO;
import za.co.liberty.dto.taxxml.TaxLogDTO;
import za.co.liberty.interfaces.tax.ITaxToolsModel;

public class  TaxToolsModel implements Serializable, ITaxToolsModel {

	/**
	 * Model for XML tax processes. 
	 */
	private static final long serialVersionUID = 1L;
	
	private List<TaxGuiPathDTO> retrieveAllLDAPPaths;
	private Map<String,String> retrieveAllTaxLDAPPathsMap;
	private List<TaxLogDTO> retrieveLogList;

	private String logPath;
	private String templatePath;
	
	private String queueName;
	private String queueManager;
	private int queueCount;
	
	
	public List<TaxLogDTO> getRetrieveLogList() {
		return retrieveLogList;
	}
	public void setRetrieveLogList(List<TaxLogDTO> retrieveLogList) {
		this.retrieveLogList = retrieveLogList;
	}

	public Map<String, String> getRetrieveAllTaxLDAPPathsMap() {
		return retrieveAllTaxLDAPPathsMap;
	}
	public void setRetrieveAllTaxLDAPPathsMap(
			Map<String, String> retrieveAllTaxLDAPPathsMap) {
		this.retrieveAllTaxLDAPPathsMap = retrieveAllTaxLDAPPathsMap;
	}
	
	
	public List<TaxGuiPathDTO> getRetrieveAllLDAPPaths() {
		if(retrieveAllLDAPPaths == null) {
			return new ArrayList<TaxGuiPathDTO>();
		}
		return retrieveAllLDAPPaths;
	}
	public void setRetrieveAllLDAPPaths(List<TaxGuiPathDTO> retrieveAllLDAPPaths) {
		this.retrieveAllLDAPPaths = retrieveAllLDAPPaths;
	}
	/* (non-Javadoc)
	 * @see za.co.liberty.web.pages.tax.model.I#getQueueName()
	 */
	public String getQueueName() {
		return queueName;
	}
	/* (non-Javadoc)
	 * @see za.co.liberty.web.pages.tax.model.I#setQueueName(java.lang.String)
	 */
	public void setQueueName(String queueName) {
		this.queueName = queueName;
	}
	/* (non-Javadoc)
	 * @see za.co.liberty.web.pages.tax.model.I#getQueueManager()
	 */
	public String getQueueManager() {
		return queueManager;
	}
	/* (non-Javadoc)
	 * @see za.co.liberty.web.pages.tax.model.I#setQueueManager(java.lang.String)
	 */
	public void setQueueManager(String queueManager) {
		this.queueManager = queueManager;
	}
	/* (non-Javadoc)
	 * @see za.co.liberty.web.pages.tax.model.I#getQueueCount()
	 */
	public int getQueueCount() {
		return queueCount;
	}
	/* (non-Javadoc)
	 * @see za.co.liberty.web.pages.tax.model.I#setQueueCount(int)
	 */
	public void setQueueCount(int queueCount) {
		this.queueCount = queueCount;
	}

}
