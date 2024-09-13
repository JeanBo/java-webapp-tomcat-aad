package za.co.liberty.business.guicontrollers.hierarchy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.log4j.Logger;

import za.co.liberty.business.agreement.IAgreementManagement;
import za.co.liberty.business.guicontrollers.IContextManagement;
import za.co.liberty.business.party.IPartyManagement;
import za.co.liberty.business.security.ISecurityManagement;
import za.co.liberty.dto.agreement.SimpleAgreementDetailDTO;
import za.co.liberty.dto.contracting.ResultAgreementDTO;
import za.co.liberty.dto.contracting.ResultPartyDTO;
import za.co.liberty.dto.gui.tree.TreeNodeDTO;
import za.co.liberty.dto.party.HierarchyNodeDTO;
import za.co.liberty.dto.party.PartyDTO;
import za.co.liberty.dto.userprofiles.ContextDTO;
import za.co.liberty.dto.userprofiles.ISessionUserProfile;
import za.co.liberty.dto.userprofiles.SessionUserHierarchyNodeDTO;
import za.co.liberty.exceptions.data.DataNotFoundException;
import za.co.liberty.interfaces.party.OrganisationType;
import za.co.liberty.interfaces.persistence.party.flow.INavigateTreeInformationPanelFlo;



/**
 * This will act as  the gui controller for the liberty tree view of branches. Call will be made to the various business beans.
 * @author JWV2310
 *
 */
@Stateless
public class HierarchyOrganogramGUIController implements IHierarchyOrganogramGUIController {
	
	private static final Logger logger = Logger.getLogger(HierarchyOrganogramGUIController.class);
	
	public TreeNodeDTO getRootNode() {
		TreeNodeDTO dto = new TreeNodeDTO();
		dto.setFullname("Liberty Hierarchy");
		dto.setOid(-1);
		dto.setContextType(-1);
		dto.setType(-1);
		return dto;
	}

	public Collection<SimpleAgreementDetailDTO> findAllAgreementsLinkedToHierarchyNodeMore(long agreementID,Integer fromFetch, Integer toFetch){
//		return partyManagement.findAllAgreementsLinkedToHierarchyNodeMore(agreementID, fromFetch, toFetch);
		throw new RuntimeException("not implemented");
	} 
	
	public PartyDTO getPartyDTOWithObjectOid(long agreementID) throws DataNotFoundException{
//		return partyManagement.getPartyDTOWithObjectOid(agreementID);
		throw new RuntimeException("not implemented");
	}
	
		
	public List<TreeNodeDTO> findChildren(TreeNodeDTO parent)
	  throws DataNotFoundException {
		
		logger.info("FindChildren = " + ((parent!=null)?parent.getOid() : "null"));
		
		if (parent.getOid() == -1L) {
//			return findAllNodes(1642l);
			List<TreeNodeDTO> resp = new ArrayList<TreeNodeDTO>();
			TreeNodeDTO d = new TreeNodeDTO();
			d.setFullname("Agency DIV");
			d.setType(OrganisationType.DIVISION.getType());
			d.setExternalRef("AAA");
			d.setOid(1L);
			resp.add(d);
			
			d = new TreeNodeDTO();
			d.setFullname("Broker DIV");
			d.setType(OrganisationType.DIVISION.getType());
			d.setExternalRef("BBB");
			d.setOid(2L);
			resp.add(d);
			return resp;
		}
		return Collections.EMPTY_LIST;
//		return findChildren(parent.getOid());

	}
	
	@SuppressWarnings("unchecked")
	public List<TreeNodeDTO> findChildren(long id) throws DataNotFoundException {
//		List<INavigateTreeInformationPanelFlo> flo = partyManagement.findAllNodeChildren(id,TreeNodeDTO.class);
//		List<TreeNodeDTO> nodeChildrenList = (List<TreeNodeDTO>) ((Collection<?> ) flo);
		throw new RuntimeException("not implemented");
//		return nodeChildrenList;
	}
	
