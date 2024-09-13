package za.co.liberty.web.pages.request.tree.model;

import java.io.Serializable;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import javax.swing.tree.TreeNode;


public class SRSRequestNode implements TreeNode , Serializable{

	private za.co.liberty.web.pages.request.tree.nodes.TreeNode requestTreeNode;
	public SRSRequestNode(za.co.liberty.web.pages.request.tree.nodes.TreeNode requestTreeNode){
		this.requestTreeNode = requestTreeNode;
	}
	
	public Enumeration children() {
		
		
		return (requestTreeNode.hasChildren()) ? Collections.enumeration(requestTreeNode.getChildren()):Collections.enumeration(Collections.EMPTY_LIST);
		
	}

	public boolean getAllowsChildren() {
		
		return (requestTreeNode.hasChildren()) ? true:false;
	}

	public TreeNode getChildAt(int childIndex) {
		requestTreeNode.hasChildren();
		za.co.liberty.web.pages.request.tree.nodes.TreeNode child = (za.co.liberty.web.pages.request.tree.nodes.TreeNode) requestTreeNode.getChildren().get(childIndex);
		
		return new SRSRequestNode(child);
	}

	public int getChildCount() {
		return	(requestTreeNode.hasChildren()) ? requestTreeNode.getChildren().size():0;
		
	}

	public int getIndex(TreeNode node) {
		if(((SRSRequestNode)getParent()).getRequestTreeNode().getName().equalsIgnoreCase("noparent")){
			
			return 0;
		}
		else{
			List<za.co.liberty.web.pages.request.tree.nodes.TreeNode> children = ((SRSRequestNode)getParent()).getRequestTreeNode().getChildren();
			int index = 0;
			for (za.co.liberty.web.pages.request.tree.nodes.TreeNode object : children) {
				if(object.equals(node)){
					break;
				}
				index++;
				
			}
			return index;
		}
		
	}

	public TreeNode getParent() {
		
		return new SRSRequestNode(requestTreeNode.getParent() != null ? requestTreeNode.getParent() : new za.co.liberty.web.pages.request.tree.nodes.TreeNode("noparent"));
	}

	public boolean isLeaf() {
		
		return(requestTreeNode.hasChildren()) ? false:true;
	}

	public za.co.liberty.web.pages.request.tree.nodes.TreeNode getRequestTreeNode() {
		return requestTreeNode;
	}

	public void setRequestTreeNode(
			za.co.liberty.web.pages.request.tree.nodes.TreeNode requestTreeNode) {
		this.requestTreeNode = requestTreeNode;
	}

	@Override
	public String toString() {
		
		return this.getRequestTreeNode().getName();
	}

	
}
