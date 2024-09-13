package za.co.liberty.web.pages.request.tree.nodes;

import java.util.Collection;
import java.util.Iterator;

import za.co.liberty.common.domain.ObjectReference;
import za.co.liberty.ftx.domain.vo.MoneyProvisionElementVO;
import za.co.liberty.ftx.domain.vo.MoneySchedulerVO;
import za.co.liberty.ftx.domain.vo.ParticularMoneyProvisionVO;

/**
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class SRSMoneyProvisionTreeNode extends AbstractSRSTreeNode {

	private ParticularMoneyProvisionVO pMoneyProvision;
	
	public SRSMoneyProvisionTreeNode(ParticularMoneyProvisionVO pMoneyProvision, 
	SRSTreeManager treeMgr){
	super(treeMgr);		
		this.pMoneyProvision = pMoneyProvision;	
		TreeNode mpNode = new TreeNode("Money Provision");
		this.setTreeNode(mpNode);
	}
	
	/* (non-Javadoc)
	 * @see za.co.liberty.srs.tree.AbstractSRSTreeNode#addChildrenIfExpanded()
	 */
	public void addChildren() {
		TreeNode oid = new TreeNode("OID:" + pMoneyProvision.getObjectReference().getObjectOid());
		MoneySchedulerVO schedulerVO = pMoneyProvision.getMoneyScheduler();
		ObjectReference objRef = null;
		String typeDesc = "";
		if (schedulerVO != null) {
            objRef = schedulerVO.getObjectReference();
            typeDesc = this.getTypeDescription(objRef.getTypeOid());
            TreeNode mScheduler = new TreeNode("Money Scheduler: " + typeDesc);
            addChild(mScheduler);
		}
		TreeNode extRef = new TreeNode("External Reference: " + pMoneyProvision.getProductReference());
		objRef = pMoneyProvision.getObjectReference();
		
//		if(this.getTypeDescription(objRef.getTypeOid()) != null){
//			typeDesc = this.getTypeDescription(objRef.getTypeOid()).replace('@','-');
//		}
		TreeNode desc = new TreeNode("Description: " + typeDesc);
		addChild(oid);			
					
		addChild(extRef);			
		addChild(desc);
		addMoneyProvisionElementChildren();
	}
	
	private void addMoneyProvisionElementChildren(){
		Collection mpes = pMoneyProvision.getMoneyProvisionElements();
		if(mpes != null){
			Iterator iter = mpes.iterator();
			while(iter.hasNext()){
				MoneyProvisionElementVO element = (MoneyProvisionElementVO) iter.next();
				SRSMoneyProvisionElementTreeNode mpeNode = new SRSMoneyProvisionElementTreeNode(element, this.treeManager);
				addChild(mpeNode);
				mpeNode.process();						
			}
		}
	}
}