	@SuppressWarnings("unchecked")
	public List<TreeNodeDTO> findAllNodes(Long type) throws DataNotFoundException {
//		try{
//			List<INavigateTreeInformationPanelFlo> flo = partyManagement.findAllPartyTypes(type, TreeNodeDTO.class);
//			List<TreeNodeDTO> temp = (List<TreeNodeDTO>) ((Collection<?> ) flo);
//			
//			return temp;
//		}catch (Exception e) {
//			throw new DataNotFoundException(
//			"Could not find the nodes for the type " + type, e);
//		} 
		throw new RuntimeException("not implemented");
	}
	
	public ResultPartyDTO findPartyIntermediaryWithAgreementNr(long id) throws DataNotFoundException {
//		return partyManagement.findPartyIntermediaryWithAgreementNr(id);
		throw new RuntimeException("not implemented");
	}
	
	public ResultAgreementDTO findAgreementWithSRSAgreementNr(long id) throws DataNotFoundException{
//		return agreementManagement.findAgreementWithSRSAgreementNr(id);
		throw new RuntimeException("not implemented");
	}
	public HierarchyNodeDTO getHierarchyNodeDTO(long id) throws DataNotFoundException{
//		return hierarchyManagement.getHierarchyNodeDTO(id);
		throw new RuntimeException("not implemented");
	}
	
	public int findSubAgreementCount(long id) {
//		return partyManagement.findAllNodeSubAgreementCount(id);
		return 100;
	}
	
	public ContextDTO setContext(long agreementNumber, ContextDTO contextDTO ) throws DataNotFoundException{
//		ContextDTO newContextDTO = contextDTO;
//		
//		ResultPartyDTO partyDTO =  findPartyIntermediaryWithAgreementNr(agreementNumber);  
//		ResultAgreementDTO agmDTO =  findAgreementWithSRSAgreementNr(agreementNumber); 
//		ContextDTO dto = contextManagement.getContext(partyDTO,agmDTO);
//		
//		newContextDTO.setPartyContextDTO(dto.getPartyContextDTO());
//		newContextDTO.setAgreementContextDTO(dto.getAgreementContextDTO());
//		
//		return newContextDTO;
		throw new RuntimeException("not implemented");
	}
	
	public boolean canUserViewAgreement(long hasHomePartyID, ISessionUserProfile session){
		logger.info("canUserViewAgreement  homeParty=" + hasHomePartyID);
		return true;
	}
	
	public List<Long> getLogginInUserHierarchySecurityLevel(long partyId, ISessionUserProfile profile){

			boolean hasHierarchicalAccess = profile.hasHierarchicalAccess();
			SessionUserHierarchyNodeDTO hierarchyNodeAccessDTO = null;
			List<Long> hierarchyBuildIDList = new ArrayList<Long>();

//			if(hasHierarchicalAccess) {
//
//				Collection<SessionUserHierarchyNodeDTO> nodeAccessList = profile.getHierarchicalNodeAccessList();
//				List<SessionUserHierarchyNodeDTO> arrHierarchyNodeDTO  = new ArrayList<SessionUserHierarchyNodeDTO>(nodeAccessList);
//
//				if(!arrHierarchyNodeDTO.isEmpty()){
//					hierarchyNodeAccessDTO = arrHierarchyNodeDTO.get(0);
//				}
//				OrganisationType nodeAccessType = hierarchyNodeAccessDTO.getOrganisationType();
//				long parentPartyOID = 0L;
//				ResultPartyDTO levelUpNodeDTO = null;
//				parentPartyOID = hierarchyNodeAccessDTO.getPartyOid();
//
//				while (nodeAccessType != OrganisationType.DIVISION) {
//					if (logger.isDebugEnabled())
//						logger.debug("  -- Processing hierarchy node type:"+nodeAccessType);
//						levelUpNodeDTO = partyManagement.findParentOfHierarchyNode(parentPartyOID,nodeAccessType.getType());
//						nodeAccessType = OrganisationType.getOrganisationType(levelUpNodeDTO.getTypeOid());
//						hierarchyBuildIDList.add(levelUpNodeDTO.getPartyOid());
//						parentPartyOID=levelUpNodeDTO.getPartyOid();
//					
//				}
//			}else{
				hierarchyBuildIDList = new ArrayList<Long>();
//			}
			
			return hierarchyBuildIDList;

	}

}
