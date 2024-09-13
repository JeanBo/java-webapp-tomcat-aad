package za.co.liberty.web.pages.hierarchy;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

import javax.naming.NamingException;
import javax.swing.tree.TreeNode;

import org.apache.log4j.Logger;

import za.co.liberty.business.guicontrollers.NavigationTree;
import za.co.liberty.business.guicontrollers.hierarchy.IHierarchyOrganogramGUIController;
import za.co.liberty.dto.gui.tree.TreeNodeDTO;
import za.co.liberty.exceptions.data.DataNotFoundException;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.helpers.lookup.ServiceLocator;

/**
 * The class is used to wrap the party information in for use in the
 * HierarchyTreeModel The underlying data is stored in the TreeNodeDTO. A lazy
 * load is used to collect the data.
 * 
 * @author HAB1404
 * 
 */
public class TreeNodeWrapper implements TreeNode, Serializable {
	
	private static final long serialVersionUID = 8096222928637675666L;
	
	private static final Logger logger = Logger.getLogger(TreeNodeWrapper.class);
	/**
	 * Wrapped data object
	 */
	TreeNodeDTO dto;

	/**
	 * Children of the current node
	 */
	Vector<TreeNodeWrapper> children;

	/**
	 * Parent of the current node
	 */
	TreeNodeWrapper parent;

	/**
	 * Session bean used to collect data
	 */
	transient private NavigationTree navigationTree;
	
	transient private IHierarchyOrganogramGUIController hierarchyOrganogramManagement;

	/**
	 * The parent and the data object must be supplied to create an instance
	 * 
	 * @param parent
	 * @param dto
	 */
	public TreeNodeWrapper(TreeNodeWrapper parent, TreeNodeDTO dto) {
		this.dto = dto;
		this.parent = parent;
	}

	/**
	 * Returns a list of the children. as and when needed.
	 * 
	 * @return
	 */
	private Vector<TreeNodeWrapper> getChildren() {
		if (children == null) {
			children = new Vector<TreeNodeWrapper>();
			List<TreeNodeDTO> list;
			try {
				 list = getHierarchyOrganogramController().findChildren(dto);
 				 for (TreeNodeDTO node : list) {
					children.add(new TreeNodeWrapper(this, node));
				 }
			} catch (DataNotFoundException e) {
				logger.error("Error retrieving children for the node");
				children = null;
			} 
		}
		return children;
	}

	/**
	 * Return a list of the children as an enumeration
	 */
	public Enumeration children() {
		Vector<TreeNodeWrapper> list = getChildren();
		if (list != null) {
			return list.elements();
		}
		return null;
	}

	/**
	 * Check to see of new children can be added
	 */
	public boolean getAllowsChildren() {

		return true;
	}

	/**
	 * Returns a child node at position childIndex in the list of children
	 */
	public TreeNode getChildAt(int childIndex) {
		Vector<TreeNodeWrapper> list = getChildren();
		if (list != null) {
			return list.elementAt(childIndex);
		}
		return null;

	}

	/**
	 * Returns a count of the children
	 */
	public int getChildCount() {
		Vector<TreeNodeWrapper> list = getChildren();
		if (list != null) {
			return list.size();
		}
		return -1;
	}

	/**
	 * Returnt the position of node in the list of children
	 */
	public int getIndex(TreeNode node) {
		Vector<TreeNodeWrapper> list = getChildren();
		if (list != null) {
			for (int k = 0; k < list.size(); k++) {
				if (list.get(k).equals(node)) {
					return k;
				}
			}
		}

		return 0;
	}

	/**
	 * Return the parent object. Parent is null is root node.
	 */
	public TreeNode getParent() {
		return parent;
	}

	/**
	 * Returns true if node has no children
	 */
	public boolean isLeaf() {
		Vector<TreeNodeWrapper> list = getChildren();
		if (list != null) {
			return list.size() == 0;
		}
		return true;
	}

	/**
	 * Returns the caption to be displayed in the tree.
	 */
	@Override
	public String toString() {
		return dto.toString();
	}

	/**
	 * Returns the gui controller
	 * 
	 * @return
	 */
	protected IHierarchyOrganogramGUIController getHierarchyOrganogramController() {
		if (hierarchyOrganogramManagement == null) {
			try {
				hierarchyOrganogramManagement = ServiceLocator.lookupService(IHierarchyOrganogramGUIController.class);
				
			} catch (NamingException namingErr) {
				//logger.error(this.getPageName()
				System.err.println(""
						+ " hierarchyOrganogramManagement can not be looked up:"
						+ namingErr);
				CommunicationException comm = new CommunicationException(" hierarchyOrganogramManagement can not be looked up",namingErr);
				throw comm;
			}
		}
		return hierarchyOrganogramManagement;
	}
	
	public TreeNodeDTO getTreeNodeDTO(){
		return dto;
	}
	
}
