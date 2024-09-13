package za.co.liberty.web.pages.request.tree.nodes;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.naming.NamingException;

import org.apache.log4j.Logger;

import za.co.liberty.agreement.client.vo.AgreementRoleListVO;
import za.co.liberty.agreement.client.vo.AgreementRoleVO;
import za.co.liberty.agreement.client.vo.PropertyVO;
import za.co.liberty.agreement.client.vo.PropertyVOImpl;
import za.co.liberty.agreement.client.vo.RequestVO;
import za.co.liberty.agreement.common.exceptions.ConformanceTypeViolationException;
import za.co.liberty.business.agreement.GatewayProductTransCodesEnum;
import za.co.liberty.common.domain.ApplicationContext;
import za.co.liberty.common.domain.ObjectReference;
import za.co.liberty.common.domain.Percentage;
import za.co.liberty.ftx.domain.vo.ParticularMoneyProvisionVO;
import za.co.liberty.helpers.lookup.ServiceLocator;
import za.co.liberty.interfaces.agreements.requests.ConformanceType;
import za.co.liberty.interfaces.agreements.requests.PropertyKindType;
import za.co.liberty.interfaces.agreements.requests.RequestKindType;
import za.co.liberty.persistence.rating.IDescriptionEntityManager;
import za.co.liberty.persistence.rating.entity.Description;
import za.co.liberty.srs.type.SRSType;
import za.co.liberty.web.pages.request.tree.util.TreeUtil;

