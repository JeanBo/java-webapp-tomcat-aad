package za.co.liberty.web.pages.request.tree.nodes;
/**
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public abstract class AbstractSRSTree {	

	private AbstractSRSTreeNode rootNode;
	protected SRSTreeManager treeManager;
		
	public AbstractSRSTree(SRSTreeManager treeMgr){
		this.treeManager = treeMgr;
	}
	
	public abstract TreeNode buildTree();
	
	public void processTree(){
		if(this.rootNode != null){
			TreeNode rootTreeNode = rootNode.getTreeNode();
			treeManager.addNodeAsExpanded(rootTreeNode.getNodePath());
			treeManager.processState();
		}
	}
	
	public void processNodes(){
		if(rootNode != null){		
			rootNode.process();
		}
	}

	/**
	 * @return
	 */
	public AbstractSRSTreeNode getRootNode() {
		return rootNode;
	}

	/**
	 * @param node
	 */
	public void setRootNode(AbstractSRSTreeNode node) {
		rootNode = node;
	}
}
