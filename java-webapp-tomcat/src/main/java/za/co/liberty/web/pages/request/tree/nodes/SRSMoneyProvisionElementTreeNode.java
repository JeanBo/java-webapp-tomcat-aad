package za.co.liberty.web.pages.request.tree.nodes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.ejb.CreateException;

import za.co.liberty.common.domain.ApplicationContext;
import za.co.liberty.common.domain.ObjectReference;
import za.co.liberty.ftx.domain.vo.MoneyProvisionElementVO;
import za.co.liberty.ftx.domain.vo.PaymentVO;
import za.co.liberty.srs.util.intermediary.IntermediaryConstants;
import za.co.liberty.web.pages.request.tree.util.TreeUtil;

/**
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class SRSMoneyProvisionElementTreeNode extends AbstractSRSTreeNode {

	private MoneyProvisionElementVO mpeVO;
	
	
	
	public SRSMoneyProvisionElementTreeNode(MoneyProvisionElementVO mpeVO,
		SRSTreeManager treeMgr){
		super(treeMgr);		
		this.mpeVO = mpeVO;		
		TreeNode mpeNode = new TreeNode("Money Provision Element");
		this.setTreeNode(mpeNode);
	}
	
	/* (non-Javadoc)
	 * @see za.co.liberty.srs.tree.AbstractSRSTreeNode#addChildrenIfExpanded()
	 */
	public void addChildren() {
		ObjectReference objRef = mpeVO.getObjectReference();
		TreeNode oid = new TreeNode("OID:" + objRef.getObjectOid());
		String strDate = TreeUtil.formatDate(mpeVO.getEndDate());
		TreeNode dueDate = new TreeNode("Due Date: " + strDate);
		TreeNode amount = new TreeNode("Amount: " + mpeVO.getBaseAmount());
		String typeDesc = "";
//		if(this.getTypeDescription(objRef.getTypeOid()) != null){
//			typeDesc = this.getTypeDescription(objRef.getTypeOid()).replace('@','-');
//		}
		TreeNode desc = new TreeNode("Description: " + typeDesc);
		addChild(oid);			
		addChild(dueDate);			
		addChild(amount);			
		addChild(desc);
		addPaymentChildren(objRef);			
	}
		
	public void addPaymentChildren(ObjectReference objRef){
		if(objRef != null){
			System.out.println("----------- MPE OID: " + objRef);
			
			ArrayList list = new ArrayList();
			list.add(objRef);
			Collection payments = null;
			try {
				payments = treeManager.getIntermediary().getPaymentsByReferences(new ApplicationContext(), list, IntermediaryConstants.BY_MPE_REFERENCES);
			} catch (CreateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			if(payments != null){
				Iterator iter = payments.iterator();
				while(iter.hasNext()){
					PaymentVO payment = (PaymentVO) iter.next(); 
					SRSPaymentTreeNode paymentNode = new SRSPaymentTreeNode(payment, treeManager);
					addChild(paymentNode);
					paymentNode.process();		
				}
			}
		}
	}
}
