package za.co.liberty.web.pages.request.tree.model;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;



public class RequestTreeModel implements TreeModel, Serializable{

	private SRSRequestNode srsRequestNode;
	public RequestTreeModel (SRSRequestNode srsRequestNode){
		this.srsRequestNode = srsRequestNode;
	}

	public void addTreeModelListener(TreeModelListener l) {
		
	}

	public Object getChild(Object parent, int index) {
		SRSRequestNode requestNode = (SRSRequestNode) parent;
		List<za.co.liberty.web.pages.request.tree.nodes.TreeNode> children = requestNode.getRequestTreeNode().hasChildren()?requestNode.getRequestTreeNode().getChildren():Collections.EMPTY_LIST;
		return new SRSRequestNode(children.get(index));
	}

	public int getChildCount(Object parent) {
		SRSRequestNode requestNode = (SRSRequestNode) parent;
		return  requestNode.getRequestTreeNode().hasChildren()?requestNode.getRequestTreeNode().getChildren().size():0;
	}

	public int getIndexOfChild(Object parent, Object child) {
		SRSRequestNode requestNode = (SRSRequestNode) parent;
		
		List<za.co.liberty.web.pages.request.tree.nodes.TreeNode> children = requestNode.getRequestTreeNode().hasChildren()?requestNode.getRequestTreeNode().getChildren():Collections.EMPTY_LIST;
		int index = 0;
		for (za.co.liberty.web.pages.request.tree.nodes.TreeNode node : children) {
			if(node.equals(child)){
				break;
			}
			index++;
		}
		return index;
	}

	public Object getRoot() {
		return srsRequestNode;
	}

	public boolean isLeaf(Object node) {
		SRSRequestNode requestNode =(SRSRequestNode)node;
		return requestNode.getRequestTreeNode().hasChildren() ? false : true;
	}

	public void removeTreeModelListener(TreeModelListener l) {
		
	}

	public void valueForPathChanged(TreePath path, Object newValue) {
		
	}
}