/**
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class SRSRequestTreeNode extends AbstractSRSTreeNode {

	private RequestVO srsRequest;
	private SRSRequestTreeNode childTreeNode = null;
	
	private List<SRSRequestTreeNode> childTreeNodeList = new ArrayList<>();
	
	private static Logger logger = Logger.getLogger(SRSRequestTreeNode.class);


	public SRSRequestTreeNode(RequestVO request, SRSTreeManager treeMgr, 
		SRSRequestTreeNode childTreeNode){
		super(treeMgr);	
		this.srsRequest = request;			
		this.childTreeNode = childTreeNode;
		if(srsRequest != null){
			long oid = srsRequest.getObjectReference().getObjectOid();
			String requestDesc = "Request";
			RequestKindType t = RequestKindType.getRequestKindTypeForKind(srsRequest.getKind());
			if (t!=null) {
				requestDesc +=  " - " + t.getDescription();
			}
			setTreeNode(new TreeNode(requestDesc)); 
			setHasChildren(true);
		}
	}
	
	/*
	 * Must be set before process is called on tree
	 */
	public void setChildTreeNode(SRSRequestTreeNode childNode) {
		this.childTreeNode = childNode;
	}
	
	public void addChildTreeNode(SRSRequestTreeNode childNode) {
		childTreeNodeList.add(childNode);
	}

	public void addChildren(){
		ObjectReference objRef = srsRequest.getObjectReference(); 
		String oid = "" + objRef.getObjectOid();
		TreeNode requestId = new TreeNode("OID: " + oid);
		addChild(requestId);		
		TreeNode status = new TreeNode("Status: " + srsRequest.getCurrentStatus());
		addChild(status);
		String strDate = TreeUtil.formatDate(srsRequest.getRequestDate());
		TreeNode reqDate = new TreeNode("Request Date: " + strDate);
		addChild(reqDate);
		strDate = TreeUtil.formatDate(srsRequest.getRequestedDate());
		TreeNode req1Date = new TreeNode("Requested Date: " + strDate);
		addChild(req1Date);
		strDate = TreeUtil.formatDate(srsRequest.getExecutedDate());
		TreeNode exeDate = new TreeNode("Executed Date: " + strDate);
		addChild(exeDate);
		String typeDesc = "";
//		if(this.getTypeDescription(objRef.getTypeOid()) != null){
//			typeDesc = this.getTypeDescription(objRef.getTypeOid()).replace('@','-');
//		}
		TreeNode desc = new TreeNode("Description: " + typeDesc);
		addChild(desc);
		TreeNode properties = new TreeNode("Properties");
		properties = addChild(properties);
		addPropertiesChildren(properties);
		addMP();
		if(childTreeNode != null){
			addChild(childTreeNode);
			childTreeNode.process();			
		}
		if (childTreeNodeList.isEmpty() == false) {
			for (SRSRequestTreeNode node : childTreeNodeList) {
				addChild(node);
				node.process();
			}
		}
	}


	
	
	private void addPropertiesChildren(TreeNode properties){
		PropertyVO[] propertiesVO = srsRequest.getProperties();

		List<PropertyVO> propertyVOList = new ArrayList<PropertyVO>();
		PropertyVO descProperty = new PropertyVOImpl();
		descProperty.setConformanceType(String.class);
		boolean isEntityManagerNull = false;
		for (PropertyVO propertyVO : propertiesVO) {
			propertyVOList.add(propertyVO);
			if(propertyVO.getKind() == PropertyKindType.MovementCode.getPropertyKind()){
				//GatewayProductTransCodesEnum gatewayProductTransCodesEnum = GatewayProductTransCodesEnum.getGatewayProductTransCodesEnum(resultDTO.getMovementCode());
				IDescriptionEntityManager descriptionEntityManager = null;
				try {
					descriptionEntityManager = ServiceLocator.lookupService(IDescriptionEntityManager.class);
				} catch (NamingException e) {

					//e.printStackTrace();
					isEntityManagerNull = true;
				}
				if(!isEntityManagerNull){
					List<Description> portfoliokinds = descriptionEntityManager.findValuesByName("portfolio_kind");
					String vDescription = "";
					try {
						for (Description description : portfoliokinds) {
							if(description.getReference() == Integer.parseInt((String)propertyVO.getValue())){
								vDescription = description.getDescription();
							}
						}
					}
					catch(NumberFormatException nfe){
						
					}
					if(portfoliokinds != null){
						try {
							descProperty.setValue( vDescription);
							descProperty.setDescription("Movement Description");
							propertyVOList.add(descProperty);
						} catch (ConformanceTypeViolationException e) {
							
							e.printStackTrace();
						}

					}
				}
//				GatewayProductTransCodesEnum propEnumValue = GatewayProductTransCodesEnum.getGatewayProductTransCodesEnum((String)propertyVO.getValue());
//				if(propEnumValue != null){
//					try {
//						descProperty.setValue( propEnumValue.getDescription());
//						descProperty.setDescription("Movement Description");
//						propertyVOList.add(descProperty);
//					} catch (ConformanceTypeViolationException e) {
//						
//						e.printStackTrace();
//					}
//				}
			}
		}
		
		PropertyVO[] propertyVOArray = new PropertyVOImpl[propertyVOList.size()];
		propertyVOList.toArray(propertyVOArray);
		String propValStr = new String();
		if(properties != null){
			if(propertyVOArray != null && propertyVOArray.length > 0){
				properties.setHasChildren(true);
			}
			//if (treeManager.isNodeExpanded(properties.getNodePath())){	
				properties.setExpanded(true);
				for(int i=0; i < propertyVOArray.length; i++){
					String desc = propertyVOArray[i].getDescription();
					if(desc == null) {
					    desc = "";
					}
					
					Object propVal = propertyVOArray[i].getValue();
					
					if (propVal != null)
					propValStr = propVal.toString();
					if(propVal instanceof BigDecimal) {
						BigDecimal bigDecVal = (BigDecimal)propVal;
						propValStr = bigDecVal.toPlainString();
					}
					if(propVal instanceof ArrayList) {
						ArrayList propList = (ArrayList)propVal;
						propValStr = new String();
						for(int j=0;j<propList.size();j++)
						{
							propVal = propList.get(j);
							if(propVal!= null && !propVal.equals("")){
								if(propVal instanceof Percentage){
									Percentage percentageVal = (Percentage)propVal;
									propValStr += percentageVal.toString(2)+",";
	
								} else if(propVal instanceof BigDecimal) {
									BigDecimal bigDecVal = (BigDecimal)propVal;
									propValStr += bigDecVal.toPlainString()+",";
								}else{
									propValStr += propVal.toString() + ",";	
								}
							}
						
									
						}
						if (logger.isDebugEnabled())
							logger.debug("propValStr"+propValStr);
						
						if(propList.size() >0 && propValStr.indexOf(",") != -1)	
						propValStr = propValStr.substring(0,propValStr.lastIndexOf(","));
							
						
					}
					
					
					if(propVal != null && !propVal.equals("")){    //mzp0801 #6382 Check for empty string as well
						TreeNode propNode = new TreeNode(desc + ":" + propValStr);
						if (logger.isDebugEnabled())
							logger.debug("---------- " + propVal);
						properties.addChild(propNode);
					}
				}	
			//}
		}

	}

	private IDescriptionEntityManager getDescriptionEntityManager() {
		IDescriptionEntityManager descriptionEntityManager = null;
		try {
			descriptionEntityManager = ServiceLocator.lookupService(IDescriptionEntityManager.class);
		} catch (NamingException e) {
			
			e.printStackTrace();
		}
		return descriptionEntityManager;
	}	
	
	public void addMP(){
		ArrayList list = this.getMoneyProvisionRoleRefs(this.srsRequest);
		if(list != null){
			Iterator iter = list.iterator();
			while(iter.hasNext()){
				ObjectReference objRef = (ObjectReference) iter.next();
				ObjectReference newRef = new ObjectReference();
				newRef.setComponentOid(objRef.getComponentOid());
				newRef.setObjectOid(objRef.getObjectOid());
				newRef.setTypeOid(objRef.getTypeOid());
				ApplicationContext applicationContext = new ApplicationContext();
				ParticularMoneyProvisionVO pmpVO = treeManager.getIntermediary().getMoneyProvision(applicationContext, newRef);
				SRSMoneyProvisionTreeNode mpTreeNode = new SRSMoneyProvisionTreeNode(pmpVO, this.treeManager);
				addChild(mpTreeNode);
				mpTreeNode.process();
			}
		}
	}
	
	
	public ArrayList getMoneyProvisionRoleRefs(RequestVO request){
		return getRoles(request, SRSType.MONEYPROVISIONAGREEMENTROLE);		
	}
	
	private ArrayList getRoles(RequestVO request, long roleType){
		////
		AgreementRoleListVO[] rols = request.getRoleLists();
		for(int k=0; k< rols.length; k++){
			if (logger.isDebugEnabled())
				logger.debug("!!!!!!!!!!!!!!!!!!!!! " + rols[k]);	
		}
		// end
		AgreementRoleVO[] roles = request.getRoles();
		if (logger.isDebugEnabled())
			logger.debug("ROLES: " + roles);
		ArrayList roleRefs = new ArrayList();
		if (logger.isDebugEnabled())
			logger.debug("######################");
		for(int i=0; i < roles.length; i++){
			if (logger.isDebugEnabled())
				logger.debug("###################### " + roles[i].getTypeOid());
			long reqRoleType = roles[i].getTypeOid();
			if(reqRoleType == roleType){
				if (logger.isDebugEnabled())
					logger.debug("######################2");
				
				ObjectReference ref = roles[i].getRolePlayerRef();
				roleRefs.add(ref);
			}
		}
		return roleRefs;
	}
		
	public ArrayList getPaymentRoleRefs(RequestVO request){
		return getRoles(request, SRSType.PAYMENTAGREEMENTROLE);		
	}
		
}
