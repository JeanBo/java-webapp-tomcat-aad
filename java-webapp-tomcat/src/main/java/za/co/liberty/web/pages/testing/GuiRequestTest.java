package za.co.liberty.web.pages.testing;

import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import za.co.liberty.business.guicontrollers.IContextManagement;
import za.co.liberty.business.party.IPartyManagement;
import za.co.liberty.business.request.IGuiRequestManagement;
import za.co.liberty.dto.contracting.ResultPartyDTO;
import za.co.liberty.helpers.lookup.ServiceLocator;
import za.co.liberty.persistence.srs.entity.GuiRequestEntity;
import za.co.liberty.persistence.srs.entity.GuiRequestImageTypeEntity;
import za.co.liberty.persistence.srs.entity.GuiRequestRequestEntity;
import za.co.liberty.web.pages.BasePage;
import za.co.liberty.web.pages.hierarchy.MaintainHierarchyPage;
import za.co.liberty.web.pages.party.MaintainPartyPage;
import za.co.liberty.web.system.SRSAuthWebSession;

/**
 * A test page used for displaying data stored in the GuiRequest table. 
 * 
 * @author JZB0608 - 09 Apr 2009
 *
 */
public class GuiRequestTest extends BasePage {
	
	private static final long serialVersionUID = 1L;

	public GuiRequestTest(PageParameters parms) {
		if (parms.get("hierarchy")!=null && parms.get("requestKind")!=null) {
			long hierarchy = parms.get("hierarchy").toLong();
			long requestKind = parms.get("requestKind").toLong();
			
			GuiRequestImageTypeEntity type = GuiRequestImageTypeEntity.CurrentImage;
			
			if (parms.get("image")!=null) {
				if (parms.get("image").toString().equalsIgnoreCase("before")) {
					type = GuiRequestImageTypeEntity.BeforeImage;
					System.out.println("Retrieving before image");
				}
			}
			System.out.println("Hierarchy="+hierarchy+" ,requestKind="+requestKind
					+ " ,imageType="+type);
			testHierarchyGuiRequest(hierarchy, requestKind, type);
		} else if (parms.get("party")!=null && parms.get("requestKind")!=null) {
			long partyRequestKey = parms.get("party").toLong();
			long requestKind = parms.get("requestKind").toLong();
			
			GuiRequestImageTypeEntity type = GuiRequestImageTypeEntity.CurrentImage;
			
			if (parms.get("image")!=null) {
				if (parms.get("image").toString().equalsIgnoreCase("before")) {
					type = GuiRequestImageTypeEntity.BeforeImage;
					System.out.println("Retrieving before image");
				}
			}
			System.out.println("partyRequestKey="+partyRequestKey+" ,requestKind="+requestKind
					+ " ,imageType="+type);
			testPartyGuiRequest(partyRequestKey, requestKind, type);
		}else {
			System.out.println("No parameters defined");
		
			throw new IllegalArgumentException("Correct parameters were not received");
		}
	}	
	
	
	private void testHierarchyGuiRequest(long hierarchy, long requestKind, 
			GuiRequestImageTypeEntity imageType) {
		
		
		try {
			IGuiRequestManagement guiRequestBean = ServiceLocator.lookupService(IGuiRequestManagement.class);
			IPartyManagement partyBean = ServiceLocator.lookupService(IPartyManagement.class);		
			IContextManagement contextBean = ServiceLocator.lookupService(IContextManagement.class);
			
			GuiRequestEntity entity = guiRequestBean.findGuiRequestEntity(hierarchy);
			System.out.println("Entity : "+entity);
			
			GuiRequestRequestEntity processRequest = null;
			
			/* Find the request */
			for (GuiRequestRequestEntity request : entity.getRequestList()) {
				System.out.println("Request : kind="+request.getRequestKind() + " ,reqOid="+request.getRequestOid());
				if (request.getRequestKind()==requestKind) {
					processRequest = request;
				}
			}
			
			/* Find the correct image */
			Object obj = guiRequestBean.retrieveDTOFromGuiRequest(entity,
					processRequest, imageType);
			System.out.println(obj);
			MaintainHierarchyPage page = new MaintainHierarchyPage(obj);

			/* Set the context */
			try {
			ResultPartyDTO partyDto = partyBean.findPartyWithObjectOid(entity.getPartyOid());
			SRSAuthWebSession.get().setContextDTO(contextBean.getContext(partyDto));
			} catch (Exception e) {
				logger.warn("Unable to set context with oid="+entity.getPartyOid(),e);
			}
			
			this.setResponsePage(page);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
	}
	
	private void testPartyGuiRequest(long partyRequestKey, long requestKind, 
			GuiRequestImageTypeEntity imageType) {
		

		try {
			IGuiRequestManagement guiRequestBean = ServiceLocator.lookupService(IGuiRequestManagement.class);
			IPartyManagement partyBean = ServiceLocator.lookupService(IPartyManagement.class);		
			IContextManagement contextBean = ServiceLocator.lookupService(IContextManagement.class);
			
			
			GuiRequestEntity entity = guiRequestBean.findGuiRequestEntity(partyRequestKey);
			System.out.println("Entity : "+entity);
			
			GuiRequestRequestEntity processRequest = null;
			
			/* Find the request */
			for (GuiRequestRequestEntity request : entity.getRequestList()) {
				System.out.println("Request : kind="+request.getRequestKind() + " ,reqOid="+request.getRequestOid());
				if (request.getRequestKind()==requestKind) {
					processRequest = request;
				}
			}
			
			/* Find the correct image */
			Object obj = guiRequestBean.retrieveDTOFromGuiRequest(entity,
					processRequest, imageType);
			System.out.println(obj);
			MaintainPartyPage page = new MaintainPartyPage(obj);

			/* Set the context */
			try {
			ResultPartyDTO partyDto = partyBean.findPartyWithObjectOid(entity.getPartyOid());
			SRSAuthWebSession.get().setContextDTO(contextBean.getContext(partyDto));
			} catch (Exception e) {
				logger.warn("Unable to set context with oid="+entity.getPartyOid(),e);
			}
			
			this.setResponsePage(page);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
	}
	
	@Override
	protected boolean isCheckAuthentication() {
		/* Disable authentication as we are logging on */
		return true;
	}
	
	@Override
	public String getPageName() {
		return "GuiRequest Test";
	}
	
	@Override
	protected Panel getContextPanel() {
		/* Does not require a panel */
		return new EmptyPanel(CONTEXT_PANEL_NAME);
	}
	
}
