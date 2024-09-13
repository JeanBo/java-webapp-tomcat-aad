package za.co.liberty.web.pages.request.tree.nodes;
/**
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public abstract class AbstractSRSTreeNode {
	
	private TreeNode treeNode;
	protected SRSTreeManager treeManager;
	
	
	public AbstractSRSTreeNode(SRSTreeManager treeMgr){
		this.treeManager = treeMgr;
	}
	
	/**
	 * @return
	 */
	public TreeNode getTreeNode() {
		return treeNode;
	}
	
	public void addChild(AbstractSRSTreeNode childNode){
		if(this.treeNode != null && childNode != null){
			TreeNode childTreeNode = childNode.getTreeNode();			
			TreeNode updateChildTreeNode = treeNode.addChild(childTreeNode);
			childNode.setTreeNode(updateChildTreeNode);
		} 
	}

	public TreeNode addChild(TreeNode node){
		if(this.treeNode != null){
			return treeNode.addChild(node);			
		} 
		return null;
	}
	
	public void setHasChildren(boolean bool){
		if(this.treeNode != null){
			treeNode.setHasChildren(bool);			
		}
	}
	
	public void setExpanded(boolean bool){
		if(this.treeNode != null){
			treeNode.setExpanded(bool);			
		}
	}
	
	public String getNodePath(){
		if(this.treeNode != null){
			return treeNode.getNodePath();			
		}
		return "";
	}
	
	public final void process(){
//		System.out.println("----- In Process ---- " + getNodePath());
		setHasChildren(true);		
		setExpanded(true);	
		addChildren();
		
	}

	/**
	 * @param node
	 */
	public void setTreeNode(TreeNode node) {
		treeNode = node;
		setHasChildren(true);
	}	
	
	public abstract void addChildren();
	
	public String getTypeDescription(long typeOid){
		if(this.treeManager != null){
			
			if(treeManager.getIntermediary() != null){
				String desc = treeManager.getIntermediary().getTypeDescription(typeOid);
				return desc; 		
			}
		}
		return "";
	}
}
