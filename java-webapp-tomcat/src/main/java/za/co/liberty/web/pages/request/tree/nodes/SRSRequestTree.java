package za.co.liberty.web.pages.request.tree.nodes;

import java.util.ArrayList;

import za.co.liberty.agreement.client.vo.AgreementRoleVO;
import za.co.liberty.agreement.client.vo.RequestVO;
import za.co.liberty.agreement.common.AgreementObjectReference;
import za.co.liberty.common.domain.ApplicationContext;
import za.co.liberty.common.domain.ObjectReference;

import za.co.liberty.srs.type.SRSType;

/**
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class SRSRequestTree extends AbstractSRSTree{
	
	private long startRequestId;

	public SRSRequestTree(long requestOID, SRSTreeManager treeManager){
		super(treeManager);
		this.startRequestId = requestOID;
	}

	/**
	 * Build the tree model
	 */
	public TreeNode buildTree(){
		// Base request
		RequestVO request = this.getRequest(startRequestId);		
		
		if(request != null){
		    ApplicationContext context = new ApplicationContext();
		    RequestVO parent = null;
//			RequestVO child = null;
			
		    parent = this.getParentRequest(request);
			
		    // Set the parent node
		    SRSRequestTreeNode parentNode = new SRSRequestTreeNode(parent, treeManager, null);				
			setRootNode(parentNode);
		    
		    // Add children requests if available
		    addChildRequests(context, parent, parentNode);
			
			TreeNode rootTreeNode = this.getRootNode().getTreeNode();
			processTree();
			
			processNodes();
			return rootTreeNode;  
		}
		return null;
	}
	
	/**
	 * Add child requests to a parent recursively
	 * 
	 * @param parent
	 * @param node
	 */
	protected void addChildRequests(ApplicationContext context, RequestVO parent, SRSRequestTreeNode node) {
		// Get direct children that link with targetActual to parent request
		RequestVO[] requestChildren = treeManager.getIntermediary().getRequestChildren(context, parent);
		
		for (RequestVO vo : requestChildren) {
			SRSRequestTreeNode childNode = new SRSRequestTreeNode(vo, treeManager, null);
			node.addChildTreeNode(childNode);
			addChildRequests(context, vo, childNode);
		}
	}
	
	/**
	 * Build the tree model
	 */
	public TreeNode buildTreeOld(){
		// Base request
		RequestVO request = this.getRequest(startRequestId);		
		
		if(request != null){
		    ApplicationContext context = new ApplicationContext();

		    // Get direct children that link with targetActual to parent request
			RequestVO[] requestChildren = treeManager.getIntermediary().getRequestChildren(context, request);
			
			RequestVO parent = null;
			if(requestChildren.length > 0)
			    parent = this.getParentRequest(requestChildren[0]);
			else
			    parent = this.getParentRequest(request);
			
			// First child is parent
			SRSRequestTreeNode childNode = new SRSRequestTreeNode(request, treeManager, null);
			
			TreeNode childTreeNode = childNode.getTreeNode();
			if(parent != null && requestChildren.length > 0){
				SRSRequestTreeNode parentNode = new SRSRequestTreeNode(parent, treeManager, childNode);				
				setRootNode(parentNode);
			} else {
				setRootNode(childNode);
			}
			TreeNode rootTreeNode = this.getRootNode().getTreeNode();
			processTree();
			
			processNodes();
			return rootTreeNode;  
		}
		return null;
	}

	public RequestVO getParentRequest(RequestVO childRequest){
		if(childRequest != null){
			ObjectReference target = childRequest.getObjectReference();
			return this.getRequest(target);
		}
		return null;
	}
	
	public RequestVO getRequest(long oid){
		AgreementObjectReference objRef = new AgreementObjectReference();
		objRef.setObjectOid(oid);
		objRef.setSpecificationOid(-1265413433);
		return this.getRequest(objRef);
	}
	
	public RequestVO getRequest(ObjectReference objReference){
		ApplicationContext context = new ApplicationContext();
	
		RequestVO request = treeManager.getIntermediary().getRequest(context, objReference);
		return request;
	}
	
	private ArrayList getMoneyProvisionRoleRefs(RequestVO request){
		return getRoles(request, SRSType.MONEYPROVISIONAGREEMENTROLE);		
	}
	
	private ArrayList getRoles(RequestVO request, long roleType){
		AgreementRoleVO[] roles = request.getRoles();
		ArrayList roleRefs = new ArrayList();
		for(int i=0; i < roles.length; i++){
			long reqRoleType = roles[i].getTypeOid(); 
			if(reqRoleType == roleType){
				ObjectReference ref = roles[i].getObjectReference();
				roleRefs.add(ref);
			}
		}
		return roleRefs;
	}
	
	private ArrayList getPaymentRoleRefs(RequestVO request){
		return getRoles(request, SRSType.PAYMENTAGREEMENTROLE);		
	}		
}
