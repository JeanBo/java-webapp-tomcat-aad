package za.co.liberty.web.pages.request.tree.nodes;

import javax.naming.NamingException;

import za.co.liberty.business.request.tree.IIntermediaryManager;
import za.co.liberty.helpers.lookup.ServiceLocator;

/**
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class SRSTreeManager {

	private String treeName;
	
	public SRSTreeManager(  String treeName){

		this.treeName = treeName;
	}

	public void addNodeAsExpanded(String nodePath){
	}
	
	public void processState(){
	}


	/**
	 * @return
	 */
	public IIntermediaryManager getIntermediary() {
		IIntermediaryManager intermediaryManager = null;
		try {
			 intermediaryManager = ServiceLocator.lookupService(IIntermediaryManager.class);
		} catch (NamingException e) {
			
		}
		return intermediaryManager;
	}
	
	public boolean isNodeExpanded(String nodePath){
		return false;
	}
	
	/**
	 * @return
	 */
	public String getTreeName() {
		return treeName;
	}

}
