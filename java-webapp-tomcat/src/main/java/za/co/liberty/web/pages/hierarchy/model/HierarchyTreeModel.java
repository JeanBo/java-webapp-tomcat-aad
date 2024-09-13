package za.co.liberty.web.pages.hierarchy.model;

import java.io.Serializable;
import java.util.Comparator;
import java.util.List;

import javax.naming.NamingException;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import za.co.liberty.business.guicontrollers.hierarchy.IHierarchyOrganogramGUIController;
import za.co.liberty.dto.agreement.SimpleAgreementDetailDTO;
import za.co.liberty.dto.gui.tree.TreeNodeDTO;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.helpers.lookup.ServiceLocator;
import za.co.liberty.web.pages.hierarchy.TreeNodeWrapper;

/**
 * The HierachyTreeModel class will populate a tree object with the Hierachy
 * structure from the party database. In order to use the model creat a wicket
 * page with a BaseTree object.
 * 
 * BaseTree hierarchyTree; hierarchyTree = new LinkTree("hierarchyTree", new
 * HierarchyTreeModel());
 * 
 * Each node is a wrapped in a TreeNodeWrapper. The TreeNodeWrapper does all the
 * work of obtaining the children.
 * 
 * @author HAB1404
 * 
 */
public class HierarchyTreeModel implements  TreeModel, Serializable {

	private static final long serialVersionUID = 1L;

	TreeNodeWrapper root;
	private boolean isAgreementNodeClicked;
	
	transient private IHierarchyOrganogramGUIController hierarchyOrganogramManagement;
	
	private List<SimpleAgreementDetailDTO> agreementDetails;
	TreeNodeDTO treeNodeDTO;
	
	boolean userViewHierarchyNode;
	
	

	public boolean isUserViewHierarchyNode() {
		return userViewHierarchyNode;
	}


	public void setUserViewHierarchyNode(boolean userViewHierarchyNode) {
		this.userViewHierarchyNode = userViewHierarchyNode;
	}


	public TreeNodeDTO getTreeNodeDTO() {
		return treeNodeDTO;
	}


	public void setTreeNodeDTO(TreeNodeDTO treeNodeDTO) {
		this.treeNodeDTO = treeNodeDTO;
	}


	public void addTreeModelListener(TreeModelListener l) {

	}
	
	
	/**
	 * Retrun the child object at index position of the parent
	 */
	public Object getChild(Object parent, int index) {
		if (parent instanceof TreeNodeWrapper) {
			TreeNodeWrapper node = (TreeNodeWrapper) parent;
			return node.getChildAt(index);
		}
		return null;
	}
	

	/**
	 * Returns the count of children
	 */
	public int getChildCount(Object parent) {
		if (parent instanceof TreeNodeWrapper) {
			TreeNodeWrapper node = (TreeNodeWrapper) parent;
			return node.getChildCount();
		}
		return 0;
	}
	
	/**
	 * Returnt the position a specific child is at in the list
	 */
	public int getIndexOfChild(Object parent, Object child) {
		if (parent instanceof TreeNodeWrapper) {
			TreeNodeWrapper node = (TreeNodeWrapper) parent;
			return node.getIndex((TreeNodeWrapper) child);

		}
		return -1;
	}

	/**
	 * Return the root object of the tree
	 */
	public Object getRoot() {
		if (root == null) {
			root = new TreeNodeWrapper(null, getHierarchyOrganogramController().getRootNode());
		}
		return root;
	}

	/**
	 * Returns false if the node has children
	 */
	public boolean isLeaf(Object parent) {
		if (parent instanceof TreeNodeWrapper) {
			TreeNodeWrapper node = (TreeNodeWrapper) parent;

		}
		return false;
	}

	/**
	 * Not implemented
	 */
	public void removeTreeModelListener(TreeModelListener l) {

	}

	/**
	 * Not implemented
	 */
	public void valueForPathChanged(TreePath path, Object newValue) {

	}
	
	/**
	 * Sort request kinds alphabetically
	 */
	public Comparator<SimpleAgreementDetailDTO> getAgreementNumberComparator() {
		return new Comparator<SimpleAgreementDetailDTO> () {
			public int compare(SimpleAgreementDetailDTO o1, SimpleAgreementDetailDTO o2) {
				return o1.getAgreementNumber().compareTo(o2.getAgreementNumber());
			}
		};
	}
	
	/**
	 * 
	 */
	protected IHierarchyOrganogramGUIController getHierarchyOrganogramController() {
		if (hierarchyOrganogramManagement == null) {
			try {
				hierarchyOrganogramManagement = ServiceLocator.lookupService(IHierarchyOrganogramGUIController.class);
			} catch (NamingException namingErr) {
				throw new CommunicationException(namingErr);
			}
		}
		return hierarchyOrganogramManagement;
	}

	/**
	 * Returns the stub for the session bean
	 */

	public boolean isAgreementNodeClicked() {
		return isAgreementNodeClicked;
	}

	public void setAgreementNodeClicked(boolean isAgreementNodeClicked) {
		this.isAgreementNodeClicked = isAgreementNodeClicked;
	}

	public List<SimpleAgreementDetailDTO> getAgreementDetails() {
		return agreementDetails;
	}

	public void setAgreementDetails(List<SimpleAgreementDetailDTO> agreementDetails) {
		this.agreementDetails = agreementDetails;
	}

}
