package za.co.liberty.web.pages.request.tree.nodes;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import javax.ejb.CreateException;

import za.co.liberty.account.domain.vo.AccountEntryVO;
import za.co.liberty.account.domain.vo.AccountVO;
import za.co.liberty.common.domain.ApplicationContext;
import za.co.liberty.common.domain.ObjectReference;
import za.co.liberty.ftx.domain.vo.PaymentVO;
import za.co.liberty.web.pages.request.tree.util.TreeUtil;


/**
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class SRSPaymentTreeNode extends AbstractSRSTreeNode {

	private PaymentVO payment;
	
	public SRSPaymentTreeNode(PaymentVO payment, SRSTreeManager treeMgr){
		super(treeMgr);		
		this.payment = payment;			
		TreeNode paymentNode = new TreeNode("Account Entries");
		this.setTreeNode(paymentNode);
	}
	
	/* (non-Javadoc)
	 * @see za.co.liberty.srs.tree.AbstractSRSTreeNode#addChildrenIfExpanded()
	 */
	public void addChildren() {
		if(payment != null){
			ObjectReference objRef = payment.getObjectReference();
			TreeNode Id = new TreeNode("OID: " + objRef.getObjectOid());
			String strDate = TreeUtil.formatDate(payment.getPostedDate());
			TreeNode posted = new TreeNode("Posted Date: " + strDate);
			TreeNode amount = new TreeNode("Amount: " + payment.getAmount());
			String typeDesc = "";
//			if(getTypeDescription(objRef.getTypeOid()) != null){
//				typeDesc = getTypeDescription(objRef.getTypeOid()).replace('@','-');	
//			}
			TreeNode desc = new TreeNode("Description: " + typeDesc);
			objRef = payment.getSourceAccountObjRef();
			AccountVO srcAccount = this.getAccountVO(objRef);
			objRef = srcAccount.getObjectReference();
			TreeNode debitAcc = new TreeNode("Debit Account: " + typeDesc);
			objRef = payment.getSourceAccountEntryObjRef(); 
			AccountEntryVO aeVO = this.getAccountEntryVO(objRef);
			objRef = aeVO.getObjectReference();			
			typeDesc = getTypeDescription(objRef.getTypeOid());
			TreeNode debitAccEntry = new TreeNode("Debit Account Entry: " + typeDesc);
			objRef = payment.getTargetAccountObjRef();
			AccountVO tarAccount = this.getAccountVO(objRef);
			objRef = tarAccount.getObjectReference();
			typeDesc = getTypeDescription(objRef.getTypeOid());
			TreeNode creditAcc = new TreeNode("Credit Account: " + typeDesc);
			objRef = payment.getTargetAccountEntryObjRef();
			aeVO = this.getAccountEntryVO(objRef);
			objRef = aeVO.getObjectReference();
			typeDesc = getTypeDescription(objRef.getTypeOid());
			TreeNode creditAccEntry = new TreeNode("Credit Account Entry: " + typeDesc);			
			
			addChild(Id);
			addChild(posted);
			addChild(amount);
			addChild(desc);
			addChild(debitAcc);
			addChild(debitAccEntry);
			addChild(creditAcc);
			addChild(creditAccEntry);
			addHierarchies();
		} else {
			TreeNode notFound = new TreeNode("Payment not found!");
			addChild(notFound);
		}
	}
	
	private void addHierarchies(){
		Map hierarchies = this.getHierarchies();
		TreeNode included = new TreeNode("Included In");
		included.setHasChildren(true);
		included = addChild(included);
		TreeNode includes = new TreeNode("Includes");
		includes.setHasChildren(true);
		includes = addChild(includes);		
		if(hierarchies != null){
			Collection includedInList = (Collection) hierarchies.get("includedin");
			addChildren(includedInList, included);
			Collection includesList = (Collection) hierarchies.get("includes");
			addChildren(includesList, includes);	
		}
	}
	
	private void addChildren(Collection collection, TreeNode nodeParent){
		if(collection != null && nodeParent != null){
			Iterator iter = collection.iterator();
			if(iter != null){
				nodeParent.setHasChildren(true);
				if (treeManager.isNodeExpanded(nodeParent.getNodePath())){	
					nodeParent.setExpanded(true);
					while(iter.hasNext()){
						PaymentVO payment = (PaymentVO) iter.next();
						SRSPaymentTreeNode paymentNode = new SRSPaymentTreeNode(payment, this.treeManager);
						TreeNode node = nodeParent.addChild(paymentNode.getTreeNode());
						paymentNode.setTreeNode(node);
						paymentNode.process();				
					}
				}
			}
		}
	}
	
	private AccountEntryVO getAccountEntryVO(ObjectReference accEntryRef){
		
		AccountEntryVO accountEntry = null;
		if(treeManager.getIntermediary() != null && this.payment != null){
			try {
				accountEntry = treeManager.getIntermediary().resolveAccountEntryReference(new ApplicationContext(), accEntryRef);
			} catch (CreateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return accountEntry;
	}

	private AccountVO getAccountVO(ObjectReference accEntryRef){
		
		AccountVO account = null;
		if(treeManager.getIntermediary() != null && this.payment != null){
			try {
				account = treeManager.getIntermediary().resolveAccountReference(new ApplicationContext(), accEntryRef);
			} catch (CreateException e) {
				
				e.printStackTrace();
			}
		}
		return account;
	}
	
	private Map getHierarchies(){
	
		Map hierarchies = null;
		if(treeManager.getIntermediary() != null && this.payment != null){
			try {
				hierarchies = treeManager.getIntermediary().getPaymentHierarchies(new ApplicationContext(), payment.getObjectReference());
			} catch (CreateException e) {
				
				e.printStackTrace();
			}
		}
		return hierarchies;
	}
}
